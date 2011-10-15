/*
 * @(#)HipparcosStar.java
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
 * The <code>HipparcosStar</code> represents a star data in the 
 * Hipparcos Catalogue.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class HipparcosStar extends CatalogStar {
	/**
	 * The number.
	 */
	protected int number;

	/**
	 * The V magnitude.
	 */
	protected float v_mag = (float)999.9;

	/**
	 * The BT magnitude.
	 */
	protected float bt_mag = (float)999.9;

	/**
	 * The VT magnitude.
	 */
	protected float vt_mag = (float)999.9;

	/**
	 * The Hp magnitude.
	 */
	protected float hp_mag = (float)999.9;

	/**
	 * The B-V magnitude.
	 */
	protected float b_v = (float)999.9;

	/**
	 * The V-I magnitude.
	 */
	protected float v_i = (float)999.9;

	/**
	 * The maximum magnitude.
	 */
	protected float mag_max = (float)999.9;

	/**
	 * The minimum magnitude.
	 */
	protected float mag_min = (float)999.9;

	/**
	 * The period.
	 */
	protected Float period = null;

	/**
	 * The proper motion in R.A. (mas/yr).
	 */
	protected Float prop_ra = null;

	/**
	 * The proper motion in Decl. (mas/yr).
	 */
	protected Float prop_decl = null;

	/**
	 * The spectrum.
	 */
	protected String spectrum = null;

	/**
	 * The parallax in milli-arcsec.
	 */
	protected Float parallax = null;

	/**
	 * The error of R.A. in mas.
	 */
	protected float err_ra = (float)-1;

	/**
	 * The error of Decl. in mas.
	 */
	protected float err_decl = (float)-1;

	/**
	 * The error of BT magnitude.
	 */
	protected float err_bt = (float)-1;

	/**
	 * The error of VT magnitude.
	 */
	protected float err_vt = (float)-1;

	/**
	 * The error of Hp magnitude.
	 */
	protected float err_hp = (float)-1;

	/**
	 * The error of B-V magnitude.
	 */
	protected float err_bv = (float)-1;

	/**
	 * The error of V-I magnitude.
	 */
	protected float err_vi = (float)-1;

	/**
	 * The error of proper motion in R.A. (mas/yr).
	 */
	protected float err_prop_ra = (float)-1;

	/**
	 * The error of proper motion in Decl. (mas/yr).
	 */
	protected float err_prop_decl = (float)-1;

	/**
	 * The error of parallax in milli-arcsec.
	 */
	protected float err_parallax = (float)-1;

	/**
	 * Constructs an empty <code>HipparcosStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public HipparcosStar ( ) {
		super();
		setMag(999.9);
	}

	/**
	 * Constructs a <code>HipparcosStar</code> with data read from the
	 * catalog file.
	 * @param number the number.
	 * @param coor   the R.A. and Decl.
	 */
	public HipparcosStar ( int number,
						   Coor coor )
	{
		super();
		setCoor(coor);
		setMag(999.9);

		this.number = number;
	}

	/**
	 * Sets the V magnitude.
	 * @param mag the V magnitude.
	 */
	public void setVMagnitude ( double mag ) {
		v_mag = (float)mag;

		this.mag = (float)mag;
	}

	/**
	 * Sets the BT magnitude.
	 * @param mag the BT magnitude.
	 */
	public void setBtMagnitude ( double mag ) {
		bt_mag = (float)mag;

		if (v_mag > 30  &&  hp_mag > 30  &&  vt_mag > 30)
			this.mag = (float)mag;
	}

	/**
	 * Sets the VT magnitude.
	 * @param mag the VT magnitude.
	 */
	public void setVtMagnitude ( double mag ) {
		vt_mag = (float)mag;

		if (v_mag > 30  &&  hp_mag > 30)
			this.mag = (float)mag;
	}

	/**
	 * Sets the Hp magnitude.
	 * @param mag the Hp magnitude.
	 */
	public void setHpMagnitude ( double mag ) {
		hp_mag = (float)mag;

		if (v_mag > 30)
			this.mag = (float)mag;
	}

	/**
	 * Sets the B-V magnitude.
	 * @param b_v the B-V magnitude.
	 */
	public void setBVDifference ( double b_v ) {
		this.b_v = (float)b_v;
	}

	/**
	 * Sets the V-I magnitude.
	 * @param v_i the V-I magnitude.
	 */
	public void setVIDifference ( double v_i ) {
		this.v_i = (float)v_i;
	}

	/**
	 * Sets the maximum magnitude.
	 * @param mag the maximum magnitude.
	 */
	public void setMagMax ( double mag ) {
		mag_max = (float)mag;
	}

	/**
	 * Sets the minimum magnitude.
	 * @param mag the minimum magnitude.
	 */
	public void setMagMin ( double mag ) {
		mag_min = (float)mag;
	}

	/**
	 * Sets the period.
	 * @param period the period.
	 */
	public void setPeriod ( double period ) {
		this.period = new Float((float)period);
	}

	/**
	 * Sets the proper motion in R.A. (mas/yr).
	 * @param prop the proper motion in R.A. (mas/yr).
	 */
	public void setProperMotionInRA ( double prop ) {
		prop_ra = new Float((float)prop);
	}

	/**
	 * Sets the proper motion in Decl. (mas/yr).
	 * @param prop the proper motion in Decl. (mas/yr).
	 */
	public void setProperMotionInDecl ( double prop ) {
		prop_decl = new Float((float)prop);
	}

	/**
	 * Sets the spectrum.
	 * @param spectrum the spectrum.
	 */
	public void setSpectrum ( String spectrum ) {
		this.spectrum = spectrum;
	}

	/**
	 * Sets the parallax in milli-arcsec.
	 * @param parallax the parallax in milli-arcsec.
	 */
	public void setParallax ( double parallax ) {
		this.parallax = new Float((float)parallax);
	}

	/**
	 * Sets the error of R.A. in mas.
	 * @param err the error of R.A. in mas.
	 */
	public void setRAError ( double err ) {
		err_ra = (float)err;
	}

	/**
	 * Sets the error of Decl. in mas.
	 * @param err the error of Decl. in mas.
	 */
	public void setDeclError ( double err ) {
		err_decl = (float)err;
	}

	/**
	 * Sets the error of BT magnitude.
	 * @param err the error of BT magnitude.
	 */
	public void setBtMagnitudeError ( double err ) {
		err_bt = (float)err;
	}

	/**
	 * Sets the error of VT magnitude.
	 * @param err the error of VT magnitude.
	 */
	public void setVtMagnitudeError ( double err ) {
		err_vt = (float)err;
	}

	/**
	 * Sets the error of Hp magnitude.
	 * @param err the error of Hp magnitude.
	 */
	public void setHpMagnitudeError ( double err ) {
		err_hp = (float)err;
	}

	/**
	 * Sets the error of B-V magnitude.
	 * @param err the error of B-V magnitude.
	 */
	public void setBVDifferenceError ( double err ) {
		err_bv = (float)err;
	}

	/**
	 * Sets the error of V-I magnitude.
	 * @param err the error of V-I magnitude.
	 */
	public void setVIDifferenceError ( double err ) {
		err_vi = (float)err;
	}

	/**
	 * Sets the error of proper motion in R.A. (mas/yr).
	 * @param err the error of proper motion in R.A. (mas/yr).
	 */
	public void setProperMotionInRAError ( double err ) {
		err_prop_ra = (float)err;
	}

	/**
	 * Sets the error of proper motion in Decl. (mas/yr).
	 * @param err the error of proper motion in Decl. (mas/yr).
	 */
	public void setProperMotionInDeclError ( double err ) {
		err_prop_decl = (float)err;
	}

	/**
	 * Sets the error of parallax in milli-arcsec.
	 * @param err the error of parallax in milli-arcsec.
	 */
	public void setParallaxError ( double err ) {
		this.err_parallax = (float)err;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>HIP 1234</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String name = "HIP " + number;
		return name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>HIP1234</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = "HIP" + number;
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
		return "Hipparcos Catalogue";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "HIP";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "Hipparcos";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "Hipparcos";
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
		return Coor.ACCURACY_100M_ARCSEC;
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
		if (v_mag < 30.0)
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
		double v_mag = getVMagnitude();
		double b_v = getBVDifference();

		return v_mag + b_v;
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
		String[] systems = new String[7];
		systems[0] = "Hp";
		systems[1] = "VT";
		systems[2] = "BT";
		systems[3] = "Ic";
		systems[4] = "Rc";
		systems[5] = "V";
		systems[6] = "B";
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
		if (system.equals("Hp")  &&  (double)hp_mag < 30.0)
			return (double)hp_mag;

		if (system.equals("VT")  &&  (double)vt_mag < 30.0)
			return (double)vt_mag;
		if (system.equals("BT")  &&  (double)bt_mag < 30.0)
			return (double)bt_mag;

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

		if (system.equals("V-I")) {
			if (v_i < 30.0)
				return (double)v_i;
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

		if (system.equals("Hp"))
			return Format.formatDouble(mag, 7, 2).trim();

		if (system.equals("VT")  ||  system.equals("BT"))
			return Format.formatDouble(mag, 6, 2).trim();
		if (system.equals("B-V"))
			return Format.formatDouble(mag, 6, 2).trim();

		return Format.formatDouble(mag, 5, 2).trim();
	}

	/**
	 * Gets the html help message for regular photometry.
	 * @return the html help message for regular photometry.
	 */
	public String getPhotometryHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "Hipparcos Catalogue contains Johnson V magnitude and B-V value.<br>";
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

		if (err_ra >= 0.0)
			l.addElement(new KeyAndValue("Error(R.A.)", Format.formatDouble((double)err_ra / 1000.0, 8, 2).trim() + "\""));
		if (err_decl >= 0.0)
			l.addElement(new KeyAndValue("Error(Decl.)", Format.formatDouble((double)err_decl / 1000.0, 8, 2).trim() + "\""));

		if (prop_ra != null)
			l.addElement(new KeyAndValue("ProperMotion(R.A.)", Format.formatDouble((double)prop_ra.floatValue() / 1000.0, 8, 2).trim() + "\"/year"));
		if (prop_decl != null)
			l.addElement(new KeyAndValue("ProperMotion(Decl.)", Format.formatDouble((double)prop_decl.floatValue() / 1000.0, 8, 2).trim() + "\"/year"));

		if (err_prop_ra >= 0.0)
			l.addElement(new KeyAndValue("ProperMotionError(R.A.)", Format.formatDouble((double)err_prop_ra / 1000.0, 7, 1).trim() + "\""));
		if (err_prop_decl >= 0.0)
			l.addElement(new KeyAndValue("ProperMotionError(Decl.)", Format.formatDouble((double)err_prop_decl / 1000.0, 7, 1).trim() + "\""));

		if (parallax != null)
			l.addElement(new KeyAndValue("Parallax", Format.formatDouble((double)parallax.floatValue() / 1000.0, 7, 1).trim() + "\""));

		if (err_parallax >= 0.0)
			l.addElement(new KeyAndValue("ParallaxError", Format.formatDouble((double)err_parallax / 1000.0, 7, 1).trim() + "\""));

		try {
			l.addElement(new KeyAndValue("Mag(Hp)", getMagnitudeString("Hp")));
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
			l.addElement(new KeyAndValue("B-V", getMagnitudeString("B-V")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}
		try {
			l.addElement(new KeyAndValue("V-I", getMagnitudeString("V-I")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}

		if (err_hp < 30.0)
			l.addElement(new KeyAndValue("MagError(Hp)", Format.formatDouble((double)err_hp, 7, 2).trim()));
		if (err_vt < 30.0)
			l.addElement(new KeyAndValue("MagError(VT)", Format.formatDouble((double)err_vt, 6, 2).trim()));
		if (err_bt < 30.0)
			l.addElement(new KeyAndValue("MagError(BT)", Format.formatDouble((double)err_bt, 6, 2).trim()));

		if (err_bv < 30.0)
			l.addElement(new KeyAndValue("MagError(B-V)", Format.formatDouble((double)err_bv, 6, 2).trim()));
		if (err_vi < 30.0)
			l.addElement(new KeyAndValue("MagError(V-I)", Format.formatDouble((double)err_vi, 5, 2).trim()));

		if (spectrum != null)
			l.addElement(new KeyAndValue("Spectrum", spectrum));

		if (mag_max < 30.0)
			l.addElement(new KeyAndValue("Mag(max)", Format.formatDouble((double)mag_max, 5, 2).trim()));
		if (mag_min < 30.0)
			l.addElement(new KeyAndValue("Mag(min)", Format.formatDouble((double)mag_min, 5, 2).trim()));
		if (period != null)
			l.addElement(new KeyAndValue("Period", Format.formatDouble((double)period.floatValue(), 7, 4).trim()));

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
		if (key_and_value.getKey().equals("Error(R.A.)")) {
			int p = key_and_value.getValue().indexOf('"');
			String s = key_and_value.getValue().substring(0, p);
			err_ra = (float)(Format.doubleValueOf(s) * 1000.0);
		} else if (key_and_value.getKey().equals("Error(Decl.)")) {
			int p = key_and_value.getValue().indexOf('"');
			String s = key_and_value.getValue().substring(0, p);
			err_decl = (float)(Format.doubleValueOf(s) * 1000.0);
		} else if (key_and_value.getKey().equals("ProperMotion(R.A.)")) {
			int p = key_and_value.getValue().indexOf('"');
			String s = key_and_value.getValue().substring(0, p);
			prop_ra = new Float((float)(Format.doubleValueOf(s) * 1000.0));
		} else if (key_and_value.getKey().equals("ProperMotion(Decl.)")) {
			int p = key_and_value.getValue().indexOf('"');
			String s = key_and_value.getValue().substring(0, p);
			prop_decl = new Float((float)(Format.doubleValueOf(s) * 1000.0));
		} else if (key_and_value.getKey().equals("ProperMotionError(R.A.)")) {
			int p = key_and_value.getValue().indexOf('"');
			String s = key_and_value.getValue().substring(0, p);
			err_prop_ra = (float)(Format.doubleValueOf(s) * 1000.0);
		} else if (key_and_value.getKey().equals("ProperMotionError(Decl.)")) {
			int p = key_and_value.getValue().indexOf('"');
			String s = key_and_value.getValue().substring(0, p);
			err_prop_decl = (float)(Format.doubleValueOf(s) * 1000.0);
		} else if (key_and_value.getKey().equals("Parallax")) {
			int p = key_and_value.getValue().indexOf('"');
			String s = key_and_value.getValue().substring(0, p);
			parallax = new Float((float)(Format.doubleValueOf(s) * 1000.0));
		} else if (key_and_value.getKey().equals("ParallaxError")) {
			int p = key_and_value.getValue().indexOf('"');
			String s = key_and_value.getValue().substring(0, p);
			err_parallax = (float)(Format.doubleValueOf(s) * 1000.0);
		} else if (key_and_value.getKey().equals("Mag(Hp)")) {
			setHpMagnitude(Format.doubleValueOf(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("Mag(VT)")) {
			setVtMagnitude(Format.doubleValueOf(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("Mag(BT)")) {
			setBtMagnitude(Format.doubleValueOf(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("Mag(V)")) {
			setVMagnitude(Format.doubleValueOf(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("B-V")) {
			setBVDifference(Format.doubleValueOf(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("V-I")) {
			setVIDifference(Format.doubleValueOf(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("MagError(Hp)")) {
			err_hp = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("MagError(VT)")) {
			err_vt = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("MagError(BT)")) {
			err_bt = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("MagError(B-V)")) {
			err_bv = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("MagError(V-I)")) {
			err_vi = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("Spectrum")) {
			spectrum = key_and_value.getValue();
		} else if (key_and_value.getKey().equals("Mag(max)")) {
			mag_max = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("Mag(min)")) {
			mag_min = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("Period")) {
			period = new Float((float)(Format.doubleValueOf(key_and_value.getValue())));
		}
	}

	/**
	 * Gets an array of keys and values related to the photometry.
	 * @return an array of keys and values related to the photometry.
	 */
	public KeyAndValue[] getKeyAndValuesForPhotometry ( ) {
		KeyAndValue[] key_and_values = new KeyAndValue[9];

		try {
			key_and_values[0] = new KeyAndValue("Hp", getMagnitudeString("Hp"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[0] = new KeyAndValue("Hp", "");
		}

		try {
			key_and_values[1] = new KeyAndValue("VT", getMagnitudeString("VT"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[1] = new KeyAndValue("VT", "");
		}

		try {
			key_and_values[2] = new KeyAndValue("BT", getMagnitudeString("BT"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[2] = new KeyAndValue("BT", "");
		}

		try {
			key_and_values[3] = new KeyAndValue("Ic", getMagnitudeString("Ic"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[3] = new KeyAndValue("Ic", "");
		}

		try {
			key_and_values[4] = new KeyAndValue("Rc", getMagnitudeString("Rc"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[4] = new KeyAndValue("Rc", "");
		}

		try {
			key_and_values[5] = new KeyAndValue("V", getMagnitudeString("V"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[5] = new KeyAndValue("V", "");
		}

		try {
			key_and_values[6] = new KeyAndValue("B", getMagnitudeString("B"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[6] = new KeyAndValue("B", "");
		}

		try {
			key_and_values[7] = new KeyAndValue("B-V", getMagnitudeString("B-V"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[7] = new KeyAndValue("B-V", "");
		}

		try {
			key_and_values[8] = new KeyAndValue("V-I", getMagnitudeString("V-I"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[8] = new KeyAndValue("V-I", "");
		}

		return key_and_values;
	}
}
