package gr.evasscissors.appointmentadmin.model

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

data class ContactProviderInfo(
    @ColorRes
    val color: Int,
    @DrawableRes
    val icon: Int,
    val text: String,
    val background: Drawable? = null,
    val copyText: () -> Boolean,
    val operation: () -> Unit
)