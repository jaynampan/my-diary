package meow.softer.mydiary.entries.diary.item

import android.view.View
//todo : this triggers errors as getter setter mixed up
interface IDiaryRow {

    var content: String?


    val type: Int


    val view: View?

    fun setEditMode(isEditMode: Boolean)

    /**
     * get position for auto save
     * @return
     */
    /**
     * For resort after add new item
     *
     * @param position
     */
    var position: Int

    companion object {
        const val TYPE_TEXT: Int = 0
        const val TYPE_PHOTO: Int = 1
        const val TYPE_WEB_BLOCK: Int = 2
    }
}
