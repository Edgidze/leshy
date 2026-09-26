#!/usr/bin/env python3
"""Наборы видов российской редакции — из разметки исследования в JSON, который читает приложение.

Собирает `shared/src/commonMain/composeResources/files/catalog/sets-ru.json` из двух файлов
разметки в `docs/russia-edition/`:

- `species-ru-171.csv` — колонки `in_base_54` (нынешняя подборка «Россия») и `region`;
- `species-ru-171-groups.csv` — колонки `habitat`, `season`, `base_proposed`, `set_D`.

Плюс `catalog.json` — оттуда берётся флаг `sortLast`.

## Почему скрипт, а не JSON, написанный руками

Состав наборов — это не набор строк, а **правило**, применённое к разметке. Правило меняется
(владелец решает, что в базе, что отдельным набором), разметка уточняется исследованием, и
руками сведённый файл после первой же правки перестал бы соответствовать и тому и другому.
Здесь правило записано один раз, и пересборка — один запуск.

## Правило (решения владельца 2026-09-26)

1. Строка с пометкой `review` в `set_D` (шесть спорных: североамериканские виды, роды вместо
   видов, неподтверждённые находки) → набор **«Другие виды»**. Не выбрасываются: ключ
   существует, вид в каталоге есть, и молча пропасть он не должен.
2. **База** = нынешняя подборка из 54 видов ∪ базовый набор исследования (27), **минус виды с
   `sortLast`**. Про минус ниже отдельно.
3. Остальное, по убыванию силы: `rare` → «Редкие виды»; только `south` в регионах → «Юг и
   Кавказ»; сезон ровно `spring` → «Весенние грибы»; иначе — теги `habitat` как есть (вид может
   попасть в два-три набора).

**Про `sortLast`.** Флаг каталога, означающий ровно «по умолчанию ниже в ленте», — приложение
съедобность не классифицирует и слова этого не произносит нигде (правило продукта, поле
удалено из базы отдельной миграцией). Девять видов из базы с этим флагом переезжают в наборы
по месту обитания: в базовом наборе, который человек не выбирал, они занимали бы места видов,
за которыми он в лес и пошёл. Из приложения они никуда не деваются — включаются галочкой того
леса, куда человек идёт.

## Что проверяется на выходе

- каждый из 171 ключа попал хотя бы в один набор;
- все ключи существуют в `catalog.json`;
- ни один набор не больше 30 видов, база не больше 50 (условие готовности из брифа).

Запуск:  python3 tools/build_russia_sets.py
"""
from __future__ import annotations

import csv
import json
import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DOCS = os.path.join(ROOT, "docs", "russia-edition")
REGIONS_CSV = os.path.join(DOCS, "species-ru-171.csv")
GROUPS_CSV = os.path.join(DOCS, "species-ru-171-groups.csv")
CATALOG = os.path.join(ROOT, "shared", "src", "commonMain", "composeResources", "files", "catalog", "catalog.json")
OUT = os.path.join(ROOT, "shared", "src", "commonMain", "composeResources", "files", "catalog", "sets-ru.json")

# Страна, чью подборку эти наборы заменяют. Отсюда же берётся частотность видов в ленте
# (`ObserveFrequentSpeciesKeysUseCase`): наборы — это разрезанная на части подборка России, и
# частотность у них та же самая.
COUNTRY = "RU"

# Порядок здесь — порядок галочек на экране. База первой, дальше леса от самого частого к
# редкому, потом сезонный, географический и два служебных.
SETS = [
    ("base", "Основные грибы", "Common mushrooms"),
    ("pine", "Сосновый бор", "Pine forest"),
    ("taiga", "Ельник и тайга", "Spruce forest and taiga"),
    ("smallleaf", "Березняк и осинник", "Birch and aspen woods"),
    ("broadleaf", "Дубрава и широколиственный лес", "Oak and broadleaf forest"),
    ("open", "Луга, поля, парки", "Meadows, fields and parks"),
    ("wood", "На пнях и стволах", "On stumps and trunks"),
    ("spring", "Весенние грибы", "Spring mushrooms"),
    ("south", "Юг и Кавказ", "South and the Caucasus"),
    ("rare", "Редкие виды", "Rare species"),
    ("other", "Другие виды", "Other species"),
]

# Что отмечено на свежей установке. Ровно база: остальное человек добавляет под свою вылазку.
DEFAULT_SETS = ["base"]

MAX_SET = 30
MAX_BASE = 50


def read_csv(path: str) -> list[dict[str, str]]:
    with open(path, encoding="utf-8-sig") as f:
        return list(csv.DictReader(f, delimiter=";"))


def build() -> dict:
    regions = {r["key"]: r for r in read_csv(REGIONS_CSV)}
    groups = read_csv(GROUPS_CSV)
    catalog = {c["key"]: c for c in json.load(open(CATALOG, encoding="utf-8"))}

    missing = [r["key"] for r in groups if r["key"] not in catalog]
    if missing:
        raise SystemExit(f"ключей нет в catalog.json: {missing}")

    sorts_last = {k for k in regions if catalog[k].get("sortLast")}
    in_54 = {k for k, r in regions.items() if r["in_base_54"].strip() == "да"}
    proposed = {r["key"] for r in groups if r["base_proposed"].strip() == "да"}
    base = (in_54 | proposed) - sorts_last

    members: dict[str, list[str]] = {set_id: [] for set_id, _, _ in SETS}
    for row in groups:
        key = row["key"]
        set_d = (row["set_D"] or "").split()
        if "review" in set_d:
            members["other"].append(key)
            continue
        if key in base:
            members["base"].append(key)
            continue
        if "rare" in set_d:
            members["rare"].append(key)
            continue
        if regions[key]["region"].strip() == "south":
            members["south"].append(key)
            continue
        if (row["season"] or "").split() == ["spring"]:
            members["spring"].append(key)
            continue
        for tag in (row["habitat"] or "").split():
            if tag not in members:
                raise SystemExit(f"неизвестный тег habitat: {tag} (ключ {key})")
            members[tag].append(key)

    uncovered = {r["key"] for r in groups} - {k for ks in members.values() for k in ks}
    if uncovered:
        raise SystemExit(f"виды не попали ни в один набор: {sorted(uncovered)}")
    if len(members["base"]) > MAX_BASE:
        raise SystemExit(f"база выросла до {len(members['base'])} при потолке {MAX_BASE}")
    too_big = {s: len(k) for s, k in members.items() if s != "base" and len(k) > MAX_SET}
    if too_big:
        raise SystemExit(f"наборы выше потолка {MAX_SET}: {too_big}")

    return {
        "country": COUNTRY,
        "defaults": DEFAULT_SETS,
        "sets": [
            {"id": set_id, "name": {"ru": ru, "en": en}, "keys": members[set_id]}
            for set_id, ru, en in SETS
        ],
    }


def main() -> None:
    data = build()
    with open(OUT, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
        f.write("\n")
    total = len({k for s in data["sets"] for k in s["keys"]})
    print(f"{os.path.relpath(OUT, ROOT)}: {len(data['sets'])} наборов, {total} видов")
    for s in data["sets"]:
        mark = " (по умолчанию)" if s["id"] in data["defaults"] else ""
        print(f"  {s['name']['ru']:34s} {len(s['keys']):3d}{mark}")


if __name__ == "__main__":
    main()
