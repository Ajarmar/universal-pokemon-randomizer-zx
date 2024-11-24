package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  Gen3Constants.java - Constants for Ruby/Sapphire/FR/LG/Emerald        --*/
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
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.Type;

public class Gen3Constants {

    public static final int RomType_Ruby = 0;
    public static final int RomType_Sapp = 1;
    public static final int RomType_Em = 2;
    public static final int RomType_FRLG = 3;

    public static final int size8M = 0x800000, size16M = 0x1000000, size32M = 0x2000000;

    public static final String unofficialEmeraldROMName = "YJencrypted";

    public static final int romNameOffset = 0xA0, romCodeOffset = 0xAC, romVersionOffset = 0xBC,
            headerChecksumOffset = 0xBD;

    public static final int pokemonCount = 386;

    public static final String wildPokemonPointerPrefix = "0348048009E00000FFFF0000";

    public static final String mapBanksPointerPrefix = "80180068890B091808687047";

    public static final String rsPokemonNamesPointerSuffix = "30B50025084CC8F7";

    public static final String frlgMapLabelsPointerPrefix = "AC470000AE470000B0470000";

    public static final String rseMapLabelsPointerPrefix = "C078288030BC01BC00470000";

    public static final String pokedexOrderPointerPrefix = "0448814208D0481C0004000C05E00000";

    public static final String rsFrontSpritesPointerPrefix = "05E0";

    public static final String rsFrontSpritesPointerSuffix = "1068191C";

    public static final String rsPokemonPalettesPointerPrefix = "04D90148006817E0";

    public static final String rsPokemonPalettesPointerSuffix = "080C064A11404840";

    private static final String runningShoesCheckPrefixRS = "0440002C1DD08620", runningShoesCheckPrefixFRLG = "02200540002D29D0",
            runningShoesCheckPrefixE = "0640002E1BD08C20";

    public static final int efrlgPokemonNamesPointer = 0x144, efrlgMoveNamesPointer = 0x148,
            efrlgAbilityNamesPointer = 0x1C0, efrlgItemDataPointer = 0x1C8, efrlgMoveDataPointer = 0x1CC,
            efrlgPokemonStatsPointer = 0x1BC, efrlgFrontSpritesPointer = 0x128, efrlgPokemonPalettesPointer = 0x130;

    public static final byte[] emptyPokemonSig = new byte[] { 0x32, (byte) 0x96, 0x32, (byte) 0x96, (byte) 0x96, 0x32,
            0x00, 0x00, 0x03, 0x01, (byte) 0xAA, 0x0A, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF, 0x78, 0x00, 0x00, 0x0F,
            0x0F, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00 };

    public static final int baseStatsEntrySize = 0x1C;

    public static final int bsHPOffset = 0, bsAttackOffset = 1, bsDefenseOffset = 2, bsSpeedOffset = 3,
            bsSpAtkOffset = 4, bsSpDefOffset = 5, bsPrimaryTypeOffset = 6, bsSecondaryTypeOffset = 7,
            bsCatchRateOffset = 8, bsCommonHeldItemOffset = 12, bsRareHeldItemOffset = 14, bsGenderRatioOffset = 16,
            bsGrowthCurveOffset = 19, bsAbility1Offset = 22, bsAbility2Offset = 23;

    public static final int textTerminator = 0xFF, textVariable = 0xFD;

    public static final byte freeSpaceByte = (byte) 0xFF;

    public static final int rseStarter2Offset = 2, rseStarter3Offset = 4, frlgStarter2Offset = 515,
            frlgStarter3Offset = 461, frlgStarterRepeatOffset = 5;

    public static final int frlgBaseStarter1 = 1, frlgBaseStarter2 = 4, frlgBaseStarter3 = 7;

    public static final int frlgStarterItemsOffset = 218;

    public static final int gbaAddRxOpcode = 0x30, gbaUnconditionalJumpOpcode = 0xE0, gbaSetRxOpcode = 0x20,
            gbaCmpRxOpcode = 0x28, gbaNopOpcode = 0x46C0;

    public static final int gbaR0 = 0, gbaR1 = 1, gbaR2 = 2, gbaR3 = 3, gbaR4 = 4, gbaR5 = 5, gbaR6 = 6, gbaR7 = 7;

    public static final Type[] typeTable = constructTypeTable();

    public static final int grassSlots = 12, surfingSlots = 5, rockSmashSlots = 5, fishingSlots = 10;

    public static final int tmCount = 50, hmCount = 8;

    public static final List<Integer> hmMoves = Arrays.asList(
            Moves.cut, Moves.fly, Moves.surf, Moves.strength, Moves.flash, Moves.rockSmash, Moves.waterfall, Moves.dive);

    public static final int tmItemOffset = Gen3Items.tm01;

    public static final int rseItemDescCharsPerLine = 18, frlgItemDescCharsPerLine = 24;

    public static final int regularTextboxCharsPerLine = 36;

    public static final int pointerSearchRadius = 500;

    public static final int itemDataDescriptionOffset = 0x14;

    public static final String deoxysObeyCode = "CD21490088420FD0";

    public static final int mewObeyOffsetFromDeoxysObey = 0x16;

    public static final String levelEvoKantoDexCheckCode = "972814DD";

    public static final String stoneEvoKantoDexCheckCode = "972808D9";

    public static final int levelEvoKantoDexJumpAmount = 0x14, stoneEvoKantoDexJumpAmount = 0x08;

    public static final String rsPokedexScriptIdentifier = "326629010803";

    public static final String rsNatDexScriptPart1 = "31720167";

    public static final String rsNatDexScriptPart2 = "32662901082B00801102006B02021103016B020211DABE4E020211675A6A02022A008003";

    public static final String frlgPokedexScriptIdentifier = "292908258101";

    public static final String frlgNatDexScript = "292908258101256F0103";

    public static final String frlgNatDexFlagChecker = "260D809301210D800100";

    public static final String frlgE4FlagChecker = "2B2C0800000000000000";

    public static final String frlgOaksLabKantoDexChecker = "257D011604800000260D80D400";

    public static final String frlgOaksLabFix = "257D011604800100";

    public static final String frlgOakOutsideHouseCheck = "1604800000260D80D4001908800580190980068083000880830109802109803C";

    public static final String frlgOakOutsideHouseFix = "1604800100";

    public static final String frlgOakAideCheckPrefix = "00B5064800880028";

    public static final String ePokedexScriptIdentifier = "3229610825F00129E40816CD40010003";

    public static final String eNatDexScriptPart1 = "31720167";

    public static final String eNatDexScriptPart2 = "3229610825F00129E40825F30116CD40010003";

    public static final String friendshipValueForEvoLocator = "DB2900D8";

    public static final String perfectOddsBranchLocator = "FE2E2FD90020";

    public static final int unhackedMaxPokedex = 411, unhackedRealPokedex = 386, hoennPokesStart = 252;

    public static final int evolutionMethodCount = 15;

    public static final int cacophonyIndex = 76, airLockIndex = 77, highestAbilityIndex = 77;

    public static final int emMeteorFallsStevenIndex = 804;

    public static final Map<Integer,List<Integer>> abilityVariations = setupAbilityVariations();

    private static Map<Integer,List<Integer>> setupAbilityVariations() {
        Map<Integer,List<Integer>> map = new HashMap<>();
        map.put(Abilities.insomnia, Arrays.asList(Abilities.insomnia, Abilities.vitalSpirit));
        map.put(Abilities.clearBody, Arrays.asList(Abilities.clearBody, Abilities.whiteSmoke));
        map.put(Abilities.hugePower, Arrays.asList(Abilities.hugePower, Abilities.purePower));
        map.put(Abilities.battleArmor, Arrays.asList(Abilities.battleArmor, Abilities.shellArmor));
        map.put(Abilities.cloudNine, Arrays.asList(Abilities.cloudNine, Gen3Constants.airLockIndex));

        return map;
    }

    public static final List<Integer> uselessAbilities = Arrays.asList(Abilities.forecast, Gen3Constants.cacophonyIndex);

    public static final int frlgMapLabelsStart = 0x58;

