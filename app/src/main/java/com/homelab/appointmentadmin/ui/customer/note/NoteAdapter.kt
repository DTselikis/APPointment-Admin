package com.homelab.appointmentadmin.ui.customer.note

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homelab.appointmentadmin.databinding.NoteItemBinding
import com.homelab.appointmentadmin.model.network.Note

class NoteAdapter(
    private val notesFragment: NotesFragment
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(Diffcallback) {

    inner class NoteViewHolder(
        private val binding: NoteItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.note = note
            binding.itemCard.setOnClickListener {
                notesFragment.editNote(note)
            }
        }
    }

    companion object Diffcallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.title == newItem.title || oldItem.description == newItem.description
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}