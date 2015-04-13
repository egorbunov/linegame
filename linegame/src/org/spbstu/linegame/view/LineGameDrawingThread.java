package org.spbstu.linegame.view;


import android.content.Context;
import android.graphics.*;
import android.view.SurfaceHolder;

import org.spbstu.linegame.R;
import org.spbstu.linegame.logic.Bonus;
import org.spbstu.linegame.logic.LineGameLogic;
import org.spbstu.linegame.model.curve.Curve;
import org.spbstu.linegame.model.curve.GameCurvePoint;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

class LineGameDrawingThread extends Thread {

    private AtomicBoolean isThreadAlive;

    private final SurfaceHolder surfaceHolder;
    private Rect surfaceFrame;
    private LineGameLogic gameLogic;


    private final int backgroundColor;

    private Paint mainCurvePaint;
    private Paint tappedCurvePaint;

    /**
     * To draw parts of curve with bonus on them, which player can catch if tap the curve
     * on the bonus place I need separate paths.
     */
    private Map<String, Paint> bonusPaintsMap;

    private Path mainPath = new Path();
    private Path tappedPath = new Path();
    private Map<String, Path> bonusPaths = new TreeMap<>();
    private int mainCurveColor;
    private int tappedCurveColor;

    public LineGameDrawingThread(SurfaceHolder surfaceHolder, final Context context) {
        isThreadAlive = new AtomicBoolean(true);

        this.surfaceHolder = surfaceHolder;
        this.surfaceFrame = surfaceHolder.getSurfaceFrame();

        backgroundColor = context.getResources().getColor(R.color.main_background_color);

        for (Bonus b : Bonus.values()) {
            if (!b.equals(Bonus.NONE)) {
                bonusPaths.put(b.toString(), new Path());
            }
        }

        preparePaints(context);
    }

