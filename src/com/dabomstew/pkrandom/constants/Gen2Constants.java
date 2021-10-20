package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  Gen2Constants.java - Constants for Gold/Silver/Crystal                --*/
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.Type;

public class Gen2Constants {

    public static final int vietCrystalCheckOffset = 0x63;

    public static final byte vietCrystalCheckValue = (byte) 0xF5;

    public static final String vietCrystalROMName = "Pokemon VietCrystal";

    public static final int pokemonCount = 251, moveCount = 251;

    public static final int baseStatsEntrySize = 0x20;

    public static final Type[] typeTable = constructTypeTable();

    public static final int bsHPOffset = 1, bsAttackOffset = 2, bsDefenseOffset = 3, bsSpeedOffset = 4,
            bsSpAtkOffset = 5, bsSpDefOffset = 6, bsPrimaryTypeOffset = 7, bsSecondaryTypeOffset = 8,
            bsCatchRateOffset = 9, bsCommonHeldItemOffset = 11, bsRareHeldItemOffset = 12, bsPicDimensionsOffset = 17,
            bsGrowthCurveOffset = 22, bsTMHMCompatOffset = 24, bsMTCompatOffset = 31;

    public static final String[] starterNames = new String[] { "CYNDAQUIL", "TOTODILE", "CHIKORITA" };

    public static final int fishingGroupCount = 12, pokesPerFishingGroup = 11, fishingGroupEntryLength = 3,
            timeSpecificFishingGroupCount = 11, pokesPerTSFishingGroup = 4;

    public static final int landEncounterSlots = 7, seaEncounterSlots = 3;

    public static final int oddEggPokemonCount = 14;

    public static final int tmCount = 50, hmCount = 7;

    public static final String mtMenuCancelString = "CANCEL";

    public static final byte mtMenuInitByte = (byte) 0x80;

    public static final int maxTrainerNameLength = 17;

    public static final int fleeingSetTwoOffset = 0xE, fleeingSetThreeOffset = 0x17;

    public static final int mapGroupCount = 26, mapsInLastGroup = 11;

    public static final List<Integer> requiredFieldTMs = Arrays.asList(4, 20, 22, 26, 28, 34, 35, 39,
            40, 43, 44, 46);

    public static final List<Integer> fieldMoves = Arrays.asList(
            Moves.cut, Moves.fly, Moves.surf, Moves.strength, Moves.flash, Moves.dig, Moves.teleport,
            Moves.whirlpool, Moves.waterfall, Moves.rockSmash, Moves.headbutt, Moves.sweetScent);

    public static final List<Integer> earlyRequiredHMMoves = Collections.singletonList(Moves.cut);

    // ban thief because trainers are broken with it (items are not returned).
    // ban transform because of Transform assumption glitch
    public static final List<Integer> bannedLevelupMoves = Arrays.asList(Moves.transform, Moves.thief);

    public static final List<Integer> brokenMoves = Arrays.asList(
            Moves.sonicBoom, Moves.dragonRage, Moves.hornDrill, Moves.fissure, Moves.guillotine);

    public static final List<Integer> illegalVietCrystalMoves = Arrays.asList(
            Moves.protect, Moves.rest, Moves.spikeCannon, Moves.detect);

    public static final int tmBlockOneIndex = 191, tmBlockOneSize = 4, tmBlockTwoIndex = 196, tmBlockTwoSize = 24,
            tmBlockThreeIndex = 221, tmBlockThreeSize = 22;

    public static final int luckyEggIndex = 0x7E;

    private static Type[] constructTypeTable() {
        Type[] table = new Type[256];
        table[0x00] = Type.NORMAL;
        table[0x01] = Type.FIGHTING;
        table[0x02] = Type.FLYING;
        table[0x03] = Type.POISON;
        table[0x04] = Type.GROUND;
        table[0x05] = Type.ROCK;
        table[0x07] = Type.BUG;
        table[0x08] = Type.GHOST;
        table[0x09] = Type.STEEL;
        table[0x14] = Type.FIRE;
        table[0x15] = Type.WATER;
        table[0x16] = Type.GRASS;
        table[0x17] = Type.ELECTRIC;
        table[0x18] = Type.PSYCHIC;
        table[0x19] = Type.ICE;
        table[0x1A] = Type.DRAGON;
        table[0x1B] = Type.DARK;
        return table;
    }

