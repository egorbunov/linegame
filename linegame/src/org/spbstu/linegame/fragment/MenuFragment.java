package org.spbstu.linegame.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import org.spbstu.linegame.MainActivity;
import org.spbstu.linegame.R;

/**
 * Created by Egor Gorbunov on 13.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 */
public class MenuFragment extends Fragment {
    TextView bestScoreView = null;

    private MediaPlayer mediaPlayer;
    ImageButton soundSwitchBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment_layout, container, false);
        bestScoreView = (TextView) view.findViewById(R.id.bestScoreTextView);
        soundSwitchBtn = (ImageButton) view.findViewById(R.id.btnSoundSwitch);

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        if (sharedPreferences != null) { // why it could be null?
            bestScoreView.setText(sharedPreferences.getString(MainActivity.BEST_SCORE_KEY, "0"));

            boolean isMuted = Boolean.parseBoolean(sharedPreferences.getString(MainActivity.WAS_MUSIC_MUTED_KEY,
                    Boolean.toString(true)));
            if (isMuted)
                changeIconAndPauseMusic();
            else
                changeIconAndStartMusic();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void updateBestScore() {
        if (bestScoreView != null) {
            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            bestScoreView.setText(sharedPreferences.getString(MainActivity.BEST_SCORE_KEY, "0"));
        }
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    private void changeIconAndPauseMusic() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        soundSwitchBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_sound));
    }

    private void changeIconAndStartMusic() {
        if (!mediaPlayer.isPlaying())
            mediaPlayer.start();
        soundSwitchBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.speaker));
    }

    public void changeIconAndSwitchPlayer() {
        if (mediaPlayer.isPlaying()) {
            changeIconAndPauseMusic();
        } else {
            changeIconAndStartMusic();
        }
    }
}
