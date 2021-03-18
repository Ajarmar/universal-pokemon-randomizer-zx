package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  Gen4Constants.java - Constants for DPPt and HGSS                      --*/
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.dabomstew.pkrandom.pokemon.*;

public class Gen4Constants {

    public static final int Type_DP = 0;
    public static final int Type_Plat = 1;
    public static final int Type_HGSS = 2;

    public static final int pokemonCount = 493, moveCount = 467;
    private static final int dpFormeCount = 5, platHgSsFormeCount = 12;
    public static final int formeOffset = 2;

    public static final int bsHPOffset = 0, bsAttackOffset = 1, bsDefenseOffset = 2, bsSpeedOffset = 3,
            bsSpAtkOffset = 4, bsSpDefOffset = 5, bsPrimaryTypeOffset = 6, bsSecondaryTypeOffset = 7,
            bsCatchRateOffset = 8, bsCommonHeldItemOffset = 12, bsRareHeldItemOffset = 14, bsGrowthCurveOffset = 19,
            bsAbility1Offset = 22, bsAbility2Offset = 23, bsTMHMCompatOffset = 28;

    public static final byte[] hgssStarterCodeSuffix = { 0x03, 0x03, 0x1A, 0x12, 0x1, 0x23, 0x0, 0x0 };

    public static final int[] hgssFilesWithRivalScript = { 7, 23, 96, 110, 819, 850, 866 };

    public static final byte[] hgssRivalScriptMagic = { (byte) 0xCE, 0x00, 0x0C, (byte) 0x80, 0x11, 0x00, 0x0C,
            (byte) 0x80, (byte) 152, 0, 0x1C, 0x00, 0x05 };

    public static final int[] ptFilesWithRivalScript = { 31, 36, 112, 123, 186, 427, 429, 1096 };

    public static final int[] dpFilesWithRivalScript = { 34, 90, 118, 180, 195, 394 };

    public static final byte[] dpptRivalScriptMagic = { (byte) 0xDE, 0x00, 0x0C, (byte) 0x80, 0x11, 0x00, 0x0C,
            (byte) 0x80, (byte) 0x83, 0x01, 0x1C, 0x00, 0x01 };

    public static final byte[] dpptTagBattleScriptMagic1 = { (byte) 0xDE, 0x00, 0x0C, (byte) 0x80, 0x28, 0x00, 0x04,
            (byte) 0x80 };

    public static final byte[] dpptTagBattleScriptMagic2 = { 0x11, 0x00, 0x0C, (byte) 0x80, (byte) 0x86, 0x01, 0x1C,
            0x00, 0x01 };

    public static final int[] ptFilesWithTagScript = { 2, 136, 201, 236 };

    public static final int[] dpFilesWithTagScript = { 2, 131, 230 };

    public static final int dpStarterStringIndex = 19, ptStarterStringIndex = 36;

    public static final int chikoritaIndex = 152, cyndaquilIndex = 155, totodileIndex = 158, turtwigIndex = 387,
            chimcharIndex = 390, piplupIndex = 393, slowpokeIndex = 79, eeveeIndex = 133;

    public static final int fossilCount = 7;

    public static final String dpptTMDataPrefix = "D100D200D300D400", hgssTMDataPrefix = "1E003200";

    public static final int tmCount = 92, hmCount = 8;

    public static final int tmItemOffset = 328;

    public static final int textCharsPerLine = 40;

    public static final String dpItemPalettesPrefix = "8D018E01210132018D018F0122013301",
            pthgssItemPalettesPrefix = "8D018E01210133018D018F0122013401";

    public static final int evolutionMethodCount = 26;

    public static final int sunStoneIndex = 80, moonStoneIndex = 81, waterStoneIndex = 84, leafStoneIndex = 85, dawnStoneIndex = 109;

    public static final int highestAbilityIndex = 123;

    public static final Map<Integer,List<Integer>> abilityVariations = setupAbilityVariations();

    private static Map<Integer,List<Integer>> setupAbilityVariations() {
        Map<Integer,List<Integer>> map = new HashMap<>();
        map.put(15,Arrays.asList(15,72)); // Insomnia/Vital Spirit
        map.put(29,Arrays.asList(29,73)); // Clear Body/White Smoke
        map.put(37,Arrays.asList(37,74)); // Huge Power/Pure Power
        map.put(4,Arrays.asList(4,75)); // Battle Armor/Shell Armor
        map.put(13,Arrays.asList(13,76)); // Cloud Nine/Air Lock
        map.put(111,Arrays.asList(111,116)); // Filter/Solid Rock

        return map;
    }

    public static final int dpptSetVarScript = 0x28, hgssSetVarScript = 0x29;

    public static final int scriptListTerminator = 0xFD13;

    public static final int itemScriptVariable = 0x8008;
    
    public static final int luckyEggIndex = 0xE7;

    private static final String dpShopDataPrefix = "4D10040295100402C910040201110402391104027511040285AF0302A5AF0302";
    private static final String ptShopDataPrefix = "017F0402397F0402817F0402B57F0402ED7F0402258004026180040281800402";
    private static final String hgssShopDataPrefix = "2D8D0402758D0402BD8D0402F18D0402298E0402618E0402998E0402B98E0402";

    public static String getShopDataPrefix(int romType) {
        if (romType == Type_DP) {
            return dpShopDataPrefix;
        } else if (romType == Type_Plat) {
            return ptShopDataPrefix;
        } else if (romType == Type_HGSS) {
            return hgssShopDataPrefix;
        }
        return "";
    }

    private static List<String> dpShopNames = Arrays.asList(
            "Sunyshore Secondary",
            "Jubilife Secondary",
            "Floaroma Secondary",
            "Oreburgh Secondary",
            "Eterna Secondary",
            "Eterna Herbs",
            "Snowpoint Secondary",
            "Solaceon Secondary",
            "Pastoria Secondary",
            "Celestic Secondary",
            "Hearthome Secondary",
            "Canalave Secondary",
            "???",
            "???",
            "Veilstone Department Store Vitamins",
            "Veilstone Department Store TMs 1",
            "???",
            "???",
            "???",
            "???",
            "Veilstone Department Store TMs 2",
            "???",
            "???",
            "???",
            "Pokemon League Secondary",
            "Veilstone Department Store X Items",
            "Veilstone Department Store Healing",
            "Veilstone Department Store Balls Etc."
    );

    private static List<String> ptShopNames = Arrays.asList(
            "Jubilife Secondary",
            "Sunyshore Secondary",
            "Floaroma Secondary",
            "Oreburgh Secondary",
            "Eterna Herbs",
            "Canalave Secondary",
            "Pastoria Secondary",
            "Celestic Secondary",
            "Snowpoint Secondary",
            "Solaceon Secondary",
            "Eterna Secondary",
            "Hearthome Secondary",
            "Veilstone Department Store B1 Berries",
            "???",
            "Veilstone Department Store Vitamins",
            "???",
            "Veilstone Department Store TMs 1",
            "???",
            "???",
            "???",
            "???",
            "Veilstone Department Store TMs 2",
            "???",
            "???",
            "???",
            "Pokemon League Secondary",
            "Veilstone Department Store X Items",
            "Veilstone Department Store Healing",
            "Veilstone Department Store Balls Etc."
    );

    private static List<String> hgssShopNames = Arrays.asList(
            "Cherrygrove Secondary",
            "Cerulean Secondary",
            "Ecruteak Secondary",
            "Celadon Department Store Mail",
            "Saffron Secondary",
            "Violet Secondary",
            "Blackthorn Secondary",
            "Olivine Secondary",
            "Fuchsia Secondary",
            "Lavender Secondary",
            "Pewter Secondary",
            "Viridian Secondary",
            "Azalea Secondary",
            "Mahogany Before Hideout",
            "Safari Zone Gate Southwest",
            "Goldenrod Herb Shop",
            "Cianwood Pharmacy",
            "???",
            "???",
            "Goldenrod Department Store Vitamins",
            "Celadon Department Store Vitamins",
            "Mt. Moon Square",
            "???",
            "???",
            "???",
            "???",
            "???",
            "???",
            "???",
            "???",
            "???",
            "Goldenrod Department Store X Items",
            "Celadon Department Store X Items",
            "Mahogany After Hideout",
            "Goldenrod Department Store Healing",
            "Celadon Department Store Healing",
            "Goldenrod Department Store Balls Etc.",
            "Goldenrod TMs",
            "Celadon Department Store Balls Etc.",
            "Celadon TMs"
    );

    public static List<String> getShopNames(int romType) {
        if (romType == Type_DP) {
            return dpShopNames;
        } else if (romType == Type_Plat) {
            return ptShopNames;
        } else if (romType == Type_HGSS) {
            return hgssShopNames;
        }
        return null;
    }

    public static final List<Integer> evolutionItems = Arrays.asList(80,81,82,83,84,85,107,108,109,
            110,221,226,227,233,235,252,321,322,323,324,325,326,327);

    public static final Map<Integer,String> formeSuffixes = setupFormeSuffixes();
    public static final Map<Integer,FormeInfo> formeMappings = setupFormeMappings();
    public static final Map<Integer,Integer> cosmeticForms = setupCosmeticForms();

    private static final Map<Integer,Map<Integer,String>> formeSuffixesByBaseForme = setupFormeSuffixesByBaseForme();
    private static final Map<Integer,String> dummyFormeSuffixes = setupDummyFormeSuffixes();

