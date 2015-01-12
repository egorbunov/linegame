package org.spbstu.linegame.utils;

public class MyMath {
    /**
     * Rotates direction around (0, 0) by specified angle
     * @param phi - angle in radians
     */
    public static Point rotate(Point direction, float phi) {
        float sin = (float) Math.sin(phi);
        float cos = (float) Math.cos(phi);
        return new Point(direction.getX() * cos - direction.getY() * sin,
                direction.getX() * sin + direction.getY() * cos);
    }

    /**
     * Moves given point along the direction by length = length(direction) * coefficient
     */
    public static Point move(Point point, Point direction, float coef) {
        return new Point(point.getX() + direction.getX() * coef,
                point.getY() + direction.getY() * coef);
    }

    /**
     * Euclidean distance btw 2d points
     */
    public static float distance(Point x, Point y) {
        return (float) Math.sqrt((x.getY() - y.getY()) * (x.getY() - y.getY()) +
                (x.getX() - y.getX()) * (x.getX() - y.getX()));
    }

    /**
     * Calculates point P on the line segment (x, y) with specified
     * ratio of (x, P) segment length to the whole segment (x, y) length
     */
    public static Point segmentPoint(Point x, Point y, float ratio) {
        return new Point(x.getX() + (y.getX() - x.getX()) * ratio,
                x.getY() + (y.getY() - x.getY()) * ratio);
    }

    /**
     * Calculates an angle in radians of given vector, [-pi, pi]
     */
    public static float angle(Point direction) {
        return (float) Math.atan2(direction.getY(), direction.getX());
    }
}
