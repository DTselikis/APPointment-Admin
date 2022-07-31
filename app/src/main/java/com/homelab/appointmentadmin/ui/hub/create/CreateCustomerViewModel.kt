package com.homelab.appointmentadmin.ui.hub.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.homelab.appointmentadmin.data.User

class CreateCustomerViewModel : ViewModel() {
    val firstname = MutableLiveData<String>()
    val lastname = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val email = MutableLiveData<String>()

    fun createUser() {
    }

    private fun getUser(): User =
        User(
            uid = null,
            firstname.value,
            lastname.value,
            if (nickname.value.isNullOrBlank()) "${firstname.value} ${lastname.value}" else nickname.value,
            phone.value,
            email.value,
            registered = false
        ).also {
            it.uid = "${it.hashCode().toString()}_${Timestamp.now().seconds}"
        }
}