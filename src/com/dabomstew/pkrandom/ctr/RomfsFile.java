package com.dabomstew.pkrandom.ctr;

/*----------------------------------------------------------------------------*/
/*--  RomfsFile.java - an entry in the romfs filesystem                     --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew                   --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2012.                                 --*/
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

import com.dabomstew.pkrandom.FileFunctions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class RomfsFile {

    private NCCH parent;
    public long offset, size;
    public String fullPath;
    private Extracted status = Extracted.NOT;
    private String extFilename;
    public byte[] data;

    public RomfsFile(NCCH parent) {
        this.parent = parent;
    }

    public byte[] getContents() throws IOException {
        if (this.status == Extracted.NOT) {
            // extract file
            parent.reopenROM();
            RandomAccessFile rom = parent.getBaseRom();
            byte[] buf = new byte[(int)this.size];  // TODO: Make this less hacky
            rom.seek(this.offset);
            rom.readFully(buf);
            if (parent.isWritingEnabled()) {
                // make a file
                String tmpDir = parent.getTmpFolder();
                this.extFilename = fullPath.replaceAll("[^A-Za-z0-9_\\.]+", "");
                File tmpFile = new File(tmpDir + File.separator + extFilename);
                FileOutputStream fos = new FileOutputStream(tmpFile);
                fos.write(buf);
                fos.close();
                tmpFile.deleteOnExit();
                this.status = Extracted.TO_FILE;
                this.data = null;
                return buf;
            } else {
                this.status = Extracted.TO_RAM;
                this.data = buf;
                byte[] newcopy = new byte[buf.length];
                System.arraycopy(buf, 0, newcopy, 0, buf.length);
                return newcopy;
            }
        } else if (this.status == Extracted.TO_RAM) {
            byte[] newcopy = new byte[this.data.length];
            System.arraycopy(this.data, 0, newcopy, 0, this.data.length);
            return newcopy;
        } else {
            String tmpDir = parent.getTmpFolder();
            return FileFunctions.readFileFullyIntoBuffer(tmpDir + File.separator + this.extFilename);
        }
    }

    private enum Extracted {
        NOT, TO_FILE, TO_RAM
    }
}
