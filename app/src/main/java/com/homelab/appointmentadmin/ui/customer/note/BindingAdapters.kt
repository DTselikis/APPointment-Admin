package com.homelab.appointmentadmin.ui.customer.note

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homelab.appointmentadmin.model.network.Note

@BindingAdapter("notes")
fun bindNotes(recyclerView: RecyclerView, notes: List<Note>?) {
    val adapter = recyclerView.adapter as NoteAdapter

    adapter.submitList(notes)
}