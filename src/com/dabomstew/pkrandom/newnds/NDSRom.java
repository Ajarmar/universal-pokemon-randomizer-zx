package com.dabomstew.pkrandom.newnds;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.dabomstew.pkrandom.SysConstants;
import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.RomFunctions;

import com.dabomstew.pkrandom.exceptions.CannotWriteToLocationException;
import com.dabomstew.pkrandom.exceptions.RandomizerIOException;
import cuecompressors.BLZCoder;

/*----------------------------------------------------------------------------*/
/*--  NDSRom.java - base class for opening/saving ROMs                      --*/
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

public class NDSRom {

    private String romCode;
    private byte version;
    private String romFilename;
    private RandomAccessFile baseRom;
    private boolean romOpen;
    private Map<String, NDSFile> files;
    private Map<Integer, NDSFile> filesByID;
    private Map<Integer, NDSY9Entry> arm9overlaysByFileID;
    private NDSY9Entry[] arm9overlays;
    private byte[] fat;
    private String tmpFolder;
    private boolean writingEnabled;
    private boolean arm9_open, arm9_changed, arm9_has_footer;
    private boolean arm9_compressed;
    private int arm9_ramoffset;
    private int arm9_szoffset;
    private byte[] arm9_footer;
    private byte[] arm9_ramstored;
    private long originalArm9CRC;

    private static final int arm9_align = 0x1FF, arm7_align = 0x1FF;
    private static final int fnt_align = 0x1FF, fat_align = 0x1FF;
    private static final int banner_align = 0x1FF, file_align = 0x1FF;

    public NDSRom(String filename) throws IOException {
        this.romFilename = filename;
        this.baseRom = new RandomAccessFile(filename, "r");
        this.romOpen = true;
        // TMP folder?
        String rawFilename = new File(filename).getName();
        String dataFolder = "tmp_" + rawFilename.substring(0, rawFilename.lastIndexOf('.'));
        // remove nonsensical chars
        dataFolder = dataFolder.replaceAll("[^A-Za-z0-9_]+", "");
        File tmpFolder = new File(SysConstants.ROOT_PATH + dataFolder);
        tmpFolder.mkdir();
        if (tmpFolder.canWrite()) {
            writingEnabled = true;
            this.tmpFolder = SysConstants.ROOT_PATH + dataFolder + File.separator;
            tmpFolder.deleteOnExit();
        } else {
            writingEnabled = false;
        }
        readFileSystem();
        arm9_open = false;
        arm9_changed = false;
        arm9_ramstored = null;
    }

    public void reopenROM() throws IOException {
        if (!this.romOpen) {
            this.baseRom = new RandomAccessFile(this.romFilename, "r");
            this.romOpen = true;
        }
    }

    public void closeROM() throws IOException {
        if (this.romOpen && this.baseRom != null) {
            this.baseRom.close();
            this.baseRom = null;
            this.romOpen = false;
        }
    }

