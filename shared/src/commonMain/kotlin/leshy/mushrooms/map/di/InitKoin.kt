package leshy.mushrooms.map.di

import leshy.mushrooms.map.domain.model.Edition
import leshy.mushrooms.map.domain.model.editionEndpointsFor
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * [edition] — единственный канал, которым хост сообщает `shared`, какой это продукт, и он
 * намеренно проходит здесь, а не параметром `App()`. Причина в том, что редакцию спрашивает не
 * только Compose: адрес тайл-хоста нужен репозиториям (`MapStyleCacheRepository`,
 * `OfflineRegionRepositoryImpl`, нативные снапшоттеры миниатюр), которые создаёт Koin задолго до
 * первой композиции. Передача параметром в `App()` оставила бы их без редакции и вынудила бы
 * хост объявлять её второй раз, другим способом.
 *
 * Значения по умолчанию у [edition] нет сознательно: новый хост обязан выбрать редакцию явно,
 * а не унаследовать мировую молчанием.
 */
fun initKoin(edition: Edition, appDeclaration: KoinAppDeclaration? = null) {
    startKoin {
        appDeclaration?.invoke(this)
        modules(
            module {
                single { edition }
                single { editionEndpointsFor(edition) }
            },
            platformModule,
            dataModule,
            domainModule,
            presentationModule,
        )
    }
}
