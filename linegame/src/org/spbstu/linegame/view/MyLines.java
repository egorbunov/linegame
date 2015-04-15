package org.spbstu.linegame.view;

/**
 * Created by Egor Gorbunov on 15.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 */
/**
 * I use that structure fir faster rendering without using {@link android.graphics.Path}
 */
public final class MyLines {
    float[] lines = null;
    int pointCapacity = 0;
    int size = 0;
    boolean moved = false;

    public MyLines(int pointNum) {
        pointCapacity = pointNum + 1;
        lines = new float[pointCapacity * 4];
    }

    void addPoint(float x, float y) {
        lines[size++] = x;
        lines[size++] = y;
    }

    void lineTo(float x, float y) {
        addPoint(x, y);
        moved = false;
        moveTo(x, y);
    }

    void moveTo(float x, float y) {
        if (!moved) {
            addPoint(x, y);
            moved = true;
        } else {
            lines[size - 2] = x;
            lines[size - 1] = y;
        }
    }

    public void reset() {
        size = 0;
        moved = false;
    }

    public void resize(int pointNum) {
        reset();
        if (pointNum >= pointCapacity) {
            pointCapacity = pointNum + 1;
            lines = new float[pointCapacity * 4];
        }
    }
}