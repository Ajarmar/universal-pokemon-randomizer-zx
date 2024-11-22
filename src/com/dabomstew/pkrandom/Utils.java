package com.dabomstew.pkrandom;

/*----------------------------------------------------------------------------*/
/*--  Utils.java - contains functions related to specific parts of the      --*/
/*--               randomizer's functionality which cannot really be used   --*/
/*--               outside of that function but should still be separated   --*/
/*--               from GUI code.                                           --*/
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.zip.CRC32;

import com.dabomstew.pkrandom.exceptions.InvalidSupplementFilesException;
import com.dabomstew.pkrandom.newgui.NewRandomizerGUI;

public class Utils {

    public static void validateRomFile(File fh) throws InvalidROMException {
        // first, check for common filetypes that aren't ROMs
        // read first 10 bytes of the file to do this
        try {
            FileInputStream fis = new FileInputStream(fh);
            byte[] sig = new byte[10];
            int sigLength = fis.read(sig);
            fis.close();
            if (sigLength < 10) {
                throw new InvalidROMException(InvalidROMException.Type.LENGTH, String.format(
                        "%s appears to be a blank or nearly blank file.", fh.getName()));
            }
            if (sig[0] == 0x50 && sig[1] == 0x4b && sig[2] == 0x03 && sig[3] == 0x04) {
                throw new InvalidROMException(InvalidROMException.Type.ZIP_FILE, String.format(
                        "%s is a ZIP archive, not a ROM.", fh.getName()));
            }
            if (sig[0] == 0x52 && sig[1] == 0x61 && sig[2] == 0x72 && sig[3] == 0x21 && sig[4] == 0x1A
                    && sig[5] == 0x07) {
                throw new InvalidROMException(InvalidROMException.Type.RAR_FILE, String.format(
                        "%s is a RAR archive, not a ROM.", fh.getName()));
            }
            if (sig[0] == 'P' && sig[1] == 'A' && sig[2] == 'T' && sig[3] == 'C' && sig[4] == 'H') {
                throw new InvalidROMException(InvalidROMException.Type.IPS_FILE, String.format(
                        "%s is a IPS patch, not a ROM.", fh.getName()));
            }
        } catch (IOException ex) {
            throw new InvalidROMException(InvalidROMException.Type.UNREADABLE, String.format(
                    "Could not read %s from disk.", fh.getName()));
        }
    }

    // RomHandlers implicitly rely on these - call this before creating settings
    // etc.
    public static void testForRequiredConfigs() throws FileNotFoundException {
        String[] required = new String[] { "gameboy_jpn.tbl", "rby_english.tbl", "rby_freger.tbl", "rby_espita.tbl",
                "green_translation.tbl", "gsc_english.tbl", "gsc_freger.tbl", "gsc_espita.tbl", "gba_english.tbl",
                "gba_jpn.tbl", "Generation4.tbl", "Generation5.tbl", "gen1_offsets.ini", "gen2_offsets.ini",
                "gen3_offsets.ini", "gen4_offsets.ini", "gen5_offsets.ini", "gen6_offsets.ini", "gen7_offsets.ini",
                SysConstants.customNamesFile };
        for (String filename : required) {
            if (!FileFunctions.configExists(filename)) {
                throw new FileNotFoundException(filename);
            }
        }
    }

    public static void validatePresetSupplementFiles(String config, CustomNamesSet customNames, BannedPokemonSet bannedPokemon)
            throws InvalidSupplementFilesException {
        byte[] data = Base64.getDecoder().decode(config);

        if (data.length < Settings.LENGTH_OF_SETTINGS_DATA + 9) {
            throw new InvalidSupplementFilesException(InvalidSupplementFilesException.Type.UNKNOWN,
                    "The preset config is too short to be valid");
        }

        // Check the checksum
        ByteBuffer buf = ByteBuffer.allocate(4).put(data, data.length - 12, 4);
        buf.rewind();
        int crc = buf.getInt();

        CRC32 checksum = new CRC32();
        checksum.update(data, 0, data.length - 12);
        if ((int) checksum.getValue() != crc) {
            throw new IllegalArgumentException("Checksum failure.");
        }

        // Check the trainerclass & trainernames & nicknames crc
        if (customNames == null && !FileFunctions.checkOtherCRC(data, 16, 4, SysConstants.customNamesFile, data.length - 8)) {
            throw new InvalidSupplementFilesException(InvalidSupplementFilesException.Type.CUSTOM_NAMES,
                    "Can't use this preset because you have a different set " + "of custom names to the creator.");
        }

        if (bannedPokemon == null && !FileFunctions.checkOtherCRC(data, 16, 4, SysConstants.bannedPokemonFile, data.length - 4)) {
            throw new InvalidSupplementFilesException(InvalidSupplementFilesException.Type.BANNED_POKEMON,
                    "Can't use this preset because you have a different set " + "of banned Pokemon to the creator.");
        }
    }

    public static File getExecutionLocation() throws UnsupportedEncodingException {
        URL location = NewRandomizerGUI.class.getProtectionDomain().getCodeSource().getLocation();
        String file = location.getFile();
        String plusEncoded = file.replaceAll("\\+", "%2b");
        return new File(java.net.URLDecoder.decode(plusEncoded, "UTF-8"));
    }

    public static class InvalidROMException extends Exception {
        /**
         * 
         */
        private static final long serialVersionUID = 6568398515886021477L;

        public enum Type {
            LENGTH, ZIP_FILE, RAR_FILE, IPS_FILE, UNREADABLE
        }

        private final Type type;

        public InvalidROMException(Type type, String message) {
            super(message);
            this.type = type;
        }

        public Type getType() {
            return type;
        }
    }
}