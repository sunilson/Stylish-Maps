package at.sunilson.stylishmaps.maps

import android.net.Uri
import at.sunilson.stylishmaps.R
import at.sunilson.stylishmaps.data.entities.MapStyle
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mapsModule = module {
    viewModel<MapsViewModel> { MapsViewModelImpl(get(), get(), get()) }
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }
    single {
        listOf(
            MapStyle(R.raw.minimal, Uri.parse("")),
            MapStyle(R.raw.dark, Uri.parse("")),
            MapStyle(R.raw.vintage, Uri.parse("")),
            MapStyle(R.raw.propia, Uri.parse("")),
            MapStyle(R.raw.mondrian, Uri.parse("")),
            MapStyle(R.raw.neutral_blue, Uri.parse("")),
            MapStyle(R.raw.red_blue, Uri.parse("")),
            MapStyle(R.raw.classic, Uri.parse("")),
            MapStyle(R.raw.zombie, Uri.parse("")),
            MapStyle(R.raw.dark_electric, Uri.parse("")),
            MapStyle(R.raw.vintage_blue_yellow, Uri.parse(""))
        )
    }
}