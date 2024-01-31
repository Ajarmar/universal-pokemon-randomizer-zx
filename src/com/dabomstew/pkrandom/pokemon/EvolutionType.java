package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  EvolutionType.java - describes what process is necessary for an       --*/
/*--                       evolution to occur                               --*/
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

public enum EvolutionType {
    /* @formatter:off */
    LEVEL(1, 1, 4, 4, 4, 4, 4),
    STONE(2, 2, 7, 7, 8, 8, 8),
    TRADE(3, 3, 5, 5, 5, 5, 5),
    TRADE_ITEM(-1, 3, 6, 6, 6, 6, 6),
    HAPPINESS(-1, 4, 1, 1, 1, 1, 1),
    HAPPINESS_DAY(-1, 4, 2, 2, 2, 2, 2),
    HAPPINESS_NIGHT(-1, 4, 3, 3, 3, 3, 3),
    LEVEL_ATTACK_HIGHER(-1, 5, 8, 8, 9, 9, 9),
    LEVEL_DEFENSE_HIGHER(-1, 5, 10, 10, 11, 11, 11),
    LEVEL_ATK_DEF_SAME(-1, 5, 9, 9, 10, 10, 10),
    LEVEL_LOW_PV(-1, -1, 11, 11, 12, 12, 12),
    LEVEL_HIGH_PV(-1, -1, 12, 12, 13, 13, 13),
    LEVEL_CREATE_EXTRA(-1, -1, 13, 13, 14, 14, 14),
    LEVEL_IS_EXTRA(-1, -1, 14, 14, 15, 15, 15),
    LEVEL_HIGH_BEAUTY(-1, -1, 15, 15, 16, 16, 16),
    STONE_MALE_ONLY(-1, -1, -1, 16, 17, 17, 17),
    STONE_FEMALE_ONLY(-1, -1, -1, 17, 18, 18, 18),
    LEVEL_ITEM_DAY(-1, -1, -1, 18, 19, 19, 19),
    LEVEL_ITEM_NIGHT(-1, -1, -1, 19, 20, 20, 20),
    LEVEL_WITH_MOVE(-1, -1, -1, 20, 21, 21, 21),
    LEVEL_WITH_OTHER(-1, -1, -1, 21, 22, 22, 22),
    LEVEL_MALE_ONLY(-1, -1, -1, 22, 23, 23, 23),
    LEVEL_FEMALE_ONLY(-1, -1, -1, 23, 24, 24, 24),
    LEVEL_ELECTRIFIED_AREA(-1, -1, -1, 24, 25, 25, 25),
    LEVEL_MOSS_ROCK(-1, -1, -1, 25, 26, 26, 26),
    LEVEL_ICY_ROCK(-1, -1, -1, 26, 27, 27, 27),
    TRADE_SPECIAL(-1, -1, -1, -1, 7, 7, 7),
    FAIRY_AFFECTION(-1, -1, -1, -1, -1, 29, 29),
    LEVEL_WITH_DARK(-1, -1, -1, -1, -1, 30, 30),
    LEVEL_UPSIDE_DOWN(-1, -1, -1, -1, -1, 28, 28),
    LEVEL_RAIN(-1, -1, -1, -1, -1, 31, 31),
    LEVEL_DAY(-1, -1, -1, -1, -1, 32, 32),
    LEVEL_NIGHT(-1, -1, -1, -1, -1, 33, 33),
    LEVEL_FEMALE_ESPURR(-1, -1, -1, -1, -1, 34, 34),
    LEVEL_GAME(-1, -1, -1, -1, -1, -1, 36),
    LEVEL_DAY_GAME(-1, -1, -1, -1, -1, -1, 37),
    LEVEL_NIGHT_GAME(-1, -1, -1, -1, -1, -1, 38),
    LEVEL_SNOWY(-1, -1, -1, -1, -1, -1, 39),
    LEVEL_DUSK(-1, -1, -1, -1, -1, -1, 40),
    LEVEL_NIGHT_ULTRA(-1, -1, -1, -1, -1, -1, 41),
    STONE_ULTRA(-1, -1, -1, -1, -1, -1, 42),
    NONE(-1, -1, -1, -1, -1, -1, -1);
    /* @formatter:on */

    private int[] indexNumbers;
    private static EvolutionType[][] reverseIndexes = new EvolutionType[7][50];

    static {
        for (EvolutionType et : EvolutionType.values()) {
            for (int i = 0; i < et.indexNumbers.length; i++) {
                if (et.indexNumbers[i] > 0 && reverseIndexes[i][et.indexNumbers[i]] == null) {
                    reverseIndexes[i][et.indexNumbers[i]] = et;
                }
            }
        }
    }

    EvolutionType(int... indexes) {
        this.indexNumbers = indexes;
    }

    public int toIndex(int generation) {
        return indexNumbers[generation - 1];
    }

    public static EvolutionType fromIndex(int generation, int index) {
        return reverseIndexes[generation - 1][index];
    }

    public boolean usesLevel() {
        return (this == LEVEL) || (this == LEVEL_ATTACK_HIGHER) || (this == LEVEL_DEFENSE_HIGHER)
                || (this == LEVEL_ATK_DEF_SAME) || (this == LEVEL_LOW_PV) || (this == LEVEL_HIGH_PV)
                || (this == LEVEL_CREATE_EXTRA) || (this == LEVEL_IS_EXTRA) || (this == LEVEL_MALE_ONLY)
                || (this == LEVEL_FEMALE_ONLY) || (this == LEVEL_WITH_DARK)|| (this == LEVEL_UPSIDE_DOWN)
                || (this == LEVEL_RAIN) || (this == LEVEL_DAY) || (this == LEVEL_NIGHT) || (this == LEVEL_FEMALE_ESPURR)
                || (this == LEVEL_GAME) || (this == LEVEL_DAY_GAME) || (this == LEVEL_NIGHT_GAME)
                || (this == LEVEL_SNOWY) || (this == LEVEL_DUSK) || (this == LEVEL_NIGHT_ULTRA);
    }

    public boolean usesHappiness() {
        return (this == HAPPINESS) || (this == HAPPINESS_DAY) || (this == HAPPINESS_NIGHT);
    }

    public boolean skipSplitEvo() {
        return (this == LEVEL_HIGH_BEAUTY) || (this == LEVEL_NIGHT_ULTRA) || (this == STONE_ULTRA);
    }
}
