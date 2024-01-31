package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Pokemon.java - represents an individual Pokemon, and contains         --*/
/*--                 common Pokemon-related functions.                      --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Originally part of "Universal Pokemon Randomizer" by Dabomstew        --*/
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

import com.dabomstew.pkrandom.constants.Species;

import java.util.*;
import java.util.stream.Collectors;

public class Pokemon implements Comparable<Pokemon> {

    public String name;
    public int number;

    public String formeSuffix = "";
    public Pokemon baseForme = null;
    public int formeNumber = 0;
    public int cosmeticForms = 0;
    public int formeSpriteIndex = 0;
    public boolean actuallyCosmetic = false;
    public List<Integer> realCosmeticFormNumbers = new ArrayList<>();

    public Type primaryType, secondaryType;

    public int hp, attack, defense, spatk, spdef, speed, special;
    public double statPowerTotal;

    public boolean isBig;

    public int ability1, ability2, ability3;

    public int catchRate, expYield;

    public int guaranteedHeldItem, commonHeldItem, rareHeldItem, darkGrassHeldItem;

    public int genderRatio;

    public int frontSpritePointer, picDimensions;

    public int callRate;

    public ExpCurve growthCurve;

    public int evoStageNumber = 1, finalStageNumber = 1;
    public int baseSPTIndex = 0, updatedSPTIndex = 0;
    public int finalBST = 0;
    public double finalSPT = 0;
    public int baseFinalSPTIndex = 0, updatedFinalSPTIndex = 0;

    public List<Evolution> evolutionsFrom = new ArrayList<>();
    public List<Evolution> evolutionsTo = new ArrayList<>();

    public List<MegaEvolution> megaEvolutionsFrom = new ArrayList<>();
    public List<MegaEvolution> megaEvolutionsTo = new ArrayList<>();

    protected List<Integer> shuffledStatsOrder;

    // A flag to use for things like recursive stats copying.
    // Must not rely on the state of this flag being preserved between calls.
    public boolean temporaryFlag;

    public Pokemon() {
        shuffledStatsOrder = Arrays.asList(0, 1, 2, 3, 4, 5);
    }

    public void shuffleStats(Random random) {
        Collections.shuffle(shuffledStatsOrder, random);
        applyShuffledOrderToStats();
    }
    
    public void copyShuffledStatsUpEvolution(Pokemon evolvesFrom) {
        // If stats were already shuffled once, un-shuffle them
        shuffledStatsOrder = Arrays.asList(
                shuffledStatsOrder.indexOf(0),
                shuffledStatsOrder.indexOf(1),
                shuffledStatsOrder.indexOf(2),
                shuffledStatsOrder.indexOf(3),
                shuffledStatsOrder.indexOf(4),
                shuffledStatsOrder.indexOf(5));
        applyShuffledOrderToStats();
        shuffledStatsOrder = evolvesFrom.shuffledStatsOrder;
        applyShuffledOrderToStats();
    }

    protected void applyShuffledOrderToStats() {
        List<Integer> stats = Arrays.asList(hp, attack, defense, spatk, spdef, speed);

        // Copy in new stats
        hp = stats.get(shuffledStatsOrder.get(0));
        attack = stats.get(shuffledStatsOrder.get(1));
        defense = stats.get(shuffledStatsOrder.get(2));
        spatk = stats.get(shuffledStatsOrder.get(3));
        spdef = stats.get(shuffledStatsOrder.get(4));
        speed = stats.get(shuffledStatsOrder.get(5));
    }

