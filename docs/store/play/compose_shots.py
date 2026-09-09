#!/usr/bin/env python3
"""Turns raw 1080x2400 emulator captures into Play-ready 1080x1920 store screenshots.

Play rejects anything longer than 2:1, which every modern phone capture is, so each shot is
cropped (status bar and gesture bar off) and set on a canvas with a one-line caption in the app's
own palette.  Raw captures in `shots/` are never modified.
"""
import os, json
from PIL import Image, ImageDraw, ImageFont

RAW = "shots"
OUT = "play/phone"
FONT = "fonts/NotoSans.ttf"

W, H = 1080, 1920
BG_TOP = (244, 241, 232)      # app background  #F4F1E8
BG_BOTTOM = (226, 221, 206)   # surfaceContainerHigh
INK = (27, 67, 50)            # LeshyGreen #1B4332
SUB = (76, 71, 57)            # onSurfaceVariant

# crop of the raw capture: drop the status bar (top) and the gesture bar (bottom)
CROP_TOP, CROP_BOTTOM = 70, 2330

SHOTS = [
    ("16_record.png",       "Tap the tile — the find is saved\nwith its place and type"),
    ("01_archive.png",      "Every walk kept on its own"),
    ("02_walk_detail.png",  "The whole walk: route, finds,\nplaces, statistics"),
    ("12_finds_map.png",    "All of your finds on one map"),
    ("09_map_stats.png",    "How much, where, and when"),
    ("07_place_card.png",   "Mark a place — with a photo,\na note and its coordinates"),
    ("14_preload.png",      "Download the map while you\nstill have internet"),
    ("15_species.png",      "408 species, 55 collections\nby country"),
]


def gradient(w, h, top, bottom):
    img = Image.new("RGB", (1, h))
    d = ImageDraw.Draw(img)
    for y in range(h):
        t = y / (h - 1)
        d.point((0, y), tuple(round(a + (b - a) * t) for a, b in zip(top, bottom)))
    return img.resize((w, h))


def rounded(img, r):
    mask = Image.new("L", img.size, 0)
    ImageDraw.Draw(mask).rounded_rectangle([0, 0, img.size[0] - 1, img.size[1] - 1], r, fill=255)
    out = img.convert("RGBA")
    out.putalpha(mask)
    return out


def compose(src, caption, dst):
    shot = Image.open(src).convert("RGB").crop((0, CROP_TOP, 1080, CROP_BOTTOM))
    canvas = gradient(W, H, BG_TOP, BG_BOTTOM)
    draw = ImageDraw.Draw(canvas)

    lines = caption.split("\n")
    font = ImageFont.truetype(FONT, 62)
    try:                              # NotoSans ships as a variable font
        font.set_variation_by_axes([100, 700])
    except Exception:
        pass
    line_h = 78
    y = 96
    for line in lines:
        w = draw.textbbox((0, 0), line, font=font)[2]
        draw.text(((W - w) // 2, y), line, font=font, fill=INK)
        y += line_h

    top = 96 + len(lines) * line_h + 54
    avail_h = H - top - 40
    scale = min(900 / shot.width, avail_h / shot.height)
    shot = shot.resize((round(shot.width * scale), round(shot.height * scale)), Image.LANCZOS)
    shot = rounded(shot, 34)

    x = (W - shot.width) // 2
    shadow = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    ImageDraw.Draw(shadow).rounded_rectangle(
        [x + 6, top + 10, x + shot.width + 6, top + shot.height + 10], 34, fill=(0, 0, 0, 46))
    canvas = Image.alpha_composite(canvas.convert("RGBA"), shadow)
    canvas.paste(shot, (x, top), shot)
    canvas.convert("RGB").save(dst, "PNG")
    return dst


os.makedirs(OUT, exist_ok=True)
made = []
for i, (name, caption) in enumerate(SHOTS, start=1):
    src = os.path.join(RAW, name)
    if not os.path.exists(src):
        print("missing", src)
        continue
    dst = os.path.join(OUT, f"{i:02d}_{name}")
    compose(src, caption, dst)
    made.append(dst)

for m in made:
    im = Image.open(m)
    print(m, im.size, "ratio ok" if max(im.size) <= 2 * min(im.size) else "RATIO REJECTED")
