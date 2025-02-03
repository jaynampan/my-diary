package meow.softer.mydiary.shared.statusbar;

import android.app.Activity;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ChinaPhoneHelper {
    @IntDef({
            OTHER,
            MIUI,
            FLYME,

    })
    @Retention(RetentionPolicy.SOURCE)
    //这段代码是使用注解定义了一个枚举类型，用来表示不同的操作系统类型。使用@IntDef注解来指定变量只能取特定的值，这样可以增强代码的可读性和类型安全性。
    //- @IntDef：这是一个元注解，用来标识这个注解可以应用在int类型的变量上。
    //- @Retention(RetentionPolicy.SOURCE)：这个注解表示该注解只在源代码中可见，编译成字节码后会被丢弃。
    //在这段代码中，定义了四个常量：
    //- OTHER：表示其他未知类型的操作系统。
    //- MIUI：表示小米的MIUI操作系统。
    //- FLYME：表示魅族的FLYME操作系统。
    //使用这种注解的好处是可以限制变量的取值范围，避免使用其他无效的值，同时也可以增加代码可读性和维护性。
    public @interface SystemType {

    }

    //定义了SystemType这个注解
    private static int deviceStatusBarType = 0;

    public static final int OTHER = -1;
    public static final int MIUI = 1;
    public static final int FLYME = 2;

    public static void setStatusBar(Activity activity, boolean lightMode) {
        @SystemType int result = OTHER;
        if (new MIUIHelper().setStatusBarLightMode(activity, lightMode)) {
            result = MIUI;
        } else if (new FlymeHelper().setStatusBarLightMode(activity, lightMode)) {
            result = FLYME;
        }
        if (deviceStatusBarType == 0) {
            deviceStatusBarType = result;
        }
    }

    public static int getDeviceStatusBarType() {
        return deviceStatusBarType;
    }
}
