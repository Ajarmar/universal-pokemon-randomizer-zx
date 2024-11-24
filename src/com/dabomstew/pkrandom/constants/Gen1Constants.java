package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  Gen1Constants.java - Constants for Red/Green/Blue/Yellow              --*/
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
import java.util.HashMap;
import java.util.List;

import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.Type;

public class Gen1Constants {

    public static final int baseStatsEntrySize = 0x1C;

    public static final int bsHPOffset = 1, bsAttackOffset = 2, bsDefenseOffset = 3, bsSpeedOffset = 4,
            bsSpecialOffset = 5, bsPrimaryTypeOffset = 6, bsSecondaryTypeOffset = 7, bsCatchRateOffset = 8,
            bsExpYieldOffset = 9, bsFrontSpriteOffset = 11, bsLevel1MovesOffset = 15, bsGrowthCurveOffset = 19,
            bsTMHMCompatOffset = 20;

    public static final int encounterTableEnd = 0xFFFF, encounterTableSize = 10, yellowSuperRodTableSize = 4;

    public static final int trainerClassCount = 47;

    public static final int champRivalOffsetFromGymLeaderMoves = 0x44;

    public static final int tmCount = 50, hmCount = 5;

    public static final int[] gymLeaderTMs = new int[] { 34, 11, 24, 21, 6, 46, 38, 27 };

    public static final int[] tclassesCounts = new int[] { 21, 47 };

    public static final List<Integer> singularTrainers = Arrays.asList(28, 32, 33, 34, 35, 36, 37, 38, 39, 43, 45, 46);

    public static final List<Integer> bannedMovesWithXAccBanned = Arrays.asList(
            Moves.sonicBoom, Moves.dragonRage, Moves.spore);

    public static final List<Integer> bannedMovesWithoutXAccBanned = Arrays.asList(
            Moves.sonicBoom, Moves.dragonRage, Moves.spore, Moves.hornDrill, Moves.fissure, Moves.guillotine);

    // ban transform because of Transform assumption glitch
    public static final List<Integer> bannedLevelupMoves = Collections.singletonList(Moves.transform);

    public static final List<Integer> fieldMoves = Arrays.asList(
            Moves.cut, Moves.fly, Moves.surf, Moves.strength, Moves.flash, Moves.dig, Moves.teleport);

    public static final int damagePoison20PercentEffect = 2, damageAbsorbEffect = 3, damageBurn10PercentEffect = 4,
            damageFreeze10PercentEffect = 5, damageParalyze10PercentEffect = 6, dreamEaterEffect = 8,
            noDamageAtkPlusOneEffect = 10, noDamageDefPlusOneEffect = 11, noDamageSpecialPlusOneEffect = 13,
            noDamageEvasionPlusOneEffect = 15, noDamageAtkMinusOneEffect = 18, noDamageDefMinusOneEffect = 19,
            noDamageSpeMinusOneEffect = 20, noDamageAccuracyMinusOneEffect = 22, flinch10PercentEffect = 31,
            noDamageSleepEffect = 32, damagePoison40PercentEffect = 33, damageBurn30PercentEffect = 34,
            damageFreeze30PercentEffect = 35, damageParalyze30PercentEffect = 36, flinch30PercentEffect = 37,
            chargeEffect = 39, flyEffect = 43, damageRecoilEffect = 48, noDamageConfusionEffect = 49,
            noDamageAtkPlusTwoEffect = 50, noDamageDefPlusTwoEffect = 51, noDamageSpePlusTwoEffect = 52,
            noDamageSpecialPlusTwoEffect = 53, noDamageDefMinusTwoEffect = 59, noDamagePoisonEffect = 66,
            noDamageParalyzeEffect = 67, damageAtkMinusOneEffect = 68, damageDefMinusOneEffect = 69,
            damageSpeMinusOneEffect = 70, damageSpecialMinusOneEffect = 71, damageConfusionEffect = 76,
            twineedleEffect = 77, hyperBeamEffect = 80;

    // Taken from critical_hit_moves.asm; we could read this from the ROM, but it's easier to hardcode it.
    public static final List<Integer> increasedCritMoves = Arrays.asList(Moves.karateChop, Moves.razorLeaf, Moves.crabhammer, Moves.slash);

