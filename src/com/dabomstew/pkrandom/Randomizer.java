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
import java.util.*;
import java.util.stream.Collectors;

import com.dabomstew.pkrandom.pokemon.*;
import com.dabomstew.pkrandom.romhandlers.Gen1RomHandler;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

// Can randomize a file based on settings. Output varies by seed.
public class Randomizer {

    private static final String NEWLINE = System.getProperty("line.separator");

    private final Settings settings;
    private final RomHandler romHandler;
    private List<Trainer> originalTrainers = new ArrayList<Trainer>();
    private final ResourceBundle bundle;
    private final boolean saveAsDirectory;

    public Randomizer(Settings settings, RomHandler romHandler, ResourceBundle bundle, boolean saveAsDirectory) {
        this.settings = settings;
        this.romHandler = romHandler;
        this.bundle = bundle;
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
        RandomSource.seed(seed);

        int checkValue = 0;



        startLog(log);

        log.println("<!---");
        log.println("Randomizer Version: " + Version.VERSION_STRING);
        log.println("Random Seed: " + seed);
        log.println("Settings String: " + Version.VERSION + settings.toString());
        log.println();

        boolean logAll = settings.isLogAll();

        // All possible changes that can be logged
        boolean movesUpdated = false;
        boolean startersChanged = false;

        boolean movesChanged = logAll;
        boolean movesetsChanged = logAll;
        boolean pokemonTraitsChanged = logAll;
        boolean evolutionsChanged = logAll;
        boolean trainersChanged = logAll;
        boolean trainerMovesetsChanged = logAll;
        boolean staticsChanged = logAll;
        boolean totemsChanged = logAll;
        boolean wildsChanged = logAll;
        boolean wildGlobal = false;
        boolean tmMovesChanged = logAll;
        boolean moveTutorMovesChanged = logAll;
        boolean tradesChanged = logAll;
        boolean tmsHmsCompatChanged = logAll;
        boolean tutorCompatChanged = logAll;
        boolean shopsChanged = logAll;
        boolean pickUpChanged = logAll;

        romHandler.findInitialBSTRange();
        romHandler.organizeBSTs(true);
        romHandler.findEvoPowerLevels(true);

        // Limit Pokemon
        // 1. Set Pokemon pool according to limits (or lack thereof)
        // 2. If limited, remove evolutions that are outside of the pool

        romHandler.setPokemonPool(settings);

        if (settings.isLimitPokemon()) {
            romHandler.removeEvosForPokemonPool();
        }

        // Move updates & data changes
        // 1. Update moves to a future generation
        // 2. Randomize move stats

        romHandler.findMoveEfficacies();

        if (settings.isUpdateMoves()) {
            romHandler.initMoveUpdates();
            romHandler.updateMoves(settings);
            movesUpdated = true;
        }


        if (movesUpdated) {
            logMoveUpdates(log);
        }
        log.println();

        if (settings.isRandomizeMovePowers()) {
            romHandler.randomizeMovePowers();
            movesChanged = true;
        }

        if (settings.getMoveAccuracyMod() != Settings.MoveAccuracyMod.UNCHANGED) {
            romHandler.changeMoveAccuracies(settings);
            movesChanged = true;
        }

        if (settings.isRandomizeMovePPs()) {
            romHandler.randomizeMovePPs();
            movesChanged = true;
        }

        if (settings.isRandomizeMoveTypes()) {
            romHandler.randomizeMoveTypes();
            movesChanged = true;
        }

        if (settings.isRandomizeMoveCategory() && romHandler.hasPhysicalSpecialSplit()) {
            romHandler.randomizeMoveCategory();
            movesChanged = true;
        }

        // Misc Tweaks
        if (settings.getCurrentMiscTweaks() != MiscTweak.NO_MISC_TWEAKS) {
            romHandler.applyMiscTweaks(settings);
        }

        // Update base stats to a future generation
        if (settings.isUpdateBaseStats()) {
            romHandler.updatePokemonStats(settings);
            pokemonTraitsChanged = true;
        }


        // If Random Types Follow Evolutions, randomize Evolutions first so that pokemon in
        // a base evolution chain don't share their types anymore
        // Otherwise, randomize types first so that evolutions can be chosen based
        // on those randomized types
        if (settings.getTypesMod() == Settings.TypesMod.RANDOM_FOLLOW_EVOLUTIONS) {
            // Random Evos
            if (settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM) {
                romHandler.randomizeEvolutions(settings);
                evolutionsChanged = true;
            } else if (settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM_EVERY_LEVEL) {
                romHandler.randomizeEvolutionsEveryLevel(settings);
                evolutionsChanged = true;
            }

            // Randomize Types
            romHandler.randomizePokemonTypes(settings);
            pokemonTraitsChanged = true;

        } else {
            // Random Types
            if (settings.getTypesMod() != Settings.TypesMod.UNCHANGED) {
                romHandler.randomizePokemonTypes(settings);
                pokemonTraitsChanged = true;
            }

            // Random Evos
            if (settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM) {
                romHandler.randomizeEvolutions(settings);
                evolutionsChanged = true;
            } else if (settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM_EVERY_LEVEL) {
                romHandler.randomizeEvolutionsEveryLevel(settings);
                evolutionsChanged = true;
            }
        }

        romHandler.findBasePokemon();
        romHandler.findEvolvingPokemon();
        romHandler.findFinalEvolutionPokemon();

        // Wild Held Items
        if (settings.isRandomizeWildPokemonHeldItems()) {
            romHandler.randomizeWildHeldItems(settings);
            pokemonTraitsChanged = true;
        }

        // Base stat randomization
        switch (settings.getBSTMod()) {
            case SHUFFLE:
                romHandler.shufflePokemonBSTs(settings);
                pokemonTraitsChanged = true;
                break;
            case RANDOM:
                romHandler.randomizePokemonBSTs(settings);
                pokemonTraitsChanged = true;
                break;
            default:
                break;
        }

        // Base stat randomization
        switch (settings.getBaseStatisticsMod()) {
            case SHUFFLE:
                romHandler.shufflePokemonStats(settings);
                pokemonTraitsChanged = true;
                break;
            case RANDOM:
                romHandler.randomizePokemonStats(settings);
                pokemonTraitsChanged = true;
                break;
            default:
                break;
        }

        if (settings.isMinimumBST() || settings.isMaximumBST()) {
            romHandler.scalePokemonStats(settings);
            pokemonTraitsChanged = true;
        }
        if (settings.isBSTRounding()) {
            romHandler.roundPokemonStats(settings);
            pokemonTraitsChanged = true;
        }

        romHandler.organizeBSTs(false);
        romHandler.findEvoPowerLevels(false);
        romHandler.findRandomizedBSTRange();
        romHandler.findBigPokemon();

        // Adjust Exp Yield of Pokemon (Gen 3 and 4 only)
        if (settings.isAdjustEXPYields()) {
            romHandler.adjustExpYield(settings);
        }

        // Standardize EXP curves
        if (settings.isStandardizeEXPCurves()) {
            romHandler.standardizeEXPCurves(settings);
        }

        if (settings.isScaleCatchRates()) {
            romHandler.scaleCatchRates();
        }

        if (settings.isUseMinimumCatchRate()) {
            romHandler.changeCatchRates(settings);
        }

        // Abilities
        if (settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE) {
            romHandler.randomizeAbilities(settings);
            pokemonTraitsChanged = true;
        }



        for (Pokemon pkmn : romHandler.getPokemon()) {
            if (pkmn != null) {
                checkValue = addToCV(checkValue, pkmn.hp, pkmn.attack, pkmn.defense, pkmn.speed, pkmn.spatk,
                        pkmn.spdef, pkmn.ability1, pkmn.ability2, pkmn.ability3);
            }
        }

        // Trade evolutions removal
        if (settings.isChangeImpossibleEvolutions()) {
            romHandler.removeImpossibleEvolutions(settings);
            evolutionsChanged = true;
        }

        // Easier evolutions
        if (settings.isMakeEvolutionsEasier()) {
            romHandler.makeEvolutionsEasier(settings);
            evolutionsChanged = true;
        }

        // Easier evolutions
        if (settings.isSpaceEvolutionLevels()) {
            romHandler.spaceLevelEvolutions(settings);
            evolutionsChanged = true;
        }

        // Easier evolutions
        if (settings.isMakeEvolutionsEasier()) {
            romHandler.condenseLevelEvolutions(settings.getMaxEvolutionLevel());
        }

        // Remove time-based evolutions
        if (settings.isRemoveTimeBasedEvolutions()) {
            romHandler.removeTimeBasedEvolutions();
            evolutionsChanged = true;
        }



        // Starter Pokemon
        // Applied after type to update the strings correctly based on new types
        switch(settings.getStartersMod()) {
            case CUSTOM:
                romHandler.customStarters(settings);
                startersChanged = true;
                break;
            case RANDOM:
                romHandler.randomizeStarters(settings);
                startersChanged = true;
                break;
            default:
                break;
        }
        if (settings.isRandomizeStartersHeldItems() && !(romHandler instanceof Gen1RomHandler)) {
            romHandler.randomizeStarterHeldItems(settings);
        }



        // Movesets
        // 1. Randomize movesets
        // 2. Reorder moves by damage
        // Note: "Metronome only" is handled after trainers instead

        if (settings.getMovesetsMod() != Settings.MovesetsMod.UNCHANGED &&
                settings.getMovesetsMod() != Settings.MovesetsMod.METRONOME_ONLY) {
            romHandler.randomizeMovesLearnt(settings);
            romHandler.randomizeEggMoves(settings);
            movesetsChanged = true;
        }

        if (settings.isReorderDamagingMoves()) {
            romHandler.orderDamagingMovesByDamage();
            movesetsChanged = true;
        }




        // TMs

        if (!(settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY)
                && settings.getTmsMod() == Settings.TMsMod.RANDOM) {
            romHandler.randomizeTMMoves(settings);
            tmMovesChanged = true;
        }





        // TM/HM compatibility
        // 1. Randomize TM/HM compatibility
        // 2. Ensure levelup move sanity
        // 3. Follow evolutions
        // 4. Full HM compatibility
        // 5. Copy to cosmetic forms

        switch (settings.getTmsHmsCompatibilityMod()) {
            case COMPLETELY_RANDOM:
            case RANDOM_PREFER_TYPE:
                romHandler.randomizeTMHMCompatibility(settings);
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


        List<Integer> oldMtMoves = romHandler.getMoveTutorMoves();
        // Move Tutors
        if (romHandler.hasMoveTutors()) {

            if (!(settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY)
                    && settings.getMoveTutorMovesMod() == Settings.MoveTutorMovesMod.RANDOM) {

                romHandler.randomizeMoveTutorMoves(settings);
                moveTutorMovesChanged = true;
            }

            // Move Tutor Compatibility
            // 1. Randomize MT compatibility
            // 2. Ensure levelup move sanity
            // 3. Follow evolutions
            // 4. Copy to cosmetic forms

            switch (settings.getMoveTutorsCompatibilityMod()) {
                case COMPLETELY_RANDOM:
                case RANDOM_PREFER_TYPE:
                    romHandler.randomizeMoveTutorCompatibility(settings);
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



        }

        // Trainer Pokemon
        // 1. Add extra Trainer Pokemon
        // 2. Set trainers to be double battles and add extra Pokemon if necessary
        // 3. Randomize Trainer Pokemon
        // 4. Modify rivals to carry starters
        // 5. Force Trainer Pokemon to be fully evolved

        // Cache original trainers
        for (Trainer t : romHandler.getTrainers()) {
            if (!t.equals(null))
                originalTrainers.add(new Trainer(t));
        }

        if (settings.getAdditionalRegularTrainerPokemon() > 0
                || settings.getAdditionalImportantTrainerPokemon() > 0
                || settings.getAdditionalBossTrainerPokemon() > 0) {
            romHandler.addTrainerPokemon(settings);
            trainersChanged = true;
        }


        if (settings.isDoubleBattleMode()) {
            romHandler.doubleBattleMode();
            trainersChanged = true;
        }

        switch(settings.getTrainersMod()) {
            case RANDOM:
            case DISTRIBUTED:
            case MAINPLAYTHROUGH:
            case TYPE_THEMED:
            case TYPE_THEMED_ELITE4_GYMS:
            case SIMILAR_TYPES:
                romHandler.randomizeTrainerPokes(settings);
                trainersChanged = true;
                break;
            default:
                if (settings.isTrainersLevelModified()) {
                    romHandler.onlyChangeTrainerLevels(settings);
                    trainersChanged = true;
                }
                break;
        }

        if ((settings.getTrainersMod() != Settings.TrainersMod.UNCHANGED
                || settings.getStartersMod() != Settings.StartersMod.UNCHANGED)
                && settings.isRivalCarriesTeamThroughout()) {
            romHandler.rivalCarriesTeam();
            trainersChanged = true;
        }

        if ((settings.getTrainersMod() != Settings.TrainersMod.UNCHANGED
                || settings.getStartersMod() != Settings.StartersMod.UNCHANGED)
                && settings.isRivalCarriesStarterThroughout()) {
            romHandler.rivalCarriesStarter();
            trainersChanged = true;
        }

        if (settings.isTrainersForceFullyEvolved()) {
            romHandler.forceFullyEvolvedTrainerPokes(settings);
            trainersChanged = true;
        }

        if (settings.isBetterBossTrainerMovesets()
                || settings.isBetterImportantTrainerMovesets()
                || settings.isBetterRegularTrainerMovesets()) {
            romHandler.pickTrainerMovesets(settings);
            trainersChanged = true;
            trainerMovesetsChanged = true;
        }

        if (settings.isRandomizeHeldItemsForBossTrainerPokemon()
                || settings.isRandomizeHeldItemsForImportantTrainerPokemon()
                || settings.isRandomizeHeldItemsForRegularTrainerPokemon()) {
            romHandler.randomizeTrainerHeldItems(settings);
            trainersChanged = true;
        }

        List<String> originalTrainerNames = getTrainerNames();
        boolean trainerNamesChanged = false;

        // Trainer names & class names randomization
        if (romHandler.canChangeTrainerText()) {
            if (settings.isRandomizeTrainerClassNames()) {
                romHandler.randomizeTrainerClassNames(settings);
                trainersChanged = true;
                trainerNamesChanged = true;
            }

            if (settings.isRandomizeTrainerNames()) {
                romHandler.randomizeTrainerNames(settings);
                trainersChanged = true;
                trainerNamesChanged = true;
            }
        }





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





        List<StaticEncounter> oldStatics = romHandler.getStaticPokemon();
        // Static Pokemon
        if (romHandler.canChangeStaticPokemon()) {

            if (settings.getStaticPokemonMod() != Settings.StaticPokemonMod.UNCHANGED) { // Legendary for L
                romHandler.randomizeStaticPokemon(settings);
                staticsChanged = true;
            } else if (settings.isStaticLevelModified()) {
                romHandler.onlyChangeStaticLevels(settings);
                staticsChanged = true;
            }


        }


        // Totem Pokemon
        if (romHandler.generationOfPokemon() == 7) {

            if (settings.getTotemPokemonMod() != Settings.TotemPokemonMod.UNCHANGED ||
                    settings.getAllyPokemonMod() != Settings.AllyPokemonMod.UNCHANGED ||
                    settings.getAuraMod() != Settings.AuraMod.UNCHANGED ||
                    settings.isRandomizeTotemHeldItems() ||
                    settings.isTotemLevelsModified()) {

                romHandler.randomizeTotemPokemon(settings);
                totemsChanged = true;
            }


        }



        switch (settings.getWildPokemonMod()) {
            case RANDOM:
                romHandler.randomEncounters(settings);
                wildsChanged = true;
                break;
            case AREA_MAPPING:
                romHandler.area1to1Encounters(settings);
                wildsChanged = true;
                break;
            case GLOBAL_MAPPING:
                romHandler.game1to1Encounters(settings);
                wildsChanged = true;
                wildGlobal = true;
                break;
            default:
                if (settings.isWildLevelsModified()) {
                    romHandler.onlyChangeWildLevels(settings);
                    wildsChanged = true;
                }
                break;
        }

        boolean useTimeBasedEncounters = settings.isUseTimeBasedEncounters() ||
                (settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED && settings.isWildLevelsModified());
        List<EncounterSet> encounters = romHandler.getEncounters(useTimeBasedEncounters);
        for (EncounterSet es : encounters) {
            for (Encounter e : es.encounters) {
                checkValue = addToCV(checkValue, e.level, e.pokemon.number);
            }
        }


        // In-game trades


        switch(settings.getInGameTradesMod()) {
            case RANDOMIZE_GIVEN:
            case RANDOMIZE_GIVEN_AND_REQUESTED:
                romHandler.randomizeIngameTrades(settings);
                tradesChanged = true;
                break;
            default:
                break;
        }



        // Field Items
        switch(settings.getFieldItemsMod()) {
            case SHUFFLE:
                romHandler.shuffleFieldItems();
                break;
            case RANDOM:
            case RANDOM_EVEN:
                romHandler.randomizeFieldItems(settings);
                break;
            default:
                break;
        }

        // Shops
        switch(settings.getShopItemsMod()) {
            case SHUFFLE:
                romHandler.shuffleShopItems();
                shopsChanged = true;
                break;
            case RANDOM:
                romHandler.randomizeShopItems(settings);
                shopsChanged = true;
                break;
            default:
                break;
        }



        // Pickup Items
        if (settings.getPickupItemsMod() == Settings.PickupItemsMod.RANDOM) {
            romHandler.randomizePickupItems(settings);
            pickUpChanged = true;
        }




        // Log Pokemon traits (stats, abilities, etc) if any have changed
        if (pokemonTraitsChanged) {
            logPokemonTraitChanges(log);
        }
        log.println();

        if (evolutionsChanged) {
            logEvolutionChanges(log);
        }
        log.println();

        // Log everything afterwards, so that "impossible evolutions" can account for "easier evolutions"
        if (settings.isChangeImpossibleEvolutions()) {
            log.println("--Removing Impossible Evolutions--");
            logUpdatedEvolutions(log, romHandler.getImpossibleEvoUpdates(), romHandler.getEasierEvoUpdates());
        }
        log.println("--Making Evolutions Easier--");
        if (!(romHandler instanceof Gen1RomHandler)) {
            log.println("Friendship evolutions now take 160 happiness (was 220).");
        }
        logUpdatedEvolutions(log, romHandler.getEasierEvoUpdates(), null);

        if (settings.isRemoveTimeBasedEvolutions()) {
            log.println("--Removing Timed-Based Evolutions--");
            logUpdatedEvolutions(log, romHandler.getTimeBasedEvoUpdates(), null);
        }

        if (startersChanged) {
            logStarters(log);
        }
        log.println();

        // Move Data Log
        // Placed here so it matches its position in the randomizer interface
         if (movesChanged || movesUpdated) {
            logMoveChanges(log);
         } else {
            log.println("<h2 id=\"md\">Move Data</h2><p>Unchanged.</p>");
         }
        log.println();

        // Show the new movesets if applicable
        if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
            log.println("<h2 id=\"pm\">Pokemon Movesets</h2>");
            log.println("<p>Metronome Only.</p>");
        } else if (movesetsChanged) {
            logMovesetChanges(log);
        } else {
            log.println("<h2 id=\"pm\">Pokemon Movesets</h2><p>Unchanged.</p>");
        }
        log.println();

        if (tmMovesChanged) {
            checkValue = logTMMoves(log, checkValue);
        } else if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
            log.println("<h2 id=\"tm\">TM Moves</h2><p>Metronome Only.</p>");
        } else {
            log.println("<h2 id=\"tm\">TM Moves</h2><p>Unchanged.</p>");
        }
        log.println();

        // Copy TM/HM compatibility to cosmetic formes if it was changed at all, and log changes
        if (tmsHmsCompatChanged) {
            romHandler.copyTMCompatibilityToCosmeticFormes();
            logTMHMCompatibility(log);
        }

        if (moveTutorMovesChanged) {
            checkValue = logMoveTutorMoves(log, checkValue, oldMtMoves);
        } else if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
            log.println("<h2 id=\"mt\">Move Tutor Moves</h2><p>Metronome Only.</p>");
        } else {
            log.println("<h2 id=\"mt\">Move Tutor Moves</h2> <p>Unchanged.</p>");
        }
        log.println();

