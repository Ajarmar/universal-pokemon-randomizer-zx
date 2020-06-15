package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  RomHandler.java - defines the functionality that each randomization   --*/
/*--                    handler must implement.                             --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew                   --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2012.                                 --*/
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

import com.dabomstew.pkrandom.CustomNamesSet;
import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.Settings;
import com.dabomstew.pkrandom.pokemon.EncounterSet;
import com.dabomstew.pkrandom.pokemon.GenRestrictions;
import com.dabomstew.pkrandom.pokemon.IngameTrade;
import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.pokemon.MoveLearnt;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.Type;

public interface RomHandler {

    abstract class Factory {
        public RomHandler create(Random random) {
            return create(random, null);
        }

        public abstract RomHandler create(Random random, PrintStream log);

        public abstract boolean isLoadable(String filename);
    }

    // Basic load/save to filenames

    boolean loadRom(String filename);

    boolean saveRomFile(String filename);

    boolean saveRomDirectory(String filename);

    String loadedFilename();

    // Log stuff

    void setLog(PrintStream logStream);

    // Get a List of Pokemon objects in this game.
    // 0 = null 1-whatever = the Pokemon.
    List<Pokemon> getPokemon();

    List<Pokemon> getPokemonInclFormes();

    List<Pokemon> getAltFormes();

    // Setup Gen Restrictions.
    void setPokemonPool(GenRestrictions restrictions);

    void removeEvosForPokemonPool();

    // Randomizer: Starters
    // Get starters, they should be ordered with Pokemon
    // following the one it is SE against.
    // E.g. Grass, Fire, Water or Fire, Water, Grass etc.
    List<Pokemon> getStarters();

    // Change the starter data in the ROM.
    // Optionally also change the starter used by the rival in
    // the level 5 battle, if there is one.
    boolean setStarters(List<Pokemon> newStarters);

    // Tells whether this ROM has the ability to have starters changed.
    // Was for before CUE's compressors were found and arm9 was untouchable.
    boolean canChangeStarters();

    // Randomizer: Pokemon stats

    // Run the stats shuffler on each Pokemon.
    void shufflePokemonStats(boolean evolutionSanity);

    // Randomise stats following evolutions for proportions or not (see
    // tooltips)
    void randomizePokemonStats(boolean evolutionSanity);

    // Update base stats to gen6
    void updatePokemonStats();

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
    Pokemon random2EvosPokemon();

    // Randomizer: types

    // return a random type valid in this game.
    // straightforward except for gen1 where dark&steel are excluded.
    Type randomType();

    boolean typeInGame(Type type);

    // randomise Pokemon types, with a switch on whether evolutions
    // should follow the same types or not.
    // some evolutions dont anyway, e.g. Eeveelutions, Hitmons
    void randomizePokemonTypes(boolean evolutionSanity);

    // Randomizer: pokemon abilities
    int abilitiesPerPokemon();

    int highestAbilityIndex();

    String abilityName(int number);

    void randomizeAbilities(boolean evolutionSanity, boolean allowWonderGuard, boolean banTrappingAbilities,
                            boolean banNegativeAbilities, boolean banBadAbilities);

    // Randomizer: wild pokemon
    List<EncounterSet> getEncounters(boolean useTimeOfDay);

    void setEncounters(boolean useTimeOfDay, List<EncounterSet> encounters);

    void randomEncounters(boolean useTimeOfDay, boolean catchEmAll, boolean typeThemed, boolean usePowerLevels,
                          boolean noLegendaries, boolean balanceShakingGrass, int levelModifier);

    void area1to1Encounters(boolean useTimeOfDay, boolean catchEmAll, boolean typeThemed,
                            boolean usePowerLevels, boolean noLegendaries, int levelModifier);

    void game1to1Encounters(boolean useTimeOfDay, boolean usePowerLevels, boolean noLegendaries, int levelModifier);

    void onlyChangeWildLevels(int levelModifier);

    boolean hasTimeBasedEncounters();

    List<Pokemon> bannedForWildEncounters();

    // Randomizer: trainer pokemon
    List<Trainer> getTrainers();

    List<Integer> getMainPlaythroughTrainers();
    
    List<Integer> getEvolutionItems();

    void setTrainers(List<Trainer> trainerData);

    void randomizeTrainerPokes(boolean usePowerLevels, boolean noLegendaries, boolean noEarlyWonderGuard,
                               int levelModifier, boolean distributionSetting, boolean mainPlaythroughSetting,
                               boolean includeFormes);

    void typeThemeTrainerPokes(boolean usePowerLevels, boolean weightByFrequency, boolean noLegendaries,
                               boolean noEarlyWonderGuard, int levelModifier, boolean includeFormes);

    void rivalCarriesStarter();

    void forceFullyEvolvedTrainerPokes(int minLevel);

    void onlyChangeTrainerLevels(int levelModifier);

    // Randomizer: moves

    void randomizeMovePowers();

    void randomizeMovePPs();

    void randomizeMoveAccuracies();

    void randomizeMoveTypes();

    boolean hasPhysicalSpecialSplit();

    void randomizeMoveCategory();

    // Update all moves to gen5 definitions as much as possible
    // e.g. change typing, power, accuracy, but don't try to
    // stuff around with effects.
    void updateMovesToGen5();

    // same for gen6
    void updateMovesToGen6();

    // stuff for printing move changes
    void initMoveUpdates();

    void printMoveUpdates();

    // return all the moves valid in this game.
    List<Move> getMoves();

    Map<Integer, List<MoveLearnt>> getMovesLearnt();