    public void randomizeStatsWithinBST(Random random) {
        if (number == Species.shedinja) {
            // Shedinja is horribly broken unless we restrict him to 1HP.
            int bst = bst() - 51;

            // Make weightings
            double atkW = random.nextDouble(), defW = random.nextDouble();
            double spaW = random.nextDouble(), spdW = random.nextDouble(), speW = random.nextDouble();

            double totW = atkW + defW + spaW + spdW + speW;

            hp = 1;
            attack = (int) Math.max(1, Math.round(atkW / totW * bst)) + 10;
            defense = (int) Math.max(1, Math.round(defW / totW * bst)) + 10;
            spatk = (int) Math.max(1, Math.round(spaW / totW * bst)) + 10;
            spdef = (int) Math.max(1, Math.round(spdW / totW * bst)) + 10;
            speed = (int) Math.max(1, Math.round(speW / totW * bst)) + 10;
        }
        else {

            int bst = bst() - 60;

            // Make weightings
            double hpW = random.nextDouble(), atkW = random.nextDouble(), defW = random.nextDouble();
            double spaW = random.nextDouble(), spdW = random.nextDouble(), speW = random.nextDouble();

            double totW = hpW + atkW + defW + spaW + spdW + speW;

            hp = (int) Math.max(1, Math.round(hpW / totW * bst)) + 10;
            attack = (int) Math.max(1, Math.round(atkW / totW * bst)) + 10;
            defense = (int) Math.max(1, Math.round(defW / totW * bst)) + 10;
            spatk = (int) Math.max(1, Math.round(spaW / totW * bst)) + 10;
            spdef = (int) Math.max(1, Math.round(spdW / totW * bst)) + 10;
            speed = (int) Math.max(1, Math.round(speW / totW * bst)) + 10;
        }

        // Check for something we can't store
        if (hp > 255 || attack > 255 || defense > 255 || spatk > 255 || spdef > 255 || speed > 255) {
            // re roll
            randomizeStatsWithinBST(random);
        }

    }

    public void randomizeBST(Random random) {
        double randomBST = random.nextGaussian();

        if (evoStageNumber == 1 && finalStageNumber == 1) {
            if (random.nextDouble() < .5) randomBST = Math.max((randomBST * 45) + 620, 580);
            else randomBST = Math.max((randomBST * 45) + 440, 315);
        } else if (evoStageNumber == 1 && finalStageNumber == 2) {
            randomBST = Math.max((randomBST * 25) + 320, 245);
        }  else if (evoStageNumber == 1) {
            randomBST = Math.max((randomBST * 25) + 290, 215);
        } else if (evoStageNumber == 2 && finalStageNumber == 2) {
            randomBST = Math.max((randomBST * 25) + 475, 400);
        } else if (evoStageNumber > 2 && evoStageNumber == finalStageNumber) {
            if (random.nextDouble() > .9) randomBST = Math.max((randomBST * 15) + 610, 575);
            else randomBST = Math.max((randomBST * 20) + 500, 440);
        } else {
            double average = 290 * Math.pow((1 + (1/(double)finalStageNumber)),evoStageNumber-1);
            double min = average - 75;
            randomBST = Math.max((randomBST * 25) + average, min);
        }

        scaleStats((int)randomBST);

        // Check for something we can't store
        if (hp > 255 || attack > 255 || defense > 255 || spatk > 255 || spdef > 255 || speed > 255) {
            // re roll
            randomizeBST(random);
        }
    }

