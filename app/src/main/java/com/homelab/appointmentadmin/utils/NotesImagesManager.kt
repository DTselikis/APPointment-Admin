package com.homelab.appointmentadmin.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.fragment.app.FragmentActivity
import java.io.File
import java.io.FileOutputStream

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
    ): String? {
        fragmentActivity.contentResolver.openInputStream(file)?.let { inputStream ->
            val fileName = context.getFileName(file)
            val noteDir = File(notesPath, noteHash).apply { mkdir() }
            val newImage = File(noteDir, fileName!!)

            FileOutputStream(newImage).apply { inputStream.copyTo(this) }.close()
            inputStream.close()

            return newImage.absolutePath
        }

        return null
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
}