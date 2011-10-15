/*
 * @(#)Ucac1Star.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.star;
import java.io.*;
import java.util.Vector;
import java.awt.Color;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.PlotProperty;

/**
 * The <code>Ucac1Star</code> represents a star data in the UCAC1 
 * Catalogue. 
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Ucac1Star extends CatalogStar {
	/**
	 * The number.
	 */
	protected int number;

	/**
	 * The epoch.
	 */
	protected float epoch;

	/**
	 * The proper motion in R.A. (mas/year).
	 */
	protected float prop_ra;

	/**
	 * The proper motion in Decl. (mas/year).
	 */
	protected float prop_decl;

	/**
	 * Constructs an empty <code>Ucac1Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Ucac1Star ( ) {
		super();

		detailed_output = false;
	}

	/**
	 * Constructs a <code>Ucac1Star</code> with data read from CD-ROMs.
	 * @param numer     the number.
	 * @param coor      the R.A. and Decl.
	 * @param mag       the magnitude.
	 * @param epoch     the epoch.
	 * @param prop_ra   the proper motion in R.A. (mas/year).
	 * @param prop_decl the proper motion in Decl. (mas/year).
	 */
	public Ucac1Star ( int number,
					   Coor coor,
					   double mag,
					   double epoch,
					   double prop_ra,
					   double prop_decl )
	{
		super();
		setCoor(coor);
		setMag(mag);

		this.number = number;
		this.epoch = (float)epoch;
		this.prop_ra = (float)prop_ra;
		this.prop_decl = (float)prop_decl;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>1UCAC 12345</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String name = "1UCAC " + number;
		return name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>1UCAC_12345</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = "1UCAC_" + number;
		return name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		number = Integer.parseInt(name.substring(6));
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "UCAC1 Catalogue";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "1UCAC";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "UCAC1";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "UCAC1";
	}

	/**
	 * Gets the category of the catalog.
	 * @return the category of the catalog.
	 */
	public String getCatalogCategory ( ) {
		return CatalogManager.getCatalogCategoryName(CatalogManager.CATEGORY_STAR);
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(CatalogManager.CATEGORY_STAR));
		folder_list.addElement(getCatalogFolderCode());

		int n1 = number / 1000;
		int n2 = n1 / 1000;
		folder_list.addElement(String.valueOf(n2));
		folder_list.addElement(String.valueOf(n1));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return String.valueOf(number);
	}

	/**
	 * Gets the accuracy of R.A. and Decl.
	 * @return the accuracy of R.A. and Decl.
	 */
	public byte getCoorAccuracy ( ) {
		return Coor.ACCURACY_1M_ARCSEC;
	}

	/**
	 * Returns true if the catalog contains magnitude data.
	 * @return true if the catalog contains magnitude data.
	 */
	public boolean supportsMagnitude ( ) {
		return true;
	}

	/**
	 * Returns true if the catalog contains accurate R.A. and Decl.
	 * enough for astrometry.
	 * @return true if the catalog contains accurate R.A. and Decl.
	 * enough for astrometry.
	 */
	public boolean supportsAstrometry ( ) {
		return true;
	}

	/**
	 * Gets the list of magnitude systems supported by this catalog.
	 * @return the list of magnitude systems supported by this catalog.
	 */
	public String[] getAvailableMagnitudeSystems ( ) {
		String[] systems = new String[1];
		systems[0] = "";
		return systems;
	}

	/**
	 * Gets the magnitude of the specified system.
	 * @param system the magnitude system.
	 * @return the magnitude of the specified system.
	 * @exception UnsupportedMagnitudeSystemException if the specified
	 * magnitude system is not supported.
	 */
	public double getMagnitude ( String system )
		throws UnsupportedMagnitudeSystemException
	{
		if (system.length() == 0)
			return getMag();

		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the magnitude string of the specified system to output.
	 * @param system the magnitude system.
	 * @return the magnitude string of the specified system.
	 * @exception UnsupportedMagnitudeSystemException if the specified
	 * magnitude system is not supported.
	 */
	public String getMagnitudeString ( String system )
		throws UnsupportedMagnitudeSystemException
	{
		double mag = getMagnitude(system);
		return Format.formatDouble(mag, 5, 2).trim();
	}

	/**
	 * Gets the default property to plot stars.
	 * @return the default property to plot stars.
	 */
	public PlotProperty getDefaultProperty ( ) {
		PlotProperty property = new PlotProperty();
		property.setColor(Color.green);
		property.setFilled(false);
		property.setDependentSizeParameters(1.0, 0.0, 1);
		property.setMark(PlotProperty.PLOT_CIRCLE);
		return property;
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		Vector l = new Vector();

		l.addElement(new KeyAndValue("Mag", Format.formatDouble(getMag(), 5, 2).trim()));
		l.addElement(new KeyAndValue("Epoch", Format.formatDouble((double)epoch, 8, 4).trim()));
		l.addElement(new KeyAndValue("ProperMotion(R.A.)", Format.formatDouble((double)prop_ra / 1000.0, 8, 3).trim() + "\"/year"));
		l.addElement(new KeyAndValue("ProperMotion(Decl.)", Format.formatDouble((double)prop_decl / 1000.0, 8, 3).trim() + "\"/year"));

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
		if (key_and_value.getKey().equals("Mag")) {
			setMag(Double.parseDouble(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("Epoch")) {
			epoch = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("ProperMotion(R.A.)")) {
			int p = key_and_value.getValue().indexOf('"');
			String s = key_and_value.getValue().substring(0, p);
			prop_ra = (float)(Format.doubleValueOf(s) * 1000.0);
		} else if (key_and_value.getKey().equals("ProperMotion(Decl.)")) {
			int p = key_and_value.getValue().indexOf('"');
			String s = key_and_value.getValue().substring(0, p);
			prop_decl = (float)(Format.doubleValueOf(s) * 1000.0);
		}
	}

	/**
	 * Gets an array of keys and values related to the photometry.
	 * @return an array of keys and values related to the photometry.
	 */
	public KeyAndValue[] getKeyAndValuesForPhotometry ( ) {
		KeyAndValue[] key_and_values = new KeyAndValue[1];

		key_and_values[0] = new KeyAndValue("Mag", Format.formatDouble(getMag(), 5, 2).trim());

		return key_and_values;
	}
}
