package org.spbstu.linegame.view;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.spbstu.linegame.R;
import org.spbstu.linegame.logic.LineGameLogic;
import org.spbstu.linegame.logic.LogicListener;

public class LineGameView extends SurfaceView implements SurfaceHolder.Callback {
    private LineGameDrawingThread gameThread;
    private LineGameLogic gameLogic;
    private final Context context;




    public LineGameView(final Context context, AttributeSet attrs) {
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
        gameThread = new LineGameDrawingThread(holder, context);
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
