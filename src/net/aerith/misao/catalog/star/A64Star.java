/*
 * @(#)A64Star.java
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
 * The <code>A64Star</code> represents a star data in the [A64] 
 * Catalogue of Faint Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class A64Star extends DefaultStar {
	/**
	 * Constructs an empty <code>A64Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public A64Star ( ) {
		super();
	}

	/**
	 * Constructs an <code>A64Star</code> with data read from the
	 * catalog file.
	 * @param area        the star area.
	 * @param number      the star number.
	 * @param coor_string the R.A. and Decl.
	 * @param b_mag       the b magnitude.
	 * @param spectrum    the spectrum.
	 */
	public A64Star ( String area,
					 String number,
					 String coor_string,
					 String b_mag,
					 String spectrum )
	{
		super(area + " " + number, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(b)", b_mag));
		setKeyAndValue(new KeyAndValue("Spectrum", spectrum));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "[A64] ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "[A64]" + name.substring(0, 2) + "." + name.substring(3);
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "[A64] Catalogue of Faint Stars";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "[A64]";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "[A64]";
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

		folder_list.addElement(name.substring(0, 2));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return name.substring(3);
	}
}
