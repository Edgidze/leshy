#!/usr/bin/env python3
"""Иконка приложения российской редакции — из `gribnye_icon.png`.

Исходник владельца: квадрат со скруглёнными углами — алая деревянная рама, белый мат, сцена с
двумя подосиновиками, вокруг белое поле.

Почему отдельный скрипт, а не `generate_app_icons.py`: тот разбирает мировую иконку по цветам
(«чёрные углы, тёмно-зелёный фон, золотая капля») и к этому исходнику неприменим ни одной
строкой.

## Что здесь делается и почему именно так

**Раскладка только под Android.** iOS-вариант этим скриптом не готовится: Apple накладывает
маску сама, и ему нужна другая композиция — рама до краёв квадрата, без альфы (`design.md`,
раздел 14). Второго таргета в `iosApp` сейчас нет, делать эту композицию вслепую нечего.

**Рама уезжает внутрь безопасной зоны, а не масштабируется 1:1.** Адаптивная иконка Android —
холст 108dp, гарантированно видимая область 72dp, и ни одна системная маска не залезает внутрь
круга d=66dp. Рама, стоящая по краю квадрата, под круглой маской ушла бы целиком — поэтому
композиция считается так, чтобы САМАЯ ДАЛЬНЯЯ точка содержимого попадала ровно на радиус 33dp.

**«Самая дальняя точка» меряется, а не выводится из стороны квадрата.** Углы рамы скруглены,
и её описанная окружность заметно меньше описанной окружности квадрата той же стороны (замер по
исходнику: 695 против 764 точек, то есть 1.28 полустороны вместо 1.414). Вписывание по стороне
дало бы иконку на 10% мельче необходимого без всякой выгоды.

**Альфа получается из маски, а маска — из заливки фона, найденной от границы.** Порог по яркости
без связности съел бы белый мат внутри рамы: он такой же белый, как поле снаружи. Маска
строится в полном разрешении бинарной и уменьшается вместе с картинкой (LANCZOS), поэтому край
приходит сглаженным — отдельное размытие не нужно и только смазало бы раму.

Запуск:  python3 tools/generate_russia_app_icons.py
"""
from __future__ import annotations

import math
import os
from collections import deque

from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, "gribnye_icon.png")
RES = os.path.join(ROOT, "androidApp", "src", "russia", "res")

# (каталог, сторона foreground 108dp, сторона legacy-иконки 48dp)
DENSITIES = [
    ("mipmap-mdpi", 108, 48),
    ("mipmap-hdpi", 162, 72),
    ("mipmap-xhdpi", 216, 96),
    ("mipmap-xxhdpi", 324, 144),
    ("mipmap-xxxhdpi", 432, 192),
]

ADAPTIVE_CANVAS_DP = 108.0
# Радиус круга, внутрь которого не залезает ни одна системная маска.
ADAPTIVE_SAFE_RADIUS_DP = 33.0
# Legacy-иконка (API 24–25, адаптивных там нет): маски системы тоже нет, поэтому содержимому
# можно отдать почти весь квадрат. У круглого варианта — вписывание в окружность.
LEGACY_FILL_RATIO = 0.92
LEGACY_ROUND_RADIUS_RATIO = 0.46


def is_field(p) -> bool:
    """Белое поле вокруг рамы. Порог с запасом: в исходнике оно 253, а не 255."""
    r, g, b = p[0], p[1], p[2]
    return min(r, g, b) >= 240 and (max(r, g, b) - min(r, g, b)) <= 12


def field_mask(im: Image.Image):
    """Пиксели поля, ДОСТИЖИМЫЕ ОТ ГРАНИЦЫ. Белый мат внутри рамы сюда не попадает."""
    w, h = im.size
    px = im.load()
    seen = bytearray(w * h)
    q = deque()

    def push(x, y):
        if not seen[y * w + x] and is_field(px[x, y]):
            seen[y * w + x] = 1
            q.append((x, y))

    for x in range(w):
        push(x, 0)
        push(x, h - 1)
    for y in range(h):
        push(0, y)
        push(w - 1, y)
    while q:
        x, y = q.popleft()
        for nx, ny in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
            if 0 <= nx < w and 0 <= ny < h:
                push(nx, ny)
    return seen


