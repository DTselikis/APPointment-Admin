package com.homelab.appointmentadmin.ui.hub.customers

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homelab.appointmentadmin.data.User

@BindingAdapter("users")
fun bindUsersList (recyclerView: RecyclerView, users: List<User>?) {
    val adapter = recyclerView.adapter as UserAdapter

    val sortedList = users?.sortedWith(compareBy { it.nickname })

    adapter.submitList(sortedList)
}