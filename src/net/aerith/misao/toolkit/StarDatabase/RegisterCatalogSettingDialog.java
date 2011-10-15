/*
 * @(#)RegisterCatalogSettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.StarDatabase;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.Dialog;

/**
 * The <code>RegisterCatalogSettingDialog</code> represents a dialog 
 * to set up parameters to register catalog.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class RegisterCatalogSettingDialog extends Dialog {
	/**
	 * The check box which represents to update catalog database.
	 */
	protected JCheckBox checkbox_db;

	/**
	 * The check box which represents to update XML report documents.
	 */
	protected JCheckBox checkbox_xml;

	/**
	 * The check box which represents to update all XML report 
	 * documents.
	 */
	protected JCheckBox checkbox_xml_all;

	/**
	 * Constructs a <code>RegisterCatalogSettingDialog</code>.
	 */
	public RegisterCatalogSettingDialog ( ) {
		components = new Object[1];

		checkbox_db = new JCheckBox("Update cataog database.");
		checkbox_db.setSelected(true);
		JPanel panel_db = new JPanel();
		panel_db.setLayout(new BorderLayout());
		panel_db.add(checkbox_db, BorderLayout.WEST);

		checkbox_xml = new JCheckBox("Update XML files.");
		checkbox_xml.setSelected(true);

		checkbox_xml_all = new JCheckBox("Update all XML files.");
		checkbox_xml_all.setSelected(false);
		checkbox_xml.addActionListener(new UpdateXmlListener());

		JPanel panel_all = new JPanel();
		panel_all.add(new JLabel("    "));
		panel_all.add(checkbox_xml_all);

		JPanel panel_xml = new JPanel();
		panel_xml.setLayout(new BorderLayout());
		panel_xml.add(checkbox_xml, BorderLayout.WEST);
		panel_xml.add(panel_all, BorderLayout.SOUTH);

		JPanel panel_option = new JPanel();
		panel_option.setLayout(new BoxLayout(panel_option, BoxLayout.Y_AXIS));
		panel_option.add(panel_db);
		panel_option.add(panel_xml);

		panel_option.setBorder(new TitledBorder("Options"));
		components[0] = panel_option;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Register Catalog Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("update-db") != null)
			checkbox_db.setSelected(((Boolean)default_values.get("update-db")).booleanValue());
		if (default_values.get("update-xml") != null)
			checkbox_xml.setSelected(((Boolean)default_values.get("update-xml")).booleanValue());
		if (default_values.get("update-xml-all") != null)
			checkbox_xml_all.setSelected(((Boolean)default_values.get("update-xml-all")).booleanValue());

		updateCheckBox();
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("update-db", new Boolean(checkbox_db.isSelected()));
		default_values.put("update-xml", new Boolean(checkbox_xml.isSelected()));
		default_values.put("update-xml-all", new Boolean(checkbox_xml_all.isSelected()));
	}

	/**
	 * Returns true when to update catalog database.
	 * @return true when to update catalog database.
	 */
	public boolean updatesCatalogDatabase ( ) {
		return checkbox_db.isSelected();
	}

	/**
	 * Returns true when to update XML files.
	 * @return true when to update XML files.
	 */
	public boolean updatesXmlFiles ( ) {
		return checkbox_xml.isSelected();
	}

	/**
	 * Returns true when to update all XML files.
	 * @return true when to update all XML files.
	 */
	public boolean updatesAllXmlFiles ( ) {
		return checkbox_xml_all.isSelected();
	}

	/**
	 * Updates the check boxes.
	 */
	private void updateCheckBox ( ) {
		if (updatesXmlFiles()) {
			checkbox_xml_all.setEnabled(true);
		} else {
			checkbox_xml_all.setEnabled(false);
		}
	}

	/**
	 * The <code>UpdateXmlListener</code> is a listener class of 
	 * button push whether to update XML files.
	 */
	protected class UpdateXmlListener implements ActionListener {
		/**
		 * Invoked when the button is pushed.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			updateCheckBox();
		}
	}
}
