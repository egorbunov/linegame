package org.spbstu.linegame.model.curve;

import org.spbstu.linegame.logic.Bonus;
import org.spbstu.linegame.utils.MyMath;
import org.spbstu.linegame.utils.Point;

import java.util.Comparator;

public class GameCurvePoint extends TapableObject implements Comparable<GameCurvePoint> {
    public static final Comparator<GameCurvePoint> ORDINATE_COMPARATOR = new Comparator<GameCurvePoint>() {
        @Override
        public int compare(GameCurvePoint a, GameCurvePoint b) {
            return a.compareTo(b);
        }
    };

    public static final Comparator<GameCurvePoint> L2_COMPARATOR = new Comparator<GameCurvePoint>() {
        final float SCALE_FACTOR = 500.0f; // TODO: Think about scale factor here
        @Override
        public int compare(GameCurvePoint a, GameCurvePoint b) {
            return (int) (MyMath.distance(a.getPoint(), b.getPoint()) * SCALE_FACTOR);
        }
    };

    final Point point;

    public Bonus getBonusType() {
        return bonusType;
    }

    public void setBonusType(Bonus bonusType) {
        this.bonusType = bonusType;
    }

    Bonus bonusType = Bonus.NONE;

    public GameCurvePoint(float x, float y) {
        super();
        point = new Point(x, y);
    }

    public GameCurvePoint(Point point) {
        super();
        this.point = point;
    }

    public float getX() {return point.getX();}
    public float getY() {return point.getY();}
    public Point getPoint() {return point; }

    public void setX(float x) { point.setX(x);}
    public void setY(float y) { point.setY(y);}


    @Override
    public String toString() {
        return "( " + getX() + ", " + getY() + ")";
    }

    @Override
    public int compareTo(GameCurvePoint another) {
        if (another == null)
            throw new NullPointerException();
        if (this == another)
            return 0;

        return Float.compare(this.getY(), another.getY());
    }
}
