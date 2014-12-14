package org.spbstu.linegame.model.curve;


public interface Curve {
    /**
     * Returns value of function, that represents the line at the point
     * @param x - coordinate to evaluate function at
     * @return y coordinate of point, so (x, y) lies on line
     */
    int fun(int x);

    /**
     * Checks if given point (x, y) lies on line with specified tolerance -
     * radius.
     */
    boolean contains(int x, int y, int tolerance);
}
