package at.sunilson.stylishmaps.export

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.stylishmaps.R
import at.sunilson.stylishmaps.base.BaseViewModel
import at.sunilson.stylishmaps.data.Repository
import kotlinx.coroutines.launch

sealed class ExportCommand {
    object CropImage : ExportCommand()
    data class ShowToast(val text: Int) : ExportCommand()
    data class ShareUri(val uri: Uri) : ExportCommand()
    data class DownloadUri(val uri: Uri) : ExportCommand()
    data class SetWallpaper(val bitmap: Bitmap) : ExportCommand()
}

abstract class ExportViewModel : BaseViewModel<ExportCommand>() {
    abstract val image: MutableLiveData<Uri>
    abstract val cropping: MutableLiveData<Boolean>

    abstract fun setWallpaper()
    abstract fun share()
    abstract fun startCrop()
    abstract fun finishCrop()
    abstract fun download()
    abstract fun saveToDownload()
    abstract fun processBitmap(bitmap: Bitmap)
}

internal class ExportViewModelImpl(private val repository: Repository) : ExportViewModel() {
    override val image = MutableLiveData<Uri>()
    override val cropping = MutableLiveData<Boolean>()

    override fun share() {
        commands.value = ExportCommand.ShareUri(image.value ?: return)
    }

    override fun saveToDownload() {
        viewModelScope.launch {
            repository.saveToGallery(image.value ?: return@launch).fold(
                { commands.value = ExportCommand.ShowToast(R.string.download_finished) },
                {commands.value = ExportCommand.ShowToast(R.string.download_finished)}
            )
        }
    }

    override fun download() {
        cropping.value = false
        commands.value = ExportCommand.DownloadUri(image.value ?: return)
    }

    override fun startCrop() {
        cropping.value = true
    }

    override fun finishCrop() {
        cropping.value = false
        commands.value = ExportCommand.CropImage
    }

    override fun processBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            repository.cacheImage(bitmap).fold(
                {
                    image.value = it
                },
                {
                    //TODO
                }
            )
        }
    }

    override fun setWallpaper() {
        cropping.value = false
        val uri = image.value ?: return
        viewModelScope.launch {
            repository.getImage(uri).fold(
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