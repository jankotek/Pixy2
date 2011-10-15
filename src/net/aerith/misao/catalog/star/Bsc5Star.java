/*
 * @(#)Bsc5Star.java
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
 * The <code>Bsc5Star</code> represents a star data in the Bright Star 
 * Catalogue, 5th Revised Ed.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Bsc5Star extends CatalogStar {
	/**
	 * The star number.
	 */
	protected int number;

	/**
	 * The B-V magnitude.
	 */
	protected float b_v = (float)999.9;

	/**
	 * The U-B magnitude.
	 */
	protected float u_b = (float)999.9;

	/**
	 * The R-I magnitude.
	 */
	protected float r_i = (float)999.9;

	/**
	 * The spectrum.
	 */
	protected String spectrum = "";

	/**
	 * The proper motion in R.A. ("/yr).
	 */
	protected Float prop_ra = null;

	/**
	 * The proper motion in Decl. ("/yr).
	 */
	protected Float prop_decl = null;

	/**
	 * Constructs an empty <code>Bsc5Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Bsc5Star ( ) {
		super();
		setMag(999.9);
	}

	/**
	 * Constructs a <code>Bsc5Star</code> with data read from the
	 * catalog file.
	 * @param number the number.
	 * @param coor   the R.A. and Decl.
	 * @param v_mag  the Johnson V magnitude.
	 */
	public Bsc5Star ( int number,
					  Coor coor,
					  double v_mag )
	{
		super();
		setCoor(coor);
		setMag(v_mag);

		this.number = number;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>HR 1234</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String name = "HR " + number;
		return name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>HR1234</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = "HR" + number;
		return name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		number = Integer.parseInt(name.substring(3));
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Bright Star Catalogue 5";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "HR";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "BSC5";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "BSC5";
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

		int n = number / 100;
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
		return Coor.ACCURACY_ARCSEC;
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
	 * Gets the U magnitude.
	 * @return the U magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no U mag data
	 * is recorded in this catalog.
	 */
	public double getUMagnitude ( )
		throws UnsupportedMagnitudeSystemException
	{
		if ((double)b_v < 99.9  &&  (double)u_b < 99.9)
			return getMag() + (double)b_v + (double)u_b;

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
		if ((double)b_v < 99.9)
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
		if ((double)r_i < 99.9) {
			double r_mag = getRcMagnitude();
			return r_mag - (double)r_i;
		}

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
		if ((double)b_v < 99.9)
			return (double)b_v;

		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Sets the difference between the V and B magnitude.
	 * @param b_v the difference between the V and B magnitude.
	 */
	public void setBVDifference ( double b_v ) {
		this.b_v = (float)b_v;
	}

	/**
	 * Sets the difference between the B and U magnitude.
	 * @param u_b the difference between the B and U magnitude.
	 */
	public void setUBDifference ( double u_b ) {
		this.u_b = (float)u_b;
	}

	/**
	 * Sets the difference between the I and R magnitude.
	 * @param r_i the difference between the I and R magnitude.
	 */
	public void setRIDifference ( double r_i ) {
		this.r_i = (float)r_i;
	}

	/**
	 * Sets the spectrum.
	 * @param spectrum the spectrum.
	 */
	public void setSpectrum ( String spectrum ) {
		this.spectrum = spectrum;
	}

	/**
	 * Sets the proper motion in R.A. ("/yr).
	 * @param prop the proper motion in R.A. ("/yr).
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
		String[] systems = new String[5];
		systems[0] = "Ic";
		systems[1] = "Rc";
		systems[2] = "Johnson V";
		systems[3] = "Johnson B";
		systems[4] = "U";
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
		if (system.equals("U"))
			return getUMagnitude();
		if (system.equals("B"))
			return getBMagnitude();
		if (system.equals("Ic"))
			return getIcMagnitude();
		if (system.equals("Rc"))
			return getRcMagnitude();
		if (system.equals("B-V"))
			return getBVDifference();

		if (system.equals("U-B")) {
			if ((double)u_b < 99.9)
				return (double)u_b;
		}
		if (system.equals("R-I")) {
			if ((double)r_i < 99.9)
				return (double)r_i;
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
		return Format.formatDouble(mag, 5, 2).trim();
	}

	/**
	 * Gets the html help message for regular photometry.
	 * @return the html help message for regular photometry.
	 */
	public String getPhotometryHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "The Bright Star Catalogue 5 contains Johnson V magnitude for all,<br>";
		html += "and U-B, B-V, R-I value for some.<br>";
		html += "</p><p>";
		html += "The Rc magnitude is calculated from the V magnitude and B-V value<br>";
		html += "by the following formula.<br>";
		html += "<pre>";
		html += "    Rc = V - 0.5 * (B-V)<br>";
		html += "</pre>";
		html += "</p><p>";
		html += "The Ic magnitude is calculated from the B magnitude and B-V value<br>";
		html += "if R-I value is not recorded, by the following formula.<br>";
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
			l.addElement(new KeyAndValue("Mag(U)", getMagnitudeString("U")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}
		try {
			l.addElement(new KeyAndValue("B-V", getMagnitudeString("B-V")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}
		try {
			l.addElement(new KeyAndValue("U-B", getMagnitudeString("U-B")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}
		try {
			l.addElement(new KeyAndValue("R-I", getMagnitudeString("R-I")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}

		l.addElement(new KeyAndValue("Spectrum", spectrum));

		if (prop_ra != null)
			l.addElement(new KeyAndValue("ProperMotion(R.A.)", Format.formatDouble((double)prop_ra.floatValue(), 6, 2).trim() + "\"/year"));
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
		if (key_and_value.getKey().equals("Mag(V)")) {
			setMag(Double.parseDouble(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("B-V")) {
			b_v = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("U-B")) {
			u_b = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("R-I")) {
			r_i = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("Spectrum")) {
			spectrum = key_and_value.getValue();
		} else if (key_and_value.getKey().equals("ProperMotion(R.A.)")) {
			int p = key_and_value.getValue().indexOf('"');
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
		KeyAndValue[] key_and_values = new KeyAndValue[8];

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
			key_and_values[4] = new KeyAndValue("U", getMagnitudeString("U"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[4] = new KeyAndValue("U", "");
		}

		try {
			key_and_values[5] = new KeyAndValue("B-V", getMagnitudeString("B-V"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[5] = new KeyAndValue("B-V", "");
		}

		try {
			key_and_values[6] = new KeyAndValue("U-B", getMagnitudeString("U-B"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[6] = new KeyAndValue("U-B", "");
		}

		try {
			key_and_values[7] = new KeyAndValue("R-I", getMagnitudeString("R-I"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[7] = new KeyAndValue("R-I", "");
		}

		return key_and_values;
	}
}
