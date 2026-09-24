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

# Доля подборки, которую разумно считать частотной. Решение владельца 2026-09-24: «корзина
# обычного выхода», 10–20 позиций из полусотни. Коридор задан долей, а не числом, потому что
# подборки разного размера (CY — 39 позиций, IS — 34, GR — 53).
MIN_SHARE, MAX_SHARE = 0.18, 0.45


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
    presets = json.loads(PRESETS.read_text(encoding="utf-8"))
    errors, warnings = [], []
    applied = 0

    for path in sorted(FREQ_DIR.glob("common_*.json")):
        data = json.loads(path.read_text(encoding="utf-8"))
        cc = data["country"]
        if cc not in presets:
            errors.append(f"{path.name}: страны {cc} нет в {PRESETS.name}")
            continue
        keys = presets[cc]["keys"]
        common = data["common"]

        unknown = [k for k in common if k not in keys]
        if unknown:
            errors.append(f"{cc}: ключи вне подборки страны: {unknown}")
        duplicates = sorted({k for k in common if common.count(k) > 1})
        if duplicates:
            errors.append(f"{cc}: повторы: {duplicates}")

        share = len(common) / len(keys)
        if not MIN_SHARE <= share <= MAX_SHARE:
            warnings.append(
                f"{cc}: {len(common)} из {len(keys)} ({share:.0%}) — вне коридора "
                f"{MIN_SHARE:.0%}–{MAX_SHARE:.0%}"
            )

        if not unknown and not duplicates:
            # Порядок — как в подборке: внутри частотных лента всё равно сортируется по
            # `importance`, а стабильный порядок делает дифф генератора читаемым.
            presets[cc]["common"] = [k for k in keys if k in set(common)]
            applied += 1

    for w in warnings:
        print(f"  предупреждение: {w}")
    for e in errors:
        print(f"  ОШИБКА: {e}")
    if errors:
        return 1

    PRESETS.write_text(json.dumps(presets, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"{PRESETS.name}: `common` проставлен у {applied} стран")
    return 0


if __name__ == "__main__":
    sys.exit(main())
