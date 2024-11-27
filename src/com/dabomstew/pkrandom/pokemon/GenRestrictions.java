package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  GenRestrictions.java - stores what generations the user has limited.  --*/
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GenRestrictions {

    public boolean allow_gen1, allow_gen2, allow_gen3, allow_gen4, allow_gen5, allow_gen6, allow_gen7;
    public boolean allow_evolutionary_relatives;

    public GenRestrictions() {
    }

    public GenRestrictions(int state) {
        allow_gen1 = (state & 1) > 0;
        allow_gen2 = (state & 2) > 0;
        allow_gen3 = (state & 4) > 0;
        allow_gen4 = (state & 8) > 0;
        allow_gen5 = (state & 16) > 0;
        allow_gen6 = (state & 32) > 0;
        allow_gen7 = (state & 64) > 0;
        allow_evolutionary_relatives = (state & 128) > 0;
    }

    public boolean nothingSelected() {
        return !allow_gen1 && !allow_gen2 && !allow_gen3 && !allow_gen4 && !allow_gen5 && !allow_gen6 && !allow_gen7;
    }

    public int toInt() {
        return makeIntSelected(allow_gen1, allow_gen2, allow_gen3, allow_gen4, allow_gen5, allow_gen6, allow_gen7,
                allow_evolutionary_relatives);
    }

    public void limitToGen(int generation) {
        if (generation < 2) {
            allow_gen2 = false;
        }
        if (generation < 3) {
            allow_gen3 = false;
        }
        if (generation < 4) {
            allow_gen4 = false;
        }
        if (generation < 5) {
            allow_gen5 = false;
        }
        if (generation < 6) {
            allow_gen6 = false;
        }
        if (generation < 7) {
            allow_gen7 = false;
        }
    }

    public boolean allowTrainerSwapMegaEvolvables(boolean isXY, boolean isTypeThemedTrainers) {
        if (isTypeThemedTrainers) {
            return megaEvolutionsOfEveryTypeAreInPool(isXY);
        } else {
            return megaEvolutionsAreInPool(isXY);
        }
    }

    public boolean megaEvolutionsOfEveryTypeAreInPool(boolean isXY) {
        Set<Type> typePool = new HashSet<>();
        if (allow_gen1) {
            typePool.addAll(Arrays.asList(Type.GRASS, Type.POISON, Type.FIRE, Type.FLYING, Type.WATER, Type.PSYCHIC,
                    Type.GHOST, Type.NORMAL, Type.BUG, Type.ROCK));
        }
        if (allow_gen2) {
            typePool.addAll(Arrays.asList(Type.ELECTRIC, Type.BUG, Type.STEEL, Type.FIGHTING, Type.DARK,
                    Type.FIRE, Type.ROCK));
            if (!isXY) {
                typePool.add(Type.GROUND);
            }
        }
        if (allow_gen3) {
            typePool.addAll(Arrays.asList(Type.FIRE, Type.FIGHTING, Type.PSYCHIC, Type.FAIRY, Type.STEEL, Type.ROCK,
                    Type.ELECTRIC, Type.GHOST, Type.DARK, Type.DRAGON));
            if (!isXY) {
                typePool.addAll(Arrays.asList(Type.GRASS, Type.WATER, Type.GROUND, Type.FLYING, Type.ICE));
            }
        }
        if (allow_gen4) {
            typePool.addAll(Arrays.asList(Type.DRAGON, Type.GROUND, Type.FIGHTING, Type.STEEL, Type.GRASS, Type.ICE));
            if (!isXY) {
                typePool.addAll(Arrays.asList(Type.NORMAL, Type.PSYCHIC));
            }
        }
        if (allow_gen5 && !isXY) {
            typePool.add(Type.NORMAL);
        }
        if (allow_gen6 && !isXY) {
            typePool.addAll(Arrays.asList(Type.ROCK, Type.FAIRY));
        }
        return typePool.size() == 18;
    }

    public boolean megaEvolutionsAreInPool(boolean isXY) {
        if (isXY) {
            return allow_gen1 || allow_gen2 || allow_gen3 || allow_gen4;
        } else {
            return allow_gen1 || allow_gen2 || allow_gen3 || allow_gen4 || allow_gen5 || allow_gen6;
        }
    }

    private int makeIntSelected(boolean... switches) {
        if (switches.length > 32) {
            // No can do
            return 0;
        }
        int initial = 0;
        int state = 1;
        for (boolean b : switches) {
            initial |= b ? state : 0;
            state *= 2;
        }
        return initial;
    }

}
