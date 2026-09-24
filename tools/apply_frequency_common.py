#!/usr/bin/env python3
"""Переносит `common` из `docs/research/frequency/` в `docs/catalog/extra_country_presets.json`.

Частотность 22 подборок партий post-soviet и europe-15 выведена из `notes` их собственных
исследований (`docs/research/*/results/research_<CC>.json`) — см. `docs/research/frequency/README.md`.
Этот скрипт — единственное место, где решение попадает в данные приложения: он проверяет, что
каждый ключ принадлежит подборке своей страны, и кладёт список в пресет. Дальше
`tools/build_catalog.py` (`load_extra_presets`) превращает его в роль `common_encounter`, и вид
поднимается в ленте «Записи» по правилу `sortCategories`.

Только стандартная библиотека — запускается системным python3, как `check_research.py`.
"""
import json
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
FREQ_DIR = REPO / "docs" / "research" / "frequency"
PRESETS = REPO / "docs" / "catalog" / "extra_country_presets.json"
DUMP = REPO / "docs" / "catalog" / "leshy_core_app.json"
# Переопределение частотности для 33 подборок исходного дампа. Отдельным файлом, а не правкой
# самого дампа: дамп — входной артефакт, его не редактируют (см. `tools/build_catalog.py`).
OVERRIDES = REPO / "docs" / "catalog" / "common_overrides.json"
# Замены позиций в подборках дампа — их надо учесть, иначе частотность сверяется с составом
# до правки (`tools/build_catalog.py`, `apply_preset_patches`).
PATCHES = REPO / "docs" / "catalog" / "preset_patches.json"

# Доля подборки, которую разумно считать частотной. Решение владельца 2026-09-24: «корзина
# обычного выхода», 10–20 позиций из полусотни. Коридор задан долей, а не числом, потому что
# подборки разного размера (CY — 39 позиций, IS — 34, GR — 53).
# Нижняя граница низкая намеренно: честный короткий список — нормальный результат. У Австралии
# исследовательская сессия оставила шесть позиций из пятидесяти, и это не брак, а вывод: съедобная
# корзина страны завязана на посадки интродуцированной сосны и коротка.
MIN_SHARE, MAX_SHARE = 0.10, 0.45
# Жёсткий потолок, решение владельца 2026-09-24: больше двадцати частотных — это уже не
# «корзина обычного выхода», а половина подборки, и сортировка перестаёт что-либо значить.
MAX_COMMON = 20


def print_review() -> int:
    """Свод «ключ + заметка исследования» по всем отобранным позициям.

    Печатается, а не коммитится: текст заметок уже лежит в `research_<CC>.json`, и держать его
    вторую копию в репозитории значит обзавестись расхождением. Это то, по чему решение
    проверяется глазами: у каждой частотной позиции видно, на каком основании она частотная.
    """
    for path in sorted(FREQ_DIR.glob("common_*.json")):
        data = json.loads(path.read_text(encoding="utf-8"))
        cc = data["country"]
        research = next(
            (p for p in (REPO / "docs" / "research").glob(f"*/results/research_{cc}.json")), None
        )
        notes = json.loads(research.read_text(encoding="utf-8")).get("notes", {}) if research else {}
        print(f"\n##### {cc} — {len(data['common'])} частотных ({data['method']})")
        for key in data["common"]:
            print(f"  {key}\n    {notes.get(key, '— заметки нет')}")
    return 0


def main() -> int:
    if "--review" in sys.argv:
        return print_review()
    catalog = {
        c["key"]: c for c in json.loads(
            (REPO / "shared" / "src" / "commonMain" / "composeResources" / "files" / "catalog"
             / "catalog.json").read_text(encoding="utf-8"))
    }
    presets = json.loads(PRESETS.read_text(encoding="utf-8"))
    dump = json.loads(DUMP.read_text(encoding="utf-8"))
    key_by_id = {c["id"]: c["key"] for c in dump["categories"]}
    dump_keys = {
        cc: [key_by_id[i["id"]] for i in sorted(preset["items"], key=lambda i: i["order"])]
        for cc, preset in dump["country_presets"].items()
    }
    patches = json.loads(PATCHES.read_text(encoding="utf-8")) if PATCHES.exists() else {}
    for cc, patch in patches.items():
        swap = {r["from"]: r["to"] for r in patch.get("replace", [])}
        dump_keys[cc] = [swap.get(k, k) for k in dump_keys.get(cc, [])]
    overrides = {}
    errors, warnings = [], []
    applied = 0

    for path in sorted(FREQ_DIR.glob("common_*.json")):
        data = json.loads(path.read_text(encoding="utf-8"))
        cc = data["country"]
        from_dump = cc not in presets
        if from_dump and cc not in dump_keys:
            errors.append(f"{path.name}: страны {cc} нет ни в {PRESETS.name}, ни в дампе")
            continue
        keys = dump_keys[cc] if from_dump else presets[cc]["keys"]
        common = data["common"]
        if len(common) > MAX_COMMON:
            errors.append(f"{cc}: {len(common)} частотных, потолок — {MAX_COMMON}")

        unknown = [k for k in common if k not in keys]
        if unknown:
            errors.append(f"{cc}: ключи вне подборки страны: {unknown}")
        duplicates = sorted({k for k in common if common.count(k) > 1})
        if duplicates:
            errors.append(f"{cc}: повторы: {duplicates}")

        # У результата исследовательской сессии обоснование обязано быть на каждой позиции —
        # иначе список нечем проверить, а именно ради проверяемости сессию и заказывали.
        if data.get("method") == "research-session":
            evidence = data.get("evidence") or {}
            missing = [k for k in common if not evidence.get(k)]
            if missing:
                warnings.append(f"{cc}: позиции без обоснования в `evidence`: {missing}")

        # `sortLast` — флаг «по умолчанию ниже», а не «не собирают»: волнушка, чёрный груздь,
        # строчок и зеленушка помечены им и при этом массово собираются. Поэтому предупреждение, а
        # не ошибка: глазами проверяется, что вид действительно берут, — частотность по решению
        # владельца 2026-09-24 означает «частотное И потенциально собираемое».
        flagged = [k for k in common if catalog.get(k, {}).get("sortLast")]
        if flagged:
            warnings.append(f"{cc}: частотные с флагом «в конец» — проверить, что их собирают: {flagged}")

        share = len(common) / len(keys)
        if not MIN_SHARE <= share <= MAX_SHARE:
            warnings.append(
                f"{cc}: {len(common)} из {len(keys)} ({share:.0%}) — вне коридора "
                f"{MIN_SHARE:.0%}–{MAX_SHARE:.0%}"
            )

        if not unknown and not duplicates:
            # Порядок — как в подборке: внутри частотных лента всё равно сортируется по
            # `importance`, а стабильный порядок делает дифф генератора читаемым.
            ordered = [k for k in keys if k in set(common)]
            if from_dump:
                overrides[cc] = ordered
            else:
                presets[cc]["common"] = ordered
            applied += 1

    for w in warnings:
        print(f"  предупреждение: {w}")
    for e in errors:
        print(f"  ОШИБКА: {e}")
    if errors:
        return 1

    PRESETS.write_text(json.dumps(presets, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    OVERRIDES.write_text(
        json.dumps(dict(sorted(overrides.items())), ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    print(
        f"{PRESETS.name}: `common` у {applied - len(overrides)} подборок исследований; "
        f"{OVERRIDES.name}: переопределение у {len(overrides)} подборок дампа"
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
