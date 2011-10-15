/*
 * @(#)OpenCatalogDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.io.File;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.gui.*;

/**
 * The <code>OpenCatalogDialog</code> represents a dialog to open a
 * catalog file or directory.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class OpenCatalogDialog extends Dialog {
	/**
	 * The panel to open a catalog.
	 */
	protected OpenCatalogPanel catalog_panel;

	/**
	 * Constructs an empty <code>OpenCatalogDialog</code>. This is
	 * only for the subclass.
	 */
	protected OpenCatalogDialog ( ) {
	}

	/**
	 * Constructs an <code>OpenCatalogDialog</code>.
	 * @param catalog_list the list of catalogs.
	 */
	public OpenCatalogDialog ( Vector catalog_list ) {
		components = new Object[1];

		catalog_panel = new OpenCatalogPanel(catalog_list);
		components[0] = catalog_panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Open Catalog";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		catalog_panel.saveDefaultValues();
	}

	/**
	 * Gets the selected catalog reader.
	 * @return the selected catalog reader.
	 */
	public CatalogReader getSelectedCatalogReader ( ) {
		return catalog_panel.getSelectedCatalogReader();
	}

	/**
	 * Gets the catalog path.
	 * @return the catalog path.
	 */
	public String getCatalogPath ( ) {
		return catalog_panel.getCatalogPath();
	}
}
