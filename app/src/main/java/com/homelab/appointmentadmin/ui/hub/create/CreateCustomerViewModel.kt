package com.homelab.appointmentadmin.ui.hub.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateCustomerViewModel : ViewModel() {
    val firstname = MutableLiveData<String>()
    val lastname = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val email = MutableLiveData<String>()
}