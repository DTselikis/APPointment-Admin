package gr.evasscissors.appointmentadmin.model.network.helping

import gr.evasscissors.appointmentadmin.model.network.Note

data class Notes(
    val notes : Map<String, Note>? = null
)