package gr.evasscissors.appointmentadmin.ui.hub.merge.conflicts

import android.content.Context
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
import gr.evasscissors.appointmentadmin.R
import gr.evasscissors.appointmentadmin.data.ConflictChoice
import gr.evasscissors.appointmentadmin.data.Gender
import gr.evasscissors.appointmentadmin.data.MERGE_NAV_KEY
import gr.evasscissors.appointmentadmin.data.User
import gr.evasscissors.appointmentadmin.databinding.FragmentMergeConflictsBinding

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

        binding.mergingGenderGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.merging_genre_female ->
                        viewModel.setGender(Gender.FEMALE)
                    R.id.merging_genre_male ->
                        viewModel.setGender(Gender.MALE)
                    R.id.merging_genre_any ->
                        viewModel.setGender(Gender.ANY)
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
        binding.mergeProgress.show()

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
                Gender.FEMALE.code -> Pair(R.id.merging_genre_female, Gender.FEMALE)
                Gender.MALE.code -> Pair(R.id.merging_genre_male, Gender.MALE)
                else -> Pair(R.id.merging_genre_any, Gender.ANY)
            }

            mergingGenderGroup.check(id)
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
            binding.mergeProgress.hide()

            val text: String
            val color: Int

            if (succeeded) {
                text = getString(R.string.save_successful)
                color = R.color.md_theme_dark_secondary
                setReturnValue(viewModel.getMergedUser())
            } else {
                text = getString(R.string.save_failed)
                color = R.color.md_theme_dark_errorContainer
                setReturnValue(null)
            }

            Snackbar.make(binding.saveEditsBtn, text, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .setBackgroundTint(resources.getColor(color, requireActivity().theme))
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