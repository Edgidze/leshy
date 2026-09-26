#!/usr/bin/env python3
"""Деревянные ассеты российской редакции: жетон, земля экрана, доска карточек, доска кнопок.

Что делает:

- `badge_wood.webp` — жетон 192px под СТОКОВЫЙ глиф Material (`design.md`, разделы 3 и 9);
  он же подложка кнопок-значков «+»/«−» и боковых кнопок «Записи»;
- `ground_light.webp` / `ground_dark.webp` — полноэкранная текстура земли для светлой и
  тёмной темы (`design.md`, раздел 7);
- `card_light.webp` / `card_dark.webp` — доска карточек: тот же материал, **другая доска**;
- `button_wood.webp` — тёмная доска под заливные кнопки, белая подпись поверх.

## Почему досок несколько, а не одна на всё

Земля, карточка и кнопка обязаны различаться, иначе карточка на земле не читается как
отдельный предмет, а кнопка — как нажимаемая. Различаются двумя вещами сразу, и обе нужны:
**тоном** (карточка светлее земли, кнопка заметно темнее обеих) и **направлением волокна** —
у земли доски идут вертикально, у карточек поперёк. Одного тона мало: на стыке двух кусков
дерева с одинаковым рисунком глаз видит пятно, а не край предмета.

Исходники лежат в корне репозитория, требования к ним — `docs/russia-edition/brief-wood-boards.md`.

## Почему подложка, а не перекрашенный значок

Монохромный значок на фоне отличается от монохромного значка на фоне ровно цветом. Значок на
деревянном жетоне отличается материалом — это тот самый «сдвиг восприятия при том же содержимом»,
ради которого затеяна вся редакция, и стоит он один файл на всё приложение.

## Геометрия жетона

192 точки — тот же размер, что у остальных служебных значков приложения (корневой `CLAUDE.md`:
потолок ширины обязан быть у любого растра, и для служебных значков он 192px).

Скругление — 4dp из 36dp видимого размера жетона, то есть 11% стороны: ровно тот радиус, что у
всех прямых углов редакции (`LeshyTokens.shapeButton`). Тонкая тёмная рама по периметру отделяет
жетон от земли, на которой он лежит: земля тоже тёплая, и без рамы край терялся бы.

Тени нет намеренно. Тень пришлось бы рисовать в растре, то есть под один цвет фона; на тёмной
теме она стала бы грязным ореолом.

## Масштаб волокна: [BADGE_ZOOM]

Первая доска (`gribnye_wood.png`, она же рама иконки) в жетоне 38dp читалась плоским красным
квадратом — волокно на ней слишком частое, и при сжатии всей доски в 192px оно схлопывается в
ровный тон. Правило брифа: самая мелкая различимая деталь не меньше одной двенадцатой стороны
в том размере, в котором картинка ПОКАЗЫВАЕТСЯ.

Отсюда два независимых рычага, и нужны оба: доска с крупным волокном
(`gribnye_wood_badge.png`) и [BADGE_ZOOM] — в жетон идёт не вся доска, а её центральная часть.
Зум х2 проверен раскладкой «жетон, отрисованный в 114px (38dp на 3x-экране), увеличенный
nearest-ом»: на х1 волокно есть, но мелкое, на х3–х4 в кадр попадают широкие тёмные полосы,
которые на одном жетоне читаются как пятно, а не как фактура.

## Тон земли подстраивается кодом, а не промптом

Генеративная модель попадает в фактуру, но не в тон: у присланной светлой земли средний цвет
`#E8CBA1` против `#E8D9BE` из палитры (`design.md`, раздел 4), у тёмной `#2E2014` против
`#211A12`. Земля обязана лечь НА МЕСТО сплошной заливки `background`, а не сменить её, поэтому
[match_tone] сдвигает средний цвет в нужный и заодно поджимает разброс светлоты до ~8% —
требование раздела 7 `design.md`: на земле лежат карточки и подписи, и фактура не должна с ними
спорить.

Запуск:  python3 tools/generate_russia_wood_assets.py
"""
from __future__ import annotations

import os
import sys

from PIL import Image, ImageDraw, ImageStat

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(ROOT, "tools"))

from generate_russia_app_icons import WOOD_SRC, center_square, trimmed_board  # noqa: E402

OUT = os.path.join(ROOT, "shared", "src", "commonMain", "composeResources", "drawable")

# Доска под жетон — своя, с крупным волокном. Рама иконки (`WOOD_SRC`) остаётся запасным
# вариантом: на ней жетон тоже соберётся, просто будет плоским, как было до 2026-09-26.
BADGE_SRC = os.path.join(ROOT, "gribnye_wood_badge.png")
GROUND_LIGHT_SRC = os.path.join(ROOT, "gribnye_ground_light.png")
GROUND_DARK_SRC = os.path.join(ROOT, "gribnye_ground_dark.png")

