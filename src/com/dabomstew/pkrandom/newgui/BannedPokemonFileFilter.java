/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dabomstew.pkrandom.newgui;

/*----------------------------------------------------------------------------*/
/*--  BannedPokemonFileFilter.java - a file filter for the banned Pokemon   --*/
/*--                          which allows selected Dex ID's to be excluded --*/
/*--                          from the randomizer.                          --*/
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

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class BannedPokemonFileFilter extends FileFilter {

    @Override
    public boolean accept(File arg0) {
        if (arg0.isDirectory()) {
            return true; // needed to allow directory navigation
        }
        String filename = arg0.getName();
        if (!filename.contains(".")) {
            return false;
        }
        String extension = arg0.getName().substring(
                arg0.getName().lastIndexOf('.') + 1);
        return extension.toLowerCase().equals("rnbp");
    }

    @Override
    public String getDescription() {
        return "Banned Pokemon File (*.rnbp)";
    }

}
