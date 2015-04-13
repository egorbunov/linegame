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
import android.widget.ImageView;
import org.spbstu.linegame.fragment.GameFinishedListener;
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
public class MainActivity extends FragmentActivity implements GameFinishedListener {
    public static final String BEST_SCORE_KEY = "best_score";
    private static final String LINE_GAME_FRAGMENT_TAG = "LINE_GAME_FRAGMENT";
    private static final String RULES_FRAGMENT_TAG = "RULES_FRAGMENT";
    private static final String MENU_FRAGMENT_TAG = "MENU_FRAGMENT";

    LineGameFragment lineGameFragment;

    ImageView soundSwitchBtn;

    MediaPlayer mediaPlayer;
    private boolean isMuted = true;

    private RulesFragment rulesFragment;
    private MenuFragment menuFragment;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        if (savedInstanceState != null) {
            // in case we're being restored from prev. state
            return;
        }

        menuFragment = new MenuFragment();
        menuFragment.setArguments(getIntent().getExtras());

        // Add menuFragment to layout
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, menuFragment, MENU_FRAGMENT_TAG).commit();
        lineGameFragment = new LineGameFragment();
        rulesFragment = new RulesFragment();
        lineGameFragment.setOnGameFinishedListener(this);


        mediaPlayer = MediaPlayer.create(this, R.raw.line_game_music);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);

        if (isMuted) {
            mediaPlayer.start();
        }
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

        if (isMuted) {
            mediaPlayer.start();
        }
    }


    public void onClick_btnNewGame(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, lineGameFragment, RULES_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onClick_btnRules(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, rulesFragment, LINE_GAME_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onClick_btnCredits(View view) {
        Intent intent = new Intent(this, SplashScreenActivity.class);
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
        soundSwitchBtn = (ImageButton) view.findViewById(R.id.btnSoundSwitch);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            soundSwitchBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_sound));
            isMuted = false;
        } else {
            mediaPlayer.start();
            soundSwitchBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.speaker));
            isMuted = true;
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

    @Override
    public void gameFinished(int score) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        int bestScore = Integer.valueOf(sharedPreferences.getString(BEST_SCORE_KEY, "0"));

        if (bestScore < score) {
            SharedPreferences.Editor ed = sharedPreferences.edit();
            ed.putString(BEST_SCORE_KEY, String.valueOf(score));
            ed.apply();
        }
    }
}
