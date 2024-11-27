package thenewpoketext;

/*----------------------------------------------------------------------------*/
/*--  TextToPoke.java - encodes gen4 games text from Unicode                --*/
/*--  Code derived from "thenewpoketext", copyright (C) loadingNOW          --*/
/*--  Ported to Java and bugfixed/customized by Dabomstew                   --*/
/*----------------------------------------------------------------------------*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextToPoke {

    public static byte[] MakeFile(List<String> textarr, boolean compressed) {
        int base = textarr.size() * 8 + 4;
        List<PointerEntry> ptrtable = new ArrayList<>();
        List<List<Integer>> rawdata = new ArrayList<>();
        for (String aTextarr : textarr) {
            List<Integer> data = ToCode(aTextarr, compressed);
            int l = data.size();
            ptrtable.add(new PointerEntry(base, l));
            rawdata.add(data);
            base += l * 2;
        }

        List<Integer> hdr = Arrays.asList(textarr.size(), 0);

        return join(wordListToBarr(hdr), pointerListToBarr(ptrtable), listOfWordListToBarr(rawdata));
    }

    private static List<Integer> ToCode(String text, boolean compressed) {
        List<Integer> data = new ArrayList<>();
        while (text.length() != 0) {
            int i = Math.max(0, 6 - text.length());
            if (text.charAt(0) == '\\') {
                if (text.charAt(1) == 'x') {
                    data.add(Integer.parseInt(text.substring(2, 6), 16));
                    text = text.substring(6);
                } else if (text.charAt(1) == 'v') {
                    data.add(0xFFFE);
                    data.add(Integer.parseInt(text.substring(2, 6), 16));
                    text = text.substring(6);
                } else if (text.charAt(1) == 'z') {
                    List<Integer> var = new ArrayList<>();
                    int w = 0;
                    while (text.length() != 0) {
                        if (text.charAt(0) == '\\' && text.charAt(1) == 'z') {
                            w++;
                            var.add(Integer.parseInt(text.substring(2, 6), 16));
                            text = text.substring(6);
                        } else {
                            break;
                        }
                    }
                    data.add(w);
                    data.addAll(var);
                } else if (text.charAt(1) == 'n') {
                    data.add(0xE000);
                    text = text.substring(2);
                } else if (text.charAt(1) == 'p') {
                    data.add(0x25BC);
                    text = text.substring(2);
                } else if (text.charAt(1) == 'l') {
                    data.add(0x25BD);
                    text = text.substring(2);
                } else if (text.substring(1, 4).equals("and")) {
                    data.add(0x1C2);
                    text = text.substring(4);
                } else {
                    System.out.printf("unknown escape: %s\n", text.substring(1, 2));
                    text = text.substring(2);
                }
            } else {
                while (!(UnicodeParser.d.containsKey(text.substring(0, 6 - i)) || (i == 6))) {
                    i++;
                }
                if (i == 6) {
                    System.out.printf("Char not found %s(%x)", text.substring(0, 1), (byte)text.charAt(0));
                    text = text.substring(1);
                } else {
                    data.add(UnicodeParser.d.get(text.substring(0, 6 - i)));
                    text = text.substring(6 - i);
                }
            }
        }
        if (compressed) {
            if (data.size() % 5 != 0 || data.size() == 0) {
                data.add(0x1FF);
            }
            byte[] bits = new byte[data.size() * 9];
            int bc = 0;
            for (Integer aData : data) {
                for (int j = 0; j < 9; j++) {
                    bits[bc++] = (byte) ((aData >> j) & 1);
                }
            }
            int tmp_uint16 = 0;
            data.clear();
            data.add(0xF100);
            for (int i = 0; i < bits.length; i++) {
                if (i % 15 == 0 && i != 0) {
                    data.add(tmp_uint16);
                    tmp_uint16 = 0;
                }
                tmp_uint16 |= (bits[i] << (i % 15));
            }
            data.add(tmp_uint16);
        }
        data.add(0xFFFF);
        return data;
    }

    private static byte[] join(byte[]... args) {
        int tlen = 0;
        for (byte[] arr : args) {
            tlen += arr.length;
        }
        byte[] barr = new byte[tlen];
        int offs = 0;
        for (byte[] arr : args) {
            System.arraycopy(arr, 0, barr, offs, arr.length);
            offs += arr.length;
        }
        return barr;
    }

    private static byte[] wordListToBarr(List<Integer> list) {
        byte[] barr = new byte[list.size() * 2];
        int l = list.size();
        for (int i = 0; i < l; i++) {
            barr[i * 2] = (byte) (list.get(i) & 0xFF);
            barr[i * 2 + 1] = (byte) ((list.get(i) >> 8) & 0xFF);
        }
        return barr;
    }

    private static byte[] pointerListToBarr(List<PointerEntry> ptrList) {
        byte[] data = new byte[ptrList.size() * 8];
        int l = ptrList.size();
        for (int i = 0; i < l; i++) {
            int ofs = i * 8;
            PointerEntry ent = ptrList.get(i);
            data[ofs] = (byte) (ent.ptr & 0xFF);
            data[ofs + 1] = (byte) ((ent.ptr >> 8) & 0xFF);
            data[ofs + 2] = (byte) ((ent.ptr >> 16) & 0xFF);
            data[ofs + 3] = (byte) ((ent.ptr >> 24) & 0xFF);
            data[ofs + 4] = (byte) (ent.chars & 0xFF);
            data[ofs + 5] = (byte) ((ent.chars >> 8) & 0xFF);
            data[ofs + 6] = (byte) ((ent.chars >> 16) & 0xFF);
            data[ofs + 7] = (byte) ((ent.chars >> 24) & 0xFF);
        }
        return data;
    }

    private static byte[] listOfWordListToBarr(List<List<Integer>> list) {
        int tlen = 0;
        for (List<Integer> subList : list) {
            tlen += subList.size() * 2;
        }
        byte[] barr = new byte[tlen];
        int offs = 0;
        for (List<Integer> slist : list) {
            for (Integer aSlist : slist) {
                barr[offs] = (byte) (aSlist & 0xFF);
                barr[offs + 1] = (byte) ((aSlist >> 8) & 0xFF);
                offs += 2;
            }
        }
        return barr;
    }

    private static class PointerEntry {

        private int ptr;
        private int chars;

        public PointerEntry(int ptr, int chars) {
            this.ptr = ptr;
            this.chars = chars;
        }
    }

}
