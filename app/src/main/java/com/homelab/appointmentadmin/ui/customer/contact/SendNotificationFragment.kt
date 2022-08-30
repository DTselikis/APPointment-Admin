package com.homelab.appointmentadmin.ui.customer.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentSendNotificationBinding

class SendNotificationFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSendNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_send_notification, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
    }
}