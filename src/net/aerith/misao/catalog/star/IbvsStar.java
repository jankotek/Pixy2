/*
 * @(#)IbvsStar.java
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
 * The <code>IbvsStar</code> represents a star data in the IBVS 
 * Reports on New Discoveries.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2005 June 25
 */

public class IbvsStar extends DefaultVariableStar {
	/**
	 * The IBVS number.
	 */
	protected short ibvs_number = 0;

	/**
	 * The sequential number.
	 */
	protected short sequential_number = 0;

	/**
	 * The accuracy of R.A. and Decl.
	 */
	protected byte coor_accuracy = Coor.ACCURACY_100M_ARCSEC;

	/**
	 * Constructs an empty <code>IbvsStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public IbvsStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>IbvsStar</code> with data read from the
	 * catalog file.
	 * @param ibvs_number       the IBVS number.
	 * @param sequential_number the sequential number.
	 * @param coor_string       the R.A. and Decl.
	 */
	public IbvsStar ( int ibvs_number,
					  int sequential_number,
					  String coor_string )
	{
		super();

		this.ibvs_number = (short)ibvs_number;
		this.sequential_number = (short)sequential_number;

		setCoor(Coor.create(coor_string));
		setCoorAccuracy(Coor.getAccuracy(coor_string));
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>R And</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return "IBVS" + ibvs_number + " No." + sequential_number;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		int p1 = name.indexOf(' ');
		int p2 = name.indexOf('.');
		ibvs_number = Short.parseShort(name.substring(4, p1));
		sequential_number = Short.parseShort(name.substring(p2 + 1));
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "IBVS Reports on New Discoveries";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "IBVS";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "IBVS";
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

		folder_list.addElement(String.valueOf(ibvs_number));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return String.valueOf(sequential_number);
	}

	/**
	 * Gets the mean error of position in arcsec.
	 * @return the mean error of position in arcsec.
	 */
	public double getPositionErrorInArcsec ( ) {
		if (coor_accuracy == Coor.ACCURACY_ROUGH_ARCSEC)
			return 15.0;
		if (coor_accuracy == Coor.ACCURACY_100M_ARCMIN)
			return 15.0;
		if (coor_accuracy == Coor.ACCURACY_100M_ARCMIN_HOURSEC)
			return 25.0;
		if (coor_accuracy == Coor.ACCURACY_ARCMIN)
			return 60.0;
		return 3.0;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		if (coor_accuracy == Coor.ACCURACY_ROUGH_ARCSEC)
			return 30.0;
		if (coor_accuracy == Coor.ACCURACY_100M_ARCMIN)
			return 30.0;
		if (coor_accuracy == Coor.ACCURACY_100M_ARCMIN_HOURSEC)
			return 30.0;
		if (coor_accuracy == Coor.ACCURACY_ARCMIN)
			return 90.0;
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
		property.setFixedRadius(6);
		property.setMark(PlotProperty.PLOT_RECTANGLE);
		return property;
	}
}