    public static final List<Integer> earlyRequiredHMs = Collections.singletonList(Moves.cut);

    public static final int hmsStartIndex = Gen1Items.hm01, tmsStartIndex = Gen1Items.tm01;

    public static final List<Integer> requiredFieldTMs = Arrays.asList(3, 4, 8, 10, 12, 14, 16, 19, 20,
            22, 25, 26, 30, 40, 43, 44, 45, 47);

    public static final int towerMapsStartIndex = 0x90, towerMapsEndIndex = 0x94;

    public static final String guaranteedCatchPrefix = "CF7EFE01";

    public static final Type[] typeTable = constructTypeTable();

    private static Type[] constructTypeTable() {
        Type[] table = new Type[0x20];
        table[0x00] = Type.NORMAL;
        table[0x01] = Type.FIGHTING;
        table[0x02] = Type.FLYING;
        table[0x03] = Type.POISON;
        table[0x04] = Type.GROUND;
        table[0x05] = Type.ROCK;
        table[0x07] = Type.BUG;
        table[0x08] = Type.GHOST;
        table[0x14] = Type.FIRE;
        table[0x15] = Type.WATER;
        table[0x16] = Type.GRASS;
        table[0x17] = Type.ELECTRIC;
        table[0x18] = Type.PSYCHIC;
        table[0x19] = Type.ICE;
        table[0x1A] = Type.DRAGON;
        return table;
    }

    public static byte typeToByte(Type type) {
        for (int i = 0; i < typeTable.length; i++) {
            if (typeTable[i] == type) {
                return (byte) i;
            }
        }
        return (byte) 0;
    }

    public static final ItemList allowedItems = setupAllowedItems();

    private static ItemList setupAllowedItems() {
        ItemList allowedItems = new ItemList(Gen1Items.tm50); // 251-255 are junk TMs
        // Assorted key items & junk
        // 23/01/2014: ban fake PP Up
        allowedItems.banSingles(Gen1Items.townMap, Gen1Items.bicycle, Gen1Items.questionMark7,
                Gen1Items.safariBall, Gen1Items.pokedex, Gen1Items.oldAmber, Gen1Items.cardKey, Gen1Items.ppUpGlitch,
                Gen1Items.coin, Gen1Items.ssTicket, Gen1Items.goldTeeth);
        allowedItems.banRange(Gen1Items.boulderBadge, 8);
        allowedItems.banRange(Gen1Items.domeFossil, 5);
        allowedItems.banRange(Gen1Items.coinCase, 10);
        // Unused
        allowedItems.banRange(Gen1Items.unused84, 112);
        // HMs
        allowedItems.banRange(hmsStartIndex, hmCount);
        // Real TMs
        allowedItems.tmRange(tmsStartIndex, tmCount);
        return allowedItems;
    }

    public static void tagTrainersUniversal(List<Trainer> trs) {
        // Gym Leaders
        tbc(trs, 34, 0, "GYM1");
        tbc(trs, 35, 0, "GYM2");
        tbc(trs, 36, 0, "GYM3");
        tbc(trs, 37, 0, "GYM4");
        tbc(trs, 38, 0, "GYM5");
        tbc(trs, 40, 0, "GYM6");
        tbc(trs, 39, 0, "GYM7");
        tbc(trs, 29, 2, "GYM8");

        // Other giovanni teams
        tbc(trs, 29, 0, "GIO1");
        tbc(trs, 29, 1, "GIO2");

        // Elite 4
        tbc(trs, 44, 0, "ELITE1");
        tbc(trs, 33, 0, "ELITE2");
        tbc(trs, 46, 0, "ELITE3");
        tbc(trs, 47, 0, "ELITE4");
    }

