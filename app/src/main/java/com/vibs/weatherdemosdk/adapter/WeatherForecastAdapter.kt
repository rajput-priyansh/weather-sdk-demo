package com.vibs.weatherdemosdk.adapter

import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.vibs.weatherapisdk.models.ListItem
import com.vibs.weatherdemosdk.BR


class WeatherForecastAdapter(
    @param:LayoutRes private val layoutId: Int
) : RecyclerView.Adapter<WeatherForecastViewHolder>() {
    var items: ArrayList<ListItem> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeatherForecastViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, viewType, parent, false)
        return WeatherForecastViewHolder(binding)
    }

    private fun getLayoutIdForPosition(): Int {
        return layoutId
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutIdForPosition()
    }

    override fun onBindViewHolder(holder: WeatherForecastViewHolder, position: Int) {
        holder.bind(position, items[position], this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * Use to set List
     */
    fun setList(items: ArrayList<ListItem>) {
        this.items = items
        notifyDataSetChanged()
    }
}

class WeatherForecastViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    /**
     * Bind the objects with UI
     */
    fun bind(
        position: Int,
        item: ListItem,
        adapter: WeatherForecastAdapter
    ) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.position, position)
        binding.setVariable(BR.adapter, adapter)
        binding.executePendingBindings()
    }
}