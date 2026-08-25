package leshy.mushrooms.map.di

import leshy.mushrooms.map.domain.usecase.AddMushroomMarkUseCase
import leshy.mushrooms.map.domain.usecase.AddPlaceMarkUseCase
import leshy.mushrooms.map.domain.usecase.BackfillWalkThumbnailsUseCase
import leshy.mushrooms.map.domain.usecase.CreateOrUpdateUserSpeciesUseCase
import leshy.mushrooms.map.domain.usecase.DeletePlaceMarkUseCase
import leshy.mushrooms.map.domain.usecase.DeleteUserSpeciesUseCase
import leshy.mushrooms.map.domain.usecase.EnsureDefaultCategoriesUseCase
import leshy.mushrooms.map.domain.usecase.EnsureDefaultCollectionsUseCase
import leshy.mushrooms.map.domain.usecase.ExportDataUseCase
import leshy.mushrooms.map.domain.usecase.FinishWalkUseCase
import leshy.mushrooms.map.domain.usecase.HealOrphanedWalksUseCase
import leshy.mushrooms.map.domain.usecase.ImportDataUseCase
import leshy.mushrooms.map.domain.usecase.RecalculateFilterEligibilityUseCase
import leshy.mushrooms.map.domain.usecase.RecordTrackPointUseCase
import leshy.mushrooms.map.domain.usecase.RefreshMapDataUseCase
import leshy.mushrooms.map.domain.usecase.RemoveLastMushroomMarkUseCase
import leshy.mushrooms.map.domain.usecase.RenameWalkUseCase
import leshy.mushrooms.map.domain.usecase.SaveCategoryIconUseCase
import leshy.mushrooms.map.domain.usecase.RepairPhotoPathsUseCase
import leshy.mushrooms.map.domain.usecase.SetCategoryPickedUseCase
import leshy.mushrooms.map.domain.usecase.SetCollectionPickedUseCase
import leshy.mushrooms.map.domain.usecase.StartWalkUseCase
import leshy.mushrooms.map.domain.usecase.ToggleUserSpeciesVisibilityUseCase
import leshy.mushrooms.map.domain.usecase.UpdatePlaceMarkUseCase
import leshy.mushrooms.map.domain.usecase.UpdateWalkThumbnailUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { EnsureDefaultCategoriesUseCase(get(), get(), get()) }
    factory { EnsureDefaultCollectionsUseCase(get(), get(), get(), get()) }
    factory { RecalculateFilterEligibilityUseCase(get(), get()) }
    factory { SetCollectionPickedUseCase(get(), get(), get()) }
    factory { SetCategoryPickedUseCase(get(), get()) }
    factory { SaveCategoryIconUseCase(get(), get()) }
    factory { StartWalkUseCase(get()) }
    factory { FinishWalkUseCase(get()) }
    factory { HealOrphanedWalksUseCase(get(), get(), get()) }
    factory { RenameWalkUseCase(get()) }
    factory { UpdateWalkThumbnailUseCase(get()) }
    factory { BackfillWalkThumbnailsUseCase(get(), get(), get(), get(), get()) }
    factory { RepairPhotoPathsUseCase(get(), get()) }
    factory { RecordTrackPointUseCase(get(), get()) }
    factory { AddMushroomMarkUseCase(get(), get()) }
    factory { RemoveLastMushroomMarkUseCase(get(), get()) }
    factory { AddPlaceMarkUseCase(get(), get()) }
    factory { UpdatePlaceMarkUseCase(get()) }
    factory { DeletePlaceMarkUseCase(get()) }
    factory { ExportDataUseCase(get(), get(), get(), get(), get()) }
    factory { ImportDataUseCase(get(), get(), get(), get(), get()) }
    factory { RefreshMapDataUseCase(get(), get()) }
    factory { CreateOrUpdateUserSpeciesUseCase(get(), get()) }
    factory { ToggleUserSpeciesVisibilityUseCase(get()) }
    factory { DeleteUserSpeciesUseCase(get(), get(), get()) }
}