BADGE_NAME = "badge_wood.webp"
GROUND_LIGHT_NAME = "ground_light.webp"
GROUND_DARK_NAME = "ground_dark.webp"
CARD_LIGHT_NAME = "card_light.webp"
CARD_DARK_NAME = "card_dark.webp"
BUTTON_NAME = "button_wood.webp"

SIDE = 192
CORNER_RATIO = 4.0 / 36.0
BORDER_RATIO = 0.018
# Насколько затемнить дерево под рамку. Не чёрный контур: край жетона должен читаться как
# фаска той же доски, а не как обводка поверх неё.
BORDER_DARKEN = 0.55
# Сглаживание краёв: маска строится крупнее и уменьшается, иначе скругление выйдет ступеньками.
SUPERSAMPLE = 4
# Во сколько раз увеличить волокно: в жетон идёт центральная 1/BADGE_ZOOM доска. Разбор — в
# докстринге модуля.
BADGE_ZOOM = 2.0

# Цели из `design.md`, раздел 4 (палитра) и раздел 7 (текстуры).
GROUND_LIGHT_TARGET = (0xE8, 0xD9, 0xBE)
GROUND_DARK_TARGET = (0x21, 0x1A, 0x12)
GROUND_SPREAD = 0.08

# Карточка светлее земли в светлой теме и светлее её же в тёмной — предмет, лежащий НА земле,
# ловит больше света, чем она. Значения совпадают с `surface`/`surfaceBright` палитры редакции
# (`RussiaColors.kt`): текстура подменяет заливку, а не вводит третий тон.
CARD_LIGHT_TARGET = (0xF4, 0xE9, 0xD6)
CARD_DARK_TARGET = (0x36, 0x2C, 0x20)
# Размер доски карточек: карточки узкие и невысокие, вся доска целиком в них не показывается —
# хватает куска, кратно меньшего земли. Больше — только вес файла.
CARD_SIZE = (720, 720)

# Кнопка — та же доска, что жетон: подложка значка и подложка кнопки обязаны быть одним
# материалом, иначе ряд кнопок и ряд значков читаются как разные продукты.
BUTTON_SIZE = (512, 192)


def rounded_mask(side: int, radius: int) -> Image.Image:
    big = Image.new("L", (side * SUPERSAMPLE, side * SUPERSAMPLE), 0)
    ImageDraw.Draw(big).rounded_rectangle(
        (0, 0, big.width - 1, big.height - 1), radius=radius * SUPERSAMPLE, fill=255
    )
    return big.resize((side, side), Image.LANCZOS)


def zoomed_center(board: Image.Image, zoom: float) -> Image.Image:
    """Центральный квадрат доски со стороной 1/[zoom] — увеличение волокна без потери фактуры."""
    if zoom <= 1.0:
        return board
    s = min(board.size) / zoom
    x0 = (board.width - s) / 2
    y0 = (board.height - s) / 2
    return board.crop((int(x0), int(y0), int(x0 + s), int(y0 + s)))


def luma_span(im: Image.Image) -> tuple[int, int, int]:
    """Первый процентиль, медиана и девяносто девятый по светлоте.

    Проценты, а не min/max: одинокий тёмный пиксель не должен решать, насколько жать контраст.
    """
    hist = im.convert("L").histogram()
    total = sum(hist)

    def pct(p: float) -> int:
        want, acc = total * p, 0
        for value, count in enumerate(hist):
            acc += count
            if acc >= want:
                return value
        return 255

    return pct(0.01), pct(0.5), pct(0.99)


def match_tone(im: Image.Image, target: tuple[int, int, int], spread: float) -> Image.Image:
    """Сдвигает средний цвет [im] в [target] и поджимает разброс светлоты до [spread] от шкалы.

    Каждый канал преобразуется одним и тем же линейным законом `v → target + (v − mean) * k`,
    поэтому оттенок древесины сохраняется, меняются только общий тон и глубина фактуры.
    """
    p1, _, p99 = luma_span(im)
    current = (p99 - p1) / 255.0
    k = 1.0 if current <= spread else spread / current
    means = ImageStat.Stat(im).mean
    channels = []
    for ch, mean, aim in zip(im.split(), means, target):
        channels.append(ch.point(lambda v, m=mean, a=aim: max(0, min(255, int(round(a + (v - m) * k))))))
    return Image.merge("RGB", channels)


