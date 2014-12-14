package org.spbstu.linegame.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import org.spbstu.linegame.R;

public class LineGameView extends View {
    private Bitmap background;
    private int backgoundYshift = 0;

    public LineGameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        background =  BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        background = Bitmap.createScaledBitmap(background,
                w, h, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        backgoundYshift = backgoundYshift - 5;
        int newYshift = background.getHeight() + backgoundYshift;
        canvas.drawBitmap(background, 0, backgoundYshift, null);
        if (newYshift <= 0) {
            backgoundYshift = 0;
        } else {
            canvas.drawBitmap(background, 0, newYshift, null);
        }

        this.invalidate();
    }


}
