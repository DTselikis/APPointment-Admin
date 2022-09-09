package com.homelab.appointmentadmin.ui.hub.customers

import android.util.TypedValue
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.google.android.material.card.MaterialCardView
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.data.Gender
import com.homelab.appointmentadmin.data.User
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("users")
fun bindUsersList(recyclerView: RecyclerView, users: List<User>?) {
    val adapter = recyclerView.adapter as UserAdapter

    adapter.submitList(users)
}

@BindingAdapter("stroke")
fun bindStroke(materialCardView: MaterialCardView, registered: Boolean?) {
    if (registered == true) {
        materialCardView.strokeColor =
            materialCardView.resources.getColor(R.color.md_theme_dark_primary, null)
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
            Gender.FEMALE.code -> R.drawable.female_placeholder_wo_bg
            Gender.MALE.code -> R.drawable.male_placeholder_wo_bg
            else -> R.drawable.any_placeholder_wo_bg
        }

        circleImageView.load(user.profilePic) {
            placeholder(placeholder)
            error(placeholder)
            memoryCachePolicy(CachePolicy.ENABLED)
            diskCachePolicy(CachePolicy.ENABLED)
        }
    }
}