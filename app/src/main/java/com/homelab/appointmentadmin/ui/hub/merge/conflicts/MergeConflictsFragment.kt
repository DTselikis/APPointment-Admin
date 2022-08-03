package com.homelab.appointmentadmin.ui.hub.merge.conflicts

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.data.ConflictChoice
import com.homelab.appointmentadmin.data.Gender
import com.homelab.appointmentadmin.data.GenderBtnId
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
}