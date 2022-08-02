package com.homelab.appointmentadmin.ui.hub.merge.conflicts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.data.Gender
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

        viewModel.setMergingUsers(args.userToBeMerged, args.usetToBeMergedWith)

        observeCurrentConflict()
    }

    fun saveChoice() {
        val choice = if (binding.unregisteredBtn.isChecked) 0 else 1

        viewModel.saveChoice(choice)
        viewModel.nextConflict()

        binding.conflictsRadioGroup.clearCheck()
    }

    private fun observeCurrentConflict() {
        viewModel.currentConflict.observe(viewLifecycleOwner) { current ->
            if (current > viewModel.numOfConflicts.value!!) {
                viewModel.populateFields()
                checkGenderButton()

                binding.apply {
                    conflictsGroup.visibility = View.GONE
                    mergeResult.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun checkGenderButton() {
        binding.apply {
            val id = when (viewModel!!.getUserToBeMerged().gender) {
                Gender.FEMALE.code -> R.id.female_option
                Gender.MALE.code -> R.id.male_option
                else -> R.id.any_option
            }

            genderGroup.check(id)
        }

    }
}