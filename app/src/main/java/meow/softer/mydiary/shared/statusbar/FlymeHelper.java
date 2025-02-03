package meow.softer.mydiary.shared.statusbar;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

public class FlymeHelper  implements IStatusBarFontHelper{
    @Override
    public boolean setStatusBarLightMode(Activity activity, boolean isFontColorDark) {
        Window window= activity.getWindow();
        boolean result=false;
        if (window!=null){
            try{
                WindowManager.LayoutParams lp=window.getAttributes();
                Field darkFlag=WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);//获取darkFlag字段的值
                int value = meizuFlags.getInt(lp);//获取meizuFlags字段在当前窗口属性（lp）上的值
                if (isFontColorDark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                //如果isFontColorDark为true，表示要将状态栏图标颜色设置为深色，那么就需要将bit标志位的值设为1，
                // 然后使用按位或（|）操作将bit标志位的值与value进行合并，最终得到的值赋给value。
                //如果isFontColorDark为false，表示要将状态栏图标颜色设置为浅色，那么就需要将bit标志位的值设为0，
                // 然后使用按位与非（~）操作对bit标志位取反，再将取反的结果与value进行按位与（&）操作，最终得到的值赋给value。
                meizuFlags.setInt(lp, value);//将更新后的值设置回去
                window.setAttributes(lp);//更新窗口属性
                result = true;//标记为成功
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }
}
