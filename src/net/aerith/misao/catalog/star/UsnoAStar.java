/*
 * @(#)UsnoAStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.star;
import java.util.Vector;
import java.awt.Color;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.PlotProperty;

/**
 * The <code>UsnoAStar</code> represents a star data in the 
 * USNO-A1.0/A2.0 CD-ROMs.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public abstract class UsnoAStar extends CatalogStar {
	/**
	 * The file number;
	 */
	protected short file_number;

	/**
	 * The star number in the file.
	 */
	protected int star_number;

	/**
	 * The R magnitude in 0.1 mag unit. In the case of 9999, no valid
	 * R  magnitude is recorded.
	 */
	protected short R_mag10 = 9999;

	/**
	 * The B magnitude in 0.1 mag unit. In the case of 9999, no valid
	 * B  magnitude is recorded.
	 */
	protected short B_mag10 = 9999;

	/**
	 * Gets the version of the USNO-A catalog. This method must be 
	 * overrided in the subclasses.
	 * @return the string of version.
	 */
	public abstract String getVersion();

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>USNO-A1.0 0123.12345678</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String name = getVersion();
		name += " ";
		name += Format.formatIntZeroPadding((int)file_number, 4);
		name += ".";
		name += Format.formatIntZeroPadding(star_number, 8);
		return name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>USNO-A1.0_0123.12345678</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = getVersion();
		name += "_";
		name += Format.formatIntZeroPadding((int)file_number, 4);
		name += ".";
		name += Format.formatIntZeroPadding(star_number, 8);
		return name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		int l = getVersion().length() + 1;
		String s = name.substring(l);

		int p = s.indexOf(".");
		file_number = Short.parseShort(s.substring(0,p));
		star_number = Integer.parseInt(s.substring(p+1));
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

		String s = Format.formatIntZeroPadding(star_number, 8);
		folder_list.addElement(s.substring(0, 2));
		folder_list.addElement(s.substring(2, 5));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		String s = Format.formatIntZeroPadding(star_number, 8);
		return s.substring(5);
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
	 * Gets the list of magnitude systems supported by this catalog.
	 * @return the list of magnitude systems supported by this catalog.
	 */
	public String[] getAvailableMagnitudeSystems ( ) {
		String[] systems = new String[3];
		systems[0] = "R";
		systems[1] = "B";
		systems[2] = "Kato V";
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
		if (system.equals("R")  &&  R_mag10 != 9999)
			return (double)R_mag10 / 10.0;
		if (system.equals("B")  &&  B_mag10 != 9999)
			return (double)B_mag10 / 10.0;
		if (system.equals("Kato V")) {
			if (R_mag10 != 9999  &&  B_mag10 != 9999)
				return getMag();
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
	 * Gets the html help message of the specified name with magnitude
	 * system for simple magnitude comparison.
	 * @param name the catalog name with magnitude system.
	 * @return the html help message for simple magnitude comparison.
	 */
	public String getHelpMessage ( String name ) {
		if (name.indexOf("(Kato V)") >= 0) {
			String html = "<html><body>";
			html += "<p>";
			html += getVersion();
			html += " catalog contains R and B magnitude.<br>";
			html += "</p><p>";
			html += "The preliminary V magnitude is calculated by<br>";
			html += "Taichi Kato's formula:<br>";
			html += "<pre>";
			html += "    V = R + 0.375 * (B - R)<br>";
			html += "</pre>";
			html += "</p><p>";
			html += "Reference:";
			html += "<blockquote>";
			html += "[vsnet-chat 700]<br>";
			html += "<u><font color=\"#0000ff\">http://www.kusastro.kyoto-u.ac.jp/vsnet/Mail/chat0/msg00700.html</font></u>";
			html += "</blockquote>";
			html += "</p>";
			html += "</body></html>";
			return html;
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

		if (R_mag10 != 9999)
			l.addElement(new KeyAndValue("Mag(R)", Format.formatDouble((double)R_mag10 / 10.0, 4, 2).trim()));
		if (B_mag10 != 9999)
			l.addElement(new KeyAndValue("Mag(B)", Format.formatDouble((double)B_mag10 / 10.0, 4, 2).trim()));

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
		if (key_and_value.getKey().equals("Mag(R)")) {
			double m = Format.doubleValueOf(key_and_value.getValue()) * 10.0 + 0.5;
			R_mag10 = (short)m;
			detailed_output = true;
		} else if (key_and_value.getKey().equals("Mag(B)")) {
			double m = Format.doubleValueOf(key_and_value.getValue()) * 10.0 + 0.5;
			B_mag10 = (short)m;
			detailed_output = true;
		}

		// preliminary V mag
		double mag_r = (double)R_mag10 / 10.0;
		double mag_b = (double)B_mag10 / 10.0;
		double mag_v = 0.0;
		if (R_mag10 != 9999  &&  B_mag10 != 9999)
			mag_v = MagnitudeSystem.getUsnoVMag(mag_r, mag_b);
		else if (R_mag10 != 9999)
			mag_v = mag_r;
		else if (B_mag10 != 9999)
			mag_v = mag_b;
		else
			mag_v = 99.9;
		setMag(mag_v);
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		if (detailed_output == true) {
			return super.getOutputString();
		}

		String s = getName() + getItemDelimiter() + getCoor().getOutputStringTo10mArcsecWithoutSpace();
		if (R_mag10 != 9999)
			s += getItemDelimiter() + Format.formatDouble((double)R_mag10 / 10.0, 4, 2).trim() + "R";
		if (B_mag10 != 9999)
			s += getItemDelimiter() + Format.formatDouble((double)B_mag10 / 10.0, 4, 2).trim() + "B";
		return s;
	}

	/**
	 * Gets an array of keys and values related to the photometry.
	 * @return an array of keys and values related to the photometry.
	 */
	public KeyAndValue[] getKeyAndValuesForPhotometry ( ) {
		KeyAndValue[] key_and_values = new KeyAndValue[2];

		if (R_mag10 != 9999)
			key_and_values[0] = new KeyAndValue("R", Format.formatDouble((double)R_mag10 / 10.0, 4, 2).trim());
		else
			key_and_values[0] = new KeyAndValue("R", "");

		if (B_mag10 != 9999)
			key_and_values[1] = new KeyAndValue("B", Format.formatDouble((double)B_mag10 / 10.0, 4, 2).trim());
		else
			key_and_values[1] = new KeyAndValue("B", "");

		return key_and_values;
	}
}
