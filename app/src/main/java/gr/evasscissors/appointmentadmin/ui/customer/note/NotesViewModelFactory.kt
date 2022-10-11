package gr.evasscissors.appointmentadmin.ui.customer.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gr.evasscissors.appointmentadmin.data.User

class NotesViewModelFactory(
    private val user: User
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotesViewModel(user) as T
    }
}