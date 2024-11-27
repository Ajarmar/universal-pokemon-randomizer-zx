package pptxt;

/*----------------------------------------------------------------------------*/
/*--  PPTxtHandler.java - handles generation 5 games text encoding          --*/
/*--  Code derived from "PPTXT", copyright (C) SCV?                         --*/
/*--  Ported to Java and bugfixed/customized by Dabomstew                   --*/
/*----------------------------------------------------------------------------*/

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dabomstew.pkrandom.FileFunctions;

public class PPTxtHandler {

    private static Map<String, String> pokeToText = new HashMap<>();
    private static Map<String, String> textToPoke = new HashMap<>();

    private static Pattern pokeToTextPattern, textToPokePattern;

    static {
        try {
            Scanner sc = new Scanner(FileFunctions.openConfig("Generation5.tbl"), "UTF-8");
            while (sc.hasNextLine()) {
                String q = sc.nextLine();
                if (!q.trim().isEmpty()) {
                    String[] r = q.split("=", 2);
                    if (r[1].endsWith("\r\n")) {
                        r[1] = r[1].substring(0, r[1].length() - 2);
                    }
                    pokeToText.put(Character.toString((char) Integer.parseInt(r[0], 16)), r[1].replace("\\", "\\\\")
                            .replace("$", "\\$"));
                    textToPoke.put(r[1], "\\\\x" + r[0]);
                }
            }
            sc.close();
            pokeToTextPattern = makePattern(pokeToText.keySet());
            textToPokePattern = makePattern(textToPoke.keySet());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Pattern makePattern(Iterable<String> tokens) {
        String patternStr = "("
                + implode(tokens, "|").replace("\\", "\\\\").replace("[", "\\[").replace("]", "\\]")
                        .replace("(", "\\(").replace(")", "\\)") + ")";
        return Pattern.compile(patternStr);
    }

    private static String implode(Iterable<String> tokens, String sep) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String token : tokens) {
            if (!first) {
                sb.append(sep);
            }
            sb.append(token);
            first = false;
        }
        return sb.toString();
    }

    /**
     * Decompress the words given into chars according to 9bits per char format
     * Based off poketext's implementation of the same in gen4, but uses all 16
     * bits per word as opposed to 15
     * 
     * @param chars
     *            List of words, beginning with [F100] which is skipped.
     * @return Decompressed list of integers corresponding to characters
     */
    private static List<Integer> decompress(List<Integer> chars) {
        List<Integer> uncomp = new ArrayList<>();
        int j = 1;
        int shift1 = 0;
        int trans = 0;
        while (true) {
            int tmp = chars.get(j);
            tmp = tmp >> shift1;
            int tmp1 = tmp;
            if (shift1 >= 0x10) {
                shift1 -= 0x10;
                if (shift1 > 0) {
                    tmp1 = (trans | ((chars.get(j) << (9 - shift1)) & 0x1FF));
                    if ((tmp1 & 0xFF) == 0xFF) {
                        break;
                    }
                    if (tmp1 != 0x0 && tmp1 != 0x1) {
                        uncomp.add(tmp1);
                    }
                }
            } else {
                tmp1 = ((chars.get(j) >> shift1) & 0x1FF);
                if ((tmp1 & 0xFF) == 0xFF) {
                    break;
                }
                if (tmp1 != 0x0 && tmp1 != 0x1) {
                    uncomp.add(tmp1);
                }
                shift1 += 9;
                if (shift1 < 0x10) {
                    trans = ((chars.get(j) >> shift1) & 0x1FF);
                    shift1 += 9;
                }
                j += 1;
            }
        }
        return uncomp;
    }

    private static List<Integer> lastKeys;
    private static List<Integer> lastUnknowns;

    /**
     * Take a byte-array corresponding to a NARC entry and build a list of
     * strings against the gen5 text encryption. Decompresses as appropriate.
     * 
     * @param ds
     *            The data from this msg.narc entry
     * @return The list of strings
     */