    public static final int noDamageSleepEffect = 1, damagePoisonEffect = 2, damageAbsorbEffect = 3, damageBurnEffect = 4,
            damageFreezeEffect = 5, damageParalyzeEffect = 6, dreamEaterEffect = 8, noDamageAtkPlusOneEffect = 10,
            noDamageDefPlusOneEffect = 11, noDamageSpAtkPlusOneEffect = 13, noDamageEvasionPlusOneEffect = 16,
            noDamageAtkMinusOneEffect = 18, noDamageDefMinusOneEffect = 19, noDamageSpeMinusOneEffect = 20,
            noDamageAccuracyMinusOneEffect = 23, noDamageEvasionMinusOneEffect = 24, flinchEffect = 31, toxicEffect = 33,
            razorWindEffect = 39, bindingEffect = 42, increasedCritEffect = 43, damageRecoil25PercentEffect = 48,
            noDamageConfusionEffect = 49, noDamageAtkPlusTwoEffect = 50, noDamageDefPlusTwoEffect = 51,
            noDamageSpePlusTwoEffect = 52, noDamageSpAtkPlusTwoEffect = 53, noDamageSpDefPlusTwoEffect = 54,
            noDamageAtkMinusTwoEffect = 58, noDamageDefMinusTwoEffect = 59, noDamageSpeMinusTwoEffect = 60,
            noDamageSpDefMinusTwoEffect = 62, noDamagePoisonEffect = 66, noDamageParalyzeEffect = 67,
            damageAtkMinusOneEffect = 68, damageDefMinusOneEffect = 69, damageSpeMinusOneEffect = 70,
            damageSpAtkMinusOneEffect = 71, damageSpDefMinusOneEffect = 72, damageAccuracyMinusOneEffect = 73,
            skyAttackEffect = 75, damageConfusionEffect = 76, twineedleEffect = 77, rechargeEffect = 80,
            snoreEffect = 92, trappingEffect = 106, minimizeEffect = 108, swaggerEffect = 118,
            damageBurnAndThawUserEffect = 125, damageUserDefPlusOneEffect = 138, damageUserAtkPlusOneEffect = 139,
            damageUserAllPlusOneEffect = 140, skullBashEffect = 145, twisterEffect = 146,
            futureSightAndDoomDesireEffect = 148, flinchWithMinimizeBonusEffect = 150, solarbeamEffect = 151,
            thunderEffect = 152, semiInvulnerableEffect = 155, defenseCurlEffect = 156, fakeOutEffect = 158,
            spitUpEffect = 161, flatterEffect = 166, noDamageBurnEffect = 167, chargeEffect = 174,
            damageUserAtkAndDefMinusOneEffect = 182, damageRecoil33PercentEffect = 198, teeterDanceEffect = 199,
            blazeKickEffect = 200, poisonFangEffect = 202, damageUserSpAtkMinusTwoEffect = 204,
            noDamageAtkAndDefMinusOneEffect = 205, noDamageDefAndSpDefPlusOneEffect = 206,
            noDamageAtkAndDefPlusOneEffect = 208, poisonTailEffect = 209, noDamageSpAtkAndSpDefPlusOneEffect = 211,
            noDamageAtkAndSpePlusOneEffect = 212;

    public static final List<Integer> soundMoves = Arrays.asList(Moves.growl, Moves.roar, Moves.sing, Moves.supersonic,
            Moves.screech, Moves.snore, Moves.uproar, Moves.metalSound, Moves.grassWhistle, Moves.hyperVoice,
            Moves.perishSong, Moves.healBell);

    public static final List<Integer> rsRequiredFieldTMs = Arrays.asList(1, 2, 6, 7, 11, 18, 22, 23,
            26, 30, 37, 48);

    public static final List<Integer> eRequiredFieldTMs = Arrays.asList(2, 6, 7, 11, 18, 22, 23, 30,
            37, 48);

    public static final List<Integer> frlgRequiredFieldTMs = Arrays.asList(1, 2, 7, 8, 9, 11, 12, 14,
            17, 18, 21, 22, 25, 32, 36, 37, 40, 41, 44, 46, 47, 48, 49, 50);

    public static final List<Integer> rseFieldMoves = Arrays.asList(
            Moves.cut, Moves.fly, Moves.surf, Moves.strength, Moves.flash, Moves.dig, Moves.teleport,
            Moves.waterfall, Moves.rockSmash, Moves.sweetScent, Moves.dive, Moves.secretPower);

    public static final List<Integer> frlgFieldMoves = Arrays.asList(
            Moves.cut, Moves.fly, Moves.surf, Moves.strength, Moves.flash, Moves.dig, Moves.teleport,
            Moves.waterfall, Moves.rockSmash, Moves.sweetScent);

    public static final List<Integer> rseEarlyRequiredHMMoves = Collections.singletonList(Moves.rockSmash);

    public static final List<Integer> frlgEarlyRequiredHMMoves = Collections.singletonList(Moves.cut);

    private static List<String> rsShopNames = Arrays.asList(
            "Slateport Vitamins",
            "Slateport TMs",
            "Oldale Poké Mart (Before Pokédex)",
            "Oldale Poké Mart (After Pokédex)",
            "Lavaridge Herbs",
            "Lavaridge Poké Mart",
            "Fallarbor Poké Mart",
            "Verdanturf Poké Mart",
            "Petalburg Poké Mart (Before 4 Badges)",
            "Petalburg Poké Mart (After 4 Badges)",
            "Slateport Poké Mart",
            "Mauville Poké Mart",
            "Rustboro Poké Mart (Before Delivering Devon Goods)",
            "Rustboro Poké Mart (After Delivering Devon Goods)",
            "Fortree Poké Mart",
            "Lilycove Department Store 2F Left",
            "Lilycove Department Store 2F Right",
            "Lilycove Department Store 3F Left",
            "Lilycove Department Store 3F Right",
            "Lilycove Department Store 4F Left (TMs)",
            "Lilycove Department Store 4F Right (TMs)",
            "Mossdeep Poké Mart",
            "Sootopolis Poké Mart",
            "Pokémon League Poké Mart"
    );

    private static List<String> frlgShopNames = Arrays.asList(
            "Trainer Tower Poké Mart",
            "Two Island Market Stall (Initial)",
            "Two Island Market Stall (After Saving Lostelle)",
            "Two Island Market Stall (After Hall of Fame)",
            "Two Island Market Stall (After Ruby/Sapphire Quest)",
            "Viridian Poké Mart",
            "Pewter Poké Mart",
            "Cerulean Poké Mart",
            "Lavender Poké Mart",
            "Vermillion Poké Mart",
            "Celadon Department 2F South",
            "Celadon Department 2F North (TMs)",
            "Celadon Department 4F",
            "Celadon Department 5F South",
            "Celadon Department 5F North",
            "Fuchsia Poké Mart",
            "Cinnabar Poké Mart",
            "Indigo Plateau Poké Mart",
            "Saffron Poké Mart",
            "Seven Island Poké Mart",
            "Three Island Poké Mart",
            "Four Island Poké Mart",
            "Six Island Poké Mart"
    );

    private static List<String> emShopNames = Arrays.asList(
            "Slateport Vitamins",
            "Slateport TMs",
            "Oldale Poké Mart (Before Pokédex)",
            "Oldale Poké Mart (After Pokédex)",
            "Lavaridge Herbs",
            "Lavaridge Poké Mart",
            "Fallarbor Poké Mart",
            "Verdanturf Poké Mart",
            "Petalburg Poké Mart (Before 4 Badges)",
            "Petalburg Poké Mart (After 4 Badges)",
            "Slateport Poké Mart",
            "Mauville Poké Mart",
            "Rustboro Poké Mart (Before Delivering Devon Goods)",
            "Rustboro Poké Mart (After Delivering Devon Goods)",
            "Fortree Poké Mart",
            "Lilycove Department Store 2F Left",
            "Lilycove Department Store 2F Right",
            "Lilycove Department Store 3F Left",
            "Lilycove Department Store 3F Right",
            "Lilycove Department Store 4F Left (TMs)",
            "Lilycove Department Store 4F Right (TMs)",
            "Mossdeep Poké Mart",
            "Sootopolis Poké Mart",
            "Pokémon League Poké Mart",
            "Battle Frontier Poké Mart",
            "Trainer Hill Poké Mart (Before Hall of Fame)",
            "Trainer Hill Poké Mart (After Hall of Fame)"
    );

    public static List<String> getShopNames(int romType) {
        if (romType == RomType_Ruby || romType == RomType_Sapp) {
            return rsShopNames;
        } else if (romType == RomType_FRLG) {
            return frlgShopNames;
        } else if (romType == RomType_Em) {
            return emShopNames;
        }
        return null;
    }

    public static final List<Integer> evolutionItems = Arrays.asList(Gen3Items.sunStone, Gen3Items.moonStone,
            Gen3Items.fireStone, Gen3Items.thunderstone, Gen3Items.waterStone, Gen3Items.leafStone);

    public static final List<Integer> xItems = Arrays.asList(Gen3Items.guardSpec, Gen3Items.direHit, Gen3Items.xAttack,
            Gen3Items.xDefend, Gen3Items.xSpeed, Gen3Items.xAccuracy, Gen3Items.xSpecial);