    public static void tagTrainersRB(List<Trainer> trs) {
        // Gary Battles
        tbc(trs, 25, 0, "RIVAL1-0");
        tbc(trs, 25, 1, "RIVAL1-1");
        tbc(trs, 25, 2, "RIVAL1-2");

        tbc(trs, 25, 3, "RIVAL2-0");
        tbc(trs, 25, 4, "RIVAL2-1");
        tbc(trs, 25, 5, "RIVAL2-2");

        tbc(trs, 25, 6, "RIVAL3-0");
        tbc(trs, 25, 7, "RIVAL3-1");
        tbc(trs, 25, 8, "RIVAL3-2");

        tbc(trs, 42, 0, "RIVAL4-0");
        tbc(trs, 42, 1, "RIVAL4-1");
        tbc(trs, 42, 2, "RIVAL4-2");

        tbc(trs, 42, 3, "RIVAL5-0");
        tbc(trs, 42, 4, "RIVAL5-1");
        tbc(trs, 42, 5, "RIVAL5-2");

        tbc(trs, 42, 6, "RIVAL6-0");
        tbc(trs, 42, 7, "RIVAL6-1");
        tbc(trs, 42, 8, "RIVAL6-2");

        tbc(trs, 42, 9, "RIVAL7-0");
        tbc(trs, 42, 10, "RIVAL7-1");
        tbc(trs, 42, 11, "RIVAL7-2");

        tbc(trs, 43, 0, "RIVAL8-0");
        tbc(trs, 43, 1, "RIVAL8-1");
        tbc(trs, 43, 2, "RIVAL8-2");

        // Gym Trainers
        tbc(trs, 5, 0, "GYM1");

        tbc(trs, 15, 0, "GYM2");
        tbc(trs, 6, 0, "GYM2");

        tbc(trs, 4, 7, "GYM3");
        tbc(trs, 20, 0, "GYM3");
        tbc(trs, 41, 2, "GYM3");

        tbc(trs, 3, 16, "GYM4");
        tbc(trs, 3, 17, "GYM4");
        tbc(trs, 6, 10, "GYM4");
        tbc(trs, 18, 0, "GYM4");
        tbc(trs, 18, 1, "GYM4");
        tbc(trs, 18, 2, "GYM4");
        tbc(trs, 32, 0, "GYM4");

        tbc(trs, 21, 2, "GYM5");
        tbc(trs, 21, 3, "GYM5");
        tbc(trs, 21, 6, "GYM5");
        tbc(trs, 21, 7, "GYM5");
        tbc(trs, 22, 0, "GYM5");
        tbc(trs, 22, 1, "GYM5");

        tbc(trs, 19, 0, "GYM6");
        tbc(trs, 19, 1, "GYM6");
        tbc(trs, 19, 2, "GYM6");
        tbc(trs, 19, 3, "GYM6");
        tbc(trs, 45, 21, "GYM6");
        tbc(trs, 45, 22, "GYM6");
        tbc(trs, 45, 23, "GYM6");

        tbc(trs, 8, 8, "GYM7");
        tbc(trs, 8, 9, "GYM7");
        tbc(trs, 8, 10, "GYM7");
        tbc(trs, 8, 11, "GYM7");
        tbc(trs, 11, 3, "GYM7");
        tbc(trs, 11, 4, "GYM7");
        tbc(trs, 11, 5, "GYM7");

        tbc(trs, 22, 2, "GYM8");
        tbc(trs, 22, 3, "GYM8");
        tbc(trs, 24, 5, "GYM8");
        tbc(trs, 24, 6, "GYM8");
        tbc(trs, 24, 7, "GYM8");
        tbc(trs, 31, 0, "GYM8");
        tbc(trs, 31, 8, "GYM8");
        tbc(trs, 31, 9, "GYM8");
    }

