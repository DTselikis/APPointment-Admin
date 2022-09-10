package com.homelab.appointmentadmin.ui.customer.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentNotesBinding
import com.homelab.appointmentadmin.model.network.Note
import com.homelab.appointmentadmin.ui.customer.CustomerProfileSharedViewModel
import kotlinx.coroutines.flow.collectLatest

class NotesFragment : Fragment() {

    private val sharedViewModel: CustomerProfileSharedViewModel by activityViewModels()
    private val viewModel: NotesViewModel by viewModels {
        NotesViewModelFactory(sharedViewModel.user.value!!)
    }
    private lateinit var binding: FragmentNotesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes, null, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            notesFragment = this@NotesFragment
            viewModel = this@NotesFragment.viewModel
            notesRv.adapter = NoteAdapter(this@NotesFragment)
        }

        viewModel.gDriveInitialize(requireContext())

        observeNewNoteStored()
    }

    fun createNote() {
        viewModel.newNoteMode()
        showNote()
    }

    fun editNote(note: Note) {
        viewModel.editNoteMode(note)
        showNote()
    }

    fun saveNote() {
        binding.notesProgress.show()

        if (viewModel.isInNewNoteMode) {
            viewModel.saveNewNote()
        } else {
            viewModel.saveChanges()
        }
    }

    private fun showNote() {
        binding.cardFrame.apply {
            reset()
            show(200)
        }
    }

    private fun hideNote() {
        binding.cardFrame.hide(200)
    }

    private fun observeNewNoteStored() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.newNoteStored.collectLatest { stored ->
                binding.notesProgress.hide()

                val text =
                    if (stored) {
                        hideNote()
                        getString(R.string.note_saved)
                    } else getString(R.string.note_not_saved)

                Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun FrameLayout.reset() {
        scaleX = 0f
        scaleY = 0f
        alpha = 0f
        visibility = View.VISIBLE
    }

    private fun FrameLayout.show(duration: Long) {
        animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .duration = duration
    }

    private fun FrameLayout.hide(duration: Long) {
        animate()
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .apply {
                this.duration = duration
                withEndAction { binding.cardFrame.visibility = View.GONE }
            }
    }

}