package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Gen4RomHandler.java - randomizer handler for D/P/Pt/HG/SS.            --*/
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

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.dabomstew.pkrandom.*;
import com.dabomstew.pkrandom.constants.*;
import com.dabomstew.pkrandom.exceptions.RandomizationException;
import com.dabomstew.pkrandom.pokemon.*;
import thenewpoketext.PokeTextData;
import thenewpoketext.TextToPoke;

import com.dabomstew.pkrandom.exceptions.RandomizerIOException;
import com.dabomstew.pkrandom.newnds.NARCArchive;

public class Gen4RomHandler extends AbstractDSRomHandler {

    public static class Factory extends RomHandler.Factory {

        @Override
        public Gen4RomHandler create(Random random, PrintStream logStream) {
            return new Gen4RomHandler(random, logStream);
        }

        public boolean isLoadable(String filename) {
            return detectNDSRomInner(getROMCodeFromFile(filename), getVersionFromFile(filename));
        }
    }

    public Gen4RomHandler(Random random) {
        super(random, null);
    }

    public Gen4RomHandler(Random random, PrintStream logStream) {
        super(random, logStream);
    }

    private static class RomFileEntry {
        public String path;
        public long expectedCRC32;
    }

    private static class RomEntry {
        private String name;
        private String romCode;
        private byte version;
        private int romType;
        private long arm9ExpectedCRC32;
        private boolean staticPokemonSupport = false, copyStaticPokemon = false,copyRoamingPokemon = false,
                ignoreGameCornerStatics = false, copyText = false;
        private Map<String, String> strings = new HashMap<>();
        private Map<String, String> tweakFiles = new HashMap<>();
        private Map<String, Integer> numbers = new HashMap<>();
        private Map<String, int[]> arrayEntries = new HashMap<>();
        private Map<String, RomFileEntry> files = new HashMap<>();
        private Map<Integer, Long> overlayExpectedCRC32s = new HashMap<>();
        private List<StaticPokemon> staticPokemon = new ArrayList<>();
        private List<RoamingPokemon> roamingPokemon = new ArrayList<>();
        private List<ScriptEntry> marillCryScriptEntries = new ArrayList<>();
        private Map<Integer, List<TextEntry>> tmTexts = new HashMap<>();
        private Map<Integer, TextEntry> tmTextsGameCorner = new HashMap<>();
        private Map<Integer, Integer> tmScriptOffsetsFrontier = new HashMap<>();
        private Map<Integer, Integer> tmTextsFrontier = new HashMap<>();

        private int getInt(String key) {
            if (!numbers.containsKey(key)) {
                numbers.put(key, 0);
            }
            return numbers.get(key);
        }

        private String getString(String key) {
            if (!strings.containsKey(key)) {
                strings.put(key, "");
            }
            return strings.get(key);
        }

        private String getFile(String key) {
            if (!files.containsKey(key)) {
                files.put(key, new RomFileEntry());
            }
            return files.get(key).path;
        }
    }

    private static List<RomEntry> roms;

    static {
        loadROMInfo();

    }

