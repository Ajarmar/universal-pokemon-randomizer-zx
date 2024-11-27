package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Gen1Pokemon.java - represents an individual Gen 1 Pokemon. Used to    --*/
/*--                 handle things related to stats because of the lack     --*/
/*--                 of the Special split in Gen 1.                         --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
/*--                                                                        --*/
/*--  The custom code written here is licensed under the terms of the GPL:  --*/
/*--                                                                        --*/
/*--  This program is free software: you can redistribute it and/or modify  --*/
/*--  it under the terms of the GNU General Public License as published by  --*/
/*--  the Free Software Foundation, either version 3 of the License, or     --*/
/*--  (at your option) any later version.                                   --*/
/*--                                                                        --*/
/*--  This program is distributed in the hope that it will be useful,       --*/
/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/
/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/
/*--  GNU General Public License for more details.                          --*/
/*--                                                                        --*/
/*--  You should have received a copy of the GNU General Public License     --*/
/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/
/*----------------------------------------------------------------------------*/

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Gen1Pokemon extends Pokemon {

    public Gen1Pokemon() {
        shuffledStatsOrder = Arrays.asList(0, 1, 2, 3, 4);
    }

    @Override
    public void copyShuffledStatsUpEvolution(Pokemon evolvesFrom) {
        // If stats were already shuffled once, un-shuffle them
        shuffledStatsOrder = Arrays.asList(
                shuffledStatsOrder.indexOf(0),
                shuffledStatsOrder.indexOf(1),
                shuffledStatsOrder.indexOf(2),
                shuffledStatsOrder.indexOf(3),
                shuffledStatsOrder.indexOf(4));
        applyShuffledOrderToStats();
        shuffledStatsOrder = evolvesFrom.shuffledStatsOrder;
        applyShuffledOrderToStats();
    }

    @Override
    protected void applyShuffledOrderToStats() {
        List<Integer> stats = Arrays.asList(hp, attack, defense, special, speed);

        // Copy in new stats
        hp = stats.get(shuffledStatsOrder.get(0));
        attack = stats.get(shuffledStatsOrder.get(1));
        defense = stats.get(shuffledStatsOrder.get(2));
        special = stats.get(shuffledStatsOrder.get(3));
        speed = stats.get(shuffledStatsOrder.get(4));
    }

    @Override
    public void randomizeStatsWithinBST(Random random) {
        // Minimum 20 HP, 10 everything else
        int bst = bst() - 50;

        // Make weightings
        double hpW = random.nextDouble(), atkW = random.nextDouble(), defW = random.nextDouble();
        double specW = random.nextDouble(), speW = random.nextDouble();

        double totW = hpW + atkW + defW + specW + speW;

        hp = (int) Math.max(1, Math.round(hpW / totW * bst)) + 10;
        attack = (int) Math.max(1, Math.round(atkW / totW * bst)) + 10;
        defense = (int) Math.max(1, Math.round(defW / totW * bst)) + 10;
        special = (int) Math.max(1, Math.round(specW / totW * bst)) + 10;
        speed = (int) Math.max(1, Math.round(speW / totW * bst)) + 10;

        // Check for something we can't store
        if (hp > 255 || attack > 255 || defense > 255 || special > 255 || speed > 255) {
            // re roll
            randomizeStatsWithinBST(random);
        }
    }

    @Override
    public void randomizeBST(Random random) {
        double randomBST = random.nextGaussian();

        if (evoStageNumber == 1 && finalEvoStageNumber == 1) {
            if (random.nextDouble() < .5) randomBST = Math.max((randomBST * 35) + 515, 485);
            else randomBST = Math.max((randomBST * 35) + 365, 260);
        } else if (evoStageNumber == 1 && finalEvoStageNumber == 2) {
            randomBST = Math.max((randomBST * 20) + 365, 205);
        }  else if (evoStageNumber == 1) {
            randomBST = Math.max((randomBST * 20) + 240, 180);
        } else if (evoStageNumber == 2 && finalEvoStageNumber == 2) {
            randomBST = Math.max((randomBST * 20) + 395, 335);
        } else if (evoStageNumber > 2 && evoStageNumber == finalEvoStageNumber) {
            if (random.nextDouble() > .9) randomBST = Math.max((randomBST * 15) + 510, 480);
            else randomBST = Math.max((randomBST * 15) + 415, 365);
        } else {
            double average = 240 * Math.pow((1 + (1/(double) finalEvoStageNumber)),evoStageNumber-1);
            double min = average - 60;
            randomBST = Math.max((randomBST * 20) + average, min);
        }

        scaleStats((int)randomBST);

        // Check for something we can't store
        if (hp > 255 || attack > 255 || defense > 255 || special > 255 || speed > 255) {
            // re roll
            randomizeBST(random);
        }
    }

    @Override
    public void copyRandomizedStatsUpEvolution(Pokemon evolvesFrom) {
        double ourBST = bst();
//        double theirBST = evolvesFrom.bst();
//
//        ourBST *= ourBST/theirBST;

//        if (finalEvoStageNumber == evoStageNumber && evoStageNumber == 2) {
//            bstRatio = 3;
//        } else if (evolutionsFrom.size() == 0) {
//            bstRatio = (nextGaussian() * 0.1) + 1.43;
//        } else {
//            bstRatio = (nextGaussian() * 0.1) + 1.27;
//        }

//        bstRatio = Math.max(1.23,bstRatio);
//        bstRatio = Math.min(1.57,bstRatio);

        scaleStats((int)ourBST, evolvesFrom);
    }

    @Override
    public void scaleBSTUpEvolution(Pokemon evolvesFrom, Random random) {
        double ourBST = bst();
        double theirBST = evolvesFrom.bst();
        double minimumBSTRatio = 1 + (1/(double)(finalEvoStageNumber +1));
        double averageBSTRatio = 1 + (1/(double) finalEvoStageNumber);
        double randomRatioMod = random.nextGaussian() * (averageBSTRatio - minimumBSTRatio)/3;
        double bstRatio = Math.max((randomRatioMod + averageBSTRatio), minimumBSTRatio);

        // double bstRatio = 1 + (1/(double)finalEvoStageNumber);

//        if (finalEvoStageNumber == evoStageNumber && evoStageNumber == 2) {
//            bstRatio = 3;
//        } else if (evolutionsFrom.size() == 0) {
//            bstRatio = (nextGaussian() * 0.1) + 1.43;
//        } else {
//            bstRatio = (nextGaussian() * 0.1) + 1.27;
//        }

//        bstRatio = Math.max(1.23,bstRatio);
//        bstRatio = Math.min(1.57,bstRatio);

        if (ourBST < theirBST * minimumBSTRatio) ourBST = theirBST * bstRatio;

        scaleStats((int)ourBST);
    }

    @Override
    public void assignNewStatsForEvolution(Pokemon evolvesFrom, Random random) {
        double ourBST = bst();
        double theirBST = evolvesFrom.bst();

        double bstDiff = ourBST - theirBST;

        // Make weightings
        double hpW = random.nextDouble(), atkW = random.nextDouble(), defW = random.nextDouble();
        double specW = random.nextDouble(), speW = random.nextDouble();

        double totW = hpW + atkW + defW + specW + speW;

        double hpDiff = Math.round((hpW / totW) * bstDiff);
        double atkDiff = Math.round((atkW / totW) * bstDiff);
        double defDiff = Math.round((defW / totW) * bstDiff);
        double specDiff = Math.round((specW / totW) * bstDiff);
        double speDiff = Math.round((speW / totW) * bstDiff);

        hp = (int) Math.min(255, Math.max(1, evolvesFrom.hp + hpDiff));
        attack = (int) Math.min(255, Math.max(1, evolvesFrom.attack + atkDiff));
        defense = (int) Math.min(255, Math.max(1, evolvesFrom.defense + defDiff));
        speed = (int) Math.min(255, Math.max(1, evolvesFrom.speed + speDiff));
        special = (int) Math.min(255, Math.max(1, evolvesFrom.special + specDiff));
    }

    @Override
    public void scaleStats(int newBSTI) {
        double newBST = newBSTI;
        double oldBST = bst();
        double[][] stats = {{hp, attack, defense, special, speed},
                {0, 1, 2, 3, 4}};


        for (int i = 0; i < stats[0].length - 1; i++) {
            if (stats[0][i] < stats[0][i + 1]) {
                double temp = stats[0][i];
                stats[0][i] = stats[0][i + 1];
                stats[0][i + 1] = temp;

                temp = stats[1][i];
                stats[1][i] = stats[1][i + 1];
                stats[1][i + 1] = temp;

                i = -1;
            }
        }

/*
        if (newBST > oldBST) {
            for (int i = 0; i <= stats[0].length - 1; i++) {
                double oldStat = stats[0][i];
                stats[0][i] *= newBST/oldBST;
                stats[0][i] = Math.round(stats[0][i]);
                if (stats[0][i] > 255) {
                    stats[0][i] = 255;
                }
                newBST -= stats[0][i];
                oldBST -= oldStat;
            }
        } else if (newBST < oldBST) {
            for (int i = stats[0].length - 1; i >= 0; i--) {
                double oldStat = stats[0][i];
                stats[0][i] *= newBST/oldBST;
                stats[0][i] = Math.round(stats[0][i]);
                if (stats[0][i] < 10) {
                    stats[0][i] = 10;
                }
                newBST -= stats[0][i];
                oldBST -= oldStat;
            }
        }
*/

        if (newBST > oldBST) {

            // Direct Proportion Growth Method
            // Stats grow in direct proportion to new BST
            // Highest stat is checked for maxing out
            // Repeat for all remaining stats
            double currentStatTotal = oldBST;
            double goalStatTotal = newBST;
            double ratioToGoal = goalStatTotal/currentStatTotal;

            for (int i = 0; i <= stats[0].length - 1; i++) {
                double oldStat = stats[0][i];

                stats[0][i] *= ratioToGoal;
                stats[0][i] = Math.round(stats[0][i]);
                if (stats[0][i] > 255) {
                    stats[0][i] = 255;
                }

                currentStatTotal -= oldStat;
                goalStatTotal -= stats[0][i];
                ratioToGoal = goalStatTotal/currentStatTotal;
            }
        } else if (newBST < oldBST) {

            // Stat Proportional Reduction Method
            // Stats fall in direct proportion to new BST
            // Lowest stat is checked for maxing out
            // Repeat for all remaining stats
            double currentStatTotal = oldBST;
            double goalStatTotal = newBST;
            double ratioToGoal = goalStatTotal/currentStatTotal;

            for (int i = stats[0].length - 1; i >=0 ; i--) {
                double oldStat = stats[0][i];

                if (!(stats[0][i] <= 10)) {
                    stats[0][i] *= ratioToGoal;
                    stats[0][i] = Math.round(stats[0][i]);
                    if (stats[0][i] < 10) {
                        stats[0][i] = 10;
                    }
                }

                currentStatTotal -= oldStat;
                goalStatTotal -= stats[0][i];
                ratioToGoal = goalStatTotal/currentStatTotal;
            }
        }

        int currentTotal = 0;
        for (int i = 0; i <= stats[0].length - 1; i++) {
            currentTotal += (int)stats[0][i];
        }

        if (currentTotal != newBSTI) {
            int diff = newBSTI - currentTotal;
            int statIndex = stats[0].length - 1;
            int miss = 0;
            while (Math.abs(diff) > 0 && miss < stats[0].length) {
                if ((int)stats[0][statIndex] < 255 && (int)stats[0][statIndex] > 10) {
                    stats[0][statIndex] += Math.abs(diff)/diff;
                    diff -= Math.abs(diff)/diff;
                } else {
                    miss++;
                }
                if (statIndex > 0) statIndex--;
                else statIndex = stats[0].length - 1;
            }
        }

        for (int i = stats[0].length - 1; i >= 0; i--) {
            switch ((int) stats[1][i]) {
                case 0:
                    hp = (int) stats[0][i];
                    break;
                case 1:
                    attack = (int) stats[0][i];
                    break;
                case 2:
                    defense = (int) stats[0][i];
                    break;
                case 3:
                    special = (int) stats[0][i];
                    break;
                case 4:
                    speed = (int) stats[0][i];
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void scaleStats(int newBSTI, Pokemon pk) {
        double newBST = newBSTI;
        double oldBST = bst();
        double[][] stats = {{pk.hp, pk.attack, pk.defense, pk.special, pk.speed},
                {0, 1, 2, 3, 4}};


        for (int i = 0; i < stats[0].length - 1; i++) {
            if (stats[0][i] < stats[0][i + 1]) {
                double temp = stats[0][i];
                stats[0][i] = stats[0][i + 1];
                stats[0][i + 1] = temp;

                temp = stats[1][i];
                stats[1][i] = stats[1][i + 1];
                stats[1][i + 1] = temp;

                i = -1;
            }
        }

/*
        if (newBST > oldBST) {
            for (int i = 0; i <= stats[0].length - 1; i++) {
                double oldStat = stats[0][i];
                stats[0][i] *= newBST/oldBST;
                stats[0][i] = Math.round(stats[0][i]);
                if (stats[0][i] > 255) {
                    stats[0][i] = 255;
                }
                newBST -= stats[0][i];
                oldBST -= oldStat;
            }
        } else if (newBST < oldBST) {
            for (int i = stats[0].length - 1; i >= 0; i--) {
                double oldStat = stats[0][i];
                stats[0][i] *= newBST/oldBST;
                stats[0][i] = Math.round(stats[0][i]);
                if (stats[0][i] < 10) {
                    stats[0][i] = 10;
                }
                newBST -= stats[0][i];
                oldBST -= oldStat;
            }
        }
*/

        if (newBST > oldBST) {

            // Direct Proportion Growth Method
            // Stats grow in direct proportion to new BST
            // Highest stat is checked for maxing out
            // Repeat for all remaining stats
            double currentStatTotal = oldBST;
            double goalStatTotal = newBST;
            double ratioToGoal = goalStatTotal/currentStatTotal;

            for (int i = 0; i <= stats[0].length - 1; i++) {
                double oldStat = stats[0][i];

                stats[0][i] *= ratioToGoal;
                stats[0][i] = Math.round(stats[0][i]);
                if (stats[0][i] > 255) {
                    stats[0][i] = 255;
                }

                currentStatTotal -= oldStat;
                goalStatTotal -= stats[0][i];
                ratioToGoal = goalStatTotal/currentStatTotal;
            }
        } else if (newBST < oldBST) {

            // Stat Proportional Reduction Method
            // Stats fall in direct proportion to new BST
            // Lowest stat is checked for maxing out
            // Repeat for all remaining stats
            double currentStatTotal = oldBST;
            double goalStatTotal = newBST;
            double ratioToGoal = goalStatTotal/currentStatTotal;

            for (int i = stats[0].length - 1; i >=0 ; i--) {
                double oldStat = stats[0][i];

                if (!(stats[0][i] <= 10)) {
                    stats[0][i] *= ratioToGoal;
                    stats[0][i] = Math.round(stats[0][i]);
                    if (stats[0][i] < 10) {
                        stats[0][i] = 10;
                    }
                }

                currentStatTotal -= oldStat;
                goalStatTotal -= stats[0][i];
                ratioToGoal = goalStatTotal/currentStatTotal;
            }
        }

        int currentTotal = 0;
        for (int i = 0; i <= stats[0].length - 1; i++) {
            currentTotal += (int)stats[0][i];
        }

        if (currentTotal != newBSTI) {
            int diff = newBSTI - currentTotal;
            int statIndex = stats[0].length - 1;
            int miss = 0;
            while (Math.abs(diff) > 0 && miss < stats[0].length) {
                if ((int)stats[0][statIndex] < 255 && (int)stats[0][statIndex] > 10) {
                    stats[0][statIndex] += Math.abs(diff)/diff;
                    diff -= Math.abs(diff)/diff;
                } else {
                    miss++;
                }
                if (statIndex > 0) statIndex--;
                else statIndex = stats[0].length - 1;
            }
        }

        for (int i = stats[0].length - 1; i >= 0; i--) {
            switch ((int) stats[1][i]) {
                case 0:
                    hp = (int) stats[0][i];
                    break;
                case 1:
                    attack = (int) stats[0][i];
                    break;
                case 2:
                    defense = (int) stats[0][i];
                    break;
                case 3:
                    special = (int) stats[0][i];
                    break;
                case 4:
                    speed = (int) stats[0][i];
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int bst() {
        return hp + attack + defense + special + speed;
    }

    @Override
    public void determineStatPowerTotal(double statAverage, double offAverage, double defAverage) {
        double statRatioMax = 255/statAverage;
        double statRatioMin = 1/statRatioMax;
        double[] stats = {hp, attack, defense, special, speed};

        statPowerTotal = 0;
        for (int i = 0; i < stats.length; i++) {
            stats[i] = Math.pow(Math.max(stats[i]/statAverage,statRatioMin),2);
            if (stats[i] < 1) stats[i] = (-1) / stats[i];

            statPowerTotal += stats[i];
        }
        statPowerTotal *= 100;
    }

    @Override
    public int bstForPowerLevels() {
        return hp + attack + defense + special + speed;
    }

    @Override
    public boolean determineBigPoke(double minBST, double maxBST) {
        double bigThresholdBST = ((maxBST - minBST) * 0.75) + minBST;
        double bigThresholdStat = (bigThresholdBST/5) * 2;

        isBig = bst() > bigThresholdBST || Collections
                .max(Arrays.asList(hp, attack, defense, speed, special)) > bigThresholdStat;
        return isBig;
    }

    @Override
    public double getAttackSpecialAttackRatio() {
        return (double)attack / ((double)attack + (double)special);
    }

    @Override
    public String toString() {
        return "Pokemon [name=" + name + ", number=" + number + ", primaryType=" + primaryType + ", secondaryType="
                + secondaryType + ", hp=" + hp + ", attack=" + attack + ", defense=" + defense + ", special=" + special
                + ", speed=" + speed + "]";
    }
}
