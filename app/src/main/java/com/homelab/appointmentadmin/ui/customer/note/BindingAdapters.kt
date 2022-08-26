package com.homelab.appointmentadmin.ui.customer.note

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.Timestamp
import com.homelab.appointmentadmin.model.network.Note
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("notes")
fun bindNotes(recyclerView: RecyclerView, notes: List<Note>?) {
    val adapter = recyclerView.adapter as NoteAdapter

    adapter.submitList(notes)
}

@BindingAdapter("notePhotos")
fun bindNotePhotos(recyclerView: RecyclerView, photos: List<String>?) {
    recyclerView.adapter?.let {
        val adapter = it as NoteImagesAdapter

        adapter.submitList(photos)
    }
}

@BindingAdapter("date")
fun bindDate(materialTextView: MaterialTextView, timestamp: Timestamp?) {
    timestamp?.let {
        materialTextView.text =
            SimpleDateFormat("E dd/MM/yy", Locale.getDefault()).format(timestamp.toDate())
    }
}

@BindingAdapter("imgUrl")
fun bindProfilePic(imageView: ImageView, url: String) {
    imageView.load(url) {
        memoryCachePolicy(CachePolicy.ENABLED)
        diskCachePolicy(CachePolicy.ENABLED)
    }
}