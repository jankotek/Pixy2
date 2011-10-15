/*
 * @(#)Gsc11Star.java
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
 * The <code>Gsc11Star</code> represents a star data in the GSC 1.1
 * CD-ROMs
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Gsc11Star extends CatalogStar {
	/**
	 * The area number.
	 */
	protected short area_number;

	/**
	 * The star number in the area.
	 */
	protected short star_number;

	/**
	 * The position error in 0.1 arcsec unit.
	 */
	protected short position_error10;

	/**
	 * The magnitude error in 0.01 mag unit.
	 */
	protected short mag_error100;

	/**
	 * The band number.
	 */
	protected byte band;

	/**
	 * The class of the star.
	 */
	protected byte class_number;

	/**
	 * The plate code.
	 */
	protected byte[] plate;

	/**
	 * The flag.
	 */
	protected byte flag;

	/**
	 * The epoch. It can be null.
	 */
	protected byte[] epoch;

	/**
	 * Constructs an empty <code>Gsc11Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Gsc11Star ( ) {
		super();

		detailed_output = false;
	}

	/**
	 * Constructs a <code>Gsc11Star</code> with data read from CD-ROMs.
	 * @param area_numer       the area number.
	 * @param star_number      the star number in the area.
	 * @param coor             the R.A. and Decl.
	 * @param mag              the magnitude.
	 * @param position_error10 the position error in 0.1 arcsec unit.
	 * @param mag_error100     the magnitude error in 0.01 mag unit.
	 * @param band             the band number.
	 * @param class_number     the class of the star.
	 * @param plate            the plate code.
	 * @param flag             the flag of this star.
	 * @param epoch            the epoch, which can be null.
	 */
	public Gsc11Star ( short area_numer,
					   short star_number,
					   Coor coor,
					   double mag,
					   short position_error10,
					   short mag_error100,
					   byte band,
					   byte class_number,
					   byte[] plate,
					   byte flag,
					   byte[] epoch )
	{
		super();
		setCoor(coor);
		setMag(mag);

		this.area_number = area_numer;
		this.star_number = star_number;
		this.position_error10 = position_error10;
		this.mag_error100 = mag_error100;
		this.band = band;
		this.class_number = class_number;
		this.plate = plate;
		this.flag = flag;
		this.epoch = epoch;
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
		return "Guide Star Catalog 1.1";
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
		return "GSC 1.1";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "GSC1.1";
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

		byte[] b = new byte[3];
		b[0] = band;
		b[1] = class_number;
		b[2] = flag;
		String byte_str = new String(b);

		if (detailed_output) {
			l.addElement(new KeyAndValue("Error", Format.formatDouble((double)position_error10 / 10.0, 3, 1).trim() + "\""));
		}
		l.addElement(new KeyAndValue("Mag", Format.formatDouble(getMag(), 5, 2).trim()));
		if (detailed_output) {
			l.addElement(new KeyAndValue("MagError", Format.formatDouble((double)mag_error100 / 100.0, 4, 1).trim()));
			l.addElement(new KeyAndValue("Band", String.valueOf(byte_str.charAt(0))));
			l.addElement(new KeyAndValue("Class", String.valueOf(byte_str.charAt(1))));
			l.addElement(new KeyAndValue("Plate", new String(plate)));
			l.addElement(new KeyAndValue("Flag", String.valueOf(byte_str.charAt(2))));
			if (epoch != null) {
				String epoch_str = new String(epoch);
				l.addElement(new KeyAndValue("Epoch", epoch_str.substring(0, 7) + "19" + epoch_str.substring(7, 9)));
			}
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
		byte[] b = null;
		try {
			b = key_and_value.getValue().getBytes("ASCII");
		} catch ( UnsupportedEncodingException exception ) {
			b = key_and_value.getValue().getBytes();
		} 

		if (key_and_value.getKey().equals("Error")) {
			double err = Format.doubleValueOf(key_and_value.getValue()) * 10.0 + 0.5;
			position_error10 = (short)err;
			detailed_output = true;
		} else if (key_and_value.getKey().equals("Mag")) {
			setMag(Double.parseDouble(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("MagError")) {
			double err = Format.doubleValueOf(key_and_value.getValue()) * 100.0 + 0.5;
			mag_error100 = (short)err;
			detailed_output = true;
		} else if (key_and_value.getKey().equals("Band")) {
			band = b[0];
			detailed_output = true;
		} else if (key_and_value.getKey().equals("Class")) {
			class_number = b[0];
			detailed_output = true;
		} else if (key_and_value.getKey().equals("Plate")) {
			plate = b;
			detailed_output = true;
		} else if (key_and_value.getKey().equals("Flag")) {
			flag = b[0];
			detailed_output = true;
		} else if (key_and_value.getKey().equals("Epoch")) {
			epoch = new byte[9];
			for (int i = 0 ; i < 11 ; i++) {
				int p = i;
				if (i >= 9)
					p -= 2;
				epoch[p] = b[i];
			}
			detailed_output = true;
		}
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		if (detailed_output == true) {
			return super.getOutputString();
		}

		String s = getName() + getItemDelimiter() + getCoor().getOutputStringTo100mArcsecWithoutSpace();
		s += getItemDelimiter() + Format.formatDouble(getMag(), 5, 2).trim();
		return s;
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
