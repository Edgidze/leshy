#!/usr/bin/env python3
"""Сборка `catalog_reference.tsv` — закрытого списка видов каталога, который
кладётся в браузерные сессии исследования вместе с `prompt_<CC>.md`.

В прошлый заход (`.claude/plans/post-soviet-countries.md`, Фаза 1) этот файл был
собран разово и скрипта после себя не оставил, поэтому `already_in` в нём
застыл на 33 странах. Здесь та же сборка, но воспроизводимая: колонка
`already_in` — самый сильный географический сигнал для сессии, и она обязана
отражать текущий состав `countries.json`, а не тот, что был на момент прошлой
партии.

Колонки с уже имеющимися названиями (`--names`) подбираются под партию: сессии
нужны языки-соседи её стран, а не все 43 файла — датской сессии полезен
шведский столбец, македонской — болгарский и сербский, португальской —
испанский. Это контекст («понять, какой концепт имеется в виду, и увидеть, что
название уже есть»), а не источник для перевода; предупреждение об этом — в
самих промптах.

Только стандартная библиотека: запускается системным python3, без venv (в
отличие от `tools/build_catalog.py`, которому нужны Pillow и Babel).

Использование:
    python3 tools/build_catalog_reference.py docs/research/europe-15/catalog_reference.tsv \
        --names en,de,fr,es,it,sv,hr,sr,bg
"""

import argparse
import json
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
CATALOG_DIR = (
    REPO_ROOT / "shared" / "src" / "commonMain" / "composeResources" / "files" / "catalog"
)


def load_names(codes: list[str]) -> dict[str, dict[str, str]]:
    names: dict[str, dict[str, str]] = {}
    for code in codes:
        path = CATALOG_DIR / "names" / f"{code}.json"
        if not path.exists():
            raise SystemExit(f"нет файла названий: {path.relative_to(REPO_ROOT)}")
        names[code] = json.loads(path.read_text(encoding="utf-8"))
    return names


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("output", type=Path, help="куда писать TSV")
    parser.add_argument(
        "--names",
        default="en",
        help="коды языков для колонок с уже имеющимися названиями, через запятую",
    )
    args = parser.parse_args()

    name_codes = [code.strip() for code in args.names.split(",") if code.strip()]
    names = load_names(name_codes)

    categories = json.loads((CATALOG_DIR / "catalog.json").read_text(encoding="utf-8"))
    presets = json.loads((CATALOG_DIR / "countries.json").read_text(encoding="utf-8"))

    # key -> коды стран, в чьих подборках вид уже есть
    already_in: dict[str, list[str]] = {}
    for preset in presets:
        for key in preset["keys"]:
            already_in.setdefault(key, []).append(preset["code"])

    header = ["key", "sci", *name_codes, "breadth", "importance", "dangerous", "already_in"]
    rows = [header]
    for entry in sorted(categories, key=lambda e: e["key"]):
        key = entry["key"]
        importance = entry.get("importance")
        rows.append(
            [
                key,
                entry.get("sci", ""),
                *(names[code].get(key, "") for code in name_codes),
                entry.get("breadth", ""),
                "" if importance is None else f"{importance:g}",
                "yes" if entry.get("dangerous") else "no",
                ",".join(sorted(already_in.get(key, []))),
            ]
        )

    output = args.output.resolve()
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text("\n".join("\t".join(row) for row in rows) + "\n", encoding="utf-8")

    named = {code: sum(1 for r in rows[1:] if r[2 + name_codes.index(code)]) for code in name_codes}
    try:
        shown = output.relative_to(REPO_ROOT)
    except ValueError:  # TSV попросили положить вне репозитория — печатаем как есть
        shown = output
    print(f"{shown}: {len(rows) - 1} видов, {len(presets)} подборок")
    print("названий по колонкам: " + ", ".join(f"{c} {n}" for c, n in named.items()))


if __name__ == "__main__":
    main()