    private static final Map<Integer,Map<Integer,Integer>> absolutePokeNumsByBaseForme = setupAbsolutePokeNumsByBaseForme();
    private static final Map<Integer,Integer> dummyAbsolutePokeNums = setupDummyAbsolutePokeNums();

    public static String getFormeSuffixByBaseForme(int baseForme, int formNum) {
        return formeSuffixesByBaseForme.getOrDefault(baseForme,dummyFormeSuffixes).getOrDefault(formNum,"");
    }

    public static Integer getAbsolutePokeNumByBaseForme(int baseForme, int formNum) {
        return absolutePokeNumsByBaseForme.getOrDefault(baseForme,dummyAbsolutePokeNums).getOrDefault(formNum,baseForme);
    }

    // The original slot each of the 20 "alternate" slots is mapped to
    // swarmx2, dayx2, nightx2, pokeradarx4, GBAx10
    // NOTE: in the game data there are 6 fillers between pokeradar and GBA

    public static final int[] dpptAlternateSlots = new int[] { 0, 1, 2, 3, 2, 3, 4, 5, 10, 11, 8, 9, 8, 9, 8, 9, 8, 9,
            8, 9 };

    public static final String[] dpptWaterSlotSetNames = new String[] { "Surfing", "Filler", "Old Rod", "Good Rod",
            "Super Rod" };

    public static final String[] hgssTimeOfDayNames = new String[] { "Morning", "Day", "Night" };

    public static final String[] hgssNonGrassSetNames = new String[] { "", "Surfing", "Rock Smash", "Old Rod",
            "Good Rod", "Super Rod" };
    public static final int hgssGoodRodReplacementIndex = 3, hgssSuperRodReplacementIndex = 1;

    public static final MoveCategory[] moveCategoryIndices = { MoveCategory.PHYSICAL, MoveCategory.SPECIAL,
            MoveCategory.STATUS };

    public static byte moveCategoryToByte(MoveCategory cat) {
        switch (cat) {
        case PHYSICAL:
            return 0;
        case SPECIAL:
            return 1;
        case STATUS:
        default:
            return 2;
        }
    }

    public static final List<Integer> dpRequiredFieldTMs = Arrays.asList(2, 3, 5, 9, 12, 19, 23, 28,
            34, 39, 41, 43, 46, 47, 49, 50, 62, 69, 79, 80, 82, 84, 85, 87);

    public static final List<Integer> ptRequiredFieldTMs = Arrays.asList(2, 3, 5, 7, 9, 11, 12, 18, 19,
            23, 28, 34, 37, 39, 41, 43, 46, 47, 49, 50, 62, 69, 79, 80, 82, 84, 85, 87);

    // DPPt:
    // cut, fly, surf, strength, flash, dig, teleport, waterfall,
    // rock smash, sweet scent, defog, rock climb
    public static final List<Integer> dpptFieldMoves = Arrays.asList(15, 19, 57, 70, 148, 91, 100, 127, 249, 230, 432,
            431);

    public static final List<Integer> hgssFieldMoves = Arrays.asList(15, 19, 57, 70, 148, 91, 100, 250, 127, 249, 29,
            230, 431);
    // DPPt: rock smash, cut
    public static final List<Integer> dpptEarlyRequiredHMMoves = Arrays.asList(249, 15);
    // HGSS: just cut
    public static final List<Integer> hgssEarlyRequiredHMMoves = Collections.singletonList(15);

    public static ItemList allowedItems, nonBadItems;
    public static List<Integer> regularShopItems, opShopItems;

    public static final String shedinjaSpeciesLocator = "492080000090281C0521";

    public static final int ilexForestScriptFile = 92, ilexForestStringsFile = 115, headbuttTutorLongTextIndex = 16;
    public static final List<Integer> headbuttTutorScriptOffsets = Arrays.asList(0xF55, 0xFC5, 0x100A),
            headbuttTutorTextIndices = Arrays.asList(16, 17, 19, 23, 25, 26);

    private static final String doubleBattleFixPrefixDP = "022912D90221214201", doubleBattleFixPrefixPt = "022919D90221214205",
            doubleBattleFixPrefixHGSS = "2C2815D00221214201";

    public static final String feebasLevelPrefixDPPt = "019813B0F0BD", honeyTreeLevelPrefixDPPt = "F0BDF0B589B0051C0C1C";

    private static final String runningShoesCheckPrefixDPPt = "281C0C24", runningShoesCheckPrefixHGSS = "301C0C24";

    private static final int trophyGardenGrassEncounterIndexDP = 304, trophyGardenGrassEncounterIndexPt = 308;
    private static final List<Integer> marshGrassEncounterIndicesDP = Arrays.asList(76, 82, 88, 94, 100, 102),
            marshGrassEncounterIndicesPt = Arrays.asList(76, 82, 88, 94, 100, 106);

    public static final int[] dpptOverworldDexMaps = new int[] {
            1,  2,  3,  4,  5, -1, -1,  6, -1,  7, // 0-9 (cities, pkmn league, wind/ironworks)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-19 (all mt coronet)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 20-29 (mt coronet, great marsh, solaceon ruins)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 30-39 (all solaceon ruins)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 40-49 (solaceon ruins/v.road)
            -1, -1, -1, -1, -1, -1,  8, -1, -1, -1, // 50-59 (v.road, stark mountain outer then inner, sendoff spring)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 60-69 (unknown, turnback cave)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 70-79 (all turnback cave)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 80-89 (all unknown)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 90-99 (all unknown)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 100-109 (unknown, snowpoint temple)
            -1, -1, -1, -1, -1, -1, -1, -1,  9, -1, // 110-119 (various dungeons, iron island outer/inner)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 120-129 (rest of iron island inner, old chateau)
            -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, // 130-139 (old chateau, inner lakes, lakefronts)
            12, 13, 14, 15, 16, 17, 18, 19, 20, 21, // 140-149 (first few routes)
            22, -1, -1, -1, -1, -1, 23, 24, 25, 26, // 150-159 (route 209 + lost tower, more routes)
            27, 28, 29, 30, 31, 32, 33, 34, 35, 36, // 160-169 (routes; 220 is skipped until later)
            37, 38, 39, 40, 41, 42, 43, 44, 45, 46, // 170-179 (last few land routes, towns, resort area, first sea route)
            47, 48, 49,                             // 180-182 (other sea routes)
    };

    public static final int[] dpptDungeonDexMaps = new int[] {
            -1, -1, -1, -1, -1,  1,  1, -1,  2, -1, // 0-9 (cities, pkmn league, wind/ironworks, mine/forest)
            3,  3,  3,  3,  3,  3,  3,  3,  3,  3, // 10-19 (all mt coronet)
            3,  3,  3,  4,  4,  4,  4,  4,  4,  5, // 20-29 (mt coronet, great marsh, solaceon ruins)
            5,  5,  5,  5,  5,  5,  5,  5,  5,  5, // 30-39 (all solaceon ruins)
            5,  5,  5,  5,  5,  5,  5,  6,  6,  6, // 40-49 (solaceon ruins/v.road)
            6,  6,  6,  7,  8,  8, -1,  9,  9, 10, // 50-59 (v.road, stark mountain outer then inner, sendoff spring)
            -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, // 60-69 (unknown, turnback cave)
            10, 10, 10, 10, 10, 10, 10, 10, 10, 10, // 70-79 (all turnback cave)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 80-89 (all unknown)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 90-99 (all unknown)
            -1, -1, -1, -1, -1, -1, 11, 11, 11, 11, // 100-109 (unknown, snowpoint temple)
            11, 11, 12, 12, 13, 13, 13, 14, -1, 15, // 110-119 (various dungeons, iron island outer/inner)
            15, 15, 15, 15, 15, 16, 16, 16, 16, 16, // 120-129 (rest of iron island inner, old chateau)
            16, 16, 16, 16, 17, 17, 18, 19, -1, -1, // 130-139 (old chateau, inner lakes, lakefronts)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 140-149 (first few routes)
            -1, 20, 20, 20, 20, 20, -1, -1, -1, -1, // 150-159 (route 209 + lost tower, more routes)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 160-169 (routes; 220 is skipped until later)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 170-179 (last few land routes, towns, resort area, first sea route)
            -1, -1, -1,                             // 180-182 (other sea routes)
    };

    public static final int[] hgssOverworldDexMaps = new int[] {
            1,  2,  3,  4,  5,  6, -1, -1,  7, -1, // 0-9 (first few cities/routes, sprout tower + alph)
            -1, -1, -1, -1, -1, -1, -1,  8, -1, -1, // 10-19 (more alph, union cave, r33, slowpoke)
            -1,  9, 10, -1, -1, 11, 12, 13, -1, -1, // 20-29 (ilex, routes, natpark, routes, burned)
            -1, -1, -1, -1, -1, -1, -1, -1, 14, 15, // 30-39 (bell tower, routes)
            16, 17, 18, -1, -1, -1, -1, -1, -1, -1, // 40-49 (olivine, routes, whirl islands, missing slots)
            -1, 19, 20, -1, -1, -1, -1, 21, 22, 23, // 50-59 (missing, cianwood, routes, mortar)
            -1, -1, -1, -1, -1, 24, -1, 25, 26, -1, // 60-69 (ice path, missing, blackthorn, dragons, routes, dark)
            -1, 27, -1, -1, -1, -1, -1, -1, -1, -1, // 70-79 (dark, route 47, moon, seafoam, silver cave)
            -1, -1, -1, -1, -1, 28, -1, -1, -1, -1, // 80-89 (more silver cave, cliff stuff, random bell tower)
            -1, -1, 29, 30, 31, 32, 33, 34, 35, 36, // 90-99 (missing, saf zone, kanto routes/cities)
            37, 38, 39, 40, 41, 42, -1, -1, -1, -1, // 100-109 (more cities, some routes, more moon, RT)
            -1, 43, 44, 45, 46, 47, 48, 49, 50, 51, // 110-119 (vroad, routes 1-9)
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, // 120-129 (routes 10-21)
            62, 63, -1, -1, -1, -1, 64, -1, -1, -1, // 130-139 (last 2 routes, tohjo, DC, VR, route 2 north, VF, CC)
            -1, -1,                                 // 140-141 (cerulean cave)
    };

