package com.eda.bookapp2.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.eda.bookapp2.databinding.ActivityLoginPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginPage : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = Firebase.auth

        val currentUser = auth.currentUser
        if(currentUser != null){
            //burda eğer kullanıcı giriş yaptıysa ve uygulama arka planda çalışıyorsa sürekli olarak giriş yapmasına gerek kalmadan
            // direkt olarak ana sayfaya yönlendiriyorum.
            val intent = Intent(this,MainPage::class.java)
            startActivity(intent)
            finish()
        }else{

        }
    }

    fun loginClicked(view: View){
        val email = binding.username.text.toString()
        val password = binding.password.text.toString()

        if(email.equals("") || password.equals("")){
            Toast.makeText(this,"Enter email and password",Toast.LENGTH_LONG).show()
        }
        else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent = Intent(this@LoginPage, MainPage::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }


        }
    }
}