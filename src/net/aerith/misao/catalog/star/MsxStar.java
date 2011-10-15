/*
 * @(#)MsxStar.java
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
 * The <code>MsxStar</code> represents a star data in the MSX Infrared
 * Astrometric Catalog.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class MsxStar extends CatalogStar {
	/**
	 * The number.
	 */
	protected int number = 0;

	/**
	 * The position error in sec and arcsec.
	 */
	protected Coor position_error = new Coor();

	/**
	 * The proper motion in sec/year and arcsec/year.
	 */
	protected Coor pm = new Coor();

	/**
	 * The proper motion error in mas/year.
	 */
	protected Coor pm_error = new Coor();

	/**
	 * The flux in band A.
	 */
	protected float flux_A = (float)0.0;

	/**
	 * The flux in band B1.
	 */
	protected float flux_B1 = (float)0.0;

	/**
	 * The flux in band B2.
	 */
	protected float flux_B2 = (float)0.0;

	/**
	 * The flux in band C.
	 */
	protected float flux_C = (float)0.0;

	/**
	 * The flux in band D.
	 */
	protected float flux_D = (float)0.0;

	/**
	 * The flux in band E.
	 */
	protected float flux_E = (float)0.0;

	/**
	 * The spectral type.
	 */
	protected byte[] spectrum;

	/**
	 * Constructs an empty <code>MsxStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public MsxStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>MsxStar</code> with data read from the
	 * catalog file.
	 * @param number        the number.
	 * @param coor          the R.A. and Decl.
	 * @param error_ra      the R.A. error in sec.
	 * @param error_decl    the Decl. error in arcsec.
	 * @param pm_ra         the proper motion in R.A. in sec/year.
	 * @param pm_decl       the proper motion in Decl. in arcsec/year.
	 * @param pm_error_ra   the proper motion error in R.A. in mas.
	 * @param pm_error_decl the proper motion error in Decl. in mas.
	 * @param v_mag         the V magnitude.
	 * @param spectrum      the spectral type.
	 */
	public MsxStar ( int number,
					 Coor coor,
					 float error_ra, 
					 float error_decl,
					 double pm_ra,
					 double pm_decl,
					 float pm_error_ra,
					 float pm_error_decl,
					 float v_mag, 
					 String spectrum )
	{
		super();
		setCoor(coor);
		setMag((double)v_mag);

		this.number = number;

		position_error = new Coor((double)error_ra, (double)error_decl);
		pm = new Coor(pm_ra, pm_decl);
		pm_error = new Coor((double)pm_error_ra, (double)pm_error_decl);

		try {
			this.spectrum = spectrum.getBytes("ASCII");
		} catch ( UnsupportedEncodingException exception ) {
			this.spectrum = spectrum.getBytes();
		}
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>MSX IR 12345</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return "MSX IR " + String.valueOf(number);
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>MSXIR12345</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "MSXIR" + String.valueOf(number);
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		int p = name.indexOf("IR");
		number = Integer.parseInt(name.substring(p + 2).trim());
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "MSX Infrared Astrometric Catalog";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "MSX IR";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "MSX IR";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "MSX-IR";
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

		int num1 = number / 10000;
		num1 *= 10000;
		int num2 = (number - num1) / 100;
		num2 *= 100;

		folder_list.addElement(String.valueOf(num1));
		folder_list.addElement(String.valueOf(num2));

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
	 * Gets the default property to plot stars.
	 * @return the default property to plot stars.
	 */
	public PlotProperty getDefaultProperty ( ) {
		PlotProperty property = new PlotProperty();
		property.setColor(Color.red);
		property.setFilled(false);
		property.setFixedRadius(5);
		property.setMark(PlotProperty.PLOT_CIRCLE);
		return property;
	}

	/**
	 * Sets the flux.
	 * @param flux_A  the flux in band A.
	 * @param flux_B1 the flux in band B1.
	 * @param flux_B2 the flux in band B2.
	 * @param flux_C  the flux in band C.
	 * @param flux_D  the flux in band D.
	 * @param flux_E  the flux in band E.
	 */
	public void setFlux ( float flux_A, float flux_B1, float flux_B2, float flux_C, float flux_D, float flux_E ) {
		this.flux_A = flux_A;
		this.flux_B1 = flux_B1;
		this.flux_B2 = flux_B2;
		this.flux_C = flux_C;
		this.flux_D = flux_D;
		this.flux_E = flux_E;
	}
		
	/**
	 * Formats the flux.
	 * @param flux the flux.
	 * @return the formatted flux string.
	 */
	protected static String formatFlux ( float flux ) {
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
		else if (flux >= 0.01)
			flux_str = Format.formatDouble((double)flux, 7, 1).trim();
		else
			flux_str = Format.formatDouble((double)flux, 8, 1).trim();

		return flux_str;
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		Vector l = new Vector();

		l.addElement(new KeyAndValue("Error(R.A.)", Format.formatDouble(position_error.getRA(), 6, 1).trim() + "s"));
		l.addElement(new KeyAndValue("Error(Decl.)", Format.formatDouble(position_error.getDecl(), 5, 1).trim() + "\""));

		l.addElement(new KeyAndValue("ProperMotion(R.A.)", Format.formatDouble(pm.getRA(), 9, 3).trim() + "s/year"));
		l.addElement(new KeyAndValue("ProperMotion(Decl.)", Format.formatDouble(pm.getDecl(), 9, 4).trim() + "\"/year"));

		l.addElement(new KeyAndValue("ProperMotionError(R.A.)", Format.formatDouble(pm_error.getRA(), 6, 3).trim() + "mas/year"));
		l.addElement(new KeyAndValue("ProperMotionError(Decl.)", Format.formatDouble(pm_error.getDecl(), 6, 3).trim() + "mas/year"));

		l.addElement(new KeyAndValue("Mag(V)", Format.formatDouble(getMag(), 5, 2).trim()));

		l.addElement(new KeyAndValue("Spectrum", new String(spectrum)));

		l.addElement(new KeyAndValue("Flux(A)", formatFlux(flux_A)));
		l.addElement(new KeyAndValue("Flux(B1)", formatFlux(flux_B1)));
		l.addElement(new KeyAndValue("Flux(B2)", formatFlux(flux_B2)));
		l.addElement(new KeyAndValue("Flux(C)", formatFlux(flux_C)));
		l.addElement(new KeyAndValue("Flux(D)", formatFlux(flux_D)));
		l.addElement(new KeyAndValue("Flux(E)", formatFlux(flux_E)));

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

		if (key_and_value.getKey().equals("Error(R.A.)")) {
			int p = value.indexOf('s');
			position_error.setRA(Double.parseDouble(value.substring(0, p)));
		} else if (key_and_value.getKey().equals("Error(Decl.)")) {
			int p = value.indexOf('"');
			position_error.setDecl(Double.parseDouble(value.substring(0, p)));
		} else if (key_and_value.getKey().equals("ProperMotion(R.A.)")) {
			int p = value.indexOf('s');
			pm.setRA(Double.parseDouble(value.substring(0, p)));
		} else if (key_and_value.getKey().equals("ProperMotion(Decl.)")) {
			int p = value.indexOf('"');
			pm.setDecl(Double.parseDouble(value.substring(0, p)));
		} else if (key_and_value.getKey().equals("ProperMotionError(R.A.)")) {
			int p = value.indexOf("mas/year");
			pm_error.setRA(Double.parseDouble(value.substring(0, p)));
		} else if (key_and_value.getKey().equals("ProperMotionError(Decl.)")) {
			int p = value.indexOf("mas/year");
			pm_error.setDecl(Double.parseDouble(value.substring(0, p)));
		} else if (key_and_value.getKey().equals("Mag(V)")) {
			setMag(Double.parseDouble(value));
		} else if (key_and_value.getKey().equals("Spectrum")) {
			try {
				spectrum = value.getBytes("ASCII");
			} catch ( UnsupportedEncodingException exception ) {
				spectrum = value.getBytes();
			}
		} else if (key_and_value.getKey().equals("Flux(A)")) {
			flux_A = Float.parseFloat(value);
		} else if (key_and_value.getKey().equals("Flux(B1)")) {
			flux_B1 = Float.parseFloat(value);
		} else if (key_and_value.getKey().equals("Flux(B2)")) {
			flux_B2 = Float.parseFloat(value);
		} else if (key_and_value.getKey().equals("Flux(C)")) {
			flux_C = Float.parseFloat(value);
		} else if (key_and_value.getKey().equals("Flux(D)")) {
			flux_D = Float.parseFloat(value);
		} else if (key_and_value.getKey().equals("Flux(E)")) {
			flux_E = Float.parseFloat(value);
		}
	}
}
