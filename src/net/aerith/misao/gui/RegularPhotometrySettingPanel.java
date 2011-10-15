/*
 * @(#)RegularPhotometrySettingPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.star.*;
import net.aerith.misao.xml.*;

/**
 * The <code>RegularPhotometrySettingPanel</code> represents a panel
 * to select the reference catalog and specify the magnitude system
 * formula for regular photometry.
 * <p>
 * Note that the catalog can be selected from the star classes 
 * identified in the specified XML report document.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 October 7
 */

public class RegularPhotometrySettingPanel extends JPanel {
	/**
	 * The radio button for standard systems.
	 */
	protected JRadioButton radio_standard;

	/**
	 * The combo box to select a standard system.
	 */
	protected JComboBox combo_standard;

	/**
	 * The radio button for unfiltered CCD image.
	 */
	protected JRadioButton radio_chip;

	/**
	 * The combo box to select a CCD chip.
	 */
	protected JComboBox combo_chip;

	/**
	 * The button to show the help on the CCD chips.
	 */
	protected JButton button_help_chip;

	/**
	 * The radio button for other system.
	 */
	protected JRadioButton radio_others;

	/**
	 * The radio button to input the formula.
	 */
	protected JRadioButton radio_input_formula;

	/**
	 * The text field to input the formula.
	 */
	protected JTextField text_input_formula;

	/**
	 * The radio button to calculate the formula.
	 */
	protected JRadioButton radio_calculate_formula;

	/**
	 * The combo box to select a catalog.
	 */
	protected JComboBox combo_catalog;

	/**
	 * The button to show the help on the catalog.
	 */
	protected JButton button_help_catalog;

	/**
	 * The pane of this component.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>RegularPhotometrySettingPanel</code>.
	 */
	public RegularPhotometrySettingPanel ( ) {
		Vector catalog_list = CatalogManager.getPhotometrySupportedCatalogList();
		initialize(catalog_list);
	}

	/**
	 * Constructs a <code>RegularPhotometrySettingPanel</code>.
	 * @param report the XML report document
	 */
	public RegularPhotometrySettingPanel ( XmlReport report ) {
		XmlData data = (XmlData)report.getData();
		Vector catalog_list = data.getPhotometrySupportedCatalogList();
		initialize(catalog_list);
	}

	/**
	 * Initializes.
	 * @param catalog_list the list of catalogs for photometry.
	 */
	protected void initialize ( Vector catalog_list ) {
		pane = this;

		ButtonGroup bg_regular = new ButtonGroup();
		radio_standard = new JRadioButton("", true);
		radio_chip = new JRadioButton("");
		radio_others = new JRadioButton("");
		bg_regular.add(radio_standard);
		bg_regular.add(radio_chip);
		bg_regular.add(radio_others);

		String[] standard_systems = MagnitudeSystem.getStandardSystemCodes();
		combo_standard = new JComboBox(standard_systems);

		JPanel panel_standard_system = new JPanel();
		panel_standard_system.add(radio_standard);
		panel_standard_system.add(new JLabel("Standard system"));
		panel_standard_system.add(combo_standard);

		JPanel panel_standard_system2 = new JPanel();
		panel_standard_system2.setLayout(new BorderLayout());
		panel_standard_system2.add(panel_standard_system, BorderLayout.WEST);

		Hashtable hash_formula = MagnitudeSystem.getDefaultMagnitudeSystemFormula();
		Enumeration enum2 = hash_formula.keys();
		Vector list_chip = new Vector();
		while (enum2.hasMoreElements())
			list_chip.addElement(enum2.nextElement());
		combo_chip = new JComboBox(list_chip);

		button_help_chip = new JButton("Help");
		button_help_chip.addActionListener(new ChipHelpListener());

		JPanel panel_chip = new JPanel();
		panel_chip.add(radio_chip);
		panel_chip.add(new JLabel("Unfiltered CCD image"));
		panel_chip.add(combo_chip);
		panel_chip.add(button_help_chip);

		JPanel panel_chip2 = new JPanel();
		panel_chip2.setLayout(new BorderLayout());
		panel_chip2.add(panel_chip, BorderLayout.WEST);

		JPanel panel_others = new JPanel();
		panel_others.add(radio_others);
		panel_others.add(new JLabel("Other instruments"));

		ButtonGroup bg_others = new ButtonGroup();
		radio_input_formula = new JRadioButton("", true);
		radio_calculate_formula = new JRadioButton("");
		bg_others.add(radio_input_formula);
		bg_others.add(radio_calculate_formula);

		text_input_formula = new JTextField("0.0");
		text_input_formula.setColumns(6);

		JPanel panel_input_formula = new JPanel();
		panel_input_formula.add(radio_input_formula);
		panel_input_formula.add(new JLabel("Mag = V + "));
		panel_input_formula.add(text_input_formula);
		panel_input_formula.add(new JLabel(" * (B-V)"));

		JPanel panel_calculate_formula = new JPanel();
		panel_calculate_formula.add(radio_calculate_formula);
		panel_calculate_formula.add(new JLabel("Calculate system formula"));

		JPanel panel_input_formula2 = new JPanel();
		panel_input_formula2.setLayout(new BorderLayout());
		panel_input_formula2.add(panel_input_formula, BorderLayout.WEST);

		JPanel panel_calculate_formula2 = new JPanel();
		panel_calculate_formula2.setLayout(new BorderLayout());
		panel_calculate_formula2.add(panel_calculate_formula, BorderLayout.WEST);

		JPanel panel_formula = new JPanel();
		panel_formula.setLayout(new BoxLayout(panel_formula, BoxLayout.Y_AXIS));
		panel_formula.add(panel_input_formula2);
		panel_formula.add(panel_calculate_formula2);

		JPanel panel_formula2 = new JPanel();
		panel_formula2.add(new JLabel("        "));
		panel_formula2.add(panel_formula);

		JPanel panel_others2 = new JPanel();
		panel_others2.setLayout(new BorderLayout());
		panel_others2.add(panel_others, BorderLayout.WEST);
		panel_others2.add(panel_formula2, BorderLayout.SOUTH);

		JPanel panel_instruments = new JPanel();
		panel_instruments.setLayout(new BoxLayout(panel_instruments, BoxLayout.Y_AXIS));
		panel_instruments.setBorder(new TitledBorder("Instruments"));
		panel_instruments.add(panel_standard_system2);
		panel_instruments.add(panel_chip2);
		panel_instruments.add(panel_others2);

		combo_catalog = new JComboBox(catalog_list);
		combo_catalog.addActionListener(new CatalogSelectionListener());

		button_help_catalog = new JButton("Help");
		button_help_catalog.addActionListener(new CatalogHelpListener());
		
		String catalog_name = (String)combo_catalog.getSelectedItem();
		CatalogStar star = createStar(catalog_name);
		if (star != null) {
			String message = star.getPhotometryHelpMessage();
			if (message != null  &&  message.length() > 0) {
				button_help_catalog.setEnabled(true);
			} else {
				button_help_catalog.setEnabled(false);
			}
		} else {
			button_help_catalog.setEnabled(false);
		}

		JPanel panel_catalog = new JPanel();
		panel_catalog.setBorder(new TitledBorder("Catalog"));
		panel_catalog.add(combo_catalog);
		panel_catalog.add(button_help_catalog);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(panel_instruments);
		add(panel_catalog);
	}

