#!/usr/bin/env python3
"""Checks `listings.json` against App Store Connect field limits and writes one file per locale.

App Store Connect has no bulk import of metadata in the web UI — unlike Play, where a CSV
template exists, here the fields are either typed in by hand (locale by locale) or pushed through
the App Store Connect API. For two locales the API is not worth its key, so this script produces
`listing_<locale>.md` for copy-pasting, and nothing else.

    python3 render_listings.py          # validate + write listing_<locale>.md
    python3 render_listings.py --check  # validate only, write nothing

Limits are checked **per locale**, not for the source language: a translation that grew past the
limit is the usual way a listing gets bounced.
"""
import argparse, json, os, sys

HERE = os.path.dirname(os.path.abspath(__file__))

# field in listings.json -> (App Store Connect label, character limit)
FIELDS = [
    ("title", "Name", 30),
    ("subtitle", "Subtitle", 30),
    ("keywords", "Keywords", 100),
    ("promotional_text", "Promotional Text", 170),
    ("description", "Description", 4000),
]


def check(data):
    bad = []
    for locale, fields in data.items():
        for key, label, limit in FIELDS:
            n = len(fields.get(key, ""))
            if n > limit:
                bad.append(f"{locale}.{key} ({label}): {n} > {limit}")
    return bad


def render(locale, fields):
    out = [f"# App Store — {locale}\n"]
    for key, label, limit in FIELDS:
        value = fields.get(key, "")
        out.append(f"\n## {label} ({len(value)}/{limit})\n\n")
        # Description is the only multi-line field; the rest are single lines and fenced so that
        # trailing spaces and commas survive the copy.
        out.append(value + "\n" if "\n" in value else f"```\n{value}\n```\n")
    return "".join(out)


def main():
    p = argparse.ArgumentParser()
    p.add_argument("--src", default=os.path.join(HERE, "listings.json"))
    p.add_argument("--check", action="store_true", help="validate only")
    args = p.parse_args()

    with open(args.src, encoding="utf-8") as f:
        data = json.load(f)

    bad = check(data)
    if bad:
        print("Over the limit:", *bad, sep="\n  ", file=sys.stderr)
        return 1
    for locale, fields in data.items():
        for key, label, limit in FIELDS:
            print(f"{locale:6} {label:17} {len(fields.get(key, '')):5} / {limit}")

    if args.check:
        return 0
    for locale, fields in data.items():
        path = os.path.join(HERE, f"listing_{locale}.md")
        with open(path, "w", encoding="utf-8") as f:
            f.write(render(locale, fields))
        print("written", os.path.basename(path))
    return 0


if __name__ == "__main__":
    sys.exit(main())
