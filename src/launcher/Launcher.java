package launcher;

/*----------------------------------------------------------------------------*/
/*--  Launcher.java - utility for launching the randomizer with certain     --*/
/*--                  settings.                                             --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
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

import com.dabomstew.pkrandom.SysConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URI;

public class Launcher {

    private static JFrame frame;
    private static boolean logEnabled = false;

    public static void main(String[] args) {
        try {
            ProcessBuilder pb = new ProcessBuilder("java", "-Xmx4096M", "-jar", "./PokeRandoStar.jar", "please-use-the-launcher");
            File log = new File(SysConstants.ROOT_PATH + "launcher-log.txt");
            if (!log.exists()) {
                log.createNewFile();
            }
            if (log.canWrite()) {
                pb.redirectErrorStream(true);
                pb.redirectOutput(ProcessBuilder.Redirect.to(log));
                logEnabled = true;
            } else {
                SwingUtilities.invokeLater(() -> {
                    frame = new JFrame("Launcher");
                    try {
                        String lafName = javax.swing.UIManager.getSystemLookAndFeelClassName();
                        // Only set Native LaF on windows.
                        if (lafName.equalsIgnoreCase("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
                            javax.swing.UIManager.setLookAndFeel(lafName);
                        }
                    } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                    String message = "The launcher is not capable of writing to launcher-log.txt. It will not be able to log or alert any errors";
                    String subMessage = "It will still attempt to launch the randomizer, but please check to see if an antivirus program is preventing launcher.jar from writing files.";
                    Object[] messages = {message, subMessage};
                    JOptionPane.showMessageDialog(frame, messages);
                });
            }

            Process p = pb.start();
            p.waitFor();
            if (p.exitValue() != 0 && logEnabled) {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(log)));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                String logContentStr = sb.toString();

                SwingUtilities.invokeLater(() -> {
                    frame = new JFrame("Launcher");
                    try {
                        String lafName = javax.swing.UIManager.getSystemLookAndFeelClassName();
                        // Only set Native LaF on windows.
                        if (lafName.equalsIgnoreCase("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
                            javax.swing.UIManager.setLookAndFeel(lafName);
                        }
                    } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                    String message = "The launcher encountered an error. Error message can be found in launcher-log.txt.";
                    if (logContentStr.contains("Invalid maximum heap size") && logContentStr.contains("exceeds the maximum representable size")) {
                        String extraMessage = "Most likely, the launcher failed because you have an incompatible version of Java.";
                        JLabel label = new JLabel("<html><a href=\"https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/About-Java\">For more information about Java requirements, click here.</a>");
                        label.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                Desktop desktop = java.awt.Desktop.getDesktop();
                                try {
                                    desktop.browse(new URI("https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/About-Java"));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                        label.setCursor(new java.awt.Cursor(Cursor.HAND_CURSOR));
                        Object[] messages = {message,extraMessage,label};
                        JOptionPane.showMessageDialog(frame, messages);
                    } else {
                        Object[] messages = {message};
                        JOptionPane.showMessageDialog(frame, messages);
                    }
                    System.exit(1);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
