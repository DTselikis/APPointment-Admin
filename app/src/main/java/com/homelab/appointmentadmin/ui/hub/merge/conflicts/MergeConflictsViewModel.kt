package com.homelab.appointmentadmin.ui.hub.merge.conflicts

import androidx.lifecycle.ViewModel
import com.homelab.appointmentadmin.data.User

class MergeConflictsViewModel : ViewModel() {
    private lateinit var userToBeMerged: User
    private lateinit var userToBeMergedWith: User

    fun setMergingUsers(userToBeMerged: User,  userToBeMergedWith: User) {
        this.userToBeMerged = userToBeMerged
        this.userToBeMergedWith = userToBeMergedWith
    }
}