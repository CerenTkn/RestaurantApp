package com.cerentekin.restaurantorderapp.models

data class OrderDetails(
    val order_id: Int,
    val item_id: Int,
    val total_quantity: Int,
    val total_subtotal: Double,
    val item_name: String,
    val tableId: Int,
    val status: String)
