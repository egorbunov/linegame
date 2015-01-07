package org.spbstu.linegame.utils;

/**
 * Simple point with float coordinates, \in R^2
 */
public class Point {
    final private float x;
    final private float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
