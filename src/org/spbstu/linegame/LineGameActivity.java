package org.spbstu.linegame;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import org.spbstu.linegame.logic.LineGameLogic;
import org.spbstu.linegame.logic.LineGameState;
import org.spbstu.linegame.logic.LogicListener;
import org.spbstu.linegame.view.LineGameView;

public class LineGameFragment extends Fragment {

    private LineGameLogic gameLogic;
    LineGameView gameView;

    // Views

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.linegame_layout);

        gameLogic = new LineGameLogic();

        gameView = (LineGameView) findViewById(R.id.LineGameView);
        gameView.setLogic(gameLogic);
        gameLogic.setListener(gameView);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (!gameLogic.getGameState().equals(LineGameState.STARTING) &&
                !gameLogic.getGameState().equals(LineGameState.PAUSED)) {
            gameLogic.pauseGame();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!gameLogic.getGameState().equals(LineGameState.STARTING))
            gameLogic.pauseGame();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameLogic == null)
            return true;
        if (event.getPointerId(event.getActionIndex()) != 0)
            return true;

        final int action = event.getActionMasked();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                gameLogic.tapCurve(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                gameLogic.setCurveNotTapped();
                break;
        }
        return true;
    }
}