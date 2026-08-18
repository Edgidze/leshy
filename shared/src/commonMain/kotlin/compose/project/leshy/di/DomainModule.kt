package compose.project.leshy.di

import compose.project.leshy.domain.usecase.AddMushroomMarkUseCase
import compose.project.leshy.domain.usecase.AddPlaceMarkUseCase
import compose.project.leshy.domain.usecase.BackfillWalkThumbnailsUseCase
import compose.project.leshy.domain.usecase.DeletePlaceMarkUseCase
import compose.project.leshy.domain.usecase.EnsureDefaultCategoriesUseCase
import compose.project.leshy.domain.usecase.EnsureDefaultCollectionsUseCase
import compose.project.leshy.domain.usecase.ExportDataUseCase
import compose.project.leshy.domain.usecase.FinishWalkUseCase
import compose.project.leshy.domain.usecase.ImportDataUseCase
import compose.project.leshy.domain.usecase.RecalculateFilterEligibilityUseCase
import compose.project.leshy.domain.usecase.RecordTrackPointUseCase
import compose.project.leshy.domain.usecase.RemoveLastMushroomMarkUseCase
import compose.project.leshy.domain.usecase.RenameWalkUseCase
import compose.project.leshy.domain.usecase.SetCategoryPickedUseCase
import compose.project.leshy.domain.usecase.SetCollectionPickedUseCase
import compose.project.leshy.domain.usecase.StartWalkUseCase
import compose.project.leshy.domain.usecase.UpdatePlaceMarkUseCase
import compose.project.leshy.domain.usecase.UpdateWalkThumbnailUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { EnsureDefaultCategoriesUseCase(get()) }
    factory { EnsureDefaultCollectionsUseCase(get(), get()) }
    factory { RecalculateFilterEligibilityUseCase(get(), get()) }
    factory { SetCollectionPickedUseCase(get(), get(), get()) }
    factory { SetCategoryPickedUseCase(get(), get()) }
    factory { StartWalkUseCase(get()) }
    factory { FinishWalkUseCase(get()) }
    factory { RenameWalkUseCase(get()) }
    factory { UpdateWalkThumbnailUseCase(get()) }
    factory { BackfillWalkThumbnailsUseCase(get(), get(), get(), get(), get()) }
    factory { RecordTrackPointUseCase(get(), get()) }
    factory { AddMushroomMarkUseCase(get(), get()) }
    factory { RemoveLastMushroomMarkUseCase(get(), get()) }
    factory { AddPlaceMarkUseCase(get(), get()) }
    factory { UpdatePlaceMarkUseCase(get()) }
    factory { DeletePlaceMarkUseCase(get()) }
    factory { ExportDataUseCase(get(), get(), get(), get()) }
    factory { ImportDataUseCase(get(), get(), get(), get(), get()) }
}
