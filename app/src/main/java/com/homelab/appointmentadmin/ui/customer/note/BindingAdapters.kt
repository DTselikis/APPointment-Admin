package com.homelab.appointmentadmin.ui.customer.note

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
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

@BindingAdapter("date")
fun bindDate(materialTextView: MaterialTextView, timestamp: Timestamp?) {
    timestamp?.let {
        materialTextView.text =
            SimpleDateFormat("E dd/MM/yy", Locale.getDefault()).format(timestamp.toDate())
    }
}