    private void readFileSystem() throws IOException {
        // read rom code
        baseRom.seek(0x0C);

        byte[] sig = new byte[4];
        baseRom.readFully(sig);
        this.romCode = new String(sig, "US-ASCII");

        baseRom.seek(0x1E);
        this.version = baseRom.readByte();

        baseRom.seek(0x28);
        this.arm9_ramoffset = readFromFile(baseRom, 4);

        baseRom.seek(0x40);
        int fntOffset = readFromFile(baseRom, 4);
        readFromFile(baseRom, 4); // fntSize not needed
        int fatOffset = readFromFile(baseRom, 4);
        int fatSize = readFromFile(baseRom, 4);

        // Read full FAT table
        baseRom.seek(fatOffset);
        fat = new byte[fatSize];
        baseRom.readFully(fat);

        Map<Integer, String> directoryPaths = new HashMap<>();
        directoryPaths.put(0xF000, "");
        int dircount = readFromFile(baseRom, fntOffset + 0x6, 2);
        files = new HashMap<>();
        filesByID = new HashMap<>();

        // read fnt table
        baseRom.seek(fntOffset);
        int[] subTableOffsets = new int[dircount];
        int[] firstFileIDs = new int[dircount];
        int[] parentDirIDs = new int[dircount];
        for (int i = 0; i < dircount && i < 0x1000; i++) {
            subTableOffsets[i] = readFromFile(baseRom, 4) + fntOffset;
            firstFileIDs[i] = readFromFile(baseRom, 2);
            parentDirIDs[i] = readFromFile(baseRom, 2);
        }

        // get dirnames
        String[] directoryNames = new String[dircount];
        Map<Integer, String> filenames = new TreeMap<>();
        Map<Integer, Integer> fileDirectories = new HashMap<>();
        for (int i = 0; i < dircount && i < 0x1000; i++) {
            firstPassDirectory(i, subTableOffsets[i], firstFileIDs[i], directoryNames, filenames, fileDirectories);
        }

        // get full dirnames
        for (int i = 1; i < dircount && i < 0x1000; i++) {
            String dirname = directoryNames[i];
            if (dirname != null) {
                StringBuilder fullDirName = new StringBuilder();
                int curDir = i;
                while (dirname != null && !dirname.isEmpty()) {
                    if (fullDirName.length() > 0) {
                        fullDirName.insert(0, "/");
                    }
                    fullDirName.insert(0, dirname);
                    int parentDir = parentDirIDs[curDir];
                    if (parentDir >= 0xF001 && parentDir <= 0xFFFF) {
                        curDir = parentDir - 0xF000;
                        dirname = directoryNames[curDir];
                    } else {
                        break;
                    }
                }
                directoryPaths.put(i + 0xF000, fullDirName.toString());
            } else {
                directoryPaths.put(i + 0xF000, "");
            }
        }

        // parse files
        for (int fileID : filenames.keySet()) {
            String filename = filenames.get(fileID);
            int directory = fileDirectories.get(fileID);
            String dirPath = directoryPaths.get(directory + 0xF000);
            String fullFilename = filename;
            if (!dirPath.isEmpty()) {
                fullFilename = dirPath + "/" + filename;
            }
            NDSFile nf = new NDSFile(this);
            int start = readFromByteArr(fat, fileID * 8, 4);
            int end = readFromByteArr(fat, fileID * 8 + 4, 4);
            nf.offset = start;
            nf.size = end - start;
            nf.fullPath = fullFilename;
            nf.fileID = fileID;
            files.put(fullFilename, nf);
            filesByID.put(fileID, nf);
        }

        // arm9 overlays
        int arm9_ovl_table_offset = readFromFile(baseRom, 0x50, 4);
        int arm9_ovl_table_size = readFromFile(baseRom, 0x54, 4);
        int arm9_ovl_count = arm9_ovl_table_size / 32;
        byte[] y9table = new byte[arm9_ovl_table_size];
        arm9overlays = new NDSY9Entry[arm9_ovl_count];
        arm9overlaysByFileID = new HashMap<>();
        baseRom.seek(arm9_ovl_table_offset);
        baseRom.readFully(y9table);

        // parse overlays
        for (int i = 0; i < arm9_ovl_count; i++) {
            NDSY9Entry overlay = new NDSY9Entry(this);
            int fileID = readFromByteArr(y9table, i * 32 + 24, 4);
            int start = readFromByteArr(fat, fileID * 8, 4);
            int end = readFromByteArr(fat, fileID * 8 + 4, 4);
            overlay.offset = start;
            overlay.size = end - start;
            overlay.original_size = end - start;
            overlay.fileID = fileID;
            overlay.overlay_id = i;
            overlay.ram_address = readFromByteArr(y9table, i * 32 + 4, 4);
            overlay.ram_size = readFromByteArr(y9table, i * 32 + 8, 4);
            overlay.bss_size = readFromByteArr(y9table, i * 32 + 12, 4);
            overlay.static_start = readFromByteArr(y9table, i * 32 + 16, 4);
            overlay.static_end = readFromByteArr(y9table, i * 32 + 20, 4);
            overlay.compressed_size = readFromByteArr(y9table, i * 32 + 28, 3);
            overlay.compress_flag = y9table[i * 32 + 31] & 0xFF;
            arm9overlays[i] = overlay;
            arm9overlaysByFileID.put(fileID, overlay);
        }
    }

