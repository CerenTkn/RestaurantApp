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
import com.cerentekin.restaurantorderapp.adapters.UserAdapter
import com.cerentekin.restaurantorderapp.databinding.ActivityAdminBinding
import com.cerentekin.restaurantorderapp.models.User
import com.cerentekin.restaurantorderapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var userAdapter: UserAdapter
    private var userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar Ayarları
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Admin Panel"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed() }

        // RecyclerView Ayarları
        setupRecyclerView()

        // Kullanıcıları Getir
        fetchUsers()

        // Kullanıcı Ekle Butonu
        binding.addUserButton.setOnClickListener {
            val intent = Intent(this, AddUserActivity::class.java)
            startActivityForResult(intent, ADD_USER_REQUEST_CODE)
        }
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
    /*
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

     */

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
        userAdapter = UserAdapter(userList) { user ->
            deleteUser(user)
        }
        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.userRecyclerView.adapter = userAdapter
    }

    private fun fetchUsers() {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.apiService.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    userList.clear()
                    userList.addAll(response.body() ?: listOf())
                    userAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@AdminActivity, "Failed to fetch users", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@AdminActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteUser(user: User) {
        RetrofitClient.apiService.deleteUser(user.user_id).enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("DeleteUser", "Deleting User with ID: ${user.user_id}")

                if (response.isSuccessful) {
                    userList.remove(user)
                    userAdapter.notifyDataSetChanged()
                    Toast.makeText(this@AdminActivity, "User deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("DeleteUser", "Response Code: ${response.code()}, Error: ${response.errorBody()?.string()}")
                    Toast.makeText(this@AdminActivity, "Failed to delete user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@AdminActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_USER_REQUEST_CODE && resultCode == RESULT_OK) {
            fetchUsers() // Listeyi yeniden yükle
        }
    }

    companion object {
        const val ADD_USER_REQUEST_CODE = 1
    }
}
