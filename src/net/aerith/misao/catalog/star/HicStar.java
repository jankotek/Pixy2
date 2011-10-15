/*
 * @(#)HicStar.java
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
 * The <code>HicStar</code> represents a star data in the Hipparcos 
 * Input Catalogue.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class HicStar extends CatalogStar {
	/**
	 * The star number.
	 */
	protected int number;

	/**
	 * The V magnitude.
	 */
	protected float v_mag = (float)999.9;

	/**
	 * The B-V magnitude.
	 */
	protected float b_v = (float)999.9;

	/**
	 * The accuracy of Hp magnitude.
	 */
	protected byte hp_accuracy = ACCURACY_100TH;

	/**
	 * The accuracy of V magnitude.
	 */
	protected byte v_accuracy = ACCURACY_100TH;

	/**
	 * The accuracy of B-V magnitude.
	 */
	protected byte bv_accuracy = ACCURACY_100TH;

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
	 * The parallax in milli-arcsec.
	 */
	protected Integer parallax = null;

	/**
	 * The number of accuracy, which represents 0.1 mag.
	 */
	public final static byte ACCURACY_10TH = 0;

	/**
	 * The number of accuracy, which represents 0.01 mag.
	 */
	public final static byte ACCURACY_100TH = 1;

	/**
	 * The number of accuracy, which represents 0.001 mag.
	 */
	public final static byte ACCURACY_1000TH = 2;

	/**
	 * Constructs an empty <code>HicStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public HicStar ( ) {
		super();
		setMag(999.9);
	}

	/**
	 * Constructs a <code>HicStar</code> with data read from the
	 * catalog file.
	 * @param number      the number.
	 * @param coor        the R.A. and Decl.
	 * @param hp_mag      the Hp magnitude.
	 * @param hp_accuracy the accuracy of Hp magnitude.
	 */
	public HicStar ( int number,
					 Coor coor,
					 double hp_mag, 
					 byte hp_accuracy )
	{
		super();
		setCoor(coor);
		setMag(hp_mag);

		this.number = number;
		this.hp_accuracy = hp_accuracy;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>HIC 1234</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String name = "HIC " + number;
		return name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>HIC1234</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = "HIC" + number;
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
		return "Hipparcos Input Catalogue";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "HIC";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "HIC";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "HIC";
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
		if ((double)v_mag < 99.9  &&  (double)b_v < 99.9)
			return (double)v_mag + (double)b_v;

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
		if ((double)b_v < 99.9)
			return (double)b_v;

		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Sets the V magnitude.
	 * @param v_mag    the V magnitude.
	 * @param accuracy the accuracy of V magnitude.
	 */
	public void setVMagnitude ( double v_mag, byte accuracy ) {
		this.v_mag = (float)v_mag;
		this.v_accuracy = accuracy;
	}

	/**
	 * Sets the difference between the V and B magnitude.
	 * @param b_v      the difference between the V and B magnitude.
	 * @param accuracy the accuracy of V magnitude.
	 */
	public void setBVDifference ( double b_v, byte accuracy ) {
		this.b_v = (float)b_v;
		this.bv_accuracy = accuracy;
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
	 * Sets the parallax in milli-arcsec.
	 * @param parallax the parallax in milli-arcsec.
	 */
	public void setParallax ( int parallax ) {
		this.parallax = new Integer(parallax);

	}

	/**
	 * Gets the list of magnitude systems supported by this catalog.
	 * @return the list of magnitude systems supported by this catalog.
	 */
	public String[] getAvailableMagnitudeSystems ( ) {
		String[] systems = new String[5];
		systems[0] = "Hp";
		systems[1] = "Ic";
		systems[2] = "Rc";
		systems[3] = "Johnson V";
		systems[4] = "Johnson B";
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
		if (system.equals("Hp"))
			return getMag();

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
		byte accuracy = ACCURACY_100TH;

		if (system.equals("Hp")) {
			accuracy = hp_accuracy;
		} else if (system.equals("Johnson V")  ||  system.equals("V")) {
			accuracy = v_accuracy;
		} else if (system.equals("B-V")) {
			accuracy = bv_accuracy;
		} else {
			accuracy = v_accuracy;
			if (accuracy > bv_accuracy)
				accuracy = bv_accuracy;
		}

		if (accuracy == ACCURACY_1000TH)
			return Format.formatDouble(mag, 6, 2).trim();
		if (accuracy == ACCURACY_100TH)
			return Format.formatDouble(mag, 5, 2).trim();
		if (accuracy == ACCURACY_10TH)
			return Format.formatDouble(mag, 4, 2).trim();

		return Format.formatDouble(mag, 5, 2).trim();
	}

	/**
	 * Gets the html help message for regular photometry.
	 * @return the html help message for regular photometry.
	 */
	public String getPhotometryHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "The Hipparcos Input Catalogue contains Hp magnitude,<br>";
		html += "and V magnitude and B-V value.<br>";
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
			l.addElement(new KeyAndValue("Mag(Hp)", getMagnitudeString("Hp")));
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

		l.addElement(new KeyAndValue("Spectrum", spectrum));

		if (prop_ra != null)
			l.addElement(new KeyAndValue("ProperMotion(R.A.)", Format.formatDouble((double)prop_ra.floatValue(), 6, 2).trim() + "\"/year"));
		if (prop_decl != null)
			l.addElement(new KeyAndValue("ProperMotion(Decl.)", Format.formatDouble((double)prop_decl.floatValue(), 6, 2).trim() + "\"/year"));

		if (parallax != null)
			l.addElement(new KeyAndValue("Parallax", Format.formatDouble((double)parallax.intValue() / 1000.0, 6, 2).trim() + "\""));

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
		if (key_and_value.getKey().equals("Mag(Hp)")) {
			setMag(Double.parseDouble(key_and_value.getValue()));
			int p = key_and_value.getValue().indexOf('.');
			if (key_and_value.getValue().length() - p == 2)
				hp_accuracy = ACCURACY_100TH;
			else
				hp_accuracy = ACCURACY_10TH;
		} else if (key_and_value.getKey().equals("Mag(V)")) {
			v_mag = Float.parseFloat(key_and_value.getValue());
			int p = key_and_value.getValue().indexOf('.');
			if (key_and_value.getValue().length() - p == 3)
				v_accuracy = ACCURACY_1000TH;
			else if (key_and_value.getValue().length() - p == 2)
				v_accuracy = ACCURACY_100TH;
			else
				v_accuracy = ACCURACY_10TH;
		} else if (key_and_value.getKey().equals("B-V")) {
			b_v = Float.parseFloat(key_and_value.getValue());
			int p = key_and_value.getValue().indexOf('.');
			if (key_and_value.getValue().length() - p == 3)
				bv_accuracy = ACCURACY_1000TH;
			else if (key_and_value.getValue().length() - p == 2)
				bv_accuracy = ACCURACY_100TH;
			else
				bv_accuracy = ACCURACY_10TH;
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
		} else if (key_and_value.getKey().equals("Parallax")) {
			int p = key_and_value.getValue().indexOf('"');
			String s = key_and_value.getValue().substring(0, p);
			double f = Format.doubleValueOf(s) * 1000.0;
			s = Format.formatDouble(f, 5, 5).trim();
			parallax = new Integer(Integer.parseInt(s));
		}
	}

	/**
	 * Gets an array of keys and values related to the photometry.
	 * @return an array of keys and values related to the photometry.
	 */
	public KeyAndValue[] getKeyAndValuesForPhotometry ( ) {
		KeyAndValue[] key_and_values = new KeyAndValue[6];

		try {
			key_and_values[0] = new KeyAndValue("Hp", getMagnitudeString("Hp"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[0] = new KeyAndValue("Hp", "");
		}

		try {
			key_and_values[1] = new KeyAndValue("Ic", getMagnitudeString("Ic"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[1] = new KeyAndValue("Ic", "");
		}

		try {
			key_and_values[2] = new KeyAndValue("Rc", getMagnitudeString("Rc"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[2] = new KeyAndValue("Rc", "");
		}

		try {
			key_and_values[3] = new KeyAndValue("Johnson V", getMagnitudeString("Johnson V"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[3] = new KeyAndValue("Johnson V", "");
		}

		try {
			key_and_values[4] = new KeyAndValue("Johnson B", getMagnitudeString("Johnson B"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[4] = new KeyAndValue("Johnson B", "");
		}

		try {
			key_and_values[5] = new KeyAndValue("B-V", getMagnitudeString("B-V"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[5] = new KeyAndValue("B-V", "");
		}

		return key_and_values;
	}
}
