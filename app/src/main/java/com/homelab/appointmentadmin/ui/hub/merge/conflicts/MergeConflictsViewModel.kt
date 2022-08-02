package com.homelab.appointmentadmin.ui.hub.merge.conflicts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.homelab.appointmentadmin.data.Conflict
import com.homelab.appointmentadmin.data.User

class MergeConflictsViewModel : ViewModel() {
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

    fun setMergingUsers(userToBeMerged: User, userToBeMergedWith: User) {
        this.userToBeMerged = userToBeMerged
        this.userToBeMergedWith = userToBeMergedWith

        findConflicts()
    }

    private fun findConflicts() {
        val conflicts = mutableListOf<Conflict>()

        if (userToBeMerged.firstname != userToBeMergedWith.firstname) conflicts.add(Conflict.FIRSTNAME)
        if (userToBeMerged.firstname != userToBeMergedWith.lastname) conflicts.add(Conflict.LASTNAME)
        if (userToBeMerged.email != userToBeMergedWith.email) conflicts.add(Conflict.EMAIL)
        if (userToBeMerged.phone != userToBeMergedWith.phone) conflicts.add(Conflict.PHONE)

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
}