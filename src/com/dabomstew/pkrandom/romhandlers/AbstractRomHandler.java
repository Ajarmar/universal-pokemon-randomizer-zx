package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  AbstractRomHandler.java - a base class for all rom handlers which     --*/
/*--                            implements the majority of the actual       --*/
/*--                            randomizer logic by building on the base    --*/
/*--                            getters & setters provided by each concrete --*/
/*--                            handler.                                    --*/
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

import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import com.dabomstew.pkrandom.*;
import com.dabomstew.pkrandom.constants.GlobalConstants;
import com.dabomstew.pkrandom.exceptions.RandomizationException;
import com.dabomstew.pkrandom.pokemon.*;

public abstract class AbstractRomHandler implements RomHandler {

    private boolean restrictionsSet;
    protected List<Pokemon> mainPokemonList;
    protected List<Pokemon> mainPokemonListInclFormes;
    private List<Pokemon> altFormesList;
    private List<MegaEvolution> megaEvolutionsList;
    private List<Pokemon> noLegendaryList, onlyLegendaryList, ultraBeastList;
    private List<Pokemon> noLegendaryListInclFormes, onlyLegendaryListInclFormes;
    private List<Pokemon> noLegendaryAltsList, onlyLegendaryAltsList;
    protected final Random random;
    private final Random cosmeticRandom;
    protected PrintStream logStream;
    private List<Pokemon> alreadyPicked = new ArrayList<>();
    private List<Pokemon> giratinaPicks;
    private Map<Pokemon, Integer> placementHistory = new HashMap<>();
    private Map<Integer, Integer> itemPlacementHistory = new HashMap<>();
    boolean ptGiratina = false;
    boolean isORAS = false;
    boolean isSM = false;
    int perfectAccuracy = 100;

    /* Constructor */

    public AbstractRomHandler(Random random, PrintStream logStream) {
        this.random = random;
        this.cosmeticRandom = RandomSource.cosmeticInstance();
        this.logStream = logStream;
    }

    /*
     * Public Methods, implemented here for all gens. Unlikely to be overridden.
     */

    public void setLog(PrintStream logStream) {
        this.logStream = logStream;
    }

    public void setPokemonPool(GenRestrictions restrictions) {
        restrictionsSet = true;
        mainPokemonList = this.allPokemonWithoutNull();
        mainPokemonListInclFormes = this.allPokemonInclFormesWithoutNull();
        altFormesList = this.getAltFormes();
        megaEvolutionsList = this.getMegaEvolutions();
        if (restrictions != null) {
            mainPokemonList = new ArrayList<>();
            mainPokemonListInclFormes = new ArrayList<>();
            megaEvolutionsList = new ArrayList<>();
            List<Pokemon> allPokemon = this.getPokemon();

            if (restrictions.allow_gen1) {
                addPokesFromRange(mainPokemonList, allPokemon, 1, 151);
                if (restrictions.assoc_g1_g2 && allPokemon.size() > 251) {
                    addEvosFromRange(mainPokemonList, 1, 151, 152, 251);
                }
                if (restrictions.assoc_g1_g4 && allPokemon.size() > 493) {
                    addEvosFromRange(mainPokemonList, 1, 151, 387, 493);
                }
                if (restrictions.assoc_g1_g6 && allPokemon.size() > 721) {
                    addEvosFromRange(mainPokemonList, 1, 151, 650, 721);
                }
            }

            if (restrictions.allow_gen2 && allPokemon.size() > 251) {
                addPokesFromRange(mainPokemonList, allPokemon, 152, 251);
                if (restrictions.assoc_g2_g1) {
                    addEvosFromRange(mainPokemonList, 152, 251, 1, 151);
                }
                if (restrictions.assoc_g2_g3 && allPokemon.size() > 386) {
                    addEvosFromRange(mainPokemonList, 152, 251, 252, 386);
                }
                if (restrictions.assoc_g2_g4 && allPokemon.size() > 493) {
                    addEvosFromRange(mainPokemonList, 152, 251, 387, 493);
                }
            }

            if (restrictions.allow_gen3 && allPokemon.size() > 386) {
                addPokesFromRange(mainPokemonList, allPokemon, 252, 386);
                if (restrictions.assoc_g3_g2) {
                    addEvosFromRange(mainPokemonList, 252, 386, 152, 251);
                }
                if (restrictions.assoc_g3_g4 && allPokemon.size() > 493) {
                    addEvosFromRange(mainPokemonList, 252, 386, 387, 493);
                }
            }

            if (restrictions.allow_gen4 && allPokemon.size() > 493) {
                addPokesFromRange(mainPokemonList, allPokemon, 387, 493);
                if (restrictions.assoc_g4_g1) {
                    addEvosFromRange(mainPokemonList, 387, 493, 1, 151);
                }
                if (restrictions.assoc_g4_g2) {
                    addEvosFromRange(mainPokemonList, 387, 493, 152, 251);
                }
                if (restrictions.assoc_g4_g3) {
                    addEvosFromRange(mainPokemonList, 387, 493, 252, 386);
                }
            }

            if (restrictions.allow_gen5 && allPokemon.size() > 649) {
                addPokesFromRange(mainPokemonList, allPokemon, 494, 649);
            }

            if (restrictions.allow_gen6 && allPokemon.size() > 721) {
                addPokesFromRange(mainPokemonList, allPokemon, 650, 721);
                if (restrictions.assoc_g6_g1) {
                    addEvosFromRange(mainPokemonList, 650, 721, 1, 151);
                }
            }

            int maxGen7SpeciesID = isSM ? 802 : 807;
            if (restrictions.allow_gen7 && allPokemon.size() > maxGen7SpeciesID) {
                addPokesFromRange(mainPokemonList, allPokemon, 722, maxGen7SpeciesID);
            }

            // Now that mainPokemonList has all the selected Pokemon, update mainPokemonListInclFormes too
            addAllPokesInclFormes(mainPokemonList, mainPokemonListInclFormes);

            // Populate megaEvolutionsList with all of the mega evolutions that exist in the pool
            List<MegaEvolution> allMegaEvolutions = this.getMegaEvolutions();
            for (MegaEvolution megaEvo : allMegaEvolutions) {
                if (mainPokemonListInclFormes.contains(megaEvo.to)) {
                    megaEvolutionsList.add(megaEvo);
                }
            }
        }

        noLegendaryList = new ArrayList<>();
        noLegendaryListInclFormes = new ArrayList<>();
        onlyLegendaryList = new ArrayList<>();
        onlyLegendaryListInclFormes = new ArrayList<>();
        noLegendaryAltsList = new ArrayList<>();
        onlyLegendaryAltsList = new ArrayList<>();
        giratinaPicks = new ArrayList<>();
        ultraBeastList = new ArrayList<>();

        for (Pokemon p : mainPokemonList) {
            if (p.isLegendary()) {
                onlyLegendaryList.add(p);
            } else if (p.isUltraBeast()) {
                ultraBeastList.add(p);
            } else {
                noLegendaryList.add(p);
            }
            for (int specialIntro : GlobalConstants.ptSpecialIntros) {
                if (p.number == specialIntro) {
                    giratinaPicks.add(p);
                    break;
                }
            }
        }
        for (Pokemon p : mainPokemonListInclFormes) {
            if (p.isLegendary()) {
                onlyLegendaryListInclFormes.add(p);
            } else if (!ultraBeastList.contains(p)) {
                noLegendaryListInclFormes.add(p);
            }
        }
        for (Pokemon f : altFormesList) {
            if (f.isLegendary()) {
                onlyLegendaryAltsList.add(f);
            } else {
                noLegendaryAltsList.add(f);
            }
        }
    }

    private void addPokesFromRange(List<Pokemon> pokemonPool, List<Pokemon> allPokemon, int range_min, int range_max) {
        for (int i = range_min; i <= range_max; i++) {
            if (!pokemonPool.contains(allPokemon.get(i))) {
                pokemonPool.add(allPokemon.get(i));
            }
        }
    }

    private void addEvosFromRange(List<Pokemon> pokemonPool, int first_min, int first_max, int second_min,
            int second_max) {
        Set<Pokemon> newPokemon = new TreeSet<>();
        for (Pokemon pk : pokemonPool) {
            if (pk.number >= first_min && pk.number <= first_max) {
                for (Evolution ev : pk.evolutionsFrom) {
                    if (ev.to.number >= second_min && ev.to.number <= second_max) {
                        if (!pokemonPool.contains(ev.to) && !newPokemon.contains(ev.to)) {
                            newPokemon.add(ev.to);
                        }
                    }
                }

                for (Evolution ev : pk.evolutionsTo) {
                    if (ev.from.number >= second_min && ev.from.number <= second_max) {
                        if (!pokemonPool.contains(ev.from) && !newPokemon.contains(ev.from)) {
                            newPokemon.add(ev.from);
                        }
                    }
                }
            }
        }

        pokemonPool.addAll(newPokemon);
    }

    private void addAllPokesInclFormes(List<Pokemon> pokemonPool, List<Pokemon> pokemonPoolInclFormes) {
        List<Pokemon> altFormes = this.getAltFormes();
        for (int i = 0; i < pokemonPool.size(); i++) {
            Pokemon currentPokemon = pokemonPool.get(i);
            if (!pokemonPoolInclFormes.contains(currentPokemon)) {
                pokemonPoolInclFormes.add(currentPokemon);
            }
            for (int j = 0; j < altFormes.size(); j++) {
                Pokemon potentialAltForme = altFormes.get(j);
                if (potentialAltForme.baseForme != null && potentialAltForme.baseForme.number == currentPokemon.number) {
                    pokemonPoolInclFormes.add(potentialAltForme);
                }
            }
        }
    }

    @Override
    public void shufflePokemonStats(boolean evolutionSanity, boolean megaEvolutionSanity) {
        if (evolutionSanity) {
            copyUpEvolutionsHelper(pk -> pk.shuffleStats(AbstractRomHandler.this.random),
                    (evFrom, evTo, toMonIsFinalEvo) -> evTo.copyShuffledStatsUpEvolution(evFrom)
            );
        } else {
            List<Pokemon> allPokes = this.getPokemonInclFormes();
            for (Pokemon pk : allPokes) {
                if (pk != null) {
                    pk.shuffleStats(this.random);
                }
            }
        }

        List<Pokemon> allPokes = this.getPokemonInclFormes();
        for (Pokemon pk : allPokes) {
            if (pk != null && pk.actuallyCosmetic) {
                pk.copyBaseFormeBaseStats(pk.baseForme);
            }
        }

        if (megaEvolutionSanity) {
            List<MegaEvolution> allMegaEvos = getMegaEvolutions();
            for (MegaEvolution megaEvo: allMegaEvos) {
                if (megaEvo.from.megaEvolutionsFrom.size() > 1) continue;
                megaEvo.to.copyShuffledStatsUpEvolution(megaEvo.from);
            }
        }
    }

    @Override
    public void randomizePokemonStats(boolean evolutionSanity, boolean megaEvolutionSanity) {

        if (evolutionSanity) {
            copyUpEvolutionsHelper(pk -> pk.randomizeStatsWithinBST(AbstractRomHandler.this.random),
                    (evFrom, evTo, toMonIsFinalEvo) -> evTo.copyRandomizedStatsUpEvolution(evFrom)
            );
        } else {
            List<Pokemon> allPokes = this.getPokemonInclFormes();
            for (Pokemon pk : allPokes) {
                if (pk != null) {
                    pk.randomizeStatsWithinBST(this.random);
                }
            }
        }

        List<Pokemon> allPokes = this.getPokemonInclFormes();
        for (Pokemon pk : allPokes) {
            if (pk != null && pk.actuallyCosmetic) {
                pk.copyBaseFormeBaseStats(pk.baseForme);
            }
        }

        if (megaEvolutionSanity) {
            List<MegaEvolution> allMegaEvos = getMegaEvolutions();
            for (MegaEvolution megaEvo: allMegaEvos) {
                if (megaEvo.from.megaEvolutionsFrom.size() > 1) continue;
                megaEvo.to.copyRandomizedStatsUpEvolution(megaEvo.from);
            }
        }
    }

    @Override
    public void updatePokemonStats(int generation) {
        List<Pokemon> pokes = getPokemonInclFormes();

        for (int gen = 6; gen <= generation; gen++) {
            Map<Integer,StatChange> statChanges = getUpdatedPokemonStats(gen);

            for (int i = 1; i < pokes.size(); i++) {
                StatChange changedStats = statChanges.get(i);
                if (changedStats != null) {
                    int statNum = 0;
                    if ((changedStats.stat & Stat.HP.val) != 0) {
                        pokes.get(i).hp = changedStats.values[statNum];
                        statNum++;
                    }
                    if ((changedStats.stat & Stat.ATK.val) != 0) {
                        pokes.get(i).attack = changedStats.values[statNum];
                        statNum++;
                    }
                    if ((changedStats.stat & Stat.DEF.val) != 0) {
                        pokes.get(i).defense = changedStats.values[statNum];
                        statNum++;
                    }
                    if ((changedStats.stat & Stat.SPATK.val) != 0) {
                        if (generationOfPokemon() != 1) {
                            pokes.get(i).spatk = changedStats.values[statNum];
                        }
                        statNum++;
                    }
                    if ((changedStats.stat & Stat.SPDEF.val) != 0) {
                        if (generationOfPokemon() != 1) {
                            pokes.get(i).spdef = changedStats.values[statNum];
                        }
                        statNum++;
                    }
                    if ((changedStats.stat & Stat.SPEED.val) != 0) {
                        pokes.get(i).speed = changedStats.values[statNum];
                        statNum++;
                    }
                    if ((changedStats.stat & Stat.SPECIAL.val) != 0) {
                        pokes.get(i).special = changedStats.values[statNum];
                        pokes.get(i).spatk = changedStats.values[statNum];
                        pokes.get(i).spdef = changedStats.values[statNum];
                    }
                }
            }
        }

//        // non-special stat gen1 pokemon
//        pokes.get(15).attack = 90; // BEEDRILL
//        pokes.get(18).speed = 101; // PIDGEOT
//        pokes.get(25).defense = 40; // PIKACHU
//        pokes.get(26).speed = 110; // RAICHU
//        pokes.get(31).attack = 92; // NIDOQUEEN
//        pokes.get(34).attack = 102; // NIDOKING
//        pokes.get(62).attack = 95; // POLIWRATH
//        pokes.get(76).attack = 120; // GOLEM
//
//        // behavior regarding special stat changes
//        // depending on whether this is actually gen1 or not.
//        if (generationOfPokemon() == 1) {
//            // only update the pokemon who's updated stat was
//            // equal to their Gen1 special stat.
//
//            pokes.get(12).special = 90; // BUTTERFREE
//            // skip PIKACHU s.def
//            pokes.get(36).special = 95; // CLEFABLE
//            // skip WIGGLYTUFF s.atk
//            pokes.get(45).special = 110; // VILEPLUME
//            // skip ALAKAZAM s.def
//            // skip VICTREEBEL s.def
//        } else {
//            // do the special stat changes then move on from gen2 onwards
//
//            pokes.get(12).spatk = 90; // BUTTERFREE
//            pokes.get(25).spdef = 50; // PIKACHU
//            pokes.get(36).spatk = 95; // CLEFABLE
//            pokes.get(40).spatk = 85; // WIGGLYTUFF
//            pokes.get(45).spatk = 110; // VILEPLUME
//            pokes.get(65).spdef = 95; // ALAKAZAM
//            pokes.get(71).spdef = 70; // VICTREEBEL
//
//            // gen 2
//            pokes.get(181).defense = 85; // AMPHAROS
//            pokes.get(182).defense = 95; // BELLOSSOM
//            pokes.get(184).spatk = 60; // AZUMARILL
//            pokes.get(189).spdef = 95; // JUMPLUFF
//
//            // gen 3
//            if (generationOfPokemon() >= 3) {
//                pokes.get(267).spatk = 100; // BEAUTIFLY
//                pokes.get(295).spdef = 73; // EXPLOUD
//            }
//
//            // gen 4
//            if (generationOfPokemon() >= 4) {
//                pokes.get(398).spdef = 60; // STARAPTOR
//                pokes.get(407).defense = 65; // ROSERADE
//            }
//
//            // gen 5
//            if (generationOfPokemon() >= 5) {
//                pokes.get(508).attack = 110; // STOUTLAND
//                pokes.get(521).attack = 115; // UNFEZANT
//                pokes.get(526).spdef = 80; // GIGALITH
//                pokes.get(537).attack = 95; // SEISMITOAD
//                pokes.get(542).spdef = 80; // LEAVANNY
//                pokes.get(545).attack = 100; // SCOLIPEDE
//                pokes.get(553).defense = 80; // KROOKODILE
//            }
//        }
    }

    public Pokemon randomPokemon() {
        checkPokemonRestrictions();
        return mainPokemonList.get(this.random.nextInt(mainPokemonList.size()));
    }

    @Override
    public Pokemon randomPokemonInclFormes() {
        checkPokemonRestrictions();
        return mainPokemonListInclFormes.get(this.random.nextInt(mainPokemonListInclFormes.size()));
    }

    @Override
    public Pokemon randomNonLegendaryPokemon() {
        checkPokemonRestrictions();
        return noLegendaryList.get(this.random.nextInt(noLegendaryList.size()));
    }

    private Pokemon randomNonLegendaryPokemonInclFormes() {
        checkPokemonRestrictions();
        return noLegendaryListInclFormes.get(this.random.nextInt(noLegendaryListInclFormes.size()));
    }

    @Override
    public Pokemon randomLegendaryPokemon() {
        checkPokemonRestrictions();
        return onlyLegendaryList.get(this.random.nextInt(onlyLegendaryList.size()));
    }

    private List<Pokemon> twoEvoPokes;

    @Override
    public Pokemon random2EvosPokemon(boolean allowAltFormes) {
        if (twoEvoPokes == null) {
            // Prepare the list
            twoEvoPokes = new ArrayList<>();
            List<Pokemon> allPokes =
                    allowAltFormes ?
                            this.getPokemonInclFormes()
                                    .stream()
                                    .filter(pk -> pk == null || !pk.actuallyCosmetic)
                                    .collect(Collectors.toList()) :
                            this.getPokemon();
            for (Pokemon pk : allPokes) {
                if (pk != null && pk.evolutionsTo.size() == 0 && pk.evolutionsFrom.size() > 0) {
                    // Potential candidate
                    for (Evolution ev : pk.evolutionsFrom) {
                        // If any of the targets here evolve, the original
                        // Pokemon has 2+ stages.
                        if (ev.to.evolutionsFrom.size() > 0) {
                            twoEvoPokes.add(pk);
                            break;
                        }
                    }
                }
            }
        }
        return twoEvoPokes.get(this.random.nextInt(twoEvoPokes.size()));
    }

    @Override
    public Type randomType() {
        Type t = Type.randomType(this.random);
        while (!typeInGame(t)) {
            t = Type.randomType(this.random);
        }
        return t;
    }

