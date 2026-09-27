package klev.fishing.map.di

import androidx.room.RoomDatabase
import klev.fishing.map.data.catalog.FishCatalogSource
import klev.fishing.map.data.local.FishingDatabase
import klev.fishing.map.data.local.buildFishingDatabase
import klev.fishing.map.data.repository.CatchRepositoryImpl
import klev.fishing.map.data.repository.FishingSettingsRepository
import klev.fishing.map.data.repository.SpeciesRepositoryImpl
import klev.fishing.map.data.repository.TripRepositoryImpl
import klev.fishing.map.data.repository.TripTrackPointRepositoryImpl
import klev.fishing.map.data.weather.OpenMeteoWeatherSource
import klev.fishing.map.data.weather.WeatherSuggestionSource
import klev.fishing.map.domain.repository.CatchRepository
import klev.fishing.map.domain.repository.SpeciesRepository
import klev.fishing.map.domain.repository.TripRepository
import klev.fishing.map.domain.repository.TripTrackPointRepository
import klev.fishing.map.domain.usecase.AddCatchUseCase
import klev.fishing.map.domain.usecase.DeleteTripUseCase
import klev.fishing.map.domain.usecase.EnsureFishSpeciesUseCase
import klev.fishing.map.domain.usecase.FinishTripUseCase
import klev.fishing.map.domain.usecase.RecordTripPointUseCase
import klev.fishing.map.domain.usecase.StartTripUseCase
import klev.fishing.map.presentation.archive.FishArchiveViewModel
import klev.fishing.map.presentation.map.CatchMapViewModel
import klev.fishing.map.presentation.record.TripViewModel
import klev.fishing.map.presentation.settings.FishSettingsViewModel
import klev.fishing.map.presentation.trip.TripDetailViewModel
import leshy.mushrooms.map.di.initKoin
import leshy.mushrooms.map.domain.model.Edition
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/** Room-строитель приходит от платформы — как и у `:shared`, единственное место с `expect`. */
expect val fishingPlatformModule: Module

val fishingDataModule = module {
    single<FishingDatabase> { buildFishingDatabase(get<RoomDatabase.Builder<FishingDatabase>>()) }
    single { get<FishingDatabase>().tripDao() }
    single { get<FishingDatabase>().trackPointDao() }
    single { get<FishingDatabase>().speciesDao() }
    single { get<FishingDatabase>().catchDao() }

    single<TripRepository> { TripRepositoryImpl(get()) }
    single<TripTrackPointRepository> { TripTrackPointRepositoryImpl(get()) }
    single<SpeciesRepository> { SpeciesRepositoryImpl(get()) }
    single<CatchRepository> { CatchRepositoryImpl(get()) }
    single { FishingSettingsRepository(get()) }

    single { FishCatalogSource() }

    // Провайдер подсказки погоды — ВЫБИРАЕМЫЙ. Open-Meteo бесплатен только для некоммерческого
    // использования (проверено 2026-09-27), поэтому перед публикацией с рекламой владелец либо
    // берёт платный план, либо другой источник, либо оставляет `NoWeatherSuggestions`: приложение
    // работоспособно и без подсказки, погоду вписывает человек. `HttpTextFetcher` — из `:shared`.
    single<WeatherSuggestionSource> { OpenMeteoWeatherSource(get()) }
}

val fishingDomainModule = module {
    factory { StartTripUseCase(get()) }
    factory { FinishTripUseCase(get(), get()) }
    factory { RecordTripPointUseCase(get(), get()) }
    factory { AddCatchUseCase(get()) }
    factory { DeleteTripUseCase(get()) }
    factory { EnsureFishSpeciesUseCase(get(), get()) }
}

val fishingPresentationModule = module {
    viewModel {
        TripViewModel(
            get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
        )
    }
    viewModel { FishArchiveViewModel(get(), get()) }
    viewModel { TripDetailViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { CatchMapViewModel(get(), get(), get()) }
    viewModel { FishSettingsViewModel(get(), get(), get()) }
}

/**
 * Запуск Koin рыбацкого хоста.
 *
 * Сначала поднимается грибной [initKoin] — и это НЕ недосмотр: в нём живёт `platformModule` со всем
 * платформенным (GPS, компас, фоновая запись, хранилище фото, снапшоттер миниатюр, DataStore,
 * MapLibre-офлайн) и `dataModule` с настройками языка, оформления и кешем стиля карты. Рыбацкий
 * продукт переиспользует это целиком — в этом и смысл аддитивной сборки.
 *
 * Грибная база при этом НЕ создаётся: `single` в Koin ленивы, а грибной `App()` здесь не зовётся,
 * так что запросить `LeshyDatabase` некому. Редакция передаётся мировая: рыбацкая российская
 * редакция — отдельный разговор (`.claude/plans/product-family.md`).
 */
fun initFishingKoin(appDeclaration: KoinAppDeclaration? = null) {
    initKoin(Edition.WORLD) { appDeclaration?.invoke(this) }
    loadKoinModules(
        listOf(fishingPlatformModule, fishingDataModule, fishingDomainModule, fishingPresentationModule)
    )
}
