#!/usr/bin/env python3
"""Turns raw 1080x2400 emulator captures into Play-ready 1080x1920 store screenshots.

Play rejects anything longer than 2:1, which every modern phone capture is, so each shot is
cropped (status bar and gesture bar off) and set on a canvas with a one-line caption in the app's
own palette.  Raw captures are never modified.

    python3 compose_shots.py            # en, from shots/    -> play/phone-en/
    python3 compose_shots.py ru         # ru, from shots-ru/ -> play/phone-ru/

Captions per locale live in captions.json, in the same order as SCREENS.
"""
import json, os, sys
from PIL import Image, ImageDraw, ImageFont

LANG = sys.argv[1] if len(sys.argv) > 1 else "en"
RAW = "shots" if LANG == "en" else f"shots-{LANG}"
OUT = f"play/phone-{LANG}"
FONT = "fonts/NotoSans.ttf"

W, H = 1080, 1920
# Light green, picked off the app's own tertiaryContainer (#DCE6C0) — the same family as the
# forest fill the map is drawn in, so the frame reads as part of the picture rather than a mat.
BG_TOP = (237, 243, 227)
BG_BOTTOM = (216, 228, 198)
INK = (27, 67, 50)            # LeshyGreen #1B4332

# crop of the raw capture: drop the status bar (top) and the gesture bar (bottom)
CROP_TOP, CROP_BOTTOM = 70, 2330

# order fixed across locales — captions.json is indexed by position
SCREENS = ["16_record.png", "02_walk_detail.png", "03b_finds_chart.png", "12_finds_map.png",
           "09_map_stats.png", "07_place_card.png", "14_preload.png", "15_species.png"]

CAPTIONS = json.load(open("captions.json", encoding="utf-8"))[LANG]


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
    # a caption that grew in translation gets shrunk rather than clipped
    while max(draw.textbbox((0, 0), l, font=font)[2] for l in lines) > W - 100 and font.size > 40:
        font = ImageFont.truetype(FONT, font.size - 2)
        try:
            font.set_variation_by_axes([100, 700])
        except Exception:
            pass
    line_h = round(font.size * 1.26)
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
for i, (name, caption) in enumerate(zip(SCREENS, CAPTIONS), start=1):
    src = os.path.join(RAW, name)
    if not os.path.exists(src):
        print("missing", src)
        continue
    dst = os.path.join(OUT, f"{i:02d}_{name}")
    compose(src, caption, dst)
    im = Image.open(dst)
    print(dst, im.size, "ratio ok" if max(im.size) <= 2 * min(im.size) else "RATIO REJECTED")
