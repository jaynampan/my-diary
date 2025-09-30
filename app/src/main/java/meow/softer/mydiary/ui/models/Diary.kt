package meow.softer.mydiary.ui.models

import meow.softer.mydiary.R

class Diary(
    override val id: Int,
    override var title: String,
    override var color: Int,
    override var isPinned: Boolean = false,
    override var count:Int = 0
) : ITopic {
    override val type = ITopic.Companion.TYPE_DIARY
    override val icon = R.drawable.ic_topic_diary
}
