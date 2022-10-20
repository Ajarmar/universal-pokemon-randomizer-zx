package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  AbstractSwitchRomHandler.java - a base class for Switch rom handlers  --*/
/*--                                  which standardizes common Switch      --*/
/*--                                  functions.                            --*/
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

import com.dabomstew.pkrandom.exceptions.RandomizerIOException;
import com.dabomstew.pkrandom.hac.SwitchFileReader;

import java.io.*;
import java.util.Random;

public abstract class AbstractSwitchRomHandler extends AbstractRomHandler {

    private String loadedFN;
    private SwitchFileReader fileReader;

    public AbstractSwitchRomHandler(Random random, PrintStream logStream) {
        super(random, logStream);
    }

    @Override
    public boolean loadRom(String filename) {
        try {
            loadedFN = filename;
            fileReader = new SwitchFileReader(filename);
            if (!this.detectSwitchGame(filename)) {
                return false;
            }
            this.loadedROM(filename);
            fileReader.setContentId(this.getContentId());
            return true;
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    protected abstract boolean detectSwitchGame(String filePath) throws IOException;

    @Override
    public String loadedFilename() {
        return loadedFN;
    }

    protected abstract void loadedROM(String filename);

    protected abstract void savingROM() throws IOException;

    protected abstract String getGameAcronym();

    protected abstract String getContentId();

    @Override
    public boolean saveRomFile(String filename, long seed) {
        try {
            savingROM();
            // TODO: Make this unsupported for Switch games
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return true;
    }

    @Override
    public boolean saveRomDirectory(String filename) {
        try {
            savingROM();
            this.writeLayeredFS(filename);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return true;
    }

    private void writeLayeredFS(String filename) throws IOException {
        fileReader.saveAsLayeredFS(filename);
    }

    protected abstract boolean isGameUpdateSupported(int version);

    @Override
    public boolean hasGameUpdateLoaded() {
        return false;
    }

    @Override
    public boolean loadGameUpdate(String filename) {
        return true;
    }

    @Override
    public void removeGameUpdate() {
    }

    protected abstract String getGameVersion();

    @Override
    public String getGameUpdateVersion() {
        return getGameVersion();
    }

    @Override
    public void printRomDiagnostics(PrintStream logStream) {
    }

    @Override
    public boolean hasPhysicalSpecialSplit() {
        // Default value for Gen4+.
        // Handlers can override again in case of ROM hacks etc.
        return true;
    }

    // Doesn't decompress this for now, so you're on you're own.
    protected byte[] readMain() throws IOException {
        return fileReader.getMain();
    }

    protected void writeMain(byte[] data) throws IOException {
        fileReader.writeMain(data);
    }

    protected byte[] readFile(String location) throws IOException {
        return fileReader.getFile(location);
    }

    protected void writeFile(String location, byte[] data) throws IOException {
        fileReader.writeFile(location, data);
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

    protected int createCmpInstruction(int register, int immediate, boolean is64Bit) {
        long instruction = 0x7100001F;
        if (is64Bit) {
            instruction |= 0x80000000;
        }
        instruction |= ((register & 0x1F) << 5);
        instruction |= ((immediate & 0xFFF) << 10);
        return (int)instruction;
    }

    protected int createMovzInstruction(int register, int immediate, boolean is64Bit) {
        long instruction = 0x52800000;
        if (is64Bit) {
            instruction |= 0x80000000;
        }
        instruction |= (register & 0x1F);
        instruction |= ((immediate & 0xFFFF) << 5);
        return (int)instruction;
    }

    protected int createNopInstruction() {
        return 0xD503201F;
    }
}