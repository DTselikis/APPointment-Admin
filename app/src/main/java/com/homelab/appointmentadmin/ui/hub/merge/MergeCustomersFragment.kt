package com.homelab.appointmentadmin.ui.hub.merge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentMergeCustomersBinding
import com.homelab.appointmentadmin.ui.hub.HubSharedViewModel

class MergeCustomersFragment : Fragment() {

    private val args: MergeCustomersFragmentArgs by navArgs<MergeCustomersFragmentArgs>()
    private val viewModel: MergeCustomersViewModel by viewModels()
    private val sharedViewModel: HubSharedViewModel by activityViewModels()

    private lateinit var binding: FragmentMergeCustomersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_merge_customers, null, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            unregisteredUsersRv.adapter = MergeUserAdapter(this@MergeCustomersFragment)
        }
    }


}