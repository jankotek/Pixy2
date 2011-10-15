/*
 * @(#)Lf1Star.java
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
 * The <code>Lf1Star</code> represents a star data in the Infrared 
 * Survey of a Region in Aquila (LF1).
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Lf1Star extends DefaultStar {
	/**
	 * Constructs an empty <code>Lf1Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Lf1Star ( ) {
		super();
	}

	/**
	 * Constructs an <code>Lf1Star</code> with data read from the
	 * catalog file.
	 * @param table       the table number.
	 * @param number      the star number.
	 * @param coor_string the R.A. and Decl.
	 * @param i_mag       the I magnitude.
	 * @param spectrum    the spectrum.
	 */
	public Lf1Star ( String table,
					 String number,
					 String coor_string,
					 String i_mag,
					 String spectrum )
	{
		super(table + " " + number, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(I)", i_mag));
		setKeyAndValue(new KeyAndValue("Spectrum", spectrum));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "LF 1 ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "LF1" + name.substring(0,1) + name.substring(2);
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Infrared Survey of a Region in Aquila (LF1)";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "LF 1";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "LF1";
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

		folder_list.addElement(name.substring(0, 1));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return String.valueOf(name.substring(2));
	}
}
