package org.spbstu.linegame.logic;

public interface LogicListener {
    void onGameEnd();
    void onGamePaused();
    void onGameStarted();
    void onDistanceChanged(int newDistance);
    void onGameContinued();
    void onGameInitialized();
}
