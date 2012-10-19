/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * Modified by Stephan Beisken, European Bioinformatics Institute
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: Class that extends JPanel and impelements ItemListener and ActionListener and 
 * * which takes care of making a save panel with checkbox, button for choosing the
 * * location and name for the file to be saved and a textfield with the result of
 * * the selection, file name .     
 **/
package net.sourceforge.metware.binche.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * ***************************************************************
 * SettingsSavePanel.java:       Steven Maere & Karel Heymans (c) 	March 2005
 * -----------------------
 * <p/>
 * Class that extends JPanel and impelements ItemListener and ActionListener and
 * which takes care of making a save panel with checkbox, button for choosing the
 * location and name for the file to be saved and a textfield with the result of
 * the selection.
 * ******************************************************************
 */
public class SaveResultsPanel extends JPanel implements ActionListener {

    /**
     * JCheckBox for making choice of saving or not.
     */
    private JCheckBox checkBox;
    /**
     * the textfield for the save directory name
     */
    private JLabel fileTextField;
    /**
     * the place where the file is to be saved.
     */
    private File saveFile;
    /**
     * constant string for the loadcorrect of the filechooser.
     */
    private final String LOADCORRECT = "LOADCORRECT";
    /**
     * parent component
     */
    private Component settingsPanel;

    /**
     * Constructor with a string argument that becomes part of the label
     *
     * @param sort string that denotes part of the name of the button.
     */
    public SaveResultsPanel(String sort, Component settingsPanel) {

        super();
        this.settingsPanel = settingsPanel;
        setOpaque(false);
        makeJComponents(sort);

        // Layout with GridLayout.
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 100;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        add(checkBox, c);
        add(fileTextField, c);
    }

    /**
     * Paintcomponent, draws panel
     */
    @Override
	public void paintComponent(Graphics g) {

        super.paintComponent(g);
    }

    /**
     * Method that creates the JComponents.
     *
     * @param sort string that denotes part of the name of the button.
     */
    public void makeJComponents(String sort) {

        // JCheckBox.
        checkBox = new JCheckBox("Save " + sort);
        checkBox.setSelected(false);
        checkBox.addActionListener(this);

        // textfield.
        fileTextField = new JLabel();
        fileTextField.setOpaque(true);
    }

    /**
     * Getter for the file dir.
     *
     * @return String file dir.
     */
    public String getFileDir() {

        return fileTextField.getText();
    }

    /**
     * Reset checkBox and JTextField()
     */
    public void reset() {

        checkBox.setSelected(false);
        fileTextField.setText(null);
    }

    /**
     * Boolean method for checking whether box is checked or not.
     *
     * @return boolean checked or not checked.
     */
    public boolean checked() {

        return checkBox.isSelected();
    }

    /**
     * Method for checking whether the selected file is legal are not.
     *
     * @return String with error or LOADCORRECT.
     */
    public String isFileNameLegal(String clusterName) {

        String resultString = LOADCORRECT;

        if (checkBox.isSelected()) {
            try {
                BufferedWriter output = new BufferedWriter(new FileWriter(new File(saveFile.toString(), clusterName)));
            } catch (Exception e) {
                resultString = "FILE NAMING ERROR:  " + e;
            }
        }
        return resultString;
    }

    /**
     * Method performed when button clicked.
     *
     * @param e event that triggers action, here clicking of the button.
     */

    @Override
	public void actionPerformed(ActionEvent e) {

        JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
        chooser.setDialogTitle("Select output directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showSaveDialog(settingsPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            saveFile = chooser.getSelectedFile();
            fileTextField.setText(saveFile.toString());
        }
    }
}
