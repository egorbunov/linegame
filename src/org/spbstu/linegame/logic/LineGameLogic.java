package org.spbstu.linegame.logic;

import org.spbstu.linegame.model.curve.Curve;
import org.spbstu.linegame.model.curve.StraightLine;
import org.spbstu.linegame.utils.MortalThread;

import java.util.concurrent.atomic.AtomicBoolean;

public class LineGameLogic {
    private final static float STARTING_LINE_WIDTH = 30.0f;
    private final static float MINIMUM_LINE_WIDTH = 5.0f;
    private final static float MAXIMUM_LINE_WIDTH = 150.0f;
    private final static float LINE_WIDTH_DELTA = 1f;
    private final static float STARTING_SPEED = 5f;


    /**
     * singleton thread for thinning line width if no finger on touch screen
     */
    private MortalThread lineThinningThread;
    private static final int LINE_THINNING_THREAD_DELAY = 25;
    private final AtomicBoolean isCurveTapped;

    private float width;
    private float height;

    private Curve currentCurve;
    private float lineThickness;
    private LineGameState gameState;
    private float scrollSpeed;

    public LineGameLogic() {
        width = height = 1f;
        lineThickness = STARTING_LINE_WIDTH;
        currentCurve = new StraightLine();
        scrollSpeed = STARTING_SPEED;

        isCurveTapped = new AtomicBoolean(false);
        lineThinningThread = null;
    }

    public void fieldResize(float width, float height) {
        if (width < 0 || height < 0)
            throw new IllegalArgumentException();
        this.width = width;
        this.height = height;
    }

    public Curve getCurve() {
        return currentCurve;
    }

    public float getLineThickness() {
        return lineThickness;
    }

    public void tapCurve(float x, float y) {
        isCurveTapped.set(true);

        if (currentCurve.tap(x / width, y / height, lineThickness / width))
            increaseLineWidth();
        else
            decreaseLineWidth();
    }

    public void setCurveNotTapped() {
        isCurveTapped.set(false);

        if (lineThinningThread == null) {
            /*
                Running task, which will check if nobody touches the screen and
                if so, call setCurveNotTapped() method (which actually makes line width less)

                Creating and running that task in onTouchEvent(...) method guarantees that at the
                start (just after "NewGame" button pushed) the line stays with it's starting width and
                game is actually starts only after first tap.
             */
            lineThinningThread = new MortalThread() {

                private AtomicBoolean isThreadRunning = new AtomicBoolean(true);

                @Override
                public void kill() {
                    isThreadRunning.set(false);
                }

                @Override
                public void run() {
                    super.run();
                    while (isThreadRunning.get()) {
                        try {
                            Thread.sleep(LINE_THINNING_THREAD_DELAY);
                        } catch (InterruptedException e) {
                            e.printStackTrace(); // TODO: is it ok just to print stack trace?
                        }

                        // next call actually make line thinner (if nobody touches the screen)
                        if (!isCurveTapped.get()) {
                            currentCurve.setNotTapped();
                            decreaseLineWidth();
                        }
                    }
                }
            };

            // starting thinning task
            lineThinningThread.start();
        }

    }

    private void increaseLineWidth() {
        if (lineThickness < MAXIMUM_LINE_WIDTH)
            lineThickness += LINE_WIDTH_DELTA;
    }

    private void decreaseLineWidth() {
        if (lineThickness > MINIMUM_LINE_WIDTH)
            lineThickness -= LINE_WIDTH_DELTA;
    }

    public LineGameState getGameState() {
        return gameState;
    }

    public void setGameState(LineGameState gameState) {
        this.gameState = gameState;
    }

    public float getScrollSpeed() {
        return scrollSpeed;
    }
}
