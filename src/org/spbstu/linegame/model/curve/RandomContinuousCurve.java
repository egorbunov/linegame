package org.spbstu.linegame.model.curve;

import android.util.Log;
import org.spbstu.linegame.logic.Bonus;
import org.spbstu.linegame.logic.BonusGenerator;
import org.spbstu.linegame.utils.MyMath;
import org.spbstu.linegame.utils.Point;

import java.util.Iterator;
import java.util.Random;

public class RandomContinuousCurve extends Curve {
    /**
     * array of curve points
     */
    private PointsCycledArray points;

    /**
     * To make it random!
     */
    private Random randomizer;
    private RandomCurveParams params;
    private BonusGenerator bonusGenerator;

    public RandomContinuousCurve(PointsCycledArray points, Curve startingCurve, RandomCurveParams params, BonusGenerator bonusGenerator) {
        super();
        this.points = points;
        this.bonusGenerator = bonusGenerator;

        if (params == null)
            throw new NullPointerException();

        this.params = params;

        if (startingCurve == null)
            throw new NullPointerException();

        randomizer = new Random();

        for (GameCurvePoint p : startingCurve) {
            points.addLast(p);
        }

        sign = randomizer.nextFloat() < 0.5f ? -1.0f : 1.0f;
        nextFrame(0.0f);
    }

    public RandomContinuousCurve(PointsCycledArray points, RandomCurveParams params, BonusGenerator bonusGenerator) {
        super();
        this.bonusGenerator = bonusGenerator;

        if (params == null)
            throw new NullPointerException();

        this.params = params;

        randomizer = new Random();
        generatePoints(new GameCurvePoint(WIDTH / 2.0f, 0.0f));
    }

    /**
     * That method is generates new curve points and adds them to
     * the point's list (to the end).
     * @param startingPoint point, which will be added firstly to the
     *                      end of the list
     */
    private void generatePoints(GameCurvePoint startingPoint) {
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
        float perimetr = MyMath.distance(p1, p2) + MyMath.distance(p2, p3);

        // Log.d("EGOR, PERIMETR=", String.valueOf(perimetr));

        final float T_STEP = 0.025f / perimetr;

        final float p1p2x = p2.getX() - p1.getX();
        final float p1p2y = p2.getY() - p1.getY();
        final float cx = p3.getX() - 2 * p2.getX() + p1.getX();
        final float cy = p3.getY() - 2 * p2.getY() + p1.getY();
        float ax, ay, abx, aby;

        Bonus b = bonusGenerator.generateRandomBonus();
        int num = bonusGenerator.getNumOfPointsInBonus();

        // Log.d("EGOR:", "Bonus = " + b.toString() + "; num_p = " + num);

        points.addLast(new GameCurvePoint(p1));
        for (float t = T_STEP; t < 1.0f; t += T_STEP) {
            ax = p1p2x * t + p1.getX();
            ay = p1p2y * t + p1.getY();
            abx = cx * t + p1p2x;
            aby = cy * t + p1p2y;
            GameCurvePoint p = new GameCurvePoint(abx * t + ax, aby * t + ay);
            if (num > 0) {
                p.setBonusType(b);
                num--;
            }
            points.addLast(p);
        }
        points.addLast(new GameCurvePoint(p3));

    }

    private float nextFloat(float l, float r) {
        return randomizer.nextFloat() * (r - l) + l;
    }

    private float sign = 1.0f; // second control point is lefter or righter to handle point
                               // and handle point is lefter or righter to first corner point
                               // actually, that is the sign of tangent
    private float lastTangent = 0.0f;

    private void generateNextHandlePoint(Point out, final Point cornerPoint, float sign, float lastTangent) {
        float ldy = Math.max(params.curveYBound, sign * (WIDTH / 2f - cornerPoint.getX()) / lastTangent);
        float hdy = (params.curveYBound / 2f + sign * (WIDTH / 2 - cornerPoint.getX())) / lastTangent;
        float dx, dy;

        // lastTangent == 0 or too big dy possible (that's bad)
        if (Float.isInfinite(hdy) || Float.isNaN(hdy) || hdy > RandomCurveParams.MAX_CURVE_Y_BOUND) {
            dy = nextFloat(params.curveYBound, params.curveYBound + RandomCurveParams.MAX_CURVE_Y_BOUND_DELTA);
            dx = dy * lastTangent;
        } else if (ldy > hdy) {
            dx = params.curveXBound / 2 + sign * (WIDTH / 2f - cornerPoint.getX());
            dy = dx / lastTangent;
            if (dy > 1.0f) {
                dy = nextFloat(params.curveYBound, params.curveYBound + RandomCurveParams.MAX_CURVE_Y_BOUND_DELTA);
            }
        } else {
            dy = nextFloat(ldy, hdy);
            dx = dy * lastTangent;
        }

        out.setX(cornerPoint.getX() + sign * dx);
        out.setY(cornerPoint.getY() + dy);
    }

