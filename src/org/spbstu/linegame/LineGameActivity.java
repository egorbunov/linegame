package org.spbstu.linegame;

import android.app.Activity;
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

public class LineGameActivity extends Activity implements LogicListener {
    private static final int SCORE_ANIMATION_VALUE = 1000;

    private LineGameLogic gameLogic;
    LineGameView gameView;

    // Views
    private TextView startingTextView;
    private TextView scoreValueTextView;
    private TextView scoreTextView;
    private TextView onPauseTextView;

    // Animations
    private Animation scoreAnimation;
    private Animation textPulseAnimation;
    int lastScoreAnimationValue = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.linegame_layout);

        gameLogic = new LineGameLogic();

        gameView = (LineGameView) findViewById(R.id.LineGameView);
        gameView.setLogic(gameLogic);

        startingTextView = (TextView) findViewById(R.id.StartingTextView);
        startingTextView.setVisibility(View.INVISIBLE);
        textPulseAnimation = AnimationUtils.loadAnimation(this, R.anim.text_pulsing);
        startingTextView.startAnimation(textPulseAnimation);
        startingTextView.setVisibility(View.VISIBLE);

        scoreTextView = (TextView) findViewById(R.id.ScoreLineTextView);
        scoreTextView.setVisibility(View.INVISIBLE);
        scoreValueTextView = (TextView) findViewById(R.id.ScoreValueTextView);
        scoreValueTextView.setVisibility(View.INVISIBLE);
        scoreAnimation = AnimationUtils.loadAnimation(this, R.anim.text_short_pulsing);

        onPauseTextView = (TextView) findViewById(R.id.OnPauseTextView);
        onPauseTextView.setVisibility(View.INVISIBLE);

        gameLogic.setListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (gameLogic.getGameState().equals(LineGameState.PAUSED)) {
            onPauseTextView.startAnimation(textPulseAnimation);
            onPauseTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (!gameLogic.getGameState().equals(LineGameState.STARTING) &&
                !gameLogic.getGameState().equals(LineGameState.PAUSED)) {
            gameLogic.pauseGame();
            onPauseTextView.startAnimation(textPulseAnimation);
            onPauseTextView.setVisibility(View.VISIBLE);
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

    @Override
    public void onGameEnd() {
        gameLogic.destroy();
    }

    @Override
    public void onGameStarted() {
        startingTextView.clearAnimation();
        startingTextView.setVisibility(View.INVISIBLE);
        scoreValueTextView.setVisibility(View.VISIBLE);
        scoreTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGameContinued() {
        onPauseTextView.clearAnimation();
        onPauseTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onScoreChanged(int newScore) {
        if (scoreValueTextView != null) {
            scoreValueTextView.setText(Integer.toString(newScore));
            if (newScore - lastScoreAnimationValue >= SCORE_ANIMATION_VALUE) {
                scoreValueTextView.startAnimation(scoreAnimation);
                lastScoreAnimationValue = newScore;
            }
        }
    }
}