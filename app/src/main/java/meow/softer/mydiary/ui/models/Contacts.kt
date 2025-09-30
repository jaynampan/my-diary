package meow.softer.mydiary.ui.models

import meow.softer.mydiary.R

class Contacts(
    override val id: Int,
    override var title: String,
    override var color: Int,
    override var isPinned: Boolean = false,
    override var count: Int = 0

) : ITopic {
    override val type: Int = ITopic.Companion.TYPE_CONTACTS
    override val icon: Int = R.drawable.ic_topic_contacts
}
