package com.homelab.appointmentadmin.ui.customer.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.homelab.appointmentadmin.data.User

class CustomerProfileEditViewModel(private val user: User) : ViewModel() {
    val firstname = MutableLiveData<String>(user.firstName)
    val lastname = MutableLiveData<String>(user.lastName)
    val nickname = MutableLiveData<String>(user.nickname)
    val phone = MutableLiveData<String>(user.phone)
    val email = MutableLiveData<String>(user.email)
    val fbName = MutableLiveData<String>(user.fbName)

    private var modified = false

    fun getUser(): User = User(
        user.uid,
        firstname.value,
        lastname.value,
        nickname.value,
        phone.value,
        email.value,
        fbName.value,
        user.token
    )

    fun acknowledgeModifications() {
        modified = true
    }

    fun isModified(): Boolean = modified

}