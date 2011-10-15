/*
 * @(#)Sharpless2Star.java
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
 * The <code>Sharpless2Star</code> represents a star data of Catalogue
 * of HII Regions (Sh2).
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 9
 */

public class Sharpless2Star extends CatalogStar {
	/**
	 * The number.
	 */
	protected short number = 0;

	/**
	 * The diameter in arcmin.
	 */
	protected int dia = 0;

	/**
	 * Constructs an empty <code>Sharpless2Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Sharpless2Star ( ) {
		super();
	}

	/**
	 * Constructs a <code>Sharpless2Star</code> with data read from the
	 * catalog file.
	 * @param number the star number.
	 * @param coor   the R.A. and Decl.
	 * @param dia    the diameter in arcmin.
	 */
	public Sharpless2Star ( int number,
							Coor coor,
							int dia )
	{
		super();
		setCoor(coor);

		this.number = (short)number;
		this.dia = dia;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>SH 2-123</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return "SH 2-" + number;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>SH2-123</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "SH2-" + number;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		int p = name.indexOf("-");
		name = name.substring(p+1).trim();
		number = Short.parseShort(name);
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Catalogue of HII Regions (Sh2)";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "Sh2";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "Sh2";
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

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		int no = (int)number;
		return String.valueOf(no);
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

		l.addElement(new KeyAndValue("Size", String.valueOf(dia) + "'"));

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
			dia = Format.intValueOf(key_and_value.getValue());
		}
	}
}
