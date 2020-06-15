package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Gen6RomHandler.java - randomizer handler for X/Y/OR/AS.               --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew                   --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2012.                                 --*/
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
import com.dabomstew.pkrandom.constants.Gen6Constants;
import com.dabomstew.pkrandom.exceptions.RandomizerIOException;
import com.dabomstew.pkrandom.pokemon.*;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

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

    private static class RomEntry {
        private String name;
        private String romCode;
        private String titleId;
        private int romType;
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
                        }
                    }
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
        }
    }

    // This ROM
    private Pokemon[] pokes;
    private List<Pokemon> pokemonList;
    private RomEntry romEntry;
    private byte[] code;

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

        // TODO: Actually make this work by loading it from the ROM. Only doing it this
        // way temporarily so the randomizer won't crash
        pokes = new Pokemon[Gen6Constants.pokemonCount + 1];
        for (int i = 1; i <= Gen6Constants.pokemonCount; i++) {
            pokes[i] = new Pokemon();
            pokes[i].number = i;
        }

        pokemonList = Arrays.asList(Arrays.copyOfRange(pokes,0,Gen6Constants.pokemonCount + 1));
    }

    @Override
    protected void savingROM() throws IOException {
        // do nothing for now
    }

    @Override
    public List<Pokemon> getPokemon() {
        return pokemonList;
    }

    @Override
    public List<Pokemon> getPokemonInclFormes() {
        // TODO: Actually make this work by loading it from the ROM. Only doing it this
        // way temporarily so the randomizer won't crash when trying to write an output ROM.
        ArrayList<Pokemon> pokemonInclFormes = new ArrayList<>();
        pokemonInclFormes.add(pokes[0]);
        return pokemonInclFormes;
    }

    @Override
    public List<Pokemon> getAltFormes() {
        return new ArrayList<>();
    }

    @Override
    public List<Pokemon> getStarters() {
        // TODO: Actually make this work by loading it from the ROM. Only doing it this
        // way temporarily so the randomizer won't crash
        List<Pokemon> starters = new ArrayList<>();
        if (romEntry.romType == Gen6Constants.Type_XY) {
            starters.add(pokes[650]);
            starters.add(pokes[653]);
            starters.add(pokes[656]);
        } else {
            starters.add(pokes[252]);
            starters.add(pokes[255]);
            starters.add(pokes[258]);
        }
        return starters;
    }

    @Override
    public boolean setStarters(List<Pokemon> newStarters) {
        return false;
    }

    @Override
    public List<Integer> getStarterHeldItems() {
        return new ArrayList<>();
    }

    @Override
    public void setStarterHeldItems(List<Integer> items) {
        // do nothing for now
    }

    @Override
    public List<Move> getMoves() {
        return new ArrayList<>();
    }

    @Override
    public List<EncounterSet> getEncounters(boolean useTimeOfDay) {
        return new ArrayList<>();
    }

    @Override
    public void setEncounters(boolean useTimeOfDay, List<EncounterSet> encountersList) {
        // do nothing for now
    }

    @Override
    public List<Trainer> getTrainers() {
        return new ArrayList<>();
    }

    @Override
    public List<Integer> getMainPlaythroughTrainers() {
        return new ArrayList<>();
    }

    @Override
    public List<Integer> getEvolutionItems() {
        return new ArrayList<>();
    }

    @Override
    public void setTrainers(List<Trainer> trainerData) {
        // do nothing for now
    }

    @Override
    public Map<Integer, List<MoveLearnt>> getMovesLearnt() {
        return new TreeMap<>();
    }

    @Override
    public void setMovesLearnt(Map<Integer, List<MoveLearnt>> movesets) {
        // do nothing for now
    }

    @Override
    public boolean canChangeStaticPokemon() {
        return false;
    }

    @Override
    public List<Pokemon> getStaticPokemon() {
        return new ArrayList<>();
    }

    @Override
    public boolean setStaticPokemon(List<Pokemon> staticPokemon) {
        return false;
    }

    @Override
    public int miscTweaksAvailable() {
        return 0;
    }

    @Override
    public void applyMiscTweak(MiscTweak tweak) {
        // do nothing for now
    }

    @Override
    public List<Integer> getTMMoves() {
        return new ArrayList<>();
    }

    @Override
    public List<Integer> getHMMoves() {
        return new ArrayList<>();
    }

    @Override
    public void setTMMoves(List<Integer> moveIndexes) {
        // do nothing for now
    }

    @Override
    public int getTMCount() {
        return 0;
    }

    @Override
    public int getHMCount() {
        return 0;
    }

    @Override
    public Map<Pokemon, boolean[]> getTMHMCompatibility() {
        return new TreeMap<>();
    }

    @Override
    public void setTMHMCompatibility(Map<Pokemon, boolean[]> compatData) {
        // do nothing for now
    }

    @Override
    public boolean hasMoveTutors() {
        return false;
    }

    @Override
    public List<Integer> getMoveTutorMoves() {
        return new ArrayList<>();
    }

    @Override
    public void setMoveTutorMoves(List<Integer> moves) {
        // do nothing for now
    }

    @Override
    public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
        return new TreeMap<>();
    }

    @Override
    public void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData) {
        // do nothing for now
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
        return "None";
    }

    @Override
    public boolean hasTimeBasedEncounters() {
        return false;
    }

    @Override
    public void removeTradeEvolutions(boolean changeMoveEvos) {
        // do nothing for now
    }

    @Override
    public void removePartyEvolutions() {
        // do nothing for now
    }

    @Override
    public boolean hasShopRandomization() {
        return false;
    }

    @Override
    public boolean canChangeTrainerText() {
        return false;
    }

    @Override
    public List<String> getTrainerNames() {
        return new ArrayList<>();
    }

    @Override
    public int maxTrainerNameLength() {
        return 0;
    }

    @Override
    public void setTrainerNames(List<String> trainerNames) {
        // do nothing for now
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
        return new ArrayList<>();
    }

    @Override
    public void setTrainerClassNames(List<String> trainerClassNames) {
        // do nothing for now
    }

    @Override
    public int maxTrainerClassNameLength() {
        return 0;
    }

    @Override
    public boolean fixedTrainerClassNamesLength() {
        return false;
    }

    @Override
    public List<Integer> getDoublesTrainerClasses() {
        return new ArrayList<>();
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
        return 0;
    }

    @Override
    public void applySignature() {
        // For now, do nothing.
    }

    @Override
    public ItemList getAllowedItems() {
        return null;
    }

    @Override
    public ItemList getNonBadItems() {
        return null;
    }

    @Override
    public List<Integer> getRegularShopItems() {
        return null;
    }

    @Override
    public List<Integer> getOPShopItems() {
        return null;
    }

    @Override
    public String[] getItemNames() {
        return new String[0];
    }

    @Override
    public String[] getShopNames() {
        return new String[0];
    }

    @Override
    public String abilityName(int number) {
        return null;
    }

    @Override
    public List<Integer> getCurrentFieldTMs() {
        return new ArrayList<>();
    }

    @Override
    public void setFieldTMs(List<Integer> fieldTMs) {
        // do nothing for now
    }

    @Override
    public List<Integer> getRegularFieldItems() {
        return new ArrayList<>();
    }

    @Override
    public void setRegularFieldItems(List<Integer> items) {
        // do nothing for now
    }

    @Override
    public List<Integer> getRequiredFieldTMs() {
        return new ArrayList<>();
    }

    @Override
    public List<IngameTrade> getIngameTrades() {
        return new ArrayList<>();
    }

    @Override
    public void setIngameTrades(List<IngameTrade> trades) {
        // do nothing for now
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
        // do nothing for now
    }

    @Override
    public boolean supportsFourStartingMoves() {
        return false;
    }

    @Override
    public List<Integer> getFieldMoves() {
        return new ArrayList<>();
    }

    @Override
    public List<Integer> getEarlyRequiredHMMoves() {
        return new ArrayList<>();
    }

    @Override
    public Map<Integer, List<Integer>> getShopItems() {
        return new TreeMap<>();
    }

    @Override
    public void setShopItems(Map<Integer, List<Integer>> shopItems) {
        // do nothing for now
    }

    @Override
    public void setShopPrices() {
        // do nothing for now
    }

    @Override
    public List<Integer> getMainGameShops() {
        return new ArrayList<>();
    }

    @Override
    public BufferedImage getMascotImage() {
        return null;
    }
}
