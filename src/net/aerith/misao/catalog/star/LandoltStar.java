/*
 * @(#)LandoltStar.java
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
 * The <code>LandoltStar</code> represents a star data in the Landolt 
 * UBVRI Photometric Standard Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 November 27
 */

public class LandoltStar extends CatalogStar {
	/**
	 * The name.
	 */
	protected String name;

	/**
	 * The B-V magnitude.
	 */
	protected float b_v = (float)999.9;

	/**
	 * The U-B magnitude.
	 */
	protected float u_b = (float)999.9;

	/**
	 * The V-R magnitude.
	 */
	protected float v_r = (float)999.9;

	/**
	 * The R-I magnitude.
	 */
	protected float r_i = (float)999.9;

	/**
	 * The V-I magnitude.
	 */
	protected float v_i = (float)999.9;

	/**
	 * The error of V magnitude.
	 */
	protected float v_err = (float)999.9;

	/**
	 * The error of B-V magnitude.
	 */
	protected float bv_err = (float)999.9;

	/**
	 * The error of U-B magnitude.
	 */
	protected float ub_err = (float)999.9;

	/**
	 * The error of V-R magnitude.
	 */
	protected float vr_err = (float)999.9;

	/**
	 * The error of R-I magnitude.
	 */
	protected float ri_err = (float)999.9;

	/**
	 * The error of V-I magnitude.
	 */
	protected float vi_err = (float)999.9;

	/**
	 * Constructs an empty <code>LandoltStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public LandoltStar ( ) {
		super();
		setMag(999.9);
	}

	/**
	 * Constructs a <code>LandoltStar</code> with data read from the
	 * catalog file.
	 * @param name   the name.
	 * @param coor   the R.A. and Decl.
	 * @param v_mag  the V magnitude.
	 * @param b_v    the B-V magnitude.
	 * @param u_b    the U-B magnitude.
	 * @param v_r    the V-R magnitude.
	 * @param r_i    the R-I magnitude.
	 * @param v_i    the V-I magnitude.
	 * @param v_err  the error of V magnitude.
	 * @param bv_err the error of B-V magnitude.
	 * @param ub_err the error of U-B magnitude.
	 * @param vr_err the error of V-R magnitude.
	 * @param ri_err the error of R-I magnitude.
	 * @param vi_err the error of V-I magnitude.
	 */
	public LandoltStar ( String name,
						 Coor coor,
						 double v_mag, 
						 double b_v,
						 double u_b,
						 double v_r,
						 double r_i,
						 double v_i,
						 double v_err,
						 double bv_err,
						 double ub_err,
						 double vr_err,
						 double ri_err,
						 double vi_err )
	{
		super();
		setCoor(coor);
		setMag(v_mag);

		this.name = name;
		this.b_v = (float)b_v;
		this.u_b = (float)u_b;
		this.v_r = (float)v_r;
		this.r_i = (float)r_i;
		this.v_i = (float)v_i;
		this.v_err = (float)v_err;
		this.bv_err = (float)bv_err;
		this.ub_err = (float)ub_err;
		this.vr_err = (float)vr_err;
		this.ri_err = (float)ri_err;
		this.vi_err = (float)vi_err;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>BD -11 162</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>BD-11.162</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		StringTokenizer st = new StringTokenizer(name);
		String s = st.nextToken() + st.nextToken();
		while (st.hasMoreElements())
			s += "." + st.nextToken();
		return s;
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
		return "Landolt UBVRI Photometric Standard Stars";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "Landolt";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "Landolt";
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

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return getVsnetName();
	}

	/**
	 * Gets the accuracy of R.A. and Decl.
	 * @return the accuracy of R.A. and Decl.
	 */
	public byte getCoorAccuracy ( ) {
		return Coor.ACCURACY_ROUGH_ARCSEC;
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
		return getMag() + (double)b_v + (double)u_b;
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
		return getMag() + (double)b_v;
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
		return getMag() - (double)v_r;
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
		return getMag() - (double)v_i;
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
		return (double)b_v;
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

		if (system.equals("U-B"))
			return (double)u_b;
		if (system.equals("V-R"))
			return (double)v_r;
		if (system.equals("R-I"))
			return (double)r_i;
		if (system.equals("V-I"))
			return (double)v_i;

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
		return Format.formatDouble(mag, 6, 2).trim();
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
			l.addElement(new KeyAndValue("V-R", getMagnitudeString("V-R")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}
		try {
			l.addElement(new KeyAndValue("R-I", getMagnitudeString("R-I")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}
		try {
			l.addElement(new KeyAndValue("V-I", getMagnitudeString("V-I")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}

		l.addElement(new KeyAndValue("MagError(V)", Format.formatDouble((double)v_err, 6, 1).trim()));
		l.addElement(new KeyAndValue("MagError(B-V)", Format.formatDouble((double)bv_err, 6, 1).trim()));
		l.addElement(new KeyAndValue("MagError(U-B)", Format.formatDouble((double)ub_err, 6, 1).trim()));
		l.addElement(new KeyAndValue("MagError(V-R)", Format.formatDouble((double)vr_err, 6, 1).trim()));
		l.addElement(new KeyAndValue("MagError(R-I)", Format.formatDouble((double)ri_err, 6, 1).trim()));
		l.addElement(new KeyAndValue("MagError(V-I)", Format.formatDouble((double)vi_err, 6, 1).trim()));

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
			b_v = Float.parseFloat(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("U-B")) {
			u_b = Float.parseFloat(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("V-R")) {
			v_r = Float.parseFloat(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("R-I")) {
			r_i = Float.parseFloat(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("V-I")) {
			v_i = Float.parseFloat(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("MagError(V)")) {
			v_err = Float.parseFloat(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("MagError(B-V)")) {
			bv_err = Float.parseFloat(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("MagError(U-B)")) {
			ub_err = Float.parseFloat(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("MagError(V-R)")) {
			vr_err = Float.parseFloat(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("MagError(R-I)")) {
			ri_err = Float.parseFloat(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("MagError(V-I)")) {
			vi_err = Float.parseFloat(key_and_value.getValue());
		}
	}

	/**
	 * Gets an array of keys and values related to the photometry.
	 * @return an array of keys and values related to the photometry.
	 */
	public KeyAndValue[] getKeyAndValuesForPhotometry ( ) {
		KeyAndValue[] key_and_values = new KeyAndValue[10];

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
			key_and_values[7] = new KeyAndValue("V-R", getMagnitudeString("V-R"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[7] = new KeyAndValue("V-R", "");
		}

		try {
			key_and_values[8] = new KeyAndValue("R-I", getMagnitudeString("R-I"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[8] = new KeyAndValue("R-I", "");
		}

		try {
			key_and_values[9] = new KeyAndValue("V-I", getMagnitudeString("V-I"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[9] = new KeyAndValue("V-I", "");
		}

		return key_and_values;
	}
}