    public void copyRandomizedStatsUpEvolution(Pokemon evolvesFrom) {
        double ourBST = bst();

        // ourBST *= ourBST/theirBST;

//        if (finalStageNumber == evoStageNumber && evoStageNumber == 2) {
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

    public void scaleBSTUpEvolution(Pokemon evolvesFrom,Random random) {
        double ourBST = bst();
        double theirBST = evolvesFrom.bst();
        double minimumBSTRatio = 1 + (1/(double)(finalStageNumber+1));
        double averageBSTRatio = 1 + (1/(double)finalStageNumber);
        double randomRatioMod = random.nextGaussian() * (averageBSTRatio - minimumBSTRatio)/3;
        double bstRatio = Math.max((randomRatioMod + averageBSTRatio), minimumBSTRatio);

        // double bstRatio = 1 + (1/(double)finalStageNumber);

//        if (finalStageNumber == evoStageNumber && evoStageNumber == 2) {
//            bstRatio = 3;
//        } else if (evolutionsFrom.size() == 0) {
//            bstRatio = (nextGaussian() * 0.1) + 1.43;
//        } else {
//            bstRatio = (nextGaussian() * 0.1) + 1.27;
//        }

//        bstRatio = Math.max(1.23,bstRatio);
//        bstRatio = Math.min(1.57,bstRatio);

        if (ourBST < theirBST * bstRatio) ourBST = theirBST * bstRatio;

        scaleStats((int)ourBST);
    }

    public void scaleStats(int newBSTI) {
        double newBST = newBSTI;
        double oldBST = bst();
        double[][] stats = {{hp, attack, defense, spatk, spdef, speed},
                {0, 1, 2, 3, 4, 5}};

        // Sort stats from Highest to Lowest
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

            // Stat Proportional Growth Method
            // Higher Stats grow Slower
            double amountToGrow = newBST - oldBST;
            double roomForGrowth = 0;

            for (int i = 0; i <= stats[0].length - 1; i++) {
                if (number == Species.shedinja && stats[1][i] == 0) {
                    continue;
                }
                roomForGrowth += 255 - stats[0][i];
            }

            for (int i = 0; i <= stats[0].length - 1; i++) {
                double oldStat = stats[0][i];
                if (number == Species.shedinja && stats[1][i] == 0) {
                    continue;
                }
                stats[0][i] += ((255 - stats[0][i])/roomForGrowth) * amountToGrow;
                stats[0][i] = Math.round(stats[0][i]);
                if (stats[0][i] > 255) {
                    stats[0][i] = 255;
                }
            }
        } else if (newBST < oldBST) {

            // Stat Proportional Reduction Method
            // Lower Stats fall slower
            double amountToReduce = oldBST - newBST;
            double roomForReduction = 0;

            for (int i = 0; i <= stats[0].length - 1; i++) {
                if ((number == Species.shedinja && stats[1][i] == 0) || stats[0][i] <= 10) {
                    continue;
                }
                roomForReduction += stats[0][i] - 10;
            }

            for (int i = 0; i <= stats[0].length - 1; i++) {
                if ((number == Species.shedinja && stats[1][i] == 0) || stats[0][i] <= 10) {
                    continue;
                }
                stats[0][i] -= ((stats[0][i] - 10)/roomForReduction) * amountToReduce;
                stats[0][i] = Math.round(stats[0][i]);
                if (stats[0][i] < 10) {
                    stats[0][i] = 10;
                }
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

                if (!(number == Species.shedinja && stats[1][i] == 0)) {
                    stats[0][i] *= ratioToGoal;
                    stats[0][i] = Math.round(stats[0][i]);
                    if (stats[0][i] > 255) {
                        stats[0][i] = 255;
                    }
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

                if (!((number == Species.shedinja && stats[1][i] == 0) || stats[0][i] <= 10)) {
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
                    spatk = (int) stats[0][i];
                    break;
                case 4:
                    spdef = (int) stats[0][i];
                    break;
                case 5:
                    speed = (int) stats[0][i];
                    break;
                default:
                    break;
            }
        }


    }

    public void scaleStats(int newBSTI, Pokemon pk) {
        double newBST = newBSTI;
        double oldBST = pk.bst();
        double[][] stats = {{pk.hp, pk.attack, pk.defense, pk.spatk, pk.spdef, pk.speed},
                {0, 1, 2, 3, 4, 5}};

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

            // Stat Proportional Growth Method
            // Higher Stats grow Slower
            double amountToGrow = newBST - oldBST;
            double roomForGrowth = 0;

            for (int i = 0; i <= stats[0].length - 1; i++) {
                if (number == Species.shedinja && stats[1][i] == 0) {
                    continue;
                }
                roomForGrowth += 255 - stats[0][i];
            }

            for (int i = 0; i <= stats[0].length - 1; i++) {
                double oldStat = stats[0][i];
                if (number == Species.shedinja && stats[1][i] == 0) {
                    continue;
                }
                stats[0][i] += ((255 - stats[0][i])/roomForGrowth) * amountToGrow;
                stats[0][i] = Math.round(stats[0][i]);
                if (stats[0][i] > 255) {
                    stats[0][i] = 255;
                }
            }
        } else if (newBST < oldBST) {

            // Stat Proportional Reduction Method
            // Lower Stats fall slower
            double amountToReduce = oldBST - newBST;
            double roomForReduction = 0;

            for (int i = 0; i <= stats[0].length - 1; i++) {
                if ((number == Species.shedinja && stats[1][i] == 0) || stats[0][i] <= 10) {
                    continue;
                }
                roomForReduction += stats[0][i] - 10;
            }

            for (int i = 0; i <= stats[0].length - 1; i++) {
                if ((number == Species.shedinja && stats[1][i] == 0) || stats[0][i] <= 10) {
                    continue;
                }
                stats[0][i] -= ((stats[0][i] - 10)/roomForReduction) * amountToReduce;
                stats[0][i] = Math.round(stats[0][i]);
                if (stats[0][i] < 10) {
                    stats[0][i] = 10;
                }
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

                if (!(number == Species.shedinja && stats[1][i] == 0)) {
                    stats[0][i] *= ratioToGoal;
                    stats[0][i] = Math.round(stats[0][i]);
                    if (stats[0][i] > 255) {
                        stats[0][i] = 255;
                    }
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

                if (!((number == Species.shedinja && stats[1][i] == 0) || stats[0][i] <= 10)) {
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
                    spatk = (int) stats[0][i];
                    break;
                case 4:
                    spdef = (int) stats[0][i];
                    break;
                case 5:
                    speed = (int) stats[0][i];
                    break;
                default:
                    break;
            }
        }

        //if (bst() != newBSTI) speed += newBSTI - bst();
    }

    public void assignNewStatsForEvolution(Pokemon evolvesFrom, Random random) {

        double ourBST = bst();
        double theirBST = evolvesFrom.bst();

        double bstDiff = ourBST - theirBST;

        // Make weightings
        double hpW = random.nextDouble(), atkW = random.nextDouble(), defW = random.nextDouble();
        double spaW = random.nextDouble(), spdW = random.nextDouble(), speW = random.nextDouble();

        double totW = hpW + atkW + defW + spaW + spdW + speW;

        double hpDiff = Math.round((hpW / totW) * bstDiff);
        double atkDiff = Math.round((atkW / totW) * bstDiff);
        double defDiff = Math.round((defW / totW) * bstDiff);
        double spaDiff = Math.round((spaW / totW) * bstDiff);
        double spdDiff = Math.round((spdW / totW) * bstDiff);
        double speDiff = Math.round((speW / totW) * bstDiff);

        hp = (int) Math.min(255, Math.max(1, evolvesFrom.hp + hpDiff));
        attack = (int) Math.min(255, Math.max(1, evolvesFrom.attack + atkDiff));
        defense = (int) Math.min(255, Math.max(1, evolvesFrom.defense + defDiff));
        speed = (int) Math.min(255, Math.max(1, evolvesFrom.speed + speDiff));
        spatk = (int) Math.min(255, Math.max(1, evolvesFrom.spatk + spaDiff));
        spdef = (int) Math.min(255, Math.max(1, evolvesFrom.spdef + spdDiff));
    }

    public int bst() {
        return hp + attack + defense + spatk + spdef + speed;
    }

    public void determineStatPowerTotal(double statAverage) {
        double statRatioMax = 255/statAverage;
        double statRatioMin = 1/statRatioMax;
        double[] stats = {hp, attack, defense, spatk, spdef, speed};

        statPowerTotal = 0;
        for (int i = 0; i < stats.length; i++) {

            stats[i] = Math.log(Math.max(stats[i]/statAverage,statRatioMin));
            if (stats[i] > 0) stats[i] *= 2;
            else if (stats[i] < 0) stats[i] /= 2;

            if (number == Species.shedinja && i == 0) statPowerTotal += 2;
            else statPowerTotal += stats[i];
        }
        statPowerTotal *= 100;
    }

    public int bstForPowerLevels() {
        // Take into account Shedinja's purposefully nerfed HP
        if (number == Species.shedinja) {
            return (attack + defense + spatk + spdef + speed) * 6 / 5;
        } else {
            return hp + attack + defense + spatk + spdef + speed;
        }
    }

    public double getAttackSpecialAttackRatio() {
        return (double)attack / ((double)attack + (double)spatk);
    }

    public int getBaseNumber() {
        Pokemon base = this;
        while (base.baseForme != null) {
            base = base.baseForme;
        }
        return base.number;
    }

    public void copyBaseFormeBaseStats(Pokemon baseForme) {
        hp = baseForme.hp;
        attack = baseForme.attack;
        defense = baseForme.defense;
        speed = baseForme.speed;
        spatk = baseForme.spatk;
        spdef = baseForme.spdef;
    }

    public void copyBaseFormeAbilities(Pokemon baseForme) {
        ability1 = baseForme.ability1;
        ability2 = baseForme.ability2;
        ability3 = baseForme.ability3;
    }

    public void copyBaseFormeEvolutions(Pokemon baseForme) {
        evolutionsFrom = baseForme.evolutionsFrom;
    }

    public int getSpriteIndex() {
        return formeNumber == 0 ? number : formeSpriteIndex + formeNumber - 1;
    }

    public String fullName() {
        return name + formeSuffix;
    }

    @Override
    public String toString() {
        return "Pokemon [name=" + name + formeSuffix + ", number=" + number + ", primaryType=" + primaryType
                + ", secondaryType=" + secondaryType + ", hp=" + hp + ", attack=" + attack + ", defense=" + defense
                + ", spatk=" + spatk + ", spdef=" + spdef + ", speed=" + speed + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + number;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pokemon other = (Pokemon) obj;
        return number == other.number;
    }

    @Override
    public int compareTo(Pokemon o) {
        return number - o.number;
    }


    private static final List<Integer> legendaries = Arrays.asList(Species.articuno, Species.zapdos, Species.moltres,
            Species.mewtwo, Species.mew, Species.raikou, Species.entei, Species.suicune, Species.lugia, Species.hoOh,
            Species.celebi, Species.regirock, Species.regice, Species.registeel, Species.latias, Species.latios,
            Species.kyogre, Species.groudon, Species.rayquaza, Species.jirachi, Species.deoxys, Species.uxie,
            Species.mesprit, Species.azelf, Species.dialga, Species.palkia, Species.heatran, Species.regigigas,
            Species.giratina, Species.cresselia, Species.phione, Species.manaphy, Species.darkrai, Species.shaymin,
            Species.arceus, Species.victini, Species.cobalion, Species.terrakion, Species.virizion, Species.tornadus,
            Species.thundurus, Species.reshiram, Species.zekrom, Species.landorus, Species.kyurem, Species.keldeo,
            Species.meloetta, Species.genesect, Species.xerneas, Species.yveltal, Species.zygarde, Species.diancie,
            Species.hoopa, Species.volcanion, Species.typeNull, Species.silvally, Species.tapuKoko, Species.tapuLele,
            Species.tapuBulu, Species.tapuFini, Species.cosmog, Species.cosmoem, Species.solgaleo, Species.lunala,
            Species.necrozma, Species.magearna, Species.marshadow, Species.zeraora);

    private static final List<Integer> strongLegendaries = Arrays.asList(Species.mewtwo, Species.lugia, Species.hoOh,
            Species.kyogre, Species.groudon, Species.rayquaza, Species.dialga, Species.palkia, Species.regigigas,
            Species.giratina, Species.arceus, Species.reshiram, Species.zekrom, Species.kyurem, Species.xerneas,
            Species.yveltal, Species.cosmog, Species.cosmoem, Species.solgaleo, Species.lunala);

    private static final List<Integer> ultraBeasts = Arrays.asList(Species.nihilego, Species.buzzwole, Species.pheromosa,
            Species.xurkitree, Species.celesteela, Species.kartana, Species.guzzlord, Species.poipole, Species.naganadel,
            Species.stakataka, Species.blacephalon);

    public boolean isLegendary() {
        return formeNumber == 0 ? legendaries.contains(this.number) : legendaries.contains(this.baseForme.number);
    }

    public boolean isStrongLegendary() {
        return formeNumber == 0 ? strongLegendaries.contains(this.number) : strongLegendaries.contains(this.baseForme.number);
    }

    public boolean determineBigPoke(double minBST, double maxBST) {
        double bigThresholdBST = ((maxBST - minBST) * 0.75) + minBST;
        double bigThresholdStat = (bigThresholdBST/6) * 2;

        isBig = bst() > bigThresholdBST || Collections
                .max(Arrays.asList(hp, attack, defense, speed, spatk, spdef)) > bigThresholdStat;
        return isBig;
    }

    public int determineEvoStageNumber() {
        if (evolutionsTo.size() == 0)
            evoStageNumber = 1;
        else
            evoStageNumber = evolutionsTo.get(0).from.determineEvoStageNumber() + 1;

        return evoStageNumber;
    }

    public int findFinalEvoStageNumber() {
        if (evolutionsFrom.size() == 0)
            finalStageNumber = evoStageNumber;
        else {
            int maxStageNumber = evoStageNumber;
            for (int i = 0; i < evolutionsFrom.size(); i++){
                maxStageNumber = Math.max(maxStageNumber, evolutionsFrom.get(i).to.findFinalEvoStageNumber());
            }
            finalStageNumber = maxStageNumber;
        }

        return finalStageNumber;
    }

    public int findFinalBST() {
        if (evolutionsFrom.size() == 0)
            finalBST = bstForPowerLevels();
        else {
            int maxBST = bstForPowerLevels();
            for (int i = 0; i < evolutionsFrom.size(); i++){
                maxBST = Math.max(maxBST, evolutionsFrom.get(i).to.findFinalBST());
            }
            finalBST = maxBST;
        }

        return finalBST;
    }

    public double findFinalSPT() {
        if (evolutionsFrom.size() == 0)
            finalSPT = statPowerTotal;
        else {
            double maxSPT = statPowerTotal;
            for (int i = 0; i < evolutionsFrom.size(); i++){
                maxSPT = Math.max(maxSPT, evolutionsFrom.get(i).to.findFinalSPT());
            }
            finalSPT = maxSPT;
        }

        return finalSPT;
    }

    public int findFinalSPTIndex(List<Pokemon> BSTSort, boolean initial) {
        if (evolutionsFrom.size() == 0)
            if (initial) baseFinalSPTIndex = BSTSort.indexOf(this);
            else updatedFinalSPTIndex = BSTSort.indexOf(this);
        else {
            int maxSPTIndex = BSTSort.indexOf(this);
            for (int i = 0; i < evolutionsFrom.size(); i++){
                maxSPTIndex = Math.max(maxSPTIndex, evolutionsFrom.get(i).to.findFinalSPTIndex(BSTSort, initial));
            }
            if (initial) baseFinalSPTIndex = maxSPTIndex;
            else updatedFinalSPTIndex = maxSPTIndex;
        }
        if (initial) return baseFinalSPTIndex;
        else return updatedFinalSPTIndex;
    }

    public boolean isBigPoke() { return isBig; }

    public int hasSplitEvo() {
        int value = 0;
        if (evolutionsFrom.size() == 0) value = 0;
        else if (evolutionsFrom.size() > 1) value = 1;
        else {
            for (int i = 0; i < evolutionsFrom.size(); i++) {
                value = Math.max(value, evolutionsFrom.get(i).to.hasSplitEvo());
            }
        }
        return value;
    }

    // This method can only be used in contexts where alt formes are NOT involved; otherwise, some alt formes
    // will be considered as Ultra Beasts in SM.
    // In contexts where formes are involved, use "if (ultraBeastList.contains(...))" instead,
    // assuming "checkPokemonRestrictions" has been used at some point beforehand.
    public boolean isUltraBeast() {
        return ultraBeasts.contains(this.number);
    }

    public int getCosmeticFormNumber(int num) {
        return realCosmeticFormNumbers.isEmpty() ? num : realCosmeticFormNumbers.get(num);
    }

    public List<Evolution> getEvolutionsFrom() {
        return evolutionsFrom;
    }

    public List<Evolution> getFilteredEvolutionsFrom() {
        // Filter out evolutions with a duplicate names
        HashSet<String> nameSet = new HashSet<String>();
        return evolutionsFrom.stream().filter(e -> nameSet.add(e.to.name))
                .collect(Collectors.toList());
    }

    public void setEvolutionsFrom(List<Evolution> evolutionsFrom) {
        this.evolutionsFrom = evolutionsFrom;
    }

    public List<Evolution> getEvolutionsTo() {
        return evolutionsTo;
    }

    public void setEvolutionsTo(List<Evolution> evolutionsTo) {
        this.evolutionsTo = evolutionsTo;
    }

}


