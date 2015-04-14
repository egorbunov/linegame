package org.spbstu.linegame.utils;

public class MyMath {
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

    public static void normalize(Point p) {
        float dist = (float) Math.sqrt(p.getX() * p.getX() + p.getY() * p.getY());
        if (dist == 0.0f)
            return;
        p.setX(p.getX() / dist);
        p.setX(p.getY() / dist);
    }
}
