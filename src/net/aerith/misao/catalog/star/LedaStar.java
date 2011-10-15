/*
 * @(#)LedaStar.java
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
 * The <code>LedaStar</code> represents a star data of HYPERLEDA. I. 
 * Catalog of Galaxies.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 9
 */

public class LedaStar extends CatalogStar {
	/**
	 * The number.
	 */
	protected int number = 0;

	/**
	 * The diameter in arcmin.
	 */
	protected double dia = -1.0;

	/**
	 * The axis ratio.
	 */
	protected double axis_ratio = -1.0;

	/**
	 * The position angle.
	 */
	protected int pa = -1;

	/**
	 * Constructs an empty <code>LedaStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public LedaStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>LedaStar</code> with data read from the
	 * catalog file.
	 * @param number the star number.
	 * @param coor   the R.A. and Decl.
	 */
	public LedaStar ( int number,
					  Coor coor )
	{
		super();
		setCoor(coor);

		this.number = number;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>LEDA 1234</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return "LEDA " + number;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>LEDA1234</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "LEDA" + number;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		name = name.substring(4).trim();
		number = Integer.parseInt(name);
	}

	/**
	 * Sets the diameter in arcmin.
	 * @param dia the diameter in arcmin.
	 */
	public void setDiameter ( double dia ) {
		this.dia = dia;
	}

	/**
	 * Sets the axis ratio.
	 * @param axis_ratio the axis_ratio.
	 */
	public void setAxisRatio ( double axis_ratio ) {
		this.axis_ratio = axis_ratio;
	}

	/**
	 * Sets the position angle.
	 * @param pa the position angle.
	 */
	public void setPositionAngle ( int pa ) {
		this.pa = pa;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "HYPERLEDA. I. Catalog of Galaxies";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "LEDA";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "LEDA";
	}

	/**
	 * Gets the category of the catalog.
	 * @return the category of the catalog.
	 */
	public String getCatalogCategory ( ) {
		return CatalogManager.getCatalogCategoryName(CatalogManager.CATEGORY_CLUSTER_AND_NEBULA);
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(CatalogManager.CATEGORY_CLUSTER_AND_NEBULA));
		folder_list.addElement(getCatalogFolderCode());

		int no = number / 1000;
		no *= 1000;
		folder_list.addElement(String.valueOf(no));

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
	 * Gets the mean error of position in arcsec.
	 * @return the mean error of position in arcsec.
	 */
	public double getPositionErrorInArcsec ( ) {
		return 120;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 120;
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
		property.setColor(Color.orange);
		property.setLineWidth(2);
		property.setFilled(false);
		property.setFixedRadius(14);
		property.setMark(PlotProperty.PLOT_CIRCLE);
		return property;
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		Vector l = new Vector();

		if (dia >= 0.0) {
			String s = "";
			if (dia >= 100.0)
				s = Format.formatDouble(dia, 10, 10).trim() + "'";
			else if (dia >= 10.0)
				s = Format.formatDouble(dia, 10, 8).trim() + "'";
			else
				s = Format.formatDouble(dia, 10, 7).trim() + "'";
			l.addElement(new KeyAndValue("Size", s));
		}
		if (axis_ratio >= 0.0) {
			String s = "";
			if (axis_ratio >= 100.0)
				s = Format.formatDouble(axis_ratio, 10, 10).trim();
			else if (axis_ratio >= 10.0)
				s = Format.formatDouble(axis_ratio, 10, 8).trim();
			else
				s = Format.formatDouble(axis_ratio, 10, 7).trim();
			l.addElement(new KeyAndValue("AxisRatio", s));
		}
		if (pa >= 0) {
			l.addElement(new KeyAndValue("P.A.", String.valueOf(pa)));
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
		if (key_and_value.getKey().equals("Size")) {
			dia = Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("AxisRatio")) {
			axis_ratio = Format.doubleValueOf(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("P.A.")) {
			pa = Format.intValueOf(key_and_value.getValue());
		}
	}
}
