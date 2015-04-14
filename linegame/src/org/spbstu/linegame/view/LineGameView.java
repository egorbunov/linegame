package org.spbstu.linegame.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import org.spbstu.linegame.logic.LineGameLogic;

public class LineGameView extends SurfaceView implements SurfaceHolder.Callback {
    private LineGameDrawingThread gameDrawingThread;
    private LineGameLogic gameLogic;
    private final Context context;
    SurfaceHolder holder;

    public LineGameView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        getHolder().addCallback(this);

        gameLogic = null;
        gameDrawingThread = null;
        setFocusable(true);
    }

    public void setLogic(LineGameLogic logic) {
        gameLogic = logic;
        if (gameDrawingThread != null)
            gameDrawingThread.setGameLogic(logic);
    }

    public void destroyDrawingThread() {
        gameDrawingThread.kill();
    }

    public void startDrawingThread() {
        if (holder != null) {
            gameDrawingThread = new LineGameDrawingThread(holder, context);
            gameDrawingThread.start();
            gameDrawingThread.setGameLogic(gameLogic);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
        // starting main draw thread
        startDrawingThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.holder = holder;
        gameDrawingThread.resizeSurface(width, height);
        gameLogic.fieldResize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (gameDrawingThread != null)
            gameDrawingThread.kill();
    }


}
