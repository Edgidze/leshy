#!/usr/bin/env python3
"""Жетоны под значки разделов для российской редакции — из доски `gribnye_wood.png`.

Жетон — подложка под СТОКОВЫЙ глиф Material (`design.md`, разделы 3 и 9). Своего набора глифов
редакция не рисует: решение владельца, и оно экономит несоизмеримо больше, чем стоит подложка.
Меняется не значок, а то, на чём он лежит.

## Почему подложка, а не перекрашенный значок

Монохромный значок на фоне отличается от монохромного значка на фоне ровно цветом. Значок на
деревянном жетоне отличается материалом — это тот самый «сдвиг восприятия при том же содержимом»,
ради которого затеяна вся редакция, и стоит он один файл на всё приложение.

## Геометрия

192 точки — тот же размер, что у остальных служебных значков приложения (корневой `CLAUDE.md`:
потолок ширины обязан быть у любого растра, и для служебных значков он 192px).

Скругление — 4dp из 36dp видимого размера жетона, то есть 11% стороны: ровно тот радиус, что у
всех прямых углов редакции (`LeshyTokens.shapeButton`). Тонкая тёмная рама по периметру отделяет
жетон от земли, на которой он лежит: земля тоже тёплая, и без рамы край терялся бы.

Тени нет намеренно. Тень пришлось бы рисовать в растре, то есть под один цвет фона; на тёмной
теме она стала бы грязным ореолом.

Запуск:  python3 tools/generate_russia_wood_assets.py
"""
from __future__ import annotations

import os
import sys

from PIL import Image, ImageDraw

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(ROOT, "tools"))

from generate_russia_app_icons import WOOD_SRC, center_square, trimmed_board  # noqa: E402

OUT = os.path.join(ROOT, "shared", "src", "commonMain", "composeResources", "drawable")
BADGE_NAME = "badge_wood.webp"

SIDE = 192
CORNER_RATIO = 4.0 / 36.0
BORDER_RATIO = 0.018
# Насколько затемнить дерево под рамку. Не чёрный контур: край жетона должен читаться как
# фаска той же доски, а не как обводка поверх неё.
BORDER_DARKEN = 0.55
# Сглаживание краёв: маска строится крупнее и уменьшается, иначе скругление выйдет ступеньками.
SUPERSAMPLE = 4


def rounded_mask(side: int, radius: int) -> Image.Image:
    big = Image.new("L", (side * SUPERSAMPLE, side * SUPERSAMPLE), 0)
    ImageDraw.Draw(big).rounded_rectangle(
        (0, 0, big.width - 1, big.height - 1), radius=radius * SUPERSAMPLE, fill=255
    )
    return big.resize((side, side), Image.LANCZOS)


def main() -> None:
    if not os.path.exists(WOOD_SRC):
        raise SystemExit(f"нет {WOOD_SRC} — жетон делать не из чего")
    board = trimmed_board(Image.open(WOOD_SRC).convert("RGB"))
    wood = center_square(board, SIDE)

    radius = max(1, int(round(SIDE * CORNER_RATIO)))
    border = max(1, int(round(SIDE * BORDER_RATIO)))

    # Рамка — то же дерево, притемнённое, а не отдельный цвет: так она остаётся частью доски.
    darker = wood.point(lambda v: int(v * BORDER_DARKEN))
    inner = rounded_mask(SIDE, radius).point(lambda v: 255 if v > 200 else 0)
    inner = Image.composite(
        Image.new("L", (SIDE, SIDE), 255), Image.new("L", (SIDE, SIDE), 0),
        rounded_mask(SIDE - 2 * border, max(1, radius - border)).crop(
            (-border, -border, SIDE - border, SIDE - border)
        ),
    )
    face = Image.composite(wood, darker, inner)
    face.putalpha(rounded_mask(SIDE, radius))

    os.makedirs(OUT, exist_ok=True)
    path = os.path.join(OUT, BADGE_NAME)
    face.save(path, "WEBP", quality=92, method=6)
    print(f"{path}: {SIDE}×{SIDE}, скругление {radius}px, рама {border}px, {os.path.getsize(path)} байт")


if __name__ == "__main__":
    main()