        // Copy move tutor compatibility to cosmetic formes if it was changed at all
        if (tutorCompatChanged) {
            romHandler.copyMoveTutorCompatibilityToCosmeticFormes();
            logTutorCompatibility(log);
        }

        if (trainersChanged) {
            maybeLogTrainerChanges(log, originalTrainerNames, trainerNamesChanged, trainerMovesetsChanged);
        } else {
            log.println("<h2 id=\"tp\">Trainers</h2><p>Unchanged.</p>");
        }
        log.println();

        if (staticsChanged) {
            checkValue = logStaticPokemon(log, checkValue, oldStatics);
        } else {
            log.println("<h2 id=\"sp\">Static Pokemon</h2><p>Unchanged.</p>");
        }
        log.println();

        if (totemsChanged) {
            List<TotemPokemon> oldTotems = romHandler.getTotemPokemon();
            checkValue = logTotemPokemon(log, checkValue, oldTotems);
        } else {
            log.println("Totem Pokemon: Unchanged." + NEWLINE);
        }

        if (wildsChanged) {
            logWildPokemonChanges(log);
        } else {
            log.println("<h2 id=\"wp\">Wild Pokemon</h2><p>Unchanged.</p>");
        }
        log.println();

        if (tradesChanged) {
            List<IngameTrade> oldTrades = romHandler.getIngameTrades();
            logTrades(log, oldTrades);
        }
        log.println();

        if (shopsChanged) {
            logShops(log);
        }

        if (pickUpChanged) {
            logPickupItems(log);
        }




        // Test output for placement history
        // romHandler.renderPlacementHistory();

        // Intro Pokemon...
        romHandler.randomizeIntroPokemon();

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
        if (!romHandler.isRomValid()) {
            log.println(bundle.getString("Log.InvalidRomLoaded"));
        }
        romHandler.printRomDiagnostics(log);

        log.println("--->");

        List<String[]> toc = new ArrayList<String[]>();


        if (movesUpdated)
            toc.add(new String[] {"mm", "Move Modernization"});
        if (pokemonTraitsChanged)
            toc.add(new String[] {"ps", "Pokemon Stats"});
        if (evolutionsChanged)
            toc.add(new String[] {"re", "Randomized Evolutions"});
        if (startersChanged)
            toc.add(new String[] {"rs", "Starters"});
        if (movesChanged || movesUpdated)
            toc.add(new String[] {"md", "Move Data"});
        if (movesetsChanged)
            toc.add(new String[] {"pm", "Pokemon Moves"});
        if (tmMovesChanged)
            toc.add(new String[] {"tm", "TM Moves"});
        if (moveTutorMovesChanged)
            toc.add(new String[] {"mt", "Tutor Moves"});
        if (trainersChanged)
            toc.add(new String[] {"tp", "Trainer Pokemon"});
        if (staticsChanged)
            toc.add(new String[] {"sp", "Static Pokemon"});
        if (wildsChanged) {
            if (wildGlobal) {
                toc.add(new String[]{"wpg", "Wild Pokemon - Global"});
                toc.add(new String[]{"wpa", "Wild Pokemon - Areas"});
            } else {
                toc.add(new String[]{"wp", "Wild Pokemon"});
            }
        }
        if (tradesChanged)
            toc.add(new String[] {"igt", "In-Game Trades"});
        if (shopsChanged)
            toc.add(new String[] {"si", "Shop Items"});
        if (pickUpChanged)
            toc.add(new String[] {"pu", "Pickup Items"});


        /*
        if (((Map) templateData.get("tweakMap")).size() > 0) {
            toc.add(new String[] {"pa", "Patches Applied"});
        }

        if (templateData.get("shuffledTypes") != null) {
            toc.add(new String[] {"st", "Shuffled Types"});
        }

        Object fte = templateData.get("updateEffectiveness");
        if (fte != null && (Boolean) fte) {
            toc.add(new String[] {"fte", "Fixed Type Effectiveness"});
        }

        if (templateData.get("typeMatchups") != null) {
            toc.add(new String[] {"tmc", "Type Chart"});
        }

        if (templateData.get("removeTradeEvo") != null
                && ((List) templateData.get("removeTradeEvo")).size() > 0) {
            toc.add(new String[] {"rte", "Impossible Evos"});
        }
        if (templateData.get("condensedEvos") != null
                && ((TreeSet) templateData.get("condensedEvos")).size() > 0) {
            toc.add(new String[] {"cle", "Condensed Evos"});
        }

        if (templateData.get("gameBreakingMoves") != null
                && (Boolean) templateData.get("gameBreakingMoves")) {
            toc.add(new String[] {"gbm", "Game Breaking Moves"});
        }
         */



        endLog(log, toc);

