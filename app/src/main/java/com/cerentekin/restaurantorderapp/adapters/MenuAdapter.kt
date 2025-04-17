package com.cerentekin.restaurantorderapp.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cerentekin.restaurantorderapp.R
import com.cerentekin.restaurantorderapp.databinding.ItemMenuBinding
import com.cerentekin.restaurantorderapp.models.MenuItem
import android.content.Context
import android.view.View
import com.cerentekin.restaurantorderapp.activities.OrderActivity
import com.cerentekin.restaurantorderapp.activities.OrderDetailsActivity


class MenuAdapter(
    private var menuItems: List<MenuItem>,
    private val context: Context, // Doğru Context parametresi
    private val onAddToOrderClicked: (MenuItem) -> Unit,
    private val tableId: Int,
    private val viewOnly: Boolean = false // Yeni Parametre



) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = menuItems[position]
        holder.bind(item)
    }

    override fun getItemCount() = menuItems.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems: List<MenuItem>) {
        menuItems = newItems
        notifyDataSetChanged()
    }

    inner class MenuViewHolder(private val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(menuItem: MenuItem) {
            binding.itemName.text = menuItem.name
            binding.itemPrice.text = "$${menuItem.price}"

            Log.d("Glide", "Loading image from URL: ${menuItem.image_url}")
            Glide.with(binding.itemImage.context)
                .load(menuItem.image_url)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(binding.itemImage)

            if (viewOnly) {
                binding.addToOrderButton.visibility = View.GONE
            } else {
                binding.addToOrderButton.visibility = View.VISIBLE
                binding.addToOrderButton.setOnClickListener {
                    onAddToOrderClicked(menuItem)
                }

                binding.addToOrderButton.setOnClickListener {
                    onAddToOrderClicked(menuItem) //sipariş eklendi
                    showSuccessDialog() // Sipariş ekranına geçiş
                }


            }
        }

        // Başarıyla eklendi dialog
        private fun showSuccessDialog() {
            val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Order Added!")
                .setMessage("You have successfully added this product to your orders. What would you like to do now?")
                .setPositiveButton("My Orders") { _, _ ->
                    // Siparişlerim ekranına git
                    val intent = Intent(context, OrderDetailsActivity::class.java)
                    intent.putExtra("tableId", tableId)
                    context.startActivity(intent)
                }
                .setNegativeButton("Back to Menu") { dialog, _ ->
                    // Pop-up'ı kapat
                    dialog.dismiss()
                }
                .create()

            dialog.show()
        }
    }

}
