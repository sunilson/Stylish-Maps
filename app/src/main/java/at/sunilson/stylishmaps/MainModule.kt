package at.sunilson.stylishmaps

import at.sunilson.stylishmaps.utils.BitmapUtils
import at.sunilson.stylishmaps.utils.BitmapUtilsImplementation
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mainModule = module {
    single(named("mapToken")) { androidContext().getString(R.string.mapbox_access_token) }
    single<BitmapUtils> { BitmapUtilsImplementation(androidApplication()) }
}