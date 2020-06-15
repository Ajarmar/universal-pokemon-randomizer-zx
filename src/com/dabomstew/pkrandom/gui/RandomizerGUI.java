package com.dabomstew.pkrandom.gui;

/*----------------------------------------------------------------------------*/
/*--  RandomizerGUI.java - the main GUI for the randomizer, containing the  --*/
/*--                       various options available and such.              --*/
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.dabomstew.pkrandom.CustomNamesSet;
import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.RandomSource;
import com.dabomstew.pkrandom.Randomizer;
import com.dabomstew.pkrandom.Settings;
import com.dabomstew.pkrandom.SysConstants;
import com.dabomstew.pkrandom.Utils;
import com.dabomstew.pkrandom.exceptions.InvalidSupplementFilesException;
import com.dabomstew.pkrandom.exceptions.RandomizationException;
import com.dabomstew.pkrandom.pokemon.GenRestrictions;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.romhandlers.AbstractDSRomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen1RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen2RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen3RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen4RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen5RomHandler;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

/**
 * 
 * @author Stewart
 */
public class RandomizerGUI extends javax.swing.JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 637989089525556154L;
    private RomHandler romHandler;
    protected RomHandler.Factory[] checkHandlers;

    private OperationDialog opDialog;
    private List<JCheckBox> tweakCheckboxes;
    private boolean presetMode;
    private GenRestrictions currentRestrictions;
    private LayoutManager noTweaksLayout;

    // Settings
    private boolean autoUpdateEnabled;
    private boolean haveCheckedCustomNames;
    private boolean useScrollPaneMode;
    private ImageIcon emptyIcon = new ImageIcon(getClass().getResource("/com/dabomstew/pkrandom/gui/emptyIcon.png"));

    java.util.ResourceBundle bundle;

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String args[]) {
        boolean autoupdate = true;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--noupdate")) {
                autoupdate = false;
                break;
            }
        }
        final boolean au = autoupdate;
        boolean onWindowsNativeLAF = false;
        try {
            String lafName = javax.swing.UIManager.getSystemLookAndFeelClassName();
            // NEW: Only set Native LaF on windows.
            if (lafName.equalsIgnoreCase("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
                javax.swing.UIManager.setLookAndFeel(lafName);
                onWindowsNativeLAF = true;
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RandomizerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RandomizerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RandomizerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RandomizerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }

        final boolean wn = onWindowsNativeLAF;

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RandomizerGUI(au, wn);
            }
        });
    }

    // constructor
    /**
     * Creates new form RandomizerGUI
     * 
     * @param autoupdate
     */
    public RandomizerGUI(boolean autoupdate, boolean onWindowsLAF) {
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        bundle = java.util.ResourceBundle.getBundle("com/dabomstew/pkrandom/gui/Bundle"); // NOI18N
        testForRequiredConfigs();
        checkHandlers = new RomHandler.Factory[] { new Gen1RomHandler.Factory(), new Gen2RomHandler.Factory(),
                new Gen3RomHandler.Factory(), new Gen4RomHandler.Factory(), new Gen5RomHandler.Factory() };
        autoUpdateEnabled = true;
        haveCheckedCustomNames = false;
        useScrollPaneMode = !onWindowsLAF;
        attemptReadConfig();
        if (!autoupdate) {
            // override autoupdate
            autoUpdateEnabled = false;
        }
        initComponents();
        initTweaksPanel();
        guiCleanup();
        if (useScrollPaneMode) {
            scrollPaneSetup();
        }
        noTweaksLayout = miscTweaksPanel.getLayout();
        initialiseState();

        boolean canWrite = attemptWriteConfig();
        if (!canWrite) {
            JOptionPane.showMessageDialog(null, bundle.getString("RandomizerGUI.cantWriteConfigFile"));
            autoUpdateEnabled = false;
        }
        setLocationRelativeTo(null);
        setVisible(true);
        if (!haveCheckedCustomNames) {
            checkCustomNames();
        }
        if (autoUpdateEnabled) {
            //new UpdateCheckThread(this, false).start();
        }
    }

    private void guiCleanup() {
        // All systems: test for font size and adjust if required
        Font f = pokeLimitCB.getFont();
        if (f == null || !f.getFontName().equalsIgnoreCase("tahoma") || f.getSize() != 11) {
            System.out.println("activating font face fix");
            Font regularFont = new Font("Tahoma", 0, 11);
            Font boldFont = new Font("Tahoma", 1, 11);
            fontFaceFix(this, regularFont, boldFont);
            for (JCheckBox cb : tweakCheckboxes) {
                cb.setFont(regularFont);
            }
        }
    }

    private void scrollPaneSetup() {
        /* @formatter:off */
        JScrollPane optionsScrollPane = new JScrollPane();
        optionsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        JPanel optionsContainerPanel = new JPanel();
        javax.swing.GroupLayout optionsContainerPanelLayout = new javax.swing.GroupLayout(
                optionsContainerPanel);
        optionsContainerPanel.setLayout(optionsContainerPanelLayout);
        optionsContainerPanelLayout
                .setHorizontalGroup(optionsContainerPanelLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(pokemonTypesPanel,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(pokemonMovesetsPanel,
                                javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(trainersPokemonPanel,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(wildPokemonPanel,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(starterPokemonPanel,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(staticPokemonPanel,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(tmhmsPanel,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addGroup(
                                optionsContainerPanelLayout
                                        .createSequentialGroup()
                                        .addComponent(
                                                baseStatsPanel,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                abilitiesPanel,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE))
                        .addComponent(moveTutorsPanel,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(inGameTradesPanel,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(fieldItemsPanel,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(pokemonEvolutionsPanel,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(moveDataPanel,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(miscTweaksPanel,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
        );
        optionsContainerPanelLayout
                .setVerticalGroup(optionsContainerPanelLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                optionsContainerPanelLayout
                                        .createSequentialGroup()
                                        .addGroup(
                                                optionsContainerPanelLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(
                                                                baseStatsPanel,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .addComponent(
                                                                abilitiesPanel,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE))
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                pokemonTypesPanel,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                pokemonEvolutionsPanel,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                starterPokemonPanel,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                moveDataPanel,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                pokemonMovesetsPanel,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                trainersPokemonPanel,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                wildPokemonPanel,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                staticPokemonPanel,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                tmhmsPanel,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                moveTutorsPanel,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                inGameTradesPanel,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                fieldItemsPanel,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                miscTweaksPanel,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));

        optionsScrollPane.setViewportView(optionsContainerPanel);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(optionsScrollPane,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                747, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(generalOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(loadQSButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(saveQSButton))
                            .addComponent(romInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addComponent(gameMascotLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(openROMButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(saveROMButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(usePresetsButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(settingsButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(versionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(websiteLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(generalOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gameMascotLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(openROMButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(saveROMButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(usePresetsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(settingsButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(romInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(loadQSButton)
                            .addComponent(saveQSButton))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(versionLabel)
                    .addComponent(websiteLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(optionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE))
        );
        getContentPane().remove(randomizerOptionsPane);
        getContentPane().setLayout(layout);
        /* @formatter:on */
    }

    private void fontFaceFix(Container root, Font font, Font boldFont) {
        for (Component c : root.getComponents()) {
            if (c != versionLabel) {
                c.setFont(font);
            }
            if (c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                Border b = jc.getBorder();
                if (b != null && b instanceof TitledBorder) {
                    ((TitledBorder) b).setTitleFont(boldFont);
                }
            }
            if (c instanceof Container) {
                fontFaceFix((Container) c, font, boldFont);
            }
        }

    }

    private void initTweaksPanel() {
        tweakCheckboxes = new ArrayList<JCheckBox>();
        int numTweaks = MiscTweak.allTweaks.size();
        for (int i = 0; i < numTweaks; i++) {
            MiscTweak ct = MiscTweak.allTweaks.get(i);
            JCheckBox tweakBox = new JCheckBox();
            tweakBox.setText(ct.getTweakName());
            tweakBox.setToolTipText(ct.getTooltipText());
            tweakCheckboxes.add(tweakBox);
        }
    }

    // config-related stuff

    private static final int TWEAK_COLS = 4;

    private GroupLayout makeTweaksLayout(List<JCheckBox> tweaks) {
        GroupLayout gl = new GroupLayout(miscTweaksPanel);
        int numTweaks = tweaks.size();

        // Handle columns
        SequentialGroup columnsGroup = gl.createSequentialGroup().addContainerGap();
        int numCols = Math.min(TWEAK_COLS, numTweaks);
        ParallelGroup[] colGroups = new ParallelGroup[numCols];
        for (int col = 0; col < numCols; col++) {
            if (col > 0) {
                columnsGroup.addGap(18, 18, 18);
            }
            colGroups[col] = gl.createParallelGroup(GroupLayout.Alignment.LEADING);
            columnsGroup.addGroup(colGroups[col]);
        }
        for (int tweak = 0; tweak < numTweaks; tweak++) {
            colGroups[tweak % numCols].addComponent(tweaks.get(tweak));
        }
        columnsGroup.addContainerGap();
        gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(columnsGroup));

        // And rows
        SequentialGroup rowsGroup = gl.createSequentialGroup().addContainerGap();
        int numRows = (numTweaks - 1) / numCols + 1;
        ParallelGroup[] rowGroups = new ParallelGroup[numRows];
        for (int row = 0; row < numRows; row++) {
            if (row > 0) {
                rowsGroup.addPreferredGap(ComponentPlacement.UNRELATED);
            }
            rowGroups[row] = gl.createParallelGroup(GroupLayout.Alignment.BASELINE);
            rowsGroup.addGroup(rowGroups[row]);
        }
        for (int tweak = 0; tweak < numTweaks; tweak++) {
            rowGroups[tweak / numCols].addComponent(tweaks.get(tweak));
        }
        rowsGroup.addContainerGap();
        gl.setVerticalGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(rowsGroup));
        return gl;
    }

    /**
     * Repurposed: now checks for converting old custom names format to new
     */
    private void checkCustomNames() {
        String[] cnamefiles = new String[] { SysConstants.tnamesFile, SysConstants.tclassesFile,
                SysConstants.nnamesFile };

        boolean foundFile = false;
        for (int file = 0; file < 3; file++) {
            File currentFile = new File(SysConstants.ROOT_PATH + cnamefiles[file]);
            if (currentFile.exists()) {
                foundFile = true;
                break;
            }
        }

        if (foundFile) {
            int response = JOptionPane.showConfirmDialog(RandomizerGUI.this,
                    bundle.getString("RandomizerGUI.convertNameFilesDialog.text"),
                    bundle.getString("RandomizerGUI.convertNameFilesDialog.title"), JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                try {
                    CustomNamesSet newNamesData = CustomNamesSet.importOldNames();
                    byte[] data = newNamesData.getBytes();
                    FileFunctions.writeBytesToFile(SysConstants.customNamesFile, data);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.convertNameFilesFailed"));
                }
            }

            haveCheckedCustomNames = true;
            attemptWriteConfig();
        }

    }

    private void attemptReadConfig() {
        File fh = new File(SysConstants.ROOT_PATH + "config.ini");
        if (!fh.exists() || !fh.canRead()) {
            return;
        }

        try {
            Scanner sc = new Scanner(fh, "UTF-8");
            while (sc.hasNextLine()) {
                String q = sc.nextLine().trim();
                if (q.contains("//")) {
                    q = q.substring(0, q.indexOf("//")).trim();
                }
                if (!q.isEmpty()) {
                    String[] tokens = q.split("=", 2);
                    if (tokens.length == 2) {
                        String key = tokens[0].trim();
                        if (key.equalsIgnoreCase("autoupdate")) {
                            autoUpdateEnabled = Boolean.parseBoolean(tokens[1].trim());
                        } else if (key.equalsIgnoreCase("checkedcustomnames172")) {
                            haveCheckedCustomNames = Boolean.parseBoolean(tokens[1].trim());
                        } else if (key.equalsIgnoreCase("usescrollpane")) {
                            useScrollPaneMode = Boolean.parseBoolean(tokens[1].trim());
                        }
                    }
                }
            }
            sc.close();
        } catch (IOException ex) {

        }
    }

    private boolean attemptWriteConfig() {
        File fh = new File(SysConstants.ROOT_PATH + "config.ini");
        if (fh.exists() && !fh.canWrite()) {
            return false;
        }

        try {
            PrintStream ps = new PrintStream(new FileOutputStream(fh), true, "UTF-8");
            ps.println("autoupdate=" + autoUpdateEnabled);
            ps.println("checkedcustomnames=true");
            ps.println("checkedcustomnames172=" + haveCheckedCustomNames);
            ps.println("usescrollpane=" + useScrollPaneMode);
            ps.close();
            return true;
        } catch (IOException e) {
            return false;
        }

    }

    private void testForRequiredConfigs() {
        try {
            Utils.testForRequiredConfigs();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    String.format(bundle.getString("RandomizerGUI.configFileMissing"), e.getMessage()));
            System.exit(1);
            return;
        }
    }

    // form initial state

    private void initialiseState() {
        this.romHandler = null;
        this.currentRestrictions = null;
        this.websiteLinkLabel.setText("<html><a href=\"" + SysConstants.WEBSITE_URL_ZX + "\">" + SysConstants.WEBSITE_URL_ZX
                + "</a>");
        initialFormState();
        this.romOpenChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
        this.romSaveChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
        if (new File(SysConstants.ROOT_PATH + "settings/").exists()) {
            this.qsOpenChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH + "settings/"));
            this.qsSaveChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH + "settings/"));
            this.qsUpdateChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH + "settings/"));
        } else {
            this.qsOpenChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
            this.qsSaveChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
            this.qsUpdateChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initialFormState() {
        // Disable all rom components
        this.goRemoveTradeEvosCheckBox.setEnabled(false);
        this.goUpdateMovesCheckBox.setEnabled(false);
        this.goUpdateMovesLegacyCheckBox.setEnabled(false);
        this.goCondenseEvosCheckBox.setEnabled(false);

        this.goRemoveTradeEvosCheckBox.setSelected(false);
        this.goUpdateMovesCheckBox.setSelected(false);
        this.goUpdateMovesLegacyCheckBox.setSelected(false);
        this.goCondenseEvosCheckBox.setSelected(false);

        this.goUpdateMovesLegacyCheckBox.setVisible(true);
        this.pokeLimitCB.setEnabled(false);
        this.pokeLimitCB.setSelected(false);
        this.pokeLimitBtn.setEnabled(false);
        this.pokeLimitBtn.setVisible(true);
        this.pokeLimitCB.setVisible(true);
        this.raceModeCB.setEnabled(false);
        this.raceModeCB.setSelected(false);
        this.brokenMovesCB.setEnabled(false);
        this.brokenMovesCB.setSelected(false);

        this.riRomNameLabel.setText(bundle.getString("RandomizerGUI.noRomLoaded"));
        this.riRomCodeLabel.setText("");
        this.riRomSupportLabel.setText("");

        this.loadQSButton.setEnabled(false);
        this.saveQSButton.setEnabled(false);

        this.pbsChangesUnchangedRB.setEnabled(false);
        this.pbsChangesRandomRB.setEnabled(false);
        this.pbsChangesShuffleRB.setEnabled(false);
        this.pbsChangesUnchangedRB.setSelected(true);
        this.pbsFollowEvolutionsCB.setEnabled(false);
        this.pbsFollowEvolutionsCB.setSelected(false);
        this.pbsUpdateStatsCB.setEnabled(false);
        this.pbsUpdateStatsCB.setSelected(false);
        this.pbsStandardEXPCurvesCB.setEnabled(false);
        this.pbsStandardEXPCurvesCB.setSelected(false);
        this.pbsEXPCurvesLegendaryRB.setEnabled(false);
        this.pbsEXPCurvesLegendaryRB.setSelected(true);
        this.pbsEXPCurvesStrongLegendaryRB.setEnabled(false);
        this.pbsEXPCurvesAllRB.setEnabled(false);

        this.abilitiesPanel.setVisible(true);
        this.paUnchangedRB.setEnabled(false);
        this.paRandomizeRB.setEnabled(false);
        this.paWonderGuardCB.setEnabled(false);
        this.paFollowEvolutionsCB.setEnabled(false);
        this.paBanTrappingCB.setEnabled(false);
        this.paBanNegativeCB.setEnabled(false);
        this.paBanBadCB.setEnabled(false);
        this.paUnchangedRB.setSelected(true);
        this.paWonderGuardCB.setSelected(false);
        this.paFollowEvolutionsCB.setSelected(false);
        this.paBanTrappingCB.setSelected(false);
        this.paBanNegativeCB.setSelected(false);
        this.paBanBadCB.setSelected(false);

        this.spCustomPoke1Chooser.setEnabled(false);
        this.spCustomPoke2Chooser.setEnabled(false);
        this.spCustomPoke3Chooser.setEnabled(false);
        this.spCustomPoke1Chooser.setSelectedIndex(0);
        this.spCustomPoke1Chooser.setModel(new DefaultComboBoxModel(new String[] { "--" }));
        this.spCustomPoke2Chooser.setSelectedIndex(0);
        this.spCustomPoke2Chooser.setModel(new DefaultComboBoxModel(new String[] { "--" }));
        this.spCustomPoke3Chooser.setSelectedIndex(0);
        this.spCustomPoke3Chooser.setModel(new DefaultComboBoxModel(new String[] { "--" }));
        this.spCustomRB.setEnabled(false);
        this.spRandomRB.setEnabled(false);
        this.spRandom2EvosRB.setEnabled(false);
        this.spUnchangedRB.setEnabled(false);
        this.spUnchangedRB.setSelected(true);
        this.spHeldItemsCB.setEnabled(false);
        this.spHeldItemsCB.setSelected(false);
        this.spHeldItemsCB.setVisible(true);
        this.spHeldItemsBanBadCB.setEnabled(false);
        this.spHeldItemsBanBadCB.setSelected(false);
        this.spHeldItemsBanBadCB.setVisible(true);

        this.mdRandomAccuracyCB.setEnabled(false);
        this.mdRandomAccuracyCB.setSelected(false);
        this.mdRandomPowerCB.setEnabled(false);
        this.mdRandomPowerCB.setSelected(false);
        this.mdRandomPPCB.setEnabled(false);
        this.mdRandomPPCB.setSelected(false);
        this.mdRandomTypeCB.setEnabled(false);
        this.mdRandomTypeCB.setSelected(false);
        this.mdRandomCategoryCB.setEnabled(false);
        this.mdRandomCategoryCB.setSelected(false);
        this.mdRandomCategoryCB.setVisible(true);

        this.pmsRandomTotalRB.setEnabled(false);
        this.pmsRandomTypeRB.setEnabled(false);
        this.pmsUnchangedRB.setEnabled(false);
        this.pmsUnchangedRB.setSelected(true);
        this.pmsMetronomeOnlyRB.setEnabled(false);
        this.pmsGuaranteedMovesCB.setEnabled(false);
        this.pmsGuaranteedMovesCB.setSelected(false);
        this.pmsGuaranteedMovesCB.setVisible(true);
        this.pmsGuaranteedMovesSlider.setEnabled(false);
        this.pmsGuaranteedMovesSlider.setValue(this.pmsGuaranteedMovesSlider.getMinimum());
        this.pmsReorderDamagingMovesCB.setEnabled(false);
        this.pmsReorderDamagingMovesCB.setSelected(false);
        this.pmsForceGoodDamagingCB.setEnabled(false);
        this.pmsForceGoodDamagingCB.setSelected(false);
        this.pmsForceGoodDamagingSlider.setEnabled(false);
        this.pmsForceGoodDamagingSlider.setValue(this.pmsForceGoodDamagingSlider.getMinimum());

        this.ptRandomFollowEvosRB.setEnabled(false);
        this.ptRandomTotalRB.setEnabled(false);
        this.ptUnchangedRB.setEnabled(false);
        this.ptUnchangedRB.setSelected(true);

        this.tpPowerLevelsCB.setEnabled(false);
        this.tpRandomRB.setEnabled(false);
        this.tpRivalCarriesStarterCB.setEnabled(false);
        this.tpTypeThemedRB.setEnabled(false);
        this.tpTypeWeightingCB.setEnabled(false);
        this.tpNoLegendariesCB.setEnabled(false);
        this.tpNoEarlyShedinjaCB.setEnabled(false);
        this.tpNoEarlyShedinjaCB.setVisible(true);
        this.tpEnforceDistribution.setEnabled(false);
        this.tpEnforceDistribution.setVisible(true);
        this.tpEnforceMainPlaythrough.setEnabled(false);
        this.tpEnforceMainPlaythrough.setVisible(true);
        this.tpUnchangedRB.setEnabled(false);
        this.tpForceFullyEvolvedCB.setEnabled(false);
        this.tpForceFullyEvolvedSlider.setEnabled(false);
        this.tpLevelModifierCB.setEnabled(false);
        this.tpLevelModifierSlider.setEnabled(false);

        this.tpUnchangedRB.setSelected(true);
        this.tpPowerLevelsCB.setSelected(false);
        this.tpRivalCarriesStarterCB.setSelected(false);
        this.tpTypeWeightingCB.setSelected(false);
        this.tpNoLegendariesCB.setSelected(false);
        this.tpNoEarlyShedinjaCB.setSelected(false);
        this.tpEnforceDistribution.setSelected(true);
        this.tpEnforceMainPlaythrough.setSelected(true);
        this.tpForceFullyEvolvedCB.setSelected(false);
        this.tpForceFullyEvolvedSlider.setValue(this.tpForceFullyEvolvedSlider.getMinimum());
        this.tpLevelModifierCB.setSelected(false);
        this.tpLevelModifierSlider.setValue(0);

        this.tnRandomizeCB.setEnabled(false);
        this.tcnRandomizeCB.setEnabled(false);

        this.tnRandomizeCB.setSelected(false);
        this.tcnRandomizeCB.setSelected(false);

        this.tnRandomizeCB.setVisible(true);
        this.tcnRandomizeCB.setVisible(true);

        this.wpUnchangedRB.setEnabled(false);
        this.wpRandomRB.setEnabled(false);
        this.wpArea11RB.setEnabled(false);
        this.wpGlobalRB.setEnabled(false);
        this.wpUnchangedRB.setSelected(true);

        this.wpARNoneRB.setEnabled(false);
        this.wpARCatchEmAllRB.setEnabled(false);
        this.wpARTypeThemedRB.setEnabled(false);
        this.wpARSimilarStrengthRB.setEnabled(false);
        this.wpARNoneRB.setSelected(true);

        this.wpUseTimeCB.setEnabled(false);
        this.wpUseTimeCB.setVisible(true);
        this.wpUseTimeCB.setSelected(false);

        this.wpNoLegendariesCB.setEnabled(false);
        this.wpNoLegendariesCB.setSelected(false);

        this.wpCatchRateCB.setEnabled(false);
        this.wpCatchRateCB.setSelected(false);
        this.wpCatchRateSlider.setEnabled(false);
        this.wpCatchRateSlider.setValue(this.wpCatchRateSlider.getMinimum());

        this.wpHeldItemsCB.setEnabled(false);
        this.wpHeldItemsCB.setSelected(false);
        this.wpHeldItemsCB.setVisible(true);
        this.wpHeldItemsBanBadCB.setEnabled(false);
        this.wpHeldItemsBanBadCB.setSelected(false);
        this.wpHeldItemsBanBadCB.setVisible(true);

        this.wpShakingGrassCB.setEnabled(false);
        this.wpShakingGrassCB.setSelected(false);
        this.wpShakingGrassCB.setVisible(true);

        this.wpLevelModifierCB.setEnabled(false);
        this.wpLevelModifierSlider.setEnabled(false);

        this.wpLevelModifierCB.setSelected(false);
        this.wpLevelModifierSlider.setValue(0);

        this.stpRandomL4LRB.setEnabled(false);
        this.stpRandomTotalRB.setEnabled(false);
        this.stpRandomPower.setEnabled(false);
        this.stpLimitMusketeers.setEnabled(false);
        this.stpLimit600.setEnabled(false);
        this.stpUnchangedRB.setEnabled(false);
        this.stpUnchangedRB.setSelected(false);
        this.stpLimitMusketeers.setEnabled(false);
        this.stpLimitMusketeers.setSelected(false);
        this.stpLimitMusketeers.setVisible(true);
        this.stpLimit600.setEnabled(false);
        this.stpLimit600.setSelected(false);
        this.stpLimit600.setVisible(true);

        this.tmmRandomRB.setEnabled(false);
        this.tmmUnchangedRB.setEnabled(false);
        this.tmmUnchangedRB.setSelected(true);

        this.thcRandomTotalRB.setEnabled(false);
        this.thcRandomTypeRB.setEnabled(false);
        this.thcUnchangedRB.setEnabled(false);
        this.thcFullRB.setEnabled(false);
        this.thcUnchangedRB.setSelected(true);

        this.tmLearningSanityCB.setEnabled(false);
        this.tmLearningSanityCB.setSelected(false);
        this.tmKeepFieldMovesCB.setEnabled(false);
        this.tmKeepFieldMovesCB.setSelected(false);
        this.tmFullHMCompatCB.setEnabled(false);
        this.tmFullHMCompatCB.setSelected(false);
        this.tmForceGoodDamagingCB.setEnabled(false);
        this.tmForceGoodDamagingCB.setSelected(false);
        this.tmForceGoodDamagingSlider.setEnabled(false);
        this.tmForceGoodDamagingSlider.setValue(this.tmForceGoodDamagingSlider.getMinimum());

        this.mtmRandomRB.setEnabled(false);
        this.mtmUnchangedRB.setEnabled(false);
        this.mtmUnchangedRB.setSelected(true);

        this.mtcRandomTotalRB.setEnabled(false);
        this.mtcRandomTypeRB.setEnabled(false);
        this.mtcUnchangedRB.setEnabled(false);
        this.mtcFullRB.setEnabled(false);
        this.mtcUnchangedRB.setSelected(true);

        this.mtLearningSanityCB.setEnabled(false);
        this.mtLearningSanityCB.setSelected(false);
        this.mtKeepFieldMovesCB.setEnabled(false);
        this.mtKeepFieldMovesCB.setSelected(false);
        this.mtForceGoodDamagingCB.setEnabled(false);
        this.mtForceGoodDamagingCB.setSelected(false);
        this.mtForceGoodDamagingSlider.setEnabled(false);
        this.mtForceGoodDamagingSlider.setValue(this.mtForceGoodDamagingSlider.getMinimum());

        this.mtMovesPanel.setVisible(true);
        this.mtCompatPanel.setVisible(true);
        this.mtNoExistLabel.setVisible(false);

        this.igtUnchangedRB.setEnabled(false);
        this.igtGivenOnlyRB.setEnabled(false);
        this.igtBothRB.setEnabled(false);
        this.igtUnchangedRB.setSelected(true);

        this.igtRandomItemCB.setEnabled(false);
        this.igtRandomItemCB.setSelected(false);
        this.igtRandomItemCB.setVisible(true);

        this.igtRandomIVsCB.setEnabled(false);
        this.igtRandomIVsCB.setSelected(false);
        this.igtRandomIVsCB.setVisible(true);

        this.igtRandomOTCB.setEnabled(false);
        this.igtRandomOTCB.setSelected(false);
        this.igtRandomOTCB.setVisible(true);

        this.igtRandomNicknameCB.setEnabled(false);
        this.igtRandomNicknameCB.setSelected(false);

        this.fiUnchangedRB.setEnabled(false);
        this.fiShuffleRB.setEnabled(false);
        this.fiRandomEvenRB.setEnabled(false);
        this.fiRandomRB.setEnabled(false);
        this.fiUnchangedRB.setSelected(true);

        this.fiBanBadCB.setEnabled(false);
        this.fiBanBadCB.setSelected(false);
        this.fiBanBadCB.setVisible(true);

        this.shUnchangedRB.setEnabled(false);
        this.shShuffleRB.setEnabled(false);
        this.shRandomRB.setEnabled(false);
        this.shUnchangedRB.setSelected(true);

        this.shBanBadCB.setEnabled(false);
        this.shBanBadCB.setSelected(false);
        this.shBanBadCB.setVisible(true);
        this.shBanRegularShopCB.setEnabled(false);
        this.shBanRegularShopCB.setSelected(false);
        this.shBanRegularShopCB.setVisible(true);
        this.shBanOPShopCB.setEnabled(false);
        this.shBanOPShopCB.setSelected(false);
        this.shBanOPShopCB.setVisible(true);
        this.shBalancePricesCB.setEnabled(false);
        this.shBalancePricesCB.setSelected(false);
        this.shBalancePricesCB.setVisible(true);
        this.shGuaranteeEvolutionItemsCB.setEnabled(false);
        this.shGuaranteeEvolutionItemsCB.setSelected(false);
        this.shGuaranteeEvolutionItemsCB.setVisible(true);

        this.peUnchangedRB.setSelected(true);
        this.peUnchangedRB.setEnabled(false);
        this.peRandomRB.setEnabled(false);
        this.peForceChangeCB.setSelected(false);
        this.peForceChangeCB.setEnabled(false);
        this.peThreeStagesCB.setSelected(false);
        this.peThreeStagesCB.setEnabled(false);
        this.peSameTypeCB.setSelected(false);
        this.peSameTypeCB.setEnabled(false);
        this.peSimilarStrengthCB.setSelected(false);
        this.peSimilarStrengthCB.setEnabled(false);

        for (JCheckBox cb : tweakCheckboxes) {
            cb.setVisible(true);
            cb.setEnabled(false);
            cb.setSelected(false);
        }

        this.mtNoneAvailableLabel.setVisible(false);
        miscTweaksPanel.setLayout(makeTweaksLayout(tweakCheckboxes));

        this.gameMascotLabel.setIcon(emptyIcon);
    }

    // rom loading

    private void loadROM() {
        romOpenChooser.setSelectedFile(null);
        int returnVal = romOpenChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File fh = romOpenChooser.getSelectedFile();
            try {
                Utils.validateRomFile(fh);
            } catch (Utils.InvalidROMException e) {
                switch (e.getType()) {
                case LENGTH:
                    JOptionPane.showMessageDialog(this,
                            String.format(bundle.getString("RandomizerGUI.tooShortToBeARom"), fh.getName()));
                    return;
                case ZIP_FILE:
                    JOptionPane.showMessageDialog(this,
                            String.format(bundle.getString("RandomizerGUI.openedZIPfile"), fh.getName()));
                    return;
                case RAR_FILE:
                    JOptionPane.showMessageDialog(this,
                            String.format(bundle.getString("RandomizerGUI.openedRARfile"), fh.getName()));
                    return;
                case IPS_FILE:
                    JOptionPane.showMessageDialog(this,
                            String.format(bundle.getString("RandomizerGUI.openedIPSfile"), fh.getName()));
                    return;
                case UNREADABLE:
                    JOptionPane.showMessageDialog(this,
                            String.format(bundle.getString("RandomizerGUI.unreadableRom"), fh.getName()));
                    return;
                }
            }

            for (RomHandler.Factory rhf : checkHandlers) {
                if (rhf.isLoadable(fh.getAbsolutePath())) {
                    this.romHandler = rhf.create(RandomSource.instance());
                    opDialog = new OperationDialog(bundle.getString("RandomizerGUI.loadingText"), this, true);
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            boolean romLoaded = false;
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    opDialog.setVisible(true);
                                }
                            });
                            try {
                                RandomizerGUI.this.romHandler.loadRom(fh.getAbsolutePath());
                                romLoaded = true;
                            } catch (Exception ex) {
                                attemptToLogException(ex, "RandomizerGUI.loadFailed", "RandomizerGUI.loadFailedNoLog");
                            }
                            final boolean loadSuccess = romLoaded;
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    RandomizerGUI.this.opDialog.setVisible(false);
                                    RandomizerGUI.this.initialFormState();
                                    if (loadSuccess) {
                                        RandomizerGUI.this.romLoaded();
                                    }
                                }
                            });
                        }
                    };
                    t.start();

                    return;
                }
            }
            JOptionPane.showMessageDialog(this,
                    String.format(bundle.getString("RandomizerGUI.unsupportedRom"), fh.getName()));
        }

    }

    private void romLoaded() {
        try {
            this.currentRestrictions = null;
            this.riRomNameLabel.setText(this.romHandler.getROMName());
            this.riRomCodeLabel.setText(this.romHandler.getROMCode());
            this.riRomSupportLabel.setText(bundle.getString("RandomizerGUI.romSupportPrefix") + " "
                    + this.romHandler.getSupportLevel());
            this.goUpdateMovesCheckBox.setSelected(false);
            this.goUpdateMovesCheckBox.setSelected(false);
            this.goUpdateMovesCheckBox.setEnabled(true);
            this.goUpdateMovesLegacyCheckBox.setSelected(false);
            this.goUpdateMovesLegacyCheckBox.setEnabled(false);
            this.goUpdateMovesLegacyCheckBox.setVisible(!(romHandler instanceof Gen5RomHandler));
            this.goRemoveTradeEvosCheckBox.setSelected(false);
            this.goRemoveTradeEvosCheckBox.setEnabled(true);
            this.goCondenseEvosCheckBox.setSelected(false);
            this.goCondenseEvosCheckBox.setEnabled(true);
            this.raceModeCB.setSelected(false);
            this.raceModeCB.setEnabled(true);

            this.pokeLimitCB.setSelected(false);
            this.pokeLimitBtn.setEnabled(false);
            this.pokeLimitCB.setEnabled(!(romHandler instanceof Gen1RomHandler || romHandler.isROMHack()));
            this.pokeLimitCB.setVisible(!(romHandler instanceof Gen1RomHandler || romHandler.isROMHack()));
            this.pokeLimitBtn.setVisible(!(romHandler instanceof Gen1RomHandler || romHandler.isROMHack()));

            this.brokenMovesCB.setSelected(false);
            this.brokenMovesCB.setEnabled(true);

            this.loadQSButton.setEnabled(true);
            this.saveQSButton.setEnabled(true);

            this.pbsChangesUnchangedRB.setEnabled(true);
            this.pbsChangesUnchangedRB.setSelected(true);
            this.pbsChangesRandomRB.setEnabled(true);
            this.pbsChangesShuffleRB.setEnabled(true);

            this.pbsStandardEXPCurvesCB.setEnabled(true);
            this.pbsStandardEXPCurvesCB.setSelected(false);

            this.pbsEXPCurvesLegendaryRB.setEnabled(false);
            this.pbsEXPCurvesLegendaryRB.setSelected(true);
            this.pbsEXPCurvesStrongLegendaryRB.setEnabled(false);
            this.pbsEXPCurvesAllRB.setEnabled(false);

            this.pbsUpdateStatsCB.setEnabled(romHandler.generationOfPokemon() < 6);
            this.pbsUpdateStatsCB.setSelected(false);

            if (romHandler.abilitiesPerPokemon() > 0) {
                this.paUnchangedRB.setEnabled(true);
                this.paUnchangedRB.setSelected(true);
                this.paRandomizeRB.setEnabled(true);
                this.paWonderGuardCB.setEnabled(false);
                this.paFollowEvolutionsCB.setEnabled(false);
            } else {
                this.abilitiesPanel.setVisible(false);
            }

            this.spUnchangedRB.setEnabled(true);
            this.spUnchangedRB.setSelected(true);

            this.spCustomPoke3Chooser.setVisible(true);
            if (romHandler.canChangeStarters()) {
                this.spCustomRB.setEnabled(true);
                this.spRandomRB.setEnabled(true);
                this.spRandom2EvosRB.setEnabled(true);
                if (romHandler.isYellow()) {
                    this.spCustomPoke3Chooser.setVisible(false);
                }
                populateDropdowns();
            }

            this.spHeldItemsCB.setSelected(false);
            boolean hasStarterHeldItems = (romHandler instanceof Gen2RomHandler || romHandler instanceof Gen3RomHandler);
            this.spHeldItemsCB.setEnabled(hasStarterHeldItems);
            this.spHeldItemsCB.setVisible(hasStarterHeldItems);
            this.spHeldItemsBanBadCB.setEnabled(false);
            this.spHeldItemsBanBadCB.setVisible(hasStarterHeldItems);
            this.stpLimitMusketeers.setEnabled(false);
            this.stpLimit600.setEnabled(false);

            this.mdRandomAccuracyCB.setEnabled(true);
            this.mdRandomPowerCB.setEnabled(true);
            this.mdRandomPPCB.setEnabled(true);
            this.mdRandomTypeCB.setEnabled(true);
            this.mdRandomCategoryCB.setEnabled(romHandler.hasPhysicalSpecialSplit());
            this.mdRandomCategoryCB.setVisible(romHandler.hasPhysicalSpecialSplit());

            this.pmsRandomTotalRB.setEnabled(true);
            this.pmsRandomTypeRB.setEnabled(true);
            this.pmsUnchangedRB.setEnabled(true);
            this.pmsUnchangedRB.setSelected(true);
            this.pmsMetronomeOnlyRB.setEnabled(true);

            this.pmsGuaranteedMovesCB.setVisible(romHandler.supportsFourStartingMoves());
            this.pmsGuaranteedMovesSlider.setVisible(romHandler.supportsFourStartingMoves());

            this.ptRandomFollowEvosRB.setEnabled(true);
            this.ptRandomTotalRB.setEnabled(true);
            this.ptUnchangedRB.setEnabled(true);
            this.ptUnchangedRB.setSelected(true);

            this.tpRandomRB.setEnabled(true);
            this.tpTypeThemedRB.setEnabled(true);
            this.tpEnforceDistribution.setEnabled(true);
            this.tpEnforceMainPlaythrough.setEnabled(true);
            this.tpUnchangedRB.setEnabled(true);
            this.tpUnchangedRB.setSelected(true);
            this.tnRandomizeCB.setEnabled(romHandler.canChangeTrainerText());
            this.tcnRandomizeCB.setEnabled(romHandler.canChangeTrainerText());
            this.tnRandomizeCB.setVisible(romHandler.canChangeTrainerText());
            this.tcnRandomizeCB.setVisible(romHandler.canChangeTrainerText());

            if (romHandler.generationOfPokemon() < 3) {
                this.tpNoEarlyShedinjaCB.setVisible(false);
            } else {
                this.tpNoEarlyShedinjaCB.setVisible(true);
            }
            this.tpNoEarlyShedinjaCB.setSelected(false);

            if (romHandler.generationOfPokemon() < 5) {
                this.tpEnforceDistribution.setVisible(false);
                this.tpEnforceMainPlaythrough.setVisible(false);
            } else {
                this.tpEnforceDistribution.setVisible(true);
                this.tpEnforceMainPlaythrough.setVisible(true);
            }

            this.tpEnforceDistribution.setSelected(false);
            this.tpEnforceMainPlaythrough.setSelected(false);

            this.wpArea11RB.setEnabled(true);
            this.wpGlobalRB.setEnabled(true);
            this.wpRandomRB.setEnabled(true);
            this.wpUnchangedRB.setEnabled(true);
            this.wpUnchangedRB.setSelected(true);
            this.wpUseTimeCB.setEnabled(false);
            this.wpNoLegendariesCB.setEnabled(false);
            if (!romHandler.hasTimeBasedEncounters()) {
                this.wpUseTimeCB.setVisible(false);
            }
            this.wpCatchRateCB.setEnabled(true);

            this.wpHeldItemsCB.setSelected(false);
            this.wpHeldItemsCB.setEnabled(true);
            this.wpHeldItemsCB.setVisible(true);
            this.wpHeldItemsBanBadCB.setSelected(false);
            this.wpHeldItemsBanBadCB.setEnabled(false);
            this.wpHeldItemsBanBadCB.setVisible(true);
            if (romHandler instanceof Gen1RomHandler) {
                this.wpHeldItemsCB.setVisible(false);
                this.wpHeldItemsBanBadCB.setVisible(false);
            }
            this.wpShakingGrassCB.setSelected(false);
            this.wpShakingGrassCB.setEnabled(false);
            if (romHandler.generationOfPokemon() < 5) {
                this.wpShakingGrassCB.setVisible(false);
            } else {
                this.wpShakingGrassCB.setVisible(true);
            }

            this.stpUnchangedRB.setEnabled(true);
            if (this.romHandler.canChangeStaticPokemon()) {
                this.stpRandomL4LRB.setEnabled(true);
                this.stpRandomTotalRB.setEnabled(true);
                this.stpRandomPower.setEnabled(true);
                if (romHandler.generationOfPokemon() < 5) {
                    this.stpLimitMusketeers.setVisible(false);
                    this.stpLimitMusketeers.setEnabled(false);
                } else {
                    this.stpLimitMusketeers.setVisible(true);
                    this.stpLimitMusketeers.setEnabled(true);
                }
                this.stpLimit600.setEnabled(true);

            }

            this.tmmRandomRB.setEnabled(true);
            this.tmmUnchangedRB.setEnabled(true);
            this.tmFullHMCompatCB.setEnabled(true);

            this.thcRandomTotalRB.setEnabled(true);
            this.thcRandomTypeRB.setEnabled(true);
            this.thcUnchangedRB.setEnabled(true);
            this.thcFullRB.setEnabled(true);

            if (this.romHandler.hasMoveTutors()) {
                this.mtmRandomRB.setEnabled(true);
                this.mtmUnchangedRB.setEnabled(true);

                this.mtcRandomTotalRB.setEnabled(true);
                this.mtcRandomTypeRB.setEnabled(true);
                this.mtcUnchangedRB.setEnabled(true);
                this.mtcFullRB.setEnabled(true);
            } else {
                this.mtCompatPanel.setVisible(false);
                this.mtMovesPanel.setVisible(false);
                this.mtNoExistLabel.setVisible(true);
            }

            this.igtUnchangedRB.setEnabled(true);
            this.igtBothRB.setEnabled(true);
            this.igtGivenOnlyRB.setEnabled(true);

            if (this.romHandler instanceof Gen1RomHandler) {
                this.igtRandomItemCB.setVisible(false);
                this.igtRandomIVsCB.setVisible(false);
                this.igtRandomOTCB.setVisible(false);
            }

            this.fiUnchangedRB.setEnabled(true);
            this.fiRandomRB.setEnabled(true);
            this.fiRandomEvenRB.setEnabled(true);
            this.fiShuffleRB.setEnabled(true);

            this.fiBanBadCB.setEnabled(false);
            this.fiBanBadCB.setSelected(false);

            if (!romHandler.hasShopRandomization()) {
                this.shopItemsPanel.setVisible(false);
            } else {
                this.shopItemsPanel.setVisible(true);
            }

            this.shUnchangedRB.setEnabled(true);
            this.shShuffleRB.setEnabled(true);
            this.shRandomRB.setEnabled(true);

            this.shBanBadCB.setEnabled(false);
            this.shBanBadCB.setSelected(false);
            this.shBanRegularShopCB.setEnabled(false);
            this.shBanRegularShopCB.setSelected(false);
            this.shBanOPShopCB.setEnabled(false);
            this.shBanOPShopCB.setSelected(false);
            this.shBalancePricesCB.setEnabled(false);
            this.shBalancePricesCB.setSelected(false);
            this.shGuaranteeEvolutionItemsCB.setEnabled(false);
            this.shGuaranteeEvolutionItemsCB.setSelected(false);
            
            this.peUnchangedRB.setEnabled(true);
            this.peUnchangedRB.setSelected(true);
            this.peRandomRB.setEnabled(true);

            int mtsAvailable = this.romHandler.miscTweaksAvailable();
            int mtCount = MiscTweak.allTweaks.size();
            List<JCheckBox> usableCheckboxes = new ArrayList<JCheckBox>();

            for (int mti = 0; mti < mtCount; mti++) {
                MiscTweak mt = MiscTweak.allTweaks.get(mti);
                JCheckBox mtCB = tweakCheckboxes.get(mti);
                mtCB.setSelected(false);
                if ((mtsAvailable & mt.getValue()) != 0) {
                    mtCB.setVisible(true);
                    mtCB.setEnabled(true);
                    usableCheckboxes.add(mtCB);
                } else {
                    mtCB.setVisible(false);
                    mtCB.setEnabled(false);
                }
            }

            if (usableCheckboxes.size() > 0) {
                this.mtNoneAvailableLabel.setVisible(false);
                miscTweaksPanel.setLayout(makeTweaksLayout(usableCheckboxes));
            } else {
                this.mtNoneAvailableLabel.setVisible(true);
                miscTweaksPanel.setLayout(noTweaksLayout);
            }

            this.gameMascotLabel.setIcon(makeMascotIcon());

            if (this.romHandler instanceof AbstractDSRomHandler) {
                ((AbstractDSRomHandler) this.romHandler).closeInnerRom();
            }
        } catch (Exception ex) {
            attemptToLogException(ex, "RandomizerGUI.processFailed", "RandomizerGUI.processFailedNoLog");
            this.romHandler = null;
            this.initialFormState();
        }
    }

    private ImageIcon makeMascotIcon() {
        try {
            BufferedImage handlerImg = romHandler.getMascotImage();

            if (handlerImg == null) {
                return emptyIcon;
            }

            BufferedImage nImg = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
            int hW = handlerImg.getWidth();
            int hH = handlerImg.getHeight();
            nImg.getGraphics().drawImage(handlerImg, 64 - hW / 2, 64 - hH / 2, this);
            return new ImageIcon(nImg);
        } catch (Exception ex) {
            return emptyIcon;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void populateDropdowns() {
        List<Pokemon> currentStarters = romHandler.getStarters();
        List<Pokemon> allPokes = romHandler.getPokemon();
        String[] pokeNames = new String[allPokes.size() - 1];
        for (int i = 1; i < allPokes.size(); i++) {
            pokeNames[i - 1] = allPokes.get(i).fullName();
        }
        this.spCustomPoke1Chooser.setModel(new DefaultComboBoxModel(pokeNames));
        this.spCustomPoke1Chooser.setSelectedIndex(allPokes.indexOf(currentStarters.get(0)) - 1);
        this.spCustomPoke2Chooser.setModel(new DefaultComboBoxModel(pokeNames));
        this.spCustomPoke2Chooser.setSelectedIndex(allPokes.indexOf(currentStarters.get(1)) - 1);
        if (!romHandler.isYellow()) {
            this.spCustomPoke3Chooser.setModel(new DefaultComboBoxModel(pokeNames));
            this.spCustomPoke3Chooser.setSelectedIndex(allPokes.indexOf(currentStarters.get(2)) - 1);
        }
    }

    private void enableOrDisableSubControls() {
        // This isn't for a new ROM being loaded (that's romLoaded)
        // This is just for when a radio button gets selected or state is loaded
        // and we need to enable/disable secondary controls
        // e.g. wild pokemon / trainer pokemon "modifier"
        // and the 3 starter pokemon dropdowns

        if (this.goUpdateMovesCheckBox.isSelected() && !(romHandler instanceof Gen5RomHandler)) {
            this.goUpdateMovesLegacyCheckBox.setEnabled(true);
        } else {
            this.goUpdateMovesLegacyCheckBox.setEnabled(false);
            this.goUpdateMovesLegacyCheckBox.setSelected(false);
        }

        this.pokeLimitBtn.setEnabled(this.pokeLimitCB.isSelected());

        if (this.spCustomRB.isSelected()) {
            this.spCustomPoke1Chooser.setEnabled(true);
            this.spCustomPoke2Chooser.setEnabled(true);
            this.spCustomPoke3Chooser.setEnabled(true);
        } else {
            this.spCustomPoke1Chooser.setEnabled(false);
            this.spCustomPoke2Chooser.setEnabled(false);
            this.spCustomPoke3Chooser.setEnabled(false);
        }

        if (this.pbsChangesUnchangedRB.isSelected()) {
            this.pbsFollowEvolutionsCB.setEnabled(false);
            this.pbsFollowEvolutionsCB.setSelected(false);
        } else {
            this.pbsFollowEvolutionsCB.setEnabled(true);
        }

        if (this.pbsStandardEXPCurvesCB.isSelected()) {
            this.pbsEXPCurvesLegendaryRB.setEnabled(true);
            this.pbsEXPCurvesStrongLegendaryRB.setEnabled(true);
            this.pbsEXPCurvesAllRB.setEnabled(true);
        } else {
            this.pbsEXPCurvesLegendaryRB.setEnabled(false);
            this.pbsEXPCurvesStrongLegendaryRB.setEnabled(false);
            this.pbsEXPCurvesAllRB.setEnabled(false);
        }

        if (this.spHeldItemsCB.isSelected() && this.spHeldItemsCB.isVisible() && this.spHeldItemsCB.isEnabled()) {
            this.spHeldItemsBanBadCB.setEnabled(true);
        } else {
            this.spHeldItemsBanBadCB.setEnabled(false);
            this.spHeldItemsBanBadCB.setSelected(false);
        }

        if (this.paRandomizeRB.isSelected()) {
            this.paWonderGuardCB.setEnabled(true);
            this.paFollowEvolutionsCB.setEnabled(true);
            this.paBanTrappingCB.setEnabled(true);
            this.paBanNegativeCB.setEnabled(true);
            this.paBanBadCB.setEnabled(true);
        } else {
            this.paWonderGuardCB.setEnabled(false);
            this.paWonderGuardCB.setSelected(false);
            this.paFollowEvolutionsCB.setEnabled(false);
            this.paFollowEvolutionsCB.setSelected(false);
            this.paBanTrappingCB.setEnabled(false);
            this.paBanTrappingCB.setSelected(false);
            this.paBanNegativeCB.setEnabled(false);
            this.paBanNegativeCB.setSelected(false);
            this.paBanBadCB.setEnabled(false);
            this.paBanBadCB.setSelected(false);
        }

        if (this.tpUnchangedRB.isSelected()) {
            this.tpPowerLevelsCB.setEnabled(false);
            this.tpNoLegendariesCB.setEnabled(false);
            this.tpNoEarlyShedinjaCB.setEnabled(false);
            this.tpNoEarlyShedinjaCB.setSelected(false);
            this.tpForceFullyEvolvedCB.setEnabled(false);
            this.tpForceFullyEvolvedCB.setSelected(false);
            this.tpLevelModifierCB.setEnabled(false);
            this.tpLevelModifierCB.setSelected(false);
        } else {
            this.tpPowerLevelsCB.setEnabled(true);
            this.tpNoLegendariesCB.setEnabled(true);
            this.tpNoEarlyShedinjaCB.setEnabled(true);
            this.tpEnforceDistribution.setEnabled(true);
            this.tpEnforceMainPlaythrough.setEnabled(true);
            this.tpForceFullyEvolvedCB.setEnabled(true);
            this.tpLevelModifierCB.setEnabled(true);
        }

        if (this.tpForceFullyEvolvedCB.isSelected()) {
            this.tpForceFullyEvolvedSlider.setEnabled(true);
        } else {
            this.tpForceFullyEvolvedSlider.setEnabled(false);
            this.tpForceFullyEvolvedSlider.setValue(this.tpForceFullyEvolvedSlider.getMinimum());
        }

        if (this.tpLevelModifierCB.isSelected()) {
            this.tpLevelModifierSlider.setEnabled(true);
        } else {
            this.tpLevelModifierSlider.setEnabled(false);
            this.tpLevelModifierSlider.setValue(0);
        }

        if (!this.spUnchangedRB.isSelected() || !this.tpUnchangedRB.isSelected()) {
            this.tpRivalCarriesStarterCB.setEnabled(true);
        } else {
            this.tpRivalCarriesStarterCB.setEnabled(false);
            this.tpRivalCarriesStarterCB.setSelected(false);
        }

        if (this.tpTypeThemedRB.isSelected()) {
            this.tpTypeWeightingCB.setEnabled(true);
        } else {
            this.tpTypeWeightingCB.setEnabled(false);
        }
        if (this.wpRandomRB.isSelected()) {
            this.wpARNoneRB.setEnabled(true);
            this.wpARSimilarStrengthRB.setEnabled(true);
            this.wpARCatchEmAllRB.setEnabled(true);
            this.wpARTypeThemedRB.setEnabled(true);
            this.wpShakingGrassCB.setEnabled(true);
        } else if (this.wpArea11RB.isSelected()) {
            this.wpARNoneRB.setEnabled(true);
            this.wpARSimilarStrengthRB.setEnabled(true);
            this.wpARCatchEmAllRB.setEnabled(true);
            this.wpARTypeThemedRB.setEnabled(true);
            this.wpShakingGrassCB.setEnabled(false);
        } else if (this.wpGlobalRB.isSelected()) {
            if (this.wpARCatchEmAllRB.isSelected() || this.wpARTypeThemedRB.isSelected()) {
                this.wpARNoneRB.setSelected(true);
            }
            this.wpARNoneRB.setEnabled(true);
            this.wpARSimilarStrengthRB.setEnabled(true);
            this.wpARCatchEmAllRB.setEnabled(false);
            this.wpARTypeThemedRB.setEnabled(false);
            this.wpShakingGrassCB.setEnabled(false);
        } else {
            this.wpARNoneRB.setEnabled(false);
            this.wpARSimilarStrengthRB.setEnabled(false);
            this.wpARCatchEmAllRB.setEnabled(false);
            this.wpARTypeThemedRB.setEnabled(false);
            this.wpARNoneRB.setSelected(true);
            this.wpShakingGrassCB.setEnabled(false);
        }

        if (this.wpUnchangedRB.isSelected()) {
            this.wpUseTimeCB.setEnabled(false);
            this.wpNoLegendariesCB.setEnabled(false);
            this.wpLevelModifierCB.setEnabled(false);
            this.wpLevelModifierCB.setSelected(false);
        } else {
            this.wpUseTimeCB.setEnabled(true);
            this.wpNoLegendariesCB.setEnabled(true);
            this.wpLevelModifierCB.setEnabled(true);
        }

        if (this.wpHeldItemsCB.isSelected() && this.wpHeldItemsCB.isVisible() && this.wpHeldItemsCB.isEnabled()) {
            this.wpHeldItemsBanBadCB.setEnabled(true);
        } else {
            this.wpHeldItemsBanBadCB.setEnabled(false);
            this.wpHeldItemsBanBadCB.setSelected(false);
        }

        if (this.wpCatchRateCB.isSelected()) {
            this.wpCatchRateSlider.setEnabled(true);
        } else {
            this.wpCatchRateSlider.setEnabled(false);
            this.wpCatchRateSlider.setValue(this.wpCatchRateSlider.getMinimum());
        }

        if (this.wpLevelModifierCB.isSelected()) {
            this.wpLevelModifierSlider.setEnabled(true);
        } else {
            this.wpLevelModifierSlider.setEnabled(false);
            this.wpLevelModifierSlider.setValue(0);
        }

        if (this.igtUnchangedRB.isSelected()) {
            this.igtRandomItemCB.setEnabled(false);
            this.igtRandomIVsCB.setEnabled(false);
            this.igtRandomNicknameCB.setEnabled(false);
            this.igtRandomOTCB.setEnabled(false);
        } else {
            this.igtRandomItemCB.setEnabled(true);
            this.igtRandomIVsCB.setEnabled(true);
            this.igtRandomNicknameCB.setEnabled(true);
            this.igtRandomOTCB.setEnabled(true);
        }

        if (this.pmsMetronomeOnlyRB.isSelected()) {
            this.tmmUnchangedRB.setEnabled(false);
            this.tmmRandomRB.setEnabled(false);
            this.tmmUnchangedRB.setSelected(true);

            this.mtmUnchangedRB.setEnabled(false);
            this.mtmRandomRB.setEnabled(false);
            this.mtmUnchangedRB.setSelected(true);

            this.tmLearningSanityCB.setEnabled(false);
            this.tmLearningSanityCB.setSelected(false);
            this.tmKeepFieldMovesCB.setEnabled(false);
            this.tmKeepFieldMovesCB.setSelected(false);
            this.tmForceGoodDamagingCB.setEnabled(false);
            this.tmForceGoodDamagingCB.setSelected(false);

            this.mtLearningSanityCB.setEnabled(false);
            this.mtLearningSanityCB.setSelected(false);
            this.mtKeepFieldMovesCB.setEnabled(false);
            this.mtKeepFieldMovesCB.setSelected(false);
            this.mtForceGoodDamagingCB.setEnabled(false);
            this.mtForceGoodDamagingCB.setSelected(false);
        } else {
            this.tmmUnchangedRB.setEnabled(true);
            this.tmmRandomRB.setEnabled(true);

            this.mtmUnchangedRB.setEnabled(true);
            this.mtmRandomRB.setEnabled(true);

            if (!(this.pmsUnchangedRB.isSelected()) || !(this.tmmUnchangedRB.isSelected())
                    || !(this.thcUnchangedRB.isSelected())) {
                this.tmLearningSanityCB.setEnabled(true);
            } else {
                this.tmLearningSanityCB.setEnabled(false);
                this.tmLearningSanityCB.setSelected(false);
            }

            if (!(this.tmmUnchangedRB.isSelected())) {
                this.tmKeepFieldMovesCB.setEnabled(true);
                this.tmForceGoodDamagingCB.setEnabled(true);
            } else {
                this.tmKeepFieldMovesCB.setEnabled(false);
                this.tmKeepFieldMovesCB.setSelected(false);
                this.tmForceGoodDamagingCB.setEnabled(false);
                this.tmForceGoodDamagingCB.setSelected(false);
            }

            if (this.romHandler.hasMoveTutors()
                    && (!(this.pmsUnchangedRB.isSelected()) || !(this.mtmUnchangedRB.isSelected()) || !(this.mtcUnchangedRB
                            .isSelected()))) {
                this.mtLearningSanityCB.setEnabled(true);
            } else {
                this.mtLearningSanityCB.setEnabled(false);
                this.mtLearningSanityCB.setSelected(false);
            }

            if (this.romHandler.hasMoveTutors() && !(this.mtmUnchangedRB.isSelected())) {
                this.mtKeepFieldMovesCB.setEnabled(true);
                this.mtForceGoodDamagingCB.setEnabled(true);
            } else {
                this.mtKeepFieldMovesCB.setEnabled(false);
                this.mtKeepFieldMovesCB.setSelected(false);
                this.mtForceGoodDamagingCB.setEnabled(false);
                this.mtForceGoodDamagingCB.setSelected(false);
            }
        }

        if (this.tmForceGoodDamagingCB.isSelected()) {
            this.tmForceGoodDamagingSlider.setEnabled(true);
        } else {
            this.tmForceGoodDamagingSlider.setEnabled(false);
            this.tmForceGoodDamagingSlider.setValue(this.tmForceGoodDamagingSlider.getMinimum());
        }

        if (this.mtForceGoodDamagingCB.isSelected()) {
            this.mtForceGoodDamagingSlider.setEnabled(true);
        } else {
            this.mtForceGoodDamagingSlider.setEnabled(false);
            this.mtForceGoodDamagingSlider.setValue(this.mtForceGoodDamagingSlider.getMinimum());
        }

        this.tmFullHMCompatCB.setEnabled(!this.thcFullRB.isSelected());

        if (this.pmsMetronomeOnlyRB.isSelected() || this.pmsUnchangedRB.isSelected()) {
            this.pmsGuaranteedMovesCB.setEnabled(false);
            this.pmsGuaranteedMovesCB.setSelected(false);
            this.pmsForceGoodDamagingCB.setEnabled(false);
            this.pmsForceGoodDamagingCB.setSelected(false);
            this.pmsReorderDamagingMovesCB.setEnabled(false);
            this.pmsReorderDamagingMovesCB.setSelected(false);
        } else {
            this.pmsGuaranteedMovesCB.setEnabled(true);
            this.pmsForceGoodDamagingCB.setEnabled(true);
            this.pmsReorderDamagingMovesCB.setEnabled(true);
        }

        if (this.pmsGuaranteedMovesCB.isSelected()) {
            this.pmsGuaranteedMovesSlider.setEnabled(true);
        } else {
            this.pmsGuaranteedMovesSlider.setEnabled(false);
            this.pmsGuaranteedMovesSlider.setValue(this.pmsGuaranteedMovesSlider.getMinimum());
        }

        if (this.pmsForceGoodDamagingCB.isSelected()) {
            this.pmsForceGoodDamagingSlider.setEnabled(true);
        } else {
            this.pmsForceGoodDamagingSlider.setEnabled(false);
            this.pmsForceGoodDamagingSlider.setValue(this.pmsForceGoodDamagingSlider.getMinimum());
        }

        if (this.fiRandomRB.isSelected() && this.fiRandomRB.isVisible() && this.fiRandomRB.isEnabled()) {
            this.fiBanBadCB.setEnabled(true);
        } else if 
                (this.fiRandomEvenRB.isSelected() && this.fiRandomEvenRB.isVisible() && this.fiRandomEvenRB.isEnabled()) {
                this.fiBanBadCB.setEnabled(true);
        } else {
            this.fiBanBadCB.setEnabled(false);
            this.fiBanBadCB.setSelected(false);
        }

        

        
        if (this.romHandler.generationOfPokemon() == 5) {
            this.fiRandomEvenRB.setEnabled(true);
        } else {
            this.fiRandomEvenRB.setEnabled(false);
            this.fiRandomEvenRB.setSelected(false);
        }

        if (this.shRandomRB.isSelected() && this.shRandomRB.isVisible() && this.shRandomRB.isEnabled()) {
            this.shBanBadCB.setEnabled(true);
            this.shBanRegularShopCB.setEnabled(true);
            this.shBanOPShopCB.setEnabled(true);
            this.shBalancePricesCB.setEnabled(true);
            this.shGuaranteeEvolutionItemsCB.setEnabled(true);
        } else {
            this.shBanBadCB.setEnabled(false);
            this.shBanBadCB.setSelected(false);
            this.shBanRegularShopCB.setEnabled(false);
            this.shBanRegularShopCB.setSelected(false);
            this.shBanOPShopCB.setEnabled(false);
            this.shBanOPShopCB.setSelected(false);
            this.shBalancePricesCB.setEnabled(false);
            this.shBalancePricesCB.setSelected(false);
            this.shGuaranteeEvolutionItemsCB.setEnabled(false);
            this.shGuaranteeEvolutionItemsCB.setSelected(false);
        }

        this.peForceChangeCB.setEnabled(this.peRandomRB.isSelected());
        this.peThreeStagesCB.setEnabled(this.peRandomRB.isSelected());
        this.peSameTypeCB.setEnabled(this.peRandomRB.isSelected());
        this.peSimilarStrengthCB.setEnabled(this.peRandomRB.isSelected());
    }

    private void saveROM() {
        if (romHandler == null) {
            return; // none loaded
        }
        if (raceModeCB.isSelected() && tpUnchangedRB.isSelected() && wpUnchangedRB.isSelected()) {
            JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.raceModeRequirements"));
            return;
        }
        if (pokeLimitCB.isSelected()
                && (this.currentRestrictions == null || this.currentRestrictions.nothingSelected())) {
            JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.pokeLimitNotChosen"));
            return;
        }
        romSaveChooser.setSelectedFile(null);
        int returnVal = romSaveChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fh = romSaveChooser.getSelectedFile();
            // Fix or add extension
            List<String> extensions = new ArrayList<String>(Arrays.asList("sgb", "gbc", "gba", "nds"));
            extensions.remove(this.romHandler.getDefaultExtension());
            fh = FileFunctions.fixFilename(fh, this.romHandler.getDefaultExtension(), extensions);
            boolean allowed = true;
            if (this.romHandler instanceof AbstractDSRomHandler) {
                String currentFN = this.romHandler.loadedFilename();
                if (currentFN.equals(fh.getAbsolutePath())) {
                    JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.cantOverwriteDS"));
                    allowed = false;
                }
            }
            if (allowed) {
                // Get a seed
                long seed = RandomSource.pickSeed();
                // Apply it
                RandomSource.seed(seed);
                presetMode = false;

                try {
                    CustomNamesSet cns = FileFunctions.getCustomNames();
                    performRandomization(fh.getAbsolutePath(), seed, cns);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.cantLoadCustomNames"));
                }

            }
        }
    }

    private Settings getCurrentSettings() throws IOException {
        Settings settings = createSettingsFromState(FileFunctions.getCustomNames());
        return settings;
    }

    public String getValidRequiredROMName(String config, CustomNamesSet customNames)
            throws UnsupportedEncodingException, InvalidSupplementFilesException {
        try {
            Utils.validatePresetSupplementFiles(config, customNames);
        } catch (InvalidSupplementFilesException e) {
            switch (e.getType()) {
            case CUSTOM_NAMES:
                JOptionPane.showMessageDialog(null, bundle.getString("RandomizerGUI.presetFailTrainerNames"));
                throw e;
            default:
                throw e;
            }
        }
        byte[] data = Base64.getDecoder().decode(config);

        int nameLength = data[Settings.LENGTH_OF_SETTINGS_DATA] & 0xFF;
        if (data.length != Settings.LENGTH_OF_SETTINGS_DATA + 9 + nameLength) {
            return null; // not valid length
        }
        String name = new String(data, Settings.LENGTH_OF_SETTINGS_DATA + 1, nameLength, "US-ASCII");
        return name;
    }

    private void restoreStateFromSettings(Settings settings) {
        this.goRemoveTradeEvosCheckBox.setSelected(settings.isChangeImpossibleEvolutions());
        this.goUpdateMovesCheckBox.setSelected(settings.isUpdateMoves());
        this.goUpdateMovesLegacyCheckBox.setSelected(settings.isUpdateMovesLegacy());
        this.tnRandomizeCB.setSelected(settings.isRandomizeTrainerNames());
        this.tcnRandomizeCB.setSelected(settings.isRandomizeTrainerClassNames());

        this.pbsChangesRandomRB.setSelected(settings.getBaseStatisticsMod() == Settings.BaseStatisticsMod.RANDOM);
        this.pbsChangesShuffleRB.setSelected(settings.getBaseStatisticsMod() == Settings.BaseStatisticsMod.SHUFFLE);
        this.pbsChangesUnchangedRB.setSelected(settings.getBaseStatisticsMod() == Settings.BaseStatisticsMod.UNCHANGED);
        this.pbsFollowEvolutionsCB.setSelected(settings.isBaseStatsFollowEvolutions());
        this.pbsUpdateStatsCB.setSelected(settings.isUpdateBaseStats());
        this.pbsStandardEXPCurvesCB.setSelected(settings.isStandardizeEXPCurves());
        this.pbsEXPCurvesLegendaryRB.setSelected(settings.getExpCurveMod() == Settings.ExpCurveMod.LEGENDARIES);
        this.pbsEXPCurvesStrongLegendaryRB.setSelected(settings.getExpCurveMod() == Settings.ExpCurveMod.STRONG_LEGENDARIES);
        this.pbsEXPCurvesAllRB.setSelected(settings.getExpCurveMod() == Settings.ExpCurveMod.ALL);

        this.paUnchangedRB.setSelected(settings.getAbilitiesMod() == Settings.AbilitiesMod.UNCHANGED);
        this.paRandomizeRB.setSelected(settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE);
        this.paWonderGuardCB.setSelected(settings.isAllowWonderGuard());
        this.paFollowEvolutionsCB.setSelected(settings.isAbilitiesFollowEvolutions());
        this.paBanTrappingCB.setSelected(settings.isBanTrappingAbilities());
        this.paBanNegativeCB.setSelected(settings.isBanNegativeAbilities());
        this.paBanBadCB.setSelected(settings.isBanBadAbilities());

        this.ptRandomFollowEvosRB.setSelected(settings.getTypesMod() == Settings.TypesMod.RANDOM_FOLLOW_EVOLUTIONS);
        this.ptRandomTotalRB.setSelected(settings.getTypesMod() == Settings.TypesMod.COMPLETELY_RANDOM);
        this.ptUnchangedRB.setSelected(settings.getTypesMod() == Settings.TypesMod.UNCHANGED);
        this.raceModeCB.setSelected(settings.isRaceMode());
        this.brokenMovesCB.setSelected(settings.doBlockBrokenMoves());
        this.pokeLimitCB.setSelected(settings.isLimitPokemon());

        this.goCondenseEvosCheckBox.setSelected(settings.isMakeEvolutionsEasier());

        this.spCustomRB.setSelected(settings.getStartersMod() == Settings.StartersMod.CUSTOM);
        this.spRandomRB.setSelected(settings.getStartersMod() == Settings.StartersMod.COMPLETELY_RANDOM);
        this.spUnchangedRB.setSelected(settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
        this.spRandom2EvosRB.setSelected(settings.getStartersMod() == Settings.StartersMod.RANDOM_WITH_TWO_EVOLUTIONS);
        this.spHeldItemsCB.setSelected(settings.isRandomizeStartersHeldItems());
        this.spHeldItemsBanBadCB.setSelected(settings.isBanBadRandomStarterHeldItems());

        int[] customStarters = settings.getCustomStarters();
        this.spCustomPoke1Chooser.setSelectedIndex(customStarters[0] - 1);
        this.spCustomPoke2Chooser.setSelectedIndex(customStarters[1] - 1);
        this.spCustomPoke3Chooser.setSelectedIndex(customStarters[2] - 1);

        this.peUnchangedRB.setSelected(settings.getEvolutionsMod() == Settings.EvolutionsMod.UNCHANGED);
        this.peRandomRB.setSelected(settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM);
        this.peSimilarStrengthCB.setSelected(settings.isEvosSimilarStrength());
        this.peSameTypeCB.setSelected(settings.isEvosSameTyping());
        this.peThreeStagesCB.setSelected(settings.isEvosMaxThreeStages());
        this.peForceChangeCB.setSelected(settings.isEvosForceChange());

        this.mdRandomAccuracyCB.setSelected(settings.isRandomizeMoveAccuracies());
        this.mdRandomCategoryCB.setSelected(settings.isRandomizeMoveCategory());
        this.mdRandomPowerCB.setSelected(settings.isRandomizeMovePowers());
        this.mdRandomPPCB.setSelected(settings.isRandomizeMovePPs());
        this.mdRandomTypeCB.setSelected(settings.isRandomizeMoveTypes());

        this.pmsRandomTotalRB.setSelected(settings.getMovesetsMod() == Settings.MovesetsMod.COMPLETELY_RANDOM);
        this.pmsRandomTypeRB.setSelected(settings.getMovesetsMod() == Settings.MovesetsMod.RANDOM_PREFER_SAME_TYPE);
        this.pmsUnchangedRB.setSelected(settings.getMovesetsMod() == Settings.MovesetsMod.UNCHANGED);
        this.pmsMetronomeOnlyRB.setSelected(settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY);
        this.pmsGuaranteedMovesCB.setSelected(settings.isStartWithGuaranteedMoves());
        this.pmsGuaranteedMovesSlider.setValue(settings.getGuaranteedMoveCount());
        this.pmsReorderDamagingMovesCB.setSelected(settings.isReorderDamagingMoves());
        this.pmsForceGoodDamagingCB.setSelected(settings.isMovesetsForceGoodDamaging());
        this.pmsForceGoodDamagingSlider.setValue(settings.getMovesetsGoodDamagingPercent());

        this.tpPowerLevelsCB.setSelected(settings.isTrainersUsePokemonOfSimilarStrength());
        this.tpRandomRB.setSelected(settings.getTrainersMod() == Settings.TrainersMod.RANDOM);
        this.tpRivalCarriesStarterCB.setSelected(settings.isRivalCarriesStarterThroughout());
        this.tpTypeThemedRB.setSelected(settings.getTrainersMod() == Settings.TrainersMod.TYPE_THEMED);
        this.tpTypeWeightingCB.setSelected(settings.isTrainersMatchTypingDistribution());
        this.tpUnchangedRB.setSelected(settings.getTrainersMod() == Settings.TrainersMod.UNCHANGED);
        this.tpEnforceDistribution.setSelected(settings.getTrainersMod() == Settings.TrainersMod.DISTRIBUTED);
        this.tpEnforceMainPlaythrough.setSelected(settings.getTrainersMod() == Settings.TrainersMod.MAINPLAYTHROUGH);
        this.tpNoLegendariesCB.setSelected(settings.isTrainersBlockLegendaries());
        this.tpNoEarlyShedinjaCB.setSelected(settings.isTrainersBlockEarlyWonderGuard());
        this.tpForceFullyEvolvedCB.setSelected(settings.isTrainersForceFullyEvolved());
        this.tpForceFullyEvolvedSlider.setValue(settings.getTrainersForceFullyEvolvedLevel());
        this.tpLevelModifierCB.setSelected(settings.isTrainersLevelModified());
        this.tpLevelModifierSlider.setValue(settings.getTrainersLevelModifier());

        this.wpARCatchEmAllRB
                .setSelected(settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.CATCH_EM_ALL);
        this.wpArea11RB.setSelected(settings.getWildPokemonMod() == Settings.WildPokemonMod.AREA_MAPPING);
        this.wpARNoneRB.setSelected(settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.NONE);
        this.wpARTypeThemedRB
                .setSelected(settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.TYPE_THEME_AREAS);
        this.wpGlobalRB.setSelected(settings.getWildPokemonMod() == Settings.WildPokemonMod.GLOBAL_MAPPING);
        this.wpRandomRB.setSelected(settings.getWildPokemonMod() == Settings.WildPokemonMod.RANDOM);
        this.wpUnchangedRB.setSelected(settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED);
        this.wpUseTimeCB.setSelected(settings.isUseTimeBasedEncounters());

        this.wpCatchRateCB.setSelected(settings.isUseMinimumCatchRate());
        this.wpCatchRateSlider.setValue(settings.getMinimumCatchRateLevel());
        this.wpNoLegendariesCB.setSelected(settings.isBlockWildLegendaries());
        this.wpARSimilarStrengthRB
                .setSelected(settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.SIMILAR_STRENGTH);
        this.wpHeldItemsCB.setSelected(settings.isRandomizeWildPokemonHeldItems());
        this.wpHeldItemsBanBadCB.setSelected(settings.isBanBadRandomWildPokemonHeldItems());
        this.wpShakingGrassCB.setSelected(settings.isBalanceShakingGrass());
        this.wpLevelModifierCB.setSelected(settings.isWildLevelsModified());
        this.wpLevelModifierSlider.setValue(settings.getWildLevelModifier());

        this.stpUnchangedRB.setSelected(settings.getStaticPokemonMod() == Settings.StaticPokemonMod.UNCHANGED);
        this.stpRandomL4LRB.setSelected(settings.getStaticPokemonMod() == Settings.StaticPokemonMod.RANDOM_MATCHING);
        this.stpRandomTotalRB
                .setSelected(settings.getStaticPokemonMod() == Settings.StaticPokemonMod.COMPLETELY_RANDOM);
        this.stpRandomPower
                .setSelected(settings.getStaticPokemonMod() == Settings.StaticPokemonMod.SIMILAR_STRENGTH);
        this.stpLimitMusketeers.setSelected(settings.isLimitMusketeers());
        this.stpLimit600.setSelected(settings.isLimit600());
        
        this.thcRandomTotalRB
                .setSelected(settings.getTmsHmsCompatibilityMod() == Settings.TMsHMsCompatibilityMod.COMPLETELY_RANDOM);
        this.thcRandomTypeRB
                .setSelected(settings.getTmsHmsCompatibilityMod() == Settings.TMsHMsCompatibilityMod.RANDOM_PREFER_TYPE);
        this.thcUnchangedRB
                .setSelected(settings.getTmsHmsCompatibilityMod() == Settings.TMsHMsCompatibilityMod.UNCHANGED);
        this.tmmRandomRB.setSelected(settings.getTmsMod() == Settings.TMsMod.RANDOM);
        this.tmmUnchangedRB.setSelected(settings.getTmsMod() == Settings.TMsMod.UNCHANGED);
        this.tmLearningSanityCB.setSelected(settings.isTmLevelUpMoveSanity());
        this.tmKeepFieldMovesCB.setSelected(settings.isKeepFieldMoveTMs());
        this.thcFullRB.setSelected(settings.getTmsHmsCompatibilityMod() == Settings.TMsHMsCompatibilityMod.FULL);
        this.tmFullHMCompatCB.setSelected(settings.isFullHMCompat());
        this.tmForceGoodDamagingCB.setSelected(settings.isTmsForceGoodDamaging());
        this.tmForceGoodDamagingSlider.setValue(settings.getTmsGoodDamagingPercent());

        this.mtcRandomTotalRB
                .setSelected(settings.getMoveTutorsCompatibilityMod() == Settings.MoveTutorsCompatibilityMod.COMPLETELY_RANDOM);
        this.mtcRandomTypeRB
                .setSelected(settings.getMoveTutorsCompatibilityMod() == Settings.MoveTutorsCompatibilityMod.RANDOM_PREFER_TYPE);
        this.mtcUnchangedRB
                .setSelected(settings.getMoveTutorsCompatibilityMod() == Settings.MoveTutorsCompatibilityMod.UNCHANGED);
        this.mtmRandomRB.setSelected(settings.getMoveTutorMovesMod() == Settings.MoveTutorMovesMod.RANDOM);
        this.mtmUnchangedRB.setSelected(settings.getMoveTutorMovesMod() == Settings.MoveTutorMovesMod.UNCHANGED);
        this.mtLearningSanityCB.setSelected(settings.isTutorLevelUpMoveSanity());
        this.mtKeepFieldMovesCB.setSelected(settings.isKeepFieldMoveTutors());
        this.mtcFullRB
                .setSelected(settings.getMoveTutorsCompatibilityMod() == Settings.MoveTutorsCompatibilityMod.FULL);
        this.mtForceGoodDamagingCB.setSelected(settings.isTutorsForceGoodDamaging());
        this.mtForceGoodDamagingSlider.setValue(settings.getTutorsGoodDamagingPercent());

        this.igtBothRB
                .setSelected(settings.getInGameTradesMod() == Settings.InGameTradesMod.RANDOMIZE_GIVEN_AND_REQUESTED);
        this.igtGivenOnlyRB.setSelected(settings.getInGameTradesMod() == Settings.InGameTradesMod.RANDOMIZE_GIVEN);
        this.igtRandomItemCB.setSelected(settings.isRandomizeInGameTradesItems());
        this.igtRandomIVsCB.setSelected(settings.isRandomizeInGameTradesIVs());
        this.igtRandomNicknameCB.setSelected(settings.isRandomizeInGameTradesNicknames());
        this.igtRandomOTCB.setSelected(settings.isRandomizeInGameTradesOTs());
        this.igtUnchangedRB.setSelected(settings.getInGameTradesMod() == Settings.InGameTradesMod.UNCHANGED);

        this.fiRandomRB.setSelected(settings.getFieldItemsMod() == Settings.FieldItemsMod.RANDOM);
        this.fiRandomEvenRB.setSelected(settings.getFieldItemsMod() == Settings.FieldItemsMod.RANDOM_EVEN);
        this.fiShuffleRB.setSelected(settings.getFieldItemsMod() == Settings.FieldItemsMod.SHUFFLE);
        this.fiUnchangedRB.setSelected(settings.getFieldItemsMod() == Settings.FieldItemsMod.UNCHANGED);
        this.fiBanBadCB.setSelected(settings.isBanBadRandomFieldItems());

        this.shRandomRB.setSelected(settings.getShopItemsMod() == Settings.ShopItemsMod.RANDOM);
        this.shShuffleRB.setSelected(settings.getShopItemsMod() == Settings.ShopItemsMod.SHUFFLE);
        this.shUnchangedRB.setSelected(settings.getShopItemsMod() == Settings.ShopItemsMod.UNCHANGED);
        this.shBanBadCB.setSelected(settings.isBanBadRandomShopItems());
        this.shBanRegularShopCB.setSelected(settings.isBanRegularShopItems());
        this.shBanOPShopCB.setSelected(settings.isBanOPShopItems());
        this.shBalancePricesCB.setSelected(settings.isBalanceShopPrices());
        this.shGuaranteeEvolutionItemsCB.setSelected(settings.isGuaranteeEvolutionItems());

        this.currentRestrictions = settings.getCurrentRestrictions();
        if (this.currentRestrictions != null) {
            this.currentRestrictions.limitToGen(this.romHandler.generationOfPokemon());
        }

        int mtsSelected = settings.getCurrentMiscTweaks();
        int mtCount = MiscTweak.allTweaks.size();

        for (int mti = 0; mti < mtCount; mti++) {
            MiscTweak mt = MiscTweak.allTweaks.get(mti);
            JCheckBox mtCB = tweakCheckboxes.get(mti);
            mtCB.setSelected((mtsSelected & mt.getValue()) != 0);
        }

        this.enableOrDisableSubControls();
    }

    private Settings createSettingsFromState(CustomNamesSet customNames) {
        Settings settings = new Settings();
        settings.setRomName(this.romHandler.getROMName());
        settings.setChangeImpossibleEvolutions(goRemoveTradeEvosCheckBox.isSelected());
        settings.setUpdateMoves(goUpdateMovesCheckBox.isSelected());
        settings.setUpdateMovesLegacy(goUpdateMovesLegacyCheckBox.isSelected());
        settings.setRandomizeTrainerNames(tnRandomizeCB.isSelected());
        settings.setRandomizeTrainerClassNames(tcnRandomizeCB.isSelected());

        settings.setBaseStatisticsMod(pbsChangesUnchangedRB.isSelected(), pbsChangesShuffleRB.isSelected(),
                pbsChangesRandomRB.isSelected());
        settings.setBaseStatsFollowEvolutions(pbsFollowEvolutionsCB.isSelected());
        settings.setUpdateBaseStats(pbsUpdateStatsCB.isSelected());
        settings.setStandardizeEXPCurves(pbsStandardEXPCurvesCB.isSelected());
        settings.setExpCurveMod(pbsEXPCurvesLegendaryRB.isSelected(), pbsEXPCurvesStrongLegendaryRB.isSelected(),
                pbsEXPCurvesAllRB.isSelected());

        settings.setAbilitiesMod(paUnchangedRB.isSelected(), paRandomizeRB.isSelected());
        settings.setAllowWonderGuard(paWonderGuardCB.isSelected());
        settings.setAbilitiesFollowEvolutions(paFollowEvolutionsCB.isSelected());
        settings.setBanTrappingAbilities(paBanTrappingCB.isSelected());
        settings.setBanNegativeAbilities(paBanNegativeCB.isSelected());
        settings.setBanBadAbilities(paBanBadCB.isSelected());

        settings.setTypesMod(ptUnchangedRB.isSelected(), ptRandomFollowEvosRB.isSelected(),
                ptRandomTotalRB.isSelected());
        settings.setRaceMode(raceModeCB.isSelected());
        settings.setBlockBrokenMoves(brokenMovesCB.isSelected());
        settings.setLimitPokemon(pokeLimitCB.isSelected());

        settings.setMakeEvolutionsEasier(goCondenseEvosCheckBox.isSelected());

        settings.setStartersMod(spUnchangedRB.isSelected(), spCustomRB.isSelected(), spRandomRB.isSelected(),
                spRandom2EvosRB.isSelected());
        settings.setRandomizeStartersHeldItems(spHeldItemsCB.isSelected());
        settings.setBanBadRandomStarterHeldItems(spHeldItemsBanBadCB.isSelected());

        int[] customStarters = new int[] { spCustomPoke1Chooser.getSelectedIndex() + 1,
                spCustomPoke2Chooser.getSelectedIndex() + 1, spCustomPoke3Chooser.getSelectedIndex() + 1 };
        settings.setCustomStarters(customStarters);

        settings.setEvolutionsMod(peUnchangedRB.isSelected(), peRandomRB.isSelected());
        settings.setEvosSimilarStrength(peSimilarStrengthCB.isSelected());
        settings.setEvosSameTyping(peSameTypeCB.isSelected());
        settings.setEvosMaxThreeStages(peThreeStagesCB.isSelected());
        settings.setEvosForceChange(peForceChangeCB.isSelected());

        settings.setRandomizeMoveAccuracies(mdRandomAccuracyCB.isSelected());
        settings.setRandomizeMoveCategory(mdRandomCategoryCB.isSelected());
        settings.setRandomizeMovePowers(mdRandomPowerCB.isSelected());
        settings.setRandomizeMovePPs(mdRandomPPCB.isSelected());
        settings.setRandomizeMoveTypes(mdRandomTypeCB.isSelected());

        settings.setMovesetsMod(pmsUnchangedRB.isSelected(), pmsRandomTypeRB.isSelected(),
                pmsRandomTotalRB.isSelected(), pmsMetronomeOnlyRB.isSelected());
        settings.setStartWithGuaranteedMoves(pmsGuaranteedMovesCB.isSelected());
        settings.setGuaranteedMoveCount(pmsGuaranteedMovesSlider.getValue());
        settings.setReorderDamagingMoves(pmsReorderDamagingMovesCB.isSelected());

        settings.setMovesetsForceGoodDamaging(pmsForceGoodDamagingCB.isSelected());
        settings.setMovesetsGoodDamagingPercent(pmsForceGoodDamagingSlider.getValue());

        settings.setTrainersMod(tpUnchangedRB.isSelected(), tpRandomRB.isSelected(), tpEnforceDistribution.isSelected(), tpEnforceMainPlaythrough.isSelected(),tpTypeThemedRB.isSelected());
        settings.setTrainersUsePokemonOfSimilarStrength(tpPowerLevelsCB.isSelected());
        settings.setRivalCarriesStarterThroughout(tpRivalCarriesStarterCB.isSelected());
        settings.setTrainersMatchTypingDistribution(tpTypeWeightingCB.isSelected());
        settings.setTrainersBlockLegendaries(tpNoLegendariesCB.isSelected());
        settings.setTrainersBlockEarlyWonderGuard(tpNoEarlyShedinjaCB.isSelected());        
        settings.setTrainersForceFullyEvolved(tpForceFullyEvolvedCB.isSelected());
        settings.setTrainersForceFullyEvolvedLevel(tpForceFullyEvolvedSlider.getValue());
        settings.setTrainersLevelModified(tpLevelModifierCB.isSelected());
        settings.setTrainersLevelModifier(tpLevelModifierSlider.getValue());

        settings.setWildPokemonMod(wpUnchangedRB.isSelected(), wpRandomRB.isSelected(), wpArea11RB.isSelected(),
                wpGlobalRB.isSelected());
        settings.setWildPokemonRestrictionMod(wpARNoneRB.isSelected(), wpARSimilarStrengthRB.isSelected(),
                wpARCatchEmAllRB.isSelected(), wpARTypeThemedRB.isSelected());
        settings.setUseTimeBasedEncounters(wpUseTimeCB.isSelected());
        settings.setUseMinimumCatchRate(wpCatchRateCB.isSelected());
        settings.setMinimumCatchRateLevel(wpCatchRateSlider.getValue());
        settings.setBlockWildLegendaries(wpNoLegendariesCB.isSelected());
        settings.setRandomizeWildPokemonHeldItems(wpHeldItemsCB.isSelected());
        settings.setBanBadRandomWildPokemonHeldItems(wpHeldItemsBanBadCB.isSelected());
        settings.setBalanceShakingGrass(wpShakingGrassCB.isSelected());
        settings.setWildLevelsModified(wpLevelModifierCB.isSelected());
        settings.setWildLevelModifier(wpLevelModifierSlider.getValue());

        settings.setStaticPokemonMod(stpUnchangedRB.isSelected(), stpRandomL4LRB.isSelected(),
                stpRandomTotalRB.isSelected(),stpRandomPower.isSelected());
        
        settings.setTmsMod(tmmUnchangedRB.isSelected(), tmmRandomRB.isSelected());

        settings.setTmsHmsCompatibilityMod(thcUnchangedRB.isSelected(), thcRandomTypeRB.isSelected(),
                thcRandomTotalRB.isSelected(), thcFullRB.isSelected());
        settings.setTmLevelUpMoveSanity(tmLearningSanityCB.isSelected());
        settings.setKeepFieldMoveTMs(tmKeepFieldMovesCB.isSelected());
        settings.setFullHMCompat(tmFullHMCompatCB.isSelected());
        settings.setTmsForceGoodDamaging(tmForceGoodDamagingCB.isSelected());
        settings.setTmsGoodDamagingPercent(tmForceGoodDamagingSlider.getValue());

        settings.setMoveTutorMovesMod(mtmUnchangedRB.isSelected(), mtmRandomRB.isSelected());
        settings.setMoveTutorsCompatibilityMod(mtcUnchangedRB.isSelected(), mtcRandomTypeRB.isSelected(),
                mtcRandomTotalRB.isSelected(), mtcFullRB.isSelected());
        settings.setTutorLevelUpMoveSanity(mtLearningSanityCB.isSelected());
        settings.setKeepFieldMoveTutors(mtKeepFieldMovesCB.isSelected());
        settings.setTutorsForceGoodDamaging(mtForceGoodDamagingCB.isSelected());
        settings.setTutorsGoodDamagingPercent(mtForceGoodDamagingSlider.getValue());

        settings.setInGameTradesMod(igtUnchangedRB.isSelected(), igtGivenOnlyRB.isSelected(), igtBothRB.isSelected());
        settings.setRandomizeInGameTradesItems(igtRandomItemCB.isSelected());
        settings.setRandomizeInGameTradesIVs(igtRandomIVsCB.isSelected());
        settings.setRandomizeInGameTradesNicknames(igtRandomNicknameCB.isSelected());
        settings.setLimitMusketeers(stpLimitMusketeers.isSelected());
        settings.setLimit600(stpLimit600.isSelected());
        settings.setRandomizeInGameTradesOTs(igtRandomOTCB.isSelected());

        settings.setFieldItemsMod(fiUnchangedRB.isSelected(), fiShuffleRB.isSelected(), fiRandomRB.isSelected(), fiRandomEvenRB.isSelected());
        settings.setBanBadRandomFieldItems(fiBanBadCB.isSelected());

        settings.setShopItemsMod(shUnchangedRB.isSelected(), shShuffleRB.isSelected(), shRandomRB.isSelected());

        settings.setBanBadRandomShopItems(shBanBadCB.isSelected());
        settings.setBanRegularShopItems(shBanRegularShopCB.isSelected());
        settings.setBanOPShopItems(shBanOPShopCB.isSelected());
        settings.setBalanceShopPrices(shBalancePricesCB.isSelected());
        settings.setGuaranteeEvolutionItems(shGuaranteeEvolutionItemsCB.isSelected());


        settings.setCurrentRestrictions(currentRestrictions);

        int currentMiscTweaks = 0;
        int mtCount = MiscTweak.allTweaks.size();

        for (int mti = 0; mti < mtCount; mti++) {
            MiscTweak mt = MiscTweak.allTweaks.get(mti);
            JCheckBox mtCB = tweakCheckboxes.get(mti);
            if (mtCB.isSelected()) {
                currentMiscTweaks |= mt.getValue();
            }
        }

        settings.setCurrentMiscTweaks(currentMiscTweaks);

        settings.setCustomNames(customNames);

        return settings;
    }

    private void performRandomization(final String filename, final long seed, CustomNamesSet customNames) {
        final Settings settings = createSettingsFromState(customNames);
        final boolean raceMode = settings.isRaceMode();
        // Setup verbose log
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream log;
        try {
            log = new PrintStream(baos, false, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log = new PrintStream(baos);
        }

        final PrintStream verboseLog = log;

        try {
            final AtomicInteger finishedCV = new AtomicInteger(0);
            opDialog = new OperationDialog(bundle.getString("RandomizerGUI.savingText"), this, true);
            Thread t = new Thread() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            opDialog.setVisible(true);
                        }
                    });
                    boolean succeededSave = false;
                    try {
                        RandomizerGUI.this.romHandler.setLog(verboseLog);
                        finishedCV.set(new Randomizer(settings, RandomizerGUI.this.romHandler, false).randomize(filename,
                                verboseLog, seed));
                        succeededSave = true;
                    } catch (RandomizationException ex) {
                        attemptToLogException(ex, "RandomizerGUI.saveFailedMessage",
                                "RandomizerGUI.saveFailedMessageNoLog", true);
                        if (verboseLog != null) {
                            verboseLog.close();
                        }
                    } catch (Exception ex) {
                        attemptToLogException(ex, "RandomizerGUI.saveFailedIO", "RandomizerGUI.saveFailedIONoLog");
                        if (verboseLog != null) {
                            verboseLog.close();
                        }
                    }
                    if (succeededSave) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                RandomizerGUI.this.opDialog.setVisible(false);
                                // Log?
                                verboseLog.close();
                                byte[] out = baos.toByteArray();

                                if (raceMode) {
                                    JOptionPane.showMessageDialog(RandomizerGUI.this,
                                            String.format(bundle.getString("RandomizerGUI.raceModeCheckValuePopup"),
                                                    finishedCV.get()));
                                } else {
                                    int response = JOptionPane.showConfirmDialog(RandomizerGUI.this,
                                            bundle.getString("RandomizerGUI.saveLogDialog.text"),
                                            bundle.getString("RandomizerGUI.saveLogDialog.title"),
                                            JOptionPane.YES_NO_OPTION);
                                    if (response == JOptionPane.YES_OPTION) {
                                        try {
                                            FileOutputStream fos = new FileOutputStream(filename + ".log");
                                            fos.write(0xEF);
                                            fos.write(0xBB);
                                            fos.write(0xBF);
                                            fos.write(out);
                                            fos.close();
                                        } catch (IOException e) {
                                            JOptionPane.showMessageDialog(RandomizerGUI.this,
                                                    bundle.getString("RandomizerGUI.logSaveFailed"));
                                            return;
                                        }
                                        JOptionPane.showMessageDialog(RandomizerGUI.this,
                                                String.format(bundle.getString("RandomizerGUI.logSaved"), filename));
                                    }
                                }
                                if (presetMode) {
                                    JOptionPane.showMessageDialog(RandomizerGUI.this,
                                            bundle.getString("RandomizerGUI.randomizationDone"));
                                    // Done
                                    RandomizerGUI.this.romHandler = null;
                                    initialFormState();
                                } else {
                                    // Compile a config string
                                    try {
                                        String configString = getCurrentSettings().toString();
                                        // Show the preset maker
                                        new PresetMakeDialog(RandomizerGUI.this, seed, configString);
                                    } catch (IOException ex) {
                                        JOptionPane.showMessageDialog(RandomizerGUI.this,
                                                bundle.getString("RandomizerGUI.cantLoadCustomNames"));
                                    }

                                    // Done
                                    RandomizerGUI.this.romHandler = null;
                                    initialFormState();
                                }
                            }
                        });
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                RandomizerGUI.this.opDialog.setVisible(false);
                                RandomizerGUI.this.romHandler = null;
                                initialFormState();
                            }
                        });
                    }
                }
            };
            t.start();
        } catch (Exception ex) {
            attemptToLogException(ex, "RandomizerGUI.saveFailed", "RandomizerGUI.saveFailedNoLog");
            if (verboseLog != null) {
                verboseLog.close();
            }
        }
    }

    private void presetLoader() {
        PresetLoadDialog pld = new PresetLoadDialog(this);
        if (pld.isCompleted()) {
            // Apply it
            long seed = pld.getSeed();
            String config = pld.getConfigString();
            this.romHandler = pld.getROM();
            this.romLoaded();
            Settings settings;
            try {
                settings = Settings.fromString(config);
                settings.tweakForRom(this.romHandler);
                this.restoreStateFromSettings(settings);
            } catch (UnsupportedEncodingException e) {
                // settings load failed
                e.printStackTrace();
                this.romHandler = null;
                initialFormState();
            }
            romSaveChooser.setSelectedFile(null);
            int returnVal = romSaveChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File fh = romSaveChooser.getSelectedFile();
                // Fix or add extension
                List<String> extensions = new ArrayList<String>(Arrays.asList("sgb", "gbc", "gba", "nds"));
                extensions.remove(this.romHandler.getDefaultExtension());
                fh = FileFunctions.fixFilename(fh, this.romHandler.getDefaultExtension(), extensions);
                boolean allowed = true;
                if (this.romHandler instanceof AbstractDSRomHandler) {
                    String currentFN = this.romHandler.loadedFilename();
                    if (currentFN.equals(fh.getAbsolutePath())) {
                        JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.cantOverwriteDS"));
                        allowed = false;
                    }
                }
                if (allowed) {
                    // Apply the seed we were given
                    RandomSource.seed(seed);
                    presetMode = true;
                    performRandomization(fh.getAbsolutePath(), seed, pld.getCustomNames());
                } else {
                    this.romHandler = null;
                    initialFormState();
                }

            } else {
                this.romHandler = null;
                initialFormState();
            }
        }

    }

    private void attemptToLogException(Exception ex, String baseMessageKey, String noLogMessageKey) {
        attemptToLogException(ex, baseMessageKey, noLogMessageKey, false);
    }

    private void attemptToLogException(Exception ex, String baseMessageKey, String noLogMessageKey, boolean showMessage) {

        // Make sure the operation dialog doesn't show up over the error
        // dialog
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                RandomizerGUI.this.opDialog.setVisible(false);
            }
        });

        Date now = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        try {
            String errlog = "error_" + ft.format(now) + ".txt";
            PrintStream ps = new PrintStream(new FileOutputStream(errlog));
            ps.println("Randomizer Version: " + SysConstants.UPDATE_VERSION);
            PrintStream e1 = System.err;
            System.setErr(ps);
            if (this.romHandler != null) {
                try {
                    ps.println("ROM: " + romHandler.getROMName());
                    ps.println("Code: " + romHandler.getROMCode());
                    ps.println("Reported Support Level: " + romHandler.getSupportLevel());
                    ps.println();
                } catch (Exception ex2) {
                    // Do nothing, just don't fail
                }
            }
            ex.printStackTrace();
            System.setErr(e1);
            ps.close();
            if (showMessage) {
                JOptionPane.showMessageDialog(this,
                        String.format(bundle.getString(baseMessageKey), ex.getMessage(), errlog));
            } else {
                JOptionPane.showMessageDialog(this, String.format(bundle.getString(baseMessageKey), errlog));
            }
        } catch (Exception logex) {
            if (showMessage) {
                JOptionPane.showMessageDialog(this, String.format(bundle.getString(noLogMessageKey), ex.getMessage()));
            } else {
                JOptionPane.showMessageDialog(this, bundle.getString(noLogMessageKey));
            }
        }
    }

    // public response methods

    public void updateFound(int newVersion, String changelog) {
        new UpdateFoundDialog(this, newVersion, changelog);
    }

    public void noUpdateFound() {
        JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.noUpdates"));
    }

    // actions

    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_updateSettingsButtonActionPerformed
        if (autoUpdateEnabled) {
            toggleAutoUpdatesMenuItem.setText(bundle.getString("RandomizerGUI.disableAutoUpdate"));
        } else {
            toggleAutoUpdatesMenuItem.setText(bundle.getString("RandomizerGUI.enableAutoUpdate"));
        }

        if (useScrollPaneMode) {
            toggleScrollPaneMenuItem.setText(bundle.getString("RandomizerGUI.changeToTabbedPane"));
        } else {
            toggleScrollPaneMenuItem.setText(bundle.getString("RandomizerGUI.changeToScrollPane"));
        }
        updateSettingsMenu.show(settingsButton, 0, settingsButton.getHeight());
    }// GEN-LAST:event_updateSettingsButtonActionPerformed

    private void toggleAutoUpdatesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_toggleAutoUpdatesMenuItemActionPerformed
        autoUpdateEnabled = !autoUpdateEnabled;
        if (autoUpdateEnabled) {
            JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.autoUpdateEnabled"));
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.autoUpdateDisabled"));
        }
        attemptWriteConfig();
    }// GEN-LAST:event_toggleAutoUpdatesMenuItemActionPerformed

    private void updateOldSettingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {

        qsUpdateChooser.setSelectedFile(null);
        int returnVal = qsUpdateChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fh = qsUpdateChooser.getSelectedFile();
            try {
                FileInputStream in = new FileInputStream(fh);
                int version = in.read();
                if (version == 0 || version > 172) {
                    JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.oldSettingsFileInvalid"));
                    in.close();
                    return;
                }
                int length = in.read();
                byte[] buffer = FileFunctions.readFullyIntoBuffer(in, length);
                in.close();

                ByteBuffer buf = ByteBuffer.allocate(length + 8);
                buf.putInt(version);
                buf.putInt(length);
                buf.put(buffer);

                FileOutputStream out = new FileOutputStream(fh);
                out.write(buf.array());
                out.close();
                JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.oldSettingsFileUpdated"));
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.invalidSettingsFile"));
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.settingsLoadFailed"));
            }
        }
    }

    private void manualUpdateMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_manualUpdateMenuItemActionPerformed
        //new UpdateCheckThread(this, true).start();
    }// GEN-LAST:event_manualUpdateMenuItemActionPerformed

    private void toggleScrollPaneMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_toggleScrollPaneMenuItemActionPerformed
        int response = JOptionPane.showConfirmDialog(RandomizerGUI.this,
                bundle.getString("RandomizerGUI.displayModeChangeDialog.text"),
                bundle.getString("RandomizerGUI.displayModeChangeDialog.title"), JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            useScrollPaneMode = !useScrollPaneMode;
            JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.displayModeChanged"));
            attemptWriteConfig();
            System.exit(0);
        }
    }// GEN-LAST:event_toggleScrollPaneMenuItemActionPerformed

    private void customNamesEditorMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_customNamesEditorMenuItemActionPerformed
        new CustomNamesEditorDialog(this);
    }// GEN-LAST:event_customNamesEditorMenuItemActionPerformed

    private void loadQSButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_loadQSButtonActionPerformed
        if (this.romHandler == null) {
            return;
        }
        qsOpenChooser.setSelectedFile(null);
        int returnVal = qsOpenChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fh = qsOpenChooser.getSelectedFile();
            try {
                FileInputStream fis = new FileInputStream(fh);
                Settings settings = Settings.read(fis);
                fis.close();

                // load settings
                initialFormState();
                romLoaded();
                Settings.TweakForROMFeedback feedback = settings.tweakForRom(this.romHandler);
                if (feedback.isChangedStarter() && settings.getStartersMod() == Settings.StartersMod.CUSTOM) {
                    JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.starterUnavailable"));
                }
                this.restoreStateFromSettings(settings);

                if (settings.isUpdatedFromOldVersion()) {
                    // show a warning dialog, but load it
                    JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.settingsFileOlder"));
                }

                JOptionPane.showMessageDialog(this,
                        String.format(bundle.getString("RandomizerGUI.settingsLoaded"), fh.getName()));
            } catch (UnsupportedOperationException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.invalidSettingsFile"));
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.settingsLoadFailed"));
            }
        }
    }// GEN-LAST:event_loadQSButtonActionPerformed

    private void saveQSButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveQSButtonActionPerformed
        if (this.romHandler == null) {
            return;
        }
        qsSaveChooser.setSelectedFile(null);
        int returnVal = qsSaveChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fh = qsSaveChooser.getSelectedFile();
            // Fix or add extension
            fh = FileFunctions.fixFilename(fh, "rnqs");
            // Save now?
            try {
                FileOutputStream fos = new FileOutputStream(fh);
                getCurrentSettings().write(fos);
                fos.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, bundle.getString("RandomizerGUI.settingsSaveFailed"));
            }
        }
    }// GEN-LAST:event_saveQSButtonActionPerformed

    private void pokeLimitBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pokeLimitBtnActionPerformed
        GenerationLimitDialog gld = new GenerationLimitDialog(this, this.currentRestrictions,
                this.romHandler.generationOfPokemon());
        if (gld.pressedOK()) {
            this.currentRestrictions = gld.getChoice();
        }
    }// GEN-LAST:event_pokeLimitBtnActionPerformed

    private void goUpdateMovesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_goUpdateMovesCheckBoxActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_goUpdateMovesCheckBoxActionPerformed

    private void pokeLimitCBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pokeLimitCBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_pokeLimitCBActionPerformed

    private void pmsMetronomeOnlyRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pmsMetronomeOnlyRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_pmsMetronomeOnlyRBActionPerformed

    private void igtUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_igtUnchangedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_igtUnchangedRBActionPerformed

    private void igtGivenOnlyRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_igtGivenOnlyRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_igtGivenOnlyRBActionPerformed

    private void igtBothRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_igtBothRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_igtBothRBActionPerformed

    private void wpARNoneRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpARNoneRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_wpARNoneRBActionPerformed

    private void wpARSimilarStrengthRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpARSimilarStrengthRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_wpARSimilarStrengthRBActionPerformed

    private void wpARCatchEmAllRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpARCatchEmAllRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_wpARCatchEmAllRBActionPerformed

    private void wpARTypeThemedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpARTypeThemedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_wpARTypeThemedRBActionPerformed

    private void pmsUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pmsUnchangedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_pmsUnchangedRBActionPerformed

    private void pmsRandomTypeRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pmsRandomTypeRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_pmsRandomTypeRBActionPerformed

    private void pmsRandomTotalRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pmsRandomTotalRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_pmsRandomTotalRBActionPerformed

    private void pmsGuaranteedMovesCBActionPerformed(java.awt.event.ActionEvent evt) {
        this.enableOrDisableSubControls();
    }

    private void mtmUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mtmUnchangedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_mtmUnchangedRBActionPerformed

    private void paUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_paUnchangedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_paUnchangedRBActionPerformed

    private void paRandomizeRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_paRandomizeRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_paRandomizeRBActionPerformed

    private void openROMButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_openROMButtonActionPerformed
        loadROM();
    }// GEN-LAST:event_openROMButtonActionPerformed

    private void saveROMButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveROMButtonActionPerformed
        saveROM();
    }// GEN-LAST:event_saveROMButtonActionPerformed

    private void usePresetsButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_usePresetsButtonActionPerformed
        presetLoader();
    }// GEN-LAST:event_usePresetsButtonActionPerformed

    private void wpUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpUnchangedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_wpUnchangedRBActionPerformed

    private void tpUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tpUnchangedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_tpUnchangedRBActionPerformed

    private void tpRandomRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tpRandomRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_tpRandomRBActionPerformed

    private void tpTypeThemedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tpTypeThemedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_tpTypeThemedRBActionPerformed

    private void tpEnforceDistributionActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tpTypeThemedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_tpTypeThemedRBActionPerformed

    private void tpEnforceMainPlaythroughActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tpTypeThemedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_tpTypeThemedRBActionPerformed

    private void spUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_spUnchangedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_spUnchangedRBActionPerformed

    private void spCustomRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_spCustomRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_spCustomRBActionPerformed

    private void spRandomRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_spRandomRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_spRandomRBActionPerformed

    private void wpRandomRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpRandomRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_wpRandomRBActionPerformed

    private void wpArea11RBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpArea11RBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_wpArea11RBActionPerformed

    private void wpGlobalRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpGlobalRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_wpGlobalRBActionPerformed

    private void tmmUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tmmUnchangedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_tmmUnchangedRBActionPerformed

    private void tmmRandomRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tmmRandomRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_tmmRandomRBActionPerformed

    private void mtmRandomRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mtmRandomRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_mtmRandomRBActionPerformed

    private void thcUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_thcUnchangedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_thcUnchangedRBActionPerformed

    private void thcRandomTypeRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_thcRandomTypeRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_thcRandomTypeRBActionPerformed

    private void thcRandomTotalRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_thcRandomTotalRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_thcRandomTotalRBActionPerformed

    private void mtcUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mtcUnchangedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_mtcUnchangedRBActionPerformed

    private void mtcRandomTypeRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mtcRandomTypeRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_mtcRandomTypeRBActionPerformed

    private void mtcRandomTotalRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mtcRandomTotalRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_mtcRandomTotalRBActionPerformed

    private void thcFullRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_thcFullRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_thcFullRBActionPerformed

    private void mtcFullRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mtcFullRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_mtcFullRBActionPerformed

    private void spHeldItemsCBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_spHeldItemsCBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_spHeldItemsCBActionPerformed

    private void wpHeldItemsCBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpHeldItemsCBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_wpHeldItemsCBActionPerformed

    private void fiUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_fiUnchangedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_fiUnchangedRBActionPerformed

    private void fiShuffleRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_fiShuffleRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_fiShuffleRBActionPerformed

    private void fiRandomRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_fiRandomRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_fiRandomRBActionPerformed
    
    private void fiRandomEvenRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_fiRandomEvenRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_fiRandomEvenRBActionPerformed

    private void shUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {
        this.enableOrDisableSubControls();
    }

    private void shShuffleRBActionPerformed(java.awt.event.ActionEvent evt) {
        this.enableOrDisableSubControls();
    }

    private void shRandomRBActionPerformed(java.awt.event.ActionEvent evt) {
        this.enableOrDisableSubControls();
    }

    private void spRandom2EvosRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_spRandom2EvosRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_spRandom2EvosRBActionPerformed

    private void goCondenseEvosCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_goCondenseEvosCheckBoxActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_goCondenseEvosCheckBoxActionPerformed

    private void websiteLinkLabelMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_websiteLinkLabelMouseClicked
        Desktop desktop = java.awt.Desktop.getDesktop();
        try {
            desktop.browse(new URI(SysConstants.WEBSITE_URL_ZX));
        } catch (IOException e) {
        } catch (URISyntaxException e) {
        }
    }// GEN-LAST:event_websiteLinkLabelMouseClicked

    private void peUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_peUnchangedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_peUnchangedRBActionPerformed

    private void peRandomRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_peRandomRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_peRandomRBActionPerformed

    private void pbsChangesUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pbsChangesUnchangedRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_pbsChangesUnchangedRBActionPerformed

    private void pbsChangesShuffleRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pbsChangesShuffleRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_pbsChangesShuffleRBActionPerformed

    private void pbsChangesRandomRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pbsChangesRandomRBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_pbsChangesRandomRBActionPerformed

    private void pbsStandardEXPCurvesCBActionPerformed(java.awt.event.ActionEvent evt) {
        this.enableOrDisableSubControls();
    }

    private void tpForceFullyEvolvedCBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tpForceFullyEvolvedCBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_tpForceFullyEvolvedCBActionPerformed

    private void tpLevelModifierCBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tpForceFullyEvolvedCBActionPerformed
        this.enableOrDisableSubControls();
    }

    private void wpCatchRateCBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpCatchRateCBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_wpCatchRateCBActionPerformed

    private void wpLevelModifierCBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tpForceFullyEvolvedCBActionPerformed
        this.enableOrDisableSubControls();
    }

    private void pmsForceGoodDamagingCBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pmsForceGoodDamagingCBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_pmsForceGoodDamagingCBActionPerformed

    private void tmForceGoodDamagingCBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tmForceGoodDamagingCBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_tmForceGoodDamagingCBActionPerformed

    private void mtForceGoodDamagingCBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mtForceGoodDamagingCBActionPerformed
        this.enableOrDisableSubControls();
    }// GEN-LAST:event_mtForceGoodDamagingCBActionPerformed

    /* @formatter:off */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pokeStatChangesButtonGroup = new javax.swing.ButtonGroup();
        expCurveButtonGroup = new javax.swing.ButtonGroup();
        pokeTypesButtonGroup = new javax.swing.ButtonGroup();
        pokeMovesetsButtonGroup = new javax.swing.ButtonGroup();
        trainerPokesButtonGroup = new javax.swing.ButtonGroup();
        wildPokesButtonGroup = new javax.swing.ButtonGroup();
        wildPokesARuleButtonGroup = new javax.swing.ButtonGroup();
        starterPokemonButtonGroup = new javax.swing.ButtonGroup();
        romOpenChooser = new javax.swing.JFileChooser();
        romSaveChooser = new JFileChooser() {

            private static final long serialVersionUID = 3244234325234511L;
            public void approveSelection() {
                File fh = getSelectedFile();
                // Fix or add extension
                List<String> extensions = new ArrayList<String>(Arrays.asList("sgb", "gbc", "gba", "nds"));
                extensions.remove(RandomizerGUI.this.romHandler.getDefaultExtension());
                fh = FileFunctions.fixFilename(fh, RandomizerGUI.this.romHandler.getDefaultExtension(), extensions);
                if (fh.exists() && getDialogType() == SAVE_DIALOG) {
                    int result = JOptionPane.showConfirmDialog(this,
                        "The file exists, overwrite?", "Existing file",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                    switch (result) {
                        case JOptionPane.YES_OPTION:
                        super.approveSelection();
                        return;
                        case JOptionPane.CANCEL_OPTION:
                        cancelSelection();
                        return;
                        default:
                        return;
                    }
                }
                super.approveSelection();
            }
        };
        qsOpenChooser = new javax.swing.JFileChooser();
        qsSaveChooser = new javax.swing.JFileChooser();
        qsUpdateChooser = new javax.swing.JFileChooser();
        staticPokemonButtonGroup = new javax.swing.ButtonGroup();
        tmMovesButtonGroup = new javax.swing.ButtonGroup();
        tmHmCompatibilityButtonGroup = new javax.swing.ButtonGroup();
        pokeAbilitiesButtonGroup = new javax.swing.ButtonGroup();
        mtMovesButtonGroup = new javax.swing.ButtonGroup();
        mtCompatibilityButtonGroup = new javax.swing.ButtonGroup();
        ingameTradesButtonGroup = new javax.swing.ButtonGroup();
        fieldItemsButtonGroup = new javax.swing.ButtonGroup();
        shopItemsButtonGroup = new javax.swing.ButtonGroup();
        updateSettingsMenu = new javax.swing.JPopupMenu();
        toggleAutoUpdatesMenuItem = new javax.swing.JMenuItem();
        manualUpdateMenuItem = new javax.swing.JMenuItem();
        toggleScrollPaneMenuItem = new javax.swing.JMenuItem();
        customNamesEditorMenuItem = new javax.swing.JMenuItem();
        updateOldSettingsMenuItem = new javax.swing.JMenuItem();
        pokeEvolutionsButtonGroup = new javax.swing.ButtonGroup();
        generalOptionsPanel = new javax.swing.JPanel();
        pokeLimitCB = new javax.swing.JCheckBox();
        pokeLimitBtn = new javax.swing.JButton();
        raceModeCB = new javax.swing.JCheckBox();
        brokenMovesCB = new javax.swing.JCheckBox();
        romInfoPanel = new javax.swing.JPanel();
        riRomNameLabel = new javax.swing.JLabel();
        riRomCodeLabel = new javax.swing.JLabel();
        riRomSupportLabel = new javax.swing.JLabel();
        openROMButton = new javax.swing.JButton();
        saveROMButton = new javax.swing.JButton();
        usePresetsButton = new javax.swing.JButton();
        loadQSButton = new javax.swing.JButton();
        saveQSButton = new javax.swing.JButton();
        settingsButton = new javax.swing.JButton();
        randomizerOptionsPane = new javax.swing.JTabbedPane();
        pokeTraitsPanel = new javax.swing.JPanel();
        pokemonTypesPanel = new javax.swing.JPanel();
        ptUnchangedRB = new javax.swing.JRadioButton();
        ptRandomFollowEvosRB = new javax.swing.JRadioButton();
        ptRandomTotalRB = new javax.swing.JRadioButton();
        baseStatsPanel = new javax.swing.JPanel();
        pbsChangesUnchangedRB = new javax.swing.JRadioButton();
        pbsChangesShuffleRB = new javax.swing.JRadioButton();
        pbsChangesRandomRB = new javax.swing.JRadioButton();
        pbsFollowEvolutionsCB = new javax.swing.JCheckBox();
        pbsUpdateStatsCB = new javax.swing.JCheckBox();
        pbsStandardEXPCurvesCB = new javax.swing.JCheckBox();
        pbsEXPCurvesLegendaryRB = new javax.swing.JRadioButton();
        pbsEXPCurvesStrongLegendaryRB = new javax.swing.JRadioButton();
        pbsEXPCurvesAllRB = new javax.swing.JRadioButton();
        abilitiesPanel = new javax.swing.JPanel();
        paUnchangedRB = new javax.swing.JRadioButton();
        paRandomizeRB = new javax.swing.JRadioButton();
        paWonderGuardCB = new javax.swing.JCheckBox();
        paFollowEvolutionsCB = new javax.swing.JCheckBox();
        paBansLabel = new javax.swing.JLabel();
        paBanTrappingCB = new javax.swing.JCheckBox();
        paBanNegativeCB = new javax.swing.JCheckBox();
        paBanBadCB = new javax.swing.JCheckBox();
        pokemonEvolutionsPanel = new javax.swing.JPanel();
        peUnchangedRB = new javax.swing.JRadioButton();
        peRandomRB = new javax.swing.JRadioButton();
        peSimilarStrengthCB = new javax.swing.JCheckBox();
        peSameTypeCB = new javax.swing.JCheckBox();
        peThreeStagesCB = new javax.swing.JCheckBox();
        peForceChangeCB = new javax.swing.JCheckBox();
        goRemoveTradeEvosCheckBox = new javax.swing.JCheckBox();
        goCondenseEvosCheckBox = new javax.swing.JCheckBox();
        startersInnerPanel = new javax.swing.JPanel();
        starterPokemonPanel = new javax.swing.JPanel();
        spUnchangedRB = new javax.swing.JRadioButton();
        spCustomRB = new javax.swing.JRadioButton();
        spCustomPoke1Chooser = new javax.swing.JComboBox();
        spCustomPoke2Chooser = new javax.swing.JComboBox();
        spCustomPoke3Chooser = new javax.swing.JComboBox();
        spRandomRB = new javax.swing.JRadioButton();
        spRandom2EvosRB = new javax.swing.JRadioButton();
        spHeldItemsCB = new javax.swing.JCheckBox();
        spHeldItemsBanBadCB = new javax.swing.JCheckBox();
        staticPokemonPanel = new javax.swing.JPanel();
        stpUnchangedRB = new javax.swing.JRadioButton();
        stpRandomL4LRB = new javax.swing.JRadioButton();
        stpRandomTotalRB = new javax.swing.JRadioButton();
        stpRandomPower = new javax.swing.JRadioButton();
        stpLimitMusketeers = new javax.swing.JCheckBox();
        stpLimit600 = new javax.swing.JCheckBox();
        inGameTradesPanel = new javax.swing.JPanel();
        igtUnchangedRB = new javax.swing.JRadioButton();
        igtGivenOnlyRB = new javax.swing.JRadioButton();
        igtBothRB = new javax.swing.JRadioButton();
        igtRandomNicknameCB = new javax.swing.JCheckBox();
        igtRandomOTCB = new javax.swing.JCheckBox();
        igtRandomIVsCB = new javax.swing.JCheckBox();
        igtRandomItemCB = new javax.swing.JCheckBox();
        movesAndSetsPanel = new javax.swing.JPanel();
        pokemonMovesetsPanel = new javax.swing.JPanel();
        pmsUnchangedRB = new javax.swing.JRadioButton();
        pmsRandomTypeRB = new javax.swing.JRadioButton();
        pmsRandomTotalRB = new javax.swing.JRadioButton();
        pmsMetronomeOnlyRB = new javax.swing.JRadioButton();
        pmsGuaranteedMovesCB = new javax.swing.JCheckBox();
        pmsGuaranteedMovesSlider = new javax.swing.JSlider();
        pmsReorderDamagingMovesCB = new javax.swing.JCheckBox();
        pmsForceGoodDamagingCB = new javax.swing.JCheckBox();
        pmsForceGoodDamagingSlider = new javax.swing.JSlider();
        moveDataPanel = new javax.swing.JPanel();
        mdRandomPowerCB = new javax.swing.JCheckBox();
        mdRandomAccuracyCB = new javax.swing.JCheckBox();
        mdRandomPPCB = new javax.swing.JCheckBox();
        mdRandomTypeCB = new javax.swing.JCheckBox();
        mdRandomCategoryCB = new javax.swing.JCheckBox();
        goUpdateMovesCheckBox = new javax.swing.JCheckBox();
        goUpdateMovesLegacyCheckBox = new javax.swing.JCheckBox();
        trainersInnerPanel = new javax.swing.JPanel();
        trainersPokemonPanel = new javax.swing.JPanel();
        tpUnchangedRB = new javax.swing.JRadioButton();
        tpRandomRB = new javax.swing.JRadioButton();
        tpTypeThemedRB = new javax.swing.JRadioButton();
        tpPowerLevelsCB = new javax.swing.JCheckBox();
        tpTypeWeightingCB = new javax.swing.JCheckBox();
        tpRivalCarriesStarterCB = new javax.swing.JCheckBox();
        tpNoLegendariesCB = new javax.swing.JCheckBox();
        tnRandomizeCB = new javax.swing.JCheckBox();
        tcnRandomizeCB = new javax.swing.JCheckBox();
        tpNoEarlyShedinjaCB = new javax.swing.JCheckBox();
        tpEnforceDistribution = new javax.swing.JRadioButton();
        tpEnforceMainPlaythrough = new javax.swing.JRadioButton();
        tpForceFullyEvolvedCB = new javax.swing.JCheckBox();
        tpForceFullyEvolvedSlider = new javax.swing.JSlider();
        tpLevelModifierCB = new javax.swing.JCheckBox();
        tpLevelModifierSlider = new javax.swing.JSlider();
        wildsInnerPanel = new javax.swing.JPanel();
        wildPokemonPanel = new javax.swing.JPanel();
        wpUnchangedRB = new javax.swing.JRadioButton();
        wpRandomRB = new javax.swing.JRadioButton();
        wpArea11RB = new javax.swing.JRadioButton();
        wpGlobalRB = new javax.swing.JRadioButton();
        wildPokemonARulePanel = new javax.swing.JPanel();
        wpARNoneRB = new javax.swing.JRadioButton();
        wpARCatchEmAllRB = new javax.swing.JRadioButton();
        wpARTypeThemedRB = new javax.swing.JRadioButton();
        wpARSimilarStrengthRB = new javax.swing.JRadioButton();
        wpUseTimeCB = new javax.swing.JCheckBox();
        wpNoLegendariesCB = new javax.swing.JCheckBox();
        wpCatchRateCB = new javax.swing.JCheckBox();
        wpHeldItemsCB = new javax.swing.JCheckBox();
        wpHeldItemsBanBadCB = new javax.swing.JCheckBox();
        wpShakingGrassCB = new javax.swing.JCheckBox();
        wpCatchRateSlider = new javax.swing.JSlider();
        wpLevelModifierCB = new javax.swing.JCheckBox();
        wpLevelModifierSlider = new javax.swing.JSlider();
        tmHmTutorPanel = new javax.swing.JPanel();
        tmhmsPanel = new javax.swing.JPanel();
        tmMovesPanel = new javax.swing.JPanel();
        tmmUnchangedRB = new javax.swing.JRadioButton();
        tmmRandomRB = new javax.swing.JRadioButton();
        tmLearningSanityCB = new javax.swing.JCheckBox();
        tmKeepFieldMovesCB = new javax.swing.JCheckBox();
        tmFullHMCompatCB = new javax.swing.JCheckBox();
        tmForceGoodDamagingCB = new javax.swing.JCheckBox();
        tmForceGoodDamagingSlider = new javax.swing.JSlider();
        tmHmCompatPanel = new javax.swing.JPanel();
        thcUnchangedRB = new javax.swing.JRadioButton();
        thcRandomTypeRB = new javax.swing.JRadioButton();
        thcRandomTotalRB = new javax.swing.JRadioButton();
        thcFullRB = new javax.swing.JRadioButton();
        moveTutorsPanel = new javax.swing.JPanel();
        mtMovesPanel = new javax.swing.JPanel();
        mtmUnchangedRB = new javax.swing.JRadioButton();
        mtmRandomRB = new javax.swing.JRadioButton();
        mtLearningSanityCB = new javax.swing.JCheckBox();
        mtKeepFieldMovesCB = new javax.swing.JCheckBox();
        mtForceGoodDamagingCB = new javax.swing.JCheckBox();
        mtForceGoodDamagingSlider = new javax.swing.JSlider();
        mtCompatPanel = new javax.swing.JPanel();
        mtcUnchangedRB = new javax.swing.JRadioButton();
        mtcRandomTypeRB = new javax.swing.JRadioButton();
        mtcRandomTotalRB = new javax.swing.JRadioButton();
        mtcFullRB = new javax.swing.JRadioButton();
        mtNoExistLabel = new javax.swing.JLabel();
        fieldItemsInnerPanel = new javax.swing.JPanel();
        fieldItemsPanel = new javax.swing.JPanel();
        fiUnchangedRB = new javax.swing.JRadioButton();
        fiShuffleRB = new javax.swing.JRadioButton();
        fiRandomRB = new javax.swing.JRadioButton();
        fiRandomEvenRB = new javax.swing.JRadioButton();
        fiBanBadCB = new javax.swing.JCheckBox();
        shopItemsPanel = new javax.swing.JPanel();
        shUnchangedRB = new javax.swing.JRadioButton();
        shShuffleRB = new javax.swing.JRadioButton();
        shRandomRB = new javax.swing.JRadioButton();
        shBanBadCB = new javax.swing.JCheckBox();
        shBanRegularShopCB = new javax.swing.JCheckBox();
        shBanOPShopCB = new javax.swing.JCheckBox();
        shBalancePricesCB = new javax.swing.JCheckBox();
        shGuaranteeEvolutionItemsCB = new javax.swing.JCheckBox();
        miscTweaksInnerPanel = new javax.swing.JPanel();
        miscTweaksPanel = new javax.swing.JPanel();
        mtNoneAvailableLabel = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        websiteLinkLabel = new javax.swing.JLabel();
        gameMascotLabel = new javax.swing.JLabel();

        romOpenChooser.setFileFilter(new ROMFilter());

        romSaveChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        romSaveChooser.setFileFilter(new ROMFilter());

        qsOpenChooser.setFileFilter(new QSFileFilter());

        qsSaveChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        qsSaveChooser.setFileFilter(new QSFileFilter());

        qsUpdateChooser.setFileFilter(new QSFileFilter());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dabomstew/pkrandom/gui/Bundle"); // NOI18N
        toggleAutoUpdatesMenuItem.setText(bundle.getString("RandomizerGUI.toggleAutoUpdatesMenuItem.text")); // NOI18N
        toggleAutoUpdatesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleAutoUpdatesMenuItemActionPerformed(evt);
            }
        });
        updateSettingsMenu.add(toggleAutoUpdatesMenuItem);

        manualUpdateMenuItem.setText(bundle.getString("RandomizerGUI.manualUpdateMenuItem.text")); // NOI18N
        manualUpdateMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualUpdateMenuItemActionPerformed(evt);
            }
        });
        updateSettingsMenu.add(manualUpdateMenuItem);

        toggleScrollPaneMenuItem.setText(bundle.getString("RandomizerGUI.toggleScrollPaneMenuItem.text")); // NOI18N
        toggleScrollPaneMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleScrollPaneMenuItemActionPerformed(evt);
            }
        });
        updateSettingsMenu.add(toggleScrollPaneMenuItem);

        customNamesEditorMenuItem.setText(bundle.getString("RandomizerGUI.customNamesEditorMenuItem.text")); // NOI18N
        customNamesEditorMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customNamesEditorMenuItemActionPerformed(evt);
            }
        });
        updateSettingsMenu.add(customNamesEditorMenuItem);

        updateOldSettingsMenuItem.setText(bundle.getString("RandomizerGUI.updateOldSettingsMenuItem.text"));
        updateOldSettingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateOldSettingsMenuItemActionPerformed(evt);
            }
        });
        updateSettingsMenu.add(updateOldSettingsMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(bundle.getString("RandomizerGUI.title")); // NOI18N

        generalOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.generalOptionsPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        pokeLimitCB.setText(bundle.getString("RandomizerGUI.pokeLimitCB.text")); // NOI18N
        pokeLimitCB.setToolTipText(bundle.getString("RandomizerGUI.pokeLimitCB.toolTipText")); // NOI18N
        pokeLimitCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pokeLimitCBActionPerformed(evt);
            }
        });

        pokeLimitBtn.setText(bundle.getString("RandomizerGUI.pokeLimitBtn.text")); // NOI18N
        pokeLimitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pokeLimitBtnActionPerformed(evt);
            }
        });

        raceModeCB.setText(bundle.getString("RandomizerGUI.raceModeCB.text")); // NOI18N
        raceModeCB.setToolTipText(bundle.getString("RandomizerGUI.raceModeCB.toolTipText")); // NOI18N

        brokenMovesCB.setText(bundle.getString("RandomizerGUI.brokenMovesCB.text")); // NOI18N
        brokenMovesCB.setToolTipText(bundle.getString("RandomizerGUI.brokenMovesCB.toolTipText")); // NOI18N

        javax.swing.GroupLayout generalOptionsPanelLayout = new javax.swing.GroupLayout(generalOptionsPanel);
        generalOptionsPanel.setLayout(generalOptionsPanelLayout);
        generalOptionsPanelLayout.setHorizontalGroup(
            generalOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(generalOptionsPanelLayout.createSequentialGroup()
                        .addComponent(pokeLimitCB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pokeLimitBtn))
                    .addComponent(raceModeCB)
                    .addComponent(brokenMovesCB))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        generalOptionsPanelLayout.setVerticalGroup(
            generalOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pokeLimitBtn)
                    .addComponent(pokeLimitCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(raceModeCB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(brokenMovesCB)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        romInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.romInfoPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        riRomNameLabel.setText(bundle.getString("RandomizerGUI.riRomNameLabel.text")); // NOI18N

        riRomCodeLabel.setText(bundle.getString("RandomizerGUI.riRomCodeLabel.text")); // NOI18N

        riRomSupportLabel.setText(bundle.getString("RandomizerGUI.riRomSupportLabel.text")); // NOI18N

        javax.swing.GroupLayout romInfoPanelLayout = new javax.swing.GroupLayout(romInfoPanel);
        romInfoPanel.setLayout(romInfoPanelLayout);
        romInfoPanelLayout.setHorizontalGroup(
            romInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(romInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(romInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(riRomNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addComponent(riRomCodeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(riRomSupportLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        romInfoPanelLayout.setVerticalGroup(
            romInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(romInfoPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(riRomNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(riRomCodeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(riRomSupportLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        openROMButton.setText(bundle.getString("RandomizerGUI.openROMButton.text")); // NOI18N
        openROMButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openROMButtonActionPerformed(evt);
            }
        });

        saveROMButton.setText(bundle.getString("RandomizerGUI.saveROMButton.text")); // NOI18N
        saveROMButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveROMButtonActionPerformed(evt);
            }
        });

        usePresetsButton.setText(bundle.getString("RandomizerGUI.usePresetsButton.text")); // NOI18N
        usePresetsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usePresetsButtonActionPerformed(evt);
            }
        });

        loadQSButton.setText(bundle.getString("RandomizerGUI.loadQSButton.text")); // NOI18N
        loadQSButton.setToolTipText(bundle.getString("RandomizerGUI.loadQSButton.toolTipText")); // NOI18N
        loadQSButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadQSButtonActionPerformed(evt);
            }
        });

        saveQSButton.setText(bundle.getString("RandomizerGUI.saveQSButton.text")); // NOI18N
        saveQSButton.setToolTipText(bundle.getString("RandomizerGUI.saveQSButton.toolTipText")); // NOI18N
        saveQSButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveQSButtonActionPerformed(evt);
            }
        });

        settingsButton.setText(bundle.getString("RandomizerGUI.settingsButton.text")); // NOI18N
        settingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsButtonActionPerformed(evt);
            }
        });

        pokemonTypesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.pokemonTypesPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        pokeTypesButtonGroup.add(ptUnchangedRB);
        ptUnchangedRB.setSelected(true);
        ptUnchangedRB.setText(bundle.getString("RandomizerGUI.ptUnchangedRB.text")); // NOI18N
        ptUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.ptUnchangedRB.toolTipText")); // NOI18N

        pokeTypesButtonGroup.add(ptRandomFollowEvosRB);
        ptRandomFollowEvosRB.setText(bundle.getString("RandomizerGUI.ptRandomFollowEvosRB.text")); // NOI18N
        ptRandomFollowEvosRB.setToolTipText(bundle.getString("RandomizerGUI.ptRandomFollowEvosRB.toolTipText")); // NOI18N

        pokeTypesButtonGroup.add(ptRandomTotalRB);
        ptRandomTotalRB.setText(bundle.getString("RandomizerGUI.ptRandomTotalRB.text")); // NOI18N
        ptRandomTotalRB.setToolTipText(bundle.getString("RandomizerGUI.ptRandomTotalRB.toolTipText")); // NOI18N

        javax.swing.GroupLayout pokemonTypesPanelLayout = new javax.swing.GroupLayout(pokemonTypesPanel);
        pokemonTypesPanel.setLayout(pokemonTypesPanelLayout);
        pokemonTypesPanelLayout.setHorizontalGroup(
            pokemonTypesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pokemonTypesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pokemonTypesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ptUnchangedRB)
                    .addComponent(ptRandomFollowEvosRB)
                    .addComponent(ptRandomTotalRB))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pokemonTypesPanelLayout.setVerticalGroup(
            pokemonTypesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pokemonTypesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ptUnchangedRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ptRandomFollowEvosRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ptRandomTotalRB)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        baseStatsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.baseStatsPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        pokeStatChangesButtonGroup.add(pbsChangesUnchangedRB);
        pbsChangesUnchangedRB.setSelected(true);
        pbsChangesUnchangedRB.setText(bundle.getString("RandomizerGUI.pbsChangesUnchangedRB.text")); // NOI18N
        pbsChangesUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.pbsChangesUnchangedRB.toolTipText")); // NOI18N
        pbsChangesUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbsChangesUnchangedRBActionPerformed(evt);
            }
        });

        pokeStatChangesButtonGroup.add(pbsChangesShuffleRB);
        pbsChangesShuffleRB.setText(bundle.getString("RandomizerGUI.pbsChangesShuffleRB.text")); // NOI18N
        pbsChangesShuffleRB.setToolTipText(bundle.getString("RandomizerGUI.pbsChangesShuffleRB.toolTipText")); // NOI18N
        pbsChangesShuffleRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbsChangesShuffleRBActionPerformed(evt);
            }
        });

        pokeStatChangesButtonGroup.add(pbsChangesRandomRB);
        pbsChangesRandomRB.setText(bundle.getString("RandomizerGUI.pbsChangesRandomRB.text")); // NOI18N
        pbsChangesRandomRB.setToolTipText(bundle.getString("RandomizerGUI.pbsChangesRandomRB.toolTipText")); // NOI18N
        pbsChangesRandomRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbsChangesRandomRBActionPerformed(evt);
            }
        });

        pbsFollowEvolutionsCB.setText(bundle.getString("RandomizerGUI.pbsFollowEvolutionsCB.text")); // NOI18N
        pbsFollowEvolutionsCB.setToolTipText(bundle.getString("RandomizerGUI.pbsFollowEvolutionsCB.toolTipText")); // NOI18N

        pbsUpdateStatsCB.setText(bundle.getString("RandomizerGUI.pbsUpdateStatsCB.text")); // NOI18N
        pbsUpdateStatsCB.setToolTipText(bundle.getString("RandomizerGUI.pbsUpdateStatsCB.toolTipText")); // NOI18N

        pbsStandardEXPCurvesCB.setText(bundle.getString("RandomizerGUI.pbsStandardEXPCurvesCB.text")); // NOI18N
        pbsStandardEXPCurvesCB.setToolTipText(bundle.getString("RandomizerGUI.pbsStandardEXPCurvesCB.toolTipText")); // NOI18N
        pbsStandardEXPCurvesCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbsStandardEXPCurvesCBActionPerformed(evt);
            }
        });

        expCurveButtonGroup.add(pbsEXPCurvesLegendaryRB);
        pbsEXPCurvesLegendaryRB.setSelected(true);
        pbsEXPCurvesLegendaryRB.setText(bundle.getString("RandomizerGUI.pbsEXPCurvesLegendaryRB.text")); // NOI18N
        pbsEXPCurvesLegendaryRB.setToolTipText(bundle.getString("RandomizerGUI.pbsEXPCurvesLegendaryRB.toolTipText")); // NOI18N

        expCurveButtonGroup.add(pbsEXPCurvesStrongLegendaryRB);
        pbsEXPCurvesStrongLegendaryRB.setText(bundle.getString("RandomizerGUI.pbsEXPCurvesStrongLegendaryRB.text")); // NOI18N
        pbsEXPCurvesStrongLegendaryRB.setToolTipText(bundle.getString("RandomizerGUI.pbsEXPCurvesStrongLegendaryRB.toolTipText")); // NOI18N

        expCurveButtonGroup.add(pbsEXPCurvesAllRB);
        pbsEXPCurvesAllRB.setText(bundle.getString("RandomizerGUI.pbsEXPCurvesAllRB.text")); // NOI18N
        pbsEXPCurvesAllRB.setToolTipText(bundle.getString("RandomizerGUI.pbsEXPCurvesAllRB.toolTipText")); // NOI18N

        javax.swing.GroupLayout baseStatsPanelLayout = new javax.swing.GroupLayout(baseStatsPanel);
        baseStatsPanel.setLayout(baseStatsPanelLayout);
        baseStatsPanelLayout.setHorizontalGroup(
            baseStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(baseStatsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(baseStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pbsChangesShuffleRB)
                    .addComponent(pbsChangesUnchangedRB)
                    .addComponent(pbsChangesRandomRB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addGroup(baseStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pbsStandardEXPCurvesCB)
                    .addComponent(pbsFollowEvolutionsCB)
                    .addComponent(pbsUpdateStatsCB))
                .addGroup(baseStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pbsEXPCurvesLegendaryRB)
                    .addComponent(pbsEXPCurvesStrongLegendaryRB)
                    .addComponent(pbsEXPCurvesAllRB))
                .addGap(38, 38, 38))
        );
        baseStatsPanelLayout.setVerticalGroup(
            baseStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(baseStatsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(baseStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pbsChangesUnchangedRB)
                    .addComponent(pbsStandardEXPCurvesCB)
                    .addComponent(pbsEXPCurvesLegendaryRB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(baseStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pbsChangesShuffleRB)
                    .addComponent(pbsFollowEvolutionsCB)
                    .addComponent(pbsEXPCurvesStrongLegendaryRB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(baseStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pbsChangesRandomRB)
                    .addComponent(pbsUpdateStatsCB)
                    .addComponent(pbsEXPCurvesAllRB))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        abilitiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.abilitiesPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        pokeAbilitiesButtonGroup.add(paUnchangedRB);
        paUnchangedRB.setSelected(true);
        paUnchangedRB.setText(bundle.getString("RandomizerGUI.paUnchangedRB.text")); // NOI18N
        paUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.paUnchangedRB.toolTipText")); // NOI18N
        paUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paUnchangedRBActionPerformed(evt);
            }
        });

        pokeAbilitiesButtonGroup.add(paRandomizeRB);
        paRandomizeRB.setText(bundle.getString("RandomizerGUI.paRandomizeRB.text")); // NOI18N
        paRandomizeRB.setToolTipText(bundle.getString("RandomizerGUI.paRandomizeRB.toolTipText")); // NOI18N
        paRandomizeRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paRandomizeRBActionPerformed(evt);
            }
        });

        paWonderGuardCB.setText(bundle.getString("RandomizerGUI.paWonderGuardCB.text")); // NOI18N
        paWonderGuardCB.setToolTipText(bundle.getString("RandomizerGUI.paWonderGuardCB.toolTipText")); // NOI18N

        paFollowEvolutionsCB.setText(bundle.getString("RandomizerGUI.paFollowEvolutionsCB.text")); // NOI18N
        paFollowEvolutionsCB.setToolTipText(bundle.getString("RandomizerGUI.paFollowEvolutionsCB.toolTipText")); // NOI18N

        paBansLabel.setText(bundle.getString("RandomizerGUI.paBansLabel.text")); // NOI18N

        paBanTrappingCB.setText(bundle.getString("RandomizerGUI.paBanTrappingCB.text")); // NOI18N
        paBanTrappingCB.setToolTipText(bundle.getString("RandomizerGUI.paBanTrappingCB.toolTipText")); // NOI18N

        paBanNegativeCB.setText(bundle.getString("RandomizerGUI.paBanNegativeCB.text")); // NOI18N
        paBanNegativeCB.setToolTipText(bundle.getString("RandomizerGUI.paBanNegativeCB.toolTipText")); // NOI18N

        paBanBadCB.setText(bundle.getString("RandomizerGUI.paBanBadCB.text"));
        paBanBadCB.setToolTipText(bundle.getString("RandomizerGUI.paBanBadCB.toolTipText"));

        javax.swing.GroupLayout abilitiesPanelLayout = new javax.swing.GroupLayout(abilitiesPanel);
        abilitiesPanel.setLayout(abilitiesPanelLayout);
        abilitiesPanelLayout.setHorizontalGroup(
            abilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(abilitiesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(abilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(abilitiesPanelLayout.createSequentialGroup()
                        .addGroup(abilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(paUnchangedRB)
                            .addComponent(paRandomizeRB))
                        .addGap(32, 32, 32))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, abilitiesPanelLayout.createSequentialGroup()
                        .addComponent(paBansLabel)
                        .addGap(18, 18, 18)))
                .addGroup(abilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(paWonderGuardCB)
                    .addGroup(abilitiesPanelLayout.createSequentialGroup()
                        .addGroup(abilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(paFollowEvolutionsCB)
                            .addComponent(paBanTrappingCB))
                        .addGap(18, 18, 18)
                        .addComponent(paBanNegativeCB)
                        .addGap(18, 18, 18)
                        .addComponent(paBanBadCB)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        abilitiesPanelLayout.setVerticalGroup(
            abilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(abilitiesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(abilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(paUnchangedRB)
                    .addComponent(paWonderGuardCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(abilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(paRandomizeRB)
                    .addComponent(paFollowEvolutionsCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(abilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(paBansLabel)
                    .addComponent(paBanTrappingCB)
                    .addComponent(paBanNegativeCB)
                    .addComponent(paBanBadCB))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pokemonEvolutionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.pokemonEvolutionsPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        pokeEvolutionsButtonGroup.add(peUnchangedRB);
        peUnchangedRB.setSelected(true);
        peUnchangedRB.setText(bundle.getString("RandomizerGUI.peUnchangedRB.text")); // NOI18N
        peUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.peUnchangedRB.toolTipText")); // NOI18N
        peUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                peUnchangedRBActionPerformed(evt);
            }
        });

        pokeEvolutionsButtonGroup.add(peRandomRB);
        peRandomRB.setText(bundle.getString("RandomizerGUI.peRandomRB.text")); // NOI18N
        peRandomRB.setToolTipText(bundle.getString("RandomizerGUI.peRandomRB.toolTipText")); // NOI18N
        peRandomRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                peRandomRBActionPerformed(evt);
            }
        });

        peSimilarStrengthCB.setText(bundle.getString("RandomizerGUI.peSimilarStrengthCB.text")); // NOI18N
        peSimilarStrengthCB.setToolTipText(bundle.getString("RandomizerGUI.peSimilarStrengthCB.toolTipText")); // NOI18N

        peSameTypeCB.setText(bundle.getString("RandomizerGUI.peSameTypeCB.text")); // NOI18N
        peSameTypeCB.setToolTipText(bundle.getString("RandomizerGUI.peSameTypeCB.toolTipText")); // NOI18N

        peThreeStagesCB.setText(bundle.getString("RandomizerGUI.peThreeStagesCB.text")); // NOI18N
        peThreeStagesCB.setToolTipText(bundle.getString("RandomizerGUI.peThreeStagesCB.toolTipText")); // NOI18N

        peForceChangeCB.setText(bundle.getString("RandomizerGUI.peForceChangeCB.text")); // NOI18N
        peForceChangeCB.setToolTipText(bundle.getString("RandomizerGUI.peForceChangeCB.toolTipText")); // NOI18N

        goRemoveTradeEvosCheckBox.setText(bundle.getString("RandomizerGUI.goRemoveTradeEvosCheckBox.text")); // NOI18N
        goRemoveTradeEvosCheckBox.setToolTipText(bundle.getString("RandomizerGUI.goRemoveTradeEvosCheckBox.toolTipText")); // NOI18N

        goCondenseEvosCheckBox.setText(bundle.getString("RandomizerGUI.goCondenseEvosCheckBox.text")); // NOI18N
        goCondenseEvosCheckBox.setToolTipText(bundle.getString("RandomizerGUI.goCondenseEvosCheckBox.toolTipText")); // NOI18N
        goCondenseEvosCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goCondenseEvosCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pokemonEvolutionsPanelLayout = new javax.swing.GroupLayout(pokemonEvolutionsPanel);
        pokemonEvolutionsPanel.setLayout(pokemonEvolutionsPanelLayout);
        pokemonEvolutionsPanelLayout.setHorizontalGroup(
            pokemonEvolutionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pokemonEvolutionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pokemonEvolutionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(peUnchangedRB)
                    .addComponent(peRandomRB))
                .addGap(83, 83, 83)
                .addGroup(pokemonEvolutionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(peForceChangeCB)
                    .addComponent(peThreeStagesCB)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pokemonEvolutionsPanelLayout.createSequentialGroup()
                        .addGroup(pokemonEvolutionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(peSimilarStrengthCB)
                            .addComponent(peSameTypeCB))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pokemonEvolutionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(goCondenseEvosCheckBox)
                            .addComponent(goRemoveTradeEvosCheckBox))))
                .addContainerGap())
        );
        pokemonEvolutionsPanelLayout.setVerticalGroup(
            pokemonEvolutionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pokemonEvolutionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pokemonEvolutionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(peUnchangedRB)
                    .addComponent(peSimilarStrengthCB)
                    .addComponent(goRemoveTradeEvosCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pokemonEvolutionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(peRandomRB)
                    .addComponent(peSameTypeCB)
                    .addComponent(goCondenseEvosCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(peThreeStagesCB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(peForceChangeCB)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pokeTraitsPanelLayout = new javax.swing.GroupLayout(pokeTraitsPanel);
        pokeTraitsPanel.setLayout(pokeTraitsPanelLayout);
        pokeTraitsPanelLayout.setHorizontalGroup(
            pokeTraitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pokeTraitsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pokeTraitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(baseStatsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pokeTraitsPanelLayout.createSequentialGroup()
                        .addComponent(pokemonTypesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(abilitiesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pokemonEvolutionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pokeTraitsPanelLayout.setVerticalGroup(
            pokeTraitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pokeTraitsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(baseStatsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pokeTraitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(pokemonTypesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(abilitiesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pokemonEvolutionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        randomizerOptionsPane.addTab(bundle.getString("RandomizerGUI.pokeTraitsPanel.TabConstraints.tabTitle"), pokeTraitsPanel); // NOI18N

        starterPokemonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.starterPokemonPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        starterPokemonButtonGroup.add(spUnchangedRB);
        spUnchangedRB.setSelected(true);
        spUnchangedRB.setText(bundle.getString("RandomizerGUI.spUnchangedRB.text")); // NOI18N
        spUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.spUnchangedRB.toolTipText")); // NOI18N
        spUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spUnchangedRBActionPerformed(evt);
            }
        });

        starterPokemonButtonGroup.add(spCustomRB);
        spCustomRB.setText(bundle.getString("RandomizerGUI.spCustomRB.text")); // NOI18N
        spCustomRB.setToolTipText(bundle.getString("RandomizerGUI.spCustomRB.toolTipText")); // NOI18N
        spCustomRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spCustomRBActionPerformed(evt);
            }
        });

        spCustomPoke1Chooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        spCustomPoke1Chooser.setEnabled(false);

        spCustomPoke2Chooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        spCustomPoke2Chooser.setEnabled(false);

        spCustomPoke3Chooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        spCustomPoke3Chooser.setEnabled(false);

        starterPokemonButtonGroup.add(spRandomRB);
        spRandomRB.setText(bundle.getString("RandomizerGUI.spRandomRB.text")); // NOI18N
        spRandomRB.setToolTipText(bundle.getString("RandomizerGUI.spRandomRB.toolTipText")); // NOI18N
        spRandomRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spRandomRBActionPerformed(evt);
            }
        });

        starterPokemonButtonGroup.add(spRandom2EvosRB);
        spRandom2EvosRB.setText(bundle.getString("RandomizerGUI.spRandom2EvosRB.text")); // NOI18N
        spRandom2EvosRB.setToolTipText(bundle.getString("RandomizerGUI.spRandom2EvosRB.toolTipText")); // NOI18N
        spRandom2EvosRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spRandom2EvosRBActionPerformed(evt);
            }
        });

        spHeldItemsCB.setText(bundle.getString("RandomizerGUI.spHeldItemsCB.text")); // NOI18N
        spHeldItemsCB.setToolTipText(bundle.getString("RandomizerGUI.spHeldItemsCB.toolTipText")); // NOI18N
        spHeldItemsCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spHeldItemsCBActionPerformed(evt);
            }
        });

        spHeldItemsBanBadCB.setText(bundle.getString("RandomizerGUI.spHeldItemsBanBadCB.text")); // NOI18N
        spHeldItemsBanBadCB.setToolTipText(bundle.getString("RandomizerGUI.spHeldItemsBanBadCB.toolTipText")); // NOI18N

        javax.swing.GroupLayout starterPokemonPanelLayout = new javax.swing.GroupLayout(starterPokemonPanel);
        starterPokemonPanel.setLayout(starterPokemonPanelLayout);
        starterPokemonPanelLayout.setHorizontalGroup(
            starterPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(starterPokemonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(starterPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spRandom2EvosRB)
                    .addGroup(starterPokemonPanelLayout.createSequentialGroup()
                        .addGroup(starterPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(starterPokemonPanelLayout.createSequentialGroup()
                                .addComponent(spCustomRB)
                                .addGap(18, 18, 18)
                                .addComponent(spCustomPoke1Chooser, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spCustomPoke2Chooser, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spCustomPoke3Chooser, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(spRandomRB)
                            .addComponent(spUnchangedRB))
                        .addGap(18, 18, 18)
                        .addGroup(starterPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spHeldItemsBanBadCB)
                            .addComponent(spHeldItemsCB))))
                .addContainerGap(168, Short.MAX_VALUE))
        );
        starterPokemonPanelLayout.setVerticalGroup(
            starterPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(starterPokemonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spUnchangedRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(starterPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spCustomRB)
                    .addComponent(spCustomPoke1Chooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spCustomPoke2Chooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spCustomPoke3Chooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spHeldItemsCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(starterPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spRandomRB)
                    .addComponent(spHeldItemsBanBadCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spRandom2EvosRB)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        staticPokemonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.staticPokemonPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        staticPokemonButtonGroup.add(stpUnchangedRB);
        stpUnchangedRB.setSelected(true);
        stpUnchangedRB.setText(bundle.getString("RandomizerGUI.stpUnchangedRB.text")); // NOI18N
        stpUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.stpUnchangedRB.toolTipText")); // NOI18N

        staticPokemonButtonGroup.add(stpRandomL4LRB);
        stpRandomL4LRB.setText(bundle.getString("RandomizerGUI.stpRandomL4LRB.text")); // NOI18N
        stpRandomL4LRB.setToolTipText(bundle.getString("RandomizerGUI.stpRandomL4LRB.toolTipText")); // NOI18N

        staticPokemonButtonGroup.add(stpRandomTotalRB);
        stpRandomTotalRB.setText(bundle.getString("RandomizerGUI.stpRandomTotalRB.text")); // NOI18N
        stpRandomTotalRB.setToolTipText(bundle.getString("RandomizerGUI.stpRandomTotalRB.toolTipText")); // NOI18N
        
        staticPokemonButtonGroup.add(stpRandomPower);
        stpRandomPower.setText(bundle.getString("RandomizerGUI.stpRandomPower.text")); // NOI18N
        stpRandomPower.setToolTipText(bundle.getString("RandomizerGUI.stpRandomPower.toolTipText")); // NOI18N

        javax.swing.GroupLayout staticPokemonPanelLayout = new javax.swing.GroupLayout(staticPokemonPanel);
        staticPokemonPanel.setLayout(staticPokemonPanelLayout);
        staticPokemonPanelLayout.setHorizontalGroup(
            staticPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(staticPokemonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(staticPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stpUnchangedRB)
                    .addComponent(stpRandomL4LRB)
                    .addComponent(stpRandomTotalRB)
                    .addComponent(stpRandomPower))
                .addContainerGap(407, Short.MAX_VALUE)
                .addGroup(staticPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(stpLimitMusketeers)
                        .addComponent(stpLimit600))
                    .addGap(234, 234, 234))
        );
        staticPokemonPanelLayout.setVerticalGroup(
            staticPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(staticPokemonPanelLayout.createSequentialGroup()
                .addComponent(stpUnchangedRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(stpRandomL4LRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(stpRandomTotalRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(stpRandomPower))
            .addGroup(staticPokemonPanelLayout.createSequentialGroup()
                    .addComponent(stpLimitMusketeers)
                    .addComponent(stpLimit600)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(stpLimitMusketeers)
                .addComponent(stpLimit600))
        );

        inGameTradesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.inGameTradesPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        ingameTradesButtonGroup.add(igtUnchangedRB);
        igtUnchangedRB.setSelected(true);
        igtUnchangedRB.setText(bundle.getString("RandomizerGUI.igtUnchangedRB.text")); // NOI18N
        igtUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.igtUnchangedRB.toolTipText")); // NOI18N
        igtUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                igtUnchangedRBActionPerformed(evt);
            }
        });

        ingameTradesButtonGroup.add(igtGivenOnlyRB);
        igtGivenOnlyRB.setText(bundle.getString("RandomizerGUI.igtGivenOnlyRB.text")); // NOI18N
        igtGivenOnlyRB.setToolTipText(bundle.getString("RandomizerGUI.igtGivenOnlyRB.toolTipText")); // NOI18N
        igtGivenOnlyRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                igtGivenOnlyRBActionPerformed(evt);
            }
        });

        ingameTradesButtonGroup.add(igtBothRB);
        igtBothRB.setText(bundle.getString("RandomizerGUI.igtBothRB.text")); // NOI18N
        igtBothRB.setToolTipText(bundle.getString("RandomizerGUI.igtBothRB.toolTipText")); // NOI18N
        igtBothRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                igtBothRBActionPerformed(evt);
            }
        });

        igtRandomNicknameCB.setText(bundle.getString("RandomizerGUI.igtRandomNicknameCB.text")); // NOI18N
        igtRandomNicknameCB.setToolTipText(bundle.getString("RandomizerGUI.igtRandomNicknameCB.toolTipText")); // NOI18N

        stpLimitMusketeers.setText(bundle.getString("RandomizerGUI.stpLimitMusketeers.text")); // NOI18N
        stpLimitMusketeers.setToolTipText(bundle.getString("RandomizerGUI.stpLimitMusketeers.toolTipText")); // NOI18N

        stpLimit600.setText(bundle.getString("RandomizerGUI.stpLimit600.text")); // NOI18N
        stpLimit600.setToolTipText(bundle.getString("RandomizerGUI.stpLimit600.toolTipText")); // NOI18N

        
        igtRandomOTCB.setText(bundle.getString("RandomizerGUI.igtRandomOTCB.text")); // NOI18N
        igtRandomOTCB.setToolTipText(bundle.getString("RandomizerGUI.igtRandomOTCB.toolTipText")); // NOI18N

        igtRandomIVsCB.setText(bundle.getString("RandomizerGUI.igtRandomIVsCB.text")); // NOI18N
        igtRandomIVsCB.setToolTipText(bundle.getString("RandomizerGUI.igtRandomIVsCB.toolTipText")); // NOI18N

        igtRandomItemCB.setText(bundle.getString("RandomizerGUI.igtRandomItemCB.text")); // NOI18N
        igtRandomItemCB.setToolTipText(bundle.getString("RandomizerGUI.igtRandomItemCB.toolTipText")); // NOI18N

        javax.swing.GroupLayout inGameTradesPanelLayout = new javax.swing.GroupLayout(inGameTradesPanel);
        inGameTradesPanel.setLayout(inGameTradesPanelLayout);
        inGameTradesPanelLayout.setHorizontalGroup(
            inGameTradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inGameTradesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inGameTradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(igtUnchangedRB)
                    .addComponent(igtGivenOnlyRB)
                    .addComponent(igtBothRB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 229, Short.MAX_VALUE)
                .addGroup(inGameTradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(igtRandomItemCB)
                    .addComponent(igtRandomNicknameCB)
                    .addComponent(igtRandomOTCB)
                    .addComponent(igtRandomIVsCB))
                .addGap(113, 113, 113))
        );
        inGameTradesPanelLayout.setVerticalGroup(
            inGameTradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inGameTradesPanelLayout.createSequentialGroup()
                .addGroup(inGameTradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(igtUnchangedRB)
                    .addComponent(igtRandomNicknameCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(inGameTradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(igtGivenOnlyRB)
                    .addComponent(igtRandomOTCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(inGameTradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(igtBothRB)
                    .addComponent(igtRandomIVsCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(igtRandomItemCB)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout startersInnerPanelLayout = new javax.swing.GroupLayout(startersInnerPanel);
        startersInnerPanel.setLayout(startersInnerPanelLayout);
        startersInnerPanelLayout.setHorizontalGroup(
            startersInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startersInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(startersInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(starterPokemonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(staticPokemonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inGameTradesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        startersInnerPanelLayout.setVerticalGroup(
            startersInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startersInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(starterPokemonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(staticPokemonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(inGameTradesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        randomizerOptionsPane.addTab(bundle.getString("RandomizerGUI.startersInnerPanel.TabConstraints.tabTitle"), startersInnerPanel); // NOI18N

        pokemonMovesetsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.pokemonMovesetsPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        pokeMovesetsButtonGroup.add(pmsUnchangedRB);
        pmsUnchangedRB.setSelected(true);
        pmsUnchangedRB.setText(bundle.getString("RandomizerGUI.pmsUnchangedRB.text")); // NOI18N
        pmsUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.pmsUnchangedRB.toolTipText")); // NOI18N
        pmsUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmsUnchangedRBActionPerformed(evt);
            }
        });

        pokeMovesetsButtonGroup.add(pmsRandomTypeRB);
        pmsRandomTypeRB.setText(bundle.getString("RandomizerGUI.pmsRandomTypeRB.text")); // NOI18N
        pmsRandomTypeRB.setToolTipText(bundle.getString("RandomizerGUI.pmsRandomTypeRB.toolTipText")); // NOI18N
        pmsRandomTypeRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmsRandomTypeRBActionPerformed(evt);
            }
        });

        pokeMovesetsButtonGroup.add(pmsRandomTotalRB);
        pmsRandomTotalRB.setText(bundle.getString("RandomizerGUI.pmsRandomTotalRB.text")); // NOI18N
        pmsRandomTotalRB.setToolTipText(bundle.getString("RandomizerGUI.pmsRandomTotalRB.toolTipText")); // NOI18N
        pmsRandomTotalRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmsRandomTotalRBActionPerformed(evt);
            }
        });

        pokeMovesetsButtonGroup.add(pmsMetronomeOnlyRB);
        pmsMetronomeOnlyRB.setText(bundle.getString("RandomizerGUI.pmsMetronomeOnlyRB.text")); // NOI18N
        pmsMetronomeOnlyRB.setToolTipText(bundle.getString("RandomizerGUI.pmsMetronomeOnlyRB.toolTipText")); // NOI18N
        pmsMetronomeOnlyRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmsMetronomeOnlyRBActionPerformed(evt);
            }
        });

        pmsGuaranteedMovesCB.setText(bundle.getString("RandomizerGUI.pmsGuaranteedMovesCB.text")); // NOI18N
        pmsGuaranteedMovesCB.setToolTipText(bundle.getString("RandomizerGUI.pmsGuaranteedMovesCB.toolTipText")); // NOI18N
        pmsGuaranteedMovesCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { pmsGuaranteedMovesCBActionPerformed(evt); }
        });

        pmsGuaranteedMovesSlider.setMajorTickSpacing(1);
        pmsGuaranteedMovesSlider.setMaximum(4);
        pmsGuaranteedMovesSlider.setMinimum(2);
        pmsGuaranteedMovesSlider.setPaintLabels(true);
        pmsGuaranteedMovesSlider.setToolTipText(bundle.getString("RandomizerGUI.pmsGuaranteedMovesSlider.toolTipText")); // NOI18N
        pmsGuaranteedMovesSlider.setValue(2);

        pmsReorderDamagingMovesCB.setText(bundle.getString("RandomizerGUI.pmsReorderDamagingMovesCB.text")); // NOI18N
        pmsReorderDamagingMovesCB.setToolTipText(bundle.getString("RandomizerGUI.pmsReorderDamagingMovesCB.toolTipText")); // NOI18N

        pmsForceGoodDamagingCB.setText(bundle.getString("RandomizerGUI.pmsForceGoodDamagingCB.text")); // NOI18N
        pmsForceGoodDamagingCB.setToolTipText(bundle.getString("RandomizerGUI.pmsForceGoodDamagingCB.toolTipText")); // NOI18N
        pmsForceGoodDamagingCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmsForceGoodDamagingCBActionPerformed(evt);
            }
        });

        pmsForceGoodDamagingSlider.setMajorTickSpacing(20);
        pmsForceGoodDamagingSlider.setMinorTickSpacing(5);
        pmsForceGoodDamagingSlider.setPaintLabels(true);
        pmsForceGoodDamagingSlider.setPaintTicks(true);
        pmsForceGoodDamagingSlider.setSnapToTicks(true);
        pmsForceGoodDamagingSlider.setToolTipText(bundle.getString("RandomizerGUI.pmsForceGoodDamagingSlider.toolTipText")); // NOI18N
        pmsForceGoodDamagingSlider.setValue(0);

        javax.swing.GroupLayout pokemonMovesetsPanelLayout = new javax.swing.GroupLayout(pokemonMovesetsPanel);
        pokemonMovesetsPanel.setLayout(pokemonMovesetsPanelLayout);
        pokemonMovesetsPanelLayout.setHorizontalGroup(
            pokemonMovesetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pokemonMovesetsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pokemonMovesetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pokemonMovesetsPanelLayout.createSequentialGroup()
                        .addComponent(pmsUnchangedRB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pokemonMovesetsPanelLayout.createSequentialGroup()
                            .addComponent(pmsGuaranteedMovesCB)
                            .addComponent(pmsGuaranteedMovesSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        )
                    .addGroup(pokemonMovesetsPanelLayout.createSequentialGroup()
                        .addComponent(pmsRandomTypeRB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pmsReorderDamagingMovesCB)
                        .addGap(204, 204, 204))
                    .addGroup(pokemonMovesetsPanelLayout.createSequentialGroup()
                        .addComponent(pmsRandomTotalRB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pmsForceGoodDamagingCB)
                        .addGap(162, 162, 162))
                    .addGroup(pokemonMovesetsPanelLayout.createSequentialGroup()
                        .addComponent(pmsMetronomeOnlyRB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pmsForceGoodDamagingSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(153, 153, 153))))
        );
        pokemonMovesetsPanelLayout.setVerticalGroup(
            pokemonMovesetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pokemonMovesetsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pokemonMovesetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addGroup(pokemonMovesetsPanelLayout.createSequentialGroup()
                        .addGroup(pokemonMovesetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pmsUnchangedRB)
                            .addComponent(pmsGuaranteedMovesCB))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pokemonMovesetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pmsRandomTypeRB)
                            .addComponent(pmsReorderDamagingMovesCB)))
                    .addComponent(pmsGuaranteedMovesSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pokemonMovesetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pmsRandomTotalRB)
                    .addComponent(pmsForceGoodDamagingCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pokemonMovesetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pmsMetronomeOnlyRB)
                    .addComponent(pmsForceGoodDamagingSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        moveDataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.moveDataPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        mdRandomPowerCB.setText(bundle.getString("RandomizerGUI.mdRandomPowerCB.text")); // NOI18N
        mdRandomPowerCB.setToolTipText(bundle.getString("RandomizerGUI.mdRandomPowerCB.toolTipText")); // NOI18N

        mdRandomAccuracyCB.setText(bundle.getString("RandomizerGUI.mdRandomAccuracyCB.text")); // NOI18N
        mdRandomAccuracyCB.setToolTipText(bundle.getString("RandomizerGUI.mdRandomAccuracyCB.toolTipText")); // NOI18N

        mdRandomPPCB.setText(bundle.getString("RandomizerGUI.mdRandomPPCB.text")); // NOI18N
        mdRandomPPCB.setToolTipText(bundle.getString("RandomizerGUI.mdRandomPPCB.toolTipText")); // NOI18N

        mdRandomTypeCB.setText(bundle.getString("RandomizerGUI.mdRandomTypeCB.text")); // NOI18N
        mdRandomTypeCB.setToolTipText(bundle.getString("RandomizerGUI.mdRandomTypeCB.toolTipText")); // NOI18N

        mdRandomCategoryCB.setText(bundle.getString("RandomizerGUI.mdRandomCategoryCB.text")); // NOI18N
        mdRandomCategoryCB.setToolTipText(bundle.getString("RandomizerGUI.mdRandomCategoryCB.toolTipText")); // NOI18N

        goUpdateMovesCheckBox.setText(bundle.getString("RandomizerGUI.goUpdateMovesCheckBox.text")); // NOI18N
        goUpdateMovesCheckBox.setToolTipText(bundle.getString("RandomizerGUI.goUpdateMovesCheckBox.toolTipText")); // NOI18N
        goUpdateMovesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goUpdateMovesCheckBoxActionPerformed(evt);
            }
        });

        goUpdateMovesLegacyCheckBox.setText(bundle.getString("RandomizerGUI.goUpdateMovesLegacyCheckBox.text")); // NOI18N
        goUpdateMovesLegacyCheckBox.setToolTipText(bundle.getString("RandomizerGUI.goUpdateMovesLegacyCheckBox.toolTipText")); // NOI18N

        javax.swing.GroupLayout moveDataPanelLayout = new javax.swing.GroupLayout(moveDataPanel);
        moveDataPanel.setLayout(moveDataPanelLayout);
        moveDataPanelLayout.setHorizontalGroup(
            moveDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(moveDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(moveDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mdRandomPowerCB)
                    .addComponent(mdRandomAccuracyCB)
                    .addComponent(mdRandomPPCB)
                    .addComponent(mdRandomTypeCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 211, Short.MAX_VALUE)
                .addGroup(moveDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mdRandomCategoryCB)
                    .addGroup(moveDataPanelLayout.createSequentialGroup()
                        .addComponent(goUpdateMovesCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(goUpdateMovesLegacyCheckBox)))
                .addGap(190, 190, 190))
        );
        moveDataPanelLayout.setVerticalGroup(
            moveDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(moveDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(moveDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mdRandomPowerCB)
                    .addComponent(mdRandomCategoryCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mdRandomAccuracyCB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mdRandomPPCB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(moveDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mdRandomTypeCB)
                    .addComponent(goUpdateMovesCheckBox)
                    .addComponent(goUpdateMovesLegacyCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout movesAndSetsPanelLayout = new javax.swing.GroupLayout(movesAndSetsPanel);
        movesAndSetsPanel.setLayout(movesAndSetsPanelLayout);
        movesAndSetsPanelLayout.setHorizontalGroup(
            movesAndSetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(movesAndSetsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(movesAndSetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(moveDataPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pokemonMovesetsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        movesAndSetsPanelLayout.setVerticalGroup(
            movesAndSetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(movesAndSetsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(moveDataPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pokemonMovesetsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(109, Short.MAX_VALUE))
        );

        randomizerOptionsPane.addTab(bundle.getString("RandomizerGUI.movesAndSetsPanel.TabConstraints.tabTitle"), movesAndSetsPanel); // NOI18N

        trainersPokemonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.trainersPokemonPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        trainerPokesButtonGroup.add(tpUnchangedRB);
        tpUnchangedRB.setSelected(true);
        tpUnchangedRB.setText(bundle.getString("RandomizerGUI.tpUnchangedRB.text")); // NOI18N
        tpUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.tpUnchangedRB.toolTipText")); // NOI18N
        tpUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tpUnchangedRBActionPerformed(evt);
            }
        });

        trainerPokesButtonGroup.add(tpRandomRB);
        tpRandomRB.setText(bundle.getString("RandomizerGUI.tpRandomRB.text")); // NOI18N
        tpRandomRB.setToolTipText(bundle.getString("RandomizerGUI.tpRandomRB.toolTipText")); // NOI18N
        tpRandomRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tpRandomRBActionPerformed(evt);
            }
        });

        trainerPokesButtonGroup.add(tpTypeThemedRB);
        tpTypeThemedRB.setText(bundle.getString("RandomizerGUI.tpTypeThemedRB.text")); // NOI18N
        tpTypeThemedRB.setToolTipText(bundle.getString("RandomizerGUI.tpTypeThemedRB.toolTipText")); // NOI18N
        tpTypeThemedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tpTypeThemedRBActionPerformed(evt);
            }
        });
        
        trainerPokesButtonGroup.add(tpEnforceDistribution);
        tpEnforceDistribution.setText(bundle.getString("RandomizerGUI.tpEnforceDistribution.text")); // NOI18N
        tpEnforceDistribution.setToolTipText(bundle.getString("RandomizerGUI.tpEnforceDistribution.toolTipText")); // NOI18N
        tpEnforceDistribution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tpEnforceDistributionActionPerformed(evt);
            }
        });
        
        trainerPokesButtonGroup.add(tpEnforceMainPlaythrough);
        tpEnforceMainPlaythrough.setText(bundle.getString("RandomizerGUI.tpEnforceMainPlaythrough.text")); // NOI18N
        tpEnforceMainPlaythrough.setToolTipText(bundle.getString("RandomizerGUI.tpEnforceMainPlaythrough.toolTipText")); // NOI18N
        tpEnforceMainPlaythrough.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tpEnforceMainPlaythroughActionPerformed(evt);
            }
        });

        tpPowerLevelsCB.setText(bundle.getString("RandomizerGUI.tpPowerLevelsCB.text")); // NOI18N
        tpPowerLevelsCB.setToolTipText(bundle.getString("RandomizerGUI.tpPowerLevelsCB.toolTipText")); // NOI18N
        tpPowerLevelsCB.setEnabled(false);

        tpTypeWeightingCB.setText(bundle.getString("RandomizerGUI.tpTypeWeightingCB.text")); // NOI18N
        tpTypeWeightingCB.setToolTipText(bundle.getString("RandomizerGUI.tpTypeWeightingCB.toolTipText")); // NOI18N
        tpTypeWeightingCB.setEnabled(false);

        tpRivalCarriesStarterCB.setText(bundle.getString("RandomizerGUI.tpRivalCarriesStarterCB.text")); // NOI18N
        tpRivalCarriesStarterCB.setToolTipText(bundle.getString("RandomizerGUI.tpRivalCarriesStarterCB.toolTipText")); // NOI18N
        tpRivalCarriesStarterCB.setEnabled(false);

        tpNoLegendariesCB.setText(bundle.getString("RandomizerGUI.tpNoLegendariesCB.text")); // NOI18N
        tpNoLegendariesCB.setEnabled(false);

        tnRandomizeCB.setText(bundle.getString("RandomizerGUI.tnRandomizeCB.text")); // NOI18N
        tnRandomizeCB.setToolTipText(bundle.getString("RandomizerGUI.tnRandomizeCB.toolTipText")); // NOI18N

        tcnRandomizeCB.setText(bundle.getString("RandomizerGUI.tcnRandomizeCB.text")); // NOI18N
        tcnRandomizeCB.setToolTipText(bundle.getString("RandomizerGUI.tcnRandomizeCB.toolTipText")); // NOI18N

        tpNoEarlyShedinjaCB.setText(bundle.getString("RandomizerGUI.tpNoEarlyShedinjaCB.text")); // NOI18N
        tpNoEarlyShedinjaCB.setToolTipText(bundle.getString("RandomizerGUI.tpNoEarlyShedinjaCB.toolTipText")); // NOI18N

        tpForceFullyEvolvedCB.setText(bundle.getString("RandomizerGUI.tpForceFullyEvolvedCB.text")); // NOI18N
        tpForceFullyEvolvedCB.setToolTipText(bundle.getString("RandomizerGUI.tpForceFullyEvolvedCB.toolTipText")); // NOI18N
        tpForceFullyEvolvedCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tpForceFullyEvolvedCBActionPerformed(evt);
            }
        });

        tpForceFullyEvolvedSlider.setMajorTickSpacing(5);
        tpForceFullyEvolvedSlider.setMaximum(65);
        tpForceFullyEvolvedSlider.setMinimum(30);
        tpForceFullyEvolvedSlider.setMinorTickSpacing(1);
        tpForceFullyEvolvedSlider.setPaintLabels(true);
        tpForceFullyEvolvedSlider.setPaintTicks(true);
        tpForceFullyEvolvedSlider.setToolTipText(bundle.getString("RandomizerGUI.tpForceFullyEvolvedSlider.toolTipText")); // NOI18N
        tpForceFullyEvolvedSlider.setValue(30);

        tpLevelModifierCB.setText(bundle.getString("RandomizerGUI.tpLevelModifierCB.text")); // NOI18N
        tpLevelModifierCB.setToolTipText(bundle.getString("RandomizerGUI.tpLevelModifierCB.toolTipText")); // NOI18N
        tpLevelModifierCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tpLevelModifierCBActionPerformed(evt);
            }
        });

        tpLevelModifierSlider.setMajorTickSpacing(10);
        tpLevelModifierSlider.setMaximum(50);
        tpLevelModifierSlider.setMinimum(-50);
        tpLevelModifierSlider.setMinorTickSpacing(2);
        tpLevelModifierSlider.setPaintLabels(true);
        tpLevelModifierSlider.setPaintTicks(true);
        tpLevelModifierSlider.setToolTipText(bundle.getString("RandomizerGUI.tpLevelModifierSlider.toolTipText")); // NOI18N
        tpLevelModifierSlider.setValue(0);

        javax.swing.GroupLayout trainersPokemonPanelLayout = new javax.swing.GroupLayout(trainersPokemonPanel);
        trainersPokemonPanel.setLayout(trainersPokemonPanelLayout);
        trainersPokemonPanelLayout.setHorizontalGroup(
            trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(trainersPokemonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tpTypeThemedRB)
                    .addGroup(trainersPokemonPanelLayout.createSequentialGroup()
                        .addGroup(trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tpUnchangedRB)
                            .addComponent(tpRandomRB)
                            .addComponent(tpEnforceDistribution)
                            .addComponent(tpEnforceMainPlaythrough)
                            )
                        .addGap(47, 47, 47)
                        .addGroup(trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tpTypeWeightingCB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tpRivalCarriesStarterCB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tpPowerLevelsCB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tpNoLegendariesCB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                )
                            .addComponent(tpNoEarlyShedinjaCB))
                        .addGap(18, 18, 18)
                        .addGroup(trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tnRandomizeCB)
                            .addComponent(tcnRandomizeCB)
                            .addComponent(tpForceFullyEvolvedCB)
                            .addComponent(tpForceFullyEvolvedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tpLevelModifierCB)
                            .addComponent(tpLevelModifierSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(143, Short.MAX_VALUE))
        );
        trainersPokemonPanelLayout.setVerticalGroup(
            trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(trainersPokemonPanelLayout.createSequentialGroup()
                .addGroup(trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tpUnchangedRB)
                    .addComponent(tpRivalCarriesStarterCB)
                    .addComponent(tnRandomizeCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tpRandomRB)
                    .addComponent(tpPowerLevelsCB)
                    .addComponent(tcnRandomizeCB)
                    )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tpEnforceDistribution)
                    .addComponent(tpTypeWeightingCB)
                    )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tpEnforceMainPlaythrough)
                        .addComponent(tpNoLegendariesCB)
                        )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tpTypeThemedRB)                        
                        .addComponent(tpNoEarlyShedinjaCB)
                        )

                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tpEnforceMainPlaythrough)                        
                        .addComponent(tpNoEarlyShedinjaCB)
                        )

                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
