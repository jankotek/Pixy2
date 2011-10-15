/*
 * @(#)Tycho2Star.java
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
 * The <code>Tycho2Star</code> represents a star data in the Tycho-2
 * Catalogue file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Tycho2Star extends CatalogStar {
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
	 * The proper motion in R.A. (mas/yr).
	 */
	protected Float prop_ra = null;

	/**
	 * The proper motion in Decl. (mas/yr).
	 */
	protected Float prop_decl = null;

	/**
	 * The error of R.A. in mas.
	 */
	protected float err_ra = (float)-1;

	/**
	 * The error of Decl. in mas.
	 */
	protected float err_decl = (float)-1;

	/**
	 * The mean epoch of R.A. in year.
	 */
	protected float epoch_ra = (float)-1;

	/**
	 * The mean epoch of Decl. in year.
	 */
	protected float epoch_decl = (float)-1;

	/**
	 * The BT magnitude.
	 */
	protected float bt_mag = (float)999.9;

	/**
	 * The VT magnitude.
	 */
	protected float vt_mag = (float)999.9;

	/**
	 * The error of BT magnitude.
	 */
	protected float err_bt = (float)-1;

	/**
	 * The error of VT magnitude.
	 */
	protected float err_vt = (float)-1;

	/**
	 * Constructs an empty <code>Tycho2Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Tycho2Star ( ) {
		super();
		setMag(999.9);
	}

	/**
	 * Constructs a <code>Tycho2Star</code> with data read from the
	 * catalog file.
	 * @param tyc1 the TYC1 number.
	 * @param tyc2 the TYC2 number.
	 * @param tyc3 the TYC3 number.
	 * @param coor the R.A. and Decl.
	 */
	public Tycho2Star ( short tyc1,
						short tyc2,
						short tyc3,
						Coor coor )
	{
		super();
		setCoor(coor);
		setMag(999.9);

		this.tyc1 = tyc1;
		this.tyc2 = tyc2;
		this.tyc3 = tyc3;
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
	 * Sets the mean epoch of R.A. in year.
	 * @param epoch the mean epoch of R.A. in year.
	 */
	public void setEpochInRA ( double epoch ) {
		epoch_ra = (float)epoch;
	}

	/**
	 * Sets the mean epoch of Decl. in year.
	 * @param epoch the mean epoch of Decl. in year.
	 */
	public void setEpochInDecl ( double epoch ) {
		epoch_decl = (float)epoch;
	}

	/**
	 * Sets the BT magnitude.
	 * @param mag the BT magnitude.
	 */
	public void setBtMagnitude ( double mag ) {
		bt_mag = (float)mag;

		if (this.mag > 30)
			this.mag = bt_mag;
	}

	/**
	 * Sets the VT magnitude.
	 * @param mag the VT magnitude.
	 */
	public void setVtMagnitude ( double mag ) {
		vt_mag = (float)mag;

		this.mag = vt_mag;
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
		return "Tycho-2 Catalogue";
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
		return "Tycho-2";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "Tycho-2";
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
		if (vt_mag < 30.0  &&  bt_mag < 30.0)
			return (double)vt_mag - 0.090 * (double)(bt_mag - vt_mag);

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
		if (vt_mag < 30.0  &&  bt_mag < 30.0)
			return 0.850 * (double)(bt_mag - vt_mag);

		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the error of BT magnitude.
	 * @return the error of BT magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no error
	 * of BT mag is recorded in this catalog.
	 */
	public double getBtMagnitudeError ( )
		throws UnsupportedMagnitudeSystemException
	{
		if (err_bt < 30.0)
			return (double)err_bt;

		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the error of VT magnitude.
	 * @return the error of VT magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no error
	 * of VT mag is recorded in this catalog.
	 */
	public double getVtMagnitudeError ( )
		throws UnsupportedMagnitudeSystemException
	{
		if (err_vt < 30.0)
			return (double)err_vt;

		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the list of magnitude systems supported by this catalog.
	 * @return the list of magnitude systems supported by this catalog.
	 */
	public String[] getAvailableMagnitudeSystems ( ) {
		String[] systems = new String[6];
		systems[0] = "VT";
		systems[1] = "BT";
		systems[2] = "Ic";
		systems[3] = "Rc";
		systems[4] = "V";
		systems[5] = "B";
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
	 * Gets the html help message for regular photometry.
	 * @return the html help message for regular photometry.
	 */
	public String getPhotometryHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "Tycho Catalogue contains VT magnitude and BT magnitude.<br>";
		html += "</p><p>";
		html += "The Johnson V magnitude and the B-V value is converted from<br>";
		html += "the VT and BT magnitude by the following formula.<br>";
		html += "<pre>";
		html += "    V = VT - 0.090 * (BT - VT)<br>";
		html += "    B-V = 0.850 * (BT - VT)<br>";
		html += "</pre>";
		html += "</p><p>";
		html += "The Rc magnitude is calculated from the converted V magnitude and<br>";
		html += "the converted B-V value by the following formula.<br>";
		html += "<pre>";
		html += "    Rc = V - 0.5 * (B-V)<br>";
		html += "</pre>";
		html += "</p><p>";
		html += "The Ic magnitude is calculated from the converted B magnitude and<br>";
		html += "the converted B-V value by the following formula.<br>";
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
		if (name.indexOf("(Ic)") >= 0  ||  name.indexOf("(Rc)") >= 0  ||  name.indexOf("(V)") >= 0  ||  name.indexOf("(B)") >= 0) {
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
			l.addElement(new KeyAndValue("Error(R.A.)", Format.formatDouble((double)err_ra / 1000.0, 5, 1).trim() + "\""));
		if (err_decl >= 0.0)
			l.addElement(new KeyAndValue("Error(Decl.)", Format.formatDouble((double)err_decl / 1000.0, 5, 1).trim() + "\""));

		if (prop_ra != null)
			l.addElement(new KeyAndValue("ProperMotion(R.A.)", Format.formatDouble((double)prop_ra.floatValue() / 1000.0, 8, 3).trim() + "\"/year"));
		if (prop_decl != null)
			l.addElement(new KeyAndValue("ProperMotion(Decl.)", Format.formatDouble((double)prop_decl.floatValue() / 1000.0, 8, 3).trim() + "\"/year"));

		if (epoch_ra > 0.0)
			l.addElement(new KeyAndValue("Epoch(R.A.)", Format.formatDouble((double)epoch_ra, 7, 4).trim()));
		if (epoch_decl > 0.0)
			l.addElement(new KeyAndValue("Epoch(Decl.)", Format.formatDouble((double)epoch_decl, 7, 4).trim()));

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

		if (err_vt < 30.0)
			l.addElement(new KeyAndValue("MagError(VT)", Format.formatDouble((double)err_vt, 6, 2).trim()));
		if (err_bt < 30.0)
			l.addElement(new KeyAndValue("MagError(BT)", Format.formatDouble((double)err_bt, 6, 2).trim()));

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
		} else if (key_and_value.getKey().equals("Epoch(R.A.)")) {
			epoch_ra = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("Epoch(Decl.)")) {
			epoch_decl = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("Mag(BT)")) {
			setBtMagnitude(Format.doubleValueOf(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("Mag(VT)")) {
			setVtMagnitude(Format.doubleValueOf(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("MagError(BT)")) {
			err_bt = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("MagError(VT)")) {
			err_vt = (float)Format.doubleValueOf(key_and_value.getValue());
		}
	}

	/**
	 * Gets an array of keys and values related to the photometry.
	 * @return an array of keys and values related to the photometry.
	 */
	public KeyAndValue[] getKeyAndValuesForPhotometry ( ) {
		KeyAndValue[] key_and_values = new KeyAndValue[7];

		try {
			key_and_values[0] = new KeyAndValue("VT", getMagnitudeString("VT"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[0] = new KeyAndValue("VT", "");
		}

		try {
			key_and_values[1] = new KeyAndValue("BT", getMagnitudeString("BT"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[1] = new KeyAndValue("BT", "");
		}

		try {
			key_and_values[2] = new KeyAndValue("Ic", getMagnitudeString("Ic"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[2] = new KeyAndValue("Ic", "");
		}

		try {
			key_and_values[3] = new KeyAndValue("Rc", getMagnitudeString("Rc"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[3] = new KeyAndValue("Rc", "");
		}

		try {
			key_and_values[4] = new KeyAndValue("V", getMagnitudeString("V"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[4] = new KeyAndValue("V", "");
		}

		try {
			key_and_values[5] = new KeyAndValue("B", getMagnitudeString("B"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[5] = new KeyAndValue("B", "");
		}

		try {
			key_and_values[6] = new KeyAndValue("B-V", getMagnitudeString("B-V"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[6] = new KeyAndValue("B-V", "");
		}

		return key_and_values;
	}
}
