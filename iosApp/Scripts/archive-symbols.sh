#!/bin/sh
#
# Складывает Mach-O-бинари каждой сборки, уезжающей на устройство, в архив
# за пределами DerivedData — чтобы crash-репорт, пришедший через неделю,
# было чем символизировать.
#
# Зачем: из трёх iOS-инцидентов с watchdog два оказались нечитаемыми ровно
# потому, что `leshy.debug.dylib` той сборки уже перезаписан, и UUID из
# репорта не совпал ни с чем сохранившимся. Шаг 0 плана —
# .claude/investigations/ios-maplibre-background-watchdog/README.md
#
# Куда класть — переопределяется переменной LESHY_SYMBOL_ARCHIVE.
# Найти сборку по UUID из .ips:
#   grep -ril <UUID> ~/Library/Developer/leshy-symbols/*/uuids.txt

set -u

ARCHIVE_ROOT="${LESHY_SYMBOL_ARCHIVE:-$HOME/Library/Developer/leshy-symbols}"
KEEP=10

# Индексная сборка бинарей не производит; симулятор на телефон не попадает,
# а значит и в crash-репорт с устройства тоже.
if [ "${ACTION:-build}" = "indexbuild" ] || [ "${PLATFORM_NAME:-}" != "iphoneos" ]; then
    exit 0
fi

APP="${TARGET_BUILD_DIR:-}/${WRAPPER_NAME:-}"
if [ ! -d "$APP" ]; then
    echo "warning: $APP не найден — символы не заархивированы"
    exit 0
fi

# В Debug Xcode 16 весь наш код лежит в <name>.debug.dylib, а сам `leshy` —
# тонкий загрузчик, поэтому ключевой UUID берём с dylib, если он есть.
primary="$APP/${EXECUTABLE_NAME}"
[ -f "$APP/${EXECUTABLE_NAME}.debug.dylib" ] && primary="$APP/${EXECUTABLE_NAME}.debug.dylib"

uuid=$(dwarfdump --uuid "$primary" 2>/dev/null | awk '/UUID:/ { print $2; exit }')
if [ -z "$uuid" ]; then
    echo "warning: не удалось прочитать UUID из $primary — символы не заархивированы"
    exit 0
fi

if grep -qs -- "$uuid" "$ARCHIVE_ROOT"/*/uuids.txt 2>/dev/null; then
    echo "note: сборка $uuid уже в архиве, пропуск"
    exit 0
fi

dest="$ARCHIVE_ROOT/$(date '+%Y%m%d-%H%M%S')-${CONFIGURATION}-$(printf '%s' "$uuid" | cut -c1-8)"
mkdir -p "$dest/binaries" || exit 0

# Копируем только Mach-O: ресурсы для символизации не нужны, а .app целиком
# раздул бы архив на порядок. MapLibre.framework — обязательно: именно его
# UUID понадобился, чтобы расшифровать зависшие кадры в инциденте №1.
for f in "$APP/${EXECUTABLE_NAME}" "$APP"/*.dylib; do
    [ -f "$f" ] && ditto "$f" "$dest/binaries/$(basename "$f")"
done
for fw in "$APP/Frameworks"/*.framework; do
    [ -d "$fw" ] || continue
    name=$(basename "$fw" .framework)
    [ -f "$fw/$name" ] && ditto "$fw/$name" "$dest/binaries/$name"
done

# В Release (dwarf-with-dsym) забираем и сами .dSYM; в Debug их нет —
# там DEBUG_INFORMATION_FORMAT = dwarf, отладочная информация внутри бинаря.
if [ -n "${DWARF_DSYM_FOLDER_PATH:-}" ] && [ -d "$DWARF_DSYM_FOLDER_PATH" ]; then
    for d in "$DWARF_DSYM_FOLDER_PATH"/*.dSYM; do
        [ -d "$d" ] && ditto "$d" "$dest/$(basename "$d")"
    done
fi

{
    echo "# собрано   $(date '+%Y-%m-%d %H:%M:%S %z')"
    echo "# конфиг    ${CONFIGURATION:-?}  ${MARKETING_VERSION:-?} (${CURRENT_PROJECT_VERSION:-?})"
    echo "# git       $(git -C "${SRCROOT:-.}/.." rev-parse --short HEAD 2>/dev/null || echo неизвестен)"
    echo
    for f in "$dest/binaries"/*; do
        [ -f "$f" ] && dwarfdump --uuid "$f" 2>/dev/null
    done
    for d in "$dest"/*.dSYM; do
        [ -d "$d" ] && dwarfdump --uuid "$d" 2>/dev/null
    done
} > "$dest/uuids.txt"

# Ротация: имена начинаются с метки времени, поэтому лексикографической
# сортировки достаточно.
total=$(find "$ARCHIVE_ROOT" -mindepth 1 -maxdepth 1 -type d | wc -l | tr -d ' ')
if [ "$total" -gt "$KEEP" ]; then
    find "$ARCHIVE_ROOT" -mindepth 1 -maxdepth 1 -type d | sort | head -n "$((total - KEEP))" | while IFS= read -r old; do
        rm -rf "$old"
    done
fi

echo "note: символы сборки $uuid -> $dest ($(du -sh "$dest" 2>/dev/null | cut -f1))"
exit 0
