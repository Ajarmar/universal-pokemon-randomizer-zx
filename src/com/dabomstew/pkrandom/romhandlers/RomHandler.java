package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  RomHandler.java - defines the functionality that each randomization   --*/
/*--                    handler must implement.                             --*/
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

import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.Settings;
import com.dabomstew.pkrandom.pokemon.*;

public interface RomHandler {

    abstract class Factory {
        public RomHandler create(Random random) {
            return create(random, null);
        }

        public abstract RomHandler create(Random random, PrintStream log);

        public abstract boolean isLoadable(String filename);
    }

    // =======================
    // Basic load/save methods
    // =======================

    boolean loadRom(String filename);

    boolean saveRomFile(String filename, long seed);

    boolean saveRomDirectory(String filename);

    String loadedFilename();

    // =============================================================
    // Methods relating to game updates for the 3DS and Switch games
    // =============================================================

    boolean hasGameUpdateLoaded();

    boolean loadGameUpdate(String filename);

    void removeGameUpdate();

    String getGameUpdateVersion();

    // ===========
    // Log methods
    // ===========

    void setLog(PrintStream logStream);

    void printRomDiagnostics(PrintStream logStream);

    boolean isRomValid();

    // ======================================================
    // Methods for retrieving a list of Pokemon objects.
    // Note that for many of these lists, index 0 is null.
    // Instead, you use index on the species' National Dex ID
    // ======================================================

    List<Pokemon> getPokemon();

    List<Pokemon> getPokemonInclFormes();

    List<Pokemon> getAltFormes();

    List<MegaEvolution> getMegaEvolutions();

    Pokemon getAltFormeOfPokemon(Pokemon pk, int forme);

    List<Pokemon> getIrregularFormes();

    // ==================================
    // Methods to set up Gen Restrictions
    // ==================================

    void setPokemonPool(Settings settings);

    void removeEvosForPokemonPool();

    // ===============
    // Starter Pokemon
    // ===============

    List<Pokemon> getStarters();

    boolean setStarters(List<Pokemon> newStarters);

    boolean hasStarterAltFormes();

    int starterCount();

    void customStarters(Settings settings);

    void randomizeStarters(Settings settings);

    void randomizeBasicTwoEvosStarters(Settings settings);

    List<Pokemon> getPickedStarters();

    List<Integer> getStarterHeldItems();

    void setStarterHeldItems(List<Integer> items);

    void randomizeStarterHeldItems(Settings settings);

    // =======================
    // Pokemon Base Statistics
    // =======================

    // Run the stats shuffler on each Pokemon.
    void shufflePokemonStats(Settings settings);

    // Randomize stats following evolutions for proportions or not (see
    // tooltips)
    void randomizePokemonStats(Settings settings);

    // Update base stats to specified generation
    void updatePokemonStats(Settings settings);

    Map<Integer,StatChange> getUpdatedPokemonStats(int generation);

    void standardizeEXPCurves(Settings settings);

    // ====================================
    // Methods for selecting random Pokemon
    // ====================================

    // Give a random Pokemon who's in this game
    Pokemon randomPokemon();

    Pokemon randomPokemonInclFormes();

    // Give a random non-legendary Pokemon who's in this game
    // Business rules for who's legendary are in Pokemon class
    Pokemon randomNonLegendaryPokemon();

    // Give a random legendary Pokemon who's in this game
    // Business rules for who's legendary are in Pokemon class
    Pokemon randomLegendaryPokemon();

    // Give a random Pokemon who has 2 evolution stages
    // Should make a good starter Pokemon
    Pokemon random2EvosPokemon(boolean allowAltFormes);

    // =============
    // Pokemon Types
    // =============

    // return a random type valid in this game.
    Type randomType();

    boolean typeInGame(Type type);

    // randomize Pokemon types, with a switch on whether evolutions
    // should follow the same types or not.
    // some evolutions dont anyway, e.g. Eeveelutions, Hitmons
    void randomizePokemonTypes(Settings settings);

    // =================
    // Pokemon Abilities
    // =================

    int abilitiesPerPokemon();

    int highestAbilityIndex();

    String abilityName(int number);

    void randomizeAbilities(Settings settings);

    Map<Integer,List<Integer>> getAbilityVariations();

    List<Integer> getUselessAbilities();

    int getAbilityForTrainerPokemon(TrainerPokemon tp);

    boolean hasMegaEvolutions();

    // ============
    // Wild Pokemon
    // ============

    List<EncounterSet> getEncounters(boolean useTimeOfDay);

    void setEncounters(boolean useTimeOfDay, List<EncounterSet> encounters);

    void randomEncounters(Settings settings);

    void area1to1Encounters(Settings settings);

    void game1to1Encounters(Settings settings);

    void onlyChangeWildLevels(Settings settings);

    boolean hasTimeBasedEncounters();

    boolean hasWildAltFormes();

    List<Pokemon> bannedForWildEncounters();

    void randomizeWildHeldItems(Settings settings);

