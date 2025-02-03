package meow.softer.mydiary.oobe;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;

import com.github.amlcurran.showcaseview.targets.Target;

public class CustomViewTarget implements Target {
    private final View mView;
    private int offsetXPercent;
    private int offsetYPercent;

    public CustomViewTarget(View view, int offsetXPercent, int offsetYPercent) {
        mView = view;
        this.offsetXPercent = offsetXPercent;
        this.offsetYPercent = offsetYPercent;
    }

    public CustomViewTarget(int viewId, int offsetXPercent, int offsetYPercent, Activity activity) {
        this.offsetXPercent = offsetXPercent;
        this.offsetYPercent = offsetYPercent;
        mView = activity.findViewById(viewId);
    }

    @Override
    public Point getPoint() {
        int[] location = new int[2];
        mView.getLocationInWindow(location);
        int x = location[0] + mView.getWidth() / offsetXPercent;
        int y = location[1] + mView.getHeight() / offsetYPercent;
        return new Point(x, y);
    }
}
