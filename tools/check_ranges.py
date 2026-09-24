#!/usr/bin/env python3
"""Ищет виды, попавшие в подборку страны, где их не бывает, — по данным GBIF.

Зачем: в исходном дампе каталога нашлись позиции-артефакты. В эстонской подборке стоял
`lactarius_thyinos` — вид североамериканских туевых болот (883 находки в Северной Америке, ноль в
Европе), а рядом `amanita_bisporigera`, неарктическая белая мухоморка, при том что её европейский
аналог `amanita_virosa` в подборке уже был. Оба имени в `names/et.json` несли не видовое, а родовое
название во множественном числе («Riisikad», «Kärbseseened») — признак подставной позиции.

Как проверяется. Два независимых признака, потому что у каждого своя слепая зона:

1. **Континент.** Вид сопоставляется с принятым таксоном GBIF, и берётся фасет `continent`. Если
   на континенте страны у вида меньше 30 находок и это меньше 1% его мировых, позиция подозрительна.
   Признак не зависит от изученности конкретной страны — а она различается на порядки: у
   Азербайджана 44 позиции из 52 вообще без находок в стране, и это не про ошибки в подборке, а про
   то, что в стране почти не ведут наблюдений.
2. **Имя как есть.** GBIF сводит часть имён в синонимы, и тогда ареал принятого вида скрывает
   ошибку: `Lactarius thyinos` сведён к европейскому `L. salmonicolor`, и по принятому таксону
   выглядит европейским. Поэтому для имён со статусом SYNONYM ареал берётся по ключу самого имени.

Печатается ещё две вещи: два ключа одного вида внутри одной подборки (задвоенная плитка) и виды,
которые GBIF не сумел сопоставить до вида, — их проверка не покрывает.

Сеть: только api.gbif.org. Результаты кешируются в `tools/.gbif-range-cache.json` (в git не идёт),
повторный прогон читает кеш и не ходит в сеть. Только стандартная библиотека.

Usage: python3 tools/check_ranges.py [--refresh]
"""
import json
import subprocess
import sys
import time
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
CATALOG = REPO / "shared" / "src" / "commonMain" / "composeResources" / "files" / "catalog"
CACHE = Path(__file__).resolve().parent / ".gbif-range-cache.json"

# Континент подборки. Вторым множеством — страны, которые честно лежат в двух: для них находка на
# любом из двух континентов считается «здесь бывает».
CONTINENT = {
    **{c: "EUROPE" for c in (
        "AL AT BA BE BG BY CH CZ DE DK EE ES FI FR GB GR HR HU IE IS IT LT LU LV MD ME MK NL NO PL "
        "PT RO RS SE SI SK UA").split()},
    **{c: "ASIA" for c in "AM AZ CY GE JP KG KR KZ TJ TM TR UZ".split()},
    "RU": "EUROPE", "CA": "NORTH_AMERICA", "MX": "NORTH_AMERICA", "US": "NORTH_AMERICA",
    "AU": "OCEANIA", "NZ": "OCEANIA",
}
ALSO = {"RU": {"ASIA"}, "TR": {"EUROPE"}, "KZ": {"EUROPE"}, "CY": {"EUROPE"},
        "GE": {"EUROPE"}, "AM": {"EUROPE"}, "AZ": {"EUROPE"}}

MIN_RECORDS = 300      # виды, у которых наблюдений меньше, не судим: слишком мало данных
HERE_MAX = 30          # находок на континенте страны
SHARE_MAX = 0.01       # и их доля от мировых


def resolve_host() -> str:
    """IP api.gbif.org через DoH: системный резолвер в этом окружении может не работать."""
    r = subprocess.run(
        ["curl", "-s", "-H", "accept: application/dns-json",
         "https://1.1.1.1/dns-query?name=api.gbif.org&type=A"],
        capture_output=True, text=True, check=True)
    return next(a["data"] for a in json.loads(r.stdout)["Answer"] if a["type"] == 1)


