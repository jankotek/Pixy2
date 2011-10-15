/*
 * @(#)TychoVarStar.java
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
 * The <code>TychoVarStar</code> represents a star data in the Tycho 
 * Variables Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class TychoVarStar extends DefaultStar {
	/**
	 * The TYC1 number.
	 */
	protected short tyc1;

	/**
	 * The TYC2 number.
	 */
	protected short tyc2;

	/**
	 * The TYC3 number.
	 */
	protected short tyc3;

	/**
	 * Constructs an empty <code>TychoVarStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public TychoVarStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>TychoVarStar</code> with data read from the
	 * catalog file.
	 * @param tyc1        the TYC1 number.
	 * @param tyc2        the TYC2 number.
	 * @param tyc3        the TYC3 number.
	 * @param coor_string the R.A. and Decl.
	 * @param v_mag       the I magnitude.
	 * @param range       the magnitude range.
	 * @param period      the period.
	 * @param status      the status.
	 */
	public TychoVarStar ( short tyc1, 
						  short tyc2,
						  short tyc3,
						  String coor_string,
						  String v_mag,
						  String range,
						  String period,
						  String status )
	{
		super(String.valueOf(tyc1) + "-" + String.valueOf(tyc2) + "-" + String.valueOf(tyc3), coor_string);

		this.tyc1 = tyc1;
		this.tyc2 = tyc2;
		this.tyc3 = tyc3;

		setKeyAndValue(new KeyAndValue("Mag(V)", v_mag));
		setKeyAndValue(new KeyAndValue("Range", range));
		setKeyAndValue(new KeyAndValue("Period", period));
		setKeyAndValue(new KeyAndValue("Status", status));
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>TYC 123-4567-1</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String name = "TYC " + tyc1 + "-" + tyc2 + "-" + tyc3;
		return name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>TYC123-4567-1</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = "TYC" + tyc1 + "." + tyc2;
		return name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		String s = name.substring(4);
		StringTokenizer st = new StringTokenizer(s, "-");
		tyc1 = Short.parseShort(st.nextToken());
		tyc2 = Short.parseShort(st.nextToken());
		tyc3 = Short.parseShort(st.nextToken());
		this.name = String.valueOf(tyc1) + "-" + String.valueOf(tyc2) + "-" + String.valueOf(tyc3);
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Tycho Variables Stars";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "TYC";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "Tycho Variables";
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

		int n = (int)tyc1 / 100;
		n *= 100;
		folder_list.addElement(String.valueOf(n));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return String.valueOf(tyc1) + "-" + String.valueOf(tyc2);
	}
}
