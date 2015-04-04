package org.spbstu.linegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainMenuActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_menu_layout);
    }

    public void onClick_btnNewGame(View view) {
        Intent intent = new Intent(this, LineGameActivity.class);
        startActivity(intent);
    }

    public void onClick_btnScores(View view) {
    }

    public void onClick_btnRules(View view) {
    }
}
