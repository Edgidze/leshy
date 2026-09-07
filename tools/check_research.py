#!/usr/bin/env python3
"""Проверка файлов `research_<CC>.json`, которые возвращают браузерные сессии.

Схема и смысл полей — `docs/research/post-soviet/README.md`; правила, по которым
сессия отбирала виды — `docs/research/post-soviet/prompt_<CC>.md`. Здесь
проверяется только то, что проверяется механически: ошибку отбора («взяли вид,
который тут не собирают») никакой скрипт не поймает, а вот выдуманный ключ,
взятую целиком пару `X`/`X__2` или название для вида не из подборки — поймает.

Только стандартная библиотека: запускается системным python3, без venv, в
отличие от `tools/build_catalog.py`.

Использование:
    python3 tools/check_research.py                       # все файлы текущей партии
    python3 tools/check_research.py --batch post-soviet   # прошлая партия
    python3 tools/check_research.py path/to/research_DK.json ...

Один файл проверяется сам по себе. Если файлов несколько, добавляется сводка
по тем вещам, которые в одном файле не видны: конфликты русских названий между
странами и слияние `gaps` по латыни. Код возврата — 1, если есть ошибки;
предупреждения на код возврата не влияют.
"""

import argparse
import json
import sys
from collections import defaultdict
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
CATALOG_JSON = (
    REPO_ROOT / "shared" / "src" / "commonMain" / "composeResources" / "files" / "catalog"
    / "catalog.json"
)
NAMES_DIR = CATALOG_JSON.parent / "names"

# Партии исследования. Каждая — своя директория результатов и своя таблица
# ожиданий по странам: языки подборки (титульный первым), коридор размера
# `(min, max, ориентир)` и коридор числа опасных видов.
#
# Коридоры вынесены в таблицу, а не заданы константами на всех, потому что
# партия `europe-15` этого потребовала: островная (`IS`) и безлесная (`IE`,
# `CY`) микобиота честно даёт меньше пятидесяти позиций, и общий нижний порог 40
# заставил бы сессию добить список видами, которых там не собирают, — ровно то,
# что промпт запрещает.
DEFAULT_SIZE = (40, 55, 50)
DEFAULT_DANGEROUS = (8, 19)

BATCHES = {
    # `.claude/plans/post-soviet-countries.md` — 33 → 40 подборок, сделано.
    "post-soviet": {
        "results": REPO_ROOT / "docs" / "research" / "post-soviet" / "results",
        "countries": {
            "KZ": {"languages": ["kk", "ru"]},
            "UZ": {"languages": ["uz", "ru"]},
            "KG": {"languages": ["ky", "ru"]},
            "TJ": {"languages": ["tg", "ru"]},
            "TM": {"languages": ["tk"]},
            "AZ": {"languages": ["az"]},
            "AM": {"languages": ["hy"]},
        },
    },
    # `.claude/plans/europe-15-countries.md` — 40 → 55 подборок.
    "europe-15": {
        "results": REPO_ROOT / "docs" / "research" / "europe-15" / "results",
        "countries": {
            "AL": {"languages": ["sq"]},
            "BA": {"languages": ["bs", "hr", "sr"]},
            "BE": {"languages": ["nl", "fr", "de"]},
            "CH": {"languages": ["de", "fr", "it"]},
            "CY": {"languages": ["el", "tr"], "size": (35, 50, 45), "dangerous": (8, 16)},
            "DK": {"languages": ["da"]},
            "GR": {"languages": ["el"]},
            "IE": {"languages": ["en"], "size": (35, 55, 45), "dangerous": (8, 18)},
            "IS": {"languages": ["is"], "size": (25, 45, 35), "dangerous": (4, 12)},
            "LU": {"languages": ["fr", "de"]},
            "ME": {"languages": ["sr"]},
            "MK": {"languages": ["mk", "sq"]},
            "NL": {"languages": ["nl"]},
            "NO": {"languages": ["nb"]},
            "PT": {"languages": ["pt"]},
        },
    },
}

GAP_PRIORITIES = {"high", "medium", "low"}

# Партия, которую проверяют без явного `--batch`: текущая.
DEFAULT_BATCH = "europe-15"


