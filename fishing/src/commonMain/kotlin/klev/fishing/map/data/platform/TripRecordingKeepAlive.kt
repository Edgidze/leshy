package klev.fishing.map.data.platform

import leshy.mushrooms.map.domain.model.AppLanguage

/**
 * «Не считай приложение фоновым, пока идёт рыбалка». На Android — foreground-сервис типа
 * `location`; на iOS (когда до него дойдёт) это no-op: там за то же отвечают флаги
 * `CLLocationManager` и фоновый режим `location`, отдельного объекта не требуется.
 */
interface TripRecordingKeepAlive {
    fun start(language: AppLanguage)
    fun stop()
}
