package com.dabomstew.pkrandom.newgui;

/*----------------------------------------------------------------------------*/
/*--  NewRandomizerGUI.java - the main GUI for the randomizer, containing   --*/
/*--                          the various options available and such        --*/
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
import com.dabomstew.pkrandom.constants.GlobalConstants;
import com.dabomstew.pkrandom.exceptions.EncryptedROMException;
import com.dabomstew.pkrandom.exceptions.InvalidSupplementFilesException;
import com.dabomstew.pkrandom.exceptions.RandomizationException;
import com.dabomstew.pkrandom.pokemon.ExpCurve;
import com.dabomstew.pkrandom.pokemon.GenRestrictions;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.romhandlers.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class NewRandomizerGUI {
    private JTabbedPane tabbedPane1;
    private JCheckBox raceModeCheckBox;
    private JButton openROMButton;
    private JButton randomizeSaveButton;
    private JButton premadeSeedButton;
    private JButton settingsButton;
    private JButton loadSettingsButton;
    private JButton saveSettingsButton;
    private JPanel mainPanel;
    private JRadioButton pbsUnchangedRadioButton;
    private JRadioButton pbsShuffleRadioButton;
    private JRadioButton pbsRandomRadioButton;
    private JRadioButton pbsLegendariesSlowRadioButton;
    private JRadioButton pbsStrongLegendariesSlowRadioButton;
    private JRadioButton pbsAllMediumFastRadioButton;
    private JCheckBox pbsStandardizeEXPCurvesCheckBox;
    private JCheckBox pbsFollowEvolutionsCheckBox;
    private JCheckBox pbsUpdateBaseStatsCheckBox;
    private JRadioButton ptUnchangedRadioButton;
    private JRadioButton ptRandomFollowEvolutionsRadioButton;
    private JRadioButton ptRandomCompletelyRadioButton;
    private JRadioButton paUnchangedRadioButton;
    private JRadioButton paRandomRadioButton;
    private JCheckBox paAllowWonderGuardCheckBox;
    private JCheckBox paFollowEvolutionsCheckBox;
    private JCheckBox paTrappingAbilitiesCheckBox;
    private JCheckBox paNegativeAbilitiesCheckBox;
    private JCheckBox paBadAbilitiesCheckBox;
    private JRadioButton peUnchangedRadioButton;
    private JRadioButton peRandomRadioButton;
    private JCheckBox peSimilarStrengthCheckBox;
    private JCheckBox peSameTypingCheckBox;
    private JCheckBox peLimitEvolutionsToThreeCheckBox;
    private JCheckBox peForceChangeCheckBox;
    private JCheckBox peChangeImpossibleEvosCheckBox;
    private JCheckBox peMakeEvolutionsEasierCheckBox;
    private JRadioButton spUnchangedRadioButton;
    private JRadioButton spCustomRadioButton;
    private JRadioButton spRandomCompletelyRadioButton;
    private JRadioButton spRandomTwoEvosRadioButton;
    private JComboBox<String> spComboBox1;
    private JComboBox<String> spComboBox2;
    private JComboBox<String> spComboBox3;
    private JCheckBox spRandomizeStarterHeldItemsCheckBox;
    private JCheckBox spBanBadItemsCheckBox;
    private JRadioButton stpUnchangedRadioButton;
    private JRadioButton stpSwapLegendariesSwapStandardsRadioButton;
    private JRadioButton stpRandomCompletelyRadioButton;
    private JRadioButton stpRandomSimilarStrengthRadioButton;
    private JCheckBox stpLimitMainGameLegendariesCheckBox;
    private JCheckBox stpRandomize600BSTCheckBox;
    private JRadioButton igtUnchangedRadioButton;
    private JRadioButton igtRandomizeGivenPokemonOnlyRadioButton;
    private JRadioButton igtRandomizeBothRequestedGivenRadioButton;
    private JCheckBox igtRandomizeNicknamesCheckBox;
    private JCheckBox igtRandomizeOTsCheckBox;
    private JCheckBox igtRandomizeIVsCheckBox;
    private JCheckBox igtRandomizeItemsCheckBox;
    private JCheckBox mdRandomizeMovePowerCheckBox;
    private JCheckBox mdRandomizeMoveAccuracyCheckBox;
    private JCheckBox mdRandomizeMovePPCheckBox;
    private JCheckBox mdRandomizeMoveTypesCheckBox;
    private JCheckBox mdRandomizeMoveCategoryCheckBox;
    private JCheckBox mdUpdateMovesCheckBox;
    private JCheckBox mdLegacyCheckBox;
    private JRadioButton pmsUnchangedRadioButton;
    private JRadioButton pmsRandomPreferringSameTypeRadioButton;
    private JRadioButton pmsRandomCompletelyRadioButton;
    private JRadioButton pmsMetronomeOnlyModeRadioButton;
    private JCheckBox pmsGuaranteedLevel1MovesCheckBox;
    private JCheckBox pmsReorderDamagingMovesCheckBox;
    private JCheckBox pmsNoGameBreakingMovesCheckBox;
    private JCheckBox pmsForceGoodDamagingCheckBox;
    private JSlider pmsGuaranteedLevel1MovesSlider;
    private JSlider pmsForceGoodDamagingSlider;
    private JRadioButton tpUnchangedRadioButton;
    private JRadioButton tpRandomRadioButton;
    private JRadioButton tpRandomEvenDistributionRadioButton;
    private JRadioButton tpRandomEvenDistributionMainRadioButton;
    private JRadioButton tpTypeThemedRadioButton;
    private JCheckBox tpRivalCarriesStarterCheckBox;
    private JCheckBox tpSimilarStrengthCheckBox;
    private JCheckBox tpWeightTypesCheckBox;
    private JCheckBox tpDontUseLegendariesCheckBox;
    private JCheckBox tpNoEarlyWonderGuardCheckBox;
    private JCheckBox tpRandomizeTrainerNamesCheckBox;
    private JCheckBox tpRandomizeTrainerClassNamesCheckBox;
    private JCheckBox tpForceFullyEvolvedAtCheckBox;
    private JSlider tpForceFullyEvolvedAtSlider;
    private JSlider tpPercentageLevelModifierSlider;
    private JCheckBox tpPercentageLevelModifierCheckBox;
    private JRadioButton wpUnchangedRadioButton;
    private JRadioButton wpRandomRadioButton;
    private JRadioButton wpArea1To1RadioButton;
    private JRadioButton wpGlobal1To1RadioButton;
    private JRadioButton wpARNoneRadioButton;
    private JRadioButton wpARSimilarStrengthRadioButton;
    private JRadioButton wpARCatchEmAllModeRadioButton;
    private JRadioButton wpARTypeThemeAreasRadioButton;
    private JCheckBox wpUseTimeBasedEncountersCheckBox;
    private JCheckBox wpDontUseLegendariesCheckBox;
    private JCheckBox wpSetMinimumCatchRateCheckBox;
    private JCheckBox wpRandomizeHeldItemsCheckBox;
    private JCheckBox wpBanBadItemsCheckBox;
    private JCheckBox wpBalanceShakingGrassPokemonCheckBox;
    private JCheckBox wpPercentageLevelModifierCheckBox;
    private JSlider wpPercentageLevelModifierSlider;
    private JSlider wpSetMinimumCatchRateSlider;
    private JRadioButton tmUnchangedRadioButton;
    private JRadioButton tmRandomRadioButton;
    private JCheckBox tmFullHMCompatibilityCheckBox;
    private JCheckBox tmLevelupMoveSanityCheckBox;
    private JCheckBox tmKeepFieldMoveTMsCheckBox;
    private JCheckBox tmForceGoodDamagingCheckBox;
    private JSlider tmForceGoodDamagingSlider;
    private JRadioButton thcUnchangedRadioButton;
    private JRadioButton thcRandomPreferSameTypeRadioButton;
    private JRadioButton thcRandomCompletelyRadioButton;
    private JRadioButton thcFullCompatibilityRadioButton;
    private JRadioButton mtUnchangedRadioButton;
    private JRadioButton mtRandomRadioButton;
    private JCheckBox mtLevelupMoveSanityCheckBox;
    private JCheckBox mtKeepFieldMoveTutorsCheckBox;
    private JCheckBox mtForceGoodDamagingCheckBox;
    private JSlider mtForceGoodDamagingSlider;
    private JRadioButton mtcUnchangedRadioButton;
    private JRadioButton mtcRandomPreferSameTypeRadioButton;
    private JRadioButton mtcRandomCompletelyRadioButton;
    private JRadioButton mtcFullCompatibilityRadioButton;
    private JRadioButton fiUnchangedRadioButton;
    private JRadioButton fiShuffleRadioButton;
    private JRadioButton fiRandomRadioButton;
    private JRadioButton fiRandomEvenDistributionRadioButton;
    private JCheckBox fiBanBadItemsCheckBox;
    private JRadioButton shUnchangedRadioButton;
    private JRadioButton shShuffleRadioButton;
    private JRadioButton shRandomRadioButton;
    private JCheckBox shBanOverpoweredShopItemsCheckBox;
    private JCheckBox shBanBadItemsCheckBox;
    private JCheckBox shBanRegularShopItemsCheckBox;
    private JCheckBox shBalanceShopItemPricesCheckBox;
    private JCheckBox shGuaranteeEvolutionItemsCheckBox;
    private JCheckBox shGuaranteeXItemsCheckBox;
    private JCheckBox miscBWExpPatchCheckBox;
    private JCheckBox miscNerfXAccuracyCheckBox;
    private JCheckBox miscFixCritRateCheckBox;
    private JCheckBox miscFastestTextCheckBox;
    private JCheckBox miscRunningShoesIndoorsCheckBox;
    private JCheckBox miscRandomizePCPotionCheckBox;
    private JCheckBox miscAllowPikachuEvolutionCheckBox;
    private JCheckBox miscGiveNationalDexAtCheckBox;
    private JCheckBox miscUpdateTypeEffectivenessCheckBox;
    private JCheckBox miscRandomizeHiddenHollowsCheckBox;
    private JCheckBox miscLowerCasePokemonNamesCheckBox;
    private JCheckBox miscRandomizeCatchingTutorialCheckBox;
    private JCheckBox miscBanLuckyEggCheckBox;
    private JCheckBox miscNoFreeLuckyEggCheckBox;
    private JCheckBox miscBanBigMoneyManiacCheckBox;
    private JPanel pokemonAbilitiesPanel;
    private JPanel moveTutorPanel;
    private JPanel mtMovesPanel;
    private JPanel mtCompatPanel;
    private JLabel mtNoExistLabel;
    private JPanel shopItemsPanel;
    private JLabel mtNoneAvailableLabel;
    private JPanel miscTweaksPanel;
    private JLabel gameMascotLabel;
    private JPanel baseTweaksPanel;
    private JLabel romNameLabel;
    private JLabel romCodeLabel;
    private JLabel romSupportLabel;
    private JLabel websiteLinkLabel;
    private JCheckBox tmNoGameBreakingMovesCheckBox;
    private JCheckBox mtNoGameBreakingMovesCheckBox;
    private JCheckBox limitPokemonCheckBox;
    private JButton limitPokemonButton;
    private JCheckBox tpAllowAlternateFormesCheckBox;
    private JLabel versionLabel;
    private JCheckBox pbsFollowMegaEvosCheckBox;
    private JCheckBox paFollowMegaEvosCheckBox;
    private JCheckBox ptFollowMegaEvosCheckBox;
    private JCheckBox spAllowAltFormesCheckBox;
    private JCheckBox stpAllowAltFormesCheckBox;
    private JCheckBox stpSwapMegaEvosCheckBox;
    private JCheckBox tpSwapMegaEvosCheckBox;
    private JCheckBox wpAllowAltFormesCheckBox;
    private JCheckBox tpDoubleBattleModeCheckBox;
    private JCheckBox tpBossTrainersCheckBox;
    private JCheckBox tpImportantTrainersCheckBox;
    private JCheckBox tpRegularTrainersCheckBox;
    private JSpinner tpBossTrainersSpinner;
    private JSpinner tpImportantTrainersSpinner;
    private JSpinner tpRegularTrainersSpinner;
    private JLabel tpAdditionalPokemonForLabel;
    private JCheckBox peAllowAltFormesCheckBox;
    private JCheckBox miscSOSBattlesCheckBox;
    private JCheckBox tpRandomShinyTrainerPokemonCheckBox;
    private JRadioButton totpUnchangedRadioButton;
    private JRadioButton totpRandomRadioButton;
    private JRadioButton totpRandomSimilarStrengthRadioButton;
    private JRadioButton totpAllyUnchangedRadioButton;
    private JRadioButton totpAllyRandomRadioButton;
    private JRadioButton totpAllyRandomSimilarStrengthRadioButton;
    private JPanel totpAllyPanel;
    private JPanel totpAuraPanel;
    private JRadioButton totpAuraUnchangedRadioButton;
    private JRadioButton totpAuraRandomRadioButton;
    private JRadioButton totpAuraRandomSameStrengthRadioButton;
    private JCheckBox totpPercentageLevelModifierCheckBox;
    private JSlider totpPercentageLevelModifierSlider;
    private JCheckBox totpRandomizeHeldItemsCheckBox;
    private JCheckBox totpAllowAltFormesCheckBox;
    private JPanel totpPanel;
    private JCheckBox pmsEvolutionMovesCheckBox;
    private JComboBox<String> pbsUpdateComboBox;
    private JComboBox<String> mdUpdateComboBox;
    private JLabel wikiLinkLabel;
    private JCheckBox paWeighDuplicatesTogetherCheckBox;
    private JCheckBox miscBalanceStaticLevelsCheckBox;
    private JCheckBox miscRetainAltFormesCheckBox;
    private JComboBox pbsEXPCurveComboBox;
    private JCheckBox miscRunWithoutRunningShoesCheckBox;
    private JCheckBox peRemoveTimeBasedEvolutionsCheckBox;
    private JCheckBox tmFollowEvolutionsCheckBox;
    private JCheckBox mtFollowEvolutionsCheckBox;
    private JCheckBox stpPercentageLevelModifierCheckBox;
    private JSlider stpPercentageLevelModifierSlider;

    private static JFrame frame;

    private static String launcherInput = "";
    public static boolean usedLauncher = false;

    private GenRestrictions currentRestrictions;
    private OperationDialog opDialog;

    private ResourceBundle bundle;
    protected RomHandler.Factory[] checkHandlers;
    private RomHandler romHandler;

    private boolean presetMode = false;
    private boolean initialPopup = true;

    private List<JCheckBox> tweakCheckBoxes;
    private JPanel liveTweaksPanel = new JPanel();

    private JFileChooser romOpenChooser = new JFileChooser();
    private JFileChooser romSaveChooser = new JFileChooser();
    private JFileChooser qsOpenChooser = new JFileChooser();
    private JFileChooser qsSaveChooser = new JFileChooser();
    private JFileChooser qsUpdateChooser = new JFileChooser();
    private JFileChooser gameUpdateChooser = new JFileChooser();

    private JPopupMenu settingsMenu;
    private JMenuItem customNamesEditorMenuItem;
    private JMenuItem applyGameUpdateMenuItem;
    private JMenuItem removeGameUpdateMenuItem;
    private JMenuItem loadGetSettingsMenuItem;
    private JMenuItem keepOrUnloadGameAfterRandomizingMenuItem;

    private ImageIcon emptyIcon = new ImageIcon(getClass().getResource("/com/dabomstew/pkrandom/newgui/emptyIcon.png"));
    private boolean haveCheckedCustomNames, unloadGameOnSuccess;
    private Map<String, String> gameUpdates = new TreeMap<>();

    public NewRandomizerGUI() {
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        bundle = ResourceBundle.getBundle("com/dabomstew/pkrandom/newgui/Bundle");
        testForRequiredConfigs();
        checkHandlers = new RomHandler.Factory[] { new Gen1RomHandler.Factory(), new Gen2RomHandler.Factory(),
                new Gen3RomHandler.Factory(), new Gen4RomHandler.Factory(), new Gen5RomHandler.Factory(),
                new Gen6RomHandler.Factory(), new Gen7RomHandler.Factory() };

        haveCheckedCustomNames = false;
        attemptReadConfig();
        initExplicit();
        initTweaksPanel();
        initFileChooserDirectories();

        boolean canWrite = attemptWriteConfig();
        if (!canWrite) {
            JOptionPane.showMessageDialog(null, bundle.getString("GUI.cantWriteConfigFile"));
        }

        if (!haveCheckedCustomNames) {
            checkCustomNames();
        }

        new Thread(() -> {
            String latestVersionString = "???";

            try {

                URL url = new URL(SysConstants.API_URL_ZX);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                String output;
                while ((output = br.readLine()) != null) {
                    String[] a = output.split("tag_name\":\"");
                    if (a.length > 1) {
                        latestVersionString = a[1].split("\",")[0];
                    }
                }

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

            // If the release version is newer than this version, bold it to make it more obvious.
            if (Version.isReleaseVersionNewer(latestVersionString)) {
                latestVersionString = String.format("<b>%s</b>", latestVersionString);
            }
            String finalLatestVersionString = latestVersionString;
            SwingUtilities.invokeLater(() -> {
                websiteLinkLabel.setText(String.format(bundle.getString("GUI.websiteLinkLabel.text"), finalLatestVersionString));
            });
        }).run();

        frame.setTitle(String.format(bundle.getString("GUI.windowTitle"),Version.VERSION_STRING));

        openROMButton.addActionListener(e -> loadROM());
        pbsUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pbsShuffleRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pbsRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pbsStandardizeEXPCurvesCheckBox.addActionListener(e -> enableOrDisableSubControls());
        paUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        paRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        peUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        peRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        spUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        spCustomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        spRandomCompletelyRadioButton.addActionListener(e -> enableOrDisableSubControls());
        spRandomTwoEvosRadioButton.addActionListener(e -> enableOrDisableSubControls());
        stpUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        stpSwapLegendariesSwapStandardsRadioButton.addActionListener(e -> enableOrDisableSubControls());
        stpRandomCompletelyRadioButton.addActionListener(e -> enableOrDisableSubControls());
        stpRandomSimilarStrengthRadioButton.addActionListener(e -> enableOrDisableSubControls());
        stpPercentageLevelModifierCheckBox.addActionListener(e -> enableOrDisableSubControls());
        igtUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        igtRandomizeGivenPokemonOnlyRadioButton.addActionListener(e -> enableOrDisableSubControls());
        igtRandomizeBothRequestedGivenRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pmsUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pmsRandomPreferringSameTypeRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pmsRandomCompletelyRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pmsMetronomeOnlyModeRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pmsGuaranteedLevel1MovesCheckBox.addActionListener(e -> enableOrDisableSubControls());
        pmsForceGoodDamagingCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tpUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        tpRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        tpTypeThemedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        tpRandomEvenDistributionRadioButton.addActionListener(e -> enableOrDisableSubControls());
        tpRandomEvenDistributionMainRadioButton.addActionListener(e -> enableOrDisableSubControls());
        tpForceFullyEvolvedAtCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tpPercentageLevelModifierCheckBox.addActionListener(e -> enableOrDisableSubControls());
        wpUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        wpRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        wpArea1To1RadioButton.addActionListener(e -> enableOrDisableSubControls());
        wpGlobal1To1RadioButton.addActionListener(e -> enableOrDisableSubControls());
        wpSetMinimumCatchRateCheckBox.addActionListener(e -> enableOrDisableSubControls());
        wpRandomizeHeldItemsCheckBox.addActionListener(e -> enableOrDisableSubControls());
        wpPercentageLevelModifierCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tmUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        tmRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        tmForceGoodDamagingCheckBox.addActionListener(e -> enableOrDisableSubControls());
        thcUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        thcRandomPreferSameTypeRadioButton.addActionListener(e -> enableOrDisableSubControls());
        thcRandomCompletelyRadioButton.addActionListener(e -> enableOrDisableSubControls());
        thcFullCompatibilityRadioButton.addActionListener(e -> enableOrDisableSubControls());
        mtUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        mtRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        mtForceGoodDamagingCheckBox.addActionListener(e -> enableOrDisableSubControls());
        mtcUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        mtcRandomPreferSameTypeRadioButton.addActionListener(e -> enableOrDisableSubControls());
        mtcRandomCompletelyRadioButton.addActionListener(e -> enableOrDisableSubControls());
        mtcFullCompatibilityRadioButton.addActionListener(e -> enableOrDisableSubControls());
        fiUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        fiShuffleRadioButton.addActionListener(e -> enableOrDisableSubControls());
        fiRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        fiRandomEvenDistributionRadioButton.addActionListener(e -> enableOrDisableSubControls());
        shUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        shShuffleRadioButton.addActionListener(e -> enableOrDisableSubControls());
        shRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        websiteLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI(SysConstants.WEBSITE_URL_ZX));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        wikiLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI(SysConstants.WIKI_URL_ZX));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        randomizeSaveButton.addActionListener(e -> saveROM());
        premadeSeedButton.addActionListener(e -> presetLoader());
        loadSettingsButton.addActionListener(e -> loadQS());
        saveSettingsButton.addActionListener(e -> saveQS());
        settingsButton.addActionListener(e -> settingsMenu.show(settingsButton,0,settingsButton.getHeight()));
        customNamesEditorMenuItem.addActionListener(e -> new CustomNamesEditorDialog(frame));
        applyGameUpdateMenuItem.addActionListener(e -> applyGameUpdateMenuItemActionPerformed());
        removeGameUpdateMenuItem.addActionListener(e -> removeGameUpdateMenuItemActionPerformed());
        loadGetSettingsMenuItem.addActionListener(e -> loadGetSettingsMenuItemActionPerformed());
        keepOrUnloadGameAfterRandomizingMenuItem.addActionListener(e -> keepOrUnloadGameAfterRandomizingMenuItemActionPerformed());
        limitPokemonButton.addActionListener(e -> {
            NewGenerationLimitDialog gld = new NewGenerationLimitDialog(frame, currentRestrictions,
                    romHandler.generationOfPokemon(), romHandler.forceSwapStaticMegaEvos());
            if (gld.pressedOK()) {
                currentRestrictions = gld.getChoice();
                if (currentRestrictions != null && !currentRestrictions.megaEvolutionsAreInPool(romHandler.forceSwapStaticMegaEvos())) {
                    tpSwapMegaEvosCheckBox.setEnabled(false);
                    tpSwapMegaEvosCheckBox.setSelected(false);
                }
            }
        });
        limitPokemonCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tpAllowAlternateFormesCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tpBossTrainersCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tpImportantTrainersCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tpRegularTrainersCheckBox.addActionListener(e -> enableOrDisableSubControls());
        totpUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        totpRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        totpRandomSimilarStrengthRadioButton.addActionListener(e -> enableOrDisableSubControls());
        totpAllyUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        totpAllyRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        totpAllyRandomSimilarStrengthRadioButton.addActionListener(e -> enableOrDisableSubControls());
        totpPercentageLevelModifierCheckBox.addActionListener(e -> enableOrDisableSubControls());
        pbsUpdateBaseStatsCheckBox.addActionListener(e -> enableOrDisableSubControls());
        mdUpdateMovesCheckBox.addActionListener(e -> enableOrDisableSubControls());
        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {

            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {
                showInitialPopup();
            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
        ptUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        ptRandomFollowEvolutionsRadioButton.addActionListener(e -> enableOrDisableSubControls());
        ptRandomCompletelyRadioButton.addActionListener(e -> enableOrDisableSubControls());
        spRandomizeStarterHeldItemsCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tmLevelupMoveSanityCheckBox.addActionListener(e -> enableOrDisableSubControls());
        mtLevelupMoveSanityCheckBox.addActionListener(e -> enableOrDisableSubControls());
    }

    private void showInitialPopup() {
        if (!usedLauncher) {
            String message = bundle.getString("GUI.pleaseUseTheLauncher");
            Object[] messages = {message};
            JOptionPane.showMessageDialog(frame, messages);
        }
        if (initialPopup) {
            String message = String.format(bundle.getString("GUI.firstStart"),Version.VERSION_STRING);
            JLabel label = new JLabel("<html><a href=\"https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/Important-Information\">Checking out the \"Important Information\" page on the Wiki is highly recommended.</a>");
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.browse(new URI("https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/Important-Information"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            Object[] messages = {message,label};
            JOptionPane.showMessageDialog(frame, messages);
            initialPopup = false;
            attemptWriteConfig();
        }
    }

    private void initFileChooserDirectories() {
        romOpenChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
        romSaveChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
        if (new File(SysConstants.ROOT_PATH + "settings/").exists()) {
            qsOpenChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH + "settings/"));
            qsSaveChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH + "settings/"));
            qsUpdateChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH + "settings/"));
        } else {
            qsOpenChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
            qsSaveChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
            qsUpdateChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
        }
    }

    private void initExplicit() {

        versionLabel.setText(String.format(bundle.getString("GUI.versionLabel.text"), Version.VERSION_STRING));
        mtNoExistLabel.setVisible(false);
        mtNoneAvailableLabel.setVisible(false);
        baseTweaksPanel.add(liveTweaksPanel);
        liveTweaksPanel.setVisible(false);
        websiteLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        wikiLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        romOpenChooser.setFileFilter(new ROMFilter());

        romSaveChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        romSaveChooser.setFileFilter(new ROMFilter());

        qsOpenChooser.setFileFilter(new QSFileFilter());

        qsSaveChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        qsSaveChooser.setFileFilter(new QSFileFilter());

        qsUpdateChooser.setFileFilter(new QSFileFilter());

        settingsMenu = new JPopupMenu();

        SpinnerModel bossTrainerModel = new SpinnerNumberModel(
                1,
                1,
                5,
                1
        );
        SpinnerModel importantTrainerModel = new SpinnerNumberModel(
                1,
                1,
                5,
                1
        );
        SpinnerModel regularTrainerModel = new SpinnerNumberModel(
                1,
                1,
                5,
                1
        );

        tpBossTrainersSpinner.setModel(bossTrainerModel);
        tpImportantTrainersSpinner.setModel(importantTrainerModel);
        tpRegularTrainersSpinner.setModel(regularTrainerModel);

        customNamesEditorMenuItem = new JMenuItem();
        customNamesEditorMenuItem.setText(bundle.getString("GUI.customNamesEditorMenuItem.text"));
        settingsMenu.add(customNamesEditorMenuItem);

        loadGetSettingsMenuItem = new JMenuItem();
        loadGetSettingsMenuItem.setText(bundle.getString("GUI.loadGetSettingsMenuItem.text"));
        settingsMenu.add(loadGetSettingsMenuItem);

        applyGameUpdateMenuItem = new JMenuItem();
        applyGameUpdateMenuItem.setText(bundle.getString("GUI.applyGameUpdateMenuItem.text"));
        settingsMenu.add(applyGameUpdateMenuItem);

        removeGameUpdateMenuItem = new JMenuItem();
        removeGameUpdateMenuItem.setText(bundle.getString("GUI.removeGameUpdateMenuItem.text"));
        settingsMenu.add(removeGameUpdateMenuItem);

        keepOrUnloadGameAfterRandomizingMenuItem = new JMenuItem();
        if (this.unloadGameOnSuccess) {
            keepOrUnloadGameAfterRandomizingMenuItem.setText(bundle.getString("GUI.keepGameLoadedAfterRandomizingMenuItem.text"));
        } else {
            keepOrUnloadGameAfterRandomizingMenuItem.setText(bundle.getString("GUI.unloadGameAfterRandomizingMenuItem.text"));
        }
        settingsMenu.add(keepOrUnloadGameAfterRandomizingMenuItem);
    }

    private void loadROM() {
        romOpenChooser.setSelectedFile(null);
        int returnVal = romOpenChooser.showOpenDialog(mainPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File fh = romOpenChooser.getSelectedFile();
            try {
                Utils.validateRomFile(fh);
            } catch (Utils.InvalidROMException e) {
                switch (e.getType()) {
                    case LENGTH:
                        JOptionPane.showMessageDialog(mainPanel,
                                String.format(bundle.getString("GUI.tooShortToBeARom"), fh.getName()));
                        return;
                    case ZIP_FILE:
                        JOptionPane.showMessageDialog(mainPanel,
                                String.format(bundle.getString("GUI.openedZIPfile"), fh.getName()));
                        return;
                    case RAR_FILE:
                        JOptionPane.showMessageDialog(mainPanel,
                                String.format(bundle.getString("GUI.openedRARfile"), fh.getName()));
                        return;
                    case IPS_FILE:
                        JOptionPane.showMessageDialog(mainPanel,
                                String.format(bundle.getString("GUI.openedIPSfile"), fh.getName()));
                        return;
                    case UNREADABLE:
                        JOptionPane.showMessageDialog(mainPanel,
                                String.format(bundle.getString("GUI.unreadableRom"), fh.getName()));
                        return;
                }
            }

            for (RomHandler.Factory rhf : checkHandlers) {
                if (rhf.isLoadable(fh.getAbsolutePath())) {
                    this.romHandler = rhf.create(RandomSource.instance());
                    if (!usedLauncher && this.romHandler instanceof Abstract3DSRomHandler) {
                        String message = bundle.getString("GUI.pleaseUseTheLauncher");
                        Object[] messages = {message};
                        JOptionPane.showMessageDialog(frame, messages);
                        this.romHandler = null;
                        return;
                    }
                    opDialog = new OperationDialog(bundle.getString("GUI.loadingText"), frame, true);
                    Thread t = new Thread(() -> {
                        boolean romLoaded = false;
                        SwingUtilities.invokeLater(() -> opDialog.setVisible(true));
                        try {
                            this.romHandler.loadRom(fh.getAbsolutePath());
                            if (gameUpdates.containsKey(this.romHandler.getROMCode())) {
                                this.romHandler.loadGameUpdate(gameUpdates.get(this.romHandler.getROMCode()));
                            }
                            romLoaded = true;
                        } catch (EncryptedROMException ex) {
                            JOptionPane.showMessageDialog(mainPanel,
                                    String.format(bundle.getString("GUI.encryptedRom"), fh.getAbsolutePath()));
                        } catch (Exception ex) {
                            attemptToLogException(ex, "GUI.loadFailed", "GUI.loadFailedNoLog", null, null);
                        }
                        final boolean loadSuccess = romLoaded;
                        SwingUtilities.invokeLater(() -> {
                            this.opDialog.setVisible(false);
                            this.initialState();
                            if (loadSuccess) {
                                this.romLoaded();
                            }
                        });
                    });
                    t.start();

                    return;
                }
            }
            JOptionPane.showMessageDialog(mainPanel,
                    String.format(bundle.getString("GUI.unsupportedRom"), fh.getName()));
        }
    }

    private void saveROM() {
        if (romHandler == null) {
            return; // none loaded
        }
        if (raceModeCheckBox.isSelected() && tpUnchangedRadioButton.isSelected() && wpUnchangedRadioButton.isSelected()) {
            JOptionPane.showMessageDialog(frame, bundle.getString("GUI.raceModeRequirements"));
            return;
        }
        if (limitPokemonCheckBox.isSelected()
                && (this.currentRestrictions == null || this.currentRestrictions.nothingSelected())) {
            JOptionPane.showMessageDialog(frame, bundle.getString("GUI.pokeLimitNotChosen"));
            return;
        }
        SaveType outputType = askForSaveType();
        romSaveChooser.setSelectedFile(null);
        boolean allowed = false;
        File fh = null;
        if (outputType == SaveType.FILE) {
            romSaveChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int returnVal = romSaveChooser.showSaveDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fh = romSaveChooser.getSelectedFile();
                // Fix or add extension
                List<String> extensions = new ArrayList<>(Arrays.asList("sgb", "gbc", "gba", "nds", "cxi"));
                extensions.remove(this.romHandler.getDefaultExtension());
                fh = FileFunctions.fixFilename(fh, this.romHandler.getDefaultExtension(), extensions);
                allowed = true;
                if (this.romHandler instanceof AbstractDSRomHandler || this.romHandler instanceof Abstract3DSRomHandler) {
                    String currentFN = this.romHandler.loadedFilename();
                    if (currentFN.equals(fh.getAbsolutePath())) {
                        JOptionPane.showMessageDialog(frame, bundle.getString("GUI.cantOverwriteDS"));
                        allowed = false;
                    }
                }
            }
        } else if (outputType == SaveType.DIRECTORY) {
            romSaveChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = romSaveChooser.showSaveDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fh = romSaveChooser.getSelectedFile();
                allowed = true;
            }
        }

        if (allowed && fh != null) {
            // Get a seed
            long seed = RandomSource.pickSeed();
            // Apply it
            RandomSource.seed(seed);
            presetMode = false;

            try {
                CustomNamesSet cns = FileFunctions.getCustomNames();
                performRandomization(fh.getAbsolutePath(), seed, cns, outputType == SaveType.DIRECTORY);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, bundle.getString("GUI.cantLoadCustomNames"));
            }

        }
    }

    private void loadQS() {
        if (this.romHandler == null) {
            return;
        }
        qsOpenChooser.setSelectedFile(null);
        int returnVal = qsOpenChooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fh = qsOpenChooser.getSelectedFile();
            try {
                FileInputStream fis = new FileInputStream(fh);
                Settings settings = Settings.read(fis);
                fis.close();

                SwingUtilities.invokeLater(() -> {
                    // load settings
                    initialState();
                    romLoaded();
                    Settings.TweakForROMFeedback feedback = settings.tweakForRom(this.romHandler);
                    if (feedback.isChangedStarter() && settings.getStartersMod() == Settings.StartersMod.CUSTOM) {
                        JOptionPane.showMessageDialog(frame, bundle.getString("GUI.starterUnavailable"));
                    }
                    this.restoreStateFromSettings(settings);

                    if (settings.isUpdatedFromOldVersion()) {
                        // show a warning dialog, but load it
                        JOptionPane.showMessageDialog(frame, bundle.getString("GUI.settingsFileOlder"));
                    }

                    JOptionPane.showMessageDialog(frame,
                            String.format(bundle.getString("GUI.settingsLoaded"), fh.getName()));
                });
            } catch (UnsupportedOperationException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, bundle.getString("GUI.invalidSettingsFile"));
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, bundle.getString("GUI.settingsLoadFailed"));
            }
        }
    }

    private void saveQS() {
        if (this.romHandler == null) {
            return;
        }
        qsSaveChooser.setSelectedFile(null);
        int returnVal = qsSaveChooser.showSaveDialog(frame);
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
                JOptionPane.showMessageDialog(frame, bundle.getString("GUI.settingsSaveFailed"));
            }
        }
    }

    private void performRandomization(final String filename, final long seed, CustomNamesSet customNames, boolean saveAsDirectory) {
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
            opDialog = new OperationDialog(bundle.getString("GUI.savingText"), frame, true);
            Thread t = new Thread(() -> {
                SwingUtilities.invokeLater(() -> opDialog.setVisible(true));
                boolean succeededSave = false;
                try {
                    romHandler.setLog(verboseLog);
                    finishedCV.set(new Randomizer(settings, romHandler, saveAsDirectory).randomize(filename,
                            verboseLog, seed));
                    succeededSave = true;
                } catch (RandomizationException ex) {
                    attemptToLogException(ex, "GUI.saveFailedMessage",
                            "GUI.saveFailedMessageNoLog", true, settings.toString(), Long.toString(seed));
                    if (verboseLog != null) {
                        verboseLog.close();
                    }
                } catch (Exception ex) {
                    attemptToLogException(ex, "GUI.saveFailedIO", "GUI.saveFailedIONoLog", settings.toString(), Long.toString(seed));
                    if (verboseLog != null) {
                        verboseLog.close();
                    }
                }
                if (succeededSave) {
                    SwingUtilities.invokeLater(() -> {
                        opDialog.setVisible(false);
                        // Log?
                        verboseLog.close();
                        byte[] out = baos.toByteArray();

                        if (raceMode) {
                            JOptionPane.showMessageDialog(frame,
                                    String.format(bundle.getString("GUI.raceModeCheckValuePopup"),
                                            finishedCV.get()));
                        } else {
                            int response = JOptionPane.showConfirmDialog(frame,
                                    bundle.getString("GUI.saveLogDialog.text"),
                                    bundle.getString("GUI.saveLogDialog.title"),
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
                                    JOptionPane.showMessageDialog(frame,
                                            bundle.getString("GUI.logSaveFailed"));
                                    return;
                                }
                                JOptionPane.showMessageDialog(frame,
                                        String.format(bundle.getString("GUI.logSaved"), filename));
                            }
                        }
                        if (presetMode) {
                            JOptionPane.showMessageDialog(frame,
                                    bundle.getString("GUI.randomizationDone"));
                            // Done
                            if (this.unloadGameOnSuccess) {
                                romHandler = null;
                                initialState();
                            } else {
                                reinitializeRomHandler();
                            }
                        } else {
                            // Compile a config string
                            try {
                                String configString = getCurrentSettings().toString();
                                // Show the preset maker
                                new PresetMakeDialog(frame, seed, configString);
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(frame,
                                        bundle.getString("GUI.cantLoadCustomNames"));
                            }

                            // Done
                            if (this.unloadGameOnSuccess) {
                                romHandler = null;
                                initialState();
                            } else {
                                reinitializeRomHandler();
                            }
                        }
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        opDialog.setVisible(false);
                        romHandler = null;
                        initialState();
                    });
                }
            });
            t.start();
        } catch (Exception ex) {
            attemptToLogException(ex, "GUI.saveFailed", "GUI.saveFailedNoLog", settings.toString(), Long.toString(seed));
            if (verboseLog != null) {
                verboseLog.close();
            }
        }
    }

    private void presetLoader() {
        PresetLoadDialog pld = new PresetLoadDialog(this,frame);
        if (pld.isCompleted()) {
            // Apply it
            long seed = pld.getSeed();
            String config = pld.getConfigString();
            this.romHandler = pld.getROM();
            if (gameUpdates.containsKey(this.romHandler.getROMCode())) {
                this.romHandler.loadGameUpdate(gameUpdates.get(this.romHandler.getROMCode()));
            }
            this.romLoaded();
            Settings settings;
            try {
                settings = Settings.fromString(config);
                settings.tweakForRom(this.romHandler);
                this.restoreStateFromSettings(settings);
            } catch (UnsupportedEncodingException | IllegalArgumentException e) {
                // settings load failed
                e.printStackTrace();
                this.romHandler = null;
                initialState();
            }
            SaveType outputType = askForSaveType();
            romSaveChooser.setSelectedFile(null);
            boolean allowed = false;
            File fh = null;
            if (outputType == SaveType.FILE) {
                romSaveChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = romSaveChooser.showSaveDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fh = romSaveChooser.getSelectedFile();
                    // Fix or add extension
                    List<String> extensions = new ArrayList<>(Arrays.asList("sgb", "gbc", "gba", "nds", "cxi"));
                    extensions.remove(this.romHandler.getDefaultExtension());
                    fh = FileFunctions.fixFilename(fh, this.romHandler.getDefaultExtension(), extensions);
                    allowed = true;
                    if (this.romHandler instanceof AbstractDSRomHandler || this.romHandler instanceof Abstract3DSRomHandler) {
                        String currentFN = this.romHandler.loadedFilename();
                        if (currentFN.equals(fh.getAbsolutePath())) {
                            JOptionPane.showMessageDialog(frame, bundle.getString("GUI.cantOverwriteDS"));
                            allowed = false;
                        }
                    }
                } else {
                    this.romHandler = null;
                    initialState();
                }
            } else if (outputType == SaveType.DIRECTORY) {
                romSaveChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = romSaveChooser.showSaveDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fh = romSaveChooser.getSelectedFile();
                    allowed = true;
                } else {
                    this.romHandler = null;
                    initialState();
                }
            }

            if (allowed && fh != null) {
                // Apply the seed we were given
                RandomSource.seed(seed);
                presetMode = true;
                performRandomization(fh.getAbsolutePath(), seed, pld.getCustomNames(), outputType == SaveType.DIRECTORY);
            }
        }

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setEnabled(true);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(panel1, gbc);
        panel1.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.generalOptionsPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel1.getFont()), new Color(-16777216)));
        raceModeCheckBox = new JCheckBox();
        raceModeCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(raceModeCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.raceModeCheckBox.text"));
        raceModeCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.raceModeCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 30);
        panel1.add(raceModeCheckBox, gbc);
        limitPokemonCheckBox = new JCheckBox();
        limitPokemonCheckBox.setEnabled(false);
        limitPokemonCheckBox.setText("");
        limitPokemonCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.limitPokemonCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 0, 0);
        panel1.add(limitPokemonCheckBox, gbc);
        limitPokemonButton = new JButton();
        limitPokemonButton.setEnabled(false);
        this.$$$loadButtonText$$$(limitPokemonButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.limitPokemonCheckBox.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 0, 0);
        panel1.add(limitPokemonButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.gridheight = 3;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(panel2, gbc);
        panel2.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.romInformationPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel2.getFont()), null));
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer2, gbc);
        romNameLabel = new JLabel();
        romNameLabel.setHorizontalAlignment(0);
        this.$$$loadLabelText$$$(romNameLabel, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.noRomLoaded"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 5, 5, 0);
        panel2.add(romNameLabel, gbc);
        romCodeLabel = new JLabel();
        romCodeLabel.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 0);
        panel2.add(romCodeLabel, gbc);
        romSupportLabel = new JLabel();
        romSupportLabel.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 0);
        panel2.add(romSupportLabel, gbc);
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setTabLayoutPolicy(1);
        tabbedPane1.setTabPlacement(1);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 11;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(tabbedPane1, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        tabbedPane1.addTab(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pokemonTraitsPanel.title"), panel3);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 0, 0);
        panel3.add(panel4, gbc);
        panel4.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel4.getFont()), null));
        pbsUnchangedRadioButton = new JRadioButton();
        pbsUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(pbsUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsUnchangedRadioButton.text"));
        pbsUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(pbsUnchangedRadioButton, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(spacer3, gbc);
        pbsShuffleRadioButton = new JRadioButton();
        pbsShuffleRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(pbsShuffleRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsShuffleRadioButton.text"));
        pbsShuffleRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsShuffleRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(pbsShuffleRadioButton, gbc);
        pbsRandomRadioButton = new JRadioButton();
        pbsRandomRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(pbsRandomRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsRandomRadioButton.text"));
        pbsRandomRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsRandomRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(pbsRandomRadioButton, gbc);
        pbsLegendariesSlowRadioButton = new JRadioButton();
        pbsLegendariesSlowRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(pbsLegendariesSlowRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsLegendariesSlowRadioButton.text"));
        pbsLegendariesSlowRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsLegendariesSlowRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(pbsLegendariesSlowRadioButton, gbc);
        pbsStrongLegendariesSlowRadioButton = new JRadioButton();
        pbsStrongLegendariesSlowRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(pbsStrongLegendariesSlowRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsStrongLegendariesSlowRadioButton.text"));
        pbsStrongLegendariesSlowRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsStrongLegendariesSlowRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(pbsStrongLegendariesSlowRadioButton, gbc);
        pbsAllMediumFastRadioButton = new JRadioButton();
        pbsAllMediumFastRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(pbsAllMediumFastRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsAllMediumFastRadioButton.text"));
        pbsAllMediumFastRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsAllMediumFastRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(pbsAllMediumFastRadioButton, gbc);
        pbsStandardizeEXPCurvesCheckBox = new JCheckBox();
        pbsStandardizeEXPCurvesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(pbsStandardizeEXPCurvesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsStandardizeEXPCurvesCheckBox.text"));
        pbsStandardizeEXPCurvesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsStandardizeEXPCurvesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(pbsStandardizeEXPCurvesCheckBox, gbc);
        pbsFollowEvolutionsCheckBox = new JCheckBox();
        pbsFollowEvolutionsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(pbsFollowEvolutionsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsFollowEvolutionsCheckBox.text"));
        pbsFollowEvolutionsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsFollowEvolutionsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.6;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(pbsFollowEvolutionsCheckBox, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel4.add(spacer5, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel4.add(spacer6, gbc);
        pbsUpdateBaseStatsCheckBox = new JCheckBox();
        pbsUpdateBaseStatsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(pbsUpdateBaseStatsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsUpdateBaseStatsCheckBox.text"));
        pbsUpdateBaseStatsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsUpdateBaseStatsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(pbsUpdateBaseStatsCheckBox, gbc);
        pbsFollowMegaEvosCheckBox = new JCheckBox();
        pbsFollowMegaEvosCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(pbsFollowMegaEvosCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsFollowMegaEvosCheckBox.text"));
        pbsFollowMegaEvosCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pbsFollowMegaEvosCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(pbsFollowMegaEvosCheckBox, gbc);
        pbsUpdateComboBox = new JComboBox();
        pbsUpdateComboBox.setEnabled(false);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("--");
        pbsUpdateComboBox.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel4.add(pbsUpdateComboBox, gbc);
        pbsEXPCurveComboBox = new JComboBox();
        pbsEXPCurveComboBox.setEnabled(false);
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("Medium Fast");
        pbsEXPCurveComboBox.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 20, 0, 5);
        panel4.add(pbsEXPCurveComboBox, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(spacer7, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel3.add(spacer8, gbc);
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(spacer9, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel5, gbc);
        panel5.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.ptPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel5.getFont()), null));
        ptUnchangedRadioButton = new JRadioButton();
        ptUnchangedRadioButton.setEnabled(false);
        ptUnchangedRadioButton.setHideActionText(false);
        this.$$$loadButtonText$$$(ptUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.ptUnchangedRadioButton.text"));
        ptUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.ptUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(ptUnchangedRadioButton, gbc);
        final JPanel spacer10 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(spacer10, gbc);
        final JPanel spacer11 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel5.add(spacer11, gbc);
        final JPanel spacer12 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(spacer12, gbc);
        final JPanel spacer13 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel5.add(spacer13, gbc);
        ptRandomFollowEvolutionsRadioButton = new JRadioButton();
        ptRandomFollowEvolutionsRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(ptRandomFollowEvolutionsRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.ptRandomFollowEvolutionsRadioButton.text"));
        ptRandomFollowEvolutionsRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.ptRandomFollowEvolutionsRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(ptRandomFollowEvolutionsRadioButton, gbc);
        ptRandomCompletelyRadioButton = new JRadioButton();
        ptRandomCompletelyRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(ptRandomCompletelyRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.ptRandomCompletelyRadioButton.text"));
        ptRandomCompletelyRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.ptRandomCompletelyRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(ptRandomCompletelyRadioButton, gbc);
        ptFollowMegaEvosCheckBox = new JCheckBox();
        ptFollowMegaEvosCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(ptFollowMegaEvosCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.ptFollowMegaEvosCheckBox.text"));
        ptFollowMegaEvosCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.ptFollowMegaEvosCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(ptFollowMegaEvosCheckBox, gbc);
        pokemonAbilitiesPanel = new JPanel();
        pokemonAbilitiesPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0.9;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(pokemonAbilitiesPanel, gbc);
        pokemonAbilitiesPanel.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, pokemonAbilitiesPanel.getFont()), null));
        paUnchangedRadioButton = new JRadioButton();
        paUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(paUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paUnchangedRadioButton.text"));
        paUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pokemonAbilitiesPanel.add(paUnchangedRadioButton, gbc);
        final JPanel spacer14 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pokemonAbilitiesPanel.add(spacer14, gbc);
        final JPanel spacer15 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.VERTICAL;
        pokemonAbilitiesPanel.add(spacer15, gbc);
        final JPanel spacer16 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pokemonAbilitiesPanel.add(spacer16, gbc);
        final JPanel spacer17 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        pokemonAbilitiesPanel.add(spacer17, gbc);
        paRandomRadioButton = new JRadioButton();
        paRandomRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(paRandomRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paRandomRadioButton.text"));
        paRandomRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paRandomRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        pokemonAbilitiesPanel.add(paRandomRadioButton, gbc);
        paAllowWonderGuardCheckBox = new JCheckBox();
        paAllowWonderGuardCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(paAllowWonderGuardCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paAllowWonderGuardCheckBox.text"));
        paAllowWonderGuardCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paAllowWonderGuardCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        pokemonAbilitiesPanel.add(paAllowWonderGuardCheckBox, gbc);
        paFollowEvolutionsCheckBox = new JCheckBox();
        paFollowEvolutionsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(paFollowEvolutionsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paFollowEvolutionsCheckBox.text"));
        paFollowEvolutionsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paFollowEvolutionsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        pokemonAbilitiesPanel.add(paFollowEvolutionsCheckBox, gbc);
        final JLabel label1 = new JLabel();
        this.$$$loadLabelText$$$(label1, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paBanLabel.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 0, 0, 0);
        pokemonAbilitiesPanel.add(label1, gbc);
        paTrappingAbilitiesCheckBox = new JCheckBox();
        paTrappingAbilitiesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(paTrappingAbilitiesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paTrappingAbilitiesCheckBox.text"));
        paTrappingAbilitiesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paTrappingAbilitiesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        pokemonAbilitiesPanel.add(paTrappingAbilitiesCheckBox, gbc);
        paNegativeAbilitiesCheckBox = new JCheckBox();
        paNegativeAbilitiesCheckBox.setEnabled(false);
        paNegativeAbilitiesCheckBox.setSelected(false);
        this.$$$loadButtonText$$$(paNegativeAbilitiesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paNegativeAbilitiesCheckBox.text"));
        paNegativeAbilitiesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paNegativeAbilitiesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        pokemonAbilitiesPanel.add(paNegativeAbilitiesCheckBox, gbc);
        paBadAbilitiesCheckBox = new JCheckBox();
        paBadAbilitiesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(paBadAbilitiesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paBadAbilitiesCheckBox.text"));
        paBadAbilitiesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paBadAbilitiesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        pokemonAbilitiesPanel.add(paBadAbilitiesCheckBox, gbc);
        paFollowMegaEvosCheckBox = new JCheckBox();
        paFollowMegaEvosCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(paFollowMegaEvosCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paFollowMegaEvosCheckBox.text"));
        paFollowMegaEvosCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paFollowMegaEvosCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        pokemonAbilitiesPanel.add(paFollowMegaEvosCheckBox, gbc);
        paWeighDuplicatesTogetherCheckBox = new JCheckBox();
        paWeighDuplicatesTogetherCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(paWeighDuplicatesTogetherCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paWeighDuplicatesTogetherCheckBox.text"));
        paWeighDuplicatesTogetherCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.paWeighDuplicatesTogetherCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        pokemonAbilitiesPanel.add(paWeighDuplicatesTogetherCheckBox, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel6, gbc);
        panel6.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pePanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel6.getFont()), null));
        peUnchangedRadioButton = new JRadioButton();
        peUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(peUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peUnchangedRadioButton.text"));
        peUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(peUnchangedRadioButton, gbc);
        final JPanel spacer18 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(spacer18, gbc);
        final JPanel spacer19 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel6.add(spacer19, gbc);
        final JPanel spacer20 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(spacer20, gbc);
        final JPanel spacer21 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel6.add(spacer21, gbc);
        peRandomRadioButton = new JRadioButton();
        peRandomRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(peRandomRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peRandomRadioButton.text"));
        peRandomRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peRandomRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(peRandomRadioButton, gbc);
        peSimilarStrengthCheckBox = new JCheckBox();
        peSimilarStrengthCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(peSimilarStrengthCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peSimilarStrengthCheckBox.text"));
        peSimilarStrengthCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peSimilarStrengthCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.9;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(peSimilarStrengthCheckBox, gbc);
        peSameTypingCheckBox = new JCheckBox();
        peSameTypingCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(peSameTypingCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peSameTypingCheckBox.text"));
        peSameTypingCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peSameTypingCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(peSameTypingCheckBox, gbc);
        peLimitEvolutionsToThreeCheckBox = new JCheckBox();
        peLimitEvolutionsToThreeCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(peLimitEvolutionsToThreeCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peLimitEvolutionsToThreeCheckBox.text"));
        peLimitEvolutionsToThreeCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peLimitEvolutionsToThreeCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(peLimitEvolutionsToThreeCheckBox, gbc);
        peForceChangeCheckBox = new JCheckBox();
        peForceChangeCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(peForceChangeCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peForceChangeCheckBox.text"));
        peForceChangeCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peForceChangeCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(peForceChangeCheckBox, gbc);
        peChangeImpossibleEvosCheckBox = new JCheckBox();
        peChangeImpossibleEvosCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(peChangeImpossibleEvosCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peChangeImpossibleEvosCheckBox.text"));
        peChangeImpossibleEvosCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peChangeImpossibleEvosCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(peChangeImpossibleEvosCheckBox, gbc);
        peMakeEvolutionsEasierCheckBox = new JCheckBox();
        peMakeEvolutionsEasierCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(peMakeEvolutionsEasierCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peMakeEvolutionsEasierCheckBox.text"));
        peMakeEvolutionsEasierCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peMakeEvolutionsEasierCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(peMakeEvolutionsEasierCheckBox, gbc);
        peAllowAltFormesCheckBox = new JCheckBox();
        peAllowAltFormesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(peAllowAltFormesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peAllowAltFormesCheckBox.text"));
        peAllowAltFormesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peAllowAltFormesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(peAllowAltFormesCheckBox, gbc);
        peRemoveTimeBasedEvolutionsCheckBox = new JCheckBox();
        peRemoveTimeBasedEvolutionsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(peRemoveTimeBasedEvolutionsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peRemoveTimeBasedEvolutions.text"));
        peRemoveTimeBasedEvolutionsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.peRemoveTimeBasedEvolutions.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(peRemoveTimeBasedEvolutionsCheckBox, gbc);
        final JPanel spacer22 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel3.add(spacer22, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        tabbedPane1.addTab(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.startersStaticsTradesPanel"), panel7);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(panel8, gbc);
        panel8.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel8.getFont()), null));
        spUnchangedRadioButton = new JRadioButton();
        spUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(spUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spUnchangedRadioButton.text"));
        spUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel8.add(spUnchangedRadioButton, gbc);
        final JPanel spacer23 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(spacer23, gbc);
        final JPanel spacer24 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel8.add(spacer24, gbc);
        final JPanel spacer25 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(spacer25, gbc);
        final JPanel spacer26 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel8.add(spacer26, gbc);
        spCustomRadioButton = new JRadioButton();
        spCustomRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(spCustomRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spCustomRadioButton.text"));
        spCustomRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spCustomRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel8.add(spCustomRadioButton, gbc);
        spRandomCompletelyRadioButton = new JRadioButton();
        spRandomCompletelyRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(spRandomCompletelyRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spRandomCompletelyRadioButton.text"));
        spRandomCompletelyRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spRandomCompletelyRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel8.add(spRandomCompletelyRadioButton, gbc);
        spRandomTwoEvosRadioButton = new JRadioButton();
        spRandomTwoEvosRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(spRandomTwoEvosRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spRandomTwoEvosRadioButton.text"));
        spRandomTwoEvosRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spRandomTwoEvosRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel8.add(spRandomTwoEvosRadioButton, gbc);
        spComboBox1 = new JComboBox();
        spComboBox1.setEnabled(false);
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("--");
        spComboBox1.setModel(defaultComboBoxModel3);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel8.add(spComboBox1, gbc);
        spComboBox2 = new JComboBox();
        spComboBox2.setEnabled(false);
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("--");
        spComboBox2.setModel(defaultComboBoxModel4);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel8.add(spComboBox2, gbc);
        spComboBox3 = new JComboBox();
        spComboBox3.setEnabled(false);
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("--");
        spComboBox3.setModel(defaultComboBoxModel5);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 5);
        panel8.add(spComboBox3, gbc);
        spRandomizeStarterHeldItemsCheckBox = new JCheckBox();
        spRandomizeStarterHeldItemsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(spRandomizeStarterHeldItemsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spRandomizeStarterHeldItemsCheckBox.text"));
        spRandomizeStarterHeldItemsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spRandomizeStarterHeldItemsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel8.add(spRandomizeStarterHeldItemsCheckBox, gbc);
        spBanBadItemsCheckBox = new JCheckBox();
        spBanBadItemsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(spBanBadItemsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spBanBadItemsCheckBox.text"));
        spBanBadItemsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spBanBadItemsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel8.add(spBanBadItemsCheckBox, gbc);
        spAllowAltFormesCheckBox = new JCheckBox();
        spAllowAltFormesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(spAllowAltFormesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spAllowAltFormesCheckBox.text"));
        spAllowAltFormesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.spAllowAltFormesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel8.add(spAllowAltFormesCheckBox, gbc);
        final JPanel spacer27 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(spacer27, gbc);
        final JPanel spacer28 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel7.add(spacer28, gbc);
        final JPanel spacer29 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(spacer29, gbc);
        final JPanel spacer30 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel7.add(spacer30, gbc);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(panel9, gbc);
        panel9.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel9.getFont()), null));
        stpUnchangedRadioButton = new JRadioButton();
        stpUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(stpUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpUnchangedRadioButton.text"));
        stpUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(stpUnchangedRadioButton, gbc);
        final JPanel spacer31 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 6.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(spacer31, gbc);
        final JPanel spacer32 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel9.add(spacer32, gbc);
        final JPanel spacer33 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(spacer33, gbc);
        final JPanel spacer34 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel9.add(spacer34, gbc);
        stpSwapLegendariesSwapStandardsRadioButton = new JRadioButton();
        stpSwapLegendariesSwapStandardsRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(stpSwapLegendariesSwapStandardsRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpSwapLegendariesSwapStandardsRadioButton.text"));
        stpSwapLegendariesSwapStandardsRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpSwapLegendariesSwapStandardsRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(stpSwapLegendariesSwapStandardsRadioButton, gbc);
        stpRandomCompletelyRadioButton = new JRadioButton();
        stpRandomCompletelyRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(stpRandomCompletelyRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpRandomCompletelyRadioButton.text"));
        stpRandomCompletelyRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpRandomCompletelyRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(stpRandomCompletelyRadioButton, gbc);
        stpRandomSimilarStrengthRadioButton = new JRadioButton();
        stpRandomSimilarStrengthRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(stpRandomSimilarStrengthRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpRandomSimilarStrengthRadioButton.text"));
        stpRandomSimilarStrengthRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpRandomSimilarStrengthRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(stpRandomSimilarStrengthRadioButton, gbc);
        stpSwapMegaEvosCheckBox = new JCheckBox();
        stpSwapMegaEvosCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(stpSwapMegaEvosCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpSwapMegaEvosCheckBox.text"));
        stpSwapMegaEvosCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpSwapMegaEvosCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(stpSwapMegaEvosCheckBox, gbc);
        stpPercentageLevelModifierCheckBox = new JCheckBox();
        stpPercentageLevelModifierCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(stpPercentageLevelModifierCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpPercentageLevelModifierCheckBox.text"));
        stpPercentageLevelModifierCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpPercentageLevelModifierCheckBox.tooltipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 6.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(stpPercentageLevelModifierCheckBox, gbc);
        stpPercentageLevelModifierSlider = new JSlider();
        stpPercentageLevelModifierSlider.setEnabled(false);
        stpPercentageLevelModifierSlider.setMajorTickSpacing(10);
        stpPercentageLevelModifierSlider.setMaximum(50);
        stpPercentageLevelModifierSlider.setMinimum(-50);
        stpPercentageLevelModifierSlider.setMinorTickSpacing(2);
        stpPercentageLevelModifierSlider.setPaintLabels(true);
        stpPercentageLevelModifierSlider.setPaintTicks(true);
        stpPercentageLevelModifierSlider.setSnapToTicks(true);
        stpPercentageLevelModifierSlider.setValue(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridheight = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(stpPercentageLevelModifierSlider, gbc);
        stpRandomize600BSTCheckBox = new JCheckBox();
        stpRandomize600BSTCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(stpRandomize600BSTCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpRandomize600BSTCheckBox.text"));
        stpRandomize600BSTCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpRandomize600BSTCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.8;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(stpRandomize600BSTCheckBox, gbc);
        stpAllowAltFormesCheckBox = new JCheckBox();
        stpAllowAltFormesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(stpAllowAltFormesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpAllowAltFormesCheckBox.text"));
        stpAllowAltFormesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpAllowAltFormesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(stpAllowAltFormesCheckBox, gbc);
        stpLimitMainGameLegendariesCheckBox = new JCheckBox();
        stpLimitMainGameLegendariesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(stpLimitMainGameLegendariesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpLimitMainGameLegendariesCheckBox.text"));
        stpLimitMainGameLegendariesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.stpLimitMainGameLegendariesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(stpLimitMainGameLegendariesCheckBox, gbc);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(panel10, gbc);
        panel10.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel10.getFont()), null));
        igtUnchangedRadioButton = new JRadioButton();
        igtUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(igtUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtUnchangedRadioButton.text"));
        igtUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        panel10.add(igtUnchangedRadioButton, gbc);
        final JPanel spacer35 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel10.add(spacer35, gbc);
        final JPanel spacer36 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel10.add(spacer36, gbc);
        igtRandomizeGivenPokemonOnlyRadioButton = new JRadioButton();
        igtRandomizeGivenPokemonOnlyRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(igtRandomizeGivenPokemonOnlyRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtRandomizeGivenPokemonOnlyRadioButton.text"));
        igtRandomizeGivenPokemonOnlyRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtRandomizeGivenPokemonOnlyRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel10.add(igtRandomizeGivenPokemonOnlyRadioButton, gbc);
        igtRandomizeBothRequestedGivenRadioButton = new JRadioButton();
        igtRandomizeBothRequestedGivenRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(igtRandomizeBothRequestedGivenRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtRandomizeBothRequestedGivenRadioButton.text"));
        igtRandomizeBothRequestedGivenRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtRandomizeBothRequestedGivenRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel10.add(igtRandomizeBothRequestedGivenRadioButton, gbc);
        final JPanel spacer37 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel10.add(spacer37, gbc);
        final JPanel spacer38 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel10.add(spacer38, gbc);
        igtRandomizeNicknamesCheckBox = new JCheckBox();
        igtRandomizeNicknamesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(igtRandomizeNicknamesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtRandomizeNicknamesCheckBox.text"));
        igtRandomizeNicknamesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtRandomizeNicknamesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.8;
        gbc.anchor = GridBagConstraints.WEST;
        panel10.add(igtRandomizeNicknamesCheckBox, gbc);
        igtRandomizeOTsCheckBox = new JCheckBox();
        igtRandomizeOTsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(igtRandomizeOTsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtRandomizeOTsCheckBox.text"));
        igtRandomizeOTsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtRandomizeOTsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel10.add(igtRandomizeOTsCheckBox, gbc);
        igtRandomizeIVsCheckBox = new JCheckBox();
        igtRandomizeIVsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(igtRandomizeIVsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtRandomizeIVsCheckBox.text"));
        igtRandomizeIVsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtRandomizeIVsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel10.add(igtRandomizeIVsCheckBox, gbc);
        igtRandomizeItemsCheckBox = new JCheckBox();
        igtRandomizeItemsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(igtRandomizeItemsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtRandomizeItemsCheckBox.text"));
        igtRandomizeItemsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.igtRandomizeItemsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel10.add(igtRandomizeItemsCheckBox, gbc);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridBagLayout());
        tabbedPane1.addTab(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.movesMovesetsPanel"), panel11);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(panel12, gbc);
        panel12.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mdPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel12.getFont()), null));
        mdRandomizeMovePowerCheckBox = new JCheckBox();
        mdRandomizeMovePowerCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(mdRandomizeMovePowerCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mdRandomizeMovePowerCheckBox.text"));
        mdRandomizeMovePowerCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mdRandomizeMovePowerCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        panel12.add(mdRandomizeMovePowerCheckBox, gbc);
        final JPanel spacer39 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel12.add(spacer39, gbc);
        final JPanel spacer40 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel12.add(spacer40, gbc);
        final JPanel spacer41 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel12.add(spacer41, gbc);
        final JPanel spacer42 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel12.add(spacer42, gbc);
        mdRandomizeMoveAccuracyCheckBox = new JCheckBox();
        mdRandomizeMoveAccuracyCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(mdRandomizeMoveAccuracyCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mdRandomizeMoveAccuracyCheckBox.text"));
        mdRandomizeMoveAccuracyCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mdRandomizeMoveAccuracyCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel12.add(mdRandomizeMoveAccuracyCheckBox, gbc);
        mdRandomizeMovePPCheckBox = new JCheckBox();
        mdRandomizeMovePPCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(mdRandomizeMovePPCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mdRandomizeMovePPCheckBox.text"));
        mdRandomizeMovePPCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mdRandomizeMovePPCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel12.add(mdRandomizeMovePPCheckBox, gbc);
        mdRandomizeMoveTypesCheckBox = new JCheckBox();
        mdRandomizeMoveTypesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(mdRandomizeMoveTypesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mdRandomizeMoveTypesCheckBox.text"));
        mdRandomizeMoveTypesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mdRandomizeMoveTypesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel12.add(mdRandomizeMoveTypesCheckBox, gbc);
        mdRandomizeMoveCategoryCheckBox = new JCheckBox();
        mdRandomizeMoveCategoryCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(mdRandomizeMoveCategoryCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mdRandomizeMoveCategoryCheckBox.text"));
        mdRandomizeMoveCategoryCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mdRandomizeMoveCategoryCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.6;
        gbc.anchor = GridBagConstraints.WEST;
        panel12.add(mdRandomizeMoveCategoryCheckBox, gbc);
        mdUpdateMovesCheckBox = new JCheckBox();
        mdUpdateMovesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(mdUpdateMovesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mdUpdateMovesCheckBox.text"));
        mdUpdateMovesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mdUpdateMovesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel12.add(mdUpdateMovesCheckBox, gbc);
        mdUpdateComboBox = new JComboBox();
        mdUpdateComboBox.setEnabled(false);
        final DefaultComboBoxModel defaultComboBoxModel6 = new DefaultComboBoxModel();
        defaultComboBoxModel6.addElement("--");
        mdUpdateComboBox.setModel(defaultComboBoxModel6);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel12.add(mdUpdateComboBox, gbc);
        final JPanel spacer43 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel11.add(spacer43, gbc);
        final JPanel spacer44 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel11.add(spacer44, gbc);
        final JPanel spacer45 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel11.add(spacer45, gbc);
        final JPanel spacer46 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel11.add(spacer46, gbc);
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(panel13, gbc);
        panel13.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel13.getFont()), null));
        pmsUnchangedRadioButton = new JRadioButton();
        pmsUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(pmsUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsUnchangedRadioButton.text"));
        pmsUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        panel13.add(pmsUnchangedRadioButton, gbc);
        final JPanel spacer47 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel13.add(spacer47, gbc);
        final JPanel spacer48 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel13.add(spacer48, gbc);
        final JPanel spacer49 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel13.add(spacer49, gbc);
        final JPanel spacer50 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel13.add(spacer50, gbc);
        pmsRandomPreferringSameTypeRadioButton = new JRadioButton();
        pmsRandomPreferringSameTypeRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(pmsRandomPreferringSameTypeRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsRandomPreferringSameTypeRadioButton.text"));
        pmsRandomPreferringSameTypeRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsRandomPreferringSameTypeRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel13.add(pmsRandomPreferringSameTypeRadioButton, gbc);
        pmsRandomCompletelyRadioButton = new JRadioButton();
        pmsRandomCompletelyRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(pmsRandomCompletelyRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsRandomCompletelyRadioButton.text"));
        pmsRandomCompletelyRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsRandomCompletelyRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel13.add(pmsRandomCompletelyRadioButton, gbc);
        pmsMetronomeOnlyModeRadioButton = new JRadioButton();
        pmsMetronomeOnlyModeRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(pmsMetronomeOnlyModeRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsMetronomeOnlyModeRadioButton.text"));
        pmsMetronomeOnlyModeRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsMetronomeOnlyModeRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel13.add(pmsMetronomeOnlyModeRadioButton, gbc);
        pmsGuaranteedLevel1MovesCheckBox = new JCheckBox();
        pmsGuaranteedLevel1MovesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(pmsGuaranteedLevel1MovesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsGuaranteedLevel1MovesCheckBox.text"));
        pmsGuaranteedLevel1MovesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsGuaranteedLevel1MovesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel13.add(pmsGuaranteedLevel1MovesCheckBox, gbc);
        pmsReorderDamagingMovesCheckBox = new JCheckBox();
        pmsReorderDamagingMovesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(pmsReorderDamagingMovesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsReorderDamagingMovesCheckBox.text"));
        pmsReorderDamagingMovesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsReorderDamagingMovesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel13.add(pmsReorderDamagingMovesCheckBox, gbc);
        pmsNoGameBreakingMovesCheckBox = new JCheckBox();
        pmsNoGameBreakingMovesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(pmsNoGameBreakingMovesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsNoGameBreakingMovesCheckBox.text"));
        pmsNoGameBreakingMovesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsNoGameBreakingMovesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel13.add(pmsNoGameBreakingMovesCheckBox, gbc);
        pmsForceGoodDamagingCheckBox = new JCheckBox();
        pmsForceGoodDamagingCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(pmsForceGoodDamagingCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsForceGoodDamagingCheckBox.text"));
        pmsForceGoodDamagingCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsForceGoodDamagingCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel13.add(pmsForceGoodDamagingCheckBox, gbc);
        pmsGuaranteedLevel1MovesSlider = new JSlider();
        pmsGuaranteedLevel1MovesSlider.setEnabled(false);
        pmsGuaranteedLevel1MovesSlider.setMajorTickSpacing(1);
        pmsGuaranteedLevel1MovesSlider.setMaximum(4);
        pmsGuaranteedLevel1MovesSlider.setMinimum(2);
        pmsGuaranteedLevel1MovesSlider.setPaintLabels(true);
        pmsGuaranteedLevel1MovesSlider.setPaintTicks(false);
        pmsGuaranteedLevel1MovesSlider.setSnapToTicks(true);
        pmsGuaranteedLevel1MovesSlider.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsGuaranteedLevel1MovesSlider.toolTipText"));
        pmsGuaranteedLevel1MovesSlider.setValue(2);
        pmsGuaranteedLevel1MovesSlider.setValueIsAdjusting(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel13.add(pmsGuaranteedLevel1MovesSlider, gbc);
        pmsForceGoodDamagingSlider = new JSlider();
        pmsForceGoodDamagingSlider.setEnabled(false);
        pmsForceGoodDamagingSlider.setMajorTickSpacing(20);
        pmsForceGoodDamagingSlider.setMinorTickSpacing(5);
        pmsForceGoodDamagingSlider.setPaintLabels(true);
        pmsForceGoodDamagingSlider.setPaintTicks(true);
        pmsForceGoodDamagingSlider.setSnapToTicks(true);
        pmsForceGoodDamagingSlider.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsForceGoodDamagingSlider.toolTipText"));
        pmsForceGoodDamagingSlider.setValue(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel13.add(pmsForceGoodDamagingSlider, gbc);
        pmsEvolutionMovesCheckBox = new JCheckBox();
        pmsEvolutionMovesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(pmsEvolutionMovesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsEvolutionMovesCheckBox.text"));
        pmsEvolutionMovesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.pmsEvolutionMovesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel13.add(pmsEvolutionMovesCheckBox, gbc);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridBagLayout());
        tabbedPane1.addTab(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.foePokemonPanel.title"), panel14);
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(panel15, gbc);
        panel15.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel15.getFont()), null));
        tpUnchangedRadioButton = new JRadioButton();
        tpUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(tpUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpUnchangedRadioButton.text"));
        tpUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpUnchangedRadioButton, gbc);
        final JPanel spacer51 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel15.add(spacer51, gbc);
        final JPanel spacer52 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel15.add(spacer52, gbc);
        final JPanel spacer53 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel15.add(spacer53, gbc);
        tpRandomRadioButton = new JRadioButton();
        tpRandomRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(tpRandomRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRandomRadioButton.text"));
        tpRandomRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRandomRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpRandomRadioButton, gbc);
        tpRandomEvenDistributionRadioButton = new JRadioButton();
        tpRandomEvenDistributionRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(tpRandomEvenDistributionRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRandomEvenDistributionRadioButton.text"));
        tpRandomEvenDistributionRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRandomEvenDistributionRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpRandomEvenDistributionRadioButton, gbc);
        tpRandomEvenDistributionMainRadioButton = new JRadioButton();
        tpRandomEvenDistributionMainRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(tpRandomEvenDistributionMainRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRandomEvenDistributionMainRadioButton.text"));
        tpRandomEvenDistributionMainRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRandomEvenDistributionMainRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpRandomEvenDistributionMainRadioButton, gbc);
        tpRivalCarriesStarterCheckBox = new JCheckBox();
        tpRivalCarriesStarterCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpRivalCarriesStarterCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRivalCarriesStarterCheckBox.text"));
        tpRivalCarriesStarterCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRivalCarriesStarterCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpRivalCarriesStarterCheckBox, gbc);
        tpSimilarStrengthCheckBox = new JCheckBox();
        tpSimilarStrengthCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpSimilarStrengthCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpSimilarStrengthCheckBox.text"));
        tpSimilarStrengthCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpSimilarStrengthCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpSimilarStrengthCheckBox, gbc);
        tpRandomizeTrainerNamesCheckBox = new JCheckBox();
        tpRandomizeTrainerNamesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpRandomizeTrainerNamesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRandomizeTrainerNamesCheckBox.text"));
        tpRandomizeTrainerNamesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRandomizeTrainerNamesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpRandomizeTrainerNamesCheckBox, gbc);
        tpRandomizeTrainerClassNamesCheckBox = new JCheckBox();
        tpRandomizeTrainerClassNamesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpRandomizeTrainerClassNamesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRandomizeTrainerClassNamesCheckBox.text"));
        tpRandomizeTrainerClassNamesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRandomizeTrainerClassNamesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpRandomizeTrainerClassNamesCheckBox, gbc);
        tpForceFullyEvolvedAtCheckBox = new JCheckBox();
        tpForceFullyEvolvedAtCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpForceFullyEvolvedAtCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpForceFullyEvolvedAtCheckBox.text"));
        tpForceFullyEvolvedAtCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpForceFullyEvolvedAtCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpForceFullyEvolvedAtCheckBox, gbc);
        tpForceFullyEvolvedAtSlider = new JSlider();
        tpForceFullyEvolvedAtSlider.setEnabled(false);
        tpForceFullyEvolvedAtSlider.setMajorTickSpacing(5);
        tpForceFullyEvolvedAtSlider.setMaximum(65);
        tpForceFullyEvolvedAtSlider.setMinimum(30);
        tpForceFullyEvolvedAtSlider.setMinorTickSpacing(1);
        tpForceFullyEvolvedAtSlider.setPaintLabels(true);
        tpForceFullyEvolvedAtSlider.setPaintTicks(true);
        tpForceFullyEvolvedAtSlider.setSnapToTicks(true);
        tpForceFullyEvolvedAtSlider.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpForceFullyEvolvedAtSlider.toolTipText"));
        tpForceFullyEvolvedAtSlider.setValue(30);
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 5;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel15.add(tpForceFullyEvolvedAtSlider, gbc);
        tpPercentageLevelModifierSlider = new JSlider();
        tpPercentageLevelModifierSlider.setEnabled(false);
        tpPercentageLevelModifierSlider.setMajorTickSpacing(10);
        tpPercentageLevelModifierSlider.setMaximum(50);
        tpPercentageLevelModifierSlider.setMinimum(-50);
        tpPercentageLevelModifierSlider.setMinorTickSpacing(2);
        tpPercentageLevelModifierSlider.setPaintLabels(true);
        tpPercentageLevelModifierSlider.setPaintTicks(true);
        tpPercentageLevelModifierSlider.setSnapToTicks(true);
        tpPercentageLevelModifierSlider.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpPercentageLevelModifierSlider.toolTipText"));
        tpPercentageLevelModifierSlider.setValue(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 8;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel15.add(tpPercentageLevelModifierSlider, gbc);
        tpPercentageLevelModifierCheckBox = new JCheckBox();
        tpPercentageLevelModifierCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpPercentageLevelModifierCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpPercentageLevelModifierCheckBox.text"));
        tpPercentageLevelModifierCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpPercentageLevelModifierCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpPercentageLevelModifierCheckBox, gbc);
        final JPanel spacer54 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel15.add(spacer54, gbc);
        tpTypeThemedRadioButton = new JRadioButton();
        tpTypeThemedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(tpTypeThemedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpTypeThemedRadioButton.text"));
        tpTypeThemedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpTypeThemedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpTypeThemedRadioButton, gbc);
        tpWeightTypesCheckBox = new JCheckBox();
        tpWeightTypesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpWeightTypesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpWeightTypesCheckBox.text"));
        tpWeightTypesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpWeightTypesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpWeightTypesCheckBox, gbc);
        tpDontUseLegendariesCheckBox = new JCheckBox();
        tpDontUseLegendariesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpDontUseLegendariesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpDontUseLegendariesCheckBox.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpDontUseLegendariesCheckBox, gbc);
        tpNoEarlyWonderGuardCheckBox = new JCheckBox();
        tpNoEarlyWonderGuardCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpNoEarlyWonderGuardCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpNoEarlyWonderGuardCheckBox.text"));
        tpNoEarlyWonderGuardCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpNoEarlyWonderGuardCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpNoEarlyWonderGuardCheckBox, gbc);
        tpAllowAlternateFormesCheckBox = new JCheckBox();
        tpAllowAlternateFormesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpAllowAlternateFormesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpAllowAlternateFormesCheckBox.text"));
        tpAllowAlternateFormesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpAllowAlternateFormesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpAllowAlternateFormesCheckBox, gbc);
        tpSwapMegaEvosCheckBox = new JCheckBox();
        tpSwapMegaEvosCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpSwapMegaEvosCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpSwapMegaEvosCheckBox.text"));
        tpSwapMegaEvosCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpSwapMegaEvosCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpSwapMegaEvosCheckBox, gbc);
        tpDoubleBattleModeCheckBox = new JCheckBox();
        tpDoubleBattleModeCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpDoubleBattleModeCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpDoubleBattleModeCheckBox.text"));
        tpDoubleBattleModeCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpDoubleBattleModeCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpDoubleBattleModeCheckBox, gbc);
        tpAdditionalPokemonForLabel = new JLabel();
        this.$$$loadLabelText$$$(tpAdditionalPokemonForLabel, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpAddMorePokemonForLabel.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        panel15.add(tpAdditionalPokemonForLabel, gbc);
        tpBossTrainersCheckBox = new JCheckBox();
        tpBossTrainersCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpBossTrainersCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpBossTrainersCheckBox.text"));
        tpBossTrainersCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpBossTrainersCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpBossTrainersCheckBox, gbc);
        tpImportantTrainersCheckBox = new JCheckBox();
        tpImportantTrainersCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpImportantTrainersCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpImportantTrainersCheckBox.text"));
        tpImportantTrainersCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpImportantTrainersCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpImportantTrainersCheckBox, gbc);
        tpRegularTrainersCheckBox = new JCheckBox();
        tpRegularTrainersCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpRegularTrainersCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRegularTrainersCheckBox.text"));
        tpRegularTrainersCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRegularTrainersCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        panel15.add(tpRegularTrainersCheckBox, gbc);
        tpBossTrainersSpinner = new JSpinner();
        tpBossTrainersSpinner.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        gbc.insets = new Insets(0, 4, 0, 0);
        panel15.add(tpBossTrainersSpinner, gbc);
        tpImportantTrainersSpinner = new JSpinner();
        tpImportantTrainersSpinner.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        gbc.insets = new Insets(0, 4, 0, 0);
        panel15.add(tpImportantTrainersSpinner, gbc);
        tpRegularTrainersSpinner = new JSpinner();
        tpRegularTrainersSpinner.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        gbc.insets = new Insets(0, 4, 0, 0);
        panel15.add(tpRegularTrainersSpinner, gbc);
        tpRandomShinyTrainerPokemonCheckBox = new JCheckBox();
        tpRandomShinyTrainerPokemonCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tpRandomShinyTrainerPokemonCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRandomShinyTrainerPokemonCheckBox.text"));
        tpRandomShinyTrainerPokemonCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tpRandomShinyTrainerPokemonCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel15.add(tpRandomShinyTrainerPokemonCheckBox, gbc);
        final JPanel spacer55 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel14.add(spacer55, gbc);
        final JPanel spacer56 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel14.add(spacer56, gbc);
        final JPanel spacer57 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel14.add(spacer57, gbc);
        final JPanel spacer58 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel14.add(spacer58, gbc);
        totpPanel = new JPanel();
        totpPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(totpPanel, gbc);
        totpPanel.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, totpPanel.getFont()), null));
        totpUnchangedRadioButton = new JRadioButton();
        totpUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(totpUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpUnchangedRadioButton.text"));
        totpUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.01;
        gbc.anchor = GridBagConstraints.WEST;
        totpPanel.add(totpUnchangedRadioButton, gbc);
        final JPanel spacer59 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        totpPanel.add(spacer59, gbc);
        final JPanel spacer60 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        totpPanel.add(spacer60, gbc);
        final JPanel spacer61 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        totpPanel.add(spacer61, gbc);
        final JPanel spacer62 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        totpPanel.add(spacer62, gbc);
        totpAllyPanel = new JPanel();
        totpAllyPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
        totpPanel.add(totpAllyPanel, gbc);
        totpAllyPanel.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAllyPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        totpAllyUnchangedRadioButton = new JRadioButton();
        totpAllyUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(totpAllyUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAllyUnchangedRadioButton.text"));
        totpAllyUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAllyUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        totpAllyPanel.add(totpAllyUnchangedRadioButton, gbc);
        totpAllyRandomRadioButton = new JRadioButton();
        totpAllyRandomRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(totpAllyRandomRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAllyRandomRadioButton.text"));
        totpAllyRandomRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAllyRandomRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        totpAllyPanel.add(totpAllyRandomRadioButton, gbc);
        totpAllyRandomSimilarStrengthRadioButton = new JRadioButton();
        totpAllyRandomSimilarStrengthRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(totpAllyRandomSimilarStrengthRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAllyRandomSimilarStrengthRadioButton.text"));
        totpAllyRandomSimilarStrengthRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAllyRandomSimilarStrengthRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        totpAllyPanel.add(totpAllyRandomSimilarStrengthRadioButton, gbc);
        totpAuraPanel = new JPanel();
        totpAuraPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
        totpPanel.add(totpAuraPanel, gbc);
        totpAuraPanel.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAuraPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        totpAuraUnchangedRadioButton = new JRadioButton();
        totpAuraUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(totpAuraUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAuraUnchangedRadioButton.text"));
        totpAuraUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAuraUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        totpAuraPanel.add(totpAuraUnchangedRadioButton, gbc);
        totpAuraRandomRadioButton = new JRadioButton();
        totpAuraRandomRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(totpAuraRandomRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAuraRandomRadioButton.text"));
        totpAuraRandomRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAuraRandomRadioButton.toolTipText."));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        totpAuraPanel.add(totpAuraRandomRadioButton, gbc);
        totpAuraRandomSameStrengthRadioButton = new JRadioButton();
        totpAuraRandomSameStrengthRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(totpAuraRandomSameStrengthRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAuraRandomSameStrengthRadioButton.text"));
        totpAuraRandomSameStrengthRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAuraRandomSameStrengthRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        totpAuraPanel.add(totpAuraRandomSameStrengthRadioButton, gbc);
        totpPercentageLevelModifierSlider = new JSlider();
        totpPercentageLevelModifierSlider.setEnabled(false);
        totpPercentageLevelModifierSlider.setMajorTickSpacing(10);
        totpPercentageLevelModifierSlider.setMaximum(50);
        totpPercentageLevelModifierSlider.setMinimum(-50);
        totpPercentageLevelModifierSlider.setMinorTickSpacing(2);
        totpPercentageLevelModifierSlider.setPaintLabels(true);
        totpPercentageLevelModifierSlider.setPaintTicks(true);
        totpPercentageLevelModifierSlider.setSnapToTicks(true);
        totpPercentageLevelModifierSlider.setValue(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        totpPanel.add(totpPercentageLevelModifierSlider, gbc);
        totpPercentageLevelModifierCheckBox = new JCheckBox();
        totpPercentageLevelModifierCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(totpPercentageLevelModifierCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpPercentageLevelModifierCheckBox.text"));
        totpPercentageLevelModifierCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpPercentageLevelModifierCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        totpPanel.add(totpPercentageLevelModifierCheckBox, gbc);
        totpRandomizeHeldItemsCheckBox = new JCheckBox();
        totpRandomizeHeldItemsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(totpRandomizeHeldItemsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpRandomizeHeldItemsCheckBox.text"));
        totpRandomizeHeldItemsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpRandomizeHeldItemsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        totpPanel.add(totpRandomizeHeldItemsCheckBox, gbc);
        totpAllowAltFormesCheckBox = new JCheckBox();
        totpAllowAltFormesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(totpAllowAltFormesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAllowAltFormesCheckBox.text"));
        totpAllowAltFormesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpAllowAltFormesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        totpPanel.add(totpAllowAltFormesCheckBox, gbc);
        totpRandomRadioButton = new JRadioButton();
        totpRandomRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(totpRandomRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpRandomRadioButton.text"));
        totpRandomRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpRandomRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        totpPanel.add(totpRandomRadioButton, gbc);
        totpRandomSimilarStrengthRadioButton = new JRadioButton();
        totpRandomSimilarStrengthRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(totpRandomSimilarStrengthRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpRandomSimilarStrengthRadioButton.text"));
        totpRandomSimilarStrengthRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.totpRandomSimilarStrengthRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        totpPanel.add(totpRandomSimilarStrengthRadioButton, gbc);
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridBagLayout());
        panel16.setEnabled(false);
        tabbedPane1.addTab(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wildPokemonPanel.title"), panel16);
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        panel16.add(panel17, gbc);
        panel17.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel17.getFont()), null));
        wpUnchangedRadioButton = new JRadioButton();
        wpUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(wpUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpUnchangedRadioButton.text"));
        wpUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        panel17.add(wpUnchangedRadioButton, gbc);
        final JPanel spacer63 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel17.add(spacer63, gbc);
        final JPanel spacer64 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel17.add(spacer64, gbc);
        final JPanel spacer65 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel17.add(spacer65, gbc);
        final JPanel spacer66 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel17.add(spacer66, gbc);
        wpRandomRadioButton = new JRadioButton();
        wpRandomRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(wpRandomRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpRandomRadioButton.text"));
        wpRandomRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpRandomRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel17.add(wpRandomRadioButton, gbc);
        wpArea1To1RadioButton = new JRadioButton();
        wpArea1To1RadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(wpArea1To1RadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpArea1To1RadioButton.text"));
        wpArea1To1RadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpArea1To1RadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel17.add(wpArea1To1RadioButton, gbc);
        wpGlobal1To1RadioButton = new JRadioButton();
        wpGlobal1To1RadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(wpGlobal1To1RadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpGlobal1To1RadioButton.text"));
        wpGlobal1To1RadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpGlobal1To1RadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel17.add(wpGlobal1To1RadioButton, gbc);
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 80);
        panel17.add(panel18, gbc);
        panel18.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpARPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        wpARNoneRadioButton = new JRadioButton();
        wpARNoneRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(wpARNoneRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpARNoneRadioButton.text"));
        wpARNoneRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpARNoneRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel18.add(wpARNoneRadioButton, gbc);
        final JPanel spacer67 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel18.add(spacer67, gbc);
        final JPanel spacer68 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel18.add(spacer68, gbc);
        final JPanel spacer69 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel18.add(spacer69, gbc);
        wpARSimilarStrengthRadioButton = new JRadioButton();
        wpARSimilarStrengthRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(wpARSimilarStrengthRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpARSimilarStrengthRadioButton.text"));
        wpARSimilarStrengthRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpARSimilarStrengthRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel18.add(wpARSimilarStrengthRadioButton, gbc);
        wpARCatchEmAllModeRadioButton = new JRadioButton();
        wpARCatchEmAllModeRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(wpARCatchEmAllModeRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpARCatchEmAllModeRadioButton.text"));
        wpARCatchEmAllModeRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpARCatchEmAllModeRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel18.add(wpARCatchEmAllModeRadioButton, gbc);
        wpARTypeThemeAreasRadioButton = new JRadioButton();
        wpARTypeThemeAreasRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(wpARTypeThemeAreasRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpARTypeThemeAreasRadioButton.text"));
        wpARTypeThemeAreasRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpARTypeThemeAreasRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel18.add(wpARTypeThemeAreasRadioButton, gbc);
        wpUseTimeBasedEncountersCheckBox = new JCheckBox();
        wpUseTimeBasedEncountersCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(wpUseTimeBasedEncountersCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpUseTimeBasedEncountersCheckBox.text"));
        wpUseTimeBasedEncountersCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpUseTimeBasedEncountersCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        panel17.add(wpUseTimeBasedEncountersCheckBox, gbc);
        wpDontUseLegendariesCheckBox = new JCheckBox();
        wpDontUseLegendariesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(wpDontUseLegendariesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpDontUseLegendariesCheckBox.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel17.add(wpDontUseLegendariesCheckBox, gbc);
        wpSetMinimumCatchRateCheckBox = new JCheckBox();
        wpSetMinimumCatchRateCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(wpSetMinimumCatchRateCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpSetMinimumCatchRateCheckBox.text"));
        wpSetMinimumCatchRateCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpSetMinimumCatchRateCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel17.add(wpSetMinimumCatchRateCheckBox, gbc);
        wpRandomizeHeldItemsCheckBox = new JCheckBox();
        wpRandomizeHeldItemsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(wpRandomizeHeldItemsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpRandomizeHeldItemsCheckBox.text"));
        wpRandomizeHeldItemsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpRandomizeHeldItemsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel17.add(wpRandomizeHeldItemsCheckBox, gbc);
        wpBanBadItemsCheckBox = new JCheckBox();
        wpBanBadItemsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(wpBanBadItemsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpBanBadItemsCheckBox.text"));
        wpBanBadItemsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpBanBadItemsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel17.add(wpBanBadItemsCheckBox, gbc);
        wpBalanceShakingGrassPokemonCheckBox = new JCheckBox();
        wpBalanceShakingGrassPokemonCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(wpBalanceShakingGrassPokemonCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpBalanceShakingGrassPokemonCheckBox.text"));
        wpBalanceShakingGrassPokemonCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpBalanceShakingGrassPokemonCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel17.add(wpBalanceShakingGrassPokemonCheckBox, gbc);
        wpPercentageLevelModifierCheckBox = new JCheckBox();
        wpPercentageLevelModifierCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(wpPercentageLevelModifierCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpPercentageLevelModifierCheckBox.text"));
        wpPercentageLevelModifierCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpPercentageLevelModifierCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel17.add(wpPercentageLevelModifierCheckBox, gbc);
        wpPercentageLevelModifierSlider = new JSlider();
        wpPercentageLevelModifierSlider.setEnabled(false);
        wpPercentageLevelModifierSlider.setMajorTickSpacing(10);
        wpPercentageLevelModifierSlider.setMaximum(50);
        wpPercentageLevelModifierSlider.setMinimum(-50);
        wpPercentageLevelModifierSlider.setMinorTickSpacing(2);
        wpPercentageLevelModifierSlider.setPaintLabels(true);
        wpPercentageLevelModifierSlider.setPaintTicks(true);
        wpPercentageLevelModifierSlider.setSnapToTicks(true);
        wpPercentageLevelModifierSlider.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpPercentageLevelModifierSlider.toolTipText"));
        wpPercentageLevelModifierSlider.setValue(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 30;
        panel17.add(wpPercentageLevelModifierSlider, gbc);
        wpSetMinimumCatchRateSlider = new JSlider();
        wpSetMinimumCatchRateSlider.setEnabled(false);
        wpSetMinimumCatchRateSlider.setMajorTickSpacing(1);
        wpSetMinimumCatchRateSlider.setMaximum(4);
        wpSetMinimumCatchRateSlider.setMinimum(1);
        wpSetMinimumCatchRateSlider.setPaintLabels(true);
        wpSetMinimumCatchRateSlider.setSnapToTicks(true);
        wpSetMinimumCatchRateSlider.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpSetMinimumCatchRateSlider.toolTipText"));
        wpSetMinimumCatchRateSlider.setValue(1);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel17.add(wpSetMinimumCatchRateSlider, gbc);
        wpAllowAltFormesCheckBox = new JCheckBox();
        wpAllowAltFormesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(wpAllowAltFormesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpAllowAltFormesCheckBox.text"));
        wpAllowAltFormesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wpAllowAltFormesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        panel17.add(wpAllowAltFormesCheckBox, gbc);
        final JPanel spacer70 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel16.add(spacer70, gbc);
        final JPanel spacer71 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel16.add(spacer71, gbc);
        final JPanel spacer72 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel16.add(spacer72, gbc);
        final JPanel spacer73 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel16.add(spacer73, gbc);
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridBagLayout());
        panel19.setEnabled(false);
        tabbedPane1.addTab(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmsHMsTutorsPanel.title"), panel19);
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        panel19.add(panel20, gbc);
        panel20.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel20.getFont()), null));
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel20.add(panel21, gbc);
        panel21.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmMovesPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        tmUnchangedRadioButton = new JRadioButton();
        tmUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(tmUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmUnchangedRadioButton.text"));
        tmUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 70);
        panel21.add(tmUnchangedRadioButton, gbc);
        final JPanel spacer74 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel21.add(spacer74, gbc);
        final JPanel spacer75 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel21.add(spacer75, gbc);
        final JPanel spacer76 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel21.add(spacer76, gbc);
        tmRandomRadioButton = new JRadioButton();
        tmRandomRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(tmRandomRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmRandomRadioButton.text"));
        tmRandomRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmRandomRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel21.add(tmRandomRadioButton, gbc);
        tmNoGameBreakingMovesCheckBox = new JCheckBox();
        tmNoGameBreakingMovesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tmNoGameBreakingMovesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmNoGameBreakingMovesCheckBox.text"));
        tmNoGameBreakingMovesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmNoGameBreakingMovesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel21.add(tmNoGameBreakingMovesCheckBox, gbc);
        tmKeepFieldMoveTMsCheckBox = new JCheckBox();
        tmKeepFieldMoveTMsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tmKeepFieldMoveTMsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmKeepFieldMoveTMsCheckBox.text"));
        tmKeepFieldMoveTMsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmKeepFieldMoveTMsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel21.add(tmKeepFieldMoveTMsCheckBox, gbc);
        tmForceGoodDamagingCheckBox = new JCheckBox();
        tmForceGoodDamagingCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tmForceGoodDamagingCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmForceGoodDamagingCheckBox.text"));
        tmForceGoodDamagingCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmForceGoodDamagingCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel21.add(tmForceGoodDamagingCheckBox, gbc);
        tmForceGoodDamagingSlider = new JSlider();
        tmForceGoodDamagingSlider.setEnabled(false);
        tmForceGoodDamagingSlider.setMajorTickSpacing(20);
        tmForceGoodDamagingSlider.setMinorTickSpacing(5);
        tmForceGoodDamagingSlider.setPaintLabels(true);
        tmForceGoodDamagingSlider.setPaintTicks(true);
        tmForceGoodDamagingSlider.setSnapToTicks(true);
        tmForceGoodDamagingSlider.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmForceGoodDamagingSlider.toolTipText"));
        tmForceGoodDamagingSlider.setValue(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel21.add(tmForceGoodDamagingSlider, gbc);
        final JPanel spacer77 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel20.add(spacer77, gbc);
        final JPanel spacer78 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel20.add(spacer78, gbc);
        final JPanel spacer79 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel20.add(spacer79, gbc);
        final JPanel spacer80 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel20.add(spacer80, gbc);
        final JPanel panel22 = new JPanel();
        panel22.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 20, 0, 0);
        panel20.add(panel22, gbc);
        panel22.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.thcPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        thcUnchangedRadioButton = new JRadioButton();
        thcUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(thcUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.thcUnchangedRadioButton.text"));
        thcUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.thcUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel22.add(thcUnchangedRadioButton, gbc);
        final JPanel spacer81 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel22.add(spacer81, gbc);
        final JPanel spacer82 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel22.add(spacer82, gbc);
        final JPanel spacer83 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel22.add(spacer83, gbc);
        thcRandomPreferSameTypeRadioButton = new JRadioButton();
        thcRandomPreferSameTypeRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(thcRandomPreferSameTypeRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.thcRandomPreferSameTypeRadioButton.text"));
        thcRandomPreferSameTypeRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.thcRandomPreferSameTypeRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel22.add(thcRandomPreferSameTypeRadioButton, gbc);
        thcRandomCompletelyRadioButton = new JRadioButton();
        thcRandomCompletelyRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(thcRandomCompletelyRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.thcRandomCompletelyRadioButton.text"));
        thcRandomCompletelyRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.thcRandomCompletelyRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel22.add(thcRandomCompletelyRadioButton, gbc);
        thcFullCompatibilityRadioButton = new JRadioButton();
        thcFullCompatibilityRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(thcFullCompatibilityRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.thcFullCompatibilityRadioButton.text"));
        thcFullCompatibilityRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.thcFullCompatibilityRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel22.add(thcFullCompatibilityRadioButton, gbc);
        tmFollowEvolutionsCheckBox = new JCheckBox();
        tmFollowEvolutionsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tmFollowEvolutionsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmFollowEvolutionsCheckBox.text"));
        tmFollowEvolutionsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmFollowEvolutionsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel22.add(tmFollowEvolutionsCheckBox, gbc);
        tmLevelupMoveSanityCheckBox = new JCheckBox();
        tmLevelupMoveSanityCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tmLevelupMoveSanityCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmLevelupMoveSanityCheckBox.text"));
        tmLevelupMoveSanityCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmLevelupMoveSanityCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel22.add(tmLevelupMoveSanityCheckBox, gbc);
        tmFullHMCompatibilityCheckBox = new JCheckBox();
        tmFullHMCompatibilityCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(tmFullHMCompatibilityCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmFullHMCompatibilityCheckBox.text"));
        tmFullHMCompatibilityCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.tmFullHMCompatibilityCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel22.add(tmFullHMCompatibilityCheckBox, gbc);
        final JPanel spacer84 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel19.add(spacer84, gbc);
        final JPanel spacer85 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel19.add(spacer85, gbc);
        final JPanel spacer86 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel19.add(spacer86, gbc);
        final JPanel spacer87 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel19.add(spacer87, gbc);
        moveTutorPanel = new JPanel();
        moveTutorPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel19.add(moveTutorPanel, gbc);
        moveTutorPanel.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, moveTutorPanel.getFont()), null));
        mtMovesPanel = new JPanel();
        mtMovesPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        moveTutorPanel.add(mtMovesPanel, gbc);
        mtMovesPanel.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtMovesPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        mtUnchangedRadioButton = new JRadioButton();
        mtUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(mtUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtUnchangedRadioButton.text"));
        mtUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 70);
        mtMovesPanel.add(mtUnchangedRadioButton, gbc);
        final JPanel spacer88 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        mtMovesPanel.add(spacer88, gbc);
        final JPanel spacer89 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mtMovesPanel.add(spacer89, gbc);
        final JPanel spacer90 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        mtMovesPanel.add(spacer90, gbc);
        mtRandomRadioButton = new JRadioButton();
        mtRandomRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(mtRandomRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtRandomRadioButton.text"));
        mtRandomRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtRandomRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mtMovesPanel.add(mtRandomRadioButton, gbc);
        mtForceGoodDamagingSlider = new JSlider();
        mtForceGoodDamagingSlider.setEnabled(false);
        mtForceGoodDamagingSlider.setMajorTickSpacing(20);
        mtForceGoodDamagingSlider.setMinorTickSpacing(5);
        mtForceGoodDamagingSlider.setPaintLabels(true);
        mtForceGoodDamagingSlider.setPaintTicks(true);
        mtForceGoodDamagingSlider.setSnapToTicks(true);
        mtForceGoodDamagingSlider.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtForceGoodDamagingSlider.toolTipText"));
        mtForceGoodDamagingSlider.setValue(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mtMovesPanel.add(mtForceGoodDamagingSlider, gbc);
        mtNoGameBreakingMovesCheckBox = new JCheckBox();
        mtNoGameBreakingMovesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(mtNoGameBreakingMovesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtNoGameBreakingMovesCheckBox.text"));
        mtNoGameBreakingMovesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtNoGameBreakingMovesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mtMovesPanel.add(mtNoGameBreakingMovesCheckBox, gbc);
        mtKeepFieldMoveTutorsCheckBox = new JCheckBox();
        mtKeepFieldMoveTutorsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(mtKeepFieldMoveTutorsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtKeepFieldMoveTutorsCheckBox.text"));
        mtKeepFieldMoveTutorsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtKeepFieldMoveTutorsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mtMovesPanel.add(mtKeepFieldMoveTutorsCheckBox, gbc);
        mtForceGoodDamagingCheckBox = new JCheckBox();
        mtForceGoodDamagingCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(mtForceGoodDamagingCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtForceGoodDamagingCheckBox.text"));
        mtForceGoodDamagingCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtForceGoodDamagingCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mtMovesPanel.add(mtForceGoodDamagingCheckBox, gbc);
        final JPanel spacer91 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        moveTutorPanel.add(spacer91, gbc);
        final JPanel spacer92 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        moveTutorPanel.add(spacer92, gbc);
        final JPanel spacer93 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        moveTutorPanel.add(spacer93, gbc);
        final JPanel spacer94 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        moveTutorPanel.add(spacer94, gbc);
        mtCompatPanel = new JPanel();
        mtCompatPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 20, 0, 0);
        moveTutorPanel.add(mtCompatPanel, gbc);
        mtCompatPanel.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtcPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        mtcUnchangedRadioButton = new JRadioButton();
        mtcUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(mtcUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtcUnchangedRadioButton.text"));
        mtcUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtcUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mtCompatPanel.add(mtcUnchangedRadioButton, gbc);
        final JPanel spacer95 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        mtCompatPanel.add(spacer95, gbc);
        final JPanel spacer96 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mtCompatPanel.add(spacer96, gbc);
        final JPanel spacer97 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        mtCompatPanel.add(spacer97, gbc);
        mtcRandomPreferSameTypeRadioButton = new JRadioButton();
        mtcRandomPreferSameTypeRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(mtcRandomPreferSameTypeRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtcRandomPreferSameTypeRadioButton.text"));
        mtcRandomPreferSameTypeRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtcRandomPreferSameTypeRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mtCompatPanel.add(mtcRandomPreferSameTypeRadioButton, gbc);
        mtcRandomCompletelyRadioButton = new JRadioButton();
        mtcRandomCompletelyRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(mtcRandomCompletelyRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtcRandomCompletelyRadioButton.text"));
        mtcRandomCompletelyRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtcRandomCompletelyRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mtCompatPanel.add(mtcRandomCompletelyRadioButton, gbc);
        mtcFullCompatibilityRadioButton = new JRadioButton();
        mtcFullCompatibilityRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(mtcFullCompatibilityRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtcFullCompatibilityRadioButton.text"));
        mtcFullCompatibilityRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtcFullCompatibilityRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        mtCompatPanel.add(mtcFullCompatibilityRadioButton, gbc);
        mtFollowEvolutionsCheckBox = new JCheckBox();
        mtFollowEvolutionsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(mtFollowEvolutionsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtFollowEvolutionsCheckBox.text"));
        mtFollowEvolutionsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtFollowEvolutionsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mtCompatPanel.add(mtFollowEvolutionsCheckBox, gbc);
        mtLevelupMoveSanityCheckBox = new JCheckBox();
        mtLevelupMoveSanityCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(mtLevelupMoveSanityCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtLevelupMoveSanityCheckBox.text"));
        mtLevelupMoveSanityCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtLevelupMoveSanityCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mtCompatPanel.add(mtLevelupMoveSanityCheckBox, gbc);
        mtNoExistLabel = new JLabel();
        mtNoExistLabel.setEnabled(true);
        this.$$$loadLabelText$$$(mtNoExistLabel, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.mtNoExistLabel.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        moveTutorPanel.add(mtNoExistLabel, gbc);
        final JPanel panel23 = new JPanel();
        panel23.setLayout(new GridBagLayout());
        panel23.setEnabled(false);
        tabbedPane1.addTab(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.itemsPanel.title"), panel23);
        final JPanel panel24 = new JPanel();
        panel24.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        panel23.add(panel24, gbc);
        panel24.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.fiPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, panel24.getFont()), null));
        fiUnchangedRadioButton = new JRadioButton();
        fiUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(fiUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.fiUnchangedRadioButton.text"));
        fiUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.fiUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 140);
        panel24.add(fiUnchangedRadioButton, gbc);
        final JPanel spacer98 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel24.add(spacer98, gbc);
        final JPanel spacer99 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel24.add(spacer99, gbc);
        final JPanel spacer100 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel24.add(spacer100, gbc);
        final JPanel spacer101 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel24.add(spacer101, gbc);
        fiShuffleRadioButton = new JRadioButton();
        fiShuffleRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(fiShuffleRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.fiShuffleRadioButton.text"));
        fiShuffleRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.fiShuffleRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel24.add(fiShuffleRadioButton, gbc);
        fiRandomRadioButton = new JRadioButton();
        fiRandomRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(fiRandomRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.fiRandomRadioButton.text"));
        fiRandomRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.fiRandomRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel24.add(fiRandomRadioButton, gbc);
        fiRandomEvenDistributionRadioButton = new JRadioButton();
        fiRandomEvenDistributionRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(fiRandomEvenDistributionRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.fiRandomEvenDistributionRadioButton.text"));
        fiRandomEvenDistributionRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.fiRandomEvenDistributionRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel24.add(fiRandomEvenDistributionRadioButton, gbc);
        fiBanBadItemsCheckBox = new JCheckBox();
        fiBanBadItemsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(fiBanBadItemsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.fiBanBadItemsCheckBox.text"));
        fiBanBadItemsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.fiBanBadItemsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel24.add(fiBanBadItemsCheckBox, gbc);
        final JPanel spacer102 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel23.add(spacer102, gbc);
        final JPanel spacer103 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel23.add(spacer103, gbc);
        final JPanel spacer104 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel23.add(spacer104, gbc);
        final JPanel spacer105 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel23.add(spacer105, gbc);
        shopItemsPanel = new JPanel();
        shopItemsPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel23.add(shopItemsPanel, gbc);
        shopItemsPanel.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, -1, shopItemsPanel.getFont()), null));
        shUnchangedRadioButton = new JRadioButton();
        shUnchangedRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(shUnchangedRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shUnchangedRadioButton.text"));
        shUnchangedRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shUnchangedRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 140);
        shopItemsPanel.add(shUnchangedRadioButton, gbc);
        final JPanel spacer106 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        shopItemsPanel.add(spacer106, gbc);
        final JPanel spacer107 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.VERTICAL;
        shopItemsPanel.add(spacer107, gbc);
        final JPanel spacer108 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        shopItemsPanel.add(spacer108, gbc);
        final JPanel spacer109 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        shopItemsPanel.add(spacer109, gbc);
        shShuffleRadioButton = new JRadioButton();
        shShuffleRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(shShuffleRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shShuffleRadioButton.text"));
        shShuffleRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shShuffleRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        shopItemsPanel.add(shShuffleRadioButton, gbc);
        shRandomRadioButton = new JRadioButton();
        shRandomRadioButton.setEnabled(false);
        this.$$$loadButtonText$$$(shRandomRadioButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shRandomRadioButton.text"));
        shRandomRadioButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shRandomRadioButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        shopItemsPanel.add(shRandomRadioButton, gbc);
        shBanOverpoweredShopItemsCheckBox = new JCheckBox();
        shBanOverpoweredShopItemsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(shBanOverpoweredShopItemsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shBanOverpoweredShopItemsCheckBox.text"));
        shBanOverpoweredShopItemsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shBanOverpoweredShopItemsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        shopItemsPanel.add(shBanOverpoweredShopItemsCheckBox, gbc);
        shBanBadItemsCheckBox = new JCheckBox();
        shBanBadItemsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(shBanBadItemsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shBanBadItemsCheckBox.text"));
        shBanBadItemsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shBanBadItemsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        shopItemsPanel.add(shBanBadItemsCheckBox, gbc);
        shBanRegularShopItemsCheckBox = new JCheckBox();
        shBanRegularShopItemsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(shBanRegularShopItemsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shBanRegularShopItemsCheckBox.text"));
        shBanRegularShopItemsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shBanRegularShopItemsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        shopItemsPanel.add(shBanRegularShopItemsCheckBox, gbc);
        shBalanceShopItemPricesCheckBox = new JCheckBox();
        shBalanceShopItemPricesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(shBalanceShopItemPricesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shBalanceShopItemPricesCheckBox.text"));
        shBalanceShopItemPricesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shBalanceShopItemPricesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        shopItemsPanel.add(shBalanceShopItemPricesCheckBox, gbc);
        shGuaranteeEvolutionItemsCheckBox = new JCheckBox();
        shGuaranteeEvolutionItemsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(shGuaranteeEvolutionItemsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shGuaranteeEvolutionItemsCheckBox.text"));
        shGuaranteeEvolutionItemsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shGuaranteeEvolutionItemsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        shopItemsPanel.add(shGuaranteeEvolutionItemsCheckBox, gbc);
        shGuaranteeXItemsCheckBox = new JCheckBox();
        shGuaranteeXItemsCheckBox.setEnabled(false);
        shGuaranteeXItemsCheckBox.setSelected(false);
        this.$$$loadButtonText$$$(shGuaranteeXItemsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shGuaranteeXItemsCheckbox.text"));
        shGuaranteeXItemsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.shGuaranteeXItemsCheckbox.tooltipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        shopItemsPanel.add(shGuaranteeXItemsCheckBox, gbc);
        baseTweaksPanel = new JPanel();
        baseTweaksPanel.setLayout(new GridBagLayout());
        baseTweaksPanel.setEnabled(false);
        tabbedPane1.addTab(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscTweaksPanel.title"), baseTweaksPanel);
        miscTweaksPanel = new JPanel();
        miscTweaksPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        baseTweaksPanel.add(miscTweaksPanel, gbc);
        miscTweaksPanel.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, -1, miscTweaksPanel.getFont()), null));
        miscBWExpPatchCheckBox = new JCheckBox();
        miscBWExpPatchCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscBWExpPatchCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscBWExpPatchCheckBox.text"));
        miscBWExpPatchCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscBWExpPatchCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscBWExpPatchCheckBox, gbc);
        final JPanel spacer110 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        miscTweaksPanel.add(spacer110, gbc);
        final JPanel spacer111 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.VERTICAL;
        miscTweaksPanel.add(spacer111, gbc);
        final JPanel spacer112 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        miscTweaksPanel.add(spacer112, gbc);
        miscNerfXAccuracyCheckBox = new JCheckBox();
        miscNerfXAccuracyCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscNerfXAccuracyCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscNerfXAccuracyCheckBox.text"));
        miscNerfXAccuracyCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscNerfXAccuracyCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscNerfXAccuracyCheckBox, gbc);
        miscFixCritRateCheckBox = new JCheckBox();
        miscFixCritRateCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscFixCritRateCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscFixCritRateCheckBox.text"));
        miscFixCritRateCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscFixCritRateCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscFixCritRateCheckBox, gbc);
        miscFastestTextCheckBox = new JCheckBox();
        miscFastestTextCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscFastestTextCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscFastestTextCheckBox.text"));
        miscFastestTextCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscFastestTextCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscFastestTextCheckBox, gbc);
        miscRunningShoesIndoorsCheckBox = new JCheckBox();
        miscRunningShoesIndoorsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscRunningShoesIndoorsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscRunningShoesIndoorsCheckBox.text"));
        miscRunningShoesIndoorsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscRunningShoesIndoorsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscRunningShoesIndoorsCheckBox, gbc);
        miscRandomizePCPotionCheckBox = new JCheckBox();
        miscRandomizePCPotionCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscRandomizePCPotionCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscRandomizePCPotionCheckBox.text"));
        miscRandomizePCPotionCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscRandomizePCPotionCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscRandomizePCPotionCheckBox, gbc);
        miscAllowPikachuEvolutionCheckBox = new JCheckBox();
        miscAllowPikachuEvolutionCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscAllowPikachuEvolutionCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscAllowPikachuEvolutionCheckBox.text"));
        miscAllowPikachuEvolutionCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscAllowPikachuEvolutionCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscAllowPikachuEvolutionCheckBox, gbc);
        miscGiveNationalDexAtCheckBox = new JCheckBox();
        miscGiveNationalDexAtCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscGiveNationalDexAtCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscGiveNationalDexAtCheckBox.text"));
        miscGiveNationalDexAtCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscGiveNationalDexAtCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscGiveNationalDexAtCheckBox, gbc);
        miscUpdateTypeEffectivenessCheckBox = new JCheckBox();
        miscUpdateTypeEffectivenessCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscUpdateTypeEffectivenessCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscUpdateTypeEffectivenessCheckBox.text"));
        miscUpdateTypeEffectivenessCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscUpdateTypeEffectivenessCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscUpdateTypeEffectivenessCheckBox, gbc);
        miscRandomizeHiddenHollowsCheckBox = new JCheckBox();
        miscRandomizeHiddenHollowsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscRandomizeHiddenHollowsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscRandomizeHiddenHollowsCheckBox.text"));
        miscRandomizeHiddenHollowsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscRandomizeHiddenHollowsCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscRandomizeHiddenHollowsCheckBox, gbc);
        miscLowerCasePokemonNamesCheckBox = new JCheckBox();
        miscLowerCasePokemonNamesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscLowerCasePokemonNamesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscLowerCasePokemonNamesCheckBox.text"));
        miscLowerCasePokemonNamesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscLowerCasePokemonNamesCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscLowerCasePokemonNamesCheckBox, gbc);
        miscRandomizeCatchingTutorialCheckBox = new JCheckBox();
        miscRandomizeCatchingTutorialCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscRandomizeCatchingTutorialCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscRandomizeCatchingTutorialCheckBox.text"));
        miscRandomizeCatchingTutorialCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscRandomizeCatchingTutorialCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscRandomizeCatchingTutorialCheckBox, gbc);
        miscBanLuckyEggCheckBox = new JCheckBox();
        miscBanLuckyEggCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscBanLuckyEggCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscBanLuckyEggCheckBox.text"));
        miscBanLuckyEggCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscBanLuckyEggCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscBanLuckyEggCheckBox, gbc);
        miscNoFreeLuckyEggCheckBox = new JCheckBox();
        miscNoFreeLuckyEggCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscNoFreeLuckyEggCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscNoFreeLuckyEggCheckBox.text"));
        miscNoFreeLuckyEggCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscNoFreeLuckyEggCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscNoFreeLuckyEggCheckBox, gbc);
        miscBanBigMoneyManiacCheckBox = new JCheckBox();
        miscBanBigMoneyManiacCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscBanBigMoneyManiacCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscBanBigMoneyManiacCheckBox.text"));
        miscBanBigMoneyManiacCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscBanBigMoneyManiacCheckBox.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscBanBigMoneyManiacCheckBox, gbc);
        mtNoneAvailableLabel = new JLabel();
        this.$$$loadLabelText$$$(mtNoneAvailableLabel, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.miscNoneAvailableLabel.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(mtNoneAvailableLabel, gbc);
        miscSOSBattlesCheckBox = new JCheckBox();
        miscSOSBattlesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscSOSBattlesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "CodeTweaks.sosBattles.name"));
        miscSOSBattlesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "CodeTweaks.sosBattles.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscSOSBattlesCheckBox, gbc);
        miscBalanceStaticLevelsCheckBox = new JCheckBox();
        miscBalanceStaticLevelsCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscBalanceStaticLevelsCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "CodeTweaks.balanceStaticLevels.name"));
        miscBalanceStaticLevelsCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "CodeTweaks.balanceStaticLevels.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscBalanceStaticLevelsCheckBox, gbc);
        miscRetainAltFormesCheckBox = new JCheckBox();
        miscRetainAltFormesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscRetainAltFormesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "CodeTweaks.retainAltFormes.name"));
        miscRetainAltFormesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "CodeTweaks.retainAltFormes.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscRetainAltFormesCheckBox, gbc);
        miscRunWithoutRunningShoesCheckBox = new JCheckBox();
        miscRunWithoutRunningShoesCheckBox.setEnabled(false);
        this.$$$loadButtonText$$$(miscRunWithoutRunningShoesCheckBox, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "CodeTweaks.runWithoutRunningShoes.name"));
        miscRunWithoutRunningShoesCheckBox.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "CodeTweaks.runWithoutRunningShoes.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        miscTweaksPanel.add(miscRunWithoutRunningShoesCheckBox, gbc);
        final JPanel spacer113 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        baseTweaksPanel.add(spacer113, gbc);
        final JPanel spacer114 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        baseTweaksPanel.add(spacer114, gbc);
        final JPanel spacer115 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        baseTweaksPanel.add(spacer115, gbc);
        final JPanel spacer116 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        baseTweaksPanel.add(spacer116, gbc);
        openROMButton = new JButton();
        openROMButton.setRequestFocusEnabled(false);
        this.$$$loadButtonText$$$(openROMButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.openROMButton.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(openROMButton, gbc);
        randomizeSaveButton = new JButton();
        this.$$$loadButtonText$$$(randomizeSaveButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.randomizeSaveButton.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(randomizeSaveButton, gbc);
        premadeSeedButton = new JButton();
        this.$$$loadButtonText$$$(premadeSeedButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.premadeSeedButton.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(premadeSeedButton, gbc);
        settingsButton = new JButton();
        this.$$$loadButtonText$$$(settingsButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.settingsButton.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(settingsButton, gbc);
        websiteLinkLabel = new JLabel();
        this.$$$loadLabelText$$$(websiteLinkLabel, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.websiteLinkLabel.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 11;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(websiteLinkLabel, gbc);
        versionLabel = new JLabel();
        Font versionLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, versionLabel.getFont());
        if (versionLabelFont != null) versionLabel.setFont(versionLabelFont);
        this.$$$loadLabelText$$$(versionLabel, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.versionLabel.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(versionLabel, gbc);
        final JPanel spacer117 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer117, gbc);
        final JPanel spacer118 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 12;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer118, gbc);
        final JPanel spacer119 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer119, gbc);
        final JPanel spacer120 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer120, gbc);
        gameMascotLabel = new JLabel();
        gameMascotLabel.setIcon(new ImageIcon(getClass().getResource("/com/dabomstew/pkrandom/newgui/emptyIcon.png")));
        gameMascotLabel.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 1;
        gbc.gridheight = 4;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(gameMascotLabel, gbc);
        saveSettingsButton = new JButton();
        saveSettingsButton.setEnabled(false);
        this.$$$loadButtonText$$$(saveSettingsButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.saveSettingsButton.text"));
        saveSettingsButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.saveSettingsButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 4;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(saveSettingsButton, gbc);
        loadSettingsButton = new JButton();
        loadSettingsButton.setEnabled(false);
        this.$$$loadButtonText$$$(loadSettingsButton, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.loadSettingsButton.text"));
        loadSettingsButton.setToolTipText(this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.loadSettingsButton.toolTipText"));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(loadSettingsButton, gbc);
        final JPanel spacer121 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer121, gbc);
        final JPanel spacer122 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer122, gbc);
        final JPanel spacer123 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer123, gbc);
        final JPanel spacer124 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer124, gbc);
        wikiLinkLabel = new JLabel();
        wikiLinkLabel.setHorizontalAlignment(10);
        this.$$$loadLabelText$$$(wikiLinkLabel, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GUI.wikiLinkLabel.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 10);
        mainPanel.add(wikiLinkLabel, gbc);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(tpUnchangedRadioButton);
        buttonGroup.add(tpRandomRadioButton);
        buttonGroup.add(tpRandomEvenDistributionRadioButton);
        buttonGroup.add(tpRandomEvenDistributionMainRadioButton);
        buttonGroup.add(tpTypeThemedRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(pbsRandomRadioButton);
        buttonGroup.add(pbsShuffleRadioButton);
        buttonGroup.add(pbsUnchangedRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(pbsLegendariesSlowRadioButton);
        buttonGroup.add(pbsStrongLegendariesSlowRadioButton);
        buttonGroup.add(pbsAllMediumFastRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(ptUnchangedRadioButton);
        buttonGroup.add(ptRandomFollowEvolutionsRadioButton);
        buttonGroup.add(ptRandomCompletelyRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(peUnchangedRadioButton);
        buttonGroup.add(peRandomRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(paUnchangedRadioButton);
        buttonGroup.add(paRandomRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(spUnchangedRadioButton);
        buttonGroup.add(spCustomRadioButton);
        buttonGroup.add(spRandomCompletelyRadioButton);
        buttonGroup.add(spRandomTwoEvosRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(stpUnchangedRadioButton);
        buttonGroup.add(stpSwapLegendariesSwapStandardsRadioButton);
        buttonGroup.add(stpRandomCompletelyRadioButton);
        buttonGroup.add(stpRandomSimilarStrengthRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(igtUnchangedRadioButton);
        buttonGroup.add(igtRandomizeGivenPokemonOnlyRadioButton);
        buttonGroup.add(igtRandomizeBothRequestedGivenRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(pmsUnchangedRadioButton);
        buttonGroup.add(pmsRandomPreferringSameTypeRadioButton);
        buttonGroup.add(pmsRandomCompletelyRadioButton);
        buttonGroup.add(pmsMetronomeOnlyModeRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(wpUnchangedRadioButton);
        buttonGroup.add(wpRandomRadioButton);
        buttonGroup.add(wpArea1To1RadioButton);
        buttonGroup.add(wpGlobal1To1RadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(wpARNoneRadioButton);
        buttonGroup.add(wpARSimilarStrengthRadioButton);
        buttonGroup.add(wpARCatchEmAllModeRadioButton);
        buttonGroup.add(wpARTypeThemeAreasRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(tmUnchangedRadioButton);
        buttonGroup.add(tmRandomRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(thcUnchangedRadioButton);
        buttonGroup.add(thcRandomPreferSameTypeRadioButton);
        buttonGroup.add(thcRandomCompletelyRadioButton);
        buttonGroup.add(thcFullCompatibilityRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(mtUnchangedRadioButton);
        buttonGroup.add(mtRandomRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(mtcUnchangedRadioButton);
        buttonGroup.add(mtcRandomPreferSameTypeRadioButton);
        buttonGroup.add(mtcRandomCompletelyRadioButton);
        buttonGroup.add(mtcFullCompatibilityRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(fiUnchangedRadioButton);
        buttonGroup.add(fiShuffleRadioButton);
        buttonGroup.add(fiRandomRadioButton);
        buttonGroup.add(fiRandomEvenDistributionRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(shUnchangedRadioButton);
        buttonGroup.add(shShuffleRadioButton);
        buttonGroup.add(shRandomRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(totpUnchangedRadioButton);
        buttonGroup.add(totpRandomRadioButton);
        buttonGroup.add(totpRandomSimilarStrengthRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(totpAllyUnchangedRadioButton);
        buttonGroup.add(totpAllyRandomRadioButton);
        buttonGroup.add(totpAllyRandomSimilarStrengthRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(totpAuraUnchangedRadioButton);
        buttonGroup.add(totpAuraRandomRadioButton);
        buttonGroup.add(totpAuraRandomSameStrengthRadioButton);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    private static Method $$$cachedGetBundleMethod$$$ = null;

    private String $$$getMessageFromBundle$$$(String path, String key) {
        ResourceBundle bundle;
        try {
            Class<?> thisClass = this.getClass();
            if ($$$cachedGetBundleMethod$$$ == null) {
                Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
                $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
            }
            bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle(path);
        }
        return bundle.getString(key);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    private enum SaveType {
        FILE, DIRECTORY, INVALID
    }

    private SaveType askForSaveType() {
        SaveType saveType = SaveType.FILE;
        if (romHandler.hasGameUpdateLoaded()) {
            String text = bundle.getString("GUI.savingWithGameUpdate");
            String url = "https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/Randomizing-the-3DS-games#managing-game-updates";
            showMessageDialogWithLink(text, url);
            saveType = SaveType.DIRECTORY;
        } else if (romHandler.generationOfPokemon() == 6 || romHandler.generationOfPokemon() == 7) {
            Object[] options3DS = {"CXI", "LayeredFS"};
            String question = "Would you like to output your 3DS game as a CXI file or as a LayeredFS directory?";
            JLabel label = new JLabel("<html><a href=\"https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/Randomizing-the-3DS-games#changes-to-saving-a-rom-when-working-with-3ds-games\">For more information, click here.</a>");
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.browse(new URI("https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/Randomizing-the-3DS-games#changes-to-saving-a-rom-when-working-with-3ds-games"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            Object[] messages = {question,label};
            int returnVal3DS = JOptionPane.showOptionDialog(frame,
                    messages,
                    "3DS Output Choice",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options3DS,
                    null);
            if (returnVal3DS < 0) {
                saveType = SaveType.INVALID;
            } else {
                saveType = SaveType.values()[returnVal3DS];
            }
        }
        return saveType;
    }

    private void applyGameUpdateMenuItemActionPerformed() {

        if (romHandler == null) return;

        gameUpdateChooser.setSelectedFile(null);
        gameUpdateChooser.setFileFilter(new GameUpdateFilter());
        int returnVal = gameUpdateChooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fh = gameUpdateChooser.getSelectedFile();

            // On the 3DS, the update has the same title ID as the base game, save for the 8th character,
            // which is 'E' instead of '0'. We can use this to detect if the update matches the game.
            String actualUpdateTitleId = Abstract3DSRomHandler.getTitleIdFromFile(fh.getAbsolutePath());
            Abstract3DSRomHandler ctrRomHandler = (Abstract3DSRomHandler) romHandler;
            String baseGameTitleId = ctrRomHandler.getTitleIdFromLoadedROM();
            char[] baseGameTitleIdChars = baseGameTitleId.toCharArray();
            baseGameTitleIdChars[7] = 'E';
            String expectedUpdateTitleId = String.valueOf(baseGameTitleIdChars);
            if (actualUpdateTitleId.equals(expectedUpdateTitleId)) {
                gameUpdates.put(romHandler.getROMCode(), fh.getAbsolutePath());
                attemptWriteConfig();
                try {
                    romHandler.loadGameUpdate(fh.getAbsolutePath());
                } catch (EncryptedROMException ex) {
                    JOptionPane.showMessageDialog(mainPanel,
                            String.format(bundle.getString("GUI.encryptedRom"), fh.getAbsolutePath()));
                }
                removeGameUpdateMenuItem.setVisible(true);
                romNameLabel.setText(romHandler.getROMName() + " (" + romHandler.getGameUpdateVersion() + ")");
                String text = String.format(bundle.getString("GUI.gameUpdateApplied"), romHandler.getROMName());
                String url = "https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/Randomizing-the-3DS-games#3ds-game-updates";
                showMessageDialogWithLink(text, url);
            } else {
                // Error: update is not for the correct game
                JOptionPane.showMessageDialog(frame, String.format(bundle.getString("GUI.nonMatchingGameUpdate"), fh.getName(), romHandler.getROMName()));
            }
        }
    }

    private void removeGameUpdateMenuItemActionPerformed() {

        if (romHandler == null) return;

        gameUpdates.remove(romHandler.getROMCode());
        attemptWriteConfig();
        romHandler.removeGameUpdate();
        removeGameUpdateMenuItem.setVisible(false);
        romNameLabel.setText(romHandler.getROMName());
    }

    private void loadGetSettingsMenuItemActionPerformed() {

        if (romHandler == null) return;

        String currentSettingsString = "Current Settings String:";
        JTextField currentSettingsStringField = new JTextField();
        currentSettingsStringField.setEditable(false);
        try {
            String theSettingsString = Version.VERSION + getCurrentSettings().toString();
            currentSettingsStringField.setColumns(Settings.LENGTH_OF_SETTINGS_DATA * 2);
            currentSettingsStringField.setText(theSettingsString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String loadSettingsString = "Load Settings String:";
        JTextField loadSettingsStringField = new JTextField();
        Object[] messages = {currentSettingsString,currentSettingsStringField,loadSettingsString,loadSettingsStringField};
        Object[] options = {"Load","Cancel"};
        int choice = JOptionPane.showOptionDialog(
                frame,
                messages,
                "Get/Load Settings String",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                null
        );
        if (choice == 0) {
            String configString = loadSettingsStringField.getText().trim();
            if (configString.length() > 0) {
                if (configString.length() < 3) {
                    JOptionPane.showMessageDialog(frame,bundle.getString("GUI.invalidSettingsString"));
                } else {
                    try {
                        int settingsStringVersionNumber = Integer.parseInt(configString.substring(0, 3));
                        if (settingsStringVersionNumber < Version.VERSION) {
                            JOptionPane.showMessageDialog(frame,bundle.getString("GUI.settingsStringOlder"));
                            Settings settings = Settings.fromString(configString.substring(3));
                            settings.tweakForRom(this.romHandler);
                            restoreStateFromSettings(settings);
                            JOptionPane.showMessageDialog(frame,bundle.getString("GUI.settingsStringLoaded"));
                        } else if (settingsStringVersionNumber > Version.VERSION) {
                            JOptionPane.showMessageDialog(frame,bundle.getString("GUI.settingsStringTooNew"));
                        } else {
                            Settings settings = Settings.fromString(configString.substring(3));
                            settings.tweakForRom(this.romHandler);
                            restoreStateFromSettings(settings);
                            JOptionPane.showMessageDialog(frame,bundle.getString("GUI.settingsStringLoaded"));
                        }
                    } catch (UnsupportedEncodingException | IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(frame,bundle.getString("GUI.invalidSettingsString"));
                    }
                }

            }
        }
    }

    private void keepOrUnloadGameAfterRandomizingMenuItemActionPerformed() {
        this.unloadGameOnSuccess = !this.unloadGameOnSuccess;
        if (this.unloadGameOnSuccess) {
            JOptionPane.showMessageDialog(frame, bundle.getString("GUI.unloadGameAfterRandomizing"));
            keepOrUnloadGameAfterRandomizingMenuItem.setText(bundle.getString("GUI.keepGameLoadedAfterRandomizingMenuItem.text"));
        } else {
            JOptionPane.showMessageDialog(frame, bundle.getString("GUI.keepGameLoadedAfterRandomizing"));
            keepOrUnloadGameAfterRandomizingMenuItem.setText(bundle.getString("GUI.unloadGameAfterRandomizingMenuItem.text"));
        }
        attemptWriteConfig();
    }

    private void showMessageDialogWithLink(String text, String url) {
        JLabel label = new JLabel("<html><a href=\"" + url + "\">For more information, click here.</a>");
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI(url));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Object[] messages = {text,label};
        JOptionPane.showMessageDialog(frame, messages);
    }

    // This is only intended to be used with the "Keep Game Loaded After Randomizing" setting; it assumes that
    // the game has already been loaded once, and we just need to reload the same game to reinitialize the
    // RomHandler. Don't use this for other purposes unless you know what you're doing.
    private void reinitializeRomHandler() {
        String currentFN = this.romHandler.loadedFilename();
        for (RomHandler.Factory rhf : checkHandlers) {
            if (rhf.isLoadable(currentFN)) {
                this.romHandler = rhf.create(RandomSource.instance());
                opDialog = new OperationDialog(bundle.getString("GUI.loadingText"), frame, true);
                Thread t = new Thread(() -> {
                    SwingUtilities.invokeLater(() -> opDialog.setVisible(true));
                    try {
                        this.romHandler.loadRom(currentFN);
                        if (gameUpdates.containsKey(this.romHandler.getROMCode())) {
                            this.romHandler.loadGameUpdate(gameUpdates.get(this.romHandler.getROMCode()));
                        }
                    } catch (Exception ex) {
                        attemptToLogException(ex, "GUI.loadFailed", "GUI.loadFailedNoLog", null, null);
                    }
                    SwingUtilities.invokeLater(() -> {
                        this.opDialog.setVisible(false);
                    });
                });
                t.start();

                return;
            }
        }
    }

    private void restoreStateFromSettings(Settings settings) {

        limitPokemonCheckBox.setSelected(settings.isLimitPokemon());
        currentRestrictions = settings.getCurrentRestrictions();
        if (currentRestrictions != null) {
            currentRestrictions.limitToGen(romHandler.generationOfPokemon());
        }
        raceModeCheckBox.setSelected(settings.isRaceMode());

        peChangeImpossibleEvosCheckBox.setSelected(settings.isChangeImpossibleEvolutions());
        mdUpdateMovesCheckBox.setSelected(settings.isUpdateMoves());
        mdUpdateComboBox.setSelectedIndex(Math.max(0,settings.getUpdateMovesToGeneration() - (romHandler.generationOfPokemon()+1)));
        tpRandomizeTrainerNamesCheckBox.setSelected(settings.isRandomizeTrainerNames());
        tpRandomizeTrainerClassNamesCheckBox.setSelected(settings.isRandomizeTrainerClassNames());

        pbsRandomRadioButton.setSelected(settings.getBaseStatisticsMod() == Settings.BaseStatisticsMod.RANDOM);
        pbsShuffleRadioButton.setSelected(settings.getBaseStatisticsMod() == Settings.BaseStatisticsMod.SHUFFLE);
        pbsUnchangedRadioButton.setSelected(settings.getBaseStatisticsMod() == Settings.BaseStatisticsMod.UNCHANGED);
        pbsFollowEvolutionsCheckBox.setSelected(settings.isBaseStatsFollowEvolutions());
        pbsUpdateBaseStatsCheckBox.setSelected(settings.isUpdateBaseStats());
        pbsUpdateComboBox.setSelectedIndex(Math.max(0,settings.getUpdateBaseStatsToGeneration() - (Math.max(6,romHandler.generationOfPokemon()+1))));
        pbsStandardizeEXPCurvesCheckBox.setSelected(settings.isStandardizeEXPCurves());
        pbsLegendariesSlowRadioButton.setSelected(settings.getExpCurveMod() == Settings.ExpCurveMod.LEGENDARIES);
        pbsStrongLegendariesSlowRadioButton.setSelected(settings.getExpCurveMod() == Settings.ExpCurveMod.STRONG_LEGENDARIES);
        pbsAllMediumFastRadioButton.setSelected(settings.getExpCurveMod() == Settings.ExpCurveMod.ALL);
        ExpCurve[] expCurves = getEXPCurvesForGeneration(romHandler.generationOfPokemon());
        int index = 0;
        for (int i = 0; i < expCurves.length; i++) {
            if (expCurves[i] == settings.getSelectedEXPCurve()) {
                index = i;
            }
        }
        pbsEXPCurveComboBox.setSelectedIndex(index);
        pbsFollowMegaEvosCheckBox.setSelected(settings.isBaseStatsFollowMegaEvolutions());

        paUnchangedRadioButton.setSelected(settings.getAbilitiesMod() == Settings.AbilitiesMod.UNCHANGED);
        paRandomRadioButton.setSelected(settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE);
        paAllowWonderGuardCheckBox.setSelected(settings.isAllowWonderGuard());
        paFollowEvolutionsCheckBox.setSelected(settings.isAbilitiesFollowEvolutions());
        paTrappingAbilitiesCheckBox.setSelected(settings.isBanTrappingAbilities());
        paNegativeAbilitiesCheckBox.setSelected(settings.isBanNegativeAbilities());
        paBadAbilitiesCheckBox.setSelected(settings.isBanBadAbilities());
        paFollowMegaEvosCheckBox.setSelected(settings.isAbilitiesFollowMegaEvolutions());
        paWeighDuplicatesTogetherCheckBox.setSelected(settings.isWeighDuplicateAbilitiesTogether());

        ptRandomFollowEvolutionsRadioButton.setSelected(settings.getTypesMod() == Settings.TypesMod.RANDOM_FOLLOW_EVOLUTIONS);
        ptRandomCompletelyRadioButton.setSelected(settings.getTypesMod() == Settings.TypesMod.COMPLETELY_RANDOM);
        ptUnchangedRadioButton.setSelected(settings.getTypesMod() == Settings.TypesMod.UNCHANGED);
        ptFollowMegaEvosCheckBox.setSelected(settings.isTypesFollowMegaEvolutions());
        pmsNoGameBreakingMovesCheckBox.setSelected(settings.doBlockBrokenMoves());

        peMakeEvolutionsEasierCheckBox.setSelected(settings.isMakeEvolutionsEasier());
        peRemoveTimeBasedEvolutionsCheckBox.setSelected(settings.isRemoveTimeBasedEvolutions());

        spCustomRadioButton.setSelected(settings.getStartersMod() == Settings.StartersMod.CUSTOM);
        spRandomCompletelyRadioButton.setSelected(settings.getStartersMod() == Settings.StartersMod.COMPLETELY_RANDOM);
        spUnchangedRadioButton.setSelected(settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
        spRandomTwoEvosRadioButton.setSelected(settings.getStartersMod() == Settings.StartersMod.RANDOM_WITH_TWO_EVOLUTIONS);
        spRandomizeStarterHeldItemsCheckBox.setSelected(settings.isRandomizeStartersHeldItems());
        spBanBadItemsCheckBox.setSelected(settings.isBanBadRandomStarterHeldItems());
        spAllowAltFormesCheckBox.setSelected(settings.isAllowStarterAltFormes());

        int[] customStarters = settings.getCustomStarters();
        spComboBox1.setSelectedIndex(customStarters[0] - 1);
        spComboBox2.setSelectedIndex(customStarters[1] - 1);
        spComboBox3.setSelectedIndex(customStarters[2] - 1);

        peUnchangedRadioButton.setSelected(settings.getEvolutionsMod() == Settings.EvolutionsMod.UNCHANGED);
        peRandomRadioButton.setSelected(settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM);
        peSimilarStrengthCheckBox.setSelected(settings.isEvosSimilarStrength());
        peSameTypingCheckBox.setSelected(settings.isEvosSameTyping());
        peLimitEvolutionsToThreeCheckBox.setSelected(settings.isEvosMaxThreeStages());
        peForceChangeCheckBox.setSelected(settings.isEvosForceChange());
        peAllowAltFormesCheckBox.setSelected(settings.isEvosAllowAltFormes());

        mdRandomizeMoveAccuracyCheckBox.setSelected(settings.isRandomizeMoveAccuracies());
        mdRandomizeMoveCategoryCheckBox.setSelected(settings.isRandomizeMoveCategory());
        mdRandomizeMovePowerCheckBox.setSelected(settings.isRandomizeMovePowers());
        mdRandomizeMovePPCheckBox.setSelected(settings.isRandomizeMovePPs());
        mdRandomizeMoveTypesCheckBox.setSelected(settings.isRandomizeMoveTypes());

        pmsRandomCompletelyRadioButton.setSelected(settings.getMovesetsMod() == Settings.MovesetsMod.COMPLETELY_RANDOM);
        pmsRandomPreferringSameTypeRadioButton.setSelected(settings.getMovesetsMod() == Settings.MovesetsMod.RANDOM_PREFER_SAME_TYPE);
        pmsUnchangedRadioButton.setSelected(settings.getMovesetsMod() == Settings.MovesetsMod.UNCHANGED);
        pmsMetronomeOnlyModeRadioButton.setSelected(settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY);
        pmsGuaranteedLevel1MovesCheckBox.setSelected(settings.isStartWithGuaranteedMoves());
        pmsGuaranteedLevel1MovesSlider.setValue(settings.getGuaranteedMoveCount());
        pmsReorderDamagingMovesCheckBox.setSelected(settings.isReorderDamagingMoves());
        pmsForceGoodDamagingCheckBox.setSelected(settings.isMovesetsForceGoodDamaging());
        pmsForceGoodDamagingSlider.setValue(settings.getMovesetsGoodDamagingPercent());
        pmsNoGameBreakingMovesCheckBox.setSelected(settings.isBlockBrokenMovesetMoves());
        pmsEvolutionMovesCheckBox.setSelected(settings.isEvolutionMovesForAll());

        tpSimilarStrengthCheckBox.setSelected(settings.isTrainersUsePokemonOfSimilarStrength());
        tpRandomRadioButton.setSelected(settings.getTrainersMod() == Settings.TrainersMod.RANDOM);
        tpRivalCarriesStarterCheckBox.setSelected(settings.isRivalCarriesStarterThroughout());
        tpTypeThemedRadioButton.setSelected(settings.getTrainersMod() == Settings.TrainersMod.TYPE_THEMED);
        tpWeightTypesCheckBox.setSelected(settings.isTrainersMatchTypingDistribution());
        tpUnchangedRadioButton.setSelected(settings.getTrainersMod() == Settings.TrainersMod.UNCHANGED);
        tpRandomEvenDistributionRadioButton.setSelected(settings.getTrainersMod() == Settings.TrainersMod.DISTRIBUTED);
        tpRandomEvenDistributionMainRadioButton.setSelected(settings.getTrainersMod() == Settings.TrainersMod.MAINPLAYTHROUGH);
        tpDontUseLegendariesCheckBox.setSelected(settings.isTrainersBlockLegendaries());
        tpNoEarlyWonderGuardCheckBox.setSelected(settings.isTrainersBlockEarlyWonderGuard());
        tpForceFullyEvolvedAtCheckBox.setSelected(settings.isTrainersForceFullyEvolved());
        tpForceFullyEvolvedAtSlider.setValue(settings.getTrainersForceFullyEvolvedLevel());
        tpPercentageLevelModifierCheckBox.setSelected(settings.isTrainersLevelModified());
        tpPercentageLevelModifierSlider.setValue(settings.getTrainersLevelModifier());
        tpAllowAlternateFormesCheckBox.setSelected(settings.isAllowTrainerAlternateFormes());
        tpSwapMegaEvosCheckBox.setSelected(settings.isSwapTrainerMegaEvos());
        tpDoubleBattleModeCheckBox.setSelected(settings.isDoubleBattleMode());
        tpBossTrainersCheckBox.setSelected(settings.getAdditionalBossTrainerPokemon() > 0);
        tpBossTrainersSpinner.setValue(settings.getAdditionalBossTrainerPokemon() > 0 ? settings.getAdditionalBossTrainerPokemon() : 1);
        tpImportantTrainersCheckBox.setSelected(settings.getAdditionalImportantTrainerPokemon() > 0);
        tpImportantTrainersSpinner.setValue(settings.getAdditionalImportantTrainerPokemon() > 0 ? settings.getAdditionalImportantTrainerPokemon() : 1);
        tpRegularTrainersCheckBox.setSelected(settings.getAdditionalRegularTrainerPokemon() > 0);
        tpRegularTrainersSpinner.setValue(settings.getAdditionalRegularTrainerPokemon() > 0 ? settings.getAdditionalRegularTrainerPokemon() : 1);
        tpRandomShinyTrainerPokemonCheckBox.setSelected(settings.isShinyChance());

        totpUnchangedRadioButton.setSelected(settings.getTotemPokemonMod() == Settings.TotemPokemonMod.UNCHANGED);
        totpRandomRadioButton.setSelected(settings.getTotemPokemonMod() == Settings.TotemPokemonMod.RANDOM);
        totpRandomSimilarStrengthRadioButton.setSelected(settings.getTotemPokemonMod() == Settings.TotemPokemonMod.SIMILAR_STRENGTH);
        totpAllyUnchangedRadioButton.setSelected(settings.getAllyPokemonMod() == Settings.AllyPokemonMod.UNCHANGED);
        totpAllyRandomRadioButton.setSelected(settings.getAllyPokemonMod() == Settings.AllyPokemonMod.RANDOM);
        totpAllyRandomSimilarStrengthRadioButton.setSelected(settings.getAllyPokemonMod() == Settings.AllyPokemonMod.SIMILAR_STRENGTH);
        totpAuraUnchangedRadioButton.setSelected(settings.getAuraMod() == Settings.AuraMod.UNCHANGED);
        totpAuraRandomRadioButton.setSelected(settings.getAuraMod() == Settings.AuraMod.RANDOM);
        totpAuraRandomSameStrengthRadioButton.setSelected(settings.getAuraMod() == Settings.AuraMod.SAME_STRENGTH);
        totpRandomizeHeldItemsCheckBox.setSelected(settings.isRandomizeTotemHeldItems());
        totpAllowAltFormesCheckBox.setSelected(settings.isAllowTotemAltFormes());
        totpPercentageLevelModifierCheckBox.setSelected(settings.isTotemLevelsModified());
        totpPercentageLevelModifierSlider.setValue(settings.getTotemLevelModifier());

        wpARCatchEmAllModeRadioButton
                .setSelected(settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.CATCH_EM_ALL);
        wpArea1To1RadioButton.setSelected(settings.getWildPokemonMod() == Settings.WildPokemonMod.AREA_MAPPING);
        wpARNoneRadioButton.setSelected(settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.NONE);
        wpARTypeThemeAreasRadioButton
                .setSelected(settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.TYPE_THEME_AREAS);
        wpGlobal1To1RadioButton.setSelected(settings.getWildPokemonMod() == Settings.WildPokemonMod.GLOBAL_MAPPING);
        wpRandomRadioButton.setSelected(settings.getWildPokemonMod() == Settings.WildPokemonMod.RANDOM);
        wpUnchangedRadioButton.setSelected(settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED);
        wpUseTimeBasedEncountersCheckBox.setSelected(settings.isUseTimeBasedEncounters());

        wpSetMinimumCatchRateCheckBox.setSelected(settings.isUseMinimumCatchRate());
        wpSetMinimumCatchRateSlider.setValue(settings.getMinimumCatchRateLevel());
        wpDontUseLegendariesCheckBox.setSelected(settings.isBlockWildLegendaries());
        wpARSimilarStrengthRadioButton
                .setSelected(settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.SIMILAR_STRENGTH);
        wpRandomizeHeldItemsCheckBox.setSelected(settings.isRandomizeWildPokemonHeldItems());
        wpBanBadItemsCheckBox.setSelected(settings.isBanBadRandomWildPokemonHeldItems());
        wpBalanceShakingGrassPokemonCheckBox.setSelected(settings.isBalanceShakingGrass());
        wpPercentageLevelModifierCheckBox.setSelected(settings.isWildLevelsModified());
        wpPercentageLevelModifierSlider.setValue(settings.getWildLevelModifier());
        wpAllowAltFormesCheckBox.setSelected(settings.isAllowWildAltFormes());

        stpUnchangedRadioButton.setSelected(settings.getStaticPokemonMod() == Settings.StaticPokemonMod.UNCHANGED);
        stpSwapLegendariesSwapStandardsRadioButton.setSelected(settings.getStaticPokemonMod() == Settings.StaticPokemonMod.RANDOM_MATCHING);
        stpRandomCompletelyRadioButton
                .setSelected(settings.getStaticPokemonMod() == Settings.StaticPokemonMod.COMPLETELY_RANDOM);
        stpRandomSimilarStrengthRadioButton
                .setSelected(settings.getStaticPokemonMod() == Settings.StaticPokemonMod.SIMILAR_STRENGTH);
        stpLimitMainGameLegendariesCheckBox.setSelected(settings.isLimitMainGameLegendaries());
        stpRandomize600BSTCheckBox.setSelected(settings.isLimit600());
        stpAllowAltFormesCheckBox.setSelected(settings.isAllowStaticAltFormes());
        stpSwapMegaEvosCheckBox.setSelected(settings.isSwapStaticMegaEvos());
        stpPercentageLevelModifierCheckBox.setSelected(settings.isStaticLevelModified());
        stpPercentageLevelModifierSlider.setValue(settings.getStaticLevelModifier());

        thcRandomCompletelyRadioButton
                .setSelected(settings.getTmsHmsCompatibilityMod() == Settings.TMsHMsCompatibilityMod.COMPLETELY_RANDOM);
        thcRandomPreferSameTypeRadioButton
                .setSelected(settings.getTmsHmsCompatibilityMod() == Settings.TMsHMsCompatibilityMod.RANDOM_PREFER_TYPE);
        thcUnchangedRadioButton
                .setSelected(settings.getTmsHmsCompatibilityMod() == Settings.TMsHMsCompatibilityMod.UNCHANGED);
        tmRandomRadioButton.setSelected(settings.getTmsMod() == Settings.TMsMod.RANDOM);
        tmUnchangedRadioButton.setSelected(settings.getTmsMod() == Settings.TMsMod.UNCHANGED);
        tmLevelupMoveSanityCheckBox.setSelected(settings.isTmLevelUpMoveSanity());
        tmKeepFieldMoveTMsCheckBox.setSelected(settings.isKeepFieldMoveTMs());
        thcFullCompatibilityRadioButton.setSelected(settings.getTmsHmsCompatibilityMod() == Settings.TMsHMsCompatibilityMod.FULL);
        tmFullHMCompatibilityCheckBox.setSelected(settings.isFullHMCompat());
        tmForceGoodDamagingCheckBox.setSelected(settings.isTmsForceGoodDamaging());
        tmForceGoodDamagingSlider.setValue(settings.getTmsGoodDamagingPercent());
        tmNoGameBreakingMovesCheckBox.setSelected(settings.isBlockBrokenTMMoves());
        tmFollowEvolutionsCheckBox.setSelected(settings.isTmsFollowEvolutions());

        mtcRandomCompletelyRadioButton
                .setSelected(settings.getMoveTutorsCompatibilityMod() == Settings.MoveTutorsCompatibilityMod.COMPLETELY_RANDOM);
        mtcRandomPreferSameTypeRadioButton
                .setSelected(settings.getMoveTutorsCompatibilityMod() == Settings.MoveTutorsCompatibilityMod.RANDOM_PREFER_TYPE);
        mtcUnchangedRadioButton
                .setSelected(settings.getMoveTutorsCompatibilityMod() == Settings.MoveTutorsCompatibilityMod.UNCHANGED);
        mtRandomRadioButton.setSelected(settings.getMoveTutorMovesMod() == Settings.MoveTutorMovesMod.RANDOM);
        mtUnchangedRadioButton.setSelected(settings.getMoveTutorMovesMod() == Settings.MoveTutorMovesMod.UNCHANGED);
        mtLevelupMoveSanityCheckBox.setSelected(settings.isTutorLevelUpMoveSanity());
        mtKeepFieldMoveTutorsCheckBox.setSelected(settings.isKeepFieldMoveTutors());
        mtcFullCompatibilityRadioButton
                .setSelected(settings.getMoveTutorsCompatibilityMod() == Settings.MoveTutorsCompatibilityMod.FULL);
        mtForceGoodDamagingCheckBox.setSelected(settings.isTutorsForceGoodDamaging());
        mtForceGoodDamagingSlider.setValue(settings.getTutorsGoodDamagingPercent());
        mtNoGameBreakingMovesCheckBox.setSelected(settings.isBlockBrokenTutorMoves());
        mtFollowEvolutionsCheckBox.setSelected(settings.isTutorFollowEvolutions());

        igtRandomizeBothRequestedGivenRadioButton
                .setSelected(settings.getInGameTradesMod() == Settings.InGameTradesMod.RANDOMIZE_GIVEN_AND_REQUESTED);
        igtRandomizeGivenPokemonOnlyRadioButton.setSelected(settings.getInGameTradesMod() == Settings.InGameTradesMod.RANDOMIZE_GIVEN);
        igtRandomizeItemsCheckBox.setSelected(settings.isRandomizeInGameTradesItems());
        igtRandomizeIVsCheckBox.setSelected(settings.isRandomizeInGameTradesIVs());
        igtRandomizeNicknamesCheckBox.setSelected(settings.isRandomizeInGameTradesNicknames());
        igtRandomizeOTsCheckBox.setSelected(settings.isRandomizeInGameTradesOTs());
        igtUnchangedRadioButton.setSelected(settings.getInGameTradesMod() == Settings.InGameTradesMod.UNCHANGED);

        fiRandomRadioButton.setSelected(settings.getFieldItemsMod() == Settings.FieldItemsMod.RANDOM);
        fiRandomEvenDistributionRadioButton.setSelected(settings.getFieldItemsMod() == Settings.FieldItemsMod.RANDOM_EVEN);
        fiShuffleRadioButton.setSelected(settings.getFieldItemsMod() == Settings.FieldItemsMod.SHUFFLE);
        fiUnchangedRadioButton.setSelected(settings.getFieldItemsMod() == Settings.FieldItemsMod.UNCHANGED);
        fiBanBadItemsCheckBox.setSelected(settings.isBanBadRandomFieldItems());

        shRandomRadioButton.setSelected(settings.getShopItemsMod() == Settings.ShopItemsMod.RANDOM);
        shShuffleRadioButton.setSelected(settings.getShopItemsMod() == Settings.ShopItemsMod.SHUFFLE);
        shUnchangedRadioButton.setSelected(settings.getShopItemsMod() == Settings.ShopItemsMod.UNCHANGED);
        shBanBadItemsCheckBox.setSelected(settings.isBanBadRandomShopItems());
        shBanRegularShopItemsCheckBox.setSelected(settings.isBanRegularShopItems());
        shBanOverpoweredShopItemsCheckBox.setSelected(settings.isBanOPShopItems());
        shBalanceShopItemPricesCheckBox.setSelected(settings.isBalanceShopPrices());
        shGuaranteeEvolutionItemsCheckBox.setSelected(settings.isGuaranteeEvolutionItems());
        shGuaranteeXItemsCheckBox.setSelected(settings.isGuaranteeXItems());


        int mtsSelected = settings.getCurrentMiscTweaks();
        int mtCount = MiscTweak.allTweaks.size();

        for (int mti = 0; mti < mtCount; mti++) {
            MiscTweak mt = MiscTweak.allTweaks.get(mti);
            JCheckBox mtCB = tweakCheckBoxes.get(mti);
            mtCB.setSelected((mtsSelected & mt.getValue()) != 0);
        }

        this.enableOrDisableSubControls();
    }

    private Settings createSettingsFromState(CustomNamesSet customNames) {
        Settings settings = new Settings();
        settings.setRomName(this.romHandler.getROMName());

        settings.setLimitPokemon(limitPokemonCheckBox.isSelected() && limitPokemonCheckBox.isVisible());
        settings.setCurrentRestrictions(currentRestrictions);
        settings.setRaceMode(raceModeCheckBox.isSelected());

        settings.setChangeImpossibleEvolutions(peChangeImpossibleEvosCheckBox.isSelected() && peChangeImpossibleEvosCheckBox.isVisible());
        settings.setUpdateMoves(mdUpdateMovesCheckBox.isSelected() && mdUpdateMovesCheckBox.isVisible());
        settings.setUpdateMovesToGeneration(mdUpdateComboBox.getSelectedIndex() + (romHandler.generationOfPokemon()+1));
        settings.setRandomizeTrainerNames(tpRandomizeTrainerNamesCheckBox.isSelected());
        settings.setRandomizeTrainerClassNames(tpRandomizeTrainerClassNamesCheckBox.isSelected());

        settings.setBaseStatisticsMod(pbsUnchangedRadioButton.isSelected(), pbsShuffleRadioButton.isSelected(),
                pbsRandomRadioButton.isSelected());
        settings.setBaseStatsFollowEvolutions(pbsFollowEvolutionsCheckBox.isSelected());
        settings.setUpdateBaseStats(pbsUpdateBaseStatsCheckBox.isSelected() && pbsUpdateBaseStatsCheckBox.isVisible());
        settings.setUpdateBaseStatsToGeneration(pbsUpdateComboBox.getSelectedIndex() + (Math.max(6,romHandler.generationOfPokemon()+1)));
        settings.setStandardizeEXPCurves(pbsStandardizeEXPCurvesCheckBox.isSelected());
        settings.setExpCurveMod(pbsLegendariesSlowRadioButton.isSelected(), pbsStrongLegendariesSlowRadioButton.isSelected(),
                pbsAllMediumFastRadioButton.isSelected());
        ExpCurve[] expCurves = getEXPCurvesForGeneration(romHandler.generationOfPokemon());
        settings.setSelectedEXPCurve(expCurves[pbsEXPCurveComboBox.getSelectedIndex()]);
        settings.setBaseStatsFollowMegaEvolutions(pbsFollowMegaEvosCheckBox.isSelected() && pbsFollowMegaEvosCheckBox.isVisible());

        settings.setAbilitiesMod(paUnchangedRadioButton.isSelected(), paRandomRadioButton.isSelected());
        settings.setAllowWonderGuard(paAllowWonderGuardCheckBox.isSelected());
        settings.setAbilitiesFollowEvolutions(paFollowEvolutionsCheckBox.isSelected());
        settings.setBanTrappingAbilities(paTrappingAbilitiesCheckBox.isSelected());
        settings.setBanNegativeAbilities(paNegativeAbilitiesCheckBox.isSelected());
        settings.setBanBadAbilities(paBadAbilitiesCheckBox.isSelected());
        settings.setAbilitiesFollowMegaEvolutions(paFollowMegaEvosCheckBox.isSelected());
        settings.setWeighDuplicateAbilitiesTogether(paWeighDuplicatesTogetherCheckBox.isSelected());

        settings.setTypesMod(ptUnchangedRadioButton.isSelected(), ptRandomFollowEvolutionsRadioButton.isSelected(),
                ptRandomCompletelyRadioButton.isSelected());
        settings.setTypesFollowMegaEvolutions(ptFollowMegaEvosCheckBox.isSelected() && ptFollowMegaEvosCheckBox.isVisible());
        settings.setBlockBrokenMovesetMoves(pmsNoGameBreakingMovesCheckBox.isSelected());

        settings.setMakeEvolutionsEasier(peMakeEvolutionsEasierCheckBox.isSelected());
        settings.setRemoveTimeBasedEvolutions(peRemoveTimeBasedEvolutionsCheckBox.isSelected());

        settings.setStartersMod(spUnchangedRadioButton.isSelected(), spCustomRadioButton.isSelected(), spRandomCompletelyRadioButton.isSelected(),
                spRandomTwoEvosRadioButton.isSelected());
        settings.setRandomizeStartersHeldItems(spRandomizeStarterHeldItemsCheckBox.isSelected() && spRandomizeStarterHeldItemsCheckBox.isVisible());
        settings.setBanBadRandomStarterHeldItems(spBanBadItemsCheckBox.isSelected() && spBanBadItemsCheckBox.isVisible());
        settings.setAllowStarterAltFormes(spAllowAltFormesCheckBox.isSelected() && spAllowAltFormesCheckBox.isVisible());

        int[] customStarters = new int[] { spComboBox1.getSelectedIndex() + 1,
                spComboBox2.getSelectedIndex() + 1, spComboBox3.getSelectedIndex() + 1 };
        settings.setCustomStarters(customStarters);

        settings.setEvolutionsMod(peUnchangedRadioButton.isSelected(), peRandomRadioButton.isSelected());
        settings.setEvosSimilarStrength(peSimilarStrengthCheckBox.isSelected());
        settings.setEvosSameTyping(peSameTypingCheckBox.isSelected());
        settings.setEvosMaxThreeStages(peLimitEvolutionsToThreeCheckBox.isSelected());
        settings.setEvosForceChange(peForceChangeCheckBox.isSelected());
        settings.setEvosAllowAltFormes(peAllowAltFormesCheckBox.isSelected() && peAllowAltFormesCheckBox.isVisible());

        settings.setRandomizeMoveAccuracies(mdRandomizeMoveAccuracyCheckBox.isSelected());
        settings.setRandomizeMoveCategory(mdRandomizeMoveCategoryCheckBox.isSelected());
        settings.setRandomizeMovePowers(mdRandomizeMovePowerCheckBox.isSelected());
        settings.setRandomizeMovePPs(mdRandomizeMovePPCheckBox.isSelected());
        settings.setRandomizeMoveTypes(mdRandomizeMoveTypesCheckBox.isSelected());

        settings.setMovesetsMod(pmsUnchangedRadioButton.isSelected(), pmsRandomPreferringSameTypeRadioButton.isSelected(),
                pmsRandomCompletelyRadioButton.isSelected(), pmsMetronomeOnlyModeRadioButton.isSelected());
        settings.setStartWithGuaranteedMoves(pmsGuaranteedLevel1MovesCheckBox.isSelected() && pmsGuaranteedLevel1MovesCheckBox.isVisible());
        settings.setGuaranteedMoveCount(pmsGuaranteedLevel1MovesSlider.getValue());
        settings.setReorderDamagingMoves(pmsReorderDamagingMovesCheckBox.isSelected());

        settings.setMovesetsForceGoodDamaging(pmsForceGoodDamagingCheckBox.isSelected());
        settings.setMovesetsGoodDamagingPercent(pmsForceGoodDamagingSlider.getValue());
        settings.setBlockBrokenMovesetMoves(pmsNoGameBreakingMovesCheckBox.isSelected());
        settings.setEvolutionMovesForAll(pmsEvolutionMovesCheckBox.isSelected());

        settings.setTrainersMod(tpUnchangedRadioButton.isSelected(), tpRandomRadioButton.isSelected(), tpRandomEvenDistributionRadioButton.isSelected(), tpRandomEvenDistributionMainRadioButton.isSelected(), tpTypeThemedRadioButton.isSelected());
        settings.setTrainersUsePokemonOfSimilarStrength(tpSimilarStrengthCheckBox.isSelected());
        settings.setRivalCarriesStarterThroughout(tpRivalCarriesStarterCheckBox.isSelected());
        settings.setTrainersMatchTypingDistribution(tpWeightTypesCheckBox.isSelected());
        settings.setTrainersBlockLegendaries(tpDontUseLegendariesCheckBox.isSelected());
        settings.setTrainersBlockEarlyWonderGuard(tpNoEarlyWonderGuardCheckBox.isSelected());
        settings.setTrainersForceFullyEvolved(tpForceFullyEvolvedAtCheckBox.isSelected());
        settings.setTrainersForceFullyEvolvedLevel(tpForceFullyEvolvedAtSlider.getValue());
        settings.setTrainersLevelModified(tpPercentageLevelModifierCheckBox.isSelected());
        settings.setTrainersLevelModifier(tpPercentageLevelModifierSlider.getValue());
        settings.setAllowTrainerAlternateFormes(tpAllowAlternateFormesCheckBox.isSelected() && tpAllowAlternateFormesCheckBox.isVisible());
        settings.setSwapTrainerMegaEvos(tpSwapMegaEvosCheckBox.isSelected() && tpSwapMegaEvosCheckBox.isVisible());
        settings.setDoubleBattleMode(tpDoubleBattleModeCheckBox.isVisible() && tpDoubleBattleModeCheckBox.isSelected());
        settings.setAdditionalBossTrainerPokemon(tpBossTrainersCheckBox.isVisible() && tpBossTrainersCheckBox.isSelected() ? (int)tpBossTrainersSpinner.getValue() : 0);
        settings.setAdditionalImportantTrainerPokemon(tpImportantTrainersCheckBox.isVisible() && tpImportantTrainersCheckBox.isSelected() ? (int)tpImportantTrainersSpinner.getValue() : 0);
        settings.setAdditionalRegularTrainerPokemon(tpRegularTrainersCheckBox.isVisible() && tpRegularTrainersCheckBox.isSelected() ? (int)tpRegularTrainersSpinner.getValue() : 0);
        settings.setShinyChance(tpRandomShinyTrainerPokemonCheckBox.isVisible() && tpRandomShinyTrainerPokemonCheckBox.isSelected());

        settings.setTotemPokemonMod(totpUnchangedRadioButton.isSelected(), totpRandomRadioButton.isSelected(), totpRandomSimilarStrengthRadioButton.isSelected());
        settings.setAllyPokemonMod(totpAllyUnchangedRadioButton.isSelected(), totpAllyRandomRadioButton.isSelected(), totpAllyRandomSimilarStrengthRadioButton.isSelected());
        settings.setAuraMod(totpAuraUnchangedRadioButton.isSelected(), totpAuraRandomRadioButton.isSelected(), totpAuraRandomSameStrengthRadioButton.isSelected());
        settings.setRandomizeTotemHeldItems(totpRandomizeHeldItemsCheckBox.isSelected());
        settings.setAllowTotemAltFormes(totpAllowAltFormesCheckBox.isSelected());
        settings.setTotemLevelsModified(totpPercentageLevelModifierCheckBox.isSelected());
        settings.setTotemLevelModifier(totpPercentageLevelModifierSlider.getValue());

        settings.setWildPokemonMod(wpUnchangedRadioButton.isSelected(), wpRandomRadioButton.isSelected(), wpArea1To1RadioButton.isSelected(),
                wpGlobal1To1RadioButton.isSelected());
        settings.setWildPokemonRestrictionMod(wpARNoneRadioButton.isSelected(), wpARSimilarStrengthRadioButton.isSelected(),
                wpARCatchEmAllModeRadioButton.isSelected(), wpARTypeThemeAreasRadioButton.isSelected());
        settings.setUseTimeBasedEncounters(wpUseTimeBasedEncountersCheckBox.isSelected());
        settings.setUseMinimumCatchRate(wpSetMinimumCatchRateCheckBox.isSelected());
        settings.setMinimumCatchRateLevel(wpSetMinimumCatchRateSlider.getValue());
        settings.setBlockWildLegendaries(wpDontUseLegendariesCheckBox.isSelected());
        settings.setRandomizeWildPokemonHeldItems(wpRandomizeHeldItemsCheckBox.isSelected() && wpRandomizeHeldItemsCheckBox.isVisible());
        settings.setBanBadRandomWildPokemonHeldItems(wpBanBadItemsCheckBox.isSelected() && wpBanBadItemsCheckBox.isVisible());
        settings.setBalanceShakingGrass(wpBalanceShakingGrassPokemonCheckBox.isSelected() && wpBalanceShakingGrassPokemonCheckBox.isVisible());
        settings.setWildLevelsModified(wpPercentageLevelModifierCheckBox.isSelected());
        settings.setWildLevelModifier(wpPercentageLevelModifierSlider.getValue());
        settings.setAllowWildAltFormes(wpAllowAltFormesCheckBox.isSelected() && wpAllowAltFormesCheckBox.isVisible());

        settings.setStaticPokemonMod(stpUnchangedRadioButton.isSelected(), stpSwapLegendariesSwapStandardsRadioButton.isSelected(),
                stpRandomCompletelyRadioButton.isSelected(), stpRandomSimilarStrengthRadioButton.isSelected());
        settings.setLimitMainGameLegendaries(stpLimitMainGameLegendariesCheckBox.isSelected() && stpLimitMainGameLegendariesCheckBox.isVisible());
        settings.setLimit600(stpRandomize600BSTCheckBox.isSelected());
        settings.setAllowStaticAltFormes(stpAllowAltFormesCheckBox.isSelected() && stpAllowAltFormesCheckBox.isVisible());
        settings.setSwapStaticMegaEvos(stpSwapMegaEvosCheckBox.isSelected() && stpSwapMegaEvosCheckBox.isVisible());
        settings.setStaticLevelModified(stpPercentageLevelModifierCheckBox.isSelected());
        settings.setStaticLevelModifier(stpPercentageLevelModifierSlider.getValue());

        settings.setTmsMod(tmUnchangedRadioButton.isSelected(), tmRandomRadioButton.isSelected());

        settings.setTmsHmsCompatibilityMod(thcUnchangedRadioButton.isSelected(), thcRandomPreferSameTypeRadioButton.isSelected(),
                thcRandomCompletelyRadioButton.isSelected(), thcFullCompatibilityRadioButton.isSelected());
        settings.setTmLevelUpMoveSanity(tmLevelupMoveSanityCheckBox.isSelected());
        settings.setKeepFieldMoveTMs(tmKeepFieldMoveTMsCheckBox.isSelected());
        settings.setFullHMCompat(tmFullHMCompatibilityCheckBox.isSelected() && tmFullHMCompatibilityCheckBox.isVisible());
        settings.setTmsForceGoodDamaging(tmForceGoodDamagingCheckBox.isSelected());
        settings.setTmsGoodDamagingPercent(tmForceGoodDamagingSlider.getValue());
        settings.setBlockBrokenTMMoves(tmNoGameBreakingMovesCheckBox.isSelected());
        settings.setTmsFollowEvolutions(tmFollowEvolutionsCheckBox.isSelected());

        settings.setMoveTutorMovesMod(mtUnchangedRadioButton.isSelected(), mtRandomRadioButton.isSelected());
        settings.setMoveTutorsCompatibilityMod(mtcUnchangedRadioButton.isSelected(), mtcRandomPreferSameTypeRadioButton.isSelected(),
                mtcRandomCompletelyRadioButton.isSelected(), mtcFullCompatibilityRadioButton.isSelected());
        settings.setTutorLevelUpMoveSanity(mtLevelupMoveSanityCheckBox.isSelected());
        settings.setKeepFieldMoveTutors(mtKeepFieldMoveTutorsCheckBox.isSelected());
        settings.setTutorsForceGoodDamaging(mtForceGoodDamagingCheckBox.isSelected());
        settings.setTutorsGoodDamagingPercent(mtForceGoodDamagingSlider.getValue());
        settings.setBlockBrokenTutorMoves(mtNoGameBreakingMovesCheckBox.isSelected());
        settings.setTutorFollowEvolutions(mtFollowEvolutionsCheckBox.isSelected());

        settings.setInGameTradesMod(igtUnchangedRadioButton.isSelected(), igtRandomizeGivenPokemonOnlyRadioButton.isSelected(), igtRandomizeBothRequestedGivenRadioButton.isSelected());
        settings.setRandomizeInGameTradesItems(igtRandomizeItemsCheckBox.isSelected());
        settings.setRandomizeInGameTradesIVs(igtRandomizeIVsCheckBox.isSelected());
        settings.setRandomizeInGameTradesNicknames(igtRandomizeNicknamesCheckBox.isSelected());
        settings.setRandomizeInGameTradesOTs(igtRandomizeOTsCheckBox.isSelected());

        settings.setFieldItemsMod(fiUnchangedRadioButton.isSelected(), fiShuffleRadioButton.isSelected(), fiRandomRadioButton.isSelected(), fiRandomEvenDistributionRadioButton.isSelected());
        settings.setBanBadRandomFieldItems(fiBanBadItemsCheckBox.isSelected());

        settings.setShopItemsMod(shUnchangedRadioButton.isSelected(), shShuffleRadioButton.isSelected(), shRandomRadioButton.isSelected());

        settings.setBanBadRandomShopItems(shBanBadItemsCheckBox.isSelected());
        settings.setBanRegularShopItems(shBanRegularShopItemsCheckBox.isSelected());
        settings.setBanOPShopItems(shBanOverpoweredShopItemsCheckBox.isSelected());
        settings.setBalanceShopPrices(shBalanceShopItemPricesCheckBox.isSelected());
        settings.setGuaranteeEvolutionItems(shGuaranteeEvolutionItemsCheckBox.isSelected());
        settings.setGuaranteeXItems(shGuaranteeXItemsCheckBox.isSelected());


        int currentMiscTweaks = 0;
        int mtCount = MiscTweak.allTweaks.size();

        for (int mti = 0; mti < mtCount; mti++) {
            MiscTweak mt = MiscTweak.allTweaks.get(mti);
            JCheckBox mtCB = tweakCheckBoxes.get(mti);
            if (mtCB.isSelected()) {
                currentMiscTweaks |= mt.getValue();
            }
        }

        settings.setCurrentMiscTweaks(currentMiscTweaks);

        settings.setCustomNames(customNames);

        return settings;
    }

    private Settings getCurrentSettings() throws IOException {
        return createSettingsFromState(FileFunctions.getCustomNames());
    }

    private void attemptToLogException(Exception ex, String baseMessageKey, String noLogMessageKey,
                                       String settingsString, String seedString) {
        attemptToLogException(ex, baseMessageKey, noLogMessageKey, false, settingsString, seedString);
    }

    private void attemptToLogException(Exception ex, String baseMessageKey, String noLogMessageKey, boolean showMessage,
                                       String settingsString, String seedString) {

        // Make sure the operation dialog doesn't show up over the error
        // dialog
        SwingUtilities.invokeLater(() -> NewRandomizerGUI.this.opDialog.setVisible(false));

        Date now = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        try {
            String errlog = "error_" + ft.format(now) + ".txt";
            PrintStream ps = new PrintStream(new FileOutputStream(errlog));
            ps.println("Randomizer Version: " + Version.VERSION_STRING);
            if (seedString != null) {
                ps.println("Seed: " + seedString);
            }
            if (settingsString != null) {
                ps.println("Settings String: " + Version.VERSION + settingsString);
            }
            ps.println("Java Version: " + System.getProperty("java.version") + ", " + System.getProperty("java.vm.name"));
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
            ps.println();
            ps.println("--ROM Diagnostics--");
            romHandler.printRomDiagnostics(ps);
            System.setErr(e1);
            ps.close();
            if (showMessage) {
                JOptionPane.showMessageDialog(mainPanel,
                        String.format(bundle.getString(baseMessageKey), ex.getMessage(), errlog));
            } else {
                JOptionPane.showMessageDialog(mainPanel, String.format(bundle.getString(baseMessageKey), errlog));
            }
        } catch (Exception logex) {
            if (showMessage) {
                JOptionPane.showMessageDialog(mainPanel, String.format(bundle.getString(noLogMessageKey), ex.getMessage()));
            } else {
                JOptionPane.showMessageDialog(mainPanel, bundle.getString(noLogMessageKey));
            }
        }
    }

    public String getValidRequiredROMName(String config, CustomNamesSet customNames)
            throws UnsupportedEncodingException, InvalidSupplementFilesException {
        try {
            Utils.validatePresetSupplementFiles(config, customNames);
        } catch (InvalidSupplementFilesException e) {
            switch (e.getType()) {
                case CUSTOM_NAMES:
                    JOptionPane.showMessageDialog(null, bundle.getString("GUI.presetDifferentCustomNames"));
                    break;
                default:
                    throw e;
            }
        }
        byte[] data = Base64.getDecoder().decode(config);

        int nameLength = data[Settings.LENGTH_OF_SETTINGS_DATA] & 0xFF;
        if (data.length != Settings.LENGTH_OF_SETTINGS_DATA + 9 + nameLength) {
            return null; // not valid length
        }
        return new String(data, Settings.LENGTH_OF_SETTINGS_DATA + 1, nameLength, "US-ASCII");
    }

    private void initialState() {

        romNameLabel.setText(bundle.getString("GUI.noRomLoaded"));
        romCodeLabel.setText("");
        romSupportLabel.setText("");

        gameMascotLabel.setIcon(emptyIcon);

        limitPokemonCheckBox.setVisible(true);
        limitPokemonCheckBox.setEnabled(false);
        limitPokemonCheckBox.setSelected(false);
        limitPokemonButton.setVisible(true);
        limitPokemonButton.setEnabled(false);
        raceModeCheckBox.setVisible(true);
        raceModeCheckBox.setEnabled(false);
        raceModeCheckBox.setSelected(false);

        openROMButton.setVisible(true);
        openROMButton.setEnabled(true);
        openROMButton.setSelected(false);
        randomizeSaveButton.setVisible(true);
        randomizeSaveButton.setEnabled(true);
        randomizeSaveButton.setSelected(false);
        premadeSeedButton.setVisible(true);
        premadeSeedButton.setEnabled(true);
        premadeSeedButton.setSelected(false);
        settingsButton.setVisible(true);
        settingsButton.setEnabled(true);
        settingsButton.setSelected(false);

        loadSettingsButton.setVisible(true);
        loadSettingsButton.setEnabled(false);
        loadSettingsButton.setSelected(false);
        saveSettingsButton.setVisible(true);
        saveSettingsButton.setEnabled(false);
        saveSettingsButton.setSelected(false);
        pbsUnchangedRadioButton.setVisible(true);
        pbsUnchangedRadioButton.setEnabled(false);
        pbsUnchangedRadioButton.setSelected(false);
        pbsShuffleRadioButton.setVisible(true);
        pbsShuffleRadioButton.setEnabled(false);
        pbsShuffleRadioButton.setSelected(false);
        pbsRandomRadioButton.setVisible(true);
        pbsRandomRadioButton.setEnabled(false);
        pbsRandomRadioButton.setSelected(false);
        pbsLegendariesSlowRadioButton.setVisible(true);
        pbsLegendariesSlowRadioButton.setEnabled(false);
        pbsLegendariesSlowRadioButton.setSelected(false);
        pbsStrongLegendariesSlowRadioButton.setVisible(true);
        pbsStrongLegendariesSlowRadioButton.setEnabled(false);
        pbsStrongLegendariesSlowRadioButton.setSelected(false);
        pbsAllMediumFastRadioButton.setVisible(true);
        pbsAllMediumFastRadioButton.setEnabled(false);
        pbsAllMediumFastRadioButton.setSelected(false);
        pbsStandardizeEXPCurvesCheckBox.setVisible(true);
        pbsStandardizeEXPCurvesCheckBox.setEnabled(false);
        pbsStandardizeEXPCurvesCheckBox.setSelected(false);
        pbsEXPCurveComboBox.setVisible(true);
        pbsEXPCurveComboBox.setEnabled(false);
        pbsEXPCurveComboBox.setSelectedIndex(0);
        pbsEXPCurveComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "Medium Fast" }));
        pbsFollowEvolutionsCheckBox.setVisible(true);
        pbsFollowEvolutionsCheckBox.setEnabled(false);
        pbsFollowEvolutionsCheckBox.setSelected(false);
        pbsUpdateBaseStatsCheckBox.setVisible(true);
        pbsUpdateBaseStatsCheckBox.setEnabled(false);
        pbsUpdateBaseStatsCheckBox.setSelected(false);
        pbsFollowMegaEvosCheckBox.setVisible(true);
        pbsFollowMegaEvosCheckBox.setEnabled(false);
        pbsFollowMegaEvosCheckBox.setSelected(false);
        pbsUpdateComboBox.setVisible(true);
        pbsUpdateComboBox.setEnabled(false);
        pbsUpdateComboBox.setSelectedIndex(0);
        pbsUpdateComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "--" }));
        ptUnchangedRadioButton.setVisible(true);
        ptUnchangedRadioButton.setEnabled(false);
        ptUnchangedRadioButton.setSelected(false);
        ptRandomFollowEvolutionsRadioButton.setVisible(true);
        ptRandomFollowEvolutionsRadioButton.setEnabled(false);
        ptRandomFollowEvolutionsRadioButton.setSelected(false);
        ptRandomCompletelyRadioButton.setVisible(true);
        ptRandomCompletelyRadioButton.setEnabled(false);
        ptRandomCompletelyRadioButton.setSelected(false);
        ptFollowMegaEvosCheckBox.setVisible(true);
        ptFollowMegaEvosCheckBox.setEnabled(false);
        ptFollowMegaEvosCheckBox.setSelected(false);
        pokemonAbilitiesPanel.setVisible(true);
        paUnchangedRadioButton.setVisible(true);
        paUnchangedRadioButton.setEnabled(false);
        paUnchangedRadioButton.setSelected(false);
        paRandomRadioButton.setVisible(true);
        paRandomRadioButton.setEnabled(false);
        paRandomRadioButton.setSelected(false);
        paAllowWonderGuardCheckBox.setVisible(true);
        paAllowWonderGuardCheckBox.setEnabled(false);
        paAllowWonderGuardCheckBox.setSelected(false);
        paFollowEvolutionsCheckBox.setVisible(true);
        paFollowEvolutionsCheckBox.setEnabled(false);
        paFollowEvolutionsCheckBox.setSelected(false);
        paTrappingAbilitiesCheckBox.setVisible(true);
        paTrappingAbilitiesCheckBox.setEnabled(false);
        paTrappingAbilitiesCheckBox.setSelected(false);
        paNegativeAbilitiesCheckBox.setVisible(true);
        paNegativeAbilitiesCheckBox.setEnabled(false);
        paNegativeAbilitiesCheckBox.setSelected(false);
        paBadAbilitiesCheckBox.setVisible(true);
        paBadAbilitiesCheckBox.setEnabled(false);
        paBadAbilitiesCheckBox.setSelected(false);
        paFollowMegaEvosCheckBox.setVisible(true);
        paFollowMegaEvosCheckBox.setEnabled(false);
        paFollowMegaEvosCheckBox.setSelected(false);
        paWeighDuplicatesTogetherCheckBox.setVisible(true);
        paWeighDuplicatesTogetherCheckBox.setEnabled(false);
        paWeighDuplicatesTogetherCheckBox.setSelected(false);
        peUnchangedRadioButton.setVisible(true);
        peUnchangedRadioButton.setEnabled(false);
        peUnchangedRadioButton.setSelected(false);
        peRandomRadioButton.setVisible(true);
        peRandomRadioButton.setEnabled(false);
        peRandomRadioButton.setSelected(false);
        peSimilarStrengthCheckBox.setVisible(true);
        peSimilarStrengthCheckBox.setEnabled(false);
        peSimilarStrengthCheckBox.setSelected(false);
        peSameTypingCheckBox.setVisible(true);
        peSameTypingCheckBox.setEnabled(false);
        peSameTypingCheckBox.setSelected(false);
        peLimitEvolutionsToThreeCheckBox.setVisible(true);
        peLimitEvolutionsToThreeCheckBox.setEnabled(false);
        peLimitEvolutionsToThreeCheckBox.setSelected(false);
        peForceChangeCheckBox.setVisible(true);
        peForceChangeCheckBox.setEnabled(false);
        peForceChangeCheckBox.setSelected(false);
        peChangeImpossibleEvosCheckBox.setVisible(true);
        peChangeImpossibleEvosCheckBox.setEnabled(false);
        peChangeImpossibleEvosCheckBox.setSelected(false);
        peMakeEvolutionsEasierCheckBox.setVisible(true);
        peMakeEvolutionsEasierCheckBox.setEnabled(false);
        peMakeEvolutionsEasierCheckBox.setSelected(false);
        peRemoveTimeBasedEvolutionsCheckBox.setVisible(true);
        peRemoveTimeBasedEvolutionsCheckBox.setEnabled(false);
        peRemoveTimeBasedEvolutionsCheckBox.setSelected(false);
        peAllowAltFormesCheckBox.setVisible(true);
        peAllowAltFormesCheckBox.setEnabled(false);
        peAllowAltFormesCheckBox.setSelected(false);
        spUnchangedRadioButton.setVisible(true);
        spUnchangedRadioButton.setEnabled(false);
        spUnchangedRadioButton.setSelected(false);
        spCustomRadioButton.setVisible(true);
        spCustomRadioButton.setEnabled(false);
        spCustomRadioButton.setSelected(false);
        spRandomCompletelyRadioButton.setVisible(true);
        spRandomCompletelyRadioButton.setEnabled(false);
        spRandomCompletelyRadioButton.setSelected(false);
        spRandomTwoEvosRadioButton.setVisible(true);
        spRandomTwoEvosRadioButton.setEnabled(false);
        spRandomTwoEvosRadioButton.setSelected(false);
        spComboBox1.setVisible(true);
        spComboBox1.setEnabled(false);
        spComboBox1.setSelectedIndex(0);
        spComboBox1.setModel(new DefaultComboBoxModel<>(new String[] { "--" }));
        spComboBox2.setVisible(true);
        spComboBox2.setEnabled(false);
        spComboBox2.setSelectedIndex(0);
        spComboBox2.setModel(new DefaultComboBoxModel<>(new String[] { "--" }));
        spComboBox3.setVisible(true);
        spComboBox3.setEnabled(false);
        spComboBox3.setSelectedIndex(0);
        spComboBox3.setModel(new DefaultComboBoxModel<>(new String[] { "--" }));
        spRandomizeStarterHeldItemsCheckBox.setVisible(true);
        spRandomizeStarterHeldItemsCheckBox.setEnabled(false);
        spRandomizeStarterHeldItemsCheckBox.setSelected(false);
        spBanBadItemsCheckBox.setVisible(true);
        spBanBadItemsCheckBox.setEnabled(false);
        spBanBadItemsCheckBox.setSelected(false);
        spAllowAltFormesCheckBox.setVisible(true);
        spAllowAltFormesCheckBox.setEnabled(false);
        spAllowAltFormesCheckBox.setSelected(false);
        stpUnchangedRadioButton.setVisible(true);
        stpUnchangedRadioButton.setEnabled(false);
        stpUnchangedRadioButton.setSelected(false);
        stpSwapLegendariesSwapStandardsRadioButton.setVisible(true);
        stpSwapLegendariesSwapStandardsRadioButton.setEnabled(false);
        stpSwapLegendariesSwapStandardsRadioButton.setSelected(false);
        stpRandomCompletelyRadioButton.setVisible(true);
        stpRandomCompletelyRadioButton.setEnabled(false);
        stpRandomCompletelyRadioButton.setSelected(false);
        stpRandomSimilarStrengthRadioButton.setVisible(true);
        stpRandomSimilarStrengthRadioButton.setEnabled(false);
        stpRandomSimilarStrengthRadioButton.setSelected(false);
        stpPercentageLevelModifierCheckBox.setVisible(true);
        stpPercentageLevelModifierCheckBox.setEnabled(false);
        stpPercentageLevelModifierCheckBox.setSelected(false);
        stpPercentageLevelModifierSlider.setVisible(true);
        stpPercentageLevelModifierSlider.setEnabled(false);
        stpPercentageLevelModifierSlider.setValue(0);
        stpLimitMainGameLegendariesCheckBox.setVisible(true);
        stpLimitMainGameLegendariesCheckBox.setEnabled(false);
        stpLimitMainGameLegendariesCheckBox.setSelected(false);
        stpRandomize600BSTCheckBox.setVisible(true);
        stpRandomize600BSTCheckBox.setEnabled(false);
        stpRandomize600BSTCheckBox.setSelected(false);
        stpAllowAltFormesCheckBox.setVisible(true);
        stpAllowAltFormesCheckBox.setEnabled(false);
        stpAllowAltFormesCheckBox.setSelected(false);
        stpSwapMegaEvosCheckBox.setVisible(true);
        stpSwapMegaEvosCheckBox.setEnabled(false);
        stpSwapMegaEvosCheckBox.setSelected(false);
        igtUnchangedRadioButton.setVisible(true);
        igtUnchangedRadioButton.setEnabled(false);
        igtUnchangedRadioButton.setSelected(false);
        igtRandomizeGivenPokemonOnlyRadioButton.setVisible(true);
        igtRandomizeGivenPokemonOnlyRadioButton.setEnabled(false);
        igtRandomizeGivenPokemonOnlyRadioButton.setSelected(false);
        igtRandomizeBothRequestedGivenRadioButton.setVisible(true);
        igtRandomizeBothRequestedGivenRadioButton.setEnabled(false);
        igtRandomizeBothRequestedGivenRadioButton.setSelected(false);
        igtRandomizeNicknamesCheckBox.setVisible(true);
        igtRandomizeNicknamesCheckBox.setEnabled(false);
        igtRandomizeNicknamesCheckBox.setSelected(false);
        igtRandomizeOTsCheckBox.setVisible(true);
        igtRandomizeOTsCheckBox.setEnabled(false);
        igtRandomizeOTsCheckBox.setSelected(false);
        igtRandomizeIVsCheckBox.setVisible(true);
        igtRandomizeIVsCheckBox.setEnabled(false);
        igtRandomizeIVsCheckBox.setSelected(false);
        igtRandomizeItemsCheckBox.setVisible(true);
        igtRandomizeItemsCheckBox.setEnabled(false);
        igtRandomizeItemsCheckBox.setSelected(false);
        mdRandomizeMovePowerCheckBox.setVisible(true);
        mdRandomizeMovePowerCheckBox.setEnabled(false);
        mdRandomizeMovePowerCheckBox.setSelected(false);
        mdRandomizeMoveAccuracyCheckBox.setVisible(true);
        mdRandomizeMoveAccuracyCheckBox.setEnabled(false);
        mdRandomizeMoveAccuracyCheckBox.setSelected(false);
        mdRandomizeMovePPCheckBox.setVisible(true);
        mdRandomizeMovePPCheckBox.setEnabled(false);
        mdRandomizeMovePPCheckBox.setSelected(false);
        mdRandomizeMoveTypesCheckBox.setVisible(true);
        mdRandomizeMoveTypesCheckBox.setEnabled(false);
        mdRandomizeMoveTypesCheckBox.setSelected(false);
        mdRandomizeMoveCategoryCheckBox.setVisible(true);
        mdRandomizeMoveCategoryCheckBox.setEnabled(false);
        mdRandomizeMoveCategoryCheckBox.setSelected(false);
        mdUpdateMovesCheckBox.setVisible(true);
        mdUpdateMovesCheckBox.setEnabled(false);
        mdUpdateMovesCheckBox.setSelected(false);
        mdUpdateComboBox.setVisible(true);
        mdUpdateComboBox.setEnabled(false);
        mdUpdateComboBox.setSelectedIndex(0);
        mdUpdateComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "--" }));
        pmsUnchangedRadioButton.setVisible(true);
        pmsUnchangedRadioButton.setEnabled(false);
        pmsUnchangedRadioButton.setSelected(false);
        pmsRandomPreferringSameTypeRadioButton.setVisible(true);
        pmsRandomPreferringSameTypeRadioButton.setEnabled(false);
        pmsRandomPreferringSameTypeRadioButton.setSelected(false);
        pmsRandomCompletelyRadioButton.setVisible(true);
        pmsRandomCompletelyRadioButton.setEnabled(false);
        pmsRandomCompletelyRadioButton.setSelected(false);
        pmsMetronomeOnlyModeRadioButton.setVisible(true);
        pmsMetronomeOnlyModeRadioButton.setEnabled(false);
        pmsMetronomeOnlyModeRadioButton.setSelected(false);
        pmsGuaranteedLevel1MovesCheckBox.setVisible(true);
        pmsGuaranteedLevel1MovesCheckBox.setEnabled(false);
        pmsGuaranteedLevel1MovesCheckBox.setSelected(false);
        pmsReorderDamagingMovesCheckBox.setVisible(true);
        pmsReorderDamagingMovesCheckBox.setEnabled(false);
        pmsReorderDamagingMovesCheckBox.setSelected(false);
        pmsNoGameBreakingMovesCheckBox.setVisible(true);
        pmsNoGameBreakingMovesCheckBox.setEnabled(false);
        pmsNoGameBreakingMovesCheckBox.setSelected(false);
        pmsForceGoodDamagingCheckBox.setVisible(true);
        pmsForceGoodDamagingCheckBox.setEnabled(false);
        pmsForceGoodDamagingCheckBox.setSelected(false);
        pmsGuaranteedLevel1MovesSlider.setVisible(true);
        pmsGuaranteedLevel1MovesSlider.setEnabled(false);
        pmsGuaranteedLevel1MovesSlider.setValue(pmsGuaranteedLevel1MovesSlider.getMinimum());
        pmsForceGoodDamagingSlider.setVisible(true);
        pmsForceGoodDamagingSlider.setEnabled(false);
        pmsForceGoodDamagingSlider.setValue(pmsForceGoodDamagingSlider.getMinimum());
        pmsEvolutionMovesCheckBox.setVisible(true);
        pmsEvolutionMovesCheckBox.setEnabled(false);
        pmsEvolutionMovesCheckBox.setSelected(false);
        tpUnchangedRadioButton.setVisible(true);
        tpUnchangedRadioButton.setEnabled(false);
        tpUnchangedRadioButton.setSelected(false);
        tpRandomRadioButton.setVisible(true);
        tpRandomRadioButton.setEnabled(false);
        tpRandomRadioButton.setSelected(false);
        tpRandomEvenDistributionRadioButton.setVisible(true);
        tpRandomEvenDistributionRadioButton.setEnabled(false);
        tpRandomEvenDistributionRadioButton.setSelected(false);
        tpRandomEvenDistributionMainRadioButton.setVisible(true);
        tpRandomEvenDistributionMainRadioButton.setEnabled(false);
        tpRandomEvenDistributionMainRadioButton.setSelected(false);
        tpTypeThemedRadioButton.setVisible(true);
        tpTypeThemedRadioButton.setEnabled(false);
        tpTypeThemedRadioButton.setSelected(false);
        tpRivalCarriesStarterCheckBox.setVisible(true);
        tpRivalCarriesStarterCheckBox.setEnabled(false);
        tpRivalCarriesStarterCheckBox.setSelected(false);
        tpSimilarStrengthCheckBox.setVisible(true);
        tpSimilarStrengthCheckBox.setEnabled(false);
        tpSimilarStrengthCheckBox.setSelected(false);
        tpWeightTypesCheckBox.setVisible(true);
        tpWeightTypesCheckBox.setEnabled(false);
        tpWeightTypesCheckBox.setSelected(false);
        tpDontUseLegendariesCheckBox.setVisible(true);
        tpDontUseLegendariesCheckBox.setEnabled(false);
        tpDontUseLegendariesCheckBox.setSelected(false);
        tpNoEarlyWonderGuardCheckBox.setVisible(true);
        tpNoEarlyWonderGuardCheckBox.setEnabled(false);
        tpNoEarlyWonderGuardCheckBox.setSelected(false);
        tpRandomizeTrainerNamesCheckBox.setVisible(true);
        tpRandomizeTrainerNamesCheckBox.setEnabled(false);
        tpRandomizeTrainerNamesCheckBox.setSelected(false);
        tpRandomizeTrainerClassNamesCheckBox.setVisible(true);
        tpRandomizeTrainerClassNamesCheckBox.setEnabled(false);
        tpRandomizeTrainerClassNamesCheckBox.setSelected(false);
        tpForceFullyEvolvedAtCheckBox.setVisible(true);
        tpForceFullyEvolvedAtCheckBox.setEnabled(false);
        tpForceFullyEvolvedAtCheckBox.setSelected(false);
        tpForceFullyEvolvedAtSlider.setVisible(true);
        tpForceFullyEvolvedAtSlider.setEnabled(false);
        tpForceFullyEvolvedAtSlider.setValue(tpForceFullyEvolvedAtSlider.getMinimum());
        tpPercentageLevelModifierSlider.setVisible(true);
        tpPercentageLevelModifierSlider.setEnabled(false);
        tpPercentageLevelModifierSlider.setValue(0);
        tpPercentageLevelModifierCheckBox.setVisible(true);
        tpPercentageLevelModifierCheckBox.setEnabled(false);
        tpPercentageLevelModifierCheckBox.setSelected(false);
        tpAllowAlternateFormesCheckBox.setVisible(true);
        tpAllowAlternateFormesCheckBox.setEnabled(false);
        tpAllowAlternateFormesCheckBox.setSelected(false);
        tpSwapMegaEvosCheckBox.setVisible(true);
        tpSwapMegaEvosCheckBox.setEnabled(false);
        tpSwapMegaEvosCheckBox.setSelected(false);
        tpDoubleBattleModeCheckBox.setVisible(true);
        tpDoubleBattleModeCheckBox.setEnabled(false);
        tpDoubleBattleModeCheckBox.setSelected(false);
        tpBossTrainersCheckBox.setVisible(true);
        tpBossTrainersCheckBox.setEnabled(false);
        tpBossTrainersCheckBox.setSelected(false);
        tpImportantTrainersCheckBox.setVisible(true);
        tpImportantTrainersCheckBox.setEnabled(false);
        tpImportantTrainersCheckBox.setSelected(false);
        tpRegularTrainersCheckBox.setVisible(true);
        tpRegularTrainersCheckBox.setEnabled(false);
        tpRegularTrainersCheckBox.setSelected(false);
        tpBossTrainersSpinner.setVisible(true);
        tpBossTrainersSpinner.setEnabled(false);
        tpBossTrainersSpinner.setValue(1);
        tpImportantTrainersSpinner.setVisible(true);
        tpImportantTrainersSpinner.setEnabled(false);
        tpImportantTrainersSpinner.setValue(1);
        tpRegularTrainersSpinner.setVisible(true);
        tpRegularTrainersSpinner.setEnabled(false);
        tpRegularTrainersSpinner.setValue(1);
        tpAdditionalPokemonForLabel.setVisible(true);
        tpRandomShinyTrainerPokemonCheckBox.setVisible(true);
        tpRandomShinyTrainerPokemonCheckBox.setEnabled(false);
        totpPanel.setVisible(true);
        totpAllyPanel.setVisible(true);
        totpAuraPanel.setVisible(true);
        totpUnchangedRadioButton.setVisible(true);
        totpUnchangedRadioButton.setEnabled(false);
        totpUnchangedRadioButton.setSelected(true);
        totpRandomRadioButton.setVisible(true);
        totpRandomRadioButton.setEnabled(false);
        totpRandomRadioButton.setSelected(false);
        totpRandomSimilarStrengthRadioButton.setVisible(true);
        totpRandomSimilarStrengthRadioButton.setEnabled(false);
        totpRandomSimilarStrengthRadioButton.setSelected(false);
        totpAllyUnchangedRadioButton.setVisible(true);
        totpAllyUnchangedRadioButton.setEnabled(false);
        totpAllyUnchangedRadioButton.setSelected(true);
        totpAllyRandomRadioButton.setVisible(true);
        totpAllyRandomRadioButton.setEnabled(false);
        totpAllyRandomRadioButton.setSelected(false);
        totpAllyRandomSimilarStrengthRadioButton.setVisible(true);
        totpAllyRandomSimilarStrengthRadioButton.setEnabled(false);
        totpAllyRandomSimilarStrengthRadioButton.setSelected(false);
        totpAuraUnchangedRadioButton.setVisible(true);
        totpAuraUnchangedRadioButton.setEnabled(false);
        totpAuraUnchangedRadioButton.setSelected(true);
        totpAuraRandomRadioButton.setVisible(true);
        totpAuraRandomRadioButton.setEnabled(false);
        totpAuraRandomRadioButton.setSelected(false);
        totpAuraRandomSameStrengthRadioButton.setVisible(true);
        totpAuraRandomSameStrengthRadioButton.setEnabled(false);
        totpAuraRandomSameStrengthRadioButton.setSelected(false);
        totpPercentageLevelModifierCheckBox.setVisible(true);
        totpPercentageLevelModifierCheckBox.setEnabled(false);
        totpPercentageLevelModifierCheckBox.setSelected(false);
        totpPercentageLevelModifierSlider.setVisible(true);
        totpPercentageLevelModifierSlider.setEnabled(false);
        totpPercentageLevelModifierSlider.setValue(0);
        totpRandomizeHeldItemsCheckBox.setVisible(true);
        totpRandomizeHeldItemsCheckBox.setEnabled(false);
        totpRandomizeHeldItemsCheckBox.setSelected(false);
        totpAllowAltFormesCheckBox.setVisible(true);
        totpAllowAltFormesCheckBox.setEnabled(false);
        totpAllowAltFormesCheckBox.setSelected(false);
        wpUnchangedRadioButton.setVisible(true);
        wpUnchangedRadioButton.setEnabled(false);
        wpUnchangedRadioButton.setSelected(false);
        wpRandomRadioButton.setVisible(true);
        wpRandomRadioButton.setEnabled(false);
        wpRandomRadioButton.setSelected(false);
        wpArea1To1RadioButton.setVisible(true);
        wpArea1To1RadioButton.setEnabled(false);
        wpArea1To1RadioButton.setSelected(false);
        wpGlobal1To1RadioButton.setVisible(true);
        wpGlobal1To1RadioButton.setEnabled(false);
        wpGlobal1To1RadioButton.setSelected(false);
        wpARNoneRadioButton.setVisible(true);
        wpARNoneRadioButton.setEnabled(false);
        wpARNoneRadioButton.setSelected(false);
        wpARSimilarStrengthRadioButton.setVisible(true);
        wpARSimilarStrengthRadioButton.setEnabled(false);
        wpARSimilarStrengthRadioButton.setSelected(false);
        wpARCatchEmAllModeRadioButton.setVisible(true);
        wpARCatchEmAllModeRadioButton.setEnabled(false);
        wpARCatchEmAllModeRadioButton.setSelected(false);
        wpARTypeThemeAreasRadioButton.setVisible(true);
        wpARTypeThemeAreasRadioButton.setEnabled(false);
        wpARTypeThemeAreasRadioButton.setSelected(false);
        wpUseTimeBasedEncountersCheckBox.setVisible(true);
        wpUseTimeBasedEncountersCheckBox.setEnabled(false);
        wpUseTimeBasedEncountersCheckBox.setSelected(false);
        wpDontUseLegendariesCheckBox.setVisible(true);
        wpDontUseLegendariesCheckBox.setEnabled(false);
        wpDontUseLegendariesCheckBox.setSelected(false);
        wpSetMinimumCatchRateCheckBox.setVisible(true);
        wpSetMinimumCatchRateCheckBox.setEnabled(false);
        wpSetMinimumCatchRateCheckBox.setSelected(false);
        wpRandomizeHeldItemsCheckBox.setVisible(true);
        wpRandomizeHeldItemsCheckBox.setEnabled(false);
        wpRandomizeHeldItemsCheckBox.setSelected(false);
        wpBanBadItemsCheckBox.setVisible(true);
        wpBanBadItemsCheckBox.setEnabled(false);
        wpBanBadItemsCheckBox.setSelected(false);
        wpBalanceShakingGrassPokemonCheckBox.setVisible(true);
        wpBalanceShakingGrassPokemonCheckBox.setEnabled(false);
        wpBalanceShakingGrassPokemonCheckBox.setSelected(false);
        wpPercentageLevelModifierCheckBox.setVisible(true);
        wpPercentageLevelModifierCheckBox.setEnabled(false);
        wpPercentageLevelModifierCheckBox.setSelected(false);
        wpPercentageLevelModifierSlider.setVisible(true);
        wpPercentageLevelModifierSlider.setEnabled(false);
        wpPercentageLevelModifierSlider.setValue(0);
        wpSetMinimumCatchRateSlider.setVisible(true);
        wpSetMinimumCatchRateSlider.setEnabled(false);
        wpSetMinimumCatchRateSlider.setValue(wpSetMinimumCatchRateSlider.getMinimum());
        wpAllowAltFormesCheckBox.setVisible(true);
        wpAllowAltFormesCheckBox.setEnabled(false);
        wpAllowAltFormesCheckBox.setSelected(false);
        tmUnchangedRadioButton.setVisible(true);
        tmUnchangedRadioButton.setEnabled(false);
        tmUnchangedRadioButton.setSelected(false);
        tmRandomRadioButton.setVisible(true);
        tmRandomRadioButton.setEnabled(false);
        tmRandomRadioButton.setSelected(false);
        tmNoGameBreakingMovesCheckBox.setVisible(true);
        tmNoGameBreakingMovesCheckBox.setEnabled(false);
        tmNoGameBreakingMovesCheckBox.setSelected(false);
        tmFullHMCompatibilityCheckBox.setVisible(true);
        tmFullHMCompatibilityCheckBox.setEnabled(false);
        tmFullHMCompatibilityCheckBox.setSelected(false);
        tmLevelupMoveSanityCheckBox.setVisible(true);
        tmLevelupMoveSanityCheckBox.setEnabled(false);
        tmLevelupMoveSanityCheckBox.setSelected(false);
        tmKeepFieldMoveTMsCheckBox.setVisible(true);
        tmKeepFieldMoveTMsCheckBox.setEnabled(false);
        tmKeepFieldMoveTMsCheckBox.setSelected(false);
        tmForceGoodDamagingCheckBox.setVisible(true);
        tmForceGoodDamagingCheckBox.setEnabled(false);
        tmForceGoodDamagingCheckBox.setSelected(false);
        tmForceGoodDamagingSlider.setVisible(true);
        tmForceGoodDamagingSlider.setEnabled(false);
        tmForceGoodDamagingSlider.setValue(tmForceGoodDamagingSlider.getMinimum());
        tmFollowEvolutionsCheckBox.setVisible(true);
        tmFollowEvolutionsCheckBox.setEnabled(false);
        tmFollowEvolutionsCheckBox.setSelected(false);
        thcUnchangedRadioButton.setVisible(true);
        thcUnchangedRadioButton.setEnabled(false);
        thcUnchangedRadioButton.setSelected(false);
        thcRandomPreferSameTypeRadioButton.setVisible(true);
        thcRandomPreferSameTypeRadioButton.setEnabled(false);
        thcRandomPreferSameTypeRadioButton.setSelected(false);
        thcRandomCompletelyRadioButton.setVisible(true);
        thcRandomCompletelyRadioButton.setEnabled(false);
        thcRandomCompletelyRadioButton.setSelected(false);
        thcFullCompatibilityRadioButton.setVisible(true);
        thcFullCompatibilityRadioButton.setEnabled(false);
        thcFullCompatibilityRadioButton.setSelected(false);
        mtUnchangedRadioButton.setVisible(true);
        mtUnchangedRadioButton.setEnabled(false);
        mtUnchangedRadioButton.setSelected(false);
        mtRandomRadioButton.setVisible(true);
        mtRandomRadioButton.setEnabled(false);
        mtRandomRadioButton.setSelected(false);
        mtNoGameBreakingMovesCheckBox.setVisible(true);
        mtNoGameBreakingMovesCheckBox.setEnabled(false);
        mtNoGameBreakingMovesCheckBox.setSelected(false);
        mtLevelupMoveSanityCheckBox.setVisible(true);
        mtLevelupMoveSanityCheckBox.setEnabled(false);
        mtLevelupMoveSanityCheckBox.setSelected(false);
        mtKeepFieldMoveTutorsCheckBox.setVisible(true);
        mtKeepFieldMoveTutorsCheckBox.setEnabled(false);
        mtKeepFieldMoveTutorsCheckBox.setSelected(false);
        mtForceGoodDamagingCheckBox.setVisible(true);
        mtForceGoodDamagingCheckBox.setEnabled(false);
        mtForceGoodDamagingCheckBox.setSelected(false);
        mtForceGoodDamagingSlider.setVisible(true);
        mtForceGoodDamagingSlider.setEnabled(false);
        mtForceGoodDamagingSlider.setValue(mtForceGoodDamagingSlider.getMinimum());
        mtFollowEvolutionsCheckBox.setVisible(true);
        mtFollowEvolutionsCheckBox.setEnabled(false);
        mtFollowEvolutionsCheckBox.setSelected(false);
        mtcUnchangedRadioButton.setVisible(true);
        mtcUnchangedRadioButton.setEnabled(false);
        mtcUnchangedRadioButton.setSelected(false);
        mtcRandomPreferSameTypeRadioButton.setVisible(true);
        mtcRandomPreferSameTypeRadioButton.setEnabled(false);
        mtcRandomPreferSameTypeRadioButton.setSelected(false);
        mtcRandomCompletelyRadioButton.setVisible(true);
        mtcRandomCompletelyRadioButton.setEnabled(false);
        mtcRandomCompletelyRadioButton.setSelected(false);
        mtcFullCompatibilityRadioButton.setVisible(true);
        mtcFullCompatibilityRadioButton.setEnabled(false);
        mtcFullCompatibilityRadioButton.setSelected(false);
        fiUnchangedRadioButton.setVisible(true);
        fiUnchangedRadioButton.setEnabled(false);
        fiUnchangedRadioButton.setSelected(false);
        fiShuffleRadioButton.setVisible(true);
        fiShuffleRadioButton.setEnabled(false);
        fiShuffleRadioButton.setSelected(false);
        fiRandomRadioButton.setVisible(true);
        fiRandomRadioButton.setEnabled(false);
        fiRandomRadioButton.setSelected(false);
        fiRandomEvenDistributionRadioButton.setVisible(true);
        fiRandomEvenDistributionRadioButton.setEnabled(false);
        fiRandomEvenDistributionRadioButton.setSelected(false);
        fiBanBadItemsCheckBox.setVisible(true);
        fiBanBadItemsCheckBox.setEnabled(false);
        fiBanBadItemsCheckBox.setSelected(false);
        shUnchangedRadioButton.setVisible(true);
        shUnchangedRadioButton.setEnabled(false);
        shUnchangedRadioButton.setSelected(false);
        shShuffleRadioButton.setVisible(true);
        shShuffleRadioButton.setEnabled(false);
        shShuffleRadioButton.setSelected(false);
        shRandomRadioButton.setVisible(true);
        shRandomRadioButton.setEnabled(false);
        shRandomRadioButton.setSelected(false);
        shBanOverpoweredShopItemsCheckBox.setVisible(true);
        shBanOverpoweredShopItemsCheckBox.setEnabled(false);
        shBanOverpoweredShopItemsCheckBox.setSelected(false);
        shBanBadItemsCheckBox.setVisible(true);
        shBanBadItemsCheckBox.setEnabled(false);
        shBanBadItemsCheckBox.setSelected(false);
        shBanRegularShopItemsCheckBox.setVisible(true);
        shBanRegularShopItemsCheckBox.setEnabled(false);
        shBanRegularShopItemsCheckBox.setSelected(false);
        shBalanceShopItemPricesCheckBox.setVisible(true);
        shBalanceShopItemPricesCheckBox.setEnabled(false);
        shBalanceShopItemPricesCheckBox.setSelected(false);
        shGuaranteeEvolutionItemsCheckBox.setVisible(true);
        shGuaranteeEvolutionItemsCheckBox.setEnabled(false);
        shGuaranteeEvolutionItemsCheckBox.setSelected(false);
        shGuaranteeXItemsCheckBox.setVisible(true);
        shGuaranteeXItemsCheckBox.setEnabled(false);
        shGuaranteeXItemsCheckBox.setSelected(false);
        miscBWExpPatchCheckBox.setVisible(true);
        miscBWExpPatchCheckBox.setEnabled(false);
        miscBWExpPatchCheckBox.setSelected(false);
        miscNerfXAccuracyCheckBox.setVisible(true);
        miscNerfXAccuracyCheckBox.setEnabled(false);
        miscNerfXAccuracyCheckBox.setSelected(false);
        miscFixCritRateCheckBox.setVisible(true);
        miscFixCritRateCheckBox.setEnabled(false);
        miscFixCritRateCheckBox.setSelected(false);
        miscFastestTextCheckBox.setVisible(true);
        miscFastestTextCheckBox.setEnabled(false);
        miscFastestTextCheckBox.setSelected(false);
        miscRunningShoesIndoorsCheckBox.setVisible(true);
        miscRunningShoesIndoorsCheckBox.setEnabled(false);
        miscRunningShoesIndoorsCheckBox.setSelected(false);
        miscRandomizePCPotionCheckBox.setVisible(true);
        miscRandomizePCPotionCheckBox.setEnabled(false);
        miscRandomizePCPotionCheckBox.setSelected(false);
        miscAllowPikachuEvolutionCheckBox.setVisible(true);
        miscAllowPikachuEvolutionCheckBox.setEnabled(false);
        miscAllowPikachuEvolutionCheckBox.setSelected(false);
        miscGiveNationalDexAtCheckBox.setVisible(true);
        miscGiveNationalDexAtCheckBox.setEnabled(false);
        miscGiveNationalDexAtCheckBox.setSelected(false);
        miscUpdateTypeEffectivenessCheckBox.setVisible(true);
        miscUpdateTypeEffectivenessCheckBox.setEnabled(false);
        miscUpdateTypeEffectivenessCheckBox.setSelected(false);
        miscRandomizeHiddenHollowsCheckBox.setVisible(true);
        miscRandomizeHiddenHollowsCheckBox.setEnabled(false);
        miscRandomizeHiddenHollowsCheckBox.setSelected(false);
        miscLowerCasePokemonNamesCheckBox.setVisible(true);
        miscLowerCasePokemonNamesCheckBox.setEnabled(false);
        miscLowerCasePokemonNamesCheckBox.setSelected(false);
        miscRandomizeCatchingTutorialCheckBox.setVisible(true);
        miscRandomizeCatchingTutorialCheckBox.setEnabled(false);
        miscRandomizeCatchingTutorialCheckBox.setSelected(false);
        miscBanLuckyEggCheckBox.setVisible(true);
        miscBanLuckyEggCheckBox.setEnabled(false);
        miscBanLuckyEggCheckBox.setSelected(false);
        miscNoFreeLuckyEggCheckBox.setVisible(true);
        miscNoFreeLuckyEggCheckBox.setEnabled(false);
        miscNoFreeLuckyEggCheckBox.setSelected(false);
        miscBanBigMoneyManiacCheckBox.setVisible(true);
        miscBanBigMoneyManiacCheckBox.setEnabled(false);
        miscBanBigMoneyManiacCheckBox.setSelected(false);
        mtNoExistLabel.setVisible(false);
        mtNoneAvailableLabel.setVisible(false);

        liveTweaksPanel.setVisible(false);
        miscTweaksPanel.setVisible(true);
    }

    private void romLoaded() {

        try {
            int pokemonGeneration = romHandler.generationOfPokemon();

            if (romHandler.hasGameUpdateLoaded()) {
                romNameLabel.setText(romHandler.getROMName() + " (" + romHandler.getGameUpdateVersion() + ")");
            } else {
                romNameLabel.setText(romHandler.getROMName());
            }
            romCodeLabel.setText(romHandler.getROMCode());
            romSupportLabel.setText(bundle.getString("GUI.romSupportPrefix") + " "
                    + this.romHandler.getSupportLevel());

            limitPokemonCheckBox.setVisible(true);
            limitPokemonCheckBox.setEnabled(true);
            limitPokemonButton.setVisible(true);

            raceModeCheckBox.setEnabled(true);

            loadSettingsButton.setEnabled(true);
            saveSettingsButton.setEnabled(true);

            // Pokemon Traits

            // Pokemon Base Statistics
            pbsUnchangedRadioButton.setEnabled(true);
            pbsUnchangedRadioButton.setSelected(true);
            pbsShuffleRadioButton.setEnabled(true);
            pbsRandomRadioButton.setEnabled(true);

            pbsStandardizeEXPCurvesCheckBox.setEnabled(true);
            pbsLegendariesSlowRadioButton.setSelected(true);
            pbsUpdateBaseStatsCheckBox.setEnabled(pokemonGeneration < 8);
            pbsFollowMegaEvosCheckBox.setVisible(romHandler.hasMegaEvolutions());
            pbsUpdateComboBox.setVisible(pokemonGeneration < 8);
            ExpCurve[] expCurves = getEXPCurvesForGeneration(pokemonGeneration);
            String[] expCurveNames = new String[expCurves.length];
            for (int i = 0; i < expCurves.length; i++) {
                expCurveNames[i] = expCurves[i].toString();
            }
            pbsEXPCurveComboBox.setModel(new DefaultComboBoxModel<>(expCurveNames));
            pbsEXPCurveComboBox.setSelectedIndex(0);

            // Pokemon Types
            ptUnchangedRadioButton.setEnabled(true);
            ptUnchangedRadioButton.setSelected(true);
            ptRandomFollowEvolutionsRadioButton.setEnabled(true);
            ptRandomCompletelyRadioButton.setEnabled(true);
            ptFollowMegaEvosCheckBox.setVisible(romHandler.hasMegaEvolutions());

            // Pokemon Abilities
            if (pokemonGeneration >= 3) {
                paUnchangedRadioButton.setEnabled(true);
                paUnchangedRadioButton.setSelected(true);
                paRandomRadioButton.setEnabled(true);

                paAllowWonderGuardCheckBox.setEnabled(false);
                paFollowEvolutionsCheckBox.setEnabled(false);
                paTrappingAbilitiesCheckBox.setEnabled(false);
                paNegativeAbilitiesCheckBox.setEnabled(false);
                paBadAbilitiesCheckBox.setEnabled(false);
                paFollowMegaEvosCheckBox.setVisible(romHandler.hasMegaEvolutions());
                paWeighDuplicatesTogetherCheckBox.setEnabled(false);
            } else {
                pokemonAbilitiesPanel.setVisible(false);
            }

            // Pokemon Evolutions
            peUnchangedRadioButton.setEnabled(true);
            peUnchangedRadioButton.setSelected(true);
            peRandomRadioButton.setEnabled(true);

//        peSimilarStrengthCheckBox.setEnabled(true);
//        peSameTypingCheckBox.setEnabled(true);
//        peLimitEvolutionsToThreeCheckBox.setEnabled(true);
//        peForceChangeCheckBox.setEnabled(true);
            peChangeImpossibleEvosCheckBox.setEnabled(true);
            peMakeEvolutionsEasierCheckBox.setEnabled(true);
            peRemoveTimeBasedEvolutionsCheckBox.setEnabled(true);
            peAllowAltFormesCheckBox.setVisible(pokemonGeneration >= 7);

            // Starters, Statics & Trades

            // Starter Pokemon
            spUnchangedRadioButton.setEnabled(true);
            spUnchangedRadioButton.setSelected(true);

            if (romHandler.canChangeStarters()) {
                spCustomRadioButton.setEnabled(true);
                spRandomCompletelyRadioButton.setEnabled(true);
                spRandomTwoEvosRadioButton.setEnabled(true);
                spAllowAltFormesCheckBox.setVisible(romHandler.hasStarterAltFormes());
                if (romHandler.isYellow()) {
                    spComboBox3.setVisible(false);
                }
                populateDropdowns();
            }

            boolean hasStarterHeldItems = (pokemonGeneration == 2 || pokemonGeneration == 3);
            spRandomizeStarterHeldItemsCheckBox.setEnabled(hasStarterHeldItems);
            spRandomizeStarterHeldItemsCheckBox.setVisible(hasStarterHeldItems);
            spBanBadItemsCheckBox.setEnabled(false);
            spBanBadItemsCheckBox.setVisible(hasStarterHeldItems);

            stpUnchangedRadioButton.setEnabled(true);
            stpUnchangedRadioButton.setSelected(true);
            if (romHandler.canChangeStaticPokemon()) {
                stpSwapLegendariesSwapStandardsRadioButton.setEnabled(true);
                stpRandomCompletelyRadioButton.setEnabled(true);
                stpRandomSimilarStrengthRadioButton.setEnabled(true);
                stpLimitMainGameLegendariesCheckBox.setVisible(romHandler.hasMainGameLegendaries());
                stpLimitMainGameLegendariesCheckBox.setEnabled(false);
                stpAllowAltFormesCheckBox.setVisible(romHandler.hasStaticAltFormes());
                stpSwapMegaEvosCheckBox.setVisible(pokemonGeneration == 6 && !romHandler.forceSwapStaticMegaEvos());
                stpPercentageLevelModifierCheckBox.setVisible(pokemonGeneration >= 3);
                stpPercentageLevelModifierCheckBox.setEnabled(pokemonGeneration >= 3);
                stpPercentageLevelModifierSlider.setVisible(pokemonGeneration >= 3);
                stpPercentageLevelModifierSlider.setEnabled(false);
            } else {
                stpSwapLegendariesSwapStandardsRadioButton.setVisible(false);
                stpRandomCompletelyRadioButton.setVisible(false);
                stpRandomSimilarStrengthRadioButton.setVisible(false);
                stpRandomize600BSTCheckBox.setVisible(false);
                stpLimitMainGameLegendariesCheckBox.setVisible(false);
                stpPercentageLevelModifierCheckBox.setVisible(false);
                stpPercentageLevelModifierSlider.setVisible(false);
            }

            igtUnchangedRadioButton.setEnabled(true);
            igtUnchangedRadioButton.setSelected(true);
            igtRandomizeGivenPokemonOnlyRadioButton.setEnabled(true);
            igtRandomizeBothRequestedGivenRadioButton.setEnabled(true);

            igtRandomizeNicknamesCheckBox.setEnabled(false);
            igtRandomizeOTsCheckBox.setEnabled(false);
            igtRandomizeIVsCheckBox.setEnabled(false);
            igtRandomizeItemsCheckBox.setEnabled(false);

            if (pokemonGeneration == 1) {
                igtRandomizeOTsCheckBox.setVisible(false);
                igtRandomizeIVsCheckBox.setVisible(false);
                igtRandomizeItemsCheckBox.setVisible(false);
            }

            // Move Data
            mdRandomizeMovePowerCheckBox.setEnabled(true);
            mdRandomizeMoveAccuracyCheckBox.setEnabled(true);
            mdRandomizeMovePPCheckBox.setEnabled(true);
            mdRandomizeMoveTypesCheckBox.setEnabled(true);
            mdRandomizeMoveCategoryCheckBox.setEnabled(romHandler.hasPhysicalSpecialSplit());
            mdRandomizeMoveCategoryCheckBox.setVisible(romHandler.hasPhysicalSpecialSplit());
            mdUpdateMovesCheckBox.setEnabled(pokemonGeneration < 8);
            mdUpdateMovesCheckBox.setVisible(pokemonGeneration < 8);

            // Pokemon Movesets
            pmsUnchangedRadioButton.setEnabled(true);
            pmsUnchangedRadioButton.setSelected(true);
            pmsRandomPreferringSameTypeRadioButton.setEnabled(true);
            pmsRandomCompletelyRadioButton.setEnabled(true);
            pmsMetronomeOnlyModeRadioButton.setEnabled(true);

            pmsGuaranteedLevel1MovesCheckBox.setVisible(romHandler.supportsFourStartingMoves());
            pmsGuaranteedLevel1MovesSlider.setVisible(romHandler.supportsFourStartingMoves());
            pmsEvolutionMovesCheckBox.setVisible(pokemonGeneration >= 7);

            tpUnchangedRadioButton.setEnabled(true);
            tpUnchangedRadioButton.setSelected(true);
            tpRandomRadioButton.setEnabled(true);
            tpTypeThemedRadioButton.setEnabled(true);
            tpRandomEvenDistributionRadioButton.setEnabled(true);
            tpRandomEvenDistributionRadioButton.setVisible(true);
            tpRandomEvenDistributionMainRadioButton.setEnabled(pokemonGeneration == 5);
            tpRandomEvenDistributionMainRadioButton.setVisible(pokemonGeneration == 5);
            tpAllowAlternateFormesCheckBox.setVisible(romHandler.hasFunctionalFormes());
            tpForceFullyEvolvedAtCheckBox.setEnabled(true);
            tpPercentageLevelModifierCheckBox.setEnabled(true);
            tpSwapMegaEvosCheckBox.setVisible(romHandler.hasMegaEvolutions());
            tpDoubleBattleModeCheckBox.setVisible(pokemonGeneration >= 4);
            tpDoubleBattleModeCheckBox.setEnabled(pokemonGeneration >= 4);

            boolean additionalPokemonAvailable = pokemonGeneration >= 4;

            tpAdditionalPokemonForLabel.setVisible(additionalPokemonAvailable);
            tpBossTrainersCheckBox.setVisible(additionalPokemonAvailable);
            tpBossTrainersCheckBox.setEnabled(false);
            tpBossTrainersSpinner.setVisible(additionalPokemonAvailable);
            tpImportantTrainersCheckBox.setVisible(additionalPokemonAvailable);
            tpImportantTrainersCheckBox.setEnabled(false);
            tpImportantTrainersSpinner.setVisible(additionalPokemonAvailable);
            tpRegularTrainersCheckBox.setVisible(additionalPokemonAvailable);
            tpRegularTrainersCheckBox.setEnabled(false);
            tpRegularTrainersSpinner.setVisible(additionalPokemonAvailable);

            tpRandomizeTrainerNamesCheckBox.setEnabled(true);
            tpRandomizeTrainerClassNamesCheckBox.setEnabled(true);
            tpNoEarlyWonderGuardCheckBox.setVisible(pokemonGeneration >= 3);
            tpRandomShinyTrainerPokemonCheckBox.setVisible(pokemonGeneration >= 7);

            totpPanel.setVisible(pokemonGeneration == 7);
            if (totpPanel.isVisible()) {
                totpUnchangedRadioButton.setEnabled(true);
                totpRandomRadioButton.setEnabled(true);
                totpRandomSimilarStrengthRadioButton.setEnabled(true);

                totpAllyPanel.setVisible(pokemonGeneration == 7);
                totpAllyUnchangedRadioButton.setEnabled(true);
                totpAllyRandomRadioButton.setEnabled(true);
                totpAllyRandomSimilarStrengthRadioButton.setEnabled(true);

                totpAuraPanel.setVisible(pokemonGeneration == 7);
                totpAuraUnchangedRadioButton.setEnabled(true);
                totpAuraRandomRadioButton.setEnabled(true);
                totpAuraRandomSameStrengthRadioButton.setEnabled(true);

                totpRandomizeHeldItemsCheckBox.setEnabled(true);
                totpAllowAltFormesCheckBox.setEnabled(false);
                totpPercentageLevelModifierCheckBox.setEnabled(true);
                totpPercentageLevelModifierSlider.setEnabled(false);
            }

            // Wild Pokemon
            wpUnchangedRadioButton.setEnabled(true);
            wpUnchangedRadioButton.setSelected(true);
            wpRandomRadioButton.setEnabled(true);
            wpArea1To1RadioButton.setEnabled(true);
            wpGlobal1To1RadioButton.setEnabled(true);

            wpARNoneRadioButton.setSelected(true);

            wpUseTimeBasedEncountersCheckBox.setVisible(romHandler.hasTimeBasedEncounters());
            wpSetMinimumCatchRateCheckBox.setEnabled(true);
            wpRandomizeHeldItemsCheckBox.setEnabled(true);
            wpRandomizeHeldItemsCheckBox.setVisible(pokemonGeneration != 1);
            wpBanBadItemsCheckBox.setVisible(pokemonGeneration != 1);
            wpBalanceShakingGrassPokemonCheckBox.setVisible(pokemonGeneration == 5);
            wpPercentageLevelModifierCheckBox.setEnabled(true);
            wpAllowAltFormesCheckBox.setVisible(romHandler.hasWildAltFormes());

            tmUnchangedRadioButton.setEnabled(true);
            tmUnchangedRadioButton.setSelected(true);
            tmRandomRadioButton.setEnabled(true);
            tmFullHMCompatibilityCheckBox.setVisible(pokemonGeneration < 7);
            if (tmFullHMCompatibilityCheckBox.isVisible()) {
                tmFullHMCompatibilityCheckBox.setEnabled(true);
            }

            thcUnchangedRadioButton.setEnabled(true);
            thcUnchangedRadioButton.setSelected(true);
            thcRandomPreferSameTypeRadioButton.setEnabled(true);
            thcRandomCompletelyRadioButton.setEnabled(true);
            thcFullCompatibilityRadioButton.setEnabled(true);

            if (romHandler.hasMoveTutors()) {
                mtMovesPanel.setVisible(true);
                mtCompatPanel.setVisible(true);
                mtNoExistLabel.setVisible(false);

                mtUnchangedRadioButton.setEnabled(true);
                mtUnchangedRadioButton.setSelected(true);
                mtRandomRadioButton.setEnabled(true);

                mtcUnchangedRadioButton.setEnabled(true);
                mtcUnchangedRadioButton.setSelected(true);
                mtcRandomPreferSameTypeRadioButton.setEnabled(true);
                mtcRandomCompletelyRadioButton.setEnabled(true);
                mtcFullCompatibilityRadioButton.setEnabled(true);
            } else {
                mtMovesPanel.setVisible(false);
                mtCompatPanel.setVisible(false);
                mtNoExistLabel.setVisible(true);
            }

            fiUnchangedRadioButton.setEnabled(true);
            fiUnchangedRadioButton.setSelected(true);
            fiShuffleRadioButton.setEnabled(true);
            fiRandomRadioButton.setEnabled(true);
            fiRandomEvenDistributionRadioButton.setEnabled(true);

            shopItemsPanel.setVisible(romHandler.hasShopRandomization());
            shUnchangedRadioButton.setEnabled(true);
            shUnchangedRadioButton.setSelected(true);
            shShuffleRadioButton.setEnabled(true);
            shRandomRadioButton.setEnabled(true);

            int mtsAvailable = romHandler.miscTweaksAvailable();
            int mtCount = MiscTweak.allTweaks.size();
            List<JCheckBox> usableCheckBoxes = new ArrayList<>();

            for (int mti = 0; mti < mtCount; mti++) {
                MiscTweak mt = MiscTweak.allTweaks.get(mti);
                JCheckBox mtCB = tweakCheckBoxes.get(mti);
                mtCB.setSelected(false);
                if ((mtsAvailable & mt.getValue()) != 0) {
                    mtCB.setVisible(true);
                    mtCB.setEnabled(true);
                    usableCheckBoxes.add(mtCB);
                } else {
                    mtCB.setVisible(false);
                    mtCB.setEnabled(false);
                }
            }

            if (usableCheckBoxes.size() > 0) {
                setTweaksPanel(usableCheckBoxes);
                //tabbedPane1.setComponentAt(7,makeTweaksLayout(usableCheckBoxes));
                //miscTweaksPanel.setLayout(makeTweaksLayout(usableCheckBoxes));
            } else {
                mtNoneAvailableLabel.setVisible(true);
                liveTweaksPanel.setVisible(false);
                miscTweaksPanel.setVisible(true);
                //miscTweaksPanel.setLayout(noTweaksLayout);
            }

            if (romHandler.generationOfPokemon() < 6) {
                applyGameUpdateMenuItem.setVisible(false);
            } else {
                applyGameUpdateMenuItem.setVisible(true);
            }

            if (romHandler.hasGameUpdateLoaded()) {
                removeGameUpdateMenuItem.setVisible(true);
            } else {
                removeGameUpdateMenuItem.setVisible(false);
            }

            gameMascotLabel.setIcon(makeMascotIcon());

            if (romHandler instanceof AbstractDSRomHandler) {
                ((AbstractDSRomHandler) romHandler).closeInnerRom();
            } else if (romHandler instanceof Abstract3DSRomHandler) {
                ((Abstract3DSRomHandler) romHandler).closeInnerRom();
            }
        } catch (Exception e) {
            attemptToLogException(e, "GUI.processFailed","GUI.processFailedNoLog", null, null);
            romHandler = null;
            initialState();
        }
    }

    private void setTweaksPanel(List<JCheckBox> usableCheckBoxes) {
        mtNoneAvailableLabel.setVisible(false);
        miscTweaksPanel.setVisible(false);
        baseTweaksPanel.remove(liveTweaksPanel);
        makeTweaksLayout(usableCheckBoxes);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.1;
        c.weighty = 0.1;
        c.gridx = 1;
        c.gridy = 1;
        baseTweaksPanel.add(liveTweaksPanel,c);
        liveTweaksPanel.setVisible(true);
    }

    private void enableOrDisableSubControls() {

        if (limitPokemonCheckBox.isSelected()) {
            limitPokemonButton.setEnabled(true);
        } else {
            limitPokemonButton.setEnabled(false);
        }

        if (pbsUnchangedRadioButton.isSelected()) {
            pbsFollowEvolutionsCheckBox.setEnabled(false);
            pbsFollowEvolutionsCheckBox.setSelected(false);
            pbsFollowMegaEvosCheckBox.setEnabled(false);
            pbsFollowMegaEvosCheckBox.setSelected(false);
        } else {
            pbsFollowEvolutionsCheckBox.setEnabled(true);
            pbsFollowMegaEvosCheckBox.setEnabled(true);
        }

        if (pbsStandardizeEXPCurvesCheckBox.isSelected()) {
            pbsLegendariesSlowRadioButton.setEnabled(true);
            pbsStrongLegendariesSlowRadioButton.setEnabled(true);
            pbsAllMediumFastRadioButton.setEnabled(true);
            pbsEXPCurveComboBox.setEnabled(true);
        } else {
            pbsLegendariesSlowRadioButton.setEnabled(false);
            pbsLegendariesSlowRadioButton.setSelected(true);
            pbsStrongLegendariesSlowRadioButton.setEnabled(false);
            pbsAllMediumFastRadioButton.setEnabled(false);
            pbsEXPCurveComboBox.setEnabled(false);
        }

        if (pbsUpdateBaseStatsCheckBox.isSelected()) {
            pbsUpdateComboBox.setEnabled(true);
        } else {
            pbsUpdateComboBox.setEnabled(false);
        }

        if (ptUnchangedRadioButton.isSelected()) {
            ptFollowMegaEvosCheckBox.setEnabled(false);
            ptFollowMegaEvosCheckBox.setSelected(false);
        } else {
            ptFollowMegaEvosCheckBox.setEnabled(true);
        }

        if (paRandomRadioButton.isSelected()) {
            paAllowWonderGuardCheckBox.setEnabled(true);
            paFollowEvolutionsCheckBox.setEnabled(true);
            paTrappingAbilitiesCheckBox.setEnabled(true);
            paNegativeAbilitiesCheckBox.setEnabled(true);
            paBadAbilitiesCheckBox.setEnabled(true);
            paFollowMegaEvosCheckBox.setEnabled(true);
            paWeighDuplicatesTogetherCheckBox.setEnabled(true);
        } else {
            paAllowWonderGuardCheckBox.setEnabled(false);
            paAllowWonderGuardCheckBox.setSelected(false);
            paFollowEvolutionsCheckBox.setEnabled(false);
            paFollowEvolutionsCheckBox.setSelected(false);
            paTrappingAbilitiesCheckBox.setEnabled(false);
            paTrappingAbilitiesCheckBox.setSelected(false);
            paNegativeAbilitiesCheckBox.setEnabled(false);
            paNegativeAbilitiesCheckBox.setSelected(false);
            paBadAbilitiesCheckBox.setEnabled(false);
            paBadAbilitiesCheckBox.setSelected(false);
            paFollowMegaEvosCheckBox.setEnabled(false);
            paFollowMegaEvosCheckBox.setSelected(false);
            paWeighDuplicatesTogetherCheckBox.setEnabled(false);
            paWeighDuplicatesTogetherCheckBox.setSelected(false);
        }

        if (peRandomRadioButton.isSelected()) {
            peSimilarStrengthCheckBox.setEnabled(true);
            peSameTypingCheckBox.setEnabled(true);
            peLimitEvolutionsToThreeCheckBox.setEnabled(true);
            peForceChangeCheckBox.setEnabled(true);
            peAllowAltFormesCheckBox.setEnabled(true);
        } else {
            peSimilarStrengthCheckBox.setEnabled(false);
            peSimilarStrengthCheckBox.setSelected(false);
            peSameTypingCheckBox.setEnabled(false);
            peSameTypingCheckBox.setSelected(false);
            peLimitEvolutionsToThreeCheckBox.setEnabled(false);
            peLimitEvolutionsToThreeCheckBox.setSelected(false);
            peForceChangeCheckBox.setEnabled(false);
            peForceChangeCheckBox.setSelected(false);
            peAllowAltFormesCheckBox.setEnabled(false);
            peAllowAltFormesCheckBox.setSelected(false);
        }

        boolean spCustomStatus = spCustomRadioButton.isSelected();
        spComboBox1.setEnabled(spCustomStatus);
        spComboBox2.setEnabled(spCustomStatus);
        spComboBox3.setEnabled(spCustomStatus);

        if (spUnchangedRadioButton.isSelected()) {
            spAllowAltFormesCheckBox.setEnabled(false);
            spAllowAltFormesCheckBox.setSelected(false);
        } else {
            spAllowAltFormesCheckBox.setEnabled(true);
        }

        if (spRandomizeStarterHeldItemsCheckBox.isSelected()) {
            spBanBadItemsCheckBox.setEnabled(true);
        } else {
            spBanBadItemsCheckBox.setEnabled(false);
            spBanBadItemsCheckBox.setSelected(false);
        }

        if (stpUnchangedRadioButton.isSelected()) {
            stpRandomize600BSTCheckBox.setEnabled(false);
            stpRandomize600BSTCheckBox.setSelected(false);
            stpAllowAltFormesCheckBox.setEnabled(false);
            stpAllowAltFormesCheckBox.setSelected(false);
            stpSwapMegaEvosCheckBox.setEnabled(false);
            stpSwapMegaEvosCheckBox.setSelected(false);
        } else {
            stpRandomize600BSTCheckBox.setEnabled(true);
            stpAllowAltFormesCheckBox.setEnabled(true);
            stpSwapMegaEvosCheckBox.setEnabled(true);
        }

        if (stpRandomSimilarStrengthRadioButton.isSelected()) {
            stpLimitMainGameLegendariesCheckBox.setEnabled(stpLimitMainGameLegendariesCheckBox.isVisible());
        } else {
            stpLimitMainGameLegendariesCheckBox.setEnabled(false);
            stpLimitMainGameLegendariesCheckBox.setSelected(false);
        }

        if (stpPercentageLevelModifierCheckBox.isSelected()) {
            stpPercentageLevelModifierSlider.setEnabled(true);
        } else {
            stpPercentageLevelModifierSlider.setEnabled(false);
            stpPercentageLevelModifierSlider.setValue(0);
        }

        if (igtUnchangedRadioButton.isSelected()) {
            igtRandomizeItemsCheckBox.setEnabled(false);
            igtRandomizeItemsCheckBox.setSelected(false);
            igtRandomizeIVsCheckBox.setEnabled(false);
            igtRandomizeIVsCheckBox.setSelected(false);
            igtRandomizeNicknamesCheckBox.setEnabled(false);
            igtRandomizeNicknamesCheckBox.setSelected(false);
            igtRandomizeOTsCheckBox.setEnabled(false);
            igtRandomizeOTsCheckBox.setSelected(false);
        } else {
            igtRandomizeItemsCheckBox.setEnabled(true);
            igtRandomizeIVsCheckBox.setEnabled(true);
            igtRandomizeNicknamesCheckBox.setEnabled(true);
            igtRandomizeOTsCheckBox.setEnabled(true);
        }

        if (mdUpdateMovesCheckBox.isSelected()) {
            mdUpdateComboBox.setEnabled(true);
        } else {
            mdUpdateComboBox.setEnabled(false);
        }

        if (pmsMetronomeOnlyModeRadioButton.isSelected() || pmsUnchangedRadioButton.isSelected()) {
            pmsGuaranteedLevel1MovesCheckBox.setEnabled(false);
            pmsGuaranteedLevel1MovesCheckBox.setSelected(false);
            pmsForceGoodDamagingCheckBox.setEnabled(false);
            pmsForceGoodDamagingCheckBox.setSelected(false);
            pmsReorderDamagingMovesCheckBox.setEnabled(false);
            pmsReorderDamagingMovesCheckBox.setSelected(false);
            pmsNoGameBreakingMovesCheckBox.setEnabled(false);
            pmsNoGameBreakingMovesCheckBox.setSelected(false);
            pmsEvolutionMovesCheckBox.setEnabled(false);
            pmsEvolutionMovesCheckBox.setSelected(false);
        } else {
            pmsGuaranteedLevel1MovesCheckBox.setEnabled(true);
            pmsForceGoodDamagingCheckBox.setEnabled(true);
            pmsReorderDamagingMovesCheckBox.setEnabled(true);
            pmsNoGameBreakingMovesCheckBox.setEnabled(true);
            pmsEvolutionMovesCheckBox.setEnabled(true);
        }

        if (pmsGuaranteedLevel1MovesCheckBox.isSelected()) {
            pmsGuaranteedLevel1MovesSlider.setEnabled(true);
        } else {
            pmsGuaranteedLevel1MovesSlider.setEnabled(false);
            pmsGuaranteedLevel1MovesSlider.setValue(pmsGuaranteedLevel1MovesSlider.getMinimum());
        }

        if (pmsForceGoodDamagingCheckBox.isSelected()) {
            pmsForceGoodDamagingSlider.setEnabled(true);
        } else {
            pmsForceGoodDamagingSlider.setEnabled(false);
            pmsForceGoodDamagingSlider.setValue(pmsForceGoodDamagingSlider.getMinimum());
        }

        if (tpUnchangedRadioButton.isSelected()) {
            tpSimilarStrengthCheckBox.setEnabled(false);
            tpSimilarStrengthCheckBox.setSelected(false);
            tpDontUseLegendariesCheckBox.setEnabled(false);
            tpDontUseLegendariesCheckBox.setSelected(false);
            tpNoEarlyWonderGuardCheckBox.setEnabled(false);
            tpNoEarlyWonderGuardCheckBox.setSelected(false);
            tpAllowAlternateFormesCheckBox.setEnabled(false);
            tpAllowAlternateFormesCheckBox.setSelected(false);
            tpSwapMegaEvosCheckBox.setEnabled(false);
            tpSwapMegaEvosCheckBox.setSelected(false);
            tpRandomShinyTrainerPokemonCheckBox.setEnabled(false);
            tpRandomShinyTrainerPokemonCheckBox.setSelected(false);
            tpBossTrainersCheckBox.setEnabled(false);
            tpBossTrainersCheckBox.setSelected(false);
            tpImportantTrainersCheckBox.setEnabled(false);
            tpImportantTrainersCheckBox.setSelected(false);
            tpRegularTrainersCheckBox.setEnabled(false);
            tpRegularTrainersCheckBox.setSelected(false);
        } else {
            tpSimilarStrengthCheckBox.setEnabled(true);
            tpDontUseLegendariesCheckBox.setEnabled(true);
            tpNoEarlyWonderGuardCheckBox.setEnabled(true);
            tpAllowAlternateFormesCheckBox.setEnabled(true);
            if (currentRestrictions == null || currentRestrictions.megaEvolutionsAreInPool(romHandler.forceSwapStaticMegaEvos())) {
                tpSwapMegaEvosCheckBox.setEnabled(true);
            } else {
                tpSwapMegaEvosCheckBox.setEnabled(false);
                tpSwapMegaEvosCheckBox.setSelected(false);
            }
            tpRandomShinyTrainerPokemonCheckBox.setEnabled(true);
            tpBossTrainersCheckBox.setEnabled(tpBossTrainersCheckBox.isVisible());
            tpImportantTrainersCheckBox.setEnabled(tpImportantTrainersCheckBox.isVisible());
            tpRegularTrainersCheckBox.setEnabled(tpRegularTrainersCheckBox.isVisible());
        }

        if (tpForceFullyEvolvedAtCheckBox.isSelected()) {
            tpForceFullyEvolvedAtSlider.setEnabled(true);
        } else {
            tpForceFullyEvolvedAtSlider.setEnabled(false);
            tpForceFullyEvolvedAtSlider.setValue(tpForceFullyEvolvedAtSlider.getMinimum());
        }

        if (tpPercentageLevelModifierCheckBox.isSelected()) {
            tpPercentageLevelModifierSlider.setEnabled(true);
        } else {
            tpPercentageLevelModifierSlider.setEnabled(false);
            tpPercentageLevelModifierSlider.setValue(0);
        }

        if (tpBossTrainersCheckBox.isSelected()) {
            tpBossTrainersSpinner.setEnabled(true);
        } else {
            tpBossTrainersSpinner.setEnabled(false);
            tpBossTrainersSpinner.setValue(1);
        }

        if (tpImportantTrainersCheckBox.isSelected()) {
            tpImportantTrainersSpinner.setEnabled(true);
        } else {
            tpImportantTrainersSpinner.setEnabled(false);
            tpImportantTrainersSpinner.setValue(1);
        }

        if (tpRegularTrainersCheckBox.isSelected()) {
            tpRegularTrainersSpinner.setEnabled(true);
        } else {
            tpRegularTrainersSpinner.setEnabled(false);
            tpRegularTrainersSpinner.setValue(1);
        }

        if (!spUnchangedRadioButton.isSelected() || !this.tpUnchangedRadioButton.isSelected()) {
            tpRivalCarriesStarterCheckBox.setEnabled(true);
        } else {
            tpRivalCarriesStarterCheckBox.setEnabled(false);
            tpRivalCarriesStarterCheckBox.setSelected(false);
        }

        if (tpTypeThemedRadioButton.isSelected()) {
            tpWeightTypesCheckBox.setEnabled(true);
        } else {
            tpWeightTypesCheckBox.setEnabled(false);
            tpWeightTypesCheckBox.setSelected(false);
        }

        if (!totpUnchangedRadioButton.isSelected() || !totpAllyUnchangedRadioButton.isSelected()) {
            totpAllowAltFormesCheckBox.setEnabled(true);
        } else {
            totpAllowAltFormesCheckBox.setEnabled(false);
            totpAllowAltFormesCheckBox.setSelected(false);
        }

        if (totpPercentageLevelModifierCheckBox.isSelected()) {
            totpPercentageLevelModifierSlider.setEnabled(true);
        } else {
            totpPercentageLevelModifierSlider.setEnabled(false);
            totpPercentageLevelModifierSlider.setValue(0);
        }

        if (wpRandomRadioButton.isSelected()) {
            wpARNoneRadioButton.setEnabled(true);
            wpARSimilarStrengthRadioButton.setEnabled(true);
            wpARCatchEmAllModeRadioButton.setEnabled(true);
            wpARTypeThemeAreasRadioButton.setEnabled(true);
            wpBalanceShakingGrassPokemonCheckBox.setEnabled(true);
        } else if (wpArea1To1RadioButton.isSelected()) {
            wpARNoneRadioButton.setEnabled(true);
            wpARSimilarStrengthRadioButton.setEnabled(true);
            wpARCatchEmAllModeRadioButton.setEnabled(true);
            wpARTypeThemeAreasRadioButton.setEnabled(true);
            wpBalanceShakingGrassPokemonCheckBox.setEnabled(false);
        } else if (wpGlobal1To1RadioButton.isSelected()) {
            if (wpARCatchEmAllModeRadioButton.isSelected() || wpARTypeThemeAreasRadioButton.isSelected()) {
                wpARNoneRadioButton.setSelected(true);
            }
            wpARNoneRadioButton.setEnabled(true);
            wpARSimilarStrengthRadioButton.setEnabled(true);
            wpARCatchEmAllModeRadioButton.setEnabled(false);
            wpARTypeThemeAreasRadioButton.setEnabled(false);
            wpBalanceShakingGrassPokemonCheckBox.setEnabled(false);
        } else {
            wpARNoneRadioButton.setEnabled(false);
            wpARNoneRadioButton.setSelected(true);
            wpARSimilarStrengthRadioButton.setEnabled(false);
            wpARCatchEmAllModeRadioButton.setEnabled(false);
            wpARTypeThemeAreasRadioButton.setEnabled(false);
            wpBalanceShakingGrassPokemonCheckBox.setEnabled(false);
        }

        if (wpUnchangedRadioButton.isSelected()) {
            wpUseTimeBasedEncountersCheckBox.setEnabled(false);
            wpUseTimeBasedEncountersCheckBox.setSelected(false);
            wpDontUseLegendariesCheckBox.setEnabled(false);
            wpDontUseLegendariesCheckBox.setSelected(false);
            wpAllowAltFormesCheckBox.setEnabled(false);
            wpAllowAltFormesCheckBox.setSelected(false);
        } else {
            wpUseTimeBasedEncountersCheckBox.setEnabled(true);
            wpDontUseLegendariesCheckBox.setEnabled(true);
            wpAllowAltFormesCheckBox.setEnabled(true);
        }

        if (wpRandomizeHeldItemsCheckBox.isSelected()
                && wpRandomizeHeldItemsCheckBox.isVisible()
                && wpRandomizeHeldItemsCheckBox.isEnabled()) { // ??? why all three
            wpBanBadItemsCheckBox.setEnabled(true);
        } else {
            wpBanBadItemsCheckBox.setEnabled(false);
            wpBanBadItemsCheckBox.setSelected(false);
        }

        if (wpSetMinimumCatchRateCheckBox.isSelected()) {
            wpSetMinimumCatchRateSlider.setEnabled(true);
        } else {
            wpSetMinimumCatchRateSlider.setEnabled(false);
            wpSetMinimumCatchRateSlider.setValue(0);
        }

        if (wpPercentageLevelModifierCheckBox.isSelected()) {
            wpPercentageLevelModifierSlider.setEnabled(true);
        } else {
            wpPercentageLevelModifierSlider.setEnabled(false);
            wpPercentageLevelModifierSlider.setValue(0);
        }

        if (pmsMetronomeOnlyModeRadioButton.isSelected()) {
            tmUnchangedRadioButton.setEnabled(false);
            tmRandomRadioButton.setEnabled(false);
            tmUnchangedRadioButton.setSelected(true);

            mtUnchangedRadioButton.setEnabled(false);
            mtRandomRadioButton.setEnabled(false);
            mtUnchangedRadioButton.setSelected(true);

            tmLevelupMoveSanityCheckBox.setEnabled(false);
            tmLevelupMoveSanityCheckBox.setSelected(false);
            tmKeepFieldMoveTMsCheckBox.setEnabled(false);
            tmKeepFieldMoveTMsCheckBox.setSelected(false);
            tmForceGoodDamagingCheckBox.setEnabled(false);
            tmForceGoodDamagingCheckBox.setSelected(false);
            tmNoGameBreakingMovesCheckBox.setEnabled(false);
            tmNoGameBreakingMovesCheckBox.setSelected(false);
            tmFollowEvolutionsCheckBox.setEnabled(false);
            tmFollowEvolutionsCheckBox.setSelected(false);

            mtLevelupMoveSanityCheckBox.setEnabled(false);
            mtLevelupMoveSanityCheckBox.setSelected(false);
            mtKeepFieldMoveTutorsCheckBox.setEnabled(false);
            mtKeepFieldMoveTutorsCheckBox.setSelected(false);
            mtForceGoodDamagingCheckBox.setEnabled(false);
            mtForceGoodDamagingCheckBox.setSelected(false);
            mtNoGameBreakingMovesCheckBox.setEnabled(false);
            mtNoGameBreakingMovesCheckBox.setSelected(false);
            mtFollowEvolutionsCheckBox.setEnabled(false);
            mtFollowEvolutionsCheckBox.setSelected(false);
        } else {
            tmUnchangedRadioButton.setEnabled(true);
            tmRandomRadioButton.setEnabled(true);

            mtUnchangedRadioButton.setEnabled(true);
            mtRandomRadioButton.setEnabled(true);

            if (!(pmsUnchangedRadioButton.isSelected()) || !(tmUnchangedRadioButton.isSelected())
                    || !(thcUnchangedRadioButton.isSelected())) {
                tmLevelupMoveSanityCheckBox.setEnabled(true);
            } else {
                tmLevelupMoveSanityCheckBox.setEnabled(false);
                tmLevelupMoveSanityCheckBox.setSelected(false);
            }

            if ((!thcUnchangedRadioButton.isSelected()) || (tmLevelupMoveSanityCheckBox.isSelected())) {
                tmFollowEvolutionsCheckBox.setEnabled(true);
            }
            else {
                tmFollowEvolutionsCheckBox.setEnabled(false);
                tmFollowEvolutionsCheckBox.setSelected(false);
            }

            if (!(tmUnchangedRadioButton.isSelected())) {
                tmKeepFieldMoveTMsCheckBox.setEnabled(true);
                tmForceGoodDamagingCheckBox.setEnabled(true);
                tmNoGameBreakingMovesCheckBox.setEnabled(true);
            } else {
                tmKeepFieldMoveTMsCheckBox.setEnabled(false);
                tmKeepFieldMoveTMsCheckBox.setSelected(false);
                tmForceGoodDamagingCheckBox.setEnabled(false);
                tmForceGoodDamagingCheckBox.setSelected(false);
                tmNoGameBreakingMovesCheckBox.setEnabled(false);
                tmNoGameBreakingMovesCheckBox.setSelected(false);
            }

            if (romHandler.hasMoveTutors()
                    && (!(pmsUnchangedRadioButton.isSelected()) || !(mtUnchangedRadioButton.isSelected())
                    || !(mtcUnchangedRadioButton.isSelected()))) {
                mtLevelupMoveSanityCheckBox.setEnabled(true);
            } else {
                mtLevelupMoveSanityCheckBox.setEnabled(false);
                mtLevelupMoveSanityCheckBox.setSelected(false);
            }

            if (!(mtcUnchangedRadioButton.isSelected()) || (mtLevelupMoveSanityCheckBox.isSelected())) {
                mtFollowEvolutionsCheckBox.setEnabled(true);
            }
            else {
                mtFollowEvolutionsCheckBox.setEnabled(false);
                mtFollowEvolutionsCheckBox.setSelected(false);
            }

            if (romHandler.hasMoveTutors() && !(mtUnchangedRadioButton.isSelected())) {
                mtKeepFieldMoveTutorsCheckBox.setEnabled(true);
                mtForceGoodDamagingCheckBox.setEnabled(true);
                mtNoGameBreakingMovesCheckBox.setEnabled(true);
            } else {
                mtKeepFieldMoveTutorsCheckBox.setEnabled(false);
                mtKeepFieldMoveTutorsCheckBox.setSelected(false);
                mtForceGoodDamagingCheckBox.setEnabled(false);
                mtForceGoodDamagingCheckBox.setSelected(false);
                mtNoGameBreakingMovesCheckBox.setEnabled(false);
                mtNoGameBreakingMovesCheckBox.setSelected(false);
            }
        }

        if (tmForceGoodDamagingCheckBox.isSelected()) {
            tmForceGoodDamagingSlider.setEnabled(true);
        } else {
            tmForceGoodDamagingSlider.setEnabled(false);
            tmForceGoodDamagingSlider.setValue(tmForceGoodDamagingSlider.getMinimum());
        }

        if (mtForceGoodDamagingCheckBox.isSelected()) {
            mtForceGoodDamagingSlider.setEnabled(true);
        } else {
            mtForceGoodDamagingSlider.setEnabled(false);
            mtForceGoodDamagingSlider.setValue(mtForceGoodDamagingSlider.getMinimum());
        }

        tmFullHMCompatibilityCheckBox.setEnabled(!thcFullCompatibilityRadioButton.isSelected());

        if (fiRandomRadioButton.isSelected() && fiRandomRadioButton.isVisible() && fiRandomRadioButton.isEnabled()) {
            fiBanBadItemsCheckBox.setEnabled(true);
        } else if (fiRandomEvenDistributionRadioButton.isSelected() && fiRandomEvenDistributionRadioButton.isVisible()
                && fiRandomEvenDistributionRadioButton.isEnabled()) {
            fiBanBadItemsCheckBox.setEnabled(true);
        } else {
            fiBanBadItemsCheckBox.setEnabled(false);
            fiBanBadItemsCheckBox.setSelected(false);
        }

        if (shRandomRadioButton.isSelected() && shRandomRadioButton.isVisible() && shRandomRadioButton.isEnabled()) {
            shBanBadItemsCheckBox.setEnabled(true);
            shBanRegularShopItemsCheckBox.setEnabled(true);
            shBanOverpoweredShopItemsCheckBox.setEnabled(true);
            shBalanceShopItemPricesCheckBox.setEnabled(true);
            shGuaranteeEvolutionItemsCheckBox.setEnabled(true);
            shGuaranteeXItemsCheckBox.setEnabled(true);
        } else {
            shBanBadItemsCheckBox.setEnabled(false);
            shBanBadItemsCheckBox.setSelected(false);
            shBanRegularShopItemsCheckBox.setEnabled(false);
            shBanRegularShopItemsCheckBox.setSelected(false);
            shBanOverpoweredShopItemsCheckBox.setEnabled(false);
            shBanOverpoweredShopItemsCheckBox.setSelected(false);
            shBalanceShopItemPricesCheckBox.setEnabled(false);
            shBalanceShopItemPricesCheckBox.setSelected(false);
            shGuaranteeEvolutionItemsCheckBox.setEnabled(false);
            shGuaranteeEvolutionItemsCheckBox.setSelected(false);
            shGuaranteeXItemsCheckBox.setEnabled(false);
            shGuaranteeXItemsCheckBox.setSelected(false);
        }
    }

    private void initTweaksPanel() {
        tweakCheckBoxes = new ArrayList<>();
        int numTweaks = MiscTweak.allTweaks.size();
        for (int i = 0; i < numTweaks; i++) {
            MiscTweak ct = MiscTweak.allTweaks.get(i);
            JCheckBox tweakBox = new JCheckBox();
            tweakBox.setText(ct.getTweakName());
            tweakBox.setToolTipText(ct.getTooltipText());
            tweakCheckBoxes.add(tweakBox);
        }
    }

    private void makeTweaksLayout(List<JCheckBox> tweaks) {
        liveTweaksPanel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Misc. Tweaks");
        border.setTitleFont(border.getTitleFont().deriveFont(Font.BOLD));
        liveTweaksPanel.setBorder(border);

        int numTweaks = tweaks.size();
        Iterator<JCheckBox> tweaksIterator = tweaks.iterator();

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(5,5,0,5);

        int TWEAK_COLS = 4;
        int numCols = Math.min(TWEAK_COLS, numTweaks);

        for (int row = 0; row <= numTweaks / numCols; row++) {
            for (int col = 0; col < numCols; col++) {
                if (!tweaksIterator.hasNext()) break;
                c.gridx = col;
                c.gridy = row;
                liveTweaksPanel.add(tweaksIterator.next(),c);
            }
        }

        // Pack the checkboxes together

        GridBagConstraints horizontalC = new GridBagConstraints();
        horizontalC.gridx = numCols;
        horizontalC.gridy = 0;
        horizontalC.weightx = 0.1;

        GridBagConstraints verticalC = new GridBagConstraints();
        verticalC.gridx = 0;
        verticalC.gridy = (numTweaks / numCols) + 1;
        verticalC.weighty = 0.1;

        liveTweaksPanel.add(new JSeparator(SwingConstants.HORIZONTAL),horizontalC);
        liveTweaksPanel.add(new JSeparator(SwingConstants.VERTICAL),verticalC);
    }

    private void populateDropdowns() {
        List<Pokemon> currentStarters = romHandler.getStarters();
        List<Pokemon> allPokes =
                romHandler.generationOfPokemon() >= 6 ?
                        romHandler.getPokemonInclFormes()
                                .stream()
                                .filter(pk -> pk == null || !pk.actuallyCosmetic)
                                .collect(Collectors.toList()) :
                        romHandler.getPokemon();
        String[] pokeNames = new String[allPokes.size() - 1];
        for (int i = 1; i < allPokes.size(); i++) {
            pokeNames[i - 1] = allPokes.get(i).fullName();
        }
        spComboBox1.setModel(new DefaultComboBoxModel<>(pokeNames));
        spComboBox1.setSelectedIndex(allPokes.indexOf(currentStarters.get(0)) - 1);
        spComboBox2.setModel(new DefaultComboBoxModel<>(pokeNames));
        spComboBox2.setSelectedIndex(allPokes.indexOf(currentStarters.get(1)) - 1);
        if (!romHandler.isYellow()) {
            spComboBox3.setModel(new DefaultComboBoxModel<>(pokeNames));
            spComboBox3.setSelectedIndex(allPokes.indexOf(currentStarters.get(2)) - 1);
        }

        String[] baseStatGenerationNumbers = new String[Math.min(3, GlobalConstants.HIGHEST_POKEMON_GEN - romHandler.generationOfPokemon())];
        int j = Math.max(6,romHandler.generationOfPokemon() + 1);
        for (int i = 0; i < baseStatGenerationNumbers.length; i++) {
            baseStatGenerationNumbers[i] = String.valueOf(j);
            j++;
        }
        pbsUpdateComboBox.setModel(new DefaultComboBoxModel<>(baseStatGenerationNumbers));

        String[] moveGenerationNumbers = new String[GlobalConstants.HIGHEST_POKEMON_GEN - romHandler.generationOfPokemon()];
        j = romHandler.generationOfPokemon() + 1;
        for (int i = 0; i < moveGenerationNumbers.length; i++) {
            moveGenerationNumbers[i] = String.valueOf(j);
            j++;
        }
        mdUpdateComboBox.setModel(new DefaultComboBoxModel<>(moveGenerationNumbers));

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
            nImg.getGraphics().drawImage(handlerImg, 64 - hW / 2, 64 - hH / 2, frame);
            return new ImageIcon(nImg);
        } catch (Exception ex) {
            return emptyIcon;
        }
    }

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
            int response = JOptionPane.showConfirmDialog(frame,
                    bundle.getString("GUI.convertNameFilesDialog.text"),
                    bundle.getString("GUI.convertNameFilesDialog.title"), JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                try {
                    CustomNamesSet newNamesData = CustomNamesSet.importOldNames();
                    byte[] data = newNamesData.getBytes();
                    FileFunctions.writeBytesToFile(SysConstants.customNamesFile, data);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, bundle.getString("GUI.convertNameFilesFailed"));
                }
            }

            haveCheckedCustomNames = true;
            attemptWriteConfig();
        }

    }

    private void attemptReadConfig() {
        // Things that should be true by default should be manually set here
        unloadGameOnSuccess = true;
        File fh = new File(SysConstants.ROOT_PATH + "config.ini");
        if (!fh.exists() || !fh.canRead()) {
            return;
        }

        try {
            Scanner sc = new Scanner(fh, "UTF-8");
            boolean isReadingUpdates = false;
            while (sc.hasNextLine()) {
                String q = sc.nextLine().trim();
                if (q.contains("//")) {
                    q = q.substring(0, q.indexOf("//")).trim();
                }
                if (q.equals("[Game Updates]")) {
                    isReadingUpdates = true;
                    continue;
                }
                if (!q.isEmpty()) {
                    String[] tokens = q.split("=", 2);
                    if (tokens.length == 2) {
                        String key = tokens[0].trim();
                        if (isReadingUpdates) {
                            gameUpdates.put(key, tokens[1]);
                        }
                        if (key.equalsIgnoreCase("checkedcustomnames172")) {
                            haveCheckedCustomNames = Boolean.parseBoolean(tokens[1].trim());
                        }
                        if (key.equals("firststart")) {
                            String val = tokens[1];
                            if (val.equals(Version.VERSION_STRING)) {
                                initialPopup = false;
                            }
                        }
                        if (key.equals("unloadgameonsuccess")) {
                            unloadGameOnSuccess = Boolean.parseBoolean(tokens[1].trim());
                        }
                    }
                } else if (isReadingUpdates) {
                    isReadingUpdates = false;
                }
            }
            sc.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean attemptWriteConfig() {
        File fh = new File(SysConstants.ROOT_PATH + "config.ini");
        if (fh.exists() && !fh.canWrite()) {
            return false;
        }

        try {
            PrintStream ps = new PrintStream(new FileOutputStream(fh), true, "UTF-8");
            ps.println("checkedcustomnames=true");
            ps.println("checkedcustomnames172=" + haveCheckedCustomNames);
            ps.println("unloadgameonsuccess=" + unloadGameOnSuccess);
            if (!initialPopup) {
                ps.println("firststart=" + Version.VERSION_STRING);
            }
            if (gameUpdates.size() > 0) {
                ps.println();
                ps.println("[Game Updates]");
                for (Map.Entry<String, String> update : gameUpdates.entrySet()) {
                    ps.format("%s=%s", update.getKey(), update.getValue());
                    ps.println();
                }
            }
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
                    String.format(bundle.getString("GUI.configFileMissing"), e.getMessage()));
            System.exit(1);
        }
    }

    private ExpCurve[] getEXPCurvesForGeneration(int generation) {
        ExpCurve[] result;
        if (generation < 3) {
            result = new ExpCurve[]{ ExpCurve.MEDIUM_FAST, ExpCurve.MEDIUM_SLOW, ExpCurve.FAST, ExpCurve.SLOW };
        } else {
            result = new ExpCurve[]{ ExpCurve.MEDIUM_FAST, ExpCurve.MEDIUM_SLOW, ExpCurve.FAST, ExpCurve.SLOW, ExpCurve.ERRATIC, ExpCurve.FLUCTUATING };
        }
        return result;
    }

    public static void main(String[] args) {
        launcherInput = args.length > 0 ? args[0] : "";
        if (launcherInput.equals("please-use-the-launcher")) usedLauncher = true;
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("NewRandomizerGUI");
            try {
                String lafName = UIManager.getSystemLookAndFeelClassName();
                // NEW: Only set Native LaF on windows.
                if (lafName.equalsIgnoreCase("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
                    UIManager.setLookAndFeel(lafName);
                }
            } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ex) {
                Logger.getLogger(NewRandomizerGUI.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
            frame.setContentPane(new NewRandomizerGUI().mainPanel);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
