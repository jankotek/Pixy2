/*
 * @(#)LowellGStar.java
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
 * The <code>LowellGStar</code> represents a star data in the Lowell
 * Proper Motion Survey.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class LowellGStar extends DefaultStar {
	/**
	 * The first star number.
	 */
	private short number1 = 0;

	/**
	 * The second star number.
	 */
	private String number2 = "";

	/**
	 * Constructs an empty <code>LowellGStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public LowellGStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>LowellGStar</code> with data read from the
	 * catalog file.
	 * @param number1     the first star number.
	 * @param number2     the second star number.
	 * @param coor_string the R.A. and Decl.
	 * @param pg_mag      the pg magnitude.
	 * @param mu          the proper motion.
	 */
	public LowellGStar ( String number1,
						 String number2,
						 String coor_string,
						 String pg_mag,
						 String mu )
	{
		super(number1 + "-" + number2, coor_string);

		this.number1 = Short.parseShort(number1);
		this.number2 = number2;

		setKeyAndValue(new KeyAndValue("Mag(pg)", pg_mag));
		setKeyAndValue(new KeyAndValue("mu", mu));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "G ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "G" + name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		this.name = name.substring(2);

		int p = this.name.indexOf('-');
		this.number1 = Short.parseShort(this.name.substring(0, p));
		this.number2 = this.name.substring(p + 1);
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Lowell Proper Motion Survey";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "G";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "G";
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

		folder_list.addElement(String.valueOf(number1));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return number2;
	}

	/**
	 * Gets the mean error of position in arcsec.
	 * @return the mean error of position in arcsec.
	 */
	public double getPositionErrorInArcsec ( ) {
		return 30.0;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 90.0;
	}
}
