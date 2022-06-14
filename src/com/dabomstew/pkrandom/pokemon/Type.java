package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Type.java - represents a Pokemon or move type.                        --*/
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
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.dabomstew.pkrandom.RomFunctions;

public enum Type {

    NORMAL, FIGHTING, FLYING, GRASS, WATER, FIRE, ROCK, GROUND, PSYCHIC, BUG, DRAGON, ELECTRIC, GHOST, POISON, ICE, STEEL, DARK, FAIRY,
    GAS(true), WOOD(true), ABNORMAL(true), WIND(true), SOUND(true), LIGHT(true), TRI(true);

    public boolean isHackOnly;

    Type() {
        this.isHackOnly = false;
    }

    Type(boolean isHackOnly) {
        this.isHackOnly = isHackOnly;
    }

    private static final List<Type> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();

    public static final List<Type> GEN1 = Collections.unmodifiableList(Arrays.asList(values()).subList(0, ICE.ordinal()+1));
    public static final List<Type> GEN2THROUGH5 = Collections.unmodifiableList(Arrays.asList(values()).subList(0, DARK.ordinal()+1));
    public static final List<Type> GEN6PLUS = Collections.unmodifiableList(Arrays.asList(values()).subList(0, FAIRY.ordinal()+1));

    public static List<Type> getAllTypes(int generation) {
        switch (generation) {
            case 1:
                return GEN1;
            case 2:
            case 3:
            case 4:
            case 5:
                return GEN2THROUGH5;
            default:
                return GEN6PLUS;
        }
    }

    public static Type randomType(Random random) {
        return VALUES.get(random.nextInt(SIZE));
    }

    public String camelCase() {
        return RomFunctions.camelCase(this.toString());
    }

}
