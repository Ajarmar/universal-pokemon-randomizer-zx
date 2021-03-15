package com.dabomstew.pkrandom.newgui;

/*----------------------------------------------------------------------------*/
/*--  NewGenerationLimitDialog.java - a GUI interface to allow users to     --*/
/*--                                  limit which Pokemon appear based on   --*/
/*--                                  their generation of origin.           --*/
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

import com.dabomstew.pkrandom.pokemon.GenRestrictions;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;

public class NewGenerationLimitDialog extends JDialog {
    private JCheckBox gen1CheckBox;
    private JCheckBox gen2CheckBox;
    private JCheckBox gen3CheckBox;
    private JCheckBox gen4CheckBox;
    private JCheckBox gen5CheckBox;
    private JCheckBox gen6CheckBox;
    private JCheckBox gen7CheckBox;
    private JButton okButton;
    private JButton cancelButton;
    private JCheckBox gen1AssocGen2CheckBox;
    private JCheckBox gen1AssocGen4CheckBox;
    private JCheckBox gen1AssocGen6CheckBox;
    private JCheckBox gen2AssocGen1CheckBox;
    private JCheckBox gen2AssocGen3CheckBox;
    private JCheckBox gen2AssocGen4CheckBox;
    private JCheckBox gen3AssocGen2CheckBox;
    private JCheckBox gen3AssocGen4CheckBox;
    private JCheckBox gen4AssocGen1CheckBox;
    private JCheckBox gen4AssocGen2CheckBox;
    private JCheckBox gen4AssocGen3CheckBox;
    private JCheckBox gen6AssocGen1CheckBox;
    private JPanel mainPanel;
    private JLabel xyWarningLabel;

    private boolean pressedOk;
    private boolean isXY;

