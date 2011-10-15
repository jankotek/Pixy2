/*
 * @(#)TerzVStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.star;
import java.io.*;
import java.util.*;
import java.awt.Color;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.PlotProperty;

/**
 * The <code>TerzVStar</code> represents a star data in the Terzan's
 * New Variable Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class TerzVStar extends DefaultStar {
	/**
	 * Constructs an empty <code>TerzVStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public TerzVStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>TerzVStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param max         the brightest magnitude.
	 * @param min         the faintest magnitude.
	 * @param mag_system  the magnitude system.
	 */
	public TerzVStar ( String name,
					   String coor_string,
					   String max,
					   String min,
					   String mag_system )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(max)", max));
		setKeyAndValue(new KeyAndValue("Mag(min)", min));
		setKeyAndValue(new KeyAndValue("MagSystem", mag_system));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "TerzV";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Terzan's New Variable Stars";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "TerzV";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "TerzV";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_VARIABLE;
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(getCatalogCategoryNumber()));
		folder_list.addElement(getCatalogFolderCode());

		int number = Integer.parseInt(name);
		int no = (int)number / 100;
		no *= 100;

		folder_list.addElement(String.valueOf(no));

		return folder_list;
	}
}
