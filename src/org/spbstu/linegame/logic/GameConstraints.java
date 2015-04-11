package org.spbstu.linegame.logic;

/**
 * Created by Egor Gorbunov on 31.03.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 *
 * That class is a part of the game logic actually...
 */
class GameConstraints {
    private float width = 1.0f;
    private float height = 1.0f;

    // starting state
    final static float STARTING_LINE_WIDTH = 40.0f;
    final static float GAME_OVER_LINE_WIDTH = 5.0f;
    final static float MAXIMUM_LINE_WIDTH = 130.0f;
    final static float LINE_WIDTH_DELTA = 1f;
    final static float STARTING_CURVE_SPEED = 0.006f;
    final static int SCORE_DELTA = 2;

    final static float STARITNG_X_MAX_SHIFT = 0.04f;

    public float getLineThickness() {
        return lineThickness;
    }

    public void setLineThickness(float lineThickness) {
        this.lineThickness = lineThickness;
    }

    // variables responsible for game hardness
    private float lineThickness;

    public float getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(float scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    /**
     * must be in [0, 1)
     */
    private float scrollSpeed;

    public GameConstraints() {
    }

    public void setSizes(float w, float h) {
        width = w;
        height = h;
    }

    public void incLineThickness() {
        lineThickness += LINE_WIDTH_DELTA;
    }

    public void decLineThickness() {
        lineThickness -= LINE_WIDTH_DELTA;
    }
}
