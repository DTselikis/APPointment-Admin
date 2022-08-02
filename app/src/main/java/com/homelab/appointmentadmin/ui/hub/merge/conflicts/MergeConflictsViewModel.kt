package com.homelab.appointmentadmin.ui.hub.merge.conflicts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.homelab.appointmentadmin.data.Conflict
import com.homelab.appointmentadmin.data.User

class MergeConflictsViewModel : ViewModel() {
    val firstname = MutableLiveData<String>()
    val lastname = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val fbName = MutableLiveData<String>()
    val profilePic = MutableLiveData<User>()

    private val _numOfConflicts = MutableLiveData<Int>()
    val numOfConflicts: LiveData<Int> = _numOfConflicts

    private val _currentConflict = MutableLiveData<Int>()
    val currentConflict: LiveData<Int> = _currentConflict

    private val _registeredText = MutableLiveData<String>()
    val registeredText: LiveData<String> = _registeredText

    private val _unregisteredText = MutableLiveData<String>()
    val unregisteredText: LiveData<String> = _unregisteredText

    private lateinit var userToBeMerged: User
    private lateinit var userToBeMergedWith: User

    private lateinit var conflicts: List<Conflict>
    private val conflictChoices = mapOf<Conflict, String>()

    fun setMergingUsers(userToBeMerged: User, userToBeMergedWith: User) {
        this.userToBeMerged = userToBeMerged
        this.userToBeMergedWith = userToBeMergedWith

        findConflicts()
    }

    private fun findConflicts() {
        val conflicts = mutableListOf<Conflict>()

        if (userToBeMerged.firstname != null && userToBeMergedWith.firstname != null &&
            userToBeMerged.firstname != userToBeMergedWith.firstname
        )
            conflicts.add(Conflict.FIRSTNAME)
        if (userToBeMerged.lastname != null && userToBeMergedWith.lastname != null &&
            userToBeMerged.lastname != userToBeMergedWith.lastname
        )
            conflicts.add(Conflict.LASTNAME)
        if (userToBeMerged.email != null && userToBeMergedWith.email != null &&
            userToBeMerged.email != userToBeMergedWith.email
        )
            conflicts.add(Conflict.EMAIL)
        if (userToBeMerged.phone != null && userToBeMergedWith.phone != null &&
            userToBeMerged.phone != userToBeMergedWith.phone
        )
            conflicts.add(Conflict.PHONE)

        this.conflicts = conflicts
        _numOfConflicts.value = conflicts.size
        _currentConflict.value = 0

        nextConflict()
    }

    fun nextConflict() {
        when (conflicts[_currentConflict.value!!]) {
            Conflict.FIRSTNAME -> {
                _unregisteredText.value = userToBeMerged.firstname!!
                _registeredText.value = userToBeMergedWith.firstname!!
            }
            Conflict.LASTNAME -> {
                _unregisteredText.value = userToBeMerged.lastname!!
                _registeredText.value = userToBeMergedWith.lastname!!
            }
            Conflict.EMAIL -> {
                _unregisteredText.value = userToBeMerged.email!!
                _registeredText.value = userToBeMergedWith.email!!
            }
            Conflict.PHONE -> {
                _unregisteredText.value = userToBeMerged.phone!!
                _registeredText.value = userToBeMergedWith.phone!!
            }
        }

        _currentConflict.value = currentConflict.value?.plus(1)
    }

    fun saveChoice(choice: Int) {
        val user = if (choice == 0) userToBeMerged else userToBeMergedWith
        val (key, value) = when (conflicts[_currentConflict.value!!.minus(1)]) {
            Conflict.FIRSTNAME -> Pair(Conflict.FIRSTNAME, user.firstname)
            Conflict.LASTNAME -> Pair(Conflict.LASTNAME, user.lastname)
            Conflict.EMAIL -> Pair(Conflict.EMAIL, user.email)
            Conflict.PHONE -> Pair(Conflict.PHONE, user.phone)
        }

        conflictChoices[key] to value
    }

    fun populateFields() {
        firstname.value = conflictChoices.getOrElse(Conflict.FIRSTNAME) {
            userToBeMergedWith.firstname ?: userToBeMerged.firstname
        }
        lastname.value = conflictChoices.getOrElse(Conflict.LASTNAME) {
            userToBeMergedWith.lastname ?: userToBeMerged.lastname
        }
        phone.value = conflictChoices.getOrElse(Conflict.PHONE) {
            userToBeMergedWith.phone ?: userToBeMerged.phone
        }
        email.value = conflictChoices.getOrElse(Conflict.EMAIL) {
            userToBeMergedWith.email ?: userToBeMerged.email
        }

        nickname.value = userToBeMerged.nickname ?: ""
        fbName.value = userToBeMergedWith.fbName ?: ""
        profilePic.value = userToBeMergedWith
    }

    fun getUserToBeMerged(): User = userToBeMerged
}
