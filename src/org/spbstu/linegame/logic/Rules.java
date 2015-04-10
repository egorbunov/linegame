package org.spbstu.linegame.logic;

/**
 * Created by Egor Gorbunov on 31.03.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 *
 * That class is a part of the game logic actually...
 */
class Rules {
    private float width = 1.0f;
    private float height = 1.0f;

    // starting state
    final static float STARTING_LINE_WIDTH = 40.0f;
    final static float MINIMUM_LINE_WIDTH = 5.0f;
    final static float MAXIMUM_LINE_WIDTH = 130.0f;
    final static float LINE_WIDTH_DELTA = 1f;
    final static float STARTING_CURVE_SPEED = 0.006f;
    final static int SCORE_DELTA = 2;
    final static float STARITNG_X_MAX_SHIFT = 0.04f;

    public Rules() {
    }

    public void setSizes(float w, float h) {
        width = w;
        height = h;
    }





}
