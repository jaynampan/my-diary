package meow.softer.mydiary.shared;

import android.content.Context;
import android.content.res.ColorStateList;

import androidx.annotation.ColorRes;

public class ColorTools {
    public static int getColor(Context context, @ColorRes int color) {
        //colorRes用于指示一个整数值必须引用一个颜色资源
        int returnColor;
        returnColor = context.getResources().getColor(color, null);
        return returnColor;
    }

    public static ColorStateList getColorStateList(Context context, @ColorRes int resId) {

        ColorStateList colorStateList;
        colorStateList = context.getResources().getColorStateList(resId, null);
        return colorStateList;
    }
}
