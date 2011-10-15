/*
 * @(#)StareStar.java
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
 * The <code>StareStar</code> represents a star data in the STARE
 * Project New Variable Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class StareStar extends DefaultStar {
	/**
	 * Constructs an empty <code>StareStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public StareStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>StareStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param mag         the magnitude.
	 * @param range       the magnitude range.
	 * @param type        the type.
	 * @param period      the period.
	 */
	public StareStar ( String name,
					   String coor_string,
					   String mag,
					   String range,
					   String type,
					   String period )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag", mag));
		setKeyAndValue(new KeyAndValue("Range", range));
		setKeyAndValue(new KeyAndValue("Type", type));
		setKeyAndValue(new KeyAndValue("Period", period));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "STARE ";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "STARE Project New Variable Stars";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "STARE";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "STARE";
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
