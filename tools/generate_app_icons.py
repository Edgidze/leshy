#!/usr/bin/env python3
"""Генерация иконок приложения из leshy_icon.png.

Исходник — квадрат со скруглёнными углами: чёрные углы, тёмно-зелёный фон и
золотой контур-«капля» (метка места) с лицом Лешего внутри. Задача — вывести
контур-каплю как можно ближе к краям иконки, но так, чтобы он нигде не
обрезался: ни краем картинки, ни маской адаптивной иконки Android, ни
скруглением iOS.

Запуск:  python3 tools/generate_app_icons.py
"""
from __future__ import annotations

import math
import os
from collections import deque

from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, "leshy_icon.png")
ANDROID_RES = os.path.join(ROOT, "androidApp", "src", "main", "res")
IOS_ICONSET = os.path.join(
    ROOT, "iosApp", "iosApp", "Assets.xcassets", "AppIcon.appiconset"
)

# Плотности Android: (каталог, сторона foreground 108dp, сторона legacy-иконки 48dp)
DENSITIES = [
    ("mipmap-mdpi", 108, 48),
    ("mipmap-hdpi", 162, 72),
    ("mipmap-xhdpi", 216, 96),
    ("mipmap-xxhdpi", 324, 144),
    ("mipmap-xxxhdpi", 432, 192),
]

# Адаптивная иконка: холст 108dp, гарантированно видимая зона — круг d=72dp.
# Радиус описанной окружности капли держим чуть меньше 36dp, чтобы контур не
# задевала ни одна из системных масок.
ADAPTIVE_SAFE_RADIUS_DP = 34.5
ADAPTIVE_CANVAS_DP = 108.0
# iOS/legacy: доля высоты капли от стороны иконки.
FULL_BLEED_HEIGHT_RATIO = 0.92
ROUND_SAFE_RATIO = 0.96  # доля радиуса круглой legacy-иконки


def is_black(p) -> bool:
    return p[0] + p[1] + p[2] <= 30


def is_background_green(p) -> bool:
    """Тёмно-зелёный фон вокруг капли (с запасом на виньетку и шум текстуры)."""
    r, g, b = p[0], p[1], p[2]
    return r <= 55 and b <= 42 and g <= 78 and g >= r - 6


def flood_from_border(px, w, h, predicate):
    """Множество пикселей, достижимых от границы картинки по predicate."""
    seen = bytearray(w * h)
    q = deque()
    for x in range(w):
        for y in (0, h - 1):
            if predicate(px[x, y]) and not seen[y * w + x]:
                seen[y * w + x] = 1
                q.append((x, y))
    for y in range(h):
        for x in (0, w - 1):
            if predicate(px[x, y]) and not seen[y * w + x]:
                seen[y * w + x] = 1
                q.append((x, y))
    while q:
        x, y = q.popleft()
        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            nx, ny = x + dx, y + dy
            if 0 <= nx < w and 0 <= ny < h and not seen[ny * w + nx]:
                if predicate(px[nx, ny]):
                    seen[ny * w + nx] = 1
                    q.append((nx, ny))
    return seen


def dilate(mask, w, h, radius):
    out = bytearray(mask)
    for _ in range(radius):
        cur = bytearray(out)
        for y in range(h):
            row = y * w
            for x in range(w):
                if cur[row + x]:
                    continue
                if (
                    (x > 0 and cur[row + x - 1])
                    or (x < w - 1 and cur[row + x + 1])
                    or (y > 0 and cur[row - w + x])
                    or (y < h - 1 and cur[row + w + x])
                ):
                    out[row + x] = 1
    return out


