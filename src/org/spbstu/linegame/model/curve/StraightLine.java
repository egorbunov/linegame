package org.spbstu.linegame.model.curve;

public class StraightLine implements Curve {
    @Override
    public int fun(int x) {
        return 0;
    }

    @Override
    public boolean contains(int x, int y, int tolerance) {
        return Math.abs(y - fun(x)) <= tolerance;
    }
}
