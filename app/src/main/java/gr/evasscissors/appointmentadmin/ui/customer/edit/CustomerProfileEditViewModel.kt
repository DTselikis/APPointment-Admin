package gr.evasscissors.appointmentadmin.ui.customer.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import gr.evasscissors.appointmentadmin.data.USERS_COLLECTION
import gr.evasscissors.appointmentadmin.data.User

class CustomerProfileEditViewModel(private var user: User) : ViewModel() {
    val firstname = MutableLiveData<String>(user.firstname)
    val lastname = MutableLiveData<String>(user.lastname)
    val nickname = MutableLiveData<String>(user.nickname)
    val phone = MutableLiveData<String>(user.phone)
    val email = MutableLiveData<String>(user.email)

    private val _changesSaved = MutableLiveData<Boolean>()
    val changesSaved: LiveData<Boolean> = _changesSaved

    fun getUser(): User = user.copy(
        firstname = firstname.value,
        lastname = lastname.value,
        nickname = nickname.value,
        phone = phone.value,
        email = email.value
    )

    fun storeChangesToDB() {
        val changes = getChanges()

        Firebase.firestore.collection(USERS_COLLECTION).document(user.uid!!).update(changes)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    this.user = getUser()
                }
                _changesSaved.value = task.isSuccessful
            }
    }

    private fun getChanges(): Map<String, String> {
        val changes = mutableMapOf<String, String>()

        if (user.firstname != firstname.value) changes["firstname"] = firstname.value!!
        if (user.lastname != lastname.value) changes["lastname"] = lastname.value!!
        if (user.nickname != nickname.value) changes["nickname"] = nickname.value!!
        if (user.phone != phone.value) changes["phone"] = phone.value!!
        if (user.email != email.value) changes["email"] = email.value!!

        return changes
    }


    fun isModified(): Boolean {
        val changes = getChanges()

        return changes.isNotEmpty()
    }

    fun revertChanges() {
        firstname.value = user.firstname ?: ""
        lastname.value = user.lastname ?: ""
        nickname.value = user.nickname ?: ""
        phone.value = user.phone ?: ""
        email.value = user.email ?: ""
    }

}