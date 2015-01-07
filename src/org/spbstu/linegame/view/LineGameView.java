package org.spbstu.linegame.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import org.spbstu.linegame.logic.LineGameLogic;

public class LineGameView extends SurfaceView implements SurfaceHolder.Callback {


    private LineGameThread gameThread;
    private LineGameLogic gameLogic;
    private Context context;

    public LineGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        getHolder().addCallback(this);

        gameLogic = null;
        gameThread = null;
        setFocusable(true);
    }

    public void setLogic(LineGameLogic logic) {
        gameLogic = logic;
        if (gameThread != null)
            gameThread.setGameLogic(logic);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // starting main draw thread
        gameThread = new LineGameThread(holder, context);
        gameThread.start();
        gameThread.setGameLogic(gameLogic);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        gameThread.resizeSurface(width, height);
        gameLogic.fieldResize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (gameThread != null)
            gameThread.kill();
    }
}
