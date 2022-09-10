package com.homelab.appointmentadmin.ui.customer.note

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.homelab.appointmentadmin.data.NOTES_FIELD_VALUE
import com.homelab.appointmentadmin.data.USERS_NOTES_COLLECTION
import com.homelab.appointmentadmin.data.User
import com.homelab.appointmentadmin.model.network.Note
import com.homelab.appointmentadmin.model.network.helping.Notes
import com.homelab.appointmentadmin.utils.GoogleDriveHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class NotesViewModel(private val user: User) : ViewModel() {
    private val _notesForDisplay = MutableLiveData<List<Note>>()
    val notesForDisplay: LiveData<List<Note>> = _notesForDisplay

    private val _notes = mutableListOf<Note>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Firebase.firestore.collection(USERS_NOTES_COLLECTION).document(user.uid!!).get()
                .addOnSuccessListener { result ->
                    if (result.exists()) {
                        _notes.addAll(result.toObject<Notes>()!!.notes!!.map { entry ->
                            entry.value
                        }.sortedByDescending { it.timestamp })
                        _notesForDisplay.value = _notes.toList()
                    }
                }
        }
    }

    private val _newNoteStored = MutableSharedFlow<Boolean>()
    val newNoteStored: SharedFlow<Boolean> = _newNoteStored

    val noteTitle = MutableLiveData<String>()
    val noteText = MutableLiveData<String>()

    var isInNewNoteMode = false
        private set

    private lateinit var currentNote: Note

    fun gDriveInitialize(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            GoogleDriveHelper.initialize(context)
            GoogleDriveHelper.createFolderStructureIfNotExists(user.uid!!)
        }
    }

    fun saveNewNote() {
        if (!hasNoteModified()) return

        val note = createNewNote()
        addNewNoteToList(note)
        storeNewNoteToFirebase(note)
    }

    private fun storeNewNoteToFirebase(note: Note) {
        val data = mapOf(NOTES_FIELD_VALUE to mapOf(note.hash to note))

        Firebase.firestore.collection(USERS_NOTES_COLLECTION).document(user.uid!!)
            .set(data, SetOptions.merge())
            .addOnCompleteListener { task ->
                viewModelScope.launch { _newNoteStored.emit(task.isSuccessful) }
            }

    }

    private fun addNewNoteToList(note: Note) {
        _notes.add(0, note)
    }

    fun newNoteMode() {
        noteTitle.value = ""
        noteText.value = ""
        isInNewNoteMode = true
        currentNote = Note()
    }

    private fun hasNoteModified(): Boolean = currentNote.title != noteTitle.value
            || currentNote.description != noteText.value
    // TODO add photo check

    private fun createNewNote(): Note {
        val timestamp = Timestamp.now()

        return currentNote.copy(
            title = noteTitle.value!!,
            description = noteText.value!!,
            timestamp = timestamp,
            hash = timestamp.seconds.toString()
            //TODO add photos
        )
    }
}