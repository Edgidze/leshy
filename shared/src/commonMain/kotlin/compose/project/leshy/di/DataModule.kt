package compose.project.leshy.di

import androidx.room.RoomDatabase
import compose.project.leshy.data.catalog.CatalogSource
import compose.project.leshy.data.local.LeshyDatabase
import compose.project.leshy.data.local.getRoomDatabase
import compose.project.leshy.data.repository.CatalogStateRepositoryImpl
import compose.project.leshy.data.repository.CategoryRepositoryImpl
import compose.project.leshy.data.repository.CollectionRepositoryImpl
import compose.project.leshy.data.repository.FieldMarkRepositoryImpl
import compose.project.leshy.data.repository.MapFilterRepositoryImpl
import compose.project.leshy.data.repository.MapStyleCacheRepository
import compose.project.leshy.data.repository.OfflineRegionRepositoryImpl
import compose.project.leshy.data.repository.OnboardingRepositoryImpl
import compose.project.leshy.data.repository.SettingsRepositoryImpl
import compose.project.leshy.data.repository.TrackPointRepositoryImpl
import compose.project.leshy.data.repository.WalkRepositoryImpl
import compose.project.leshy.domain.repository.CatalogStateRepository
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.CollectionRepository
import compose.project.leshy.domain.repository.FieldMarkRepository
import compose.project.leshy.domain.repository.MapFilterRepository
import compose.project.leshy.domain.repository.OfflineRegionRepository
import compose.project.leshy.domain.repository.OnboardingRepository
import compose.project.leshy.domain.repository.SettingsRepository
import compose.project.leshy.domain.repository.TrackPointRepository
import compose.project.leshy.domain.repository.WalkRepository
import compose.project.leshy.i18n.MushroomNames
import org.koin.dsl.module

val dataModule = module {
    single { CatalogSource() }
    single { MushroomNames() }
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
