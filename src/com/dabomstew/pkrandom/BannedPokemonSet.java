package com.dabomstew.pkrandom;

        /*----------------------------------------------------------------------------*/
        /*--  BannedPokemonSet.java - handles functionality related to banned       --*/
        /*--  Pokemon                                                               --*/
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

import com.dabomstew.pkrandom.pokemon.Pokemon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class BannedPokemonSet {

    private Set<Integer> bannedPokemon;

    private static final int BANNED_POKEMON_VERSION = 1;

    // Standard constructor: read binary data from an input stream.
    public BannedPokemonSet(InputStream data) throws IOException {

        if (data.read() != BANNED_POKEMON_VERSION) {
            throw new IOException("Invalid banned pokemon file provided.");
        }

        bannedPokemon = readIDsBlock(data);
    }

    // Alternate constructor: blank all lists
    // Used for importing old names and on the editor dialog.
    public BannedPokemonSet() {
        bannedPokemon = new HashSet<>();
    }

    private Set<Integer> readIDsBlock(InputStream in) throws IOException {
        // Read the size of the block to come.
        byte[] szData = FileFunctions.readFullyIntoBuffer(in, 4);
        int size = FileFunctions.readFullIntBigEndian(szData, 0);
        if (in.available() < size) {
            throw new IOException("Invalid size specified.");
        }

        // Read the block and translate it into a list of ID's.
        byte[] idData = FileFunctions.readFullyIntoBuffer(in, size);
        List<Integer> ids = new ArrayList<>();
        Scanner sc = new Scanner(new ByteArrayInputStream(idData), "UTF-8");
        while (sc.hasNextLine()) {
            String id = sc.nextLine().trim();
            if (!id.isEmpty()) {
                try {
                    ids.add(Integer.parseInt(id));
                } catch (NumberFormatException e) {
                    throw new IOException("File contains non-numeric values.");
                }
            }
        }
        sc.close();

        return new HashSet<>(ids);
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write(BANNED_POKEMON_VERSION);

        writePokemonBlock(baos, bannedPokemon);

        return baos.toByteArray();
    }

    private void writePokemonBlock(OutputStream out, Set<Integer> pokemon) throws IOException {
        String newln = SysConstants.LINE_SEP;
        StringBuilder outIDs = new StringBuilder();
        boolean first = true;
        for (Integer poke : pokemon) {
            if (!first) {
                outIDs.append(newln);
            }
            first = false;
            outIDs.append(poke);
        }
        byte[] pokemonData = outIDs.toString().getBytes("UTF-8");
        byte[] szData = new byte[4];
        FileFunctions.writeFullIntBigEndian(szData, 0, pokemonData.length);
        out.write(szData);
        out.write(pokemonData);
    }

    public Set<Integer> getBannedPokemon() {
        return Collections.unmodifiableSet(bannedPokemon);
    }


    public void addBannedPokemon(Integer pokemon) {
        bannedPokemon.add(pokemon);
    }

    public void addBannedPokemon(List<Integer> pokemon) {
        bannedPokemon.addAll(pokemon);
    }

    public void removeBannedPokemon(Integer pokemon) { bannedPokemon.remove(pokemon); }

    public void setBannedPokemon(List<Integer> pokemon) {
        bannedPokemon.clear();
        bannedPokemon.addAll(pokemon);
    }

    public boolean contains(Pokemon pokemon) {
        return bannedPokemon.contains(pokemon.number);
    }

    public boolean contains(Integer pokemon) {
        return bannedPokemon.contains(pokemon);
    }

}