    public void saveTo(String filename) throws IOException {
        this.reopenROM();

        // Initialize new ROM
        RandomAccessFile fNew = new RandomAccessFile(filename, "rw");

        int headersize = readFromFile(this.baseRom, 0x84, 4);
        this.baseRom.seek(0);
        copy(this.baseRom, fNew, headersize);

        // arm9
        int arm9_offset = ((int) (fNew.getFilePointer() + arm9_align)) & (~arm9_align);
        int old_arm9_offset = readFromFile(this.baseRom, 0x20, 4);
        int arm9_size = readFromFile(this.baseRom, 0x2C, 4);
        if (arm9_open && arm9_changed) {
            // custom arm9
            byte[] newARM9 = getARM9();
            if (arm9_compressed) {
                newARM9 = new BLZCoder(null).BLZ_EncodePub(newARM9, true, false, "arm9.bin");
                if (arm9_szoffset > 0) {
                    int newValue = newARM9.length + arm9_ramoffset;
                    writeToByteArr(newARM9, arm9_szoffset, 4, newValue);
                }
            }
            arm9_size = newARM9.length;
            // copy new arm9
            fNew.seek(arm9_offset);
            fNew.write(newARM9);
            // footer?
            if (arm9_has_footer) {
                fNew.write(arm9_footer);
            }

        } else {
            // copy arm9+footer
            this.baseRom.seek(old_arm9_offset);
            fNew.seek(arm9_offset);
            copy(this.baseRom, fNew, arm9_size + 12);
        }

        // arm9 ovl
        int arm9_ovl_offset = (int) fNew.getFilePointer();
        int arm9_ovl_size = arm9overlays.length * 32;

        // don't actually write arm9 ovl yet

        // arm7
        int arm7_offset = arm9_ovl_offset + arm9_ovl_size + arm7_align & (~arm7_align);
        int old_arm7_offset = readFromFile(this.baseRom, 0x30, 4);
        int arm7_size = readFromFile(this.baseRom, 0x3C, 4);
        // copy arm7
        this.baseRom.seek(old_arm7_offset);
        fNew.seek(arm7_offset);
        copy(this.baseRom, fNew, arm7_size);

        // arm7 ovl
        int arm7_ovl_offset = (int) fNew.getFilePointer();
        int old_arm7_ovl_offset = readFromFile(this.baseRom, 0x58, 4);
        int arm7_ovl_size = readFromFile(this.baseRom, 0x5C, 4);

        // copy arm7 ovl
        this.baseRom.seek(old_arm7_ovl_offset);
        fNew.seek(arm7_ovl_offset);
        copy(this.baseRom, fNew, arm7_ovl_size);

        // banner
        int banner_offset = ((int) (fNew.getFilePointer() + banner_align)) & (~banner_align);
        int old_banner_offset = readFromFile(this.baseRom, 0x68, 4);
        int banner_size = 0x840;
        // copy banner
        this.baseRom.seek(old_banner_offset);
        fNew.seek(banner_offset);
        copy(this.baseRom, fNew, banner_size);

        // filename table (doesn't change)
        int fnt_offset = ((int) (fNew.getFilePointer() + fnt_align)) & (~fnt_align);
        int old_fnt_offset = readFromFile(this.baseRom, 0x40, 4);
        int fnt_size = readFromFile(this.baseRom, 0x44, 4);
        // copy fnt
        this.baseRom.seek(old_fnt_offset);
        fNew.seek(fnt_offset);
        copy(this.baseRom, fNew, fnt_size);

        // make space for the FAT table
        int fat_offset = ((int) (fNew.getFilePointer() + fat_align)) & (~fat_align);
        int fat_size = fat.length;

        // Now for actual files
        // Make a new FAT as needed
        // also make a new y9 table
        byte[] newfat = new byte[fat.length];
        byte[] y9table = new byte[arm9overlays.length * 32];
        int base_offset = fat_offset + fat_size;
        int filecount = fat.length / 8;
        for (int fid = 0; fid < filecount; fid++) {
            int offset_of_file = (base_offset + file_align) & (~file_align);
            int file_len = 0;
            boolean copiedCustom = false;
            if (filesByID.containsKey(fid)) {
                byte[] customContents = filesByID.get(fid).getOverrideContents();
                if (customContents != null) {
                    // copy custom
                    fNew.seek(offset_of_file);
                    fNew.write(customContents);
                    copiedCustom = true;
                    file_len = customContents.length;
                }
            }
            if (arm9overlaysByFileID.containsKey(fid)) {
                NDSY9Entry entry = arm9overlaysByFileID.get(fid);
                int overlay_id = entry.overlay_id;
                byte[] customContents = entry.getOverrideContents();
                if (customContents != null) {
                    // copy custom
                    fNew.seek(offset_of_file);
                    fNew.write(customContents);
                    copiedCustom = true;
                    file_len = customContents.length;
                }
                // regardless, fill in y9 table
                writeToByteArr(y9table, overlay_id * 32, 4, overlay_id);
                writeToByteArr(y9table, overlay_id * 32 + 4, 4, entry.ram_address);
                writeToByteArr(y9table, overlay_id * 32 + 8, 4, entry.ram_size);
                writeToByteArr(y9table, overlay_id * 32 + 12, 4, entry.bss_size);
                writeToByteArr(y9table, overlay_id * 32 + 16, 4, entry.static_start);
                writeToByteArr(y9table, overlay_id * 32 + 20, 4, entry.static_end);
                writeToByteArr(y9table, overlay_id * 32 + 24, 4, fid);
                writeToByteArr(y9table, overlay_id * 32 + 28, 3, entry.compressed_size);
                writeToByteArr(y9table, overlay_id * 32 + 31, 1, entry.compress_flag);
            }
            if (!copiedCustom) {
                // copy from original ROM
                int file_starts = readFromByteArr(fat, fid * 8, 4);
                int file_ends = readFromByteArr(fat, fid * 8 + 4, 4);
                file_len = file_ends - file_starts;
                this.baseRom.seek(file_starts);
                fNew.seek(offset_of_file);
                copy(this.baseRom, fNew, file_len);
            }
            // write to new FAT
            writeToByteArr(newfat, fid * 8, 4, offset_of_file);
            writeToByteArr(newfat, fid * 8 + 4, 4, offset_of_file + file_len);
            // update base_offset
            base_offset = offset_of_file + file_len;
        }

        // write new FAT table
        fNew.seek(fat_offset);
        fNew.write(newfat);

        // write y9 table
        fNew.seek(arm9_ovl_offset);
        fNew.write(y9table);

        // tidy up ending
        // base_offset is the end of the last file
        int newfilesize = base_offset;
        newfilesize = (newfilesize + 3) & ~3;
        int application_end_offset = newfilesize;
        if (newfilesize != base_offset) {
            fNew.seek(newfilesize - 1);
            fNew.write(0);
        }

        // calculate device capacity;
        newfilesize |= newfilesize >> 16;
        newfilesize |= newfilesize >> 8;
        newfilesize |= newfilesize >> 4;
        newfilesize |= newfilesize >> 2;
        newfilesize |= newfilesize >> 1;
        newfilesize++;
        if (newfilesize <= 128 * 1024) {
            newfilesize = 128 * 1024;
        }
        int devcap = -18;
        int x = newfilesize;
        while (x != 0) {
            x >>= 1;
            devcap++;
        }
        int devicecap = ((devcap < 0) ? 0 : devcap);

        // Update offsets in ROM header
        writeToFile(fNew, 0x20, 4, arm9_offset);
        writeToFile(fNew, 0x2C, 4, arm9_size);
        writeToFile(fNew, 0x30, 4, arm7_offset);
        writeToFile(fNew, 0x3C, 4, arm7_size);
        writeToFile(fNew, 0x40, 4, fnt_offset);
        writeToFile(fNew, 0x48, 4, fat_offset);
        writeToFile(fNew, 0x50, 4, arm9_ovl_offset);
        writeToFile(fNew, 0x58, 4, arm7_ovl_offset);
        writeToFile(fNew, 0x68, 4, banner_offset);
        writeToFile(fNew, 0x80, 4, application_end_offset);
        writeToFile(fNew, 0x14, 1, devicecap);

        // Update header CRC
        fNew.seek(0);
        byte[] headerForCRC = new byte[0x15E];
        fNew.readFully(headerForCRC);
        short crc = CRC16.calculate(headerForCRC, 0, 0x15E);
        writeToFile(fNew, 0x15E, 2, (crc & 0xFFFF));

        // done
        fNew.close();
        closeROM();
    }

