package org.spbstu.linegame;

import android.app.Activity;
import android.os.Bundle;
import org.spbstu.linegame.view.LineGameView;

public class LineGameActivity extends Activity {
    LineGameView view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.linegame_layout);
    }
}