package meow.softer.mydiary.ui.models

import meow.softer.mydiary.R

class Memo(
    override val id: Int,
    override var title: String,
    override var color: Int,
    override var count: Int = 0,
    override var isPinned: Boolean = false
) : ITopic {
    override val type = ITopic.Companion.TYPE_MEMO
    override val icon = R.drawable.ic_topic_memo
}