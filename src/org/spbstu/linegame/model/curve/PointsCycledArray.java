package org.spbstu.linegame.model.curve;

import org.spbstu.linegame.utils.MyMath;
import org.spbstu.linegame.utils.Point;

import java.nio.channels.UnresolvedAddressException;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Cycled array with points. All elements in that array must be sorted for
 * correctness of find() method
 *
 * All elements have indices \in [start, end)
 * Maybe it's strange, but here to cover capacity (max) number of elements the
 * segment will be: [start, end) := [0, 0), so the previous element
 * for 0 is size - 1
 */
public class PointsCycledArray implements Iterable<CurvePoint> {
    private CurvePoint[] points;
    // because array is cycled we need to store end and start
    // start can be > end
    private int start;
    private int end;
    private int elementCount;
    private int capacity;

    public PointsCycledArray(int capacity) {

        this.capacity = capacity;
        if (capacity < 1)
            throw new IllegalArgumentException();

        points = new CurvePoint[capacity];
        start = end = 0;
        elementCount = 0;
    }

    public void addLast(CurvePoint point) {
        if (point == null) {
            throw new NullPointerException();
        }

        // array must be sorted by y-coordinate
        if (size() > 0) {
            if (points[lastElementIndex()].compareTo(point) > 0)
                throw new IllegalArgumentException();
        }

        if (++elementCount > capacity)
            throw new ArrayIndexOutOfBoundsException("Capacity overflow!");

        points[end] = point;
        end += 1;
        if (end == points.length)
            end = 0;
    }

    public void addFirst(CurvePoint point) {
        if (point == null) {
            throw new NullPointerException();
        }

        if (++elementCount > capacity)
            throw new ArrayIndexOutOfBoundsException("Capacity overflow!");

        // array must be sorted by y-coordinate
        if (points[start] != null) {
            if (point.compareTo(points[start]) > 0)
                throw new IllegalArgumentException();
        }

        start -= 1;
        if (start == -1)
            start = points.length - 1;
        points[start] = point;
    }

    public CurvePoint deleteFirst() {
        if (start == end)
            return null;
        elementCount -= 1;

        CurvePoint toReturn = points[start];
        points[start] = null;
        start += 1;

        if (start >= points.length)
            start = 0;

        return toReturn;
    }

    public CurvePoint getLast() {
        return points[lastElementIndex()];
    }

    public CurvePoint getFirst() {
        return points[start];
    }

    /**
     * That method sets all of the Curve points (which stored in array), which
     * are "near" to specified point (tap)
     *
     * Because that method is based on binary search and number of points
     * in neighbourhood of specified point should be small (not more than 10-20, I'm sure),
     * so the complexity is O(log(2, n)), where n - number of points in the frame (curve)
     *
     * @param tap - point to compare array points to
     * @param tolerance - permissible variation
     * @param curveWidth - width of the actual curve on the screen
     * @return true, if at least one point of the curve is "near" given point
     */
    public boolean setTapped(Point tap, float tolerance, float curveWidth) {
        int nearest;
        CurvePoint toSearch = new CurvePoint(tap.getX(), tap.getY());

        // binary search and choosing bound...TODO: try to avoid some if statements maybe
        if (start <= end) {
            nearest = Arrays.binarySearch(points, start, end, toSearch, CurvePoint.ORDINATE_COMPARATOR);
            nearest = -(nearest + 1);
            if (nearest < start)
                nearest = start;
            if (nearest >= end)
                nearest = lastElementIndex();
        } else {
            int tmp = Arrays.binarySearch(points, start, points.length, toSearch, CurvePoint.ORDINATE_COMPARATOR);
            tmp = -(tmp + 1);
            nearest = Arrays.binarySearch(points, 0, end, toSearch, CurvePoint.ORDINATE_COMPARATOR);
            nearest = -(nearest + 1);
            if (tmp < start)
                tmp = start;
            if (tmp >= start && tmp < points.length)
                nearest = tmp;
            else if (nearest < 0) {
                nearest = points.length - 1;
            } else if (nearest >= end) {
                nearest = lastElementIndex();
            }
        }
        assert points[nearest] != null;

        boolean result = false;
        int cur = nearest;
        // going to the left
        while (cur >= start && MyMath.distance(tap, points[cur].getPoint()) <= tolerance + curveWidth / 3f) {
            result = true;
            points[cur--].setTapped();
            if (cur < 0 && end < start)
                cur = points.length - 1;
        }
        // going to the right
        cur = nearest;
        while (cur < end && MyMath.distance(tap, points[cur].getPoint()) <= tolerance + curveWidth / 3f) {
            result = true;
            points[cur++].setTapped();
            if (cur >= points.length && end < start)
                cur = 0;
        }

        return result;
    }

    @Override
    public Iterator<CurvePoint> iterator() {
        return new Iterator<CurvePoint>() {
            private boolean hasNext = size() > 0;
            private int curIndex = start;
            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public CurvePoint next() {
                if (!hasNext)
                    throw new UnresolvedAddressException();
                CurvePoint toReturn = points[curIndex];
                curIndex += 1;
                if (curIndex >= points.length)
                    curIndex = 0;
                if (curIndex == end)
                    hasNext = false;

                return toReturn;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public int size() {
        return elementCount;
    }

    private int lastElementIndex() {
        return end == 0 ? points.length - 1 : end - 1;
    }
}
