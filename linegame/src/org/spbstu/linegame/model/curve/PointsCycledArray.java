package org.spbstu.linegame.model.curve;

import android.util.Log;
import org.spbstu.linegame.logic.Bonus;
import org.spbstu.linegame.logic.BonusClickListener;
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
 *
 * Also that class can signal listeners, that bonus was clicked!!! =)
 */
public final class PointsCycledArray implements Iterable<GameCurvePoint> {
    final GameCurvePoint[] points;
    // because array is cycled we need to store end and start
    // start can be > end
    private int start;
    private int end;
    private int elementCount;
    private final int capacity;

    private BonusClickListener bonusClickListener;

    public void addBounsListener(BonusClickListener listener) {
        bonusClickListener = listener;
    }

    public PointsCycledArray(int capacity) {
        bonusClickListener = null;
        this.capacity = capacity;
        if (capacity < 1)
            throw new IllegalArgumentException();

        points = new GameCurvePoint[capacity];
        start = end = 0;
        elementCount = 0;
    }

    public void addLast(GameCurvePoint point) {
        if (point == null) {
            throw new NullPointerException();
        }

        if (size() > 0) {
            // I want all elements to be unique
            if (points[lastElementIndex()].compareTo(point) == 0)
                return;
            // array must be sorted by y-coordinate
            /* TODO: UNCOMMENT!!!!!! */
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

    public void addFirst(GameCurvePoint point) {
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

    public GameCurvePoint deleteFirst() {
        if (start == end)
            return null;
        elementCount -= 1;

        GameCurvePoint toReturn = points[start];
        points[start] = null;
        start += 1;

        if (start >= points.length)
            start = 0;

        return toReturn;
    }

    public void deleteKFirst(int k) {
        if (start + k < points.length) {
            // Arrays.fill(points, start, start + k, null);
            start = start + k;
        } else {
            // Arrays.fill(points, start, points.length, null);
            start = k + start - points.length;
            // Arrays.fill(points, 0, start, null);
        }
        elementCount -= k;
    }

    public GameCurvePoint skipYDist(float distToSkip) {
        int nearest = binarySearch(new GameCurvePoint(0, points[start].getY() + distToSkip),
                GameCurvePoint.ORDINATE_COMPARATOR);

        //Log.d("EGOR.", "Nearest to = ( 0, " + (points[start].getY() + distToSkip) + " ) is : " + points[nearest].toString());

        GameCurvePoint toRet = points[nearest];

        deleteKFirst((nearest < start) ? (points.length - start + nearest) : (nearest - start));


        return toRet;
    }

    public GameCurvePoint getLast() {
        return points[lastElementIndex()];
    }

    public GameCurvePoint getFirst() {
        return points[start];
    }

    /**
     * Performs binary search on cycled array
     * @param toSearch point to search
     * @return index of nearest to given one point in array
     */
    private int binarySearch(GameCurvePoint toSearch, Comparator<GameCurvePoint> comparator) {
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
        return binaryTapSearch(tap, tolerance, curveWidth);
    }

    /**
     * Simple search for points closest to given one
     */
    private boolean linearTapSearch(Point tap, float tolerance, float curveWidth) {
        boolean res = false;
        int r = start < end ? end : points.length;
        for (int cur = start; cur < r; ++cur) {
            if (points[cur] == null) {
                throw new NullPointerException();
            }
            if (MyMath.distance(tap, points[cur].getPoint()) <= tolerance + curveWidth / 3f) {
                if (points[cur].getY() > tap.getY()) {
                    break;
                }
                res = true;
                bonusTapped(points[cur]);
                points[cur].setTapped();
            }
        }
        if (start >= end && !res) {
            for (int cur = 0; cur < end; ++cur) {
                if (MyMath.distance(tap, points[cur].getPoint()) <= tolerance + curveWidth / 3f) {
                    if (points[cur].getY() > tap.getY()) {
                        break;
                    }
                    res = true;
                    bonusTapped(points[cur]);
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
        GameCurvePoint toSearch = new GameCurvePoint(tap);

        // Here is some heuristic applied: I searching for the closest point
        // by Ordinate in array. It fast, but it's not a Euclidean distance
        // (and it cant be so for sorted array). So that means, that if you
        // consider a single tap near sharp part of curve it may not be recognized as
        // a curve hit, but for now It seem to work really good for the dynamically
        // changing curve and for it's properties.
        // TO EGOR FROM THE FUTURE: if that became a problem, I think, it will be okay
        // solution to just scan some fixed number of points near closest by Ordinate.
        // So asymptotic will be kept...
        int nearest = binarySearch(toSearch, GameCurvePoint.ORDINATE_COMPARATOR);
        assert points[nearest] != null;

        boolean result = false;
        int cur = nearest;
        // going to the left (setting all point above nearest and also close to given one to be tapped)
        while (cur >= start && MyMath.distance(tap, points[cur].getPoint()) <= tolerance + curveWidth / 3f) {
            result = true;

            bonusTapped(points[cur]);

            points[cur--].setTapped();

            if (cur < 0 && end < start)
                cur = points.length - 1;
        }
        // going to the right (setting all point below nearest and also close to given one to be tapped)

        /* No longer needed because of not continuous rendering of the point of tap
        cur = nearest;
        while (cur < end && MyMath.distance(tap, points[cur].getPoint()) <= tolerance + curveWidth / 3f) {
            result = true;

            bonusTapped(points[cur]);

            points[cur++].setTapped();

            if (cur >= points.length && end < start)
                cur = 0;
        }
        */

        return result;
    }

    @Override
    public Iterator<GameCurvePoint> iterator() {
        return new Iterator<GameCurvePoint>() {
            private boolean hasNext = size() > 0;
            private int curIndex = start;
            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public GameCurvePoint next() {
                if (!hasNext)
                    throw new UnresolvedAddressException();
                GameCurvePoint toReturn = points[curIndex];
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

    private void bonusTapped(GameCurvePoint point) {
        if (!point.isTapped() && point.getBonusId() != Bonus.NO_BONUS &&
                bonusClickListener != null) {

            bonusClickListener.onBonusClicked(point.getBonusId());
        }
    }


    public int getCapacity() {
        return capacity;
    }

}
