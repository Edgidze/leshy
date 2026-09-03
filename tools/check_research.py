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
    python3 tools/check_research.py                       # все файлы в results/
    python3 tools/check_research.py path/to/research_KZ.json ...

Один файл проверяется сам по себе. Если файлов несколько, добавляется сводка
по тем вещам, которые в одном файле не видны: конфликты русских названий между
странами и слияние `gaps` по латыни. Код возврата — 1, если есть ошибки;
предупреждения на код возврата не влияют.
"""

import json
import sys
from collections import defaultdict
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
CATALOG_JSON = (
    REPO_ROOT / "shared" / "src" / "commonMain" / "composeResources" / "files" / "catalog"
    / "catalog.json"
)
RESULTS_DIR = REPO_ROOT / "docs" / "research" / "post-soviet" / "results"

# Страны плана и их языки: титульный первым, затем русский там, где он в `langs`
# подборки по решению §7 плана. Сессия присылает `languages` сама — здесь это
# ожидание, расхождение с которым стоит увидеть, а не молча принять.
EXPECTED_LANGUAGES = {
    "KZ": ["kk", "ru"],
    "UZ": ["uz", "ru"],
    "KG": ["ky", "ru"],
    "TJ": ["tg", "ru"],
    "TM": ["tk"],
    "AZ": ["az"],
    "AM": ["hy"],
}

PRESET_SIZE_RANGE = (40, 55)
PRESET_SIZE_TYPICAL = 50
DANGEROUS_RANGE = (8, 19)
GAP_PRIORITIES = {"high", "medium", "low"}

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
    if country not in EXPECTED_LANGUAGES:
        report.error(f"{country} не входит в семь стран плана: {sorted(EXPECTED_LANGUAGES)}")
        return report, {}

    # ---- языки --------------------------------------------------------------
    languages = data["languages"]
    expected = EXPECTED_LANGUAGES[country]
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

    low, high = PRESET_SIZE_RANGE
    if not low <= len(keys) <= high:
        report.error(f"{len(keys)} позиций — вне коридора {low}–{high}")
    elif len(keys) < PRESET_SIZE_TYPICAL:
        report.warn(f"{len(keys)} позиций (обычный размер — {PRESET_SIZE_TYPICAL}); "
                    f"это допустимо, но должно быть объяснено в ответе сессии")
    else:
        report.fact(f"{len(keys)} позиций")

    known_keys = [k for k in keys if k in catalog]
    dangerous = [k for k in known_keys if catalog[k]["dangerous"]]
    low_d, high_d = DANGEROUS_RANGE
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
        outside = sorted((also_in - set(EXPECTED_LANGUAGES)) | (also_in & {country}))
        if outside:
            report.error(f"{where}: `also_in` содержит {outside} — ожидались коды "
                         f"из семи стран плана, кроме самой {country}")
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


def cross_check(files: dict) -> Report:
    """То, что не видно внутри одного файла: конфликты названий и слияние `gaps`."""
    report = Report("сводка по всем файлам")

    # Название вида на данном языке в приложении одно. Титульные языки у семи
    # стран не пересекаются, так что реально конфликтует только `ru` — но
    # проверяется любой язык, встретившийся больше чем в одном файле.
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
        report.fact("`gaps` пуст во всех файлах — каталог покрывает все семь стран")

    missing = sorted(set(EXPECTED_LANGUAGES) - set(files))
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

    if len(sys.argv) > 1:
        paths = [Path(a) for a in sys.argv[1:]]
    else:
        paths = sorted(RESULTS_DIR.glob("research_*.json"))
        if not paths:
            print(f"в {RESULTS_DIR} нет файлов research_*.json — нечего проверять")
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
        cross_check(parsed).print()

    print()
    print("ЕСТЬ ОШИБКИ — файл(ы) вернуть в сессию" if failed else "Ошибок нет")
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
