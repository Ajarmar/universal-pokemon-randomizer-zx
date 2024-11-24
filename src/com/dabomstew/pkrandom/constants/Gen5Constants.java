package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  Gen5Constants.java - Constants for Black/White/Black 2/White 2        --*/
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

import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.Type;

public class Gen5Constants {

    public static final int Type_BW = 0;
    public static final int Type_BW2 = 1;

    public static final int arm9Offset = 0x02004000;

    public static final int pokemonCount = 649, moveCount = 559;
    private static final int bw1FormeCount = 18, bw2FormeCount = 24;
    private static final int bw1formeOffset = 0, bw2formeOffset = 35;

    private static final int bw1NonPokemonBattleSpriteCount = 3;
    private static final int bw2NonPokemonBattleSpriteCount = 36;

    public static final int bsHPOffset = 0, bsAttackOffset = 1, bsDefenseOffset = 2, bsSpeedOffset = 3,
            bsSpAtkOffset = 4, bsSpDefOffset = 5, bsPrimaryTypeOffset = 6, bsSecondaryTypeOffset = 7,
            bsCatchRateOffset = 8, bsCommonHeldItemOffset = 12, bsRareHeldItemOffset = 14,
            bsDarkGrassHeldItemOffset = 16, bsGrowthCurveOffset = 21, bsAbility1Offset = 24, bsAbility2Offset = 25,
            bsAbility3Offset = 26, bsFormeOffset = 28, bsFormeSpriteOffset = 30, bsFormeCountOffset = 32,
            bsTMHMCompatOffset = 40, bsMTCompatOffset = 60;

    public static final byte[] bw1NewStarterScript = { 0x24, 0x00, (byte) 0xA7, 0x02, (byte) 0xE7, 0x00, 0x00, 0x00,
            (byte) 0xDE, 0x00, 0x00, 0x00, (byte) 0xF8, 0x01, 0x05, 0x00 };

    public static final String bw1StarterScriptMagic = "2400A702";

    public static final int bw1StarterTextOffset = 18, bw1CherenText1Offset = 26, bw1CherenText2Offset = 53;

    public static final byte[] bw2NewStarterScript = { 0x28, 0x00, (byte) 0xA1, 0x40, 0x04, 0x00, (byte) 0xDE, 0x00,
            0x00, 0x00, (byte) 0xFD, 0x01, 0x05, 0x00 };

    public static final String bw2StarterScriptMagic = "2800A1400400";

    public static final int bw2StarterTextOffset = 37, bw2RivalTextOffset = 60;

    public static final int perSeasonEncounterDataLength = 232;
    private static final int bw1AreaDataEntryLength = 249, bw2AreaDataEntryLength = 345, bw1EncounterAreaCount = 61, bw2EncounterAreaCount = 85;

    public static final int[] encountersOfEachType = { 12, 12, 12, 5, 5, 5, 5 };

    public static final String[] encounterTypeNames = { "Grass/Cave", "Doubles Grass", "Shaking Spots", "Surfing",
            "Surfing Spots", "Fishing", "Fishing Spots" };

    public static final int[] habitatClassificationOfEachType = { 0, 0, 0, 1, 1, 2, 2 };

    public static final int bw2Route4AreaIndex = 40, bw2VictoryRoadAreaIndex = 76, bw2ReversalMountainAreaIndex = 73;

    public static final int b2Route4EncounterFile = 104, b2VRExclusiveRoom1 = 71, b2VRExclusiveRoom2 = 73,
            b2ReversalMountainStart = 49, b2ReversalMountainEnd = 54;

    public static final int w2Route4EncounterFile = 105, w2VRExclusiveRoom1 = 78, w2VRExclusiveRoom2 = 79,
            w2ReversalMountainStart = 55, w2ReversalMountainEnd = 60;

    public static final List<Integer> bw2HiddenHollowUnovaPokemon = Arrays.asList(Species.watchog, Species.herdier, Species.liepard,
            Species.pansage, Species.pansear, Species.panpour, Species.pidove, Species.zebstrika, Species.boldore,
            Species.woobat, Species.drilbur, Species.audino, Species.gurdurr, Species.tympole, Species.throh,
            Species.sawk, Species.leavanny, Species.scolipede, Species.cottonee, Species.petilil, Species.basculin,
            Species.krookodile, Species.maractus, Species.crustle, Species.scraggy, Species.sigilyph, Species.tirtouga,
            Species.garbodor, Species.minccino, Species.gothorita, Species.duosion, Species.ducklett, Species.vanillish,
            Species.emolga, Species.karrablast, Species.alomomola, Species.galvantula, Species.klinklang, Species.elgyem,
            Species.litwick, Species.axew, Species.cubchoo, Species.shelmet, Species.stunfisk, Species.mienfoo,
            Species.druddigon, Species.golett, Species.pawniard, Species.bouffalant, Species.braviary, Species.mandibuzz,
            Species.heatmor, Species.durant);

    public static final String tmDataPrefix = "87038803";

    public static final int tmCount = 95, hmCount = 6, tmBlockOneCount = 92, tmBlockOneOffset = Items.tm01,
            tmBlockTwoOffset = Items.tm93;

    public static final String bw1ItemPalettesPrefix = "E903EA03020003000400050006000700",
            bw2ItemPalettesPrefix = "FD03FE03020003000400050006000700";

    public static final int bw2MoveTutorCount = 60, bw2MoveTutorBytesPerEntry = 12;

    public static final int evolutionMethodCount = 27;

    public static final int highestAbilityIndex = Abilities.teravolt;

    public static final int fossilPokemonFile = 877;
    public static final int fossilPokemonLevelOffset = 0x3F7;

    public static final Map<Integer,List<Integer>> abilityVariations = setupAbilityVariations();

    private static Map<Integer,List<Integer>> setupAbilityVariations() {
        Map<Integer,List<Integer>> map = new HashMap<>();
        map.put(Abilities.insomnia, Arrays.asList(Abilities.insomnia, Abilities.vitalSpirit));
        map.put(Abilities.clearBody, Arrays.asList(Abilities.clearBody, Abilities.whiteSmoke));
        map.put(Abilities.hugePower, Arrays.asList(Abilities.hugePower, Abilities.purePower));
        map.put(Abilities.battleArmor, Arrays.asList(Abilities.battleArmor, Abilities.shellArmor));
        map.put(Abilities.cloudNine, Arrays.asList(Abilities.cloudNine, Abilities.airLock));
        map.put(Abilities.filter, Arrays.asList(Abilities.filter, Abilities.solidRock));
        map.put(Abilities.roughSkin, Arrays.asList(Abilities.roughSkin, Abilities.ironBarbs));
        map.put(Abilities.moldBreaker, Arrays.asList(Abilities.moldBreaker, Abilities.turboblaze, Abilities.teravolt));

        return map;
    }

    public static final List<Integer> uselessAbilities = Arrays.asList(Abilities.forecast, Abilities.multitype,
            Abilities.flowerGift, Abilities.zenMode);

    public static final int normalItemSetVarCommand = 0x28, hiddenItemSetVarCommand = 0x2A, normalItemVarSet = 0x800C,
            hiddenItemVarSet = 0x8000;

    public static final int scriptListTerminator = 0xFD13;

    public static final int[] mulchIndices = {Items.growthMulch, Items.dampMulch, Items.stableMulch, Items.gooeyMulch};

    public static final MoveCategory[] moveCategoryIndices = { MoveCategory.STATUS, MoveCategory.PHYSICAL,
            MoveCategory.SPECIAL };

    public static byte moveCategoryToByte(MoveCategory cat) {
        switch (cat) {
        case PHYSICAL:
            return 1;
        case SPECIAL:
            return 2;
        case STATUS:
        default:
            return 0;
        }
    }

    public static final int trappingEffect = 106;

    public static final int noDamageStatusQuality = 1, noDamageStatChangeQuality = 2, damageStatusQuality = 4,
            noDamageStatusAndStatChangeQuality = 5, damageTargetDebuffQuality = 6, damageUserBuffQuality = 7,
            damageAbsorbQuality = 8;

    public static final Type[] typeTable = constructTypeTable();

    private static final Map<Integer,String> bw1FormeSuffixes = setupFormeSuffixes(Gen5Constants.Type_BW);

    private static final Map<Integer,String> bw2FormeSuffixes = setupFormeSuffixes(Gen5Constants.Type_BW2);

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

    private static final List<Integer> bw1IrregularFormes = Arrays.asList(
            Species.Gen5Formes.castformF, Species.Gen5Formes.castformW, Species.Gen5Formes.castformI,
            Species.Gen5Formes.darmanitanZ,
            Species.Gen5Formes.meloettaP
    );

    private static final List<Integer> bw2IrregularFormes = Arrays.asList(
            Species.Gen5Formes.castformF, Species.Gen5Formes.castformW, Species.Gen5Formes.castformI,
            Species.Gen5Formes.darmanitanZ,
            Species.Gen5Formes.meloettaP,
            Species.Gen5Formes.kyuremW,
            Species.Gen5Formes.kyuremB
    );

    public static final List<Integer> emptyPlaythroughTrainers = Arrays.asList(new Integer[] { });
    
    public static final List<Integer> bw1MainPlaythroughTrainers = Arrays.asList(
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
            40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
            62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 79,
            80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 93, 94, 95, 96, 97, 98,
            99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118,
            119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 137, 138,
            139, 140, 141, 142, 143, 144, 145, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162,
            163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181,
            182, 183, 184, 186, 187, 188, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203,
            204, 212, 213, 214, 215, 216, 217, 218, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230,
            231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250,
            251, 252, 253, 254, 255, 256, 257, 258, 263, 264, 265, 266, 267, 268, 269, 270, 271, 272, 273,
            274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284, 285, 286, 290, 291, 292, 293,
            294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312,
            313, 315, 316, 401, 402, 408, 409, 412, 413, 438, 439, 441, 442, 443, 445, 447, 450,
            460, 461, 462, 465, 466, 468, 469, 470, 471, 472, 473, 474, 475, 476, 477, 478, 479, 480, 481,
            484, 485, 488, 489, 490, 501, 502, 503, 504, 505, 506,
            513, 514, 515, 516, 517, 518, 519, 520, 526, 531, 532, 533, 534, 535, 536, 537,
            538, 544, 545, 546, 549, 550, 552, 553, 554, 555, 556, 557, 582, 583, 584, 585, 586,
            587, 600, 601, 602, 603, 604, 605, 606, 607, 610, 611, 612, 613);

