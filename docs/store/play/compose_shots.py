#!/usr/bin/env python3
"""Turns raw 1080x2400 emulator captures into store-ready screenshots for Play and the App Store.

Play rejects anything longer than 2:1, which every modern phone capture is, so each shot is
cropped (status bar and gesture bar off) and set on a canvas with a one-line caption in the app's
own palette.  Raw captures are never modified.

The same composition serves both stores — the interface is Compose Multiplatform and identical on
iOS, and the crop leaves no platform chrome in the frame.  Only the canvas differs, so the layout
is described once for Play (1080x1920) and scaled by W/1080 for anything else:

    python3 compose_shots.py                      # en, play preset, shots/ -> play/phone-en/
    python3 compose_shots.py ru                   # ru, play preset
    python3 compose_shots.py en --preset appstore # 6.9" iPhone, 1320x2868
    python3 compose_shots.py en --raw DIR --out DIR --font PATH

The App Store preset is the 6.9" iPhone set (1320x2868), the only one App Store Connect requires —
the rest are scaled by Apple.  The shot is inscribed into 1100px of a 1320px canvas, i.e. the
1080px source is upscaled by about 2%: no visible degradation, no reshooting needed.

Captions per locale live in captions.json, in the same order as SCREENS.
"""
import argparse, json, os, sys
from PIL import Image, ImageDraw, ImageFont

HERE = os.path.dirname(os.path.abspath(__file__))

# The layout below is written for the Play canvas; every other preset is this one times W/1080.
BASE_W, BASE_H = 1080, 1920
PRESETS = {
    "play": (1080, 1920),
    "appstore": (1320, 2868),   # 6.9" iPhone, App Store Connect
}

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

# The variable NotoSans of the original run lives in the owner's working directory, not in the
# repository (a font binary is not project source).  Anything Noto-like with Cyrillic will do;
# --font or LESHY_CAPTION_FONT override the search.
FONT_CANDIDATES = [
    "fonts/NotoSans.ttf",
    "fonts/NotoSans-Bold.ttf",
    os.path.expanduser("~/Library/Fonts/NotoSans-Bold.ttf"),
    "/System/Library/Fonts/Supplemental/Arial Bold.ttf",
]


def find_font(explicit=None):
    for path in ([explicit] if explicit else []) + [os.environ.get("LESHY_CAPTION_FONT")] + FONT_CANDIDATES:
        if path and os.path.exists(path):
            return path
    sys.exit("no caption font found — pass --font PATH (Noto Sans, Cyrillic-capable)")


def load_font(path, size):
    font = ImageFont.truetype(path, size)
    try:                              # a variable NotoSans needs its axes pinned; a static Bold does not
        font.set_variation_by_axes([100, 700])
    except Exception:
        pass
    return font


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


def compose(src, caption, dst, size, font_path):
    W, H = size
    s = W / BASE_W                    # everything below is the Play layout times this
    px = lambda v: round(v * s)

    shot = Image.open(src).convert("RGB").crop((0, CROP_TOP, 1080, CROP_BOTTOM))
    canvas = gradient(W, H, BG_TOP, BG_BOTTOM)
    draw = ImageDraw.Draw(canvas)

    lines = caption.split("\n")
    font = load_font(font_path, px(62))
    # a caption that grew in translation gets shrunk rather than clipped
    while max(draw.textbbox((0, 0), l, font=font)[2] for l in lines) > W - px(100) and font.size > px(40):
        font = load_font(font_path, font.size - 2)
    line_h = round(font.size * 1.26)
    y = px(96)
    for line in lines:
        w = draw.textbbox((0, 0), line, font=font)[2]
        draw.text(((W - w) // 2, y), line, font=font, fill=INK)
        y += line_h

    top = px(96) + len(lines) * line_h + px(54)
    avail_h = H - top - px(40)
    scale = min(px(900) / shot.width, avail_h / shot.height)
    shot = shot.resize((round(shot.width * scale), round(shot.height * scale)), Image.LANCZOS)
    shot = rounded(shot, px(34))

    x = (W - shot.width) // 2
    shadow = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    ImageDraw.Draw(shadow).rounded_rectangle(
        [x + px(6), top + px(10), x + shot.width + px(6), top + shot.height + px(10)],
        px(34), fill=(0, 0, 0, 46))
    canvas = Image.alpha_composite(canvas.convert("RGBA"), shadow)
    canvas.paste(shot, (x, top), shot)
    canvas.convert("RGB").save(dst, "PNG")
    return dst


def main():
    p = argparse.ArgumentParser()
    p.add_argument("lang", nargs="?", default="en")
    p.add_argument("--preset", choices=sorted(PRESETS), default="play")
    p.add_argument("--raw", default=None, help="directory with the raw captures")
    p.add_argument("--out", default=None, help="output directory")
    p.add_argument("--font", default=None, help="caption font (Noto Sans or similar)")
    args = p.parse_args()

    lang = args.lang
    raw = args.raw or ("shots" if lang == "en" else f"shots-{lang}")
    out = args.out or (f"play/phone-{lang}" if args.preset == "play" else f"{args.preset}/phone-{lang}")
    size = PRESETS[args.preset]
    font_path = find_font(args.font)

    captions = json.load(open(os.path.join(HERE, "captions.json"), encoding="utf-8"))[lang]
    os.makedirs(out, exist_ok=True)
    print(f"{args.preset}: {size[0]}x{size[1]}, font {font_path}")
    for i, (name, caption) in enumerate(zip(SCREENS, captions), start=1):
        src = os.path.join(raw, name)
        if not os.path.exists(src):
            print("missing", src)
            continue
        dst = os.path.join(out, f"{i:02d}_{name}")
        compose(src, caption, dst, size, font_path)
        im = Image.open(dst)
        # Play refuses anything longer than 2:1; the App Store wants the exact 6.9" size instead.
        ok = (max(im.size) <= 2 * min(im.size)) if args.preset == "play" else (im.size == size)
        print(dst, im.size, "ok" if ok else "SIZE REJECTED")


if __name__ == "__main__":
    main()
