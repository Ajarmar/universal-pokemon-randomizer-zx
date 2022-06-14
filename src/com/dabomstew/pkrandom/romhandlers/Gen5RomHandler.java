package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Gen5RomHandler.java - randomizer handler for B/W/B2/W2.               --*/
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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.dabomstew.pkrandom.*;
import com.dabomstew.pkrandom.constants.*;
import com.dabomstew.pkrandom.exceptions.RandomizationException;
import com.dabomstew.pkrandom.pokemon.*;
import pptxt.PPTxtHandler;

import com.dabomstew.pkrandom.exceptions.RandomizerIOException;
import com.dabomstew.pkrandom.newnds.NARCArchive;
import compressors.DSDecmp;

public class Gen5RomHandler extends AbstractDSRomHandler {

    public static class Factory extends RomHandler.Factory {

        @Override
        public Gen5RomHandler create(Random random, PrintStream logStream) {
            return new Gen5RomHandler(random, logStream);
        }

        public boolean isLoadable(String filename) {
            return detectNDSRomInner(getROMCodeFromFile(filename), getVersionFromFile(filename));
        }
    }

    public Gen5RomHandler(Random random) {
        super(random, null);
    }

    public Gen5RomHandler(Random random, PrintStream logStream) {
        super(random, logStream);
    }

    @Override
    public void changeCatchRates(Settings settings) {
        int minimumCatchRateLevel = settings.getMinimumCatchRateLevel();

        int normalMin, legendaryMin;
        switch (minimumCatchRateLevel) {
            case 1:
            default:
                normalMin = 50;
                legendaryMin = 25;
                break;
            case 2:
                normalMin = 100;
                legendaryMin = 45;
                break;
            case 3:
                normalMin = 180;
                legendaryMin = 75;
                break;
            case 4:
                normalMin = legendaryMin = 255;
                break;
        }
        minimumCatchRate(normalMin, legendaryMin);
    }

