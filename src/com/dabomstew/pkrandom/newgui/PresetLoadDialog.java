/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dabomstew.pkrandom.newgui;

/*----------------------------------------------------------------------------*/
/*--  PresetLoadDialog.java - a dialog to allow use of preset files or      --*/
/*--                          random seed/config string pairs to produce    --*/
/*--                          premade ROMs.                                 --*/
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
import com.dabomstew.pkrandom.exceptions.InvalidSupplementFilesException;
import com.dabomstew.pkrandom.romhandlers.Abstract3DSRomHandler;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 
 * @author Stewart
 */
public class PresetLoadDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = -7898067118947765260L;
    private NewRandomizerGUI parentGUI;
    private RomHandler currentROM;
    private boolean completed = false;
    private String requiredName = null;
    private volatile boolean changeFieldsWithoutCheck = false;
    private CustomNamesSet customNames;
    private BannedPokemonSet bannedPokemon;
    private java.util.ResourceBundle bundle;

    /**
     * Creates new form PresetLoadDialog
     */
    public PresetLoadDialog(NewRandomizerGUI parent, JFrame frame) {
        super(frame, true);
        bundle = java.util.ResourceBundle.getBundle("com/dabomstew/pkrandom/newgui/Bundle"); // NOI18N
        initComponents();
        this.parentGUI = parent;
        this.presetFileChooser.setCurrentDirectory(new File("./"));
        this.romFileChooser.setCurrentDirectory(new File("./"));
        initialState();
        setLocationRelativeTo(frame);
        setVisible(true);
    }

    private void initialState() {
        this.romFileButton.setEnabled(false);
        this.acceptButton.setEnabled(false);
        addChangeListener(this.randomSeedField);
        addChangeListener(this.configStringField);
    }

    private void addChangeListener(JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!changeFieldsWithoutCheck)
                    PresetLoadDialog.this.checkValues();

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!changeFieldsWithoutCheck)
                    PresetLoadDialog.this.checkValues();

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!changeFieldsWithoutCheck)
                    PresetLoadDialog.this.checkValues();

            }
        });

    }

    private boolean checkValues() {
        String name;
        try {
            Long.parseLong(this.randomSeedField.getText());
        } catch (NumberFormatException ex) {
            invalidValues();
            return false;
        }

        // 161 onwards: look for version number
        String configString = this.configStringField.getText();
        if (configString.length() < 3) {
            invalidValues();
            return false;
        }

        try {
            int presetVersionNumber = Integer.parseInt(configString.substring(0, 3));
            if (presetVersionNumber != Version.VERSION) {
                promptForDifferentRandomizerVersion(presetVersionNumber);
                safelyClearFields();
                invalidValues();
                return false;
            }
        } catch (NumberFormatException ex) {
            invalidValues();
            return false;
        }

        try {
            name = this.parentGUI.getValidRequiredROMName(configString.substring(3), customNames, bannedPokemon);
        } catch (InvalidSupplementFilesException ex) {
            safelyClearFields();
            invalidValues();
            return false;
        } catch (Exception ex) {
            // other exception, just call it invalid for now
            invalidValues();
            return false;
        }
        if (name == null) {
            invalidValues();
            return false;
        }
        requiredName = name;
        this.romRequiredLabel.setText(String.format(bundle.getString("PresetLoadDialog.romRequiredLabel.textWithROM"),
                name));
        this.romFileButton.setEnabled(true);

        if (currentROM != null && !currentROM.getROMName().equals(name)) {
            this.currentROM = null;
            this.acceptButton.setEnabled(false);
            this.romFileField.setText("");
        }
        return true;
    }

    private void promptForDifferentRandomizerVersion(int presetVN) {
        // so what version number was it?
        if (presetVN > Version.VERSION) {
            // it's for a newer version
            JOptionPane.showMessageDialog(this, bundle.getString("PresetLoadDialog.newerVersionRequired"));
        } else {
            // tell them which older version to use to load this preset
            // this should be the newest version that used that value
            // for the constant PRESET_FILE_VERSION
            String versionWanted = Version.oldVersions.getOrDefault(presetVN,"Unknown");
            JOptionPane.showMessageDialog(this,
                    String.format(bundle.getString("PresetLoadDialog.olderVersionRequired"), versionWanted));
        }
    }

    private void safelyClearFields() {
        SwingUtilities.invokeLater(() -> {
            changeFieldsWithoutCheck = true;
            configStringField.setText("");
            randomSeedField.setText("");
            changeFieldsWithoutCheck = false;
        });
    }

    private void invalidValues() {
        this.currentROM = null;
        this.romFileField.setText("");
        this.romRequiredLabel.setText(bundle.getString("PresetLoadDialog.romRequiredLabel.text"));
        this.romFileButton.setEnabled(false);
        this.acceptButton.setEnabled(false);
        this.requiredName = null;

    }

    public boolean isCompleted() {
        return completed;
    }

    public RomHandler getROM() {
        return currentROM;
    }

    public long getSeed() {
        return Long.parseLong(this.randomSeedField.getText());
    }

    public String getConfigString() {
        return this.configStringField.getText().substring(3);
    }

    public CustomNamesSet getCustomNames() {
        return customNames;
    }
    public BannedPokemonSet getBannedPokemon() {return bannedPokemon;}

    private void presetFileButtonActionPerformed() {// GEN-FIRST:event_presetFileButtonActionPerformed
        presetFileChooser.setSelectedFile(null);
        int returnVal = presetFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fh = presetFileChooser.getSelectedFile();
            try {
                DataInputStream dis = new DataInputStream(new FileInputStream(fh));
                int checkInt = dis.readInt();
                if (checkInt != Version.VERSION) {
                    dis.close();
                    promptForDifferentRandomizerVersion(checkInt);
                    return;
                }
                long seed = dis.readLong();
                String preset = dis.readUTF();
                customNames = new CustomNamesSet(dis);
                bannedPokemon = new BannedPokemonSet(dis);
                changeFieldsWithoutCheck = true;
                this.randomSeedField.setText(Long.toString(seed));
                this.configStringField.setText(checkInt + "" + preset);
                changeFieldsWithoutCheck = false;
                if (checkValues()) {
                    this.randomSeedField.setEnabled(false);
                    this.configStringField.setEnabled(false);
                    this.presetFileField.setText(fh.getAbsolutePath());
                } else {
                    this.randomSeedField.setText("");
                    this.configStringField.setText("");
                    this.randomSeedField.setEnabled(true);
                    this.configStringField.setEnabled(true);
                    this.presetFileField.setText("");
                    customNames = null;
                    bannedPokemon = null;
                    JOptionPane.showMessageDialog(this, bundle.getString("PresetLoadDialog.invalidSeedFile"));
                }
                dis.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, bundle.getString("PresetLoadDialog.loadingSeedFileFailed"));
            }
        }
    }// GEN-LAST:event_presetFileButtonActionPerformed

    private void romFileButtonActionPerformed() {// GEN-FIRST:event_romFileButtonActionPerformed
        romFileChooser.setSelectedFile(null);
        int returnVal = romFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File fh = romFileChooser.getSelectedFile();
            for (RomHandler.Factory rhf : parentGUI.checkHandlers) {
                if (rhf.isLoadable(fh.getAbsolutePath())) {
                    final RomHandler checkHandler = rhf.create(RandomSource.instance());
                    if (!NewRandomizerGUI.usedLauncher && checkHandler instanceof Abstract3DSRomHandler) {
                        String message = bundle.getString("GUI.pleaseUseTheLauncher");
                        Object[] messages = {message};
                        JOptionPane.showMessageDialog(this, messages);
                        return;
                    }
                    final JDialog opDialog = new OperationDialog(bundle.getString("GUI.loadingText"), this,
                            true);
                    Thread t = new Thread(() -> {
                        SwingUtilities.invokeLater(() -> opDialog.setVisible(true));
                        try {
                            checkHandler.loadRom(fh.getAbsolutePath());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(PresetLoadDialog.this,
                                    bundle.getString("GUI.loadFailedNoLog"));
                        }
                        SwingUtilities.invokeLater(() -> {
                            opDialog.setVisible(false);
                            if (checkHandler.getROMName().equals(requiredName)) {
                                // Got it
                                romFileField.setText(fh.getAbsolutePath());
                                currentROM = checkHandler;
                                acceptButton.setEnabled(true);
                                return;
                            } else {
                                JOptionPane.showMessageDialog(PresetLoadDialog.this, String.format(
                                        bundle.getString("PresetLoadDialog.notRequiredROM"), requiredName,
                                        checkHandler.getROMName()));
                                return;
                            }
                        });
                    });
                    t.start();
                    return;
                }
            }
            JOptionPane.showMessageDialog(this,
                    String.format(bundle.getString("GUI.unsupportedRom"), fh.getName()));
        }
    }// GEN-LAST:event_romFileButtonActionPerformed

    private void acceptButtonActionPerformed() {// GEN-FIRST:event_acceptButtonActionPerformed
        if (customNames == null) {
            try {
                customNames = FileFunctions.getCustomNames();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bannedPokemon == null) {
            try {
                bannedPokemon = FileFunctions.getBannedPokemon();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        completed = true;
        this.setVisible(false);
    }// GEN-LAST:event_acceptButtonActionPerformed

    private void cancelButtonActionPerformed() {// GEN-FIRST:event_cancelButtonActionPerformed
        completed = false;
        this.setVisible(false);
    }// GEN-LAST:event_cancelButtonActionPerformed

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed"
    // desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        presetFileChooser = new JFileChooser();
        romFileChooser = new JFileChooser();
        presetFileLabel = new javax.swing.JLabel();
        presetFileField = new JTextField();
        presetFileButton = new javax.swing.JButton();
        orLabel = new javax.swing.JLabel();
        seedBoxLabel = new javax.swing.JLabel();
        randomSeedField = new JTextField();
        configStringBoxLabel = new javax.swing.JLabel();
        configStringField = new JTextField();
        romRequiredLabel = new javax.swing.JLabel();
        romFileBoxLabel = new javax.swing.JLabel();
        romFileField = new JTextField();
        romFileButton = new javax.swing.JButton();
        acceptButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        presetFileChooser.setFileFilter(new PresetFileFilter());

        romFileChooser.setFileFilter(new ROMFilter());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dabomstew/pkrandom/newgui/Bundle"); // NOI18N
        setTitle(bundle.getString("PresetLoadDialog.title")); // NOI18N
        setModal(true);
        setResizable(false);

        presetFileLabel.setText(bundle.getString("PresetLoadDialog.presetFileLabel.text")); // NOI18N

        presetFileField.setEditable(false);

        presetFileButton.setText(bundle.getString("PresetLoadDialog.presetFileButton.text")); // NOI18N
        presetFileButton.addActionListener(evt -> presetFileButtonActionPerformed());

        orLabel.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        orLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        orLabel.setText(bundle.getString("PresetLoadDialog.orLabel.text")); // NOI18N

        seedBoxLabel.setText(bundle.getString("PresetLoadDialog.seedBoxLabel.text")); // NOI18N

        configStringBoxLabel.setText(bundle.getString("PresetLoadDialog.configStringBoxLabel.text")); // NOI18N

        romRequiredLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        romRequiredLabel.setText(bundle.getString("PresetLoadDialog.romRequiredLabel.text")); // NOI18N

        romFileBoxLabel.setText(bundle.getString("PresetLoadDialog.romFileBoxLabel.text")); // NOI18N

        romFileField.setEditable(false);

        romFileButton.setText(bundle.getString("PresetLoadDialog.romFileButton.text")); // NOI18N
        romFileButton.addActionListener(evt -> romFileButtonActionPerformed());

        acceptButton.setText(bundle.getString("PresetLoadDialog.acceptButton.text")); // NOI18N
        acceptButton.addActionListener(evt -> acceptButtonActionPerformed());

        cancelButton.setText(bundle.getString("PresetLoadDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(evt -> cancelButtonActionPerformed());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                        layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(
                                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(romFileBoxLabel,
                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(presetFileLabel,
                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(seedBoxLabel, javax.swing.GroupLayout.Alignment.LEADING,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(configStringBoxLabel,
                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(
                                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addComponent(acceptButton)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                        169, Short.MAX_VALUE)
                                                                .addComponent(cancelButton))
                                                .addComponent(randomSeedField).addComponent(configStringField)
                                                .addComponent(presetFileField).addComponent(romFileField))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(presetFileButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(romFileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 1,
                                                        Short.MAX_VALUE)).addGap(12, 12, 12))
                .addComponent(orLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)
                .addComponent(romRequiredLabel, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(presetFileLabel)
                                        .addComponent(presetFileField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(presetFileButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(orLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(seedBoxLabel)
                                        .addComponent(randomSeedField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(configStringBoxLabel)
                                        .addComponent(configStringField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(romRequiredLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(romFileBoxLabel)
                                        .addComponent(romFileField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(romFileButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(acceptButton).addComponent(cancelButton)).addContainerGap()));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton acceptButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel configStringBoxLabel;
    private JTextField configStringField;
    private javax.swing.JLabel orLabel;
    private javax.swing.JButton presetFileButton;
    private JFileChooser presetFileChooser;
    private JTextField presetFileField;
    private javax.swing.JLabel presetFileLabel;
    private JTextField randomSeedField;
    private javax.swing.JLabel romFileBoxLabel;
    private javax.swing.JButton romFileButton;
    private JFileChooser romFileChooser;
    private JTextField romFileField;
    private javax.swing.JLabel romRequiredLabel;
    private javax.swing.JLabel seedBoxLabel;
    // End of variables declaration//GEN-END:variables
}
