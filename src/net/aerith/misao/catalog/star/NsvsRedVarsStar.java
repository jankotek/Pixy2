/*
 * @(#)NsvsRedVarsStar.java
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
 * The <code>NsvsRedVarsStar</code> represents a star data in the Red 
 * AGB Variables from Northern Sky Variability Survey (NSVS).
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 December 31
 */

public class NsvsRedVarsStar extends DefaultStar {
	/**
	 * Constructs an empty <code>NsvsRedVarsStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public NsvsRedVarsStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>NsvsRedVarsStar</code> with data read from the
	 * catalog file.
	 * @param name      the star name.
	 * @param coor      the R.A. and Decl.
	 * @param mag       the median magnitude.
	 * @param mag_error the magnitude error.
	 * @param range     the magnitude range.
	 * @param period    the period.
	 */
	public NsvsRedVarsStar ( String name,
							 Coor coor,
							 double mag,
							 double mag_error,
							 double range,
							 int period )
	{
		super();

		this.name = name;
		setCoor(coor);
		setCoorAccuracy(Coor.ACCURACY_10M_ARCSEC);

		setKeyAndValue(new KeyAndValue("Mag", Format.formatDouble(mag, 6, 2).trim()));
		setKeyAndValue(new KeyAndValue("MagError", Format.formatDouble(mag_error, 6, 2).trim()));
		setKeyAndValue(new KeyAndValue("MagRange", Format.formatDouble(range, 6, 2).trim()));
		setKeyAndValue(new KeyAndValue("Period", String.valueOf(period)));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "NSVS ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>NSVS0123456+01234</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "NSVS" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Red AGB Variables from Northern Sky Variability Survey (NSVS)";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "NSVS Red Vars";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "NSVS Red Vars";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_VARIABLE;
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(CatalogManager.CATEGORY_VARIABLE));
		folder_list.addElement(getCatalogFolderCode());

		folder_list.addElement(name.substring(0, 2));

		return folder_list;
	}
}