    public NewGenerationLimitDialog(JFrame parent, GenRestrictions current, int generation, boolean isXY) {
        super(parent, true);
        add(mainPanel);
        this.isXY = isXY;
        initComponents();
        initialState(generation);
        if (current != null) {
            current.limitToGen(generation);
            restoreFrom(current);
        }
        enableAndDisableBoxes();
        pressedOk = false;
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public boolean pressedOK() {
        return pressedOk;
    }

    public GenRestrictions getChoice() {
        GenRestrictions gr = new GenRestrictions();
        gr.allow_gen1 = gen1CheckBox.isSelected();
        gr.allow_gen2 = gen2CheckBox.isSelected();
        gr.allow_gen3 = gen3CheckBox.isSelected();
        gr.allow_gen4 = gen4CheckBox.isSelected();
        gr.allow_gen5 = gen5CheckBox.isSelected();
        gr.allow_gen6 = gen6CheckBox.isSelected();
        gr.allow_gen7 = gen7CheckBox.isSelected();

        gr.assoc_g1_g2 = gen1AssocGen2CheckBox.isSelected();
        gr.assoc_g1_g4 = gen1AssocGen4CheckBox.isSelected();
        gr.assoc_g1_g6 = gen1AssocGen6CheckBox.isSelected();

        gr.assoc_g2_g1 = gen2AssocGen1CheckBox.isSelected();
        gr.assoc_g2_g3 = gen2AssocGen3CheckBox.isSelected();
        gr.assoc_g2_g4 = gen2AssocGen4CheckBox.isSelected();

        gr.assoc_g3_g2 = gen3AssocGen2CheckBox.isSelected();
        gr.assoc_g3_g4 = gen3AssocGen4CheckBox.isSelected();

        gr.assoc_g4_g1 = gen4AssocGen1CheckBox.isSelected();
        gr.assoc_g4_g2 = gen4AssocGen2CheckBox.isSelected();
        gr.assoc_g4_g3 = gen4AssocGen3CheckBox.isSelected();

        gr.assoc_g6_g1 = gen6AssocGen1CheckBox.isSelected();

        return gr;
    }

    private void initialState(int generation) {
        if (generation < 2) {
            gen2CheckBox.setVisible(false);
            gen1AssocGen2CheckBox.setVisible(false);
            gen2AssocGen1CheckBox.setVisible(false);
            gen2AssocGen3CheckBox.setVisible(false);
            gen2AssocGen4CheckBox.setVisible(false);
        }
        if (generation < 3) {
            gen3CheckBox.setVisible(false);
            gen2AssocGen3CheckBox.setVisible(false);
            gen3AssocGen2CheckBox.setVisible(false);
            gen3AssocGen4CheckBox.setVisible(false);
        }
        if (generation < 4) {
            gen4CheckBox.setVisible(false);
            gen1AssocGen4CheckBox.setVisible(false);
            gen2AssocGen4CheckBox.setVisible(false);
            gen3AssocGen4CheckBox.setVisible(false);
            gen4AssocGen1CheckBox.setVisible(false);
            gen4AssocGen2CheckBox.setVisible(false);
            gen4AssocGen3CheckBox.setVisible(false);
        }
        if (generation < 5) {
            gen5CheckBox.setVisible(false);
        }
        if (generation < 6) {
            gen6CheckBox.setVisible(false);
            gen1AssocGen6CheckBox.setVisible(false);
            gen6AssocGen1CheckBox.setVisible(false);
        }
        if (generation < 7) {
            gen7CheckBox.setVisible(false);
        }
    }

    private void restoreFrom(GenRestrictions restrict) {
        gen1CheckBox.setSelected(restrict.allow_gen1);
        gen2CheckBox.setSelected(restrict.allow_gen2);
        gen3CheckBox.setSelected(restrict.allow_gen3);
        gen4CheckBox.setSelected(restrict.allow_gen4);
        gen5CheckBox.setSelected(restrict.allow_gen5);
        gen6CheckBox.setSelected(restrict.allow_gen6);
        gen7CheckBox.setSelected(restrict.allow_gen7);

        gen1AssocGen2CheckBox.setSelected(restrict.assoc_g1_g2);
        gen1AssocGen4CheckBox.setSelected(restrict.assoc_g1_g4);
        gen1AssocGen6CheckBox.setSelected(restrict.assoc_g1_g6);

        gen2AssocGen1CheckBox.setSelected(restrict.assoc_g2_g1);
        gen2AssocGen3CheckBox.setSelected(restrict.assoc_g2_g3);
        gen2AssocGen4CheckBox.setSelected(restrict.assoc_g2_g4);

        gen3AssocGen2CheckBox.setSelected(restrict.assoc_g3_g2);
        gen3AssocGen4CheckBox.setSelected(restrict.assoc_g3_g4);

        gen4AssocGen1CheckBox.setSelected(restrict.assoc_g4_g1);
        gen4AssocGen2CheckBox.setSelected(restrict.assoc_g4_g2);
        gen4AssocGen3CheckBox.setSelected(restrict.assoc_g4_g3);

        gen6AssocGen1CheckBox.setSelected(restrict.assoc_g6_g1);
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ResourceBundle bundle = ResourceBundle.getBundle("com/dabomstew/pkrandom/newgui/Bundle");
        setTitle(bundle.getString("GenerationLimitDialog.title"));
        gen1CheckBox.addActionListener(ev -> enableAndDisableBoxes());
        gen2CheckBox.addActionListener(ev -> enableAndDisableBoxes());
        gen3CheckBox.addActionListener(ev -> enableAndDisableBoxes());
        gen4CheckBox.addActionListener(ev -> enableAndDisableBoxes());
        gen5CheckBox.addActionListener(ev -> enableAndDisableBoxes());
        gen6CheckBox.addActionListener(ev -> enableAndDisableBoxes());
        gen7CheckBox.addActionListener(ev -> enableAndDisableBoxes());
        okButton.addActionListener(evt -> okButtonActionPerformed());
        cancelButton.addActionListener(evt -> cancelButtonActionPerformed());
        xyWarningLabel.setVisible(isXY);
        if (isXY) {
            okButton.setEnabled(false);
        }
        pack();
    }

    private void enableAndDisableBoxes() {
        // enable sub-boxes of checked main boxes
        gen1AssocGen2CheckBox.setEnabled(gen1CheckBox.isSelected());
        gen1AssocGen4CheckBox.setEnabled(gen1CheckBox.isSelected());
        gen1AssocGen6CheckBox.setEnabled(gen1CheckBox.isSelected());
        gen2AssocGen1CheckBox.setEnabled(gen2CheckBox.isSelected());
        gen2AssocGen3CheckBox.setEnabled(gen2CheckBox.isSelected());
        gen2AssocGen4CheckBox.setEnabled(gen2CheckBox.isSelected());
        gen3AssocGen2CheckBox.setEnabled(gen3CheckBox.isSelected());
        gen3AssocGen4CheckBox.setEnabled(gen3CheckBox.isSelected());
        gen4AssocGen1CheckBox.setEnabled(gen4CheckBox.isSelected());
        gen4AssocGen2CheckBox.setEnabled(gen4CheckBox.isSelected());
        gen4AssocGen3CheckBox.setEnabled(gen4CheckBox.isSelected());
        gen6AssocGen1CheckBox.setEnabled(gen6CheckBox.isSelected());

        // uncheck disabled sub-boxes
        if (!gen1CheckBox.isSelected()) {
            gen1AssocGen2CheckBox.setSelected(false);
            gen1AssocGen4CheckBox.setSelected(false);
            gen1AssocGen6CheckBox.setSelected(false);
        }
        if (!gen2CheckBox.isSelected()) {
            gen2AssocGen1CheckBox.setSelected(false);
            gen2AssocGen3CheckBox.setSelected(false);
            gen2AssocGen4CheckBox.setSelected(false);
        }
        if (!gen3CheckBox.isSelected()) {
            gen3AssocGen2CheckBox.setSelected(false);
            gen3AssocGen4CheckBox.setSelected(false);
        }
        if (!gen4CheckBox.isSelected()) {
            gen4AssocGen1CheckBox.setSelected(false);
            gen4AssocGen2CheckBox.setSelected(false);
            gen4AssocGen3CheckBox.setSelected(false);
        }
        if (!gen6CheckBox.isSelected()) {
            gen6AssocGen1CheckBox.setSelected(false);
        }

        // check and disable implied boxes
        if (gen1CheckBox.isSelected()) {
            gen2AssocGen1CheckBox.setEnabled(false);
            gen2AssocGen1CheckBox.setSelected(true);
            gen4AssocGen1CheckBox.setEnabled(false);
            gen4AssocGen1CheckBox.setSelected(true);
            gen6AssocGen1CheckBox.setEnabled(false);
            gen6AssocGen1CheckBox.setSelected(true);
        }

        if (gen2CheckBox.isSelected()) {
            gen1AssocGen2CheckBox.setEnabled(false);
            gen1AssocGen2CheckBox.setSelected(true);
            gen3AssocGen2CheckBox.setEnabled(false);
            gen3AssocGen2CheckBox.setSelected(true);
            gen4AssocGen2CheckBox.setEnabled(false);
            gen4AssocGen2CheckBox.setSelected(true);
        }

        if (gen3CheckBox.isSelected()) {
            gen2AssocGen3CheckBox.setEnabled(false);
            gen2AssocGen3CheckBox.setSelected(true);
            gen4AssocGen3CheckBox.setEnabled(false);
            gen4AssocGen3CheckBox.setSelected(true);
        }

        if (gen4CheckBox.isSelected()) {
            gen1AssocGen4CheckBox.setEnabled(false);
            gen1AssocGen4CheckBox.setSelected(true);
            gen2AssocGen4CheckBox.setEnabled(false);
            gen2AssocGen4CheckBox.setSelected(true);
            gen3AssocGen4CheckBox.setEnabled(false);
            gen3AssocGen4CheckBox.setSelected(true);
        }

        if (gen6CheckBox.isSelected()) {
            gen1AssocGen6CheckBox.setEnabled(false);
            gen1AssocGen6CheckBox.setSelected(true);
        }

        // To prevent softlocks on the Successor Korrina fight, only turn
        // on the OK button for XY if at least one of Gens 1-4 is selected.
        if (isXY) {
            if (gen1CheckBox.isSelected() || gen2CheckBox.isSelected() || gen3CheckBox.isSelected() || gen4CheckBox.isSelected()) {
                okButton.setEnabled(true);
            } else {
                okButton.setEnabled(false);
            }
        }
    }

    private void okButtonActionPerformed() {
        pressedOk = true;
        setVisible(false);
    }

    private void cancelButtonActionPerformed() {
        pressedOk = false;
        setVisible(false);
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
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, -1, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Include Pokemon from:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label1, gbc);
        gen1CheckBox = new JCheckBox();
        gen1CheckBox.setText("Generation 1");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen1CheckBox, gbc);
        gen2CheckBox = new JCheckBox();
        gen2CheckBox.setText("Generation 2");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen2CheckBox, gbc);
        gen3CheckBox = new JCheckBox();
        gen3CheckBox.setText("Generation 3");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen3CheckBox, gbc);
        gen4CheckBox = new JCheckBox();
        gen4CheckBox.setText("Generation 4");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen4CheckBox, gbc);
        gen5CheckBox = new JCheckBox();
        gen5CheckBox.setText("Generation 5");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen5CheckBox, gbc);
        gen6CheckBox = new JCheckBox();
        gen6CheckBox.setText("Generation 6");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen6CheckBox, gbc);
        gen7CheckBox = new JCheckBox();
        gen7CheckBox.setText("Generation 7");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen7CheckBox, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer1, gbc);
        gen1AssocGen2CheckBox = new JCheckBox();
        gen1AssocGen2CheckBox.setText("Gen 2");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen1AssocGen2CheckBox, gbc);
        gen1AssocGen4CheckBox = new JCheckBox();
        gen1AssocGen4CheckBox.setText("Gen 4");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen1AssocGen4CheckBox, gbc);
        gen1AssocGen6CheckBox = new JCheckBox();
        gen1AssocGen6CheckBox.setText("Gen 6");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen1AssocGen6CheckBox, gbc);
        gen2AssocGen1CheckBox = new JCheckBox();
        gen2AssocGen1CheckBox.setText("Gen 1");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen2AssocGen1CheckBox, gbc);
        gen2AssocGen3CheckBox = new JCheckBox();
        gen2AssocGen3CheckBox.setText("Gen 3");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen2AssocGen3CheckBox, gbc);
        gen2AssocGen4CheckBox = new JCheckBox();
        gen2AssocGen4CheckBox.setText("Gen 4");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen2AssocGen4CheckBox, gbc);
        gen3AssocGen2CheckBox = new JCheckBox();
        gen3AssocGen2CheckBox.setText("Gen 2");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen3AssocGen2CheckBox, gbc);
        gen3AssocGen4CheckBox = new JCheckBox();
        gen3AssocGen4CheckBox.setText("Gen 4");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen3AssocGen4CheckBox, gbc);
        gen4AssocGen1CheckBox = new JCheckBox();
        gen4AssocGen1CheckBox.setText("Gen 1");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen4AssocGen1CheckBox, gbc);
        gen4AssocGen2CheckBox = new JCheckBox();
        gen4AssocGen2CheckBox.setText("Gen 2");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen4AssocGen2CheckBox, gbc);
        gen4AssocGen3CheckBox = new JCheckBox();
        gen4AssocGen3CheckBox.setText("Gen 3");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen4AssocGen3CheckBox, gbc);
        gen6AssocGen1CheckBox = new JCheckBox();
        gen6AssocGen1CheckBox.setText("Gen 1");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(gen6AssocGen1CheckBox, gbc);
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, Font.BOLD, -1, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("...and related Pokemon from:");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label2, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer5, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 13;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer6, gbc);
        xyWarningLabel = new JLabel();
        Font xyWarningLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, xyWarningLabel.getFont());
        if (xyWarningLabelFont != null) xyWarningLabel.setFont(xyWarningLabelFont);
        this.$$$loadLabelText$$$(xyWarningLabel, this.$$$getMessageFromBundle$$$("com/dabomstew/pkrandom/newgui/Bundle", "GenerationLimitDialog.warningXYLabel.text"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 12;
        gbc.gridwidth = 7;
        mainPanel.add(xyWarningLabel, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer7, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.gridwidth = 7;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(panel1, gbc);
        okButton = new JButton();
        okButton.setText("OK");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(okButton, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer8, gbc);
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(cancelButton, gbc);
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
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
