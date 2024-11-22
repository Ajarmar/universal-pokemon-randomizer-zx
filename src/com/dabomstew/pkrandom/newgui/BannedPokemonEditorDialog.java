package com.dabomstew.pkrandom.newgui;

/*----------------------------------------------------------------------------*/
/*--  BannedPokemonEditorDIalog.java - a GUI interface to allow users to    --*/
/*--                                 edit the Pokemon they don't want to    --*/
/*--                                 have show up in the randomizers.       --*/
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

import com.dabomstew.pkrandom.*;
import com.dabomstew.pkrandom.ctr.BFLIM;
import com.dabomstew.pkrandom.pokemon.MegaEvolution;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Type;
import com.dabomstew.pkrandom.romhandlers.RomHandler;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class BannedPokemonEditorDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = -1421503126547242929L;
    private boolean pendingChanges;
    private List<Pokemon> allPokemon;
    RomHandler romHandler;
    private BannedPokemonSet bannedPokemon;
    private ImageIcon emptyIcon = new ImageIcon(getClass().getResource("/com/dabomstew/pkrandom/newgui/emptyIcon.png"));
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dabomstew/pkrandom/newgui/Bundle");




    /**
     * Creates new form BannedPokemonEditorDialog
     */
    public BannedPokemonEditorDialog(java.awt.Frame parent, RomHandler initRomHandler) {
        super(parent, true);

        if (initRomHandler == null) {
            java.awt.EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(BannedPokemonEditorDialog.this,
                    "You need to load a rom to use the Banned Pokemon Editor."));
            return;
        }
        romHandler = initRomHandler;
        allPokemon = initRomHandler.getPokemon();

        initComponents();
        setLocationRelativeTo(parent);

        java.awt.EventQueue.invokeLater(() -> setVisible(true));

        bannedFileChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));

        try {
            bannedPokemon = FileFunctions.getBannedPokemon();
            populatePokemon(bannedPokemonText, bannedPokemon.getBannedPokemon());

        } catch (IOException ex) {
            java.awt.EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(BannedPokemonEditorDialog.this,
                    "Your banned pokemon file is for a different randomizer version or otherwise corrupt."));
        }

        // dialog if there's no custom names file yet
