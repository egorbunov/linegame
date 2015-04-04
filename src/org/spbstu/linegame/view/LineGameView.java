package org.spbstu.linegame.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import org.spbstu.linegame.R;
import org.spbstu.linegame.logic.LineGameLogic;
import org.spbstu.linegame.logic.LogicListener;

public class LineGameView extends SurfaceView implements SurfaceHolder.Callback, LogicListener {
    private static final int SCORE_ANIMATION_VALUE = 1000;

    // Animations
    private Animation scoreAnimation;
    private Animation textPulseAnimation;
    int lastScoreAnimationValue = 0;

    private LineGameDrawingThread gameThread;
    private LineGameLogic gameLogic;
    private final Context context;


    // nested Views
    private TextView startingTextView;
    private TextView scoreValueTextView;
    private TextView scoreTextView;
    private TextView onPauseTextView;

    public LineGameView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        getHolder().addCallback(this);

        gameLogic = null;
        gameThread = null;
        setFocusable(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        textPulseAnimation = AnimationUtils.loadAnimation(context, R.anim.text_pulsing);
        scoreAnimation = AnimationUtils.loadAnimation(context, R.anim.text_short_pulsing);

        startingTextView = (TextView) ((Activity) context).findViewById(R.id.StartingTextView);
        startingTextView.setVisibility(View.INVISIBLE);
        startingTextView.startAnimation(textPulseAnimation);
        startingTextView.setVisibility(View.VISIBLE);

        scoreTextView = (TextView) ((Activity) context).findViewById(R.id.ScoreLineTextView);
        scoreTextView.setVisibility(View.INVISIBLE);
        scoreValueTextView = (TextView) ((Activity) context).findViewById(R.id.ScoreValueTextView);
        scoreValueTextView.setVisibility(View.INVISIBLE);

        onPauseTextView = (TextView) ((Activity) context).findViewById(R.id.OnPauseTextView);
        onPauseTextView.setVisibility(View.INVISIBLE);
    }

    public void setLogic(LineGameLogic logic) {
        gameLogic = logic;
        if (gameThread != null)
            gameThread.setGameLogic(logic);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // starting main draw thread
        gameThread = new LineGameDrawingThread(holder, context);
        gameThread.start();
        gameThread.setGameLogic(gameLogic);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        gameThread.resizeSurface(width, height);
        gameLogic.fieldResize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (gameThread != null)
            gameThread.kill();
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
        startingTextView.setVisibility(View.INVISIBLE);
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
        onPauseTextView.setVisibility(View.INVISIBLE);
    }
}
