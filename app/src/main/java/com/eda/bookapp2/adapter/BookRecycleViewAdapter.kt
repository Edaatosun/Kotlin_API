package com.eda.bookapp2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eda.bookapp2.databinding.BookCardsBinding
import com.eda.bookapp2.model.Book
import com.squareup.picasso.Picasso

class BookRecycleViewAdapter(
    private val bookList: ArrayList<Book>,
    private val listener: Listener // Dinleyici parametresi
) : RecyclerView.Adapter<BookRecycleViewAdapter.RowHolder>() {

    // Dinleyici arayüzü
    interface Listener {
        fun onItemClick(bookModel: Book)  // Kitap tıklama

    }

    class RowHolder(val binding: BookCardsBinding) : RecyclerView.ViewHolder(binding.root) {
        // ViewHolder burada
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val binding = BookCardsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowHolder(binding)
    }

    override fun getItemCount(): Int {
        return bookList.count()
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        val book = bookList[position]

        // Kitap tıklandığında onItemClick tetiklenir
        holder.itemView.setOnClickListener {
            listener.onItemClick(book)
        }
        // Resim yükleme
        Picasso.get()
            .load(book.imageUrl)
            .into(holder.binding.bookImageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    holder.binding.progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    holder.binding.progressBar.visibility = View.GONE
                    e?.printStackTrace()
                }
            })

        // Kitap bilgilerini yerleştirme
        holder.binding.descriptionTV.text = book.description
        holder.binding.titleTV.text = book.title
        holder.binding.dateTV.text = book.date
    }
}
