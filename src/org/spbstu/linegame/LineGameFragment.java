package org.spbstu.linegame;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import org.spbstu.linegame.logic.LineGameLogic;
import org.spbstu.linegame.logic.LineGameState;
import org.spbstu.linegame.logic.LogicListener;
import org.spbstu.linegame.view.LineGameView;

public class LineGameFragment extends Fragment implements LogicListener {
    private static final int SCORE_ANIMATION_VALUE = 1000;
    int lastScoreAnimationValue = 0;

    private LineGameLogic gameLogic;
    LineGameView gameView;

    // nested Views
    private TextView startingTextView;
    private TextView scoreValueTextView;
    private TextView scoreTextView;
    private TextView onPauseTextView;

    // Animations
    private Animation scoreAnimation;
    private Animation textPulseAnimation;


    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textPulseAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.text_pulsing);
        scoreAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.text_short_pulsing);

        gameLogic = new LineGameLogic();
        gameLogic.setListener(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment_layout, container, false);

        // Getting nested views...

        startingTextView = (TextView) view.findViewById(R.id.StartingTextView);
        startingTextView.setVisibility(View.GONE);


        scoreTextView = (TextView) view.findViewById(R.id.ScoreLineTextView);
        scoreTextView.setVisibility(View.GONE);

        scoreValueTextView = (TextView) view.findViewById(R.id.ScoreValueTextView);
        scoreValueTextView.setVisibility(View.GONE);

        onPauseTextView = (TextView) view.findViewById(R.id.OnPauseTextView);
        onPauseTextView.setVisibility(View.GONE);

        gameView = (LineGameView)view.findViewById(R.id.LineGameView);

        gameView.setLogic(gameLogic);

        // Setting touch listener
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gameLogic == null)
                    return true;
                if (event.getPointerId(event.getActionIndex()) != 0)
                    return true;

                final int action = event.getActionMasked();

                switch (action) {
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
        });

        gameLogic.initializeGame();

        return view;
    }


    @Override
    public void onResume() {
        if (!gameLogic.getGameState().equals(LineGameState.STARTING))
            textPulseAnimation.startNow();
        super.onResume();
    }

    /**
     * Method pause the game, it it's not starting or already pausing
     * @return true if game was paused and false if game had already been pausing
     */
    public boolean onBackPressed() {
        if (!gameLogic.getGameState().equals(LineGameState.STARTING) &&
                !gameLogic.getGameState().equals(LineGameState.PAUSED)) {
            gameLogic.pauseGame();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!gameLogic.getGameState().equals(LineGameState.STARTING))
            gameLogic.pauseGame();
    }

    @Override
    public void onGameEnd() {

    }

    @Override
    public void onGamePaused() {
        onPauseTextView.startAnimation(textPulseAnimation);
        onPauseTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGameStarted() {
        startingTextView.clearAnimation();
        startingTextView.setVisibility(View.GONE);

        scoreValueTextView.setVisibility(View.VISIBLE);
        scoreTextView.setVisibility(View.VISIBLE);
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

    @Override
    public void onGameContinued() {
        onPauseTextView.clearAnimation();
        onPauseTextView.setVisibility(View.GONE);
    }

    @Override
    public void onGameInitialized() {
        startingTextView.clearAnimation();
        startingTextView.startAnimation(textPulseAnimation); // It's here, because ...
        startingTextView.setVisibility(View.VISIBLE);
    }
}