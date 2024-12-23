package com.eda.bookapp2.model

data class Book(
    val id:String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val date: String,
    val author: String,
    val pages: Int,
    val language: String,
    val price: Double,
    val genres: List<String>,
)
