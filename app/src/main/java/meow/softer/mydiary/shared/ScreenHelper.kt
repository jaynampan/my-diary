package meow.softer.mydiary.shared

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import java.text.DecimalFormat

object ScreenHelper {
    fun getScreenRatio(activity: Activity): Float {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val decimalFormat = DecimalFormat("#.##")
        val ScreenRatio =
            decimalFormat.format((metrics.heightPixels / metrics.widthPixels).toLong()).toFloat()
        return ScreenRatio
    }

    @JvmStatic
    fun dpToPixel(r: Resources, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.displayMetrics
        ).toInt()
    }

    @JvmStatic
    fun getScreenHeight(context: Context): Int {
        val metrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(metrics)
        return metrics.heightPixels
    }

    @JvmStatic
    fun getScreenWidth(context: Context): Int {
        val metrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(metrics)
        return metrics.widthPixels
    }

    @JvmStatic
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        //status_bar_height:要获取的资源名称，dimen:资源类型,android:所在包名
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }


    private fun openImmersiveMode(view: View) {
        view.setSystemUiVisibility(
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN //hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE)
        )
    }

    fun closeImmersiveMode(view: View) {
        view.setSystemUiVisibility(
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        )
    }
}
