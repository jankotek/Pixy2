/*
 * @(#)Eros2GsaStar.java
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
 * The <code>Eros2GsaStar</code> represents a star data in the EROS II
 * Galactic Spiral Arms Variable Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Eros2GsaStar extends DefaultStar {
	/**
	 * Constructs an empty <code>Eros2GsaStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Eros2GsaStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>Eros2GsaStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param Rmag        the R magnitude.
	 * @param Rrange      the range of R magnitude.
	 * @param Vmag        the V magnitude.
	 * @param Vrange      the range of V magnitude.
	 * @param type        the type.
	 * @param period      the period.
	 */
	public Eros2GsaStar ( String name,
						  String coor_string,
						  String Rmag,
						  String Rrange,
						  String Vmag,
						  String Vrange,
						  String type,
						  String period )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(R)", Rmag));
		setKeyAndValue(new KeyAndValue("MagRange(R)", Rrange));
		setKeyAndValue(new KeyAndValue("Mag(V)", Vmag));
		setKeyAndValue(new KeyAndValue("MagRange(V)", Vrange));
		setKeyAndValue(new KeyAndValue("Type", type));
		setKeyAndValue(new KeyAndValue("Period", period));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "EROS2 GSA J";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "EROS II Galactic Spiral Arms Variable Stars";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "EROS2 GSA";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "EROS2 GSA";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "EROS2-GSA";
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

		folder_list.addElement(name.substring(0, 2));
		folder_list.addElement(name.substring(2, 4));

		return folder_list;
	}
}
