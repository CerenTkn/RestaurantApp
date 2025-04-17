package com.cerentekin.restaurantorderapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cerentekin.restaurantorderapp.databinding.ItemDashboardBinding
import com.cerentekin.restaurantorderapp.models.DashboardItem

class DashboardAdapter(
    private val items: List<DashboardItem>,
    private val onItemClick: (DashboardItem) -> Unit
) : RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {

    inner class DashboardViewHolder(val binding: ItemDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DashboardItem) {
            binding.titleTextView.text = item.title
            binding.iconImageView.setImageResource(item.icon)

            binding.root.setOnClickListener {
                onItemClick(item) // Tıklama olayını yönlendir
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val binding = ItemDashboardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DashboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
