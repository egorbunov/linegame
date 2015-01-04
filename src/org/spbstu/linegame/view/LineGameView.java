package org.spbstu.linegame.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import org.spbstu.linegame.R;
import org.spbstu.linegame.logic.LineGameLogic;

public class LineGameView extends SurfaceView implements SurfaceHolder.Callback {
    private LineGameThread gameThread;

    public LineGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

        gameThread = new LineGameThread(getHolder(), context);
        setFocusable(true);
    }

    public void setLogic(LineGameLogic logic) {
        gameThread.setGameLogic(logic);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameThread.setIsRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        gameThread.resizeSurface(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gameThread.pause();
    }
}
