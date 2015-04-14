package org.spbstu.linegame.model.curve;

import android.util.Log;
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
    private final BonusGenerator bonusGenerator;

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

    /**
     * Generate 2d Bezier curve points (something like De Casteljau's algorithm)
     * @param p1 first control point
     * @param p2 handle point
     * @param p3 second control point
     */
    private void generateBezier2DCurve(Point p1, Point p2, Point p3) {
        float perimeter = MyMath.distance(p1, p2) + MyMath.distance(p2, p3);

        Log.d("EGOR, PERIMETER=", String.valueOf(perimeter));

        final float T_STEP = params.bezier2DStep / perimeter;

        Log.d("EGOR.", "Point count on curve = " + 1 / T_STEP + "; Points in array = " + points.size() + "; CAPACITY = " + points.getCapacity());

        final float p1p2x = p2.getX() - p1.getX();
        final float p1p2y = p2.getY() - p1.getY();
        final float cx = p3.getX() - 2 * p2.getX() + p1.getX();
        final float cy = p3.getY() - 2 * p2.getY() + p1.getY();
        float ax, ay, abx, aby;

        char b = bonusGenerator.generateRandomBonus();
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
                p.setBonusId(b);
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

    private void generateNextHandlePointSimple(Point out, final Point cornerPoint, float sign, float lastTangent) {
        float x = WIDTH / 2 + sign * (params.curveXBound / 2);
        float y = cornerPoint.getY() + (lastTangent == 0 ? 0 : params.curveXBound / (2 * lastTangent));

        out.setX(x);
        out.setY(y);
    }

    private void generateSecondCornerPointSimple(Point out, final Point handle, float sign) {
        float x = WIDTH / 2;
        float y = handle.getY() + nextFloat(params.curveYBound, params.curveYBound + RandomCurveParams.MAX_CURVE_Y_BOUND_DELTA);

        out.setX(x);
        out.setY(y);
    }

    /**
     * That method is generates new curve points and adds them to
     * the point's list (to the end).
     */
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
            generateNextHandlePointSimple(handlePoint, lastPoint.getPoint(), sign, lastTangent);
            sign = -sign;
            generateSecondCornerPointSimple(secondCorner, handlePoint, sign);

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
        return !(points == null || points.size() < 1 || points.getFirst() == null) // TODO: think about that sometimes that condition is true!

                && points.setTapped(new Point(x, y + points.getFirst().getY()), Curve.TAP_TOLERANCE, curveWidth);
    }

    @Override
    public GameCurvePoint getLastPoint() {
        return points.getLast();
    }

    // TODO: make point skip faster by using binary search
    @Override
    public void nextFrame(float toSkip) {
        // deleting skipped points
        points.skipYDist(toSkip);
        generatePoints();
    }

    @Override
    public Iterator<GameCurvePoint> iterator() {
        return points.iterator();
    }

    @Override
    public float getYShift() {
        return - points.getFirst().getY();
    }
}
