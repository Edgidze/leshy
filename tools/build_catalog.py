#!/usr/bin/env python3
"""Generates the catalog data/color/image files consumed by the app from the
raw source dump `docs/catalog/leshy_core_app.json`.

See `.claude/plans/countries-and-languages.md`, section 3 and Phase 0, for
the rules this implements. Idempotent — re-running overwrites all outputs
from scratch, safe after the source JSON or `name_overrides.json` changes.

Usage: python3 tools/build_catalog.py
"""

import colorsys
import json
from collections import Counter, defaultdict
from pathlib import Path
from typing import Optional

from PIL import Image

REPO_ROOT = Path(__file__).resolve().parent.parent
SOURCE_JSON = REPO_ROOT / "docs" / "catalog" / "leshy_core_app.json"
OVERRIDES_JSON = REPO_ROOT / "docs" / "catalog" / "name_overrides.json"
IMAGES_SRC_DIR = REPO_ROOT / "app_assets_256" / "mushrooms"
DRAWABLE_DIR = REPO_ROOT / "shared" / "src" / "commonMain" / "composeResources" / "drawable"
FILES_CATALOG_DIR = REPO_ROOT / "shared" / "src" / "commonMain" / "composeResources" / "files" / "catalog"

# Section 3.3: RU preset gained/lost these categories relative to what the
# source dump shipped, per the project owner's decisions.
RU_PRESET_ADD = ["GC0094", "GC0007", "GC0048", "GC0031", "GC0011"]
RU_PRESET_REMOVE = ["GC0165"]

# Section 3.5 addendum for Phase 0: the 6 old demo-catalog group illustrations
# have no 1:1 counterpart in the new 408-category set (their concept got
# split into specific species) and become orphaned once the new images land.
ORPHANED_GROUP_IMAGES = [
    "agaricus_species.webp",
    "gyromitra_species.webp",
    "lycoperdon_calvatia_species.webp",
    "morchella_species.webp",
    "russula_species.webp",
    "xerocomus_subtomentosus_group.webp",
]

# Section 3.5: dominant-hue estimate, ported from `SpeciesFormDialog.kt`'s
# `dominantHue()` + `hueOf()` (must stay bit-for-bit equivalent in spirit —
# same thresholds, same bucket math), extended with per-bucket average S/V
# (source doesn't fix S/V to constants for 408 categories: see plan 3.5) and
# an alpha cutoff (source has an alpha channel; the original one-species
# picker input didn't).
HUE_BUCKETS = 36
SATURATION_MIN = 0.15
VALUE_MIN = 0.12
VALUE_MAX = 0.97
ALPHA_MIN = 0.5
FINAL_SATURATION_RANGE = (0.35, 0.85)
FINAL_VALUE_RANGE = (0.45, 0.80)


def hue_of(r: float, g: float, b: float, mx: float, mn: float) -> float:
    delta = mx - mn
    if delta == 0:
        return 0.0
    if mx == r:
        hue = 60 * (((g - b) / delta) % 6)
    elif mx == g:
        hue = 60 * (((b - r) / delta) + 2)
    else:
        hue = 60 * (((r - g) / delta) + 4)
    return hue + 360 if hue < 0 else hue