def batch_of(country: str) -> "str | None":
    """Имя партии, в которую входит код страны, или None."""
    for name, batch in BATCHES.items():
        if country in batch["countries"]:
            return name
    return None


def spec_of(country: str) -> dict:
    spec = BATCHES[batch_of(country)]["countries"][country]
    return {
        "languages": spec["languages"],
        "size": spec.get("size", DEFAULT_SIZE),
        "dangerous": spec.get("dangerous", DEFAULT_DANGEROUS),
    }


def load_existing_names(codes) -> dict:
    """Уже лежащие в приложении названия по языкам, для проверки на перезапись."""
    existing = {}
    for code in codes:
        path = NAMES_DIR / f"{code}.json"
        existing[code] = json.loads(path.read_text(encoding="utf-8")) if path.exists() else {}
    return existing

# Подборки стран, чей ареал заведомо не пересекается с семью нашими. Вид,
# который сейчас входит ТОЛЬКО в них, почти наверняка взят по ошибке — это тот
# самый случай, о котором предупреждает промпт («проверь ареал по GBIF»).
FARAWAY_ONLY = {"AU", "NZ", "MX", "JP", "KR", "US", "CA"}

# Один и тот же гриб под разными принятыми родовыми именами. Промпт
# предупреждает только про пары `X`/`X__2` (общая латынь), но каталог содержит и
# такие: `Lepista nuda` = `Collybia nuda` = `Clitocybe nuda` — три ключа, один
# вид. Взять из группы два — та же ошибка, что взять оба ключа пары, только её
# не видно по строке `sci`.
#
# Список выверен вручную, а не выведён эвристикой по видовому эпитету: эпитет
# совпадает и у совершенно разных грибов (`Morchella esculenta` — сморчок,
# `Gyromitra esculenta` — строчок; они вместе почти в каждой из 33 подборок и
# ошибкой не являются). Проверено: ни одна существующая подборка не содержит
# двух ключей из одной группы ниже.
SYNONYM_GROUPS = [
    {"lepista_nuda", "collybia_nuda", "clitocybe_nuda", "collybia_nuda__2"},
    {"lepista_personata", "collybia_personata"},
    {"leccinellum_pseudoscabrum", "leccinum_pseudoscabrum"},
    {"lactifluus_volemus", "lactarius_volemus"},
    {"inosperma_erubescens", "inocybe_erubescens"},
    {"clitocybe_gibba", "infundibulicybe_gibba"},
    {"lampteromyces_japonicus", "omphalotus_japonicus"},
    {"cerioporus_squamosus", "polyporus_squamosus"},
    {"clathrus_archeri", "anthurus_archeri"},
]


class Report:
    def __init__(self, label: str):
        self.label = label
        self.errors: list[str] = []
        self.warnings: list[str] = []
        self.facts: list[str] = []

    def error(self, message: str) -> None:
        self.errors.append(message)

    def warn(self, message: str) -> None:
        self.warnings.append(message)

    def fact(self, message: str) -> None:
        self.facts.append(message)

    def print(self) -> None:
        status = "FAIL" if self.errors else ("WARN" if self.warnings else "OK")
        print(f"\n=== {self.label}: {status} ===")
        for line in self.facts:
            print(f"  · {line}")
        for line in self.warnings:
            print(f"  ! {line}")
        for line in self.errors:
            print(f"  ✗ {line}")


def load_catalog() -> dict:
    entries = json.loads(CATALOG_JSON.read_text(encoding="utf-8"))
    return {e["key"]: e for e in entries}


def sibling_key(key: str) -> str:
    """Второй ключ пары с общей латынью: `agaricus_campestris` <-> `..._campestris__2`."""
    return key[: -len("__2")] if key.endswith("__2") else f"{key}__2"


