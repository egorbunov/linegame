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
    public Curve() {
    }

    /**
     * Checks if given point (x, y) lies on line with specified tolerance -
     * radius.
     */
    public abstract boolean contains(float x, float y, float tolerance);

    public float getWidth() {
        return WIDTH;
    }

    public float getHeight() {
        return HEIGHT;
    }
}
