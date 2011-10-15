/*
 * @(#)MagnitudeSystemSettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.gui.*;

/**
 * The <code>MagnitudeSystemSettingDialog</code> represents a dialog 
 * to configure the magnitude system definition to plot stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 January 18
 */

public class MagnitudeSystemSettingDialog extends Dialog {
	/**
	 * The catalog star object.
	 */
	protected CatalogStar catalog_star;

	/**
	 * The radio button for default.
	 */
	protected JRadioButton radio_default;

	/**
	 * The radio button for catalog magnitude.
	 */
	protected JRadioButton radio_catalog;

	/**
	 * The combo box to select a catalog magnitude.
	 */
	protected JComboBox combo_catalog_magnitude;

	/**
	 * The button to show the help on the catalog magnitude.
	 */
	protected JButton button_help_catalog_magnitude;

	/**
	 * The radio button for standard systems.
	 */
	protected JRadioButton radio_standard;

	/**
	 * The combo box to select a standard system.
	 */
	protected JComboBox combo_standard;

	/**
	 * The button to show the help on the standard system.
	 */
	protected JButton button_help_standard;

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
	 * The radio button to input the system formula.
	 */
	protected JRadioButton radio_input_formula;

	/**
	 * The text field to input the system formula.
	 */
	protected JTextField text_input_formula;

	/**
	 * Constructs a <code>MagnitudeSystemSettingDialog</code>.
	 * @param catalog_star the catalog star object.
	 * @exception UnsupportedMagnitudeSystemException if the specified
	 * catalog does not contain any magnitude data.
	 */
	public MagnitudeSystemSettingDialog ( CatalogStar catalog_star )
		throws UnsupportedMagnitudeSystemException
	{
		if (catalog_star.supportsMagnitude() == false)
			throw new UnsupportedMagnitudeSystemException();

		this.catalog_star = catalog_star;

		ButtonGroup bg_regular = new ButtonGroup();
		radio_default = new JRadioButton("", true);
		radio_catalog = new JRadioButton("");
		radio_standard = new JRadioButton("");
		radio_chip = new JRadioButton("");
		radio_input_formula = new JRadioButton("");
		bg_regular.add(radio_default);
		bg_regular.add(radio_catalog);
		bg_regular.add(radio_standard);
		bg_regular.add(radio_chip);
		bg_regular.add(radio_input_formula);

		JPanel panel_default = new JPanel();
		panel_default.add(radio_default);
		panel_default.add(new JLabel("Default"));

		JPanel panel_default2 = new JPanel();
		panel_default2.setLayout(new BorderLayout());
		panel_default2.add(panel_default, BorderLayout.WEST);

		button_help_catalog_magnitude = new JButton("Help");
		button_help_catalog_magnitude.addActionListener(new CatalogMagnitudeHelpListener());

		String[] systems = catalog_star.getAvailableMagnitudeSystems();
		if (systems == null) {
			radio_catalog.setEnabled(false);
			button_help_catalog_magnitude.setEnabled(false);
		} else {
			combo_catalog_magnitude = new JComboBox(systems);
			combo_catalog_magnitude.addActionListener(new CatalogMagnitudeSelectionListener());

			String catalog_name = catalog_star.getCatalogNameWithMagnitudeSystem(systems[0]);
			if (catalog_star.getHelpMessage(catalog_name) != null)
				button_help_catalog_magnitude.setEnabled(true);
			else
				button_help_catalog_magnitude.setEnabled(false);
		}

		JPanel panel_catalog_magnitude = new JPanel();
		panel_catalog_magnitude.add(radio_catalog);
		panel_catalog_magnitude.add(new JLabel("Catalog magnitude"));
		panel_catalog_magnitude.add(combo_catalog_magnitude);
		panel_catalog_magnitude.add(button_help_catalog_magnitude);

		JPanel panel_catalog_magnitude2 = new JPanel();
		panel_catalog_magnitude2.setLayout(new BorderLayout());
		panel_catalog_magnitude2.add(panel_catalog_magnitude, BorderLayout.WEST);

		String[] standard_systems = MagnitudeSystem.getStandardSystemCodes();
		combo_standard = new JComboBox(standard_systems);

		button_help_standard = new JButton("Help");
		button_help_standard.addActionListener(new StandardHelpListener());

		JPanel panel_standard_system = new JPanel();
		panel_standard_system.add(radio_standard);
		panel_standard_system.add(new JLabel("Standard system"));
		panel_standard_system.add(combo_standard);
		panel_standard_system.add(button_help_standard);

		JPanel panel_standard_system2 = new JPanel();
		panel_standard_system2.setLayout(new BorderLayout());
		panel_standard_system2.add(panel_standard_system, BorderLayout.WEST);

		Hashtable hash_formula = MagnitudeSystem.getDefaultMagnitudeSystemFormula();
		Enumeration enum = hash_formula.keys();
		Vector list_chip = new Vector();
		while (enum.hasMoreElements())
			list_chip.addElement(enum.nextElement());
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

		text_input_formula = new JTextField("0.0");
		text_input_formula.setColumns(6);

		JPanel panel_input_formula = new JPanel();
		panel_input_formula.add(radio_input_formula);
		panel_input_formula.add(new JLabel("Instrumental mag = V + "));
		panel_input_formula.add(text_input_formula);
		panel_input_formula.add(new JLabel(" * (B-V)"));

		JPanel panel_input_formula2 = new JPanel();
		panel_input_formula2.setLayout(new BorderLayout());
		panel_input_formula2.add(panel_input_formula, BorderLayout.WEST);

		if (catalog_star.supportsPhotometry()) {
			if (catalog_star.getPhotometryHelpMessage() != null)
				button_help_standard.setEnabled(true);
			else
				button_help_standard.setEnabled(false);
		} else {
			radio_standard.setEnabled(false);
			radio_chip.setEnabled(false);
			radio_input_formula.setEnabled(false);

			button_help_standard.setEnabled(false);
		}

		components = new Object[5];

		components[0] = panel_default2;
		components[1] = panel_catalog_magnitude2;
		components[2] = panel_standard_system2;
		components[3] = panel_chip2;
		components[4] = panel_input_formula2;
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Magnitude System Setting";
	}