    @Override
    public void randomizePokemonTypes(boolean evolutionSanity, boolean megaEvolutionSanity) {
        List<Pokemon> allPokes = this.getPokemonInclFormes();
        if (evolutionSanity) {
            // Type randomization with evolution sanity
            copyUpEvolutionsHelper(pk -> {
                // Step 1: Basic or Excluded From Copying Pokemon
                // A Basic/EFC pokemon has a 35% chance of a second type if
                // it has an evolution that copies type/stats, a 50% chance
                // otherwise
                pk.primaryType = randomType();
                pk.secondaryType = null;
                if (pk.evolutionsFrom.size() == 1 && pk.evolutionsFrom.get(0).carryStats) {
                    if (AbstractRomHandler.this.random.nextDouble() < 0.35) {
                        pk.secondaryType = randomType();
                        while (pk.secondaryType == pk.primaryType) {
                            pk.secondaryType = randomType();
                        }
                    }
                } else {
                    if (AbstractRomHandler.this.random.nextDouble() < 0.5) {
                        pk.secondaryType = randomType();
                        while (pk.secondaryType == pk.primaryType) {
                            pk.secondaryType = randomType();
                        }
                    }
                }
            }, (evFrom, evTo, toMonIsFinalEvo) -> {
                evTo.primaryType = evFrom.primaryType;
                evTo.secondaryType = evFrom.secondaryType;

                if (evTo.secondaryType == null) {
                    double chance = toMonIsFinalEvo ? 0.25 : 0.15;
                    if (AbstractRomHandler.this.random.nextDouble() < chance) {
                        evTo.secondaryType = randomType();
                        while (evTo.secondaryType == evTo.primaryType) {
                            evTo.secondaryType = randomType();
                        }
                    }
                }
            });
        } else {
            // Entirely random types
            for (Pokemon pkmn : allPokes) {
                if (pkmn != null) {
                    pkmn.primaryType = randomType();
                    pkmn.secondaryType = null;
                    if (this.random.nextDouble() < 0.5) {
                        pkmn.secondaryType = randomType();
                        while (pkmn.secondaryType == pkmn.primaryType) {
                            pkmn.secondaryType = randomType();
                        }
                    }
                }
            }
        }

        for (Pokemon pk : allPokes) {
            if (pk != null && pk.actuallyCosmetic) {
                pk.primaryType = pk.baseForme.primaryType;
                pk.secondaryType = pk.baseForme.secondaryType;
            }
        }

        if (megaEvolutionSanity) {
            List<MegaEvolution> allMegaEvos = getMegaEvolutions();
            for (MegaEvolution megaEvo: allMegaEvos) {
                if (megaEvo.from.megaEvolutionsFrom.size() > 1) continue;
                megaEvo.to.primaryType = megaEvo.from.primaryType;
                megaEvo.to.secondaryType = megaEvo.from.secondaryType;

                if (megaEvo.to.secondaryType == null) {
                    if (this.random.nextDouble() < 0.25) {
                        megaEvo.to.secondaryType = randomType();
                        while (megaEvo.to.secondaryType == megaEvo.to.primaryType) {
                            megaEvo.to.secondaryType = randomType();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void randomizeAbilities(boolean evolutionSanity, boolean allowWonderGuard, boolean banTrappingAbilities,
                                   boolean banNegativeAbilities, boolean banBadAbilities, boolean megaEvolutionSanity,
                                   boolean weighDuplicatesTogether) {
        // Abilities don't exist in some games...
        if (this.abilitiesPerPokemon() == 0) {
            return;
        }

        final boolean hasDWAbilities = (this.abilitiesPerPokemon() == 3);

        final List<Integer> bannedAbilities = new ArrayList<>();

        if (!allowWonderGuard) {
            bannedAbilities.add(GlobalConstants.WONDER_GUARD_INDEX);
        }

        if (banTrappingAbilities) {
            bannedAbilities.addAll(GlobalConstants.battleTrappingAbilities);
        }

        if (banNegativeAbilities) {
            bannedAbilities.addAll(GlobalConstants.negativeAbilities);
        }

        if (banBadAbilities) {
            bannedAbilities.addAll(GlobalConstants.badAbilities);
        }

        if (weighDuplicatesTogether) {
            bannedAbilities.addAll(GlobalConstants.duplicateAbilities);
            if (generationOfPokemon() == 3) {
                bannedAbilities.add(77); // Special case for Cacophony in gen 3
            }
        }

        final int maxAbility = this.highestAbilityIndex();

        if (evolutionSanity) {
            // copy abilities straight up evolution lines
            // still keep WG as an exception, though

            copyUpEvolutionsHelper(pk -> {
                if (pk.ability1 != GlobalConstants.WONDER_GUARD_INDEX
                        && pk.ability2 != GlobalConstants.WONDER_GUARD_INDEX
                        && pk.ability3 != GlobalConstants.WONDER_GUARD_INDEX) {
                    // Pick first ability
                    pk.ability1 = pickRandomAbility(maxAbility, bannedAbilities, weighDuplicatesTogether);

                    // Second ability?
                    if (AbstractRomHandler.this.random.nextDouble() < 0.5) {
                        // Yes, second ability
                        pk.ability2 = pickRandomAbility(maxAbility, bannedAbilities, weighDuplicatesTogether,
                                pk.ability1);
                    } else {
                        // Nope
                        pk.ability2 = 0;
                    }

                    // Third ability?
                    if (hasDWAbilities) {
                        pk.ability3 = pickRandomAbility(maxAbility, bannedAbilities, weighDuplicatesTogether,
                                pk.ability1, pk.ability2);
                    }
                }
            }, (evFrom, evTo, toMonIsFinalEvo) -> {
                if (evTo.ability1 != GlobalConstants.WONDER_GUARD_INDEX
                        && evTo.ability2 != GlobalConstants.WONDER_GUARD_INDEX
                        && evTo.ability3 != GlobalConstants.WONDER_GUARD_INDEX) {
                    evTo.ability1 = evFrom.ability1;
                    evTo.ability2 = evFrom.ability2;
                    evTo.ability3 = evFrom.ability3;
                }
            });
        } else {
            List<Pokemon> allPokes = this.getPokemonInclFormes();
            for (Pokemon pk : allPokes) {
                if (pk == null) {
                    continue;
                }

                // Don't remove WG if already in place.
                if (pk.ability1 != GlobalConstants.WONDER_GUARD_INDEX
                        && pk.ability2 != GlobalConstants.WONDER_GUARD_INDEX
                        && pk.ability3 != GlobalConstants.WONDER_GUARD_INDEX) {
                    // Pick first ability
                    pk.ability1 = this.pickRandomAbility(maxAbility, bannedAbilities, weighDuplicatesTogether);

                    // Second ability?
                    if (this.random.nextDouble() < 0.5) {
                        // Yes, second ability
                        pk.ability2 = this.pickRandomAbility(maxAbility, bannedAbilities, weighDuplicatesTogether,
                                pk.ability1);
                    } else {
                        // Nope
                        pk.ability2 = 0;
                    }

                    // Third ability?
                    if (hasDWAbilities) {
                        pk.ability3 = pickRandomAbility(maxAbility, bannedAbilities, weighDuplicatesTogether,
                                pk.ability1, pk.ability2);
                    }
                }
            }
        }

        List<Pokemon> allPokes = this.getPokemonInclFormes();
        for (Pokemon pk : allPokes) {
            if (pk != null && pk.actuallyCosmetic) {
                pk.copyBaseFormeAbilities(pk.baseForme);
            }
        }

        if (megaEvolutionSanity) {
            List<MegaEvolution> allMegaEvos = this.getMegaEvolutions();
            for (MegaEvolution megaEvo: allMegaEvos) {
                if (megaEvo.from.megaEvolutionsFrom.size() > 1) continue;
                megaEvo.to.ability1 = megaEvo.from.ability1;
                megaEvo.to.ability2 = megaEvo.from.ability2;
                megaEvo.to.ability3 = megaEvo.from.ability3;
            }
        }
    }

    private int pickRandomAbility(int maxAbility, List<Integer> bannedAbilities, boolean useVariations,
                                  int... alreadySetAbilities) {
        int newAbility;

        while (true) {
            newAbility = this.random.nextInt(maxAbility) + 1;

            if (bannedAbilities.contains(newAbility)) {
                continue;
            }

            boolean repeat = false;
            for (int alreadySetAbility : alreadySetAbilities) {
                if (alreadySetAbility == newAbility) {
                    repeat = true;
                    break;
                }
            }

            if (!repeat) {
                if (useVariations) {
                    Map<Integer,List<Integer>> abilityVariations = getAbilityVariations();
                    for (int baseAbility: abilityVariations.keySet()) {
                        if (newAbility == baseAbility) {
                            List<Integer> variationsForThisAbility = abilityVariations.get(newAbility);
                            newAbility = variationsForThisAbility.get(this.random.nextInt(variationsForThisAbility.size()));
                            break;
                        }
                    }
                }
                break;
            }
        }

        return newAbility;
    }

    @Override
    public void randomEncounters(boolean useTimeOfDay, boolean catchEmAll, boolean typeThemed,
                                 boolean usePowerLevels, boolean noLegendaries, boolean balanceShakingGrass,
                                 int levelModifier, boolean allowAltFormes, boolean abilitiesAreRandomized) {
        List<EncounterSet> currentEncounters = this.getEncounters(useTimeOfDay);

        if (isORAS) {
            List<EncounterSet> collapsedEncounters = collapseAreasORAS(currentEncounters);
            area1to1EncountersImpl(collapsedEncounters, catchEmAll, typeThemed, usePowerLevels,
                    noLegendaries, levelModifier, allowAltFormes, abilitiesAreRandomized);
            enhanceRandomEncountersORAS(collapsedEncounters, catchEmAll, typeThemed, usePowerLevels, noLegendaries, allowAltFormes);
            setEncounters(useTimeOfDay, currentEncounters);
            return;
        }

        checkPokemonRestrictions();

        // New: randomize the order encounter sets are randomized in.
        // Leads to less predictable results for various modifiers.
        // Need to keep the original ordering around for saving though.
        List<EncounterSet> scrambledEncounters = new ArrayList<>(currentEncounters);
        Collections.shuffle(scrambledEncounters, this.random);

        List<Pokemon> banned = this.bannedForWildEncounters();
        banned.addAll(this.getBannedFormes());
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        // Assume EITHER catch em all OR type themed OR match strength for now
        if (catchEmAll) {

            List<Pokemon> allPokes;
            if (allowAltFormes) {
                allPokes = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes) : new ArrayList<>(
                        mainPokemonListInclFormes);
                allPokes.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
            } else {
                allPokes = noLegendaries ? new ArrayList<>(noLegendaryList) : new ArrayList<>(
                        mainPokemonList);
            }
            allPokes.removeAll(banned);

            for (EncounterSet area : scrambledEncounters) {
                List<Pokemon> pickablePokemon = allPokes;
                if (area.bannedPokemon.size() > 0) {
                    pickablePokemon = new ArrayList<>(allPokes);
                    pickablePokemon.removeAll(area.bannedPokemon);
                }
                for (Encounter enc : area.encounters) {
                    // Pick a random pokemon
                    if (pickablePokemon.size() == 0) {
                        // Only banned pokes are left, ignore them and pick
                        // something else for now.
                        List<Pokemon> tempPickable;
                        if (allowAltFormes) {
                            tempPickable = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes) : new ArrayList<>(
                                    mainPokemonListInclFormes);
                            tempPickable.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
                        } else {
                            tempPickable = noLegendaries ? new ArrayList<>(noLegendaryList) : new ArrayList<>(
                                    mainPokemonList);
                        }
                        tempPickable.removeAll(banned);
                        tempPickable.removeAll(area.bannedPokemon);
                        if (tempPickable.size() == 0) {
                            throw new RandomizationException("ERROR: Couldn't replace a wild Pokemon!");
                        }
                        int picked = this.random.nextInt(tempPickable.size());
                        enc.pokemon = tempPickable.get(picked);
                        setFormeForEncounter(enc, enc.pokemon);
                    } else {
                        // Picked this Pokemon, remove it
                        int picked = this.random.nextInt(pickablePokemon.size());
                        enc.pokemon = pickablePokemon.get(picked);
                        pickablePokemon.remove(picked);
                        if (allPokes != pickablePokemon) {
                            allPokes.remove(enc.pokemon);
                        }
                        setFormeForEncounter(enc, enc.pokemon);
                        if (allPokes.size() == 0) {
                            // Start again
                            if (allowAltFormes) {
                                allPokes.addAll(noLegendaries ? noLegendaryListInclFormes : mainPokemonListInclFormes);
                                allPokes.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
                            } else {
                                allPokes.addAll(noLegendaries ? noLegendaryList : mainPokemonList);
                            }
                            allPokes.removeAll(banned);
                            if (pickablePokemon != allPokes) {
                                pickablePokemon.addAll(allPokes);
                                pickablePokemon.removeAll(area.bannedPokemon);
                            }
                        }
                    }
                }
            }
        } else if (typeThemed) {
            Map<Type, List<Pokemon>> cachedPokeLists = new TreeMap<>();
            for (EncounterSet area : scrambledEncounters) {
                List<Pokemon> possiblePokemon = null;
                int iterLoops = 0;
                while (possiblePokemon == null && iterLoops < 10000) {
                    Type areaTheme = randomType();
                    if (!cachedPokeLists.containsKey(areaTheme)) {
                        List<Pokemon> pType = allowAltFormes ? pokemonOfTypeInclFormes(areaTheme, noLegendaries) :
                                pokemonOfType(areaTheme, noLegendaries);
                        pType.removeAll(banned);
                        cachedPokeLists.put(areaTheme, pType);
                    }
                    possiblePokemon = cachedPokeLists.get(areaTheme);
                    if (area.bannedPokemon.size() > 0) {
                        possiblePokemon = new ArrayList<>(possiblePokemon);
                        possiblePokemon.removeAll(area.bannedPokemon);
                    }
                    if (possiblePokemon.size() == 0) {
                        // Can't use this type for this area
                        possiblePokemon = null;
                    }
                    iterLoops++;
                }
                if (possiblePokemon == null) {
                    throw new RandomizationException("Could not randomize an area in a reasonable amount of attempts.");
                }
                for (Encounter enc : area.encounters) {
                    // Pick a random themed pokemon
                    enc.pokemon = possiblePokemon.get(this.random.nextInt(possiblePokemon.size()));
                    while (enc.pokemon.actuallyCosmetic) {
                        enc.pokemon = possiblePokemon.get(this.random.nextInt(possiblePokemon.size()));
                    }
                    setFormeForEncounter(enc, enc.pokemon);
                }
            }
        } else if (usePowerLevels) {
            List<Pokemon> allowedPokes;
            if (allowAltFormes) {
                allowedPokes  = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes)
                        : new ArrayList<>(mainPokemonListInclFormes);
            } else {
                allowedPokes = noLegendaries ? new ArrayList<>(noLegendaryList)
                        : new ArrayList<>(mainPokemonList);
            }
            allowedPokes.removeAll(banned);
            for (EncounterSet area : scrambledEncounters) {
                List<Pokemon> localAllowed = allowedPokes;
                if (area.bannedPokemon.size() > 0) {
                    localAllowed = new ArrayList<>(allowedPokes);
                    localAllowed.removeAll(area.bannedPokemon);
                }
                for (Encounter enc : area.encounters) {
                    if (balanceShakingGrass) {
                        if (area.displayName.contains("Shaking")) {
                            enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, (enc.level + enc.maxLevel) / 2);
                            while (enc.pokemon.actuallyCosmetic) {
                                enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, (enc.level + enc.maxLevel) / 2);
                            }
                            setFormeForEncounter(enc, enc.pokemon);
                        } else {
                            enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                            while (enc.pokemon.actuallyCosmetic) {
                                enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                            }
                            setFormeForEncounter(enc, enc.pokemon);
                        }
                    } else {
                        enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                        while (enc.pokemon.actuallyCosmetic) {
                            enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                        }
                        setFormeForEncounter(enc, enc.pokemon);
                    }
                }
            }
        } else {
            // Entirely random
            for (EncounterSet area : scrambledEncounters) {
                for (Encounter enc : area.encounters) {
                    enc.pokemon = pickEntirelyRandomPokemon(allowAltFormes, noLegendaries, area, banned);
                    setFormeForEncounter(enc, enc.pokemon);
                }
            }
        }
        if (levelModifier != 0) {
            for (EncounterSet area : currentEncounters) {
                for (Encounter enc : area.encounters) {
                    enc.level = Math.min(100, (int) Math.round(enc.level * (1 + levelModifier / 100.0)));
                    enc.maxLevel = Math.min(100, (int) Math.round(enc.maxLevel * (1 + levelModifier / 100.0)));
                }
            }
        }

        setEncounters(useTimeOfDay, currentEncounters);
    }

    @Override
    public void area1to1Encounters(boolean useTimeOfDay, boolean catchEmAll, boolean typeThemed,
                                   boolean usePowerLevels, boolean noLegendaries, int levelModifier,
                                   boolean allowAltformes, boolean abilitiesAreRandomized) {
        List<EncounterSet> currentEncounters = this.getEncounters(useTimeOfDay);
        if (isORAS) {
            List<EncounterSet> collapsedEncounters = collapseAreasORAS(currentEncounters);
            area1to1EncountersImpl(collapsedEncounters, catchEmAll, typeThemed, usePowerLevels,
                    noLegendaries, levelModifier, allowAltformes, abilitiesAreRandomized);
            setEncounters(useTimeOfDay, currentEncounters);
            return;
        } else {
            area1to1EncountersImpl(currentEncounters, catchEmAll, typeThemed, usePowerLevels,
                    noLegendaries, levelModifier, allowAltformes, abilitiesAreRandomized);
            setEncounters(useTimeOfDay, currentEncounters);
        }
    }

    private void area1to1EncountersImpl(List<EncounterSet> currentEncounters, boolean catchEmAll, boolean typeThemed,
                                        boolean usePowerLevels, boolean noLegendaries, int levelModifier,
                                        boolean allowAltFormes, boolean abilitiesAreRandomized) {
        checkPokemonRestrictions();
        List<Pokemon> banned = this.bannedForWildEncounters();
        banned.addAll(this.getBannedFormes());
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }

        // New: randomize the order encounter sets are randomized in.
        // Leads to less predictable results for various modifiers.
        // Need to keep the original ordering around for saving though.
        List<EncounterSet> scrambledEncounters = new ArrayList<>(currentEncounters);
        Collections.shuffle(scrambledEncounters, this.random);

        // Assume EITHER catch em all OR type themed for now
        if (catchEmAll) {
            List<Pokemon> allPokes;
            if (allowAltFormes) {
                allPokes = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes) : new ArrayList<>(
                        mainPokemonListInclFormes);
                allPokes.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
            } else {
                allPokes = noLegendaries ? new ArrayList<>(noLegendaryList) : new ArrayList<>(
                        mainPokemonList);
            }
            allPokes.removeAll(banned);
            for (EncounterSet area : scrambledEncounters) {
                // Poke-set
                Set<Pokemon> inArea = pokemonInArea(area);
                // Build area map using catch em all
                Map<Pokemon, Pokemon> areaMap = new TreeMap<>();
                List<Pokemon> pickablePokemon = allPokes;
                if (area.bannedPokemon.size() > 0) {
                    pickablePokemon = new ArrayList<>(allPokes);
                    pickablePokemon.removeAll(area.bannedPokemon);
                }
                for (Pokemon areaPk : inArea) {
                    if (pickablePokemon.size() == 0) {
                        // No more pickable pokes left, take a random one
                        List<Pokemon> tempPickable;
                        if (allowAltFormes) {
                            tempPickable = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes) : new ArrayList<>(
                                    mainPokemonListInclFormes);
                            tempPickable.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
                        } else {
                            tempPickable = noLegendaries ? new ArrayList<>(noLegendaryList) : new ArrayList<>(
                                    mainPokemonList);
                        }
                        tempPickable.removeAll(banned);
                        tempPickable.removeAll(area.bannedPokemon);
                        if (tempPickable.size() == 0) {
                            throw new RandomizationException("ERROR: Couldn't replace a wild Pokemon!");
                        }
                        int picked = this.random.nextInt(tempPickable.size());
                        Pokemon pickedMN = tempPickable.get(picked);
                        areaMap.put(areaPk, pickedMN);
                    } else {
                        int picked = this.random.nextInt(allPokes.size());
                        Pokemon pickedMN = allPokes.get(picked);
                        areaMap.put(areaPk, pickedMN);
                        pickablePokemon.remove(pickedMN);
                        if (allPokes != pickablePokemon) {
                            allPokes.remove(pickedMN);
                        }
                        if (allPokes.size() == 0) {
                            // Start again
                            if (allowAltFormes) {
                                allPokes.addAll(noLegendaries ? noLegendaryListInclFormes : mainPokemonListInclFormes);
                                allPokes.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
                            } else {
                                allPokes.addAll(noLegendaries ? noLegendaryList : mainPokemonList);
                            }
                            allPokes.removeAll(banned);
                            if (pickablePokemon != allPokes) {
                                pickablePokemon.addAll(allPokes);
                                pickablePokemon.removeAll(area.bannedPokemon);
                            }
                        }
                    }
                }
                for (Encounter enc : area.encounters) {
                    // Apply the map
                    enc.pokemon = areaMap.get(enc.pokemon);
                    setFormeForEncounter(enc, enc.pokemon);
                }
            }
        } else if (typeThemed) {
            Map<Type, List<Pokemon>> cachedPokeLists = new TreeMap<>();
            for (EncounterSet area : scrambledEncounters) {
                // Poke-set
                Set<Pokemon> inArea = pokemonInArea(area);
                List<Pokemon> possiblePokemon = null;
                int iterLoops = 0;
                while (possiblePokemon == null && iterLoops < 10000) {
                    Type areaTheme = randomType();
                    if (!cachedPokeLists.containsKey(areaTheme)) {
                        List<Pokemon> pType = allowAltFormes ? pokemonOfTypeInclFormes(areaTheme, noLegendaries) :
                                pokemonOfType(areaTheme, noLegendaries);
                        pType.removeAll(banned);
                        cachedPokeLists.put(areaTheme, pType);
                    }
                    possiblePokemon = new ArrayList<>(cachedPokeLists.get(areaTheme));
                    if (area.bannedPokemon.size() > 0) {
                        possiblePokemon.removeAll(area.bannedPokemon);
                    }
                    if (possiblePokemon.size() < inArea.size()) {
                        // Can't use this type for this area
                        possiblePokemon = null;
                    }
                    iterLoops++;
                }
                if (possiblePokemon == null) {
                    throw new RandomizationException("Could not randomize an area in a reasonable amount of attempts.");
                }

                // Build area map using type theme.
                Map<Pokemon, Pokemon> areaMap = new TreeMap<>();
                for (Pokemon areaPk : inArea) {
                    int picked = this.random.nextInt(possiblePokemon.size());
                    Pokemon pickedMN = possiblePokemon.get(picked);
                    while (pickedMN.actuallyCosmetic) {
                        picked = this.random.nextInt(possiblePokemon.size());
                        pickedMN = possiblePokemon.get(picked);
                    }
                    areaMap.put(areaPk, pickedMN);
                    possiblePokemon.remove(picked);
                }
                for (Encounter enc : area.encounters) {
                    // Apply the map
                    enc.pokemon = areaMap.get(enc.pokemon);
                    setFormeForEncounter(enc, enc.pokemon);
                }
            }
        } else if (usePowerLevels) {
            List<Pokemon> allowedPokes;
            if (allowAltFormes) {
                allowedPokes  = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes)
                        : new ArrayList<>(mainPokemonListInclFormes);
            } else {
                allowedPokes = noLegendaries ? new ArrayList<>(noLegendaryList)
                        : new ArrayList<>(mainPokemonList);
            }
            allowedPokes.removeAll(banned);
            for (EncounterSet area : scrambledEncounters) {
                // Poke-set
                Set<Pokemon> inArea = pokemonInArea(area);
                // Build area map using randoms
                Map<Pokemon, Pokemon> areaMap = new TreeMap<>();
                List<Pokemon> usedPks = new ArrayList<>();
                List<Pokemon> localAllowed = allowedPokes;
                if (area.bannedPokemon.size() > 0) {
                    localAllowed = new ArrayList<>(allowedPokes);
                    localAllowed.removeAll(area.bannedPokemon);
                }
                for (Pokemon areaPk : inArea) {
                    Pokemon picked = pickWildPowerLvlReplacement(localAllowed, areaPk, false, usedPks, 100);
                    while (picked.actuallyCosmetic) {
                        picked = pickWildPowerLvlReplacement(localAllowed, areaPk, false, usedPks, 100);
                    }
                    areaMap.put(areaPk, picked);
                    usedPks.add(picked);
                }
                for (Encounter enc : area.encounters) {
                    // Apply the map
                    enc.pokemon = areaMap.get(enc.pokemon);
                    setFormeForEncounter(enc, enc.pokemon);
                }
            }
        } else {
            // Entirely random
            for (EncounterSet area : scrambledEncounters) {
                // Poke-set
                Set<Pokemon> inArea = pokemonInArea(area);
                // Build area map using randoms
                Map<Pokemon, Pokemon> areaMap = new TreeMap<>();
                for (Pokemon areaPk : inArea) {
                    Pokemon picked = pickEntirelyRandomPokemon(allowAltFormes, noLegendaries, area, banned);
                    while (areaMap.containsValue(picked)) {
                        picked = pickEntirelyRandomPokemon(allowAltFormes, noLegendaries, area, banned);
                    }
                    areaMap.put(areaPk, picked);
                }
                for (Encounter enc : area.encounters) {
                    // Apply the map
                    enc.pokemon = areaMap.get(enc.pokemon);
                    setFormeForEncounter(enc, enc.pokemon);
                }
            }
        }

        if (levelModifier != 0) {
            for (EncounterSet area : currentEncounters) {
                for (Encounter enc : area.encounters) {
                    enc.level = Math.min(100, (int) Math.round(enc.level * (1 + levelModifier / 100.0)));
                    enc.maxLevel = Math.min(100, (int) Math.round(enc.maxLevel * (1 + levelModifier / 100.0)));
                }
            }
        }
    }

    @Override
    public void game1to1Encounters(boolean useTimeOfDay, boolean usePowerLevels, boolean noLegendaries,
                                   int levelModifier, boolean allowAltFormes, boolean abilitiesAreRandomized) {
        checkPokemonRestrictions();
        // Build the full 1-to-1 map
        Map<Pokemon, Pokemon> translateMap = new TreeMap<>();
        List<Pokemon> remainingLeft = allPokemonInclFormesWithoutNull();
        remainingLeft.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
        List<Pokemon> remainingRight;
        if (allowAltFormes) {
            remainingRight = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes)
                    : new ArrayList<>(mainPokemonListInclFormes);
            remainingRight.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
        } else {
            remainingRight = noLegendaries ? new ArrayList<>(noLegendaryList)
                    : new ArrayList<>(mainPokemonList);
        }
        List<Pokemon> banned = this.bannedForWildEncounters();
        banned.addAll(this.getBannedFormes());
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        // Banned pokemon should be mapped to themselves
        for (Pokemon bannedPK : banned) {
            translateMap.put(bannedPK, bannedPK);
            remainingLeft.remove(bannedPK);
            remainingRight.remove(bannedPK);
        }
        while (!remainingLeft.isEmpty()) {
            if (usePowerLevels) {
                int pickedLeft = this.random.nextInt(remainingLeft.size());
                Pokemon pickedLeftP = remainingLeft.remove(pickedLeft);
                Pokemon pickedRightP;
                if (remainingRight.size() == 1) {
                    // pick this (it may or may not be the same poke)
                    pickedRightP = remainingRight.get(0);
                } else {
                    // pick on power level with the current one blocked
                    pickedRightP = pickWildPowerLvlReplacement(remainingRight, pickedLeftP, true, null, 100);
                }
                remainingRight.remove(pickedRightP);
                translateMap.put(pickedLeftP, pickedRightP);
            } else {
                int pickedLeft = this.random.nextInt(remainingLeft.size());
                int pickedRight = this.random.nextInt(remainingRight.size());
                Pokemon pickedLeftP = remainingLeft.remove(pickedLeft);
                Pokemon pickedRightP = remainingRight.get(pickedRight);
                while (pickedLeftP.number == pickedRightP.number && remainingRight.size() != 1) {
                    // Reroll for a different pokemon if at all possible
                    pickedRight = this.random.nextInt(remainingRight.size());
                    pickedRightP = remainingRight.get(pickedRight);
                }
                remainingRight.remove(pickedRight);
                translateMap.put(pickedLeftP, pickedRightP);
            }
            if (remainingRight.size() == 0) {
                // restart
                if (allowAltFormes) {
                    remainingRight.addAll(noLegendaries ? noLegendaryListInclFormes : mainPokemonListInclFormes);
                    remainingRight.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
                } else {
                    remainingRight.addAll(noLegendaries ? noLegendaryList : mainPokemonList);
                }
                remainingRight.removeAll(banned);
            }
        }

        // Map remaining to themselves just in case
        List<Pokemon> allPokes = allPokemonInclFormesWithoutNull();
        for (Pokemon poke : allPokes) {
            if (!translateMap.containsKey(poke)) {
                translateMap.put(poke, poke);
            }
        }

        List<EncounterSet> currentEncounters = this.getEncounters(useTimeOfDay);

        for (EncounterSet area : currentEncounters) {
            for (Encounter enc : area.encounters) {
                // Apply the map
                enc.pokemon = translateMap.get(enc.pokemon);
                if (area.bannedPokemon.contains(enc.pokemon)) {
                    // Ignore the map and put a random non-banned poke
                    List<Pokemon> tempPickable;
                    if (allowAltFormes) {
                        tempPickable = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes)
                                : new ArrayList<>(mainPokemonListInclFormes);
                        tempPickable.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
                    } else {
                        tempPickable = noLegendaries ? new ArrayList<>(noLegendaryList)
                                : new ArrayList<>(mainPokemonList);
                    }
                    tempPickable.removeAll(banned);
                    tempPickable.removeAll(area.bannedPokemon);
                    if (tempPickable.size() == 0) {
                        throw new RandomizationException("ERROR: Couldn't replace a wild Pokemon!");
                    }
                    if (usePowerLevels) {
                        enc.pokemon = pickWildPowerLvlReplacement(tempPickable, enc.pokemon, false, null, 100);
                    } else {
                        int picked = this.random.nextInt(tempPickable.size());
                        enc.pokemon = tempPickable.get(picked);
                    }
                }
                setFormeForEncounter(enc, enc.pokemon);
            }
        }
        if (levelModifier != 0) {
            for (EncounterSet area : currentEncounters) {
                for (Encounter enc : area.encounters) {
                    enc.level = Math.min(100, (int) Math.round(enc.level * (1 + levelModifier / 100.0)));
                    enc.maxLevel = Math.min(100, (int) Math.round(enc.maxLevel * (1 + levelModifier / 100.0)));
                }
            }
        }

        setEncounters(useTimeOfDay, currentEncounters);

    }

    @Override
    public void onlyChangeWildLevels(int levelModifier) {
        List<EncounterSet> currentEncounters = this.getEncounters(true);

        if (levelModifier != 0) {
            for (EncounterSet area : currentEncounters) {
                for (Encounter enc : area.encounters) {
                    enc.level = Math.min(100, (int) Math.round(enc.level * (1 + levelModifier / 100.0)));
                    enc.maxLevel = Math.min(100, (int) Math.round(enc.maxLevel * (1 + levelModifier / 100.0)));
                }
            }
            setEncounters(true, currentEncounters);
        }
    }

    private void enhanceRandomEncountersORAS(List<EncounterSet> collapsedEncounters, boolean catchEmAll,
             boolean typeThemed, boolean usePowerLevels, boolean noLegendaries, boolean allowAltFormes) {
        List<Pokemon> banned = this.bannedForWildEncounters();
        Map<Integer, List<EncounterSet>> zonesToEncounters = mapZonesToEncounters(collapsedEncounters);
        Map<Type, List<Pokemon>> cachedPokeLists = new TreeMap<>();
        for (List<EncounterSet> encountersInZone : zonesToEncounters.values()) {
            int currentAreaIndex = -1;
            List<EncounterSet> nonRockSmashAreas = new ArrayList<>();
            Map<Integer, List<Integer>> areasAndEncountersToRandomize = new TreeMap<>();
            // Since Rock Smash Pokemon do not show up on DexNav, they can be fully randomized
            for (EncounterSet area : encountersInZone) {
                if (area.displayName.contains("Rock Smash")) {
                    // Assume EITHER catch em all OR type themed OR match strength for now
                    if (catchEmAll) {
                        for (Encounter enc : area.encounters) {
                            boolean shouldRandomize = doesAnotherEncounterWithSamePokemonExistInArea(enc, area);
                            if (shouldRandomize) {
                                enc.pokemon = pickEntirelyRandomPokemon(allowAltFormes, noLegendaries, area, banned);
                                setFormeForEncounter(enc, enc.pokemon);
                            }
                        }
                    } else if (typeThemed) {
                        List<Pokemon> possiblePokemon = null;
                        int iterLoops = 0;
                        while (possiblePokemon == null && iterLoops < 10000) {
                            Type areaTheme = randomType();
                            if (!cachedPokeLists.containsKey(areaTheme)) {
                                List<Pokemon> pType = allowAltFormes ? pokemonOfTypeInclFormes(areaTheme, noLegendaries) :
                                        pokemonOfType(areaTheme, noLegendaries);
                                pType.removeAll(banned);
                                cachedPokeLists.put(areaTheme, pType);
                            }
                            possiblePokemon = cachedPokeLists.get(areaTheme);
                            if (area.bannedPokemon.size() > 0) {
                                possiblePokemon = new ArrayList<>(possiblePokemon);
                                possiblePokemon.removeAll(area.bannedPokemon);
                            }
                            if (possiblePokemon.size() == 0) {
                                // Can't use this type for this area
                                possiblePokemon = null;
                            }
                            iterLoops++;
                        }
                        if (possiblePokemon == null) {
                            throw new RandomizationException("Could not randomize an area in a reasonable amount of attempts.");
                        }
                        for (Encounter enc : area.encounters) {
                            // Pick a random themed pokemon
                            enc.pokemon = possiblePokemon.get(this.random.nextInt(possiblePokemon.size()));
                            while (enc.pokemon.actuallyCosmetic) {
                                enc.pokemon = possiblePokemon.get(this.random.nextInt(possiblePokemon.size()));
                            }
                            setFormeForEncounter(enc, enc.pokemon);
                        }
                    } else if (usePowerLevels) {
                        List<Pokemon> allowedPokes;
                        if (allowAltFormes) {
                            allowedPokes  = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes)
                                    : new ArrayList<>(mainPokemonListInclFormes);
                        } else {
                            allowedPokes = noLegendaries ? new ArrayList<>(noLegendaryList)
                                    : new ArrayList<>(mainPokemonList);
                        }
                        allowedPokes.removeAll(banned);
                        List<Pokemon> localAllowed = allowedPokes;
                        if (area.bannedPokemon.size() > 0) {
                            localAllowed = new ArrayList<>(allowedPokes);
                            localAllowed.removeAll(area.bannedPokemon);
                        }
                        for (Encounter enc : area.encounters) {
                            enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                            while (enc.pokemon.actuallyCosmetic) {
                                enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                            }
                            setFormeForEncounter(enc, enc.pokemon);
                        }
                    } else {
                        // Entirely random
                        for (Encounter enc : area.encounters) {
                            enc.pokemon = pickEntirelyRandomPokemon(allowAltFormes, noLegendaries, area, banned);
                            setFormeForEncounter(enc, enc.pokemon);
                        }
                    }
                } else {
                    currentAreaIndex++;
                    nonRockSmashAreas.add(area);
                    List<Integer> encounterIndices = new ArrayList<>();
                    for (int i = 0; i < area.encounters.size(); i++) {
                        encounterIndices.add(i);
                    }
                    areasAndEncountersToRandomize.put(currentAreaIndex, encounterIndices);
                }
            }

            // Now, randomize non-Rock Smash Pokemon until we hit the threshold for DexNav
            int crashThreshold = computeDexNavCrashThreshold(encountersInZone);
            while (crashThreshold < 18 && areasAndEncountersToRandomize.size() > 0) {
                Set<Integer> areaIndices = areasAndEncountersToRandomize.keySet();
                int areaIndex = areaIndices.stream().skip(this.random.nextInt(areaIndices.size())).findFirst().orElse(-1);
                List<Integer> encounterIndices = areasAndEncountersToRandomize.get(areaIndex);
                int indexInListOfEncounterIndices = this.random.nextInt(encounterIndices.size());
                int randomEncounterIndex = encounterIndices.get(indexInListOfEncounterIndices);
                EncounterSet area = nonRockSmashAreas.get(areaIndex);
                Encounter enc = area.encounters.get(randomEncounterIndex);
                // Assume EITHER catch em all OR type themed OR match strength for now
                if (catchEmAll) {
                    boolean shouldRandomize = doesAnotherEncounterWithSamePokemonExistInArea(enc, area);
                    if (shouldRandomize) {
                        enc.pokemon = pickEntirelyRandomPokemon(allowAltFormes, noLegendaries, area, banned);
                        setFormeForEncounter(enc, enc.pokemon);
                    }
                } else if (typeThemed) {
                    List<Pokemon> possiblePokemon = null;
                    Type areaTheme = getTypeForArea(area);
                    if (!cachedPokeLists.containsKey(areaTheme)) {
                        List<Pokemon> pType = allowAltFormes ? pokemonOfTypeInclFormes(areaTheme, noLegendaries) :
                                pokemonOfType(areaTheme, noLegendaries);
                        pType.removeAll(banned);
                        cachedPokeLists.put(areaTheme, pType);
                    }
                    possiblePokemon = cachedPokeLists.get(areaTheme);
                    if (area.bannedPokemon.size() > 0) {
                        possiblePokemon = new ArrayList<>(possiblePokemon);
                        possiblePokemon.removeAll(area.bannedPokemon);
                    }
                    if (possiblePokemon.size() == 0) {
                        // Can't use this type for this area
                        throw new RandomizationException("Could not find a possible Pokemon of the correct type.");
                    }
                    // Pick a random themed pokemon
                    enc.pokemon = possiblePokemon.get(this.random.nextInt(possiblePokemon.size()));
                    while (enc.pokemon.actuallyCosmetic) {
                        enc.pokemon = possiblePokemon.get(this.random.nextInt(possiblePokemon.size()));
                    }
                    setFormeForEncounter(enc, enc.pokemon);
                } else if (usePowerLevels) {
                    List<Pokemon> allowedPokes;
                    if (allowAltFormes) {
                        allowedPokes  = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes)
                                : new ArrayList<>(mainPokemonListInclFormes);
                    } else {
                        allowedPokes = noLegendaries ? new ArrayList<>(noLegendaryList)
                                : new ArrayList<>(mainPokemonList);
                    }
                    allowedPokes.removeAll(banned);
                    List<Pokemon> localAllowed = allowedPokes;
                    if (area.bannedPokemon.size() > 0) {
                        localAllowed = new ArrayList<>(allowedPokes);
                        localAllowed.removeAll(area.bannedPokemon);
                    }
                    enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                    while (enc.pokemon.actuallyCosmetic) {
                        enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                    }
                    setFormeForEncounter(enc, enc.pokemon);
                } else {
                    // Entirely random
                    enc.pokemon = pickEntirelyRandomPokemon(allowAltFormes, noLegendaries, area, banned);
                    setFormeForEncounter(enc, enc.pokemon);
                }
                crashThreshold = computeDexNavCrashThreshold(encountersInZone);
                encounterIndices.remove(indexInListOfEncounterIndices);
                if (encounterIndices.size() == 0) {
                    areasAndEncountersToRandomize.remove(areaIndex);
                }
            }
        }
    }

    private Type getTypeForArea(EncounterSet area) {
        Pokemon firstPokemon = area.encounters.get(0).pokemon;
        if (area.encounters.get(0).formeNumber != 0) {
            firstPokemon = getAltFormeOfPokemon(firstPokemon, area.encounters.get(0).formeNumber);
        }
        Type primaryType = firstPokemon.primaryType;
        int primaryCount = 1;
        Type secondaryType = null;
        int secondaryCount = 0;
        if (firstPokemon.secondaryType != null) {
            secondaryType = firstPokemon.secondaryType;
            secondaryCount = 1;
        }
        for (int i = 1; i < area.encounters.size(); i++) {
            Pokemon pokemon = area.encounters.get(i).pokemon;
            if (area.encounters.get(i).formeNumber != 0) {
                pokemon = getAltFormeOfPokemon(pokemon, area.encounters.get(i).formeNumber);
            }
            if (pokemon.primaryType == primaryType || pokemon.secondaryType == primaryType) {
                primaryCount++;
            }
            if (pokemon.primaryType == secondaryType || pokemon.secondaryType == secondaryType) {
                secondaryCount++;
            }
        }
        return primaryCount > secondaryCount ? primaryType : secondaryType;
    }

    private boolean doesAnotherEncounterWithSamePokemonExistInArea(Encounter enc, EncounterSet area) {
        for (Encounter encounterToCheck : area.encounters) {
            if (enc != encounterToCheck && enc.pokemon == encounterToCheck.pokemon) {
                return true;
            }
        }
        return false;
    }

    private List<EncounterSet> collapseAreasORAS(List<EncounterSet> currentEncounters) {
        List<EncounterSet> output = new ArrayList<>();
        Map<Integer, List<EncounterSet>> zonesToEncounters = mapZonesToEncounters(currentEncounters);
        for (Integer zone : zonesToEncounters.keySet()) {
            List<EncounterSet> encountersInZone = zonesToEncounters.get(zone);
            int crashThreshold = computeDexNavCrashThreshold(encountersInZone);
            if (crashThreshold <= 18) {
                output.addAll(encountersInZone);
                continue;
            }

            // Naive Area 1-to-1 randomization will crash the game, so let's start collapsing areas to prevent this.
            // Start with combining all the fishing rod encounters, since it's a little less noticeable when they've
            // been collapsed.
            List<EncounterSet> collapsedEncounters = new ArrayList<>(encountersInZone);
            EncounterSet rodGroup = new EncounterSet();
            rodGroup.offset = zone;
            rodGroup.displayName = "Rod Group";
            for (EncounterSet area : encountersInZone) {
                if (area.displayName.contains("Old Rod") || area.displayName.contains("Good Rod") || area.displayName.contains("Super Rod")) {
                    collapsedEncounters.remove(area);
                    rodGroup.encounters.addAll(area.encounters);
                }
            }
            if (rodGroup.encounters.size() > 0) {
                collapsedEncounters.add(rodGroup);
            }
            crashThreshold = computeDexNavCrashThreshold(collapsedEncounters);
            if (crashThreshold <= 18) {
                output.addAll(collapsedEncounters);
                continue;
            }

            // Even after combining all the fishing rod encounters, we're still not below the threshold to prevent
            // DexNav from crashing the game. Combine all the grass encounters now to drop us below the threshold;
            // we've combined everything that DexNav normally combines, so at this point, we're *guaranteed* not
            // to crash the game.
            EncounterSet grassGroup = new EncounterSet();
            grassGroup.offset = zone;
            grassGroup.displayName = "Grass Group";
            for (EncounterSet area : encountersInZone) {
                if (area.displayName.contains("Grass/Cave") || area.displayName.contains("Long Grass") || area.displayName.contains("Horde")) {
                    collapsedEncounters.remove(area);
                    grassGroup.encounters.addAll(area.encounters);
                }
            }
            if (grassGroup.encounters.size() > 0) {
                collapsedEncounters.add(grassGroup);
            }

            output.addAll(collapsedEncounters);
        }
        return output;
    }

    private int computeDexNavCrashThreshold(List<EncounterSet> encountersInZone) {
        int crashThreshold = 0;
        for (EncounterSet area : encountersInZone) {
            if (area.displayName.contains("Rock Smash")) {
                continue; // Rock Smash Pokemon don't display on DexNav
            }
            Set<Pokemon> uniquePokemonInArea = new HashSet<>();
            for (Encounter enc : area.encounters) {
                if (enc.pokemon.baseForme != null) { // DexNav treats different forms as one Pokemon
                    uniquePokemonInArea.add(enc.pokemon.baseForme);
                } else {
                    uniquePokemonInArea.add(enc.pokemon);
                }
            }
            crashThreshold += uniquePokemonInArea.size();
        }
        return crashThreshold;
    }

    @Override
    public void randomizeTrainerPokes(boolean usePowerLevels, boolean noLegendaries, boolean noEarlyWonderGuard,
                                      int levelModifier, boolean distributionSetting, boolean mainPlaythroughSetting,
                                      boolean includeFormes, boolean swapMegaEvos, boolean shinyChance,
                                      boolean abilitiesAreRandomized) {
        checkPokemonRestrictions();
        List<Trainer> currentTrainers = this.getTrainers();
        // New: randomize the order trainers are randomized in.
        // Leads to less predictable results for various modifiers.
        // Need to keep the original ordering around for saving though.
        List<Trainer> scrambledTrainers = new ArrayList<>(currentTrainers);
        Collections.shuffle(scrambledTrainers, this.random);

        cachedReplacementLists = new TreeMap<>();
        cachedAllList = noLegendaries ? new ArrayList<>(noLegendaryList) : new ArrayList<>(
                mainPokemonList);
        if (includeFormes) {
            cachedAllList = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes) : new ArrayList<>(
                    mainPokemonListInclFormes);
        }
        cachedAllList =
                cachedAllList
                        .stream()
                        .filter(pk -> !pk.actuallyCosmetic)
                        .collect(Collectors.toList());
        List<Integer> mainPlaythroughTrainers = getMainPlaythroughTrainers();

        List<Pokemon> banned = new ArrayList<>();
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        cachedAllList.removeAll(banned);

        // Fully random is easy enough - randomize then worry about rival
        // carrying starter at the end
        for (Trainer t : scrambledTrainers) {
            applyLevelModifierToTrainerPokemon(t, levelModifier);
            if (t.tag != null && t.tag.equals("IRIVAL")) {
                // This is the first rival in Yellow. His Pokemon is used to determine the non-player
                // starter, so we can't change it here. Just skip it
                continue;
            }
            for (TrainerPokemon tp : t.pokemon) {
                boolean swapThisMegaEvo = swapMegaEvos && tp.canMegaEvolve();
                boolean wgAllowed = (!noEarlyWonderGuard) || tp.level >= 20;
                Pokemon newPK;
                Pokemon oldPK = tp.pokemon;
                if (tp.forme > 0) {
                    oldPK = getAltFormeOfPokemon(oldPK, tp.forme);
                }
                // new code for distribution with mainplaythrough balancing
                // what we do is check each trainer if they're part of the main playthrough
                // if so, add to placement history, if not, pure random
                if (distributionSetting && mainPlaythroughSetting) {
                    // new code for main playthrough distribution
                    if (mainPlaythroughTrainers.contains(t.offset)) { // this determines if this trainer is in the pool of main playthrough

                        newPK =
                                pickReplacement(
                                        oldPK,
                                        usePowerLevels,
                                        null,
                                        noLegendaries,
                                        wgAllowed,
                                        true,
                                        swapThisMegaEvo,
                                        abilitiesAreRandomized,
                                        includeFormes
                                );
                        setPlacementHistory(newPK);
                        tp.absolutePokeNumber = newPK.number;
                        tp.pokemon = newPK;
                        setFormeForTrainerPokemon(tp, newPK);
                    }
                    else { // pure random when trainer not in pool
                        newPK =
                                pickReplacement(
                                        oldPK,
                                        usePowerLevels,
                                        null,
                                        noLegendaries,
                                        wgAllowed,
                                        false,
                                        swapThisMegaEvo,
                                        abilitiesAreRandomized,
                                        includeFormes
                                );
                        tp.absolutePokeNumber = newPK.number;
                        tp.pokemon = newPK;
                        setFormeForTrainerPokemon(tp, newPK);
                    }
                } else {
                    newPK =
                            pickReplacement(
                                    oldPK,
                                    usePowerLevels,
                                    null,
                                    noLegendaries,
                                    wgAllowed,
                                    distributionSetting,
                                    swapThisMegaEvo,
                                    abilitiesAreRandomized,
                                    includeFormes
                            );
                    if (distributionSetting) {
                        setPlacementHistory(newPK);
                    }
                    tp.absolutePokeNumber = newPK.number;
                    tp.pokemon = newPK;
                    setFormeForTrainerPokemon(tp, newPK);
                }

                if (swapThisMegaEvo) {
                    tp.heldItem = newPK
                            .megaEvolutionsFrom
                            .get(this.random.nextInt(newPK.megaEvolutionsFrom.size()))
                            .argument;
                }
                tp.resetMoves = true;
                if (shinyChance) {
                    if (this.random.nextInt(256) == 0) {
                        tp.IVs |= (1 << 30);
                    }
                }
            }
        }

        // Save it all up
        this.setTrainers(currentTrainers, false);
    }

    private void setFormeForTrainerPokemon(TrainerPokemon tp, Pokemon pk) {
        boolean checkCosmetics = true;
        tp.formeSuffix = "";
        tp.forme = 0;
        if (pk.formeNumber > 0) {
            tp.forme = pk.formeNumber;
            tp.formeSuffix = pk.formeSuffix;
            tp.pokemon = pk.baseForme;
            checkCosmetics = false;
        }
        if (checkCosmetics && tp.pokemon.cosmeticForms > 0) {
            tp.forme = tp.pokemon.getCosmeticFormNumber(this.random.nextInt(tp.pokemon.cosmeticForms));
        } else if (!checkCosmetics && pk.cosmeticForms > 0) {
            tp.forme += pk.getCosmeticFormNumber(this.random.nextInt(pk.cosmeticForms));
        }
    }

    private void applyLevelModifierToTrainerPokemon(Trainer trainer, int levelModifier) {
        if (levelModifier != 0) {
            for (TrainerPokemon tp : trainer.pokemon) {
                tp.level = Math.min(100, (int) Math.round(tp.level * (1 + levelModifier / 100.0)));
            }
        }
    }

    @Override
    public void typeThemeTrainerPokes(boolean usePowerLevels, boolean weightByFrequency, boolean noLegendaries,
                                      boolean noEarlyWonderGuard, int levelModifier, boolean includeFormes,
                                      boolean swapMegaEvos, boolean shinyChance, boolean abilitiesAreRandomized) {
        checkPokemonRestrictions();
        List<Trainer> currentTrainers = this.getTrainers();
        cachedReplacementLists = new TreeMap<>();
        cachedAllList = noLegendaries ? new ArrayList<>(noLegendaryList) : new ArrayList<>(
                mainPokemonList);
        if (includeFormes) {
            if (noLegendaries) {
                cachedAllList.addAll(noLegendaryAltsList);
            } else {
                cachedAllList.addAll(altFormesList);
            }
        }
        cachedAllList =
                cachedAllList
                        .stream()
                        .filter(pk -> !pk.actuallyCosmetic)
                        .collect(Collectors.toList());
        typeWeightings = new TreeMap<>();
        totalTypeWeighting = 0;

        List<Pokemon> banned = new ArrayList<>();
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        cachedAllList.removeAll(banned);

        // Construct groupings for types
        // Anything starting with GYM or ELITE or CHAMPION is a group
        Set<Trainer> assignedTrainers = new TreeSet<>();
        Map<String, List<Trainer>> groups = new TreeMap<>();
        for (Trainer t : currentTrainers) {
            applyLevelModifierToTrainerPokemon(t, levelModifier);
            if (t.tag != null && t.tag.equals("IRIVAL")) {
                // This is the first rival in Yellow. His Pokemon is used to determine the non-player
                // starter, so we can't change it here. Just skip it.
                continue;
            }
            String group = t.tag == null ? "" : t.tag;
            if (group.contains("-")) {
                group = group.substring(0, group.indexOf('-'));
            }
            if (group.startsWith("GYM") || group.startsWith("ELITE") || group.startsWith("CHAMPION")
                    || group.startsWith("THEMED")) {
                // Yep this is a group
                if (!groups.containsKey(group)) {
                    groups.put(group, new ArrayList<>());
                }
                groups.get(group).add(t);
                assignedTrainers.add(t);
            } else if (group.startsWith("GIO")) {
                // Giovanni has same grouping as his gym, gym 8
                if (!groups.containsKey("GYM8")) {
                    groups.put("GYM8", new ArrayList<>());
                }
                groups.get("GYM8").add(t);
                assignedTrainers.add(t);
            }
        }

        // Give a type to each group
        // Gym & elite types have to be unique
        // So do uber types, including the type we pick for champion
        Set<Type> usedGymTypes = new TreeSet<>();
        Set<Type> usedEliteTypes = new TreeSet<>();
        Set<Type> usedUberTypes = new TreeSet<>();
        for (String group : groups.keySet()) {
            List<Trainer> trainersInGroup = groups.get(group);
            // Shuffle ordering within group to promote randomness
            Collections.shuffle(trainersInGroup, random);
            Type typeForGroup = pickType(weightByFrequency, noLegendaries, includeFormes);
            if (group.startsWith("GYM")) {
                while (usedGymTypes.contains(typeForGroup)) {
                    typeForGroup = pickType(weightByFrequency, noLegendaries, includeFormes);
                }
                usedGymTypes.add(typeForGroup);
            }
            if (group.startsWith("ELITE")) {
                while (usedEliteTypes.contains(typeForGroup)) {
                    typeForGroup = pickType(weightByFrequency, noLegendaries, includeFormes);
                }
                usedEliteTypes.add(typeForGroup);
            }
            if (group.equals("CHAMPION")) {
                usedUberTypes.add(typeForGroup);
            }
            // Themed groups just have a theme, no special criteria
            for (Trainer t : trainersInGroup) {
                for (TrainerPokemon tp : t.pokemon) {
                    boolean swapThisMegaEvo = swapMegaEvos && tp.canMegaEvolve();
                    boolean wgAllowed = (!noEarlyWonderGuard) || tp.level >= 20;
                    Pokemon oldPK = tp.pokemon;
                    if (tp.forme > 0) {
                        oldPK = getAltFormeOfPokemon(oldPK, tp.forme);
                    }
                    Pokemon newPK =
                            pickReplacement(
                                    oldPK,
                                    usePowerLevels,
                                    typeForGroup,
                                    noLegendaries,
                                    wgAllowed,
                                    false,
                                    swapThisMegaEvo,
                                    abilitiesAreRandomized,
                                    includeFormes
                            );
                    tp.absolutePokeNumber = newPK.number;
                    tp.pokemon = newPK;
                    setFormeForTrainerPokemon(tp, newPK);

                    if (swapThisMegaEvo) {
                        tp.heldItem = newPK
                                .megaEvolutionsFrom
                                .get(this.random.nextInt(newPK.megaEvolutionsFrom.size()))
                                .argument;
                    }

                    tp.resetMoves = true;
                    if (shinyChance) {
                        if (this.random.nextInt(256) == 0) {
                            tp.IVs |= (1 << 30);
                        }
                    }
                }
            }
        }

        // New: randomize the order trainers are randomized in.
        // Leads to less predictable results for various modifiers.
        // Need to keep the original ordering around for saving though.
        List<Trainer> scrambledTrainers = new ArrayList<>(currentTrainers);
        Collections.shuffle(scrambledTrainers, this.random);

        // Give a type to each unassigned trainer
        for (Trainer t : scrambledTrainers) {
            if (t.tag != null && t.tag.equals("IRIVAL")) {
                continue; // skip
            }

            if (!assignedTrainers.contains(t)) {
                Type typeForTrainer = pickType(weightByFrequency, noLegendaries, includeFormes);
                // Ubers: can't have the same type as each other
                if (t.tag != null && t.tag.equals("UBER")) {
                    while (usedUberTypes.contains(typeForTrainer)) {
                        typeForTrainer = pickType(weightByFrequency, noLegendaries, includeFormes);
                    }
                    usedUberTypes.add(typeForTrainer);
                }
                for (TrainerPokemon tp : t.pokemon) {
                    boolean swapThisMegaEvo = swapMegaEvos && tp.canMegaEvolve();
                    boolean shedAllowed = (!noEarlyWonderGuard) || tp.level >= 20;
                    Pokemon oldPK = tp.pokemon;
                    if (tp.forme > 0) {
                        oldPK = getAltFormeOfPokemon(oldPK, tp.forme);
                    }
                    Pokemon newPK =
                            pickReplacement(
                                    oldPK,
                                    usePowerLevels,
                                    typeForTrainer,
                                    noLegendaries,
                                    shedAllowed,
                                    false,
                                    swapThisMegaEvo,
                                    abilitiesAreRandomized,
                                    includeFormes
                            );
                    tp.absolutePokeNumber = newPK.number;
                    tp.pokemon = newPK;
                    setFormeForTrainerPokemon(tp, newPK);

                    if (swapThisMegaEvo) {
                        tp.heldItem = newPK
                                        .megaEvolutionsFrom
                                        .get(this.random.nextInt(newPK.megaEvolutionsFrom.size()))
                                        .argument;
                    }

                    tp.resetMoves = true;
                    if (shinyChance) {
                        if (this.random.nextInt(256) == 0) {
                            tp.IVs |= (1 << 30);
                        }
                    }
                }
            }
        }

        // Save it all up
        this.setTrainers(currentTrainers, false);
    }

    @Override
    public void rivalCarriesStarter() {
        checkPokemonRestrictions();
        List<Trainer> currentTrainers = this.getTrainers();
        rivalCarriesStarterUpdate(currentTrainers, "RIVAL", isORAS ? 0 : 1);
        rivalCarriesStarterUpdate(currentTrainers, "FRIEND", 2);
        this.setTrainers(currentTrainers, false);
    }

    @Override
    public void forceFullyEvolvedTrainerPokes(int minLevel) {
        checkPokemonRestrictions();
        List<Trainer> currentTrainers = this.getTrainers();
        for (Trainer t : currentTrainers) {
            for (TrainerPokemon tp : t.pokemon) {
                if (tp.level >= minLevel) {
                    Pokemon newPokemon = fullyEvolve(tp.pokemon);
                    if (newPokemon != tp.pokemon) {
                        tp.pokemon = newPokemon;
                        tp.absolutePokeNumber = newPokemon.number;
                        setFormeForTrainerPokemon(tp, newPokemon);
                        tp.resetMoves = true;
                    }
                }
            }
        }
        this.setTrainers(currentTrainers, false);
    }

    @Override
    public void onlyChangeTrainerLevels(int levelModifier) {
        List<Trainer> currentTrainers = this.getTrainers();
        for (Trainer t: currentTrainers) {
            applyLevelModifierToTrainerPokemon(t, levelModifier);
        }
        this.setTrainers(currentTrainers, false);
    }

    @Override
    public void addTrainerPokemon(int additionalNormal, int additionalImportant, int additionalBoss) {

        if (additionalNormal == 0 && additionalImportant == 0 && additionalBoss == 0) {
            return;
        }

        List<Trainer> currentTrainers = this.getTrainers();
        for (Trainer t: currentTrainers) {
            int additional;
            if (t.isBoss()) {
                additional = additionalBoss;
            } else if (t.isImportant()) {
                if (t.skipImportant()) continue;
                additional = additionalImportant;
            } else {
                additional = additionalNormal;
            }

            if (additional == 0) {
                continue;
            }

            int lowest = 100;
            List<TrainerPokemon> potentialPokes = new ArrayList<>();

            // First pass: find lowest level
            for (TrainerPokemon tpk: t.pokemon) {
                if (tpk.level < lowest) {
                    lowest = tpk.level;
                }
            }

            // Second pass: find all Pokemon at lowest level
            for (TrainerPokemon tpk: t.pokemon) {
                if (tpk.level == lowest) {
                    potentialPokes.add(tpk);
                }
            }

            // If a trainer can appear in a Multi Battle (i.e., a Double Battle where the enemy consists
            // of two independent trainers), we want to be aware of that so we don't give them a team of
            // six Pokemon and have a 6v12 battle
            int maxPokemon = t.couldBeMultiBattle ? 3 : 6;
            for (int i = 0; i < additional; i++) {
                if (t.pokemon.size() == maxPokemon) break;

                // We want to preserve the original last Pokemon because the order is sometimes used to
                // determine the rival's starter
                int secondToLastIndex = t.pokemon.size() - 1;
                TrainerPokemon newPokemon = potentialPokes.get(i % potentialPokes.size()).copy();

                // Clear out the held item because we only want one Pokemon with a mega stone if we're
                // swapping mega evolvables
                newPokemon.heldItem = 0;
                t.pokemon.add(secondToLastIndex, newPokemon);
            }
        }
        this.setTrainers(currentTrainers, false);
    }

    @Override
    public void doubleBattleMode() {

        List<Trainer> currentTrainers = this.getTrainers();
        for (Trainer t: currentTrainers) {

            if (generationOfPokemon() == 4 || generationOfPokemon() == 5) {
                if (!t.isBoss() && !t.isImportant()) {
                    continue;
                }
            }

            if (t.pokemon.size() != 1 || t.couldBeMultiBattle || (t.tag != null && (t.tag.startsWith("RIVAL1-") || t.tag.startsWith("FRIEND1-") || t.tag.endsWith("NOTSTRONG")))) {
                continue;
            }


            t.pokemon.add(t.pokemon.get(0).copy());
        }
        this.setTrainers(currentTrainers, true);
    }

    // MOVE DATA
    // All randomizers don't touch move ID 165 (Struggle)
    // They also have other exclusions where necessary to stop things glitching.

    @Override
    public void randomizeMovePowers() {
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != 165 && mv.power >= 10) {
                // "Generic" damaging move to randomize power
                if (random.nextInt(3) != 2) {
                    // "Regular" move
                    mv.power = random.nextInt(11) * 5 + 50; // 50 ... 100
                } else {
                    // "Extreme" move
                    mv.power = random.nextInt(27) * 5 + 20; // 20 ... 150
                }
                // Tiny chance for massive power jumps
                for (int i = 0; i < 2; i++) {
                    if (random.nextInt(100) == 0) {
                        mv.power += 50;
                    }
                }

                if (mv.hitCount != 1) {
                    // Divide randomized power by average hit count, round to
                    // nearest 5
                    mv.power = (int) (Math.round(mv.power / mv.hitCount / 5) * 5);
                    if (mv.power == 0) {
                        mv.power = 5;
                    }
                }
            }
        }
    }

    @Override
    public void randomizeMovePPs() {
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != 165) {
                if (random.nextInt(3) != 2) {
                    // "average" PP: 15-25
                    mv.pp = random.nextInt(3) * 5 + 15;
                } else {
                    // "extreme" PP: 5-40
                    mv.pp = random.nextInt(8) * 5 + 5;
                }
            }
        }
    }

    @Override
    public void randomizeMoveAccuracies() {
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != 165 && mv.hitratio >= 5) {
                // "Sane" accuracy randomization
                // Broken into three tiers based on original accuracy
                // Designed to limit the chances of 100% accurate OHKO moves and
                // keep a decent base of 100% accurate regular moves.

                if (mv.hitratio <= 50) {
                    // lowest tier (acc <= 50)
                    // new accuracy = rand(20...50) inclusive
                    // with a 10% chance to increase by 50%
                    mv.hitratio = random.nextInt(7) * 5 + 20;
                    if (random.nextInt(10) == 0) {
                        mv.hitratio = (mv.hitratio * 3 / 2) / 5 * 5;
                    }
                } else if (mv.hitratio < 90) {
                    // middle tier (50 < acc < 90)
                    // count down from 100% to 20% in 5% increments with 20%
                    // chance to "stop" and use the current accuracy at each
                    // increment
                    // gives decent-but-not-100% accuracy most of the time
                    mv.hitratio = 100;
                    while (mv.hitratio > 20) {
                        if (random.nextInt(10) < 2) {
                            break;
                        }
                        mv.hitratio -= 5;
                    }
                } else {
                    // highest tier (90 <= acc <= 100)
                    // count down from 100% to 20% in 5% increments with 40%
                    // chance to "stop" and use the current accuracy at each
                    // increment
                    // gives high accuracy most of the time
                    mv.hitratio = 100;
                    while (mv.hitratio > 20) {
                        if (random.nextInt(10) < 4) {
                            break;
                        }
                        mv.hitratio -= 5;
                    }
                }
            }
        }
    }

    @Override
    public void randomizeMoveTypes() {
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != 165 && mv.type != null) {
                mv.type = randomType();
            }
        }
    }

    @Override
    public void randomizeMoveCategory() {
        if (!this.hasPhysicalSpecialSplit()) {
            return;
        }
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != 165 && mv.category != MoveCategory.STATUS) {
                if (random.nextInt(2) == 0) {
                    mv.category = (mv.category == MoveCategory.PHYSICAL) ? MoveCategory.SPECIAL : MoveCategory.PHYSICAL;
                }
            }
        }

    }

    @Override
    public void updateMoves(int generation) {
        List<Move> moves = this.getMoves();

        // gen1
        // Karate Chop => FIGHTING (gen1)
        updateMoveType(moves, 2, Type.FIGHTING);
        // Gust => FLYING (gen1)
        updateMoveType(moves, 16, Type.FLYING);
        // Wing Attack => 60 power (gen1)
        updateMovePower(moves, 17, 60);
        // Whirlwind => 100 accuracy (gen1)
        updateMoveAccuracy(moves, 18, 100);
        // Sand Attack => GROUND (gen1)
        updateMoveType(moves, 28, Type.GROUND);
        // Double-Edge => 120 power (gen1)
        updateMovePower(moves, 38, 120);
        // Move 44, Bite, becomes dark (but doesn't exist anyway)
        // Blizzard => 70% accuracy (gen1)
        updateMoveAccuracy(moves, 59, 70);
        // Rock Throw => 90% accuracy (gen1)
        updateMoveAccuracy(moves, 88, 90);
        // Hypnosis => 60% accuracy (gen1)
        updateMoveAccuracy(moves, 95, 60);
        // SelfDestruct => 200power (gen1)
        updateMovePower(moves, 120, 200);
        // Explosion => 250 power (gen1)
        updateMovePower(moves, 153, 250);
        // Dig => 60 power (gen1)
        updateMovePower(moves, 91, 60);

        if (generation >= 3 && generationOfPokemon() < 3) {
            // Razor Wind => 100% accuracy (gen1/2)
            updateMoveAccuracy(moves, 13, 100);
            // Move 67, Low Kick, has weight-based power in gen3+
            // Low Kick => 100% accuracy (gen1)
            updateMoveAccuracy(moves, 67, 100);
        }

        if (generation >= 4 && generationOfPokemon() < 4) {
            // Fly => 90 power (gen1/2/3)
            updateMovePower(moves, 19, 90);
            // Vine Whip => 15 pp (gen1/2/3)
            updateMovePP(moves, 22, 15);
            // Absorb => 25pp (gen1/2/3)
            updateMovePP(moves, 71, 25);
            // Mega Drain => 15pp (gen1/2/3)
            updateMovePP(moves, 72, 15);
            // Dig => 80 power (gen1/2/3)
            updateMovePower(moves, 91, 80);
            // Recover => 10pp (gen1/2/3)
            updateMovePP(moves, 105, 10);
            // Flash => 100% acc (gen1/2/3)
            updateMoveAccuracy(moves, 148, 100);
            // Petal Dance => 90 power (gen1/2/3)
            updateMovePower(moves, 80, 90);
            // Disable => 100% accuracy (gen1-4)
            updateMoveAccuracy(moves, 50, 80);
            // Jump Kick => 85 power
            updateMovePower(moves, 26, 85);
            // Hi Jump Kick => 100 power
            updateMovePower(moves, 136, 100);

            if (generationOfPokemon() >= 2) {
                // Zap Cannon => 120 power (gen2-3)
                updateMovePower(moves, 192, 120);
                // Outrage => 120 power (gen2-3)
                updateMovePower(moves, 200, 120);
                updateMovePP(moves, 200, 10);
                // Giga Drain => 10pp (gen2-3)
                updateMovePP(moves, 202, 10);
                // Rock Smash => 40 power (gen2-3)
                updateMovePower(moves, 249, 40);
            }
        }

        if (generation >= 5 && generationOfPokemon() < 5) {
            // Bind => 85% accuracy (gen1-4)
            updateMoveAccuracy(moves, 20, 85);
            // Jump Kick => 10 pp, 100 power (gen1-4)
            updateMovePP(moves, 26, 10);
            updateMovePower(moves, 26, 100);
            // Tackle => 50 power, 100% accuracy , gen1-4
            updateMovePower(moves, 33, 50);
            updateMoveAccuracy(moves, 33, 100);
            // Wrap => 90% accuracy (gen1-4)
            updateMoveAccuracy(moves, 35, 90);
            // Thrash => 120 power, 10pp (gen1-4)
            updateMovePP(moves, 37, 10);
            updateMovePower(moves, 37, 120);
            // Disable => 100% accuracy (gen1-4)
            updateMoveAccuracy(moves, 50, 100);
            // Petal Dance => 120power, 10pp (gen1-4)
            updateMovePP(moves, 80, 10);
            updateMovePower(moves, 80, 120);
            // Fire Spin => 35 power, 85% acc (gen1-4)
            updateMoveAccuracy(moves, 83, 85);
            updateMovePower(moves, 83, 35);
            // Toxic => 90% accuracy (gen1-4)
            updateMoveAccuracy(moves, 92, 90);
            // Clamp => 15pp, 85% acc (gen1-4)
            updateMoveAccuracy(moves, 128, 85);
            updateMovePP(moves, 128, 15);
            // HJKick => 130 power, 10pp (gen1-4)
            updateMovePP(moves, 136, 10);
            updateMovePower(moves, 136, 130);
            // Glare => 90% acc (gen1-4)
            updateMoveAccuracy(moves, 137, 90);
            // Poison Gas => 80% acc (gen1-4)
            updateMoveAccuracy(moves, 139, 80);
            // Crabhammer => 90% acc (gen1-4)
            updateMoveAccuracy(moves, 152, 90);

            if (generationOfPokemon() >= 2) {
                // Curse => GHOST (gen2-4)
                updateMoveType(moves, 174, Type.GHOST);
                // Cotton Spore => 100% acc (gen2-4)
                updateMoveAccuracy(moves, 178, 100);
                // Scary Face => 100% acc (gen2-4)
                updateMoveAccuracy(moves, 184, 100);
                // Bone Rush => 90% acc (gen2-4)
                updateMoveAccuracy(moves, 198, 90);
                // Giga Drain => 75 power (gen2-4)
                updateMovePower(moves, 202, 75);
                // Fury Cutter => 20 power (gen2-4)
                updateMovePower(moves, 210, 20);
                // Future Sight => 10 pp, 100 power, 100% acc (gen2-4)
                updateMovePP(moves, 248, 10);
                updateMovePower(moves, 248, 100);
                updateMoveAccuracy(moves, 248, 100);
                // Whirlpool => 35 pow, 85% acc (gen2-4)
                updateMovePower(moves, 250, 35);
                updateMoveAccuracy(moves, 250, 85);
            }

            if (generationOfPokemon() >= 3) {
                // Uproar => 90 power (gen3-4)
                updateMovePower(moves, 253, 90);
                updateMovePP(moves, 254, 20);
                updateMovePower(moves, 291, 80);
                // Sand Tomb => 35 pow, 85% acc (gen3-4)
                updateMovePower(moves, 328, 35);
                updateMoveAccuracy(moves, 328, 85);
                // Bullet Seed => 25 power (gen3-4)
                updateMovePower(moves, 331, 25);
                // Icicle Spear => 25 power (gen3-4)
                updateMovePower(moves, 333, 25);
                // Covet => 60 power (gen3-4)
                updateMovePower(moves, 343, 60);
                updateMovePower(moves, 348, 90);
                // Rock Blast => 90% acc (gen3-4)
                updateMoveAccuracy(moves, 350, 90);
                // Doom Desire => 140 pow, 100% acc, gen3-4
                updateMovePower(moves, 353, 140);
                updateMoveAccuracy(moves, 353, 100);
            }

            if (generationOfPokemon() == 4) {
                // Feint => 30 pow
                updateMovePower(moves, 364, 30);
                // Last Resort => 140 pow
                updateMovePower(moves, 387, 140);
                // Drain Punch => 10 pp, 75 pow
                updateMovePP(moves, 409, 10);
                updateMovePower(moves, 409, 75);
                // Magma Storm => 75% acc
                updateMoveAccuracy(moves, 463, 75);
            }
        }

        if (generation >= 6 && generationOfPokemon() < 6) {
            // gen 1
            // Swords Dance 20 PP
            updateMovePP(moves, 14, 20);
            // Whirlwind can't miss
            updateMoveAccuracy(moves, 18, perfectAccuracy);
            // Vine Whip 25 PP, 45 Power
            updateMovePP(moves, 22, 25);
            updateMovePower(moves, 22, 45);
            // Pin Missile 25 Power, 95% Accuracy
            updateMovePower(moves, 42, 25);
            updateMoveAccuracy(moves, 42, 95);
            // Flamethrower 90 Power
            updateMovePower(moves, 53, 90);
            // Hydro Pump 110 Power
            updateMovePower(moves, 56, 110);
            // Surf 90 Power
            updateMovePower(moves, 57, 90);
            // Ice Beam 90 Power
            updateMovePower(moves, 58, 90);
            // Blizzard 110 Power
            updateMovePower(moves, 59, 110);
            // Growth 20 PP
            updateMovePP(moves, 74, 20);
            // Thunderbolt 90 Power
            updateMovePower(moves, 85, 90);
            // Thunder 110 Power
            updateMovePower(moves, 87, 110);
            // Minimize 10 PP
            updateMovePP(moves, 107, 10);
            // Barrier 20 PP
            updateMovePP(moves, 112, 20);
            // Lick 30 Power
            updateMovePower(moves, 122, 30);
            // Smog 30 Power
            updateMovePower(moves, 123, 30);
            // Fire Blast 110 Power
            updateMovePower(moves, 126, 110);
            // Skull Bash 10 PP, 130 Power
            updateMovePP(moves, 130, 10);
            updateMovePower(moves, 130, 130);
            // Glare 100% Accuracy
            updateMoveAccuracy(moves, 137, 100);
            // Poison Gas 90% Accuracy
            updateMoveAccuracy(moves, 139, 90);
            // Bubble 40 Power
            updateMovePower(moves, 145, 40);
            // Psywave 100% Accuracy
            updateMoveAccuracy(moves, 149, 100);
            // Acid Armor 20 PP
            updateMovePP(moves, 151, 20);
            // Crabhammer 100 Power
            updateMovePower(moves, 152, 100);

            if (generationOfPokemon() >= 2) {
                // Thief 25 PP, 60 Power
                updateMovePP(moves, 168, 25);
                updateMovePower(moves, 168, 60);
                // Snore 50 Power
                updateMovePower(moves, 173, 50);
                // Fury Cutter 40 Power
                updateMovePower(moves, 210, 40);
                // Future Sight 120 Power
                updateMovePower(moves, 248, 120);
            }

            if (generationOfPokemon() >= 3) {
                // Heat Wave 95 Power
                updateMovePower(moves, 257, 95);
                // Will-o-Wisp 85% Accuracy
                updateMoveAccuracy(moves, 261, 85);
                // Smellingsalt 70 Power
                updateMovePower(moves, 265, 70);
                // Knock off 65 Power
                updateMovePower(moves, 282, 65);
                // Meteor Mash 90 Power, 90% Accuracy
                updateMovePower(moves, 309, 90);
                updateMoveAccuracy(moves, 309, 90);
                // Air Cutter 60 Power
                updateMovePower(moves, 314, 60);
                // Overheat 130 Power
                updateMovePower(moves, 315, 130);
                // Rock Tomb 15 PP, 60 Power, 95% Accuracy
                updateMovePP(moves, 317, 15);
                updateMovePower(moves, 317, 60);
                updateMoveAccuracy(moves, 317, 95);
                // Extrasensory 20 PP
                updateMovePP(moves, 326, 20);
                // Muddy Water 90 Power
                updateMovePower(moves, 330, 90);
                // Covet 25 PP
                updateMovePP(moves, 343, 25);
            }

            if (generationOfPokemon() >= 4) {
                // Wake-Up Slap 70 Power
                updateMovePower(moves, 358, 70);
                // Tailwind 15 PP
                updateMovePP(moves, 366, 15);
                // Assurance 60 Power
                updateMovePower(moves, 372, 60);
                // Psycho Shift 100% Accuracy
                updateMoveAccuracy(moves, 375, 100);
                // Aura Sphere 80 Power
                updateMovePower(moves, 396, 80);
                // Air Slash 15 PP
                updateMovePP(moves, 403, 15);
                // Dragon Pulse 85 Power
                updateMovePower(moves, 406, 85);
                // Power Gem 80 Power
                updateMovePower(moves, 408, 80);
                // Energy Ball 90 Power
                updateMovePower(moves, 412, 90);
                // Draco Meteor 130 Power
                updateMovePower(moves, 434, 130);
                // Leaf Storm 130 Power
                updateMovePower(moves, 437, 130);
                // Gunk Shot 80% Accuracy
                updateMoveAccuracy(moves, 441, 80);
                // Chatter 65 Power
                updateMovePower(moves, 448, 65);
                // Magma Storm 100 Power
                updateMovePower(moves, 463, 100);
            }

            if (generationOfPokemon() == 5) {
                // Storm Throw 60 Power
                updateMovePower(moves, 480, 60);
                // Synchronoise 120 Power
                updateMovePower(moves, 485, 120);
                // Low Sweep 65 Power
                updateMovePower(moves, 490, 65);
                // Hex 65 Power
                updateMovePower(moves, 506, 65);
                // Incinerate 60 Power
                updateMovePower(moves, 510, 60);
                // Pledges 80 Power
                updateMovePower(moves, 518, 80);
                updateMovePower(moves, 519, 80);
                updateMovePower(moves, 520, 80);
                // Struggle Bug 50 Power
                updateMovePower(moves, 522, 50);
                // Frost Breath 45 Power
                // crits are 2x in these games
                updateMovePower(moves, 524, 45);
                // Sacred Sword 15 PP
                updateMovePP(moves, 533, 15);
                // Hurricane 110 Power
                updateMovePower(moves, 542, 110);
                // Techno Blast 120 Power
                updateMovePower(moves, 546, 120);
            }
        }

        if (generation >= 7 && generationOfPokemon() < 7) {

            // Leech Life 80 Power, 10 PP
            updateMovePower(moves, 141, 80);
            updateMovePP(moves, 141, 10);
            // Submission 20 PP
            updateMovePP(moves, 66, 20);
            // Tackle 40 Power
            updateMovePower(moves, 33, 40);
            // Thunder Wave 90% Accuracy
            updateMoveAccuracy(moves, 86, 90);

            if (generationOfPokemon() >= 2) {
                // Swagger 85% Accuracy
                updateMoveAccuracy(moves, 207, 85);
            }

            if (generationOfPokemon() >= 3) {
                // Knock Off 20 PP
                updateMovePP(moves, 282, 20);
            }

            if (generationOfPokemon() >= 4) {
                // Dark Void 50% Accuracy
                updateMoveAccuracy(moves, 464, 50);
                // Sucker Punch 70 Power
                updateMovePower(moves, 389, 70);
            }

            if (generationOfPokemon() == 6) {
                // Aromatic Mist can't miss
                updateMoveAccuracy(moves, 597, perfectAccuracy);
                // Fell Stinger 50 Power
                updateMovePower(moves, 565, 50);
                // Flying Press 100 Power
                updateMovePower(moves, 560, 100);
                // Mat Block 10 PP
                updateMovePP(moves, 561, 10);
                // Mystical Fire 75 Power
                updateMovePower(moves, 595, 75);
                // Parabolic Charge 65 Power
                updateMovePower(moves, 570, 65);
                // Topsy-Turvy can't miss
                updateMoveAccuracy(moves, 576, perfectAccuracy);
                // Water Shuriken Special
                updateMoveCategory(moves, 594, MoveCategory.SPECIAL);
            }
        }

        if (generation >= 8 && generationOfPokemon() < 8) {
            if (generationOfPokemon() >= 2) {
                // Rapid Spin 50 Power
                updateMovePower(moves, 229, 50);
            }

            if (generationOfPokemon() == 7) {
                // Multi-Attack 120 Power
                updateMovePower(moves, 718, 120);
            }
        }
    }

    private Map<Integer, boolean[]> moveUpdates;

    @Override
    public void initMoveUpdates() {
        moveUpdates = new TreeMap<>();
    }

    @Override
    public void printMoveUpdates() {
        log("--Move Updates--");
        List<Move> moves = this.getMoves();
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
            log(logStr);
        }
        logBlankLine();
    }

    @Override
    public void randomizeMovesLearnt(boolean typeThemed, boolean noBroken, boolean forceStartingMoves,
                                     int forceStartingMoveCount, double goodDamagingProbability,
                                     boolean evolutionMovesForAll) {
        // Get current sets
        Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();
        List<Integer> hms = this.getHMMoves();
        List<Move> allMoves = this.getMoves();

        @SuppressWarnings("unchecked")
        Set<Integer> allBanned = new HashSet<Integer>(noBroken ? this.getGameBreakingMoves() : Collections.EMPTY_SET);
        allBanned.addAll(hms);
        allBanned.addAll(this.getMovesBannedFromLevelup());
        allBanned.addAll(GlobalConstants.zMoves);

        // Build sets of moves
        List<Move> validMoves = new ArrayList<>();
        List<Move> validDamagingMoves = new ArrayList<>();
        Map<Type, List<Move>> validTypeMoves = new HashMap<>();
        Map<Type, List<Move>> validTypeDamagingMoves = new HashMap<>();

        for (Move mv : allMoves) {
            if (mv != null && !GlobalConstants.bannedRandomMoves[mv.number] && !allBanned.contains(mv.number)) {
                validMoves.add(mv);
                if (mv.type != null) {
                    if (!validTypeMoves.containsKey(mv.type)) {
                        validTypeMoves.put(mv.type, new ArrayList<>());
                    }
                    validTypeMoves.get(mv.type).add(mv);
                }

                if (!GlobalConstants.bannedForDamagingMove[mv.number]) {
                    if ((mv.power * mv.hitCount) >= 2 * GlobalConstants.MIN_DAMAGING_MOVE_POWER
                            || ((mv.power * mv.hitCount) >= GlobalConstants.MIN_DAMAGING_MOVE_POWER && mv.hitratio >= 90)) {
                        validDamagingMoves.add(mv);
                        if (mv.type != null) {
                            if (!validTypeDamagingMoves.containsKey(mv.type)) {
                                validTypeDamagingMoves.put(mv.type, new ArrayList<>());
                            }
                            validTypeDamagingMoves.get(mv.type).add(mv);
                        }
                    }
                }
            }
        }

        for (Integer pkmnNum : movesets.keySet()) {
            Set<Integer> learnt = new TreeSet<>();
            List<MoveLearnt> moves = movesets.get(pkmnNum);
            Pokemon pkmn = findPokemonInPoolWithSpeciesID(mainPokemonListInclFormes, pkmnNum);
            if (pkmn == null) {
                continue;
            }

            // 4 starting moves?
            if (forceStartingMoves) {
                int lv1count = 0;
                for (MoveLearnt ml : moves) {
                    if (ml.level == 1) {
                        lv1count++;
                    }
                }
                if (lv1count < forceStartingMoveCount) {
                    for (int i = 0; i < forceStartingMoveCount - lv1count; i++) {
                        MoveLearnt fakeLv1 = new MoveLearnt();
                        fakeLv1.level = 1;
                        fakeLv1.move = 0;
                        moves.add(0, fakeLv1);
                    }
                }
            }

            if (evolutionMovesForAll) {
                if (moves.get(0).level != 0) {
                    MoveLearnt fakeEvoMove = new MoveLearnt();
                    fakeEvoMove.level = 0;
                    fakeEvoMove.move = 0;
                    moves.add(0, fakeEvoMove);
                }
            }

            if (pkmn.actuallyCosmetic) {
                for (int i = 0; i < moves.size(); i++) {
                    moves.get(i).move = movesets.get(pkmn.baseForme.number).get(i).move;
                }
                continue;
            }

            // Find last lv1 move
            // lv1index ends up as the index of the first non-lv1 move
            int lv1index = moves.get(0).level == 1 ? 0 : 1; // Evolution move handling (level 0 = evo move)
            while (lv1index < moves.size() && moves.get(lv1index).level == 1) {
                lv1index++;
            }

            // last lv1 move is 1 before lv1index
            if (lv1index != 0) {
                lv1index--;
            }

            // Replace moves as needed
            for (int i = 0; i < moves.size(); i++) {
                // should this move be forced damaging?
                boolean attemptDamaging = i == lv1index || random.nextDouble() < goodDamagingProbability;

                // type themed?
                Type typeOfMove = null;
                if (typeThemed) {
                    double picked = random.nextDouble();
                    if ((pkmn.primaryType == Type.NORMAL && pkmn.secondaryType != null) ||
                            (pkmn.secondaryType == Type.NORMAL)) {

                        Type otherType = pkmn.primaryType == Type.NORMAL ? pkmn.secondaryType : pkmn.primaryType;

                        // Normal/OTHER: 10% normal, 30% other, 60% random
                        if (picked < 0.1) {
                            typeOfMove = Type.NORMAL;
                        } else if (picked < 0.4) {
                            typeOfMove = otherType;
                        }
                        // else random
                    } else if (pkmn.secondaryType != null) {
                        // Primary/Secondary: 20% primary, 20% secondary, 60% random
                        if (picked < 0.2) {
                            typeOfMove = pkmn.primaryType;
                        } else if (picked < 0.4) {
                            typeOfMove = pkmn.secondaryType;
                        }
                        // else random
                    } else {
                        // Primary/None: 40% primary, 60% random
                        if (picked < 0.4) {
                            typeOfMove = pkmn.primaryType;
                        }
                        // else random
                    }
                }

                // select a list to pick a move from that has at least one free
                List<Move> pickList = validMoves;
                if (attemptDamaging) {
                    if (typeOfMove != null) {
                        if (validTypeDamagingMoves.containsKey(typeOfMove)
                                && checkForUnusedMove(validTypeDamagingMoves.get(typeOfMove), learnt)) {
                            pickList = validTypeDamagingMoves.get(typeOfMove);
                        } else if (checkForUnusedMove(validDamagingMoves, learnt)) {
                            pickList = validDamagingMoves;
                        }
                    } else if (checkForUnusedMove(validDamagingMoves, learnt)) {
                        pickList = validDamagingMoves;
                    }
                } else if (typeOfMove != null) {
                    if (validTypeMoves.containsKey(typeOfMove)
                            && checkForUnusedMove(validTypeMoves.get(typeOfMove), learnt)) {
                        pickList = validTypeMoves.get(typeOfMove);
                    }
                }

                // now pick a move until we get a valid one
                Move mv = pickList.get(random.nextInt(pickList.size()));
                while (learnt.contains(mv.number)) {
                    mv = pickList.get(random.nextInt(pickList.size()));
                }

                // write it
                moves.get(i).move = mv.number;
                if (i == lv1index) {
                    // just in case, set this to lv1
                    moves.get(i).level = 1;
                }
                learnt.add(mv.number);

            }
        }
        // Done, save
        this.setMovesLearnt(movesets);

    }

    @Override
    public void orderDamagingMovesByDamage() {
        Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();
        List<Move> allMoves = this.getMoves();
        for (Integer pkmn : movesets.keySet()) {
            List<MoveLearnt> moves = movesets.get(pkmn);

            // Build up a list of damaging moves and their positions
            List<Integer> damagingMoveIndices = new ArrayList<>();
            List<Move> damagingMoves = new ArrayList<>();
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).level == 0) continue; // Don't reorder evolution move
                Move mv = allMoves.get(moves.get(i).move);
                if (mv.power > 1) {
                    // considered a damaging move for this purpose
                    damagingMoveIndices.add(i);
                    damagingMoves.add(mv);
                }
            }

            // Ties should be sorted randomly, so shuffle the list first.
            Collections.shuffle(damagingMoves, random);

            // Sort the damaging moves by power
            damagingMoves.sort(Comparator.comparingDouble(m -> m.power * m.hitCount));

            // Reassign damaging moves in the ordered positions
            for (int i = 0; i < damagingMoves.size(); i++) {
                moves.get(damagingMoveIndices.get(i)).move = damagingMoves.get(i).number;
            }
        }

        // Done, save
        this.setMovesLearnt(movesets);
    }

    @Override
    public void metronomeOnlyMode() {

        // movesets
        Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();

        MoveLearnt metronomeML = new MoveLearnt();
        metronomeML.level = 1;
        metronomeML.move = GlobalConstants.METRONOME_MOVE;

        for (List<MoveLearnt> ms : movesets.values()) {
            if (ms != null && ms.size() > 0) {
                ms.clear();
                ms.add(metronomeML);
            }
        }

        this.setMovesLearnt(movesets);

        // trainers
        // run this to remove all custom non-Metronome moves
        List<Trainer> trainers = this.getTrainers();

        for (Trainer t : trainers) {
            for (TrainerPokemon tpk : t.pokemon) {
                tpk.resetMoves = true;
            }
        }

        this.setTrainers(trainers, false);

        // tms
        List<Integer> tmMoves = this.getTMMoves();

        for (int i = 0; i < tmMoves.size(); i++) {
            tmMoves.set(i, GlobalConstants.METRONOME_MOVE);
        }

        this.setTMMoves(tmMoves);

        // movetutors
        if (this.hasMoveTutors()) {
            List<Integer> mtMoves = this.getMoveTutorMoves();

            for (int i = 0; i < mtMoves.size(); i++) {
                mtMoves.set(i, GlobalConstants.METRONOME_MOVE);
            }

            this.setMoveTutorMoves(mtMoves);
        }

        // move tweaks
        List<Move> moveData = this.getMoves();

        Move metronome = moveData.get(GlobalConstants.METRONOME_MOVE);

        metronome.pp = 40;

        List<Integer> hms = this.getHMMoves();

        for (int hm : hms) {
            Move thisHM = moveData.get(hm);
            thisHM.pp = 0;
        }
    }

    @Override
    public void randomizeStaticPokemon(boolean swapLegendaries, boolean similarStrength, boolean limitMainGameLegendaries,
                                       boolean limit600, boolean allowAltFormes, boolean swapMegaEvos,
                                       boolean abilitiesAreRandomized, int levelModifier) {
        // Load
        checkPokemonRestrictions();
        List<StaticEncounter> currentStaticPokemon = this.getStaticPokemon();
        List<StaticEncounter> replacements = new ArrayList<>();
        List<Pokemon> banned = this.bannedForStaticPokemon();
        banned.addAll(this.getBannedFormes());
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        boolean reallySwapMegaEvos = forceSwapStaticMegaEvos() || swapMegaEvos;
        if (swapLegendaries) {
            List<Pokemon> legendariesLeft = new ArrayList<>(onlyLegendaryList);
            if (allowAltFormes) {
                legendariesLeft.addAll(onlyLegendaryAltsList);
                legendariesLeft =
                        legendariesLeft
                                .stream()
                                .filter(pk -> !pk.actuallyCosmetic)
                                .collect(Collectors.toList());
            }
            List<Pokemon> nonlegsLeft = new ArrayList<>(noLegendaryList);
            if (allowAltFormes) {
                nonlegsLeft.addAll(noLegendaryAltsList);
                nonlegsLeft =
                        nonlegsLeft
                                .stream()
                                .filter(pk -> !pk.actuallyCosmetic)
                                .collect(Collectors.toList());
            }
            List<Pokemon> ultraBeastsLeft = new ArrayList<>(ultraBeastList);
            legendariesLeft.removeAll(banned);
            nonlegsLeft.removeAll(banned);
            ultraBeastsLeft.removeAll(banned);
            for (StaticEncounter old : currentStaticPokemon) {
                StaticEncounter newStatic = cloneStaticEncounter(old);
                Pokemon newPK;
                if (old.pkmn.number == 487 && ptGiratina) {
                    newPK = giratinaPicks.remove(this.random.nextInt(giratinaPicks.size()));
                    setPokemonAndFormeForStaticEncounter(newStatic, newPK);
                    legendariesLeft.remove(newPK);
                    if (legendariesLeft.size() == 0) {
                        legendariesLeft.addAll(onlyLegendaryList);
                        if (allowAltFormes) {
                            legendariesLeft.addAll(onlyLegendaryAltsList);
                        }
                        legendariesLeft.removeAll(banned);
                    }
                } else if (old.pkmn.isLegendary()) {
                    if (reallySwapMegaEvos && old.canMegaEvolve()) {
                        newPK = getMegaEvoPokemon(onlyLegendaryList, legendariesLeft, newStatic);
                    } else {
                        newPK = legendariesLeft.remove(this.random.nextInt(legendariesLeft.size()));
                    }

                    setPokemonAndFormeForStaticEncounter(newStatic, newPK);

                    if (legendariesLeft.size() == 0) {
                        legendariesLeft.addAll(onlyLegendaryList);
                        if (allowAltFormes) {
                            legendariesLeft.addAll(onlyLegendaryAltsList);
                            legendariesLeft =
                                    legendariesLeft
                                            .stream()
                                            .filter(pk -> !pk.actuallyCosmetic)
                                            .collect(Collectors.toList());
                        }
                        legendariesLeft.removeAll(banned);
                    }
                } else if (ultraBeastList.contains(old.pkmn)) {
                    newPK = ultraBeastsLeft.remove(this.random.nextInt(ultraBeastsLeft.size()));

                    setPokemonAndFormeForStaticEncounter(newStatic, newPK);

                    if (ultraBeastsLeft.size() == 0) {
                        ultraBeastsLeft.addAll(ultraBeastList);
                    }
                } else {
                    if (reallySwapMegaEvos && old.canMegaEvolve()) {
                        newPK = getMegaEvoPokemon(noLegendaryList, nonlegsLeft, newStatic);
                    } else {
                        newPK = nonlegsLeft.remove(this.random.nextInt(nonlegsLeft.size()));
                    }
                    setPokemonAndFormeForStaticEncounter(newStatic, newPK);

                    if (nonlegsLeft.size() == 0) {
                        nonlegsLeft.addAll(noLegendaryList);
                        if (allowAltFormes) {
                            nonlegsLeft.addAll(noLegendaryAltsList);
                            nonlegsLeft =
                                    nonlegsLeft
                                            .stream()
                                            .filter(pk -> !pk.actuallyCosmetic)
                                            .collect(Collectors.toList());
                        }
                        nonlegsLeft.removeAll(banned);
                    }
                }
                replacements.add(newStatic);
            }
        } else if (similarStrength) {
            List<Pokemon> listInclFormesExclCosmetics =
                    mainPokemonListInclFormes
                            .stream()
                            .filter(pk -> !pk.actuallyCosmetic)
                            .collect(Collectors.toList());
            List<Pokemon> pokemonLeft = new ArrayList<>(!allowAltFormes ? mainPokemonList : listInclFormesExclCosmetics);
            pokemonLeft.removeAll(banned);
            List<Integer> mainGameLegendaries = getMainGameLegendaries();
            for (StaticEncounter old : currentStaticPokemon) {
                StaticEncounter newStatic = cloneStaticEncounter(old);
                Pokemon newPK;
                Pokemon oldPK = old.pkmn;
                if (old.forme > 0) {
                    oldPK = getAltFormeOfPokemon(oldPK, old.forme);
                }
                Integer oldBST = oldPK.hp + oldPK.attack + oldPK.defense + oldPK.spatk + oldPK.spdef + oldPK.speed;
                if (oldPK.number == 487 && ptGiratina) {
                    newPK = giratinaPicks.remove(this.random.nextInt(giratinaPicks.size()));
                    pokemonLeft.remove(newPK);
                    setPokemonAndFormeForStaticEncounter(newStatic, newPK);
                } else if (oldBST >= 600 && limit600) {
                    if (reallySwapMegaEvos && old.canMegaEvolve()) {
                        newPK = getMegaEvoPokemon(mainPokemonList, pokemonLeft, newStatic);
                    } else {
                        newPK = pokemonLeft.remove(this.random.nextInt(pokemonLeft.size()));
                    }
                    setPokemonAndFormeForStaticEncounter(newStatic, newPK);
                } else {
                    boolean limitBST = oldPK.baseForme == null ?
                            limitMainGameLegendaries && mainGameLegendaries.contains(oldPK.number) :
                            limitMainGameLegendaries && mainGameLegendaries.contains(oldPK.baseForme.number);
                    if (reallySwapMegaEvos && old.canMegaEvolve()) {
                        List<Pokemon> megaEvoPokemonLeft =
                                megaEvolutionsList
                                        .stream()
                                        .filter(mega -> mega.method == 1)
                                        .map(mega -> mega.from)
                                        .distinct()
                                        .filter(pokemonLeft::contains)
                                        .collect(Collectors.toList());
                        if (megaEvoPokemonLeft.isEmpty()) {
                            megaEvoPokemonLeft =
                                    megaEvolutionsList
                                            .stream()
                                            .filter(mega -> mega.method == 1)
                                            .map(mega -> mega.from)
                                            .distinct()
                                            .filter(mainPokemonList::contains)
                                            .collect(Collectors.toList());
                        }
                        newPK = pickStaticPowerLvlReplacement(
                                megaEvoPokemonLeft,
                                oldPK,
                                true,
                                replacements.stream().map(enc -> enc.pkmn).collect(Collectors.toList()),
                                limitBST);
                        newStatic.heldItem = newPK
                                .megaEvolutionsFrom
                                .get(this.random.nextInt(newPK.megaEvolutionsFrom.size()))
                                .argument;
                    } else {
                        newPK = pickStaticPowerLvlReplacement(
                                pokemonLeft,
                                oldPK,
                                true,
                                replacements.stream().map(enc -> enc.pkmn).collect(Collectors.toList()),
                                limitBST);
                    }
                    pokemonLeft.remove(newPK);
                    setPokemonAndFormeForStaticEncounter(newStatic, newPK);
                }

                if (pokemonLeft.size() == 0) {
                    pokemonLeft.addAll(!allowAltFormes ? mainPokemonList : listInclFormesExclCosmetics);
                    pokemonLeft.removeAll(banned);
                }
                replacements.add(newStatic);
            }
        } else { // Completely random
            List<Pokemon> listInclFormesExclCosmetics =
                    mainPokemonListInclFormes
                            .stream()
                            .filter(pk -> !pk.actuallyCosmetic)
                            .collect(Collectors.toList());
            List<Pokemon> pokemonLeft = new ArrayList<>(!allowAltFormes ? mainPokemonList : listInclFormesExclCosmetics);
            pokemonLeft.removeAll(banned);
            for (StaticEncounter old : currentStaticPokemon) {
                StaticEncounter newStatic = cloneStaticEncounter(old);
                Pokemon newPK;
                if (old.pkmn.number == 487 && ptGiratina) {
                    newPK = giratinaPicks.remove(this.random.nextInt(giratinaPicks.size()));
                    pokemonLeft.remove(newPK);
                    setPokemonAndFormeForStaticEncounter(newStatic, newPK);
                } else {
                    if (reallySwapMegaEvos && old.canMegaEvolve()) {
                        newPK = getMegaEvoPokemon(mainPokemonList, pokemonLeft, newStatic);
                    } else {
                        newPK = pokemonLeft.remove(this.random.nextInt(pokemonLeft.size()));
                    }
                    pokemonLeft.remove(newPK);
                    setPokemonAndFormeForStaticEncounter(newStatic, newPK);
                }
                if (pokemonLeft.size() == 0) {
                    pokemonLeft.addAll(!allowAltFormes ? mainPokemonList : listInclFormesExclCosmetics);
                    pokemonLeft.removeAll(banned);
                }
                replacements.add(newStatic);
            }
        }

        if (levelModifier != 0) {
            for (StaticEncounter se : replacements) {
                if (!se.isEgg) {
                    se.level = Math.min(100, (int) Math.round(se.level * (1 + levelModifier / 100.0)));
                    for (StaticEncounter linkedStatic : se.linkedEncounters) {
                        if (!linkedStatic.isEgg) {
                            linkedStatic.level = Math.min(100, (int) Math.round(linkedStatic.level * (1 + levelModifier / 100.0)));
                        }
                    }
                }
            }
        }

        // Save
        this.setStaticPokemon(replacements);
    }

    @Override
    public void onlyChangeStaticLevels(int levelModifier) {
        List<StaticEncounter> currentStaticPokemon = this.getStaticPokemon();
        for (StaticEncounter se : currentStaticPokemon) {
            if (!se.isEgg) {
                se.level = Math.min(100, (int) Math.round(se.level * (1 + levelModifier / 100.0)));
                for (StaticEncounter linkedStatic : se.linkedEncounters) {
                    if (!linkedStatic.isEgg) {
                        linkedStatic.level = Math.min(100, (int) Math.round(linkedStatic.level * (1 + levelModifier / 100.0)));
                    }
                }
            }
            setPokemonAndFormeForStaticEncounter(se, se.pkmn);
        }
        this.setStaticPokemon(currentStaticPokemon);
    }

    private StaticEncounter cloneStaticEncounter(StaticEncounter old) {
        StaticEncounter newStatic = new StaticEncounter();
        newStatic.pkmn = old.pkmn;
        newStatic.level = old.level;
        newStatic.heldItem = old.heldItem;
        newStatic.isEgg = old.isEgg;
        newStatic.resetMoves = true;
        for (StaticEncounter oldLinked : old.linkedEncounters) {
            StaticEncounter newLinked = new StaticEncounter();
            newLinked.pkmn = oldLinked.pkmn;
            newLinked.level = oldLinked.level;
            newLinked.heldItem = oldLinked.heldItem;
            newLinked.isEgg = oldLinked.isEgg;
            newLinked.resetMoves = true;
            newStatic.linkedEncounters.add(newLinked);
        }
        return newStatic;
    }

    private void setPokemonAndFormeForStaticEncounter(StaticEncounter newStatic, Pokemon pk) {
        boolean checkCosmetics = true;
        Pokemon newPK = pk;
        int newForme = 0;
        if (pk.formeNumber > 0) {
            newForme = pk.formeNumber;
            newPK = pk.baseForme;
            checkCosmetics = false;
        }
        if (checkCosmetics && pk.cosmeticForms > 0) {
            newForme = pk.getCosmeticFormNumber(this.random.nextInt(pk.cosmeticForms));
        } else if (!checkCosmetics && pk.cosmeticForms > 0) {
            newForme += pk.getCosmeticFormNumber(this.random.nextInt(pk.cosmeticForms));
        }
        newStatic.pkmn = newPK;
        newStatic.forme = newForme;
        for (StaticEncounter linked : newStatic.linkedEncounters) {
            linked.pkmn = newPK;
            linked.forme = newForme;
        }
    }

    private void setFormeForStaticEncounter(StaticEncounter newStatic, Pokemon pk) {
        boolean checkCosmetics = true;
        newStatic.forme = 0;
        if (pk.formeNumber > 0) {
            newStatic.forme = pk.formeNumber;
            newStatic.pkmn = pk.baseForme;
            checkCosmetics = false;
        }
        if (checkCosmetics && newStatic.pkmn.cosmeticForms > 0) {
            newStatic.forme = newStatic.pkmn.getCosmeticFormNumber(this.random.nextInt(newStatic.pkmn.cosmeticForms));
        } else if (!checkCosmetics && pk.cosmeticForms > 0) {
            newStatic.forme += pk.getCosmeticFormNumber(this.random.nextInt(pk.cosmeticForms));
        }
    }

    private Pokemon getMegaEvoPokemon(List<Pokemon> fullList, List<Pokemon> pokemonLeft, StaticEncounter newStatic) {
        List<MegaEvolution> megaEvos = megaEvolutionsList;
        List<Pokemon> megaEvoPokemon =
                megaEvos
                        .stream()
                        .filter(mega -> mega.method == 1)
                        .map(mega -> mega.from)
                        .distinct()
                        .collect(Collectors.toList());
        Pokemon newPK;
        List<Pokemon> megaEvoPokemonLeft =
                megaEvoPokemon
                        .stream()
                        .filter(pokemonLeft::contains)
                        .collect(Collectors.toList());
        if (megaEvoPokemonLeft.isEmpty()) {
            megaEvoPokemonLeft = megaEvoPokemon
                    .stream()
                    .filter(fullList::contains)
                    .collect(Collectors.toList());
        }
        newPK = megaEvoPokemonLeft.remove(this.random.nextInt(megaEvoPokemonLeft.size()));
        pokemonLeft.remove(newPK);
        newStatic.heldItem = newPK
                .megaEvolutionsFrom
                .get(this.random.nextInt(newPK.megaEvolutionsFrom.size()))
                .argument;
        return newPK;
    }

    @Override
    public void randomizeTMMoves(boolean noBroken, boolean preserveField, double goodDamagingProbability) {
        // Pick some random TM moves.
        int tmCount = this.getTMCount();
        List<Move> allMoves = this.getMoves();
        List<Integer> hms = this.getHMMoves();
        List<Integer> oldTMs = this.getTMMoves();
        @SuppressWarnings("unchecked")
        List<Integer> banned = new ArrayList<Integer>(noBroken ? this.getGameBreakingMoves() : Collections.EMPTY_LIST);
        banned.addAll(getMovesBannedFromLevelup());
        // field moves?
        List<Integer> fieldMoves = this.getFieldMoves();
        int preservedFieldMoveCount = 0;

        if (preserveField) {
            List<Integer> banExistingField = new ArrayList<>(oldTMs);
            banExistingField.retainAll(fieldMoves);
            preservedFieldMoveCount = banExistingField.size();
            banned.addAll(banExistingField);
        }

        // Determine which moves are pickable
        List<Move> usableMoves = new ArrayList<>(allMoves);
        usableMoves.remove(0); // remove null entry
        Set<Move> unusableMoves = new HashSet<>();
        Set<Move> unusableDamagingMoves = new HashSet<>();

        for (Move mv : usableMoves) {
            if (GlobalConstants.bannedRandomMoves[mv.number] || GlobalConstants.zMoves.contains(mv.number) ||
                    hms.contains(mv.number) || banned.contains(mv.number)) {
                unusableMoves.add(mv);
            } else if (GlobalConstants.bannedForDamagingMove[mv.number]
                    || mv.power < GlobalConstants.MIN_DAMAGING_MOVE_POWER) {
                unusableDamagingMoves.add(mv);
            }
        }

        usableMoves.removeAll(unusableMoves);
        List<Move> usableDamagingMoves = new ArrayList<>(usableMoves);
        usableDamagingMoves.removeAll(unusableDamagingMoves);

        // pick (tmCount - preservedFieldMoveCount) moves
        List<Integer> pickedMoves = new ArrayList<>();

        for (int i = 0; i < tmCount - preservedFieldMoveCount; i++) {
            Move chosenMove;
            if (random.nextDouble() < goodDamagingProbability && usableDamagingMoves.size() > 0) {
                chosenMove = usableDamagingMoves.get(random.nextInt(usableDamagingMoves.size()));
            } else {
                chosenMove = usableMoves.get(random.nextInt(usableMoves.size()));
            }
            pickedMoves.add(chosenMove.number);
            usableMoves.remove(chosenMove);
            usableDamagingMoves.remove(chosenMove);
        }

        // shuffle the picked moves because high goodDamagingProbability
        // could bias them towards early numbers otherwise

        Collections.shuffle(pickedMoves, random);

        // finally, distribute them as tms
        int pickedMoveIndex = 0;
        List<Integer> newTMs = new ArrayList<>();

        for (int i = 0; i < tmCount; i++) {
            if (preserveField && fieldMoves.contains(oldTMs.get(i))) {
                newTMs.add(oldTMs.get(i));
            } else {
                newTMs.add(pickedMoves.get(pickedMoveIndex++));
            }
        }

        this.setTMMoves(newTMs);
    }

    @Override
    public void randomizeTMHMCompatibility(boolean preferSameType, boolean followEvolutions) {
        // Get current compatibility
        // new: increase HM chances if required early on
        List<Integer> requiredEarlyOn = this.getEarlyRequiredHMMoves();
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();
        List<Integer> tmHMs = new ArrayList<>(this.getTMMoves());
        tmHMs.addAll(this.getHMMoves());

        if (followEvolutions) {
            copyUpEvolutionsHelper(pk -> randomizePokemonMoveCompatibility(
                    pk, compat.get(pk), tmHMs, requiredEarlyOn, preferSameType),
            (evFrom, evTo, toMonIsFinalEvo) ->  copyPokemonMoveCompatibilityUpEvolutions(
                    evFrom, evTo, compat.get(evFrom), compat.get(evTo), tmHMs, preferSameType
            ), false);
        }
        else {
            for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
                randomizePokemonMoveCompatibility(compatEntry.getKey(), compatEntry.getValue(), tmHMs,
                        requiredEarlyOn, preferSameType);
            }
        }

        // Set the new compatibility
        this.setTMHMCompatibility(compat);
    }

    private void randomizePokemonMoveCompatibility(Pokemon pkmn, boolean[] moveCompatibilityFlags,
                                                   List<Integer> moveIDs, List<Integer> prioritizedMoves,
                                                   boolean preferSameType) {
        List<Move> moveData = this.getMoves();
        for (int i = 1; i <= moveIDs.size(); i++) {
            int move = moveIDs.get(i - 1);
            Move mv = moveData.get(move);
            double probability = getMoveCompatibilityProbability(
                    pkmn,
                    mv,
                    prioritizedMoves.contains(move),
                    preferSameType
            );
            moveCompatibilityFlags[i] = (this.random.nextDouble() < probability);
        }
    }

    private void copyPokemonMoveCompatibilityUpEvolutions(Pokemon evFrom, Pokemon evTo, boolean[] prevCompatibilityFlags,
                                                          boolean[] toCompatibilityFlags, List<Integer> moveIDs,
                                                          boolean preferSameType) {
        List<Move> moveData = this.getMoves();
        for (int i = 1; i <= moveIDs.size(); i++) {
            if (!prevCompatibilityFlags[i]) {
                // Slight chance to gain TM/HM compatibility for a move if not learned by an earlier evolution step
                // Without prefer same type: 25% chance
                // With prefer same type:    10% chance, 90% chance for a type new to this evolution
                int move = moveIDs.get(i - 1);
                Move mv = moveData.get(move);
                double probability = 0.25;
                if (preferSameType) {
                    probability = 0.1;
                    if (evTo.primaryType.equals(mv.type)
                            && !evTo.primaryType.equals(evFrom.primaryType) && !evTo.primaryType.equals(evFrom.secondaryType)
                            || evTo.secondaryType != null && evTo.secondaryType.equals(mv.type)
                            && !evTo.secondaryType.equals(evFrom.secondaryType) && !evTo.secondaryType.equals(evFrom.primaryType)) {
                        probability = 0.9;
                    }
                }
                toCompatibilityFlags[i] = (this.random.nextDouble() < probability);
            }
            else {
                toCompatibilityFlags[i] = prevCompatibilityFlags[i];
            }
        }
    }

    private double getMoveCompatibilityProbability(Pokemon pkmn, Move mv, boolean requiredEarlyOn,
                                                  boolean preferSameType) {
            double probability = 0.5;
            if (preferSameType) {
                if (pkmn.primaryType.equals(mv.type)
                        || (pkmn.secondaryType != null && pkmn.secondaryType.equals(mv.type))) {
                    probability = 0.9;
                } else if (mv.type != null && mv.type.equals(Type.NORMAL)) {
                    probability = 0.5;
                } else {
                    probability = 0.25;
                }
            }
            if (requiredEarlyOn) {
                probability = Math.min(1.0, probability * 1.8);
            }
            return probability;
    }

    @Override
    public void fullTMHMCompatibility() {
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();
        for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
            boolean[] flags = compatEntry.getValue();
            for (int i = 1; i < flags.length; i++) {
                flags[i] = true;
            }
        }
        this.setTMHMCompatibility(compat);
    }

    @Override
    public void ensureTMCompatSanity() {
        // if a pokemon learns a move in its moveset
        // and there is a TM of that move, make sure
        // that TM can be learned.
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();
        Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();
        List<Integer> tmMoves = this.getTMMoves();
        for (Pokemon pkmn : compat.keySet()) {
            List<MoveLearnt> moveset = movesets.get(pkmn.number);
            boolean[] pkmnCompat = compat.get(pkmn);
            for (MoveLearnt ml : moveset) {
                if (tmMoves.contains(ml.move)) {
                    int tmIndex = tmMoves.indexOf(ml.move);
                    pkmnCompat[tmIndex + 1] = true;
                }
            }
        }
        this.setTMHMCompatibility(compat);
    }

    @Override
    public void ensureTMEvolutionSanity() {
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();
        // Don't do anything with the base, just copy upwards to ensure later evolutions retain learn compatibility
        copyUpEvolutionsHelper(pk -> {}, ((evFrom, evTo, toMonIsFinalEvo) -> {
            boolean[] fromCompat = compat.get(evFrom);
            boolean[] toCompat = compat.get(evTo);
            for (int i = 1; i < toCompat.length; i++) {
                toCompat[i] |= fromCompat[i];
            }
        }), false);
        this.setTMHMCompatibility(compat);
    }

    @Override
    public void fullHMCompatibility() {
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();
        int tmCount = this.getTMCount();
        for (boolean[] flags : compat.values()) {
            for (int i = tmCount + 1; i < flags.length; i++) {
                flags[i] = true;
            }
        }

        // Set the new compatibility
        this.setTMHMCompatibility(compat);
    }

    @Override
    public void copyTMCompatibilityToCosmeticFormes() {
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();

        for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
            Pokemon pkmn = compatEntry.getKey();
            boolean[] flags = compatEntry.getValue();
            if (pkmn.actuallyCosmetic) {
                boolean[] baseFlags = compat.get(pkmn.baseForme);
                for (int i = 1; i < flags.length; i++) {
                    flags[i] = baseFlags[i];
                }
            }
        }

        this.setTMHMCompatibility(compat);
    }

    @Override
    public void randomizeMoveTutorMoves(boolean noBroken, boolean preserveField, double goodDamagingProbability) {
        if (!this.hasMoveTutors()) {
            return;
        }

        // Pick some random Move Tutor moves, excluding TMs.
        List<Move> allMoves = this.getMoves();
        List<Integer> tms = this.getTMMoves();
        List<Integer> oldMTs = this.getMoveTutorMoves();
        int mtCount = oldMTs.size();
        List<Integer> hms = this.getHMMoves();
        @SuppressWarnings("unchecked")
        List<Integer> banned = new ArrayList<Integer>(noBroken ? this.getGameBreakingMoves() : Collections.EMPTY_LIST);
        banned.addAll(getMovesBannedFromLevelup());

        // field moves?
        List<Integer> fieldMoves = this.getFieldMoves();
        int preservedFieldMoveCount = 0;
        if (preserveField) {
            List<Integer> banExistingField = new ArrayList<>(oldMTs);
            banExistingField.retainAll(fieldMoves);
            preservedFieldMoveCount = banExistingField.size();
            banned.addAll(banExistingField);
        }

        // Determine which moves are pickable
        List<Move> usableMoves = new ArrayList<>(allMoves);
        usableMoves.remove(0); // remove null entry
        Set<Move> unusableMoves = new HashSet<>();
        Set<Move> unusableDamagingMoves = new HashSet<>();

        for (Move mv : usableMoves) {
            if (GlobalConstants.bannedRandomMoves[mv.number] || tms.contains(mv.number) || hms.contains(mv.number)
                    || banned.contains(mv.number) || GlobalConstants.zMoves.contains(mv.number)) {
                unusableMoves.add(mv);
            } else if (GlobalConstants.bannedForDamagingMove[mv.number]
                    || mv.power < GlobalConstants.MIN_DAMAGING_MOVE_POWER) {
                unusableDamagingMoves.add(mv);
            }
        }

        usableMoves.removeAll(unusableMoves);
        List<Move> usableDamagingMoves = new ArrayList<>(usableMoves);
        usableDamagingMoves.removeAll(unusableDamagingMoves);

        // pick (tmCount - preservedFieldMoveCount) moves
        List<Integer> pickedMoves = new ArrayList<>();

        for (int i = 0; i < mtCount - preservedFieldMoveCount; i++) {
            Move chosenMove;
            if (random.nextDouble() < goodDamagingProbability && usableDamagingMoves.size() > 0) {
                chosenMove = usableDamagingMoves.get(random.nextInt(usableDamagingMoves.size()));
            } else {
                chosenMove = usableMoves.get(random.nextInt(usableMoves.size()));
            }
            pickedMoves.add(chosenMove.number);
            usableMoves.remove(chosenMove);
            usableDamagingMoves.remove(chosenMove);
        }

        // shuffle the picked moves because high goodDamagingProbability
        // could bias them towards early numbers otherwise

        Collections.shuffle(pickedMoves, random);

        // finally, distribute them as tutors
        int pickedMoveIndex = 0;
        List<Integer> newMTs = new ArrayList<>();

        for (Integer oldMT : oldMTs) {
            if (preserveField && fieldMoves.contains(oldMT)) {
                newMTs.add(oldMT);
            } else {
                newMTs.add(pickedMoves.get(pickedMoveIndex++));
            }
        }

        this.setMoveTutorMoves(newMTs);
    }

    @Override
    public void randomizeMoveTutorCompatibility(boolean preferSameType, boolean followEvolutions) {
        if (!this.hasMoveTutors()) {
            return;
        }
        // Get current compatibility
        Map<Pokemon, boolean[]> compat = this.getMoveTutorCompatibility();
        List<Integer> mts = this.getMoveTutorMoves();

        // Empty list
        List<Integer> priorityTutors = new ArrayList<Integer>();

        if (followEvolutions) {
            copyUpEvolutionsHelper(pk -> randomizePokemonMoveCompatibility(
                    pk, compat.get(pk), mts, priorityTutors, preferSameType),
                    (evFrom, evTo, toMonIsFinalEvo) ->  copyPokemonMoveCompatibilityUpEvolutions(
                            evFrom, evTo, compat.get(evFrom), compat.get(evTo), mts, preferSameType
                    ), false);
        }
        else {
            for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
                randomizePokemonMoveCompatibility(compatEntry.getKey(), compatEntry.getValue(), mts, priorityTutors, preferSameType);
            }
        }

        // Set the new compatibility
        this.setMoveTutorCompatibility(compat);
    }

    @Override
    public void fullMoveTutorCompatibility() {
        if (!this.hasMoveTutors()) {
            return;
        }
        Map<Pokemon, boolean[]> compat = this.getMoveTutorCompatibility();
        for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
            boolean[] flags = compatEntry.getValue();
            for (int i = 1; i < flags.length; i++) {
                flags[i] = true;
            }
        }
        this.setMoveTutorCompatibility(compat);
    }

    @Override
    public void ensureMoveTutorCompatSanity() {
        if (!this.hasMoveTutors()) {
            return;
        }
        // if a pokemon learns a move in its moveset
        // and there is a tutor of that move, make sure
        // that tutor can be learned.
        Map<Pokemon, boolean[]> compat = this.getMoveTutorCompatibility();
        Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();
        List<Integer> mtMoves = this.getMoveTutorMoves();
        for (Pokemon pkmn : compat.keySet()) {
            List<MoveLearnt> moveset = movesets.get(pkmn.number);
            boolean[] pkmnCompat = compat.get(pkmn);
            for (MoveLearnt ml : moveset) {
                if (mtMoves.contains(ml.move)) {
                    int mtIndex = mtMoves.indexOf(ml.move);
                    pkmnCompat[mtIndex + 1] = true;
                }
            }
        }
        this.setMoveTutorCompatibility(compat);
    }

    @Override
    public void ensureMoveTutorEvolutionSanity() {
        if (!this.hasMoveTutors()) {
            return;
        }
        Map<Pokemon, boolean[]> compat = this.getMoveTutorCompatibility();
        // Don't do anything with the base, just copy upwards to ensure later evolutions retain learn compatibility
        copyUpEvolutionsHelper(pk -> {}, ((evFrom, evTo, toMonIsFinalEvo) -> {
            boolean[] fromCompat = compat.get(evFrom);
            boolean[] toCompat = compat.get(evTo);
            for (int i = 1; i < toCompat.length; i++) {
                toCompat[i] |= fromCompat[i];
            }
        }), false);
        this.setMoveTutorCompatibility(compat);
    }

    @Override
    public void copyMoveTutorCompatibilityToCosmeticFormes() {
        Map<Pokemon, boolean[]> compat = this.getMoveTutorCompatibility();

        for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
            Pokemon pkmn = compatEntry.getKey();
            boolean[] flags = compatEntry.getValue();
            if (pkmn.actuallyCosmetic) {
                boolean[] baseFlags = compat.get(pkmn.baseForme);
                for (int i = 1; i < flags.length; i++) {
                    flags[i] = baseFlags[i];
                }
            }
        }

        this.setMoveTutorCompatibility(compat);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void randomizeTrainerNames(CustomNamesSet customNames) {
        if (!this.canChangeTrainerText()) {
            return;
        }

        // index 0 = singles, 1 = doubles
        List<String>[] allTrainerNames = new List[] { new ArrayList<String>(), new ArrayList<String>() };
        Map<Integer, List<String>> trainerNamesByLength[] = new Map[] { new TreeMap<Integer, List<String>>(),
                new TreeMap<Integer, List<String>>() };

        // Read name lists
        for (String trainername : customNames.getTrainerNames()) {
            int len = this.internalStringLength(trainername);
            if (len <= 10) {
                allTrainerNames[0].add(trainername);
                if (trainerNamesByLength[0].containsKey(len)) {
                    trainerNamesByLength[0].get(len).add(trainername);
                } else {
                    List<String> namesOfThisLength = new ArrayList<>();
                    namesOfThisLength.add(trainername);
                    trainerNamesByLength[0].put(len, namesOfThisLength);
                }
            }
        }

        for (String trainername : customNames.getDoublesTrainerNames()) {
            int len = this.internalStringLength(trainername);
            if (len <= 10) {
                allTrainerNames[1].add(trainername);
                if (trainerNamesByLength[1].containsKey(len)) {
                    trainerNamesByLength[1].get(len).add(trainername);
                } else {
                    List<String> namesOfThisLength = new ArrayList<>();
                    namesOfThisLength.add(trainername);
                    trainerNamesByLength[1].put(len, namesOfThisLength);
                }
            }
        }

        // Get the current trainer names data
        List<String> currentTrainerNames = this.getTrainerNames();
        if (currentTrainerNames.size() == 0) {
            // RBY have no trainer names
            return;
        }
        TrainerNameMode mode = this.trainerNameMode();
        int maxLength = this.maxTrainerNameLength();
        int totalMaxLength = this.maxSumOfTrainerNameLengths();

        boolean success = false;
        int tries = 0;

        // Init the translation map and new list
        Map<String, String> translation = new HashMap<>();
        List<String> newTrainerNames = new ArrayList<>();
        List<Integer> tcNameLengths = this.getTCNameLengthsByTrainer();

        // loop until we successfully pick names that fit
        // should always succeed first attempt except for gen2.
        while (!success && tries < 10000) {
            success = true;
            translation.clear();
            newTrainerNames.clear();
            int totalLength = 0;

            // Start choosing
            int tnIndex = -1;
            for (String trainerName : currentTrainerNames) {
                tnIndex++;
                if (translation.containsKey(trainerName) && !trainerName.equalsIgnoreCase("GRUNT")
                        && !trainerName.equalsIgnoreCase("EXECUTIVE")) {
                    // use an already picked translation
                    newTrainerNames.add(translation.get(trainerName));
                    totalLength += this.internalStringLength(translation.get(trainerName));
                } else {
                    int idx = trainerName.contains("&") ? 1 : 0;
                    List<String> pickFrom = allTrainerNames[idx];
                    int intStrLen = this.internalStringLength(trainerName);
                    if (mode == TrainerNameMode.SAME_LENGTH) {
                        pickFrom = trainerNamesByLength[idx].get(intStrLen);
                    }
                    String changeTo = trainerName;
                    int ctl = intStrLen;
                    if (pickFrom != null && pickFrom.size() > 0 && intStrLen > 0) {
                        int innerTries = 0;
                        changeTo = pickFrom.get(this.cosmeticRandom.nextInt(pickFrom.size()));
                        ctl = this.internalStringLength(changeTo);
                        while ((mode == TrainerNameMode.MAX_LENGTH && ctl > maxLength)
                                || (mode == TrainerNameMode.MAX_LENGTH_WITH_CLASS && ctl + tcNameLengths.get(tnIndex) > maxLength)) {
                            innerTries++;
                            if (innerTries == 100) {
                                changeTo = trainerName;
                                ctl = intStrLen;
                                break;
                            }
                            changeTo = pickFrom.get(this.cosmeticRandom.nextInt(pickFrom.size()));
                            ctl = this.internalStringLength(changeTo);
                        }
                    }
                    translation.put(trainerName, changeTo);
                    newTrainerNames.add(changeTo);
                    totalLength += ctl;
                }

                if (totalLength > totalMaxLength) {
                    success = false;
                    tries++;
                    break;
                }
            }
        }

        if (!success) {
            throw new RandomizationException("Could not randomize trainer names in a reasonable amount of attempts."
                    + "\nPlease add some shorter names to your custom trainer names.");
        }

        // Done choosing, save
        this.setTrainerNames(newTrainerNames);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void randomizeTrainerClassNames(CustomNamesSet customNames) {
        if (!this.canChangeTrainerText()) {
            return;
        }

        // index 0 = singles, index 1 = doubles
        List<String> allTrainerClasses[] = new List[] { new ArrayList<String>(), new ArrayList<String>() };
        Map<Integer, List<String>> trainerClassesByLength[] = new Map[] { new HashMap<Integer, List<String>>(),
                new HashMap<Integer, List<String>>() };

        // Read names data
        for (String trainerClassName : customNames.getTrainerClasses()) {
            allTrainerClasses[0].add(trainerClassName);
            int len = this.internalStringLength(trainerClassName);
            if (trainerClassesByLength[0].containsKey(len)) {
                trainerClassesByLength[0].get(len).add(trainerClassName);
            } else {
                List<String> namesOfThisLength = new ArrayList<>();
                namesOfThisLength.add(trainerClassName);
                trainerClassesByLength[0].put(len, namesOfThisLength);
            }
        }

        for (String trainerClassName : customNames.getDoublesTrainerClasses()) {
            allTrainerClasses[1].add(trainerClassName);
            int len = this.internalStringLength(trainerClassName);
            if (trainerClassesByLength[1].containsKey(len)) {
                trainerClassesByLength[1].get(len).add(trainerClassName);
            } else {
                List<String> namesOfThisLength = new ArrayList<>();
                namesOfThisLength.add(trainerClassName);
                trainerClassesByLength[1].put(len, namesOfThisLength);
            }
        }

        // Get the current trainer names data
        List<String> currentClassNames = this.getTrainerClassNames();
        boolean mustBeSameLength = this.fixedTrainerClassNamesLength();
        int maxLength = this.maxTrainerClassNameLength();

        // Init the translation map and new list
        Map<String, String> translation = new HashMap<>();
        List<String> newClassNames = new ArrayList<>();

        int numTrainerClasses = currentClassNames.size();
        List<Integer> doublesClasses = this.getDoublesTrainerClasses();

        // Start choosing
        for (int i = 0; i < numTrainerClasses; i++) {
            String trainerClassName = currentClassNames.get(i);
            if (translation.containsKey(trainerClassName)) {
                // use an already picked translation
                newClassNames.add(translation.get(trainerClassName));
            } else {
                int idx = doublesClasses.contains(i) ? 1 : 0;
                List<String> pickFrom = allTrainerClasses[idx];
                int intStrLen = this.internalStringLength(trainerClassName);
                if (mustBeSameLength) {
                    pickFrom = trainerClassesByLength[idx].get(intStrLen);
                }
                String changeTo = trainerClassName;
                if (pickFrom != null && pickFrom.size() > 0) {
                    changeTo = pickFrom.get(this.cosmeticRandom.nextInt(pickFrom.size()));
                    while (changeTo.length() > maxLength) {
                        changeTo = pickFrom.get(this.cosmeticRandom.nextInt(pickFrom.size()));
                    }
                }
                translation.put(trainerClassName, changeTo);
                newClassNames.add(changeTo);
            }
        }

        // Done choosing, save
        this.setTrainerClassNames(newClassNames);
    }

    @Override
    public void banPlatesAndIncense(ItemList itemList) {
        // Do nothing except in gen-specific logic
    }

    @Override
    public void randomizeWildHeldItems(boolean banBadItems) {
        List<Pokemon> pokemon = allPokemonInclFormesWithoutNull();
        ItemList possibleItems = banBadItems ? this.getNonBadItems() : this.getAllowedItems();
        for (Pokemon pk : pokemon) {
            if (pk.guaranteedHeldItem == -1 && pk.commonHeldItem == -1 && pk.rareHeldItem == -1
                    && pk.darkGrassHeldItem == -1) {
                // No held items at all, abort
                return;
            }
            boolean canHaveDarkGrass = pk.darkGrassHeldItem != -1;
            if (pk.guaranteedHeldItem != -1) {
                // Guaranteed held items are supported.
                if (pk.guaranteedHeldItem > 0) {
                    // Currently have a guaranteed item
                    double decision = this.random.nextDouble();
                    if (decision < 0.9) {
                        // Stay as guaranteed
                        canHaveDarkGrass = false;
                        pk.guaranteedHeldItem = possibleItems.randomItem(this.random);
                    } else {
                        // Change to 25% or 55% chance
                        pk.guaranteedHeldItem = 0;
                        pk.commonHeldItem = possibleItems.randomItem(this.random);
                        pk.rareHeldItem = possibleItems.randomItem(this.random);
                        while (pk.rareHeldItem == pk.commonHeldItem) {
                            pk.rareHeldItem = possibleItems.randomItem(this.random);
                        }
                    }
                } else {
                    // No guaranteed item atm
                    double decision = this.random.nextDouble();
                    if (decision < 0.5) {
                        // No held item at all
                        pk.commonHeldItem = 0;
                        pk.rareHeldItem = 0;
                    } else if (decision < 0.65) {
                        // Just a rare item
                        pk.commonHeldItem = 0;
                        pk.rareHeldItem = possibleItems.randomItem(this.random);
                    } else if (decision < 0.8) {
                        // Just a common item
                        pk.commonHeldItem = possibleItems.randomItem(this.random);
                        pk.rareHeldItem = 0;
                    } else if (decision < 0.95) {
                        // Both a common and rare item
                        pk.commonHeldItem = possibleItems.randomItem(this.random);
                        pk.rareHeldItem = possibleItems.randomItem(this.random);
                        while (pk.rareHeldItem == pk.commonHeldItem) {
                            pk.rareHeldItem = possibleItems.randomItem(this.random);
                        }
                    } else {
                        // Guaranteed item
                        canHaveDarkGrass = false;
                        pk.guaranteedHeldItem = possibleItems.randomItem(this.random);
                        pk.commonHeldItem = 0;
                        pk.rareHeldItem = 0;
                    }
                }
            } else {
                // Code for no guaranteed items
                double decision = this.random.nextDouble();
                if (decision < 0.5) {
                    // No held item at all
                    pk.commonHeldItem = 0;
                    pk.rareHeldItem = 0;
                } else if (decision < 0.65) {
                    // Just a rare item
                    pk.commonHeldItem = 0;
                    pk.rareHeldItem = possibleItems.randomItem(this.random);
                } else if (decision < 0.8) {
                    // Just a common item
                    pk.commonHeldItem = possibleItems.randomItem(this.random);
                    pk.rareHeldItem = 0;
                } else {
                    // Both a common and rare item
                    pk.commonHeldItem = possibleItems.randomItem(this.random);
                    pk.rareHeldItem = possibleItems.randomItem(this.random);
                    while (pk.rareHeldItem == pk.commonHeldItem) {
                        pk.rareHeldItem = possibleItems.randomItem(this.random);
                    }
                }
            }

            if (canHaveDarkGrass) {
                double dgDecision = this.random.nextDouble();
                if (dgDecision < 0.5) {
                    // Yes, dark grass item
                    pk.darkGrassHeldItem = possibleItems.randomItem(this.random);
                } else {
                    pk.darkGrassHeldItem = 0;
                }
            } else if (pk.darkGrassHeldItem != -1) {
                pk.darkGrassHeldItem = 0;
            }
        }

    }

    @Override
    public void randomizeStarterHeldItems(boolean banBadItems) {
        List<Integer> oldHeldItems = this.getStarterHeldItems();
        List<Integer> newHeldItems = new ArrayList<>();
        ItemList possibleItems = banBadItems ? this.getNonBadItems() : this.getAllowedItems();
        for (int i = 0; i < oldHeldItems.size(); i++) {
            newHeldItems.add(possibleItems.randomItem(this.random));
        }
        this.setStarterHeldItems(newHeldItems);
    }

    @Override
    public void shuffleFieldItems() {
        List<Integer> currentItems = this.getRegularFieldItems();
        List<Integer> currentTMs = this.getCurrentFieldTMs();

        Collections.shuffle(currentItems, this.random);
        Collections.shuffle(currentTMs, this.random);

        this.setRegularFieldItems(currentItems);
        this.setFieldTMs(currentTMs);
    }

    @Override
    public void randomizeFieldItems(boolean banBadItems, boolean banPlatesAndIncense, boolean distributeItemsControl) {
        ItemList possibleItems = banBadItems ? this.getNonBadItems() : this.getAllowedItems();
        if (banPlatesAndIncense) {
            this.banPlatesAndIncense(possibleItems);
        }

        List<Integer> currentItems = this.getRegularFieldItems();
        List<Integer> currentTMs = this.getCurrentFieldTMs();
        List<Integer> requiredTMs = this.getRequiredFieldTMs();
        // System.out.println("distributeItemsControl: "+ distributeItemsControl);

        int fieldItemCount = currentItems.size();
        int fieldTMCount = currentTMs.size();
        int reqTMCount = requiredTMs.size();
        int totalTMCount = this.getTMCount();

        List<Integer> newItems = new ArrayList<>();
        List<Integer> newTMs = new ArrayList<>(requiredTMs);

        // List<Integer> chosenItems = new ArrayList<Integer>(); // collecting chosenItems for later process
        
        if (distributeItemsControl) {
            for (int i = 0; i < fieldItemCount; i++) {
                int chosenItem = possibleItems.randomNonTM(this.random);
                int iterNum = 0;
                while ((this.getItemPlacementHistory(chosenItem) > this.getItemPlacementAverage()) && iterNum < 100) {
                    chosenItem = possibleItems.randomNonTM(this.random);
                    iterNum +=1;
                }
                newItems.add(chosenItem);
                this.setItemPlacementHistory(chosenItem);
            }
        } else {
            for (int i = 0; i < fieldItemCount; i++) {
                newItems.add(possibleItems.randomNonTM(this.random));
            }
        }

        for (int i = reqTMCount; i < fieldTMCount; i++) {
            while (true) {
                int tm = this.random.nextInt(totalTMCount) + 1;
                if (!newTMs.contains(tm)) {
                    newTMs.add(tm);
                    break;
                }
            }
        }


        Collections.shuffle(newItems, this.random);
        Collections.shuffle(newTMs, this.random);

        this.setRegularFieldItems(newItems);
        this.setFieldTMs(newTMs);
    }

    @Override
    public void randomizeIngameTrades(boolean randomizeRequest, boolean randomNickname, boolean randomOT,
            boolean randomStats, boolean randomItem, CustomNamesSet customNames) {
        checkPokemonRestrictions();
        // Process trainer names
        List<String> trainerNames = new ArrayList<>();
        // Check for the file
        if (randomOT) {
            int maxOT = this.maxTradeOTNameLength();
            for (String trainername : customNames.getTrainerNames()) {
                int len = this.internalStringLength(trainername);
                if (len <= maxOT && !trainerNames.contains(trainername)) {
                    trainerNames.add(trainername);
                }
            }
        }

        // Process nicknames
        List<String> nicknames = new ArrayList<>();
        // Check for the file
        if (randomNickname) {
            int maxNN = this.maxTradeNicknameLength();
            for (String nickname : customNames.getPokemonNicknames()) {
                int len = this.internalStringLength(nickname);
                if (len <= maxNN && !nicknames.contains(nickname)) {
                    nicknames.add(nickname);
                }
            }
        }

        // get old trades
        List<IngameTrade> trades = this.getIngameTrades();
        List<Pokemon> usedRequests = new ArrayList<>();
        List<Pokemon> usedGivens = new ArrayList<>();
        List<String> usedOTs = new ArrayList<>();
        List<String> usedNicknames = new ArrayList<>();
        ItemList possibleItems = this.getAllowedItems();

        int nickCount = nicknames.size();
        int trnameCount = trainerNames.size();

        for (IngameTrade trade : trades) {
            // pick new given pokemon
            Pokemon oldgiven = trade.givenPokemon;
            Pokemon given = this.randomPokemon();
            while (usedGivens.contains(given)) {
                given = this.randomPokemon();
            }
            usedGivens.add(given);
            trade.givenPokemon = given;

            // requested pokemon?
            if (oldgiven == trade.requestedPokemon) {
                // preserve trades for the same pokemon
                trade.requestedPokemon = given;
            } else if (randomizeRequest) {
                if (trade.requestedPokemon != null) {
                    Pokemon request = this.randomPokemon();
                    while (usedRequests.contains(request) || request == given) {
                        request = this.randomPokemon();
                    }
                    usedRequests.add(request);
                    trade.requestedPokemon = request;
                }
            }

            // nickname?
            if (randomNickname && nickCount > usedNicknames.size()) {
                String nickname = nicknames.get(this.random.nextInt(nickCount));
                while (usedNicknames.contains(nickname)) {
                    nickname = nicknames.get(this.random.nextInt(nickCount));
                }
                usedNicknames.add(nickname);
                trade.nickname = nickname;
            } else if (trade.nickname.equalsIgnoreCase(oldgiven.name)) {
                // change the name for sanity
                trade.nickname = trade.givenPokemon.name;
            }

            if (randomOT && trnameCount > usedOTs.size()) {
                String ot = trainerNames.get(this.random.nextInt(trnameCount));
                while (usedOTs.contains(ot)) {
                    ot = trainerNames.get(this.random.nextInt(trnameCount));
                }
                usedOTs.add(ot);
                trade.otName = ot;
                trade.otId = this.random.nextInt(65536);
            }

            if (randomStats) {
                int maxIV = this.hasDVs() ? 16 : 32;
                for (int i = 0; i < trade.ivs.length; i++) {
                    trade.ivs[i] = this.random.nextInt(maxIV);
                }
            }

            if (randomItem) {
                trade.item = possibleItems.randomItem(this.random);
            }
        }

        // things that the game doesn't support should just be ignored
        this.setIngameTrades(trades);
    }

    @Override
    public void condenseLevelEvolutions(int maxLevel, int maxIntermediateLevel) {
        List<Pokemon> allPokemon = this.getPokemon();
        Set<Evolution> changedEvos = new TreeSet<>();
        // search for level evolutions
        for (Pokemon pk : allPokemon) {
            if (pk != null) {
                for (Evolution checkEvo : pk.evolutionsFrom) {
                    if (checkEvo.type.usesLevel()) {
                        // bring down the level of this evo if it exceeds max
                        // level
                        if (checkEvo.extraInfo > maxLevel) {
                            checkEvo.extraInfo = maxLevel;
                            changedEvos.add(checkEvo);
                        }

                        // Now, seperately, if an intermediate level evo is too
                        // high, bring it down
                        if (checkEvo.extraInfo > maxIntermediateLevel && checkEvo.to.evolutionsFrom.size() > 0) {
                            checkEvo.extraInfo = maxIntermediateLevel;
                            changedEvos.add(checkEvo);
                        }
                    }
                    if (checkEvo.type == EvolutionType.LEVEL_UPSIDE_DOWN) {
                        checkEvo.type = EvolutionType.LEVEL;
                        changedEvos.add(checkEvo);
                    }
                }
            }
        }
        // Log changes now that we're done (to avoid repeats)
        log("--Condensed Level Evolutions--");
        for (Evolution evol : changedEvos) {
            log(String.format("%s now evolves into %s at minimum level %d", evol.from.name, evol.toFullName(),
                    evol.extraInfo));
        }
        logBlankLine();

    }

    @Override
    public void randomizeEvolutions(boolean similarStrength, boolean sameType, boolean limitToThreeStages,
                                    boolean forceChange, boolean allowAltFormes, boolean abilitiesAreRandomized) {
        checkPokemonRestrictions();
        List<Pokemon> pokemonPool = new ArrayList<>(mainPokemonListInclFormes);
        List<Pokemon> actuallyCosmeticPokemonPool = new ArrayList<>();
        int stageLimit = limitToThreeStages ? 3 : 10;

        List<Pokemon> banned = this.getBannedFormes();
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }

        for (int i = 0; i < pokemonPool.size(); i++) {
            Pokemon pk = pokemonPool.get(i);
            if (pk.actuallyCosmetic) {
                pokemonPool.remove(pk);
                i--;
                actuallyCosmeticPokemonPool.add(pk);
            }
        }

        // Cache old evolutions for data later
        Map<Pokemon, List<Evolution>> originalEvos = new HashMap<>();
        for (Pokemon pk : pokemonPool) {
            originalEvos.put(pk, new ArrayList<>(pk.evolutionsFrom));
        }

        Set<EvolutionPair> newEvoPairs = new HashSet<>();
        Set<EvolutionPair> oldEvoPairs = new HashSet<>();

        if (forceChange) {
            for (Pokemon pk : pokemonPool) {
                for (Evolution ev : pk.evolutionsFrom) {
                    oldEvoPairs.add(new EvolutionPair(ev.from, ev.to));
                    if (generationOfPokemon() >= 7 && ev.from.number == 790) { // Special case for Cosmoem to add Lunala/Solgaleo since we remove the split evo
                        oldEvoPairs.add(new EvolutionPair(ev.from, pokemonPool.get(ev.from.number + (ev.to.number % 791))));
                    }
                }
            }
        }

        List<Pokemon> replacements = new ArrayList<>();

        int loops = 0;
        while (loops < 1) {
            // Setup for this loop.
            boolean hadError = false;
            for (Pokemon pk : pokemonPool) {
                pk.evolutionsFrom.clear();
                pk.evolutionsTo.clear();
            }
            newEvoPairs.clear();

            // Shuffle pokemon list so the results aren't overly predictable.
            Collections.shuffle(pokemonPool, this.random);

            for (Pokemon fromPK : pokemonPool) {
                List<Evolution> oldEvos = originalEvos.get(fromPK);
                for (Evolution ev : oldEvos) {
                    // Pick a Pokemon as replacement
                    replacements.clear();

                    List<Pokemon> chosenList =
                            allowAltFormes ?
                            mainPokemonListInclFormes
                                    .stream()
                                    .filter(pk -> !pk.actuallyCosmetic)
                                    .collect(Collectors.toList()) :
                            mainPokemonList;
                    // Step 1: base filters
                    for (Pokemon pk : chosenList) {
                        // Prevent evolving into oneself (mandatory)
                        if (pk == fromPK) {
                            continue;
                        }

                        // Force same EXP curve (mandatory)
                        if (pk.growthCurve != fromPK.growthCurve) {
                            continue;
                        }

                        // Prevent evolving into banned Pokemon (mandatory)
                        if (banned.contains(pk)) {
                            continue;
                        }

                        EvolutionPair ep = new EvolutionPair(fromPK, pk);
                        // Prevent split evos choosing the same Pokemon
                        // (mandatory)
                        if (newEvoPairs.contains(ep)) {
                            continue;
                        }

                        // Prevent evolving into old thing if flagged
                        if (forceChange && oldEvoPairs.contains(ep)) {
                            continue;
                        }

                        // Prevent evolution that causes cycle (mandatory)
                        if (evoCycleCheck(fromPK, pk)) {
                            continue;
                        }

                        // Prevent evolution that exceeds stage limit
                        Evolution tempEvo = new Evolution(fromPK, pk, false, EvolutionType.NONE, 0);
                        fromPK.evolutionsFrom.add(tempEvo);
                        pk.evolutionsTo.add(tempEvo);
                        boolean exceededLimit = false;

                        Set<Pokemon> related = relatedPokemon(fromPK);

                        for (Pokemon pk2 : related) {
                            int numPreEvos = numPreEvolutions(pk2, stageLimit);
                            if (numPreEvos >= stageLimit) {
                                exceededLimit = true;
                                break;
                            } else if (numPreEvos == stageLimit - 1 && pk2.evolutionsFrom.size() == 0
                                    && originalEvos.get(pk2).size() > 0) {
                                exceededLimit = true;
                                break;
                            }
                        }

                        fromPK.evolutionsFrom.remove(tempEvo);
                        pk.evolutionsTo.remove(tempEvo);

                        if (exceededLimit) {
                            continue;
                        }

                        // Passes everything, add as a candidate.
                        replacements.add(pk);
                    }

                    // If we don't have any candidates after Step 1, severe
                    // failure
                    // exit out of this loop and try again from scratch
                    if (replacements.size() == 0) {
                        hadError = true;
                        break;
                    }

                    // Step 2: filter by type, if needed
                    if (replacements.size() > 1 && sameType) {
                        Set<Pokemon> includeType = new HashSet<>();
                        for (Pokemon pk : replacements) {
                            // Special case for Eevee
                            if (fromPK.number == 133) {
                                if (pk.primaryType == ev.to.primaryType
                                        || (pk.secondaryType != null) && pk.secondaryType == ev.to.primaryType) {
                                    includeType.add(pk);
                                }
                            } else if (pk.primaryType == fromPK.primaryType
                                    || (fromPK.secondaryType != null && pk.primaryType == fromPK.secondaryType)
                                    || (pk.secondaryType != null && pk.secondaryType == fromPK.primaryType)
                                    || (fromPK.secondaryType != null && pk.secondaryType != null && pk.secondaryType == fromPK.secondaryType)) {
                                includeType.add(pk);
                            }
                        }

                        if (includeType.size() != 0) {
                            replacements.retainAll(includeType);
                        }
                    }

                    if (!alreadyPicked.containsAll(replacements) && !similarStrength) {
                        replacements.removeAll(alreadyPicked);
                    }

                    // Step 3: pick - by similar strength or otherwise
                    Pokemon picked;

                    if (replacements.size() == 1) {
                        // Foregone conclusion.
                        picked = replacements.get(0);
                        alreadyPicked.add(picked);
                    } else if (similarStrength) {
                        picked = pickEvoPowerLvlReplacement(replacements, ev.to);
                        alreadyPicked.add(picked);
                    } else {
                        picked = replacements.get(this.random.nextInt(replacements.size()));
                        alreadyPicked.add(picked);
                    }

                    // Step 4: add it to the new evos pool
                    Evolution newEvo = new Evolution(fromPK, picked, ev.carryStats, ev.type, ev.extraInfo);
                    boolean checkCosmetics = true;
                    if (picked.formeNumber > 0) {
                        newEvo.forme = picked.formeNumber;
                        newEvo.formeSuffix = picked.formeSuffix;
                        checkCosmetics = false;
                    }
                    if (checkCosmetics && newEvo.to.cosmeticForms > 0) {
                        newEvo.forme = newEvo.to.getCosmeticFormNumber(this.random.nextInt(newEvo.to.cosmeticForms));
                    } else if (!checkCosmetics && picked.cosmeticForms > 0) {
                        newEvo.forme += picked.getCosmeticFormNumber(this.random.nextInt(picked.cosmeticForms));
                    }
                    if (newEvo.type == EvolutionType.LEVEL_FEMALE_ESPURR) {
                        newEvo.type = EvolutionType.LEVEL_FEMALE_ONLY;
                    }
                    fromPK.evolutionsFrom.add(newEvo);
                    picked.evolutionsTo.add(newEvo);
                    newEvoPairs.add(new EvolutionPair(fromPK, picked));
                }

                if (hadError) {
                    // No need to check the other Pokemon if we already errored
                    break;
                }
            }

            // If no error, done and return
            if (!hadError) {
                for (Pokemon pk: actuallyCosmeticPokemonPool) {
                    pk.copyBaseFormeEvolutions(pk.baseForme);
                }
                return;
            } else {
                loops++;
            }
        }

        // If we made it out of the loop, we weren't able to randomize evos.
        throw new RandomizationException("Not able to randomize evolutions in a sane amount of retries.");
    }

    @Override
    public void shuffleShopItems() {
        Map<Integer,List<Integer>> currentItems = this.getShopItems();
        if (currentItems == null) return;
        List<Integer> itemList = new ArrayList<>();
        for (List<Integer> shopList: currentItems.values()) {
            itemList.addAll(shopList);
        }
        Collections.shuffle(itemList, this.random);

        Iterator<Integer> itemListIter = itemList.iterator();

        for (List<Integer> shopList: currentItems.values()) {
            for (int i = 0; i < shopList.size(); i++) {
                shopList.remove(i);
                shopList.add(i,itemListIter.next());
            }
        }

        this.setShopItems(currentItems);
    }

    // Note: If you use this on a game where the amount of randomizable shop items is greater than the amount of
    // possible items, you will get owned by the while loop
    @Override
    public void randomizeShopItems(boolean banBadItems, boolean banRegularShopItems, boolean banOPShopItems, boolean balancePrices,
                                   boolean placeEvolutionItems, boolean placeXItems) {
        if (this.getShopItems() == null) return;
        ItemList possibleItems = banBadItems ? this.getNonBadItems() : this.getAllowedItems();
        if (banRegularShopItems) {
            possibleItems.banSingles(this.getRegularShopItems().stream().mapToInt(Integer::intValue).toArray());
        }
        if (banOPShopItems) {
            possibleItems.banSingles(this.getOPShopItems().stream().mapToInt(Integer::intValue).toArray());
        }
        Map<Integer,List<Integer>> currentItems = this.getShopItems();

        int shopItemCount = currentItems.values().stream().mapToInt(List::size).sum();

        List<Integer> newItems = new ArrayList<>();
        Map<Integer,List<Integer>> newItemsMap = new TreeMap<>();
        int newItem;
        List<Integer> guaranteedItems = new ArrayList<>();
        if (placeEvolutionItems) {
            guaranteedItems.addAll(getEvolutionItems());
        }
        if (placeXItems) {
            guaranteedItems.addAll(GlobalConstants.xItems);
        }
        if (placeEvolutionItems || placeXItems) {
            newItems.addAll(guaranteedItems);
            shopItemCount = shopItemCount - newItems.size();

            for (int i = 0; i < shopItemCount; i++) {
                while (newItems.contains(newItem = possibleItems.randomNonTM(this.random)));
                newItems.add(newItem);
            }

            // Guarantee main-game
            List<Integer> mainGameShopsTemp = getMainGameShops();
            List<Integer> mainGameShops = new ArrayList<>();
            List<Integer> nonMainGameShops = new ArrayList<>();
            for (int i: currentItems.keySet()) {
                if (mainGameShopsTemp.contains(i)) {
                    mainGameShops.add(i);
                } else {
                    nonMainGameShops.add(i);
                }
            }

            // Confirms that the main-game shop list gotten from the constants matches the in-game main-game shops
            for (int i: mainGameShopsTemp) {
                if (!mainGameShops.contains(i)) {
                    System.err.println("Discrepancy between main-game shop list in constants and in game: index " + i);
                }
            }

            // Place items in non-main-game shops; skip over guaranteed items
            Collections.shuffle(newItems, this.random);
            for (int i: nonMainGameShops) {
                int j = 0;
                List<Integer> newShopItems = new ArrayList<>();
                for (Integer ignored: currentItems.get(i)) {
                    Integer item = newItems.get(j);
                    while (guaranteedItems.contains(item)) {
                        j++;
                        item = newItems.get(j);
                    }
                    newShopItems.add(item);
                    newItems.remove(item);
                }
                newItemsMap.put(i,newShopItems);
            }

            // Place items in main-game shops
            Collections.shuffle(newItems, this.random);
            for (int i: mainGameShops) {
                List<Integer> newShopItems = new ArrayList<>();
                for (Integer ignored: currentItems.get(i)) {
                    Integer item = newItems.get(0);
                    newShopItems.add(item);
                    newItems.remove(0);
                }
                newItemsMap.put(i,newShopItems);
            }
        } else {
            for (int i = 0; i < shopItemCount; i++) {
                while (newItems.contains(newItem = possibleItems.randomNonTM(this.random)));
                newItems.add(newItem);
            }

            Iterator<Integer> newItemsIter = newItems.iterator();

            for (int i: currentItems.keySet()) {
                List<Integer> newShopItems = new ArrayList<>();
                for (Integer ignored: currentItems.get(i)) {
                    newShopItems.add(newItemsIter.next());
                }
                newItemsMap.put(i,newShopItems);
            }
        }

        this.setShopItems(newItemsMap);
        if (balancePrices) {
            this.setShopPrices();
        }
    }

    @Override
    public void minimumCatchRate(int rateNonLegendary, int rateLegendary) {
        List<Pokemon> pokes = getPokemonInclFormes();
        for (Pokemon pkmn : pokes) {
            if (pkmn == null) {
                continue;
            }
            int minCatchRate = pkmn.isLegendary() ? rateLegendary : rateNonLegendary;
            pkmn.catchRate = Math.max(pkmn.catchRate, minCatchRate);
        }

    }

    @Override
    public void standardizeEXPCurves(Settings.ExpCurveMod mod, ExpCurve expCurve) {
        List<Pokemon> pokes = getPokemonInclFormes();
        switch (mod) {
            case LEGENDARIES:
                for (Pokemon pkmn : pokes) {
                    if (pkmn == null) {
                        continue;
                    }
                    pkmn.growthCurve = pkmn.isLegendary() ? ExpCurve.SLOW : expCurve;
                }
                break;
            case STRONG_LEGENDARIES:
                for (Pokemon pkmn : pokes) {
                    if (pkmn == null) {
                        continue;
                    }
                    pkmn.growthCurve = pkmn.isStrongLegendary() ? ExpCurve.SLOW : expCurve;
                }
                break;
            case ALL:
                for (Pokemon pkmn : pokes) {
                    if (pkmn == null) {
                        continue;
                    }
                    pkmn.growthCurve = expCurve;
                }
                break;
        }
    }

    /* Private methods/structs used internally by the above methods */

    private void updateMovePower(List<Move> moves, int moveNum, int power) {
        Move mv = moves.get(moveNum);
        if (mv.power != power) {
            mv.power = power;
            addMoveUpdate(moveNum, 0);
        }
    }

    private void updateMovePP(List<Move> moves, int moveNum, int pp) {
        Move mv = moves.get(moveNum);
        if (mv.pp != pp) {
            mv.pp = pp;
            addMoveUpdate(moveNum, 1);
        }
    }

    private void updateMoveAccuracy(List<Move> moves, int moveNum, int accuracy) {
        Move mv = moves.get(moveNum);
        if (Math.abs(mv.hitratio - accuracy) >= 1) {
            mv.hitratio = accuracy;
            addMoveUpdate(moveNum, 2);
        }
    }

    private void updateMoveType(List<Move> moves, int moveNum, Type type) {
        Move mv = moves.get(moveNum);
        if (mv.type != type) {
            mv.type = type;
            addMoveUpdate(moveNum, 3);
        }
    }

    private void updateMoveCategory(List<Move> moves, int moveNum, MoveCategory category) {
        Move mv = moves.get(moveNum);
        if (mv.category != category) {
            mv.category = category;
            addMoveUpdate(moveNum, 4);
        }
    }

    private void addMoveUpdate(int moveNum, int updateType) {
        if (!moveUpdates.containsKey(moveNum)) {
            boolean[] updateField = new boolean[5];
            updateField[updateType] = true;
            moveUpdates.put(moveNum, updateField);
        } else {
            moveUpdates.get(moveNum)[updateType] = true;
        }
    }

    private Pokemon pickEvoPowerLvlReplacement(List<Pokemon> pokemonPool, Pokemon current) {
        // start with within 10% and add 5% either direction till we find
        // something
        int currentBST = current.bstForPowerLevels();
        int minTarget = currentBST - currentBST / 10;
        int maxTarget = currentBST + currentBST / 10;
        List<Pokemon> canPick = new ArrayList<>();
        List<Pokemon> emergencyPick = new ArrayList<>();
        int expandRounds = 0;
        while (canPick.isEmpty() || (canPick.size() < 3 && expandRounds < 3)) {
            for (Pokemon pk : pokemonPool) {
                if (pk.bstForPowerLevels() >= minTarget && pk.bstForPowerLevels() <= maxTarget && !canPick.contains(pk) && !emergencyPick.contains(pk)) {
                    if (alreadyPicked.contains(pk)) {
                        emergencyPick.add(pk);
                    } else {
                        canPick.add(pk);
                    }
                }
            }
            if (expandRounds >= 2 && canPick.isEmpty()) {
                canPick.addAll(emergencyPick);
            }
            minTarget -= currentBST / 20;
            maxTarget += currentBST / 20;
            expandRounds++;
        }
        return canPick.get(this.random.nextInt(canPick.size()));
    }

    // Note that this is slow and somewhat hacky.
    private Pokemon findPokemonInPoolWithSpeciesID(List<Pokemon> pokemonPool, int speciesID) {
        for (int i = 0; i < pokemonPool.size(); i++) {
            if (pokemonPool.get(i).number == speciesID) {
                return pokemonPool.get(i);
            }
        }
        return null;
    }

    private static class EvolutionPair {
        private Pokemon from;
        private Pokemon to;

        EvolutionPair(Pokemon from, Pokemon to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((from == null) ? 0 : from.hashCode());
            result = prime * result + ((to == null) ? 0 : to.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            EvolutionPair other = (EvolutionPair) obj;
            if (from == null) {
                if (other.from != null)
                    return false;
            } else if (!from.equals(other.from))
                return false;
            if (to == null) {
                return other.to == null;
            } else return to.equals(other.to);
        }
    }

    /**
     * Check whether adding an evolution from one Pokemon to another will cause
     * an evolution cycle.
     * 
     * @param from Pokemon that is evolving
     * @param to Pokemon to evolve to
     * @return True if there is an evolution cycle, else false
     */
    private boolean evoCycleCheck(Pokemon from, Pokemon to) {
        Evolution tempEvo = new Evolution(from, to, false, EvolutionType.NONE, 0);
        from.evolutionsFrom.add(tempEvo);
        Set<Pokemon> visited = new HashSet<>();
        Set<Pokemon> recStack = new HashSet<>();
        boolean recur = isCyclic(from, visited, recStack);
        from.evolutionsFrom.remove(tempEvo);
        return recur;
    }

    private boolean isCyclic(Pokemon pk, Set<Pokemon> visited, Set<Pokemon> recStack) {
        if (!visited.contains(pk)) {
            visited.add(pk);
            recStack.add(pk);
            for (Evolution ev : pk.evolutionsFrom) {
                if (!visited.contains(ev.to) && isCyclic(ev.to, visited, recStack)) {
                    return true;
                } else if (recStack.contains(ev.to)) {
                    return true;
                }
            }
        }
        recStack.remove(pk);
        return false;
    }

    private interface BasePokemonAction {
        void applyTo(Pokemon pk);
    }

    private interface EvolvedPokemonAction {
        void applyTo(Pokemon evFrom, Pokemon evTo, boolean toMonIsFinalEvo);
    }

    private interface CosmeticFormAction {
        void applyTo(Pokemon pk, Pokemon baseForme);
    }

    /**
     * Universal implementation for things that have "copy X up evolutions"
     * support.
     *  @param bpAction
     *            Method to run on all base or no-copy Pokemon
     * @param epAction
     *            Method to run on all evolved Pokemon with a linear chain of
     * @param dontCopySplitEvos
     *            If true, treat split evolutions the same way as base Pokemon
     */
    private void copyUpEvolutionsHelper(BasePokemonAction bpAction, EvolvedPokemonAction epAction,
                                        boolean dontCopySplitEvos) {
        List<Pokemon> allPokes = this.getPokemonInclFormes();
        for (Pokemon pk : allPokes) {
            if (pk != null) {
                pk.temporaryFlag = false;
            }
        }

        // Get evolution data.
        Set<Pokemon> dontCopyPokes = RomFunctions.getBasicOrNoCopyPokemon(this, dontCopySplitEvos);
        Set<Pokemon> middleEvos = RomFunctions.getMiddleEvolutions(this, !dontCopySplitEvos);

        for (Pokemon pk : dontCopyPokes) {
            bpAction.applyTo(pk);
            pk.temporaryFlag = true;
        }

        // go "up" evolutions looking for pre-evos to do first
        for (Pokemon pk : allPokes) {
            if (pk != null && !pk.temporaryFlag) {

                // Non-randomized pokes at this point must have
                // a linear chain of single evolutions down to
                // a randomized poke.
                Stack<Evolution> currentStack = new Stack<>();
                Evolution ev = pk.evolutionsTo.get(0);
                while (!ev.from.temporaryFlag) {
                    currentStack.push(ev);
                    ev = ev.from.evolutionsTo.get(0);
                }

                // Now "ev" is set to an evolution from a Pokemon that has had
                // the base action done on it to one that hasn't.
                // Do the evolution action for everything left on the stack.
                epAction.applyTo(ev.from, ev.to, !middleEvos.contains(ev.to));
                ev.to.temporaryFlag = true;
                while (!currentStack.isEmpty()) {
                    ev = currentStack.pop();
                    epAction.applyTo(ev.from, ev.to, !middleEvos.contains(ev.to));
                    ev.to.temporaryFlag = true;
                }

            }
        }
    }

    private void copyUpEvolutionsHelper(BasePokemonAction bpAction, EvolvedPokemonAction epAction) {
        copyUpEvolutionsHelper(bpAction, epAction, true);
    }

    private boolean checkForUnusedMove(List<Move> potentialList, Set<Integer> alreadyUsed) {
        for (Move mv : potentialList) {
            if (!alreadyUsed.contains(mv.number)) {
                return true;
            }
        }
        return false;
    }

    private List<Pokemon> pokemonOfType(Type type, boolean noLegendaries) {
        List<Pokemon> typedPokes = new ArrayList<>();
        for (Pokemon pk : mainPokemonList) {
            if (pk != null && (!noLegendaries || !pk.isLegendary()) && !pk.actuallyCosmetic) {
                if (pk.primaryType == type || pk.secondaryType == type) {
                    typedPokes.add(pk);
                }
            }
        }
        return typedPokes;
    }

    private List<Pokemon> pokemonOfTypeInclFormes(Type type, boolean noLegendaries) {
        List<Pokemon> typedPokes = new ArrayList<>();
        for (Pokemon pk : mainPokemonListInclFormes) {
            if (pk != null && (!noLegendaries || !pk.isLegendary())) {
                if (pk.primaryType == type || pk.secondaryType == type) {
                    typedPokes.add(pk);
                }
            }
        }
        return typedPokes;
    }

    private List<Pokemon> allPokemonWithoutNull() {
        List<Pokemon> allPokes = new ArrayList<>(this.getPokemon());
        allPokes.remove(0);
        return allPokes;
    }

    private List<Pokemon> allPokemonInclFormesWithoutNull() {
        List<Pokemon> allPokes = new ArrayList<>(this.getPokemonInclFormes());
        allPokes.remove(0);
        return allPokes;
    }

    private Set<Pokemon> pokemonInArea(EncounterSet area) {
        Set<Pokemon> inArea = new TreeSet<>();
        for (Encounter enc : area.encounters) {
            inArea.add(enc.pokemon);
        }
        return inArea;
    }

    private Map<Type, Integer> typeWeightings;
    private int totalTypeWeighting;

    private Type pickType(boolean weightByFrequency, boolean noLegendaries, boolean allowAltFormes) {
        if (totalTypeWeighting == 0) {
            // Determine weightings
            for (Type t : Type.values()) {
                if (typeInGame(t)) {
                    List<Pokemon> pokemonOfType = allowAltFormes ? pokemonOfTypeInclFormes(t, noLegendaries) :
                            pokemonOfType(t, noLegendaries);
                    int pkWithTyping = pokemonOfType.size();
                    typeWeightings.put(t, pkWithTyping);
                    totalTypeWeighting += pkWithTyping;
                }
            }
        }

        if (weightByFrequency) {
            int typePick = this.random.nextInt(totalTypeWeighting);
            int typePos = 0;
            for (Type t : typeWeightings.keySet()) {
                int weight = typeWeightings.get(t);
                if (typePos + weight > typePick) {
                    return t;
                }
                typePos += weight;
            }
            return null;
        } else {
            return randomType();
        }
    }

    private void rivalCarriesStarterUpdate(List<Trainer> currentTrainers, String prefix, int pokemonOffset) {
        // Find the highest rival battle #
        int highestRivalNum = 0;
        for (Trainer t : currentTrainers) {
            if (t.tag != null && t.tag.startsWith(prefix)) {
                highestRivalNum = Math.max(highestRivalNum,
                        Integer.parseInt(t.tag.substring(prefix.length(), t.tag.indexOf('-'))));
            }
        }

        if (highestRivalNum == 0) {
            // This rival type not used in this game
            return;
        }

        // Get the starters
        // us 0 1 2 => them 0+n 1+n 2+n
        List<Pokemon> starters = this.getStarters();

        // Yellow needs its own case, unfortunately.
        if (isYellow()) {
            // The rival's starter is index 1
            Pokemon rivalStarter = starters.get(1);
            int timesEvolves = numEvolutions(rivalStarter, 2);
            // Apply evolutions as appropriate
            if (timesEvolves == 0) {
                for (int j = 1; j <= 3; j++) {
                    changeStarterWithTag(currentTrainers, prefix + j + "-0", rivalStarter);
                }
                for (int j = 4; j <= 7; j++) {
                    for (int i = 0; i < 3; i++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, rivalStarter);
                    }
                }
            } else if (timesEvolves == 1) {
                for (int j = 1; j <= 3; j++) {
                    changeStarterWithTag(currentTrainers, prefix + j + "-0", rivalStarter);
                }
                rivalStarter = pickRandomEvolutionOf(rivalStarter, false);
                for (int j = 4; j <= 7; j++) {
                    for (int i = 0; i < 3; i++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, rivalStarter);
                    }
                }
            } else if (timesEvolves == 2) {
                for (int j = 1; j <= 2; j++) {
                    changeStarterWithTag(currentTrainers, prefix + j + "-" + 0, rivalStarter);
                }
                rivalStarter = pickRandomEvolutionOf(rivalStarter, true);
                changeStarterWithTag(currentTrainers, prefix + "3-0", rivalStarter);
                for (int i = 0; i < 3; i++) {
                    changeStarterWithTag(currentTrainers, prefix + "4-" + i, rivalStarter);
                }
                rivalStarter = pickRandomEvolutionOf(rivalStarter, false);
                for (int j = 5; j <= 7; j++) {
                    for (int i = 0; i < 3; i++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, rivalStarter);
                    }
                }
            }
        } else {
            // Replace each starter as appropriate
            // Use level to determine when to evolve, not number anymore
            for (int i = 0; i < 3; i++) {
                // Rival's starters are pokemonOffset over from each of ours
                int starterToUse = (i + pokemonOffset) % 3;
                Pokemon thisStarter = starters.get(starterToUse);
                int timesEvolves = numEvolutions(thisStarter, 2);
                // If a fully evolved pokemon, use throughout
                // Otherwise split by evolutions as appropriate
                if (timesEvolves == 0) {
                    for (int j = 1; j <= highestRivalNum; j++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter);
                    }
                } else if (timesEvolves == 1) {
                    int j = 1;
                    for (; j <= highestRivalNum / 2; j++) {
                        if (getLevelOfStarter(currentTrainers, prefix + j + "-" + i) >= 30) {
                            break;
                        }
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter);
                    }
                    thisStarter = pickRandomEvolutionOf(thisStarter, false);
                    for (; j <= highestRivalNum; j++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter);
                    }
                } else if (timesEvolves == 2) {
                    int j = 1;
                    for (; j <= highestRivalNum; j++) {
                        if (getLevelOfStarter(currentTrainers, prefix + j + "-" + i) >= 16) {
                            break;
                        }
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter);
                    }
                    thisStarter = pickRandomEvolutionOf(thisStarter, true);
                    for (; j <= highestRivalNum; j++) {
                        if (getLevelOfStarter(currentTrainers, prefix + j + "-" + i) >= 36) {
                            break;
                        }
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter);
                    }
                    thisStarter = pickRandomEvolutionOf(thisStarter, false);
                    for (; j <= highestRivalNum; j++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter);
                    }
                }
            }
        }

    }

    private Pokemon pickRandomEvolutionOf(Pokemon base, boolean mustEvolveItself) {
        // Used for "rival carries starter"
        // Pick a random evolution of base Pokemon, subject to
        // "must evolve itself" if appropriate.
        List<Pokemon> candidates = new ArrayList<>();
        for (Evolution ev : base.evolutionsFrom) {
            if (!mustEvolveItself || ev.to.evolutionsFrom.size() > 0) {
                candidates.add(ev.to);
            }
        }

        if (candidates.size() == 0) {
            throw new RandomizationException("Random evolution called on a Pokemon without any usable evolutions.");
        }

        return candidates.get(random.nextInt(candidates.size()));
    }

    private int getLevelOfStarter(List<Trainer> currentTrainers, String tag) {
        for (Trainer t : currentTrainers) {
            if (t.tag != null && t.tag.equals(tag)) {
                // Bingo, get highest level
                // last pokemon is given priority +2 but equal priority
                // = first pokemon wins, so its effectively +1
                // If it's tagged the same we can assume it's the same team
                // just the opposite gender or something like that...
                // So no need to check other trainers with same tag.
                int highestLevel = t.pokemon.get(0).level;
                int trainerPkmnCount = t.pokemon.size();
                for (int i = 1; i < trainerPkmnCount; i++) {
                    int levelBonus = (i == trainerPkmnCount - 1) ? 2 : 0;
                    if (t.pokemon.get(i).level + levelBonus > highestLevel) {
                        highestLevel = t.pokemon.get(i).level;
                    }
                }
                return highestLevel;
            }
        }
        return 0;
    }

    private void changeStarterWithTag(List<Trainer> currentTrainers, String tag, Pokemon starter) {
        for (Trainer t : currentTrainers) {
            if (t.tag != null && t.tag.equals(tag)) {

                // Bingo
                TrainerPokemon bestPoke = t.pokemon.get(0);

                if (t.forceStarterPosition >= 0) {
                    bestPoke = t.pokemon.get(t.forceStarterPosition);
                } else {
                    // Change the highest level pokemon, not the last.
                    // BUT: last gets +2 lvl priority (effectively +1)
                    // same as above, equal priority = earlier wins
                    int trainerPkmnCount = t.pokemon.size();
                    for (int i = 1; i < trainerPkmnCount; i++) {
                        int levelBonus = (i == trainerPkmnCount - 1) ? 2 : 0;
                        if (t.pokemon.get(i).level + levelBonus > bestPoke.level) {
                            bestPoke = t.pokemon.get(i);
                        }
                    }
                }
                bestPoke.pokemon = starter;
                bestPoke.absolutePokeNumber = starter.number;
                setFormeForTrainerPokemon(bestPoke,starter);
                bestPoke.resetMoves = true;
            }
        }

    }

    // Return the max depth of pre-evolutions a Pokemon has
    private int numPreEvolutions(Pokemon pk, int maxInterested) {
        return numPreEvolutions(pk, 0, maxInterested);
    }

    private int numPreEvolutions(Pokemon pk, int depth, int maxInterested) {
        if (pk.evolutionsTo.size() == 0) {
            return 0;
        } else {
            if (depth == maxInterested - 1) {
                return 1;
            } else {
                int maxPreEvos = 0;
                for (Evolution ev : pk.evolutionsTo) {
                    maxPreEvos = Math.max(maxPreEvos, numPreEvolutions(ev.from, depth + 1, maxInterested) + 1);
                }
                return maxPreEvos;
            }
        }
    }

    private int numEvolutions(Pokemon pk, int maxInterested) {
        return numEvolutions(pk, 0, maxInterested);
    }

    private int numEvolutions(Pokemon pk, int depth, int maxInterested) {
        if (pk.evolutionsFrom.size() == 0) {
            return 0;
        } else {
            if (depth == maxInterested - 1) {
                return 1;
            } else {
                int maxEvos = 0;
                for (Evolution ev : pk.evolutionsFrom) {
                    maxEvos = Math.max(maxEvos, numEvolutions(ev.to, depth + 1, maxInterested) + 1);
                }
                return maxEvos;
            }
        }
    }

    private Pokemon fullyEvolve(Pokemon pokemon) {
        Set<Pokemon> seenMons = new HashSet<>();
        seenMons.add(pokemon);

        while (true) {
            if (pokemon.evolutionsFrom.size() == 0) {
                // fully evolved
                break;
            }

            // check for cyclic evolutions from what we've already seen
            boolean cyclic = false;
            for (Evolution ev : pokemon.evolutionsFrom) {
                if (seenMons.contains(ev.to)) {
                    // cyclic evolution detected - bail now
                    cyclic = true;
                    break;
                }
            }

            if (cyclic) {
                break;
            }

            // pick a random evolution to continue from
            pokemon = pokemon.evolutionsFrom.get(random.nextInt(pokemon.evolutionsFrom.size())).to;
            seenMons.add(pokemon);
        }

        return pokemon;
    }

    private Set<Pokemon> relatedPokemon(Pokemon original) {
        Set<Pokemon> results = new HashSet<>();
        results.add(original);
        Queue<Pokemon> toCheck = new LinkedList<>();
        toCheck.add(original);
        while (!toCheck.isEmpty()) {
            Pokemon check = toCheck.poll();
            for (Evolution ev : check.evolutionsFrom) {
                if (!results.contains(ev.to)) {
                    results.add(ev.to);
                    toCheck.add(ev.to);
                }
            }
            for (Evolution ev : check.evolutionsTo) {
                if (!results.contains(ev.from)) {
                    results.add(ev.from);
                    toCheck.add(ev.from);
                }
            }
        }
        return results;
    }

    private Map<Type, List<Pokemon>> cachedReplacementLists;
    private List<Pokemon> cachedAllList;

    private Pokemon pickReplacement(Pokemon current, boolean usePowerLevels, Type type, boolean noLegendaries,
                                    boolean wonderGuardAllowed, boolean usePlacementHistory, boolean swapMegaEvos,
                                    boolean abilitiesAreRandomized, boolean allowAltFormes) {
        List<Pokemon> pickFrom;
        if (swapMegaEvos) {
            pickFrom = megaEvolutionsList
                    .stream()
                    .filter(mega -> mega.method == 1)
                    .map(mega -> mega.from)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (usePlacementHistory) {
            pickFrom = getBelowAveragePlacements();
        } else {
            pickFrom = cachedAllList;
        }
        if (type != null) {
            if (!cachedReplacementLists.containsKey(type)) {
//                System.out.println(current.name + " using cachedReplacementLists");
                List<Pokemon> pokemonOfType = allowAltFormes ? pokemonOfTypeInclFormes(type, noLegendaries) :
                        pokemonOfType(type, noLegendaries);
                pokemonOfType.removeAll(this.getBannedFormes());
                if (!abilitiesAreRandomized) {
                    List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
                    pokemonOfType.removeAll(abilityDependentFormes);
                }
                cachedReplacementLists.put(type, pokemonOfType);
            }
            if (swapMegaEvos) {
                pickFrom = cachedReplacementLists.get(type)
                        .stream()
                        .filter(pickFrom::contains)
                        .collect(Collectors.toList());
                if (pickFrom.isEmpty()) {
                    pickFrom = cachedReplacementLists.get(type);
                }
            } else {
                pickFrom = cachedReplacementLists.get(type);
            }
        }

        if (usePowerLevels) {
            // start with within 10% and add 5% either direction till we find
            // something
            int currentBST = current.bstForPowerLevels();
            int minTarget = currentBST - currentBST / 10;
            int maxTarget = currentBST + currentBST / 10;
            List<Pokemon> canPick = new ArrayList<>();
            int expandRounds = 0;
            while (canPick.isEmpty() || (canPick.size() < 3 && expandRounds < 2)) {
                for (Pokemon pk : pickFrom) {
                    if (pk.bstForPowerLevels() >= minTarget
                            && pk.bstForPowerLevels() <= maxTarget
                            && (wonderGuardAllowed || (pk.ability1 != GlobalConstants.WONDER_GUARD_INDEX
                                    && pk.ability2 != GlobalConstants.WONDER_GUARD_INDEX && pk.ability3 != GlobalConstants.WONDER_GUARD_INDEX))) {
                        canPick.add(pk);
                    }
                }
                minTarget -= currentBST / 20;
                maxTarget += currentBST / 20;
                expandRounds++;
            }
            // If usePlacementHistory is True, then we need to do some
            // extra checking to make sure the randomly chosen pokemon
            // is actually below the current average placement
            // if not, re-roll

            Pokemon chosenPokemon = canPick.get(this.random.nextInt(canPick.size()));
            if (usePlacementHistory) {
//                   System.out.println("Pokemon: "+ chosenPokemon.name + " placement history: " + getPlacementHistory(chosenPokemon) + " current average: " + getPlacementAverage());
               int breaknum = 0;
               while (getPlacementHistory(chosenPokemon) > getPlacementAverage() && breaknum < 100) {
//                   System.out.println(">> Pokemon: "+ chosenPokemon.name + " exceed threshold, rerolling");
                   chosenPokemon = canPick.get(this.random.nextInt(canPick.size()));
//                   System.out.println(">> NEW Pokemon: "+ chosenPokemon.name + " placement history: " + getPlacementHistory(chosenPokemon) + " current average: " + getPlacementAverage());
                   breaknum += 1;
               }
            }
            return chosenPokemon;
        } else {
            if (wonderGuardAllowed) {
                return pickFrom.get(this.random.nextInt(pickFrom.size()));
            } else {
                Pokemon pk = pickFrom.get(this.random.nextInt(pickFrom.size()));
                while (pk.ability1 == GlobalConstants.WONDER_GUARD_INDEX
                        || pk.ability2 == GlobalConstants.WONDER_GUARD_INDEX
                        || pk.ability3 == GlobalConstants.WONDER_GUARD_INDEX) {
                    pk = pickFrom.get(this.random.nextInt(pickFrom.size()));
                }
                return pk;
            }
        }
    }

    private Pokemon pickWildPowerLvlReplacement(List<Pokemon> pokemonPool, Pokemon current, boolean banSamePokemon,
            List<Pokemon> usedUp, int bstBalanceLevel) {
        // start with within 10% and add 5% either direction till we find
        // something
        int balancedBST = bstBalanceLevel * 10 + 250;
        int currentBST = Math.min(current.bstForPowerLevels(), balancedBST);
        int minTarget = currentBST - currentBST / 10;
        int maxTarget = currentBST + currentBST / 10;
        List<Pokemon> canPick = new ArrayList<>();
        int expandRounds = 0;
        while (canPick.isEmpty() || (canPick.size() < 3 && expandRounds < 3)) {
            for (Pokemon pk : pokemonPool) {
                if (pk.bstForPowerLevels() >= minTarget && pk.bstForPowerLevels() <= maxTarget
                        && (!banSamePokemon || pk != current) && (usedUp == null || !usedUp.contains(pk))
                        && !canPick.contains(pk)) {
                    canPick.add(pk);
                }
            }
            minTarget -= currentBST / 20;
            maxTarget += currentBST / 20;
            expandRounds++;
        }
        return canPick.get(this.random.nextInt(canPick.size()));
    }

    private void setFormeForEncounter(Encounter enc, Pokemon pk) {
        boolean checkCosmetics = true;
        enc.formeNumber = 0;
        if (enc.pokemon.formeNumber > 0) {
            enc.formeNumber = enc.pokemon.formeNumber;
            enc.pokemon = enc.pokemon.baseForme;
            checkCosmetics = false;
        }
        if (checkCosmetics && enc.pokemon.cosmeticForms > 0) {
            enc.formeNumber = enc.pokemon.getCosmeticFormNumber(this.random.nextInt(enc.pokemon.cosmeticForms));
        } else if (!checkCosmetics && pk.cosmeticForms > 0) {
            enc.formeNumber += pk.getCosmeticFormNumber(this.random.nextInt(pk.cosmeticForms));
        }
    }

    private Map<Integer, List<EncounterSet>> mapZonesToEncounters(List<EncounterSet> encountersForAreas) {
        Map<Integer, List<EncounterSet>> zonesToEncounters = new TreeMap<>();
        for (EncounterSet encountersInArea : encountersForAreas) {
            if (zonesToEncounters.containsKey(encountersInArea.offset)) {
                zonesToEncounters.get(encountersInArea.offset).add(encountersInArea);
            } else {
                List<EncounterSet> encountersForZone = new ArrayList<>();
                encountersForZone.add(encountersInArea);
                zonesToEncounters.put(encountersInArea.offset, encountersForZone);
            }
        }
        return zonesToEncounters;
    }

    public Pokemon pickEntirelyRandomPokemon(boolean includeFormes, boolean noLegendaries, EncounterSet area, List<Pokemon> banned) {
        Pokemon result;
        Pokemon randomNonLegendaryPokemon = includeFormes ? randomNonLegendaryPokemonInclFormes() : randomNonLegendaryPokemon();
        Pokemon randomPokemon = includeFormes ? randomPokemonInclFormes() : randomPokemon();
        result = noLegendaries ? randomNonLegendaryPokemon : randomPokemon;
        while (result.actuallyCosmetic) {
            randomNonLegendaryPokemon = includeFormes ? randomNonLegendaryPokemonInclFormes() : randomNonLegendaryPokemon();
            randomPokemon = includeFormes ? randomPokemonInclFormes() : randomPokemon();
            result = noLegendaries ? randomNonLegendaryPokemon : randomPokemon;
        }
        while (banned.contains(result) || area.bannedPokemon.contains(result)) {
            randomNonLegendaryPokemon = includeFormes ? randomNonLegendaryPokemonInclFormes() : randomNonLegendaryPokemon();
            randomPokemon = includeFormes ? randomPokemonInclFormes() : randomPokemon();
            result = noLegendaries ? randomNonLegendaryPokemon : randomPokemon;
            while (result.actuallyCosmetic) {
                randomNonLegendaryPokemon = includeFormes ? randomNonLegendaryPokemonInclFormes() : randomNonLegendaryPokemon();
                randomPokemon = includeFormes ? randomPokemonInclFormes() : randomPokemon();
                result = noLegendaries ? randomNonLegendaryPokemon : randomPokemon;
            }
        }
        return result;
    }

    private Pokemon pickStaticPowerLvlReplacement(List<Pokemon> pokemonPool, Pokemon current, boolean banSamePokemon,
                                                List<Pokemon> usedUp, boolean limitBST) {
        // start with within 10% and add 5% either direction till we find
        // something
        int currentBST = current.bstForPowerLevels();
        int minTarget = limitBST ? currentBST - currentBST / 5 : currentBST - currentBST / 10;
        int maxTarget = limitBST ? currentBST : currentBST + currentBST / 10;
        List<Pokemon> canPick = new ArrayList<>();
        int expandRounds = 0;
        while (canPick.isEmpty() || (canPick.size() < 3 && expandRounds < 3)) {
            for (Pokemon pk : pokemonPool) {
                if (pk.bstForPowerLevels() >= minTarget && pk.bstForPowerLevels() <= maxTarget
                        && (!banSamePokemon || pk != current) && (usedUp == null || !usedUp.contains(pk))
                        && !canPick.contains(pk)) {
                    canPick.add(pk);
                }
            }
            minTarget -= currentBST / 20;
            maxTarget += currentBST / 20;
            expandRounds++;
        }
        return canPick.get(this.random.nextInt(canPick.size()));
    }

    @Override
    public List<Pokemon> getAbilityDependentFormes() {
        List<Pokemon> abilityDependentFormes = new ArrayList<>();
        for (int i = 0; i < mainPokemonListInclFormes.size(); i++) {
            Pokemon pokemon = mainPokemonListInclFormes.get(i);
            if (pokemon.baseForme != null) {
                if (pokemon.baseForme.number == 351) {
                    // All alternate Castform formes
                    abilityDependentFormes.add(pokemon);
                } else if (pokemon.baseForme.number == 555 && pokemon.formeNumber == 1) {
                    // Damanitan-Z
                    abilityDependentFormes.add(pokemon);
                } else if (pokemon.baseForme.number == 681) {
                    // Aegislash-B
                    abilityDependentFormes.add(pokemon);
                } else if (pokemon.baseForme.number == 746) {
                    // Wishiwashi-S
                    abilityDependentFormes.add(pokemon);
                }
            }
        }
        return abilityDependentFormes;
    }

    @Override
    public List<Pokemon> getBannedFormes() {
        List<Pokemon> bannedFormes = new ArrayList<>();
        for (int i = 0; i < mainPokemonListInclFormes.size(); i++) {
            Pokemon pokemon = mainPokemonListInclFormes.get(i);
            if (pokemon.baseForme != null) {
                if (pokemon.baseForme.number == 487) {
                    // Giratina-O is banned because it reverts back to Altered Forme if
                    // equipped with any item that isn't the Griseous Orb.
                    bannedFormes.add(pokemon);
                } else if (pokemon.baseForme.number == 492) {
                    // Shaymin-S is banned because it reverts back to its original forme
                    // under a variety of circumstances, and can only be changed back
                    // with the Gracidea.
                    bannedFormes.add(pokemon);
                }
            }
        }
        return bannedFormes;
    }

    @Override
    public void randomizeTotemPokemon(boolean randomizeTotem, boolean similarStrengthTotem, boolean randomizeAllies,
                                      boolean similarStrengthAllies, boolean randomizeAuras,
                                      boolean similarStrengthAuras, boolean randomizeHeldItems, int levelModifier,
                                      boolean allowAltFormes, boolean abilitiesAreRandomized) {
        checkPokemonRestrictions();
        List<TotemPokemon> currentTotemPokemon = this.getTotemPokemon();
        List<TotemPokemon> replacements = new ArrayList<>();
        List<Pokemon> banned = this.bannedForStaticPokemon();
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        List<Pokemon> listInclFormesExclCosmetics =
                mainPokemonListInclFormes
                        .stream()
                        .filter(pk -> !pk.actuallyCosmetic)
                        .collect(Collectors.toList());
        List<Pokemon> pokemonLeft = new ArrayList<>(!allowAltFormes ? mainPokemonList : listInclFormesExclCosmetics);
        pokemonLeft.removeAll(banned);
        for (TotemPokemon old : currentTotemPokemon) {
            TotemPokemon newTotem = new TotemPokemon();
            newTotem.heldItem = old.heldItem;
            if (randomizeTotem) {
                Pokemon newPK;
                Pokemon oldPK = old.pkmn;
                if (old.forme > 0) {
                    oldPK = getAltFormeOfPokemon(oldPK, old.forme);
                }

                if (similarStrengthTotem) {
                    newPK = pickStaticPowerLvlReplacement(
                            pokemonLeft,
                            oldPK,
                            true,
                            replacements.stream().map(enc -> enc.pkmn).collect(Collectors.toList()),
                            false);
                } else {
                    newPK = pokemonLeft.remove(this.random.nextInt(pokemonLeft.size()));
                }

                pokemonLeft.remove(newPK);
                newTotem.pkmn = newPK;
                setFormeForStaticEncounter(newTotem, newPK);
                newTotem.resetMoves = true;
                newTotem.level = old.level;

                if (levelModifier != 0) {
                    newTotem.level = Math.min(100, (int) Math.round(newTotem.level * (1 + levelModifier / 100.0)));
                }
                if (pokemonLeft.size() == 0) {
                    pokemonLeft.addAll(!allowAltFormes ? mainPokemonList : listInclFormesExclCosmetics);
                    pokemonLeft.removeAll(banned);
                }
            } else {
                newTotem.pkmn = old.pkmn;
                newTotem.level = old.level;
                if (levelModifier != 0) {
                    newTotem.level = Math.min(100, (int) Math.round(newTotem.level * (1 + levelModifier / 100.0)));
                }
                setFormeForStaticEncounter(newTotem, newTotem.pkmn);
            }

            if (randomizeAllies) {
                for (Integer oldAllyIndex: old.allies.keySet()) {
                    StaticEncounter oldAlly = old.allies.get(oldAllyIndex);
                    StaticEncounter newAlly = new StaticEncounter();
                    Pokemon newAllyPK;
                    Pokemon oldAllyPK = oldAlly.pkmn;
                    if (oldAlly.forme > 0) {
                        oldAllyPK = getAltFormeOfPokemon(oldAllyPK, oldAlly.forme);
                    }
                    if (similarStrengthAllies) {
                        newAllyPK = pickStaticPowerLvlReplacement(
                                pokemonLeft,
                                oldAllyPK,
                                true,
                                replacements.stream().map(enc -> enc.pkmn).collect(Collectors.toList()),
                                false);
                    } else {
                        newAllyPK = pokemonLeft.remove(this.random.nextInt(pokemonLeft.size()));
                    }

                    pokemonLeft.remove(newAllyPK);
                    newAlly.pkmn = newAllyPK;
                    setFormeForStaticEncounter(newAlly, newAllyPK);
                    newAlly.resetMoves = true;
                    newAlly.level = oldAlly.level;
                    if (levelModifier != 0) {
                        newAlly.level = Math.min(100, (int) Math.round(newAlly.level * (1 + levelModifier / 100.0)));
                    }

                    newTotem.allies.put(oldAllyIndex,newAlly);
                    if (pokemonLeft.size() == 0) {
                        pokemonLeft.addAll(!allowAltFormes ? mainPokemonList : listInclFormesExclCosmetics);
                        pokemonLeft.removeAll(banned);
                    }
                }
            } else {
                newTotem.allies = old.allies;
                for (StaticEncounter ally: newTotem.allies.values()) {
                    if (levelModifier != 0) {
                        ally.level = Math.min(100, (int) Math.round(ally.level * (1 + levelModifier / 100.0)));
                        setFormeForStaticEncounter(ally, ally.pkmn);
                    }
                }
            }

            if (randomizeAuras) {
                if (similarStrengthAuras) {
                    newTotem.aura = Aura.randomAuraSimilarStrength(this.random, old.aura);
                } else {
                    newTotem.aura = Aura.randomAura(this.random);
                }
            } else {
                newTotem.aura = old.aura;
            }

            if (randomizeHeldItems) {
                if (old.heldItem != 0) {
                    newTotem.heldItem = randomHeldItem();
                }
            }

            replacements.add(newTotem);
        }

        // Save
        this.setTotemPokemon(replacements);
    }

    /* Helper methods used by subclasses and/or this class */

    void checkPokemonRestrictions() {
        if (!restrictionsSet) {
            setPokemonPool(null);
        }
    }

    protected void applyCamelCaseNames() {
        List<Pokemon> pokes = getPokemon();
        for (Pokemon pkmn : pokes) {
            if (pkmn == null) {
                continue;
            }
            pkmn.name = RomFunctions.camelCase(pkmn.name);
        }

    }
    
    private void setPlacementHistory(Pokemon newPK) {
        Integer history = getPlacementHistory(newPK);
//         System.out.println("Current history: " + newPK.name + " : " + history);
        placementHistory.put(newPK, history + 1);        
    }

    private int getPlacementHistory(Pokemon newPK) {
        List<Pokemon> placedPK = new ArrayList<>(placementHistory.keySet());
        if (placedPK.contains(newPK)) {
            return placementHistory.get(newPK);
        }
        else {
            return 0;
        }        
    }

    private float getPlacementAverage() {
        List<Pokemon> placedPK = new ArrayList<>(placementHistory.keySet());
        int placedPKNum = 0;
        for (Pokemon p : placedPK) {
            placedPKNum += placementHistory.get(p); 
        }
        return (float)placedPKNum / (float)placedPK.size();
        }

        
    private List<Pokemon> getBelowAveragePlacements() {
        // This method will return a PK if the number of times a pokemon has been
        // placed is less than average of all placed pokemon's appearances
        // E.g., Charmander's been placed once, but the average for all pokemon is 2.2
        // So add to list and return 
        
        List<Pokemon> toPlacePK = new ArrayList<>();
        List<Pokemon> placedPK = new ArrayList<>(placementHistory.keySet());
        List<Pokemon> allPK = cachedAllList;
        int placedPKNum = 0;
        for (Pokemon p : placedPK) {
            placedPKNum += placementHistory.get(p); 
        }
        float placedAverage = Math.round((float)placedPKNum / (float)placedPK.size());

        
        
        if (placedAverage != placedAverage) { // this is checking for NaN, should only happen on first call
            placedAverage = 1;
        }
        
        // now we've got placement average, iterate all pokemon and see if they qualify to be placed

        for (Pokemon newPK : allPK) {
            if (placedPK.contains(newPK)) { // if it's in the list of previously placed, then check its viability 
                if (placementHistory.get(newPK) <= placedAverage) {
//                     System.out.println(newPK.name + ": " + placementHistory.get(newPK)+" "+placedAverage+" ACCEPT");
                    toPlacePK.add(newPK);
                }
                else {
//                  System.out.println(newPK.name + ": " + placementHistory.get(newPK)+" "+placedAverage+" REJECT");
                }
            }
            else {
                toPlacePK.add(newPK); // if not placed at all, automatically flag true for placing
                
            }
        }

        // System.out.println("Size: " + toPlacePK.size());
        return toPlacePK; 
        
    }

    @Override
    public void renderPlacementHistory() {
        List<Pokemon> placedPK = new ArrayList<>(placementHistory.keySet());
        for (Pokemon p : placedPK) {
            System.out.println(p.name+": "+ placementHistory.get(p));
        }
        
        
    }
    
    
    ///// Item functions
    private void setItemPlacementHistory(int newItem) {
        Integer history = getItemPlacementHistory(newItem);
        // System.out.println("Current history: " + newPK.name + " : " + history);
        itemPlacementHistory.put(newItem, history + 1);        
    }

    private int getItemPlacementHistory(int newItem) {
        List<Integer> placedItem = new ArrayList<>(itemPlacementHistory.keySet());
        if (placedItem.contains(newItem)) {
            return itemPlacementHistory.get(newItem);
        }
        else {
            return 0;
        }        
    }
    
    private float getItemPlacementAverage() {
        // This method will return an integer of average for itemPlacementHistory
        // placed is less than average of all placed pokemon's appearances
        // E.g., Charmander's been placed once, but the average for all pokemon is 2.2
        // So add to list and return 
        
        List<Integer> placedPK = new ArrayList<>(itemPlacementHistory.keySet());
        int placedPKNum = 0;
        for (Integer p : placedPK) {
            placedPKNum += itemPlacementHistory.get(p); 
        }
        return (float)placedPKNum / (float)placedPK.size();
    }
    
    private void reportItemHistory() {
        String[] itemNames = this.getItemNames();
        List<Integer> placedItem = new ArrayList<>(itemPlacementHistory.keySet());
        for (Integer p : placedItem) {
            System.out.println(itemNames[p]+": "+ itemPlacementHistory.get(p)); 
        }
    }
    
    

    
    protected void log(String log) {
        if (logStream != null) {
            logStream.println(log);
        }
    }

    protected void logBlankLine() {
        if (logStream != null) {
            logStream.println();
        }
    }

    protected void logEvoChangeLevel(String pkFrom, String pkTo, int level) {
        if (logStream != null) {
            logStream.printf("%-15s -> %-15s at level %d", pkFrom, pkTo, level);
            logStream.println();
        }
    }

    protected void logEvoChangeLevelWithItem(String pkFrom, String pkTo, String itemName) {
        if (logStream != null) {
            logStream.printf("%-15s -> %-15s by leveling up holding %s", pkFrom, pkTo, itemName);
            logStream.println();
        }
    }

    protected void logEvoChangeStone(String pkFrom, String pkTo, String itemName) {
        if (logStream != null) {
            logStream.printf("%-15s -> %-15s using a %s", pkFrom, pkTo, itemName);
            logStream.println();
        }
    }

    protected void logEvoChangeLevelWithPkmn(String pkFrom, String pkTo, String otherRequired) {
        if (logStream != null) {
            logStream.printf("%-15s -> %-15s by leveling up with %s in the party", pkFrom, pkTo, otherRequired);
            logStream.println();
        }
    }

    protected void logEvoChangeHappiness(String pkFrom, String pkTo) {
        if (logStream != null) {
            logStream.printf("%-15s -> %-15s by reaching high happiness", pkFrom, pkTo);
            logStream.println();
        }
    }

    /* Default Implementations */
    /* Used when a subclass doesn't override */
    /*
     * The implication here is that these WILL be overridden by at least one
     * subclass.
     */

    @Override
    public boolean canChangeStarters() {
        return true;
    }

    @Override
    public boolean typeInGame(Type type) {
        return !type.isHackOnly && !(type == Type.FAIRY && generationOfPokemon() < 6);
    }

    @Override
    public String abilityName(int number) {
        return "";
    }

    @Override
    public boolean hasTimeBasedEncounters() {
        // DEFAULT: no
        return false;
    }

    @Override
    public List<Pokemon> bannedForWildEncounters() {
        return new ArrayList<>();
    }

    @Override
    public List<Integer> getMovesBannedFromLevelup() {
        return new ArrayList<>();
    }

    @Override
    public List<Pokemon> bannedForStaticPokemon() {
        return new ArrayList<>();
    }

    @Override
    public boolean forceSwapStaticMegaEvos() {
        return false;
    }

    @Override
    public int maxTrainerNameLength() {
        // default: no real limit
        return Integer.MAX_VALUE;
    }

    @Override
    public int maxSumOfTrainerNameLengths() {
        // default: no real limit
        return Integer.MAX_VALUE;
    }

    @Override
    public int maxTrainerClassNameLength() {
        // default: no real limit
        return Integer.MAX_VALUE;
    }

    @Override
    public int maxTradeNicknameLength() {
        return 10;
    }

    @Override
    public int maxTradeOTNameLength() {
        return 7;
    }

    @Override
    public List<Integer> getGameBreakingMoves() {
        // Sonicboom & drage
        return Arrays.asList(49, 82);
    }

    @Override
    public boolean isYellow() {
        return false;
    }

    @Override
    public boolean isROMHack() {
        // override until detection implemented
        return false;
    }

    @Override
    public void writeCheckValueToROM(int value) {
        // do nothing
    }

    @Override
    public int miscTweaksAvailable() {
        // default: none
        return 0;
    }

    @Override
    public void applyMiscTweak(MiscTweak tweak) {
        // default: do nothing
    }
}