    public static final int[] hgssDungeonDexMaps = new int[] {
            -1, -1, -1, -1, -1, -1,  1,  1, -1,  2, // 0-9 (first few cities/routes, sprout tower + alph)
            2,  2,  2,  2,  3,  3,  3, -1,  4,  4, // 10-19 (more alph, union cave, r33, slowpoke)
            5, -1, -1,  6,  -1, -1, -1, -1,  7,  7, // 20-29 (ilex, routes, natpark, routes, burned)
            8,  8,  8,  8,  8,  8,  8,  8, -1, -1, // 30-39 (bell tower, routes)
            -1, -1, -1,  9,  9, -1,  9, -1,  9, -1, // 40-49 (olivine, routes, whirl islands, missing slots)
            -1, -1, -1, 10, 10, 10, 10, -1, -1, -1, // 50-59 (missing, cianwood, routes, mortar)
            11, 11, 11, 11, -1, -1, 12, -1, -1, 13, // 60-69 (ice path, missing, blackthorn, dragons, routes, dark)
            13, -1, 14, 14, 15, 15, 15, 15, 15, 16, // 70-79 (dark, route 47, moon, seafoam, silver cave)
            16, 16, 17, 18,  8, -1, 16, 16, 16, 16, // 80-89 (more silver cave, cliff stuff, random bell tower)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 90-99 (missing, saf zone, kanto routes/cities)
            -1, -1, -1, -1, -1, -1, 14, 14, 20, 20, // 100-109 (more cities, some routes, more moon, RT)
            21, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 110-119 (vroad, routes 1-9)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 120-129 (routes 10-21)
            -1, -1, 22, 23, 21, 21, -1, 24, -1, 25, // 130-139 (last 2 routes, tohjo, DC, VR, route 2 north, VF, CC)
            25, 25,                                 // 140-141 (cerulean cave)
    };

    public static final int[] hgssHeadbuttOverworldDexMaps = new int[] {
            43, 44, 45, 46, 47, 48, 49, 50, 53, 29, // Routes 1-12, skipping 9 and 10
            54, 55, 56, 59, 61, 63, 40, 41, 42,  2, // Routes 13-15, Route 18, Route 22, Routes 25-29
             4,  5,  7,  8,  9, 10, 11, 12, 14, 15, // Routes 30-39
            20, 21, 23, 25, 26, 32, 33, 65, 34, 35, // Routes 42-46, first five Kanto cities
            36, 37,  1,  3,  6, 66, 13, 22, 28, 60, // Remaining Kanto cities, Johto cities, Lake of Rage, Mt Silver, Route 21
            -1, -1, -1, 27, 39, 67, 64, 57, -1,     // National Park, Ilex/Viridian Forest, Routes 47-48, Safari Zone Gate, Routes 2 (north) and 16, Mt Silver Cave
    };

    public static final int[] hgssHeadbuttDungeonDexMaps = new int[] {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // Routes 1-12, skipping 9 and 10
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // Routes 13-15, Route 18, Route 22, Routes 25-29
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // Routes 30-39
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // Routes 42-46, first five Kanto cities
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // Remaining Kanto cities, Johto cities, Lake of Rage, Mt Silver, Route 21
             6,  5, 24, -1, -1, -1, -1, -1, 16,     // National Park, Ilex/Viridian Forest, Routes 47-48, Safari Zone Gate, Routes 2 (north) and 16, Mt Silver Cave
    };

    public static final int pokedexAreaDataSize = 495;
    public static final int dpptMtCoronetDexIndex = 3, dpptGreatMarshDexIndex = 4, dpptTrophyGardenDexIndex = 14, dpptFloaromaMeadowDexIndex = 21;
    public static final List<Integer> dpptOverworldHoneyTreeDexIndicies = Arrays.asList(6, 7, 17, 18, 19, 20, 21, 22, 23, 24, 26, 27, 28, 29, 30, 31, 34, 36, 37, 50);
    public static final int hgssNationalParkDexIndex = 6;

    static {
        setupAllowedItems();
    }

    private static void setupAllowedItems() {
        allowedItems = new ItemList(536);
        // Key items + version exclusives
        allowedItems.banRange(428, 109);
        // Unknown blank items or version exclusives
        allowedItems.banRange(112, 23);
        // HMs
        allowedItems.banRange(420, 8);
        // TMs
        allowedItems.tmRange(328, 92);

        // non-bad items
        // ban specific pokemon hold items, berries, apricorns, mail
        nonBadItems = allowedItems.copy();

        nonBadItems.banSingles(0x6F, 0x70, 0xEC, 0x9B);
        nonBadItems.banRange(0x46,2);
        nonBadItems.banRange(0x5F, 4); // mulch
        nonBadItems.banRange(0x87, 2); // orbs
        nonBadItems.banRange(0x89, 12); // mails
        nonBadItems.banRange(0x9F, 54); // berries DansGame
        nonBadItems.banRange(0x100, 4); // pokemon specific
        nonBadItems.banRange(0x104, 5); // contest scarves
        nonBadItems.banRange(0x110, 2); // toxic orb, flame orb
        nonBadItems.banRange(0x116, 2); // iron ball, lagging tail
        nonBadItems.banSingles(0x120); // sticky barb

        regularShopItems = new ArrayList<>();

        regularShopItems.addAll(IntStream.rangeClosed(2,4).boxed().collect(Collectors.toList()));
        regularShopItems.addAll(IntStream.rangeClosed(0x11,0x1C).boxed().collect(Collectors.toList()));
        regularShopItems.addAll(IntStream.rangeClosed(0x4C,0x4F).boxed().collect(Collectors.toList()));

        opShopItems = new ArrayList<>();

        // "Money items" etc
        opShopItems.add(0x32);
        opShopItems.addAll(IntStream.rangeClosed(0x56,0x5C).boxed().collect(Collectors.toList()));
        opShopItems.add(0x6A);
        opShopItems.add(0xE7);
    }

    public static String getDoubleBattleFixPrefix(int romType) {
        if (romType == Gen4Constants.Type_DP) {
            return doubleBattleFixPrefixDP;
        } else if (romType == Gen4Constants.Type_Plat) {
            return doubleBattleFixPrefixPt;
        } else {
            return doubleBattleFixPrefixHGSS;
        }
    }

    public static String getRunWithoutRunningShoesPrefix(int romType) {
        if (romType == Gen4Constants.Type_DP || romType == Gen4Constants.Type_Plat) {
            return runningShoesCheckPrefixDPPt;
        } else {
            return runningShoesCheckPrefixHGSS;
        }
    }

    public static int getTrophyGardenGrassEncounterIndex(int romType) {
        if (romType == Gen4Constants.Type_DP) {
            return trophyGardenGrassEncounterIndexDP;
        } else {
            return trophyGardenGrassEncounterIndexPt;
        }
    }

    public static List<Integer> getMarshGrassEncounterIndices(int romType) {
        if (romType == Gen4Constants.Type_DP) {
            return marshGrassEncounterIndicesDP;
        } else {
            return marshGrassEncounterIndicesPt;
        }
    }

