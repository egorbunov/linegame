package org.spbstu.linegame.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment_layout, container, false);
        bestScoreView = (TextView) view.findViewById(R.id.bestScoreTextView);

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        if (sharedPreferences != null)
            bestScoreView.setText(sharedPreferences.getString(MainActivity.BEST_SCORE_KEY, "0"));

        return view;
    }

    public void updateBestScore() {
        if (bestScoreView != null) {
            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            bestScoreView.setText(sharedPreferences.getString(MainActivity.BEST_SCORE_KEY, "0"));
        }
    }
}
