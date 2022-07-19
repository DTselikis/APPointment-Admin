package com.homelab.appointmentadmin.ui.hub.customers

import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.homelab.appointmentadmin.data.User

@BindingAdapter("users")
fun bindUsersList (recyclerView: RecyclerView, users: List<MutableLiveData<User>>?) {
    val adapter = recyclerView.adapter as UserAdapter

    val sortedList = users?.sortedWith(compareBy { it.value!!.nickname })

    adapter.submitList(sortedList)
}