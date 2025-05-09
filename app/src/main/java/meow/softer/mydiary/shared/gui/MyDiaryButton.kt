package meow.softer.mydiary.shared.gui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.ColorTools
import meow.softer.mydiary.shared.ScreenHelper
import meow.softer.mydiary.shared.ThemeManager

class MyDiaryButton : AppCompatButton {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.setAllCaps(false)
        this.background = ThemeManager.instance!!.getButtonBgDrawable(context)
        this.setTextColor(ColorTools.getColorStateList(context, R.color.button_text_color))
        this.setStateListAnimator(null)
        this.setMinimumWidth(ScreenHelper.dpToPixel(context.resources, 80))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}