def background_color(im) -> tuple[int, int, int]:
    """Медианный цвет фоновой зелени вдоль краёв (без чёрных углов)."""
    px = im.load()
    w, h = im.size
    samples = []
    for t in range(0, w, 7):
        for x, y in ((t, h // 2), (w // 2, t), (t, int(h * 0.22)), (int(w * 0.22), t)):
            p = px[x, y]
            if is_background_green(p) and not is_black(p):
                samples.append(p)
    samples.sort(key=lambda p: p[0] + p[1] + p[2])
    return samples[len(samples) // 2]


def main() -> None:
    src = Image.open(SRC).convert("RGB")
    w, h = src.size
    px = src.load()

    bg = background_color(src)
    print(f"фон: {bg} = #{bg[0]:02X}{bg[1]:02X}{bg[2]:02X}")

    # 1. Убираем чёрные углы скруглённой рамки исходника — заливаем фоном.
    black = dilate(flood_from_border(px, w, h, is_black), w, h, 3)
    flat = src.copy()
    fpx = flat.load()
    for y in range(h):
        row = y * w
        for x in range(w):
            if black[row + x]:
                fpx[x, y] = bg

    # 2. Вырезаем каплю: фон, достижимый от границы, становится прозрачным.
    #    Зелень внутри капли контуром отсечена и остаётся непрозрачной.
    outside = flood_from_border(fpx, w, h, is_background_green)
    cut = Image.new("RGBA", (w, h))
    cpx = cut.load()
    for y in range(h):
        row = y * w
        for x in range(w):
            if outside[row + x]:
                cpx[x, y] = (0, 0, 0, 0)
            else:
                r, g, b = fpx[x, y]
                cpx[x, y] = (r, g, b, 255)

    bbox = cut.getbbox()
    drop = cut.crop(bbox)
    dw, dh = drop.size
    print(f"капля: bbox={bbox} размер={dw}x{dh}")

    # Радиус описанной окружности относительно центра bbox — по нему считаем
    # безопасный масштаб для круглых масок.
    dpx = drop.load()
    cx, cy = (dw - 1) / 2.0, (dh - 1) / 2.0
    r2 = 0.0
    for y in range(dh):
        for x in range(dw):
            if dpx[x, y][3]:
                d = (x - cx) ** 2 + (y - cy) ** 2
                if d > r2:
                    r2 = d
    circ_r = math.sqrt(r2)
    print(f"радиус описанной окружности: {circ_r:.1f}px ({circ_r / dh:.3f} высоты)")

    def render(size: int, drop_h: float, opaque: bool, round_mask: bool = False):
        """Капля высотой drop_h px по центру холста size×size."""
        scale = drop_h / dh
        tw, th = max(1, round(dw * scale)), max(1, round(dh * scale))
        layer = drop.resize((tw, th), Image.LANCZOS)
        if opaque:
            canvas = Image.new("RGBA", (size, size), bg + (255,))
        else:
            canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        canvas.alpha_composite(layer, ((size - tw) // 2, (size - th) // 2))
        if round_mask:
            mask = Image.new("L", (size * 4, size * 4), 0)
            from PIL import ImageDraw

            ImageDraw.Draw(mask).ellipse((0, 0, size * 4 - 1, size * 4 - 1), fill=255)
            canvas.putalpha(mask.resize((size, size), Image.LANCZOS))
        return canvas

    # 3. Android: адаптивный foreground — капля вписана в круг d=72dp.
    for folder, fg_size, legacy_size in DENSITIES:
        dp = fg_size / ADAPTIVE_CANVAS_DP
        drop_h = dh * (ADAPTIVE_SAFE_RADIUS_DP * dp / circ_r)
        out = os.path.join(ANDROID_RES, folder)
        os.makedirs(out, exist_ok=True)
        render(fg_size, drop_h, opaque=False).save(
            os.path.join(out, "ic_launcher_foreground.png")
        )
        # legacy (API 24–25): квадратная и круглая иконки целиком
        render(
            legacy_size, legacy_size * FULL_BLEED_HEIGHT_RATIO, opaque=True
        ).convert("RGB").save(os.path.join(out, "ic_launcher.png"))
        round_h = dh * (legacy_size / 2 * ROUND_SAFE_RATIO / circ_r)
        render(legacy_size, round_h, opaque=True, round_mask=True).save(
            os.path.join(out, "ic_launcher_round.png")
        )
        print(f"  {folder}: foreground {fg_size}px, legacy {legacy_size}px")

    # 4. Android: фоновый слой адаптивной иконки — сплошная зелень исходника.
    with open(os.path.join(ANDROID_RES, "drawable", "ic_launcher_background.xml"), "w") as f:
        f.write(
            '<?xml version="1.0" encoding="utf-8"?>\n'
            '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
            '        android:width="108dp"\n'
            '        android:height="108dp"\n'
            '        android:viewportWidth="108"\n'
            '        android:viewportHeight="108">\n'
            "    <path\n"
            f'            android:fillColor="#{bg[0]:02X}{bg[1]:02X}{bg[2]:02X}"\n'
            '            android:pathData="M0,0h108v108h-108z"/>\n'
            "</vector>\n"
        )

    # 5. iOS: единственный 1024×1024 без альфы, скругление накладывает система.
    render(1024, 1024 * FULL_BLEED_HEIGHT_RATIO, opaque=True).convert("RGB").save(
        os.path.join(IOS_ICONSET, "app-icon-1024.png")
    )
    print("  iOS: app-icon-1024.png")


if __name__ == "__main__":
    main()
