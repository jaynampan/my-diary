package meow.softer.mydiary.main.topic

import meow.softer.mydiary.R
import meow.softer.mydiary.main.topic.ITopic.Companion.TYPE_CONTACTS

class Contacts(
    override val id: Long,
    override var title: String?,
    override var color: Int,
    override val type: Int = TYPE_CONTACTS,
    override val icon: Int = R.drawable.ic_topic_contacts,
    override var isPinned: Boolean = false
) : ITopic {
    override var count: Long = 0




}
