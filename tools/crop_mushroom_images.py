#!/usr/bin/env python3
"""Обрезка прозрачных полей у иллюстраций грибов каталога.

Все 409 изображений каталога квадратные (256×256), а сам гриб занимает в среднем 94% ширины и
лишь 75% высоты — остальное прозрачные поля. В площадке под фото на плитке «Записи» эти поля
превращались в пустоту над грибом (около 15dp), из-за чего расстояние от кнопок плитки до гриба
было втрое больше, чем до верха плитки, хотя в самой вёрстке отступы уже были одинаковые.

Скрипт обрезает каждый файл по его собственной границе непрозрачности. Соотношение сторон
площадки (`MUSHROOM_PHOTO_ASPECT_RATIO`, `MushroomTile.kt`) выставлено под медианное соотношение
обрезанного содержимого, так что типичный гриб заполняет площадку целиком.

## Про качество

Обрезка сама по себе не теряет ничего, а вот пересохранение теряет: файлы — lossy VP8, и это
второй проход кодирования.

Мерить это надо PSNR **только по непрозрачным пикселям**. У прозрачных RGB после lossy-кодирования
произвольный (его всё равно не видно), и метрика по всему кадру даёт бессмысленные 18 dB там, где
глаз видит 36. Альфа-канал libwebp пишет без потерь — проверено, PSNR 99 dB, прозрачность не
страдает ни при каком качестве.

Замер по всем файлам (PSNR по видимым пикселям, суммарный размер от исходных 7.75 МБ):

| качество | размер        | PSNR медиана | PSNR минимум |
|----------|---------------|--------------|--------------|
| 85       |  8.5 МБ (109%)|    36.3 dB   |   34.9 dB    |
| 90       |  9.5 МБ (123%)|    38.2 dB   |   36.7 dB    |
| 92       | 10.1 МБ (130%)|    39.1 dB   |   37.5 dB    |
| 95       | 11.1 МБ (143%)|    40.6 dB   |   38.4 dB    |

Выбрано 95. Ориентир «визуально неразличимо» — это примерно 40 dB, и только 95 его даёт; а по
размеру выбор почти ничего не стоит, потому что release APK весит 74 МБ и +3.3 МБ — это +4.5%.

Повторный запуск безопасен: у обрезанного файла прозрачных полей уже нет, он пропускается —
второго пересохранения не будет.

Запуск:  python3 tools/crop_mushroom_images.py [--dry-run]
"""
from __future__ import annotations

import os
import sys

from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DRAWABLES = os.path.join(ROOT, "shared", "src", "commonMain", "composeResources", "drawable")

# Не иллюстрация гриба — логотип, полей у него быть и не должно.
SKIP = {"leshy_logo.webp"}

QUALITY = 95
METHOD = 6


def main() -> None:
    dry_run = "--dry-run" in sys.argv
    files = sorted(f for f in os.listdir(DRAWABLES) if f.endswith(".webp") and f not in SKIP)
    cropped = skipped = 0
    size_before = size_after = 0
    aspects = []

    for name in files:
        path = os.path.join(DRAWABLES, name)
        image = Image.open(path).convert("RGBA")
        bbox = image.getchannel("A").getbbox()
        if bbox is None:
            print(f"  пропуск (полностью прозрачный): {name}")
            skipped += 1
            continue
        if bbox == (0, 0, image.size[0], image.size[1]):
            skipped += 1
            continue

        size_before += os.path.getsize(path)
        crop = image.crop(bbox)
        aspects.append(crop.size[0] / crop.size[1])
        if not dry_run:
            crop.save(path, "WEBP", quality=QUALITY, method=METHOD)
            size_after += os.path.getsize(path)
        cropped += 1

    print(f"обрезано: {cropped}, пропущено (уже без полей): {skipped}")
    if aspects:
        aspects.sort()
        print(f"соотношение сторон содержимого: медиана {aspects[len(aspects) // 2]:.3f}, "
              f"мин {aspects[0]:.3f}, макс {aspects[-1]:.3f}")
    if not dry_run and cropped:
        print(f"размер: было {size_before / 1e6:.2f} МБ, стало {size_after / 1e6:.2f} МБ "
              f"({100 * size_after / size_before:.0f}%)")


if __name__ == "__main__":
    main()
