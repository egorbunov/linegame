package org.spbstu.linegame.view.rules;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Egor Gorbunov on 13.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 */
public class LineSample extends View {

    Paint paint = null;
    private int width;
    private int height;

    public LineSample(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                width = getMeasuredWidth();
                height = getMeasuredHeight();
            }
        });
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (paint != null) {
            paint.setStrokeWidth(width / 1.8f);
            int Y_PADDING = 60;

            Path path = new Path();
            path.moveTo(width / 2, Y_PADDING);
            path.lineTo(width / 2, height - Y_PADDING);

            canvas.drawPath(path, paint);
        }
    }
}
