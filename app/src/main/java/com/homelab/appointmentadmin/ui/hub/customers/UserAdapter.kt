package com.homelab.appointmentadmin.ui.hub.customers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homelab.appointmentadmin.data.User
import com.homelab.appointmentadmin.databinding.CustomerHubItemBinding

class UserAdapter(
    private val customersFragment: CustomersFragment
) : ListAdapter<MutableLiveData<User>, UserAdapter.UserViewHolder>(Diffcallback) {

    inner class UserViewHolder(
        private val binding: CustomerHubItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: MutableLiveData<User>) {
            binding.user = user.value
            binding.customerFragment = customersFragment
        }
    }

    companion object Diffcallback : DiffUtil.ItemCallback<MutableLiveData<User>>() {
        override fun areItemsTheSame(oldItem: MutableLiveData<User>, newItem: MutableLiveData<User>): Boolean {
            return oldItem.value!!.uid == newItem.value!!.uid
        }

        override fun areContentsTheSame(oldItem: MutableLiveData<User>, newItem: MutableLiveData<User>): Boolean {
            return oldItem.value!!.lastname == newItem.value!!.lastname
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            CustomerHubItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}