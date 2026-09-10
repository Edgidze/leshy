#!/usr/bin/env python3
"""Builds a demo import archive for the Google Play listing screenshots.

Input: the owner's own export — walk-1, 4.75 km, 102 finds, 6 photographed places.

Output: six walks that keep the *character* of that walk (how finds cling to the path, how they
clump, the shape of the species mix) but share nothing identifying with it:

  * geometry — mirrored E-W, rotated, scaled and bent by a low-frequency wobble, so no stretch of
    the real GPS trace survives as such, then rigidly placed around a different lake;
  * place — an unnamed stretch of forest by Naukajarvi (61.340 N, 25.025 E, Finland), 1.4 km from
    the nearest building. `place_walk.py` picked walk 1's rotation/offset so that the track grazes
    the lake shore and a stream without ever crossing the water;
  * species — remapped onto the Finland collection (the one the demo device has switched on);
  * text — English throughout.

Place marks are *snapped* to real OSM features: "Forest pond" onto the actual shoreline,
"Stream below the bank" onto an actual stream. A photo of water next to a map with no water on it
is the one thing that reads as fake immediately.
"""
import json, math, os, random, shutil, sys, zipfile, datetime

from PIL import Image

SRC = "walk"
OUT = "demo"
random.seed(20260908)

TZ = datetime.timezone(datetime.timedelta(hours=3))  # Europe/Helsinki, summer time

R = 6371000.0

# ---------------------------------------------------------------- OSM ground truth
osm = json.load(open("osm_area.json"))["elements"]


def geom(e):
    return [(g["lat"], g["lon"]) for g in e.get("geometry", [])]


LAKE = max((e for e in osm if e.get("tags", {}).get("natural") == "water"),
           key=lambda e: len(e.get("geometry", [])))
LAKE_PTS = geom(LAKE)
LC = (sum(p[0] for p in LAKE_PTS) / len(LAKE_PTS), sum(p[1] for p in LAKE_PTS) / len(LAKE_PTS))
STREAMS = [geom(e) for e in osm
           if e.get("tags", {}).get("waterway") in ("stream", "flowline", "ditch")]


def xy(lat, lon, lat0=None, lon0=None):
    lat0 = LC[0] if lat0 is None else lat0
    lon0 = LC[1] if lon0 is None else lon0
    return (math.radians(lon - lon0) * R * math.cos(math.radians(lat0)),
            math.radians(lat - lat0) * R)


def geo(x, y, lat0=None, lon0=None):
    lat0 = LC[0] if lat0 is None else lat0
    lon0 = LC[1] if lon0 is None else lon0
    return (lat0 + math.degrees(y / R), lon0 + math.degrees(x / (R * math.cos(math.radians(lat0)))))


LAKE_XY = [xy(*p) for p in LAKE_PTS]
LAKE_SEGS = [(LAKE_XY[i], LAKE_XY[(i + 1) % len(LAKE_XY)]) for i in range(len(LAKE_XY))]
STREAM_SEGS = [(xy(*s[i]), xy(*s[i + 1])) for s in STREAMS for i in range(len(s) - 1)]


def seg_point(p, a, b):
    """closest point on segment ab to p, plus the distance to it"""
    px, py = p; ax, ay = a; bx, by = b
    dx, dy = bx - ax, by - ay
    if dx == dy == 0:
        return a, math.hypot(px - ax, py - ay)
    t = max(0.0, min(1.0, ((px - ax) * dx + (py - ay) * dy) / (dx * dx + dy * dy)))
    q = (ax + t * dx, ay + t * dy)
    return q, math.hypot(px - q[0], py - q[1])


def nearest(p, segs):
    best_q, best_d = None, 1e9
    for a, b in segs:
        q, d = seg_point(p, a, b)
        if d < best_d:
            best_q, best_d = q, d
    return best_q, best_d


def inside_lake(p):
    x, y = p; c = False; n = len(LAKE_XY)
    for i in range(n):
        x1, y1 = LAKE_XY[i]; x2, y2 = LAKE_XY[(i + 1) % n]
        if ((y1 > y) != (y2 > y)) and x < (x2 - x1) * (y - y1) / (y2 - y1) + x1:
            c = not c
    return c


