/*
 * @(#)CrossIdentificationSettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.CrossIdentification;
import java.io.File;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;

/**
 * The <code>CrossIdentificationSettingDialog</code> represents a 
 * dialog to configure the cross identification setting.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 December 31
 */

public class CrossIdentificationSettingDialog extends net.aerith.misao.gui.dialog.Dialog {
	/**
	 * The panel to open the base catalog.
	 */
	protected OpenCatalogPanel base_catalog_panel;

	/**
	 * The panel to open the reference catalog.
	 */
	protected OpenCatalogPanel reference_catalog_panel;

	/**
	 * The radio button to identify with database.
	 */
	protected JRadioButton radio_database;

	/**
	 * The radio button to identify with another catalog.
	 */
	protected JRadioButton radio_catalog;

	/**
	 * The text field to input the output file path.
	 */
	protected JTextField text_path_output;

	/**
	 * The button to browse.
	 */
	protected JButton button_browse_output;

	/**
	 * The panel to select the output file.
	 */
	protected JPanel panel_output;

	/**
	 * Constructs a <code>CrossIdentificationSettingDialog</code>.
	 */
	public CrossIdentificationSettingDialog ( ) {
		components = new Object[3];

		Vector base_catalog_list = CatalogManager.getIdentificationCatalogReaderList();
		base_catalog_panel = new OpenCatalogPanel(base_catalog_list, 1);
		JPanel panel_base = new JPanel();
		panel_base.add(base_catalog_panel);
		panel_base.setBorder(new TitledBorder("Base Catalog"));
		components[0] = panel_base;

		ButtonGroup bg = new ButtonGroup();
		radio_database = new JRadioButton("Database.", true);
		radio_catalog = new JRadioButton("Selected catalog.");
		bg.add(radio_database);
		bg.add(radio_catalog);

		Vector reference_catalog_list = CatalogManager.getIdentificationCatalogReaderList();
		reference_catalog_panel = new OpenCatalogPanel(reference_catalog_list, 2);
		JPanel panel_reference = new JPanel();
		panel_reference.setLayout(new BoxLayout(panel_reference, BoxLayout.Y_AXIS));
		panel_reference.add(radio_database);
		panel_reference.add(radio_catalog);
		panel_reference.add(reference_catalog_panel);
		panel_reference.setBorder(new TitledBorder("Identify With"));
		components[1] = panel_reference;

		panel_output = new JPanel();
		text_path_output = new JTextField();
		text_path_output.setColumns(30);
		panel_output.add(text_path_output);
		button_browse_output = new JButton("Browse");
		button_browse_output.addActionListener(new BrowseListener());
		panel_output.add(button_browse_output);
		panel_output.setBorder(new TitledBorder("Output"));
		components[2] = panel_output;

		TargetListener target_listener = new TargetListener();
		radio_database.addActionListener(target_listener);
		radio_catalog.addActionListener(target_listener);

		setDefaultValues();

		reference_catalog_panel.setEnabled(radio_catalog.isSelected());
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Cross Identification Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("identify-with") != null) {
			if (((Boolean)default_values.get("identify-with")).booleanValue()) {
				radio_database.setSelected(true);
				radio_catalog.setSelected(false);
			} else {
				radio_database.setSelected(false);
				radio_catalog.setSelected(true);
			}
		}
		if (default_values.get("output") != null)
			text_path_output.setText((String)default_values.get("output"));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		base_catalog_panel.saveDefaultValues();
		reference_catalog_panel.saveDefaultValues();

		default_values.put("identify-with", new Boolean(identifiesWithDatabase()));
		default_values.put("output", text_path_output.getText());
	}

	/**
	 * Gets the base catalog reader.
	 * @return the base catalog reader.
	 */
	public CatalogReader getBaseCatalogReader ( ) {
		return base_catalog_panel.getSelectedCatalogReader();
	}

	/**
	 * Gets the base catalog path.
	 * @return the base catalog path.
	 */
	public String getBaseCatalogPath ( ) {
		return base_catalog_panel.getCatalogPath();
	}

	/**
	 * Returns true when to identify with database.
	 * @return true when to identify with database.
	 */
	public boolean identifiesWithDatabase ( ) {
		return radio_database.isSelected();
	}

	/**
	 * Gets the reference catalog reader.
	 * @return the reference catalog reader.
	 */
	public CatalogReader getReferenceCatalogReader ( ) {
		return reference_catalog_panel.getSelectedCatalogReader();
	}

	/**
	 * Gets the reference catalog path.
	 * @return the reference catalog path.
	 */
	public String getReferenceCatalogPath ( ) {
		return reference_catalog_panel.getCatalogPath();
	}

	/**
	 * Gets the output file path.
	 * @return the output file path.
	 */
	public String getOutputPath ( ) {
		return text_path_output.getText();
	}

	/**
	 * The <code>TargetListener</code> is a listener class of 
	 * selection of a radio button to select whether to identify with
	 * database or another catalog.
	 */
	protected class TargetListener implements ActionListener {
		/**
		 * Invoked when one of the check box is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			reference_catalog_panel.setEnabled(radio_catalog.isSelected());
		}
	}

	/**
	 * The <code>BrowseListener</code> is a listener class of button
	 * push to open a file chooser dialog to search an output file.
	 */
	protected class BrowseListener implements ActionListener {
		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			CommonFileChooser file_chooser = new CommonFileChooser();
			file_chooser.setMultiSelectionEnabled(false);

			if (file_chooser.showOpenDialog(panel_output) == JFileChooser.APPROVE_OPTION) {
				File file = file_chooser.getSelectedFile();
				text_path_output.setText(file.getPath());
			}
		}
	}
}