    private void generateSecondCornerPoint(Point out, final Point handle, float sign) {
        float dx = nextFloat(Math.max(WIDTH / 2f - handle.getX(), 0.0f),
                params.maxCornerXValue / 2 + sign * (WIDTH / 2 - handle.getX()));
        float dy = nextFloat(params.curveYBound, params.curveYBound + RandomCurveParams.MAX_CURVE_Y_BOUND_DELTA);

        out.setX(handle.getX() + sign * dx);
        out.setY(handle.getY() + dy);

        /*
        if (handle.getX() + sign * dx > 1.0f || handle.getX() + sign * dx < 0.0f) {
            Log.d("EGOR:", "Bad dx = " + dx + " in [" + Math.min(WIDTH / 2f - handle.getX(), 0.0f) + ", "
            + (params.maxCornerXValue / 2 + sign * (WIDTH / 2 - handle.getX())) + " ] ");
        }
        */

    }

    private void generatePoints() {
        GameCurvePoint lastPoint = points.getLast();
        /**
         * Because points y-coordinate is always increasing, overflow is possible (it's will take
         * a lot of time to produce that overflow, but it still possible), so we need to renew y-coordinate
         * sometimes
         */
        final float BIG_FLOAT = Float.MAX_VALUE - 1000f;
        if (points.getFirst().getY() > BIG_FLOAT) {
            float toSub = points.getFirst().getY();
            for (GameCurvePoint p : points) {
                p.setY(p.getY() - toSub);
            }
        }

        // generating points below
        Point handlePoint = new Point(0.0f, 0.0f);
        Point secondCorner = new Point(0.0f, 0.0f);
        while (lastPoint.getY() < HEIGHT + points.getFirst().getY()) {
            generateNextHandlePoint(handlePoint, lastPoint.getPoint(), sign, lastTangent);
            sign = -sign;
            generateSecondCornerPoint(secondCorner, handlePoint, sign);

            lastTangent = (Math.abs(secondCorner.getX() - handlePoint.getX()))
                    / (Math.abs(secondCorner.getY() - handlePoint.getY()));


            // Log.d("EGOR: ", lastPoint.getPoint().toString() + " " + handlePoint.toString() + " " + secondCorner.toString());

            generateBezier2DCurve(lastPoint.getPoint(),
                    handlePoint,
                    secondCorner);

            lastPoint = points.getLast();
        }
    }

    @Override
    public boolean tap(float x, float y, float curveWidth) {
        super.tap(x, y, curveWidth);
        if (points == null || points.size() < 1 || points.getFirst() == null) // TODO: think about that sometimes that condition is true!
        	return false;
        return points.setTapped(new Point(x, y + points.getFirst().getY()), Curve.TAP_TOLERANCE, curveWidth);
    }

    @Override
    public GameCurvePoint getLastPoint() {
        return points.getLast();
    }

    // TODO: make point skip faster by using binary search
    @Override
    public void nextFrame(float toSkip) {
        // deleting skipped points
        GameCurvePoint startPoint = points.deleteFirst();
        GameCurvePoint curPoint = points.getFirst();
        GameCurvePoint prevPoint = startPoint;
        while (curPoint.getY() - startPoint.getY() < toSkip) {
            points.deleteFirst();
            prevPoint = curPoint;
            curPoint = points.getFirst();
        }

        // adding first point if needed
        if (curPoint.getY() - startPoint.getY() != toSkip) {
            float ratio = (startPoint.getY() - prevPoint.getY() + toSkip) / (curPoint.getY() - prevPoint.getY());
            GameCurvePoint toAdd = new GameCurvePoint(MyMath.segmentPoint(prevPoint.getPoint(), curPoint.getPoint(), ratio));
            if (prevPoint.isTapped())
                toAdd.setTapped();
            points.addFirst(toAdd);
        }

        generatePoints();
    }

    @Override
    public Iterator<GameCurvePoint> iterator() {
        return new Iterator<GameCurvePoint>() {
            Iterator<GameCurvePoint> arrayIterator = points.iterator();
            @Override
            public boolean hasNext() {
                return arrayIterator.hasNext();
            }

            @Override
            public GameCurvePoint next() {
                GameCurvePoint notTransformed = arrayIterator.next();
                if (notTransformed == null) {
                    Log.e(this.getClass().getName(), "NULL!!!!!!!!!!! WHY?????????????????????? HOOOORSEEEE!!!!!!!!");
                    return null;
                }
                GameCurvePoint toReturn = new GameCurvePoint(new Point(notTransformed.getX(),
                        notTransformed.getY() - points.getFirst().getY()));
                if (notTransformed.isTapped())
                    toReturn.setTapped();
                toReturn.setBonusType(notTransformed.bonusType);
                return toReturn;
            }

            @Override
            public void remove() {
                arrayIterator.remove();
            }
        };
    }
}