def haversine(a, b):
    la1, lo1, la2, lo2 = map(math.radians, (a[0], a[1], b[0], b[1]))
    h = math.sin((la2 - la1) / 2) ** 2 + math.cos(la1) * math.cos(la2) * math.sin((lo2 - lo1) / 2) ** 2
    return 2 * R * math.asin(math.sqrt(h))


# ---------------------------------------------------------------- language packs
# Walk and place texts live in the archive as *content*: the app localises its own interface and
# the mushroom names, but a walk called "Ridge behind the bog" stays English however the interface
# is set. One pack per store locale, picked with `python3 make_demo_archive.py <lang>`.
TEXTS = {
    "en": dict(
        walks=["Ridge behind the bog", "Early morning, west side", "After the rain",
               "First walk of the season", "Along the cutting", "Late October, mostly empty"],
        descriptions={
            0: "Warm and dry after a week of rain. Started at the ride, went round the bog and "
               "back along the ridge — chanterelles by the ditch as always, and the birch boletes "
               "have clearly only just started.",
            1: "Two hours before the heat. Slow, along the wet side only.",
        },
        places={
            "pond": ("Forest pond",
                     "Lily pads, and an easy way down to the water. Good place to sit down and "
                     "think."),
            "streamtree": ("Alder over the water",
                           "The old alder leans right over the stream — visible from the ride, "
                           "the crossing is just below it."),
            "windfall": ("Fallen spruce",
                         "Landmark for the mushroom clearing to the right of it."),
            "pine": ("The tall pine",
                     "Head for it from anywhere on the ridge — it stands a good head above the "
                     "rest of the stand."),
            "ride": ("Overgrown ride",
                     "The old ride, grown in with young spruce. Chanterelles along its edges."),
            "ditch": ("Old ditch",
                      "Overgrown drainage ditch — chanterelles along both banks, year after year."),
            "bog": ("Boggy hollow",
                    "Wet all summer, spruce along the edge. Skirt it on the north side."),
        },
    ),
    "ru": dict(
        walks=["Гряда за болотом", "Раннее утро, западный край", "После дождя",
               "Первый выход за сезон", "Вдоль просеки", "Конец октября, почти пусто"],
        descriptions={
            0: "Тепло и сухо после недели дождей. Начал от просеки, обошёл болото и назад по "
               "гряде — лисички у канавы как всегда, а подберёзовики явно ещё только начались",
            1: "Два часа до жары. Медленно, только по сырой стороне.",
        },
        places={
            "pond": ("Лесной пруд",
                     "Кувшинки и удобный спуск к воде. Хорошее место посидеть и подумать."),
            "streamtree": ("Ольха над водой",
                           "Старая ольха наклонилась прямо над ручьём — видно с просеки, переход "
                           "сразу под ней."),
            "windfall": ("Упавшая ель",
                         "Ориентир для грибной поляны справа"),
            "pine": ("Высокая сосна",
                     "На неё можно выходить с любого места гряды — она заметно выше остального леса."),
            "ride": ("Заросшая просека",
                     "Старая просека, заросшая молодым ельником. Лисички по краям."),
            "ditch": ("Старая канава",
                      "Заросшая мелиоративная канава — лисички по обоим берегам, год за годом."),
            "bog": ("Мокрая низина",
                    "Сырая всё лето, по краю ельник. Обходить с севера."),
        },
    ),
}

LANG = sys.argv[1] if len(sys.argv) > 1 else "en"
PACK = TEXTS[LANG]


