package org.spbstu.linegame.logic;

import android.util.Log;
import org.spbstu.linegame.model.curve.RandomCurveParams;

/**
 * Created by Egor Gorbunov on 31.03.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 *
 * That class is a part of the game logic actually. There is declared all
 * variables which responsible for game hardness.
 */
public class GameConstraints {
    // starting state
    public final static float STARTING_LINE_WIDTH = 40.0f;
    public final static float GAME_OVER_LINE_WIDTH = 10.0f;
    public final static float MAXIMUM_LINE_WIDTH = 100.0f;
    public final static float LINE_WIDTH_DELTA = 1f;
    public final static float LINE_THINNING_SPEED_DELTA = 0.1f;
    public final static float STARTING_CURVE_SPEED = 0.006f;
    public final static float CURVE_SPEED_DELTA = 0.0004f;

    public static final float STARTING_CURVE_Y_BOUND = 0.5f;
    public static final float CURVE_Y_BOUND_DELTA = 0.035f;
    public static final float STARTING_CURVE_X_BOUND = 0.1f;
    public static final float CURVE_X_BOUND_DELTA = 0.075f;
    public static final float INITIAL_BONUS_PROB = 0.25f;

    public static final int MAX_BONUS_POINT_NUM = 20;
    public static final int MIN_BONUS_POINT_NUM = 5;

    public static final int IMPOSSIBLE_TO_MISS_TIMER_DELTA = 20;

    /**
     * minimal distance between ordinates of successive points
     */
    public static final float MINIMAL_Y_DISTANCE = 0.00025f;
    private static final int INVISIBLE_LINE_TIMER_DELTA = 10;
    public static final int GAME_HARDNESS_DIST_STEP = 2;


    // variables responsible for game hardness
    /**
     * Thickness of the game curve
     * in [GAME_OVER_LINE_WIDTH, MAXIMUM_LINE_WIDTH];
     * less --> harder
     */
    private float lineThickness = STARTING_LINE_WIDTH;
    /**
     * Portion of the screen, which is skipped every drawing step
     * in [0, 1];
     * greater --> harder
     */
    private float scrollSpeed = STARTING_CURVE_SPEED;  // in [0, 1]

    private RandomCurveParams randomCurveParams = new RandomCurveParams(STARTING_CURVE_X_BOUND, STARTING_CURVE_Y_BOUND);

    /**
     * Probability of generating a bonus (any bonus).
     * 1 - bonusProbability = P{no bonus generated} =)
     */
    private float bonusProbability = INITIAL_BONUS_PROB;

    /**
     * Amounts with that line width is decremented and incremented
     */
    private float lineThinningSpeed = LINE_WIDTH_DELTA;
    private float lineThickeningSpeed = LINE_WIDTH_DELTA;

    /**
     * If it's value is N --> next N frames user can touch any part of the screen to tap line
     */
    private int impossibleToMissTimer = 0;
    private int invisibleLineTimer = 0;

    public GameConstraints() {}

    public void incImpossibleToMissTimer() {
        impossibleToMissTimer += IMPOSSIBLE_TO_MISS_TIMER_DELTA;
    }

    public void decImpossibleToMissTimer() {
        if (impossibleToMissTimer > 0)
            impossibleToMissTimer -= 1;
    }

    public void incLineThinningSpeed() {
        lineThinningSpeed += LINE_THINNING_SPEED_DELTA;
    }

    public void decLineThinningSpeed() {
        if (lineThinningSpeed - LINE_THINNING_SPEED_DELTA > 0)
            lineThinningSpeed -= LINE_THINNING_SPEED_DELTA;
    }

    public void incLineThickeningSpeed() {
        lineThickeningSpeed += LINE_THINNING_SPEED_DELTA;
    }

    public void decLineThickeningSpeed() {
        if (lineThickeningSpeed - LINE_THINNING_SPEED_DELTA > 0)
            lineThickeningSpeed -= LINE_THINNING_SPEED_DELTA;
    }


    public void decCurveYBound() {
        if (randomCurveParams.curveYBound - CURVE_Y_BOUND_DELTA >= RandomCurveParams.MIN_CURVE_Y_BOUND)
            randomCurveParams.curveYBound -= CURVE_Y_BOUND_DELTA;
    }

    public void decCurveXBound() {
        if (randomCurveParams.curveXBound - CURVE_X_BOUND_DELTA >= 0)
            randomCurveParams.curveXBound -= CURVE_X_BOUND_DELTA;
    }

    public void incCurveYBound() {
        if (randomCurveParams.curveYBound + CURVE_Y_BOUND_DELTA <= 1.0f)
            randomCurveParams.curveYBound += CURVE_Y_BOUND_DELTA;
    }

    public void incCurveXBound() {
        if (randomCurveParams.curveXBound + CURVE_X_BOUND_DELTA <= RandomCurveParams.MAX_HANDLE_X_BOUND)
            randomCurveParams.curveXBound += CURVE_X_BOUND_DELTA;
        randomCurveParams.maxCornerXValue = randomCurveParams.curveXBound / RandomCurveParams.CORNER_HANDLE_BOUND_RATIO;
    }

    public void incSpeed() {
        scrollSpeed += CURVE_SPEED_DELTA;
    }

    public void decSpeed() {
        scrollSpeed -= CURVE_SPEED_DELTA;
    }

    public float getLineThickness() {
        return lineThickness;
    }

    public void setLineThickness(float lineThickness) {
        this.lineThickness = lineThickness;
    }

    public float getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(float scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public void incLineThickness() {
        lineThickness += lineThickeningSpeed;
    }

    public void decLineThickness() {
        lineThickness -= lineThinningSpeed;
    }

    public float getCurveXBound() {
        return randomCurveParams.curveXBound;
    }

    public float getCurveYBound() {
        return randomCurveParams.curveYBound;
    }

    public RandomCurveParams getRandomCurveParams() {
        return randomCurveParams;
    }

    public float getBonusProbability() {
        return bonusProbability;
    }

    public void setBonusProbability(float bonusProbability) {
        this.bonusProbability = bonusProbability;
    }

    public int getImpossibleToMissTimer() {
        return impossibleToMissTimer;
    }

    public void setImpossibleToMissTimer(int impossibleToMissTimer) {
        this.impossibleToMissTimer = impossibleToMissTimer;
    }

    public int getInvisibleLineTimer() {
        return invisibleLineTimer;
    }

    public void decInvesibleLineTimer() {
        if (invisibleLineTimer > 0) {
            invisibleLineTimer -= 1;
        }
    }

    public void incInvisibleLineTimer() {
        invisibleLineTimer += INVISIBLE_LINE_TIMER_DELTA;
    }
}
