package com.eda.bookapp2.server

import com.eda.bookapp2.model.User
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {

    @GET("/users/{userId}")
    fun getUser(@Path("userId") userId: String): Call<User>

    // Kullanıcı eklemek için POST işlemi
    @POST("/users") // users.json altına kullanıcı verisini ekliyoruz
    fun createUser(@Body user: User): Call<User> // User modelini gönderiyoruz

    @PATCH("/users/{userId}")
    fun updateUserImage(
        @Path("userId") userId: String,
        @Body imageUrl: Map<String, String>
    ): Call<User>

    @PATCH("/user/{userId}")
    fun updateUserField(
        @Path("userId") userId: String,
        @Query("field") field: String,
        @Query("value") value: String
    ): Call<User>

    @POST("/user/{userId}/favorites")
    fun addToFavorites(
        @Path("userId") userId: String,  // Pass the userId as a parameter
        @Body bookId: Map<String, String>                // Send the bookId in the request body
    ): Call<Void>

    // Kullanıcı ID'si ile favori kitapları al
    @GET("/user/{userId}/favorites")
    fun showFavorites(@Path("userId") userId: String): Call<List<String>>

    @GET("/user/{userId}/favorites/{bookId}")
    fun showFavoriteById(
        @Path("userId") userId: String,
        @Body bookId: String
    ): Call<Boolean>


    @POST("/user/{userId}/toReadList")
    fun addToToReadList(
        @Path("userId") userId: String,
        @Body bookId: Map<String, String> // bookId'yi JSON formatında alıyoruz
    ): Call<Void>



    @GET("/user/{userId}/toReadList")
    fun showToReadList(@Path("userId") userId: String): Call<List<String>>


    @DELETE("/user/{userId}/favorite/{bookId}")
    fun removeFavorite(
        @Path("userId") userId: String,
        @Path("bookId") bookId: String
    ): Call<Void>

    @DELETE("/user/{userId}/toReadList/{bookId}")
    fun removeToReadList(
        @Path("userId") userId: String,
        @Path("bookId") bookId: String
    ): Call<Void>




}
