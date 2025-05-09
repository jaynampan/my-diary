package meow.softer.mydiary.main.topic

import meow.softer.mydiary.R

class Memo(override val id: Long, override var title: String?, override var color: Int) : ITopic {
    override var count: Long = 0
    override var isPinned: Boolean = false


    override val type: Int
        get() = ITopic.Companion.TYPE_MEMO

    override val icon: Int
        get() = R.drawable.ic_topic_memo
}