package at.sunilson.stylishmaps.maps

import at.sunilson.stylishmaps.R
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mapsModule = module {
    viewModel<MapsViewModel> { MapsViewModelImpl(get(named("Styles")), get(), get()) }
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }
    single(named("Styles")) {
        setOf(
            R.raw.minimal,
            R.raw.dark,
            R.raw.vintage,
            R.raw.mondrian,
            R.raw.neutral_blue,
            R.raw.red_blue,
            R.raw.classic,
            R.raw.zombie,
            R.raw.dark_electric,
            R.raw.vintage_blue_yellow,
            R.raw.classic,
            R.raw.roads,
            R.raw.pale_dawn,
            R.raw.ultra_light,
            R.raw.countries,
            R.raw.navigation,
            R.raw.noli,
            R.raw.cobalt,
            R.raw.multi_brand,
            R.raw.light_and_dark,
            R.raw.red_alert,
            R.raw.two_tone,
            R.raw.flat_design,
            R.raw.greyworld,
            R.raw.old_dry,
            R.raw.pink,
            R.raw.transport,
            R.raw.purple
        ).toList()
    }
}