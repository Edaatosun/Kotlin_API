
package com.eda.bookapp2.model
data class BookCommentModel(
    val commentId:String,
    val bookId: String,
    val userId: String,
    val imageUrl:String,
    val username: String,
    val commentText: String,
    val bookName: String,
    val bookImageUrl: String?,
    val CommentModel: MutableList<CommentModel> = mutableListOf()
)
