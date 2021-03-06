/*
 * @(#)EsoHaStar.java
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
 * The <code>EsoHaStar</code> represents an ESO-HA star data.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class EsoHaStar extends DefaultStar {
	/**
	 * Constructs an empty <code>EsoHaStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public EsoHaStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>EsoHaStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param mag         the magnitude.
	 */
	public EsoHaStar ( String name,
					   String coor_string,
					   String mag )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag", mag));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "ESO-HA ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "ESO-HA" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "ESO-HA";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "ESO-HA";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "ESO-HA";
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
