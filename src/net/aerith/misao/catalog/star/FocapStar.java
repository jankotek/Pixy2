/*
 * @(#)FocapStar.java
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
 * The <code>FocapStar</code> represents a star data in the Faint 
 * UV-excess Objects (FOCAP).
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class FocapStar extends DefaultStar {
	/**
	 * Constructs an empty <code>FocapStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public FocapStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>FocapStar</code> with data read from the
	 * catalog file.
	 * @param category    the star category.
	 * @param number      the star number.
	 * @param coor_string the R.A. and Decl.
	 * @param b_mag       the B magnitude.
	 * @param u_b         the U-B magnitude.
	 * @param z           the red shift.
	 */
	public FocapStar ( String category,
					   String number,
					   String coor_string,
					   String b_mag,
					   String u_b,
					   String z )
	{
		super(category + number, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(B)", b_mag));
		setKeyAndValue(new KeyAndValue("U-B", u_b));
		setKeyAndValue(new KeyAndValue("z", z));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "FOCAP ";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Faint UV-excess Objects (FOCAP)";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "FOCAP";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "FOCAP";
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

		folder_list.addElement(name.substring(0, 3));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return name.substring(3).replace(':', '-');
	}
}
