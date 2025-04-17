package com.cerentekin.restaurantorderapp.network

import com.cerentekin.restaurantorderapp.models.AddUserRequest
import com.cerentekin.restaurantorderapp.models.ApiResponse
import com.cerentekin.restaurantorderapp.models.DailyOccupancyResponse
import com.cerentekin.restaurantorderapp.models.LoginRequest
import com.cerentekin.restaurantorderapp.models.LoginResponse
import com.cerentekin.restaurantorderapp.models.MenuItem
import com.cerentekin.restaurantorderapp.models.Order
import com.cerentekin.restaurantorderapp.models.OrderDetails
import com.cerentekin.restaurantorderapp.models.OrderRequest
import com.cerentekin.restaurantorderapp.models.OrderStatusUpdate
import com.cerentekin.restaurantorderapp.models.Table
import com.cerentekin.restaurantorderapp.models.TopMenuItem
import com.cerentekin.restaurantorderapp.models.User
import retrofit2.Call
import retrofit2.http.*

interface ApiService {


    // Tüm masaları getirir
    @GET("api/tables")
    fun getTables(): Call<List<Table>>

    @PATCH("api/tables/{tableId}/status")
    fun updateTableStatus(
        @Path("tableId") tableId: Int,
        @Body status: Map<String, String>
    ): Call<Void>

    // Menüdeki tüm ürünleri getirir
    @GET("/api/menu")
    fun getMenuItems(): Call<List<MenuItem>>

    // Tüm siparişleri getirir
    @GET("api/orders")
    fun getOrders(): Call<List<Order>>

    @GET("api/orders/details/{tableId}")
    fun getOrderDetailsForTable(@Path("tableId") tableId: Int): Call<List<OrderDetails>>

    @POST("api/orders")
    fun addOrder(@Body requestBody: OrderRequest
    ): Call<ApiResponse>

    @DELETE("api/orders/{orderId}")
    fun deleteOrder(@Path("orderId") orderId: Int): Call<Void>

    // Sipariş Durumu Güncelle
    @PUT("api/orders/{orderId}")
    fun updateOrderStatus(
        @Path("orderId") orderId: Int,
        @Body requestBody: OrderStatusUpdate
    ): Call<ApiResponse>

    @POST("api/users/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("api/users")
    fun getUsers(): Call<List<User>>

    @DELETE("api/users/{id}")
    fun deleteUser(@Path("id") userId: Int): Call<Void>

    // Kullanıcı ekleme endpoint'i
    @POST("api/users")
    fun addUser(@Body addUserRequest: AddUserRequest): Call<Void>

    //Change Password
    @PUT("api/users/update-password")
    fun changePassword(
        @Query("userId") userId: Int,
        @Query("oldPassword") oldPassword: String,
        @Query("newPassword") newPassword: String
    ): Call<Void>


    @GET("api/orders/table/{tableId}")
    fun getOrdersForTable(@Path("tableId") tableId: Int): Call<List<Order>>

    @PATCH("api/orders/{orderId}/status")
    fun updateOrderStatus(
        @Path("orderId") orderId: Int,
        @Body status: Map<String, String>
    ): Call<Void>

    @GET("api/reports/daily-occupancy")
    fun getDailyOccupancy(): Call<DailyOccupancyResponse>

    @GET("api/reports/top-menu-items")
    fun getTopMenuItems(): Call<List<TopMenuItem>>
}
