package org.spbstu.linegame.model.curve;

import org.spbstu.linegame.utils.Point;

import java.util.Comparator;

public class CurvePoint extends TapableObject implements Comparable<CurvePoint> {
    public static final Comparator<CurvePoint> ORDINATE_COMPARATOR = new Comparator<CurvePoint>() {
        @Override
        public int compare(CurvePoint a, CurvePoint b) {
            return a.compareTo(b);
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


    @Override
    public int compareTo(CurvePoint another) {
        if (another == null)
            throw new NullPointerException();
        if (this == another)
            return 0;

        return Float.compare(this.getY(), another.getY());
    }
}
