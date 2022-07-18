package com.homelab.appointmentadmin.ui.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.homelab.appointmentadmin.data.User

class CustomerProfileSharedViewModel : ViewModel() {
    lateinit var user: User

    private val _saveBtnPressed = MutableLiveData<Boolean>()
    val saveBtnPressed: LiveData<Boolean> = _saveBtnPressed

    fun unpressSaveBtn() {
        if (_saveBtnPressed.value!!) {
            _saveBtnPressed.value = false
        }
    }

    fun pressSaveBtn() {
        _saveBtnPressed.value = true
    }
}