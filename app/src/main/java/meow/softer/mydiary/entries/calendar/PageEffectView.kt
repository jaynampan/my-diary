package meow.softer.mydiary.entries.calendar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Region
import android.graphics.drawable.GradientDrawable
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.ScreenHelper
import meow.softer.mydiary.shared.statusbar.ChinaPhoneHelper
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin
import androidx.core.graphics.createBitmap
import meow.softer.mydiary.shared.statusbar.PhoneModel

class PageEffectView(context: Context, calendar: Calendar) : View(context) {
    private var calendarFactory: CalendarFactory? = null

    private var mWidth = 0
    private var mHeight = 0
    private var mCornerX = 0 // 拖拽点对应的页脚
    private var mCornerY = 0
    private var mPath0: Path? = null
    private var mPath1: Path? = null
    private var mCurPageBitmap: Bitmap? = null // this page
    private var mNextPageBitmap: Bitmap? = null
    private var mCurrentPageCanvas: Canvas? = null
    private var mNextPageCanvas: Canvas? = null

    //This rect is to clip the over view shadow.
    private var calendarRect: Rect? = null

    private val mTouch = PointF() // Touch point
    private var initialTouchX = 0f // start touch point
    private var minSize = 0
    private val mBezierStart1 = PointF() // 贝塞尔曲线起始点
    private val mBezierControl1 = PointF() // 贝塞尔曲线控制点
    private val mBeziervertex1 = PointF() // 贝塞尔曲线顶点
    private var mBezierEnd1 = PointF() // 贝塞尔曲线结束点

    private val mBezierStart2 = PointF() // 另一条贝塞尔曲线
    private val mBezierControl2 = PointF()
    private val mBeziervertex2 = PointF()
    private var mBezierEnd2 = PointF()

