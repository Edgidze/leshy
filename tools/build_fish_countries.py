#!/usr/bin/env python3
"""Собирает `fishing/src/commonMain/composeResources/files/catalog/fish_countries.json`.

Источник истины — таблица ниже: для каждого вида перечислены страны, где его РЕАЛЬНО ЛОВЯТ.
Это не академический чеклист присутствия: вид, занесённый в местную Красную книгу или
встречающийся единично, в подборку не идёт — подборка отвечает на вопрос «что я тут поймаю»,
а не «что тут когда-либо находили». Обоснование и спорные позиции — `docs/research/fish-countries/`.

Скрипт запускается руками при правке таблицы; результат коммитится.
"""

import json
import pathlib

# Порядок стран в файле — как здесь.
COUNTRIES = (
    "RU BY UA MD KZ LT LV EE FI SE NO DK IS PL DE CZ SK HU AT CH NL BE FR GB IE "
    "RO BG RS HR SI IT ES PT"
).split()

ALL = set(COUNTRIES)


def everywhere_except(*codes: str) -> set:
    return ALL - set(codes)


# Вид -> страны. Порядок ключей = порядок в каталоге (fish.json).
SPECIES = {
    "fish_pike": everywhere_except("IS"),
    "fish_perch": everywhere_except("IS"),
    "fish_zander": everywhere_except("IS", "NO", "IE", "PT"),
    "fish_bream": everywhere_except("IS", "ES", "PT"),
    "fish_silver_bream": set("RU BY UA MD KZ LT LV EE FI SE DK PL DE CZ SK HU AT NL BE FR GB RO BG RS HR SI".split()),
    "fish_roach": everywhere_except("IS"),
    "fish_rudd": everywhere_except("IS"),
    "fish_crucian": everywhere_except("IS", "ES", "PT"),
    "fish_carp": everywhere_except("IS"),
    "fish_tench": everywhere_except("IS"),
    "fish_catfish": set("RU BY UA MD KZ LT LV PL DE CZ SK HU AT CH NL BE FR GB SE RO BG RS HR SI IT ES PT".split()),
    "fish_burbot": set("RU BY UA KZ LT LV EE FI SE NO DK PL DE CZ SK AT CH FR NL RO".split()),
    "fish_ide": set("RU BY UA MD KZ LT LV EE FI SE NO DK PL DE CZ SK HU AT NL BE FR GB RO".split()),
    "fish_chub": set("RU BY UA MD LT LV DK PL DE CZ SK HU AT CH NL BE FR GB IE RO BG RS HR SI IT".split()),
    "fish_dace": set("RU BY UA MD LT LV EE FI SE NO DK PL DE CZ SK HU AT CH NL BE FR GB IE RO".split()),
    "fish_asp": set("RU BY UA MD KZ LT LV EE FI SE PL DE CZ SK HU AT NL BE FR RO BG RS HR SI".split()),
    "fish_bleak": set("RU BY UA MD KZ LT LV EE FI SE DK PL DE CZ SK HU AT CH NL BE FR GB RO BG RS HR SI IT ES PT".split()),
    "fish_gudgeon": set("RU BY UA MD LT LV EE FI SE DK PL DE CZ SK HU AT CH NL BE FR GB IE RO BG RS HR SI IT".split()),
    "fish_ruffe": set("RU BY UA MD KZ LT LV EE FI SE NO DK PL DE CZ SK HU AT CH NL BE FR GB RO BG RS HR SI IT".split()),
    "fish_eel": everywhere_except("KZ"),
    "fish_brown_trout": everywhere_except("KZ"),
    "fish_rainbow_trout": set(COUNTRIES),
    "fish_grayling": set("RU BY UA LT LV EE FI SE NO DK PL DE CZ SK AT CH SI FR GB RO IT".split()),
    "fish_whitefish": set("RU FI SE NO EE LV LT KZ PL DE CH AT IT FR".split()),
    "fish_vendace": set("RU FI SE NO EE LV LT PL DE".split()),
    "fish_atlantic_salmon": set("RU NO SE FI IS EE LV LT IE GB FR DK ES".split()),
}


def main() -> None:
    root = pathlib.Path(__file__).resolve().parent.parent
    catalog_path = root / "fishing/src/commonMain/composeResources/files/catalog/fish.json"
    catalog = json.loads(catalog_path.read_text())
    order = [entry["key"] for entry in catalog]

    unknown = set(SPECIES) - set(order)
    if unknown:
        raise SystemExit(f"В таблице есть виды, которых нет в каталоге: {sorted(unknown)}")
    missing = set(order) - set(SPECIES)
    if missing:
        raise SystemExit(f"У этих видов каталога нет строки в таблице: {sorted(missing)}")

    entries = []
    for code in COUNTRIES:
        keys = [key for key in order if code in SPECIES[key]]
        if not keys:
            raise SystemExit(f"Пустая подборка у {code} — так быть не должно")
        entries.append({"code": code, "keys": keys})

    out = root / "fishing/src/commonMain/composeResources/files/catalog/fish_countries.json"
    out.write_text(json.dumps(entries, ensure_ascii=False, indent=2) + "\n")
    sizes = {entry["code"]: len(entry["keys"]) for entry in entries}
    print(f"{len(entries)} подборок → {out.relative_to(root)}")
    print("размеры:", sizes)


if __name__ == "__main__":
    main()
