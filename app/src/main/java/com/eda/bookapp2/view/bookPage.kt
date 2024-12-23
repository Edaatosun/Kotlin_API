package com.eda.bookapp2.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eda.bookapp2.adapter.BookRecycleViewAdapter
import com.eda.bookapp2.databinding.ActivityBookPageBinding
import com.eda.bookapp2.model.Book
import com.eda.bookapp2.constants.Constants // base url var
import com.eda.bookapp2.server.BookApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BookPage : AppCompatActivity(), BookRecycleViewAdapter.Listener {
    private lateinit var binding: ActivityBookPageBinding
    private var bookModels: ArrayList<Book> = ArrayList()
    private var recyclerViewAdapter: BookRecycleViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.bookRecycleView.layoutManager = layoutManager

        binding.icBack.setOnClickListener {
            finish()
        }

        loadData()

        // Arama alanına TextWatcher ekliyoruz
        binding.searchTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("BookPage", "Text will change: $s")
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString()
                if (query.isNotEmpty()) {
                    filterBooks(query)
                } else {
                    updateRecyclerView(bookModels)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("BookPage", "after change: $s")
            }
        })
    }

    private fun filterBooks(query: String) {
        val filteredBooks = bookModels.filter {
            it.title.contains(query, ignoreCase = true) // Title içerisinde arama terimini arıyoruz
        }
        updateRecyclerView(ArrayList(filteredBooks))
    }

    private fun loadData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(BookApiService::class.java)

        val call = service.getBooks()

        call.enqueue(object : Callback<List<Book>> {
            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@BookPage, "Failed to load data"+t.printStackTrace(), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<Book>>, response: Response<List<Book>>) {
                if (response.isSuccessful) {
                    response.body()?.let { books ->
                        bookModels.clear()
                        bookModels.addAll(books)
                        updateRecyclerView(bookModels)
                    } ?: run {
                        Toast.makeText(this@BookPage, "No data received", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@BookPage, "Error in response", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateRecyclerView(books: ArrayList<Book>) {
        recyclerViewAdapter = BookRecycleViewAdapter(books, this@BookPage)
        binding.bookRecycleView.adapter = recyclerViewAdapter
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
