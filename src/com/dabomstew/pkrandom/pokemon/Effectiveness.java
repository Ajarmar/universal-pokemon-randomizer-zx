package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Effectiveness.java - represents a type's effectiveness and the        --*/
/*--  results of applying super effectiveness and resistance                --*/
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Effectiveness {
    ZERO, HALF, NEUTRAL, DOUBLE, QUARTER, QUADRUPLE;

    public static Map<Type, Effectiveness> against(Type primaryType, Type secondaryType, int gen) {
        return against(primaryType, secondaryType, gen, false);
    }

    // Returns a map where the key is a type and the value is the effectiveness against
    // a pokemon with the two types in a given gen. It does not account for abilities.
    public static Map<Type, Effectiveness> against(Type primaryType, Type secondaryType, int gen, boolean effectivenessUpdated) {
        if (gen >= 2 && gen <= 5) {
            if (effectivenessUpdated) {
                return against(primaryType, secondaryType, gen6PlusTable, Type.GEN2THROUGH5);
            } else {
                return against(primaryType, secondaryType, gen2Through5Table, Type.GEN2THROUGH5);
            }
        }
        if (gen >= 6) {
            return against(primaryType, secondaryType, gen6PlusTable, Type.GEN6PLUS);
        }
        return null;
    }

    private static Map<Type, Effectiveness> against(Type primaryType, Type secondaryType, Effectiveness[][] effectivenesses, List<Type> allTypes) {
        Map<Type, Effectiveness> result = new HashMap<>();
        for(Type type : allTypes) {
            Effectiveness effect = effectivenesses[type.ordinal()][primaryType.ordinal()];
            if (secondaryType != null) {
                effect = effect.combine(effectivenesses[type.ordinal()][secondaryType.ordinal()]);
            }
            result.put(type, effect);
        }
        return result;
    }

    public static List<Type> notVeryEffective(Type attackingType, int generation, boolean effectivenessUpdated) {
        Effectiveness[][] effectivenesses;
        if (generation == 1) {
            effectivenesses = effectivenessUpdated ? gen2Through5Table : gen1Table;
        } else if (generation >= 2 && generation <= 5) {
            effectivenesses = effectivenessUpdated ? gen6PlusTable : gen2Through5Table;
        } else {
            effectivenesses = gen6PlusTable;
        }
        List<Type> allTypes = Type.getAllTypes(generation);

        return allTypes
                .stream()
                .filter(defendingType ->
                        effectivenesses[attackingType.ordinal()][defendingType.ordinal()] == Effectiveness.HALF ||
                                effectivenesses[attackingType.ordinal()][defendingType.ordinal()] == Effectiveness.ZERO)
                .collect(Collectors.toList());
    }

    public static List<Type> superEffective(Type attackingType, int generation, boolean effectivenessUpdated) {
        Effectiveness[][] effectivenesses;
        if (generation == 1) {
            effectivenesses = effectivenessUpdated ? gen2Through5Table : gen1Table;
        } else if (generation >= 2 && generation <= 5) {
            effectivenesses = effectivenessUpdated ? gen6PlusTable : gen2Through5Table;
        } else {
            effectivenesses = gen6PlusTable;
        }
        List<Type> allTypes = Type.getAllTypes(generation);

        return allTypes
                .stream()
                .filter(defendingType ->
                        effectivenesses[attackingType.ordinal()][defendingType.ordinal()] == Effectiveness.DOUBLE)
                .collect(Collectors.toList());
    }

    // Attacking type is the row, Defending type is the column. This corresponds to the ordinal of types.
    private static final Effectiveness[][] gen1Table = {
        /*            NORMAL,FIGHTING, FLYING,   GRASS ,   WATER,   FIRE ,   ROCK , GROUND,  PSYCHIC,   BUG  ,  DRAGON,ELECTRIC,   GHOST , POISON,   ICE  */
        /*NORMAL */ {NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    ZERO, NEUTRAL, NEUTRAL},
        /*FIGHTING*/{ DOUBLE, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL,    ZERO,    HALF,  DOUBLE},
        /*FLYING */ {NEUTRAL,  DOUBLE, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL},
        /*GRASS  */ {NEUTRAL, NEUTRAL,    HALF,    HALF,  DOUBLE,    HALF,  DOUBLE,  DOUBLE, NEUTRAL,    HALF,    HALF, NEUTRAL, NEUTRAL,    HALF, NEUTRAL},
        /*WATER  */ {NEUTRAL, NEUTRAL, NEUTRAL,    HALF,    HALF,  DOUBLE,  DOUBLE,  DOUBLE, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL},
        /*FIRE   */ {NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE,    HALF,    HALF,    HALF, NEUTRAL, NEUTRAL,  DOUBLE,    HALF, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE},
        /*ROCK   */ {NEUTRAL,    HALF,  DOUBLE, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL,    HALF, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE},
        /*GROUND */ {NEUTRAL, NEUTRAL,    ZERO,    HALF, NEUTRAL,  DOUBLE,  DOUBLE, NEUTRAL, NEUTRAL,    HALF, NEUTRAL,  DOUBLE, NEUTRAL,  DOUBLE, NEUTRAL},
        /*PSYCHIC*/ {NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL},
        /*BUG    */ {NEUTRAL,    HALF,    HALF,  DOUBLE, NEUTRAL,    HALF, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL,    HALF,  DOUBLE, NEUTRAL},
        /*DRAGON */ {NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL},
        /*ELECTRIC*/{NEUTRAL, NEUTRAL,  DOUBLE,    HALF,  DOUBLE, NEUTRAL, NEUTRAL,    ZERO, NEUTRAL, NEUTRAL,    HALF,    HALF, NEUTRAL, NEUTRAL, NEUTRAL},
        /*GHOST  */ {   ZERO, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    ZERO, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL},
        /*POISON */ {NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,    HALF,    HALF, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,    HALF,    HALF, NEUTRAL},
        /*ICE    */ {NEUTRAL, NEUTRAL,  DOUBLE,  DOUBLE,    HALF, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL,    HALF},
    };
    private static final Effectiveness[][] gen2Through5Table = {
        /*            NORMAL,FIGHTING, FLYING,   GRASS ,   WATER,   FIRE ,   ROCK , GROUND,  PSYCHIC,   BUG  ,  DRAGON,ELECTRIC,   GHOST , POISON,   ICE  ,  STEEL ,  DARK  */
        /*NORMAL */ {NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    ZERO, NEUTRAL, NEUTRAL,    HALF, NEUTRAL},
        /*FIGHTING*/{ DOUBLE, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL,    ZERO,    HALF,  DOUBLE,  DOUBLE,  DOUBLE},
        /*FLYING */ {NEUTRAL,  DOUBLE, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL,    HALF, NEUTRAL},
        /*GRASS  */ {NEUTRAL, NEUTRAL,    HALF,    HALF,  DOUBLE,    HALF,  DOUBLE,  DOUBLE, NEUTRAL,    HALF,    HALF, NEUTRAL, NEUTRAL,    HALF, NEUTRAL,    HALF, NEUTRAL},
        /*WATER  */ {NEUTRAL, NEUTRAL, NEUTRAL,    HALF,    HALF,  DOUBLE,  DOUBLE,  DOUBLE, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL},
        /*FIRE   */ {NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE,    HALF,    HALF,    HALF, NEUTRAL, NEUTRAL,  DOUBLE,    HALF, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE,  DOUBLE, NEUTRAL},
        /*ROCK   */ {NEUTRAL,    HALF,  DOUBLE, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL,    HALF, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE,    HALF, NEUTRAL},
        /*GROUND */ {NEUTRAL, NEUTRAL,    ZERO,    HALF, NEUTRAL,  DOUBLE,  DOUBLE, NEUTRAL, NEUTRAL,    HALF, NEUTRAL,  DOUBLE, NEUTRAL,  DOUBLE, NEUTRAL,  DOUBLE, NEUTRAL},
        /*PSYCHIC*/ {NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL,    HALF,    ZERO},
        /*BUG    */ {NEUTRAL,    HALF,    HALF,  DOUBLE, NEUTRAL,    HALF, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL,    HALF,    HALF, NEUTRAL,    HALF,  DOUBLE},
        /*DRAGON */ {NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    HALF, NEUTRAL},
        /*ELECTRIC*/{NEUTRAL, NEUTRAL,  DOUBLE,    HALF,  DOUBLE, NEUTRAL, NEUTRAL,    ZERO, NEUTRAL, NEUTRAL,    HALF,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL},
        /*GHOST  */ {   ZERO, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,    HALF,    HALF},
        /*POISON */ {NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,    HALF,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    HALF,    HALF, NEUTRAL,    ZERO, NEUTRAL},
        /*ICE    */ {NEUTRAL, NEUTRAL,  DOUBLE,  DOUBLE,    HALF,    HALF, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL,    HALF,    HALF, NEUTRAL},
        /*STEEL  */ {NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    HALF,    HALF,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL,  DOUBLE,    HALF, NEUTRAL},
        /*DARK   */ {NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,    HALF,    HALF},
    };
    private static final Effectiveness[][] gen6PlusTable = {
        /*            NORMAL,FIGHTING, FLYING,   GRASS ,   WATER,   FIRE ,   ROCK , GROUND,  PSYCHIC,   BUG  ,  DRAGON,ELECTRIC,   GHOST , POISON,   ICE  ,  STEEL ,  DARK  , FAIRY */
        /*NORMAL */ {NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    ZERO, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL},
        /*FIGHTING*/{ DOUBLE, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL,    ZERO,    HALF,  DOUBLE,  DOUBLE,  DOUBLE,    HALF},
        /*FLYING */ {NEUTRAL,  DOUBLE, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL},
        /*GRASS  */ {NEUTRAL, NEUTRAL,    HALF,    HALF,  DOUBLE,    HALF,  DOUBLE,  DOUBLE, NEUTRAL,    HALF,    HALF, NEUTRAL, NEUTRAL,    HALF, NEUTRAL,    HALF, NEUTRAL, NEUTRAL},
        /*WATER  */ {NEUTRAL, NEUTRAL, NEUTRAL,    HALF,    HALF,  DOUBLE,  DOUBLE,  DOUBLE, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL},
        /*FIRE   */ {NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE,    HALF,    HALF,    HALF, NEUTRAL, NEUTRAL,  DOUBLE,    HALF, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE,  DOUBLE, NEUTRAL, NEUTRAL},
        /*ROCK   */ {NEUTRAL,    HALF,  DOUBLE, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL,    HALF, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE,    HALF, NEUTRAL, NEUTRAL},
        /*GROUND */ {NEUTRAL, NEUTRAL,    ZERO,    HALF, NEUTRAL,  DOUBLE,  DOUBLE, NEUTRAL, NEUTRAL,    HALF, NEUTRAL,  DOUBLE, NEUTRAL,  DOUBLE, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL},
        /*PSYCHIC*/ {NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL,    HALF,    ZERO, NEUTRAL},
        /*BUG    */ {NEUTRAL,    HALF,    HALF,  DOUBLE, NEUTRAL,    HALF, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL,    HALF,    HALF, NEUTRAL,    HALF,  DOUBLE,    HALF},
        /*DRAGON */ {NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    HALF, NEUTRAL,    ZERO},
        /*ELECTRIC*/{NEUTRAL, NEUTRAL,  DOUBLE,    HALF,  DOUBLE, NEUTRAL, NEUTRAL,    ZERO, NEUTRAL, NEUTRAL,    HALF,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL},
        /*GHOST  */ {   ZERO, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL,    HALF, NEUTRAL},
        /*POISON */ {NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,    HALF,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    HALF,    HALF, NEUTRAL,    ZERO, NEUTRAL,  DOUBLE},
        /*ICE    */ {NEUTRAL, NEUTRAL,  DOUBLE,  DOUBLE,    HALF,    HALF, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL,    HALF,    HALF, NEUTRAL, NEUTRAL},
        /*STEEL  */ {NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    HALF,    HALF,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL,  DOUBLE,    HALF, NEUTRAL,  DOUBLE},
        /*DARK   */ {NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL,    HALF,    HALF},
        /*FAIRY  */ {NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL, NEUTRAL,    HALF, NEUTRAL, NEUTRAL, NEUTRAL, NEUTRAL,  DOUBLE, NEUTRAL, NEUTRAL,    HALF, NEUTRAL,    HALF,  DOUBLE, NEUTRAL},
    };

    private Effectiveness combine(Effectiveness other) {
        return combineTable[this.ordinal()][other.ordinal()];
    }

    // Allows easier calculation of combining a single type attacking a double typed pokemon.
    // The rows and columns are the ordinals of Effectiveness (but only the first 4, as we don't need to
    // combine 3 or more type considerations).
    private static final Effectiveness[][] combineTable = {
        {ZERO,    ZERO,    ZERO,      ZERO},
        {ZERO, QUARTER,    HALF,   NEUTRAL},
        {ZERO,    HALF, NEUTRAL,    DOUBLE},
        {ZERO, NEUTRAL,  DOUBLE, QUADRUPLE},
    };
}
