package org.spbstu.linegame.model.curve;

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

    public RandomCurveParams(float initCurveXBound, float initCurveYBound) {
        curveXBound = initCurveXBound;
        curveYBound = initCurveYBound;
        maxCornerXValue = curveXBound / MAX_HANDLE_X_BOUND;
    }
}
