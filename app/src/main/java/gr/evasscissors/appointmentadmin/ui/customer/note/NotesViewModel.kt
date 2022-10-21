package gr.evasscissors.appointmentadmin.ui.customer.note

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.load
import coil.request.CachePolicy
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import gr.evasscissors.appointmentadmin.data.GDriveOperation
import gr.evasscissors.appointmentadmin.data.NOTES_FIELD_VALUE
import gr.evasscissors.appointmentadmin.data.USERS_NOTES_COLLECTION
import gr.evasscissors.appointmentadmin.data.User
import gr.evasscissors.appointmentadmin.model.network.Note
import gr.evasscissors.appointmentadmin.model.network.NotePhoto
import gr.evasscissors.appointmentadmin.model.network.helping.Notes
import gr.evasscissors.appointmentadmin.utils.GoogleDriveHelper
import gr.evasscissors.appointmentadmin.utils.NotesImagesManager
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

    private val _needsAuthorization = MutableSharedFlow<Pair<GDriveOperation, Intent>>()
    val needsAuthorization: SharedFlow<Pair<GDriveOperation, Intent>> = _needsAuthorization

    private val _gDriveApiDisabled = MutableSharedFlow<String>()
    val gDriveApiDisabled: SharedFlow<String> = _gDriveApiDisabled

    private val _photoUploaded = MutableSharedFlow<Boolean>()
    val photoUploaded: SharedFlow<Boolean> = _photoUploaded

    val noteTitle = MutableLiveData<String>()
    val noteText = MutableLiveData<String>()

    var isInNewNoteMode = false
        private set

    var isInEditNoteMode = false
        private set

    private lateinit var currentNote: Note

    private val _photosForDisplay = MutableLiveData<List<NotePhoto>>()
    val photosForDisplay: LiveData<List<NotePhoto>> = _photosForDisplay

    private val _photos = mutableListOf<NotePhoto>()

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
            try {
                GoogleDriveHelper.initialize(context)
                userNotesFolder = GoogleDriveHelper.createFolderStructureIfNotExists(user.uid!!)
            } catch (e: UserRecoverableAuthIOException) {
                _needsAuthorization.emit(Pair(GDriveOperation.INITIALIZE, e.intent))
            } catch (e: GoogleJsonResponseException) {
                if (e.statusCode == 403) {
                    _gDriveApiDisabled.emit(e.details.message)
                }
            }
        }
    }

    fun saveNewNote(): Boolean? {
        if (!hasNoteModified()) {
            isInNewNoteMode = false
            clearPhotosLists()
            return false
        }

        val note = createNewNote()
        storeNewNoteToFirebase(note)

        clearPhotosLists()

        return null
    }

    fun saveChanges(): Boolean? {
        if (!hasNoteModified()) {
            clearPhotosLists()
            isInEditNoteMode = false
            return false
        }

        val updatedNote = updateExistingNote(currentNote)
        updateExistingNoteToFirebase(updatedNote)

        clearPhotosLists()

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

    private fun addPhotoToNote(notePhoto: NotePhoto) {
        _photos.add(0, notePhoto)

        _photosForDisplay.postValue(_photos.toList())
    }

    private fun addPhotosToNote(notePhotos: List<NotePhoto>) {
        _photos.addAll(notePhotos)

        _photosForDisplay.value = _photos.toList()
    }

    fun uploadPhoto(photo: File = this.photo, mimeType: String = this.mimeType) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val photoId = GoogleDriveHelper.uploadImage(photo, mimeType, noteFolder!!)
                addPhotoToNote(NotePhoto(photo.absolutePath, photoId))

                viewModelScope.launch { _photoUploaded.emit(true) }
            } catch (e: UserRecoverableAuthIOException) {
                saveFileInfo(photo, mimeType)
                _needsAuthorization.emit(Pair(GDriveOperation.PHOTO_UPLOAD, e.intent))
            }
        }
    }

    fun newNoteMode() {
        noteTitle.value = ""
        noteText.value = ""
        isInNewNoteMode = true
        currentNote = Note()
    }

    fun editNoteMode(note: Note) {
        noteTitle.value = note.title
        noteText.value = note.description
        isInEditNoteMode = true
        currentNote = note

        note.photos?.let {
            addPhotosToNote(it)
        }
    }

    private fun hasNoteModified(): Boolean = currentNote.title != noteTitle.value
            || currentNote.description != noteText.value
            || _photos.notContainsAll(currentNote.photos)

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
            hash = timestamp!!.seconds.toString(),
            photos = if (_photos.isNotEmpty()) _photos.toList() else null
        )
    }

    private fun updateExistingNote(existingNote: Note): Note =
        existingNote.copy(
            title = noteTitle.value!!,
            description = noteText.value!!,
            timestamp = Timestamp.now(),
            photos = _photos.toList()
        )

    private fun clearPhotosLists() {
        _photos.clear()
        _photosForDisplay.value = _photos.toList()
    }

    private fun MutableList<Note>.replace(existingNote: Note, updatedNote: Note) {
        remove(existingNote)
        add(0, updatedNote)
    }

    private fun NotePhoto.extractNoteHash(parent: String): String =
        localUri?.let {
            return@let it.substring(
                it.indexOf(parent).plus(parent.length),
                it.lastIndexOf('/')
            )
        }!!


    private fun MutableList<NotePhoto>.notContainsAll(notePhotoList: List<NotePhoto>?): Boolean =
        this.toSet() != (notePhotoList?.toSet() ?: setOf<NotePhoto>())

    fun bindNotePhoto(imageView: ImageView, notePhoto: NotePhoto) {
        viewModelScope.launch(Dispatchers.IO) {
            val noteHash = notePhoto.extractNoteHash("Notes/")

            val uri =
                if (NotesImagesManager.fileExists(
                        imageView.context,
                        noteHash,
                        notePhoto.localUri!!
                    )
                ) {
                    notePhoto.localUri
                } else {
                    val imageBytes = GoogleDriveHelper.downloadImage(notePhoto.driveId!!)
                    NotesImagesManager.copyFileToInternalAppStorage(
                        imageView.context,
                        imageBytes,
                        notePhoto.localUri,
                        noteHash
                    )
                }

            imageView.load(uri) {
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            }
        }
    }

}