package com.eda.bookapp2.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.eda.bookapp2.R
import com.eda.bookapp2.constants.Constants
import com.eda.bookapp2.databinding.ActivityBookDetailsPageBinding
import com.eda.bookapp2.model.Book
import com.eda.bookapp2.model.BookCommentModel
import com.eda.bookapp2.model.User
import com.eda.bookapp2.server.BookApiService
import com.eda.bookapp2.server.CommentApiService
import com.eda.bookapp2.server.UserApiService
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

class BookDetailsPage : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailsPageBinding
    private var bookId: Int = -1
    private lateinit var auth: FirebaseAuth
    private lateinit var userApi: UserApiService
    private lateinit var commentApi: CommentApiService
    private var bookTitle: String? = ""
    private var bookImageUrl: String? = ""
    private var isFavorite = false
    private lateinit var commentList: MutableList<BookCommentModel>
    private lateinit var recyclerViewAdapter: LittleCommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailsPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Set up RecyclerView
        binding.commentRecycleView.layoutManager = LinearLayoutManager(this)
        commentList = mutableListOf()
        recyclerViewAdapter = LittleCommentAdapter(commentList)
        binding.commentRecycleView.adapter = recyclerViewAdapter

        // Firebase Authentication setup
        auth = FirebaseAuth.getInstance()
        val userId = auth.uid ?: run {
            Toast.makeText(this, "Kullanıcı kimliği alınamadı.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Retrofit setup
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userApi = retrofit.create(UserApiService::class.java)
        commentApi = retrofit.create(CommentApiService::class.java)

        // Get the bookId from intent
        bookId = intent.getIntExtra("BOOK_ID", -1)
        if (bookId == -1) {
            Toast.makeText(this, "Geçersiz kitap ID'si.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        // Arama EditText'e dinleyici ekleyin

        loadData(bookId)
        //getFavoriteStatus(userId,bookId.toString())

        // Back button
        binding.icBack.setOnClickListener {
            finish()
        }

        // Add to Favorites
        binding.icFav.setOnClickListener {
            isFavorite = if (!isFavorite) {
                addToFavorites(userId, bookId)
                binding.icFav.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorSelected))
                true
            } else {
                removeFavorite(userId, bookId.toString())
                binding.icFav.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorUnselected))
                false
            }
        }

        // Add to To Read list
        binding.btnAddToReadList.setOnClickListener {
            addToToReadList(userId, bookId)
        }

        // Send comment
        binding.sendButton.setOnClickListener {
            val commentTxt = binding.commentTxt.text.toString()
            if (commentTxt.isNotBlank()) {
                addComment(userId, bookId, commentTxt)
            } else {
                Toast.makeText(this, "Yorum boş olamaz.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadData(bookId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(BookApiService::class.java)
        val call = service.getBooks()

        call.enqueue(object : Callback<List<Book>> {
            override fun onResponse(call: Call<List<Book>>, response: Response<List<Book>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val selectedBook = it.find { book -> book.id == bookId.toString() }
                        selectedBook?.let { book ->
                            displayBookDetails(book)
                            bookTitle = book.title
                            bookImageUrl = book.imageUrl
                        } ?: run {
                            Toast.makeText(
                                this@BookDetailsPage,
                                "Kitap bulunamadı.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(
                    this@BookDetailsPage,
                    "Kitap verileri yüklenemedi.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun displayBookDetails(book: Book) {
        Picasso.get()
            .load(book.imageUrl)
            .into(binding.bookImageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    binding.progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    binding.progressBar.visibility = View.GONE
                    e?.printStackTrace()
                }
            })
        binding.titleTV.text = book.title
        binding.dateTV.text = book.date
        binding.authorTV.text = book.author
        binding.priceTV.text = book.price.toString()
        binding.languageTV.text = book.language
        binding.pagesTV.text = book.pages.toString()
        binding.categoryTV.text = book.genres.toString()
        binding.descriptionTV.text = book.description
    }

    private fun addToFavorites(userId: String, bookId: Int) {
        // Map oluşturuyoruz, burada bookId'yi JSON formatında gönderiyoruz
        val bookIdMap = mapOf("bookId" to bookId.toString())
        userApi.addToFavorites(userId, bookIdMap).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@BookDetailsPage, "Favorilere eklendi.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        this@BookDetailsPage,
                        "Favorilere eklenemedi.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@BookDetailsPage, "Bağlantı hatası.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addToToReadList(userId: String, bookId: Int) {
        // Map oluşturuyoruz, burada bookId'yi JSON formatında gönderiyoruz
        val bookIdMap = mapOf("bookId" to bookId.toString())

        // API'ye istek gönderiyoruz
        userApi.addToToReadList(userId, bookIdMap).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Başarılı işlem mesajı
                    Toast.makeText(
                        this@BookDetailsPage,
                        "To Read listesine eklendi.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Başarısız işlem mesajı
                    Toast.makeText(this@BookDetailsPage, "Listeye eklenemedi.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Bağlantı hatası mesajı
                Toast.makeText(this@BookDetailsPage, "Bağlantı hatası.", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun addComment(userId: String, bookId: Int, commentText: String) {
        // Kullanıcı bilgilerini al
        userApi.getUser(userId).enqueue(object : Callback<User> { // Tek bir kullanıcıyı alıyoruz
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        val username = user.username ?: "Unknown User"
                        val userImageUrl = user.imageUrl ?: ""

                        Log.d("username...............", "Username: $username")

                        // Yorum nesnesini hazırla
                        val comment = BookCommentModel(
                            commentId = Random.nextInt(1000, 10000).toString(),
                            bookId = bookId.toString(),
                            userId = userId,
                            imageUrl = userImageUrl,
                            username = username,
                            commentText = commentText,
                            bookName = bookTitle ?: "Unknown Book Title",
                            bookImageUrl = bookImageUrl,
                            CommentModel = mutableListOf()
                        )
                        // Yorum ekle
                        commentApi.addComment(comment).enqueue(object : Callback<BookCommentModel> {
                            override fun onResponse(
                                call: Call<BookCommentModel>,
                                response: Response<BookCommentModel>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@BookDetailsPage,
                                        "Yorum eklendi.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    commentList.add(comment)
                                    recyclerViewAdapter.notifyDataSetChanged()
                                } else {
                                    Toast.makeText(
                                        this@BookDetailsPage,
                                        "Yorum eklenemedi.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<BookCommentModel>, t: Throwable) {
                                Toast.makeText(
                                    this@BookDetailsPage,
                                    "Bağlantı hatası.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    } else {
                        Log.e("username...............", "User is null")
                        Toast.makeText(
                            this@BookDetailsPage,
                            "Kullanıcı bilgileri alınamadı.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e(
                        "username...............",
                        "User API call failed with status: ${response.code()}"
                    )
                    Toast.makeText(
                        this@BookDetailsPage,
                        "Kullanıcı bilgileri alınamadı.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("username...............", "API call failed", t)
                Toast.makeText(
                    this@BookDetailsPage,
                    "Kullanıcı bilgileri alınamadı.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun removeFavorite(userId: String, bookId: String) {
        // API isteğini yapıyoruz
        userApi.removeFavorite(userId, bookId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {

                    Toast.makeText(this@BookDetailsPage, "Favorilerden çıkarıldı!", Toast.LENGTH_SHORT).show()

                } else {
                    // API isteği başarısızsa kullanıcıya mesaj gösteriyoruz
                    Toast.makeText(this@BookDetailsPage, "Favoriden çıkarma işlemi başarısız!", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                // API isteği başarısızsa hata mesajı gösteriyoruz
                Toast.makeText(this@BookDetailsPage, "Bir hata oluştu: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removetoReadList(userId: String, bookId: String) {
        // API isteğini yapıyoruz
        userApi.removeToReadList(userId, bookId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {

                    Toast.makeText(this@BookDetailsPage, "Favorilerden çıkarıldı!", Toast.LENGTH_SHORT).show()

                } else {
                    // API isteği başarısızsa kullanıcıya mesaj gösteriyoruz
                    Toast.makeText(this@BookDetailsPage, "Favoriden çıkarma işlemi başarısız!", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                // API isteği başarısızsa hata mesajı gösteriyoruz
                Toast.makeText(this@BookDetailsPage, "Bir hata oluştu: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
/*
    private fun getFavoriteStatus(userId: String, bookId: String) {
        userApi.showFavoriteById(userId, bookId).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    var isFavorite = response.body() ?: false // API'den dönen değeri alıyoruz, varsayılan olarak false
                    if (isFavorite) {
                        isFavorite = true
                        binding.icFav.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorSelected))
                        // Kitap zaten favorilerde, işlemi gerçekleştirebiliriz true
                        Toast.makeText(this@BookDetailsPage, "Kitap zaten favorilerde!", Toast.LENGTH_SHORT).show()
                    } else {
                        isFavorite = false;
                        binding.icFav.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorUnselected));
                        Toast.makeText(this@BookDetailsPage, "Kitap favorilere eklendi!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // API isteği başarısızsa kullanıcıya mesaj gösteriyoruz
                    Toast.makeText(this@BookDetailsPage, "Favori kontrolü sırasında bir hata oluştu!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                // API isteği başarısızsa hata mesajı gösteriyoruz
                Toast.makeText(this@BookDetailsPage, "Bir hata oluştu: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
*/

}


