package org.spbstu.linegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

public class MainMenuActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onClick_btnNewGame(View view) {
        Intent intent = new Intent(this, LineGameActivity.class);
        startActivity(intent);
    }

    public void onClick_btnScores(View view) {
        //view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_translate));
    }
}
