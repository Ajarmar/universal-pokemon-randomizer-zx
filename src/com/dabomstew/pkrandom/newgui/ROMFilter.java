/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dabomstew.pkrandom.newgui;

/*----------------------------------------------------------------------------*/
/*-- ROMFilter.java - a file filter for the various games which can be      --*/
/*--                  randomized with this program.                         --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew                   --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2012.                                 --*/
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

public class ROMFilter extends FileFilter {

    @Override
    public boolean accept(File arg0) {
        if (arg0.isDirectory()) {
            return true; // needed to allow directory navigation
        }
        String filename = arg0.getName();
        if (!filename.contains(".")) {
            return false;
        }
        String extension = arg0.getName().substring(arg0.getName().lastIndexOf('.') + 1).toLowerCase();
        return extension.equals("gb") || extension.equals("sgb") || extension.equals("gbc") || extension.equals("gba")
                || extension.equals("nds") || extension.equals("cxi");
    }

    @Override
    public String getDescription() {
        return "Nintendo GB(C/A)/(3)DS ROM File (*.gb,*.sgb,*.gbc,*.gba,*.nds,*.cxi)";
    }

}
