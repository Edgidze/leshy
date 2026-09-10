#!/usr/bin/env python3
"""Play Store graphics: the 512x512 icon and a 1024x500 feature graphic per locale.

Icon: the project's own `leshy_icon.png`, resized and given the alpha channel Play asks for.
It carries no text, so one file serves every locale.

Feature graphic: the owner's own photo of the forest lake — the same one that is the "Forest pond"
mark in the demo data — with the app name over a darkened left half. This one *does* carry text,
and Play takes a separate upload per locale, so it is written once per language in WORDMARKS.

    python3 make_graphics.py            # icon + every locale's banner
"""
from PIL import Image, ImageDraw, ImageFilter, ImageFont

REPO = "/Users/ffradkin/IdeaProjects/leshy"
FONT = "fonts/NotoSans.ttf"
INK = (245, 242, 233)
SUBINK = (214, 226, 210)

# name split over two lines, then the two subtitle lines
WORDMARKS = {
    "en": (["Mushroom Map", "from Leshy"],
           ["Record the walk. Log every find.", "Come back to the best spots."]),
    "ru": (["Грибная карта", "от Лешего"],
           ["Записывайте прогулку и находки.", "Возвращайтесь на грибные места."]),
}


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
print("play/icon-512.png", icon.size, icon.mode)

# ---------------------------------------------------------------- feature graphic 1024x500
W, H = 1024, 500
photo = Image.open("photos_src/pond.jpg").convert("RGB")
scale = W / photo.width
photo = photo.resize((W, round(photo.height * scale)), Image.LANCZOS)
top = round(photo.height * 0.30)     # the band that still holds the treeline and the water
band = photo.crop((0, top, W, top + H))

# darken the left half so the wordmark sits on something readable
shade = Image.new("L", (W, H), 0)
d = ImageDraw.Draw(shade)
for x in range(W):
    t = max(0.0, min(1.0, (700 - x) / 700))
    d.line([(x, 0), (x, H)], fill=int(205 * t))
shade = shade.filter(ImageFilter.GaussianBlur(24))
dark = Image.new("RGB", (W, H), (10, 24, 14))

for lang, (title_lines, sub_lines) in WORDMARKS.items():
    banner = Image.composite(dark, band, shade)
    draw = ImageDraw.Draw(banner)

    title = var(ImageFont.truetype(FONT, 66), 700)
    # Russian is the longer wordmark; shrink rather than run into the photo
    while max(draw.textbbox((0, 0), l, font=title)[2] for l in title_lines) > 520:
        title = var(ImageFont.truetype(FONT, title.size - 2), 700)
    sub = var(ImageFont.truetype(FONT, 30), 400)
    while max(draw.textbbox((0, 0), l, font=sub)[2] for l in sub_lines) > 540:
        sub = var(ImageFont.truetype(FONT, sub.size - 1), 400)

    # The photo is bright where the water is, and the Russian lines run further right than the
    # English ones. A blurred dark copy under every line keeps both readable without darkening
    # the whole picture.
    glow = Image.new("L", (W, H), 0)
    gdraw = ImageDraw.Draw(glow)
    layout = []
    y = 176
    for line in title_lines:
        layout.append((64, y, line, title))
        y += round(title.size * 1.12)
    y += 30
    for line in sub_lines:
        layout.append((66, y, line, sub))
        y += round(sub.size * 1.33)
    for x, ly, line, f in layout:
        gdraw.text((x, ly), line, font=f, fill=190)
    banner = Image.composite(dark, banner, glow.filter(ImageFilter.GaussianBlur(9)))
    draw = ImageDraw.Draw(banner)
    for x, ly, line, f in layout:
        draw.text((x, ly), line, font=f, fill=INK if f is title else SUBINK)

    out = f"play/feature-graphic-1024x500-{lang}.png"
    banner.convert("RGB").save(out, "PNG")
    im = Image.open(out)
    print(out, im.size, im.mode)
