package leshy.mushrooms.map.di

import leshy.mushrooms.map.presentation.archive.ArchiveViewModel
import leshy.mushrooms.map.presentation.archive.WalkDetailViewModel
import leshy.mushrooms.map.presentation.data.DataViewModel
import leshy.mushrooms.map.presentation.map.MapViewModel
import leshy.mushrooms.map.presentation.mapfilter.MapFilterViewModel
import leshy.mushrooms.map.presentation.onboarding.OnboardingViewModel
import leshy.mushrooms.map.presentation.preparation.PreparationViewModel
import leshy.mushrooms.map.presentation.record.RecordViewModel
import leshy.mushrooms.map.presentation.settings.SettingsViewModel
import leshy.mushrooms.map.presentation.species.SpeciesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel {
        RecordViewModel(
            get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
            get(), get(), get(), get(), get(), get(), get(), get(),
        )
    }
    viewModel { ArchiveViewModel(get(), get(), get(), get()) }
    viewModel { params -> WalkDetailViewModel(params.get(), get(), get(), get(), get(), get(), get()) }
    viewModel { MapViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel {
        SettingsViewModel(get(), get(), get(), get(), get(), get(), get())
    }
    viewModel {
        SpeciesViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
    }
    viewModel { MapFilterViewModel(get(), get(), get(), get()) }
    viewModel { DataViewModel(get(), get(), get(), get(), get()) }
    viewModel { PreparationViewModel(get()) }
    viewModel { OnboardingViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}
