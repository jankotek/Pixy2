/*
 * @(#)Hat199Star.java
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
 * The <code>Hat199Star</code> represents a star data in the HAT
 * (Hungarian-made Automated Telescope)-199 Field Variable Star 
 * Catalog.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 December 31
 */

public class Hat199Star extends DefaultStar {
	/**
	 * Constructs an empty <code>Hat199Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Hat199Star ( ) {
		super();
	}

	/**
	 * Constructs an <code>Hat199Star</code> with data read from the
	 * catalog file.
	 * @param number the star number.
	 * @param coor   the R.A. and Decl.
	 * @param max    the brightest magnitude.
	 * @param min    the faintest magnitude.
	 * @param type   the type.
	 */
	public Hat199Star ( int number,
						Coor coor,
						double max,
						double min,
						String type )
	{
		super();

		this.name = Format.formatIntZeroPadding(number, 5);
		setCoor(coor);
		setCoorAccuracy(Coor.ACCURACY_100M_ARCSEC);

		setKeyAndValue(new KeyAndValue("Mag(max)", Format.formatDouble(max, 6, 2).trim()));
		setKeyAndValue(new KeyAndValue("Mag(min)", Format.formatDouble(min, 6, 2).trim()));
		setKeyAndValue(new KeyAndValue("MagSystem", "I"));
		setKeyAndValue(new KeyAndValue("Type", type));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "H199-";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "HAT(Hungarian-made Automated Telescope)-199 Field Variable Star Catalog";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "HAT-199";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "HAT-199";
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
	 * Sets the period.
	 * @period the period.
	 */
	public void setPeriod ( double period ) {
		setKeyAndValue(new KeyAndValue("Period", Format.formatDouble(period, 7, 2).trim()));
	}
}
