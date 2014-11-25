package org.spbstu.linegame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

public class MainMenuActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onClick_btnNewGame(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_translate));
        Toast.makeText(this, "You clicked on New Game button!", Toast.LENGTH_LONG).show();
    }

    public void onClick_btnScores(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_translate));
        Toast.makeText(this, "You clicked on Scores button!", Toast.LENGTH_LONG).show();
    }
}
