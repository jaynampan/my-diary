package meow.softer.mydiary.main.topic

import meow.softer.mydiary.R
import meow.softer.mydiary.main.topic.ITopic.Companion.TYPE_DIARY

class Diary(
    override val id: Long,
    override var title: String?,
    override var color: Int,
    override val type: Int =
        TYPE_DIARY,
    override val icon: Int = R.drawable.ic_topic_diary,
    override var isPinned: Boolean = false
) : ITopic {
    override var count: Long = 0



}
