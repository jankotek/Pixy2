/*
 * @(#)Msx5cStar.java
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
 * The <code>Msx5cStar</code> represents a star data in the MSX5C 
 * Infrared Point Source Catalog.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Msx5cStar extends CatalogStar {
	/**
	 * The name.
	 */
	protected String name;

	/**
	 * The in-scan position error in arcsec.
	 */
	protected float error_in_scan = (float)0.0;

	/**
	 * The cross-scan position error in arcsec.
	 */
	protected float error_cross_scan = (float)0.0;

	/**
	 * The flux in band A.
	 */
	protected float flux_A = (float)-1;

	/**
	 * The flux variability in band A.
	 */
	protected boolean flux_A_var = false;

	/**
	 * The flux in band B1.
	 */
	protected float flux_B1 = (float)-1;

	/**
	 * The flux variability in band B1.
	 */
	protected boolean flux_B1_var = false;

	/**
	 * The flux in band B2.
	 */
	protected float flux_B2 = (float)-1;

	/**
	 * The flux variability in band B2.
	 */
	protected boolean flux_B2_var = false;

	/**
	 * The flux in band C.
	 */
	protected float flux_C = (float)-1;

	/**
	 * The flux variability in band C.
	 */
	protected boolean flux_C_var = false;

	/**
	 * The flux in band D.
	 */
	protected float flux_D = (float)-1;

	/**
	 * The flux variability in band D.
	 */
	protected boolean flux_D_var = false;

	/**
	 * The flux in band E.
	 */
	protected float flux_E = (float)-1;

	/**
	 * The flux variability in band E.
	 */
	protected boolean flux_E_var = false;

	/**
	 * Constructs an empty <code>Msx5cStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Msx5cStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>Msx5cStar</code> with data read from the
	 * catalog file.
	 * @param name             the name.
	 * @param coor             the R.A. and Decl.
	 * @param error_in_scan    the in-scan position error in arcsec.
	 * @param error_cross_scan the cross-scan position error in arcsec.
	 */
	public Msx5cStar ( String name,
					   Coor coor,
					   float error_in_scan,
					   float error_cross_scan )
	{
		super();
		setCoor(coor);

		this.name = name;

		this.error_in_scan = error_in_scan;
		this.error_cross_scan = error_cross_scan;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>MSX5C G123.4567+12.3456</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return "MSX5C " + name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>MSX5C_G123.4567+12.3456</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "MSX5C_" + name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		name = name.substring(5).trim();
		if (name.charAt(0) == '_')
			name = name.substring(1);
		this.name = name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "MSX5C Infrared Point Source Catalog";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "MSX5C";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "MSX5C";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "MSX5C";
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

		folder_list.addElement(name.substring(1, 3) + "0");
		folder_list.addElement(name.substring(9, 12));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return name;
	}

	/**
	 * Gets the mean error of position in arcsec.
	 * @return the mean error of position in arcsec.
	 */
	public double getPositionErrorInArcsec ( ) {
		return 7.5;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 20.0;
	}

	/**
	 * Gets the accuracy of R.A. and Decl.
	 * @return the accuracy of R.A. and Decl.
	 */
	public byte getCoorAccuracy ( ) {
		return Coor.ACCURACY_100M_ARCSEC;
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
	 * Sets the flux in band A.
	 * @param flux the flux.
	 * @param var  true if variable.
	 */
	public void setFluxA ( float flux, boolean var ) {
		this.flux_A = flux;
		this.flux_A_var = var;
	}
		
	/**
	 * Sets the flux in band B1.
	 * @param flux the flux.
	 * @param var  true if variable.
	 */
	public void setFluxB1 ( float flux, boolean var ) {
		this.flux_B1 = flux;
		this.flux_B1_var = var;
	}
		
	/**
	 * Sets the flux in band .
	 * @param flux the flux.
	 * @param var  true if variable.
	 */
	public void setFluxB2 ( float flux, boolean var ) {
		this.flux_B2 = flux;
		this.flux_B2_var = var;
	}
		
	/**
	 * Sets the flux in band C.
	 * @param flux the flux.
	 * @param var  true if variable.
	 */
	public void setFluxC ( float flux, boolean var ) {
		this.flux_C = flux;
		this.flux_C_var = var;
	}
		
	/**
	 * Sets the flux in band D.
	 * @param flux the flux.
	 * @param var  true if variable.
	 */
	public void setFluxD ( float flux, boolean var ) {
		this.flux_D = flux;
		this.flux_D_var = var;
	}
		
	/**
	 * Sets the flux in band E.
	 * @param flux the flux.
	 * @param var  true if variable.
	 */
	public void setFluxE ( float flux, boolean var ) {
		this.flux_E = flux;
		this.flux_E_var = var;
	}
		
	/**
	 * Formats the flux.
	 * @param flux the flux.
	 * @param var  true if variable.
	 * @return the formatted flux string.
	 */
	protected static String formatFlux ( float flux, boolean var ) {
		String flux_str = "";

		if (flux >= 1000)
			flux_str = Format.formatDouble((double)flux, 6, 4).trim();
		else if (flux >= 100)
			flux_str = Format.formatDouble((double)flux, 6, 3).trim();
		else if (flux >= 10)
			flux_str = Format.formatDouble((double)flux, 6, 2).trim();
		else if (flux >= 1)
			flux_str = Format.formatDouble((double)flux, 6, 1).trim();
		else if (flux >= 0.1)
			flux_str = Format.formatDouble((double)flux, 7, 1).trim();
		else if (flux >= 0.01)
			flux_str = Format.formatDouble((double)flux, 8, 1).trim();
		else
			flux_str = Format.formatDouble((double)flux, 9, 1).trim();

		if (var)
			flux_str = flux_str + "(var)";

		return flux_str;
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		Vector l = new Vector();

		l.addElement(new KeyAndValue("InScanError", Format.formatDouble((double)error_in_scan, 4, 2).trim() + "\""));
		l.addElement(new KeyAndValue("CrossScanError", Format.formatDouble((double)error_cross_scan, 4, 2).trim() + "\""));

		if (flux_A >= 0)
			l.addElement(new KeyAndValue("Flux(A)", formatFlux(flux_A, flux_A_var)));
		if (flux_B1 >= 0)
			l.addElement(new KeyAndValue("Flux(B1)", formatFlux(flux_B1, flux_B1_var)));
		if (flux_B2 >= 0)
			l.addElement(new KeyAndValue("Flux(B2)", formatFlux(flux_B2, flux_B2_var)));
		if (flux_C >= 0)
			l.addElement(new KeyAndValue("Flux(C)", formatFlux(flux_C, flux_C_var)));
		if (flux_D >= 0)
			l.addElement(new KeyAndValue("Flux(D)", formatFlux(flux_D, flux_D_var)));
		if (flux_E >= 0)
			l.addElement(new KeyAndValue("Flux(E)", formatFlux(flux_E, flux_E_var)));

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

		if (key_and_value.getKey().equals("InScanError")) {
			int p = value.indexOf('"');
			error_in_scan = Float.parseFloat(value.substring(0, p));
		} else if (key_and_value.getKey().equals("CrossScanError")) {
			int p = value.indexOf('"');
			error_cross_scan = Float.parseFloat(value.substring(0, p));
		} else if (key_and_value.getKey().equals("Flux(A)")) {
			if (value.indexOf("(var)") > 0) {
				int p = value.indexOf("(var)");
				flux_A = Float.parseFloat(value.substring(0, p));
				flux_A_var = true;
			} else {
				flux_A = Float.parseFloat(value);
				flux_A_var = false;
			}
		} else if (key_and_value.getKey().equals("Flux(B1)")) {
			if (value.indexOf("(var)") > 0) {
				int p = value.indexOf("(var)");
				flux_B1 = Float.parseFloat(value.substring(0, p));
				flux_B1_var = true;
			} else {
				flux_B1 = Float.parseFloat(value);
				flux_B1_var = false;
			}
		} else if (key_and_value.getKey().equals("Flux(B2)")) {
			if (value.indexOf("(var)") > 0) {
				int p = value.indexOf("(var)");
				flux_B2 = Float.parseFloat(value.substring(0, p));
				flux_B2_var = true;
			} else {
				flux_B2 = Float.parseFloat(value);
				flux_B2_var = false;
			}
		} else if (key_and_value.getKey().equals("Flux(C)")) {
			if (value.indexOf("(var)") > 0) {
				int p = value.indexOf("(var)");
				flux_C = Float.parseFloat(value.substring(0, p));
				flux_C_var = true;
			} else {
				flux_C = Float.parseFloat(value);
				flux_C_var = false;
			}
		} else if (key_and_value.getKey().equals("Flux(D)")) {
			if (value.indexOf("(var)") > 0) {
				int p = value.indexOf("(var)");
				flux_D = Float.parseFloat(value.substring(0, p));
				flux_D_var = true;
			} else {
				flux_D = Float.parseFloat(value);
				flux_D_var = false;
			}
		} else if (key_and_value.getKey().equals("Flux(E)")) {
			if (value.indexOf("(var)") > 0) {
				int p = value.indexOf("(var)");
				flux_E = Float.parseFloat(value.substring(0, p));
				flux_E_var = true;
			} else {
				flux_E = Float.parseFloat(value);
				flux_E_var = false;
			}
		}
	}
}