class Gbif:
    def __init__(self, refresh: bool):
        self.cache = {} if refresh or not CACHE.exists() else json.loads(CACHE.read_text(encoding="utf-8"))
        self.ip = None
        self.fetched = 0

    def get(self, path: str):
        if path in self.cache:
            return self.cache[path]
        if self.ip is None:
            self.ip = resolve_host()
        for _ in range(3):
            r = subprocess.run(
                ["curl", "-s", "--max-time", "30", "--resolve", f"api.gbif.org:443:{self.ip}",
                 f"https://api.gbif.org/v1/{path}"], capture_output=True, text=True)
            try:
                data = json.loads(r.stdout)
                if isinstance(data, dict):
                    break
            except Exception:
                pass
            time.sleep(1.5)
        else:
            data = None
        self.cache[path] = data
        self.fetched += 1
        if self.fetched % 40 == 0:
            self.save()
        return data

    def continents(self, taxon_key) -> dict:
        d = self.get(f"occurrence/search?taxonKey={taxon_key}&facet=continent&facetLimit=20&limit=0")
        counts = {}
        if d:
            for f in d.get("facets", []):
                if f["field"] == "CONTINENT":
                    counts = {x["name"]: x["count"] for x in f["counts"]}
        return {"total": (d or {}).get("count", 0), "by_continent": counts}

    def save(self):
        CACHE.write_text(json.dumps(self.cache, ensure_ascii=False), encoding="utf-8")


def main() -> int:
    gbif = Gbif("--refresh" in sys.argv)
    catalog = {c["key"]: c for c in json.loads((CATALOG / "catalog.json").read_text(encoding="utf-8"))}
    countries = json.loads((CATALOG / "countries.json").read_text(encoding="utf-8"))

    matched, unmatched = {}, []
    for key, entry in catalog.items():
        if entry["sci"].endswith(("sp.", "ceae")):
            continue  # родовые и семейственные плитки — не вид, ареал для них бессмыслен
        m = gbif.get(f"species/match?name={entry['sci'].replace(' ', '%20')}&kingdom=Fungi")
        if not m or "usageKey" not in m or m.get("rank") not in ("SPECIES", "SUBSPECIES", "VARIETY", "FORM"):
            unmatched.append((key, entry["sci"], (m or {}).get("rank"), (m or {}).get("scientificName")))
            continue
        matched[key] = {
            "sci": entry["sci"],
            "name_key": m["usageKey"],
            "accepted_key": m.get("acceptedUsageKey") or m["usageKey"],
            "synonym": m.get("status") == "SYNONYM",
        }

    problems = []
    for country in countries:
        cc = country["code"]
        allowed = {CONTINENT[cc]} | ALSO.get(cc, set())
        for key in country["keys"]:
            info = matched.get(key)
            if not info:
                continue
            # Для синонимов ареал берётся по ключу самого имени: принятый таксон может быть шире и
            # спрятать ошибку (см. docstring, случай Lactarius thyinos).
            taxon = info["name_key"] if info["synonym"] else info["accepted_key"]
            stats = gbif.continents(taxon)
            total = stats["total"]
            if total < MIN_RECORDS:
                continue
            here = sum(v for cont, v in stats["by_continent"].items() if cont in allowed)
            if here < HERE_MAX and here / total < SHARE_MAX:
                problems.append((here / total, cc, key, info["sci"], here, total, stats["by_continent"]))
    gbif.save()

    problems.sort()
    print("=== вид в подборке страны, на чьём континенте его почти не находят ===")
    for _share, cc, key, sci, here, total, by in problems:
        top = ", ".join(f"{a}:{b}" for a, b in sorted(by.items(), key=lambda x: -x[1])[:3])
        print(f"{cc} {key:30} {sci:32} на континенте={here:<5} всего={total:<7} [{top}]")
    print(f"всего: {len(problems)}")

    print("\n=== два ключа одного вида внутри одной подборки ===")
    dupes = 0
    for country in countries:
        seen = {}
        for key in country["keys"]:
            info = matched.get(key)
            if info:
                seen.setdefault(info["accepted_key"], []).append(key)
        for keys in seen.values():
            if len(keys) > 1:
                dupes += 1
                print(f'{country["code"]}: {" + ".join(keys)}')
    print(f"всего: {dupes}")

    print("\n=== GBIF не сопоставил до вида (проверка их не покрывает) ===")
    for key, sci, rank, got in unmatched:
        print(f"{key:30} {sci:32} -> {got} ({rank})")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
