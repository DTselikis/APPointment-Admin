package com.homelab.appointmentadmin.ui.hub.merge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homelab.appointmentadmin.data.User
import com.homelab.appointmentadmin.databinding.MergeProfileItemBinding

class MergeUserAdapter(
    private val mergeCustomersFragment: MergeCustomersFragment
) : ListAdapter<User, MergeUserAdapter.UserViewHolder>(Diffcallback) {

    inner class UserViewHolder(
        private val binding: MergeProfileItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
        }
    }

    companion object Diffcallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.lastname == newItem.lastname
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            MergeProfileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}