package at.sunilson.stylishmaps.export

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.stylishmaps.R
import at.sunilson.stylishmaps.base.BaseViewModel
import at.sunilson.stylishmaps.data.Repository
import kotlinx.coroutines.launch

data class ExportState(
    val image: Uri? = null,
    val cropping: Boolean = false
)

sealed class ExportCommand {
    object CropImage : ExportCommand()
    object NavigateBack : ExportCommand()
    data class ShowToast(val text: Int) : ExportCommand()
    data class ShareUri(val uri: Uri) : ExportCommand()
    data class DownloadUri(val uri: Uri) : ExportCommand()
    data class SetWallpaper(val bitmap: Bitmap) : ExportCommand()
}

abstract class ExportViewModel : BaseViewModel<ExportCommand, ExportState>(ExportState()) {
    abstract fun setWallpaper()
    abstract fun share()
    abstract fun startCrop()
    abstract fun finishCrop()
    abstract fun download()
    abstract fun saveToDownload()
    abstract fun processBitmap(bitmap: Bitmap)
    abstract fun setImage(uri: Uri)
    abstract fun backPressed()
}

internal class ExportViewModelImpl(private val repository: Repository) : ExportViewModel() {

    override fun backPressed() {
        getState {
            when {
                it.cropping -> setState { copy(cropping = false) }
                else -> commands.value = ExportCommand.NavigateBack
            }
        }
    }

    override fun setImage(uri: Uri) {
        setState { copy(image = uri) }
    }

    override fun share() {
        getState {
            commands.value = ExportCommand.ShareUri(it.image ?: return@getState)
        }
    }

    override fun saveToDownload() {
        getState {
            repository.saveToGallery(it.image ?: return@getState).fold(
                { commands.value = ExportCommand.ShowToast(R.string.download_finished) },
                { commands.value = ExportCommand.ShowToast(R.string.download_finished) }
            )
        }
    }

    override fun download() {
        getState {
            setState { copy(cropping = false) }
            commands.value = ExportCommand.DownloadUri(it.image ?: return@getState)
        }
    }

    override fun startCrop() {
        setState { copy(cropping = true) }
    }

    override fun finishCrop() {
        setState { copy(cropping = false) }
        commands.value = ExportCommand.CropImage
    }

    override fun processBitmap(bitmap: Bitmap) {
        setState {
            repository.cacheImage(bitmap).fold(
                { copy(image = it) },
                { this }
            )
        }
    }

    override fun setWallpaper() {
        getState {
            setState { copy(cropping = false) }
            repository.getImage(it.image ?: return@getState).fold(
                {
                    commands.value = ExportCommand.SetWallpaper(it)
                },
                {
                    //TODO
                }
            )
        }
    }
}