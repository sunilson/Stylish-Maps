package at.sunilson.stylishmaps.base

import androidx.lifecycle.ViewModel
import at.sunilson.stylishmaps.utils.SingleLiveData

abstract class BaseViewModel<Command> : ViewModel() {
    val commands: SingleLiveData<Command> = SingleLiveData()
}