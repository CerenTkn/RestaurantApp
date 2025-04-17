package com.cerentekin.restaurantorderapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cerentekin.restaurantorderapp.R
import com.cerentekin.restaurantorderapp.adapters.DashboardAdapter
import com.cerentekin.restaurantorderapp.adapters.OrderAdapter
import com.cerentekin.restaurantorderapp.databinding.ActivityManagerBinding
import com.cerentekin.restaurantorderapp.models.DashboardItem
import com.cerentekin.restaurantorderapp.models.Order
import com.cerentekin.restaurantorderapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManagerBinding
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var dashboardAdapter: DashboardAdapter
    private var orderList = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar Ayarları
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Manager Panel"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { navigateToLogin() }




        // RecyclerView Ayarları
        setupRecyclerView()

        // Siparişleri Getir
        //fetchOrders()
    }

    // Menü oluştur
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_staff_activity, menu) // Aynı menüyü kullanıyoruz
        return true
    }
    // Menü item'larının tıklama işlemleri
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_change_password -> {
                // Change Password Activity'ye yönlendir
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_logout -> {
                // Logout işlemi
                performLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performLogout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Geri düğmesine basıldığında LoginActivity'ye yönlendirme
    override fun onBackPressed() {
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    private fun setupRecyclerView() {
        /*
        orderAdapter = OrderAdapter(orderList, context = this, onOrderClicked = {order ->
            Toast.makeText(this, "Order ID: ${order.order_id}", Toast.LENGTH_SHORT).show()

            // İlgili sipariş için detay ekranına geçiş
            val intent = Intent(this, OrderDetailsActivity::class.java)
            intent.putExtra("orderId", order.order_id)
            startActivity(intent)
        })

         */

        val dashboardItems = listOf(
            DashboardItem("Tables", R.drawable.ic_table),
            DashboardItem("Menu Management", R.drawable.ic_menu_management),
            DashboardItem("User Management", R.drawable.ic_user_management),
            DashboardItem("Reports", R.drawable.ic_reports)
        )

        binding.dashboardRV.layoutManager = LinearLayoutManager(this)
        binding.dashboardRV.adapter = DashboardAdapter(dashboardItems) { item ->
            handleDashboardItemClick(item)
        }
    }

    private fun handleDashboardItemClick(item: DashboardItem) {
        when (item.title) {
            "Tables" -> {
                val intent = Intent(this, StaffActivity::class.java)
                intent.putExtra("source", "ManagerActivity")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
            "Menu Management" -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("viewOnly", true) // View-Only Modu Aktarılıyor
                //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
            "User Management" -> {
                val intent = Intent(this, AdminActivity::class.java)
                intent.putExtra("source", "ManagerActivity")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
            "Reports" -> {
                val intent = Intent(this, ReportsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }
    }

    /*

    private fun fetchOrders() {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.apiService.getOrders().enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    Log.e("manager","fetch orders")

                    orderList.clear()
                    orderList.addAll(response.body() ?: listOf())
                    orderAdapter.notifyDataSetChanged()

                    // Eğer sipariş listesi boşsa mesaj göster
                    if (orderList.isEmpty()) {
                        binding.ordersRecyclerView.visibility = View.GONE
                        binding.emptyMessageTextView.visibility = View.VISIBLE
                    } else {
                        binding.ordersRecyclerView.visibility = View.VISIBLE
                        binding.emptyMessageTextView.visibility = View.GONE
                    }

                } else {
                    Log.e("manager","cant fetch orders")
                    Toast.makeText(this@ManagerActivity, "Failed to fetch orders", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@ManagerActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

     */
}
