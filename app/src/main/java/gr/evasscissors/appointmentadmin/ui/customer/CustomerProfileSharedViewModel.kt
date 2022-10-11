package gr.evasscissors.appointmentadmin.ui.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gr.evasscissors.appointmentadmin.data.User

class CustomerProfileSharedViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _saveBtnPressed = MutableLiveData<Boolean>()
    val saveBtnPressed: LiveData<Boolean> = _saveBtnPressed

    private val _backBtnPress = MutableLiveData<Boolean>()
    val backBtnPressed: LiveData<Boolean> = _backBtnPress

    fun setUser(user: User) {
        _user.value = user
    }

    fun unpressSaveBtn() {
        if (_saveBtnPressed.value!!) {
            _saveBtnPressed.value = false
        }
    }

    fun pressSaveBtn() {
        _saveBtnPressed.value = true
    }

    fun unpressBackBtn() {
        if (_backBtnPress.value!!) {
            _backBtnPress.value = false
        }
    }

    fun pressBackBtn() {
        _backBtnPress.value = true
    }
}