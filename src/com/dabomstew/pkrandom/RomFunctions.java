package com.dabomstew.pkrandom;

/*----------------------------------------------------------------------------*/
/*--  RomFunctions.java - contains functions useful throughout the program. --*/
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.MoveLearnt;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

public class RomFunctions {

    public static Set<Pokemon> getBasicPokemon(RomHandler baseRom) {
        List<Pokemon> allPokes = baseRom.getPokemonInclFormes();
        Set<Pokemon> basicPokes = new TreeSet<>();
        for (Pokemon pkmn : allPokes) {
            if (pkmn != null) {
                if (pkmn.evolutionsTo.size() < 1) {
                    basicPokes.add(pkmn);
                }
            }
        }
        return basicPokes;
    }

    public static Set<Pokemon> getSplitEvolutions(RomHandler baseRom) {
        List<Pokemon> allPokes = baseRom.getPokemonInclFormes();
        Set<Pokemon> splitEvos = new TreeSet<>();
        for (Pokemon pkmn : allPokes) {
            if (pkmn != null) {
                if (pkmn.evolutionsTo.size() > 0) {
                    Evolution onlyEvo = pkmn.evolutionsTo.get(0);
                    if (!onlyEvo.carryStats) {
                        splitEvos.add(pkmn);
                    }
                }
            }
        }
        return splitEvos;
    }

    public static Set<Pokemon> getMiddleEvolutions(RomHandler baseRom, boolean includeSplitEvos) {
        List<Pokemon> allPokes = baseRom.getPokemon();
        Set<Pokemon> middleEvolutions = new TreeSet<>();
        for (Pokemon pkmn : allPokes) {
            if (pkmn != null) {
                if (pkmn.evolutionsTo.size() == 1 && pkmn.evolutionsFrom.size() > 0) {
                    Evolution onlyEvo = pkmn.evolutionsTo.get(0);
                    if (onlyEvo.carryStats || includeSplitEvos) {
                        middleEvolutions.add(pkmn);
                    }
                }
            }
        }
        return middleEvolutions;
    }

    public static Set<Pokemon> getFinalEvolutions(RomHandler baseRom, boolean includeSplitEvos) {
        List<Pokemon> allPokes = baseRom.getPokemon();
        Set<Pokemon> finalEvolutions = new TreeSet<>();
        for (Pokemon pkmn : allPokes) {
            if (pkmn != null) {
                if (pkmn.evolutionsTo.size() == 1 && pkmn.evolutionsFrom.size() == 0) {
                    Evolution onlyEvo = pkmn.evolutionsTo.get(0);
                    if (onlyEvo.carryStats || includeSplitEvos) {
                        finalEvolutions.add(pkmn);
                    }
                }
            }
        }
        return finalEvolutions;
    }

    /**
     * Get the 4 moves known by a Pokemon at a particular level.
     * 
     * @param pkmn Pokemon index to get moves for.
     * @param movesets Map of Pokemon indices mapped to movesets.
     * @param level Level to get at.
     * @return Array with move indices.
     */
    public static int[] getMovesAtLevel(int pkmn, Map<Integer, List<MoveLearnt>> movesets, int level) {
        return getMovesAtLevel(pkmn, movesets, level, 0);
    }

    public static int[] getMovesAtLevel(int pkmn, Map<Integer, List<MoveLearnt>> movesets, int level, int emptyValue) {
        int[] curMoves = new int[4];

        if (emptyValue != 0) {
            Arrays.fill(curMoves, emptyValue);
        }

        int moveCount = 0;
        List<MoveLearnt> movepool = movesets.get(pkmn);
        for (MoveLearnt ml : movepool) {
            if (ml.level > level) {
                // we're done
                break;
            }

            boolean alreadyKnownMove = false;
            for (int i = 0; i < moveCount; i++) {
                if (curMoves[i] == ml.move) {
                    alreadyKnownMove = true;
                    break;
                }
            }

            if (!alreadyKnownMove) {
                // add this move to the moveset
                if (moveCount == 4) {
                    // shift moves up and add to last slot
                    System.arraycopy(curMoves, 1, curMoves, 0, 3);
                    curMoves[3] = ml.move;
                } else {
                    // add to next available slot
                    curMoves[moveCount++] = ml.move;
                }
            }
        }

        return curMoves;
    }

    public static String camelCase(String original) {
        char[] string = original.toLowerCase().toCharArray();
        boolean docap = true;
        for (int j = 0; j < string.length; j++) {
            char current = string[j];
            if (docap && Character.isLetter(current)) {
                string[j] = Character.toUpperCase(current);
                docap = false;
            } else {
                if (!docap && !Character.isLetter(current) && current != '\'' && current != '’') {
                    docap = true;
                }
            }
        }
        return new String(string);
    }

    public static int freeSpaceFinder(byte[] rom, byte freeSpace, int amount, int offset) {
        // by default align to 4 bytes to make sure things don't break
        return freeSpaceFinder(rom, freeSpace, amount, offset, true);
    }

