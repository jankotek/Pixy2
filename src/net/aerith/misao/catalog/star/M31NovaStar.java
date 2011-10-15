/*
 * @(#)M31NovaStar.java
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
 * The <code>M31NovaStar</code> represents a star data in the CBAT M31 
 * (Apparent) Novae Page.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 February 12
 */

public class M31NovaStar extends DefaultStar {
	/**
	 * The year.
	 */
	protected int year;

	/**
	 * The designation.
	 */
	protected String designation;

	/**
	 * Constructs an empty <code>M31NovaStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public M31NovaStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>M31NovaStar</code> with data read from the
	 * catalog file.
	 * @param year        the year.
	 * @param designation the designation.
	 * @param coor_string the R.A. and Decl.
	 * @param mag         the magnitude.
	 * @param mag_system  the magnitude system.
	 * @param date        the date.
	 * @param reporter    the reporter.
	 */
	public M31NovaStar ( int year,
						 String designation,
						 String coor_string,
						 String mag,
						 String mag_system,
						 String date,
						 String reporter )
	{
		super(String.valueOf(year) + "-" + designation, coor_string);

		this.year = year;
		this.designation = designation;

		setKeyAndValue(new KeyAndValue("Mag", mag));
		setKeyAndValue(new KeyAndValue("MagSystem", mag_system));
		setKeyAndValue(new KeyAndValue("Date", date));
		setKeyAndValue(new KeyAndValue("Reporter", reporter));
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		name = name.substring(4).trim();

		year = Integer.parseInt(name.substring(0, 4));
		designation = name.substring(5);

		this.name = String.valueOf(year) + "-" + designation;
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "M31N ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "M31N" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "CBAT M31 (Apparent) Novae";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "M31N";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "M31N";
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

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(getCatalogCategoryNumber()));
		folder_list.addElement(getCatalogFolderCode());

		folder_list.addElement(String.valueOf(year));

		return folder_list;
	}
}
