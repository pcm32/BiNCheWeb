/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere
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
 **/
package net.sourceforge.metware.binche.gui;

import BiNGO.BingoParameters;
import BiNGO.methods.BingoAlgorithm;
import net.sourceforge.metware.binche.BiNChe;
import net.sourceforge.metware.binche.graph.ChebiGraph;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

/**
 * Settings panel for the binche overrepresentation tool.
 */
public class SettingsPanel extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(SettingsPanel.class);

    private final int DIM_HEIGHT = 450;
    private final int DIM_WIDTH = 350;

    // JComboBox with the possible tests.
    private JComboBox testBox;
    // JComboBox with the possible corrections.
    private JComboBox correctionBox;
    // JComboBox with the possible number of categories in the graph.
    private JComboBox categoriesBox;
    // JTextField for input of the desired significance level.
    private JTextField alphaField;
    // JButton the bingo button
    private JButton bingoButton;
    // ChooseOntologyPanel for choosing the ontology file
    private ChooseOntologyPanel ontologyPanel;
    // ChooseInputPanel for choosing the input file
    private ChooseInputPanel inputPanel;
    // text field for naming test cluster
    private JTextField nameField;
    // text field for evidence codes to be removed
    private JTextField ecField;
    // SettingsSavePanel for option of saving BiNGO-file
    private SaveResultsPanel dataPanel;

    //    public static final String TEST_DATA = "/BiNGO/data/sample.txt";
    public static final String TEST_DATA = "/BiNGO/data/7852_input.tsv";
    public static final String[] testsArray = {BingoAlgorithm.SADDLESUM, BingoAlgorithm.HYPERGEOMETRIC};
    public static final String[] correctionArray = {BingoAlgorithm.NONE, BingoAlgorithm.BONFERRONI};
    public static final String[] categoriesArray = {BingoAlgorithm.CATEGORY_CORRECTION};
    public static final String[] clusterVsArray = {BingoAlgorithm.GENOME};

    private BingoParameters params;
    private Properties bingo_props;

    private InfiniteProgressPanel progressPanel;

    String input_label = "Select input file:";
    String clustername_label = "Project name:";
    String test_label = "Select a statistical test:";
    String correction_label = "Select a multiple testing correction:";
    String sig_label = "Choose a significance level:";
    String category_label = "Select the categories to be visualized:";
    String ontology_label = "Select ontology file:";
    String ec_label = "Discard the following evidence codes:";

    /**
     * This constructor creates the panel with its swing-components.
     */
    public SettingsPanel() {

        super();

        //Create a new Bingo parameter set
        try {
            params = new BingoParameters("");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error opening the properties file." + "\n" +
                    "Please make sure that there is bingo_gui.properties file" + "\n" +
                    "in the BiNGO.jar.");
        }
        bingo_props = params.getBingo_props();

        //create the JComponents.
        makeJComponents();
        setPreferredSize(new Dimension(DIM_WIDTH, DIM_HEIGHT));

        // Layout with GridBagLayout.
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        c.gridx = 1;

        c.weighty = 1;
        c.weightx = 100;

        c.gridwidth = GridBagConstraints.RELATIVE;
        c.gridheight = GridBagConstraints.BOTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 5, 2, 5);

        add(new JLabel(clustername_label), c);
        add(nameField, c);
        add(new JLabel(input_label), c);
        add(inputPanel, c);
        add(new JLabel(test_label), c);
        add(testBox, c);
        add(new JLabel(correction_label), c);
        add(correctionBox, c);
        add(new JLabel(sig_label), c);
        add(alphaField, c);
        add(new JLabel(category_label), c);
        add(categoriesBox, c);
        add(new JLabel(ontology_label), c);
        add(ontologyPanel, c);
        add(new JLabel(ec_label), c);
        add(ecField, c);
        add(dataPanel, c);
        add(bingoButton, c);

        validate();
    }

    /**
     * Paintcomponent method that paints the panel.
     *
     * @param g Graphics-object.
     */
    @Override
	public void paintComponent(Graphics g) {

        super.paintComponent(g);
    }

    /**
     * Method that makes the necessary JComponents for the SettingsPanel.
     * The used JComponents are: a textfield for the cluster name,
     * a TextOrGraphPanel for choosing between text and graph input,
     * two comboboxes for the distributions and
     * the corrections, a textfield for the alpha, two comboboxes for the
     * option against what cluster should be tested and one for the option
     * how many categories must be displayed int the graph, two SettingsOpenPanels
     * for choosing the annotation and ontology file, a SettingsSavePanel
     * for the BiNGO-file and a button to start
     * the calculations.
     */
    public void makeJComponents() {

        // input panel
        inputPanel = new ChooseInputPanel(this, new String[]{TEST_DATA}, bingo_props.getProperty("data_def"));

        // JComboboxes
        testBox = new JComboBox(testsArray);
        testBox.setSelectedItem(bingo_props.getProperty("tests_def"));
        correctionBox = new JComboBox(correctionArray);
        correctionBox.setSelectedItem(bingo_props.getProperty("correction_def"));
        categoriesBox = new JComboBox(categoriesArray);
        categoriesBox.setSelectedItem(bingo_props.getProperty("categories_def"));

        //JTextField.
        alphaField = new JTextField(bingo_props.getProperty("signif_def"));
        nameField = new JTextField();

        // evidence code Field
        ecField = new JTextField();

        // ontologyPanel.
        JLabel ontologyLabel = new JLabel(ontology_label);
        ontologyPanel =
                new ChooseOntologyPanel(this, params.getOntologyLabels(), bingo_props.getProperty("ontology_file_def"));

        // Creating SettingsSavePanels.
        dataPanel = new SaveResultsPanel("Data", this);

        progressPanel = new InfiniteProgressPanel();

        // the BiNGO-button to start the calculations.
        bingoButton = new JButton("Start BiNChe");
        bingoButton.setMnemonic(KeyEvent.VK_B);
        bingoButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {

                setParams();
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
					public void run() {

                        progressPanel.start();
                        Thread bincheExec = new Thread(getRunnable(), "BiNChe");
                        bincheExec.start();
                    }
                });
            }
        });
    }

    private void setParams() {

        params.setTest((String) testBox.getSelectedItem());
        params.setCorrection((String) correctionBox.getSelectedItem());
        params.setOntologyFile(getClass().getResource(ontologyPanel.getSelection()).getFile());
        params.setOntology_default(false);
        params.setNameSpace("chebi_ontology");
        params.setOverOrUnder("Overrepresentation");
        params.setSignificance(new BigDecimal(alphaField.getText()));
        params.setCategory((String) categoriesBox.getSelectedItem());

        params.setReferenceSet(BingoAlgorithm.GENOME);
        params.setAllNodes(null);
        params.setSelectedNodes(null);
    }

    private Runnable getRunnable() {

        Runnable runnable = new Runnable() {

            @Override
			public void run() {

                BiNChe binche = new BiNChe();
                binche.setParameters(params);

                LOGGER.log(Level.INFO, "Reading input file ...");
                try {
                        binche.loadDesiredElementsForEnrichmentFromFile(inputPanel.getFilePath());
                } catch (IOException exception) {
                    LOGGER.log(Level.ERROR,
                            "Error reading file: " + inputPanel.getFilePath() + " " + exception.getMessage());
                    System.exit(1);
                }

                binche.execute();

                ChebiGraph chebiGraph = new ChebiGraph(binche.getPValueMap(), binche.getOntology(), binche.getNodes());

                JFrame frame = new MainFrame();
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.getContentPane().add(chebiGraph.getVisualizationViewer(new Dimension(1024, 768)));
                frame.pack();
                frame.setVisible(true);

                progressPanel.stop();
            }
        };

        return runnable;
    }

    BingoParameters getParams() {

        return params;
    }

    public InfiniteProgressPanel getProgressPanel() {

        return progressPanel;
    }
}

