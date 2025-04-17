package com.cerentekin.restaurantorderapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cerentekin.restaurantorderapp.adapters.MenuAdapter
import com.cerentekin.restaurantorderapp.databinding.ActivityMainBinding
import com.cerentekin.restaurantorderapp.models.ApiResponse
import com.cerentekin.restaurantorderapp.models.MenuItem
import com.cerentekin.restaurantorderapp.models.Order
import com.cerentekin.restaurantorderapp.models.OrderItem
import com.cerentekin.restaurantorderapp.models.OrderRequest
import com.cerentekin.restaurantorderapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var menuAdapter: MenuAdapter
   // private var orderList = mutableListOf<MenuItem>()
    private var tableId: Int = -1// Seçili masa ID'si


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar başlığı
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // View-Only Modu Kontrolü
        val isViewOnly = intent.getBooleanExtra("viewOnly", false)
        if (isViewOnly) {
            setupViewOnlyMode() // View-Only modunu ayarla
            return
        }

        // Masa ID'sini kontrol et
        tableId = intent.getIntExtra("tableId", -1)
        if (tableId == -1) {
            Toast.makeText(this, "Invalid Table ID!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Masa için Menü Başlığı
        supportActionBar?.title = "Menu for Table $tableId"

        setupRecyclerView(isViewOnly)
        fetchMenuItems() // Menü öğelerini çek
    }
    private fun setupViewOnlyMode() {
        supportActionBar?.title = "Menu Items"
        setupRecyclerView(true) // View-only için RecyclerView ayarla
        fetchMenuItems() // Menü öğelerini yükle
    }

    private fun setupRecyclerView(isViewOnly: Boolean) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        menuAdapter = MenuAdapter(
            menuItems = listOf(),
            context = this,
            onAddToOrderClicked = { menuItem ->
                if (!isViewOnly) {
                    addToOrders(menuItem) // Sadece sipariş eklenebilir modda çalışır
                }
            },
            tableId = tableId,
            viewOnly = isViewOnly
        )
        binding.recyclerView.adapter = menuAdapter
    }

    private fun fetchMenuItems() {
        // ProgressBar göster
        binding.progressBar.visibility = android.view.View.VISIBLE

        RetrofitClient.apiService.getMenuItems().enqueue(object : Callback<List<MenuItem>> {
            override fun onResponse(call: Call<List<MenuItem>>, response: Response<List<MenuItem>>) {
                binding.progressBar.visibility = android.view.View.GONE // ProgressBar gizle

                if (response.isSuccessful) {
                    menuAdapter.updateData(response.body() ?: listOf())
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load menu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<MenuItem>>, t: Throwable) {
                binding.progressBar.visibility = android.view.View.GONE // ProgressBar gizle
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    /**
     * Siparişe ürün ekleme
     */
    private fun addToOrders(menuItem: MenuItem) {

        // Ara toplamı hesapla
        val subtotal = menuItem.price * 1 // Miktar varsayılan olarak 1

        // Sipariş için OrderRequest nesnesini oluştur
        val orderRequest = OrderRequest(
            tableId = tableId,
            items = listOf(
                OrderItem(
                    itemId = menuItem.item_id,
                    quantity = 1,   // Varsayılan miktar
                    subtotal = subtotal // Ara toplam (price * quantity)
                )
            )
        )
        // JSON verisini loglayarak doğrulayın
        Log.d("OrderRequest", "Gönderilen JSON: $orderRequest")

        RetrofitClient.apiService.addOrder(orderRequest).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        // Ürün başarıyla eklendi
                        Toast.makeText(
                            this@MainActivity,
                            "${menuItem.name} başarıyla eklendi.",
                            Toast.LENGTH_SHORT
                        ).show()

                        // OrderDetailsActivity'e yönlendirme
                        val intent = Intent(this@MainActivity, OrderDetailsActivity::class.java)
                        intent.putExtra("tableId", tableId)
                        startActivity(intent)
                    } else {
                        // Başarısız yanıt
                        Toast.makeText(this@MainActivity, "${menuItem.name} eklenemedi: ${apiResponse?.message ?: "Bilinmeyen hata"}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // HTTP başarısızlığı
                    Toast.makeText(this@MainActivity, "${menuItem.name} eklenemedi: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
                showOrderDetailsDialog()
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun showOrderDetailsDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this).apply {
            setTitle("Order Added!")
            setMessage("You have successfully added this product to your orders. What would you like to do now?")
            setPositiveButton("My Orders") { _, _ ->
                val intent = Intent(this@MainActivity, OrderDetailsActivity::class.java)
                intent.putExtra("tableId", tableId)
                startActivity(intent)
            }
            setNegativeButton("Back to Menu") { dialog, _ -> dialog.dismiss() }
            create()
        }.show()
    }

}
