package com.homelab.appointmentadmin.ui.hub.merge.conflicts

import androidx.lifecycle.ViewModel
import com.homelab.appointmentadmin.data.Conflict
import com.homelab.appointmentadmin.data.User

class MergeConflictsViewModel : ViewModel() {
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
    }
}