package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  EncounterSet.java - contains a group of wild Pokemon                  --*/
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EncounterSet {

    public int rate;
    public List<Encounter> encounters = new ArrayList<>();
    public Set<Pokemon> bannedPokemon = new HashSet<>();
    public String displayName;
    public int offset;

    public String getDivClass() {
        if (this.displayName != null) {
            if (this.displayName.contains("Surfing")) {
                return "pk-set-surfing";
            } else if (this.displayName.contains("Fishing") || this.displayName.contains("Old Rod")
                    || this.displayName.contains("Good Rod")
                    || this.displayName.contains("Super Rod")) {
                return "pk-set-fishing";
            } else if (this.displayName.contains("Grass/Cave")) {
                return "pk-set-grass";
            } else if (this.displayName.contains("Headbutt")) {
                return "pk-set-headbutt-trees";
            } else if (this.displayName.contains("Bug Catching Contest")) {
                return "pk-set-bug-catching-contest";
            } else if (this.displayName.contains("Rock Smash")) {
                return "pk-set-rock-smash";
            } else if (this.displayName.contains("Swarm") || this.displayName.contains("Radar")
                    || this.displayName.contains("GBA")) {
                return "pk-set-poke-radar";
            } else if (this.displayName.contains("Doubles Grass")) {
                return "pk-set-doubles-grass";
            } else if (this.displayName.contains("Shaking Spots")) {
                return "pk-set-shaking-spot";
            } else if (this.displayName.contains("Radio")) {
                return "pk-set-radio";
            }
        }
        // Return empty string if no class is supported
        return "";
    }

    public String getUlClass() {
        if (this.displayName != null) {
            if (this.displayName.contains("Surfing")) {
                return "pk-list-surfing";
            } else if (this.displayName.contains("Fishing") || this.displayName.contains("Old Rod")
                    || this.displayName.contains("Good Rod")
                    || this.displayName.contains("Super Rod")) {
                return "pk-list-fishing";
            } else if (this.displayName.contains("Grass/Cave")) {
                return "pk-list-grass";
            } else if (this.displayName.contains("Headbutt")) {
                return "pk-list-headbutt-trees";
            } else if (this.displayName.contains("Bug Catching Contest")) {
                return "pk-list-bug-catching-contest";
            } else if (this.displayName.contains("Rock Smash")) {
                return "pk-list-rock-smash";
            } else if (this.displayName.contains("Swarm") || this.displayName.contains("Radar")
                    || this.displayName.contains("GBA")) {
                return "pk-list-poke-radar";
            } else if (this.displayName.contains("Doubles Grass")) {
                return "pk-list-doubles-grass";
            } else if (this.displayName.contains("Shaking Spots")) {
                return "pk-list-shaking-spot";
            } else if (this.displayName.contains("Radio")) {
                return "pk-list-radio";
            }
        }
        // Return empty string if no class is supported
        return "";
    }

    public String toString() {
        return "Encounter [Rate = " + rate + ", Encounters = " + encounters + "]";
    }

}