    public static final List<Integer> consumableHeldItems = Collections.unmodifiableList(Arrays.asList(
            Gen3Items.cheriBerry, Gen3Items.chestoBerry, Gen3Items.pechaBerry, Gen3Items.rawstBerry,
            Gen3Items.rawstBerry, Gen3Items.leppaBerry, Gen3Items.oranBerry, Gen3Items.persimBerry, Gen3Items.lumBerry,
            Gen3Items.sitrusBerry, Gen3Items.figyBerry, Gen3Items.wikiBerry, Gen3Items.magoBerry, Gen3Items.aguavBerry,
            Gen3Items.iapapaBerry, Gen3Items.liechiBerry, Gen3Items.ganlonBerry, Gen3Items.salacBerry,
            Gen3Items.petayaBerry, Gen3Items.apicotBerry, Gen3Items.lansatBerry, Gen3Items.starfBerry,
            Gen3Items.berryJuice, Gen3Items.whiteHerb, Gen3Items.mentalHerb));

    public static final List<Integer> allHeldItems = setupAllHeldItems();

    private static List<Integer> setupAllHeldItems() {
        List<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(Gen3Items.brightPowder, Gen3Items.quickClaw, Gen3Items.choiceBand,
                Gen3Items.kingsRock, Gen3Items.silverPowder, Gen3Items.focusBand, Gen3Items.scopeLens,
                Gen3Items.metalCoat, Gen3Items.leftovers, Gen3Items.softSand, Gen3Items.hardStone,
                Gen3Items.miracleSeed, Gen3Items.blackGlasses, Gen3Items.blackBelt, Gen3Items.magnet,
                Gen3Items.mysticWater, Gen3Items.sharpBeak, Gen3Items.poisonBarb, Gen3Items.neverMeltIce,
                Gen3Items.spellTag, Gen3Items.twistedSpoon, Gen3Items.charcoal, Gen3Items.dragonFang,
                Gen3Items.silkScarf, Gen3Items.shellBell, Gen3Items.seaIncense, Gen3Items.laxIncense));
        list.addAll(consumableHeldItems);
        return Collections.unmodifiableList(list);
    }

    public static final List<Integer> generalPurposeConsumableItems = Collections.unmodifiableList(Arrays.asList(
            Gen3Items.cheriBerry, Gen3Items.chestoBerry, Gen3Items.pechaBerry, Gen3Items.rawstBerry,
            Gen3Items.aspearBerry, Gen3Items.leppaBerry, Gen3Items.oranBerry, Gen3Items.persimBerry, Gen3Items.lumBerry,
            Gen3Items.sitrusBerry, Gen3Items.ganlonBerry, Gen3Items.salacBerry,
            // An NPC pokemon's nature is generated randomly with IVs during gameplay. Therefore, we do not include
            // the flavor berries because, prior to Gen 7, they aren't worth the risk.
            Gen3Items.apicotBerry, Gen3Items.lansatBerry, Gen3Items.starfBerry, Gen3Items.berryJuice,
            Gen3Items.whiteHerb, Gen3Items.mentalHerb
    ));

    public static final List<Integer> generalPurposeItems = Collections.unmodifiableList(Arrays.asList(
            Gen3Items.brightPowder, Gen3Items.quickClaw, Gen3Items.kingsRock, Gen3Items.focusBand, Gen3Items.scopeLens,
            Gen3Items.leftovers, Gen3Items.shellBell, Gen3Items.laxIncense
    ));

    public static final Map<Type, List<Integer>> typeBoostingItems = initializeTypeBoostingItems();

    private static Map<Type, List<Integer>> initializeTypeBoostingItems() {
        Map<Type, List<Integer>> map = new HashMap<>();
        map.put(Type.BUG, Arrays.asList(Gen3Items.silverPowder));
        map.put(Type.DARK, Arrays.asList(Gen3Items.blackGlasses));
        map.put(Type.DRAGON, Arrays.asList(Gen3Items.dragonFang));
        map.put(Type.ELECTRIC, Arrays.asList(Gen3Items.magnet));
        map.put(Type.FIGHTING, Arrays.asList(Gen3Items.blackBelt));
        map.put(Type.FIRE, Arrays.asList(Gen3Items.charcoal));
        map.put(Type.FLYING, Arrays.asList(Gen3Items.sharpBeak));
        map.put(Type.GHOST, Arrays.asList(Gen3Items.spellTag));
        map.put(Type.GRASS, Arrays.asList(Gen3Items.miracleSeed));
        map.put(Type.GROUND, Arrays.asList(Gen3Items.softSand));
        map.put(Type.ICE, Arrays.asList(Gen3Items.neverMeltIce));
        map.put(Type.NORMAL, Arrays.asList(Gen3Items.silkScarf));
        map.put(Type.POISON, Arrays.asList(Gen3Items.poisonBarb));
        map.put(Type.PSYCHIC, Arrays.asList(Gen3Items.twistedSpoon));
        map.put(Type.ROCK, Arrays.asList(Gen3Items.hardStone));
        map.put(Type.STEEL, Arrays.asList(Gen3Items.metalCoat));
        map.put(Type.WATER, Arrays.asList(Gen3Items.mysticWater, Gen3Items.seaIncense));
        map.put(null, Collections.emptyList()); // ??? type
        return Collections.unmodifiableMap(map);
    }

    public static final Map<Integer, List<Integer>> speciesBoostingItems = initializeSpeciesBoostingItems();

    private static Map<Integer, List<Integer>> initializeSpeciesBoostingItems() {
        Map<Integer, List<Integer>> map = new HashMap<>();
        map.put(Species.latias, Arrays.asList(Gen3Items.soulDew));
        map.put(Species.latios, Arrays.asList(Gen3Items.soulDew));
        map.put(Species.clamperl, Arrays.asList(Gen3Items.deepSeaTooth, Gen3Items.deepSeaScale));
        map.put(Species.pikachu, Arrays.asList(Gen3Items.lightBall));
        map.put(Species.chansey, Arrays.asList(Gen3Items.luckyPunch));
        map.put(Species.ditto, Arrays.asList(Gen3Items.metalPowder));
        map.put(Species.cubone, Arrays.asList(Gen3Items.thickClub));
        map.put(Species.marowak, Arrays.asList(Gen3Items.thickClub));
        map.put(Species.farfetchd, Arrays.asList(Gen3Items.stick));
        return Collections.unmodifiableMap(map);
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

    public static ItemList allowedItems, nonBadItemsRSE, nonBadItemsFRLG;
    public static List<Integer> regularShopItems, opShopItems;

    public static String getRunningShoesCheckPrefix(int romType) {
        if (romType == Gen3Constants.RomType_Ruby || romType == Gen3Constants.RomType_Sapp) {
            return runningShoesCheckPrefixRS;
        } else if (romType == Gen3Constants.RomType_FRLG) {
            return runningShoesCheckPrefixFRLG;
        } else {
            return runningShoesCheckPrefixE;
        }
    }

    static {
        setupAllowedItems();
    }

    private static void setupAllowedItems() {
        allowedItems = new ItemList(Gen3Items.oldSeaMap);
        // Key items (+1 unknown item)
        allowedItems.banRange(Gen3Items.machBike, 30);
        allowedItems.banRange(Gen3Items.oaksParcel, 28);
        // Unknown blank items
        allowedItems.banRange(Gen3Items.unknown52, 11);
        allowedItems.banRange(Gen3Items.unknown87, 6);
        allowedItems.banRange(Gen3Items.unknown99, 4);
        allowedItems.banRange(Gen3Items.unknown112, 9);
        allowedItems.banRange(Gen3Items.unknown176, 3);
        allowedItems.banRange(Gen3Items.unknown226, 28);
        allowedItems.banRange(Gen3Items.unknown347, 2);
        allowedItems.banSingles(Gen3Items.unknown72, Gen3Items.unknown82, Gen3Items.unknown105, Gen3Items.unknown267);
        // HMs
        allowedItems.banRange(Gen3Items.hm01, 8);
        // TMs
        allowedItems.tmRange(Gen3Items.tm01, 50);

        // non-bad items
        // ban specific pokemon hold items, berries, apricorns, mail
        nonBadItemsRSE = allowedItems.copy();
        nonBadItemsRSE.banSingles(Gen3Items.lightBall, Gen3Items.oranBerry, Gen3Items.soulDew);
        nonBadItemsRSE.banRange(Gen3Items.orangeMail, 12); // mail
        nonBadItemsRSE.banRange(Gen3Items.figyBerry, 33); // berries
        nonBadItemsRSE.banRange(Gen3Items.luckyPunch, 4); // pokemon specific
        nonBadItemsRSE.banRange(Gen3Items.redScarf, 5); // contest scarves

        // FRLG-exclusive bad items
        // Ban Shoal items and Shards, since they don't do anything
        nonBadItemsFRLG = nonBadItemsRSE.copy();
        nonBadItemsFRLG.banRange(Gen3Items.shoalSalt, 6);

        regularShopItems = new ArrayList<>();

        regularShopItems.addAll(IntStream.rangeClosed(Gen3Items.ultraBall,Gen3Items.pokeBall).boxed().collect(Collectors.toList()));
        regularShopItems.addAll(IntStream.rangeClosed(Gen3Items.potion,Gen3Items.revive).boxed().collect(Collectors.toList()));
        regularShopItems.addAll(IntStream.rangeClosed(Gen3Items.superRepel,Gen3Items.repel).boxed().collect(Collectors.toList()));

        opShopItems = new ArrayList<>();

        // "Money items" etc
        opShopItems.add(Gen3Items.rareCandy);
        opShopItems.addAll(IntStream.rangeClosed(Gen3Items.tinyMushroom,Gen3Items.bigMushroom).boxed().collect(Collectors.toList()));
        opShopItems.addAll(IntStream.rangeClosed(Gen3Items.pearl,Gen3Items.nugget).boxed().collect(Collectors.toList()));
        opShopItems.add(Gen3Items.luckyEgg);
    }

    public static ItemList getNonBadItems(int romType) {
        if (romType == Gen3Constants.RomType_FRLG) {
            return nonBadItemsFRLG;
        } else {
            return nonBadItemsRSE;
        }
    }

    public static void trainerTagsRS(List<Trainer> trs, int romType) {
        // Gym Trainers
        tag(trs, "GYM1", 0x140, 0x141);
        tag(trs, "GYM2", 0x1AA, 0x1A9, 0xB3);
        tag(trs, "GYM3", 0xBF, 0x143, 0xC2, 0x289);
        tag(trs, "GYM4", 0xC9, 0x288, 0xCB, 0x28A, 0xCD);
        tag(trs, "GYM5", 0x47, 0x59, 0x49, 0x5A, 0x48, 0x5B, 0x4A);
        tag(trs, "GYM6", 0x191, 0x28F, 0x28E, 0x194);
        tag(trs, "GYM7", 0xE9, 0xEA, 0xEB, 0xF4, 0xF5, 0xF6);
        tag(trs, "GYM8", 0x82, 0x266, 0x83, 0x12D, 0x81, 0x74, 0x80, 0x265);

        // Gym Leaders
        tag(trs, 0x109, "GYM1-LEADER");
        tag(trs, 0x10A, "GYM2-LEADER");
        tag(trs, 0x10B, "GYM3-LEADER");
        tag(trs, 0x10C, "GYM4-LEADER");
        tag(trs, 0x10D, "GYM5-LEADER");
        tag(trs, 0x10E, "GYM6-LEADER");
        tag(trs, 0x10F, "GYM7-LEADER");
        tag(trs, 0x110, "GYM8-LEADER");
        // Elite 4
        tag(trs, 0x105, "ELITE1");
        tag(trs, 0x106, "ELITE2");
        tag(trs, 0x107, "ELITE3");
        tag(trs, 0x108, "ELITE4");
        tag(trs, 0x14F, "CHAMPION");
        // Brendan
        tag(trs, 0x208, "RIVAL1-2");
        tag(trs, 0x20B, "RIVAL1-0");
        tag(trs, 0x20E, "RIVAL1-1");

        tag(trs, 0x209, "RIVAL2-2");
        tag(trs, 0x20C, "RIVAL2-0");
        tag(trs, 0x20F, "RIVAL2-1");

        tag(trs, 0x20A, "RIVAL3-2");
        tag(trs, 0x20D, "RIVAL3-0");
        tag(trs, 0x210, "RIVAL3-1");

        tag(trs, 0x295, "RIVAL4-2");
        tag(trs, 0x296, "RIVAL4-0");
        tag(trs, 0x297, "RIVAL4-1");

        // May
        tag(trs, 0x211, "RIVAL1-2");
        tag(trs, 0x214, "RIVAL1-0");
        tag(trs, 0x217, "RIVAL1-1");

        tag(trs, 0x212, "RIVAL2-2");
        tag(trs, 0x215, "RIVAL2-0");
        tag(trs, 0x218, "RIVAL2-1");

        tag(trs, 0x213, "RIVAL3-2");
        tag(trs, 0x216, "RIVAL3-0");
        tag(trs, 0x219, "RIVAL3-1");

        tag(trs, 0x298, "RIVAL4-2");
        tag(trs, 0x299, "RIVAL4-0");
        tag(trs, 0x29A, "RIVAL4-1");

        // Wally
        tag(trs, "THEMED:WALLY-STRONG", 0x207, 0x290, 0x291, 0x292, 0x293, 0x294);

        if (romType == RomType_Ruby) {
            tag(trs, "THEMED:MAXIE-LEADER", 0x259, 0x25A);
            tag(trs, "THEMED:COURTNEY-STRONG", 0x257, 0x258);
            tag(trs, "THEMED:TABITHA-STRONG", 0x254, 0x255);
        } else {
            tag(trs, "THEMED:ARCHIE-LEADER", 0x23, 0x22);
            tag(trs, "THEMED:MATT-STRONG", 0x1E, 0x1F);
            tag(trs, "THEMED:SHELLY-STRONG", 0x20, 0x21);
        }

    }

    public static void trainerTagsE(List<Trainer> trs) {
        // Gym Trainers
        tag(trs, "GYM1", 0x140, 0x141, 0x23B);
        tag(trs, "GYM2", 0x1AA, 0x1A9, 0xB3, 0x23C, 0x23D, 0x23E);
        tag(trs, "GYM3", 0xBF, 0x143, 0xC2, 0x289, 0x322);
        tag(trs, "GYM4", 0x288, 0xC9, 0xCB, 0x28A, 0xCA, 0xCC, 0x1F5, 0xCD);
        tag(trs, "GYM5", 0x47, 0x59, 0x49, 0x5A, 0x48, 0x5B, 0x4A);
        tag(trs, "GYM6", 0x192, 0x28F, 0x191, 0x28E, 0x194, 0x323);
        tag(trs, "GYM7", 0xE9, 0xEA, 0xEB, 0xF4, 0xF5, 0xF6, 0x24F, 0x248, 0x247, 0x249, 0x246, 0x23F);
        tag(trs, "GYM8", 0x265, 0x80, 0x1F6, 0x73, 0x81, 0x76, 0x82, 0x12D, 0x83, 0x266);

        // Gym Leaders + Emerald Rematches!
        tag(trs, "GYM1-LEADER", 0x109, 0x302, 0x303, 0x304, 0x305);
        tag(trs, "GYM2-LEADER", 0x10A, 0x306, 0x307, 0x308, 0x309);
        tag(trs, "GYM3-LEADER", 0x10B, 0x30A, 0x30B, 0x30C, 0x30D);
        tag(trs, "GYM4-LEADER", 0x10C, 0x30E, 0x30F, 0x310, 0x311);
        tag(trs, "GYM5-LEADER", 0x10D, 0x312, 0x313, 0x314, 0x315);
        tag(trs, "GYM6-LEADER", 0x10E, 0x316, 0x317, 0x318, 0x319);
        tag(trs, "GYM7-LEADER", 0x10F, 0x31A, 0x31B, 0x31C, 0x31D);
        tag(trs, "GYM8-LEADER", 0x110, 0x31E, 0x31F, 0x320, 0x321);

        // Elite 4
        tag(trs, 0x105, "ELITE1");
        tag(trs, 0x106, "ELITE2");
        tag(trs, 0x107, "ELITE3");
        tag(trs, 0x108, "ELITE4");
        tag(trs, 0x14F, "CHAMPION");

        // Brendan
        tag(trs, 0x208, "RIVAL1-2");
        tag(trs, 0x20B, "RIVAL1-0");
        tag(trs, 0x20E, "RIVAL1-1");

        tag(trs, 0x251, "RIVAL2-2");
        tag(trs, 0x250, "RIVAL2-0");
        tag(trs, 0x257, "RIVAL2-1");

        tag(trs, 0x209, "RIVAL3-2");
        tag(trs, 0x20C, "RIVAL3-0");
        tag(trs, 0x20F, "RIVAL3-1");

        tag(trs, 0x20A, "RIVAL4-2");
        tag(trs, 0x20D, "RIVAL4-0");
        tag(trs, 0x210, "RIVAL4-1");

        tag(trs, 0x295, "RIVAL5-2");
        tag(trs, 0x296, "RIVAL5-0");
        tag(trs, 0x297, "RIVAL5-1");

        // May
        tag(trs, 0x211, "RIVAL1-2");
        tag(trs, 0x214, "RIVAL1-0");
        tag(trs, 0x217, "RIVAL1-1");

        tag(trs, 0x258, "RIVAL2-2");
        tag(trs, 0x300, "RIVAL2-0");
        tag(trs, 0x301, "RIVAL2-1");

        tag(trs, 0x212, "RIVAL3-2");
        tag(trs, 0x215, "RIVAL3-0");
        tag(trs, 0x218, "RIVAL3-1");

        tag(trs, 0x213, "RIVAL4-2");
        tag(trs, 0x216, "RIVAL4-0");
        tag(trs, 0x219, "RIVAL4-1");

        tag(trs, 0x298, "RIVAL5-2");
        tag(trs, 0x299, "RIVAL5-0");
        tag(trs, 0x29A, "RIVAL5-1");

        // Themed
        tag(trs, "THEMED:MAXIE-LEADER", 0x259, 0x25A, 0x2DE);
        tag(trs, "THEMED:TABITHA-STRONG", 0x202, 0x255, 0x2DC);
        tag(trs, "THEMED:ARCHIE-LEADER", 0x22);
        tag(trs, "THEMED:MATT-STRONG", 0x1E);
        tag(trs, "THEMED:SHELLY-STRONG", 0x20, 0x21);
        tag(trs, "THEMED:WALLY-STRONG", 0x207, 0x290, 0x291, 0x292, 0x293, 0x294);

        // Steven
        tag(trs, emMeteorFallsStevenIndex, "UBER");

    }

    public static void trainerTagsFRLG(List<Trainer> trs) {

        // Gym Trainers
        tag(trs, "GYM1", 0x8E);
        tag(trs, "GYM2", 0xEA, 0x96);
        tag(trs, "GYM3", 0xDC, 0x8D, 0x1A7);
        tag(trs, "GYM4", 0x10A, 0x84, 0x109, 0xA0, 0x192, 0x10B, 0x85);
        tag(trs, "GYM5", 0x125, 0x124, 0x120, 0x127, 0x126, 0x121);
        tag(trs, "GYM6", 0x11A, 0x119, 0x1CF, 0x11B, 0x1CE, 0x1D0, 0x118);
        tag(trs, "GYM7", 0xD5, 0xB1, 0xB2, 0xD6, 0xB3, 0xD7, 0xB4);
        tag(trs, "GYM8", 0x129, 0x143, 0x188, 0x190, 0x142, 0x128, 0x191, 0x144);

        // Gym Leaders
        tag(trs, 0x19E, "GYM1-LEADER");
        tag(trs, 0x19F, "GYM2-LEADER");
        tag(trs, 0x1A0, "GYM3-LEADER");
        tag(trs, 0x1A1, "GYM4-LEADER");
        tag(trs, 0x1A2, "GYM5-LEADER");
        tag(trs, 0x1A4, "GYM6-LEADER");
        tag(trs, 0x1A3, "GYM7-LEADER");
        tag(trs, 0x15E, "GYM8-LEADER");

        // Giovanni
        tag(trs, 0x15C, "GIO1-LEADER");
        tag(trs, 0x15D, "GIO2-LEADER");

        // E4 Round 1
        tag(trs, 0x19A, "ELITE1-1");
        tag(trs, 0x19B, "ELITE2-1");
        tag(trs, 0x19C, "ELITE3-1");
        tag(trs, 0x19D, "ELITE4-1");

        // E4 Round 2
        tag(trs, 0x2DF, "ELITE1-2");
        tag(trs, 0x2E0, "ELITE2-2");
        tag(trs, 0x2E1, "ELITE3-2");
        tag(trs, 0x2E2, "ELITE4-2");

        // Rival Battles

        // Initial Rival
        tag(trs, 0x148, "RIVAL1-0");
        tag(trs, 0x146, "RIVAL1-1");
        tag(trs, 0x147, "RIVAL1-2");

        // Route 22 (weak)
        tag(trs, 0x14B, "RIVAL2-0");
        tag(trs, 0x149, "RIVAL2-1");
        tag(trs, 0x14A, "RIVAL2-2");

        // Cerulean
        tag(trs, 0x14E, "RIVAL3-0");
        tag(trs, 0x14C, "RIVAL3-1");
        tag(trs, 0x14D, "RIVAL3-2");

        // SS Anne
        tag(trs, 0x1AC, "RIVAL4-0");
        tag(trs, 0x1AA, "RIVAL4-1");
        tag(trs, 0x1AB, "RIVAL4-2");

        // Pokemon Tower
        tag(trs, 0x1AF, "RIVAL5-0");
        tag(trs, 0x1AD, "RIVAL5-1");
        tag(trs, 0x1AE, "RIVAL5-2");

        // Silph Co
        tag(trs, 0x1B2, "RIVAL6-0");
        tag(trs, 0x1B0, "RIVAL6-1");
        tag(trs, 0x1B1, "RIVAL6-2");

        // Route 22 (strong)
        tag(trs, 0x1B5, "RIVAL7-0");
        tag(trs, 0x1B3, "RIVAL7-1");
        tag(trs, 0x1B4, "RIVAL7-2");

        // E4 Round 1
        tag(trs, 0x1B8, "RIVAL8-0");
        tag(trs, 0x1B6, "RIVAL8-1");
        tag(trs, 0x1B7, "RIVAL8-2");

        // E4 Round 2
        tag(trs, 0x2E5, "RIVAL9-0");
        tag(trs, 0x2E3, "RIVAL9-1");
        tag(trs, 0x2E4, "RIVAL9-2");

    }

    private static void tag(List<Trainer> trainers, int trainerNum, String tag) {
        trainers.get(trainerNum - 1).tag = tag;
    }

    private static void tag(List<Trainer> allTrainers, String tag, int... numbers) {
        for (int num : numbers) {
            allTrainers.get(num - 1).tag = tag;
        }
    }

    public static final HashMap<String, Type> gymAndEliteThemesRS = setupGymAndEliteThemesRS();

    private static HashMap<String, Type> setupGymAndEliteThemesRS() {
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

    public static final HashMap<String, Type> gymAndEliteThemesEm = setupGymAndEliteThemesEm();

    private static HashMap<String, Type> setupGymAndEliteThemesEm() {
        HashMap<String, Type> themeMap = new HashMap<>();
        themeMap.put("CHAMPION", Type.WATER); //Wallace
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
        themeMap.put("GYM8", Type.WATER); //Juan
        return themeMap;
    }

    public static final HashMap<String, Type> gymAndEliteThemesFRLG = setupGymAndEliteThemesFRLG();

    private static HashMap<String, Type> setupGymAndEliteThemesFRLG() {
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

    public static void setMultiBattleStatusEm(List<Trainer> trs) {
        // 25 + 569: Double Battle with Team Aqua Grunts on Mt. Pyre
        // 105 + 237: Double Battle with Hex Maniac Patricia and Psychic Joshua
        // 397 + 508: Double Battle with Dragon Tamer Aaron and Cooltrainer Marley
        // 404 + 654: Double Battle with Bird Keeper Edwardo and Camper Flint
        // 504 + 505: Double Battle with Ninja Boy Jonas and Parasol Lady Kayley
        // 514 + 734: Double Battle with Tabitha and Maxie in Mossdeep Space Center
        // 572 + 573: Double Battle with Sailor Brenden and Battle Girl Lilith
        // 721 + 730: Double Battle with Team Magma Grunts in Team Magma Hideout
        // 848 + 850: Double Battle with Psychic Mariela and Gentleman Everett
        setMultiBattleStatus(trs, Trainer.MultiBattleStatus.ALWAYS, 25, 105, 237, 397, 404, 504, 505, 508, 514,
                569, 572, 573, 654, 721, 730, 734, 848, 850
        );

        // 1 + 124: Potential Double Battle with Hiker Sawyer and Beauty Melissa
        // 3 + 192: Potential Double Battle with Team Aqua Grunts in Team Aqua Hideout
        // 8 + 14: Potential Double Battle with Team Aqua Grunts in Seafloor Cavern
        // 9 + 236 + 247: Potential Double Battle with Pokemon Breeder Gabrielle, Psychic William, and Psychic Kayla
        // 11 + 767: Potential Double Battle with Cooltrainer Marcel and Cooltrainer Cristin
        // 12 + 195: Potential Double Battle with Bird Keeper Alberto and Guitarist Fernando
        // 13 + 106: Potential Double Battle with Collector Ed and Hex Maniac Kindra
        // 15 + 450: Potential Double Battle with Swimmer Declan and Swimmer Grace
        // 18 + 596: Potential Double Battle with Team Aqua Grunts in Weather Institute
        // 28 + 193: Potential Double Battle with Team Aqua Grunts in Team Aqua Hideout
        // 29 + 249: Potential Double Battle with Expert Fredrick and Psychic Jacki
        // 31 + 35 + 145: Potential Double Battles with Black Belt Zander, Hex Maniac Leah, and PokéManiac Mark
        // 33 + 567: Potential Double Battle with Shelly and Team Aqua Grunt in Seafloor Cavern
        // 37 + 715: Potential Double Battle with Aroma Lady Rose and Youngster Deandre
        // 38 + 417: Potential Double Battle with Cooltrainer Felix and Cooltrainer Dianne
        // 57 + 698: Potential Double Battle with Tuber Lola and Tuber Chandler
        // 64 + 491 + 697: Potential Double Battles with Tuber Ricky, Sailor Edmond, and Tuber Hailey
        // 107 + 764: Potential Double Battle with Hex Maniac Tammy and Bug Maniac Cale
        // 108 + 475: Potential Double Battle with Hex Maniac Valerie and Psychic Cedric
        // 115 + 502: Potential Double Battle with Lady Daphne and Pokéfan Annika
        // 118 + 129: Potential Double Battle with Lady Brianna and Beauty Bridget
        // 130 + 301: Potential Double Battle with Beauty Olivia and Pokéfan Bethany
        // 131 + 614: Potential Double Battle with Beauty Tiffany and Lass Crissy
        // 137 + 511: Potential Double Battle with Expert Mollie and Expert Conor
        // 144 + 375: Potential Double Battle with Beauty Thalia and Youngster Demetrius
        // 146 + 579: Potential Double Battle with Team Magma Grunts on Mt. Chimney
        // 160 + 595: Potential Double Battle with Swimmer Roland and Triathlete Isabella
        // 168 + 455: Potential Double Battle with Swimmer Santiago and Swimmer Katie
        // 170 + 460: Potential Double Battle with Swimmer Franklin and Swimmer Debra
        // 171 + 385: Potential Double Battle with Swimmer Kevin and Triathlete Taila
        // 180 + 509: Potential Double Battle with Black Belt Hitoshi and Battle Girl Reyna
        // 182 + 307 + 748 + 749: Potential Double Battles with Black Belt Koichi, Expert Timothy, Triathlete Kyra, and Ninja Boy Jaiden
        // 191 + 649: Potential Double Battle with Guitarist Kirk and Battle Girl Vivian
        // 194 + 802: Potential Double Battle with Guitarist Shawn and Bug Maniac Angelo
        // 201 + 648: Potential Double Battle with Kindler Cole and Cooltrainer Gerald
        // 204 + 501: Potential Double Battle with Kindler Jace and Hiker Eli
        // 217 + 566: Potential Double Battle with Picnicker Autumn and Triathlete Julio
        // 232 + 701: Potential Double Battle with Psychic Edward and Triathlete Alyssa
        // 233 + 246: Potential Double Battle with Psychic Preston and Psychic Maura
        // 234 + 244 + 575 + 582: Potential Double Battles with Psychic Virgil, Psychic Hannah, Hex Maniac Sylvia, and Gentleman Nate
        // 235 + 245: Potential Double Battle with Psychic Blake and Psychic Samantha
        // 248 + 849: Potential Double Battle with Psychic Alexis and Psychic Alvaro
        // 273 + 605: Potential Double Battle with School Kid Jerry and Lass Janice
        // 302 + 699: Potential Double Battle with Pokéfan Isabel and Pokéfan Kaleb
        // 321 + 571: Potential Double Battle with Youngster Tommy and Hiker Marc
        // 324 + 325: Potential Double Battle with Cooltrainer Quincy and Cooltrainer Katelynn
        // 345 + 742: Potential Double Battle with Fisherman Carter and Bird Keeper Elijah
        // 377 + 459: Potential Double Battle with Triathlete Pablo and Swimmer Sienna
        // 383 + 576: Potential Double Battle with Triathlete Isobel and Swimmer Leonardo
        // 400 + 761: Potential Double Battle with Bird Keeper Phil and Parasol Lady Rachel
        // 401 + 655: Potential Double Battle with Bird Keeper Jared and Picnicker Ashley
        // 403 + 506: Potential Double Battle with Bird Keeper Presley and Expert Auron
        // 413 + 507: Potential Double Battle with Bird Keeper Alex and Sailor Kelvin
        // 415 + 759: Potential Double Battle with Ninja Boy Yasu and Guitarist Fabian
        // 416 + 760: Potential Double Battle with Ninja Boy Takashi and Kindler Dayton
        // 418 + 547: Potential Double Battle with Tuber Jani and Ruin Maniac Garrison
        // 420 + 710 + 711: Potential Double Battles with Ninja Boy Lung, Camper Lawrence, and PokéManiac Wyatt
        // 436 + 762: Potential Double Battle with Parasol Lady Angelica and Cooltrainer Leonel
        // 445 + 739: Potential Double Battle with Swimmer Beth and Triathlete Camron
        // 464 + 578: Potential Double Battle with Swimmer Carlee and Swimmer Harrison
        // 494 + 495: Potential Double Battle with Sailor Phillip and Sailor Leonard (S.S. Tidal)
        // 503 + 539: Potential Double Battle with Cooltrainer Jazmyn and Bug Catcher Davis
        // 512 + 700: Potential Double Battle with Collector Edwin and Guitarist Joseph
        // 513 + 752: Potential Double Battle with Collector Hector and Psychic Marlene
        // 540 + 546: Potential Double Battle with Cooltrainer Mitchell and Cooltrainer Halle
        // 577 + 674: Potential Double Battle with Cooltrainer Athena and Bird Keeper Aidan
        // 580 + 676: Potential Double Battle with Swimmer Clarence and Swimmer Tisha
        // 583 + 584 + 585 + 591: Potential Double Battles with Hex Maniac Kathleen, Gentleman Clifford, Psychic Nicholas, and Psychic Macey
        // 594 + 733: Potential Double Battle with Expert Paxton and Cooltrainer Darcy
        // 598 + 758: Potential Double Battle with Cooltrainer Jonathan and Expert Makayla
        // 629 + 712: Potential Double Battle with Hiker Lucas and Picnicker Angelina
        // 631 + 753 + 754: Potential Double Battles with Hiker Clark, Hiker Devan, and Youngster Johnson
        // 653 + 763: Potential Double Battle with Ninja Boy Riley and Battle Girl Callie
        // 694 + 695: Potential Double Battle with Rich Boy Dawson and Lady Sarah
        // 702 + 703: Potential Double Battle with Guitarist Marcos and Black Belt Rhett
        // 704 + 705: Potential Double Battle with Camper Tyron and Aroma Lady Celina
        // 706 + 707: Potential Double Battle with Picnicker Bianca and Kindler Hayden
        // 708 + 709: Potential Double Battle with Picnicker Sophie and Bird Keeper Coby
        // 713 + 714: Potential Double Battle with Fisherman Kai and Picnicker Charlotte
        // 719 + 720: Potential Double Battle with Team Magma Grunts in Team Magma Hideout
        // 727 + 728: Potential Double Battle with Team Magma Grunts in Team Magma Hideout
        // 735 + 736: Potential Double Battle with Swimmer Pete and Swimmer Isabelle
        // 737 + 738: Potential Double Battle with Ruin Maniac Andres and Bird Keeper Josue
        // 740 + 741: Potential Double Battle with Sailor Cory and Cooltrainer Carolina
        // 743 + 744 + 745: Potential Double Battles with Picnicker Celia, Ruin Maniac Bryan, and Camper Branden
        // 746 + 747: Potential Double Battle with Kindler Bryant and Aroma Lady Shayla
        // 750 + 751: Potential Double Battle with Psychic Alix and Battle Girl Helene
        // 755 + 756 + 757: Potential Double Battles with Triathlete Melina, Psychic Brandi, and Battle Girl Aisha
        // 765 + 766: Potential Double Battle with Pokémon Breeder Myles and Pokémon Breeder Pat
        setMultiBattleStatus(trs, Trainer.MultiBattleStatus.POTENTIAL, 1, 3, 8, 9, 11, 12, 13, 14, 15, 18, 28,
                29, 31, 33, 35, 37, 38, 57, 64, 106, 107, 108, 115, 118, 124, 129, 130, 131, 137, 144, 145, 146, 160,
                168, 170, 171, 180, 182, 191, 192, 193, 194, 195, 201, 204, 217, 232, 233, 234, 235, 236, 244, 245, 246,
                247, 248, 249, 273, 301, 302, 307, 321, 324, 325, 345, 375, 377, 383, 385, 400, 401, 403, 413, 415, 416,
                417, 418, 420, 436, 445, 450, 455, 459, 460, 464, 475, 491, 494, 495, 501, 502, 503, 506, 507, 509, 511,
                512, 513, 539, 540, 546, 547, 566, 567, 571, 575, 576, 577, 578, 579, 580, 582, 583, 584, 585, 591, 594,
                595, 596, 598, 605, 614, 629, 631, 648, 649, 653, 655, 674, 676, 694, 695, 697, 698, 699, 700, 701, 702,
                703, 704, 705, 706, 707, 708, 709, 710, 711, 712, 713, 714, 715, 719, 720, 727, 728, 733, 735, 736, 737,
                738, 739, 740, 741, 742, 743, 744, 745, 746, 747, 748, 749, 750, 751, 752, 753, 754, 755, 756, 757, 758,
                759, 760, 761, 762, 763, 764, 765, 766, 767, 802, 849
        );
    }

    private static void setMultiBattleStatus(List<Trainer> allTrainers, Trainer.MultiBattleStatus status, int... numbers) {
        for (int num : numbers) {
            if (allTrainers.size() > (num - 1)) {
                allTrainers.get(num - 1).multiBattleStatus = status;
            }
        }
    }

    public static final Map<Integer,Integer> balancedItemPrices = Stream.of(new Integer[][] {
            // Skip item index 0. All prices divided by 10
            {Gen3Items.masterBall, 300},
            {Gen3Items.ultraBall, 120},
            {Gen3Items.greatBall, 60},
            {Gen3Items.pokeBall, 20},
            {Gen3Items.safariBall, 50},
            {Gen3Items.netBall, 100},
            {Gen3Items.diveBall, 100},
            {Gen3Items.nestBall, 100},
            {Gen3Items.repeatBall, 100},
            {Gen3Items.timerBall, 100},
            {Gen3Items.luxuryBall, 100},
            {Gen3Items.premierBall, 20},
            {Gen3Items.potion, 30},
            {Gen3Items.antidote, 10},
            {Gen3Items.burnHeal, 25},
            {Gen3Items.iceHeal, 25},
            {Gen3Items.awakening, 25},
            {Gen3Items.parlyzHeal, 20},
            {Gen3Items.fullRestore, 300},
            {Gen3Items.maxPotion, 250},
            {Gen3Items.hyperPotion, 120},
            {Gen3Items.superPotion, 70},
            {Gen3Items.fullHeal, 60},
            {Gen3Items.revive, 150},
            {Gen3Items.maxRevive, 400},
            {Gen3Items.freshWater, 40},
            {Gen3Items.sodaPop, 60},
            {Gen3Items.lemonade, 70},
            {Gen3Items.moomooMilk, 80},
            {Gen3Items.energyPowder, 40},
            {Gen3Items.energyRoot, 110},
            {Gen3Items.healPowder, 45},
            {Gen3Items.revivalHerb, 280},
            {Gen3Items.ether, 300},
            {Gen3Items.maxEther, 450},
            {Gen3Items.elixir, 1500},
            {Gen3Items.maxElixir, 1800},
            {Gen3Items.lavaCookie, 45},
            {Gen3Items.blueFlute, 2},
            {Gen3Items.yellowFlute, 2},
            {Gen3Items.redFlute, 2},
            {Gen3Items.blackFlute, 2},
            {Gen3Items.whiteFlute, 2},
            {Gen3Items.berryJuice, 10},
            {Gen3Items.sacredAsh, 1000},
            {Gen3Items.shoalSalt, 2},
            {Gen3Items.shoalShell, 2},
            {Gen3Items.redShard, 40},
            {Gen3Items.blueShard, 40},
            {Gen3Items.yellowShard, 40},
            {Gen3Items.greenShard, 40},
            {Gen3Items.unknown52, 0},
            {Gen3Items.unknown53, 0},
            {Gen3Items.unknown54, 0},
            {Gen3Items.unknown55, 0},
            {Gen3Items.unknown56, 0},
            {Gen3Items.unknown57, 0},
            {Gen3Items.unknown58, 0},
            {Gen3Items.unknown59, 0},
            {Gen3Items.unknown60, 0},
            {Gen3Items.unknown61, 0},
            {Gen3Items.unknown62, 0},
            {Gen3Items.hpUp, 980},
            {Gen3Items.protein, 980},
            {Gen3Items.iron, 980},
            {Gen3Items.carbos, 980},
            {Gen3Items.calcium, 980},
            {Gen3Items.rareCandy, 1000},
            {Gen3Items.ppUp, 980},
            {Gen3Items.zinc, 980},
            {Gen3Items.ppMax, 2490},
            {Gen3Items.unknown72, 0},
            {Gen3Items.guardSpec, 70},
            {Gen3Items.direHit, 65},
            {Gen3Items.xAttack, 50},
            {Gen3Items.xDefend, 55},
            {Gen3Items.xSpeed, 35},
            {Gen3Items.xAccuracy, 95},
            {Gen3Items.xSpecial, 35},
            {Gen3Items.pokeDoll, 100},
            {Gen3Items.fluffyTail, 100},
            {Gen3Items.unknown82, 0},
            {Gen3Items.superRepel, 50},
            {Gen3Items.maxRepel, 70},
            {Gen3Items.escapeRope, 55},
            {Gen3Items.repel, 35},
            {Gen3Items.unknown87, 0},
            {Gen3Items.unknown88, 0},
            {Gen3Items.unknown89, 0},
            {Gen3Items.unknown90, 0},
            {Gen3Items.unknown91, 0},
            {Gen3Items.unknown92, 0},
            {Gen3Items.sunStone, 300},
            {Gen3Items.moonStone, 300},
            {Gen3Items.fireStone, 300},
            {Gen3Items.thunderstone, 300},
            {Gen3Items.waterStone, 300},
            {Gen3Items.leafStone, 300},
            {Gen3Items.unknown99, 0},
            {Gen3Items.unknown100, 0},
            {Gen3Items.unknown101, 0},
            {Gen3Items.unknown102, 0},
            {Gen3Items.tinyMushroom, 50},
            {Gen3Items.bigMushroom, 500},
            {Gen3Items.unknown105, 0},
            {Gen3Items.pearl, 140},
            {Gen3Items.bigPearl, 750},
            {Gen3Items.stardust, 200},
            {Gen3Items.starPiece, 980},
            {Gen3Items.nugget, 1000},
            {Gen3Items.heartScale, 500},
            {Gen3Items.unknown112, 0},
            {Gen3Items.unknown113, 0},
            {Gen3Items.unknown114, 0},
            {Gen3Items.unknown115, 0},
            {Gen3Items.unknown116, 0},
            {Gen3Items.unknown117, 0},
            {Gen3Items.unknown118, 0},
            {Gen3Items.unknown119, 0},
            {Gen3Items.unknown120, 0},
            {Gen3Items.orangeMail, 5},
            {Gen3Items.harborMail, 5},
            {Gen3Items.glitterMail, 5},
            {Gen3Items.mechMail, 5},
            {Gen3Items.woodMail, 5},
            {Gen3Items.waveMail, 5},
            {Gen3Items.beadMail, 5},
            {Gen3Items.shadowMail, 5},
            {Gen3Items.tropicMail, 5},
            {Gen3Items.dreamMail, 5},
            {Gen3Items.fabMail, 5},
            {Gen3Items.retroMail, 5},
            {Gen3Items.cheriBerry, 20},
            {Gen3Items.chestoBerry, 25},
            {Gen3Items.pechaBerry, 10},
            {Gen3Items.rawstBerry, 25},
            {Gen3Items.aspearBerry, 25},
            {Gen3Items.leppaBerry, 300},
            {Gen3Items.oranBerry, 5},
            {Gen3Items.persimBerry, 20},
            {Gen3Items.lumBerry, 50},
            {Gen3Items.sitrusBerry, 50},
            {Gen3Items.figyBerry, 10},
            {Gen3Items.wikiBerry, 10},
            {Gen3Items.magoBerry, 10},
            {Gen3Items.aguavBerry, 10},
            {Gen3Items.iapapaBerry, 10},
            {Gen3Items.razzBerry, 50},
            {Gen3Items.blukBerry, 50},
            {Gen3Items.nanabBerry, 50},
            {Gen3Items.wepearBerry, 50},
            {Gen3Items.pinapBerry, 50},
            {Gen3Items.pomegBerry, 50},
            {Gen3Items.kelpsyBerry, 50},
            {Gen3Items.qualotBerry, 50},
            {Gen3Items.hondewBerry, 50},
            {Gen3Items.grepaBerry, 50},
            {Gen3Items.tamatoBerry, 50},
            {Gen3Items.cornnBerry, 50},
            {Gen3Items.magostBerry, 50},
            {Gen3Items.rabutaBerry, 50},
            {Gen3Items.nomelBerry, 50},
            {Gen3Items.spelonBerry, 50},
            {Gen3Items.pamtreBerry, 50},
            {Gen3Items.watmelBerry, 50},
            {Gen3Items.durinBerry, 50},
            {Gen3Items.belueBerry, 50},
            {Gen3Items.liechiBerry, 100},
            {Gen3Items.ganlonBerry, 100},
            {Gen3Items.salacBerry, 100},
            {Gen3Items.petayaBerry, 100},
            {Gen3Items.apicotBerry, 100},
            {Gen3Items.lansatBerry, 100},
            {Gen3Items.starfBerry, 100},
            {Gen3Items.enigmaBerry, 100},
            {Gen3Items.unknown176, 0},
            {Gen3Items.unknown177, 0},
            {Gen3Items.unknown178, 0},
            {Gen3Items.brightPowder, 300},
            {Gen3Items.whiteHerb, 100},
            {Gen3Items.machoBrace, 300},
            {Gen3Items.expShare, 600},
            {Gen3Items.quickClaw, 450},
            {Gen3Items.sootheBell, 100},
            {Gen3Items.mentalHerb, 100},
            {Gen3Items.choiceBand, 1000},
            {Gen3Items.kingsRock, 500},
            {Gen3Items.silverPowder, 200},
            {Gen3Items.amuletCoin, 1500},
            {Gen3Items.cleanseTag, 100},
            {Gen3Items.soulDew, 20},
            {Gen3Items.deepSeaTooth, 300},
            {Gen3Items.deepSeaScale, 300},
            {Gen3Items.smokeBall, 20},
            {Gen3Items.everstone, 20},
            {Gen3Items.focusBand, 300},
            {Gen3Items.luckyEgg, 1000},
            {Gen3Items.scopeLens, 500},
            {Gen3Items.metalCoat, 300},
            {Gen3Items.leftovers, 1000},
            {Gen3Items.dragonScale, 300},
            {Gen3Items.lightBall, 10},
            {Gen3Items.softSand, 200},
            {Gen3Items.hardStone, 200},
            {Gen3Items.miracleSeed, 200},
            {Gen3Items.blackGlasses, 200},
            {Gen3Items.blackBelt, 200},
            {Gen3Items.magnet, 200},
            {Gen3Items.mysticWater, 200},
            {Gen3Items.sharpBeak, 200},
            {Gen3Items.poisonBarb, 200},
            {Gen3Items.neverMeltIce, 200},
            {Gen3Items.spellTag, 200},
            {Gen3Items.twistedSpoon, 200},
            {Gen3Items.charcoal, 200},
            {Gen3Items.dragonFang, 200},
            {Gen3Items.silkScarf, 200},
            {Gen3Items.upGrade, 300},
            {Gen3Items.shellBell, 600},
            {Gen3Items.seaIncense, 200},
            {Gen3Items.laxIncense, 300},
            {Gen3Items.luckyPunch, 1},
            {Gen3Items.metalPowder, 1},
            {Gen3Items.thickClub, 50},
            {Gen3Items.stick, 20},
            {Gen3Items.unknown226, 0},
            {Gen3Items.unknown227, 0},
            {Gen3Items.unknown228, 0},
            {Gen3Items.unknown229, 0},
            {Gen3Items.unknown230, 0},
            {Gen3Items.unknown231, 0},
            {Gen3Items.unknown232, 0},
            {Gen3Items.unknown233, 0},
            {Gen3Items.unknown234, 0},
            {Gen3Items.unknown235, 0},
            {Gen3Items.unknown236, 0},
            {Gen3Items.unknown237, 0},
            {Gen3Items.unknown238, 0},
            {Gen3Items.unknown239, 0},
            {Gen3Items.unknown240, 0},
            {Gen3Items.unknown241, 0},
            {Gen3Items.unknown242, 0},
            {Gen3Items.unknown243, 0},
            {Gen3Items.unknown244, 0},
            {Gen3Items.unknown245, 0},
            {Gen3Items.unknown246, 0},
            {Gen3Items.unknown247, 0},
            {Gen3Items.unknown248, 0},
            {Gen3Items.unknown249, 0},
            {Gen3Items.unknown250, 0},
            {Gen3Items.unknown251, 0},
            {Gen3Items.unknown252, 0},
            {Gen3Items.unknown253, 0},
            {Gen3Items.redScarf, 10},
            {Gen3Items.blueScarf, 10},
            {Gen3Items.pinkScarf, 10},
            {Gen3Items.greenScarf, 10},
            {Gen3Items.yellowScarf, 10},
            {Gen3Items.machBike, 0},
            {Gen3Items.coinCase, 0},
            {Gen3Items.itemfinder, 0},
            {Gen3Items.oldRod, 0},
            {Gen3Items.goodRod, 0},
            {Gen3Items.superRod, 0},
            {Gen3Items.ssTicket, 0},
            {Gen3Items.contestPass, 0},
            {Gen3Items.unknown267, 0},
            {Gen3Items.wailmerPail, 0},
            {Gen3Items.devonGoods, 0},
            {Gen3Items.sootSack, 0},
            {Gen3Items.basementKey, 0},
            {Gen3Items.acroBike, 0},
            {Gen3Items.pokeblockCase, 0},
            {Gen3Items.letter, 0},
            {Gen3Items.eonTicket, 0},
            {Gen3Items.redOrb, 0},
            {Gen3Items.blueOrb, 0},
            {Gen3Items.scanner, 0},
            {Gen3Items.goGoggles, 0},
            {Gen3Items.meteorite, 0},
            {Gen3Items.rm1Key, 0},
            {Gen3Items.rm2Key, 0},
            {Gen3Items.rm4Key, 0},
            {Gen3Items.rm6Key, 0},
            {Gen3Items.storageKey, 0},
            {Gen3Items.rootFossil, 0},
            {Gen3Items.clawFossil, 0},
            {Gen3Items.devonScope, 0},
            {Gen3Items.tm01, 300},
            {Gen3Items.tm02, 300},
            {Gen3Items.tm03, 300},
            {Gen3Items.tm04, 150},
            {Gen3Items.tm05, 100},
            {Gen3Items.tm06, 300},
            {Gen3Items.tm07, 200},
            {Gen3Items.tm08, 150},
            {Gen3Items.tm09, 200},
            {Gen3Items.tm10, 200},
            {Gen3Items.tm11, 200},
            {Gen3Items.tm12, 150},
            {Gen3Items.tm13, 300},
            {Gen3Items.tm14, 550},
            {Gen3Items.tm15, 750},
            {Gen3Items.tm16, 200},
            {Gen3Items.tm17, 200},
            {Gen3Items.tm18, 200},
            {Gen3Items.tm19, 300},
            {Gen3Items.tm20, 200},
            {Gen3Items.tm21, 100},
            {Gen3Items.tm22, 300},
            {Gen3Items.tm23, 300},
            {Gen3Items.tm24, 300},
            {Gen3Items.tm25, 550},
            {Gen3Items.tm26, 300},
            {Gen3Items.tm27, 100},
            {Gen3Items.tm28, 200},
            {Gen3Items.tm29, 300},
            {Gen3Items.tm30, 300},
            {Gen3Items.tm31, 300},
            {Gen3Items.tm32, 100},
            {Gen3Items.tm33, 200},
            {Gen3Items.tm34, 300},
            {Gen3Items.tm35, 300},
            {Gen3Items.tm36, 300},
            {Gen3Items.tm37, 200},
            {Gen3Items.tm38, 550},
            {Gen3Items.tm39, 200},
            {Gen3Items.tm40, 300},
            {Gen3Items.tm41, 150},
            {Gen3Items.tm42, 300},
            {Gen3Items.tm43, 200},
            {Gen3Items.tm44, 300},
            {Gen3Items.tm45, 300},
            {Gen3Items.tm46, 200},
            {Gen3Items.tm47, 300},
            {Gen3Items.tm48, 300},
            {Gen3Items.tm49, 150},
            {Gen3Items.tm50, 550},
            {Gen3Items.hm01, 0},
            {Gen3Items.hm02, 0},
            {Gen3Items.hm03, 0},
            {Gen3Items.hm04, 0},
            {Gen3Items.hm05, 0},
            {Gen3Items.hm06, 0},
            {Gen3Items.hm07, 0},
            {Gen3Items.hm08, 0},
            {Gen3Items.unknown347, 0},
            {Gen3Items.unknown348, 0},
            {Gen3Items.oaksParcel, 0},
            {Gen3Items.pokeFlute, 0},
            {Gen3Items.secretKey, 0},
            {Gen3Items.bikeVoucher, 0},
            {Gen3Items.goldTeeth, 0},
            {Gen3Items.oldAmber, 0},
            {Gen3Items.cardKey, 0},
            {Gen3Items.liftKey, 0},
            {Gen3Items.helixFossil, 0},
            {Gen3Items.domeFossil, 0},
            {Gen3Items.silphScope, 0},
            {Gen3Items.bicycle, 0},
            {Gen3Items.townMap, 0},
            {Gen3Items.vsSeeker, 0},
            {Gen3Items.fameChecker, 0},
            {Gen3Items.tmCase, 0},
            {Gen3Items.berryPouch, 0},
            {Gen3Items.teachyTV, 0},
            {Gen3Items.triPass, 0},
            {Gen3Items.rainbowPass, 0},
            {Gen3Items.tea, 0},
            {Gen3Items.mysticTicket, 0},
            {Gen3Items.auroraTicket, 0},
            {Gen3Items.powderJar, 0},
            {Gen3Items.ruby, 0},
            {Gen3Items.sapphire, 0},
            {Gen3Items.magmaEmblem, 0},
            {Gen3Items.oldSeaMap, 0},
    }).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
}
