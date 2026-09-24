#!/usr/bin/env python3
"""Снимает отпечаток корпуса данных каталога и считает его SHA-256.

Зачем. Единственный актив приложения, который копируется мгновенно и стоил месяцев
работы, — данные каталога: отбор видов, состав и порядок страновых подборок, выбор
названий из числа синонимов на 52 языках. Факты (латынь, микофлора страны) — общественное
достояние, но выбор между равноправными вариантами фактом не является: цвет плитки
`#b59971` ничем не продиктован, порядок внутри подборки редакторский, «Мухомор красный»
против лежащего рядом в `aliases` «Красный мухомор» — наше решение. Совпадение таких
значений один-в-один случайностью не объясняется.

Чего у этого отпечатка нет само по себе — даты. Отсюда весь смысл файла: снимок делается
один раз, его SHA-256 публикуется (`site/fingerprints.txt` → GitHub Pages), сам снимок
остаётся здесь. Позже, если понадобится, снимок предъявляется, любой считает от него хеш
и видит совпадение с публично датированной строкой.

Подсаженных маркеров («канареек») тут нет намеренно — см. `docs/release/data-fingerprint.md`,
раздел «Почему не канарейки»: при 8523 естественных элементах выбора они не добавляли бы
ничего, зато требовали бы держать в данных значения, которые нельзя чинить.

Сравнение — долевое, а не побайтовое: `--check` печатает, какая часть каждого слоя совпала
со снимком. Копировальщик, поменявший двадцать названий, остаётся с нашими 360 цветами из
366 — и именно эта доля, а не точное равенство, что-то доказывает.

Только стандартная библиотека, сети не требует.

Usage:
    python3 tools/fingerprint.py --out docs/release/fingerprints/catalog-YYYY-MM-DD.json
    python3 tools/fingerprint.py --check docs/release/fingerprints/catalog-YYYY-MM-DD.json
    python3 tools/fingerprint.py --check <снимок> --strict   # код возврата 1 при расхождении
"""
import argparse
import datetime
import hashlib
import json
import subprocess
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
CATALOG = REPO / "shared" / "src" / "commonMain" / "composeResources" / "files" / "catalog"
DRAWABLE = REPO / "shared" / "src" / "commonMain" / "composeResources" / "drawable"

# Поля вида, у которых нет «правильного» значения: цвет выбран рисовальщиком, importance и
# breadth субъективны по определению, sortLast — редакторское решение о месте в ленте.
SPECIES_FIELDS = ("sci", "image", "color", "breadth", "importance", "sortLast")


def canonical(document):
    """Один и тот же документ — всегда одни и те же байты, иначе хеш ничего не значит."""
    return json.dumps(document, ensure_ascii=False, sort_keys=True, indent=2) + "\n"


def load(path):
    return json.loads(path.read_text(encoding="utf-8"))


def collect():
    species = {}
    for entry in load(CATALOG / "catalog.json"):
        species[entry["key"]] = {f: entry.get(f) for f in SPECIES_FIELDS}

    countries = {}
    for entry in load(CATALOG / "countries.json"):
        # Порядок внутри keys сохраняется как есть: он и есть отпечаток, сортировать нельзя.
        countries[entry["code"]] = {"keys": entry["keys"], "common": entry.get("common")}

    names = {p.stem: load(p) for p in sorted((CATALOG / "names").glob("*.json"))}
    aliases = {p.stem: load(p) for p in sorted((CATALOG / "aliases").glob("*.json"))}

    images = {}
    for path in sorted(DRAWABLE.iterdir()):
        if path.is_file() and not path.name.startswith("."):
            images[path.name] = hashlib.sha256(path.read_bytes()).hexdigest()

    return {"species": species, "countries": countries, "names": names,
            "aliases": aliases, "images": images}


