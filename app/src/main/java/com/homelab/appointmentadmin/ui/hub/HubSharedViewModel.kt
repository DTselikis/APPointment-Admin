package com.homelab.appointmentadmin.ui.hub

import androidx.lifecycle.ViewModel
import com.homelab.appointmentadmin.data.User

class HubSharedViewModel : ViewModel() {
    lateinit var registeredUsers: List<User>
}