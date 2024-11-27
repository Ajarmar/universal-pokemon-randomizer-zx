package com.dabomstew.pkrandom;

/*----------------------------------------------------------------------------*/
/*--  SysConstants.java - contains constants not related to the             --*/
/*--                      randomization process itself, such as those       --*/
/*--                      relating to file I/O and the updating system.     --*/
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

import java.io.File;

public class SysConstants {

    public static final String AUTOUPDATE_URL = "http://pokehacks.dabomstew.com/randomizer/autoupdate/";
    public static final String WEBSITE_URL = "http://pokehacks.dabomstew.com/randomizer/";
    public static final String WEBSITE_URL_ZX = "https://github.com/Ajarmar/universal-pokemon-randomizer-zx/releases";
    public static final String WEBSITE_URL_STAR = "https://github.com/srvachet94/universal-pokemon-randomizer-star";
    public static final String WIKI_URL_ZX = "https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki";
    public static final String API_URL_ZX = "https://api.github.com/repos/ajarmar/universal-pokemon-randomizer-zx/releases/latest";
    public static final String VERSION_STAR = "1.0";
    public static final int UPDATE_VERSION = 1721;
    public static final String ROOT_PATH = getRootPath();
    public static final String LINE_SEP = System.getProperty("line.separator");
    public static final String customNamesFile = "customnames.rncn";

    // OLD custom names files
    public static final String tnamesFile = "trainernames.txt";
    public static final String tclassesFile = "trainerclasses.txt";
    public static final String nnamesFile = "nicknames.txt";

    private static String getRootPath() {
        try {
            File fh = Utils.getExecutionLocation().getParentFile();
            return fh.getAbsolutePath() + File.separator;
        } catch (Exception e) {
            return "./";
        }
    }

}
