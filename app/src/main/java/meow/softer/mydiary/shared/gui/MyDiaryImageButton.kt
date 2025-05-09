package meow.softer.mydiary.shared.gui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.ColorTools
import meow.softer.mydiary.shared.ScreenHelper
import meow.softer.mydiary.shared.ThemeManager

class MyDiaryImageButton : AppCompatImageButton {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.background = ThemeManager.instance!!.getButtonBgDrawable(context)
        this.setColorFilter(ColorTools.getColor(context, R.color.imagebutton_hint_color))
        this.setStateListAnimator(null)
        this.setMinimumWidth(ScreenHelper.dpToPixel(context.resources, 80))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}
