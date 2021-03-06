package org.spbstu.linegame.logic;

import android.util.Log;
import org.spbstu.linegame.model.curve.*;
import org.spbstu.linegame.utils.MortalRunnable;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class LineGameLogic implements BonusClickListener {
    private final GameConstraints gameConstraints;
    private final BonusGenerator bonusGenerator;

    /**
     * Listeners will be notified if any important event happens
     */
    private final LinkedList<LogicListener> logicListeners;


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

    //LineGameTask mainGameThreadTask;

    /**
     * singleton runnable for thinning line width if no finger on touch screen
     */
    LineThinningTask lineThinningTask;


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
        PointsCycledArray points = new PointsCycledArray(GameConstraints.POINT_ON_SCREEN_CAPACITY);
        currentCurve = new RandomContinuousCurve(points, currentCurve, gameConstraints.getRandomCurveParams(), bonusGenerator);

        points.addBounsListener(this);

        isGameTapped = false;
        gameState = LineGameState.STARTING;
        passedDistance = 0;

        gameState = LineGameState.RUNNING;
        createThinningThread();

        for (LogicListener listener : logicListeners)
            listener.onGameStarted();

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

    private void finishTheGame() {
        destroyThinningThread();
        gameConstraints.setScrollSpeed(0.0f);
        gameState = LineGameState.FINISHED;
        for (LogicListener listener : logicListeners)
            listener.onGameEnd();
    }

    public LineGameLogic() {
        logicListeners = new LinkedList<>();
        width = height = 1f;
        gameConstraints = new GameConstraints();

        bonusGenerator = new BonusGenerator(gameConstraints.getBonusProbability(),
                GameConstraints.MIN_BONUS_POINT_NUM,
                GameConstraints.MAX_BONUS_POINT_NUM);

    }

    /**
     * Running task, which will check if nobody touches the screen and
     * if so, call setGameNotTapped() method (which actually makes line width less)
     * Creating and running that task in onTouchEvent(...) method guarantees that at the
     * start (just after "NewGame" button pushed) the line stays with it's starting width and
     * game is actually starts only after first tap.
     */
    private void createThinningThread() {
        lineThinningTask = new LineThinningTask(this);
        new Thread(lineThinningTask).start();
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
        return gameConstraints.getLineThickness();
    }

    public GameConstraints getGameConstraints() {
        return gameConstraints;
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

        if (currentCurve.tap(x / width, y / height, gameConstraints.getLineThickness() / width)
                || (gameConstraints.getImpossibleToMissTimer() > 0)) {
            curveTapped();
        }
        else {
            tapMissed();
        }
    }

    /**
     * There all the bad for player stuff happens. More tapMissed() => closer the game over
     *
     * Package local because of line thinning task
     */
    void tapMissed() {
       decreaseLineWidth();
    }

    /**
     * If curve tapped some things happen to player...Some good things and also things, that
     * make the game harder
     */
    private void curveTapped() {
        increaseLineWidth();
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

    private int lastLevel = 0;

    public void nextGameFrame() {
        //Log.d("EGOR speed", String.valueOf(gameConstraints.getScrollSpeed()));

        // A little of bonus processing work:
        // ------------------------------
        gameConstraints.decImpossibleToMissTimer();
        gameConstraints.decInvisibleLineTimer();

        // ------------------------------

        if (!gameState.equals(LineGameState.PAUSED))
            currentCurve.nextFrame(gameConstraints.getScrollSpeed());

        heightPassed += gameConstraints.getScrollSpeed();

        if (heightPassed >= GameConstraints.GAME_DIST_STEP) {
            passedDistance += 1;

            for (LogicListener l : logicListeners) {
                l.onDistanceChanged(passedDistance);
            }

            heightPassed = 0.0f;

            if (passedDistance - lastLevel == GameConstraints.INCREASE_HARDNESS_STEP) {
                lastLevel = passedDistance;
                gameConstraints.incCurveXBound();
                gameConstraints.decCurveYBound();
                gameConstraints.incSpeed();
                gameConstraints.decThinningThreadDelay();

                bonusGenerator.setBonusProbability((bonusGenerator.getBonusProbability() - GameConstraints.PROB_STEP) > 0.0f ?
                        (bonusGenerator.getBonusProbability() - GameConstraints.PROB_STEP) : bonusGenerator.getBonusProbability());

                Log.d("EGOR", "Thin delay = " + gameConstraints.getThinningThreadDelay());

            }

            Log.d("EGOR: ", "CurveXBonud = " + gameConstraints.getRandomCurveParams().curveXBound + "; " +
                    "CurveYBound = " + gameConstraints.getRandomCurveParams().curveYBound);
        }

    }

    private void destroyThinningThread() {
        if (lineThinningTask != null) {
            lineThinningTask.kill();
            lineThinningTask = null; // TODO: it's ok?
        }
    }

    @Override
    public void onBonusClicked(char b) {
        switch (b) {
            case Bonus.IMPOSSIBLE_TO_MISS:
                gameConstraints.incImpossibleToMissTimer();
                break;
            case Bonus.SUDDEN_DEATH:
                finishTheGame();
                break;
            case Bonus.INVISIBLE_LINE:
                gameConstraints.incInvisibleLineTimer();
                break;
            case Bonus.INCREASE_THICKENING_SPEED:
                gameConstraints.incLineThickeningSpeed();
                break;
            case Bonus.DECREASE_THICKENING_SPEED:
                gameConstraints.decLineThickeningSpeed();
                break;
            case Bonus.INCREASE_GAME_SPEED:
                gameConstraints.incSpeed();
                break;
            case Bonus.DECREASE_GAME_SPEED:
                gameConstraints.decSpeed();
                break;
        }
    }

    public int getPassedDistance() {
        return passedDistance;
    }

    public boolean isGameTapped() {
        return isGameTapped;
    }
}
