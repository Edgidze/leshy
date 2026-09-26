#!/usr/bin/env python3
"""Иконка приложения российской редакции — из `gribnye_icon.png`.

Исходник владельца: квадрат со скруглёнными углами — алая деревянная рама, белый мат, сцена с
двумя подосиновиками, вокруг белое поле.

Почему отдельный скрипт, а не `generate_app_icons.py`: тот разбирает мировую иконку по цветам
(«чёрные углы, тёмно-зелёный фон, золотая капля») и к этому исходнику неприменим ни одной
строкой.

## Раскладка по слоям: рама — это ФОН, а не картинка внутри иконки

Первая версия скрипта вписывала всю раму целиком в безопасную зону адаптивной иконки. Формально
правильно (ничего не срезается), а на устройстве плохо: лаунчер накладывает круглую маску, и
квадратная рама оказывалась вписанной в круг — квадрат в круге, с белым полем по углам (репорт
владельца 2026-09-26).

Причина, по которой «сделать отдельную круглую картинку» это не чинит: с Android 8 лаунчеры почти
не спрашивают `ic_launcher_round`, а берут ОБЫЧНУЮ адаптивную иконку и режут её своей маской —
круглой, сквиркл, каплей, по теме лаунчера. Второй файл в этом месте просто не участвует.

Чинит другая раскладка, она здесь и сделана:

- **фоновый слой — деревянное поле во всю площадь**. Маска режет дерево, а не картинку, и край
  иконки сам становится рамой — какой бы формы маску ни выбрал лаунчер;
- **передний слой — белый мат со сценой**, вписанный в безопасную зону.

Под круглой маской получается алый ободок с фотографией внутри, под сквирклом — то же самое
квадратнее. Рама перестаёт быть нарисованной и становится краем иконки.

## Откуда берётся деревянное поле

Растянуть исходную раму до краёв нельзя: её брусок — 50 точек из 1084, то есть 4.6% ширины. Чтобы
дерево дошло до углов холста, рама должна была бы раздуться настолько, что её внутреннее окно ушло
бы далеко за пределы холста.

**Если рядом лежит `gribnye_wood.png` — поле берётся оттуда**: с доски снимается белое поле
вокруг, отрезается фаска по периметру (иначе светлая кромка легла бы полосой поперёк иконки), и
остаток обрезается по центру до квадрата. Это основной путь: доска, нарисованная целиком, всегда
лучше того, что можно вытащить из полоски в тридцать три точки.

**Если файла нет — запасной путь**: тело верхнего бруска (без фаски сверху и тёмной кромки снизу)
увеличивается ИЗОТРОПНО до высоты холста и обрезается по центру. Изотропно, потому что оба
неизотропных варианта хуже: растяжение поперёк волокна смазывает его в длинные полосы, а
замощение с отражением оставляет видимый шов ровно посередине иконки. Цена изотропного пути —
мыло: 33 точки растут в 13 раз. Поле при этом получается ровным и без артефактов, то есть годным
как временное.

## Геометрия переднего слоя

Безопасная зона адаптивной иконки — круг d=66dp на холсте 108dp: внутрь него не залезает ни одна
системная маска. Мат со сценой масштабируется так, чтобы его САМАЯ ДАЛЬНЯЯ точка попадала ровно на
радиус 33dp. «Самая дальняя» меряется по пикселям, а не выводится из стороны квадрата: углы мата
скруглены внутренним краем рамы, и его описанная окружность заметно меньше описанной окружности
квадрата той же стороны.

Углы мата вырезаются заливкой от углов обрезка по «дереву», а не порогом по цвету: красные шляпки
подосиновиков по цвету от рамы отличаются слабо, но с углами не соединены.

**Раскладка только под Android.** iOS-вариант этим скриптом не готовится: Apple накладывает маску
сама, и ему нужен full-bleed квадрат без альфы (`design.md`, раздел 14). Второго таргета в
`iosApp` сейчас нет, делать эту композицию вслепую нечего.

Запуск:  python3 tools/generate_russia_app_icons.py
"""
from __future__ import annotations

import math
import os
from collections import deque

from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, "gribnye_icon.png")
# Необязательный отдельный исходник деревянного поля. Есть — берётся он; нет — поле собирается
# из бруска самой рамы (хуже, см. `wood_field`).
WOOD_SRC = os.path.join(ROOT, "gribnye_wood.png")
RES = os.path.join(ROOT, "androidApp", "src", "russia", "res")

