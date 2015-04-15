package org.spbstu.linegame.logic;

import org.spbstu.linegame.utils.MortalRunnable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Egor Gorbunov on 15.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 */
public class LineThinningTask implements MortalRunnable{
    private final AtomicBoolean isThreadRunning = new AtomicBoolean(true);
    private final LineGameLogic logic;

    public LineThinningTask(LineGameLogic logic) {

        this.logic = logic;
    }

    @Override
    public void kill() {
        isThreadRunning.set(false);
    }

    @Override
    public void run() {
        while (isThreadRunning.get()) {
            try {
                Thread.sleep(logic.getGameConstraints().getThinningThreadDelay());
            } catch (InterruptedException e) {
                e.printStackTrace(); // TODO: is it ok just to print stack trace?
            }
            // next call actually make line thinner (if nobody touches the screen)
            if (!logic.isGameTapped() && logic.getGameState().equals(LineGameState.RUNNING)) {
                logic.getCurve().setNotTapped();
                logic.tapMissed();
            }
        }
    }
}
