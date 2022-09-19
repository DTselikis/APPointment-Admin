package com.homelab.appointmentadmin.ui.hub.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.homelab.appointmentadmin.data.Gender
import com.homelab.appointmentadmin.data.USERS_COLLECTION
import com.homelab.appointmentadmin.data.User

class CreateCustomerViewModel : ViewModel() {
    val firstname = MutableLiveData<String>()
    val lastname = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val email = MutableLiveData<String>()

    private val _userStored = MutableLiveData<Boolean>()
    val userStored: LiveData<Boolean> = _userStored

    private lateinit var user: User
    private lateinit var gender: Gender

    fun createUser() {
        val user = createUserObject()

        storeUserToDB(user)
    }

    private fun storeUserToDB(user: User) {
        Firebase.firestore.collection(USERS_COLLECTION).document(user.uid!!).set(user)
            .addOnCompleteListener { task ->
                this@CreateCustomerViewModel.user = user
                _userStored.value = task.isSuccessful
            }
    }

    private fun createUserObject(): User =
        User(
            uid = null,
            firstname.value,
            lastname.value,
            if (!nickname.value.isNullOrBlank()) nickname.value!! else defaultNickname(),
            phone.value,
            email.value,
            gender = gender.code,
            registered = false,
            activeNotifications = 0
        ).also {
            it.uid = "${it.hashCode()}_${Timestamp.now().seconds}"
        }

    fun getUser(): User = user

    fun isModified(): Boolean {
        val changes = mutableListOf<Boolean>()
        changes.add(firstname.value.isNullOrBlank())
        changes.add(lastname.value.isNullOrBlank())
        changes.add(nickname.value.isNullOrBlank())
        changes.add(phone.value.isNullOrBlank())
        changes.add(email.value.isNullOrBlank())

        return changes.contains(false)
    }

    fun setGender(gender: Gender) {
        this.gender = gender
    }

    private fun defaultNickname(): String {
        var nickname: String
        if (!firstname.value.isNullOrBlank()) {
            nickname = firstname.value!!
            if (!lastname.value.isNullOrBlank()) {
                nickname += " ${lastname.value}"
            }
        } else {
            nickname = lastname.value!!
        }

        return nickname
    }
}