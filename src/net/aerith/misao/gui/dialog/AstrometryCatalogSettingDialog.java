/*
 * @(#)AstrometryCatalogSettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
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
import net.aerith.misao.gui.*;

/**
 * The <code>AstrometryCatalogSettingDialog</code> represents a dialog
 * to select the method of astrometry and the reference catalog.
 * <p>
 * Note that the catalog for astrometry can be selected from the star
 * classes identified in the specified XML report document.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 April 13
 */

public class AstrometryCatalogSettingDialog extends Dialog {
	/**
	 * The combo box to select a catalog.
	 */
	protected JComboBox combo_catalog;

	/**
	 * The text field to input the catalog description.
	 */
	protected JTextField text_description;

	/**
	 * The check box which represents to calculate the distortion 
	 * field.
	 */
	protected JCheckBox checkbox_distortion;

	/**
	 * Constructs an <code>AstrometryCatalogSettingDialog</code>.
	 */
	public AstrometryCatalogSettingDialog ( ) {
		Vector catalog_list = CatalogManager.getAstrometrySupportedCatalogList();
		initialize(catalog_list);
	}

	/**
	 * Constructs an <code>AstrometryCatalogSettingDialog</code>.
	 * @param report the XML report document
	 */
	public AstrometryCatalogSettingDialog ( XmlReport report ) {
		XmlData data = (XmlData)report.getData();
		Vector catalog_list = data.getAstrometrySupportedCatalogList();
		initialize(catalog_list);
	}

	/**
	 * Initializes.
	 * @param catalog_list the list of catalogs for astrometry.
	 */
	protected void initialize ( Vector catalog_list ) {
		components = new Object[4];

		combo_catalog = new JComboBox(catalog_list);
		combo_catalog.addActionListener(new CatalogSelectionListener());

		text_description = new JTextField("");
		text_description.setColumns(30);
		
		String catalog_name = (String)combo_catalog.getSelectedItem();
		CatalogStar star = createStar(catalog_name);
		if (star != null) {
			if (star.isDescriptionEdittable()) {
				text_description.setEnabled(true);
				text_description.setText(catalog_name);
			} else {
				text_description.setEnabled(false);
				text_description.setText("");
			}
		} else {
			text_description.setEnabled(false);
		}

		JPanel panel_catalog = new JPanel();
		panel_catalog.setLayout(new BoxLayout(panel_catalog, BoxLayout.Y_AXIS));
		panel_catalog.setBorder(new TitledBorder("Catalog"));
		panel_catalog.add(combo_catalog);
		panel_catalog.add(text_description);

		JPanel panel_equinox = new JPanel();
		panel_equinox.setBorder(new TitledBorder("Equinox"));
		panel_equinox.add(new JLabel("J2000.0 only."));

		JPanel panel_epoch = new JPanel();
		panel_epoch.setBorder(new TitledBorder("Epoch"));
		panel_epoch.add(new JLabel("Not supported."));

		JPanel panel_option = new JPanel();
		checkbox_distortion = new JCheckBox("Calculate distortion field.");
		panel_option.add(checkbox_distortion);
		checkbox_distortion.setSelected(true);

		components[0] = panel_catalog;
		components[1] = panel_equinox;
		components[2] = panel_epoch;
		components[3] = panel_option;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Astrometry Catalog Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("distortion") != null)
			setCalculateDistortionField(((Boolean)default_values.get("distortion")).booleanValue());
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("distortion", new Boolean(calculatesDistortionField()));
	}

	/**
	 * Gets the setting.
	 * @return the setting.
	 */
	public AstrometrySetting getAstrometrySetting ( ) {
		try {
			String catalog_name = (String)combo_catalog.getSelectedItem();
			String class_name = CatalogManager.getCatalogStarClassName(catalog_name);
			if (class_name == null  ||  class_name.length() == 0)
				return null;

			Class t = Class.forName(class_name);
			CatalogStar star = (CatalogStar)t.newInstance();

			AstrometrySetting setting = new AstrometrySetting((String)combo_catalog.getSelectedItem());

			if (star.isDescriptionEdittable())
				setting.setDescription(text_description.getText());

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
	 * Returns true to calculate the distortion field.
	 * @return true to calculate the distortion field.
	 */
	public boolean calculatesDistortionField ( ) {
		return checkbox_distortion.isSelected();
	}

	/**
	 * Sets the flag to calculate the distortion field.
	 * @param flag true when to calculate the distortion field.
	 */
	public void setCalculateDistortionField ( boolean flag ) {
		checkbox_distortion.setSelected(flag);
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
				if (star.isDescriptionEdittable()) {
					text_description.setEnabled(true);
					text_description.setText(catalog_name);
				} else {
					text_description.setEnabled(false);
					text_description.setText("");
				}
			} else {
				text_description.setEnabled(false);
			}
		}
	}
}
