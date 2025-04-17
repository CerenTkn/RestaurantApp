package com.cerentekin.restaurantorderapp.models

data class User(
    val user_id: Int,     // Kullanıcı ID'si (Auto-increment, backend döndürüyor)
    val username: String,        // Kullanıcı adı (zorunlu alan)
    val password: String,        // Şifre (zorunlu alan, hashlenmiş olarak gönderilecek)
    val role_id: Int,
    val role_name: String
)
