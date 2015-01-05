package org.spbstu.linegame.model.curve;

public class Point {
    final float x;
    final float y;
    boolean isTapped;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
        this.isTapped = false;
    }

    public float getX() {return x;}
    public float getY() {return y;}

    public void setTapped() {
        isTapped = true;
    }

    public void setNotTapped() {
        isTapped = false;
    }

    public boolean isTapped() {
        return isTapped;
    }

}
