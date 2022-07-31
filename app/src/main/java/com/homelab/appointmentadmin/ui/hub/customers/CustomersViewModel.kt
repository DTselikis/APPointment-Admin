package com.homelab.appointmentadmin.ui.hub.customers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.homelab.appointmentadmin.data.USERS_COLLECTION
import com.homelab.appointmentadmin.data.User

class CustomersViewModel : ViewModel() {
    private val _users = MutableLiveData<MutableList<User>>()
    val users: LiveData<MutableList<User>> = _users

    fun fetchUsersFromDB() {
        Firebase.firestore.collection(USERS_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                _users.value = result.map { document ->
                    document.toObject<User>()
                }.toMutableList()
            }
    }

    fun updateUser(updatedUser: User): Int {
        val existingUser = _users.value!!.find { it.uid == updatedUser.uid }
        val index = _users.value!!.indexOf(existingUser)
        _users.value!![index] = updatedUser

        return index
    }

    fun insertUser(newUser: User) {
        _users.value!!.add(newUser)
    }
}