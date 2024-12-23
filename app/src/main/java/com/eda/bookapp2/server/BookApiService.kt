package com.eda.bookapp2.server

import com.eda.bookapp2.model.Book
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

//unutma bu bir interface içerisindeki fonk tekrar oluşturucaz
interface BookApiService {
    @GET("/books") // BaseUrl ye eklenen kısım
    fun getBooks(): Call<List<Book>> // Dönüş tipi olarak Book modelini kullanıyoruz

    @GET("/books/{bookId}") // BaseUrl'ye eklenen kısım
    fun getBook(@Path("bookId") bookId: String): Call<Book> // Tek bir kitap dönülecek


    //normalde şu olmalı
    //Get("book.json/{bookId}"
    //fun getdetailsBook(): call<List<Book>>
}
