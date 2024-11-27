package com.dabomstew.pkrandom;

/*----------------------------------------------------------------------------*/
/*--  Version.java - contains information about the randomizer's versions   --*/
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
import java.util.Map;

public class Version {
    public static final int VERSION = 323; // Increment by 1 for new version. Updated for Star 1.0 Fork
    public static final String VERSION_STRING = "1.0";

    public static final Map<Integer,String> oldVersions = setupVersionsMap();

    private static Map<Integer,String> setupVersionsMap() {
        Map<Integer,String> map = new HashMap<>();

        map.put(100, "1.0.1a");
        map.put(102, "1.0.2a");
        map.put(110, "1.1.0");
        map.put(111, "1.1.1");
        map.put(112, "1.1.2");
        map.put(120, "1.2.0a");
        map.put(150, "1.5.0");
        map.put(160, "1.6.0a");
        map.put(161, "1.6.1");
        map.put(162, "1.6.2");
        map.put(163, "1.6.3b");
        map.put(170, "1.7.0b");
        map.put(171, "1.7.1");
        map.put(172, "1.7.2");
        map.put(310, "3.1.0");
        map.put(311, "4.0.0");
        map.put(312, "4.0.1");
        map.put(313, "4.0.2");
        map.put(314, "4.1.0");
        map.put(315, "4.2.0");
        map.put(316, "4.2.1");
        map.put(317, "4.3.0");
        map.put(318, "4.4.0");
        map.put(319, "4.5.0");
        map.put(320, "4.5.1");
        map.put(321, "4.6.0");
        map.put(321, "1.0");

        // Latest version - when version is updated, add the old version as an explicit put
        map.put(VERSION, VERSION_STRING);

        return map;
    }

    public static boolean isReleaseVersionNewer(String releaseVersion) {
        if (VERSION_STRING.contains("dev")) {
            return false;
        }
        // Chop off leading "v" from release version
        try {
            String releaseVersionTrimmed = releaseVersion.substring(1);
            String[] thisVersionPieces = VERSION_STRING.split("\\.");
            String[] releaseVersionPieces = releaseVersionTrimmed.split("\\.");
            int smallestLength = Math.min(thisVersionPieces.length, releaseVersionPieces.length);
            for (int i = 0; i < smallestLength; i++) {
                int thisVersionPiece = Integer.parseInt(thisVersionPieces[i]);
                int releaseVersionPiece = Integer.parseInt(releaseVersionPieces[i]);
                if (thisVersionPiece < releaseVersionPiece) {
                    return true;
                } else if (thisVersionPiece > releaseVersionPiece) {
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            // Really not a big deal if we fail at this, probably because we can't connect to Github.
            return false;
        }
    }
}
