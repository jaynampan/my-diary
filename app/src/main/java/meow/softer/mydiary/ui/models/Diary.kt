package meow.softer.mydiary.ui.models

import meow.softer.mydiary.R

class Diary(
    override val id: Long,
    override var title: String?,
    override var color: Int,
    override val type: Int =
        ITopic.Companion.TYPE_DIARY,
    override val icon: Int = R.drawable.ic_topic_diary,
    override var isPinned: Boolean = false
) : ITopic {
    override var count: Long = 0
}
