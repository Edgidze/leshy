"""Picks a rotation+offset for the five secondary walks: inside the forest, off the roads,
never across the lake, and spread around the main walk."""
import json, math, random
exec(open('make_demo_archive.py').read().split('# ---------------------------------------------------------------- source data')[0]
     .replace('random.seed(20260908)', 'random.seed(20260908)'))

ROAD_KINDS = ('motorway', 'trunk', 'primary', 'secondary', 'tertiary', 'unclassified',
              'residential', 'service')
road_segs, build_pts = [], []
for e in osm:
    t = e.get('tags', {})
    g = geom(e)
    if t.get('highway') in ROAD_KINDS and len(g) > 1:
        road_segs += [(xy(*g[i]), xy(*g[i+1])) for i in range(len(g)-1)]
    if 'building' in t:
        build_pts += [xy(*p) for p in g] or ([xy(e['lat'], e['lon'])] if 'lat' in e else [])

base_track = json.load(open('walk/walks/walk-1/track.json'))
src_lat = sum(p['lat'] for p in base_track)/len(base_track)
src_lon = sum(p['lon'] for p in base_track)/len(base_track)

SPECS = [  # (name, seg, angle, scale, mirror, preferred centroid)
    ('Early morning, west side',   (150, 400),  -72, 0.92, False, (-420, 120), False),
    ('After the rain',             (330, 620),  128, 1.02, True,  (520, -80), False),
    ('First walk of the season',   (120, 400), -155, 0.88, False, (-160, -620), False),
    ('Along the cutting',          (430, 700),   20, 0.95, True,  (-560, 560), False),
    ('Late October, mostly empty', (520, 660),  -40, 0.90, False, (480, -560), False),
]

for name, (a, b), ang, sc, mi, pref, need_stream in SPECS:
    random.seed(20260908)
    warp = Warp(src_lat, src_lon, ang, sc, mi)
    pts = [warp(p['lat'], p['lon']) for p in base_track[a:b:12]]
    best = None
    for post in range(0, 360, 30):
        for dx in range(-450, 500, 100):
            for dy in range(-450, 500, 100):
                c = (pref[0]+dx, pref[1]+dy)
                q = rigid(pts, post, c)
                if any(inside_lake(p) for p in q):
                    continue
                dr = min(min(seg_point(p, s, e)[1] for s, e in road_segs) for p in q)
                db = min(min(math.hypot(p[0]-t[0], p[1]-t[1]) for t in build_pts) for p in q)
                dl = min(nearest(p, LAKE_SEGS)[1] for p in q)
                if dr < 80 or db < 260 or dl < 25:
                    continue
                if need_stream and min(nearest(p, STREAM_SEGS)[1] for p in q) > 45:
                    continue
                score = min(dr, 250) + min(db, 700)/4 - math.hypot(dx, dy)/2
                if best is None or score > best[0]:
                    best = (score, post, c, round(dr), round(db), round(dl))
    print(f"{name:30} post={best[1]:4} centre={best[2]}  road {best[3]} m, building {best[4]} m, lake {best[5]} m")