def build_badge() -> None:
    src = BADGE_SRC if os.path.exists(BADGE_SRC) else WOOD_SRC
    if not os.path.exists(src):
        raise SystemExit(f"нет {src} — жетон делать не из чего")
    board = trimmed_board(Image.open(src).convert("RGB"))
    wood = center_square(zoomed_center(board, BADGE_ZOOM), SIDE)

    radius = max(1, int(round(SIDE * CORNER_RATIO)))
    border = max(1, int(round(SIDE * BORDER_RATIO)))

    # Рамка — то же дерево, притемнённое, а не отдельный цвет: так она остаётся частью доски.
    darker = wood.point(lambda v: int(v * BORDER_DARKEN))
    inner = Image.composite(
        Image.new("L", (SIDE, SIDE), 255), Image.new("L", (SIDE, SIDE), 0),
        rounded_mask(SIDE - 2 * border, max(1, radius - border)).crop(
            (-border, -border, SIDE - border, SIDE - border)
        ),
    )
    face = Image.composite(wood, darker, inner)
    face.putalpha(rounded_mask(SIDE, radius))

    path = os.path.join(OUT, BADGE_NAME)
    face.save(path, "WEBP", quality=92, method=6)
    p1, p50, p99 = luma_span(face.convert("RGB"))
    print(
        f"{BADGE_NAME}: {SIDE}×{SIDE} из {os.path.basename(src)} (зум ×{BADGE_ZOOM:g}), "
        f"скругление {radius}px, рама {border}px, светлота {p1}/{p50}/{p99}, "
        f"{os.path.getsize(path)} байт"
    )


def build_ground(src: str, name: str, target: tuple[int, int, int]) -> None:
    if not os.path.exists(src):
        print(f"нет {os.path.basename(src)} — {name} пропущен")
        return
    # Текстура показывается через `ContentScale.Crop`, то есть кадрируется под пропорции экрана;
    # исходный размер сохраняется как есть — подгонять его под конкретный телефон нечем.
    ground = match_tone(Image.open(src).convert("RGB"), target, GROUND_SPREAD)
    path = os.path.join(OUT, name)
    ground.save(path, "WEBP", quality=82, method=6)
    mean = ImageStat.Stat(ground).mean
    p1, _, p99 = luma_span(ground)
    print(
        f"{name}: {ground.width}×{ground.height}, средний "
        f"#{int(mean[0]):02X}{int(mean[1]):02X}{int(mean[2]):02X}, "
        f"разброс {(p99 - p1) / 255 * 100:.1f}%, {os.path.getsize(path)} байт"
    )


def build_card(src: str, name: str, target: tuple[int, int, int]) -> None:
    """Доска карточек: та же древесина, повёрнутая на 90°, в другом тоне.

    Поворот — не украшение: волокно поперёк отличает карточку от земли даже там, где тона
    сближаются (край карточки на границе с землёй), и стоит он ноль байт.
    """
    if not os.path.exists(src):
        print(f"нет {os.path.basename(src)} — {name} пропущен")
        return
    board = Image.open(src).convert("RGB").rotate(90, expand=True)
    board = board.resize(CARD_SIZE, Image.LANCZOS)
    card = match_tone(board, target, GROUND_SPREAD)
    path = os.path.join(OUT, name)
    card.save(path, "WEBP", quality=86, method=6)
    mean = ImageStat.Stat(card).mean
    print(
        f"{name}: {card.width}×{card.height}, средний "
        f"#{int(mean[0]):02X}{int(mean[1]):02X}{int(mean[2]):02X}, {os.path.getsize(path)} байт"
    )


def build_button() -> None:
    """Доска кнопок — тот же исходник и тот же зум, что у жетона, но без скругления и альфы.

    Форму кнопке задаёт `shapeButton`, а не растр: кнопки бывают разной ширины, и вмороженное
    в картинку скругление растянулось бы вместе с ней.
    """
    src = BADGE_SRC if os.path.exists(BADGE_SRC) else WOOD_SRC
    if not os.path.exists(src):
        print(f"нет {os.path.basename(src)} — {BUTTON_NAME} пропущен")
        return
    board = zoomed_center(trimmed_board(Image.open(src).convert("RGB")), BADGE_ZOOM)
    width, height = BUTTON_SIZE
    k = max(width / board.width, height / board.height)
    scaled = board.resize((max(width, int(board.width * k)), max(height, int(board.height * k))), Image.LANCZOS)
    x0 = (scaled.width - width) // 2
    y0 = (scaled.height - height) // 2
    button = scaled.crop((x0, y0, x0 + width, y0 + height))
    path = os.path.join(OUT, BUTTON_NAME)
    button.save(path, "WEBP", quality=90, method=6)
    p1, p50, p99 = luma_span(button)
    print(
        f"{BUTTON_NAME}: {width}×{height} из {os.path.basename(src)}, "
        f"светлота {p1}/{p50}/{p99}, {os.path.getsize(path)} байт"
    )


def main() -> None:
    os.makedirs(OUT, exist_ok=True)
    build_badge()
    build_ground(GROUND_LIGHT_SRC, GROUND_LIGHT_NAME, GROUND_LIGHT_TARGET)
    build_ground(GROUND_DARK_SRC, GROUND_DARK_NAME, GROUND_DARK_TARGET)
    build_card(GROUND_LIGHT_SRC, CARD_LIGHT_NAME, CARD_LIGHT_TARGET)
    build_card(GROUND_DARK_SRC, CARD_DARK_NAME, CARD_DARK_TARGET)
    build_button()


if __name__ == "__main__":
    main()
