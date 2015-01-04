package org.spbstu.linegame.view;


import android.content.Context;
import android.graphics.*;
import android.view.SurfaceHolder;
import org.spbstu.linegame.R;
import org.spbstu.linegame.logic.LineGameLogic;
import org.spbstu.linegame.model.curve.Curve;
import org.spbstu.linegame.model.curve.Point;

class LineGameThread extends Thread {

    private final SurfaceHolder surfaceHolder;
    private Rect surfaceFrame;
    private final Context context;
    private LineGameLogic gameLogic;


    private int backgroundShift;
    private Bitmap background;
    private int backgroundColor;
    private boolean isRunning;

    private int backgroundSpeed; // TODO: move it to logic part, or no...

    public LineGameThread(SurfaceHolder surfaceHolder, Context context) {

        this.surfaceHolder = surfaceHolder;
        this.context = context;
        this.surfaceFrame = surfaceHolder.getSurfaceFrame();

        background =  BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        backgroundColor = context.getResources().getColor(R.color.main_background_color);
        backgroundShift = 0;
        backgroundSpeed = 5;

    }

    public void setGameLogic(LineGameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    public void resizeSurface(int width, int height) {
        background = Bitmap.createScaledBitmap(background,
                width, height, true);
        surfaceFrame = surfaceHolder.getSurfaceFrame();
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public void run() {
        super.run();
        Canvas canvas = null;

        while(isRunning) {
            try {
                canvas = surfaceHolder.lockCanvas(null);
                if (canvas != null) {
                    redrawBackground(canvas);
                    drawLogic(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            canvas = null;
        }
    }

    public void pause() {
        isRunning = false;
    }

    private void redrawBackground(Canvas canvas) {
        if (canvas == null)
            return;

        canvas.drawColor(backgroundColor);
        backgroundShift = backgroundShift - backgroundSpeed;
        canvas.drawBitmap(background, 0, backgroundShift, null);
        canvas.drawBitmap(background, 0, background.getHeight() + backgroundShift, null);
        if (background.getHeight() + backgroundShift <= 0)
            backgroundShift = 0;
    }

    private void drawLogic(Canvas canvas) {
        if (canvas == null)
            return;

        Curve curve = gameLogic.getCurve();
        Paint paint = new Paint();
        paint.setStrokeWidth(gameLogic.getLineThickness());

        Point prevPoint = null;
        for (Point curPoint : curve) {
            if (prevPoint == null)
                prevPoint = curPoint;
            else {
                canvas.drawLine(scaleWidth(prevPoint.getX()), scaleHeight(prevPoint.getY()),
                        scaleWidth(curPoint.getX()), scaleHeight(curPoint.getY()), paint);
            }
        }
    }

    /**
     * Scales the value in [0, 1] to [0, surface.width()]
     * @param val - value to scale
     * @return
     */
    private float scaleWidth(float val) {
        assert val <= 1 && val >= 0;

        return val * surfaceFrame.width();
    }

    /**
     * Scales the value in [0, 1] to [0, surface.height()]
     * @param val - value to scale
     * @return
     */
    private float scaleHeight(float val) {
        assert val <= 1 && val >= 0;

        return val * surfaceFrame.height();
    }
}
