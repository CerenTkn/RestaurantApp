package com.cerentekin.restaurantorderapp.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cerentekin.restaurantorderapp.databinding.ActivityChangePasswordBinding
import com.cerentekin.restaurantorderapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar başlığı
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Change Password"
        // Toolbar'daki geri butonunu etkinleştir
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.changePasswordButton.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        val oldPassword = binding.oldPasswordEditText.text.toString()
        val newPassword = binding.newPasswordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()

        // Yeni şifre ile onay eşleşiyor mu?
        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Backend'e istek gönder
        val userId = UserSession.getUserId(this) // Mevcut kullanıcı ID'sini alın
        Log.d("ChangePasswordActivity", "User ID being sent: $userId")

        RetrofitClient.apiService.changePassword(userId, oldPassword, newPassword).enqueue(object :
            Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ChangePasswordActivity, "Password updated successfully", Toast.LENGTH_SHORT).show()
                    finish() // Şifre başarıyla değiştikten sonra ekranı kapat
                } else {
                    Log.e("ChangePasswordActivity", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                    Toast.makeText(this@ChangePasswordActivity, "Failed to update password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ChangePasswordActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