    private void preparePaints(Context context) {
        mainCurvePaint = new Paint();
        mainCurveColor = context.getResources().getColor(R.color.main_line_color);
        mainCurvePaint.setDither(true);
        mainCurvePaint.setStyle(Paint.Style.STROKE);
        mainCurvePaint.setStrokeJoin(Paint.Join.ROUND);
        mainCurvePaint.setStrokeCap(Paint.Cap.SQUARE);
        mainCurvePaint.setColor(mainCurveColor);
        mainCurvePaint.setAntiAlias(true);

        tappedCurvePaint = new Paint();
        tappedCurveColor = context.getResources().getColor(R.color.tapped_line_color);
        tappedCurvePaint.set(mainCurvePaint);
        tappedCurvePaint.setColor(tappedCurveColor);


        bonusPaintsMap = new TreeMap<>();

        Paint doubleThinningBonusPaint = new Paint();
        doubleThinningBonusPaint.set(mainCurvePaint);
        doubleThinningBonusPaint.setColor(context.getResources().getColor(R.color.dec_thickening_bonus_color));

        Paint doubleThickeningBonusPaint = new Paint();
        doubleThickeningBonusPaint.set(mainCurvePaint);
        doubleThickeningBonusPaint.setColor(context.getResources().getColor(R.color.inc_thickening_bonus_color));

        Paint invisibleBonusPaint = new Paint();
        invisibleBonusPaint.set(mainCurvePaint);
        invisibleBonusPaint.setColor(context.getResources().getColor(R.color.invisible_bonus_color));

        Paint impossibleToMissBonusPaint = new Paint();
        impossibleToMissBonusPaint.set(mainCurvePaint);
        impossibleToMissBonusPaint.setColor(context.getResources().getColor(R.color.impossible_to_miss_bonus_color));

        Paint suddenDeathBonusPaint = new Paint();
        suddenDeathBonusPaint.set(mainCurvePaint);
        suddenDeathBonusPaint.setColor(context.getResources().getColor(R.color.sudden_game_over_bonus_color));

        bonusPaintsMap.put(Bonus.DECREASE_THICKENING_SPEED.toString(), doubleThinningBonusPaint);
        bonusPaintsMap.put(Bonus.INCREASE_THICKENING_SPEED.toString(), doubleThickeningBonusPaint);
        bonusPaintsMap.put(Bonus.INVISIBLE_LINE.toString(), invisibleBonusPaint);
        bonusPaintsMap.put(Bonus.SUDDEN_GAME_OVER.toString(), suddenDeathBonusPaint);
        bonusPaintsMap.put(Bonus.IMPOSSIBLE_TO_MISS.toString(), impossibleToMissBonusPaint);
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

        GameCurvePoint prevPoint = null;
        mainPath.reset();
        tappedPath.reset();
        for (Path p : bonusPaths.values()) {
            p.reset();
        }

        float sx, sy; // scaled point coordinates
        for (GameCurvePoint curPoint : curve) {
            sy = scaleHeight(curPoint.getY());
            sx = scaleWidth(curPoint.getX());
            if (prevPoint == null) {
                mainPath.moveTo(sx, sy);
                tappedPath.moveTo(sx, sy);
                for (Path p : bonusPaths.values()) {
                    p.moveTo(sx, sy);
                }
            }
            else {
                mainPath.lineTo(sx, sy);
            }

        	if (curPoint.isTapped()) {
                tappedPath.lineTo(sx, sy);
            } else {
                tappedPath.moveTo(sx, sy);
            }


            for (Bonus b : Bonus.values()) {
                if (!b.equals(Bonus.NONE)) {
                    Path curPath = bonusPaths.get(b.toString());
                    if (curPoint.getBonusType().equals(b)) {
                        curPath.lineTo(sx, sy);
                    } else {
                        curPath.moveTo(sx, sy);
                    }
                }
            }

            prevPoint = curPoint;
        }

        if (gameLogic.getGameConstraints().getInvisibleLineTimer() == 0) {
            if (gameLogic.getGameConstraints().getImpossibleToMissTimer() > 0) {
                mainCurvePaint.setColor(tappedCurveColor);
            } else {
                mainCurvePaint.setColor(mainCurveColor);
            }
            mainCurvePaint.setStrokeWidth(gameLogic.getLineThickness());
            canvas.drawPath(mainPath, mainCurvePaint);

            for (Bonus b : Bonus.values()) {
                if (!b.equals(Bonus.NONE)) {
                    Path curPath = bonusPaths.get(b.toString());
                    Paint paint = bonusPaintsMap.get(b.toString());
                    float ADDITIONAL_BONUS_STROKE_WIDTH = 15f;
                    paint.setStrokeWidth(gameLogic.getLineThickness() + ADDITIONAL_BONUS_STROKE_WIDTH);
                    canvas.drawPath(curPath, paint);
                }
            }

            tappedCurvePaint.setStrokeWidth(gameLogic.getLineThickness());
            canvas.drawPath(tappedPath, tappedCurvePaint);


        }


        // TODO: delete
        /*

        Paint tmp = new Paint();
        tmp.setColor(Color.RED);
        tmp.setStrokeWidth(2);
        RandomCurveParams p = gameLogic.getCurveParams();
        if (p != null) {
            canvas.drawLine(scaleWidth(0.5f - p.curveXBound / 2f), 0.0f, scaleWidth(0.5f - p.curveXBound / 2f), scaleHeight(1.0f), tmp);
            canvas.drawLine(scaleWidth(0.5f + p.curveXBound / 2f), 0.0f, scaleWidth(0.5f + p.curveXBound / 2f), scaleHeight(1.0f), tmp);
            tmp.setColor(Color.GREEN);
            canvas.drawLine(scaleWidth(0.5f - p.maxCornerXValue / 2f), 0.0f, scaleWidth(0.5f - p.maxCornerXValue / 2f), scaleHeight(1.0f), tmp);
            canvas.drawLine(scaleWidth(0.5f + p.maxCornerXValue / 2f), 0.0f, scaleWidth(0.5f + p.maxCornerXValue / 2f), scaleHeight(1.0f), tmp);
        }
        */

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
