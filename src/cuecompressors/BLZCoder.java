package cuecompressors;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import com.dabomstew.pkrandom.FileFunctions;

/*----------------------------------------------------------------------------*/
/*--  BLZCoder.java - Bottom LZ coding for Nintendo GBA/DS/3DS              --*/
/*--                                                                        --*/
/*--  Contains code based on "pk3DS", copyright (C) Kaphotics               --*/
/*--  Contains code based on "pokemon-x-y-icons", copyright (C) CatTrinket  --*/
/*--  Contains code based on "blz.c", copyright (C) 2011 CUE                --*/
/*--  Above-listed code ported to Java by Dabomstew and UPR-ZX team under   --*/
/*--  the terms of the GPL:                                                 --*/
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

public class BLZCoder {

    public static void main(String[] args) {
        new BLZCoder(args);
    }

    private static final int CMD_DECODE = 0;
    private static final int CMD_ENCODE = 1;

    private static final int BLZ_NORMAL = 0;
    private static final int BLZ_BEST = 1;

    private static final int BLZ_SHIFT = 1;
    private static final int BLZ_MASK = 0x80;

    private static final int BLZ_THRESHOLD = 2;
    private static final int BLZ_N = 0x1002;
    private static final int BLZ_F = 0x12;

    private static final int RAW_MAXIM = 0x00FFFFFF;

    private static final int BLZ_MAXIM = 0x01400000;

    private boolean arm9;

    public BLZCoder(String[] args) {

        int cmd, mode = 0, arg;

        if (args == null) {
            // going to be used internally
            return;
        }

        Title();

        if (args.length < 1) {
            Usage();
        }

        if (args[0].equalsIgnoreCase("-d")) {
            cmd = CMD_DECODE;
        } else if (args[0].equalsIgnoreCase("-en") || args[0].equalsIgnoreCase("-en9")) {
            cmd = CMD_ENCODE;
            mode = BLZ_NORMAL;
        } else if (args[0].equalsIgnoreCase("-eo") || args[0].equalsIgnoreCase("-eo9")) {
            cmd = CMD_ENCODE;
            mode = BLZ_BEST;
        } else {
            EXIT("Command not supported\n");
            return;
        }

        if (args.length < 2) {
            EXIT("Filename not specified\n");
        }

        switch (cmd) {
        case CMD_DECODE:
            for (arg = 1; arg < args.length; arg++)
                BLZ_Decode(args[arg]);
            break;
        case CMD_ENCODE:
            arm9 = (args[0].length() > 3 && args[0].charAt(3) == '9');
            for (arg = 1; arg < args.length; arg++)
                BLZ_Encode(args[arg], mode);
            break;
        }

        System.out.print("\nDone\n");
    }

    private void Usage() {
        EXIT("Usage: BLZ command filename [filename [...]]\n" + "\n" + "command:\n"
                + "  -d ....... decode 'filename'\n" + "  -en[9] ... encode 'filename', normal mode\n"
                + "  -eo[9] ... encode 'filename', optimal mode (LZ-CUE)\n" + "\n"
                + "* '9' compress an ARM9 file with 0x4000 bytes decoded\n"
                + "* multiple filenames and wildcards are permitted\n"
                + "* the original file is overwritten with the new file\n"
                + "* this codification is used in the DS overlay files\n");

    }

    private void Title() {
        System.out.print("\n");
        System.out.print("BLZ - (c) CUE 2011\n");
        System.out.print("Bottom LZ coding for Nintendo GBA/DS\n");
        System.out.print("\n");
    }

    private void EXIT(String text) {
        System.out.print(text);
        System.exit(0);
    }

