package com.dabomstew.pkrandom.newnds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.dabomstew.pkrandom.FileFunctions;

/*----------------------------------------------------------------------------*/
/*--  NDSFile.java - an entry in the FAT/FNT filesystem                     --*/
/*--  Code based on "Nintendo DS rom tool", copyright (C) DevkitPro         --*/
/*--  Original Code by Rafael Vuijk, Dave Murphy, Alexei Karpenko           --*/
/*--                                                                        --*/
/*--  Ported to Java by Dabomstew under the terms of the GPL:               --*/
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

public class NDSFile {

    private NDSRom parent;
    public int offset, size;
    public int fileID;
    public String fullPath;
    private Extracted status = Extracted.NOT;
    private String extFilename;
    public byte[] data;
    public long originalCRC;

    public NDSFile(NDSRom parent) {
        this.parent = parent;
    }

    public byte[] getContents() throws IOException {
        if (this.status == Extracted.NOT) {
            // extract file
            parent.reopenROM();
            RandomAccessFile rom = parent.getBaseRom();
            byte[] buf = new byte[this.size];
            rom.seek(this.offset);
            rom.readFully(buf);
            originalCRC = FileFunctions.getCRC32(buf);
            if (parent.isWritingEnabled()) {
                // make a file
                String tmpDir = parent.getTmpFolder();
                this.extFilename = fullPath.replaceAll("[^A-Za-z0-9_]+", "");
                File tmpFile = new File(tmpDir + extFilename);
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
            return FileFunctions.readFileFullyIntoBuffer(tmpDir + this.extFilename);
        }
    }

    public void writeOverride(byte[] data) throws IOException {
        if (status == Extracted.NOT) {
            // temp extract
            getContents();
        }
        if (status == Extracted.TO_FILE) {
            String tmpDir = parent.getTmpFolder();
            FileOutputStream fos = new FileOutputStream(new File(tmpDir + this.extFilename));
            fos.write(data);
            fos.close();
        } else {
            if (this.data.length == data.length) {
                // copy new in
                System.arraycopy(data, 0, this.data, 0, data.length);
            } else {
                // make new array
                this.data = null;
                this.data = new byte[data.length];
                System.arraycopy(data, 0, this.data, 0, data.length);
            }
        }
    }

    // returns null if no override
    public byte[] getOverrideContents() throws IOException {
        if (status == Extracted.NOT) {
            return null;
        }
        return getContents();
    }

    private enum Extracted {
        NOT, TO_FILE, TO_RAM
    }

}
