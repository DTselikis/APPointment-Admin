package com.homelab.appointmentadmin.ui.customer.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homelab.appointmentadmin.databinding.NotePhotoItemBinding

class NoteImagesAdapter :
    ListAdapter<String, NoteImagesAdapter.NoteImagesViewHolder>(DiffCallback) {

    inner class NoteImagesViewHolder(
        private val binding: NotePhotoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(path: String) {
            binding.photoUrl = path
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteImagesViewHolder {
        return NoteImagesViewHolder(
            NotePhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteImagesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}