    private void copy(RandomAccessFile from, RandomAccessFile to, int bytes) throws IOException {
        int sizeof_copybuf = Math.min(256 * 1024, bytes);
        byte[] copybuf = new byte[sizeof_copybuf];
        while (bytes > 0) {
            int size2 = (bytes >= sizeof_copybuf) ? sizeof_copybuf : bytes;
            int read = from.read(copybuf, 0, size2);
            to.write(copybuf, 0, read);
            bytes -= read;
        }
    }

    // get rom code for opened rom
    public String getCode() {
        return this.romCode;
    }

    public byte getVersion() {
        return this.version;
    }

    // returns null if file doesn't exist
    public byte[] getFile(String filename) throws IOException {
        if (files.containsKey(filename)) {
            return files.get(filename).getContents();
        } else {
            return null;
        }
    }

    public byte[] getOverlay(int number) throws IOException {
        if (number >= 0 && number < arm9overlays.length) {
            return arm9overlays[number].getContents();
        } else {
            return null;
        }
    }

    public int getOverlayAddress(int number) {
        if (number >= 0 && number < arm9overlays.length) {
            return arm9overlays[number].ram_address;
        } else {
            return -1;
        }
    }

    public byte[] getARM9() throws IOException {
        if (!arm9_open) {
            arm9_open = true;
            this.reopenROM();
            int arm9_offset = readFromFile(this.baseRom, 0x20, 4);
            int arm9_size = readFromFile(this.baseRom, 0x2C, 4);
            byte[] arm9 = new byte[arm9_size];
            this.baseRom.seek(arm9_offset);
            this.baseRom.readFully(arm9);
            originalArm9CRC = FileFunctions.getCRC32(arm9);
            // footer check
            int nitrocode = readFromFile(this.baseRom, 4);
            if (nitrocode == 0xDEC00621) {
                // found a footer
                arm9_footer = new byte[12];
                writeToByteArr(arm9_footer, 0, 4, 0xDEC00621);
                this.baseRom.readFully(arm9_footer, 4, 8);
                arm9_has_footer = true;
            } else {
                arm9_has_footer = false;
            }
            // Any extras?
            while ((readFromByteArr(arm9, arm9.length - 12, 4) == 0xDEC00621)
                    || ((readFromByteArr(arm9, arm9.length - 12, 4) == 0
                            && readFromByteArr(arm9, arm9.length - 8, 4) == 0 && readFromByteArr(arm9, arm9.length - 4,
                            4) == 0))) {
                if (!arm9_has_footer) {
                    arm9_has_footer = true;
                    arm9_footer = new byte[0];
                }
                byte[] newfooter = new byte[arm9_footer.length + 12];
                System.arraycopy(arm9, arm9.length - 12, newfooter, 0, 12);
                System.arraycopy(arm9_footer, 0, newfooter, 12, arm9_footer.length);
                arm9_footer = newfooter;
                byte[] newarm9 = new byte[arm9.length - 12];
                System.arraycopy(arm9, 0, newarm9, 0, arm9.length - 12);
                arm9 = newarm9;
            }
            // Compression?
            arm9_compressed = false;
            arm9_szoffset = 0;
            if (((int) arm9[arm9.length - 5]) >= 0x08 && ((int) arm9[arm9.length - 5]) <= 0x0B) {
                int compSize = readFromByteArr(arm9, arm9.length - 8, 3);
                if (compSize > (arm9.length * 9 / 10) && compSize < (arm9.length * 11 / 10)) {
                    arm9_compressed = true;
                    byte[] compLength = new byte[4];
                    writeToByteArr(compLength, 0, 4, arm9.length + arm9_ramoffset);
                    List<Integer> foundOffsets = RomFunctions.search(arm9, compLength);
                    if (foundOffsets.size() == 1) {
                        arm9_szoffset = foundOffsets.get(0);
                    } else {
                        throw new RandomizerIOException("Could not read ARM9 size offset. May be a bad ROM.");
                    }
                }
            }

            if (arm9_compressed) {
                arm9 = new BLZCoder(null).BLZ_DecodePub(arm9, "arm9.bin");
            }

            // Now actually make the copy or w/e
            if (writingEnabled) {
                File arm9file = new File(tmpFolder + "arm9.bin");
                FileOutputStream fos = new FileOutputStream(arm9file);
                fos.write(arm9);
                fos.close();
                arm9file.deleteOnExit();
                this.arm9_ramstored = null;
                return arm9;
            } else {
                this.arm9_ramstored = arm9;
                byte[] newcopy = new byte[arm9.length];
                System.arraycopy(arm9, 0, newcopy, 0, arm9.length);
                return newcopy;
            }
        } else {
            if (writingEnabled) {
                return FileFunctions.readFileFullyIntoBuffer(tmpFolder + "arm9.bin");
            } else {
                byte[] newcopy = new byte[this.arm9_ramstored.length];
                System.arraycopy(this.arm9_ramstored, 0, newcopy, 0, this.arm9_ramstored.length);
                return newcopy;
            }
        }
    }

