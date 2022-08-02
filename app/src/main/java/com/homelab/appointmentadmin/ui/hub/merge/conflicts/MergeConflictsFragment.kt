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
    }
}