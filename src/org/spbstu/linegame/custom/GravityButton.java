package org.spbstu.linegame.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Egor Gorbunov on 03.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 */
public class GravityButton extends Button {
    public GravityButton(Context context) {
        super(context);
    }

    public GravityButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GravityButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed) {
        if (pressed != isPressed()) {
            setGravity(pressed ? Gravity.CENTER_HORIZONTAL |
                    Gravity.BOTTOM : Gravity.CENTER);
        }
        super.setPressed(pressed);
    }
}
