package thenewpoketext;

/*----------------------------------------------------------------------------*/
/*--  PokeTextData.java - decodes gen4 games text into Unicode              --*/
/*--  Code derived from "thenewpoketext", copyright (C) loadingNOW          --*/
/*--  Ported to Java and bugfixed/customized by Dabomstew                   --*/
/*----------------------------------------------------------------------------*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PokeTextData {
    private byte[] data;
    private List<PointerEntry> ptrlist;
    public List<String> strlist;
    public boolean compressFlag;

    public PokeTextData(byte[] data) {
        this.data = Arrays.copyOf(data, data.length);
    }

    public byte[] get() {
        return data;
    }

    private int read16(int ofs) {
        return (data[ofs] & 0xFF) | ((data[ofs + 1] & 0xFF) << 8);
    }

    private void write16(int d, int ofs) {
        data[ofs] = (byte) (d & 0xFF);
        data[ofs + 1] = (byte) ((d >> 8) & 0xFF);
    }

    private int read32(int ofs) {
        return (data[ofs] & 0xFF) | ((data[ofs + 1] & 0xFF) << 8) | ((data[ofs + 2] & 0xFF) << 16)
                | ((data[ofs + 3] & 0xFF) << 24);
    }

    private void write32(int d, int ofs) {
        data[ofs] = (byte) (d & 0xFF);
        data[ofs + 1] = (byte) ((d >> 8) & 0xFF);
        data[ofs + 2] = (byte) ((d >> 16) & 0xFF);
        data[ofs + 3] = (byte) ((d >> 24) & 0xFF);
    }

    public void decrypt() {
        DecyptPtrs(read16(0), read16(2), 4);
        this.ptrlist = CreatePtrList(read16(0), 4);

        this.strlist = new ArrayList<>();

        int num = read16(0);

        for (int i = 0; i < num; i++) {
            PointerEntry entry = this.ptrlist.get(i);
            DecyptTxt(entry.getChars(), i + 1, entry.getPtr());
            this.strlist.add(MakeString(entry.getChars(), entry.getPtr()));
        }
    }

    public void encrypt() {
        this.ptrlist = CreatePtrList(read16(0), 4);
        int num = read16(0);
        for (int i = 0; i < num; i++) {
            PointerEntry entry = this.ptrlist.get(i);
            DecyptTxt(entry.getChars(), i + 1, entry.getPtr());
        }

        DecyptPtrs(read16(0), read16(2), 4);
    }

    private void DecyptPtrs(int count, int key, int sdidx) {
        key = (key * 0x2FD) & 0xFFFF;

        for (int i = 0; i < count; i++) {
            int key2 = (key * (i + 1) & 0xFFFF);
            int realkey = key2 | (key2 << 16);
            write32(read32(sdidx) ^ realkey, sdidx);
            write32(read32(sdidx + 4) ^ realkey, sdidx + 4);
            sdidx += 8;
        }

    }

    private List<PointerEntry> CreatePtrList(int count, int sdidx) {
        List<PointerEntry> ptrlist = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ptrlist.add(new PointerEntry(read32(sdidx), read32(sdidx + 4)));
            sdidx += 8;
        }
        return ptrlist;
    }

    private void DecyptTxt(int count, int id, int idx) {
        int key = (0x91BD3 * id) & 0xFFFF;
        for (int i = 0; i < count; i++) {
            write16(read16(idx) ^ key, idx);
            key += 0x493D;
            key = key & 0xFFFF;
            idx += 2;
        }

    }

    private String MakeString(int count, int idx) {
        StringBuilder string = new StringBuilder();
        List<Integer> chars = new ArrayList<>();
        List<Integer> uncomp = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            chars.add(read16(idx));
            idx += 2;
        }

        if (chars.get(0) == 0xF100) {
            compressFlag = true;
            int j = 1;
            int shift1 = 0;
            int trans = 0;
            while (true) {
                int tmp = chars.get(j);
                tmp = tmp >> shift1;
                int tmp1 = tmp;
                if (shift1 >= 0xF) {
                    shift1 -= 0xF;
                    if (shift1 > 0) {
                        tmp1 = (trans | ((chars.get(j) << (9 - shift1)) & 0x1FF));
                        if (tmp1 == 0x1FF) {
                            break;
                        }
                        uncomp.add(tmp1);
                    }
                } else {
                    tmp1 = ((chars.get(j) >> shift1) & 0x1FF);
                    if (tmp1 == 0x1FF) {
                        break;
                    }
                    uncomp.add(tmp1);
                    shift1 += 9;
                    if (shift1 < 0xF) {
                        trans = ((chars.get(j) >> shift1) & 0x1FF);
                        shift1 += 9;
                    }
                    j += 1;
                }
            }
            chars = uncomp;
        }
        int i = 0;
        for (int c = 0; c < chars.size(); c++) {
            int currChar = chars.get(i);
            if (UnicodeParser.tb[currChar] != null) {
                string.append(UnicodeParser.tb[currChar]);
            } else {
                if (currChar == 0xFFFE) {
                    i++;
                    string.append("\\v").append(String.format("%04X", chars.get(i)));
                    i++;
                    int total = chars.get(i);
                    if (total == 0) {
                        string.append("\\x").append(String.format("%04X", 0));
                    }
                    for (int z = 0; z < total; z++) {
                        i++;
                        string.append("\\z").append(String.format("%04X", chars.get(i)));
                    }
                } else if (currChar == 0xFFFF) {
                    break;
                } else {
                    string.append("\\x").append(String.format("%04X", chars.get(i)));
                }
            }
            i++;
        }
        return string.toString();
    }

    public void SetKey(int key) {
        write16(key, 2);
    }

    public int GetKey() {
        return read16(2);
    }

    public class PointerEntry {

        private int ptr;
        private int chars;

        public PointerEntry(int ptr, int chars) {
            this.ptr = ptr;
            this.chars = chars;
        }

        public int getPtr() {
            return ptr;
        }

        public int getChars() {
            return chars;
        }
    }

}