    public static final Map<Integer,Integer> balancedItemPrices = Stream.of(new Integer[][] {
            // Skip item index 0. All prices divided by 10
            {1, 300}, // Master Ball
            {2, 120}, // Ultra Ball
            {3, 60}, // Great Ball
            {4, 20}, // Poké Ball
            {5, 50}, // Safari Ball
            {6, 100}, // Net Ball
            {7, 100}, // Dive Ball
            {8, 100}, // Nest Ball
            {9, 100}, // Repeat Ball
            {10, 100}, // Timer Ball
            {11, 100}, // Luxury Ball
            {12, 20}, // Premier Ball
            {13, 100}, // Dusk Ball
            {14, 30}, // Heal Ball
            {15, 100}, // Quick Ball
            {16, 20}, // Cherish Ball
            {17, 30}, // Potion
            {18, 10}, // Antidote
            {19, 25}, // Burn Heal
            {20, 25}, // Ice Heal
            {21, 25}, // Awakening
            {22, 20}, // Parlyz Heal
            {23, 300}, // Full Restore
            {24, 250}, // Max Potion
            {25, 120}, // Hyper Potion
            {26, 70}, // Super Potion
            {27, 60}, // Full Heal
            {28, 150}, // Revive
            {29, 400}, // Max Revive
            {30, 40}, // Fresh Water
            {31, 60}, // Soda Pop
            {32, 70}, // Lemonade
            {33, 80}, // Moomoo Milk
            {34, 40}, // EnergyPowder
            {35, 110}, // Energy Root
            {36, 45}, // Heal Powder
            {37, 280}, // Revival Herb
            {38, 300}, // Ether
            {39, 450}, // Max Ether
            {40, 1500}, // Elixir
            {41, 1800}, // Max Elixir
            {42, 45}, // Lava Cookie
            {43, 10}, // Berry Juice
            {44, 1000}, // Sacred Ash
            {45, 980}, // HP Up
            {46, 980}, // Protein
            {47, 980}, // Iron
            {48, 980}, // Carbos
            {49, 980}, // Calcium
            {50, 1000}, // Rare Candy
            {51, 980}, // PP Up
            {52, 980}, // Zinc
            {53, 2490}, // PP Max
            {54, 45}, // Old Gateau
            {55, 70}, // Guard Spec.
            {56, 65}, // Dire Hit
            {57, 50}, // X Attack
            {58, 55}, // X Defend
            {59, 35}, // X Speed
            {60, 95}, // X Accuracy
            {61, 35}, // X Special
            {62, 35}, // X Sp. Def
            {63, 100}, // Poké Doll
            {64, 100}, // Fluffy Tail
            {65, 2}, // Blue Flute
            {66, 2}, // Yellow Flute
            {67, 2}, // Red Flute
            {68, 2}, // Black Flute
            {69, 2}, // White Flute
            {70, 2}, // Shoal Salt
            {71, 2}, // Shoal Shell
            {72, 40}, // Red Shard
            {73, 40}, // Blue Shard
            {74, 40}, // Yellow Shard
            {75, 40}, // Green Shard
            {76, 50}, // Super Repel
            {77, 70}, // Max Repel
            {78, 55}, // Escape Rope
            {79, 35}, // Repel
            {80, 300}, // Sun Stone
            {81, 300}, // Moon Stone
            {82, 300}, // Fire Stone
            {83, 300}, // Thunderstone
            {84, 300}, // Water Stone
            {85, 300}, // Leaf Stone
            {86, 50}, // TinyMushroom
            {87, 500}, // Big Mushroom
            {88, 140}, // Pearl
            {89, 750}, // Big Pearl
            {90, 200}, // Stardust
            {91, 980}, // Star Piece
            {92, 1000}, // Nugget
            {93, 500}, // Heart Scale
            {94, 50}, // Honey
            {95, 20}, // Growth Mulch
            {96, 20}, // Damp Mulch
            {97, 20}, // Stable Mulch
            {98, 20}, // Gooey Mulch
            {99, 500}, // Root Fossil
            {100, 500}, // Claw Fossil
            {101, 500}, // Helix Fossil
            {102, 500}, // Dome Fossil
            {103, 800}, // Old Amber
            {104, 500}, // Armor Fossil
            {105, 500}, // Skull Fossil
            {106, 1000}, // Rare Bone
            {107, 300}, // Shiny Stone
            {108, 300}, // Dusk Stone
            {109, 300}, // Dawn Stone
            {110, 300}, // Oval Stone
            {111, 210}, // Odd Keystone
            {112, 1000}, // Griseous Orb
            {113, 0}, // unknown
            {114, 0}, // unknown
            {115, 0}, // unknown
            {116, 100}, // Douse Drive
            {117, 100}, // Shock Drive
            {118, 100}, // Burn Drive
            {119, 100}, // Chill Drive
            {120, 0}, // unknown
            {121, 0}, // unknown
            {122, 0}, // unknown
            {123, 0}, // unknown
            {124, 0}, // unknown
            {125, 0}, // unknown
            {126, 0}, // unknown
            {127, 0}, // unknown
            {128, 0}, // unknown
            {129, 0}, // unknown
            {130, 0}, // unknown
            {131, 0}, // unknown
            {132, 0}, // unknown
            {133, 0}, // unknown
            {134, 15}, // Sweet Heart
            {135, 1000}, // Adamant Orb
            {136, 1000}, // Lustrous Orb
            {137, 5}, // Greet Mail
            {138, 5}, // Favored Mail
            {139, 5}, // RSVP Mail
            {140, 5}, // Thanks Mail
            {141, 5}, // Inquiry Mail
            {142, 5}, // Like Mail
            {143, 5}, // Reply Mail
            {144, 5}, // BridgeMail S
            {145, 5}, // BridgeMail D
            {146, 5}, // BridgeMail T
            {147, 5}, // BridgeMail V
            {148, 5}, // BridgeMail M
            {149, 20}, // Cheri Berry
            {150, 25}, // Chesto Berry
            {151, 10}, // Pecha Berry
            {152, 25}, // Rawst Berry
            {153, 25}, // Aspear Berry
            {154, 300}, // Leppa Berry
            {155, 5}, // Oran Berry
            {156, 20}, // Persim Berry
            {157, 50}, // Lum Berry
            {158, 50}, // Sitrus Berry
            {159, 10}, // Figy Berry
            {160, 10}, // Wiki Berry
            {161, 10}, // Mago Berry
            {162, 10}, // Aguav Berry
            {163, 10}, // Iapapa Berry
            {164, 50}, // Razz Berry
            {165, 50}, // Bluk Berry
            {166, 50}, // Nanab Berry
            {167, 50}, // Wepear Berry
            {168, 50}, // Pinap Berry
            {169, 50}, // Pomeg Berry
            {170, 50}, // Kelpsy Berry
            {171, 50}, // Qualot Berry
            {172, 50}, // Hondew Berry
            {173, 50}, // Grepa Berry
            {174, 50}, // Tamato Berry
            {175, 50}, // Cornn Berry
            {176, 50}, // Magost Berry
            {177, 50}, // Rabuta Berry
            {178, 50}, // Nomel Berry
            {179, 50}, // Spelon Berry
            {180, 50}, // Pamtre Berry
            {181, 50}, // Watmel Berry
            {182, 50}, // Durin Berry
            {183, 50}, // Belue Berry
            {184, 100}, // Occa Berry
            {185, 100}, // Passho Berry
            {186, 100}, // Wacan Berry
            {187, 100}, // Rindo Berry
            {188, 100}, // Yache Berry
            {189, 100}, // Chople Berry
            {190, 100}, // Kebia Berry
            {191, 100}, // Shuca Berry
            {192, 100}, // Coba Berry
            {193, 100}, // Payapa Berry
            {194, 100}, // Tanga Berry
            {195, 100}, // Charti Berry
            {196, 100}, // Kasib Berry
            {197, 100}, // Haban Berry
            {198, 100}, // Colbur Berry
            {199, 100}, // Babiri Berry
            {200, 100}, // Chilan Berry
            {201, 100}, // Liechi Berry
            {202, 100}, // Ganlon Berry
            {203, 100}, // Salac Berry
            {204, 100}, // Petaya Berry
            {205, 100}, // Apicot Berry
            {206, 100}, // Lansat Berry
            {207, 100}, // Starf Berry
            {208, 100}, // Enigma Berry
            {209, 100}, // Micle Berry
            {210, 100}, // Custap Berry
            {211, 100}, // Jaboca Berry
            {212, 100}, // Rowap Berry
            {213, 300}, // BrightPowder
            {214, 100}, // White Herb
            {215, 300}, // Macho Brace
            {216, 600}, // Exp. Share
            {217, 450}, // Quick Claw
            {218, 100}, // Soothe Bell
            {219, 100}, // Mental Herb
            {220, 1000}, // Choice Band
            {221, 500}, // King's Rock
            {222, 200}, // SilverPowder
            {223, 1500}, // Amulet Coin
            {224, 100}, // Cleanse Tag
            {225, 20}, // Soul Dew
            {226, 300}, // DeepSeaTooth
            {227, 300}, // DeepSeaScale
            {228, 20}, // Smoke Ball
            {229, 20}, // Everstone
            {230, 300}, // Focus Band
            {231, 1000}, // Lucky Egg
            {232, 500}, // Scope Lens
            {233, 300}, // Metal Coat
            {234, 1000}, // Leftovers
            {235, 300}, // Dragon Scale
            {236, 10}, // Light Ball
            {237, 200}, // Soft Sand
            {238, 200}, // Hard Stone
            {239, 200}, // Miracle Seed
            {240, 200}, // BlackGlasses
            {241, 200}, // Black Belt
            {242, 200}, // Magnet
            {243, 200}, // Mystic Water
            {244, 200}, // Sharp Beak
            {245, 200}, // Poison Barb
            {246, 200}, // NeverMeltIce
            {247, 200}, // Spell Tag
            {248, 200}, // TwistedSpoon
            {249, 200}, // Charcoal
            {250, 200}, // Dragon Fang
            {251, 200}, // Silk Scarf
            {252, 300}, // Up-Grade
            {253, 600}, // Shell Bell
            {254, 200}, // Sea Incense
            {255, 300}, // Lax Incense
            {256, 1}, // Lucky Punch
            {257, 1}, // Metal Powder
            {258, 50}, // Thick Club
            {259, 20}, // Stick
            {260, 10}, // Red Scarf
            {261, 10}, // Blue Scarf
            {262, 10}, // Pink Scarf
            {263, 10}, // Green Scarf
            {264, 10}, // Yellow Scarf
            {265, 150}, // Wide Lens
            {266, 200}, // Muscle Band
            {267, 200}, // Wise Glasses
            {268, 600}, // Expert Belt
            {269, 150}, // Light Clay
            {270, 1000}, // Life Orb
            {271, 100}, // Power Herb
            {272, 150}, // Toxic Orb
            {273, 150}, // Flame Orb
            {274, 1}, // Quick Powder
            {275, 200}, // Focus Sash
            {276, 150}, // Zoom Lens
            {277, 300}, // Metronome
            {278, 100}, // Iron Ball
            {279, 100}, // Lagging Tail
            {280, 150}, // Destiny Knot
            {281, 500}, // Black Sludge
            {282, 20}, // Icy Rock
            {283, 20}, // Smooth Rock
            {284, 20}, // Heat Rock
            {285, 20}, // Damp Rock
            {286, 150}, // Grip Claw
            {287, 1000}, // Choice Scarf
            {288, 150}, // Sticky Barb
            {289, 300}, // Power Bracer
            {290, 300}, // Power Belt
            {291, 300}, // Power Lens
            {292, 300}, // Power Band
            {293, 300}, // Power Anklet
            {294, 300}, // Power Weight
            {295, 50}, // Shed Shell
            {296, 150}, // Big Root
            {297, 1000}, // Choice Specs
            {298, 200}, // Flame Plate
            {299, 200}, // Splash Plate
            {300, 200}, // Zap Plate
            {301, 200}, // Meadow Plate
            {302, 200}, // Icicle Plate
            {303, 200}, // Fist Plate
            {304, 200}, // Toxic Plate
            {305, 200}, // Earth Plate
            {306, 200}, // Sky Plate
            {307, 200}, // Mind Plate
            {308, 200}, // Insect Plate
            {309, 200}, // Stone Plate
            {310, 200}, // Spooky Plate
            {311, 200}, // Draco Plate
            {312, 200}, // Dread Plate
            {313, 200}, // Iron Plate
            {314, 200}, // Odd Incense
            {315, 200}, // Rock Incense
            {316, 100}, // Full Incense
            {317, 200}, // Wave Incense
            {318, 200}, // Rose Incense
            {319, 1500}, // Luck Incense
            {320, 100}, // Pure Incense
            {321, 300}, // Protector
            {322, 300}, // Electirizer
            {323, 300}, // Magmarizer
            {324, 300}, // Dubious Disc
            {325, 300}, // Reaper Cloth
            {326, 500}, // Razor Claw
            {327, 500}, // Razor Fang
            {328, 300}, // TMs
            {329, 300},
            {330, 300},
            {331, 150},
            {332, 100},
            {333, 300},
            {334, 200},
            {335, 150},
            {336, 200},
            {337, 200},
            {338, 200},
            {339, 150},
            {340, 300},
            {341, 550},
            {342, 750},
            {343, 200},
            {344, 200},
            {345, 200},
            {346, 300},
            {347, 200},
            {348, 100},
            {349, 300},
            {350, 300},
            {351, 300},
            {352, 550},
            {353, 300},
            {354, 100},
            {355, 200},
            {356, 300},
            {357, 300},
            {358, 300},
            {359, 100},
            {360, 200},
            {361, 300},
            {362, 300},
            {363, 300},
            {364, 200},
            {365, 550},
            {366, 200},
            {367, 300},
            {368, 150},
            {369, 300},
            {370, 200},
            {371, 300},
            {372, 300},
            {373, 200},
            {374, 300},
            {375, 300},
            {376, 150},
            {377, 550},
            {378, 200},
            {379, 550},
            {380, 300},
            {381, 200},
            {382, 300},
            {383, 200},
            {384, 300},
            {385, 200},
            {386, 300},
            {387, 300},
            {388, 200},
            {389, 300},
            {390, 200},
            {391, 750},
            {392, 300},
            {393, 300},
            {394, 100},
            {395, 750},
            {396, 150},
            {397, 100},
            {398, 300},
            {399, 300},
            {400, 200},
            {401, 300},
            {402, 150},
            {403, 200},
            {404, 150},
            {405, 150},
            {406, 300},
            {407, 300},
            {408, 300},
            {409, 100},
            {410, 200},
            {411, 300},
            {412, 300},
            {413, 300},
            {414, 150},
            {415, 300},
            {416, 300},
            {417, 200},
            {418, 300},
            {419, 550},
            {420, 0}, // HM01
            {421, 0}, // HM02
            {422, 0}, // HM03
            {423, 0}, // HM04
            {424, 0}, // HM05
            {425, 0}, // HM06
            {426, 0}, // HM07
            {427, 0}, // HM08
            {428, 0}, // Explorer Kit
            {429, 0}, // Loot Sack
            {430, 0}, // Rule Book
            {431, 0}, // Poké Radar
            {432, 0}, // Point Card
            {433, 0}, // Journal
            {434, 0}, // Seal Case
            {435, 0}, // Fashion Case
            {436, 0}, // Seal Bag
            {437, 0}, // Pal Pad
            {438, 0}, // Works Key
            {439, 0}, // Old Charm
            {440, 0}, // Galactic Key
            {441, 0}, // Red Chain
            {442, 0}, // Town Map
            {443, 0}, // Vs. Seeker
            {444, 0}, // Coin Case
            {445, 0}, // Old Rod
            {446, 0}, // Good Rod
            {447, 0}, // Super Rod
            {448, 0}, // Sprayduck
            {449, 0}, // Poffin Case
            {450, 0}, // Bicycle
            {451, 0}, // Suite Key
            {452, 0}, // Oak's Letter
            {453, 0}, // Lunar Wing
            {454, 0}, // Member Card
            {455, 0}, // Azure Flute
            {456, 0}, // S.S. Ticket
            {457, 0}, // Contest Pass
            {458, 0}, // Magma Stone
            {459, 0}, // Parcel
            {460, 0}, // Coupon 1
            {461, 0}, // Coupon 2
            {462, 0}, // Coupon 3
            {463, 0}, // Storage Key
            {464, 0}, // SecretPotion
            {465, 0}, // Vs. Recorder
            {466, 0}, // Gracidea
            {467, 0}, // Secret Key
            {468, 0}, // Apricorn Box
            {469, 0}, // Unown Report
            {470, 0}, // Berry Pots
            {471, 0}, // Dowsing MCHN
            {472, 0}, // Blue Card
            {473, 0}, // SlowpokeTail
            {474, 0}, // Clear Bell
            {475, 0}, // Card Key
            {476, 0}, // Basement Key
            {477, 0}, // SquirtBottle
            {478, 0}, // Red Scale
            {479, 0}, // Lost Item
            {480, 0}, // Pass
            {481, 0}, // Machine Part
            {482, 0}, // Silver Wing
            {483, 0}, // Rainbow Wing
            {484, 0}, // Mystery Egg
            {485, 2}, // Red Apricorn
            {486, 2}, // Blu Apricorn
            {487, 2}, // Ylw Apricorn
            {488, 2}, // Grn Apricorn
            {489, 2}, // Pnk Apricorn
            {490, 2}, // Wht Apricorn
            {491, 2}, // Blk Apricorn
            {492, 30}, // Fast Ball
            {493, 30}, // Level Ball
            {494, 30}, // Lure Ball
            {495, 30}, // Heavy Ball
            {496, 30}, // Love Ball
            {497, 30}, // Friend Ball
            {498, 30}, // Moon Ball
            {499, 30}, // Sport Ball
            {500, 0}, // Park Ball
            {501, 0}, // Photo Album
            {502, 0}, // GB Sounds
            {503, 0}, // Tidal Bell
            {504, 0}, // RageCandyBar
            {505, 0}, // Data Card 01
            {506, 0}, // Data Card 02
            {507, 0}, // Data Card 03
            {508, 0}, // Data Card 04
            {509, 0}, // Data Card 05
            {510, 0}, // Data Card 06
            {511, 0}, // Data Card 07
            {512, 0}, // Data Card 08
            {513, 0}, // Data Card 09
            {514, 0}, // Data Card 10
            {515, 0}, // Data Card 11
            {516, 0}, // Data Card 12
            {517, 0}, // Data Card 13
            {518, 0}, // Data Card 14
            {519, 0}, // Data Card 15
            {520, 0}, // Data Card 16
            {521, 0}, // Data Card 17
            {522, 0}, // Data Card 18
            {523, 0}, // Data Card 19
            {524, 0}, // Data Card 20
            {525, 0}, // Data Card 21
            {526, 0}, // Data Card 22
            {527, 0}, // Data Card 23
            {528, 0}, // Data Card 24
            {529, 0}, // Data Card 25
            {530, 0}, // Data Card 26
            {531, 0}, // Data Card 27
            {532, 0}, // Jade Orb
            {533, 0}, // Lock Capsule
            {534, 0}, // Red Orb
            {535, 0}, // Blue Orb
            {536, 0}, // Enigma Stone
    }).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

