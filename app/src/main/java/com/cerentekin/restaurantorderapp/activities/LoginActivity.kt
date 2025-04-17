package com.cerentekin.restaurantorderapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cerentekin.restaurantorderapp.R
import com.cerentekin.restaurantorderapp.activities.UserSession.saveUserId
import com.cerentekin.restaurantorderapp.databinding.ActivityLoginBinding
import com.cerentekin.restaurantorderapp.models.LoginRequest
import com.cerentekin.restaurantorderapp.models.LoginResponse
import com.cerentekin.restaurantorderapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(){

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                login(username, password)
            }
        }

        binding.signUpButton.setOnClickListener {
            // Kullanıcıyı SignUpActivity'ye yönlendir
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    private fun login(username: String, password: String) {
        // ProgressBar'ı Göster
        binding.progressBar.visibility = View.VISIBLE
        val loginRequest = LoginRequest(username, password)

        // API Çağrısı
        RetrofitClient.apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                // ProgressBar'ı Gizle
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Toast.makeText(this@LoginActivity, "Welcome, ${loginResponse?.message}", Toast.LENGTH_SHORT).show()
                    if (loginResponse != null) {
                        saveUserId(this@LoginActivity,loginResponse.userId)
                    }

                    // Kullanıcının Rolüne Göre Yönlendirme
                    when (loginResponse?.roleId) {
                        1 -> {
                            // Admin ekranına yönlendir
                            val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                            intent.putExtra("userId", loginResponse.userId)
                            intent.putExtra("source", "LoginActivity")

                            startActivity(intent)
                        }
                        2 -> {
                            // Manager ekranına yönlendir
                            val intent = Intent(this@LoginActivity, ManagerActivity::class.java)
                            intent.putExtra("userId", loginResponse.userId)
                            startActivity(intent)
                        }
                        3 -> {
                            // Staff ekranına yönlendir
                            val intent = Intent(this@LoginActivity, StaffActivity::class.java)
                            intent.putExtra("userId", loginResponse.userId)
                            intent.putExtra("source", "LoginActivity")
                            startActivity(intent)
                        }
                        else -> {
                            Log.e("LoginError", "Unknown roleId: ${loginResponse?.roleId}")
                            Toast.makeText(this@LoginActivity, "Unknown role!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    finish() // LoginActivity'yi kapat
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // ProgressBar'ı Gizle
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
