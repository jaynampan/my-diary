package meow.softer.mydiary.shared.statusbar

import android.app.Activity

interface IStatusBarFontHelper {
    fun setStatusBarLightMode(activity: Activity, isFontColorDark: Boolean): Boolean
}
