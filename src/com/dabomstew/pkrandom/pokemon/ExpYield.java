package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  ExpCurve.java - represents the EXP curves that a Pokemon can have.    --*/
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

public enum ExpYield {

    MINIMAL, AVERAGE, MAXIMAL, HITPOINTS, HIGHESTSTAT, STATAVERAGE;

    public static ExpYield fromByte(byte curve) {
        switch (curve) {
        case 0:
            return MINIMAL;
        case 1:
            return AVERAGE;
        case 2:
            return MAXIMAL;
        case 3:
            return HITPOINTS;
        case 4:
            return HIGHESTSTAT;
        case 5:
            return STATAVERAGE;
        }
        return null;
    }

    public byte toByte() {
        switch (this) {
        case MINIMAL:
            return 0;
        case AVERAGE:
            return 1;
        case MAXIMAL:
            return 2;
        case HITPOINTS:
            return 3;
        case HIGHESTSTAT:
            return 4;
        case STATAVERAGE:
            return 5;
        }
        return 0; // default
    }

    @Override
    public String toString() {
        switch (this) {
        case MINIMAL:
            return "Minimal";
        case AVERAGE:
            return "Average";
        case MAXIMAL:
            return "Maximal";
        case HITPOINTS:
            return "Hit Points";
        case HIGHESTSTAT:
            return "Highest Stat";
        case STATAVERAGE:
            return "Stat Average";
        }
        return null;
    }

}
