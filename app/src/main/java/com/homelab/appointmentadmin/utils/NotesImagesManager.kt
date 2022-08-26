package com.homelab.appointmentadmin.utils

import android.content.Context
import java.io.File

object NotesImagesManager {
    private const val NOTES_DIR = "Notes"

    private lateinit var notesPath: String

    fun initialize(context: Context) {
        File(context.filesDir, NOTES_DIR).apply {
            notesPath = path
            mkdir()
        }
    }
}