//        if (!new File(SysConstants.ROOT_PATH + SysConstants.bannedPokemonFile).exists()) {
//            java.awt.EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(
//                    BannedPokemonEditorDialog.this,
//                    String.format(
//                            "Welcome to the banned Pokemon editor!\nThis is where you can edit the PokeDex ID's used for the option in \"Limit Pokemon\".\nThe ID's are initially empty, but you can either add Pokemon ID's (eg. '1' to ban Bulbasaur) or use the buttons on the side.\nYou can share your banned ID sets with others, too!\nJust send them the %s file created in the randomizer directory.",
//                            SysConstants.bannedPokemonFile)));
//        }

        pendingChanges = false;

        addDocListener(bannedPokemonText);
    }

    private void addDocListener(JTextArea textArea) {
        textArea.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                pendingChanges = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                pendingChanges = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                pendingChanges = true;
            }
        });

    }

    private void formWindowClosing() {// GEN-FIRST:event_formWindowClosing
        attemptClose();
    }// GEN-LAST:event_formWindowClosing

    private void saveBtnActionPerformed() {// GEN-FIRST:event_saveBtnActionPerformed
        save();
    }// GEN-LAST:event_saveBtnActionPerformed

    private void loadBtnActionPerformed() { load(); }

    private void closeBtnActionPerformed() {// GEN-FIRST:event_closeBtnActionPerformed
        attemptClose();
    }// GEN-LAST:event_closeBtnActionPerformed

    private boolean save() {
        bannedFileChooser.setSelectedFile(new File(SysConstants.ROOT_PATH + SysConstants.bannedPokemonFile));
        int returnVal = bannedFileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fh = bannedFileChooser.getSelectedFile();

            fh = FileFunctions.fixFilename(fh, "rnbp");
            try {
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(fh));
                bannedPokemon.setBannedPokemon(getPokemonList(bannedPokemonText));
                byte[] data = bannedPokemon.getBytes();

                dos.write(data);
                dos.close();

                FileFunctions.writeBytesToFile(SysConstants.ROOT_PATH + SysConstants.bannedPokemonFile, data);

                JOptionPane.showMessageDialog(this, "Banned ID's file saved to\n" + fh.getAbsolutePath());

                pendingChanges = false;
                return true;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not save the preset file.");
                return false;
            }
        }
        return false;
    }

    private boolean load() {
        bannedFileChooser.setSelectedFile(null);
        int returnVal = bannedFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fh = bannedFileChooser.getSelectedFile();
            try {
                DataInputStream dis = new DataInputStream(Files.newInputStream(fh.toPath()));

                bannedPokemon = new BannedPokemonSet(dis);

                populatePokemon(bannedPokemonText, bannedPokemon.getBannedPokemon());

                dis.close();
                return true;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not load Banned Pokemon File.");
                return false;
            }
        }
        return false;
    }

    private void attemptClose() {
        if (pendingChanges) {
            int result = JOptionPane
                    .showConfirmDialog(this,
                            "You've made some unsaved changes to your banned Pokemon.\nDo you want to save them before closing the editor?");
            if (result == JOptionPane.YES_OPTION) {
                if (save()) {
                    dispose();
                }
            } else if (result == JOptionPane.NO_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    private List<Integer> getPokemonList(JTextArea textArea) throws NumberFormatException {
        String contents = textArea.getText();
        // standardize newlines
        contents = contents.replace("\r\n", "\n");
        contents = contents.replace("\r", "\n");
        // split by them
        String[] pokemon = contents.split("\n");
        List<Integer> results = new ArrayList<>();
        for (String id : pokemon) {
            String ln = id.trim();
            if (!ln.isEmpty()) {
                results.add(Integer.parseInt(ln));
            }
        }
        return results;
    }

    private void populatePokemon(JTextArea textArea, Set<Integer> pokemon) {
        textArea.setText("");
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Integer uid : pokemon.stream().sorted().collect(Collectors.toList())) {
            if (!first) {
                sb.append(SysConstants.LINE_SEP);
            }
            first = false;
            sb.append(uid);
        }
        textArea.setText(sb.toString());
        textArea.update(textArea.getGraphics());
    }

    private JCheckBox[] getPokemonTypes() {
        List<JCheckBox> typeBoxes = new ArrayList<JCheckBox>();
        for (com.dabomstew.pkrandom.pokemon.Type pokeType : com.dabomstew.pkrandom.pokemon.Type.getAllTypes(romHandler.generationOfPokemon())) {
            typeBoxes.add(getTypeBox(pokeType));
        }

        return typeBoxes.toArray(new JCheckBox[0]);
    }

    private void selectAllTypesActionPerformed() {
        List<Boolean> typeBoxesSelected = new ArrayList<>();

        for (JCheckBox typeBox : getPokemonTypes()) {
            typeBoxesSelected.add(typeBox.isSelected());
        }

        boolean allSelected = !typeBoxesSelected.contains(false);

        for (JCheckBox typeBox : getPokemonTypes()) {
            typeBox.setSelected(!allSelected);
        }
    }

    private JCheckBox getTypeBox(com.dabomstew.pkrandom.pokemon.Type pokeType) {
        switch (pokeType) {
            case NORMAL:
                return checkBoxNORMAL;
            case FIGHTING:
                return checkBoxFIGHTING;
            case FLYING:
                return checkBoxFLYING;
            case GRASS:
                return checkBoxGRASS;
            case WATER:
                return checkBoxWATER;
            case FIRE:
                return checkBoxFIRE;
            case ROCK:
                return checkBoxROCK;
            case GROUND:
                return checkBoxGROUND;
            case PSYCHIC:
                return checkBoxPSYCHIC;
            case BUG:
                return checkBoxBUG;
            case DRAGON:
                return checkBoxDRAGON;
            case ELECTRIC:
                return checkBoxELECTRIC;
            case GHOST:
                return checkBoxGHOST;
            case POISON:
                return checkBoxPOISON;
            case ICE:
                return checkBoxICE;
            case STEEL:
                return checkBoxSTEEL;
            case DARK:
                return checkBoxDARK;
            case FAIRY:
                return checkBoxFAIRY;
        }
        return null;
    }

    private boolean typeIsSelected(com.dabomstew.pkrandom.pokemon.Type pokeType) {
        return getTypeBox(pokeType) != null && getTypeBox(pokeType).isSelected();
    }

    private void banPokemon(Pokemon poke) {
        bannedPokemon.addBannedPokemon(poke.number);
    }
    private void unbanPokemon(Pokemon poke) {
        bannedPokemon.removeBannedPokemon(poke.number);
    }

    private void syncBannedPokemon() { bannedPokemon.setBannedPokemon(getPokemonList(bannedPokemonText));}

    private void banByTypeActionPerformed() {
        syncBannedPokemon();
        for (int pokeID = 1; pokeID < allPokemon.size(); pokeID ++) {
            Pokemon poke = allPokemon.get(pokeID);
            if (banByMonoType.isSelected()) {
                if (poke.secondaryType == null && typeIsSelected(poke.primaryType)) {
                    banPokemon(poke);
                }
            }
            else if (banByPrimaryType.isSelected()) {
                if (typeIsSelected(poke.primaryType)) {
                    banPokemon(poke);
                }
            }
            else if (typeIsSelected(poke.primaryType) || (poke.secondaryType != null && typeIsSelected(poke.secondaryType))) {
                banPokemon(poke);
            }
        }
        populatePokemon(bannedPokemonText, bannedPokemon.getBannedPokemon());

    }

    private void unBanByTypeActionPerformed() {
        syncBannedPokemon();
        for (int pokeID = 1; pokeID < allPokemon.size(); pokeID ++) {
            Pokemon poke = allPokemon.get(pokeID);
            if (banByMonoType.isSelected() && poke.secondaryType == null && typeIsSelected(poke.primaryType)) {
                unbanPokemon(poke);
            }
            else if (banByPrimaryType.isSelected() && typeIsSelected(poke.primaryType)) {
                unbanPokemon(poke);
            }
            else if (typeIsSelected(poke.primaryType) || (poke.secondaryType != null && typeIsSelected(poke.secondaryType))) {
                unbanPokemon(poke);
            }
        }

        populatePokemon(bannedPokemonText, bannedPokemon.getBannedPokemon());

    }

    private ImageIcon getPokemonIcon(JLabel frame, Integer pkIndex) {
        try {
            byte[] iconBytes = FileFunctions.openPokemonIcon(pkIndex.toString());

            ImageIcon icon = new ImageIcon(iconBytes);
            Image image = icon.getImage();
            Image newimg = image.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH);

            return new ImageIcon(newimg);
        } catch (IOException e) {
            System.out.println("Could not load Icon: " + e);
            return emptyIcon;
        }
    }

    private void prevPokeSelectAction() {
        int newIndex = selectPokeCB.getSelectedIndex() - 1;
        if (newIndex > 0) {
            selectPokeCB.setSelectedIndex(selectPokeCB.getSelectedIndex() - 1);
            setPokeSelect();
        }
    }

    private void nextPokeSelectAction() {
        int newIndex = selectPokeCB.getSelectedIndex() + 1;
        if (newIndex < allPokemon.size()) {
            selectPokeCB.setSelectedIndex(selectPokeCB.getSelectedIndex() + 1);
            setPokeSelect();
        }
    }

    private void banPokeActionPerformed() {
        syncBannedPokemon();
        banPokemon(allPokemon.get(((ComboItem)selectPokeCB.getSelectedItem()).getValue()));
        populatePokemon(bannedPokemonText, bannedPokemon.getBannedPokemon());
        nextPokeSelectAction();
    }

    private void unbanPokeActionPerformed() {
        syncBannedPokemon();
        unbanPokemon(allPokemon.get(((ComboItem)selectPokeCB.getSelectedItem()).getValue()));
        populatePokemon(bannedPokemonText, bannedPokemon.getBannedPokemon());
        nextPokeSelectAction();
    }

    private void selectPokeActionPerformed() {
        setPokeSelect();
    }

    private void setPokeSelect() {
        if (selectPokeCB.getSelectedIndex() < 1) {
            pokemonIconLabel.setIcon(emptyIcon);
            return;
        }
        pokemonIconLabel.setIcon(getPokemonIcon(pokemonIconLabel, ((ComboItem)selectPokeCB.getSelectedItem()).getValue()));
    }

    private void banRandomTypeAction() {
        syncBannedPokemon();
        Set<com.dabomstew.pkrandom.pokemon.Type> randomTypes = new HashSet<>();
        while (randomTypes.size() < (Integer)banRandomCountSpinner.getValue()) {
            com.dabomstew.pkrandom.pokemon.Type randomType = romHandler.randomType();
            if (romHandler.typeInGame(randomType)) {
                randomTypes.add(randomType);
            }
        }

        for (int pokeID = 1; pokeID < allPokemon.size(); pokeID ++) {
            Pokemon poke = allPokemon.get(pokeID);
            if (banByMonoType.isSelected()) {
                if (poke.secondaryType == null && randomTypes.contains(poke.primaryType)) {
                    banPokemon(poke);
                }
            }
            else if (banByPrimaryType.isSelected()) {
                if (randomTypes.contains(poke.primaryType)) {
                    banPokemon(poke);
                }
            }
            else if (randomTypes.contains(poke.primaryType) || (poke.secondaryType != null && randomTypes.contains(poke.primaryType))) {
                banPokemon(poke);
            }
        }

        populatePokemon(bannedPokemonText, bannedPokemon.getBannedPokemon());

        if (banRandomTypeCheckBox.isSelected()) {
            java.awt.EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(BannedPokemonEditorDialog.this,
                    "Banned: " + randomTypes));
        }
    }

    private void unbanRandomTypeAction() {
        syncBannedPokemon();

        Set<com.dabomstew.pkrandom.pokemon.Type> randomTypes = new HashSet<>();
        while (randomTypes.size() < (Integer)banRandomCountSpinner.getValue()) {
            com.dabomstew.pkrandom.pokemon.Type randomType = romHandler.randomType();
            if (romHandler.typeInGame(randomType)) {
                randomTypes.add(randomType);
            }
        }

        for (int pokeID = 1; pokeID < allPokemon.size(); pokeID ++) {
            Pokemon poke = allPokemon.get(pokeID);
            if (banByMonoType.isSelected()) {
                if (poke.secondaryType == null && randomTypes.contains(poke.primaryType)) {
                    unbanPokemon(poke);
                }
            }
            else if (banByPrimaryType.isSelected()) {
                if (randomTypes.contains(poke.primaryType)) {
                    unbanPokemon(poke);
                }
            }
            else if (randomTypes.contains(poke.primaryType) || (poke.secondaryType != null && randomTypes.contains(poke.primaryType))) {
                unbanPokemon(poke);
            }
        }

        populatePokemon(bannedPokemonText, bannedPokemon.getBannedPokemon());

        if (banRandomTypeCheckBox.isSelected()) {
            java.awt.EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(BannedPokemonEditorDialog.this,
                    "Unbanned: " + randomTypes));
        }
    }


    class ComboItem {
        private Integer value;
        private String label;

        public ComboItem(Integer value, String label) {
            this.value = value;
            this.label = label;
        }

        public Integer getValue() {
            return this.value;
        }

        public String getLabel() {
            return this.label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private void insertComboItem(ComboItem item, ComboItem[] array, int index) {
        for (int i = array.length - 1; i > index + 1; i--) {
            array[i] = array[i-1];
            array[i-1] = null;
        }
        array[index + 1] = item;
    }

    private void initComponents() {

        bannedFileChooser = new JFileChooser();
        bannedFileChooser.setFileFilter(new BannedPokemonFileFilter());

        editorSplitPane = new JSplitPane();
        bannedPokemonSP = new JScrollPane();
        bannedPokemonText = new JTextArea();
        bannedPokemonText.setToolTipText(bundle.getString("BannedPokemonEditorDialog.bannedPokemonTextArea.tooltipText"));
        banFeaturesSP = new JPanel();

        banByTypePane = new JPanel();
        banByTypePane.setBorder(BorderFactory.createLineBorder(Color.black));
        banByTypeCheckBoxPane = new JPanel();
        banByTypeButtonPane = new JPanel();
        banByTypeBtn = new JButton();
        unbanByTypeBtn = new JButton();
        selectAllTypesBtn = new JButton();
        banByPrimaryType = new JCheckBox();
        banByMonoType = new JCheckBox();

        saveBtn = new JButton();
        loadBtn = new JButton();
        closeBtn = new JButton();

        checkBoxNORMAL= new JCheckBox(bundle.getString("PokemonTypeLabelNormal"));
        checkBoxFIRE= new JCheckBox(bundle.getString("PokemonTypeLabelFire"));
        checkBoxWATER= new JCheckBox(bundle.getString("PokemonTypeLabelWater"));
        checkBoxGRASS= new JCheckBox(bundle.getString("PokemonTypeLabelGrass"));
        checkBoxFLYING= new JCheckBox(bundle.getString("PokemonTypeLabelFlying"));
        checkBoxFIGHTING= new JCheckBox(bundle.getString("PokemonTypeLabelFighting"));
        checkBoxPOISON= new JCheckBox(bundle.getString("PokemonTypeLabelPoison"));
        checkBoxELECTRIC= new JCheckBox(bundle.getString("PokemonTypeLabelElectric"));
        checkBoxGROUND= new JCheckBox(bundle.getString("PokemonTypeLabelGround"));
        checkBoxROCK= new JCheckBox(bundle.getString("PokemonTypeLabelRock"));
        checkBoxPSYCHIC= new JCheckBox(bundle.getString("PokemonTypeLabelPsychic"));
        checkBoxICE= new JCheckBox(bundle.getString("PokemonTypeLabelIce"));
        checkBoxBUG= new JCheckBox(bundle.getString("PokemonTypeLabelBug"));
        checkBoxGHOST= new JCheckBox(bundle.getString("PokemonTypeLabelGhost"));
        checkBoxSTEEL= new JCheckBox(bundle.getString("PokemonTypeLabelSteel"));
        checkBoxDRAGON= new JCheckBox(bundle.getString("PokemonTypeLabelDragon"));
        checkBoxDARK= new JCheckBox(bundle.getString("PokemonTypeLabelDark"));
        checkBoxFAIRY= new JCheckBox(bundle.getString("PokemonTypeLabelFairy"));

        banSelectPokemonPane= new JPanel();
        banSelectPokemonPane.setBorder(BorderFactory.createLineBorder(Color.black));
        pokemonIconLabel= new JLabel();
        prevPokeBtn= new JButton("<-");
        nextPokeBtn= new JButton("->");
        banPokeBtn= new JButton("Ban");
        unbanPokeBtn= new JButton("Unban");
        selectPokeCB= new JComboBox<ComboItem>();
        pokePane = new JPanel();

        banRandomTypePane = new JPanel();
        banRandomTypePane.setBorder(BorderFactory.createLineBorder(Color.black));
        banRandomCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1,
                com.dabomstew.pkrandom.pokemon.Type.getAllTypes(romHandler.generationOfPokemon()).size(), 1));
        banRandomCountSpinner.setEditor(new JSpinner.NumberEditor(banRandomCountSpinner, "# /" +
                com.dabomstew.pkrandom.pokemon.Type.getAllTypes(romHandler.generationOfPokemon()).size() + " ")
        );
        ((JSpinner.NumberEditor) banRandomCountSpinner.getEditor()).getTextField().setEditable(false);

        banRandomTypeBtn = new JButton("Ban");
        banRandomTypeBtn.setToolTipText(bundle.getString("BannedPokemonEditorDialog.banRandomTypeButton.tooltipText"));
        unbanRandomTypeBtn = new JButton("UnBan");
        unbanRandomTypeBtn.setToolTipText(bundle.getString("BannedPokemonEditorDialog.unbanRandomTypeButton.tooltipText"));
        banRandomTypeLabel = new JLabel("Random Types");
        banRandomTypeCheckBox = new JCheckBox("Spoilers");
        banRandomTypeCheckBox.setToolTipText(bundle.getString("BannedPokemonEditorDialog.banRandomTypeSpoiler.tooltipText"));

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(bundle.getString("BannedPokemonEditorDialog.title"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing();
            }
        });

        bannedPokemonSP.setHorizontalScrollBar(null);

        bannedPokemonSP.setMinimumSize(new Dimension(100, 50));
        bannedPokemonText.setColumns(20);
        bannedPokemonText.setRows(5);
        bannedPokemonSP.setViewportView(bannedPokemonText);

        selectAllTypesBtn.setText("(Un)Check All");
        banByTypeBtn.setText("Ban by Type");
        banByTypeBtn.setToolTipText(bundle.getString("BannedPokemonEditorDialog.banPokeTypeButton.tooltip"));
        unbanByTypeBtn.setText("Unban by Type");
        unbanByTypeBtn.setToolTipText(bundle.getString("BannedPokemonEditorDialog.unbanPokeTypeButton.tooltip"));

        banByTypeCheckBoxPane.setLayout(new GridLayout(7,4));

        GroupLayout banByTypeLayout = new GroupLayout(banByTypePane);
        banByTypePane.setLayout(banByTypeLayout);

        banByTypeLayout.setHorizontalGroup(
                banByTypeLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(banByTypeLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(banByTypeCheckBoxPane)
                                .addComponent(banByTypeButtonPane)
                        )
        );
        banByTypeLayout.setVerticalGroup(
                banByTypeLayout.createSequentialGroup()
                        .addGroup(banByTypeLayout.createSequentialGroup()
                                .addComponent(banByTypeCheckBoxPane)
                                .addComponent(banByTypeButtonPane)
                        )
        );


        for (JCheckBox typeBox : getPokemonTypes()) {
            banByTypeCheckBoxPane.add(typeBox);
        }

        banByPrimaryType.setText("by Primary Type");
        banByPrimaryType.setToolTipText(bundle.getString("BannedPokemonEditorDialog.banByPrimaryType.tooltip"));
        banByTypeButtonPane.add(banByPrimaryType);
        banByMonoType.setText("Mono Type Only");
        banByMonoType.setToolTipText(bundle.getString("BannedPokemonEditorDialog.banByMonoType.tooltip"));

        banByTypeButtonPane.add(banByMonoType);

        banByTypeButtonPane.add(selectAllTypesBtn);
        selectAllTypesBtn.addActionListener(evt -> selectAllTypesActionPerformed());
        banByTypeButtonPane.add(banByTypeBtn);
        banByTypeBtn.addActionListener(evt -> banByTypeActionPerformed());
        banByTypeButtonPane.add(unbanByTypeBtn);
        unbanByTypeBtn.addActionListener(evt -> unBanByTypeActionPerformed());

        GroupLayout banRandomTypeLayout = new GroupLayout(banRandomTypePane);
        banRandomTypePane.setLayout(banRandomTypeLayout);

        banRandomTypeLayout.setHorizontalGroup(
                banRandomTypeLayout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(banRandomTypeLayout.createSequentialGroup()
                                .addComponent(banRandomTypeBtn)
                                .addGap(15)
                                .addComponent(unbanRandomTypeBtn)
                                .addGap(15)
                                .addComponent(banRandomCountSpinner, 50,60,70)
                                .addGap(15)
                                .addComponent(banRandomTypeLabel)
                                .addGap(15)
                                .addComponent(banRandomTypeCheckBox)
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        banRandomTypeLayout.setVerticalGroup(
            banRandomTypeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(
                    banRandomTypeLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(banRandomTypeBtn)
                        .addComponent(unbanRandomTypeBtn)
                        .addComponent(banRandomCountSpinner)
                        .addComponent(banRandomTypeLabel)
                        .addComponent(banRandomTypeCheckBox)
                )
                .addContainerGap()
        );

        banRandomTypeBtn.addActionListener(evt -> banRandomTypeAction());
        unbanRandomTypeBtn.addActionListener(evt -> unbanRandomTypeAction());
        banRandomTypeLabel.setText(bundle.getString("BannedPokemonEditorDialog.banRandomTypeLabel.text"));

        GroupLayout banSelectLayout = new GroupLayout(banSelectPokemonPane);
        banSelectPokemonPane.setLayout(banSelectLayout);

        banSelectLayout.setHorizontalGroup(
                banSelectLayout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(banSelectLayout.createSequentialGroup()
                                .addGroup(banSelectLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(prevPokeBtn)
                                        .addComponent(unbanPokeBtn)
                                )
                                .addComponent(pokePane, GroupLayout.DEFAULT_SIZE, 5,Short.MAX_VALUE)
                                .addGroup(banSelectLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(nextPokeBtn)
                                        .addComponent(banPokeBtn)
                                )
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        banSelectLayout.setVerticalGroup(
                banSelectLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(banSelectLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addGroup(banSelectLayout.createSequentialGroup()
                                        .addComponent(prevPokeBtn)
                                        .addGap(15)
                                        .addComponent(unbanPokeBtn)
                                )
                                .addComponent(pokePane)
                                .addGroup(banSelectLayout.createSequentialGroup()
                                        .addComponent(nextPokeBtn)
                                        .addGap(15)
                                        .addComponent(banPokeBtn)
                                )
                        )
        );

        ComboItem[] pokemon = new ComboItem[allPokemon.size()];
        for (Pokemon poke : allPokemon) {
            if (poke != null) {
                pokemon[poke.number] = new ComboItem(poke.number, poke.name + " (" + poke.number + ")");
            }
        }

        selectPokeCB.setModel(new DefaultComboBoxModel<>(pokemon));
        GroupLayout pokePaneLayout = new GroupLayout(pokePane);
        pokePane.setLayout(pokePaneLayout);

        pokePaneLayout.setHorizontalGroup(
                pokePaneLayout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pokePaneLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(pokemonIconLabel)
                                .addComponent(selectPokeCB, GroupLayout.DEFAULT_SIZE, 25, 50)
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pokePaneLayout.setVerticalGroup(
                pokePaneLayout.createSequentialGroup()
                        .addGroup(pokePaneLayout.createSequentialGroup()
                                .addComponent(pokemonIconLabel)
                                .addComponent(selectPokeCB)
                        )
        );

        selectPokeCB.setSelectedIndex(1);
        setPokeSelect();

        prevPokeBtn.addActionListener(evt -> prevPokeSelectAction());
        nextPokeBtn.addActionListener(evt -> nextPokeSelectAction());
        unbanPokeBtn.addActionListener(evt -> unbanPokeActionPerformed());
        banPokeBtn.addActionListener(evt -> banPokeActionPerformed());
        selectPokeCB.addActionListener(evt -> selectPokeActionPerformed());

        banFeaturesSP.add(banByTypePane);
        banByTypePane.add(banByTypeCheckBoxPane);
        banByTypePane.add(banByTypeButtonPane);

        banFeaturesSP.add(banSelectPokemonPane);

        GroupLayout banFeaturesLayout = new GroupLayout(banFeaturesSP);
        banFeaturesSP.setLayout(banFeaturesLayout);

        banFeaturesLayout.setHorizontalGroup(
                banFeaturesLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(banFeaturesLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(banByTypePane)
                                .addComponent(banRandomTypePane)
                                .addComponent(banSelectPokemonPane)
                        )
        );
        banFeaturesLayout.setVerticalGroup(
                banFeaturesLayout.createSequentialGroup()
                        .addGroup(banFeaturesLayout.createSequentialGroup()
                                .addComponent(banByTypePane)
                                .addComponent(banRandomTypePane)
                                .addComponent(banSelectPokemonPane)
                        )
        );

        editorSplitPane.setLeftComponent(bannedPokemonSP);
        editorSplitPane.setRightComponent(banFeaturesSP);

        saveBtn.setText(bundle.getString("BannedPokemonEditorDialog.saveBtn.text"));
        saveBtn.addActionListener(evt -> saveBtnActionPerformed());

        loadBtn.setText(bundle.getString("BannedPokemonEditorDialog.loadBtn.text"));
        loadBtn.addActionListener(evt -> loadBtnActionPerformed());

        closeBtn.setText(bundle.getString("BannedPokemonEditorDialog.closeBtn.text"));
        closeBtn.addActionListener(evt -> closeBtnActionPerformed());

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(editorSplitPane, GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(saveBtn)
                                                .addComponent(loadBtn)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(closeBtn)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(editorSplitPane, GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(saveBtn)
                                        .addComponent(loadBtn)
                                        .addComponent(closeBtn))
                                .addContainerGap())
        );

        pack();
    }

    private JButton closeBtn;
    private JSplitPane editorSplitPane;
    private JButton saveBtn;
    private JButton loadBtn;
    private JScrollPane bannedPokemonSP;
    private JPanel banFeaturesSP;
    private JPanel banByTypePane;
    private JPanel banByTypeButtonPane;
    private JButton banByTypeBtn;
    private JButton unbanByTypeBtn;
    private JButton selectAllTypesBtn;
    private JPanel banByTypeCheckBoxPane;
    private JCheckBox checkBoxNORMAL;
    private JCheckBox checkBoxFIRE;
    private JCheckBox checkBoxWATER;
    private JCheckBox checkBoxGRASS;
    private JCheckBox checkBoxFLYING;
    private JCheckBox checkBoxFIGHTING;
    private JCheckBox checkBoxPOISON;
    private JCheckBox checkBoxELECTRIC;
    private JCheckBox checkBoxGROUND;
    private JCheckBox checkBoxROCK;
    private JCheckBox checkBoxPSYCHIC;
    private JCheckBox checkBoxICE;
    private JCheckBox checkBoxBUG;
    private JCheckBox checkBoxGHOST;
    private JCheckBox checkBoxSTEEL;
    private JCheckBox checkBoxDRAGON;
    private JCheckBox checkBoxDARK;
    private JCheckBox checkBoxFAIRY;
    private JCheckBox banByPrimaryType;
    private JCheckBox banByMonoType;
    private JTextArea bannedPokemonText;
    private JPanel banSelectPokemonPane;
    private JLabel pokemonIconLabel;
    private JButton prevPokeBtn;
    private JButton nextPokeBtn;
    private JButton banPokeBtn;
    private JButton unbanPokeBtn;
    private JComboBox<ComboItem> selectPokeCB;
    private JPanel pokePane;
    private JPanel banRandomTypePane;
    private JSpinner banRandomCountSpinner;
    private JButton banRandomTypeBtn;
    private JButton unbanRandomTypeBtn;
    private JLabel banRandomTypeLabel;
    private JCheckBox banRandomTypeCheckBox;
    private JFileChooser bannedFileChooser;


}
