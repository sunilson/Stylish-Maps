package at.sunilson.stylishmaps.maps

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.stylishmaps.base.BaseViewModel
import at.sunilson.stylishmaps.data.Repository
import at.sunilson.stylishmaps.data.entities.Location
import at.sunilson.stylishmaps.data.entities.MapStyle
import at.sunilson.stylishmaps.data.entities.SearchResult
import at.sunilson.stylishmaps.utils.BitmapUtils
import com.github.kittinunf.result.coroutines.success
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

data class MapsState(
    val style: MapStyle? = null,
    val searchResults: List<SearchResult>? = null,
    val searching: Boolean = false,
    val query: String? = null,
    val currentCapture: Bitmap? = null,
    val currentLocation: Location? = null
)

sealed class MapsCommands {
    object ChooseStyle : MapsCommands()
    object TakePicture : MapsCommands()
    data class ExportPicture(val uri: Uri) : MapsCommands()
    data class MoveMap(val location: Location) : MapsCommands()
    data class SetMapStyle(val style: MapStyle) : MapsCommands()
}

abstract class MapsViewModel : BaseViewModel<MapsCommands, MapsState>(MapsState()) {
    abstract fun chooseStyle()
    abstract fun export()
    abstract fun locateUser()
    abstract fun searchResultSelected(searchResult: SearchResult)
    abstract fun search(query: String)
    abstract fun pictureTaken(bitmap: Bitmap)
    abstract fun setLocation(location: Location)
    abstract fun setStyle(mapStyle: MapStyle)
    abstract fun restoreStyleFromResource(resource: Int)

    abstract val styles: List<MapStyle>
}

internal class MapsViewModelImpl(
    override val styles: List<MapStyle>,
    private val locationClient: FusedLocationProviderClient,
    private val repository: Repository
) : MapsViewModel() {
    private var searchJob: Job? = null

    override fun search(query: String) {
        setState { copy(query = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            if (query.length < 3) setState { copy(searchResults = null) }
            else {
                setState { copy(searching = true) }
                repository.searchForLocation(query)
                    .success { setState { copy(searchResults = it) } }
                setState { copy(searching = false) }
            }
        }
    }

    override fun searchResultSelected(searchResult: SearchResult) {
        commands.value = MapsCommands.MoveMap(searchResult.location)
        setState {
            copy(
                currentLocation = searchResult.location,
                searchResults = null,
                query = null
            )
        }
    }

    override fun chooseStyle() {
        commands.value = MapsCommands.ChooseStyle
    }

    override fun export() {
        commands.value = MapsCommands.TakePicture
    }

    override fun locateUser() {
        locationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                val location = Location(it.latitude, it.longitude)
                commands.value = MapsCommands.MoveMap(location)
                setState { copy(currentLocation = location) }
            }
        }
    }

    override fun setLocation(location: Location) {
        commands.value = MapsCommands.MoveMap(location)
        setState { copy(currentLocation = location) }
    }

    override fun setStyle(mapStyle: MapStyle) {
        getState {
            if (mapStyle != it.style) {
                commands.value = MapsCommands.SetMapStyle(mapStyle)
                setState { copy(style = mapStyle) }
            }
        }
    }

    override fun restoreStyleFromResource(resource: Int) {
        setState { copy(style = styles.firstOrNull { it.jsonResource == resource }) }
    }

    override fun pictureTaken(bitmap: Bitmap) {
        viewModelScope.launch {
            repository.cacheImage(bitmap).fold(
                {
                    commands.value = MapsCommands.ExportPicture(it)
                },
                {
                    //TODO
                }
            )
        }
    }
}