# ---------------------------------------------------------------- geometry warp
class Warp:
    """mirror -> rotate -> scale -> low-frequency wobble, in metres around the source centroid"""

    def __init__(self, src_lat, src_lon, angle_deg, scale, mirror, wobble_m=18.0):
        self.s_lat, self.s_lon = src_lat, src_lon
        self.a = math.radians(angle_deg)
        self.scale = scale
        self.mirror = mirror
        self.h = [(random.uniform(0.6, 2.2), random.uniform(0, 6.28), random.uniform(0.5, 1.0)) for _ in range(3)]
        self.v = [(random.uniform(0.6, 2.2), random.uniform(0, 6.28), random.uniform(0.5, 1.0)) for _ in range(3)]
        self.wobble = wobble_m

    def __call__(self, lat, lon):
        x, y = xy(lat, lon, self.s_lat, self.s_lon)
        if self.mirror:
            x = -x
        ca, sa = math.cos(self.a), math.sin(self.a)
        x, y = (x * ca - y * sa) * self.scale, (x * sa + y * ca) * self.scale
        t = (x + y) / 900.0
        dx = sum(w * math.sin(f * t + p) for f, p, w in self.h) * self.wobble
        dy = sum(w * math.sin(f * t + p) for f, p, w in self.v) * self.wobble
        return x + dx, y + dy


def rigid(points, angle_deg, centre_xy):
    """rotate a metric point list about its own centroid, then move that centroid to centre_xy"""
    cx = sum(p[0] for p in points) / len(points)
    cy = sum(p[1] for p in points) / len(points)
    a = math.radians(angle_deg); ca, sa = math.cos(a), math.sin(a)
    out = []
    for x, y in points:
        x, y = x - cx, y - cy
        out.append((x * ca - y * sa + centre_xy[0], x * sa + y * ca + centre_xy[1]))
    return out


# ---------------------------------------------------------------- source data
base_track = json.load(open(f"{SRC}/walks/walk-1/track.json"))
base_objects = json.load(open(f"{SRC}/walks/walk-1/objects.json"))
src_lat = sum(p["lat"] for p in base_track) / len(base_track)
src_lon = sum(p["lon"] for p in base_track) / len(base_track)

# Place photos: three are the owner's own forest shots, two the best frames of the source walk.
# The first two are snapped onto real water, so their text may name water; the rest are features
# too small for OSM to carry and are believable anywhere in this forest.
# Two of them name water and are therefore snapped onto real OSM geometry — the lake shore and a
# stream; the other five name things too small for OSM to carry, and are believable anywhere in
# this forest. One or two marks per walk, no more: on the combined map a crowd of photo pins
# competes with the mushrooms, which are what the app is actually about.
PLACES = {
    key: dict(photo=photo, snap=snap, name=PACK["places"][key][0], text=PACK["places"][key][1])
    for key, photo, snap in [
        ("pond", "pond.jpg", "lake"),
        ("streamtree", "streamtree.jpg", "stream"),
        ("windfall", "windfall.jpg", None),
        ("pine", "pine.jpg", None),
        ("ride", "ride.jpg", None),
        ("ditch", "ditch.jpg", None),
        ("bog", "bog.jpg", None),
    ]
}

# The owner's walk was picked in a Russian forest; the demo forest is Finnish and the demo device
# has the Finland collection switched on, so every species is remapped onto a member of that
# collection that also carries an English common name — otherwise half the tiles would read as
# Latin binomials in an English-language listing. The shape of the mix is kept exactly.
SPECIES_REMAP = {
    "lactarius_flexuosus": "cortinarius_caperatus",     # 36 — gypsy mushroom
    "cantharellus_cibarius": "cantharellus_cibarius",   # 20 — chanterelle
    "suillus_bovinus": "craterellus_tubaeformis",       # 10 — winter chanterelle
    "lactarius_rufus": "hydnum_repandum",               #  7 — hedgehog fungus
    "leccinum_aurantiacum": "albatrellus_ovinus",       #  5 — sheep polypore
    "leccinum_scabrum": "leccinum_scabrum",             #  4 — birch bolete
    "paxillus_involutus": "paxillus_involutus",         #  3 — brown roll-rim
    "amanita_muscaria": "amanita_muscaria",             #  3 — fly agaric
    "imleria_badia": "russula_cyanoxantha__2",          #  3 — charcoal burner
    "russula_cyanoxantha": "tylopilus_felleus",         #  2 — bitter bolete
    "ramaria_flava": "hygrophoropsis_aurantiaca",       #  2 — false chanterelle
    "amanita_pantherina": "amanita_pantherina",         #  1 — panther cap
    "amanita_virosa": "amanita_virosa",                 #  1 — destroying angel
    "lactarius_necator": "lactarius_helvus",            #  1 — maple-scented milkcap
    "boletus_edulis": "boletus_edulis",                 #  1 — porcini
    "suillus_luteus__2": "suillus_luteus",              #  1 — slippery jack
    "russula_nigricans": "macrolepiota_procera",        #  1 — parasol
    "lycoperdon_perlatum": "agaricus_arvensis",         #  1 — horse mushroom
}


