package at.sunilson.stylishmaps.maps

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.stylishmaps.base.BaseViewModel
import at.sunilson.stylishmaps.data.Repository
import at.sunilson.stylishmaps.data.entities.MapStyle
import at.sunilson.stylishmaps.data.entities.SearchResult
import com.github.kittinunf.result.coroutines.success
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class MapsCommands {
    data class MoveMap(val lat: Double, val lng: Double) : MapsCommands()
    object ChooseStyle : MapsCommands()
    object TakePicture : MapsCommands()
}

abstract class MapsViewModel : BaseViewModel<MapsCommands>() {
    abstract val style: MutableLiveData<MapStyle>
    abstract val searchResults: MutableLiveData<List<SearchResult>>
    abstract val searching: MutableLiveData<Boolean>
    abstract val styles: List<MapStyle>
    abstract val currentCapture: MutableLiveData<Bitmap>

    abstract fun chooseStyle()
    abstract fun export()
    abstract fun locateUser()
    abstract fun search(query: String)
}

internal class MapsViewModelImpl(
    override val styles: List<MapStyle>,
    private val locationClient: FusedLocationProviderClient,
    private val repository: Repository
) : MapsViewModel() {

    override val style = MutableLiveData<MapStyle>()
    override val searchResults = MutableLiveData<List<SearchResult>>()
    override val searching = MutableLiveData<Boolean>()
    override val currentCapture = MutableLiveData<Bitmap>()

    private var searchJob: Job? = null

    override fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            searching.value = true
            repository.searchForLocation(query).success { searchResults.value = it }
            searching.value = false
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
            commands.value = MapsCommands.MoveMap(it.latitude, it.longitude)
        }
    }
}