    public static final Type[] typeTable = constructTypeTable();

    private static Type[] constructTypeTable() {
        Type[] table = new Type[256];
        table[0x00] = Type.NORMAL;
        table[0x01] = Type.FIGHTING;
        table[0x02] = Type.FLYING;
        table[0x03] = Type.POISON;
        table[0x04] = Type.GROUND;
        table[0x05] = Type.ROCK;
        table[0x06] = Type.BUG;
        table[0x07] = Type.GHOST;
        table[0x08] = Type.STEEL;
        table[0x0A] = Type.FIRE;
        table[0x0B] = Type.WATER;
        table[0x0C] = Type.GRASS;
        table[0x0D] = Type.ELECTRIC;
        table[0x0E] = Type.PSYCHIC;
        table[0x0F] = Type.ICE;
        table[0x10] = Type.DRAGON;
        table[0x11] = Type.DARK;
        return table;
    }

    public static byte typeToByte(Type type) {
        if (type == null) {
            return 0x09; // ???-type
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
            return 0x06;
        case GHOST:
            return 0x07;
        case FIRE:
            return 0x0A;
        case WATER:
            return 0x0B;
        case GRASS:
            return 0x0C;
        case ELECTRIC:
            return 0x0D;
        case PSYCHIC:
            return 0x0E;
        case ICE:
            return 0x0F;
        case DRAGON:
            return 0x10;
        case STEEL:
            return 0x08;
        case DARK:
            return 0x11;
        default:
            return 0; // normal by default
        }
    }

