package com.homelab.appointmentadmin.ui.hub.customers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.homelab.appointmentadmin.data.CustomerFilter
import com.homelab.appointmentadmin.data.USERS_COLLECTION
import com.homelab.appointmentadmin.data.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class CustomersViewModel : ViewModel() {
    lateinit var _users: MutableList<User>
    private lateinit var _registeredUsers: List<User>
    private lateinit var _unregisteredUsers: List<User>

    private val _usersForDisplay = MutableLiveData<List<User>>()
    val usersForDisplay: LiveData<List<User>> = _usersForDisplay

    private val _userDeleted = MutableSharedFlow<Boolean>()
    val userDeleted: SharedFlow<Boolean> = _userDeleted.asSharedFlow()

    private var activeFilter = CustomerFilter.ALL

    var mergeMode = false
    private lateinit var userToBeMerged: User
    private lateinit var userToBeMergedWith: User

    fun fetchUsersFromDB() {
        Firebase.firestore.collection(USERS_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                _users = result.map { document ->
                    document.toObject<User>()
                }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.nickname!! })
                    .toMutableList()

                val (registered, unregistered) = _users.partition { it.registered }
                _registeredUsers = registered
                _unregisteredUsers = unregistered

                _usersForDisplay.value = _users
            }
    }

    fun updateUser(updatedUser: User): Int {
        val updatedIndex = _users.update(updatedUser)
        updateLists()

        return updatedIndex
    }

    fun insertUser(newUser: User): Int {
        val insertedIndex = _users.addSorted(newUser)
        updateLists()

        return insertedIndex
    }

    fun deleteUser(user: User) {
        Firebase.firestore.runTransaction { transaction ->
            val userRef = Firebase.firestore.collection(USERS_COLLECTION).document(user.uid!!)
            val userNotesRef = Firebase.firestore.collection(USERS_COLLECTION).document(user.uid!!)

            transaction.delete(userRef)
            transaction.delete(userNotesRef)
        }.addOnCompleteListener { task ->
            val deleted: Boolean =
                if (task.isSuccessful) {
                    _users.remove(user)
                    updateLists()
                    true
                } else {
                    false
                }
            viewModelScope.launch {
                _userDeleted.emit(deleted)
            }
        }
    }

    fun filterUsers(registered: Boolean) {
        when (registered) {
            true -> {
                activeFilter = CustomerFilter.REGISTERED
                _usersForDisplay.value = _registeredUsers
            }
            false -> {
                activeFilter = CustomerFilter.UNREGISTERED
                _usersForDisplay.value = _unregisteredUsers
            }
        }
    }

    fun resetFilter() {
        _usersForDisplay.value = _users

        activeFilter = CustomerFilter.ALL
    }

    fun filterUsersByName(name: CharSequence) {
        val list = when (activeFilter) {
            CustomerFilter.ALL -> _users
            CustomerFilter.REGISTERED -> _registeredUsers
            else -> _unregisteredUsers
        }

        val mergedList = mutableListOf<User>()

        name.split(" ").forEach { part ->
            val subList = list.filter {
                it.nickname?.contains(part, true) == true
                        || it.firstname?.contains(part, true) == true
                        || it.lastname?.contains(part, true) == true
            }

            subList.forEach { user ->
                if (user !in mergedList) mergedList.add(user)
            }
        }

        _usersForDisplay.value = mergedList
    }

    private fun updateLists() {
        val (registered, unregistered) = _users.partition { it.registered }

        _registeredUsers = registered
        _unregisteredUsers = unregistered

        _usersForDisplay.value = when (activeFilter) {
            CustomerFilter.ALL -> _users
            CustomerFilter.UNREGISTERED -> _unregisteredUsers
            CustomerFilter.REGISTERED -> _registeredUsers
        }
    }

    fun setUserToBeMerged(user: User) {
        this.userToBeMerged = user
    }

    fun setUserToBeMergedWith(user: User) {
        this.userToBeMergedWith = user
    }

    fun getUserToBeMerged(): User = userToBeMerged

    fun getRegisteredUsers(): List<User> = _registeredUsers

    private fun MutableList<User>.update(updatedUser: User): Int {
        val existingUser = this.find { it.uid == updatedUser.uid }
        val index = this.indexOf(existingUser)
        this[index] = updatedUser

        return index
    }

    private fun MutableList<User>.addSorted(newUser: User): Int {
        this.add(newUser)
        this.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.nickname!! })

        return this.indexOf(newUser)
    }

}