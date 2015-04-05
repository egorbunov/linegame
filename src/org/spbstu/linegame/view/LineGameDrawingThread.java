package org.spbstu.linegame.view;


import android.content.Context;
import android.graphics.*;
import android.view.SurfaceHolder;

import android.widget.TextView;
import org.spbstu.linegame.R;
import org.spbstu.linegame.logic.LineGameLogic;
import org.spbstu.linegame.model.curve.Curve;
import org.spbstu.linegame.model.curve.CurvePoint;
import org.spbstu.svg.PathParser;

import java.util.concurrent.atomic.AtomicBoolean;

class LineGameDrawingThread extends Thread {
    private AtomicBoolean isThreadAlive;

    private final SurfaceHolder surfaceHolder;
    private Rect surfaceFrame;
    private LineGameLogic gameLogic;


    private final int backgroundColor;

    private final Paint mainCurvePaint;
    private final Paint tappedCurvePaint;

    public LineGameDrawingThread(SurfaceHolder surfaceHolder, final Context context) {
        isThreadAlive = new AtomicBoolean(true);

        this.surfaceHolder = surfaceHolder;
        this.surfaceFrame = surfaceHolder.getSurfaceFrame();

        backgroundColor = context.getResources().getColor(R.color.main_background_color);
        int tappedLineColor = context.getResources().getColor(R.color.tapped_line_color);
        int mainLineColor = context.getResources().getColor(R.color.main_line_color);

        mainCurvePaint = new Paint();

        mainCurvePaint.setDither(true);
        mainCurvePaint.setStyle(Paint.Style.STROKE);
        mainCurvePaint.setStrokeJoin(Paint.Join.ROUND);
        mainCurvePaint.setStrokeCap(Paint.Cap.ROUND);
        mainCurvePaint.setColor(mainLineColor);

        tappedCurvePaint = new Paint();

        tappedCurvePaint.setDither(true);
        tappedCurvePaint.setStyle(Paint.Style.STROKE);
        tappedCurvePaint.setStrokeJoin(Paint.Join.ROUND);
        tappedCurvePaint.setStrokeCap(Paint.Cap.ROUND);
        tappedCurvePaint.setColor(tappedLineColor);

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
                //redrawBackground(canvas);
                drawLogic(canvas);
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            canvas = null;

        }
    }

    /**
     * Drawing line - main object of the view
     * @param canvas - just canvas
     */
    private void drawLogic(Canvas canvas) {
        if (canvas == null)
            return;

        Curve curve = gameLogic.getCurve();

        CurvePoint prevPoint = null;
        Path mainPath = new Path();
        Path tappedPath = new Path();
        for (CurvePoint curPoint : curve) {
            if (prevPoint == null) {
                mainPath.moveTo(scaleWidth(curPoint.getX()), scaleHeight(curPoint.getY()));
                tappedPath.moveTo(scaleWidth(curPoint.getX()), scaleHeight(curPoint.getY()));
            }
            else {
                mainPath.lineTo(scaleWidth(curPoint.getX()), scaleHeight(curPoint.getY()));
            }

        	if (curPoint.isTapped()) {
                tappedPath.lineTo(scaleWidth(curPoint.getX()), scaleHeight(curPoint.getY()));
            } else {
                tappedPath.moveTo(scaleWidth(curPoint.getX()), scaleHeight(curPoint.getY()));
            }

            prevPoint = curPoint;
        }
        mainCurvePaint.setStrokeWidth(gameLogic.getLineThickness());
        canvas.drawPath(mainPath, mainCurvePaint);

        tappedCurvePaint.setStrokeWidth(gameLogic.getLineThickness());
        canvas.drawPath(tappedPath, tappedCurvePaint);
    }

    /**
     * Scales the value in [0, 1] to [0, surface.width()]
     * @param val - value to scale
     * @return scaled value
     */
    private float scaleWidth(float val) {
        return val * surfaceFrame.width();
    }

    /**
     * Scales the value in [0, 1] to [0, surface.height()]
     * @param val - value to scale
     * @return scaled height
     */
    private float scaleHeight(float val) {
        return val * surfaceFrame.height();
    }
}
