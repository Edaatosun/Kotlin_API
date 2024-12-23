package com.eda.bookapp2.server

import com.eda.bookapp2.model.BookCommentModel
import com.eda.bookapp2.model.CommentModel
import com.eda.bookapp2.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface  CommentApiService {
    @GET("/comments")
    fun getComments(): Call<List<BookCommentModel>>

    @GET("/comments/{commentId}")
    fun getCommentsById(
        @Path("commentId") commentId: String,
    ): Call<List<BookCommentModel>>

    @POST("/comments/{commentId}")
    fun addCommentModel(
        @Path("commentId") commentId: String,
        @Body commentModel: Map<String, String>
    ): Call<List<BookCommentModel>>

    @POST("/comments")
    fun addComment(@Body comment: BookCommentModel): Call<BookCommentModel>

    @POST("/commentsModel")
    fun addComment2(@Body comment: CommentModel): Call<CommentModel>


}