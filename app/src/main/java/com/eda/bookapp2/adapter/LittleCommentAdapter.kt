package com.eda.bookapp2.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eda.bookapp2.databinding.CommentBinding
import com.eda.bookapp2.model.BookCommentModel


class LittleCommentAdapter(private val commentList: List<BookCommentModel>) : RecyclerView.Adapter<LittleCommentAdapter.BookCommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookCommentViewHolder {
        val binding = CommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookCommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookCommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        //4 öge gösterilecek
        return if (commentList.size > 4) 4 else commentList.size
    }


    class BookCommentViewHolder(private val binding: CommentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: BookCommentModel) {
            // Set the comment data to the views
            binding.usernameTxt.text = comment.username
            binding.commentTxt.text = comment.commentText

        }
    }
}