    private static class OffsetWithinEntry {
        private int entry;
        private int offset;
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
        private boolean staticPokemonSupport = false, copyStaticPokemon = false, copyRoamingPokemon = false,
                copyTradeScripts = false, isBlack = false;
        private Map<String, String> strings = new HashMap<>();
        private Map<String, Integer> numbers = new HashMap<>();
        private Map<String, String> tweakFiles = new HashMap<>();
        private Map<String, int[]> arrayEntries = new HashMap<>();
        private Map<String, OffsetWithinEntry[]> offsetArrayEntries = new HashMap<>();
        private Map<String, RomFileEntry> files = new HashMap<>();
        private Map<Integer, Long> overlayExpectedCRC32s = new HashMap<>();
        private List<StaticPokemon> staticPokemon = new ArrayList<>();
        private List<StaticPokemon> staticPokemonFakeBall = new ArrayList<>();
        private List<RoamingPokemon> roamingPokemon = new ArrayList<>();
        private List<TradeScript> tradeScripts = new ArrayList<>();
        

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
            Scanner sc = new Scanner(FileFunctions.openConfig("gen5_offsets.ini"), "UTF-8");
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
                            if (r[1].equalsIgnoreCase("BW2")) {
                                current.romType = Gen5Constants.Type_BW2;
                            } else {
                                current.romType = Gen5Constants.Type_BW;
                            }
                        } else if (r[0].equals("CopyFrom")) {
                            for (RomEntry otherEntry : roms) {
                                if (r[1].equalsIgnoreCase(otherEntry.romCode)) {
                                    // copy from here
                                    current.arrayEntries.putAll(otherEntry.arrayEntries);
                                    current.numbers.putAll(otherEntry.numbers);
                                    current.strings.putAll(otherEntry.strings);
                                    current.offsetArrayEntries.putAll(otherEntry.offsetArrayEntries);
                                    current.files.putAll(otherEntry.files);
                                    if (current.copyStaticPokemon) {
                                        current.staticPokemon.addAll(otherEntry.staticPokemon);
                                        current.staticPokemonFakeBall.addAll(otherEntry.staticPokemonFakeBall);
                                        current.staticPokemonSupport = true;
                                    } else {
                                        current.staticPokemonSupport = false;
                                    }
                                    if (current.copyTradeScripts) {
                                        current.tradeScripts.addAll(otherEntry.tradeScripts);
                                    }
                                    if (current.copyRoamingPokemon) {
                                        current.roamingPokemon.addAll(otherEntry.roamingPokemon);
                                    }
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
                        } else if (r[0].equals("StaticPokemonFakeBall{}")) {
                            current.staticPokemonFakeBall.add(parseStaticPokemon(r[1]));
                        } else if (r[0].equals("RoamingPokemon{}")) {
                            current.roamingPokemon.add(parseRoamingPokemon(r[1]));
                        } else if (r[0].equals("TradeScript[]")) {
                            String[] offsets = r[1].substring(1, r[1].length() - 1).split(",");
                            int[] reqOffs = new int[offsets.length];
                            int[] givOffs = new int[offsets.length];
                            int file = 0;
                            int c = 0;
                            for (String off : offsets) {
                                String[] parts = off.split(":");
                                file = parseRIInt(parts[0]);
                                reqOffs[c] = parseRIInt(parts[1]);
                                givOffs[c++] = parseRIInt(parts[2]);
                            }
                            TradeScript ts = new TradeScript();
                            ts.fileNum = file;
                            ts.requestedOffsets = reqOffs;
                            ts.givenOffsets = givOffs;
                            current.tradeScripts.add(ts);
                        } else if (r[0].equals("StaticPokemonSupport")) {
                            int spsupport = parseRIInt(r[1]);
                            current.staticPokemonSupport = (spsupport > 0);
                        } else if (r[0].equals("CopyStaticPokemon")) {
                            int csp = parseRIInt(r[1]);
                            current.copyStaticPokemon = (csp > 0);
                        } else if (r[0].equals("CopyRoamingPokemon")) {
                            int crp = parseRIInt(r[1]);
                            current.copyRoamingPokemon = (crp > 0);
                        } else if (r[0].equals("CopyTradeScripts")) {
                            int cts = parseRIInt(r[1]);
                            current.copyTradeScripts = (cts > 0);
                        } else if (r[0].startsWith("StarterOffsets")) {
                            String[] offsets = r[1].substring(1, r[1].length() - 1).split(",");
                            OffsetWithinEntry[] offs = new OffsetWithinEntry[offsets.length];
                            int c = 0;
                            for (String off : offsets) {
                                String[] parts = off.split(":");
                                OffsetWithinEntry owe = new OffsetWithinEntry();
                                owe.entry = parseRIInt(parts[0]);
                                owe.offset = parseRIInt(parts[1]);
                                offs[c++] = owe;
                            }
                            current.offsetArrayEntries.put(r[0], offs);
                        } else if (r[0].endsWith("Tweak")) {
                            current.tweakFiles.put(r[0], r[1]);
                        } else if (r[0].equals("IsBlack")) {
                            int isBlack = parseRIInt(r[1]);
                            current.isBlack = (isBlack > 0);
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
            FileEntry[] entries = new FileEntry[offsets.length];
            for (int i = 0; i < entries.length; i++) {
                String[] parts = offsets[i].split(":");
                entries[i] = new FileEntry(parseRIInt(parts[0]), parseRIInt(parts[1]));
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
                    int[] speciesOverlayOffsets = new int[offsets.length];
                    for (int i = 0; i < speciesOverlayOffsets.length; i++) {
                        speciesOverlayOffsets[i] = parseRIInt(offsets[i]);
                    }
                    rp.speciesOverlayOffsets = speciesOverlayOffsets;
                    break;
                case "Level":
                    int[] levelOverlayOffsets = new int[offsets.length];
                    for (int i = 0; i < levelOverlayOffsets.length; i++) {
                        levelOverlayOffsets[i] = parseRIInt(offsets[i]);
                    }
                    rp.levelOverlayOffsets = levelOverlayOffsets;
                    break;
                case "Script":
                    FileEntry[] entries = new FileEntry[offsets.length];
                    for (int i = 0; i < entries.length; i++) {
                        String[] parts = offsets[i].split(":");
                        entries[i] = new FileEntry(parseRIInt(parts[0]), parseRIInt(parts[1]));
                    }
                    rp.speciesScriptOffsets = entries;
                    break;
            }
        }
        return rp;
    }

    // This ROM
    private Pokemon[] pokes;
    private Map<Integer,FormeInfo> formeMappings = new TreeMap<>();
    private List<Pokemon> pokemonList;
    private List<Pokemon> pokemonListInclFormes;
    private Move[] moves;
    private RomEntry romEntry;
    private byte[] arm9;
    private List<String> abilityNames;
    private List<String> itemNames;
    private List<String> shopNames;
    private boolean loadedWildMapNames;
    private Map<Integer, String> wildMapNames;
    private ItemList allowedItems, nonBadItems;
    private List<Integer> regularShopItems;
    private List<Integer> opShopItems;
    private int hiddenHollowCount = 0;
    private boolean hiddenHollowCounted = false;
    private List<Integer> originalDoubleTrainers = new ArrayList<>();
    private boolean effectivenessUpdated;
    private int pickupItemsTableOffset;
    private long actualArm9CRC32;
    private Map<Integer, Long> actualOverlayCRC32s;
    private Map<String, Long> actualFileCRC32s;
    
    private NARCArchive pokeNarc, moveNarc, stringsNarc, storyTextNarc, scriptNarc, shopNarc;

    @Override
    protected boolean detectNDSRom(String ndsCode, byte version) {
        return detectNDSRomInner(ndsCode, version);
    }

    private static boolean detectNDSRomInner(String ndsCode, byte version) {
        return entryFor(ndsCode, version) != null;
    }

    private static RomEntry entryFor(String ndsCode, byte version) {
        if (ndsCode == null) {
            return null;
        }

        for (RomEntry re : roms) {
            if (ndsCode.equals(re.romCode) && re.version == version) {
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
            stringsNarc = readNARC(romEntry.getFile("TextStrings"));
            storyTextNarc = readNARC(romEntry.getFile("TextStory"));
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        try {
            scriptNarc = readNARC(romEntry.getFile("Scripts"));
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        if (romEntry.romType == Gen5Constants.Type_BW2) {
            try {
                shopNarc = readNARC(romEntry.getFile("ShopItems"));
            } catch (IOException e) {
                throw new RandomizerIOException(e);
            }
        }
        loadPokemonStats();
        pokemonListInclFormes = Arrays.asList(pokes);
        pokemonList = Arrays.asList(Arrays.copyOfRange(pokes,0,Gen5Constants.pokemonCount + 1));
        loadMoves();

        abilityNames = getStrings(false, romEntry.getInt("AbilityNamesTextOffset"));
        itemNames = getStrings(false, romEntry.getInt("ItemNamesTextOffset"));
        if (romEntry.romType == Gen5Constants.Type_BW) {
            shopNames = Gen5Constants.bw1ShopNames;
        }
        else if (romEntry.romType == Gen5Constants.Type_BW2) {
            shopNames = Gen5Constants.bw2ShopNames;
        }
        
        loadedWildMapNames = false;

        allowedItems = Gen5Constants.allowedItems.copy();
        nonBadItems = Gen5Constants.getNonBadItems(romEntry.romType).copy();
        regularShopItems = Gen5Constants.regularShopItems;
        opShopItems = Gen5Constants.opShopItems;

        try {
            computeCRC32sForRom();
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void loadPokemonStats() {
        try {
            pokeNarc = this.readNARC(romEntry.getFile("PokemonStats"));
            String[] pokeNames = readPokemonNames();
            int formeCount = Gen5Constants.getFormeCount(romEntry.romType);
            pokes = new Pokemon[Gen5Constants.pokemonCount + formeCount + 1];
            for (int i = 1; i <= Gen5Constants.pokemonCount; i++) {
                pokes[i] = new Pokemon();
                pokes[i].number = i;
                loadBasicPokeStats(pokes[i], pokeNarc.files.get(i), formeMappings);
                // Name?
                pokes[i].name = pokeNames[i];
            }

            int i = Gen5Constants.pokemonCount + 1;
            for (int k: formeMappings.keySet()) {
                pokes[i] = new Pokemon();
                pokes[i].number = i;
                loadBasicPokeStats(pokes[i], pokeNarc.files.get(k), formeMappings);
                FormeInfo fi = formeMappings.get(k);
                pokes[i].name = pokeNames[fi.baseForme];
                pokes[i].baseForme = pokes[fi.baseForme];
                pokes[i].formeNumber = fi.formeNumber;
                pokes[i].formeSpriteIndex = fi.formeSpriteOffset + Gen5Constants.pokemonCount + Gen5Constants.getNonPokemonBattleSpriteCount(romEntry.romType);
                pokes[i].formeSuffix = Gen5Constants.getFormeSuffix(k,romEntry.romType);
                i = i + 1;
            }
            populateEvolutions();
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

    }

    private void loadMoves() {
        try {
            moveNarc = this.readNARC(romEntry.getFile("MoveData"));
            moves = new Move[Gen5Constants.moveCount + 1];
            List<String> moveNames = getStrings(false, romEntry.getInt("MoveNamesTextOffset"));
            for (int i = 1; i <= Gen5Constants.moveCount; i++) {
                byte[] moveData = moveNarc.files.get(i);
                moves[i] = new Move();
                moves[i].name = moveNames.get(i);
                moves[i].number = i;
                moves[i].internalId = i;
                moves[i].effectIndex = readWord(moveData, 16);
                moves[i].hitratio = (moveData[4] & 0xFF);
                moves[i].power = moveData[3] & 0xFF;
                moves[i].pp = moveData[5] & 0xFF;
                moves[i].type = Gen5Constants.typeTable[moveData[0] & 0xFF];
                moves[i].flinchPercentChance = moveData[15] & 0xFF;
                moves[i].target = moveData[20] & 0xFF;
                moves[i].category = Gen5Constants.moveCategoryIndices[moveData[2] & 0xFF];
                moves[i].priority = moveData[6];

                int critStages = moveData[14] & 0xFF;
                if (critStages == 6) {
                    moves[i].criticalChance = CriticalChance.GUARANTEED;
                } else if (critStages > 0) {
                    moves[i].criticalChance = CriticalChance.INCREASED;
                }

                int internalStatusType = readWord(moveData, 8);
                int flags = FileFunctions.readFullInt(moveData, 32);
                moves[i].makesContact = (flags & 0x001) != 0;
                moves[i].isChargeMove = (flags & 0x002) != 0;
                moves[i].isRechargeMove = (flags & 0x004) != 0;
                moves[i].isPunchMove = (flags & 0x080) != 0;
                moves[i].isSoundMove = (flags & 0x100) != 0;
                moves[i].isTrapMove = (moves[i].effectIndex == Gen5Constants.trappingEffect || internalStatusType == 8);
                int qualities = moveData[1];
                int recoilOrAbsorbPercent = moveData[18];
                if (qualities == Gen5Constants.damageAbsorbQuality) {
                    moves[i].absorbPercent = recoilOrAbsorbPercent;
                } else {
                    moves[i].recoilPercent = -recoilOrAbsorbPercent;
                }

                if (i == Moves.swift) {
                    perfectAccuracy = (int)moves[i].hitratio;
                }

                if (GlobalConstants.normalMultihitMoves.contains(i)) {
                    moves[i].hitCount = 19 / 6.0;
                } else if (GlobalConstants.doubleHitMoves.contains(i)) {
                    moves[i].hitCount = 2;
                } else if (i == Moves.tripleKick) {
                    moves[i].hitCount = 2.71; // this assumes the first hit lands
                }

                switch (qualities) {
                    case Gen5Constants.noDamageStatChangeQuality:
                    case Gen5Constants.noDamageStatusAndStatChangeQuality:
                        // All Allies or Self
                        if (moves[i].target == 6 || moves[i].target == 7) {
                            moves[i].statChangeMoveType = StatChangeMoveType.NO_DAMAGE_USER;
                        } else {
                            moves[i].statChangeMoveType = StatChangeMoveType.NO_DAMAGE_TARGET;
                        }
                        break;
                    case Gen5Constants.damageTargetDebuffQuality:
                        moves[i].statChangeMoveType = StatChangeMoveType.DAMAGE_TARGET;
                        break;
                    case Gen5Constants.damageUserBuffQuality:
                        moves[i].statChangeMoveType = StatChangeMoveType.DAMAGE_USER;
                        break;
                    default:
                        moves[i].statChangeMoveType = StatChangeMoveType.NONE_OR_UNKNOWN;
                        break;
                }

                for (int statChange = 0; statChange < 3; statChange++) {
                    moves[i].statChanges[statChange].type = StatChangeType.values()[moveData[21 + statChange]];
                    moves[i].statChanges[statChange].stages = moveData[24 + statChange];
                    moves[i].statChanges[statChange].percentChance = moveData[27 + statChange];
                }

                // Exclude status types that aren't in the StatusType enum.
                if (internalStatusType < 7) {
                    moves[i].statusType = StatusType.values()[internalStatusType];
                    if (moves[i].statusType == StatusType.POISON && (i == Moves.toxic || i == Moves.poisonFang)) {
                        moves[i].statusType = StatusType.TOXIC_POISON;
                    }
                    moves[i].statusPercentChance = moveData[10] & 0xFF;
                    if (moves[i].number == Moves.chatter) {
                        moves[i].statusPercentChance = 1.0;
                    }
                    switch (qualities) {
                        case Gen5Constants.noDamageStatusQuality:
                        case Gen5Constants.noDamageStatusAndStatChangeQuality:
                            moves[i].statusMoveType = StatusMoveType.NO_DAMAGE;
                            break;
                        case Gen5Constants.damageStatusQuality:
                            moves[i].statusMoveType = StatusMoveType.DAMAGE;
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

    }

    private void loadBasicPokeStats(Pokemon pkmn, byte[] stats, Map<Integer,FormeInfo> altFormes) {
        pkmn.hp = stats[Gen5Constants.bsHPOffset] & 0xFF;
        pkmn.attack = stats[Gen5Constants.bsAttackOffset] & 0xFF;
        pkmn.defense = stats[Gen5Constants.bsDefenseOffset] & 0xFF;
        pkmn.speed = stats[Gen5Constants.bsSpeedOffset] & 0xFF;
        pkmn.spatk = stats[Gen5Constants.bsSpAtkOffset] & 0xFF;
        pkmn.spdef = stats[Gen5Constants.bsSpDefOffset] & 0xFF;
        // Type
        pkmn.primaryType = Gen5Constants.typeTable[stats[Gen5Constants.bsPrimaryTypeOffset] & 0xFF];
        pkmn.secondaryType = Gen5Constants.typeTable[stats[Gen5Constants.bsSecondaryTypeOffset] & 0xFF];
        // Only one type?
        if (pkmn.secondaryType == pkmn.primaryType) {
            pkmn.secondaryType = null;
        }
        pkmn.catchRate = stats[Gen5Constants.bsCatchRateOffset] & 0xFF;
        pkmn.growthCurve = ExpCurve.fromByte(stats[Gen5Constants.bsGrowthCurveOffset]);

        pkmn.ability1 = stats[Gen5Constants.bsAbility1Offset] & 0xFF;
        pkmn.ability2 = stats[Gen5Constants.bsAbility2Offset] & 0xFF;
        pkmn.ability3 = stats[Gen5Constants.bsAbility3Offset] & 0xFF;

        // Held Items?
        int item1 = readWord(stats, Gen5Constants.bsCommonHeldItemOffset);
        int item2 = readWord(stats, Gen5Constants.bsRareHeldItemOffset);

        if (item1 == item2) {
            // guaranteed
            pkmn.guaranteedHeldItem = item1;
            pkmn.commonHeldItem = 0;
            pkmn.rareHeldItem = 0;
            pkmn.darkGrassHeldItem = 0;
        } else {
            pkmn.guaranteedHeldItem = 0;
            pkmn.commonHeldItem = item1;
            pkmn.rareHeldItem = item2;
            pkmn.darkGrassHeldItem = readWord(stats, Gen5Constants.bsDarkGrassHeldItemOffset);
        }

        int formeCount = stats[Gen5Constants.bsFormeCountOffset] & 0xFF;
        if (formeCount > 1) {
            int firstFormeOffset = readWord(stats, Gen5Constants.bsFormeOffset);
            if (firstFormeOffset != 0) {
                for (int i = 1; i < formeCount; i++) {
                    altFormes.put(firstFormeOffset + i - 1,new FormeInfo(pkmn.number,i,readWord(stats,Gen5Constants.bsFormeSpriteOffset))); // Assumes that formes are in memory in the same order as their numbers
                    if (pkmn.number == Species.keldeo) {
                        pkmn.cosmeticForms = formeCount;
                    }
                }
            } else {
                if (pkmn.number != Species.cherrim && pkmn.number != Species.arceus && pkmn.number != Species.deerling && pkmn.number != Species.sawsbuck && pkmn.number < Species.genesect) {
                    // Reason for exclusions:
                    // Cherrim/Arceus/Genesect: to avoid confusion
                    // Deerling/Sawsbuck: handled automatically in gen 5
                    pkmn.cosmeticForms = formeCount;
                }
                if (pkmn.number == Species.Gen5Formes.keldeoCosmetic1) {
                    pkmn.actuallyCosmetic = true;
                }
            }
        }
    }

    private String[] readPokemonNames() {
        String[] pokeNames = new String[Gen5Constants.pokemonCount + 1];
        List<String> nameList = getStrings(false, romEntry.getInt("PokemonNamesTextOffset"));
        for (int i = 1; i <= Gen5Constants.pokemonCount; i++) {
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
            writeNARC(romEntry.getFile("TextStrings"), stringsNarc);
            writeNARC(romEntry.getFile("TextStory"), storyTextNarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        try {
            writeNARC(romEntry.getFile("Scripts"), scriptNarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void saveMoves() {
        for (int i = 1; i <= Gen5Constants.moveCount; i++) {
            byte[] data = moveNarc.files.get(i);
            data[2] = Gen5Constants.moveCategoryToByte(moves[i].category);
            data[3] = (byte) moves[i].power;
            data[0] = Gen5Constants.typeToByte(moves[i].type);
            int hitratio = (int) Math.round(moves[i].hitratio);
            if (hitratio < 0) {
                hitratio = 0;
            }
            if (hitratio > 101) {
                hitratio = 100;
            }
            data[4] = (byte) hitratio;
            data[5] = (byte) moves[i].pp;
        }

        try {
            this.writeNARC(romEntry.getFile("MoveData"), moveNarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

    }

    private void savePokemonStats() {
        List<String> nameList = getStrings(false, romEntry.getInt("PokemonNamesTextOffset"));

        int formeCount = Gen5Constants.getFormeCount(romEntry.romType);
        int formeOffset = Gen5Constants.getFormeOffset(romEntry.romType);
        for (int i = 1; i <= Gen5Constants.pokemonCount + formeCount; i++) {
            if (i > Gen5Constants.pokemonCount) {
                saveBasicPokeStats(pokes[i], pokeNarc.files.get(i + formeOffset));
                continue;
            }
            saveBasicPokeStats(pokes[i], pokeNarc.files.get(i));
            nameList.set(i, pokes[i].name);
        }

        setStrings(false, romEntry.getInt("PokemonNamesTextOffset"), nameList);

        try {
            this.writeNARC(romEntry.getFile("PokemonStats"), pokeNarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        writeEvolutions();
    }

    private void saveBasicPokeStats(Pokemon pkmn, byte[] stats) {
        stats[Gen5Constants.bsHPOffset] = (byte) pkmn.hp;
        stats[Gen5Constants.bsAttackOffset] = (byte) pkmn.attack;
        stats[Gen5Constants.bsDefenseOffset] = (byte) pkmn.defense;
        stats[Gen5Constants.bsSpeedOffset] = (byte) pkmn.speed;
        stats[Gen5Constants.bsSpAtkOffset] = (byte) pkmn.spatk;
        stats[Gen5Constants.bsSpDefOffset] = (byte) pkmn.spdef;
        stats[Gen5Constants.bsPrimaryTypeOffset] = Gen5Constants.typeToByte(pkmn.primaryType);
        if (pkmn.secondaryType == null) {
            stats[Gen5Constants.bsSecondaryTypeOffset] = stats[Gen5Constants.bsPrimaryTypeOffset];
        } else {
            stats[Gen5Constants.bsSecondaryTypeOffset] = Gen5Constants.typeToByte(pkmn.secondaryType);
        }
        stats[Gen5Constants.bsCatchRateOffset] = (byte) pkmn.catchRate;
        stats[Gen5Constants.bsGrowthCurveOffset] = pkmn.growthCurve.toByte();

        stats[Gen5Constants.bsAbility1Offset] = (byte) pkmn.ability1;
        stats[Gen5Constants.bsAbility2Offset] = (byte) pkmn.ability2;
        stats[Gen5Constants.bsAbility3Offset] = (byte) pkmn.ability3;

        // Held items
        if (pkmn.guaranteedHeldItem > 0) {
            writeWord(stats, Gen5Constants.bsCommonHeldItemOffset, pkmn.guaranteedHeldItem);
            writeWord(stats, Gen5Constants.bsRareHeldItemOffset, pkmn.guaranteedHeldItem);
            writeWord(stats, Gen5Constants.bsDarkGrassHeldItemOffset, 0);
        } else {
            writeWord(stats, Gen5Constants.bsCommonHeldItemOffset, pkmn.commonHeldItem);
            writeWord(stats, Gen5Constants.bsRareHeldItemOffset, pkmn.rareHeldItem);
            writeWord(stats, Gen5Constants.bsDarkGrassHeldItemOffset, pkmn.darkGrassHeldItem);
        }
    }

    @Override
    public List<Pokemon> getPokemon() {
        return pokemonList;
    }

    @Override
    public List<Pokemon> getPokemonInclFormes() {
        return pokemonListInclFormes;
    }

    @Override
    public List<Pokemon> getAltFormes() {
        int formeCount = Gen5Constants.getFormeCount(romEntry.romType);
        return pokemonListInclFormes.subList(Gen5Constants.pokemonCount + 1, Gen5Constants.pokemonCount + formeCount + 1);
    }

    @Override
    public List<MegaEvolution> getMegaEvolutions() {
        return new ArrayList<>();
    }

    @Override
    public Pokemon getAltFormeOfPokemon(Pokemon pk, int forme) {
        int pokeNum = Gen5Constants.getAbsolutePokeNumByBaseForme(pk.number,forme);
        return pokeNum != 0 ? pokes[pokeNum] : pk;
    }

    @Override
    public List<Pokemon> getIrregularFormes() {
        return Gen5Constants.getIrregularFormes(romEntry.romType).stream().map(i -> pokes[i]).collect(Collectors.toList());
    }

    @Override
    public boolean hasFunctionalFormes() {
        return true;
    }

    @Override
    public List<Pokemon> getStarters() {
        NARCArchive scriptNARC = scriptNarc;
        List<Pokemon> starters = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            OffsetWithinEntry[] thisStarter = romEntry.offsetArrayEntries.get("StarterOffsets" + (i + 1));
            starters.add(pokes[readWord(scriptNARC.files.get(thisStarter[0].entry), thisStarter[0].offset)]);
        }
        return starters;
    }

    @Override
    public boolean setStarters(List<Pokemon> newStarters) {
        if (newStarters.size() != 3) {
            return false;
        }

        // Fix up starter offsets
        try {
            NARCArchive scriptNARC = scriptNarc;
            for (int i = 0; i < 3; i++) {
                int starter = newStarters.get(i).number;
                OffsetWithinEntry[] thisStarter = romEntry.offsetArrayEntries.get("StarterOffsets" + (i + 1));
                for (OffsetWithinEntry entry : thisStarter) {
                    writeWord(scriptNARC.files.get(entry.entry), entry.offset, starter);
                }
            }
            // GIVE ME BACK MY PURRLOIN
            if (romEntry.romType == Gen5Constants.Type_BW2) {
                byte[] newScript = Gen5Constants.bw2NewStarterScript;
                byte[] oldFile = scriptNARC.files.get(romEntry.getInt("PokedexGivenFileOffset"));
                byte[] newFile = new byte[oldFile.length + newScript.length];
                int offset = find(oldFile, Gen5Constants.bw2StarterScriptMagic);
                if (offset > 0) {
                    System.arraycopy(oldFile, 0, newFile, 0, oldFile.length);
                    System.arraycopy(newScript, 0, newFile, oldFile.length, newScript.length);
                    if (romEntry.romCode.charAt(3) == 'J') {
                        newFile[oldFile.length + 0x6] -= 4;
                    }
                    newFile[offset++] = 0x1E;
                    newFile[offset++] = 0x0;
                    writeRelativePointer(newFile, offset, oldFile.length);
                    scriptNARC.files.set(romEntry.getInt("PokedexGivenFileOffset"), newFile);
                }
            } else {
                byte[] newScript = Gen5Constants.bw1NewStarterScript;

                byte[] oldFile = scriptNARC.files.get(romEntry.getInt("PokedexGivenFileOffset"));
                byte[] newFile = new byte[oldFile.length + newScript.length];
                int offset = find(oldFile, Gen5Constants.bw1StarterScriptMagic);
                if (offset > 0) {
                    System.arraycopy(oldFile, 0, newFile, 0, oldFile.length);
                    System.arraycopy(newScript, 0, newFile, oldFile.length, newScript.length);
                    if (romEntry.romCode.charAt(3) == 'J') {
                        newFile[oldFile.length + 0x4] -= 4;
                        newFile[oldFile.length + 0x8] -= 4;
                    }
                    newFile[offset++] = 0x04;
                    newFile[offset++] = 0x0;
                    writeRelativePointer(newFile, offset, oldFile.length);
                    scriptNARC.files.set(romEntry.getInt("PokedexGivenFileOffset"), newFile);
                }
            }

            // Starter sprites
            NARCArchive starterNARC = this.readNARC(romEntry.getFile("StarterGraphics"));
            NARCArchive pokespritesNARC = this.readNARC(romEntry.getFile("PokemonGraphics"));
            replaceStarterFiles(starterNARC, pokespritesNARC, 0, newStarters.get(0).number);
            replaceStarterFiles(starterNARC, pokespritesNARC, 1, newStarters.get(1).number);
            replaceStarterFiles(starterNARC, pokespritesNARC, 2, newStarters.get(2).number);
            writeNARC(romEntry.getFile("StarterGraphics"), starterNARC);

            // Starter cries
            byte[] starterCryOverlay = this.readOverlay(romEntry.getInt("StarterCryOvlNumber"));
            String starterCryTablePrefix = romEntry.getString("StarterCryTablePrefix");
            int offset = find(starterCryOverlay, starterCryTablePrefix);
            if (offset > 0) {
                offset += starterCryTablePrefix.length() / 2; // because it was a prefix
                for (Pokemon newStarter : newStarters) {
                    writeWord(starterCryOverlay, offset, newStarter.number);
                    offset += 2;
                }
                this.writeOverlay(romEntry.getInt("StarterCryOvlNumber"), starterCryOverlay);
            }
        } catch (IOException  ex) {
            throw new RandomizerIOException(ex);
        }
        // Fix text depending on version
        if (romEntry.romType == Gen5Constants.Type_BW) {
            List<String> yourHouseStrings = getStrings(true, romEntry.getInt("StarterLocationTextOffset"));
            for (int i = 0; i < 3; i++) {
                yourHouseStrings.set(Gen5Constants.bw1StarterTextOffset - i,
                        "\\xF000\\xBD02\\x0000The " + newStarters.get(i).primaryType.camelCase()
                                + "-type Pok\\x00E9mon\\xFFFE\\xF000\\xBD02\\x0000" + newStarters.get(i).name);
            }
            // Update what the friends say
            yourHouseStrings
                    .set(Gen5Constants.bw1CherenText1Offset,
                            "Cheren: Hey, how come you get to pick\\xFFFEout my Pok\\x00E9mon?"
                                    + "\\xF000\\xBE01\\x0000\\xFFFEOh, never mind. I wanted this one\\xFFFEfrom the start, anyway."
                                    + "\\xF000\\xBE01\\x0000");
            yourHouseStrings.set(Gen5Constants.bw1CherenText2Offset,
                    "It's decided. You'll be my opponent...\\xFFFEin our first Pok\\x00E9mon battle!"
                            + "\\xF000\\xBE01\\x0000\\xFFFELet's see what you can do, \\xFFFEmy Pok\\x00E9mon!"
                            + "\\xF000\\xBE01\\x0000");

            // rewrite
            setStrings(true, romEntry.getInt("StarterLocationTextOffset"), yourHouseStrings);
        } else {
            List<String> starterTownStrings = getStrings(true, romEntry.getInt("StarterLocationTextOffset"));
            for (int i = 0; i < 3; i++) {
                starterTownStrings.set(Gen5Constants.bw2StarterTextOffset - i, "\\xF000\\xBD02\\x0000The "
                        + newStarters.get(i).primaryType.camelCase()
                        + "-type Pok\\x00E9mon\\xFFFE\\xF000\\xBD02\\x0000" + newStarters.get(i).name);
            }
            // Update what the rival says
            starterTownStrings.set(Gen5Constants.bw2RivalTextOffset,
                    "\\xF000\\x0100\\x0001\\x0001: Let's see how good\\xFFFEa Trainer you are!"
                            + "\\xF000\\xBE01\\x0000\\xFFFEI'll use my Pok\\x00E9mon"
                            + "\\xFFFEthat I raised from an Egg!\\xF000\\xBE01\\x0000");

            // rewrite
            setStrings(true, romEntry.getInt("StarterLocationTextOffset"), starterTownStrings);
        }
        return true;
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

    @Override
    public List<Integer> getStarterHeldItems() {
        // do nothing
        return new ArrayList<>();
    }

    @Override
    public void setStarterHeldItems(List<Integer> items) {
        // do nothing
    }

    private void replaceStarterFiles(NARCArchive starterNARC, NARCArchive pokespritesNARC, int starterIndex,
            int pokeNumber) {
        starterNARC.files.set(starterIndex * 2, pokespritesNARC.files.get(pokeNumber * 20 + 18));
        // Get the picture...
        byte[] compressedPic = pokespritesNARC.files.get(pokeNumber * 20);
        // Decompress it with JavaDSDecmp
        byte[] uncompressedPic = DSDecmp.Decompress(compressedPic);
        starterNARC.files.set(12 + starterIndex, uncompressedPic);
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
            NARCArchive encounterNARC = readNARC(romEntry.getFile("WildPokemon"));
            List<EncounterSet> encounters = new ArrayList<>();
            int idx = -1;
            for (byte[] entry : encounterNARC.files) {
                idx++;
                if (entry.length > Gen5Constants.perSeasonEncounterDataLength && useTimeOfDay) {
                    for (int i = 0; i < 4; i++) {
                        processEncounterEntry(encounters, entry, i * Gen5Constants.perSeasonEncounterDataLength, idx);
                    }
                } else {
                    processEncounterEntry(encounters, entry, 0, idx);
                }
            }
            return encounters;
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void processEncounterEntry(List<EncounterSet> encounters, byte[] entry, int startOffset, int idx) {

        if (!wildMapNames.containsKey(idx)) {
            wildMapNames.put(idx, "? Unknown ?");
        }
        String mapName = wildMapNames.get(idx);

        int[] amounts = Gen5Constants.encountersOfEachType;

        int offset = 8;
        for (int i = 0; i < 7; i++) {
            int rate = entry[startOffset + i] & 0xFF;
            if (rate != 0) {
                List<Encounter> encs = readEncounters(entry, startOffset + offset, amounts[i]);
                EncounterSet area = new EncounterSet();
                area.rate = rate;
                area.encounters = encs;
                area.offset = idx;
                area.displayName = mapName + " " + Gen5Constants.encounterTypeNames[i];
                encounters.add(area);
            }
            offset += amounts[i] * 4;
        }

    }

    private List<Encounter> readEncounters(byte[] data, int offset, int number) {
        List<Encounter> encs = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Encounter enc1 = new Encounter();
            int species = readWord(data, offset + i * 4) & 0x7FF;
            int forme = readWord(data, offset + i * 4) >> 11;
            Pokemon baseForme = pokes[species];
            if (forme <= baseForme.cosmeticForms || forme == 30 || forme == 31) {
                enc1.pokemon = pokes[species];
            } else {
                int speciesWithForme = Gen5Constants.getAbsolutePokeNumByBaseForme(species,forme);
                if (speciesWithForme == 0) {
                    enc1.pokemon = pokes[species]; // Failsafe
                } else {
                    enc1.pokemon = pokes[speciesWithForme];
                }
            }
            enc1.formeNumber = forme;
            enc1.level = data[offset + 2 + i * 4] & 0xFF;
            enc1.maxLevel = data[offset + 3 + i * 4] & 0xFF;
            encs.add(enc1);
        }
        return encs;
    }

    @Override
    public void setEncounters(boolean useTimeOfDay, List<EncounterSet> encountersList) {
        try {
            NARCArchive encounterNARC = readNARC(romEntry.getFile("WildPokemon"));
            Iterator<EncounterSet> encounters = encountersList.iterator();
            for (byte[] entry : encounterNARC.files) {
                writeEncounterEntry(encounters, entry, 0);
                if (entry.length > 232) {
                    if (useTimeOfDay) {
                        for (int i = 1; i < 4; i++) {
                            writeEncounterEntry(encounters, entry, i * 232);
                        }
                    } else {
                        // copy for other 3 seasons
                        System.arraycopy(entry, 0, entry, 232, 232);
                        System.arraycopy(entry, 0, entry, 464, 232);
                        System.arraycopy(entry, 0, entry, 696, 232);
                    }
                }
            }

            // Save
            writeNARC(romEntry.getFile("WildPokemon"), encounterNARC);

            this.updatePokedexAreaData(encounterNARC);

            // Habitat List
            if (romEntry.romType == Gen5Constants.Type_BW2) {
                // disabled: habitat list changes cause a crash if too many
                // entries for now.

                // NARCArchive habitatNARC = readNARC(romEntry.getFile("HabitatList"));
                // for (int i = 0; i < habitatNARC.files.size(); i++) {
                // byte[] oldEntry = habitatNARC.files.get(i);
                // int[] encounterFiles = habitatListEntries[i];
                // Map<Pokemon, byte[]> pokemonHere = new TreeMap<Pokemon,
                // byte[]>();
                // for (int encFile : encounterFiles) {
                // byte[] encEntry = encounterNARC.files.get(encFile);
                // if (encEntry.length > 232) {
                // for (int s = 0; s < 4; s++) {
                // addHabitats(encEntry, s * 232, pokemonHere, s);
                // }
                // } else {
                // for (int s = 0; s < 4; s++) {
                // addHabitats(encEntry, 0, pokemonHere, s);
                // }
                // }
                // }
                // // Make the new file
                // byte[] habitatEntry = new byte[10 + pokemonHere.size() * 28];
                // System.arraycopy(oldEntry, 0, habitatEntry, 0, 10);
                // habitatEntry[8] = (byte) pokemonHere.size();
                // // 28-byte entries for each pokemon
                // int num = -1;
                // for (Pokemon pkmn : pokemonHere.keySet()) {
                // num++;
                // writeWord(habitatEntry, 10 + num * 28, pkmn.number);
                // byte[] slots = pokemonHere.get(pkmn);
                // System.arraycopy(slots, 0, habitatEntry, 12 + num * 28,
                // 12);
                // }
                // // Save
                // habitatNARC.files.set(i, habitatEntry);
                // }
                // // Save habitat
                // this.writeNARC(romEntry.getFile("HabitatList"),
                // habitatNARC);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

    }

    private void updatePokedexAreaData(NARCArchive encounterNARC) throws IOException {
        NARCArchive areaNARC = this.readNARC(romEntry.getFile("PokedexAreaData"));
        int areaDataEntryLength = Gen5Constants.getAreaDataEntryLength(romEntry.romType);
        int encounterAreaCount = Gen5Constants.getEncounterAreaCount(romEntry.romType);
        List<byte[]> newFiles = new ArrayList<>();
        for (int i = 0; i < Gen5Constants.pokemonCount; i++) {
            byte[] nf = new byte[areaDataEntryLength];
            newFiles.add(nf);
        }
        // Get data now
        for (int i = 0; i < encounterNARC.files.size(); i++) {
            byte[] encEntry = encounterNARC.files.get(i);
            if (encEntry.length > Gen5Constants.perSeasonEncounterDataLength) {
                for (int season = 0; season < 4; season++) {
                    updateAreaDataFromEncounterEntry(encEntry, season * Gen5Constants.perSeasonEncounterDataLength, newFiles, season, i);
                }
            } else {
                for (int season = 0; season < 4; season++) {
                    updateAreaDataFromEncounterEntry(encEntry, 0, newFiles, season, i);
                }
            }
        }
        // Now update unobtainables, check for seasonal-dependent entries, & save
        for (int i = 0; i < Gen5Constants.pokemonCount; i++) {
            byte[] file = newFiles.get(i);
            for (int season = 0; season < 4; season++) {
                boolean unobtainable = true;
                for (int enc = 0; enc < encounterAreaCount; enc++) {
                    if (file[season * (encounterAreaCount + 1) + enc + 2] != 0) {
                        unobtainable = false;
                        break;
                    }
                }
                if (unobtainable) {
                    file[season * (encounterAreaCount + 1) + 1] = 1;
                }
            }
            boolean seasonalDependent = false;
            for (int enc = 0; enc < encounterAreaCount; enc++) {
                byte springEnc = file[enc + 2];
                byte summerEnc = file[(encounterAreaCount + 1) + enc + 2];
                byte autumnEnc = file[2 * (encounterAreaCount + 1) + enc + 2];
                byte winterEnc = file[3 * (encounterAreaCount + 1) + enc + 2];
                boolean allSeasonsAreTheSame = springEnc == summerEnc && springEnc == autumnEnc && springEnc == winterEnc;
                if (!allSeasonsAreTheSame) {
                    seasonalDependent = true;
                    break;
                }
            }
            if (!seasonalDependent) {
                file[0] = 1;
            }
            areaNARC.files.set(i, file);
        }
        // Save
        this.writeNARC(romEntry.getFile("PokedexAreaData"), areaNARC);
    }

    private void updateAreaDataFromEncounterEntry(byte[] entry, int startOffset, List<byte[]> areaData, int season, int fileNumber) {
        int[] amounts = Gen5Constants.encountersOfEachType;
        int encounterAreaCount = Gen5Constants.getEncounterAreaCount(romEntry.romType);
        int[] wildFileToAreaMap = Gen5Constants.getWildFileToAreaMap(romEntry.romType);

        int offset = 8;
        for (int i = 0; i < 7; i++) {
            int rate = entry[startOffset + i] & 0xFF;
            if (rate != 0) {
                for (int e = 0; e < amounts[i]; e++) {
                    Pokemon pkmn = pokes[((entry[startOffset + offset + e * 4] & 0xFF) + ((entry[startOffset + offset
                            + 1 + e * 4] & 0x03) << 8))];
                    byte[] pokeFile = areaData.get(pkmn.getBaseNumber() - 1);
                    int areaIndex = wildFileToAreaMap[fileNumber];
                    // Route 4?
                    if (romEntry.romType == Gen5Constants.Type_BW2 && areaIndex == Gen5Constants.bw2Route4AreaIndex) {
                        if ((fileNumber == Gen5Constants.b2Route4EncounterFile && romEntry.romCode.charAt(2) == 'D')
                                || (fileNumber == Gen5Constants.w2Route4EncounterFile && romEntry.romCode.charAt(2) == 'E')) {
                            areaIndex = -1; // wrong version
                        }
                    }
                    // Victory Road?
                    if (romEntry.romType == Gen5Constants.Type_BW2 && areaIndex == Gen5Constants.bw2VictoryRoadAreaIndex) {
                        if (romEntry.romCode.charAt(2) == 'D') {
                            // White 2
                            if (fileNumber == Gen5Constants.b2VRExclusiveRoom1
                                    || fileNumber == Gen5Constants.b2VRExclusiveRoom2) {
                                areaIndex = -1; // wrong version
                            }
                        } else {
                            // Black 2
                            if (fileNumber == Gen5Constants.w2VRExclusiveRoom1
                                    || fileNumber == Gen5Constants.w2VRExclusiveRoom2) {
                                areaIndex = -1; // wrong version
                            }
                        }
                    }
                    // Reversal Mountain?
                    if (romEntry.romType == Gen5Constants.Type_BW2 && areaIndex == Gen5Constants.bw2ReversalMountainAreaIndex) {
                        if (romEntry.romCode.charAt(2) == 'D') {
                            // White 2
                            if (fileNumber >= Gen5Constants.b2ReversalMountainStart
                                    && fileNumber <= Gen5Constants.b2ReversalMountainEnd) {
                                areaIndex = -1; // wrong version
                            }
                        } else {
                            // Black 2
                            if (fileNumber >= Gen5Constants.w2ReversalMountainStart
                                    && fileNumber <= Gen5Constants.w2ReversalMountainEnd) {
                                areaIndex = -1; // wrong version
                            }
                        }
                    }
                    // Skip stuff that isn't on the map or is wrong version
                    if (areaIndex != -1) {
                        pokeFile[season * (encounterAreaCount + 1) + 2 + areaIndex] |= (1 << i);
                    }
                }
            }
            offset += amounts[i] * 4;
        }
    }

    @SuppressWarnings("unused")
    private void addHabitats(byte[] entry, int startOffset, Map<Pokemon, byte[]> pokemonHere, int season) {
        int[] amounts = Gen5Constants.encountersOfEachType;
        int[] type = Gen5Constants.habitatClassificationOfEachType;

        int offset = 8;
        for (int i = 0; i < 7; i++) {
            int rate = entry[startOffset + i] & 0xFF;
            if (rate != 0) {
                for (int e = 0; e < amounts[i]; e++) {
                    Pokemon pkmn = pokes[((entry[startOffset + offset + e * 4] & 0xFF) + ((entry[startOffset + offset
                            + 1 + e * 4] & 0x03) << 8))];
                    if (pokemonHere.containsKey(pkmn)) {
                        pokemonHere.get(pkmn)[type[i] + season * 3] = 1;
                    } else {
                        byte[] locs = new byte[12];
                        locs[type[i] + season * 3] = 1;
                        pokemonHere.put(pkmn, locs);
                    }
                }
            }
            offset += amounts[i] * 4;
        }
    }

    private void writeEncounterEntry(Iterator<EncounterSet> encounters, byte[] entry, int startOffset) {
        int[] amounts = Gen5Constants.encountersOfEachType;

        int offset = 8;
        for (int i = 0; i < 7; i++) {
            int rate = entry[startOffset + i] & 0xFF;
            if (rate != 0) {
                EncounterSet area = encounters.next();
                for (int j = 0; j < amounts[i]; j++) {
                    Encounter enc = area.encounters.get(j);
                    int speciesAndFormeData = (enc.formeNumber << 11) + enc.pokemon.getBaseNumber();
                    writeWord(entry, startOffset + offset + j * 4, speciesAndFormeData);
                    entry[startOffset + offset + j * 4 + 2] = (byte) enc.level;
                    entry[startOffset + offset + j * 4 + 3] = (byte) enc.maxLevel;
                }
            }
            offset += amounts[i] * 4;
        }
    }

    private void loadWildMapNames() {
        try {
            wildMapNames = new HashMap<>();
            byte[] mapHeaderData = this.readNARC(romEntry.getFile("MapTableFile")).files.get(0);
            int numMapHeaders = mapHeaderData.length / 48;
            List<String> allMapNames = getStrings(false, romEntry.getInt("MapNamesTextOffset"));
            for (int map = 0; map < numMapHeaders; map++) {
                int baseOffset = map * 48;
                int mapNameIndex = mapHeaderData[baseOffset + 26] & 0xFF;
                String mapName = allMapNames.get(mapNameIndex);
                if (romEntry.romType == Gen5Constants.Type_BW2) {
                    int wildSet = mapHeaderData[baseOffset + 20] & 0xFF;
                    if (wildSet != 255) {
                        wildMapNames.put(wildSet, mapName);
                    }
                } else {
                    int wildSet = readWord(mapHeaderData, baseOffset + 20);
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

    @Override
    public List<Trainer> getTrainers() {
        List<Trainer> allTrainers = new ArrayList<>();
        try {
            NARCArchive trainers = this.readNARC(romEntry.getFile("TrainerData"));
            NARCArchive trpokes = this.readNARC(romEntry.getFile("TrainerPokemon"));
            int trainernum = trainers.files.size();
            List<String> tclasses = this.getTrainerClassNames();
            List<String> tnames = this.getTrainerNames();
            for (int i = 1; i < trainernum; i++) {
                // Trainer entries are 20 bytes
                // Team flags; 1 byte; 0x01 = custom moves, 0x02 = held item
                // Class; 1 byte
                // Battle Mode; 1 byte; 0=single, 1=double, 2=triple, 3=rotation
                // Number of pokemon in team; 1 byte
                // Items; 2 bytes each, 4 item slots
                // AI Flags; 2 byte
                // 2 bytes not used
                // Healer; 1 byte; 0x01 means they will heal player's pokes after defeat.
                // Victory Money; 1 byte; The money given out after defeat =
                //         4 * this value * highest level poke in party
                // Victory Item; 2 bytes; The item given out after defeat (e.g. berries)
                byte[] trainer = trainers.files.get(i);
                byte[] trpoke = trpokes.files.get(i);
                Trainer tr = new Trainer();
                tr.poketype = trainer[0] & 0xFF;
                tr.index = i;
                tr.trainerclass = trainer[1] & 0xFF;
                int numPokes = trainer[3] & 0xFF;
                int pokeOffs = 0;
                tr.fullDisplayName = tclasses.get(tr.trainerclass) + " " + tnames.get(i - 1);
                if (trainer[2] == 1) {
                    originalDoubleTrainers.add(i);
                }
                for (int poke = 0; poke < numPokes; poke++) {
                    // Structure is
                    // IV SB LV LV SP SP FRM FRM
                    // (HI HI)
                    // (M1 M1 M2 M2 M3 M3 M4 M4)
                    // where SB = 0 0 Ab Ab 0 0 Fm Ml
                    // IV is a "difficulty" level between 0 and 255 to represent 0 to 31 IVs.
                    //     These IVs affect all attributes. For the vanilla games, the
                    //     vast majority of trainers have 0 IVs; Elite Four members will
                    //     have 30 IVs.
                    // Ab Ab = ability number, 0 for random
                    // Fm = 1 for forced female
                    // Ml = 1 for forced male
                    // There's also a trainer flag to force gender, but
                    // this allows fixed teams with mixed genders.

                    int difficulty = trpoke[pokeOffs] & 0xFF;
                    int level = readWord(trpoke, pokeOffs + 2);
                    int species = readWord(trpoke, pokeOffs + 4);
                    int formnum = readWord(trpoke, pokeOffs + 6);
                    TrainerPokemon tpk = new TrainerPokemon();
                    tpk.level = level;
                    tpk.pokemon = pokes[species];
                    tpk.IVs = (difficulty) * 31 / 255;
                    int abilityAndFlag = trpoke[pokeOffs + 1];
                    tpk.abilitySlot = (abilityAndFlag >>> 4) & 0xF;
                    tpk.forcedGenderFlag = (abilityAndFlag & 0xF);
                    tpk.forme = formnum;
                    tpk.formeSuffix = Gen5Constants.getFormeSuffixByBaseForme(species,formnum);
                    pokeOffs += 8;
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
                    tr.pokemon.add(tpk);
                }
                allTrainers.add(tr);
            }
            if (romEntry.romType == Gen5Constants.Type_BW) {
                Gen5Constants.tagTrainersBW(allTrainers);
                Gen5Constants.setMultiBattleStatusBW(allTrainers);
            } else {
                if (!romEntry.getFile("DriftveilPokemon").isEmpty()) {
                    NARCArchive driftveil = this.readNARC(romEntry.getFile("DriftveilPokemon"));
                    int currentFile = 1;
                    for (int trno = 0; trno < 17; trno++) {
                        Trainer tr = new Trainer();
                        tr.poketype = 3; // have held items and custom moves
                        int nameAndClassIndex = Gen5Constants.bw2DriftveilTrainerOffsets.get(trno);
                        tr.fullDisplayName = tclasses.get(Gen5Constants.normalTrainerClassLength + nameAndClassIndex) + " " + tnames.get(Gen5Constants.normalTrainerNameLength + nameAndClassIndex);
                        tr.requiresUniqueHeldItems = true;
                        int pokemonNum = 6;
                        if (trno < 2) {
                            pokemonNum = 3;
                        }
                        for (int poke = 0; poke < pokemonNum; poke++) {
                            byte[] pkmndata = driftveil.files.get(currentFile);
                            int species = readWord(pkmndata, 0);
                            TrainerPokemon tpk = new TrainerPokemon();
                            tpk.level = 25;
                            tpk.pokemon = pokes[species];
                            tpk.IVs = 31;
                            tpk.heldItem = readWord(pkmndata, 12);
                            for (int move = 0; move < 4; move++) {
                                tpk.moves[move] = readWord(pkmndata, 2 + (move*2));
                            }
                            tr.pokemon.add(tpk);
                            currentFile++;
                        }
                        allTrainers.add(tr);
                    }
                }
                boolean isBlack2 = romEntry.romCode.startsWith("IRE");
                Gen5Constants.tagTrainersBW2(allTrainers);
                Gen5Constants.setMultiBattleStatusBW2(allTrainers, isBlack2);
            }
        } catch (IOException ex) {
            throw new RandomizerIOException(ex);
        }
        return allTrainers;
    }

    @Override
    public List<Integer> getMainPlaythroughTrainers() {
        if (romEntry.romType == Gen5Constants.Type_BW) { // BW1
            return Gen5Constants.bw1MainPlaythroughTrainers;
        }
        else if (romEntry.romType == Gen5Constants.Type_BW2) { // BW2
            return Gen5Constants.bw2MainPlaythroughTrainers;
        }
        else {
            return Gen5Constants.emptyPlaythroughTrainers;
        }
    }

    @Override
    public List<Integer> getEliteFourTrainers(boolean isChallengeMode) {
        if (isChallengeMode) {
            return Arrays.stream(romEntry.arrayEntries.get("ChallengeModeEliteFourIndices")).boxed().collect(Collectors.toList());
        } else {
            return Arrays.stream(romEntry.arrayEntries.get("EliteFourIndices")).boxed().collect(Collectors.toList());
        }
    }


    @Override
    public List<Integer> getEvolutionItems() {
            return Gen5Constants.evolutionItems;
        }

    @Override
    public void setTrainers(List<Trainer> trainerData, boolean doubleBattleMode) {
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
                // preserve original poketype for held item & moves
                trainer[0] = (byte) tr.poketype;
                int numPokes = tr.pokemon.size();
                trainer[3] = (byte) numPokes;

                if (doubleBattleMode) {
                    if (!tr.skipImportant()) {
                        if (trainer[2] == 0) {
                            trainer[2] = 1;
                            trainer[12] |= 0x80; // Flag that needs to be set for trainers not to attack their own pokes
                        }
                    }
                }

                int bytesNeeded = 8 * numPokes;
                if (tr.pokemonHaveCustomMoves()) {
                    bytesNeeded += 8 * numPokes;
                }
                if (tr.pokemonHaveItems()) {
                    bytesNeeded += 2 * numPokes;
                }
                byte[] trpoke = new byte[bytesNeeded];
                int pokeOffs = 0;
                Iterator<TrainerPokemon> tpokes = tr.pokemon.iterator();
                for (int poke = 0; poke < numPokes; poke++) {
                    TrainerPokemon tp = tpokes.next();
                    // Add 1 to offset integer division truncation
                    int difficulty = Math.min(255, 1 + (tp.IVs * 255) / 31);
                    byte abilityAndFlag = (byte)((tp.abilitySlot << 4) | tp.forcedGenderFlag);
                    writeWord(trpoke, pokeOffs, difficulty | abilityAndFlag << 8);
                    writeWord(trpoke, pokeOffs + 2, tp.level);
                    writeWord(trpoke, pokeOffs + 4, tp.pokemon.number);
                    writeWord(trpoke, pokeOffs + 6, tp.forme);
                    // no form info, so no byte 6/7
                    pokeOffs += 8;
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
                }
                trpokes.files.add(trpoke);
            }
            this.writeNARC(romEntry.getFile("TrainerData"), trainers);
            this.writeNARC(romEntry.getFile("TrainerPokemon"), trpokes);

            if (doubleBattleMode) {

                NARCArchive trainerTextBoxes = readNARC(romEntry.getFile("TrainerTextBoxes"));
                byte[] data = trainerTextBoxes.files.get(0);
                for (int i = 0; i < data.length; i += 4) {
                    int trainerIndex = readWord(data, i);
                    if (originalDoubleTrainers.contains(trainerIndex)) {
                        int textBoxIndex = readWord(data, i+2);
                        if (textBoxIndex == 3) {
                            writeWord(data, i+2, 0);
                        } else if (textBoxIndex == 5) {
                            writeWord(data, i+2, 2);
                        } else if (textBoxIndex == 6) {
                            writeWord(data, i+2, 0x18);
                        }
                    }
                }

                trainerTextBoxes.files.set(0, data);
                writeNARC(romEntry.getFile("TrainerTextBoxes"), trainerTextBoxes);


                try {
                    byte[] fieldOverlay = readOverlay(romEntry.getInt("FieldOvlNumber"));
                    String trainerOverworldTextBoxPrefix = romEntry.getString("TrainerOverworldTextBoxPrefix");
                    int offset = find(fieldOverlay, trainerOverworldTextBoxPrefix);
                    if (offset > 0) {
                        offset += trainerOverworldTextBoxPrefix.length() / 2; // because it was a prefix
                        // Overwrite text box values for trainer 1 in a doubles pair to use the same as a single trainer
                        fieldOverlay[offset-2] = 0;
                        fieldOverlay[offset] = 2;
                        fieldOverlay[offset+2] = 0x18;
                    } else {
                        throw new RandomizationException("Double Battle Mode not supported for this game");
                    }

                    String doubleBattleLimitPrefix = romEntry.getString("DoubleBattleLimitPrefix");
                    offset = find(fieldOverlay, doubleBattleLimitPrefix);
                    if (offset > 0) {
                        offset += trainerOverworldTextBoxPrefix.length() / 2; // because it was a prefix
                        // No limit for doubles trainers, i.e. they will spot you even if you have a single Pokemon
                        writeWord(fieldOverlay, offset, 0x46C0);           // nop
                        writeWord(fieldOverlay, offset+2, 0x46C0);  // nop
                    } else {
                        throw new RandomizationException("Double Battle Mode not supported for this game");
                    }

                    String doubleBattleGetPointerPrefix = romEntry.getString("DoubleBattleGetPointerPrefix");
                    int beqToSingleTrainer = romEntry.getInt("BeqToSingleTrainerNumber");
                    offset = find(fieldOverlay, doubleBattleGetPointerPrefix);
                    if (offset > 0) {
                        offset += trainerOverworldTextBoxPrefix.length() / 2; // because it was a prefix
                        // Move some instructions up
                        writeWord(fieldOverlay, offset + 0x10, readWord(fieldOverlay, offset + 0xE));
                        writeWord(fieldOverlay, offset + 0xE, readWord(fieldOverlay, offset + 0xC));
                        writeWord(fieldOverlay, offset + 0xC, readWord(fieldOverlay, offset + 0xA));
                        // Add a beq and cmp to go to the "single trainer" case if a certain pointer is 0
                        writeWord(fieldOverlay, offset + 0xA, beqToSingleTrainer);
                        writeWord(fieldOverlay, offset + 8, 0x2800);
                    } else {
                        throw new RandomizationException("Double Battle Mode not supported for this game");
                    }

                    writeOverlay(romEntry.getInt("FieldOvlNumber"), fieldOverlay);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String textBoxChoicePrefix = romEntry.getString("TextBoxChoicePrefix");
                int offset = find(arm9,textBoxChoicePrefix);

                if (offset > 0) {
                    // Change a branch destination in order to only check the relevant trainer instead of checking
                    // every trainer in the game (will result in incorrect text boxes when being spotted by doubles
                    // pairs, but this is better than the game freezing for half a second and getting a blank text box)
                    offset += textBoxChoicePrefix.length() / 2;
                    arm9[offset-4] = 2;
                } else {
                    throw new RandomizationException("Double Battle Mode not supported for this game");
                }

            }

            // Deal with PWT
            if (romEntry.romType == Gen5Constants.Type_BW2 && !romEntry.getFile("DriftveilPokemon").isEmpty()) {
                NARCArchive driftveil = this.readNARC(romEntry.getFile("DriftveilPokemon"));
                int currentFile = 1;
                for (int trno = 0; trno < 17; trno++) {
                    Trainer tr = allTrainers.next();
                    Iterator<TrainerPokemon> tpks = tr.pokemon.iterator();
                    int pokemonNum = 6;
                    if (trno < 2) {
                        pokemonNum = 3;
                    }
                    for (int poke = 0; poke < pokemonNum; poke++) {
                        byte[] pkmndata = driftveil.files.get(currentFile);
                        TrainerPokemon tp = tpks.next();
                        // pokemon and held item
                        writeWord(pkmndata, 0, tp.pokemon.number);
                        writeWord(pkmndata, 12, tp.heldItem);
                        // handle moves
                        if (tp.resetMoves) {
                            int[] pokeMoves = RomFunctions.getMovesAtLevel(tp.pokemon.number, movesets, tp.level);
                            for (int m = 0; m < 4; m++) {
                                writeWord(pkmndata, 2 + m * 2, pokeMoves[m]);
                            }
                        } else {
                            writeWord(pkmndata, 2, tp.moves[0]);
                            writeWord(pkmndata, 4, tp.moves[1]);
                            writeWord(pkmndata, 6, tp.moves[2]);
                            writeWord(pkmndata, 8, tp.moves[3]);
                        }
                        currentFile++;
                    }
                }
                this.writeNARC(romEntry.getFile("DriftveilPokemon"), driftveil);
            }
        } catch (IOException ex) {
            throw new RandomizerIOException(ex);
        }
    }

    @Override
    public Map<Integer, List<MoveLearnt>> getMovesLearnt() {
        Map<Integer, List<MoveLearnt>> movesets = new TreeMap<>();
        try {
            NARCArchive movesLearnt = this.readNARC(romEntry.getFile("PokemonMovesets"));
            int formeCount = Gen5Constants.getFormeCount(romEntry.romType);
            int formeOffset = Gen5Constants.getFormeOffset(romEntry.romType);
            for (int i = 1; i <= Gen5Constants.pokemonCount + formeCount; i++) {
                Pokemon pkmn = pokes[i];
                byte[] movedata;
                if (i > Gen5Constants.pokemonCount) {
                    movedata = movesLearnt.files.get(i + formeOffset);
                } else {
                    movedata = movesLearnt.files.get(i);
                }
                int moveDataLoc = 0;
                List<MoveLearnt> learnt = new ArrayList<>();
                while (readWord(movedata, moveDataLoc) != 0xFFFF || readWord(movedata, moveDataLoc + 2) != 0xFFFF) {
                    int move = readWord(movedata, moveDataLoc);
                    int level = readWord(movedata, moveDataLoc + 2);
                    MoveLearnt ml = new MoveLearnt();
                    ml.level = level;
                    ml.move = move;
                    learnt.add(ml);
                    moveDataLoc += 4;
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
        try {
            NARCArchive movesLearnt = readNARC(romEntry.getFile("PokemonMovesets"));
            int formeCount = Gen5Constants.getFormeCount(romEntry.romType);
            int formeOffset = Gen5Constants.getFormeOffset(romEntry.romType);
            for (int i = 1; i <= Gen5Constants.pokemonCount + formeCount; i++) {
                Pokemon pkmn = pokes[i];
                List<MoveLearnt> learnt = movesets.get(pkmn.number);
                int sizeNeeded = learnt.size() * 4 + 4;
                byte[] moveset = new byte[sizeNeeded];
                int j = 0;
                for (; j < learnt.size(); j++) {
                    MoveLearnt ml = learnt.get(j);
                    writeWord(moveset, j * 4, ml.move);
                    writeWord(moveset, j * 4 + 2, ml.level);
                }
                writeWord(moveset, j * 4, 0xFFFF);
                writeWord(moveset, j * 4 + 2, 0xFFFF);
                if (i > Gen5Constants.pokemonCount) {
                    movesLearnt.files.set(i + formeOffset, moveset);
                } else {
                    movesLearnt.files.set(i, moveset);
                }
            }
            // Save
            this.writeNARC(romEntry.getFile("PokemonMovesets"), movesLearnt);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

    }

    @Override
    public Map<Integer, List<Integer>> getEggMoves() {
        Map<Integer, List<Integer>> eggMoves = new TreeMap<>();
        try {
            NARCArchive eggMovesNarc = this.readNARC(romEntry.getFile("EggMoves"));
            for (int i = 1; i <= Gen5Constants.pokemonCount; i++) {
                Pokemon pkmn = pokes[i];
                byte[] movedata = eggMovesNarc.files.get(i);
                int numberOfEggMoves = readWord(movedata, 0);
                List<Integer> moves = new ArrayList<>();
                for (int j = 0; j < numberOfEggMoves; j++) {
                    int move = readWord(movedata, 2 + (j * 2));
                    moves.add(move);
                }
                eggMoves.put(pkmn.number, moves);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return eggMoves;
    }

    @Override
    public void setEggMoves(Map<Integer, List<Integer>> eggMoves) {
        try {
            NARCArchive eggMovesNarc = this.readNARC(romEntry.getFile("EggMoves"));
            for (int i = 1; i <= Gen5Constants.pokemonCount; i++) {
                Pokemon pkmn = pokes[i];
                byte[] movedata = eggMovesNarc.files.get(i);
                List<Integer> moves = eggMoves.get(pkmn.number);
                for (int j = 0; j < moves.size(); j++) {
                    writeWord(movedata, 2 + (j * 2), moves.get(j));
                }
            }
            // Save
            this.writeNARC(romEntry.getFile("EggMoves"), eggMovesNarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private static class FileEntry {
        private int file;
        private int offset;

        public FileEntry(int file, int offset) {
            this.file = file;
            this.offset = offset;
        }
    }

    private static class StaticPokemon {
        private FileEntry[] speciesEntries;
        private FileEntry[] formeEntries;
        private FileEntry[] levelEntries;

        public StaticPokemon() {
            this.speciesEntries = new FileEntry[0];
            this.formeEntries = new FileEntry[0];
            this.levelEntries = new FileEntry[0];
        }

        public Pokemon getPokemon(Gen5RomHandler parent, NARCArchive scriptNARC) {
            return parent.pokes[parent.readWord(scriptNARC.files.get(speciesEntries[0].file), speciesEntries[0].offset)];
        }

        public void setPokemon(Gen5RomHandler parent, NARCArchive scriptNARC, Pokemon pkmn) {
            int value = pkmn.number;
            for (int i = 0; i < speciesEntries.length; i++) {
                byte[] file = scriptNARC.files.get(speciesEntries[i].file);
                parent.writeWord(file, speciesEntries[i].offset, value);
            }
        }

        public int getForme(NARCArchive scriptNARC) {
            if (formeEntries.length == 0) {
                return 0;
            }
            byte[] file = scriptNARC.files.get(formeEntries[0].file);
            return file[formeEntries[0].offset];
        }

        public void setForme(NARCArchive scriptNARC, int forme) {
            for (int i = 0; i < formeEntries.length; i++) {
                byte[] file = scriptNARC.files.get(formeEntries[i].file);
                file[formeEntries[i].offset] = (byte) forme;
            }
        }

        public int getLevelCount() {
            return levelEntries.length;
        }

        public int getLevel(NARCArchive scriptOrMapNARC, int i) {
            if (levelEntries.length <= i) {
                return 1;
            }
            byte[] file = scriptOrMapNARC.files.get(levelEntries[i].file);
            return file[levelEntries[i].offset];
        }

        public void setLevel(NARCArchive scriptOrMapNARC, int level, int i) {
            if (levelEntries.length > i) { // Might not have a level entry e.g., it's an egg
                byte[] file = scriptOrMapNARC.files.get(levelEntries[i].file);
                file[levelEntries[i].offset] = (byte) level;
            }
        }
    }

    private static class RoamingPokemon {
        private int[] speciesOverlayOffsets;
        private int[] levelOverlayOffsets;
        private FileEntry[] speciesScriptOffsets;

        public RoamingPokemon() {
            this.speciesOverlayOffsets = new int[0];
            this.levelOverlayOffsets = new int[0];
            this.speciesScriptOffsets = new FileEntry[0];
        }

        public Pokemon getPokemon(Gen5RomHandler parent) throws IOException {
            byte[] overlay = parent.readOverlay(parent.romEntry.getInt("RoamerOvlNumber"));
            int species = parent.readWord(overlay, speciesOverlayOffsets[0]);
            return parent.pokes[species];
        }

        public void setPokemon(Gen5RomHandler parent, NARCArchive scriptNARC, Pokemon pkmn) throws IOException {
            int value = pkmn.number;
            byte[] overlay = parent.readOverlay(parent.romEntry.getInt("RoamerOvlNumber"));
            for (int speciesOverlayOffset : speciesOverlayOffsets) {
                parent.writeWord(overlay, speciesOverlayOffset, value);
            }
            parent.writeOverlay(parent.romEntry.getInt("RoamerOvlNumber"), overlay);
            for (FileEntry speciesScriptOffset : speciesScriptOffsets) {
                byte[] file = scriptNARC.files.get(speciesScriptOffset.file);
                parent.writeWord(file, speciesScriptOffset.offset, value);
            }
        }

        public int getLevel(Gen5RomHandler parent) throws IOException {
            if (levelOverlayOffsets.length == 0) {
                return 1;
            }
            byte[] overlay = parent.readOverlay(parent.romEntry.getInt("RoamerOvlNumber"));
            return overlay[levelOverlayOffsets[0]];
        }

        public void setLevel(Gen5RomHandler parent, int level) throws IOException {
            byte[] overlay = parent.readOverlay(parent.romEntry.getInt("RoamerOvlNumber"));
            for (int levelOverlayOffset : levelOverlayOffsets) {
                overlay[levelOverlayOffset] = (byte) level;
            }
            parent.writeOverlay(parent.romEntry.getInt("RoamerOvlNumber"), overlay);
        }
    }

    private static class TradeScript {
        private int fileNum;
        private int[] requestedOffsets;
        private int[] givenOffsets;

        public void setPokemon(Gen5RomHandler parent, NARCArchive scriptNARC, Pokemon requested, Pokemon given) {
            int req = requested.number;
            int giv = given.number;
            for (int i = 0; i < requestedOffsets.length; i++) {
                byte[] file = scriptNARC.files.get(fileNum);
                parent.writeWord(file, requestedOffsets[i], req);
                parent.writeWord(file, givenOffsets[i], giv);
            }
        }
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
    public void applyCorrectStaticMusic(Map<Integer, Integer> specialMusicStaticChanges) {

        try {
            byte[] fieldOverlay = readOverlay(romEntry.getInt("FieldOvlNumber"));
            genericIPSPatch(fieldOverlay, "NewIndexToMusicOvlTweak");
            writeOverlay(romEntry.getInt("FieldOvlNumber"), fieldOverlay);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int extendBy = romEntry.getInt("NewIndexToMusicSize");
        arm9 = extendARM9(arm9, extendBy, romEntry.getString("TCMCopyingPrefix"), Gen5Constants.arm9Offset);
        genericIPSPatch(arm9, "NewIndexToMusicTweak");

        String newIndexToMusicPrefix = romEntry.getString("NewIndexToMusicPrefix");
        int newIndexToMusicPoolOffset = find(arm9, newIndexToMusicPrefix);
        newIndexToMusicPoolOffset += newIndexToMusicPrefix.length() / 2;

        List<Integer> replaced = new ArrayList<>();
        int iMax = -1;

        switch(romEntry.romType) {
            case Gen5Constants.Type_BW:
                for (int oldStatic: specialMusicStaticChanges.keySet()) {
                    int i = newIndexToMusicPoolOffset;
                    int index = readWord(arm9, i);
                    while (index != oldStatic || replaced.contains(i)) {
                        i += 4;
                        index = readWord(arm9, i);
                    }
                    writeWord(arm9, i, specialMusicStaticChanges.get(oldStatic));
                    replaced.add(i);
                    if (i > iMax) iMax = i;
                }
                break;
            case Gen5Constants.Type_BW2:
                for (int oldStatic: specialMusicStaticChanges.keySet()) {
                    int i = newIndexToMusicPoolOffset;
                    int index = readWord(arm9, i);
                    while (index != oldStatic || replaced.contains(i)) {
                        i += 4;
                        index = readWord(arm9, i);
                    }
                    // Special Kyurem-B/W handling
                    if (index > Gen5Constants.pokemonCount) {
                        writeWord(arm9, i - 0xFE, 0);
                        writeWord(arm9, i - 0xFC, 0);
                        writeWord(arm9, i - 0xFA, 0);
                        writeWord(arm9, i - 0xF8, 0x4290);
                    }
                    writeWord(arm9, i, specialMusicStaticChanges.get(oldStatic));
                    replaced.add(i);
                    if (i > iMax) iMax = i;
                }
                break;
        }

        List<Integer> specialMusicStatics = getSpecialMusicStatics();

        for (int i = newIndexToMusicPoolOffset; i <= iMax; i+= 4) {
            if (!replaced.contains(i)) {
                int pkID = readWord(arm9, i);

                // If a Pokemon is a "special music static" but the music hasn't been replaced, leave as is
                // Otherwise zero it out, because the original static encounter doesn't exist
                if (!specialMusicStatics.contains(pkID)) {
                    writeWord(arm9, i, 0);
                }
            }
        }

    }

    @Override
    public boolean hasStaticMusicFix() {
        return romEntry.tweakFiles.get("NewIndexToMusicTweak") != null;
    }

    @Override
    public List<TotemPokemon> getTotemPokemon() {
        return new ArrayList<>();
    }

    @Override
    public void setTotemPokemon(List<TotemPokemon> totemPokemon) {

    }

    @Override
    public List<StaticEncounter> getStaticPokemon() {
        List<StaticEncounter> sp = new ArrayList<>();
        if (!romEntry.staticPokemonSupport) {
            return sp;
        }
        int[] staticEggOffsets = new int[0];
        if (romEntry.arrayEntries.containsKey("StaticEggPokemonOffsets")) {
            staticEggOffsets = romEntry.arrayEntries.get("StaticEggPokemonOffsets");
        }

        // Regular static encounters
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

        // Foongus/Amoongus fake ball encounters
        try {
            NARCArchive mapNARC = readNARC(romEntry.getFile("MapFiles"));
            for (int i = 0; i < romEntry.staticPokemonFakeBall.size(); i++) {
                StaticPokemon statP = romEntry.staticPokemonFakeBall.get(i);
                StaticEncounter se = new StaticEncounter();
                Pokemon newPK = statP.getPokemon(this, scriptNARC);
                se.pkmn = newPK;
                se.level = statP.getLevel(mapNARC, 0);
                for (int levelEntry = 1; levelEntry < statP.getLevelCount(); levelEntry++) {
                    StaticEncounter linkedStatic = new StaticEncounter();
                    linkedStatic.pkmn = newPK;
                    linkedStatic.level = statP.getLevel(mapNARC, levelEntry);
                    se.linkedEncounters.add(linkedStatic);
                }
                sp.add(se);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        // BW2 hidden grotto encounters
        if (romEntry.romType == Gen5Constants.Type_BW2) {
            List<Pokemon> allowedHiddenHollowPokemon = new ArrayList<>();
            allowedHiddenHollowPokemon.addAll(Arrays.asList(Arrays.copyOfRange(pokes,1,494)));
            allowedHiddenHollowPokemon.addAll(
                    Gen5Constants.bw2HiddenHollowUnovaPokemon.stream().map(i -> pokes[i]).collect(Collectors.toList()));

            try {
                NARCArchive hhNARC = this.readNARC(romEntry.getFile("HiddenHollows"));
                for (byte[] hhEntry : hhNARC.files) {
                    for (int version = 0; version < 2; version++) {
                        if (version != romEntry.getInt("HiddenHollowIndex")) continue;
                        for (int raritySlot = 0; raritySlot < 3; raritySlot++) {
                            List<StaticEncounter> encountersInGroup = new ArrayList<>();
                            for (int group = 0; group < 4; group++) {
                                StaticEncounter se = new StaticEncounter();
                                Pokemon newPK = pokes[readWord(hhEntry, version * 78 + raritySlot * 26 + group * 2)];
                                newPK = getAltFormeOfPokemon(newPK, hhEntry[version * 78 + raritySlot * 26 + 20 + group]);
                                se.pkmn = newPK;
                                se.level = hhEntry[version * 78 + raritySlot * 26 + 12 + group];
                                se.maxLevel = hhEntry[version * 78 + raritySlot * 26 + 8 + group];
                                se.isEgg = false;
                                se.restrictedPool = true;
                                se.restrictedList = allowedHiddenHollowPokemon;
                                boolean originalEncounter = true;
                                for (StaticEncounter encounterInGroup: encountersInGroup) {
                                    if (encounterInGroup.pkmn.equals(se.pkmn)) {
                                        encounterInGroup.linkedEncounters.add(se);
                                        originalEncounter = false;
                                        break;
                                    }
                                }
                                if (originalEncounter) {
                                    encountersInGroup.add(se);
                                    sp.add(se);
                                    if (!hiddenHollowCounted) {
                                        hiddenHollowCount++;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RandomizerIOException(e);
            }
        }
        hiddenHollowCounted = true;

        // Roaming encounters
        if (romEntry.roamingPokemon.size() > 0) {
            try {
                int firstSpeciesOffset = romEntry.roamingPokemon.get(0).speciesOverlayOffsets[0];
                byte[] overlay = readOverlay(romEntry.getInt("RoamerOvlNumber"));
                if (readWord(overlay, firstSpeciesOffset) > pokes.length) {
                    // In the original code, this is "mov r0, #0x2", which read as a word is
                    // 0x2002, much larger than the number of species in the game.
                    applyBlackWhiteRoamerPatch();
                }
                for (int i = 0; i < romEntry.roamingPokemon.size(); i++) {
                    RoamingPokemon roamer = romEntry.roamingPokemon.get(i);
                    StaticEncounter se = new StaticEncounter();
                    se.pkmn = roamer.getPokemon(this);
                    se.level = roamer.getLevel(this);
                    sp.add(se);
                }
            } catch (Exception e) {
                throw new RandomizerIOException(e);
            }
        }

        return sp;
    }

    @Override
    public boolean setStaticPokemon(List<StaticEncounter> staticPokemon) {
        if (!romEntry.staticPokemonSupport) {
            return false;
        }
        if (staticPokemon.size() != (romEntry.staticPokemon.size() + romEntry.staticPokemonFakeBall.size() + hiddenHollowCount + romEntry.roamingPokemon.size())) {
            return false;
        }
        Iterator<StaticEncounter> statics = staticPokemon.iterator();

        // Regular static encounters
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

        // Foongus/Amoongus fake ball encounters
        try {
            NARCArchive mapNARC = readNARC(romEntry.getFile("MapFiles"));
            for (StaticPokemon statP : romEntry.staticPokemonFakeBall) {
                StaticEncounter se = statics.next();
                statP.setPokemon(this, scriptNARC, se.pkmn);
                statP.setLevel(mapNARC, se.level, 0);
                for (int i = 0; i < se.linkedEncounters.size(); i++) {
                    StaticEncounter linkedStatic = se.linkedEncounters.get(i);
                    statP.setLevel(mapNARC, linkedStatic.level, i + 1);
                }
            }
            this.writeNARC(romEntry.getFile("MapFiles"), mapNARC);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        // BW2 hidden grotto encounters
        if (romEntry.romType == Gen5Constants.Type_BW2) {
            try {
                NARCArchive hhNARC = this.readNARC(romEntry.getFile("HiddenHollows"));
                for (byte[] hhEntry : hhNARC.files) {
                    for (int version = 0; version < 2; version++) {
                        if (version != romEntry.getInt("HiddenHollowIndex")) continue;
                        for (int raritySlot = 0; raritySlot < 3; raritySlot++) {
                            for (int group = 0; group < 4; group++) {
                                StaticEncounter se = statics.next();
                                writeWord(hhEntry, version * 78 + raritySlot * 26 + group * 2, se.pkmn.number);
                                int genderRatio = this.random.nextInt(101);
                                hhEntry[version * 78 + raritySlot * 26 + 16 + group] = (byte) genderRatio;
                                hhEntry[version * 78 + raritySlot * 26 + 20 + group] = (byte) se.forme; // forme
                                hhEntry[version * 78 + raritySlot * 26 + 12 + group] = (byte) se.level;
                                hhEntry[version * 78 + raritySlot * 26 + 8 + group] = (byte) se.maxLevel;
                                for (int i = 0; i < se.linkedEncounters.size(); i++) {
                                    StaticEncounter linkedStatic = se.linkedEncounters.get(i);
                                    group++;
                                    writeWord(hhEntry, version * 78 + raritySlot * 26 + group * 2, linkedStatic.pkmn.number);
                                    hhEntry[version * 78 + raritySlot * 26 + 16 + group] = (byte) genderRatio;
                                    hhEntry[version * 78 + raritySlot * 26 + 20 + group] = (byte) linkedStatic.forme; // forme
                                    hhEntry[version * 78 + raritySlot * 26 + 12 + group] = (byte) linkedStatic.level;
                                    hhEntry[version * 78 + raritySlot * 26 + 8 + group] = (byte) linkedStatic.maxLevel;
                                }
                            }
                        }
                    }
                }
                this.writeNARC(romEntry.getFile("HiddenHollows"), hhNARC);
            } catch (IOException e) {
                throw new RandomizerIOException(e);
            }
        }

        // Roaming encounters
        try {
            for (int i = 0; i < romEntry.roamingPokemon.size(); i++) {
                RoamingPokemon roamer = romEntry.roamingPokemon.get(i);
                StaticEncounter roamerEncounter = statics.next();
                roamer.setPokemon(this, scriptNarc, roamerEncounter.pkmn);
                roamer.setLevel(this, roamerEncounter.level);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        // In Black/White, the game has multiple hardcoded checks for Reshiram/Zekrom's species
        // ID in order to properly move it out of a box and into the first slot of the player's
        // party. We need to replace these checks with the species ID of whatever occupies
        // Reshiram/Zekrom's static encounter for the game to still function properly.
        if (romEntry.romType == Gen5Constants.Type_BW) {
            int boxLegendaryIndex = romEntry.getInt("BoxLegendaryOffset");
            try {
                int boxLegendarySpecies = staticPokemon.get(boxLegendaryIndex).pkmn.number;
                fixBoxLegendaryBW1(boxLegendarySpecies);
            } catch (IOException e) {
                throw new RandomizerIOException(e);
            }
        }

        return true;
    }

    private void fixBoxLegendaryBW1(int boxLegendarySpecies) throws IOException {
        byte[] boxLegendaryOverlay = readOverlay(romEntry.getInt("FieldOvlNumber"));
        if (romEntry.isBlack) {
            // In Black, Reshiram's species ID is always retrieved via a pc-relative
            // load to some constant. All we need to is replace these constants with
            // the new species ID.
            int firstConstantOffset = find(boxLegendaryOverlay, Gen5Constants.blackBoxLegendaryCheckPrefix1);
            if (firstConstantOffset > 0) {
                firstConstantOffset += Gen5Constants.blackBoxLegendaryCheckPrefix1.length() / 2; // because it was a prefix
                FileFunctions.writeFullInt(boxLegendaryOverlay, firstConstantOffset, boxLegendarySpecies);
            }
            int secondConstantOffset = find(boxLegendaryOverlay, Gen5Constants.blackBoxLegendaryCheckPrefix2);
            if (secondConstantOffset > 0) {
                secondConstantOffset += Gen5Constants.blackBoxLegendaryCheckPrefix2.length() / 2; // because it was a prefix
                FileFunctions.writeFullInt(boxLegendaryOverlay, secondConstantOffset, boxLegendarySpecies);
            }
        } else {
            // In White, Zekrom's species ID is always loaded by loading 161 into a register
            // and then shifting left by 2. Thus, we need to be more clever with how we
            // modify code in order to set up some pc-relative loads.
            int firstFunctionOffset = find(boxLegendaryOverlay, Gen5Constants.whiteBoxLegendaryCheckPrefix1);
            if (firstFunctionOffset > 0) {
                firstFunctionOffset += Gen5Constants.whiteBoxLegendaryCheckPrefix1.length() / 2; // because it was a prefix

                // First, nop the instruction that loads a pointer to the string
                // "scrcmd_pokemon_fld.c" into a register; this has seemingly no
                // effect on the game and was probably used strictly for debugging.
                boxLegendaryOverlay[firstFunctionOffset + 66] = 0x00;
                boxLegendaryOverlay[firstFunctionOffset + 67] = 0x00;

                // In the space that used to hold the address of the "scrcmd_pokemon_fld.c"
                // string, we're going to instead store the species ID of the box legendary
                // so that we can do a pc-relative load to it.
                FileFunctions.writeFullInt(boxLegendaryOverlay, firstFunctionOffset + 320, boxLegendarySpecies);

                // Zekrom's species ID is originally loaded by doing a mov into r1 and then a shift
                // on that same register four instructions later. This nops out the first instruction
                // and replaces the left shift with a pc-relative load to the constant we stored above.
                boxLegendaryOverlay[firstFunctionOffset + 18] = 0x00;
                boxLegendaryOverlay[firstFunctionOffset + 19] = 0x00;
                boxLegendaryOverlay[firstFunctionOffset + 26] = 0x49;
                boxLegendaryOverlay[firstFunctionOffset + 27] = 0x49;
            }

            int secondFunctionOffset = find(boxLegendaryOverlay, Gen5Constants.whiteBoxLegendaryCheckPrefix2);
            if (secondFunctionOffset > 0) {
                secondFunctionOffset += Gen5Constants.whiteBoxLegendaryCheckPrefix2.length() / 2; // because it was a prefix

                // A completely unrelated function below this one decides to pc-relative load 0x00000000 into r4
                // instead of just doing a mov. We can replace it with a simple "mov r4, #0x0", but we have to be
                // careful about where we put it. The original code calls a function, performs an "add r6, r0, #0x0",
                // then does the load into r4. This means that whether or not the Z bit is set depends on the result
                // of the function call. If we naively replace the load with our mov, we'll be forcibly setting the Z
                // bit to 1, which will cause the subsequent beq to potentially take us to the wrong place. To get
                // around this, we reorder the code so the "mov r4, #0x0" occurs *before* the "add r6, r0, #0x0".
                boxLegendaryOverlay[secondFunctionOffset + 502] = 0x00;
                boxLegendaryOverlay[secondFunctionOffset + 503] = 0x24;
                boxLegendaryOverlay[secondFunctionOffset + 504] = 0x06;
                boxLegendaryOverlay[secondFunctionOffset + 505] = 0x1C;

                // Now replace the 0x00000000 constant with the species ID
                FileFunctions.writeFullInt(boxLegendaryOverlay, secondFunctionOffset + 556, boxLegendarySpecies);

                // Lastly, replace the mov and lsl that originally puts Zekrom's species ID into r1
                // with a pc-relative of the above constant and a nop.
                boxLegendaryOverlay[secondFunctionOffset + 78] = 0x77;
                boxLegendaryOverlay[secondFunctionOffset + 79] = 0x49;
                boxLegendaryOverlay[secondFunctionOffset + 80] = 0x00;
                boxLegendaryOverlay[secondFunctionOffset + 81] = 0x00;
            }
        }
        writeOverlay(romEntry.getInt("FieldOvlNumber"), boxLegendaryOverlay);
    }

    private void applyBlackWhiteRoamerPatch() throws IOException {
        int offset = romEntry.getInt("GetRoamerFlagOffsetStartOffset");
        byte[] overlay = readOverlay(romEntry.getInt("RoamerOvlNumber"));

        // This function returns 0 for Thundurus, 1 for Tornadus, and 2 for any other species.
        // In testing, this 2 case is never used, so we can use the space for it to pc-relative
        // load Thundurus's ID. The original code compares to Tornadus and Thundurus then does
        // "bne #0xA" to the default case. Change it to "bne #0x4", which will just make this
        // case immediately return.
        overlay[offset + 10] = 0x00;

        // Now in the space that used to do "mov r0, #0x2" and return, write Thundurus's ID
        FileFunctions.writeFullInt(overlay, offset + 20, Species.thundurus);

        // Lastly, instead of computing Thundurus's ID as TornadusID + 1, pc-relative load it
        // from what we wrote earlier.
        overlay[offset + 6] = 0x03;
        overlay[offset + 7] = 0x49;
        writeOverlay(romEntry.getInt("RoamerOvlNumber"), overlay);
    }

    @Override
    public int miscTweaksAvailable() {
        int available = 0;
        if (romEntry.tweakFiles.get("FastestTextTweak") != null) {
            available |= MiscTweak.FASTEST_TEXT.getValue();
        }
        available |= MiscTweak.BAN_LUCKY_EGG.getValue();
        available |= MiscTweak.NO_FREE_LUCKY_EGG.getValue();
        available |= MiscTweak.BAN_BIG_MANIAC_ITEMS.getValue();
        available |= MiscTweak.UPDATE_TYPE_EFFECTIVENESS.getValue();
        if (romEntry.romType == Gen5Constants.Type_BW) {
            available |= MiscTweak.BALANCE_STATIC_LEVELS.getValue();
        }
        if (romEntry.tweakFiles.get("NationalDexAtStartTweak") != null) {
            available |= MiscTweak.NATIONAL_DEX_AT_START.getValue();
        }
        available |= MiscTweak.RUN_WITHOUT_RUNNING_SHOES.getValue();
        if (romEntry.romType == Gen5Constants.Type_BW2) {
            available |= MiscTweak.FORCE_CHALLENGE_MODE.getValue();
        }
        return available;
    }

    @Override
    public void applyMiscTweak(MiscTweak tweak) {
        if (tweak == MiscTweak.FASTEST_TEXT) {
            applyFastestText();
        } else if (tweak == MiscTweak.BAN_LUCKY_EGG) {
            allowedItems.banSingles(Items.luckyEgg);
            nonBadItems.banSingles(Items.luckyEgg);
        } else if (tweak == MiscTweak.NO_FREE_LUCKY_EGG) {
            removeFreeLuckyEgg();
        } else if (tweak == MiscTweak.BAN_BIG_MANIAC_ITEMS) {
            // BalmMushroom, Big Nugget, Pearl String, Comet Shard
            allowedItems.banRange(Items.balmMushroom, 4);
            nonBadItems.banRange(Items.balmMushroom, 4);

            // Relics
            allowedItems.banRange(Items.relicVase, 4);
            nonBadItems.banRange(Items.relicVase, 4);

            // Rare berries
            allowedItems.banRange(Items.lansatBerry, 7);
            nonBadItems.banRange(Items.lansatBerry, 7);
        } else if (tweak == MiscTweak.BALANCE_STATIC_LEVELS) {
            byte[] fossilFile = scriptNarc.files.get(Gen5Constants.fossilPokemonFile);
            writeWord(fossilFile,Gen5Constants.fossilPokemonLevelOffset,20);
        } else if (tweak == MiscTweak.NATIONAL_DEX_AT_START) {
            patchForNationalDex();
        } else if (tweak == MiscTweak.RUN_WITHOUT_RUNNING_SHOES) {
            applyRunWithoutRunningShoesPatch();
        } else if (tweak == MiscTweak.UPDATE_TYPE_EFFECTIVENESS) {
            updateTypeEffectiveness();
        } else if (tweak == MiscTweak.FORCE_CHALLENGE_MODE) {
            forceChallengeMode();
        }
    }

    @Override
    public boolean isEffectivenessUpdated() {
        return effectivenessUpdated;
    }

    // Removes the free lucky egg you receive from Professor Juniper and replaces it with a gooey mulch.
    private void removeFreeLuckyEgg() {
        int scriptFileGifts = romEntry.getInt("LuckyEggScriptOffset");
        int setVarGift = Gen5Constants.hiddenItemSetVarCommand;
        int mulchIndex = this.random.nextInt(4);

        byte[] itemScripts = scriptNarc.files.get(scriptFileGifts);
        int offset = 0;
        int lookingForEggs = romEntry.romType == Gen5Constants.Type_BW ? 1 : 2;
        while (lookingForEggs > 0) {
            int part1 = readWord(itemScripts, offset);
            if (part1 == Gen5Constants.scriptListTerminator) {
                // done
                break;
            }
            int offsetInFile = readRelativePointer(itemScripts, offset);
            offset += 4;
            if (offsetInFile > itemScripts.length) {
                break;
            }
            while (true) {
                offsetInFile++;
                // Gift items are not necessarily word aligned, so need to read one byte at a time
                int b = readByte(itemScripts, offsetInFile);
                if (b == setVarGift) {
                    int command = readWord(itemScripts, offsetInFile);
                    int variable = readWord(itemScripts,offsetInFile + 2);
                    int item = readWord(itemScripts, offsetInFile + 4);
                    if (command == setVarGift && variable == Gen5Constants.hiddenItemVarSet && item == Items.luckyEgg) {

                        writeWord(itemScripts, offsetInFile + 4, Gen5Constants.mulchIndices[mulchIndex]);
                        lookingForEggs--;
                    }
                }
                if (b == 0x2E) { // Beginning of a new block in the file
                    break;
                }
            }
        }
    }

    private void applyFastestText() {
        genericIPSPatch(arm9, "FastestTextTweak");
    }

    private void patchForNationalDex() {
        byte[] pokedexScript = scriptNarc.files.get(romEntry.getInt("NationalDexScriptOffset"));

        // Our patcher breaks if the output file is larger than the input file. In our case, we want
        // to expand the script by four bytes to add an instruction to enable the national dex. Thus,
        // the IPS patch was created with us adding four 0x00 bytes to the end of the script in mind.
        byte[] expandedPokedexScript = new byte[pokedexScript.length + 4];
        System.arraycopy(pokedexScript, 0, expandedPokedexScript, 0, pokedexScript.length);
        genericIPSPatch(expandedPokedexScript, "NationalDexAtStartTweak");
        scriptNarc.files.set(romEntry.getInt("NationalDexScriptOffset"), expandedPokedexScript);
    }

    private void applyRunWithoutRunningShoesPatch() {
        try {
            // In the overlay that handles field movement, there's a very simple function
            // that checks if the player has the Running Shoes by checking if flag 2403 is
            // set on the save file. If it isn't, the code branches to a separate code path
            // where the function returns 0. The below code simply nops this branch so that
            // this function always returns 1, regardless of the status of flag 2403.
            int fieldOverlayNumber = Gen5Constants.getFieldOverlayNumber(romEntry.romType);
            byte[] fieldOverlay = readOverlay(fieldOverlayNumber);
            String prefix = Gen5Constants.runningShoesPrefix;
            int offset = find(fieldOverlay, prefix);
            if (offset != 0) {
                writeWord(fieldOverlay, offset, 0);
                writeOverlay(fieldOverlayNumber, fieldOverlay);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void updateTypeEffectiveness() {
        try {
            byte[] battleOverlay = readOverlay(romEntry.getInt("BattleOvlNumber"));
            int typeEffectivenessTableOffset = find(battleOverlay, Gen5Constants.typeEffectivenessTableLocator);
            if (typeEffectivenessTableOffset > 0) {
                Effectiveness[][] typeEffectivenessTable = readTypeEffectivenessTable(battleOverlay, typeEffectivenessTableOffset);
                log("--Updating Type Effectiveness--");
                int steel = Gen5Constants.typeToByte(Type.STEEL);
                int dark = Gen5Constants.typeToByte(Type.DARK);
                int ghost = Gen5Constants.typeToByte(Type.GHOST);
                typeEffectivenessTable[ghost][steel] = Effectiveness.NEUTRAL;
                log("Replaced: Ghost not very effective vs Steel => Ghost neutral vs Steel");
                typeEffectivenessTable[dark][steel] = Effectiveness.NEUTRAL;
                log("Replaced: Dark not very effective vs Steel => Dark neutral vs Steel");
                logBlankLine();
                writeTypeEffectivenessTable(typeEffectivenessTable, battleOverlay, typeEffectivenessTableOffset);
                writeOverlay(romEntry.getInt("BattleOvlNumber"), battleOverlay);
                effectivenessUpdated = true;
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private Effectiveness[][] readTypeEffectivenessTable(byte[] battleOverlay, int typeEffectivenessTableOffset) {
        Effectiveness[][] effectivenessTable = new Effectiveness[Type.DARK.ordinal() + 1][Type.DARK.ordinal() + 1];
        for (int attacker = Type.NORMAL.ordinal(); attacker <= Type.DARK.ordinal(); attacker++) {
            for (int defender = Type.NORMAL.ordinal(); defender <= Type.DARK.ordinal(); defender++) {
                int offset = typeEffectivenessTableOffset + (attacker * (Type.DARK.ordinal() + 1)) + defender;
                int effectivenessInternal = battleOverlay[offset];
                Effectiveness effectiveness = null;
                switch (effectivenessInternal) {
                    case 8:
                        effectiveness = Effectiveness.DOUBLE;
                        break;
                    case 4:
                        effectiveness = Effectiveness.NEUTRAL;
                        break;
                    case 2:
                        effectiveness = Effectiveness.HALF;
                        break;
                    case 0:
                        effectiveness = Effectiveness.ZERO;
                        break;
                }
                effectivenessTable[attacker][defender] = effectiveness;
            }
        }
        return effectivenessTable;
    }

    private void writeTypeEffectivenessTable(Effectiveness[][] typeEffectivenessTable, byte[] battleOverlay,
                                             int typeEffectivenessTableOffset) {
        for (int attacker = Type.NORMAL.ordinal(); attacker <= Type.DARK.ordinal(); attacker++) {
            for (int defender = Type.NORMAL.ordinal(); defender <= Type.DARK.ordinal(); defender++) {
                Effectiveness effectiveness = typeEffectivenessTable[attacker][defender];
                int offset = typeEffectivenessTableOffset + (attacker * (Type.DARK.ordinal() + 1)) + defender;
                byte effectivenessInternal = 0;
                switch (effectiveness) {
                    case DOUBLE:
                        effectivenessInternal = 8;
                        break;
                    case NEUTRAL:
                        effectivenessInternal = 4;
                        break;
                    case HALF:
                        effectivenessInternal = 2;
                        break;
                    case ZERO:
                        effectivenessInternal = 0;
                        break;
                }
                battleOverlay[offset] = effectivenessInternal;
            }
        }
    }

    private void forceChallengeMode() {
        int offset = find(arm9, Gen5Constants.forceChallengeModeLocator);
        if (offset > 0) {
            // offset is now pointing at the start of sub_2010528, which is the function that
            // determines which difficulty the player currently has enabled. It returns 0 for
            // Easy Mode, 1 for Normal Mode, and 2 for Challenge Mode. Since we're just trying
            // to force Challenge Mode, all we need to do is:
            // mov r0, #0x2
            // bx lr
            arm9[offset] = 0x02;
            arm9[offset + 1] = 0x20;
            arm9[offset + 2] = 0x70;
            arm9[offset + 3] = 0x47;
        }
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

    @Override
    public List<Integer> getTMMoves() {
        String tmDataPrefix = Gen5Constants.tmDataPrefix;
        int offset = find(arm9, tmDataPrefix);
        if (offset > 0) {
            offset += Gen5Constants.tmDataPrefix.length() / 2; // because it was
                                                               // a prefix
            List<Integer> tms = new ArrayList<>();
            for (int i = 0; i < Gen5Constants.tmBlockOneCount; i++) {
                tms.add(readWord(arm9, offset + i * 2));
            }
            // Skip past first 92 TMs and 6 HMs
            offset += (Gen5Constants.tmBlockOneCount + Gen5Constants.hmCount) * 2;
            for (int i = 0; i < (Gen5Constants.tmCount - Gen5Constants.tmBlockOneCount); i++) {
                tms.add(readWord(arm9, offset + i * 2));
            }
            return tms;
        } else {
            return null;
        }
    }

    @Override
    public List<Integer> getHMMoves() {
        String tmDataPrefix = Gen5Constants.tmDataPrefix;
        int offset = find(arm9, tmDataPrefix);
        if (offset > 0) {
            offset += Gen5Constants.tmDataPrefix.length() / 2; // because it was
                                                               // a prefix
            offset += Gen5Constants.tmBlockOneCount * 2; // TM data
            List<Integer> hms = new ArrayList<>();
            for (int i = 0; i < Gen5Constants.hmCount; i++) {
                hms.add(readWord(arm9, offset + i * 2));
            }
            return hms;
        } else {
            return null;
        }
    }

    @Override
    public void setTMMoves(List<Integer> moveIndexes) {
        String tmDataPrefix = Gen5Constants.tmDataPrefix;
        int offset = find(arm9, tmDataPrefix);
        if (offset > 0) {
            offset += Gen5Constants.tmDataPrefix.length() / 2; // because it was
                                                               // a prefix
            for (int i = 0; i < Gen5Constants.tmBlockOneCount; i++) {
                writeWord(arm9, offset + i * 2, moveIndexes.get(i));
            }
            // Skip past those 92 TMs and 6 HMs
            offset += (Gen5Constants.tmBlockOneCount + Gen5Constants.hmCount) * 2;
            for (int i = 0; i < (Gen5Constants.tmCount - Gen5Constants.tmBlockOneCount); i++) {
                writeWord(arm9, offset + i * 2, moveIndexes.get(i + Gen5Constants.tmBlockOneCount));
            }

            // Update TM item descriptions
            List<String> itemDescriptions = getStrings(false, romEntry.getInt("ItemDescriptionsTextOffset"));
            List<String> moveDescriptions = getStrings(false, romEntry.getInt("MoveDescriptionsTextOffset"));
            // TM01 is item 328 and so on
            for (int i = 0; i < Gen5Constants.tmBlockOneCount; i++) {
                itemDescriptions.set(i + Gen5Constants.tmBlockOneOffset, moveDescriptions.get(moveIndexes.get(i)));
            }
            // TM93-95 are 618-620
            for (int i = 0; i < (Gen5Constants.tmCount - Gen5Constants.tmBlockOneCount); i++) {
                itemDescriptions.set(i + Gen5Constants.tmBlockTwoOffset,
                        moveDescriptions.get(moveIndexes.get(i + Gen5Constants.tmBlockOneCount)));
            }
            // Save the new item descriptions
            setStrings(false, romEntry.getInt("ItemDescriptionsTextOffset"), itemDescriptions);
            // Palettes
            String baseOfPalettes;
            if (romEntry.romType == Gen5Constants.Type_BW) {
                baseOfPalettes = Gen5Constants.bw1ItemPalettesPrefix;
            } else {
                baseOfPalettes = Gen5Constants.bw2ItemPalettesPrefix;
            }
            int offsPals = find(arm9, baseOfPalettes);
            if (offsPals > 0) {
                // Write pals
                for (int i = 0; i < Gen5Constants.tmBlockOneCount; i++) {
                    int itmNum = Gen5Constants.tmBlockOneOffset + i;
                    Move m = this.moves[moveIndexes.get(i)];
                    int pal = this.typeTMPaletteNumber(m.type);
                    writeWord(arm9, offsPals + itmNum * 4 + 2, pal);
                }
                for (int i = 0; i < (Gen5Constants.tmCount - Gen5Constants.tmBlockOneCount); i++) {
                    int itmNum = Gen5Constants.tmBlockTwoOffset + i;
                    Move m = this.moves[moveIndexes.get(i + Gen5Constants.tmBlockOneCount)];
                    int pal = this.typeTMPaletteNumber(m.type);
                    writeWord(arm9, offsPals + itmNum * 4 + 2, pal);
                }
            }
        }
    }

    private static RomFunctions.StringSizeDeterminer ssd = encodedText -> {
        int offs = 0;
        int len = encodedText.length();
        while (encodedText.indexOf("\\x", offs) != -1) {
            len -= 5;
            offs = encodedText.indexOf("\\x", offs) + 1;
        }
        return len;
    };

    @Override
    public int getTMCount() {
        return Gen5Constants.tmCount;
    }

    @Override
    public int getHMCount() {
        return Gen5Constants.hmCount;
    }

    @Override
    public Map<Pokemon, boolean[]> getTMHMCompatibility() {
        Map<Pokemon, boolean[]> compat = new TreeMap<>();
        int formeCount = Gen5Constants.getFormeCount(romEntry.romType);
        int formeOffset = Gen5Constants.getFormeOffset(romEntry.romType);
        for (int i = 1; i <= Gen5Constants.pokemonCount + formeCount; i++) {
            byte[] data;
            if (i > Gen5Constants.pokemonCount) {
                data = pokeNarc.files.get(i + formeOffset);
            } else {
                data = pokeNarc.files.get(i);
            }
            Pokemon pkmn = pokes[i];
            boolean[] flags = new boolean[Gen5Constants.tmCount + Gen5Constants.hmCount + 1];
            for (int j = 0; j < 13; j++) {
                readByteIntoFlags(data, flags, j * 8 + 1, Gen5Constants.bsTMHMCompatOffset + j);
            }
            compat.put(pkmn, flags);
        }
        return compat;
    }

    @Override
    public void setTMHMCompatibility(Map<Pokemon, boolean[]> compatData) {
        int formeOffset = Gen5Constants.getFormeOffset(romEntry.romType);
        for (Map.Entry<Pokemon, boolean[]> compatEntry : compatData.entrySet()) {
            Pokemon pkmn = compatEntry.getKey();
            boolean[] flags = compatEntry.getValue();
            int number = pkmn.number;
            if (number > Gen5Constants.pokemonCount) {
                number += formeOffset;
            }
            byte[] data = pokeNarc.files.get(number);
            for (int j = 0; j < 13; j++) {
                data[Gen5Constants.bsTMHMCompatOffset + j] = getByteFromFlags(flags, j * 8 + 1);
            }
        }
    }

    @Override
    public boolean hasMoveTutors() {
        return romEntry.romType == Gen5Constants.Type_BW2;
    }

    @Override
    public List<Integer> getMoveTutorMoves() {
        if (!hasMoveTutors()) {
            return new ArrayList<>();
        }
        int baseOffset = romEntry.getInt("MoveTutorDataOffset");
        int amount = Gen5Constants.bw2MoveTutorCount;
        int bytesPer = Gen5Constants.bw2MoveTutorBytesPerEntry;
        List<Integer> mtMoves = new ArrayList<>();
        try {
            byte[] mtFile = readOverlay(romEntry.getInt("MoveTutorOvlNumber"));
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
        int baseOffset = romEntry.getInt("MoveTutorDataOffset");
        int amount = Gen5Constants.bw2MoveTutorCount;
        int bytesPer = Gen5Constants.bw2MoveTutorBytesPerEntry;
        if (moves.size() != amount) {
            return;
        }
        try {
            byte[] mtFile = readOverlay(romEntry.getInt("MoveTutorOvlNumber"));
            for (int i = 0; i < amount; i++) {
                writeWord(mtFile, baseOffset + i * bytesPer, moves.get(i));
            }
            writeOverlay(romEntry.getInt("MoveTutorOvlNumber"), mtFile);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
        if (!hasMoveTutors()) {
            return new TreeMap<>();
        }
        Map<Pokemon, boolean[]> compat = new TreeMap<>();
        int[] countsPersonalOrder = new int[] { 15, 17, 13, 15 };
        int[] countsMoveOrder = new int[] { 13, 15, 15, 17 };
        int[] personalToMoveOrder = new int[] { 1, 3, 0, 2 };
        int formeCount = Gen5Constants.getFormeCount(romEntry.romType);
        int formeOffset = Gen5Constants.getFormeOffset(romEntry.romType);
        for (int i = 1; i <= Gen5Constants.pokemonCount + formeCount; i++) {
            byte[] data;
            if (i > Gen5Constants.pokemonCount) {
                data = pokeNarc.files.get(i + formeOffset);
            } else {
                data = pokeNarc.files.get(i);
            }
            Pokemon pkmn = pokes[i];
            boolean[] flags = new boolean[Gen5Constants.bw2MoveTutorCount + 1];
            for (int mt = 0; mt < 4; mt++) {
                boolean[] mtflags = new boolean[countsPersonalOrder[mt] + 1];
                for (int j = 0; j < 4; j++) {
                    readByteIntoFlags(data, mtflags, j * 8 + 1, Gen5Constants.bsMTCompatOffset + mt * 4 + j);
                }
                int offsetOfThisData = 0;
                for (int cmoIndex = 0; cmoIndex < personalToMoveOrder[mt]; cmoIndex++) {
                    offsetOfThisData += countsMoveOrder[cmoIndex];
                }
                System.arraycopy(mtflags, 1, flags, offsetOfThisData + 1, countsPersonalOrder[mt]);
            }
            compat.put(pkmn, flags);
        }
        return compat;
    }

    @Override
    public void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData) {
        if (!hasMoveTutors()) {
            return;
        }
        int formeOffset = Gen5Constants.getFormeOffset(romEntry.romType);
        // BW2 move tutor flags aren't using the same order as the move tutor
        // move data.
        // We unscramble them from move data order to personal.narc flag order.
        int[] countsPersonalOrder = new int[] { 15, 17, 13, 15 };
        int[] countsMoveOrder = new int[] { 13, 15, 15, 17 };
        int[] personalToMoveOrder = new int[] { 1, 3, 0, 2 };
        for (Map.Entry<Pokemon, boolean[]> compatEntry : compatData.entrySet()) {
            Pokemon pkmn = compatEntry.getKey();
            boolean[] flags = compatEntry.getValue();
            int number = pkmn.number;
            if (number > Gen5Constants.pokemonCount) {
                number += formeOffset;
            }
            byte[] data = pokeNarc.files.get(number);
            for (int mt = 0; mt < 4; mt++) {
                int offsetOfThisData = 0;
                for (int cmoIndex = 0; cmoIndex < personalToMoveOrder[mt]; cmoIndex++) {
                    offsetOfThisData += countsMoveOrder[cmoIndex];
                }
                boolean[] mtflags = new boolean[countsPersonalOrder[mt] + 1];
                System.arraycopy(flags, offsetOfThisData + 1, mtflags, 1, countsPersonalOrder[mt]);
                for (int j = 0; j < 4; j++) {
                    data[Gen5Constants.bsMTCompatOffset + mt * 4 + j] = getByteFromFlags(mtflags, j * 8 + 1);
                }
            }
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

    private List<String> getStrings(boolean isStoryText, int index) {
        NARCArchive baseNARC = isStoryText ? storyTextNarc : stringsNarc;
        byte[] rawFile = baseNARC.files.get(index);
        return new ArrayList<>(PPTxtHandler.readTexts(rawFile));
    }

    private void setStrings(boolean isStoryText, int index, List<String> strings) {
        NARCArchive baseNARC = isStoryText ? storyTextNarc : stringsNarc;
        byte[] oldRawFile = baseNARC.files.get(index);
        byte[] newRawFile = PPTxtHandler.saveEntry(oldRawFile, strings);
        baseNARC.files.set(index, newRawFile);
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
        return true; // All BW/BW2 do [seasons]
    }

    @Override
    public boolean hasWildAltFormes() {
        return true;
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
            for (int i = 1; i <= Gen5Constants.pokemonCount; i++) {
                Pokemon pk = pokes[i];
                byte[] evoEntry = evoNARC.files.get(i);
                for (int evo = 0; evo < 7; evo++) {
                    int method = readWord(evoEntry, evo * 6);
                    int species = readWord(evoEntry, evo * 6 + 4);
                    if (method >= 1 && method <= Gen5Constants.evolutionMethodCount && species >= 1) {
                        EvolutionType et = EvolutionType.fromIndex(5, method);
                        if (et.equals(EvolutionType.LEVEL_HIGH_BEAUTY)) continue; // Remove Feebas "split" evolution
                        int extraInfo = readWord(evoEntry, evo * 6 + 2);
                        Evolution evol = new Evolution(pk, pokes[species], true, et, extraInfo);
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
            for (int i = 1; i <= Gen5Constants.pokemonCount; i++) {
                byte[] evoEntry = evoNARC.files.get(i);
                Pokemon pk = pokes[i];
                if (pk.number == Species.nincada) {
                    writeShedinjaEvolution();
                }
                int evosWritten = 0;
                for (Evolution evo : pk.evolutionsFrom) {
                    writeWord(evoEntry, evosWritten * 6, evo.type.toIndex(5));
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

    private void writeShedinjaEvolution() throws IOException {
        Pokemon nincada = pokes[Species.nincada];

        // When the "Limit Pokemon" setting is enabled and Gen 3 is disabled, or when
        // "Random Every Level" evolutions are selected, we end up clearing out Nincada's
        // vanilla evolutions. In that case, there's no point in even worrying about
        // Shedinja, so just return.
        if (nincada.evolutionsFrom.size() < 2) {
            return;
        }
        Pokemon extraEvolution = nincada.evolutionsFrom.get(1).to;

        // In all the Gen 5 games, the evolution overlay is hardcoded to generate
        // a Shedinja by loading its species ID using the following instructions:
        // mov r1, #0x49
        // lsl r1, r1, #2
        // Since Gen 5 has more than 510 species, we cannot use 8-bit addition to
        // load any Pokemon; instead, we nop out a useless load of a string, then
        // use the space that used to store the address of that string to instead
        // store Nincada's new extra evolution's species ID.
        byte[] evolutionOverlay = readOverlay(romEntry.getInt("EvolutionOvlNumber"));
        int functionOffset = find(evolutionOverlay, Gen5Constants.shedinjaFunctionLocator);
        if (functionOffset > 0) {
            int[] patchOffsets = romEntry.arrayEntries.get("ShedinjaCodePatchOffsets");

            // First, nop the instruction that loads a pointer to the string
            // "shinka_demo.c" into a register; this has seemingly no effect on
            // the game and was probably used strictly for debugging.
            evolutionOverlay[functionOffset + patchOffsets[0]] = 0x00;
            evolutionOverlay[functionOffset + patchOffsets[0] + 1] = 0x00;

            // In the space that used to hold the address of the "shinka_demo.c" string,
            // we're going to instead store a species ID. We need to write a pc-relative
            // load to that space. However, the original Shedinja instructions are
            // misaligned to do a load; there's an "add r0, r4, #0x0" between the move
            // and the shift that is correctly-aligned. So we first move this add up one
            // instruction, then we write out the load ("ldr r1, [pc #pcRelativeOffset]")
            // in the correctly-aligned space, then we nop out the shift.
            int pcRelativeOffset = patchOffsets[2] - patchOffsets[1] - 6;
            evolutionOverlay[functionOffset + patchOffsets[1]] = 0x20;
            evolutionOverlay[functionOffset + patchOffsets[1] + 1] = 0x1c;
            evolutionOverlay[functionOffset + patchOffsets[1] + 2] = (byte) (pcRelativeOffset / 4);
            evolutionOverlay[functionOffset + patchOffsets[1] + 3] = 0x49;
            evolutionOverlay[functionOffset + patchOffsets[1] + 4] = 0x00;
            evolutionOverlay[functionOffset + patchOffsets[1] + 5] = 0x00;

            // Finally, we replace what used to store the address of "shinka_demo.c"
            // with the species ID of Nincada's new extra evolution.
            int newSpeciesIDOffset = functionOffset + patchOffsets[2];
            FileFunctions.writeFullInt(evolutionOverlay, newSpeciesIDOffset, extraEvolution.number);

            writeOverlay(romEntry.getInt("EvolutionOvlNumber"), evolutionOverlay);
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
                    if (evo.type == EvolutionType.TRADE_SPECIAL) {
                        // This is the karrablast <-> shelmet trade
                        // Replace it with Level up w/ Other Species in Party
                        // (22)
                        // Based on what species we're currently dealing with
                        evo.type = EvolutionType.LEVEL_WITH_OTHER;
                        evo.extraInfo = (evo.from.number == Species.karrablast ? Species.shelmet : Species.karrablast);
                        addEvoUpdateParty(impossibleEvolutionUpdates, evo, pokes[evo.extraInfo].fullName());
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
    public boolean canChangeTrainerText() {
        return true;
    }

    @Override
    public List<String> getTrainerNames() {
        List<String> tnames = getStrings(false, romEntry.getInt("TrainerNamesTextOffset"));
        tnames.remove(0); // blank one
        if (romEntry.romType == Gen5Constants.Type_BW2) {
            List<String> pwtNames = getStrings(false, romEntry.getInt("PWTTrainerNamesTextOffset"));
            tnames.addAll(pwtNames);
        }
        // Tack the mugshot names on the end
        List<String> mnames = getStrings(false, romEntry.getInt("TrainerMugshotsTextOffset"));
        for (String mname : mnames) {
            if (!mname.isEmpty() && (mname.charAt(0) >= 'A' && mname.charAt(0) <= 'Z')) {
                tnames.add(mname);
            }
        }
        return tnames;
    }

    @Override
    public int maxTrainerNameLength() {
        return 10;// based off the english ROMs
    }

    @Override
    public void setTrainerNames(List<String> trainerNames) {
        List<String> tnames = getStrings(false, romEntry.getInt("TrainerNamesTextOffset"));
        // Grab the mugshot names off the back of the list of trainer names
        // we got back
        List<String> mnames = getStrings(false, romEntry.getInt("TrainerMugshotsTextOffset"));
        int trNamesSize = trainerNames.size();
        for (int i = mnames.size() - 1; i >= 0; i--) {
            String origMName = mnames.get(i);
            if (!origMName.isEmpty() && (origMName.charAt(0) >= 'A' && origMName.charAt(0) <= 'Z')) {
                // Grab replacement
                String replacement = trainerNames.remove(--trNamesSize);
                mnames.set(i, replacement);
            }
        }
        // Save back mugshot names
        setStrings(false, romEntry.getInt("TrainerMugshotsTextOffset"), mnames);

        // Now save the rest of trainer names
        if (romEntry.romType == Gen5Constants.Type_BW2) {
            List<String> pwtNames = getStrings(false, romEntry.getInt("PWTTrainerNamesTextOffset"));
            List<String> newTNames = new ArrayList<>();
            List<String> newPWTNames = new ArrayList<>();
            newTNames.add(0, tnames.get(0)); // the 0-entry, preserve it
            for (int i = 1; i < tnames.size() + pwtNames.size(); i++) {
                if (i < tnames.size()) {
                    newTNames.add(trainerNames.get(i - 1));
                } else {
                    newPWTNames.add(trainerNames.get(i - 1));
                }
            }
            setStrings(false, romEntry.getInt("TrainerNamesTextOffset"), newTNames);
            setStrings(false, romEntry.getInt("PWTTrainerNamesTextOffset"), newPWTNames);
        } else {
            List<String> newTNames = new ArrayList<>(trainerNames);
            newTNames.add(0, tnames.get(0)); // the 0-entry, preserve it
            setStrings(false, romEntry.getInt("TrainerNamesTextOffset"), newTNames);
        }
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
        List<String> classNames = getStrings(false, romEntry.getInt("TrainerClassesTextOffset"));
        if (romEntry.romType == Gen5Constants.Type_BW2) {
            classNames.addAll(getStrings(false, romEntry.getInt("PWTTrainerClassesTextOffset")));
        }
        return classNames;
    }

    @Override
    public void setTrainerClassNames(List<String> trainerClassNames) {
        if (romEntry.romType == Gen5Constants.Type_BW2) {
            List<String> newTClasses = new ArrayList<>();
            List<String> newPWTClasses = new ArrayList<>();
            List<String> classNames = getStrings(false, romEntry.getInt("TrainerClassesTextOffset"));
            List<String> pwtClassNames = getStrings(false, romEntry.getInt("PWTTrainerClassesTextOffset"));
            for (int i = 0; i < classNames.size() + pwtClassNames.size(); i++) {
                if (i < classNames.size()) {
                    newTClasses.add(trainerClassNames.get(i));
                } else {
                    newPWTClasses.add(trainerClassNames.get(i));
                }
            }
            setStrings(false, romEntry.getInt("TrainerClassesTextOffset"), newTClasses);
            setStrings(false, romEntry.getInt("PWTTrainerClassesTextOffset"), newPWTClasses);
        } else {
            setStrings(false, romEntry.getInt("TrainerClassesTextOffset"), trainerClassNames);
        }
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
        return 3;
    }

    @Override
    public int highestAbilityIndex() {
        return Gen5Constants.highestAbilityIndex;
    }

    @Override
    public int internalStringLength(String string) {
        return ssd.lengthFor(string);
    }

    @Override
    public void randomizeIntroPokemon() {
        try {
            int introPokemon = randomPokemon().number;
            byte[] introGraphicOverlay = readOverlay(romEntry.getInt("IntroGraphicOvlNumber"));
            int offset = find(introGraphicOverlay, Gen5Constants.introGraphicPrefix);
            if (offset > 0) {
                offset += Gen5Constants.introGraphicPrefix.length() / 2; // because it was a prefix
                // offset is now pointing at the species constant that gets pc-relative
                // loaded to determine what sprite to load.
                writeWord(introGraphicOverlay, offset, introPokemon);
                writeOverlay(romEntry.getInt("IntroGraphicOvlNumber"), introGraphicOverlay);
            }

            if (romEntry.romType == Gen5Constants.Type_BW) {
                byte[] introCryOverlay = readOverlay(romEntry.getInt("IntroCryOvlNumber"));
                offset = find(introCryOverlay, Gen5Constants.bw1IntroCryPrefix);
                if (offset > 0) {
                    offset += Gen5Constants.bw1IntroCryPrefix.length() / 2; // because it was a prefix
                    // The function starting from the offset looks like this:
                    // mov r0, #0x8f
                    // str r1, [sp, #local_94]
                    // lsl r0, r0, #0x2
                    // mov r2, #0x40
                    // mov r3, #0x0
                    // bl PlayCry
                    // [rest of the function...]
                    // pop { r3, r4, r5, r6, r7, pc }
                    // C0 46 (these are useless padding bytes)
                    // To make this more extensible, we want to pc-relative load a species ID into r0 instead.
                    // Start by moving everything below the left shift up by 2 bytes. We won't need the left
                    // shift later, and it will give us 4 bytes after the pop to use for the ID.
                    for (int i = offset + 6; i < offset + 40; i++) {
                        introCryOverlay[i - 2] = introCryOverlay[i];
                    }

                    // The call to PlayCry needs to be adjusted as well, since it got moved.
                    introCryOverlay[offset + 10]++;

                    // Now write the species ID in the 4 bytes of space now available at the bottom,
                    // and then write a pc-relative load to this species ID at the offset.
                    FileFunctions.writeFullInt(introCryOverlay, offset + 38, introPokemon);
                    introCryOverlay[offset] = 0x9;
                    introCryOverlay[offset + 1] = 0x48;
                    writeOverlay(romEntry.getInt("IntroCryOvlNumber"), introCryOverlay);
                }
            } else {
                byte[] introCryOverlay = readOverlay(romEntry.getInt("IntroCryOvlNumber"));
                offset = find(introCryOverlay, Gen5Constants.bw2IntroCryLocator);
                if (offset > 0) {
                    // offset is now pointing at the species constant that gets pc-relative
                    // loaded to determine what cry to play.
                    writeWord(introCryOverlay, offset, introPokemon);
                    writeOverlay(romEntry.getInt("IntroCryOvlNumber"), introCryOverlay);
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
        return regularShopItems;
    }

    @Override
    public List<Integer> getOPShopItems() {
        return opShopItems;
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
        return Gen5Constants.abilityVariations;
    }

    @Override
    public List<Integer> getUselessAbilities() {
        return new ArrayList<>(Gen5Constants.uselessAbilities);
    }

    @Override
    public int getAbilityForTrainerPokemon(TrainerPokemon tp) {
        // Before randomizing Trainer Pokemon, one possible value for abilitySlot is 0,
        // which represents "Either Ability 1 or 2". During randomization, we make sure to
        // to set abilitySlot to some non-zero value, but if you call this method without
        // randomization, then you'll hit this case.
        if (tp.abilitySlot < 1 || tp.abilitySlot > 3) {
            return 0;
        }

        // In Gen 5, alt formes for Trainer Pokemon use the base forme's ability
        Pokemon pkmn = tp.pokemon;
        while (pkmn.baseForme != null) {
            pkmn = pkmn.baseForme;
        }

        List<Integer> abilityList = Arrays.asList(pkmn.ability1, pkmn.ability2, pkmn.ability3);
        return abilityList.get(tp.abilitySlot - 1);
    }

    @Override
    public boolean hasMegaEvolutions() {
        return false;
    }

    private List<Integer> getFieldItems() {
        List<Integer> fieldItems = new ArrayList<>();
        // normal items
        int scriptFileNormal = romEntry.getInt("ItemBallsScriptOffset");
        int scriptFileHidden = romEntry.getInt("HiddenItemsScriptOffset");
        int[] skipTable = romEntry.arrayEntries.get("ItemBallsSkip");
        int[] skipTableH = romEntry.arrayEntries.get("HiddenItemsSkip");
        int setVarNormal = Gen5Constants.normalItemSetVarCommand;
        int setVarHidden = Gen5Constants.hiddenItemSetVarCommand;

        byte[] itemScripts = scriptNarc.files.get(scriptFileNormal);
        int offset = 0;
        int skipTableOffset = 0;
        while (true) {
            int part1 = readWord(itemScripts, offset);
            if (part1 == Gen5Constants.scriptListTerminator) {
                // done
                break;
            }
            int offsetInFile = readRelativePointer(itemScripts, offset);
            offset += 4;
            if (offsetInFile > itemScripts.length) {
                break;
            }
            if (skipTableOffset < skipTable.length && (skipTable[skipTableOffset] == (offset / 4) - 1)) {
                skipTableOffset++;
                continue;
            }
            int command = readWord(itemScripts, offsetInFile + 2);
            int variable = readWord(itemScripts, offsetInFile + 4);
            if (command == setVarNormal && variable == Gen5Constants.normalItemVarSet) {
                int item = readWord(itemScripts, offsetInFile + 6);
                fieldItems.add(item);
            }

        }

        // hidden items
        byte[] hitemScripts = scriptNarc.files.get(scriptFileHidden);
        offset = 0;
        skipTableOffset = 0;
        while (true) {
            int part1 = readWord(hitemScripts, offset);
            if (part1 == Gen5Constants.scriptListTerminator) {
                // done
                break;
            }
            int offsetInFile = readRelativePointer(hitemScripts, offset);
            if (offsetInFile > hitemScripts.length) {
                break;
            }
            offset += 4;
            if (skipTableOffset < skipTable.length && (skipTableH[skipTableOffset] == (offset / 4) - 1)) {
                skipTableOffset++;
                continue;
            }
            int command = readWord(hitemScripts, offsetInFile + 2);
            int variable = readWord(hitemScripts, offsetInFile + 4);
            if (command == setVarHidden && variable == Gen5Constants.hiddenItemVarSet) {
                int item = readWord(hitemScripts, offsetInFile + 6);
                fieldItems.add(item);
            }

        }

        return fieldItems;
    }

    private void setFieldItems(List<Integer> fieldItems) {
        Iterator<Integer> iterItems = fieldItems.iterator();

        // normal items
        int scriptFileNormal = romEntry.getInt("ItemBallsScriptOffset");
        int scriptFileHidden = romEntry.getInt("HiddenItemsScriptOffset");
        int[] skipTable = romEntry.arrayEntries.get("ItemBallsSkip");
        int[] skipTableH = romEntry.arrayEntries.get("HiddenItemsSkip");
        int setVarNormal = Gen5Constants.normalItemSetVarCommand;
        int setVarHidden = Gen5Constants.hiddenItemSetVarCommand;

        byte[] itemScripts = scriptNarc.files.get(scriptFileNormal);
        int offset = 0;
        int skipTableOffset = 0;
        while (true) {
            int part1 = readWord(itemScripts, offset);
            if (part1 == Gen5Constants.scriptListTerminator) {
                // done
                break;
            }
            int offsetInFile = readRelativePointer(itemScripts, offset);
            offset += 4;
            if (offsetInFile > itemScripts.length) {
                break;
            }
            if (skipTableOffset < skipTable.length && (skipTable[skipTableOffset] == (offset / 4) - 1)) {
                skipTableOffset++;
                continue;
            }
            int command = readWord(itemScripts, offsetInFile + 2);
            int variable = readWord(itemScripts, offsetInFile + 4);
            if (command == setVarNormal && variable == Gen5Constants.normalItemVarSet) {
                int item = iterItems.next();
                writeWord(itemScripts, offsetInFile + 6, item);
            }

        }

        // hidden items
        byte[] hitemScripts = scriptNarc.files.get(scriptFileHidden);
        offset = 0;
        skipTableOffset = 0;
        while (true) {
            int part1 = readWord(hitemScripts, offset);
            if (part1 == Gen5Constants.scriptListTerminator) {
                // done
                break;
            }
            int offsetInFile = readRelativePointer(hitemScripts, offset);
            offset += 4;
            if (offsetInFile > hitemScripts.length) {
                break;
            }
            if (skipTableOffset < skipTable.length && (skipTableH[skipTableOffset] == (offset / 4) - 1)) {
                skipTableOffset++;
                continue;
            }
            int command = readWord(hitemScripts, offsetInFile + 2);
            int variable = readWord(hitemScripts, offsetInFile + 4);
            if (command == setVarHidden && variable == Gen5Constants.hiddenItemVarSet) {
                int item = iterItems.next();
                writeWord(hitemScripts, offsetInFile + 6, item);
            }

        }
    }

    private int tmFromIndex(int index) {
        if (index >= Gen5Constants.tmBlockOneOffset
                && index < Gen5Constants.tmBlockOneOffset + Gen5Constants.tmBlockOneCount) {
            return index - (Gen5Constants.tmBlockOneOffset - 1);
        } else {
            return (index + Gen5Constants.tmBlockOneCount) - (Gen5Constants.tmBlockTwoOffset - 1);
        }
    }

    private int indexFromTM(int tm) {
        if (tm >= 1 && tm <= Gen5Constants.tmBlockOneCount) {
            return tm + (Gen5Constants.tmBlockOneOffset - 1);
        } else {
            return tm + (Gen5Constants.tmBlockTwoOffset - 1 - Gen5Constants.tmBlockOneCount);
        }
    }

    @Override
    public List<Integer> getCurrentFieldTMs() {
        List<Integer> fieldItems = this.getFieldItems();
        List<Integer> fieldTMs = new ArrayList<>();

        for (int item : fieldItems) {
            if (Gen5Constants.allowedItems.isTM(item)) {
                fieldTMs.add(tmFromIndex(item));
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
            if (Gen5Constants.allowedItems.isTM(oldItem)) {
                int newItem = indexFromTM(iterTMs.next());
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
            if (Gen5Constants.allowedItems.isAllowed(item) && !(Gen5Constants.allowedItems.isTM(item))) {
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
            if (!(Gen5Constants.allowedItems.isTM(oldItem)) && Gen5Constants.allowedItems.isAllowed(oldItem)) {
                int newItem = iterNewItems.next();
                fieldItems.set(i, newItem);
            }
        }

        this.setFieldItems(fieldItems);
    }

    @Override
    public List<Integer> getRequiredFieldTMs() {
        if (romEntry.romType == Gen5Constants.Type_BW) {
            return Gen5Constants.bw1RequiredFieldTMs;
        } else {
            return Gen5Constants.bw2RequiredFieldTMs;
        }
    }

    @Override
    public List<IngameTrade> getIngameTrades() {
        List<IngameTrade> trades = new ArrayList<>();
        try {
            NARCArchive tradeNARC = this.readNARC(romEntry.getFile("InGameTrades"));
            List<String> tradeStrings = getStrings(false, romEntry.getInt("IngameTradesTextOffset"));
            int[] unused = romEntry.arrayEntries.get("TradesUnused");
            int unusedOffset = 0;
            int tableSize = tradeNARC.files.size();

            for (int entry = 0; entry < tableSize; entry++) {
                if (unusedOffset < unused.length && unused[unusedOffset] == entry) {
                    unusedOffset++;
                    continue;
                }
                IngameTrade trade = new IngameTrade();
                byte[] tfile = tradeNARC.files.get(entry);
                trade.nickname = tradeStrings.get(entry * 2);
                trade.givenPokemon = pokes[readLong(tfile, 4)];
                trade.ivs = new int[6];
                for (int iv = 0; iv < 6; iv++) {
                    trade.ivs[iv] = readLong(tfile, 0x10 + iv * 4);
                }
                trade.otId = readWord(tfile, 0x34);
                trade.item = readLong(tfile, 0x4C);
                trade.otName = tradeStrings.get(entry * 2 + 1);
                trade.requestedPokemon = pokes[readLong(tfile, 0x5C)];
                trades.add(trade);
            }
        } catch (Exception ex) {
            throw new RandomizerIOException(ex);
        }

        return trades;

    }

    @Override
    public void setIngameTrades(List<IngameTrade> trades) {
        // info
        int tradeOffset = 0;
        List<IngameTrade> oldTrades = this.getIngameTrades();
        try {
            NARCArchive tradeNARC = this.readNARC(romEntry.getFile("InGameTrades"));
            List<String> tradeStrings = getStrings(false, romEntry.getInt("IngameTradesTextOffset"));
            int tradeCount = tradeNARC.files.size();
            int[] unused = romEntry.arrayEntries.get("TradesUnused");
            int unusedOffset = 0;
            for (int i = 0; i < tradeCount; i++) {
                if (unusedOffset < unused.length && unused[unusedOffset] == i) {
                    unusedOffset++;
                    continue;
                }
                byte[] tfile = tradeNARC.files.get(i);
                IngameTrade trade = trades.get(tradeOffset++);
                tradeStrings.set(i * 2, trade.nickname);
                tradeStrings.set(i * 2 + 1, trade.otName);
                writeLong(tfile, 4, trade.givenPokemon.number);
                writeLong(tfile, 8, 0); // disable forme
                for (int iv = 0; iv < 6; iv++) {
                    writeLong(tfile, 0x10 + iv * 4, trade.ivs[iv]);
                }
                writeLong(tfile, 0x2C, 0xFF); // random nature
                writeWord(tfile, 0x34, trade.otId);
                writeLong(tfile, 0x4C, trade.item);
                writeLong(tfile, 0x5C, trade.requestedPokemon.number);
                if (romEntry.tradeScripts.size() > 0) {
                    romEntry.tradeScripts.get(i - unusedOffset).setPokemon(this,scriptNarc,trade.requestedPokemon,trade.givenPokemon);
                }
            }
            this.writeNARC(romEntry.getFile("InGameTrades"), tradeNARC);
            this.setStrings(false, romEntry.getInt("IngameTradesTextOffset"), tradeStrings);
            // update what the people say when they talk to you
            unusedOffset = 0;
            if (romEntry.arrayEntries.containsKey("IngameTradePersonTextOffsets")) {
                int[] textOffsets = romEntry.arrayEntries.get("IngameTradePersonTextOffsets");
                for (int tr = 0; tr < textOffsets.length; tr++) {
                    if (unusedOffset < unused.length && unused[unusedOffset] == tr+24) {
                        unusedOffset++;
                        continue;
                    }
                    if (textOffsets[tr] > 0) {
                        if (tr+24 >= oldTrades.size() || tr+24 >= trades.size()) {
                            break;
                        }
                        IngameTrade oldTrade = oldTrades.get(tr+24);
                        IngameTrade newTrade = trades.get(tr+24);
                        Map<String, String> replacements = new TreeMap<>();
                        replacements.put(oldTrade.givenPokemon.name, newTrade.givenPokemon.name);
                        if (oldTrade.requestedPokemon != newTrade.requestedPokemon) {
                            replacements.put(oldTrade.requestedPokemon.name, newTrade.requestedPokemon.name);
                        }
                        replaceAllStringsInEntry(textOffsets[tr], replacements);
                    }
                }
            }
        } catch (IOException ex) {
            throw new RandomizerIOException(ex);
        }
    }

    private void replaceAllStringsInEntry(int entry, Map<String, String> replacements) {
        List<String> thisTradeStrings = this.getStrings(true, entry);
        int ttsCount = thisTradeStrings.size();
        for (int strNum = 0; strNum < ttsCount; strNum++) {
            String newString = thisTradeStrings.get(strNum);
            for (String old: replacements.keySet()) {
                newString = newString.replaceAll(old,replacements.get(old));
            }
            thisTradeStrings.set(strNum, newString);
        }
        this.setStrings(true, entry, thisTradeStrings);
    }

    @Override
    public boolean hasDVs() {
        return false;
    }

    @Override
    public int generationOfPokemon() {
        return 5;
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
            NARCArchive babyNARC = readNARC(romEntry.getFile("BabyPokemon"));
            // baby pokemon
            for (int i = 1; i <= Gen5Constants.pokemonCount; i++) {
                Pokemon baby = pokes[i];
                while (baby.evolutionsTo.size() > 0) {
                    // Grab the first "to evolution" even if there are multiple
                    baby = baby.evolutionsTo.get(0).from;
                }
                writeWord(babyNARC.files.get(i), 0, baby.number);
            }
            // finish up
            writeNARC(romEntry.getFile("BabyPokemon"), babyNARC);
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
        // cut, fly, surf, strength, flash, dig, teleport, waterfall,
        // sweet scent, dive
        return Gen5Constants.fieldMoves;
    }

    @Override
    public List<Integer> getEarlyRequiredHMMoves() {
        // BW1: cut
        // BW2: none
        if (romEntry.romType == Gen5Constants.Type_BW2) {
            return Gen5Constants.bw2EarlyRequiredHMMoves;
        } else {
            return Gen5Constants.bw1EarlyRequiredHMMoves;
        }
    }

    @Override
    public Map<Integer, Shop> getShopItems() {
        int[] tmShops = romEntry.arrayEntries.get("TMShops");
        int[] regularShops = romEntry.arrayEntries.get("RegularShops");
        int[] shopItemOffsets = romEntry.arrayEntries.get("ShopItemOffsets");
        int[] shopItemSizes = romEntry.arrayEntries.get("ShopItemSizes");
        int shopCount = romEntry.getInt("ShopCount");
        List<Integer> shopItems = new ArrayList<>();
        Map<Integer, Shop> shopItemsMap = new TreeMap<>();

        try {
            byte[] shopItemOverlay = readOverlay(romEntry.getInt("ShopItemOvlNumber"));
            IntStream.range(0, shopCount).forEachOrdered(i -> {
                boolean badShop = false;
                for (int tmShop : tmShops) {
                    if (i == tmShop) {
                        badShop = true;
                        break;
                    }
                }
                for (int regularShop : regularShops) {
                    if (badShop) break;
                    if (i == regularShop) {
                        badShop = true;
                        break;
                    }
                }
                if (!badShop) {
                    List<Integer> items = new ArrayList<>();
                    if (romEntry.romType == Gen5Constants.Type_BW) {
                        for (int j = 0; j < shopItemSizes[i]; j++) {
                            items.add(readWord(shopItemOverlay, shopItemOffsets[i] + j * 2));
                        }
                    } else if (romEntry.romType == Gen5Constants.Type_BW2) {
                        byte[] shop = shopNarc.files.get(i);
                        for (int j = 0; j < shop.length; j += 2) {
                            items.add(readWord(shop, j));
                        }
                    }
                    Shop shop = new Shop();
                    shop.items = items;
                    shop.name = shopNames.get(i);
                    shop.isMainGame = Gen5Constants.getMainGameShops(romEntry.romType).contains(i);
                    shopItemsMap.put(i, shop);
                }
            });
            return shopItemsMap;
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public void setShopItems(Map<Integer, Shop> shopItems) {
        int[] shopItemOffsets = romEntry.arrayEntries.get("ShopItemOffsets");
        int[] shopItemSizes = romEntry.arrayEntries.get("ShopItemSizes");
        int[] tmShops = romEntry.arrayEntries.get("TMShops");
        int[] regularShops = romEntry.arrayEntries.get("RegularShops");
        int shopCount = romEntry.getInt("ShopCount");

        try {
            byte[] shopItemOverlay = readOverlay(romEntry.getInt("ShopItemOvlNumber"));
            IntStream.range(0, shopCount).forEachOrdered(i -> {
                boolean badShop = false;
                for (int tmShop : tmShops) {
                    if (badShop) break;
                    if (i == tmShop) badShop = true;
                }
                for (int regularShop : regularShops) {
                    if (badShop) break;
                    if (i == regularShop) badShop = true;
                }
                if (!badShop) {
                    List<Integer> shopContents = shopItems.get(i).items;
                    Iterator<Integer> iterItems = shopContents.iterator();
                    if (romEntry.romType == Gen5Constants.Type_BW) {
                        for (int j = 0; j < shopItemSizes[i]; j++) {
                            Integer item = iterItems.next();
                            writeWord(shopItemOverlay, shopItemOffsets[i] + j * 2, item);
                        }
                    } else if (romEntry.romType == Gen5Constants.Type_BW2) {
                        byte[] shop = shopNarc.files.get(i);
                        for (int j = 0; j < shop.length; j += 2) {
                            Integer item = iterItems.next();
                            writeWord(shop, j, item);
                        }
                    }
                }
            });
            if (romEntry.romType == Gen5Constants.Type_BW2) {
                writeNARC(romEntry.getFile("ShopItems"), shopNarc);
            } else {
                writeOverlay(romEntry.getInt("ShopItemOvlNumber"), shopItemOverlay);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public void setShopPrices() {
        try {
            NARCArchive itemPriceNarc = this.readNARC(romEntry.getFile("ItemData"));
            for (int i = 1; i < itemPriceNarc.files.size(); i++) {
                writeWord(itemPriceNarc.files.get(i),0,Gen5Constants.balancedItemPrices.get(i));
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
            byte[] battleOverlay = readOverlay(romEntry.getInt("PickupOvlNumber"));

            // If we haven't found the pickup table for this ROM already, find it.
            if (pickupItemsTableOffset == 0) {
                int offset = find(battleOverlay, Gen5Constants.pickupTableLocator);
                if (offset > 0) {
                    pickupItemsTableOffset = offset;
                }
            }

            // Assuming we've found the pickup table, extract the items out of it.
            if (pickupItemsTableOffset > 0) {
                for (int i = 0; i < Gen5Constants.numberOfPickupItems; i++) {
                    int itemOffset = pickupItemsTableOffset + (2 * i);
                    int item = FileFunctions.read2ByteInt(battleOverlay, itemOffset);
                    PickupItem pickupItem = new PickupItem(item);
                    pickupItems.add(pickupItem);
                }
            }

            // Assuming we got the items from the last step, fill out the probabilities.
            if (pickupItems.size() > 0) {
                for (int levelRange = 0; levelRange < 10; levelRange++) {
                    int startingRareItemOffset = levelRange;
                    int startingCommonItemOffset = 11 + levelRange;
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
            if (pickupItemsTableOffset > 0) {
                byte[] battleOverlay = readOverlay(romEntry.getInt("PickupOvlNumber"));
                for (int i = 0; i < Gen5Constants.numberOfPickupItems; i++) {
                    int itemOffset = pickupItemsTableOffset + (2 * i);
                    int item = pickupItems.get(i).item;
                    FileFunctions.write2ByteInt(battleOverlay, itemOffset, item);
                }
                writeOverlay(romEntry.getInt("PickupOvlNumber"), battleOverlay);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
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
            Pokemon pk = randomPokemonInclFormes();
            NARCArchive pokespritesNARC = this.readNARC(romEntry.getFile("PokemonGraphics"));

            // First prepare the palette, it's the easy bit
            int palIndex = pk.getSpriteIndex() * 20 + 18;
            if (random.nextInt(10) == 0) {
                // shiny
                palIndex++;
            }
            byte[] rawPalette = pokespritesNARC.files.get(palIndex);
            int[] palette = new int[16];
            for (int i = 1; i < 16; i++) {
                palette[i] = GFXFunctions.conv16BitColorToARGB(readWord(rawPalette, 40 + i * 2));
            }

            // Get the picture and uncompress it.
            byte[] compressedPic = pokespritesNARC.files.get(pk.getSpriteIndex() * 20);
            byte[] uncompressedPic = DSDecmp.Decompress(compressedPic);

            // Output to 64x144 tiled image to prepare for unscrambling
            BufferedImage bim = GFXFunctions.drawTiledImage(uncompressedPic, palette, 48, 64, 144, 4);

            // Unscramble the above onto a 96x96 canvas
            BufferedImage finalImage = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
            Graphics g = finalImage.getGraphics();
            g.drawImage(bim, 0, 0, 64, 64, 0, 0, 64, 64, null);
            g.drawImage(bim, 64, 0, 96, 8, 0, 64, 32, 72, null);
            g.drawImage(bim, 64, 8, 96, 16, 32, 64, 64, 72, null);
            g.drawImage(bim, 64, 16, 96, 24, 0, 72, 32, 80, null);
            g.drawImage(bim, 64, 24, 96, 32, 32, 72, 64, 80, null);
            g.drawImage(bim, 64, 32, 96, 40, 0, 80, 32, 88, null);
            g.drawImage(bim, 64, 40, 96, 48, 32, 80, 64, 88, null);
            g.drawImage(bim, 64, 48, 96, 56, 0, 88, 32, 96, null);
            g.drawImage(bim, 64, 56, 96, 64, 32, 88, 64, 96, null);
            g.drawImage(bim, 0, 64, 64, 96, 0, 96, 64, 128, null);
            g.drawImage(bim, 64, 64, 96, 72, 0, 128, 32, 136, null);
            g.drawImage(bim, 64, 72, 96, 80, 32, 128, 64, 136, null);
            g.drawImage(bim, 64, 80, 96, 88, 0, 136, 32, 144, null);
            g.drawImage(bim, 64, 88, 96, 96, 32, 136, 64, 144, null);

            // Phew, all done.
            return finalImage;
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public List<Integer> getAllHeldItems() {
        return Gen5Constants.allHeldItems;
    }

    @Override
    public List<Integer> getAllConsumableHeldItems() {
        return Gen5Constants.consumableHeldItems;
    }

    @Override
    public List<Integer> getSensibleHeldItemsFor(TrainerPokemon tp, boolean consumableOnly, List<Move> moves, int[] pokeMoves) {
        List<Integer> items = new ArrayList<>();
        items.addAll(Gen5Constants.generalPurposeConsumableItems);
        int frequencyBoostCount = 6; // Make some very good items more common, but not too common
        if (!consumableOnly) {
            frequencyBoostCount = 8; // bigger to account for larger item pool.
            items.addAll(Gen5Constants.generalPurposeItems);
        }
        for (int moveIdx : pokeMoves) {
            Move move = moves.get(moveIdx);
            if (move == null) {
                continue;
            }
            if (move.category == MoveCategory.PHYSICAL) {
                items.add(Items.liechiBerry);
                items.add(Gen5Constants.consumableTypeBoostingItems.get(move.type));
                if (!consumableOnly) {
                    items.addAll(Gen5Constants.typeBoostingItems.get(move.type));
                    items.add(Items.choiceBand);
                    items.add(Items.muscleBand);
                }
            }
            if (move.category == MoveCategory.SPECIAL) {
                items.add(Items.petayaBerry);
                items.add(Gen5Constants.consumableTypeBoostingItems.get(move.type));
                if (!consumableOnly) {
                    items.addAll(Gen5Constants.typeBoostingItems.get(move.type));
                    items.add(Items.wiseGlasses);
                    items.add(Items.choiceSpecs);
                }
            }
            if (!consumableOnly && Gen5Constants.moveBoostingItems.containsKey(moveIdx)) {
                items.addAll(Gen5Constants.moveBoostingItems.get(moveIdx));
            }
        }
        Map<Type, Effectiveness> byType = Effectiveness.against(tp.pokemon.primaryType, tp.pokemon.secondaryType, 5, effectivenessUpdated);
        for(Map.Entry<Type, Effectiveness> entry : byType.entrySet()) {
            Integer berry = Gen5Constants.weaknessReducingBerries.get(entry.getKey());
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
        } else if (byType.get(Type.GROUND) == Effectiveness.DOUBLE || byType.get(Type.GROUND) == Effectiveness.QUADRUPLE) {
            items.add(Items.airBalloon);
        }

        if (!consumableOnly) {
            if (Gen5Constants.abilityBoostingItems.containsKey(ability)) {
                items.addAll(Gen5Constants.abilityBoostingItems.get(ability));
            }
            if (tp.pokemon.primaryType == Type.POISON || tp.pokemon.secondaryType == Type.POISON) {
                items.add(Items.blackSludge);
            }
            List<Integer> speciesItems = Gen5Constants.speciesBoostingItems.get(tp.pokemon.number);
            if (speciesItems != null) {
                for (int i = 0; i < frequencyBoostCount; i++) {
                    items.addAll(speciesItems);
                }
            }
            if (!tp.pokemon.evolutionsFrom.isEmpty() && tp.level >= 20) {
                // eviolite can be too good for early game, so we gate it behind a minimum level.
                // We go with the same level as the option for "No early wonder guard".
                items.add(Items.eviolite);
            }
        }
        return items;
    }
}
