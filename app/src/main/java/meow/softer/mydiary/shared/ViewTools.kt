package meow.softer.mydiary.shared

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ScrollView
import androidx.annotation.DrawableRes
import meow.softer.mydiary.shared.ThemeManager.Companion.instance

object ViewTools {
    fun getDrawable(context: Context, @DrawableRes drawRes: Int): Drawable? {
        val returnDraw = context.resources.getDrawable(drawRes, null)
        return returnDraw
    }

    fun setScrollBarColor(context: Context, scrollView: ScrollView?) {
        try {
            val mScrollCacheField = View::class.java.getDeclaredField("mScrollCache")
            //get "mScrollCache" by reflection can bypass access restrictions 
            mScrollCacheField.isAccessible = true
            val mScrollCache: Any? = checkNotNull(mScrollCacheField.get(scrollView))
            val scrollBarField = mScrollCache!!.javaClass.getDeclaredField("scrollBar")
            scrollBarField.isAccessible = true
            val scrollBar: Any? = checkNotNull(scrollBarField.get(mScrollCache))
            val method = scrollBar!!.javaClass.getDeclaredMethod(
                "setVerticalThumbDrawable",
                Drawable::class.java
            )
            method.isAccessible = true
            method.invoke(scrollBar, ColorDrawable(instance!!.getThemeDarkColor(context)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Check if the position is inside the clickable area
     */
    fun hitTest(v: View, x: Int, y: Int): Boolean {
        val tx = (v.translationX + 0.5f).toInt()
        val ty = (v.translationY + 0.5f).toInt()
        val left = v.left + tx
        val right = v.right + tx
        val top = v.top + ty
        val bottom = v.bottom + ty

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom)
    }
}
