package at.sunilson.stylishmaps

import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mainModule = module {
    single(named("mapToken")) { androidContext().getString(R.string.mapbox_access_token) }
}