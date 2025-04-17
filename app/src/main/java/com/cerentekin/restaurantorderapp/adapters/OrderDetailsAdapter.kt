package com.cerentekin.restaurantorderapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cerentekin.restaurantorderapp.databinding.ItemOrderDetailsBinding
import com.cerentekin.restaurantorderapp.models.OrderDetails

class OrderDetailsAdapter(private val orderDetailsList: MutableList<OrderDetails>,
                          private val onDeleteClicked: (OrderDetails) -> Unit // Callback fonksiyonu
) :
    RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {

    inner class OrderDetailsViewHolder(val binding: ItemOrderDetailsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val binding = ItemOrderDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderDetailsViewHolder(binding)
    }

    override fun getItemCount(): Int = orderDetailsList.size

    fun removeItem(orderDetails: OrderDetails) {
        val index = orderDetailsList.indexOf(orderDetails) // Silinmek istenen öğenin indeksini bulun
        if (index != -1) { // Eğer liste içinde bulunuyorsa
            orderDetailsList.removeAt(index) // Listeden kaldır
            notifyItemRemoved(index) // RecyclerView'e değişikliği bildir
            notifyItemRangeChanged(index, orderDetailsList.size) // Kalan elemanları güncelle
        }
    }


    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        val detail = orderDetailsList[position]
        holder.binding.itemName.text = detail.item_name
        holder.binding.quantity.text = "Quantity: ${detail.total_quantity}"
        holder.binding.subtotal.text = "Subtotal: $${detail.total_subtotal}"
        holder.binding.orderDetailsId.text = "Order ID: ${detail.order_id}"

        // Delete butonu için tıklama dinleyici
        holder.binding.deleteOrderButton.setOnClickListener {
            onDeleteClicked(detail)
        }
    }



}
