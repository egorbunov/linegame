package org.spbstu.linegame;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import org.spbstu.linegame.fragment.LineGameFragment;

/**
 * Created by Egor Gorbunov on 04.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 * <p>
 * That's main activity whose starting fragment is game menu fragment {@link LineGameFragment.MenuFragment}.
 */
public class MainActivity extends FragmentActivity {
    private static final String LINE_GAME_FRAGMENT_TAG = "LINE_GAME_FRAGMENT";
    private static final String MENU_FRAGMENT_TAG = "MENU_FRAGMENT";

    LineGameFragment lineGameFragment;
    MediaPlayer mediaPlayer;


    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        if (savedInstanceState != null) {
            // in case we're being restored from prev. state
            return;
        }

        LineGameFragment.MenuFragment menuFragment = new LineGameFragment.MenuFragment();
        menuFragment.setArguments(getIntent().getExtras());

        // Add menuFragment to layout
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, menuFragment, MENU_FRAGMENT_TAG).commit();
        lineGameFragment = new LineGameFragment();

        mediaPlayer = MediaPlayer.create(this, R.raw.line_game_music);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }


    public void onClick_btnNewGame(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, lineGameFragment, LINE_GAME_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
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
