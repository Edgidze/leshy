package compose.project.leshy.di

import compose.project.leshy.presentation.archive.ArchiveViewModel
import compose.project.leshy.presentation.archive.WalkDetailViewModel
import compose.project.leshy.presentation.map.MapViewModel
import compose.project.leshy.presentation.record.RecordViewModel
import compose.project.leshy.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { RecordViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ArchiveViewModel(get()) }
    viewModel { params -> WalkDetailViewModel(params.get(), get(), get(), get(), get()) }
    viewModel { MapViewModel(get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
}
