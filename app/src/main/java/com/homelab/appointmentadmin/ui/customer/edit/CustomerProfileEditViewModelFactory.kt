package com.homelab.appointmentadmin.ui.customer.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homelab.appointmentadmin.data.User

class CustomerProfileEditViewModelFactory(
    private val user: User
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CustomerProfileEditViewModel(user) as T
    }
}