    private var mMiddleX = 0f
    private var mMiddleY = 0f
    private var mDegrees = 0f
    private var mTouchToCornerDis = 0f
    private var mColorMatrixFilter: ColorMatrixColorFilter? = null
    private var mMatrix: Matrix? = null
    private val mMatrixArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1.0f)

    private var mIsRTandLB = false // 是否属于右上左下
    private var mMaxLength = 0f
    private var mBackShadowColors: IntArray? = null
    private var mFrontShadowColors: IntArray? = null
    private var mBackShadowDrawableLR: GradientDrawable? = null
    private var mBackShadowDrawableRL: GradientDrawable? = null
    private var mFolderShadowDrawableLR: GradientDrawable? = null
    private var mFolderShadowDrawableRL: GradientDrawable? = null

    private var mFrontShadowDrawableHBT: GradientDrawable? = null
    private var mFrontShadowDrawableHTB: GradientDrawable? = null
    private var mFrontShadowDrawableVLR: GradientDrawable? = null
    private var mFrontShadowDrawableVRL: GradientDrawable? = null

    private var mPaint: Paint? = null

    private var mScroller: Scroller? = null


    //Calendar lock
    private var isCalendarUpdated = false

    init {
        init(context, calendar)
    }

    private fun init(context: Context, calendar: Calendar) {
        //View


        setScreen(context)
        createBitmaps()

        //Set calendar , this object should be created after w,h was set.
        calendarFactory = CalendarFactory(context, calendar, mWidth, mHeight)

        //Page effect
        mPath0 = Path()
        mPath1 = Path()
        createDrawable()

        mPaint = Paint()
        mPaint!!.style = Paint.Style.FILL

        val cm = ColorMatrix()
        val array: FloatArray? = floatArrayOf(
            0.55f, 0f, 0f, 0f, 80.0f, 0f, 0.55f, 0f, 0f, 80.0f, 0f, 0f,
            0.55f, 0f, 80.0f, 0f, 0f, 0f, 0.2f, 0f
        )
        cm.set(array)
        mColorMatrixFilter = ColorMatrixColorFilter(cm)
        mMatrix = Matrix()
        mScroller = Scroller(getContext())

        mTouch.x = 0.01f // 不让x,y为0,否则在点计算时会有问题
        mTouch.y = 0.01f

        //Set today text
        calendarFactory!!.onDraw(mCurrentPageCanvas!!)
    }

    private fun setScreen(context: Context) {
        mWidth = ScreenHelper.getScreenWidth(context)
        if (ChinaPhoneHelper.deviceStatusBarType == PhoneModel.OTHER) {
            mHeight =
                ((ScreenHelper.getScreenHeight(context) - ScreenHelper.getStatusBarHeight(context)
                        - context.resources.getDimension(R.dimen.top_bar_height))
                        * 0.7).toInt()
        } else {
            mHeight = ((ScreenHelper.getScreenHeight(context) -
                    context.resources.getDimension(R.dimen.top_bar_height))
                    * 0.7).toInt()
        }
        calendarRect = Rect(0, 0, mWidth, mHeight)
        mMaxLength = hypot(mWidth.toDouble(), mHeight.toDouble()).toFloat()
        minSize = mWidth / 10
    }

    private fun createBitmaps() {
        mCurPageBitmap = createBitmap(mWidth, mHeight)
        mNextPageBitmap = createBitmap(mWidth, mHeight)

        mCurrentPageCanvas = Canvas(mCurPageBitmap!!)
        mNextPageCanvas = Canvas(mNextPageBitmap!!)
    }

    fun calcCornerXY(x: Float, y: Float) {
        if (x <= mWidth / 2) mCornerX = 0
        else mCornerX = mWidth
        if (y <= mHeight / 2) mCornerY = 0
        else mCornerY = mHeight
        if ((mCornerX == 0 && mCornerY == mHeight)
            || (mCornerX == mWidth && mCornerY == 0)
        ) mIsRTandLB = true
        else mIsRTandLB = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return doTouchEvent(event)
    }

    fun doTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                isCalendarUpdated = false
                //
                abortAnimation()
                calcCornerXY(event.x, event.y)
                calendarFactory!!.onDraw(mCurrentPageCanvas!!)
                //
                mTouch.x = event.x
                mTouch.y = event.y
                initialTouchX = event.x
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragOverMinSize(event.x) && !isCalendarUpdated) {
                    if (DragToRight()) {
                        calendarFactory!!.preDateDraw(mNextPageCanvas!!)
                    } else {
                        calendarFactory!!.nextDateDraw(mNextPageCanvas!!)
                    }
                    isCalendarUpdated = true
                }
                mTouch.x = event.x
                mTouch.y = event.y
                this.postInvalidate()
            }

            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
                if (canDragOver() && isCalendarUpdated) {
                    startAnimation(1200)
                } else {
                    mTouch.x = mCornerX - 0.09f
                    mTouch.y = mCornerY - 0.09f
                }
                this.postInvalidate()
            }

            MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(false)
        }
        return true
    }

    fun getCross(P1: PointF, P2: PointF, P3: PointF, P4: PointF): PointF {
        val CrossP = PointF()
        // 二元函数通式： y=ax+b
        val a1 = (P2.y - P1.y) / (P2.x - P1.x)
        val b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x)

        val a2 = (P4.y - P3.y) / (P4.x - P3.x)
        val b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x)
        CrossP.x = (b2 - b1) / (a1 - a2)
        CrossP.y = a1 * CrossP.x + b1
        return CrossP
    }

    private fun calcPoints() {
        mMiddleX = (mTouch.x + mCornerX) / 2
        mMiddleY = (mTouch.y + mCornerY) / 2
        mBezierControl1.x = (mMiddleX - (mCornerY - mMiddleY)
                * (mCornerY - mMiddleY) / (mCornerX - mMiddleX))
        mBezierControl1.y = mCornerY.toFloat()
        mBezierControl2.x = mCornerX.toFloat()
        mBezierControl2.y = (mMiddleY - (mCornerX - mMiddleX)
                * (mCornerX - mMiddleX) / (mCornerY - mMiddleY))

        // Log.i("TAG", "mTouchX  " + mTouch.x + "  mTouchY  " + mTouch.y);
        // Log.i("TAG", "mBezierControl1.x  " + mBezierControl1.x
        // + "  mBezierControl1.y  " + mBezierControl1.y);
        // Log.i("TAG", "mBezierControl2.x  " + mBezierControl2.x
        // + "  mBezierControl2.y  " + mBezierControl2.y);
        mBezierStart1.x = (mBezierControl1.x - (mCornerX - mBezierControl1.x)
                / 2)
        mBezierStart1.y = mCornerY.toFloat()

        // 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
        // 如果继续翻页，会出现BUG故在此限制
        if (mTouch.x > 0 && mTouch.x < mWidth) {
            if (mBezierStart1.x < 0 || mBezierStart1.x > mWidth) {
                if (mBezierStart1.x < 0) mBezierStart1.x = mWidth - mBezierStart1.x

                val f1 = abs((mCornerX - mTouch.x).toDouble()).toFloat()
                val f2 = mWidth * f1 / mBezierStart1.x
                mTouch.x = abs((mCornerX - f2).toDouble()).toFloat()

                val f3: Double =
                    abs((mCornerX - mTouch.x).toDouble()) * abs((mCornerY - mTouch.y).toDouble()) / f1
                mTouch.y = abs((mCornerY - f3).toDouble()).toFloat()

                mMiddleX = (mTouch.x + mCornerX) / 2
                mMiddleY = (mTouch.y + mCornerY) / 2

                mBezierControl1.x = (mMiddleX - (mCornerY - mMiddleY)
                        * (mCornerY - mMiddleY) / (mCornerX - mMiddleX))
                mBezierControl1.y = mCornerY.toFloat()

                mBezierControl2.x = mCornerX.toFloat()
                mBezierControl2.y = (mMiddleY - (mCornerX - mMiddleX)
                        * (mCornerX - mMiddleX) / (mCornerY - mMiddleY))
                // Log.i("TAG", "mTouchX --> " + mTouch.x + "  mTouchY-->  "
                // + mTouch.y);
                // Log.i("TAG", "mBezierControl1.x--  " + mBezierControl1.x
                // + "  mBezierControl1.y -- " + mBezierControl1.y);
                // Log.i("TAG", "mBezierControl2.x -- " + mBezierControl2.x
                // + "  mBezierControl2.y -- " + mBezierControl2.y);
                mBezierStart1.x = (mBezierControl1.x
                        - (mCornerX - mBezierControl1.x) / 2)
            }
        }
        mBezierStart2.x = mCornerX.toFloat()
        mBezierStart2.y = (mBezierControl2.y - (mCornerY - mBezierControl2.y)
                / 2)

        mTouchToCornerDis = hypot(
            (mTouch.x - mCornerX).toDouble(),
            (mTouch.y - mCornerY).toDouble()
        ).toFloat()

        mBezierEnd1 = getCross(
            mTouch, mBezierControl1, mBezierStart1,
            mBezierStart2
        )
        mBezierEnd2 = getCross(
            mTouch, mBezierControl2, mBezierStart1,
            mBezierStart2
        )

        // Log.i("TAG", "mBezierEnd1.x  " + mBezierEnd1.x + "  mBezierEnd1.y  "
        // + mBezierEnd1.y);
        // Log.i("TAG", "mBezierEnd2.x  " + mBezierEnd2.x + "  mBezierEnd2.y  "
        // + mBezierEnd2.y);

        /*
         * mBeziervertex1.x 推导
         * ((mBezierStart1.x+mBezierEnd1.x)/2+mBezierControl1.x)/2 化简等价于
         * (mBezierStart1.x+ 2*mBezierControl1.x+mBezierEnd1.x) / 4
         */
        mBeziervertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4
        mBeziervertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4
        mBeziervertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4
        mBeziervertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4
    }

    private fun drawCurrentPageArea(canvas: Canvas, bitmap: Bitmap, path: Path?) {
        mPath0!!.reset()
        mPath0!!.moveTo(mBezierStart1.x, mBezierStart1.y)
        mPath0!!.quadTo(
            mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
            mBezierEnd1.y
        )
        mPath0!!.lineTo(mTouch.x, mTouch.y)
        mPath0!!.lineTo(mBezierEnd2.x, mBezierEnd2.y)
        mPath0!!.quadTo(
            mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
            mBezierStart2.y
        )
        mPath0!!.lineTo(mCornerX.toFloat(), mCornerY.toFloat())
        mPath0!!.close()

        canvas.save()
        //TODO:ERROR canvas.clipPath(path, Region.Op.XOR);
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.restore()
    }

    private fun drawNextPageAreaAndShadow(canvas: Canvas, bitmap: Bitmap) {
        mPath1!!.reset()
        mPath1!!.moveTo(mBezierStart1.x, mBezierStart1.y)
        mPath1!!.lineTo(mBeziervertex1.x, mBeziervertex1.y)
        mPath1!!.lineTo(mBeziervertex2.x, mBeziervertex2.y)
        mPath1!!.lineTo(mBezierStart2.x, mBezierStart2.y)
        mPath1!!.lineTo(mCornerX.toFloat(), mCornerY.toFloat())
        mPath1!!.close()

        mDegrees = Math.toDegrees(
            atan2(
                (mBezierControl1.x
                        - mCornerX).toDouble(), (mBezierControl2.y - mCornerY).toDouble()
            )
        ).toFloat()
        val leftx: Int
        val rightx: Int
        val mBackShadowDrawable: GradientDrawable
        if (mIsRTandLB) {
            leftx = (mBezierStart1.x).toInt()
            rightx = (mBezierStart1.x + mTouchToCornerDis / 4).toInt()
            mBackShadowDrawable = mBackShadowDrawableLR!!
        } else {
            leftx = (mBezierStart1.x - mTouchToCornerDis / 4).toInt()
            rightx = mBezierStart1.x.toInt()
            mBackShadowDrawable = mBackShadowDrawableRL!!
        }
        canvas.save()
        canvas.clipPath(mPath0!!)
        canvas.clipPath(mPath1!!, Region.Op.INTERSECT)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y)
        mBackShadowDrawable.setBounds(
            leftx, mBezierStart1.y.toInt(), rightx,
            (mMaxLength + mBezierStart1.y).toInt()
        )
        mBackShadowDrawable.draw(canvas)
        canvas.restore()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        calcPoints()
        drawCurrentPageArea(canvas, mCurPageBitmap!!, mPath0)
        drawNextPageAreaAndShadow(canvas, mNextPageBitmap!!)
        drawCurrentPageShadow(canvas)
        drawCurrentBackArea(canvas, mCurPageBitmap!!)
    }

    private fun createDrawable() {
        val color = intArrayOf(0x333333, -0x4fcccccd)
        mFolderShadowDrawableRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, color
        )
        mFolderShadowDrawableRL!!
            .setGradientType(GradientDrawable.LINEAR_GRADIENT)

        mFolderShadowDrawableLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, color
        )
        mFolderShadowDrawableLR!!
            .setGradientType(GradientDrawable.LINEAR_GRADIENT)

        mBackShadowColors = intArrayOf(-0xeeeeef, 0x111111)
        mBackShadowDrawableRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors
        )
        mBackShadowDrawableRL!!.setGradientType(GradientDrawable.LINEAR_GRADIENT)

        mBackShadowDrawableLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors
        )
        mBackShadowDrawableLR!!.setGradientType(GradientDrawable.LINEAR_GRADIENT)

        mFrontShadowColors = intArrayOf(-0x7feeeeef, 0x111111)
        mFrontShadowDrawableVLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors
        )
        mFrontShadowDrawableVLR!!
            .setGradientType(GradientDrawable.LINEAR_GRADIENT)
        mFrontShadowDrawableVRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors
        )
        mFrontShadowDrawableVRL!!
            .setGradientType(GradientDrawable.LINEAR_GRADIENT)

        mFrontShadowDrawableHTB = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors
        )
        mFrontShadowDrawableHTB!!
            .setGradientType(GradientDrawable.LINEAR_GRADIENT)

        mFrontShadowDrawableHBT = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors
        )
        mFrontShadowDrawableHBT!!
            .setGradientType(GradientDrawable.LINEAR_GRADIENT)
    }

    /**
     * Description : 绘制翻起页的阴影
     */
    fun drawCurrentPageShadow(canvas: Canvas) {
        val degree: Double
        if (mIsRTandLB) {
            degree = (Math.PI
                    / 4
                    - atan2(
                (mBezierControl1.y - mTouch.y).toDouble(), (mTouch.x
                        - mBezierControl1.x).toDouble()
            ))
        } else {
            degree = (Math.PI
                    / 4
                    - atan2(
                (mTouch.y - mBezierControl1.y).toDouble(), (mTouch.x
                        - mBezierControl1.x).toDouble()
            ))
        }
        // 翻起页阴影顶点与touch点的距离
        val d1 = 25f * 1.414 * cos(degree)
        val d2 = 25f * 1.414 * sin(degree)
        val x = (mTouch.x + d1).toFloat()
        val y: Float
        if (mIsRTandLB) {
            y = (mTouch.y + d2).toFloat()
        } else {
            y = (mTouch.y - d2).toFloat()
        }
        mPath1!!.reset()
        mPath1!!.moveTo(x, y)
        mPath1!!.lineTo(mTouch.x, mTouch.y)
        mPath1!!.lineTo(mBezierControl1.x, mBezierControl1.y)
        mPath1!!.lineTo(mBezierStart1.x, mBezierStart1.y)
        mPath1!!.close()
        var rotateDegrees: Float
        canvas.save()

        //TODO:ERROR canvas.clipPath(mPath0, Region.Op.XOR);
        canvas.clipPath(mPath1!!, Region.Op.INTERSECT)
        var leftx: Int
        var rightx: Int
        var mCurrentPageShadow: GradientDrawable
        if (mIsRTandLB) {
            leftx = (mBezierControl1.x).toInt()
            rightx = mBezierControl1.x.toInt() + 25
            mCurrentPageShadow = mFrontShadowDrawableVLR!!
        } else {
            leftx = (mBezierControl1.x - 25).toInt()
            rightx = mBezierControl1.x.toInt() + 1
            mCurrentPageShadow = mFrontShadowDrawableVRL!!
        }

        rotateDegrees = Math.toDegrees(
            atan2(
                (mTouch.x
                        - mBezierControl1.x).toDouble(), (mBezierControl1.y - mTouch.y).toDouble()
            )
        ).toFloat()
        canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y)
        mCurrentPageShadow.setBounds(
            leftx,
            (mBezierControl1.y - mMaxLength).toInt(), rightx,
            (mBezierControl1.y).toInt()
        )
        mCurrentPageShadow.draw(canvas)
        canvas.restore()

        mPath1!!.reset()
        mPath1!!.moveTo(x, y)
        mPath1!!.lineTo(mTouch.x, mTouch.y)
        mPath1!!.lineTo(mBezierControl2.x, mBezierControl2.y)
        mPath1!!.lineTo(mBezierStart2.x, mBezierStart2.y)
        mPath1!!.close()
        canvas.save()
        //TODO:ERROR canvas.clipPath(mPath0, Region.Op.XOR);
        canvas.clipPath(mPath1!!, Region.Op.INTERSECT)
        canvas.clipRect(calendarRect!!)
        if (mIsRTandLB) {
            leftx = (mBezierControl2.y).toInt()
            rightx = (mBezierControl2.y + 25).toInt()
            mCurrentPageShadow = mFrontShadowDrawableHTB!!
        } else {
            leftx = (mBezierControl2.y - 25).toInt()
            rightx = (mBezierControl2.y + 1).toInt()
            mCurrentPageShadow = mFrontShadowDrawableHBT!!
        }
        rotateDegrees = Math.toDegrees(
            atan2(
                (mBezierControl2.y
                        - mTouch.y).toDouble(), (mBezierControl2.x - mTouch.x).toDouble()
            )
        ).toFloat()
        canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y)
        val temp: Float
        if (mBezierControl2.y < 0) {
            temp = mBezierControl2.y - mHeight
        } else {
            temp = mBezierControl2.y
        }
        val hmg = hypot(mBezierControl2.x.toDouble(), temp.toDouble()).toInt()
        if (hmg > mMaxLength) {
            mCurrentPageShadow
                .setBounds(
                    (mBezierControl2.x - 25).toInt() - hmg, leftx,
                    (mBezierControl2.x + mMaxLength).toInt() - hmg,
                    rightx
                )
        } else {
            mCurrentPageShadow.setBounds(
                (mBezierControl2.x - mMaxLength).toInt(), leftx,
                (mBezierControl2.x).toInt(), rightx
            )
        }

        // Log.i("TAG", "mBezierControl2.x   " + mBezierControl2.x
        // + "  mBezierControl2.y  " + mBezierControl2.y);
        mCurrentPageShadow.draw(canvas)
        canvas.restore()
    }

    /**
     * Description : 绘制翻起页背面
     */
    private fun drawCurrentBackArea(canvas: Canvas, bitmap: Bitmap) {
        val i = (mBezierStart1.x + mBezierControl1.x).toInt() / 2
        val f1 = abs((i - mBezierControl1.x).toDouble()).toFloat()
        val i1 = (mBezierStart2.y + mBezierControl2.y).toInt() / 2
        val f2 = abs((i1 - mBezierControl2.y).toDouble()).toFloat()
        val f3 = min(f1.toDouble(), f2.toDouble()).toFloat()
        mPath1!!.reset()
        mPath1!!.moveTo(mBeziervertex2.x, mBeziervertex2.y)
        mPath1!!.lineTo(mBeziervertex1.x, mBeziervertex1.y)
        mPath1!!.lineTo(mBezierEnd1.x, mBezierEnd1.y)
        mPath1!!.lineTo(mTouch.x, mTouch.y)
        mPath1!!.lineTo(mBezierEnd2.x, mBezierEnd2.y)
        mPath1!!.close()
        val mFolderShadowDrawable: GradientDrawable
        val left: Int
        val right: Int
        if (mIsRTandLB) {
            left = (mBezierStart1.x - 1).toInt()
            right = (mBezierStart1.x + f3 + 1).toInt()
            mFolderShadowDrawable = mFolderShadowDrawableLR!!
        } else {
            left = (mBezierStart1.x - f3 - 1).toInt()
            right = (mBezierStart1.x + 1).toInt()
            mFolderShadowDrawable = mFolderShadowDrawableRL!!
        }
        canvas.save()
        canvas.clipPath(mPath0!!)
        canvas.clipPath(mPath1!!, Region.Op.INTERSECT)

        mPaint!!.setColorFilter(mColorMatrixFilter)

        val dis = hypot(
            (mCornerX - mBezierControl1.x).toDouble(),
            (mBezierControl2.y - mCornerY).toDouble()
        ).toFloat()
        val f8 = (mCornerX - mBezierControl1.x) / dis
        val f9 = (mBezierControl2.y - mCornerY) / dis
        mMatrixArray[0] = 1 - 2 * f9 * f9
        mMatrixArray[1] = 2 * f8 * f9
        mMatrixArray[3] = mMatrixArray[1]
        mMatrixArray[4] = 1 - 2 * f8 * f8
        mMatrix!!.reset()
        mMatrix!!.setValues(mMatrixArray)
        mMatrix!!.preTranslate(-mBezierControl1.x, -mBezierControl1.y)
        mMatrix!!.postTranslate(mBezierControl1.x, mBezierControl1.y)
        canvas.drawBitmap(bitmap, mMatrix!!, mPaint)
        // canvas.drawBitmap(bitmap, mMatrix, null);
        mPaint!!.setColorFilter(null)
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y)
        mFolderShadowDrawable.setBounds(
            left, mBezierStart1.y.toInt(), right,
            (mBezierStart1.y + mMaxLength).toInt()
        )
        mFolderShadowDrawable.draw(canvas)
        canvas.restore()
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller!!.computeScrollOffset()) {
            val x = mScroller!!.currX.toFloat()
            val y = mScroller!!.currY.toFloat()
            mTouch.x = x
            mTouch.y = y
            postInvalidate()
        }
    }

    private fun startAnimation(delayMillis: Int) {
        val dx: Int
        val dy: Int
        // dx 水平方向滑动的距离，负值会使滚动向左滚动
        // dy 垂直方向滑动的距离，负值会使滚动向上滚动
        if (mCornerX > 0) {
            dx = -(mWidth + mTouch.x).toInt()
        } else {
            dx = (mWidth - mTouch.x + mWidth).toInt()
        }
        if (mCornerY > 0) {
            dy = (mHeight - mTouch.y).toInt()
        } else {
            dy = (1 - mTouch.y).toInt() // 防止mTouch.y最终变为0
        }
        mScroller!!.startScroll(
            mTouch.x.toInt(), mTouch.y.toInt(), dx, dy,
            delayMillis
        )
    }

    fun abortAnimation() {
        if (!mScroller!!.isFinished) {
            mScroller!!.abortAnimation()
        }
    }

    /**
     * Check touch point is not in Corner
     *
     * @return
     */
    fun canDragOver(): Boolean {
        if (mTouchToCornerDis > minSize) {
            return true
        }
        return false
    }

    fun isDragOverMinSize(newX: Float): Boolean {
        if (DragToRight()) {
            if ((newX - initialTouchX) > minSize) {
                return true
            }
        } else {
            if ((initialTouchX - newX) > minSize) {
                return true
            }
        }
        return false
    }

    /**
     * Description : 是否从左边翻向右边
     */
    fun DragToRight(): Boolean {
        return mCornerX <= 0
    }
}