    public static final List<Integer> bw2MainPlaythroughTrainers = Arrays.asList(
            4, 5, 6, 133, 134, 135, 136, 137, 138, 139, 147, 148, 149,
            150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160,
            164, 165, 169, 170, 171, 172, 173, 174, 175, 176, 177,
            178, 179, 180, 181, 182, 203, 204, 205, 206, 207, 208, 209, 210, 211,
            212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225,
            226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 237, 238, 239, 240,
            242, 243, 244, 245, 247, 248, 249, 250, 252, 253, 254, 255, 256, 257,
            258, 259, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269, 270, 271,
            272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284, 285,
            286, 287, 288, 289, 290, 291, 292, 293, 294, 295, 296, 297, 298, 299,
            300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313,
            314, 315, 316, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327,
            328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341,
            342, 343, 344, 345, 346, 347, 348, 349, 350, 351, 352, 353, 354, 355,
            356, 357, 358, 359, 360, 361, 362, 363, 364, 365, 366, 367,
            372, 373, 374, 375, 376, 377, 381, 382, 383,
            384, 385, 386, 387, 388, 389, 390, 391, 392, 426, 427, 428, 429, 430,
            431, 432, 433, 434, 435, 436, 437, 438, 439, 440, 441, 442, 443, 444,
            445, 446, 447, 448, 449, 450, 451, 452, 453, 454, 455, 461, 462, 463,
            464, 465, 466, 467, 468, 469, 470, 471, 472, 473, 474, 475, 476, 477,
            478, 479, 480, 481, 482, 483, 484, 485, 486, 497, 498, 499, 500, 501,
            502, 503, 510, 511, 512, 513, 514, 515, 516, 517, 518, 519, 520, 521,
            522, 523, 524, 537, 538, 539, 540, 541, 542, 543, 544, 545, 546, 547,
            548, 549, 550, 551, 552, 553, 554, 555, 556, 557, 558, 559, 560, 561,
            562, 563, 564, 565, 566, 567, 568, 569, 570, 580, 581, 583, 584, 585,
            586, 587, 592, 593, 594, 595, 596, 597, 598, 599, 600,
            601, 602, 603, 604, 605, 606, 607, 608, 609, 610, 611, 612, 613, 614,
            615, 621, 622, 623, 624, 625, 626, 627, 628, 629, 630, 631, 657, 658,
            659, 660, 661, 662, 663, 664, 665, 666, 667, 668, 669, 670, 671, 672,
            673, 679, 680, 681, 682, 683, 690, 691, 692, 703, 704,
            705, 712, 713, 714, 715, 716, 717, 718, 719, 720, 721, 722, 723, 724,
            725, 726, 727, 728, 729, 730, 731, 732, 733, 734, 735, 736, 737, 738,
            745, 746, 747, 748, 749, 750, 751, 752, 754, 755, 756, 763, 764, 765,
            766, 767, 768, 769, 770, 771, 772, 773, 774, 775, 776, 786, 787, 788,
            789, 797, 798, 799, 800, 801, 802, 803, 804, 805, 806,
            807, 808, 809, 810, 811, 812);

    public static final List<Integer> bw2DriftveilTrainerOffsets = Arrays.asList(56, 57, 0, 1, 2, 3, 4, 68, 69, 70,
            71, 72, 73, 74, 75, 76, 77);

    public static final int normalTrainerNameLength = 813, normalTrainerClassLength = 236;
    
//    public static final Map<Integer, String> bw1ShopIndex = new HashMap<Integer, String>() {1:"Check"};

    public static final List<Integer> bw1MainGameShops = Arrays.asList(
            3, 5, 6, 8, 9, 12, 14, 17, 18, 19, 21, 22
    );

    public static final List<String> bw1ShopNames = Arrays.asList(
            "Primary 0 Badges",
            "Shopping Mall 9 TMs",
            "Icirrus Secondary (TMs)",
            "Driftveil Herb Salesman",
            "Mistralton Secondary (TMs)",
            "Shopping Mall 9 F3 Left",
            "Accumula Secondary",
            "Nimbasa Secondary (TMs)",
            "Striaton Secondary",
            "League Secondary",
            "Lacunosa Secondary",
            "Black City/White Forest Secondary",
            "Nacrene/Shopping Mall 9 X Items",
            "Driftveil Incense Salesman",
            "Nacrene Secondary",
            "Undella Secondary",
            "Primary 2 Badges",
            "Castelia Secondary",
            "Driftveil Secondary",
            "Opelucid Secondary",
            "Primary 3 Badges",
            "Shopping Mall 9 F1",
            "Shopping Mall 9 F2",
            "Primary 5 Badges",
            "Primary 7 Badges",
            "Primary 8 Badges");

    public static final List<Integer> bw2MainGameShops = Arrays.asList(
            9, 11, 14, 15, 16, 18, 20, 21, 22, 23, 25, 26, 27, 28, 29, 30, 31
    );
    
    public static final List<String> bw2ShopNames = Arrays.asList(
            "Primary 0 Badges",
            "Primary 1 Badges",
            "Primary 3 Badges",
            "Primary 5 Badges",
            "Primary 7 Badges",
            "Primary 8 Badges",
            "Accumula Secondary",
            "Striaton Secondary (TMs)",
            "Nacrene Secondary",
            "Castelia Secondary",
            "Nimbasa Secondary (TMs)",
            "Driftveil Secondary",
            "Mistralton Secondary (TMs)",
            "Icirrus Secondary",
            "Opelucid Secondary",
            "Victory Road Secondary",
            "Pokemon League Secondary",
            "Lacunosa Secondary (TMs)",
            "Undella Secondary",
            "Black City/White Forest Secondary",
            "Nacrene/Shopping Mall 9 X Items",
            "Driftveil Herb Salesman",
            "Driftveil Incense Salesman",
            "Shopping Mall 9 F1",
            "Shopping Mall 9 TMs",
            "Shopping Mall 9 F2",
            "Shopping Mall 9 F3 Left",
            "Aspertia Secondary",
            "Virbank Secondary",
            "Humilau Secondary",
            "Floccesy Secondary",
            "Lentimas Secondary");


    public static final List<Integer> evolutionItems = Arrays.asList(Items.sunStone, Items.moonStone, Items.fireStone,
            Items.thunderStone, Items.waterStone, Items.leafStone, Items.shinyStone, Items.duskStone, Items.dawnStone,
            Items.ovalStone, Items.kingsRock, Items.deepSeaTooth, Items.deepSeaScale, Items.metalCoat, Items.dragonScale,
            Items.upgrade, Items.protector, Items.electirizer, Items.magmarizer, Items.dubiousDisc, Items.reaperCloth,
            Items.razorClaw, Items.razorFang, Items.prismScale);
    
    public static final List<Integer> bw1RequiredFieldTMs = Arrays.asList(2, 3, 5, 6, 9, 12, 13, 19,
            22, 24, 26, 29, 30, 35, 36, 39, 41, 46, 47, 50, 52, 53, 55, 58, 61, 63, 65, 66, 71, 80, 81, 84, 85, 86, 90,
            91, 92, 93);

    public static final List<Integer> bw2RequiredFieldTMs = Arrays.asList(1, 2, 3, 5, 6, 12, 13, 19,
            22, 26, 28, 29, 30, 36, 39, 41, 46, 47, 50, 52, 53, 56, 58, 61, 63, 65, 66, 67, 69, 71, 80, 81, 84, 85, 86,
            90, 91, 92, 93);

    public static final List<Integer> bw1EarlyRequiredHMMoves = Collections.singletonList(Moves.cut);

    public static final List<Integer> bw2EarlyRequiredHMMoves = Collections.emptyList();

    public static final List<Integer> fieldMoves = Arrays.asList(
            Moves.cut, Moves.fly, Moves.surf, Moves.strength, Moves.flash, Moves.dig, Moves.teleport,
            Moves.waterfall, Moves.sweetScent, Moves.dive);

    public static final String shedinjaSpeciesLocator = "24010000";

    public static final String runningShoesPrefix = "01D0012008BD002008BD63";

    public static final String introGraphicPrefix = "5A0000010000001700000001000000", bw1IntroCryPrefix = "0021009101910291", bw2IntroCryLocator = "3D020000F8B51C1C";

    public static final String typeEffectivenessTableLocator = "0404040404020400";

    public static final String forceChallengeModeLocator = "816A406B0B1C07490022434090000858834201D1";

    public static final String pickupTableLocator = "19005C00DD00";
    public static final int numberOfPickupItems = 29;

    public static final String friendshipValueForEvoLocator = "DC282FD3";

    public static final String perfectOddsBranchLocator = "08DB002801D0012000E0";

    public static final String lowHealthMusicLocator = "00D10127";

    public static final List<Integer> consumableHeldItems = setupAllConsumableItems();

    private static List<Integer> setupAllConsumableItems() {
        List<Integer> list = new ArrayList<>(Gen4Constants.consumableHeldItems);
        list.addAll(Arrays.asList(Items.airBalloon, Items.redCard, Items.absorbBulb, Items.cellBattery,
                Items.ejectButton, Items.fireGem, Items.waterGem, Items.electricGem, Items.grassGem, Items.iceGem,
                Items.fightingGem, Items.poisonGem, Items.groundGem, Items.flyingGem, Items.psychicGem, Items.bugGem,
                Items.rockGem, Items.ghostGem, Items.dragonGem, Items.darkGem, Items.steelGem, Items.normalGem));
        return Collections.unmodifiableList(list);
    }

    public static final List<Integer> allHeldItems = setupAllHeldItems();

    private static List<Integer> setupAllHeldItems() {
        List<Integer> list = new ArrayList<>(Gen4Constants.allHeldItems);
        list.addAll(Arrays.asList(Items.airBalloon, Items.redCard, Items.absorbBulb, Items.cellBattery,
                Items.ejectButton, Items.fireGem, Items.waterGem, Items.electricGem, Items.grassGem, Items.iceGem,
                Items.fightingGem, Items.poisonGem, Items.groundGem, Items.flyingGem, Items.psychicGem, Items.bugGem,
                Items.rockGem, Items.ghostGem, Items.dragonGem, Items.darkGem, Items.steelGem, Items.normalGem));
        list.addAll(Arrays.asList(Items.eviolite, Items.floatStone, Items.rockyHelmet, Items.ringTarget, Items.bindingBand));
        return Collections.unmodifiableList(list);
    }

    public static final List<Integer> generalPurposeConsumableItems = initializeGeneralPurposeConsumableItems();

    private static List<Integer> initializeGeneralPurposeConsumableItems() {
        List<Integer> list = new ArrayList<>(Gen4Constants.generalPurposeConsumableItems);
        list.addAll(Arrays.asList(Items.redCard, Items.absorbBulb, Items.cellBattery, Items.ejectButton));
        return Collections.unmodifiableList(list);
    }

    public static final List<Integer> generalPurposeItems = initializeGeneralPurposeItems();

    private static List<Integer> initializeGeneralPurposeItems() {
        List<Integer> list = new ArrayList<>(Gen4Constants.generalPurposeItems);
        list.addAll(Arrays.asList(Items.floatStone, Items.rockyHelmet));
        return Collections.unmodifiableList(list);
    }

    public static final Map<Type, Integer> consumableTypeBoostingItems = initializeConsumableTypeBoostingItems();

