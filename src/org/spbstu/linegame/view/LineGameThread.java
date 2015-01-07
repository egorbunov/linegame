package org.spbstu.linegame.view;


import android.content.Context;
import android.graphics.*;
import android.view.SurfaceHolder;
import org.spbstu.linegame.R;
import org.spbstu.linegame.logic.LineGameLogic;
import org.spbstu.linegame.model.curve.Curve;
import org.spbstu.linegame.model.curve.CurvePoint;

import java.util.concurrent.atomic.AtomicBoolean;

class LineGameThread extends Thread {
    private AtomicBoolean isThreadAlive;

    private final SurfaceHolder surfaceHolder;
    private Rect surfaceFrame;
    private LineGameLogic gameLogic;


    private float backgroundShift;
    private Bitmap background;
    private int backgroundColor;

    private final int tappedLineColor;
    private final int mainLineColor;


    public LineGameThread(SurfaceHolder surfaceHolder, Context context) {
        isThreadAlive = new AtomicBoolean(true);

        this.surfaceHolder = surfaceHolder;
        this.surfaceFrame = surfaceHolder.getSurfaceFrame();

        background =  BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        backgroundColor = context.getResources().getColor(R.color.main_background_color);
        backgroundShift = 0f;

        tappedLineColor = context.getResources().getColor(R.color.tapped_line_color);
        mainLineColor = context.getResources().getColor(R.color.main_line_color);
    }

    public void kill() {
        isThreadAlive.set(false);
    }

    public void setGameLogic(LineGameLogic gameLogic) {
        if (gameLogic == null) {
            throw new NullPointerException();
        }

        this.gameLogic = gameLogic;
    }

    /**
     * Callback function, which is called from LineGameView.surfaceChanged(...) method.
     * @param width - new width of the view surface
     * @param height - new height of the view surface
     */
    public void resizeSurface(int width, int height) {
        background = Bitmap.createScaledBitmap(background,
                width, height, true);
        surfaceFrame = surfaceHolder.getSurfaceFrame();
    }

    @Override
    public void run() {
        super.run();
        Canvas canvas = null;

        while(isThreadAlive.get()) {
            // drawing all the game stuff
            try {
                canvas = surfaceHolder.lockCanvas(null);
                if (canvas == null)
                    continue;

                canvas.drawColor(backgroundColor);
                redrawBackground(canvas);
                drawLogic(canvas);
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            canvas = null;

        }
    }


    private void redrawBackground(Canvas canvas) {
        if (canvas == null)
            return;

        // scrolling background if needed
        backgroundShift = backgroundShift - gameLogic.getScrollSpeed();
        canvas.drawBitmap(background, 0, backgroundShift, null);
        canvas.drawBitmap(background, 0, background.getHeight() + backgroundShift, null);
        if (background.getHeight() + backgroundShift <= 0)
            backgroundShift = 0;
    }

    /**
     * Drawing line - main object of the view
     * @param canvas - just canvas
     */
    private void drawLogic(Canvas canvas) {
        if (canvas == null)
            return;

        Curve curve = gameLogic.getCurve();
        Paint paint = new Paint();
        paint.setStrokeWidth(gameLogic.getLineThickness());

        CurvePoint prevPoint = null;
        float midX, midY;
        for (CurvePoint curPoint : curve) {
            if (prevPoint != null) {
                if (prevPoint.isTapped() == curPoint.isTapped()) {
                    // if two points (previous and current) are both tapped or not tapped , we draw
                    // whole segment with one color
                    paint.setColor(prevPoint.isTapped() ? tappedLineColor : mainLineColor);
                    canvas.drawLine(scaleWidth(prevPoint.getX()), scaleHeight(prevPoint.getY()),
                            scaleWidth(curPoint.getX()), scaleHeight(curPoint.getY()), paint);
                } else {
                    // here we draw the segment with two different colors (half with color
                    // of the previous point and half with color of current)
                    midX = (prevPoint.getX() + curPoint.getX()) / 2f;
                    midY = (prevPoint.getY() + curPoint.getY()) / 2f;

                    paint.setColor(prevPoint.isTapped() ? tappedLineColor : mainLineColor);
                    canvas.drawLine(scaleWidth(prevPoint.getX()), scaleHeight(prevPoint.getY()),
                            scaleWidth(midX), scaleHeight(midY), paint);

                    paint.setColor(curPoint.isTapped() ? tappedLineColor : mainLineColor);
                    canvas.drawLine(scaleWidth(midX), scaleHeight(midY),
                            scaleWidth(curPoint.getX()), scaleHeight(curPoint.getY()), paint);
                }
            }
            prevPoint = curPoint;
        }
        gameLogic.nextCurveFrame();
    }

    /**
     * Scales the value in [0, 1] to [0, surface.width()]
     * @param val - value to scale
     * @return scaled value
     */
    private float scaleWidth(float val) {
        assert val <= 1 && val >= 0;

        return val * surfaceFrame.width();
    }

    /**
     * Scales the value in [0, 1] to [0, surface.height()]
     * @param val - value to scale
     * @return scaled height
     */
    private float scaleHeight(float val) {
        assert val <= 1 && val >= 0;

        return val * surfaceFrame.height();
    }
}
