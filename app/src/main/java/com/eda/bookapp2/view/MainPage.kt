package com.eda.bookapp2.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eda.bookapp2.R
import com.eda.bookapp2.adapter.CommentAdapter
import com.eda.bookapp2.constants.Constants
import com.eda.bookapp2.databinding.ActivityMainPageBinding
import com.eda.bookapp2.model.BookCommentModel
import com.eda.bookapp2.server.CommentApiService
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainPage : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding
    private lateinit var recyclerView: RecyclerView
    private var recyclerViewAdapter: CommentAdapter? = null

    private lateinit var drawer: DrawerLayout
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        recyclerView = binding.BookCommentCards // Bu satır zaten mevcut
        recyclerView.layoutManager = LinearLayoutManager(this) // LinearLayoutManager ile RecyclerView'ı başlatıyoruz


        drawer = binding.drawerLayout
        auth = FirebaseAuth.getInstance()

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.BookCommentCards.layoutManager = layoutManager

        val selectedIconMenu: ImageButton = binding.drawerMenu

        selectedIconMenu.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }

        // NavigationView için öğe tıklama işlemleri
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_acount -> {
                    startActivity(Intent(this@MainPage, ProfilPage::class.java))
                }
                R.id.nav_logout -> {
                    auth.signOut()
                    startActivity(Intent(this@MainPage, LoginPage::class.java))
                    finish()
                }
                R.id.nav_about -> {
                    startActivity(Intent(this@MainPage, AboutPage::class.java))
                }
                R.id.nav_books -> {
                    startActivity(Intent(this@MainPage, BookPage::class.java))
                }
                R.id.nav_fav -> {
                    startActivity(Intent(this@MainPage, favouritePage::class.java))
                }
                R.id.action_will_read_list -> {
                    startActivity(Intent(this@MainPage, toReadList::class.java))
                }
            }
            // Menü kapama
            drawer.closeDrawer(GravityCompat.START)
            true
        }

        // API'den veri çekme
        fetchComments()
    }

    private fun fetchComments() {
        // Retrofit nesnesi oluşturuluyor
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)  // API'nin base URL'ini Constants class'ından alıyoruz
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Retrofit ile API servisi oluşturuluyor
        val service = retrofit.create(CommentApiService::class.java)

        // API'den yorumları çekmek için çağrı yapıyoruz
        val call = service.getComments()

        // Callback metodu ile API yanıtını alıyoruz
        call.enqueue(object : Callback<List<BookCommentModel>> {
            override fun onResponse(
                call: Call<List<BookCommentModel>>,
                response: Response<List<BookCommentModel>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    // API'den gelen veriyi alıyoruz
                    val commentList = response.body()!!
                    recyclerViewAdapter = CommentAdapter(this@MainPage, commentList)
                    binding.BookCommentCards.adapter = recyclerViewAdapter
                } else {
                    Toast.makeText(this@MainPage, "Veri yüklenemedi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<BookCommentModel>>, t: Throwable) {
                // API çağrısı başarısız olursa hata mesajı gösteriyoruz
                Toast.makeText(this@MainPage, "Bir hata oluştu: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
