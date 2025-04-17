package com.cerentekin.restaurantorderapp.models

data class LoginResponse(   val userId: Int,
                            val username: String,
                            val roleId: Int,
                            val message: String)
