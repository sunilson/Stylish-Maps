package at.sunilson.stylishmaps.utils

import android.graphics.Bitmap
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import at.sunilson.stylishmaps.base.BaseRecyclerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.theartofdev.edmodo.cropper.CropImageView

interface OnTextChangedBindingListener {
    fun textChanged(text: String)
}

@BindingAdapter("app:onTextChanged")
fun EditText.onTextChangedBinding(listener: OnTextChangedBindingListener) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener.textChanged(s.toString())
        }
    })
}

@BindingAdapter("app:fromUrl")
fun ImageView.fromUrl(url: String?) {
    if (url?.isNotEmpty() == true) {
        Glide.with(context).load(url).transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
}

@BindingAdapter("app:entries")
fun <T> RecyclerView.setEntries(entries: List<T>?) {
    if (entries == null) return
    (adapter as? BaseRecyclerAdapter<T, *>)?.setItems(entries)
}

@BindingAdapter("app:hideIf")
fun View.hide(value: Boolean?) {
    visibility = if (value == true) {
        GONE
    } else {
        VISIBLE
    }
}

@BindingAdapter("app:showIf")
fun View.show(value: Boolean?) {
    visibility = if (value == true) {
        VISIBLE
    } else {
        GONE
    }
}

@BindingAdapter("android:src")
fun ImageView.bindSrc(bitmap: Bitmap) {
    setImageBitmap(bitmap)
}

@BindingAdapter("app:fromUri")
fun ImageView.fromUri(uri: Uri) {
    Glide.with(context).load(uri).into(this)
}

@BindingAdapter("app:fromUri")
fun CropImageView.fromBitmap(uri: Uri) {
    setImageUriAsync(uri)
}