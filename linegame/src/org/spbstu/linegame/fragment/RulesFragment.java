package org.spbstu.linegame.fragment;

import android.graphics.BlurMaskFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.spbstu.linegame.R;
import org.spbstu.linegame.view.rules.LineSample;

/**
 * Created by Egor Gorbunov on 13.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 */
public class RulesFragment extends Fragment {
    private final static float BLUR_RADIUS = 15;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rules_fragment_layout, container, false);

        LineSample mainLineView = (LineSample) view.findViewById(R.id.MainLineSample);
        Paint mainCurvePaint = new Paint();
        mainCurvePaint.setDither(true);
        mainCurvePaint.setStyle(Paint.Style.STROKE);
        mainCurvePaint.setStrokeJoin(Paint.Join.ROUND);
        mainCurvePaint.setStrokeCap(Paint.Cap.ROUND);
        mainCurvePaint.setColor(getResources().getColor(R.color.main_line_color));
        mainCurvePaint.setAntiAlias(true);
        mainLineView.setPaint(mainCurvePaint);


        LineSample tappedLineSample = (LineSample) view.findViewById(R.id.TappedLineSample);
        Paint tappedCurvePaint = new Paint();
        tappedCurvePaint.set(mainCurvePaint);
        tappedCurvePaint.setColor(getResources().getColor(R.color.tapped_line_color));
        tappedLineSample.setPaint(tappedCurvePaint);


        LineSample bonusLineSample = (LineSample) view.findViewById(R.id.DecThickSpeedLineSample);
        Paint decThickeningPaint = new Paint();
        decThickeningPaint.set(mainCurvePaint);
        decThickeningPaint.setColor(getResources().getColor(R.color.dec_thickening_bonus_color));
        bonusLineSample.setPaint(decThickeningPaint);

        bonusLineSample = (LineSample) view.findViewById(R.id.IncThickSpeedLineSample);
        Paint incThickeningPaint = new Paint();
        incThickeningPaint.set(mainCurvePaint);
        incThickeningPaint.setColor(getResources().getColor(R.color.inc_thickening_bonus_color));
        bonusLineSample.setPaint(incThickeningPaint);

        bonusLineSample = (LineSample) view.findViewById(R.id.InvisibleBonusLineSample);
        Paint invisibleBonusPaint = new Paint();
        invisibleBonusPaint.set(mainCurvePaint);
        invisibleBonusPaint.setColor(getResources().getColor(R.color.invisible_bonus_color));
        bonusLineSample.setPaint(invisibleBonusPaint);

        bonusLineSample = (LineSample) view.findViewById(R.id.NoMissLineSample);
        Paint impossibleToMissBonusPaint = new Paint();
        impossibleToMissBonusPaint.set(mainCurvePaint);
        impossibleToMissBonusPaint.setColor(getResources().getColor(R.color.impossible_to_miss_bonus_color));
        bonusLineSample.setPaint(impossibleToMissBonusPaint);

        bonusLineSample = (LineSample) view.findViewById(R.id.SuddenDeathSample);
        Paint suddenDeathBonusPaint = new Paint();
        suddenDeathBonusPaint.set(mainCurvePaint);
        suddenDeathBonusPaint.setColor(getResources().getColor(R.color.sudden_game_over_bonus_color));
        bonusLineSample.setPaint(suddenDeathBonusPaint);

        return view;
    }


}
