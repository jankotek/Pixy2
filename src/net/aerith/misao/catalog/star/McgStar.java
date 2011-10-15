/*
 * @(#)McgStar.java
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
 * The <code>McgStar</code> represents a star data in the 
 * Morphological Catalog of Galaxies.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class McgStar extends DefaultStar {
	/**
	 * Constructs an empty <code>McgStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public McgStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>McgStar</code> with data read from the
	 * catalog file.
	 * @param name       the name.
	 * @param coor       the R.A. and Decl.
	 * @param p_mag      the p magnitude.
	 * @param size       the size.
	 * @param inner_size the inner size.
	 * @param id         the identified star.
	 */
	public McgStar ( String name,
					 Coor coor,
					 String p_mag,
					 String size,
					 String inner_size,
					 String id )
	{
		super();

		this.name = name;
		setCoor(coor);
		setCoorAccuracy(Coor.ACCURACY_ARCMIN);

		setKeyAndValue(new KeyAndValue("Mag(p)", p_mag));
		setKeyAndValue(new KeyAndValue("Size", size));
		setKeyAndValue(new KeyAndValue("InnerSize", inner_size));
		setKeyAndValue(new KeyAndValue("ID", id));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "MCG ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "MCG" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Morphological Catalog of Galaxies";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "MCG";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "MCG";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_CLUSTER_AND_NEBULA;
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(getCatalogCategoryNumber()));
		folder_list.addElement(getCatalogFolderCode());

		folder_list.addElement(name.substring(0, 3));
		folder_list.addElement(name.substring(4, 6));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return name.substring(7);
	}
}
