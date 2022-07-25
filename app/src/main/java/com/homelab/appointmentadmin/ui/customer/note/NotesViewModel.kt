package com.homelab.appointmentadmin.ui.customer.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.homelab.appointmentadmin.data.USERS_NOTES_COLLECTI0N
import com.homelab.appointmentadmin.data.User
import com.homelab.appointmentadmin.model.network.Note
import com.homelab.appointmentadmin.model.network.helping.Notes

class NotesViewModel(private val user: User) : ViewModel() {
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    private val _updatesStored = MutableLiveData<Boolean>()
    val updatesStored: LiveData<Boolean> = _updatesStored

    private lateinit var selectedNote: Note
    private var isNew = false

    fun fetchNotes() {
        Firebase.firestore.collection(USERS_NOTES_COLLECTI0N).document(user.uid!!).get()
            .addOnSuccessListener { result ->
                _notes.value = result.toObject<Notes>()!!.notes!!.map { entry ->
                    entry.value
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

    fun storeNewNoteToDB() {
        val newNote = Note(description = description.value, photos = null, title = title.value)
        val data = mapOf<String, Map<String, Note>>(
            "notes" to mapOf<String, Note>(
                newNote.hashCode().toString() to newNote
            )
        )
        Firebase.firestore.collection(USERS_NOTES_COLLECTI0N).document(user.uid!!)
            .set(data, SetOptions.merge())
    }

    fun setNewNoteState() {
        title.value = null
        description.value = null
        isNew = true
    }

    fun isNewNote(): Boolean = isNew
}