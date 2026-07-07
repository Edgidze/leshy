package compose.project.leshy.di

import compose.project.leshy.domain.usecase.AddMushroomMarkUseCase
import compose.project.leshy.domain.usecase.AddPhotoMarkUseCase
import compose.project.leshy.domain.usecase.EnsureDefaultCategoriesUseCase
import compose.project.leshy.domain.usecase.FinishWalkUseCase
import compose.project.leshy.domain.usecase.RecordTrackPointUseCase
import compose.project.leshy.domain.usecase.RemoveLastMushroomMarkUseCase
import compose.project.leshy.domain.usecase.StartWalkUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { EnsureDefaultCategoriesUseCase(get()) }
    factory { StartWalkUseCase(get()) }
    factory { FinishWalkUseCase(get()) }
    factory { RecordTrackPointUseCase(get(), get()) }
    factory { AddMushroomMarkUseCase(get(), get()) }
    factory { RemoveLastMushroomMarkUseCase(get(), get()) }
    factory { AddPhotoMarkUseCase(get(), get()) }
}
