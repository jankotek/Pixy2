/*
 * @(#)CeStar.java
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
 * The <code>CeStar</code> represents a star data in the Calan-ESO
 * Proper-Motion Catalog.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class CeStar extends DefaultStar {
	/**
	 * Constructs an empty <code>CeStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public CeStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>CeStar</code> with data read from the
	 * catalog file.
	 * @param name        the name.
	 * @param coor_string the R.A. and Decl.
	 * @param r_mag       the R magnitude.
	 * @param mu          the proper motion.
	 */
	public CeStar ( String name,
					String coor_string,
					String r_mag,
					String mu )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(R)", r_mag));
		setKeyAndValue(new KeyAndValue("mu", mu));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "CE ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "CE-" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Calan-ESO Proper-Motion Catalog";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "CE";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "CE";
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
	 * Gets the mean error of position in arcsec.
	 * @return the mean error of position in arcsec.
	 */
	public double getPositionErrorInArcsec ( ) {
		return 10.0;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 30.0;
	}
}