//                .addGroup(trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
//                        .addComponent(tpLevelModifierCB)
//                        )
//                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
//                .addGroup(trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
//                        .addComponent(tpLevelModifierSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
//                        )
//                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
//                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)

//                    
//                .addGroup(trainersPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                    .addGroup(trainersPokemonPanelLayout.createSequentialGroup()
//                        .addComponent(tpNoLegendariesCB)
//                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
//                        .addComponent(tpNoEarlyShedinjaCB)
////                        .addComponent(tpEnforceDistribution)
////                        .addComponent(tpEnforceMainPlaythrough)
//                        )
//                    .addComponent(tpForceFullyEvolvedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tpForceFullyEvolvedCB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tpForceFullyEvolvedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tpLevelModifierCB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tpLevelModifierSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                )
        );

        javax.swing.GroupLayout trainersInnerPanelLayout = new javax.swing.GroupLayout(trainersInnerPanel);
        trainersInnerPanel.setLayout(trainersInnerPanelLayout);
        trainersInnerPanelLayout.setHorizontalGroup(
            trainersInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(trainersInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(trainersPokemonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        trainersInnerPanelLayout.setVerticalGroup(
            trainersInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(trainersInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(trainersPokemonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(180, Short.MAX_VALUE))
        );

        randomizerOptionsPane.addTab(bundle.getString("RandomizerGUI.trainersInnerPanel.TabConstraints.tabTitle"), trainersInnerPanel); // NOI18N

        wildPokemonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.wildPokemonPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        wildPokesButtonGroup.add(wpUnchangedRB);
        wpUnchangedRB.setSelected(true);
        wpUnchangedRB.setText(bundle.getString("RandomizerGUI.wpUnchangedRB.text")); // NOI18N
        wpUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.wpUnchangedRB.toolTipText")); // NOI18N
        wpUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpUnchangedRBActionPerformed(evt);
            }
        });

        wildPokesButtonGroup.add(wpRandomRB);
        wpRandomRB.setText(bundle.getString("RandomizerGUI.wpRandomRB.text")); // NOI18N
        wpRandomRB.setToolTipText(bundle.getString("RandomizerGUI.wpRandomRB.toolTipText")); // NOI18N
        wpRandomRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpRandomRBActionPerformed(evt);
            }
        });

        wildPokesButtonGroup.add(wpArea11RB);
        wpArea11RB.setText(bundle.getString("RandomizerGUI.wpArea11RB.text")); // NOI18N
        wpArea11RB.setToolTipText(bundle.getString("RandomizerGUI.wpArea11RB.toolTipText")); // NOI18N
        wpArea11RB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpArea11RBActionPerformed(evt);
            }
        });

        wildPokesButtonGroup.add(wpGlobalRB);
        wpGlobalRB.setText(bundle.getString("RandomizerGUI.wpGlobalRB.text")); // NOI18N
        wpGlobalRB.setToolTipText(bundle.getString("RandomizerGUI.wpGlobalRB.toolTipText")); // NOI18N
        wpGlobalRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpGlobalRBActionPerformed(evt);
            }
        });

        wildPokemonARulePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RandomizerGUI.wildPokemonARulePanel.border.title"))); // NOI18N

        wildPokesARuleButtonGroup.add(wpARNoneRB);
        wpARNoneRB.setSelected(true);
        wpARNoneRB.setText(bundle.getString("RandomizerGUI.wpARNoneRB.text")); // NOI18N
        wpARNoneRB.setToolTipText(bundle.getString("RandomizerGUI.wpARNoneRB.toolTipText")); // NOI18N
        wpARNoneRB.setEnabled(false);
        wpARNoneRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpARNoneRBActionPerformed(evt);
            }
        });

        wildPokesARuleButtonGroup.add(wpARCatchEmAllRB);
        wpARCatchEmAllRB.setText(bundle.getString("RandomizerGUI.wpARCatchEmAllRB.text")); // NOI18N
        wpARCatchEmAllRB.setToolTipText(bundle.getString("RandomizerGUI.wpARCatchEmAllRB.toolTipText")); // NOI18N
        wpARCatchEmAllRB.setEnabled(false);
        wpARCatchEmAllRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpARCatchEmAllRBActionPerformed(evt);
            }
        });

        wildPokesARuleButtonGroup.add(wpARTypeThemedRB);
        wpARTypeThemedRB.setText(bundle.getString("RandomizerGUI.wpARTypeThemedRB.text")); // NOI18N
        wpARTypeThemedRB.setToolTipText(bundle.getString("RandomizerGUI.wpARTypeThemedRB.toolTipText")); // NOI18N
        wpARTypeThemedRB.setEnabled(false);
        wpARTypeThemedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpARTypeThemedRBActionPerformed(evt);
            }
        });

        wildPokesARuleButtonGroup.add(wpARSimilarStrengthRB);
        wpARSimilarStrengthRB.setText(bundle.getString("RandomizerGUI.wpARSimilarStrengthRB.text")); // NOI18N
        wpARSimilarStrengthRB.setToolTipText(bundle.getString("RandomizerGUI.wpARSimilarStrengthRB.toolTipText")); // NOI18N
        wpARSimilarStrengthRB.setEnabled(false);
        wpARSimilarStrengthRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpARSimilarStrengthRBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout wildPokemonARulePanelLayout = new javax.swing.GroupLayout(wildPokemonARulePanel);
        wildPokemonARulePanel.setLayout(wildPokemonARulePanelLayout);
        wildPokemonARulePanelLayout.setHorizontalGroup(
            wildPokemonARulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wildPokemonARulePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(wildPokemonARulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(wildPokemonARulePanelLayout.createSequentialGroup()
                        .addComponent(wpARTypeThemedRB)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(wildPokemonARulePanelLayout.createSequentialGroup()
                        .addGroup(wildPokemonARulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(wpARSimilarStrengthRB)
                            .addComponent(wpARNoneRB)
                            .addComponent(wpARCatchEmAllRB))
                        .addContainerGap(58, Short.MAX_VALUE))))
        );
        wildPokemonARulePanelLayout.setVerticalGroup(
            wildPokemonARulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wildPokemonARulePanelLayout.createSequentialGroup()
                .addComponent(wpARNoneRB)
                .addGap(3, 3, 3)
                .addComponent(wpARSimilarStrengthRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(wpARCatchEmAllRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 3, Short.MAX_VALUE)
                .addComponent(wpARTypeThemedRB, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        wpUseTimeCB.setText(bundle.getString("RandomizerGUI.wpUseTimeCB.text")); // NOI18N
        wpUseTimeCB.setToolTipText(bundle.getString("RandomizerGUI.wpUseTimeCB.toolTipText")); // NOI18N

        wpNoLegendariesCB.setText(bundle.getString("RandomizerGUI.wpNoLegendariesCB.text")); // NOI18N

        wpCatchRateCB.setText(bundle.getString("RandomizerGUI.wpCatchRateCB.text")); // NOI18N
        wpCatchRateCB.setToolTipText(bundle.getString("RandomizerGUI.wpCatchRateCB.toolTipText")); // NOI18N
        wpCatchRateCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpCatchRateCBActionPerformed(evt);
            }
        });

        wpHeldItemsCB.setText(bundle.getString("RandomizerGUI.wpHeldItemsCB.text")); // NOI18N
        wpHeldItemsCB.setToolTipText(bundle.getString("RandomizerGUI.wpHeldItemsCB.toolTipText")); // NOI18N
        wpHeldItemsCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpHeldItemsCBActionPerformed(evt);
            }
        });

        wpHeldItemsBanBadCB.setText(bundle.getString("RandomizerGUI.wpHeldItemsBanBadCB.text")); // NOI18N
        wpHeldItemsBanBadCB.setToolTipText(bundle.getString("RandomizerGUI.wpHeldItemsBanBadCB.toolTipText")); // NOI18N

        wpShakingGrassCB.setText(bundle.getString("RandomizerGUI.wpShakingGrassCB.text"));
        wpShakingGrassCB.setToolTipText(bundle.getString("RandomizerGUI.wpShakingGrassCB.toolTipText"));

        wpCatchRateSlider.setMajorTickSpacing(1);
        wpCatchRateSlider.setMaximum(4);
        wpCatchRateSlider.setMinimum(1);
        wpCatchRateSlider.setPaintLabels(true);
        wpCatchRateSlider.setToolTipText(bundle.getString("RandomizerGUI.wpCatchRateSlider.toolTipText")); // NOI18N
        wpCatchRateSlider.setValue(1);


        wpLevelModifierCB.setText(bundle.getString("RandomizerGUI.wpLevelModifierCB.text")); // NOI18N
        wpLevelModifierCB.setToolTipText(bundle.getString("RandomizerGUI.wpLevelModifierCB.toolTipText")); // NOI18N
        wpLevelModifierCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpLevelModifierCBActionPerformed(evt);
            }
        });

        wpLevelModifierSlider.setMajorTickSpacing(10);
        wpLevelModifierSlider.setMaximum(50);
        wpLevelModifierSlider.setMinimum(-50);
        wpLevelModifierSlider.setMinorTickSpacing(2);
        wpLevelModifierSlider.setPaintLabels(true);
        wpLevelModifierSlider.setPaintTicks(true);
        wpLevelModifierSlider.setToolTipText(bundle.getString("RandomizerGUI.wpLevelModifierSlider.toolTipText")); // NOI18N
        wpLevelModifierSlider.setValue(0);

        javax.swing.GroupLayout wildPokemonPanelLayout = new javax.swing.GroupLayout(wildPokemonPanel);
        wildPokemonPanel.setLayout(wildPokemonPanelLayout);
        wildPokemonPanelLayout.setHorizontalGroup(
            wildPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wildPokemonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(wildPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(wpUnchangedRB)
                    .addComponent(wpRandomRB)
                    .addComponent(wpArea11RB)
                    .addComponent(wpGlobalRB))
                .addGap(18, 18, 18)
                .addComponent(wildPokemonARulePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(wildPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(wpUseTimeCB)
                    .addComponent(wpNoLegendariesCB)
                    .addGroup(wildPokemonPanelLayout.createSequentialGroup()
                        .addGroup(wildPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(wpCatchRateCB)
                            .addComponent(wpHeldItemsCB))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wpCatchRateSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(wpHeldItemsBanBadCB)
                    .addComponent(wpShakingGrassCB)
                    .addComponent(wpLevelModifierCB)
                    .addComponent(wpLevelModifierSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        wildPokemonPanelLayout.setVerticalGroup(
            wildPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(wildPokemonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(wildPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(wildPokemonPanelLayout.createSequentialGroup()
                        .addComponent(wpUnchangedRB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wpRandomRB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wpArea11RB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wpGlobalRB))
                    .addGroup(wildPokemonPanelLayout.createSequentialGroup()
                        .addComponent(wpUseTimeCB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wpNoLegendariesCB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(wildPokemonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(wpCatchRateSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(wildPokemonPanelLayout.createSequentialGroup()
                                .addComponent(wpCatchRateCB)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(wpHeldItemsCB)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wpHeldItemsBanBadCB)
                        .addComponent(wpShakingGrassCB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wpLevelModifierCB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wpLevelModifierSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(wildPokemonARulePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9))
        );

        javax.swing.GroupLayout wildsInnerPanelLayout = new javax.swing.GroupLayout(wildsInnerPanel);
        wildsInnerPanel.setLayout(wildsInnerPanelLayout);
        wildsInnerPanelLayout.setHorizontalGroup(
            wildsInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wildsInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wildPokemonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        wildsInnerPanelLayout.setVerticalGroup(
            wildsInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wildsInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wildPokemonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(245, Short.MAX_VALUE))
        );

        randomizerOptionsPane.addTab(bundle.getString("RandomizerGUI.wildsInnerPanel.TabConstraints.tabTitle"), wildsInnerPanel); // NOI18N

        tmhmsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.tmhmsPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        tmMovesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RandomizerGUI.tmMovesPanel.border.title"))); // NOI18N

        tmMovesButtonGroup.add(tmmUnchangedRB);
        tmmUnchangedRB.setSelected(true);
        tmmUnchangedRB.setText(bundle.getString("RandomizerGUI.tmmUnchangedRB.text")); // NOI18N
        tmmUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.tmmUnchangedRB.toolTipText")); // NOI18N
        tmmUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tmmUnchangedRBActionPerformed(evt);
            }
        });

        tmMovesButtonGroup.add(tmmRandomRB);
        tmmRandomRB.setText(bundle.getString("RandomizerGUI.tmmRandomRB.text")); // NOI18N
        tmmRandomRB.setToolTipText(bundle.getString("RandomizerGUI.tmmRandomRB.toolTipText")); // NOI18N
        tmmRandomRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tmmRandomRBActionPerformed(evt);
            }
        });

        tmLearningSanityCB.setText(bundle.getString("RandomizerGUI.tmLearningSanityCB.text")); // NOI18N
        tmLearningSanityCB.setToolTipText(bundle.getString("RandomizerGUI.tmLearningSanityCB.toolTipText")); // NOI18N

        tmKeepFieldMovesCB.setText(bundle.getString("RandomizerGUI.tmKeepFieldMovesCB.text")); // NOI18N
        tmKeepFieldMovesCB.setToolTipText(bundle.getString("RandomizerGUI.tmKeepFieldMovesCB.toolTipText")); // NOI18N

        tmFullHMCompatCB.setText(bundle.getString("RandomizerGUI.tmFullHMCompatCB.text")); // NOI18N
        tmFullHMCompatCB.setToolTipText(bundle.getString("RandomizerGUI.tmFullHMCompatCB.toolTipText")); // NOI18N

        tmForceGoodDamagingCB.setText(bundle.getString("RandomizerGUI.tmForceGoodDamagingCB.text")); // NOI18N
        tmForceGoodDamagingCB.setToolTipText(bundle.getString("RandomizerGUI.tmForceGoodDamagingCB.toolTipText")); // NOI18N
        tmForceGoodDamagingCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tmForceGoodDamagingCBActionPerformed(evt);
            }
        });

        tmForceGoodDamagingSlider.setMajorTickSpacing(20);
        tmForceGoodDamagingSlider.setMinorTickSpacing(5);
        tmForceGoodDamagingSlider.setPaintLabels(true);
        tmForceGoodDamagingSlider.setPaintTicks(true);
        tmForceGoodDamagingSlider.setSnapToTicks(true);
        tmForceGoodDamagingSlider.setToolTipText(bundle.getString("RandomizerGUI.tmForceGoodDamagingSlider.toolTipText")); // NOI18N
        tmForceGoodDamagingSlider.setValue(0);

        javax.swing.GroupLayout tmMovesPanelLayout = new javax.swing.GroupLayout(tmMovesPanel);
        tmMovesPanel.setLayout(tmMovesPanelLayout);
        tmMovesPanelLayout.setHorizontalGroup(
            tmMovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tmMovesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tmMovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tmmUnchangedRB)
                    .addComponent(tmmRandomRB)
                    .addComponent(tmFullHMCompatCB))
                .addGap(17, 17, 17)
                .addGroup(tmMovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tmForceGoodDamagingSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tmForceGoodDamagingCB)
                    .addComponent(tmKeepFieldMovesCB)
                    .addComponent(tmLearningSanityCB))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        tmMovesPanelLayout.setVerticalGroup(
            tmMovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tmMovesPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(tmMovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tmmUnchangedRB)
                    .addComponent(tmLearningSanityCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tmMovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tmmRandomRB)
                    .addComponent(tmKeepFieldMovesCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tmMovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tmFullHMCompatCB)
                    .addComponent(tmForceGoodDamagingCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tmForceGoodDamagingSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tmHmCompatPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RandomizerGUI.tmHmCompatPanel.border.title"))); // NOI18N

        tmHmCompatibilityButtonGroup.add(thcUnchangedRB);
        thcUnchangedRB.setSelected(true);
        thcUnchangedRB.setText(bundle.getString("RandomizerGUI.thcUnchangedRB.text")); // NOI18N
        thcUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.thcUnchangedRB.toolTipText")); // NOI18N
        thcUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thcUnchangedRBActionPerformed(evt);
            }
        });

        tmHmCompatibilityButtonGroup.add(thcRandomTypeRB);
        thcRandomTypeRB.setText(bundle.getString("RandomizerGUI.thcRandomTypeRB.text")); // NOI18N
        thcRandomTypeRB.setToolTipText(bundle.getString("RandomizerGUI.thcRandomTypeRB.toolTipText")); // NOI18N
        thcRandomTypeRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thcRandomTypeRBActionPerformed(evt);
            }
        });

        tmHmCompatibilityButtonGroup.add(thcRandomTotalRB);
        thcRandomTotalRB.setText(bundle.getString("RandomizerGUI.thcRandomTotalRB.text")); // NOI18N
        thcRandomTotalRB.setToolTipText(bundle.getString("RandomizerGUI.thcRandomTotalRB.toolTipText")); // NOI18N
        thcRandomTotalRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thcRandomTotalRBActionPerformed(evt);
            }
        });

        tmHmCompatibilityButtonGroup.add(thcFullRB);
        thcFullRB.setText(bundle.getString("RandomizerGUI.thcFullRB.text")); // NOI18N
        thcFullRB.setToolTipText(bundle.getString("RandomizerGUI.thcFullRB.toolTipText")); // NOI18N
        thcFullRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thcFullRBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tmHmCompatPanelLayout = new javax.swing.GroupLayout(tmHmCompatPanel);
        tmHmCompatPanel.setLayout(tmHmCompatPanelLayout);
        tmHmCompatPanelLayout.setHorizontalGroup(
            tmHmCompatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tmHmCompatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tmHmCompatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(thcUnchangedRB)
                    .addComponent(thcRandomTypeRB)
                    .addComponent(thcRandomTotalRB)
                    .addComponent(thcFullRB))
                .addContainerGap(79, Short.MAX_VALUE))
        );
        tmHmCompatPanelLayout.setVerticalGroup(
            tmHmCompatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tmHmCompatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(thcUnchangedRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(thcRandomTypeRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(thcRandomTotalRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(thcFullRB)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tmhmsPanelLayout = new javax.swing.GroupLayout(tmhmsPanel);
        tmhmsPanel.setLayout(tmhmsPanelLayout);
        tmhmsPanelLayout.setHorizontalGroup(
            tmhmsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tmhmsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tmMovesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tmHmCompatPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tmhmsPanelLayout.setVerticalGroup(
            tmhmsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tmhmsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(tmhmsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tmMovesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tmHmCompatPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        moveTutorsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.moveTutorsPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        mtMovesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RandomizerGUI.mtMovesPanel.border.title"))); // NOI18N

        mtMovesButtonGroup.add(mtmUnchangedRB);
        mtmUnchangedRB.setSelected(true);
        mtmUnchangedRB.setText(bundle.getString("RandomizerGUI.mtmUnchangedRB.text")); // NOI18N
        mtmUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.mtmUnchangedRB.toolTipText")); // NOI18N
        mtmUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mtmUnchangedRBActionPerformed(evt);
            }
        });

        mtMovesButtonGroup.add(mtmRandomRB);
        mtmRandomRB.setText(bundle.getString("RandomizerGUI.mtmRandomRB.text")); // NOI18N
        mtmRandomRB.setToolTipText(bundle.getString("RandomizerGUI.mtmRandomRB.toolTipText")); // NOI18N
        mtmRandomRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mtmRandomRBActionPerformed(evt);
            }
        });

        mtLearningSanityCB.setText(bundle.getString("RandomizerGUI.mtLearningSanityCB.text")); // NOI18N
        mtLearningSanityCB.setToolTipText(bundle.getString("RandomizerGUI.mtLearningSanityCB.toolTipText")); // NOI18N

        mtKeepFieldMovesCB.setText(bundle.getString("RandomizerGUI.mtKeepFieldMovesCB.text")); // NOI18N
        mtKeepFieldMovesCB.setToolTipText(bundle.getString("RandomizerGUI.mtKeepFieldMovesCB.toolTipText")); // NOI18N

        mtForceGoodDamagingCB.setText(bundle.getString("RandomizerGUI.mtForceGoodDamagingCB.text")); // NOI18N
        mtForceGoodDamagingCB.setToolTipText(bundle.getString("RandomizerGUI.mtForceGoodDamagingCB.toolTipText")); // NOI18N
        mtForceGoodDamagingCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mtForceGoodDamagingCBActionPerformed(evt);
            }
        });

        mtForceGoodDamagingSlider.setMajorTickSpacing(20);
        mtForceGoodDamagingSlider.setMinorTickSpacing(5);
        mtForceGoodDamagingSlider.setPaintLabels(true);
        mtForceGoodDamagingSlider.setPaintTicks(true);
        mtForceGoodDamagingSlider.setSnapToTicks(true);
        mtForceGoodDamagingSlider.setToolTipText(bundle.getString("RandomizerGUI.mtForceGoodDamagingSlider.toolTipText")); // NOI18N
        mtForceGoodDamagingSlider.setValue(0);

        javax.swing.GroupLayout mtMovesPanelLayout = new javax.swing.GroupLayout(mtMovesPanel);
        mtMovesPanel.setLayout(mtMovesPanelLayout);
        mtMovesPanelLayout.setHorizontalGroup(
            mtMovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mtMovesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mtMovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mtmUnchangedRB)
                    .addComponent(mtmRandomRB))
                .addGap(64, 64, 64)
                .addGroup(mtMovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mtForceGoodDamagingSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mtForceGoodDamagingCB)
                    .addComponent(mtKeepFieldMovesCB)
                    .addComponent(mtLearningSanityCB))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        mtMovesPanelLayout.setVerticalGroup(
            mtMovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mtMovesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mtMovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mtmUnchangedRB)
                    .addComponent(mtLearningSanityCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mtMovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mtmRandomRB)
                    .addComponent(mtKeepFieldMovesCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mtForceGoodDamagingCB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mtForceGoodDamagingSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mtCompatPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RandomizerGUI.mtCompatPanel.border.title"))); // NOI18N

        mtCompatibilityButtonGroup.add(mtcUnchangedRB);
        mtcUnchangedRB.setSelected(true);
        mtcUnchangedRB.setText(bundle.getString("RandomizerGUI.mtcUnchangedRB.text")); // NOI18N
        mtcUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.mtcUnchangedRB.toolTipText")); // NOI18N
        mtcUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mtcUnchangedRBActionPerformed(evt);
            }
        });

        mtCompatibilityButtonGroup.add(mtcRandomTypeRB);
        mtcRandomTypeRB.setText(bundle.getString("RandomizerGUI.mtcRandomTypeRB.text")); // NOI18N
        mtcRandomTypeRB.setToolTipText(bundle.getString("RandomizerGUI.mtcRandomTypeRB.toolTipText")); // NOI18N
        mtcRandomTypeRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mtcRandomTypeRBActionPerformed(evt);
            }
        });

        mtCompatibilityButtonGroup.add(mtcRandomTotalRB);
        mtcRandomTotalRB.setText(bundle.getString("RandomizerGUI.mtcRandomTotalRB.text")); // NOI18N
        mtcRandomTotalRB.setToolTipText(bundle.getString("RandomizerGUI.mtcRandomTotalRB.toolTipText")); // NOI18N
        mtcRandomTotalRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mtcRandomTotalRBActionPerformed(evt);
            }
        });

        mtCompatibilityButtonGroup.add(mtcFullRB);
        mtcFullRB.setText(bundle.getString("RandomizerGUI.mtcFullRB.text")); // NOI18N
        mtcFullRB.setToolTipText(bundle.getString("RandomizerGUI.mtcFullRB.toolTipText")); // NOI18N
        mtcFullRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mtcFullRBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mtCompatPanelLayout = new javax.swing.GroupLayout(mtCompatPanel);
        mtCompatPanel.setLayout(mtCompatPanelLayout);
        mtCompatPanelLayout.setHorizontalGroup(
            mtCompatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mtCompatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mtCompatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mtcUnchangedRB)
                    .addComponent(mtcRandomTypeRB)
                    .addComponent(mtcRandomTotalRB)
                    .addComponent(mtcFullRB))
                .addContainerGap(79, Short.MAX_VALUE))
        );
        mtCompatPanelLayout.setVerticalGroup(
            mtCompatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mtCompatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mtcUnchangedRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mtcRandomTypeRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mtcRandomTotalRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mtcFullRB)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mtNoExistLabel.setText(bundle.getString("RandomizerGUI.mtNoExistLabel.text")); // NOI18N

        javax.swing.GroupLayout moveTutorsPanelLayout = new javax.swing.GroupLayout(moveTutorsPanel);
        moveTutorsPanel.setLayout(moveTutorsPanelLayout);
        moveTutorsPanelLayout.setHorizontalGroup(
            moveTutorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(moveTutorsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(moveTutorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(moveTutorsPanelLayout.createSequentialGroup()
                        .addComponent(mtNoExistLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(moveTutorsPanelLayout.createSequentialGroup()
                        .addComponent(mtMovesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(mtCompatPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))))
        );
        moveTutorsPanelLayout.setVerticalGroup(
            moveTutorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(moveTutorsPanelLayout.createSequentialGroup()
                .addComponent(mtNoExistLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(moveTutorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mtCompatPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mtMovesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout tmHmTutorPanelLayout = new javax.swing.GroupLayout(tmHmTutorPanel);
        tmHmTutorPanel.setLayout(tmHmTutorPanelLayout);
        tmHmTutorPanelLayout.setHorizontalGroup(
            tmHmTutorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tmHmTutorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tmHmTutorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(moveTutorsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tmhmsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tmHmTutorPanelLayout.setVerticalGroup(
            tmHmTutorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tmHmTutorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tmhmsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(moveTutorsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        randomizerOptionsPane.addTab(bundle.getString("RandomizerGUI.tmHmTutorPanel.TabConstraints.tabTitle"), tmHmTutorPanel); // NOI18N

        fieldItemsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.fieldItemsPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        fieldItemsButtonGroup.add(fiUnchangedRB);
        fiUnchangedRB.setSelected(true);
        fiUnchangedRB.setText(bundle.getString("RandomizerGUI.fiUnchangedRB.text")); // NOI18N
        fiUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.fiUnchangedRB.toolTipText")); // NOI18N
        fiUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fiUnchangedRBActionPerformed(evt);
            }
        });

        fieldItemsButtonGroup.add(fiShuffleRB);
        fiShuffleRB.setText(bundle.getString("RandomizerGUI.fiShuffleRB.text")); // NOI18N
        fiShuffleRB.setToolTipText(bundle.getString("RandomizerGUI.fiShuffleRB.toolTipText")); // NOI18N
        fiShuffleRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fiShuffleRBActionPerformed(evt);
            }
        });

        fieldItemsButtonGroup.add(fiRandomRB);
        fiRandomRB.setText(bundle.getString("RandomizerGUI.fiRandomRB.text")); // NOI18N
        fiRandomRB.setToolTipText(bundle.getString("RandomizerGUI.fiRandomRB.toolTipText")); // NOI18N
        fiRandomRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fiRandomRBActionPerformed(evt);
            }
        });
        
        fieldItemsButtonGroup.add(fiRandomEvenRB);
        fiRandomEvenRB.setText(bundle.getString("RandomizerGUI.fiRandomEvenRB.text")); // NOI18N
        fiRandomEvenRB.setToolTipText(bundle.getString("RandomizerGUI.fiRandomEvenRB.toolTipText")); // NOI18N
        fiRandomEvenRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fiRandomEvenRBActionPerformed(evt);
            }
        });

        fiBanBadCB.setText(bundle.getString("RandomizerGUI.fiBanBadCB.text")); // NOI18N
        fiBanBadCB.setToolTipText(bundle.getString("RandomizerGUI.fiBanBadCB.toolTipText")); // NOI18N

        javax.swing.GroupLayout fieldItemsPanelLayout = new javax.swing.GroupLayout(fieldItemsPanel);
        fieldItemsPanel.setLayout(fieldItemsPanelLayout);
        fieldItemsPanelLayout.setHorizontalGroup(
            fieldItemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fieldItemsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fieldItemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fiUnchangedRB)
                    .addComponent(fiShuffleRB)
                    .addComponent(fiRandomRB)
                    .addGroup(fieldItemsPanelLayout.createSequentialGroup()
                        .addComponent(fiRandomEvenRB)
                        .addGap(76, 76, 76)
                        .addComponent(fiBanBadCB)))
                .addContainerGap(466, Short.MAX_VALUE))
        );
        fieldItemsPanelLayout.setVerticalGroup(
            fieldItemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fieldItemsPanelLayout.createSequentialGroup()
                .addComponent(fiUnchangedRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fiShuffleRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fiRandomRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(fieldItemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fiRandomEvenRB)
                    .addComponent(fiBanBadCB))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        shopItemsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.shopItemsPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        shopItemsButtonGroup.add(shUnchangedRB);
        shUnchangedRB.setText(bundle.getString("RandomizerGUI.shUnchangedRB.text")); // NOI18N
        shUnchangedRB.setToolTipText(bundle.getString("RandomizerGUI.shUnchangedRB.toolTipText")); // NOI18N
        shUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shUnchangedRBActionPerformed(evt);
            }
        });

        shopItemsButtonGroup.add(shShuffleRB);
        shShuffleRB.setText(bundle.getString("RandomizerGUI.shShuffleRB.text")); // NOI18N
        shShuffleRB.setToolTipText(bundle.getString("RandomizerGUI.shShuffleRB.toolTipText")); // NOI18N
        shShuffleRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shShuffleRBActionPerformed(evt);
            }
        });

        shopItemsButtonGroup.add(shRandomRB);
        shRandomRB.setText(bundle.getString("RandomizerGUI.shRandomRB.text")); // NOI18N
        shRandomRB.setToolTipText(bundle.getString("RandomizerGUI.shRandomRB.toolTipText")); // NOI18N
        shRandomRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shRandomRBActionPerformed(evt);
            }
        });

        shBanBadCB.setText(bundle.getString("RandomizerGUI.shBanBadCB.text"));
        shBanBadCB.setToolTipText(bundle.getString("RandomizerGUI.shBanBadCB.toolTipText"));

        shBanRegularShopCB.setText(bundle.getString("RandomizerGUI.shBanRegularShopCB.text"));
        shBanRegularShopCB.setToolTipText(bundle.getString("RandomizerGUI.shBanRegularShopCB.toolTipText"));

        shBanOPShopCB.setText(bundle.getString("RandomizerGUI.shBanOPShopCB.text"));
        shBanOPShopCB.setToolTipText(bundle.getString("RandomizerGUI.shBanOPShopCB.toolTipText"));

        shBalancePricesCB.setText(bundle.getString("RandomizerGUI.shBalancePricesCB.text"));
        shBalancePricesCB.setToolTipText(bundle.getString("RandomizerGUI.shBalancePricesCB.toolTipText"));
        
        shGuaranteeEvolutionItemsCB.setText(bundle.getString("RandomizerGUI.shGuaranteeEvolutionItemsCB.text"));
        shGuaranteeEvolutionItemsCB.setToolTipText(bundle.getString("RandomizerGUI.shGuaranteeEvolutionItemsCB.toolTipText"));
        
        

        javax.swing.GroupLayout shopItemsPanelLayout = new javax.swing.GroupLayout(shopItemsPanel);
        shopItemsPanel.setLayout(shopItemsPanelLayout);
        shopItemsPanelLayout.setHorizontalGroup(
                shopItemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(shopItemsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(shopItemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(shUnchangedRB)
                                        .addComponent(shShuffleRB)
                                        .addGroup(shopItemsPanelLayout.createSequentialGroup()
                                                .addComponent(shRandomRB)
                                                .addGap(76, 76, 76)
                                                .addComponent(shBanBadCB)
                                                .addComponent(shBanRegularShopCB)
                                                .addComponent(shBanOPShopCB)
                                                .addComponent(shBalancePricesCB)
                                                ))
                                .addGroup(shopItemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(shUnchangedRB)
                                        .addComponent(shShuffleRB)))
                                .addGroup(shopItemsPanelLayout.createSequentialGroup()
                                        .addGap(161, 161, 161)
                                        .addComponent(shGuaranteeEvolutionItemsCB)

                                .addContainerGap(466, Short.MAX_VALUE))
        );
        shopItemsPanelLayout.setVerticalGroup(
                shopItemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(shopItemsPanelLayout.createSequentialGroup()
                                .addComponent(shUnchangedRB)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(shShuffleRB)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(shopItemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(shRandomRB)
                                        .addComponent(shBanBadCB)
                                        .addComponent(shBanRegularShopCB)
                                        .addComponent(shBanOPShopCB)
                                        .addComponent(shBalancePricesCB)
                                        )
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(shGuaranteeEvolutionItemsCB)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
)
        );

        javax.swing.GroupLayout fieldItemsInnerPanelLayout = new javax.swing.GroupLayout(fieldItemsInnerPanel);
        fieldItemsInnerPanel.setLayout(fieldItemsInnerPanelLayout);
        fieldItemsInnerPanelLayout.setHorizontalGroup(
            fieldItemsInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fieldItemsInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fieldItemsInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fieldItemsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(shopItemsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        fieldItemsInnerPanelLayout.setVerticalGroup(
            fieldItemsInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fieldItemsInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fieldItemsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(shopItemsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(321, Short.MAX_VALUE))
        );

        randomizerOptionsPane.addTab(bundle.getString("RandomizerGUI.fieldItemsInnerPanel.TabConstraints.tabTitle"), fieldItemsInnerPanel); // NOI18N

        miscTweaksPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("RandomizerGUI.miscTweaksPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        mtNoneAvailableLabel.setText(bundle.getString("RandomizerGUI.mtNoneAvailableLabel.text")); // NOI18N

        javax.swing.GroupLayout miscTweaksPanelLayout = new javax.swing.GroupLayout(miscTweaksPanel);
        miscTweaksPanel.setLayout(miscTweaksPanelLayout);
        miscTweaksPanelLayout.setHorizontalGroup(
            miscTweaksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(miscTweaksPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mtNoneAvailableLabel)
                .addContainerGap(418, Short.MAX_VALUE))
        );
        miscTweaksPanelLayout.setVerticalGroup(
            miscTweaksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(miscTweaksPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mtNoneAvailableLabel)
                .addContainerGap(367, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout miscTweaksInnerPanelLayout = new javax.swing.GroupLayout(miscTweaksInnerPanel);
        miscTweaksInnerPanel.setLayout(miscTweaksInnerPanelLayout);
        miscTweaksInnerPanelLayout.setHorizontalGroup(
            miscTweaksInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(miscTweaksInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(miscTweaksPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        miscTweaksInnerPanelLayout.setVerticalGroup(
            miscTweaksInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(miscTweaksInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(miscTweaksPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        randomizerOptionsPane.addTab(bundle.getString("RandomizerGUI.miscTweaksInnerPanel.TabConstraints.tabTitle"), miscTweaksInnerPanel); // NOI18N

        versionLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        versionLabel.setText(bundle.getString("RandomizerGUI.versionLabel.text")); // NOI18N

        websiteLinkLabel.setText(bundle.getString("RandomizerGUI.websiteLinkLabel.text")); // NOI18N
        websiteLinkLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        websiteLinkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                websiteLinkLabelMouseClicked(evt);
            }
        });

        gameMascotLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/dabomstew/pkrandom/gui/emptyIcon.png"))); // NOI18N
        gameMascotLabel.setText(bundle.getString("RandomizerGUI.gameMascotLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(generalOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(loadQSButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(saveQSButton))
                            .addComponent(romInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addComponent(gameMascotLabel)
                        .addGap(18, 37, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(openROMButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(saveROMButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(usePresetsButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(settingsButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(versionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(websiteLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(randomizerOptionsPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(generalOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gameMascotLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(openROMButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(saveROMButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(usePresetsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(settingsButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(romInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(loadQSButton)
                            .addComponent(saveQSButton))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(versionLabel)
                    .addComponent(websiteLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(randomizerOptionsPane, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel abilitiesPanel;
    private javax.swing.JPanel baseStatsPanel;
    private javax.swing.JCheckBox brokenMovesCB;
    private javax.swing.JMenuItem customNamesEditorMenuItem;
    private javax.swing.JCheckBox fiBanBadCB;
    private javax.swing.JRadioButton fiRandomRB;
    private javax.swing.JRadioButton fiRandomEvenRB;
    private javax.swing.JRadioButton fiShuffleRB;
    private javax.swing.JRadioButton fiUnchangedRB;
    private javax.swing.ButtonGroup fieldItemsButtonGroup;
    private javax.swing.JPanel fieldItemsInnerPanel;
    private javax.swing.JPanel fieldItemsPanel;
    private javax.swing.JLabel gameMascotLabel;
    private javax.swing.JPanel generalOptionsPanel;
    private javax.swing.JCheckBox goCondenseEvosCheckBox;
    private javax.swing.JCheckBox goRemoveTradeEvosCheckBox;
    private javax.swing.JCheckBox goUpdateMovesCheckBox;
    private javax.swing.JCheckBox goUpdateMovesLegacyCheckBox;
    private javax.swing.JRadioButton igtBothRB;
    private javax.swing.JRadioButton igtGivenOnlyRB;
    private javax.swing.JCheckBox igtRandomIVsCB;
    private javax.swing.JCheckBox igtRandomItemCB;
    private javax.swing.JCheckBox igtRandomNicknameCB;
    private javax.swing.JCheckBox igtRandomOTCB;
    private javax.swing.JRadioButton igtUnchangedRB;
    private javax.swing.JPanel inGameTradesPanel;
    private javax.swing.ButtonGroup ingameTradesButtonGroup;
    private javax.swing.JButton loadQSButton;
    private javax.swing.JMenuItem manualUpdateMenuItem;
    private javax.swing.JCheckBox mdRandomAccuracyCB;
    private javax.swing.JCheckBox mdRandomCategoryCB;
    private javax.swing.JCheckBox mdRandomPPCB;
    private javax.swing.JCheckBox mdRandomPowerCB;
    private javax.swing.JCheckBox mdRandomTypeCB;
    private javax.swing.JPanel miscTweaksInnerPanel;
    private javax.swing.JPanel miscTweaksPanel;
    private javax.swing.JPanel moveDataPanel;
    private javax.swing.JPanel moveTutorsPanel;
    private javax.swing.JPanel movesAndSetsPanel;
    private javax.swing.JPanel mtCompatPanel;
    private javax.swing.ButtonGroup mtCompatibilityButtonGroup;
    private javax.swing.JCheckBox mtForceGoodDamagingCB;
    private javax.swing.JSlider mtForceGoodDamagingSlider;
    private javax.swing.JCheckBox mtKeepFieldMovesCB;
    private javax.swing.JCheckBox mtLearningSanityCB;
    private javax.swing.ButtonGroup mtMovesButtonGroup;
    private javax.swing.JPanel mtMovesPanel;
    private javax.swing.JLabel mtNoExistLabel;
    private javax.swing.JLabel mtNoneAvailableLabel;
    private javax.swing.JRadioButton mtcFullRB;
    private javax.swing.JRadioButton mtcRandomTotalRB;
    private javax.swing.JRadioButton mtcRandomTypeRB;
    private javax.swing.JRadioButton mtcUnchangedRB;
    private javax.swing.JRadioButton mtmRandomRB;
    private javax.swing.JRadioButton mtmUnchangedRB;
    private javax.swing.JButton openROMButton;
    private javax.swing.JCheckBox paBanNegativeCB;
    private javax.swing.JCheckBox paBanBadCB;
    private javax.swing.JCheckBox paBanTrappingCB;
    private javax.swing.JLabel paBansLabel;
    private javax.swing.JCheckBox paFollowEvolutionsCB;
    private javax.swing.JRadioButton paRandomizeRB;
    private javax.swing.JRadioButton paUnchangedRB;
    private javax.swing.JCheckBox paWonderGuardCB;
    private javax.swing.JRadioButton pbsChangesRandomRB;
    private javax.swing.JRadioButton pbsChangesShuffleRB;
    private javax.swing.JRadioButton pbsChangesUnchangedRB;
    private javax.swing.JCheckBox pbsFollowEvolutionsCB;
    private javax.swing.JCheckBox pbsUpdateStatsCB;
    private javax.swing.JCheckBox pbsStandardEXPCurvesCB;
    private javax.swing.JRadioButton pbsEXPCurvesLegendaryRB;
    private javax.swing.JRadioButton pbsEXPCurvesStrongLegendaryRB;
    private javax.swing.JRadioButton pbsEXPCurvesAllRB;
    private javax.swing.ButtonGroup expCurveButtonGroup;
    private javax.swing.JCheckBox peForceChangeCB;
    private javax.swing.JRadioButton peRandomRB;
    private javax.swing.JCheckBox peSameTypeCB;
    private javax.swing.JCheckBox peSimilarStrengthCB;
    private javax.swing.JCheckBox peThreeStagesCB;
    private javax.swing.JRadioButton peUnchangedRB;
    private javax.swing.JCheckBox pmsGuaranteedMovesCB;
    private javax.swing.JSlider pmsGuaranteedMovesSlider;
    private javax.swing.JCheckBox pmsForceGoodDamagingCB;
    private javax.swing.JSlider pmsForceGoodDamagingSlider;
    private javax.swing.JRadioButton pmsMetronomeOnlyRB;
    private javax.swing.JRadioButton pmsRandomTotalRB;
    private javax.swing.JRadioButton pmsRandomTypeRB;
    private javax.swing.JCheckBox pmsReorderDamagingMovesCB;
    private javax.swing.JRadioButton pmsUnchangedRB;
    private javax.swing.ButtonGroup pokeAbilitiesButtonGroup;
    private javax.swing.ButtonGroup pokeEvolutionsButtonGroup;
    private javax.swing.JButton pokeLimitBtn;
    private javax.swing.JCheckBox pokeLimitCB;
    private javax.swing.ButtonGroup pokeMovesetsButtonGroup;
    private javax.swing.ButtonGroup pokeStatChangesButtonGroup;
    private javax.swing.JPanel pokeTraitsPanel;
    private javax.swing.ButtonGroup pokeTypesButtonGroup;
    private javax.swing.JPanel pokemonEvolutionsPanel;
    private javax.swing.JPanel pokemonMovesetsPanel;
    private javax.swing.JPanel pokemonTypesPanel;
    private javax.swing.JRadioButton ptRandomFollowEvosRB;
    private javax.swing.JRadioButton ptRandomTotalRB;
    private javax.swing.JRadioButton ptUnchangedRB;
    private javax.swing.JFileChooser qsOpenChooser;
    private javax.swing.JFileChooser qsSaveChooser;
    private javax.swing.JFileChooser qsUpdateChooser;
    private javax.swing.JCheckBox raceModeCB;
    private javax.swing.JTabbedPane randomizerOptionsPane;
    private javax.swing.JLabel riRomCodeLabel;
    private javax.swing.JLabel riRomNameLabel;
    private javax.swing.JLabel riRomSupportLabel;
    private javax.swing.JPanel romInfoPanel;
    private javax.swing.JFileChooser romOpenChooser;
    private javax.swing.JFileChooser romSaveChooser;
    private javax.swing.JButton saveQSButton;
    private javax.swing.JButton saveROMButton;
    private javax.swing.JButton settingsButton;
    private javax.swing.JCheckBox shBanBadCB;
    private javax.swing.JCheckBox shBanRegularShopCB;
    private javax.swing.JCheckBox shBanOPShopCB;
    private javax.swing.JCheckBox shBalancePricesCB;
    private javax.swing.JCheckBox shGuaranteeEvolutionItemsCB;
    private javax.swing.JRadioButton shRandomRB;
    private javax.swing.JRadioButton shShuffleRB;
    private javax.swing.JRadioButton shUnchangedRB;
    private javax.swing.ButtonGroup shopItemsButtonGroup;
    private javax.swing.JPanel shopItemsPanel;
    private javax.swing.JComboBox spCustomPoke1Chooser;
    private javax.swing.JComboBox spCustomPoke2Chooser;
    private javax.swing.JComboBox spCustomPoke3Chooser;
    private javax.swing.JRadioButton spCustomRB;
    private javax.swing.JCheckBox spHeldItemsBanBadCB;
    private javax.swing.JCheckBox spHeldItemsCB;
    private javax.swing.JRadioButton spRandom2EvosRB;
    private javax.swing.JRadioButton spRandomRB;
    private javax.swing.JRadioButton spUnchangedRB;
    private javax.swing.ButtonGroup starterPokemonButtonGroup;
    private javax.swing.JPanel starterPokemonPanel;
    private javax.swing.JPanel startersInnerPanel;
    private javax.swing.ButtonGroup staticPokemonButtonGroup;
    private javax.swing.JPanel staticPokemonPanel;
    private javax.swing.JRadioButton stpRandomL4LRB;
    private javax.swing.JRadioButton stpRandomTotalRB;
    private javax.swing.JRadioButton stpRandomPower;
    private javax.swing.JRadioButton stpUnchangedRB;
    private javax.swing.JCheckBox stpLimitMusketeers;
    private javax.swing.JCheckBox stpLimit600;
    private javax.swing.JCheckBox tcnRandomizeCB;
    private javax.swing.JRadioButton thcFullRB;
    private javax.swing.JRadioButton thcRandomTotalRB;
    private javax.swing.JRadioButton thcRandomTypeRB;
    private javax.swing.JRadioButton thcUnchangedRB;
    private javax.swing.JCheckBox tmForceGoodDamagingCB;
    private javax.swing.JSlider tmForceGoodDamagingSlider;
    private javax.swing.JCheckBox tmFullHMCompatCB;
    private javax.swing.JPanel tmHmCompatPanel;
    private javax.swing.ButtonGroup tmHmCompatibilityButtonGroup;
    private javax.swing.JPanel tmHmTutorPanel;
    private javax.swing.JCheckBox tmKeepFieldMovesCB;
    private javax.swing.JCheckBox tmLearningSanityCB;
    private javax.swing.ButtonGroup tmMovesButtonGroup;
    private javax.swing.JPanel tmMovesPanel;
    private javax.swing.JPanel tmhmsPanel;
    private javax.swing.JRadioButton tmmRandomRB;
    private javax.swing.JRadioButton tmmUnchangedRB;
    private javax.swing.JCheckBox tnRandomizeCB;
    private javax.swing.JMenuItem toggleAutoUpdatesMenuItem;
    private javax.swing.JMenuItem toggleScrollPaneMenuItem;
    private javax.swing.JCheckBox tpForceFullyEvolvedCB;
    private javax.swing.JSlider tpForceFullyEvolvedSlider;
    private javax.swing.JCheckBox tpLevelModifierCB;
    private javax.swing.JSlider tpLevelModifierSlider;
    private javax.swing.JCheckBox tpNoEarlyShedinjaCB;
    private javax.swing.JRadioButton tpEnforceDistribution;
    private javax.swing.JRadioButton tpEnforceMainPlaythrough;
    private javax.swing.JCheckBox tpNoLegendariesCB;
    private javax.swing.JCheckBox tpPowerLevelsCB;
    private javax.swing.JRadioButton tpRandomRB;
    private javax.swing.JCheckBox tpRivalCarriesStarterCB;
    private javax.swing.JRadioButton tpTypeThemedRB;
    private javax.swing.JCheckBox tpTypeWeightingCB;
    private javax.swing.JRadioButton tpUnchangedRB;
    private javax.swing.ButtonGroup trainerPokesButtonGroup;
    private javax.swing.JPanel trainersInnerPanel;
    private javax.swing.JPanel trainersPokemonPanel;
    private javax.swing.JMenuItem updateOldSettingsMenuItem;
    private javax.swing.JPopupMenu updateSettingsMenu;
    private javax.swing.JButton usePresetsButton;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JLabel websiteLinkLabel;
    private javax.swing.JPanel wildPokemonARulePanel;
    private javax.swing.JPanel wildPokemonPanel;
    private javax.swing.ButtonGroup wildPokesARuleButtonGroup;
    private javax.swing.ButtonGroup wildPokesButtonGroup;
    private javax.swing.JPanel wildsInnerPanel;
    private javax.swing.JRadioButton wpARCatchEmAllRB;
    private javax.swing.JRadioButton wpARNoneRB;
    private javax.swing.JRadioButton wpARSimilarStrengthRB;
    private javax.swing.JRadioButton wpARTypeThemedRB;
    private javax.swing.JRadioButton wpArea11RB;
    private javax.swing.JCheckBox wpCatchRateCB;
    private javax.swing.JSlider wpCatchRateSlider;
    private javax.swing.JRadioButton wpGlobalRB;
    private javax.swing.JCheckBox wpHeldItemsBanBadCB;
    private javax.swing.JCheckBox wpShakingGrassCB;
    private javax.swing.JCheckBox wpHeldItemsCB;
    private javax.swing.JCheckBox wpNoLegendariesCB;
    private javax.swing.JRadioButton wpRandomRB;
    private javax.swing.JRadioButton wpUnchangedRB;
    private javax.swing.JCheckBox wpUseTimeCB;
    private javax.swing.JCheckBox wpLevelModifierCB;
    private javax.swing.JSlider wpLevelModifierSlider;
    // End of variables declaration//GEN-END:variables
    /* @formatter:on */
}
