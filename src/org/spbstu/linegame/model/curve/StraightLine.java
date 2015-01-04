package org.spbstu.linegame.model.curve;

import java.util.Iterator;

public class StraightLine extends Curve {
    private Point[] points;

    public StraightLine() {
        points = new Point[2];
        points[0] = new Point(WIDTH / 2f, 0f);
        points[1] = new Point(WIDTH / 2f, HEIGHT);
    }


    @Override
    public boolean contains(float x, float y, float tolerance) {
        return false;
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<Point>() {
            private int curIndex = 0;
            @Override
            public boolean hasNext() {
                return curIndex < points.length;
            }

            @Override
            public Point next() {
                return points[curIndex++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
