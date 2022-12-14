package gr.evasscissors.appointmentadmin.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.fragment.app.FragmentActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

object NotesImagesManager {
    private const val NOTES_DIR = "Notes"

    private lateinit var notesPath: String

    fun initialize(context: Context) {
        File(context.filesDir, NOTES_DIR).apply {
            notesPath = path
            mkdir()
        }
    }

    fun copyFileToInternalAppStorage(
        fragmentActivity: FragmentActivity,
        context: Context,
        file: Uri,
        noteHash: String
    ): File? {
        fragmentActivity.contentResolver.openInputStream(file)?.let { inputStream ->
            val fileName = context.getFileName(file)
            val noteDir = File(notesPath, noteHash).apply { mkdir() }
            val newImage = File(noteDir, fileName!!)

            FileOutputStream(newImage).apply { inputStream.copyTo(this) }.close()
            inputStream.close()

            return newImage
        }

        return null
    }

    fun copyFileToInternalAppStorage(
        context: Context,
        imageBytes: ByteArrayOutputStream,
        oldPath: String,
        noteHash: String,
    ): String {
        val fileName = context.getFileName(Uri.parse(oldPath))
        val noteDir = File(notesPath, noteHash)
        noteDir.createDirectoryIfNotExists()

        return File(noteDir, fileName!!).apply {
            writeBytes(imageBytes.toByteArray())
        }.absolutePath
    }

    fun copyAllToInternalAppStorage(noteHash: String, photos: List<Pair<String, String>>) {
        val noteDir = File(notesPath, noteHash)

        if (!noteDir.isDirectory) noteDir.mkdir()

        photos.forEach { photo ->
            val bytes = GoogleDriveHelper.downloadImage(photo.first)

            File(noteDir, photo.second).apply {
                writeBytes(bytes.toByteArray())
            }
        }
    }

    fun fileExists(context: Context, noteHash: String, path: String): Boolean {
        val fileName = context.getFileName(Uri.parse(path))
        val noteDir = File(notesPath, noteHash)

        return File(noteDir, fileName!!).exists()
    }

    fun getPhotosSorted(noteHash: String): List<String>? {
        val noteDir = File(notesPath, noteHash)

        return if (noteDir.isDirectory) {
            noteDir.listFiles()
                ?.sortedByDescending {
                    Files.readAttributes(it.toPath(), BasicFileAttributes::class.java)
                        .creationTime().toMillis()
                }
                ?.map { it.absolutePath }
        } else {
            null
        }
    }


    private fun Context.getFileName(uri: Uri): String? = when (uri.scheme) {
        ContentResolver.SCHEME_CONTENT -> getContentFileName(uri)
        else -> uri.path?.let(::File)?.name
    }

    private fun Context.getContentFileName(uri: Uri): String? = runCatching {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                .let(cursor::getString)
        }
    }.getOrNull()

    private fun File.createDirectoryIfNotExists() {
        if (!exists()) mkdir()
    }
}