    public static byte typeToByte(Type type) {
        if (type == null) {
            return 0x13; // ???-type
        }
        switch (type) {
        case NORMAL:
            return 0x00;
        case FIGHTING:
            return 0x01;
        case FLYING:
            return 0x02;
        case POISON:
            return 0x03;
        case GROUND:
            return 0x04;
        case ROCK:
            return 0x05;
        case BUG:
            return 0x07;
        case GHOST:
            return 0x08;
        case FIRE:
            return 0x14;
        case WATER:
            return 0x15;
        case GRASS:
            return 0x16;
        case ELECTRIC:
            return 0x17;
        case PSYCHIC:
            return 0x18;
        case ICE:
            return 0x19;
        case DRAGON:
            return 0x1A;
        case STEEL:
            return 0x09;
        case DARK:
            return 0x1B;
        default:
            return 0; // normal by default
        }
    }

    public static ItemList allowedItems;

    public static ItemList nonBadItems;

    static {
        setupAllowedItems();
    }

    private static void setupAllowedItems() {
        allowedItems = new ItemList(249); // 250-255 are junk and cancel
        // Assorted key items
        allowedItems.banSingles(6, 54, 55, 58, 59, 61, 115, 116, 133, 134, 175, 178);
        allowedItems.banRange(66, 6);
        allowedItems.banRange(127, 4);
        // HMs
        allowedItems.banRange(243, 7);
        // Unused items (Teru-Samas and dummy TMs)
        allowedItems.banSingles(7, 25, 45, 50, 56, 90, 100, 120, 135, 136, 137);
        allowedItems.banSingles(141, 142, 145, 147, 148, 149, 153, 154, 155, 162, 171);
        allowedItems.banSingles(176, 179, 190, 220, 195);
        // Real TMs
        allowedItems.tmRange(tmBlockOneIndex, tmBlockOneSize);
        allowedItems.tmRange(tmBlockTwoIndex, tmBlockTwoSize);
        allowedItems.tmRange(tmBlockThreeIndex, tmBlockThreeSize);

        // non-bad items
        // ban specific pokemon hold items, berries, apricorns, mail
        nonBadItems = allowedItems.copy();
        nonBadItems
                .banSingles(0x1E, 0x23, 0x3C, 0x4B, 0x55, 0x59, 0x61, 0x63, 0x65, 0x69, 0x76, 0x9E, 0xA3, 0xAD, 0xB4);
        nonBadItems.banRange(0x5C, 2);
        nonBadItems.banRange(0xA7, 2);
        nonBadItems.banRange(0xB5, 9);
    }

