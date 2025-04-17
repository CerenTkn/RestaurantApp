package com.cerentekin.restaurantorderapp.models

import java.io.Serializable

data class MenuItem(
    val table_id: Int,
    val item_id: Int,
    val name: String,
    val price: Double,
    val image_url: String,
    val category_id: Int,
    val created_at: String
) : Serializable
