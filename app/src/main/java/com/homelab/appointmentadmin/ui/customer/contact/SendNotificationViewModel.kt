package com.homelab.appointmentadmin.ui.customer.contact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SendNotificationViewModel : ViewModel() {
    val notificationTitle = MutableLiveData<String>()
    val notificationMessage = MutableLiveData<String>()
}