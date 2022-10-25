package gr.evasscissors.appointmentadmin.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import gr.evasscissors.appointmentadmin.R
import gr.evasscissors.appointmentadmin.data.FB_MESSENGER_LITE_PACKAGE_NAME
import gr.evasscissors.appointmentadmin.data.FB_MESSENGER_PACKAGE_NAME

object ContactProvider {
    fun callCustomer(context: Context, phone: String) {
        val phoneUri = Uri.parse("tel:$phone")
        val phoneIntent = Intent(Intent.ACTION_DIAL, phoneUri)
        phoneIntent.resolveActivity(context.packageManager)?.let {
            context.startActivity(phoneIntent)
        }
    }


    fun sendSms(context: Context, phone: String) {
        val phoneUri = Uri.parse("smsto:$phone")
        val smsIntent = Intent(Intent.ACTION_VIEW, phoneUri)
        smsIntent.resolveActivity(context.packageManager)?.let {
            context.startActivity(smsIntent)
        }
    }

    fun sendEmail(context: Context, email: String) {
        val emailUri = Uri.parse("mailto:")
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = emailUri
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_signature))
        }
        emailIntent.resolveActivity(context.packageManager)?.let {
            context.startActivity(emailIntent)
        }
    }

    fun chatOnFacebook(context: Context, fbProfileId: String) {
        val packageManager = context.packageManager

        val installedApps = packageManager.getInstalledPackagesCustom(0)

        val messengerUri = when (installedApps.find {
            it.packageName == FB_MESSENGER_PACKAGE_NAME ||
                    it.packageName == FB_MESSENGER_LITE_PACKAGE_NAME
        }?.packageName) {
            FB_MESSENGER_PACKAGE_NAME -> "fb-messenger://user/"
            FB_MESSENGER_LITE_PACKAGE_NAME -> "fb-messenger-lite://user/"
            else -> "https://www.messenger.com/t/"
        }

        Intent(Intent.ACTION_VIEW, Uri.parse(messengerUri + fbProfileId)).apply {
            resolveActivity(packageManager)?.let {
                context.startActivity(this)
            }
        }
    }

    private fun PackageManager.getInstalledPackagesCustom(flags: Int): List<PackageInfo> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getInstalledPackages(PackageManager.PackageInfoFlags.of(flags.toLong()))
        } else {
            getInstalledPackages(0)
        }
}