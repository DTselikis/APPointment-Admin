package com.homelab.appointmentadmin.ui.customer.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.homelab.appointmentadmin.data.USERS_NOTES_COLLECTI0N
import com.homelab.appointmentadmin.data.User
import com.homelab.appointmentadmin.model.network.Note

class NotesViewModel(private val user: User) : ViewModel() {
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    private val _updatesStored = MutableLiveData<Boolean>()
    val updatesStored: LiveData<Boolean> = _updatesStored

    private lateinit var selectedNote: Note

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

    fun setSelectedNote(note: Note) {
        selectedNote = note
        title.value = note.title
        description.value = note.description
    }

    fun isModified(): Boolean =
        title.value != selectedNote.title || description.value != selectedNote.description

    fun storeChangesToDB(): Timestamp {
        val changes = mutableMapOf<String, Any>()

        if (title.value != selectedNote.title) changes["title"] = title.value!!
        if (description.value != selectedNote.description) changes["description"] =
            description.value!!
        changes["timestamp"] = FieldValue.serverTimestamp()

        Firebase.firestore.collection(USERS_NOTES_COLLECTI0N).document(user.uid!!).update(changes)
            .addOnCompleteListener { task ->
                _updatesStored.value = task.isSuccessful
            }

        return changes["timestamp"] as Timestamp
    }
}