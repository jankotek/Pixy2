/*
 * @(#)SaoStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.star;
import java.io.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.awt.Color;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.PlotProperty;

/**
 * The <code>SaoStar</code> represents a star data in the SAO Star 
 * Catalog.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class SaoStar extends CatalogStar {
	/**
	 * The star number.
	 */
	protected int number;

	/**
	 * The p magnitude.
	 */
	protected float p_mag = (float)999.9;

	/**
	 * The V magnitude.
	 */
	protected float v_mag = (float)999.9;

	/**
	 * The spectrum.
	 */
	protected String spectrum = "";

	/**
	 * The proper motion in R.A. (s/yr).
	 */
	protected Float prop_ra = null;

	/**
	 * The proper motion in Decl. ("/yr).
	 */
	protected Float prop_decl = null;

	/**
	 * Constructs an empty <code>SaoStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public SaoStar ( ) {
		super();
		setMag(999.9);
	}

	/**
	 * Constructs a <code>SaoStar</code> with data read from the
	 * catalog file.
	 * @param number the number.
	 * @param coor   the R.A. and Decl.
	 */
	public SaoStar ( int number,
					 Coor coor )
	{
		super();
		setCoor(coor);
		setMag(999.9);

		this.number = number;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>SAO 1234</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String name = "SAO " + number;
		return name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>SAO1234</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = "SAO" + number;
		return name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		number = Integer.parseInt(name.substring(4));
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "SAO Star Catalog";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "SAO";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "SAO";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "SAO";
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

		int n = number / 1000;
		folder_list.addElement("" + n);

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
		return Coor.ACCURACY_10M_ARCSEC;
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
	 * Sets the p magnitude.
	 * @param p_mag the p magnitude.
	 */
	public void setPMagnitude ( double p_mag ) {
		this.p_mag = (float)p_mag;

		if (getMag() > (double)p_mag)
			setMag((double)p_mag);
	}

	/**
	 * Sets the V magnitude.
	 * @param v_mag the V magnitude.
	 */
	public void setVMagnitude ( double v_mag ) {
		this.v_mag = (float)v_mag;

		if (getMag() > (double)v_mag)
			setMag((double)v_mag);
	}

	/**
	 * Sets the spectrum.
	 * @param spectrum the spectrum.
	 */
	public void setSpectrum ( String spectrum ) {
		this.spectrum = spectrum;
	}

	/**
	 * Sets the proper motion in R.A. (s/yr).
	 * @param prop the proper motion in R.A. (s/yr).
	 */
	public void setProperMotionInRA ( double prop ) {
		prop_ra = new Float((float)prop);
	}

	/**
	 * Sets the proper motion in Decl. ("/yr).
	 * @param prop the proper motion in Decl. ("/yr).
	 */
	public void setProperMotionInDecl ( double prop ) {
		prop_decl = new Float((float)prop);
	}

	/**
	 * Gets the list of magnitude systems supported by this catalog.
	 * @return the list of magnitude systems supported by this catalog.
	 */
	public String[] getAvailableMagnitudeSystems ( ) {
		String[] systems = new String[2];
		systems[0] = "p";
		systems[1] = "V";
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
		if (system.equals("p")) {
			if ((double)p_mag < 99.9)
				return (double)p_mag;
		}
		if (system.equals("V")) {
			if ((double)v_mag < 99.9)
				return (double)v_mag;
		}

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
		return Format.formatDouble(mag, 4, 2).trim();
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

		try {
			l.addElement(new KeyAndValue("Mag(p)", getMagnitudeString("p")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}
		try {
			l.addElement(new KeyAndValue("Mag(V)", getMagnitudeString("V")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}

		l.addElement(new KeyAndValue("Spectrum", spectrum));

		if (prop_ra != null)
			l.addElement(new KeyAndValue("ProperMotion(R.A.)", Format.formatDouble((double)prop_ra.floatValue(), 6, 2).trim() + "s/year"));
		if (prop_decl != null)
			l.addElement(new KeyAndValue("ProperMotion(Decl.)", Format.formatDouble((double)prop_decl.floatValue(), 6, 2).trim() + "\"/year"));

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
		if (key_and_value.getKey().equals("Mag(p)")) {
			setPMagnitude(Double.parseDouble(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("Mag(V)")) {
			setVMagnitude(Double.parseDouble(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("Spectrum")) {
			spectrum = key_and_value.getValue();
		} else if (key_and_value.getKey().equals("ProperMotion(R.A.)")) {
			int p = key_and_value.getValue().indexOf('s');
			String s = key_and_value.getValue().substring(0, p);
			prop_ra = new Float((float)(Format.doubleValueOf(s)));
		} else if (key_and_value.getKey().equals("ProperMotion(Decl.)")) {
			int p = key_and_value.getValue().indexOf('"');
			String s = key_and_value.getValue().substring(0, p);
			prop_decl = new Float((float)(Format.doubleValueOf(s)));
		}
	}

	/**
	 * Gets an array of keys and values related to the photometry.
	 * @return an array of keys and values related to the photometry.
	 */
	public KeyAndValue[] getKeyAndValuesForPhotometry ( ) {
		KeyAndValue[] key_and_values = new KeyAndValue[2];

		try {
			key_and_values[0] = new KeyAndValue("p", getMagnitudeString("p"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[0] = new KeyAndValue("p", "");
		}

		try {
			key_and_values[1] = new KeyAndValue("V", getMagnitudeString("V"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[1] = new KeyAndValue("V", "");
		}

		return key_and_values;
	}
}
