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
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class NCCH {

    private String romFilename;
    private RandomAccessFile baseRom;
    private long ncchStartingOffset;
    private String titleId;
    private long exefsOffset, romfsOffset, fileDataOffset;
    private ExefsFileHeader codeFileHeader;
    private Map<String, RomfsFile> romfsFiles;
    private boolean romOpen;
    private String tmpFolder;
    private boolean writingEnabled;
    private boolean codeCompressed, codeOpen, codeChanged;
    private byte[] codeRamstored;

    private static final int media_unit_size = 0x200;
    private static final int exefs_header_size = 0x200;
    private static final int romfs_header_size = 0x5C;
    private static final int romfs_magic_1 = 0x49564643;
    private static final int romfs_magic_2 = 0x00000100;
    private static final int level3_header_size = 0x28;
    private static final int metadata_unused = 0xFFFFFFFF;

    public NCCH(String filename, long ncchStartingOffset, String titleId) throws IOException {
        this.romFilename = filename;
        this.baseRom = new RandomAccessFile(filename, "r");
        this.ncchStartingOffset = ncchStartingOffset;
        this.titleId = titleId;
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
        if (!this.isDecrypted()) {
            return;
        }
        exefsOffset = ncchStartingOffset + FileFunctions.readLittleEndianIntFromFile(baseRom, ncchStartingOffset + 0x1A0) * media_unit_size;
        romfsOffset = ncchStartingOffset + FileFunctions.readLittleEndianIntFromFile(baseRom, ncchStartingOffset + 0x1B0) * media_unit_size;
        baseRom.seek(ncchStartingOffset + 0x20D);
        byte systemControlInfoFlags = baseRom.readByte();
        codeCompressed = (systemControlInfoFlags & 0x01) != 0;
        readExefs();
        readRomfs();
    }

    private void readExefs() throws IOException {
        byte[] exefsHeaderData = new byte[exefs_header_size];
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
        byte[] romfsHeaderData = new byte[romfs_header_size];
        baseRom.seek(romfsOffset);
        baseRom.readFully(romfsHeaderData);
        int magic1 = FileFunctions.readFullInt(romfsHeaderData, 0x00);
        int magic2 = FileFunctions.readFullInt(romfsHeaderData, 0x04);
        if (magic1 != romfs_magic_1 || magic2 != romfs_magic_2) {
            // Not a valid romfs
            return;
        }
        int masterHashSize = FileFunctions.readFullIntLittleEndian(romfsHeaderData, 0x08);
        int level3HashBlockSize = 1 << FileFunctions.readFullIntLittleEndian(romfsHeaderData, 0x4C);
        long level3Offset = romfsOffset + alignLong(0x60 + masterHashSize, level3HashBlockSize);

        byte[] level3HeaderData = new byte[level3_header_size];
        baseRom.seek(level3Offset);
        baseRom.readFully(level3HeaderData);
        int headerLength = FileFunctions.readFullIntLittleEndian(level3HeaderData, 0x00);
        if (headerLength != level3_header_size) {
            // Not a valid romfs
            return;
        }
        int directoryMetadataOffset = FileFunctions.readFullIntLittleEndian(level3HeaderData, 0x0C);
        int directoryMetadataLength = FileFunctions.readFullIntLittleEndian(level3HeaderData, 0x10);
        int fileMetadataOffset = FileFunctions.readFullIntLittleEndian(level3HeaderData, 0x1c);
        int fileMetadataLength = FileFunctions.readFullIntLittleEndian(level3HeaderData, 0x20);
        int fileDataOffsetFromHeaderStart = FileFunctions.readFullIntLittleEndian(level3HeaderData, 0x24);
        fileDataOffset = level3Offset + fileDataOffsetFromHeaderStart;

        byte[] directoryMetadataBlock = new byte[directoryMetadataLength];
        baseRom.seek(level3Offset + directoryMetadataOffset);
        baseRom.readFully(directoryMetadataBlock);
        byte[] fileMetadataBlock = new byte[fileMetadataLength];
        baseRom.seek(level3Offset + fileMetadataOffset);
        baseRom.readFully(fileMetadataBlock);
        romfsFiles = new TreeMap<>();
        visitDirectory(0, "", directoryMetadataBlock, fileMetadataBlock);
    }

    private void visitDirectory(int offset, String rootPath, byte[] directoryMetadataBlock, byte[] fileMetadataBlock) {
        DirectoryMetadata metadata = new DirectoryMetadata(directoryMetadataBlock, offset);
        String currentPath = rootPath;
        if (!metadata.name.equals("")) {
            currentPath = rootPath + metadata.name + "/";
        }

        if (metadata.firstFileOffset != metadata_unused) {
            visitFile(metadata.firstFileOffset, currentPath, fileMetadataBlock);
        }
        if (metadata.firstChildDirectoryOffset != metadata_unused) {
            visitDirectory(metadata.firstChildDirectoryOffset, currentPath, directoryMetadataBlock, fileMetadataBlock);
        }
        if (metadata.siblingDirectoryOffset != metadata_unused) {
            visitDirectory(metadata.siblingDirectoryOffset, rootPath, directoryMetadataBlock, fileMetadataBlock);
        }
    }

    private void visitFile(int offset, String rootPath, byte[] fileMetadataBlock) {
        FileMetadata metadata = new FileMetadata(fileMetadataBlock, offset);
        String currentPath = rootPath + metadata.name;
        RomfsFile file = new RomfsFile(this);
        file.offset = fileDataOffset + metadata.fileDataOffset;
        file.size = (int) metadata.fileDataLength;  // no Pokemon game has a file larger than unsigned int max
        file.fullPath = currentPath;
        romfsFiles.put(currentPath, file);
        if (metadata.siblingFileOffset != metadata_unused) {
            visitFile(metadata.siblingFileOffset, rootPath, fileMetadataBlock);
        }
    }

    public void saveAsLayeredFS(String outputPath) throws IOException {
        String layeredFSRootPath = outputPath + File.separator + titleId + File.separator;
        File layeredFSRootDir = new File(layeredFSRootPath);
        if (!layeredFSRootDir.exists()) {
            layeredFSRootDir.mkdir();
        }
        String romfsRootPath = layeredFSRootPath + "romfs" + File.separator;
        File romfsDir = new File(romfsRootPath);
        if (!romfsDir.exists()) {
            romfsDir.mkdir();
        }

        if (codeChanged) {
            byte[] code = getCode();
            FileOutputStream fos = new FileOutputStream(new File(layeredFSRootPath + "code.bin"));
            fos.write(code);
            fos.close();
        }

        for (Map.Entry<String, RomfsFile> entry : romfsFiles.entrySet()) {
            RomfsFile file = entry.getValue();
            if (file.fileChanged) {
                writeRomfsFileToLayeredFS(file, romfsRootPath);
            }
        }
    }

    private void writeRomfsFileToLayeredFS(RomfsFile file, String layeredFSRootPath) throws IOException {
        String[] romfsPathComponents = file.fullPath.split("/");
        StringBuffer buffer = new StringBuffer(layeredFSRootPath);
        for (int i = 0; i < romfsPathComponents.length - 1; i++) {
            buffer.append(romfsPathComponents[i]);
            buffer.append(File.separator);
            File currentDir = new File(buffer.toString());
            if (!currentDir.exists()) {
                currentDir.mkdir();
            }
        }
        buffer.append(romfsPathComponents[romfsPathComponents.length - 1]);
        String romfsFilePath = buffer.toString();
        FileOutputStream fos = new FileOutputStream(new File(romfsFilePath));
        fos.write(file.getOverrideContents());
        fos.close();
    }

    public boolean isDecrypted() throws IOException {
        long ncchFlagOffset = ncchStartingOffset + 0x188;
        byte[] ncchFlags = new byte[8];
        baseRom.seek(ncchFlagOffset);
        baseRom.readFully(ncchFlags);
        return (ncchFlags[7] & 0x4) != 0;
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

            // File header offsets are from the start of the exefs but *exclude* the
            // size of the exefs header, so we need to add it back ourselves.
            baseRom.seek(exefsOffset + exefs_header_size + codeFileHeader.offset);
            baseRom.readFully(code);

            if (codeCompressed) {
                code = new BLZCoder(null).BLZ_DecodePub(code, ".code");
            }

            // Now actually make the copy or w/e
            if (writingEnabled) {
                File arm9file = new File(tmpFolder + ".code");
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
                return FileFunctions.readFileFullyIntoBuffer(tmpFolder + ".code");
            } else {
                byte[] newcopy = new byte[this.codeRamstored.length];
                System.arraycopy(this.codeRamstored, 0, newcopy, 0, this.codeRamstored.length);
                return newcopy;
            }
        }
    }

    public void writeCode(byte[] code) throws IOException {
        if (!codeOpen) {
            getCode();
        }
        codeChanged = true;
        if (writingEnabled) {
            FileOutputStream fos = new FileOutputStream(new File(tmpFolder + ".code"));
            fos.write(code);
            fos.close();
        } else {
            if (this.codeRamstored.length == code.length) {
                // copy new in
                System.arraycopy(code, 0, this.codeRamstored, 0, code.length);
            } else {
                // make new array
                this.codeRamstored = null;
                this.codeRamstored = new byte[code.length];
                System.arraycopy(code, 0, this.codeRamstored, 0, code.length);
            }
        }
    }

    // returns null if file doesn't exist
    public byte[] getFile(String filename) throws IOException {
        if (romfsFiles.containsKey(filename)) {
            return romfsFiles.get(filename).getContents();
        } else {
            return null;
        }
    }

    public void writeFile(String filename, byte[] data) throws IOException {
        if (romfsFiles.containsKey(filename)) {
            romfsFiles.get(filename).writeOverride(data);
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

    public static long alignLong(long num, long alignment) {
        long mask = ~(alignment - 1);
        return (num + (alignment - 1)) & mask;
    }

    private class ExefsFileHeader {
        public String filename;
        public int offset;
        public int size;

        public ExefsFileHeader(byte[] exefsHeaderData, int fileHeaderOffset) {
            byte[] filenameBytes = new byte[0x8];
            System.arraycopy(exefsHeaderData, fileHeaderOffset, filenameBytes, 0, 0x8);
            this.filename = new String(filenameBytes, StandardCharsets.UTF_8).trim();
            this.offset = FileFunctions.readFullIntLittleEndian(exefsHeaderData, fileHeaderOffset + 0x08);
            this.size = FileFunctions.readFullIntLittleEndian(exefsHeaderData, fileHeaderOffset + 0x0C);
        }

        public boolean isValid() {
            return this.filename != "" && this.size != 0;
        }
    }

    private class DirectoryMetadata {
        public int parentDirectoryOffset;
        public int siblingDirectoryOffset;
        public int firstChildDirectoryOffset;
        public int firstFileOffset;
        public int nextDirectoryInHashBucketOffset;
        public int nameLength;
        public String name;

        public DirectoryMetadata(byte[] directoryMetadataBlock, int offset) {
            parentDirectoryOffset = FileFunctions.readFullIntLittleEndian(directoryMetadataBlock, offset);
            siblingDirectoryOffset = FileFunctions.readFullIntLittleEndian(directoryMetadataBlock, offset + 0x04);
            firstChildDirectoryOffset = FileFunctions.readFullIntLittleEndian(directoryMetadataBlock, offset + 0x08);
            firstFileOffset = FileFunctions.readFullIntLittleEndian(directoryMetadataBlock, offset + 0x0C);
            nextDirectoryInHashBucketOffset = FileFunctions.readFullIntLittleEndian(directoryMetadataBlock, offset + 0x10);
            nameLength = FileFunctions.readFullIntLittleEndian(directoryMetadataBlock, offset + 0x14);
            name = "";
            if (nameLength != metadata_unused) {
                byte[] nameBytes = new byte[nameLength];
                System.arraycopy(directoryMetadataBlock, offset + 0x18, nameBytes, 0, nameLength);
                name = new String(nameBytes, StandardCharsets.UTF_16LE).trim();
            }
        }
    }

    private class FileMetadata {
        public int parentDirectoryOffset;
        public int siblingFileOffset;
        public long fileDataOffset;
        public long fileDataLength;
        public int nextFileInHashBucketOffset;
        public int nameLength;
        public String name;

        public FileMetadata(byte[] fileMetadataBlock, int offset) {
            parentDirectoryOffset = FileFunctions.readFullIntLittleEndian(fileMetadataBlock, offset);
            siblingFileOffset = FileFunctions.readFullIntLittleEndian(fileMetadataBlock, offset + 0x04);
            fileDataOffset = FileFunctions.readFullLongLittleEndian(fileMetadataBlock, offset + 0x08);
            fileDataLength = FileFunctions.readFullLongLittleEndian(fileMetadataBlock, offset + 0x10);
            nextFileInHashBucketOffset = FileFunctions.readFullIntLittleEndian(fileMetadataBlock, offset + 0x18);
            nameLength = FileFunctions.readFullIntLittleEndian(fileMetadataBlock, offset + 0x1C);
            name = "";
            if (nameLength != metadata_unused) {
                byte[] nameBytes = new byte[nameLength];
                System.arraycopy(fileMetadataBlock, offset + 0x20, nameBytes, 0, nameLength);
                name = new String(nameBytes, StandardCharsets.UTF_16LE).trim();
            }
        }
    }
}
