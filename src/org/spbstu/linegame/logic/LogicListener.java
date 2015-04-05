package org.spbstu.linegame.logic;

public interface LogicListener {
    void onGameEnd();
    void onGamePaused();
    void onGameStarted();
    void onScoreChanged(int newScore);
    void onGameContinued();
    void onGameInitialized();
}