    private void Save(String filename, int[] buffer, int length) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            byte[] write = new byte[length];
            for (int i = 0; i < length; i++) {
                write[i] = (byte) buffer[i];
            }
            fos.write(write);
            fos.close();
        } catch (IOException e) {
            EXIT("\nFile write error\n");
        }

    }

    private void BLZ_Decode(String filename) {
        try {
            System.out.printf("- decoding '%s'", filename);
            long startTime = System.currentTimeMillis();
            byte[] buf = FileFunctions.readFileFullyIntoBuffer(filename);
            BLZResult result = BLZ_Decode(buf);
            if (result != null)
                Save(filename, result.buffer, result.length);
            System.out.print(" - done, time=" + (System.currentTimeMillis() - startTime) + "ms");
            System.out.print("\n");
        } catch (IOException e) {
            EXIT("\nFile read error\n");
        }
    }

    public byte[] BLZ_DecodePub(byte[] data, String reference) {
        if (reference.equals("GARC")) {
            return LZSS_Decode(data);
        } else {
            BLZResult result = BLZ_Decode(data);
            if (result != null) {
                byte[] retbuf = new byte[result.length];
                for (int i = 0; i < result.length; i++) {
                    retbuf[i] = (byte) result.buffer[i];
                }
                return retbuf;
            } else {
                return null;
            }
        }
    }

    private BLZResult BLZ_Decode(byte[] data) {
        int[] pak_buffer, raw_buffer;
        int pak, raw, pak_end, raw_end;
        int pak_len, raw_len, len, pos, inc_len, hdr_len, enc_len, dec_len;
        int flags = 0, mask;

        pak_buffer = prepareData(data);
        pak_len = pak_buffer.length - 3;

        inc_len = readUnsigned(pak_buffer, pak_len - 4);
        if (inc_len < 1) {
            System.out.print(", WARNING: not coded file!");
            enc_len = 0;
            dec_len = pak_len;
            pak_len = 0;
            raw_len = dec_len;
        } else {
            if (pak_len < 8) {
                EXIT("\nFile has a bad header\n");
                return null;
            }
            hdr_len = pak_buffer[pak_len - 5];
            if (hdr_len < 8 || hdr_len > 0xB) {
                EXIT("\nBad header length\n");
                return null;
            }
            if (pak_len <= hdr_len) {
                EXIT("\nBad length\n");
                return null;
            }
            enc_len = readUnsigned(pak_buffer, pak_len - 8) & 0x00FFFFFF;
            dec_len = pak_len - enc_len;
            pak_len = enc_len - hdr_len;
            raw_len = dec_len + enc_len + inc_len;
            if (raw_len > RAW_MAXIM) {
                EXIT("\nBad decoded length\n");
                return null;
            }
        }

        raw_buffer = new int[raw_len];

        pak = 0;
        raw = 0;
        pak_end = dec_len + pak_len;
        raw_end = raw_len;

        for (len = 0; len < dec_len; len++) {
            raw_buffer[raw++] = pak_buffer[pak++];
        }

        BLZ_Invert(pak_buffer, dec_len, pak_len);

        mask = 0;

        while (raw < raw_end) {
            if ((mask = (mask >>> BLZ_SHIFT)) == 0) {
                if (pak == pak_end) {
                    break;
                }
                flags = pak_buffer[pak++];
                mask = BLZ_MASK;
            }

            if ((flags & mask) == 0) {
                if (pak == pak_end) {
                    break;
                }
                raw_buffer[raw++] = pak_buffer[pak++];
            } else {
                if ((pak + 1) >= pak_end) {
                    break;
                }
                pos = pak_buffer[pak++] << 8;
                pos |= pak_buffer[pak++];
                len = (pos >>> 12) + BLZ_THRESHOLD + 1;
                if (raw + len > raw_end) {
                    System.out.print(", WARNING: wrong decoded length!");
                    len = raw_end - raw;
                }
                pos = (pos & 0xFFF) + 3;
                while ((len--) > 0) {
                    int charHere = raw_buffer[raw - pos];
                    raw_buffer[raw++] = charHere;
                }
            }
        }

        BLZ_Invert(raw_buffer, dec_len, raw_len - dec_len);

        raw_len = raw;

        if (raw != raw_end) {
            System.out.print(", WARNING: unexpected end of encoded file!");
        }

        return new BLZResult(raw_buffer, raw_len);

    }

    // LZSS Decoding ported to Java (based on pk3DS by Kaphotics and pokemon-x-y-icons by CatTrinket)

    private byte[] LZSS_Decode(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        if (buf.get(0) != 0x11) {
            System.err.println("Not a valid LZSS compressed file");
            return null;
        }
        int decSize = buf.getInt() >>> 8;
        if (decSize == 0) {
            decSize = buf.getInt();
        }

        ByteBuffer outBuf = ByteBuffer.allocate(decSize);
        byte flags = 0;
        int mask = 1;
        while (outBuf.position() < decSize) {
            if (mask == 1) {
                if (buf.position() >= data.length) {
                    System.err.println("Not enough data");
                    return null;
                }
                flags = buf.get();
                mask = 0x80;
            } else {
                mask >>>= 1;
            }

            if ((flags & mask) > 0) {
                if (buf.position() >= data.length) {
                    System.err.println("Not enough data");
                    return null;
                }
                byte byte1 = buf.get();
                int length = byte1 >> 4;
                int disp;
                if (length == 0) {
                    if (buf.position() + 1 >= data.length) {
                        System.err.println("Not enough data");
                        return null;
                    }
                    byte byte2 = buf.get();
                    byte byte3 = buf.get();
                    length = (((byte1 & 0x0F) << 4) | ((byte2 & 0xFF) >>> 4)) + 0x11;
                    disp = (((byte2 & 0x0F) << 8) | (byte3 & 0xFF)) + 0x1;
                } else if (length == 1) {
                    if (buf.position() + 2 >= data.length) {
                        System.err.println("Not enough data");
                        return null;
                    }

                    byte byte2 = buf.get();
                    byte byte3 = buf.get();
                    byte byte4 = buf.get();

                    length = (((byte1 & 0x0F) << 12) | ((byte2 & 0xFF) << 4) | ((byte3 & 0xFF) >>> 4)) + 0x111;
                    disp = (((byte3 & 0x0F) << 8) | (byte4 & 0xFF)) + 0x1;
                } else {
                    if (buf.position() > data.length) {
                        System.err.println("Not enough data");
                        return null;
                    }
                    byte byte2 = buf.get();

                    length = ((byte1 & 0xF0) >>> 4) + 0x1;
                    disp = (((byte1 & 0x0F) << 8) | (byte2 & 0xFF)) + 0x1;
                }

                if (disp > outBuf.position()) {
                    System.err.println("oops");
                    return null;
                }
                int bufIndex = outBuf.position() - disp;
                for (int i = 0; i < length; i++) {
                    byte next = outBuf.get(bufIndex);
                    bufIndex++;
                    outBuf.put(next);
                }
            } else {
                if (buf.position() > data.length) {
                    System.err.println("Not enough data");
                    return null;
                }
                byte next = buf.get();
                outBuf.put(next);

            }
        }
        if ((buf.position() ^ (buf.position() & 0x3)) + 4 < data.length) {
            System.err.println("Too much input");
            return null;
        }
        outBuf.flip();
        return outBuf.array();
    }

    private int[] prepareData(byte[] data) {
        int fs = data.length;
        int[] fb = new int[fs + 3];
        for (int i = 0; i < fs; i++) {
            fb[i] = data[i] & 0xFF;
        }
        return fb;
    }

    private int readUnsigned(int[] buffer, int offset) {
        return buffer[offset] | (buffer[offset + 1] << 8) | (buffer[offset + 2] << 16)
                | ((buffer[offset + 3] & 0x7F) << 24);
    }

    private void writeUnsigned(int[] buffer, int offset, int value) {
        buffer[offset] = value & 0xFF;
        buffer[offset + 1] = (value >> 8) & 0xFF;
        buffer[offset + 2] = (value >> 16) & 0xFF;
        buffer[offset + 3] = (value >> 24) & 0x7F;
    }

    private int new_len;

    private void BLZ_Encode(String filename, int mode) {
        try {
            System.out.printf("- encoding '%s'", filename);
            long startTime = System.currentTimeMillis();
            byte[] buf = FileFunctions.readFileFullyIntoBuffer(filename);
            BLZResult result = BLZ_Encode(buf, mode);
            if (result != null)
                Save(filename, result.buffer, result.length);
            System.out.print(" - done, time=" + (System.currentTimeMillis() - startTime) + "ms");
            System.out.print("\n");
        } catch (IOException e) {
            EXIT("\nFile read error\n");
        }
    }

    public byte[] BLZ_EncodePub(byte[] data, boolean arm9, boolean best, String reference) {
        int mode = best ? BLZ_BEST : BLZ_NORMAL;
        this.arm9 = arm9;
        if (reference.equals("GARC")) {
            return LZSS_Encode(data);
        } else {
            System.out.printf("- encoding '%s' (memory)", reference);
            long startTime = System.currentTimeMillis();
            BLZResult result = BLZ_Encode(data, mode);
            System.out.print(" - done, time=" + (System.currentTimeMillis() - startTime) + "ms");
            System.out.print("\n");
            if (result != null) {
                byte[] retbuf = new byte[result.length];
                for (int i = 0; i < result.length; i++) {
                    retbuf[i] = (byte) result.buffer[i];
                }
                return retbuf;
            } else {
                return null;
            }
        }
    }

    private BLZResult BLZ_Encode(byte[] data, int mode) {
        int[] raw_buffer, pak_buffer, new_buffer;
        int raw_len, pak_len;

        new_len = 0;

        raw_buffer = prepareData(data);
        raw_len = raw_buffer.length - 3;

        pak_buffer = null;
        pak_len = BLZ_MAXIM + 1;

        new_buffer = BLZ_Code(raw_buffer, raw_len, mode);

        if (new_len < pak_len) {
            pak_buffer = new_buffer;
            pak_len = new_len;
        }
        return new BLZResult(pak_buffer, pak_len);
    }

    private int[] BLZ_Code(int[] raw_buffer, int raw_len, int best) {
        int[] pak_buffer, tmp;
        int pak, raw, raw_end, flg = 0;
        int pak_len, inc_len, hdr_len, enc_len, len;
        int len_best, pos_best = 0, len_next, pos_next = 0, len_post, pos_post = 0;
        int pak_tmp, raw_tmp, raw_new;
        int mask;

        pak_tmp = 0;
        raw_tmp = raw_len;

        pak_len = raw_len + ((raw_len + 7) / 8) + 11;
        pak_buffer = new int[pak_len];

        raw_new = raw_len;

        if (arm9) {
            // We don't do any of the checks here
            // Presume that we actually are using an arm9
            raw_new -= 0x4000;
        }

        BLZ_Invert(raw_buffer, 0, raw_len);

        pak = 0;
        raw = 0;
        raw_end = raw_new;

        mask = 0;
        while (raw < raw_end) {
            if ((mask = (mask >>> BLZ_SHIFT)) == 0) {
                pak_buffer[(flg = pak++)] = 0;
                mask = BLZ_MASK;
            }

            SearchPair sl1 = SEARCH(pos_best, raw_buffer, raw, raw_end);
            len_best = sl1.l;
            pos_best = sl1.p;

            // LZ-CUE optimization start
            if (best == BLZ_BEST) {
                if (len_best > BLZ_THRESHOLD) {
                    if (raw + len_best < raw_end) {
                        raw += len_best;
                        SearchPair sl2 = SEARCH(pos_next, raw_buffer, raw, raw_end);
                        len_next = sl2.l;
                        pos_next = sl2.p;
                        raw -= (len_best - 1);
                        SearchPair sl3 = SEARCH(pos_post, raw_buffer, raw, raw_end);
                        len_post = sl3.l;
                        pos_post = sl3.p;
                        raw--;

                        if (len_next <= BLZ_THRESHOLD) {
                            len_next = 1;
                        }
                        if (len_post <= BLZ_THRESHOLD) {
                            len_post = 1;
                        }
                        if ((len_best + len_next) <= (1 + len_post)) {
                            len_best = 1;
                        }
                    }
                }
            }
            // LZ-CUE optimization end
            pak_buffer[flg] = (pak_buffer[flg] << 1);
            if (len_best > BLZ_THRESHOLD) {
                raw += len_best;
                pak_buffer[flg] |= 1;
                pak_buffer[pak++] = ((len_best - (BLZ_THRESHOLD + 1)) << 4) | ((pos_best - 3) >>> 8);
                pak_buffer[pak++] = (pos_best - 3) & 0xFF;
            } else {
                pak_buffer[pak++] = raw_buffer[raw++];
            }

            if (pak + raw_len - raw < pak_tmp + raw_tmp) {
                pak_tmp = pak;
                raw_tmp = raw_len - raw;
            }
        }

        while ((mask > 0) && (mask != 1)) {
            mask = (mask >>> BLZ_SHIFT);
            pak_buffer[flg] = pak_buffer[flg] << 1;
        }

        pak_len = pak;

        BLZ_Invert(raw_buffer, 0, raw_len);
        BLZ_Invert(pak_buffer, 0, pak_len);

        if (pak_tmp == 0 || (raw_len + 4 < ((pak_tmp + raw_tmp + 3) & 0xFFFFFFFC) + 8)) {
            pak = 0;
            raw = 0;
            raw_end = raw_len;

            while (raw < raw_end) {
                pak_buffer[pak] = raw_buffer[raw];
            }

            while ((pak & 3) > 0) {
                pak_buffer[pak++] = 0;
            }

            pak_buffer[pak++] = 0;
            pak_buffer[pak++] = 0;
            pak_buffer[pak++] = 0;
            pak_buffer[pak++] = 0;
        } else {
            tmp = new int[raw_tmp + pak_tmp + 11];
            for (len = 0; len < raw_tmp; len++) {
                tmp[len] = raw_buffer[len];
            }
            for (len = 0; len < pak_tmp; len++) {
                tmp[raw_tmp + len] = pak_buffer[len + pak_len - pak_tmp];
            }

            pak_buffer = tmp;

            pak = raw_tmp + pak_tmp;

            enc_len = pak_tmp;
            hdr_len = 8;
            inc_len = raw_len - pak_tmp - raw_tmp;

            while ((pak & 3) > 0) {
                pak_buffer[pak++] = 0xFF;
                hdr_len++;
            }

            writeUnsigned(pak_buffer, pak, enc_len + hdr_len);
            pak += 3;
            pak_buffer[pak++] = hdr_len;
            writeUnsigned(pak_buffer, pak, inc_len - hdr_len);
            pak += 4;

        }
        new_len = pak;
        return pak_buffer;
    }

    // LZSS Encoding ported to Java (based on pk3DS by Kaphotics)

    private byte[] LZSS_Encode(byte[] data) {
        if (data.length > 0xFFFFFF) {
            System.err.println("Encoding: Too much data");
            return null;
        }

        ByteBuffer inBuf = ByteBuffer.wrap(data);
        ByteBuffer outBuf = ByteBuffer.allocate(data.length + 8);
        outBuf.order(ByteOrder.LITTLE_ENDIAN);
        outBuf.put((byte)0x11);
        outBuf.putInt(data.length);
        outBuf.position(outBuf.position()-1); // Go back one byte since length should only be 3 bytes
        if (data.length == 0) {
            outBuf.putInt(0);
            return Arrays.copyOfRange(outBuf.array(),0,outBuf.position());
        }

        ByteBuffer blockBuf = ByteBuffer.allocate((8 * 4) + 1);
        blockBuf.put((byte)0);
        int bufferedBlocks = 0;
        while (inBuf.position() < data.length) {
            if (bufferedBlocks == 8) {
                outBuf.put(Arrays.copyOfRange(blockBuf.array(),0,blockBuf.position()));
                blockBuf.rewind();
                blockBuf.put((byte)0);
                bufferedBlocks = 0;
            }

            int oldLength = Math.min(inBuf.position(),0x1000);
            LengthDispPair pair =
                    getOccurrenceLength(
                            inBuf,
                            inBuf.position(),
                            Math.min(data.length - inBuf.position(), 0x10110),
                            inBuf.position() - oldLength,
                            oldLength);
            int length = pair.length;
            int disp = pair.disp;

            if (length < 3) {
                blockBuf.put(inBuf.get());
            } else {
                inBuf.position(inBuf.position() + length);

                byte compFlag = (byte)(blockBuf.get(0) | (1 << (7 - bufferedBlocks)));
                blockBuf.put(0,compFlag);

                if (length > 0x110) {
                    blockBuf.put((byte)(0x10 | (((length - 0x111) >>> 12) & 0x0F))); // Dangerous
                    blockBuf.put((byte)(((length - 0x111) >>> 4) & 0xFF));
                    blockBuf.put((byte)(((length - 0x111) << 4) & 0xF0));
                    blockBuf.position(blockBuf.position()-1);
                } else if (length > 0x10) {
                    blockBuf.put((byte)(((length - 0x111) >>> 4) & 0x0F));
                    blockBuf.put((byte)(((length - 0x111) << 4) & 0xF0));
                    blockBuf.position(blockBuf.position()-1);
                } else {
                    blockBuf.put((byte)(((length - 1) << 4) & 0xF0));
                    blockBuf.position(blockBuf.position()-1);
                }
                byte dispPart1 = (byte)(blockBuf.get(blockBuf.position()) | (byte)(((disp - 1) >>> 8) & 0x0F));
                blockBuf.put(dispPart1);
                blockBuf.put((byte)((disp - 1) & 0xFF));
            }
            bufferedBlocks++;
        }
        if (bufferedBlocks > 0) {
            outBuf.put(Arrays.copyOfRange(blockBuf.array(),0,blockBuf.position()));
        }
        return Arrays.copyOfRange(outBuf.array(),0,outBuf.position());
    }

    private LengthDispPair getOccurrenceLength(ByteBuffer buf, int newIndex, int newLength, int oldIndex, int oldLength) {
        int disp = 0;
        if (newLength == 0) {
            return new LengthDispPair(0,0);
        }
        int maxLength = 0;

        for (int i = 0; i < oldLength - 1; i++) {
            int currentOldStart = oldIndex + i;
            int currentLength = 0;

            for (int j = 0; j < newLength; j++) {
                if (buf.get(currentOldStart + j) != buf.get(newIndex + j)) break;
                currentLength++;
            }

            if (currentLength > maxLength) {
                maxLength = currentLength;
                disp = oldLength - i;

                if (maxLength == newLength) {
                    break;
                }
            }
        }
        return new LengthDispPair(maxLength,disp);
    }

    private class LengthDispPair {
        int length;
        int disp;

        LengthDispPair(int length, int disp) {
            this.length = length;
            this.disp = disp;
        }
    }

    private static class SearchPair {
        public int l;
        public int p;

        public SearchPair(int l, int p) {
            this.l = l;
            this.p = p;
        }
    }

    private SearchPair SEARCH(int p, int[] raw_buffer, int raw, int raw_end) {
        int l = BLZ_THRESHOLD;
        int max = (raw >= BLZ_N) ? BLZ_N : raw;
        for (int pos = 3; pos <= max; pos++) {
            int len;
            for (len = 0; len < BLZ_F; len++) {
                if (raw + len == raw_end) {
                    break;
                }
                if (len >= pos) {
                    break;
                }
                if (raw_buffer[raw + len] != raw_buffer[raw + len - pos]) {
                    break;
                }
            }

            if (len > l) {
                p = pos;
                if ((l = len) == BLZ_F) {
                    break;
                }
            }
        }
        return new SearchPair(l, p);
    }

    private class BLZResult {
        public BLZResult(int[] raw_buffer, int raw_len) {
            this.buffer = raw_buffer;
            this.length = raw_len;
        }

        int[] buffer;
        int length;
    }

    private void BLZ_Invert(int[] buffer, int offset, int length) {
        int bottom, ch;

        bottom = offset + length - 1;

        while (offset < bottom) {
            ch = buffer[offset];
            buffer[offset++] = buffer[bottom];
            buffer[bottom--] = ch;
        }
    }

}
