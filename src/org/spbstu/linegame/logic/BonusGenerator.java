package org.spbstu.linegame.logic;

import java.util.Random;

/**
 * Created by Egor Gorbunov on 12.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 */
public class BonusGenerator {
    private final Random random = new Random();
    private float bonusProbability;
    private final int minBonusPointNum;
    private final int maxBonusPointNum;
    private final Bonus[] bonuses; // all bonuses except Bonus.NONE

    /**
     * @param bonusProbability - the prob. with that not NONE Bonus will be generated
     */
    public BonusGenerator(float bonusProbability, int minBonusPointNum, int maxBonusPointNum) {

        this.bonusProbability = bonusProbability;
        this.minBonusPointNum = minBonusPointNum;
        this.maxBonusPointNum = maxBonusPointNum;
        bonuses = new Bonus[Bonus.values().length - 1];
        int i = 0;
        for (Bonus b : Bonus.values()) {
            if (!b.equals(Bonus.NONE)) {
                bonuses[i++] = b;
            }
        }
    }

    public Bonus generateRandomBonus() {
        if (random.nextFloat() < (1 - bonusProbability)) {
            return Bonus.NONE;
        }
        return bonuses[random.nextInt(bonuses.length)];
    }

    public int getNumOfPointsInBonus() {
        return random.nextInt(maxBonusPointNum - minBonusPointNum) + minBonusPointNum;
    }
}
