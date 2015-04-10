package org.spbstu.linegame.logic;

import org.spbstu.linegame.model.curve.Curve;
import org.spbstu.linegame.model.curve.RandomContinuousCurve;
import org.spbstu.linegame.model.curve.StraightLine;
import org.spbstu.linegame.utils.MortalThread;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class LineGameLogic {
    final Rules rules;

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
    private LineGameState gameState;
    private float lastScrollSpeed; // I use that variable to correctly pause the game


    // variables responsible for game hardness
    private float lineThickness;
    /**
     * must be in [0, HEIGHT)
     */
    private float scrollSpeed;

    /**
     * Game score is stored in that variable.
     */
    private int score;

    private void resumeGame() {
        createThinningThread();
        scrollSpeed = lastScrollSpeed;
        gameState = LineGameState.RUNNING;
        for (LogicListener listener : logicListeners)
            listener.onGameContinued();
    }

    private void increaseLineWidth() {
        if (lineThickness < Rules.MAXIMUM_LINE_WIDTH)
            lineThickness += Rules.LINE_WIDTH_DELTA;
    }

    private void decreaseLineWidth() {
        if (lineThickness > Rules.MINIMUM_LINE_WIDTH)
            lineThickness -= Rules.LINE_WIDTH_DELTA;
    }


    /**
     * Method must be called on the very beginning of the game. Game
     * after call is not starting, it's just initialized.
     *
     * Game is actually started only after private method startGame() called from
     * tapCurve() method
     */
    public void initializeGame() {
        lineThickness = Rules.STARTING_LINE_WIDTH;
        scrollSpeed = Rules.STARTING_CURVE_SPEED;

        currentCurve = new StraightLine();

        isGameTapped = false;
        gameState = LineGameState.STARTING;
        score = 0;

        for (LogicListener listener : logicListeners)
            listener.onGameInitialized();
    }

    private void startGame() {
        lineThickness = Rules.STARTING_LINE_WIDTH;
        scrollSpeed = Rules.STARTING_CURVE_SPEED;

        currentCurve = new RandomContinuousCurve(currentCurve);

        isGameTapped = false;
        gameState = LineGameState.STARTING;
        score = 0;

        gameState = LineGameState.RUNNING;
        createThinningThread();

        for (LogicListener listener : logicListeners)
            listener.onGameStarted();
    }

    public LineGameLogic() {
        logicListeners = new LinkedList<>();
        width = height = 1f;
        rules = new Rules();

    }

    /**
     * Running task, which will check if nobody touches the screen and
     * if so, call setCurveNotTapped() method (which actually makes line width less)
     * Creating and running that task in onTouchEvent(...) method guarantees that at the
     * start (just after "NewGame" button pushed) the line stays with it's starting width and
     * game is actually starts only after first tap.
     */
    private void createThinningThread() {
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
                        tapMissed();
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
        rules.setSizes(width, height);
    }

    public Curve getCurve() {
        this.nextCurveFrame();
        return currentCurve;
    }

    public float getLineThickness() {
        return lineThickness;
    }

    public void tapCurve(float x, float y) {
        isGameTapped = true;

        if (gameState == LineGameState.STARTING) {
            // starting the game!
            startGame();
        }

        if (gameState == LineGameState.PAUSED) {
            // resuming the game!
            resumeGame();
        }

        if (currentCurve.tap(x / width, y / height, lineThickness / width)) {
            curveTapped();
        }
        else
            tapMissed();
    }

    /**
     * There all the bad for player stuff happens. More tapMissed() => closer the game over
     */
    private void tapMissed() {
        decreaseLineWidth();
    }

    /**
     * If curve tapped some things happen to player...Some good things and also things, that
     * make the game harder
     */
    private void curveTapped() {
        increaseLineWidth();
        increaseScore();
    }

    private void increaseScore() {
        score += Rules.SCORE_DELTA;
        // notifying listeners, that score changed
        for (LogicListener listener : logicListeners)
            listener.onScoreChanged(score);
    }

    public void setCurveNotTapped() {
        isGameTapped = false;
    }

    public LineGameState getGameState() {
        return gameState;
    }

    public void destroy() {
        destroyThinningThread();
    }

    public void setListener(LogicListener newListener) {
        logicListeners.addLast(newListener);
    }

    public void pauseGame() {
        if (!gameState.equals(LineGameState.PAUSED)) {
            destroyThinningThread();
            gameState = LineGameState.PAUSED;
            lastScrollSpeed = scrollSpeed;
            scrollSpeed = 0;

            for (LogicListener listener : logicListeners)
                listener.onGamePaused();
        }
    }

    public void nextCurveFrame() {
        if (!gameState.equals(LineGameState.PAUSED))
            currentCurve.nextFrame(scrollSpeed);
    }

    private void destroyThinningThread() {
        if (lineThinningThread != null) {
            lineThinningThread.kill();
            lineThinningThread = null; // TODO: it's ok?
        }
    }
}
