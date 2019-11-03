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

sealed class MapsCommands {
    object ChooseStyle : MapsCommands()
    object TakePicture : MapsCommands()
    data class ExportPicture(val uri: Uri) : MapsCommands()
}

abstract class MapsViewModel : BaseViewModel<MapsCommands>() {
    abstract val style: MutableLiveData<MapStyle>
    abstract val searchResults: MutableLiveData<List<SearchResult>>
    abstract val searching: MutableLiveData<Boolean>
    abstract val query: MutableLiveData<String>
    abstract val styles: List<MapStyle>
    abstract val currentCapture: MutableLiveData<Bitmap>
    abstract val currentLocation: MutableLiveData<Location>

    abstract fun chooseStyle()
    abstract fun export()
    abstract fun locateUser()
    abstract fun searchResultSelected(searchResult: SearchResult)
    abstract fun search(query: String)
    abstract fun pictureTaken(bitmap: Bitmap)
    abstract fun restoreStyleFromResource(resource: Int)
}

internal class MapsViewModelImpl(
    override val styles: List<MapStyle>,
    private val locationClient: FusedLocationProviderClient,
    private val repository: Repository
) : MapsViewModel() {

    override val style = MutableLiveData<MapStyle>()
    override val searchResults = MutableLiveData<List<SearchResult>>()
    override val searching = MutableLiveData<Boolean>()
    override val query = MutableLiveData<String>()
    override val currentCapture = MutableLiveData<Bitmap>()
    override val currentLocation = MutableLiveData<Location>()

    private var searchJob: Job? = null

    override fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            if (query.length < 3) searchResults.value = null
            else {
                searching.value = true
                repository.searchForLocation(query).success { searchResults.value = it }
                searching.value = false
            }
        }
    }

    override fun searchResultSelected(searchResult: SearchResult) {
        currentLocation.value = searchResult.location
        searchResults.value = null
        query.value = null
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
                currentLocation.value = Location(it.latitude, it.longitude)
            }
        }
    }

    override fun restoreStyleFromResource(resource: Int) {
        style.value = styles.firstOrNull { it.jsonResource == resource } ?: return
    }

    override fun pictureTaken(bitmap: Bitmap) {
        viewModelScope.launch {
            repository.cacheImage(bitmap).fold(
                {
                    commands.value = MapsCommands.ExportPicture(it)
                },
                {
                    //TODO
                    val test = ""
                }
            )
        }
    }
}