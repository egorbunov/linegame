package org.spbstu.linegame.model.curve;

import android.util.Log;
import org.spbstu.linegame.utils.MyMath;
import org.spbstu.linegame.utils.Point;

import java.util.Iterator;

public class RandomContinuousCurve extends Curve {
    // min dist between ordinates of successive points
    private static final float MINIMAL_Y_DISTANCE = 0.0005f;
    PointsCycledArray points;

    public RandomContinuousCurve() {
        super();
        points = new PointsCycledArray((int) (HEIGHT / MINIMAL_Y_DISTANCE));
        generatePoints(new CurvePoint(new Point(WIDTH / 2, 0), new Point(0f, 1f)));
    }

    public RandomContinuousCurve(CurvePoint startingPoint) {
        super();
        points = new PointsCycledArray((int) (HEIGHT / MINIMAL_Y_DISTANCE));
        generatePoints(startingPoint);
    }


    /**
     * That method is generates new curve points and adds them to
     * the point's list (to the end).
     * @param startingPoint point, which will be added firstly to the
     *                      end of the list
     */
    private void generatePoints(CurvePoint startingPoint) {
        points.addLast(startingPoint);
        generatePoints();
    }

    private void generatePoints() {
        CurvePoint lastPoint = points.getLast();
        while (lastPoint.getY() < HEIGHT + points.getFirst().getY()) {
            points.addLast(new CurvePoint(new Point(lastPoint.getX(), lastPoint.getY() + 0.005f),
                    lastPoint.getDirection()));
            lastPoint = points.getLast();
        }
    }

    @Override
    public boolean tap(float x, float y, float curveWidth) {
        super.tap(x, y, curveWidth);
        if (points.getFirst() == null) {
            Log.e(this.getClass().getName(), "NUUUULUUUUUUULLLL!!! WWWWWIIIIIIII!!!! ");
        }
        return points.setTapped(new Point(x, y + points.getFirst().getY()), Curve.TAP_TOLERANCE, curveWidth);
    }

    @Override
    public void setNotTapped() {
        super.setNotTapped();
    }

    @Override
    public CurvePoint getLastPoint() {
        return null;
    }

    @Override
    public void nextFrame(float toSkip) {
        // deleting skipped points
        CurvePoint startPoint = points.deleteFirst();
        CurvePoint curPoint = points.getFirst();
        CurvePoint prevPoint = startPoint;
        while (curPoint.getY() - startPoint.getY() < toSkip) {
            points.deleteFirst();
            prevPoint = curPoint;
            curPoint = points.getFirst();
        }

        // adding first point if needed
        if (curPoint.getY() - startPoint.getY() != toSkip) {
            float ratio = (startPoint.getY() - prevPoint.getY() + toSkip) / (curPoint.getY() - prevPoint.getY());
            CurvePoint toAdd = new CurvePoint(MyMath.segmentPoint(prevPoint.getPoint(), curPoint.getPoint(), ratio),
                    prevPoint.getDirection());
            if (prevPoint.isTapped())
                toAdd.setTapped();
            points.addFirst(toAdd);
        }

        generatePoints();
    }

    @Override
    public Iterator<CurvePoint> iterator() {
        return new Iterator<CurvePoint>() {
            Iterator<CurvePoint> arrayIterator = points.iterator();
            @Override
            public boolean hasNext() {
                return arrayIterator.hasNext();
            }

            @Override
            public CurvePoint next() {
                CurvePoint notTransformed = arrayIterator.next();
                if (notTransformed == null) {
                    Log.e(this.getClass().getName(), "NULL!!!!!!!!!!! WHY?????????????????????? HOOOORSEEEE!!!!!!!!");
                }
                CurvePoint toReturn = new CurvePoint(new Point(notTransformed.getX(),
                        notTransformed.getY() - points.getFirst().getY()),
                        notTransformed.getDirection());
                if (notTransformed.isTapped())
                    toReturn.setTapped();
                return toReturn;
            }

            @Override
            public void remove() {
                arrayIterator.remove();
            }
        };
    }
}