# (каталог, сторона слоя адаптивной иконки 108dp, сторона legacy-иконки 48dp)
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
# Legacy-иконка (API 24–25, адаптивных там нет): системной маски тоже нет, поэтому мат можно
# отпустить пошире — до края остаётся видимая рама из того же дерева.
LEGACY_MAT_RATIO = 0.74
LEGACY_ROUND_MAT_RATIO = 0.62
# Какую долю бруска отрезать с концов, чтобы не захватить скруглённые углы рамы.
BAR_END_TRIM = 0.18
# Доли высоты бруска, занятые светлой фаской сверху и тёмной кромкой снизу.
BAR_BEVEL_TOP = 0.26
BAR_BEVEL_BOTTOM = 0.10
# Насколько отступить внутрь от края доски в `gribnye_wood.png`. Доска нарисована со скруглёнными
# углами и светлой фаской по периметру; отступ на её радиус оставляет прямоугольник, целиком
# лежащий внутри древесины.
WOOD_BEVEL_INSET = 0.12


def is_wood(p) -> bool:
    """Алое дерево рамы. Шляпки подосиновиков ярче и желтее — сюда не попадают."""
    r, g, b = p[0], p[1], p[2]
    return r > 90 and r - g > 45 and r - b > 45


def is_field(p) -> bool:
    """Белое поле вокруг рамы. Порог с запасом: в исходнике оно 253, а не 255."""
    r, g, b = p[0], p[1], p[2]
    return min(r, g, b) >= 240 and (max(r, g, b) - min(r, g, b)) <= 12


def runs(flags: list[bool]) -> list[tuple[int, int]]:
    out: list[tuple[int, int]] = []
    start = None
    for i, v in enumerate(flags):
        if v and start is None:
            start = i
        elif not v and start is not None:
            out.append((start, i - 1))
            start = None
    if start is not None:
        out.append((start, len(flags) - 1))
    return out


