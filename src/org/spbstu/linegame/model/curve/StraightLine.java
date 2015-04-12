package org.spbstu.linegame.model.curve;

import org.spbstu.linegame.logic.BonusGenerator;

import java.util.Iterator;

public class StraightLine extends Curve {
    static final int POINT_NUM = 120;
    private GameCurvePoint[] points;

    public StraightLine() {
        super();


        points = new GameCurvePoint[POINT_NUM];
        points[0] = new GameCurvePoint(WIDTH / 2f, 0f);

        float dy = HEIGHT / (float) POINT_NUM;
        for (int i = 1; i < POINT_NUM - 1; ++i) {
            points[i] = new GameCurvePoint(points[i - 1].getX(), points[i - 1].getY() + dy);
        }
        points[POINT_NUM - 1] = new GameCurvePoint(WIDTH / 2f, HEIGHT);
    }


    @Override
    public boolean tap(float x, float y, float curveWidth) {
        super.tap(x, y, curveWidth);

        boolean ans = false;
        for (GameCurvePoint p : points) {
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
    public GameCurvePoint getLastPoint() {
        return points[2];
    }

    @Override
    public void nextFrame(float toSkip) {
        // there is no next frame for straight line, because it's straight
    }


    @Override
    public Iterator<GameCurvePoint> iterator() {
        return new Iterator<GameCurvePoint>() {
            private int curIndex = 0;
            @Override
            public boolean hasNext() {
                return curIndex < points.length;
            }

            @Override
            public GameCurvePoint next() {
                return points[curIndex++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
