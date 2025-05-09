package meow.softer.mydiary.main.topic

import androidx.annotation.DrawableRes

interface ITopic {
    /**
     * For update topic
     */
    var title: String?

    val type: Int

    val id: Long

    @get:DrawableRes
    val icon: Int

    /**
     * For update count in Main Page
     */
    var count: Long

    /**
     * For update topic
     */
    var color: Int

    /**
     * For the left swipe
     */
    var isPinned: Boolean

    companion object {
        /**
         * The contacts , Mitsuha  and Taki change their cell phone number in this function.
         */
        const val TYPE_CONTACTS: Int = 0

        /**
         * Mitsuha and Taki write daily diary when their soul change.
         */
        const val TYPE_DIARY: Int = 1

        /**
         * Mitsuha and Taki add some memo to notice that something can't do.
         */
        const val TYPE_MEMO: Int = 2
    }
}
