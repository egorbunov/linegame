package org.spbstu.linegame.model.curve;

import android.util.Log;
import org.spbstu.linegame.utils.MyMath;
import org.spbstu.linegame.utils.Point;

import java.util.Iterator;
import java.util.Random;

public class RandomContinuousCurve extends Curve {
    /**
     * minimal distance between ordinates of successive points
     */
    private static final float MINIMAL_Y_DISTANCE = 0.0005f;

    /**
     * array of curve points
     */
    private PointsCycledArray points;

    /**
     * To make it random!
     */
    private Random randomizer;

    public RandomContinuousCurve(Curve startingCurve) {
        super();

        if (startingCurve == null)
            throw new NullPointerException();

        randomizer = new Random();
        points = new PointsCycledArray((int) (HEIGHT / MINIMAL_Y_DISTANCE));

        for (CurvePoint p : startingCurve) {
            points.addLast(p);
        }

        nextFrame(0.0f);
    }

    public RandomContinuousCurve() {
        super();
        randomizer = new Random();
        points = new PointsCycledArray((int) (HEIGHT / MINIMAL_Y_DISTANCE));
        generatePoints(new CurvePoint(WIDTH / 2.0f, 0.0f));
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

    /**
     * Generate 2d Bezier curve points (something like De Casteljau's algorithm)
     * @param p1 first control point
     * @param p2 handle point
     * @param p3 second control point
     */
    private void generateBezier2DCurve(Point p1, Point p2, Point p3) {
        final float T_STEP = 0.025f;

        final float p1p2x = p2.getX() - p1.getX();
        final float p1p2y = p2.getY() - p1.getY();
        final float cx = p3.getX() - 2 * p2.getX() + p1.getX();
        final float cy = p3.getY() - 2 * p2.getY() + p1.getY();
        float ax, ay, abx, aby;
        points.addLast(new CurvePoint(p1));
        for (float t = T_STEP; t < 1.0f; t += T_STEP) {
            ax = p1p2x * t + p1.getX();
            ay = p1p2y * t + p1.getY();
            abx = cx * t + p1p2x;
            aby = cy * t + p1p2y;
            points.addLast(new CurvePoint(abx * t + ax, aby * t + ay));
        }
        points.addLast(new CurvePoint(p3));

    }

    private float dirSign = 1.0f;
    private Point lastTangentVec = new Point(0f, 1f);

    private void generatePoints() {
        CurvePoint lastPoint = points.getLast();

        /**
         * Because points y-coordinate is always increasing, overflow is possible (it's will take
         * a lot of time to produce that overflow, but it still possible), so we need to renew y-coordinate
         * sometimes
         */
        final float BIG_FLOAT = Float.MAX_VALUE - 1000f;
        if (points.getFirst().getY() > BIG_FLOAT) {
            float toSub = points.getFirst().getY();
            for (CurvePoint p : points) {
                p.setY(p.getY() - toSub);
            }
        }

        float dir;
        while (lastPoint.getY() < HEIGHT + points.getFirst().getY()) {
            dir = randomizer.nextFloat() / 1.5f + 0.01f;

            Point handlePoint = new Point(lastPoint.getX() + dir * lastTangentVec.getX(),
                    lastPoint.getY() + dir * lastTangentVec.getY());

            float nextCornerX = lastPoint.getX() + 0.1f * dirSign;
            float nextCornerY = handlePoint.getY() + randomizer.nextFloat() / 3f + 0.01f;

            generateBezier2DCurve(lastPoint.getPoint(),
                    handlePoint,
                    new Point(nextCornerX, nextCornerY));

            lastPoint = points.getLast();
            dirSign = -dirSign;

            lastTangentVec = new Point(lastPoint.getX() - handlePoint.getX(), lastPoint.getY() - handlePoint.getY());
            lastTangentVec.normalize();
        }
    }

    @Override
    public boolean tap(float x, float y, float curveWidth) {
        super.tap(x, y, curveWidth);
        if (points.size() < 1 || points.getFirst() == null) // TODO: think about that sometimes that condition is true!
        	return false;
        return points.setTapped(new Point(x, y + points.getFirst().getY()), Curve.TAP_TOLERANCE, curveWidth);
    }

    @Override
    public CurvePoint getLastPoint() {
        return points.getLast();
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
            CurvePoint toAdd = new CurvePoint(MyMath.segmentPoint(prevPoint.getPoint(), curPoint.getPoint(), ratio));
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
                    return null;
                }
                CurvePoint toReturn = new CurvePoint(new Point(notTransformed.getX(),
                        notTransformed.getY() - points.getFirst().getY()));
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
