package com.homelab.appointmentadmin.data

enum class Tab(val code: Int) {
    CONTACT(0),
    EDIT(1),
    NOTES(2)
}

enum class CustomerFilter(val code: Int) {
    ALL(0),
    REGISTERED(1),
    UNREGISTERED(2)
}