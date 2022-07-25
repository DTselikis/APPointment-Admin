package com.homelab.appointmentadmin.ui.customer.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homelab.appointmentadmin.data.User
import com.homelab.appointmentadmin.ui.customer.edit.CustomerProfileEditViewModel

class NotesViewModelFactory(
    private val user: User
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotesViewModel(user) as T
    }
}