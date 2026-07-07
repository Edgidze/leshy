package compose.project.leshy.di

import compose.project.leshy.presentation.archive.ArchiveViewModel
import compose.project.leshy.presentation.archive.WalkDetailViewModel
import compose.project.leshy.presentation.record.RecordViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { RecordViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ArchiveViewModel(get()) }
    viewModel { params -> WalkDetailViewModel(params.get(), get(), get(), get(), get()) }
}