    public static int freeSpaceFinder(byte[] rom, byte freeSpace, int amount, int offset, boolean longAligned) {
        if (!longAligned) {
            // Find 2 more than necessary and return 2 into it,
            // to preserve stuff like FF terminators for strings
            // 161: and FFFF terminators for movesets
            byte[] searchNeedle = new byte[amount + 2];
            for (int i = 0; i < amount + 2; i++) {
                searchNeedle[i] = freeSpace;
            }
            return searchForFirst(rom, offset, searchNeedle) + 2;
        } else {
            // Find 5 more than necessary and return into it as necessary for
            // 4-alignment,
            // to preserve stuff like FF terminators for strings
            // 161: and FFFF terminators for movesets
            byte[] searchNeedle = new byte[amount + 5];
            for (int i = 0; i < amount + 5; i++) {
                searchNeedle[i] = freeSpace;
            }
            return (searchForFirst(rom, offset, searchNeedle) + 5) & ~3;
        }
    }

    public static List<Integer> search(byte[] haystack, byte[] needle) {
        return search(haystack, 0, haystack.length, needle);
    }

    public static List<Integer> search(byte[] haystack, int beginOffset, byte[] needle) {
        return search(haystack, beginOffset, haystack.length, needle);
    }

    public static List<Integer> search(byte[] haystack, int beginOffset, int endOffset, byte[] needle) {
        int currentMatchStart = beginOffset;
        int currentCharacterPosition = 0;

        int needleSize = needle.length;

        int[] toFillTable = buildKMPSearchTable(needle);
        List<Integer> results = new ArrayList<>();

        while ((currentMatchStart + currentCharacterPosition) < endOffset) {

            if (needle[currentCharacterPosition] == (haystack[currentCharacterPosition + currentMatchStart])) {
                currentCharacterPosition = currentCharacterPosition + 1;

                if (currentCharacterPosition == (needleSize)) {
                    results.add(currentMatchStart);
                    currentCharacterPosition = 0;
                    currentMatchStart = currentMatchStart + needleSize;

                }

            } else {
                currentMatchStart = currentMatchStart + currentCharacterPosition
                        - toFillTable[currentCharacterPosition];

                if (toFillTable[currentCharacterPosition] > -1) {
                    currentCharacterPosition = toFillTable[currentCharacterPosition];
                }

                else {
                    currentCharacterPosition = 0;

                }

            }
        }
        return results;
    }

    private static int searchForFirst(byte[] haystack, int beginOffset, byte[] needle) {
        int currentMatchStart = beginOffset;
        int currentCharacterPosition = 0;

        int docSize = haystack.length;
        int needleSize = needle.length;

        int[] toFillTable = buildKMPSearchTable(needle);

        while ((currentMatchStart + currentCharacterPosition) < docSize) {

            if (needle[currentCharacterPosition] == (haystack[currentCharacterPosition + currentMatchStart])) {
                currentCharacterPosition = currentCharacterPosition + 1;

                if (currentCharacterPosition == (needleSize)) {
                    return currentMatchStart;
                }

            } else {
                currentMatchStart = currentMatchStart + currentCharacterPosition
                        - toFillTable[currentCharacterPosition];

                if (toFillTable[currentCharacterPosition] > -1) {
                    currentCharacterPosition = toFillTable[currentCharacterPosition];
                }

                else {
                    currentCharacterPosition = 0;

                }

            }
        }
        return -1;
    }

    private static int[] buildKMPSearchTable(byte[] needle) {
        int[] stable = new int[needle.length];
        int pos = 2;
        int j = 0;
        stable[0] = -1;
        stable[1] = 0;
        while (pos < needle.length) {
            if (needle[pos - 1] == needle[j]) {
                stable[pos] = j + 1;
                pos++;
                j++;
            } else if (j > 0) {
                j = stable[j];
            } else {
                stable[pos] = 0;
                pos++;
            }
        }
        return stable;
    }

