package at.sunilson.stylishmaps.maps.searchList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import at.sunilson.stylishmaps.R
import at.sunilson.stylishmaps.base.BaseRecyclerAdapter
import at.sunilson.stylishmaps.data.entities.SearchResult
import at.sunilson.stylishmaps.databinding.SearchListItemBinding

class SearchListRecyclerAdapter(
    private val onItemClicked: (SearchResult) -> Unit
) : BaseRecyclerAdapter<SearchResult, SearchListItemBinding>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<SearchListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.search_list_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun bind(binding: SearchListItemBinding, obj: SearchResult) {
        binding.searchResult = obj
        binding.root.setOnClickListener { onItemClicked(obj) }
    }
}