	/**
	 * Gets a star object of the specified catalog name.
	 * @param catalog_name the catalog name.
	 * @return a star object.
	 */
	protected CatalogStar createStar ( String catalog_name ) {
		try {
			String class_name = CatalogManager.getCatalogStarClassName(catalog_name);
			if (class_name == null  ||  class_name.length() == 0)
				return null;

			Class t = Class.forName(class_name);
			CatalogStar star = (CatalogStar)t.newInstance();

			return star;
		} catch ( ClassNotFoundException exception ) {
			System.err.println(exception);
		} catch ( IllegalAccessException exception ) {
			System.err.println(exception);
		} catch ( InstantiationException exception ) {
			System.err.println(exception);
		}

		return null;
	}

	/**
	 * Gets the setting.
	 * @return the setting.
	 */
	public PhotometrySetting getPhotometrySetting ( ) {
		PhotometrySetting setting = new PhotometrySetting((String)combo_catalog.getSelectedItem());

		if (radio_standard.isSelected()) {
			setting.setMethod(PhotometrySetting.METHOD_STANDARD);
			setting.setSystemCode((String)combo_standard.getSelectedItem());
		} else if (radio_chip.isSelected()) {
			String chip = (String)combo_chip.getSelectedItem();
			Hashtable hash = MagnitudeSystem.getDefaultMagnitudeSystemFormula();
			double gradient_bv = ((Double)hash.get(chip)).doubleValue();

			setting.setMethod(PhotometrySetting.METHOD_INSTRUMENTAL_PHOTOMETRY);
			setting.setGradientBV(gradient_bv);
		} else {
			if (radio_input_formula.isSelected()) {
				setting.setMethod(PhotometrySetting.METHOD_INSTRUMENTAL_PHOTOMETRY);
				setting.setGradientBV(Format.doubleValueOf(text_input_formula.getText()));
			} else {
				setting.setMethod(PhotometrySetting.METHOD_FREE_PHOTOMETRY);
			}
		}

		return setting;
	}

	/**
	 * The <code>ChipHelpListener</code> is a listener class of button 
	 * push to show the help message on the CCD chips.
	 */
	protected class ChipHelpListener implements ActionListener {
		/**
		 * Invoked when the button is pushed.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			String message = MagnitudeSystem.getDefaultMagnitudeSystemFormulaHelpMessage();

			JLabel label = new JLabel(message);
			label.setSize(400,300);
			JOptionPane.showMessageDialog(pane, label);
		}
	}

	/**
	 * The <code>CatalogHelpListener</code> is a listener class of
	 * button push to show the help message on the selectged catalog.
	 */
	protected class CatalogHelpListener implements ActionListener {
		/**
		 * Invoked when the button is pushed.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			String catalog_name = (String)combo_catalog.getSelectedItem();
			CatalogStar star = createStar(catalog_name);
			if (star != null) {
				String message = star.getPhotometryHelpMessage();

				JLabel label = new JLabel(message);
				label.setSize(400,300);
				JOptionPane.showMessageDialog(pane, label);
			}
		}
	}

	/**
	 * The <code>CatalogSelectionListener</code> is a listener class
	 * of item selection in combo box to select the catalog.
	 */
	protected class CatalogSelectionListener implements ActionListener {
		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			String catalog_name = (String)combo_catalog.getSelectedItem();
			CatalogStar star = createStar(catalog_name);
			if (star != null) {
				String message = star.getPhotometryHelpMessage();
				if (message != null  &&  message.length() > 0) {
					button_help_catalog.setEnabled(true);
				} else {
					button_help_catalog.setEnabled(false);
				}
			} else {
				button_help_catalog.setEnabled(false);
			}
		}
	}
}
