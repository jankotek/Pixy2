/*
 * @(#)OpenChartCatalogDialog.java
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
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.gui.*;

/**
 * The <code>OpenChartCatalogDialog</code> represents a dialog to 
 * open a catalog file or directory for star chart.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class OpenChartCatalogDialog extends OpenCatalogDialog {
	/**
	 * The panel to input R.A. and Decl.
	 */
	protected CoorPanel coor_panel;

	/**
	 * The text field to input field of view.
	 */
	protected JTextField text_fov;

	/**
	 * The combo box to select an unit of field of view.
	 */
	protected JComboBox combo_unit;

	/**
	 * The text field to input limiting mag.
	 */
	protected JTextField text_limit;

	/**
	 * Constructs an <code>OpenChartCatalogDialog</code>.
	 */
	public OpenChartCatalogDialog ( ) {
		components = new Object[4];

		Vector catalog_list = CatalogManager.getStarCatalogReaderList();
		catalog_panel = new OpenCatalogPanel(catalog_list);
		components[0] = catalog_panel;

		coor_panel = new CoorPanel();
		coor_panel.setBorder(new TitledBorder("Center R.A. and Decl."));
		components[1] = coor_panel;

		JPanel panel = new JPanel();
		text_fov = new JTextField("1");
		text_fov.setColumns(10);
		panel.add(text_fov);
		String[] units = { "degree", "arcmin", "arcsec" };
		combo_unit = new JComboBox(units);
		panel.add(combo_unit);
		panel.setBorder(new TitledBorder("Field of View"));
		components[2] = panel;

		panel = new JPanel();
		text_limit = new JTextField("20");
		text_limit.setColumns(10);
		panel.add(text_limit);
		panel.add(new JLabel("mag"));
		panel.setBorder(new TitledBorder("Limiting Magnitude"));
		components[3] = panel;

		setDefaultValues();
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("fov") != null)
			text_fov.setText((String)default_values.get("fov"));
		if (default_values.get("fov-unit") != null)
			combo_unit.setSelectedItem((String)default_values.get("fov-unit"));
		if (default_values.get("limiting-mag") != null)
			text_limit.setText((String)default_values.get("limiting-mag"));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		catalog_panel.saveDefaultValues();
		coor_panel.saveDefaultValues();

		default_values.put("fov", text_fov.getText());
		default_values.put("fov-unit", combo_unit.getSelectedItem());
		default_values.put("limiting-mag", text_limit.getText());
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
	 * Gets the field of view in degree.
	 * @return the field of view in degree.
	 */
	public double getFieldOfView ( ) {
		double fov = Double.valueOf(text_fov.getText()).doubleValue();
		if (((String)combo_unit.getSelectedItem()).equals("arcmin"))
			fov /= 60.0;
		if (((String)combo_unit.getSelectedItem()).equals("arcsec"))
			fov /= 3600.0;
		return fov;
	}

	/**
	 * Gets the limiting mag.
	 * @return the limiting mag.
	 */
	public double getLimitingMag ( ) {
		return Double.valueOf(text_limit.getText()).doubleValue();
	}
}