def check_file(path: Path, catalog: dict, already_in: dict) -> tuple[Report, dict]:
    report = Report(path.name)
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except json.JSONDecodeError as exc:
        report.error(f"невалидный JSON: {exc}")
        return report, {}

    # ---- обязательные поля --------------------------------------------------
    for field in ("country", "languages", "keys", "names"):
        if field not in data:
            report.error(f"нет обязательного поля `{field}`")
    if report.errors:
        return report, {}

    country = data["country"]
    expected_country = path.stem.replace("research_", "")
    if country != expected_country:
        report.error(f"`country` = {country!r}, а файл называется research_{expected_country}.json")
    batch = batch_of(country)
    if batch is None:
        known = sorted(c for b in BATCHES.values() for c in b["countries"])
        report.error(f"{country} не входит ни в одну партию исследования: {known}")
        return report, {}
    spec = spec_of(country)
    report.label = f"{path.name} ({batch})"

    # ---- языки --------------------------------------------------------------
    languages = data["languages"]
    expected = spec["languages"]
    if not languages:
        report.error("`languages` пуст")
    elif languages != expected:
        # Не ошибка: состав `langs` — решение владельца проекта, и исследование
        # вправе его оспорить. Но увидеть расхождение надо.
        report.warn(f"`languages` = {languages}, план ожидал {expected} — сверить с §7 плана")

    # ---- keys ---------------------------------------------------------------
    keys = data["keys"]
    seen, duplicates = set(), []
    for key in keys:
        if key in seen:
            duplicates.append(key)
        seen.add(key)
    if duplicates:
        report.error(f"повторяющиеся ключи: {sorted(set(duplicates))}")

    unknown = [k for k in keys if k not in catalog]
    if unknown:
        report.error(f"ключей нет в каталоге: {unknown}")

    both_of_pair = sorted({k for k in keys if sibling_key(k) in seen})
    if both_of_pair:
        report.error(f"взяты оба ключа пары с общей латынью: {both_of_pair}")

    for group in SYNONYM_GROUPS:
        taken = sorted(group & seen)
        if len(taken) > 1:
            report.error(f"взято несколько ключей одного вида под разными родовыми именами: "
                         f"{taken} — оставить один")

    low, high, typical = spec["size"]
    if not low <= len(keys) <= high:
        report.error(f"{len(keys)} позиций — вне коридора {low}–{high}")
    elif len(keys) < typical:
        report.warn(f"{len(keys)} позиций (ориентир партии — {typical}); "
                    f"это допустимо, но должно быть объяснено в ответе сессии")
    else:
        report.fact(f"{len(keys)} позиций")

    known_keys = [k for k in keys if k in catalog]
    dangerous = [k for k in known_keys if catalog[k]["dangerous"]]
    low_d, high_d = spec["dangerous"]
    share = f"{len(dangerous)} опасных ({100 * len(dangerous) // max(1, len(known_keys))}%)"
    if not low_d <= len(dangerous) <= high_d:
        report.error(f"{share} — вне коридора {low_d}–{high_d}")
    else:
        report.fact(share)

    faraway = [
        k for k in known_keys
        if already_in.get(k) and set(already_in[k]) <= FARAWAY_ONLY
    ]
    if faraway:
        report.warn("виды, входящие только в подборки далёких стран — проверить ареал: "
                    + ", ".join(f"{k} ({','.join(already_in[k])})" for k in faraway))

    # ---- названия -----------------------------------------------------------
    names = data["names"]
    key_set = set(known_keys)
    stray_languages = sorted(set(names) - set(languages))
    if stray_languages:
        report.error(f"`names` содержит языки, которых нет в `languages`: {stray_languages}")

    for lang, table in names.items():
        outside = sorted(set(table) - key_set)
        if outside:
            report.error(f"names.{lang}: названия для ключей вне `keys`: {outside}")
        blank = sorted(k for k, v in table.items() if not str(v).strip())
        if blank:
            report.error(f"names.{lang}: пустые названия: {blank}")
        covered = len(set(table) & key_set)
        report.fact(f"names.{lang}: {covered} из {len(key_set)} "
                    f"({100 * covered // max(1, len(key_set))}%)")

    # Файл `names/<lang>.json` в приложении один на все подборки, и `extra_names`
    # применяется последним, то есть перезаписывает. Предложенное имя для ключа,
    # у которого имя уже есть, — это правка чужой подборки (`DE`, `FR`, `GB`,
    # `RS`, ...), а не дозаполнение; в прошлой партии такие 51 расхождение
    # разбирались руками и почти все были отвергнуты.
    existing = load_existing_names(names)
    for lang in sorted(names):
        clashes, case_only = {}, []
        for key, name in sorted(names[lang].items()):
            was = existing[lang].get(key)
            if not was or was == name:
                continue
            if was.casefold() == name.casefold():
                case_only.append(key)
            else:
                clashes[key] = (was, name)
        if clashes:
            shown = list(clashes.items())[:12]
            rendered = "; ".join(f"{k}: было {a!r}, предложено {b!r}" for k, (a, b) in shown)
            tail = f" (и ещё {len(clashes) - len(shown)})" if len(clashes) > len(shown) else ""
            report.warn(f"names.{lang}: {len(clashes)} названий уже есть в приложении и "
                        f"расходятся по существу — {rendered}{tail}")
        if case_only:
            report.warn(f"names.{lang}: ещё {len(case_only)} расходятся только регистром — "
                        f"брать существующее написание")

    # Два вида под одним именем — две одинаковые плитки на экране записи. Именно
    # это случилось в прошлой партии с «Маслятами» (`suillus_luteus` и
    # `suillus_granulatus` в подборках AM и AZ) и чинилось вручную уже после
    # раскладки. Здесь ловится до неё.
    for lang in sorted(names):
        seen = {}
        for key, name in sorted(names[lang].items()):
            seen.setdefault(str(name).strip().casefold(), []).append(key)
        dupes = {n: ks for n, ks in seen.items() if len(ks) > 1}
        if dupes:
            rendered = "; ".join(f"{n!r} → {', '.join(ks)}" for n, ks in sorted(dupes.items()))
            report.error(f"names.{lang}: одно имя у нескольких видов — на «Записи» это "
                         f"одинаковые плитки: {rendered}")

    titular = languages[0] if languages else None
    if titular and titular not in names:
        report.warn(f"нет ни одного названия на титульном языке `{titular}`")

    coined = data.get("coined", [])
    named_keys = {k for table in names.values() for k in table}
    stray_coined = sorted(set(coined) - named_keys)
    if stray_coined:
        report.error(f"`coined` называет ключи, которых нет ни в одном `names`: {stray_coined}")
    if coined:
        report.fact(f"{len(coined)} сконструированных названий (в `coined`)")

    # ---- notes --------------------------------------------------------------
    notes = data.get("notes", {})
    stray_notes = sorted(set(notes) - key_set)
    if stray_notes:
        report.error(f"`notes` для ключей вне `keys`: {stray_notes}")
    missing_notes = len(key_set - set(notes))
    if missing_notes:
        report.warn(f"{missing_notes} позиций без `notes` — обоснование состава неполное")

    # ---- gaps ---------------------------------------------------------------
    gaps = data.get("gaps", [])
    for i, gap in enumerate(gaps):
        where = f"gaps[{i}]"
        if not gap.get("sci"):
            report.error(f"{where}: пустой `sci`")
        priority = gap.get("priority")
        if priority not in GAP_PRIORITIES:
            report.error(f"{where}: `priority` = {priority!r}, ожидалось одно из {sorted(GAP_PRIORITIES)}")
        nearest = gap.get("nearest_key")
        if nearest is not None and nearest not in catalog:
            report.error(f"{where}: `nearest_key` = {nearest!r} — такого ключа в каталоге нет")
        also_in = set(gap.get("also_in", []))
        batch_countries = set(BATCHES[batch]["countries"])
        outside = sorted((also_in - batch_countries) | (also_in & {country}))
        if outside:
            report.error(f"{where}: `also_in` содержит {outside} — ожидались коды "
                         f"стран партии {batch}, кроме самой {country}")
        if not gap.get("sources"):
            report.warn(f"{where} ({gap.get('sci', '?')}): нет `sources`")
        if gap.get("same_concept_as_nearest") and priority == "high":
            report.warn(f"{where} ({gap.get('sci', '?')}): priority=high при "
                        f"same_concept_as_nearest=true — если грибник его не отличает, "
                        f"отдельная категория не нужна, хватит названия")
    report.fact(f"{len(gaps)} позиций в `gaps`")

    if not data.get("sources"):
        report.warn("пустой общий `sources`")

    return report, data