def ts(y, mo, d, h, mi):
    return int(datetime.datetime(y, mo, d, h, mi, tzinfo=TZ).timestamp() * 1000)


# `post` = (rotation about the walk's own centroid, centroid position in metres from the lake
# centre). Walk 1's pair comes from place_walk.py, the other five from place_others.py: each is
# searched for a rotation/offset that keeps the whole track >110 m from any road, >320 m from any
# building and out of the lake, while spreading the walks around the main one.
WALKS = [
    dict(seg=(0, 760), start=ts(2026, 8, 30, 9, 12), keep=1.0,
         angle=44, scale=0.96, mirror=True, post=(300, (275, 600)), places=["pond", "windfall"]),
    dict(seg=(150, 400), start=ts(2026, 9, 4, 7, 40), keep=0.55,
         angle=-72, scale=0.92, mirror=False, post=(180, (-470, 270)), places=["pine"]),
    dict(seg=(330, 620), start=ts(2026, 8, 12, 15, 5), keep=0.7,
         angle=128, scale=1.02, mirror=True, post=(180, (670, -430)), places=["streamtree"]),
    dict(seg=(120, 400), start=ts(2026, 7, 22, 10, 20), keep=0.32,
         angle=-155, scale=0.88, mirror=False, post=(210, (-210, -670)), places=["ride"]),
    dict(seg=(430, 700), start=ts(2025, 9, 16, 11, 0), keep=0.62,
         angle=20, scale=0.95, mirror=True, post=(330, (-610, 510)), places=["ditch"]),
    dict(seg=(520, 660), start=ts(2025, 10, 11, 12, 30), keep=0.18,
         angle=-40, scale=0.9, mirror=False, post=(300, (530, -610)), places=["bog"]),
]

if os.path.isdir(OUT):
    shutil.rmtree(OUT)
os.makedirs(OUT)

