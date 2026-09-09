"""Searches for a placement of the (already warped) main track around Naukajarvi such that it
   * never runs across the lake,
   * touches the lake shore at one point (a place mark goes there),
   * touches a stream at another (a second place mark goes there).
"""
import json, math
R = 6371000.0

osm = json.load(open('osm_local.json'))['elements']
def geom(e): return [(g['lat'], g['lon']) for g in e.get('geometry', [])]

lake = max((e for e in osm if e.get('tags', {}).get('natural') == 'water'),
           key=lambda e: max(g['lon'] for g in e['geometry']))   # the eastern one = Naukajarvi
lake_pts = geom(lake)
lc = (sum(p[0] for p in lake_pts)/len(lake_pts), sum(p[1] for p in lake_pts)/len(lake_pts))
streams = [geom(e) for e in osm if e.get('tags', {}).get('waterway') in ('stream', 'flowline')]
stream_pts = [p for s in streams for p in s]

def xy(lat, lon, lat0=lc[0], lon0=lc[1]):
    return (math.radians(lon-lon0)*R*math.cos(math.radians(lat0)), math.radians(lat-lat0)*R)
def geo(x, y, lat0=lc[0], lon0=lc[1]):
    return (lat0 + math.degrees(y/R), lon0 + math.degrees(x/(R*math.cos(math.radians(lat0)))))

lake_xy = [xy(*p) for p in lake_pts]
stream_xy = [xy(*p) for p in stream_pts]

def inside(pt, poly):
    x, y = pt; c = False; n = len(poly)
    for i in range(n):
        x1, y1 = poly[i]; x2, y2 = poly[(i+1) % n]
        if ((y1 > y) != (y2 > y)) and x < (x2-x1)*(y-y1)/(y2-y1)+x1:
            c = not c
    return c

def seg_d(p, a, b):
    px, py = p; ax, ay = a; bx, by = b
    dx, dy = bx-ax, by-ay
    if dx == dy == 0: return math.hypot(px-ax, py-ay)
    t = max(0, min(1, ((px-ax)*dx + (py-ay)*dy)/(dx*dx+dy*dy)))
    return math.hypot(px-ax-t*dx, py-ay-t*dy)

def shore_d(p):
    return min(seg_d(p, a, b) for a, b in LAKE_SEGS)
STREAM_SEGS = [(xy(*s[i]), xy(*s[i+1])) for s in streams for i in range(len(s)-1)]
LAKE_SEGS = [(lake_xy[i], lake_xy[(i+1) % len(lake_xy)]) for i in range(len(lake_xy))]

def stream_d(p):
    return min(seg_d(p, a, b) for a, b in STREAM_SEGS)

base = json.load(open('demo/walks/walk-1/track.json'))[::8]      # already warped, old centre
bx = [xy(p['lat'], p['lon']) for p in base]
cx = sum(p[0] for p in bx)/len(bx); cy = sum(p[1] for p in bx)/len(bx)
bx = [(x-cx, y-cy) for x, y in bx]

best = None
for adeg in range(0, 360, 10):
    a = math.radians(adeg); ca, sa = math.cos(a), math.sin(a)
    rot = [(x*ca - y*sa, x*sa + y*ca) for x, y in bx]
    for ox in range(-400, 1500, 75):
        for oy in range(-300, 1600, 75):
            pts = [(x+ox, y+oy) for x, y in rot]
            if any(inside(p, lake_xy) for p in pts):
                continue
            ds = min(shore_d(p) for p in pts)
            if not (10 < ds < 45):
                continue
            dw = min(stream_d(p) for p in pts)
            if dw > 30:
                continue
            score = abs(ds-25) + abs(dw-12)
            if best is None or score < best[0]:
                best = (score, adeg, ox, oy, round(ds), round(dw))
print('best:', best)
if best:
    _, adeg, ox, oy, ds, dw = best
    a = math.radians(adeg); ca, sa = math.cos(a), math.sin(a)
    pts = [((x*ca - y*sa)+ox, (x*sa + y*ca)+oy) for x, y in bx]
    ll = [geo(*p) for p in pts]
    print('centre', geo(ox, oy))
    print('lat', min(p[0] for p in ll), max(p[0] for p in ll))
    print('lon', min(p[1] for p in ll), max(p[1] for p in ll))
    json.dump(dict(angle=adeg, ox=ox, oy=oy, lake_centre=lc), open('placement.json', 'w'))
