/*
 * @(#)YaloStar.java
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
 * The <code>YaloStar</code> represents a star data in the Yale, AURA, 
 * Lisbon, and Ohio New Variable Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class YaloStar extends DefaultStar {
	/**
	 * Constructs an empty <code>YaloStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public YaloStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>YaloStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param max         the brightest magnitude.
	 * @param min         the faintest magnitude.
	 * @param mag_system  the magnitude system.
	 * @param type        the type.
	 * @param period      the period.
	 * @param epoch       the epoch.
	 */
	public YaloStar ( String name,
					  String coor_string,
					  String max,
					  String min,
					  String mag_system,
					  String type, 
					  String period,
					  String epoch )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(max)", max));
		setKeyAndValue(new KeyAndValue("Mag(min)", min));
		setKeyAndValue(new KeyAndValue("MagSystem", mag_system));
		setKeyAndValue(new KeyAndValue("Type", type));
		setKeyAndValue(new KeyAndValue("Period", period));
		setKeyAndValue(new KeyAndValue("Epoch", epoch));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "YALO J";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Yale, AURA, Lisbon, and Ohio New Variable Stars";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "YALO";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "YALO";
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
