package com.cerentekin.restaurantorderapp.models

data class OrderItem(
    val itemId: Int,
    val quantity: Int,
    val subtotal: Double
)
