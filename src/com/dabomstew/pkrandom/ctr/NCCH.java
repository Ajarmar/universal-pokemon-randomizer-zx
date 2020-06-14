package com.dabomstew.pkrandom.ctr;

/*----------------------------------------------------------------------------*/
/*--  NCCH.java - a base class for dealing with 3DS NCCH ROM images.        --*/
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
import com.dabomstew.pkrandom.SysConstants;
import cuecompressors.BLZCoder;

import java.io.*;

public class NCCH {

    private String romFilename;
    private RandomAccessFile baseRom;
    private int ncchStartingOffset;
    private int exefsOffset, romfsOffset;
    private ExefsFileHeader codeFileHeader;
    private boolean romOpen;
    private String tmpFolder;
    private boolean writingEnabled;
    private boolean codeCompressed, codeOpen;
    private byte[] codeRamstored;

    private static final int media_unit_size = 0x200;

    public NCCH(String filename, int ncchStartingOffset) throws IOException {
        this.romFilename = filename;
        this.baseRom = new RandomAccessFile(filename, "r");
        this.ncchStartingOffset = ncchStartingOffset;
        this.romOpen = true;
        // TMP folder?
        String rawFilename = new File(filename).getName();
        String dataFolder = "tmp_" + rawFilename.substring(0, rawFilename.lastIndexOf('.'));
        // remove nonsensical chars
        dataFolder = dataFolder.replaceAll("[^A-Za-z0-9_]+", "");
        File tmpFolder = new File(SysConstants.ROOT_PATH + dataFolder);
        tmpFolder.mkdir();
        File tmpExefsFolder = new File(SysConstants.ROOT_PATH + dataFolder + File.separator + "exefs");
        tmpExefsFolder.mkdir();
        File tmpRomfsFolder = new File(SysConstants.ROOT_PATH + dataFolder + File.separator + "romfs");
        tmpRomfsFolder.mkdir();
        if (tmpFolder.canWrite()) {
            writingEnabled = true;
            this.tmpFolder = SysConstants.ROOT_PATH + dataFolder + File.separator;
            tmpFolder.deleteOnExit();
        } else {
            writingEnabled = false;
        }
        readFileSystem();
    }

    private void readFileSystem() throws IOException {
        if (!this.isDecrypted()) {
            return;
        }
        exefsOffset = ncchStartingOffset + readIntFromFile(baseRom, ncchStartingOffset + 0x1A0) * media_unit_size;
        romfsOffset = ncchStartingOffset + readIntFromFile(baseRom, ncchStartingOffset + 0x1B0) * media_unit_size;
        baseRom.seek(ncchStartingOffset + 0x20D);
        byte systemControlInfoFlags = baseRom.readByte();
        codeCompressed = (systemControlInfoFlags & 0x01) != 0;
        readExefs();
        readRomfs();
    }

    private void readExefs() throws IOException {
        byte[] exefsHeaderData = new byte[0x200];
        baseRom.seek(exefsOffset);
        baseRom.readFully(exefsHeaderData);

        ExefsFileHeader[] fileHeaders = new ExefsFileHeader[10];
        for (int i = 0; i < 10; i++) {
            fileHeaders[i] = new ExefsFileHeader(exefsHeaderData, i * 0x10);
        }

        // For the purposes of randomization, the only file in the exefs that we
        // care about is the .code file (i.e., the game's executable).
        for (ExefsFileHeader fileHeader : fileHeaders) {
            if (fileHeader.isValid() && fileHeader.filename.equals(".code")) {
                codeFileHeader = fileHeader;
            }
        }
    }

    private void readRomfs() throws IOException {
        // do nothing for now
    }

    // Note that certain older dumps of games have incorrectly set crypto flags,
    // meaning this method might return false for an NCCH that is truly decrypted.
    // This method correctly checks the flags and matches the way games are
    // currently dumped at the time of this writing.
    public boolean isDecrypted() throws IOException {
        int ncchFlagOffset = ncchStartingOffset + 0x188;
        byte[] ncchFlags = new byte[8];
        baseRom.seek(ncchFlagOffset);
        baseRom.readFully(ncchFlags);
        return ncchFlags[3] == 0 && (ncchFlags[7] & 0x4) != 0;
    }

    // Retrieves a decompressed version of .code (the game's executable).
    // The first time this is called, it will retrieve it straight from the
    // exefs. Future calls will rely on a cached version to speed things up.
    // If writing is enabled, it will cache the decompressed version to the
    // tmpFolder; otherwise, it will store it in RAM.
    public byte[] getCode() throws IOException {
        if (!codeOpen) {
            codeOpen = true;
            byte[] code = new byte[codeFileHeader.size];
            baseRom.seek(exefsOffset + 0x200 + codeFileHeader.offset);
            baseRom.readFully(code);

            if (codeCompressed) {
                code = new BLZCoder(null).BLZ_DecodePub(code, ".code");
            }

            // Now actually make the copy or w/e
            if (writingEnabled) {
                File arm9file = new File(tmpFolder + "exefs" + File.separator + ".code");
                FileOutputStream fos = new FileOutputStream(arm9file);
                fos.write(code);
                fos.close();
                arm9file.deleteOnExit();
                this.codeRamstored = null;
                return code;
            } else {
                this.codeRamstored = code;
                byte[] newcopy = new byte[code.length];
                System.arraycopy(code, 0, newcopy, 0, code.length);
                return newcopy;
            }
        } else {
            if (writingEnabled) {
                return FileFunctions.readFileFullyIntoBuffer(tmpFolder + "exefs" + File.separator + ".code");
            } else {
                byte[] newcopy = new byte[this.codeRamstored.length];
                System.arraycopy(this.codeRamstored, 0, newcopy, 0, this.codeRamstored.length);
                return newcopy;
            }
        }
    }

    private int readIntFromFile(RandomAccessFile file, int offset) throws IOException {
        byte[] buf = new byte[4];
        if (offset >= 0)
            file.seek(offset);
        file.readFully(buf);
        return this.convertBytesToInt(buf, 0);
    }

    private int convertBytesToInt(byte[] bytes, int offset) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result |= (bytes[offset + i] & 0xFF) << (i * 8);
        }
        return result;
    }

    private class ExefsFileHeader {
        public String filename;
        public int offset;
        public int size;

        public ExefsFileHeader(byte[] exefsHeaderData, int fileHeaderOffset) {
            byte[] filenameBytes = new byte[0x8];
            System.arraycopy(exefsHeaderData, fileHeaderOffset, filenameBytes, 0, 0x8);
            try {
                this.filename = new String(filenameBytes, "UTF-8").trim();
            } catch (UnsupportedEncodingException e) {
                this.filename = "";
            }
            this.offset = convertBytesToInt(exefsHeaderData, fileHeaderOffset + 0x08);
            this.size = convertBytesToInt(exefsHeaderData, fileHeaderOffset + 0x0C);
        }

        public boolean isValid() {
            return this.filename != "" && this.size != 0;
        }
    }
}
