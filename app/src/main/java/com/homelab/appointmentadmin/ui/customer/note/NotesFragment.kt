package com.homelab.appointmentadmin.ui.customer.note

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            notesRv.adapter = NoteAdapter(this@NotesFragment)
            viewModel = this@NotesFragment.viewModel
            notesFragment = this@NotesFragment
        }

        viewModel.fetchNotes()
    }

    fun showNote(x: Float, y: Float) {
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

}