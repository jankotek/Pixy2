/*
 * @(#)LsStar.java
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
 * The <code>LsStar</code> represents a star data in the Luminous 
 * Stars in the Northern Milky Way.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class LsStar extends DefaultStar {
	/**
	 * The catalog revision.
	 */
	private String revision;

	/**
	 * The star area.
	 */
	private String area;

	/**
	 * The star number.
	 */
	private String number;

	/**
	 * Constructs an empty <code>LsStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public LsStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>LsStar</code> with data read from the
	 * catalog file.
	 * @param revision    the catalog revision.
	 * @param area        the star area.
	 * @param number      the star number.
	 * @param coor_string the R.A. and Decl.
	 * @param p_mag       the p magnitude.
	 * @param type        the type.
	 */
	public LsStar ( String revision,
					String area,
					String number,
					String coor_string,
					String p_mag,
					String type )
	{
		super(area + " " + number, coor_string);

		this.revision = revision;
		this.area = area;
		this.number = number;

		setKeyAndValue(new KeyAndValue("Mag(p)", p_mag));
		setKeyAndValue(new KeyAndValue("Type", type));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "LS ";
	}

	/**
	 * Gets the name of this star.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return "LS " + revision + " " + area + " " + number;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "LS" + revision + area + "." + number;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		StringTokenizer st = new StringTokenizer(name);

		st.nextToken();		// LS
		this.revision = st.nextToken();
		this.area = st.nextToken();
		this.number = st.nextToken();

		this.name = this.area + " " + this.number;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Luminous Stars in the Northern Milky Way";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "LS";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "LS";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_OTHERS;
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(getCatalogCategoryNumber()));
		folder_list.addElement(getCatalogFolderCode());

		folder_list.addElement(revision);
		folder_list.addElement(area);

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return number;
	}
}
