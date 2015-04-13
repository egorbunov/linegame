package org.spbstu.linegame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import com.spbstu.appmathdep.DepSplashScreenActivity;
import org.spbstu.linegame.fragment.LineGameFragment;
import org.spbstu.linegame.fragment.MenuFragment;
import org.spbstu.linegame.fragment.RulesFragment;

/**
 * Created by Egor Gorbunov on 04.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 * <p>
 * That's main activity whose starting fragment is game menu fragment {@link MenuFragment}.
 */
public class MainActivity extends FragmentActivity {
    public static final String BEST_SCORE_KEY = "best_score";
    public static final String WAS_MUSIC_MUTED_KEY = "was_music_muted";

    private static final String LINE_GAME_FRAGMENT_TAG = "LINE_GAME_FRAGMENT";
    private static final String RULES_FRAGMENT_TAG = "RULES_FRAGMENT";
    private static final String MENU_FRAGMENT_TAG = "MENU_FRAGMENT";

    MediaPlayer mediaPlayer;
    boolean isMuted = false;

    private MenuFragment menuFragment;
    private LineGameFragment lineGameFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        if (savedInstanceState != null) {
            return;
        }

        // for music
        mediaPlayer = MediaPlayer.create(this, R.raw.line_game_music);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);

        // Starting with menu fragment
        menuFragment = new MenuFragment();
        menuFragment.setMediaPlayer(mediaPlayer);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, menuFragment, MENU_FRAGMENT_TAG).commit();

        // Creating anther used fragments
        lineGameFragment = new LineGameFragment();


        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        isMuted = Boolean.parseBoolean(sharedPreferences.getString(MainActivity.WAS_MUSIC_MUTED_KEY,
                Boolean.toString(true)));
        if (!isMuted)
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

        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mediaPlayer.isPlaying() && !isMuted) {
            mediaPlayer.start();
        }
    }


    @Override
    public void onBackPressed() {
        if (lineGameFragment != null && lineGameFragment.isVisible()) {
            if (!lineGameFragment.onBackPressed())
                super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }


    // ======== MenuFragments Button Click handling ======= //
    // TODO: maybe it's better to do it with clickListeners on MenuFragment class...

    public void onClick_btnNewGame(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, lineGameFragment, RULES_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onClick_btnRules(View view) {
        RulesFragment rulesFragment = new RulesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, rulesFragment, LINE_GAME_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onClick_btnCredits(View view) {
        Intent intent = new Intent(this, DepSplashScreenActivity.class);
        this.startActivity(intent);
    }

    public void onClick_btnResetScore(View view) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(BEST_SCORE_KEY, String.valueOf(0));
        ed.apply();
        if (menuFragment != null) {
            menuFragment.updateBestScore();
        }
    }

    public void onClick_btnSwitchSound(View view) {
        isMuted = !isMuted;
        if (menuFragment != null) {
            menuFragment.changeIconAndSwitchPlayer();
        }

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(WAS_MUSIC_MUTED_KEY, String.valueOf(isMuted));
        edit.apply();
    }
}
