/*
 * Copyright (c) 2012, Stephan Beisken. All rights reserved.
 *
 * This file is part of BiNChe.
 *
 * BiNChe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BiNChe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BiNChe. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.metware.binche.gui;

import BiNGO.methods.BingoAlgorithm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;

public class ChooseInputPanel extends JPanel implements ActionListener {

    private final String CUSTOM = "Test";

    /**
     * JComboBox with the possible choices.
     */
    public JComboBox choiceBox;
    /**
     * the selected file.
     */
    private File openFile = null;
    /**
     * parent window
     */
    private SettingsPanel settingsPanel;
    /**
     * default = true, custom = false
     */
    private boolean def = true;
    private String filePath;
    private String[] choiceArray;

    /**
     * Constructor with a string argument that becomes part of the label of
     * the button.
     *
     * @param settingsPanel : parent window
     */
    public ChooseInputPanel(SettingsPanel settingsPanel, String[] choiceArray, String choice_def) {

        super();
        this.settingsPanel = settingsPanel;
        this.choiceArray = choiceArray;

        setOpaque(false);
        makeJComponents();
        setLayout(new GridLayout(1, 0));
        add(choiceBox);

        //defaults
        HashSet<String> choiceSet = new HashSet<String>();
        for (String s : choiceArray) {
            choiceSet.add(s);
        }
        if (choiceSet.contains(choice_def)) {
            choiceBox.setSelectedItem(choice_def);
        } else {
            choiceBox.removeActionListener(this);
            choiceBox.setEditable(true);
            choiceBox.setSelectedItem(choice_def);
            choiceBox.setEditable(false);
            filePath = BingoAlgorithm.CUSTOM;
            def = false;
            choiceBox.addActionListener(this);
        }
    }

    /**
     * Paintcomponent, part where the drawing on the panel takes place.
     */
    @Override
	public void paintComponent(Graphics g) {

        super.paintComponent(g);
    }

    /**
     * Method that creates the JComponents.
     */
    public void makeJComponents() {

        choiceBox = new JComboBox(choiceArray);
        choiceBox.setEditable(false);
        choiceBox.addActionListener(this);
    }

    /**
     * Method that returns the selected item.
     *
     * @return String selection.
     */
    public String getSelection() {

        return choiceBox.getSelectedItem().toString();
    }

    public String getFilePath() {

        return filePath;
    }

    /**
     * Method that returns 1 if one of default choices was chosen, 0 if custom
     */
    public boolean getDefault() {

        return def;
    }

    /**
     * Method performed when button clicked.
     *
     * @param event event that triggers action, here clicking of the button.
     */
    @Override
	public void actionPerformed(ActionEvent event) {

        if (choiceBox.getSelectedItem().equals(CUSTOM)) {
            JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
            int returnVal = chooser.showOpenDialog(settingsPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                filePath = CUSTOM;
                openFile = chooser.getSelectedFile();
                choiceBox.setEditable(true);
                choiceBox.setSelectedItem(openFile.toString());
                choiceBox.setEditable(false);
                def = false;
            }
            if (returnVal == JFileChooser.CANCEL_OPTION) {
                choiceBox.setSelectedItem(CUSTOM);
                filePath = CUSTOM;
                def = true;
            }
        } else {
            filePath = (String) choiceBox.getSelectedItem();
            def = true;
        }
    }
}
