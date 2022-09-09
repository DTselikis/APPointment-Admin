package com.homelab.appointmentadmin.ui.customer.contact

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView
import com.homelab.appointmentadmin.model.ContactProviderInfo

@BindingAdapter("visibility")
fun bindVisibility(materialCardView: MaterialCardView, text: String?) {
    materialCardView.visibility = View.GONE
    text?.let {
        materialCardView.visibility = View.VISIBLE
    }
}

@BindingAdapter("background")
fun bindBackground(constraintLayout: ConstraintLayout, contactProviderInfo: ContactProviderInfo?) {
    contactProviderInfo?.let {
        if (it.background == null) {
            constraintLayout.setBackgroundResource(it.color)
        } else {
            constraintLayout.background = it.background
        }
    }
}