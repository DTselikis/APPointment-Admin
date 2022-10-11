package gr.evasscissors.appointmentadmin.ui.customer.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import gr.evasscissors.appointmentadmin.databinding.NotePhotoItemBinding
import gr.evasscissors.appointmentadmin.model.network.NotePhoto

class NoteImagesAdapter(private val notesViewModel: NotesViewModel) :
    ListAdapter<NotePhoto, NoteImagesAdapter.NoteImagesViewHolder>(DiffCallback) {

    inner class NoteImagesViewHolder(
        private val binding: NotePhotoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notePhoto: NotePhoto) {
            notesViewModel.bindNotePhoto(binding.notePhoto, notePhoto)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<NotePhoto>() {
        override fun areItemsTheSame(oldItem: NotePhoto, newItem: NotePhoto): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NotePhoto, newItem: NotePhoto): Boolean {
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