#!/usr/bin/env python3
"""Generates the catalog data/color/image files consumed by the app from the
raw source dump `docs/catalog/leshy_core_app.json` plus the manual layers
beside it in `docs/catalog/`.

See `.claude/plans/countries-and-languages.md`, section 3 and Phase 0, for the
rules this implements, and `.claude/plans/post-soviet-countries.md`, Phase 0,
for the `extra_*` layers and the incremental colour pass. Idempotent —
re-running rewrites every output, and a run that adds no new data must not
change a single byte (that is the acceptance test for the repair).

Requires `Pillow` (dominant colors) and `Babel` (CLDR country names). `Babel`
is not in the system python and no longer needs a venv: it lives in the project
(`tools/.pydeps/`, gitignored) and `tools/pydeps.py` puts it on the path here.
Once, on a fresh checkout: `python3 tools/pydeps.py --fetch`.

Usage: python3 tools/build_catalog.py
       python3 tools/build_catalog.py --only-country-names
       python3 tools/build_catalog.py --recompute-colors   # see below

`app_assets_256/`, the original 256×256 source images, was deleted after Phase 0
of the first plan — the images live on in `composeResources/drawable/`, which is
where this script now reads them from when the original directory is absent.

**Colours are computed only for keys `catalog.json` does not already have.**
That is not an optimization, it is correctness: re-deriving all 408 from
`drawable/` disagrees with the stored values for 298 of them (mostly ±1/255,
but 12 species flip to a neighbouring hue bucket and change visibly — the
webp bytes and/or the libwebp decoder are no longer what Phase 0 ran on).
`--recompute-colors` forces the full pass anyway; expect that churn, and expect
every installed device to re-upsert all 408 `colorHex` values because
`CatalogSource.version` is a hash of the file.
"""

import argparse
import colorsys
import json
import re
from collections import Counter, defaultdict
from pathlib import Path
from typing import Optional

import pydeps

pydeps.ensure_on_path()  # `tools/.pydeps` on sys.path — must precede `babel`

from babel import Locale  # noqa: E402
from PIL import Image  # noqa: E402

REPO_ROOT = Path(__file__).resolve().parent.parent
SOURCE_JSON = REPO_ROOT / "docs" / "catalog" / "leshy_core_app.json"
OVERRIDES_JSON = REPO_ROOT / "docs" / "catalog" / "name_overrides.json"
COUNTRY_OVERRIDES_JSON = REPO_ROOT / "docs" / "catalog" / "country_name_overrides.json"
IMAGES_SRC_DIR = REPO_ROOT / "app_assets_256" / "mushrooms"
DRAWABLE_DIR = REPO_ROOT / "shared" / "src" / "commonMain" / "composeResources" / "drawable"
FILES_CATALOG_DIR = REPO_ROOT / "shared" / "src" / "commonMain" / "composeResources" / "files" / "catalog"
APP_LANGUAGE_KT = (
    REPO_ROOT / "shared" / "src" / "commonMain" / "kotlin" / "leshy" / "mushrooms" / "map"
    / "domain" / "model" / "AppLanguage.kt"
)

# The three manual layers of `.claude/plans/post-soviet-countries.md` §5. All
# three are optional: absent means "no extras", and the script then reproduces
# exactly what the source dump alone produces. Unlike `name_overrides.json`
# (which stayed on the dump's `GC####` ids), these are keyed by catalog `key` —
# that is what the research prompts, the TSV handed to them, and the app itself
# all speak; a `GC` id means nothing in any of those three places.
EXTRA_PRESETS_JSON = REPO_ROOT / "docs" / "catalog" / "extra_country_presets.json"
EXTRA_CATEGORIES_JSON = REPO_ROOT / "docs" / "catalog" / "extra_categories.json"
EXTRA_NAMES_DIR = REPO_ROOT / "docs" / "catalog" / "extra_names"

# Section 3.3: RU preset gained/lost these categories relative to what the
# source dump shipped, per the project owner's decisions.
RU_PRESET_ADD = ["GC0094", "GC0007", "GC0048", "GC0031", "GC0011"]
RU_PRESET_REMOVE = ["GC0165"]

