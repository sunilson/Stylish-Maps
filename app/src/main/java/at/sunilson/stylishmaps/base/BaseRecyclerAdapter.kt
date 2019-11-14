package at.sunilson.stylishmaps.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerAdapter<T, Binding : ViewDataBinding>(initialData: List<T> = listOf()) :
    RecyclerView.Adapter<BaseRecyclerAdapter<T, Binding>.ViewHolder>() {

    protected val _data = mutableListOf<T>()

    init {
        setItems(initialData)
    }


    fun setItems(items: List<T>) {
        _data.clear()
        _data.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = _data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(_data[position])
    }

    abstract fun bind(binding: Binding, obj: T)

    fun getPositionForItem(item: T) = _data.indexOf(item)

    open inner class ViewHolder(private val binding: Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(obj: T) {
            bind(binding, obj)
            binding.executePendingBindings()
        }
    }
}