    private static void loadROMInfo() {
        roms = new ArrayList<>();
        RomEntry current = null;
        try {
            Scanner sc = new Scanner(FileFunctions.openConfig("gen4_offsets.ini"), "UTF-8");
            while (sc.hasNextLine()) {
                String q = sc.nextLine().trim();
                if (q.contains("//")) {
                    q = q.substring(0, q.indexOf("//")).trim();
                }
                if (!q.isEmpty()) {
                    if (q.startsWith("[") && q.endsWith("]")) {
                        // New rom
                        current = new RomEntry();
                        current.name = q.substring(1, q.length() - 1);
                        roms.add(current);
                    } else {
                        String[] r = q.split("=", 2);
                        if (r.length == 1) {
                            System.err.println("invalid entry " + q);
                            continue;
                        }
                        if (r[1].endsWith("\r\n")) {
                            r[1] = r[1].substring(0, r[1].length() - 2);
                        }
                        r[1] = r[1].trim();
                        if (r[0].equals("Game")) {
                            current.romCode = r[1];
                        } else if (r[0].equals("Version")) {
                            current.version = Byte.parseByte(r[1]);
                        } else if (r[0].equals("Type")) {
                            if (r[1].equalsIgnoreCase("DP")) {
                                current.romType = Gen4Constants.Type_DP;
                            } else if (r[1].equalsIgnoreCase("Plat")) {
                                current.romType = Gen4Constants.Type_Plat;
                            } else if (r[1].equalsIgnoreCase("HGSS")) {
                                current.romType = Gen4Constants.Type_HGSS;
                            } else {
                                System.err.println("unrecognised rom type: " + r[1]);
                            }
                        } else if (r[0].equals("CopyFrom")) {
                            for (RomEntry otherEntry : roms) {
                                if (r[1].equalsIgnoreCase(otherEntry.name)) {
                                    // copy from here
                                    current.arrayEntries.putAll(otherEntry.arrayEntries);
                                    current.numbers.putAll(otherEntry.numbers);
                                    current.strings.putAll(otherEntry.strings);
                                    current.files.putAll(otherEntry.files);
                                    if (current.copyStaticPokemon) {
                                        current.staticPokemon.addAll(otherEntry.staticPokemon);
                                        if (current.ignoreGameCornerStatics) {
                                            current.staticPokemon.removeIf(staticPokemon -> staticPokemon instanceof StaticPokemonGameCorner);
                                        }
                                        current.staticPokemonSupport = true;
                                    } else {
                                        current.staticPokemonSupport = false;
                                    }
                                    if (current.copyRoamingPokemon) {
                                        current.roamingPokemon.addAll(otherEntry.roamingPokemon);
                                    }
                                    if (current.copyText) {
                                        current.tmTexts.putAll(otherEntry.tmTexts);
                                        current.tmTextsGameCorner.putAll(otherEntry.tmTextsGameCorner);
                                        current.tmScriptOffsetsFrontier.putAll(otherEntry.tmScriptOffsetsFrontier);
                                        current.tmTextsFrontier.putAll(otherEntry.tmTextsFrontier);
                                    }
                                    current.marillCryScriptEntries.addAll(otherEntry.marillCryScriptEntries);
                                }
                            }
                        } else if (r[0].startsWith("File<")) {
                            String key = r[0].split("<")[1].split(">")[0];
                            String[] values = r[1].substring(1, r[1].length() - 1).split(",");
                            RomFileEntry entry = new RomFileEntry();
                            entry.path = values[0].trim();
                            entry.expectedCRC32 = parseRILong("0x" + values[1].trim());
                            current.files.put(key, entry);
                        } else if (r[0].equals("Arm9CRC32")) {
                            current.arm9ExpectedCRC32 = parseRILong("0x" + r[1]);
                        } else if (r[0].startsWith("OverlayCRC32<")) {
                            String keyString = r[0].split("<")[1].split(">")[0];
                            int key = parseRIInt(keyString);
                            long value = parseRILong("0x" + r[1]);
                            current.overlayExpectedCRC32s.put(key, value);
                        } else if (r[0].equals("StaticPokemon{}")) {
                            current.staticPokemon.add(parseStaticPokemon(r[1]));
                        } else if (r[0].equals("RoamingPokemon{}")) {
                            current.roamingPokemon.add(parseRoamingPokemon(r[1]));
                        } else if (r[0].equals("StaticPokemonGameCorner{}")) {
                            current.staticPokemon.add(parseStaticPokemonGameCorner(r[1]));
                        } else if (r[0].equals("TMText{}")) {
                            parseTMText(r[1], current.tmTexts);
                        } else if (r[0].equals("TMTextGameCorner{}")) {
                            parseTMTextGameCorner(r[1], current.tmTextsGameCorner);
                        } else if (r[0].equals("FrontierScriptTMOffsets{}")) {
                            String[] offsets = r[1].substring(1, r[1].length() - 1).split(",");
                            for (String off : offsets) {
                                String[] parts = off.split("=");
                                int tmNum = parseRIInt(parts[0]);
                                int offset = parseRIInt(parts[1]);
                                current.tmScriptOffsetsFrontier.put(tmNum, offset);
                            }
                        } else if (r[0].equals("FrontierTMText{}")) {
                            String[] offsets = r[1].substring(1, r[1].length() - 1).split(",");
                            for (String off : offsets) {
                                String[] parts = off.split("=");
                                int tmNum = parseRIInt(parts[0]);
                                int stringNumber = parseRIInt(parts[1]);
                                current.tmTextsFrontier.put(tmNum, stringNumber);
                            }
                        } else if (r[0].equals("StaticPokemonSupport")) {
                            int spsupport = parseRIInt(r[1]);
                            current.staticPokemonSupport = (spsupport > 0);
                        } else if (r[0].equals("CopyStaticPokemon")) {
                            int csp = parseRIInt(r[1]);
                            current.copyStaticPokemon = (csp > 0);
                        } else if (r[0].equals("CopyRoamingPokemon")) {
                            int crp = parseRIInt(r[1]);
                            current.copyRoamingPokemon = (crp > 0);
                        } else if (r[0].equals("CopyText")) {
                            int ct = parseRIInt(r[1]);
                            current.copyText = (ct > 0);
                        } else if (r[0].equals("IgnoreGameCornerStatics")) {
                            int ct = parseRIInt(r[1]);
                            current.ignoreGameCornerStatics = (ct > 0);
                        } else if (r[0].endsWith("Tweak")) {
                            current.tweakFiles.put(r[0], r[1]);
                        } else if (r[0].endsWith("MarillCryScripts")) {
                            current.marillCryScriptEntries.clear();
                            String[] offsets = r[1].substring(1, r[1].length() - 1).split(",");
                            for (String off : offsets) {
                                String[] parts = off.split(":");
                                int file = parseRIInt(parts[0]);
                                int offset = parseRIInt(parts[1]);
                                ScriptEntry entry = new ScriptEntry(file, offset);
                                current.marillCryScriptEntries.add(entry);
                            }
                        } else {
                            if (r[1].startsWith("[") && r[1].endsWith("]")) {
                                String[] offsets = r[1].substring(1, r[1].length() - 1).split(",");
                                if (offsets.length == 1 && offsets[0].trim().isEmpty()) {
                                    current.arrayEntries.put(r[0], new int[0]);
                                } else {
                                    int[] offs = new int[offsets.length];
                                    int c = 0;
                                    for (String off : offsets) {
                                        offs[c++] = parseRIInt(off);
                                    }
                                    current.arrayEntries.put(r[0], offs);
                                }
                            } else if (r[0].endsWith("Offset") || r[0].endsWith("Count") || r[0].endsWith("Number")
                                    || r[0].endsWith("Size") || r[0].endsWith("Index")) {
                                int offs = parseRIInt(r[1]);
                                current.numbers.put(r[0], offs);
                            } else {
                                current.strings.put(r[0], r[1]);
                            }
                        }
                    }
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
        }

    }

    private static int parseRIInt(String off) {
        int radix = 10;
        off = off.trim().toLowerCase();
        if (off.startsWith("0x") || off.startsWith("&h")) {
            radix = 16;
            off = off.substring(2);
        }
        try {
            return Integer.parseInt(off, radix);
        } catch (NumberFormatException ex) {
            System.err.println("invalid base " + radix + "number " + off);
            return 0;
        }
    }

    private static long parseRILong(String off) {
        int radix = 10;
        off = off.trim().toLowerCase();
        if (off.startsWith("0x") || off.startsWith("&h")) {
            radix = 16;
            off = off.substring(2);
        }
        try {
            return Long.parseLong(off, radix);
        } catch (NumberFormatException ex) {
            System.err.println("invalid base " + radix + "number " + off);
            return 0;
        }
    }

    private static StaticPokemon parseStaticPokemon(String staticPokemonString) {
        StaticPokemon sp = new StaticPokemon();
        String pattern = "[A-z]+=\\[([0-9]+:0x[0-9a-fA-F]+,?\\s?)+]";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(staticPokemonString);
        while (m.find()) {
            String[] segments = m.group().split("=");
            String[] offsets = segments[1].substring(1, segments[1].length() - 1).split(",");
            ScriptEntry[] entries = new ScriptEntry[offsets.length];
            for (int i = 0; i < entries.length; i++) {
                String[] parts = offsets[i].split(":");
                entries[i] = new ScriptEntry(parseRIInt(parts[0]), parseRIInt(parts[1]));
            }
            switch (segments[0]) {
                case "Species":
                    sp.speciesEntries = entries;
                    break;
                case "Level":
                    sp.levelEntries = entries;
                    break;
                case "Forme":
                    sp.formeEntries = entries;
                    break;
            }
        }
        return sp;
    }

    private static StaticPokemonGameCorner parseStaticPokemonGameCorner(String staticPokemonString) {
        StaticPokemonGameCorner sp = new StaticPokemonGameCorner();
        String pattern = "[A-z]+=\\[([0-9]+:0x[0-9a-fA-F]+,?\\s?)+]";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(staticPokemonString);
        while (m.find()) {
            String[] segments = m.group().split("=");
            String[] offsets = segments[1].substring(1, segments[1].length() - 1).split(",");
            switch (segments[0]) {
                case "Species":
                    ScriptEntry[] speciesEntries = new ScriptEntry[offsets.length];
                    for (int i = 0; i < speciesEntries.length; i++) {
                        String[] parts = offsets[i].split(":");
                        speciesEntries[i] = new ScriptEntry(parseRIInt(parts[0]), parseRIInt(parts[1]));
                    }
                    sp.speciesEntries = speciesEntries;
                    break;
                case "Level":
                    ScriptEntry[] levelEntries = new ScriptEntry[offsets.length];
                    for (int i = 0; i < levelEntries.length; i++) {
                        String[] parts = offsets[i].split(":");
                        levelEntries[i] = new ScriptEntry(parseRIInt(parts[0]), parseRIInt(parts[1]));
                    }
                    sp.levelEntries = levelEntries;
                    break;
                case "Text":
                    TextEntry[] textEntries = new TextEntry[offsets.length];
                    for (int i = 0; i < textEntries.length; i++) {
                        String[] parts = offsets[i].split(":");
                        textEntries[i] = new TextEntry(parseRIInt(parts[0]), parseRIInt(parts[1]));
                    }
                    sp.textEntries = textEntries;
                    break;
            }
        }
        return sp;
    }

    private static RoamingPokemon parseRoamingPokemon(String roamingPokemonString) {
        RoamingPokemon rp = new RoamingPokemon();
        String pattern = "[A-z]+=\\[(0x[0-9a-fA-F]+,?\\s?)+]|[A-z]+=\\[([0-9]+:0x[0-9a-fA-F]+,?\\s?)+]";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(roamingPokemonString);
        while (m.find()) {
            String[] segments = m.group().split("=");
            String[] offsets = segments[1].substring(1, segments[1].length() - 1).split(",");
            switch (segments[0]) {
                case "Species":
                    int[] speciesCodeOffsets = new int[offsets.length];
                    for (int i = 0; i < speciesCodeOffsets.length; i++) {
                        speciesCodeOffsets[i] = parseRIInt(offsets[i]);
                    }
                    rp.speciesCodeOffsets = speciesCodeOffsets;
                    break;
                case "Level":
                    int[] levelCodeOffsets = new int[offsets.length];
                    for (int i = 0; i < levelCodeOffsets.length; i++) {
                        levelCodeOffsets[i] = parseRIInt(offsets[i]);
                    }
                    rp.levelCodeOffsets = levelCodeOffsets;
                    break;
                case "Script":
                    ScriptEntry[] scriptEntries = new ScriptEntry[offsets.length];
                    for (int i = 0; i < scriptEntries.length; i++) {
                        String[] parts = offsets[i].split(":");
                        scriptEntries[i] = new ScriptEntry(parseRIInt(parts[0]), parseRIInt(parts[1]));
                    }
                    rp.speciesScriptOffsets = scriptEntries;
                    break;
                case "Gender":
                    ScriptEntry[] genderEntries = new ScriptEntry[offsets.length];
                    for (int i = 0; i < genderEntries.length; i++) {
                        String[] parts = offsets[i].split(":");
                        genderEntries[i] = new ScriptEntry(parseRIInt(parts[0]), parseRIInt(parts[1]));
                    }
                    rp.genderOffsets = genderEntries;
                    break;
            }
        }
        return rp;
    }

    private static void parseTMText(String tmTextString, Map<Integer, List<TextEntry>> tmTexts) {
        String pattern = "[0-9]+=\\[([0-9]+:[0-9]+,?\\s?)+]";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(tmTextString);
        while (m.find()) {
            String[] segments = m.group().split("=");
            int tmNum = parseRIInt(segments[0]);
            String[] entries = segments[1].substring(1, segments[1].length() - 1).split(",");
            List<TextEntry> textEntries = new ArrayList<>();
            for (String entry : entries) {
                String[] textSegments = entry.split(":");
                TextEntry textEntry = new TextEntry(parseRIInt(textSegments[0]), parseRIInt(textSegments[1]));
                textEntries.add(textEntry);
            }
            tmTexts.put(tmNum, textEntries);
        }
    }

    private static void parseTMTextGameCorner(String tmTextGameCornerString, Map<Integer, TextEntry> tmTextGameCorner) {
        String[] tmTextGameCornerEntries = tmTextGameCornerString.substring(1, tmTextGameCornerString.length() - 1).split(",");
        for (String tmTextGameCornerEntry : tmTextGameCornerEntries) {
            String[] segments = tmTextGameCornerEntry.trim().split("=");
            int tmNum = parseRIInt(segments[0]);
            String textEntry = segments[1].substring(1, segments[1].length() - 1);
            String[] textSegments = textEntry.split(":");
            TextEntry entry = new TextEntry(parseRIInt(textSegments[0]), parseRIInt(textSegments[1]));
            tmTextGameCorner.put(tmNum, entry);
        }
    }

    // This rom
    private Pokemon[] pokes;
    private List<Pokemon> pokemonListInclFormes;
    private List<Pokemon> pokemonList;
    private Move[] moves;
    private NARCArchive pokeNarc, moveNarc;
    private NARCArchive msgNarc;
    private NARCArchive scriptNarc;
    private NARCArchive eventNarc;
    private byte[] arm9;
    private List<String> abilityNames;
    private List<String> itemNames;
    private boolean loadedWildMapNames;
    private Map<Integer, String> wildMapNames, headbuttMapNames;
    private ItemList allowedItems, nonBadItems;
    private boolean roamerRandomizationEnabled;
    private boolean effectivenessUpdated;
    private int pickupItemsTableOffset, rarePickupItemsTableOffset;
    private long actualArm9CRC32;
    private Map<Integer, Long> actualOverlayCRC32s;
    private Map<String, Long> actualFileCRC32s;

    private RomEntry romEntry;

    @Override
    protected boolean detectNDSRom(String ndsCode, byte version) {
        return detectNDSRomInner(ndsCode, version);
    }

    private static boolean detectNDSRomInner(String ndsCode, byte version) {
        return entryFor(ndsCode, version) != null;
    }

    private static RomEntry entryFor(String ndsCode, byte version) {
        for (RomEntry re : roms) {
            if (ndsCode.equals(re.romCode) && version == re.version) {
                return re;
            }
        }
        return null;
    }

    @Override
    protected void loadedROM(String romCode, byte version) {
        this.romEntry = entryFor(romCode, version);
        try {
            arm9 = readARM9();
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        try {
            msgNarc = readNARC(romEntry.getFile("Text"));
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        try {
            scriptNarc = readNARC(romEntry.getFile("Scripts"));
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        try {
            eventNarc = readNARC(romEntry.getFile("Events"));
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        loadPokemonStats();
        pokemonListInclFormes = Arrays.asList(pokes);
        pokemonList = Arrays.asList(Arrays.copyOfRange(pokes,0,Gen4Constants.pokemonCount + 1));
        loadMoves();
        abilityNames = getStrings(romEntry.getInt("AbilityNamesTextOffset"));
        itemNames = getStrings(romEntry.getInt("ItemNamesTextOffset"));
        loadedWildMapNames = false;

        allowedItems = Gen4Constants.allowedItems.copy();
        nonBadItems = Gen4Constants.nonBadItems.copy();

        roamerRandomizationEnabled =
                (romEntry.romType == Gen4Constants.Type_DP && romEntry.roamingPokemon.size() > 0) ||
                (romEntry.romType == Gen4Constants.Type_Plat && romEntry.tweakFiles.containsKey("NewRoamerSubroutineTweak")) ||
                (romEntry.romType == Gen4Constants.Type_HGSS && romEntry.tweakFiles.containsKey("NewRoamerSubroutineTweak"));

        try {
            computeCRC32sForRom();
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        // We want to guarantee that the catching tutorial in HGSS has Ethan/Lyra's new Pokemon. We also
        // want to allow the option of randomizing the enemy Pokemon too. Unfortunately, the latter can
        // occur *before* the former, but there's no guarantee that it will even happen. Since we *know*
        // we'll need to do this patch eventually, just expand the arm9 here to make things easy.
        if (romEntry.romType == Gen4Constants.Type_HGSS && romEntry.tweakFiles.containsKey("NewCatchingTutorialSubroutineTweak")) {
            int extendBy = romEntry.getInt("Arm9ExtensionSize");
            arm9 = extendARM9(arm9, extendBy, romEntry.getString("TCMCopyingPrefix"), Gen4Constants.arm9Offset);
            genericIPSPatch(arm9, "NewCatchingTutorialSubroutineTweak");
        }
    }

    private void loadMoves() {
        try {
            moveNarc = this.readNARC(romEntry.getFile("MoveData"));
            moves = new Move[Gen4Constants.moveCount + 1];
            List<String> moveNames = getStrings(romEntry.getInt("MoveNamesTextOffset"));
            for (int i = 1; i <= Gen4Constants.moveCount; i++) {
                byte[] moveData = moveNarc.files.get(i);
                moves[i] = new Move();
                moves[i].name = moveNames.get(i);
                moves[i].number = i;
                moves[i].internalId = i;
                moves[i].effectIndex = readWord(moveData, 0);
                moves[i].hitratio = (moveData[5] & 0xFF);
                moves[i].power = moveData[3] & 0xFF;
                moves[i].pp = moveData[6] & 0xFF;
                moves[i].type = Gen4Constants.typeTable[moveData[4] & 0xFF];
                moves[i].target = readWord(moveData, 8);
                moves[i].category = Gen4Constants.moveCategoryIndices[moveData[2] & 0xFF];
                moves[i].priority = moveData[10];
                int flags = moveData[11] & 0xFF;
                moves[i].makesContact = (flags & 1) != 0;
                moves[i].isPunchMove = Gen4Constants.punchMoves.contains(moves[i].number);
                moves[i].isSoundMove = Gen4Constants.soundMoves.contains(moves[i].number);

                if (i == Moves.swift) {
                    perfectAccuracy = (int)moves[i].hitratio;
                }

                if (GlobalConstants.normalMultihitMoves.contains(i)) {
                    moves[i].hitCount = 3;
                } else if (GlobalConstants.doubleHitMoves.contains(i)) {
                    moves[i].hitCount = 2;
                } else if (i == Moves.tripleKick) {
                    moves[i].hitCount = 2.71; // this assumes the first hit lands
                }

                int secondaryEffectChance = moveData[7] & 0xFF;
                loadStatChangesFromEffect(moves[i], secondaryEffectChance);
                loadStatusFromEffect(moves[i], secondaryEffectChance);
                loadMiscMoveInfoFromEffect(moves[i], secondaryEffectChance);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void loadStatChangesFromEffect(Move move, int secondaryEffectChance) {
        switch (move.effectIndex) {
            case Gen4Constants.noDamageAtkPlusOneEffect:
            case Gen4Constants.noDamageDefPlusOneEffect:
            case Gen4Constants.noDamageSpAtkPlusOneEffect:
            case Gen4Constants.noDamageEvasionPlusOneEffect:
            case Gen4Constants.noDamageAtkMinusOneEffect:
            case Gen4Constants.noDamageDefMinusOneEffect:
            case Gen4Constants.noDamageSpeMinusOneEffect:
            case Gen4Constants.noDamageAccuracyMinusOneEffect:
            case Gen4Constants.noDamageEvasionMinusOneEffect:
            case Gen4Constants.noDamageAtkPlusTwoEffect:
            case Gen4Constants.noDamageDefPlusTwoEffect:
            case Gen4Constants.noDamageSpePlusTwoEffect:
            case Gen4Constants.noDamageSpAtkPlusTwoEffect:
            case Gen4Constants.noDamageSpDefPlusTwoEffect:
            case Gen4Constants.noDamageAtkMinusTwoEffect:
            case Gen4Constants.noDamageDefMinusTwoEffect:
            case Gen4Constants.noDamageSpeMinusTwoEffect:
            case Gen4Constants.noDamageSpDefMinusTwoEffect:
            case Gen4Constants.minimizeEffect:
            case Gen4Constants.swaggerEffect:
            case Gen4Constants.defenseCurlEffect:
            case Gen4Constants.flatterEffect:
            case Gen4Constants.chargeEffect:
            case Gen4Constants.noDamageAtkAndDefMinusOneEffect:
            case Gen4Constants.noDamageDefAndSpDefPlusOneEffect:
            case Gen4Constants.noDamageAtkAndDefPlusOneEffect:
            case Gen4Constants.noDamageSpAtkAndSpDefPlusOneEffect:
            case Gen4Constants.noDamageAtkAndSpePlusOneEffect:
            case Gen4Constants.noDamageSpAtkMinusTwoEffect:
                if (move.target == 16) {
                    move.statChangeMoveType = StatChangeMoveType.NO_DAMAGE_USER;
                } else {
                    move.statChangeMoveType = StatChangeMoveType.NO_DAMAGE_TARGET;
                }
                break;

            case Gen4Constants.damageAtkMinusOneEffect:
            case Gen4Constants.damageDefMinusOneEffect:
            case Gen4Constants.damageSpeMinusOneEffect:
            case Gen4Constants.damageSpAtkMinusOneEffect:
            case Gen4Constants.damageSpDefMinusOneEffect:
            case Gen4Constants.damageAccuracyMinusOneEffect:
            case Gen4Constants.damageSpDefMinusTwoEffect:
                move.statChangeMoveType = StatChangeMoveType.DAMAGE_TARGET;
                break;

            case Gen4Constants.damageUserDefPlusOneEffect:
            case Gen4Constants.damageUserAtkPlusOneEffect:
            case Gen4Constants.damageUserAllPlusOneEffect:
            case Gen4Constants.damageUserAtkAndDefMinusOneEffect:
            case Gen4Constants.damageUserSpAtkMinusTwoEffect:
            case Gen4Constants.damageUserSpeMinusOneEffect:
            case Gen4Constants.damageUserDefAndSpDefMinusOneEffect:
            case Gen4Constants.damageUserSpAtkPlusOneEffect:
                move.statChangeMoveType = StatChangeMoveType.DAMAGE_USER;
                break;

            default:
                // Move does not have a stat-changing effect
                return;
        }

        switch (move.effectIndex) {
            case Gen4Constants.noDamageAtkPlusOneEffect:
            case Gen4Constants.damageUserAtkPlusOneEffect:
                move.statChanges[0].type = StatChangeType.ATTACK;
                move.statChanges[0].stages = 1;
                break;
            case Gen4Constants.noDamageDefPlusOneEffect:
            case Gen4Constants.damageUserDefPlusOneEffect:
            case Gen4Constants.defenseCurlEffect:
                move.statChanges[0].type = StatChangeType.DEFENSE;
                move.statChanges[0].stages = 1;
                break;
            case Gen4Constants.noDamageSpAtkPlusOneEffect:
            case Gen4Constants.flatterEffect:
            case Gen4Constants.damageUserSpAtkPlusOneEffect:
                move.statChanges[0].type = StatChangeType.SPECIAL_ATTACK;
                move.statChanges[0].stages = 1;
                break;
            case Gen4Constants.noDamageEvasionPlusOneEffect:
            case Gen4Constants.minimizeEffect:
                move.statChanges[0].type = StatChangeType.EVASION;
                move.statChanges[0].stages = 1;
                break;
            case Gen4Constants.noDamageAtkMinusOneEffect:
            case Gen4Constants.damageAtkMinusOneEffect:
                move.statChanges[0].type = StatChangeType.ATTACK;
                move.statChanges[0].stages = -1;
                break;
            case Gen4Constants.noDamageDefMinusOneEffect:
            case Gen4Constants.damageDefMinusOneEffect:
                move.statChanges[0].type = StatChangeType.DEFENSE;
                move.statChanges[0].stages = -1;
                break;
            case Gen4Constants.noDamageSpeMinusOneEffect:
            case Gen4Constants.damageSpeMinusOneEffect:
            case Gen4Constants.damageUserSpeMinusOneEffect:
                move.statChanges[0].type = StatChangeType.SPEED;
                move.statChanges[0].stages = -1;
                break;
            case Gen4Constants.noDamageAccuracyMinusOneEffect:
            case Gen4Constants.damageAccuracyMinusOneEffect:
                move.statChanges[0].type = StatChangeType.ACCURACY;
                move.statChanges[0].stages = -1;
                break;
            case Gen4Constants.noDamageEvasionMinusOneEffect:
                move.statChanges[0].type = StatChangeType.EVASION;
                move.statChanges[0].stages = -1;
                break;
            case Gen4Constants.noDamageAtkPlusTwoEffect:
            case Gen4Constants.swaggerEffect:
                move.statChanges[0].type = StatChangeType.ATTACK;
                move.statChanges[0].stages = 2;
                break;
            case Gen4Constants.noDamageDefPlusTwoEffect:
                move.statChanges[0].type = StatChangeType.DEFENSE;
                move.statChanges[0].stages = 2;
                break;
            case Gen4Constants.noDamageSpePlusTwoEffect:
                move.statChanges[0].type = StatChangeType.SPEED;
                move.statChanges[0].stages = 2;
                break;
            case Gen4Constants.noDamageSpAtkPlusTwoEffect:
                move.statChanges[0].type = StatChangeType.SPECIAL_ATTACK;
                move.statChanges[0].stages = 2;
                break;
            case Gen4Constants.noDamageSpDefPlusTwoEffect:
                move.statChanges[0].type = StatChangeType.SPECIAL_DEFENSE;
                move.statChanges[0].stages = 2;
                break;
            case Gen4Constants.noDamageAtkMinusTwoEffect:
                move.statChanges[0].type = StatChangeType.ATTACK;
                move.statChanges[0].stages = -2;
                break;
            case Gen4Constants.noDamageDefMinusTwoEffect:
                move.statChanges[0].type = StatChangeType.DEFENSE;
                move.statChanges[0].stages = -2;
                break;
            case Gen4Constants.noDamageSpeMinusTwoEffect:
                move.statChanges[0].type = StatChangeType.SPEED;
                move.statChanges[0].stages = -2;
                break;
            case Gen4Constants.noDamageSpDefMinusTwoEffect:
            case Gen4Constants.damageSpDefMinusTwoEffect:
                move.statChanges[0].type = StatChangeType.SPECIAL_DEFENSE;
                move.statChanges[0].stages = -2;
                break;
            case Gen4Constants.damageSpAtkMinusOneEffect:
                move.statChanges[0].type = StatChangeType.SPECIAL_ATTACK;
                move.statChanges[0].stages = -1;
                break;
            case Gen4Constants.damageSpDefMinusOneEffect:
                move.statChanges[0].type = StatChangeType.SPECIAL_DEFENSE;
                move.statChanges[0].stages = -1;
                break;
            case Gen4Constants.damageUserAllPlusOneEffect:
                move.statChanges[0].type = StatChangeType.ALL;
                move.statChanges[0].stages = 1;
                break;
            case Gen4Constants.chargeEffect:
                move.statChanges[0].type = StatChangeType.SPECIAL_DEFENSE;
                move.statChanges[0].stages = 1;
                break;
            case Gen4Constants.damageUserAtkAndDefMinusOneEffect:
            case Gen4Constants.noDamageAtkAndDefMinusOneEffect:
                move.statChanges[0].type = StatChangeType.ATTACK;
                move.statChanges[0].stages = -1;
                move.statChanges[1].type = StatChangeType.DEFENSE;
                move.statChanges[1].stages = -1;
                break;
            case Gen4Constants.damageUserSpAtkMinusTwoEffect:
            case Gen4Constants.noDamageSpAtkMinusTwoEffect:
                move.statChanges[0].type = StatChangeType.SPECIAL_ATTACK;
                move.statChanges[0].stages = -2;
                break;
            case Gen4Constants.noDamageDefAndSpDefPlusOneEffect:
                move.statChanges[0].type = StatChangeType.DEFENSE;
                move.statChanges[0].stages = 1;
                move.statChanges[1].type = StatChangeType.SPECIAL_DEFENSE;
                move.statChanges[1].stages = 1;
                break;
            case Gen4Constants.noDamageAtkAndDefPlusOneEffect:
                move.statChanges[0].type = StatChangeType.ATTACK;
                move.statChanges[0].stages = 1;
                move.statChanges[1].type = StatChangeType.DEFENSE;
                move.statChanges[1].stages = 1;
                break;
            case Gen4Constants.noDamageSpAtkAndSpDefPlusOneEffect:
                move.statChanges[0].type = StatChangeType.SPECIAL_ATTACK;
                move.statChanges[0].stages = 1;
                move.statChanges[1].type = StatChangeType.SPECIAL_DEFENSE;
                move.statChanges[1].stages = 1;
                break;
            case Gen4Constants.noDamageAtkAndSpePlusOneEffect:
                move.statChanges[0].type = StatChangeType.ATTACK;
                move.statChanges[0].stages = 1;
                move.statChanges[1].type = StatChangeType.SPEED;
                move.statChanges[1].stages = 1;
                break;
            case Gen4Constants.damageUserDefAndSpDefMinusOneEffect:
                move.statChanges[0].type = StatChangeType.DEFENSE;
                move.statChanges[0].stages = -1;
                move.statChanges[1].type = StatChangeType.SPECIAL_DEFENSE;
                move.statChanges[1].stages = -1;
                break;
        }

        if (move.statChangeMoveType == StatChangeMoveType.DAMAGE_TARGET || move.statChangeMoveType == StatChangeMoveType.DAMAGE_USER) {
            for (int i = 0; i < move.statChanges.length; i++) {
                if (move.statChanges[i].type != StatChangeType.NONE) {
                    move.statChanges[i].percentChance = secondaryEffectChance;
                    if (move.statChanges[i].percentChance == 0.0) {
                        move.statChanges[i].percentChance = 100.0;
                    }
                }
            }
        }
    }

    private void loadStatusFromEffect(Move move, int secondaryEffectChance) {
        switch (move.effectIndex) {
            case Gen4Constants.noDamageSleepEffect:
            case Gen4Constants.toxicEffect:
            case Gen4Constants.noDamageConfusionEffect:
            case Gen4Constants.noDamagePoisonEffect:
            case Gen4Constants.noDamageParalyzeEffect:
            case Gen4Constants.noDamageBurnEffect:
            case Gen4Constants.swaggerEffect:
            case Gen4Constants.flatterEffect:
            case Gen4Constants.teeterDanceEffect:
                move.statusMoveType = StatusMoveType.NO_DAMAGE;
                break;

            case Gen4Constants.damagePoisonEffect:
            case Gen4Constants.damageBurnEffect:
            case Gen4Constants.damageFreezeEffect:
            case Gen4Constants.damageParalyzeEffect:
            case Gen4Constants.damageConfusionEffect:
            case Gen4Constants.twineedleEffect:
            case Gen4Constants.damageBurnAndThawUserEffect:
            case Gen4Constants.thunderEffect:
            case Gen4Constants.blazeKickEffect:
            case Gen4Constants.poisonFangEffect:
            case Gen4Constants.damagePoisonWithIncreasedCritEffect:
            case Gen4Constants.flareBlitzEffect:
            case Gen4Constants.blizzardEffect:
            case Gen4Constants.voltTackleEffect:
            case Gen4Constants.bounceEffect:
            case Gen4Constants.chatterEffect:
            case Gen4Constants.fireFangEffect:
            case Gen4Constants.iceFangEffect:
            case Gen4Constants.thunderFangEffect:
                move.statusMoveType = StatusMoveType.DAMAGE;
                break;

            default:
                // Move does not have a status effect
                return;
        }

        switch (move.effectIndex) {
            case Gen4Constants.noDamageSleepEffect:
                move.statusType = StatusType.SLEEP;
                break;
            case Gen4Constants.damagePoisonEffect:
            case Gen4Constants.noDamagePoisonEffect:
            case Gen4Constants.twineedleEffect:
            case Gen4Constants.damagePoisonWithIncreasedCritEffect:
                move.statusType = StatusType.POISON;
                break;
            case Gen4Constants.damageBurnEffect:
            case Gen4Constants.damageBurnAndThawUserEffect:
            case Gen4Constants.noDamageBurnEffect:
            case Gen4Constants.blazeKickEffect:
            case Gen4Constants.flareBlitzEffect:
            case Gen4Constants.fireFangEffect:
                move.statusType = StatusType.BURN;
                break;
            case Gen4Constants.damageFreezeEffect:
            case Gen4Constants.blizzardEffect:
            case Gen4Constants.iceFangEffect:
                move.statusType = StatusType.FREEZE;
                break;
            case Gen4Constants.damageParalyzeEffect:
            case Gen4Constants.noDamageParalyzeEffect:
            case Gen4Constants.thunderEffect:
            case Gen4Constants.voltTackleEffect:
            case Gen4Constants.bounceEffect:
            case Gen4Constants.thunderFangEffect:
                move.statusType = StatusType.PARALYZE;
                break;
            case Gen4Constants.toxicEffect:
            case Gen4Constants.poisonFangEffect:
                move.statusType = StatusType.TOXIC_POISON;
                break;
            case Gen4Constants.noDamageConfusionEffect:
            case Gen4Constants.damageConfusionEffect:
            case Gen4Constants.swaggerEffect:
            case Gen4Constants.flatterEffect:
            case Gen4Constants.teeterDanceEffect:
            case Gen4Constants.chatterEffect:
                move.statusType = StatusType.CONFUSION;
                break;
        }

        if (move.statusMoveType == StatusMoveType.DAMAGE) {
            move.statusPercentChance = secondaryEffectChance;
            if (move.statusPercentChance == 0.0) {
                if (move.number == Moves.chatter) {
                    move.statusPercentChance = 1.0;
                } else {
                    move.statusPercentChance = 100.0;
                }
            }
        }
    }

    private void loadMiscMoveInfoFromEffect(Move move, int secondaryEffectChance) {
        switch (move.effectIndex) {
            case Gen4Constants.increasedCritEffect:
            case Gen4Constants.blazeKickEffect:
            case Gen4Constants.damagePoisonWithIncreasedCritEffect:
                move.criticalChance = CriticalChance.INCREASED;
                break;

            case Gen4Constants.futureSightAndDoomDesireEffect:
                move.criticalChance = CriticalChance.NONE;

            case Gen4Constants.flinchEffect:
            case Gen4Constants.snoreEffect:
            case Gen4Constants.twisterEffect:
            case Gen4Constants.stompEffect:
            case Gen4Constants.fakeOutEffect:
            case Gen4Constants.fireFangEffect:
            case Gen4Constants.iceFangEffect:
            case Gen4Constants.thunderFangEffect:
                move.flinchPercentChance = secondaryEffectChance;
                break;

            case Gen4Constants.damageAbsorbEffect:
            case Gen4Constants.dreamEaterEffect:
                move.absorbPercent = 50;
                break;

            case Gen4Constants.damageRecoil25PercentEffect:
                move.recoilPercent = 25;
                break;

            case Gen4Constants.damageRecoil33PercentEffect:
            case Gen4Constants.flareBlitzEffect:
            case Gen4Constants.voltTackleEffect:
                move.recoilPercent = 33;
                break;

            case Gen4Constants.damageRecoil50PercentEffect:
                move.recoilPercent = 50;
                break;

            case Gen4Constants.bindingEffect:
            case Gen4Constants.trappingEffect:
                move.isTrapMove = true;
                break;

            case Gen4Constants.skullBashEffect:
            case Gen4Constants.solarbeamEffect:
            case Gen4Constants.flyEffect:
            case Gen4Constants.diveEffect:
            case Gen4Constants.digEffect:
            case Gen4Constants.bounceEffect:
            case Gen4Constants.shadowForceEffect:
                move.isChargeMove = true;
                break;

            case Gen3Constants.rechargeEffect:
                move.isRechargeMove = true;
                break;

            case Gen4Constants.razorWindEffect:
                move.criticalChance = CriticalChance.INCREASED;
                move.isChargeMove = true;
                break;

            case Gen4Constants.skyAttackEffect:
                move.criticalChance = CriticalChance.INCREASED;
                move.flinchPercentChance = secondaryEffectChance;
                move.isChargeMove = true;
                break;
        }
    }

    private void loadPokemonStats() {
        try {
            String pstatsnarc = romEntry.getFile("PokemonStats");
            pokeNarc = this.readNARC(pstatsnarc);
            String[] pokeNames = readPokemonNames();
            int formeCount = Gen4Constants.getFormeCount(romEntry.romType);
            pokes = new Pokemon[Gen4Constants.pokemonCount + formeCount + 1];
            for (int i = 1; i <= Gen4Constants.pokemonCount; i++) {
                pokes[i] = new Pokemon();
                pokes[i].number = i;
                loadBasicPokeStats(pokes[i], pokeNarc.files.get(i));
                // Name?
                pokes[i].name = pokeNames[i];
            }

            int i = Gen4Constants.pokemonCount + 1;
            for (int k: Gen4Constants.formeMappings.keySet()) {
                if (i >= pokes.length) {
                    break;
                }
                pokes[i] = new Pokemon();
                pokes[i].number = i;
                loadBasicPokeStats(pokes[i], pokeNarc.files.get(k));
                pokes[i].name = pokeNames[Gen4Constants.formeMappings.get(k).baseForme];
                pokes[i].baseForme = pokes[Gen4Constants.formeMappings.get(k).baseForme];
                pokes[i].formeNumber = Gen4Constants.formeMappings.get(k).formeNumber;
                pokes[i].formeSuffix = Gen4Constants.formeSuffixes.get(k);
                i = i + 1;
            }

            populateEvolutions();
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

    }

    private void loadBasicPokeStats(Pokemon pkmn, byte[] stats) {
        pkmn.hp = stats[Gen4Constants.bsHPOffset] & 0xFF;
        pkmn.attack = stats[Gen4Constants.bsAttackOffset] & 0xFF;
        pkmn.defense = stats[Gen4Constants.bsDefenseOffset] & 0xFF;
        pkmn.speed = stats[Gen4Constants.bsSpeedOffset] & 0xFF;
        pkmn.spatk = stats[Gen4Constants.bsSpAtkOffset] & 0xFF;
        pkmn.spdef = stats[Gen4Constants.bsSpDefOffset] & 0xFF;
        // Type
        pkmn.primaryType = Gen4Constants.typeTable[stats[Gen4Constants.bsPrimaryTypeOffset] & 0xFF];
        pkmn.secondaryType = Gen4Constants.typeTable[stats[Gen4Constants.bsSecondaryTypeOffset] & 0xFF];
        // Only one type?
        if (pkmn.secondaryType == pkmn.primaryType) {
            pkmn.secondaryType = null;
        }
        pkmn.catchRate = stats[Gen4Constants.bsCatchRateOffset] & 0xFF;
        pkmn.growthCurve = ExpCurve.fromByte(stats[Gen4Constants.bsGrowthCurveOffset]);

        // Abilities
        pkmn.ability1 = stats[Gen4Constants.bsAbility1Offset] & 0xFF;
        pkmn.ability2 = stats[Gen4Constants.bsAbility2Offset] & 0xFF;

        // Held Items?
        int item1 = readWord(stats, Gen4Constants.bsCommonHeldItemOffset);
        int item2 = readWord(stats, Gen4Constants.bsRareHeldItemOffset);

        if (item1 == item2) {
            // guaranteed
            pkmn.guaranteedHeldItem = item1;
            pkmn.commonHeldItem = 0;
            pkmn.rareHeldItem = 0;
        } else {
            pkmn.guaranteedHeldItem = 0;
            pkmn.commonHeldItem = item1;
            pkmn.rareHeldItem = item2;
        }
        pkmn.darkGrassHeldItem = -1;

        pkmn.genderRatio = stats[Gen4Constants.bsGenderRatioOffset] & 0xFF;

        int cosmeticForms = Gen4Constants.cosmeticForms.getOrDefault(pkmn.number,0);
        if (cosmeticForms > 0 && romEntry.romType != Gen4Constants.Type_DP) {
            pkmn.cosmeticForms = cosmeticForms;
        }
    }

    private String[] readPokemonNames() {
        String[] pokeNames = new String[Gen4Constants.pokemonCount + 1];
        List<String> nameList = getStrings(romEntry.getInt("PokemonNamesTextOffset"));
        for (int i = 1; i <= Gen4Constants.pokemonCount; i++) {
            pokeNames[i] = nameList.get(i);
        }
        return pokeNames;
    }

    @Override
    protected void savingROM() {
        savePokemonStats();
        saveMoves();
        try {
            writeARM9(arm9);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        try {
            writeNARC(romEntry.getFile("Text"), msgNarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        try {
            writeNARC(romEntry.getFile("Scripts"), scriptNarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        try {
            writeNARC(romEntry.getFile("Events"), eventNarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void saveMoves() {
        for (int i = 1; i <= Gen4Constants.moveCount; i++) {
            byte[] data = moveNarc.files.get(i);
            writeWord(data, 0, moves[i].effectIndex);
            data[2] = Gen4Constants.moveCategoryToByte(moves[i].category);
            data[3] = (byte) moves[i].power;
            data[4] = Gen4Constants.typeToByte(moves[i].type);
            int hitratio = (int) Math.round(moves[i].hitratio);
            if (hitratio < 0) {
                hitratio = 0;
            }
            if (hitratio > 100) {
                hitratio = 100;
            }
            data[5] = (byte) hitratio;
            data[6] = (byte) moves[i].pp;
        }

        try {
            this.writeNARC(romEntry.getFile("MoveData"), moveNarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

    }

    private void savePokemonStats() {
        // Update the "a/an X" list too, if it exists
        List<String> namesList = getStrings(romEntry.getInt("PokemonNamesTextOffset"));
        int formeCount = Gen4Constants.getFormeCount(romEntry.romType);
        if (romEntry.getString("HasExtraPokemonNames").equalsIgnoreCase("Yes")) {
            List<String> namesList2 = getStrings(romEntry.getInt("PokemonNamesTextOffset") + 1);
            for (int i = 1; i <= Gen4Constants.pokemonCount + formeCount; i++) {
                if (i > Gen4Constants.pokemonCount) {
                    saveBasicPokeStats(pokes[i], pokeNarc.files.get(i + Gen4Constants.formeOffset));
                    continue;
                }
                saveBasicPokeStats(pokes[i], pokeNarc.files.get(i));
                String oldName = namesList.get(i);
                namesList.set(i, pokes[i].name);
                namesList2.set(i, namesList2.get(i).replace(oldName, pokes[i].name));
            }
            setStrings(romEntry.getInt("PokemonNamesTextOffset") + 1, namesList2, false);
        } else {
            for (int i = 1; i <= Gen4Constants.pokemonCount + formeCount; i++) {
                if (i > Gen4Constants.pokemonCount) {
                    saveBasicPokeStats(pokes[i], pokeNarc.files.get(i + Gen4Constants.formeOffset));
                    continue;
                }
                saveBasicPokeStats(pokes[i], pokeNarc.files.get(i));
                namesList.set(i, pokes[i].name);
            }
        }
        setStrings(romEntry.getInt("PokemonNamesTextOffset"), namesList, false);

        try {
            String pstatsnarc = romEntry.getFile("PokemonStats");
            this.writeNARC(pstatsnarc, pokeNarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        writeEvolutions();

    }

    private void saveBasicPokeStats(Pokemon pkmn, byte[] stats) {
        stats[Gen4Constants.bsHPOffset] = (byte) pkmn.hp;
        stats[Gen4Constants.bsAttackOffset] = (byte) pkmn.attack;
        stats[Gen4Constants.bsDefenseOffset] = (byte) pkmn.defense;
        stats[Gen4Constants.bsSpeedOffset] = (byte) pkmn.speed;
        stats[Gen4Constants.bsSpAtkOffset] = (byte) pkmn.spatk;
        stats[Gen4Constants.bsSpDefOffset] = (byte) pkmn.spdef;
        stats[Gen4Constants.bsPrimaryTypeOffset] = Gen4Constants.typeToByte(pkmn.primaryType);
        if (pkmn.secondaryType == null) {
            stats[Gen4Constants.bsSecondaryTypeOffset] = stats[Gen4Constants.bsPrimaryTypeOffset];
        } else {
            stats[Gen4Constants.bsSecondaryTypeOffset] = Gen4Constants.typeToByte(pkmn.secondaryType);
        }
        stats[Gen4Constants.bsCatchRateOffset] = (byte) pkmn.catchRate;
        stats[Gen4Constants.bsGrowthCurveOffset] = pkmn.growthCurve.toByte();

        stats[Gen4Constants.bsAbility1Offset] = (byte) pkmn.ability1;
        stats[Gen4Constants.bsAbility2Offset] = (byte) pkmn.ability2;

        // Held items
        if (pkmn.guaranteedHeldItem > 0) {
            writeWord(stats, Gen4Constants.bsCommonHeldItemOffset, pkmn.guaranteedHeldItem);
            writeWord(stats, Gen4Constants.bsRareHeldItemOffset, pkmn.guaranteedHeldItem);
        } else {
            writeWord(stats, Gen4Constants.bsCommonHeldItemOffset, pkmn.commonHeldItem);
            writeWord(stats, Gen4Constants.bsRareHeldItemOffset, pkmn.rareHeldItem);
        }
    }

    @Override
    public List<Pokemon> getPokemon() {
        return pokemonList;
    }

    @Override
    public List<Pokemon> getPokemonInclFormes() {
        return pokemonListInclFormes; // No formes for now
    }

    @Override
    public List<Pokemon> getAltFormes() {
        int formeCount = Gen4Constants.getFormeCount(romEntry.romType);
        return pokemonListInclFormes.subList(Gen4Constants.pokemonCount + 1, Gen4Constants.pokemonCount + formeCount + 1);
    }

    @Override
    public List<MegaEvolution> getMegaEvolutions() {
        return new ArrayList<>();
    }

    @Override
    public Pokemon getAltFormeOfPokemon(Pokemon pk, int forme) {
        int pokeNum = Gen4Constants.getAbsolutePokeNumByBaseForme(pk.number,forme);
        return pokeNum != 0 ? pokes[pokeNum] : pk;
    }

    @Override
    public List<Pokemon> getIrregularFormes() {
        return new ArrayList<>();
    }

    @Override
    public boolean hasFunctionalFormes() {
        return romEntry.romType != Gen4Constants.Type_DP;
    }

    @Override
    public List<Pokemon> getStarters() {
        if (romEntry.romType == Gen4Constants.Type_HGSS) {
            List<Integer> tailOffsets = RomFunctions.search(arm9, Gen4Constants.hgssStarterCodeSuffix);
            if (tailOffsets.size() == 1) {
                // Found starters
                int starterOffset = tailOffsets.get(0) - 13;
                int poke1 = readWord(arm9, starterOffset);
                int poke2 = readWord(arm9, starterOffset + 4);
                int poke3 = readWord(arm9, starterOffset + 8);
                return Arrays.asList(pokes[poke1], pokes[poke2], pokes[poke3]);
            } else {
                return Arrays.asList(pokes[Species.chikorita], pokes[Species.cyndaquil],
                        pokes[Species.totodile]);
            }
        } else {
            try {
                byte[] starterData = readOverlay(romEntry.getInt("StarterPokemonOvlNumber"));
                int poke1 = readWord(starterData, romEntry.getInt("StarterPokemonOffset"));
                int poke2 = readWord(starterData, romEntry.getInt("StarterPokemonOffset") + 4);
                int poke3 = readWord(starterData, romEntry.getInt("StarterPokemonOffset") + 8);
                return Arrays.asList(pokes[poke1], pokes[poke2], pokes[poke3]);
            } catch (IOException e) {
                throw new RandomizerIOException(e);
            }
        }
    }

    @Override
    public boolean setStarters(List<Pokemon> newStarters) {
        if (newStarters.size() != 3) {
            return false;
        }

        if (romEntry.romType == Gen4Constants.Type_HGSS) {
            List<Integer> tailOffsets = RomFunctions.search(arm9, Gen4Constants.hgssStarterCodeSuffix);
            if (tailOffsets.size() == 1) {
                // Found starters
                int starterOffset = tailOffsets.get(0) - 13;
                writeWord(arm9, starterOffset, newStarters.get(0).number);
                writeWord(arm9, starterOffset + 4, newStarters.get(1).number);
                writeWord(arm9, starterOffset + 8, newStarters.get(2).number);
                // Go fix the rival scripts, which rely on fixed pokemon numbers
                // The logic to be changed each time is roughly:
                // Set 0x800C = player starter
                // If(0x800C==152) { trainerbattle rival w/ cynda }
                // ElseIf(0x800C==155) { trainerbattle rival w/ totodile }
                // Else { trainerbattle rival w/ chiko }
                // So we basically have to adjust the 152 and the 155.
                int[] filesWithRivalScript = Gen4Constants.hgssFilesWithRivalScript;
                // below code represents a rival script for sure
                // it means: StoreStarter2 0x800C; If 0x800C 152; CheckLR B_!=
                // <offset to follow>
                byte[] magic = Gen4Constants.hgssRivalScriptMagic;
                NARCArchive scriptNARC = scriptNarc;
                for (int fileCheck : filesWithRivalScript) {
                    byte[] file = scriptNARC.files.get(fileCheck);
                    List<Integer> rivalOffsets = RomFunctions.search(file, magic);
                    if (rivalOffsets.size() == 1) {
                        // found, adjust
                        int baseOffset = rivalOffsets.get(0);
                        // Replace 152 (chiko) with first starter
                        writeWord(file, baseOffset + 8, newStarters.get(0).number);
                        int jumpAmount = readLong(file, baseOffset + 13);
                        int secondBase = jumpAmount + baseOffset + 17;
                        // TODO find out what this constant 0x11 is and remove
                        // it
                        if (file[secondBase] != 0x11 || (file[secondBase + 4] & 0xFF) != Species.cyndaquil) {
                            // This isn't what we were expecting...
                        } else {
                            // Replace 155 (cynda) with 2nd starter
                            writeWord(file, secondBase + 4, newStarters.get(1).number);
                        }
                    }
                }
                // Fix starter text
                List<String> spStrings = getStrings(romEntry.getInt("StarterScreenTextOffset"));
                String[] intros = new String[] { "So, you like", "You’ll take", "Do you want" };
                for (int i = 0; i < 3; i++) {
                    Pokemon newStarter = newStarters.get(i);
                    int color = (i == 0) ? 3 : i;
                    String newStarterDesc = "Professor Elm: " + intros[i] + " \\vFF00\\z000" + color + newStarter.name
                            + "\\vFF00\\z0000,\\nthe " + newStarter.primaryType.camelCase() + "-type Pokémon?";
                    spStrings.set(i + 1, newStarterDesc);
                    String altStarterDesc = "\\vFF00\\z000" + color + newStarter.name + "\\vFF00\\z0000, the "
                            + newStarter.primaryType.camelCase() + "-type Pokémon, is\\nin this Poké Ball!";
                    spStrings.set(i + 4, altStarterDesc);
                }
                setStrings(romEntry.getInt("StarterScreenTextOffset"), spStrings);


                try {
                    // Fix starter cries
                    byte[] starterPokemonOverlay = readOverlay(romEntry.getInt("StarterPokemonOvlNumber"));
                    String spCriesPrefix = Gen4Constants.starterCriesPrefix;
                    int offset = find(starterPokemonOverlay, spCriesPrefix);
                    if (offset > 0) {
                        offset += spCriesPrefix.length() / 2; // because it was a prefix
                        for (Pokemon newStarter: newStarters) {
                            writeLong(starterPokemonOverlay, offset, newStarter.number);
                            offset += 4;
                        }
                    }
                    writeOverlay(romEntry.getInt("StarterPokemonOvlNumber"), starterPokemonOverlay);
                } catch (IOException e) {
                    throw new RandomizerIOException(e);
                }
                return true;
            } else {
                return false;
            }
        } else {
            try {
                byte[] starterData = readOverlay(romEntry.getInt("StarterPokemonOvlNumber"));
                writeWord(starterData, romEntry.getInt("StarterPokemonOffset"), newStarters.get(0).number);
                writeWord(starterData, romEntry.getInt("StarterPokemonOffset") + 4, newStarters.get(1).number);
                writeWord(starterData, romEntry.getInt("StarterPokemonOffset") + 8, newStarters.get(2).number);

                if (romEntry.romType == Gen4Constants.Type_DP || romEntry.romType == Gen4Constants.Type_Plat) {
                    String starterPokemonGraphicsPrefix = romEntry.getString("StarterPokemonGraphicsPrefix");
                    int offset = find(starterData, starterPokemonGraphicsPrefix);
                    if (offset > 0) {

                        // The original subroutine for handling the starter graphics is optimized by the compiler to use
                        // a value as a pointer offset and then adding to that value to get the Pokemon's index.
                        // We will keep this logic, but in order to make place for an extra instruction that will let
                        // us set the Pokemon index to any Gen 4 value we want, we change the base address of the
                        // pointer that the offset is used for; this also requires some changes to the instructions
                        // that utilize this pointer.
                        offset += starterPokemonGraphicsPrefix.length() / 2;

                        // Move down a section of instructions to make place for an add instruction that modifies the
                        // pointer. A PC-relative load and a BL have to be slightly modified to point to the correct
                        // thing.
                        writeWord(starterData, offset+0xC, readWord(starterData, offset+0xA));
                        if (offset % 4 == 0) {
                            starterData[offset+0xC] = (byte)(starterData[offset+0xC] - 1);
                        }
                        writeWord(starterData, offset+0xA, readWord(starterData, offset+0x8));
                        starterData[offset+0xA] = (byte)(starterData[offset+0xA] - 1);
                        writeWord(starterData, offset+0x8, readWord(starterData, offset+0x6));
                        writeWord(starterData, offset+0x6, readWord(starterData, offset+0x4));
                        writeWord(starterData, offset+0x4, readWord(starterData, offset+0x2));
                        // This instruction normally uses the value in r0 (0x200) as an offset for an ldr that uses
                        // the pointer as its base address; we change this to not use an offset at all because we
                        // change the instruction before it to add that 0x200 to the base address.
                        writeWord(starterData, offset+0x2, 0x6828);
                        writeWord(starterData, offset, 0x182D);

                        offset += 0x16;
                        // Change another ldr to not use any offset since we changed the base address
                        writeWord(starterData, offset, 0x6828);

                        offset += 0xA;

                        // This is where we write the actual starter numbers, as two adds/subs

                        for (int i = 0; i < 3; i++) {
                            // The offset that we want to use for the pointer is 4, then 8, then 0xC.
                            // We take the difference of the Pokemon's index and the offset, because we want to add
                            // (or subtract) that to/from the offset to get the Pokemon's index later.
                            int starterDiff = newStarters.get(i).number - (4*(i+1));

                            // Prepare two "add r0, #0x0" instructions where we'll modify the immediate
                            int instr1 = 0x3200;
                            int instr2 = 0x3200;

                            if (starterDiff < 0) {
                                // Pokemon's index is below the offset, change to a sub instruction
                                instr1 |= 0x800;
                                starterDiff = Math.abs(starterDiff);
                            } else if (starterDiff > 255) {
                                // Pokemon's index is above (offset + 255), need to utilize the second add instruction
                                instr2 |= 0xFF;
                                starterDiff -= 255;
                            }

                            // Modify the first add instruction's immediate value
                            instr1 |= (starterDiff & 0xFF);

                            // Change the original offset that's loaded, then move an instruction up one step
                            // and insert our add instructions
                            starterData[offset] = (byte)(4*(i+1));
                            writeWord(starterData, offset+2, readWord(starterData, offset+4));
                            writeWord(starterData, offset+4, instr1);
                            writeWord(starterData, offset+8, instr2);

                            // Repeat for each starter
                            offset += 0xE;
                        }

                        // Change a loaded value to be 1 instead of 0x81 because we changed the pointer
                        starterData[offset] = 1;

                        // Also need to change one usage of the pointer we changed, in the inner function
                        String starterPokemonGraphicsPrefixInner = romEntry.getString("StarterPokemonGraphicsPrefixInner");
                        offset = find(starterData, starterPokemonGraphicsPrefixInner);

                        if (offset > 0) {
                            offset += starterPokemonGraphicsPrefixInner.length() / 2;
                            starterData[offset+1] = 0x68;
                        }
                    }
                }

                writeOverlay(romEntry.getInt("StarterPokemonOvlNumber"), starterData);
                // Patch DPPt-style rival scripts
                // these have a series of IfJump commands
                // following pokemon IDs
                // the jumps either go to trainer battles, or a HoF times
                // checker, or the StarterBattle command (Pt only)
                // the HoF times checker case is for the Fight Area or Survival
                // Area (depending on version).
                // the StarterBattle case is for Route 201 in Pt.
                int[] filesWithRivalScript = (romEntry.romType == Gen4Constants.Type_Plat) ? Gen4Constants.ptFilesWithRivalScript
                        : Gen4Constants.dpFilesWithRivalScript;
                byte[] magic = Gen4Constants.dpptRivalScriptMagic;
                NARCArchive scriptNARC = scriptNarc;
                for (int fileCheck : filesWithRivalScript) {
                    byte[] file = scriptNARC.files.get(fileCheck);
                    List<Integer> rivalOffsets = RomFunctions.search(file, magic);
                    if (rivalOffsets.size() > 0) {
                        for (int baseOffset : rivalOffsets) {
                            // found, check for trainer battle or HoF
                            // check at jump
                            int jumpLoc = baseOffset + magic.length;
                            int jumpTo = readLong(file, jumpLoc) + jumpLoc + 4;
                            // TODO find out what these constants are and remove
                            // them
                            if (readWord(file, jumpTo) != 0xE5 && readWord(file, jumpTo) != 0x28F
                                    && (readWord(file, jumpTo) != 0x125 || romEntry.romType != Gen4Constants.Type_Plat)) {
                                continue; // not a rival script
                            }
                            // Replace the two starter-words 387 and 390
                            writeWord(file, baseOffset + 0x8, newStarters.get(0).number);
                            writeWord(file, baseOffset + 0x15, newStarters.get(1).number);
                        }
                    }
                }
                // Tag battles with rival or friend
                // Have their own script magic
                // 2 for Lucas/Dawn (=4 occurrences), 1 or 2 for Barry
                byte[] tagBattleMagic = Gen4Constants.dpptTagBattleScriptMagic1;
                byte[] tagBattleMagic2 = Gen4Constants.dpptTagBattleScriptMagic2;
                int[] filesWithTagBattleScript = (romEntry.romType == Gen4Constants.Type_Plat) ? Gen4Constants.ptFilesWithTagScript
                        : Gen4Constants.dpFilesWithTagScript;
                for (int fileCheck : filesWithTagBattleScript) {
                    byte[] file = scriptNARC.files.get(fileCheck);
                    List<Integer> tbOffsets = RomFunctions.search(file, tagBattleMagic);
                    if (tbOffsets.size() > 0) {
                        for (int baseOffset : tbOffsets) {
                            // found, check for second part
                            int secondPartStart = baseOffset + tagBattleMagic.length + 2;
                            if (secondPartStart + tagBattleMagic2.length > file.length) {
                                continue; // match failed
                            }
                            boolean valid = true;
                            for (int spo = 0; spo < tagBattleMagic2.length; spo++) {
                                if (file[secondPartStart + spo] != tagBattleMagic2[spo]) {
                                    valid = false;
                                    break;
                                }
                            }
                            if (!valid) {
                                continue;
                            }
                            // Make sure the jump following the second
                            // part jumps to a <return> command
                            int jumpLoc = secondPartStart + tagBattleMagic2.length;
                            int jumpTo = readLong(file, jumpLoc) + jumpLoc + 4;
                            // TODO find out what this constant is and remove it
                            if (readWord(file, jumpTo) != 0x1B) {
                                continue; // not a tag battle script
                            }
                            // Replace the two starter-words
                            if (readWord(file, baseOffset + 0x21) == Species.turtwig) {
                                // first starter
                                writeWord(file, baseOffset + 0x21, newStarters.get(0).number);
                            } else {
                                // third starter
                                writeWord(file, baseOffset + 0x21, newStarters.get(2).number);
                            }
                            // second starter
                            writeWord(file, baseOffset + 0xE, newStarters.get(1).number);
                        }
                    }
                }
                // Fix starter script text
                // The starter picking screen
                List<String> spStrings = getStrings(romEntry.getInt("StarterScreenTextOffset"));
                // Get pokedex info
                List<String> pokedexSpeciesStrings = getStrings(romEntry.getInt("PokedexSpeciesTextOffset"));
                for (int i = 0; i < 3; i++) {
                    Pokemon newStarter = newStarters.get(i);
                    int color = (i == 0) ? 3 : i;
                    String newStarterDesc = "\\vFF00\\z000" + color + pokedexSpeciesStrings.get(newStarter.number)
                            + " " + newStarter.name + "\\vFF00\\z0000!\\nWill you take this Pokémon?";
                    spStrings.set(i + 1, newStarterDesc);
                }
                // rewrite starter picking screen
                setStrings(romEntry.getInt("StarterScreenTextOffset"), spStrings);
                if (romEntry.romType == Gen4Constants.Type_DP) {
                    // what rival says after we get the Pokemon
                    List<String> lakeStrings = getStrings(romEntry.getInt("StarterLocationTextOffset"));
                    lakeStrings
                            .set(Gen4Constants.dpStarterStringIndex,
                                    "\\v0103\\z0000: Fwaaah!\\nYour Pokémon totally rocked!\\pBut mine was way tougher\\nthan yours!\\p...They were other people’s\\nPokémon, though...\\pBut we had to use them...\\nThey won’t mind, will they?\\p");
                    setStrings(romEntry.getInt("StarterLocationTextOffset"), lakeStrings);
                } else {
                    // what rival says after we get the Pokemon
                    List<String> r201Strings = getStrings(romEntry.getInt("StarterLocationTextOffset"));
                    r201Strings.set(Gen4Constants.ptStarterStringIndex,
                            "\\v0103\\z0000\\z0000: Then, I choose you!\\nI’m picking this one!\\p");
                    setStrings(romEntry.getInt("StarterLocationTextOffset"), r201Strings);
                }
            } catch (IOException e) {
                throw new RandomizerIOException(e);
            }
            return true;
        }
    }

    @Override
    public boolean supportsStarterHeldItems() {
        return romEntry.romType == Gen4Constants.Type_DP || romEntry.romType == Gen4Constants.Type_Plat;
    }

    @Override
    public List<Integer> getStarterHeldItems() {
        int starterScriptNumber = romEntry.getInt("StarterPokemonScriptOffset");
        int starterHeldItemOffset = romEntry.getInt("StarterPokemonHeldItemOffset");
        byte[] file = scriptNarc.files.get(starterScriptNumber);
        int item = FileFunctions.read2ByteInt(file, starterHeldItemOffset);
        return Arrays.asList(item);
    }

    @Override
    public void setStarterHeldItems(List<Integer> items) {
        int starterScriptNumber = romEntry.getInt("StarterPokemonScriptOffset");
        int starterHeldItemOffset = romEntry.getInt("StarterPokemonHeldItemOffset");
        byte[] file = scriptNarc.files.get(starterScriptNumber);
        FileFunctions.write2ByteInt(file, starterHeldItemOffset, items.get(0));
    }

    @Override
    public List<Move> getMoves() {
        return Arrays.asList(moves);
    }

    @Override
    public List<EncounterSet> getEncounters(boolean useTimeOfDay) {
        if (!loadedWildMapNames) {
            loadWildMapNames();
        }

        try {
            if (romEntry.romType == Gen4Constants.Type_HGSS) {
                return getEncountersHGSS(useTimeOfDay);
            } else {
                return getEncountersDPPt(useTimeOfDay);
            }
        } catch (IOException ex) {
            throw new RandomizerIOException(ex);
        }
    }

    private List<EncounterSet> getEncountersDPPt(boolean useTimeOfDay) throws IOException {
        // Determine file to use
        String encountersFile = romEntry.getFile("WildPokemon");

        NARCArchive encounterData = readNARC(encountersFile);
        List<EncounterSet> encounters = new ArrayList<>();
        // Credit for
        // https://github.com/magical/pokemon-encounters/blob/master/nds/encounters-gen4-sinnoh.py
        // for the structure for this.
        int c = -1;
        for (byte[] b : encounterData.files) {
            c++;
            if (!wildMapNames.containsKey(c)) {
                wildMapNames.put(c, "? Unknown ?");
            }
            String mapName = wildMapNames.get(c);
            int grassRate = readLong(b, 0);
            if (grassRate != 0) {
                // up to 4
                List<Encounter> grassEncounters = readEncountersDPPt(b, 4, 12);
                EncounterSet grass = new EncounterSet();
                grass.displayName = mapName + " Grass/Cave";
                grass.encounters = grassEncounters;
                grass.rate = grassRate;
                grass.offset = c;
                encounters.add(grass);

                // Time of day replacements?
                if (useTimeOfDay) {
                    for (int i = 0; i < 4; i++) {
                        int pknum = readLong(b, 108 + 4 * i);
                        if (pknum >= 1 && pknum <= Gen4Constants.pokemonCount) {
                            Pokemon pk = pokes[pknum];
                            Encounter enc = new Encounter();
                            enc.level = grassEncounters.get(Gen4Constants.dpptAlternateSlots[i + 2]).level;
                            enc.pokemon = pk;
                            grassEncounters.add(enc);
                        }
                    }
                }
                // (if useTimeOfDay is off, just override them later)

                // Other conditional replacements (swarm, radar, GBA)
                EncounterSet conds = new EncounterSet();
                conds.displayName = mapName + " Swarm/Radar/GBA";
                conds.rate = grassRate;
                conds.offset = c;
                for (int i = 0; i < 20; i++) {
                    if (i >= 2 && i <= 5) {
                        // Time of day slot, handled already
                        continue;
                    }
                    int offs = 100 + i * 4 + (i >= 10 ? 24 : 0);
                    int pknum = readLong(b, offs);
                    if (pknum >= 1 && pknum <= Gen4Constants.pokemonCount) {
                        Pokemon pk = pokes[pknum];
                        Encounter enc = new Encounter();
                        enc.level = grassEncounters.get(Gen4Constants.dpptAlternateSlots[i]).level;
                        enc.pokemon = pk;
                        conds.encounters.add(enc);
                    }
                }
                if (conds.encounters.size() > 0) {
                    encounters.add(conds);
                }
            }

            // up to 204, 5 sets of "sea" encounters to go
            int offset = 204;
            for (int i = 0; i < 5; i++) {
                int rate = readLong(b, offset);
                offset += 4;
                List<Encounter> encountersHere = readSeaEncountersDPPt(b, offset, 5);
                offset += 40;
                if (rate == 0 || i == 1) {
                    continue;
                }
                EncounterSet other = new EncounterSet();
                other.displayName = mapName + " " + Gen4Constants.dpptWaterSlotSetNames[i];
                other.offset = c;
                other.encounters = encountersHere;
                other.rate = rate;
                encounters.add(other);
            }
        }

        // Now do the extra encounters (Feebas tiles, honey trees, Great Marsh rotating Pokemon, etc.)
        String extraEncountersFile = romEntry.getFile("ExtraEncounters");
        NARCArchive extraEncounterData = readNARC(extraEncountersFile);

        // Feebas tiles
        byte[] feebasData = extraEncounterData.files.get(0);
        EncounterSet feebasEncounters = readExtraEncountersDPPt(feebasData, 0, 1);
        byte[] encounterOverlay = readOverlay(romEntry.getInt("EncounterOvlNumber"));
        int offset = find(encounterOverlay, Gen4Constants.feebasLevelPrefixDPPt);
        if (offset > 0) {
            offset += Gen4Constants.feebasLevelPrefixDPPt.length() / 2; // because it was a prefix
            for (Encounter enc : feebasEncounters.encounters) {
                enc.maxLevel = encounterOverlay[offset];
                enc.level = encounterOverlay[offset + 4];
            }
        }
        feebasEncounters.displayName = "Mt. Coronet Feebas Tiles";
        encounters.add(feebasEncounters);

        // Honey trees
        int[] honeyTreeOffsets = romEntry.arrayEntries.get("HoneyTreeOffsets");
        for (int i = 0; i < honeyTreeOffsets.length; i++) {
            byte[] honeyTreeData = extraEncounterData.files.get(honeyTreeOffsets[i]);
            EncounterSet honeyTreeEncounters = readExtraEncountersDPPt(honeyTreeData, 0, 6);
            offset = find(encounterOverlay, Gen4Constants.honeyTreeLevelPrefixDPPt);
            if (offset > 0) {
                offset += Gen4Constants.honeyTreeLevelPrefixDPPt.length() / 2; // because it was a prefix

                // To make different min levels work, we rewrite some assembly code in
                // setEncountersDPPt, which has the side effect of making reading the min
                // level easier. In case the original code is still there, just hardcode
                // the min level used in the vanilla game, since extracting it is hard.
                byte level;
                if (encounterOverlay[offset + 46] == 0x0B && encounterOverlay[offset + 47] == 0x2E) {
                    level = 5;
                } else {
                    level = encounterOverlay[offset + 46];
                }
                for (Encounter enc : honeyTreeEncounters.encounters) {
                    enc.maxLevel = encounterOverlay[offset + 102];
                    enc.level = level;
                }
            }
            honeyTreeEncounters.displayName = "Honey Tree Group " + (i + 1);
            encounters.add(honeyTreeEncounters);
        }

        // Trophy Garden rotating Pokemon (Mr. Backlot)
        byte[] trophyGardenData = extraEncounterData.files.get(8);
        EncounterSet trophyGardenEncounters = readExtraEncountersDPPt(trophyGardenData, 0, 16);

        // Trophy Garden rotating Pokemon get their levels from the regular Trophy Garden grass encounters,
        // indices 6 and 7. To make the logs nice, read in these encounters for this area and set the level
        // and maxLevel for the rotating encounters appropriately.
        int trophyGardenGrassEncounterIndex = Gen4Constants.getTrophyGardenGrassEncounterIndex(romEntry.romType);
        EncounterSet trophyGardenGrassEncounterSet = encounters.get(trophyGardenGrassEncounterIndex);
        int level1 = trophyGardenGrassEncounterSet.encounters.get(6).level;
        int level2 = trophyGardenGrassEncounterSet.encounters.get(7).level;
        for (Encounter enc : trophyGardenEncounters.encounters) {
            enc.level = Math.min(level1, level2);
            if (level1 != level2) {
                enc.maxLevel = Math.max(level1, level2);
            }
        }
        trophyGardenEncounters.displayName = "Trophy Garden Rotating Pokemon (via Mr. Backlot)";
        encounters.add(trophyGardenEncounters);

        // Great Marsh rotating Pokemon
        int[] greatMarshOffsets = new int[]{9, 10};
        for (int i = 0; i < greatMarshOffsets.length; i++) {
            byte[] greatMarshData = extraEncounterData.files.get(greatMarshOffsets[i]);
            EncounterSet greatMarshEncounters = readExtraEncountersDPPt(greatMarshData, 0, 32);

            // Great Marsh rotating Pokemon get their levels from the regular Great Marsh grass encounters,
            // indices 6 and 7. To make the logs nice, read in these encounters for all areas and set the
            // level and maxLevel for the rotating encounters appropriately.
            int level = 100;
            int maxLevel = 0;
            List<Integer> marshGrassEncounterIndices = Gen4Constants.getMarshGrassEncounterIndices(romEntry.romType);
            for (int j = 0; j < marshGrassEncounterIndices.size(); j++) {
                EncounterSet marshGrassEncounterSet = encounters.get(marshGrassEncounterIndices.get(j));
                int currentLevel = marshGrassEncounterSet.encounters.get(6).level;
                if (currentLevel < level) {
                    level = currentLevel;
                }
                if (currentLevel > maxLevel) {
                    maxLevel = currentLevel;
                }
                currentLevel = marshGrassEncounterSet.encounters.get(7).level;
                if (currentLevel < level) {
                    level = currentLevel;
                }
                if (currentLevel > maxLevel) {
                    maxLevel = currentLevel;
                }
            }
            for (Encounter enc : greatMarshEncounters.encounters) {
                enc.level = level;
                enc.maxLevel = maxLevel;
            }
            String pokedexStatus = i == 0 ? "(Post-National Dex)" : "(Pre-National Dex)";
            greatMarshEncounters.displayName = "Great Marsh Rotating Pokemon " + pokedexStatus;
            encounters.add(greatMarshEncounters);
        }
        return encounters;
    }

    private List<Encounter> readEncountersDPPt(byte[] data, int offset, int amount) {
        List<Encounter> encounters = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            int level = readLong(data, offset + i * 8);
            int pokemon = readLong(data, offset + 4 + i * 8);
            Encounter enc = new Encounter();
            enc.level = level;
            enc.pokemon = pokes[pokemon];
            encounters.add(enc);
        }
        return encounters;
    }

    private List<Encounter> readSeaEncountersDPPt(byte[] data, int offset, int amount) {
        List<Encounter> encounters = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            int level = readLong(data, offset + i * 8);
            int pokemon = readLong(data, offset + 4 + i * 8);
            Encounter enc = new Encounter();
            enc.level = level >> 8;
            enc.maxLevel = level & 0xFF;
            enc.pokemon = pokes[pokemon];
            encounters.add(enc);
        }
        return encounters;
    }

    private EncounterSet readExtraEncountersDPPt(byte[] data, int offset, int amount) {
        EncounterSet es = new EncounterSet();
        es.rate = 1;
        for (int i = 0; i < amount; i++) {
            int pokemon = readLong(data, offset + i * 4);
            Encounter e = new Encounter();
            e.level = 1;
            e.pokemon = pokes[pokemon];
            es.encounters.add(e);
        }
        return es;
    }

    private List<EncounterSet> getEncountersHGSS(boolean useTimeOfDay) throws IOException {
        String encountersFile = romEntry.getFile("WildPokemon");
        NARCArchive encounterData = readNARC(encountersFile);
        List<EncounterSet> encounters = new ArrayList<>();
        // Credit for
        // https://github.com/magical/pokemon-encounters/blob/master/nds/encounters-gen4-johto.py
        // for the structure for this.
        int[] amounts = new int[] { 0, 5, 2, 5, 5, 5 };
        int c = -1;
        for (byte[] b : encounterData.files) {
            c++;
            if (!wildMapNames.containsKey(c)) {
                wildMapNames.put(c, "? Unknown ?");
            }
            String mapName = wildMapNames.get(c);
            int[] rates = new int[6];
            rates[0] = b[0] & 0xFF;
            rates[1] = b[1] & 0xFF;
            rates[2] = b[2] & 0xFF;
            rates[3] = b[3] & 0xFF;
            rates[4] = b[4] & 0xFF;
            rates[5] = b[5] & 0xFF;
            // Up to 8 after the rates
            // Grass has to be handled on its own because the levels
            // are reused for every time of day
            int[] grassLevels = new int[12];
            for (int i = 0; i < 12; i++) {
                grassLevels[i] = b[8 + i] & 0xFF;
            }
            // Up to 20 now (12 for levels)
            Pokemon[][] grassPokes = new Pokemon[3][12];
            grassPokes[0] = readPokemonHGSS(b, 20, 12);
            grassPokes[1] = readPokemonHGSS(b, 44, 12);
            grassPokes[2] = readPokemonHGSS(b, 68, 12);
            // Up to 92 now (12*2*3 for pokemon)
            if (rates[0] != 0) {
                if (!useTimeOfDay) {
                    // Just write "day" encounters
                    List<Encounter> grassEncounters = stitchEncsToLevels(grassPokes[1], grassLevels);
                    EncounterSet grass = new EncounterSet();
                    grass.encounters = grassEncounters;
                    grass.rate = rates[0];
                    grass.displayName = mapName + " Grass/Cave";
                    encounters.add(grass);
                } else {
                    for (int i = 0; i < 3; i++) {
                        EncounterSet grass = new EncounterSet();
                        grass.encounters = stitchEncsToLevels(grassPokes[i], grassLevels);
                        grass.rate = rates[0];
                        grass.displayName = mapName + " " + Gen4Constants.hgssTimeOfDayNames[i] + " Grass/Cave";
                        encounters.add(grass);
                    }
                }
            }

            // Hoenn/Sinnoh Radio
            EncounterSet radio = readOptionalEncountersHGSS(b, 92, 4);
            radio.displayName = mapName + " Hoenn/Sinnoh Radio";
            if (radio.encounters.size() > 0) {
                encounters.add(radio);
            }

            // Up to 100 now... 2*2*2 for radio pokemon
            // Time to handle Surfing, Rock Smash, Rods
            int offset = 100;
            for (int i = 1; i < 6; i++) {
                List<Encounter> encountersHere = readSeaEncountersHGSS(b, offset, amounts[i]);
                offset += 4 * amounts[i];
                if (rates[i] != 0) {
                    // Valid area.
                    EncounterSet other = new EncounterSet();
                    other.encounters = encountersHere;
                    other.displayName = mapName + " " + Gen4Constants.hgssNonGrassSetNames[i];
                    other.rate = rates[i];
                    encounters.add(other);
                }
            }

            // Swarms
            EncounterSet swarms = readOptionalEncountersHGSS(b, offset, 2);
            swarms.displayName = mapName + " Swarms";
            if (swarms.encounters.size() > 0) {
                encounters.add(swarms);
            }
            EncounterSet nightFishingReplacement = readOptionalEncountersHGSS(b, offset + 4, 1);
            nightFishingReplacement.displayName = mapName + " Night Fishing Replacement";
            if (nightFishingReplacement.encounters.size() > 0) {
                encounters.add(nightFishingReplacement);
            }
            EncounterSet fishingSwarms = readOptionalEncountersHGSS(b, offset + 6, 1);
            fishingSwarms.displayName = mapName + " Fishing Swarm";
            if (fishingSwarms.encounters.size() > 0) {
                encounters.add(fishingSwarms);
            }
        }

        // Headbutt Encounters
        String headbuttEncountersFile = romEntry.getFile("HeadbuttPokemon");
        NARCArchive headbuttEncounterData = readNARC(headbuttEncountersFile);
        c = -1;
        for (byte[] b : headbuttEncounterData.files) {
            c++;

            // Each headbutt encounter file starts with four bytes, which I believe are used
            // to indicate the number of "normal" and "special" trees that are available in
            // this area. For areas that don't contain any headbutt encounters, these four
            // bytes constitute the only four bytes in the file, so we can stop looking at
            // this file in this case.
            if (b.length == 4) {
                continue;
            }

            String mapName = headbuttMapNames.get(c);
            EncounterSet headbuttEncounters = readHeadbuttEncountersHGSS(b, 4, 18);
            headbuttEncounters.displayName = mapName + " Headbutt";

            // Map 24 is an unused version of Route 16, but it still has valid headbutt encounter data.
            // Avoid adding it to the list of encounters to prevent confusion.
            if (headbuttEncounters.encounters.size() > 0 && c != 24) {
                encounters.add(headbuttEncounters);
            }
        }

        // Bug Catching Contest Encounters
        String bccEncountersFile = romEntry.getFile("BCCWilds");
        byte[] bccEncountersData = readFile(bccEncountersFile);
        EncounterSet bccEncountersPreNationalDex = readBCCEncountersHGSS(bccEncountersData, 0, 10);
        bccEncountersPreNationalDex.displayName = "Bug Catching Contest (Pre-National Dex)";
        if (bccEncountersPreNationalDex.encounters.size() > 0) {
            encounters.add(bccEncountersPreNationalDex);
        }
        EncounterSet bccEncountersPostNationalDexTues = readBCCEncountersHGSS(bccEncountersData, 80, 10);
        bccEncountersPostNationalDexTues.displayName = "Bug Catching Contest (Post-National Dex, Tuesdays)";
        if (bccEncountersPostNationalDexTues.encounters.size() > 0) {
            encounters.add(bccEncountersPostNationalDexTues);
        }
        EncounterSet bccEncountersPostNationalDexThurs = readBCCEncountersHGSS(bccEncountersData, 160, 10);
        bccEncountersPostNationalDexThurs.displayName = "Bug Catching Contest (Post-National Dex, Thursdays)";
        if (bccEncountersPostNationalDexThurs.encounters.size() > 0) {
            encounters.add(bccEncountersPostNationalDexThurs);
        }
        EncounterSet bccEncountersPostNationalDexSat = readBCCEncountersHGSS(bccEncountersData, 240, 10);
        bccEncountersPostNationalDexSat.displayName = "Bug Catching Contest (Post-National Dex, Saturdays)";
        if (bccEncountersPostNationalDexSat.encounters.size() > 0) {
            encounters.add(bccEncountersPostNationalDexSat);
        }
        return encounters;
    }

    private EncounterSet readOptionalEncountersHGSS(byte[] data, int offset, int amount) {
        EncounterSet es = new EncounterSet();
        es.rate = 1;
        for (int i = 0; i < amount; i++) {
            int pokemon = readWord(data, offset + i * 2);
            if (pokemon != 0) {
                Encounter e = new Encounter();
                e.level = 1;
                e.pokemon = pokes[pokemon];
                es.encounters.add(e);
            }
        }
        return es;
    }

    private Pokemon[] readPokemonHGSS(byte[] data, int offset, int amount) {
        Pokemon[] pokesHere = new Pokemon[amount];
        for (int i = 0; i < amount; i++) {
            pokesHere[i] = pokes[readWord(data, offset + i * 2)];
        }
        return pokesHere;
    }

    private List<Encounter> readSeaEncountersHGSS(byte[] data, int offset, int amount) {
        List<Encounter> encounters = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            int level = readWord(data, offset + i * 4);
            int pokemon = readWord(data, offset + 2 + i * 4);
            Encounter enc = new Encounter();
            enc.level = level & 0xFF;
            enc.maxLevel = level >> 8;
            enc.pokemon = pokes[pokemon];
            encounters.add(enc);
        }
        return encounters;
    }

    private EncounterSet readHeadbuttEncountersHGSS(byte[] data, int offset, int amount) {
        EncounterSet es = new EncounterSet();
        es.rate = 1;
        for (int i = 0; i < amount; i++) {
            int pokemon = readWord(data, offset + i * 4);
            if (pokemon != 0) {
                Encounter enc = new Encounter();
                enc.level = data[offset + 2 + i * 4];
                enc.maxLevel = data[offset + 3 + i * 4];
                enc.pokemon = pokes[pokemon];
                es.encounters.add(enc);
            }
        }
        return es;
    }

    private EncounterSet readBCCEncountersHGSS(byte[] data, int offset, int amount) {
        EncounterSet es = new EncounterSet();
        es.rate = 1;
        for (int i = 0; i < amount; i++) {
            int pokemon = readWord(data, offset + i * 8);
            if (pokemon != 0) {
                Encounter enc = new Encounter();
                enc.level = data[offset + 2 + i * 8];
                enc.maxLevel = data[offset + 3 + i * 8];
                enc.pokemon = pokes[pokemon];
                es.encounters.add(enc);
            }
        }
        return es;
    }

    private List<EncounterSet> readTimeBasedRodEncountersHGSS(byte[] data, int offset, Pokemon replacement, int replacementIndex) {
        List<EncounterSet> encounters = new ArrayList<>();
        List<Encounter> rodMorningDayEncounters = readSeaEncountersHGSS(data, offset, 5);
        EncounterSet rodMorningDay = new EncounterSet();
        rodMorningDay.encounters = rodMorningDayEncounters;
        encounters.add(rodMorningDay);

        List<Encounter> rodNightEncounters = new ArrayList<>(rodMorningDayEncounters);
        Encounter replacedEncounter = cloneEncounterAndReplacePokemon(rodMorningDayEncounters.get(replacementIndex), replacement);
        rodNightEncounters.set(replacementIndex, replacedEncounter);
        EncounterSet rodNight = new EncounterSet();
        rodNight.encounters = rodNightEncounters;
        encounters.add(rodNight);
        return encounters;
    }

    private Encounter cloneEncounterAndReplacePokemon(Encounter enc, Pokemon pkmn) {
        Encounter clone = new Encounter();
        clone.level = enc.level;
        clone.maxLevel = enc.maxLevel;
        clone.pokemon = pkmn;
        return clone;
    }

    @Override
    public void setEncounters(boolean useTimeOfDay, List<EncounterSet> encounters) {
        try {
            if (romEntry.romType == Gen4Constants.Type_HGSS) {
                setEncountersHGSS(useTimeOfDay, encounters);
                updatePokedexAreaDataHGSS(encounters);
            } else {
                setEncountersDPPt(useTimeOfDay, encounters);
                updatePokedexAreaDataDPPt(encounters);
            }
        } catch (IOException ex) {
            throw new RandomizerIOException(ex);
        }
    }

    private void setEncountersDPPt(boolean useTimeOfDay, List<EncounterSet> encounterList) throws IOException {
        // Determine file to use
        String encountersFile = romEntry.getFile("WildPokemon");
        NARCArchive encounterData = readNARC(encountersFile);
        Iterator<EncounterSet> encounters = encounterList.iterator();
        // Credit for
        // https://github.com/magical/pokemon-encounters/blob/master/nds/encounters-gen4-sinnoh.py
        // for the structure for this.
        for (byte[] b : encounterData.files) {
            int grassRate = readLong(b, 0);
            if (grassRate != 0) {
                // grass encounters are a-go
                EncounterSet grass = encounters.next();
                writeEncountersDPPt(b, 4, grass.encounters, 12);

                // Time of day encounters?
                int todEncounterSlot = 12;
                for (int i = 0; i < 4; i++) {
                    int pknum = readLong(b, 108 + 4 * i);
                    if (pknum >= 1 && pknum <= Gen4Constants.pokemonCount) {
                        // Valid time of day slot
                        if (useTimeOfDay) {
                            // Get custom randomized encounter
                            Pokemon pk = grass.encounters.get(todEncounterSlot++).pokemon;
                            writeLong(b, 108 + 4 * i, pk.number);
                        } else {
                            // Copy the original slot's randomized encounter
                            Pokemon pk = grass.encounters.get(Gen4Constants.dpptAlternateSlots[i + 2]).pokemon;
                            writeLong(b, 108 + 4 * i, pk.number);
                        }
                    }
                }

                // Other conditional encounters?
                Iterator<Encounter> condEncounters = null;
                for (int i = 0; i < 20; i++) {
                    if (i >= 2 && i <= 5) {
                        // Time of day slot, handled already
                        continue;
                    }
                    int offs = 100 + i * 4 + (i >= 10 ? 24 : 0);
                    int pknum = readLong(b, offs);
                    if (pknum >= 1 && pknum <= Gen4Constants.pokemonCount) {
                        // This slot is used, grab a replacement.
                        if (condEncounters == null) {
                            // Fetch the set of conditional encounters for this
                            // area now that we know it's necessary and exists.
                            condEncounters = encounters.next().encounters.iterator();
                        }
                        Pokemon pk = condEncounters.next().pokemon;
                        writeLong(b, offs, pk.number);
                    }
                }
            }
            // up to 204, 5 special ones to go
            // This is for surf, filler, old rod, good rod, super rod
            // so we skip index 1 (filler)
            int offset = 204;
            for (int i = 0; i < 5; i++) {
                int rate = readLong(b, offset);
                offset += 4;
                if (rate == 0 || i == 1) {
                    offset += 40;
                    continue;
                }

                EncounterSet other = encounters.next();
                writeSeaEncountersDPPt(b, offset, other.encounters);
                offset += 40;
            }
        }

        // Save
        writeNARC(encountersFile, encounterData);

        // Now do the extra encounters (Feebas tiles, honey trees, Great Marsh rotating Pokemon, etc.)
        String extraEncountersFile = romEntry.getFile("ExtraEncounters");
        NARCArchive extraEncounterData = readNARC(extraEncountersFile);

        // Feebas tiles
        byte[] feebasData = extraEncounterData.files.get(0);
        EncounterSet feebasEncounters = encounters.next();
        byte[] encounterOverlay = readOverlay(romEntry.getInt("EncounterOvlNumber"));
        int offset = find(encounterOverlay, Gen4Constants.feebasLevelPrefixDPPt);
        if (offset > 0) {
            offset += Gen4Constants.feebasLevelPrefixDPPt.length() / 2; // because it was a prefix
            encounterOverlay[offset] = (byte) feebasEncounters.encounters.get(0).maxLevel;
            encounterOverlay[offset + 4] = (byte) feebasEncounters.encounters.get(0).level;
        }
        writeExtraEncountersDPPt(feebasData, 0, feebasEncounters.encounters);

        // Honey trees
        int[] honeyTreeOffsets = romEntry.arrayEntries.get("HoneyTreeOffsets");
        for (int i = 0; i < honeyTreeOffsets.length; i++) {
            byte[] honeyTreeData = extraEncounterData.files.get(honeyTreeOffsets[i]);
            EncounterSet honeyTreeEncounters = encounters.next();
            offset = find(encounterOverlay, Gen4Constants.honeyTreeLevelPrefixDPPt);
            if (offset > 0) {
                offset += Gen4Constants.honeyTreeLevelPrefixDPPt.length() / 2; // because it was a prefix
                int level = honeyTreeEncounters.encounters.get(0).level;
                int maxLevel = honeyTreeEncounters.encounters.get(0).maxLevel;

                // The original code makes it impossible for certain min levels
                // from being used in the assembly, but there's also a hardcoded
                // check for the original level range that we don't want. So we
                // can use that space to just do "mov r0, level", nop out the rest
                // of the check, then change "mov r0, r6, #5" to "mov r0, r0, r6".
                encounterOverlay[offset + 46] = (byte) level;
                encounterOverlay[offset + 47] = 0x20;
                encounterOverlay[offset + 48] = 0x00;
                encounterOverlay[offset + 49] = 0x00;
                encounterOverlay[offset + 50] = 0x00;
                encounterOverlay[offset + 51] = 0x00;
                encounterOverlay[offset + 52] = 0x00;
                encounterOverlay[offset + 53] = 0x00;
                encounterOverlay[offset + 54] = (byte) 0x80;
                encounterOverlay[offset + 55] = 0x19;

                encounterOverlay[offset + 102] = (byte) maxLevel;

                // In the above comment, r6 is a random number between 0 and
                // (maxLevel - level). To calculate this number, the game rolls
                // a random number between 0 and 0xFFFF and then divides it by
                // 0x1746; this produces values between 0 and 10, the original
                // level range. We need to replace the 0x1746 with our own
                // constant that has the same effect.
                int newRange = maxLevel - level;
                int divisor = (0xFFFF / (newRange + 1)) + 1;
                FileFunctions.writeFullInt(encounterOverlay, offset + 148, divisor);
            }
            writeExtraEncountersDPPt(honeyTreeData, 0, honeyTreeEncounters.encounters);
        }

        // Trophy Garden rotating Pokemon (Mr. Backlot)
        byte[] trophyGardenData = extraEncounterData.files.get(8);
        EncounterSet trophyGardenEncounters = encounters.next();

        // The game will softlock if all the Pokemon here are the same species. As an
        // emergency mitigation, just randomly pick a different species in case this
        // happens. This is very unlikely to happen in practice, even with very
        // restrictive settings, so it should be okay that we're breaking logic here.
        while (trophyGardenEncounters.encounters.stream().distinct().count() == 1) {
            trophyGardenEncounters.encounters.get(0).pokemon = randomPokemon();
        }
        writeExtraEncountersDPPt(trophyGardenData, 0, trophyGardenEncounters.encounters);

        // Great Marsh rotating Pokemon
        int[] greatMarshOffsets = new int[]{9, 10};
        for (int i = 0; i < greatMarshOffsets.length; i++) {
            byte[] greatMarshData = extraEncounterData.files.get(greatMarshOffsets[i]);
            EncounterSet greatMarshEncounters = encounters.next();
            writeExtraEncountersDPPt(greatMarshData, 0, greatMarshEncounters.encounters);
        }

        // Save
        writeOverlay(romEntry.getInt("EncounterOvlNumber"), encounterOverlay);
        writeNARC(extraEncountersFile, extraEncounterData);

    }

    private void writeEncountersDPPt(byte[] data, int offset, List<Encounter> encounters, int enclength) {
        for (int i = 0; i < enclength; i++) {
            Encounter enc = encounters.get(i);
            writeLong(data, offset + i * 8, enc.level);
            writeLong(data, offset + i * 8 + 4, enc.pokemon.number);
        }
    }

    private void writeSeaEncountersDPPt(byte[] data, int offset, List<Encounter> encounters) {
        int enclength = encounters.size();
        for (int i = 0; i < enclength; i++) {
            Encounter enc = encounters.get(i);
            writeLong(data, offset + i * 8, (enc.level << 8) + enc.maxLevel);
            writeLong(data, offset + i * 8 + 4, enc.pokemon.number);
        }
    }

    private void writeExtraEncountersDPPt(byte[] data, int offset, List<Encounter> encounters) {
        int enclength = encounters.size();
        for (int i = 0; i < enclength; i++) {
            Encounter enc = encounters.get(i);
            writeLong(data, offset + i * 4, enc.pokemon.number);
        }
    }

    private void setEncountersHGSS(boolean useTimeOfDay, List<EncounterSet> encounterList) throws IOException {
        String encountersFile = romEntry.getFile("WildPokemon");
        NARCArchive encounterData = readNARC(encountersFile);
        Iterator<EncounterSet> encounters = encounterList.iterator();
        // Credit for
        // https://github.com/magical/pokemon-encounters/blob/master/nds/encounters-gen4-johto.py
        // for the structure for this.
        int[] amounts = new int[] { 0, 5, 2, 5, 5, 5 };
        for (byte[] b : encounterData.files) {
            int[] rates = new int[6];
            rates[0] = b[0] & 0xFF;
            rates[1] = b[1] & 0xFF;
            rates[2] = b[2] & 0xFF;
            rates[3] = b[3] & 0xFF;
            rates[4] = b[4] & 0xFF;
            rates[5] = b[5] & 0xFF;
            // Up to 20 after the rates & levels
            // Grass has to be handled on its own because the levels
            // are reused for every time of day
            if (rates[0] != 0) {
                if (!useTimeOfDay) {
                    // Get a single set of encounters...
                    // Write the encounters we get 3x for morning, day, night
                    EncounterSet grass = encounters.next();
                    writeGrassEncounterLevelsHGSS(b, 8, grass.encounters);
                    writePokemonHGSS(b, 20, grass.encounters);
                    writePokemonHGSS(b, 44, grass.encounters);
                    writePokemonHGSS(b, 68, grass.encounters);
                } else {
                    EncounterSet grass = encounters.next();
                    writeGrassEncounterLevelsHGSS(b, 8, grass.encounters);
                    writePokemonHGSS(b, 20, grass.encounters);
                    for (int i = 1; i < 3; i++) {
                        grass = encounters.next();
                        writePokemonHGSS(b, 20 + i * 24, grass.encounters);
                    }
                }
            }

            // Write radio pokemon
            writeOptionalEncountersHGSS(b, 92, 4, encounters);

            // Up to 100 now... 2*2*2 for radio pokemon
            // Write surf, rock smash, and rods
            int offset = 100;
            for (int i = 1; i < 6; i++) {
                if (rates[i] != 0) {
                    // Valid area.
                    EncounterSet other = encounters.next();
                    writeSeaEncountersHGSS(b, offset, other.encounters);
                }
                offset += 4 * amounts[i];
            }

            // Write swarm pokemon
            writeOptionalEncountersHGSS(b, offset, 2, encounters);
            writeOptionalEncountersHGSS(b, offset + 4, 1, encounters);
            writeOptionalEncountersHGSS(b, offset + 6, 1, encounters);
        }

        // Save
        writeNARC(encountersFile, encounterData);

        // Write Headbutt encounters
        String headbuttEncountersFile = romEntry.getFile("HeadbuttPokemon");
        NARCArchive headbuttEncounterData = readNARC(headbuttEncountersFile);
        int c = -1;
        for (byte[] b : headbuttEncounterData.files) {
            c++;

            // In getEncountersHGSS, we ignored maps with no headbutt encounter data,
            // and we also ignored map 24 for being unused. We need to ignore them
            // here as well to keep encounters.next() in sync with the correct file.
            if (b.length == 4 || c == 24) {
                continue;
            }

            EncounterSet headbutt = encounters.next();
            writeHeadbuttEncountersHGSS(b, 4, headbutt.encounters);
        }

        // Save
        writeNARC(headbuttEncountersFile, headbuttEncounterData);

        // Write Bug Catching Contest encounters
        String bccEncountersFile = romEntry.getFile("BCCWilds");
        byte[] bccEncountersData = readFile(bccEncountersFile);
        EncounterSet bccEncountersPreNationalDex = encounters.next();
        writeBCCEncountersHGSS(bccEncountersData, 0, bccEncountersPreNationalDex.encounters);
        EncounterSet bccEncountersPostNationalDexTues = encounters.next();
        writeBCCEncountersHGSS(bccEncountersData, 80, bccEncountersPostNationalDexTues.encounters);
        EncounterSet bccEncountersPostNationalDexThurs = encounters.next();
        writeBCCEncountersHGSS(bccEncountersData, 160, bccEncountersPostNationalDexThurs.encounters);
        EncounterSet bccEncountersPostNationalDexSat = encounters.next();
        writeBCCEncountersHGSS(bccEncountersData, 240, bccEncountersPostNationalDexSat.encounters);

        // Save
        writeFile(bccEncountersFile, bccEncountersData);
    }

    private void writeOptionalEncountersHGSS(byte[] data, int offset, int amount, Iterator<EncounterSet> encounters) {
        Iterator<Encounter> eIter = null;
        for (int i = 0; i < amount; i++) {
            int origPokemon = readWord(data, offset + i * 2);
            if (origPokemon != 0) {
                // Need an encounter set, yes.
                if (eIter == null) {
                    eIter = encounters.next().encounters.iterator();
                }
                Encounter here = eIter.next();
                writeWord(data, offset + i * 2, here.pokemon.number);
            }
        }

    }

    private void writeGrassEncounterLevelsHGSS(byte[] data, int offset, List<Encounter> encounters) {
        int enclength = encounters.size();
        for (int i = 0; i < enclength; i++) {
            data[offset + i] = (byte) encounters.get(i).level;
        }

    }

    private void writePokemonHGSS(byte[] data, int offset, List<Encounter> encounters) {
        int enclength = encounters.size();
        for (int i = 0; i < enclength; i++) {
            writeWord(data, offset + i * 2, encounters.get(i).pokemon.number);
        }

    }

    private void writeSeaEncountersHGSS(byte[] data, int offset, List<Encounter> encounters) {
        int enclength = encounters.size();
        for (int i = 0; i < enclength; i++) {
            Encounter enc = encounters.get(i);
            data[offset + i * 4] = (byte) enc.level;
            data[offset + i * 4 + 1] = (byte) enc.maxLevel;
            writeWord(data, offset + i * 4 + 2, enc.pokemon.number);
        }

    }

    private void writeHeadbuttEncountersHGSS(byte[] data, int offset, List<Encounter> encounters) {
        int enclength = encounters.size();
        for (int i = 0; i < enclength; i++) {
            Encounter enc = encounters.get(i);
            writeWord(data, offset + i * 4, enc.pokemon.number);
            data[offset + 2 + i * 4] = (byte) enc.level;
            data[offset + 3 + i * 4] = (byte) enc.maxLevel;
        }
    }

    private void writeBCCEncountersHGSS(byte[] data, int offset, List<Encounter> encounters) {
        int enclength = encounters.size();
        for (int i = 0; i < enclength; i++) {
            Encounter enc = encounters.get(i);
            writeWord(data, offset + i * 8, enc.pokemon.number);
            data[offset + 2 + i * 8] = (byte) enc.level;
            data[offset + 3 + i * 8] = (byte) enc.maxLevel;
        }
    }

    private List<Encounter> stitchEncsToLevels(Pokemon[] pokemon, int[] levels) {
        List<Encounter> encounters = new ArrayList<>();
        for (int i = 0; i < pokemon.length; i++) {
            Encounter enc = new Encounter();
            enc.level = levels[i];
            enc.pokemon = pokemon[i];
            encounters.add(enc);
        }
        return encounters;
    }

    private void loadWildMapNames() {
        try {
            wildMapNames = new HashMap<>();
            headbuttMapNames = new HashMap<>();
            byte[] internalNames = this.readFile(romEntry.getFile("MapTableFile"));
            int numMapHeaders = internalNames.length / 16;
            int baseMHOffset = romEntry.getInt("MapTableARM9Offset");
            List<String> allMapNames = getStrings(romEntry.getInt("MapNamesTextOffset"));
            int mapNameIndexSize = romEntry.getInt("MapTableNameIndexSize");
            for (int map = 0; map < numMapHeaders; map++) {
                int baseOffset = baseMHOffset + map * 24;
                int mapNameIndex = (mapNameIndexSize == 2) ? readWord(arm9, baseOffset + 18)
                        : (arm9[baseOffset + 18] & 0xFF);
                String mapName = allMapNames.get(mapNameIndex);
                if (romEntry.romType == Gen4Constants.Type_HGSS) {
                    int wildSet = arm9[baseOffset] & 0xFF;
                    if (wildSet != 255) {
                        wildMapNames.put(wildSet, mapName);
                    }
                    headbuttMapNames.put(map, mapName);
                } else {
                    int wildSet = readWord(arm9, baseOffset + 14);
                    if (wildSet != 65535) {
                        wildMapNames.put(wildSet, mapName);
                    }
                }
            }
            loadedWildMapNames = true;
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

    }

    private void updatePokedexAreaDataDPPt(List<EncounterSet> encounters) throws IOException {
        String encountersFile = romEntry.getFile("WildPokemon");
        NARCArchive encounterData = readNARC(encountersFile);

        // Initialize empty area data
        Set[][] dungeonAreaData = new Set[Gen4Constants.pokemonCount + 1][3];
        Set[] dungeonSpecialPreNationalData = new Set[Gen4Constants.pokemonCount + 1];
        Set[] dungeonSpecialPostNationalData = new Set[Gen4Constants.pokemonCount + 1];
        Set[][] overworldAreaData = new Set[Gen4Constants.pokemonCount + 1][3];
        Set[] overworldSpecialPreNationalData = new Set[Gen4Constants.pokemonCount + 1];
        Set[] overworldSpecialPostNationalData = new Set[Gen4Constants.pokemonCount + 1];

        for (int pk = 1; pk <= Gen4Constants.pokemonCount; pk++) {
            for (int time = 0; time < 3; time++) {
                dungeonAreaData[pk][time] = new TreeSet<>();
                overworldAreaData[pk][time] = new TreeSet<>();
            }
            dungeonSpecialPreNationalData[pk] = new TreeSet<>();
            dungeonSpecialPostNationalData[pk] = new TreeSet<>();
            overworldSpecialPreNationalData[pk] = new TreeSet<>();
            overworldSpecialPostNationalData[pk] = new TreeSet<>();
        }

        for (int c = 0; c < encounterData.files.size(); c++) {
            Set<Integer>[][] target;
            Set<Integer>[] specialTarget;
            int index;
            if (Gen4Constants.dpptOverworldDexMaps[c] != -1) {
                target = overworldAreaData;
                specialTarget = overworldSpecialPostNationalData;
                index = Gen4Constants.dpptOverworldDexMaps[c];
            } else if (Gen4Constants.dpptDungeonDexMaps[c] != -1) {
                target = dungeonAreaData;
                specialTarget = dungeonSpecialPostNationalData;
                index = Gen4Constants.dpptDungeonDexMaps[c];
            } else {
                continue;
            }

            byte[] b = encounterData.files.get(c);

            int grassRate = readLong(b, 0);
            if (grassRate != 0) {
                // up to 4
                List<Encounter> grassEncounters = readEncountersDPPt(b, 4, 12);

                for (int i = 0; i < 12; i++) {
                    int pknum = grassEncounters.get(i).pokemon.number;
                    if (i == 2 || i == 3) {
                        // morning only - time of day data for day/night for
                        // these slots
                        target[pknum][0].add(index);
                    } else {
                        // all times of day
                        target[pknum][0].add(index);
                        target[pknum][1].add(index);
                        target[pknum][2].add(index);
                    }
                }

                // time of day data for slots 2 and 3 day/night
                for (int i = 0; i < 4; i++) {
                    int pknum = readLong(b, 108 + 4 * i);
                    if (pknum >= 1 && pknum <= Gen4Constants.pokemonCount) {
                        target[pknum][i > 1 ? 2 : 1].add(index);
                    }
                }

                // For Swarm/Radar/GBA encounters, only Poke Radar encounters appear in the dex
                for (int i = 6; i < 10; i++) {
                    int offs = 100 + i * 4;
                    int pknum = readLong(b, offs);
                    if (pknum >= 1 && pknum <= Gen4Constants.pokemonCount) {
                        specialTarget[pknum].add(index);
                    }
                }
            }

            // up to 204, 5 sets of "sea" encounters to go
            int offset = 204;
            for (int i = 0; i < 5; i++) {
                int rate = readLong(b, offset);
                offset += 4;
                List<Encounter> encountersHere = readSeaEncountersDPPt(b, offset, 5);
                offset += 40;
                if (rate == 0 || i == 1) {
                    continue;
                }
                for (Encounter enc : encountersHere) {
                    target[enc.pokemon.number][0].add(index);
                    target[enc.pokemon.number][1].add(index);
                    target[enc.pokemon.number][2].add(index);
                }
            }
        }

        // Handle the "special" encounters that aren't in the encounter GARC
        for (EncounterSet es : encounters) {
            if (es.displayName.contains("Mt. Coronet Feebas Tiles")) {
                for (Encounter enc : es.encounters) {
                    dungeonSpecialPreNationalData[enc.pokemon.number].add(Gen4Constants.dpptMtCoronetDexIndex);
                    dungeonSpecialPostNationalData[enc.pokemon.number].add(Gen4Constants.dpptMtCoronetDexIndex);
                }
            } else if (es.displayName.contains("Honey Tree Group 1") || es.displayName.contains("Honey Tree Group 2")) {
                for (Encounter enc : es.encounters) {
                    dungeonSpecialPreNationalData[enc.pokemon.number].add(Gen4Constants.dpptFloaromaMeadowDexIndex);
                    dungeonSpecialPostNationalData[enc.pokemon.number].add(Gen4Constants.dpptFloaromaMeadowDexIndex);
                    overworldSpecialPreNationalData[enc.pokemon.number].addAll(Gen4Constants.dpptOverworldHoneyTreeDexIndicies);
                    overworldSpecialPostNationalData[enc.pokemon.number].addAll(Gen4Constants.dpptOverworldHoneyTreeDexIndicies);
                }
            } else if (es.displayName.contains("Trophy Garden Rotating Pokemon")) {
                for (Encounter enc : es.encounters) {
                    dungeonSpecialPostNationalData[enc.pokemon.number].add(Gen4Constants.dpptTrophyGardenDexIndex);
                }
            } else if (es.displayName.contains("Great Marsh Rotating Pokemon (Post-National Dex)")) {
                for (Encounter enc : es.encounters) {
                    dungeonSpecialPostNationalData[enc.pokemon.number].add(Gen4Constants.dpptGreatMarshDexIndex);
                }
            } else if (es.displayName.contains("Great Marsh Rotating Pokemon (Pre-National Dex)")) {
                for (Encounter enc : es.encounters) {
                    dungeonSpecialPreNationalData[enc.pokemon.number].add(Gen4Constants.dpptGreatMarshDexIndex);
                }
            }
        }

        // Write new area data to its file
        // Area data format credit to Ganix
        String pokedexAreaDataFile = romEntry.getFile("PokedexAreaData");
        NARCArchive pokedexAreaData = readNARC(pokedexAreaDataFile);
        int dungeonDataIndex = romEntry.getInt("PokedexAreaDataDungeonIndex");
        int dungeonSpecialPreNationalDataIndex = romEntry.getInt("PokedexAreaDataDungeonSpecialPreNationalIndex");
        int dungeonSpecialPostNationalDataIndex = romEntry.getInt("PokedexAreaDataDungeonSpecialPostNationalIndex");
        int overworldDataIndex = romEntry.getInt("PokedexAreaDataOverworldIndex");
        int overworldSpecialPreNationalDataIndex = romEntry.getInt("PokedexAreaDataOverworldSpecialPreNationalIndex");
        int overworldSpecialPostNationalDataIndex = romEntry.getInt("PokedexAreaDataOverworldSpecialPostNationalIndex");
        for (int pk = 1; pk <= Gen4Constants.pokemonCount; pk++) {
            for (int time = 0; time < 3; time++) {
                pokedexAreaData.files.set(dungeonDataIndex + pk + time * Gen4Constants.pokedexAreaDataSize,
                        makePokedexAreaDataFile(dungeonAreaData[pk][time]));
                pokedexAreaData.files.set(overworldDataIndex + pk + time * Gen4Constants.pokedexAreaDataSize,
                        makePokedexAreaDataFile(overworldAreaData[pk][time]));
            }
            pokedexAreaData.files.set(dungeonSpecialPreNationalDataIndex + pk,
                    makePokedexAreaDataFile(dungeonSpecialPreNationalData[pk]));
            pokedexAreaData.files.set(dungeonSpecialPostNationalDataIndex + pk,
                    makePokedexAreaDataFile(dungeonSpecialPostNationalData[pk]));
            pokedexAreaData.files.set(overworldSpecialPreNationalDataIndex + pk,
                    makePokedexAreaDataFile(overworldSpecialPreNationalData[pk]));
            pokedexAreaData.files.set(overworldSpecialPostNationalDataIndex + pk,
                    makePokedexAreaDataFile(overworldSpecialPostNationalData[pk]));
        }
        writeNARC(pokedexAreaDataFile, pokedexAreaData);
    }

    private void updatePokedexAreaDataHGSS(List<EncounterSet> encounters) throws IOException {
        String encountersFile = romEntry.getFile("WildPokemon");
        NARCArchive encounterData = readNARC(encountersFile);

        // Initialize empty area data
        Set[][] dungeonAreaData = new Set[Gen4Constants.pokemonCount + 1][3];
        Set[][] overworldAreaData = new Set[Gen4Constants.pokemonCount + 1][3];
        Set[] dungeonSpecialData = new Set[Gen4Constants.pokemonCount + 1];
        Set[] overworldSpecialData = new Set[Gen4Constants.pokemonCount + 1];

        for (int pk = 1; pk <= Gen4Constants.pokemonCount; pk++) {
            for (int time = 0; time < 3; time++) {
                dungeonAreaData[pk][time] = new TreeSet<>();
                overworldAreaData[pk][time] = new TreeSet<>();
            }
            dungeonSpecialData[pk] = new TreeSet<>();
            overworldSpecialData[pk] = new TreeSet<>();
        }

        for (int c = 0; c < encounterData.files.size(); c++) {
            Set<Integer>[][] target;
            Set<Integer>[] specialTarget;
            int index;
            if (Gen4Constants.hgssOverworldDexMaps[c] != -1) {
                target = overworldAreaData;
                specialTarget = overworldSpecialData;
                index = Gen4Constants.hgssOverworldDexMaps[c];
            } else if (Gen4Constants.hgssDungeonDexMaps[c] != -1) {
                target = dungeonAreaData;
                specialTarget = dungeonSpecialData;
                index = Gen4Constants.hgssDungeonDexMaps[c];
            } else {
                continue;
            }

            byte[] b = encounterData.files.get(c);
            int[] amounts = new int[]{0, 5, 2, 5, 5, 5};
            int[] rates = new int[6];
            rates[0] = b[0] & 0xFF;
            rates[1] = b[1] & 0xFF;
            rates[2] = b[2] & 0xFF;
            rates[3] = b[3] & 0xFF;
            rates[4] = b[4] & 0xFF;
            rates[5] = b[5] & 0xFF;
            // Up to 20 now (12 for levels)
            if (rates[0] != 0) {
                for (int time = 0; time < 3; time++) {
                    Pokemon[] pokes = readPokemonHGSS(b, 20 + time * 24, 12);
                    for (Pokemon pk : pokes) {
                        target[pk.number][time].add(index);
                    }
                }
            }

            // Hoenn/Sinnoh Radio
            EncounterSet radio = readOptionalEncountersHGSS(b, 92, 4);
            for (Encounter enc : radio.encounters) {
                specialTarget[enc.pokemon.number].add(index);
            }

            // Up to 100 now... 2*2*2 for radio pokemon
            // Handle surf, rock smash, and old rod
            int offset = 100;
            for (int i = 1; i < 4; i++) {
                List<Encounter> encountersHere = readSeaEncountersHGSS(b, offset, amounts[i]);
                offset += 4 * amounts[i];
                if (rates[i] != 0) {
                    // Valid area.
                    for (Encounter enc : encountersHere) {
                        target[enc.pokemon.number][0].add(index);
                        target[enc.pokemon.number][1].add(index);
                        target[enc.pokemon.number][2].add(index);
                    }
                }
            }

            // Handle good and super rod, because they can get an encounter slot replaced by the night fishing replacement
            Pokemon nightFishingReplacement = pokes[readWord(b, 192)];
            if (rates[4] != 0) {
                List<EncounterSet> goodRodEncounters =
                        readTimeBasedRodEncountersHGSS(b, offset, nightFishingReplacement, Gen4Constants.hgssGoodRodReplacementIndex);
                for (Encounter enc : goodRodEncounters.get(0).encounters) {
                    target[enc.pokemon.number][0].add(index);
                    target[enc.pokemon.number][1].add(index);
                }
                for (Encounter enc : goodRodEncounters.get(1).encounters) {
                    target[enc.pokemon.number][2].add(index);
                }
            }
            if (rates[5] != 0) {
                List<EncounterSet> superRodEncounters =
                        readTimeBasedRodEncountersHGSS(b, offset + 20, nightFishingReplacement, Gen4Constants.hgssSuperRodReplacementIndex);
                for (Encounter enc : superRodEncounters.get(0).encounters) {
                    target[enc.pokemon.number][0].add(index);
                    target[enc.pokemon.number][1].add(index);
                }
                for (Encounter enc : superRodEncounters.get(1).encounters) {
                    target[enc.pokemon.number][2].add(index);
                }
            }
        }

        // Handle headbutt encounters too (only doing it like this because reading the encounters from the ROM is really annoying)
        EncounterSet firstHeadbuttEncounter = encounters.stream().filter(es -> es.displayName.contains("Route 1 Headbutt")).findFirst().orElse(null);
        int startingHeadbuttOffset = encounters.indexOf(firstHeadbuttEncounter);
        if (startingHeadbuttOffset != -1) {
            for (int i = 0; i < Gen4Constants.hgssHeadbuttOverworldDexMaps.length; i++) {
                EncounterSet es = encounters.get(startingHeadbuttOffset + i);
                for (Encounter enc : es.encounters) {
                    if (Gen4Constants.hgssHeadbuttOverworldDexMaps[i] != -1) {
                        overworldSpecialData[enc.pokemon.number].add(Gen4Constants.hgssHeadbuttOverworldDexMaps[i]);
                    } else if (Gen4Constants.hgssHeadbuttDungeonDexMaps[i] != -1) {
                        dungeonSpecialData[enc.pokemon.number].add(Gen4Constants.hgssHeadbuttDungeonDexMaps[i]);
                    }
                }
            }
        }

        // Write new area data to its file
        // Area data format credit to Ganix
        String pokedexAreaDataFile = romEntry.getFile("PokedexAreaData");
        NARCArchive pokedexAreaData = readNARC(pokedexAreaDataFile);
        int dungeonDataIndex = romEntry.getInt("PokedexAreaDataDungeonIndex");
        int overworldDataIndex = romEntry.getInt("PokedexAreaDataOverworldIndex");
        int dungeonSpecialIndex = romEntry.getInt("PokedexAreaDataDungeonSpecialIndex");
        int overworldSpecialDataIndex = romEntry.getInt("PokedexAreaDataOverworldSpecialIndex");
        for (int pk = 1; pk <= Gen4Constants.pokemonCount; pk++) {
            for (int time = 0; time < 3; time++) {
                pokedexAreaData.files.set(dungeonDataIndex + pk + time * Gen4Constants.pokedexAreaDataSize,
                        makePokedexAreaDataFile(dungeonAreaData[pk][time]));
                pokedexAreaData.files.set(overworldDataIndex + pk + time * Gen4Constants.pokedexAreaDataSize,
                        makePokedexAreaDataFile(overworldAreaData[pk][time]));
            }
            pokedexAreaData.files.set(dungeonSpecialIndex + pk, makePokedexAreaDataFile(dungeonSpecialData[pk]));
            pokedexAreaData.files.set(overworldSpecialDataIndex + pk, makePokedexAreaDataFile(overworldSpecialData[pk]));
        }
        writeNARC(pokedexAreaDataFile, pokedexAreaData);
    }

    private byte[] makePokedexAreaDataFile(Set<Integer> data) {
        byte[] output = new byte[data.size() * 4 + 4];
        int idx = 0;
        for (Integer obj : data) {
            int areaIndex = obj;
            this.writeLong(output, idx, areaIndex);
            idx += 4;
        }
        return output;
    }

    @Override
    public List<Trainer> getTrainers() {
        List<Trainer> allTrainers = new ArrayList<>();
        try {
            NARCArchive trainers = this.readNARC(romEntry.getFile("TrainerData"));
            NARCArchive trpokes = this.readNARC(romEntry.getFile("TrainerPokemon"));
            List<String> tclasses = this.getTrainerClassNames();
            List<String> tnames = this.getTrainerNames();
            int trainernum = trainers.files.size();
            for (int i = 1; i < trainernum; i++) {
                // Trainer entries are 20 bytes
                // Team flags; 1 byte; 0x01 = custom moves, 0x02 = held item
                // Class; 1 byte
                // 1 byte not used
                // Number of pokemon in team; 1 byte
                // Items; 2 bytes each, 4 item slots
                // AI Flags; 2 byte
                // 2 bytes not used
                // Battle Mode; 1 byte; 0 means single, 1 means double.
                // 3 bytes not used
                byte[] trainer = trainers.files.get(i);
                byte[] trpoke = trpokes.files.get(i);
                Trainer tr = new Trainer();
                tr.poketype = trainer[0] & 0xFF;
                tr.trainerclass = trainer[1] & 0xFF;
                tr.index = i;
                int numPokes = trainer[3] & 0xFF;
                int pokeOffs = 0;
                tr.fullDisplayName = tclasses.get(tr.trainerclass) + " " + tnames.get(i - 1);
                for (int poke = 0; poke < numPokes; poke++) {
                    // Structure is
                    // IV SB LV LV SP SP FRM FRM
                    // (HI HI)
                    // (M1 M1 M2 M2 M3 M3 M4 M4)
                    // where SB = 0 0 Ab Ab 0 0 G G
                    // IV is a "difficulty" level between 0 and 255 to represent 0 to 31 IVs.
                    //     These IVs affect all attributes. For the vanilla games, the
                    //     vast majority of trainers have 0 IVs; Elite Four members will
                    //     have 30 IVs.
                    // Ab Ab = ability number, 0 for first ability, 2 for second [HGSS only]
                    // G G affect the gender somehow. 0 appears to mean "most common
                    //     gender for the species".
                    int difficulty = trpoke[pokeOffs] & 0xFF;
                    int level = trpoke[pokeOffs + 2] & 0xFF;
                    int species = (trpoke[pokeOffs + 4] & 0xFF) + ((trpoke[pokeOffs + 5] & 0x01) << 8);
                    int formnum = (trpoke[pokeOffs + 5] >> 2);
                    TrainerPokemon tpk = new TrainerPokemon();
                    tpk.level = level;
                    tpk.pokemon = pokes[species];
                    tpk.IVs = (difficulty * 31) / 255;
                    int abilitySlot = (trpoke[pokeOffs + 1] >>> 4) & 0xF;
                    if (abilitySlot == 0) {
                        // All Gen 4 games represent the first ability as ability 0.
                        abilitySlot = 1;
                    }
                    tpk.abilitySlot = abilitySlot;
                    tpk.forme = formnum;
                    tpk.formeSuffix = Gen4Constants.getFormeSuffixByBaseForme(species,formnum);
                    pokeOffs += 6;
                    if (tr.pokemonHaveItems()) {
                        tpk.heldItem = readWord(trpoke, pokeOffs);
                        pokeOffs += 2;
                    }
                    if (tr.pokemonHaveCustomMoves()) {
                        for (int move = 0; move < 4; move++) {
                            tpk.moves[move] = readWord(trpoke, pokeOffs + (move*2));
                        }
                        pokeOffs += 8;
                    }
                    // Plat/HGSS have another random pokeOffs +=2 here.
                    if (romEntry.romType != Gen4Constants.Type_DP) {
                        pokeOffs += 2;
                    }
                    tr.pokemon.add(tpk);
                }
                allTrainers.add(tr);
            }
            if (romEntry.romType == Gen4Constants.Type_DP) {
                Gen4Constants.tagTrainersDP(allTrainers);
                Gen4Constants.setMultiBattleStatusDP(allTrainers);
            } else if (romEntry.romType == Gen4Constants.Type_Plat) {
                Gen4Constants.tagTrainersPt(allTrainers);
                Gen4Constants.setMultiBattleStatusPt(allTrainers);
            } else {
                Gen4Constants.tagTrainersHGSS(allTrainers);
                Gen4Constants.setMultiBattleStatusHGSS(allTrainers);
            }
        } catch (IOException ex) {
            throw new RandomizerIOException(ex);
        }
        return allTrainers;
    }

    @Override
    public List<Integer> getMainPlaythroughTrainers() {
        return new ArrayList<>(); // Not implemented
    }

    @Override
    public List<Integer> getEliteFourTrainers(boolean isChallengeMode) {
        return Arrays.stream(romEntry.arrayEntries.get("EliteFourIndices")).boxed().collect(Collectors.toList());
    }

    @Override
    public List<Integer> getEvolutionItems() {
        return Gen4Constants.evolutionItems;
    }

    @Override
    public void setTrainers(List<Trainer> trainerData, boolean doubleBattleMode) {
        if (romEntry.romType == Gen4Constants.Type_HGSS) {
            fixAbilitySlotValuesForHGSS(trainerData);
        }
        Iterator<Trainer> allTrainers = trainerData.iterator();
        try {
            NARCArchive trainers = this.readNARC(romEntry.getFile("TrainerData"));
            NARCArchive trpokes = new NARCArchive();

            // Get current movesets in case we need to reset them for certain
            // trainer mons.
            Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();

            // empty entry
            trpokes.files.add(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 });
            int trainernum = trainers.files.size();
            for (int i = 1; i < trainernum; i++) {
                byte[] trainer = trainers.files.get(i);
                Trainer tr = allTrainers.next();
                // preserve original poketype
                trainer[0] = (byte) tr.poketype;
                int numPokes = tr.pokemon.size();
                trainer[3] = (byte) numPokes;

                if (doubleBattleMode) {
                    if (!tr.skipImportant()) {
                        // If we set this flag for partner trainers (e.g., Cheryl), then the double wild battles
                        // will turn into trainer battles with glitchy trainers.
                        boolean excludedPartnerTrainer = romEntry.romType != Gen4Constants.Type_HGSS &&
                                Gen4Constants.partnerTrainerIndices.contains(tr.index);
                        if (trainer[16] == 0 && !excludedPartnerTrainer) {
                            trainer[16] |= 3;
                        }
                    }
                }

                int bytesNeeded = 6 * numPokes;
                if (romEntry.romType != Gen4Constants.Type_DP) {
                    bytesNeeded += 2 * numPokes;
                }
                if (tr.pokemonHaveCustomMoves()) {
                    bytesNeeded += 8 * numPokes; // 2 bytes * 4 moves
                }
                if (tr.pokemonHaveItems()) {
                    bytesNeeded += 2 * numPokes;
                }
                byte[] trpoke = new byte[bytesNeeded];
                int pokeOffs = 0;
                Iterator<TrainerPokemon> tpokes = tr.pokemon.iterator();
                for (int poke = 0; poke < numPokes; poke++) {
                    TrainerPokemon tp = tpokes.next();
                    int ability = tp.abilitySlot << 4;
                    if (tp.abilitySlot == 1) {
                        // All Gen 4 games represent the first ability as ability 0.
                        ability = 0;
                    }
                    // Add 1 to offset integer division truncation
                    int difficulty = Math.min(255, 1 + (tp.IVs * 255) / 31);
                    writeWord(trpoke, pokeOffs, difficulty | ability << 8);
                    writeWord(trpoke, pokeOffs + 2, tp.level);
                    writeWord(trpoke, pokeOffs + 4, tp.pokemon.number);
                    trpoke[pokeOffs + 5] |= (tp.forme << 2);
                    pokeOffs += 6;
                    if (tr.pokemonHaveItems()) {
                        writeWord(trpoke, pokeOffs, tp.heldItem);
                        pokeOffs += 2;
                    }
                    if (tr.pokemonHaveCustomMoves()) {
                        if (tp.resetMoves) {
                            int[] pokeMoves = RomFunctions.getMovesAtLevel(getAltFormeOfPokemon(tp.pokemon, tp.forme).number, movesets, tp.level);
                            for (int m = 0; m < 4; m++) {
                                writeWord(trpoke, pokeOffs + m * 2, pokeMoves[m]);
                            }
                        } else {
                            writeWord(trpoke, pokeOffs, tp.moves[0]);
                            writeWord(trpoke, pokeOffs + 2, tp.moves[1]);
                            writeWord(trpoke, pokeOffs + 4, tp.moves[2]);
                            writeWord(trpoke, pokeOffs + 6, tp.moves[3]);
                        }
                        pokeOffs += 8;
                    }
                    // Plat/HGSS have another random pokeOffs +=2 here.
                    if (romEntry.romType != Gen4Constants.Type_DP) {
                        pokeOffs += 2;
                    }
                }
                trpokes.files.add(trpoke);
            }
            this.writeNARC(romEntry.getFile("TrainerData"), trainers);
            this.writeNARC(romEntry.getFile("TrainerPokemon"), trpokes);

            // In Gen 4, the game prioritizes showing the special double battle intro over almost any
            // other kind of intro. Since the trainer music is tied to the intro, this results in the
            // vast majority of "special" trainers losing their intro and music in double battle mode.
            // To fix this, the below code patches the executable to skip the case for the special
            // double battle intro (by changing a beq to an unconditional branch); this slightly breaks
            // battles that are double battles in the original game, but the trade-off is worth it.

            // Then, also patch various subroutines that control the "Trainer Eye" event and text boxes
            // related to this in order to make double battles work on all trainers
            if (doubleBattleMode) {
                String doubleBattleFixPrefix = Gen4Constants.getDoubleBattleFixPrefix(romEntry.romType);
                int offset = find(arm9, doubleBattleFixPrefix);
                if (offset > 0) {
                    offset += doubleBattleFixPrefix.length() / 2; // because it was a prefix
                    arm9[offset] = (byte) 0xE0;
                } else {
                    throw new RandomizationException("Double Battle Mode not supported for this game");
                }

                String doubleBattleFlagReturnPrefix = romEntry.getString("DoubleBattleFlagReturnPrefix");
                String doubleBattleWalkingPrefix1 = romEntry.getString("DoubleBattleWalkingPrefix1");
                String doubleBattleWalkingPrefix2 = romEntry.getString("DoubleBattleWalkingPrefix2");
                String doubleBattleTextBoxPrefix = romEntry.getString("DoubleBattleTextBoxPrefix");

                // After getting the double battle flag, return immediately instead of converting it to a 1 for
                // non-zero values/0 for zero
                offset = find(arm9, doubleBattleFlagReturnPrefix);
                if (offset > 0) {
                    offset += doubleBattleFlagReturnPrefix.length() / 2; // because it was a prefix
                    writeWord(arm9, offset, 0xBD08);
                } else {
                    throw new RandomizationException("Double Battle Mode not supported for this game");
                }

                // Instead of doing "double trainer walk" for nonzero values, do it only for value == 2
                offset = find(arm9, doubleBattleWalkingPrefix1);
                if (offset > 0) {
                    offset += doubleBattleWalkingPrefix1.length() / 2; // because it was a prefix
                    arm9[offset] = (byte) 0x2;      // cmp r0, #0x2
                    arm9[offset+3] = (byte) 0xD0;   // beq DOUBLE_TRAINER_WALK
                } else {
                    throw new RandomizationException("Double Battle Mode not supported for this game");
                }

                // Instead of checking if the value was exactly 1 after checking that it was nonzero, check that it's
                // 2 again lol
                offset = find(arm9, doubleBattleWalkingPrefix2);
                if (offset > 0) {
                    offset += doubleBattleWalkingPrefix2.length() / 2; // because it was a prefix
                    arm9[offset] = (byte) 0x2;
                } else {
                    throw new RandomizationException("Double Battle Mode not supported for this game");
                }

                // Once again, compare a value to 2 instead of just checking that it's nonzero
                offset = find(arm9, doubleBattleTextBoxPrefix);
                if (offset > 0) {
                    offset += doubleBattleTextBoxPrefix.length() / 2; // because it was a prefix
                    writeWord(arm9, offset, 0x46C0);
                    writeWord(arm9, offset+2, 0x2802);
                    arm9[offset+5] = (byte) 0xD0;
                } else {
                    throw new RandomizationException("Double Battle Mode not supported for this game");
                }

                // This NARC has some data that controls how text boxes are handled at the end of a trainer battle.
                // Changing this byte from 4 -> 0 makes it check if the "double battle" flag is exactly 2 instead of
                // checking "flag & 2", which makes the single trainer double battles use the single battle
                // handling (since we set their flag to 3 instead of 2)
                NARCArchive battleSkillSubSeq = readNARC(romEntry.getFile("BattleSkillSubSeq"));
                byte[] trainerEndFile = battleSkillSubSeq.files.get(romEntry.getInt("TrainerEndFileNumber"));
                trainerEndFile[romEntry.getInt("TrainerEndTextBoxOffset")] = 0;
                writeNARC(romEntry.getFile("BattleSkillSubSeq"), battleSkillSubSeq);

            }
        } catch (IOException ex) {
            throw new RandomizerIOException(ex);
        }
    }

    // Note: This method is here to avoid bloating AbstractRomHandler with special-case logic.
    // It only works here because nothing in AbstractRomHandler cares about the abilitySlot at
    // the moment; if that changes, then this should be moved there instead.
    private void fixAbilitySlotValuesForHGSS(List<Trainer> trainers) {
        for (Trainer tr : trainers) {
            if (tr.pokemon.size() > 0) {
                TrainerPokemon lastPokemon = tr.pokemon.get(tr.pokemon.size() - 1);
                int lastAbilitySlot = lastPokemon.abilitySlot;
                for (int i = 0; i < tr.pokemon.size(); i++) {
                    // HGSS has a nasty bug where if a single Pokemon with an abilitySlot of 2
                    // appears on the trainer's team, then all Pokemon that appear after it in
                    // the trpoke data will *also* use their second ability in-game, regardless
                    // of what their abilitySlot is set to. This can mess with the rival's
                    // starter carrying forward their ability, and can also cause sensible items
                    // to behave incorrectly. To fix this, we just make sure everything on a
                    // Trainer's team uses the same abilitySlot. The choice to copy the last
                    // Pokemon's abilitySlot is arbitrary, but allows us to avoid any special-
                    // casing involving the rival's starter, since it always appears last.
                    tr.pokemon.get(i).abilitySlot = lastAbilitySlot;
                }
            }
        }
    }

    @Override
    public List<Pokemon> bannedForWildEncounters() {
        // Ban Unown in DPPt because you can't get certain letters outside of Solaceon Ruins.
        // Ban Unown in HGSS because they don't show up unless you complete a puzzle in the Ruins of Alph.
        return new ArrayList<>(Collections.singletonList(pokes[Species.unown]));
    }

    @Override
    public List<Pokemon> getBannedFormesForTrainerPokemon() {
        List<Pokemon> banned = new ArrayList<>();
        if (romEntry.romType != Gen4Constants.Type_DP) {
            Pokemon giratinaOrigin = this.getAltFormeOfPokemon(pokes[Species.giratina], 1);
            if (giratinaOrigin != null) {
                // Ban Giratina-O for trainers in Gen 4, since he just instantly transforms
                // back to Altered Forme if he's not holding the Griseous Orb.
                banned.add(giratinaOrigin);
            }
        }
        return banned;
    }

    @Override
    public Map<Integer, List<MoveLearnt>> getMovesLearnt() {
        Map<Integer, List<MoveLearnt>> movesets = new TreeMap<>();
        try {
            NARCArchive movesLearnt = this.readNARC(romEntry.getFile("PokemonMovesets"));
            int formeCount = Gen4Constants.getFormeCount(romEntry.romType);
            for (int i = 1; i <= Gen4Constants.pokemonCount + formeCount; i++) {
                Pokemon pkmn = pokes[i];
                byte[] rom;
                if (i > Gen4Constants.pokemonCount) {
                    rom = movesLearnt.files.get(i + Gen4Constants.formeOffset);
                } else {
                    rom = movesLearnt.files.get(i);
                }
                int moveDataLoc = 0;
                List<MoveLearnt> learnt = new ArrayList<>();
                while ((rom[moveDataLoc] & 0xFF) != 0xFF || (rom[moveDataLoc + 1] & 0xFF) != 0xFF) {
                    int move = (rom[moveDataLoc] & 0xFF);
                    int level = (rom[moveDataLoc + 1] & 0xFE) >> 1;
                    if ((rom[moveDataLoc + 1] & 0x01) == 0x01) {
                        move += 256;
                    }
                    MoveLearnt ml = new MoveLearnt();
                    ml.level = level;
                    ml.move = move;
                    learnt.add(ml);
                    moveDataLoc += 2;
                }
                movesets.put(pkmn.number, learnt);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return movesets;
    }

    @Override
    public void setMovesLearnt(Map<Integer, List<MoveLearnt>> movesets) {
        // int[] extraLearnSets = new int[] { 7, 13, 13 };
        // Build up a new NARC
        NARCArchive movesLearnt = new NARCArchive();
        // The blank moveset
        byte[] blankSet = new byte[] { (byte) 0xFF, (byte) 0xFF, 0, 0 };
        movesLearnt.files.add(blankSet);
        int formeCount = Gen4Constants.getFormeCount(romEntry.romType);
        for (int i = 1; i <= Gen4Constants.pokemonCount + formeCount; i++) {
            if (i == Gen4Constants.pokemonCount + 1) {
                for (int j = 0; j < Gen4Constants.formeOffset; j++) {
                    movesLearnt.files.add(blankSet);
                }
            }
            Pokemon pkmn = pokes[i];
            List<MoveLearnt> learnt = movesets.get(pkmn.number);
            int sizeNeeded = learnt.size() * 2 + 2;
            if ((sizeNeeded % 4) != 0) {
                sizeNeeded += 2;
            }
            byte[] moveset = new byte[sizeNeeded];
            int j = 0;
            for (; j < learnt.size(); j++) {
                MoveLearnt ml = learnt.get(j);
                moveset[j * 2] = (byte) (ml.move & 0xFF);
                int levelPart = (ml.level << 1) & 0xFE;
                if (ml.move > 255) {
                    levelPart++;
                }
                moveset[j * 2 + 1] = (byte) levelPart;
            }
            moveset[j * 2] = (byte) 0xFF;
            moveset[j * 2 + 1] = (byte) 0xFF;
            movesLearnt.files.add(moveset);
        }
        //for (int j = 0; j < extraLearnSets[romEntry.romType]; j++) {
        //    movesLearnt.files.add(blankSet);
        //}
        // Save
        try {
            this.writeNARC(romEntry.getFile("PokemonMovesets"), movesLearnt);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

    }

    @Override
    public Map<Integer, List<Integer>> getEggMoves() {
        Map<Integer, List<Integer>> eggMoves = new TreeMap<>();
        try {
            if (romEntry.romType == Gen4Constants.Type_HGSS) {
                NARCArchive eggMoveNARC = this.readNARC(romEntry.getFile("EggMoves"));
                byte[] eggMoveData = eggMoveNARC.files.get(0);
                eggMoves = readEggMoves(eggMoveData, 0);
            } else {
                byte[] fieldOvl = readOverlay(romEntry.getInt("FieldOvlNumber"));
                int offset = find(fieldOvl, Gen4Constants.dpptEggMoveTablePrefix);
                if (offset > 0) {
                    offset += Gen4Constants.dpptEggMoveTablePrefix.length() / 2; // because it was a prefix
                    eggMoves = readEggMoves(fieldOvl, offset);
                }
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        return eggMoves;
    }

    @Override
    public void setEggMoves(Map<Integer, List<Integer>> eggMoves) {
        try {
            if (romEntry.romType == Gen4Constants.Type_HGSS) {
                NARCArchive eggMoveNARC = this.readNARC(romEntry.getFile("EggMoves"));
                byte[] eggMoveData = eggMoveNARC.files.get(0);
                writeEggMoves(eggMoves, eggMoveData, 0);
                eggMoveNARC.files.set(0, eggMoveData);
                this.writeNARC(romEntry.getFile("EggMoves"), eggMoveNARC);
            } else {
                byte[] fieldOvl = readOverlay(romEntry.getInt("FieldOvlNumber"));
                int offset = find(fieldOvl, Gen4Constants.dpptEggMoveTablePrefix);
                if (offset > 0) {
                    offset += Gen4Constants.dpptEggMoveTablePrefix.length() / 2; // because it was a prefix
                    writeEggMoves(eggMoves, fieldOvl, offset);
                    this.writeOverlay(romEntry.getInt("FieldOvlNumber"), fieldOvl);
                }
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private Map<Integer, List<Integer>> readEggMoves(byte[] data, int startingOffset) {
        Map<Integer, List<Integer>> eggMoves = new TreeMap<>();
        int currentOffset = startingOffset;
        int currentSpecies = 0;
        List<Integer> currentMoves = new ArrayList<>();
        int val = FileFunctions.read2ByteInt(data, currentOffset);

        // Egg move data is stored exactly like in Gen 3, so check egg_moves.h in the
        // Gen 3 decomps for more info on how this algorithm works.
        while (val != 0xFFFF) {
            if (val > 20000) {
                int species = val - 20000;
                if (currentMoves.size() > 0) {
                    eggMoves.put(currentSpecies, currentMoves);
                }
                currentSpecies = species;
                currentMoves = new ArrayList<>();
            } else {
                currentMoves.add(val);
            }
            currentOffset += 2;
            val = FileFunctions.read2ByteInt(data, currentOffset);
        }

        // Need to make sure the last entry gets recorded too
        if (currentMoves.size() > 0) {
            eggMoves.put(currentSpecies, currentMoves);
        }

        return eggMoves;
    }

    private void writeEggMoves(Map<Integer, List<Integer>> eggMoves, byte[] data, int startingOffset) {
        int currentOffset = startingOffset;
        for (int species : eggMoves.keySet()) {
            FileFunctions.write2ByteInt(data, currentOffset, species + 20000);
            currentOffset += 2;
            for (int move : eggMoves.get(species)) {
                FileFunctions.write2ByteInt(data, currentOffset, move);
                currentOffset += 2;
            }
        }
    }

    private static class ScriptEntry {
        private int scriptFile;
        private int scriptOffset;

        public ScriptEntry(int scriptFile, int scriptOffset) {
            this.scriptFile = scriptFile;
            this.scriptOffset = scriptOffset;
        }
    }

    private static class TextEntry {
        private int textIndex;
        private int stringNumber;

        public TextEntry(int textIndex, int stringNumber) {
            this.textIndex = textIndex;
            this.stringNumber = stringNumber;
        }
    }

    private static class StaticPokemon {
        protected ScriptEntry[] speciesEntries;
        protected ScriptEntry[] formeEntries;
        protected ScriptEntry[] levelEntries;

        public StaticPokemon() {
            this.speciesEntries = new ScriptEntry[0];
            this.formeEntries = new ScriptEntry[0];
            this.levelEntries = new ScriptEntry[0];
        }

        public Pokemon getPokemon(Gen4RomHandler parent, NARCArchive scriptNARC) {
            return parent.pokes[parent.readWord(scriptNARC.files.get(speciesEntries[0].scriptFile), speciesEntries[0].scriptOffset)];
        }

        public void setPokemon(Gen4RomHandler parent, NARCArchive scriptNARC, Pokemon pkmn) {
            int value = pkmn.number;
            for (int i = 0; i < speciesEntries.length; i++) {
                byte[] file = scriptNARC.files.get(speciesEntries[i].scriptFile);
                parent.writeWord(file, speciesEntries[i].scriptOffset, value);
            }
        }

        public int getForme(NARCArchive scriptNARC) {
            if (formeEntries.length == 0) {
                return 0;
            }
            byte[] file = scriptNARC.files.get(formeEntries[0].scriptFile);
            return file[formeEntries[0].scriptOffset];
        }

        public void setForme(NARCArchive scriptNARC, int forme) {
            for (int i = 0; i < formeEntries.length; i++) {
                byte[] file = scriptNARC.files.get(formeEntries[i].scriptFile);
                file[formeEntries[i].scriptOffset] = (byte) forme;
            }
        }

        public int getLevelCount() {
            return levelEntries.length;
        }

        public int getLevel(NARCArchive scriptNARC, int i) {
            if (levelEntries.length <= i) {
                return 1;
            }
            byte[] file = scriptNARC.files.get(levelEntries[i].scriptFile);
            return file[levelEntries[i].scriptOffset];
        }

        public void setLevel(NARCArchive scriptNARC, int level, int i) {
            if (levelEntries.length > i) { // Might not have a level entry e.g., it's an egg
                byte[] file = scriptNARC.files.get(levelEntries[i].scriptFile);
                file[levelEntries[i].scriptOffset] = (byte) level;
            }
        }
    }

    private static class StaticPokemonGameCorner extends StaticPokemon {
        private TextEntry[] textEntries;

        public StaticPokemonGameCorner() {
            super();
            this.textEntries = new TextEntry[0];
        }

        @Override
        public void setPokemon(Gen4RomHandler parent, NARCArchive scriptNARC, Pokemon pkmn) {
            super.setPokemon(parent, scriptNARC, pkmn);
            for (TextEntry textEntry : textEntries) {
                List<String> strings = parent.getStrings(textEntry.textIndex);
                String originalString = strings.get(textEntry.stringNumber);
                // For JP, the first thing after the name is "\x0001". For non-JP, it's "\v0203"
                int postNameIndex = originalString.indexOf("\\");
                String newString = pkmn.name.toUpperCase() + originalString.substring(postNameIndex);
                strings.set(textEntry.stringNumber, newString);
                parent.setStrings(textEntry.textIndex, strings);
            }
        }
    }

    private static class RoamingPokemon {
        private int[] speciesCodeOffsets;
        private int[] levelCodeOffsets;
        private ScriptEntry[] speciesScriptOffsets;
        private ScriptEntry[] genderOffsets;

        public RoamingPokemon() {
            this.speciesCodeOffsets = new int[0];
            this.levelCodeOffsets = new int[0];
            this.speciesScriptOffsets = new ScriptEntry[0];
            this.genderOffsets = new ScriptEntry[0];
        }

        public Pokemon getPokemon(Gen4RomHandler parent) {
            int species = parent.readWord(parent.arm9, speciesCodeOffsets[0]);
            return parent.pokes[species];
        }

        public void setPokemon(Gen4RomHandler parent, NARCArchive scriptNARC, Pokemon pkmn) {
            int value = pkmn.number;
            for (int speciesCodeOffset : speciesCodeOffsets) {
                parent.writeWord(parent.arm9, speciesCodeOffset, value);
            }
            for (ScriptEntry speciesScriptOffset : speciesScriptOffsets) {
                byte[] file = scriptNARC.files.get(speciesScriptOffset.scriptFile);
                parent.writeWord(file, speciesScriptOffset.scriptOffset, value);
            }
            int gender = 0; // male (works for genderless Pokemon too)
            if (pkmn.genderRatio == 0xFE) {
                gender = 1; // female
            }
            for (ScriptEntry genderOffset : genderOffsets) {
                byte[] file = scriptNARC.files.get(genderOffset.scriptFile);
                parent.writeWord(file, genderOffset.scriptOffset, gender);
            }
        }

        public int getLevel(Gen4RomHandler parent) {
            if (levelCodeOffsets.length == 0) {
                return 1;
            }
            return parent.arm9[levelCodeOffsets[0]];
        }

        public void setLevel(Gen4RomHandler parent, int level) {
            for (int levelCodeOffset : levelCodeOffsets) {
                parent.arm9[levelCodeOffset] = (byte) level;
            }
        }
    }

    @Override
    public List<StaticEncounter> getStaticPokemon() {
        List<StaticEncounter> sp = new ArrayList<>();
        if (!romEntry.staticPokemonSupport) {
            return sp;
        }
        try {
            int[] staticEggOffsets = new int[0];
            if (romEntry.arrayEntries.containsKey("StaticEggPokemonOffsets")) {
                staticEggOffsets = romEntry.arrayEntries.get("StaticEggPokemonOffsets");
            }
            NARCArchive scriptNARC = scriptNarc;
            for (int i = 0; i < romEntry.staticPokemon.size(); i++) {
                int currentOffset = i;
                StaticPokemon statP = romEntry.staticPokemon.get(i);
                StaticEncounter se = new StaticEncounter();
                Pokemon newPK = statP.getPokemon(this, scriptNARC);
                newPK = getAltFormeOfPokemon(newPK, statP.getForme(scriptNARC));
                se.pkmn = newPK;
                se.level = statP.getLevel(scriptNARC, 0);
                se.isEgg = Arrays.stream(staticEggOffsets).anyMatch(x-> x == currentOffset);
                for (int levelEntry = 1; levelEntry < statP.getLevelCount(); levelEntry++) {
                    StaticEncounter linkedStatic = new StaticEncounter();
                    linkedStatic.pkmn = newPK;
                    linkedStatic.level = statP.getLevel(scriptNARC, levelEntry);
                    se.linkedEncounters.add(linkedStatic);
                }
                sp.add(se);
            }
            if (romEntry.arrayEntries.containsKey("StaticPokemonTrades")) {
                NARCArchive tradeNARC = this.readNARC(romEntry.getFile("InGameTrades"));
                int[] trades = romEntry.arrayEntries.get("StaticPokemonTrades");
                int[] scripts = romEntry.arrayEntries.get("StaticPokemonTradeScripts");
                int[] scriptOffsets = romEntry.arrayEntries.get("StaticPokemonTradeLevelOffsets");
                for (int i = 0; i < trades.length; i++) {
                    int tradeNum = trades[i];
                    byte[] scriptFile = scriptNARC.files.get(scripts[i]);
                    int level = scriptFile[scriptOffsets[i]];
                    StaticEncounter se = new StaticEncounter(pokes[readLong(tradeNARC.files.get(tradeNum), 0)]);
                    se.level = level;
                    sp.add(se);
                }
            }
            if (romEntry.getInt("MysteryEggOffset") > 0) {
                byte[] ovOverlay = readOverlay(romEntry.getInt("FieldOvlNumber"));
                StaticEncounter se = new StaticEncounter(pokes[ovOverlay[romEntry.getInt("MysteryEggOffset")] & 0xFF]);
                se.isEgg = true;
                sp.add(se);
            }
            if (romEntry.getInt("FossilTableOffset") > 0) {
                byte[] ftData = arm9;
                int baseOffset = romEntry.getInt("FossilTableOffset");
                int fossilLevelScriptNum = romEntry.getInt("FossilLevelScriptNumber");
                byte[] fossilLevelScript = scriptNARC.files.get(fossilLevelScriptNum);
                int level = fossilLevelScript[romEntry.getInt("FossilLevelOffset")];
                if (romEntry.romType == Gen4Constants.Type_HGSS) {
                    ftData = readOverlay(romEntry.getInt("FossilTableOvlNumber"));
                }
                // read the 7 Fossil Pokemon
                for (int f = 0; f < Gen4Constants.fossilCount; f++) {
                    StaticEncounter se = new StaticEncounter(pokes[readWord(ftData, baseOffset + 2 + f * 4)]);
                    se.level = level;
                    sp.add(se);
                }
            }

            if (roamerRandomizationEnabled) {
                getRoamers(sp);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return sp;
    }

    @Override
    public boolean setStaticPokemon(List<StaticEncounter> staticPokemon) {
        if (!romEntry.staticPokemonSupport) {
            return false;
        }
        int sptsize = romEntry.arrayEntries.containsKey("StaticPokemonTrades") ? romEntry.arrayEntries
                .get("StaticPokemonTrades").length : 0;
        int meggsize = romEntry.getInt("MysteryEggOffset") > 0 ? 1 : 0;
        int fossilsize = romEntry.getInt("FossilTableOffset") > 0 ? 7 : 0;
        if (staticPokemon.size() != romEntry.staticPokemon.size() + sptsize + meggsize + fossilsize + romEntry.roamingPokemon.size()) {
            return false;
        }
        try {
            Iterator<StaticEncounter> statics = staticPokemon.iterator();
            NARCArchive scriptNARC = scriptNarc;
            for (StaticPokemon statP : romEntry.staticPokemon) {
                StaticEncounter se = statics.next();
                statP.setPokemon(this, scriptNARC, se.pkmn);
                statP.setForme(scriptNARC, se.pkmn.formeNumber);
                statP.setLevel(scriptNARC, se.level, 0);
                for (int i = 0; i < se.linkedEncounters.size(); i++) {
                    StaticEncounter linkedStatic = se.linkedEncounters.get(i);
                    statP.setLevel(scriptNARC, linkedStatic.level, i + 1);
                }
            }
            if (romEntry.arrayEntries.containsKey("StaticPokemonTrades")) {
                NARCArchive tradeNARC = this.readNARC(romEntry.getFile("InGameTrades"));
                int[] trades = romEntry.arrayEntries.get("StaticPokemonTrades");
                int[] scripts = romEntry.arrayEntries.get("StaticPokemonTradeScripts");
                int[] scriptOffsets = romEntry.arrayEntries.get("StaticPokemonTradeLevelOffsets");
                for (int i = 0; i < trades.length; i++) {
                    int tradeNum = trades[i];
                    StaticEncounter se = statics.next();
                    Pokemon thisTrade = se.pkmn;
                    List<Integer> possibleAbilities = new ArrayList<>();
                    possibleAbilities.add(thisTrade.ability1);
                    if (thisTrade.ability2 > 0) {
                        possibleAbilities.add(thisTrade.ability2);
                    }
                    if (thisTrade.ability3 > 0) {
                        possibleAbilities.add(thisTrade.ability3);
                    }

                    // Write species and ability
                    writeLong(tradeNARC.files.get(tradeNum), 0, thisTrade.number);
                    writeLong(tradeNARC.files.get(tradeNum), 0x1C,
                            possibleAbilities.get(this.random.nextInt(possibleAbilities.size())));

                    // Write level to script file
                    byte[] scriptFile = scriptNARC.files.get(scripts[i]);
                    scriptFile[scriptOffsets[i]] = (byte) se.level;

                    // If it's Kenya, write new species name to text file
                    if (i == 1) {
                        Map<String, String> replacements = new TreeMap<>();
                        replacements.put(pokes[Species.spearow].name.toUpperCase(), se.pkmn.name);
                        replaceAllStringsInEntry(romEntry.getInt("KenyaTextOffset"), replacements);
                    }
                }
                writeNARC(romEntry.getFile("InGameTrades"), tradeNARC);
            }
            if (romEntry.getInt("MysteryEggOffset") > 0) {
                // Same overlay as MT moves
                // Truncate the pokemon# to 1byte, unless it's 0
                int pokenum = statics.next().pkmn.number;
                if (pokenum > 255) {
                    pokenum = this.random.nextInt(255) + 1;
                }
                byte[] ovOverlay = readOverlay(romEntry.getInt("FieldOvlNumber"));
                ovOverlay[romEntry.getInt("MysteryEggOffset")] = (byte) pokenum;
                writeOverlay(romEntry.getInt("FieldOvlNumber"), ovOverlay);
            }
            if (romEntry.getInt("FossilTableOffset") > 0) {
                int baseOffset = romEntry.getInt("FossilTableOffset");
                int fossilLevelScriptNum = romEntry.getInt("FossilLevelScriptNumber");
                byte[] fossilLevelScript = scriptNARC.files.get(fossilLevelScriptNum);
                if (romEntry.romType == Gen4Constants.Type_HGSS) {
                    byte[] ftData = readOverlay(romEntry.getInt("FossilTableOvlNumber"));
                    for (int f = 0; f < Gen4Constants.fossilCount; f++) {
                        StaticEncounter se = statics.next();
                        int pokenum = se.pkmn.number;
                        writeWord(ftData, baseOffset + 2 + f * 4, pokenum);
                        fossilLevelScript[romEntry.getInt("FossilLevelOffset")] = (byte) se.level;
                    }
                    writeOverlay(romEntry.getInt("FossilTableOvlNumber"), ftData);
                } else {
                    // write to arm9
                    for (int f = 0; f < Gen4Constants.fossilCount; f++) {
                        StaticEncounter se = statics.next();
                        int pokenum = se.pkmn.number;
                        writeWord(arm9, baseOffset + 2 + f * 4, pokenum);
                        fossilLevelScript[romEntry.getInt("FossilLevelOffset")] = (byte) se.level;
                    }
                }
            }
            if (roamerRandomizationEnabled) {
                setRoamers(statics);
            }
            if (romEntry.romType == Gen4Constants.Type_Plat) {
                patchDistortionWorldGroundCheck();
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return true;
    }

    private void getRoamers(List<StaticEncounter> statics) {
        if (romEntry.romType == Gen4Constants.Type_DP) {
            int offset = romEntry.getInt("RoamingPokemonFunctionStartOffset");
            if (readWord(arm9, offset + 44) != 0) {
                // In the original code, the code at this offset would be performing a shift to put
                // Cresselia's constant in r7. After applying the patch, this is now a nop, since
                // we just pc-relative load it instead. So if a nop isn't here, apply the patch.
                applyDiamondPearlRoamerPatch();
            }
        } else if (romEntry.romType == Gen4Constants.Type_Plat || romEntry.romType == Gen4Constants.Type_HGSS) {
            int firstSpeciesOffset = romEntry.roamingPokemon.get(0).speciesCodeOffsets[0];
            if (arm9.length < firstSpeciesOffset || readWord(arm9, firstSpeciesOffset) == 0) {
                // Either the arm9 hasn't been extended, or the patch hasn't been written
                int extendBy = romEntry.getInt("Arm9ExtensionSize");
                arm9 = extendARM9(arm9, extendBy, romEntry.getString("TCMCopyingPrefix"), Gen4Constants.arm9Offset);
                genericIPSPatch(arm9, "NewRoamerSubroutineTweak");
            }
        }
        for (int i = 0; i < romEntry.roamingPokemon.size(); i++) {
            RoamingPokemon roamer = romEntry.roamingPokemon.get(i);
            StaticEncounter se = new StaticEncounter();
            se.pkmn = roamer.getPokemon(this);
            se.level = roamer.getLevel(this);
            statics.add(se);
        }
    }

    private void setRoamers(Iterator<StaticEncounter> statics) {
        for (int i = 0; i < romEntry.roamingPokemon.size(); i++) {
            RoamingPokemon roamer = romEntry.roamingPokemon.get(i);
            StaticEncounter roamerEncounter = statics.next();
            roamer.setPokemon(this, scriptNarc, roamerEncounter.pkmn);
            roamer.setLevel(this, roamerEncounter.level);
        }
    }

    private void applyDiamondPearlRoamerPatch() {
        int offset = romEntry.getInt("RoamingPokemonFunctionStartOffset");

        // The original code had an entry for Darkrai; its species ID is pc-relative loaded. Since this
        // entry is clearly unused, just replace Darkrai's species ID constant with Cresselia's, since
        // in the original code, her ID is computed as 0x7A << 0x2
        FileFunctions.writeFullInt(arm9, offset + 244, Species.cresselia);

        // Now write a pc-relative load to our new constant over where Cresselia's ID is normally mov'd
        // into r7 and shifted.
        arm9[offset + 42] = 0x32;
        arm9[offset + 43] = 0x4F;
        arm9[offset + 44] = 0x00;
        arm9[offset + 45] = 0x00;
    }

    private void patchDistortionWorldGroundCheck() throws IOException {
        byte[] fieldOverlay = readOverlay(romEntry.getInt("FieldOvlNumber"));
        int offset = find(fieldOverlay, Gen4Constants.distortionWorldGroundCheckPrefix);
        if (offset > 0) {
            offset += Gen4Constants.distortionWorldGroundCheckPrefix.length() / 2; // because it was a prefix

            // We're now looking at a jump table in the field overlay that determines which intro graphic the game
            // should display when encountering a Pokemon that does *not* have a special intro. The Giratina fight
            // in the Distortion World uses ground type 23, and that particular ground type never initializes the
            // variable that determines which graphic to use. As a result, if Giratina is replaced with a Pokemon
            // that lacks a special intro, the game will use an uninitialized value for the intro graphic and crash.
            // The below code simply patches the jump table entry for ground type 23 to take the same branch that
            // regular grass encounters take, ensuring the intro graphic variable is initialized.
            fieldOverlay[offset + (2 * 23)] = 0x30;
            writeOverlay(romEntry.getInt("FieldOvlNumber"), fieldOverlay);
        }
    }

    @Override
    public List<Integer> getTMMoves() {
        String tmDataPrefix;
        if (romEntry.romType == Gen4Constants.Type_DP || romEntry.romType == Gen4Constants.Type_Plat) {
            tmDataPrefix = Gen4Constants.dpptTMDataPrefix;
        } else {
            tmDataPrefix = Gen4Constants.hgssTMDataPrefix;
        }
        int offset = find(arm9, tmDataPrefix);
        if (offset > 0) {
            offset += tmDataPrefix.length() / 2; // because it was a prefix
            List<Integer> tms = new ArrayList<>();
            for (int i = 0; i < Gen4Constants.tmCount; i++) {
                tms.add(readWord(arm9, offset + i * 2));
            }
            return tms;
        } else {
            return null;
        }
    }

    @Override
    public List<Integer> getHMMoves() {
        String tmDataPrefix;
        if (romEntry.romType == Gen4Constants.Type_DP || romEntry.romType == Gen4Constants.Type_Plat) {
            tmDataPrefix = Gen4Constants.dpptTMDataPrefix;
        } else {
            tmDataPrefix = Gen4Constants.hgssTMDataPrefix;
        }
        int offset = find(arm9, tmDataPrefix);
        if (offset > 0) {
            offset += tmDataPrefix.length() / 2; // because it was a prefix
            offset += Gen4Constants.tmCount * 2; // TM data
            List<Integer> hms = new ArrayList<>();
            for (int i = 0; i < Gen4Constants.hmCount; i++) {
                hms.add(readWord(arm9, offset + i * 2));
            }
            return hms;
        } else {
            return null;
        }
    }

    @Override
    public void setTMMoves(List<Integer> moveIndexes) {
        List<Integer> oldMoveIndexes = this.getTMMoves();
        String tmDataPrefix;
        if (romEntry.romType == Gen4Constants.Type_DP || romEntry.romType == Gen4Constants.Type_Plat) {
            tmDataPrefix = Gen4Constants.dpptTMDataPrefix;
        } else {
            tmDataPrefix = Gen4Constants.hgssTMDataPrefix;
        }
        int offset = find(arm9, tmDataPrefix);
        if (offset > 0) {
            offset += tmDataPrefix.length() / 2; // because it was a prefix
            for (int i = 0; i < Gen4Constants.tmCount; i++) {
                writeWord(arm9, offset + i * 2, moveIndexes.get(i));
            }

            // Update TM item descriptions
            List<String> itemDescriptions = getStrings(romEntry.getInt("ItemDescriptionsTextOffset"));
            List<String> moveDescriptions = getStrings(romEntry.getInt("MoveDescriptionsTextOffset"));
            int textCharsPerLine = Gen4Constants.getTextCharsPerLine(romEntry.romType);
            // TM01 is item 328 and so on
            for (int i = 0; i < Gen4Constants.tmCount; i++) {
                // Rewrite 5-line move descs into 3-line item descs
                itemDescriptions.set(i + Gen4Constants.tmItemOffset, RomFunctions.rewriteDescriptionForNewLineSize(
                        moveDescriptions.get(moveIndexes.get(i)), "\\n", textCharsPerLine, ssd));
            }
            // Save the new item descriptions
            setStrings(romEntry.getInt("ItemDescriptionsTextOffset"), itemDescriptions);
            // Palettes update
            String baseOfPalettes = Gen4Constants.pthgssItemPalettesPrefix;
            if (romEntry.romType == Gen4Constants.Type_DP) {
                baseOfPalettes = Gen4Constants.dpItemPalettesPrefix;
            }
            int offsPals = find(arm9, baseOfPalettes);
            if (offsPals > 0) {
                // Write pals
                for (int i = 0; i < Gen4Constants.tmCount; i++) {
                    Move m = this.moves[moveIndexes.get(i)];
                    int pal = this.typeTMPaletteNumber(m.type);
                    writeWord(arm9, offsPals + i * 8 + 2, pal);
                }
            }
            // if we can't update the palettes, it's not a big deal...

            // Update TM Text
            for (int i = 0; i < Gen4Constants.tmCount; i++) {
                int oldMoveIndex = oldMoveIndexes.get(i);
                int newMoveIndex = moveIndexes.get(i);
                int tmNumber = i + 1;

                if (romEntry.tmTexts.containsKey(tmNumber)) {
                    List<TextEntry> textEntries = romEntry.tmTexts.get(tmNumber);
                    Set<Integer> textFiles = new HashSet<>();
                    for (TextEntry textEntry : textEntries) {
                        textFiles.add(textEntry.textIndex);
                    }
                    String oldMoveName = moves[oldMoveIndex].name;
                    String newMoveName = moves[newMoveIndex].name;
                    if (romEntry.romType == Gen4Constants.Type_HGSS && oldMoveIndex == Moves.roar) {
                        // It's somewhat dumb to even be bothering with this, but it's too silly not to do
                        oldMoveName = oldMoveName.toUpperCase();
                        newMoveName = newMoveName.toUpperCase();
                    }
                    Map<String, String> replacements = new TreeMap<>();
                    replacements.put(oldMoveName, newMoveName);
                    for (int textFile : textFiles) {
                        replaceAllStringsInEntry(textFile, replacements);
                    }
                }

                if (romEntry.tmTextsGameCorner.containsKey(tmNumber)) {
                    TextEntry textEntry = romEntry.tmTextsGameCorner.get(tmNumber);
                    setBottomScreenTMText(textEntry.textIndex, textEntry.stringNumber, newMoveIndex);
                }

                if (romEntry.tmScriptOffsetsFrontier.containsKey(tmNumber)) {
                    int scriptFile = romEntry.getInt("FrontierScriptNumber");
                    byte[] frontierScript = scriptNarc.files.get(scriptFile);
                    int scriptOffset = romEntry.tmScriptOffsetsFrontier.get(tmNumber);
                    writeWord(frontierScript, scriptOffset, newMoveIndex);
                    scriptNarc.files.set(scriptFile, frontierScript);
                }

                if (romEntry.tmTextsFrontier.containsKey(tmNumber)) {
                    int textOffset = romEntry.getInt("MiscUITextOffset");
                    int stringNumber = romEntry.tmTextsFrontier.get(tmNumber);
                    setBottomScreenTMText(textOffset, stringNumber, newMoveIndex);
                }
            }
        }
    }

    private void setBottomScreenTMText(int textOffset, int stringNumber, int newMoveIndex) {
        List<String> strings = getStrings(textOffset);
        String originalString = strings.get(stringNumber);

        // The first thing after the name is "\n".
        int postNameIndex = originalString.indexOf("\\");
        String originalName = originalString.substring(0, postNameIndex);

        // Some languages (like English) write the name in ALL CAPS, others don't.
        // Check if the original is ALL CAPS and then match it for consistency.
        boolean isAllCaps = originalName.equals(originalName.toUpperCase());
        String newName = moves[newMoveIndex].name;
        if (isAllCaps) {
            newName = newName.toUpperCase();
        }
        String newString = newName + originalString.substring(postNameIndex);
        strings.set(stringNumber, newString);
        setStrings(textOffset, strings);
    }

    private static RomFunctions.StringSizeDeterminer ssd = new RomFunctions.StringLengthSD();

    @Override
    public int getTMCount() {
        return Gen4Constants.tmCount;
    }

    @Override
    public int getHMCount() {
        return Gen4Constants.hmCount;
    }

    @Override
    public Map<Pokemon, boolean[]> getTMHMCompatibility() {
        Map<Pokemon, boolean[]> compat = new TreeMap<>();
        int formeCount = Gen4Constants.getFormeCount(romEntry.romType);
        for (int i = 1; i <= Gen4Constants.pokemonCount + formeCount; i++) {
            byte[] data;
            if (i > Gen4Constants.pokemonCount) {
                data = pokeNarc.files.get(i + Gen4Constants.formeOffset);
            } else {
                data = pokeNarc.files.get(i);
            }
            Pokemon pkmn = pokes[i];
            boolean[] flags = new boolean[Gen4Constants.tmCount + Gen4Constants.hmCount + 1];
            for (int j = 0; j < 13; j++) {
                readByteIntoFlags(data, flags, j * 8 + 1, Gen4Constants.bsTMHMCompatOffset + j);
            }
            compat.put(pkmn, flags);
        }
        return compat;
    }

    @Override
    public void setTMHMCompatibility(Map<Pokemon, boolean[]> compatData) {
        for (Map.Entry<Pokemon, boolean[]> compatEntry : compatData.entrySet()) {
            Pokemon pkmn = compatEntry.getKey();
            boolean[] flags = compatEntry.getValue();
            byte[] data = pokeNarc.files.get(pkmn.number);
            for (int j = 0; j < 13; j++) {
                data[Gen4Constants.bsTMHMCompatOffset + j] = getByteFromFlags(flags, j * 8 + 1);
            }
        }
    }

    @Override
    public boolean hasMoveTutors() {
        return romEntry.romType != Gen4Constants.Type_DP;
    }

    @Override
    public List<Integer> getMoveTutorMoves() {
        if (!hasMoveTutors()) {
            return new ArrayList<>();
        }
        int baseOffset = romEntry.getInt("MoveTutorMovesOffset");
        int amount = romEntry.getInt("MoveTutorCount");
        int bytesPer = romEntry.getInt("MoveTutorBytesCount");
        List<Integer> mtMoves = new ArrayList<>();
        try {
            byte[] mtFile = readOverlay(romEntry.getInt("FieldOvlNumber"));
            for (int i = 0; i < amount; i++) {
                mtMoves.add(readWord(mtFile, baseOffset + i * bytesPer));
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return mtMoves;
    }

    @Override
    public void setMoveTutorMoves(List<Integer> moves) {
        if (!hasMoveTutors()) {
            return;
        }
        int baseOffset = romEntry.getInt("MoveTutorMovesOffset");
        int amount = romEntry.getInt("MoveTutorCount");
        int bytesPer = romEntry.getInt("MoveTutorBytesCount");
        if (moves.size() != amount) {
            return;
        }
        try {
            byte[] mtFile = readOverlay(romEntry.getInt("FieldOvlNumber"));
            for (int i = 0; i < amount; i++) {
                writeWord(mtFile, baseOffset + i * bytesPer, moves.get(i));
            }
            writeOverlay(romEntry.getInt("FieldOvlNumber"), mtFile);

            // In HGSS, Headbutt is the last tutor move, but the tutor teaches it
            // to you via a hardcoded script rather than looking at this data
            if (romEntry.romType == Gen4Constants.Type_HGSS) {
                setHGSSHeadbuttTutor(moves.get(moves.size() - 1));
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void setHGSSHeadbuttTutor(int headbuttReplacement) {
        byte[] ilexForestScripts = scriptNarc.files.get(Gen4Constants.ilexForestScriptFile);
        for (int offset : Gen4Constants.headbuttTutorScriptOffsets) {
            writeWord(ilexForestScripts, offset, headbuttReplacement);
        }

        String replacementName = moves[headbuttReplacement].name;
        Map<String, String> replacements = new TreeMap<>();
        replacements.put(moves[Moves.headbutt].name, replacementName);
        replaceAllStringsInEntry(Gen4Constants.ilexForestStringsFile, replacements);
    }

    @Override
    public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
        if (!hasMoveTutors()) {
            return new TreeMap<>();
        }
        Map<Pokemon, boolean[]> compat = new TreeMap<>();
        int amount = romEntry.getInt("MoveTutorCount");
        int baseOffset = romEntry.getInt("MoveTutorCompatOffset");
        int bytesPer = romEntry.getInt("MoveTutorCompatBytesCount");
        try {
            byte[] mtcFile;
            if (romEntry.romType == Gen4Constants.Type_HGSS) {
                mtcFile = readFile(romEntry.getFile("MoveTutorCompat"));
            } else {
                mtcFile = readOverlay(romEntry.getInt("MoveTutorCompatOvlNumber"));
            }
            int formeCount = Gen4Constants.getFormeCount(romEntry.romType);
            for (int i = 1; i <= Gen4Constants.pokemonCount + formeCount; i++) {
                Pokemon pkmn = pokes[i];
                boolean[] flags = new boolean[amount + 1];
                for (int j = 0; j < bytesPer; j++) {
                    if (i > Gen4Constants.pokemonCount) {
                        readByteIntoFlags(mtcFile, flags, j * 8 + 1, baseOffset + (i - 1) * bytesPer + j);
                    } else {
                        readByteIntoFlags(mtcFile, flags, j * 8 + 1, baseOffset + (i - 1) * bytesPer + j);
                    }
                }
                compat.put(pkmn, flags);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return compat;
    }

    @Override
    public void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData) {
        if (!hasMoveTutors()) {
            return;
        }
        int amount = romEntry.getInt("MoveTutorCount");
        int baseOffset = romEntry.getInt("MoveTutorCompatOffset");
        int bytesPer = romEntry.getInt("MoveTutorCompatBytesCount");
        try {
            byte[] mtcFile;
            if (romEntry.romType == Gen4Constants.Type_HGSS) {
                mtcFile = readFile(romEntry.getFile("MoveTutorCompat"));
            } else {
                mtcFile = readOverlay(romEntry.getInt("MoveTutorCompatOvlNumber"));
            }
            for (Map.Entry<Pokemon, boolean[]> compatEntry : compatData.entrySet()) {
                Pokemon pkmn = compatEntry.getKey();
                boolean[] flags = compatEntry.getValue();
                for (int j = 0; j < bytesPer; j++) {
                    int offsHere = baseOffset + (pkmn.number - 1) * bytesPer + j;
                    if (j * 8 + 8 <= amount) {
                        // entirely new byte
                        mtcFile[offsHere] = getByteFromFlags(flags, j * 8 + 1);
                    } else if (j * 8 < amount) {
                        // need some of the original byte
                        int newByte = getByteFromFlags(flags, j * 8 + 1) & 0xFF;
                        int oldByteParts = (mtcFile[offsHere] >>> (8 - amount + j * 8)) << (8 - amount + j * 8);
                        mtcFile[offsHere] = (byte) (newByte | oldByteParts);
                    }
                    // else do nothing to the byte
                }
            }
            if (romEntry.romType == Gen4Constants.Type_HGSS) {
                writeFile(romEntry.getFile("MoveTutorCompat"), mtcFile);
            } else {
                writeOverlay(romEntry.getInt("MoveTutorCompatOvlNumber"), mtcFile);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private int find(byte[] data, String hexString) {
        if (hexString.length() % 2 != 0) {
            return -3; // error
        }
        byte[] searchFor = new byte[hexString.length() / 2];
        for (int i = 0; i < searchFor.length; i++) {
            searchFor[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
        }
        List<Integer> found = RomFunctions.search(data, searchFor);
        if (found.size() == 0) {
            return -1; // not found
        } else if (found.size() > 1) {
            return -2; // not unique
        } else {
            return found.get(0);
        }
    }

    private boolean lastStringsCompressed = false;

    private List<String> getStrings(int index) {
        PokeTextData pt = new PokeTextData(msgNarc.files.get(index));
        pt.decrypt();
        lastStringsCompressed = pt.compressFlag;
        return new ArrayList<>(pt.strlist);
    }

    private void setStrings(int index, List<String> newStrings) {
        setStrings(index, newStrings, false);
    }

    private void setStrings(int index, List<String> newStrings, boolean compressed) {
        byte[] rawUnencrypted = TextToPoke.MakeFile(newStrings, compressed);

        // make new encrypted name set
        PokeTextData encrypt = new PokeTextData(rawUnencrypted);
        encrypt.SetKey(0xD00E);
        encrypt.encrypt();

        // rewrite
        msgNarc.files.set(index, encrypt.get());
    }

    @Override
    public String getROMName() {
        return "Pokemon " + romEntry.name;
    }

    @Override
    public String getROMCode() {
        return romEntry.romCode;
    }

    @Override
    public String getSupportLevel() {
        return romEntry.staticPokemonSupport ? "Complete" : "No Static Pokemon";
    }

    @Override
    public boolean hasTimeBasedEncounters() {
        // dppt technically do but we ignore them completely
        return romEntry.romType == Gen4Constants.Type_HGSS;
    }

    @Override
    public boolean hasWildAltFormes() {
        return false;
    }

    @Override
    public boolean canChangeStaticPokemon() {
        return romEntry.staticPokemonSupport;
    }

    @Override
    public boolean hasStaticAltFormes() {
        return false;
    }

    @Override
    public boolean hasMainGameLegendaries() {
        return true;
    }

    @Override
    public List<Integer> getMainGameLegendaries() {
        return Arrays.stream(romEntry.arrayEntries.get("MainGameLegendaries")).boxed().collect(Collectors.toList());
    }

    @Override
    public List<Integer> getSpecialMusicStatics() {
        return Arrays.stream(romEntry.arrayEntries.get("SpecialMusicStatics")).boxed().collect(Collectors.toList());
    }

    @Override
    public List<TotemPokemon> getTotemPokemon() {
        return new ArrayList<>();
    }

    @Override
    public void setTotemPokemon(List<TotemPokemon> totemPokemon) {

    }

    @Override
    public boolean hasStarterAltFormes() {
        return false;
    }

    @Override
    public int starterCount() {
        return 3;
    }

    @Override
    public Map<Integer, StatChange> getUpdatedPokemonStats(int generation) {
        return GlobalConstants.getStatChanges(generation);
    }

    private void populateEvolutions() {
        for (Pokemon pkmn : pokes) {
            if (pkmn != null) {
                pkmn.evolutionsFrom.clear();
                pkmn.evolutionsTo.clear();
            }
        }

        // Read NARC
        try {
            NARCArchive evoNARC = readNARC(romEntry.getFile("PokemonEvolutions"));
            for (int i = 1; i <= Gen4Constants.pokemonCount; i++) {
                Pokemon pk = pokes[i];
                byte[] evoEntry = evoNARC.files.get(i);
                for (int evo = 0; evo < 7; evo++) {
                    int method = readWord(evoEntry, evo * 6);
                    int species = readWord(evoEntry, evo * 6 + 4);
                    if (method >= 1 && method <= Gen4Constants.evolutionMethodCount && species >= 1) {
                        EvolutionType et = EvolutionType.fromIndex(4, method);
                        int extraInfo = readWord(evoEntry, evo * 6 + 2);
                        Evolution evol = new Evolution(pokes[i], pokes[species], true, et, extraInfo);
                        if (!pk.evolutionsFrom.contains(evol)) {
                            pk.evolutionsFrom.add(evol);
                            pokes[species].evolutionsTo.add(evol);
                        }
                    }
                }
                // Split evos shouldn't carry stats unless the evo is Nincada's
                // In that case, we should have Ninjask carry stats
                if (pk.evolutionsFrom.size() > 1) {
                    for (Evolution e : pk.evolutionsFrom) {
                        if (e.type != EvolutionType.LEVEL_CREATE_EXTRA) {
                            e.carryStats = false;
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void writeEvolutions() {
        try {
            NARCArchive evoNARC = readNARC(romEntry.getFile("PokemonEvolutions"));
            for (int i = 1; i <= Gen4Constants.pokemonCount; i++) {
                byte[] evoEntry = evoNARC.files.get(i);
                Pokemon pk = pokes[i];
                if (pk.number == Species.nincada) {
                    writeShedinjaEvolution();
                }
                int evosWritten = 0;
                for (Evolution evo : pk.evolutionsFrom) {
                    writeWord(evoEntry, evosWritten * 6, evo.type.toIndex(4));
                    writeWord(evoEntry, evosWritten * 6 + 2, evo.extraInfo);
                    writeWord(evoEntry, evosWritten * 6 + 4, evo.to.number);
                    evosWritten++;
                    if (evosWritten == 7) {
                        break;
                    }
                }
                while (evosWritten < 7) {
                    writeWord(evoEntry, evosWritten * 6, 0);
                    writeWord(evoEntry, evosWritten * 6 + 2, 0);
                    writeWord(evoEntry, evosWritten * 6 + 4, 0);
                    evosWritten++;
                }
            }
            writeNARC(romEntry.getFile("PokemonEvolutions"), evoNARC);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void writeShedinjaEvolution() {
        Pokemon nincada = pokes[Species.nincada];

        // When the "Limit Pokemon" setting is enabled and Gen 3 is disabled, or when
        // "Random Every Level" evolutions are selected, we end up clearing out Nincada's
        // vanilla evolutions. In that case, there's no point in even worrying about
        // Shedinja, so just return.
        if (nincada.evolutionsFrom.size() < 2) {
            return;
        }
        Pokemon extraEvolution = nincada.evolutionsFrom.get(1).to;

        // In all the Gen 4 games, the game is hardcoded to check for
        // the LEVEL_IS_EXTRA evolution method; if it the Pokemon has it,
        // then a harcoded Shedinja is generated after every evolution
        // by using the following instructions:
        // mov r0, #0x49
        // lsl r0, r0, #2
        // The below code tweaks this instruction to load the species ID of Nincada's
        // new extra evolution into r0 using an 8-bit addition. Since Gen 4 has fewer
        // than 510 species in it, this will always succeed.
        int offset = find(arm9, Gen4Constants.shedinjaSpeciesLocator);
        if (offset > 0) {
            int lowByte, highByte;
            if (extraEvolution.number < 256) {
                lowByte = extraEvolution.number;
                highByte = 0;
            } else {
                lowByte = 255;
                highByte = extraEvolution.number - 255;
            }

            // mov r0, lowByte
            // add r0, r0, highByte
            arm9[offset] = (byte) lowByte;
            arm9[offset + 1] = 0x20;
            arm9[offset + 2] = (byte) highByte;
            arm9[offset + 3] = 0x30;
        }
    }

    @Override
    public void removeImpossibleEvolutions(Settings settings) {
        boolean changeMoveEvos = !(settings.getMovesetsMod() == Settings.MovesetsMod.UNCHANGED);

        Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();
        Set<Evolution> extraEvolutions = new HashSet<>();
        for (Pokemon pkmn : pokes) {
            if (pkmn != null) {
                extraEvolutions.clear();
                for (Evolution evo : pkmn.evolutionsFrom) {
                    // new 160 other impossible evolutions:
                    if (romEntry.romType == Gen4Constants.Type_HGSS) {
                        // beauty milotic
                        if (evo.type == EvolutionType.LEVEL_HIGH_BEAUTY) {
                            // Replace w/ level 35
                            evo.type = EvolutionType.LEVEL;
                            evo.extraInfo = 35;
                            addEvoUpdateLevel(impossibleEvolutionUpdates, evo);
                        }
                        // mt.coronet (magnezone/probopass)
                        if (evo.type == EvolutionType.LEVEL_ELECTRIFIED_AREA) {
                            // Replace w/ level 40
                            evo.type = EvolutionType.LEVEL;
                            evo.extraInfo = 40;
                            addEvoUpdateLevel(impossibleEvolutionUpdates, evo);
                        }
                        // moss rock (leafeon)
                        if (evo.type == EvolutionType.LEVEL_MOSS_ROCK) {
                            // Replace w/ leaf stone
                            evo.type = EvolutionType.STONE;
                            evo.extraInfo = Items.leafStone;
                            addEvoUpdateStone(impossibleEvolutionUpdates, evo, itemNames.get(evo.extraInfo));
                        }
                        // icy rock (glaceon)
                        if (evo.type == EvolutionType.LEVEL_ICY_ROCK) {
                            // Replace w/ dawn stone
                            evo.type = EvolutionType.STONE;
                            evo.extraInfo = Items.dawnStone;
                            addEvoUpdateStone(impossibleEvolutionUpdates, evo, itemNames.get(evo.extraInfo));
                        }
                    }
                    if (changeMoveEvos && evo.type == EvolutionType.LEVEL_WITH_MOVE) {
                        // read move
                        int move = evo.extraInfo;
                        int levelLearntAt = 1;
                        for (MoveLearnt ml : movesets.get(evo.from.number)) {
                            if (ml.move == move) {
                                levelLearntAt = ml.level;
                                break;
                            }
                        }
                        if (levelLearntAt == 1) {
                            // override for piloswine
                            levelLearntAt = 45;
                        }
                        // change to pure level evo
                        evo.type = EvolutionType.LEVEL;
                        evo.extraInfo = levelLearntAt;
                        addEvoUpdateLevel(impossibleEvolutionUpdates, evo);
                    }
                    // Pure Trade
                    if (evo.type == EvolutionType.TRADE) {
                        // Replace w/ level 37
                        evo.type = EvolutionType.LEVEL;
                        evo.extraInfo = 37;
                        addEvoUpdateLevel(impossibleEvolutionUpdates, evo);
                    }
                    // Trade w/ Item
                    if (evo.type == EvolutionType.TRADE_ITEM) {
                        // Get the current item & evolution
                        int item = evo.extraInfo;
                        if (evo.from.number == Species.slowpoke) {
                            // Slowpoke is awkward - he already has a level evo
                            // So we can't do Level up w/ Held Item for him
                            // Put Water Stone instead
                            evo.type = EvolutionType.STONE;
                            evo.extraInfo = Items.waterStone;
                            addEvoUpdateStone(impossibleEvolutionUpdates, evo, itemNames.get(evo.extraInfo));
                        } else {
                            addEvoUpdateHeldItem(impossibleEvolutionUpdates, evo, itemNames.get(item));
                            // Replace, for this entry, w/
                            // Level up w/ Held Item at Day
                            evo.type = EvolutionType.LEVEL_ITEM_DAY;
                            // now add an extra evo for
                            // Level up w/ Held Item at Night
                            Evolution extraEntry = new Evolution(evo.from, evo.to, true,
                                    EvolutionType.LEVEL_ITEM_NIGHT, item);
                            extraEvolutions.add(extraEntry);
                        }
                    }
                }
                pkmn.evolutionsFrom.addAll(extraEvolutions);
                for (Evolution ev : extraEvolutions) {
                    ev.to.evolutionsTo.add(ev);
                }
            }
        }

    }

    @Override
    public void makeEvolutionsEasier(Settings settings) {
        boolean wildsRandomized = !settings.getWildPokemonMod().equals(Settings.WildPokemonMod.UNCHANGED);

        // Reduce the amount of happiness required to evolve.
        int offset = find(arm9, Gen4Constants.friendshipValueForEvoLocator);
        if (offset > 0) {
            // Amount of required happiness for HAPPINESS evolutions.
            if (arm9[offset] == (byte)220) {
                arm9[offset] = (byte)160;
            }
            // Amount of required happiness for HAPPINESS_DAY evolutions.
            if (arm9[offset + 22] == (byte)220) {
                arm9[offset + 22] = (byte)160;
            }
            // Amount of required happiness for HAPPINESS_NIGHT evolutions.
            if (arm9[offset + 44] == (byte)220) {
                arm9[offset + 44] = (byte)160;
            }
        }

        if (wildsRandomized) {
            for (Pokemon pkmn : pokes) {
                if (pkmn != null) {
                    for (Evolution evo : pkmn.evolutionsFrom) {
                        if (evo.type == EvolutionType.LEVEL_WITH_OTHER) {
                            // Replace w/ level 35
                            evo.type = EvolutionType.LEVEL;
                            evo.extraInfo = 35;
                            addEvoUpdateCondensed(easierEvolutionUpdates, evo, false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void removeTimeBasedEvolutions() {
        Set<Evolution> extraEvolutions = new HashSet<>();
        for (Pokemon pkmn : pokes) {
            if (pkmn != null) {
                extraEvolutions.clear();
                for (Evolution evo : pkmn.evolutionsFrom) {
                    if (evo.type == EvolutionType.HAPPINESS_DAY) {
                        if (evo.from.number == Species.eevee) {
                            // We can't set Eevee to evolve into Espeon with happiness at night because that's how
                            // Umbreon works in the original game. Instead, make Eevee: == sun stone => Espeon
                            evo.type = EvolutionType.STONE;
                            evo.extraInfo = Items.sunStone;
                            addEvoUpdateStone(timeBasedEvolutionUpdates, evo, itemNames.get(evo.extraInfo));
                        } else {
                            // Add an extra evo for Happiness at Night
                            addEvoUpdateHappiness(timeBasedEvolutionUpdates, evo);
                            Evolution extraEntry = new Evolution(evo.from, evo.to, true,
                                    EvolutionType.HAPPINESS_NIGHT, 0);
                            extraEvolutions.add(extraEntry);
                        }
                    } else if (evo.type == EvolutionType.HAPPINESS_NIGHT) {
                        if (evo.from.number == Species.eevee) {
                            // We can't set Eevee to evolve into Umbreon with happiness at day because that's how
                            // Espeon works in the original game. Instead, make Eevee: == moon stone => Umbreon
                            evo.type = EvolutionType.STONE;
                            evo.extraInfo = Items.moonStone;
                            addEvoUpdateStone(timeBasedEvolutionUpdates, evo, itemNames.get(evo.extraInfo));
                        } else {
                            // Add an extra evo for Happiness at Day
                            addEvoUpdateHappiness(timeBasedEvolutionUpdates, evo);
                            Evolution extraEntry = new Evolution(evo.from, evo.to, true,
                                    EvolutionType.HAPPINESS_DAY, 0);
                            extraEvolutions.add(extraEntry);
                        }
                    } else if (evo.type == EvolutionType.LEVEL_ITEM_DAY) {
                        int item = evo.extraInfo;
                        // Make sure we don't already have an evo for the same item at night (e.g., when using Change Impossible Evos)
                        if (evo.from.evolutionsFrom.stream().noneMatch(e -> e.type == EvolutionType.LEVEL_ITEM_NIGHT && e.extraInfo == item)) {
                            // Add an extra evo for Level w/ Item During Night
                            addEvoUpdateHeldItem(timeBasedEvolutionUpdates, evo, itemNames.get(item));
                            Evolution extraEntry = new Evolution(evo.from, evo.to, true,
                                    EvolutionType.LEVEL_ITEM_NIGHT, item);
                            extraEvolutions.add(extraEntry);
                        }
                    } else if (evo.type == EvolutionType.LEVEL_ITEM_NIGHT) {
                        int item = evo.extraInfo;
                        // Make sure we don't already have an evo for the same item at day (e.g., when using Change Impossible Evos)
                        if (evo.from.evolutionsFrom.stream().noneMatch(e -> e.type == EvolutionType.LEVEL_ITEM_DAY && e.extraInfo == item)) {
                            // Add an extra evo for Level w/ Item During Day
                            addEvoUpdateHeldItem(timeBasedEvolutionUpdates, evo, itemNames.get(item));
                            Evolution extraEntry = new Evolution(evo.from, evo.to, true,
                                    EvolutionType.LEVEL_ITEM_DAY, item);
                            extraEvolutions.add(extraEntry);
                        }
                    }
                }
                pkmn.evolutionsFrom.addAll(extraEvolutions);
                for (Evolution ev : extraEvolutions) {
                    ev.to.evolutionsTo.add(ev);
                }
            }
        }

    }

    @Override
    public boolean hasShopRandomization() {
        return true;
    }

    @Override
    public Map<Integer, Shop> getShopItems() {
        List<String> shopNames = Gen4Constants.getShopNames(romEntry.romType);
        List<Integer> mainGameShops = Arrays.stream(romEntry.arrayEntries.get("MainGameShops")).boxed().collect(Collectors.toList());
        List<Integer> skipShops = Arrays.stream(romEntry.arrayEntries.get("SkipShops")).boxed().collect(Collectors.toList());
        int shopCount = romEntry.getInt("ShopCount");
        Map<Integer, Shop> shopItemsMap = new TreeMap<>();
        String shopDataPrefix = romEntry.getString("ShopDataPrefix");
        int offset = find(arm9,shopDataPrefix);
        offset += shopDataPrefix.length() / 2;

        for (int i = 0; i < shopCount; i++) {
            if (!skipShops.contains(i)) {
                List<Integer> items = new ArrayList<>();
                int val = (FileFunctions.read2ByteInt(arm9, offset));
                while ((val & 0xFFFF) != 0xFFFF) {
                    if (val != 0) {
                        items.add(val);
                    }
                    offset += 2;
                    val = (FileFunctions.read2ByteInt(arm9, offset));
                }
                offset += 2;
                Shop shop = new Shop();
                shop.items = items;
                shop.name = shopNames.get(i);
                shop.isMainGame = mainGameShops.contains(i);
                shopItemsMap.put(i, shop);
            } else {
                while ((FileFunctions.read2ByteInt(arm9, offset) & 0xFFFF) != 0xFFFF) {
                    offset += 2;
                }
                offset += 2;
            }
        }
        return shopItemsMap;
    }

    @Override
    public void setShopItems(Map<Integer, Shop> shopItems) {
        int shopCount = romEntry.getInt("ShopCount");
        String shopDataPrefix = romEntry.getString("ShopDataPrefix");
        int offset = find(arm9,shopDataPrefix);
        offset += shopDataPrefix.length() / 2;

        for (int i = 0; i < shopCount; i++) {
            Shop thisShop = shopItems.get(i);
            if (thisShop == null || thisShop.items == null) {
                while ((FileFunctions.read2ByteInt(arm9, offset) & 0xFFFF) != 0xFFFF) {
                    offset += 2;
                }
                offset += 2;
                continue;
            }
            Iterator<Integer> iterItems = thisShop.items.iterator();
            int val = (FileFunctions.read2ByteInt(arm9, offset));
            while ((val & 0xFFFF) != 0xFFFF) {
                if (val != 0) {
                    FileFunctions.write2ByteInt(arm9,offset,iterItems.next());
                }
                offset += 2;
                val = (FileFunctions.read2ByteInt(arm9, offset));
            }
            offset += 2;
        }
    }

    @Override
    public void setShopPrices() {
        try {
            // In Diamond and Pearl, item IDs 112 through 134 are unused. In Platinum and HGSS, item ID 112 is used for
            // the Griseous Orb. So we need to skip through the unused IDs at different points depending on the game.
            int startOfUnusedIDs = romEntry.romType == Gen4Constants.Type_DP ? 112 : 113;
            NARCArchive itemPriceNarc = this.readNARC(romEntry.getFile("ItemData"));
            int itemID = 1;
            for (int i = 1; i < itemPriceNarc.files.size(); i++) {
                writeWord(itemPriceNarc.files.get(i),0,Gen4Constants.balancedItemPrices.get(itemID) * 10);
                itemID++;
                if (itemID == startOfUnusedIDs) {
                    itemID = 135;
                }
            }
            writeNARC(romEntry.getFile("ItemData"),itemPriceNarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public List<PickupItem> getPickupItems() {
        List<PickupItem> pickupItems = new ArrayList<>();
        try {
            byte[] battleOverlay = readOverlay(romEntry.getInt("BattleOvlNumber"));
            if (pickupItemsTableOffset == 0) {
                int offset = find(battleOverlay, Gen4Constants.pickupTableLocator);
                if (offset > 0) {
                    pickupItemsTableOffset = offset;
                }
            }

            // If we haven't found the pickup table for this ROM already, find it.
            if (rarePickupItemsTableOffset == 0) {
                int offset = find(battleOverlay, Gen4Constants.rarePickupTableLocator);
                if (offset > 0) {
                    rarePickupItemsTableOffset = offset;
                }
            }

            // Assuming we've found the pickup table, extract the items out of it.
            if (pickupItemsTableOffset > 0 && rarePickupItemsTableOffset > 0) {
                for (int i = 0; i < Gen4Constants.numberOfCommonPickupItems; i++) {
                    int itemOffset = pickupItemsTableOffset + (2 * i);
                    int item = FileFunctions.read2ByteInt(battleOverlay, itemOffset);
                    PickupItem pickupItem = new PickupItem(item);
                    pickupItems.add(pickupItem);
                }
                for (int i = 0; i < Gen4Constants.numberOfRarePickupItems; i++) {
                    int itemOffset = rarePickupItemsTableOffset + (2 * i);
                    int item = FileFunctions.read2ByteInt(battleOverlay, itemOffset);
                    PickupItem pickupItem = new PickupItem(item);
                    pickupItems.add(pickupItem);
                }
            }

            // Assuming we got the items from the last step, fill out the probabilities.
            if (pickupItems.size() > 0) {
                for (int levelRange = 0; levelRange < 10; levelRange++) {
                    int startingCommonItemOffset = levelRange;
                    int startingRareItemOffset = 18 + levelRange;
                    pickupItems.get(startingCommonItemOffset).probabilities[levelRange] = 30;
                    for (int i = 1; i < 7; i++) {
                        pickupItems.get(startingCommonItemOffset + i).probabilities[levelRange] = 10;
                    }
                    pickupItems.get(startingCommonItemOffset + 7).probabilities[levelRange] = 4;
                    pickupItems.get(startingCommonItemOffset + 8).probabilities[levelRange] = 4;
                    pickupItems.get(startingRareItemOffset).probabilities[levelRange] = 1;
                    pickupItems.get(startingRareItemOffset + 1).probabilities[levelRange] = 1;
                }
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return pickupItems;
    }

    @Override
    public void setPickupItems(List<PickupItem> pickupItems) {
        try {
            if (pickupItemsTableOffset > 0 && rarePickupItemsTableOffset > 0) {
                byte[] battleOverlay = readOverlay(romEntry.getInt("BattleOvlNumber"));
                Iterator<PickupItem> itemIterator = pickupItems.iterator();
                for (int i = 0; i < Gen4Constants.numberOfCommonPickupItems; i++) {
                    int itemOffset = pickupItemsTableOffset + (2 * i);
                    int item = itemIterator.next().item;
                    FileFunctions.write2ByteInt(battleOverlay, itemOffset, item);
                }
                for (int i = 0; i < Gen4Constants.numberOfRarePickupItems; i++) {
                    int itemOffset = rarePickupItemsTableOffset + (2 * i);
                    int item = itemIterator.next().item;
                    FileFunctions.write2ByteInt(battleOverlay, itemOffset, item);
                }
                writeOverlay(romEntry.getInt("BattleOvlNumber"), battleOverlay);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public boolean canChangeTrainerText() {
        return true;
    }

    @Override
    public List<String> getTrainerNames() {
        List<String> tnames = new ArrayList<>(getStrings(romEntry.getInt("TrainerNamesTextOffset")));
        tnames.remove(0); // blank one
        for (int i = 0; i < tnames.size(); i++) {
            if (tnames.get(i).contains("\\and")) {
                tnames.set(i, tnames.get(i).replace("\\and", "&"));
            }
        }
        return tnames;
    }

    @Override
    public int maxTrainerNameLength() {
        return 10;// based off the english ROMs fixed
    }

    @Override
    public void setTrainerNames(List<String> trainerNames) {
        List<String> oldTNames = getStrings(romEntry.getInt("TrainerNamesTextOffset"));
        List<String> newTNames = new ArrayList<>(trainerNames);
        for (int i = 0; i < newTNames.size(); i++) {
            if (newTNames.get(i).contains("&")) {
                newTNames.set(i, newTNames.get(i).replace("&", "\\and"));
            }
        }
        newTNames.add(0, oldTNames.get(0)); // the 0-entry, preserve it

        // rewrite, only compressed if they were compressed before
        setStrings(romEntry.getInt("TrainerNamesTextOffset"), newTNames, lastStringsCompressed);

    }

    @Override
    public TrainerNameMode trainerNameMode() {
        return TrainerNameMode.MAX_LENGTH;
    }

    @Override
    public List<Integer> getTCNameLengthsByTrainer() {
        // not needed
        return new ArrayList<>();
    }

    @Override
    public List<String> getTrainerClassNames() {
        return getStrings(romEntry.getInt("TrainerClassesTextOffset"));
    }

    @Override
    public void setTrainerClassNames(List<String> trainerClassNames) {
        setStrings(romEntry.getInt("TrainerClassesTextOffset"), trainerClassNames);
    }

    @Override
    public int maxTrainerClassNameLength() {
        return 12;// based off the english ROMs
    }

    @Override
    public boolean fixedTrainerClassNamesLength() {
        return false;
    }

    @Override
    public List<Integer> getDoublesTrainerClasses() {
        int[] doublesClasses = romEntry.arrayEntries.get("DoublesTrainerClasses");
        List<Integer> doubles = new ArrayList<>();
        for (int tClass : doublesClasses) {
            doubles.add(tClass);
        }
        return doubles;
    }

    @Override
    public String getDefaultExtension() {
        return "nds";
    }

    @Override
    public int abilitiesPerPokemon() {
        return 2;
    }

    @Override
    public int highestAbilityIndex() {
        return Gen4Constants.highestAbilityIndex;
    }

    @Override
    public int internalStringLength(String string) {
        return string.length();
    }

    @Override
    public void randomizeIntroPokemon() {
        try {
            if (romEntry.romType == Gen4Constants.Type_DP || romEntry.romType == Gen4Constants.Type_Plat) {
                Pokemon introPokemon = randomPokemon();
                while (introPokemon.genderRatio == 0xFE) {
                    // This is a female-only Pokemon. Gen 4 has an annoying quirk where female-only Pokemon *need*
                    // to pass a special parameter into the function that loads Pokemon sprites; the game will
                    // softlock on native hardware otherwise. The way the compiler has optimized the intro Pokemon
                    // code makes it very hard to modify, so passing in this special parameter is difficult. Rather
                    // than attempt to patch this code, just reroll until it isn't female-only.
                    introPokemon = randomPokemon();
                }
                byte[] introOverlay = readOverlay(romEntry.getInt("IntroOvlNumber"));
                for (String prefix : Gen4Constants.dpptIntroPrefixes) {
                    int offset = find(introOverlay, prefix);
                    if (offset > 0) {
                        offset += prefix.length() / 2; // because it was a prefix
                        writeWord(introOverlay, offset, introPokemon.number);
                    }
                }
                writeOverlay(romEntry.getInt("IntroOvlNumber"), introOverlay);
            } else if (romEntry.romType == Gen4Constants.Type_HGSS) {
                // Modify the sprite used for Ethan/Lyra's Marill
                int marillReplacement = this.random.nextInt(548) + 297;
                while (Gen4Constants.hgssBannedOverworldPokemon.contains(marillReplacement)) {
                    marillReplacement = this.random.nextInt(548) + 297;
                }

                byte[] fieldOverlay = readOverlay(romEntry.getInt("FieldOvlNumber"));
                String prefix = Gen4Constants.lyraEthanMarillSpritePrefix;
                int offset = find(fieldOverlay, prefix);
                if (offset > 0) {
                    offset += prefix.length() / 2; // because it was a prefix
                    writeWord(fieldOverlay, offset, marillReplacement);
                    if (Gen4Constants.hgssBigOverworldPokemon.contains(marillReplacement)) {
                        // Write the constant to indicate it's big (0x208 | (20 << 10))
                        writeWord(fieldOverlay, offset + 2, 0x5208);
                    } else {
                        // Write the constant to indicate it's normal-sized (0x227 | (19 << 10))
                        writeWord(fieldOverlay, offset + 2, 0x4E27);
                    }
                }
                writeOverlay(romEntry.getInt("FieldOvlNumber"), fieldOverlay);

                // Now modify the Marill's cry in every script it appears in to ensure consistency
                int marillReplacementId = Gen4Constants.convertOverworldSpriteToSpecies(marillReplacement);
                for (ScriptEntry entry : romEntry.marillCryScriptEntries) {
                    byte[] script = scriptNarc.files.get(entry.scriptFile);
                    writeWord(script, entry.scriptOffset, marillReplacementId);
                    scriptNarc.files.set(entry.scriptFile, script);
                }

                // Modify the text too for additional consistency
                int[] textOffsets = romEntry.arrayEntries.get("MarillTextFiles");
                String originalSpeciesString = pokes[Species.marill].name.toUpperCase();
                String newSpeciesString = pokes[marillReplacementId].name;
                Map<String, String> replacements = new TreeMap<>();
                replacements.put(originalSpeciesString, newSpeciesString);
                for (int i = 0; i < textOffsets.length; i++) {
                    replaceAllStringsInEntry(textOffsets[i], replacements);
                }

                // Lastly, modify the catching tutorial to use the new Pokemon if we're capable of doing so
                if (romEntry.tweakFiles.containsKey("NewCatchingTutorialSubroutineTweak")) {
                    String catchingTutorialMonTablePrefix = romEntry.getString("CatchingTutorialMonTablePrefix");
                    offset = find(arm9, catchingTutorialMonTablePrefix);
                    if (offset > 0) {
                        offset += catchingTutorialMonTablePrefix.length() / 2; // because it was a prefix

                        // As part of our catching tutorial patch, the player Pokemon's ID is just pc-relative
                        // loaded, and offset is now pointing to it.
                        writeWord(arm9, offset, marillReplacementId);
                    }
                }
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public ItemList getAllowedItems() {
        return allowedItems;
    }

    @Override
    public ItemList getNonBadItems() {
        return nonBadItems;
    }

    @Override
    public List<Integer> getUniqueNoSellItems() {
        return new ArrayList<>();
    }

    @Override
    public List<Integer> getRegularShopItems() {
        return Gen4Constants.regularShopItems;
    }

    @Override
    public List<Integer> getOPShopItems() {
        return Gen4Constants.opShopItems;
    }

    @Override
    public String[] getItemNames() {
        return itemNames.toArray(new String[0]);
    }

    @Override
    public String abilityName(int number) {
        return abilityNames.get(number);
    }

    @Override
    public Map<Integer, List<Integer>> getAbilityVariations() {
        return Gen4Constants.abilityVariations;
    }

    @Override
    public List<Integer> getUselessAbilities() {
        return new ArrayList<>(Gen4Constants.uselessAbilities);
    }

    @Override
    public int getAbilityForTrainerPokemon(TrainerPokemon tp) {
        // In Gen 4, alt formes for Trainer Pokemon use the base forme's ability
        Pokemon pkmn = tp.pokemon;
        while (pkmn.baseForme != null) {
            pkmn = pkmn.baseForme;
        }

        if (romEntry.romType == Gen4Constants.Type_DP || romEntry.romType == Gen4Constants.Type_Plat) {
            // In DPPt, Trainer Pokemon *always* use the first Ability, no matter what
            return pkmn.ability1;
        } else {
            // In HGSS, Trainer Pokemon can specify which ability they want to use.
            return tp.abilitySlot == 2 ? pkmn.ability2 : pkmn.ability1;
        }
    }

    @Override
    public boolean hasMegaEvolutions() {
        return false;
    }

    private List<Integer> getFieldItems() {
        List<Integer> fieldItems = new ArrayList<>();
        // normal items
        int scriptFile = romEntry.getInt("ItemBallsScriptOffset");
        byte[] itemScripts = scriptNarc.files.get(scriptFile);
        int offset = 0;
        int skipTableOffset = 0;
        int[] skipTable = romEntry.arrayEntries.get("ItemBallsSkip");
        int setVar = romEntry.romType == Gen4Constants.Type_HGSS ? Gen4Constants.hgssSetVarScript
                : Gen4Constants.dpptSetVarScript;
        while (true) {
            int part1 = readWord(itemScripts, offset);
            if (part1 == Gen4Constants.scriptListTerminator) {
                // done
                break;
            }
            int offsetInFile = readRelativePointer(itemScripts, offset);
            offset += 4;
            if (skipTableOffset < skipTable.length && (skipTable[skipTableOffset] == (offset / 4) - 1)) {
                skipTableOffset++;
                continue;
            }
            int command = readWord(itemScripts, offsetInFile);
            int variable = readWord(itemScripts, offsetInFile + 2);
            if (command == setVar && variable == Gen4Constants.itemScriptVariable) {
                int item = readWord(itemScripts, offsetInFile + 4);
                fieldItems.add(item);
            }

        }

        // hidden items
        int hiTableOffset = romEntry.getInt("HiddenItemTableOffset");
        int hiTableLimit = romEntry.getInt("HiddenItemCount");
        for (int i = 0; i < hiTableLimit; i++) {
            int item = readWord(arm9, hiTableOffset + i * 8);
            fieldItems.add(item);
        }

        return fieldItems;
    }

    private void setFieldItems(List<Integer> fieldItems) {
        Iterator<Integer> iterItems = fieldItems.iterator();

        // normal items
        int scriptFile = romEntry.getInt("ItemBallsScriptOffset");
        byte[] itemScripts = scriptNarc.files.get(scriptFile);
        int offset = 0;
        int skipTableOffset = 0;
        int[] skipTable = romEntry.arrayEntries.get("ItemBallsSkip");
        int setVar = romEntry.romType == Gen4Constants.Type_HGSS ? Gen4Constants.hgssSetVarScript
                : Gen4Constants.dpptSetVarScript;
        while (true) {
            int part1 = readWord(itemScripts, offset);
            if (part1 == Gen4Constants.scriptListTerminator) {
                // done
                break;
            }
            int offsetInFile = readRelativePointer(itemScripts, offset);
            offset += 4;
            if (skipTableOffset < skipTable.length && (skipTable[skipTableOffset] == (offset / 4) - 1)) {
                skipTableOffset++;
                continue;
            }
            int command = readWord(itemScripts, offsetInFile);
            int variable = readWord(itemScripts, offsetInFile + 2);
            if (command == setVar && variable == Gen4Constants.itemScriptVariable) {
                int item = iterItems.next();
                writeWord(itemScripts, offsetInFile + 4, item);
            }
        }

        // hidden items
        int hiTableOffset = romEntry.getInt("HiddenItemTableOffset");
        int hiTableLimit = romEntry.getInt("HiddenItemCount");
        for (int i = 0; i < hiTableLimit; i++) {
            int item = iterItems.next();
            writeWord(arm9, hiTableOffset + i * 8, item);
        }
    }

    @Override
    public List<Integer> getRequiredFieldTMs() {
        if (romEntry.romType == Gen4Constants.Type_DP) {
            return Gen4Constants.dpRequiredFieldTMs;
        } else if (romEntry.romType == Gen4Constants.Type_Plat) {
            // same as DP just we have to keep the weather TMs
            return Gen4Constants.ptRequiredFieldTMs;
        }
        return new ArrayList<>();
    }

    @Override
    public List<Integer> getCurrentFieldTMs() {
        List<Integer> fieldItems = this.getFieldItems();
        List<Integer> fieldTMs = new ArrayList<>();

        for (int item : fieldItems) {
            if (Gen4Constants.allowedItems.isTM(item)) {
                fieldTMs.add(item - Gen4Constants.tmItemOffset + 1);
            }
        }

        return fieldTMs;
    }

    @Override
    public void setFieldTMs(List<Integer> fieldTMs) {
        List<Integer> fieldItems = this.getFieldItems();
        int fiLength = fieldItems.size();
        Iterator<Integer> iterTMs = fieldTMs.iterator();

        for (int i = 0; i < fiLength; i++) {
            int oldItem = fieldItems.get(i);
            if (Gen4Constants.allowedItems.isTM(oldItem)) {
                int newItem = iterTMs.next() + Gen4Constants.tmItemOffset - 1;
                fieldItems.set(i, newItem);
            }
        }

        this.setFieldItems(fieldItems);
    }

    @Override
    public List<Integer> getRegularFieldItems() {
        List<Integer> fieldItems = this.getFieldItems();
        List<Integer> fieldRegItems = new ArrayList<>();

        for (int item : fieldItems) {
            if (Gen4Constants.allowedItems.isAllowed(item) && !(Gen4Constants.allowedItems.isTM(item))) {
                fieldRegItems.add(item);
            }
        }

        return fieldRegItems;
    }

    @Override
    public void setRegularFieldItems(List<Integer> items) {
        List<Integer> fieldItems = this.getFieldItems();
        int fiLength = fieldItems.size();
        Iterator<Integer> iterNewItems = items.iterator();

        for (int i = 0; i < fiLength; i++) {
            int oldItem = fieldItems.get(i);
            if (!(Gen4Constants.allowedItems.isTM(oldItem)) && Gen4Constants.allowedItems.isAllowed(oldItem)) {
                int newItem = iterNewItems.next();
                fieldItems.set(i, newItem);
            }
        }

        this.setFieldItems(fieldItems);
    }

    @Override
    public List<IngameTrade> getIngameTrades() {
        List<IngameTrade> trades = new ArrayList<>();
        try {
            NARCArchive tradeNARC = this.readNARC(romEntry.getFile("InGameTrades"));
            int[] spTrades = new int[0];
            if (romEntry.arrayEntries.containsKey("StaticPokemonTrades")) {
                spTrades = romEntry.arrayEntries.get("StaticPokemonTrades");
            }
            List<String> tradeStrings = getStrings(romEntry.getInt("IngameTradesTextOffset"));
            int tradeCount = tradeNARC.files.size();
            for (int i = 0; i < tradeCount; i++) {
                boolean isSP = false;
                for (int spTrade : spTrades) {
                    if (spTrade == i) {
                        isSP = true;
                        break;
                    }
                }
                if (isSP) {
                    continue;
                }
                byte[] tfile = tradeNARC.files.get(i);
                IngameTrade trade = new IngameTrade();
                trade.nickname = tradeStrings.get(i);
                trade.givenPokemon = pokes[readLong(tfile, 0)];
                trade.ivs = new int[6];
                for (int iv = 0; iv < 6; iv++) {
                    trade.ivs[iv] = readLong(tfile, 4 + iv * 4);
                }
                trade.otId = readWord(tfile, 0x20);
                trade.otName = tradeStrings.get(i + tradeCount);
                trade.item = readLong(tfile, 0x3C);
                trade.requestedPokemon = pokes[readLong(tfile, 0x4C)];
                trades.add(trade);
            }
        } catch (IOException ex) {
            throw new RandomizerIOException(ex);
        }
        return trades;
    }

    @Override
    public void setIngameTrades(List<IngameTrade> trades) {
        int tradeOffset = 0;
        List<IngameTrade> oldTrades = this.getIngameTrades();
        try {
            NARCArchive tradeNARC = this.readNARC(romEntry.getFile("InGameTrades"));
            int[] spTrades = new int[0];
            if (romEntry.arrayEntries.containsKey("StaticPokemonTrades")) {
                spTrades = romEntry.arrayEntries.get("StaticPokemonTrades");
            }
            List<String> tradeStrings = getStrings(romEntry.getInt("IngameTradesTextOffset"));
            int tradeCount = tradeNARC.files.size();
            for (int i = 0; i < tradeCount; i++) {
                boolean isSP = false;
                for (int spTrade : spTrades) {
                    if (spTrade == i) {
                        isSP = true;
                        break;
                    }
                }
                if (isSP) {
                    continue;
                }
                byte[] tfile = tradeNARC.files.get(i);
                IngameTrade trade = trades.get(tradeOffset++);
                tradeStrings.set(i, trade.nickname);
                tradeStrings.set(i + tradeCount, trade.otName);
                writeLong(tfile, 0, trade.givenPokemon.number);
                for (int iv = 0; iv < 6; iv++) {
                    writeLong(tfile, 4 + iv * 4, trade.ivs[iv]);
                }
                writeWord(tfile, 0x20, trade.otId);
                writeLong(tfile, 0x3C, trade.item);
                writeLong(tfile, 0x4C, trade.requestedPokemon.number);
                if (tfile.length > 0x50) {
                    writeLong(tfile, 0x50, 0); // disable gender
                }
            }
            this.writeNARC(romEntry.getFile("InGameTrades"), tradeNARC);
            this.setStrings(romEntry.getInt("IngameTradesTextOffset"), tradeStrings);
            // update what the people say when they talk to you
            if (romEntry.arrayEntries.containsKey("IngameTradePersonTextOffsets")) {
                int[] textOffsets = romEntry.arrayEntries.get("IngameTradePersonTextOffsets");
                for (int trade = 0; trade < textOffsets.length; trade++) {
                    if (textOffsets[trade] > 0) {
                        if (trade >= oldTrades.size() || trade >= trades.size()) {
                            break;
                        }
                        IngameTrade oldTrade = oldTrades.get(trade);
                        IngameTrade newTrade = trades.get(trade);
                        Map<String, String> replacements = new TreeMap<>();
                        replacements.put(oldTrade.givenPokemon.name.toUpperCase(), newTrade.givenPokemon.name);
                        if (oldTrade.requestedPokemon != newTrade.requestedPokemon) {
                            replacements.put(oldTrade.requestedPokemon.name.toUpperCase(), newTrade.requestedPokemon.name);
                        }
                        replaceAllStringsInEntry(textOffsets[trade], replacements);
                        // hgss override for one set of strings that appears 2x
                        if (romEntry.romType == Gen4Constants.Type_HGSS && trade == 6) {
                            replaceAllStringsInEntry(textOffsets[trade] + 1, replacements);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new RandomizerIOException(ex);
        }
    }

    private void replaceAllStringsInEntry(int entry, Map<String, String> replacements) {
        // This function currently only replaces move and Pokemon names, and we don't want them
        // split across multiple lines if there is a space.
        replacements.replaceAll((key, oldValue) -> oldValue.replace(' ', '_'));
        int lineLength = Gen4Constants.getTextCharsPerLine(romEntry.romType);
        List<String> strings = this.getStrings(entry);
        for (int strNum = 0; strNum < strings.size(); strNum++) {
            String oldString = strings.get(strNum);
            boolean needsReplacement = false;
            for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                if (oldString.contains(replacement.getKey())) {
                    needsReplacement = true;
                    break;
                }
            }
            if (needsReplacement) {
                String newString = RomFunctions.formatTextWithReplacements(oldString, replacements, "\\n", "\\l", "\\p",
                        lineLength, ssd);
                newString = newString.replace('_', ' ');
                strings.set(strNum, newString);
            }
        }
        this.setStrings(entry, strings);
    }

    @Override
    public boolean hasDVs() {
        return false;
    }

    @Override
    public int generationOfPokemon() {
        return 4;
    }

    @Override
    public void removeEvosForPokemonPool() {
        // slightly more complicated than gen2/3
        // we have to update a "baby table" too
        List<Pokemon> pokemonIncluded = this.mainPokemonList;
        Set<Evolution> keepEvos = new HashSet<>();
        for (Pokemon pk : pokes) {
            if (pk != null) {
                keepEvos.clear();
                for (Evolution evol : pk.evolutionsFrom) {
                    if (pokemonIncluded.contains(evol.from) && pokemonIncluded.contains(evol.to)) {
                        keepEvos.add(evol);
                    } else {
                        evol.to.evolutionsTo.remove(evol);
                    }
                }
                pk.evolutionsFrom.retainAll(keepEvos);
            }
        }

        try {
            byte[] babyPokes = readFile(romEntry.getFile("BabyPokemon"));
            // baby pokemon
            for (int i = 1; i <= Gen4Constants.pokemonCount; i++) {
                Pokemon baby = pokes[i];
                while (baby.evolutionsTo.size() > 0) {
                    // Grab the first "to evolution" even if there are multiple
                    baby = baby.evolutionsTo.get(0).from;
                }
                writeWord(babyPokes, i * 2, baby.number);
            }
            // finish up
            writeFile(romEntry.getFile("BabyPokemon"), babyPokes);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public boolean supportsFourStartingMoves() {
        return true;
    }

    @Override
    public List<Integer> getFieldMoves() {
        if (romEntry.romType == Gen4Constants.Type_HGSS) {
            return Gen4Constants.hgssFieldMoves;
        } else {
            return Gen4Constants.dpptFieldMoves;
        }
    }

    @Override
    public List<Integer> getEarlyRequiredHMMoves() {
        if (romEntry.romType == Gen4Constants.Type_HGSS) {
            return Gen4Constants.hgssEarlyRequiredHMMoves;
        } else {
            return Gen4Constants.dpptEarlyRequiredHMMoves;
        }
    }

    @Override
    public int miscTweaksAvailable() {
        int available = MiscTweak.LOWER_CASE_POKEMON_NAMES.getValue();
        available |= MiscTweak.RANDOMIZE_CATCHING_TUTORIAL.getValue();
        available |= MiscTweak.UPDATE_TYPE_EFFECTIVENESS.getValue();
        if (romEntry.tweakFiles.get("FastestTextTweak") != null) {
            available |= MiscTweak.FASTEST_TEXT.getValue();
        }
        available |= MiscTweak.BAN_LUCKY_EGG.getValue();
        if (romEntry.tweakFiles.get("NationalDexAtStartTweak") != null) {
            available |= MiscTweak.NATIONAL_DEX_AT_START.getValue();
        }
        available |= MiscTweak.RUN_WITHOUT_RUNNING_SHOES.getValue();
        available |= MiscTweak.FASTER_HP_AND_EXP_BARS.getValue();
        if (romEntry.tweakFiles.get("FastDistortionWorldTweak") != null) {
            available |= MiscTweak.FAST_DISTORTION_WORLD.getValue();
        }
        if (romEntry.romType == Gen4Constants.Type_Plat || romEntry.romType == Gen4Constants.Type_HGSS) {
            available |= MiscTweak.UPDATE_ROTOM_FORME_TYPING.getValue();
        }
        return available;
    }

    @Override
    public void applyMiscTweak(MiscTweak tweak) {
        if (tweak == MiscTweak.LOWER_CASE_POKEMON_NAMES) {
            applyCamelCaseNames();
        } else if (tweak == MiscTweak.RANDOMIZE_CATCHING_TUTORIAL) {
            randomizeCatchingTutorial();
        } else if (tweak == MiscTweak.FASTEST_TEXT) {
            applyFastestText();
        } else if (tweak == MiscTweak.BAN_LUCKY_EGG) {
            allowedItems.banSingles(Items.luckyEgg);
            nonBadItems.banSingles(Items.luckyEgg);
        } else if (tweak == MiscTweak.NATIONAL_DEX_AT_START) {
            patchForNationalDex();
        } else if (tweak == MiscTweak.RUN_WITHOUT_RUNNING_SHOES) {
            applyRunWithoutRunningShoesPatch();
        } else if (tweak == MiscTweak.FASTER_HP_AND_EXP_BARS) {
            patchFasterBars();
        } else if (tweak == MiscTweak.UPDATE_TYPE_EFFECTIVENESS) {
            updateTypeEffectiveness();
        } else if (tweak == MiscTweak.FAST_DISTORTION_WORLD) {
            applyFastDistortionWorld();
        } else if (tweak == MiscTweak.UPDATE_ROTOM_FORME_TYPING) {
            updateRotomFormeTyping();
        }
    }

    @Override
    public boolean isEffectivenessUpdated() {
        return effectivenessUpdated;
    }

    private void randomizeCatchingTutorial() {
        int opponentOffset = romEntry.getInt("CatchingTutorialOpponentMonOffset");

        if (romEntry.tweakFiles.containsKey("NewCatchingTutorialSubroutineTweak")) {
            String catchingTutorialMonTablePrefix = romEntry.getString("CatchingTutorialMonTablePrefix");
            int offset = find(arm9, catchingTutorialMonTablePrefix);
            if (offset > 0) {
                offset += catchingTutorialMonTablePrefix.length() / 2; // because it was a prefix

                // The player's mon is randomized as part of randomizing Lyra/Ethan's Pokemon (see
                // randomizeIntroPokemon), so we just care about the enemy mon. As part of our catching
                // tutorial patch, the player and enemy species IDs are pc-relative loaded, with the
                // enemy ID occurring right after the player ID (which is what offset is pointing to).
                Pokemon opponent = randomPokemonLimited(Integer.MAX_VALUE, false);
                writeWord(arm9, offset + 4, opponent.number);
            }
        } else if (romEntry.romType == Gen4Constants.Type_HGSS) {
            // For non-US HGSS, just handle it in the old-school way. Can randomize both Pokemon, but both limited to 1-255
            // Make sure to raise the level of Lyra/Ethan's Pokemon to 10 to prevent softlocks
            int playerOffset = romEntry.getInt("CatchingTutorialPlayerMonOffset");
            int levelOffset = romEntry.getInt("CatchingTutorialPlayerLevelOffset");
            Pokemon opponent = randomPokemonLimited(255, false);
            Pokemon player = randomPokemonLimited(255, false);
            if (opponent != null && player != null) {
                arm9[opponentOffset] = (byte) opponent.number;
                arm9[playerOffset] = (byte) player.number;
                arm9[levelOffset] = 10;
            }
        } else {
            // DPPt only supports randomizing the opponent, but enough space for any mon
            Pokemon opponent = randomPokemonLimited(Integer.MAX_VALUE, false);

            if (opponent != null) {
                writeLong(arm9, opponentOffset, opponent.number);
            }
        }

    }

    private void applyFastestText() {
        genericIPSPatch(arm9, "FastestTextTweak");
    }

    private void patchForNationalDex() {
        byte[] pokedexScript = scriptNarc.files.get(romEntry.getInt("NationalDexScriptOffset"));

        if (romEntry.romType == Gen4Constants.Type_HGSS) {
            // Our patcher breaks if the output file is larger than the input file. For HGSS, we want
            // to expand the script by four bytes to add an instruction to enable the national dex. Thus,
            // the IPS patch was created with us adding four 0x00 bytes to the end of the script in mind.
            byte[] expandedPokedexScript = new byte[pokedexScript.length + 4];
            System.arraycopy(pokedexScript, 0, expandedPokedexScript, 0, pokedexScript.length);
            pokedexScript = expandedPokedexScript;
        }
        genericIPSPatch(pokedexScript, "NationalDexAtStartTweak");
        scriptNarc.files.set(romEntry.getInt("NationalDexScriptOffset"), pokedexScript);
    }

    private void applyRunWithoutRunningShoesPatch() {
        String prefix = Gen4Constants.getRunWithoutRunningShoesPrefix(romEntry.romType);
        int offset = find(arm9, prefix);
        if (offset != 0) {
            // The prefix starts 0xE bytes from what we want to patch because what comes
            // between is region and revision dependent. To start running, the game checks:
            // 1. That you're holding the B button
            // 2. That the FLAG_SYS_B_DASH flag is set (aka, you've acquired Running Shoes)
            // For #2, if the flag is unset, it jumps to a different part of the
            // code to make you walk instead. This simply nops out this jump so the
            // game stops caring about the FLAG_SYS_B_DASH flag entirely.
            writeWord(arm9,offset + 0xE, 0);
        }
    }

    private void patchFasterBars() {
        // To understand what this code is patching, take a look at the CalcNewBarValue
        // and MoveBattleBar functions in this file from the Emerald decompilation:
        // https://github.com/pret/pokeemerald/blob/master/src/battle_interface.c
        // The code in Gen 4 is almost identical outside of one single constant; the
        // reason the bars scroll slower is because Gen 4 runs at 30 FPS instead of 60.
        try {
            byte[] battleOverlay = readOverlay(romEntry.getInt("BattleOvlNumber"));
            int offset = find(battleOverlay, Gen4Constants.hpBarSpeedPrefix);
            if (offset > 0) {
                offset += Gen4Constants.hpBarSpeedPrefix.length() / 2; // because it was a prefix
                // For the HP bar, the original game passes 1 for the toAdd parameter of CalcNewBarValue.
                // We want to pass 2 instead, so we simply change the mov instruction at offset.
                battleOverlay[offset] = 0x02;
            }

            offset = find(battleOverlay, Gen4Constants.expBarSpeedPrefix);
            if (offset > 0) {
                offset += Gen4Constants.expBarSpeedPrefix.length() / 2; // because it was a prefix
                // For the EXP bar, the original game passes expFraction for the toAdd parameter. The
                // game calculates expFraction by doing a division, and to do *that*, it has to load
                // receivedValue into r0 so it can call the division function with it as the first
                // parameter. It gets the value from r6 like so:
                // add r0, r6, #0
                // Since we ultimately want toAdd (and thus expFraction) to be doubled, we can double
                // receivedValue when it gets loaded into r0 by tweaking the add to be:
                // add r0, r6, r6
                battleOverlay[offset] = (byte) 0xB0;
                battleOverlay[offset + 1] = 0x19;
            }

            offset = find(battleOverlay, Gen4Constants.bothBarsSpeedPrefix);
            if (offset > 0) {
                offset += Gen4Constants.bothBarsSpeedPrefix.length() / 2; // because it was a prefix
                // For both HP and EXP bars, a different set of logic is used when the maxValue has
                // fewer pixels than the whole bar; this logic ignores the toAdd parameter entirely and
                // calculates its *own* toAdd by doing maxValue << 8 / scale. If we instead do
                // maxValue << 9, the new toAdd becomes doubled as well.
                battleOverlay[offset] = 0x40;
            }

            writeOverlay(romEntry.getInt("BattleOvlNumber"), battleOverlay);

        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void updateTypeEffectiveness() {
        try {
            byte[] battleOverlay = readOverlay(romEntry.getInt("BattleOvlNumber"));
            int typeEffectivenessTableOffset = find(battleOverlay, Gen4Constants.typeEffectivenessTableLocator);
            if (typeEffectivenessTableOffset > 0) {
                List<TypeRelationship> typeEffectivenessTable = readTypeEffectivenessTable(battleOverlay, typeEffectivenessTableOffset);
                log("--Updating Type Effectiveness--");
                for (TypeRelationship relationship : typeEffectivenessTable) {
                    // Change Ghost 0.5x against Steel to Ghost 1x to Steel
                    if (relationship.attacker == Type.GHOST && relationship.defender == Type.STEEL) {
                        relationship.effectiveness = Effectiveness.NEUTRAL;
                        log("Replaced: Ghost not very effective vs Steel => Ghost neutral vs Steel");
                    }

                    // Change Dark 0.5x against Steel to Dark 1x to Steel
                    else if (relationship.attacker == Type.DARK && relationship.defender == Type.STEEL) {
                        relationship.effectiveness = Effectiveness.NEUTRAL;
                        log("Replaced: Dark not very effective vs Steel => Dark neutral vs Steel");
                    }
                }
                logBlankLine();
                writeTypeEffectivenessTable(typeEffectivenessTable, battleOverlay, typeEffectivenessTableOffset);
                writeOverlay(romEntry.getInt("BattleOvlNumber"), battleOverlay);
                effectivenessUpdated = true;
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private List<TypeRelationship> readTypeEffectivenessTable(byte[] battleOverlay, int typeEffectivenessTableOffset) {
        List<TypeRelationship> typeEffectivenessTable = new ArrayList<>();
        int currentOffset = typeEffectivenessTableOffset;
        int attackingType = battleOverlay[currentOffset];
        // 0xFE marks the end of the table *not* affected by Foresight, while 0xFF marks
        // the actual end of the table. Since we don't care about Ghost immunities at all,
        // just stop once we reach the Foresight section.
        while (attackingType != (byte) 0xFE) {
            int defendingType = battleOverlay[currentOffset + 1];
            int effectivenessInternal = battleOverlay[currentOffset + 2];
            Type attacking = Gen4Constants.typeTable[attackingType];
            Type defending = Gen4Constants.typeTable[defendingType];
            Effectiveness effectiveness = null;
            switch (effectivenessInternal) {
                case 20:
                    effectiveness = Effectiveness.DOUBLE;
                    break;
                case 10:
                    effectiveness = Effectiveness.NEUTRAL;
                    break;
                case 5:
                    effectiveness = Effectiveness.HALF;
                    break;
                case 0:
                    effectiveness = Effectiveness.ZERO;
                    break;
            }
            if (effectiveness != null) {
                TypeRelationship relationship = new TypeRelationship(attacking, defending, effectiveness);
                typeEffectivenessTable.add(relationship);
            }
            currentOffset += 3;
            attackingType = battleOverlay[currentOffset];
        }
        return typeEffectivenessTable;
    }

    private void writeTypeEffectivenessTable(List<TypeRelationship> typeEffectivenessTable, byte[] battleOverlay,
                                             int typeEffectivenessTableOffset) {
        int currentOffset = typeEffectivenessTableOffset;
        for (TypeRelationship relationship : typeEffectivenessTable) {
            battleOverlay[currentOffset] = Gen4Constants.typeToByte(relationship.attacker);
            battleOverlay[currentOffset + 1] = Gen4Constants.typeToByte(relationship.defender);
            byte effectivenessInternal = 0;
            switch (relationship.effectiveness) {
                case DOUBLE:
                    effectivenessInternal = 20;
                    break;
                case NEUTRAL:
                    effectivenessInternal = 10;
                    break;
                case HALF:
                    effectivenessInternal = 5;
                    break;
                case ZERO:
                    effectivenessInternal = 0;
                    break;
            }
            battleOverlay[currentOffset + 2] = effectivenessInternal;
            currentOffset += 3;
        }
    }

    private void applyFastDistortionWorld() {
        byte[] spearPillarPortalScript = scriptNarc.files.get(Gen4Constants.ptSpearPillarPortalScriptFile);
        byte[] expandedSpearPillarPortalScript = new byte[spearPillarPortalScript.length + 12];
        System.arraycopy(spearPillarPortalScript, 0, expandedSpearPillarPortalScript, 0, spearPillarPortalScript.length);
        spearPillarPortalScript = expandedSpearPillarPortalScript;
        genericIPSPatch(spearPillarPortalScript, "FastDistortionWorldTweak");
        scriptNarc.files.set(Gen4Constants.ptSpearPillarPortalScriptFile, spearPillarPortalScript);
    }

    private void updateRotomFormeTyping() {
        pokes[Species.Gen4Formes.rotomH].secondaryType = Type.FIRE;
        pokes[Species.Gen4Formes.rotomW].secondaryType = Type.WATER;
        pokes[Species.Gen4Formes.rotomFr].secondaryType = Type.ICE;
        pokes[Species.Gen4Formes.rotomFa].secondaryType = Type.FLYING;
        pokes[Species.Gen4Formes.rotomM].secondaryType = Type.GRASS;
    }

    @Override
    public void enableGuaranteedPokemonCatching() {
        try {
            byte[] battleOverlay = readOverlay(romEntry.getInt("BattleOvlNumber"));
            int offset = find(battleOverlay, Gen4Constants.perfectOddsBranchLocator);
            if (offset > 0) {
                // In Cmd_handleballthrow (name taken from pokeemerald decomp), the middle of the function checks
                // if the odds of catching a Pokemon is greater than 254; if it is, then the Pokemon is automatically
                // caught. In ASM, this is represented by:
                // cmp r1, #0xFF
                // bcc oddsLessThanOrEqualTo254
                // The below code just nops these two instructions so that we *always* act like our odds are 255,
                // and Pokemon are automatically caught no matter what.
                battleOverlay[offset] = 0x00;
                battleOverlay[offset + 1] = 0x00;
                battleOverlay[offset + 2] = 0x00;
                battleOverlay[offset + 3] = 0x00;
                writeOverlay(romEntry.getInt("BattleOvlNumber"), battleOverlay);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public void applyCorrectStaticMusic(Map<Integer,Integer> specialMusicStaticChanges) {
        List<Integer> replaced = new ArrayList<>();
        String newIndexToMusicPrefix;
        int newIndexToMusicPoolOffset;

        switch(romEntry.romType) {
            case Gen4Constants.Type_DP:
            case Gen4Constants.Type_Plat:
                int extendBy = romEntry.getInt("Arm9ExtensionSize");
                arm9 = extendARM9(arm9, extendBy, romEntry.getString("TCMCopyingPrefix"), Gen4Constants.arm9Offset);
                genericIPSPatch(arm9, "NewIndexToMusicTweak");

                newIndexToMusicPrefix = romEntry.getString("NewIndexToMusicPrefix");
                newIndexToMusicPoolOffset = find(arm9, newIndexToMusicPrefix);
                newIndexToMusicPoolOffset += newIndexToMusicPrefix.length() / 2;

                for (int oldStatic: specialMusicStaticChanges.keySet()) {
                    int i = newIndexToMusicPoolOffset;
                    int index = readWord(arm9, i);
                    while (index != oldStatic || replaced.contains(i)) {
                        i += 4;
                        index = readWord(arm9, i);
                    }
                    writeWord(arm9, i, specialMusicStaticChanges.get(oldStatic));
                    replaced.add(i);
                }
                break;
            case Gen4Constants.Type_HGSS:
                newIndexToMusicPrefix  = romEntry.getString("IndexToMusicPrefix");
                newIndexToMusicPoolOffset = find(arm9, newIndexToMusicPrefix);

                if (newIndexToMusicPoolOffset > 0) {
                    newIndexToMusicPoolOffset += newIndexToMusicPrefix.length() / 2;

                    for (int oldStatic: specialMusicStaticChanges.keySet()) {
                        int i = newIndexToMusicPoolOffset;
                        int indexEtc = readWord(arm9, i);
                        int index = indexEtc & 0x3FF;
                        while (index != oldStatic || replaced.contains(i)) {
                            i += 2;
                            indexEtc = readWord(arm9, i);
                            index = indexEtc & 0x3FF;
                        }
                        int newIndexEtc = specialMusicStaticChanges.get(oldStatic) | (indexEtc & 0xFC00);
                        writeWord(arm9, i, newIndexEtc);
                        replaced.add(i);
                    }
                }
                break;
        }
    }

    @Override
    public boolean hasStaticMusicFix() {
        return romEntry.tweakFiles.get("NewIndexToMusicTweak") != null || romEntry.romType == Gen4Constants.Type_HGSS;
    }

    private boolean genericIPSPatch(byte[] data, String ctName) {
        String patchName = romEntry.tweakFiles.get(ctName);
        if (patchName == null) {
            return false;
        }

        try {
            FileFunctions.applyPatch(data, patchName);
            return true;
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private Pokemon randomPokemonLimited(int maxValue, boolean blockNonMales) {
        checkPokemonRestrictions();
        List<Pokemon> validPokemon = new ArrayList<>();
        for (Pokemon pk : this.mainPokemonList) {
            if (pk.number <= maxValue && (!blockNonMales || pk.genderRatio <= 0xFD)) {
                validPokemon.add(pk);
            }
        }
        if (validPokemon.size() == 0) {
            return null;
        } else {
            return validPokemon.get(random.nextInt(validPokemon.size()));
        }
    }

    private void computeCRC32sForRom() throws IOException {
        this.actualOverlayCRC32s = new HashMap<>();
        this.actualFileCRC32s = new HashMap<>();
        this.actualArm9CRC32 = FileFunctions.getCRC32(arm9);
        for (int overlayNumber : romEntry.overlayExpectedCRC32s.keySet()) {
            byte[] overlay = readOverlay(overlayNumber);
            long crc32 = FileFunctions.getCRC32(overlay);
            this.actualOverlayCRC32s.put(overlayNumber, crc32);
        }
        for (String fileKey : romEntry.files.keySet()) {
            byte[] file = readFile(romEntry.getFile(fileKey));
            long crc32 = FileFunctions.getCRC32(file);
            this.actualFileCRC32s.put(fileKey, crc32);
        }
    }

    @Override
    public boolean isRomValid() {
        if (romEntry.arm9ExpectedCRC32 != actualArm9CRC32) {
            System.out.println(actualArm9CRC32);
            return false;
        }

        for (int overlayNumber : romEntry.overlayExpectedCRC32s.keySet()) {
            long expectedCRC32 = romEntry.overlayExpectedCRC32s.get(overlayNumber);
            long actualCRC32 = actualOverlayCRC32s.get(overlayNumber);
            if (expectedCRC32 != actualCRC32) {
                return false;
            }
        }

        for (String fileKey : romEntry.files.keySet()) {
            long expectedCRC32 = romEntry.files.get(fileKey).expectedCRC32;
            long actualCRC32 = actualFileCRC32s.get(fileKey);
            if (expectedCRC32 != actualCRC32) {
                return false;
            }
        }

        return true;
    }

    @Override
    public BufferedImage getMascotImage() {
        try {
            Pokemon pk = randomPokemon();
            NARCArchive pokespritesNARC = this.readNARC(romEntry.getFile("PokemonGraphics"));
            int spriteIndex = pk.number * 6 + 2 + random.nextInt(2);
            int palIndex = pk.number * 6 + 4;
            if (random.nextInt(10) == 0) {
                // shiny
                palIndex++;
            }

            // read sprite
            byte[] rawSprite = pokespritesNARC.files.get(spriteIndex);
            if (rawSprite.length == 0) {
                // Must use other gender form
                rawSprite = pokespritesNARC.files.get(spriteIndex ^ 1);
            }
            int[] spriteData = new int[3200];
            for (int i = 0; i < 3200; i++) {
                spriteData[i] = readWord(rawSprite, i * 2 + 48);
            }

            // Decrypt sprite (why does EVERYTHING use the RNG formula geez)
            if (romEntry.romType != Gen4Constants.Type_DP) {
                int key = spriteData[0];
                for (int i = 0; i < 3200; i++) {
                    spriteData[i] ^= (key & 0xFFFF);
                    key = key * 0x41C64E6D + 0x6073;
                }
            } else {
                // D/P sprites are encrypted *backwards*. Wut.
                int key = spriteData[3199];
                for (int i = 3199; i >= 0; i--) {
                    spriteData[i] ^= (key & 0xFFFF);
                    key = key * 0x41C64E6D + 0x6073;
                }
            }

            byte[] rawPalette = pokespritesNARC.files.get(palIndex);

            int[] palette = new int[16];
            for (int i = 1; i < 16; i++) {
                palette[i] = GFXFunctions.conv16BitColorToARGB(readWord(rawPalette, 40 + i * 2));
            }

            // Deliberately chop off the right half of the image while still
            // correctly indexing the array.
            BufferedImage bim = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < 80; y++) {
                for (int x = 0; x < 80; x++) {
                    int value = ((spriteData[y * 40 + x / 4]) >> (x % 4) * 4) & 0x0F;
                    bim.setRGB(x, y, palette[value]);
                }
            }
            return bim;
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public List<Integer> getAllConsumableHeldItems() {
        return Gen4Constants.consumableHeldItems;
    }

    @Override
    public List<Integer> getAllHeldItems() {
        return Gen4Constants.allHeldItems;
    }

    @Override
    public List<Integer> getSensibleHeldItemsFor(TrainerPokemon tp, boolean consumableOnly, List<Move> moves, int[] pokeMoves) {
        List<Integer> items = new ArrayList<>();
        items.addAll(Gen4Constants.generalPurposeConsumableItems);
        int frequencyBoostCount = 6; // Make some very good items more common, but not too common
        if (!consumableOnly) {
            frequencyBoostCount = 8; // bigger to account for larger item pool.
            items.addAll(Gen4Constants.generalPurposeItems);
        }
        for (int moveIdx : pokeMoves) {
            Move move = moves.get(moveIdx);
            if (move == null) {
                continue;
            }
            if (move.category == MoveCategory.PHYSICAL) {
                items.add(Items.liechiBerry);
                if (!consumableOnly) {
                    items.addAll(Gen4Constants.typeBoostingItems.get(move.type));
                    items.add(Items.choiceBand);
                    items.add(Items.muscleBand);
                }
            }
            if (move.category == MoveCategory.SPECIAL) {
                items.add(Items.petayaBerry);
                if (!consumableOnly) {
                    items.addAll(Gen4Constants.typeBoostingItems.get(move.type));
                    items.add(Items.wiseGlasses);
                    items.add(Items.choiceSpecs);
                }
            }
            if (!consumableOnly && Gen4Constants.moveBoostingItems.containsKey(moveIdx)) {
                items.addAll(Gen4Constants.moveBoostingItems.get(moveIdx));
            }
        }
        Map<Type, Effectiveness> byType = Effectiveness.against(tp.pokemon.primaryType, tp.pokemon.secondaryType, 4, effectivenessUpdated);
        for(Map.Entry<Type, Effectiveness> entry : byType.entrySet()) {
            Integer berry = Gen4Constants.weaknessReducingBerries.get(entry.getKey());
            if (entry.getValue() == Effectiveness.DOUBLE) {
                items.add(berry);
            } else if (entry.getValue() == Effectiveness.QUADRUPLE) {
                for (int i = 0; i < frequencyBoostCount; i++) {
                    items.add(berry);
                }
            }
        }
        if (byType.get(Type.NORMAL) == Effectiveness.NEUTRAL) {
            items.add(Items.chilanBerry);
        }

        int ability = this.getAbilityForTrainerPokemon(tp);
        if (ability == Abilities.levitate) {
            items.removeAll(Arrays.asList(Items.shucaBerry));
        }

        if (!consumableOnly) {
            if (Gen4Constants.abilityBoostingItems.containsKey(ability)) {
                items.addAll(Gen4Constants.abilityBoostingItems.get(ability));
            }
            if (tp.pokemon.primaryType == Type.POISON || tp.pokemon.secondaryType == Type.POISON) {
                items.add(Items.blackSludge);
            }
            List<Integer> speciesItems = Gen4Constants.speciesBoostingItems.get(tp.pokemon.number);
            if (speciesItems != null) {
                for (int i = 0; i < frequencyBoostCount; i++) {
                    items.addAll(speciesItems);
                }
            }
        }
        return items;
    }
}
