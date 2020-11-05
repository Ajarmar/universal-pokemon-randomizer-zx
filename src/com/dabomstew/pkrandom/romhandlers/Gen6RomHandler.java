package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Gen6RomHandler.java - randomizer handler for X/Y/OR/AS.               --*/
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
import com.dabomstew.pkrandom.GFXFunctions;
import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.RomFunctions;
import com.dabomstew.pkrandom.constants.Gen5Constants;
import com.dabomstew.pkrandom.constants.Gen6Constants;
import com.dabomstew.pkrandom.constants.GlobalConstants;
import com.dabomstew.pkrandom.ctr.AMX;
import com.dabomstew.pkrandom.ctr.GARCArchive;
import com.dabomstew.pkrandom.ctr.Mini;
import com.dabomstew.pkrandom.exceptions.RandomizerIOException;
import com.dabomstew.pkrandom.newnds.NARCArchive;
import com.dabomstew.pkrandom.pokemon.*;
import pptxt.N3DSTxtHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Gen6RomHandler extends Abstract3DSRomHandler {

    public static class Factory extends RomHandler.Factory {

        @Override
        public Gen6RomHandler create(Random random, PrintStream logStream) {
            return new Gen6RomHandler(random, logStream);
        }

        public boolean isLoadable(String filename) {
            return detect3DSRomInner(getProductCodeFromFile(filename), getTitleIdFromFile(filename));
        }
    }

    public Gen6RomHandler(Random random) {
        super(random, null);
    }

    public Gen6RomHandler(Random random, PrintStream logStream) {
        super(random, logStream);
    }

    private static class OffsetWithinEntry {
        private int entry;
        private int offset;
    }

    private static class RomEntry {
        private String name;
        private String romCode;
        private String titleId;
        private String acronym;
        private int romType;
        private boolean staticPokemonSupport = true, copyStaticPokemon = true;
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
    }

    private static List<RomEntry> roms;

    static {
        loadROMInfo();
    }

    private static void loadROMInfo() {
        roms = new ArrayList<>();
        RomEntry current = null;
        try {
            Scanner sc = new Scanner(FileFunctions.openConfig("gen6_offsets.ini"), "UTF-8");
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
                            if (r[1].equalsIgnoreCase("ORAS")) {
                                current.romType = Gen6Constants.Type_ORAS;
                            } else {
                                current.romType = Gen6Constants.Type_XY;
                            }
                        } else if (r[0].equals("TitleId")) {
                            current.titleId = r[1];
                        } else if (r[0].equals("Acronym")) {
                            current.acronym = r[1];
                        } else if (r[0].equals("CopyFrom")) {
                            for (RomEntry otherEntry : roms) {
                                if (r[1].equalsIgnoreCase(otherEntry.romCode)) {
                                    // copy from here
                                    current.arrayEntries.putAll(otherEntry.arrayEntries);
                                    current.numbers.putAll(otherEntry.numbers);
                                    current.strings.putAll(otherEntry.strings);
                                    current.offsetArrayEntries.putAll(otherEntry.offsetArrayEntries);
//                                    if (current.copyStaticPokemon) {
//                                        current.staticPokemon.addAll(otherEntry.staticPokemon);
//                                        current.staticPokemonSupport = true;
//                                    } else {
//                                        current.staticPokemonSupport = false;
//                                    }
                                }
                            }
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
                        } else if (r[0].endsWith("Offset") || r[0].endsWith("Count") || r[0].endsWith("Number")) {
                            int offs = parseRIInt(r[1]);
                            current.numbers.put(r[0], offs);
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

    // This ROM
    private Pokemon[] pokes;
    private Map<Integer,FormeInfo> formeMappings = new TreeMap<>();
    private Map<Integer,Map<Integer,Integer>> absolutePokeNumByBaseForme;
    private Map<Integer,Integer> dummyAbsolutePokeNums;
    private List<Pokemon> pokemonList;
    private List<Pokemon> pokemonListInclFormes;
    private List<MegaEvolution> megaEvolutions;
    private Move[] moves;
    private RomEntry romEntry;
    private byte[] code;
    private List<String> abilityNames;
    private boolean loadedWildMapNames;
    private Map<Integer, String> wildMapNames;
    private int moveTutorMovesOffset;
    private List<String> itemNames;
    private List<String> shopNames;
    private int shopItemsOffset;
    private ItemList allowedItems, nonBadItems;

    private GARCArchive pokeGarc, moveGarc, stringsGarc, storyTextGarc;

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
            stringsGarc = readGARC(romEntry.getString("TextStrings"),true);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        loadPokemonStats();
        loadMoves();

        pokemonListInclFormes = Arrays.asList(pokes);
        pokemonList = Arrays.asList(Arrays.copyOfRange(pokes,0,Gen6Constants.pokemonCount + 1));

        abilityNames = getStrings(false,romEntry.getInt("AbilityNamesTextOffset"));
        itemNames = getStrings(false,romEntry.getInt("ItemNamesTextOffset"));
        shopNames = Gen6Constants.getShopNames(romEntry.romType);

        loadedWildMapNames = false;
        if (romEntry.romType == Gen6Constants.Type_ORAS) {
            isORAS = true;
        }

        allowedItems = Gen6Constants.getAllowedItems(romEntry.romType).copy();
        nonBadItems = Gen6Constants.nonBadItems.copy();
    }

    private void loadPokemonStats() {
        try {
            pokeGarc = this.readGARC(romEntry.getString("PokemonStats"),true);
            String[] pokeNames = readPokemonNames();
            int formeCount = Gen6Constants.getFormeCount(romEntry.romType);
            pokes = new Pokemon[Gen6Constants.pokemonCount + formeCount + 1];
            for (int i = 1; i <= Gen6Constants.pokemonCount; i++) {
                pokes[i] = new Pokemon();
                pokes[i].number = i;
                loadBasicPokeStats(pokes[i],pokeGarc.files.get(i).get(0),formeMappings);
                pokes[i].name = pokeNames[i];
            }

            absolutePokeNumByBaseForme = new HashMap<>();
            dummyAbsolutePokeNums = new HashMap<>();
            dummyAbsolutePokeNums.put(255,0);

            int i = Gen6Constants.pokemonCount + 1;
            int formNum = 1;
            int prevSpecies = 0;
            Map<Integer,Integer> currentMap = new HashMap<>();
            for (int k: formeMappings.keySet()) {
                pokes[i] = new Pokemon();
                pokes[i].number = i;
                loadBasicPokeStats(pokes[i], pokeGarc.files.get(k).get(0),formeMappings);
                FormeInfo fi = formeMappings.get(k);
                pokes[i].name = pokeNames[fi.baseForme];
                pokes[i].baseForme = pokes[fi.baseForme];
                pokes[i].formeNumber = fi.formeNumber;
                pokes[i].formeSuffix = Gen6Constants.formeSuffixes.getOrDefault(k,"");
                if (fi.baseForme == prevSpecies) {
                    formNum++;
                    currentMap.put(formNum,i);
                } else {
                    if (prevSpecies != 0) {
                        absolutePokeNumByBaseForme.put(prevSpecies,currentMap);
                    }
                    prevSpecies = fi.baseForme;
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
        pkmn.hp = stats[Gen6Constants.bsHPOffset] & 0xFF;
        pkmn.attack = stats[Gen6Constants.bsAttackOffset] & 0xFF;
        pkmn.defense = stats[Gen6Constants.bsDefenseOffset] & 0xFF;
        pkmn.speed = stats[Gen6Constants.bsSpeedOffset] & 0xFF;
        pkmn.spatk = stats[Gen6Constants.bsSpAtkOffset] & 0xFF;
        pkmn.spdef = stats[Gen6Constants.bsSpDefOffset] & 0xFF;
        // Type
        pkmn.primaryType = Gen6Constants.typeTable[stats[Gen6Constants.bsPrimaryTypeOffset] & 0xFF];
        pkmn.secondaryType = Gen6Constants.typeTable[stats[Gen6Constants.bsSecondaryTypeOffset] & 0xFF];
        // Only one type?
        if (pkmn.secondaryType == pkmn.primaryType) {
            pkmn.secondaryType = null;
        }
        pkmn.catchRate = stats[Gen6Constants.bsCatchRateOffset] & 0xFF;
        pkmn.growthCurve = ExpCurve.fromByte(stats[Gen6Constants.bsGrowthCurveOffset]);

        pkmn.ability1 = stats[Gen6Constants.bsAbility1Offset] & 0xFF;
        pkmn.ability2 = stats[Gen6Constants.bsAbility2Offset] & 0xFF;
        pkmn.ability3 = stats[Gen6Constants.bsAbility3Offset] & 0xFF;

        // Held Items?
        int item1 = FileFunctions.read2ByteInt(stats, Gen6Constants.bsCommonHeldItemOffset);
        int item2 = FileFunctions.read2ByteInt(stats, Gen6Constants.bsRareHeldItemOffset);

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
            pkmn.darkGrassHeldItem = FileFunctions.read2ByteInt(stats, Gen6Constants.bsDarkGrassHeldItemOffset);
        }

        int formeCount = stats[Gen6Constants.bsFormeCountOffset] & 0xFF;
        if (formeCount > 1) {
            if (!altFormes.keySet().contains(pkmn.number)) {
                int firstFormeOffset = FileFunctions.read2ByteInt(stats, Gen6Constants.bsFormeOffset);
                if (firstFormeOffset != 0) {
                    for (int i = 1; i < formeCount; i++) {
                        altFormes.put(firstFormeOffset + i - 1,new FormeInfo(pkmn.number,i,FileFunctions.read2ByteInt(stats,Gen6Constants.bsFormeSpriteOffset))); // Assumes that forms are in memory in the same order as their numbers
                        if (Gen6Constants.actuallyCosmeticForms.contains(firstFormeOffset+i-1)) {
                            if (pkmn.number != 25 && pkmn.number != 421) { // No Pikachu/Cherrim
                                pkmn.cosmeticForms += 1;
                            }
                        }
                    }
                } else {
                    if (pkmn.number != 493 && pkmn.number != 649 && pkmn.number != 716) {
                        // Reason for exclusions:
                        // Arceus/Genesect: to avoid confusion
                        // Xerneas: Should be handled automatically?
                        pkmn.cosmeticForms = formeCount;
                    }
                }
            } else {
                if (Gen6Constants.actuallyCosmeticForms.contains(pkmn.number)) {
                    pkmn.actuallyCosmetic = true;
                }
            }
        }
    }

    private String[] readPokemonNames() {
        String[] pokeNames = new String[Gen6Constants.pokemonCount + 1];
        List<String> nameList = getStrings(false, romEntry.getInt("PokemonNamesTextOffset"));
        for (int i = 1; i <= Gen6Constants.pokemonCount; i++) {
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
            GARCArchive evoGARC = readGARC(romEntry.getString("PokemonEvolutions"),true);
            for (int i = 1; i <= Gen6Constants.pokemonCount + Gen6Constants.getFormeCount(romEntry.romType); i++) {
                Pokemon pk = pokes[i];
                byte[] evoEntry = evoGARC.files.get(i).get(0);
                for (int evo = 0; evo < 8; evo++) {
                    int method = readWord(evoEntry, evo * 6);
                    int species = readWord(evoEntry, evo * 6 + 4);
                    if (method >= 1 && method <= Gen6Constants.evolutionMethodCount && species >= 1) {
                        EvolutionType et = EvolutionType.fromIndex(6, method);
                        if (et.equals(EvolutionType.LEVEL_HIGH_BEAUTY)) continue; // Remove Feebas "split" evolution
                        int extraInfo = readWord(evoEntry, evo * 6 + 2);
                        Evolution evol = new Evolution(pk, pokes[species], true, et, extraInfo);
                        if (!pk.evolutionsFrom.contains(evol)) {
                            pk.evolutionsFrom.add(evol);
                            if (!pk.actuallyCosmetic) pokes[species].evolutionsTo.add(evol);
                        }
                    }
                }
                // Nincada's Shedinja evo is hardcoded into the game's executable, so
                // if the Pokemon is Nincada, then let's put it as one of its evolutions
                if (pk.number == 290) {
                    Pokemon shedinja = pokes[292];
                    Evolution evol = new Evolution(pk, shedinja, false, EvolutionType.LEVEL_IS_EXTRA, 20);
                    evol.forme = -1;
                    evol.level = 20;
                    pk.evolutionsFrom.add(evol);
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
            GARCArchive megaEvoGARC = readGARC(romEntry.getString("MegaEvolutions"),true);
            for (int i = 1; i <= Gen6Constants.pokemonCount; i++) {
                Pokemon pk = pokes[i];
                byte[] megaEvoEntry = megaEvoGARC.files.get(i).get(0);
                for (int evo = 0; evo < 3; evo++) {
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

    private List<String> getStrings(boolean isStoryText, int index) {
        GARCArchive baseGARC = isStoryText ? storyTextGarc : stringsGarc;
        byte[] rawFile = baseGARC.files.get(index).get(0);
        return new ArrayList<>(N3DSTxtHandler.readTexts(rawFile,true,romEntry.romType));
    }

    private void setStrings(boolean isStoryText, int index, List<String> strings) {
        GARCArchive baseGARC = isStoryText ? storyTextGarc : stringsGarc;
        byte[] oldRawFile = baseGARC.files.get(index).get(0);
        try {
            byte[] newRawFile = N3DSTxtHandler.saveEntry(oldRawFile, strings, romEntry.romType);
            baseGARC.setFile(index, newRawFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMoves() {
        try {
            moveGarc = this.readGARC(romEntry.getString("MoveData"),true);
            int moveCount = Gen6Constants.getMoveCount(romEntry.romType);
            moves = new Move[moveCount + 1];
            List<String> moveNames = getStrings(false, romEntry.getInt("MoveNamesTextOffset"));
            for (int i = 1; i <= moveCount; i++) {
                byte[] moveData;
                if (romEntry.romType == Gen6Constants.Type_ORAS) {
                    moveData = Mini.UnpackMini(moveGarc.files.get(0).get(0), "WD")[i];
                } else {
                    moveData = moveGarc.files.get(i).get(0);
                }
                moves[i] = new Move();
                moves[i].name = moveNames.get(i);
                moves[i].number = i;
                moves[i].internalId = i;
                moves[i].hitratio = (moveData[4] & 0xFF);
                moves[i].power = moveData[3] & 0xFF;
                moves[i].pp = moveData[5] & 0xFF;
                moves[i].type = Gen6Constants.typeTable[moveData[0] & 0xFF];
                moves[i].category = Gen6Constants.moveCategoryIndices[moveData[2] & 0xFF];

                if (i == GlobalConstants.SWIFT_INDEX) {
                    perfectAccuracy = (int)moves[i].hitratio;
                }

                if (GlobalConstants.normalMultihitMoves.contains(i)) {
                    moves[i].hitCount = 19 / 6.0;
                } else if (GlobalConstants.doubleHitMoves.contains(i)) {
                    moves[i].hitCount = 2;
                } else if (i == GlobalConstants.TRIPLE_KICK_INDEX) {
                    moves[i].hitCount = 2.71; // this assumes the first hit lands
                }
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    protected void savingROM() throws IOException {
        savePokemonStats();
        saveMoves();
        try {
            writeCode(code);
            writeGARC(romEntry.getString("TextStrings"), stringsGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    protected String getGameAcronym() {
        return romEntry.acronym;
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

    private void savePokemonStats() {
        int k = Gen6Constants.getBsSize(romEntry.romType);
        byte[] duplicateData = pokeGarc.files.get(Gen6Constants.pokemonCount + Gen6Constants.getFormeCount(romEntry.romType) + 1).get(0);
        for (int i = 1; i <= Gen6Constants.pokemonCount + Gen6Constants.getFormeCount(romEntry.romType); i++) {
            byte[] pokeData = pokeGarc.files.get(i).get(0);
            saveBasicPokeStats(pokes[i], pokeData);
            for (byte pokeDataByte : pokeData) {
                duplicateData[k] = pokeDataByte;
                k++;
            }
        }

        try {
            this.writeGARC(romEntry.getString("PokemonStats"),pokeGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        writeEvolutions();
    }

    private void saveBasicPokeStats(Pokemon pkmn, byte[] stats) {
        stats[Gen6Constants.bsHPOffset] = (byte) pkmn.hp;
        stats[Gen6Constants.bsAttackOffset] = (byte) pkmn.attack;
        stats[Gen6Constants.bsDefenseOffset] = (byte) pkmn.defense;
        stats[Gen6Constants.bsSpeedOffset] = (byte) pkmn.speed;
        stats[Gen6Constants.bsSpAtkOffset] = (byte) pkmn.spatk;
        stats[Gen6Constants.bsSpDefOffset] = (byte) pkmn.spdef;
        stats[Gen6Constants.bsPrimaryTypeOffset] = Gen6Constants.typeToByte(pkmn.primaryType);
        if (pkmn.secondaryType == null) {
            stats[Gen6Constants.bsSecondaryTypeOffset] = stats[Gen6Constants.bsPrimaryTypeOffset];
        } else {
            stats[Gen6Constants.bsSecondaryTypeOffset] = Gen6Constants.typeToByte(pkmn.secondaryType);
        }
        stats[Gen6Constants.bsCatchRateOffset] = (byte) pkmn.catchRate;
        stats[Gen6Constants.bsGrowthCurveOffset] = pkmn.growthCurve.toByte();

        stats[Gen6Constants.bsAbility1Offset] = (byte) pkmn.ability1;
        stats[Gen6Constants.bsAbility2Offset] = pkmn.ability2 != 0 ? (byte) pkmn.ability2 : (byte) pkmn.ability1;
        stats[Gen6Constants.bsAbility3Offset] = (byte) pkmn.ability3;

        // Held items
        if (pkmn.guaranteedHeldItem > 0) {
            FileFunctions.write2ByteInt(stats, Gen6Constants.bsCommonHeldItemOffset, pkmn.guaranteedHeldItem);
            FileFunctions.write2ByteInt(stats, Gen6Constants.bsRareHeldItemOffset, pkmn.guaranteedHeldItem);
            FileFunctions.write2ByteInt(stats, Gen6Constants.bsDarkGrassHeldItemOffset, 0);
        } else {
            FileFunctions.write2ByteInt(stats, Gen6Constants.bsCommonHeldItemOffset, pkmn.commonHeldItem);
            FileFunctions.write2ByteInt(stats, Gen6Constants.bsRareHeldItemOffset, pkmn.rareHeldItem);
            FileFunctions.write2ByteInt(stats, Gen6Constants.bsDarkGrassHeldItemOffset, pkmn.darkGrassHeldItem);
        }

        if (pkmn.fullName().equals("Meowstic")) {
            stats[Gen6Constants.bsGenderOffset] = 0;
        } else if (pkmn.fullName().equals("Meowstic-F")) {
            stats[Gen6Constants.bsGenderOffset] = (byte)0xFE;
        }
    }

    private void writeEvolutions() {
        try {
            GARCArchive evoGARC = readGARC(romEntry.getString("PokemonEvolutions"),true);
            for (int i = 1; i <= Gen6Constants.pokemonCount + Gen6Constants.getFormeCount(romEntry.romType); i++) {
                byte[] evoEntry = evoGARC.files.get(i).get(0);
                Pokemon pk = pokes[i];
                if (pk.number == 290) {
                    writeShedinjaEvolution();
                }
                int evosWritten = 0;
                for (Evolution evo : pk.evolutionsFrom) {
                    writeWord(evoEntry, evosWritten * 6, evo.type.toIndex(6));
                    writeWord(evoEntry, evosWritten * 6 + 2, evo.extraInfo);
                    writeWord(evoEntry, evosWritten * 6 + 4, evo.to.number);
                    evosWritten++;
                    if (evosWritten == 8) {
                        break;
                    }
                }
                while (evosWritten < 8) {
                    writeWord(evoEntry, evosWritten * 6, 0);
                    writeWord(evoEntry, evosWritten * 6 + 2, 0);
                    writeWord(evoEntry, evosWritten * 6 + 4, 0);
                    evosWritten++;
                }
            }
            writeGARC(romEntry.getString("PokemonEvolutions"), evoGARC);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void writeShedinjaEvolution() throws IOException {
        Pokemon nincada = pokes[290];

        // When the "Limit Pokemon" setting is enabled, we clear out the evolutions of
        // everything *not* in the pool, which could include Nincada. In that case,
        // there's no point in even worrying about Shedinja, so just return.
        if (nincada.evolutionsFrom.size() == 0) {
            return;
        }
        Pokemon primaryEvolution = nincada.evolutionsFrom.get(0).to;
        Pokemon extraEvolution = nincada.evolutionsFrom.get(1).to;

        // In the CRO that handles the evolution cutscene, there's a hardcoded check to
        // see if the Pokemon that just evolved is now a Ninjask after evolving. It
        // performs that check using the following instructions:
        // sub    r0, r1, #0x100
        // subs   r0, r0, #0x23
        // bne    skipMakingShedinja
        // The below code tweaks these instructions to use the species ID of Nincada's
        // new primary evolution; that way, evolving Nincada will still produce an "extra"
        // Pokemon like in older generations.
        byte[] evolutionCRO = readFile(romEntry.getString("Evolution"));
        int offset = find(evolutionCRO, Gen6Constants.ninjaskSpeciesPrefix);
        if (offset > 0) {
            offset += Gen6Constants.ninjaskSpeciesPrefix.length() / 2; // because it was a prefix
            int primaryEvoLower = primaryEvolution.number & 0x00FF;
            int primaryEvoUpper = (primaryEvolution.number & 0xFF00) >> 8;
            evolutionCRO[offset] = (byte) primaryEvoUpper;
            evolutionCRO[offset + 4] = (byte) primaryEvoLower;
        }

        // In the game's executable, there's a hardcoded value to indicate what "extra"
        // Pokemon to create. It produces a Shedinja using the following instruction:
        // mov r1, #0x124, where 0x124 = 292 in decimal, which is Shedinja's species ID.
        // The below code tweaks this instruction to use the species ID of Nincada's
        // new extra evolution.
        offset = find(code, Gen6Constants.shedinjaSpeciesPrefix);
        if (offset > 0) {
            offset += Gen6Constants.shedinjaSpeciesPrefix.length() / 2; // because it was a prefix
            int extraEvoLower = extraEvolution.number & 0x00FF;
            int extraEvoUpper = (extraEvolution.number & 0xFF00) >> 8;
            code[offset] = (byte) extraEvoLower;
            code[offset + 1] = (byte) (0x10 + extraEvoUpper);
            code[offset + 2] = 0x00;
            code[offset + 3] = (byte) 0xE3;
        }

        // Now that we've handled the hardcoded Shedinja evolution, delete it so that
        // we do *not* handle it in WriteEvolutions
        nincada.evolutionsFrom.remove(1);
        writeFile(romEntry.getString("Evolution"), evolutionCRO);
    }

    private void saveMoves() {
        int moveCount = Gen6Constants.getMoveCount(romEntry.romType);
        byte[][] miniArchive = new byte[0][0];
        if (romEntry.romType == Gen6Constants.Type_ORAS) {
            miniArchive = Mini.UnpackMini(moveGarc.files.get(0).get(0), "WD");
        }
        for (int i = 1; i <= moveCount; i++) {
            byte[] data;
            if (romEntry.romType == Gen6Constants.Type_ORAS) {
                data = miniArchive[i];
            } else {
                data = moveGarc.files.get(i).get(0);
            }
            data[2] = Gen6Constants.moveCategoryToByte(moves[i].category);
            data[3] = (byte) moves[i].power;
            data[0] = Gen6Constants.typeToByte(moves[i].type);
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
            if (romEntry.romType == Gen6Constants.Type_ORAS) {
                moveGarc.setFile(0, Mini.PackMini(miniArchive, "WD"));
            }
            this.writeGARC(romEntry.getString("MoveData"), moveGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void patchFormeReversion() throws IOException {
        // Upon loading a save, all Mega Pokemon and all Primal Reversions
        // in the player's party are set back to their base forme. This
        // patches .code such that this reversion does not happen.
        String saveLoadFormeReversionPrefix = Gen6Constants.getSaveLoadFormeReversionPrefix(romEntry.romType);
        int offset = find(code, saveLoadFormeReversionPrefix);
        if (offset > 0) {
            offset += saveLoadFormeReversionPrefix.length() / 2; // because it was a prefix

            // The actual offset of the code we want to patch is 0x10 bytes from the end of
            // the prefix. We have to do this because these 0x10 bytes differ between the
            // base game and all game updates, so we cannot use them as part of our prefix.
            offset += 0x10;

            // Stubs the call to the function that checks for Primal Reversions and
            // Mega Pokemon
            code[offset] = 0x00;
            code[offset + 1] = 0x00;
            code[offset + 2] = 0x00;
            code[offset + 3] = 0x00;
        }

        // In ORAS, the game also has hardcoded checks to revert Primal Groudon and Primal Kyogre
        // immediately after catching them.
        if (romEntry.romType == Gen6Constants.Type_ORAS) {
            byte[] battleCRO = readFile(romEntry.getString("Battle"));
            offset = find(battleCRO, Gen6Constants.afterBattleFormeReversionPrefix);
            if (offset > 0) {
                offset += Gen6Constants.afterBattleFormeReversionPrefix.length() / 2; // because it was a prefix

                // The game checks for Primal Kyogre and Primal Groudon by pc-relative loading 0x17E,
                // which is Kyogre's species ID. The call to pml::pokepara::CoreParam::ChangeFormNo
                // is used by other species which we probably don't want to break, so instead of
                // stubbing the call to the function, just break the hardcoded species ID check by
                // making the game pc-relative load a total nonsense ID.
                battleCRO[offset] = (byte) 0xFF;
                battleCRO[offset + 1] = (byte) 0xFF;

                writeFile(romEntry.getString("Battle"), battleCRO);
            }
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
        int formeCount = Gen6Constants.getFormeCount(romEntry.romType);
        return pokemonListInclFormes.subList(Gen6Constants.pokemonCount + 1, Gen6Constants.pokemonCount + formeCount + 1);
    }

    @Override
    public List<MegaEvolution> getMegaEvolutions() {
        return megaEvolutions;
    }

    @Override
    public Pokemon getAltFormeOfPokemon(Pokemon pk, int forme) {
        int pokeNum = absolutePokeNumByBaseForme.getOrDefault(pk.number,dummyAbsolutePokeNums).getOrDefault(forme,0);
        return pokeNum != 0 ? pokes[pokeNum] : pk;
    }

    @Override
    public boolean hasFunctionalFormes() {
        return true;
    }

    @Override
    public List<Pokemon> getStarters() {
        List<StaticEncounter> starters = new ArrayList<>();
        try {
            byte[] staticCRO = readFile(romEntry.getString("StaticPokemon"));

            List<Integer> starterIndices =
                    Arrays.stream(romEntry.arrayEntries.get("StarterIndices")).boxed().collect(Collectors.toList());

            // Gift Pokemon
            int count = Gen6Constants.getGiftPokemonCount(romEntry.romType);
            int size = Gen6Constants.getGiftPokemonSize(romEntry.romType);
            int offset = romEntry.getInt("GiftPokemonOffset");
            for (int i = 0; i < count; i++) {
                if (!starterIndices.contains(i)) continue;
                StaticEncounter se = new StaticEncounter();
                int species = FileFunctions.read2ByteInt(staticCRO,offset+i*size);
                Pokemon pokemon = pokes[species];
                int forme = staticCRO[offset+i*size + 4];
                if (forme > pokemon.cosmeticForms && forme != 30 && forme != 31) {
                    int speciesWithForme = absolutePokeNumByBaseForme
                            .getOrDefault(species, dummyAbsolutePokeNums)
                            .getOrDefault(forme, 0);
                    pokemon = pokes[speciesWithForme];
                }
                se.pkmn = pokemon;
                se.forme = forme;
                se.level = staticCRO[offset+i*size + 5];
                int heldItem = FileFunctions.readFullIntLittleEndian(staticCRO,offset+i*size + 12);
                if (heldItem < 0) {
                    heldItem = 0;
                }
                se.heldItem = heldItem;
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
            byte[] staticCRO = readFile(romEntry.getString("StaticPokemon"));
            byte[] displayCRO = readFile(romEntry.getString("StarterDisplay"));

            List<Integer> starterIndices =
                    Arrays.stream(romEntry.arrayEntries.get("StarterIndices")).boxed().collect(Collectors.toList());

            // Gift Pokemon
            int count = Gen6Constants.getGiftPokemonCount(romEntry.romType);
            int size = Gen6Constants.getGiftPokemonSize(romEntry.romType);
            int offset = romEntry.getInt("GiftPokemonOffset");
            int displayOffset = readWord(displayCRO,romEntry.getInt("StarterOffsetOffset")) + romEntry.getInt("StarterExtraOffset");

            Iterator<Pokemon> starterIter = newStarters.iterator();

            int displayIndex = 0;

            List<String> starterText = getStrings(false,romEntry.getInt("StarterTextOffset"));
            int[] starterTextIndices = romEntry.arrayEntries.get("SpecificStarterTextOffsets");

            for (int i = 0; i < count; i++) {
                if (!starterIndices.contains(i)) continue;

                StaticEncounter newStatic = new StaticEncounter();
                Pokemon starter = starterIter.next();
                if (starter.formeNumber > 0) {
                    newStatic.forme = starter.formeNumber;
                    newStatic.formeSuffix = starter.formeSuffix;
                    starter = starter.baseForme;
                }
                newStatic.pkmn = starter;
                if (starter.cosmeticForms > 0) {
                    newStatic.forme = this.random.nextInt(starter.cosmeticForms);
                }
                writeWord(staticCRO,offset+i*size,newStatic.pkmn.number);
                staticCRO[offset+i*size + 4] = (byte)newStatic.forme;
//                staticCRO[offset+i*size + 5] = (byte)newStatic.level;
                if (newStatic.heldItem == 0) {
                    writeWord(staticCRO,offset+i*size + 12,-1);
                } else {
                    writeWord(staticCRO,offset+i*size + 12,newStatic.heldItem);
                }
                writeWord(displayCRO,displayOffset+displayIndex*0x54,newStatic.pkmn.number);
                displayCRO[displayOffset+displayIndex*0x54+2] = (byte)newStatic.forme;
                if (displayIndex < 3) {
                    starterText.set(starterTextIndices[displayIndex],
                            "[VAR PKNAME(0000)]");
                }
                displayIndex++;
            }
            writeFile(romEntry.getString("StaticPokemon"),staticCRO);
            writeFile(romEntry.getString("StarterDisplay"),displayCRO);
            setStrings(false, romEntry.getInt("StarterTextOffset"), starterText);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return true;
    }

    @Override
    public boolean hasStarterAltFormes() {
        return true;
    }

    @Override
    public int starterCount() {
        return romEntry.romType == Gen6Constants.Type_XY ? 6 : 12;
    }

    @Override
    public Map<Integer, StatChange> getUpdatedPokemonStats(int generation) {
        Map<Integer, StatChange> map = GlobalConstants.getStatChanges(generation);
        switch(generation) {
            case 7:
                map.put(781,new StatChange(Stat.SPDEF.val,105));
                break;
            case 8:
                map.put(776,new StatChange(Stat.ATK.val | Stat.SPATK.val,140,140));
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
        if (!loadedWildMapNames) {
            loadWildMapNames();
        }
        try {
            if (romEntry.romType == Gen6Constants.Type_ORAS) {
                return getEncountersORAS();
            } else {
                return getEncountersXY();
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private List<EncounterSet> getEncountersXY() throws IOException {
        GARCArchive encounterGarc = readGARC(romEntry.getString("WildPokemon"), false);
        List<EncounterSet> encounters = new ArrayList<>();
        for (int i = 0; i < encounterGarc.files.size() - 1; i++) {
            byte[] b = encounterGarc.files.get(i).get(0);
            if (!wildMapNames.containsKey(i)) {
                wildMapNames.put(i, "? Unknown ?");
            }
            String mapName = wildMapNames.get(i);
            int offset = FileFunctions.readFullIntLittleEndian(b, 0x10) + 0x10;
            int length = b.length - offset;
            if (length < 0x178) { // No encounters in this map
                continue;
            }
            byte[] encounterData = new byte[0x178];
            System.arraycopy(b, offset, encounterData, 0, 0x178);

            // TODO: Is there some rate we can check like in older gens?
            // First, 12 grass encounters, 12 rough terrain encounters, and 12 encounters each for yellow/purple/red flowers
            EncounterSet grassEncounters = readEncounter(encounterData, 0, 12);
            if (grassEncounters.encounters.size() > 0) {
                grassEncounters.displayName = mapName + " Grass/Cave";
                encounters.add(grassEncounters);
            }
            EncounterSet yellowFlowerEncounters = readEncounter(encounterData, 48, 12);
            if (yellowFlowerEncounters.encounters.size() > 0) {
                yellowFlowerEncounters.displayName = mapName + " Yellow Flowers";
                encounters.add(yellowFlowerEncounters);
            }
            EncounterSet purpleFlowerEncounters = readEncounter(encounterData, 96, 12);
            if (purpleFlowerEncounters.encounters.size() > 0) {
                purpleFlowerEncounters.displayName = mapName + " Purple Flowers";
                encounters.add(purpleFlowerEncounters);
            }
            EncounterSet redFlowerEncounters = readEncounter(encounterData, 144, 12);
            if (redFlowerEncounters.encounters.size() > 0) {
                redFlowerEncounters.displayName = mapName + " Red Flowers";
                encounters.add(redFlowerEncounters);
            }
            EncounterSet roughTerrainEncounters = readEncounter(encounterData, 192, 12);
            if (roughTerrainEncounters.encounters.size() > 0) {
                roughTerrainEncounters.displayName = mapName + " Rough Terrain";
                encounters.add(roughTerrainEncounters);
            }

            // 5 surf and 5 rock smash encounters
            EncounterSet surfEncounters = readEncounter(encounterData, 240, 5);
            if (surfEncounters.encounters.size() > 0) {
                surfEncounters.displayName = mapName + " Surf";
                encounters.add(surfEncounters);
            }
            EncounterSet rockSmashEncounters = readEncounter(encounterData, 260, 5);
            if (rockSmashEncounters.encounters.size() > 0) {
                rockSmashEncounters.displayName = mapName + " Rock Smash";
                encounters.add(rockSmashEncounters);
            }

            // 3 Encounters for each type of rod
            EncounterSet oldRodEncounters = readEncounter(encounterData, 280, 3);
            if (oldRodEncounters.encounters.size() > 0) {
                oldRodEncounters.displayName = mapName + " Old Rod";
                encounters.add(oldRodEncounters);
            }
            EncounterSet goodRodEncounters = readEncounter(encounterData, 292, 3);
            if (goodRodEncounters.encounters.size() > 0) {
                goodRodEncounters.displayName = mapName + " Good Rod";
                encounters.add(goodRodEncounters);
            }
            EncounterSet superRodEncounters = readEncounter(encounterData, 304, 3);
            if (superRodEncounters.encounters.size() > 0) {
                superRodEncounters.displayName = mapName + " Super Rod";
                encounters.add(superRodEncounters);
            }

            // Lastly, 5 for each kind of Horde
            EncounterSet hordeCommonEncounters = readEncounter(encounterData, 316, 5);
            if (hordeCommonEncounters.encounters.size() > 0) {
                hordeCommonEncounters.displayName = mapName + " Common Horde";
                encounters.add(hordeCommonEncounters);
            }
            EncounterSet hordeUncommonEncounters = readEncounter(encounterData, 336, 5);
            if (hordeUncommonEncounters.encounters.size() > 0) {
                hordeUncommonEncounters.displayName = mapName + " Uncommon Horde";
                encounters.add(hordeUncommonEncounters);
            }
            EncounterSet hordeRareEncounters = readEncounter(encounterData, 356, 5);
            if (hordeRareEncounters.encounters.size() > 0) {
                hordeRareEncounters.displayName = mapName + " Rare Horde";
                encounters.add(hordeRareEncounters);
            }
        }

        // The ceiling/flying/rustling bush encounters are hardcoded in the Field CRO
        byte[] fieldCRO = readFile(romEntry.getString("Field"));
        String currentName = Gen6Constants.fallingEncounterNameMap.get(0);
        int startingOffsetOfCurrentName = 0;
        for (int i = 0; i < Gen6Constants.fallingEncounterCount; i++) {
            int offset = Gen6Constants.fallingEncounterOffset + i * Gen6Constants.fieldEncounterSize;
            EncounterSet fallingEncounter = readFieldEncounter(fieldCRO, offset);
            if (Gen6Constants.fallingEncounterNameMap.containsKey(i)) {
                currentName = Gen6Constants.fallingEncounterNameMap.get(i);
                startingOffsetOfCurrentName = i;
            }
            int encounterNumber = (i - startingOffsetOfCurrentName) + 1;
            fallingEncounter.displayName = currentName + " #" + encounterNumber;
            encounters.add(fallingEncounter);
        }
        currentName = Gen6Constants.rustlingBushEncounterNameMap.get(0);
        startingOffsetOfCurrentName = 0;
        for (int i = 0; i < Gen6Constants.rustlingBushEncounterCount; i++) {
            int offset = Gen6Constants.rustlingBushEncounterOffset + i * Gen6Constants.fieldEncounterSize;
            EncounterSet rustlingBushEncounter = readFieldEncounter(fieldCRO, offset);
            if (Gen6Constants.rustlingBushEncounterNameMap.containsKey(i)) {
                currentName = Gen6Constants.rustlingBushEncounterNameMap.get(i);
                startingOffsetOfCurrentName = i;
            }
            int encounterNumber = (i - startingOffsetOfCurrentName) + 1;
            rustlingBushEncounter.displayName = currentName + " #" + encounterNumber;
            encounters.add(rustlingBushEncounter);
        }
        return encounters;
    }

    private List<EncounterSet> getEncountersORAS() throws IOException {
        GARCArchive encounterGarc = readGARC(romEntry.getString("WildPokemon"), false);
        List<EncounterSet> encounters = new ArrayList<>();
        for (int i = 0; i < encounterGarc.files.size() - 2; i++) {
            byte[] b = encounterGarc.files.get(i).get(0);
            if (!wildMapNames.containsKey(i)) {
                wildMapNames.put(i, "? Unknown ?");
            }
            String mapName = wildMapNames.get(i);
            int offset = FileFunctions.readFullIntLittleEndian(b, 0x10) + 0xE;
            int offset2 = FileFunctions.readFullIntLittleEndian(b, 0x14);
            int length = offset2 - offset;
            if (length < 0xF6) { // No encounters in this map
                continue;
            }
            byte[] encounterData = new byte[0xF6];
            System.arraycopy(b, offset, encounterData, 0, 0xF6);

            // First, read 12 grass encounters and 12 long grass encounters
            EncounterSet grassEncounters = readEncounter(encounterData, 0, 12);
            if (grassEncounters.encounters.size() > 0) {
                grassEncounters.displayName = mapName + " Grass/Cave";
                grassEncounters.offset = i;
                encounters.add(grassEncounters);
            }
            EncounterSet longGrassEncounters = readEncounter(encounterData, 48, 12);
            if (longGrassEncounters.encounters.size() > 0) {
                longGrassEncounters.displayName = mapName + " Long Grass";
                longGrassEncounters.offset = i;
                encounters.add(longGrassEncounters);
            }

            // Now, 3 DexNav Foreign encounters
            EncounterSet dexNavForeignEncounters = readEncounter(encounterData, 96, 3);
            if (dexNavForeignEncounters.encounters.size() > 0) {
                dexNavForeignEncounters.displayName = mapName + " DexNav Foreign Encounter";
                dexNavForeignEncounters.offset = i;
                encounters.add(dexNavForeignEncounters);
            }

            // 5 surf and 5 rock smash encounters
            EncounterSet surfEncounters = readEncounter(encounterData, 108, 5);
            if (surfEncounters.encounters.size() > 0) {
                surfEncounters.displayName = mapName + " Surf";
                surfEncounters.offset = i;
                encounters.add(surfEncounters);
            }
            EncounterSet rockSmashEncounters = readEncounter(encounterData, 128, 5);
            if (rockSmashEncounters.encounters.size() > 0) {
                rockSmashEncounters.displayName = mapName + " Rock Smash";
                rockSmashEncounters.offset = i;
                encounters.add(rockSmashEncounters);
            }

            // 3 Encounters for each type of rod
            EncounterSet oldRodEncounters = readEncounter(encounterData, 148, 3);
            if (oldRodEncounters.encounters.size() > 0) {
                oldRodEncounters.displayName = mapName + " Old Rod";
                oldRodEncounters.offset = i;
                encounters.add(oldRodEncounters);
            }
            EncounterSet goodRodEncounters = readEncounter(encounterData, 160, 3);
            if (goodRodEncounters.encounters.size() > 0) {
                goodRodEncounters.displayName = mapName + " Good Rod";
                goodRodEncounters.offset = i;
                encounters.add(goodRodEncounters);
            }
            EncounterSet superRodEncounters = readEncounter(encounterData, 172, 3);
            if (superRodEncounters.encounters.size() > 0) {
                superRodEncounters.displayName = mapName + " Super Rod";
                superRodEncounters.offset = i;
                encounters.add(superRodEncounters);
            }

            // Lastly, 5 for each kind of Horde
            EncounterSet hordeCommonEncounters = readEncounter(encounterData, 184, 5);
            if (hordeCommonEncounters.encounters.size() > 0) {
                hordeCommonEncounters.displayName = mapName + " Common Horde";
                hordeCommonEncounters.offset = i;
                encounters.add(hordeCommonEncounters);
            }
            EncounterSet hordeUncommonEncounters = readEncounter(encounterData, 204, 5);
            if (hordeUncommonEncounters.encounters.size() > 0) {
                hordeUncommonEncounters.displayName = mapName + " Uncommon Horde";
                hordeUncommonEncounters.offset = i;
                encounters.add(hordeUncommonEncounters);
            }
            EncounterSet hordeRareEncounters = readEncounter(encounterData, 224, 5);
            if (hordeRareEncounters.encounters.size() > 0) {
                hordeRareEncounters.displayName = mapName + " Rare Horde";
                hordeRareEncounters.offset = i;
                encounters.add(hordeRareEncounters);
            }
        }
        return encounters;
    }

    private EncounterSet readEncounter(byte[] data, int offset, int amount) {
        EncounterSet es = new EncounterSet();
        es.rate = 1;
        for (int i = 0; i < amount; i++) {
            int species = readWord(data, offset + i * 4) & 0x7FF;
            int forme = readWord(data, offset + i * 4) >> 11;
            if (species != 0) {
                Encounter e = new Encounter();
                Pokemon baseForme = pokes[species];

                // If the forme is purely cosmetic, just use the base forme as the Pokemon
                // for this encounter (the cosmetic forme will be stored in the encounter).
                // Do the same for forms 30 and 31, because they actually aren't forms, but
                // rather act as indicators for what forme should appear when encountered:
                // 30 = Spawn the cosmetic forme specific to the user's region (Scatterbug line)
                // 31 = Spawn *any* cosmetic forme with equal probability (Unown Mirage Cave)
                if (forme <= baseForme.cosmeticForms || forme == 30 || forme == 31) {
                    e.pokemon = pokes[species];
                } else {
                    int speciesWithForme = absolutePokeNumByBaseForme
                            .getOrDefault(species, dummyAbsolutePokeNums)
                            .getOrDefault(forme, 0);
                    e.pokemon = pokes[speciesWithForme];
                }
                e.formeNumber = forme;
                e.level = data[offset + 2 + i * 4];
                e.maxLevel = data[offset + 3 + i * 4];
                es.encounters.add(e);
            }
        }
        return es;
    }

    private EncounterSet readFieldEncounter(byte[] data, int offset) {
        EncounterSet es = new EncounterSet();
        for (int i = 0; i < 7; i++) {
            int species = readWord(data, offset + 4 + i * 8);
            int level = data[offset + 8 + i * 8];
            if (species != 0) {
                Encounter e = new Encounter();
                e.pokemon = pokes[species];
                e.formeNumber = 0;
                e.level = level;
                e.maxLevel = level;
                es.encounters.add(e);
            }
        }
        return es;
    }

    @Override
    public void setEncounters(boolean useTimeOfDay, List<EncounterSet> encountersList) {
        try {
            if (romEntry.romType == Gen6Constants.Type_ORAS) {
                setEncountersORAS(encountersList);
            } else {
                setEncountersXY(encountersList);
            }
        } catch (IOException ex) {
            throw new RandomizerIOException(ex);
        }
    }

    private void setEncountersXY(List<EncounterSet> encountersList) throws IOException {
        String encountersFile = romEntry.getString("WildPokemon");
        GARCArchive encounterGarc = readGARC(encountersFile, false);
        Iterator<EncounterSet> encounters = encountersList.iterator();
        for (int i = 0; i < encounterGarc.files.size() - 1; i++) {
            byte[] b = encounterGarc.files.get(i).get(0);
            int offset = FileFunctions.readFullIntLittleEndian(b, 0x10) + 0x10;
            int length = b.length - offset;
            if (length < 0x178) { // No encounters in this map
                continue;
            }
            byte[] encounterData = new byte[0x178];
            System.arraycopy(b, offset, encounterData, 0, 0x178);

            // First, 12 grass encounters, 12 rough terrain encounters, and 12 encounters each for yellow/purple/red flowers
            if (readEncounter(encounterData, 0, 12).encounters.size() > 0) {
                EncounterSet grass = encounters.next();
                writeEncounter(encounterData, 0, grass.encounters);
            }
            if (readEncounter(encounterData, 48, 12).encounters.size() > 0) {
                EncounterSet yellowFlowers = encounters.next();
                writeEncounter(encounterData, 48, yellowFlowers.encounters);
            }
            if (readEncounter(encounterData, 96, 12).encounters.size() > 0) {
                EncounterSet purpleFlowers = encounters.next();
                writeEncounter(encounterData, 96, purpleFlowers.encounters);
            }
            if (readEncounter(encounterData, 144, 12).encounters.size() > 0) {
                EncounterSet redFlowers = encounters.next();
                writeEncounter(encounterData, 144, redFlowers.encounters);
            }
            if (readEncounter(encounterData, 192, 12).encounters.size() > 0) {
                EncounterSet roughTerrain = encounters.next();
                writeEncounter(encounterData, 192, roughTerrain.encounters);
            }

            // 5 surf and 5 rock smash encounters
            if (readEncounter(encounterData, 240, 5).encounters.size() > 0) {
                EncounterSet surf = encounters.next();
                writeEncounter(encounterData, 240, surf.encounters);
            }
            if (readEncounter(encounterData, 260, 5).encounters.size() > 0) {
                EncounterSet rockSmash = encounters.next();
                writeEncounter(encounterData, 260, rockSmash.encounters);
            }

            // 3 Encounters for each type of rod
            if (readEncounter(encounterData, 280, 3).encounters.size() > 0) {
                EncounterSet oldRod = encounters.next();
                writeEncounter(encounterData, 280, oldRod.encounters);
            }
            if (readEncounter(encounterData, 292, 3).encounters.size() > 0) {
                EncounterSet goodRod = encounters.next();
                writeEncounter(encounterData, 292, goodRod.encounters);
            }
            if (readEncounter(encounterData, 304, 3).encounters.size() > 0) {
                EncounterSet superRod = encounters.next();
                writeEncounter(encounterData, 304, superRod.encounters);
            }

            // Lastly, 5 for each kind of Horde
            if (readEncounter(encounterData, 316, 5).encounters.size() > 0) {
                EncounterSet commonHorde = encounters.next();
                writeEncounter(encounterData, 316, commonHorde.encounters);
            }
            if (readEncounter(encounterData, 336, 5).encounters.size() > 0) {
                EncounterSet uncommonHorde = encounters.next();
                writeEncounter(encounterData, 336, uncommonHorde.encounters);
            }
            if (readEncounter(encounterData, 356, 5).encounters.size() > 0) {
                EncounterSet rareHorde = encounters.next();
                writeEncounter(encounterData, 356, rareHorde.encounters);
            }

            // Write the encounter data back to the file
            System.arraycopy(encounterData, 0, b, offset, 0x178);
        }

        // Save
        writeGARC(encountersFile, encounterGarc);

        // Now write the encounters hardcoded in the Field CRO
        byte[] fieldCRO = readFile(romEntry.getString("Field"));
        for (int i = 0; i < Gen6Constants.fallingEncounterCount; i++) {
            int offset = Gen6Constants.fallingEncounterOffset + i * Gen6Constants.fieldEncounterSize;
            EncounterSet fallingEncounter = encounters.next();
            writeFieldEncounter(fieldCRO, offset, fallingEncounter.encounters);
        }
        for (int i = 0; i < Gen6Constants.rustlingBushEncounterCount; i++) {
            int offset = Gen6Constants.rustlingBushEncounterOffset + i * Gen6Constants.fieldEncounterSize;
            EncounterSet rustlingBushEncounter = encounters.next();
            writeFieldEncounter(fieldCRO, offset, rustlingBushEncounter.encounters);
        }

        // Save
        writeFile(romEntry.getString("Field"), fieldCRO);
    }

    private void setEncountersORAS(List<EncounterSet> encountersList) throws IOException {
        String encountersFile = romEntry.getString("WildPokemon");
        GARCArchive encounterGarc = readGARC(encountersFile, false);
        Iterator<EncounterSet> encounters = encountersList.iterator();
        byte[] decStorage = encounterGarc.files.get(encounterGarc.files.size() - 1).get(0);
        for (int i = 0; i < encounterGarc.files.size() - 2; i++) {
            byte[] b = encounterGarc.files.get(i).get(0);
            int offset = FileFunctions.readFullIntLittleEndian(b, 0x10) + 0xE;
            int offset2 = FileFunctions.readFullIntLittleEndian(b, 0x14);
            int length = offset2 - offset;
            if (length < 0xF6) { // No encounters in this map
                continue;
            }
            byte[] encounterData = new byte[0xF6];
            System.arraycopy(b, offset, encounterData, 0, 0xF6);

            // First, 12 grass encounters and 12 long grass encounters
            if (readEncounter(encounterData, 0, 12).encounters.size() > 0) {
                EncounterSet grass = encounters.next();
                writeEncounter(encounterData, 0, grass.encounters);
            }
            if (readEncounter(encounterData, 48, 12).encounters.size() > 0) {
                EncounterSet longGrass = encounters.next();
                writeEncounter(encounterData, 48, longGrass.encounters);
            }

            // Now, 3 DexNav Foreign encounters
            if (readEncounter(encounterData, 96, 3).encounters.size() > 0) {
                EncounterSet dexNav = encounters.next();
                writeEncounter(encounterData, 96, dexNav.encounters);
            }

            // 5 surf and 5 rock smash encounters
            if (readEncounter(encounterData, 108, 5).encounters.size() > 0) {
                EncounterSet surf = encounters.next();
                writeEncounter(encounterData, 108, surf.encounters);
            }
            if (readEncounter(encounterData, 128, 5).encounters.size() > 0) {
                EncounterSet rockSmash = encounters.next();
                writeEncounter(encounterData, 128, rockSmash.encounters);
            }

            // 3 Encounters for each type of rod
            if (readEncounter(encounterData, 148, 3).encounters.size() > 0) {
                EncounterSet oldRod = encounters.next();
                writeEncounter(encounterData, 148, oldRod.encounters);
            }
            if (readEncounter(encounterData, 160, 3).encounters.size() > 0) {
                EncounterSet goodRod = encounters.next();
                writeEncounter(encounterData, 160, goodRod.encounters);
            }
            if (readEncounter(encounterData, 172, 3).encounters.size() > 0) {
                EncounterSet superRod = encounters.next();
                writeEncounter(encounterData, 172, superRod.encounters);
            }

            // Lastly, 5 for each kind of Horde
            if (readEncounter(encounterData, 184, 5).encounters.size() > 0) {
                EncounterSet commonHorde = encounters.next();
                writeEncounter(encounterData, 184, commonHorde.encounters);
            }
            if (readEncounter(encounterData, 204, 5).encounters.size() > 0) {
                EncounterSet uncommonHorde = encounters.next();
                writeEncounter(encounterData, 204, uncommonHorde.encounters);
            }
            if (readEncounter(encounterData, 224, 5).encounters.size() > 0) {
                EncounterSet rareHorde = encounters.next();
                writeEncounter(encounterData, 224, rareHorde.encounters);
            }

            // Write the encounter data back to the file
            System.arraycopy(encounterData, 0, b, offset, 0xF6);

            // Also write the encounter data to the decStorage file
            int decStorageOffset = FileFunctions.readFullIntLittleEndian(decStorage, (i + 1) * 4) + 0xE;
            System.arraycopy(encounterData, 0, decStorage, decStorageOffset, 0xF4);
        }

        // Save
        writeGARC(encountersFile, encounterGarc);
    }

    private void writeEncounter(byte[] data, int offset, List<Encounter> encounters) {
        for (int i = 0; i < encounters.size(); i++) {
            Encounter encounter = encounters.get(i);
            if (encounter.pokemon.formeNumber > 0) { // Failsafe if we need to write encounters without modifying species
                encounter.pokemon = encounter.pokemon.baseForme;
            }
            int speciesAndFormeData = (encounter.formeNumber << 11) + encounter.pokemon.number;
            writeWord(data, offset + i * 4, speciesAndFormeData);
            data[offset + 2 + i * 4] = (byte) encounter.level;
            data[offset + 3 + i * 4] = (byte) encounter.maxLevel;
        }
    }

    private void writeFieldEncounter(byte[] data, int offset, List<Encounter> encounters) {
        for (int i = 0; i < encounters.size(); i++) {
            Encounter encounter = encounters.get(i);
            if (encounter.pokemon.formeNumber > 0) { // Failsafe if we need to write encounters without modifying species
                encounter.pokemon = encounter.pokemon.baseForme;
            }
            writeWord(data, offset + 4 + i * 8, encounter.pokemon.number);
            data[offset + 8 + i * 8] = (byte) encounter.level;
        }
    }

    private void loadWildMapNames() {
        try {
            wildMapNames = new HashMap<>();
            GARCArchive encounterGarc = this.readGARC(romEntry.getString("WildPokemon"), false);
            int zoneDataOffset = romEntry.getInt("MapTableFileOffset");
            byte[] zoneData = encounterGarc.files.get(zoneDataOffset).get(0);
            List<String> allMapNames = getStrings(false, romEntry.getInt("MapNamesTextOffset"));
            for (int map = 0; map < zoneDataOffset; map++) {
                int indexNum = (map * 56) + 0x1C;
                int nameIndex1 = zoneData[indexNum] & 0xFF;
                int nameIndex2 = 0x100 * ((int) (zoneData[indexNum + 1]) & 1);
                String mapName = allMapNames.get(nameIndex1 + nameIndex2);
                wildMapNames.put(map, mapName);
            }
            loadedWildMapNames = true;
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public List<Trainer> getTrainers() {
        List<Trainer> allTrainers = new ArrayList<>();
        boolean isORAS = romEntry.romType == Gen6Constants.Type_ORAS;
        try {
            GARCArchive trainers = this.readGARC(romEntry.getString("TrainerData"),true);
            GARCArchive trpokes = this.readGARC(romEntry.getString("TrainerPokemon"),true);
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
                tr.poketype = isORAS ? readWord(trainer,0) : trainer[0] & 0xFF;
                tr.offset = i;
                tr.trainerclass = isORAS ? readWord(trainer,2) : trainer[1] & 0xFF;
                int offset = isORAS ? 6 : 2;
                int battleType = trainer[offset] & 0xFF;
                int numPokes = trainer[offset+1] & 0xFF;
                int trainerAILevel = trainer[offset+9] & 0xFF;
                boolean healer = trainer[offset+13] != 0;
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
                    int level = readWord(trpoke, pokeOffs + 2);
                    int species = readWord(trpoke, pokeOffs + 4);
                    int formnum = readWord(trpoke, pokeOffs + 6);
                    TrainerPokemon tpk = new TrainerPokemon();
                    tpk.level = level;
                    tpk.pokemon = pokes[species];
                    tpk.AILevel = trainerAILevel;
                    tpk.ability = trpoke[pokeOffs + 1] & 0xFF;
                    tpk.forme = formnum;
                    tpk.formeSuffix = Gen6Constants.getFormeSuffixByBaseForme(species,formnum);
                    tpk.absolutePokeNumber = absolutePokeNumByBaseForme
                            .getOrDefault(species,dummyAbsolutePokeNums)
                            .getOrDefault(formnum,species);
                    pokeOffs += 8;
                    if ((tr.poketype & 2) == 2) {
                        tpk.heldItem = readWord(trpoke, pokeOffs);
                        pokeOffs += 2;
                    }
                    if ((tr.poketype & 1) == 1) {
                        int attack1 = readWord(trpoke, pokeOffs);
                        int attack2 = readWord(trpoke, pokeOffs + 2);
                        int attack3 = readWord(trpoke, pokeOffs + 4);
                        int attack4 = readWord(trpoke, pokeOffs + 6);
                        tpk.move1 = attack1;
                        tpk.move2 = attack2;
                        tpk.move3 = attack3;
                        tpk.move4 = attack4;
                        pokeOffs += 8;
                    }
                    tr.pokemon.add(tpk);
                }
                allTrainers.add(tr);
            }
            if (romEntry.romType == Gen6Constants.Type_XY) {
                Gen6Constants.tagTrainersXY(allTrainers);
                Gen6Constants.setCouldBeMultiBattleXY(allTrainers);
            } else {
                Gen6Constants.tagTrainersORAS(allTrainers);
                Gen6Constants.setCouldBeMultiBattleORAS(allTrainers);
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
    public List<Integer> getEvolutionItems() {
        return Gen6Constants.evolutionItems;
    }

    @Override
    public void setTrainers(List<Trainer> trainerData, boolean doubleBattleMode) {
        Iterator<Trainer> allTrainers = trainerData.iterator();
        boolean isORAS = romEntry.romType == Gen6Constants.Type_ORAS;
        try {
            GARCArchive trainers = this.readGARC(romEntry.getString("TrainerData"),true);
            GARCArchive trpokes = this.readGARC(romEntry.getString("TrainerPokemon"),true);
            // Get current movesets in case we need to reset them for certain
            // trainer mons.
            Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();
            int trainernum = trainers.files.size();
            for (int i = 1; i < trainernum; i++) {
                byte[] trainer = trainers.files.get(i).get(0);
                Trainer tr = allTrainers.next();
                // preserve original poketype for held item & moves
                int offset = 0;
                if (isORAS) {
                    writeWord(trainer,0,tr.poketype);
                    offset = 4;
                } else {
                    trainer[0] = (byte) tr.poketype;
                }
                int numPokes = tr.pokemon.size();
                trainer[offset+3] = (byte) numPokes;

                if (doubleBattleMode) {
                    if (!tr.skipImportant()) {
                        if (trainer[offset+2] == 0) {
                            trainer[offset+2] = 1;
                            trainer[offset+12] |= 0x80; // Flag that needs to be set for trainers not to attack their own pokes
                        }
                    }
                }

                int bytesNeeded = 8 * numPokes;
                if ((tr.poketype & 1) == 1) {
                    bytesNeeded += 8 * numPokes;
                }
                if ((tr.poketype & 2) == 2) {
                    bytesNeeded += 2 * numPokes;
                }
                byte[] trpoke = new byte[bytesNeeded];
                int pokeOffs = 0;
                Iterator<TrainerPokemon> tpokes = tr.pokemon.iterator();
                for (int poke = 0; poke < numPokes; poke++) {
                    TrainerPokemon tp = tpokes.next();
                    trpoke[pokeOffs] = (byte) tp.AILevel;
                    // no gender or ability info, so no byte 1
                    writeWord(trpoke, pokeOffs + 2, tp.level);
                    writeWord(trpoke, pokeOffs + 4, tp.pokemon.number);
                    writeWord(trpoke, pokeOffs + 6, tp.forme);
                    pokeOffs += 8;
                    if ((tr.poketype & 2) == 2) {
                        writeWord(trpoke, pokeOffs, tp.heldItem);
                        pokeOffs += 2;
                    }
                    if ((tr.poketype & 1) == 1) {
                        if (tp.resetMoves) {
                            int[] pokeMoves = RomFunctions.getMovesAtLevel(tp.absolutePokeNumber, movesets, tp.level);
                            for (int m = 0; m < 4; m++) {
                                writeWord(trpoke, pokeOffs + m * 2, pokeMoves[m]);
                            }
                        } else {
                            writeWord(trpoke, pokeOffs, tp.move1);
                            writeWord(trpoke, pokeOffs + 2, tp.move2);
                            writeWord(trpoke, pokeOffs + 4, tp.move3);
                            writeWord(trpoke, pokeOffs + 6, tp.move4);
                        }
                        pokeOffs += 8;
                    }
                }
                trpokes.setFile(i,trpoke);
            }
            this.writeGARC(romEntry.getString("TrainerData"), trainers);
            this.writeGARC(romEntry.getString("TrainerPokemon"), trpokes);
        } catch (IOException ex) {
            throw new RandomizerIOException(ex);
        }
    }

    @Override
    public Map<Integer, List<MoveLearnt>> getMovesLearnt() {
        Map<Integer, List<MoveLearnt>> movesets = new TreeMap<>();
        try {
            GARCArchive movesLearnt = this.readGARC(romEntry.getString("PokemonMovesets"),true);
            int formeCount = Gen6Constants.getFormeCount(romEntry.romType);
//            int formeOffset = Gen5Constants.getFormeMovesetOffset(romEntry.romType);
            for (int i = 1; i <= Gen6Constants.pokemonCount + formeCount; i++) {
                Pokemon pkmn = pokes[i];
                byte[] movedata;
//                if (i > Gen6Constants.pokemonCount) {
//                    movedata = movesLearnt.files.get(i + formeOffset);
//                } else {
//                    movedata = movesLearnt.files.get(i);
//                }
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
            GARCArchive movesLearnt = readGARC(romEntry.getString("PokemonMovesets"),true);
            int formeCount = Gen6Constants.getFormeCount(romEntry.romType);
//            int formeOffset = Gen6Constants.getFormeMovesetOffset(romEntry.romType);
            for (int i = 1; i <= Gen6Constants.pokemonCount + formeCount; i++) {
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
//                if (i > Gen5Constants.pokemonCount) {
//                    movesLearnt.files.set(i + formeOffset, moveset);
//                } else {
//                    movesLearnt.files.set(i, moveset);
//                }
                movesLearnt.setFile(i, moveset);
            }
            // Save
            this.writeGARC(romEntry.getString("PokemonMovesets"), movesLearnt);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

    }

    @Override
    public boolean canChangeStaticPokemon() {
        return romEntry.staticPokemonSupport;
    }

    @Override
    public boolean hasStaticAltFormes() {
        return true;
    }

    @Override
    public List<StaticEncounter> getStaticPokemon() {
        List<StaticEncounter> statics = new ArrayList<>();
        try {
            byte[] staticCRO = readFile(romEntry.getString("StaticPokemon"));

            // Static Pokemon
            int count = Gen6Constants.getStaticPokemonCount(romEntry.romType);
            int size = Gen6Constants.staticPokemonSize;
            int offset = romEntry.getInt("StaticPokemonOffset");
            for (int i = 0; i < count; i++) {
                StaticEncounter se = new StaticEncounter();
                int species = FileFunctions.read2ByteInt(staticCRO,offset+i*size);
                Pokemon pokemon = pokes[species];
                int forme = staticCRO[offset+i*size + 2];
                if (forme > pokemon.cosmeticForms && forme != 30 && forme != 31) {
                    int speciesWithForme = absolutePokeNumByBaseForme
                            .getOrDefault(species, dummyAbsolutePokeNums)
                            .getOrDefault(forme, 0);
                    pokemon = pokes[speciesWithForme];
                }
                se.pkmn = pokemon;
                se.forme = forme;
                se.level = staticCRO[offset+i*size + 3];
                short heldItem = (short)FileFunctions.read2ByteInt(staticCRO,offset+i*size + 4);
                if (heldItem < 0) {
                    heldItem = 0;
                }
                se.heldItem = heldItem;
                se.formeSuffix = Gen6Constants.getFormeSuffixByBaseForme(se.pkmn.number,se.forme);
                statics.add(se);
            }

            List<Integer> skipStarters =
                    Arrays.stream(romEntry.arrayEntries.get("StarterIndices")).boxed().collect(Collectors.toList());

            // Gift Pokemon
            count = Gen6Constants.getGiftPokemonCount(romEntry.romType);
            size = Gen6Constants.getGiftPokemonSize(romEntry.romType);
            offset = romEntry.getInt("GiftPokemonOffset");
            for (int i = 0; i < count; i++) {
                if (skipStarters.contains(i)) continue;
                StaticEncounter se = new StaticEncounter();
                int species = FileFunctions.read2ByteInt(staticCRO,offset+i*size);
                Pokemon pokemon = pokes[species];
                int forme = staticCRO[offset+i*size + 4];
                if (forme > pokemon.cosmeticForms && forme != 30 && forme != 31) {
                    int speciesWithForme = absolutePokeNumByBaseForme
                            .getOrDefault(species, dummyAbsolutePokeNums)
                            .getOrDefault(forme, 0);
                    pokemon = pokes[speciesWithForme];
                }
                se.pkmn = pokemon;
                se.forme = forme;
                se.level = staticCRO[offset+i*size + 5];
                int heldItem = FileFunctions.readFullIntLittleEndian(staticCRO,offset+i*size + 12);
                if (heldItem < 0) {
                    heldItem = 0;
                }
                se.heldItem = heldItem;
                statics.add(se);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        // In XY, there are two Xerneas/Yveltal encounters; one for when you
        // first encounter them, and another for every time after that. For
        // multiple reasons, we don't want these to be different, so delete
        // the second one.
        if (romEntry.romType == Gen6Constants.Type_XY) {
            int[] boxLegendaryOffsets = romEntry.arrayEntries.get("BoxLegendaryOffsets");
            statics.remove(boxLegendaryOffsets[1]);
        }
        return statics;
    }

    @Override
    public boolean setStaticPokemon(List<StaticEncounter> staticPokemon) {
        // Static Pokemon
        try {
            byte[] staticCRO = readFile(romEntry.getString("StaticPokemon"));

            // To match what we did in getStaticPokemon, we have to add the
            // second Xerneas/Yveltal encounter back into the list of statics.
            if (romEntry.romType == Gen6Constants.Type_XY) {
                int[] boxLegendaryOffsets = romEntry.arrayEntries.get("BoxLegendaryOffsets");
                staticPokemon.add(boxLegendaryOffsets[1], staticPokemon.get(boxLegendaryOffsets[0]));
            }

            Iterator<StaticEncounter> staticIter = staticPokemon.iterator();

            int staticCount = Gen6Constants.getStaticPokemonCount(romEntry.romType);
            int size = Gen6Constants.staticPokemonSize;
            int offset = romEntry.getInt("StaticPokemonOffset");
            for (int i = 0; i < staticCount; i++) {
                StaticEncounter se = staticIter.next();
                writeWord(staticCRO,offset+i*size,se.pkmn.number);
                staticCRO[offset+i*size + 2] = (byte)se.forme;
                staticCRO[offset+i*size + 3] = (byte)se.level;
                if (se.heldItem == 0) {
                    writeWord(staticCRO,offset+i*size + 4,-1);
                } else {
                    writeWord(staticCRO,offset+i*size + 4,se.heldItem);
                }
            }

            List<Integer> skipStarters =
                    Arrays.stream(romEntry.arrayEntries.get("StarterIndices")).boxed().collect(Collectors.toList());

            // Gift Pokemon
            int giftCount = Gen6Constants.getGiftPokemonCount(romEntry.romType);
            size = Gen6Constants.getGiftPokemonSize(romEntry.romType);
            offset = romEntry.getInt("GiftPokemonOffset");
            for (int i = 0; i < giftCount; i++) {
                if (skipStarters.contains(i)) continue;
                StaticEncounter se = staticIter.next();
                writeWord(staticCRO,offset+i*size,se.pkmn.number);
                staticCRO[offset+i*size + 4] = (byte)se.forme;
                staticCRO[offset+i*size + 5] = (byte)se.level;
                if (se.heldItem == 0) {
                    writeWord(staticCRO,offset+i*size + 12,-1);
                } else {
                    writeWord(staticCRO,offset+i*size + 12,se.heldItem);
                }
            }
            writeFile(romEntry.getString("StaticPokemon"),staticCRO);

            if (romEntry.romType == Gen6Constants.Type_XY) {
                int[] boxLegendaryOffsets = romEntry.arrayEntries.get("BoxLegendaryOffsets");
                StaticEncounter boxLegendaryEncounter = staticPokemon.get(boxLegendaryOffsets[0]);
                fixBoxLegendariesXY(boxLegendaryEncounter.pkmn.number);
            } else {
                StaticEncounter rayquazaEncounter = staticPokemon.get(romEntry.getInt("RayquazaEncounterNumber"));
                fixRayquazaORAS(rayquazaEncounter.pkmn.number);
            }

            return true;
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void fixBoxLegendariesXY(int boxLegendarySpecies) throws IOException {
        // We need to edit the script file or otherwise the text will still say "Xerneas" or "Yveltal"
        GARCArchive encounterGarc = readGARC(romEntry.getString("WildPokemon"), false);
        byte[] boxLegendaryRoomData = encounterGarc.getFile(Gen6Constants.boxLegendaryEncounterFileXY);
        AMX localScript = new AMX(boxLegendaryRoomData, 1);
        byte[] data = localScript.decData;
        int[] boxLegendaryScriptOffsets = romEntry.arrayEntries.get("BoxLegendaryScriptOffsets");
        for (int i = 0; i < boxLegendaryScriptOffsets.length; i++) {
            FileFunctions.write2ByteInt(data, boxLegendaryScriptOffsets[i], boxLegendarySpecies);
        }
        byte[] modifiedScript = localScript.getBytes();
        System.arraycopy(modifiedScript, 0, boxLegendaryRoomData, Gen6Constants.boxLegendaryLocalScriptOffsetXY, modifiedScript.length);
        encounterGarc.setFile(Gen6Constants.boxLegendaryEncounterFileXY, boxLegendaryRoomData);
        writeGARC(romEntry.getString("WildPokemon"), encounterGarc);

        // We also need to edit DllField.cro so that the hardcoded checks for
        // Xerneas's/Yveltal's ID will instead be checks for our randomized species ID.
        byte[] staticCRO = readFile(romEntry.getString("StaticPokemon"));
        int functionOffset = find(staticCRO, Gen6Constants.boxLegendaryFunctionPrefixXY);
        if (functionOffset > 0) {
            functionOffset += Gen6Constants.boxLegendaryFunctionPrefixXY.length() / 2; // because it was a prefix

            // At multiple points in the function, the game calls pml::pokepara::CoreParam::GetMonNo
            // and compares the result to r8; every single one of these comparisons is followed by a
            // nop. However, the way in which the species ID is loaded into r8 differs depending on
            // the game. We'd prefer to write the same assembly for both games, and there's a trick
            // we can abuse to do so. Since the species ID is never used outside of this comparison,
            // we can feel free to mutate it however we please. The below code allows us to write any
            // arbitrary species ID and make the proper comparison like this:
            // sub r0, r0, (speciesLower x 0x100)
            // subs r0, r0, speciesUpper
            int speciesUpper = boxLegendarySpecies & 0x00FF;
            int speciesLower = (boxLegendarySpecies & 0xFF00) >> 8;
            for (int i = 0; i < Gen6Constants.boxLegendaryCodeOffsetsXY.length; i++) {
                int codeOffset = functionOffset + Gen6Constants.boxLegendaryCodeOffsetsXY[i];
                staticCRO[codeOffset] = (byte) speciesLower;
                staticCRO[codeOffset + 1] = 0x0C;
                staticCRO[codeOffset + 2] = 0x40;
                staticCRO[codeOffset + 3] = (byte) 0xE2;
                staticCRO[codeOffset + 4] = (byte) speciesUpper;
                staticCRO[codeOffset + 5] = 0x00;
                staticCRO[codeOffset + 6] = 0x50;
                staticCRO[codeOffset + 7] = (byte) 0xE2;
            }
        }
        writeFile(romEntry.getString("StaticPokemon"), staticCRO);
    }

    private void fixRayquazaORAS(int rayquazaEncounterSpecies) throws IOException {
        // We need to edit the script file or otherwise the text will still say "Rayquaza"
        int rayquazaScriptFile = romEntry.getInt("RayquazaEncounterScriptNumber");
        GARCArchive scriptGarc = readGARC(romEntry.getString("Scripts"), true);
        AMX rayquazaAMX = new AMX(scriptGarc.files.get(rayquazaScriptFile).get(0));
        byte[] data = rayquazaAMX.decData;
        for (int i = 0; i < Gen6Constants.rayquazaScriptOffsetsORAS.length; i++) {
            FileFunctions.write2ByteInt(data, Gen6Constants.rayquazaScriptOffsetsORAS[i], rayquazaEncounterSpecies);
        }
        scriptGarc.setFile(rayquazaScriptFile, rayquazaAMX.getBytes());
        writeGARC(romEntry.getString("Scripts"), scriptGarc);

        // We also need to edit DllField.cro so that the hardcoded checks for Rayquaza's species
        // ID will instead be checks for our randomized species ID.
        byte[] staticCRO = readFile(romEntry.getString("StaticPokemon"));
        int functionOffset = find(staticCRO, Gen6Constants.rayquazaFunctionPrefixORAS);
        if (functionOffset > 0) {
            functionOffset += Gen6Constants.rayquazaFunctionPrefixORAS.length() / 2; // because it was a prefix

            // Every Rayquaza check consists of "cmp r0, #0x180" followed by a nop. Replace
            // all three checks with a sub and subs instructions so that we can write any
            // random species ID.
            int speciesUpper = rayquazaEncounterSpecies & 0x00FF;
            int speciesLower = (rayquazaEncounterSpecies & 0xFF00) >> 8;
            for (int i = 0; i < Gen6Constants.rayquazaCodeOffsetsORAS.length; i++) {
                int codeOffset = functionOffset + Gen6Constants.rayquazaCodeOffsetsORAS[i];
                staticCRO[codeOffset] = (byte) speciesLower;
                staticCRO[codeOffset + 1] = 0x0C;
                staticCRO[codeOffset + 2] = 0x40;
                staticCRO[codeOffset + 3] = (byte) 0xE2;
                staticCRO[codeOffset + 4] = (byte) speciesUpper;
                staticCRO[codeOffset + 5] = 0x00;
                staticCRO[codeOffset + 6] = 0x50;
                staticCRO[codeOffset + 7] = (byte) 0xE2;
            }
        }
        writeFile(romEntry.getString("StaticPokemon"), staticCRO);
    }

    @Override
    public int miscTweaksAvailable() {
        int available = 0;
        available |= MiscTweak.FASTEST_TEXT.getValue();
        available |= MiscTweak.BAN_LUCKY_EGG.getValue();
        available |= MiscTweak.RETAIN_ALT_FORMES.getValue();
        return available;
    }

    @Override
    public void applyMiscTweak(MiscTweak tweak) {
        if (tweak == MiscTweak.FASTEST_TEXT) {
            applyFastestText();
        } else if (tweak == MiscTweak.BAN_LUCKY_EGG) {
            allowedItems.banSingles(Gen6Constants.luckyEggIndex);
            nonBadItems.banSingles(Gen6Constants.luckyEggIndex);
        } else if (tweak == MiscTweak.RETAIN_ALT_FORMES) {
            try {
                patchFormeReversion();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void applyFastestText() {
        int offset = find(code, Gen6Constants.fastestTextPrefixes[0]);
        if (offset > 0) {
            offset += Gen6Constants.fastestTextPrefixes[0].length() / 2; // because it was a prefix
            code[offset] = 0x03;
            code[offset + 1] = 0x40;
            code[offset + 2] = (byte) 0xA0;
            code[offset + 3] = (byte) 0xE3;
        }
        offset = find(code, Gen6Constants.fastestTextPrefixes[1]);
        if (offset > 0) {
            offset += Gen6Constants.fastestTextPrefixes[1].length() / 2; // because it was a prefix
            code[offset] = 0x03;
            code[offset + 1] = 0x50;
            code[offset + 2] = (byte) 0xA0;
            code[offset + 3] = (byte) 0xE3;
        }
    }

    @Override
    public List<Integer> getTMMoves() {
        String tmDataPrefix = Gen6Constants.tmDataPrefix;
        int offset = find(code, tmDataPrefix);
        if (offset != 0) {
            offset += Gen6Constants.tmDataPrefix.length() / 2; // because it was a prefix
            List<Integer> tms = new ArrayList<>();
            for (int i = 0; i < Gen6Constants.tmBlockOneCount; i++) {
                tms.add(readWord(code, offset + i * 2));
            }
            offset += (Gen6Constants.getTMBlockTwoStartingOffset(romEntry.romType) * 2);
            for (int i = 0; i < (Gen6Constants.tmCount - Gen6Constants.tmBlockOneCount); i++) {
                tms.add(readWord(code, offset + i * 2));
            }
            return tms;
        } else {
            return null;
        }
    }

    @Override
    public List<Integer> getHMMoves() {
        String tmDataPrefix = Gen6Constants.tmDataPrefix;
        int offset = find(code, tmDataPrefix);
        if (offset != 0) {
            offset += Gen6Constants.tmDataPrefix.length() / 2; // because it was a prefix
            offset += Gen6Constants.tmBlockOneCount * 2; // TM data
            List<Integer> hms = new ArrayList<>();
            for (int i = 0; i < Gen6Constants.hmBlockOneCount; i++) {
                hms.add(readWord(code, offset + i * 2));
            }
            if (romEntry.romType == Gen6Constants.Type_ORAS) {
                hms.add(readWord(code, offset + Gen6Constants.rockSmashOffsetORAS));
                hms.add(readWord(code, offset + Gen6Constants.diveOffsetORAS));
            }
            return hms;
        } else {
            return null;
        }
    }

    @Override
    public void setTMMoves(List<Integer> moveIndexes) {
        String tmDataPrefix = Gen6Constants.tmDataPrefix;
        int offset = find(code, tmDataPrefix);
        if (offset > 0) {
            offset += Gen6Constants.tmDataPrefix.length() / 2; // because it was a prefix
            for (int i = 0; i < Gen6Constants.tmBlockOneCount; i++) {
                writeWord(code, offset + i * 2, moveIndexes.get(i));
            }
            offset += (Gen6Constants.getTMBlockTwoStartingOffset(romEntry.romType) * 2);
            for (int i = 0; i < (Gen6Constants.tmCount - Gen6Constants.tmBlockOneCount); i++) {
                writeWord(code, offset + i * 2, moveIndexes.get(i + Gen6Constants.tmBlockOneCount));
            }

            // Update TM item descriptions
            List<String> itemDescriptions = getStrings(false, romEntry.getInt("ItemDescriptionsTextOffset"));
            List<String> moveDescriptions = getStrings(false, romEntry.getInt("MoveDescriptionsTextOffset"));
            // TM01 is item 328 and so on
            for (int i = 0; i < Gen6Constants.tmBlockOneCount; i++) {
                itemDescriptions.set(i + Gen6Constants.tmBlockOneOffset, moveDescriptions.get(moveIndexes.get(i)));
            }
            // TM93-95 are 618-620
            for (int i = 0; i < Gen6Constants.tmBlockTwoCount; i++) {
                itemDescriptions.set(i + Gen6Constants.tmBlockTwoOffset,
                        moveDescriptions.get(moveIndexes.get(i + Gen6Constants.tmBlockOneCount)));
            }
            // TM96-100 are 690 and so on
            for (int i = 0; i < Gen6Constants.tmBlockThreeCount; i++) {
                itemDescriptions.set(i + Gen6Constants.tmBlockThreeOffset,
                        moveDescriptions.get(moveIndexes.get(i + Gen6Constants.tmBlockOneCount + Gen6Constants.tmBlockTwoCount)));
            }
            // Save the new item descriptions
            setStrings(false, romEntry.getInt("ItemDescriptionsTextOffset"), itemDescriptions);
            // Palettes
            String palettePrefix = Gen6Constants.itemPalettesPrefix;
            int offsPals = find(code, palettePrefix);
            if (offsPals > 0) {
                offsPals += Gen6Constants.itemPalettesPrefix.length() / 2; // because it was a prefix
                // Write pals
                for (int i = 0; i < Gen6Constants.tmBlockOneCount; i++) {
                    int itmNum = Gen6Constants.tmBlockOneOffset + i;
                    Move m = this.moves[moveIndexes.get(i)];
                    int pal = this.typeTMPaletteNumber(m.type, false);
                    writeWord(code, offsPals + itmNum * 4, pal);
                }
                for (int i = 0; i < (Gen6Constants.tmBlockTwoCount); i++) {
                    int itmNum = Gen6Constants.tmBlockTwoOffset + i;
                    Move m = this.moves[moveIndexes.get(i + Gen6Constants.tmBlockOneCount)];
                    int pal = this.typeTMPaletteNumber(m.type, false);
                    writeWord(code, offsPals + itmNum * 4, pal);
                }
                for (int i = 0; i < (Gen6Constants.tmBlockThreeCount); i++) {
                    int itmNum = Gen6Constants.tmBlockThreeOffset + i;
                    Move m = this.moves[moveIndexes.get(i + Gen6Constants.tmBlockOneCount + Gen6Constants.tmBlockTwoCount)];
                    int pal = this.typeTMPaletteNumber(m.type, false);
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
        return Gen6Constants.tmCount;
    }

    @Override
    public int getHMCount() {
        return Gen6Constants.getHMCount(romEntry.romType);
    }

    @Override
    public Map<Pokemon, boolean[]> getTMHMCompatibility() {
        Map<Pokemon, boolean[]> compat = new TreeMap<>();
        int formeCount = Gen6Constants.getFormeCount(romEntry.romType);
        for (int i = 1; i <= Gen6Constants.pokemonCount + formeCount; i++) {
            byte[] data;
            data = pokeGarc.files.get(i).get(0);
            Pokemon pkmn = pokes[i];
            boolean[] flags = new boolean[Gen6Constants.tmCount + Gen6Constants.getHMCount(romEntry.romType) + 1];
            for (int j = 0; j < 14; j++) {
                readByteIntoFlags(data, flags, j * 8 + 1, Gen6Constants.bsTMHMCompatOffset + j);
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
            for (int j = 0; j < 14; j++) {
                data[Gen6Constants.bsTMHMCompatOffset + j] = getByteFromFlags(flags, j * 8 + 1);
            }
        }
    }

    @Override
    public boolean hasMoveTutors() {
        return romEntry.romType == Gen6Constants.Type_ORAS;
    }

    @Override
    public List<Integer> getMoveTutorMoves() {
        List<Integer> mtMoves = new ArrayList<>();

        int mtOffset = getMoveTutorMovesOffset();
        if (mtOffset > 0) {
            int val = 0;
            while (val != 0xFFFF) {
                val = FileFunctions.read2ByteInt(code,mtOffset);
                mtOffset += 2;
                if (val == 0x26E || val == 0xFFFF) continue;
                mtMoves.add(val);
            }
        }

        return mtMoves;
    }

    @Override
    public void setMoveTutorMoves(List<Integer> moves) {

        int mtOffset = find(code, Gen6Constants.tutorsShopPrefix);
        if (mtOffset > 0) {
            mtOffset += Gen6Constants.tutorsShopPrefix.length() / 2; // because it was a prefix
            for (int i = 0; i < Gen6Constants.tutorMoveCount; i++) {
                FileFunctions.write2ByteInt(code,mtOffset + i*8, moves.get(i));
            }
        }

        mtOffset = getMoveTutorMovesOffset();
        if (mtOffset > 0) {
            for (int move: moves) {
                int val = FileFunctions.read2ByteInt(code,mtOffset);
                if (val == 0x26E) mtOffset += 2;
                FileFunctions.write2ByteInt(code,mtOffset,move);
                mtOffset += 2;
            }
        }
    }

    private int getMoveTutorMovesOffset() {
        int offset = moveTutorMovesOffset;
        if (offset == 0) {
            offset = find(code, Gen6Constants.tutorsLocator);
            moveTutorMovesOffset = offset;
        }
        return offset;
    }

    @Override
    public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
        Map<Pokemon, boolean[]> compat = new TreeMap<>();
        int[] sizes = Gen6Constants.tutorSize;
        int formeCount = Gen6Constants.getFormeCount(romEntry.romType);
        for (int i = 1; i <= Gen6Constants.pokemonCount + formeCount; i++) {
            byte[] data;
            data = pokeGarc.files.get(i).get(0);
            Pokemon pkmn = pokes[i];
            boolean[] flags = new boolean[Arrays.stream(sizes).sum() + 1];
            int offset = 0;
            for (int mt = 0; mt < 4; mt++) {
                for (int j = 0; j < 4; j++) {
                    readByteIntoFlags(data, flags, offset + j * 8 + 1, Gen6Constants.bsMTCompatOffset + mt * 4 + j);
                }
                offset += sizes[mt];
            }
            compat.put(pkmn, flags);
        }
        return compat;
    }

    @Override
    public void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData) {
        if (!hasMoveTutors()) return;
        int[] sizes = Gen6Constants.tutorSize;
        int formeCount = Gen6Constants.getFormeCount(romEntry.romType);
        for (int i = 1; i <= Gen6Constants.pokemonCount + formeCount; i++) {
            byte[] data;
            data = pokeGarc.files.get(i).get(0);
            Pokemon pkmn = pokes[i];
            boolean[] flags = compatData.get(pkmn);
            int offset = 0;
            for (int mt = 0; mt < 4; mt++) {
                boolean[] mtflags = new boolean[sizes[mt] + 1];
                System.arraycopy(flags, offset + 1, mtflags, 1, sizes[mt]);
                for (int j = 0; j < 4; j++) {
                    data[Gen6Constants.bsMTCompatOffset + mt * 4 + j] = getByteFromFlags(mtflags, j * 8 + 1);
                }
                offset += sizes[mt];
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
        return false;
    }

    @Override
    public List<Integer> getMovesBannedFromLevelup() {
        return Gen6Constants.bannedMoves;
    }

    @Override
    public boolean hasWildAltFormes() {
        return true;
    }

    @Override
    public List<Pokemon> bannedForStaticPokemon() {
        return Gen6Constants.actuallyCosmeticForms
                .stream()
                .filter(index -> index < Gen6Constants.pokemonCount + Gen6Constants.getFormeCount(romEntry.romType))
                .map(index -> pokes[index])
                .collect(Collectors.toList());
    }

    @Override
    public boolean forceSwapStaticMegaEvos() {
        return romEntry.romType == Gen6Constants.Type_XY;
    }

    @Override
    public List<TotemPokemon> getTotemPokemon() {
        return new ArrayList<>();
    }

    @Override
    public void setTotemPokemon(List<TotemPokemon> totemPokemon) {

    }

    @Override
    public void removeImpossibleEvolutions(boolean changeMoveEvos) {
        Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();
        log("--Removing Impossible Evolutions--");
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
                        logEvoChangeLevel(evo.from.fullName(), evo.to.fullName(), levelLearntAt);
                    }
                    // Pure Trade
                    if (evo.type == EvolutionType.TRADE) {
                        // Replace w/ level 37
                        evo.type = EvolutionType.LEVEL;
                        evo.extraInfo = 37;
                        logEvoChangeLevel(evo.from.fullName(), evo.to.fullName(), 37);
                    }
                    // Trade w/ Item
                    if (evo.type == EvolutionType.TRADE_ITEM) {
                        // Get the current item & evolution
                        int item = evo.extraInfo;
                        if (evo.from.number == Gen6Constants.slowpokeIndex) {
                            // Slowpoke is awkward - he already has a level evo
                            // So we can't do Level up w/ Held Item for him
                            // Put Water Stone instead
                            evo.type = EvolutionType.STONE;
                            evo.extraInfo = Gen6Constants.waterStoneIndex; // water
                            // stone
                            logEvoChangeStone(evo.from.fullName(), evo.to.fullName(), itemNames.get(Gen6Constants.waterStoneIndex));
                        } else {
                            logEvoChangeLevelWithItem(evo.from.fullName(), evo.to.fullName(), itemNames.get(item));
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
                        evo.extraInfo = (evo.from.number == Gen6Constants.karrablastIndex ? Gen6Constants.shelmetIndex
                                : Gen6Constants.karrablastIndex);
                        logEvoChangeLevelWithPkmn(evo.from.fullName(), evo.to.fullName(),
                                pokes[(evo.from.number == Gen6Constants.karrablastIndex ? Gen6Constants.shelmetIndex
                                        : Gen6Constants.karrablastIndex)].fullName());
                    }
                    // TBD: Pancham, Sliggoo? Sylveon?
                }

                pkmn.evolutionsFrom.addAll(extraEvolutions);
                for (Evolution ev : extraEvolutions) {
                    ev.to.evolutionsTo.add(ev);
                }
            }
        }
        logBlankLine();
    }

    @Override
    public void makeEvolutionsEasier(boolean wildsRandomized) {
        if (wildsRandomized) {
            for (Pokemon pkmn : pokes) {
                if (pkmn != null) {
                    for (Evolution evo : pkmn.evolutionsFrom) {
                        if (evo.type == EvolutionType.LEVEL_WITH_OTHER) {
                            // Replace w/ level 35
                            evo.type = EvolutionType.LEVEL;
                            evo.extraInfo = 35;
                            log(String.format("%s now evolves into %s at minimum level %d", evo.from.fullName(), evo.to.fullName(),
                                    evo.extraInfo));
                        }
                    }
                }
            }
            logBlankLine();
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
    }

    @Override
    public int maxTrainerClassNameLength() {
        return 15; // "Pokémon Breeder" is possible, so,
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
        return Gen6Constants.getHighestAbilityIndex(romEntry.romType);
    }

    @Override
    public int internalStringLength(String string) {
        return string.length();
    }

    @Override
    public void applySignature() {
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
    public List<Integer> getRegularShopItems() {
        return Gen6Constants.regularShopItems;
    }

    @Override
    public List<Integer> getOPShopItems() {
        return Gen6Constants.opShopItems;
    }

    @Override
    public String[] getItemNames() {
        return itemNames.toArray(new String[0]);
    }

    @Override
    public String[] getShopNames() {
        return shopNames.toArray(new String[0]);
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
    public boolean hasMegaEvolutions() {
        return true;
    }

    private int tmFromIndex(int index) {

        if (index >= Gen6Constants.tmBlockOneOffset
                && index < Gen6Constants.tmBlockOneOffset + Gen6Constants.tmBlockOneCount) {
            return index - (Gen6Constants.tmBlockOneOffset - 1);
        } else if (index >= Gen6Constants.tmBlockTwoOffset
                && index < Gen6Constants.tmBlockTwoOffset + Gen6Constants.tmBlockTwoCount) {
            return (index + Gen6Constants.tmBlockOneCount) - (Gen6Constants.tmBlockTwoOffset - 1);
        } else {
            return (index + Gen6Constants.tmBlockOneCount + Gen6Constants.tmBlockTwoCount) - (Gen6Constants.tmBlockThreeOffset - 1);
        }
    }

    private int indexFromTM(int tm) {
        if (tm >= 1 && tm <= Gen6Constants.tmBlockOneCount) {
            return tm + (Gen6Constants.tmBlockOneOffset - 1);
        } else if (tm > Gen6Constants.tmBlockOneCount && tm <= Gen6Constants.tmBlockOneCount + Gen6Constants.tmBlockTwoCount) {
            return tm + (Gen6Constants.tmBlockTwoOffset - 1 - Gen6Constants.tmBlockOneCount);
        } else {
            return tm + (Gen6Constants.tmBlockThreeOffset - 1 - (Gen6Constants.tmBlockOneCount + Gen6Constants.tmBlockTwoCount));
        }
    }

    @Override
    public List<Integer> getCurrentFieldTMs() {
        List<Integer> fieldItems = this.getFieldItems();
        List<Integer> fieldTMs = new ArrayList<>();

        ItemList allowedItems = Gen6Constants.getAllowedItems(romEntry.romType);
        for (int item : fieldItems) {
            if (allowedItems.isTM(item)) {
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

        ItemList allowedItems = Gen6Constants.getAllowedItems(romEntry.romType);
        for (int i = 0; i < fiLength; i++) {
            int oldItem = fieldItems.get(i);
            if (allowedItems.isTM(oldItem)) {
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

        ItemList allowedItems = Gen6Constants.getAllowedItems(romEntry.romType);
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

        ItemList allowedItems = Gen6Constants.getAllowedItems(romEntry.romType);
        for (int i = 0; i < fiLength; i++) {
            int oldItem = fieldItems.get(i);
            if (!(allowedItems.isTM(oldItem)) && allowedItems.isAllowed(oldItem) && oldItem != Gen6Constants.masterBallIndex) {
                int newItem = iterNewItems.next();
                fieldItems.set(i, newItem);
            }
        }

        this.setFieldItems(fieldItems);
    }

    @Override
    public List<Integer> getRequiredFieldTMs() {
        return Gen6Constants.getRequiredFieldTMs(romEntry.romType);
    }

    public List<Integer> getFieldItems() {
        List<Integer> fieldItems = new ArrayList<>();
        try {
            // normal items
            int normalItemsFile = romEntry.getInt("FieldItemsScriptNumber");
            int normalItemsOffset = romEntry.getInt("FieldItemsOffset");
            GARCArchive scriptGarc = readGARC(romEntry.getString("Scripts"),true);
            AMX normalItemAMX = new AMX(scriptGarc.files.get(normalItemsFile).get(0));
            byte[] data = normalItemAMX.decData;
            for (int i = normalItemsOffset; i < data.length; i += 12) {
                int item = FileFunctions.read2ByteInt(data,i);
                fieldItems.add(item);
            }

            // hidden items - separate handling for XY and ORAS
            if (romEntry.romType == Gen6Constants.Type_XY) {
                int hiddenItemsFile = romEntry.getInt("HiddenItemsScriptNumber");
                int hiddenItemsOffset = romEntry.getInt("HiddenItemsOffset");
                AMX hiddenItemAMX = new AMX(scriptGarc.files.get(hiddenItemsFile).get(0));
                data = hiddenItemAMX.decData;
                for (int i = hiddenItemsOffset; i < data.length; i += 12) {
                    int item = FileFunctions.read2ByteInt(data,i);
                    fieldItems.add(item);
                }
            } else {
                String hiddenItemsPrefix = Gen6Constants.hiddenItemsPrefixORAS;
                int offsHidden = find(code,hiddenItemsPrefix);
                if (offsHidden > 0) {
                    offsHidden += hiddenItemsPrefix.length() / 2;
                    for (int i = 0; i < Gen6Constants.hiddenItemCountORAS; i++) {
                        int item = FileFunctions.read2ByteInt(code, offsHidden + (i * 0xE) + 2);
                        fieldItems.add(item);
                    }
                }
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        return fieldItems;
    }

    public void setFieldItems(List<Integer> items) {
        try {
            Iterator<Integer> iterItems = items.iterator();
            // normal items
            int normalItemsFile = romEntry.getInt("FieldItemsScriptNumber");
            int normalItemsOffset = romEntry.getInt("FieldItemsOffset");
            GARCArchive scriptGarc = readGARC(romEntry.getString("Scripts"),true);
            AMX normalItemAMX = new AMX(scriptGarc.files.get(normalItemsFile).get(0));
            byte[] data = normalItemAMX.decData;
            for (int i = normalItemsOffset; i < data.length; i += 12) {
                int item = iterItems.next();
                FileFunctions.write2ByteInt(data,i,item);
            }
            scriptGarc.setFile(normalItemsFile,normalItemAMX.getBytes());

            // hidden items - separate handling for XY and ORAS
            if (romEntry.romType == Gen6Constants.Type_XY) {
                int hiddenItemsFile = romEntry.getInt("HiddenItemsScriptNumber");
                int hiddenItemsOffset = romEntry.getInt("HiddenItemsOffset");
                AMX hiddenItemAMX = new AMX(scriptGarc.files.get(hiddenItemsFile).get(0));
                data = hiddenItemAMX.decData;
                for (int i = hiddenItemsOffset; i < data.length; i += 12) {
                    int item = iterItems.next();
                    FileFunctions.write2ByteInt(data,i,item);
                }
                scriptGarc.setFile(hiddenItemsFile,hiddenItemAMX.getBytes());
            } else {
                String hiddenItemsPrefix = Gen6Constants.hiddenItemsPrefixORAS;
                int offsHidden = find(code,hiddenItemsPrefix);
                if (offsHidden > 0) {
                    offsHidden += hiddenItemsPrefix.length() / 2;
                    for (int i = 0; i < Gen6Constants.hiddenItemCountORAS; i++) {
                        int item = iterItems.next();
                        FileFunctions.write2ByteInt(code,offsHidden + (i * 0xE) + 2, item);
                    }
                }
            }

            writeGARC(romEntry.getString("Scripts"),scriptGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public List<IngameTrade> getIngameTrades() {
        List<IngameTrade> trades = new ArrayList<>();

        int count = romEntry.getInt("IngameTradeCount");
        String prefix = Gen6Constants.getIngameTradesPrefix(romEntry.romType);
        List<String> tradeStrings = getStrings(false, romEntry.getInt("IngameTradesTextOffset"));
        int textOffset = romEntry.getInt("IngameTradesTextExtraOffset");
        int offset = find(code,prefix);
        if (offset > 0) {
            offset += prefix.length() / 2;
            for (int i = 0; i < count; i++) {
                IngameTrade trade = new IngameTrade();
                trade.nickname = tradeStrings.get(textOffset + i);
                trade.givenPokemon = pokes[FileFunctions.read2ByteInt(code,offset)];
                trade.ivs = new int[6];
                for (int iv = 0; iv < 6; iv++) {
                    trade.ivs[iv] = code[offset + 5 + iv];
                }
                trade.otId = FileFunctions.read2ByteInt(code,offset + 0xE);
                trade.item = FileFunctions.read2ByteInt(code,offset + 0x10);
                trade.otName = tradeStrings.get(textOffset + count + i);
                trade.requestedPokemon = pokes[FileFunctions.read2ByteInt(code,offset + 0x20)];
                trades.add(trade);
                offset += Gen6Constants.ingameTradeSize;
            }
        }
        return trades;
    }

    @Override
    public void setIngameTrades(List<IngameTrade> trades) {

        int count = romEntry.getInt("IngameTradeCount");
        String prefix = Gen6Constants.getIngameTradesPrefix(romEntry.romType);
        List<String> tradeStrings = getStrings(false, romEntry.getInt("IngameTradesTextOffset"));
        int textOffset = romEntry.getInt("IngameTradesTextExtraOffset");
        int offset = find(code,prefix);
        if (offset > 0) {
            offset += prefix.length() / 2;
            for (int i = 0; i < count; i++) {
                IngameTrade trade = trades.get(i);
                tradeStrings.set(textOffset + i, trade.nickname);
                FileFunctions.write2ByteInt(code,offset,trade.givenPokemon.number);
                for (int iv = 0; iv < 6; iv++) {
                    code[offset + 5 + iv] = (byte)trade.ivs[iv];
                }
                FileFunctions.write2ByteInt(code,offset + 0xE,trade.otId);
                FileFunctions.write2ByteInt(code,offset + 0x10,trade.item);
                tradeStrings.set(textOffset + count + i, trade.otName);
                FileFunctions.write2ByteInt(code,offset + 0x20,
                        trade.requestedPokemon == null ? 0 : trade.requestedPokemon.number);
                offset += Gen6Constants.ingameTradeSize;
            }
            this.setStrings(false, romEntry.getInt("IngameTradesTextOffset"), tradeStrings);
        }
    }

    @Override
    public boolean hasDVs() {
        return false;
    }

    @Override
    public int generationOfPokemon() {
        return 6;
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
            GARCArchive babyGarc = readGARC(romEntry.getString("BabyPokemon"), true);
            byte[] masterFile = babyGarc.getFile(Gen6Constants.pokemonCount + 1);
            for (int i = 1; i <= Gen6Constants.pokemonCount; i++) {
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
            babyGarc.setFile(Gen6Constants.pokemonCount + 1, masterFile);
            writeGARC(romEntry.getString("BabyPokemon"), babyGarc);
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
        if (romEntry.romType == Gen6Constants.Type_XY) {
            return Gen6Constants.fieldMovesXY;
        } else {
            return Gen6Constants.fieldMovesORAS;
        }
    }

    @Override
    public List<Integer> getEarlyRequiredHMMoves() {
        return new ArrayList<>();
    }

    @Override
    public Map<Integer, List<Integer>> getShopItems() {
        int[] tmShops = romEntry.arrayEntries.get("TMShops");
        int[] regularShops = romEntry.arrayEntries.get("RegularShops");
        int[] shopItemSizes = romEntry.arrayEntries.get("ShopItemSizes");
        int shopCount = romEntry.getInt("ShopCount");
        Map<Integer,List<Integer>> shopItemsMap = new TreeMap<>();

        int offset = getShopItemsOffset();
        if (offset <= 0) {
            return shopItemsMap;
        }
        for (int i = 0; i < shopCount; i++) {
            boolean badShop = false;
            for (int tmShop: tmShops) {
                if (i == tmShop) {
                    badShop = true;
                    offset += (shopItemSizes[i] * 2);
                    break;
                }
            }
            for (int regularShop: regularShops) {
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
                    items.add(FileFunctions.read2ByteInt(code,offset));
                    offset += 2;
                }
                shopItemsMap.put(i,items);
            }
        }
        return shopItemsMap;
    }

    @Override
    public void setShopItems(Map<Integer, List<Integer>> shopItems) {
        int[] shopItemSizes = romEntry.arrayEntries.get("ShopItemSizes");
        int[] tmShops = romEntry.arrayEntries.get("TMShops");
        int[] regularShops = romEntry.arrayEntries.get("RegularShops");
        int shopCount = romEntry.getInt("ShopCount");

        int offset = getShopItemsOffset();
        if (offset <= 0) {
            return;
        }
        for (int i = 0; i < shopCount; i++) {
            boolean badShop = false;
            for (int tmShop: tmShops) {
                if (badShop) break;
                if (i == tmShop) {
                    badShop = true;
                    offset += (shopItemSizes[i] * 2);
                    break;
                }
            }
            for (int regularShop: regularShops) {
                if (badShop) break;
                if (i == regularShop) {
                    badShop = true;
                    offset += (shopItemSizes[i] * 2);
                    break;
                }
            }
            if (!badShop) {
                List<Integer> shopContents = shopItems.get(i);
                Iterator<Integer> iterItems = shopContents.iterator();
                for (int j = 0; j < shopItemSizes[i]; j++) {
                    Integer item = iterItems.next();
                    FileFunctions.write2ByteInt(code,offset,item);
                    offset += 2;
                }
            }
        }
    }

    private int getShopItemsOffset() {
        int offset = shopItemsOffset;
        if (offset == 0) {
            String locator = Gen6Constants.getShopItemsLocator(romEntry.romType);
            offset = find(code, locator);
            shopItemsOffset = offset;
        }
        return offset;
    }

    @Override
    public void setShopPrices() {
        try {
            GARCArchive itemPriceGarc = this.readGARC(romEntry.getString("ItemData"),true);
            for (int i = 1; i < itemPriceGarc.files.size(); i++) {
                writeWord(itemPriceGarc.files.get(i).get(0),0,Gen6Constants.balancedItemPrices.get(i));
            }
            writeGARC(romEntry.getString("ItemData"),itemPriceGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public List<Integer> getMainGameShops() {
        return Gen6Constants.getMainGameShops(romEntry.romType);
    }

    @Override
    public int randomHeldItem() {
        return 0;
    }

    @Override
    public BufferedImage getMascotImage() {
        try {
            GARCArchive pokespritesGARC = this.readGARC(romEntry.getString("PokemonGraphics"),false);
            int pkIndex = this.random.nextInt(pokespritesGARC.files.size()-2)+1;

            byte[] icon = pokespritesGARC.files.get(pkIndex).get(0);
            int paletteCount = readWord(icon,2);
            byte[] rawPalette = Arrays.copyOfRange(icon,4,4+paletteCount*2);
            int[] palette = new int[paletteCount];
            for (int i = 0; i < paletteCount; i++) {
                palette[i] = GFXFunctions.conv3DS16BitColorToARGB(readWord(rawPalette, i * 2));
            }

            int width = 64;
            int height = 32;
            // Get the picture and uncompress it.
            byte[] uncompressedPic = Arrays.copyOfRange(icon,4+paletteCount*2,4+paletteCount*2+width*height);

            int bpp = paletteCount <= 0x10 ? 4 : 8;
            // Output to 64x144 tiled image to prepare for unscrambling
            BufferedImage bim = GFXFunctions.drawTiledZOrderImage(uncompressedPic, palette, 0, width, height, bpp);

            // Unscramble the above onto a 96x96 canvas
            BufferedImage finalImage = new BufferedImage(40, 30, BufferedImage.TYPE_INT_ARGB);
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
}
