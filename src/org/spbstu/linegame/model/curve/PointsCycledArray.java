package org.spbstu.linegame.model.curve;

import org.spbstu.linegame.utils.MyMath;
import org.spbstu.linegame.utils.Point;

import java.nio.channels.UnresolvedAddressException;
import java.util.Arrays;
import java.util.Comparator;
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
public final class PointsCycledArray implements Iterable<CurvePoint> {
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

        if (size() > 0) {
            // I want all elements to be unique
            if (points[lastElementIndex()].compareTo(point) == 0)
                return;
            // array must be sorted by y-coordinate
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

    public CurvePoint skipYDist(float distToSkip) {
        int nearest = binarySearch(new CurvePoint(0, points[start].getY() + distToSkip),
                CurvePoint.ORDINATE_COMPARATOR);

        //if (points[nearest].getY())
        return null;
    }

    public CurvePoint getLast() {
        return points[lastElementIndex()];
    }

    public CurvePoint getFirst() {
        return points[start];
    }

    /**
     * Performs binary search on cycled array
     * @param toSearch point to search
     * @return index of nearest to given one point in array
     */
    private int binarySearch(CurvePoint toSearch, Comparator<CurvePoint> comparator) {
        int nearest;
        if (start <= end) {
            nearest = Arrays.binarySearch(points, start, end, toSearch, comparator);
            nearest = nearest < 0 ? -(nearest + 1) : nearest;
            if (nearest < start)
                nearest = start;
            if (nearest >= end)
                nearest = lastElementIndex();
        } else {
            int tmp = Arrays.binarySearch(points, start, points.length, toSearch, comparator);
            tmp = tmp < 0 ? -(tmp + 1) : tmp;
            nearest = Arrays.binarySearch(points, 0, end, toSearch, comparator);
            nearest = nearest < 0 ? -(nearest + 1) : nearest;
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
        return nearest;
    }

    /**
     * That method sets all of the Curve points (which stored in array), which
     * are "near" to specified point (tap)
     *
     * @param tap - point to compare array points to
     * @param tolerance - permissible variation
     * @param curveWidth - width of the actual curve on the screen
     * @return true, if at least one point of the curve is "near" given point
     */
    public boolean setTapped(Point tap, float tolerance, float curveWidth) {
        return linearTapSearch(tap, tolerance, curveWidth);
    }

    /**
     * Simple search for points closest to given one
     */
    private boolean linearTapSearch(Point tap, float tolerance, float curveWidth) {
        boolean res = false;
        int r = start < end ? end : points.length;
        for (int cur = start; cur < r; ++cur) {
            if (MyMath.distance(tap, points[cur].getPoint()) <= tolerance + curveWidth / 3f) {
                res = true;
                points[cur].setTapped();
            }
        }
        if (start >= end) {
            for (int cur = 0; cur < end; ++cur) {
                if (MyMath.distance(tap, points[cur].getPoint()) <= tolerance + curveWidth / 3f) {
                    res = true;
                    points[cur].setTapped();
                }
            }
        }
        return res;
    }

    /**
     * Binary search of points close to given one
     *
     * Because that method is based on binary search and number of points
     * in neighbourhood of specified point should be small (not more than 10-20, I'm sure),
     * so the complexity is O(log(2, n)), where n - number of points in the frame (curve)
     *
     * Unf., it doesn't gain a lot.
     */
    private boolean binaryTapSearch(Point tap, float tolerance, float curveWidth) {
        CurvePoint toSearch = new CurvePoint(tap);

        // Here is some heuristic applied: I searching for the closest point
        // by Ordinate in array. It fast, but it's not a Euclidean distance
        // (and it cant be so for sorted array). So that means, that if you
        // consider a single tap near sharp part of curve it may not be recognized as
        // a curve hit, but for now It seem to work really good for the dynamically
        // changing curve and for it's properties.
        // TO EGOR FROM THE FUTURE: if that became a problem, I think, it will be okay
        // solution to just scan some fixed number of points near closest by Ordinate.
        // So asymptotic will be kept...
        int nearest = binarySearch(toSearch, CurvePoint.ORDINATE_COMPARATOR);
        assert points[nearest] != null;

        boolean result = false;
        int cur = nearest;
        // going to the left (setting all point above nearest and also close to given one to be tapped)
        while (cur >= start && MyMath.distance(tap, points[cur].getPoint()) <= tolerance + curveWidth / 3f) {
            result = true;
            points[cur--].setTapped();
            if (cur < 0 && end < start)
                cur = points.length - 1;
        }
        // going to the right (setting all point below nearest and also close to given one to be tapped)
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
