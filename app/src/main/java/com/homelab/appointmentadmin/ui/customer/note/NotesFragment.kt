package com.homelab.appointmentadmin.ui.customer.note

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.homelab.appointmentadmin.utils.GoogleDriveHelper
import com.homelab.appointmentadmin.utils.NotesImagesManager
import kotlinx.coroutines.flow.collectLatest
import java.io.IOException

class NotesFragment : Fragment() {

    private val sharedViewModel: CustomerProfileSharedViewModel by activityViewModels()
    private val viewModel: NotesViewModel by viewModels {
        NotesViewModelFactory(sharedViewModel.user.value!!)
    }
    private lateinit var binding: FragmentNotesBinding
    private lateinit var adapter: NoteAdapter

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
                        viewModel.timestamp!!
                    )

                    viewModel.addPhotoToNote(image!!.absolutePath)

                    viewModel.uploadFile(image, mimeType)
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
            viewModel.uploadFile()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes, null, false)

        backPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                if (viewModel.isNoteVisible()) {
                    back()
                } else {
                    sharedViewModel.pressBackBtn()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)

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

        observeChangedStoredtoDB()
        observeForNoteDeletion()
        observeNeedsAuthorization()
        observeImageUploaded()

        GoogleDriveHelper.initialize(requireContext())
        viewModel.initializeFolderStructure()
        NotesImagesManager.initialize(requireContext())
    }

    override fun onResume() {
        super.onResume()
        backPressedCallback.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        backPressedCallback.isEnabled = false
    }

    fun showNote() {
        viewModel.setNoteVisible(true)
        binding.notePhotosRv.adapter = NoteImagesAdapter()

        binding.cardFrame.apply {
            scaleX = 0f
            scaleY = 0f
            alpha = 0f
            visibility = View.VISIBLE
        }
        binding.newNoteBtn.visibility = View.GONE

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
                binding.newNoteBtn.visibility = View.VISIBLE
                updateAdapter()
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationRepeat(p0: Animator?) {}
        })
    }

    fun deleteNote(note: Note) {
        viewModel.deleteNote(note)
    }

    fun back() {
        hideNote()
        viewModel.setNoteVisible(false)
    }

    fun createNote() {
        viewModel.setNewNoteState()
        showNote()
    }

    fun editNote(note: Note) {
        viewModel.setSelectedNote(note)
        showNote()
    }

    fun addPhoto() {
        openGallery.launch("image/*")
    }

    private fun updateAdapter() {
        viewModel.apply {
            if (isNewNote()) {
                storeNewNoteToDB()
            } else if (isModified()) {
                storeChangesToDB()
            }
        }
    }

    private fun observeChangedStoredtoDB() {
        viewModel.updatesStored.observe(viewLifecycleOwner) { stored ->
            val msg =
                if (stored) getString(R.string.note_saved) else getString(R.string.note_not_saved)

            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeForNoteDeletion() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.noteDeleted.collectLatest { deleted ->
                val text = if (deleted) {
                    getString(R.string.delete_success)
                } else getString(R.string.delete_fail)

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

    private fun observeImageUploaded() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.imageUploaded.collectLatest { uploaded ->
                val text =
                    if (uploaded) getString(R.string.image_stored)
                    else getString(R.string.image_not_stored)

                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }
        }
    }
}