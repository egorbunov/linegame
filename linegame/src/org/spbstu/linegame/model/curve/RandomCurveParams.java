package org.spbstu.linegame.model.curve;

import org.spbstu.linegame.utils.Point;

import java.util.LinkedList;

/**
 * Created by Egor Gorbunov on 12.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 *
 *
 */
public class RandomCurveParams {
    public final static float MIN_CURVE_Y_BOUND = 0.025f;
    public final static float MAX_CURVE_Y_BOUND = 1.0f;
    public final static float MAX_HANDLE_X_BOUND = 0.90f;
    public final static float MAX_CURVE_Y_BOUND_DELTA = 0.2f;
    public final static float CORNER_HANDLE_BOUND_RATIO = 1.15f;

    /**
     * Every segment of {@link org.spbstu.linegame.model.curve.RandomContinuousCurve} is a Bezier curve with 2 degree
     * We need to sample that curve before rendering. So that variable equal to maximum sampling step.
     *
     * TODO: that variable actually must depend on specific screen resolution.
     */
    public static final float BEZIER2D_INIT_STEP = 0.0110f;



    /**
     * how far can bezier handle    |-----X-----|
     * point be from center line [  <-----|----->  ]
     * in [0, 1];
     * greater --> harder (probably)
     */
    public float curveXBound;
    /**
     * lowest dist (by y-coordinate) btw corner and handle points of 2d-bezier curve be
     * in [0, 1];
     * less --> harder (probably)
     */
    public float curveYBound;
    /**
     * how can bezier corner point abscissa be
     */
    public float maxCornerXValue;
    public float bezier2DStep = BEZIER2D_INIT_STEP;

    public RandomCurveParams(float initCurveXBound, float initCurveYBound) {
        curveXBound = initCurveXBound;
        curveYBound = initCurveYBound;
        maxCornerXValue = curveXBound / MAX_HANDLE_X_BOUND;
    }
}
