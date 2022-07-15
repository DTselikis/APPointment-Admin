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
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private var page = 0
    private val itemsPerPage = 15L

    fun fetchUsersFromDB() {
        Firebase.firestore.collection(USERS_COLLECTION)
            .startAt(page * itemsPerPage)
            .limit(itemsPerPage)
            .get()
            .addOnSuccessListener { result ->
                _users.value = result.map { document ->
                    document.toObject<User>()
                }
                page++
            }
    }
}