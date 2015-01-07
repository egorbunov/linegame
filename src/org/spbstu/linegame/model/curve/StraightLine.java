package org.spbstu.linegame.model.curve;

import org.spbstu.linegame.utils.Point;

import java.util.Iterator;

public class StraightLine extends Curve {
    private CurvePoint[] points;

    public StraightLine() {
        super();
        points = new CurvePoint[2];
        // straight line direction is (0, 1) vector
        points[0] = new CurvePoint(new Point(WIDTH / 2f, 0f), new Point(0f, 1f));
        points[1] = new CurvePoint(new Point(WIDTH / 2f, HEIGHT), new Point(0f, 1f));
    }


    @Override
    public boolean tap(float x, float y, float curveWidth) {
        super.tap(x, y, curveWidth);

        if (Math.abs(x - points[0].getX()) < curveWidth / 2f + TAP_TOLERANCE) {
            points[0].setTapped();
            points[1].setTapped();
            return true;
        }
        else {
            points[0].setNotTapped();
            points[1].setNotTapped();
            return false;
        }
    }

    @Override
    public void setNotTapped() {
        super.setNotTapped();

        points[0].setNotTapped();
        points[1].setNotTapped();
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
