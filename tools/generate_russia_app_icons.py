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

## Геометрия переднего слоя: медальон, а не мат

**Репорт владельца 2026-09-26, вторая итерация:** грибы мелкие, квадрат картинки вписан в круг.
Так и было: передним слоем шёл белый мат со сценой, вписанный в безопасный круг d=66dp. Потеря
на этом пути двойная и чисто геометрическая — квадрат, вписанный в круг, теряет в √2 (сторона
46.7 из 108), а внутри него ещё сколько-то съедает белое поле мата. До самих грибов доходило
меньше трети ширины иконки.

Круглая версия картинки это НЕ чинит: с Android 8 лаунчер берёт обычную адаптивную иконку и режет
её своей маской, а вписывать круг в круг — та же задача с тем же ответом.

Чинит отказ от квадрата: передний слой — **круглый кроп сцены, плотно взятый по грибам**, во весь
безопасный круг. Белый мат исчезает, дерево остаётся краем иконки при любой маске, а грибы
становятся примерно вдвое крупнее. Выбор владельца из двух путей; второй — вырезанные грибы с
альфой поверх дерева, он даёт ещё крупнее и без видимой границы круга, но требует отдельной
генерации арта без фона.

Кроп берётся от прямоугольника СЦЕНЫ (внутри мата), а не от всей картинки: рама и мат отыскиваются
сканированием от краёв, пока идут красное и белое. Центр ([SCENE_CENTER_X], [SCENE_CENTER_Y]) и радиус
([SCENE_RADIUS]) подобраны по раскладке вариантов: круг обязан вместить обе шляпки целиком —
срезанная шляпка читается как брак печати, а не как кадрирование, — и не набрать при этом пустого
неба по краям.

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
# Значок, нарисованный ВНУТРИ приложения (приветственный экран, заставка холодного старта), —
# не иконка лаунчера, а обычный ресурс Compose. Пара к `leshy_icon.webp` мировой редакции.
DRAWABLES = os.path.join(ROOT, "shared", "src", "commonMain", "composeResources", "drawable")
IN_APP_NAME = "gribnye_icon.webp"
# Та же сторона, что у `leshy_icon.webp`. Показывается в 112dp, то есть 336px на экране 3x, —
# запас на растяжение остаётся даже на 4x.
IN_APP_SIDE = 512

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
# Доля стороны legacy-иконки под медальон. Больше прежних матовых долей: круглая картинка на
# квадратной подложке может подойти к краю ближе, чем квадратная, — у неё нет углов, которые
# упёрлись бы в кромку.
LEGACY_MEDALLION_RATIO = 0.80
LEGACY_ROUND_MEDALLION_RATIO = 0.74

# Центр кропа в долях сцены и его радиус в долях её ширины. Подобраны по раскладке четырёх
# вариантов: круг обязан вместить обе шляпки целиком (срезанная шляпка читается как брак печати),
# и при этом не набрать пустого неба по краям. Правее и ниже геометрического центра — пара грибов
# в кадре смещена туда.
SCENE_CENTER_X = 0.51
SCENE_CENTER_Y = 0.52
SCENE_RADIUS = 0.42
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
    _, (minx, miny, maxx, maxy) = field_flood(wood)
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


def scene_box(im: Image.Image) -> tuple[int, int, int, int]:
    """Прямоугольник фотографии внутри мата: от краёв внутрь, пока идут рама и мат.

    Сканирование, а не константы: рама нарисована от руки, её толщина по сторонам не совпадает, и
    зашитые доли разъехались бы на первой же перегенерации исходника.
    """
    w, h = im.size
    px = im.load()

    def frame_or_mat(c: tuple[int, int, int]) -> bool:
        r, g, b = c
        white = r > 225 and g > 225 and b > 225
        red = r > 110 and r - g > 45 and r - b > 45
        return white or red

    cy = h // 2
    x0 = 0
    while x0 < w and frame_or_mat(px[x0, cy]):
        x0 += 1
    x1 = w - 1
    while x1 > 0 and frame_or_mat(px[x1, cy]):
        x1 -= 1
    cx = (x0 + x1) // 2
    y0 = 0
    while y0 < h and frame_or_mat(px[cx, y0]):
        y0 += 1
    y1 = h - 1
    while y1 > 0 and frame_or_mat(px[cx, y1]):
        y1 -= 1
    return x0, y0, x1, y1


