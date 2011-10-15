/*
 * @(#)ExtraGalacticStar.java
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
 * The <code>ExtraGalacticStar</code> represents a star data in the 
 * Extragalactic Variable Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 December 29
 */

public class ExtraGalacticStar extends DefaultVariableStar {
	/**
	 * The host galaxy name.
	 */
	protected String host = "";

	/**
	 * The star number.
	 */
	protected int star_number = 0;

	/**
	 * The accuracy of R.A. and Decl.
	 */
	protected byte coor_accuracy = Coor.ACCURACY_100M_ARCSEC;

	/**
	 * Constructs an empty <code>ExtraGalacticStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public ExtraGalacticStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>ExtraGalacticStar</code> with data read from the
	 * catalog file.
	 * @param host          the host galaxy name.
	 * @param star_number   the star number.
	 * @param coor          the R.A. and Decl.
	 * @param coor_accuracy the accuracy of R.A. and Decl.
	 * @param type          the type.
	 * @param spectrum      the spectrum.
	 * @param epoch         the epoch in JD.
	 * @param period        the period in day.
	 */
	public ExtraGalacticStar ( String host, 
							   int star_number,
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

		this.host = host;
		this.star_number = star_number;

		setType(type);
		setSpectrum(spectrum);
		setEpoch(epoch);
		setPeriod(period);
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>NGC 147 V1</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return host + " V" + star_number;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>NGC147_V1</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = "";
		StringTokenizer st = new StringTokenizer(host);
		while (st.hasMoreElements())
			name += st.nextToken();
		if (host.equals("W-L-M")) {
			name = "WLM";
		} else if (host.indexOf(' ') > 0) {
			name += "_";
		}
		return name + "V" + star_number;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		int p = name.lastIndexOf(" V");
		star_number = Format.intValueOf(name.substring(p + 2));
		host = name.substring(0, p);
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Extragalactic Variable Stars";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "ExtraGalactic";
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

		String s = "";
		StringTokenizer st = new StringTokenizer(host);
		while (st.hasMoreElements())
			s += st.nextToken();
		folder_list.addElement(s);

		int n = star_number / 100;
		folder_list.addElement(String.valueOf(n * 100));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return String.valueOf(star_number);
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
		return 10.0;
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
