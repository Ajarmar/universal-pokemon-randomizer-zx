package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  ItemList.java - contains the list of all items in the game.           --*/
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

import java.util.Random;

public class ItemList {

    private boolean[] items;
    private boolean[] tms;

    public ItemList(int highestIndex) {
        items = new boolean[highestIndex + 1];
        tms = new boolean[highestIndex + 1];
        for (int i = 1; i <= highestIndex; i++) {
            items[i] = true;
        }
    }

    public boolean isTM(int index) {
        return index >= 0 && index < tms.length && tms[index];
    }

    public boolean isAllowed(int index) {
        return index >= 0 && index < tms.length && items[index];
    }

    public void banSingles(int... indexes) {
        for (int index : indexes) {
            items[index] = false;
        }
    }

    public void banRange(int startIndex, int length) {
        for (int i = 0; i < length; i++) {
            items[i + startIndex] = false;
        }
    }

    public void tmRange(int startIndex, int length) {
        for (int i = 0; i < length; i++) {
            tms[i + startIndex] = true;
        }
    }

    public int randomItem(Random random) {
        int chosen = 0;
        while (!items[chosen]) {
            chosen = random.nextInt(items.length);
        }
        return chosen;
    }

    public int randomNonTM(Random random) {
        int chosen = 0;
        while (!items[chosen] || tms[chosen]) {
            chosen = random.nextInt(items.length);
        }
        return chosen;
    }

    public int randomTM(Random random) {
        int chosen = 0;
        while (!tms[chosen]) {
            chosen = random.nextInt(items.length);
        }
        return chosen;
    }

    public ItemList copy() {
        ItemList other = new ItemList(items.length - 1);
        System.arraycopy(items, 0, other.items, 0, items.length);
        System.arraycopy(tms, 0, other.tms, 0, tms.length);
        return other;
    }

    public ItemList copy(int newMax) {
        ItemList other = new ItemList(newMax);
        System.arraycopy(items, 0, other.items, 0, items.length);
        System.arraycopy(tms, 0, other.tms, 0, tms.length);
        return other;
    }

}