# Country display names come from CLDR (shipped inside `babel`), for every
# `AppLanguage` at once — see the plan's "Решения, принятые после Фазы 5". This
# replaced the hand-written per-language tables Phase 3 started with, which
# would otherwise have made every translation batch carry 33 country names.
# Verified when switching: all 33 codes resolve in all 26 languages, and CLDR's
# English matches the source dump's `country` field exactly.
#
# `docs/catalog/country_name_overrides.json` is the manual layer on top, same
# precedent as `name_overrides.json`: CLDR gives only the formal form of a
# territory (`Locale.territories` has no access to CLDR's `alt="short"`), and a
# picker list wants the short one — "США", not "Соединенные Штаты".

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


def app_language_codes() -> list[str]:
    """The `code` of every `AppLanguage` entry, read out of the enum itself.

    Parsed rather than duplicated here so that adding a 27th interface language
    is a one-line Kotlin change: re-running this script then generates its
    country names too, instead of silently leaving the language without a
    `countries/<lang>.json` (which `CountryNames.namesFor` would swallow via its
    `runCatching`, falling back to English forever).
    """
    text = APP_LANGUAGE_KT.read_text(encoding="utf-8")
    codes = re.findall(r'^\s+[A-Z_]+\("([a-z]{2}(?:-[A-Za-z]+)?)",', text, re.MULTILINE)
    if len(codes) < 20 or "ru" not in codes or "en" not in codes:
        raise ValueError(f"AppLanguage.kt parse looks wrong, got {len(codes)} codes: {codes}")
    if len(set(codes)) != len(codes):
        raise ValueError(f"AppLanguage.kt has duplicate codes: {codes}")
    return codes


def load_optional_json(path: Path, default):
    """The `extra_*` layers are all optional — absent means "no extras", not an error."""
    if not path.exists():
        return default
    return json.loads(path.read_text(encoding="utf-8"))


def images_dir() -> Path:
    """Where a category's illustration is read from.

    `app_assets_256/` was the original source and is gone; `drawable/` holds a
    copy of all 410 files and is where newly generated ones are dropped. The
    original directory still wins if someone restores it, so a full rebuild from
    the untouched source stays possible.
    """
    return IMAGES_SRC_DIR if IMAGES_SRC_DIR.is_dir() else DRAWABLE_DIR


def load_extra_categories(categories: list) -> list:
    """`docs/catalog/extra_categories.json` — species the source dump has no entry for.

    Same shape as one element of the dump's `categories`, minus what this script
    doesn't read (`alt_names`, `label`, `label_lang`, ...). `image` may be a bare
    basename — [image_basename] strips a `mushrooms/` prefix and a `.webp` suffix
    either way. Names do *not* go here: they belong in `extra_names/<lang>.json`,
    so that one file per language stays the only place a name is written.
    """
    extras = load_optional_json(EXTRA_CATEGORIES_JSON, [])
    if not extras:
        return []

    known_ids = {c["id"] for c in categories}
    known_keys = {c["key"] for c in categories}
    out = []
    for entry in extras:
        missing = [f for f in ("id", "key", "sci", "image", "breadth", "importance") if f not in entry]
        if missing:
            raise ValueError(f"{EXTRA_CATEGORIES_JSON.name}: entry {entry.get('key', entry)} lacks {missing}")
        if entry["id"] in known_ids:
            raise ValueError(f"{EXTRA_CATEGORIES_JSON.name}: id {entry['id']} collides with the source dump")
        if entry["key"] in known_keys:
            raise ValueError(f"{EXTRA_CATEGORIES_JSON.name}: key {entry['key']} collides with the source dump")
        image = images_dir() / f"{image_basename(entry['image'])}.webp"
        if not image.exists():
            raise ValueError(f"{EXTRA_CATEGORIES_JSON.name}: {entry['key']} has no illustration at {image}")
        known_ids.add(entry["id"])
        known_keys.add(entry["key"])
        out.append({
            "id": entry["id"],
            "key": entry["key"],
            "sci": entry["sci"],
            "image": entry["image"],
            "breadth": entry["breadth"],
            "importance": entry["importance"],
            "flags": {"dangerous": entry.get("flags", {}).get("dangerous", entry.get("dangerous", False))},
            "labels": entry.get("labels", {}),
        })
    return out