    public static int getFormeCount(int romType) {
        if (romType == Type_DP) {
            return dpFormeCount;
        } else if (romType == Type_Plat || romType == Type_HGSS) {
            return platHgSsFormeCount;
        }
        return 0;
    }

    private static Map<Integer,String> setupFormeSuffixes() {
        Map<Integer,String> formeSuffixes = new HashMap<>();
        formeSuffixes.put(496,"-A"); // Deoxys-A
        formeSuffixes.put(497,"-D"); // Deoxys-D
        formeSuffixes.put(498,"-S"); // Deoxys-S
        formeSuffixes.put(499,"-S"); // Wormadam-S
        formeSuffixes.put(500,"-T"); // Wormadam-T
        formeSuffixes.put(501,"-O"); // Giratina-O
        formeSuffixes.put(502,"-S"); // Shaymin-S
        formeSuffixes.put(503,"-H"); // Rotom-H
        formeSuffixes.put(504,"-W"); // Rotom-W
        formeSuffixes.put(505,"-Fr"); // Rotom-Fr
        formeSuffixes.put(506,"-Fa"); // Rotom-Fa
        formeSuffixes.put(507,"-M"); // Rotom-M
        return formeSuffixes;
    }

    private static Map<Integer,FormeInfo> setupFormeMappings() {
        Map<Integer,FormeInfo> formeMappings = new TreeMap<>();

        formeMappings.put(496,new FormeInfo(386,1, 0));
        formeMappings.put(497,new FormeInfo(386,2, 0));
        formeMappings.put(498,new FormeInfo(386,3, 0));
        formeMappings.put(499,new FormeInfo(413,1, 0));
        formeMappings.put(500,new FormeInfo(413,2, 0));
        formeMappings.put(501,new FormeInfo(487,1, 0));
        formeMappings.put(502,new FormeInfo(492,1, 0));
        formeMappings.put(503,new FormeInfo(479,1, 0));
        formeMappings.put(504,new FormeInfo(479,2, 0));
        formeMappings.put(505,new FormeInfo(479,3, 0));
        formeMappings.put(506,new FormeInfo(479,4, 0));
        formeMappings.put(507,new FormeInfo(479,5, 0));

        return formeMappings;
    }

    private static Map<Integer,Integer> setupCosmeticForms() {
        Map<Integer,Integer> cosmeticForms = new TreeMap<>();

        cosmeticForms.put(201,28);
        cosmeticForms.put(412,3);
        cosmeticForms.put(422,2);
        cosmeticForms.put(423,2);
        return cosmeticForms;
    }

    private static Map<Integer,Map<Integer,String>> setupFormeSuffixesByBaseForme() {
        Map<Integer,Map<Integer,String>> map = new HashMap<>();

        Map<Integer,String> deoxysMap = new HashMap<>();
        deoxysMap.put(1,"-A");
        deoxysMap.put(2,"-D");
        deoxysMap.put(3,"-S");
        map.put(386,deoxysMap);

        Map<Integer,String> wormadamMap = new HashMap<>();
        wormadamMap.put(1,"-S");
        wormadamMap.put(2,"-T");
        map.put(413,wormadamMap);

        Map<Integer,String> shayminMap = new HashMap<>();
        shayminMap.put(1,"-S");
        map.put(492,shayminMap);

        Map<Integer,String> giratinaMap = new HashMap<>();
        giratinaMap.put(1,"-O");
        map.put(487,giratinaMap);

        Map<Integer,String> rotomMap = new HashMap<>();
        rotomMap.put(1,"-H");
        rotomMap.put(2,"-W");
        rotomMap.put(3,"-Fr");
        rotomMap.put(4,"-Fa");
        rotomMap.put(5,"-M");
        map.put(479,rotomMap);

        return map;
    }

    private static Map<Integer,String> setupDummyFormeSuffixes() {
        Map<Integer,String> m = new HashMap<>();
        m.put(0,"");
        return m;
    }

    private static Map<Integer,Map<Integer,Integer>> setupAbsolutePokeNumsByBaseForme() {
        Map<Integer,Map<Integer,Integer>> map = new HashMap<>();

        Map<Integer,Integer> deoxysMap = new HashMap<>();
        deoxysMap.put(1,494);
        deoxysMap.put(2,495);
        deoxysMap.put(3,496);
        map.put(386,deoxysMap);

        Map<Integer,Integer> wormadamMap = new HashMap<>();
        wormadamMap.put(1,497);
        wormadamMap.put(2,498);
        map.put(413,wormadamMap);

        Map<Integer,Integer> giratinaMap = new HashMap<>();
        giratinaMap.put(1,499);
        map.put(487,giratinaMap);

        Map<Integer,Integer> shayminMap = new HashMap<>();
        shayminMap.put(1,500);
        map.put(492,shayminMap);

        Map<Integer,Integer> rotomMap = new HashMap<>();
        rotomMap.put(1,501);
        rotomMap.put(2,502);
        rotomMap.put(3,503);
        rotomMap.put(4,504);
        rotomMap.put(5,505);
        map.put(479,rotomMap);

        return map;
    }

    private static Map<Integer,Integer> setupDummyAbsolutePokeNums() {
        Map<Integer,Integer> m = new HashMap<>();
        m.put(255,0);
        return m;
    }

    public static void tagTrainersDP(List<Trainer> trs) {
        // Gym Trainers
        tag(trs, "GYM1", 0xf4, 0xf5);
        tag(trs, "GYM2", 0x144, 0x103, 0x104, 0x15C);
        tag(trs, "GYM3", 0x135, 0x136, 0x137, 0x138);
        tag(trs, "GYM4", 0x1f1, 0x1f2, 0x191, 0x153, 0x125, 0x1E3);
        tag(trs, "GYM5", 0x165, 0x145, 0x10a, 0x14a, 0x154, 0x157, 0x118, 0x11c);
        tag(trs, "GYM6", 0x13a, 0x100, 0x101, 0x117, 0x16f, 0xe8, 0x11b);
        tag(trs, "GYM7", 0x10c, 0x10d, 0x10e, 0x10f, 0x33b, 0x33c);
        tag(trs, "GYM8", 0x158, 0x155, 0x12d, 0x12e, 0x12f, 0x11d, 0x119);

        // Gym Leaders
        tag(trs, 0xf6, "GYM1-LEADER");
        tag(trs, 0x13b, "GYM2-LEADER");
        tag(trs, 0x13d, "GYM3-LEADER"); // Maylene
        tag(trs, 0x13c, "GYM4-LEADER"); // Wake
        tag(trs, 0x13e, "GYM5-LEADER"); // Fantina
        tag(trs, 0xfa, "GYM6-LEADER"); // Byron
        tag(trs, 0x13f, "GYM7-LEADER"); // Candice
        tag(trs, 0x140, "GYM8-LEADER"); // Volkner

        // Elite 4
        tag(trs, 0x105, "ELITE1");
        tag(trs, 0x106, "ELITE2");
        tag(trs, 0x107, "ELITE3");
        tag(trs, 0x108, "ELITE4");
        tag(trs, 0x10b, "CHAMPION");

        // Rival battles (8)
        tagRivalConsecutive(trs, "RIVAL1", 0xf8);
        tagRivalConsecutive(trs, "RIVAL2", 0x1d7);
        tagRivalConsecutive(trs, "RIVAL3", 0x1da);
        tagRivalConsecutive(trs, "RIVAL4", 0x1dd);
        // Tag battle is not following ze usual format
        tag(trs, 0x26b, "RIVAL5-0");
        tag(trs, 0x26c, "RIVAL5-1");
        tag(trs, 0x25f, "RIVAL5-2");
        // Back to normal
        tagRivalConsecutive(trs, "RIVAL6", 0x1e0);
        tagRivalConsecutive(trs, "RIVAL7", 0x346);
        tagRivalConsecutive(trs, "RIVAL8", 0x349);

        // Themed
        tag(trs, "THEMED:CYRUS-LEADER", 0x193, 0x194);
        tag(trs, "THEMED:MARS-STRONG", 0x127, 0x195, 0x210);
        tag(trs, "THEMED:JUPITER-STRONG", 0x196, 0x197);
        tag(trs, "THEMED:SATURN-STRONG", 0x198, 0x199);

        // Lucas & Dawn tag battles
        tagFriendConsecutive(trs, "FRIEND1", 0x265);
        tagFriendConsecutive(trs, "FRIEND1", 0x268);
        tagFriendConsecutive2(trs, "FRIEND2", 0x26D);
        tagFriendConsecutive2(trs, "FRIEND2", 0x270);

    }

