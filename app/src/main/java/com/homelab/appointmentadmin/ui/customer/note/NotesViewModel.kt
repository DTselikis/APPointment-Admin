package com.homelab.appointmentadmin.ui.customer.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.homelab.appointmentadmin.data.NOTES_FIELD_VALUE
import com.homelab.appointmentadmin.data.USERS_NOTES_COLLECTION
import com.homelab.appointmentadmin.data.User
import com.homelab.appointmentadmin.model.network.Note
import com.homelab.appointmentadmin.model.network.helping.Notes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class NotesViewModel(private val user: User) : ViewModel() {
    private val _notes = MutableLiveData<MutableList<Note>>(mutableListOf())
    val notes: LiveData<MutableList<Note>> = _notes

    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    private val _updatesStored = MutableLiveData<Boolean>()
    val updatesStored: LiveData<Boolean> = _updatesStored

    private val _noteDeleted = MutableSharedFlow<Pair<Int, Boolean>>()
    val noteDeleted: SharedFlow<Pair<Int, Boolean>> = _noteDeleted

    private lateinit var selectedNote: Note
    private var isNew = false

    private var isNoteVisible = false

    fun fetchNotes() {
        Firebase.firestore.collection(USERS_NOTES_COLLECTION).document(user.uid!!).get()
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    _notes.value!!.addAll(result.toObject<Notes>()!!.notes!!.map { entry ->
                        entry.value
                    }.sortedByDescending { it.timestamp })
                }
            }
    }

    fun setSelectedNote(note: Note) {
        selectedNote = note
        isNew = false
        title.value = note.title
        description.value = note.description
    }

    fun isModified(): Boolean =
        title.value != selectedNote.title || description.value != selectedNote.description

    fun storeChangesToDB(): Int {
        val note =
            Note(
                title = title.value,
                description = description.value,
                photos = selectedNote.photos,
                hash = selectedNote.hash
            )
        storeToDB(note, selectedNote.hash!!)

        return updateExistingNote(note)
    }

    fun storeNewNoteToDB(): Int {
        val newNote =
            Note(description = description.value, photos = null, title = title.value).also {
                it.hash = it.hashCode().toString()
            }
        storeToDB(newNote, newNote.hash!!)

        return insertNoteToList(newNote)
    }

    private fun storeToDB(note: Note, hash: String) {
        val data = mapOf<String, Map<String, Note>>(
            "notes" to mapOf<String, Note>(
                hash to note
            )
        )

        Firebase.firestore.collection(USERS_NOTES_COLLECTION).document(user.uid!!)
            .set(data, SetOptions.merge())
            .addOnCompleteListener { task ->
                _updatesStored.value = task.isSuccessful
            }
    }

    fun deleteNote(note: Note) {
        val index = _notes.value!!.indexOf(note)
        Firebase.firestore.collection(USERS_NOTES_COLLECTION).document(user.uid!!)
            .update(mapOf("$NOTES_FIELD_VALUE.${note.hash}" to FieldValue.delete()))
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    _notes.value!!.remove(note)

                viewModelScope.launch {
                    _noteDeleted.emit(Pair(index, task.isSuccessful))
                }
            }
    }

    private fun insertNoteToList(note: Note): Int {
        _notes.value!!.add(0, note)

        return 0
    }

    private fun updateExistingNote(note: Note): Int {
        val existingNote = _notes.value!!.find { it.hash == note.hash }
        val index = _notes.value!!.indexOf(existingNote)
        _notes.value!!.remove(existingNote)

        insertNoteToList(note)

        return index
    }

    fun setNewNoteState() {
        title.value = null
        description.value = null
        isNew = true
    }

    fun isNewNote(): Boolean = isNew

    fun setNoteVisible(visible: Boolean) {
        isNoteVisible = visible
    }

    fun isNoteVisible(): Boolean = isNoteVisible
}