package com.cerentekin.restaurantorderapp.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cerentekin.restaurantorderapp.databinding.ActivityAddUserBinding
import com.cerentekin.restaurantorderapp.models.AddUserRequest
import com.cerentekin.restaurantorderapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserBinding
    private val roles = listOf("Admin", "Manager", "Staff") // Roller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar Ayarları
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Add User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        // Spinner için rollerin yüklenmesi
        setupRoleSpinner()

        // Kullanıcı ekleme işlemi
        binding.addUserButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val selectedRole = binding.roleSpinner.selectedItem.toString()

            if (username.isBlank() || password.isBlank() || selectedRole.isBlank()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                addUser(username, password, selectedRole)
            }
        }
    }

    private fun setupRoleSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.roleSpinner.adapter = adapter
    }

    private fun addUser(username: String, password: String, roleName: String) {
        val roleId = when (roleName) {
            "Admin" -> 1
            "Manager" -> 2
            "Staff" -> 3
            else -> 3
        }

        val addUserRequest = AddUserRequest(username, password, roleId)

        RetrofitClient.apiService.addUser(addUserRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddUserActivity, "User added successfully", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK) // Başarıyla ekleme işlemini bildir
                    finish() // Aktiviteyi kapat
                } else {
                    Toast.makeText(this@AddUserActivity, "Failed to add user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@AddUserActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
