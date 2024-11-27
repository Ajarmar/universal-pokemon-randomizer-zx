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

public enum ExpCurve {

    SLOW, MEDIUM_SLOW, MEDIUM_FAST, FAST, ERRATIC, FLUCTUATING;

    public static ExpCurve fromByte(byte curve) {
        switch (curve) {
        case 0:
            return MEDIUM_FAST;
        case 1:
            return ERRATIC;
        case 2:
            return FLUCTUATING;
        case 3:
            return MEDIUM_SLOW;
        case 4:
            return FAST;
        case 5:
            return SLOW;
        }
        return null;
    }

    public byte toByte() {
        switch (this) {
        case SLOW:
            return 5;
        case MEDIUM_SLOW:
            return 3;
        case MEDIUM_FAST:
            return 0;
        case FAST:
            return 4;
        case ERRATIC:
            return 1;
        case FLUCTUATING:
            return 2;
        }
        return 0; // default
    }

    @Override
    public String toString() {
        switch (this) {
        case MEDIUM_FAST:
            return "Medium Fast";
        case ERRATIC:
            return "Erratic";
        case FLUCTUATING:
            return "Fluctuating";
        case MEDIUM_SLOW:
            return "Medium Slow";
        case FAST:
            return "Fast";
        case SLOW:
            return "Slow";
        }
        return null;
    }

}
