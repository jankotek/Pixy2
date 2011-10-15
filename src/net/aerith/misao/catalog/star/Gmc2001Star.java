/*
 * @(#)Gmc2001Star.java
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
 * The <code>Gmc2001Star</code> represents a star data in the [GMC2001] 
 * Large-Amplitude Variables near the Galactic Centre.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Gmc2001Star extends DefaultStar {
	/**
	 * Constructs an empty <code>Gmc2001Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Gmc2001Star ( ) {
		super();
	}

	/**
	 * Constructs an <code>Gmc2001Star</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param k_mag       the K magnitude.
	 * @param range       the magnitude range.
	 * @param period      the period.
	 */
	public Gmc2001Star ( String name,
						 String coor_string,
						 String k_mag,
						 String range,
						 String period )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(K)", k_mag));
		setKeyAndValue(new KeyAndValue("Range", range));
		setKeyAndValue(new KeyAndValue("Period", period));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "[GMC2001] ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "[GMC2001]" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "[GMC2001] Large-Amplitude Variables near the Galactic Centre";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "[GMC2001]";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "[GMC2001]";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_VARIABLE;
	}
}