def medallion_layer(im: Image.Image) -> Image.Image:
    """Круглый кроп сцены, плотно по грибам — передний слой иконки. Разбор — в докстринге модуля."""
    x0, y0, x1, y1 = scene_box(im)
    width, height = x1 - x0, y1 - y0
    cx = x0 + width * SCENE_CENTER_X
    cy = y0 + height * SCENE_CENTER_Y
    r = width * SCENE_RADIUS
    # Кроп не должен вылезти за сцену: иначе в медальон попадёт белый мат куском по краю.
    r = min(r, cx - x0, x1 - cx, cy - y0, y1 - cy)
    scene = im.crop((int(cx - r), int(cy - r), int(cx + r), int(cy + r))).convert("RGB")
    return circular(scene)


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


def in_app_icon(im: Image.Image) -> Image.Image:
    """Значок для показа ВНУТРИ приложения: рама целиком, белое поле вокруг снято в прозрачность.

    Отличается от всего остального в этом файле тем, что маски лаунчера здесь нет вовсе — значок
    рисуется нашим же кодом на нашем же фоне. Поэтому композиция берётся исходная, со всей рамой:
    она и есть то, чем редакция отличается, и срезать её незачем.

    Поле вокруг рамы уходит в **альфу**, а не остаётся белым, по двум причинам сразу: на
    приветственном экране и на заставке холодного старта значок лежит на деревянной земле, и белые
    углы читались бы как вырезанный из бумаги квадрат; и скругление тогда не нужно повторять
    клипом в Compose — растр несёт собственный силуэт, а клип с чужим радиусом срезал бы раму.
    """
    seen, (minx, miny, maxx, maxy) = field_flood(im)
    w, _ = im.size
    alpha = Image.new("L", im.size)
    alpha.putdata([0 if seen[i] else 255 for i in range(len(seen))])
    out = im.convert("RGB").copy()
    out.putalpha(alpha)
    out = out.crop((minx, miny, maxx + 1, maxy + 1))
    # Квадрат, а не «как получилось»: рама нарисована от руки и на пару точек несимметрична, а
    # значок показывается в квадратном слоте.
    side = max(out.size)
    square = Image.new("RGBA", (side, side), (0, 0, 0, 0))
    square.alpha_composite(out, ((side - out.width) // 2, (side - out.height) // 2))
    return square.resize((IN_APP_SIDE, IN_APP_SIDE), Image.LANCZOS)


def field_flood(im: Image.Image) -> tuple[bytearray, tuple[int, int, int, int]]:
    """Заливка белого поля от краёв картинки: маска поля и рамка того, что в него не попало."""
    w, h = im.size
    px = im.load()
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
    return seen, (minx, miny, maxx, maxy)


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
    medallion = medallion_layer(src)
    x0, y0, x1, y1 = scene_box(src)
    print(f"исходник {src.size[0]}×{src.size[1]}")
    print(f"бруски рамы: слева {left}, справа {right}, сверху {top}, снизу {bottom}")
    print(f"сцена внутри мата: x {x0}..{x1}, y {y0}..{y1}")
    print(f"медальон {medallion.size[0]}×{medallion.size[1]}")

    in_app = in_app_icon(src)
    in_app_path = os.path.join(DRAWABLES, IN_APP_NAME)
    in_app.save(in_app_path, "WEBP", quality=92, method=6)
    print(f"{IN_APP_NAME}: {in_app.width}×{in_app.height}, {os.path.getsize(in_app_path)} байт")

    for folder, layer_px, legacy_px in DENSITIES:
        out_dir = os.path.join(RES, folder)
        os.makedirs(out_dir, exist_ok=True)

        wood_field(src, layer_px, wood).save(os.path.join(out_dir, "ic_launcher_background.png"))
        content = layer_px * (ADAPTIVE_SAFE_RADIUS_DP * 2 / ADAPTIVE_CANVAS_DP)
        fitted(medallion, layer_px, content).save(os.path.join(out_dir, "ic_launcher_foreground.png"))

        legacy = wood_field(src, legacy_px, wood).convert("RGBA")
        legacy.alpha_composite(fitted(medallion, legacy_px, legacy_px * LEGACY_MEDALLION_RATIO))
        legacy.convert("RGB").save(os.path.join(out_dir, "ic_launcher.png"))

        round_icon = wood_field(src, legacy_px, wood).convert("RGBA")
        round_icon.alpha_composite(fitted(medallion, legacy_px, legacy_px * LEGACY_ROUND_MEDALLION_RATIO))
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