        return checkValue;
    }

    private void startLog(PrintStream log) {

        log.println("<!DOCTYPE html>");
        log.println("<body>");
    }

    private void endLog(PrintStream log, List<String[]> toc) {

        log.println("<ul id=\"toc\">");
        for (String[] tocElement : toc) log.println("<li class=\"pk-type flying\"><a href=\"#"+ tocElement[0] +"\">" + tocElement[1] + "</a></li>");
        log.println("</ul>");

        log.println("</body>");

        log.println("<head>");
        log.println("<title>" + romHandler.getROMName() + " randomization log 23</title>");
        log.println("<meta charset=\"UTF-8\"> ");
        log.println("<style type=\"text/css\"> ");
        log.println(getLogCss());
        log.println("</style");
        log.println("</head>");

        log.println("<script>");
        log.println("var theme = \"\";");
        log.println("// Set it once per load - otherwise set it below");
        log.println("if (theme == \"\" && window.matchMedia(\"(prefers-color-scheme: dark)\").matches) {");
        log.println("theme = \"dark\";");
        log.println("document.body.classList.add(\"dark-mode\");");
        log.println("}");
        log.println("function darkMode() {");
        log.println("if (theme==\"dark\") {");
        log.println("theme=\"light\";");
        log.println("document.body.classList.remove(\"dark-mode\");");
        log.println("} else {");
        log.println("theme=\"dark\";");
        log.println("document.body.classList.add(\"dark-mode\");");
        log.println("}");
        log.println("}");
        log.println("</script>");

        log.println("</html>");


    }

    private int logMoveTutorMoves(PrintStream log, int checkValue, List<Integer> oldMtMoves) {
        List<Integer> newMtMoves = romHandler.getMoveTutorMoves();
        List<Move> moves = romHandler.getMoves();

        /*  Non-HTML Log
        log.println("--Tutor Moves--");
        for (int i = 0; i < newMtMoves.size(); i++) {
            log.printf("%-10s -> %-10s" + NEWLINE, moves.get(oldMtMoves.get(i)).name,
                    moves.get(newMtMoves.get(i)).name);
            checkValue = addToCV(checkValue, newMtMoves.get(i));
        }
        */

        log.println("--->");

        log.println("<h2 id=\"mt\">Move Tutor Moves</h2>");
        log.println("<table>");
        log.println("<tr>");
        log.println("<th>OLD MOVE</th>");
        log.println("<th>NEW MOVE</th>");
        log.println("<th>TYPE</th>");
        if (romHandler.hasPhysicalSpecialSplit())
            log.println("<th>CAT.</th>");
        log.println("<th>POWER</th>");
        log.println("<th>ACC.</th>");
        log.println("<th>PP</th>");
        log.println("</tr>");
        for (int i = 0; i < newMtMoves.size(); i++) {
            Move move = moves.get(newMtMoves.get(i));
            String mvType = (move.type == null) ? "???" : move.type.toString();

            log.println("<tr><td>" + moves.get(oldMtMoves.get(i)).name + "</td>");
            log.println("<td>" + move.name + "</td>");
            log.println("<td><span class=\"pk-type "+ mvType.toLowerCase() + "\">" + mvType.toUpperCase() + "</span></td>");

            if (romHandler.hasPhysicalSpecialSplit())
                log.println("<td><span class=\"move-cat "+ move.category.toString().toLowerCase()
                        + "\">" + move.category.toString().substring(0,3).toUpperCase() + "</span></td>");

            log.println("<td>" + (move.power <= 1 ? "-" : move.power) + "</td>");
            log.println("<td>" + ((int) move.hitratio <= 1 ? "-" : (int) move.hitratio) + "</td>");
            log.println("<td>" + move.pp + "</td>");
            log.println("</tr>");

            checkValue = addToCV(checkValue, newMtMoves.get(i));
        }
        log.println("</table>");

        log.println("<!---");

        return checkValue;
    }

    private int logTMMoves(PrintStream log, int checkValue) {

        List<Integer> tmMoves = romHandler.getTMMoves();
        List<Move> moves = romHandler.getMoves();

        /*  Non-HTML Log
        log.println("--TM Moves--");
        for (int i = 0; i < tmMoves.size(); i++) {
            log.printf("TM%02d %s" + NEWLINE, i + 1, moves.get(tmMoves.get(i)).name);
            checkValue = addToCV(checkValue, tmMoves.get(i));
        }
        */

        log.println("--->");

        log.println("<h2 id=\"tm\">TM Moves</h2>");
        log.println("<table class = \"tm-table\">");
        log.println("<tr>");
        log.println("<th>NUM</th>");
        log.println("<th>NAME</th>");
        log.println("<th>TYPE</th>");
        if (romHandler.hasPhysicalSpecialSplit())
            log.println("<th>CAT.</th>");
        log.println("<th>POWER</th>");
        log.println("<th>ACC.</th>");
        log.println("<th>PP</th>");
        //log.println("<th>EFF.</th>");
        log.println("</tr>");

        for (int i = 0; i < tmMoves.size(); i++) {
            Move move = moves.get(tmMoves.get(i));
            String mvType = (move.type == null) ? "???" : move.type.toString();

            if (i % 2 == 1)
                log.println("<tr>");
            else
                log.println("<tr class=\"alt\">");

            if (i < 9) log.println("<td><span class=\"tm-element\">TM0" + (i + 1) + "</span></td>");
            else log.println("<td><span class=\"tm-element\">TM" + (i + 1) + "</span></td>");

            log.println("<td>" + move.name + "</td>");
            log.println("<td><span class=\"pk-type "+ mvType.toLowerCase() + "\">" + mvType.toUpperCase() + "</span></td>");

            if (romHandler.hasPhysicalSpecialSplit())
                log.println("<td><span class=\"move-cat "+ move.category.toString().toLowerCase()
                        + "\">" + move.category.toString().substring(0,3).toUpperCase() + "</span></td>");

            log.println("<td>" + (move.power <= 1 ? "-" : move.power)  + "</td>");
            log.println("<td>" +  ((int) move.hitratio <= 1 ? "-" : (int) move.hitratio)+ "</td>");
            log.println("<td>" + move.pp + "</td>");
            //log.println("<td>" + move.efficacyTier + "</td>");
            log.println("</tr>");

            checkValue = addToCV(checkValue, tmMoves.get(i));
        }
        log.println("</table>");

        log.println("<!---");

        return checkValue;
    }

    private void logTrades(PrintStream log, List<IngameTrade> oldTrades) {
        List<IngameTrade> newTrades = romHandler.getIngameTrades();
        int size = oldTrades.size();

        /*  Non-HTML Log
        log.println("--In-Game Trades--");
        for (int i = 0; i < size; i++) {
            IngameTrade oldT = oldTrades.get(i);
            IngameTrade newT = newTrades.get(i);
            log.printf("Trade %-11s -> %-11s the %-11s        ->      %-11s -> %-15s the %s" + NEWLINE,
                    oldT.requestedPokemon != null ? oldT.requestedPokemon.fullName() : "Any",
                    oldT.nickname, oldT.givenPokemon.fullName(),
                    newT.requestedPokemon != null ? newT.requestedPokemon.fullName() : "Any",
                    newT.nickname, newT.givenPokemon.fullName());
        }
        */

        log.println("--->");

        log.println("<h2 id=\"igt\">In-Game Trades</h2>");
        log.println("<p>Trades are shown in pairs. New trades in <strong>bold</strong> on top, old trades below in <em>italics</em>.</p>");
        log.println("<table class=\"trades-table\">");
        log.println("<tr><th>Requested</th><th>Given</th><th>Held Item</th></tr>");
        for (int i = 0; i < size; i++) {
            IngameTrade oldT = oldTrades.get(i);
            IngameTrade newT = newTrades.get(i);

            log.println("<tr><td>" + newT.requestedPokemon.name + "</td><td>" + newT.givenPokemon.name + " (" + newT.nickname + ")</td>");
            if (newT.item > 0) log.println("<td>" + romHandler.getItemNames()[newT.item] + "</td>");
            else log.println("<td>" + newT.item + "</td>");
            log.print("</tr>");

            log.print("<tr class=\"alt\"><td>" + oldT.requestedPokemon.name + "</td><td>" + oldT.givenPokemon.name + " (" + oldT.nickname + ")</td>");
            if (oldT.item > 0) log.println("<td>" + romHandler.getItemNames()[oldT.item] + "</td>");
            else log.println("<td>" + oldT.item + "</td>");
        }
        log.println("</table>");

        log.println("<!---");

    }

    private void logMovesetChanges(PrintStream log) {

        List<String> movesets = new ArrayList<>();
        Map<Integer, List<MoveLearnt>> moveData = romHandler.getMovesLearnt();
        Map<Integer, List<Integer>> eggMoves = romHandler.getEggMoves();
        List<Move> moves = romHandler.getMoves();
        List<Pokemon> pkmnList = romHandler.getPokemonInclFormes();

        log.println("--->");

        log.println("<h2 id=\"pm\">Pokemon Movesets</h2>");



        for (Pokemon pk : pkmnList) {
            if (pk == null || pk.actuallyCosmetic) {
                continue;
            }
            log.println("<h3>" + pk.number + " " + pk.name + "</h3>");
            log.println("<ul class=\"moveset\">");

            List<MoveLearnt> data = moveData.get(pk.number);
            for (MoveLearnt mv : data) {
                if (moves.get(mv.move) != null) {
                    Move move = moves.get(mv.move);

                    String mvType = (move.type == null) ? "???" : move.type.toString();

                    log.println("<li><strong>" + move.name + "</strong><em>Lv " + mv.level + "</em>");

                    log.println("<table class=\"moveset-table\">");

                    log.println("<td id=\"type\" class=\"pk-type2 " + mvType.toLowerCase() + "\">" + mvType.toUpperCase() + "</td>");
                    if (romHandler.hasPhysicalSpecialSplit())
                        log.println("<td class=\"move-cat2 "+ move.category.toString().toLowerCase() + "\">" + move.category.toString().substring(0,3).toUpperCase() + "</td>");
                    log.println("<td id=\"alt\">"+ (move.power <= 1 ? "-" : move.power) +"</td>");
                    log.println("<td>"+ ((int)move.hitratio <= 1 ? "-" : (int)move.hitratio)+"</td>");
                    log.println("<td id=\"alt\">"+move.pp+"</td></tr>");
                    log.println("</tr>");
                    log.println("</table>");

                    log.println("</li>");
                }
                else log.println("<li><span class=\"error\">Invalid move at level " + mv.level + "</span></li>");
            }

            List<Integer> eggMove = eggMoves.get(pk.number);

            if (eggMove != null && eggMove.size() != 0) {
                log.println("<h3>Egg Moves</h3>");
                for (Integer mv : eggMove) {
                    Move move = moves.get(mv);

                    String mvType = (move.type == null) ? "???" : move.type.toString();

                    log.println("<li><strong>" + move.name + "</strong>");

                    log.println("<table class=\"moveset-table\">");

                    log.println("<td class=\"pk-type2 " + mvType.toLowerCase() + "\">" + mvType.toUpperCase() + "</td>");
                    if (romHandler.hasPhysicalSpecialSplit())
                        log.println("<td class=\"move-cat2 " + move.category.toString().toLowerCase() + "\">" + move.category.toString().substring(0,3).toUpperCase() + "</td>");
                    log.println("<td id=\"alt\">"+ (move.power <= 1 ? "-" : move.power) +"</td>");
                    log.println("<td>"+ ((int)move.hitratio <= 1 ? "-" : (int)move.hitratio)+"</td>");
                    log.println("<td id=\"alt\">"+move.pp+"</td>");
                    log.print("</tr>");
                    log.println("</tr>");
                    log.println("</table>");

                    log.println("</li>");
                }
            }
            log.println("</ul>");
        }

        log.println("<!---");


        /*  Non-HTML Log
        log.println("--Pokemon Movesets--");
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

            if (romHandler instanceof Gen1RomHandler) {
                sb.append(String.format("%03d %s", i, pkmn.fullName()))
                        .append(evoStr).append(System.getProperty("line.separator"))
                        .append(String.format("HP   %-3d", pkmn.hp)).append(System.getProperty("line.separator"))
                        .append(String.format("ATK  %-3d", pkmn.attack)).append(System.getProperty("line.separator"))
                        .append(String.format("DEF  %-3d", pkmn.defense)).append(System.getProperty("line.separator"))
                        .append(String.format("SPEC %-3d", pkmn.special)).append(System.getProperty("line.separator"))
                        .append(String.format("SPE  %-3d", pkmn.speed)).append(System.getProperty("line.separator"));
            } else {
                sb.append(String.format("%03d %s", i, pkmn.fullName()))
                        .append(evoStr).append(System.getProperty("line.separator"))
                        .append(String.format("HP  %-3d", pkmn.hp)).append(System.getProperty("line.separator"))
                        .append(String.format("ATK %-3d", pkmn.attack)).append(System.getProperty("line.separator"))
                        .append(String.format("DEF %-3d", pkmn.defense)).append(System.getProperty("line.separator"))
                        .append(String.format("SPA %-3d", pkmn.spatk)).append(System.getProperty("line.separator"))
                        .append(String.format("SPD %-3d", pkmn.spdef)).append(System.getProperty("line.separator"))
                        .append(String.format("SPE %-3d", pkmn.speed)).append(System.getProperty("line.separator"));
            }

            i++;

            List<MoveLearnt> data = moveData.get(pkmn.number);
            for (MoveLearnt ml : data) {
                try {
                    if (ml.level == 0) {
                        sb.append("Learned upon evolution: ")
                                .append(moves.get(ml.move).name).append(System.getProperty("line.separator"));
                    } else {
                        sb.append("Level ")
                                .append(String.format("%-2d", ml.level))
                                .append(": ")
                                .append(moves.get(ml.move).name).append(System.getProperty("line.separator"));
                    }
                } catch (NullPointerException ex) {
                    sb.append("invalid move at level").append(ml.level);
                }
            }
            List<Integer> eggMove = eggMoves.get(pkmn.number);
            if (eggMove != null && eggMove.size() != 0) {
                sb.append("Egg Moves:").append(System.getProperty("line.separator"));
                for (Integer move : eggMove) {
                    sb.append(" - ").append(moves.get(move).name).append(System.getProperty("line.separator"));
                }
            }

            movesets.add(sb.toString());
        }
        Collections.sort(movesets);
        for (String moveset : movesets) {
            log.println(moveset);
        }
         */

    }

    private void logMoveUpdates(PrintStream log) {

        List<Move> moves = romHandler.getMoves();
        Map<Integer, boolean[]> moveUpdates = romHandler.getMoveUpdates();

        log.println("--->");

        log.println("<h2 id=\"mm\">Move Modernization</h2>");
        log.println("<ul>");

        for (int moveID : moveUpdates.keySet()) {
            boolean[] changes = moveUpdates.get(moveID);

            Move move = moves.get(moveID);
            log.print("<li>Made <span class=\"pk-type " + move.type.toString().toLowerCase() + "\">" +
                            move.name.toString().toUpperCase() + "</span> ");

            if (changes[3]) {
                if (changes[0] || changes[1] || changes[2])
                    log.print("be <span class=\"pk-type " +
                        move.type.toString().toLowerCase() + "\">" + move.type.toString().toUpperCase() +
                        "</span> and have ");
                else
                    log.print("be <span class=\"pk-type " + move.type.toString().toLowerCase() + "\">" +
                            move.type.toString().toUpperCase() + "</span> ");
            }
            else log.print("have ");

            if (changes[0]) {
                if (changes[1] || changes[2]) {
                    if (changes[1] && changes[2])
                        log.print("<strong>" + move.power + " power,</strong> ");
                    else
                        log.print("<strong>" + move.power + " power</strong> and ");
                }
                else log.print("<strong>" + move.power + " power</strong> ");
            }

            if (changes[1] && changes[2])
                log.print("<strong>" + move.pp + " pp</strong> and ");
            else if (changes[1])
                log.print("<strong>" + move.pp +" pp</strong> ");

            if (changes[2]) log.print("<strong>" + (int) move.hitratio + " accuracy</strong> ");

            log.println("</li>");
        }
        log.println("</ul>");

        log.println("<!---");

        /*
        log.println("--Move Updates--");
        for (int moveID : moveUpdates.keySet()) {
            boolean[] changes = moveUpdates.get(moveID);
            Move mv = moves.get(moveID);
            List<String> nonTypeChanges = new ArrayList<>();
            if (changes[0]) {
                nonTypeChanges.add(String.format("%d power", mv.power));
            }
            if (changes[1]) {
                nonTypeChanges.add(String.format("%d PP", mv.pp));
            }
            if (changes[2]) {
                nonTypeChanges.add(String.format("%.00f%% accuracy", mv.hitratio));
            }
            String logStr = "Made " + mv.name;
            // type or not?
            if (changes[3]) {
                logStr += " be " + mv.type + "-type";
                if (nonTypeChanges.size() > 0) {
                    logStr += " and";
                }
            }
            if (changes[4]) {
                if (mv.category == MoveCategory.PHYSICAL) {
                    logStr += " a Physical move";
                } else if (mv.category == MoveCategory.SPECIAL) {
                    logStr += " a Special move";
                } else if (mv.category == MoveCategory.STATUS) {
                    logStr += " a Status move";
                }
            }
            if (nonTypeChanges.size() > 0) {
                logStr += " have ";
                if (nonTypeChanges.size() == 3) {
                    logStr += nonTypeChanges.get(0) + ", " + nonTypeChanges.get(1) + " and " + nonTypeChanges.get(2);
                } else if (nonTypeChanges.size() == 2) {
                    logStr += nonTypeChanges.get(0) + " and " + nonTypeChanges.get(1);
                } else {
                    logStr += nonTypeChanges.get(0);
                }
            }
            log.println(logStr);
        }

         */

    }

    private void logEvolutionChanges(PrintStream log) {

        List<Pokemon> allPokes = romHandler.getPokemonInclFormes();
        List<Pokemon> basePokes = romHandler.getBasePokemon();
        List<Pokemon> evoPokes = romHandler.getEvolvingPokemon();

        log.println("--->");



        log.println("<h2 id=\"re\">Randomized Evolutions</h2>");
        log.println("<ul>");

        for (Pokemon pk : basePokes) {
            if (pk != null) {
                generatePokeEvoText(pk, log, true);
            }

        }
        log.println("</ul>");


        /* Obsolete with new Random Evolution Logs Method
        log.println("<h2 id=\"ep\">New Evolution Paths</h2>");
        log.println("<table class=\"pk-table\">");

        for (Pokemon pk : evoPokes) {
            if (pk != null) {
                log.println("<tr>");
                if (pk.evolutionsTo.size() == 0)
                    log.println("<td><div class=\"pk-chain-element-base\" id=\"" + pk.name + "evoA\">"+ pk.name + "</div></td>");
                else
                    log.println("<td><div class=\"pk-chain-element\" id=\"" + pk.name + "evoA\"><a href=\"#"+ pk.name +"evoB\">" + pk.name + "</a></div></td>");

                log.print("<td>");
                List<Evolution> filteredEvos1 = pk.getFilteredEvolutionsFrom();
                for (Evolution evo1 : filteredEvos1) {
                    if (evo1.to.evolutionsFrom.size() > 0)
                        log.print("<div class=\"pk-chain-element\" id=\"" + evo1.to.name + "evoB\"><a href=\"#"+ evo1.to.name +"evoA\">" + evo1.to.name + "</a></div>");
                    else
                        log.print("<div class=\"pk-chain-element-tail\" id=\"" + evo1.to.name + "evoB\">" + evo1.to.name + "</div>");
                }
                log.println("</td>");

                log.print("<td>");
                for (Evolution evo : filteredEvos1) {
                    log.println("<div style=\"min-height:34px;margin:5px 0;\">");
                    List<Evolution> filteredEvos2 = evo.to.getFilteredEvolutionsFrom();
                    for (Evolution evo2 : filteredEvos2) {
                        if (evo2.to.evolutionsFrom.size() > 0)
                            log.print("<div class=\"pk-chain-element\"><a href=\"#"+ evo2.to.name +"evoA\">" + evo2.to.name + "</a></div>");
                        else
                            log.print("<div class=\"pk-chain-element-tail\">" + evo2.to.name + "</div>");
                    }
                    log.print("</div>");
                }
                log.println("</td>");

                log.println("</tr>");
            }
        }
        log.println("</table>");
         */



        log.println("<!---");



        /* Non-HTML Log
        log.println("--Randomized Evolutions--");
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
                    log.printf("%-15s -> %-15s" + NEWLINE, pk.fullName(), evoStr.toString());
                }
            }
        }

         */

    }

    // Made to be able to recursively log random Pokemon evolutions
    private void generatePokeEvoText(Pokemon pk, PrintStream log, boolean nameNeeded) {
        int numEvos = pk.evolutionsFrom.size();
        if (!pk.actuallyCosmetic && numEvos > 0) {
            if (nameNeeded) {
                log.print("<li>");
                log.print(pk.name + " now evolves into ");
            }
            log.println("<ul>");
            for (Evolution evoFm : pk.evolutionsFrom) {
                log.print("<li>");
                log.print(evoFm.to.name + " - ");
                switch (evoFm.type) {
                    case LEVEL:
                    case LEVEL_ATK_DEF_SAME:
                    case LEVEL_ATTACK_HIGHER:
                    case LEVEL_DEFENSE_HIGHER:
                        log.print(evoFm.type + " " + evoFm.extraInfo);
                        break;
                    case LEVEL_HIGH_PV:
                        log.print("LEVEL " + evoFm.extraInfo + " when HIGH PERSONALITY VALUE");
                        break;
                    case LEVEL_LOW_PV:
                        log.print("LEVEL " + evoFm.extraInfo + " when LOW PERSONALITY VALUE");
                        break;
                    case LEVEL_MALE_ONLY:
                        log.print("LEVEL " + evoFm.extraInfo + " when MALE");
                        break;
                    case LEVEL_FEMALE_ONLY:
                        log.print("LEVEL " + evoFm.extraInfo + " when FEMALE");
                        break;
                    case LEVEL_WITH_OTHER:
                        log.print("LEVEL with " + romHandler.getPokemon().get(evoFm.extraInfo).name + " in party");
                        break;
                    case LEVEL_ITEM_DAY:
                        log.print("LEVEL holding " + romHandler.getItemNames()[evoFm.extraInfo] + " during DAY");
                        break;
                    case LEVEL_ITEM_NIGHT:
                        log.print("LEVEL holding " + romHandler.getItemNames()[evoFm.extraInfo] + " during NIGHT");
                        break;
                    case LEVEL_WITH_MOVE:
                        log.print("LEVEL while knowing " + romHandler.getMoves().get(evoFm.extraInfo).name);
                        break;
                    case STONE:
                        log.print(romHandler.getItemNames()[evoFm.extraInfo]);
                        break;
                    case STONE_MALE_ONLY:
                        log.print(romHandler.getItemNames()[evoFm.extraInfo] + " when MALE");
                        break;
                    case STONE_FEMALE_ONLY:
                        log.print(romHandler.getItemNames()[evoFm.extraInfo] + " when FEMALE");
                        break;
                    case TRADE_ITEM:
                        log.print("TRADE holding " + romHandler.getItemNames()[evoFm.extraInfo]);
                        break;
                    // case MEGA_EVOLVE:
                    // MEGA EVOLVE holding ${romHandler.getItemNames()[evoFm.extraInfo]}
                    //break;
                    case FAIRY_AFFECTION:
                        log.print("HAPPINESS with FAIRY-type move");
                        break;
                    default:
                        log.print(evoFm.type);
                        break;
                }
                log.println("</li>");
                generatePokeEvoText(evoFm.to, log, false);
            }
            log.print("</ul>");
            if (nameNeeded) log.println("</li>");
        }
    }

    private void logPokemonTraitChanges(final PrintStream log) {
        List<Pokemon> allPokes = romHandler.getPokemonInclFormes();
        String[] itemNames = romHandler.getItemNames();
        boolean gen1 = romHandler instanceof Gen1RomHandler;
        int numAbilities = romHandler.abilitiesPerPokemon();

//        List<Pokemon> baseBSTSort = romHandler.getBaseSPTSort();
//        List<Pokemon> updatedBSTSort = romHandler.getUpdatedSPTSort();
//
//        for (int i = 0; i < baseBSTSort.size(); i++) {
//            Pokemon basePK = baseBSTSort.get(i);
//            Pokemon updatedPK = updatedBSTSort.get(i);
//            if (basePK != null && updatedPK != null)
//                System.out.println(baseBSTSort.indexOf(basePK) + " " + basePK.name + " " + (int)basePK.statPowerTotal
//                        + " | " + updatedPK.name + " " + (int)updatedPK.statPowerTotal);
//        }


        log.println("--->");

        log.println("<h2 id=\"ps\">Pokemon Base Stats & Types</h2>");
        log.println("<table class=\"pk-table\">");
        log.print("<tr>");
        log.print("<th>NUM</th>");
        log.print("<th>NAME</th>");
        log.print("<th>TYPE</th>");
        log.print("<th>HP</th>");
        log.print("<th>ATK</th>");
        log.print("<th>DEF</th>");

        if (gen1)
            log.print("<th>SPEC</th>");
        else
            log.print("<th>SPATK</th><th>SPDEF</th>");

        log.print("<th>SPEED</th>");
        log.print("<th>TOTAL</th>");
        // log.print("<th>FINAL</th>");

        if (numAbilities > 0)
            for (int i = 1; i <= romHandler.abilitiesPerPokemon(); i++)
                log.print("<th>ABILITY" + i + "</th>");

        if (!gen1)
            log.print("<th>ITEM</th>");

        // log.print("<th>BIG</th>");
        // log.print("<th>POWER</th>");
        // log.print("<th>FINAL</th>");

        if (settings.isScaleCatchRates() || settings.isUseMinimumCatchRate()) {
            log.print("<th>CATCH</th>");
        }

        if (settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM) {
            log.print("<th>STAGE</th>");
            log.print("<th>FINAL</th>");
        }
        log.println("</tr>");

        int index = 0;
        for (Pokemon pkmn : allPokes) {
            if (pkmn != null) {
                String pkmnT1;
                String pkmnT2;

                if (pkmn.primaryType != null)
                    pkmnT1 = pkmn.primaryType.toString();
                else
                    pkmnT1 = "???";
                if (pkmn.secondaryType != null)
                    pkmnT2 = pkmn.secondaryType.toString();
                else
                    pkmnT2 = "";

                if (index % 2 == 0 )
                    log.println("<tr>");
                else
                    log.println("<tr class=\"alt\">");

                log.println("<td>" + pkmn.number + "</td>");
                log.println("<td class=\"left\">" + pkmn.name + "</td>");

                log.println("<td>");
                log.println("<span class=\"pk-type "+ pkmnT1.toLowerCase() +"\">"+ pkmnT1.toUpperCase() +"</span>");
                if (pkmnT2 != "")
                    log.println("<span class=\"pk-type "+ pkmnT2.toLowerCase() +"\">"+ pkmnT2.toUpperCase() +"</span>");
                log.println("</td>");

                log.println("<td>"+ pkmn.hp +"</td>");
                log.println("<td>"+ pkmn.attack +"</td>");
                log.println("<td>"+ pkmn.defense +"</td>");

                if (gen1) {
                    log.println("<td>"+pkmn.special+"</td>");
                }
                else {
                    log.println("<td>"+ pkmn.spatk +"</td>");
                    log.println("<td>"+ pkmn.spdef +"</td>");
                }

                log.println("<td>"+ pkmn.speed +"</td>");
                log.println("<td>"+ pkmn.bst() +"</td>");
                // log.println("<td>"+ pkmn.finalBST +"</td>");

                switch (numAbilities) {
                    case 1:
                        log.println("<td>"+ romHandler.abilityName(pkmn.ability1) +"</td>");
                        break;
                    case 2:
                        log.println("<td>"+ romHandler.abilityName(pkmn.ability1) +"</td>");
                        log.println("<td>"+ romHandler.abilityName(pkmn.ability2) +"</td>");
                        break;
                    case 3:
                        log.println("<td>"+ romHandler.abilityName(pkmn.ability1) +"</td>");
                        log.println("<td>"+ romHandler.abilityName(pkmn.ability2) +"</td>");
                        log.println("<td>"+ romHandler.abilityName(pkmn.ability3) +"</td>");
                        break;
                    default:
                        break;
                }

                if (!gen1) {
                    log.println("<td>");

                    if (pkmn.guaranteedHeldItem > 0)
                        log.println(romHandler.getItemNames()[pkmn.guaranteedHeldItem] +" (100%)");
                    if (pkmn.commonHeldItem > 0)
                        log.println(romHandler.getItemNames()[pkmn.commonHeldItem] +" (common) <br />");
                    if (pkmn.rareHeldItem > 0)
                        log.println(romHandler.getItemNames()[pkmn.rareHeldItem] +" (rare) <br />");
                    if (pkmn.darkGrassHeldItem > 0)
                        log.println(romHandler.getItemNames()[pkmn.darkGrassHeldItem] +" (dark grass only)");

                    log.println("</td>");
                }

                // if (pkmn.isBig)
                //     log.println("<td>YES</td>");
                // else
                //     log.println("<td></td>");

                // log.println("<td>"+ pkmn.updatedSPTIndex +"</td>");
                // log.println("<td>"+ pkmn.updatedFinalSPTIndex +"</td>");

                if (settings.isScaleCatchRates() || settings.isUseMinimumCatchRate()) {
                    log.println("<td>"+ pkmn.catchRate +"</td>");
                }

                if (settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM) {
                    log.println("<td>" + pkmn.evoStageNumber + "</td>");
                    log.println("<td>" + pkmn.finalStageNumber + "</td>");
                }

            }
            index++;
        }

        log.println("</table>");

        log.println("<!---");

        /*  Non-HTML Method
        // Log base stats & types
        log.println("--Pokemon Base Stats & Types--");
        if (romHandler instanceof Gen1RomHandler) {
            log.println("NUM|NAME      |TYPE             |  HP| ATK| DEF| SPE|SPEC");
            for (Pokemon pkmn : allPokes) {
                if (pkmn != null) {
                    String typeString = pkmn.primaryType == null ? "???" : pkmn.primaryType.toString();
                    if (pkmn.secondaryType != null) {
                        typeString += "/" + pkmn.secondaryType.toString();
                    }
                    log.printf("%3d|%-10s|%-17s|%4d|%4d|%4d|%4d|%4d" + NEWLINE, pkmn.number, pkmn.fullName(), typeString,
                            pkmn.hp, pkmn.attack, pkmn.defense, pkmn.speed, pkmn.special );
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

         */



    }

    private void logTMHMCompatibility(final PrintStream log) {
        log.println("--TM Compatibility--");
        Map<Pokemon, boolean[]> compat = romHandler.getTMHMCompatibility();
        List<Integer> tmHMs = new ArrayList<>(romHandler.getTMMoves());
        tmHMs.addAll(romHandler.getHMMoves());
        List<Move> moveData = romHandler.getMoves();

        logCompatibility(log, compat, tmHMs, moveData, true);
    }

    private void logTutorCompatibility(final PrintStream log) {
        log.println("--Move Tutor Compatibility--");
        Map<Pokemon, boolean[]> compat = romHandler.getMoveTutorCompatibility();
        List<Integer> tutorMoves = romHandler.getMoveTutorMoves();
        List<Move> moveData = romHandler.getMoves();

        logCompatibility(log, compat, tutorMoves, moveData, false);
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
                String moveName = moveData.get(moveList.get(i - 1)).name;
                if (moveName.length() == 0) {
                    moveName = "(BLANK)";
                }
                int moveNameLength = moveName.length();
                if (flags[i]) {
                    if (includeTMNumber) {
                        if (i <= tmCount) {
                            log.printf("|TM%02d %" + moveNameLength + "s ", i, moveName);
                        } else {
                            log.printf("|HM%02d %" + moveNameLength + "s ", i-tmCount, moveName);
                        }
                    } else {
                        log.printf("|%" + moveNameLength + "s ", moveName);
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

    private void logUpdatedEvolutions(final PrintStream log, Set<EvolutionUpdate> updatedEvolutions,
                                      Set<EvolutionUpdate> otherUpdatedEvolutions) {
        for (EvolutionUpdate evo: updatedEvolutions) {
            if (otherUpdatedEvolutions != null && otherUpdatedEvolutions.contains(evo)) {
                log.println(evo.toString() + " (Overwritten by \"Make Evolutions Easier\", see below)");
            } else {
                log.println(evo.toString());
            }
        }
        log.println();
    }

    private void logStarters(final PrintStream log) {

        List<Pokemon> starters = romHandler.getPickedStarters();
        boolean gen1 = romHandler instanceof Gen1RomHandler;
        int numAbilities = romHandler.abilitiesPerPokemon();

        log.println("--->");

        switch(settings.getStartersMod()) {
            case CUSTOM:
                log.println("<h2 id=\"rs\">Custom Starters</h2>");
                break;
            default:
                log.println("<h2 id=\"rs\">" + settings.getStartersMod() + "</h2>");
                if (romHandler.getStartersFailed()) {
                    log.println("<h3>Starter Settings too strict!</h3>");
                    log.println("<p>Happens often when evo slider is at 2 Weakest is chosen with evolution strength checked (or Strongest without it checked).<br />" +
                            "Settings have been defaulted to starter Similar Strength to Each Other.</p>");
                }
                break;
        }

        log.println("<ul>");
        int index = 1;
        for (Pokemon pkmn : starters) {
            log.println("<li>Set starter " + index + " to <strong>" + pkmn.name + "</strong></li> ");
            index++;
        }
        log.println("</ul>");

        log.println("<table class=\"pk-table\">");
        log.print("<tr>");
        log.print("<th>NUM</th>");
        log.print("<th>NAME</th>");
        log.print("<th>TYPE</th>");
        log.print("<th>HP</th>");
        log.print("<th>ATK</th>");
        log.print("<th>DEF</th>");

        if (gen1)
            log.print("<th>SPEC</th>");
        else
            log.print("<th>SPATK</th><th>SPDEF</th>");

        log.print("<th>SPEED</th>");
        log.print("<th>TOTAL</th>");
        // log.print("<th>POWER</th>");

        if (numAbilities > 0)
            for (int i = 1; i <= romHandler.abilitiesPerPokemon(); i++)
                log.print("<th>ABILITY" + i + "</th>");

        log.print("<th>STAGE</th>");
        log.print("<th>FINAL</th>");
        log.println("</tr>");
        index = 1;
        for (Pokemon pk : starters) {
            generateStarterRows(pk, log, index);
            index++;
        }
        log.println("</table>");








        log.println("<!---");

        /* Non-HTML Log
        switch(settings.getStartersMod()) {
            case CUSTOM:
                log.println("--Custom Starters--");
                break;
            case COMPLETELY_RANDOM:
                log.println("--Random Starters--");
                break;
            case RANDOM_WITH_TWO_EVOLUTIONS:
                log.println("--Random 2-Evolution Starters--");
                break;
            default:
                break;
        }

        List<Pokemon> starters = romHandler.getPickedStarters();
        int i = 1;
        for (Pokemon starter: starters) {
            log.println("Set starter " + i + " to " + starter.fullName());
            i++;
        }
         */

    }

    private void generateStarterRows (Pokemon pkmn, PrintStream log, int index) {
        boolean gen1 = romHandler instanceof Gen1RomHandler;
        int numAbilities = romHandler.abilitiesPerPokemon();
        String pkmnT1;
        String pkmnT2;

        if (pkmn.primaryType != null)
            pkmnT1 = pkmn.primaryType.toString();
        else
            pkmnT1 = "???";
        if (pkmn.secondaryType != null)
            pkmnT2 = pkmn.secondaryType.toString();
        else
            pkmnT2 = "";

        if (index % 2 == 0 )
            log.println("<tr>");
        else
            log.println("<tr class=\"alt\">");

        log.println("<td>" + pkmn.number + "</td>");
        log.println("<td class=\"left\">" + pkmn.name + "</td>");

        log.println("<td>");
        log.println("<span class=\"pk-type "+ pkmnT1.toLowerCase() +"\">"+ pkmnT1.toUpperCase() +"</span>");
        if (pkmnT2 != "")
            log.println("<span class=\"pk-type "+ pkmnT2.toLowerCase() +"\">"+ pkmnT2.toUpperCase() +"</span>");
        log.println("</td>");

        log.println("<td>"+ pkmn.hp +"</td>");
        log.println("<td>"+ pkmn.attack +"</td>");
        log.println("<td>"+ pkmn.defense +"</td>");

        if (gen1) {
            log.println("<td>"+pkmn.special+"</td>");
        }
        else {
            log.println("<td>"+ pkmn.spatk +"</td>");
            log.println("<td>"+ pkmn.spdef +"</td>");
        }

        log.println("<td>"+ pkmn.speed +"</td>");
        log.println("<td>"+ pkmn.bst() +"</td>");
        // log.println("<td>"+ pkmn.updatedSPTIndex +"</td>");

        switch (numAbilities) {
            case 1:
                log.println("<td>"+ romHandler.abilityName(pkmn.ability1) +"</td>");
                break;
            case 2:
                log.println("<td>"+ romHandler.abilityName(pkmn.ability1) +"</td>");
                log.println("<td>"+ romHandler.abilityName(pkmn.ability2) +"</td>");
                break;
            case 3:
                log.println("<td>"+ romHandler.abilityName(pkmn.ability1) +"</td>");
                log.println("<td>"+ romHandler.abilityName(pkmn.ability2) +"</td>");
                log.println("<td>"+ romHandler.abilityName(pkmn.ability3) +"</td>");
                break;
            default:
                break;
        }

        log.println("<td>"+ pkmn.evoStageNumber +"</td>");
        log.println("<td>"+ pkmn.finalStageNumber +"</td>");

        for (Evolution evoFm : pkmn.evolutionsFrom) {
            generateStarterRows(evoFm.to, log, index);
        }
    }

    private void logWildPokemonChanges(final PrintStream log) {

        boolean useTimeBasedEncounters = settings.isUseTimeBasedEncounters() ||
                (settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED && settings.isWildLevelsModified());
        List<EncounterSet> encounters = romHandler.getEncounters(useTimeBasedEncounters);
        boolean globalSwap = (settings.getWildPokemonMod() == Settings.WildPokemonMod.GLOBAL_MAPPING);
        Map<Pokemon, Pokemon> globalPokeSwaps = romHandler.getGlobalWildPokemonTranslateMap();

        log.println("--->");

        log.println("<h2 id=\"wp\">Wild Pokemon</h2>");

        if (globalSwap) {
            log.println("<h3 id=\"wpg\">Global Pokemon Swaps</h3>");
            log.println("<ul>");
            for (Map.Entry<Pokemon, Pokemon> globalPokeSwap : globalPokeSwaps.entrySet()) {
                log.println("<li>" + globalPokeSwap.getKey().name + " => " + globalPokeSwap.getValue().name + "</li>");
            }
            log.println("</ul>");
        }


        log.println("<h3 id=\"wpa\">Area Pokemon</h3>");
        log.println("<ul>");



        int index = 0;
        for (EncounterSet encounterSet : encounters) {

            Map<String,List<Integer>> pokemonInSet = new HashMap<>();
            for (Encounter encounter : encounterSet.encounters) {
                if (!pokemonInSet.containsKey(encounter.pokemon.name))
                    pokemonInSet.put(encounter.pokemon.name, new ArrayList<>());

                if (encounter.maxLevel > 0  && encounter.maxLevel != encounter.level) {
                    for (int i = encounter.level; i <= encounter.maxLevel; i++) {
                        pokemonInSet.get(encounter.pokemon.name).add(i);
                    }
                } else {
                    pokemonInSet.get(encounter.pokemon.name).add(encounter.level);
                }
            }
            ArrayList<String> sortedPokemon = new ArrayList<>(pokemonInSet.keySet());
            Collections.sort(sortedPokemon);
            for (Map.Entry<String,List<Integer>> pokemonLevelSet : pokemonInSet.entrySet()) {
                List<Integer> distinctLevels = pokemonLevelSet.getValue().stream().distinct().collect(Collectors.toList());
                pokemonLevelSet.setValue(distinctLevels);
                Collections.sort(pokemonLevelSet.getValue());
            }

            log.println("<div class=\"wild-pk-set " + encounterSet.getDivClass() + "\">");

            if (encounterSet.displayName != null)
                log.println("Set #" + index + " - " + encounterSet.displayName + " (rate=" + encounterSet.rate +")");
            else
                log.println("Set #" + index + " (rate=" + encounterSet.rate + ")");

            log.println("</div>");

            log.println("<ul class=\"pk-set-list " + encounterSet.getUlClass() + "\">");

            for (String pokemonName : sortedPokemon) {
                log.print("<li>" + pokemonName + " <em>Lv: ");
                List<Integer> levelSet = pokemonInSet.get(pokemonName);
                int previous = -1;
                int current;
                int next;
                for (int i = 0; i < levelSet.size(); i++) {
                    current = levelSet.get(i);
                    if (i < levelSet.size() - 1) next = levelSet.get(i + 1);
                    else next = 0;

                    if (current != previous + 1) {
                        log.print(levelSet.get(i));
                        if (i != levelSet.size()-1 && current != next - 1) log.print(", ");
                        if (current == next - 1) log.print("-");
                    } else if (current != next - 1) {
                        log.print(levelSet.get(i));
                        if (i != levelSet.size()-1) log.print(", ");
                    }

                    previous = current;
                }
                log.print("</em>");
                log.println("</li>");
            }

//            for (Map.Entry<String,List<Integer>> pokemonLevelSet : pokemonInSet.entrySet()) {
//                log.println("<li>" + pokemonLevelSet.getKey() + " Lv: ");
//                int previous = -1;
//                int current;
//                int next;
//                for (int i = 0; i < pokemonLevelSet.getValue().size(); i++) {
//                    current = pokemonLevelSet.getValue().get(i);
//                    if (i < pokemonLevelSet.getValue().size() - 1) next = pokemonLevelSet.getValue().get(i + 1);
//                    else next = 0;
//
//                    if (current != previous + 1) {
//                        log.print(pokemonLevelSet.getValue().get(i));
//                        if (i != pokemonLevelSet.getValue().size()-1 && current != next - 1) log.print(", ");
//                        if (current == next - 1) log.print("-");
//                    } else if (current != next - 1) {
//                        log.print(pokemonLevelSet.getValue().get(i));
//                        if (i != pokemonLevelSet.getValue().size()-1) log.print(", ");
//                    }
//
//                    previous = current;
//                }
//                log.println("</li>");
//            }

//            for (Encounter encounter : encounterSet.encounters) {
//                log.println("<li>" + encounter.pokemon.name + " ");
//
//                if (encounter.maxLevel > 0 && encounter.maxLevel != encounter.level)
//                    log.println("<em>Lvs " + encounter.level + " - " + encounter.maxLevel + "</em>");
//                else
//                    log.println("<em>Lv " + encounter.level + "</em>");
//
//                log.println("</li>");
//            }
            index++;
            log.println("</ul>");
        }
        log.println("</ul>");



        log.println("<!---");


        /* Non-HTML Log
        log.println("--Wild Pokemon--");

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
                if (romHandler instanceof Gen1RomHandler) {
                    sb2.append(String.format("HP %-3d ATK %-3d DEF %-3d SPECIAL %-3d SPEED %-3d", e.pokemon.hp, e.pokemon.attack, e.pokemon.defense, e.pokemon.special, e.pokemon.speed));
                } else {
                    sb2.append(String.format("HP %-3d ATK %-3d DEF %-3d SPATK %-3d SPDEF %-3d SPEED %-3d", e.pokemon.hp, e.pokemon.attack, e.pokemon.defense, e.pokemon.spatk, e.pokemon.spdef, e.pokemon.speed));
                }
                log.print(sb2);
                log.println();
            }
            log.println();
        }
         */

    }

    private void maybeLogTrainerChanges(final PrintStream log, List<String> originalTrainerNames, boolean trainerNamesChanged, boolean logTrainerMovesets) {
        List<Trainer> trainers = romHandler.getTrainers();
        String[] itemNames = romHandler.getItemNames();
        List<Move> moves = romHandler.getMoves();
        boolean gen1 = romHandler instanceof Gen1RomHandler;

        log.println("--->");
        boolean flexbox = false;

        if (!flexbox) {
            log.println("<h2 id=\"tp\">Trainers Pokemon</h2>");
            log.println("<div class=\"all-trainers\">");
            int index = 0;
            for (Trainer trainer : trainers) {
                if (trainer != null) {
                    log.println("<div class=\"trainer-box\">");

                    log.println("<div class=\"trainer\">");
                    log.println("<span class=\"trainer-name\"><em># " + index + "</em>" + trainer.getLogName());
                    if (trainer.offset != index && trainer.offset != 0)
                        log.println("<br /><em>@" + trainer.stringOffset() + "</em>");
                    log.println("</span>");

                    log.println("</div>");

                    log.println("<em>Old Team</em>");
                    log.println("<ul class=\"old-trainer-pk\">");
                    for (TrainerPokemon tpk : originalTrainers.get(index).pokemon) {
                        if (tpk != null) {
                            log.println("<li>" + tpk.pokemon.name + " <em style=\"font-size: .75em\">Lv" + tpk.level + "</em></li>");
                        }
                    }
                    log.println("</ul>");

                    log.println("<em>New Team</em>");
                    log.println("<ul class=\"new-trainer-pk\">");
                    for (TrainerPokemon tpk : trainer.pokemon) {
                        if (tpk != null) {
                            log.println("<li>");

                            log.println("<table class=\"seemless\">");
                            log.println("<tr>");

                            log.println("<td>");
                            log.println("<div><b>" + tpk.pokemon.name + "</b> <em style=\"font-size: .75em\">Lv" + tpk.level + "</em></div>");
                            log.println("</td>");

                            log.println("<td>");
                            if (tpk.heldItem > 0)
                                log.print("<div class=\"attributes\"><mark>" + itemNames[tpk.heldItem] + "</mark></div>");
                            log.println("</td>");

                            log.println("</tr>");

                            Pokemon pkmn = tpk.pokemon;
                            String pkmnT1;
                            String pkmnT2;

                            if (pkmn.primaryType != null)
                                pkmnT1 = pkmn.primaryType.toString();
                            else
                                pkmnT1 = "???";
                            if (pkmn.secondaryType != null)
                                pkmnT2 = pkmn.secondaryType.toString();
                            else
                                pkmnT2 = "";

                            log.println("<tr>");

                            log.println("<td>");
                            log.println("<div style=\"float: left; font-size: 11px\">");
                            log.println("<span class=\"pk-type3 " + pkmnT1.toLowerCase() + "\">" + pkmnT1.toUpperCase() + "</span>");
                            if (pkmnT2 != "")
                                log.println("<span class=\"pk-type3 " + pkmnT2.toLowerCase() + "\">" + pkmnT2.toUpperCase() + "</span>");
                            log.println("</div>");
                            log.println("</td>");


                            log.println("<td>");
                            log.print("<div class=\"attributes\">" + romHandler.abilityName(romHandler.getAbilityForTrainerPokemon(tpk)) + "</div>");
                            log.println("</td>");

                            log.println("</tr>");

                            log.println("<table>");

                            /////////////////
                            log.println("<table class=\"new-trainer-pokemon-stats\">");

                            log.print("<tr>");
                            log.print("<td>HP</td>");
                            log.print("<td>ATK</td>");
                            log.print("<td>DEF</td>");

                            if (gen1)
                                log.print("<td>SPEC</td>");
                            else
                                log.print("<td>SPATK</td><td>SPDEF</td>");

                            log.print("<td>SPEED</td>");
                            log.println("</tr>");

                            log.println("<tr>");

                            log.println("<td>" + pkmn.hp + "</td>");
                            log.println("<td>" + pkmn.attack + "</td>");
                            log.println("<td>" + pkmn.defense + "</td>");

                            if (gen1) {
                                log.println("<td>" + pkmn.special + "</td>");
                            } else {
                                log.println("<td>" + pkmn.spatk + "</td>");
                                log.println("<td>" + pkmn.spdef + "</td>");
                            }

                            log.println("<td>" + pkmn.speed + "</td>");
                            log.println("</tr>");

                            log.println("</table>");
                            ////////////////////////

                            log.println("<table class=\"new-trainer-table\">");

                            for (int i = 0; i < 4; i++) {
                                int move;
                                if (trainer.pokemonHaveCustomMoves()) move = tpk.moves[i];
                                else move = romHandler.getPokemonLevelMoves(tpk)[i];

                                if (move != 0) {
                                    Move mv = moves.get(move);
                                    String mvType = (mv.type == null) ? "???" : mv.type.toString();

                                    if (i % 2 == 0)
                                        log.print("<tr>");
                                    else
                                        log.print("<tr id=\"alt\">");

                                    log.print("<td>" + mv.name);

                                    log.println("<table class=\"moveset-table\">");

                                    log.println("<td class=\"pk-type2 " + mvType.toLowerCase() + "\">" + mvType.toUpperCase() + "</td>");
                                    if (romHandler.hasPhysicalSpecialSplit())
                                        log.println("<td class=\"move-cat2 " + mv.category.toString().toLowerCase() + "\">" + mv.category.toString().substring(0, 3).toUpperCase() + "</td>");
                                    log.println("<td id=\"alt\">" + (mv.power <= 1 ? "-" : mv.power) + "</td>");
                                    log.println("<td>" + ((int) mv.hitratio <= 1 ? "-" : (int) mv.hitratio) + "</td>");
                                    log.println("<td id=\"alt\">" + mv.pp + "</td>");
                                    log.println("</table>");

                                    log.println("</td>");

                                    log.print("</tr>");
                                }
                            }

                            log.println("</table>");

                            log.println("</li>");
                        }
                    }
                    log.println("</ul>");

                    log.println("</div>");
                }
                index++;
            }
            log.println("</div>");
        }

        else{
            log.println("<h2 id=\"tp\">Trainers Pokemon</h2>");
            int index = 0;
            for (Trainer trainer : trainers) {
                if (trainer != null) {
                    log.println("<div class=\"trainer-box\">");

                    log.println("<div class=\"trainer\">");
                    log.println("<span class=\"trainer-name\"><em># " + index + "</em>" + trainer.getLogName());
                    if (trainer.offset != index && trainer.offset != 0)
                        log.println("<br /><em>@" + trainer.stringOffset() + "</em>");
                    log.println("</span>");

                    log.println("</div>");

                    log.println("<em>Old Team</em>");
                    log.println("<ul class=\"old-trainer-pk\">");
                    for (TrainerPokemon tpk : originalTrainers.get(index).pokemon) {
                        if (tpk != null) {
                            log.println("<li>" + tpk.pokemon.name + " <em style=\"font-size: .75em\">Lv" + tpk.level + "</em></li>");
                        }
                    }
                    log.println("</ul>");

                    log.println("<em>New Team</em>");
                    log.println("<ul class=\"new-trainer-pk\">");
                    for (TrainerPokemon tpk : trainer.pokemon) {
                        if (tpk != null) {
                            log.println("<li>");

                            log.println("<table class=\"seemless\">");
                            log.println("<tr>");

                            log.println("<td>");
                            log.println("<div><b>" + tpk.pokemon.name + "</b> <em style=\"font-size: .75em\">Lv" + tpk.level + "</em></div>");
                            log.println("</td>");

                            log.println("<td>");
                            if (tpk.heldItem > 0)
                                log.print("<div class=\"attributes\"><mark>" + itemNames[tpk.heldItem] + "</mark></div>");
                            log.println("</td>");

                            log.println("</tr>");

                            Pokemon pkmn = tpk.pokemon;
                            String pkmnT1;
                            String pkmnT2;

                            if (pkmn.primaryType != null)
                                pkmnT1 = pkmn.primaryType.toString();
                            else
                                pkmnT1 = "???";
                            if (pkmn.secondaryType != null)
                                pkmnT2 = pkmn.secondaryType.toString();
                            else
                                pkmnT2 = "";

                            log.println("<tr>");

                            log.println("<td>");
                            log.println("<div style=\"float: left; font-size: 11px\">");
                            log.println("<span class=\"pk-type3 " + pkmnT1.toLowerCase() + "\">" + pkmnT1.toUpperCase() + "</span>");
                            if (pkmnT2 != "")
                                log.println("<span class=\"pk-type3 " + pkmnT2.toLowerCase() + "\">" + pkmnT2.toUpperCase() + "</span>");
                            log.println("</div>");
                            log.println("</td>");


                            log.println("<td>");
                            log.print("<div class=\"attributes\">" + romHandler.abilityName(romHandler.getAbilityForTrainerPokemon(tpk)) + "</div>");
                            log.println("</td>");

                            log.println("</tr>");

                            log.println("<table>");

                            /////////////////
                            log.println("<table class=\"new-trainer-pokemon-stats\">");

                            log.print("<tr>");
                            log.print("<td>HP</td>");
                            log.print("<td>ATK</td>");
                            log.print("<td>DEF</td>");

                            if (gen1)
                                log.print("<td>SPEC</td>");
                            else
                                log.print("<td>SPATK</td><td>SPDEF</td>");

                            log.print("<td>SPEED</td>");
                            log.println("</tr>");

                            log.println("<tr>");

                            log.println("<td>" + pkmn.hp + "</td>");
                            log.println("<td>" + pkmn.attack + "</td>");
                            log.println("<td>" + pkmn.defense + "</td>");

                            if (gen1) {
                                log.println("<td>" + pkmn.special + "</td>");
                            } else {
                                log.println("<td>" + pkmn.spatk + "</td>");
                                log.println("<td>" + pkmn.spdef + "</td>");
                            }

                            log.println("<td>" + pkmn.speed + "</td>");
                            log.println("</tr>");

                            log.println("</table>");
                            ////////////////////////

                            log.println("<table class=\"new-trainer-table\">");

                            for (int i = 0; i < 4; i++) {
                                int move;
                                if (trainer.pokemonHaveCustomMoves()) move = tpk.moves[i];
                                else move = romHandler.getPokemonLevelMoves(tpk)[i];

                                if (move != 0) {
                                    Move mv = moves.get(move);
                                    String mvType = (mv.type == null) ? "???" : mv.type.toString();

                                    if (i % 2 == 0)
                                        log.print("<tr>");
                                    else
                                        log.print("<tr id=\"alt\">");

                                    log.print("<td>" + mv.name);

                                    log.println("<table class=\"moveset-table\">");

                                    log.println("<td class=\"pk-type2 " + mvType.toLowerCase() + "\">" + mvType.toUpperCase() + "</td>");
                                    if (romHandler.hasPhysicalSpecialSplit())
                                        log.println("<td class=\"move-cat2 " + mv.category.toString().toLowerCase() + "\">" + mv.category.toString().substring(0, 3).toUpperCase() + "</td>");
                                    log.println("<td id=\"alt\">" + (mv.power <= 1 ? "-" : mv.power) + "</td>");
                                    log.println("<td>" + ((int) mv.hitratio <= 1 ? "-" : (int) mv.hitratio) + "</td>");
                                    log.println("<td id=\"alt\">" + mv.pp + "</td>");
                                    log.println("</table>");

                                    log.println("</td>");

                                    log.print("</tr>");
                                }
                            }

                            log.println("</table>");

                            log.println("</li>");
                        }
                    }
                    log.println("</ul>");

                    log.println("</div>");
                }
                index++;
            }
        }

        log.println("<!-- Preserves spacing between trainer divs. Intentionally left empty -->");
        log.println("<div class=\"clear\"></div>");

        log.println("<!---");


        // Non-HTML Log
        /*
        log.println("--Trainers Pokemon--");
        List<Trainer> trainers = romHandler.getTrainers();
        for (Trainer t : trainers) {
            log.print("#" + t.index + " ");
            String originalTrainerName = originalTrainerNames.get(t.index);
            String currentTrainerName = "";
            if (t.fullDisplayName != null) {
                currentTrainerName = t.fullDisplayName;
            } else if (t.name != null) {
                currentTrainerName = t.name;
            }
            if (!currentTrainerName.isEmpty()) {
                if (trainerNamesChanged) {
                    log.printf("(%s => %s)", originalTrainerName, currentTrainerName);
                } else {
                    log.printf("(%s)", currentTrainerName);
                }
            }
            if (t.offset != 0) {
                log.printf("@%X", t.offset);
            }

            String[] itemNames = romHandler.getItemNames();
            if (logTrainerMovesets) {
                log.println();
                for (TrainerPokemon tpk : t.pokemon) {
                    List<Move> moves = romHandler.getMoves();
                    log.printf(tpk.toString(), itemNames[tpk.heldItem]);
                    log.print(", Ability: " + romHandler.abilityName(romHandler.getAbilityForTrainerPokemon(tpk)));
                    log.print(" - ");
                    boolean first = true;
                    for (int move : tpk.moves) {
                        if (move != 0) {
                            if (!first) {
                                log.print(", ");
                            }
                            log.print(moves.get(move).name);
                            first = false;
                        }
                    }
                    log.println();
                }
            } else {
                log.print(" - ");
                boolean first = true;
                for (TrainerPokemon tpk : t.pokemon) {
                    if (!first) {
                        log.print(", ");
                    }
                    log.printf(tpk.toString(), itemNames[tpk.heldItem]);
                    first = false;
                }
            }
            log.println();
        }
         */

    }

    private int logStaticPokemon(final PrintStream log, int checkValue, List<StaticEncounter> oldStatics) {

        List<StaticEncounter> newStatics = romHandler.getStaticPokemon();
        Map<String, Integer> seenPokemon = new TreeMap<>();

        log.println("--->");

        log.println("<h2 id=\"sp\">Static Pokemon</h2>");
        log.println("<ul>");

        for (int i = 0; i < oldStatics.size(); i++) {
            StaticEncounter oldP = oldStatics.get(i);
            StaticEncounter newP = newStatics.get(i);
            checkValue = addToCV(checkValue, newP.pkmn.number);

            log.println("<li>" + oldP.pkmn.name + " => " + newP.pkmn.name + "</li>");
        }


        log.println("<!---");

        /*
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
        */

        return checkValue;
    }

    private int logTotemPokemon(final PrintStream log, int checkValue, List<TotemPokemon> oldTotems) {

        List<TotemPokemon> newTotems = romHandler.getTotemPokemon();
        String[] itemNames = romHandler.getItemNames();


        /*
        log.println("<h2 id=\"ttp\">Trainers Pokemon</h2>");
        int index = 0;
        for (int i = 0; i < oldTotems.size(); i++) {
            TotemPokemon oldP = oldTotems.get(i);
            TotemPokemon newP = newTotems.get(i);
            checkValue = addToCV(checkValue, newP.pkmn.number);

            log.println("<div class=\"trainer-box\">");


            log.println("<em>Old Totem</em>");
            log.println("<ul class=\"old-trainer-pk\">");
            for (TrainerPokemon tpk : originalTrainers.get(index).pokemon) {
                if (tpk != null) {
                    log.println("<li>" + oldP.pkmn.fullName() + "</li>");
                }
            }
            log.println("</ul>");

            log.println("<em>New Totem</em>");
            log.println("<ul class=\"new-trainer-pk\">");

//            log.println("<li>" + tpk.pokemon.name + " <em>Lv" + tpk.level + "</em>");
//            log.print("<div id=\"attributes\">"+ romHandler.abilityName(romHandler.getAbilityForTrainerPokemon(tpk)) + "&nbsp;");

            log.print("<li>" + newP.toString());
            log.print("<div id=\"attributes\">");

            if (newP.heldItem > 0)
                log.println("<mark>&nbsp;" + itemNames[newP.heldItem] + "&nbsp;</mark></div>");
            else
                log.println("</div>");


//            log.println("<table class=\"new-trainer-table\">");
//
//            for (int i = 0; i < 4; i++) {
//                int move;
//                if (trainer.pokemonHaveCustomMoves()) move = tpk.moves[i];
//                else move = romHandler.getPokemonLevelMoves(tpk)[i];
//
//                if (move != 0) {
//                    if (i%2 == 0)
//                        log.print("<tr>");
//                    else
//                        log.print("<tr id=\"alt\">");
//
//                    log.print("<td>" + moves.get(move).name + "</td>");
//
//                    log.print("</tr>");
//                }
//            }

            log.println("</table>");


            log.println("</li>");

            log.println("</ul>");

            log.println("</div>");

            index++;
        }

        */



        log.println("--Totem Pokemon--");
        for (int i = 0; i < oldTotems.size(); i++) {
            TotemPokemon oldP = oldTotems.get(i);
            TotemPokemon newP = newTotems.get(i);

            checkValue = addToCV(checkValue, newP.pkmn.number);
            log.println(oldP.pkmn.fullName() + " =>");
            log.printf(newP.toString(),itemNames[newP.heldItem]);
        }
        log.println();

        return checkValue;
    }

    private void logMoveChanges(final PrintStream log) {
        List<Move> allMoves = romHandler.getMoves();

        /*  Non-HTML Log
        log.println("--Move Data--");
        log.print("NUM|NAME           |TYPE    |POWER|ACC.|PP");
        if (romHandler.hasPhysicalSpecialSplit()) {
            log.print(" |CATEGORY");
        }
        log.println();

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
         */

        log.println("--->");

        log.println("<h2 id=\"md\">Move Data</h2>");
        log.println("<h3>NOTE: Phy/Spe is not split in Gen 1-3. Based on type.</h3>");
        log.println("<table class=\"moves-table\">");
        log.println("<tr>");
        log.println("<th>NUM</th>");
        log.println("<th>NAME</th>");
        log.println("<th>TYPE</th>");
        if (romHandler.hasPhysicalSpecialSplit())
            log.println("<th>CAT.</th>");
        log.println("<th>POWER</th>");
        log.println("<th>ACC.</th>");
        log.println("<th>PP</th>");
        //log.println("<th>EFF.</th>");
        log.println("</tr>");

        int index = 0;
        for (Move mv : allMoves) {
            if (mv != null) {

                String mvType = (mv.type == null) ? "???" : mv.type.toString();

                if (index % 2 == 0)
                    log.println("<tr>");
                else
                    log.println("<tr class=\"alt\">");

                log.println("<td>"+mv.internalId+"</td>");
                log.println("<td class=\"left\">"+mv.name+"</td>");
                log.println("<td><span class=\"pk-type " + mvType.toLowerCase() + "\">" + mvType.toUpperCase() + "</span></td>");
                if (romHandler.hasPhysicalSpecialSplit())
                    log.println("<td><span class=\"move-cat "+ mv.category.toString().toLowerCase() + "\">" + mv.category.toString().substring(0,3).toUpperCase() + "</span></td>");
                log.println("<td>"+ (mv.power <= 1 ? "-" : mv.power) +"</td>");
                log.println("<td>"+ ((int)mv.hitratio <= 1 || (int)mv.hitratio > 100 ? "-" : (int)mv.hitratio) +"</td>");
                log.print("<td>"+mv.pp+"</td>");
                //log.print("<td>"+mv.efficacyTier+"</td>");
                log.println("</tr>");
                log.println("</tr>");
            }
            index++;
        }
        log.println("</table>");

        log.println("<!---");
    }

    private void logShops(final PrintStream log) {

        log.println("--->");
        log.println("<h2 id=\"si\">Shop Items</h2>");

        String[] itemNames = romHandler.getItemNames();
        Map<Integer, Shop> shopsDict = romHandler.getShopItems();

        log.println("<ul>");
        for (int shopID : shopsDict.keySet()) {
            Shop shop = shopsDict.get(shopID);
            log.println("<li>" + shop.name);

            log.println("<ul>");
            List<Integer> shopItems = shop.items;
            for (int shopItemID : shopItems) {
                log.print("<li>" + itemNames[shopItemID] + "</li>");
            }
            log.println("</ul>");

            log.println("</li>");
        }
        log.println("</ul>");

        log.println("<!---");

        // Non-HTML log
//        log.println("--Shops--");
//        for (int shopID : shopsDict.keySet()) {
//            Shop shop = shopsDict.get(shopID);
//            log.printf("%s", shop.name);
//            log.println();
//            List<Integer> shopItems = shop.items;
//            for (int shopItemID : shopItems) {
//                log.printf("- %5s", itemNames[shopItemID]);
//                log.println();
//            }
//
//            log.println();
//        }

        log.println();
    }

    private void logPickupItems(final PrintStream log) {
        List<PickupItem> pickupItems = romHandler.getPickupItems();
        String[] itemNames = romHandler.getItemNames();

        log.println("--->");

        log.println("<h2 id=\"pu\">Pickup Items</h2>");
        log.println("<table class=\"pk-table\">");

        for (int levelRange = 0; levelRange < 10; levelRange++) {
            int startingLevel = (levelRange * 10) + 1;
            int endingLevel = (levelRange + 1) * 10;

            log.println("<tr class=\"alt2\"><td /><td style=\" text-align: left;\">" +
                    "<b>Levels " + startingLevel + "-" + endingLevel + "</b></td></tr>");

            TreeMap<Integer, List<String>> itemListPerProbability = new TreeMap<>();
            for (PickupItem pickupItem : pickupItems) {
                int probability = pickupItem.probabilities[levelRange];
                if (itemListPerProbability.containsKey(probability)) {
                    itemListPerProbability.get(probability).add(itemNames[pickupItem.item]);
                } else if (probability > 0) {
                    List<String> itemList = new ArrayList<>();
                    itemList.add(itemNames[pickupItem.item]);
                    itemListPerProbability.put(probability, itemList);
                }
            }

            int rowSubCount = 0;
            for (Map.Entry<Integer, List<String>> itemListPerProbabilityEntry : itemListPerProbability.descendingMap().entrySet()) {
                int probability = itemListPerProbabilityEntry.getKey();
                List<String> itemList = itemListPerProbabilityEntry.getValue();
                Collections.sort(itemList);
                String itemsString = String.join(", ", itemList);

                if (rowSubCount%2 == 0)
                    log.print("<tr>");
                else
                    log.print("<tr class=\"alt\">");
                log.print("<td>");
                log.print(probability + "%");
                log.print("</td>");
                log.print("<td style=\"text-align: left;\">");
                log.print(itemsString);
                log.print("</td>");
                log.println("</tr>");
                rowSubCount++;
            }
            log.println("</tr>");
        }

        log.println("</table>");

        log.println("<!---");

        // Non-HTML log
//        log.println("--Pickup Items--");
//        for (int levelRange = 0; levelRange < 10; levelRange++) {
//            int startingLevel = (levelRange * 10) + 1;
//            int endingLevel = (levelRange + 1) * 10;
//            log.printf("Level %s-%s", startingLevel, endingLevel);
//            log.println();
//            TreeMap<Integer, List<String>> itemListPerProbability = new TreeMap<>();
//            for (PickupItem pickupItem : pickupItems) {
//                int probability = pickupItem.probabilities[levelRange];
//                if (itemListPerProbability.containsKey(probability)) {
//                    itemListPerProbability.get(probability).add(itemNames[pickupItem.item]);
//                } else if (probability > 0) {
//                    List<String> itemList = new ArrayList<>();
//                    itemList.add(itemNames[pickupItem.item]);
//                    itemListPerProbability.put(probability, itemList);
//                }
//            }
//            for (Map.Entry<Integer, List<String>> itemListPerProbabilityEntry : itemListPerProbability.descendingMap().entrySet()) {
//                int probability = itemListPerProbabilityEntry.getKey();
//                List<String> itemList = itemListPerProbabilityEntry.getValue();
//                String itemsString = String.join(", ", itemList);
//                log.printf("%d%%: %s", probability, itemsString);
//                log.println();
//            }
//            log.println();
//        }


        log.println();
    }

    private List<String> getTrainerNames() {
        List<String> trainerNames = new ArrayList<>();
        trainerNames.add(""); // for index 0
        List<Trainer> trainers = romHandler.getTrainers();
        for (Trainer t : trainers) {
            if (t.fullDisplayName != null) {
                trainerNames.add(t.fullDisplayName);
            } else if (t.name != null) {
                trainerNames.add(t.name);
            } else {
                trainerNames.add("");
            }
        }
        return trainerNames;
    }

    
    private static int addToCV(int checkValue, int... values) {
        for (int value : values) {
            checkValue = Integer.rotateLeft(checkValue, 3);
            checkValue ^= value;
        }
        return checkValue;
    }

    private String getLogCss(){
        return "/*----------------------------------------------------------------------------*/\n" +
                "/*--  log.css - Contains stylesheet for log produced at end of              --*/\n" +
                "/*--  randomization.                                                        --*/\n" +
                "/*--                                                                        --*/\n" +
                "/*--  Part of \"Universal Pokemon Randomizer\" by Dabomstew                   --*/\n" +
                "/*--  Pokemon and any associated names and the like are                     --*/\n" +
                "/*--  trademark and (C) Nintendo 1996-2012.                                 --*/\n" +
                "/*--                                                                        --*/\n" +
                "/*--  The custom code written here is licensed under the terms of the GPL:  --*/\n" +
                "/*--                                                                        --*/\n" +
                "/*--  This program is free software: you can redistribute it and/or modify  --*/\n" +
                "/*--  it under the terms of the GNU General Public License as published by  --*/\n" +
                "/*--  the Free Software Foundation, either version 3 of the License, or     --*/\n" +
                "/*--  (at your option) any later version.                                   --*/\n" +
                "/*--                                                                        --*/\n" +
                "/*--  This program is distributed in the hope that it will be useful,       --*/\n" +
                "/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/\n" +
                "/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/\n" +
                "/*--  GNU General Public License for more details.                          --*/\n" +
                "/*--                                                                        --*/\n" +
                "/*--  You should have received a copy of the GNU General Public License     --*/\n" +
                "/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/\n" +
                "/*----------------------------------------------------------------------------*/\n" +
                "#top {\n" +
                "  position: fixed;\n" +
                "  top: 10px;\n" +
                "  z-index 999;\n" +
                "}\n" +
                "\n" +
                "body {\n" +
                "  background-color: var(--background-color);\n" +
                "  color: var(--text-contrast);\n" +
                "  font-family: Arial, Helvetica, sans-serif;\n" +
                "  padding: 20px 60px;\n" +
                "}\n" +
                "\n" +
                "h2 {\n" +
                "  font-size: 1.8em;\n" +
                "}\n" +
                "\n" +
                "h3 {\n" +
                "  font-size: 1.3em;\n" +
                "}\n" +
                "\n" +
                "table {\n" +
                "  border: 2px solid #444;\n" +
                "  border-collapse: collapse;\n" +
                "  empty-cells: show;\n" +
                "}\n" +
                "\n" +
                "th,\n" +
                "td {\n" +
                "  padding: 6px 8px;\n" +
                "  border-right: 1px solid #444;\n" +
                "  border-left: 1px solid #444;\n" +
                "}\n" +
                "\n" +
                "th {\n" +
                "  position: -webkit-sticky;\n" +
                "  position: sticky;\n" +
                "  top: -1px;\n" +
                "  z-index: 1;\n" +
                "}\n" +
                "\n" +
                "td {\n" +
                "  text-align: center;\n" +
                "}\n" +
                "\n" +
                ".seemless {\n" +
                "  width: 100%;\n" +
                "  border: 0;\n" +
                "  border-collapse: collapse;\n" +
                "  empty-cells: show;\n" +
                "}\n" +
                "\n" +
                ".seemless td {\n" +
                "  width: 50%;\n" +
                "  padding: 0px;\n" +
                "  border-right: 0px solid #444;\n" +
                "  border-left: 0px solid #444;\n" +
                "}\n" +
                "\n" +
                ".seemless td > div{\n" +
                "  text-align: left;\n" +
                "}\n" +
                "\n" +
                ".seemless th {\n" +
                "  position: -webkit-sticky;\n" +
                "  position: sticky;\n" +
                "  top: 0px;\n" +
                "  z-index: 1;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "ul#toc {\n" +
                "  position: fixed;\n" +
                "  top: 10px;\n" +
                "  display: inline;\n" +
                "  z-index: 999;\n" +
                "}\n" +
                "ul#toc li {\n" +
                "  display: inline;\n" +
                "  z-index: 999;\n" +
                "}\n" +
                "\n" +
                "li {\n" +
                "  margin-top: 4px;\n" +
                "  margin-bottom: 4px;\n" +
                "}\n" +
                "\n" +
                ".clear {\n" +
                "  clear: both;\n" +
                "}\n" +
                "\n" +
                ".pk-table {\n" +
                "  border-color: var(--pk-border);\n" +
                "}\n" +
                "\n" +
                ".pk-table th {\n" +
                "  background-color: var(--pk-title);\n" +
                "  color: #eee;\n" +
                "  border-color: inherit;\n" +
                "}\n" +
                "\n" +
                ".pk-table tr {\n" +
                "  background-color: var(--pk-row-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".pk-table tr.alt2 {\n" +
                "  background-color: var(--pk-row-alt2-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".pk-table tr.alt {\n" +
                "  background-color: var(--pk-row-alt-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".pk-table td {\n" +
                "  border-color: inherit;\n" +
                "}\n" +
                "\n" +
                ".moves-table {\n" +
                "  border-color: var(--moves-border);\n" +
                "}\n" +
                "\n" +
                ".moves-table th {\n" +
                "  background-color: var(--moves-title);\n" +
                "  color: #eee;\n" +
                "  border-color: inherit;\n" +
                "}\n" +
                "\n" +
                ".moves-table tr {\n" +
                "  background-color: var(--moves-row-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".moves-table tr.alt {\n" +
                "  background-color: var(--moves-row-alt-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".moves-table td {\n" +
                "  border-color: inherit;\n" +
                "}\n" +
                "\n" +
                ".tm-table {\n" +
                "  border-color: var(--tm-border);\n" +
                "}\n" +
                "\n" +
                ".tm-table th {\n" +
                "  background-color: var(--tm-title);\n" +
                "  color: #eee;\n" +
                "  border-color: inherit;\n" +
                "}\n" +
                "\n" +
                ".tm-table tr {\n" +
                "  background-color: var(--tm-row-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".tm-table tr.alt {\n" +
                "  background-color: var(--tm-row-alt-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".tm-table td {\n" +
                "  border-color: inherit;\n" +
                "}\n" +
                "\n" +
                ".trades-table {\n" +
                "  border-color: var(--trades-border);\n" +
                "}\n" +
                "\n" +
                ".trades-table th {\n" +
                "  background-color: var(--trades-title);\n" +
                "  color: #eee;\n" +
                "  border-color: inherit;\n" +
                "}\n" +
                "\n" +
                ".trades-table tr {\n" +
                "  background-color: var(--trades-row-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".trades-table tr.alt {\n" +
                "  background-color: var(--trades-row-alt-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".trades-table td {\n" +
                "  border-color: inherit;\n" +
                "  font-weight: bold;\n" +
                "}\n" +
                "\n" +
                ".trades-table tr.alt td {\n" +
                "  border-color: inherit;\n" +
                "  font-weight: lighter;\n" +
                "  font-style: italic;\n" +
                "  color: gray;\n" +
                "}\n" +
                "\n" +
                ".left {\n" +
                "  text-align: left;\n" +
                "}\n" +
                "\n" +
                ".error {\n" +
                "  color: red;\n" +
                "  padding: 3px 10px;\n" +
                "  border: solid 1px red;\n" +
                "  font-style: italic;\n" +
                "  background-color: #ffcbcb;\n" +
                "  border-radius: 4px;\n" +
                "}\n" +
                "\n" +
                ".success {\n" +
                "  color: green;\n" +
                "  padding: 3px 10px;\n" +
                "  border: solid 1px green;\n" +
                "  font-style: italic;\n" +
                "  background-color: #ccffcc;\n" +
                "  border-radius: 4px;\n" +
                "}\n" +
                "\n" +
                ".black-background {\n" +
                "  color: white;\n" +
                "  padding: 3px 10px;\n" +
                "  border: solid 1px white;\n" +
                "  font-style: italic;\n" +
                "  background-color: black;\n" +
                "  border-radius: 4px;\n" +
                "}\n" +
                "\n" +
                ".pk-type {\n" +
                "  font-family: \"Lucida Console\", Monaco, monospace;\n" +
                "  font-weight: bold;\n" +
                "  border-radius: 4px;\n" +
                "  color: #eee;\n" +
                "  padding: 4px 6px;\n" +
                "  margin: 2px;\n" +
                "  font-size: 0.9em;\n" +
                "  text-shadow: 0px 1px 2px #333;\n" +
                "  text-transform: uppercase;\n" +
                "  background-color: #333;\n" +
                "  display: inline-block;\n" +
                "}\n" +
                ".pk-type2 {\n" +
                "  font-family: \"Lucida Console\", Monaco, monospace;\n" +
                "  font-weight: bold;\n" +
                "  border-radius: 4px;\n" +
                "  color: #eee;\n" +
                "  font-size: 0.9em;\n" +
                "  text-shadow: 0px 1px 2px #333;\n" +
                "  text-transform: uppercase;\n" +
                "  background-color: #333;\n" +
                "}\n" +
                ".pk-type3 {\n" +
                "  font-family: \"Lucida Console\", Monaco, monospace;\n" +
                "  font-weight: bold;\n" +
                "  border-radius: 4px;\n" +
                "  color: #eee;\n" +
                "  padding: 4px 6px;\n" +
                "  margin: 1px -1px 1px -1px;\n" +
                "  font-size: 0.9em;\n" +
                "  text-shadow: 0px 1px 2px #333;\n" +
                "  text-transform: uppercase;\n" +
                "  background-color: #333;\n" +
                "  display: inline-block;\n" +
                "}\n" +
                "\n" +
                ".normal {\n" +
                "  background-color: var(--type-normal-color);\n" +
                "}\n" +
                "\n" +
                ".grass {\n" +
                "  background-color: var(--type-grass-color);\n" +
                "}\n" +
                "\n" +
                ".fire {\n" +
                "  background-color: var(--type-fire-color);\n" +
                "}\n" +
                "\n" +
                ".water {\n" +
                "  background-color: var(--type-water-color);\n" +
                "}\n" +
                "\n" +
                ".electric {\n" +
                "  background-color: var(--type-electric-color);\n" +
                "}\n" +
                "\n" +
                ".rock {\n" +
                "  background-color: var(--type-rock-color);\n" +
                "}\n" +
                "\n" +
                ".ground {\n" +
                "  background-color: var(--type-ground-color);\n" +
                "}\n" +
                "\n" +
                ".poison {\n" +
                "  background-color: var(--type-poison-color);\n" +
                "}\n" +
                "\n" +
                ".flying {\n" +
                "  background-color: var(--type-flying-color);\n" +
                "}\n" +
                "\n" +
                ".ice {\n" +
                "  background-color: var(--type-ice-color);\n" +
                "}\n" +
                "\n" +
                ".psychic {\n" +
                "  background-color: var(--type-psychic-color);\n" +
                "}\n" +
                "\n" +
                ".ghost {\n" +
                "  background-color: var(--type-ghost-color);\n" +
                "}\n" +
                "\n" +
                ".bug {\n" +
                "  background-color: var(--type-bug-color);\n" +
                "}\n" +
                "\n" +
                ".fighting {\n" +
                "  background-color: var(--type-fighting-color);\n" +
                "}\n" +
                "\n" +
                ".dragon {\n" +
                "  background-color: var(--type-dragon-color);\n" +
                "}\n" +
                "\n" +
                ".dark {\n" +
                "  background-color: var(--type-dark-color);\n" +
                "}\n" +
                "\n" +
                ".steel {\n" +
                "  background-color: var(--type-steel-color);\n" +
                "}\n" +
                "\n" +
                ".fairy {\n" +
                "  background-color: var(--type-fairy-color);\n" +
                "}\n" +
                "\n" +
                ".move-cat {\n" +
                "  font-family: \"Lucida Console\", Monaco, monospace;\n" +
                "  font-weight: bold;\n" +
                "  border-radius: 4px;\n" +
                "  color: #eee;\n" +
                "  padding: 4px 10px;\n" +
                "  margin: 2px;\n" +
                "  font-size: 0.9em;\n" +
                "  text-shadow: 0px 1px 2px #333;\n" +
                "  text-transform: uppercase;\n" +
                "  background-color: #333;\n" +
                "  display: inline-block;\n" +
                "}\n" +
                ".move-cat2 {\n" +
                "  font-family: \"Lucida Console\", Monaco, monospace;\n" +
                "  font-weight: bold;\n" +
                "  border-radius: 4px;\n" +
                "  color: #eee;\n" +
                "  font-size: 0.9em;\n" +
                "  text-shadow: 0px 1px 2px #333;\n" +
                "  text-transform: uppercase;\n" +
                "  background-color: #333;\n" +
                "}\n" +
                "\n" +
                ".physical {\n" +
                "  background-color: var(--cat-physical-color);\n" +
                "}\n" +
                ".special {\n" +
                "  background-color: var(--cat-special-color);\n" +
                "}\n" +
                ".status {\n" +
                "  background-color: var(--cat-status-color);\n" +
                "}\n" +
                "\n" +
                ".tm-element {\n" +
                "  font-family: \"Lucida Console\", Monaco, monospace;\n" +
                "  font-weight: bold;\n" +
                "  border-radius: 4px;\n" +
                "  color: #eee;\n" +
                "  padding: 4px 10px;\n" +
                "  margin: 2px;\n" +
                "  font-size: 1em;\n" +
                "  text-shadow: 0px 1px 2px #333;\n" +
                "  text-transform: uppercase;\n" +
                "  background-color: var(--tm-color);\n" +
                "  display: inline-block;\n" +
                "}\n" +
                "\n" +
                ".moveset {\n" +
                "  padding-left: 20px;\n" +
                "}\n" +
                "\n" +
                ".moveset > li {\n" +
                "  min-width: 90px;\n" +
                "  border-radius: 3px;\n" +
                "  display: inline-block;\n" +
                "  background-color: var(--moveset-color);\n" +
                "  border: 2px solid var(--moveset-border);\n" +
                "  padding: 2px 6px;\n" +
                "  margin: 1px;\n" +
                "  color: #358;\n" +
                "}\n" +
                "\n" +
                ".moveset > li > strong {\n" +
                "  display: block;\n" +
                "}\n" +
                "\n" +
                ".moveset > li > em {\n" +
                "  display: block;\n" +
                "  font-size: 0.8em;\n" +
                "  text-align: right;\n" +
                "}\n" +
                "\n" +
                ".moveset-table {\n" +
                "  width: 100%;\n" +
                "  border: 0px;\n" +
                "  empty-cells: hide;\n" +
                "  color: #333;\n" +
                "  font-size: 0.9em;\n" +
                "}\n" +
                "\n" +
                ".moveset-table td {\n" +
                "  width: 20%;\n" +
                "  border-right: 0px;\n" +
                "  border-left: 0px;\n" +
                "  white-space: nowrap;\n" +
                "}\n" +
                "\n" +
                ".moveset-table td#alt {\n" +
                "  width: 20%;\n" +
                "  border-right: 0px;\n" +
                "  border-left: 0px;\n" +
                "  white-space: nowrap;\n" +
                "  background-color: rgba(0,0,0,.1)\n" +
                "}\n" +
                "\n" +
                ".moveset-table th,\n" +
                "td {\n" +
                "  padding: 0px 2px;\n" +
                "}\n" +
                "\n" +
                ".new-trainer-pokemon-stats {\n" +
                "  width: 100%;\n" +
                "  border: 0px;\n" +
                "  empty-cells: hide;\n" +
                "  color: #333;\n" +
                "  font-size: 0.9em;\n" +
                "}\n" +
                "\n" +
                ".new-trainer-pokemon-stats td {\n" +
                "  width: 20%;\n" +
                "  border-right: 0px;\n" +
                "  border-left: 0px;\n" +
                "  white-space: nowrap;\n" +
                "}\n" +
                "\n" +
                ".new-trainer-pokemon-stats td#alt {\n" +
                "  width: 20%;\n" +
                "  border-right: 0px;\n" +
                "  border-left: 0px;\n" +
                "  white-space: nowrap;\n" +
                "  background-color: rgba(0,0,0,.1)\n" +
                "}\n" +
                "\n" +
                ".new-trainer-pokemon-stats th,\n" +
                "td {\n" +
                "  padding: 0px 2px;\n" +
                "}\n" +
                "\n" +
                ".tm-list {\n" +
                "  list-style: none;\n" +
                "  padding-left: 20px;\n" +
                "}\n" +
                "\n" +
                ".tm-list > li {\n" +
                "  margin: 8px 0;\n" +
                "}\n" +
                "\n" +
                ".tm-list > li > strong {\n" +
                "  border-radius: 3px;\n" +
                "  font-size: 1.2em;\n" +
                "  display: inline-block;\n" +
                "  color: #eee;\n" +
                "  background-color: var(--tm-color);\n" +
                "  padding: 5px 10px;\n" +
                "  margin-right: 10px;\n" +
                "}\n" +
                "\n" +
                ".wild-pk-set {\n" +
                "  padding: 10px 20px;\n" +
                "  border-radius: 6px 6px 0 6px;\n" +
                "}\n" +
                "\n" +
                ".pk-set-list {\n" +
                "  list-style: none;\n" +
                "  border-radius: 0 0 10px 10px;\n" +
                "  margin: 0 0 40px 20px;\n" +
                "  padding: 10px 20px;\n" +
                "}\n" +
                "\n" +
                ".pk-set-list > li {\n" +
                "  display: inline-block;\n" +
                "  border-radius: 5px;\n" +
                "  padding: 8px 18px;\n" +
                "  margin: 4px;\n" +
                "}\n" +
                "\n" +
                ".pk-set-list > li em {\n" +
                "  font-size: 0.8em;\n" +
                "}\n" +
                "\n" +
                ".pk-set-grass {\n" +
                "  background-color: var(--encounter-grass-title);\n" +
                "  color: #eee;\n" +
                "}\n" +
                "\n" +
                ".pk-list-grass {\n" +
                "  background-color: var(--encounter-grass-background);\n" +
                "}\n" +
                "\n" +
                ".pk-list-grass > li {\n" +
                "  background-color: var(--encounter-grass-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".pk-set-doubles-grass {\n" +
                "  background-color: var(--encounter-doubles-title);\n" +
                "  color: #eee;\n" +
                "}\n" +
                "\n" +
                ".pk-list-doubles-grass {\n" +
                "  background-color: var(--encounter-doubles-background);\n" +
                "}\n" +
                "\n" +
                ".pk-list-doubles-grass > li {\n" +
                "  background-color: var(--encounter-doubles-color);\n" +
                "  color: #eee;\n" +
                "}\n" +
                "\n" +
                ".pk-set-surfing {\n" +
                "  background-color: var(--encounter-surfing-title);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".pk-list-surfing {\n" +
                "  background-color: var(--encounter-surfing-background);\n" +
                "}\n" +
                "\n" +
                ".pk-list-surfing > li {\n" +
                "  background-color: var(--encounter-surfing-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".pk-set-fishing {\n" +
                "  background-color: var(--encounter-fishing-title);\n" +
                "  color: #eee;\n" +
                "}\n" +
                "\n" +
                ".pk-list-fishing {\n" +
                "  background-color: var(--encounter-fishing-background);\n" +
                "}\n" +
                "\n" +
                ".pk-list-fishing > li {\n" +
                "  background-color: var(--encounter-fishing-color);\n" +
                "  color: #eee;\n" +
                "}\n" +
                "\n" +
                ".pk-set-rock-smash {\n" +
                "  background-color: var(--encounter-rocksmash-title);\n" +
                "  color: #eee;\n" +
                "}\n" +
                "\n" +
                ".pk-list-rock-smash {\n" +
                "  background-color: var(--encounter-rocksmash-background);\n" +
                "}\n" +
                "\n" +
                ".pk-list-rock-smash > li {\n" +
                "  background-color: var(--encounter-rocksmash-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".pk-set-poke-radar {\n" +
                "  background-color: var(--encounter-radar-title);\n" +
                "  color: #eee;\n" +
                "}\n" +
                "\n" +
                ".pk-list-poke-radar {\n" +
                "  background-color: var(--encounter-radar-background);\n" +
                "}\n" +
                "\n" +
                ".pk-list-poke-radar > li {\n" +
                "  background-color: var(--encounter-radar-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".pk-set-shaking-spot {\n" +
                "  background-color: var(--encounter-shaking-title);\n" +
                "  color: #eee;\n" +
                "}\n" +
                "\n" +
                ".pk-list-shaking-spot {\n" +
                "  background-color: var(--encounter-shaking-background);\n" +
                "}\n" +
                "\n" +
                ".pk-list-shaking-spot > li {\n" +
                "  background-color: var(--encounter-shaking-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".pk-set-headbutt-trees {\n" +
                "  background-color: var(--encounter-headbutt-title);\n" +
                "  color: #eee;\n" +
                "}\n" +
                "\n" +
                ".pk-list-headbutt-trees {\n" +
                "  background-color: var(--encounter-headbutt-background);\n" +
                "}\n" +
                "\n" +
                ".pk-list-headbutt-trees > li {\n" +
                "  background-color: var(--encounter-headbutt-color);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".pk-set-bug-catching-contest {\n" +
                "  background-color: var(--encounter-bcc-title);\n" +
                "  color: #eee;\n" +
                "}\n" +
                "\n" +
                ".pk-list-bug-catching-contest {\n" +
                "  background-color: var(--encounter-bcc-background);\n" +
                "}\n" +
                "\n" +
                ".pk-list-bug-catching-contest > li {\n" +
                "  background-color: var(--encounter-bcc-color);\n" +
                "}\n" +
                "\n" +
                ".pk-set-radio {\n" +
                "  background-color: var(--encounter-radio-title);\n" +
                "  color: #eee;\n" +
                "}\n" +
                "\n" +
                ".pk-list-radio {\n" +
                "  background-color: var(--encounter-radio-background);\n" +
                "}\n" +
                "\n" +
                ".pk-list-radio > li {\n" +
                "  background-color: var(--encounter-radio-color);\n" +
                "  color: #eee;\n" +
                "}\n" +
                "\n" +
                ".all-trainers {\n" +
                "  height: 900px;\n" +
                "  overflow: scroll;\n" +
                "}\n" +
                "\n" +
                ".trainer-box {\n" +
                "  float: left;\n" +
                "  width: 840px;\n" +
                "  margin: 10px 10px;\n" +
                "  padding: 5px;\n" +
                "  font-size: 14px;\n" +
                "}\n" +
                "\n" +
                ".trainer {\n" +
                "  background-color: var(--trainer-background);\n" +
                "  padding: 10px 10px;\n" +
                "  border-radius: 10px;\n" +
                "  color: #333;\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                ".trainer > em {\n" +
                "  display: block;\n" +
                "  font-size: 0.9em;\n" +
                "  margin-left: 60px;\n" +
                "}\n" +
                "\n" +
                ".trainer-name {\n" +
                "  display: block;\n" +
                "}\n" +
                "\n" +
                ".trainer-name > em {\n" +
                "  font-size: 0.9em;\n" +
                "  background-color: var(--trainer-color);\n" +
                "  padding: 5px 8px;\n" +
                "  border-radius: 12px;\n" +
                "  margin-right: 2px;\n" +
                "}\n" +
                "\n" +
                ".old-trainer-pk {\n" +
                "  background-color: var(--old-trainer-background);\n" +
                "  height: 70px;\n" +
                "  width: 100%;\n" +
                "  list-style-type: none;\n" +
                "  margin: 0;\n" +
                "  padding: 10px 10px;\n" +
                "  border-radius: 10px;\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".old-trainer-pk > li {\n" +
                "  margin: 0 5px 5px 0;\n" +
                "  background-color: var(--old-trainer-color);\n" +
                "  padding: 4px 8px;\n" +
                "  border-radius: 8px;\n" +
                "  border: 2px solid var(--old-trainer-border);\n" +
                "  color: #333;\n" +
                "  height: 30%;\n" +
                "  width: 30%;\n" +
                "  float: left;\n" +
                "}\n" +
                "\n" +
                ".old-trainer-pk > li > em {\n" +
                "  font-size: 0.75em;\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".new-trainer-pk {\n" +
                "  background-color: var(--chain-element-background);\n" +
                "  height: 450px;\n" +
                "  width: 100%;\n" +
                "  list-style-type: none;\n" +
                "  margin: 0;\n" +
                "  padding: 10px 10px;\n" +
                "  border-radius: 10px;\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".new-trainer-pk > li {\n" +
                "  margin: 0 5px 5px 0;\n" +
                "  background-color: var(--chain-element-color);\n" +
                "  padding: 4px 8px;\n" +
                "  border-radius: 8px;\n" +
                "  border: 2px solid var(--chain-element-border);\n" +
                "  color: #333;\n" +
                "  height: 47%;\n" +
                "  width: 30%;\n" +
                "  float: left;\n" +
                "}\n" +
                "\n" +
                ".attributes {\n" +
                "  font-style: italic;\n" +
                "  float: right;\n" +
                "  font-size: 0.75em;\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".attributes > mark {\n" +
                "  background-color: rgba(0,0,0,0.1);\n" +
                "}\n" +
                "\n" +
                ".new-trainer-table {\n" +
                "  width: 100%;\n" +
                "  border: 0px;\n" +
                "  empty-cells: hide;\n" +
                "  color: #333;\n" +
                "  font-size: 0.9em;\n" +
                "}\n" +
                "\n" +
                ".new-trainer-table tr {\n" +
                "  padding: 2px 3px;\n" +
                "  border-right: 0px;\n" +
                "  border-left: 0px;\n" +
                "  white-space: nowrap;\n" +
                "  background-color: rgba(0,0,0,0.05);\n" +
                "}\n" +
                "\n" +
                ".new-trainer-table tr#alt {\n" +
                "  padding: 2px 3px;\n" +
                "  border-right: 0px;\n" +
                "  border-left: 0px;\n" +
                "  white-space: nowrap;\n" +
                "  background-color: rgba(0,0,0,0.1);\n" +
                "}\n" +
                "\n" +
                ".new-trainer-table td {\n" +
                "  padding: 2px 3px;\n" +
                "  border-right: 0px;\n" +
                "  border-left: 0px;\n" +
                "  text-align: center;\n" +
                "  white-space: nowrap;\n" +
                "}\n" +
                "\n" +
                ".pk-chain-element {\n" +
                "  background-color: var(--chain-element-color);\n" +
                "  margin: 5px 0;\n" +
                "  padding: 6px 14px;\n" +
                "  border-radius: 8px;\n" +
                "  border: 2px solid var(--chain-element-border);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".pk-chain-element-base {\n" +
                "  background-color: var(--chain-element-base-color);\n" +
                "  margin: 5px 0;\n" +
                "  padding: 6px 14px;\n" +
                "  border-radius: 8px;\n" +
                "  border: 2px solid var(--chain-element-base-border);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ".pk-chain-element-tail {\n" +
                "  background-color: var(--chain-element-tail-color);\n" +
                "  margin: 5px 0;\n" +
                "  padding: 6px 14px;\n" +
                "  border-radius: 8px;\n" +
                "  border: 2px solid var(--chain-element-tail-border);\n" +
                "  color: #333;\n" +
                "}\n" +
                "\n" +
                ":root {\n" +
                "  --background-color: #eeeeee;\n" +
                "  --text-contrast: #444444;\n" +
                "  --chain-element-color: #f5da8e;\n" +
                "  --chain-element-border: #ffb262;\n" +
                "  --chain-element-background: #f1e1b7;\n" +
                "  --chain-element-base-color: #FDF8EB;\n" +
                "  --chain-element-base-border: #FFDBB5;\n" +
                "  --chain-element-base-background: #F7EED7;\n" +
                "  --chain-element-tail-color: #B28810;\n" +
                "  --chain-element-tail-border: #B05A00;\n" +
                "  --chain-element-tail-background: #B18A23;\n" +
                "  --old-trainer-color: #dac281;\n" +
                "  --old-trainer-border: #e7a45b;\n" +
                "  --old-trainer-background: #e2d2ab;\n" +
                "  --trainer-color: #ffde93;\n" +
                "  --trainer-background: #ffc379;\n" +
                "  --encounter-radio-color: #bd6e73;\n" +
                "  --encounter-radio-background: #ab2836;\n" +
                "  --encounter-radio-title: #942b3b;\n" +
                "  --encounter-bcc-color: #ecd375;\n" +
                "  --encounter-bcc-background: #deb84d;\n" +
                "  --encounter-bcc-title: #a7822e;\n" +
                "  --encounter-headbutt-color: #c0d66a;\n" +
                "  --encounter-headbutt-background: #86b34b;\n" +
                "  --encounter-headbutt-title: #557d37;\n" +
                "  --encounter-shaking-color: #b0bccc;\n" +
                "  --encounter-shaking-background: #8da1b9;\n" +
                "  --encounter-shaking-title: #5f697b;\n" +
                "  --encounter-radar-color: #d8ade6;\n" +
                "  --encounter-radar-background: #af79ea;\n" +
                "  --encounter-radar-title: #7c51c3;\n" +
                "  --encounter-rocksmash-color: #e4d0a0;\n" +
                "  --encounter-rocksmash-background: #b7a070;\n" +
                "  --encounter-rocksmash-title: #96764e;\n" +
                "  --encounter-fishing-color: #296fd7;\n" +
                "  --encounter-fishing-background: #2b4e8e;\n" +
                "  --encounter-fishing-title: #3773e2;\n" +
                "  --encounter-surfing-color: #7ad7ec;\n" +
                "  --encounter-surfing-background: #48b9da;\n" +
                "  --encounter-surfing-title: #6edde8;\n" +
                "  --encounter-doubles-color: #399255;\n" +
                "  --encounter-doubles-background: #69b985;\n" +
                "  --encounter-doubles-title: #346b3b;\n" +
                "  --encounter-grass-color: #94e49e;\n" +
                "  --encounter-grass-background: #63d066;\n" +
                "  --encounter-grass-title: #319634;\n" +
                "  --tm-color: #5290ec;\n" +
                "  --moveset-border: #80b2f5;\n" +
                "  --moveset-color: #aaccdd;\n" +
                "  --type-fairy-color: #ff9ddd;\n" +
                "  --type-steel-color: #93a8c1;\n" +
                "  --type-dark-color: #5133ad;\n" +
                "  --type-dragon-color: #00c7be;\n" +
                "  --type-fighting-color: #bf533b;\n" +
                "  --type-bug-color: #a8d02d;\n" +
                "  --type-ghost-color: #6860a9;\n" +
                "  --type-psychic-color: #ff6aaf;\n" +
                "  --type-ice-color: #95d0fd;\n" +
                "  --type-flying-color: #c1c0fa;\n" +
                "  --type-poison-color: #782fff;\n" +
                "  --type-ground-color: #d2a96a;\n" +
                "  --type-rock-color: #6f6155;\n" +
                "  --type-electric-color: #ffce00;\n" +
                "  --type-water-color: #2d94ff;\n" +
                "  --type-fire-color: #ff690f;\n" +
                "  --type-grass-color: #19ad30;\n" +
                "  --type-normal-color: #7f7f7f;\n" +
                "  --cat-physical-color: #c92112;\n" +
                "  --cat-special-color: #4f5870;\n" +
                "  --cat-status-color: #8c888c;\n" +
                "  --trades-row-alt-color: #e0ffe0;\n" +
                "  --trades-row-color: #c3f9c3;\n" +
                "  --trades-title: #33ac33;\n" +
                "  --trades-border: #0f630f;\n" +
                "  --tm-row-alt-color: #F9EFC5;\n" +
                "  --tm-row-color: #FFF8DF;\n" +
                "  --tm-title: #FF9D46;\n" +
                "  --tm-border: #884615;\n" +
                "  --moves-row-alt-color: #f9dbc5;\n" +
                "  --moves-row-color: #ffecdf;\n" +
                "  --moves-title: #ff5746;\n" +
                "  --moves-border: #881a15;\n" +
                "  --pk-row-alt2-color: #8BC3F3;\n" +
                "  --pk-row-alt-color: #c5e1f9;\n" +
                "  --pk-row-color: #dff0ff;\n" +
                "  --pk-title: #1b7df7;\n" +
                "  --pk-border: #244073;\n" +
                "}\n" +
                "\n" +
                "body.dark-mode {\n" +
                "  /* dark mode variables go here */\n" +
                "  --background-color: #121212;\n" +
                "  --text-contrast: #aaaaaa;\n" +
                "  --chain-element-color: #f1c95b;\n" +
                "  --chain-element-border: #ff9c33;\n" +
                "  --chain-element-background: #ead295;\n" +
                "  --chain-element-base-color: #F8E4AE;\n" +
                "  --chain-element-base-border: #FFCE99;\n" +
                "  --chain-element-base-background: #F4E8CA;\n" +
                "  --chain-element-tail-color: #99740D;\n" +
                "  --chain-element-tail-border: #9A4F00;\n" +
                "  --chain-element-tail-background: #A07C20;\n" +
                "  --old-trainer-color: #d1b361;\n" +
                "  --old-trainer-border: #e18f37;\n" +
                "  --old-trainer-background: #d1b97a;\n" +
                "  --trainer-color: #ffd166;\n" +
                "  --trainer-background: #ffaf4d;\n" +
                "  --encounter-radio-color: #af5057;\n" +
                "  --encounter-radio-background: #91222d;\n" +
                "  --encounter-radio-title: #77222f;\n" +
                "  --encounter-bcc-color: #e7c54b;\n" +
                "  --encounter-bcc-background: #d7a928;\n" +
                "  --encounter-bcc-title: #8c6e26;\n" +
                "  --encounter-headbutt-color: #b3ce4b;\n" +
                "  --encounter-headbutt-background: #6c903c;\n" +
                "  --encounter-headbutt-title: #3c5927;\n" +
                "  --encounter-shaking-color: #92a3b9;\n" +
                "  --encounter-shaking-background: #7189a8;\n" +
                "  --encounter-shaking-title: #596273;\n" +
                "  --encounter-radar-color: #aa77bb;\n" +
                "  --encounter-radar-background: #7c40bf;\n" +
                "  --encounter-radar-title: #5a3498;\n" +
                "  --encounter-rocksmash-color: #d8ba74;\n" +
                "  --encounter-rocksmash-background: #aa8f55;\n" +
                "  --encounter-rocksmash-title: #876945;\n" +
                "  --encounter-fishing-color: #2058ac;\n" +
                "  --encounter-fishing-background: #234076;\n" +
                "  --encounter-fishing-title: #1d59c9;\n" +
                "  --encounter-surfing-color: #4dc9e6;\n" +
                "  --encounter-surfing-background: #2bacd4;\n" +
                "  --encounter-surfing-title: #4fd7e3;\n" +
                "  --encounter-doubles-color: #2b6e40;\n" +
                "  --encounter-doubles-background: #52ad72;\n" +
                "  --encounter-doubles-title: #29562f;\n" +
                "  --encounter-grass-color: #66b372;\n" +
                "  --encounter-grass-background: #3c913e;\n" +
                "  --encounter-grass-title: #184e1a;\n" +
                "  --tm-color: #307ae8;\n" +
                "  --moveset-border: #5a9cf2;\n" +
                "  --moveset-color: #92bdd3;\n" +
                "  --type-fairy-color: #ff9ddd;\n" +
                "  --type-steel-color: #7d96b5;\n" +
                "  --type-dark-color: #4a2f9d;\n" +
                "  --type-dragon-color: #009991;\n" +
                "  --type-fighting-color: #9c4430;\n" +
                "  --type-bug-color: #87a725;\n" +
                "  --type-ghost-color: #585095;\n" +
                "  --type-psychic-color: #e963a1;\n" +
                "  --type-ice-color: #69bcfc;\n" +
                "  --type-flying-color: #a2a1f7;\n" +
                "  --type-poison-color: #5900ff;\n" +
                "  --type-ground-color: #ca994e;\n" +
                "  --type-rock-color: #65584e;\n" +
                "  --type-electric-color: #e6b800;\n" +
                "  --type-water-color: #207cdf;\n" +
                "  --type-fire-color: #e65400;\n" +
                "  --type-grass-color: #1b982e;\n" +
                "  --type-normal-color: #737373;\n" +
                "  --trades-row-alt-color: #d3ffd3;\n" +
                "  --trades-row-color: #a2f6a2;\n" +
                "  --trades-title: #298929;\n" +
                "  --trades-border: #0d590d;\n" +
                "  --tm-row-alt-color: #F5E5A3;\n" +
                "  --tm-row-color: #FFEEB3;\n" +
                "  --tm-title: #E28736;\n" +
                "  --tm-border: #6E3811;\n" +
                "  --moves-row-alt-color: #f5c5a3;\n" +
                "  --moves-row-color: #ffd1b3;\n" +
                "  --moves-title: #e24736;\n" +
                "  --moves-border: #6e1611;\n" +
                "  --pk-row-alt2-color: #84BFF2;\n" +
                "  --pk-row-alt-color: #a3cff5;\n" +
                "  --pk-row-color: #b3dbff;\n" +
                "  --pk-title: #1375ec;\n" +
                "  --pk-border: #1f3661;\n" +
                "}";
    }
}