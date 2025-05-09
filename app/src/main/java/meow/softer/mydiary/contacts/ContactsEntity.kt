package meow.softer.mydiary.contacts

class ContactsEntity(
    @JvmField val contactsId: Long,
    @JvmField val name: String?,
    @JvmField val phoneNumber: String?,
    val photo: String?
) {
    @JvmField
    var sortLetters: String? = null
}
