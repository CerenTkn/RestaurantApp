package com.cerentekin.restaurantorderapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cerentekin.restaurantorderapp.adapters.OrderAdapter
import com.cerentekin.restaurantorderapp.databinding.ActivityOrderBinding
import com.cerentekin.restaurantorderapp.models.Order
import com.cerentekin.restaurantorderapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private lateinit var orderAdapter: OrderAdapter
    private var orderList = mutableListOf<Order>()
    private var tableId: Int = 0 // Masaya ait ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar Ayarları
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Orders"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }


        // Intent ile gelen tableId verisini al
        tableId = intent.getIntExtra("tableId", 0)
        if (tableId == 0) {
            Toast.makeText(this, "Invalid table ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // RecyclerView Ayarları
        setupRecyclerView()


        fetchOrders(tableId)
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(orderList, context = this) { order ->

            val intent = Intent(this, OrderDetailsActivity::class.java)
            intent.putExtra("tableId", tableId) // Doğru veriyi gönder
            startActivity(intent)        }

        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.ordersRecyclerView.adapter = orderAdapter
    }

    private fun fetchOrders(tableId : Int) {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.apiService.getOrdersForTable(tableId).enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    orderList.clear()
                    orderList.addAll(response.body() ?: listOf())
                    orderAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@OrderActivity, "Failed to fetch orders", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@OrderActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateOrderStatus(order: Order) {
        val newStatus = if (order.status == "pending") "completed" else "pending"
        RetrofitClient.apiService.updateOrderStatus(order.order_id, mapOf("status" to newStatus))
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@OrderActivity, "Order status updated", Toast.LENGTH_SHORT).show()
                        fetchOrders(tableId) // Siparişleri güncelle
                    } else {
                        Toast.makeText(this@OrderActivity, "Failed to update order status", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@OrderActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
