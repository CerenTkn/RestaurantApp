package com.cerentekin.restaurantorderapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cerentekin.restaurantorderapp.R
import com.cerentekin.restaurantorderapp.databinding.PopularMenuItemBinding
import com.cerentekin.restaurantorderapp.models.TopMenuItem

class PopularMenuAdapter(
    private val menuItems: List<TopMenuItem>,
) : RecyclerView.Adapter<PopularMenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(private val binding: PopularMenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TopMenuItem) {
            binding.itemName.text = item.menu_item
            binding.itemQuantity.text = "Ordered: ${item.total_quantity}"
            // Glide ile resim yükleme

            Log.d("Glide", "Loading image from URL: ${item.image_url}")
            Glide.with(binding.itemIcon.context)
                .load(item.image_url) // `imageUrl` API'den dönen bir URL olmalı
                .placeholder(R.drawable.placeholder_image) // Yüklenirken gösterilecek resim
                .error(R.drawable.error_image) // Hata durumunda gösterilecek resim
                .into(binding.itemIcon)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = PopularMenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menuItems[position])
    }

    override fun getItemCount(): Int = menuItems.size
}