def load_extra_presets(categories: list) -> dict:
    """`docs/catalog/extra_country_presets.json` — country collections the dump has no preset for.

    Written as `{cc: {country, languages, keys}}` because that is what the
    research sessions return (`docs/research/post-soviet/README.md`); converted
    here into the dump's `{country, languages, items: [{order, id, names}]}` so
    everything downstream stays unaware there are two sources. `names` is left
    empty on purpose — species names for these countries come from
    `extra_names/<lang>.json`, never from the preset.
    """
    extras = load_optional_json(EXTRA_PRESETS_JSON, {})
    if not extras:
        return {}

    id_by_key = {c["key"]: c["id"] for c in categories}
    out = {}
    for cc, preset in extras.items():
        missing = [f for f in ("country", "languages", "keys") if f not in preset]
        if missing:
            raise ValueError(f"{EXTRA_PRESETS_JSON.name}: {cc} lacks {missing}")
        keys = preset["keys"]
        duplicates = sorted({k for k in keys if keys.count(k) > 1})
        if duplicates:
            raise ValueError(f"{EXTRA_PRESETS_JSON.name}: {cc} repeats keys: {duplicates}")
        unknown = [k for k in keys if k not in id_by_key]
        if unknown:
            raise ValueError(f"{EXTRA_PRESETS_JSON.name}: {cc} names keys outside the catalog: {unknown}")
        if not preset["languages"]:
            raise ValueError(f"{EXTRA_PRESETS_JSON.name}: {cc} has an empty `languages`")
        out[cc] = {
            "country": preset["country"],
            "languages": preset["languages"],
            "items": [
                {"order": i, "id": id_by_key[key], "names": {}}
                for i, key in enumerate(keys, start=1)
            ],
        }
    return out


def load_extra_names(categories: list) -> dict:
    """`docs/catalog/extra_names/<lang>.json` (`{key: name}`) -> `{lang: {key: name}}`.

    The last layer applied to `names/<lang>.json`, so it also wins over
    `name_overrides.json` — it is the newer and more specific of the two.
    """
    if not EXTRA_NAMES_DIR.is_dir():
        return {}

    known_keys = {c["key"] for c in categories}
    out = {}
    for path in sorted(EXTRA_NAMES_DIR.glob("*.json")):
        names = json.loads(path.read_text(encoding="utf-8"))
        unknown = sorted(set(names) - known_keys)
        if unknown:
            raise ValueError(f"{path.name}: names keys outside the catalog: {unknown}")
        blank = sorted(k for k, v in names.items() if not v.strip())
        if blank:
            raise ValueError(f"{path.name}: blank names for: {blank}")
        if names:
            out[path.stem] = names
    return out


def resolve_colors(categories: list, recompute: bool) -> dict:
    """`{key: color}`, reusing what `catalog.json` already stores.

    See the module docstring for why re-deriving the existing 408 from
    `drawable/` is not equivalent to reading them back.
    """
    stored = {e["key"]: e["color"] for e in load_optional_json(FILES_CATALOG_DIR / "catalog.json", [])}
    if recompute:
        stored = {}

    colors, computed = {}, 0
    for category in categories:
        key = category["key"]
        if key in stored:
            colors[key] = stored[key]
            continue
        colors[key] = dominant_color_hex(images_dir() / f"{image_basename(category['image'])}.webp")
        computed += 1
    print(f"colors: {len(colors) - computed} reused from catalog.json, {computed} computed from images")
    return colors


