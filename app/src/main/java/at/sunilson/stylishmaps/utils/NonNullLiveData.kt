package at.sunilson.stylishmaps.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class NonNullLiveData<T>(initValue: T) : MutableLiveData<T>() {

    init {
        value = initValue
    }

    @Suppress("UnsafeCallOnNullableType")
    override fun getValue(): T {
        return super.getValue()!!
    }

    override fun setValue(value: T) {
        super.setValue(value)
    }

    @Suppress("UnsafeCallOnNullableType")
    fun observe(owner: LifecycleOwner, body: (T) -> Unit) {
        observe(owner, Observer<T> { t -> body(t!!) })
    }

    override fun postValue(value: T) {
        super.postValue(value)
    }
}
