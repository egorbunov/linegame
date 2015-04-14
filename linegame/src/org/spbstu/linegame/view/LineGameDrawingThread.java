package org.spbstu.linegame.view;


import android.content.Context;
import android.graphics.*;
import android.view.SurfaceHolder;

import org.spbstu.linegame.R;
import org.spbstu.linegame.logic.Bonus;
import org.spbstu.linegame.logic.LineGameLogic;
import org.spbstu.linegame.model.curve.Curve;
import org.spbstu.linegame.model.curve.GameCurvePoint;

import java.util.concurrent.atomic.AtomicBoolean;

class LineGameDrawingThread extends Thread {
    private boolean isThreadAlive; // TODO: maybe need to be Atomic

    private final SurfaceHolder surfaceHolder;
    private Rect surfaceFrame;
    private LineGameLogic gameLogic;

    private final int backgroundColor;
    private Paint tappedCurvePaint;

    /**
     * To draw parts of curve with bonus on them, which player can catch if tap the curve
     * on the bonus place I need separate paths.
     *
     * Also line with no bonus - it's just a line with it's main color.
     */
    private Paint[] bonusPaintsMap;

    /**
     * All Paths including path for main line but except the tap trace.
     * Now actually main line not drawing fully. It contains of bonus and no-bonus parts.
     */
    private final Path[] bonusPaths;
    private final Path tappedPath = new Path();

    private int mainCurveColor;
    private int tappedCurveColor;

    public LineGameDrawingThread(SurfaceHolder surfaceHolder, final Context context) {
        isThreadAlive = true;

        this.surfaceHolder = surfaceHolder;
        this.surfaceFrame = surfaceHolder.getSurfaceFrame();

        backgroundColor = context.getResources().getColor(R.color.main_background_color);

        bonusPaths = new Path[Bonus.getBonusNum()];
        for (char b : Bonus.ALL_BONUSES) {
            bonusPaths[b] = new Path();
        }

        preparePaints(context);
    }

    private void preparePaints(Context context) {
        Paint mainCurvePaint = new Paint();
        mainCurveColor = context.getResources().getColor(R.color.main_line_color);
        mainCurvePaint.setDither(true);
        mainCurvePaint.setStyle(Paint.Style.STROKE);
        mainCurvePaint.setStrokeJoin(Paint.Join.ROUND);
        mainCurvePaint.setStrokeCap(Paint.Cap.ROUND);
        mainCurvePaint.setColor(mainCurveColor);
        mainCurvePaint.setAntiAlias(true);

        tappedCurvePaint = new Paint();
        tappedCurveColor = context.getResources().getColor(R.color.tapped_line_color);
        tappedCurvePaint.set(mainCurvePaint);
        tappedCurvePaint.setColor(tappedCurveColor);


        bonusPaintsMap = new Paint[Bonus.getBonusNum()];

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

        Paint increaseGameSpeedPaint = new Paint();
        increaseGameSpeedPaint.set(mainCurvePaint);
        increaseGameSpeedPaint.setColor(context.getResources().getColor(R.color.increase_game_speed_bonus_color));

        Paint decreaseGameSpeedPaint = new Paint();
        decreaseGameSpeedPaint.set(mainCurvePaint);
        decreaseGameSpeedPaint.setColor(context.getResources().getColor(R.color.decrease_game_speed_bonus_color));

        bonusPaintsMap[Bonus.NO_BONUS] = mainCurvePaint;
        bonusPaintsMap[Bonus.DECREASE_THICKENING_SPEED] =  doubleThinningBonusPaint;
        bonusPaintsMap[Bonus.INCREASE_THICKENING_SPEED] =  doubleThickeningBonusPaint;
        bonusPaintsMap[Bonus.INVISIBLE_LINE] =  invisibleBonusPaint;
        bonusPaintsMap[Bonus.SUDDEN_DEATH] =  suddenDeathBonusPaint;
        bonusPaintsMap[Bonus.IMPOSSIBLE_TO_MISS] =  impossibleToMissBonusPaint;
        bonusPaintsMap[Bonus.INCREASE_GAME_SPEED] =  increaseGameSpeedPaint;
        bonusPaintsMap[Bonus.DECREASE_GAME_SPEED] =  decreaseGameSpeedPaint;
    }

    public void kill() {
        isThreadAlive = false;
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

        while(isThreadAlive) {
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

        gameLogic.nextGameFrame();


        Curve curve = gameLogic.getCurve();

        GameCurvePoint prevPoint = null;
        tappedPath.reset();
        for (Path p : bonusPaths) {
            p.rewind();
        }

        float shiftY = curve.getYShift();

        float sx, sy; // scaled point coordinates
        for (GameCurvePoint curPoint : curve) {
            sy = scaleHeight(curPoint.getY() + shiftY);
            sx = scaleWidth(curPoint.getX());
            if (prevPoint == null) {
                tappedPath.moveTo(sx, sy);
                for (Path p : bonusPaths) {
                    p.moveTo(sx, sy);
                }
            }

        	if (curPoint.isTapped()) {
                tappedPath.lineTo(sx, sy);
            } else {
                tappedPath.moveTo(sx, sy);
            }

            for (char b : Bonus.ALL_BONUSES) {
                Path curPath = bonusPaths[b];
                if (curPoint.getBonusId() == b) {
                    curPath.lineTo(sx, sy);
                } else {
                    curPath.moveTo(sx, sy);
                }
            }

            prevPoint = curPoint;
        }

        if (gameLogic.getGameConstraints().getInvisibleLineTimer() == 0) {
            if (gameLogic.getGameConstraints().getImpossibleToMissTimer() > 0) {
                bonusPaintsMap[Bonus.NO_BONUS].setColor(tappedCurveColor);
            } else {
                bonusPaintsMap[Bonus.NO_BONUS].setColor(mainCurveColor);
            }

            for (char b : Bonus.ALL_BONUSES) {
                Path curPath = bonusPaths[b];
                Paint paint = bonusPaintsMap[b];
                paint.setStrokeWidth(gameLogic.getLineThickness());
                canvas.drawPath(curPath, paint);
            }

            tappedCurvePaint.setStrokeWidth(gameLogic.getLineThickness());
            canvas.drawPath(tappedPath, tappedCurvePaint);
        }

        /*
        TODO: delete
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
