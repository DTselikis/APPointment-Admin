package gr.evasscissors.appointmentadmin.ui.customer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import gr.evasscissors.appointmentadmin.data.Tab
import gr.evasscissors.appointmentadmin.ui.customer.contact.CustomerContactFragment
import gr.evasscissors.appointmentadmin.ui.customer.edit.CustomerProfileEditFragment
import gr.evasscissors.appointmentadmin.ui.customer.note.NotesFragment

class CustomerPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            Tab.CONTACT.code -> CustomerContactFragment()
            Tab.NOTES.code -> NotesFragment()
            else -> CustomerProfileEditFragment()
        }
    }
}