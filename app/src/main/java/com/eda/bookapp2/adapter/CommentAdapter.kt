package com.eda.bookapp2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eda.bookapp2.databinding.BookCommentCardBinding
import com.eda.bookapp2.model.BookCommentModel
import com.eda.bookapp2.view.BookDetailsPage
import com.eda.bookapp2.view.CommentPage
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
class CommentAdapter(
    private val context: Context,
    private val commentList: List<BookCommentModel>,
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    class CommentViewHolder(val binding: BookCommentCardBinding) : RecyclerView.ViewHolder(binding.root) {
        val profileImageView: ShapeableImageView = binding.profilepicture
        val usernameText: TextView = binding.usernameCard
        val commentText: TextView = binding.commentText
        val bookNameText: TextView = binding.booknameCard
        val bookImageView: ImageView = binding.bookpicture
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = BookCommentCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val currentItem = commentList[position]

        // Profil ve kitap resimlerini yükle
        Picasso.get().load(currentItem.imageUrl).into(holder.profileImageView)
        holder.usernameText.text = currentItem.username
        holder.commentText.text = currentItem.commentText
        holder.bookNameText.text = currentItem.bookName
        Picasso.get().load(currentItem.bookImageUrl).into(holder.bookImageView)
/*
        // Yorum ekleme butonuna tıklama olayını ekliyoruz
        holder.binding.addComment.setOnClickListener {
            val intent = Intent(it.context, CommentPage::class.java)

            // Kitap ID'sini Intent ile gönderiyoruz
            val commentId = currentItem.commentId // commentId'yi doğru şekilde alıyoruz
            intent.putExtra("commentId", commentId)

            // Intent ile CommentPage'e geçiş yapıyoruz
            it.context.startActivity(intent)
        }*/

    }

    override fun getItemCount(): Int {
        return commentList.count()
    }
}

/* <!-- CardView
    <androidx.cardview.widget.CardView
        android:layout_below="@id/topBar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="5dp"
        android:padding="16dp"
        app:cardCornerRadius="12dp"
        app:cardElevation="4dp"
        app:cardBackgroundColor="@color/gray">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <!-- Profil Bölümü -->
            <RelativeLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:padding="8dp">

                <com.google.android.material.imageview.ShapeableImageView
                    android:id="@+id/profilepicture"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:layout_alignParentStart="true"
                    android:scaleType="centerCrop"
                    android:src="@drawable/icon_login_account"
                    app:strokeWidth="2dp" />

                <TextView
                    android:id="@+id/username_card"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginStart="12dp"
                    android:layout_toEndOf="@id/profilepicture"
                    android:text="Person Name"
                    android:textStyle="bold"
                    android:textColor="#D3D3D3"
                    android:textSize="16sp" />
            </RelativeLayout>

            <!-- Ayırıcı Çizgi -->
            <View
                android:layout_width="match_parent"
                android:layout_height="1dp"
                android:layout_marginVertical="8dp"
                android:background="@color/white"
                android:baselineAligned="false" />

            <!-- Kullanıcı Yorumu -->
            <TextView
                android:id="@+id/comment_text"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginStart="5dp"
                android:layout_marginBottom="8dp"
                android:text="This is a sample comment about the book. It will be  after a few lines if it exceeds the maximum length."
                android:textColor="#CCCCCC"
                android:textSize="14sp" />

            <!-- Kitap Bölümü -->
            <RelativeLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:padding="8dp">

                <ImageView
                    android:id="@+id/bookpicture"
                    android:layout_width="30dp"
                    android:layout_height="40dp"
                    android:layout_alignParentStart="true"
                    android:scaleType="centerCrop"
                    android:src="@drawable/book01" />

                <TextView
                    android:id="@+id/bookname_card"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_alignTop="@id/bookpicture"
                    android:layout_marginStart="12dp"
                    android:layout_toEndOf="@id/bookpicture"
                    android:text="Book Name"
                    android:textColor="#FFFFFF"
                    android:textSize="16sp"
                    android:textStyle="bold" />
            </RelativeLayout>

            <View
                android:layout_width="match_parent"
                android:layout_height="1dp"
                android:layout_marginVertical="8dp"
                android:background="@color/white"
                android:baselineAligned="false" />

            <!-- like ve comment kısımları -->
            <ScrollView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content">

                <RelativeLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:padding="8dp">

                    <LinearLayout
                        android:id="@+id/commentLinearLayout"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal">

                        <TextView
                            android:id="@+id/commentTitle"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:text="Yorumlar"
                            android:textSize="16sp"
                            android:textColor="@color/white"
                            android:textStyle="bold" />
                    </LinearLayout>

                    <androidx.recyclerview.widget.RecyclerView
                        android:id="@+id/commentRecycleView2"
                        android:layout_below="@id/commentLinearLayout"
                        android:layout_width="match_parent"
                        android:layout_marginTop="10dp"
                        android:layout_height="wrap_content"
                        tools:listitem="@layout/comment" />

                    <LinearLayout
                        android:layout_below="@id/commentRecycleView2"
                        android:layout_marginTop="10dp"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal">

                        <RelativeLayout
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content">

                            <EditText
                                android:id="@+id/commentEdit"
                                android:layout_width="0dp"
                                android:layout_height="wrap_content"
                                android:layout_alignParentStart="true"
                                android:layout_toStartOf="@id/sendButton"
                                android:hint="Yorum Yap"
                                android:textColorHint="@color/white"
                                android:textColor="@color/black"
                                android:background="@drawable/rounded_corner"
                                android:textSize="16sp"
                                android:padding="15dp"
                                android:paddingEnd="16dp" />

                            <ImageButton
                                android:id="@+id/sendButton"
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:src="@drawable/ic_send_comment"
                                android:contentDescription="Gönder"
                                android:layout_alignParentEnd="true"
                                android:layout_centerVertical="true"
                                android:background="?android:attr/selectableItemBackground"
                                android:padding="10dp" />
                        </RelativeLayout>
                    </LinearLayout>
                </RelativeLayout>
            </ScrollView>
        </LinearLayout>
    </androidx.cardview.widget.CardView> */