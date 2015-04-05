package org.spbstu.linegame;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment_layout, null);
        return view;
    }

    public void onClick_btnNewGame(View view) {
    }

    public void onClick_btnScores(View view) {
    }

    public void onClick_btnRules(View view) {
    }
}
