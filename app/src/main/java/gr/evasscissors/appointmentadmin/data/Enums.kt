package gr.evasscissors.appointmentadmin.data

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

enum class ConflictChoice(val code: Int) {
    UNREGISTERD(0),
    REGISTERED(1)
}

enum class GDriveOperation() {
    INITIALIZE,
    PHOTO_UPLOAD
}