    void changeCatchRates(Settings settings);

    void minimumCatchRate(int rateNonLegendary, int rateLegendary);

    // ===============
    // Trainer Pokemon
    // ===============

    List<Trainer> getTrainers();

    List<Integer> getMainPlaythroughTrainers();

    List<Integer> getEliteFourTrainers(boolean isChallengeMode);

    void setTrainers(List<Trainer> trainerData, boolean doubleBattleMode);

    void randomizeTrainerPokes(Settings settings);

    void randomizeTrainerHeldItems(Settings settings);

    List<Integer> getSensibleHeldItemsFor(TrainerPokemon tp, boolean consumableOnly, List<Move> moves, int[] pokeMoves);

    List<Integer> getAllConsumableHeldItems();

    List<Integer> getAllHeldItems();

    void rivalCarriesStarter();

    boolean hasRivalFinalBattle();

    void forceFullyEvolvedTrainerPokes(Settings settings);

    void onlyChangeTrainerLevels(Settings settings);

    void addTrainerPokemon(Settings settings);

    void doubleBattleMode();

    List<Move> getMoveSelectionPoolAtLevel(TrainerPokemon tp, boolean cyclicEvolutions);

    void pickTrainerMovesets(Settings settings);

    // =========
    // Move Data
    // =========

    void randomizeMovePowers();

    void randomizeMovePPs();

    void randomizeMoveAccuracies();

    void randomizeMoveTypes();

    boolean hasPhysicalSpecialSplit();

    void randomizeMoveCategory();

    void updateMoves(Settings settings);

    // stuff for printing move changes
    void initMoveUpdates();

    Map<Integer, boolean[]> getMoveUpdates();

    // return all the moves valid in this game.
    List<Move> getMoves();

    // ================
    // Pokemon Movesets
    // ================

    Map<Integer, List<MoveLearnt>> getMovesLearnt();

    void setMovesLearnt(Map<Integer, List<MoveLearnt>> movesets);

    List<Integer> getMovesBannedFromLevelup();

    Map<Integer, List<Integer>> getEggMoves();

    void setEggMoves(Map<Integer, List<Integer>> eggMoves);

    void randomizeMovesLearnt(Settings settings);

    void randomizeEggMoves(Settings settings);

    void orderDamagingMovesByDamage();

    void metronomeOnlyMode();

    boolean supportsFourStartingMoves();

    // ==============
    // Static Pokemon
    // ==============

    List<StaticEncounter> getStaticPokemon();

    boolean setStaticPokemon(List<StaticEncounter> staticPokemon);

    void randomizeStaticPokemon(Settings settings);

    boolean canChangeStaticPokemon();

    boolean hasStaticAltFormes();

    List<Pokemon> bannedForStaticPokemon();

    boolean forceSwapStaticMegaEvos();

    void onlyChangeStaticLevels(Settings settings);

    boolean hasMainGameLegendaries();

    List<Integer> getMainGameLegendaries();

    List<Integer> getSpecialMusicStatics();

    void applyCorrectStaticMusic(Map<Integer,Integer> specialMusicStaticChanges);

    boolean hasStaticMusicFix();

    // =============
    // Totem Pokemon
    // =============

    List<TotemPokemon> getTotemPokemon();

    void setTotemPokemon(List<TotemPokemon> totemPokemon);

    void randomizeTotemPokemon(Settings settings);

    // =========
    // TMs & HMs
    // =========

    List<Integer> getTMMoves();

    List<Integer> getHMMoves();

    void setTMMoves(List<Integer> moveIndexes);

    void randomizeTMMoves(Settings settings);

    int getTMCount();

    int getHMCount();

    /**
     * Get TM/HM compatibility data from this rom. The result should contain a
     * boolean array for each Pokemon indexed as such:
     * 
     * 0: blank (false) / 1 - (getTMCount()) : TM compatibility /
     * (getTMCount()+1) - (getTMCount()+getHMCount()) - HM compatibility
     * 
     * @return Map of TM/HM compatibility
     */

    Map<Pokemon, boolean[]> getTMHMCompatibility();

    void setTMHMCompatibility(Map<Pokemon, boolean[]> compatData);

    void randomizeTMHMCompatibility(Settings settings);

    void fullTMHMCompatibility();

    void ensureTMCompatSanity();

    void ensureTMEvolutionSanity();

    void fullHMCompatibility();

    // ===========
    // Move Tutors
    // ===========

    void copyTMCompatibilityToCosmeticFormes();

    boolean hasMoveTutors();

    List<Integer> getMoveTutorMoves();

    void setMoveTutorMoves(List<Integer> moves);

    void randomizeMoveTutorMoves(Settings settings);

    Map<Pokemon, boolean[]> getMoveTutorCompatibility();

