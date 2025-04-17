package com.cerentekin.restaurantorderapp.models

data class OrderRequest(
    val tableId: Int,    // Hangi masa için sipariş
    val items: List<OrderItem>
)
