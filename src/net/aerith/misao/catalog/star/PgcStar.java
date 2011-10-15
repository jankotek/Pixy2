/*
 * @(#)PgcStar.java
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
 * The <code>PgcStar</code> represents a star data in the Catalogue of
 * Principal Galaxies (PGC).
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class PgcStar extends DefaultStar {
	/**
	 * Constructs an empty <code>PgcStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public PgcStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>PgcStar</code> with data read from the
	 * catalog file.
	 * @param number      the number.
	 * @param coor_string the R.A. and Decl.
	 * @param mag         the magnitude.
	 * @param type        the type.
	 * @param size        the size.
	 * @param pa          the position angle.
	 */
	public PgcStar ( int number,
					 String coor_string,
					 String mag,
					 String type,
					 String size,
					 String pa )
	{
		super(String.valueOf(number), coor_string);

		setKeyAndValue(new KeyAndValue("Mag", mag));
		setKeyAndValue(new KeyAndValue("Type", type));
		setKeyAndValue(new KeyAndValue("Size", size));
		setKeyAndValue(new KeyAndValue("P.A.", pa));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "PGC ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "PGC" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Catalogue of Principal Galaxies (PGC)";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "PGC";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "PGC";
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

		int number = Integer.parseInt(name) / 100;
		number *= 100;
		folder_list.addElement(String.valueOf(number));

		return folder_list;
	}
}
