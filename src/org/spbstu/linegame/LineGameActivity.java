package org.spbstu.linegame;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.spbstu.linegame.logic.LineGameLogic;
import org.spbstu.linegame.logic.LineGameState;
import org.spbstu.linegame.view.LineGameView;
import org.w3c.dom.Text;

public class LineGameActivity extends Activity {
    private LineGameLogic gameLogic;
    private LineGameView gameView;
    private TextView startingTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.linegame_layout);

        gameLogic = new LineGameLogic();
        gameLogic.setGameState(LineGameState.STARTING);

        gameView = (LineGameView) findViewById(R.id.LineGameView);
        gameView.setLogic(gameLogic);

        startingTextView = (TextView) findViewById(R.id.StartingTextView);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.text_pulsing);
        startingTextView.startAnimation(animation);
    }

    @Override
    public boolean onTouchEvent(@NotNull MotionEvent event) {
        if (gameLogic.getGameState().equals(LineGameState.STARTING)) {
            gameLogic.setGameState(LineGameState.RUNNING);

            startingTextView.clearAnimation();
            startingTextView.setVisibility(View.INVISIBLE);
        }

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