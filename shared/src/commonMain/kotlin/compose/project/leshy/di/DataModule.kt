package compose.project.leshy.di

import androidx.room.RoomDatabase
import compose.project.leshy.data.local.LeshyDatabase
import compose.project.leshy.data.local.getRoomDatabase
import compose.project.leshy.data.repository.CategoryRepositoryImpl
import compose.project.leshy.data.repository.FieldMarkRepositoryImpl
import compose.project.leshy.data.repository.TrackPointRepositoryImpl
import compose.project.leshy.data.repository.WalkRepositoryImpl
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.FieldMarkRepository
import compose.project.leshy.domain.repository.TrackPointRepository
import compose.project.leshy.domain.repository.WalkRepository
import org.koin.dsl.module

val dataModule = module {
    single { getRoomDatabase(get<RoomDatabase.Builder<LeshyDatabase>>()) }
    single { get<LeshyDatabase>().categoryDao() }
    single { get<LeshyDatabase>().walkDao() }
    single { get<LeshyDatabase>().objectDao() }
    single { get<LeshyDatabase>().trackPointDao() }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<WalkRepository> { WalkRepositoryImpl(get()) }
    single<FieldMarkRepository> { FieldMarkRepositoryImpl(get()) }
    single<TrackPointRepository> { TrackPointRepositoryImpl(get()) }
}