    public static void tagTrainersYellow(List<Trainer> trs) {
        // Rival Battles
        tbc(trs, 25, 0, "IRIVAL");

        tbc(trs, 25, 1, "RIVAL1-0");

        tbc(trs, 25, 2, "RIVAL2-0");

        tbc(trs, 42, 0, "RIVAL3-0");

        tbc(trs, 42, 1, "RIVAL4-0");
        tbc(trs, 42, 2, "RIVAL4-1");
        tbc(trs, 42, 3, "RIVAL4-2");

        tbc(trs, 42, 4, "RIVAL5-0");
        tbc(trs, 42, 5, "RIVAL5-1");
        tbc(trs, 42, 6, "RIVAL5-2");

        tbc(trs, 42, 7, "RIVAL6-0");
        tbc(trs, 42, 8, "RIVAL6-1");
        tbc(trs, 42, 9, "RIVAL6-2");

        tbc(trs, 43, 0, "RIVAL7-0");
        tbc(trs, 43, 1, "RIVAL7-1");
        tbc(trs, 43, 2, "RIVAL7-2");

        // Rocket Jessie & James
        tbc(trs, 30, 41, "THEMED:JESSIE&JAMES");
        tbc(trs, 30, 42, "THEMED:JESSIE&JAMES");
        tbc(trs, 30, 43, "THEMED:JESSIE&JAMES");
        tbc(trs, 30, 44, "THEMED:JESSIE&JAMES");

        // Gym Trainers
        tbc(trs, 5, 0, "GYM1");

        tbc(trs, 6, 0, "GYM2");
        tbc(trs, 15, 0, "GYM2");

        tbc(trs, 4, 7, "GYM3");
        tbc(trs, 20, 0, "GYM3");
        tbc(trs, 41, 2, "GYM3");

        tbc(trs, 3, 16, "GYM4");
        tbc(trs, 3, 17, "GYM4");
        tbc(trs, 6, 10, "GYM4");
        tbc(trs, 18, 0, "GYM4");
        tbc(trs, 18, 1, "GYM4");
        tbc(trs, 18, 2, "GYM4");
        tbc(trs, 32, 0, "GYM4");

        tbc(trs, 21, 2, "GYM5");
        tbc(trs, 21, 3, "GYM5");
        tbc(trs, 21, 6, "GYM5");
        tbc(trs, 21, 7, "GYM5");
        tbc(trs, 22, 0, "GYM5");
        tbc(trs, 22, 1, "GYM5");

        tbc(trs, 19, 0, "GYM6");
        tbc(trs, 19, 1, "GYM6");
        tbc(trs, 19, 2, "GYM6");
        tbc(trs, 19, 3, "GYM6");
        tbc(trs, 45, 21, "GYM6");
        tbc(trs, 45, 22, "GYM6");
        tbc(trs, 45, 23, "GYM6");

        tbc(trs, 8, 8, "GYM7");
        tbc(trs, 8, 9, "GYM7");
        tbc(trs, 8, 10, "GYM7");
        tbc(trs, 8, 11, "GYM7");
        tbc(trs, 11, 3, "GYM7");
        tbc(trs, 11, 4, "GYM7");
        tbc(trs, 11, 5, "GYM7");

        tbc(trs, 22, 2, "GYM8");
        tbc(trs, 22, 3, "GYM8");
        tbc(trs, 24, 5, "GYM8");
        tbc(trs, 24, 6, "GYM8");
        tbc(trs, 24, 7, "GYM8");
        tbc(trs, 31, 0, "GYM8");
        tbc(trs, 31, 8, "GYM8");
        tbc(trs, 31, 9, "GYM8");
    }

    public static final HashMap<String, Type> gymAndEliteThemes = setupGymAndEliteThemes();

    private static HashMap<String, Type> setupGymAndEliteThemes() {
        HashMap<String, Type> themeMap = new HashMap<>();
        themeMap.put("ELITE1", Type.ICE); //Lorelei
        themeMap.put("ELITE2", Type.FIGHTING); //Bruno
        themeMap.put("ELITE3", Type.GHOST); //Agatha
        themeMap.put("ELITE4", Type.DRAGON); //Lance
        themeMap.put("GYM1", Type.ROCK); //Brock
        themeMap.put("GYM2", Type.WATER); //Misty
        themeMap.put("GYM3", Type.ELECTRIC); //Lt. Surge
        themeMap.put("GYM4", Type.GRASS); //Erika
        themeMap.put("GYM5", Type.POISON); //Koga
        themeMap.put("GYM6", Type.PSYCHIC); //Sabrina
        themeMap.put("GYM7", Type.FIRE); //Blaine
        themeMap.put("GYM8", Type.GROUND); //Giovanni
        return themeMap;
    }

    private static void tbc(List<Trainer> allTrainers, int classNum, int number, String tag) {
        int currnum = -1;
        for (Trainer t : allTrainers) {
            if (t.trainerclass == classNum) {
                currnum++;
                if (currnum == number) {
                    t.tag = tag;
                    return;
                }
            }
        }
    }

}