    public static List<String> readTexts(byte[] ds) {
        int pos = 0;
        int i = 0;
        lastKeys = new ArrayList<>();
        lastUnknowns = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        int numSections, numEntries, tmpCharCount, tmpUnknown, tmpChar;
        int tmpOffset;
        int[] sizeSections = new int[] { 0, 0, 0 };
        int[] sectionOffset = new int[] { 0, 0, 0 };
        Map<Integer, List<Integer>> tableOffsets = new HashMap<>();
        Map<Integer, List<Integer>> characterCount = new HashMap<>();
        Map<Integer, List<Integer>> unknown = new HashMap<>();
        Map<Integer, List<List<Integer>>> encText = new HashMap<>();
        Map<Integer, List<List<String>>> decText = new HashMap<>();
        StringBuilder sb;
        int key;

        numSections = readWord(ds, 0);
        numEntries = readWord(ds, 2);
        sizeSections[0] = readLong(ds, 4);
        // unk1 = readLong(ds, 8);
        pos += 12;
        if (numSections > i) {
            for (int z = 0; z < numSections; z++) {
                sectionOffset[z] = readLong(ds, pos);
                pos += 4;
            }
            pos = sectionOffset[i];
            sizeSections[i] = readLong(ds, pos);
            pos += 4;
            tableOffsets.put(i, new ArrayList<>());
            characterCount.put(i, new ArrayList<>());
            unknown.put(i, new ArrayList<>());
            encText.put(i, new ArrayList<>());
            decText.put(i, new ArrayList<>());
            for (int j = 0; j < numEntries; j++) {
                tmpOffset = readLong(ds, pos);
                pos += 4;
                tmpCharCount = readWord(ds, pos);
                pos += 2;
                tmpUnknown = readWord(ds, pos);
                pos += 2;
                tableOffsets.get(i).add(tmpOffset);
                characterCount.get(i).add(tmpCharCount);
                unknown.get(i).add(tmpUnknown);
                lastUnknowns.add(tmpUnknown);
            }
            for (int j = 0; j < numEntries; j++) {
                List<Integer> tmpEncChars = new ArrayList<>();
                pos = sectionOffset[i] + tableOffsets.get(i).get(j);
                for (int k = 0; k < characterCount.get(i).get(j); k++) {
                    tmpChar = readWord(ds, pos);
                    pos += 2;
                    tmpEncChars.add(tmpChar);
                }
                encText.get(i).add(tmpEncChars);
                key = encText.get(i).get(j).get(characterCount.get(i).get(j) - 1) ^ 0xFFFF;
                for (int k = characterCount.get(i).get(j) - 1; k >= 0; k--) {
                    encText.get(i).get(j).set(k, (encText.get(i).get(j).get(k)) ^ key);
                    if (k == 0) {
                        lastKeys.add(key);
                    }
                    key = ((key >>> 3) | (key << 13)) & 0xffff;
                }
                if (encText.get(i).get(j).get(0) == 0xF100) {
                    encText.get(i).set(j, decompress(encText.get(i).get(j)));
                    characterCount.get(i).set(j, encText.get(i).get(j).size());
                }
                List<String> chars = new ArrayList<>();
                sb = new StringBuilder();
                for (int k = 0; k < characterCount.get(i).get(j); k++) {
                    if (encText.get(i).get(j).get(k) == 0xFFFF) {
                        chars.add("\\xFFFF");
                    } else {
                        if (encText.get(i).get(j).get(k) > 20 && encText.get(i).get(j).get(k) <= 0xFFF0
                                && Character.UnicodeBlock.of(encText.get(i).get(j).get(k)) != null) {
                            chars.add("" + ((char) encText.get(i).get(j).get(k).intValue()));
                        } else {
                            String num = String.format("%04X", encText.get(i).get(j).get(k));
                            chars.add("\\x" + num);
                        }
                        sb.append(chars.get(k));
                    }
                }
                strings.add(sb.toString());
                decText.get(i).add(chars);
            }
        }

        // Parse strings against the table
        for (int sn = 0; sn < strings.size(); sn++) {
            strings.set(sn, bulkReplace(strings.get(sn), pokeToTextPattern, pokeToText));
        }
        return strings;
    }

