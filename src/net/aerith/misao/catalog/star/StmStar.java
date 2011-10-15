/*
 * @(#)StmStar.java
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
 * The <code>StmStar</code> represents a star data in the Minoru 
 * Sato's Possible Noises in GSC.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class StmStar extends CatalogStar {
	/**
	 * The number.
	 */
	protected short number = 0;

	/**
	 * Constructs an empty <code>StmStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public StmStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>StmStar</code> with data read from the
	 * catalog file.
	 * @param number     the star number.
	 * @param coor       the R.A. and Decl.
	 * @param mag        the magnitude.
	 */
	public StmStar ( int number,
					 Coor coor,
					 float mag )
	{
		super();
		setCoor(coor);
		setMag(mag);

		this.number = (short)number;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>R And</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String name = Format.formatIntZeroPadding((int)number, 4);
		return "STM" + name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>ANDR</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = Format.formatIntZeroPadding((int)number, 4);
		return "STM" + name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		number = Short.parseShort(name.substring(3));
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Minoru Sato's Possible Noises in GSC";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "STM";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "STM";
	}

	/**
	 * Gets the category of the catalog.
	 * @return the category of the catalog.
	 */
	public String getCatalogCategory ( ) {
		return CatalogManager.getCatalogCategoryName(CatalogManager.CATEGORY_ERROR);
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(CatalogManager.CATEGORY_ERROR));
		folder_list.addElement(getCatalogFolderCode());

		int no = (int)number / 100;
		no *= 100;

		folder_list.addElement(String.valueOf(no));

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
		property.setColor(Color.blue);
		property.setFilled(false);
		property.setFixedRadius(6);
		property.setMark(PlotProperty.PLOT_CIRCLE);
		return property;
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		KeyAndValue[] key_and_values = new KeyAndValue[1];
		key_and_values[0] = new KeyAndValue("Mag", Format.formatDouble(getMag(), 4, 2).trim());
		return key_and_values;
	}

	/**
	 * Sets the value of the specified key.
	 * @param key_and_value the key and value to set.
	 */
	public void setKeyAndValue ( KeyAndValue key_and_value ) {
		if (key_and_value.getKey().equals("Mag"))
			setMag(Double.parseDouble(key_and_value.getValue()));
	}
}
