package meow.softer.mydiary.entries.diary.item

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.EditText
import meow.softer.mydiary.shared.ScreenHelper

class DiaryText(context: Context,
                override var content: String?,
                override val type: Int = IDiaryRow.TYPE_TEXT,
                override val view: View?
) : IDiaryRow {
    private var EDT_diary_text: EditText? = null
    override var position = 0

    init {
        createEditText(context)
        //Default is editable
        setEditMode(true)
    }

    private fun createEditText(context: Context) {
        EDT_diary_text = EditText(context)
        EDT_diary_text!!.setTextColor(Color.BLACK)
        EDT_diary_text!!.setBackgroundColor(Color.TRANSPARENT)
        EDT_diary_text!!.setGravity(Gravity.TOP or Gravity.LEFT)
        //2dp paddding
        val padding = ScreenHelper.dpToPixel(context.resources, 2)
        EDT_diary_text!!.setPadding(padding, padding, padding, padding)
    }

    fun insertText(text: String?) {
        EDT_diary_text!!.getText().insert(EDT_diary_text!!.getText().length, text)
    }




    override fun setEditMode(isEditMode: Boolean) {
        if (isEditMode) {
            EDT_diary_text!!.setFocusable(true)
            EDT_diary_text!!.setFocusableInTouchMode(true)
            EDT_diary_text!!.isClickable = true
            EDT_diary_text!!.setEnabled(true)
        } else {
            EDT_diary_text!!.setFocusable(false)
            EDT_diary_text!!.setFocusableInTouchMode(false)
            EDT_diary_text!!.isClickable = false
            EDT_diary_text!!.setEnabled(false)
        }
    }

     fun setTextPosition(position: Int) {
        this.position = position
        if (EDT_diary_text!!.tag != null && EDT_diary_text!!.tag is DiaryTextTag) {
            (EDT_diary_text!!.tag as DiaryTextTag).positionTag = position
        } else {
            val tag = DiaryTextTag(position)
            EDT_diary_text!!.tag = tag
        }
    }

}
