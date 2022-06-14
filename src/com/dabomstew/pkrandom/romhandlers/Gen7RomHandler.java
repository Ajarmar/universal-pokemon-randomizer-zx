package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Gen7RomHandler.java - randomizer handler for Su/Mo/US/UM.             --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
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

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.RomFunctions;
import com.dabomstew.pkrandom.Settings;
import com.dabomstew.pkrandom.constants.*;
import com.dabomstew.pkrandom.ctr.AMX;
import com.dabomstew.pkrandom.ctr.BFLIM;
import com.dabomstew.pkrandom.ctr.GARCArchive;
import com.dabomstew.pkrandom.ctr.Mini;
import com.dabomstew.pkrandom.exceptions.RandomizerIOException;
import com.dabomstew.pkrandom.pokemon.*;
import pptxt.N3DSTxtHandler;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Gen7RomHandler extends Abstract3DSRomHandler {

    public static class Factory extends RomHandler.Factory {

        @Override
        public Gen7RomHandler create(Random random, PrintStream logStream) {
            return new Gen7RomHandler(random, logStream);
        }

        public boolean isLoadable(String filename) {
            return detect3DSRomInner(getProductCodeFromFile(filename), getTitleIdFromFile(filename));
        }
    }

    public Gen7RomHandler(Random random) {
        super(random, null);
    }

    public Gen7RomHandler(Random random, PrintStream logStream) {
        super(random, logStream);
    }

    private static class OffsetWithinEntry {
        private int entry;
        private int offset;
    }

    private static class RomFileEntry {
        public String path;
        public long[] expectedCRC32s;
    }

    private static class RomEntry {
        private String name;
        private String romCode;
        private String titleId;
        private String acronym;
        private int romType;
        private long[] expectedCodeCRC32s = new long[2];
        private Map<String, RomFileEntry> files = new HashMap<>();
        private Map<Integer, Integer> linkedStaticOffsets = new HashMap<>();
        private Map<String, String> strings = new HashMap<>();
        private Map<String, Integer> numbers = new HashMap<>();
        private Map<String, int[]> arrayEntries = new HashMap<>();
        private Map<String, OffsetWithinEntry[]> offsetArrayEntries = new HashMap<>();

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
            Scanner sc = new Scanner(FileFunctions.openConfig("gen7_offsets.ini"), "UTF-8");
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
                        } else if (r[0].equals("Type")) {
                            if (r[1].equalsIgnoreCase("USUM")) {
                                current.romType = Gen7Constants.Type_USUM;
                            } else {
                                current.romType = Gen7Constants.Type_SM;
                            }
                        } else if (r[0].equals("TitleId")) {
                            current.titleId = r[1];
                        } else if (r[0].equals("Acronym")) {
                            current.acronym = r[1];
                        } else if (r[0].startsWith("File<")) {
                            String key = r[0].split("<")[1].split(">")[0];
                            String[] values = r[1].substring(1, r[1].length() - 1).split(",");
                            String path = values[0];
                            String crcString = values[1].trim() + ", " + values[2].trim();
                            String[] crcs = crcString.substring(1, crcString.length() - 1).split(",");
                            RomFileEntry entry = new RomFileEntry();
                            entry.path = path.trim();
                            entry.expectedCRC32s = new long[2];
                            entry.expectedCRC32s[0] = parseRILong("0x" + crcs[0].trim());
                            entry.expectedCRC32s[1] = parseRILong("0x" + crcs[1].trim());
                            current.files.put(key, entry);
                        } else if (r[0].equals("CodeCRC32")) {
                            String[] values = r[1].substring(1, r[1].length() - 1).split(",");
                            current.expectedCodeCRC32s[0] = parseRILong("0x" + values[0].trim());
                            current.expectedCodeCRC32s[1] = parseRILong("0x" + values[1].trim());
                        } else if (r[0].equals("LinkedStaticEncounterOffsets")) {
                            String[] offsets = r[1].substring(1, r[1].length() - 1).split(",");
                            for (int i = 0; i < offsets.length; i++) {
                                String[] parts = offsets[i].split(":");
                                current.linkedStaticOffsets.put(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
                            }
                        } else if (r[0].endsWith("Offset") || r[0].endsWith("Count") || r[0].endsWith("Number")) {
                            int offs = parseRIInt(r[1]);
                            current.numbers.put(r[0], offs);
                        } else if (r[1].startsWith("[") && r[1].endsWith("]")) {
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
                        } else if (r[0].equals("CopyFrom")) {
                            for (RomEntry otherEntry : roms) {
                                if (r[1].equalsIgnoreCase(otherEntry.romCode)) {
                                    // copy from here
                                    current.linkedStaticOffsets.putAll(otherEntry.linkedStaticOffsets);
                                    current.arrayEntries.putAll(otherEntry.arrayEntries);
                                    current.numbers.putAll(otherEntry.numbers);
                                    current.strings.putAll(otherEntry.strings);
                                    current.offsetArrayEntries.putAll(otherEntry.offsetArrayEntries);
                                    current.files.putAll(otherEntry.files);
                                }
                            }
                        } else {
                            current.strings.put(r[0],r[1]);
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

    // This ROM
    private Pokemon[] pokes;
    private Map<Integer,FormeInfo> formeMappings = new TreeMap<>();
    private Map<Integer,Map<Integer,Integer>> absolutePokeNumByBaseForme;
    private Map<Integer,Integer> dummyAbsolutePokeNums;
    private List<Pokemon> pokemonList;
    private List<Pokemon> pokemonListInclFormes;
    private List<MegaEvolution> megaEvolutions;
    private List<AreaData> areaDataList;
    private Move[] moves;
    private RomEntry romEntry;
    private byte[] code;
    private List<String> itemNames;
    private List<String> shopNames;
    private List<String> abilityNames;
    private ItemList allowedItems, nonBadItems;
    private long actualCodeCRC32;
    private Map<String, Long> actualFileCRC32s;

    private GARCArchive pokeGarc, moveGarc, encounterGarc, stringsGarc, storyTextGarc;

    @Override
    protected boolean detect3DSRom(String productCode, String titleId) {
        return detect3DSRomInner(productCode, titleId);
    }

    private static boolean detect3DSRomInner(String productCode, String titleId) {
        return entryFor(productCode, titleId) != null;
    }

    private static RomEntry entryFor(String productCode, String titleId) {
        if (productCode == null || titleId == null) {
            return null;
        }

        for (RomEntry re : roms) {
            if (productCode.equals(re.romCode) && titleId.equals(re.titleId)) {
                return re;
            }
        }
        return null;
    }

    @Override
    protected void loadedROM(String productCode, String titleId) {
        this.romEntry = entryFor(productCode, titleId);

        try {
            code = readCode();
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        try {
            stringsGarc = readGARC(romEntry.getFile("TextStrings"), true);
            storyTextGarc = readGARC(romEntry.getFile("StoryText"), true);
            areaDataList = getAreaData();
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        loadPokemonStats();
        loadMoves();

        pokemonListInclFormes = Arrays.asList(pokes);
        pokemonList = Arrays.asList(Arrays.copyOfRange(pokes,0,Gen7Constants.getPokemonCount(romEntry.romType) + 1));

        itemNames = getStrings(false,romEntry.getInt("ItemNamesTextOffset"));
        abilityNames = getStrings(false,romEntry.getInt("AbilityNamesTextOffset"));
        shopNames = Gen7Constants.getShopNames(romEntry.romType);

        allowedItems = Gen7Constants.getAllowedItems(romEntry.romType).copy();
        nonBadItems = Gen7Constants.nonBadItems.copy();

        if (romEntry.romType == Gen7Constants.Type_SM) {
            isSM = true;
        }

        try {
            computeCRC32sForRom();
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private List<String> getStrings(boolean isStoryText, int index) {
        GARCArchive baseGARC = isStoryText ? storyTextGarc : stringsGarc;
        return getStrings(baseGARC, index);
    }

    private List<String> getStrings(GARCArchive textGARC, int index) {
        byte[] rawFile = textGARC.files.get(index).get(0);
        return new ArrayList<>(N3DSTxtHandler.readTexts(rawFile,true,romEntry.romType));
    }

    private void setStrings(boolean isStoryText, int index, List<String> strings) {
        GARCArchive baseGARC = isStoryText ? storyTextGarc : stringsGarc;
        setStrings(baseGARC, index, strings);
    }

    private void setStrings(GARCArchive textGARC, int index, List<String> strings) {
        byte[] oldRawFile = textGARC.files.get(index).get(0);
        try {
            byte[] newRawFile = N3DSTxtHandler.saveEntry(oldRawFile, strings, romEntry.romType);
            textGARC.setFile(index, newRawFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPokemonStats() {
        try {
            pokeGarc = this.readGARC(romEntry.getFile("PokemonStats"),true);
            String[] pokeNames = readPokemonNames();
            int pokemonCount = Gen7Constants.getPokemonCount(romEntry.romType);
            int formeCount = Gen7Constants.getFormeCount(romEntry.romType);
            pokes = new Pokemon[pokemonCount + formeCount + 1];
            for (int i = 1; i <= pokemonCount; i++) {
                pokes[i] = new Pokemon();
                pokes[i].number = i;
                loadBasicPokeStats(pokes[i],pokeGarc.files.get(i).get(0),formeMappings);
                pokes[i].name = pokeNames[i];
            }

            absolutePokeNumByBaseForme = new HashMap<>();
            dummyAbsolutePokeNums = new HashMap<>();
            dummyAbsolutePokeNums.put(255,0);

            int i = pokemonCount + 1;
            int formNum = 1;
            int prevSpecies = 0;
            Map<Integer,Integer> currentMap = new HashMap<>();
            for (int k: formeMappings.keySet()) {
                pokes[i] = new Pokemon();
                pokes[i].number = i;
                loadBasicPokeStats(pokes[i], pokeGarc.files.get(k).get(0),formeMappings);
                FormeInfo fi = formeMappings.get(k);
                int realBaseForme = pokes[fi.baseForme].baseForme == null ? fi.baseForme : pokes[fi.baseForme].baseForme.number;
                pokes[i].name = pokeNames[realBaseForme];
                pokes[i].baseForme = pokes[fi.baseForme];
                pokes[i].formeNumber = fi.formeNumber;
                if (pokes[i].actuallyCosmetic) {
                    pokes[i].formeSuffix = pokes[i].baseForme.formeSuffix;
                } else {
                    pokes[i].formeSuffix = Gen7Constants.getFormeSuffixByBaseForme(fi.baseForme,fi.formeNumber);
                }
                if (realBaseForme == prevSpecies) {
                    formNum++;
                    currentMap.put(formNum,i);
                } else {
                    if (prevSpecies != 0) {
                        absolutePokeNumByBaseForme.put(prevSpecies,currentMap);
                    }
                    prevSpecies = realBaseForme;
                    formNum = 1;
                    currentMap = new HashMap<>();
                    currentMap.put(formNum,i);
                }
                i++;
            }
            if (prevSpecies != 0) {
                absolutePokeNumByBaseForme.put(prevSpecies,currentMap);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        populateEvolutions();
        populateMegaEvolutions();
    }

    private void loadBasicPokeStats(Pokemon pkmn, byte[] stats, Map<Integer,FormeInfo> altFormes) {
        pkmn.hp = stats[Gen7Constants.bsHPOffset] & 0xFF;
        pkmn.attack = stats[Gen7Constants.bsAttackOffset] & 0xFF;
        pkmn.defense = stats[Gen7Constants.bsDefenseOffset] & 0xFF;
        pkmn.speed = stats[Gen7Constants.bsSpeedOffset] & 0xFF;
        pkmn.spatk = stats[Gen7Constants.bsSpAtkOffset] & 0xFF;
        pkmn.spdef = stats[Gen7Constants.bsSpDefOffset] & 0xFF;
        // Type
        pkmn.primaryType = Gen7Constants.typeTable[stats[Gen7Constants.bsPrimaryTypeOffset] & 0xFF];
        pkmn.secondaryType = Gen7Constants.typeTable[stats[Gen7Constants.bsSecondaryTypeOffset] & 0xFF];
        // Only one type?
        if (pkmn.secondaryType == pkmn.primaryType) {
            pkmn.secondaryType = null;
        }
        pkmn.catchRate = stats[Gen7Constants.bsCatchRateOffset] & 0xFF;
        pkmn.growthCurve = ExpCurve.fromByte(stats[Gen7Constants.bsGrowthCurveOffset]);

        pkmn.ability1 = stats[Gen7Constants.bsAbility1Offset] & 0xFF;
        pkmn.ability2 = stats[Gen7Constants.bsAbility2Offset] & 0xFF;
        pkmn.ability3 = stats[Gen7Constants.bsAbility3Offset] & 0xFF;
        if (pkmn.ability1 == pkmn.ability2) {
            pkmn.ability2 = 0;
        }

        pkmn.callRate = stats[Gen7Constants.bsCallRateOffset] & 0xFF;

        // Held Items?
        int item1 = FileFunctions.read2ByteInt(stats, Gen7Constants.bsCommonHeldItemOffset);
        int item2 = FileFunctions.read2ByteInt(stats, Gen7Constants.bsRareHeldItemOffset);

        if (item1 == item2) {
            // guaranteed
            pkmn.guaranteedHeldItem = item1;
            pkmn.commonHeldItem = 0;
            pkmn.rareHeldItem = 0;
            pkmn.darkGrassHeldItem = -1;
        } else {
            pkmn.guaranteedHeldItem = 0;
            pkmn.commonHeldItem = item1;
            pkmn.rareHeldItem = item2;
            pkmn.darkGrassHeldItem = -1;
        }

        int formeCount = stats[Gen7Constants.bsFormeCountOffset] & 0xFF;
        if (formeCount > 1) {
            if (!altFormes.keySet().contains(pkmn.number)) {
                int firstFormeOffset = FileFunctions.read2ByteInt(stats, Gen7Constants.bsFormeOffset);
                if (firstFormeOffset != 0) {
                    int j = 0;
                    int jMax = 0;
                    int theAltForme = 0;
                    Set<Integer> altFormesWithCosmeticForms = Gen7Constants.getAltFormesWithCosmeticForms(romEntry.romType).keySet();
                    for (int i = 1; i < formeCount; i++) {
                        if (j == 0 || j > jMax) {
                            altFormes.put(firstFormeOffset + i - 1,new FormeInfo(pkmn.number,i,FileFunctions.read2ByteInt(stats,Gen7Constants.bsFormeSpriteOffset))); // Assumes that formes are in memory in the same order as their numbers
                            if (Gen7Constants.getActuallyCosmeticForms(romEntry.romType).contains(firstFormeOffset+i-1)) {
                                if (!Gen7Constants.getIgnoreForms(romEntry.romType).contains(firstFormeOffset+i-1)) { // Skip ignored forms (identical or confusing cosmetic forms)
                                    pkmn.cosmeticForms += 1;
                                    pkmn.realCosmeticFormNumbers.add(i);
                                }
                            }
                        } else {
                            altFormes.put(firstFormeOffset + i - 1,new FormeInfo(theAltForme,j,FileFunctions.read2ByteInt(stats,Gen7Constants.bsFormeSpriteOffset)));
                            j++;
                        }
                        if (altFormesWithCosmeticForms.contains(firstFormeOffset + i - 1)) {
                            j = 1;
                            jMax = Gen7Constants.getAltFormesWithCosmeticForms(romEntry.romType).get(firstFormeOffset + i - 1);
                            theAltForme = firstFormeOffset + i - 1;
                        }
                    }
                } else {
                    if (pkmn.number != Species.arceus && pkmn.number != Species.genesect && pkmn.number != Species.xerneas && pkmn.number != Species.silvally) {
                        // Reason for exclusions:
                        // Arceus/Genesect/Silvally: to avoid confusion
                        // Xerneas: Should be handled automatically?
                        pkmn.cosmeticForms = formeCount;
                    }
                }
            } else {
                if (!Gen7Constants.getIgnoreForms(romEntry.romType).contains(pkmn.number)) {
                    pkmn.cosmeticForms = Gen7Constants.getAltFormesWithCosmeticForms(romEntry.romType).getOrDefault(pkmn.number,0);
                }
                if (Gen7Constants.getActuallyCosmeticForms(romEntry.romType).contains(pkmn.number)) {
                    pkmn.actuallyCosmetic = true;
                }
            }
        }
    }

    private String[] readPokemonNames() {
        int pokemonCount = Gen7Constants.getPokemonCount(romEntry.romType);
        String[] pokeNames = new String[pokemonCount + 1];
        List<String> nameList = getStrings(false, romEntry.getInt("PokemonNamesTextOffset"));
        for (int i = 1; i <= pokemonCount; i++) {
            pokeNames[i] = nameList.get(i);
        }
        return pokeNames;
    }

    private void populateEvolutions() {
        for (Pokemon pkmn : pokes) {
            if (pkmn != null) {
                pkmn.evolutionsFrom.clear();
                pkmn.evolutionsTo.clear();
            }
        }

        // Read GARC
        try {
            GARCArchive evoGARC = readGARC(romEntry.getFile("PokemonEvolutions"),true);
            for (int i = 1; i <= Gen7Constants.getPokemonCount(romEntry.romType) + Gen7Constants.getFormeCount(romEntry.romType); i++) {
                Pokemon pk = pokes[i];
                byte[] evoEntry = evoGARC.files.get(i).get(0);
                boolean skipNext = false;
                for (int evo = 0; evo < 8; evo++) {
                    int method = readWord(evoEntry, evo * 8);
                    int species = readWord(evoEntry, evo * 8 + 4);
                    if (method >= 1 && method <= Gen7Constants.evolutionMethodCount && species >= 1) {
                        EvolutionType et = EvolutionType.fromIndex(7, method);
                        if (et.skipSplitEvo()) continue; // Remove Feebas "split" evolution
                        if (skipNext) {
                            skipNext = false;
                            continue;
                        }
                        if (et == EvolutionType.LEVEL_GAME) {
                            skipNext = true;
                        }

                        int extraInfo = readWord(evoEntry, evo * 8 + 2);
                        int forme = evoEntry[evo * 8 + 6];
                        int level = evoEntry[evo * 8 + 7];
                        Evolution evol = new Evolution(pk, getPokemonForEncounter(species,forme), true, et, extraInfo);
                        evol.forme = forme;
                        evol.level = level;
                        if (et.usesLevel()) {
                            evol.extraInfo = level;
                        }
                        switch (et) {
                            case LEVEL_GAME:
                                evol.type = EvolutionType.LEVEL;
                                evol.to = pokes[romEntry.getInt("CosmoemEvolutionNumber")];
                                break;
                            case LEVEL_DAY_GAME:
                                evol.type = EvolutionType.LEVEL_DAY;
                                break;
                            case LEVEL_NIGHT_GAME:
                                evol.type = EvolutionType.LEVEL_NIGHT;
                                break;
                            default:
                                break;
                        }
                        if (pk.baseForme != null && pk.baseForme.number == Species.rockruff && pk.formeNumber > 0) {
                            evol.from = pk.baseForme;
                            pk.baseForme.evolutionsFrom.add(evol);
                            pokes[absolutePokeNumByBaseForme.get(species).get(evol.forme)].evolutionsTo.add(evol);
                        }
                        if (!pk.evolutionsFrom.contains(evol)) {
                            pk.evolutionsFrom.add(evol);
                            if (!pk.actuallyCosmetic) {
                                if (evol.forme > 0) {
                                    // The forme number for the evolution might represent an actual alt forme, or it
                                    // might simply represent a cosmetic forme. If it represents an actual alt forme,
                                    // we'll need to figure out what the absolute species ID for that alt forme is
                                    // and update its evolutions. If it instead represents a cosmetic forme, then the
                                    // absolutePokeNumByBaseFormeMap will be null, since there's no secondary species
                                    // entry for this forme.
                                    Map<Integer, Integer> absolutePokeNumByBaseFormeMap = absolutePokeNumByBaseForme.get(species);
                                    if (absolutePokeNumByBaseFormeMap != null) {
                                        species = absolutePokeNumByBaseFormeMap.get(evol.forme);
                                    }
                                }
                                pokes[species].evolutionsTo.add(evol);
                            }
                        }
                    }
                }

                // Nincada's Shedinja evo is hardcoded into the game's executable,
                // so if the Pokemon is Nincada, then let's and put it as one of its evolutions
                if (pk.number == Species.nincada) {
                    Pokemon shedinja = pokes[Species.shedinja];
                    Evolution evol = new Evolution(pk, shedinja, false, EvolutionType.LEVEL_IS_EXTRA, 20);
                    evol.forme = -1;
                    evol.level = 20;
                    pk.evolutionsFrom.add(evol);
                    shedinja.evolutionsTo.add(evol);
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

    private void populateMegaEvolutions() {
        for (Pokemon pkmn : pokes) {
            if (pkmn != null) {
                pkmn.megaEvolutionsFrom.clear();
                pkmn.megaEvolutionsTo.clear();
            }
        }

        // Read GARC
        try {
            megaEvolutions = new ArrayList<>();
            GARCArchive megaEvoGARC = readGARC(romEntry.getFile("MegaEvolutions"),true);
            for (int i = 1; i <= Gen7Constants.getPokemonCount(romEntry.romType); i++) {
                Pokemon pk = pokes[i];
                byte[] megaEvoEntry = megaEvoGARC.files.get(i).get(0);
                for (int evo = 0; evo < 2; evo++) {
                    int formNum = readWord(megaEvoEntry, evo * 8);
                    int method = readWord(megaEvoEntry, evo * 8 + 2);
                    if (method >= 1) {
                        int argument = readWord(megaEvoEntry, evo * 8 + 4);
                        int megaSpecies = absolutePokeNumByBaseForme
                                .getOrDefault(pk.number,dummyAbsolutePokeNums)
                                .getOrDefault(formNum,0);
                        MegaEvolution megaEvo = new MegaEvolution(pk, pokes[megaSpecies], method, argument);
                        if (!pk.megaEvolutionsFrom.contains(megaEvo)) {
                            pk.megaEvolutionsFrom.add(megaEvo);
                            pokes[megaSpecies].megaEvolutionsTo.add(megaEvo);
                        }
                        megaEvolutions.add(megaEvo);
                    }
                }
                // split evos don't carry stats
                if (pk.megaEvolutionsFrom.size() > 1) {
                    for (MegaEvolution e : pk.megaEvolutionsFrom) {
                        e.carryStats = false;
                    }
                }
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void loadMoves() {
        try {
            moveGarc = this.readGARC(romEntry.getFile("MoveData"),true);
            int moveCount = Gen7Constants.getMoveCount(romEntry.romType);
            moves = new Move[moveCount + 1];
            List<String> moveNames = getStrings(false, romEntry.getInt("MoveNamesTextOffset"));
            byte[][] movesData = Mini.UnpackMini(moveGarc.files.get(0).get(0), "WD");
            for (int i = 1; i <= moveCount; i++) {
                byte[] moveData = movesData[i];
                moves[i] = new Move();
                moves[i].name = moveNames.get(i);
                moves[i].number = i;
                moves[i].internalId = i;
                moves[i].effectIndex = readWord(moveData, 16);
                moves[i].hitratio = (moveData[4] & 0xFF);
                moves[i].power = moveData[3] & 0xFF;
                moves[i].pp = moveData[5] & 0xFF;
                moves[i].type = Gen7Constants.typeTable[moveData[0] & 0xFF];
                moves[i].flinchPercentChance = moveData[15] & 0xFF;
                moves[i].target = moveData[20] & 0xFF;
                moves[i].category = Gen7Constants.moveCategoryIndices[moveData[2] & 0xFF];
                moves[i].priority = moveData[6];

                int critStages = moveData[14] & 0xFF;
                if (critStages == 6) {
                    moves[i].criticalChance = CriticalChance.GUARANTEED;
                } else if (critStages > 0) {
                    moves[i].criticalChance = CriticalChance.INCREASED;
                }

                int internalStatusType = readWord(moveData, 8);
                int flags = FileFunctions.readFullInt(moveData, 36);
                moves[i].makesContact = (flags & 0x001) != 0;
                moves[i].isChargeMove = (flags & 0x002) != 0;
                moves[i].isRechargeMove = (flags & 0x004) != 0;
                moves[i].isPunchMove = (flags & 0x080) != 0;
                moves[i].isSoundMove = (flags & 0x100) != 0;
                moves[i].isTrapMove = internalStatusType == 8;
                switch (moves[i].effectIndex) {
                    case Gen7Constants.noDamageTargetTrappingEffect:
                    case Gen7Constants.noDamageFieldTrappingEffect:
                    case Gen7Constants.damageAdjacentFoesTrappingEffect:
                    case Gen7Constants.damageTargetTrappingEffect:
                        moves[i].isTrapMove = true;
                        break;
                }

                int qualities = moveData[1];
                int recoilOrAbsorbPercent = moveData[18];
                if (qualities == Gen7Constants.damageAbsorbQuality) {
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
                    case Gen7Constants.noDamageStatChangeQuality:
                    case Gen7Constants.noDamageStatusAndStatChangeQuality:
                        // All Allies or Self
                        if (moves[i].target == 6 || moves[i].target == 7) {
                            moves[i].statChangeMoveType = StatChangeMoveType.NO_DAMAGE_USER;
                        } else if (moves[i].target == 2) {
                            moves[i].statChangeMoveType = StatChangeMoveType.NO_DAMAGE_ALLY;
                        } else if (moves[i].target == 8) {
                            moves[i].statChangeMoveType = StatChangeMoveType.NO_DAMAGE_ALL;
                        } else {
                            moves[i].statChangeMoveType = StatChangeMoveType.NO_DAMAGE_TARGET;
                        }
                        break;
                    case Gen7Constants.damageTargetDebuffQuality:
                        moves[i].statChangeMoveType = StatChangeMoveType.DAMAGE_TARGET;
                        break;
                    case Gen7Constants.damageUserBuffQuality:
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
                    switch (qualities) {
                        case Gen7Constants.noDamageStatusQuality:
                        case Gen7Constants.noDamageStatusAndStatChangeQuality:
                            moves[i].statusMoveType = StatusMoveType.NO_DAMAGE;
                            break;
                        case Gen7Constants.damageStatusQuality:
                            moves[i].statusMoveType = StatusMoveType.DAMAGE;
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    protected void savingROM() {
        savePokemonStats();
        saveMoves();
        try {
            writeCode(code);
            writeGARC(romEntry.getFile("WildPokemon"), encounterGarc);
            writeGARC(romEntry.getFile("TextStrings"), stringsGarc);
            writeGARC(romEntry.getFile("StoryText"), storyTextGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void savePokemonStats() {
        int k = Gen7Constants.bsSize;
        int pokemonCount = Gen7Constants.getPokemonCount(romEntry.romType);
        int formeCount = Gen7Constants.getFormeCount(romEntry.romType);
        byte[] duplicateData = pokeGarc.files.get(pokemonCount + formeCount + 1).get(0);
        for (int i = 1; i <= pokemonCount + formeCount; i++) {
            byte[] pokeData = pokeGarc.files.get(i).get(0);
            saveBasicPokeStats(pokes[i], pokeData);
            for (byte pokeDataByte : pokeData) {
                duplicateData[k] = pokeDataByte;
                k++;
            }
        }

        try {
            this.writeGARC(romEntry.getFile("PokemonStats"),pokeGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        writeEvolutions();
    }

    private void saveBasicPokeStats(Pokemon pkmn, byte[] stats) {
        stats[Gen7Constants.bsHPOffset] = (byte) pkmn.hp;
        stats[Gen7Constants.bsAttackOffset] = (byte) pkmn.attack;
        stats[Gen7Constants.bsDefenseOffset] = (byte) pkmn.defense;
        stats[Gen7Constants.bsSpeedOffset] = (byte) pkmn.speed;
        stats[Gen7Constants.bsSpAtkOffset] = (byte) pkmn.spatk;
        stats[Gen7Constants.bsSpDefOffset] = (byte) pkmn.spdef;
        stats[Gen7Constants.bsPrimaryTypeOffset] = Gen7Constants.typeToByte(pkmn.primaryType);
        if (pkmn.secondaryType == null) {
            stats[Gen7Constants.bsSecondaryTypeOffset] = stats[Gen7Constants.bsPrimaryTypeOffset];
        } else {
            stats[Gen7Constants.bsSecondaryTypeOffset] = Gen7Constants.typeToByte(pkmn.secondaryType);
        }
        stats[Gen7Constants.bsCatchRateOffset] = (byte) pkmn.catchRate;
        stats[Gen7Constants.bsGrowthCurveOffset] = pkmn.growthCurve.toByte();

        stats[Gen7Constants.bsAbility1Offset] = (byte) pkmn.ability1;
        stats[Gen7Constants.bsAbility2Offset] = pkmn.ability2 != 0 ? (byte) pkmn.ability2 : (byte) pkmn.ability1;
        stats[Gen7Constants.bsAbility3Offset] = (byte) pkmn.ability3;

        stats[Gen7Constants.bsCallRateOffset] = (byte) pkmn.callRate;

        // Held items
        if (pkmn.guaranteedHeldItem > 0) {
            FileFunctions.write2ByteInt(stats, Gen7Constants.bsCommonHeldItemOffset, pkmn.guaranteedHeldItem);
            FileFunctions.write2ByteInt(stats, Gen7Constants.bsRareHeldItemOffset, pkmn.guaranteedHeldItem);
            FileFunctions.write2ByteInt(stats, Gen7Constants.bsDarkGrassHeldItemOffset, 0);
        } else {
            FileFunctions.write2ByteInt(stats, Gen7Constants.bsCommonHeldItemOffset, pkmn.commonHeldItem);
            FileFunctions.write2ByteInt(stats, Gen7Constants.bsRareHeldItemOffset, pkmn.rareHeldItem);
            FileFunctions.write2ByteInt(stats, Gen7Constants.bsDarkGrassHeldItemOffset, 0);
        }

        if (pkmn.fullName().equals("Meowstic")) {
            stats[Gen7Constants.bsGenderOffset] = 0;
        } else if (pkmn.fullName().equals("Meowstic-F")) {
            stats[Gen7Constants.bsGenderOffset] = (byte)0xFE;
        }
    }

    private void writeEvolutions() {
        try {
            GARCArchive evoGARC = readGARC(romEntry.getFile("PokemonEvolutions"),true);
            for (int i = 1; i <= Gen7Constants.getPokemonCount(romEntry.romType) + Gen7Constants.getFormeCount(romEntry.romType); i++) {
                byte[] evoEntry = evoGARC.files.get(i).get(0);
                Pokemon pk = pokes[i];
                if (pk.number == Species.nincada) {
                    writeShedinjaEvolution();
                }
                int evosWritten = 0;
                for (Evolution evo : pk.evolutionsFrom) {
                    Pokemon toPK = evo.to;
                    writeWord(evoEntry, evosWritten * 8, evo.type.toIndex(7));
                    writeWord(evoEntry, evosWritten * 8 + 2, evo.type.usesLevel() ? 0 : evo.extraInfo);
                    writeWord(evoEntry, evosWritten * 8 + 4, toPK.getBaseNumber());
                    evoEntry[evosWritten * 8 + 6] = (byte)evo.forme;
                    evoEntry[evosWritten * 8 + 7] = evo.type.usesLevel() ? (byte)evo.extraInfo : (byte)evo.level;
                    evosWritten++;
                    if (evosWritten == 8) {
                        break;
                    }
                }
                while (evosWritten < 8) {
                    writeWord(evoEntry, evosWritten * 8, 0);
                    writeWord(evoEntry, evosWritten * 8 + 2, 0);
                    writeWord(evoEntry, evosWritten * 8 + 4, 0);
                    writeWord(evoEntry, evosWritten * 8 + 6, 0);
                    evosWritten++;
                }
            }
            writeGARC(romEntry.getFile("PokemonEvolutions"), evoGARC);
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
        Pokemon primaryEvolution = nincada.evolutionsFrom.get(0).to;
        Pokemon extraEvolution = nincada.evolutionsFrom.get(1).to;

        // In the game's executable, there's a hardcoded check to see if the Pokemon
        // that just evolved is now a Ninjask after evolving; if it is, then we start
        // going down the path of creating a Shedinja. To accomplish this check, they
        // hardcoded Ninjask's species ID as a constant. We replace this constant
        // with the species ID of Nincada's new primary evolution; that way, evolving
        // Nincada will still produce an "extra" Pokemon like in older generations.
        int offset = find(code, Gen7Constants.ninjaskSpeciesPrefix);
        if (offset > 0) {
            offset += Gen7Constants.ninjaskSpeciesPrefix.length() / 2; // because it was a prefix
            FileFunctions.writeFullInt(code, offset, primaryEvolution.number);
        }

        // In the game's executable, there's a hardcoded value to indicate what "extra"
        // Pokemon to create. It produces a Shedinja using the following instruction:
        // mov r1, #0x124, where 0x124 = 292 in decimal, which is Shedinja's species ID.
        // We can't just blindly replace it, though, because certain constants (for example,
        // 0x125) cannot be moved without using the movw instruction. This works fine in
        // Citra, but crashes on real hardware. Instead, we have to annoyingly shift up a
        // big chunk of code to fill in a nop; we can then do a pc-relative load to a
        // constant in the new free space.
        offset = find(code, Gen7Constants.shedinjaPrefix);
        if (offset > 0) {
            offset += Gen7Constants.shedinjaPrefix.length() / 2; // because it was a prefix

            // Shift up everything below the last nop to make some room at the bottom of the function.
            for (int i = 84; i < 120; i++) {
                code[offset + i] = code[offset + i + 4];
            }

            // For every bl that we shifted up, patch them so they're now pointing to the same place they
            // were before (without this, they will be pointing to 0x4 before where they're supposed to).
            List<Integer> blOffsetsToPatch = Arrays.asList(84, 96, 108);
            for (int blOffsetToPatch : blOffsetsToPatch) {
                code[offset + blOffsetToPatch] += 1;
            }

            // Write Nincada's new extra evolution in the new free space.
            writeLong(code, offset + 120, extraEvolution.number);

            // Second parameter of pml::pokepara::CoreParam::ChangeMonsNo is the
            // new forme number
            code[offset] = (byte) extraEvolution.formeNumber;

            // First parameter of pml::pokepara::CoreParam::ChangeMonsNo is the
            // new species number. Write a pc-relative load to what we wrote before.
            code[offset + 4] = (byte) 0x6C;
            code[offset + 5] = 0x10;
            code[offset + 6] = (byte) 0x9F;
            code[offset + 7] = (byte) 0xE5;
        }

        // Now that we've handled the hardcoded Shedinja evolution, delete it so that
        // we do *not* handle it in WriteEvolutions
        nincada.evolutionsFrom.remove(1);
        extraEvolution.evolutionsTo.remove(0);
    }

    private void saveMoves() {
        int moveCount = Gen7Constants.getMoveCount(romEntry.romType);
        byte[][] movesData = Mini.UnpackMini(moveGarc.files.get(0).get(0), "WD");
        for (int i = 1; i <= moveCount; i++) {
            byte[] moveData = movesData[i];
            moveData[2] = Gen7Constants.moveCategoryToByte(moves[i].category);
            moveData[3] = (byte) moves[i].power;
            moveData[0] = Gen7Constants.typeToByte(moves[i].type);
            int hitratio = (int) Math.round(moves[i].hitratio);
            if (hitratio < 0) {
                hitratio = 0;
            }
            if (hitratio > 101) {
                hitratio = 100;
            }
            moveData[4] = (byte) hitratio;
            moveData[5] = (byte) moves[i].pp;
        }
        try {
            moveGarc.setFile(0, Mini.PackMini(movesData, "WD"));
            this.writeGARC(romEntry.getFile("MoveData"), moveGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void patchFormeReversion() throws IOException {
        // Upon loading a save, all Mega Pokemon, all Primal Reversions,
        // all Greninja-A, all Zygarde-C, and all Necrozma-U in the player's
        // party are set back to their base forme. This patches .code such
        // that this reversion does not happen.
        String saveLoadFormeReversionPrefix = Gen7Constants.getSaveLoadFormeReversionPrefix(romEntry.romType);
        int offset = find(code, saveLoadFormeReversionPrefix);
        if (offset > 0) {
            offset += saveLoadFormeReversionPrefix.length() / 2; // because it was a prefix

            // The actual offset of the code we want to patch is 8 bytes from the end of
            // the prefix. We have to do this because these 8 bytes differ between the
            // base game and all game updates, so we cannot use them as part of our prefix.
            offset += 8;

            // Stubs the call to the function that checks for Primal Reversions and
            // Mega Pokemon
            code[offset] = 0x00;
            code[offset + 1] = 0x00;
            code[offset + 2] = 0x00;
            code[offset + 3] = 0x00;

            if (romEntry.romType == Gen7Constants.Type_USUM) {
                // In Sun/Moon, Greninja-A and Zygarde-C are treated as Mega Pokemon
                // and handled by the function above. In USUM, they are handled by a
                // different function, along with Necrozma-U. This stubs the call
                // to that function.
                code[offset + 8] = 0x00;
                code[offset + 9] = 0x00;
                code[offset + 10] = 0x00;
                code[offset + 11] = 0x00;
            }
        }

        // Additionally, upon completing a battle, Kyogre-P, Groudon-P,
        // and Wishiwashi-S are forcibly returned to their base forme.
        // Minior is also forcibly set to the "correct" Core forme.
        // This patches the Battle CRO to prevent this from happening.
        byte[] battleCRO = readFile(romEntry.getFile("Battle"));
        offset = find(battleCRO, Gen7Constants.afterBattleFormeReversionPrefix);
        if (offset > 0) {
            offset += Gen7Constants.afterBattleFormeReversionPrefix.length() / 2; // because it was a prefix

            // Stubs the call to pml::pokepara::CoreParam::ChangeFormNo for Kyogre
            battleCRO[offset] = 0x00;
            battleCRO[offset + 1] = 0x00;
            battleCRO[offset + 2] = 0x00;
            battleCRO[offset + 3] = 0x00;

            // Stubs the call to pml::pokepara::CoreParam::ChangeFormNo for Groudon
            battleCRO[offset + 60] = 0x00;
            battleCRO[offset + 61] = 0x00;
            battleCRO[offset + 62] = 0x00;
            battleCRO[offset + 63] = 0x00;

            // Stubs the call to pml::pokepara::CoreParam::ChangeFormNo for Wishiwashi
            battleCRO[offset + 92] = 0x00;
            battleCRO[offset + 93] = 0x00;
            battleCRO[offset + 94] = 0x00;
            battleCRO[offset + 95] = 0x00;

            // Stubs the call to pml::pokepara::CoreParam::ChangeFormNo for Minior
            battleCRO[offset + 148] = 0x00;
            battleCRO[offset + 149] = 0x00;
            battleCRO[offset + 150] = 0x00;
            battleCRO[offset + 151] = 0x00;

            writeFile(romEntry.getFile("Battle"), battleCRO);
        }
    }

    @Override
    protected String getGameAcronym() {
        return romEntry.acronym;
    }

    @Override
    protected boolean isGameUpdateSupported(int version) {
        return version == romEntry.numbers.get("FullyUpdatedVersionNumber");
    }

    @Override
    protected String getGameVersion() {
        List<String> titleScreenText = getStrings(false, romEntry.getInt("TitleScreenTextOffset"));
        if (titleScreenText.size() > romEntry.getInt("UpdateStringOffset")) {
            return titleScreenText.get(romEntry.getInt("UpdateStringOffset"));
        }
        // This shouldn't be seen by users, but is correct assuming we accidentally show it to them.
        return "Unpatched";
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
        int formeCount = Gen7Constants.getFormeCount(romEntry.romType);
        int pokemonCount = Gen7Constants.getPokemonCount(romEntry.romType);
        return pokemonListInclFormes.subList(pokemonCount + 1, pokemonCount + formeCount + 1);
    }

    @Override
    public List<MegaEvolution> getMegaEvolutions() {
        return megaEvolutions;
    }

    @Override
    public Pokemon getAltFormeOfPokemon(Pokemon pk, int forme) {
        int pokeNum = absolutePokeNumByBaseForme.getOrDefault(pk.number,dummyAbsolutePokeNums).getOrDefault(forme,0);
        return pokeNum != 0 ? !pokes[pokeNum].actuallyCosmetic ? pokes[pokeNum] : pokes[pokeNum].baseForme : pk;
    }

    @Override
    public List<Pokemon> getIrregularFormes() {
        return Gen7Constants.getIrregularFormes(romEntry.romType).stream().map(i -> pokes[i]).collect(Collectors.toList());
    }

    @Override
    public boolean hasFunctionalFormes() {
        return true;
    }

    @Override
    public List<Pokemon> getStarters() {
        List<StaticEncounter> starters = new ArrayList<>();
        try {
            GARCArchive staticGarc = readGARC(romEntry.getFile("StaticPokemon"), true);
            byte[] giftsFile = staticGarc.files.get(0).get(0);
            for (int i = 0; i < 3; i++) {
                int offset = i * 0x14;
                StaticEncounter se = new StaticEncounter();
                int species = FileFunctions.read2ByteInt(giftsFile, offset);
                Pokemon pokemon = pokes[species];
                int forme = giftsFile[offset + 2];
                if (forme > pokemon.cosmeticForms && forme != 30 && forme != 31) {
                    int speciesWithForme = absolutePokeNumByBaseForme
                            .getOrDefault(species, dummyAbsolutePokeNums)
                            .getOrDefault(forme, 0);
                    pokemon = pokes[speciesWithForme];
                }
                se.pkmn = pokemon;
                se.forme = forme;
                se.level = giftsFile[offset + 3];
                se.heldItem = FileFunctions.read2ByteInt(giftsFile, offset + 8);
                starters.add(se);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return starters.stream().map(pk -> pk.pkmn).collect(Collectors.toList());
    }

    @Override
    public boolean setStarters(List<Pokemon> newStarters) {
        try {
            GARCArchive staticGarc = readGARC(romEntry.getFile("StaticPokemon"), true);
            byte[] giftsFile = staticGarc.files.get(0).get(0);
            for (int i = 0; i < 3; i++) {
                int offset = i * 0x14;
                Pokemon starter = newStarters.get(i);
                int forme = 0;
                boolean checkCosmetics = true;
                if (starter.formeNumber > 0) {
                    forme = starter.formeNumber;
                    starter = starter.baseForme;
                    checkCosmetics = false;
                }
                if (checkCosmetics && starter.cosmeticForms > 0) {
                    forme = starter.getCosmeticFormNumber(this.random.nextInt(starter.cosmeticForms));
                } else if (!checkCosmetics && starter.cosmeticForms > 0) {
                    forme += starter.getCosmeticFormNumber(this.random.nextInt(starter.cosmeticForms));
                }
                writeWord(giftsFile, offset, starter.number);
                giftsFile[offset + 2] = (byte) forme;
            }
            writeGARC(romEntry.getFile("StaticPokemon"), staticGarc);
            setStarterText(newStarters);
            return true;
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    // TODO: We should be editing the script file so that the game reads in our new
    // starters; this way, strings that depend on the starter defined in the script
    // would work without any modification. Instead, we're just manually editing all
    // strings here, and if a string originally referred to the starter in the script,
    // we just hardcode the starter's name if we can get away with it.
    private void setStarterText(List<Pokemon> newStarters) {
        int starterTextIndex = romEntry.getInt("StarterTextOffset");
        List<String> starterText = getStrings(true, starterTextIndex);
        if (romEntry.romType == Gen7Constants.Type_USUM) {
            String rowletDescriptor = newStarters.get(0).name + starterText.get(1).substring(6);
            String littenDescriptor = newStarters.get(1).name + starterText.get(2).substring(6);
            String popplioDescriptor = newStarters.get(2).name + starterText.get(3).substring(7);
            starterText.set(1, rowletDescriptor);
            starterText.set(2, littenDescriptor);
            starterText.set(3, popplioDescriptor);
            for (int i = 0; i < 3; i++) {
                int confirmationOffset = i + 7;
                int optionOffset = i + 14;
                Pokemon starter = newStarters.get(i);
                String confirmationText = String.format("So, you wanna go with the %s-type Pokémon\\n%s?[VAR 0114(0005)]",
                        starter.primaryType.camelCase(), starter.name);
                String optionText = starter.name;
                starterText.set(confirmationOffset, confirmationText);
                starterText.set(optionOffset, optionText);
            }
        } else {
            String rowletDescriptor = newStarters.get(0).name + starterText.get(11).substring(6);
            String littenDescriptor = newStarters.get(1).name + starterText.get(12).substring(6);
            String popplioDescriptor = newStarters.get(2).name + starterText.get(13).substring(7);
            starterText.set(11, rowletDescriptor);
            starterText.set(12, littenDescriptor);
            starterText.set(13, popplioDescriptor);
            for (int i = 0; i < 3; i++) {
                int optionOffset = i + 1;
                int confirmationOffset = i + 4;
                int flavorOffset = i + 35;
                Pokemon starter = newStarters.get(i);
                String optionText = String.format("The %s-type %s", starter.primaryType.camelCase(), starter.name);
                String confirmationText = String.format("Will you choose the %s-type Pokémon\\n%s?[VAR 0114(0008)]",
                        starter.primaryType.camelCase(), starter.name);
                String flavorSubstring = starterText.get(flavorOffset).substring(starterText.get(flavorOffset).indexOf("\\n"));
                String flavorText = String.format("The %s-type %s", starter.primaryType.camelCase(), starter.name) + flavorSubstring;
                starterText.set(optionOffset, optionText);
                starterText.set(confirmationOffset, confirmationText);
                starterText.set(flavorOffset, flavorText);
            }
        }
        setStrings(true, starterTextIndex, starterText);
    }

    @Override
    public boolean hasStarterAltFormes() {
        return true;
    }

    @Override
    public int starterCount() {
        return 3;
    }

    @Override
    public Map<Integer, StatChange> getUpdatedPokemonStats(int generation) {
        Map<Integer, StatChange> map = GlobalConstants.getStatChanges(generation);
        switch(generation) {
            case 8:
                map.put(859,new StatChange(Stat.ATK.val | Stat.SPATK.val,140,140));
                break;
        }
        return map;
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

    @Override
    public List<Move> getMoves() {
        return Arrays.asList(moves);
    }

    @Override
    public List<EncounterSet> getEncounters(boolean useTimeOfDay) {
        List<EncounterSet> encounters = new ArrayList<>();
        for (AreaData areaData : areaDataList) {
            if (!areaData.hasTables) {
                continue;
            }
            for (int i = 0; i < areaData.encounterTables.size(); i++) {
                byte[] encounterTable = areaData.encounterTables.get(i);
                byte[] dayTable = new byte[0x164];
                System.arraycopy(encounterTable, 0, dayTable, 0, 0x164);
                EncounterSet dayEncounters = readEncounterTable(dayTable);
                if (!useTimeOfDay) {
                    dayEncounters.displayName = areaData.name + ", Table " + (i + 1);
                    encounters.add(dayEncounters);
                } else {
                    dayEncounters.displayName = areaData.name + ", Table " + (i + 1) + " (Day)";
                    encounters.add(dayEncounters);
                    byte[] nightTable = new byte[0x164];
                    System.arraycopy(encounterTable, 0x164, nightTable, 0, 0x164);
                    EncounterSet nightEncounters = readEncounterTable(nightTable);
                    nightEncounters.displayName = areaData.name + ", Table " + (i + 1) + " (Night)";
                    encounters.add(nightEncounters);
                }
            }
        }
        return encounters;
    }

    private EncounterSet readEncounterTable(byte[] encounterTable) {
        int minLevel = encounterTable[0];
        int maxLevel = encounterTable[1];
        EncounterSet es = new EncounterSet();
        es.rate = 1;
        for (int i = 0; i < 10; i++) {
            int offset = 0xC + (i * 4);
            int speciesAndFormeData = readWord(encounterTable, offset);
            int species = speciesAndFormeData & 0x7FF;
            int forme = speciesAndFormeData >> 11;
            if (species != 0) {
                Encounter e = new Encounter();
                e.pokemon = getPokemonForEncounter(species, forme);
                e.formeNumber = forme;
                e.level = minLevel;
                e.maxLevel = maxLevel;
                es.encounters.add(e);

                // Get all the SOS encounters for this non-SOS encounter
                for (int j = 1; j < 8; j++) {
                    species = readWord(encounterTable, offset + (40 * j)) & 0x7FF;
                    forme = readWord(encounterTable, offset + (40 * j)) >> 11;
                    Encounter sos = new Encounter();
                    sos.pokemon = getPokemonForEncounter(species, forme);
                    sos.formeNumber = forme;
                    sos.level = minLevel;
                    sos.maxLevel = maxLevel;
                    sos.isSOS = true;
                    sos.sosType = SOSType.GENERIC;
                    es.encounters.add(sos);
                }
            }
        }

        // Get the weather SOS encounters for this area
        for (int i = 0; i < 6; i++) {
            int offset = 0x14C + (i * 4);
            int species = readWord(encounterTable, offset) & 0x7FF;
            int forme = readWord(encounterTable, offset) >> 11;
            if (species != 0) {
                Encounter weatherSOS = new Encounter();
                weatherSOS.pokemon = getPokemonForEncounter(species, forme);
                weatherSOS.formeNumber = forme;
                weatherSOS.level = minLevel;
                weatherSOS.maxLevel = maxLevel;
                weatherSOS.isSOS = true;
                weatherSOS.sosType = getSOSTypeForIndex(i);
                es.encounters.add(weatherSOS);
            }
        }
        return es;
    }

    private SOSType getSOSTypeForIndex(int index) {
        if (index / 2 == 0) {
            return SOSType.RAIN;
        } else if (index / 2 == 1) {
            return SOSType.HAIL;
        } else {
            return SOSType.SAND;
        }
    }

    private Pokemon getPokemonForEncounter(int species, int forme) {
        Pokemon pokemon = pokes[species];

        // If the forme is purely cosmetic, just use the base forme as the Pokemon
        // for this encounter (the cosmetic forme will be stored in the encounter).
        if (forme <= pokemon.cosmeticForms || forme == 30 || forme == 31) {
            return pokemon;
        } else {
            int speciesWithForme = absolutePokeNumByBaseForme
                    .getOrDefault(species, dummyAbsolutePokeNums)
                    .getOrDefault(forme, 0);
            return pokes[speciesWithForme];
        }
    }

    @Override
    public void setEncounters(boolean useTimeOfDay, List<EncounterSet> encountersList) {
        Iterator<EncounterSet> encounters = encountersList.iterator();
        for (AreaData areaData : areaDataList) {
            if (!areaData.hasTables) {
                continue;
            }

            for (int i = 0; i < areaData.encounterTables.size(); i++) {
                byte[] encounterTable = areaData.encounterTables.get(i);
                if (useTimeOfDay) {
                    EncounterSet dayEncounters = encounters.next();
                    EncounterSet nightEncounters = encounters.next();
                    writeEncounterTable(encounterTable, 0, dayEncounters.encounters);
                    writeEncounterTable(encounterTable, 0x164, nightEncounters.encounters);
                } else {
                    EncounterSet dayEncounters = encounters.next();
                    writeEncounterTable(encounterTable, 0, dayEncounters.encounters);
                    writeEncounterTable(encounterTable, 0x164, dayEncounters.encounters);
                }
            }
        }

        try {
            saveAreaData();
            patchMiniorEncounterCode();
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void writeEncounterTable(byte[] encounterTable, int offset, List<Encounter> encounters) {
        Iterator<Encounter> encounter = encounters.iterator();
        Encounter firstEncounter = encounters.get(0);
        encounterTable[offset] = (byte) firstEncounter.level;
        encounterTable[offset + 1] = (byte) firstEncounter.maxLevel;
        int numberOfEncounterSlots = encounters.size() / 8;
        for (int i = 0; i < numberOfEncounterSlots; i++) {
            int currentOffset = offset + 0xC + (i * 4);
            Encounter enc = encounter.next();
            int speciesAndFormeData = (enc.formeNumber << 11) + enc.pokemon.getBaseNumber();
            writeWord(encounterTable, currentOffset, speciesAndFormeData);

            // SOS encounters for this encounter
            for (int j = 1; j < 8; j++) {
                Encounter sosEncounter = encounter.next();
                speciesAndFormeData = (sosEncounter.formeNumber << 11) + sosEncounter.pokemon.getBaseNumber();
                writeWord(encounterTable, currentOffset + (40 * j), speciesAndFormeData);
            }
        }

        // Weather SOS encounters
        if (encounters.size() != numberOfEncounterSlots * 8) {
            for (int i = 0; i < 6; i++) {
                int currentOffset = offset + 0x14C + (i * 4);
                Encounter weatherSOSEncounter = encounter.next();
                int speciesAndFormeData = (weatherSOSEncounter.formeNumber << 11) + weatherSOSEncounter.pokemon.getBaseNumber();
                writeWord(encounterTable, currentOffset, speciesAndFormeData);
            }
        }
    }

    private List<AreaData> getAreaData() throws IOException {
        GARCArchive worldDataGarc = readGARC(romEntry.getFile("WorldData"), false);
        List<byte[]> worlds = new ArrayList<>();
        for (Map<Integer, byte[]> file : worldDataGarc.files) {
            byte[] world = Mini.UnpackMini(file.get(0), "WD")[0];
            worlds.add(world);
        }
        GARCArchive zoneDataGarc = readGARC(romEntry.getFile("ZoneData"), false);
        byte[] zoneDataBytes = zoneDataGarc.getFile(0);
        byte[] worldData = zoneDataGarc.getFile(1);
        List<String> locationList = createGoodLocationList();
        ZoneData[] zoneData = getZoneData(zoneDataBytes, worldData, locationList, worlds);
        encounterGarc = readGARC(romEntry.getFile("WildPokemon"), Gen7Constants.getRelevantEncounterFiles(romEntry.romType));
        int fileCount = encounterGarc.files.size();
        int numberOfAreas = fileCount / 11;
        AreaData[] areaData = new AreaData[numberOfAreas];
        for (int i = 0; i < numberOfAreas; i++) {
            int areaOffset = i;
            areaData[i] = new AreaData();
            areaData[i].fileNumber = 9 + (11 * i);
            areaData[i].zones = Arrays.stream(zoneData).filter((zone -> zone.areaIndex == areaOffset)).collect(Collectors.toList());
            areaData[i].name = getAreaNameFromZones(areaData[i].zones);
            byte[] encounterData = encounterGarc.getFile(areaData[i].fileNumber);
            if (encounterData.length == 0) {
                areaData[i].hasTables = false;
            } else {
                byte[][] encounterTables = Mini.UnpackMini(encounterData, "EA");
                areaData[i].hasTables = Arrays.stream(encounterTables).anyMatch(t -> t.length > 0);
                if (!areaData[i].hasTables) {
                    continue;
                }

                for (byte[] encounterTable : encounterTables) {
                    byte[] trimmedEncounterTable = new byte[0x2C8];
                    System.arraycopy(encounterTable, 4, trimmedEncounterTable, 0, 0x2C8);
                    areaData[i].encounterTables.add(trimmedEncounterTable);
                }
            }
        }

        return Arrays.asList(areaData);
    }

    private void saveAreaData() throws IOException {
        for (AreaData areaData : areaDataList) {
            if (areaData.hasTables) {
                byte[] encounterData = encounterGarc.getFile(areaData.fileNumber);
                byte[][] encounterTables = Mini.UnpackMini(encounterData, "EA");
                for (int i = 0; i < encounterTables.length; i++) {
                    byte[] originalEncounterTable = encounterTables[i];
                    byte[] newEncounterTable = areaData.encounterTables.get(i);
                    System.arraycopy(newEncounterTable, 0, originalEncounterTable, 4, newEncounterTable.length);
                }
                byte[] newEncounterData = Mini.PackMini(encounterTables, "EA");
                encounterGarc.setFile(areaData.fileNumber, newEncounterData);
            }
        }
    }

    private List<String> createGoodLocationList() {
        List<String> locationList = getStrings(false, romEntry.getInt("MapNamesTextOffset"));
        List<String> goodLocationList = new ArrayList<>(locationList);
        for (int i = 0; i < locationList.size(); i += 2) {
            // The location list contains both areas and subareas. If a subarea is associated with an area, it will
            // appear directly after it. This code combines these subarea and area names.
            String subarea = locationList.get(i + 1);
            if (!subarea.isEmpty() && subarea.charAt(0) != '[') {
                String updatedLocation = goodLocationList.get(i) + " (" + subarea + ")";
                goodLocationList.set(i, updatedLocation);
            }

            // Some areas appear in the location list multiple times and don't have any subarea name to distinguish
            // them. This code distinguishes them by appending the number of times they've appeared previously to
            // the area name.
            if (i > 0) {
                List<String> goodLocationUpToCurrent = goodLocationList.stream().limit(i - 1).collect(Collectors.toList());
                if (!goodLocationList.get(i).isEmpty() && goodLocationUpToCurrent.contains(goodLocationList.get(i))) {
                    int numberOfUsages = Collections.frequency(goodLocationUpToCurrent, goodLocationList.get(i));
                    String updatedLocation = goodLocationList.get(i) + " (" + (numberOfUsages + 1) + ")";
                    goodLocationList.set(i, updatedLocation);
                }
            }
        }
        return goodLocationList;
    }

    private ZoneData[] getZoneData(byte[] zoneDataBytes, byte[] worldData, List<String> locationList, List<byte[]> worlds) {
        ZoneData[] zoneData = new ZoneData[zoneDataBytes.length / ZoneData.size];
        for (int i = 0; i < zoneData.length; i++) {
            zoneData[i] = new ZoneData(zoneDataBytes, i);
            zoneData[i].worldIndex = FileFunctions.read2ByteInt(worldData, i * 0x2);
            zoneData[i].locationName = locationList.get(zoneData[i].parentMap);

            byte[] world = worlds.get(zoneData[i].worldIndex);
            int mappingOffset = FileFunctions.readFullInt(world, 0x8);
            for (int offset = mappingOffset; offset < world.length; offset += 4) {
                int potentialZoneIndex = FileFunctions.read2ByteInt(world, offset);
                if (potentialZoneIndex == i) {
                    zoneData[i].areaIndex = FileFunctions.read2ByteInt(world, offset + 0x2);
                    break;
                }
            }
        }
        return zoneData;
    }

    private String getAreaNameFromZones(List<ZoneData> zoneData) {
        Set<String> uniqueZoneNames = new HashSet<>();
        for (ZoneData zone : zoneData) {
            uniqueZoneNames.add(zone.locationName);
        }
        return String.join(" / ", uniqueZoneNames);
    }

    private void patchMiniorEncounterCode() {
        int offset = find(code, Gen7Constants.miniorWildEncounterPatchPrefix);
        if (offset > 0) {
            offset += Gen7Constants.miniorWildEncounterPatchPrefix.length() / 2;

            // When deciding the *actual* forme for a wild encounter (versus the forme stored
            // in the encounter data), the game has a hardcoded check for Minior's species ID.
            // If the species is Minior, then it branches to code that randomly selects a forme
            // for one of Minior's seven Meteor forms. As a consequence, you can't directly
            // spawn Minior's Core forms; the forme number will just be replaced. The below
            // code nops out the beq instruction so that Minior-C can be spawned directly.
            code[offset] = 0x00;
            code[offset + 1] = 0x00;
            code[offset + 2] = 0x00;
            code[offset + 3] = 0x00;
        }
    }

    @Override
    public List<Trainer> getTrainers() {
        List<Trainer> allTrainers = new ArrayList<>();
        try {
            GARCArchive trainers = this.readGARC(romEntry.getFile("TrainerData"),true);
            GARCArchive trpokes = this.readGARC(romEntry.getFile("TrainerPokemon"),true);
            int trainernum = trainers.files.size();
            List<String> tclasses = this.getTrainerClassNames();
            List<String> tnames = this.getTrainerNames();
            Map<Integer,String> tnamesMap = new TreeMap<>();
            for (int i = 0; i < tnames.size(); i++) {
                tnamesMap.put(i,tnames.get(i));
            }
            for (int i = 1; i < trainernum; i++) {
                byte[] trainer = trainers.files.get(i).get(0);
                byte[] trpoke = trpokes.files.get(i).get(0);
                Trainer tr = new Trainer();
                tr.poketype = trainer[13] & 0xFF;
                tr.index = i;
                tr.trainerclass = trainer[0] & 0xFF;
                int battleType = trainer[2] & 0xFF;
                int numPokes = trainer[3] & 0xFF;
                int trainerAILevel = trainer[12] & 0xFF;
                boolean healer = trainer[15] != 0;
                int pokeOffs = 0;
                String trainerClass = tclasses.get(tr.trainerclass);
                String trainerName = tnamesMap.getOrDefault(i - 1, "UNKNOWN");
                tr.fullDisplayName = trainerClass + " " + trainerName;

                for (int poke = 0; poke < numPokes; poke++) {
                    // Structure is
                    // IV SB LV LV SP SP FRM FRM
                    // (HI HI)
                    // (M1 M1 M2 M2 M3 M3 M4 M4)
                    // where SB = 0 0 Ab Ab 0 0 Fm Ml
                    // Ab Ab = ability number, 0 for random
                    // Fm = 1 for forced female
                    // Ml = 1 for forced male
                    // There's also a trainer flag to force gender, but
                    // this allows fixed teams with mixed genders.

                    // int secondbyte = trpoke[pokeOffs + 1] & 0xFF;
                    int abilityAndFlag = trpoke[pokeOffs];
                    int level = readWord(trpoke, pokeOffs + 14);
                    int species = readWord(trpoke, pokeOffs + 16);
                    int formnum = readWord(trpoke, pokeOffs + 18);
                    TrainerPokemon tpk = new TrainerPokemon();
                    tpk.abilitySlot = (abilityAndFlag >>> 4) & 0xF;
                    tpk.forcedGenderFlag = (abilityAndFlag & 0xF);
                    tpk.nature = trpoke[pokeOffs + 1];
                    tpk.hpEVs = trpoke[pokeOffs + 2];
                    tpk.atkEVs = trpoke[pokeOffs + 3];
                    tpk.defEVs = trpoke[pokeOffs + 4];
                    tpk.spatkEVs = trpoke[pokeOffs + 5];
                    tpk.spdefEVs = trpoke[pokeOffs + 6];
                    tpk.speedEVs = trpoke[pokeOffs + 7];
                    tpk.IVs = FileFunctions.readFullInt(trpoke, pokeOffs + 8);
                    tpk.level = level;
                    if (romEntry.romType == Gen7Constants.Type_USUM) {
                        if (i == 78) {
                            if (poke == 3 && tpk.level == 16 && tr.pokemon.get(0).level == 16) {
                                tpk.level = 14;
                            }
                        }
                    }
                    tpk.pokemon = pokes[species];
                    tpk.forme = formnum;
                    tpk.formeSuffix = Gen7Constants.getFormeSuffixByBaseForme(species,formnum);
                    pokeOffs += 20;
                    tpk.heldItem = readWord(trpoke, pokeOffs);
                    tpk.hasMegaStone = Gen6Constants.isMegaStone(tpk.heldItem);
                    tpk.hasZCrystal = Gen7Constants.isZCrystal(tpk.heldItem);
                    pokeOffs += 4;
                    for (int move = 0; move < 4; move++) {
                        tpk.moves[move] = readWord(trpoke, pokeOffs + (move*2));
                    }
                    pokeOffs += 8;
                    tr.pokemon.add(tpk);
                }
                allTrainers.add(tr);
            }
            if (romEntry.romType == Gen7Constants.Type_SM) {
                Gen7Constants.tagTrainersSM(allTrainers);
                Gen7Constants.setMultiBattleStatusSM(allTrainers);
            } else {
                Gen7Constants.tagTrainersUSUM(allTrainers);
                Gen7Constants.setMultiBattleStatusUSUM(allTrainers);
                Gen7Constants.setForcedRivalStarterPositionsUSUM(allTrainers);
            }
        } catch (IOException ex) {
            throw new RandomizerIOException(ex);
        }
        return allTrainers;
    }

    @Override
    public List<Integer> getMainPlaythroughTrainers() {
        return new ArrayList<>();
    }

    @Override
    public List<Integer> getEliteFourTrainers(boolean isChallengeMode) {
        return Arrays.stream(romEntry.arrayEntries.get("EliteFourIndices")).boxed().collect(Collectors.toList());
    }

    @Override
    public void setTrainers(List<Trainer> trainerData, boolean doubleBattleMode) {
        Iterator<Trainer> allTrainers = trainerData.iterator();
        try {
            GARCArchive trainers = this.readGARC(romEntry.getFile("TrainerData"),true);
            GARCArchive trpokes = this.readGARC(romEntry.getFile("TrainerPokemon"),true);
            // Get current movesets in case we need to reset them for certain
            // trainer mons.
            Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();
            int trainernum = trainers.files.size();
            for (int i = 1; i < trainernum; i++) {
                byte[] trainer = trainers.files.get(i).get(0);
                Trainer tr = allTrainers.next();
                int offset = 0;
                trainer[13] = (byte) tr.poketype;
                int numPokes = tr.pokemon.size();
                trainer[offset+3] = (byte) numPokes;

                if (doubleBattleMode) {
                    if (!tr.skipImportant()) {
                        if (trainer[offset+2] == 0) {
                            trainer[offset+2] = 1;
                            trainer[offset+12] |= 0x8; // Flag that needs to be set for trainers not to attack their own pokes
                        }
                    }
                }

                int bytesNeeded = 32 * numPokes;
                byte[] trpoke = new byte[bytesNeeded];
                int pokeOffs = 0;
                Iterator<TrainerPokemon> tpokes = tr.pokemon.iterator();
                for (int poke = 0; poke < numPokes; poke++) {
                    TrainerPokemon tp = tpokes.next();
                    byte abilityAndFlag = (byte)((tp.abilitySlot << 4) | tp.forcedGenderFlag);
                    trpoke[pokeOffs] = abilityAndFlag;
                    trpoke[pokeOffs + 1] = tp.nature;
                    trpoke[pokeOffs + 2] = tp.hpEVs;
                    trpoke[pokeOffs + 3] = tp.atkEVs;
                    trpoke[pokeOffs + 4] = tp.defEVs;
                    trpoke[pokeOffs + 5] = tp.spatkEVs;
                    trpoke[pokeOffs + 6] = tp.spdefEVs;
                    trpoke[pokeOffs + 7] = tp.speedEVs;
                    FileFunctions.writeFullInt(trpoke, pokeOffs + 8, tp.IVs);
                    writeWord(trpoke, pokeOffs + 14, tp.level);
                    writeWord(trpoke, pokeOffs + 16, tp.pokemon.number);
                    writeWord(trpoke, pokeOffs + 18, tp.forme);
                    pokeOffs += 20;
                    writeWord(trpoke, pokeOffs, tp.heldItem);
                    pokeOffs += 4;
                    if (tp.resetMoves) {
                        int[] pokeMoves = RomFunctions.getMovesAtLevel(getAltFormeOfPokemon(tp.pokemon, tp.forme).number, movesets, tp.level);
                        for (int m = 0; m < 4; m++) {
                            writeWord(trpoke, pokeOffs + m * 2, pokeMoves[m]);
                        }
                        if (Gen7Constants.heldZCrystals.contains(tp.heldItem)) { // Choose a new Z-Crystal at random based on the types of the Pokemon's moves
                            int chosenMove = this.random.nextInt(Arrays.stream(pokeMoves).filter(mv -> mv != 0).toArray().length);
                            int newZCrystal = Gen7Constants.heldZCrystals.get((int)Gen7Constants.typeToByte(moves[pokeMoves[chosenMove]].type));
                            writeWord(trpoke, pokeOffs - 4, newZCrystal);
                        }
                    } else {
                        writeWord(trpoke, pokeOffs, tp.moves[0]);
                        writeWord(trpoke, pokeOffs + 2, tp.moves[1]);
                        writeWord(trpoke, pokeOffs + 4, tp.moves[2]);
                        writeWord(trpoke, pokeOffs + 6, tp.moves[3]);
                        if (Gen7Constants.heldZCrystals.contains(tp.heldItem)) { // Choose a new Z-Crystal at random based on the types of the Pokemon's moves
                            int chosenMove = this.random.nextInt(Arrays.stream(tp.moves).filter(mv -> mv != 0).toArray().length);
                            int newZCrystal = Gen7Constants.heldZCrystals.get((int)Gen7Constants.typeToByte(moves[tp.moves[chosenMove]].type));
                            writeWord(trpoke, pokeOffs - 4, newZCrystal);
                        }
                    }
                    pokeOffs += 8;
                }
                trpokes.setFile(i,trpoke);
            }
            this.writeGARC(romEntry.getFile("TrainerData"), trainers);
            this.writeGARC(romEntry.getFile("TrainerPokemon"), trpokes);

            // In Sun/Moon, Beast Lusamine's Pokemon have aura boosts that are hardcoded.
            if (romEntry.romType == Gen7Constants.Type_SM) {
                Trainer beastLusamine = trainerData.get(Gen7Constants.beastLusamineTrainerIndex);
                setBeastLusaminePokemonBuffs(beastLusamine);
            }
        } catch (IOException ex) {
            throw new RandomizerIOException(ex);
        }
    }

    private void setBeastLusaminePokemonBuffs(Trainer beastLusamine) throws IOException {
        byte[] battleCRO = readFile(romEntry.getFile("Battle"));
        int offset = find(battleCRO, Gen7Constants.beastLusaminePokemonBoostsPrefix);
        if (offset > 0) {
            offset += Gen7Constants.beastLusaminePokemonBoostsPrefix.length() / 2; // because it was a prefix

            // The game only has room for five boost entries, where each boost entry is determined by species ID.
            // However, Beast Lusamine might have duplicates in her party, meaning that two Pokemon can share the
            // same boost entry. First, figure out all the unique Pokemon in her party. We avoid using a Set here
            // in order to preserve the original ordering; we want to make sure to boost the *first* five Pokemon
            List<Pokemon> uniquePokemon = new ArrayList<>();
            for (int i = 0; i < beastLusamine.pokemon.size(); i++) {
                if (!uniquePokemon.contains(beastLusamine.pokemon.get(i).pokemon)) {
                    uniquePokemon.add(beastLusamine.pokemon.get(i).pokemon);
                }
            }
            int numberOfBoostEntries = Math.min(uniquePokemon.size(), 5);
            for (int i = 0; i < numberOfBoostEntries; i++) {
                Pokemon boostedPokemon = uniquePokemon.get(i);
                int auraNumber = getAuraNumberForHighestStat(boostedPokemon);
                int speciesNumber = boostedPokemon.getBaseNumber();
                FileFunctions.write2ByteInt(battleCRO, offset + (i * 0x10), speciesNumber);
                battleCRO[offset + (i * 0x10) + 2] = (byte) auraNumber;
            }
            writeFile(romEntry.getFile("Battle"), battleCRO);
        }
    }

    // Finds the highest stat for the purposes of setting the aura boost on Beast Lusamine's Pokemon.
    // In the case where two or more stats are tied for the highest stat, it randomly selects one.
    private int getAuraNumberForHighestStat(Pokemon boostedPokemon) {
        int currentBestStat = boostedPokemon.attack;
        int auraNumber = 1;
        boolean useDefenseAura = boostedPokemon.defense > currentBestStat || (boostedPokemon.defense == currentBestStat && random.nextBoolean());
        if (useDefenseAura) {
            currentBestStat = boostedPokemon.defense;
            auraNumber = 2;
        }
        boolean useSpAtkAura = boostedPokemon.spatk > currentBestStat || (boostedPokemon.spatk == currentBestStat && random.nextBoolean());
        if (useSpAtkAura) {
            currentBestStat = boostedPokemon.spatk;
            auraNumber = 3;
        }
        boolean useSpDefAura = boostedPokemon.spdef > currentBestStat || (boostedPokemon.spdef == currentBestStat && random.nextBoolean());
        if (useSpDefAura) {
            currentBestStat = boostedPokemon.spdef;
            auraNumber = 4;
        }
        boolean useSpeedAura = boostedPokemon.speed > currentBestStat || (boostedPokemon.speed == currentBestStat && random.nextBoolean());
        if (useSpeedAura) {
            auraNumber = 5;
        }
        return auraNumber;
    }

    @Override
    public List<Integer> getEvolutionItems() {
        return Gen7Constants.evolutionItems;
    }

    @Override
    public Map<Integer, List<MoveLearnt>> getMovesLearnt() {
        Map<Integer, List<MoveLearnt>> movesets = new TreeMap<>();
        try {
            GARCArchive movesLearnt = this.readGARC(romEntry.getFile("PokemonMovesets"),true);
            int formeCount = Gen7Constants.getFormeCount(romEntry.romType);
            for (int i = 1; i <= Gen7Constants.getPokemonCount(romEntry.romType) + formeCount; i++) {
                Pokemon pkmn = pokes[i];
                byte[] movedata;
                movedata = movesLearnt.files.get(i).get(0);
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
            GARCArchive movesLearnt = readGARC(romEntry.getFile("PokemonMovesets"),true);
            int formeCount = Gen7Constants.getFormeCount(romEntry.romType);
            for (int i = 1; i <= Gen7Constants.getPokemonCount(romEntry.romType) + formeCount; i++) {
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
                movesLearnt.setFile(i, moveset);
            }
            // Save
            this.writeGARC(romEntry.getFile("PokemonMovesets"), movesLearnt);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

    }

    @Override
    public Map<Integer, List<Integer>> getEggMoves() {
        Map<Integer, List<Integer>> eggMoves = new TreeMap<>();
        try {
            GARCArchive eggMovesGarc = this.readGARC(romEntry.getFile("EggMoves"),true);
            TreeMap<Pokemon, Integer> altFormeEggMoveFiles = new TreeMap<>();
            for (int i = 1; i <= Gen7Constants.getPokemonCount(romEntry.romType); i++) {
                Pokemon pkmn = pokes[i];
                byte[] movedata = eggMovesGarc.files.get(i).get(0);
                int formeReference = readWord(movedata, 0);
                if (formeReference != pkmn.number) {
                    altFormeEggMoveFiles.put(pkmn, formeReference);
                }
                int numberOfEggMoves = readWord(movedata, 2);
                List<Integer> moves = new ArrayList<>();
                for (int j = 0; j < numberOfEggMoves; j++) {
                    int move = readWord(movedata, 4 + (j * 2));
                    moves.add(move);
                }
                eggMoves.put(pkmn.number, moves);
            }
            Iterator<Pokemon> iter = altFormeEggMoveFiles.keySet().iterator();
            while (iter.hasNext()) {
                Pokemon originalForme = iter.next();
                int formeNumber = 1;
                int fileNumber = altFormeEggMoveFiles.get(originalForme);
                Pokemon altForme = getAltFormeOfPokemon(originalForme, formeNumber);
                while (!originalForme.equals(altForme)) {
                    byte[] movedata = eggMovesGarc.files.get(fileNumber).get(0);
                    int numberOfEggMoves = readWord(movedata, 2);
                    List<Integer> moves = new ArrayList<>();
                    for (int j = 0; j < numberOfEggMoves; j++) {
                        int move = readWord(movedata, 4 + (j * 2));
                        moves.add(move);
                    }
                    eggMoves.put(altForme.number, moves);
                    formeNumber++;
                    fileNumber++;
                    altForme = getAltFormeOfPokemon(originalForme, formeNumber);
                }
                iter.remove();
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return eggMoves;
    }

    @Override
    public void setEggMoves(Map<Integer, List<Integer>> eggMoves) {
        try {
            GARCArchive eggMovesGarc = this.readGARC(romEntry.getFile("EggMoves"), true);
            TreeMap<Pokemon, Integer> altFormeEggMoveFiles = new TreeMap<>();
            for (int i = 1; i <= Gen7Constants.getPokemonCount(romEntry.romType); i++) {
                Pokemon pkmn = pokes[i];
                byte[] movedata = eggMovesGarc.files.get(i).get(0);
                int formeReference = readWord(movedata, 0);
                if (formeReference != pkmn.number) {
                    altFormeEggMoveFiles.put(pkmn, formeReference);
                }
                List<Integer> moves = eggMoves.get(pkmn.number);
                for (int j = 0; j < moves.size(); j++) {
                    writeWord(movedata, 4 + (j * 2), moves.get(j));
                }
            }
            Iterator<Pokemon> iter = altFormeEggMoveFiles.keySet().iterator();
            while (iter.hasNext()) {
                Pokemon originalForme = iter.next();
                int formeNumber = 1;
                int fileNumber = altFormeEggMoveFiles.get(originalForme);
                Pokemon altForme = getAltFormeOfPokemon(originalForme, formeNumber);
                while (!originalForme.equals(altForme)) {
                    byte[] movedata = eggMovesGarc.files.get(fileNumber).get(0);
                    List<Integer> moves = eggMoves.get(altForme.number);
                    for (int j = 0; j < moves.size(); j++) {
                        writeWord(movedata, 4 + (j * 2), moves.get(j));
                    }
                    formeNumber++;
                    fileNumber++;
                    altForme = getAltFormeOfPokemon(originalForme, formeNumber);
                }
                iter.remove();
            }
            // Save
            this.writeGARC(romEntry.getFile("EggMoves"), eggMovesGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public boolean canChangeStaticPokemon() {
        return true;
    }

    @Override
    public boolean hasStaticAltFormes() {
        return true;
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
        return new ArrayList<>();
    }

    @Override
    public void applyCorrectStaticMusic(Map<Integer, Integer> specialMusicStaticChanges) {

    }

    @Override
    public boolean hasStaticMusicFix() {
        return false;
    }

    @Override
    public List<TotemPokemon> getTotemPokemon() {
        List<TotemPokemon> totems = new ArrayList<>();
        try {
            GARCArchive staticGarc = readGARC(romEntry.getFile("StaticPokemon"), true);
            List<Integer> totemIndices =
                    Arrays.stream(romEntry.arrayEntries.get("TotemPokemonIndices")).boxed().collect(Collectors.toList());

            // Static encounters
            byte[] staticEncountersFile = staticGarc.files.get(1).get(0);
            for (int i: totemIndices) {
                int offset = i * 0x38;
                TotemPokemon totem = new TotemPokemon();
                int species = FileFunctions.read2ByteInt(staticEncountersFile, offset);
                Pokemon pokemon = pokes[species];
                int forme = staticEncountersFile[offset + 2];
                if (forme > pokemon.cosmeticForms && forme != 30 && forme != 31) {
                    int speciesWithForme = absolutePokeNumByBaseForme
                            .getOrDefault(species, dummyAbsolutePokeNums)
                            .getOrDefault(forme, 0);
                    pokemon = pokes[speciesWithForme];
                }
                totem.pkmn = pokemon;
                totem.forme = forme;
                totem.level = staticEncountersFile[offset + 3];
                int heldItem = FileFunctions.read2ByteInt(staticEncountersFile, offset + 4);
                if (heldItem == 0xFFFF) {
                    heldItem = 0;
                }
                totem.heldItem = heldItem;
                totem.aura = new Aura(staticEncountersFile[offset + 0x25]);
                int allies = staticEncountersFile[offset + 0x27];
                for (int j = 0; j < allies; j++) {
                    int allyIndex = (staticEncountersFile[offset + 0x28 + 4*j] - 1) & 0xFF;
                    totem.allies.put(allyIndex,readStaticEncounter(staticEncountersFile, allyIndex * 0x38));
                }
                totems.add(totem);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return totems;
    }

    @Override
    public void setTotemPokemon(List<TotemPokemon> totemPokemon) {
        try {
            GARCArchive staticGarc = readGARC(romEntry.getFile("StaticPokemon"), true);
            List<Integer> totemIndices =
                    Arrays.stream(romEntry.arrayEntries.get("TotemPokemonIndices")).boxed().collect(Collectors.toList());
            Iterator<TotemPokemon> totemIter = totemPokemon.iterator();

            // Static encounters
            byte[] staticEncountersFile = staticGarc.files.get(1).get(0);
            for (int i: totemIndices) {
                int offset = i * 0x38;
                TotemPokemon totem = totemIter.next();
                if (totem.pkmn.formeNumber > 0) {
                    totem.forme = totem.pkmn.formeNumber;
                    totem.pkmn = totem.pkmn.baseForme;
                }
                writeWord(staticEncountersFile, offset, totem.pkmn.number);
                staticEncountersFile[offset + 2] = (byte) totem.forme;
                staticEncountersFile[offset + 3] = (byte) totem.level;
                if (totem.heldItem == 0) {
                    writeWord(staticEncountersFile, offset + 4, -1);
                } else {
                    writeWord(staticEncountersFile, offset + 4, totem.heldItem);
                }
                if (totem.resetMoves) {
                    writeWord(staticEncountersFile, offset + 12, 0);
                    writeWord(staticEncountersFile, offset + 14, 0);
                    writeWord(staticEncountersFile, offset + 16, 0);
                    writeWord(staticEncountersFile, offset + 18, 0);
                }
                staticEncountersFile[offset + 0x25] = totem.aura.toByte();
                for (Integer allyIndex: totem.allies.keySet()) {
                    offset = allyIndex * 0x38;
                    StaticEncounter ally = totem.allies.get(allyIndex);
                    if (ally.pkmn.formeNumber > 0) {
                        ally.forme = ally.pkmn.formeNumber;
                        ally.pkmn = ally.pkmn.baseForme;
                    }
                    writeWord(staticEncountersFile, offset, ally.pkmn.number);
                    staticEncountersFile[offset + 2] = (byte) ally.forme;
                    staticEncountersFile[offset + 3] = (byte) ally.level;
                    if (ally.heldItem == 0) {
                        writeWord(staticEncountersFile, offset + 4, -1);
                    } else {
                        writeWord(staticEncountersFile, offset + 4, ally.heldItem);
                    }
                    if (ally.resetMoves) {
                        writeWord(staticEncountersFile, offset + 12, 0);
                        writeWord(staticEncountersFile, offset + 14, 0);
                        writeWord(staticEncountersFile, offset + 16, 0);
                        writeWord(staticEncountersFile, offset + 18, 0);
                    }
                }
            }

            writeGARC(romEntry.getFile("StaticPokemon"), staticGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

    }

    @Override
    public List<StaticEncounter> getStaticPokemon() {
        List<StaticEncounter> statics = new ArrayList<>();
        try {
            GARCArchive staticGarc = readGARC(romEntry.getFile("StaticPokemon"), true);
            List<Integer> skipIndices =
                    Arrays.stream(romEntry.arrayEntries.get("TotemPokemonIndices")).boxed().collect(Collectors.toList());
            skipIndices.addAll(Arrays.stream(romEntry.arrayEntries.get("AllyPokemonIndices")).boxed().collect(Collectors.toList()));

            // Gifts, start at 3 to skip the starters
            byte[] giftsFile = staticGarc.files.get(0).get(0);
            int numberOfGifts = giftsFile.length / 0x14;
            for (int i = 3; i < numberOfGifts; i++) {
                int offset = i * 0x14;
                StaticEncounter se = new StaticEncounter();
                int species = FileFunctions.read2ByteInt(giftsFile, offset);
                Pokemon pokemon = pokes[species];
                int forme = giftsFile[offset + 2];
                if (forme > pokemon.cosmeticForms && forme != 30 && forme != 31) {
                    int speciesWithForme = absolutePokeNumByBaseForme
                            .getOrDefault(species, dummyAbsolutePokeNums)
                            .getOrDefault(forme, 0);
                    pokemon = pokes[speciesWithForme];
                }
                se.pkmn = pokemon;
                se.forme = forme;
                se.level = giftsFile[offset + 3];
                se.heldItem = FileFunctions.read2ByteInt(giftsFile, offset + 8);
                se.isEgg = giftsFile[offset + 10] == 1;
                statics.add(se);
            }

            // Static encounters
            byte[] staticEncountersFile = staticGarc.files.get(1).get(0);
            int numberOfStaticEncounters = staticEncountersFile.length / 0x38;
            for (int i = 0; i < numberOfStaticEncounters; i++) {
                if (skipIndices.contains(i)) continue;
                int offset = i * 0x38;
                StaticEncounter se = readStaticEncounter(staticEncountersFile, offset);
                statics.add(se);
            }

            // Zygarde created via Assembly on Route 16 is hardcoded
            readAssemblyZygarde(statics);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        consolidateLinkedEncounters(statics);
        return statics;
    }

    private StaticEncounter readStaticEncounter(byte[] staticEncountersFile, int offset) {
        StaticEncounter se = new StaticEncounter();
        int species = FileFunctions.read2ByteInt(staticEncountersFile, offset);
        Pokemon pokemon = pokes[species];
        int forme = staticEncountersFile[offset + 2];
        if (forme > pokemon.cosmeticForms && forme != 30 && forme != 31) {
            int speciesWithForme = absolutePokeNumByBaseForme
                    .getOrDefault(species, dummyAbsolutePokeNums)
                    .getOrDefault(forme, 0);
            pokemon = pokes[speciesWithForme];
        }
        se.pkmn = pokemon;
        se.forme = forme;
        se.level = staticEncountersFile[offset + 3];
        int heldItem = FileFunctions.read2ByteInt(staticEncountersFile, offset + 4);
        if (heldItem == 0xFFFF) {
            heldItem = 0;
        }
        se.heldItem = heldItem;
        return se;
    }

    private void consolidateLinkedEncounters(List<StaticEncounter> statics) {
        List<StaticEncounter> encountersToRemove = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : romEntry.linkedStaticOffsets.entrySet()) {
            StaticEncounter baseEncounter = statics.get(entry.getKey());
            StaticEncounter linkedEncounter = statics.get(entry.getValue());
            baseEncounter.linkedEncounters.add(linkedEncounter);
            encountersToRemove.add(linkedEncounter);
        }
        for (StaticEncounter encounter : encountersToRemove) {
            statics.remove(encounter);
        }
    }

    private void readAssemblyZygarde(List<StaticEncounter> statics) throws IOException {
        GARCArchive scriptGarc = readGARC(romEntry.getFile("Scripts"), true);
        int[] scriptLevelOffsets = romEntry.arrayEntries.get("ZygardeScriptLevelOffsets");
        int[] levels = new int[scriptLevelOffsets.length];
        byte[] zygardeAssemblyScriptBytes = scriptGarc.getFile(Gen7Constants.zygardeAssemblyScriptFile);
        AMX zygardeAssemblyScript = new AMX(zygardeAssemblyScriptBytes);
        for (int i = 0; i < scriptLevelOffsets.length; i++) {
            levels[i] = zygardeAssemblyScript.decData[scriptLevelOffsets[i]];
        }

        int speciesOffset = find(code, Gen7Constants.zygardeAssemblySpeciesPrefix);
        int formeOffset = find(code, Gen7Constants.zygardeAssemblyFormePrefix);
        if (speciesOffset > 0 && formeOffset > 0) {
            speciesOffset += Gen7Constants.zygardeAssemblySpeciesPrefix.length() / 2; // because it was a prefix
            formeOffset += Gen7Constants.zygardeAssemblyFormePrefix.length() / 2; // because it was a prefix
            int species = FileFunctions.read2ByteInt(code, speciesOffset);

            // The original code for this passed in the forme via a parameter, stored that onto
            // the stack, then did a ldr to put that stack variable into r0 before finally
            // storing that value in the right place. If we already modified this code, then we
            // don't care about all of this; we just wrote a "mov r0, #forme" over the ldr instead.
            // Thus, if the original ldr instruction is still there, assume we haven't touched it.
            int forme = 0;
            if (FileFunctions.readFullInt(code, formeOffset) == 0xE59D0040) {
                // Since we haven't modified the code yet, this is Zygarde. For SM, use 10%,
                // since you can get it fairly early. For USUM, use 50%, since it's only
                // obtainable in the postgame.
                forme = isSM ? 1 : 0;
            } else {
                // We have modified the code, so just read the constant forme number we wrote.
                forme = code[formeOffset];
            }

            StaticEncounter lowLevelAssembly = new StaticEncounter();
            Pokemon pokemon = pokes[species];
            if (forme > pokemon.cosmeticForms && forme != 30 && forme != 31) {
                int speciesWithForme = absolutePokeNumByBaseForme
                        .getOrDefault(species, dummyAbsolutePokeNums)
                        .getOrDefault(forme, 0);
                pokemon = pokes[speciesWithForme];
            }
            lowLevelAssembly.pkmn = pokemon;
            lowLevelAssembly.forme = forme;
            lowLevelAssembly.level = levels[0];
            for (int i = 1; i < levels.length; i++) {
                StaticEncounter higherLevelAssembly = new StaticEncounter();
                higherLevelAssembly.pkmn = pokemon;
                higherLevelAssembly.forme = forme;
                higherLevelAssembly.level = levels[i];
                lowLevelAssembly.linkedEncounters.add(higherLevelAssembly);
            }

            statics.add(lowLevelAssembly);
        }
    }

    @Override
    public boolean setStaticPokemon(List<StaticEncounter> staticPokemon) {
        try {
            unlinkStaticEncounters(staticPokemon);
            GARCArchive staticGarc = readGARC(romEntry.getFile("StaticPokemon"), true);
            List<Integer> skipIndices =
                    Arrays.stream(romEntry.arrayEntries.get("TotemPokemonIndices")).boxed().collect(Collectors.toList());
            skipIndices.addAll(Arrays.stream(romEntry.arrayEntries.get("AllyPokemonIndices")).boxed().collect(Collectors.toList()));
            Iterator<StaticEncounter> staticIter = staticPokemon.iterator();

            // Gifts, start at 3 to skip the starters
            byte[] giftsFile = staticGarc.files.get(0).get(0);
            int numberOfGifts = giftsFile.length / 0x14;
            for (int i = 3; i < numberOfGifts; i++) {
                int offset = i * 0x14;
                StaticEncounter se = staticIter.next();
                writeWord(giftsFile, offset, se.pkmn.number);
                giftsFile[offset + 2] = (byte) se.forme;
                giftsFile[offset + 3] = (byte) se.level;
                writeWord(giftsFile, offset + 8, se.heldItem);
            }

            // Static encounters
            byte[] staticEncountersFile = staticGarc.files.get(1).get(0);
            int numberOfStaticEncounters = staticEncountersFile.length / 0x38;
            for (int i = 0; i < numberOfStaticEncounters; i++) {
                if (skipIndices.contains(i)) continue;
                int offset = i * 0x38;
                StaticEncounter se = staticIter.next();
                writeWord(staticEncountersFile, offset, se.pkmn.number);
                staticEncountersFile[offset + 2] = (byte) se.forme;
                staticEncountersFile[offset + 3] = (byte) se.level;
                if (se.heldItem == 0) {
                    writeWord(staticEncountersFile, offset + 4, -1);
                } else {
                    writeWord(staticEncountersFile, offset + 4, se.heldItem);
                }
                if (se.resetMoves) {
                    writeWord(staticEncountersFile, offset + 12, 0);
                    writeWord(staticEncountersFile, offset + 14, 0);
                    writeWord(staticEncountersFile, offset + 16, 0);
                    writeWord(staticEncountersFile, offset + 18, 0);
                }
            }

            // Zygarde created via Assembly on Route 16 is hardcoded
            writeAssemblyZygarde(staticIter.next());

            writeGARC(romEntry.getFile("StaticPokemon"), staticGarc);
            return true;
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void unlinkStaticEncounters(List<StaticEncounter> statics) {
        List<Integer> offsetsToInsert = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : romEntry.linkedStaticOffsets.entrySet()) {
            offsetsToInsert.add(entry.getValue());
        }
        Collections.sort(offsetsToInsert);
        for (Integer offsetToInsert : offsetsToInsert) {
            statics.add(offsetToInsert, new StaticEncounter());
        }
        for (Map.Entry<Integer, Integer> entry : romEntry.linkedStaticOffsets.entrySet()) {
            StaticEncounter baseEncounter = statics.get(entry.getKey());
            statics.set(entry.getValue(), baseEncounter.linkedEncounters.get(0));
        }
    }

    private void writeAssemblyZygarde(StaticEncounter se) throws IOException {
        int[] levels = new int[se.linkedEncounters.size() + 1];
        levels[0] = se.level;
        for (int i = 0; i < se.linkedEncounters.size(); i++) {
            levels[i + 1] = se.linkedEncounters.get(i).level;
        }

        GARCArchive scriptGarc = readGARC(romEntry.getFile("Scripts"), true);
        int[] scriptLevelOffsets = romEntry.arrayEntries.get("ZygardeScriptLevelOffsets");
        byte[] zygardeAssemblyScriptBytes = scriptGarc.getFile(Gen7Constants.zygardeAssemblyScriptFile);
        AMX zygardeAssemblyScript = new AMX(zygardeAssemblyScriptBytes);
        for (int i = 0; i < scriptLevelOffsets.length; i++) {
            zygardeAssemblyScript.decData[scriptLevelOffsets[i]] = (byte) levels[i];
        }
        scriptGarc.setFile(Gen7Constants.zygardeAssemblyScriptFile, zygardeAssemblyScript.getBytes());
        writeGARC(romEntry.getFile("Scripts"), scriptGarc);

        int speciesOffset = find(code, Gen7Constants.zygardeAssemblySpeciesPrefix);
        int formeOffset = find(code, Gen7Constants.zygardeAssemblyFormePrefix);
        if (speciesOffset > 0 && formeOffset > 0) {
            speciesOffset += Gen7Constants.zygardeAssemblySpeciesPrefix.length() / 2; // because it was a prefix
            formeOffset += Gen7Constants.zygardeAssemblyFormePrefix.length() / 2; // because it was a prefix
            FileFunctions.write2ByteInt(code, speciesOffset, se.pkmn.getBaseNumber());

            // Just write "mov r0, #forme" to where the game originally loaded the forme.
            code[formeOffset] = (byte) se.forme;
            code[formeOffset + 1] = 0x00;
            code[formeOffset + 2] = (byte) 0xA0;
            code[formeOffset + 3] = (byte) 0xE3;
        }
    }

    @Override
    public int miscTweaksAvailable() {
        int available = 0;
        available |= MiscTweak.FASTEST_TEXT.getValue();
        available |= MiscTweak.BAN_LUCKY_EGG.getValue();
        available |= MiscTweak.SOS_BATTLES_FOR_ALL.getValue();
        available |= MiscTweak.RETAIN_ALT_FORMES.getValue();
        return available;
    }

    @Override
    public void applyMiscTweak(MiscTweak tweak) {
        if (tweak == MiscTweak.FASTEST_TEXT) {
            applyFastestText();
        }
        if (tweak == MiscTweak.BAN_LUCKY_EGG) {
            allowedItems.banSingles(Items.luckyEgg);
            nonBadItems.banSingles(Items.luckyEgg);
        }
        if (tweak == MiscTweak.SOS_BATTLES_FOR_ALL) {
            positiveCallRates();
        }
        if (tweak == MiscTweak.RETAIN_ALT_FORMES) {
            try {
                patchFormeReversion();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isEffectivenessUpdated() {
        return false;
    }

    private void applyFastestText() {
        int offset = find(code, Gen7Constants.fastestTextPrefixes[0]);
        if (offset > 0) {
            offset += Gen7Constants.fastestTextPrefixes[0].length() / 2; // because it was a prefix
            code[offset] = 0x03;
            code[offset + 1] = 0x40;
            code[offset + 2] = (byte) 0xA0;
            code[offset + 3] = (byte) 0xE3;
        }
        offset = find(code, Gen7Constants.fastestTextPrefixes[1]);
        if (offset > 0) {
            offset += Gen7Constants.fastestTextPrefixes[1].length() / 2; // because it was a prefix
            code[offset] = 0x03;
            code[offset + 1] = 0x50;
            code[offset + 2] = (byte) 0xA0;
            code[offset + 3] = (byte) 0xE3;
        }
    }

    private void positiveCallRates() {
        for (Pokemon pk: pokes) {
            if (pk == null) continue;
            if (pk.callRate <= 0) {
                pk.callRate = 5;
            }
        }
    }

    @Override
    public List<Integer> getTMMoves() {
        String tmDataPrefix = Gen7Constants.getTmDataPrefix(romEntry.romType);
        int offset = find(code, tmDataPrefix);
        if (offset != 0) {
            offset += tmDataPrefix.length() / 2; // because it was a prefix
            List<Integer> tms = new ArrayList<>();
            for (int i = 0; i < Gen7Constants.tmCount; i++) {
                tms.add(readWord(code, offset + i * 2));
            }
            return tms;
        } else {
            return null;
        }
    }

    @Override
    public List<Integer> getHMMoves() {
        // Gen 7 does not have any HMs
        return new ArrayList<>();
    }

    @Override
    public void setTMMoves(List<Integer> moveIndexes) {
        String tmDataPrefix = Gen7Constants.getTmDataPrefix(romEntry.romType);
        int offset = find(code, tmDataPrefix);
        if (offset > 0) {
            offset += tmDataPrefix.length() / 2; // because it was a prefix
            for (int i = 0; i < Gen7Constants.tmCount; i++) {
                writeWord(code, offset + i * 2, moveIndexes.get(i));
            }

            // Update TM item descriptions
            List<String> itemDescriptions = getStrings(false, romEntry.getInt("ItemDescriptionsTextOffset"));
            List<String> moveDescriptions = getStrings(false, romEntry.getInt("MoveDescriptionsTextOffset"));
            // TM01 is item 328 and so on
            for (int i = 0; i < Gen7Constants.tmBlockOneCount; i++) {
                itemDescriptions.set(i + Gen7Constants.tmBlockOneOffset, moveDescriptions.get(moveIndexes.get(i)));
            }
            // TM93-95 are 618-620
            for (int i = 0; i < Gen7Constants.tmBlockTwoCount; i++) {
                itemDescriptions.set(i + Gen7Constants.tmBlockTwoOffset,
                        moveDescriptions.get(moveIndexes.get(i + Gen7Constants.tmBlockOneCount)));
            }
            // TM96-100 are 690 and so on
            for (int i = 0; i < Gen7Constants.tmBlockThreeCount; i++) {
                itemDescriptions.set(i + Gen7Constants.tmBlockThreeOffset,
                        moveDescriptions.get(moveIndexes.get(i + Gen7Constants.tmBlockOneCount + Gen7Constants.tmBlockTwoCount)));
            }
            // Save the new item descriptions
            setStrings(false, romEntry.getInt("ItemDescriptionsTextOffset"), itemDescriptions);
            // Palettes
            String palettePrefix = Gen7Constants.itemPalettesPrefix;
            int offsPals = find(code, palettePrefix);
            if (offsPals > 0) {
                offsPals += Gen7Constants.itemPalettesPrefix.length() / 2; // because it was a prefix
                // Write pals
                for (int i = 0; i < Gen7Constants.tmBlockOneCount; i++) {
                    int itmNum = Gen7Constants.tmBlockOneOffset + i;
                    Move m = this.moves[moveIndexes.get(i)];
                    int pal = this.typeTMPaletteNumber(m.type, true);
                    writeWord(code, offsPals + itmNum * 4, pal);
                }
                for (int i = 0; i < (Gen7Constants.tmBlockTwoCount); i++) {
                    int itmNum = Gen7Constants.tmBlockTwoOffset + i;
                    Move m = this.moves[moveIndexes.get(i + Gen7Constants.tmBlockOneCount)];
                    int pal = this.typeTMPaletteNumber(m.type, true);
                    writeWord(code, offsPals + itmNum * 4, pal);
                }
                for (int i = 0; i < (Gen7Constants.tmBlockThreeCount); i++) {
                    int itmNum = Gen7Constants.tmBlockThreeOffset + i;
                    Move m = this.moves[moveIndexes.get(i + Gen7Constants.tmBlockOneCount + Gen7Constants.tmBlockTwoCount)];
                    int pal = this.typeTMPaletteNumber(m.type, true);
                    writeWord(code, offsPals + itmNum * 4, pal);
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

    @Override
    public int getTMCount() {
        return Gen7Constants.tmCount;
    }

    @Override
    public int getHMCount() {
        // Gen 7 does not have any HMs
        return 0;
    }

    @Override
    public Map<Pokemon, boolean[]> getTMHMCompatibility() {
        Map<Pokemon, boolean[]> compat = new TreeMap<>();
        int pokemonCount = Gen7Constants.getPokemonCount(romEntry.romType);
        int formeCount = Gen7Constants.getFormeCount(romEntry.romType);
        for (int i = 1; i <= pokemonCount + formeCount; i++) {
            byte[] data;
            data = pokeGarc.files.get(i).get(0);
            Pokemon pkmn = pokes[i];
            boolean[] flags = new boolean[Gen7Constants.tmCount + 1];
            for (int j = 0; j < 13; j++) {
                readByteIntoFlags(data, flags, j * 8 + 1, Gen7Constants.bsTMHMCompatOffset + j);
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
            byte[] data = pokeGarc.files.get(pkmn.number).get(0);
            for (int j = 0; j < 13; j++) {
                data[Gen7Constants.bsTMHMCompatOffset + j] = getByteFromFlags(flags, j * 8 + 1);
            }
        }
    }

    @Override
    public boolean hasMoveTutors() {
        return romEntry.romType == Gen7Constants.Type_USUM;
    }

    @Override
    public List<Integer> getMoveTutorMoves() {
        List<Integer> mtMoves = new ArrayList<>();

        int mtOffset = find(code, Gen7Constants.tutorsPrefix);
        if (mtOffset > 0) {
            mtOffset += Gen7Constants.tutorsPrefix.length() / 2;
            int val = 0;
            while (val != 0xFFFF) {
                val = FileFunctions.read2ByteInt(code, mtOffset);
                mtOffset += 2;
                if (val == 0xFFFF) continue;
                mtMoves.add(val);
            }
        }

        return mtMoves;
    }

    @Override
    public void setMoveTutorMoves(List<Integer> moves) {
        int mtOffset = find(code, Gen7Constants.tutorsPrefix);
        if (mtOffset > 0) {
            mtOffset += Gen7Constants.tutorsPrefix.length() / 2;
            for (int move: moves) {
                FileFunctions.write2ByteInt(code,mtOffset, move);
                mtOffset += 2;
            }
        }

        try {
            byte[] tutorCRO = readFile(romEntry.getFile("ShopsAndTutors"));
            for (int i = 0; i < moves.size(); i++) {
                int offset = Gen7Constants.tutorsOffset + i * 4;
                FileFunctions.write2ByteInt(tutorCRO, offset, moves.get(i));
            }
            writeFile(romEntry.getFile("ShopsAndTutors"), tutorCRO);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
        Map<Pokemon, boolean[]> compat = new TreeMap<>();
        int pokemonCount = Gen7Constants.getPokemonCount(romEntry.romType);
        int formeCount = Gen7Constants.getFormeCount(romEntry.romType);
        for (int i = 1; i <= pokemonCount + formeCount; i++) {
            byte[] data;
            data = pokeGarc.files.get(i).get(0);
            Pokemon pkmn = pokes[i];
            boolean[] flags = new boolean[Gen7Constants.tutorMoveCount + 1];
            for (int j = 0; j < 10; j++) {
                readByteIntoFlags(data, flags, j * 8 + 1, Gen7Constants.bsMTCompatOffset + j);
            }
            compat.put(pkmn, flags);
        }
        return compat;
    }

    @Override
    public void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData) {
        if (!hasMoveTutors()) return;
        int pokemonCount = Gen7Constants.getPokemonCount(romEntry.romType);
        int formeCount = Gen7Constants.getFormeCount(romEntry.romType);
        for (int i = 1; i <= pokemonCount + formeCount; i++) {
            byte[] data;
            data = pokeGarc.files.get(i).get(0);
            Pokemon pkmn = pokes[i];
            boolean[] flags = compatData.get(pkmn);
            for (int j = 0; j < 10; j++) {
                data[Gen7Constants.bsMTCompatOffset + j] = getByteFromFlags(flags, j * 8 + 1);
            }
        }
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
        return "Complete";
    }

    @Override
    public boolean hasTimeBasedEncounters() {
        return true;
    }

    @Override
    public List<Integer> getMovesBannedFromLevelup() {
        return Gen7Constants.bannedMoves;
    }

    @Override
    public boolean hasWildAltFormes() {
        return true;
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
                            extraEntry.forme = evo.forme;
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
                    // TBD: Pancham, Sliggoo? Sylveon?
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

        for (Pokemon pkmn : pokes) {
            if (pkmn != null) {
                Evolution extraEntry = null;
                for (Evolution evo : pkmn.evolutionsFrom) {
                    if (wildsRandomized) {
                        if (evo.type == EvolutionType.LEVEL_WITH_OTHER) {
                            // Replace w/ level 35
                            evo.type = EvolutionType.LEVEL;
                            evo.extraInfo = 35;
                            addEvoUpdateCondensed(easierEvolutionUpdates, evo, false);
                        }
                    }
                    if (romEntry.romType == Gen7Constants.Type_SM) {
                        if (evo.type == EvolutionType.LEVEL_SNOWY) {
                            extraEntry = new Evolution(evo.from, evo.to, true,
                                    EvolutionType.LEVEL, 35);
                            extraEntry.forme = evo.forme;
                            addEvoUpdateCondensed(easierEvolutionUpdates, extraEntry, true);
                        } else if (evo.type == EvolutionType.LEVEL_ELECTRIFIED_AREA) {
                            extraEntry = new Evolution(evo.from, evo.to, true,
                                    EvolutionType.LEVEL, 35);
                            extraEntry.forme = evo.forme;
                            addEvoUpdateCondensed(easierEvolutionUpdates, extraEntry, true);
                        }
                    }
                }
                if (extraEntry != null) {
                    pkmn.evolutionsFrom.add(extraEntry);
                    extraEntry.to.evolutionsTo.add(extraEntry);
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
                            extraEntry.forme = evo.forme;
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
                            extraEntry.forme = evo.forme;
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
                            extraEntry.forme = evo.forme;
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
                            extraEntry.forme = evo.forme;
                            extraEvolutions.add(extraEntry);
                        }
                    } else if (evo.type == EvolutionType.LEVEL_DAY) {
                        if (evo.from.number == Species.rockruff) {
                            // We can't set Rockruff to evolve into Lycanroc-Midday with level at night because that's how
                            // Lycanroc-Midnight works in the original game. Instead, make Rockruff: == sun stone => Lycanroc-Midday
                            evo.type = EvolutionType.STONE;
                            evo.extraInfo = Items.sunStone;
                            addEvoUpdateStone(timeBasedEvolutionUpdates, evo, itemNames.get(evo.extraInfo));
                        } else {
                            addEvoUpdateLevel(timeBasedEvolutionUpdates, evo);
                            evo.type = EvolutionType.LEVEL;
                        }
                    } else if (evo.type == EvolutionType.LEVEL_NIGHT) {
                        if (evo.from.number == Species.rockruff) {
                            // We can't set Rockruff to evolve into Lycanroc-Midnight with level at night because that's how
                            // Lycanroc-Midday works in the original game. Instead, make Rockruff: == moon stone => Lycanroc-Midnight
                            evo.type = EvolutionType.STONE;
                            evo.extraInfo = Items.moonStone;
                            addEvoUpdateStone(timeBasedEvolutionUpdates, evo, itemNames.get(evo.extraInfo));
                        } else {
                            addEvoUpdateLevel(timeBasedEvolutionUpdates, evo);
                            evo.type = EvolutionType.LEVEL;
                        }
                    } else if (evo.type == EvolutionType.LEVEL_DUSK) {
                        // This is the Rockruff => Lycanroc-Dusk evolution. We can't set it to evolve with level at other
                        // times because the other Lycanroc formes work like that in the original game. Instead, make
                        // Rockruff: == dusk stone => Lycanroc-Dusk
                        evo.type = EvolutionType.STONE;
                        evo.extraInfo = Items.duskStone;
                        addEvoUpdateStone(timeBasedEvolutionUpdates, evo, itemNames.get(evo.extraInfo));
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
    public boolean altFormesCanHaveDifferentEvolutions() {
        return true;
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

        return tnames;
    }

    @Override
    public int maxTrainerNameLength() {
        return 10;
    }

    @Override
    public void setTrainerNames(List<String> trainerNames) {
        List<String> tnames = getStrings(false, romEntry.getInt("TrainerNamesTextOffset"));
        List<String> newTNames = new ArrayList<>(trainerNames);
        newTNames.add(0, tnames.get(0)); // the 0-entry, preserve it
        setStrings(false, romEntry.getInt("TrainerNamesTextOffset"), newTNames);
        try {
            writeStringsForAllLanguages(newTNames, romEntry.getInt("TrainerNamesTextOffset"));
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void writeStringsForAllLanguages(List<String> strings, int index) throws IOException {
        List<String> nonEnglishLanguages = Arrays.asList("JaKana", "JaKanji", "Fr", "It", "De", "Es", "Ko", "ZhSimplified", "ZhTraditional");
        for (String nonEnglishLanguage : nonEnglishLanguages) {
            String key = "TextStrings" + nonEnglishLanguage;
            GARCArchive stringsGarcForLanguage = readGARC(romEntry.getFile(key),true);
            setStrings(stringsGarcForLanguage, index, strings);
            writeGARC(romEntry.getFile(key), stringsGarcForLanguage);
        }
    }

    @Override
    public TrainerNameMode trainerNameMode() {
        return TrainerNameMode.MAX_LENGTH;
    }

    @Override
    public List<Integer> getTCNameLengthsByTrainer() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getTrainerClassNames() {
        return getStrings(false, romEntry.getInt("TrainerClassesTextOffset"));
    }

    @Override
    public void setTrainerClassNames(List<String> trainerClassNames) {
        setStrings(false, romEntry.getInt("TrainerClassesTextOffset"), trainerClassNames);
        try {
            writeStringsForAllLanguages(trainerClassNames, romEntry.getInt("TrainerClassesTextOffset"));
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public int maxTrainerClassNameLength() {
        return 15;
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
        return "cxi";
    }

    @Override
    public int abilitiesPerPokemon() {
        return 3;
    }

    @Override
    public int highestAbilityIndex() {
        return Gen7Constants.getHighestAbilityIndex(romEntry.romType);
    }

    @Override
    public int internalStringLength(String string) {
        return string.length();
    }

    @Override
    public void randomizeIntroPokemon() {
        // For now, do nothing.
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
        return Gen7Constants.getRegularShopItems(romEntry.romType);
    }

    @Override
    public List<Integer> getOPShopItems() {
        return Gen7Constants.opShopItems;
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
        return Gen7Constants.abilityVariations;
    }

    @Override
    public List<Integer> getUselessAbilities() {
        return new ArrayList<>(Gen7Constants.uselessAbilities);
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

        List<Integer> abilityList = Arrays.asList(tp.pokemon.ability1, tp.pokemon.ability2, tp.pokemon.ability3);
        return abilityList.get(tp.abilitySlot - 1);
    }

    @Override
    public boolean hasMegaEvolutions() {
        return true;
    }

    private int tmFromIndex(int index) {

        if (index >= Gen7Constants.tmBlockOneOffset
                && index < Gen7Constants.tmBlockOneOffset + Gen7Constants.tmBlockOneCount) {
            return index - (Gen7Constants.tmBlockOneOffset - 1);
        } else if (index >= Gen7Constants.tmBlockTwoOffset
                && index < Gen7Constants.tmBlockTwoOffset + Gen7Constants.tmBlockTwoCount) {
            return (index + Gen7Constants.tmBlockOneCount) - (Gen7Constants.tmBlockTwoOffset - 1);
        } else {
            return (index + Gen7Constants.tmBlockOneCount + Gen7Constants.tmBlockTwoCount) - (Gen7Constants.tmBlockThreeOffset - 1);
        }
    }

    private int indexFromTM(int tm) {
        if (tm >= 1 && tm <= Gen7Constants.tmBlockOneCount) {
            return tm + (Gen7Constants.tmBlockOneOffset - 1);
        } else if (tm > Gen7Constants.tmBlockOneCount && tm <= Gen7Constants.tmBlockOneCount + Gen7Constants.tmBlockTwoCount) {
            return tm + (Gen7Constants.tmBlockTwoOffset - 1 - Gen7Constants.tmBlockOneCount);
        } else {
            return tm + (Gen7Constants.tmBlockThreeOffset - 1 - (Gen7Constants.tmBlockOneCount + Gen7Constants.tmBlockTwoCount));
        }
    }

    @Override
    public List<Integer> getCurrentFieldTMs() {
        List<Integer> fieldItems = this.getFieldItems();
        List<Integer> fieldTMs = new ArrayList<>();

        ItemList allowedItems = Gen7Constants.getAllowedItems(romEntry.romType);
        for (int item : fieldItems) {
            if (allowedItems.isTM(item)) {
                fieldTMs.add(tmFromIndex(item));
            }
        }

        return fieldTMs.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public void setFieldTMs(List<Integer> fieldTMs) {
        List<Integer> fieldItems = this.getFieldItems();
        int fiLength = fieldItems.size();
        Iterator<Integer> iterTMs = fieldTMs.iterator();
        Map<Integer,Integer> tmMap = new HashMap<>();

        ItemList allowedItems = Gen7Constants.getAllowedItems(romEntry.romType);
        for (int i = 0; i < fiLength; i++) {
            int oldItem = fieldItems.get(i);
            if (allowedItems.isTM(oldItem)) {
                if (tmMap.get(oldItem) != null) {
                    fieldItems.set(i,tmMap.get(oldItem));
                    continue;
                }
                int newItem = indexFromTM(iterTMs.next());
                fieldItems.set(i, newItem);
                tmMap.put(oldItem,newItem);
            }
        }

        this.setFieldItems(fieldItems);
    }

    @Override
    public List<Integer> getRegularFieldItems() {
        List<Integer> fieldItems = this.getFieldItems();
        List<Integer> fieldRegItems = new ArrayList<>();

        ItemList allowedItems = Gen7Constants.getAllowedItems(romEntry.romType);
        for (int item : fieldItems) {
            if (allowedItems.isAllowed(item) && !(allowedItems.isTM(item))) {
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

        ItemList allowedItems = Gen7Constants.getAllowedItems(romEntry.romType);
        for (int i = 0; i < fiLength; i++) {
            int oldItem = fieldItems.get(i);
            if (!(allowedItems.isTM(oldItem)) && allowedItems.isAllowed(oldItem)) {
                int newItem = iterNewItems.next();
                fieldItems.set(i, newItem);
            }
        }

        this.setFieldItems(fieldItems);
    }

    @Override
    public List<Integer> getRequiredFieldTMs() {
        return Gen7Constants.getRequiredFieldTMs(romEntry.romType);
    }

    public List<Integer> getFieldItems() {
        List<Integer> fieldItems = new ArrayList<>();
        int numberOfAreas = encounterGarc.files.size() / 11;
        for (int i = 0; i < numberOfAreas; i++) {
            byte[][] environmentData = Mini.UnpackMini(encounterGarc.getFile(i * 11),"ED");
            if (environmentData == null) continue;

            byte[][] itemDataFull = Mini.UnpackMini(environmentData[10],"EI");

            byte[][] berryPileDataFull = Mini.UnpackMini(environmentData[11],"EB");

            // Field/hidden items
            for (byte[] itemData: itemDataFull) {
                if (itemData.length > 0) {
                    int itemCount = itemData[0];

                    for (int j = 0; j < itemCount; j++) {
                        fieldItems.add(FileFunctions.read2ByteInt(itemData,(j * 64) + 52));
                    }
                }
            }

            // Berry piles
            for (byte[] berryPileData: berryPileDataFull) {
                if (berryPileData.length > 0) {
                    int pileCount = berryPileData[0];
                    for (int j = 0; j < pileCount; j++) {
                        for (int k = 0; k < 7; k++) {
                            fieldItems.add(FileFunctions.read2ByteInt(berryPileData,4 + j*68 + 54 + k*2));
                        }
                    }
                }
            }
        }
        return fieldItems;
    }

    public void setFieldItems(List<Integer> items) {
        try {
            int numberOfAreas = encounterGarc.files.size() / 11;
            Iterator<Integer> iterItems = items.iterator();
            for (int i = 0; i < numberOfAreas; i++) {
                byte[][] environmentData = Mini.UnpackMini(encounterGarc.getFile(i * 11),"ED");
                if (environmentData == null) continue;

                byte[][] itemDataFull = Mini.UnpackMini(environmentData[10],"EI");

                byte[][] berryPileDataFull = Mini.UnpackMini(environmentData[11],"EB");

                // Field/hidden items
                for (byte[] itemData: itemDataFull) {
                    if (itemData.length > 0) {
                        int itemCount = itemData[0];

                        for (int j = 0; j < itemCount; j++) {
                            FileFunctions.write2ByteInt(itemData,(j * 64) + 52,iterItems.next());
                        }
                    }
                }

                byte[] itemDataPacked = Mini.PackMini(itemDataFull,"EI");
                environmentData[10] = itemDataPacked;

                // Berry piles
                for (byte[] berryPileData: berryPileDataFull) {
                    if (berryPileData.length > 0) {
                        int pileCount = berryPileData[0];

                        for (int j = 0; j < pileCount; j++) {
                            for (int k = 0; k < 7; k++) {
                                FileFunctions.write2ByteInt(berryPileData,4 + j*68 + 54 + k*2,iterItems.next());
                            }
                        }
                    }
                }

                byte[] berryPileDataPacked = Mini.PackMini(berryPileDataFull,"EB");
                environmentData[11] = berryPileDataPacked;

                encounterGarc.setFile(i * 11, Mini.PackMini(environmentData,"ED"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<IngameTrade> getIngameTrades() {
        List<IngameTrade> ingameTrades = new ArrayList<>();
        try {
            GARCArchive staticGarc = readGARC(romEntry.getFile("StaticPokemon"), true);
            List<String> tradeStrings = getStrings(true, romEntry.getInt("IngameTradesTextOffset"));
            byte[] tradesFile = staticGarc.files.get(4).get(0);
            int numberOfIngameTrades = tradesFile.length / 0x34;
            for (int i = 0; i < numberOfIngameTrades; i++) {
                int offset = i * 0x34;
                IngameTrade trade = new IngameTrade();
                int givenSpecies = FileFunctions.read2ByteInt(tradesFile, offset);
                int requestedSpecies = FileFunctions.read2ByteInt(tradesFile, offset + 0x2C);
                Pokemon givenPokemon = pokes[givenSpecies];
                Pokemon requestedPokemon = pokes[requestedSpecies];
                int forme = tradesFile[offset + 4];
                if (forme > givenPokemon.cosmeticForms && forme != 30 && forme != 31) {
                    int speciesWithForme = absolutePokeNumByBaseForme
                            .getOrDefault(givenSpecies, dummyAbsolutePokeNums)
                            .getOrDefault(forme, 0);
                    givenPokemon = pokes[speciesWithForme];
                }
                trade.givenPokemon = givenPokemon;
                trade.requestedPokemon = requestedPokemon;
                trade.nickname = tradeStrings.get(FileFunctions.read2ByteInt(tradesFile, offset + 2));
                trade.otName = tradeStrings.get(FileFunctions.read2ByteInt(tradesFile, offset + 0x18));
                trade.otId = FileFunctions.readFullInt(tradesFile, offset + 0x10);
                trade.ivs = new int[6];
                for (int iv = 0; iv < 6; iv++) {
                    trade.ivs[iv] = tradesFile[offset + 6 + iv];
                }
                trade.item = FileFunctions.read2ByteInt(tradesFile, offset + 0x14);
                if (trade.item < 0) {
                    trade.item = 0;
                }
                ingameTrades.add(trade);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return ingameTrades;
    }

    @Override
    public void setIngameTrades(List<IngameTrade> trades) {
        try {
            List<IngameTrade> oldTrades = this.getIngameTrades();
            GARCArchive staticGarc = readGARC(romEntry.getFile("StaticPokemon"), true);
            List<String> tradeStrings = getStrings(true, romEntry.getInt("IngameTradesTextOffset"));
            Map<Integer, List<Integer>> hardcodedTradeTextOffsets = Gen7Constants.getHardcodedTradeTextOffsets(romEntry.romType);
            byte[] tradesFile = staticGarc.files.get(4).get(0);
            int numberOfIngameTrades = tradesFile.length / 0x34;
            for (int i = 0; i < numberOfIngameTrades; i++) {
                IngameTrade trade = trades.get(i);
                int offset = i * 0x34;
                Pokemon givenPokemon = trade.givenPokemon;
                int forme = 0;
                if (givenPokemon.formeNumber > 0) {
                    forme = givenPokemon.formeNumber;
                    givenPokemon = givenPokemon.baseForme;
                }
                FileFunctions.write2ByteInt(tradesFile, offset, givenPokemon.number);
                tradesFile[offset + 4] = (byte) forme;
                FileFunctions.write2ByteInt(tradesFile, offset + 0x2C, trade.requestedPokemon.number);
                tradeStrings.set(FileFunctions.read2ByteInt(tradesFile, offset + 2), trade.nickname);
                tradeStrings.set(FileFunctions.read2ByteInt(tradesFile, offset + 0x18), trade.otName);
                FileFunctions.writeFullInt(tradesFile, offset + 0x10, trade.otId);
                for (int iv = 0; iv < 6; iv++) {
                    tradesFile[offset + 6 + iv] = (byte) trade.ivs[iv];
                }
                FileFunctions.write2ByteInt(tradesFile, offset + 0x14, trade.item);

                List<Integer> hardcodedTextOffsetsForThisTrade = hardcodedTradeTextOffsets.get(i);
                if (hardcodedTextOffsetsForThisTrade != null) {
                    updateHardcodedTradeText(oldTrades.get(i), trade, tradeStrings, hardcodedTextOffsetsForThisTrade);
                }
            }
            writeGARC(romEntry.getFile("StaticPokemon"), staticGarc);
            setStrings(true, romEntry.getInt("IngameTradesTextOffset"), tradeStrings);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    // NOTE: This method is kind of stupid, in that it doesn't try to reflow the text to better fit; it just
    // blindly replaces the Pokemon's name. However, it seems to work well enough for what we need.
    private void updateHardcodedTradeText(IngameTrade oldTrade, IngameTrade newTrade, List<String> tradeStrings, List<Integer> hardcodedTextOffsets) {
        for (int offset : hardcodedTextOffsets) {
            String hardcodedText = tradeStrings.get(offset);
            String oldRequestedName = oldTrade.requestedPokemon.name;
            String oldGivenName = oldTrade.givenPokemon.name;
            String newRequestedName = newTrade.requestedPokemon.name;
            String newGivenName = newTrade.givenPokemon.name;
            hardcodedText = hardcodedText.replace(oldRequestedName, newRequestedName);
            hardcodedText = hardcodedText.replace(oldGivenName, newGivenName);
            tradeStrings.set(offset, hardcodedText);
        }
    }

    @Override
    public boolean hasDVs() {
        return false;
    }

    @Override
    public int generationOfPokemon() {
        return 7;
    }

    @Override
    public void removeEvosForPokemonPool() {
        // slightly more complicated than gen2/3
        // we have to update a "baby table" too
        List<Pokemon> pokemonIncluded = this.mainPokemonListInclFormes;
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
            // baby pokemon
            GARCArchive babyGarc = readGARC(romEntry.getFile("BabyPokemon"), true);
            int pokemonCount = Gen7Constants.getPokemonCount(romEntry.romType);
            byte[] masterFile = babyGarc.getFile(pokemonCount + 1);
            for (int i = 1; i <= pokemonCount; i++) {
                byte[] babyFile = babyGarc.getFile(i);
                Pokemon baby = pokes[i];
                while (baby.evolutionsTo.size() > 0) {
                    // Grab the first "to evolution" even if there are multiple
                    baby = baby.evolutionsTo.get(0).from;
                }
                writeWord(babyFile, 0, baby.number);
                writeWord(masterFile, i * 2, baby.number);
                babyGarc.setFile(i, babyFile);
            }
            babyGarc.setFile(pokemonCount + 1, masterFile);
            writeGARC(romEntry.getFile("BabyPokemon"), babyGarc);
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
        // Gen 7 does not have field moves
        return new ArrayList<>();
    }

    @Override
    public List<Integer> getEarlyRequiredHMMoves() {
        // Gen 7 does not have any HMs
        return new ArrayList<>();
    }

    @Override
    public Map<Integer, Shop> getShopItems() {
        int[] tmShops = romEntry.arrayEntries.get("TMShops");
        int[] regularShops = romEntry.arrayEntries.get("RegularShops");
        int[] shopItemSizes = romEntry.arrayEntries.get("ShopItemSizes");
        int shopCount = romEntry.getInt("ShopCount");
        Map<Integer, Shop> shopItemsMap = new TreeMap<>();
        try {
            byte[] shopsCRO = readFile(romEntry.getFile("ShopsAndTutors"));
            int offset = Gen7Constants.getShopItemsOffset(romEntry.romType);
            for (int i = 0; i < shopCount; i++) {
                boolean badShop = false;
                for (int tmShop : tmShops) {
                    if (i == tmShop) {
                        badShop = true;
                        offset += (shopItemSizes[i] * 2);
                        break;
                    }
                }
                for (int regularShop : regularShops) {
                    if (badShop) break;
                    if (i == regularShop) {
                        badShop = true;
                        offset += (shopItemSizes[i] * 2);
                        break;
                    }
                }
                if (!badShop) {
                    List<Integer> items = new ArrayList<>();
                    for (int j = 0; j < shopItemSizes[i]; j++) {
                        items.add(FileFunctions.read2ByteInt(shopsCRO, offset));
                        offset += 2;
                    }
                    Shop shop = new Shop();
                    shop.items = items;
                    shop.name = shopNames.get(i);
                    shop.isMainGame = Gen7Constants.getMainGameShops(romEntry.romType).contains(i);
                    shopItemsMap.put(i, shop);
                }
            }
            return shopItemsMap;
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public void setShopItems(Map<Integer, Shop> shopItems) {
        int[] tmShops = romEntry.arrayEntries.get("TMShops");
        int[] regularShops = romEntry.arrayEntries.get("RegularShops");
        int[] shopItemSizes = romEntry.arrayEntries.get("ShopItemSizes");
        int shopCount = romEntry.getInt("ShopCount");
        try {
            byte[] shopsCRO = readFile(romEntry.getFile("ShopsAndTutors"));
            int offset = Gen7Constants.getShopItemsOffset(romEntry.romType);
            for (int i = 0; i < shopCount; i++) {
                boolean badShop = false;
                for (int tmShop : tmShops) {
                    if (i == tmShop) {
                        badShop = true;
                        offset += (shopItemSizes[i] * 2);
                        break;
                    }
                }
                for (int regularShop : regularShops) {
                    if (badShop) break;
                    if (i == regularShop) {
                        badShop = true;
                        offset += (shopItemSizes[i] * 2);
                        break;
                    }
                }
                if (!badShop) {
                    List<Integer> shopContents = shopItems.get(i).items;
                    Iterator<Integer> iterItems = shopContents.iterator();
                    for (int j = 0; j < shopItemSizes[i]; j++) {
                        Integer item = iterItems.next();
                        FileFunctions.write2ByteInt(shopsCRO, offset, item);
                        offset += 2;
                    }
                }
            }
            writeFile(romEntry.getFile("ShopsAndTutors"), shopsCRO);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public void setShopPrices() {
        try {
            GARCArchive itemPriceGarc = this.readGARC(romEntry.getFile("ItemData"),true);
            for (int i = 1; i < itemPriceGarc.files.size(); i++) {
                writeWord(itemPriceGarc.files.get(i).get(0),0, Gen7Constants.balancedItemPrices.get(i));
            }
            writeGARC(romEntry.getFile("ItemData"),itemPriceGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public List<PickupItem> getPickupItems() {
        List<PickupItem> pickupItems = new ArrayList<>();
        try {
            GARCArchive pickupGarc = this.readGARC(romEntry.getFile("PickupData"), false);
            byte[] pickupData = pickupGarc.getFile(0);
            int numberOfPickupItems = FileFunctions.readFullInt(pickupData, 0) - 1; // GameFreak why???
            for (int i = 0; i < numberOfPickupItems; i++) {
                int offset = 4 + (i * 0xC);
                int item = FileFunctions.read2ByteInt(pickupData, offset);
                PickupItem pickupItem = new PickupItem(item);
                for (int levelRange = 0; levelRange < 10; levelRange++) {
                    pickupItem.probabilities[levelRange] = pickupData[offset + levelRange + 2];
                }
                pickupItems.add(pickupItem);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return pickupItems;
    }

    @Override
    public void setPickupItems(List<PickupItem> pickupItems) {
        try {
            GARCArchive pickupGarc = this.readGARC(romEntry.getFile("PickupData"), false);
            byte[] pickupData = pickupGarc.getFile(0);
            for (int i = 0; i < pickupItems.size(); i++) {
                int offset = 4 + (i * 0xC);
                int item = pickupItems.get(i).item;
                FileFunctions.write2ByteInt(pickupData, offset, item);
            }
            this.writeGARC(romEntry.getFile("PickupData"), pickupGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void computeCRC32sForRom() throws IOException {
        this.actualFileCRC32s = new HashMap<>();
        this.actualCodeCRC32 = FileFunctions.getCRC32(code);
        for (String fileKey : romEntry.files.keySet()) {
            byte[] file = readFile(romEntry.getFile(fileKey));
            long crc32 = FileFunctions.getCRC32(file);
            this.actualFileCRC32s.put(fileKey, crc32);
        }
    }

    @Override
    public boolean isRomValid() {
        int index = this.hasGameUpdateLoaded() ? 1 : 0;
        if (romEntry.expectedCodeCRC32s[index] != actualCodeCRC32) {
            return false;
        }

        for (String fileKey : romEntry.files.keySet()) {
            long expectedCRC32 = romEntry.files.get(fileKey).expectedCRC32s[index];
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
            GARCArchive pokespritesGARC = this.readGARC(romEntry.getFile("PokemonGraphics"), false);
            int pkIndex = this.random.nextInt(pokespritesGARC.files.size() - 1) + 1;
            if (romEntry.romType == Gen7Constants.Type_SM) {
                while (pkIndex == 1109 || pkIndex == 1117) {
                    pkIndex = this.random.nextInt(pokespritesGARC.files.size() - 1) + 1;
                }
            }
            byte[] iconBytes = pokespritesGARC.files.get(pkIndex).get(0);
            BFLIM icon = new BFLIM(iconBytes);
            return icon.getImage();
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private class ZoneData {
        public int worldIndex;
        public int areaIndex;
        public int parentMap;
        public String locationName;
        private byte[] data;

        public static final int size = 0x54;

        public ZoneData(byte[] zoneDataBytes, int index) {
            data = new byte[size];
            System.arraycopy(zoneDataBytes, index * size, data, 0, size);
            parentMap = FileFunctions.readFullInt(data, 0x1C);
        }
    }

    private class AreaData {
        public int fileNumber;
        public boolean hasTables;
        public List<byte[]> encounterTables;
        public List<ZoneData> zones;
        public String name;

        public AreaData() {
            encounterTables = new ArrayList<>();
        }
    }

    @Override
    public List<Integer> getAllConsumableHeldItems() {
        return Gen7Constants.consumableHeldItems;
    }

    @Override
    public List<Integer> getAllHeldItems() {
        return Gen7Constants.allHeldItems;
    }

    @Override
    public boolean hasRivalFinalBattle() {
        return true;
    }

    @Override
    public List<Integer> getSensibleHeldItemsFor(TrainerPokemon tp, boolean consumableOnly, List<Move> moves, int[] pokeMoves) {
        List<Integer> items = new ArrayList<>();
        items.addAll(Gen7Constants.generalPurposeConsumableItems);
        int frequencyBoostCount = 6; // Make some very good items more common, but not too common
        if (!consumableOnly) {
            frequencyBoostCount = 8; // bigger to account for larger item pool.
            items.addAll(Gen7Constants.generalPurposeItems);
        }
        int numDamagingMoves = 0;
        for (int moveIdx : pokeMoves) {
            Move move = moves.get(moveIdx);
            if (move == null) {
                continue;
            }
            if (move.category == MoveCategory.PHYSICAL) {
                numDamagingMoves++;
                items.add(Items.liechiBerry);
                items.add(Gen7Constants.consumableTypeBoostingItems.get(move.type));
                if (!consumableOnly) {
                    items.addAll(Gen7Constants.typeBoostingItems.get(move.type));
                    items.add(Items.choiceBand);
                    items.add(Items.muscleBand);
                }
            }
            if (move.category == MoveCategory.SPECIAL) {
                numDamagingMoves++;
                items.add(Items.petayaBerry);
                items.add(Gen7Constants.consumableTypeBoostingItems.get(move.type));
                if (!consumableOnly) {
                    items.addAll(Gen7Constants.typeBoostingItems.get(move.type));
                    items.add(Items.wiseGlasses);
                    items.add(Items.choiceSpecs);
                }
            }
            if (!consumableOnly && Gen7Constants.moveBoostingItems.containsKey(moveIdx)) {
                items.addAll(Gen7Constants.moveBoostingItems.get(moveIdx));
            }
        }
        if (numDamagingMoves >= 2) {
            items.add(Items.assaultVest);
        }
        Map<Type, Effectiveness> byType = Effectiveness.against(tp.pokemon.primaryType, tp.pokemon.secondaryType, 7);
        for(Map.Entry<Type, Effectiveness> entry : byType.entrySet()) {
            Integer berry = Gen7Constants.weaknessReducingBerries.get(entry.getKey());
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
        if (Gen7Constants.consumableAbilityBoostingItems.containsKey(ability)) {
            items.add(Gen7Constants.consumableAbilityBoostingItems.get(ability));
        }

        if (!consumableOnly) {
            if (Gen7Constants.abilityBoostingItems.containsKey(ability)) {
                items.addAll(Gen7Constants.abilityBoostingItems.get(ability));
            }
            if (tp.pokemon.primaryType == Type.POISON || tp.pokemon.secondaryType == Type.POISON) {
                items.add(Items.blackSludge);
            }
            List<Integer> speciesItems = Gen7Constants.speciesBoostingItems.get(tp.pokemon.number);
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
