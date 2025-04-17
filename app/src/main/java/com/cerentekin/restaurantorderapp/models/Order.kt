package com.cerentekin.restaurantorderapp.models

data class Order(
    val order_id: Int,
    val table_id: Int,
    val user_id: Int,
    val items: List<MenuItem>, // Sipariş edilen öğelerin listesi
    val total_amount: Double,
    val status: String
)
