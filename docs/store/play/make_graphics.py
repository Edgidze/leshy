#!/usr/bin/env python3
"""Play Store graphics: the 512x512 icon and the 1024x500 feature graphic.

Icon: the project's own `leshy_icon.png`, resized and given the alpha channel Play asks for.
Feature graphic: the owner's own photo of the forest lake — the same one that is the "Forest pond"
mark in the demo data — with the app name over a darkened left half.
"""
from PIL import Image, ImageDraw, ImageFilter, ImageFont

REPO = "/Users/ffradkin/IdeaProjects/leshy"
FONT = "fonts/NotoSans.ttf"
INK = (245, 242, 233)


def var(font, weight, width=100):
    try:
        font.set_variation_by_axes([width, weight])
    except Exception:
        pass
    return font


# ---------------------------------------------------------------- icon 512x512
icon = Image.open(f"{REPO}/leshy_icon.png").convert("RGB").resize((512, 512), Image.LANCZOS)
icon = icon.convert("RGBA")          # Play wants a 32-bit PNG; fully opaque alpha is fine
icon.save("play/icon-512.png", "PNG")

# ---------------------------------------------------------------- feature graphic 1024x500
W, H = 1024, 500
photo = Image.open("photos_src/pond.jpg").convert("RGB")
# portrait source: take the widest band that still holds the treeline and the water
scale = W / photo.width
photo = photo.resize((W, round(photo.height * scale)), Image.LANCZOS)
top = round(photo.height * 0.30)
banner = photo.crop((0, top, W, top + H))

# darken the left half so the wordmark sits on something readable
shade = Image.new("L", (W, H), 0)
d = ImageDraw.Draw(shade)
for x in range(W):
    t = max(0.0, min(1.0, (620 - x) / 620))
    d.line([(x, 0), (x, H)], fill=int(190 * t))
shade = shade.filter(ImageFilter.GaussianBlur(24))
dark = Image.new("RGB", (W, H), (10, 24, 14))
banner = Image.composite(dark, banner, shade.point(lambda v: v))
banner = Image.blend(banner, Image.composite(dark, banner, shade), 0.0)

draw = ImageDraw.Draw(banner)
title = var(ImageFont.truetype(FONT, 66), 700)
sub = var(ImageFont.truetype(FONT, 30), 400)
draw.text((64, 176), "Mushroom Map", font=title, fill=INK)
draw.text((64, 250), "from Leshy", font=title, fill=INK)
draw.text((66, 344), "Record the walk. Log every find.", font=sub, fill=(214, 226, 210))
draw.text((66, 384), "Come back to the spot.", font=sub, fill=(214, 226, 210))

banner.convert("RGB").save("play/feature-graphic-1024x500.png", "PNG")

for p in ("play/icon-512.png", "play/feature-graphic-1024x500.png"):
    im = Image.open(p)
    print(p, im.size, im.mode)
