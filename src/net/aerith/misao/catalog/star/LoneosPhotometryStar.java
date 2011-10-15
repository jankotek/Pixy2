/*
 * @(#)LoneosPhotometryStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.star;
import java.io.*;
import java.util.*;
import java.awt.Color;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.PlotProperty;

/**
 * The <code>LoneosPhotometryStar</code> represents a star data in the
 * LONEOS Photometry File.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 19
 */

public class LoneosPhotometryStar extends CatalogStar {
	/**
	 * The star name.
	 */
	protected String name;

	/**
	 * The accuracy of R.A. and Decl.
	 */
	protected byte coor_accuracy = Coor.ACCURACY_100M_ARCSEC;

	/**
	 * The B-V magnitude.
	 */
	protected float b_v = (float)999.9;

	/**
	 * The V-R magnitude.
	 */
	protected float v_r = (float)999.9;

	/**
	 * The V-I magnitude.
	 */
	protected float v_i = (float)999.9;

	/**
	 * The accuracy of magnitude.
	 */
	protected int accuracy = ACCURACY_100TH;

	/**
	 * The number of accuracy, which represents 0.01 mag.
	 */
	public final static int ACCURACY_100TH = 0;

	/**
	 * The number of accuracy, which represents 0.001 mag.
	 */
	public final static int ACCURACY_1000TH = 1;

	/**
	 * Constructs an empty <code>LoneosPhotometryStar</code>. It is 
	 * used in <code>StarClass#newInstance</code> to review the XML 
	 * data.
	 */
	public LoneosPhotometryStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>LoneosPhotometryStar</code> with data read 
	 * from the catalog file.
	 * @param name        the name.
	 * @param coor_string the string which represents R.A. and Decl.
	 * @param v_mag       the V magnitude.
	 * @param accuracy    the accuracy of magnitude.
	 */
	public LoneosPhotometryStar ( String name,
								  String coor_string, 
								  double v_mag,
								  int accuracy )
	{
		super();

		setCoor(Coor.create(coor_string));
		setMag(v_mag);

		this.name = name;
		this.coor_accuracy = Coor.getAccuracy(coor_string);
		this.accuracy = accuracy;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>AB CD/123.J123456.7+123456</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String ra_decl = "";
		if (coor_accuracy == Coor.ACCURACY_100M_ARCSEC)
			ra_decl = getCoor().getOutputStringTo100mArcsecWithoutSpace();
		else
			ra_decl = getCoor().getOutputStringToArcsecWithoutSpace();

		StringTokenizer st = new StringTokenizer(ra_decl);
		ra_decl = "";
		while (st.hasMoreElements())
			ra_decl = ra_decl + st.nextToken();

		return name + ".J" + ra_decl;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>AB_CD-123.J123456.7+123456</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = getName();
		return name.replace(' ', '_').replace('"', '-').replace('|', '-').replace('*', '-').replace('?', '-').replace('<', '-').replace('>', '-').replace('/', '-').replace('\\', '-').replace(':', '-');
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		int p = name.lastIndexOf(".J");
		this.name = name.substring(0, p);
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "LONEOS Photometry File";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "loneos.phot";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "loneos.phot";
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

		String folder_name = name.toUpperCase().replace('"', '-').replace('|', '-').replace('*', '-').replace('?', '-').replace('<', '-').replace('>', '-').replace('/', '-').replace('\\', '-').replace(':', '-');
		int p = folder_name.indexOf(' ');

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(CatalogManager.CATEGORY_STAR));
		folder_list.addElement(getCatalogFolderCode());

		folder_list.addElement(folder_name.substring(0, 1));
		if (p >= 0)
			folder_list.addElement(folder_name.substring(0, p));
		else
			folder_list.addElement(folder_name);

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		String ra_decl = "";
		if (coor_accuracy == Coor.ACCURACY_100M_ARCSEC)
			ra_decl = getCoor().getOutputStringTo100mArcsecWithoutSpace();
		else
			ra_decl = getCoor().getOutputStringToArcsecWithoutSpace();

		StringTokenizer st = new StringTokenizer(ra_decl);
		ra_decl = "";
		while (st.hasMoreElements())
			ra_decl = ra_decl + st.nextToken();

