package com.cerentekin.restaurantorderapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cerentekin.restaurantorderapp.databinding.ItemOrderBinding
import com.cerentekin.restaurantorderapp.models.Order
import com.cerentekin.restaurantorderapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderAdapter(
    private val orderList: MutableList<Order>,
    private val context: Context, // Context eklendi
    private val onOrderClicked: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.binding.order.text = "Order ID: ${order.order_id}"
        holder.binding.orderTable.text = "Table: ${order.table_id}"
        holder.binding.orderStatus.text = "Status: ${order.status}"
        holder.binding.orderTotal.text = "Total: $${order.total_amount}"

        holder.binding.orderDetailsButton.setOnClickListener {
            onOrderClicked(order)
        }
        holder.binding.deleteOrderButton.setOnClickListener {
            deleteOrder(order.order_id)
        }

    }
    private fun deleteOrder(orderId: Int) {
        RetrofitClient.apiService.deleteOrder(orderId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Order deleted successfully", Toast.LENGTH_SHORT).show()
                    removeOrderFromList(orderId)
                } else {
                    Toast.makeText(context, "Failed to delete order", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Remove the deleted order from the list and refresh the adapter
    private fun removeOrderFromList(orderId: Int) {
        val index = orderList.indexOfFirst { it.order_id == orderId }
        if (index != -1) {
            orderList.removeAt(index)
            notifyItemRemoved(index)
        }
    }


    override fun getItemCount() = orderList.size
}
