/*
 * @(#)MinorPlanetCheckerStar.java
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
 * The <code>MinorPlanetCheckerStar</code> represents a star data of
 * the query result at the Minor Planet Checker.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 September 18
 */

public class MinorPlanetCheckerStar extends CatalogStar {
	/**
	 * The name
	 */
	protected String name;

	/**
	 * The R.A. motion per 1 hour in arcmin.
	 */
	protected float motion_ra;

	/**
	 * The Decl. motion per 1 hour in arcmin.
	 */
	protected float motion_decl;

	/**
	 * The orbit comment.
	 */
	protected String orbit;

	/**
	 * Constructs an empty <code>MinorPlanetCheckerStar</code>. It is
	 * used in <code>StarClass#newInstance</code> to review the XML
	 * data.
	 */
	public MinorPlanetCheckerStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>MinorPlanetCheckerStar</code> with data 
	 * read from the query result.
	 * @param name        the name.
	 * @param coor        the R.A. and Decl.
	 * @param mag         the magnitude.
	 * @param motion_ra   the R.A. motion per 1 hour in arcmin.
	 * @param motion_decl the Decl. motion per 1 hour in arcmin.
	 * @param orbit       the orbit comment.
	 */
	public MinorPlanetCheckerStar ( String name,
									Coor coor,
									float mag,
									float motion_ra,
									float motion_decl,
									String orbit )
	{
		super();
		setCoor(coor);

		this.mag = mag;

		this.name = name;
		this.motion_ra = motion_ra;
		this.motion_decl = motion_decl;
		this.orbit = orbit;
	}

	/**
	 * Gets the name of this star.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		this.name = name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Minor Planet Checker";
	}

	/**
	 * Gets the category of the catalog.
	 * @return the category of the catalog.
	 */
	public String getCatalogCategory ( ) {
		return CatalogManager.getCatalogCategoryName(CatalogManager.CATEGORY_COMET_AND_ASTEROID);
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
		return 60.0;
	}

	/**
	 * Gets the accuracy of R.A. and Decl.
	 * @return the accuracy of R.A. and Decl.
	 */
	public byte getCoorAccuracy ( ) {
		return Coor.ACCURACY_100M_ARCMIN;
	}

	/**
	 * Gets the default property to plot stars.
	 * @return the default property to plot stars.
	 */
	public PlotProperty getDefaultProperty ( ) {
		PlotProperty property = new PlotProperty();
		property.setColor(Color.blue);
		property.setFilled(false);
		property.setDependentSizeParameters(1.0, 0.0, 4);
		property.setMark(PlotProperty.PLOT_CIRCLE);
		return property;
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		Vector l = new Vector();

		l.addElement(new KeyAndValue("Mag", Format.formatDouble(getMag(), 4, 2).trim()));

		String s = Format.formatDouble(motion_ra, 5, 3).trim() + "'/h";
		if (motion_ra >= 0)
			s = "+" + s;
		l.addElement(new KeyAndValue("Motion(R.A.)", s));
		s = Format.formatDouble(motion_decl, 5, 3).trim() + "'/h";
		if (motion_decl >= 0)
			s = "+" + s;
		l.addElement(new KeyAndValue("Motion(Decl.)", s));

		l.addElement(new KeyAndValue("Orbit", orbit));

		KeyAndValue[] key_and_values = new KeyAndValue[l.size()];
		for (int i = 0 ; i < l.size() ; i++)
			key_and_values[i] = (KeyAndValue)l.elementAt(i);

		return key_and_values;
	}

	/**
	 * Sets the value of the specified key.
	 * @param key_and_value the key and value to set.
	 */
	public void setKeyAndValue ( KeyAndValue key_and_value ) {
		String value = key_and_value.getValue();

		if (key_and_value.getKey().equals("Mag")) {
			setMag(Double.parseDouble(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("Motion(R.A.)")) {
			String s = key_and_value.getValue();
			int p = s.indexOf("'/h");
			motion_ra = Float.parseFloat(s.substring(0, p));
		} else if (key_and_value.getKey().equals("Motion(Decl.)")) {
			String s = key_and_value.getValue();
			int p = s.indexOf("'/h");
			motion_decl = Float.parseFloat(s.substring(0, p));
		} else if (key_and_value.getKey().equals("Orbit")) {
			orbit = key_and_value.getValue();
		}
	}
}
