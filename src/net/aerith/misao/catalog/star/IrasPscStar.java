/*
 * @(#)IrasPscStar.java
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
 * The <code>IrasPscStar</code> represents a star data in the IRAS 
 * catalogue of Point Sources. 
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class IrasPscStar extends CatalogStar {
	/**
	 * The number.
	 */
	protected byte[] number;

	/**
	 * The uncertainty ellipse major axis.
	 */
	protected short ellipse_major = 0;

	/**
	 * The uncertainty ellipse minor axis.
	 */
	protected short ellipse_minor = 0;

	/**
	 * The uncertainty ellipse position angle.
	 */
	protected short ellipse_pa = 0;

	/**
	 * The flux at 12 um.
	 */
	protected float flux12 = (float)0.0;

	/**
	 * The flux at 25 um.
	 */
	protected float flux25 = (float)0.0;

	/**
	 * The flux at 60 um.
	 */
	protected float flux60 = (float)0.0;

	/**
	 * The flux at 100 um.
	 */
	protected float flux100 = (float)0.0;

	/**
	 * True if the flux at 12 um is upper limit.
	 */
	protected boolean flux12_limit = false;

	/**
	 * True if the flux at 25 um is upper limit.
	 */
	protected boolean flux25_limit = false;

	/**
	 * True if the flux at 60 um is upper limit.
	 */
	protected boolean flux60_limit = false;

	/**
	 * True if the flux at 100 um is upper limit.
	 */
	protected boolean flux100_limit = false;

	/**
	 * The likelihood of variability. When no data is recorded, it is
	 * set as -1.
	 */
	protected byte variability = -1;

	/**
	 * Constructs an empty <code>IrasPscStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public IrasPscStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>IrasPscStar</code> with data read from the
	 * catalog file.
	 * @param number        the IRAS number.
	 * @param coor          the R.A. and Decl.
	 * @param ellipse_major the uncertainty ellipse major axis.
	 * @param ellipse_minor the uncertainty ellipse minor axis.
	 * @param ellipse_pa    the uncertainty ellipse position angle.
	 */
	public IrasPscStar ( String number,
						 Coor coor,
						 short ellipse_major,
						 short ellipse_minor,
						 short ellipse_pa )
	{
		super();
		setCoor(coor);

		try {
			this.number = number.getBytes("ASCII");
		} catch ( UnsupportedEncodingException exception ) {
			this.number = number.getBytes();
		}

		this.ellipse_major = ellipse_major;
		this.ellipse_minor = ellipse_minor;
		this.ellipse_pa = ellipse_pa;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>IRAS 01234+5678</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String name = new String(number);
		return "IRAS " + name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>IRAS01234+5678</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = new String(number);
		return "IRAS" + name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		name = name.substring(4).trim();
		try {
			this.number = name.getBytes("ASCII");
		} catch ( UnsupportedEncodingException exception ) {
			this.number = name.getBytes();
		}
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "IRAS Point Source Catalogue";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "IRAS";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "IRAS PSC";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "IRAS-PSC";
	}

	/**
	 * Gets the category of the catalog.
	 * @return the category of the catalog.
	 */
	public String getCatalogCategory ( ) {
		return CatalogManager.getCatalogCategoryName(CatalogManager.CATEGORY_INFRARED);
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(CatalogManager.CATEGORY_INFRARED));
		folder_list.addElement(getCatalogFolderCode());

		String s = new String(number);

		folder_list.addElement(s.substring(0, 2));
		folder_list.addElement(s.substring(2, 3) + "0");
		folder_list.addElement(s.substring(5, 7) + "0");
		folder_list.addElement(s.substring(7, 8));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return new String(number);
	}

	/**
	 * Gets the mean error of position in arcsec.
	 * @return the mean error of position in arcsec.
	 */
	public double getPositionErrorInArcsec ( ) {
		return 60.0;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 60.0;
	}

	/**
	 * Gets the accuracy of R.A. and Decl.
	 * @return the accuracy of R.A. and Decl.
	 */
	public byte getCoorAccuracy ( ) {
		return Coor.ACCURACY_ARCSEC;
	}

	/**
	 * Gets the default property to plot stars.
	 * @return the default property to plot stars.
	 */
	public PlotProperty getDefaultProperty ( ) {
		PlotProperty property = new PlotProperty();
		property.setColor(Color.red);
		property.setFilled(false);
		property.setFixedRadius(10);
		property.setMark(PlotProperty.PLOT_CIRCLE);
		return property;
	}

	/**
	 * Sets the flux at 12 um.
	 * @param flux  the flux.
	 * @param limit true if the flux is upper limit.
	 */
	public void setFlux12 ( float flux, boolean limit ) {
		flux12 = flux;
		flux12_limit = limit;
	}

	/**
	 * Sets the flux at 25 um.
	 * @param flux  the flux.
	 * @param limit true if the flux is upper limit.
	 */
	public void setFlux25 ( float flux, boolean limit ) {
		flux25 = flux;
		flux25_limit = limit;
	}

	/**
	 * Sets the flux at 60 um.
	 * @param flux  the flux.
	 * @param limit true if the flux is upper limit.
	 */
	public void setFlux60 ( float flux, boolean limit ) {
		flux60 = flux;
		flux60_limit = limit;
	}

	/**
	 * Sets the flux at 100 um.
	 * @param flux  the flux.
	 * @param limit true if the flux is upper limit.
	 */
	public void setFlux100 ( float flux, boolean limit ) {
		flux100 = flux;
		flux100_limit = limit;
	}
		
	/**
	 * Sets the variability.
	 * @param variability the variability.
	 */
	public void setVariability ( int variability ) {
		this.variability = (byte)variability;
	}
		
	/**
	 * Formats the flux.
	 * @param flux  the flux.
	 * @param limit true if the flux is upper limit.
	 * @return the formatted flux string.
	 */
	protected static String formatFlux ( float flux, boolean limit ) {
		String flux_str = "";

		if (flux >= 1000)
			flux_str = Format.formatDouble((double)flux, 10, 10).trim();
		else if (flux >= 100)
			flux_str = Format.formatDouble((double)flux, 5, 3).trim();
		else if (flux >= 10)
			flux_str = Format.formatDouble((double)flux, 5, 2).trim();
		else if (flux >= 1)
			flux_str = Format.formatDouble((double)flux, 5, 1).trim();
		else if (flux >= 0.1)
			flux_str = Format.formatDouble((double)flux, 6, 1).trim();
		else
			flux_str = Format.formatDouble((double)flux, 7, 1).trim();

		if (limit)
			flux_str = "<" + flux_str;

		return flux_str;
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		Vector l = new Vector();

		String ellipse = "" + ellipse_major + "x" + ellipse_minor + "\"";
		l.addElement(new KeyAndValue("Ellipse", ellipse));
		l.addElement(new KeyAndValue("P.A.", "" + ellipse_pa));

		l.addElement(new KeyAndValue("Flux(12)", formatFlux(flux12, flux12_limit)));
		l.addElement(new KeyAndValue("Flux(25)", formatFlux(flux25, flux25_limit)));
		l.addElement(new KeyAndValue("Flux(60)", formatFlux(flux60, flux60_limit)));
		l.addElement(new KeyAndValue("Flux(100)", formatFlux(flux100, flux100_limit)));

		int var = (int)variability;
		if (var >= 0)
			l.addElement(new KeyAndValue("Var", "" + var + "%"));

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
		String value = key_and_value.getValue();

		if (key_and_value.getKey().equals("Ellipse")) {
			StringTokenizer st = new StringTokenizer(value, "x\"");
			ellipse_major = Short.parseShort(st.nextToken());
			ellipse_minor = Short.parseShort(st.nextToken());
		} else if (key_and_value.getKey().equals("P.A.")) {
			ellipse_pa = Short.parseShort(value);
		} else if (key_and_value.getKey().equals("Flux(12)")) {
			flux12_limit = false;
			if (value.charAt(0) == '<') {
				flux12_limit = true;
				value = value.substring(1);
			}
			flux12 = Float.parseFloat(value);
		} else if (key_and_value.getKey().equals("Flux(25)")) {
			flux25_limit = false;
			if (value.charAt(0) == '<') {
				flux25_limit = true;
				value = value.substring(1);
			}
			flux25 = Float.parseFloat(value);
		} else if (key_and_value.getKey().equals("Flux(60)")) {
			flux60_limit = false;
			if (value.charAt(0) == '<') {
				flux60_limit = true;
				value = value.substring(1);
			}
			flux60 = Float.parseFloat(value);
		} else if (key_and_value.getKey().equals("Flux(100)")) {
			flux100_limit = false;
			if (value.charAt(0) == '<') {
				flux100_limit = true;
				value = value.substring(1);
			}
			flux100 = Float.parseFloat(value);
		} else if (key_and_value.getKey().equals("Var")) {
			int p = value.indexOf('%');
			variability = (byte)Integer.parseInt(value.substring(0, p));
		}
	}
}
