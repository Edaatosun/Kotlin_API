package com.eda.bookapp2.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eda.bookapp2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loginButton.setOnClickListener{
            // Yeni aktiviteyi başlat
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }

        binding.signUpButton.setOnClickListener{
            val intent = Intent(this@MainActivity, signUp::class.java)
            startActivity(intent)
            finish()
        }
    }
}