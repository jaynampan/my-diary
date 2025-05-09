package meow.softer.mydiary.shared.statusbar

import android.app.Activity

enum class PhoneModel {
    OTHER, MIUI, FLY_ME, UNSPECIFIED
}

class ChinaPhoneHelper {

    companion object {
        var deviceStatusBarType = PhoneModel.UNSPECIFIED
            private set


        fun setStatusBar(activity: Activity, lightMode: Boolean) {
            var result = PhoneModel.OTHER
            if (MIUIHelper().setStatusBarLightMode(activity, lightMode)) {
                result = PhoneModel.MIUI
            } else if (FlymeHelper().setStatusBarLightMode(activity, lightMode)) {
                result = PhoneModel.FLY_ME
            }
            if (deviceStatusBarType == PhoneModel.UNSPECIFIED) {
                deviceStatusBarType = result
            }
        }
    }

}