def dominant_color_hex(image_path: Path) -> str:
    im = Image.open(image_path).convert("RGBA")
    bucket_weight = [0.0] * HUE_BUCKETS
    bucket_pixels = [[] for _ in range(HUE_BUCKETS)]
    saw_any = False
    for r, g, b, a in im.getdata():
        if a / 255.0 < ALPHA_MIN:
            continue
        rf, gf, bf = r / 255.0, g / 255.0, b / 255.0
        mx, mn = max(rf, gf, bf), min(rf, gf, bf)
        saturation = 0.0 if mx == 0 else (mx - mn) / mx
        value = mx
        if saturation < SATURATION_MIN or value < VALUE_MIN or value > VALUE_MAX:
            continue
        hue = hue_of(rf, gf, bf, mx, mn)
        bucket = min(HUE_BUCKETS - 1, max(0, int(hue // 10)))
        bucket_weight[bucket] += saturation
        bucket_pixels[bucket].append((saturation, value))
        saw_any = True

    if not saw_any:
        raise ValueError(f"{image_path.name}: no pixel passed the color filter")

    best_bucket = max(range(HUE_BUCKETS), key=lambda i: bucket_weight[i])
    hue_deg = best_bucket * 10 + 5
    svs = bucket_pixels[best_bucket]
    avg_s = sum(s for s, _ in svs) / len(svs)
    avg_v = sum(v for _, v in svs) / len(svs)
    s_clamped = min(FINAL_SATURATION_RANGE[1], max(FINAL_SATURATION_RANGE[0], avg_s))
    v_clamped = min(FINAL_VALUE_RANGE[1], max(FINAL_VALUE_RANGE[0], avg_v))
    r, g, b = colorsys.hsv_to_rgb(hue_deg / 360.0, s_clamped, v_clamped)

    def channel(v: float) -> str:
        return format(min(255, max(0, round(v * 255))), "02x")

    return f"#{channel(r)}{channel(g)}{channel(b)}"


def image_basename(image_field: str) -> str:
    """`"mushrooms/gyromitra_esculenta_2.webp"` -> `"gyromitra_esculenta_2"`.

    Deliberately not derived from `key` — 13 duplicate-`sci` categories have
    `__2` (double underscore) in `key` but `_2` (single) in their image
    filename (plan section 1).
    """
    name = image_field.rsplit("/", 1)[-1]
    return name[:-len(".webp")] if name.endswith(".webp") else name


def merge_name(
    category: dict,
    lang: str,
    pair_index: dict,
    languages_by_country: dict,
) -> Optional[str]:
    entries = pair_index.get((category["id"], lang), [])
    if entries:
        main_matches = [
            (cc, name) for cc, name in entries if languages_by_country[cc][0] == lang
        ]
        if len(main_matches) == 1:
            return main_matches[0][1]
        counts = Counter(name for _, name in entries)
        best_count = max(counts.values())
        # Deterministic tie-break: alphabetically smallest name among the
        # most-frequent candidates.
        return min(name for name, count in counts.items() if count == best_count)
    return category["labels"].get(lang)


def main() -> None:
    data = json.loads(SOURCE_JSON.read_text(encoding="utf-8"))
    overrides = json.loads(OVERRIDES_JSON.read_text(encoding="utf-8"))
    categories = data["categories"]
    presets = data["country_presets"]

    categories_by_id = {c["id"]: c for c in categories}
    languages_by_country = {cc: p["languages"] for cc, p in presets.items()}

    all_langs = sorted({lang for c in categories for lang in c["labels"]})

    print(f"{len(categories)} categories, {len(presets)} countries, {len(all_langs)} languages")

    # ---- catalog.json -----------------------------------------------------
    print("Computing dominant colors for all categories...")
    catalog_entries = []
    for c in categories:
        image_path = IMAGES_SRC_DIR / f"{image_basename(c['image'])}.webp"
        color = dominant_color_hex(image_path)
        catalog_entries.append({
            "key": c["key"],
            "sci": c["sci"],
            "image": image_basename(c["image"]),
            "color": color,
            "breadth": c["breadth"],
            "importance": c["importance"],
            "dangerous": c["flags"]["dangerous"],
        })

    FILES_CATALOG_DIR.mkdir(parents=True, exist_ok=True)
    (FILES_CATALOG_DIR / "catalog.json").write_text(
        json.dumps(catalog_entries, ensure_ascii=False, indent=2) + "\n", encoding="utf-8",
    )

    distinct_colors = len({e["color"] for e in catalog_entries})
    print(f"catalog.json: {len(catalog_entries)} entries, {distinct_colors} distinct colors")

    # ---- countries.json -----------------------------------------------------
    countries_out = []
    country_distinct_colors = []
    colors_by_id = {c["id"]: e["color"] for c, e in zip(categories, catalog_entries)}
    for cc in sorted(presets):
        preset = presets[cc]
        items = sorted(preset["items"], key=lambda it: it["order"])
        ids = [it["id"] for it in items]
        if cc == "RU":
            ids = [i for i in ids if i not in RU_PRESET_REMOVE] + RU_PRESET_ADD
        keys = [categories_by_id[i]["key"] for i in ids]
        countries_out.append({
            "code": cc,
            "langs": preset["languages"],
            "keys": keys,
        })
        country_distinct_colors.append(len({colors_by_id[i] for i in ids}))

    (FILES_CATALOG_DIR / "countries.json").write_text(
        json.dumps(countries_out, ensure_ascii=False, indent=2) + "\n", encoding="utf-8",
    )

    avg_country_colors = sum(country_distinct_colors) / len(country_distinct_colors)
    ru_entry = next(c for c in countries_out if c["code"] == "RU")
    ru_colors = next(n for c, n in zip(countries_out, country_distinct_colors) if c["code"] == "RU")
    print(f"countries.json: {len(countries_out)} countries")
    print(f"  avg distinct colors/country: {avg_country_colors:.1f} (expect ~48.4)")
    print(f"  RU: {len(ru_entry['keys'])} keys, {ru_colors} distinct colors (expect 54, 50)")

    # ---- names/<lang>.json -----------------------------------------------------
    pair_index = defaultdict(list)
    for cc, preset in presets.items():
        for it in preset["items"]:
            for lang, name in it.get("names", {}).items():
                pair_index[(it["id"], lang)].append((cc, name))

    names_dir = FILES_CATALOG_DIR / "names"
    names_dir.mkdir(parents=True, exist_ok=True)
    for lang in all_langs:
        result = {}
        for c in categories:
            name = merge_name(c, lang, pair_index, languages_by_country)
            if name:
                result[c["key"]] = name
        for gc_id, over in overrides.items():
            if lang in over:
                result[categories_by_id[gc_id]["key"]] = over[lang]
        (names_dir / f"{lang}.json").write_text(
            json.dumps(result, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8",
        )
    print(f"names/: {len(all_langs)} files written")

    # ---- images -----------------------------------------------------
    DRAWABLE_DIR.mkdir(parents=True, exist_ok=True)
    copied = 0
    for c in categories:
        basename = image_basename(c["image"])
        src = IMAGES_SRC_DIR / f"{basename}.webp"
        dst = DRAWABLE_DIR / f"{basename}.webp"
        dst.write_bytes(src.read_bytes())
        copied += 1

    removed = 0
    for name in ORPHANED_GROUP_IMAGES:
        path = DRAWABLE_DIR / name
        if path.exists():
            path.unlink()
            removed += 1
    print(f"images: copied {copied}, removed {removed} orphaned group images")


if __name__ == "__main__":
    main()
