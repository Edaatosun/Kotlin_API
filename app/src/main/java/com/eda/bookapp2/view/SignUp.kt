package com.eda.bookapp2.view

import com.eda.bookapp2.model.User
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.eda.bookapp2.constants.Constants
import com.eda.bookapp2.databinding.ActivitySignUpBinding
import com.eda.bookapp2.server.UserApiService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class signUp : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        binding.signUpButton.setOnClickListener(){
            signUp()
        }
    }

    fun signUp(){
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val username = binding.username.text.toString()
        val age = binding.age.text.toString()
        val imageUrl = "https://firebasestorage.googleapis.com/v0/b/bookappkotlin-ba0eb.firebasestorage.app/o/images%2Fimage_acount.png?alt=media&token=859097fb-2846-4f8e-956f-22c3e943c487"

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Enter email, password, and username", Toast.LENGTH_LONG).show()
        }
        else{
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener{

                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val user = User(
                        id = userId,
                        username = username,
                        email = email,
                        age = age,
                        imageUrl = imageUrl,
                        favoriteBooks = mutableListOf(), // Yeni kullanıcı için favori kitaplar boş
                        toReadBooks = mutableListOf()
                    )
                    addUserToDatabase(user)
                }
            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }


        }


    }
    fun addUserToDatabase(user: User) {
        // Retrofit yapılandırması
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)  // API'nin temel URL'si
            .addConverterFactory(GsonConverterFactory.create())  // JSON dönüşümü için Gson
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .build()

        // API servisini oluşturuyoruz
        val service = retrofit.create(UserApiService::class.java)

        // Kullanıcıyı eklemek için API çağrısı yapıyoruz
        val call = service.createUser(user)

        // Yanıtı işlemek için enqueue kullanıyoruz
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // Başarılı bir yanıt geldi
                    Toast.makeText(this@signUp, "Kullanıcı başarıyla kaydedildi", Toast.LENGTH_SHORT).show()

                    // Login sayfasına yönlendirme
                    val intent = Intent(this@signUp, LoginPage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Sunucudan hata yanıtı geldi
                    val errorBody = response.errorBody()?.string() ?: "Bilinmeyen bir hata oluştu"
                    Toast.makeText(this@signUp, "Kullanıcı eklenemedi: $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                // Ağ hatası veya beklenmedik hata durumu
                Toast.makeText(this@signUp, "Bağlantı hatası: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }


}