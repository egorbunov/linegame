package org.spbstu.linegame;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

/**
 * Created by Egor Gorbunov on 04.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 *
 * That's main activity which starting fragment is game menu fragment {@link MenuFragment}.
 */
public class MainActivity extends FragmentActivity {
    private final String LINE_GAME_FRAGMENT_TAG = "LINE_GAME_FRAGMENT";
    private final String MENU_FRAGMENT_TAG = "MENU_FRAGMENT";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        if (savedInstanceState != null) {
            // in case we're being restored from prev. state
            return;
        }

        MenuFragment menuFragment = new MenuFragment();
        menuFragment.setArguments(getIntent().getExtras());

        // Add menuFragment to layout
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, menuFragment, MENU_FRAGMENT_TAG).commit();
    }

    public void onClick_btnNewGame(View view) {
        LineGameFragment gameFragment = new LineGameFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, gameFragment, LINE_GAME_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onClick_btnScores(View view) {
    }

    public void onClick_btnRules(View view) {
    }

    @Override
    public void onBackPressed() {
        LineGameFragment lineGameFragment = (LineGameFragment) getSupportFragmentManager()
                .findFragmentByTag(LINE_GAME_FRAGMENT_TAG);
        if (lineGameFragment != null && lineGameFragment.isVisible()) {
            if (!lineGameFragment.onBackPressed())
                super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

}
