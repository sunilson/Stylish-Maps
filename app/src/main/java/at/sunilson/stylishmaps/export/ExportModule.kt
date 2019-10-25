package at.sunilson.stylishmaps.export

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val exportModule = module {
    viewModel<ExportViewModel> { ExportViewModelImpl(get(named("mapToken"))) }
}