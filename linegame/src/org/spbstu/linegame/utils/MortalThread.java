package org.spbstu.linegame.utils;

public abstract class MortalThread extends Thread {
    public MortalThread() {
        super();
    }

    public MortalThread(Runnable runnable) {
        super(runnable);
    }

    public abstract void kill();
}
