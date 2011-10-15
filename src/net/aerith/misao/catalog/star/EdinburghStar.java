/*
 * @(#)EdinburghStar.java
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
 * The <code>EdinburghStar</code> represents a star data in the 
 * Spectrophotometry of Cool Stars in the Near Infrared (Edinburgh).
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class EdinburghStar extends DefaultStar {
	/**
	 * Constructs an empty <code>EdinburghStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public EdinburghStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>EdinburghStar</code> with data read from the
	 * catalog file.
	 * @param revision    the revision.
	 * @param number      the star number.
	 * @param coor_string the R.A. and Decl.
	 * @param i_mag       the I magnitude.
	 * @param spectrum    the spectrum.
	 */
	public EdinburghStar ( String revision,
						   String number,
						   String coor_string,
						   String i_mag,
						   String spectrum )
	{
		super(revision + "-" + number, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(I)", i_mag));
		setKeyAndValue(new KeyAndValue("Spectrum", spectrum));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "Edinburgh ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "Edinburgh" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Spectrophotometry of Cool Stars in the Near Infrared (Edinburgh)";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "Edinburgh";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "Edinburgh";
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
		
		int number = Format.intValueOf(name.substring(2)) / 100;
		number *= 100;
		folder_list.addElement(String.valueOf(number));

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
