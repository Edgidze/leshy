#!/usr/bin/env python3
"""Значок уведомления в статус-баре — из значка гриба, общего для обеих редакций.

## Почему не логотип приложения

До 2026-09-26 здесь лежал силуэт Лешего, снятый с `ic_launcher_foreground` яркостью
(`notif_ic_leshy.png`). Пока продукт был один, это было правильно: значок в статус-баре обязан
называть приложение. С появлением российской редакции он стал остатком мирового «Лешего» в
чужом продукте — ровно тем, что ищет задача 14 `docs/russia-edition/track-app.md`, — а иконки
у редакций разные, и снять маску с обеих значило бы завести второй файл и развилку по редакции
в сервисе записи.

Решение владельца (2026-09-26): **один значок на обе редакции, из простого значка-логотипа
гриба**, того самого, что стоит рядом со счётчиками находок (`ic_mushrooms.webp`). Он уже
силуэтный, узнаётся в 24dp и не принадлежит ни одной из редакций в отдельности.

## Почему яркостной растяжки, в отличие от прежнего значка, здесь нет

`notif_ic_leshy` собирался формулой «альфа × smoothstep(яркость)»: внутри лица Лешего есть
светотень, и одна альфа схлопывала её в сплошное пятно. Значок гриба нарисован **плоским
чёрным силуэтом** — у него внутри нет ничего, что можно было бы потерять, и альфа исходника
является готовой маской.

## Геометрия

Своего поля у картинки нет: силуэт обрезается по непрозрачности и вписывается в квадрат по
длинной стороне. Android 12+ и так рисует значок внутри круглой плашки со своим отступом, и
любой добавленный здесь пиксель делает гриб мельче внутри неё.

Пять плотностей, потому что значок рисуется в фиксированные 24dp.

Запуск:  python3 tools/generate_notification_icon.py
"""
from __future__ import annotations

import os

from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, "shared", "src", "commonMain", "composeResources", "drawable", "ic_mushrooms.webp")
RES = os.path.join(ROOT, "shared", "src", "androidMain", "res")
NAME = "notif_ic_mushroom.png"

# (каталог, сторона в точках) — значок показывается в 24dp.
DENSITIES = [
    ("drawable-mdpi", 24),
    ("drawable-hdpi", 36),
    ("drawable-xhdpi", 48),
    ("drawable-xxhdpi", 72),
    ("drawable-xxxhdpi", 96),
]


def silhouette(src: Image.Image) -> Image.Image:
    """Белый силуэт с альфой исходника, обрезанный по непрозрачности и вписанный в квадрат."""
    alpha = src.getchannel("A")
    box = alpha.getbbox()
    alpha = alpha.crop(box)
    side = max(alpha.size)
    square = Image.new("L", (side, side), 0)
    square.paste(alpha, ((side - alpha.width) // 2, (side - alpha.height) // 2))
    out = Image.new("RGBA", (side, side), (255, 255, 255, 0))
    out.putalpha(square)
    return out


def main() -> None:
    src = Image.open(SRC).convert("RGBA")
    mask = silhouette(src)
    print(f"исходник {os.path.basename(SRC)} {src.width}×{src.height} → силуэт {mask.width}×{mask.height}")
    for folder, side in DENSITIES:
        out_dir = os.path.join(RES, folder)
        os.makedirs(out_dir, exist_ok=True)
        path = os.path.join(out_dir, NAME)
        mask.resize((side, side), Image.LANCZOS).save(path)
        print(f"  {folder}: {side}×{side}, {os.path.getsize(path)} байт")

    stale = [os.path.join(RES, folder, "notif_ic_leshy.png") for folder, _ in DENSITIES]
    for path in stale:
        if os.path.exists(path):
            os.remove(path)
            print(f"  удалён прежний {os.path.relpath(path, ROOT)}")
    print("готово")


if __name__ == "__main__":
    main()
