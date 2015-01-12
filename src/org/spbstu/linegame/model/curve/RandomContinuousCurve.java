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
        float y = points.getLast().getY();
        float dy = 0.02f;
        while (lastPoint.getY() < HEIGHT + points.getFirst().getY()) {
            points.addLast(new CurvePoint((float) ((Math.sin(y) + 1.5) * 0.3), y));
            lastPoint = points.getLast();
            y += dy;
        }
        /*final Point xBound = new Point(0.35f, 0.65f); //x - left bound, y - right bound
        final Random random = new Random(System.currentTimeMillis());
        final float eps = 0.1f;

        boolean flag = true;

        CurvePoint lastPoint = points.getLast();
        Point newPoint;
        Point newDirection;

        float angleDelta = 0.2f;
        float targetAngle = (float) (random.nextFloat() * Math.PI / 2 + Math.PI / 4);
        while (lastPoint.getY() < HEIGHT + points.getFirst().getY()) {
            float curAngle = MyMath.angle(lastPoint.getDirection());

            if (Math.abs(targetAngle - curAngle) > eps) {
                newDirection = MyMath.rotate(lastPoint.getDirection(),
                        (curAngle - targetAngle > 0 ? -1f : 1f) * angleDelta);
                newPoint = MyMath.move(lastPoint.getPoint(), newDirection, 0.009f);

                points.addLast(new CurvePoint(newPoint, newDirection));
                lastPoint = points.getLast();
            }

            if (flag && (lastPoint.getX() <= xBound.getX() || lastPoint.getX() >= xBound.getY())) {
                lastPoint.setDirection(new Point(0, 1));
                flag = false;
                if (lastPoint.getX() <= xBound.getX()) {
                    targetAngle -= Math.PI / 2;
                }
                if (lastPoint.getX() >= xBound.getY()) {
                    targetAngle += Math.PI / 2;
                }
            } else {
                flag = true;
                if (Math.abs(targetAngle - curAngle) <= eps) {
                    targetAngle = (float) (random.nextFloat() * Math.PI / 2 + Math.PI / 4);
                }
            }
        }*/
    }

    @Override
    public boolean tap(float x, float y, float curveWidth) {
        super.tap(x, y, curveWidth);
        if (points.size() < 1) // TODO: think about that sometimes that condition is true!
        	return false;
        return points.setTapped(new Point(x, y + points.getFirst().getY()), Curve.TAP_TOLERANCE, curveWidth);
    }

    @Override
    public CurvePoint getLastPoint() {
        CurvePoint res = new CurvePoint(new Point(points.getLast().getX(),
                points.getLast().getY() - points.getFirst().getY()),
                points.getLast().getDirection());
        if (points.getLast().isTapped())
            res.setTapped();
        return res;
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
