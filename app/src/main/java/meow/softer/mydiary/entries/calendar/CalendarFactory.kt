package meow.softer.mydiary.entries.calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import meow.softer.mydiary.shared.ScreenHelper
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.TimeTools
import java.util.Calendar

class CalendarFactory(
    private val mContext: Context,
    private val calendar: Calendar,
    width: Int,
    height: Int
) {
    private var dateChange = 0

    private val timeTools: TimeTools
    private val monthPaint: Paint
    private val datePaint: Paint
    private val dayPaint: Paint

    private val textRect: Rect
    private val textBaseX: Int
    private val centerBaseLine: Float
    private val monthBaseLine: Float
    private val dayBaseLine: Float

    //Test size
    private val scale: Float


    init {
        timeTools = TimeTools.getInstance(mContext)
        textRect = Rect(0, 0, width, height)
        scale = mContext.resources.displayMetrics.density

        //init Color
        monthPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        setPrintTextSize(monthPaint, 40f)
        monthPaint.setColor(ThemeManager.instance!!.getThemeDarkColor(mContext))
        monthPaint.textAlign = Paint.Align.CENTER

        datePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        setPrintTextSize(datePaint, 130f)
        datePaint.setColor(ThemeManager.instance!!.getThemeDarkColor(mContext))
        datePaint.textAlign = Paint.Align.CENTER
        datePaint.setTypeface(Typeface.DEFAULT_BOLD)

        dayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        setPrintTextSize(dayPaint, 25f)
        dayPaint.setColor(ThemeManager.instance!!.getThemeDarkColor(mContext))
        dayPaint.textAlign = Paint.Align.CENTER

        textBaseX = width / 2
        centerBaseLine =
            textRect.centerY() + (getTextHeight(datePaint) / 2) - datePaint.getFontMetrics().bottom
        monthBaseLine = centerBaseLine + (datePaint.getFontMetrics().top - ScreenHelper.dpToPixel(
            mContext.resources,
            5
        ))
        dayBaseLine = centerBaseLine + (getTextHeight(dayPaint) + ScreenHelper.dpToPixel(
            mContext.resources,
            20
        ))
    }

    private fun setPrintTextSize(paint: Paint, textSize: Float) {
        paint.textSize = textSize * scale + 0.5f
    }

    @Synchronized
    fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        updateCalendar(canvas)
    }


    @Synchronized
    fun nextDateDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        dateChange = 1
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + dateChange)
        updateCalendar(canvas)
    }

    @Synchronized
    fun preDateDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        dateChange = -1
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + dateChange)
        updateCalendar(canvas)
    }


    private fun updateCalendar(canvas: Canvas) {
        canvas.drawText(
            calendar.get(Calendar.DAY_OF_MONTH).toString(),
            textBaseX.toFloat(), centerBaseLine, datePaint
        )

        canvas.drawText(
            timeTools.monthsFullName?.get(calendar.get(Calendar.MONTH)).toString(),
            textBaseX.toFloat(), monthBaseLine, monthPaint
        )

        canvas.drawText(
            timeTools.daysFullName?.get(calendar.get(Calendar.DAY_OF_WEEK) - 1).toString(),
            textBaseX.toFloat(), dayBaseLine, dayPaint
        )
    }

    private fun getTextHeight(paint: Paint): Float {
        val fontMetrics = paint.getFontMetrics()
        val top = fontMetrics.top
        val bottom = fontMetrics.bottom
        return bottom - top
    }
}
