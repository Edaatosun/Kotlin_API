package com.eda.bookapp2.view

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eda.bookapp2.R
import com.eda.bookapp2.constants.Constants
import com.eda.bookapp2.databinding.ActivityProfilPageBinding
import com.eda.bookapp2.databinding.EditProfileCardsBinding
import com.eda.bookapp2.model.User
import com.eda.bookapp2.server.UserApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class ProfilPage : AppCompatActivity() {
    private lateinit var binding: ActivityProfilPageBinding
    private lateinit var auth: FirebaseAuth
    private var txt: String? = null
    private var userId: String? = null
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        userId = auth.currentUser?.uid

        userId?.let {
            loadData(it)
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Lütfen bekleyin...")

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.icAdd.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@ProfilPage, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this@ProfilPage, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101
                )
            } else {
                openGallery()
            }
        }

        // Edit butonlarına tıklama işlemi
        binding.usernameEdit.setOnClickListener { v ->
            txt = "username"
            userId?.let { openEditProfileDialog(txt!!, it) }
        }

        binding.emailEdit.setOnClickListener { v ->
            txt = "email"
            userId?.let { openEditProfileDialog(txt!!, it) }
        }

        binding.ageEdit.setOnClickListener { v ->
            txt = "age"
            userId?.let { openEditProfileDialog(txt!!, it) }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 102)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102 && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                updateUserImage(imageUri)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "İzin reddedildi!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserImage(imageUri: Uri) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val storageReference = FirebaseStorage.getInstance().reference
            val imageRef = storageReference.child("profile_images/${userId}.jpg")

            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        updateProfileImageUrl(userId, imageUrl)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@ProfilPage, "Resim yükleme hatası: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun updateProfileImageUrl(userId: String, imageUrl: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(UserApiService::class.java)

        val requestBody = mapOf("imageUrl" to imageUrl)
        val call = service.updateUserImage(userId, requestBody)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProfilPage, "Profil resmi güncellendi!", Toast.LENGTH_SHORT).show()
                    loadData(userId)
                } else {
                    Toast.makeText(this@ProfilPage, "Güncelleme hatası: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@ProfilPage, "API hatası: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun loadData(userId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(UserApiService::class.java)
        val call = service.getUser(userId)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        binding.usernameTxt2.text = user.username
                        binding.emailTxt.text = user.email
                        binding.ageTxt.text = user.age
                        Picasso.get().load(user.imageUrl).into(binding.profilPhoto)
                    } else {
                        Toast.makeText(this@ProfilPage, "Kullanıcı bulunamadı.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@ProfilPage, "Sunucu hatası: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@ProfilPage, "API hatası: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun openEditProfileDialog(txt: String,userId:String) {
        val editBinding: EditProfileCardsBinding = EditProfileCardsBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        builder.setView(editBinding.root)
        val alertDialog = builder.create()
        alertDialog.show()

        when (txt) {
            "username" -> editBinding.editTxt.setHint("Yeni Kullanıcı Adı")
            "email" -> editBinding.editTxt.setHint("Yeni Email")
            "age" -> editBinding.editTxt.setHint("Yeni Yaş")
        }

        editBinding.backBtn.setOnClickListener { alertDialog.dismiss() }

        editBinding.submitBtn.setOnClickListener { v ->
            val editedValue = editBinding.editTxt.text.toString()
            if (editedValue.isEmpty()) {
                Toast.makeText(this, "Lütfen bir değer girin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressDialog.show()

            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(UserApiService::class.java)

            val call: Call<User> = when (txt) {
                "username" -> {
                    service.updateUserField(userId!!, "username", editedValue)
                }
                "email" -> service.updateUserField(userId!!, "email", editedValue)
                "age" -> service.updateUserField(userId!!, "age", editedValue)
                else -> return@setOnClickListener
            }

            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProfilPage, "Başarıyla güncellendi!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProfilPage, "Güncelleme hatası: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                    progressDialog.dismiss()
                    alertDialog.dismiss()
                    userId?.let { loadData(it) }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(this@ProfilPage, "API hatası: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                }
            })
        }
    }
}
