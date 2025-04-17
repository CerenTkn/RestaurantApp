package com.cerentekin.restaurantorderapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cerentekin.restaurantorderapp.adapters.OrderDetailsAdapter
import com.cerentekin.restaurantorderapp.databinding.ActivityOrdersDetailsBinding
import com.cerentekin.restaurantorderapp.models.OrderDetails
import com.cerentekin.restaurantorderapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrdersDetailsBinding
    private val orderDetailsList = mutableListOf<OrderDetails>()
    private lateinit var adapter: OrderDetailsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar Ayarları
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Order Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        val tableId = intent.getIntExtra("tableId", -1)
        if (tableId != -1) {
            fetchOrderDetailsForTable(tableId)
        } else {
            Toast.makeText(this, "Invalid table ID", Toast.LENGTH_SHORT).show()
            finish()
        }


        // Yeni Sipariş Ekleme
        binding.addOrderButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("tableId", tableId)
            startActivity(intent)
        }

        binding.ordersdetailsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.ordersdetailsRecyclerView.adapter = OrderDetailsAdapter(orderDetailsList, onDeleteClicked = { orderDetails ->
            deleteOrder(orderDetails)
        })
    }
    override fun onBackPressed() {
        navigateToStaffPanel()
    }

    private fun navigateToStaffPanel(){
        val intent = Intent(this, StaffActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
    private fun calculateTotal(): Double {
        return orderDetailsList.sumOf { it.total_subtotal }
    }

    private fun updateTotal() {
        val total = calculateTotal()
        binding.totalTextView.text = "Total: $${"%.2f".format(total)}"
    }


    private fun fetchOrderDetailsForTable(tableId: Int) {
        RetrofitClient.apiService.getOrderDetailsForTable(tableId).enqueue(object :
            Callback<List<OrderDetails>> {
            override fun onResponse(call: Call<List<OrderDetails>>, response: Response<List<OrderDetails>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val details = response.body()
                    if (!details.isNullOrEmpty()) {
                        orderDetailsList.clear()
                        orderDetailsList.addAll(details)
                        binding.ordersdetailsRecyclerView.adapter?.notifyDataSetChanged()

                        updateTotal()
                    } else {
                        Log.e("OrderDetails", "No details found for tableId: $tableId")
                        Toast.makeText(this@OrderDetailsActivity, "No orders found!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("OrderDetails", "Error Code: ${response.code()}, Message: ${response.message()}")
                    Log.e("OrderDetails", "Error Body: ${response.errorBody()?.string()}")
                    Toast.makeText(this@OrderDetailsActivity, "Failed to fetch orders2. Code: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<OrderDetails>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("order details","fail")
                Toast.makeText(this@OrderDetailsActivity, "Failed to fetch details: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteOrder(orderDetails: OrderDetails) {
        // API'ye DELETE isteği gönder
        RetrofitClient.apiService.deleteOrder(orderDetails.order_id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Sipariş başarıyla silindiyse
                    Toast.makeText(this@OrderDetailsActivity, "Order deleted successfully", Toast.LENGTH_SHORT).show()

                    // RecyclerView'den öğeyi kaldır
                    adapter.removeItem(orderDetails)
                    updateTotal()
                } else {
                    // Hata durumunda yanıt kontrolü
                    val errorMessage = response.errorBody()?.string() ?: "Failed to delete order"
                    Log.e("API Error", "Hata Mesajı: $errorMessage")

                    Toast.makeText(this@OrderDetailsActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Bağlantı hatası veya başka bir hata
                Log.e("API Error", "Bağlantı Hatası: ${t.message}")
                Toast.makeText(this@OrderDetailsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
