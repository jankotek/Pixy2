/*
 * @(#)SearchStarSettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageDatabase;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.Dialog;

/**
 * The <code>SearchStarSettingDialog</code> represents a dialog to set
 * up the R.A. and Decl., and the radius to search a star.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class SearchStarSettingDialog extends Dialog {
	/**
	 * The panel to input R.A. and Decl.
	 */
	protected CoorPanel coor_panel;

	/**
	 * The text field to input the radius.
	 */
	protected JTextField text_radius;

	/**
	 * The combo box to select an unit of radius.
	 */
	protected JComboBox combo_unit;

	/**
	 * The panel to specify the range of the limiting magnitude.
	 */
	protected LimitingMagPanel limiting_mag_panel;

	/**
	 * Constructs a <code>SearchStarSettingDialog</code>.
	 */
	public SearchStarSettingDialog ( ) {
		components = new Object[3];

		coor_panel = new CoorPanel();
		coor_panel.setBorder(new TitledBorder("R.A. and Decl."));
		components[0] = coor_panel;

		JPanel panel = new JPanel();
		text_radius = new JTextField("1.0");
		text_radius.setColumns(10);
		panel.add(text_radius);
		String[] units = { "degree", "arcmin", "arcsec" };
		combo_unit = new JComboBox(units);
		combo_unit.setSelectedIndex(2);
		panel.add(combo_unit);
		panel.setBorder(new TitledBorder("Identification Radius"));
		components[1] = panel;

		limiting_mag_panel = new LimitingMagPanel();
		limiting_mag_panel.setBorder(new TitledBorder("Limiting Mag"));
		components[2] = limiting_mag_panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Search Star Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("radius") != null)
			text_radius.setText((String)default_values.get("radius"));
		if (default_values.get("radius-unit") != null)
			combo_unit.setSelectedItem((String)default_values.get("radius-unit"));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		coor_panel.saveDefaultValues();
		limiting_mag_panel.saveDefaultValues();

		default_values.put("radius", text_radius.getText());
		default_values.put("radius-unit", combo_unit.getSelectedItem());
	}

	/**
	 * Gets the R.A. and Decl.
	 * @return the R.A. and Decl.
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
	 * Gets the radius in degree.
	 * @return the radius in degree.
	 */
	public double getRadius ( ) {
		double radius = Double.valueOf(text_radius.getText()).doubleValue();
		if (((String)combo_unit.getSelectedItem()).equals("arcmin"))
			radius /= 60.0;
		if (((String)combo_unit.getSelectedItem()).equals("arcsec"))
			radius /= 3600.0;
		return radius;
	}

	/**
	 * Gets the brighter limit of the limiting magnitude.
	 * @return the brighter limit of the limiting magnitude.
	 */
	public double getBrighterLimit ( ) {
		return limiting_mag_panel.getBrighterLimit();
	}

	/**
	 * Gets the fainter limit of the limiting magnitude.
	 * @return the fainter limit of the limiting magnitude.
	 */
	public double getFainterLimit ( ) {
		return limiting_mag_panel.getFainterLimit();
	}
}