report = []
for idx, w in enumerate(WALKS, start=1):
    w = dict(w, name=PACK["walks"][idx - 1],
             description=PACK["descriptions"].get(idx - 1, ""))
    warp = Warp(src_lat, src_lon, w["angle"], w["scale"], w["mirror"])
    a, b = w["seg"]
    seg = base_track[a:b]
    t0 = seg[0]["timestamp"]

    # warp track and objects together, then place both with one and the same rigid transform
    track_xy = [warp(p["lat"], p["lon"]) for p in seg]
    t_lo, t_hi = seg[0]["timestamp"], seg[-1]["timestamp"]
    kept = []
    poi_index = 0
    for o in base_objects:
        if not (t_lo <= o["timestamp"] <= t_hi):
            continue
        if o["type"] != "MUSHROOM":
            if poi_index >= len(w["places"]):
                continue
            kept.append((o, PLACES[w["places"][poi_index]]))
            poi_index += 1
        else:
            if random.random() > w["keep"]:
                continue
            kept.append((o, None))
    obj_xy = [warp(o["lat"], o["lon"]) for o, _ in kept]

    n = len(track_xy)
    placed = rigid(track_xy + obj_xy, w["post"][0], w["post"][1])
    track_xy, obj_xy = placed[:n], placed[n:]

    assert not any(inside_lake(p) for p in track_xy), f"walk {idx} runs across the lake"

    track = [dict(lat=round(geo(*p)[0], 8), lon=round(geo(*p)[1], 8),
                  timestamp=w["start"] + (s["timestamp"] - t0),
                  elevation=s["elevation"], sequence=i)
             for i, (p, s) in enumerate(zip(track_xy, seg))]

    objs = []
    for (o, spec), p in zip(kept, obj_xy):
        name, desc, photo = o["name"], o["description"], o["photoFile"]
        if spec:
            name, desc, photo = spec["name"], spec["text"], f"photos/{spec['photo']}"
            if spec["snap"]:
                segs = LAKE_SEGS if spec["snap"] == "lake" else STREAM_SEGS
                # put the mark where the track came closest to the feature, on the feature itself
                anchor = min(track_xy, key=lambda q: nearest(q, segs)[1])
                p = nearest(anchor, segs)[0]
        lat, lon = geo(*p)
        objs.append(dict(categoryNameKey=SPECIES_REMAP.get(o["categoryNameKey"], o["categoryNameKey"]),
                         lat=round(lat, 8), lon=round(lon, 8),
                         timestamp=w["start"] + (o["timestamp"] - t0), type=o["type"],
                         photoFile=photo, name=name, description=desc))

    dist = sum(haversine((track[i]["lat"], track[i]["lon"]), (track[i + 1]["lat"], track[i + 1]["lon"]))
               for i in range(len(track) - 1))
    dur_s = (track[-1]["timestamp"] - track[0]["timestamp"]) / 1000.0
    mushrooms = sum(1 for o in objs if o["type"] == "MUSHROOM")
    walk = dict(originalId=idx, name=w["name"], startTime=track[0]["timestamp"],
                endTime=track[-1]["timestamp"] + 60_000, distanceMeters=dist,
                avgSpeed=dist / dur_s, startLat=track[0]["lat"], startLon=track[0]["lon"],
                endLat=track[-1]["lat"], endLon=track[-1]["lon"], mushroomCount=mushrooms)
    if w["description"]:
        walk["description"] = w["description"]

    d = f"{OUT}/walks/walk-{idx}"
    os.makedirs(f"{d}/photos", exist_ok=True)
    json.dump(walk, open(f"{d}/walk.json", "w"), ensure_ascii=False)
    json.dump(track, open(f"{d}/track.json", "w"), ensure_ascii=False)
    json.dump(objs, open(f"{d}/objects.json", "w"), ensure_ascii=False)
    for o in objs:
        if o["photoFile"]:
            src = f"photos_src/{os.path.basename(o['photoFile'])}"
            # Two of these are frames from the owner's real walk and came out of the camera with
            # GPS in EXIF. Nothing displays it, but the archive is committed — so the real
            # coordinates would be published inside the JPEG. Re-encode without any EXIF.
            im = Image.open(src)
            if im.getexif():
                clean = Image.new(im.mode, im.size)
                clean.putdata(list(im.getdata()))
                clean.save(f"{d}/{o['photoFile']}", "JPEG", quality=92, subsampling=0)
            else:
                shutil.copy(src, f"{d}/{o['photoFile']}")
    if not os.listdir(f"{d}/photos"):
        os.rmdir(f"{d}/photos")
    report.append((w["name"], round(dist), round(dur_s / 60), mushrooms, len(objs) - mushrooms))

json.dump(dict(schemaVersion=2, exportedAt=int(datetime.datetime.now().timestamp() * 1000),
               walkCount=len(WALKS)), open(f"{OUT}/manifest.json", "w"))

zpath = f"leshy-demo-{LANG}.zip"
if os.path.exists(zpath):
    os.remove(zpath)
# ZipReader in the app only understands STORED entries — a deflated archive is rejected outright
# as "not a Leshy one"
with zipfile.ZipFile(zpath, "w", zipfile.ZIP_STORED) as z:
    for root, _, files in os.walk(OUT):
        for f in files:
            p = os.path.join(root, f)
            z.write(p, os.path.relpath(p, OUT))

print(f"{'walk':32} {'m':>6} {'min':>5} {'finds':>6} {'places':>6}")
for nm, dm, mi, f, pl in report:
    print(f"{nm:32} {dm:6} {mi:5} {f:6} {pl:6}")
print("archive:", zpath, os.path.getsize(zpath) // 1024, "KB")

# sanity: how close the snapped marks really landed to the features they are named after
for o in json.load(open(f"{OUT}/walks/walk-1/objects.json")):
    if o["type"] == "MUSHROOM":
        continue
    p = xy(o["lat"], o["lon"])
    print(f"  {o['name']:24} lake {nearest(p, LAKE_SEGS)[1]:7.1f} m   stream {nearest(p, STREAM_SEGS)[1]:7.1f} m")
