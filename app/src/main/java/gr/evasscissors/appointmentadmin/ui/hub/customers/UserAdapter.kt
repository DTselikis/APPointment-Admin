package gr.evasscissors.appointmentadmin.ui.hub.customers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import gr.evasscissors.appointmentadmin.R
import gr.evasscissors.appointmentadmin.data.User
import gr.evasscissors.appointmentadmin.databinding.CustomerHubItemBinding

class UserAdapter(
    private val customersFragment: CustomersFragment
) : ListAdapter<User, UserAdapter.UserViewHolder>(Diffcallback) {

    inner class UserViewHolder(
        private val binding: CustomerHubItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
            binding.customerFragment = customersFragment
        }
    }

    companion object Diffcallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return  oldItem.firstname == newItem.firstname && oldItem.lastname == newItem.lastname
                    &&  oldItem.nickname == newItem.nickname &&  oldItem.phone == newItem.phone
                    &&  oldItem.email == newItem.email &&  oldItem.gender == newItem.gender
                    && oldItem.fbName == newItem.fbName && oldItem.registered == newItem.registered
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            CustomerHubItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)

        addContextMenu(holder.itemView, user)

        holder.bind(user)
    }

    private fun addContextMenu(itemView: View, user: User) {
        itemView.setOnCreateContextMenuListener { contextMenu, view, _ ->
            if (!user.registered) {
                contextMenu.add(view.context.getString(R.string.merge_customers))
                    .setOnMenuItemClickListener {
                        customersFragment.activateMergeMode(user)
                        true
                    }
            }
            contextMenu.add(view.context.getString(R.string.delete_user_context_menu_item))
                .setOnMenuItemClickListener {
                    customersFragment.deleteUser(user)
                    true
                }
        }
    }
}