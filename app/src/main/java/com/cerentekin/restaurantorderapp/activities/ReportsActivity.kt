package com.cerentekin.restaurantorderapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cerentekin.restaurantorderapp.adapters.PopularMenuAdapter
import com.cerentekin.restaurantorderapp.databinding.ActivityReportsBinding
import com.cerentekin.restaurantorderapp.models.DailyOccupancyResponse
import com.cerentekin.restaurantorderapp.models.TopMenuItem
import com.cerentekin.restaurantorderapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar ayarları
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Reports"
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        // RecyclerView ayarları
        binding.popularMenuRecyclerView.layoutManager = LinearLayoutManager(this)

        // Raporları yükle
        fetchReports()
    }

    private fun fetchReports() {
        fetchDailyOccupancyRate()
        fetchTopMenuItems()
    }

    private fun fetchDailyOccupancyRate() {
        RetrofitClient.apiService.getDailyOccupancy().enqueue(object : Callback<DailyOccupancyResponse> {
            override fun onResponse(call: Call<DailyOccupancyResponse>, response: Response<DailyOccupancyResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.let {
                        binding.occupancyProgressBar.progress = it.occupancyRate.toFloat().toInt()
                        binding.occupancyPercentage.text = "${it.occupancyRate}%"
                    }
                } else {
                    Toast.makeText(this@ReportsActivity, "Failed to fetch occupancy data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DailyOccupancyResponse>, t: Throwable) {
                Toast.makeText(this@ReportsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchTopMenuItems() {
        RetrofitClient.apiService.getTopMenuItems().enqueue(object : Callback<List<TopMenuItem>> {
            override fun onResponse(call: Call<List<TopMenuItem>>, response: Response<List<TopMenuItem>>) {
                if (response.isSuccessful) {
                    val topItems = response.body() ?: emptyList()
                    val adapter = PopularMenuAdapter(topItems)
                    binding.popularMenuRecyclerView.adapter = adapter
                } else {
                    Toast.makeText(this@ReportsActivity, "Failed to fetch top menu items", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TopMenuItem>>, t: Throwable) {
                Toast.makeText(this@ReportsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
