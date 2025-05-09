package meow.softer.mydiary.shared.gui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class BoardImageView : AppCompatImageView {
    private var rect: Rect? = null
    private var paint: Paint? = null

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, 0)
    }

    private fun init(context: Context?, attrs: AttributeSet?, defStyle: Int) {
        rect = Rect()
        paint = Paint()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.getClipBounds(rect!!)
        rect!!.bottom--
        rect!!.right--
        paint!!.setColor(Color.WHITE)
        paint!!.style = Paint.Style.STROKE
        paint!!.strokeWidth = 3f
        canvas.drawRect(rect!!, paint!!)
    }
}
