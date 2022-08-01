package com.homelab.appointmentadmin.ui.hub.customers

import android.util.TypedValue
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.data.User

@BindingAdapter("users")
fun bindUsersList(recyclerView: RecyclerView, users: List<User>?) {
    val adapter = recyclerView.adapter as UserAdapter

    val sortedList = users?.sortedWith(compareBy { it.nickname })

    adapter.submitList(sortedList)
}

@BindingAdapter("stroke")
fun bindStroke(materialCardView: MaterialCardView, registered: Boolean?) {
    if (registered == true) {
        materialCardView.strokeColor =
            materialCardView.resources.getColor(R.color.registered_stroke, null)
        materialCardView.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1.1f,
            materialCardView.resources.displayMetrics
        ).toInt()
    }
}