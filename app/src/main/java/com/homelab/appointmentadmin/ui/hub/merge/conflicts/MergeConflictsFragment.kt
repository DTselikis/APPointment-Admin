package com.homelab.appointmentadmin.ui.hub.merge.conflicts

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.data.*
import com.homelab.appointmentadmin.databinding.FragmentMergeConflictsBinding

class MergeConflictsFragment : Fragment() {

    private val args: MergeConflictsFragmentArgs by navArgs<MergeConflictsFragmentArgs>()
    private val viewModel: MergeConflictsViewModel by viewModels()

    private lateinit var binding: FragmentMergeConflictsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_merge_conflicts, null, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@MergeConflictsFragment.viewModel
            mergeConflictsFragment = this@MergeConflictsFragment
        }

        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showWarningMessage()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)

        binding.genderGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    GenderBtnId.FEMALE.code ->
                        viewModel.setGender(GenderBtnId.FEMALE)
                    GenderBtnId.MALE.code ->
                        viewModel.setGender(GenderBtnId.MALE)
                    GenderBtnId.ANY.code ->
                        viewModel.setGender(GenderBtnId.ANY)
                }
            }
        }

        viewModel.setMergingUsers(args.userToBeMerged, args.usetToBeMergedWith)
        observeStoreStatus()
    }

    fun saveChoice() {
        val choice =
            if (binding.unregisteredBtn.isChecked) ConflictChoice.UNREGISTERD else ConflictChoice.REGISTERED

        viewModel.saveChoice(choice)
        binding.conflictsRadioGroup.clearCheck()

        if (viewModel.currentConflict.value != viewModel.numOfConflicts.value) {
            viewModel.nextConflict()
        } else {
            showMergeResult()
        }
    }

    fun mergeUsers() {
        if (isOnline()) {
            viewModel.storeMergedUserToDbTransaction()
        } else {
            viewModel.mergeUsers()
        }
    }

    private fun showMergeResult() {
        viewModel.populateFields()
        checkGenderButton()

        binding.apply {
            conflictsGroup.visibility = View.GONE
            mergeResult.visibility = View.VISIBLE
        }
    }

    private fun checkGenderButton() {
        binding.apply {
            val (id, gender) = when (viewModel!!.getUserToBeMerged().gender) {
                Gender.FEMALE.code -> Pair(R.id.female_option, GenderBtnId.FEMALE)
                Gender.MALE.code -> Pair(R.id.male_option, GenderBtnId.MALE)
                else -> Pair(R.id.any_option, GenderBtnId.ANY)
            }

            genderGroup.check(id)
            viewModel!!.setGender(gender)
        }

    }

    fun backPress() {
        requireActivity().onBackPressed()
    }

    private fun closeFragment() {
        findNavController().navigateUp()
    }

    private fun observeStoreStatus() {
        viewModel.storeSucceeded.observe(viewLifecycleOwner) { succeeded ->
            val text: String
            val color: Int

            if (succeeded) {
                text = getString(R.string.save_successful)
                color = Color.parseColor(getString(R.color.teal_200))
                setReturnValue(viewModel.getMergedUser())
            } else {
                text = getString(R.string.save_failed)
                color = Color.parseColor(getString(R.color.email_red))
                setReturnValue(null)
            }

            Snackbar.make(binding.saveEditsBtn, text, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .setBackgroundTint(color)
                .setAction("Ok") {
                    closeFragment()
                }
                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onShown(transientBottomBar: Snackbar?) {
                        super.onShown(transientBottomBar)
                    }

                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)

                        findNavController().navigateUp()
                    }
                })
                .show()
        }
    }

    private fun setReturnValue(mergedUser: User?) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            MERGE_NAV_KEY,
            mergedUser
        )
    }

    private fun isOnline(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        capabilities?.let {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }

        return false
    }

    private fun showWarningMessage() {
        activity?.let {
            val builder = androidx.appcompat.app.AlertDialog.Builder(it)
            builder.apply {
                setTitle(getString(R.string.unsaved_changes_warning_title))
                setMessage(getString(R.string.unsaved_merging_warning_msg))
                setPositiveButton(getString(R.string.dialog_yes_btn)) { _, _ ->
                    setReturnValue(null)
                    closeFragment()
                }
                setNegativeButton(getString(R.string.dialog_no_btn)) { _, _ ->

                }
            }
                .create()
        }?.show()
    }
}