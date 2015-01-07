package org.spbstu.linegame.logic;

public interface LogicListener {
    void onGameEnd();
    void onGameStarted();
    void onScoreChanged(int newScore);
    void onGameContinued();
}