def frame_bars(im: Image.Image):
    """Границы четырёх брусков рамы — по прогонам «дерева» вдоль центральных линий.

    Именно прогоны, а не общая рамка «всех красных пикселей»: последняя захватила бы шляпки
    подосиновиков и сдвинула бы внутреннее окно на полкартинки.
    """
    px = im.load()
    w, h = im.size
    horizontal = runs([is_wood(px[x, h // 2]) for x in range(w)])
    vertical = runs([is_wood(px[w // 2, y]) for y in range(h)])
    left, right = horizontal[0], horizontal[-1]
    top, bottom = vertical[0], vertical[-1]
    return left, right, top, bottom


def center_square(im: Image.Image, side: int) -> Image.Image:
    """Квадрат side×side из центра [im], вписанный без искажения пропорций."""
    k = side / min(im.size)
    scaled = im.resize((max(side, int(round(im.width * k))), max(side, int(round(im.height * k)))), Image.LANCZOS)
    x0 = (scaled.width - side) // 2
    y0 = (scaled.height - side) // 2
    return scaled.crop((x0, y0, x0 + side, y0 + side))


def trimmed_board(wood: Image.Image) -> Image.Image:
    """Доска без белого поля вокруг и без фаски по периметру."""
    w, h = wood.size
    px = wood.load()
    seen = bytearray(w * h)
    q: deque[tuple[int, int]] = deque()

    def push(x: int, y: int) -> None:
        if 0 <= x < w and 0 <= y < h and not seen[y * w + x] and is_field(px[x, y]):
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
        push(x + 1, y)
        push(x - 1, y)
        push(x, y + 1)
        push(x, y - 1)

    minx, miny, maxx, maxy = w, h, -1, -1
    for y in range(h):
        row = y * w
        for x in range(w):
            if not seen[row + x]:
                minx = min(minx, x)
                maxx = max(maxx, x)
                miny = min(miny, y)
                maxy = max(maxy, y)
    inset = int(min(maxx - minx, maxy - miny) * WOOD_BEVEL_INSET)
    return wood.crop((minx + inset, miny + inset, maxx - inset + 1, maxy - inset + 1))


def wood_field(im: Image.Image, side: int, wood: Image.Image | None) -> Image.Image:
    """Деревянное поле side×side: из [wood], если он есть, иначе из бруска рамы."""
    if wood is not None:
        return center_square(wood, side)
    left, right, top, _ = frame_bars(im)
    span = right[1] - left[0]
    trim = int(span * BAR_END_TRIM)
    # Тело бруска без фаски сверху и тёмной кромки снизу: и та и другая, растянутые на весь
    # холст, читались бы как широкая светлая полоса поперёк иконки.
    body_top = top[0] + int((top[1] - top[0]) * BAR_BEVEL_TOP)
    body_bottom = top[1] - int((top[1] - top[0]) * BAR_BEVEL_BOTTOM)
    bar = im.crop((left[0] + trim, body_top, right[1] - trim, body_bottom + 1))
    return center_square(bar, side)


def mat_layer(im: Image.Image):
    """Белый мат со сценой как RGBA со скруглёнными углами, плюс его описанный радиус."""
    left, right, top, bottom = frame_bars(im)
    box = (left[1] + 1, top[1] + 1, right[0], bottom[0])
    crop = im.crop(box).convert("RGB")
    w, h = crop.size
    px = crop.load()

    # Углы обрезка — это внутреннее скругление рамы. Убираются заливкой ОТ УГЛОВ по дереву:
    # порог по цвету снял бы заодно шляпки подосиновиков, они почти того же тона.
    wood = bytearray(w * h)
    q: deque[tuple[int, int]] = deque()

    def push(x: int, y: int) -> None:
        if 0 <= x < w and 0 <= y < h and not wood[y * w + x] and is_wood(px[x, y]):
            wood[y * w + x] = 1
            q.append((x, y))

    for corner in ((0, 0), (w - 1, 0), (0, h - 1), (w - 1, h - 1)):
        push(*corner)
    while q:
        x, y = q.popleft()
        push(x + 1, y)
        push(x - 1, y)
        push(x, y + 1)
        push(x, y - 1)

    alpha = Image.new("L", (w, h), 255)
    ap = alpha.load()
    cx, cy = (w - 1) / 2.0, (h - 1) / 2.0
    radius = 0.0
    for y in range(h):
        row = y * w
        for x in range(w):
            if wood[row + x]:
                ap[x, y] = 0
            else:
                radius = max(radius, math.hypot(x - cx, y - cy))
    rgba = crop.copy()
    rgba.putalpha(alpha)

    side = int(math.ceil(radius * 2))
    square = Image.new("RGBA", (side, side), (0, 0, 0, 0))
    square.paste(rgba, ((side - w) // 2, (side - h) // 2), rgba)
    return square, radius


def fitted(layer: Image.Image, canvas_px: int, content_px: float) -> Image.Image:
    size = max(1, int(round(content_px)))
    scaled = layer.resize((size, size), Image.LANCZOS)
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
    wood = Image.open(WOOD_SRC).convert("RGB") if os.path.exists(WOOD_SRC) else None
    if wood is not None:
        raw = wood.size
        wood = trimmed_board(wood)
        print(f"доска {raw[0]}×{raw[1]} → без поля и фаски {wood.size[0]}×{wood.size[1]}")
    print(
        f"деревянное поле: {os.path.basename(WOOD_SRC)} {wood.size[0]}×{wood.size[1]}"
        if wood is not None
        else "деревянное поле: отдельного исходника нет, собирается из бруска рамы (запасной путь)"
    )
    left, right, top, bottom = frame_bars(src)
    mat, radius = mat_layer(src)
    print(f"исходник {src.size[0]}×{src.size[1]}")
    print(f"бруски рамы: слева {left}, справа {right}, сверху {top}, снизу {bottom}")
    print(f"мат со сценой {mat.size[0]}×{mat.size[1]}, описанный радиус {radius:.0f} px")

    for folder, layer_px, legacy_px in DENSITIES:
        out_dir = os.path.join(RES, folder)
        os.makedirs(out_dir, exist_ok=True)

        wood_field(src, layer_px, wood).save(os.path.join(out_dir, "ic_launcher_background.png"))
        content = layer_px * (ADAPTIVE_SAFE_RADIUS_DP * 2 / ADAPTIVE_CANVAS_DP)
        fitted(mat, layer_px, content).save(os.path.join(out_dir, "ic_launcher_foreground.png"))

        legacy = wood_field(src, legacy_px, wood).convert("RGBA")
        legacy.alpha_composite(fitted(mat, legacy_px, legacy_px * LEGACY_MAT_RATIO))
        legacy.convert("RGB").save(os.path.join(out_dir, "ic_launcher.png"))

        round_icon = wood_field(src, legacy_px, wood).convert("RGBA")
        round_icon.alpha_composite(fitted(mat, legacy_px, legacy_px * LEGACY_ROUND_MAT_RATIO))
        circular(round_icon).save(os.path.join(out_dir, "ic_launcher_round.png"))
        print(f"  {folder}: слои {layer_px}px, legacy {legacy_px}px")

    # Фон адаптивной иконки стал растром, поэтому прежний векторный XML больше не нужен.
    stale = os.path.join(RES, "drawable", "ic_launcher_background.xml")
    if os.path.exists(stale):
        os.remove(stale)
        print("  удалён прежний векторный фон drawable/ic_launcher_background.xml")
    print("готово")


if __name__ == "__main__":
    main()