    public static void tagTrainersPt(List<Trainer> trs) {
        // Gym Trainers
        tag(trs, "GYM1", 0xf4, 0xf5);
        tag(trs, "GYM2", 0x144, 0x103, 0x104, 0x15C);
        tag(trs, "GYM3", 0x165, 0x145, 0x154, 0x157, 0x118, 0x11c);
        tag(trs, "GYM4", 0x135, 0x136, 0x137, 0x138);
        tag(trs, "GYM5", 0x1f1, 0x1f2, 0x191, 0x153, 0x125, 0x1E3);
        tag(trs, "GYM6", 0x13a, 0x100, 0x101, 0x117, 0x16f, 0xe8, 0x11b);
        tag(trs, "GYM7", 0x10c, 0x10d, 0x10e, 0x10f, 0x33b, 0x33c);
        tag(trs, "GYM8", 0x158, 0x155, 0x12d, 0x12e, 0x12f, 0x11d, 0x119, 0x14b);

        // Gym Leaders
        tag(trs, 0xf6, "GYM1-LEADER");
        tag(trs, 0x13b, "GYM2-LEADER");
        tag(trs, 0x13e, "GYM3-LEADER"); // Fantina
        tag(trs, 0x13d, "GYM4-LEADER"); // Maylene
        tag(trs, 0x13c, "GYM5-LEADER"); // Wake
        tag(trs, 0xfa, "GYM6-LEADER"); // Byron
        tag(trs, 0x13f, "GYM7-LEADER"); // Candice
        tag(trs, 0x140, "GYM8-LEADER"); // Volkner

        // Elite 4
        tag(trs, 0x105, "ELITE1");
        tag(trs, 0x106, "ELITE2");
        tag(trs, 0x107, "ELITE3");
        tag(trs, 0x108, "ELITE4");
        tag(trs, 0x10b, "CHAMPION");

        // Rival battles (10)
        tagRivalConsecutive(trs, "RIVAL1", 0x353);
        tagRivalConsecutive(trs, "RIVAL2", 0xf8);
        tagRivalConsecutive(trs, "RIVAL3", 0x1d7);
        tagRivalConsecutive(trs, "RIVAL4", 0x1da);
        tagRivalConsecutive(trs, "RIVAL5", 0x1dd);
        // Tag battle is not following ze usual format
        tag(trs, 0x26b, "RIVAL6-0");
        tag(trs, 0x26c, "RIVAL6-1");
        tag(trs, 0x25f, "RIVAL6-2");
        // Back to normal
        tagRivalConsecutive(trs, "RIVAL7", 0x1e0);
        tagRivalConsecutive(trs, "RIVAL8", 0x346);
        tagRivalConsecutive(trs, "RIVAL9", 0x349);
        tagRivalConsecutive(trs, "RIVAL10", 0x368);

        // Battleground Gym Leaders
        tag(trs, 0x35A, "GYM1");
        tag(trs, 0x359, "GYM2");
        tag(trs, 0x35C, "GYM3");
        tag(trs, 0x356, "GYM4");
        tag(trs, 0x35B, "GYM5");
        tag(trs, 0x358, "GYM6");
        tag(trs, 0x355, "GYM7");
        tag(trs, 0x357, "GYM8");

        // Match vs Volkner and Flint in Battle Frontier
        tag(trs, 0x399, "GYM8");
        tag(trs, 0x39A, "ELITE3");

        // E4 rematch
        tag(trs, 0x362, "ELITE1");
        tag(trs, 0x363, "ELITE2");
        tag(trs, 0x364, "ELITE3");
        tag(trs, 0x365, "ELITE4");
        tag(trs, 0x366, "CHAMPION");

        // Themed
        tag(trs, "THEMED:CYRUS-LEADER", 0x391, 0x193, 0x194);
        tag(trs, "THEMED:MARS-STRONG", 0x127, 0x195, 0x210, 0x39e);
        tag(trs, "THEMED:JUPITER-STRONG", 0x196, 0x197, 0x39f);
        tag(trs, "THEMED:SATURN-STRONG", 0x198, 0x199);

        // Lucas & Dawn tag battles
        tagFriendConsecutive(trs, "FRIEND1", 0x265);
        tagFriendConsecutive(trs, "FRIEND1", 0x268);
        tagFriendConsecutive2(trs, "FRIEND2", 0x26D);
        tagFriendConsecutive2(trs, "FRIEND2", 0x270);

    }

    public static void tagTrainersHGSS(List<Trainer> trs) {
        // Gym Trainers
        tag(trs, "GYM1", 0x32, 0x1D);
        tag(trs, "GYM2", 0x43, 0x44, 0x45, 0x0a);
        tag(trs, "GYM3", 0x05, 0x46, 0x47, 0x16);
        tag(trs, "GYM4", 0x57, 0x58, 0x59, 0x2e);
        tag(trs, "GYM5", 0x9c, 0x9d, 0x9f, 0xfb);
        tag(trs, "GYM7", 0x1e0, 0x1e1, 0x1e2, 0x1e3, 0x1e4);
        tag(trs, "GYM8", 0x6e, 0x6f, 0x70, 0x75, 0x77);

        tag(trs, "GYM9", 0x134, 0x2ad);
        tag(trs, "GYM10", 0x2a4, 0x2a5, 0x2a6, 0x129, 0x12a);
        tag(trs, "GYM11", 0x18c, 0xe8, 0x151);
        tag(trs, "GYM12", 0x150, 0x146, 0x164, 0x15a);
        tag(trs, "GYM13", 0x53, 0x54, 0xb7, 0x88);
        tag(trs, "GYM14", 0x170, 0x171, 0xe6, 0x19f);
        tag(trs, "GYM15", 0x2b1, 0x2b2, 0x2b3, 0x2b4, 0x2b5, 0x2b6);
        tag(trs, "GYM16", 0x2a9, 0x2aa, 0x2ab, 0x2ac);

        // Gym Leaders
        tag(trs, 0x14, "GYM1-LEADER");
        tag(trs, 0x15, "GYM2-LEADER");
        tag(trs, 0x1e, "GYM3-LEADER");
        tag(trs, 0x1f, "GYM4-LEADER");
        tag(trs, 0x22, "GYM5-LEADER");
        tag(trs, 0x21, "GYM6-LEADER");
        tag(trs, 0x20, "GYM7-LEADER");
        tag(trs, 0x23, "GYM8-LEADER");

        tag(trs, 0xFD, "GYM9-LEADER");
        tag(trs, 0xFE, "GYM10-LEADER");
        tag(trs, 0xFF, "GYM11-LEADER");
        tag(trs, 0x100, "GYM12-LEADER");
        tag(trs, 0x101, "GYM13-LEADER");
        tag(trs, 0x102, "GYM14-LEADER");
        tag(trs, 0x103, "GYM15-LEADER");
        tag(trs, 0x105, "GYM16-LEADER");

        // Elite 4
        tag(trs, 0xf5, "ELITE1");
        tag(trs, 0xf7, "ELITE2");
        tag(trs, 0x1a2, "ELITE3");
        tag(trs, 0xf6, "ELITE4");
        tag(trs, 0xf4, "CHAMPION");

        // Red
        tag(trs, 0x104, "UBER");

        // Gym Rematches
        tag(trs, 0x2c8, "GYM1-LEADER");
        tag(trs, 0x2c9, "GYM2-LEADER");
        tag(trs, 0x2ca, "GYM3-LEADER");
        tag(trs, 0x2cb, "GYM4-LEADER");
        tag(trs, 0x2ce, "GYM5-LEADER");
        tag(trs, 0x2cd, "GYM6-LEADER");
        tag(trs, 0x2cc, "GYM7-LEADER");
        tag(trs, 0x2cf, "GYM8-LEADER");

        tag(trs, 0x2d0, "GYM9-LEADER");
        tag(trs, 0x2d1, "GYM10-LEADER");
        tag(trs, 0x2d2, "GYM11-LEADER");
        tag(trs, 0x2d3, "GYM12-LEADER");
        tag(trs, 0x2d4, "GYM13-LEADER");
        tag(trs, 0x2d5, "GYM14-LEADER");
        tag(trs, 0x2d6, "GYM15-LEADER");
        tag(trs, 0x2d7, "GYM16-LEADER");

        // Elite 4 Rematch
        tag(trs, 0x2be, "ELITE1");
        tag(trs, 0x2bf, "ELITE2");
        tag(trs, 0x2c0, "ELITE3");
        tag(trs, 0x2c1, "ELITE4");
        tag(trs, 0x2bd, "CHAMPION");

        // Rival Battles
        tagRivalConsecutive(trs, "RIVAL1", 0x1F0);

        tag(trs, 0x10a, "RIVAL2-0");
        tag(trs, 0x10d, "RIVAL2-1");
        tag(trs, 0x1, "RIVAL2-2");

        tag(trs, 0x10B, "RIVAL3-0");
        tag(trs, 0x10E, "RIVAL3-1");
        tag(trs, 0x107, "RIVAL3-2");

        tag(trs, 0x121, "RIVAL4-0");
        tag(trs, 0x10f, "RIVAL4-1");
        tag(trs, 0x120, "RIVAL4-2");

        tag(trs, 0x10C, "RIVAL5-0");
        tag(trs, 0x110, "RIVAL5-1");
        tag(trs, 0x108, "RIVAL5-2");

        tagRivalConsecutive(trs, "RIVAL6", 0x11e);
        tagRivalConsecutive(trs, "RIVAL7", 0x2e0); // dragons den tag battle
        tagRivalConsecutive(trs, "RIVAL8", 0x1EA);

        // Clair & Lance match in Dragons Den
        tag(trs, 0x2DE, "GYM8");
        tag(trs, 0x2DD, "CHAMPION");

        tag(trs, 0xa0, "KIMONO1-STRONG");
        tag(trs, 0xa1, "KIMONO2-STRONG");
        tag(trs, 0xa2, "KIMONO3-STRONG");
        tag(trs, 0xa3, "KIMONO4-STRONG");
        tag(trs, 0xa4, "KIMONO5-STRONG");

        // Themed
        tag(trs, "THEMED:ARIANA-STRONG", 0x1df);
        tag(trs, "THEMED:ARIANA-NOTSTRONG", 0x1de);
        tag(trs, "THEMED:PETREL-STRONG", 0x1e8, 0x1e7);
        tag(trs, "THEMED:PROTON-STRONG", 0x1e6);
        tag(trs, "THEMED:PROTON-NOTSTRONG", 0x2c2);
        tag(trs, "THEMED:SPROUTTOWER", 0x2b, 0x33, 0x34, 0x35, 0x36, 0x37, 0x122);

        tag(trs,"LEADER",485); // Archer
    }

