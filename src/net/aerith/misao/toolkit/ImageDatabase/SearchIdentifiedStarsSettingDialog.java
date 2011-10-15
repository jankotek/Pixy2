/*
 * @(#)SearchIdentifiedStarsSettingDialog.java
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
import net.aerith.misao.gui.dialog.OpenCatalogDialog;

/**
 * The <code>SearchIdentifiedStarsSettingDialog</code> represents a 
 * dialog to open a catalog file or directory for identification, and 
 * set up the query conditions.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 May 4
 */

public class SearchIdentifiedStarsSettingDialog extends OpenCatalogDialog {
	/**
	 * The panel to specify the range of the limiting magnitude.
	 */
	protected LimitingMagPanel limiting_mag_panel;

	/**
	 * Constructs a <code>SearchIdentifiedStarsSettingDialog</code>.
	 * @param catalog_list the list of catalogs.
	 */
	public SearchIdentifiedStarsSettingDialog ( Vector catalog_list ) {
		components = new Object[2];

		catalog_panel = new OpenCatalogPanel(catalog_list);
		components[0] = catalog_panel;

		limiting_mag_panel = new LimitingMagPanel();
		limiting_mag_panel.setBorder(new TitledBorder("Limiting Mag"));
		components[1] = limiting_mag_panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Search Identified Stars Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		super.setDefaultValues();
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		super.saveDefaultValues();

		limiting_mag_panel.saveDefaultValues();
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