def write_country_names(presets: dict, out_dir: Path) -> None:
    codes = sorted(presets)
    languages = app_language_codes()
    overrides = json.loads(COUNTRY_OVERRIDES_JSON.read_text(encoding="utf-8"))

    unknown = sorted(set(overrides) - set(codes))
    if unknown:
        raise ValueError(f"country_name_overrides.json names unknown countries: {unknown}")

    # The source dump's own English names are no longer written out directly,
    # so check they still agree with CLDR rather than dropping them silently.
    for cc in codes:
        source_name = presets[cc]["country"]
        cldr_name = Locale.parse("en").territories.get(cc)
        if source_name != cldr_name:
            print(f"  warning: source `country` for {cc} is {source_name!r}, CLDR says {cldr_name!r}")

    out_dir.mkdir(parents=True, exist_ok=True)
    overridden = 0
    for lang in languages:
        territories = Locale.parse(lang.replace("-", "_")).territories
        missing = [cc for cc in codes if cc not in territories]
        if missing:
            raise ValueError(f"CLDR has no {lang} name for: {missing}")
        names = {cc: territories[cc] for cc in codes}
        for cc, per_lang in overrides.items():
            if lang in per_lang:
                names[cc] = per_lang[lang]
                overridden += 1
        (out_dir / f"{lang}.json").write_text(
            json.dumps(names, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8",
        )
    print(f"countries/: {len(languages)} files written ({len(codes)} countries each, "
          f"{overridden} manual overrides applied)")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--only-country-names", action="store_true",
        help="Regenerate countries/<lang>.json only — the cheap section, no images touched.",
    )
    parser.add_argument(
        "--recompute-colors", action="store_true",
        help="Re-derive every dominant colour from the images instead of reusing the ones "
             "catalog.json already stores. Changes ~298 of 408 values, 12 of them visibly — "
             "see the module docstring before using this.",
    )
    args = parser.parse_args()

    if args.only_country_names:
        data = json.loads(SOURCE_JSON.read_text(encoding="utf-8"))
        categories = data["categories"] + load_extra_categories(data["categories"])
        presets = {**data["country_presets"], **load_extra_presets(categories)}
        write_country_names(presets, FILES_CATALOG_DIR / "countries")
        return

    run_full(recompute_colors=args.recompute_colors)


def run_full(recompute_colors: bool = False) -> None:
    data = json.loads(SOURCE_JSON.read_text(encoding="utf-8"))
    overrides = json.loads(OVERRIDES_JSON.read_text(encoding="utf-8"))
    categories = data["categories"]
    presets = data["country_presets"]

    # Manual layers (`.claude/plans/post-soviet-countries.md` §5). Appended
    # rather than interleaved so that every existing category keeps its index —
    # `EnsureDefaultCategoriesUseCase` derives `Category.order` from it.
    extra_categories = load_extra_categories(categories)
    categories = categories + extra_categories
    extra_presets = load_extra_presets(categories)
    collisions = sorted(set(extra_presets) & set(presets))
    if collisions:
        raise ValueError(f"{EXTRA_PRESETS_JSON.name}: {collisions} already exist in the source dump")
    presets = {**presets, **extra_presets}
    extra_names = load_extra_names(categories)

    categories_by_id = {c["id"]: c for c in categories}
    languages_by_country = {cc: p["languages"] for cc, p in presets.items()}

    all_langs = sorted({lang for c in categories for lang in c["labels"]} | set(extra_names))

    print(f"{len(categories)} categories (+{len(extra_categories)} extra), "
          f"{len(presets)} countries (+{len(extra_presets)} extra), {len(all_langs)} languages")

    # ---- catalog.json -----------------------------------------------------
    colors = resolve_colors(categories, recompute_colors)
    catalog_entries = []
    for c in categories:
        catalog_entries.append({
            "key": c["key"],
            "sci": c["sci"],
            "image": image_basename(c["image"]),
            "color": colors[c["key"]],
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

    # ---- countries/<lang>.json ---------------------------------------------
    write_country_names(presets, FILES_CATALOG_DIR / "countries")

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
        result.update(extra_names.get(lang, {}))
        (names_dir / f"{lang}.json").write_text(
            json.dumps(result, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8",
        )
    print(f"names/: {len(all_langs)} files written ({len(extra_names)} of them fed by extra_names/)")

    # ---- images -----------------------------------------------------
    DRAWABLE_DIR.mkdir(parents=True, exist_ok=True)
    copied, missing = 0, []
    for c in categories:
        basename = image_basename(c["image"])
        src = images_dir() / f"{basename}.webp"
        dst = DRAWABLE_DIR / f"{basename}.webp"
        if not src.exists():
            missing.append(basename)
            continue
        # No-op when the source *is* `drawable/` (`app_assets_256/` gone): the
        # file is already where it belongs, and rewriting it would only risk
        # touching bytes for nothing.
        if src != dst:
            dst.write_bytes(src.read_bytes())
            copied += 1
    if missing:
        raise ValueError(f"no illustration in {images_dir()} for: {missing}")

    removed = 0
    for name in ORPHANED_GROUP_IMAGES:
        path = DRAWABLE_DIR / name
        if path.exists():
            path.unlink()
            removed += 1
    print(f"images: {len(categories)} verified, {copied} copied, "
          f"{removed} orphaned group images removed")


if __name__ == "__main__":
    main()
