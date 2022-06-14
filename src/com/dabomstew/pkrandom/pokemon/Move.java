package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Move.java - represents a move usable by Pokemon.                      --*/
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

import com.dabomstew.pkrandom.constants.GlobalConstants;

public class Move {
    public class StatChange {
        public StatChangeType type;
        public int stages;
        public double percentChance;

        @Override
        public boolean equals(Object obj) {
            StatChange other = (StatChange)obj;
            return this.type == other.type && this.stages == other.stages && this.percentChance == other.percentChance;
        }

    }

    public String name;
    public int number;
    public int internalId;
    public int power;
    public int pp;
    public double hitratio;
    public Type type;
    public MoveCategory category;
    public StatChangeMoveType statChangeMoveType = StatChangeMoveType.NONE_OR_UNKNOWN;
    public StatChange[] statChanges = new StatChange[3];
    public StatusMoveType statusMoveType = StatusMoveType.NONE_OR_UNKNOWN;
    public StatusType statusType = StatusType.NONE;
    public CriticalChance criticalChance = CriticalChance.NORMAL;
    public double statusPercentChance;
    public double flinchPercentChance;
    public int recoilPercent;
    public int absorbPercent;
    public int priority;
    public boolean makesContact;
    public boolean isChargeMove;
    public boolean isRechargeMove;
    public boolean isPunchMove;
    public boolean isSoundMove;
    public boolean isTrapMove; // True for both binding moves (like Wrap) and trapping moves (like Mean Look)
    public int effectIndex;
    public int target;
    public double hitCount = 1; // not saved, only used in randomized move powers.

    public Move() {
        // Initialize all statStageChanges to something sensible so that we don't need to have
        // each RomHandler mess with them if they don't need to.
        for (int i = 0; i < this.statChanges.length; i++) {
            this.statChanges[i] = new StatChange();
            this.statChanges[i].type = StatChangeType.NONE;
        }
    }

    public boolean hasSpecificStatChange(StatChangeType type, boolean isPositive) {
        for (StatChange sc: this.statChanges) {
            if (sc.type == type && (isPositive ^ sc.stages < 0)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBeneficialStatChange() {
        return (statChangeMoveType == StatChangeMoveType.DAMAGE_TARGET && statChanges[0].stages < 0) ||
                statChangeMoveType == StatChangeMoveType.DAMAGE_USER && statChanges[0].stages > 0;
    }

    public boolean isGoodDamaging(int perfectAccuracy) {
        return (power * hitCount) >= 2 * GlobalConstants.MIN_DAMAGING_MOVE_POWER
                || ((power * hitCount) >= GlobalConstants.MIN_DAMAGING_MOVE_POWER && (hitratio >= 90 || hitratio == perfectAccuracy));
    }

    public String toString() {
        return "#" + number + " " + name + " - Power: " + power + ", Base PP: " + pp + ", Type: " + type + ", Hit%: "
                + (hitratio) + ", Effect: " + effectIndex + ", Priority: " + priority;
    }

}