def cross_check(files: dict, expected_countries: set) -> Report:
    """То, что не видно внутри одного файла: конфликты названий и слияние `gaps`."""
    report = Report("сводка по всем файлам")

    # Название вида на данном языке в приложении одно, а один и тот же язык
    # просят несколько сессий: `ru` — все семь стран прошлой партии, `el` — GR и
    # CY, `sq` — AL и MK, `nl` — NL и BE, `fr` — BE, CH и LU. Проверяется любой
    # язык, встретившийся больше чем в одном файле.
    by_language_key = defaultdict(lambda: defaultdict(dict))
    for country, data in files.items():
        for lang, table in data.get("names", {}).items():
            for key, name in table.items():
                by_language_key[lang][key][country] = name

    conflicts = 0
    for lang in sorted(by_language_key):
        for key in sorted(by_language_key[lang]):
            variants = by_language_key[lang][key]
            if len(set(variants.values())) > 1:
                conflicts += 1
                rendered = "; ".join(f"{cc}: {name!r}" for cc, name in sorted(variants.items()))
                report.warn(f"names.{lang}[{key}] — разные варианты: {rendered}")
    if conflicts:
        report.fact(f"{conflicts} названий требуют выбора одного варианта перед раскладкой "
                    f"в extra_names/")
    else:
        report.fact("конфликтов названий между странами нет")

    # Слияние `gaps` по латыни: `also_in` заявлен сессией, здесь считается
    # фактическое число стран, назвавших этот вид.
    by_sci = defaultdict(dict)
    for country, data in files.items():
        for gap in data.get("gaps", []):
            if gap.get("sci"):
                by_sci[gap["sci"]][country] = gap

    if by_sci:
        report.fact(f"{len(by_sci)} различных видов в `gaps` по всем файлам:")
        ranked = sorted(by_sci.items(), key=lambda kv: -len(kv[1]))
        for sci, per_country in ranked:
            countries = ",".join(sorted(per_country))
            priorities = {g.get("priority") for g in per_country.values()}
            distinct = {g.get("same_concept_as_nearest") for g in per_country.values()}
            marks = []
            if len(priorities) > 1:
                marks.append(f"приоритеты расходятся: {sorted(p for p in priorities if p)}")
            if len(distinct) > 1:
                marks.append("страны расходятся в том, отдельный ли это концепт")
            suffix = f" — {'; '.join(marks)}" if marks else ""
            report.fact(f"    {sci}: {len(per_country)} стран ({countries}), "
                        f"{'/'.join(sorted(str(p) for p in priorities))}{suffix}")
    else:
        report.fact("`gaps` пуст во всех файлах — каталог покрывает все страны партии")

    missing = sorted(expected_countries - set(files))
    if missing:
        report.fact(f"ещё не пришли: {', '.join(missing)}")

    return report


