package com.eda.bookapp2.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eda.bookapp2.adapter.BookRecycleViewAdapter
import com.eda.bookapp2.constants.Constants
import com.eda.bookapp2.databinding.ActivityToReadListPageBinding
import com.eda.bookapp2.model.Book
import com.eda.bookapp2.server.BookApiService
import com.eda.bookapp2.server.UserApiService
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class toReadList : AppCompatActivity(),BookRecycleViewAdapter.Listener {


    private lateinit var binding: ActivityToReadListPageBinding
    private var bookModels: ArrayList<Book> = ArrayList()
    private var recyclerViewAdapter: BookRecycleViewAdapter? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToReadListPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Firebase Authentication başlatma
        auth = FirebaseAuth.getInstance()

        // Geri butonu
        binding.icBack.setOnClickListener {
            finish()
        }

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.favRecycleView.layoutManager = layoutManager

        // Kullanıcı kimliğini al ve verileri yükle
        val userId = auth.uid
        if (userId != null) {
            loadData(userId)
        } else {
            Toast.makeText(this, "Kullanıcı kimliği alınamadı.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadData(userId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(UserApiService::class.java)

        val call = service.showToReadList(userId)

        call.enqueue(object : Callback<List<String>> {

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@toReadList, "Veriler yüklenemedi: ${t.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    response.body()?.let { favoriteBookIds ->
                        // Kitap ID'lerini alıyoruz
                        loadBookDetails(favoriteBookIds)
                    } ?: run {
                        Toast.makeText(this@toReadList, "Favoriler alınamadı.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@toReadList, "Yanıt hatası", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // Kitap detaylarını almak için yeni fonksiyon
    private fun loadBookDetails(bookIds: List<String>) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val bookApiService = retrofit.create(BookApiService::class.java)

        val bookDetailsCalls = ArrayList<Call<Book>>()

        // Her bir bookId için detayları alıyoruz
        for (bookId in bookIds) {
            val call = bookApiService.getBook(bookId)
            bookDetailsCalls.add(call)
        }

        // Tüm kitap detaylarını paralel olarak yükleyelim
        for (call in bookDetailsCalls) {
            call.enqueue(object : Callback<Book> {
                override fun onFailure(call: Call<Book>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(this@toReadList, "Kitap verisi yüklenemedi.", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<Book>, response: Response<Book>) {
                    if (response.isSuccessful) {
                        response.body()?.let { book ->
                            bookModels.add(book)
                            updateRecyclerView(bookModels)
                        } ?: run {
                            Toast.makeText(this@toReadList, "Kitap verisi alınamadı.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
    }

   private fun updateRecyclerView(books: ArrayList<Book>) {
        recyclerViewAdapter = BookRecycleViewAdapter(books, this@toReadList)
        binding.favRecycleView.adapter = recyclerViewAdapter
    }

    override fun onItemClick(bookModel: Book) {
        // Detay sayfasına gitmek için Intent oluşturuyoruz
        val intent = Intent(this, BookDetailsPage::class.java)

        // Kitap ID'sini Intent ile gönderiyoruz
        intent.putExtra("BOOK_ID", bookModel.id.toInt())  // id'nin doğru şekilde gönderildiğinden emin olun

        // Intent ile BookDetailsPage'e geçiş yapıyoruz
        startActivity(intent)
    }

}
