package com.cerentekin.restaurantorderapp.models

data class AddUserRequest(
    val username: String,
    val password: String,
    val role_id: Int
)