    private static void tag(List<Trainer> allTrainers, int number, String tag) {
        allTrainers.get(number - 1).tag = tag;
    }

    private static void tag(List<Trainer> allTrainers, String tag, int... numbers) {
        for (int num : numbers) {
            allTrainers.get(num - 1).tag = tag;
        }
    }

    private static void tagRivalConsecutive(List<Trainer> allTrainers, String tag, int offsetFire) {
        allTrainers.get(offsetFire - 1).tag = tag + "-0";
        allTrainers.get(offsetFire).tag = tag + "-1";
        allTrainers.get(offsetFire - 2).tag = tag + "-2";

    }

    private static void tagFriendConsecutive(List<Trainer> allTrainers, String tag, int offsetGrass) {
        allTrainers.get(offsetGrass - 1).tag = tag + "-1";
        allTrainers.get(offsetGrass).tag = tag + "-2";
        allTrainers.get(offsetGrass + 1).tag = tag + "-0";

    }

    private static void tagFriendConsecutive2(List<Trainer> allTrainers, String tag, int offsetWater) {
        allTrainers.get(offsetWater - 1).tag = tag + "-0";
        allTrainers.get(offsetWater).tag = tag + "-1";
        allTrainers.get(offsetWater + 1).tag = tag + "-2";

    }

    public static void setCouldBeMultiBattleDP(List<Trainer> trs) {
        // 34 + 35: Potential Double Battle with Camper Anthony and Picnicker Lauren
        // 82 + 83: Potential Double Battle with Rich Boy Jason and Lady Melissa
        // 84 + 85: Potential Double Battle with Gentleman Jeremy and Socialite Reina
        // 95 + 96: Potential Double Battle with PKMN Ranger Jeffrey and PKMN Ranger Allison
        // 104 + 106: Potential Double Battle with Swimmer Evan and Swimmer Mary
        // 160 + 494: Potential Double Battle with Swimmer Erik and Swimmer Claire
        // 186 + 191: Potential Double Battle with Swimmer Colton and Swimmer Paige
        // 201 + 204: Potential Double Battle with Bug Catcher Jack and Lass Briana
        // 202 + 203: Potential Double Battle with Bug Catcher Phillip and Bug Catcher Donald
        // 205 + 206: Potential Double Battle with Psychic Elijah and Psychic Lindsey
        // 278 + 287: Potential Double Battle with Ace Trainer Maya and Ace Trainer Dennis
        // 337 + 359: Potential Double Battle with Sailor Marc and Tuber Conner
        // 358 + 360: Potential Double Battle with Tuber Trenton and Tuber Mariel
        // 372 + 445: Potential Double Battle with Battle Girl Tyler and Black Belt Kendal
        // 373 + 386: Potential Double Battle with Bird Keeper Autumn and Dragon Tamer Joe
        // 379 + 459: Potential Double Battle with Camper Diego and Picnicker Ana
        // 383 + 443: Potential Double Battle with Collector Terry and Ruin Maniac Gerald
        // 388 + 392: Potential Double Battle with Ace Trainer Jonah and Ace Trainer Brenda
        // 389 + 393: Potential Double Battle with Ace Trainer Micah and Ace Trainer Brandi
        // 390 + 394: Potential Double Battle with Ace Trainer Arthur and Ace Trainer Clarice
        // 395 + 398: Potential Double Battle with Psychic Kody and Psychic Rachael
        // 396 + 399: Potential Double Battle with Psychic Landon and Psychic Desiree
        // 397 + 400: Potential Double Battle with Psychic Deandre and Psychic Kendra
        // 407 + 528: Commander Mars and Commander Jupiter Multi Battle on Spear Pillar
        // 414 + 415: Galactic Grunts in Jubilife City
        // 419 + 426: Galactic Grunts in Lake Verity
        // 420 + 427: Galactic Grunts in Lake Verity
        // 446 + 499: Potential Double Battle with Black Belt Eddie and Veteran Terrell
        // 447 + 500: Potential Double Battle with Black Belt Willie and Veteran Brenden
        // 450 + 496: Potential Double Battle with Lass Cassidy and Youngster Wayne
        // 452 + 453: Potential Double Battle with Hiker Damon and Hiker Maurice
        // 454 + 455: Potential Double Battle with Hiker Reginald and Hiker Lorenzo
        // 505 + 506: Potential Double Battle with Worker Brendon and Worker Quentin
        // 521 + 527: Galactic Grunts on Spear Pillar
        // 555 + 560: Potential Double Battle with Bird Keeper Geneva and Dragon Tamer Stanley
        // 556 + 589: Potential Double Battle with Bird Keeper Krystal and Black Belt Ray
        // 562 + 606: Potential Double Battle with Dragon Tamer Kenny and Veteran Harlan
        // 566 + 575: Potential Double Battle with Ace Trainer Felix and Ace Trainer Dana
        // 569 + 579: Potential Double Battle with Ace Trainer Keenan and Ace Trainer Kassandra
        // 570 + 580: Potential Double Battle with Ace Trainer Stefan and Ace Trainer Jasmin
        // 571 + 581: Potential Double Battle with Ace Trainer Skylar and Ace Trainer Natasha
        // 572 + 582: Potential Double Battle with Ace Trainer Abel and Ace Trainer Monique
        // 584 + 586: Potential Double Battle with Psychic Sterling and Psychic Chelsey
        // 561 + 590: Potential Double Battle with Dragon Tamer Drake and Black Belt Jarrett
        // 591 + 596: Potential Double Battle with PKMN Ranger Kyler and PKMN Ranger Krista
        // 594 + 554/585: Potential Double Battle with PKMN Ranger Ashlee and either Bird Keeper Audrey or Psychic Daisy
        // 599 + 602: Potential Double Battle with Swimmer Sam and Swimmer Sophia
        // 835 + 836: Galactic Grunts in Iron Island
        // 848 + 849: Galactic Grunts in Veilstone City
        setCouldBeMultiBattle(trs, 34, 35, 82, 83, 84, 85, 95, 96, 104, 106, 160, 186, 191, 201, 202, 203,
                204, 205, 206, 278, 287, 337, 358, 359, 360, 372, 373, 379, 383, 386, 388, 389, 390, 392, 393, 394,
                395, 396, 397, 398, 399, 400, 407, 414, 415, 419, 420, 426, 427, 443, 445, 446, 447, 450, 452, 453,
                454, 455, 459, 494, 496, 499, 500, 505, 506, 521, 527, 528, 554, 555, 556, 560, 561, 562, 566, 569,
                570, 571, 572, 575, 579, 580, 581, 582, 584, 585, 586, 589, 590, 591, 594, 596, 599, 602, 606, 835,
                836, 848, 849);
    }

    public static void setCouldBeMultiBattlePt(List<Trainer> trs) {
        // In addition to every single trainer listed in setCouldBeMultiBattleDP...
        // 921 + 922: Elite Four Flint and Leader Volkner Multi Battle in the Fight Area
        setCouldBeMultiBattle(trs, 34, 35, 82, 83, 84, 85, 95, 96, 104, 106, 160, 186, 191, 201, 202, 203,
                204, 205, 206, 278, 287, 337, 358, 359, 360, 372, 373, 379, 383, 386, 388, 389, 390, 392, 393, 394,
                395, 396, 397, 398, 399, 400, 407, 414, 415, 419, 420, 426, 427, 443, 445, 446, 447, 450, 452, 453,
                454, 455, 459, 494, 496, 499, 500, 505, 506, 521, 527, 528, 554, 555, 556, 560, 561, 562, 566, 569,
                570, 571, 572, 575, 579, 580, 581, 582, 584, 585, 586, 589, 590, 591, 594, 596, 599, 602, 606, 835,
                836, 848, 849, 921, 922);
    }

    public static void setCouldBeMultiBattleHGSS(List<Trainer> trs) {
        // 120 + 417: Double Battle with Ace Trainer Irene and Ace Trainer Jenn
        // 147 + 151: Potential Double Battle with Camper Ted and Picnicker Erin
        // 354 + 355: Potential Double Battle with Lass Laura and Lass Shannon
        // 423: Potential Double Battle with Pokéfan Jeremy. His potential teammate (Pokéfan Georgia) has more than
        // three Pokemon in the vanilla game, so we leave her be.
        // 479 + 499: Multi Battle with Executive Ariana and Team Rocket Grunt in Team Rocket HQ
        // 564 + 567: Potential Double Battle with Teacher Clarice and School Kid Torin
        // 575 + 576: Potential Double Battle with Biker Dan and Biker Theron
        // 577 + 579: Potential Double Battle with Biker Markey and Biker Teddy
        // 679 + 680: Double Battle with Beauty Callie and Beauty Kassandra
        // 733 + 734: Multi Battle with Champion Lance and Leader Clair in the Dragon's Den
        setCouldBeMultiBattle(trs, 120, 147, 151, 354, 355, 417, 423, 479, 499, 564, 567, 575, 576, 577,
                579, 679, 680, 733, 734);
    }

    private static void setCouldBeMultiBattle(List<Trainer> allTrainers, int... numbers) {
        for (int num : numbers) {
            if (allTrainers.size() > (num - 1)) {
                allTrainers.get(num - 1).couldBeMultiBattle = true;
            }
        }
    }

}
