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

object GoogleDriveHelper {
    lateinit var gDrive: Drive
        private set

    fun initialize(context: Context) {
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

    fun uploadImage(image: java.io.File, mime: String?) {
        val gFile = File().apply { name = image.name }
        val fileContent = FileContent(mime ?: "image/*", image)

        gDrive.Files().create(gFile, fileContent).execute()
    }

    fun createFolderInNotExist(uid: String): String =
        gDrive.folder(uid)?.id ?: createFolder(uid)


    private fun createFolder(name: String): String =
        gDrive.files().create(
            File().apply {
                this.name = name
                mimeType = MIME_TYPE_GDRIVE_FOLDER
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