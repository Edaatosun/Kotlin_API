package com.eda.bookapp2.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eda.bookapp2.R
import com.eda.bookapp2.databinding.ActivityAboutPageBinding

class AboutPage : AppCompatActivity() {

    private lateinit var binding: ActivityAboutPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backBtn.setOnClickListener() {
            finish()
        }
    }
}