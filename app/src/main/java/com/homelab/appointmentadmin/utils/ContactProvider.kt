package com.homelab.appointmentadmin.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.homelab.appointmentadmin.R

object ContactProvider {
    fun callCustomer(context: Context, phone: String) {
        val phoneUri = Uri.parse("tel:$phone")
        val phoneIntent = Intent(Intent.ACTION_DIAL, phoneUri)
        phoneIntent.resolveActivity(context.packageManager)?.let {
            context.startActivity(phoneIntent)
        }
    }

    fun sendEmail(context: Context, email: String) {
        val emailUri = Uri.parse("mailto:$email")
        val emailIntent = Intent(Intent.ACTION_SENDTO, emailUri)
            .putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_signature))
        emailIntent.resolveActivity(context.packageManager)?.let {
            context.startActivity(emailIntent)
        }
    }

    fun openFacebookChat(context: Context, fbProfileId: String) {
        val fbProfileIdUri = Uri.parse("fb-messenger://user/$fbProfileId")
        val fbIntent = Intent(Intent.ACTION_VIEW, fbProfileIdUri)

        fbIntent.resolveActivity(context.packageManager)?.let {
            context.startActivity(fbIntent)
        }
    }
}