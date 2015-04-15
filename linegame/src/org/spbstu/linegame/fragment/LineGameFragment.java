package org.spbstu.linegame.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import org.spbstu.LogicEventToUiSender;
import org.spbstu.linegame.MainActivity;
import org.spbstu.linegame.R;
import org.spbstu.linegame.logic.LineGameLogic;
import org.spbstu.linegame.logic.LineGameState;
import org.spbstu.linegame.logic.LogicListener;
import org.spbstu.linegame.view.LineGameView;

public class LineGameFragment extends Fragment implements LogicListener {
    private static final int DISTANCE_ANIMATION_VALUE = 10;
    private int lastScoreAnimationValue = 0;

    private LineGameLogic gameLogic;
    private LineGameView gameView;

    // nested Views
    private TextView centeredTextView;
    private TextView scoreValueTextView;
    private TextView scoreTextView;

    // Animations
    private Animation scoreAnimation;
    private Animation textPulseAnimation;
    private Animation gameOverTextAnimation;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textPulseAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.text_pulsing);
        scoreAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.text_short_pulsing);
        gameOverTextAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.text_scale_rotate);

        gameLogic = new LineGameLogic();

        LogicEventToUiSender logicEventToUiSender = new LogicEventToUiSender();
        logicEventToUiSender.setHandlerLogicListener(this);
        gameLogic.setListener(logicEventToUiSender);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment_layout, container, false);

        // Getting nested views...
        centeredTextView = (TextView) view.findViewById(R.id.CenteredText);
        centeredTextView.setVisibility(View.GONE);

        scoreTextView = (TextView) view.findViewById(R.id.ScoreLineTextView);
        scoreTextView.setVisibility(View.GONE);

        scoreValueTextView = (TextView) view.findViewById(R.id.ScoreValueTextView);
        scoreValueTextView.setVisibility(View.GONE);

        gameView = (LineGameView) view.findViewById(R.id.LineGameView);

        gameView.setLogic(gameLogic);

        // Setting touch listener
        gameView.setOnTouchListener(new View.OnTouchListener() {
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
                        gameLogic.setGameNotTapped();
                        break;
                    default:
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
        super.onResume();

        if (!gameLogic.getGameState().equals(LineGameState.STARTING))
            textPulseAnimation.startNow();

        gameView.startDrawingThread();
    }

    /**
     * Method pause the game, it it's not starting or already pausing
     * @return true if game was paused and false if game had already been pausing
     */
    public boolean onBackPressed() {
        if (!gameLogic.getGameState().equals(LineGameState.STARTING) &&
                !gameLogic.getGameState().equals(LineGameState.PAUSED) &&
                !gameLogic.getGameState().equals(LineGameState.FINISHED)) {
            gameLogic.pauseGame();
            return true;
        }
        updateBestScore();


        return false;
    }

    private void updateBestScore() {
        // saving new best score
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        int bestScore = Integer.parseInt(sharedPreferences.getString(MainActivity.BEST_SCORE_KEY, "0"));

        if (bestScore < gameLogic.getPassedDistance()) {
            SharedPreferences.Editor ed = sharedPreferences.edit();
            ed.putString(MainActivity.BEST_SCORE_KEY, String.valueOf(gameLogic.getPassedDistance()));
            ed.apply();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!gameLogic.getGameState().equals(LineGameState.STARTING) &&
                !gameLogic.getGameState().equals(LineGameState.PAUSED) &&
                !gameLogic.getGameState().equals(LineGameState.FINISHED))
            gameLogic.pauseGame();

        gameView.destroyDrawingThread();
    }

    @Override
    public void onGameEnd() {
        centeredTextView.setText(getResources().getString(R.string.game_over_string));
        centeredTextView.setTextColor(getResources().getColor(R.color.game_over_text_color));
        centeredTextView.startAnimation(gameOverTextAnimation);
        centeredTextView.setVisibility(View.VISIBLE);
        gameOverTextAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                gameView.setOnTouchListener(null);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gameView.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        updateBestScore();
                        getActivity().getSupportFragmentManager().popBackStack();
                        return true;
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public void onGamePaused() {
        centeredTextView.setText(getResources().getString(R.string.tap_to_continue_string));
        centeredTextView.setTextColor(getResources().getColor(R.color.pausing_text_color));
        centeredTextView.startAnimation(textPulseAnimation);
        centeredTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGameStarted() {
        centeredTextView.clearAnimation();
        centeredTextView.setVisibility(View.GONE);
        scoreValueTextView.setVisibility(View.VISIBLE);
        scoreTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDistanceChanged(int newDistance) {
        if (scoreValueTextView != null) {
            scoreValueTextView.setText(Integer.toString(newDistance));
            if (newDistance - lastScoreAnimationValue >= DISTANCE_ANIMATION_VALUE) {
                scoreValueTextView.startAnimation(scoreAnimation);
                lastScoreAnimationValue = newDistance;
            }
        }
    }

    @Override
    public void onGameContinued() {
        centeredTextView.clearAnimation();
        centeredTextView.setVisibility(View.GONE);
    }

    @Override
    public void onGameInitialized() {
        centeredTextView.clearAnimation();
        centeredTextView.startAnimation(textPulseAnimation); // It's here, because ...
        centeredTextView.setVisibility(View.VISIBLE);
    }
}