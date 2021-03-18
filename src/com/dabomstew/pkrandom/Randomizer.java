package com.dabomstew.pkrandom;

/*----------------------------------------------------------------------------*/
/*--  Randomizer.java - Can randomize a file based on settings.             --*/
/*--                    Output varies by seed.                              --*/
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

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.dabomstew.pkrandom.pokemon.*;
import com.dabomstew.pkrandom.romhandlers.Gen1RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen5RomHandler;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

// Can randomize a file based on settings. Output varies by seed.
public class Randomizer {

    private static final String NEWLINE = System.getProperty("line.separator");

    private final Settings settings;
    private final RomHandler romHandler;
    private final boolean saveAsDirectory;

    public Randomizer(Settings settings, RomHandler romHandler, boolean saveAsDirectory) {
        this.settings = settings;
        this.romHandler = romHandler;
        this.saveAsDirectory = saveAsDirectory;
    }

    public int randomize(final String filename) {
        return randomize(filename, new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
            }
        }));
    }

    public int randomize(final String filename, final PrintStream log) {
        long seed = RandomSource.pickSeed();
        // long seed = 123456789;    // TESTING
        return randomize(filename, log, seed);
    }

    public int randomize(final String filename, final PrintStream log, long seed) {
        final long startTime = System.currentTimeMillis();
        // long seed2 = 123456789;    // TESTING
        // RandomSource.seed(seed2);    // TESTING
        RandomSource.seed(seed);

        int checkValue = 0;

        log.println("Randomizer Version: " + Version.VERSION_STRING);
        log.println("Random Seed: " + seed);
        log.println("Settings String: " + Version.VERSION + settings.toString());
        log.println();

        // limit pokemon?
        if (settings.isLimitPokemon()) {
            romHandler.setPokemonPool(settings.getCurrentRestrictions());
            romHandler.removeEvosForPokemonPool();
        } else {
            romHandler.setPokemonPool(null);
        }

        // Move updates & data changes
        if (settings.isUpdateMoves()) {
            romHandler.initMoveUpdates();
            romHandler.updateMoves(settings.getUpdateMovesToGeneration());
//            if (!(romHandler instanceof Gen5RomHandler)) {
//                romHandler.updateMovesToGen5();
//            }
//            if (!settings.isUpdateMovesLegacy()) {
//                romHandler.updateMovesToGen6();
//            }
            romHandler.printMoveUpdates();
        }

        if (settings.isRandomizeMovePowers()) {
            romHandler.randomizeMovePowers();
        }

        if (settings.isRandomizeMoveAccuracies()) {
            romHandler.randomizeMoveAccuracies();
        }

        if (settings.isRandomizeMovePPs()) {
            romHandler.randomizeMovePPs();
        }

        if (settings.isRandomizeMoveTypes()) {
            romHandler.randomizeMoveTypes();
        }

        if (settings.isRandomizeMoveCategory() && romHandler.hasPhysicalSpecialSplit()) {
            romHandler.randomizeMoveCategory();
        }

        List<Move> moves = romHandler.getMoves();

        // Misc Tweaks?
        int currentMiscTweaks = settings.getCurrentMiscTweaks();
        if (romHandler.miscTweaksAvailable() != 0) {
            int codeTweaksAvailable = romHandler.miscTweaksAvailable();
            List<MiscTweak> tweaksToApply = new ArrayList<>();

            for (MiscTweak mt : MiscTweak.allTweaks) {
                if ((codeTweaksAvailable & mt.getValue()) > 0 && (currentMiscTweaks & mt.getValue()) > 0) {
                    tweaksToApply.add(mt);
                }
            }

            // Sort so priority is respected in tweak ordering.
            Collections.sort(tweaksToApply);

            // Now apply in order.
            for (MiscTweak mt : tweaksToApply) {
                romHandler.applyMiscTweak(mt);
            }
        }

        if (settings.isUpdateBaseStats()) {
            romHandler.updatePokemonStats(settings.getUpdateBaseStatsToGeneration());
        }

        if (settings.isStandardizeEXPCurves()) {
            romHandler.standardizeEXPCurves(settings.getExpCurveMod(), settings.getSelectedEXPCurve());
        }


        // Pokemon Types
        switch (settings.getTypesMod()) {
        case RANDOM_FOLLOW_EVOLUTIONS:
            romHandler.randomizePokemonTypes(true, settings.isTypesFollowMegaEvolutions());
            break;
        case COMPLETELY_RANDOM:
            romHandler.randomizePokemonTypes(false, settings.isTypesFollowMegaEvolutions());
            break;
        default:
            break;
        }

        // Wild Held Items?
        if (settings.isRandomizeWildPokemonHeldItems()) {
            romHandler.randomizeWildHeldItems(settings.isBanBadRandomWildPokemonHeldItems());
        }

        // Random Evos
        // Applied after type to pick new evos based on new types.
        if (settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM) {
            romHandler.randomizeEvolutions(settings.isEvosSimilarStrength(), settings.isEvosSameTyping(),
                    settings.isEvosMaxThreeStages(), settings.isEvosForceChange(), settings.isEvosAllowAltFormes(),
                    settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE);

            log.println("--Randomized Evolutions--");
            List<Pokemon> allPokes = romHandler.getPokemonInclFormes();
            for (Pokemon pk : allPokes) {
                if (pk != null && !pk.actuallyCosmetic) {
                    int numEvos = pk.evolutionsFrom.size();
                    if (numEvos > 0) {
                        StringBuilder evoStr = new StringBuilder(pk.evolutionsFrom.get(0).toFullName());
                        for (int i = 1; i < numEvos; i++) {
                            if (i == numEvos - 1) {
                                evoStr.append(" and ").append(pk.evolutionsFrom.get(i).toFullName());
                            } else {
                                evoStr.append(", ").append(pk.evolutionsFrom.get(i).toFullName());
                            }
                        }
                        // log.println(pk.fullName() + " now evolves into " + evoStr.toString());
                        log.printf("%-15s -> %-15s" + NEWLINE, pk.fullName(), evoStr.toString());
                    }
                }
            }

            log.println();
        }

        // Base stats changing
        switch (settings.getBaseStatisticsMod()) {
            case SHUFFLE:
                romHandler.shufflePokemonStats(settings.isBaseStatsFollowEvolutions(),
                        settings.isBaseStatsFollowMegaEvolutions());
                break;
            case RANDOM:
                romHandler.randomizePokemonStats(settings.isBaseStatsFollowEvolutions(),
                        settings.isBaseStatsFollowMegaEvolutions());
                break;
            default:
                break;
        }

        // Abilities? (new 1.0.2)
        if (romHandler.abilitiesPerPokemon() > 0 && settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE) {
            romHandler.randomizeAbilities(settings.isAbilitiesFollowEvolutions(), settings.isAllowWonderGuard(),
                    settings.isBanTrappingAbilities(), settings.isBanNegativeAbilities(), settings.isBanBadAbilities(),
                    settings.isAbilitiesFollowMegaEvolutions(), settings.isWeighDuplicateAbilitiesTogether());
        }

        maybeLogBaseStatAndTypeChanges(log, romHandler);
        for (Pokemon pkmn : romHandler.getPokemon()) {
            if (pkmn != null) {
                checkValue = addToCV(checkValue, pkmn.hp, pkmn.attack, pkmn.defense, pkmn.speed, pkmn.spatk,
                        pkmn.spdef, pkmn.ability1, pkmn.ability2, pkmn.ability3);
            }
        }

        // Trade evolutions removal
        if (settings.isChangeImpossibleEvolutions()) {
            romHandler.removeImpossibleEvolutions(!(settings.getMovesetsMod() == Settings.MovesetsMod.UNCHANGED));
        }

        // Easier evolutions
        if (settings.isMakeEvolutionsEasier()) {
            romHandler.condenseLevelEvolutions(40, 30);
            romHandler.makeEvolutionsEasier(!settings.getWildPokemonMod().equals(Settings.WildPokemonMod.UNCHANGED));
        }

        // Remove time-based evolutions
        if (settings.isRemoveTimeBasedEvolutions()) {
            romHandler.removeTimeBasedEvolutions();
        }

        // Starter Pokemon
        // Applied after type to update the strings correctly based on new types
        maybeChangeAndLogStarters(log, romHandler);

        // Move Data Log
        // Placed here so it matches its position in the randomizer interface
        maybeLogMoveChanges(log, romHandler);

        // Movesets
        boolean noBrokenMovesetMoves = settings.isBlockBrokenMovesetMoves();
        boolean forceLv1s = romHandler.supportsFourStartingMoves() && settings.isStartWithGuaranteedMoves();
        int forceLv1Count = settings.getGuaranteedMoveCount();
        double msGoodDamagingProb = settings.isMovesetsForceGoodDamaging() ? settings.getMovesetsGoodDamagingPercent() / 100.0
                : 0;
        if (settings.getMovesetsMod() == Settings.MovesetsMod.RANDOM_PREFER_SAME_TYPE) {
            romHandler.randomizeMovesLearnt(true, noBrokenMovesetMoves, forceLv1s, forceLv1Count, msGoodDamagingProb,
                    settings.isEvolutionMovesForAll());
        } else if (settings.getMovesetsMod() == Settings.MovesetsMod.COMPLETELY_RANDOM) {
            romHandler.randomizeMovesLearnt(false, noBrokenMovesetMoves, forceLv1s, forceLv1Count, msGoodDamagingProb,
                    settings.isEvolutionMovesForAll());
        }

        if (settings.isReorderDamagingMoves()) {
            romHandler.orderDamagingMovesByDamage();
        }

        // Show the new movesets if applicable
        if (settings.getMovesetsMod() == Settings.MovesetsMod.UNCHANGED) {
            log.println("Pokemon Movesets: Unchanged." + NEWLINE);
        } else if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
            log.println("Pokemon Movesets: Metronome Only." + NEWLINE);
        } else {
            log.println("--Pokemon Movesets--");
            List<String> movesets = new ArrayList<>();
            Map<Integer, List<MoveLearnt>> moveData = romHandler.getMovesLearnt();
            List<Pokemon> pkmnList = romHandler.getPokemonInclFormes();
            int i = 1;
            for (Pokemon pkmn : pkmnList) {

                if (pkmn == null || pkmn.actuallyCosmetic) {
                    continue;
                }
                StringBuilder evoStr = new StringBuilder(); 
                try {
                    evoStr.append(" -> ").append(pkmn.evolutionsFrom.get(0).to.fullName());
                } catch (Exception e) {
                    evoStr.append(" (no evolution)");
                }

                StringBuilder sb = new StringBuilder();

                // sb.append(String.format("%03d %s", pkmn.number, pkmn.fullName())).append(System.getProperty("line.separator")).append(String.format("HP %-3d ATK %-3d DEF %-3d SPATK %-3d SPDEF %-3d SPD %-3d", pkmn.hp, pkmn.attack, pkmn.defense, pkmn.speed, pkmn.spatk, pkmn.spdef)).append(System.getProperty("line.separator"));

                sb.append(String.format("%03d %s", i, pkmn.fullName())).append(evoStr).append(System.getProperty("line.separator")).append(String.format("HP  %-3d", pkmn.hp)).append(System.getProperty("line.separator")).append(String.format("ATK %-3d", pkmn.attack)).append(System.getProperty("line.separator")).append(String.format("DEF %-3d", pkmn.defense)).append(System.getProperty("line.separator")).append(String.format("SPA %-3d", pkmn.spatk)).append(System.getProperty("line.separator")).append(String.format("SPD %-3d", pkmn.spdef)).append(System.getProperty("line.separator")).append(String.format("SPE %-3d", pkmn.speed)).append(System.getProperty("line.separator"));
                i++;

                List<MoveLearnt> data = moveData.get(pkmn.number);
                for (MoveLearnt ml : data) {
                    try {
                        if (ml.level == 0) {
                            sb.append("Learned upon evolution: ").append(moves.get(ml.move).name).append(System.getProperty("line.separator"));
                        } else {
                            sb.append("Level ").append(String.format("%-2d", ml.level)).append(": ").append(moves.get(ml.move).name).append(System.getProperty("line.separator"));
                        }
                    } catch (NullPointerException ex) {
                        sb.append("invalid move at level").append(ml.level);
                    }
                }
                movesets.add(sb.toString());
            }
            Collections.sort(movesets);
            for (String moveset : movesets) {
                log.println(moveset);
            }
            log.println();
        }

        // Trainer Pokemon
        romHandler.addTrainerPokemon(
                settings.getAdditionalRegularTrainerPokemon(),
                settings.getAdditionalImportantTrainerPokemon(),
                settings.getAdditionalBossTrainerPokemon());

        if (settings.isDoubleBattleMode()) {
            romHandler.doubleBattleMode();
        }

        if (settings.getTrainersMod() == Settings.TrainersMod.RANDOM) {
            romHandler.randomizeTrainerPokes(settings.isTrainersUsePokemonOfSimilarStrength(),
                    settings.isTrainersBlockLegendaries(), settings.isTrainersBlockEarlyWonderGuard(),
                    settings.isTrainersLevelModified() ? settings.getTrainersLevelModifier() : 0, false, false,
                    settings.isAllowTrainerAlternateFormes(), settings.isSwapTrainerMegaEvos(), settings.isShinyChance(),
                    settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE);
        } else if (settings.getTrainersMod() == Settings.TrainersMod.TYPE_THEMED) {
            romHandler.typeThemeTrainerPokes(settings.isTrainersUsePokemonOfSimilarStrength(),
                    settings.isTrainersMatchTypingDistribution(), settings.isTrainersBlockLegendaries(),
                    settings.isTrainersBlockEarlyWonderGuard(),
                    settings.isTrainersLevelModified() ? settings.getTrainersLevelModifier() : 0,
                    settings.isAllowTrainerAlternateFormes(), settings.isSwapTrainerMegaEvos(), settings.isShinyChance(),
                    settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE);
        } else if (settings.getTrainersMod() == Settings.TrainersMod.DISTRIBUTED) {
            romHandler.randomizeTrainerPokes(settings.isTrainersUsePokemonOfSimilarStrength(),
                    settings.isTrainersBlockLegendaries(), settings.isTrainersBlockEarlyWonderGuard(),
                    settings.isTrainersLevelModified() ? settings.getTrainersLevelModifier() : 0, true, false,
                    settings.isAllowTrainerAlternateFormes(), settings.isSwapTrainerMegaEvos(), settings.isShinyChance(),
                    settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE);
        } else if (settings.getTrainersMod() == Settings.TrainersMod.MAINPLAYTHROUGH) {
            romHandler.randomizeTrainerPokes(settings.isTrainersUsePokemonOfSimilarStrength(),
                    settings.isTrainersBlockLegendaries(), settings.isTrainersBlockEarlyWonderGuard(),
                    settings.isTrainersLevelModified() ? settings.getTrainersLevelModifier() : 0, true, true,
                    settings.isAllowTrainerAlternateFormes(), settings.isSwapTrainerMegaEvos(), settings.isShinyChance(),
                    settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE);
        } else {
            if (settings.isTrainersLevelModified()) {
                romHandler.onlyChangeTrainerLevels(settings.getTrainersLevelModifier());
            }
        }

        if ((settings.getTrainersMod() != Settings.TrainersMod.UNCHANGED || settings.getStartersMod() != Settings.StartersMod.UNCHANGED)
                && settings.isRivalCarriesStarterThroughout()) {
            romHandler.rivalCarriesStarter();
        }

        if (settings.isTrainersForceFullyEvolved()) {
            romHandler.forceFullyEvolvedTrainerPokes(settings.getTrainersForceFullyEvolvedLevel());
        }

        // Trainer names & class names randomization
        // done before trainer log to add proper names

        if (romHandler.canChangeTrainerText()) {
            if (settings.isRandomizeTrainerClassNames()) {
                romHandler.randomizeTrainerClassNames(settings.getCustomNames());
            }

            if (settings.isRandomizeTrainerNames()) {
                romHandler.randomizeTrainerNames(settings.getCustomNames());
            }
        }

        maybeLogTrainerChanges(log, romHandler);

        // Apply metronome only mode now that trainers have been dealt with
        if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
            romHandler.metronomeOnlyMode();
        }

        List<Trainer> trainers = romHandler.getTrainers();
        for (Trainer t : trainers) {
            for (TrainerPokemon tpk : t.pokemon) {
                checkValue = addToCV(checkValue, tpk.level, tpk.pokemon.number);
            }
        }

        // Static Pokemon
        checkValue = maybeChangeAndLogStaticPokemon(log, romHandler, checkValue);

        if (romHandler.generationOfPokemon() == 7) {
            checkValue = maybeChangeAndLogTotemPokemon(log, romHandler, checkValue);
        }

        // Wild Pokemon
        if (settings.isUseMinimumCatchRate()) {
            boolean gen5 = romHandler instanceof Gen5RomHandler;
            int normalMin, legendaryMin;
            switch (settings.getMinimumCatchRateLevel()) {
            case 1:
            default:
                normalMin = gen5 ? 50 : 75;
                legendaryMin = gen5 ? 25 : 37;
                break;
            case 2:
                normalMin = gen5 ? 100 : 128;
                legendaryMin = gen5 ? 45 : 64;
                break;
            case 3:
                normalMin = gen5 ? 180 : 200;
                legendaryMin = gen5 ? 75 : 100;
                break;
            case 4:
                normalMin = legendaryMin = 255;
                break;
            }
            romHandler.minimumCatchRate(normalMin, legendaryMin);
        }

        switch (settings.getWildPokemonMod()) {
        case RANDOM:
            romHandler.randomEncounters(settings.isUseTimeBasedEncounters(),
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.CATCH_EM_ALL,
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.TYPE_THEME_AREAS,
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.SIMILAR_STRENGTH,
                    settings.isBlockWildLegendaries(),
                    settings.isBalanceShakingGrass(),
                    settings.isWildLevelsModified() ? settings.getWildLevelModifier() : 0, settings.isAllowWildAltFormes(),
                    settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE);
            break;
        case AREA_MAPPING:
            romHandler.area1to1Encounters(settings.isUseTimeBasedEncounters(),
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.CATCH_EM_ALL,
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.TYPE_THEME_AREAS,
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.SIMILAR_STRENGTH,
                    settings.isBlockWildLegendaries(),
                    settings.isWildLevelsModified() ? settings.getWildLevelModifier() : 0, settings.isAllowWildAltFormes(),
                    settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE);
            break;
        case GLOBAL_MAPPING:
            romHandler.game1to1Encounters(settings.isUseTimeBasedEncounters(),
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.SIMILAR_STRENGTH,
                    settings.isBlockWildLegendaries(),
                    settings.isWildLevelsModified() ? settings.getWildLevelModifier() : 0, settings.isAllowWildAltFormes(),
                    settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE);
            break;
        default:
            if (settings.isWildLevelsModified()) {
                romHandler.onlyChangeWildLevels(settings.getWildLevelModifier());
            }
            break;
        }

        maybeLogWildPokemonChanges(log, romHandler);
        boolean useTimeBasedEncounters = settings.isUseTimeBasedEncounters() ||
                (settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED && settings.isWildLevelsModified());
        List<EncounterSet> encounters = romHandler.getEncounters(useTimeBasedEncounters);
        for (EncounterSet es : encounters) {
            for (Encounter e : es.encounters) {
                checkValue = addToCV(checkValue, e.level, e.pokemon.number);
            }
        }

        // TMs
        boolean noBrokenTMMoves = settings.isBlockBrokenTMMoves();
        if (!(settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY)
                && settings.getTmsMod() == Settings.TMsMod.RANDOM) {
            double goodDamagingProb = settings.isTmsForceGoodDamaging() ? settings.getTmsGoodDamagingPercent() / 100.0
                    : 0;
            romHandler.randomizeTMMoves(noBrokenTMMoves, settings.isKeepFieldMoveTMs(), goodDamagingProb);
            log.println("--TM Moves--");
            List<Integer> tmMoves = romHandler.getTMMoves();
            for (int i = 0; i < tmMoves.size(); i++) {
                log.printf("TM%02d %s" + NEWLINE, i + 1, moves.get(tmMoves.get(i)).name);
                checkValue = addToCV(checkValue, tmMoves.get(i));
            }
            log.println();
        } else if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
            log.println("TM Moves: Metronome Only." + NEWLINE);
        } else {
            log.println("TM Moves: Unchanged." + NEWLINE);
        }

        boolean tmsHmsCompatChanged = false;
        // TM/HM compatibility
        switch (settings.getTmsHmsCompatibilityMod()) {
        case RANDOM_PREFER_TYPE:
            romHandler.randomizeTMHMCompatibility(true, settings.isTmsFollowEvolutions());
            tmsHmsCompatChanged = true;
            break;
        case COMPLETELY_RANDOM:
            romHandler.randomizeTMHMCompatibility(false, settings.isTmsFollowEvolutions());
            tmsHmsCompatChanged = true;
            break;
        case FULL:
            romHandler.fullTMHMCompatibility();
            tmsHmsCompatChanged = true;
            break;
        default:
            break;
        }

        if (settings.isTmLevelUpMoveSanity()) {
            romHandler.ensureTMCompatSanity();
            if (settings.isTmsFollowEvolutions()) {
                romHandler.ensureTMEvolutionSanity();
            }
            tmsHmsCompatChanged = true;
        }

        if (settings.isFullHMCompat()) {
            romHandler.fullHMCompatibility();
            tmsHmsCompatChanged = true;
        }

        // Copy TM/HM compatibility to cosmetic formes if it was changed at all, and log changes
        if (tmsHmsCompatChanged) {
            romHandler.copyTMCompatibilityToCosmeticFormes();
            maybeLogTMHMCompatibility(log, romHandler);
        }

        // Move Tutors (new 1.0.3)
        if (romHandler.hasMoveTutors()) {
            boolean noBrokenTutorMoves = settings.isBlockBrokenTutorMoves();
            if (!(settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY)
                    && settings.getMoveTutorMovesMod() == Settings.MoveTutorMovesMod.RANDOM) {
                List<Integer> oldMtMoves = romHandler.getMoveTutorMoves();
                double goodDamagingProb = settings.isTutorsForceGoodDamaging() ? settings
                        .getTutorsGoodDamagingPercent() / 100.0 : 0;
                romHandler.randomizeMoveTutorMoves(noBrokenTutorMoves, settings.isKeepFieldMoveTutors(), goodDamagingProb);
                log.println("--Move Tutor Moves--");
                List<Integer> newMtMoves = romHandler.getMoveTutorMoves();
                for (int i = 0; i < newMtMoves.size(); i++) {
                    log.printf("%-10s -> %-10s" + NEWLINE, moves.get(oldMtMoves.get(i)).name,
                            moves.get(newMtMoves.get(i)).name);
                    checkValue = addToCV(checkValue, newMtMoves.get(i));
                }
                log.println();
            } else if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
                log.println("Move Tutor Moves: Metronome Only." + NEWLINE);
            } else {
                log.println("Move Tutor Moves: Unchanged." + NEWLINE);
            }

            boolean tutorCompatChanged = false;
            // Compatibility
            switch (settings.getMoveTutorsCompatibilityMod()) {
            case RANDOM_PREFER_TYPE:
                romHandler.randomizeMoveTutorCompatibility(true, settings.isTutorFollowEvolutions());
                tutorCompatChanged = true;
                break;
            case COMPLETELY_RANDOM:
                romHandler.randomizeMoveTutorCompatibility(false, settings.isTutorFollowEvolutions());
                tutorCompatChanged = true;
                break;
            case FULL:
                romHandler.fullMoveTutorCompatibility();
                tutorCompatChanged = true;
                break;
            default:
                break;
            }

            if (settings.isTutorLevelUpMoveSanity()) {
                romHandler.ensureMoveTutorCompatSanity();
                if (settings.isTutorFollowEvolutions()) {
                    romHandler.ensureMoveTutorEvolutionSanity();
                }
                tutorCompatChanged = true;
            }

            // Copy move tutor compatibility to cosmetic formes if it was changed at all
            if (tutorCompatChanged) {
                romHandler.copyMoveTutorCompatibilityToCosmeticFormes();
                maybeLogTutorCompatibility(log,romHandler);
            }

        }

        // In-game trades
        List<IngameTrade> oldTrades = romHandler.getIngameTrades();
        if (settings.getInGameTradesMod() == Settings.InGameTradesMod.RANDOMIZE_GIVEN) {
            romHandler.randomizeIngameTrades(false, settings.isRandomizeInGameTradesNicknames(),
                    settings.isRandomizeInGameTradesOTs(), settings.isRandomizeInGameTradesIVs(),
                    settings.isRandomizeInGameTradesItems(), settings.getCustomNames());
        } else if (settings.getInGameTradesMod() == Settings.InGameTradesMod.RANDOMIZE_GIVEN_AND_REQUESTED) {
            romHandler.randomizeIngameTrades(true, settings.isRandomizeInGameTradesNicknames(),
                    settings.isRandomizeInGameTradesOTs(), settings.isRandomizeInGameTradesIVs(),
                    settings.isRandomizeInGameTradesItems(), settings.getCustomNames());
        }

        if (!(settings.getInGameTradesMod() == Settings.InGameTradesMod.UNCHANGED)) {
            log.println("--In-Game Trades--");
            List<IngameTrade> newTrades = romHandler.getIngameTrades();
            int size = oldTrades.size();
            for (int i = 0; i < size; i++) {
                IngameTrade oldT = oldTrades.get(i);
                IngameTrade newT = newTrades.get(i);
                log.printf("Trade %-11s -> %-11s the %-11s        ->      %-11s -> %-15s the %s" + NEWLINE,
                        oldT.requestedPokemon != null ? oldT.requestedPokemon.fullName() : "Any",
                        oldT.nickname, oldT.givenPokemon.fullName(),
                        newT.requestedPokemon != null ? newT.requestedPokemon.fullName() : "Any",
                        newT.nickname, newT.givenPokemon.fullName());
            }
            log.println();
        }

        // Field Items
        if (settings.getFieldItemsMod() == Settings.FieldItemsMod.SHUFFLE) {
            romHandler.shuffleFieldItems();
        } else if (settings.getFieldItemsMod() == Settings.FieldItemsMod.RANDOM) {
            romHandler.randomizeFieldItems(settings.isBanBadRandomFieldItems(), settings.isBanRandomPlatesAndIncense(), false);
        } else if (settings.getFieldItemsMod() == Settings.FieldItemsMod.RANDOM_EVEN) {
            romHandler.randomizeFieldItems(settings.isBanBadRandomFieldItems(), settings.isBanRandomPlatesAndIncense(), true);
        }

        if (settings.getShopItemsMod() == Settings.ShopItemsMod.SHUFFLE) {
            romHandler.shuffleShopItems();
        } else if (settings.getShopItemsMod() == Settings.ShopItemsMod.RANDOM) {
            romHandler.randomizeShopItems(settings.isBanBadRandomShopItems(), settings.isBanRegularShopItems(), settings.isBanOPShopItems(),
                    settings.isBalanceShopPrices(), settings.isGuaranteeEvolutionItems(), settings.isGuaranteeXItems());
        }
        
        // Shops
        if ((settings.getShopItemsMod() == Settings.ShopItemsMod.RANDOM || settings.getShopItemsMod() == Settings.ShopItemsMod.SHUFFLE)
                && this.romHandler.generationOfPokemon() >= 4) {
            maybeLogShops(log, romHandler);
        }
        // Test output for placement history
        // romHandler.renderPlacementHistory();

        // Signature...
        romHandler.applySignature();

        // Record check value?
        romHandler.writeCheckValueToROM(checkValue);

        // Save
        if (saveAsDirectory) {
            romHandler.saveRomDirectory(filename);
        } else {
            romHandler.saveRomFile(filename, seed);
        }

        // Log tail
        String gameName = romHandler.getROMName();
        if (romHandler.hasGameUpdateLoaded()) {
            gameName = gameName + " (" + romHandler.getGameUpdateVersion() + ")";
        }
        log.println("------------------------------------------------------------------");
        log.println("Randomization of " + gameName + " completed.");
        log.println("Time elapsed: " + (System.currentTimeMillis() - startTime) + "ms");
        log.println("RNG Calls: " + RandomSource.callsSinceSeed());
        log.println("------------------------------------------------------------------");
        log.println();

        // Diagnostics
        log.println("--ROM Diagnostics--");
        romHandler.printRomDiagnostics(log);

        return checkValue;
    }

    private void maybeLogBaseStatAndTypeChanges(final PrintStream log, final RomHandler romHandler) {
        List<Pokemon> allPokes = romHandler.getPokemonInclFormes();
        String[] itemNames = romHandler.getItemNames();
        // Log base stats & types if changed at all
        if (settings.getBaseStatisticsMod() == Settings.BaseStatisticsMod.UNCHANGED
                && settings.getTypesMod() == Settings.TypesMod.UNCHANGED
                && settings.getAbilitiesMod() == Settings.AbilitiesMod.UNCHANGED
                && !settings.isRandomizeWildPokemonHeldItems()) {
            log.println("Pokemon base stats & type: unchanged" + NEWLINE);
        } else {
            log.println("--Pokemon Base Stats & Types--");
            if (romHandler instanceof Gen1RomHandler) {
                log.println("NUM|NAME         |TYPE             |  HP| ATK| DEF| SPE|SPEC");
                for (Pokemon pkmn : allPokes) {
                    if (pkmn != null) {
                        String typeString = pkmn.primaryType == null ? "???" : pkmn.primaryType.toString();
                        if (pkmn.secondaryType != null) {
                            typeString += "/" + pkmn.secondaryType.toString();
                        }
                        log.printf("%3d|%-10s|%-17s|%4d|%4d|%4d|%4d|%4d" + NEWLINE, pkmn.number, pkmn.fullName(), typeString,
                                pkmn.hp, pkmn.attack, pkmn.defense, pkmn.speed, pkmn.spatk, pkmn.spdef );
                    }

                }
            } else {
                String nameSp = "      ";
                String nameSpFormat = "%-13s";
                String abSp = "    ";
                String abSpFormat = "%-12s";
                if (romHandler.generationOfPokemon() == 5) {
                    nameSp = "         ";
                } else if (romHandler.generationOfPokemon() == 6) {
                    nameSp = "            ";
                    nameSpFormat = "%-16s";
                    abSp = "      ";
                    abSpFormat = "%-14s";
                } else if (romHandler.generationOfPokemon() >= 7) {
                    nameSp = "            ";
                    nameSpFormat = "%-16s";
                    abSp = "        ";
                    abSpFormat = "%-16s";
                }

                log.print("NUM|NAME" + nameSp + "|TYPE             |  HP| ATK| DEF|SATK|SDEF| SPD");
                int abils = romHandler.abilitiesPerPokemon();
                for (int i = 0; i < abils; i++) {
                    log.print("|ABILITY" + (i + 1) + abSp);
                }
                log.print("|ITEM");
                log.println();
                int i = 0;
                for (Pokemon pkmn : allPokes) {
                    if (pkmn != null && !pkmn.actuallyCosmetic) {
                        i++;
                        String typeString = pkmn.primaryType == null ? "???" : pkmn.primaryType.toString();
                        if (pkmn.secondaryType != null) {
                            typeString += "/" + pkmn.secondaryType.toString();
                        }
                        log.printf("%3d|" + nameSpFormat + "|%-17s|%4d|%4d|%4d|%4d|%4d|%4d", i, pkmn.fullName(), typeString,
                                pkmn.hp, pkmn.attack, pkmn.defense, pkmn.spatk, pkmn.spdef, pkmn.speed);
                        if (abils > 0) {
                            log.printf("|" + abSpFormat + "|" + abSpFormat, romHandler.abilityName(pkmn.ability1),
                                    pkmn.ability1 == pkmn.ability2 ? "--" : romHandler.abilityName(pkmn.ability2));
                            if (abils > 2) {
                                log.printf("|" + abSpFormat, romHandler.abilityName(pkmn.ability3));
                            }
                        }
                        log.print("|");
                        if (pkmn.guaranteedHeldItem > 0) {
                            log.print(itemNames[pkmn.guaranteedHeldItem] + " (100%)");
                        } else {
                            int itemCount = 0;
                            if (pkmn.commonHeldItem > 0) {
                                itemCount++;
                                log.print(itemNames[pkmn.commonHeldItem] + " (common)");
                            }
                            if (pkmn.rareHeldItem > 0) {
                                if (itemCount > 0) {
                                    log.print(", ");
                                }
                                itemCount++;
                                log.print(itemNames[pkmn.rareHeldItem] + " (rare)");
                            }
                            if (pkmn.darkGrassHeldItem > 0) {
                                if (itemCount > 0) {
                                    log.print(", ");
                                }
                                log.print(itemNames[pkmn.darkGrassHeldItem] + " (dark grass only)");
                            }
                        }
                        log.println();
                    }

                }
            }
            log.println();
        }
    }

    private void maybeLogTMHMCompatibility(final PrintStream log, final RomHandler romHandler) {
        if (settings.getTmsHmsCompatibilityMod() != Settings.TMsHMsCompatibilityMod.UNCHANGED) {
            log.println("--TM Compatibility--");
            Map<Pokemon, boolean[]> compat = romHandler.getTMHMCompatibility();
            List<Integer> tmHMs = new ArrayList<>(romHandler.getTMMoves());
            tmHMs.addAll(romHandler.getHMMoves());
            List<Move> moveData = romHandler.getMoves();

            logCompatibility(log, compat, tmHMs, moveData, true);
        }
    }

    private void maybeLogTutorCompatibility(final PrintStream log, final RomHandler romHandler) {
        if (settings.getTmsHmsCompatibilityMod() != Settings.TMsHMsCompatibilityMod.UNCHANGED) {
            log.println("--Move Tutor Compatibility--");
            Map<Pokemon, boolean[]> compat = romHandler.getMoveTutorCompatibility();
            List<Integer> tutorMoves = romHandler.getMoveTutorMoves();
            List<Move> moveData = romHandler.getMoves();

            logCompatibility(log, compat, tutorMoves, moveData, false);
        }
    }

    private void logCompatibility(final PrintStream log, Map<Pokemon, boolean[]> compat, List<Integer> moveList,
                                  List<Move> moveData, boolean includeTMNumber) {
        int tmCount = romHandler.getTMCount();
        for (Map.Entry<Pokemon, boolean[]> entry : compat.entrySet()) {
            Pokemon pkmn = entry.getKey();
            if (pkmn.actuallyCosmetic) continue;
            boolean[] flags = entry.getValue();

            String nameSpFormat = "%-14s";
            if (romHandler.generationOfPokemon() >= 6) {
                nameSpFormat = "%-17s";
            }
            log.printf("%3d " + nameSpFormat, pkmn.number, pkmn.fullName() + " ");

            for (int i = 1; i < flags.length; i++) {

                int moveNameLength = moveData.get(moveList.get(i - 1)).name.length();
                if (flags[i]) {
                    if (includeTMNumber) {
                        if (i <= tmCount) {
                            log.printf("|TM%02d %" + moveNameLength + "s ", i, moveData.get(moveList.get(i - 1)).name);
                        } else {
                            log.printf("|HM%02d %" + moveNameLength + "s ", i-tmCount, moveData.get(moveList.get(i - 1)).name);
                        }
                    } else {
                        log.printf("|%" + moveNameLength + "s ", moveData.get(moveList.get(i - 1)).name);
                    }
                } else {
                    if (includeTMNumber) {
                        log.printf("| %" + (moveNameLength+4) + "s ", "-");
                    } else {
                        log.printf("| %" + (moveNameLength-1) + "s ", "-");
                    }
                }
            }
            log.println("|");
        }
        log.println("");
    }

    private void maybeChangeAndLogStarters(final PrintStream log, final RomHandler romHandler) {
        if (romHandler.canChangeStarters()) {
            if (settings.getStartersMod() == Settings.StartersMod.CUSTOM) {
                log.println("--Custom Starters--");
                List<Pokemon> romPokemon = romHandler.getPokemonInclFormes()
                                        .stream()
                                        .filter(pk -> pk == null || !pk.actuallyCosmetic)
                                        .collect(Collectors.toList());
                int[] customStarters = settings.getCustomStarters();
                Pokemon pkmn1 = romPokemon.get(customStarters[0]);
                log.println("Set starter 1 to " + pkmn1.fullName());
                Pokemon pkmn2 = romPokemon.get(customStarters[1]);
                log.println("Set starter 2 to " + pkmn2.fullName());
                if (romHandler.isYellow()) {
                    romHandler.setStarters(Arrays.asList(pkmn1, pkmn2));
                } else {
                    Pokemon pkmn3 = romPokemon.get(customStarters[2]);
                    log.println("Set starter 3 to " + pkmn3.fullName());
                    if (romHandler.starterCount() > 3) {
                        List<Pokemon> starters = new ArrayList<>();
                        starters.add(pkmn1);
                        starters.add(pkmn2);
                        starters.add(pkmn3);
                        for (int i = 3; i < romHandler.starterCount(); i++) {
                            Pokemon pkmn = romHandler.random2EvosPokemon(settings.isAllowStarterAltFormes());
                            while (starters.contains(pkmn)) {
                                pkmn = romHandler.random2EvosPokemon(settings.isAllowStarterAltFormes());
                            }
                            log.println("Set starter " + i + " to " + pkmn.fullName());
                            starters.add(pkmn);
                        }
                        romHandler.setStarters(starters);
                    } else {
                        romHandler.setStarters(Arrays.asList(pkmn1, pkmn2, pkmn3));
                    }
                }
                log.println();

            } else if (settings.getStartersMod() == Settings.StartersMod.COMPLETELY_RANDOM) {
                // Randomise
                log.println("--Random Starters--");
                int starterCount = romHandler.starterCount();
                List<Pokemon> starters = new ArrayList<>();
                List<Pokemon> banned = romHandler.getBannedFormes();
                if (settings.getAbilitiesMod() == Settings.AbilitiesMod.UNCHANGED) {
                    List<Pokemon> abilityDependentFormes = romHandler.getAbilityDependentFormes();
                    banned.addAll(abilityDependentFormes);
                }
                for (int i = 0; i < starterCount; i++) {
                    Pokemon pkmn =
                            settings.isAllowStarterAltFormes() ?
                                    romHandler.randomPokemonInclFormes() :
                                    romHandler.randomPokemon();
                    while (starters.contains(pkmn) || banned.contains(pkmn) || pkmn.actuallyCosmetic) {
                        pkmn = settings.isAllowStarterAltFormes() ?
                                    romHandler.randomPokemonInclFormes() :
                                    romHandler.randomPokemon();
                    }
                    log.println("Set starter " + (i + 1) + " to " + pkmn.fullName());
                    starters.add(pkmn);
                }
                romHandler.setStarters(starters);
                log.println();
            } else if (settings.getStartersMod() == Settings.StartersMod.RANDOM_WITH_TWO_EVOLUTIONS) {
                // Randomise
                log.println("--Random 2-Evolution Starters--");
                int starterCount = romHandler.starterCount();
                List<Pokemon> starters = new ArrayList<>();
                List<Pokemon> banned = romHandler.getBannedFormes();
                if (settings.getAbilitiesMod() == Settings.AbilitiesMod.UNCHANGED) {
                    List<Pokemon> abilityDependentFormes = romHandler.getAbilityDependentFormes();
                    banned.addAll(abilityDependentFormes);
                }
                for (int i = 0; i < starterCount; i++) {
                    Pokemon pkmn = romHandler.random2EvosPokemon(settings.isAllowStarterAltFormes());
                    while (starters.contains(pkmn) || banned.contains(pkmn)) {
                        pkmn = romHandler.random2EvosPokemon(settings.isAllowStarterAltFormes());
                    }
                    log.println("Set starter " + (i + 1) + " to " + pkmn.fullName());
                    starters.add(pkmn);
                }
                romHandler.setStarters(starters);
                log.println();
            }
            if (settings.isRandomizeStartersHeldItems() && !(romHandler instanceof Gen1RomHandler)) {
                romHandler.randomizeStarterHeldItems(settings.isBanBadRandomStarterHeldItems());
            }
        }
    }

    private void maybeLogWildPokemonChanges(final PrintStream log, final RomHandler romHandler) {
        if (settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED && !settings.isWildLevelsModified()) {
            log.println("Wild Pokemon: Unchanged." + NEWLINE);
        } else {
            log.println("--Wild Pokemon--");
            boolean useTimeBasedEncounters = settings.isUseTimeBasedEncounters() ||
                    (settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED && settings.isWildLevelsModified());
            List<EncounterSet> encounters = romHandler.getEncounters(useTimeBasedEncounters);
            int idx = 0;
            for (EncounterSet es : encounters) {
                idx++;
                log.print("Set #" + idx + " ");
                if (es.displayName != null) {
                    log.print("- " + es.displayName + " ");
                }
                log.print("(rate=" + es.rate + ")");
                log.println();
                for (Encounter e : es.encounters) {
                
                // sb.append(String.format("%03d %s", pkmn.number, pkmn.fullName())).append(System.getProperty("line.separator")).append(String.format("HP %d ATK %-3d DEF %-3d SPATK %-3d SPDEF %-3d SPD %-3d", pkmn.hp, pkmn.attack, pkmn.defense, pkmn.speed, pkmn.spatk, pkmn.spdef)).append(System.getProperty("line.separator"));
                    StringBuilder sb = new StringBuilder();
                    if (e.isSOS) {
                        String stringToAppend;
                        switch (e.sosType) {
                            case RAIN:
                                stringToAppend = "Rain SOS: ";
                                break;
                            case HAIL:
                                stringToAppend = "Hail SOS: ";
                                break;
                            case SAND:
                                stringToAppend = "Sand SOS: ";
                                break;
                            default:
                                stringToAppend = "  SOS: ";
                                break;
                        }
                        sb.append(stringToAppend);
                    }
                    sb.append(e.pokemon.fullName()).append(" Lv");
                    if (e.maxLevel > 0 && e.maxLevel != e.level) {
                        sb.append("s ").append(e.level).append("-").append(e.maxLevel);
                    } else {
                        sb.append(e.level);
                    }
                    String whitespaceFormat = romHandler.generationOfPokemon() == 7 ? "%-31s" : "%-25s";
                    log.print(String.format(whitespaceFormat, sb));
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(String.format("HP %-3d ATK %-3d DEF %-3d SPATK %-3d SPDEF %-3d SPEED %-3d", e.pokemon.hp, e.pokemon.attack, e.pokemon.defense, e.pokemon.spatk, e.pokemon.spdef, e.pokemon.speed));
                    log.print(sb2);
                    log.println();
                }
                log.println();
            }
            log.println();
        }
    }

    private void maybeLogTrainerChanges(final PrintStream log, final RomHandler romHandler) {
        if (settings.getTrainersMod() == Settings.TrainersMod.UNCHANGED && !settings.isRivalCarriesStarterThroughout()
                && !settings.isTrainersLevelModified() && !settings.isTrainersForceFullyEvolved()) {
            log.println("Trainers: Unchanged." + NEWLINE);
        } else {
            log.println("--Trainers Pokemon--");
            List<Trainer> trainers = romHandler.getTrainers();
            int idx = 0;
            for (Trainer t : trainers) {
                idx++;
                log.print("#" + idx + " ");
                if (t.fullDisplayName != null) {
                    log.print("(" + t.fullDisplayName + ")");
                } else if (t.name != null) {
                    log.print("(" + t.name + ")");
                }
                if (t.offset != idx && t.offset != 0) {
                    log.printf("@%X", t.offset);
                }
                log.print(" - ");
                boolean first = true;
                for (TrainerPokemon tpk : t.pokemon) {
                    if (!first) {
                        log.print(", ");
                    }
                    log.print(tpk.toString());
                    first = false;
                }
                log.println();
            }
            log.println();
        }
    }

    private int maybeChangeAndLogStaticPokemon(final PrintStream log, final RomHandler romHandler, int checkValue) {
        if (romHandler.canChangeStaticPokemon()) {
            List<StaticEncounter> oldStatics = romHandler.getStaticPokemon();
            if (settings.getStaticPokemonMod() == Settings.StaticPokemonMod.RANDOM_MATCHING) { // Legendary for L
                romHandler.randomizeStaticPokemon(true, false,settings.isLimitMainGameLegendaries(),
                        settings.isLimit600(), settings.isAllowStaticAltFormes(), settings.isSwapStaticMegaEvos(),
                        settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE,
                        settings.isStaticLevelModified() ? settings.getStaticLevelModifier() : 0);
            } else if (settings.getStaticPokemonMod() == Settings.StaticPokemonMod.COMPLETELY_RANDOM) {
                romHandler.randomizeStaticPokemon(false, false,settings.isLimitMainGameLegendaries(),
                        settings.isLimit600(), settings.isAllowStaticAltFormes(), settings.isSwapStaticMegaEvos(),
                        settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE,
                        settings.isStaticLevelModified() ? settings.getStaticLevelModifier() : 0);
            } else if (settings.getStaticPokemonMod() == Settings.StaticPokemonMod.SIMILAR_STRENGTH) {
                romHandler.randomizeStaticPokemon(false, true,settings.isLimitMainGameLegendaries(),
                        settings.isLimit600(), settings.isAllowStaticAltFormes(), settings.isSwapStaticMegaEvos(),
                        settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE,
                        settings.isStaticLevelModified() ? settings.getStaticLevelModifier() : 0);
            } else if (settings.isStaticLevelModified()) {
                romHandler.onlyChangeStaticLevels(settings.getStaticLevelModifier());
            }
            List<StaticEncounter> newStatics = romHandler.getStaticPokemon();
            if (settings.getStaticPokemonMod() == Settings.StaticPokemonMod.UNCHANGED && !settings.isStaticLevelModified()) {
                log.println("Static Pokemon: Unchanged." + NEWLINE);
            } else {
                log.println("--Static Pokemon--");
                Map<String, Integer> seenPokemon = new TreeMap<>();
                for (int i = 0; i < oldStatics.size(); i++) {
                    StaticEncounter oldP = oldStatics.get(i);
                    StaticEncounter newP = newStatics.get(i);
                    checkValue = addToCV(checkValue, newP.pkmn.number);
                    String oldStaticString = oldP.toString(settings.isStaticLevelModified());
                    log.print(oldStaticString);
                    if (seenPokemon.containsKey(oldStaticString)) {
                        int amount = seenPokemon.get(oldStaticString);
                        log.print("(" + (++amount) + ")");
                        seenPokemon.put(oldStaticString, amount);
                    } else {
                        seenPokemon.put(oldStaticString, 1);
                    }
                    log.println(" => " + newP.toString(settings.isStaticLevelModified()));
                }
                log.println();
            }
        }
        return checkValue;
    }

    private int maybeChangeAndLogTotemPokemon(final PrintStream log, final RomHandler romHandler, int checkValue) {
        if (settings.getTotemPokemonMod() != Settings.TotemPokemonMod.UNCHANGED ||
                settings.getAllyPokemonMod() != Settings.AllyPokemonMod.UNCHANGED ||
                settings.getAuraMod() != Settings.AuraMod.UNCHANGED ||
                settings.isRandomizeTotemHeldItems() ||
                settings.isTotemLevelsModified()) {

            List<TotemPokemon> oldTotems = romHandler.getTotemPokemon();
            boolean randomizeTotems =
                    settings.getTotemPokemonMod() == Settings.TotemPokemonMod.RANDOM ||
                            settings.getTotemPokemonMod() == Settings.TotemPokemonMod.SIMILAR_STRENGTH;
            boolean randomizeAllies =
                    settings.getAllyPokemonMod() == Settings.AllyPokemonMod.RANDOM ||
                            settings.getAllyPokemonMod() == Settings.AllyPokemonMod.SIMILAR_STRENGTH;
            boolean randomizeAuras =
                    settings.getAuraMod() == Settings.AuraMod.RANDOM ||
                            settings.getAuraMod() == Settings.AuraMod.SAME_STRENGTH;
            romHandler.randomizeTotemPokemon(
                    randomizeTotems,
                    settings.getTotemPokemonMod() == Settings.TotemPokemonMod.SIMILAR_STRENGTH,
                    randomizeAllies,
                    settings.getAllyPokemonMod() == Settings.AllyPokemonMod.SIMILAR_STRENGTH,
                    randomizeAuras,
                    settings.getAuraMod() == Settings.AuraMod.SAME_STRENGTH,
                    settings.isRandomizeTotemHeldItems(),
                    settings.isTotemLevelsModified() ? settings.getTotemLevelModifier() : 0,
                    settings.isAllowTotemAltFormes(),
                    settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE
            );
            List<TotemPokemon> newTotems = romHandler.getTotemPokemon();

            String[] itemNames = romHandler.getItemNames();
            log.println("--Totem Pokemon--");
            for (int i = 0; i < oldTotems.size(); i++) {
                TotemPokemon oldP = oldTotems.get(i);
                TotemPokemon newP = newTotems.get(i);
                checkValue = addToCV(checkValue, newP.pkmn.number);
                log.println(oldP.pkmn.fullName() + " =>");
                log.printf(newP.toString(),itemNames[newP.heldItem]);
            }
            log.println();
        } else {
            log.println("Totem Pokemon: Unchanged." + NEWLINE);
        }

        return checkValue;
    }

    private void maybeLogMoveChanges(final PrintStream log, final RomHandler romHandler) {
        if (!settings.isRandomizeMoveAccuracies() && !settings.isRandomizeMovePowers()
                && !settings.isRandomizeMovePPs() && !settings.isRandomizeMoveCategory()
                && !settings.isRandomizeMoveTypes()) {
            if (!settings.isUpdateMoves()) {
                log.println("Move Data: Unchanged." + NEWLINE);
            }
        } else {
            log.println("--Move Data--");
            log.print("NUM|NAME           |TYPE    |POWER|ACC.|PP");
            if (romHandler.hasPhysicalSpecialSplit()) {
                log.print(" |CATEGORY");
            }
            log.println();
            List<Move> allMoves = romHandler.getMoves();
            for (Move mv : allMoves) {
                if (mv != null) {
                    String mvType = (mv.type == null) ? "???" : mv.type.toString();
                    log.printf("%3d|%-15s|%-8s|%5d|%4d|%3d", mv.internalId, mv.name, mvType, mv.power,
                            (int) mv.hitratio, mv.pp);
                    if (romHandler.hasPhysicalSpecialSplit()) {
                        log.printf("| %s", mv.category.toString());
                    }
                    log.println();
                }
            }
            log.println();
        }
    }

    private void maybeLogShops(final PrintStream log, final RomHandler romHandler) {
        String[] shopNames = romHandler.getShopNames();
        String[] itemNames = romHandler.getItemNames();
        log.println("--Shops--");
        Map<Integer, List<Integer>> shopsDict = romHandler.getShopItems();
        for (int shopID : shopsDict.keySet()) {
            log.printf("%s", shopNames[shopID]);
            log.println();
            List<Integer> shopItems = shopsDict.get(shopID);
            for (int shopItemID : shopItems) {
                log.printf("- %5s", itemNames[shopItemID]);
                log.println();
            }
            
            log.println();
        }
        log.println();
    }

    
    private static int addToCV(int checkValue, int... values) {
        for (int value : values) {
            checkValue = Integer.rotateLeft(checkValue, 3);
            checkValue ^= value;
        }
        return checkValue;
    }
}