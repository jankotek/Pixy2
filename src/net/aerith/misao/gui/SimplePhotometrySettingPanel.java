/*
 * @(#)SimplePhotometrySettingPanel.java
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
 * The <code>SimplePhotometrySettingPanel</code> represents a panel
 * to select the reference catalog and the method for simple
 * photometry.
 * <p>
 * Note that the catalog can be selected from the star classes 
 * identified in the specified XML report document.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class SimplePhotometrySettingPanel extends JPanel {
	/**
	 * The radio button for the average fitting.
	 */
	protected JRadioButton radio_average;

	/**
	 * The radio button for the line fitting.
	 */
	protected JRadioButton radio_line;

	/**
	 * The combo box to select a catalog.
	 */
	protected JComboBox combo_catalog;

	/**
	 * The button to show the help on the catalog.
	 */
	protected JButton button_help_catalog;

	/**
	 * The text field to input the catalog description.
	 */
	protected JTextField text_description;

	/**
	 * The pane of this component.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>SimplePhotometrySettingPanel</code>.
	 */
	public SimplePhotometrySettingPanel ( ) {
		Vector catalog_list = CatalogManager.getMagnitudeSupportedCatalogList();
		initialize(catalog_list);
	}

	/**
	 * Constructs a <code>SimplePhotometrySettingPanel</code>.
	 * @param report the XML report document
	 */
	public SimplePhotometrySettingPanel ( XmlReport report ) {
		XmlData data = (XmlData)report.getData();
		Vector catalog_list = data.getMagnitudeSupportedCatalogList();
		initialize(catalog_list);
	}

	/**
	 * Initializes.
	 * @param catalog_list the list of catalogs supporting magnitude.
	 */
	protected void initialize ( Vector catalog_list ) {
		pane = this;

		ButtonGroup bg_detector = new ButtonGroup();
		radio_average = new JRadioButton("", true);
		radio_line = new JRadioButton("");
		bg_detector.add(radio_average);
		bg_detector.add(radio_line);

		JPanel panel_average = new JPanel();
		panel_average.add(radio_average);
		panel_average.add(new JLabel("Average fitting"));

		JPanel panel_average2 = new JPanel();
		panel_average2.setLayout(new BorderLayout());
		panel_average2.add(panel_average, BorderLayout.WEST);

		JPanel panel_line = new JPanel();
		panel_line.add(radio_line);
		panel_line.add(new JLabel("Line fitting"));

		JPanel panel_line2 = new JPanel();
		panel_line2.setLayout(new BorderLayout());
		panel_line2.add(panel_line, BorderLayout.WEST);

		JPanel panel_fitting = new JPanel();
		panel_fitting.setLayout(new BoxLayout(panel_fitting, BoxLayout.Y_AXIS));
		panel_fitting.setBorder(new TitledBorder("Fitting method"));
		panel_fitting.add(panel_average2);
		panel_fitting.add(new JLabel("Mag = - log_2.5 (Value) + base_magnitude"));
		panel_fitting.add(panel_line2);
		panel_fitting.add(new JLabel("Mag = gradient * log_2.5 (Value) + base_magnitude"));

		combo_catalog = new JComboBox(catalog_list);
		combo_catalog.addActionListener(new CatalogSelectionListener());

		button_help_catalog = new JButton("Help");
		button_help_catalog.addActionListener(new CatalogHelpListener());

		text_description = new JTextField("");
		text_description.setColumns(30);
		
		String catalog_name = (String)combo_catalog.getSelectedItem();
		CatalogStar star = createStar(catalog_name);
		if (star != null) {
			String message = star.getHelpMessage(catalog_name);
			if (message != null  &&  message.length() > 0) {
				button_help_catalog.setEnabled(true);
			} else {
				button_help_catalog.setEnabled(false);
			}

			if (star.isDescriptionEdittable()) {
				text_description.setEnabled(true);
				text_description.setText(catalog_name);
			} else {
				text_description.setEnabled(false);
				text_description.setText("");
			}
		} else {
			button_help_catalog.setEnabled(false);
			text_description.setEnabled(false);
		}

		JPanel panel = new JPanel();
		panel.add(combo_catalog);
		panel.add(button_help_catalog);

		JPanel panel_catalog = new JPanel();
		panel_catalog.setLayout(new BoxLayout(panel_catalog, BoxLayout.Y_AXIS));
		panel_catalog.add(panel);
		panel_catalog.add(text_description);
		panel_catalog.setBorder(new TitledBorder("Catalog"));

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(panel_fitting);
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
		try {
			String catalog_name = (String)combo_catalog.getSelectedItem();
			String class_name = CatalogManager.getCatalogStarClassName(catalog_name);
			if (class_name == null  ||  class_name.length() == 0)
				return null;

			Class t = Class.forName(class_name);
			CatalogStar star = (CatalogStar)t.newInstance();
			String system = star.getMagnitudeSystem(catalog_name);

			PhotometrySetting setting = new PhotometrySetting(catalog_name);

			if (star.isDescriptionEdittable())
				setting.setDescription(text_description.getText());

			setting.setMethod(PhotometrySetting.METHOD_COMPARISON);
			setting.setSystemCode(system);

			if (radio_average.isSelected()) {
				setting.setFixGradient(true);
			} else {
				setting.setFixGradient(false);
			}

			return setting;
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
				String message = star.getHelpMessage(catalog_name);

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
				String message = star.getHelpMessage(catalog_name);
				if (message != null  &&  message.length() > 0) {
					button_help_catalog.setEnabled(true);
				} else {
					button_help_catalog.setEnabled(false);
				}

				if (star.isDescriptionEdittable()) {
					text_description.setEnabled(true);
					text_description.setText(catalog_name);
				} else {
					text_description.setEnabled(false);
					text_description.setText("");
				}
			} else {
				button_help_catalog.setEnabled(false);
				text_description.setEnabled(false);
			}
		}
	}
}
