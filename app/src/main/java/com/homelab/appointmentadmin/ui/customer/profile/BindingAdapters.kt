package com.homelab.appointmentadmin.ui.customer.profile

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView

@BindingAdapter("visibility")
fun bindVisibility(materialCardView: MaterialCardView, text: String?) {
    materialCardView.visibility = View.GONE
    text?.let {
        materialCardView.visibility = View.VISIBLE
    }
}