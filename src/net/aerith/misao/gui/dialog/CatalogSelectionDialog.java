/*
 * @(#)CatalogSelectionDialog.java
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
import net.aerith.misao.util.star.DetectedStar;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.gui.*;

/**
 * The <code>CatalogSelectionDialog</code> represents a dialog to 
 * select some of the catalogs from the specified list.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class CatalogSelectionDialog extends Dialog {
	/**
	 * The table to select catalogs.
	 */
	protected CatalogSelectionTable table;

	/**
	 * Constructs a <code>CatalogSelectionDialog</code> of all supported 
	 * catalogs.
	 */
	public CatalogSelectionDialog ( ) {
		Vector catalog_list = new Vector();

		DetectedStar detected_star = new DetectedStar();

		String[] categories = CatalogManager.getCatalogCategories();
		for (int i = 0 ; i < categories.length ; i++) {
			Vector list = CatalogManager.getCatalogListInCategory(categories[i]);
			for (int j = 0 ; j < list.size() ; j++) {
				String catalog_name = (String)list.elementAt(j);
				if (! catalog_name.equals(detected_star.getCatalogName()))
					catalog_list.addElement(catalog_name);
			}
		}

		initialize(catalog_list);
	}

	/**
	 * Constructs a <code>CatalogSelectionDialog</code>.
	 * @param catalog_list the list of catalogs.
	 */
	public CatalogSelectionDialog ( Vector catalog_list ) {
		initialize(catalog_list);
	}

	/**
	 * Initializes.
	 * @param catalog_list the list of catalogs.
	 */
	private void initialize ( Vector catalog_list ) {
		components = new Object[1];

		JSplitPane split_pane = new JSplitPane();
		split_pane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		split_pane.setDividerSize(1);

		CatalogTree tree = new CatalogTree(new Vector());
		split_pane.setLeftComponent(new FixedScrollPane(new Size(200, 400), tree));

		table = new CatalogSelectionTable(catalog_list);
		split_pane.setRightComponent(new JScrollPane(table));

		tree.addCatalogTreeSelectionListener(table);

		components[0] = split_pane;
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Select Catalogs";
	}

	/**
	 * Gets the list of selected catalogs.
	 * @return the list of selected catalogs.
	 */
	public Vector getSelectedCatalogList ( ) {
		return table.getSelectedCatalogList();
	}
}
