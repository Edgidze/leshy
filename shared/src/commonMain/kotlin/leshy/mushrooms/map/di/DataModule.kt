package leshy.mushrooms.map.di

import androidx.room.RoomDatabase
import leshy.mushrooms.map.data.catalog.CatalogSource
import leshy.mushrooms.map.data.catalog.CountriesSource
import leshy.mushrooms.map.data.local.LeshyDatabase
import leshy.mushrooms.map.data.local.getRoomDatabase
import leshy.mushrooms.map.data.repository.CatalogStateRepositoryImpl
import leshy.mushrooms.map.data.repository.CategoryRepositoryImpl
import leshy.mushrooms.map.data.repository.CollectionRepositoryImpl
import leshy.mushrooms.map.data.repository.FieldMarkRepositoryImpl
import leshy.mushrooms.map.data.repository.MapFilterRepositoryImpl
import leshy.mushrooms.map.data.repository.MapStyleCacheRepository
import leshy.mushrooms.map.data.repository.OfflineRegionRepositoryImpl
import leshy.mushrooms.map.data.repository.OnboardingRepositoryImpl
import leshy.mushrooms.map.data.repository.SettingsRepositoryImpl
import leshy.mushrooms.map.data.repository.TrackPointRepositoryImpl
import leshy.mushrooms.map.data.repository.WalkRepositoryImpl
import leshy.mushrooms.map.domain.repository.CatalogStateRepository
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.CollectionRepository
import leshy.mushrooms.map.domain.repository.FieldMarkRepository
import leshy.mushrooms.map.domain.repository.MapFilterRepository
import leshy.mushrooms.map.domain.repository.OfflineRegionRepository
import leshy.mushrooms.map.domain.repository.OnboardingRepository
import leshy.mushrooms.map.domain.repository.SettingsRepository
import leshy.mushrooms.map.domain.repository.TrackPointRepository
import leshy.mushrooms.map.domain.repository.WalkRepository
import leshy.mushrooms.map.i18n.CountryNames
import leshy.mushrooms.map.i18n.MushroomNames
import org.koin.dsl.module

val dataModule = module {
    single { CatalogSource() }
    single { MushroomNames() }
    single { CountriesSource() }
    single { CountryNames() }
    single { getRoomDatabase(get<RoomDatabase.Builder<LeshyDatabase>>()) }
    single { get<LeshyDatabase>().categoryDao() }
    single { get<LeshyDatabase>().walkDao() }
    single { get<LeshyDatabase>().objectDao() }
    single { get<LeshyDatabase>().trackPointDao() }
    single { get<LeshyDatabase>().collectionDao() }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<CollectionRepository> { CollectionRepositoryImpl(get()) }
    single<WalkRepository> { WalkRepositoryImpl(get()) }
    single<FieldMarkRepository> { FieldMarkRepositoryImpl(get()) }
    single<TrackPointRepository> { TrackPointRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<MapFilterRepository> { MapFilterRepositoryImpl(get()) }
    single { MapStyleCacheRepository(get(), get(), get()) }
    single<OfflineRegionRepository> { OfflineRegionRepositoryImpl(get()) }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
    single<CatalogStateRepository> { CatalogStateRepositoryImpl(get()) }
}