    void setMovesLearnt(Map<Integer, List<MoveLearnt>> movesets);

    List<Integer> getMovesBannedFromLevelup();

    void randomizeMovesLearnt(boolean typeThemed, boolean noBroken, boolean forceStartingMoves,
                              int forceStartingMoveCount, double goodDamagingProbability);

    void orderDamagingMovesByDamage();

    void metronomeOnlyMode();

    boolean supportsFourStartingMoves();

    // Randomizer: static pokemon (except starters)

    List<Pokemon> getStaticPokemon();

    boolean setStaticPokemon(List<Pokemon> staticPokemon);

    void randomizeStaticPokemon(boolean swapLegendaries, boolean similarStrength, boolean limitMusketeers, boolean limit600);

    boolean canChangeStaticPokemon();

    List<Pokemon> bannedForStaticPokemon();

    // Randomizer: TMs/HMs

    List<Integer> getTMMoves();

    List<Integer> getHMMoves();

    void setTMMoves(List<Integer> moveIndexes);

    void randomizeTMMoves(boolean noBroken, boolean preserveField, double goodDamagingProbability);

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

    void randomizeTMHMCompatibility(boolean preferSameType);

    void fullTMHMCompatibility();

    // tm/moveset sanity

    void ensureTMCompatSanity();

    // new 170: full HM (but not TM) compat override

    void fullHMCompatibility();

    // Randomizer: move tutors

    boolean hasMoveTutors();

    List<Integer> getMoveTutorMoves();

    void setMoveTutorMoves(List<Integer> moves);

    void randomizeMoveTutorMoves(boolean noBroken, boolean preserveField, double goodDamagingProbability);

    Map<Pokemon, boolean[]> getMoveTutorCompatibility();

    void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData);

    void randomizeMoveTutorCompatibility(boolean preferSameType);

    void fullMoveTutorCompatibility();

    // mt/moveset sanity

    void ensureMoveTutorCompatSanity();

    // Randomizer: trainer names

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

    void randomizeTrainerNames(CustomNamesSet customNames);

    // Randomizer: trainer class names

    List<String> getTrainerClassNames();

    void setTrainerClassNames(List<String> trainerClassNames);

    boolean fixedTrainerClassNamesLength();

    int maxTrainerClassNameLength();

    void randomizeTrainerClassNames(CustomNamesSet customNames);

    List<Integer> getDoublesTrainerClasses();

    // Items

    ItemList getAllowedItems();

    ItemList getNonBadItems();

    List<Integer> getRegularShopItems();

    List<Integer> getOPShopItems();

    void randomizeWildHeldItems(boolean banBadItems);

    String[] getItemNames();
    
    String[] getShopNames();

    List<Integer> getStarterHeldItems();

    void setStarterHeldItems(List<Integer> items);

    void randomizeStarterHeldItems(boolean banBadItems);

    // Field Items

    // TMs on the field

    List<Integer> getRequiredFieldTMs();

    List<Integer> getCurrentFieldTMs();

    void setFieldTMs(List<Integer> fieldTMs);

    // Everything else

    List<Integer> getRegularFieldItems();

    void setRegularFieldItems(List<Integer> items);

    // Randomizer methods

    void shuffleFieldItems();

    void randomizeFieldItems(boolean banBadItems, boolean distributeItemsControl);

    // Trades

    List<IngameTrade> getIngameTrades();

    void setIngameTrades(List<IngameTrade> trades);

    void randomizeIngameTrades(boolean randomizeRequest, boolean randomNickname, boolean randomOT,
                               boolean randomStats, boolean randomItem, CustomNamesSet customNames);

    boolean hasDVs();

    int maxTradeNicknameLength();

    int maxTradeOTNameLength();

    // Evos

    void removeTradeEvolutions(boolean changeMoveEvos);

    void condenseLevelEvolutions(int maxLevel, int maxIntermediateLevel);

    void removePartyEvolutions();

    void randomizeEvolutions(boolean similarStrength, boolean sameType, boolean limitToThreeStages,
                             boolean forceChange);

    boolean hasShopRandomization();

    void shuffleShopItems();

    void randomizeShopItems(boolean banBadItems, boolean banRegularShopItems, boolean banOPShopItems, boolean balancePrices, boolean placeEvolutionItems);

    Map<Integer, List<Integer>> getShopItems();

    void setShopItems(Map<Integer, List<Integer>> shopItems);

    void setShopPrices();

    List<Integer> getMainGameShops();

    // stats stuff
    void minimumCatchRate(int rateNonLegendary, int rateLegendary);

    void standardizeEXPCurves(Settings.ExpCurveMod mod);

    // (Mostly) unchanging lists of moves

    List<Integer> getGameBreakingMoves();

    // includes game or gen-specific moves like Secret Power
    // but NOT healing moves (Softboiled, Milk Drink)
    List<Integer> getFieldMoves();

    // any HMs required to obtain 4 badges
    // (excluding Gameshark codes or early drink in RBY)
    List<Integer> getEarlyRequiredHMMoves();

    // Misc

    boolean isYellow();

    String getROMName();

    String getROMCode();

    String getSupportLevel();

    String getDefaultExtension();

    int internalStringLength(String string);

    void applySignature();

    BufferedImage getMascotImage();

    boolean isROMHack();

    int generationOfPokemon();

    void writeCheckValueToROM(int value);

    // code tweaks

    int miscTweaksAvailable();

    void applyMiscTweak(MiscTweak tweak);

    void renderPlacementHistory();

}