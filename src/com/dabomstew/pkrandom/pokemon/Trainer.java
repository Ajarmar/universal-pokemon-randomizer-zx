package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Trainer.java - represents a Trainer's pokemon set/other details.      --*/
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
import java.util.List;

import com.dabomstew.pkrandom.romhandlers.RomHandler;
import org.json.JSONObject;

public class Trainer implements Comparable<Trainer> {
    public int offset;
    public int index;
    public List<TrainerPokemon> pokemon = new ArrayList<>();
    public String tag;
    public Type typeTheme;
    public boolean importantTrainer;
    // This value has some flags about the trainer's pokemon (e.g. if they have items or custom moves)
    public int poketype;
    public String name;
    public int trainerclass;
    public String fullDisplayName;
    public MultiBattleStatus multiBattleStatus = MultiBattleStatus.NEVER;
    public int forceStarterPosition = -1;
    // Certain trainers (e.g., trainers in the PWT in BW2) require unique held items for all of their Pokemon to prevent a game crash.
    public boolean requiresUniqueHeldItems;
    public int originalTeamSize;

    public Trainer(){};

    public Trainer(Trainer t) {
        this.offset = t.offset;
        this.tag = t.tag;
        this.typeTheme = t.typeTheme;
        this.multiBattleStatus = t.multiBattleStatus;
        this.importantTrainer = t.importantTrainer;
        this.poketype = t.poketype;
        this.name = t.name;
        this.trainerclass = t.trainerclass;
        this.fullDisplayName = t.fullDisplayName;

        for(TrainerPokemon tpk : t.pokemon) {
            if (tpk != null)
                pokemon.add(new TrainerPokemon(tpk));
        }
        this.originalTeamSize = t.pokemon.size();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        if (fullDisplayName != null) {
            sb.append(fullDisplayName).append(" ");
        } else if (name != null) {
            sb.append(name).append(" ");
        }
        if (trainerclass != 0) {
            sb.append("(").append(trainerclass).append(") - ");
        }
        if (offset > 0) {
            sb.append(String.format("%x", offset));
        }
        sb.append(" => ");
        boolean first = true;
        for (TrainerPokemon p : pokemon) {
            if (!first) {
                sb.append(',');
            }
            sb.append(p.pokemon.name).append(" Lv").append(p.level);
            first = false;
        }
        sb.append(']');
        if (tag != null) {
            sb.append(" (").append(tag).append(")");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        return result;
    }

    public String stringOffset() {
        return String.format("%X", this.offset);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Trainer other = (Trainer) obj;
        return index == other.index;
    }

    @Override
    public int compareTo(Trainer o) {
        return index - o.index;
    }

    public boolean isElite() {
        return tag != null && (tag.startsWith("ELITE") || tag.startsWith("CHAMPION") || tag.startsWith("UBER"));
    }

    public boolean isBoss() {
        return tag != null && (tag.startsWith("ELITE") || tag.startsWith("CHAMPION")
                || tag.startsWith("UBER") || tag.endsWith("LEADER"));
    }

    public boolean isImportant() {
        return tag != null && (tag.startsWith("RIVAL") || tag.startsWith("FRIEND") || tag.endsWith("STRONG"));
    }

    public boolean skipImportant() {
        return ((tag != null) && (tag.startsWith("RIVAL1-") || tag.startsWith("FRIEND1-") || tag.endsWith("NOTSTRONG")));
    }

    public void setOriginalTeamSize(int size) {
        this.originalTeamSize = size;
    }

    public void setPokemonHaveItems(boolean haveItems) {
        if (haveItems) {
            this.poketype |= 2;
        } else {
            // https://stackoverflow.com/a/1073328
            this.poketype = poketype & ~2;
        }
    }

    public boolean pokemonHaveItems() {
        // This flag seems consistent for all gens
        return (this.poketype & 2) == 2;
    }

    public void setPokemonHaveCustomMoves(boolean haveCustomMoves) {
        if (haveCustomMoves) {
            this.poketype |= 1;
        } else {
            this.poketype = poketype & ~1;
        }
    }

    public boolean pokemonHaveCustomMoves() {
        // This flag seems consistent for all gens
        return (this.poketype & 1) == 1;
    }

    public boolean pokemonHaveUniqueHeldItems() {
        List<Integer> heldItemsForThisTrainer = new ArrayList<>();
        for (TrainerPokemon poke : this.pokemon) {
            if (poke.heldItem > 0) {
                if (heldItemsForThisTrainer.contains(poke.heldItem)) {
                    return false;
                } else {
                    heldItemsForThisTrainer.add(poke.heldItem);
                }
            }
        }
        return true;
    }

    public String getLogName() {
        if (this.fullDisplayName != null) {
            return this.fullDisplayName;
        }
        if (this.name != null) {
            return this.name;
        }
        return "No_Name_Found";
    }

    public JSONObject toJSON(RomHandler romHandler) {
        JSONObject trainer = new JSONObject();

        trainer.put("name", getLogName());
        trainer.put("offset", stringOffset());

        JSONObject team = new JSONObject();
        int slot = 0;
        for (TrainerPokemon tpk : pokemon) {
            if (tpk == null) continue;

            String slotText = String.valueOf(slot);

            JSONObject pokemon = tpk.toJSON(romHandler);
            team.put(slotText, pokemon);

            slot++;
        }

        trainer.put("team",team);

        return trainer;
    }

    public enum MultiBattleStatus {
        NEVER, POTENTIAL, ALWAYS
    }
}
