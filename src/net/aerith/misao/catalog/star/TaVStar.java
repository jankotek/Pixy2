/*
 * @(#)TaVStar.java
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
 * The <code>TaVStar</code> represents a star data in the The 
 * Astronomer Variables.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class TaVStar extends DefaultStar {
	/**
	 * Constructs an empty <code>TaVStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public TaVStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>TaVStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param max         the brightest magnitude.
	 * @param min         the faintest magnitude.
	 * @param type        the type.
	 * @param period      the period.
	 */
	public TaVStar ( String name,
					 String coor_string,
					 String max,
					 String min,
					 String type,
					 String period )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(max)", max));
		setKeyAndValue(new KeyAndValue("Mag(min)", min));
		setKeyAndValue(new KeyAndValue("Type", type));
		setKeyAndValue(new KeyAndValue("Period", period));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "TAV ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "TAV" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "The Astronomer Variables";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "TAV";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "TAV";
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
