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
import com.cerentekin.restaurantorderapp.adapters.TableAdapter
import com.cerentekin.restaurantorderapp.databinding.ActivityStaffBinding
import com.cerentekin.restaurantorderapp.models.Table
import com.cerentekin.restaurantorderapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StaffActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffBinding
    private lateinit var tableAdapter: TableAdapter
    private var tableList = mutableListOf<Table>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar Ayarları
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Staff Panel"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }


        // RecyclerView Ayarları
        setupRecyclerView()

        // Masaları Getir
        fetchTables()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_staff_activity, menu)
        return true
    }

    // Menü item'larına tıklama işlemlerini yönetme
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
                // Logout işlemini gerçekleştir
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onBackPressed() {
        // Hangi ekrandan geldiğimizi kontrol ediyoruz
        val source = intent.getStringExtra("source")
        if (source == "LoginActivity") {
            // Login ekranından geldiysek geri tuşu LoginActivity'ye yönlendirsin
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else if (source == "ManagerActivity") {
            // Manager ekranından geldiysek geri tuşu ManagerActivity'ye yönlendirsin
            val intent = Intent(this, ManagerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        finish() // AdminPanelActivity'yi kapat
    }

    private fun setupRecyclerView() {
        tableAdapter = TableAdapter(tableList, this) { table ->
            openOrderScreen(table)
        }
        binding.tablesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.tablesRecyclerView.adapter = tableAdapter
    }

    private fun fetchTables() {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.apiService.getTables().enqueue(object : Callback<List<Table>> {
            override fun onResponse(call: Call<List<Table>>, response: Response<List<Table>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    tableList.clear()
                    response.body()?.let {
                        tableList.addAll(it)
                        for (table in it) {
                            Log.d("FetchTables", "Table ID: ${table.table_id}, Status: ${table.status}")
                        }
                    }
                    tableAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@StaffActivity, "Failed to fetch tables", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Table>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@StaffActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openOrderScreen(table: Table) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        intent.putExtra("tableId", table.table_id)
        startActivity(intent)
    }
}