    // returns null if file doesn't exist
    public void writeFile(String filename, byte[] data) throws IOException {
        if (files.containsKey(filename)) {
            files.get(filename).writeOverride(data);
        }
    }

    public void writeOverlay(int number, byte[] data) throws IOException {
        if (number >= 0 && number <= arm9overlays.length) {
            arm9overlays[number].writeOverride(data);
        }
    }

    public void writeARM9(byte[] arm9) throws IOException {
        if (!arm9_open) {
            getARM9();
        }
        arm9_changed = true;
        if (writingEnabled) {
            FileOutputStream fos = new FileOutputStream(new File(tmpFolder + "arm9.bin"));
            fos.write(arm9);
            fos.close();
        } else {
            if (this.arm9_ramstored.length == arm9.length) {
                // copy new in
                System.arraycopy(arm9, 0, this.arm9_ramstored, 0, arm9.length);
            } else {
                // make new array
                this.arm9_ramstored = null;
                this.arm9_ramstored = new byte[arm9.length];
                System.arraycopy(arm9, 0, this.arm9_ramstored, 0, arm9.length);
            }
        }
    }

    private void firstPassDirectory(int dir, int subTableOffset, int firstFileID, String[] directoryNames,
            Map<Integer, String> filenames, Map<Integer, Integer> fileDirectories) throws IOException {
        // read subtable
        baseRom.seek(subTableOffset);
        while (true) {
            int control = baseRom.read();
            if (control == 0x00) {
                // done
                break;
            }
            int namelen = control & 0x7F;
            byte[] rawname = new byte[namelen];
            baseRom.readFully(rawname);
            String name = new String(rawname, "US-ASCII");
            if ((control & 0x80) > 0x00) {
                // sub-directory
                int subDirectoryID = readFromFile(baseRom, 2);
                directoryNames[subDirectoryID - 0xF000] = name;
            } else {
                int fileID = firstFileID++;
                filenames.put(fileID, name);
                fileDirectories.put(fileID, dir);
            }
        }
    }

