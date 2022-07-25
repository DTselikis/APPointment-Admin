package com.homelab.appointmentadmin.ui.customer.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.homelab.appointmentadmin.data.USERS_NOTES_COLLECTI0N
import com.homelab.appointmentadmin.data.User
import com.homelab.appointmentadmin.model.network.Note

class NotesViewModel(private val user: User) : ViewModel() {
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    fun fetchNotes() {
        Firebase.firestore.collection(USERS_NOTES_COLLECTI0N).document(user.uid!!).get()
            .addOnSuccessListener {
                val result = it["notes"] as List<Map<String, String>>

                _notes.value = result.map { mapEntry ->
                    Note(
                        mapEntry["description"],
                        mapEntry["photos"] as List<String>?,
                        mapEntry["timestamp"] as Timestamp,
                        mapEntry["title"]
                    )
                }
            }
    }
}