    public static void universalTrainerTags(List<Trainer> allTrainers) {
        // Gym Leaders
        tbc(allTrainers, 1, 0, "GYM1");
        tbc(allTrainers, 3, 0, "GYM2");
        tbc(allTrainers, 2, 0, "GYM3");
        tbc(allTrainers, 4, 0, "GYM4");
        tbc(allTrainers, 7, 0, "GYM5");
        tbc(allTrainers, 6, 0, "GYM6");
        tbc(allTrainers, 5, 0, "GYM7");
        tbc(allTrainers, 8, 0, "GYM8");
        tbc(allTrainers, 17, 0, "GYM9");
        tbc(allTrainers, 18, 0, "GYM10");
        tbc(allTrainers, 19, 0, "GYM11");
        tbc(allTrainers, 21, 0, "GYM12");
        tbc(allTrainers, 26, 0, "GYM13");
        tbc(allTrainers, 35, 0, "GYM14");
        tbc(allTrainers, 46, 0, "GYM15");
        tbc(allTrainers, 64, 0, "GYM16");

        // Elite 4 & Red
        tbc(allTrainers, 11, 0, "ELITE1");
        tbc(allTrainers, 15, 0, "ELITE2");
        tbc(allTrainers, 13, 0, "ELITE3");
        tbc(allTrainers, 14, 0, "ELITE4");
        tbc(allTrainers, 16, 0, "CHAMPION");
        tbc(allTrainers, 63, 0, "UBER");

        // Silver
        // Order in rom is BAYLEEF, QUILAVA, CROCONAW teams
        // Starters go CYNDA, TOTO, CHIKO
        // So we want 0=CROCONAW/FERALI, 1=BAYLEEF/MEGAN, 2=QUILAVA/TYPHLO
        tbc(allTrainers, 9, 0, "RIVAL1-1");
        tbc(allTrainers, 9, 1, "RIVAL1-2");
        tbc(allTrainers, 9, 2, "RIVAL1-0");

        tbc(allTrainers, 9, 3, "RIVAL2-1");
        tbc(allTrainers, 9, 4, "RIVAL2-2");
        tbc(allTrainers, 9, 5, "RIVAL2-0");

        tbc(allTrainers, 9, 6, "RIVAL3-1");
        tbc(allTrainers, 9, 7, "RIVAL3-2");
        tbc(allTrainers, 9, 8, "RIVAL3-0");

        tbc(allTrainers, 9, 9, "RIVAL4-1");
        tbc(allTrainers, 9, 10, "RIVAL4-2");
        tbc(allTrainers, 9, 11, "RIVAL4-0");

        tbc(allTrainers, 9, 12, "RIVAL5-1");
        tbc(allTrainers, 9, 13, "RIVAL5-2");
        tbc(allTrainers, 9, 14, "RIVAL5-0");

        tbc(allTrainers, 42, 0, "RIVAL6-1");
        tbc(allTrainers, 42, 1, "RIVAL6-2");
        tbc(allTrainers, 42, 2, "RIVAL6-0");

        tbc(allTrainers, 42, 3, "RIVAL7-1");
        tbc(allTrainers, 42, 4, "RIVAL7-2");
        tbc(allTrainers, 42, 5, "RIVAL7-0");

        // Female Rocket Executive (Ariana)
        tbc(allTrainers, 55, 0, "THEMED:ARIANA");
        tbc(allTrainers, 55, 1, "THEMED:ARIANA");

        // others (unlabeled in this game, using HGSS names)
        tbc(allTrainers, 51, 2, "THEMED:PETREL");
        tbc(allTrainers, 51, 3, "THEMED:PETREL");

        tbc(allTrainers, 51, 1, "THEMED:PROTON");
        tbc(allTrainers, 31, 0, "THEMED:PROTON");

        // Sprout Tower
        tbc(allTrainers, 56, 0, "THEMED:SPROUTTOWER");
        tbc(allTrainers, 56, 1, "THEMED:SPROUTTOWER");
        tbc(allTrainers, 56, 2, "THEMED:SPROUTTOWER");
        tbc(allTrainers, 56, 3, "THEMED:SPROUTTOWER");
        tbc(allTrainers, 56, 6, "THEMED:SPROUTTOWER");
        tbc(allTrainers, 56, 7, "THEMED:SPROUTTOWER");
        tbc(allTrainers, 56, 8, "THEMED:SPROUTTOWER");
    }

    public static void goldSilverTags(List<Trainer> allTrainers) {
        tbc(allTrainers, 24, 0, "GYM1");
        tbc(allTrainers, 24, 1, "GYM1");
        tbc(allTrainers, 36, 4, "GYM2");
        tbc(allTrainers, 36, 5, "GYM2");
        tbc(allTrainers, 36, 6, "GYM2");
        tbc(allTrainers, 61, 0, "GYM2");
        tbc(allTrainers, 61, 3, "GYM2");
        tbc(allTrainers, 25, 0, "GYM3");
        tbc(allTrainers, 25, 1, "GYM3");
        tbc(allTrainers, 29, 0, "GYM3");
        tbc(allTrainers, 29, 1, "GYM3");
        tbc(allTrainers, 56, 4, "GYM4");
        tbc(allTrainers, 56, 5, "GYM4");
        tbc(allTrainers, 57, 0, "GYM4");
        tbc(allTrainers, 57, 1, "GYM4");
        tbc(allTrainers, 50, 1, "GYM5");
        tbc(allTrainers, 50, 3, "GYM5");
        tbc(allTrainers, 50, 4, "GYM5");
        tbc(allTrainers, 50, 6, "GYM5");
        tbc(allTrainers, 58, 0, "GYM7");
        tbc(allTrainers, 58, 1, "GYM7");
        tbc(allTrainers, 58, 2, "GYM7");
        tbc(allTrainers, 33, 0, "GYM7");
        tbc(allTrainers, 33, 1, "GYM7");
        tbc(allTrainers, 27, 2, "GYM8");
        tbc(allTrainers, 27, 4, "GYM8");
        tbc(allTrainers, 27, 3, "GYM8");
        tbc(allTrainers, 28, 2, "GYM8");
        tbc(allTrainers, 28, 3, "GYM8");
        tbc(allTrainers, 54, 17, "GYM9");
        tbc(allTrainers, 38, 20, "GYM10");
        tbc(allTrainers, 39, 17, "GYM10");
        tbc(allTrainers, 39, 18, "GYM10");
        tbc(allTrainers, 49, 2, "GYM11");
        tbc(allTrainers, 43, 1, "GYM11");
        tbc(allTrainers, 32, 2, "GYM11");
        tbc(allTrainers, 61, 4, "GYM12");
        tbc(allTrainers, 61, 5, "GYM12");
        tbc(allTrainers, 25, 8, "GYM12");
        tbc(allTrainers, 53, 18, "GYM12");
        tbc(allTrainers, 29, 13, "GYM12");
        tbc(allTrainers, 25, 2, "GYM13");
        tbc(allTrainers, 25, 5, "GYM13");
        tbc(allTrainers, 53, 4, "GYM13");
        tbc(allTrainers, 54, 4, "GYM13");
        tbc(allTrainers, 57, 5, "GYM14");
        tbc(allTrainers, 57, 6, "GYM14");
        tbc(allTrainers, 52, 1, "GYM14");
        tbc(allTrainers, 52, 10, "GYM14");
    }

