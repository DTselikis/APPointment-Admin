package com.homelab.appointmentadmin.ui.customer.edit

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentCustomerProfileEditBinding
import com.homelab.appointmentadmin.ui.customer.CustomerProfileSharedViewModel

class CustomerProfileEditFragment : Fragment() {

    private val sharedViewModel: CustomerProfileSharedViewModel by activityViewModels()
    private val viewModel: CustomerProfileEditViewModel by viewModels {
        CustomerProfileEditViewModelFactory(sharedViewModel.user.value!!)
    }
    private lateinit var binding: FragmentCustomerProfileEditBinding

    private lateinit var backPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_customer_profile_edit, null, false)

        backPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                if (viewModel.isModified()) {
                    showWarningMessage()
                } else {
                    closeEditsFragment()
                }
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)

        observeSaveStatus()
        observeNavHostSaveBtn()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CustomerProfileEditFragment.viewModel
            customerProfileEditFragment = this@CustomerProfileEditFragment
        }

        if (sharedViewModel.user.value!!.registered) {
            binding.emailEditText.apply {
                isFocusableInTouchMode = false
                setOnClickListener {
                    showEmailWarningDialog()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        backPressedCallback.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        backPressedCallback.isEnabled = false

        if (viewModel.isModified()) {
            viewModel.revertChanges()
        }
    }

    fun saveChanges() {
        if (viewModel.isModified()) {
            viewModel.storeChangesToDB()
        } else {
            Toast.makeText(requireContext(), getString(R.string.no_edits_made), Toast.LENGTH_SHORT)
                .show()
            closeEditsFragment()
        }
    }

    private fun closeEditsFragment() {
        sharedViewModel.pressBackBtn()
    }


    private fun observeSaveStatus() {
        viewModel.changesSaved.observe(viewLifecycleOwner) { saved ->
            val text: String
            val color: Int

            if (saved) {
                text = getString(R.string.save_successful)
                color = Color.parseColor(getString(R.color.teal_200))
            } else {
                text = getString(R.string.save_failed)
                color = Color.parseColor(getString(R.color.email_red))
            }

            Snackbar.make(binding.saveEditsBtn, text, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .setBackgroundTint(color)
                .setAction(getString(R.string.ok)) {
                    sharedViewModel.setUser(viewModel.getUser())
                    closeEditsFragment()
                }
                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onShown(transientBottomBar: Snackbar?) {
                        super.onShown(transientBottomBar)
                    }

                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        sharedViewModel.setUser(viewModel.getUser())
                        closeEditsFragment()
                    }
                })
                .show()
        }
    }

    private fun observeNavHostSaveBtn() {
        sharedViewModel.saveBtnPressed.observe(viewLifecycleOwner) { pressed ->
            if (pressed) {
                saveChanges()
            }
            sharedViewModel.unpressSaveBtn()
        }
    }

    private fun showWarningMessage() {
        activity?.let {
            val builder = androidx.appcompat.app.AlertDialog.Builder(it)
            builder.apply {
                setTitle(getString(R.string.unsaved_changes_warning_title))
                setMessage(getString(R.string.unsaved_changes_warning_msg))
                setPositiveButton(getString(R.string.dialog_yes_btn)) { dialog, id ->
                    closeEditsFragment()
                }
                setNegativeButton(getString(R.string.dialog_no_btn)) { dialog, id ->

                }
            }
                .create()
        }?.show()
    }

    private fun showEmailWarningDialog() {
        activity?.let {
            AlertDialog.Builder(it).apply {
                setTitle(getString(R.string.email_change_warning_title))
                setMessage(getString(R.string.email_change_warning_message))
                setPositiveButton(getString(R.string.ok)) { _, _ -> }
                setOnDismissListener {
                    binding.emailEditText.apply {
                        isFocusableInTouchMode = true
                        setOnClickListener { }
                        requestFocus()

                        val keyboard =
                            requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                        keyboard.showSoftInput(this, InputMethodManager.SHOW_FORCED)
                    }
                }
            }
                .create()
                .show()
        }
    }
}