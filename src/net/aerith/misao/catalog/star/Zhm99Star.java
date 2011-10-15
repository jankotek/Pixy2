/*
 * @(#)Zhm99Star.java
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
 * The <code>Zhm99Star</code> represents a star data in the [ZHM99] 
 * ESO Imaging Survey.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Zhm99Star extends DefaultStar {
	/**
	 * Constructs an empty <code>Zhm99Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Zhm99Star ( ) {
		super();
	}

	/**
	 * Constructs an <code>Zhm99Star</code> with data read from the
	 * catalog file.
	 * @param category    the star category.
	 * @param number      the star number.
	 * @param coor_string the R.A. and Decl.
	 * @param i_mag       the I magnitude.
	 */
	public Zhm99Star ( String category,
					   String number,
					   String coor_string,
					   String i_mag )
	{
		super(category + " " + number, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(I)", i_mag));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "[ZHM99] EIS ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		int p = name.indexOf(' ');
		return "EIS_" + name.substring(0, p) + name.substring(p+1);
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "[ZHM99] ESO Imaging Survey";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "[ZHM99]";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "[ZHM99]";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_OTHERS;
	}
}
