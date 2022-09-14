package com.homelab.appointmentadmin.ui.customer.note

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.NoteItemBinding
import com.homelab.appointmentadmin.model.network.Note

class NoteAdapter(
    private val notesFragment: NotesFragment,
    private val notesViewModel: NotesViewModel
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(Diffcallback) {

    inner class NoteViewHolder(
        private val binding: NoteItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.note = note
            binding.itemCard.setOnClickListener {
                notesFragment.editNote(note)
            }
            note.photos
                ?.sortedByDescending { it.photoUploaded }
                ?.getOrNull(0)
                ?.let { noteCover ->
                    notesViewModel.bindNotePhoto(binding.noteThumbnailImage, noteCover)
                }
        }
    }

    companion object Diffcallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.hash == newItem.hash
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.title == newItem.title && oldItem.description == newItem.description
                    && oldItem.timestamp == newItem.timestamp && oldItem.photos == newItem.photos
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)

        addContextMenu(holder.itemView, note)

        holder.bind(note)
    }

    private fun addContextMenu(itemView: View, note: Note) {
        itemView.setOnCreateContextMenuListener { contextMenu, view, _ ->
            contextMenu.add(view.context.getString(R.string.delete_user_context_menu_item))
                .setOnMenuItemClickListener {
                    notesFragment.deleteNote(note)
                    true
                }
        }
    }
}