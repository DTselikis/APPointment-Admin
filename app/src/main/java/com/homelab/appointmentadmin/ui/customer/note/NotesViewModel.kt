package com.homelab.appointmentadmin.ui.customer.note

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.firebase.Timestamp
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
import com.homelab.appointmentadmin.utils.GoogleDriveHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.File

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

    private val _noteDeleted = MutableSharedFlow<Boolean>()
    val noteDeleted: SharedFlow<Boolean> = _noteDeleted

    private val _needsAuthorization = MutableSharedFlow<Intent>()
    val needsAuthorization: SharedFlow<Intent> = _needsAuthorization

    val noteTitle = MutableLiveData<String>()
    val noteText = MutableLiveData<String>()

    var isInNewNoteMode = false
        private set

    var isInEditNoteMode = false
        private set

    private lateinit var currentNote: Note

    private lateinit var userNotesFolder: String
    private var noteFolder: String? = null
        get() {
            if (field == null) {
                field =
                    GoogleDriveHelper.createFolderIfNotExist(getCurrentNotesHash(), userNotesFolder)
            }

            return field
        }
    private lateinit var photo: File
    private lateinit var mimeType: String

    fun gDriveInitialize(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            GoogleDriveHelper.initialize(context)
            userNotesFolder = GoogleDriveHelper.createFolderStructureIfNotExists(user.uid!!)
        }
    }

    fun saveNewNote(): Boolean? {
        if (!hasNoteModified()) {
            isInNewNoteMode = false
            return false
        }

        val note = createNewNote()
        storeNewNoteToFirebase(note)

        return null
    }

    fun saveChanges(): Boolean? {
        if (!hasNoteModified()) {
            isInEditNoteMode = false
            return false
        }

        val updatedNote = updateExistingNote(currentNote)
        updateExistingNoteToFirebase(updatedNote)

        return null
    }

    fun deleteNote(existingNote: Note) {
        deleteExistingNoteFromFirebase(existingNote)
    }

    private fun storeNewNoteToFirebase(note: Note) {
        val data = mapOf(NOTES_FIELD_VALUE to mapOf(note.hash to note))

        Firebase.firestore.collection(USERS_NOTES_COLLECTION).document(user.uid!!)
            .set(data, SetOptions.merge())
            .addOnCompleteListener { task ->
                viewModelScope.launch { _newNoteStored.emit(task.isSuccessful) }
                if (task.isSuccessful) {
                    isInNewNoteMode = false
                    addNewNoteToList(note)
                }
            }
    }

    private fun updateExistingNoteToFirebase(updatedNote: Note) {
        val data = mapOf("$NOTES_FIELD_VALUE.${updatedNote.hash}" to updatedNote)

        Firebase.firestore.collection(USERS_NOTES_COLLECTION).document(user.uid!!)
            .update(data)
            .addOnCompleteListener { task ->
                viewModelScope.launch { _newNoteStored.emit(task.isSuccessful) }

                if (task.isSuccessful) {
                    isInEditNoteMode = false
                    updateNoteInList(updatedNote)
                }
            }
    }

    private fun deleteExistingNoteFromFirebase(existingNote: Note) {
        Firebase.firestore.collection(USERS_NOTES_COLLECTION).document(user.uid!!)
            .update(mapOf("$NOTES_FIELD_VALUE.${existingNote.hash}" to FieldValue.delete()))
            .addOnCompleteListener { task ->
                viewModelScope.launch { _noteDeleted.emit(task.isSuccessful) }

                if (task.isSuccessful) {
                    deleteExistingNoteFromList(existingNote)
                }
            }
    }

    private fun addNewNoteToList(note: Note) {
        _notes.add(0, note)

        _notesForDisplay.value = _notes.toList()
    }

    private fun updateNoteInList(updatedNote: Note) {
        _notes.replace(currentNote, updatedNote)

        _notesForDisplay.value = _notes.toList()
    }

    private fun deleteExistingNoteFromList(existingNote: Note) {
        _notes.remove(existingNote)
    }

    fun uploadPhoto(photo: File = this.photo, mimeType: String = this.mimeType) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                GoogleDriveHelper.uploadImage(photo, mimeType, noteFolder!!)
            } catch (e: UserRecoverableAuthIOException) {
                saveFileInfo(photo, mimeType)
                _needsAuthorization.emit(e.intent)
            }
        }
    }

    fun newNoteMode() {
        noteTitle.value = ""
        noteText.value = ""
        //TODO photos
        isInNewNoteMode = true
        currentNote = Note()
    }

    fun editNoteMode(note: Note) {
        noteTitle.value = note.title
        noteText.value = note.description
        //TODO photos
        isInEditNoteMode = true
        currentNote = note

    }

    private fun hasNoteModified(): Boolean = currentNote.title != noteTitle.value
            || currentNote.description != noteText.value
    // TODO add photo check

    fun isNoteVisible(): Boolean = isInNewNoteMode || isInEditNoteMode

    fun getCurrentNotesHash(): String =
        if (isInNewNoteMode) currentNote.timestamp!!.seconds.toString() else currentNote.hash!!

    private fun saveFileInfo(photo: File, mimeType: String) {
        this.photo = photo
        this.mimeType = mimeType
    }

    private fun createNewNote(): Note {
        val timestamp = currentNote.timestamp

        return currentNote.copy(
            title = noteTitle.value!!,
            description = noteText.value!!,
            hash = timestamp!!.seconds.toString()
            //TODO add photos
        )
    }

    private fun updateExistingNote(existingNote: Note): Note =
        existingNote.copy(
            title = noteTitle.value!!,
            description = noteText.value!!,
            timestamp = Timestamp.now(),
            // TODO photos
        )

    private fun MutableList<Note>.replace(existingNote: Note, updatedNote: Note) {
        remove(existingNote)
        add(0, updatedNote)
    }
}