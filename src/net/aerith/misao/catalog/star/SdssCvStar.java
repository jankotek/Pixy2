/*
 * @(#)SdssCvStar.java
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
 * The <code>SdssCvStar</code> represents a star data in the 
 * Cataclysmic Variables from Sloan Digital Sky Survey.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class SdssCvStar extends DefaultStar {
	/**
	 * Constructs an empty <code>SdssCvStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public SdssCvStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>SdssCvStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param mag         the K magnitude.
	 * @param type        the type.
	 */
	public SdssCvStar ( String name,
						String coor_string,
						String mag,
						String type )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag", mag));
		setKeyAndValue(new KeyAndValue("Type", type));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "SDSSp J";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Cataclysmic Variables from Sloan Digital Sky Survey";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "SDSSp";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "SDSS CV";
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
