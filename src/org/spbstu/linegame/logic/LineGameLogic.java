package org.spbstu.linegame.logic;

import org.spbstu.linegame.model.curve.Curve;
import org.spbstu.linegame.model.curve.StraightLine;

public class LineGameLogic {
    private final static float STARTING_LINE_WIDTH = 15.0f;
    private final static float MAXIMUM_LINE_WIDTH = 150.0f;

    private Curve currentCurve;
    private float lineThickness;

    public LineGameLogic() {
        lineThickness = STARTING_LINE_WIDTH;
        currentCurve = new StraightLine();
    }

    public Curve getCurve() {
        return currentCurve;
    }

    public float getLineThickness() {
        return lineThickness;
    }
}
