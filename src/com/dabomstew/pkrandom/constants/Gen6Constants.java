package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  Gen6Constants.java - Constants for X/Y/Omega Ruby/Alpha Sapphire      --*/
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

import com.dabomstew.pkrandom.pokemon.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Gen6Constants {

    public static final int Type_XY = N3DSConstants.Type_XY;
    public static final int Type_ORAS = N3DSConstants.Type_ORAS;

    public static final int pokemonCount = 721;
    private static final int xyFormeCount = 77, orasFormeCount = 104;
    private static final int orasformeMovesetOffset = 35;

    public static final List<Integer> actuallyCosmeticForms = Arrays.asList(
            Species.Gen6Formes.cherrimCosmetic1,
            Species.Gen6Formes.keldeoCosmetic1,
            Species.Gen6Formes.furfrouCosmetic1, Species.Gen6Formes.furfrouCosmetic2,
            Species.Gen6Formes.furfrouCosmetic3, Species.Gen6Formes.furfrouCosmetic4,
            Species.Gen6Formes.furfrouCosmetic5, Species.Gen6Formes.furfrouCosmetic6,
            Species.Gen6Formes.furfrouCosmetic7, Species.Gen6Formes.furfrouCosmetic8,
            Species.Gen6Formes.furfrouCosmetic9,
            Species.Gen6Formes.pumpkabooCosmetic1, Species.Gen6Formes.pumpkabooCosmetic2,
            Species.Gen6Formes.pumpkabooCosmetic3,
            Species.Gen6Formes.gourgeistCosmetic1, Species.Gen6Formes.gourgeistCosmetic2,
            Species.Gen6Formes.gourgeistCosmetic3,
            Species.Gen6Formes.floetteCosmetic1, Species.Gen6Formes.floetteCosmetic2,
            Species.Gen6Formes.floetteCosmetic3, Species.Gen6Formes.floetteCosmetic4,
            Species.Gen6Formes.pikachuCosmetic1, Species.Gen6Formes.pikachuCosmetic2,
            Species.Gen6Formes.pikachuCosmetic3, Species.Gen6Formes.pikachuCosmetic4,
            Species.Gen6Formes.pikachuCosmetic5, Species.Gen6Formes.pikachuCosmetic6 // Cosplay Pikachu
    );

    public static final String criesTablePrefixXY = "60000A006B000A0082000A003D010A00";

    public static final String introPokemonModelOffsetXY = "01000400020002000200000003000000";
    public static final String introInitialCryOffset1XY = "3AFEFFEB000055E31400D40507005001";
    public static final String introInitialCryOffset2XY = "0800A0E110FEFFEB000057E31550C405";
    public static final String introInitialCryOffset3XY = "0020E0E30310A0E1E4FDFFEB0000A0E3";
    public static final String introRepeatedCryOffsetXY = "1080BDE800002041000000008D001000";

    public static final Map<Integer,List<Integer>> speciesToMegaStoneXY = setupSpeciesToMegaStone(Type_XY);
    public static final Map<Integer,List<Integer>> speciesToMegaStoneORAS = setupSpeciesToMegaStone(Type_ORAS);

    public static final Map<Integer,String> formeSuffixes = setupFormeSuffixes();
    public static final Map<Integer,String> dummyFormeSuffixes = setupDummyFormeSuffixes();
    public static final Map<Integer,Map<Integer,String>> formeSuffixesByBaseForme = setupFormeSuffixesByBaseForme();

    public static String getFormeSuffixByBaseForme(int baseForme, int formNum) {
        return formeSuffixesByBaseForme.getOrDefault(baseForme,dummyFormeSuffixes).getOrDefault(formNum,"");
    }

    private static final List<Integer> xyIrregularFormes = Arrays.asList(
            Species.Gen6Formes.castformF, Species.Gen6Formes.castformW, Species.Gen6Formes.castformI,
            Species.Gen6Formes.darmanitanZ,
            Species.Gen6Formes.meloettaP,
            Species.Gen6Formes.kyuremW,
            Species.Gen6Formes.kyuremB,
            Species.Gen6Formes.gengarMega,
            Species.Gen6Formes.gardevoirMega,
            Species.Gen6Formes.ampharosMega,
            Species.Gen6Formes.venusaurMega,
            Species.Gen6Formes.charizardMegaX, Species.Gen6Formes.charizardMegaY,
            Species.Gen6Formes.mewtwoMegaX, Species.Gen6Formes.mewtwoMegaY,
            Species.Gen6Formes.blazikenMega,
            Species.Gen6Formes.medichamMega,
            Species.Gen6Formes.houndoomMega,
            Species.Gen6Formes.aggronMega,
            Species.Gen6Formes.banetteMega,
            Species.Gen6Formes.tyranitarMega,
            Species.Gen6Formes.scizorMega,
            Species.Gen6Formes.pinsirMega,
            Species.Gen6Formes.aerodactylMega,
            Species.Gen6Formes.lucarioMega,
            Species.Gen6Formes.abomasnowMega,
            Species.Gen6Formes.aegislashB,
            Species.Gen6Formes.blastoiseMega,
            Species.Gen6Formes.kangaskhanMega,
            Species.Gen6Formes.gyaradosMega,
            Species.Gen6Formes.absolMega,
            Species.Gen6Formes.alakazamMega,
            Species.Gen6Formes.heracrossMega,
            Species.Gen6Formes.mawileMega,
            Species.Gen6Formes.manectricMega,
            Species.Gen6Formes.garchompMega,
            Species.Gen6Formes.latiosMega,
            Species.Gen6Formes.latiasMega
    );

    private static final List<Integer> orasIrregularFormes = Arrays.asList(
            Species.Gen6Formes.castformF, Species.Gen6Formes.castformW, Species.Gen6Formes.castformI,
            Species.Gen6Formes.darmanitanZ,
            Species.Gen6Formes.meloettaP,
            Species.Gen6Formes.kyuremW,
            Species.Gen6Formes.kyuremB,
            Species.Gen6Formes.gengarMega,
            Species.Gen6Formes.gardevoirMega,
            Species.Gen6Formes.ampharosMega,
            Species.Gen6Formes.venusaurMega,
            Species.Gen6Formes.charizardMegaX, Species.Gen6Formes.charizardMegaY,
            Species.Gen6Formes.mewtwoMegaX, Species.Gen6Formes.mewtwoMegaY,
            Species.Gen6Formes.blazikenMega,
            Species.Gen6Formes.medichamMega,
            Species.Gen6Formes.houndoomMega,
            Species.Gen6Formes.aggronMega,
            Species.Gen6Formes.banetteMega,
            Species.Gen6Formes.tyranitarMega,
            Species.Gen6Formes.scizorMega,
            Species.Gen6Formes.pinsirMega,
            Species.Gen6Formes.aerodactylMega,
            Species.Gen6Formes.lucarioMega,
            Species.Gen6Formes.abomasnowMega,
            Species.Gen6Formes.aegislashB,
            Species.Gen6Formes.blastoiseMega,
            Species.Gen6Formes.kangaskhanMega,
            Species.Gen6Formes.gyaradosMega,
            Species.Gen6Formes.absolMega,
            Species.Gen6Formes.alakazamMega,
            Species.Gen6Formes.heracrossMega,
            Species.Gen6Formes.mawileMega,
            Species.Gen6Formes.manectricMega,
            Species.Gen6Formes.garchompMega,
            Species.Gen6Formes.latiosMega,
            Species.Gen6Formes.latiasMega,
            Species.Gen6Formes.swampertMega,
            Species.Gen6Formes.sceptileMega,
            Species.Gen6Formes.sableyeMega,
            Species.Gen6Formes.altariaMega,
            Species.Gen6Formes.galladeMega,
            Species.Gen6Formes.audinoMega,
            Species.Gen6Formes.sharpedoMega,
            Species.Gen6Formes.slowbroMega,
            Species.Gen6Formes.steelixMega,
            Species.Gen6Formes.pidgeotMega,
            Species.Gen6Formes.glalieMega,
            Species.Gen6Formes.diancieMega,
            Species.Gen6Formes.metagrossMega,
            Species.Gen6Formes.kyogreP,
            Species.Gen6Formes.groudonP,
            Species.Gen6Formes.rayquazaMega,
            Species.Gen6Formes.cameruptMega,
            Species.Gen6Formes.lopunnyMega,
            Species.Gen6Formes.salamenceMega,
            Species.Gen6Formes.beedrillMega
    );

    private static final int moveCountXY = 617, moveCountORAS = 621;
    private static final int highestAbilityIndexXY = Abilities.auraBreak, highestAbilityIndexORAS = Abilities.deltaStream;

    public static final List<Integer> uselessAbilities = Arrays.asList(Abilities.forecast, Abilities.multitype,
            Abilities.flowerGift, Abilities.zenMode, Abilities.stanceChange);

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

    public static final int noDamageTargetTrappingEffect = 106, noDamageFieldTrappingEffect = 354,
            damageAdjacentFoesTrappingEffect = 373;

    public static final int noDamageStatusQuality = 1, noDamageStatChangeQuality = 2, damageStatusQuality = 4,
            noDamageStatusAndStatChangeQuality = 5, damageTargetDebuffQuality = 6, damageUserBuffQuality = 7,
            damageAbsorbQuality = 8;

    public static List<Integer> bannedMoves = Collections.singletonList(Moves.hyperspaceFury);

    public static final Type[] typeTable = constructTypeTable();

    // Copied from pk3DS. "Dark Grass Held Item" should probably be renamed
    public static final int bsHPOffset = 0, bsAttackOffset = 1, bsDefenseOffset = 2, bsSpeedOffset = 3,
            bsSpAtkOffset = 4, bsSpDefOffset = 5, bsPrimaryTypeOffset = 6, bsSecondaryTypeOffset = 7,
            bsCatchRateOffset = 8, bsCommonHeldItemOffset = 12, bsRareHeldItemOffset = 14, bsDarkGrassHeldItemOffset = 16,
            bsGenderOffset = 18, bsGrowthCurveOffset = 21, bsAbility1Offset = 24, bsAbility2Offset = 25,
            bsAbility3Offset = 26, bsFormeOffset = 28, bsFormeSpriteOffset = 30, bsFormeCountOffset = 32,
            bsTMHMCompatOffset = 40, bsSpecialMTCompatOffset = 56, bsMTCompatOffset = 64;

    private static final int bsSizeXY = 0x40;
    private static final int bsSizeORAS = 0x50;

    public static final int evolutionMethodCount = 34;

    public static final int staticPokemonSize = 0xC;
    private static final int staticPokemonCountXY = 0xD;
    private static final int staticPokemonCountORAS = 0x3B;

    private static final int giftPokemonSizeXY = 0x18;
    private static final int giftPokemonSizeORAS = 0x24;
    private static final int giftPokemonCountXY = 0x13;
    private static final int giftPokemonCountORAS = 0x25;

    public static final String tmDataPrefix = "D400AE02AF02B002";
    public static final int tmCount = 100, tmBlockOneCount = 92, tmBlockTwoCount = 3, tmBlockThreeCount = 5,
            tmBlockOneOffset = Items.tm01, tmBlockTwoOffset = Items.tm93, tmBlockThreeOffset = Items.tm96, hmBlockOneCount = 5,
            rockSmashOffsetORAS = 10, diveOffsetORAS = 28;
    private static final int tmBlockTwoStartingOffsetXY = 97, tmBlockTwoStartingOffsetORAS = 98,
            hmCountXY = 5, hmCountORAS = 7;
    public static final int hiddenItemCountORAS = 170;
    public static final String hiddenItemsPrefixORAS = "A100A200A300A400A5001400010053004A0084000900";
    public static final String itemPalettesPrefix = "6F7461746500FF920A063F";
    private static final String shopItemsLocatorXY = "0400110004000300", shopItemsLocatorORAS = "04001100120004000300";

    public static final int tutorMoveCount = 60;
    public static final String tutorsLocator = "C2015701A20012024401BA01";
    public static final String tutorsShopPrefix = "8A02000030000000";

    public static final int[] tutorSize = new int[]{15, 17, 16, 15};

    private static final String ingameTradesPrefixXY = "BA0A02015E000100BC0A150069000100";
    private static final String ingameTradesPrefixORAS = "810B7A0097000A00000047006B000A00";

    public static final int ingameTradeSize = 0x24;

    public static final String friendshipValueForEvoLocator = "DC0050E3BC00002A";

    public static final String perfectOddsBranchLocator = "050000BA000050E3";

    public static final String[] fastestTextPrefixes = new String[]{"1080BDE80000A0E31080BDE8F0412DE9", "485080E59C4040E24C50C0E5EC009FE5"};

    private static final List<Integer> mainGameShopsXY = Arrays.asList(
            10,11,12,13,16,17,20,21,24,25
    );

    private static final List<Integer> mainGameShopsORAS = Arrays.asList(
            10, 11, 13, 14, 16, 17, 18, 19, 20, 21
    );

    private static final List<String> shopNamesXY = Arrays.asList(
            "Primary 0 Badges",
            "Primary 1 Badges",
            "Primary 2 Badges",
            "Primary 3 Badges",
            "Primary 4 Badges",
            "Primary 5 Badges",
            "Primary 6 Badges",
            "Primary 7 Badges",
            "Primary 8 Badges",
            "Unused",
            "Lumiose Herboriste",
            "Lumiose Poké Ball Boutique",
            "Lumiose Stone Emporium",
            "Coumarine Incenses",
            "Aquacorde Poké Ball",
            "Aquacorde Potion",
            "Lumiose North Secondary",
            "Cyllage Secondary",
            "Shalour Secondary (TMs)",
            "Lumiose South Secondary (TMs)",
            "Laverre Secondary",
            "Snowbelle Secondary",
            "Kiloude Secondary (TMs)",
            "Anistar Secondary (TMs)",
            "Santalune Secondary",
            "Coumarine Secondary");

    private static final List<String> shopNamesORAS = Arrays.asList(
            "Primary 0 Badges (After Pokédex)",
            "Primary 1 Badges",
            "Primary 2 Badges",
            "Primary 3 Badges",
            "Primary 4 Badges",
            "Primary 5 Badges",
            "Primary 6 Badges",
            "Primary 7 Badges",
            "Primary 8 Badges",
            "Primary 0 Badges (Before Pokédex)",
            "Slateport Incenses",
            "Slateport Vitamins",
            "Slateport TMs",
            "Rustboro Secondary",
            "Slateport Secondary",
            "Mauville Secondary (TMs)",
            "Verdanturf Secondary",
            "Fallarbor Secondary",
            "Lavaridge Herbs",
            "Lilycove Dept. Store 2F Left",
            "Lilycove Dept. Store 3F Left",
            "Lilycove Dept. Store 3F Right",
            "Lilycove Dept. Store 4F Left (TMs)",
            "Lilycove Dept. Store 4F Right (TMs)");


    public static final List<Integer> evolutionItems = Arrays.asList(Items.sunStone, Items.moonStone, Items.fireStone,
            Items.thunderStone, Items.waterStone, Items.leafStone, Items.shinyStone, Items.duskStone, Items.dawnStone,
            Items.ovalStone, Items.kingsRock, Items.deepSeaTooth, Items.deepSeaScale, Items.metalCoat, Items.dragonScale,
            Items.upgrade, Items.protector, Items.electirizer, Items.magmarizer, Items.dubiousDisc, Items.reaperCloth,
            Items.razorClaw, Items.razorFang, Items.prismScale, Items.whippedDream, Items.sachet);

    private static final List<Integer> requiredFieldTMsXY = Arrays.asList(
            1, 9, 40, 19, 65, 73, 69, 74, 81, 57, 61, 97, 95, 71, 79, 30, 31, 36, 53, 29, 22, 3, 2, 80, 26);

    private static final List<Integer> requiredFieldTMsORAS = Arrays.asList(
            37, 32, 62, 11, 86, 29, 59, 43, 53, 69, 6, 2, 13, 18, 22, 61, 30, 97, 7, 90, 26, 55, 34, 35, 64, 65, 66,
            74, 79, 80, 81, 84, 89, 91, 93, 95);

    public static final List<Integer> fieldMovesXY = Arrays.asList(
            Moves.cut, Moves.fly, Moves.surf, Moves.strength, Moves.flash, Moves.dig, Moves.teleport,
            Moves.waterfall, Moves.sweetScent, Moves.rockSmash);
    public static final List<Integer> fieldMovesORAS = Arrays.asList(
            Moves.cut, Moves.fly, Moves.surf, Moves.strength, Moves.flash, Moves.dig, Moves.teleport,
            Moves.waterfall, Moves.sweetScent, Moves.rockSmash, Moves.secretPower, Moves.dive);

    public static final int fallingEncounterOffset = 0xF4270, fallingEncounterCount = 55, fieldEncounterSize = 0x3C,
                            rustlingBushEncounterOffset = 0xF40CC, rustlingBushEncounterCount = 7;
    public static final Map<Integer, String> fallingEncounterNameMap = constructFallingEncounterNameMap();
    public static final Map<Integer, String> rustlingBushEncounterNameMap = constructRustlingBushEncounterNameMap();
    public static final int perPokemonAreaDataLengthXY = 0xE8, perPokemonAreaDataLengthORAS = 0x2A0;

    private static final String saveLoadFormeReversionPrefixXY = "09EB000094E5141094E54A0B80E2", saveLoadFormeReversionPrefixORAS = "09EB000094E5141094E5120A80E2";
    public static final String afterBattleFormeReversionPrefix = "E4FFFFEA0000000000000000";
    public static final String ninjaskSpeciesPrefix = "241094E5B810D1E1", shedinjaSpeciesPrefix = "C2FFFFEB0040A0E10020A0E3";
    public static final String boxLegendaryFunctionPrefixXY = "14D08DE20900A0E1";
    public static final int boxLegendaryEncounterFileXY = 341, boxLegendaryLocalScriptOffsetXY = 0x6E0;
    public static final int[] boxLegendaryCodeOffsetsXY = new int[]{ 144, 300, 584 };
    public static final int seaSpiritsDenEncounterFileXY = 351, seaSpiritsDenLocalScriptOffsetXY = 0x1C0;
    public static final int[] seaSpiritsDenScriptOffsetsXY = new int[]{ 0x500, 0x508, 0x510 };
    public static final String rayquazaFunctionPrefixORAS = "0900A0E1F08FBDE8";
    public static final int[] rayquazaScriptOffsetsORAS = new int[]{ 3334, 14734 }, rayquazaCodeOffsetsORAS = new int[]{ 136, 292, 576 };
    public static final String nationalDexFunctionLocator = "080094E5010000E21080BDE8170F122F", xyGetDexFlagFunctionLocator = "000055E30100A0030A00000A",
            orasGetHoennDexCaughtFunctionPrefix = "170F122F1CC15800";
    public static final int megastoneTableStartingOffsetORAS = 0xABA, megastoneTableEntrySizeORAS = 0x20, megastoneTableLengthORAS = 27;

    public static final String pickupTableLocator = "110012001A00";
    public static final int numberOfPickupItems = 29;

    public static final String xyRoamerFreeSpacePostfix = "540095E50220A0E30810A0E1", xyRoamerSpeciesLocator = "9040A0030400000A",
            xyRoamerLevelPrefix = "B020DDE13F3BC1E3";

    public static final String xyTrashEncountersTablePrefix = "4028100000";
    public static final int xyTrashEncounterDataLength = 16, xyTrashCanEncounterCount = 24,
            pokemonVillageGarbadorOffset = 0, pokemonVillageGarbadorCount = 6, pokemonVillageBanetteOffset = 6,
            pokemonVillageBanetteCount = 6, lostHotelGarbadorOffset = 12, lostHotelGarbadorCount = 3,
            lostHotelTrubbishOffset = 15, lostHotelTrubbishCount = 3, lostHotelRotomOffset = 18, lostHotelRotomCount = 6;


    public static List<Integer> xyHardcodedTradeOffsets = Arrays.asList(1, 8);
    public static List<Integer> xyHardcodedTradeTexts = Arrays.asList(129, 349);

    public static final List<Integer> consumableHeldItems = setupAllConsumableItems();

    private static List<Integer> setupAllConsumableItems() {
        List<Integer> list = new ArrayList<>(Gen5Constants.consumableHeldItems);
        list.addAll(Arrays.asList(Items.weaknessPolicy, Items.luminousMoss, Items.snowball, Items.roseliBerry,
                Items.keeBerry, Items.marangaBerry, Items.fairyGem));
        return list;
    }

    public static final List<Integer> allHeldItems = setupAllHeldItems();

    private static List<Integer> setupAllHeldItems() {
        List<Integer> list = new ArrayList<>(Gen5Constants.allHeldItems);
        list.addAll(Arrays.asList(Items.weaknessPolicy, Items.snowball, Items.roseliBerry, Items.keeBerry,
                Items.marangaBerry, Items.fairyGem));
        list.addAll(Arrays.asList(Items.assaultVest, Items.pixiePlate, Items.safetyGoggles));
        return list;
    }

    public static final List<Integer> generalPurposeConsumableItems = initializeGeneralPurposeConsumableItems();

    private static List<Integer> initializeGeneralPurposeConsumableItems() {
        List<Integer> list = new ArrayList<>(Gen5Constants.generalPurposeConsumableItems);
        list.addAll(Arrays.asList(Items.weaknessPolicy, Items.luminousMoss, Items.snowball, Items.keeBerry, Items.marangaBerry));
        return Collections.unmodifiableList(list);
    }

    public static final List<Integer> generalPurposeItems = initializeGeneralPurposeItems();

    private static List<Integer> initializeGeneralPurposeItems() {
        List<Integer> list = new ArrayList<>(Gen5Constants.generalPurposeItems);
        list.addAll(Arrays.asList(Items.safetyGoggles));
        return Collections.unmodifiableList(list);
    }

    public static final Map<Type, Integer> weaknessReducingBerries = initializeWeaknessReducingBerries();

    private static Map<Type, Integer> initializeWeaknessReducingBerries() {
        Map<Type, Integer> map = new HashMap<>(Gen5Constants.weaknessReducingBerries);
        map.put(Type.FAIRY, Items.roseliBerry);
        return Collections.unmodifiableMap(map);
    }

    public static final Map<Type, Integer> consumableTypeBoostingItems = initializeConsumableTypeBoostingItems();

    private static Map<Type, Integer> initializeConsumableTypeBoostingItems() {
        Map<Type, Integer> map = new HashMap<>(Gen5Constants.consumableTypeBoostingItems);
        map.put(Type.FAIRY, Items.fairyGem);
        return Collections.unmodifiableMap(map);
    }

    public static final Map<Type, List<Integer>> typeBoostingItems = initializeTypeBoostingItems();

    private static Map<Type, List<Integer>> initializeTypeBoostingItems() {
        Map<Type, List<Integer>> map = new HashMap<>(Gen5Constants.typeBoostingItems);
        map.put(Type.FAIRY, Arrays.asList(Items.pixiePlate));
        return Collections.unmodifiableMap(map);
    }

    public static final Map<Integer, List<Integer>> moveBoostingItems = initializeMoveBoostingItems();

    private static Map<Integer, List<Integer>> initializeMoveBoostingItems() {
        Map<Integer, List<Integer>> map = new HashMap<>(Gen5Constants.moveBoostingItems);
        map.put(Moves.drainingKiss, Arrays.asList(Items.bigRoot));
        map.put(Moves.infestation, Arrays.asList(Items.gripClaw, Items.bindingBand));
        map.put(Moves.oblivionWing, Arrays.asList(Items.bigRoot));
        map.put(Moves.parabolicCharge, Arrays.asList(Items.bigRoot));
        return Collections.unmodifiableMap(map);
    }

    public static final Map<Integer, List<Integer>> abilityBoostingItems = initializeAbilityBoostingItems();

    private static Map<Integer, List<Integer>> initializeAbilityBoostingItems() {
        Map<Integer, List<Integer>> map = new HashMap<>(Gen5Constants.abilityBoostingItems);
        // Weather from abilities changed in Gen VI, so these items become relevant.
        map.put(Abilities.drizzle, Arrays.asList(Items.dampRock));
        map.put(Abilities.drought, Arrays.asList(Items.heatRock));
        map.put(Abilities.sandStream, Arrays.asList(Items.smoothRock));
        map.put(Abilities.snowWarning, Arrays.asList(Items.icyRock));
        return Collections.unmodifiableMap(map);
    }

    // No new species boosting items in Gen VI
    public static final Map<Integer, List<Integer>> speciesBoostingItems = Gen5Constants.speciesBoostingItems;

    public static String getIngameTradesPrefix(int romType) {
        if (romType == Type_XY) {
            return ingameTradesPrefixXY;
        } else {
            return ingameTradesPrefixORAS;
        }
    }

    public static List<Integer> getRequiredFieldTMs(int romType) {
        if (romType == Type_XY) {
            return requiredFieldTMsXY;
        } else {
            return requiredFieldTMsORAS;
        }
    }

    public static List<Integer> getMainGameShops(int romType) {
        if (romType == Type_XY) {
            return mainGameShopsXY;
        } else {
            return mainGameShopsORAS;
        }
    }

    public static List<String> getShopNames(int romType) {
        if (romType == Type_XY) {
            return shopNamesXY;
        } else {
            return shopNamesORAS;
        }
    }

    public static int getBsSize(int romType) {
        if (romType == Type_XY) {
            return bsSizeXY;
        } else {
            return bsSizeORAS;
        }
    }

    public static List<Integer> getIrregularFormes(int romType) {
        if (romType == Type_XY) {
            return xyIrregularFormes;
        } else if (romType == Type_ORAS) {
            return orasIrregularFormes;
        }
        return new ArrayList<>();
    }

    public static int getFormeCount(int romType) {
        if (romType == Type_XY) {
            return xyFormeCount;
        } else if (romType == Type_ORAS) {
            return orasFormeCount;
        }
        return 0;
    }

    public static int getFormeMovesetOffset(int romType) {
        if (romType == Type_XY) {
            return orasformeMovesetOffset;
        } else if (romType == Type_ORAS) {
            return orasformeMovesetOffset;
        }
        return 0;
    }

    public static int getMoveCount(int romType) {
        if (romType == Type_XY) {
            return moveCountXY;
        } else if (romType == Type_ORAS) {
            return moveCountORAS;
        }
        return moveCountXY;
    }

    public static int getTMBlockTwoStartingOffset(int romType) {
        if (romType == Type_XY) {
            return tmBlockTwoStartingOffsetXY;
        } else if (romType == Type_ORAS) {
            return tmBlockTwoStartingOffsetORAS;
        }
        return tmBlockTwoStartingOffsetXY;
    }

    public static int getHMCount(int romType) {
        if (romType == Type_XY) {
            return hmCountXY;
        } else if (romType == Type_ORAS) {
            return hmCountORAS;
        }
        return hmCountXY;
    }

    public static int getHighestAbilityIndex(int romType) {
        if (romType == Type_XY) {
            return highestAbilityIndexXY;
        } else if (romType == Type_ORAS) {
            return highestAbilityIndexORAS;
        }
        return highestAbilityIndexXY;
    }

    public static int getStaticPokemonCount(int romType) {
        if (romType == Type_XY) {
            return staticPokemonCountXY;
        } else if (romType == Type_ORAS) {
            return staticPokemonCountORAS;
        }
        return staticPokemonCountXY;
    }

    public static int getGiftPokemonCount(int romType) {
        if (romType == Type_XY) {
            return giftPokemonCountXY;
        } else if (romType == Type_ORAS) {
            return giftPokemonCountORAS;
        }
        return giftPokemonCountXY;
    }

    public static int getGiftPokemonSize(int romType) {
        if (romType == Type_XY) {
            return giftPokemonSizeXY;
        } else if (romType == Type_ORAS) {
            return giftPokemonSizeORAS;
        }
        return giftPokemonSizeXY;
    }

    public static String getShopItemsLocator(int romType) {
        if (romType == Type_XY) {
            return shopItemsLocatorXY;
        } else if (romType == Type_ORAS) {
            return shopItemsLocatorORAS;
        }
        return shopItemsLocatorXY;
    }

    public static boolean isMegaStone(int itemIndex) {
        // These values come from https://bulbapedia.bulbagarden.net/wiki/List_of_items_by_index_number_(Generation_VI)
        return (itemIndex >= Items.gengarite && itemIndex <= Items.latiosite) ||
                (itemIndex >= Items.swampertite && itemIndex <= Items.diancite) ||
                (itemIndex >= Items.cameruptite && itemIndex <= Items.beedrillite);
    }

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
        table[0x11] = Type.FAIRY;
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
            case FAIRY:
                return 0x11;
            default:
                return 0; // normal by default
        }
    }

    public static String getSaveLoadFormeReversionPrefix(int romType) {
        if (romType == Type_XY) {
            return saveLoadFormeReversionPrefixXY;
        } else {
            return saveLoadFormeReversionPrefixORAS;
        }
    }

    private static Map<Integer,String> setupFormeSuffixes() {
        Map<Integer,String> formeSuffixes = new HashMap<>();
        formeSuffixes.put(Species.Gen6Formes.deoxysA,"-A");
        formeSuffixes.put(Species.Gen6Formes.deoxysD,"-D");
        formeSuffixes.put(Species.Gen6Formes.deoxysS,"-S");
        formeSuffixes.put(Species.Gen6Formes.wormadamS,"-S");
        formeSuffixes.put(Species.Gen6Formes.wormadamT,"-T");
        formeSuffixes.put(Species.Gen6Formes.shayminS,"-S");
        formeSuffixes.put(Species.Gen6Formes.giratinaO,"-O");
        formeSuffixes.put(Species.Gen6Formes.rotomH,"-H");
        formeSuffixes.put(Species.Gen6Formes.rotomW,"-W");
        formeSuffixes.put(Species.Gen6Formes.rotomFr,"-Fr");
        formeSuffixes.put(Species.Gen6Formes.rotomFa,"-Fa");
        formeSuffixes.put(Species.Gen6Formes.rotomM,"-M");
        formeSuffixes.put(Species.Gen6Formes.castformF,"-F");
        formeSuffixes.put(Species.Gen6Formes.castformW,"-W");
        formeSuffixes.put(Species.Gen6Formes.castformI,"-I");
        formeSuffixes.put(Species.Gen6Formes.basculinB,"-B");
        formeSuffixes.put(Species.Gen6Formes.darmanitanZ,"-Z");
        formeSuffixes.put(Species.Gen6Formes.meloettaP,"-P");
        formeSuffixes.put(Species.Gen6Formes.kyuremW,"-W");
        formeSuffixes.put(Species.Gen6Formes.kyuremB,"-B");
        formeSuffixes.put(Species.Gen6Formes.keldeoCosmetic1,"-R");
        formeSuffixes.put(Species.Gen6Formes.tornadusT,"-T");
        formeSuffixes.put(Species.Gen6Formes.thundurusT,"-T");
        formeSuffixes.put(Species.Gen6Formes.landorusT,"-T");
        formeSuffixes.put(Species.Gen6Formes.gengarMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.meowsticF,"-F");
        // 749 - 757 Furfrou
        formeSuffixes.put(Species.Gen6Formes.gardevoirMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.ampharosMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.venusaurMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.charizardMegaX,"-Mega-X");
        formeSuffixes.put(Species.Gen6Formes.charizardMegaY,"-Mega-Y");
        formeSuffixes.put(Species.Gen6Formes.mewtwoMegaX,"-Mega-X");
        formeSuffixes.put(Species.Gen6Formes.mewtwoMegaY,"-Mega-Y");
        formeSuffixes.put(Species.Gen6Formes.blazikenMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.medichamMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.houndoomMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.aggronMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.banetteMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.tyranitarMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.scizorMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.pinsirMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.aerodactylMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.lucarioMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.abomasnowMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.aegislashB,"-B");
        formeSuffixes.put(Species.Gen6Formes.blastoiseMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.kangaskhanMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.gyaradosMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.absolMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.alakazamMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.heracrossMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.mawileMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.manectricMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.garchompMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.latiosMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.latiasMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.pumpkabooCosmetic1,"-M");
        formeSuffixes.put(Species.Gen6Formes.pumpkabooCosmetic2,"-L");
        formeSuffixes.put(Species.Gen6Formes.pumpkabooCosmetic3,"-XL");
        formeSuffixes.put(Species.Gen6Formes.gourgeistCosmetic1,"-M");
        formeSuffixes.put(Species.Gen6Formes.gourgeistCosmetic2,"-L");
        formeSuffixes.put(Species.Gen6Formes.gourgeistCosmetic3,"-XL");
        // 794 - 797 Floette
        formeSuffixes.put(Species.Gen6Formes.floetteE,"-E");
        formeSuffixes.put(Species.Gen6Formes.swampertMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.sceptileMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.sableyeMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.altariaMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.galladeMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.audinoMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.sharpedoMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.slowbroMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.steelixMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.pidgeotMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.glalieMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.diancieMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.metagrossMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.kyogreP,"-P");
        formeSuffixes.put(Species.Gen6Formes.groudonP,"-P");
        formeSuffixes.put(Species.Gen6Formes.rayquazaMega,"-Mega");
        // 815 - 820 contest Pikachu
        formeSuffixes.put(Species.Gen6Formes.hoopaU,"-U");
        formeSuffixes.put(Species.Gen6Formes.cameruptMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.lopunnyMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.salamenceMega,"-Mega");
        formeSuffixes.put(Species.Gen6Formes.beedrillMega,"-Mega");

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

        Map<Integer,String> tornadusMap = new HashMap<>();
        tornadusMap.put(1,"-T");
        map.put(Species.tornadus, tornadusMap);

        Map<Integer,String> thundurusMap = new HashMap<>();
        thundurusMap.put(1,"-T");
        map.put(Species.thundurus, thundurusMap);

        Map<Integer,String> landorusMap = new HashMap<>();
        landorusMap.put(1,"-T");
        map.put(Species.landorus, landorusMap);

        Map<Integer,String> meowsticMap = new HashMap<>();
        meowsticMap.put(1,"-F");
        map.put(Species.meowstic, meowsticMap);

        Map<Integer,String> aegislashMap = new HashMap<>();
        aegislashMap.put(1,"-B");
        map.put(Species.aegislash, aegislashMap);

        Map<Integer,String> pumpkabooMap = new HashMap<>();
        pumpkabooMap.put(1,"-M");
        pumpkabooMap.put(2,"-L");
        pumpkabooMap.put(3,"-XL");
        map.put(Species.pumpkaboo, pumpkabooMap);

        Map<Integer,String> gourgeistMap = new HashMap<>();
        gourgeistMap.put(1,"-M");
        gourgeistMap.put(2,"-L");
        gourgeistMap.put(3,"-XL");
        map.put(Species.gourgeist, gourgeistMap);

        Map<Integer,String> floetteMap = new HashMap<>();
        floetteMap.put(5,"-E");
        map.put(Species.floette, floetteMap);

        Map<Integer,String> kyogreMap = new HashMap<>();
        kyogreMap.put(1,"-P");
        map.put(Species.kyogre, kyogreMap);

        Map<Integer,String> groudonMap = new HashMap<>();
        groudonMap.put(1,"-P");
        map.put(Species.groudon, groudonMap);

        Map<Integer,String> rayquazaMap = new HashMap<>();
        rayquazaMap.put(1,"-Mega");
        map.put(Species.rayquaza, rayquazaMap);

        Map<Integer,String> hoopaMap = new HashMap<>();
        hoopaMap.put(1,"-U");
        map.put(Species.hoopa, hoopaMap);

        for (Integer species: speciesToMegaStoneORAS.keySet()) {
            Map<Integer,String> megaMap = new HashMap<>();
            if (species == Species.charizard || species == Species.mewtwo) {
                megaMap.put(1,"-Mega-X");
                megaMap.put(2,"-Mega-Y");
            } else {
                megaMap.put(1,"-Mega");
            }
            map.put(species,megaMap);
        }

        return map;
    }

    private static Map<Integer,String> setupDummyFormeSuffixes() {
        Map<Integer,String> m = new HashMap<>();
        m.put(0,"");
        return m;
    }

    public static ItemList allowedItemsXY, allowedItemsORAS, nonBadItemsXY, nonBadItemsORAS;
    public static List<Integer> regularShopItems, opShopItems;

    static {
        setupAllowedItems();
    }

    private static void setupAllowedItems() {
        allowedItemsXY = new ItemList(Items.megaGlove);
        // Key items + version exclusives
        allowedItemsXY.banRange(Items.explorerKit, 76);
        allowedItemsXY.banRange(Items.dataCard01, 32);
        allowedItemsXY.banRange(Items.xtransceiverMale, 18);
        allowedItemsXY.banSingles(Items.expShare, Items.libertyPass, Items.propCase, Items.dragonSkull,
                Items.lightStone, Items.darkStone);
        // Unknown blank items or version exclusives
        allowedItemsXY.banRange(Items.tea, 3);
        allowedItemsXY.banRange(Items.unused120, 14);
        // TMs & HMs - tms cant be held in gen6
        allowedItemsXY.tmRange(Items.tm01, 92);
        allowedItemsXY.tmRange(Items.tm93, 3);
        allowedItemsXY.banRange(Items.tm01, 100);
        allowedItemsXY.banRange(Items.tm93, 3);
        // Battle Launcher exclusives
        allowedItemsXY.banRange(Items.direHit2, 24);

        // Key items (Gen 6)
        allowedItemsXY.banRange(Items.holoCasterMale,3);
        allowedItemsXY.banSingles(Items.pokeFlute, Items.sprinklotad);
        allowedItemsXY.banRange(Items.powerPlantPass,4);
        allowedItemsXY.banRange(Items.elevatorKey,4);
        allowedItemsXY.banRange(Items.lensCase,3);
        allowedItemsXY.banRange(Items.lookerTicket,3);
        allowedItemsXY.banRange(Items.megaCharm,2);

        // TMs (Gen 6)
        allowedItemsXY.tmRange(Items.tm96,5);
        allowedItemsXY.banRange(Items.tm96,5);

        allowedItemsORAS = allowedItemsXY.copy(Items.eonFlute);
        // Key items and an HM
        allowedItemsORAS.banRange(Items.machBike,34);
        allowedItemsORAS.banRange(Items.prisonBottle,2);
        allowedItemsORAS.banRange(Items.meteoriteThirdForm,5);

        // non-bad items
        // ban specific pokemon hold items, berries, apricorns, mail
        nonBadItemsXY = allowedItemsXY.copy();

        nonBadItemsXY.banSingles(Items.oddKeystone, Items.griseousOrb, Items.soulDew, Items.lightBall,
                Items.oranBerry, Items.quickPowder, Items.passOrb, Items.discountCoupon, Items.strangeSouvenir);
        nonBadItemsXY.banRange(Items.growthMulch, 4); // mulch
        nonBadItemsXY.banRange(Items.adamantOrb, 2); // orbs
        nonBadItemsXY.banRange(Items.mail1, 12); // mails
        nonBadItemsXY.banRange(Items.figyBerry, 25); // berries without useful battle effects
        nonBadItemsXY.banRange(Items.luckyPunch, 4); // pokemon specific
        nonBadItemsXY.banRange(Items.redScarf, 5); // contest scarves
        nonBadItemsXY.banRange(Items.relicCopper,7); // relic items
        nonBadItemsXY.banRange(Items.richMulch,4); // more mulch
        nonBadItemsXY.banRange(Items.shoalSalt, 6); // Shoal items and Shards; they serve no purpose in XY

        nonBadItemsORAS = allowedItemsORAS.copy();

        nonBadItemsORAS.banSingles(Items.oddKeystone, Items.griseousOrb, Items.soulDew, Items.lightBall,
                Items.oranBerry, Items.quickPowder, Items.passOrb, Items.discountCoupon, Items.strangeSouvenir);
        nonBadItemsORAS.banRange(Items.growthMulch, 4); // mulch
        nonBadItemsORAS.banRange(Items.adamantOrb, 2); // orbs
        nonBadItemsORAS.banRange(Items.mail1, 12); // mails
        nonBadItemsORAS.banRange(Items.figyBerry, 25); // berries without useful battle effects
        nonBadItemsORAS.banRange(Items.luckyPunch, 4); // pokemon specific
        nonBadItemsORAS.banRange(Items.redScarf, 5); // contest scarves
        nonBadItemsORAS.banRange(Items.relicCopper,7); // relic items
        nonBadItemsORAS.banRange(Items.richMulch,4); // more mulch

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

    public static ItemList getAllowedItems(int romType) {
        if (romType == Type_XY) {
            return allowedItemsXY;
        } else {
            return allowedItemsORAS;
        }
    }

    public static ItemList getNonBadItems(int romType) {
        if (romType == Type_XY) {
            return nonBadItemsXY;
        } else {
            return nonBadItemsORAS;
        }
    }

    public static final List<Integer> uniqueNoSellItems = Arrays.asList(Items.gengarite, Items.gardevoirite,
            Items.ampharosite, Items.venusaurite, Items.charizarditeX, Items.blastoisinite, Items.mewtwoniteX,
            Items.mewtwoniteY, Items.blazikenite, Items.medichamite, Items.houndoominite, Items.aggronite,
            Items.banettite, Items.tyranitarite, Items.scizorite, Items.pinsirite, Items.aerodactylite,
            Items.lucarionite, Items.abomasite, Items.kangaskhanite, Items.gyaradosite, Items.absolite,
            Items.charizarditeY, Items.alakazite, Items.heracronite, Items.mawilite, Items.manectite, Items.garchompite,
            Items.latiasite, Items.latiosite, Items.swampertite, Items.sceptilite, Items.sablenite, Items.altarianite,
            Items.galladite, Items.audinite, Items.metagrossite, Items.sharpedonite, Items.slowbronite,
            Items.steelixite, Items.pidgeotite, Items.glalitite, Items.diancite, Items.cameruptite, Items.lopunnite,
            Items.salamencite, Items.beedrillite);

    private static Map<Integer,List<Integer>> setupSpeciesToMegaStone(int romType) {
        Map<Integer,List<Integer>> map = new TreeMap<>();

        map.put(Species.venusaur, Collections.singletonList(Items.venusaurite));
        map.put(Species.charizard, Arrays.asList(Items.charizarditeX, Items.charizarditeY));
        map.put(Species.blastoise, Collections.singletonList(Items.blastoisinite));
        map.put(Species.alakazam, Collections.singletonList(Items.alakazite));
        map.put(Species.gengar, Collections.singletonList(Items.gengarite));
        map.put(Species.kangaskhan, Collections.singletonList(Items.kangaskhanite));
        map.put(Species.pinsir, Collections.singletonList(Items.pinsirite));
        map.put(Species.gyarados, Collections.singletonList(Items.gyaradosite));
        map.put(Species.aerodactyl, Collections.singletonList(Items.aerodactylite));
        map.put(Species.mewtwo, Arrays.asList(Items.mewtwoniteX, Items.mewtwoniteY));
        map.put(Species.ampharos, Collections.singletonList(Items.ampharosite));
        map.put(Species.scizor, Collections.singletonList(Items.scizorite));
        map.put(Species.heracross, Collections.singletonList(Items.heracronite));
        map.put(Species.houndoom, Collections.singletonList(Items.houndoominite));
        map.put(Species.tyranitar, Collections.singletonList(Items.tyranitarite));
        map.put(Species.blaziken, Collections.singletonList(Items.blazikenite));
        map.put(Species.gardevoir, Collections.singletonList(Items.gardevoirite));
        map.put(Species.mawile, Collections.singletonList(Items.mawilite));
        map.put(Species.aggron, Collections.singletonList(Items.aggronite));
        map.put(Species.medicham, Collections.singletonList(Items.medichamite));
        map.put(Species.manectric, Collections.singletonList(Items.manectite));
        map.put(Species.banette, Collections.singletonList(Items.banettite));
        map.put(Species.absol, Collections.singletonList(Items.absolite));
        map.put(Species.latias, Collections.singletonList(Items.latiasite));
        map.put(Species.latios, Collections.singletonList(Items.latiosite));
        map.put(Species.garchomp, Collections.singletonList(Items.garchompite));
        map.put(Species.lucario, Collections.singletonList(Items.lucarionite));
        map.put(Species.abomasnow, Collections.singletonList(Items.abomasite));

        if (romType == Type_ORAS) {
            map.put(Species.beedrill, Collections.singletonList(Items.beedrillite));
            map.put(Species.pidgeot, Collections.singletonList(Items.pidgeotite));
            map.put(Species.slowbro, Collections.singletonList(Items.slowbronite));
            map.put(Species.steelix, Collections.singletonList(Items.steelixite));
            map.put(Species.sceptile, Collections.singletonList(Items.sceptilite));
            map.put(Species.swampert, Collections.singletonList(Items.swampertite));
            map.put(Species.sableye, Collections.singletonList(Items.sablenite));
            map.put(Species.sharpedo, Collections.singletonList(Items.sharpedonite));
            map.put(Species.camerupt, Collections.singletonList(Items.cameruptite));
            map.put(Species.altaria, Collections.singletonList(Items.altarianite));
            map.put(Species.glalie, Collections.singletonList(Items.glalitite));
            map.put(Species.salamence, Collections.singletonList(Items.salamencite));
            map.put(Species.metagross, Collections.singletonList(Items.metagrossite));
            map.put(Species.lopunny, Collections.singletonList(Items.lopunnite));
            map.put(Species.gallade, Collections.singletonList(Items.galladite));
            map.put(Species.audino, Collections.singletonList(Items.audinite));
            map.put(Species.diancie, Collections.singletonList(Items.diancite));
        }

        return map;
    }

    public static void tagTrainersXY(List<Trainer> trs) {

        // Gym Trainers
        tag(trs,"GYM1", 39, 40, 48);
        tag(trs,"GYM2",64, 63, 106, 105);
        tag(trs,"GYM3",83, 84, 146, 147);
        tag(trs,"GYM4", 121, 122, 123, 124);
        tag(trs,"GYM5", 461, 462, 463, 464, 465, 466, 467, 468, 469, 28, 29, 30);
        tag(trs,"GYM6", 245, 250, 248, 243);
        tag(trs,"GYM7", 170, 171, 172, 365, 366);
        tag(trs,"GYM8", 168, 169, 31, 32);

        // Gym Leaders
        tag(trs,"GYM1-LEADER", 6);
        tag(trs,"GYM2-LEADER",76);
        tag(trs,"GYM3-LEADER",21);
        tag(trs,"GYM4-LEADER", 22);
        tag(trs,"GYM5-LEADER", 23);
        tag(trs,"GYM6-LEADER", 24);
        tag(trs,"GYM7-LEADER", 25);
        tag(trs,"GYM8-LEADER", 26);

        tag(trs, 188, "NOTSTRONG"); // Successor Korrina

        // Elite 4
        tag(trs, 269, "ELITE1"); // Malva
        tag(trs, 271, "ELITE2"); // Siebold
        tag(trs, 187, "ELITE3"); // Wikstrom
        tag(trs, 270, "ELITE4"); // Drasna
        tag(trs, 276, "CHAMPION"); // Diantha

        tag(trs,"THEMED:LYSANDRE-LEADER", 303, 525, 526);
        tag(trs,"STRONG", 174, 175, 304, 344, 345, 346, 347, 348, 349, 350, 351, 470, 471, 472, 473, 474, 475, 476, 477, 478, 479); // Team Flare Admins lol
        tag(trs,"STRONG", 324, 325, 438, 439, 573); // Tierno and Trevor
        tag(trs,"STRONG", 327, 328); // Sycamore

        // Rival - Serena
        tagRival(trs, "RIVAL1", 596);
        tagRival(trs, "RIVAL2", 575);
        tagRival(trs, "RIVAL3", 581);
        tagRival(trs, "RIVAL4", 578);
        tagRival(trs, "RIVAL5", 584);
        tagRival(trs, "RIVAL6", 607);
        tagRival(trs, "RIVAL7", 587);
        tagRival(trs, "RIVAL8", 590);
        tagRival(trs, "RIVAL9", 593);
        tagRival(trs, "RIVAL10", 599);

        // Rival - Calem
        tagRival(trs, "RIVAL1", 435);
        tagRival(trs, "RIVAL2", 130);
        tagRival(trs, "RIVAL3", 329);
        tagRival(trs, "RIVAL4", 184);
        tagRival(trs, "RIVAL5", 332);
        tagRival(trs, "RIVAL6", 604);
        tagRival(trs, "RIVAL7", 335);
        tagRival(trs, "RIVAL8", 338);
        tagRival(trs, "RIVAL9", 341);
        tagRival(trs, "RIVAL10", 519);

        // Rival - Shauna
        tagRival(trs, "FRIEND1", 137);
        tagRival(trs, "FRIEND2", 321);
    }

    public static void tagTrainersORAS(List<Trainer> trs) {

        // Gym Trainers & Leaders
        tag(trs,"GYM1",562, 22, 667);
        tag(trs,"GYM2",60, 56, 59);
        tag(trs,"GYM3",34, 568, 614, 35);
        tag(trs,"GYM4",81, 824, 83, 615, 823, 613, 85);
        tag(trs,"GYM5",63, 64, 65, 66, 67, 68, 69);
        tag(trs,"GYM6",115, 517, 516, 118, 730);
        tag(trs,"GYM7",157, 158, 159, 226, 320, 225);
        tag(trs,"GYM8",647, 342, 594, 646, 338, 339, 340, 341); // Includes Wallace in Delta Episode

        // Gym Leaders
        tag(trs,"GYM1-LEADER", 561);
        tag(trs,"GYM2-LEADER",563);
        tag(trs,"GYM3-LEADER",567);
        tag(trs,"GYM4-LEADER", 569);
        tag(trs,"GYM5-LEADER", 570);
        tag(trs,"GYM6-LEADER", 571);
        tag(trs,"GYM7-LEADER", 552);
        tag(trs,"GYM8-LEADER", 572, 943);

        // Elite 4
        tag(trs, "ELITE1", 553, 909); // Sidney
        tag(trs, "ELITE2", 554, 910); // Phoebe
        tag(trs, "ELITE3", 555, 911); // Glacia
        tag(trs, "ELITE4", 556, 912); // Drake
        tag(trs, "CHAMPION", 557, 913, 680, 942); // Steven (includes other appearances)

        tag(trs,"THEMED:MAXIE-LEADER", 235, 236, 271);
        tag(trs,"THEMED:ARCHIE-LEADER",178, 231, 266);
        tag(trs,"THEMED:MATT-STRONG",683, 684, 685, 686, 687);
        tag(trs,"THEMED:SHELLY-STRONG",688,689,690);
        tag(trs,"THEMED:TABITHA-STRONG",691,692,693);
        tag(trs,"THEMED:COURTNEY-STRONG",694,695,696,697,698);
        tag(trs, "THEMED:WALLY-STRONG", 518, 583, 944, 946);

        // Rival - Brendan
        tagRival(trs, "RIVAL1", 1);
        tagRival(trs, "RIVAL2", 289);
        tagRival(trs, "RIVAL3", 674);
        tagRival(trs, "RIVAL4", 292);
        tagRival(trs, "RIVAL5", 527);
        tagRival(trs, "RIVAL6", 699);

        // Rival - May
        tagRival(trs, "RIVAL1", 4);
        tagRival(trs, "RIVAL2", 295);
        tagRival(trs, "RIVAL3", 677);
        tagRival(trs, "RIVAL4", 298);
        tagRival(trs, "RIVAL5", 530);
        tagRival(trs, "RIVAL6", 906);
    }

    private static void tagRival(List<Trainer> allTrainers, String tag, int offset) {
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

    public static final HashMap<String, Type> gymAndEliteThemesXY = setupGymAndEliteThemesXY();

    private static HashMap<String, Type> setupGymAndEliteThemesXY() {
        HashMap<String, Type> themeMap = new HashMap<>();
        //Diantha has no theme
        themeMap.put("ELITE1", Type.FIRE); //Malva
        themeMap.put("ELITE2", Type.WATER); //Siebold
        themeMap.put("ELITE3", Type.STEEL); //Wikstrom
        themeMap.put("ELITE4", Type.DRAGON); //Drasna
        themeMap.put("GYM1", Type.BUG); //Viola
        themeMap.put("GYM2", Type.ROCK); //Grant
        themeMap.put("GYM3", Type.FIGHTING); //Korrina
        themeMap.put("GYM4", Type.GRASS); //Ramos
        themeMap.put("GYM5", Type.ELECTRIC); //Clemont
        themeMap.put("GYM6", Type.FAIRY); //Valerie
        themeMap.put("GYM7", Type.PSYCHIC); //Olympia
        themeMap.put("GYM8", Type.ICE); //Wulfric
        return themeMap;

    }

    public static final HashMap<String, Type> gymAndEliteThemesORAS = setupGymAndEliteThemesORAS();

    private static HashMap<String, Type> setupGymAndEliteThemesORAS() {
        HashMap<String, Type> themeMap = new HashMap<>();
        themeMap.put("CHAMPION", Type.STEEL); //Steven
        themeMap.put("ELITE1", Type.DARK); //Sidney
        themeMap.put("ELITE2", Type.GHOST); //Phoebe
        themeMap.put("ELITE3", Type.ICE); //Glacia
        themeMap.put("ELITE4", Type.DRAGON); //Drake
        themeMap.put("GYM1", Type.ROCK); //Roxanne
        themeMap.put("GYM2", Type.FIGHTING); //Brawly
        themeMap.put("GYM3", Type.ELECTRIC); //Wattson
        themeMap.put("GYM4", Type.FIRE); //Flannery
        themeMap.put("GYM5", Type.NORMAL); //Norman
        themeMap.put("GYM6", Type.FLYING); //Winona
        themeMap.put("GYM7", Type.PSYCHIC); //Tate & Liza
        themeMap.put("GYM8", Type.WATER); //Wallace
        return themeMap;
    }

    public static void setMultiBattleStatusXY(List<Trainer> trs) {
        // 108 + 111: Team Flare Grunts in Glittering Cave
        // 348 + 350: Team Flare Celosia and Bryony fight in Poké Ball Factory
        // 438 + 439: Tierno and Trevor fight on Route 7
        // 470 + 611, 472 + 610, 476 + 612: Team Flare Admin and Grunt fights in Team Flare Secret HQ
        setMultiBattleStatus(trs, 108, 111, 348, 350, 438, 439, 470, 472, 476, 610, 611, 612);
    }

    public static void setMultiBattleStatusORAS(List<Trainer> trs) {
        // 683 + 904: Aqua Admin Matt and Team Aqua Grunt fight on the Southern Island
        // 687 + 905: Aqua Admin Matt and Team Aqua Grunt fight at the Mossdeep Space Center
        // 688 + 903: Aqua Admin Shelly and Team Aqua Grunt fight in Meteor Falls
        // 691 + 902: Magma Admin Tabitha and Team Magma Grunt fight in Meteor Falls
        // 694 + 900: Magma Admin Courtney and Team Magma Grunt fight on the Southern Island
        // 698 + 901: Magma Admin Courtney and Team Magma Grunt fight at the Mossdeep Space Center
        setMultiBattleStatus(trs, 683, 687, 688, 691, 694, 698, 900, 901, 902, 903, 904, 905);
    }

    private static void setMultiBattleStatus(List<Trainer> allTrainers, int... numbers) {
        for (int num : numbers) {
            if (allTrainers.size() > (num - 1)) {
                allTrainers.get(num - 1).multiBattleStatus = Trainer.MultiBattleStatus.ALWAYS;
            }
        }
    }

    private static Map<Integer, String> constructFallingEncounterNameMap() {
        Map<Integer, String> map = new TreeMap<>();
        map.put(0, "Glittering Cave Ceiling Encounter");
        map.put(4, "Reflection Cave Ceiling Encounter");
        map.put(20, "Victory Road Outside 2 Sky Encounter");
        map.put(24, "Victory Road Inside 2 Encounter");
        map.put(28, "Victory Road Outside 3 Sky Encounter");
        map.put(32, "Victory Road Inside 3 Ceiling Encounter");
        map.put(36, "Victory Road Outside 4 Sky Encounter");
        map.put(46, "Terminus Cave Ceiling Encounter");
        return map;
    }

    private static Map<Integer, String> constructRustlingBushEncounterNameMap() {
        Map<Integer, String> map = new TreeMap<>();
        map.put(0, "Route 6 Rustling Bush Encounter");
        map.put(3, "Route 18 Rustling Bush Encounter");
        return map;
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
            {Items.tea, 0}, // unused in Gen 6
            {Items.unused114, 0},
            {Items.autograph, 0},
            {Items.douseDrive, 100},
            {Items.shockDrive, 100},
            {Items.burnDrive, 100},
            {Items.chillDrive, 100},
            {Items.unused120, 0},
            {Items.pokemonBox, 0}, // unused in Gen 6
            {Items.medicinePocket, 0}, // unused in Gen 6
            {Items.tmCase, 0}, // unused in Gen 6
            {Items.candyJar, 0}, // unused in Gen 6
            {Items.powerUpPocket, 0}, // unused in Gen 6
            {Items.clothingTrunk, 0}, // unused in Gen 6
            {Items.catchingPocket, 0}, // unused in Gen 6
            {Items.battlePocket, 0}, // unused in Gen 6
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
            {Items.expShare, 0},
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
            {Items.hm07, 0}, // unused in Gen 6
            {Items.hm08, 0}, // unused in Gen 6
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
            {Items.rageCandyBar, 15},
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
            {Items.balmMushroom, 1250},
            {Items.bigNugget, 2000},
            {Items.pearlString, 1500},
            {Items.cometShard, 3000},
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
            {Items.revealGlass, 0},
            {Items.weaknessPolicy, 200},
            {Items.assaultVest, 600},
            {Items.holoCasterMale, 0},
            {Items.profsLetter, 0},
            {Items.rollerSkates, 0},
            {Items.pixiePlate, 200},
            {Items.abilityCapsule, 500},
            {Items.whippedDream, 300},
            {Items.sachet, 300},
            {Items.luminousMoss, 20},
            {Items.snowball, 20},
            {Items.safetyGoggles, 300},
            {Items.pokeFlute, 0},
            {Items.richMulch, 20},
            {Items.surpriseMulch, 20},
            {Items.boostMulch, 20},
            {Items.amazeMulch, 20},
            {Items.gengarite, 1000},
            {Items.gardevoirite, 1000},
            {Items.ampharosite, 1000},
            {Items.venusaurite, 1000},
            {Items.charizarditeX, 1000},
            {Items.blastoisinite, 1000},
            {Items.mewtwoniteX, 2000},
            {Items.mewtwoniteY, 2000},
            {Items.blazikenite, 1000},
            {Items.medichamite, 500},
            {Items.houndoominite, 1000},
            {Items.aggronite, 1000},
            {Items.banettite, 500},
            {Items.tyranitarite, 2000},
            {Items.scizorite, 1000},
            {Items.pinsirite, 1000},
            {Items.aerodactylite, 1000},
            {Items.lucarionite, 1000},
            {Items.abomasite, 500},
            {Items.kangaskhanite, 500},
            {Items.gyaradosite, 1000},
            {Items.absolite, 500},
            {Items.charizarditeY, 1000},
            {Items.alakazite, 1000},
            {Items.heracronite, 1000},
            {Items.mawilite, 300},
            {Items.manectite, 500},
            {Items.garchompite, 2000},
            {Items.latiasite, 2000},
            {Items.latiosite, 2000},
            {Items.roseliBerry, 100},
            {Items.keeBerry, 100},
            {Items.marangaBerry, 100},
            {Items.sprinklotad, 0},
            {Items.tm96, 1000},
            {Items.tm97, 1000},
            {Items.tm98, 1000},
            {Items.tm99, 1000},
            {Items.tm100, 500},
            {Items.powerPlantPass, 0},
            {Items.megaRing, 0},
            {Items.intriguingStone, 0},
            {Items.commonStone, 0},
            {Items.discountCoupon, 2},
            {Items.elevatorKey, 0},
            {Items.tmvPass, 0},
            {Items.honorofKalos, 0},
            {Items.adventureGuide, 0},
            {Items.strangeSouvenir, 1},
            {Items.lensCase, 0},
            {Items.makeupBag, 0},
            {Items.travelTrunk, 0},
            {Items.lumioseGalette, 45},
            {Items.shalourSable, 45},
            {Items.jawFossil, 500},
            {Items.sailFossil, 500},
            {Items.lookerTicket, 0},
            {Items.bikeYellow, 0},
            {Items.holoCasterFemale, 0},
            {Items.fairyGem, 100},
            {Items.megaCharm, 0},
            {Items.megaGlove, 0},
            {Items.machBike, 0},
            {Items.acroBike, 0},
            {Items.wailmerPail, 0},
            {Items.devonParts, 0},
            {Items.sootSack, 0},
            {Items.basementKeyHoenn, 0},
            {Items.pokeblockKit, 0},
            {Items.letter, 0},
            {Items.eonTicket, 0},
            {Items.scanner, 0},
            {Items.goGoggles, 0},
            {Items.meteoriteFirstForm, 0},
            {Items.keytoRoom1, 0},
            {Items.keytoRoom2, 0},
            {Items.keytoRoom4, 0},
            {Items.keytoRoom6, 0},
            {Items.storageKeyHoenn, 0},
            {Items.devonScope, 0},
            {Items.ssTicketHoenn, 0},
            {Items.hm07ORAS, 0},
            {Items.devonScubaGear, 0},
            {Items.contestCostumeMale, 0},
            {Items.contestCostumeFemale, 0},
            {Items.magmaSuit, 0},
            {Items.aquaSuit, 0},
            {Items.pairOfTickets, 0},
            {Items.megaBracelet, 0},
            {Items.megaPendant, 0},
            {Items.megaGlasses, 0},
            {Items.megaAnchor, 0},
            {Items.megaStickpin, 0},
            {Items.megaTiara, 0},
            {Items.megaAnklet, 0},
            {Items.meteoriteSecondForm, 0},
            {Items.swampertite, 1000},
            {Items.sceptilite, 1000},
            {Items.sablenite, 300},
            {Items.altarianite, 500},
            {Items.galladite, 1000},
            {Items.audinite, 500},
            {Items.metagrossite, 2000},
            {Items.sharpedonite, 500},
            {Items.slowbronite, 500},
            {Items.steelixite, 1000},
            {Items.pidgeotite, 500},
            {Items.glalitite, 500},
            {Items.diancite, 2000},
            {Items.prisonBottle, 0},
            {Items.megaCuff, 0},
            {Items.cameruptite, 500},
            {Items.lopunnite, 500},
            {Items.salamencite, 2000},
            {Items.beedrillite, 300},
            {Items.meteoriteThirdForm, 0},
            {Items.meteoriteFinalForm, 0},
            {Items.keyStone, 0},
            {Items.meteoriteShard, 0},
            {Items.eonFlute, 0},
    }).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

    public static final int[] xyMapNumToPokedexIndex = {
            7,  // Couriway Town
            8,  // Ambrette Town
            13, // Cyllage City
            14, // Shalour City
            16, // Laverre City
            22, // Route 2
            23, // Route 3
            24, // Route 4
            25, // Route 5
            26, // Route 6
            27, // Route 7
            28, // Route 8
            29, // Route 9
            30, // Route 10
            31, // Route 11
            32, // Route 12
            33, // Route 13
            34, // Route 14
            35, // Route 15
            36, // Route 16
            37, // Route 17
            38, // Route 18
            39, // Route 19
            40, // Route 20
            41, // Route 21
            42, // Route 22
            44, // Santalune Forest
            45, // Parfum Palace
            46, 46, // Glittering Cave
            47, 47, 47, 47, // Reflection Cave
            49, 49, 49, 49, 49, // Frost Cavern
            50, // Pokemon Village
            51, 51, 51, 51, 51, // Victory Road
            52, // Connecting Cave
            54, 54, 54, 54, 54, // Terminus Cave
            55, // Lost Hotel
            43, // Azure Bay
            46, 46, 46, 46, // Glittering Cave (ceiling)
            47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, // Reflection Cave (ceiling)
            51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, // Victory Road (ceiling and sky)
            54, 54, 54, 54, 54, 54, 54, 54, 54, // Terminus Cave (ceiling)
            26, 26, 26, // Route 6 (rustling bush)
            38, 38, 38, 38 // Route 18 (rustling bush)
    };

    public static final int[] orasMapNumToPokedexIndex = {
            2,  // Dewford Town
            6,  // Pacifidlog Town
            7,  // Petalburg City
            8,  // Slateport City
            12, // Lilycove City
            13, // Mossdeep City
            14, // Sootopolis City
            15, // Ever Grande City
            17, // Route 101
            18, // Route 102
            19, // Route 103
            20, // Route 104 (North Section)
            21, // Route 104 (South Section)
            22, // Route 105
            23, // Route 106
            24, // Route 107
            26, // Route 108
            27, // Route 109
            28, // Route 110
            30, // Route 111 (Desert)
            32, // Route 111 (South Section)
            33, // Route 112 (North Section)
            34, // Route 112 (South Section)
            35, // Route 113
            36, // Route 114
            37, // Route 115
            38, // Route 116
            39, // Route 117
            40, // Route 118
            41, 41, // Route 119
            43, 43, // Route 120
            45, // Route 121
            46, // Route 122
            47, // Route 123
            48, // Route 124
            50, // Route 125
            51, // Route 126
            53, // Route 127
            55, // Route 128
            57, // Route 129
            59, // Route 130
            61, // Route 131
            62, // Route 132
            63, // Route 133
            64, // Route 134
            25, // Route 107 (Underwater)
            49, // Route 124 (Underwater)
            52, // Route 126 (Underwater)
            56, // Route 128 (Underwater)
            58, // Route 129 (Underwater)
            60, // Route 130 (Underwater)
            69, 69, 69, 69, // Meteor Falls
            73, // Rusturf Tunnel
            74, 74, 74, // Granite Cave
            78, // Petalburg Woods
            80, // Jagged Pass
            81, // Fiery Path
            82, 82, 82, 82, 82, 82, // Mt. Pyre
            -1, // Team Aqua Hideout
            88, 88, 88, 88, 88, 88, 88, 88, 88, 88, 88, // Seafloor Cavern
            102, 102, 102, 102, 102, // Cave of Origin
            114, 114, 114, 114, // Victory Road
            119, 119, 119, 119, 119, 119, 119, // Shoal Cave
            130, // New Mauville
            136, 136, 136, 136, // Sea Mauville
            -1, // Sealed Chamber
            -1, -1, -1, -1, // Scorched Slab
            -1, // Team Magma Hideout
            150, // Sky Pillar
            -1, -1, -1, -1, -1, -1, -1, -1, // Mirage Forest
            -1, -1, -1, -1, -1, -1, -1, -1, // Mirage Island
            -1, // Mirage Mountain
            159, // Battle Resort
            65, 65, 65, 65, // Safari Zone
            102, // Cave of Origin
            -1, -1, -1, -1, -1, -1, -1, // Mirage Mountain
            -1, -1, -1, -1, -1, -1, -1, -1, // Mirage Cave
            -1, // Mt. Pyre (unused)
            -1  // Sootopolis City (unused)
    };
}