def content_layer(im: Image.Image):
    """RGBA с содержимым (рама + сцена) и его геометрия: центр и описанный радиус."""
    w, h = im.size
    seen = field_mask(im)
    alpha = Image.new("L", (w, h), 0)
    ap = alpha.load()
    minx, miny, maxx, maxy = w, h, -1, -1
    for y in range(h):
        row = y * w
        for x in range(w):
            if not seen[row + x]:
                ap[x, y] = 255
                minx = min(minx, x)
                maxx = max(maxx, x)
                miny = min(miny, y)
                maxy = max(maxy, y)
    cx, cy = (minx + maxx) / 2.0, (miny + maxy) / 2.0
    radius = 0.0
    for y in range(miny, maxy + 1):
        row = y * w
        for x in range(minx, maxx + 1):
            if not seen[row + x]:
                radius = max(radius, math.hypot(x - cx, y - cy))

    # Квадратная обрезка вокруг измеренного центра со стороной 2*radius: дальше всё считается
    # от неё, и центр содержимого гарантированно совпадает с центром холста.
    side = int(math.ceil(radius * 2))
    left, top = int(round(cx - side / 2)), int(round(cy - side / 2))
    rgba = im.convert("RGB").copy()
    rgba.putalpha(alpha)
    crop = Image.new("RGBA", (side, side), (0, 0, 0, 0))
    crop.paste(rgba.crop((left, top, left + side, top + side)), (0, 0))
    return crop, radius


def field_color(im: Image.Image):
    w, h = im.size
    px = im.load()
    acc = [0, 0, 0]
    n = 0
    for x in range(0, w, 7):
        for y in (2, h - 3):
            p = px[x, y]
            acc[0] += p[0]
            acc[1] += p[1]
            acc[2] += p[2]
            n += 1
    return tuple(round(c / n) for c in acc)


def fitted(crop: Image.Image, canvas_px: int, content_px: float) -> Image.Image:
    """Содержимое, вписанное в холст так, что его описанная окружность = content_px/2."""
    size = max(1, int(round(content_px)))
    scaled = crop.resize((size, size), Image.LANCZOS)
    out = Image.new("RGBA", (canvas_px, canvas_px), (0, 0, 0, 0))
    off = (canvas_px - size) // 2
    out.paste(scaled, (off, off), scaled)
    return out


def circular(im: Image.Image) -> Image.Image:
    from PIL import ImageDraw

    mask = Image.new("L", (im.width * 4, im.height * 4), 0)
    ImageDraw.Draw(mask).ellipse((0, 0, mask.width - 1, mask.height - 1), fill=255)
    mask = mask.resize(im.size, Image.LANCZOS)
    out = im.copy()
    out.putalpha(mask)
    return out


def main() -> None:
    src = Image.open(SRC).convert("RGB")
    crop, radius = content_layer(src)
    bg = field_color(src)
    print(f"исходник {src.size[0]}×{src.size[1]}, описанный радиус содержимого {radius:.0f} px")
    print(f"фон поля #{bg[0]:02X}{bg[1]:02X}{bg[2]:02X}")

    for folder, fg_px, legacy_px in DENSITIES:
        out_dir = os.path.join(RES, folder)
        os.makedirs(out_dir, exist_ok=True)

        # Адаптивный foreground: описанная окружность содержимого = безопасный радиус.
        content = fg_px * (ADAPTIVE_SAFE_RADIUS_DP * 2 / ADAPTIVE_CANVAS_DP)
        fitted(crop, fg_px, content).save(
            os.path.join(out_dir, "ic_launcher_foreground.png")
        )

        legacy = Image.new("RGBA", (legacy_px, legacy_px), bg + (255,))
        square = fitted(crop, legacy_px, legacy_px * LEGACY_FILL_RATIO)
        legacy.alpha_composite(square)
        legacy.convert("RGB").save(os.path.join(out_dir, "ic_launcher.png"))

        round_bg = Image.new("RGBA", (legacy_px, legacy_px), bg + (255,))
        round_bg.alpha_composite(
            fitted(crop, legacy_px, legacy_px * LEGACY_ROUND_RADIUS_RATIO * 2)
        )
        circular(round_bg).save(os.path.join(out_dir, "ic_launcher_round.png"))
        print(f"  {folder}: foreground {fg_px}px, legacy {legacy_px}px")

    drawable = os.path.join(RES, "drawable")
    os.makedirs(drawable, exist_ok=True)
    with open(os.path.join(drawable, "ic_launcher_background.xml"), "w", encoding="utf-8") as f:
        f.write(
            '<?xml version="1.0" encoding="utf-8"?>\n'
            "<!-- Фон адаптивной иконки российской редакции: то самое белое поле, которое в\n"
            "     исходнике окружает раму. Сгенерирован tools/generate_russia_app_icons.py,\n"
            "     цвет взят замером по краю исходника. Сам XML адаптивной иконки\n"
            "     (mipmap-anydpi-v26/ic_launcher.xml) берётся из src/main — он ссылается на\n"
            "     @drawable/ic_launcher_background и @mipmap/ic_launcher_foreground, а те\n"
            "     перекрыты этим флейвором. -->\n"
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
    print("готово")


if __name__ == "__main__":
    main()