    private static String bulkReplace(String string, Pattern pattern, Map<String, String> replacements) {
        Matcher matcher = pattern.matcher(string);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, replacements.get(matcher.group(1)));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * Write newStrings to the text datafile originalData, as language 0 (the
     * only one in most releases BUT japanese). Return the resulting binary as a
     * byte-array. Will never use the [F100] compression, even if the original
     * file used it.
     * 
     * @param originalData
     *            The original file, to copy stuff like unknowns.
     * @param text
     *            The new data.
     * @return The file to write back to the NARC.
     */
    public static byte[] saveEntry(byte[] originalData, List<String> text) {

        // Parse strings against the reverse table
        for (int sn = 0; sn < text.size(); sn++) {
            text.set(sn, bulkReplace(text.get(sn), textToPokePattern, textToPoke));
        }

        // Make sure we have the original unknowns etc
        readTexts(originalData);

        // Start getting stuff
        int numSections, numEntries;
        int[] sizeSections = new int[] { 0, 0, 0 };
        int[] sectionOffset = new int[] { 0, 0, 0 };
        int[] newsizeSections = new int[] { 0, 0, 0 };
        int[] newsectionOffset = new int[] { 0, 0, 0 };

        // Data-Stream
        int pos = 0;

        numSections = readWord(originalData, 0);
        numEntries = readWord(originalData, 2);
        sizeSections[0] = readLong(originalData, 4);
        // unk1 readLong(ds, 8);
        pos += 12;

        if (text.size() < numEntries) {
            System.err.println("Can't do anything due to too few lines");
            return originalData;
        } else {
            byte[] newEntry = makeSection(text, numEntries);
            for (int z = 0; z < numSections; z++) {
                sectionOffset[z] = readLong(originalData, pos);
                pos += 4;
            }
            for (int z = 0; z < numSections; z++) {
                pos = sectionOffset[z];
                sizeSections[z] = readLong(originalData, pos);
            }
            newsizeSections[0] = newEntry.length;

            byte[] newData = new byte[originalData.length - sizeSections[0] + newsizeSections[0]];
            System.arraycopy(originalData, 0, newData, 0, Math.min(originalData.length, newData.length));
            writeLong(newData, 4, newsizeSections[0]);
            if (numSections == 2) {
                newsectionOffset[1] = newsizeSections[0] + sectionOffset[0];
                writeLong(newData, 0x10, newsectionOffset[1]);
            }
            System.arraycopy(newEntry, 0, newData, sectionOffset[0], newEntry.length);
            if (numSections == 2) {
                System.arraycopy(originalData, sectionOffset[1], newData, newsectionOffset[1], sizeSections[1]);
            }
            return newData;
        }
    }

    private static byte[] makeSection(List<String> strings, int numEntries) {
        List<List<Integer>> data = new ArrayList<>();
        int size = 0;
        int offset = 4 + 8 * numEntries;
        int charCount;
        for (int i = 0; i < numEntries; i++) {
            data.add(parseString(strings.get(i), i));
            size += (data.get(i).size() * 2);
        }
        if (size % 4 == 2) {
            size += 2;
            int tmpKey = lastKeys.get(numEntries - 1);
            for (int i = 0; i < data.get(numEntries - 1).size(); i++) {
                tmpKey = ((tmpKey << 3) | (tmpKey >> 13)) & 0xFFFF;
            }
            data.get(numEntries - 1).add(0xFFFF ^ tmpKey);
        }
        size += offset;
        byte[] section = new byte[size];
        int pos = 0;
        writeLong(section, pos, size);
        pos += 4;
        for (int i = 0; i < numEntries; i++) {
            charCount = data.get(i).size();
            writeLong(section, pos, offset);
            pos += 4;
            writeWord(section, pos, charCount);
            pos += 2;
            writeWord(section, pos, lastUnknowns.get(i));
            pos += 2;
            offset += (charCount * 2);
        }
        for (int i = 0; i < numEntries; i++) {
            for (int word : data.get(i)) {
                writeWord(section, pos, word);
                pos += 2;
            }
        }
        return section;
    }

    private static List<Integer> parseString(String string, int entry_id) {
        List<Integer> chars = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != '\\') {
                chars.add((int) string.charAt(i));
            } else {
                if (((i + 2) < string.length()) && string.charAt(i + 2) == '{') {
                    chars.add((int) string.charAt(i));
                } else {
                    chars.add(Integer.parseInt(string.substring(i + 2, i + 6), 16));
                    i += 5;
                }
            }
        }
        chars.add(0xFFFF);
        int key = lastKeys.get(entry_id);
        for (int i = 0; i < chars.size(); i++) {
            chars.set(i, (chars.get(i) ^ key) & 0xFFFF);
            key = ((key << 3) | (key >>> 13)) & 0xFFFF;
        }
        return chars;
    }

    private static int readWord(byte[] data, int offset) {
        return (data[offset] & 0xFF) + ((data[offset + 1] & 0xFF) << 8);
    }

    private static int readLong(byte[] data, int offset) {
        return (data[offset] & 0xFF) + ((data[offset + 1] & 0xFF) << 8) + ((data[offset + 2] & 0xFF) << 16)
                + ((data[offset + 3] & 0xFF) << 24);
    }

    protected static void writeWord(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

    private static void writeLong(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
        data[offset + 2] = (byte) ((value >> 16) & 0xFF);
        data[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }
}