def main() -> int:
    if not CATALOG_JSON.exists():
        print(f"нет {CATALOG_JSON}", file=sys.stderr)
        return 2
    catalog = load_catalog()

    countries_json = CATALOG_JSON.parent / "countries.json"
    already_in = defaultdict(list)
    for entry in json.loads(countries_json.read_text(encoding="utf-8")):
        for key in entry["keys"]:
            already_in[key].append(entry["code"])

    parser = argparse.ArgumentParser(description="Проверка файлов research_<CC>.json")
    parser.add_argument("paths", nargs="*", type=Path,
                        help="конкретные файлы; без них — вся директория результатов партии")
    parser.add_argument("--batch", choices=sorted(BATCHES), default=DEFAULT_BATCH,
                        help=f"партия исследования (по умолчанию {DEFAULT_BATCH})")
    args = parser.parse_args()

    if args.paths:
        paths = args.paths
    else:
        results_dir = BATCHES[args.batch]["results"]
        paths = sorted(results_dir.glob("research_*.json"))
        if not paths:
            print(f"в {results_dir} нет файлов research_*.json — нечего проверять")
            return 0

    failed = False
    parsed = {}
    for path in paths:
        if not path.exists():
            print(f"нет файла {path}", file=sys.stderr)
            failed = True
            continue
        report, data = check_file(path, catalog, already_in)
        report.print()
        failed |= bool(report.errors)
        if data.get("country"):
            parsed[data["country"]] = data

    if len(parsed) > 1:
        batches = {batch_of(cc) for cc in parsed if batch_of(cc)}
        expected = {cc for b in batches for cc in BATCHES[b]["countries"]}
        cross_check(parsed, expected).print()

    print()
    print("ЕСТЬ ОШИБКИ — файл(ы) вернуть в сессию" if failed else "Ошибок нет")
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