    public static String rewriteDescriptionForNewLineSize(String moveDesc, String newline, int lineSize,
            StringSizeDeterminer ssd) {
        // We rewrite the description we're given based on some new chars per
        // line.
        moveDesc = moveDesc.replace("-" + newline, "").replace(newline, " ");
        // Keep spatk/spdef as one word on one line
        moveDesc = moveDesc.replace("Sp. Atk", "Sp__Atk");
        moveDesc = moveDesc.replace("Sp. Def", "Sp__Def");
        moveDesc = moveDesc.replace("SP. ATK", "SP__ATK");
        moveDesc = moveDesc.replace("SP. DEF", "SP__DEF");
        String[] words = moveDesc.split(" ");
        StringBuilder fullDesc = new StringBuilder();
        StringBuilder thisLine = new StringBuilder();
        int currLineWC = 0;
        int currLineCC = 0;
        int linesWritten = 0;
        for (int i = 0; i < words.length; i++) {
            // Reverse the spatk/spdef preservation from above
            words[i] = words[i].replace("SP__", "SP. ");
            words[i] = words[i].replace("Sp__", "Sp. ");
            int reqLength = ssd.lengthFor(words[i]);
            if (currLineWC > 0) {
                reqLength++;
            }
            if (currLineCC + reqLength <= lineSize) {
                // add to current line
                if (currLineWC > 0) {
                    thisLine.append(' ');
                }
                thisLine.append(words[i]);
                currLineWC++;
                currLineCC += reqLength;
            } else {
                // Save current line, if applicable
                if (currLineWC > 0) {
                    if (linesWritten > 0) {
                        fullDesc.append(newline);
                    }
                    fullDesc.append(thisLine.toString());
                    linesWritten++;
                    thisLine = new StringBuilder();
                }
                // Start the new line
                thisLine.append(words[i]);
                currLineWC = 1;
                currLineCC = ssd.lengthFor(words[i]);
            }
        }

        // If the last line has anything add it
        if (currLineWC > 0) {
            if (linesWritten > 0) {
                fullDesc.append(newline);
            }
            fullDesc.append(thisLine.toString());
        }

        return fullDesc.toString();
    }

    public static String formatTextWithReplacements(String text, Map<String, String> replacements, String newline,
            String extraline, String newpara, int maxLineLength, StringSizeDeterminer ssd) {
        // Ends with a paragraph indicator?
        boolean endsWithPara = false;
        if (text.endsWith(newpara)) {
            endsWithPara = true;
            text = text.substring(0, text.length() - newpara.length());
        }
        // Replace current line endings with spaces
        text = text.replace(newline, " ").replace(extraline, " ");
        // Replace words if replacements are set
        // do it in two stages so the rules don't conflict
        if (replacements != null) {
            int index = 0;
            for (Map.Entry<String, String> toReplace : replacements.entrySet()) {
                index++;
                text = text.replace(toReplace.getKey(), "<tmpreplace" + index + ">");
            }
            index = 0;
            for (Map.Entry<String, String> toReplace : replacements.entrySet()) {
                index++;
                text = text.replace("<tmpreplace" + index + ">", toReplace.getValue());
            }
        }
        // Split on paragraphs and deal with each one individually
        String[] oldParagraphs = text.split(newpara.replace("\\", "\\\\"));
        StringBuilder finalResult = new StringBuilder();
        int sentenceNewLineSize = Math.max(10, maxLineLength / 2);
        for (int para = 0; para < oldParagraphs.length; para++) {
            String[] words = oldParagraphs[para].split(" ");
            StringBuilder fullPara = new StringBuilder();
            StringBuilder thisLine = new StringBuilder();
            int currLineWC = 0;
            int currLineCC = 0;
            int linesWritten = 0;
            char currLineLastChar = 0;
            for (String word : words) {
                int reqLength = ssd.lengthFor(word);
                if (currLineWC > 0) {
                    reqLength++;
                }
                if ((currLineCC + reqLength > maxLineLength)
                        || (currLineCC >= sentenceNewLineSize && (currLineLastChar == '.' || currLineLastChar == '?'
                        || currLineLastChar == '!' || currLineLastChar == '…' || currLineLastChar == ','))) {
                    // new line
                    // Save current line, if applicable
                    if (currLineWC > 0) {
                        if (linesWritten > 1) {
                            fullPara.append(extraline);
                        } else if (linesWritten == 1) {
                            fullPara.append(newline);
                        }
                        fullPara.append(thisLine.toString());
                        linesWritten++;
                        thisLine = new StringBuilder();
                    }
                    // Start the new line
                    thisLine.append(word);
                    currLineWC = 1;
                    currLineCC = ssd.lengthFor(word);
                    if (word.length() == 0) {
                        currLineLastChar = 0;
                    } else {
                        currLineLastChar = word.charAt(word.length() - 1);
                    }
                } else {
                    // add to current line
                    if (currLineWC > 0) {
                        thisLine.append(' ');
                    }
                    thisLine.append(word);
                    currLineWC++;
                    currLineCC += reqLength;
                    if (word.length() == 0) {
                        currLineLastChar = 0;
                    } else {
                        currLineLastChar = word.charAt(word.length() - 1);
                    }
                }
            }

            // If the last line has anything add it
            if (currLineWC > 0) {
                if (linesWritten > 1) {
                    fullPara.append(extraline);
                } else if (linesWritten == 1) {
                    fullPara.append(newline);
                }
                fullPara.append(thisLine.toString());
            }
            if (para > 0) {
                finalResult.append(newpara);
            }
            finalResult.append(fullPara.toString());
        }
        if (endsWithPara) {
            finalResult.append(newpara);
        }
        return finalResult.toString();
    }

    public interface StringSizeDeterminer {
        int lengthFor(String encodedText);
    }

    public static class StringLengthSD implements StringSizeDeterminer {

        @Override
        public int lengthFor(String encodedText) {
            return encodedText.length();
        }

    }

}
