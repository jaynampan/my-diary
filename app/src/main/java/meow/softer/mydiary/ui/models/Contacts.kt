package meow.softer.mydiary.ui.models

import meow.softer.mydiary.R

class Contacts(
    override val id: Long,
    override var title: String?,
    override var color: Int,
    override val type: Int = ITopic.Companion.TYPE_CONTACTS,
    override val icon: Int = R.drawable.ic_topic_contacts,
    override var isPinned: Boolean = false
) : ITopic {
    override var count: Long = 0




}
