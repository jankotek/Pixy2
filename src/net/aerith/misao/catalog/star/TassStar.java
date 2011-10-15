/*
 * @(#)TassStar.java
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
 * The <code>TassStar</code> represents a star data in the TASS
 * Variables.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public class TassStar extends DefaultStar {
	/**
	 * True if the name prefix is "TASS".
	 */
	protected boolean prefix_tass = false;

	/**
	 * Constructs an empty <code>TassStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public TassStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>TassStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param mag         the magnitude.
	 * @param range       the magnitude range.
	 * @param max         the brightest magnitude.
	 * @param min         the faintest magnitude.
	 * @param mag_system  the magnitude system.
	 * @param type        the type.
	 * @param period      the period.
	 * @param epoch       the epoch.
	 */
	public TassStar ( String name,
					  String coor_string,
					  String mag,
					  String range,
					  String max,
					  String min,
					  String mag_system,
					  String type,
					  String period, 
					  String epoch )
	{
		super(name, coor_string);

		setName(name);

		setKeyAndValue(new KeyAndValue("Mag", mag));
		setKeyAndValue(new KeyAndValue("Range", range));
		setKeyAndValue(new KeyAndValue("Mag(max)", max));
		setKeyAndValue(new KeyAndValue("Mag(min)", min));
		setKeyAndValue(new KeyAndValue("Mag(min)", min));
		setKeyAndValue(new KeyAndValue("MagSystem", mag_system));
		setKeyAndValue(new KeyAndValue("Type", type));
		setKeyAndValue(new KeyAndValue("Period", period));
		setKeyAndValue(new KeyAndValue("Epoch", epoch));
	}

	/**
	 * Gets the name of this star.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		if (prefix_tass)
			return "TASS " + name;

		return name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses. This method returns the name itself. The name 
	 * cannot have any space characters.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		if (name.indexOf("GSC ") == 0)
			return "GSC" + name.substring(4);
		if (name.indexOf("BD ") == 0)
			return "BD" + name.substring(3);
		if (name.indexOf("HD ") == 0)
			return "HD" + name.substring(3);
		if (name.indexOf("IRAS ") == 0)
			return "IRAS" + name.substring(5);

		return getName().replace(' ', '_');
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		if (name.indexOf("TASS ") == 0) {
			this.name = name.substring(5);
			prefix_tass = true;
		} else {
			this.name = name;
			prefix_tass = false;
		}
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "TASS Variables";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "TASS";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "TASS";
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
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		if (prefix_tass)
			return name.replace('"', '-').replace('|', '-').replace('*', '-').replace('?', '-').replace('<', '-').replace('>', '-').replace('/', '-').replace('\\', '-').replace(':', '-');

		return getVsnetName();
	}

	/**
	 * Gets the mean error of position in arcsec.
	 * @return the mean error of position in arcsec.
	 */
	public double getPositionErrorInArcsec ( ) {
		return 15.0;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 60.0;
	}
}
