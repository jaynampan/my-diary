package meow.softer.mydiary.contacts

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.TextView
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.ColorTools.getColor

class LetterSortLayout(private val mContext: Context?, attrs: AttributeSet?) : LinearLayout(
    mContext, attrs
) {
    private var onTouchingLetterChangedListener: OnTouchingLetterChangedListener? = null

    private val sortTextList: MutableList<String?> = ArrayList<String?>()
    private var Choose = -1
    private var sortTextView: TextView? = null

    interface OnTouchingLetterChangedListener {
        fun onTouchingLetterChanged(s: String?)
    }

    fun setSortTextView(sortTextView: TextView?) {
        this.sortTextView = sortTextView
    }

    init {
        orientation = VERTICAL
        initSortText()
    }

    private fun initSortText() {
        addView(buildTextLayout("#"))
        var i = 'A'
        while (i <= 'Z') {
            val character = i.toString() + ""
            val tv = buildTextLayout(character)
            addView(tv)
            i++
        }
    }

    private fun buildTextLayout(character: String?): TextView {
        sortTextList.add(character)
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f)

        val sortTextView = TextView(mContext)
        sortTextView.setLayoutParams(layoutParams)
        sortTextView.setGravity(Gravity.CENTER)
        sortTextView.isClickable = true
        sortTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
        sortTextView.text = character
        sortTextView.setTextColor(getColor(context, R.color.contacts_latter_text))
        sortTextView.setShadowLayer(1f, 1f, 1f, R.color.contacts_latter_text_shadow)
        return sortTextView
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val y = event.y
        val oldChoose = Choose
        val listener = onTouchingLetterChangedListener
        val clickItem = (y / height * sortTextList.size).toInt()

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                Choose = -1 //
                invalidate()
                if (sortTextView != null) {
                    sortTextView!!.visibility = GONE
                }
            }

            else -> if (oldChoose != clickItem) {
                if (clickItem >= 0 && clickItem < sortTextList.size) {
                    listener?.onTouchingLetterChanged(sortTextList[clickItem])
                    if (sortTextView != null) {
                        sortTextView!!.text = sortTextList[clickItem]
                        sortTextView!!.visibility = VISIBLE
                    }
                    Choose = clickItem
                    invalidate()
                }
            }
        }
        return true
    }

    fun setOnTouchingLetterChangedListener(
        onTouchingLetterChangedListener: OnTouchingLetterChangedListener?
    ) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener
    }
}
