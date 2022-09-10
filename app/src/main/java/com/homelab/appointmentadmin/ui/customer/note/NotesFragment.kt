package com.homelab.appointmentadmin.ui.customer.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentNotesBinding
import com.homelab.appointmentadmin.model.network.Note
import com.homelab.appointmentadmin.ui.customer.CustomerProfileSharedViewModel
import com.homelab.appointmentadmin.utils.NotesImagesManager
import kotlinx.coroutines.flow.collectLatest
import java.io.IOException

class NotesFragment : Fragment() {

    private val sharedViewModel: CustomerProfileSharedViewModel by activityViewModels()
    private val viewModel: NotesViewModel by viewModels {
        NotesViewModelFactory(sharedViewModel.user.value!!)
    }
    private lateinit var binding: FragmentNotesBinding

    private lateinit var backPressedCallback: OnBackPressedCallback

    private val openGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val mimeType = requireContext().contentResolver.getType(it)
                try {
                    val image = NotesImagesManager.copyFileToInternalAppStorage(
                        requireActivity(),
                        requireContext(),
                        uri,
                        viewModel.getCurrentNotesHash()
                    )

                    binding.notesProgress.show()
                    viewModel.uploadPhoto(image!!, mimeType!!)
                } catch (e: IOException) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.img_copy_err),
                        Toast.LENGTH_SHORT
                    )
                }
            }
        }

    private val requestAuthorization =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.uploadPhoto()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                if (viewModel.isNoteVisible()) {
                    saveNote()
                } else {
                    sharedViewModel.pressBackBtn()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
    }

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
            notePhotosRv.adapter = NoteImagesAdapter()
        }

        viewModel.gDriveInitialize(requireContext())
        NotesImagesManager.initialize(requireContext())

        observeNewNoteStored()
        observeNoteDeleted()
        observeNeedsAuthorization()
        observePhotoUploaded()
    }

    override fun onResume() {
        super.onResume()
        backPressedCallback.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        backPressedCallback.isEnabled = false
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
            viewModel.saveNewNote()?.let { hideNote() }
        } else {
            viewModel.saveChanges()?.let { hideNote() }
        }
    }

    fun deleteNote(existingNote: Note) {
        binding.notesProgress.show()

        viewModel.deleteNote(existingNote)
    }

    fun addPhotoToNote() {
        openGallery.launch("image/*")
    }

    private fun showNote() {
        binding.cardFrame.apply {
            reset()
            show(200)
        }
        binding.newNoteBtn.visibility = View.GONE
    }

    private fun hideNote() {
        binding.notesProgress.hide()
        binding.cardFrame.hide(200)
        binding.newNoteBtn.visibility = View.VISIBLE
    }

    private fun observeNewNoteStored() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.newNoteStored.collectLatest { stored ->
                val text =
                    if (stored) {
                        hideNote()
                        getString(R.string.note_saved)
                    } else getString(R.string.note_not_saved)

                Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeNoteDeleted() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.noteDeleted.collectLatest { deleted ->
                val text =
                    if (deleted) getString(R.string.note_deleted) else getString(R.string.note_not_deleted)

                Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeNeedsAuthorization() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.needsAuthorization.collectLatest { requestAuthIntent ->
                requestAuthorization.launch(requestAuthIntent)
            }
        }
    }

    private fun observePhotoUploaded() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.photoUploaded.collectLatest { uploaded ->
                binding.notesProgress.hide()

                val text =
                    if (uploaded) getString(R.string.image_stored) else getString(R.string.image_not_stored)

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