package com.homelab.appointmentadmin.ui.customer.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homelab.appointmentadmin.databinding.CustomerContactItemBinding
import com.homelab.appointmentadmin.model.ContactProviderInfo

class ContactProvidersAdapter :
    ListAdapter<ContactProviderInfo, ContactProvidersAdapter.ContactProviderViewHolder>(DiffCallback) {

    inner class ContactProviderViewHolder(
        val binding: CustomerContactItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contactProviderInfo: ContactProviderInfo) {
            binding.contactProviderInfo = contactProviderInfo
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ContactProviderInfo>() {
        override fun areItemsTheSame(
            oldItem: ContactProviderInfo,
            newItem: ContactProviderInfo
        ): Boolean {
            return oldItem.icon == newItem.icon
        }

        override fun areContentsTheSame(
            oldItem: ContactProviderInfo,
            newItem: ContactProviderInfo
        ): Boolean {
            return oldItem.text == newItem.text
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactProviderViewHolder {
        return ContactProviderViewHolder(
            CustomerContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ContactProviderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}