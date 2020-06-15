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
import java.io.RandomAccessFile;
import java.util.Random;

public abstract class Abstract3DSRomHandler extends AbstractRomHandler {

    private NCCH baseRom;
    private String loadedFN;

    private static final int ncch_magic = 0x4E434348;
    private static final int ncsd_magic = 0x4E435344;
    private static final int cia_header_size = 0x2020;
    private static final int ncch_and_ncsd_magic_offset = 0x100;

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
            baseRom = new NCCH(filename, getCXIOffsetInFile(filename), titleId);
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

    protected abstract void savingROM() throws IOException;

    @Override
    public boolean saveRomFile(String filename) {
        // do nothing for now, as we currently don't support CXI writing.
        return true;
    }

    @Override
    public boolean saveRomDirectory(String filename) {
        try {
            savingROM();
            baseRom.saveAsLayeredFS(filename);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return true;
    }

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

    protected void writeCode(byte[] data) throws IOException {
        baseRom.writeCode(data);
    }

    protected byte[] readFile(String location) throws IOException {
        return baseRom.getFile(location);
    }

    protected void writeFile(String location, byte[] data) throws IOException {
        writeFile(location, data, 0, data.length);
    }

    protected void writeFile(String location, byte[] data, int offset, int length) throws IOException {
        if (offset != 0 || length != data.length) {
            byte[] newData = new byte[length];
            System.arraycopy(data, offset, newData, 0, length);
            data = newData;
        }
        baseRom.writeFile(location, data);
    }

    // At the bare minimum, a 3DS game consists of what's known as a CXI file, which
    // is just an NCCH that contains executable code. However, 3DS games are packaged
    // in various containers that can hold other NCCH files like the game manual and
    // firmware updates, among other things. This function's determines the location
    // of the CXI regardless of the container.
    protected static long getCXIOffsetInFile(String filename) {
        try {
            RandomAccessFile rom = new RandomAccessFile(filename, "r");
            int ciaHeaderSize = FileFunctions.readLittleEndianIntFromFile(rom, 0x00);
            if (ciaHeaderSize == cia_header_size) {
                // This *might* be a CIA; let's do our best effort to try to get
                // a CXI out of this.
                int certChainSize = FileFunctions.readLittleEndianIntFromFile(rom, 0x08);
                int ticketSize = FileFunctions.readLittleEndianIntFromFile(rom, 0x0C);
                int tmdFileSize = FileFunctions.readLittleEndianIntFromFile(rom, 0x10);

                // If this is *really* a CIA, we'll find our CXI at the beginning of the
                // content section, which is after the certificate chain, ticket, and TMD
                long certChainOffset = NCCH.alignLong(ciaHeaderSize, 64);
                long ticketOffset = NCCH.alignLong(certChainOffset + certChainSize, 64);
                long tmdOffset = NCCH.alignLong(ticketOffset + ticketSize, 64);
                long contentOffset = NCCH.alignLong(tmdOffset + tmdFileSize, 64);
                int magic = FileFunctions.readIntFromFile(rom, contentOffset + ncch_and_ncsd_magic_offset);
                if (magic == ncch_magic) {
                    // This CIA's content contains a valid CXI!
                    return contentOffset;
                }
            }

            // We don't put the following code in an else-block because there *might*
            // exist a totally-valid CXI or CCI whose first four bytes just so
            // *happen* to be the same as the first four bytes of a CIA file.
            int magic = FileFunctions.readIntFromFile(rom, ncch_and_ncsd_magic_offset);
            rom.close();
            if (magic == ncch_magic) {
                // Magic is NCCH, so this just a straight-up NCCH/CXI; there is no container
                // around the game data. Thus, the CXI offset is the beginning of the file.
                return 0;
            } else if (magic == ncsd_magic) {
                // Magic is NCSD, so this is almost certainly a CCI. The CXI is always
                // a fixed distance away from the start.
                return 0x4000;
            } else {
                // This doesn't seem to be a valid 3DS file.
                return -1;
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    protected static String getProductCodeFromFile(String filename) {
        try {
            long ncchStartingOffset = getCXIOffsetInFile(filename);
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
            long ncchStartingOffset = getCXIOffsetInFile(filename);
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
