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

enum class GenderBtnId(val code: Int) {
    FEMALE(1),
    MALE(2),
    ANY(3)
}

enum class Gender(val code: String) {
    FEMALE("F"),
    MALE("M"),
    ANY("A")
}

enum class Conflict(val code: Int) {
    FIRSTNAME(0),
    LASTNAME(1),
    NICKNAME(2),
    PHONE(3),
    EMAIL(4)
}