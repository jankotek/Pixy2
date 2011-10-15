/*
 * @(#)NsvStar.java
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
 * The <code>NsvStar</code> represents a star data in the New 
 * Catalogue of Suspected Variable Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class NsvStar extends DefaultVariableStar {
	/**
	 * The star number.
	 */
	protected int star_number = 0;

	/**
	 * The additional character.
	 */
	protected String additional_character = null;

	/**
	 * The accuracy of R.A. and Decl.
	 */
	protected byte coor_accuracy = Coor.ACCURACY_100M_ARCSEC;

	/**
	 * Constructs an empty <code>NsvStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public NsvStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>NsvStar</code> with data read from the
	 * catalog file.
	 * @param star_number          the star number.
	 * @param additional_character the additional character.
	 * @param coor                 the R.A. and Decl.
	 * @param coor_accuracy        the accuracy of R.A. and Decl.
	 * @param type                 the type.
	 * @param spectrum             the spectrum.
	 * @param epoch                the epoch in JD.
	 * @param period               the period in day.
	 */
	public NsvStar ( int star_number,
					 String additional_character,
					 Coor coor,
					 byte coor_accuracy,
					 String type,
					 String spectrum,
					 String epoch,
					 String period )
	{
		super();
		setCoor(coor);

		setCoorAccuracy(coor_accuracy);

		this.star_number = star_number;
		this.additional_character = additional_character;

		setType(type);
		setSpectrum(spectrum);
		setEpoch(epoch);
		setPeriod(period);
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>R And</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		if (additional_character == null)
			return "NSV " + String.valueOf(star_number);
		return "NSV " + String.valueOf(star_number) + additional_character;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>ANDR</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String n = String.valueOf(star_number);
		while (n.length() < 5)
			n = "0" + n;
		if (additional_character == null)
			return "NSV" + n;
		return "NSV" + n + additional_character;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		additional_character = null;

		String s = name.substring(3).trim();
		String num = "";
		for (int i = 0 ; i < s.length() ; i++) {
			if ('0' <= s.charAt(i)  &&  s.charAt(i) <= '9') {
				num += s.charAt(i);
			} else {
				additional_character = s.substring(i);
				break;
			}
		}
		this.star_number = Integer.parseInt(num);
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "New Catalogue of Suspected Variable Stars";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "NSV";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "NSV";
	}

	/**
	 * Gets the category of the catalog.
	 * @return the category of the catalog.
	 */
	public String getCatalogCategory ( ) {
		return CatalogManager.getCatalogCategoryName(CatalogManager.CATEGORY_VARIABLE);
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(CatalogManager.CATEGORY_VARIABLE));
		folder_list.addElement(getCatalogFolderCode());

		int num1 = star_number / 1000;
		num1 *= 1000;
		int num2 = star_number / 100;
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
		if (additional_character == null)
			return String.valueOf(star_number);
		return String.valueOf(star_number) + additional_character;
	}

	/**
	 * Gets the mean error of position in arcsec.
	 * @return the mean error of position in arcsec.
	 */
	public double getPositionErrorInArcsec ( ) {
		return 4.5;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 30.0;
	}

	/**
	 * Gets the accuracy of R.A. and Decl.
	 * @return the accuracy of R.A. and Decl.
	 */
	public byte getCoorAccuracy ( ) {
		return coor_accuracy;
	}

	/**
	 * Sets the accuracy of R.A. and Decl.
	 * @param accuracy the accuracy of R.A. and Decl.
	 */
	public void setCoorAccuracy ( byte accuracy ) {
		coor_accuracy = accuracy;
	}

	/**
	 * Gets the default property to plot stars.
	 * @return the default property to plot stars.
	 */
	public PlotProperty getDefaultProperty ( ) {
		PlotProperty property = new PlotProperty();
		property.setColor(Color.magenta);
		property.setFilled(false);
		property.setFixedRadius(7);
		property.setMark(PlotProperty.PLOT_CIRCLE);
		return property;
	}
}
