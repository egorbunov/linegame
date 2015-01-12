package org.spbstu.linegame.logic;

import org.spbstu.linegame.model.curve.Curve;
import org.spbstu.linegame.model.curve.RandomContinuousCurve;
import org.spbstu.linegame.utils.MortalThread;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class LineGameLogic {
    private final static float STARTING_LINE_WIDTH = 40.0f;
    private final static float MINIMUM_LINE_WIDTH = 5.0f;
    private final static float MAXIMUM_LINE_WIDTH = 130.0f;
    private final static float LINE_WIDTH_DELTA = 1f;
    private final static float STARTING_SPEED = 5f;
    private final static float STARTING_CURVE_SPEED = 0.01f;
    private final static int SCORE_DELTA = 2;

    /**
     * Listeners will be notified if any important event happens
     */
    LinkedList<LogicListener> logicListeners;

    /**
     * singleton thread for thinning line width if no finger on touch screen
     */
    private MortalThread lineThinningThread; // TODO: u need to kill() that thread at some point
    private static final int LINE_THINNING_THREAD_DELAY = 25;

    /**
     * That is a boolean variable., which indicates if the finger is down on the screen now.
     * If not, accordingly to creator's game logic the line must loose it's thickness.
     */
    private boolean isGameTapped;


    private float width; // width of the game surface
    private float height; // height of the game surface


    /**
     * Curve, that currently draw on the game field. That is the main logic object
     */
    private Curve currentCurve;
    private float lineThickness;
    private LineGameState gameState;

    private float scrollSpeed; // maybe that value will increase with time...
    private float lastScrollSpeed;

    /**
     * Game score is stored in that variable.
     */
    private int score;

    private void resumeGame() {
        scrollSpeed = lastScrollSpeed;
        gameState = LineGameState.RUNNING;
        for (LogicListener listener : logicListeners)
            listener.onGameContinued();
    }

    private void increaseLineWidth() {
        if (lineThickness < MAXIMUM_LINE_WIDTH)
            lineThickness += LINE_WIDTH_DELTA;
    }

    private void decreaseLineWidth() {
        if (lineThickness > MINIMUM_LINE_WIDTH)
            lineThickness -= LINE_WIDTH_DELTA;
    }


    public LineGameLogic() {
        logicListeners = new LinkedList<LogicListener>();

        width = height = 1f;
        lineThickness = STARTING_LINE_WIDTH;
        scrollSpeed = STARTING_SPEED;

        currentCurve = new RandomContinuousCurve();

        isGameTapped = false;
        gameState = LineGameState.STARTING;
        score = 0;
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
                    if (!isGameTapped && gameState.equals(LineGameState.RUNNING)) {
                        currentCurve.setNotTapped();
                        decreaseLineWidth();
                    }
                }
            }
        };
        lineThinningThread.start();
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
        isGameTapped = true;

        if (gameState == LineGameState.STARTING) {
            // starting the game!
            gameState = LineGameState.RUNNING;
            for (LogicListener listener : logicListeners)
                listener.onGameStarted();
        }

        if (gameState == LineGameState.PAUSED) {
            // resuming the game!
            resumeGame();
        }

        if (currentCurve.tap(x / width, y / height, lineThickness / width)) {
            increaseLineWidth();

            score += SCORE_DELTA;
            // notifying listeners, that score changed
            for (LogicListener listener : logicListeners)
                listener.onScoreChanged(score);
        }
        else
            decreaseLineWidth();
    }

    public void setCurveNotTapped() {
        isGameTapped = false;
    }

    public LineGameState getGameState() {
        return gameState;
    }

    public float getScrollSpeed() {
        return scrollSpeed;
    }

    public void destroy() {
        if (lineThinningThread != null)
            lineThinningThread.kill();
    }

    public void setListener(LogicListener newListener) {
        logicListeners.addLast(newListener);
    }

    public void pauseGame() {
        gameState = LineGameState.PAUSED;
        lastScrollSpeed = scrollSpeed;
        scrollSpeed = 0;
    }

    public void nextCurveFrame() {
        if (!gameState.equals(LineGameState.PAUSED))
            currentCurve.nextFrame(STARTING_CURVE_SPEED);
    }
}
