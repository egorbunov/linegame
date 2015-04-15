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
    /**
     * @param bonusProbability - the prob. with that not NONE Bonus will be generated
     */
    public BonusGenerator(float bonusProbability, int minBonusPointNum, int maxBonusPointNum) {

        this.bonusProbability = bonusProbability;
        this.minBonusPointNum = minBonusPointNum;
        this.maxBonusPointNum = maxBonusPointNum;
    }

    public void setBonusProbability(float prob) {
        bonusProbability = prob;
    }

    public float getBonusProbability() {
        return bonusProbability;
    }
    public char generateRandomBonus() {
        if (random.nextFloat() < (1 - bonusProbability)) {
            return Bonus.NO_BONUS;
        }
        return (char) (random.nextInt(Bonus.getBonusNum() - 1) + 1);
    }

    public int getNumOfPointsInBonus() {
        return random.nextInt(maxBonusPointNum - minBonusPointNum) + minBonusPointNum;
    }
}
