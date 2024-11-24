package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  Gen7Constants.java - Constants for Sun/Moon/Ultra Sun/Ultra Moon      --*/
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

import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.Type;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Gen7Constants {

    public static final int Type_SM = N3DSConstants.Type_SM;
    public static final int Type_USUM = N3DSConstants.Type_USUM;

    private static final int pokemonCountSM = 802, pokemonCountUSUM = 807;
    private static final int formeCountSM = 158, formeCountUSUM = 168;
    private static final int moveCountSM = 719, moveCountUSUM = 728;
    private static final int highestAbilityIndexSM = Abilities.prismArmor, highestAbilityIndexUSUM = Abilities.neuroforce;

    public static final int bsHPOffset = 0, bsAttackOffset = 1, bsDefenseOffset = 2, bsSpeedOffset = 3,
            bsSpAtkOffset = 4, bsSpDefOffset = 5, bsPrimaryTypeOffset = 6, bsSecondaryTypeOffset = 7,
            bsCatchRateOffset = 8, bsCommonHeldItemOffset = 12, bsRareHeldItemOffset = 14, bsDarkGrassHeldItemOffset = 16,
            bsGenderOffset = 18, bsGrowthCurveOffset = 21, bsAbility1Offset = 24, bsAbility2Offset = 25,
            bsAbility3Offset = 26, bsCallRateOffset = 27, bsFormeOffset = 28, bsFormeSpriteOffset = 30,
            bsFormeCountOffset = 32, bsTMHMCompatOffset = 40, bsSpecialMTCompatOffset = 56, bsMTCompatOffset = 60;

    public static final int bsSize = 0x54;

    public static final int evolutionMethodCount = 42;

    private static List<Integer> speciesWithAlolanForms = Arrays.asList(
            Species.rattata, Species.raticate, Species.raichu, Species.sandshrew, Species.sandslash, Species.vulpix,
            Species.ninetales, Species.diglett, Species.dugtrio, Species.meowth, Species.persian, Species.geodude,
            Species.graveler, Species.golem, Species.grimer, Species.muk, Species.exeggutor, Species.marowak
    );

    private static final Map<Integer,String> dummyFormeSuffixes = setupDummyFormeSuffixes();
    private static final Map<Integer,Map<Integer,String>> formeSuffixesByBaseForme = setupFormeSuffixesByBaseForme();

    public static String getFormeSuffixByBaseForme(int baseForme, int formNum) {
        return formeSuffixesByBaseForme.getOrDefault(baseForme,dummyFormeSuffixes).getOrDefault(formNum,"");
    }

    public static List<Integer> getIrregularFormes(int romType) {
        if (romType == Type_SM) {
            return irregularFormesSM;
        } else if (romType == Type_USUM) {
            return irregularFormesUSUM;
        }
        return irregularFormesSM;
    }

    public static final List<Integer> irregularFormesSM = Arrays.asList(
            Species.SMFormes.castformF, Species.SMFormes.castformW, Species.SMFormes.castformI,
            Species.SMFormes.darmanitanZ,
            Species.SMFormes.meloettaP,
            Species.SMFormes.kyuremW,
            Species.SMFormes.kyuremB,
            Species.SMFormes.gengarMega,
            Species.SMFormes.gardevoirMega,
            Species.SMFormes.ampharosMega,
            Species.SMFormes.venusaurMega,
            Species.SMFormes.charizardMegaX, Species.SMFormes.charizardMegaY,
            Species.SMFormes.mewtwoMegaX, Species.SMFormes.mewtwoMegaY,
            Species.SMFormes.blazikenMega,
            Species.SMFormes.medichamMega,
            Species.SMFormes.houndoomMega,
            Species.SMFormes.aggronMega,
            Species.SMFormes.banetteMega,
            Species.SMFormes.tyranitarMega,
            Species.SMFormes.scizorMega,
            Species.SMFormes.pinsirMega,
            Species.SMFormes.aerodactylMega,
            Species.SMFormes.lucarioMega,
            Species.SMFormes.abomasnowMega,
            Species.SMFormes.aegislashB,
            Species.SMFormes.blastoiseMega,
            Species.SMFormes.kangaskhanMega,
            Species.SMFormes.gyaradosMega,
            Species.SMFormes.absolMega,
            Species.SMFormes.alakazamMega,
            Species.SMFormes.heracrossMega,
            Species.SMFormes.mawileMega,
            Species.SMFormes.manectricMega,
            Species.SMFormes.garchompMega,
            Species.SMFormes.latiosMega,
            Species.SMFormes.latiasMega,
            Species.SMFormes.swampertMega,
            Species.SMFormes.sceptileMega,
            Species.SMFormes.sableyeMega,
            Species.SMFormes.altariaMega,
            Species.SMFormes.galladeMega,
            Species.SMFormes.audinoMega,
            Species.SMFormes.sharpedoMega,
            Species.SMFormes.slowbroMega,
            Species.SMFormes.steelixMega,
            Species.SMFormes.pidgeotMega,
            Species.SMFormes.glalieMega,
            Species.SMFormes.diancieMega,
            Species.SMFormes.metagrossMega,
            Species.SMFormes.kyogreP,
            Species.SMFormes.groudonP,
            Species.SMFormes.rayquazaMega,
            Species.SMFormes.cameruptMega,
            Species.SMFormes.lopunnyMega,
            Species.SMFormes.salamenceMega,
            Species.SMFormes.beedrillMega,
            Species.SMFormes.wishiwashiS,
            Species.SMFormes.greninjaA,
            Species.SMFormes.zygardeC,
            Species.SMFormes.miniorC
    );

    public static final List<Integer> irregularFormesUSUM = Arrays.asList(
            Species.USUMFormes.castformF, Species.USUMFormes.castformW, Species.USUMFormes.castformI,
            Species.USUMFormes.darmanitanZ,
            Species.USUMFormes.meloettaP,
            Species.USUMFormes.kyuremW,
            Species.USUMFormes.kyuremB,
            Species.USUMFormes.gengarMega,
            Species.USUMFormes.gardevoirMega,
            Species.USUMFormes.ampharosMega,
            Species.USUMFormes.venusaurMega,
            Species.USUMFormes.charizardMegaX, Species.USUMFormes.charizardMegaY,
            Species.USUMFormes.mewtwoMegaX, Species.USUMFormes.mewtwoMegaY,
            Species.USUMFormes.blazikenMega,
            Species.USUMFormes.medichamMega,
            Species.USUMFormes.houndoomMega,
            Species.USUMFormes.aggronMega,
            Species.USUMFormes.banetteMega,
            Species.USUMFormes.tyranitarMega,
            Species.USUMFormes.scizorMega,
            Species.USUMFormes.pinsirMega,
            Species.USUMFormes.aerodactylMega,
            Species.USUMFormes.lucarioMega,
            Species.USUMFormes.abomasnowMega,
            Species.USUMFormes.aegislashB,
            Species.USUMFormes.blastoiseMega,
            Species.USUMFormes.kangaskhanMega,
            Species.USUMFormes.gyaradosMega,
            Species.USUMFormes.absolMega,
            Species.USUMFormes.alakazamMega,
            Species.USUMFormes.heracrossMega,
            Species.USUMFormes.mawileMega,
            Species.USUMFormes.manectricMega,
            Species.USUMFormes.garchompMega,
            Species.USUMFormes.latiosMega,
            Species.USUMFormes.latiasMega,
            Species.USUMFormes.swampertMega,
            Species.USUMFormes.sceptileMega,
            Species.USUMFormes.sableyeMega,
            Species.USUMFormes.altariaMega,
            Species.USUMFormes.galladeMega,
            Species.USUMFormes.audinoMega,
            Species.USUMFormes.sharpedoMega,
            Species.USUMFormes.slowbroMega,
            Species.USUMFormes.steelixMega,
            Species.USUMFormes.pidgeotMega,
            Species.USUMFormes.glalieMega,
            Species.USUMFormes.diancieMega,
            Species.USUMFormes.metagrossMega,
            Species.USUMFormes.kyogreP,
            Species.USUMFormes.groudonP,
            Species.USUMFormes.rayquazaMega,
            Species.USUMFormes.cameruptMega,
            Species.USUMFormes.lopunnyMega,
            Species.USUMFormes.salamenceMega,
            Species.USUMFormes.beedrillMega,
            Species.USUMFormes.wishiwashiS,
            Species.USUMFormes.greninjaA,
            Species.USUMFormes.zygardeC,
            Species.USUMFormes.miniorC,
            Species.USUMFormes.necrozmaDM,
            Species.USUMFormes.necrozmaDW,
            Species.USUMFormes.necrozmaU
    );

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
            damageAdjacentFoesTrappingEffect = 373, damageTargetTrappingEffect = 384;

    public static final int noDamageStatusQuality = 1, noDamageStatChangeQuality = 2, damageStatusQuality = 4,
            noDamageStatusAndStatChangeQuality = 5, damageTargetDebuffQuality = 6, damageUserBuffQuality = 7,
            damageAbsorbQuality = 8;

    public static List<Integer> bannedMoves = Arrays.asList(Moves.darkVoid, Moves.hyperspaceFury);

    public static final Type[] typeTable = constructTypeTable();

    private static final String tmDataPrefixSM = "034003410342034303",  tmDataPrefixUSUM = "03BC03BD03BE03BF03";
    public static final int tmCount = 100, tmBlockOneCount = 92, tmBlockTwoCount = 3, tmBlockThreeCount = 5,
            tmBlockOneOffset = Items.tm01, tmBlockTwoOffset = Items.tm93, tmBlockThreeOffset = Items.tm96;
    public static final String itemPalettesPrefix = "070000000000000000010100";

    public static final int shopItemsOffsetSM = 0x50A8;
    public static final int shopItemsOffsetUSUM = 0x50BC;

    public static final int tutorsOffset = 0x54DE;
    public static final String tutorsPrefix = "5F6F6E5F6F6666FF";
    public static final int tutorMoveCount = 67;

    public static final String[] fastestTextPrefixes = new String[]{"1080BDE80E000500F0412DE9", "34019FE50060A0E3"};

    private static final List<Integer> mainGameShopsSM = Arrays.asList(
            8, 9, 10, 11, 14, 15, 17, 20, 21, 22, 23
    );

    private static final List<Integer> mainGameShopsUSUM = Arrays.asList(
            8, 9, 10, 11, 14, 15, 17, 20, 21, 22, 23, 24, 25, 26, 27
    );

    public static final List<Integer> evolutionItems = Arrays.asList(Items.sunStone, Items.moonStone, Items.fireStone,
            Items.thunderStone, Items.waterStone, Items.leafStone, Items.shinyStone, Items.duskStone, Items.dawnStone,
            Items.ovalStone, Items.kingsRock, Items.deepSeaTooth, Items.deepSeaScale, Items.metalCoat, Items.dragonScale,
            Items.upgrade, Items.protector, Items.electirizer, Items.magmarizer, Items.dubiousDisc, Items.reaperCloth,
            Items.razorClaw, Items.razorFang, Items.prismScale, Items.whippedDream, Items.sachet, Items.iceStone);

    private static final List<Boolean> relevantEncounterFilesSM = setupRelevantEncounterFiles(Type_SM);
    private static final List<Boolean> relevantEncounterFilesUSUM = setupRelevantEncounterFiles(Type_USUM);

    public static final List<Integer> heldZCrystals = Arrays.asList(
            Items.normaliumZHeld, // Normal
            Items.fightiniumZHeld, // Fighting
            Items.flyiniumZHeld, // Flying
            Items.poisoniumZHeld, // Poison
            Items.groundiumZHeld, // Ground
            Items.rockiumZHeld, // Rock
            Items.buginiumZHeld, // Bug
            Items.ghostiumZHeld, // Ghost
            Items.steeliumZHeld, // Steel
            Items.firiumZHeld, // Fire
            Items.wateriumZHeld, // Water
            Items.grassiumZHeld, // Grass
            Items.electriumZHeld, // Electric
            Items.psychiumZHeld, // Psychic
            Items.iciumZHeld, // Ice
            Items.dragoniumZHeld, // Dragon
            Items.darkiniumZHeld, // Dark
            Items.fairiumZHeld  // Fairy
    );

    public static final Map<Integer,List<Integer>> abilityVariations = setupAbilityVariations();

    private static Map<Integer,List<Integer>> setupAbilityVariations() {
        Map<Integer,List<Integer>> map = new HashMap<>();
        map.put(Abilities.insomnia, Arrays.asList(Abilities.insomnia, Abilities.vitalSpirit));
        map.put(Abilities.clearBody, Arrays.asList(Abilities.clearBody, Abilities.whiteSmoke, Abilities.fullMetalBody));
        map.put(Abilities.hugePower, Arrays.asList(Abilities.hugePower, Abilities.purePower));
        map.put(Abilities.battleArmor, Arrays.asList(Abilities.battleArmor, Abilities.shellArmor));
        map.put(Abilities.cloudNine, Arrays.asList(Abilities.cloudNine, Abilities.airLock));
        map.put(Abilities.filter, Arrays.asList(Abilities.filter, Abilities.solidRock, Abilities.prismArmor));
        map.put(Abilities.roughSkin, Arrays.asList(Abilities.roughSkin, Abilities.ironBarbs));
        map.put(Abilities.moldBreaker, Arrays.asList(Abilities.moldBreaker, Abilities.turboblaze, Abilities.teravolt));
        map.put(Abilities.wimpOut, Arrays.asList(Abilities.wimpOut, Abilities.emergencyExit));
        map.put(Abilities.queenlyMajesty, Arrays.asList(Abilities.queenlyMajesty, Abilities.dazzling));
        map.put(Abilities.gooey, Arrays.asList(Abilities.gooey, Abilities.tanglingHair));
        map.put(Abilities.receiver, Arrays.asList(Abilities.receiver, Abilities.powerOfAlchemy));
        map.put(Abilities.multiscale, Arrays.asList(Abilities.multiscale, Abilities.shadowShield));

        return map;
    }

    public static final List<Integer> uselessAbilities = Arrays.asList(Abilities.forecast, Abilities.multitype,
            Abilities.flowerGift, Abilities.zenMode, Abilities.stanceChange, Abilities.shieldsDown, Abilities.schooling,
            Abilities.disguise, Abilities.battleBond, Abilities.powerConstruct, Abilities.rksSystem);

    private static final String saveLoadFormeReversionPrefixSM = "00EB040094E50C1094E5F70E80E2", saveLoadFormeReversionPrefixUSUM = "00EB040094E50C1094E5030B80E2EE0F80E2";
    public static final String afterBattleFormeReversionPrefix = "0055E10B00001A0010A0E30700A0E1";

    public static final String ninjaskSpeciesPrefix = "11FF2FE11CD08DE2F080BDE8", shedinjaPrefix = "A0E194FDFFEB0040A0E1";

    public static final String beastLusaminePokemonBoostsPrefix = "1D14FFFF";
    public static final int beastLusamineTrainerIndex = 157;

    public static final String miniorWildEncounterPatchPrefix = "032C42E2062052E2";

    public static final int zygardeAssemblyScriptFile = 45;
    public static final String zygardeAssemblyFormePrefix = "BC21CDE1B801CDE1", zygardeAssemblySpeciesPrefix = "FBEB4CD08DE20400A0E1F08FBDE8";

    public static final String friendshipValueForEvoLocator = "DC0050E3F700002A";

    public static final String perfectOddsBranchLocator = "050000BA000050E3";

    public static int getPokemonCount(int romType) {
        if (romType == Type_SM) {
            return pokemonCountSM;
        } else if (romType == Type_USUM) {
            return pokemonCountUSUM;
        }
        return pokemonCountSM;
    }

    public static List<Integer> getRegularShopItems(int romType) {
        if (romType == Type_SM) {
            return regularShopItemsSM;
        } else {
            return regularShopItemsUSUM;
        }
    }

    public static final List<Integer> consumableHeldItems = setupAllConsumableItems();

    private static List<Integer> setupAllConsumableItems() {
        List<Integer> list = new ArrayList<>(Gen6Constants.consumableHeldItems);
        list.addAll(Arrays.asList(Items.adrenalineOrb, Items.electricSeed, Items.psychicSeed, Items.mistySeed, Items.grassySeed));
        return list;
    }

    public static final List<Integer> allHeldItems = setupAllHeldItems();

    private static List<Integer> setupAllHeldItems() {
        // We intentionally do not include Z Crystals in this list. Adding Z-Crystals to random trainers should
        // probably require its own setting if desired.
        List<Integer> list = new ArrayList<>(Gen6Constants.allHeldItems);
        list.addAll(Arrays.asList(Items.adrenalineOrb, Items.electricSeed, Items.psychicSeed, Items.mistySeed, Items.grassySeed));
        list.addAll(Arrays.asList(Items.terrainExtender, Items.protectivePads));
        return list;
    }

    public static final List<Integer> generalPurposeConsumableItems = initializeGeneralPurposeConsumableItems();

    private static List<Integer> initializeGeneralPurposeConsumableItems() {
        List<Integer> list = new ArrayList<>(Gen6Constants.generalPurposeConsumableItems);
        // These berries are worth the risk of causing confusion because they heal for half max HP.
        list.addAll(Arrays.asList(Items.figyBerry, Items.wikiBerry, Items.magoBerry,
                Items.aguavBerry, Items.iapapaBerry, Items.adrenalineOrb));
        return Collections.unmodifiableList(list);
    }

    public static final List<Integer> generalPurposeItems = initializeGeneralPurposeItems();

    private static List<Integer> initializeGeneralPurposeItems() {
        List<Integer> list = new ArrayList<>(Gen6Constants.generalPurposeItems);
        list.addAll(Arrays.asList(Items.protectivePads));
        return Collections.unmodifiableList(list);
    }

    public static final Map<Integer, List<Integer>> moveBoostingItems = initializeMoveBoostingItems();

    private static Map<Integer, List<Integer>> initializeMoveBoostingItems() {
        Map<Integer, List<Integer>> map = new HashMap<>(Gen6Constants.moveBoostingItems);
        map.put(Moves.electricTerrain, Arrays.asList(Items.terrainExtender));
        map.put(Moves.grassyTerrain, Arrays.asList(Items.terrainExtender));
        map.put(Moves.mistyTerrain, Arrays.asList(Items.terrainExtender));
        map.put(Moves.psychicTerrain, Arrays.asList(Items.terrainExtender));
        map.put(Moves.strengthSap, Arrays.asList(Items.bigRoot));
        return Collections.unmodifiableMap(map);
    }
    public static final Map<Integer, List<Integer>> abilityBoostingItems = initializeAbilityBoostingItems();

    private static Map<Integer, List<Integer>> initializeAbilityBoostingItems() {
        Map<Integer, List<Integer>> map = new HashMap<>(Gen6Constants.abilityBoostingItems);
        map.put(Abilities.electricSurge, Arrays.asList(Items.terrainExtender));
        map.put(Abilities.grassySurge, Arrays.asList(Items.terrainExtender));
        map.put(Abilities.mistySurge, Arrays.asList(Items.terrainExtender));
        map.put(Abilities.psychicSurge, Arrays.asList(Items.terrainExtender));
        return Collections.unmodifiableMap(map);
    }

    public static final Map<Integer, Integer> consumableAbilityBoostingItems = initializeConsumableAbilityBoostingItems();

    private static Map<Integer, Integer> initializeConsumableAbilityBoostingItems() {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(Abilities.electricSurge, Items.electricSeed);
        map.put(Abilities.grassySurge, Items.grassySeed);
        map.put(Abilities.mistySurge, Items.mistySeed);
        map.put(Abilities.psychicSurge, Items.psychicSeed);
        return Collections.unmodifiableMap(map);
    }

    // None of these have new entries in Gen VII.
    public static final Map<Type, Integer> consumableTypeBoostingItems = Gen6Constants.consumableTypeBoostingItems;
    public static final Map<Integer, List<Integer>> speciesBoostingItems = Gen6Constants.speciesBoostingItems;
    public static final Map<Type, List<Integer>> typeBoostingItems = Gen6Constants.typeBoostingItems;
    public static final Map<Type, Integer> weaknessReducingBerries = Gen6Constants.weaknessReducingBerries;

    public static boolean isZCrystal(int itemIndex) {
        // From https://bulbapedia.bulbagarden.net/wiki/List_of_items_by_index_number_(Generation_VII)
        return (itemIndex >= Items.normaliumZHeld && itemIndex <= Items.pikaniumZHeld) ||
                (itemIndex >= Items.decidiumZHeld && itemIndex <= Items.pikashuniumZBag) ||
                (itemIndex >= Items.solganiumZBag && itemIndex <= Items.kommoniumZBag);

    }

    public static List<String> getShopNames(int romType) {
        List<String> shopNames = new ArrayList<>();
        shopNames.add("Primary 0 Trials");
        shopNames.add("Primary 1 Trials");
        shopNames.add("Primary 2 Trials");
        shopNames.add("Primary 3 Trials");
        shopNames.add("Primary 4 Trials");
        shopNames.add("Primary 5 Trials");
        shopNames.add("Primary 6 Trials");
        shopNames.add("Primary 7 Trials");
        shopNames.add("Konikoni City Incenses");
        shopNames.add("Konikoni City Herbs");
        shopNames.add("Hau'oli City Secondary");
        shopNames.add("Route 2 Secondary");
        shopNames.add("Heahea City Secondary (TMs)");
        shopNames.add("Royal Avenue Secondary (TMs)");
        shopNames.add("Route 8 Secondary");
        shopNames.add("Paniola Town Secondary");
        shopNames.add("Malie City Secondary (TMs)");
        shopNames.add("Mount Hokulani Secondary");
        shopNames.add("Seafolk Village Secondary (TMs)");
        shopNames.add("Konikoni City TMs");
        shopNames.add("Konikoni City Stones");
        shopNames.add("Thrifty Megamart, Center-Left");
        shopNames.add("Thrifty Megamart, Center-Right");
        shopNames.add("Thrifty Megamart, Right");
        if (romType == Type_USUM) {
            shopNames.add("Route 5 Secondary");
            shopNames.add("Konikoni City Secondary");
            shopNames.add("Tapu Village Secondary");
            shopNames.add("Mount Lanakila Secondary");
        }
        return shopNames;
    }

    public static List<Integer> getMainGameShops(int romType) {
        if (romType == Type_SM) {
            return mainGameShopsSM;
        } else {
            return mainGameShopsUSUM;
        }
    }

    public static int getShopItemsOffset(int romType) {
        if (romType == Type_SM) {
            return shopItemsOffsetSM;
        } else {
            return shopItemsOffsetUSUM;
        }
    }

    public static int getFormeCount(int romType) {
        if (romType == Type_SM) {
            return formeCountSM;
        } else {
            return formeCountUSUM;
        }
    }

    public static int getMoveCount(int romType) {
        if (romType == Type_SM) {
            return moveCountSM;
        } else if (romType == Type_USUM) {
            return moveCountUSUM;
        }
        return moveCountSM;
    }

    public static String getTmDataPrefix(int romType) {
        if (romType == Type_SM) {
            return tmDataPrefixSM;
        } else if (romType == Type_USUM) {
            return tmDataPrefixUSUM;
        }
        return tmDataPrefixSM;
    }

    public static int getHighestAbilityIndex(int romType) {
        if (romType == Type_SM) {
            return highestAbilityIndexSM;
        } else if (romType == Type_USUM) {
            return highestAbilityIndexUSUM;
        }
        return highestAbilityIndexSM;
    }

    public static List<Boolean> getRelevantEncounterFiles(int romType) {
        if (romType == Type_SM) {
            return relevantEncounterFilesSM;
        } else {
            return relevantEncounterFilesUSUM;
        }
    }

    public static String getSaveLoadFormeReversionPrefix(int romType) {
        if (romType == Type_SM) {
            return saveLoadFormeReversionPrefixSM;
        } else {
            return saveLoadFormeReversionPrefixUSUM;
        }
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

        for (Integer species: Gen6Constants.speciesToMegaStoneORAS.keySet()) {
            Map<Integer,String> megaMap = new HashMap<>();
            if (species == Species.charizard || species == Species.mewtwo) {
                megaMap.put(1,"-Mega-X");
                megaMap.put(2,"-Mega-Y");
            } else {
                megaMap.put(1,"-Mega");
            }
            map.put(species,megaMap);
        }

        Map<Integer,String> wishiwashiMap = new HashMap<>();
        wishiwashiMap.put(1,"-S");
        map.put(Species.wishiwashi, wishiwashiMap);

        Map<Integer,String> oricorioMap = new HashMap<>();
        oricorioMap.put(1,"-E");
        oricorioMap.put(2,"-P");
        oricorioMap.put(3,"-G");
        map.put(Species.oricorio, oricorioMap);

        Map<Integer,String> lycanrocMap = new HashMap<>();
        lycanrocMap.put(1,"-M");
        lycanrocMap.put(2,"-D");
        map.put(Species.lycanroc, lycanrocMap);

        for (int species: speciesWithAlolanForms) {
            Map<Integer,String> alolanMap = new HashMap<>();
            alolanMap.put(1,"-A");
            map.put(species, alolanMap);
        }

        Map<Integer,String> greninjaMap = new HashMap<>();
        greninjaMap.put(2,"-A");
        map.put(Species.greninja, greninjaMap);

        Map<Integer,String> zygardeMap = new HashMap<>();
        zygardeMap.put(1,"-10");
        zygardeMap.put(4,"-C");
        map.put(Species.zygarde, zygardeMap);

        Map<Integer,String> miniorMap = new HashMap<>();
        miniorMap.put(7,"-C");
        map.put(Species.minior, miniorMap);

        Map<Integer,String> necrozmaMap = new HashMap<>();
        necrozmaMap.put(1,"-DM");
        necrozmaMap.put(2,"-DW");
        necrozmaMap.put(3,"-U");
        map.put(Species.necrozma, necrozmaMap);

        return map;
    }

    private static Map<Integer,String> setupDummyFormeSuffixes() {
        Map<Integer,String> m = new HashMap<>();
        m.put(0,"");
        return m;
    }

    private static List<Integer> actuallyCosmeticFormsSM = Arrays.asList(
            Species.SMFormes.cherrimCosmetic1,
            Species.SMFormes.shellosCosmetic1,
            Species.SMFormes.gastrodonCosmetic1,
            Species.SMFormes.keldeoCosmetic1,
            Species.SMFormes.furfrouCosmetic1, Species.SMFormes.furfrouCosmetic2,
            Species.SMFormes.furfrouCosmetic3, Species.SMFormes.furfrouCosmetic4,
            Species.SMFormes.furfrouCosmetic5, Species.SMFormes.furfrouCosmetic6,
            Species.SMFormes.furfrouCosmetic7, Species.SMFormes.furfrouCosmetic8,
            Species.SMFormes.furfrouCosmetic9,
            Species.SMFormes.pumpkabooCosmetic1, Species.SMFormes.pumpkabooCosmetic2,
            Species.SMFormes.pumpkabooCosmetic3,
            Species.SMFormes.gourgeistCosmetic1, Species.SMFormes.gourgeistCosmetic2,
            Species.SMFormes.gourgeistCosmetic3,
            Species.SMFormes.floetteCosmetic1, Species.SMFormes.floetteCosmetic2,
            Species.SMFormes.floetteCosmetic3, Species.SMFormes.floetteCosmetic4,
            Species.SMFormes.raticateACosmetic1,
            Species.SMFormes.mimikyuCosmetic1, Species.SMFormes.mimikyuCosmetic2, Species.SMFormes.mimikyuCosmetic3,
            Species.SMFormes.gumshoosCosmetic1,
            Species.SMFormes.vikavoltCosmetic1,
            Species.SMFormes.lurantisCosmetic1,
            Species.SMFormes.salazzleCosmetic1,
            Species.SMFormes.kommoOCosmetic1,
            Species.SMFormes.greninjaCosmetic1,
            Species.SMFormes.zygarde10Cosmetic1, Species.SMFormes.zygardeCosmetic1,
            Species.SMFormes.miniorCosmetic1, Species.SMFormes.miniorCosmetic2, Species.SMFormes.miniorCosmetic3,
            Species.SMFormes.miniorCosmetic4, Species.SMFormes.miniorCosmetic5, Species.SMFormes.miniorCosmetic6,
            Species.SMFormes.miniorCCosmetic1, Species.SMFormes.miniorCCosmetic2, Species.SMFormes.miniorCCosmetic3,
            Species.SMFormes.miniorCCosmetic4, Species.SMFormes.miniorCCosmetic5, Species.SMFormes.miniorCCosmetic6,
            Species.SMFormes.magearnaCosmetic1,
            Species.SMFormes.pikachuCosmetic1, Species.SMFormes.pikachuCosmetic2, Species.SMFormes.pikachuCosmetic3,
            Species.SMFormes.pikachuCosmetic4, Species.SMFormes.pikachuCosmetic5, Species.SMFormes.pikachuCosmetic6 // Pikachu With Funny Hats
    );

    private static List<Integer> actuallyCosmeticFormsUSUM = Arrays.asList(
            Species.USUMFormes.cherrimCosmetic1,
            Species.USUMFormes.shellosCosmetic1,
            Species.USUMFormes.gastrodonCosmetic1,
            Species.USUMFormes.keldeoCosmetic1,
            Species.USUMFormes.furfrouCosmetic1, Species.USUMFormes.furfrouCosmetic2,
            Species.USUMFormes.furfrouCosmetic3, Species.USUMFormes.furfrouCosmetic4,
            Species.USUMFormes.furfrouCosmetic5, Species.USUMFormes.furfrouCosmetic6,
            Species.USUMFormes.furfrouCosmetic7, Species.USUMFormes.furfrouCosmetic8,
            Species.USUMFormes.furfrouCosmetic9,
            Species.USUMFormes.pumpkabooCosmetic1, Species.USUMFormes.pumpkabooCosmetic2,
            Species.USUMFormes.pumpkabooCosmetic3,
            Species.USUMFormes.gourgeistCosmetic1, Species.USUMFormes.gourgeistCosmetic2,
            Species.USUMFormes.gourgeistCosmetic3,
            Species.USUMFormes.floetteCosmetic1, Species.USUMFormes.floetteCosmetic2,
            Species.USUMFormes.floetteCosmetic3, Species.USUMFormes.floetteCosmetic4,
            Species.USUMFormes.raticateACosmetic1,
            Species.USUMFormes.marowakACosmetic1,
            Species.USUMFormes.mimikyuCosmetic1, Species.USUMFormes.mimikyuCosmetic2, Species.USUMFormes.mimikyuCosmetic3,
            Species.USUMFormes.gumshoosCosmetic1,
            Species.USUMFormes.vikavoltCosmetic1,
            Species.USUMFormes.lurantisCosmetic1,
            Species.USUMFormes.salazzleCosmetic1,
            Species.USUMFormes.kommoOCosmetic1,
            Species.USUMFormes.araquanidCosmetic1,
            Species.USUMFormes.togedemaruCosmetic1,
            Species.USUMFormes.ribombeeCosmetic1,
            Species.USUMFormes.greninjaCosmetic1,
            Species.USUMFormes.zygarde10Cosmetic1, Species.USUMFormes.zygardeCosmetic1,
            Species.USUMFormes.miniorCosmetic1, Species.USUMFormes.miniorCosmetic2, Species.USUMFormes.miniorCosmetic3,
            Species.USUMFormes.miniorCosmetic4, Species.USUMFormes.miniorCosmetic5, Species.USUMFormes.miniorCosmetic6,
            Species.USUMFormes.miniorCCosmetic1, Species.USUMFormes.miniorCCosmetic2, Species.USUMFormes.miniorCCosmetic3,
            Species.USUMFormes.miniorCCosmetic4, Species.USUMFormes.miniorCCosmetic5, Species.USUMFormes.miniorCCosmetic6,
            Species.USUMFormes.magearnaCosmetic1,
            Species.USUMFormes.pikachuCosmetic1, Species.USUMFormes.pikachuCosmetic2, Species.USUMFormes.pikachuCosmetic3,
            Species.USUMFormes.pikachuCosmetic4, Species.USUMFormes.pikachuCosmetic5, Species.USUMFormes.pikachuCosmetic6,
            Species.USUMFormes.pikachuCosmetic7, // Pikachu With Funny Hats
            Species.USUMFormes.rockruffCosmetic1
    );

    public static List<Integer> getActuallyCosmeticForms(int romType) {
        if (romType == Type_SM) {
            return actuallyCosmeticFormsSM;
        } else {
            return actuallyCosmeticFormsUSUM;
        }
    }

    private static List<Integer> ignoreFormsSM = Arrays.asList(
            Species.SMFormes.cherrimCosmetic1,
            Species.SMFormes.greninjaCosmetic1,
            Species.SMFormes.zygarde10Cosmetic1,
            Species.SMFormes.zygardeCosmetic1,
            Species.SMFormes.miniorCosmetic1,
            Species.SMFormes.miniorCosmetic2,
            Species.SMFormes.miniorCosmetic3,
            Species.SMFormes.miniorCosmetic4,
            Species.SMFormes.miniorCosmetic5,
            Species.SMFormes.miniorCosmetic6,
            Species.SMFormes.mimikyuCosmetic1,
            Species.SMFormes.mimikyuCosmetic3
    );

    private static List<Integer> ignoreFormsUSUM = Arrays.asList(
            Species.USUMFormes.cherrimCosmetic1,
            Species.USUMFormes.greninjaCosmetic1,
            Species.USUMFormes.zygarde10Cosmetic1,
            Species.USUMFormes.zygardeCosmetic1,
            Species.USUMFormes.miniorCosmetic1,
            Species.USUMFormes.miniorCosmetic2,
            Species.USUMFormes.miniorCosmetic3,
            Species.USUMFormes.miniorCosmetic4,
            Species.USUMFormes.miniorCosmetic5,
            Species.USUMFormes.miniorCosmetic6,
            Species.USUMFormes.mimikyuCosmetic1,
            Species.USUMFormes.mimikyuCosmetic3,
            Species.USUMFormes.rockruffCosmetic1
    );

    public static List<Integer> getIgnoreForms(int romType) {
        if (romType == Type_SM) {
            return ignoreFormsSM;
        } else {
            return ignoreFormsUSUM;
        }
    }

    private static Map<Integer,Integer> altFormesWithCosmeticFormsSM = setupAltFormesWithCosmeticForms(Type_SM);
    private static Map<Integer,Integer> altFormesWithCosmeticFormsUSUM = setupAltFormesWithCosmeticForms(Type_USUM);

    public static Map<Integer,Integer> getAltFormesWithCosmeticForms(int romType) {
        if (romType == Type_SM) {
            return altFormesWithCosmeticFormsSM;
        } else {
            return altFormesWithCosmeticFormsUSUM;
        }
    }

    private static Map<Integer,Integer> setupAltFormesWithCosmeticForms(int romType) {
        Map<Integer,Integer> map = new HashMap<>();
        if (romType == Type_SM) {
            map.put(Species.SMFormes.raticateA,1); // 1 form (Totem)
            map.put(Species.SMFormes.zygarde10,1); // 1 form (Power Construct)
            map.put(Species.SMFormes.miniorC,6); // 6 forms (colors)
        } else {
            map.put(Species.USUMFormes.raticateA,1); // 1 form (Totem)
            map.put(Species.USUMFormes.marowakA,1); // 1 form (Totem)
            map.put(Species.USUMFormes.zygarde10,1); // 1 form (Power Construct)
            map.put(Species.USUMFormes.miniorC,6); // 6 forms (colors)
        }

        return map;
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

    private static List<Boolean> setupRelevantEncounterFiles(int romType) {
        int fileCount = romType == Type_SM ? 2761 : 3696;
        List<Boolean> list = new ArrayList<>();

        for (int i = 0; i < fileCount; i++) {
            if (((i - 9) % 11 == 0) || (i % 11 == 0)) {
                list.add(true);
            } else {
                list.add(false);
            }
        }

        return list;
    }


    public static Map<Integer, List<Integer>> getHardcodedTradeTextOffsets(int romType) {
        Map<Integer, List<Integer>> hardcodedTradeTextOffsets = new HashMap<>();
        if (romType == Gen7Constants.Type_USUM) {
            // For some reason, the Route 2 trade is hardcoded in USUM but not in SM
            hardcodedTradeTextOffsets.put(0, Arrays.asList(20, 21, 22));
        }
        hardcodedTradeTextOffsets.put(1, Arrays.asList(26, 28, 30));
        hardcodedTradeTextOffsets.put(2, Arrays.asList(32, 33, 34, 36));
        hardcodedTradeTextOffsets.put(3, Arrays.asList(38, 39, 40, 42));
        hardcodedTradeTextOffsets.put(4, Arrays.asList(44, 45, 46, 48));
        hardcodedTradeTextOffsets.put(5, Arrays.asList(50, 51, 52, 54));
        hardcodedTradeTextOffsets.put(6, Arrays.asList(56, 57, 58, 60));
        return hardcodedTradeTextOffsets;
    }

    public static ItemList allowedItemsSM, allowedItemsUSUM, nonBadItems;
    public static List<Integer> regularShopItemsSM, regularShopItemsUSUM, opShopItems;

    static {
        setupAllowedItems();
    }

    private static void setupAllowedItems() {
        allowedItemsSM = new ItemList(Items.fairyMemory);
        // Key items + version exclusives
        allowedItemsSM.banRange(Items.explorerKit, 76);
        allowedItemsSM.banRange(Items.dataCard01, 32);
        allowedItemsSM.banRange(Items.xtransceiverMale, 18);
        allowedItemsSM.banSingles(Items.expShare, Items.libertyPass, Items.propCase, Items.dragonSkull,
                Items.lightStone, Items.darkStone);
        // Unknown blank items or version exclusives
        allowedItemsSM.banRange(Items.tea, 3);
        allowedItemsSM.banRange(Items.unused120, 14);
        // TMs & HMs - tms cant be held in gen7
        allowedItemsSM.tmRange(Items.tm01, 92);
        allowedItemsSM.tmRange(Items.tm93, 3);
        allowedItemsSM.banRange(Items.tm01, 100);
        allowedItemsSM.banRange(Items.tm93, 3);
        // Battle Launcher exclusives
        allowedItemsSM.banRange(Items.direHit2, 24);

        // Key items (Gen 6)
        allowedItemsSM.banRange(Items.holoCasterMale,3);
        allowedItemsSM.banSingles(Items.pokeFlute, Items.sprinklotad);
        allowedItemsSM.banRange(Items.powerPlantPass,4);
        allowedItemsSM.banRange(Items.elevatorKey,4);
        allowedItemsSM.banRange(Items.lensCase,3);
        allowedItemsSM.banRange(Items.lookerTicket,3);
        allowedItemsSM.banRange(Items.megaCharm,2);

        // TMs (Gen 6)
        allowedItemsSM.tmRange(Items.tm96,5);
        allowedItemsSM.banRange(Items.tm96,5);

        // Key items and an HM
        allowedItemsSM.banRange(Items.machBike,34);
        allowedItemsSM.banRange(Items.prisonBottle,2);
        allowedItemsSM.banRange(Items.meteoriteThirdForm,5);

        // Z-Crystals
        allowedItemsSM.banRange(Items.normaliumZHeld,19);
        allowedItemsSM.banRange(Items.decidiumZHeld,39);

        // Key Items (Gen 7)
        allowedItemsSM.banSingles(Items.zRing, Items.sparklingStone, Items.zygardeCube, Items.ridePager,
                Items.sunFlute, Items.moonFlute, Items.enigmaticCard);
        allowedItemsSM.banRange(Items.forageBag,3);

        // Unused
        allowedItemsSM.banSingles(Items.unused848, Items.unused859);
        allowedItemsSM.banRange(Items.unused837,4);
        allowedItemsSM.banRange(Items.silverRazzBerry,18);
        allowedItemsSM.banRange(Items.stretchySpring,19);

        allowedItemsUSUM = allowedItemsSM.copy(Items.rotoCatch);

        // Z-Crystals
        allowedItemsUSUM.banRange(Items.solganiumZBag,12);

        // Key Items
        allowedItemsUSUM.banRange(Items.zPowerRing,16);

        // ROTO LOTO
        allowedItemsUSUM.banRange(Items.rotoHatch,11);

        // non-bad items
        // ban specific pokemon hold items, berries, apricorns, mail
        nonBadItems = allowedItemsSM.copy();

        nonBadItems.banSingles(Items.oddKeystone, Items.griseousOrb, Items.soulDew, Items.lightBall,
                Items.oranBerry, Items.quickPowder, Items.passOrb, Items.discountCoupon, Items.strangeSouvenir,
                Items.festivalTicket);
        nonBadItems.banRange(Items.growthMulch, 4); // mulch
        nonBadItems.banRange(Items.adamantOrb, 2); // orbs
        nonBadItems.banRange(Items.mail1, 12); // mails
        nonBadItems.banRange(Items.figyBerry, 25); // berries without useful battle effects
        nonBadItems.banRange(Items.luckyPunch, 4); // pokemon specific
        nonBadItems.banRange(Items.redScarf, 5); // contest scarves
        nonBadItems.banRange(Items.richMulch,4); // more mulch
        nonBadItems.banRange(Items.gengarite, 30); // Mega Stones, part 1
        nonBadItems.banRange(Items.swampertite, 13); // Mega Stones, part 2
        nonBadItems.banRange(Items.cameruptite, 4); // Mega Stones, part 3
        nonBadItems.banRange(Items.fightingMemory,17); // Memories
        nonBadItems.banRange(Items.relicCopper,7); // relic items
        nonBadItems.banSingles(Items.shoalSalt, Items.shoalShell); // Shoal items; have no purpose and sell for $10.
        nonBadItems.banRange(Items.blueFlute, 5); // Flutes; have no purpose and sell for $10.

        regularShopItemsSM = new ArrayList<>();

        regularShopItemsSM.addAll(IntStream.rangeClosed(Items.ultraBall, Items.pokeBall).boxed().collect(Collectors.toList()));
        regularShopItemsSM.addAll(IntStream.rangeClosed(Items.potion, Items.revive).boxed().collect(Collectors.toList()));
        regularShopItemsSM.addAll(IntStream.rangeClosed(Items.superRepel, Items.repel).boxed().collect(Collectors.toList()));
        regularShopItemsSM.add(Items.honey);
        regularShopItemsSM.add(Items.adrenalineOrb);

        regularShopItemsUSUM = new ArrayList<>(regularShopItemsSM);
        regularShopItemsUSUM.add(Items.pokeToy);

        opShopItems = new ArrayList<>();

        // "Money items" etc
        opShopItems.add(Items.lavaCookie);
        opShopItems.add(Items.berryJuice);
        opShopItems.add(Items.rareCandy);
        opShopItems.add(Items.oldGateau);
        opShopItems.addAll(IntStream.rangeClosed(Items.tinyMushroom, Items.nugget).boxed().collect(Collectors.toList()));
        opShopItems.add(Items.rareBone);
        opShopItems.addAll(IntStream.rangeClosed(Items.lansatBerry, Items.rowapBerry).boxed().collect(Collectors.toList()));
        opShopItems.add(Items.luckyEgg);
        opShopItems.add(Items.prettyFeather);
        opShopItems.addAll(IntStream.rangeClosed(Items.balmMushroom, Items.casteliacone).boxed().collect(Collectors.toList()));
    }

    public static ItemList getAllowedItems(int romType) {
        if (romType == Type_SM) {
            return allowedItemsSM;
        } else {
            return allowedItemsUSUM;
        }
    }

    private static final List<Integer> requiredFieldTMsSM = Arrays.asList(
            80, 49, 5, 83, 64, 62, 100, 31, 46, 88, 57, 41, 59, 73, 53, 61, 28, 39, 55, 86, 30, 93, 81, 84, 74, 85, 72,
            3, 3, 13, 36, 91, 79, 24, 97, 50, 99, 35, 2, 26, 6, 6
    );

    private static final List<Integer> requiredFieldTMsUSUM = Arrays.asList(
            49, 5, 83, 64, 23, 100, 79, 24, 31, 46, 88, 41, 59, 32, 53, 61, 28, 39, 57, 86, 30, 62, 81, 80, 74, 85, 73,
            72, 3, 3, 84, 13, 36, 91, 55, 97, 50, 93, 93, 99, 35, 2, 26, 6, 6
    );

    public static List<Integer> getRequiredFieldTMs(int romType) {
        if (romType == Type_SM) {
            return requiredFieldTMsSM.stream().distinct().collect(Collectors.toList());
        } else {
            return requiredFieldTMsUSUM.stream().distinct().collect(Collectors.toList());
        }
    }

    public static void tagTrainersSM(List<Trainer> trs) {

        tag(trs,"ELITE1", 23, 152, 349); // Hala
        tag(trs,"ELITE2",90, 153, 351); // Olivia
        tag(trs,"ELITE3", 154, 403); // Nanu
        tag(trs,"ELITE4", 155, 359); // Hapu
        tag(trs,"ELITE5", 149, 350); // Acerola
        tag(trs,"ELITE6", 156, 352); // Kahili

        tag(trs,"RIVAL2-0", 129);
        tag(trs,"RIVAL2-1", 413);
        tag(trs,"RIVAL2-2", 414);
        tagRival(trs,"RIVAL3",477);

        tagRival(trs,"FRIEND1", 6);
        tagRival(trs,"FRIEND2", 9);
        tagRival(trs,"FRIEND3", 12);
        tagRival(trs,"FRIEND4", 76);
        tagRival(trs,"FRIEND5", 82);
        tagRival(trs,"FRIEND6", 438);
        tagRival(trs,"FRIEND7", 217);
        tagRival(trs,"FRIEND8", 220);
        tagRival(trs,"FRIEND9", 447);
        tagRival(trs,"FRIEND10", 450);
        tagRival(trs,"FRIEND11", 482);
        tagRival(trs,"FRIEND12", 356);

        tag(trs,"THEMED:GLADION-STRONG", 79, 185, 239, 240, 415, 416, 417, 418, 419, 441);
        tag(trs,"THEMED:ILIMA-STRONG", 52, 215, 216, 396);
        tag(trs,"THEMED:LANA-STRONG", 144);
        tag(trs,"THEMED:KIAWE-STRONG", 398);
        tag(trs,"THEMED:MALLOW-STRONG", 146);
        tag(trs,"THEMED:SOPHOCLES-STRONG", 405);
        tag(trs,"THEMED:MOLAYNE-STRONG", 167, 481);
        tag(trs,"THEMED:MINA-STRONG", 435, 467);
        tag(trs,"THEMED:PLUMERIA-STRONG", 89, 238, 401);
        tag(trs,"THEMED:SINA-STRONG", 75);
        tag(trs,"THEMED:DEXIO-STRONG", 74, 412);
        tag(trs,"THEMED:FABA-STRONG",132, 241, 360, 410);
        tag(trs,"THEMED:GUZMA-LEADER", 138, 235, 236, 400);
        tag(trs,"THEMED:LUSAMINE-LEADER", 131, 158);
    }

    public static void tagTrainersUSUM(List<Trainer> trs) {

        tag(trs,"ELITE1", 23, 650); // Hala
        tag(trs,"ELITE2", 90, 153, 351); // Olivia
        tag(trs,"ELITE3", 154, 508); // Nanu
        tag(trs,"ELITE4", 359, 497); // Hapu
        tag(trs,"ELITE5", 489, 490); // Big Mo
        tag(trs,"ELITE6", 149, 350); // Acerola
        tag(trs,"ELITE7", 156, 352); // Kahili

        tagRival(trs,"RIVAL2", 477); // Kukui

        // Hau
        tagRival(trs,"FRIEND1", 491);
        tagRival(trs,"FRIEND2", 9);
        tagRival(trs,"FRIEND3", 12);
        tagRival(trs,"FRIEND4", 76);
        tagRival(trs,"FRIEND5", 82);
        tagRival(trs,"FRIEND6", 438);
        tagRival(trs,"FRIEND7", 217);
        tagRival(trs,"FRIEND8", 220);
        tagRival(trs,"FRIEND9", 447);
        tagRival(trs,"FRIEND10", 450);
        tagRival(trs,"FRIEND11", 494);
        tagRival(trs,"FRIEND12", 356);

        tag(trs,"THEMED:GLADION-STRONG", 79, 185, 239, 240, 415, 416, 417, 418, 419, 441);
        tag(trs,"THEMED:ILIMA-STRONG", 52, 215, 216, 396, 502);
        tag(trs,"THEMED:LANA-STRONG", 144, 503);
        tag(trs,"THEMED:KIAWE-STRONG", 398, 504);
        tag(trs,"THEMED:MALLOW-STRONG", 146, 505);
        tag(trs,"THEMED:SOPHOCLES-STRONG", 405, 506);
        tag(trs,"THEMED:MINA-STRONG", 507);
        tag(trs,"THEMED:PLUMERIA-STRONG", 89, 238, 401);
        tag(trs,"THEMED:SINA-STRONG", 75);
        tag(trs,"THEMED:DEXIO-STRONG", 74, 412, 623);
        tag(trs,"THEMED:FABA-STRONG", 132, 241, 410, 561);
        tag(trs,"THEMED:SOLIERA-STRONG", 498, 499, 648, 651);
        tag(trs,"THEMED:DULSE-STRONG", 500, 501, 649, 652);
        tag(trs,"THEMED:GUZMA-LEADER", 138, 235, 236, 558, 647);
        tag(trs,"THEMED:LUSAMINE-LEADER", 131, 644);

        tag(trs,"UBER", 541, 542, 543, 580, 572, 573, 559, 560, 562, 645); // RR Episode
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

    public static final HashMap<String, Type> gymAndEliteThemesSM = setupGymAndEliteThemesSM();

    private static HashMap<String, Type> setupGymAndEliteThemesSM() {
        HashMap<String, Type> themeMap = new HashMap<>();
        themeMap.put("ELITE1", Type.FIGHTING); //Hala
        themeMap.put("ELITE2", Type.ROCK); //Olivia
        themeMap.put("ELITE3", Type.DARK); //Nanu
        themeMap.put("ELITE4", Type.GROUND); //Hapu
        themeMap.put("ELITE5", Type.GHOST); //Acerola
        themeMap.put("ELITE6", Type.FLYING); //Kahili
        return themeMap;
    }

    public static final HashMap<String, Type> gymAndEliteThemesUSUM = setupGymAndEliteThemesUSUM();

    private static HashMap<String, Type> setupGymAndEliteThemesUSUM() {
        HashMap<String, Type> themeMap = new HashMap<>();
        themeMap.put("ELITE1", Type.FIGHTING); //Hala
        themeMap.put("ELITE2", Type.ROCK); //Olivia
        themeMap.put("ELITE3", Type.DARK); //Nanu
        themeMap.put("ELITE4", Type.GROUND); //Hapu
        themeMap.put("ELITE5", Type.STEEL); //Molayne
        themeMap.put("ELITE6", Type.GHOST); //Acerola
        themeMap.put("ELITE7", Type.FLYING); //Kahili
        return themeMap;
    }

    public static void setMultiBattleStatusSM(List<Trainer> trs) {
        // All Double Battles in Gen 7 are internally treated as a Multi Battle
        // 92 + 93: Rising Star Duo Justin and Lauren
        // 97 + 98: Twins Isa and Nico
        // 134 + 136: Aether Foundation Employees in Secret Lab B w/ Hau
        // 141 + 227: Team Skull Grunts on Route 17
        // 241 + 442: Faba and Aether Foundation Employee w/ Hau
        // 262 + 265: Ace Duo Aimee and Kent
        // 270 + 299: Swimmers Jake and Yumi
        // 278 + 280: Honeymooners Noriko and Devin
        // 303 + 307: Veteran Duo Tsunekazu and Nobuko
        // 315 + 316: Team Skull Grunts in Po Town
        // 331 + 332: Karate Family Guy and Samuel
        // 371 + 372: Twins Harper and Sarah
        // 373 + 374: Swimmer Girls Ashlyn and Kylie
        // 375 + 376: Golf Buddies Tara and Tina
        // 421 + 422: Athletic Siblings Alyssa and Sho
        // 425 + 426: Punk Pair Lane and Yoko
        // 429 + 430: Punk Pair Troy and Marie
        // 443 + 444: Team Skull Grunts in Diglett's Tunnel w/ Hau
        // 453 + 454: Aether Foundation Employees w/ Hau
        // 455 + 456: Aether Foundation Employees w/ Gladion
        setMultiBattleStatus(trs, 92, 93, 97, 98, 134, 136, 141, 227, 241, 262, 265, 270, 278, 280, 299, 303,
                307, 315, 316, 331, 332, 371, 372, 373, 374, 375, 376, 421, 422, 425, 426, 429, 430, 442, 443, 444, 453,
                454, 455, 456
        );
    }

    public static void setMultiBattleStatusUSUM(List<Trainer> trs) {
        // All Double Battles in Gen 7 are internally treated as a Multi Battle
        // 92 + 93: Rising Star Duo Justin and Lauren
        // 97 + 98: Twins Isa and Nico
        // 134 + 136: Aether Foundation Employees in Secret Lab B w/ Hau
        // 141 + 227: Team Skull Grunts on Route 17
        // 178 + 511: Capoeira Couple Cara and Douglas
        // 241 + 442: Faba and Aether Foundation Employee w/ Hau
        // 262 + 265: Ace Duo Aimee and Kent
        // 270 + 299: Swimmers Jake and Yumi
        // 278 + 280: Honeymooners Noriko and Devin
        // 303 + 307: Veteran Duo Tsunekazu and Nobuko
        // 315 + 316: Team Skull Grunts in Po Town
        // 331 + 332: Karate Family Guy and Samuel
        // 371 + 372: Twins Harper and Sarah
        // 373 + 374: Swimmer Girls Ashlyn and Kylie
        // 375 + 376: Golf Buddies Tara and Tina
        // 421 + 422: Athletic Siblings Alyssa and Sho
        // 425 + 426: Punk Pair Lane and Yoko
        // 429 + 430: Punk Pair Troy and Marie
        // 443 + 444: Team Skull Grunts in Diglett's Tunnel w/ Hau
        // 453 + 454: Aether Foundation Employees w/ Hau
        // 455 + 456: Aether Foundation Employees w/ Gladion
        // 514 + 521: Tourist Couple Yuriko and Landon
        // 515 + 534: Tourist Couple Steve and Reika
        // 529 + 530: Dancing Family Jen and Fumiko
        // 554 + 561: Aether Foundation Employee and Faba w/ Lillie
        // 557 + 578: GAME FREAK Iwao and Morimoto
        // 586 + 595: Team Rainbow Rocket Grunts w/ Guzma
        // 613 + 626: Master & Apprentice Kaimana and Breon
        // 617 + 618: Sparring Partners Allon and Eimar
        // 619 + 620: Sparring Partners Craig and Jason
        setMultiBattleStatus(trs, 92, 93, 97, 98, 134, 136, 141, 178, 227, 241, 262, 265, 270, 278, 280, 299,
                303, 307, 315, 316, 331, 332, 371, 372, 373, 374, 375, 376, 421, 422, 425, 426, 429, 430, 442, 443, 444,
                453, 454, 455, 456, 511, 514, 515, 521, 529, 530, 534, 544, 557, 561, 578, 586, 595, 613, 617, 618, 619,
                620, 626
        );
    }

    private static void setMultiBattleStatus(List<Trainer> allTrainers, int... numbers) {
        for (int num : numbers) {
            if (allTrainers.size() > (num - 1)) {
                allTrainers.get(num - 1).multiBattleStatus = Trainer.MultiBattleStatus.ALWAYS;
            }
        }
    }

    public static void setForcedRivalStarterPositionsUSUM(List<Trainer> allTrainers) {

        // Hau 3
        allTrainers.get(12 - 1).forceStarterPosition = 0;
        allTrainers.get(13 - 1).forceStarterPosition = 0;
        allTrainers.get(14 - 1).forceStarterPosition = 0;

        // Hau 6
        allTrainers.get(217 - 1).forceStarterPosition = 0;
        allTrainers.get(218 - 1).forceStarterPosition = 0;
        allTrainers.get(219 - 1).forceStarterPosition = 0;
    }

    public static final Map<Integer,Integer> balancedItemPrices = Stream.of(new Integer[][] {
            // Skip item index 0. All prices divided by 10
            {Items.masterBall, 300},
            {Items.ultraBall, 80},
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
            {Items.potion, 20},
            {Items.antidote, 20},
            {Items.burnHeal, 30},
            {Items.iceHeal, 10},
            {Items.awakening, 10},
            {Items.paralyzeHeal, 30},
            {Items.fullRestore, 300},
            {Items.maxPotion, 250},
            {Items.hyperPotion, 150},
            {Items.superPotion, 70},
            {Items.fullHeal, 40},
            {Items.revive, 200},
            {Items.maxRevive, 400},
            {Items.freshWater, 20},
            {Items.sodaPop, 30},
            {Items.lemonade, 40},
            {Items.moomooMilk, 60},
            {Items.energyPowder, 50},
            {Items.energyRoot, 120},
            {Items.healPowder, 30},
            {Items.revivalHerb, 280},
            {Items.ether, 300},
            {Items.maxEther, 450},
            {Items.elixir, 1500},
            {Items.maxElixir, 1800},
            {Items.lavaCookie, 35},
            {Items.berryJuice, 20},
            {Items.sacredAsh, 500},
            {Items.hpUp, 1000},
            {Items.protein, 1000},
            {Items.iron, 1000},
            {Items.carbos, 1000},
            {Items.calcium, 1000},
            {Items.rareCandy, 1000},
            {Items.ppUp, 1000},
            {Items.zinc, 1000},
            {Items.ppMax, 2500},
            {Items.oldGateau, 35},
            {Items.guardSpec, 150},
            {Items.direHit, 100},
            {Items.xAttack, 100},
            {Items.xDefense, 200},
            {Items.xSpeed, 100},
            {Items.xAccuracy, 100},
            {Items.xSpAtk, 100},
            {Items.xSpDef, 200},
            {Items.pokeDoll, 10},
            {Items.fluffyTail, 10},
            {Items.blueFlute, 2},
            {Items.yellowFlute, 2},
            {Items.redFlute, 2},
            {Items.blackFlute, 2},
            {Items.whiteFlute, 2},
            {Items.shoalSalt, 2},
            {Items.shoalShell, 2},
            {Items.redShard, 100},
            {Items.blueShard, 100},
            {Items.yellowShard, 100},
            {Items.greenShard, 100},
            {Items.superRepel, 70},
            {Items.maxRepel, 90},
            {Items.escapeRope, 100},
            {Items.repel, 40},
            {Items.sunStone, 300},
            {Items.moonStone, 300},
            {Items.fireStone, 300},
            {Items.thunderStone, 300},
            {Items.waterStone, 300},
            {Items.leafStone, 300},
            {Items.tinyMushroom, 50},
            {Items.bigMushroom, 500},
            {Items.pearl, 200},
            {Items.bigPearl, 800},
            {Items.stardust, 300},
            {Items.starPiece, 1200},
            {Items.nugget, 1000},
            {Items.heartScale, 500},
            {Items.honey, 30},
            {Items.growthMulch, 20},
            {Items.dampMulch, 20},
            {Items.stableMulch, 20},
            {Items.gooeyMulch, 20},
            {Items.rootFossil, 700},
            {Items.clawFossil, 700},
            {Items.helixFossil, 700},
            {Items.domeFossil, 700},
            {Items.oldAmber, 1000},
            {Items.armorFossil, 700},
            {Items.skullFossil, 700},
            {Items.rareBone, 500},
            {Items.shinyStone, 300},
            {Items.duskStone, 300},
            {Items.dawnStone, 300},
            {Items.ovalStone, 200},
            {Items.oddKeystone, 210},
            {Items.griseousOrb, 1000},
            {Items.tea, 0}, // unused in Gen 7
            {Items.unused114, 0},
            {Items.autograph, 0},
            {Items.douseDrive, 100},
            {Items.shockDrive, 100},
            {Items.burnDrive, 100},
            {Items.chillDrive, 100},
            {Items.unused120, 0},
            {Items.pokemonBox, 0}, // unused in Gen 7
            {Items.medicinePocket, 0}, // unused in Gen 7
            {Items.tmCase, 0}, // unused in Gen 7
            {Items.candyJar, 0}, // unused in Gen 7
            {Items.powerUpPocket, 0}, // unused in Gen 7
            {Items.clothingTrunk, 0}, // unused in Gen 7
            {Items.catchingPocket, 0}, // unused in Gen 7
            {Items.battlePocket, 0}, // unused in Gen 7
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
            {Items.brightPowder, 400},
            {Items.whiteHerb, 400},
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
            {Items.smokeBall, 400},
            {Items.everstone, 300},
            {Items.focusBand, 300},
            {Items.luckyEgg, 1000},
            {Items.scopeLens, 500},
            {Items.metalCoat, 300},
            {Items.leftovers, 1000},
            {Items.dragonScale, 300},
            {Items.lightBall, 100},
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
            {Items.luckyPunch, 100},
            {Items.metalPowder, 100},
            {Items.thickClub, 100},
            {Items.leek, 100},
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
            {Items.quickPowder, 100},
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
            {Items.tm16, 1000},
            {Items.tm17, 1000},
            {Items.tm18, 2000},
            {Items.tm19, 1000},
            {Items.tm20, 1000},
            {Items.tm21, 1000},
            {Items.tm22, 1000},
            {Items.tm23, 1000},
            {Items.tm24, 1000},
            {Items.tm25, 2000},
            {Items.tm26, 1000},
            {Items.tm27, 1000},
            {Items.tm28, 2000},
            {Items.tm29, 1000},
            {Items.tm30, 1000},
            {Items.tm31, 1000},
            {Items.tm32, 1000},
            {Items.tm33, 1000},
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
            {Items.tm50, 2000},
            {Items.tm51, 1000},
            {Items.tm52, 2000},
            {Items.tm53, 1000},
            {Items.tm54, 1000},
            {Items.tm55, 1000},
            {Items.tm56, 1000},
            {Items.tm57, 1000},
            {Items.tm58, 1000},
            {Items.tm59, 2000},
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
            {Items.tm70, 2000},
            {Items.tm71, 2000},
            {Items.tm72, 1000},
            {Items.tm73, 500},
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
            {Items.hm07, 0}, // unused in Gen 7
            {Items.hm08, 0}, // unused in Gen 7
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
            {Items.rageCandyBar, 35},
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
            {Items.healthFeather, 30},
            {Items.muscleFeather, 30},
            {Items.resistFeather, 30},
            {Items.geniusFeather, 30},
            {Items.cleverFeather, 30},
            {Items.swiftFeather, 30},
            {Items.prettyFeather, 100},
            {Items.coverFossil, 700},
            {Items.plumeFossil, 700},
            {Items.libertyPass, 0},
            {Items.passOrb, 20},
            {Items.dreamBall, 100},
            {Items.pokeToy, 10},
            {Items.propCase, 0},
            {Items.dragonSkull, 0},
            {Items.balmMushroom, 1500},
            {Items.bigNugget, 4000},
            {Items.pearlString, 3000},
            {Items.cometShard, 6000},
            {Items.relicCopper, 0},
            {Items.relicSilver, 0},
            {Items.relicGold, 0},
            {Items.relicVase, 0},
            {Items.relicBand, 0},
            {Items.relicStatue, 0},
            {Items.relicCrown, 0},
            {Items.casteliacone, 35},
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
            {Items.tm93, 2000},
            {Items.tm94, 2000},
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
            {Items.tm98, 2000},
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
            {Items.strangeSouvenir, 300},
            {Items.lensCase, 0},
            {Items.makeupBag, 0},
            {Items.travelTrunk, 0},
            {Items.lumioseGalette, 35},
            {Items.shalourSable, 35},
            {Items.jawFossil, 700},
            {Items.sailFossil, 700},
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
            {Items.normaliumZHeld, 0},
            {Items.firiumZHeld, 0},
            {Items.wateriumZHeld, 0},
            {Items.electriumZHeld, 0},
            {Items.grassiumZHeld, 0},
            {Items.iciumZHeld, 0},
            {Items.fightiniumZHeld, 0},
            {Items.poisoniumZHeld, 0},
            {Items.groundiumZHeld, 0},
            {Items.flyiniumZHeld, 0},
            {Items.psychiumZHeld, 0},
            {Items.buginiumZHeld, 0},
            {Items.rockiumZHeld, 0},
            {Items.ghostiumZHeld, 0},
            {Items.dragoniumZHeld, 0},
            {Items.darkiniumZHeld, 0},
            {Items.steeliumZHeld, 0},
            {Items.fairiumZHeld, 0},
            {Items.pikaniumZHeld, 0},
            {Items.bottleCap, 500},
            {Items.goldBottleCap, 1000},
            {Items.zRing, 0},
            {Items.decidiumZHeld, 0},
            {Items.inciniumZHeld, 0},
            {Items.primariumZHeld, 0},
            {Items.tapuniumZHeld, 0},
            {Items.marshadiumZHeld, 0},
            {Items.aloraichiumZHeld, 0},
            {Items.snorliumZHeld, 0},
            {Items.eeviumZHeld, 0},
            {Items.mewniumZHeld, 0},
            {Items.normaliumZBag, 0},
            {Items.firiumZBag, 0},
            {Items.wateriumZBag, 0},
            {Items.electriumZBag, 0},
            {Items.grassiumZBag, 0},
            {Items.iciumZBag, 0},
            {Items.fightiniumZBag, 0},
            {Items.poisoniumZBag, 0},
            {Items.groundiumZBag, 0},
            {Items.flyiniumZBag, 0},
            {Items.psychiumZBag, 0},
            {Items.buginiumZBag, 0},
            {Items.rockiumZBag, 0},
            {Items.ghostiumZBag, 0},
            {Items.dragoniumZBag, 0},
            {Items.darkiniumZBag, 0},
            {Items.steeliumZBag, 0},
            {Items.fairiumZBag, 0},
            {Items.pikaniumZBag, 0},
            {Items.decidiumZBag, 0},
            {Items.inciniumZBag, 0},
            {Items.primariumZBag, 0},
            {Items.tapuniumZBag, 0},
            {Items.marshadiumZBag, 0},
            {Items.aloraichiumZBag, 0},
            {Items.snorliumZBag, 0},
            {Items.eeviumZBag, 0},
            {Items.mewniumZBag, 0},
            {Items.pikashuniumZHeld, 0},
            {Items.pikashuniumZBag, 0},
            {Items.unused837, 0},
            {Items.unused838, 0},
            {Items.unused839, 0},
            {Items.unused840, 0},
            {Items.forageBag, 0},
            {Items.fishingRod, 0},
            {Items.professorsMask, 0},
            {Items.festivalTicket, 1},
            {Items.sparklingStone, 0},
            {Items.adrenalineOrb, 30},
            {Items.zygardeCube, 0},
            {Items.unused848, 0},
            {Items.iceStone, 300},
            {Items.ridePager, 0},
            {Items.beastBall, 30},
            {Items.bigMalasada, 35},
            {Items.redNectar, 30},
            {Items.yellowNectar, 30},
            {Items.pinkNectar, 30},
            {Items.purpleNectar, 30},
            {Items.sunFlute, 0},
            {Items.moonFlute, 0},
            {Items.unused859, 0},
            {Items.enigmaticCard, 0},
            {Items.silverRazzBerry, 0}, // unused in Gen 7
            {Items.goldenRazzBerry, 0}, // unused in Gen 7
            {Items.silverNanabBerry, 0}, // unused in Gen 7
            {Items.goldenNanabBerry, 0}, // unused in Gen 7
            {Items.silverPinapBerry, 0}, // unused in Gen 7
            {Items.goldenPinapBerry, 0}, // unused in Gen 7
            {Items.unused867, 0},
            {Items.unused868, 0},
            {Items.unused869, 0},
            {Items.unused870, 0},
            {Items.unused871, 0},
            {Items.secretKeyKanto, 0}, // unused in Gen 7
            {Items.ssTicketKanto, 0}, // unused in Gen 7
            {Items.silphScope, 0}, // unused in Gen 7
            {Items.parcelKanto, 0}, // unused in Gen 7
            {Items.cardKeyKanto, 0}, // unused in Gen 7
            {Items.goldTeeth, 0}, // unused in Gen 7
            {Items.liftKey, 0}, // unused in Gen 7
            {Items.terrainExtender, 400},
            {Items.protectivePads, 300},
            {Items.electricSeed, 100},
            {Items.psychicSeed, 100},
            {Items.mistySeed, 100},
            {Items.grassySeed, 100},
            {Items.stretchySpring, 0}, // unused in Gen 7
            {Items.chalkyStone, 0}, // unused in Gen 7
            {Items.marble, 0}, // unused in Gen 7
            {Items.loneEarring, 0}, // unused in Gen 7
            {Items.beachGlass, 0}, // unused in Gen 7
            {Items.goldLeaf, 0}, // unused in Gen 7
            {Items.silverLeaf, 0}, // unused in Gen 7
            {Items.polishedMudBall, 0}, // unused in Gen 7
            {Items.tropicalShell, 0}, // unused in Gen 7
            {Items.leafLetterPikachu, 0}, // unused in Gen 7
            {Items.leafLetterEevee, 0}, // unused in Gen 7
            {Items.smallBouquet, 0}, // unused in Gen 7
            {Items.unused897, 0},
            {Items.unused898, 0},
            {Items.unused899, 0},
            {Items.lure, 0}, // unused in Gen 7
            {Items.superLure, 0}, // unused in Gen 7
            {Items.maxLure, 0}, // unused in Gen 7
            {Items.pewterCrunchies, 0}, // unused in Gen 7
            {Items.fightingMemory, 100},
            {Items.flyingMemory, 100},
            {Items.poisonMemory, 100},
            {Items.groundMemory, 100},
            {Items.rockMemory, 100},
            {Items.bugMemory, 100},
            {Items.ghostMemory, 100},
            {Items.steelMemory, 100},
            {Items.fireMemory, 100},
            {Items.waterMemory, 100},
            {Items.grassMemory, 100},
            {Items.electricMemory, 100},
            {Items.psychicMemory, 100},
            {Items.iceMemory, 100},
            {Items.dragonMemory, 100},
            {Items.darkMemory, 100},
            {Items.fairyMemory, 100},
            {Items.solganiumZBag, 0},
            {Items.lunaliumZBag, 0},
            {Items.ultranecroziumZBag, 0},
            {Items.mimikiumZHeld, 0},
            {Items.lycaniumZHeld, 0},
            {Items.kommoniumZHeld, 0},
            {Items.solganiumZHeld, 0},
            {Items.lunaliumZHeld, 0},
            {Items.ultranecroziumZHeld, 0},
            {Items.mimikiumZBag, 0},
            {Items.lycaniumZBag, 0},
            {Items.kommoniumZBag, 0},
            {Items.zPowerRing, 0},
            {Items.pinkPetal, 0},
            {Items.orangePetal, 0},
            {Items.bluePetal, 0},
            {Items.redPetal, 0},
            {Items.greenPetal, 0},
            {Items.yellowPetal, 0},
            {Items.purplePetal, 0},
            {Items.rainbowFlower, 0},
            {Items.surgeBadge, 0},
            {Items.nSolarizerFuse, 0},
            {Items.nLunarizerFuse, 0},
            {Items.nSolarizerSeparate, 0},
            {Items.nLunarizerSeparate, 0},
            {Items.ilimaNormaliumZ, 0},
            {Items.leftPokeBall, 0},
            {Items.rotoHatch, 0},
            {Items.rotoBargain, 0},
            {Items.rotoPrizeMoney, 0},
            {Items.rotoExpPoints, 0},
            {Items.rotoFriendship, 0},
            {Items.rotoEncounter, 0},
            {Items.rotoStealth, 0},
            {Items.rotoHPRestore, 0},
            {Items.rotoPPRestore, 0},
            {Items.rotoBoost, 0},
            {Items.rotoCatch, 0},
    }).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
}
