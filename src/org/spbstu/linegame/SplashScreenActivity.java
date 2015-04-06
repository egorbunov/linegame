package org.spbstu.linegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Egor Gorbunov on 05.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 */
public class SplashScreenActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.splash_screen_layout);
        super.onCreate(savedInstanceState);
    }

    boolean isGameStarted = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isGameStarted) {
            startGameActivity();
            return true;
        } else {
            return false;
        }
    }

    private void startGameActivity() {
        isGameStarted = true;
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        finish();
    }
}
