package meow.softer.mydiary.shared.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BoardImageView extends androidx.appcompat.widget.AppCompatImageView {
    private Rect rect;
    private Paint paint;
    public BoardImageView(@NonNull Context context) {
        super(context);
        init(context,null,0);
    }

    public BoardImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public BoardImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,0);
    }
    private void init(Context context,AttributeSet attrs,int defStyle){
        rect=new Rect();
        paint=new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.getClipBounds(rect);//获取当前画布的裁剪边界,裁剪边界赋值给rect
        rect.bottom--;
        rect.right--;//将裁剪边界底部和右侧坐标减少1
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);//设置为描边样式
        paint.setStrokeWidth(3);
        canvas.drawRect(rect,paint);
    }
}
