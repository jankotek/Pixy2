/*
 * @(#)DirectVStar.java
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
 * The <code>DirectVStar</code> represents a star data in the DIRECT 
 * M31 Eclipsing Binaries and Cepheids.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class DirectVStar extends DefaultStar {
	/**
	 * The catalog revision.
	 */
	private String catalog_revision;

	/**
	 * Constructs an empty <code>DirectVStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public DirectVStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>DirectVStar</code> with data read from the
	 * catalog file.
	 * @param name             the star name.
	 * @param catalog_revision the catalog revision.
	 * @param coor_string      the R.A. and Decl.
	 * @param v_mag            the V magnitude.
	 * @param type             the type.
	 * @param period           the period.
	 */
	public DirectVStar ( String name,
						 String catalog_revision,
						 String coor_string,
						 String v_mag,
						 String type,
						 String period )
	{
		super(name, coor_string);

		this.catalog_revision = catalog_revision;

		setKeyAndValue(new KeyAndValue("Mag(V)", v_mag));
		setKeyAndValue(new KeyAndValue("Type", type));
		setKeyAndValue(new KeyAndValue("Period", period));
	}

	/**
	 * Gets the name of this star.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return "DIRECT " + name + " " + catalog_revision;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses. 
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "DIRECT_" + name + "_" + catalog_revision;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		StringTokenizer st = new StringTokenizer(name);
		st.nextToken();		// DIRECT
		this.name = st.nextToken();
		this.catalog_revision = st.nextToken();
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "DIRECT M31 Eclipsing Binaries and Cepheids";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "DIRECT V";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "DIRECT";
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

		folder_list.addElement(catalog_revision);

		return folder_list;
	}
}
