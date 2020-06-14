package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Abstract3DSRomHandler.java - a base class for 3DS rom handlers        --*/
/*--                              which standardises common 3DS functions.  --*/
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

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.ctr.NCCH;
import com.dabomstew.pkrandom.exceptions.RandomizerIOException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

public abstract class Abstract3DSRomHandler extends AbstractRomHandler {

    private NCCH baseRom;
    private String loadedFN;

    public Abstract3DSRomHandler(Random random, PrintStream logStream) {
        super(random, logStream);
    }

    @Override
    public boolean loadRom(String filename) {
        String productCode = getProductCodeFromFile(filename);
        String titleId = getTitleIdFromFile(filename);
        if (!this.detect3DSRom(productCode, titleId)) {
            return false;
        }
        // Load inner rom
        try {
            baseRom = new NCCH(filename, getNCCHOffsetInFile(filename));
            if (!baseRom.isDecrypted()) {
                return false;
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        loadedFN = filename;
        this.loadedROM(productCode, titleId);
        return true;
    }

    protected abstract boolean detect3DSRom(String productCode, String titleId);

    @Override
    public String loadedFilename() {
        return loadedFN;
    }

    protected abstract void loadedROM(String productCode, String titleId);

    public void closeInnerRom() throws IOException {
        baseRom.closeROM();
    }

    @Override
    public boolean hasPhysicalSpecialSplit() {
        // Default value for Gen4+.
        // Handlers can override again in case of ROM hacks etc.
        return true;
    }

    protected byte[] readCode() throws IOException {
        return baseRom.getCode();
    }

    protected byte[] readFile(String location) throws IOException {
        return baseRom.getFile(location);
    }

    protected static int getNCCHOffsetInFile(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);
            fis.skip(0x100);
            byte[] magic = FileFunctions.readFullyIntoBuffer(fis, 0x04);
            fis.close();
            if (magic[0] == 0x4E && magic[1] == 0x43 && magic[2] == 0x43 && magic[3] == 0x48) {
                // Magic is NCCH, so this is an NCCH/CXI file; the NCCH Offset is at the start.
                return 0;
            } else if (magic[0] == 0x4E && magic[1] == 0x43 && magic[2] == 0x53 && magic[3] == 0x44) {
                // Magic is NCSD, so the executable NCCH for this cart is located at 0x4000.
                return 0x4000;
            } else {
                return -1;
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    protected static String getProductCodeFromFile(String filename) {
        try {
            int ncchStartingOffset = getNCCHOffsetInFile(filename);
            if (ncchStartingOffset == -1) {
                return null;
            }
            FileInputStream fis = new FileInputStream(filename);
            fis.skip(ncchStartingOffset + 0x150);
            byte[] productCode = FileFunctions.readFullyIntoBuffer(fis, 0x10);
            fis.close();
            return new String(productCode, "UTF-8").trim();
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    protected static String getTitleIdFromFile(String filename) {
        try {
            int ncchStartingOffset = getNCCHOffsetInFile(filename);
            if (ncchStartingOffset == -1) {
                return null;
            }
            FileInputStream fis = new FileInputStream(filename);
            fis.skip(ncchStartingOffset + 0x118);
            byte[] programId = FileFunctions.readFullyIntoBuffer(fis, 0x8);
            fis.close();
            reverseArray(programId);
            return bytesToHex(programId);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private static void reverseArray(byte[] bytes) {
        for (int i = 0; i < bytes.length / 2; i++) {
            byte temp = bytes[i];
            bytes[i] = bytes[bytes.length - i - 1];
            bytes[bytes.length - i - 1] = temp;
        }
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int unsignedByte = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[unsignedByte >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[unsignedByte & 0x0F];
        }
        return new String(hexChars);
    }
}
