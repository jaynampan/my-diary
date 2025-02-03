package meow.softer.mydiary.shared;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.DrawableRes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ViewTools {
    public static Drawable getDrawable(Context context, @DrawableRes int drawRes) {
        Drawable returnDraw;
        returnDraw = context.getResources().getDrawable(drawRes, null);
        return returnDraw;
    }
    public static void setScrollBarColor(Context context, ScrollView scrollView) {
        try {
            Field mScrollCacheField = View.class.getDeclaredField("mScrollCache");
            //通过反射获取“mScrollCache”通过反射可以绕过访问权限限制，对该字段进行读取和写入操作
            mScrollCacheField.setAccessible(true);
            Object mScrollCache = mScrollCacheField.get(scrollView);
            assert mScrollCache != null;
            Field scrollBarField = mScrollCache.getClass().getDeclaredField("scrollBar");
            scrollBarField.setAccessible(true);
            Object scrollBar = scrollBarField.get(mScrollCache);
            assert scrollBar != null;
            Method method = scrollBar.getClass().getDeclaredMethod("setVerticalThumbDrawable", Drawable.class);
            method.setAccessible(true);
            method.invoke(scrollBar, new ColorDrawable(ThemeManager.getInstance().getThemeDarkColor(context)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 判断给定的坐标是否在View v的可点击区域内
     * */
    public static boolean hitTest(View v, int x, int y) {
        final int tx = (int) (v.getTranslationX() + 0.5f);
        final int ty = (int) (v.getTranslationY() + 0.5f);
        final int left = v.getLeft() + tx;
        final int right = v.getRight() + tx;
        final int top = v.getTop() + ty;
        final int bottom = v.getBottom() + ty;

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
    }

}
