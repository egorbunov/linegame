package org.spbstu.linegame.logic;

import org.spbstu.linegame.utils.MortalRunnable;

/**
 * Created by Egor Gorbunov on 14.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 */
public class LineGameTask implements MortalRunnable {
    boolean isRunning = true;
    private LineGameLogic logic;

    public LineGameTask(LineGameLogic logic) {

        this.logic = logic;
    }

    @Override
    public void kill() {
        isRunning = false;
        logic = null;
    }

    @Override
    public void run() {
        while (isRunning) {
            logic.nextGameFrame();
        }
    }
}