    private static Map<Type, Integer> initializeConsumableTypeBoostingItems() {
        Map<Type, Integer> map = new HashMap<>();
        map.put(Type.FIRE, Items.fireGem);
        map.put(Type.WATER, Items.waterGem);
        map.put(Type.ELECTRIC, Items.electricGem);
        map.put(Type.GRASS, Items.grassGem);
        map.put(Type.ICE, Items.iceGem);
        map.put(Type.FIGHTING, Items.fightingGem);
        map.put(Type.POISON, Items.poisonGem);
        map.put(Type.GROUND, Items.groundGem);
        map.put(Type.FLYING, Items.flyingGem);
        map.put(Type.PSYCHIC, Items.psychicGem);
        map.put(Type.BUG, Items.bugGem);
        map.put(Type.ROCK, Items.rockGem);
        map.put(Type.GHOST, Items.ghostGem);
        map.put(Type.DRAGON, Items.dragonGem);
        map.put(Type.DARK, Items.darkGem);
        map.put(Type.STEEL, Items.steelGem);
        map.put(Type.NORMAL, Items.normalGem);
        return Collections.unmodifiableMap(map);
    }

    public static final Map<Integer, List<Integer>> moveBoostingItems = initializeMoveBoostingItems();

    private static Map<Integer, List<Integer>> initializeMoveBoostingItems() {
        Map<Integer, List<Integer>> map = new HashMap<>(Gen4Constants.moveBoostingItems);
        map.put(Moves.trick, Arrays.asList(Items.toxicOrb, Items.flameOrb, Items.ringTarget));
        map.put(Moves.switcheroo, Arrays.asList(Items.toxicOrb, Items.flameOrb, Items.ringTarget));

        map.put(Moves.bind, Arrays.asList(Items.gripClaw, Items.bindingBand));
        map.put(Moves.clamp, Arrays.asList(Items.gripClaw, Items.bindingBand));
        map.put(Moves.fireSpin, Arrays.asList(Items.gripClaw, Items.bindingBand));
        map.put(Moves.magmaStorm, Arrays.asList(Items.gripClaw, Items.bindingBand));
        map.put(Moves.sandTomb, Arrays.asList(Items.gripClaw, Items.bindingBand));
        map.put(Moves.whirlpool, Arrays.asList(Items.gripClaw, Items.bindingBand));
        map.put(Moves.wrap, Arrays.asList(Items.gripClaw, Items.bindingBand));

        map.put(Moves.hornLeech, Arrays.asList(Items.bigRoot));
        return Collections.unmodifiableMap(map);
    }

