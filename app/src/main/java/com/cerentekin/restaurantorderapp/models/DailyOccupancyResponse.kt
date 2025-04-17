package com.cerentekin.restaurantorderapp.models

data class DailyOccupancyResponse(
    val occupiedTables: Int,
    val totalTables: Int,
    val occupancyRate: String
)
