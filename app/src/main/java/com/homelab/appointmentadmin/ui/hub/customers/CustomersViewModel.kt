package com.homelab.appointmentadmin.ui.hub.customers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.homelab.appointmentadmin.data.CustomerFilter
import com.homelab.appointmentadmin.data.USERS_COLLECTION
import com.homelab.appointmentadmin.data.User

class CustomersViewModel : ViewModel() {
    private lateinit var _users: MutableList<User>
    private lateinit var _registeredUsers: List<User>
    private lateinit var _unregisteredUsers: List<User>

    private val _usersForDisplay = MutableLiveData<List<User>>()
    val usersForDisplay: LiveData<List<User>> = _usersForDisplay

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
                }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.nickname!! }).toMutableList()

                val (registered, unregistered) = _users.partition { it.registered }
                _registeredUsers = registered
                _unregisteredUsers = unregistered

                _usersForDisplay.value = _users
            }
    }

    fun updateUser(updatedUser: User): Int {
        val existingUser = _users.find { it.uid == updatedUser.uid }
        val index = _users.indexOf(existingUser)
        _users[index] = updatedUser

        return index
    }

    fun insertUser(newUser: User): Int {
        _users.add(newUser)

        updateLists()

        _usersForDisplay.value = when (activeFilter) {
            CustomerFilter.ALL -> _users
            CustomerFilter.REGISTERED -> _registeredUsers
            CustomerFilter.UNREGISTERED -> _unregisteredUsers
        }

        return 0
    }

    fun deleteUser(userToBeDeleted: User): Int {
        val activeList = when (activeFilter) {
            CustomerFilter.ALL -> _users
            CustomerFilter.REGISTERED -> _registeredUsers
            CustomerFilter.UNREGISTERED -> _unregisteredUsers
        }

        val index = activeList.indexOf(userToBeDeleted)
        _users.remove(userToBeDeleted)

        updateLists()

        _usersForDisplay.value = when (activeFilter) {
            CustomerFilter.ALL -> _users
            CustomerFilter.REGISTERED -> _registeredUsers
            CustomerFilter.UNREGISTERED -> _unregisteredUsers
        }

        return index
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

    fun reflectMergeChanges(mergedUser: User): Pair<Int, Int> {
        val userToBeMergedIndex = _users.indexOf(userToBeMerged)
        val userToBeMergedWithIndex = _users.indexOf(userToBeMergedWith)

        _users.remove(userToBeMerged)
        _users.replace(userToBeMergedWith, mergedUser)

        updateLists()

        return Pair(userToBeMergedIndex, userToBeMergedWithIndex)
    }

    private fun updateLists() {
        _users.sortWith((compareBy(String.CASE_INSENSITIVE_ORDER) { it.nickname!! }))
        val (registered, unregistered) = _users.partition { it.registered }

        _registeredUsers = registered
        _unregisteredUsers = unregistered
    }

    fun setUserToBeMerged(user: User) {
        this.userToBeMerged = user
    }

    fun setUserToBeMergedWith(user: User) {
        this.userToBeMergedWith = user
    }

    fun getUserToBeMerged(): User = userToBeMerged

    fun getRegisteredUsers(): List<User> = _registeredUsers

    private fun MutableList<User>.replace(existingUser: User, updated: User) {
        val index = this.indexOf(existingUser)
        this[index] = updated
    }

}