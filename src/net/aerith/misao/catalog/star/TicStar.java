/*
 * @(#)TicStar.java
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
 * The <code>TicStar</code> represents a star data in the Tycho Input 
 * Catalogue.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class TicStar extends CatalogStar {
	/**
	 * The area number.
	 */
	protected short area_number;

	/**
	 * The star number in the area.
	 */
	protected short star_number;

	/**
	 * The V magnitude.
	 */
	protected float v_mag = (float)999.9;

	/**
	 * The B magnitude.
	 */
	protected float b_mag = (float)999.9;

	/**
	 * Constructs an empty <code>TicStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public TicStar ( ) {
		super();
		setMag(999.9);
	}

	/**
	 * Constructs a <code>TicStar</code> with data read from the
	 * catalog file.
	 * @param area_numer  the area number.
	 * @param star_number the star number in the area.
	 * @param coor        the R.A. and Decl.
	 */
	public TicStar ( short area_number,
					 short star_number,
					 Coor coor )
	{
		super();
		setCoor(coor);
		setMag(999.9);

		this.area_number = area_number;
		this.star_number = star_number;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>GSC 01234-05678</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String name = "GSC ";
		name += Format.formatIntZeroPadding((int)area_number, 4);
		name += "-";
		name += Format.formatIntZeroPadding((int)star_number, 5);
		return name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>GSC1234.5678</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = "GSC";
		name += String.valueOf((int)area_number);
		name += ".";
		name += String.valueOf((int)star_number);
		return name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		String s = name.substring(4);
		int p = s.indexOf("-");
		area_number = Short.parseShort(s.substring(0,p));
		star_number = Short.parseShort(s.substring(p+1));
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Tycho Input Catalogue";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "GSC";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "TIC";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "TIC";
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

		int area1 = (int)area_number / 100;
		area1 *= 100;
		int area2 = (int)area_number % 100;
		int star1 = (int)star_number / 100;
		star1 *= 100;

		folder_list.addElement(String.valueOf(area1));
		folder_list.addElement(String.valueOf(area2));
		folder_list.addElement(String.valueOf(star1));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		int star2 = (int)star_number % 100;
		return String.valueOf(star2);
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
	 * Returns true if the catalog contains magnitude data enough for
	 * photometry.
	 * @return true if the catalog contains magnitude data enough for
	 * photometry.
	 */
	public boolean supportsPhotometry ( ) {
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
	 * Gets the V magnitude.
	 * @return the V magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no V mag data
	 * is recorded in this catalog.
	 */
	public double getVMagnitude ( )
		throws UnsupportedMagnitudeSystemException
	{
		if ((double)v_mag < 99.9)
			return (double)v_mag;

		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the B magnitude.
	 * @return the B magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no B mag data
	 * is recorded in this catalog.
	 */
	public double getBMagnitude ( )
		throws UnsupportedMagnitudeSystemException
	{
		if ((double)b_mag < 99.9)
			return (double)b_mag;

		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the Rc magnitude.
	 * @return the Rc magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no Rc mag data
	 * is recorded in this catalog.
	 */
	public double getRcMagnitude ( )
		throws UnsupportedMagnitudeSystemException
	{
		double v_mag = getVMagnitude();
		double b_mag = getBMagnitude();

		return MagnitudeSystem.getRcMag(v_mag, b_mag);
	}

	/**
	 * Gets the Ic magnitude.
	 * @return the Ic magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no Rc mag data
	 * is recorded in this catalog.
	 */
	public double getIcMagnitude ( )
		throws UnsupportedMagnitudeSystemException
	{
		double v_mag = getVMagnitude();
		double b_mag = getBMagnitude();

		return MagnitudeSystem.getIcMag(v_mag, b_mag);
	}

	/**
	 * Gets the difference between the V and B magnitude.
	 * @return the difference between the V and B magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no V mag or 
	 * B mag data is recorded in this catalog.
	 */
	public double getBVDifference ( )
		throws UnsupportedMagnitudeSystemException
	{
		double v_mag = getVMagnitude();
		double b_mag = getBMagnitude();

		return b_mag - v_mag;
	}

	/**
	 * Sets the V magnitude.
	 * @param v_mag the V magnitude.
	 */
	public void setVMagnitude ( double v_mag ) {
		this.v_mag = (float)v_mag;

		setMag(v_mag);
	}

	/**
	 * Sets the B magnitude.
	 * @param b_mag the B magnitude.
	 */
	public void setBMagnitude ( double b_mag ) {
		this.b_mag = (float)b_mag;

		if (getMag() >= 99.9)
			setMag(b_mag);
	}

	/**
	 * Gets the list of magnitude systems supported by this catalog.
	 * @return the list of magnitude systems supported by this catalog.
	 */
	public String[] getAvailableMagnitudeSystems ( ) {
		String[] systems = new String[4];
		systems[0] = "Ic";
		systems[1] = "Rc";
		systems[2] = "Johnson V";
		systems[3] = "Johnson B";
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
		if (system.equals("Johnson V"))
			return getVMagnitude();
		if (system.equals("Johnson B"))
			return getBMagnitude();

		if (system.equals("V"))
			return getVMagnitude();
		if (system.equals("B"))
			return getBMagnitude();
		if (system.equals("Ic"))
			return getIcMagnitude();
		if (system.equals("Rc"))
			return getRcMagnitude();
		if (system.equals("B-V"))
			return getBVDifference();

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
	 * Gets the html help message for regular photometry.
	 * @return the html help message for regular photometry.
	 */
	public String getPhotometryHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "The Tycho Input Catalogue contains B and V magnitude.<br>";
		html += "</p><p>";
		html += "The Rc magnitude is calculated from them by the following formula.<br>";
		html += "<pre>";
		html += "    Rc = V - 0.5 * (B-V)<br>";
		html += "</pre>";
		html += "</p><p>";
		html += "The Ic magnitude is calculated from them by the following formula.<br>";
		html += "<pre>";
		html += "    Ic = B - 2.36 * (B-V)<br>";
		html += "</pre>";
		html += "</p><p>";
		html += "Reference:";
		html += "<blockquote>";
		html += "Natali F., Natali G., Pompei E., Pedichini F., 1994, A&amp;A 289, 756<br>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Gets the html help message of the specified name with magnitude
	 * system for simple magnitude comparison.
	 * @param name the catalog name with magnitude system.
	 * @return the html help message for simple magnitude comparison.
	 */
	public String getHelpMessage ( String name ) {
		if (name.indexOf("(Ic)") >= 0  ||  name.indexOf("(Rc)") >= 0) {
			return getPhotometryHelpMessage();
		}

		return null;
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
			l.addElement(new KeyAndValue("Mag(Ic)", getMagnitudeString("Ic")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}
		try {
			l.addElement(new KeyAndValue("Mag(Rc)", getMagnitudeString("Rc")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}
		try {
			l.addElement(new KeyAndValue("Mag(V)", getMagnitudeString("V")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}
		try {
			l.addElement(new KeyAndValue("Mag(B)", getMagnitudeString("B")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}

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
		if (key_and_value.getKey().equals("Mag(V)")) {
			double mag = Double.parseDouble(key_and_value.getValue());
			setVMagnitude(mag);
		} else if (key_and_value.getKey().equals("Mag(B)")) {
			double mag = Double.parseDouble(key_and_value.getValue());
			setBMagnitude(mag);
		}
	}

	/**
	 * Gets an array of keys and values related to the photometry.
	 * @return an array of keys and values related to the photometry.
	 */
	public KeyAndValue[] getKeyAndValuesForPhotometry ( ) {
		KeyAndValue[] key_and_values = new KeyAndValue[4];

		try {
			key_and_values[0] = new KeyAndValue("Ic", getMagnitudeString("Ic"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[0] = new KeyAndValue("Ic", "");
		}

		try {
			key_and_values[1] = new KeyAndValue("Rc", getMagnitudeString("Rc"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[1] = new KeyAndValue("Rc", "");
		}

		try {
			key_and_values[2] = new KeyAndValue("Johnson V", getMagnitudeString("Johnson V"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[2] = new KeyAndValue("Johnson V", "");
		}

		try {
			key_and_values[3] = new KeyAndValue("Johnson B", getMagnitudeString("Johnson B"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[3] = new KeyAndValue("Johnson B", "");
		}

		return key_and_values;
	}
}
