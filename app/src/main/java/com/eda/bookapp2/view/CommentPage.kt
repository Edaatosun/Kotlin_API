package com.eda.bookapp2.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eda.bookapp2.adapter.BookRecycleViewAdapter
import com.eda.bookapp2.adapter.CommentAdapter
import com.eda.bookapp2.databinding.ActivityCommentPageBinding
import com.eda.bookapp2.model.BookCommentModel
import com.eda.bookapp2.server.CommentApiService
import com.eda.bookapp2.constants.Constants
import com.eda.bookapp2.model.CommentModel
import com.eda.bookapp2.model.User
import com.eda.bookapp2.server.UserApiService
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommentPage : AppCompatActivity() {

    private lateinit var binding: ActivityCommentPageBinding
    private lateinit var commentId: String  // commentId'yi alacağız
    private lateinit var commentList: MutableList<BookCommentModel>
    private lateinit var recyclerViewAdapter: LittleCommentAdapter
    private lateinit var commentApi: CommentApiService
    private lateinit var userApi: UserApiService
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
/*
        // Intent'ten commentId'yi alıyoruz
        val commentId = intent.getStringExtra("commentId") ?: ""
        if (commentId.isEmpty()) {
            Toast.makeText(this, "Yorum ID bulunamadı", Toast.LENGTH_SHORT).show()
            finish()  // Aktiviteyi sonlandırarak çökmesini engelleyebilirsin
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        commentApi = retrofit.create(CommentApiService::class.java)
        userApi = retrofit.create(UserApiService::class.java)

        auth = FirebaseAuth.getInstance()
        val userId = auth.uid

        binding.sendButton.setOnClickListener {
            val commentText = binding.commentEdit.text.toString()  // Yorum metni

            if (commentText.isNotEmpty()) {
                val userId = auth.uid // Firebase'den userId'yi alıyoruz

                // getUser fonksiyonunu asenkron çağırıyoruz
                getUser(userId ?: "") { username ->
                    if (username.isNotEmpty()) {
                        val newComment = CommentModel(
                            commentModelId = "", // Yeni yorumun ID'si
                            username = username,
                            commentText = commentText
                        )

                        // Yorum ekleme işlemi
                        addComment(newComment, commentId)
                    } else {
                        Toast.makeText(this, "Kullanıcı adı alınamadı", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Lütfen yorumunuzu yazın", Toast.LENGTH_SHORT).show()
            }
        }

        // Veri yükleme işlemini başlatıyoruz
        //loadData(commentId)*/
    }
/*
    private fun getUser(userId: String, callback: (String) -> Unit) {
        userApi.getUser(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()
                    callback(user?.username ?: "")  // Kullanıcı adı döndürülüyor
                } else {
                    Toast.makeText(this@CommentPage, "User data not found", Toast.LENGTH_SHORT).show()
                    callback("")  // Kullanıcı adı bulunamazsa boş döndürülüyor
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@CommentPage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                callback("")  // Hata durumunda boş döndürülüyor
            }
        })
    }


    private fun loadData(commentId: String) {
        // Callback metodu ile API yanıtını alıyoruz
        commentApi.getCommentsById(commentId).enqueue(object : Callback<List<BookCommentModel>> {
            override fun onResponse(
                call: Call<List<BookCommentModel>>,
                response: Response<List<BookCommentModel>>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    commentList.clear()
                    response.body()?.let { commentList.addAll(it) }

                    // Her bir yorumu displayData fonksiyonu ile göster
                    for (comment in commentList) {
                        displayData(comment)
                    }

                  /*  binding.commentRecycleView2.layoutManager = LinearLayoutManager(this@CommentPage)
                    recyclerViewAdapter = LittleCommentAdapter(commentList)
                    binding.commentRecycleView2.adapter = recyclerViewAdapter
                    recyclerViewAdapter.notifyDataSetChanged()*/
                } else {
                    Toast.makeText(this@CommentPage, "Veri yüklenemedi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<BookCommentModel>>, t: Throwable) {
                // API çağrısı başarısız olursa hata mesajı gösteriyoruz
                Toast.makeText(
                    this@CommentPage,
                    "Bir hata oluştu: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun displayData(comment: BookCommentModel) {
        Picasso.get().load(comment.imageUrl).into(binding.profilepicture)

        // Kitap resmi URL'sini yükleme (null kontrolü yapıyoruz)
        if (comment.bookImageUrl != null) {
            Picasso.get().load(comment.bookImageUrl).into(binding.bookpicture)
        }
        // Yorum verilerini uygun alanlara set etme
        binding.usernameCard.text = comment.username
        binding.commentText.text = comment.commentText
        binding.booknameCard.text = comment.bookName
    }

    private fun addComment(commentModel: CommentModel, commentId: String) {
        // API'ye Yorum ekleme
        commentApi.addComment2(commentModel).enqueue(object : Callback<CommentModel> {
            override fun onResponse(call: Call<CommentModel>, response: Response<CommentModel>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CommentPage, "Yorum eklendi.", Toast.LENGTH_SHORT).show()

                    // Yorum başarıyla eklendiğinde, commentId'ye göre ikinci POST işlemi yapılır
                    addCommentToSpecificId(commentModel, commentId)

                    recyclerViewAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@CommentPage, "Yorum eklenemedi.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CommentModel>, t: Throwable) {
                Toast.makeText(this@CommentPage, "Bağlantı hatası.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Yeni Yorum Eklemek İçin İkinci API Çağrısı
    private fun addCommentToSpecificId(commentModel: CommentModel, commentId: String) {
        // Yorum ekleme işlemi
        val commentData = mapOf(
            "commentModelId" to commentModel.commentModelId,
            "username" to commentModel.username,
            "commentText" to commentModel.commentText
        )

        commentApi.addCommentModel(commentId, commentData).enqueue(object : Callback<List<BookCommentModel>> {
            override fun onResponse(call: Call<List<BookCommentModel>>, response: Response<List<BookCommentModel>>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@CommentPage,
                        "Yorum başarılı şekilde 'commentId'ye eklendi.",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadData(commentId) // Reload the comments after successfully adding
                } else {
                    Toast.makeText(
                        this@CommentPage,
                        "Yorum 'commentId'ye eklenemedi.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<BookCommentModel>>, t: Throwable) {
                Toast.makeText(
                    this@CommentPage,
                    "Bağlantı hatası.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
*/
}
