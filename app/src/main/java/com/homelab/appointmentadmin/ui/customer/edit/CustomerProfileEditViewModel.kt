package com.homelab.appointmentadmin.ui.customer.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.homelab.appointmentadmin.data.USERS_COLLECTION
import com.homelab.appointmentadmin.data.User

class CustomerProfileEditViewModel(private val user: User) : ViewModel() {
    val firstname = MutableLiveData<String>(user.firstname)
    val lastname = MutableLiveData<String>(user.lastname)
    val nickname = MutableLiveData<String>(user.nickname)
    val phone = MutableLiveData<String>(user.phone)
    val email = MutableLiveData<String>(user.email)
    val fbName = MutableLiveData<String>(user.fbName)

    private val _changesSaved = MutableLiveData<Boolean>(false)
    val changesSaved: LiveData<Boolean> = _changesSaved

    fun getUser(): User = User(
        user.uid,
        firstname.value,
        lastname.value,
        nickname.value,
        phone.value,
        email.value,
        fbName.value,
        user.token
    )

    fun storeChangesToDB() {
        val changes = getChanges()

        Firebase.firestore.collection(USERS_COLLECTION).document(user.uid!!).update(changes)
            .addOnCompleteListener { task ->
                _changesSaved.value = task.isSuccessful
            }
    }

    private fun getChanges(): Map<String, String> {
        val changes = mutableMapOf<String, String>()

        if (user.firstname != firstname.value) changes["firstname"] = firstname.value!!
        if (user.lastname != lastname.value) changes["lastName"] = lastname.value!!
        if (user.nickname != nickname.value) changes["nickname"] = nickname.value!!
        if (user.phone != phone.value) changes["phone"] = phone.value!!
        if (user.email != email.value) changes["email"] = email.value!!
        if (user.fbName != fbName.value) changes["fbName"] = fbName.value!!

        return changes
    }


    fun isModified(): Boolean {
        val changes = getChanges()

        return !changes.isEmpty()
    }

}