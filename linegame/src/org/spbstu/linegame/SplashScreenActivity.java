package org.spbstu.linegame;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import org.spbstu.linegame.view.AppleView;

/**
 * Created by Egor Gorbunov on 05.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 */
public class SplashScreenActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            findViewById(R.id.btnGoToDepSite).setVisibility(View.GONE);
        }

        AppleView apple = (AppleView) findViewById(R.id.appleView);

        Animation anim = new TranslateAnimation(0f, 0f, -2000f, 0f);
        anim.setDuration(2000);
        anim.setInterpolator(new BounceInterpolator());
        ObjectAnimator animY = ObjectAnimator.ofFloat(apple, "translationY", -2000f, 0f);
        animY.setDuration(2000);
        animY.setInterpolator(new BounceInterpolator());
        animY.setRepeatCount(0);
        //animY.start();

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                apple.startAppleAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        apple.setAnimation(anim);
        anim.start();
    }

    boolean isGameStarted = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isGameStarted) {
            startGameActivity();
            return true;
        } else {
            return false;
        }
    }

    private void startGameActivity() {
        isGameStarted = true;
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        finish();
    }

    public void onClick_btnToSite(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.dep_site)));
        startActivity(browserIntent);
    }


}
