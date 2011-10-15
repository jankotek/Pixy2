/*
 * @(#)BpsStar.java
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
 * The <code>BpsStar</code> represents a star data in the Beers
 * Preston Shectman Catalog with Burrell Schmidt/Curtis Schmidt Telescope.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class BpsStar extends DefaultStar {
	/**
	 * Constructs an empty <code>BpsStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public BpsStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>BpsStar</code> with data read from the
	 * catalog file.
	 * @param category    the star category.
	 * @param number      the star number.
	 * @param coor_string the R.A. and Decl.
	 * @param v_mag       the V magnitude.
	 * @param b_v         the B-V.
	 */
	public BpsStar ( String category,
					 String number,
					 String coor_string,
					 String v_mag, 
					 String b_v )
	{
		super(category + " " + number, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(V)", v_mag));
		setKeyAndValue(new KeyAndValue("B-V", b_v));
	}

	/**
	 * Gets the prefix of the name. It must be overrided in the
	 * subclasses.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "BPS ";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Beers Preston Shectman Catalog with Burrell Schmidt/Curtis Schmidt Telescope";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "BPS";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "BPS";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_OTHERS;
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(getCatalogCategoryNumber()));
		folder_list.addElement(getCatalogFolderCode());

		int p = name.indexOf(" ");
		folder_list.addElement(name.substring(0, p));

		if (name.substring(0, p).equals("CS"))
			folder_list.addElement(name.substring(p+1, p+3));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		int p = name.indexOf(" ");
		return name.substring(p + 1);
	}
}
