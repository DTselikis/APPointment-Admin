package com.homelab.appointmentadmin.ui.hub.customers

import android.util.TypedValue
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.data.User
import de.hdodenhof.circleimageview.CircleImageView

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

@BindingAdapter("imgUrl")
fun bindProfilePic(circleImageView: CircleImageView, user: User?) {
    user?.let {
        val placeholder = when (it.gender) {
            "M" -> R.drawable.male_placeholder_wo_bg
            else -> R.drawable.female_placeholder_wo_bg
        }

        circleImageView.load(user.profilePic) {
            placeholder(placeholder)
            error(placeholder)
            memoryCachePolicy(CachePolicy.ENABLED)
            diskCachePolicy(CachePolicy.ENABLED)
        }
    }
}