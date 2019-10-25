package at.sunilson.stylishmaps.maps.styleList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import at.sunilson.stylishmaps.R
import at.sunilson.stylishmaps.base.BaseRecyclerAdapter
import at.sunilson.stylishmaps.data.entities.MapStyle
import at.sunilson.stylishmaps.databinding.StyleListItemBinding
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions

class StyleListRecyclerAdapter(
    private val onStyleClicked: (MapStyle) -> Unit
) :
    BaseRecyclerAdapter<MapStyle, StyleListItemBinding>() {

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

    override fun bind(binding: StyleListItemBinding, obj: MapStyle) {
        binding.mapStyle = obj
        binding.styleMap.getMapAsync {
            it.setOnMapClickListener { onStyleClicked(obj) }
            it.uiSettings.isMapToolbarEnabled = false
            it.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    binding.root.context,
                    obj.jsonResource
                )
            )
        }
    }
}