    void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData);

    void randomizeMoveTutorCompatibility(Settings settings);

    void fullMoveTutorCompatibility();

    void ensureMoveTutorCompatSanity();

    void ensureMoveTutorEvolutionSanity();

    // =============
    // Trainer Names
    // =============

    void copyMoveTutorCompatibilityToCosmeticFormes();

    boolean canChangeTrainerText();

    List<String> getTrainerNames();

    void setTrainerNames(List<String> trainerNames);

    enum TrainerNameMode {
        SAME_LENGTH, MAX_LENGTH, MAX_LENGTH_WITH_CLASS
    }

    TrainerNameMode trainerNameMode();

    // Returns this with or without the class
    int maxTrainerNameLength();

    // Only relevant for gen2, which has fluid trainer name length but
    // only a certain amount of space in the ROM bank.
    int maxSumOfTrainerNameLengths();

    // Only needed if above mode is "MAX LENGTH WITH CLASS"
    List<Integer> getTCNameLengthsByTrainer();

    void randomizeTrainerNames(Settings settings);

    // ===============
    // Trainer Classes
    // ===============

    List<String> getTrainerClassNames();

    void setTrainerClassNames(List<String> trainerClassNames);

    boolean fixedTrainerClassNamesLength();

    int maxTrainerClassNameLength();

    void randomizeTrainerClassNames(Settings settings);

    List<Integer> getDoublesTrainerClasses();

    // =====
    // Items
    // =====

    ItemList getAllowedItems();

    ItemList getNonBadItems();

    List<Integer> getEvolutionItems();

    List<Integer> getXItems();

    List<Integer> getUniqueNoSellItems();

    List<Integer> getRegularShopItems();

    List<Integer> getOPShopItems();

    String[] getItemNames();

    // ===========
    // Field Items
    // ===========

    // TMs on the field

    List<Integer> getRequiredFieldTMs();

    List<Integer> getCurrentFieldTMs();

    void setFieldTMs(List<Integer> fieldTMs);

    // Everything else

    List<Integer> getRegularFieldItems();

    void setRegularFieldItems(List<Integer> items);

    // Randomizer methods

    void shuffleFieldItems();

    void randomizeFieldItems(Settings settings);

    // ============
    // Special Shops
    // =============

    boolean hasShopRandomization();

    void shuffleShopItems();

    void randomizeShopItems(Settings settings);

    Map<Integer, Shop> getShopItems();

    void setShopItems(Map<Integer, Shop> shopItems);

    void setShopPrices();

    // ============
    // Pickup Items
    // ============

    List<PickupItem> getPickupItems();

    void setPickupItems(List<PickupItem> pickupItems);

    void randomizePickupItems(Settings settings);

    // ==============
    // In-Game Trades
    // ==============

    List<IngameTrade> getIngameTrades();

    void setIngameTrades(List<IngameTrade> trades);

    void randomizeIngameTrades(Settings settings);

    boolean hasDVs();

    int maxTradeNicknameLength();

    int maxTradeOTNameLength();

    // ==================
    // Pokemon Evolutions
    // ==================

    void removeImpossibleEvolutions(Settings settings);

    void condenseLevelEvolutions(int maxLevel, int maxIntermediateLevel);

    void makeEvolutionsEasier(Settings settings);

    void removeTimeBasedEvolutions();

    Set<EvolutionUpdate> getImpossibleEvoUpdates();

    Set<EvolutionUpdate> getEasierEvoUpdates();

    Set<EvolutionUpdate> getTimeBasedEvoUpdates();

    void randomizeEvolutions(Settings settings);

    void randomizeEvolutionsEveryLevel(Settings settings);

    // In the earlier games, alt formes use the same evolutions as the base forme.
    // In later games, this was changed so that alt formes can have unique evolutions
    // compared to the base forme.
    boolean altFormesCanHaveDifferentEvolutions();

    // ==================================
    // (Mostly) unchanging lists of moves
    // ==================================

    List<Integer> getGameBreakingMoves();

    List<Integer> getIllegalMoves();

    // includes game or gen-specific moves like Secret Power
    // but NOT healing moves (Softboiled, Milk Drink)
    List<Integer> getFieldMoves();

    // any HMs required to obtain 4 badges
    // (excluding Gameshark codes or early drink in RBY)
    List<Integer> getEarlyRequiredHMMoves();


    // ====
    // Misc
    // ====

    boolean isYellow();

    String getROMName();

    String getROMCode();

    String getSupportLevel();

    String getDefaultExtension();

    int internalStringLength(String string);

    void randomizeIntroPokemon();

    BufferedImage getMascotImage();

    int generationOfPokemon();

    void writeCheckValueToROM(int value);

    // ===========
    // code tweaks
    // ===========

    int miscTweaksAvailable();

    void applyMiscTweaks(Settings settings);

    void applyMiscTweak(MiscTweak tweak);

    boolean isEffectivenessUpdated();

    void renderPlacementHistory();

    // ==========================
    // Misc forme-related methods
    // ==========================

    boolean hasFunctionalFormes();

    List<Pokemon> getAbilityDependentFormes();

    List<Pokemon> getBannedFormesForPlayerPokemon();

    List<Pokemon> getBannedFormesForTrainerPokemon();
}