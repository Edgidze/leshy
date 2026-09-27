package klev.fishing.map.data.weather

import klev.fishing.map.domain.model.TripWeather

/**
 * Подсказка погоды: что, по данным сети, было в этом месте в это время.
 *
 * **Это именно подсказка, а не источник истины** — решение владельца (2026-09-27). Погоду в дневник
 * вписывает рыбак, потому что база знает узел сетки за час, а он ловил в конкретной точке в
 * конкретные минуты: над водой, в затишке под берегом или на ветродуе показания расходятся с
 * моделью заметно. Отсюда два следствия для кода:
 *
 * 1. приложение обязано быть полностью работоспособным при полном отсутствии реализации —
 *    возвращать `null` штатно, а не бросать;
 * 2. никакого фонового опроса: запрос делается ровно тогда, когда человек нажал «Подсказать».
 */
interface WeatherSuggestionSource {
    /**
     * Погода около [atMillis] в точке ([lat], [lon]), или `null`, если узнать не удалось —
     * нет сети, сервис ответил ошибкой, точка вне покрытия. Провенанс у результата всегда
     * [klev.fishing.map.domain.model.WeatherProvenance.SUGGESTED]: правки человека проставит экран.
     */
    suspend fun suggest(lat: Double, lon: Double, atMillis: Long): TripWeather?
}

/** Заглушка на случай, когда провайдер не выбран: кнопка «Подсказать» честно сообщает о неудаче. */
object NoWeatherSuggestions : WeatherSuggestionSource {
    override suspend fun suggest(lat: Double, lon: Double, atMillis: Long): TripWeather? = null
}
