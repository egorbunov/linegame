package org.spbstu.linegame.model.curve;

import java.util.Iterator;

public class StraightLine extends Curve {
    static final int POINT_NUM = 120;
    private CurvePoint[] points;

    public StraightLine() {
        super();


        points = new CurvePoint[POINT_NUM];
        points[0] = new CurvePoint(WIDTH / 2f, 0f);

        float dy = HEIGHT / (float) POINT_NUM;
        for (int i = 1; i < POINT_NUM - 1; ++i) {
            points[i] = new CurvePoint(points[i - 1].getX(), points[i - 1].getY() + dy);
        }
        points[POINT_NUM - 1] = new CurvePoint(WIDTH / 2f, HEIGHT);
    }


    @Override
    public boolean tap(float x, float y, float curveWidth) {
        super.tap(x, y, curveWidth);

        boolean ans = false;
        for (CurvePoint p : points) {
            if (Math.abs(x - points[0].getX()) < curveWidth / 2f + TAP_TOLERANCE) {
                ans = true;
                p.setTapped();
            } else {
                p.setNotTapped();
            }
        }

        return ans;
    }

    @Override
    public CurvePoint getLastPoint() {
        return points[2];
    }

    @Override
    public void nextFrame(float toSkip) {
        // there is no next frame for straight line, because it's straight
    }


    @Override
    public Iterator<CurvePoint> iterator() {
        return new Iterator<CurvePoint>() {
            private int curIndex = 0;
            @Override
            public boolean hasNext() {
                return curIndex < points.length;
            }

            @Override
            public CurvePoint next() {
                return points[curIndex++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
