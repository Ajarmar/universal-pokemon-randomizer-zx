package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Abstract3DSRomHandler.java - a base class for 3DS rom handlers        --*/
/*--                              which standardises common 3DS functions.  --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
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

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.ctr.GARCArchive;
import com.dabomstew.pkrandom.ctr.NCCH;
import com.dabomstew.pkrandom.exceptions.EncryptedROMException;
import com.dabomstew.pkrandom.exceptions.RandomizerIOException;
import com.dabomstew.pkrandom.pokemon.Type;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

public abstract class Abstract3DSRomHandler extends AbstractRomHandler {

    private NCCH baseRom;
    private NCCH gameUpdate;
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
            baseRom = new NCCH(filename, productCode, titleId);
            if (!baseRom.isDecrypted()) {
                throw new EncryptedROMException(filename);
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

    protected abstract String getGameAcronym();

    @Override
    public boolean saveRomFile(String filename, long seed) {
        try {
            savingROM();
            baseRom.saveAsNCCH(filename, getGameAcronym(), seed);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RandomizerIOException(e);
        }
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

    protected abstract boolean isGameUpdateSupported(int version);

    @Override
    public boolean hasGameUpdateLoaded() {
        return gameUpdate != null;
    }

    @Override
    public boolean loadGameUpdate(String filename) {
        String productCode = getProductCodeFromFile(filename);
        String titleId = getTitleIdFromFile(filename);
        try {
            gameUpdate = new NCCH(filename, productCode, titleId);
            if (!gameUpdate.isDecrypted()) {
                throw new EncryptedROMException(filename);
            }
            int version = gameUpdate.getVersion();
            if (!this.isGameUpdateSupported(version)) {
                System.out.println("Game Update: Supplied unexpected version " + version);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        this.loadedROM(baseRom.getProductCode(), baseRom.getTitleId());
        return true;
    }

    @Override
    public void removeGameUpdate() {
        gameUpdate = null;
        this.loadedROM(baseRom.getProductCode(), baseRom.getTitleId());
    }

    protected abstract String getGameVersion();

    @Override
    public String getGameUpdateVersion() {
        return getGameVersion();
    }

    @Override
    public void printRomDiagnostics(PrintStream logStream) {
        baseRom.printRomDiagnostics(logStream, gameUpdate);
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
        if (gameUpdate != null) {
            return gameUpdate.getCode();
        }
        return baseRom.getCode();
    }

    protected void writeCode(byte[] data) throws IOException {
        baseRom.writeCode(data);
    }

    protected GARCArchive readGARC(String subpath, boolean skipDecompression) throws IOException {
        return new GARCArchive(readFile(subpath),skipDecompression);
    }

    protected GARCArchive readGARC(String subpath, List<Boolean> compressThese) throws IOException {
        return new GARCArchive(readFile(subpath),compressThese);
    }

    protected void writeGARC(String subpath, GARCArchive garc) throws IOException {
        this.writeFile(subpath,garc.getBytes());
    }

    protected byte[] readFile(String location) throws IOException {
        if (gameUpdate != null && gameUpdate.hasFile(location)) {
            return gameUpdate.getFile(location);
        }
        return baseRom.getFile(location);
    }

    protected void writeFile(String location, byte[] data) throws IOException {
        writeFile(location, data, 0, data.length);
    }

    protected void readByteIntoFlags(byte[] data, boolean[] flags, int offsetIntoFlags, int offsetIntoData) {
        int thisByte = data[offsetIntoData] & 0xFF;
        for (int i = 0; i < 8 && (i + offsetIntoFlags) < flags.length; i++) {
            flags[offsetIntoFlags + i] = ((thisByte >> i) & 0x01) == 0x01;
        }
    }

    protected byte getByteFromFlags(boolean[] flags, int offsetIntoFlags) {
        int thisByte = 0;
        for (int i = 0; i < 8 && (i + offsetIntoFlags) < flags.length; i++) {
            thisByte |= (flags[offsetIntoFlags + i] ? 1 : 0) << i;
        }
        return (byte) thisByte;
    }

    protected int readWord(byte[] data, int offset) {
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
    }

    protected void writeWord(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

    protected int readLong(byte[] data, int offset) {
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8) | ((data[offset + 2] & 0xFF) << 16)
                | ((data[offset + 3] & 0xFF) << 24);
    }

    protected void writeLong(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
        data[offset + 2] = (byte) ((value >> 16) & 0xFF);
        data[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }

    protected void writeFile(String location, byte[] data, int offset, int length) throws IOException {
        if (offset != 0 || length != data.length) {
            byte[] newData = new byte[length];
            System.arraycopy(data, offset, newData, 0, length);
            data = newData;
        }
        baseRom.writeFile(location, data);
        if (gameUpdate != null && gameUpdate.hasFile(location)) {
            gameUpdate.writeFile(location, data);
        }
    }

    public String getTitleIdFromLoadedROM() {
        return baseRom.getTitleId();
    }

    protected static String getProductCodeFromFile(String filename) {
        try {
            long ncchStartingOffset = NCCH.getCXIOffsetInFile(filename);
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

    public static String getTitleIdFromFile(String filename) {
        try {
            long ncchStartingOffset = NCCH.getCXIOffsetInFile(filename);
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

    protected int typeTMPaletteNumber(Type t, boolean isGen7) {
        if (t == null) {
            return 322; // CURSE
        }
        switch (t) {
            case DARK:
                return 309;
            case DRAGON:
                return 310;
            case PSYCHIC:
                return 311;
            case NORMAL:
                return 312;
            case POISON:
                return 313;
            case ICE:
                return 314;
            case FIGHTING:
                return 315;
            case FIRE:
                return 316;
            case WATER:
                return 317;
            case FLYING:
                return 323;
            case GRASS:
                return 318;
            case ROCK:
                return 319;
            case ELECTRIC:
                return 320;
            case GROUND:
                return 321;
            case GHOST:
            default:
                return 322; // for CURSE
            case STEEL:
                return 324;
            case BUG:
                return 325;
            case FAIRY:
                return isGen7 ? 555 : 546;
        }
    }
}
