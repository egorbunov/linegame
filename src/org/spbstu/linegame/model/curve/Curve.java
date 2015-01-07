package org.spbstu.linegame.model.curve;

/**
 * That abstract class describes game curve.
 * Class implements Iterable<CurvePoint>, which provides user ability to iterate through points
 * of the curve (that is the sense, which I put into "iterability").
 * Also, because of the game logic, we need to detect, does user tapped the line or not,
 * so every Curve class must answer to that question by implementing contains(...) method.
 *
 * All points of the curve must be in [0, 1]x[0, 1]
 *
 * Also, because of Curve is Iterable, which means some arrangement of Curve points, first point
 * must have 0 y-coordinate and last must have 1 x-coordinate
 */
public abstract class Curve implements Iterable<CurvePoint> {
    protected static final float WIDTH = 1f;
    protected static final float HEIGHT = 1f;
    protected static final float TAP_TOLERANCE = 0.03f;

    protected float tapX;
    protected float tapY;

    public Curve() {
        tapX = tapY = -1f;
    }

    /**
     * indicates, that point (x, y) was tapped, so the curve may be tapped
     * on that coordinate since now
     *
     * @param x - x-coordinate, in [0, 1]
     * @param y - y-coordinate, in [0, 1]
     * @return true, if point lies on line and else false
     */
    public boolean tap(float x, float y, float curveWidth) {
        tapX = x;
        tapY = y;

        return false;
    }

    /**
     * indicates, that curve cannot be tapped (finger is not on the screen)
     */
    public void setNotTapped() {
        tapX = tapY = -1;
    }

    /**
     * That method is used for changing various Curves continuously
     * @return last point of the Curve
     */
    public abstract CurvePoint getLastPoint();

    /**
     * call of that function indicates logically, that curve need to be "continued"
     * @param toSkip - how much of height should be scrolled, value \in [0, 1]
     */
    public abstract void nextFrame(float toSkip);
}
