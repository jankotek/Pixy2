/*
 * @(#)GcvsStar.java
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
 * The <code>GcvsStar</code> represents a star data in the General 
 * Catalog of Variable Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public class GcvsStar extends DefaultVariableStar {
	/**
	 * The number of constellation.
	 */
	protected byte constellation = 0;

	/**
	 * The star number (R, S, ...).
	 */
	protected byte[] star_number = null;

	/**
	 * The accuracy of R.A. and Decl.
	 */
	protected byte coor_accuracy = Coor.ACCURACY_100M_ARCSEC;

	/**
	 * Constructs an empty <code>GcvsStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public GcvsStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>GcvsStar</code> with data read from the
	 * catalog file.
	 * @param constellation_name the constellation name.
	 * @param star_number        the star number.
	 * @param coor               the R.A. and Decl.
	 * @param coor_accuracy      the accuracy of R.A. and Decl.
	 * @param type               the type.
	 * @param spectrum           the spectrum.
	 * @param epoch              the epoch in JD.
	 * @param period             the period in day.
	 */
	public GcvsStar ( String constellation_name,
					  String star_number,
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

		this.constellation = (byte)ConstellationTable.getConstellationNumber(constellation_name);

		try {
			this.star_number = star_number.getBytes("ASCII");
		} catch ( UnsupportedEncodingException exception ) {
			this.star_number = star_number.getBytes();
		}			

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
		String name = new String(star_number);
		name += " ";
		name += ConstellationTable.getConstellationCode(constellation);
		return name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>ANDR</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = ConstellationTable.getConstellationCode(constellation).toUpperCase();
		name += new String(star_number);
		return name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		StringTokenizer st = new StringTokenizer(name);
		String constel = null;
		String num = null;
		if (st.countTokens() == 1) {
			// In the case of "ANDR" style.
			constel = name.substring(0, 3);
			num = name.substring(3);
		} else {
			// In the case of "R And" style.
			num = st.nextToken();
			constel = st.nextToken();
		}

		constellation = (byte)ConstellationTable.getConstellationNumber(constel);
		try {
			star_number = num.getBytes("ASCII");
		} catch ( UnsupportedEncodingException exception ) {
			star_number = num.getBytes();
		}			
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "General Catalogue of Variable Stars";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "GCVS";
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

		folder_list.addElement(ConstellationTable.getConstellationCode(constellation));

		String name = new String(star_number);
		if (name.length() >= 2  &&  name.charAt(0) == 'V'  &&
			('0' <= name.charAt(1)  &&  name.charAt(1) <= '9')) {
			int number = Integer.parseInt(name.substring(1)) / 100;
			number *= 100;
			folder_list.addElement("V" + String.valueOf(number));
		} else if ('A' <= name.charAt(0)  &&  name.charAt(0) <= 'Z') {
			folder_list.addElement(name.substring(0, 1));
		} else {
			folder_list.addElement("others");
		}

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return new String(star_number);
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