def counts(layers):
    return {
        "species": len(layers["species"]),
        "countries": len(layers["countries"]),
        "country_positions": sum(len(c["keys"]) for c in layers["countries"].values()),
        "name_languages": len(layers["names"]),
        "names": sum(len(v) for v in layers["names"].values()),
        "alias_languages": len(layers["aliases"]),
        "aliases": sum(len(v) for v in layers["aliases"].values()),
        "alias_variants": sum(len(variants)
                              for entries in layers["aliases"].values()
                              for variants in entries.values()),
        "images": len(layers["images"]),
    }


def head_commit():
    try:
        out = subprocess.run(["git", "-C", str(REPO), "rev-parse", "HEAD"],
                             capture_output=True, text=True, check=True)
        return out.stdout.strip()
    except (subprocess.CalledProcessError, FileNotFoundError):
        return None


def build(out_path):
    layers = collect()
    document = {
        "schema": "leshy.catalog.fingerprint/1",
        "date": datetime.date.today().isoformat(),
        "commit": head_commit(),
        "counts": counts(layers),
        "layers": layers,
    }
    text = canonical(document)
    out_path.parent.mkdir(parents=True, exist_ok=True)
    out_path.write_text(text, encoding="utf-8")
    digest = hashlib.sha256(text.encode("utf-8")).hexdigest()

    print(f"снимок:  {out_path.relative_to(REPO)}")
    print(f"sha256:  {digest}")
    print(f"размер:  {len(text.encode('utf-8')) // 1024} КБ")
    print()
    for name, value in document["counts"].items():
        print(f"  {name:20} {value}")
    print()
    print("Хеш считается от файла целиком, то есть проверяется и без этого скрипта:")
    print(f"  shasum -a 256 {out_path.relative_to(REPO)}")
    return digest


def compare_layer(old, new, describe):
    """Доля совпавших значений по ключам, присутствующим в снимке."""
    same = sum(1 for key, value in old.items() if key in new and new[key] == value)
    missing = sum(1 for key in old if key not in new)
    added = sum(1 for key in new if key not in old)
    share = 100.0 * same / len(old) if old else 100.0
    print(f"  {describe:22} совпало {same}/{len(old)} ({share:.1f}%)"
          f"{f', пропало {missing}' if missing else ''}"
          f"{f', добавилось {added}' if added else ''}")
    return same == len(old) and not missing and not added


def check(snapshot_path, strict):
    snapshot = load(snapshot_path)
    old, new = snapshot["layers"], collect()
    print(f"снимок от {snapshot['date']}, коммит {snapshot.get('commit') or '—'}")
    print()
    intact = True
    intact &= compare_layer(old["species"], new["species"], "виды")
    intact &= compare_layer(old["countries"], new["countries"], "подборки стран")
    intact &= compare_layer(old["images"], new["images"], "картинки")
    for layer in ("names", "aliases"):
        flat_old = {f"{lang}/{key}": value
                    for lang, entries in old[layer].items() for key, value in entries.items()}
        flat_new = {f"{lang}/{key}": value
                    for lang, entries in new[layer].items() for key, value in entries.items()}
        intact &= compare_layer(flat_old, flat_new, "имена" if layer == "names" else "синонимы")
    print()
    if intact:
        print("Данные совпадают со снимком полностью.")
    else:
        print("Данные разошлись со снимком. Это НЕ ошибка сама по себе: корпус растёт, и старый")
        print("снимок продолжает доказывать состояние на свою дату. Новый снимок нужен, только")
        print("если хочется покрыть прирост, — см. docs/release/data-fingerprint.md.")
    return 0 if intact or not strict else 1


def main():
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--out", type=Path, help="куда записать новый снимок")
    parser.add_argument("--check", type=Path, help="сверить текущие данные со снимком")
    parser.add_argument("--strict", action="store_true",
                        help="с --check: код возврата 1 при любом расхождении")
    args = parser.parse_args()

    if args.check:
        return check(args.check, args.strict)
    out = args.out or (REPO / "docs" / "release" / "fingerprints" /
                       f"catalog-{datetime.date.today().isoformat()}.json")
    build(out)
    return 0


if __name__ == "__main__":
    sys.exit(main())
