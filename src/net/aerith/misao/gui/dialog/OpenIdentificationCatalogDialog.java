/*
 * @(#)OpenIdentificationCatalogDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.gui.*;

/**
 * The <code>OpenIdentificationCatalogDialog</code> represents a 
 * dialog to open a catalog file or directory for identification.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class OpenIdentificationCatalogDialog extends OpenCatalogDialog {
	/**
	 * The check box to ignore negative data.
	 */
	protected JCheckBox checkbox_ignore_negative;

	/**
	 * Constructs an <code>OpenIdentificaitionCatalogDialog</code>.
	 * @param catalog_list the list of catalogs.
	 */
	public OpenIdentificationCatalogDialog ( Vector catalog_list ) {
		components = new Object[2];

		catalog_panel = new OpenCatalogPanel(catalog_list);
		components[0] = catalog_panel;

		checkbox_ignore_negative = new JCheckBox("Ignore negative data.");
		components[1] = checkbox_ignore_negative;

		setDefaultValues();
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		super.setDefaultValues();

		if (default_values.get("ignore-negative") != null)
			setNegativeDataIgnored(((Boolean)default_values.get("ignore-negative")).booleanValue());
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		super.saveDefaultValues();

		default_values.put("ignore-negative", new Boolean(isNegativeDataIgnored()));
	}

	/**
	 * Returns true if the negative data must be ignored.
	 * @return true if the negative data must be ignored.
	 */
	public boolean isNegativeDataIgnored ( ) {
		return checkbox_ignore_negative.isSelected();
	}

	/**
	 * Sets the flag to ignore the negative data if true specified.
	 * @param f if true, the negative data is ignored.
	 */
	public void setNegativeDataIgnored ( boolean f ) {
		checkbox_ignore_negative.setSelected(f);
	}
}
