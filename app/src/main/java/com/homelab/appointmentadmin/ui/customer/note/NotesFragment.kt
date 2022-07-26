package com.homelab.appointmentadmin.ui.customer.note

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentNotesBinding
import com.homelab.appointmentadmin.model.network.Note
import com.homelab.appointmentadmin.ui.customer.CustomerProfileSharedViewModel

class NotesFragment : Fragment() {

    private val sharedViewModel: CustomerProfileSharedViewModel by activityViewModels()
    private val viewModel: NotesViewModel by viewModels {
        NotesViewModelFactory(sharedViewModel.user.value!!)
    }
    private lateinit var binding: FragmentNotesBinding
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes, null, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NoteAdapter(this@NotesFragment)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            notesRv.adapter = adapter
            viewModel = this@NotesFragment.viewModel
            notesFragment = this@NotesFragment
        }

        viewModel.fetchNotes()

        observeChangedStoredtoDB()
    }

    fun showNote() {
        binding.cardFrame.apply {
            scaleX = 0f
            scaleY = 0f
            alpha = 0f
            visibility = View.VISIBLE
        }

        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f)
        val animator =
            ObjectAnimator.ofPropertyValuesHolder(binding.cardFrame, scaleX, scaleY, alpha)
        animator.start()
    }

    private fun hideNote() {
        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 0f)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 0f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f)
        val animator =
            ObjectAnimator.ofPropertyValuesHolder(binding.cardFrame, scaleX, scaleY, alpha)
        animator.start()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {
                binding.cardFrame.visibility = View.GONE
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationRepeat(p0: Animator?) {}
        })
    }

    fun back() {
        viewModel.apply {
            if (isNewNote()) {
                storeNewNoteToDB()
            } else if (isModified()) {
                storeChangesToDB()
            }
        }
        hideNote()
        updateAdapter()
    }

    fun createNote() {
        viewModel.setNewNoteState()
        showNote()
    }

    fun editNote(note: Note) {
        viewModel.setSelectedNote(note)
        showNote()
    }

    private fun updateAdapter() {
        adapter.notifyDataSetChanged()
    }

    private fun observeChangedStoredtoDB() {
        viewModel.updatesStored.observe(viewLifecycleOwner) { stored ->
            val msg =
                if (stored) getString(R.string.note_saved) else getString(R.string.note_not_saved)

            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }
}