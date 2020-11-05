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

import com.dabomstew.pkrandom.constants.Gen7Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

    public int ability1, ability2, ability3;

    public int catchRate, expYield;

    public int guaranteedHeldItem, commonHeldItem, rareHeldItem, darkGrassHeldItem;

    public int genderRatio;

    public int frontSpritePointer, picDimensions;

    public int callRate;

    public ExpCurve growthCurve;

    public List<Evolution> evolutionsFrom = new ArrayList<>();
    public List<Evolution> evolutionsTo = new ArrayList<>();

    public List<MegaEvolution> megaEvolutionsFrom = new ArrayList<>();
    public List<MegaEvolution> megaEvolutionsTo = new ArrayList<>();

    private List<Integer> shuffledStatsOrder;

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
        shuffledStatsOrder = evolvesFrom.shuffledStatsOrder;
        applyShuffledOrderToStats();
    }

    private void applyShuffledOrderToStats() {
        List<Integer> stats = Arrays.asList(hp, attack, defense, spatk, spdef, speed);

        // Copy in new stats
        hp = stats.get(shuffledStatsOrder.get(0));
        attack = stats.get(shuffledStatsOrder.get(1));
        defense = stats.get(shuffledStatsOrder.get(2));
        spatk = stats.get(shuffledStatsOrder.get(3));
        spdef = stats.get(shuffledStatsOrder.get(4));
        speed = stats.get(shuffledStatsOrder.get(5));

        // make special the average of spatk and spdef
        special = (int) Math.ceil((spatk + spdef) / 2.0f);
    }

    public void randomizeStatsWithinBST(Random random) {
        if (number == 292) {
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

            // Fix up special too
            special = (int) Math.ceil((spatk + spdef) / 2.0f);

        } else {
            // Minimum 20 HP, 10 everything else
            int bst = bst() - 70;

            // Make weightings
            double hpW = random.nextDouble(), atkW = random.nextDouble(), defW = random.nextDouble();
            double spaW = random.nextDouble(), spdW = random.nextDouble(), speW = random.nextDouble();

            double totW = hpW + atkW + defW + spaW + spdW + speW;

            hp = (int) Math.max(1, Math.round(hpW / totW * bst)) + 20;
            attack = (int) Math.max(1, Math.round(atkW / totW * bst)) + 10;
            defense = (int) Math.max(1, Math.round(defW / totW * bst)) + 10;
            spatk = (int) Math.max(1, Math.round(spaW / totW * bst)) + 10;
            spdef = (int) Math.max(1, Math.round(spdW / totW * bst)) + 10;
            speed = (int) Math.max(1, Math.round(speW / totW * bst)) + 10;

            // Fix up special too
            special = (int) Math.ceil((spatk + spdef) / 2.0f);
        }

        // Check for something we can't store
        if (hp > 255 || attack > 255 || defense > 255 || spatk > 255 || spdef > 255 || speed > 255) {
            // re roll
            randomizeStatsWithinBST(random);
        }

    }

    public void copyRandomizedStatsUpEvolution(Pokemon evolvesFrom) {
        double ourBST = bst();
        double theirBST = evolvesFrom.bst();

        double bstRatio = ourBST / theirBST;

        hp = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.hp * bstRatio)));
        attack = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.attack * bstRatio)));
        defense = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.defense * bstRatio)));
        speed = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.speed * bstRatio)));
        spatk = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.spatk * bstRatio)));
        spdef = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.spdef * bstRatio)));

        special = (int) Math.ceil((spatk + spdef) / 2.0f);
    }

    private int bst() {
        return hp + attack + defense + spatk + spdef + speed;
    }

    public int bstForPowerLevels() {
        // Take into account Shedinja's purposefully nerfed HP
        if (number == 292) {
            return (attack + defense + spatk + spdef + speed) * 6 / 5;
        } else {
            return hp + attack + defense + spatk + spdef + speed;
        }
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

    public String toStringRBY() {
        return "Pokemon [name=" + name + ", number=" + number + ", primaryType=" + primaryType + ", secondaryType="
                + secondaryType + ", hp=" + hp + ", attack=" + attack + ", defense=" + defense + ", special=" + special
                + ", speed=" + speed + "]";
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

    private static final List<Integer> legendaries = Arrays.asList(144, 145, 146, 150, 151, 243, 244, 245, 249, 250,
            251, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 479, 480, 481, 482, 483, 484, 485, 486, 487, 488,
            489, 490, 491, 492, 493, 494, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 648, 649, 716, 717, 718,
            719, 720, 721, 785, 786, 787, 788, 789, 790, 791, 792, 800, 801, 802, 807);

    private static final List<Integer> strongLegendaries = Arrays.asList(150, 249, 250, 382, 383, 384, 483, 484, 486,
            487, 493, 643, 644, 646, 716, 717, 789, 790, 791, 792);

    private static final List<Integer> ultraBeasts = Arrays.asList(793, 794, 795, 796, 797, 798, 799, 803, 804, 805, 806);

    public boolean isLegendary() {
        return formeNumber == 0 ? legendaries.contains(this.number) : legendaries.contains(this.baseForme.number);
    }

    public boolean isStrongLegendary() {
        return formeNumber == 0 ? strongLegendaries.contains(this.number) : strongLegendaries.contains(this.baseForme.number);
    }

    // This method can only be used in contexts where alt forms are NOT involved; otherwise, some alt forms
    // will be considered as Ultra Beasts in SM.
    // In contexts where forms are involved, use "if (ultraBeastList.contains(...))" instead,
    // assuming "checkPokemonRestrictions" has been used at some point beforehand.
    public boolean isUltraBeast() {
        return ultraBeasts.contains(this.number);
    }

    public int getCosmeticFormNumber(int num) {
        return realCosmeticFormNumbers.isEmpty() ? num : realCosmeticFormNumbers.get(num);
    }

}