	/**
	 * Gets the magnitude system definition.
	 * @return the magnitude system definition.
	 */
	public MagnitudeSystem getMagnitudeSystem ( ) {
		MagnitudeSystem system = new MagnitudeSystem();

		if (radio_default.isSelected()) {
			system.setMethod(MagnitudeSystem.METHOD_DEFAULT);
		} else if (radio_catalog.isSelected()) {
			system.setMethod(MagnitudeSystem.METHOD_CATALOG);
			system.setSystemCode((String)combo_catalog_magnitude.getSelectedItem());
		} else if (radio_standard.isSelected()) {
			system.setMethod(MagnitudeSystem.METHOD_STANDARD);
			system.setSystemCode((String)combo_standard.getSelectedItem());
		} else if (radio_chip.isSelected()) {
			String chip = (String)combo_chip.getSelectedItem();
			Hashtable hash = MagnitudeSystem.getDefaultMagnitudeSystemFormula();
			double gradient_bv = ((Double)hash.get(chip)).doubleValue();

			system.setMethod(MagnitudeSystem.METHOD_INSTRUMENTAL);
			system.setGradientBV(gradient_bv);
		} else {
			system.setMethod(MagnitudeSystem.METHOD_INSTRUMENTAL);
			system.setGradientBV(Format.doubleValueOf(text_input_formula.getText()));
		}

		return system;
	}

	/**
	 * The <code>CatalogMagnitudeHelpListener</code> is a listener 
	 * class of button push to show the help message on the selectged 
	 * catalog magnitude.
	 */
	protected class CatalogMagnitudeHelpListener implements ActionListener {
		/**
		 * Invoked when the button is pushed.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			try {
				String catalog_name = catalog_star.getCatalogNameWithMagnitudeSystem((String)combo_catalog_magnitude.getSelectedItem());

				String message = catalog_star.getHelpMessage(catalog_name);
				if (message != null) {
					JLabel label = new JLabel(message);
					label.setSize(400,300);
					JOptionPane.showMessageDialog(parent_pane, label);
				}
			} catch ( UnsupportedMagnitudeSystemException exception ) {
				button_help_catalog_magnitude.setEnabled(false);
			}
		}
	}

	/**
	 * The <code>CatalogMagnitudeSelectionListener</code> is a 
	 * listener class of item selection in combo box to select the 
	 * catalog magnitude.
	 */
	protected class CatalogMagnitudeSelectionListener implements ActionListener {
		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			try {
				String catalog_name = catalog_star.getCatalogNameWithMagnitudeSystem((String)combo_catalog_magnitude.getSelectedItem());
				if (catalog_star.getHelpMessage(catalog_name) != null)
					button_help_catalog_magnitude.setEnabled(true);
				else
					button_help_catalog_magnitude.setEnabled(false);
			} catch ( UnsupportedMagnitudeSystemException exception ) {
				button_help_catalog_magnitude.setEnabled(false);
			}
		}
	}

	/**
	 * The <code>StandardHelpListener</code> is a listener class of 
	 * button push to show the help message on the standard system.
	 */
	protected class StandardHelpListener implements ActionListener {
		/**
		 * Invoked when the button is pushed.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			String message = catalog_star.getPhotometryHelpMessage();
			if (message != null) {
				JLabel label = new JLabel(message);
				label.setSize(400,300);
				JOptionPane.showMessageDialog(parent_pane, label);
			}
		}
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
			JOptionPane.showMessageDialog(parent_pane, label);
		}
	}
}
