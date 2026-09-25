package leshy.mushrooms.map

import leshy.mushrooms.map.domain.model.Edition

/**
 * Редакция, которую этот хост объявляет `shared` при старте (`LeshyApplication.onCreate`).
 *
 * Лежит во флейворном source set, а не в `BuildConfig`, ради симметрии с iOS: там роль этого
 * файла играет аргумент `MainViewController(edition:)` из Swift-таргета, и сборочной константы
 * взять неоткуда (обе редакции стоят поверх одного и того же `Shared.framework`). Значение —
 * типизированное, без строкового round-trip'а через `BuildConfig`, так что опечатка не доживёт
 * до рантайма.
 */
val HOST_EDITION = Edition.WORLD
