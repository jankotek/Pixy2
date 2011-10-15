/*
 * @(#)MatchingCatalogSettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PixyDesktop;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.Dialog;

/**
 * The <code>MatchingCatalogSettingDialog</code> represents a dialog
 * to set up parameters to operate matching.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 November 24
 */

public class MatchingCatalogSettingDialog extends Dialog {
	/**
	 * The panel to open a catalog.
	 */
	protected OpenCatalogPanel catalog_panel;

	/**
	 * The panel to input R.A. and Decl.
	 */
	protected CoorPanel coor_panel;

	/**
	 * The text field to input horizontal field of view.
	 */
	protected JTextField text_fov_width;

	/**
	 * The text field to input vertical field of view.
	 */
	protected JTextField text_fov_height;

	/**
	 * The combo box to select an unit of field of view.
	 */
	protected JComboBox combo_unit;

	/**
	 * The check box whether to specify the limiting magnitude.
	 */
	protected JCheckBox checkbox_limit;

	/**
	 * The text field to input limiting mag.
	 */
	protected JTextField text_limit;

	/**
	 * Constructs a <code>MatchingCatalogSettingDialog</code>.
	 */
	public MatchingCatalogSettingDialog ( ) {
		components = new Object[4];

		Vector catalog_list = CatalogManager.getStarCatalogReaderList();
		catalog_panel = new OpenCatalogPanel(catalog_list);
		components[0] = catalog_panel;

		coor_panel = new CoorPanel();
		coor_panel.setBorder(new TitledBorder("Center R.A. and Decl."));
		components[1] = coor_panel;

		JPanel panel = new JPanel();
		text_fov_width = new JTextField("1");
		text_fov_width.setColumns(10);
		panel.add(text_fov_width);
		panel.add(new JLabel("x"));
		text_fov_height = new JTextField("1");
		text_fov_height.setColumns(10);
		panel.add(text_fov_height);
		String[] units = { "degree", "arcmin", "arcsec" };
		combo_unit = new JComboBox(units);
		panel.add(combo_unit);
		panel.setBorder(new TitledBorder("Image field of view"));
		components[2] = panel;

		panel = new JPanel();
		checkbox_limit = new JCheckBox("");
		checkbox_limit.setSelected(false);
		checkbox_limit.addActionListener(new CheckLimitingMagnitudeListener());
		text_limit = new JTextField("20");
		text_limit.setColumns(10);
		panel.add(checkbox_limit);
		panel.add(text_limit);
		panel.add(new JLabel("mag"));
		panel.setBorder(new TitledBorder("Limiting Magnitude"));
		components[3] = panel;

		setDefaultValues();

		text_limit.setEnabled(checkbox_limit.isSelected());
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Matching Catalog Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("width") != null)
			text_fov_width.setText((String)default_values.get("width"));
		if (default_values.get("height") != null)
			text_fov_height.setText((String)default_values.get("height"));
		if (default_values.get("unit") != null)
			combo_unit.setSelectedItem((String)default_values.get("unit"));
		if (default_values.get("check-limiting-mag") != null)
			checkbox_limit.setSelected(((Boolean)default_values.get("check-limiting-mag")).booleanValue());
		if (default_values.get("limiting-mag") != null)
			text_limit.setText((String)default_values.get("limiting-mag"));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		catalog_panel.saveDefaultValues();
		coor_panel.saveDefaultValues();

		default_values.put("width", text_fov_width.getText());
		default_values.put("height", text_fov_height.getText());
		default_values.put("unit", combo_unit.getSelectedItem());
		default_values.put("check-limiting-mag", new Boolean(checkbox_limit.isSelected()));
		default_values.put("limiting-mag", text_limit.getText());
	}

	/**
	 * Adds a catalog reader.
	 * @param reader the catalog reader to add to the combo box.
	 */
	public void addCatalogReader ( CatalogReader reader ) {
		catalog_panel.addCatalogReader(reader);
	}

	/**
	 * Gets the selected catalog reader.
	 * @return the selected catalog reader.
	 */
	public CatalogReader getSelectedCatalogReader ( ) {
		return catalog_panel.getSelectedCatalogReader();
	}

	/**
	 * Selects a catalog reader in the combo box.
	 * @param reader the catalog reader to select.
	 */
	public void selectCatalogReader ( CatalogReader reader ) {
		catalog_panel.selectCatalogReader(reader);
	}

	/**
	 * Gets the catalog path.
	 * @return the catalog path.
	 */
	public String getCatalogPath ( ) {
		return catalog_panel.getCatalogPath();
	}

	/**
	 * Sets the catalog path.
	 * @param path the catalog path.
	 */
	public void setCatalogPath ( String path ) {
		catalog_panel.setCatalogPath(path);
	}

	/**
	 * Gets the center R.A. and Decl.
	 * @return the center R.A. and Decl.
	 */
	public Coor getCoor ( ) {
		return coor_panel.getCoor();
	}

	/**
	 * Sets the center R.A. and Decl.
	 * @param coor the center R.A. and Decl.
	 */
	public void setCoor ( Coor coor ) {
		coor_panel.setCoor(coor);
	}

	/**
	 * Gets the horizontal field of view in degree.
	 * @return the horizontal field of view in degree.
	 */
	public double getHorizontalFieldOfView ( ) {
		double fov = Double.valueOf(text_fov_width.getText()).doubleValue();
		if (((String)combo_unit.getSelectedItem()).equals("arcmin"))
			fov /= 60.0;
		if (((String)combo_unit.getSelectedItem()).equals("arcsec"))
			fov /= 3600.0;
		return fov;
	}

	/**
	 * Gets the vertical field of view in degree.
	 * @return the vertical field of view in degree.
	 */
	public double getVerticalFieldOfView ( ) {
		double fov = Double.valueOf(text_fov_height.getText()).doubleValue();
		if (((String)combo_unit.getSelectedItem()).equals("arcmin"))
			fov /= 60.0;
		if (((String)combo_unit.getSelectedItem()).equals("arcsec"))
			fov /= 3600.0;
		return fov;
	}

	/**
	 * Sets the horizontal and vertical field of view in degree.
	 * @param fov_width  the horizontal field of view in degree.
	 * @param fov_height the vertical field of view in degree.
	 */
	public void setFieldOfView ( double fov_width, double fov_height ) {
		text_fov_width.setText(Format.formatDouble(fov_width, 6, 3).trim());
		text_fov_height.setText(Format.formatDouble(fov_height, 6, 3).trim());
		combo_unit.setSelectedIndex(0);
	}

	/**
	 * Returns true when to specify the limiting magnitude.
	 * @return true when to specify the limiting magnitude.
	 */
	public boolean isLimitingMagnitudeSpecified ( ) {
		return checkbox_limit.isSelected();
	}

	/**
	 * Gets the limiting mag.
	 * @return the limiting mag.
	 */
	public double getLimitingMag ( ) {
		return Double.valueOf(text_limit.getText()).doubleValue();
	}

	/**
	 * The <code>CheckLimitingMagnitudeListener</code> is a listener 
	 * class of selection of a check box to input the limiting 
	 * magnitude.
	 */
	protected class CheckLimitingMagnitudeListener implements ActionListener {
		/**
		 * Invoked when one of the check box is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			text_limit.setEnabled(checkbox_limit.isSelected());
		}
	}
}
