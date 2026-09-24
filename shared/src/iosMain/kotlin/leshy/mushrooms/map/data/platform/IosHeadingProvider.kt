package leshy.mushrooms.map.data.platform

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.CLHeading
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.darwin.NSObject

/**
 * Курс от того же `CLLocationManager`, что и координаты, а НЕ от `CoreMotion`. Разница не
 * техническая, а в разрешениях: `CLLocationManager` уже работает на выданном «При использовании»,
 * а `CMMotionManager` потребовал бы `NSMotionUsageDescription` в `Info.plist` и новый системный
 * запрос. Условие владельца было прямым — доработка делается, только если новых разрешений не
 * появляется.
 *
 * Отдельный менеджер, а не тот, что внутри [IosLocationTracker]: курс нужен лишь пока открыта
 * панель навигации, а координаты — всю прогулку, и склеивать два разных жизненных цикла в один
 * объект значило бы держать включённым магнитометр всю прогулку.
 */
class IosHeadingProvider : HeadingProvider {

    // Те же поля класса, что и в [IosLocationTracker], и ровно по той же причине: `delegate` у
    // `CLLocationManager` объявлен `weak`, и без сильной ссылки за пределами функции ARC
    // освобождает делегата сразу после первого колбэка. Там это стоило владельцу целой прогулки
    // (все находки легли в одну устаревшую точку) — здесь стоило бы намертво замершей стрелки.
    private var manager: CLLocationManager? = null
    private var delegate: CLLocationManagerDelegateProtocol? = null

    override fun heading(): Flow<Double> = callbackFlow {
        // Договор [HeadingProvider.heading]: нет компаса — пустой поток, вызывающий останется на
        // GPS-курсе. На iPhone магнитометр есть всегда, но `headingAvailable()` отвечает и за
        // «датчик сейчас недоступен», так что проверка не формальная.
        if (!CLLocationManager.headingAvailable()) {
            close()
            return@callbackFlow
        }
        val manager = CLLocationManager()
        manager.headingFilter = HEADING_MIN_CHANGE_DEGREES

        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateHeading: CLHeading) {
                // Отрицательная точность — договор CoreLocation для «показание недостоверно»
                // (телефон рядом с магнитом, динамиком, в машине). Такое значение лучше пропустить
                // целиком: стрелка замрёт на прошлом курсе, а не покажет случайную сторону.
                if (didUpdateHeading.headingAccuracy < 0) return
                // `trueHeading` отрицателен, пока система не знает местоположения (геолокация
                // выключена, фикса ещё нет) — тогда остаётся магнитный курс. Он отличается от
                // истинного на магнитное склонение, то есть заметно, но направление «примерно
                // туда» всё равно полезнее отсутствующего; как только появится фикс, iOS начнёт
                // отдавать истинный сама, без перезапуска подписки.
                val heading = didUpdateHeading.trueHeading
                    .takeIf { it >= 0 }
                    ?: didUpdateHeading.magneticHeading
                trySend(heading)
            }

            // Экран калибровки («покрутите телефон восьмёркой») — модальный, во весь экран и
            // поверх всего. Появляться посреди леса, когда человек идёт к отмеченному месту, он не
            // должен: показания без калибровки приходят с худшей `headingAccuracy`, и это уже
            // отработано проверкой выше.
            override fun locationManagerShouldDisplayHeadingCalibration(manager: CLLocationManager): Boolean = false
        }

        manager.delegate = delegate
        this@IosHeadingProvider.manager = manager
        this@IosHeadingProvider.delegate = delegate
        manager.startUpdatingHeading()

        awaitClose {
            manager.stopUpdatingHeading()
            manager.delegate = null
            this@IosHeadingProvider.manager = null
            this@IosHeadingProvider.delegate = null
        }
    }
}
