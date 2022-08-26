package com.homelab.appointmentadmin.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.homelab.appointmentadmin.R

private const val MIME_TYPE_GDRIVE_FOLDER = "application/vnd.google-apps.folder"
private const val NOTES_SUBFOLDER = "Notes"

object GoogleDriveHelper {
    lateinit var gDrive: Drive
        private set

    fun initialize(context: Context) {
        if (this::gDrive.isInitialized) return

        GoogleSignIn.getLastSignedInAccount(context)?.let { googleAccount ->
            val credential =
                GoogleAccountCredential.usingOAuth2(
                    context,
                    listOf(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE)
                )
            credential.selectedAccount = googleAccount.account

            gDrive = Drive.Builder(
                NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
            )
                .setApplicationName(context.getString(R.string.app_name))
                .build()
        }
    }

    fun uploadImage(image: java.io.File, mime: String?, parent: String): String {
        val gFile = File().apply {
            name = image.name
            parents = listOf(parent)
        }
        val fileContent = FileContent(mime ?: "image/*", image)

        return gDrive.Files().create(gFile, fileContent).execute().id
    }

    fun createFolderStructureIfNotExists(uid: String): String {
        val userFolder = createFolderInNotExist(uid)

        return createFolderInNotExist(NOTES_SUBFOLDER, userFolder)
    }

    fun createFolderInNotExist(name: String, parent: String? = null): String =
        gDrive.folder(name)?.id ?: createFolder(name, parent)


    private fun createFolder(name: String, parent: String? = null): String =
        gDrive.files().create(
            File().apply {
                this.name = name
                mimeType = MIME_TYPE_GDRIVE_FOLDER
                parent?.let {
                    this.parents = listOf(it)
                }
            }
        )
            .setFields("id")
            .execute()
            .id

    private fun Drive.folder(name: String): File? =
        files().list().apply {
            q = "mimeType='$MIME_TYPE_GDRIVE_FOLDER' and name='$name'"
            spaces = "drive"
        }.execute().files.getOrNull(0)
}