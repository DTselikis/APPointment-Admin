package com.homelab.appointmentadmin.ui.hub.merge.conflicts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.homelab.appointmentadmin.data.*
import com.homelab.appointmentadmin.model.network.helping.Notes

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

    private val _storeSucceeded = MutableLiveData<Boolean>()
    val storeSucceeded: LiveData<Boolean> = _storeSucceeded

    private lateinit var userToBeMerged: User
    private lateinit var userToBeMergedWith: User

    private lateinit var conflicts: List<Conflict>
    private val conflictChoices = mutableMapOf<Conflict, String>()

    fun setMergingUsers(userToBeMerged: User, userToBeMergedWith: User) {
        this.userToBeMerged = userToBeMerged
        this.userToBeMergedWith = userToBeMergedWith

        findConflicts()
    }

    private fun findConflicts() {
        val conflicts = mutableListOf<Conflict>()

        if (userToBeMerged.firstname?.notSameAs(userToBeMergedWith.firstname) == true)
            conflicts.add(Conflict.FIRSTNAME)
        if (userToBeMerged.lastname?.notSameAs(userToBeMergedWith.lastname) == true)
            conflicts.add(Conflict.LASTNAME)
        if (userToBeMerged.email?.notSameAs(userToBeMergedWith.email) == true)
            conflicts.add(Conflict.EMAIL)
        if (userToBeMerged.nickname?.notSameAs(userToBeMergedWith.nickname) == true)
            conflicts.add(Conflict.NICKNAME)
        if (userToBeMerged.phone?.notSameAs(userToBeMergedWith.phone) == true)
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

        conflictChoices[key] = value!!
    }

    fun populateFields() {
        firstname.value = conflictChoices.getOrElse(Conflict.FIRSTNAME) {
            userToBeMergedWith.firstname ?: userToBeMerged.firstname
        }
        lastname.value = conflictChoices.getOrElse(Conflict.LASTNAME) {
            userToBeMergedWith.lastname ?: userToBeMerged.lastname
        }
        nickname.value = conflictChoices.getOrElse(Conflict.NICKNAME) {
            userToBeMergedWith.nickname ?: userToBeMerged.nickname
        }
        phone.value = conflictChoices.getOrElse(Conflict.PHONE) {
            userToBeMergedWith.phone ?: userToBeMerged.phone
        }
        email.value = conflictChoices.getOrElse(Conflict.EMAIL) {
            userToBeMergedWith.email ?: userToBeMerged.email
        }

        fbName.value = userToBeMergedWith.fbName ?: ""
        profilePic.value = userToBeMergedWith
    }

    fun mergeUsers() {
        val user = getMergedUser()

        storeMergedUserToDB(user)
    }

    fun storeMergedUserToDbTransaction() {
        val db = Firebase.firestore

        db.runTransaction { transaction ->
            val notes = fetchNotesTransaction(transaction, db)

            mergeUsersInfo(transaction, db)

            notes?.let {
                mergeNotes(transaction, db, it)
            }

            deleteUserToBeMergedTransaction(transaction, db)
        }
            .addOnCompleteListener { task ->
                _storeSucceeded.value = task.isSuccessful
                println(task.exception)
            }
    }

    // Will be synced when back online
    private fun storeMergedUserToDB(mergedUser: User) {
        Firebase.firestore.collection(USERS_COLLECTION).document(mergedUser.uid!!)
            .set(mergedUser, SetOptions.merge())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchNotes()
                } else {
                    _storeSucceeded.value = false
                }
            }
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

    private fun String?.notSameAs(str: String?): Boolean =
        str != null && !this.equals(str, true)

    private fun getMergedUser(): User = User(
        uid = userToBeMergedWith.uid,
        firstname.value,
        lastname.value,
        nickname.value,
        phone.value,
        email.value,
        gender = getGender(),
        registered = true
    )

    private fun fetchNotesTransaction(
        transaction: Transaction,
        db: FirebaseFirestore
    ): Notes? {
        val userToBeMergedNotesRef =
            db.collection(USERS_NOTES_COLLECTI0N).document(userToBeMerged.uid!!)

        return transaction.get(userToBeMergedNotesRef)
            .toObject<Notes>()
    }

    private fun mergeUsersInfo(transaction: Transaction, db: FirebaseFirestore) {
        val userToBeMergedWithRef =
            db.collection(USERS_COLLECTION).document(userToBeMergedWith.uid!!)

        transaction.set(userToBeMergedWithRef, getMergedUser(), SetOptions.merge())
    }

    private fun mergeNotes(transaction: Transaction, db: FirebaseFirestore, notes: Notes) {
        val userToBeMergedWithNotesRef =
            db.collection(USERS_NOTES_COLLECTI0N).document(userToBeMergedWith.uid!!)

        transaction.set(userToBeMergedWithNotesRef, notes, SetOptions.merge())
    }

    private fun deleteUserToBeMergedTransaction(
        transaction: Transaction,
        db: FirebaseFirestore
    ) {
        val userToBeMergedRef =
            db.collection(USERS_COLLECTION).document(userToBeMerged.uid!!)
        val userToBeMergedNotesRef =
            db.collection(USERS_NOTES_COLLECTI0N).document(userToBeMerged.uid!!)

        transaction.delete(userToBeMergedRef)
        transaction.delete(userToBeMergedNotesRef)
    }

    private fun fetchNotes() {
        Firebase.firestore.collection(USERS_NOTES_COLLECTI0N).document(userToBeMerged.uid!!)
            .get()
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    val notes = result.toObject<Notes>()

                    mergeNotes(notes!!)
                } else {
                    _storeSucceeded.value = true
                }
            }
            .addOnFailureListener {
                _storeSucceeded.value = false
            }
    }

    private fun mergeNotes(notes: Notes) {
        Firebase.firestore.collection(USERS_NOTES_COLLECTI0N).document(userToBeMergedWith.uid!!)
            .set(notes, SetOptions.merge())
            .addOnSuccessListener {
                deleteUserToBeMerged()
            }
            .addOnFailureListener {
                _storeSucceeded.value = false
            }
    }

    private fun deleteUserToBeMerged() {
        Firebase.firestore.collection(USERS_COLLECTION).document(userToBeMerged.uid!!)
            .delete()
            .addOnCompleteListener { task ->
                _storeSucceeded.value = task.isSuccessful
            }
    }
}
