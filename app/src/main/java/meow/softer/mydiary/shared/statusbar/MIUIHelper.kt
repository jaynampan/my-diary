package meow.softer.mydiary.shared.statusbar

import android.app.Activity
import android.view.WindowManager

class MIUIHelper : IStatusBarFontHelper {
    override fun setStatusBarLightMode(activity: Activity, isFontColorDark: Boolean): Boolean {
        val window = activity.window
        var result = false
        if (window != null) {
            try {
                val clazz: Class<*> = window.javaClass
                var darkModeFlag: Int
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod(
                    "setExtraFlags",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
                if (isFontColorDark) {
                    //set Translucent
                    val winParams = window.attributes
                    val bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    if (isFontColorDark) {
                        winParams.flags = winParams.flags or bits
                    } else {
                        winParams.flags = winParams.flags and bits.inv()
                    }
                    window.setAttributes(winParams)
                    extraFlagField.invoke(
                        window,
                        darkModeFlag,
                        darkModeFlag
                    ) // transparent status bar with black font
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag) //clear dark font 
                }
                result = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }
}
