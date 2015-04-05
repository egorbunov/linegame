package org.spbstu.linegame.utils;

/**
 * Simple point with float coordinates, \in R^2
 */
public class Point {
    private float x;
    private float y;

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

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void normalize() {
        float dist = (float) Math.sqrt(x * x + y * y);
        if (dist == 0.0f)
            return;
        x = x / dist;
        y = y / dist;
    }
}
