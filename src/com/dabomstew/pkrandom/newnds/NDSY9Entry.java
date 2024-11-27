package com.dabomstew.pkrandom.newnds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.dabomstew.pkrandom.FileFunctions;

import cuecompressors.BLZCoder;

/*----------------------------------------------------------------------------*/
/*--  NDSY9Entry.java - an entry in the arm9 overlay system                 --*/
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

public class NDSY9Entry {

    private NDSRom parent;
    public int offset, size, original_size;
    public int fileID;
    public int overlay_id;
    public int ram_address, ram_size;
    public int bss_size;
    public int static_start, static_end;
    public int compressed_size;
    public int compress_flag;
    private Extracted status = Extracted.NOT;
    private String extFilename;
    public byte[] data;
    public long originalCRC;
    private boolean decompressed_data = false;

    public NDSY9Entry(NDSRom parent) {
        this.parent = parent;
    }

    public byte[] getContents() throws IOException {
        if (this.status == Extracted.NOT) {
            // extract file
            parent.reopenROM();
            RandomAccessFile rom = parent.getBaseRom();
            byte[] buf = new byte[this.original_size];
            rom.seek(this.offset);
            rom.readFully(buf);
            originalCRC = FileFunctions.getCRC32(buf);
            // Compression?
            if (compress_flag != 0 && this.original_size == this.compressed_size && this.compressed_size != 0) {
                buf = new BLZCoder(null).BLZ_DecodePub(buf, "overlay " + overlay_id);
                decompressed_data = true;
            }
            if (parent.isWritingEnabled()) {
                // make a file
                String tmpDir = parent.getTmpFolder();
                String fullPath = String.format("overlay_%04d", overlay_id);
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
        size = data.length;
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
        byte[] buf = getContents();
        if (this.decompressed_data) {
            buf = new BLZCoder(null).BLZ_EncodePub(buf, false, false, "overlay " + overlay_id);
            // update our compressed size
            this.compressed_size = buf.length;
        }
        return buf;
    }

    private enum Extracted {
        NOT, TO_FILE, TO_RAM
    }

}