    public void printRomDiagnostics(PrintStream logStream) {
        List<String> overlayList = new ArrayList<>();
        List<String> fileList = new ArrayList<>();
        for (Map.Entry<Integer, NDSY9Entry> entry : arm9overlaysByFileID.entrySet()) {
            if (entry.getValue().originalCRC != 0) {
                overlayList.add("overlay9_" + entry.getKey() + ": " + String.format("%08X", entry.getValue().originalCRC));
            }
        }
        for (Map.Entry<String, NDSFile> entry : files.entrySet()) {
            if (entry.getValue().originalCRC != 0) {
                fileList.add(entry.getKey() + ": " + String.format("%08X", entry.getValue().originalCRC));
            }
        }
        Collections.sort(overlayList);
        Collections.sort(fileList);
        Path p = Paths.get(this.romFilename);
        logStream.println("File name: " + p.getFileName().toString());
        logStream.println("arm9: " + String.format("%08X", originalArm9CRC));
        for (String overlayLog : overlayList) {
            logStream.println(overlayLog);
        }
        for (String fileLog : fileList) {
            logStream.println(fileLog);
        }
    }

    public String getTmpFolder() {
        return tmpFolder;
    }

    public RandomAccessFile getBaseRom() {
        return baseRom;
    }

    public boolean isWritingEnabled() {
        return writingEnabled;
    }

    private int readFromByteArr(byte[] data, int offset, int size) {
        int result = 0;
        for (int i = 0; i < size; i++) {
            result |= (data[i + offset] & 0xFF) << (i * 8);
        }
        return result;
    }

    private void writeToByteArr(byte[] data, int offset, int size, int value) {
        for (int i = 0; i < size; i++) {
            data[offset + i] = (byte) ((value >> (i * 8)) & 0xFF);
        }
    }

    private int readFromFile(RandomAccessFile file, int size) throws IOException {
        return readFromFile(file, -1, size);
    }

    // use -1 offset to read from current position
    // useful if you want to read blocks
    private int readFromFile(RandomAccessFile file, int offset, int size) throws IOException {
        byte[] buf = new byte[size];
        if (offset >= 0)
            file.seek(offset);
        file.readFully(buf);
        int result = 0;
        for (int i = 0; i < size; i++) {
            result |= (buf[i] & 0xFF) << (i * 8);
        }
        return result;
    }

    public void writeToFile(RandomAccessFile file, int size, int value) throws IOException {
        writeToFile(file, -1, size, value);
    }

    private void writeToFile(RandomAccessFile file, int offset, int size, int value) throws IOException {
        byte[] buf = new byte[size];
        for (int i = 0; i < size; i++) {
            buf[i] = (byte) ((value >> (i * 8)) & 0xFF);
        }
        if (offset >= 0)
            file.seek(offset);
        file.write(buf);
    }

}
