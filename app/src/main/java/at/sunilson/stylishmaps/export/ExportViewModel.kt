package at.sunilson.stylishmaps.export

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import at.sunilson.stylishmaps.base.BaseViewModel

sealed class ExportCommand {

}

abstract class ExportViewModel : BaseViewModel<ExportCommand>() {
    abstract val image: MutableLiveData<Bitmap>
}

internal class ExportViewModelImpl(private val accessToken: String) : ExportViewModel() {
    override val image = MutableLiveData<Bitmap>()
}