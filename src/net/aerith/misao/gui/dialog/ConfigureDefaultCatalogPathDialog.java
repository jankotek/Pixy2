/*
 * @(#)ConfigureDefaultCatalogPathDialog.java
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
 * The <code>ConfigureDefaultCatalogPathDialog</code> represents a 
 * dialog to set the default path to read the catalog.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class ConfigureDefaultCatalogPathDialog extends OpenCatalogDialog {
	/**
	 * Constructs an <code>OpenIdentificaitonCatalogDialog</code>.
	 * @param catalog_list the list of catalogs.
	 */
	public ConfigureDefaultCatalogPathDialog ( Vector catalog_list ) {
		components = new Object[2];

		catalog_panel = new OpenCatalogPanel(catalog_list);
		components[0] = catalog_panel;

		setDefaultValues();

		// Sets the default path when a catalog is selected.
		catalog_panel.enableDefaultPath();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Configure Default Catalog Path";
	}
}
