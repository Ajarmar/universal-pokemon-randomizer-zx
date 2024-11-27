package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Evolution.java - represents an evolution between 2 Pokemon.           --*/
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

import com.dabomstew.pkrandom.romhandlers.RomHandler;

public class Evolution implements Comparable<Evolution> {

    public Pokemon from;
    public Pokemon to;
    public boolean carryStats;
    public EvolutionType type;
    public int extraInfo;
    public int forme;
    public String formeSuffix = "";
    public int level = 0;

    public Evolution(Pokemon from, Pokemon to, boolean carryStats, EvolutionType type, int extra) {
        this.from = from;
        this.to = to;
        this.carryStats = carryStats;
        this.type = type;
        this.extraInfo = extra;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + from.number;
        result = prime * result + to.number;
        result = prime * result + type.ordinal();
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
        Evolution other = (Evolution) obj;
        return from == other.from && to == other.to && type == other.type;
    }

    @Override
    public int compareTo(Evolution o) {
        if (this.from.number < o.from.number) {
            return -1;
        } else if (this.from.number > o.from.number) {
            return 1;
        } else if (this.to.number < o.to.number) {
            return -1;
        } else if (this.to.number > o.to.number) {
            return 1;
        } else return Integer.compare(this.type.ordinal(), o.type.ordinal());
    }

    public String toFullName() {
        return to.name + formeSuffix;
    }

    public String methodString(RomHandler romHandler) {
        String evoText = "";
        switch (type) {
            case LEVEL:
            case LEVEL_ATK_DEF_SAME:
            case LEVEL_ATTACK_HIGHER:
            case LEVEL_DEFENSE_HIGHER:
                evoText = type + " " + extraInfo;
                break;
            case LEVEL_HIGH_PV:
                evoText = ("LEVEL " + extraInfo + " when HIGH PERSONALITY VALUE");
                break;
            case LEVEL_LOW_PV:
                evoText = ("LEVEL " + extraInfo + " when LOW PERSONALITY VALUE");
                break;
            case LEVEL_MALE_ONLY:
                evoText = ("LEVEL " + extraInfo + " when MALE");
                break;
            case LEVEL_FEMALE_ONLY:
                evoText = ("LEVEL " + extraInfo + " when FEMALE");
                break;
            case LEVEL_WITH_OTHER:
                evoText = ("LEVEL with " + romHandler.getPokemon().get(extraInfo).name + " in party");
                break;
            case LEVEL_ITEM_DAY:
                evoText = ("LEVEL holding " + romHandler.getItemNames()[extraInfo] + " during DAY");
                break;
            case LEVEL_ITEM_NIGHT:
                evoText = ("LEVEL holding " + romHandler.getItemNames()[extraInfo] + " during NIGHT");
                break;
            case LEVEL_WITH_MOVE:
                evoText = ("LEVEL while knowing " + romHandler.getMoves().get(extraInfo).name);
                break;
            case STONE:
                evoText = (romHandler.getItemNames()[extraInfo]);
                break;
            case STONE_MALE_ONLY:
                evoText = (romHandler.getItemNames()[extraInfo] + " when MALE");
                break;
            case STONE_FEMALE_ONLY:
                evoText = (romHandler.getItemNames()[extraInfo] + " when FEMALE");
                break;
            case TRADE_ITEM:
                evoText = ("TRADE holding " + romHandler.getItemNames()[extraInfo]);
                break;
            // case MEGA_EVOLVE:
            // MEGA EVOLVE holding ${romHandler.getItemNames()[evoFm.extraInfo]}
            //break;
            case FAIRY_AFFECTION:
                evoText = ("HAPPINESS with FAIRY-type move");
                break;
            default:
                evoText = type.toString();
                break;
        }
        return evoText;
    }
}
