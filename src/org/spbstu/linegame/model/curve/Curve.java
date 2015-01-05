package org.spbstu.linegame.model.curve;

/**
 * That abstract class describes game curve.
 * Class implements Iterable<Point>, which provides user ability to iterate through points
 * of the curve (that is the sense, which I put into "iterability").
 * Also, because of the game logic, we need to detect, does user tapped the line or not,
 * so every Curve class must answer to that question by implementing contains(...) method.
 */
public abstract class Curve implements Iterable<Point> {
    protected static final float WIDTH = 1f;
    protected static final float HEIGHT = 1f;
    protected static final float TOLERANCE = 0.05f;

    protected float tapX;
    protected float tapY;

    public Curve() {
        tapX = tapY = -1f;
    }

    public float getWidth() {
        return WIDTH;
    }

    public float getHeight() {
        return HEIGHT;
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
}
