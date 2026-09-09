#!/usr/bin/env python3
"""Reshapes `listings.json` into whatever column layout the Play Console import template wants.

The exact schema of that template is not published anywhere, so `listings.json` stays the single
source of truth and this script is the only thing that has to change once the real template is
downloaded from the console: edit COLUMNS (and, if the header names differ, HEADERS).

    python3 convert_listings.py                    # -> listings.csv, RFC 4180 quoting, UTF-8
    python3 convert_listings.py --out other.csv
    python3 convert_listings.py --columns locale,title,short      # ad-hoc subset
    python3 convert_listings.py --format tsv
"""
import argparse, csv, json, os, sys

HERE = os.path.dirname(os.path.abspath(__file__))

# field name in listings.json -> column header in the output file
COLUMNS = [
    ("locale", "locale"),
    ("title", "title"),
    ("short", "short_description"),
    ("full", "full_description"),
]

LIMITS = {"title": 30, "short": 80, "full": 4000}


def load(path):
    with open(path, encoding="utf-8") as f:
        return json.load(f)


def rows(data, columns):
    for locale, fields in data.items():
        row = []
        for key, _ in columns:
            row.append(locale if key == "locale" else fields.get(key, ""))
        yield row


def check(data):
    """Play enforces the limits per locale, not per source language — a translation that grew is
    the usual way a listing gets rejected, so refuse to write a file that would be."""
    bad = []
    for locale, fields in data.items():
        for key, limit in LIMITS.items():
            n = len(fields.get(key, ""))
            if n > limit:
                bad.append(f"{locale}.{key}: {n} > {limit}")
    return bad


def main():
    p = argparse.ArgumentParser()
    p.add_argument("--src", default=os.path.join(HERE, "listings.json"))
    p.add_argument("--out", default=None)
    p.add_argument("--columns", default=None,
                   help="comma-separated listings.json field names, overriding COLUMNS")
    p.add_argument("--format", choices=("csv", "tsv"), default="csv")
    p.add_argument("--force", action="store_true", help="write even if a field is over its limit")
    a = p.parse_args()

    data = load(a.src)
    over = check(data)
    if over and not a.force:
        print("over the Play limits:\n  " + "\n  ".join(over), file=sys.stderr)
        return 1
    for line in over:
        print("WARNING " + line, file=sys.stderr)

    columns = [(c, c) for c in a.columns.split(",")] if a.columns else COLUMNS
    out = a.out or os.path.join(HERE, "listings." + a.format)
    delim = "\t" if a.format == "tsv" else ","
    # newline="" + QUOTE_MINIMAL is what keeps the newlines inside full_description RFC 4180-legal
    with open(out, "w", encoding="utf-8", newline="") as f:
        w = csv.writer(f, delimiter=delim, quoting=csv.QUOTE_MINIMAL, lineterminator="\r\n")
        w.writerow([h for _, h in columns])
        w.writerows(rows(data, columns))
    print(f"{out}: {len(data)} locales, columns {[h for _, h in columns]}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
