package org.spbstu.linegame.logic;

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
    public final static float STARTING_LINE_WIDTH = 35.0f;
    public final static float GAME_OVER_LINE_WIDTH = 10.0f;
    public final static float MAXIMUM_LINE_WIDTH = 90.0f;
    private final static float LINE_WIDTH_DELTA = 0.8f;
    private final static float LINE_THINNING_SPEED_DELTA = 0.1f;
    public final static float STARTING_CURVE_SPEED = 0.01f;
    private final static float CURVE_SPEED_DELTA = 0.0005f;

    private static final float STARTING_CURVE_Y_BOUND = 0.5f;
    private static final float CURVE_Y_BOUND_DELTA = 0.09f;
    private static final float STARTING_CURVE_X_BOUND = 0.1f;
    private static final float CURVE_X_BOUND_DELTA = 0.09f;
    private static final float INITIAL_BONUS_PROB = 0.25f;

    public static final int MAX_BONUS_POINT_NUM = 30;
    public static final int MIN_BONUS_POINT_NUM = 5;

    private static final int IMPOSSIBLE_TO_MISS_TIMER_DELTA = 10;

    /**
     * minimal distance between ordinates of successive points
     */
    public static final int POINT_ON_SCREEN_CAPACITY = 5000;
    private static final int INVISIBLE_LINE_TIMER_DELTA = 10;
    public static final float GAME_DIST_STEP = 1.0f; // in points
    private static final float MIN_CURVE_SPEED = 0.002f;
    public static final int INCREASE_HARDNESS_STEP = 5;
    public static final float PROB_STEP = 0.02f;

    public static final int LINE_THINNING_THREAD_INIT_DELAY = 30;
    public static final int LINE_THINNING_THREAD_DELAY_DELTA = 1;

    public static final int LINE_THINNING_THREAD_DELAY_MIN = 5;

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

    private final RandomCurveParams randomCurveParams = new RandomCurveParams(STARTING_CURVE_X_BOUND, STARTING_CURVE_Y_BOUND);

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
    private long thinningThreadDelay = LINE_THINNING_THREAD_INIT_DELAY;

    public GameConstraints() {}

    public void incImpossibleToMissTimer() {
        impossibleToMissTimer += IMPOSSIBLE_TO_MISS_TIMER_DELTA;
    }

    public void decImpossibleToMissTimer() {
        if (impossibleToMissTimer > 0)
            impossibleToMissTimer -= 1;
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

    public void incCurveXBound() {
        if (randomCurveParams.curveXBound + CURVE_X_BOUND_DELTA <= RandomCurveParams.MAX_HANDLE_X_BOUND)
            randomCurveParams.curveXBound += CURVE_X_BOUND_DELTA;
        randomCurveParams.maxCornerXValue = randomCurveParams.curveXBound / RandomCurveParams.CORNER_HANDLE_BOUND_RATIO;
    }

    public void incSpeed() {
        scrollSpeed += CURVE_SPEED_DELTA;
        /* BAD APPROACH: if (randomCurveParams.bezier2DStep + RandomCurveParams.BEZIER_STEP_DELTA < 0.03)
            randomCurveParams.bezier2DStep += RandomCurveParams.BEZIER_STEP_DELTA;
            */
    }

    public void decSpeed() {
        if (scrollSpeed - CURVE_SPEED_DELTA > MIN_CURVE_SPEED) {
            scrollSpeed -= CURVE_SPEED_DELTA;
        }
        /* BAD APPROACH: if (randomCurveParams.bezier2DStep -  RandomCurveParams.BEZIER_STEP_DELTA > RandomCurveParams.BEZIER2D_INIT_STEP - 0.005)
            randomCurveParams.bezier2DStep -= RandomCurveParams.BEZIER_STEP_DELTA;*/
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

    public RandomCurveParams getRandomCurveParams() {
        return randomCurveParams;
    }

    public float getBonusProbability() {
        return bonusProbability;
    }

    public int getImpossibleToMissTimer() {
        return impossibleToMissTimer;
    }

    public int getInvisibleLineTimer() {
        return invisibleLineTimer;
    }

    public void decInvisibleLineTimer() {
        if (invisibleLineTimer > 0) {
            invisibleLineTimer -= 1;
        }
    }

    public void incInvisibleLineTimer() {
        invisibleLineTimer += INVISIBLE_LINE_TIMER_DELTA;
    }

    public long getThinningThreadDelay() {
        return thinningThreadDelay;
    }

    public void incThinningThreadDelay() {
        thinningThreadDelay += LINE_THINNING_THREAD_DELAY_DELTA;
    }

    public void decThinningThreadDelay() {
        if (thinningThreadDelay - LINE_THINNING_THREAD_DELAY_DELTA > LINE_THINNING_THREAD_DELAY_MIN) {
            thinningThreadDelay -= LINE_THINNING_THREAD_DELAY_DELTA;
        }
    }
}
