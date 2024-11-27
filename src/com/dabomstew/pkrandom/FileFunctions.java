package com.dabomstew.pkrandom;

/*----------------------------------------------------------------------------*/
/*--  FileFunctions.java - functions relating to file I/O.                  --*/
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

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.zip.CRC32;

public class FileFunctions {

    public static File fixFilename(File original, String defaultExtension) {
        return fixFilename(original, defaultExtension, new ArrayList<>());
    }

    // Behavior:
    // if file has no extension, add defaultExtension
    // if there are banned extensions & file has a banned extension, replace
    // with defaultExtension
    // else, leave as is
    public static File fixFilename(File original, String defaultExtension, List<String> bannedExtensions) {
        String absolutePath = original.getAbsolutePath();
        for (String bannedExtension: bannedExtensions) {
            if (absolutePath.endsWith("." + bannedExtension)) {
                absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf('.') + 1) + defaultExtension;
                break;
            }
        }
        if (!absolutePath.endsWith("." + defaultExtension)) {
            absolutePath += "." + defaultExtension;
        }
        return new File(absolutePath);
    }

    private static List<String> overrideFiles = Arrays.asList(SysConstants.customNamesFile,
            SysConstants.tclassesFile, SysConstants.tnamesFile, SysConstants.nnamesFile);

    public static boolean configExists(String filename) {
        if (overrideFiles.contains(filename)) {
            File fh = new File(SysConstants.ROOT_PATH + filename);
            if (fh.exists() && fh.canRead()) {
                return true;
            }
            fh = new File("./" + filename);
            if (fh.exists() && fh.canRead()) {
                return true;
            }
        }
        return FileFunctions.class.getResource("/com/dabomstew/pkrandom/config/" + filename) != null;
    }

    public static InputStream openConfig(String filename) throws FileNotFoundException {
        if (overrideFiles.contains(filename)) {
            File fh = new File(SysConstants.ROOT_PATH + filename);
            if (fh.exists() && fh.canRead()) {
                return new FileInputStream(fh);
            }
            fh = new File("./" + filename);
            if (fh.exists() && fh.canRead()) {
                return new FileInputStream(fh);
            }
        }
        return FileFunctions.class.getResourceAsStream("/com/dabomstew/pkrandom/config/" + filename);
    }

    public static CustomNamesSet getCustomNames() throws IOException {
        InputStream is = openConfig(SysConstants.customNamesFile);
        CustomNamesSet cns = new CustomNamesSet(is);
        is.close();
        return cns;
    }

    public static long readFullLong(byte[] data, int offset) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put(data, offset, 8);
        buf.rewind();
        return buf.getLong();
    }

    public static int readFullInt(byte[] data, int offset) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put(data, offset, 4);
        buf.rewind();
        return buf.getInt();
    }

    public static int readFullIntBigEndian(byte[] data, int offset) {
        ByteBuffer buf = ByteBuffer.allocate(4).put(data, offset, 4);
        buf.rewind();
        return buf.getInt();
    }

    public static int read2ByteIntBigEndian(byte[] data, int index) {
        return (data[index + 1] & 0xFF) | ((data[index] & 0xFF) << 8);
    }

    public static int read2ByteInt(byte[] data, int index) {
        return (data[index] & 0xFF) | ((data[index + 1] & 0xFF) << 8);
    }

    public static void write2ByteInt(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

    public static void writeFullInt(byte[] data, int offset, int value) {
        byte[] valueBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
        System.arraycopy(valueBytes, 0, data, offset, 4);
    }

    public static void writeFullIntBigEndian(byte[] data, int offset, int value) {
        byte[] valueBytes = ByteBuffer.allocate(4).putInt(value).array();
        System.arraycopy(valueBytes, 0, data, offset, 4);
    }

    public static void writeFullLong(byte[] data, int offset, long value) {
        byte[] valueBytes = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array();
        System.arraycopy(valueBytes, 0, data, offset, 8);
    }

    public static byte[] readFileFullyIntoBuffer(String filename) throws IOException {
        File fh = new File(filename);
        if (!fh.exists() || !fh.isFile() || !fh.canRead()) {
            throw new FileNotFoundException(filename);
        }
        long fileSize = fh.length();
        if (fileSize > Integer.MAX_VALUE) {
            throw new IOException(filename + " is too long to read in as a byte-array.");
        }
        FileInputStream fis = new FileInputStream(filename);
        byte[] buf = readFullyIntoBuffer(fis, (int) fileSize);
        fis.close();
        return buf;
    }

    public static byte[] readFullyIntoBuffer(InputStream in, int bytes) throws IOException {
        byte[] buf = new byte[bytes];
        readFully(in, buf, 0, bytes);
        return buf;
    }

    private static void readFully(InputStream in, byte[] buf, int offset, int length) throws IOException {
        int offs = 0, read;
        while (offs < length && (read = in.read(buf, offs + offset, length - offs)) != -1) {
            offs += read;
        }
    }

    public static int read2ByteBigEndianIntFromFile(RandomAccessFile file, long offset) throws IOException {
        byte[] buf = new byte[2];
        file.seek(offset);
        file.readFully(buf);
        return read2ByteIntBigEndian(buf, 0);
    }

    public static int readBigEndianIntFromFile(RandomAccessFile file, long offset) throws IOException {
        byte[] buf = new byte[4];
        file.seek(offset);
        file.readFully(buf);
        return readFullIntBigEndian(buf, 0);
    }

    public static int readIntFromFile(RandomAccessFile file, long offset) throws IOException {
        byte[] buf = new byte[4];
        file.seek(offset);
        file.readFully(buf);
        return readFullInt(buf, 0);
    }

    public static void writeBytesToFile(String filename, byte[] data) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);
        fos.write(data);
        fos.close();
    }

    public static byte[] getConfigAsBytes(String filename) throws IOException {
        InputStream in = openConfig(filename);
        byte[] buf = readFullyIntoBuffer(in, in.available());
        in.close();
        return buf;
    }

    public static int getFileChecksum(String filename) {
        try {
            return getFileChecksum(openConfig(filename));
        } catch (IOException e) {
            return 0;
        }
    }

    private static int getFileChecksum(InputStream stream) {
        try {
            Scanner sc = new Scanner(stream, "UTF-8");
            CRC32 checksum = new CRC32();
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) {
                    checksum.update(line.getBytes("UTF-8"));
                }
            }
            sc.close();
            return (int) checksum.getValue();
        } catch (IOException e) {
            return 0;
        }
    }

    public static boolean checkOtherCRC(byte[] data, int byteIndex, int switchIndex, String filename, int offsetInData) {
        // If the switch at data[byteIndex].switchIndex is on, then check that
        // the CRC at data[offsetInData] ... data[offsetInData+3] matches the
        // CRC of filename.
        // If not, return false.
        // If any other case, return true.
        int switches = data[byteIndex] & 0xFF;
        if (((switches >> switchIndex) & 0x01) == 0x01) {
            // have to check the CRC
            int crc = readFullIntBigEndian(data, offsetInData);

            return getFileChecksum(filename) == crc;
        }
        return true;
    }

    public static long getCRC32(byte[] data) {
        CRC32 checksum = new CRC32();
        checksum.update(data);
        return checksum.getValue();
    }

    private static byte[] getCodeTweakFile(String filename) throws IOException {
        InputStream is = FileFunctions.class.getResourceAsStream("/com/dabomstew/pkrandom/patches/" + filename);
        byte[] buf = readFullyIntoBuffer(is, is.available());
        is.close();
        return buf;
    }

    public static byte[] downloadFile(String url) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int count;
        while ((count = in.read(buf, 0, 1024)) != -1) {
            out.write(buf, 0, count);
        }
        in.close();
        return out.toByteArray();
    }

    public static void applyPatch(byte[] rom, String patchName) throws IOException {
        byte[] patch = getCodeTweakFile(patchName + ".ips");

        // check sig
        int patchlen = patch.length;
        if (patchlen < 8 || patch[0] != 'P' || patch[1] != 'A' || patch[2] != 'T' || patch[3] != 'C' || patch[4] != 'H') {
            throw new IOException("not a valid IPS file");
        }

        // records
        int offset = 5;
        while (offset + 2 < patchlen) {
            int writeOffset = readIPSOffset(patch, offset);
            if (writeOffset == 0x454f46) {
                // eof, done
                return;
            }
            offset += 3;
            if (offset + 1 >= patchlen) {
                // error
                throw new IOException("abrupt ending to IPS file, entry cut off before size");
            }
            int size = readIPSSize(patch, offset);
            offset += 2;
            if (size == 0) {
                // RLE
                if (offset + 1 >= patchlen) {
                    // error
                    throw new IOException("abrupt ending to IPS file, entry cut off before RLE size");
                }
                int rleSize = readIPSSize(patch, offset);
                if (writeOffset + rleSize > rom.length) {
                    // error
                    throw new IOException("trying to patch data past the end of the ROM file");
                }
                offset += 2;
                if (offset >= patchlen) {
                    // error
                    throw new IOException("abrupt ending to IPS file, entry cut off before RLE byte");
                }
                byte rleByte = patch[offset++];
                for (int i = writeOffset; i < writeOffset + rleSize; i++) {
                    rom[i] = rleByte;
                }
            } else {
                if (offset + size > patchlen) {
                    // error
                    throw new IOException("abrupt ending to IPS file, entry cut off before end of data block");
                }
                if (writeOffset + size > rom.length) {
                    // error
                    throw new IOException("trying to patch data past the end of the ROM file");
                }
                System.arraycopy(patch, offset, rom, writeOffset, size);
                offset += size;
            }
        }
        throw new IOException("improperly terminated IPS file");
    }

    private static int readIPSOffset(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 16) | ((data[offset + 1] & 0xFF) << 8) | (data[offset + 2] & 0xFF);
    }

    private static int readIPSSize(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }

    public static byte[] convIntArrToByteArr(int[] arg) {
        byte[] out = new byte[arg.length];
        for (int i = 0; i < arg.length; i++) {
            out[i] = (byte) arg[i];
        }
        return out;
    }
}