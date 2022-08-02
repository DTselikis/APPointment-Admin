package com.homelab.appointmentadmin.ui.hub.merge.conflicts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.homelab.appointmentadmin.data.*

class MergeConflictsViewModel : ViewModel() {
    val firstname = MutableLiveData<String>()
    val lastname = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val fbName = MutableLiveData<String>()
    val profilePic = MutableLiveData<User>()
    private lateinit var gender: GenderBtnId

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
        if (userToBeMerged.nickname != null && userToBeMergedWith.nickname != null &&
            userToBeMerged.nickname != userToBeMergedWith.nickname
        )
            conflicts.add(Conflict.NICKNAME)
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
            Conflict.NICKNAME -> {
                _unregisteredText.value = userToBeMerged.nickname!!
                _registeredText.value = userToBeMergedWith.nickname!!
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

    fun saveChoice(choice: ConflictChoice) {
        val user = if (choice == ConflictChoice.UNREGISTERD) userToBeMerged else userToBeMergedWith
        val (key, value) = when (conflicts[_currentConflict.value!!.minus(1)]) {
            Conflict.FIRSTNAME -> Pair(Conflict.FIRSTNAME, user.firstname)
            Conflict.LASTNAME -> Pair(Conflict.LASTNAME, user.lastname)
            Conflict.NICKNAME -> Pair(Conflict.NICKNAME, user.nickname)
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

    fun mergeUsers() {
        val user = User(
            uid = userToBeMergedWith.uid,
            firstname.value,
            lastname.value,
            nickname.value,
            phone.value,
            email.value,
            gender = getGender(),
            registered = true
        )

        storeMergedUserToDB(user)
    }

    private fun storeMergedUserToDB(mergedUser: User) {
        Firebase.firestore.collection(USERS_COLLECTION).document(mergedUser.uid!!)
            .set(mergedUser, SetOptions.merge())
    }

    fun getUserToBeMerged(): User = userToBeMerged

    fun setGender(gender: GenderBtnId) {
        this.gender = gender
    }

    private fun getGender(): String = when (gender) {
        GenderBtnId.FEMALE -> Gender.FEMALE.code
        GenderBtnId.MALE -> Gender.MALE.code
        else -> Gender.ANY.code
    }
}
