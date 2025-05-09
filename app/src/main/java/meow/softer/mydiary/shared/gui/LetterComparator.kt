package meow.softer.mydiary.shared.gui

import meow.softer.mydiary.contacts.ContactsEntity

class LetterComparator : Comparator<ContactsEntity> {
    override fun compare(lhs: ContactsEntity, rhs: ContactsEntity): Int {
        return lhs.sortLetters!!.compareTo(rhs.sortLetters!!)
    }
}
