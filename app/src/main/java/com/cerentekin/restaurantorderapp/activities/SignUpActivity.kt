package com.cerentekin.restaurantorderapp.activities

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cerentekin.restaurantorderapp.R
import com.cerentekin.restaurantorderapp.databinding.ActivitySignupBinding
import com.cerentekin.restaurantorderapp.models.AddUserRequest
import com.cerentekin.restaurantorderapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar başlığı
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Sign Up"
        // Toolbar'daki geri butonunu etkinleştir
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Spinner'a veri yükleme
        setupRoleSpinner()

        binding.registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun setupRoleSpinner() {
        // Spinner için adapter oluştur ve role verilerini bağla
        val roles = resources.getStringArray(R.array.roles)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.roleSpinner.adapter = adapter
    }

    private fun registerUser() {
        // Kullanıcıdan alınan veriler
        val username = binding.usernameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val roleInput = binding.roleSpinner.selectedItem.toString() // Spinner'dan seçilen değer

        // Verilerin doğruluğunu kontrol et
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        // Rol bilgisini Integer olarak dönüştür
        val roleId = when (roleInput) {
            "Admin" -> 1
            "Manager" -> 2
            "Staff" -> 3
            else -> {
                Toast.makeText(this, "Invalid role selected.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Yeni kullanıcı isteği oluştur
        val addUserRequest = AddUserRequest(username, password, roleId)

        // Retrofit üzerinden kullanıcı ekleme isteği
        RetrofitClient.apiService.addUser(addUserRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.e("register","response success")
                    Toast.makeText(this@SignUpActivity, "User registered successfully!", Toast.LENGTH_SHORT).show()
                    finish() // Kayıttan sonra aktiviteyi kapat
                } else if(response.code() == 400){
                    // Kullanıcı adı alınmış
                    Toast.makeText(this@SignUpActivity, "Username is already taken. Please choose another.", Toast.LENGTH_SHORT).show()
                }
                else {
                    Log.e("register","response fail: ${response.code()} - ${response.errorBody()?.string()}")

                    Toast.makeText(this@SignUpActivity, "Failed to register user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("register","response failure")

                Toast.makeText(this@SignUpActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