    public static void crystalTags(List<Trainer> allTrainers) {
        tbc(allTrainers, 24, 0, "GYM1");
        tbc(allTrainers, 24, 1, "GYM1");
        tbc(allTrainers, 36, 4, "GYM2");
        tbc(allTrainers, 36, 5, "GYM2");
        tbc(allTrainers, 36, 6, "GYM2");
        tbc(allTrainers, 61, 0, "GYM2");
        tbc(allTrainers, 61, 3, "GYM2");
        tbc(allTrainers, 25, 0, "GYM3");
        tbc(allTrainers, 25, 1, "GYM3");
        tbc(allTrainers, 29, 0, "GYM3");
        tbc(allTrainers, 29, 1, "GYM3");
        tbc(allTrainers, 56, 4, "GYM4");
        tbc(allTrainers, 56, 5, "GYM4");
        tbc(allTrainers, 57, 0, "GYM4");
        tbc(allTrainers, 57, 1, "GYM4");
        tbc(allTrainers, 50, 1, "GYM5");
        tbc(allTrainers, 50, 3, "GYM5");
        tbc(allTrainers, 50, 4, "GYM5");
        tbc(allTrainers, 50, 6, "GYM5");
        tbc(allTrainers, 58, 0, "GYM7");
        tbc(allTrainers, 58, 1, "GYM7");
        tbc(allTrainers, 58, 2, "GYM7");
        tbc(allTrainers, 33, 0, "GYM7");
        tbc(allTrainers, 33, 1, "GYM7");
        tbc(allTrainers, 27, 2, "GYM8");
        tbc(allTrainers, 27, 4, "GYM8");
        tbc(allTrainers, 27, 3, "GYM8");
        tbc(allTrainers, 28, 2, "GYM8");
        tbc(allTrainers, 28, 3, "GYM8");
        tbc(allTrainers, 54, 17, "GYM9");
        tbc(allTrainers, 38, 20, "GYM10");
        tbc(allTrainers, 39, 17, "GYM10");
        tbc(allTrainers, 39, 18, "GYM10");
        tbc(allTrainers, 49, 2, "GYM11");
        tbc(allTrainers, 43, 1, "GYM11");
        tbc(allTrainers, 32, 2, "GYM11");
        tbc(allTrainers, 61, 4, "GYM12");
        tbc(allTrainers, 61, 5, "GYM12");
        tbc(allTrainers, 25, 8, "GYM12");
        tbc(allTrainers, 53, 18, "GYM12");
        tbc(allTrainers, 29, 13, "GYM12");
        tbc(allTrainers, 25, 2, "GYM13");
        tbc(allTrainers, 25, 5, "GYM13");
        tbc(allTrainers, 53, 4, "GYM13");
        tbc(allTrainers, 54, 4, "GYM13");
        tbc(allTrainers, 57, 5, "GYM14");
        tbc(allTrainers, 57, 6, "GYM14");
        tbc(allTrainers, 52, 1, "GYM14");
        tbc(allTrainers, 52, 10, "GYM14");
    }

    private static void tbc(List<Trainer> allTrainers, int classNum, int number, String tag) {
        int currnum = -1;
        for (Trainer t : allTrainers) {
            // adjusted to not change the above but use 0-indexing properly
            if (t.trainerclass == classNum - 1) {
                currnum++;
                if (currnum == number) {
                    t.tag = tag;
                    return;
                }
            }
        }
    }

}
