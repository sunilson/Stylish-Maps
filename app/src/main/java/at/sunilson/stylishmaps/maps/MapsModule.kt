package at.sunilson.stylishmaps.maps

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mapsModule = module {
    viewModel<MapsViewModel> { MapsViewModelImpl() }
}