		return ra_decl;
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
	 * Gets the B magnitude.
	 * @return the B magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no B mag data
	 * is recorded in this catalog.
	 */
	public double getBMagnitude ( )
		throws UnsupportedMagnitudeSystemException
	{
		if (b_v < 100.0)
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
		if (v_r < 100.0)
			return getMag() - (double)v_r;

		if (0.3 < b_v  &&  b_v < 0.9)
			return getMag() - 0.508 * (double)b_v - 0.040;

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
		if (v_i < 100.0)
			return getMag() - (double)v_i;

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
		if (b_v < 100.0)
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
	 * Sets the difference between the Rc and V magnitude.
	 * @param v_r the difference between the Rc and V magnitude.
	 */
	public void setVRcDifference ( double v_r ) {
		this.v_r = (float)v_r;
	}

	/**
	 * Sets the difference between the Ic and V magnitude.
	 * @param v_i the difference between the Ic and V magnitude.
	 */
	public void setVIcDifference ( double v_i ) {
		this.v_i = (float)v_i;
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

		if (system.equals("V-R")) {
			if (v_r < 100.0)
				return (double)v_r;
		}
		if (system.equals("V-I")) {
			if (v_i < 100.0)
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

		if (accuracy == ACCURACY_1000TH) {
			if (system.equals("Johnson V")  ||  system.equals("V")  ||
				((system.equals("Johnson B")  ||  system.equals("B"))  &&  b_v < 100.0)  ||
				(system.equals("Ic")  &&  v_i < 100.0)  ||
				(system.equals("Rc")  &&  v_r < 100.0)  ||
				(system.equals("B-V")  &&  b_v < 100.0)  ||
				(system.equals("V-R")  &&  v_r < 100.0)  ||
				(system.equals("V-I")  &&  v_i < 100.0)) {
				return Format.formatDouble(mag, 6, 2).trim();
			}
		}

		return Format.formatDouble(mag, 5, 2).trim();
	}

	/**
	 * Gets the html help message for regular photometry.
	 * @return the html help message for regular photometry.
	 */
	public String getPhotometryHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "The LONEOS Photometry File contains Johnson V magnitude for all,<br>";
		html += "and B-V, V-Rc, V-Ic value for some.<br>";
		html += "</p><p>";
		html += "The Rc magnitude is calculated from the V magnitude and B-V value<br>";
		html += "if V-Rc value is not recorded.<br>";
		html += "For the range 0.3 &lt; B-V &lt; 0.9, by the following formula.<br>";
		html += "<pre>";
		html += "    Rc = V - 0.508 * (B-V) - 0.040<br>";
		html += "</pre>";
		html += "Or, for the rest by the following formula.<br>";
		html += "<pre>";
		html += "    Rc = V - 0.5 * (B-V)<br>";
		html += "</pre>";
		html += "</p><p>";
		html += "The Ic magnitude is calculated from the B magnitude and B-V value<br>";
		html += "if V-Ic value is not recorded, by the following formula.<br>";
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
	 * Gets the accuracy of R.A. and Decl.
	 * @return the accuracy of R.A. and Decl.
	 */
	public byte getCoorAccuracy ( ) {
		return coor_accuracy;
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
			l.addElement(new KeyAndValue("B-V", getMagnitudeString("B-V")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}
		try {
			l.addElement(new KeyAndValue("V-R", getMagnitudeString("V-R")));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
		}
		try {
			l.addElement(new KeyAndValue("V-I", getMagnitudeString("V-I")));
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

			accuracy = ACCURACY_100TH;
			if (key_and_value.getValue().length() - key_and_value.getValue().indexOf('.') == 4)
				accuracy = ACCURACY_1000TH;
		} else if (key_and_value.getKey().equals("B-V")) {
			b_v = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("V-R")) {
			v_r = (float)Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("V-I")) {
			v_i = (float)Format.doubleValueOf(key_and_value.getValue());
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
			key_and_values[4] = new KeyAndValue("B-V", getMagnitudeString("B-V"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[4] = new KeyAndValue("B-V", "");
		}

		try {
			key_and_values[5] = new KeyAndValue("V-R", getMagnitudeString("V-R"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[5] = new KeyAndValue("V-R", "");
		}

		try {
			key_and_values[6] = new KeyAndValue("V-I", getMagnitudeString("V-I"));
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			key_and_values[6] = new KeyAndValue("V-I", "");
		}

		return key_and_values;
	}
}
