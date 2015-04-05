package org.spbstu.linegame.model.curve;

import org.spbstu.linegame.utils.MyMath;
import org.spbstu.linegame.utils.Point;

import java.util.Comparator;

public class CurvePoint extends TapableObject implements Comparable<CurvePoint> {
    public static final Comparator<CurvePoint> ORDINATE_COMPARATOR = new Comparator<CurvePoint>() {
        @Override
        public int compare(CurvePoint a, CurvePoint b) {
            return a.compareTo(b);
        }
    };

    public static final Comparator<CurvePoint> L2_COMPARATOR = new Comparator<CurvePoint>() {
        final float SCALE_FACTOR = 500.0f; // TODO: Think about scale factor here
        @Override
        public int compare(CurvePoint a, CurvePoint b) {
            return (int) (MyMath.distance(a.getPoint(), b.getPoint()) * SCALE_FACTOR);
        }
    };

    final Point point;

    public CurvePoint(float x, float y) {
        super();
        point = new Point(x, y);
    }

    public CurvePoint(Point point) {
        super();
        this.point = point;
    }

    public float getX() {return point.getX();}
    public float getY() {return point.getY();}
    public Point getPoint() {return point; }

    public void setX(float x) { point.setX(x);}
    public void setY(float y) { point.setY(y);}


    @Override
    public int compareTo(CurvePoint another) {
        if (another == null)
            throw new NullPointerException();
        if (this == another)
            return 0;

        return Float.compare(this.getY(), another.getY());
    }
}
