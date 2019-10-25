package at.sunilson.stylishmaps

import android.app.Application
import at.sunilson.stylishmaps.data.dataModule
import at.sunilson.stylishmaps.export.exportModule
import at.sunilson.stylishmaps.maps.mapsModule
import com.mapbox.mapboxsdk.Mapbox
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import timber.log.Timber

class MainApplication : Application() {

    private val mapToken: String by inject(named("mapToken"))

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(
                listOf(
                    mainModule,
                    dataModule,
                    mapsModule,
                    exportModule
                )
            )
        }

        Timber.plant(Timber.DebugTree())

        Mapbox.getInstance(applicationContext, mapToken)
    }
}