package at.sunilson.stylishmaps.maps.styleList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import at.sunilson.stylishmaps.R
import at.sunilson.stylishmaps.base.BaseRecyclerAdapter
import at.sunilson.stylishmaps.databinding.StyleListItemBinding
import com.google.android.gms.maps.model.MapStyleOptions

class StyleListRecyclerAdapter(
    private val onStyleClicked: (Int) -> Unit
) : BaseRecyclerAdapter<Int, StyleListItemBinding>() {

    private var selectedItem = -1
        set(value) {
            val previousIndex = _data.indexOf(field)
            val newIndex = _data.indexOf(value)
            field = value
            notifyItemChanged(previousIndex)
            notifyItemChanged(newIndex)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<StyleListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.style_list_item,
            parent,
            false
        )
        binding.styleMap.onCreate(null)
        return ViewHolder(binding)
    }

    override fun bind(binding: StyleListItemBinding, obj: Int) {
        binding.mapStyle = obj
        binding.selected = obj == selectedItem
        binding.styleMap.getMapAsync {
            it.setOnMapClickListener {
                selectedItem = obj
                onStyleClicked(obj)
            }
            it.uiSettings.isMapToolbarEnabled = false
            it.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    binding.root.context,
                    obj
                )
            )
        }
    }
}