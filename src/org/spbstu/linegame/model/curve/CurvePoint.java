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

    // direction here stands for tangent vector, which
    // specifies line-direction
    Point direction;

    public CurvePoint(float x, float y) {
        super();
        point = new Point(x, y);
        direction = new Point(0f, 0f);
    }

    public CurvePoint(Point point, Point direction) {
        super();
        this.point = point;
        this.direction = direction;
    }

    public void setDirection(Point direction) {
        this.direction = direction;
    }
    public Point getDirection() {
        return direction;
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