    // None of these have new entries in Gen V.
    public static final Map<Integer, List<Integer>> abilityBoostingItems = Gen4Constants.abilityBoostingItems;
    public static final Map<Integer, List<Integer>> speciesBoostingItems = Gen4Constants.speciesBoostingItems;
    public static final Map<Type, List<Integer>> typeBoostingItems = Gen4Constants.typeBoostingItems;
    public static final Map<Type, Integer> weaknessReducingBerries = Gen4Constants.weaknessReducingBerries;

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
        table[0x09] = Type.FIRE;
        table[0x0A] = Type.WATER;
        table[0x0B] = Type.GRASS;
        table[0x0C] = Type.ELECTRIC;
        table[0x0D] = Type.PSYCHIC;
        table[0x0E] = Type.ICE;
        table[0x0F] = Type.DRAGON;
        table[0x10] = Type.DARK;
        return table;
    }

    public static byte typeToByte(Type type) {
        if (type == null) {
            return 0x00; // normal?
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
            return 0x09;
        case WATER:
            return 0x0A;
        case GRASS:
            return 0x0B;
        case ELECTRIC:
            return 0x0C;
        case PSYCHIC:
            return 0x0D;
        case ICE:
            return 0x0E;
        case DRAGON:
            return 0x0F;
        case STEEL:
            return 0x08;
        case DARK:
            return 0x10;
        default:
            return 0; // normal by default
        }
    }

    public static int getAreaDataEntryLength(int romType) {
        if (romType == Type_BW) {
            return bw1AreaDataEntryLength;
        } else if (romType == Type_BW2) {
            return bw2AreaDataEntryLength;
        }
        return 0;
    }

    public static int getEncounterAreaCount(int romType) {
        if (romType == Type_BW) {
            return bw1EncounterAreaCount;
        } else if (romType == Type_BW2) {
            return bw2EncounterAreaCount;
        }
        return 0;
    }

    public static int[] getWildFileToAreaMap(int romType) {
        if (romType == Type_BW) {
            return bw1WildFileToAreaMap;
        } else if (romType == Type_BW2) {
            return bw2WildFileToAreaMap;
        }
        return new int[0];
    }

    public static List<Integer> getMainGameShops(int romType) {
        if (romType == Type_BW) {
            return bw1MainGameShops;
        } else if (romType == Type_BW2) {
            return bw2MainGameShops;
        }
        return new ArrayList<>();
    }

    public static List<Integer> getIrregularFormes(int romType) {
        if (romType == Type_BW) {
            return bw1IrregularFormes;
        } else if (romType == Type_BW2) {
            return bw2IrregularFormes;
        }
        return new ArrayList<>();
    }

    public static int getFormeCount(int romType) {
        if (romType == Type_BW) {
            return bw1FormeCount;
        } else if (romType == Type_BW2) {
            return bw2FormeCount;
        }
        return 0;
    }

    public static int getFormeOffset(int romType) {
        if (romType == Type_BW) {
            return bw1formeOffset;
        } else if (romType == Type_BW2) {
            return bw2formeOffset;
        }
        return 0;
    }

    public static int getNonPokemonBattleSpriteCount(int romType) {
        if (romType == Type_BW) {
            return bw1NonPokemonBattleSpriteCount;
        } else if (romType == Type_BW2) {
            return bw2NonPokemonBattleSpriteCount;
        }
        return 0;
    }

    public static String getFormeSuffix(int internalIndex, int romType) {
        if (romType == Type_BW) {
            return bw1FormeSuffixes.getOrDefault(internalIndex,"");
        } else if (romType == Type_BW2) {
            return bw2FormeSuffixes.getOrDefault(internalIndex,"");
        } else {
            return "";
        }
    }

    private static Map<Integer,String> setupFormeSuffixes(int gameVersion) {
        Map<Integer,String> formeSuffixes = new HashMap<>();
        if (gameVersion == Gen5Constants.Type_BW) {
            formeSuffixes.put(Species.Gen5Formes.deoxysA,"-A");
            formeSuffixes.put(Species.Gen5Formes.deoxysD,"-D");
            formeSuffixes.put(Species.Gen5Formes.deoxysS,"-S");
            formeSuffixes.put(Species.Gen5Formes.wormadamS,"-S");
            formeSuffixes.put(Species.Gen5Formes.wormadamT,"-T");
            formeSuffixes.put(Species.Gen5Formes.shayminS,"-S");
            formeSuffixes.put(Species.Gen5Formes.giratinaO,"-O");
            formeSuffixes.put(Species.Gen5Formes.rotomH,"-H");
            formeSuffixes.put(Species.Gen5Formes.rotomW,"-W");
            formeSuffixes.put(Species.Gen5Formes.rotomFr,"-Fr");
            formeSuffixes.put(Species.Gen5Formes.rotomFa,"-Fa");
            formeSuffixes.put(Species.Gen5Formes.rotomM,"-M");
            formeSuffixes.put(Species.Gen5Formes.castformF,"-F");
            formeSuffixes.put(Species.Gen5Formes.castformW,"-W");
            formeSuffixes.put(Species.Gen5Formes.castformI,"-I");
            formeSuffixes.put(Species.Gen5Formes.basculinB,"-B");
            formeSuffixes.put(Species.Gen5Formes.darmanitanZ,"-Z");
            formeSuffixes.put(Species.Gen5Formes.meloettaP,"-P");
        } else if (gameVersion == Gen5Constants.Type_BW2) {
            formeSuffixes.put(Species.Gen5Formes.deoxysA + bw2formeOffset,"-A");
            formeSuffixes.put(Species.Gen5Formes.deoxysD + bw2formeOffset,"-D");
            formeSuffixes.put(Species.Gen5Formes.deoxysS + bw2formeOffset,"-S");
            formeSuffixes.put(Species.Gen5Formes.wormadamS + bw2formeOffset,"-S");
            formeSuffixes.put(Species.Gen5Formes.wormadamT + bw2formeOffset,"-T");
            formeSuffixes.put(Species.Gen5Formes.shayminS + bw2formeOffset,"-S");
            formeSuffixes.put(Species.Gen5Formes.giratinaO + bw2formeOffset,"-O");
            formeSuffixes.put(Species.Gen5Formes.rotomH + bw2formeOffset,"-H");
            formeSuffixes.put(Species.Gen5Formes.rotomW + bw2formeOffset,"-W");
            formeSuffixes.put(Species.Gen5Formes.rotomFr + bw2formeOffset,"-Fr");
            formeSuffixes.put(Species.Gen5Formes.rotomFa + bw2formeOffset,"-Fa");
            formeSuffixes.put(Species.Gen5Formes.rotomM + bw2formeOffset,"-M");
            formeSuffixes.put(Species.Gen5Formes.castformF + bw2formeOffset,"-F");
            formeSuffixes.put(Species.Gen5Formes.castformW + bw2formeOffset,"-W");
            formeSuffixes.put(Species.Gen5Formes.castformI + bw2formeOffset,"-I");
            formeSuffixes.put(Species.Gen5Formes.basculinB + bw2formeOffset,"-B");
            formeSuffixes.put(Species.Gen5Formes.darmanitanZ + bw2formeOffset,"-Z");
            formeSuffixes.put(Species.Gen5Formes.meloettaP + bw2formeOffset,"-P");
            formeSuffixes.put(Species.Gen5Formes.kyuremW + bw2formeOffset,"-W");
            formeSuffixes.put(Species.Gen5Formes.kyuremB + bw2formeOffset,"-B");
            formeSuffixes.put(Species.Gen5Formes.tornadusT + bw2formeOffset,"-T");
            formeSuffixes.put(Species.Gen5Formes.thundurusT + bw2formeOffset,"-T");
            formeSuffixes.put(Species.Gen5Formes.landorusT + bw2formeOffset,"-T");
        }

        return formeSuffixes;
    }

    private static Map<Integer,Map<Integer,String>> setupFormeSuffixesByBaseForme() {
        Map<Integer,Map<Integer,String>> map = new HashMap<>();

        Map<Integer,String> deoxysMap = new HashMap<>();
        deoxysMap.put(1,"-A");
        deoxysMap.put(2,"-D");
        deoxysMap.put(3,"-S");
        map.put(Species.deoxys, deoxysMap);

        Map<Integer,String> wormadamMap = new HashMap<>();
        wormadamMap.put(1,"-S");
        wormadamMap.put(2,"-T");
        map.put(Species.wormadam, wormadamMap);

        Map<Integer,String> shayminMap = new HashMap<>();
        shayminMap.put(1,"-S");
        map.put(Species.shaymin, shayminMap);

        Map<Integer,String> giratinaMap = new HashMap<>();
        giratinaMap.put(1,"-O");
        map.put(Species.giratina, giratinaMap);

        Map<Integer,String> rotomMap = new HashMap<>();
        rotomMap.put(1,"-H");
        rotomMap.put(2,"-W");
        rotomMap.put(3,"-Fr");
        rotomMap.put(4,"-Fa");
        rotomMap.put(5,"-M");
        map.put(Species.rotom, rotomMap);

        Map<Integer,String> castformMap = new HashMap<>();
        castformMap.put(1,"-F");
        castformMap.put(2,"-W");
        castformMap.put(3,"-I");
        map.put(Species.castform, castformMap);

        Map<Integer,String> basculinMap = new HashMap<>();
        basculinMap.put(1,"-B");
        map.put(Species.basculin, basculinMap);

        Map<Integer,String> darmanitanMap = new HashMap<>();
        darmanitanMap.put(1,"-Z");
        map.put(Species.darmanitan, darmanitanMap);

        Map<Integer,String> meloettaMap = new HashMap<>();
        meloettaMap.put(1,"-P");
        map.put(Species.meloetta, meloettaMap);

        Map<Integer,String> kyuremMap = new HashMap<>();
        kyuremMap.put(1,"-W");
        kyuremMap.put(2,"-B");
        map.put(Species.kyurem, kyuremMap);

        Map<Integer,String> keldeoMap = new HashMap();
        keldeoMap.put(1,"-R");
        map.put(Species.keldeo, keldeoMap);

        Map<Integer,String> tornadusMap = new HashMap<>();
        tornadusMap.put(1,"-T");
        map.put(Species.tornadus, tornadusMap);

        Map<Integer,String> thundurusMap = new HashMap<>();
        thundurusMap.put(1,"-T");
        map.put(Species.thundurus, thundurusMap);

        Map<Integer,String> landorusMap = new HashMap<>();
        landorusMap.put(1,"-T");
        map.put(Species.landorus, landorusMap);

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
        deoxysMap.put(1,Species.Gen5Formes.deoxysA);
        deoxysMap.put(2,Species.Gen5Formes.deoxysD);
        deoxysMap.put(3,Species.Gen5Formes.deoxysS);
        map.put(Species.deoxys, deoxysMap);

        Map<Integer,Integer> wormadamMap = new HashMap<>();
        wormadamMap.put(1,Species.Gen5Formes.wormadamS);
        wormadamMap.put(2,Species.Gen5Formes.wormadamT);
        map.put(Species.wormadam, wormadamMap);

        Map<Integer,Integer> shayminMap = new HashMap<>();
        shayminMap.put(1,Species.Gen5Formes.shayminS);
        map.put(Species.shaymin, shayminMap);

        Map<Integer,Integer> giratinaMap = new HashMap<>();
        giratinaMap.put(1,Species.Gen5Formes.giratinaO);
        map.put(Species.giratina, giratinaMap);

        Map<Integer,Integer> rotomMap = new HashMap<>();
        rotomMap.put(1,Species.Gen5Formes.rotomH);
        rotomMap.put(2,Species.Gen5Formes.rotomW);
        rotomMap.put(3,Species.Gen5Formes.rotomFr);
        rotomMap.put(4,Species.Gen5Formes.rotomFa);
        rotomMap.put(5,Species.Gen5Formes.rotomM);
        map.put(Species.rotom, rotomMap);

        Map<Integer,Integer> castformMap = new HashMap<>();
        castformMap.put(1,Species.Gen5Formes.castformF);
        castformMap.put(2,Species.Gen5Formes.castformW);
        castformMap.put(3,Species.Gen5Formes.castformI);
        map.put(Species.castform, castformMap);

        Map<Integer,Integer> basculinMap = new HashMap<>();
        basculinMap.put(1,Species.Gen5Formes.basculinB);
        map.put(Species.basculin, basculinMap);

        Map<Integer,Integer> darmanitanMap = new HashMap<>();
        darmanitanMap.put(1,Species.Gen5Formes.darmanitanZ);
        map.put(Species.darmanitan, darmanitanMap);

        Map<Integer,Integer> meloettaMap = new HashMap<>();
        meloettaMap.put(1,Species.Gen5Formes.meloettaP);
        map.put(Species.meloetta, meloettaMap);

        Map<Integer,Integer> kyuremMap = new HashMap<>();
        kyuremMap.put(1,Species.Gen5Formes.kyuremW);
        kyuremMap.put(2,Species.Gen5Formes.kyuremB);
        map.put(Species.kyurem, kyuremMap);

        Map<Integer,Integer> keldeoMap = new HashMap<>();
        keldeoMap.put(1,Species.Gen5Formes.keldeoCosmetic1);
        map.put(Species.keldeo, keldeoMap);

        Map<Integer,Integer> tornadusMap = new HashMap<>();
        tornadusMap.put(1,Species.Gen5Formes.tornadusT);
        map.put(Species.tornadus, tornadusMap);

        Map<Integer,Integer> thundurusMap = new HashMap<>();
        thundurusMap.put(1,Species.Gen5Formes.thundurusT);
        map.put(Species.thundurus, thundurusMap);

        Map<Integer,Integer> landorusMap = new HashMap<>();
        landorusMap.put(1,Species.Gen5Formes.landorusT);
        map.put(Species.landorus, landorusMap);

        return map;
    }

    private static Map<Integer,Integer> setupDummyAbsolutePokeNums() {
        Map<Integer,Integer> m = new HashMap<>();
        m.put(255,0);
        return m;
    }

    public static ItemList allowedItems, nonBadItemsBW1, nonBadItemsBW2;
    public static List<Integer> regularShopItems, opShopItems;

    public static String blackBoxLegendaryCheckPrefix1 = "79F6BAEF07B0F0BDC046", blackBoxLegendaryCheckPrefix2 = "DEDB0020C04302B0F8BDC046",
        whiteBoxLegendaryCheckPrefix1 = "00F0FEF8002070BD", whiteBoxLegendaryCheckPrefix2 = "64F62EF970BD0000";

    static {
        setupAllowedItems();
    }

    private static void setupAllowedItems() {
        allowedItems = new ItemList(Items.revealGlass);
        // Key items + version exclusives
        allowedItems.banRange(Items.explorerKit, 76);
        allowedItems.banRange(Items.dataCard01, 32);
        allowedItems.banRange(Items.xtransceiverMale, 18);
        allowedItems.banSingles(Items.libertyPass, Items.propCase, Items.dragonSkull, Items.lightStone, Items.darkStone);
        // Unknown blank items or version exclusives
        allowedItems.banRange(Items.tea, 3);
        allowedItems.banRange(Items.unused120, 14);
        // TMs & HMs - tms cant be held in gen5
        allowedItems.tmRange(Items.tm01, 92);
        allowedItems.tmRange(Items.tm93, 3);
        allowedItems.banRange(Items.tm01, 100);
        allowedItems.banRange(Items.tm93, 3);
        // Battle Launcher exclusives
        allowedItems.banRange(Items.direHit2, 24);

        // non-bad items
        // ban specific pokemon hold items, berries, apricorns, mail
        nonBadItemsBW2 = allowedItems.copy();

        nonBadItemsBW2.banSingles(Items.oddKeystone, Items.griseousOrb, Items.soulDew, Items.lightBall,
                Items.oranBerry, Items.quickPowder, Items.passOrb);
        nonBadItemsBW2.banRange(Items.growthMulch, 4); // mulch
        nonBadItemsBW2.banRange(Items.adamantOrb, 2); // orbs
        nonBadItemsBW2.banRange(Items.mail1, 12); // mails
        nonBadItemsBW2.banRange(Items.figyBerry, 25); // berries without useful battle effects
        nonBadItemsBW2.banRange(Items.luckyPunch, 4); // pokemon specific
        nonBadItemsBW2.banRange(Items.redScarf, 5); // contest scarves

        // Ban the shards in BW1; even the maniac only gives you $200 for them, and they serve no other purpose.
        nonBadItemsBW1 = nonBadItemsBW2.copy();
        nonBadItemsBW1.banRange(Items.redShard, 4);

        regularShopItems = new ArrayList<>();

        regularShopItems.addAll(IntStream.rangeClosed(Items.ultraBall, Items.pokeBall).boxed().collect(Collectors.toList()));
        regularShopItems.addAll(IntStream.rangeClosed(Items.potion, Items.revive).boxed().collect(Collectors.toList()));
        regularShopItems.addAll(IntStream.rangeClosed(Items.superRepel, Items.repel).boxed().collect(Collectors.toList()));

        opShopItems = new ArrayList<>();

        // "Money items" etc
        opShopItems.add(Items.lavaCookie);
        opShopItems.add(Items.berryJuice);
        opShopItems.add(Items.rareCandy);
        opShopItems.add(Items.oldGateau);
        opShopItems.addAll(IntStream.rangeClosed(Items.blueFlute, Items.shoalShell).boxed().collect(Collectors.toList()));
        opShopItems.addAll(IntStream.rangeClosed(Items.tinyMushroom, Items.nugget).boxed().collect(Collectors.toList()));
        opShopItems.add(Items.rareBone);
        opShopItems.addAll(IntStream.rangeClosed(Items.lansatBerry, Items.rowapBerry).boxed().collect(Collectors.toList()));
        opShopItems.add(Items.luckyEgg);
        opShopItems.add(Items.prettyFeather);
        opShopItems.addAll(IntStream.rangeClosed(Items.balmMushroom, Items.casteliacone).boxed().collect(Collectors.toList()));
    }

    public static ItemList getNonBadItems(int romType) {
        if (romType == Gen5Constants.Type_BW2) {
            return nonBadItemsBW2;
        } else {
            return nonBadItemsBW1;
        }
    }

    public static final Map<Integer,Integer> balancedItemPrices = Stream.of(new Integer[][] {
            // Skip item index 0. All prices divided by 10
            {Items.masterBall, 300},
            {Items.ultraBall, 120},
            {Items.greatBall, 60},
            {Items.pokeBall, 20},
            {Items.safariBall, 50},
            {Items.netBall, 100},
            {Items.diveBall, 100},
            {Items.nestBall, 100},
            {Items.repeatBall, 100},
            {Items.timerBall, 100},
            {Items.luxuryBall, 100},
            {Items.premierBall, 20},
            {Items.duskBall, 100},
            {Items.healBall, 30},
            {Items.quickBall, 100},
            {Items.cherishBall, 20},
            {Items.potion, 30},
            {Items.antidote, 10},
            {Items.burnHeal, 25},
            {Items.iceHeal, 25},
            {Items.awakening, 25},
            {Items.paralyzeHeal, 20},
            {Items.fullRestore, 300},
            {Items.maxPotion, 250},
            {Items.hyperPotion, 120},
            {Items.superPotion, 70},
            {Items.fullHeal, 60},
            {Items.revive, 150},
            {Items.maxRevive, 400},
            {Items.freshWater, 40},
            {Items.sodaPop, 60},
            {Items.lemonade, 70},
            {Items.moomooMilk, 80},
            {Items.energyPowder, 40},
            {Items.energyRoot, 110},
            {Items.healPowder, 45},
            {Items.revivalHerb, 280},
            {Items.ether, 300},
            {Items.maxEther, 450},
            {Items.elixir, 1500},
            {Items.maxElixir, 1800},
            {Items.lavaCookie, 45},
            {Items.berryJuice, 10},
            {Items.sacredAsh, 1000},
            {Items.hpUp, 980},
            {Items.protein, 980},
            {Items.iron, 980},
            {Items.carbos, 980},
            {Items.calcium, 980},
            {Items.rareCandy, 1000},
            {Items.ppUp, 980},
            {Items.zinc, 980},
            {Items.ppMax, 2490},
            {Items.oldGateau, 45},
            {Items.guardSpec, 70},
            {Items.direHit, 65},
            {Items.xAttack, 50},
            {Items.xDefense, 55},
            {Items.xSpeed, 35},
            {Items.xAccuracy, 95},
            {Items.xSpAtk, 35},
            {Items.xSpDef, 35},
            {Items.pokeDoll, 100},
            {Items.fluffyTail, 100},
            {Items.blueFlute, 2},
            {Items.yellowFlute, 2},
            {Items.redFlute, 2},
            {Items.blackFlute, 2},
            {Items.whiteFlute, 2},
            {Items.shoalSalt, 2},
            {Items.shoalShell, 2},
            {Items.redShard, 40},
            {Items.blueShard, 40},
            {Items.yellowShard, 40},
            {Items.greenShard, 40},
            {Items.superRepel, 50},
            {Items.maxRepel, 70},
            {Items.escapeRope, 55},
            {Items.repel, 35},
            {Items.sunStone, 300},
            {Items.moonStone, 300},
            {Items.fireStone, 300},
            {Items.thunderStone, 300},
            {Items.waterStone, 300},
            {Items.leafStone, 300},
            {Items.tinyMushroom, 50},
            {Items.bigMushroom, 500},
            {Items.pearl, 140},
            {Items.bigPearl, 750},
            {Items.stardust, 200},
            {Items.starPiece, 980},
            {Items.nugget, 1000},
            {Items.heartScale, 500},
            {Items.honey, 50},
            {Items.growthMulch, 20},
            {Items.dampMulch, 20},
            {Items.stableMulch, 20},
            {Items.gooeyMulch, 20},
            {Items.rootFossil, 500},
            {Items.clawFossil, 500},
            {Items.helixFossil, 500},
            {Items.domeFossil, 500},
            {Items.oldAmber, 800},
            {Items.armorFossil, 500},
            {Items.skullFossil, 500},
            {Items.rareBone, 1000},
            {Items.shinyStone, 300},
            {Items.duskStone, 300},
            {Items.dawnStone, 300},
            {Items.ovalStone, 300},
            {Items.oddKeystone, 210},
            {Items.griseousOrb, 1000},
            {Items.tea, 0}, // unused in Gen 5
            {Items.unused114, 0},
            {Items.autograph, 0}, // unused in Gen 5
            {Items.douseDrive, 100},
            {Items.shockDrive, 100},
            {Items.burnDrive, 100},
            {Items.chillDrive, 100},
            {Items.unused120, 0},
            {Items.pokemonBox, 0}, // unused in Gen 5
            {Items.medicinePocket, 0}, // unused in Gen 5
            {Items.tmCase, 0}, // unused in Gen 5
            {Items.candyJar, 0}, // unused in Gen 5
            {Items.powerUpPocket, 0}, // unused in Gen 5
            {Items.clothingTrunk, 0}, // unused in Gen 5
            {Items.catchingPocket, 0}, // unused in Gen 5
            {Items.battlePocket, 0}, // unused in Gen 5
            {Items.unused129, 0},
            {Items.unused130, 0},
            {Items.unused131, 0},
            {Items.unused132, 0},
            {Items.unused133, 0},
            {Items.sweetHeart, 15},
            {Items.adamantOrb, 1000},
            {Items.lustrousOrb, 1000},
            {Items.mail1, 5},
            {Items.mail2, 5},
            {Items.mail3, 5},
            {Items.mail4, 5},
            {Items.mail5, 5},
            {Items.mail6, 5},
            {Items.mail7, 5},
            {Items.mail8, 5},
            {Items.mail9, 5},
            {Items.mail10, 5},
            {Items.mail11, 5},
            {Items.mail12, 5},
            {Items.cheriBerry, 20},
            {Items.chestoBerry, 25},
            {Items.pechaBerry, 10},
            {Items.rawstBerry, 25},
            {Items.aspearBerry, 25},
            {Items.leppaBerry, 300},
            {Items.oranBerry, 5},
            {Items.persimBerry, 20},
            {Items.lumBerry, 50},
            {Items.sitrusBerry, 50},
            {Items.figyBerry, 10},
            {Items.wikiBerry, 10},
            {Items.magoBerry, 10},
            {Items.aguavBerry, 10},
            {Items.iapapaBerry, 10},
            {Items.razzBerry, 50},
            {Items.blukBerry, 50},
            {Items.nanabBerry, 50},
            {Items.wepearBerry, 50},
            {Items.pinapBerry, 50},
            {Items.pomegBerry, 50},
            {Items.kelpsyBerry, 50},
            {Items.qualotBerry, 50},
            {Items.hondewBerry, 50},
            {Items.grepaBerry, 50},
            {Items.tamatoBerry, 50},
            {Items.cornnBerry, 50},
            {Items.magostBerry, 50},
            {Items.rabutaBerry, 50},
            {Items.nomelBerry, 50},
            {Items.spelonBerry, 50},
            {Items.pamtreBerry, 50},
            {Items.watmelBerry, 50},
            {Items.durinBerry, 50},
            {Items.belueBerry, 50},
            {Items.occaBerry, 100},
            {Items.passhoBerry, 100},
            {Items.wacanBerry, 100},
            {Items.rindoBerry, 100},
            {Items.yacheBerry, 100},
            {Items.chopleBerry, 100},
            {Items.kebiaBerry, 100},
            {Items.shucaBerry, 100},
            {Items.cobaBerry, 100},
            {Items.payapaBerry, 100},
            {Items.tangaBerry, 100},
            {Items.chartiBerry, 100},
            {Items.kasibBerry, 100},
            {Items.habanBerry, 100},
            {Items.colburBerry, 100},
            {Items.babiriBerry, 100},
            {Items.chilanBerry, 100},
            {Items.liechiBerry, 100},
            {Items.ganlonBerry, 100},
            {Items.salacBerry, 100},
            {Items.petayaBerry, 100},
            {Items.apicotBerry, 100},
            {Items.lansatBerry, 100},
            {Items.starfBerry, 100},
            {Items.enigmaBerry, 100},
            {Items.micleBerry, 100},
            {Items.custapBerry, 100},
            {Items.jabocaBerry, 100},
            {Items.rowapBerry, 100},
            {Items.brightPowder, 300},
            {Items.whiteHerb, 100},
            {Items.machoBrace, 300},
            {Items.expShare, 600},
            {Items.quickClaw, 450},
            {Items.sootheBell, 100},
            {Items.mentalHerb, 100},
            {Items.choiceBand, 1000},
            {Items.kingsRock, 500},
            {Items.silverPowder, 200},
            {Items.amuletCoin, 1500},
            {Items.cleanseTag, 100},
            {Items.soulDew, 20},
            {Items.deepSeaTooth, 300},
            {Items.deepSeaScale, 300},
            {Items.smokeBall, 20},
            {Items.everstone, 20},
            {Items.focusBand, 300},
            {Items.luckyEgg, 1000},
            {Items.scopeLens, 500},
            {Items.metalCoat, 300},
            {Items.leftovers, 1000},
            {Items.dragonScale, 300},
            {Items.lightBall, 10},
            {Items.softSand, 200},
            {Items.hardStone, 200},
            {Items.miracleSeed, 200},
            {Items.blackGlasses, 200},
            {Items.blackBelt, 200},
            {Items.magnet, 200},
            {Items.mysticWater, 200},
            {Items.sharpBeak, 200},
            {Items.poisonBarb, 200},
            {Items.neverMeltIce, 200},
            {Items.spellTag, 200},
            {Items.twistedSpoon, 200},
            {Items.charcoal, 200},
            {Items.dragonFang, 200},
            {Items.silkScarf, 200},
            {Items.upgrade, 300},
            {Items.shellBell, 600},
            {Items.seaIncense, 200},
            {Items.laxIncense, 300},
            {Items.luckyPunch, 1},
            {Items.metalPowder, 1},
            {Items.thickClub, 50},
            {Items.leek, 20},
            {Items.redScarf, 10},
            {Items.blueScarf, 10},
            {Items.pinkScarf, 10},
            {Items.greenScarf, 10},
            {Items.yellowScarf, 10},
            {Items.wideLens, 150},
            {Items.muscleBand, 200},
            {Items.wiseGlasses, 200},
            {Items.expertBelt, 600},
            {Items.lightClay, 150},
            {Items.lifeOrb, 1000},
            {Items.powerHerb, 100},
            {Items.toxicOrb, 150},
            {Items.flameOrb, 150},
            {Items.quickPowder, 1},
            {Items.focusSash, 200},
            {Items.zoomLens, 150},
            {Items.metronome, 300},
            {Items.ironBall, 100},
            {Items.laggingTail, 100},
            {Items.destinyKnot, 150},
            {Items.blackSludge, 500},
            {Items.icyRock, 20},
            {Items.smoothRock, 20},
            {Items.heatRock, 20},
            {Items.dampRock, 20},
            {Items.gripClaw, 150},
            {Items.choiceScarf, 1000},
            {Items.stickyBarb, 150},
            {Items.powerBracer, 300},
            {Items.powerBelt, 300},
            {Items.powerLens, 300},
            {Items.powerBand, 300},
            {Items.powerAnklet, 300},
            {Items.powerWeight, 300},
            {Items.shedShell, 50},
            {Items.bigRoot, 150},
            {Items.choiceSpecs, 1000},
            {Items.flamePlate, 200},
            {Items.splashPlate, 200},
            {Items.zapPlate, 200},
            {Items.meadowPlate, 200},
            {Items.iciclePlate, 200},
            {Items.fistPlate, 200},
            {Items.toxicPlate, 200},
            {Items.earthPlate, 200},
            {Items.skyPlate, 200},
            {Items.mindPlate, 200},
            {Items.insectPlate, 200},
            {Items.stonePlate, 200},
            {Items.spookyPlate, 200},
            {Items.dracoPlate, 200},
            {Items.dreadPlate, 200},
            {Items.ironPlate, 200},
            {Items.oddIncense, 200},
            {Items.rockIncense, 200},
            {Items.fullIncense, 100},
            {Items.waveIncense, 200},
            {Items.roseIncense, 200},
            {Items.luckIncense, 1500},
            {Items.pureIncense, 100},
            {Items.protector, 300},
            {Items.electirizer, 300},
            {Items.magmarizer, 300},
            {Items.dubiousDisc, 300},
            {Items.reaperCloth, 300},
            {Items.razorClaw, 500},
            {Items.razorFang, 500},
            {Items.tm01, 1000},
            {Items.tm02, 1000},
            {Items.tm03, 1000},
            {Items.tm04, 1000},
            {Items.tm05, 1000},
            {Items.tm06, 1000},
            {Items.tm07, 2000},
            {Items.tm08, 1000},
            {Items.tm09, 1000},
            {Items.tm10, 1000},
            {Items.tm11, 2000},
            {Items.tm12, 1000},
            {Items.tm13, 1000},
            {Items.tm14, 2000},
            {Items.tm15, 2000},
            {Items.tm16, 2000},
            {Items.tm17, 1000},
            {Items.tm18, 2000},
            {Items.tm19, 1000},
            {Items.tm20, 2000},
            {Items.tm21, 1000},
            {Items.tm22, 1000},
            {Items.tm23, 1000},
            {Items.tm24, 1000},
            {Items.tm25, 2000},
            {Items.tm26, 1000},
            {Items.tm27, 1000},
            {Items.tm28, 1000},
            {Items.tm29, 1000},
            {Items.tm30, 1000},
            {Items.tm31, 1000},
            {Items.tm32, 1000},
            {Items.tm33, 2000},
            {Items.tm34, 1000},
            {Items.tm35, 1000},
            {Items.tm36, 1000},
            {Items.tm37, 2000},
            {Items.tm38, 2000},
            {Items.tm39, 1000},
            {Items.tm40, 1000},
            {Items.tm41, 1000},
            {Items.tm42, 1000},
            {Items.tm43, 1000},
            {Items.tm44, 1000},
            {Items.tm45, 1000},
            {Items.tm46, 1000},
            {Items.tm47, 1000},
            {Items.tm48, 1000},
            {Items.tm49, 1000},
            {Items.tm50, 1000},
            {Items.tm51, 1000},
            {Items.tm52, 1000},
            {Items.tm53, 1000},
            {Items.tm54, 1000},
            {Items.tm55, 1000},
            {Items.tm56, 1000},
            {Items.tm57, 1000},
            {Items.tm58, 1000},
            {Items.tm59, 1000},
            {Items.tm60, 1000},
            {Items.tm61, 1000},
            {Items.tm62, 1000},
            {Items.tm63, 1000},
            {Items.tm64, 1000},
            {Items.tm65, 1000},
            {Items.tm66, 1000},
            {Items.tm67, 1000},
            {Items.tm68, 2000},
            {Items.tm69, 1000},
            {Items.tm70, 1000},
            {Items.tm71, 1000},
            {Items.tm72, 1000},
            {Items.tm73, 1000},
            {Items.tm74, 1000},
            {Items.tm75, 1000},
            {Items.tm76, 1000},
            {Items.tm77, 1000},
            {Items.tm78, 1000},
            {Items.tm79, 1000},
            {Items.tm80, 1000},
            {Items.tm81, 1000},
            {Items.tm82, 1000},
            {Items.tm83, 1000},
            {Items.tm84, 1000},
            {Items.tm85, 1000},
            {Items.tm86, 1000},
            {Items.tm87, 1000},
            {Items.tm88, 1000},
            {Items.tm89, 1000},
            {Items.tm90, 1000},
            {Items.tm91, 1000},
            {Items.tm92, 1000},
            {Items.hm01, 0},
            {Items.hm02, 0},
            {Items.hm03, 0},
            {Items.hm04, 0},
            {Items.hm05, 0},
            {Items.hm06, 0},
            {Items.hm07, 0}, // unused in Gen 5
            {Items.hm08, 0}, // unused in Gen 5
            {Items.explorerKit, 0},
            {Items.lootSack, 0},
            {Items.ruleBook, 0},
            {Items.pokeRadar, 0},
            {Items.pointCard, 0},
            {Items.journal, 0},
            {Items.sealCase, 0},
            {Items.fashionCase, 0},
            {Items.sealBag, 0},
            {Items.palPad, 0},
            {Items.worksKey, 0},
            {Items.oldCharm, 0},
            {Items.galacticKey, 0},
            {Items.redChain, 0},
            {Items.townMap, 0},
            {Items.vsSeeker, 0},
            {Items.coinCase, 0},
            {Items.oldRod, 0},
            {Items.goodRod, 0},
            {Items.superRod, 0},
            {Items.sprayduck, 0},
            {Items.poffinCase, 0},
            {Items.bike, 0},
            {Items.suiteKey, 0},
            {Items.oaksLetter, 0},
            {Items.lunarWing, 0},
            {Items.memberCard, 0},
            {Items.azureFlute, 0},
            {Items.ssTicketJohto, 0},
            {Items.contestPass, 0},
            {Items.magmaStone, 0},
            {Items.parcelSinnoh, 0},
            {Items.coupon1, 0},
            {Items.coupon2, 0},
            {Items.coupon3, 0},
            {Items.storageKeySinnoh, 0},
            {Items.secretPotion, 0},
            {Items.vsRecorder, 0},
            {Items.gracidea, 0},
            {Items.secretKeySinnoh, 0},
            {Items.apricornBox, 0},
            {Items.unownReport, 0},
            {Items.berryPots, 0},
            {Items.dowsingMachine, 0},
            {Items.blueCard, 0},
            {Items.slowpokeTail, 0},
            {Items.clearBell, 0},
            {Items.cardKeyJohto, 0},
            {Items.basementKeyJohto, 0},
            {Items.squirtBottle, 0},
            {Items.redScale, 0},
            {Items.lostItem, 0},
            {Items.pass, 0},
            {Items.machinePart, 0},
            {Items.silverWing, 0},
            {Items.rainbowWing, 0},
            {Items.mysteryEgg, 0},
            {Items.redApricorn, 2},
            {Items.blueApricorn, 2},
            {Items.yellowApricorn, 2},
            {Items.greenApricorn, 2},
            {Items.pinkApricorn, 2},
            {Items.whiteApricorn, 2},
            {Items.blackApricorn, 2},
            {Items.fastBall, 30},
            {Items.levelBall, 30},
            {Items.lureBall, 30},
            {Items.heavyBall, 30},
            {Items.loveBall, 30},
            {Items.friendBall, 30},
            {Items.moonBall, 30},
            {Items.sportBall, 30},
            {Items.parkBall, 0},
            {Items.photoAlbum, 0},
            {Items.gbSounds, 0},
            {Items.tidalBell, 0},
            {Items.rageCandyBar, 1500},
            {Items.dataCard01, 0},
            {Items.dataCard02, 0},
            {Items.dataCard03, 0},
            {Items.dataCard04, 0},
            {Items.dataCard05, 0},
            {Items.dataCard06, 0},
            {Items.dataCard07, 0},
            {Items.dataCard08, 0},
            {Items.dataCard09, 0},
            {Items.dataCard10, 0},
            {Items.dataCard11, 0},
            {Items.dataCard12, 0},
            {Items.dataCard13, 0},
            {Items.dataCard14, 0},
            {Items.dataCard15, 0},
            {Items.dataCard16, 0},
            {Items.dataCard17, 0},
            {Items.dataCard18, 0},
            {Items.dataCard19, 0},
            {Items.dataCard20, 0},
            {Items.dataCard21, 0},
            {Items.dataCard22, 0},
            {Items.dataCard23, 0},
            {Items.dataCard24, 0},
            {Items.dataCard25, 0},
            {Items.dataCard26, 0},
            {Items.dataCard27, 0},
            {Items.jadeOrb, 0},
            {Items.lockCapsule, 0},
            {Items.redOrb, 0},
            {Items.blueOrb, 0},
            {Items.enigmaStone, 0},
            {Items.prismScale, 300},
            {Items.eviolite, 1000},
            {Items.floatStone, 100},
            {Items.rockyHelmet, 600},
            {Items.airBalloon, 100},
            {Items.redCard, 100},
            {Items.ringTarget, 100},
            {Items.bindingBand, 200},
            {Items.absorbBulb, 100},
            {Items.cellBattery, 100},
            {Items.ejectButton, 100},
            {Items.fireGem, 100},
            {Items.waterGem, 100},
            {Items.electricGem, 100},
            {Items.grassGem, 100},
            {Items.iceGem, 100},
            {Items.fightingGem, 100},
            {Items.poisonGem, 100},
            {Items.groundGem, 100},
            {Items.flyingGem, 100},
            {Items.psychicGem, 100},
            {Items.bugGem, 100},
            {Items.rockGem, 100},
            {Items.ghostGem, 100},
            {Items.dragonGem, 100},
            {Items.darkGem, 100},
            {Items.steelGem, 100},
            {Items.normalGem, 100},
            {Items.healthFeather, 300},
            {Items.muscleFeather, 300},
            {Items.resistFeather, 300},
            {Items.geniusFeather, 300},
            {Items.cleverFeather, 300},
            {Items.swiftFeather, 300},
            {Items.prettyFeather, 20},
            {Items.coverFossil, 500},
            {Items.plumeFossil, 500},
            {Items.libertyPass, 0},
            {Items.passOrb, 20},
            {Items.dreamBall, 100},
            {Items.pokeToy, 100},
            {Items.propCase, 0},
            {Items.dragonSkull, 0},
            {Items.balmMushroom, 0},
            {Items.bigNugget, 0},
            {Items.pearlString, 0},
            {Items.cometShard, 0},
            {Items.relicCopper, 0},
            {Items.relicSilver, 0},
            {Items.relicGold, 0},
            {Items.relicVase, 0},
            {Items.relicBand, 0},
            {Items.relicStatue, 0},
            {Items.relicCrown, 0},
            {Items.casteliacone, 45},
            {Items.direHit2, 0},
            {Items.xSpeed2, 0},
            {Items.xSpAtk2, 0},
            {Items.xSpDef2, 0},
            {Items.xDefense2, 0},
            {Items.xAttack2, 0},
            {Items.xAccuracy2, 0},
            {Items.xSpeed3, 0},
            {Items.xSpAtk3, 0},
            {Items.xSpDef3, 0},
            {Items.xDefense3, 0},
            {Items.xAttack3, 0},
            {Items.xAccuracy3, 0},
            {Items.xSpeed6, 0},
            {Items.xSpAtk6, 0},
            {Items.xSpDef6, 0},
            {Items.xDefense6, 0},
            {Items.xAttack6, 0},
            {Items.xAccuracy6, 0},
            {Items.abilityUrge, 0},
            {Items.itemDrop, 0},
            {Items.itemUrge, 0},
            {Items.resetUrge, 0},
            {Items.direHit3, 0},
            {Items.lightStone, 0},
            {Items.darkStone, 0},
            {Items.tm93, 1000},
            {Items.tm94, 1000},
            {Items.tm95, 1000},
            {Items.xtransceiverMale, 0},
            {Items.unused622, 0},
            {Items.gram1, 0},
            {Items.gram2, 0},
            {Items.gram3, 0},
            {Items.xtransceiverFemale, 0},
            {Items.medalBox, 0},
            {Items.dNASplicersFuse, 0},
            {Items.dNASplicersSeparate, 0},
            {Items.permit, 0},
            {Items.ovalCharm, 0},
            {Items.shinyCharm, 0},
            {Items.plasmaCard, 0},
            {Items.grubbyHanky, 0},
            {Items.colressMachine, 0},
            {Items.droppedItemCurtis, 0},
            {Items.droppedItemYancy, 0},
            {Items.revealGlass, 0}
    }).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

    /* @formatter:off */
    @SuppressWarnings("unused")
    private static final int[][] habitatListEntries = {
        { 104, 105 }, // Route 4
        { 124 }, // Route 15
        { 134 }, // Route 21
        { 84, 85, 86 }, // Clay Tunnel
        { 23, 24, 25, 26 }, // Twist Mountain
        { 97 }, // Village Bridge
        { 27, 28, 29, 30 }, // Dragonspiral Tower
        { 81, 82, 83 }, // Relic Passage
        { 106 }, // Route 5*
        { 125 }, // Route 16*
        { 98 }, // Marvelous Bridge
        { 123 }, // Abundant Shrine
        { 132 }, // Undella Town
        { 107 }, // Route 6
        { 43 }, // Undella Bay
        { 102, 103 }, // Wellspring Cave
        { 95 }, // Nature Preserve
        { 127 }, // Route 18
        { 32, 33, 34, 35, 36 }, // Giant Chasm
        { 111 }, // Route 7
        { 31, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80 }, // Victory Road
        { 12, 13, 14, 15, 16, 17, 18, 19 }, // Relic Castle
        { 0 }, // Striation City
        { 128 }, // Route 19
        { 3 }, // Aspertia City
        { 116 }, // Route 8*
        { 44, 45 }, // Floccesy Ranch
        { 61, 62, 63, 64, 65, 66, 67, 68, 69, 70 }, // Strange House
        { 129 }, // Route 20
        { 4 }, // Virbank City
        { 37, 38, 39, 40, 41 }, // Castelia Sewers
        { 118 }, // Route 9
        { 46, 47 }, // Virbank Complex
        { 42 }, // P2 Laboratory
        { 1 }, // Castelia City
        { 8, 9 }, // Pinwheel Forest
        { 5 }, // Humilau City
        { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60 }, // Reversal Mountain
        { 6, 7 }, // Dreamyard
        { 112, 113, 114, 115 }, // Celestial Tower
        { 130 }, // Route 22
        { 10, 11 }, // Desert Resort
        { 119 }, // Route 11
        { 133 }, // Route 17
        { 99 }, // Route 1
        { 131 }, // Route 23
        { 2 }, // Icirrus City*
        { 120 }, // Route 12
        { 100 }, // Route 2
        { 108, 109 }, // Mistralton Cave
        { 121 }, // Route 13
        { 101 }, // Route 3
        { 117 }, // Moor of Icirrus*
        { 96 }, // Driftveil Drawbridge
        { 93, 94 }, // Seaside Cave
        { 126 }, // Lostlorn Forest
        { 122 }, // Route 14
        { 20, 21, 22 }, // Chargestone Cave
    };

    private static final int[] bw1WildFileToAreaMap = {
        2,
        6,
        8,
        18, 18,
        19, 19,
        20, 20,
        21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, // lol
        22,
        23, 23, 23,
        24, 24, 24, 24,
        25, 25, 25, 25,
        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
        27, 27, 27, 27,
        29,
        36,
        57,
        59,
        60,
        38,
        39,
        40,
        30, 30,
        41,
        42,
        43,
        31, 31, 31,
        44,
        33, 33, 33, 33,
        45,
        34,
        46,
        32, 32, 32,
        47, 47,
        48,
        49,
        50,
        51,
        35,
        52,
        53,
        37,
        55,
        12,
        54,
    };
    
    private static final int[] bw2WildFileToAreaMap = {
        2,
        4,
        8,
        59,
        61,
        63,
        19, 19,
        20, 20,
        21, 21,
        22, 22, 22, 22, 22, 22, 22, 22,
        24, 24, 24,
        25, 25, 25, 25,
        26, 26, 26, 26,
        76,
        27, 27, 27, 27, 27,
        70, 70, 70, 70, 70,
        29,
        35,
        71, 71,
        72, 72,
        73, 73, 73, 73, 73, 73, 73, 73, 73, 73, 73, 73, 73,
        74, 74, 74, 74, 74, 74, 74, 74, 74, 74,
        76, 76, 76, 76, 76, 76, 76, 76, 76, 76,
        77, 77, 77,
        79, 79, 79, 79, 79, 79, 79, 79, 79,
        78, 78,
        -1, // Nature Preserve (not on map)
        55,
        57,
        58,
        37,
        38,
        39,
        30, 30,
        40, 40,
        41,
        42,
        31, 31, 31,
        43,
        32, 32, 32, 32,
        44,
        33,
        45,
        46,
        47,
        48,
        49,
        34,
        50,
        51,
        36,
        53,
        66,
        67,
        69,
        75,
        12,
        52,
        68,
    };

    public static void tagTrainersBW(List<Trainer> trs) {
        // We use different Gym IDs to cheat the system for the 3 n00bs
        // Chili, Cress, and Cilan
        // Cilan can be GYM1, then Chili is GYM9 and Cress GYM10
        // Also their *trainers* are GYM11 lol

        // Gym Trainers
        tag(trs, "GYM11", 0x09, 0x0A);
        tag(trs, "GYM2", 0x56, 0x57, 0x58);
        tag(trs, "GYM3", 0xC4, 0xC6, 0xC7, 0xC8);
        tag(trs, "GYM4", 0x42, 0x43, 0x44, 0x45);
        tag(trs, "GYM5", 0xC9, 0xCA, 0xCB, 0x5F, 0xA8);
        tag(trs, "GYM6", 0x7D, 0x7F, 0x80, 0x46, 0x47);
        tag(trs, "GYM7", 0xD7, 0xD8, 0xD9, 0xD4, 0xD5, 0xD6);
        tag(trs, "GYM8", 0x109, 0x10A, 0x10F, 0x10E, 0x110, 0x10B, 0x113, 0x112);

        // Gym Leaders
        tag(trs, 0x0C, "GYM1-LEADER"); // Cilan
        tag(trs, 0x0B, "GYM9-LEADER"); // Chili
        tag(trs, 0x0D, "GYM10-LEADER"); // Cress
        tag(trs, 0x15, "GYM2-LEADER"); // Lenora
        tag(trs, 0x16, "GYM3-LEADER"); // Burgh
        tag(trs, 0x17, "GYM4-LEADER"); // Elesa
        tag(trs, 0x18, "GYM5-LEADER"); // Clay
        tag(trs, 0x19, "GYM6-LEADER"); // Skyla
        tag(trs, 0x83, "GYM7-LEADER"); // Brycen
        tag(trs, 0x84, "GYM8-LEADER"); // Iris or Drayden
        tag(trs, 0x85, "GYM8-LEADER"); // Iris or Drayden

        // Elite 4
        tag(trs, 0xE4, "ELITE1"); // Shauntal
        tag(trs, 0xE6, "ELITE2"); // Grimsley
        tag(trs, 0xE7, "ELITE3"); // Caitlin
        tag(trs, 0xE5, "ELITE4"); // Marshal

        // Elite 4 R2
        tag(trs, 0x233, "ELITE1"); // Shauntal
        tag(trs, 0x235, "ELITE2"); // Grimsley
        tag(trs, 0x236, "ELITE3"); // Caitlin
        tag(trs, 0x234, "ELITE4"); // Marshal
        tag(trs, 0x197, "CHAMPION"); // Alder

        // Ubers?
        tag(trs, 0x21E, "UBER"); // Game Freak Guy
        tag(trs, 0x237, "UBER"); // Cynthia
        tag(trs, 0xE8, "UBER"); // Ghetsis
        tag(trs, 0x24A, "UBER"); // N-White
        tag(trs, 0x24B, "UBER"); // N-Black

        // Rival - Cheren
        tagRivalBW(trs, "RIVAL1", 0x35);
        tagRivalBW(trs, "RIVAL2", 0x11F);
        tagRivalBW(trs, "RIVAL3", 0x38); // used for 3rd battle AND tag battle
        tagRivalBW(trs, "RIVAL4", 0x193);
        tagRivalBW(trs, "RIVAL5", 0x5A); // 5th battle & 2nd tag battle
        tagRivalBW(trs, "RIVAL6", 0x21B);
        tagRivalBW(trs, "RIVAL7", 0x24C);
        tagRivalBW(trs, "RIVAL8", 0x24F);

        // Rival - Bianca
        tagRivalBW(trs, "FRIEND1", 0x3B);
        tagRivalBW(trs, "FRIEND2", 0x1F2);
        tagRivalBW(trs, "FRIEND3", 0x1FB);
        tagRivalBW(trs, "FRIEND4", 0x1EB);
        tagRivalBW(trs, "FRIEND5", 0x1EE);
        tagRivalBW(trs, "FRIEND6", 0x252);

        // N
        tag(trs, "NOTSTRONG", 64);
        tag(trs, "STRONG", 65, 89, 218);
    }

    public static void tagTrainersBW2(List<Trainer> trs) {
        // Use GYM9/10/11 for the retired Chili/Cress/Cilan.
        // Lenora doesn't have a team, or she'd be 12.
        // Likewise for Brycen

        // Some trainers have TWO teams because of Challenge Mode
        // I believe this is limited to Gym Leaders, E4, Champ...
        // The "Challenge Mode" teams have levels at similar to regular,
        // but have the normal boost applied too.

        // Gym Trainers
        tag(trs, "GYM1", 0xab, 0xac);
        tag(trs, "GYM2", 0xb2, 0xb3);
        tag(trs, "GYM3", 0x2de, 0x2df, 0x2e0, 0x2e1);
        // GYM4: old gym site included to give the city a theme
        tag(trs, "GYM4", 0x26d, 0x94, 0xcf, 0xd0, 0xd1); // 0x94 might be 0x324
        tag(trs, "GYM5", 0x13f, 0x140, 0x141, 0x142, 0x143, 0x144, 0x145);
        tag(trs, "GYM6", 0x95, 0x96, 0x97, 0x98, 0x14c);
        tag(trs, "GYM7", 0x17d, 0x17e, 0x17f, 0x180, 0x181);
        tag(trs, "GYM8", 0x15e, 0x15f, 0x160, 0x161, 0x162, 0x163);

        // Gym Leaders
        // Order: Normal, Challenge Mode
        // All the challenge mode teams are near the end of the ROM
        // which makes things a bit easier.
        tag(trs, "GYM1-LEADER", 0x9c, 0x2fc); // Cheren
        tag(trs, "GYM2-LEADER", 0x9d, 0x2fd); // Roxie
        tag(trs, "GYM3-LEADER", 0x9a, 0x2fe); // Burgh
        tag(trs, "GYM4-LEADER", 0x99, 0x2ff); // Elesa
        tag(trs, "GYM5-LEADER", 0x9e, 0x300); // Clay
        tag(trs, "GYM6-LEADER", 0x9b, 0x301); // Skyla
        tag(trs, "GYM7-LEADER", 0x9f, 0x302); // Drayden
        tag(trs, "GYM8-LEADER", 0xa0, 0x303); // Marlon

        // Elite 4 / Champion
        // Order: Normal, Challenge Mode, Rematch, Rematch Challenge Mode
        tag(trs, "ELITE1", 0x26, 0x304, 0x8f, 0x309);
        tag(trs, "ELITE2", 0x28, 0x305, 0x91, 0x30a);
        tag(trs, "ELITE3", 0x29, 0x307, 0x92, 0x30c);
        tag(trs, "ELITE4", 0x27, 0x306, 0x90, 0x30b);
        tag(trs, "CHAMPION", 0x155, 0x308, 0x218, 0x30d);

        // Rival - Hugh
        tagRivalBW(trs, "RIVAL1", 0xa1); // Start
        tagRivalBW(trs, "RIVAL2", 0xa6); // Floccessy Ranch
        tagRivalBW(trs, "RIVAL3", 0x24c); // Tag Battles in the sewers
        tagRivalBW(trs, "RIVAL4", 0x170); // Tag Battle on the Plasma Frigate
        tagRivalBW(trs, "RIVAL5", 0x17a); // Undella Town 1st visit
        tagRivalBW(trs, "RIVAL6", 0x2bd); // Lacunosa Town Tag Battle
        tagRivalBW(trs, "RIVAL7", 0x31a); // 2nd Plasma Frigate Tag Battle
        tagRivalBW(trs, "RIVAL8", 0x2ac); // Victory Road
        tagRivalBW(trs, "RIVAL9", 0x2b5); // Undella Town Post-E4
        tagRivalBW(trs, "RIVAL10", 0x2b8); // Driftveil Post-Undella-Battle

        // Tag Battle with Opposite Gender Hero
        tagRivalBW(trs, "FRIEND1", 0x168);
        tagRivalBW(trs, "FRIEND1", 0x16b);

        // Tag/PWT Battles with Cheren
        tag(trs, "GYM1", 0x173, 0x278, 0x32E);

        // The Restaurant Brothers
        tag(trs, "GYM9-LEADER", 0x1f0); // Cilan
        tag(trs, "GYM10-LEADER", 0x1ee); // Chili
        tag(trs, "GYM11-LEADER", 0x1ef); // Cress

        // Themed Trainers
        tag(trs, "THEMED:ZINZOLIN-STRONG", 0x2c0, 0x248, 0x15b, 0x1f1);
        tag(trs, "THEMED:COLRESS-STRONG", 0x166, 0x158, 0x32d, 0x32f);
        tag(trs, "THEMED:SHADOW1", 0x247, 0x15c, 0x2af);
        tag(trs, "THEMED:SHADOW2", 0x1f2, 0x2b0);
        tag(trs, "THEMED:SHADOW3", 0x1f3, 0x2b1);

        // Uber-Trainers
        // There are *fourteen* ubers of 17 allowed (incl. the champion)
        // It's a rather stacked game...
        tag(trs, 0x246, "UBER"); // Alder
        tag(trs, 0x1c8, "UBER"); // Cynthia
        tag(trs, 0xca, "UBER"); // Benga/BlackTower
        tag(trs, 0xc9, "UBER"); // Benga/WhiteTreehollow
        tag(trs, 0x5, "UBER"); // N/Zekrom
        tag(trs, 0x6, "UBER"); // N/Reshiram
        tag(trs, 0x30e, "UBER"); // N/Spring
        tag(trs, 0x30f, "UBER"); // N/Summer
        tag(trs, 0x310, "UBER"); // N/Autumn
        tag(trs, 0x311, "UBER"); // N/Winter
        tag(trs, 0x159, "UBER"); // Ghetsis
        tag(trs, 0x8c, "UBER"); // Game Freak Guy
        tag(trs, 0x24f, "UBER"); // Game Freak Leftovers Guy

    }

    private static void tagRivalBW(List<Trainer> allTrainers, String tag, int offset) {
        allTrainers.get(offset - 1).tag = tag + "-0";
        allTrainers.get(offset).tag = tag + "-1";
        allTrainers.get(offset + 1).tag = tag + "-2";

    }

    private static void tag(List<Trainer> allTrainers, int number, String tag) {
        if (allTrainers.size() > (number - 1)) {
            allTrainers.get(number - 1).tag = tag;
        }
    }

    private static void tag(List<Trainer> allTrainers, String tag, int... numbers) {
        for (int num : numbers) {
            if (allTrainers.size() > (num - 1)) {
                allTrainers.get(num - 1).tag = tag;
            }
        }
    }

    public static final HashMap<String, Type> gymAndEliteThemesBW = setupGymAndEliteThemesBW();

    private static HashMap<String, Type> setupGymAndEliteThemesBW() {
        HashMap<String, Type> themeMap = new HashMap<>();
        //Alder has no theme
        themeMap.put("ELITE1", Type.GHOST); //Shauntal
        themeMap.put("ELITE2", Type.DARK); //Grimsley
        themeMap.put("ELITE3", Type.PSYCHIC); //Caitlin
        themeMap.put("ELITE4", Type.FIGHTING); //Marshal
        themeMap.put("GYM1", Type.GRASS); //Cilan
        themeMap.put("GYM2", Type.NORMAL); //Lenora
        themeMap.put("GYM3", Type.BUG); //Burgh
        themeMap.put("GYM4", Type.ELECTRIC); //Elesa
        themeMap.put("GYM5", Type.GROUND); //Clay
        themeMap.put("GYM6", Type.FLYING); //Skyla
        themeMap.put("GYM7", Type.ICE); //Brycen
        themeMap.put("GYM8", Type.DRAGON); //Iris
        themeMap.put("GYM9", Type.FIRE); //Chili
        themeMap.put("GYM10", Type.WATER); //Cress
        themeMap.put("GYM11", Type.NORMAL); //Trio gym trainers
        return themeMap;
    }

    public static final HashMap<String, Type> gymAndEliteThemesBW2 = setupGymAndEliteThemesBW2();

    private static HashMap<String, Type> setupGymAndEliteThemesBW2() {
        HashMap<String, Type> themeMap = new HashMap<>();
        themeMap.put("CHAMPION", Type.DRAGON); //Iris
        themeMap.put("ELITE1", Type.GHOST); //Shauntal
        themeMap.put("ELITE2", Type.DARK); //Grimsley
        themeMap.put("ELITE3", Type.PSYCHIC); //Caitlin
        themeMap.put("ELITE4", Type.FIGHTING); //Marshal
        themeMap.put("GYM1", Type.NORMAL); //Cheren
        themeMap.put("GYM2", Type.POISON); //Roxie
        themeMap.put("GYM3", Type.BUG); //Burgh
        themeMap.put("GYM4", Type.ELECTRIC); //Elesa
        themeMap.put("GYM5", Type.GROUND); //Clay
        themeMap.put("GYM6", Type.FLYING); //Skyla
        themeMap.put("GYM7", Type.DRAGON); //Drayden
        themeMap.put("GYM8", Type.WATER); //Marlon
        //The trio gym is no longer an official League gym, so I'm not including it.
        //(Besides, their theming works as-is.)
        return themeMap;
    }

    public static void setMultiBattleStatusBW(List<Trainer> trs) {
        // 62 + 63: Multi Battle with Team Plasma Grunts in Wellspring Cave w/ Cheren
        // 401 + 402: Double Battle with Preschooler Sarah and Preschooler Billy
        setMultiBattleStatus(trs, Trainer.MultiBattleStatus.ALWAYS, 62, 63, 401, 402);
    }

    public static void setMultiBattleStatusBW2(List<Trainer> trs, boolean isBlack2) {
        // 342 + 356: Multi Battle with Team Plasma Grunts in Castelia Sewers w/ Hugh
        // 347 + 797: Multi Battle with Team Plasma Zinzolin and Team Plasma Grunt on Plasma Frigate w/ Hugh
        // 374 + 375: Multi Battle with Team Plasma Grunts on Plasma Frigate w/ Cheren
        // 376 + 377: Multi Battle with Team Plasma Grunts on Plasma Frigate w/ Hugh
        // 494 + 495 + 496: Cilan, Chili, and Cress all participate in a Multi Battle
        // 614 + 615: Double Battle with Veteran Claude and Veteran Cecile
        // 643 + 644: Double Battle with Veteran Sinan and Veteran Rosaline
        // 704 + 705: Multi Battle with Team Plasma Zinzolin and Team Plasma Grunt in Lacunosa Town w/ Hugh
        // 798 + 799: Multi Battle with Team Plasma Grunts on Plasma Frigate w/ Hugh
        // 807 + 809: Double Battle with Team Plasma Grunts on Plasma Frigate
        setMultiBattleStatus(trs, Trainer.MultiBattleStatus.ALWAYS, 342, 347, 356, 374, 375, 376, 377, 494,
                495, 496, 614, 615, 643, 644, 704, 705, 797, 798, 799, 807, 809
        );

        // 513/788 + 522: Potential Double Battle with Backpacker Kiyo (513 in B2, 788 in W2) and Hiker Markus
        // 519/786 + 520/787: Potential Double Battle with Ace Trainer Ray (519 in W2, 786 in B2) and Ace Trainer Cora (520 in B2, 787 in W2)
        // 602 + 603: Potential Double Battle with Ace Trainer Webster and Ace Trainer Shanta
        // 790 + 791: Potential Double Battle with Nursery Aide Rosalyn and Preschooler Ike
        // 792 + 793: Potential Double Battle with Youngster Henley and Lass Helia
        setMultiBattleStatus(trs, Trainer.MultiBattleStatus.POTENTIAL, 513, 522, 602, 603, 788, 790, 791, 792, 793);

        if (isBlack2) {
            // 789 + 521: Double Battle with Backpacker Kumiko and Hiker Jared
            setMultiBattleStatus(trs, Trainer.MultiBattleStatus.ALWAYS, 521, 789);

            // 786 + 520: Potential Double Batlte with Ace Trainer Ray and Ace Trainer Cora
            setMultiBattleStatus(trs, Trainer.MultiBattleStatus.POTENTIAL, 520, 786);
        } else {
            // 514 + 521: Potential Double Battle with Backpacker Kumiko and Hiker Jared
            setMultiBattleStatus(trs, Trainer.MultiBattleStatus.POTENTIAL, 514, 521);

            // 519 + 787: Double Battle with Ace Trainer Ray and Ace Trainer Cora
            setMultiBattleStatus(trs, Trainer.MultiBattleStatus.ALWAYS, 519, 787);
        }
    }

    private static void setMultiBattleStatus(List<Trainer> allTrainers, Trainer.MultiBattleStatus status, int... numbers) {
        for (int num : numbers) {
            if (allTrainers.size() > (num - 1)) {
                allTrainers.get(num - 1).multiBattleStatus = status;
            }
        }
    }

}
