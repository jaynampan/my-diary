package meow.softer.mydiary.oobe

import android.app.Activity
import android.graphics.Point
import android.view.View
import com.github.amlcurran.showcaseview.targets.Target

class CustomViewTarget : Target {
    private val mView: View
    private val offsetXPercent: Int
    private val offsetYPercent: Int

    constructor(view: View, offsetXPercent: Int, offsetYPercent: Int) {
        mView = view
        this.offsetXPercent = offsetXPercent
        this.offsetYPercent = offsetYPercent
    }

    constructor(viewId: Int, offsetXPercent: Int, offsetYPercent: Int, activity: Activity) {
        this.offsetXPercent = offsetXPercent
        this.offsetYPercent = offsetYPercent
        mView = activity.findViewById<View>(viewId)
    }

    override fun getPoint(): Point {
        val location = IntArray(2)
        mView.getLocationInWindow(location)
        val x = location[0] + mView.width / offsetXPercent
        val y = location[1] + mView.height / offsetYPercent
        return Point(x, y)
    }
}
