package org.spbstu.linegame.logic;

import org.spbstu.linegame.model.curve.Curve;
import org.spbstu.linegame.model.curve.RandomContinuousCurve;
import org.spbstu.linegame.model.curve.StraightLine;
import org.spbstu.linegame.utils.MortalRunnable;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class LineGameLogic {
    final GameConstraints gameConstraints;

    /**
     * Listeners will be notified if any important event happens
     */
    LinkedList<LogicListener> logicListeners;

    /**
     * singleton runnable for thinning line width if no finger on touch screen
     */
    private MortalRunnable lineThinningTask;
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

    /**
     * Game passedDistance is stored in that variable.
     */
    private int passedDistance;

    /**
     * How much height of one screen is already skipped
     */
    private float heightPassed = 0.0f;


    private void resumeGame() {
        createThinningThread();
        gameConstraints.setScrollSpeed(lastScrollSpeed);
        gameState = LineGameState.RUNNING;
        for (LogicListener listener : logicListeners)
            listener.onGameContinued();
    }

    private void increaseLineWidth() {
        if (gameConstraints.getLineThickness() < GameConstraints.MAXIMUM_LINE_WIDTH)
            gameConstraints.incLineThickness();
    }

    public void finishTheGame() {
        destroyThinningThread();
        gameConstraints.setScrollSpeed(0.0f);
        gameState = LineGameState.FINISHED;
        for (LogicListener listener : logicListeners)
            listener.onGameEnd();
    }

    private void decreaseLineWidth() {
        gameConstraints.decLineThickness();
        if (gameConstraints.getLineThickness() <= GameConstraints.GAME_OVER_LINE_WIDTH) {
            finishTheGame();
        }
    }


    /**
     * Method must be called on the very beginning of the game. Game
     * after call is not starting, it's just initialized.
     *
     * Game is actually started only after private method startGame() called from
     * tapCurve() method
     */
    public void initializeGame() {
        gameConstraints.setLineThickness(GameConstraints.STARTING_LINE_WIDTH);
        gameConstraints.setScrollSpeed(GameConstraints.STARTING_CURVE_SPEED);

        currentCurve = new StraightLine();

        isGameTapped = false;
        gameState = LineGameState.STARTING;
        passedDistance = 0;

        for (LogicListener listener : logicListeners)
            listener.onGameInitialized();
    }

    private void startGame() {
        currentCurve = new RandomContinuousCurve(currentCurve);

        isGameTapped = false;
        gameState = LineGameState.STARTING;
        passedDistance = 0;

        gameState = LineGameState.RUNNING;
        createThinningThread();

        for (LogicListener listener : logicListeners)
            listener.onGameStarted();
    }

    public LineGameLogic() {
        logicListeners = new LinkedList<>();
        width = height = 1f;
        gameConstraints = new GameConstraints();

    }

    /**
     * Running task, which will check if nobody touches the screen and
     * if so, call setGameNotTapped() method (which actually makes line width less)
     * Creating and running that task in onTouchEvent(...) method guarantees that at the
     * start (just after "NewGame" button pushed) the line stays with it's starting width and
     * game is actually starts only after first tap.
     */
    private void createThinningThread() {
        lineThinningTask = new MortalRunnable() {

            private AtomicBoolean isThreadRunning = new AtomicBoolean(true);

            @Override
            public void kill() {
                isThreadRunning.set(false);
            }

            @Override
            public void run() {
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
        new Thread(lineThinningTask).start();
    }

    public void fieldResize(float width, float height) {
        if (width < 0 || height < 0)
            throw new IllegalArgumentException();
        this.width = width;
        this.height = height;
        gameConstraints.setSizes(width, height);
    }

    public Curve getCurve() {
        this.nextCurveFrame();
        return currentCurve;
    }

    public float getLineThickness() {
        return gameConstraints.getLineThickness();
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

        if (currentCurve.tap(x / width, y / height, gameConstraints.getLineThickness() / width)) {
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

    }

    public void setGameNotTapped() {
        isGameTapped = false;
    }

    public LineGameState getGameState() {
        return gameState;
    }

    public void setListener(LogicListener newListener) {
        logicListeners.addLast(newListener);
    }

    public void pauseGame() {
        if (!gameState.equals(LineGameState.PAUSED)) {
            destroyThinningThread();
            gameState = LineGameState.PAUSED;
            lastScrollSpeed = gameConstraints.getScrollSpeed();
            gameConstraints.setScrollSpeed(0.0f);

            for (LogicListener listener : logicListeners)
                listener.onGamePaused();
        }
    }

    public void nextCurveFrame() {
        if (!gameState.equals(LineGameState.PAUSED))
            currentCurve.nextFrame(gameConstraints.getScrollSpeed());

        heightPassed += gameConstraints.getScrollSpeed();

        if (heightPassed >= 1.0f) {
            passedDistance += 1;

            for (LogicListener l : logicListeners) {
                l.onDistanceChanged(passedDistance);
            }

            heightPassed = 0.0f;
        }

    }

    private void destroyThinningThread() {
        if (lineThinningTask != null) {
            lineThinningTask.kill();
            lineThinningTask = null; // TODO: it's ok?
        }
    }
}
