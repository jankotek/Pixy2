/*
 * @(#)TychoStar.java
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
 * The <code>TychoStar</code> represents a star data in the Tycho
 * Catalogue file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class TychoStar extends CatalogStar {
	/**
	 * The TYC1 number.
	 */
	protected short tyc1;

	/**
	 * The TYC2 number.
	 */
	protected short tyc2;

	/**
	 * The TYC3 number.
	 */
	protected short tyc3;

	/**
	 * The BT magnitude.
	 */
	protected float bt_mag = (float)999.9;

	/**
	 * The VT magnitude.
	 */
	protected float vt_mag = (float)999.9;

	/**
	 * The B-V magnitude.
	 */
	protected float b_v = (float)999.9;

	/**
	 * Constructs an empty <code>TychoStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public TychoStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>TychoStar</code> with data read from the
	 * catalog file.
	 * @param tyc1 the TYC1 number.
	 * @param tyc2 the TYC2 number.
	 * @param tyc3 the TYC3 number.
	 * @param coor the R.A. and Decl.
	 * @param mag  the Johnson V magnitude.
	 */
	public TychoStar ( short tyc1,
					   short tyc2,
					   short tyc3,
					   Coor coor,
					   double mag )
	{
		super();
		setCoor(coor);
		setMag(mag);

		this.tyc1 = tyc1;
		this.tyc2 = tyc2;
		this.tyc3 = tyc3;
		this.bt_mag = (float)999.9;
		this.vt_mag = (float)999.9;
		this.b_v = (float)999.9;
	}

	/**
	 * Sets the BT magnitude.
	 * @param mag the BT magnitude.
	 */
	public void setBtMagnitude ( double mag ) {
		bt_mag = (float)mag;
	}

	/**
	 * Sets the VT magnitude.
	 * @param mag the VT magnitude.
	 */
	public void setVtMagnitude ( double mag ) {
		vt_mag = (float)mag;
	}

	/**
	 * Sets the difference between the V and B magnitude.
	 * @param mag the difference between the V and B magnitude.
	 */
	public void setBVDifference ( double mag ) {
		b_v = (float)mag;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>TYC 123-4567-1</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String name = "TYC " + tyc1 + "-" + tyc2 + "-" + tyc3;
		return name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>TYC123-4567-1</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = "TYC" + tyc1 + "-" + tyc2 + "-" + tyc3;
		return name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		String s = name.substring(4);
		StringTokenizer st = new StringTokenizer(s, "-");
		tyc1 = Short.parseShort(st.nextToken());
		tyc2 = Short.parseShort(st.nextToken());
		tyc3 = Short.parseShort(st.nextToken());
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Tycho Catalogue";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "TYC";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "Tycho";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "Tycho";
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

		int tyc1a = (int)tyc1 / 100;
		int tyc1b = (int)tyc1 % 100;
		tyc1a *= 100;
		int tyc2a = (int)tyc2 / 100;

		folder_list.addElement(String.valueOf(tyc1a));
		folder_list.addElement(String.valueOf(tyc1b));
		folder_list.addElement(String.valueOf(tyc2a));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		int tyc2b = (int)tyc2 % 100;
		String s = String.valueOf(tyc2b);
		if (tyc3 != 1)
			s += "-" + String.valueOf(tyc3);
		return s;
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
		return getMag();
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
		if (b_v < 30.0)
			return getMag() + (double)b_v;
		
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
		if (b_v < 30.0)
			return (double)b_v;
		
		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the list of magnitude systems supported by this catalog.
	 * @return the list of magnitude systems supported by this catalog.
	 */
	public String[] getAvailableMagnitudeSystems ( ) {
		String[] systems = new String[6];
		systems[0] = "Ic";
		systems[1] = "Rc";
		systems[2] = "Johnson V";
		systems[3] = "Johnson B";
		systems[4] = "VT";
		systems[5] = "BT";
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
		if (system.equals("VT")  &&  (double)vt_mag < 30.0)
			return (double)vt_mag;
		if (system.equals("BT")  &&  (double)bt_mag < 30.0)
			return (double)bt_mag;

		if (system.equals("V"))
			return getVMagnitude();
		if (system.equals("B"))
			return getBMagnitude();
		if (system.equals("Rc"))
			return getRcMagnitude();
		if (system.equals("Ic"))
			return getIcMagnitude();
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

		int figures = 6;
		if (system.equals("Johnson V")  ||  system.equals("V")  ||
			system.equals("Johnson B")  ||  system.equals("B")  ||
			system.equals("Rc")  ||  system.equals("Ic"))
			figures = 5;

		return Format.formatDouble(mag, figures, 2).trim();
	}

	/**
	 * Gets the html help message for regular photometry.
	 * @return the html help message for regular photometry.
	 */
	public String getPhotometryHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "Tycho Catalogue contains Johnson V magnitude and B-V value.<br>";
		html += "</p><p>";
		html += "The Rc magnitude is calculated from the V magnitude and B-V value<br>";
		html += "by the following formula.<br>";
		html += "<pre>";
		html += "    Rc = V - 0.5 * (B-V)<br>";
		html += "</pre>";
		html += "</p><p>";
		html += "The Ic magnitude is calculated from the B magnitude and B-V value<br>";
		html += "by the following formula.<br>";
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
		try {
			l.addElement(new KeyAndValue("Mag(VT)", getMagnitudeString("VT")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}
		try {
			l.addElement(new KeyAndValue("Mag(BT)", getMagnitudeString("BT")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}
		try {
			l.addElement(new KeyAndValue("B-V", getMagnitudeString("B-V")));
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
			setMag(Double.parseDouble(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("Mag(VT)")) {
			vt_mag = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("Mag(BT)")) {
			bt_mag = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("B-V")) {
			b_v = (float)Format.doubleValueOf(key_and_value.getValue());
		}
	}

	/**
	 * Gets an array of keys and values related to the photometry.
	 * @return an array of keys and values related to the photometry.
	 */
	public KeyAndValue[] getKeyAndValuesForPhotometry ( ) {
		KeyAndValue[] key_and_values = new KeyAndValue[7];

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

		try {
			key_and_values[4] = new KeyAndValue("VT", getMagnitudeString("VT"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[4] = new KeyAndValue("VT", "");
		}

		try {
			key_and_values[5] = new KeyAndValue("BT", getMagnitudeString("BT"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[5] = new KeyAndValue("BT", "");
		}

		try {
			key_and_values[6] = new KeyAndValue("B-V", getMagnitudeString("B-V"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[6] = new KeyAndValue("B-V", "");
		}

		return key_and_values;
	}
}
