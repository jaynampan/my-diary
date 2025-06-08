package meow.softer.mydiary.shared.statusbar

import android.app.Activity
import android.view.WindowManager

class FlymeHelper : IStatusBarFontHelper {
    override fun setStatusBarLightMode(activity: Activity, isFontColorDark: Boolean): Boolean {
        val window = activity.window
        var result = false
        if (window != null) {
            try {
                val lp = window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                    .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                value = if (isFontColorDark) {
                    value or bit
                } else {
                    value and bit.inv()
                }

                meizuFlags.setInt(lp, value)
                window.setAttributes(lp)
                result = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }
}