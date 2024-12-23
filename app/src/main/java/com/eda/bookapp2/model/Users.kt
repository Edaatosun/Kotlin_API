package com.eda.bookapp2.model

data class User(
    val id: String = "",           // Firebase Authentication'dan alınacak userId
    val imageUrl:String= "",
    val username: String = "",     // Kullanıcının adı
    val age: String = "",
    val email: String = "",        // Kullanıcının e-posta adresi
    val favoriteBooks: MutableList<String> = mutableListOf(),  // Kullanıcının favori kitaplarının listesi (Kitap ID'leri)
    val toReadBooks: MutableList<String> = mutableListOf()      // Kullanıcının okunacak kitaplarının listesi (Kitap ID'leri)
)
