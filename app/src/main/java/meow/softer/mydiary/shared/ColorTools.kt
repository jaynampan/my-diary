package meow.softer.mydiary.shared

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.ColorRes

object ColorTools {
    @JvmStatic
    fun getColor(context: Context, @ColorRes color: Int): Int {
        val returnColor: Int = context.resources.getColor(color, null)
        return returnColor
    }

    fun getColorStateList(context: Context, @ColorRes resId: Int): ColorStateList {
        val colorStateList: ColorStateList = context.resources.getColorStateList(resId, null)
        return colorStateList
    }
}
