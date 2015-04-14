package org.spbstu.linegame.logic;

/**
 * Created by Egor Gorbunov on 03.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 *
 * Describe Bonuses. Every bonus has it's own id from [0, BONUS_NUM)
 */
public class Bonus {
    public final static char NO_BONUS = 0;
    public final static char DECREASE_THICKENING_SPEED = 1;
    public final static char INCREASE_THICKENING_SPEED = 2;
    public final static char INVISIBLE_LINE = 3;
    public final static char IMPOSSIBLE_TO_MISS =  4;
    public final static char SUDDEN_DEATH = 5;
    public final static char DECREASE_GAME_SPEED = 6;
    public final static char INCREASE_GAME_SPEED = 7;

    public final static char[] ALL_SIGNIFICANT_BONUSES = {
            DECREASE_THICKENING_SPEED, INCREASE_THICKENING_SPEED, INVISIBLE_LINE, IMPOSSIBLE_TO_MISS,
            SUDDEN_DEATH, DECREASE_GAME_SPEED, INCREASE_GAME_SPEED
    };

    public final static char[] ALL_BONUSES = {
            NO_BONUS, DECREASE_THICKENING_SPEED, INCREASE_THICKENING_SPEED, INVISIBLE_LINE, IMPOSSIBLE_TO_MISS,
            SUDDEN_DEATH, DECREASE_GAME_SPEED, INCREASE_GAME_SPEED
    };

    public static char getBonusNum() {
        return (char) (ALL_BONUSES.length);
    }
}
