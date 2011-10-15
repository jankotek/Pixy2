/*
 * @(#)Qz2Star.java
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
 * The <code>Qz2Star</code> represents a star data in the 2dF
 * QSO Redshift Survey.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Qz2Star extends DefaultStar {
	/**
	 * Constructs an empty <code>Qz2Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Qz2Star ( ) {
		super();
	}

	/**
	 * Constructs an <code>Qz2Star</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param b_mag       the B magnitude.
	 * @param type        the type.
	 * @param z           the red shift.
	 */
	public Qz2Star ( String name,
					 String coor_string,
					 String b_mag,
					 String type,
					 String z )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(B)", b_mag));
		setKeyAndValue(new KeyAndValue("Type", type));
		setKeyAndValue(new KeyAndValue("z", z));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "2QZ J";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "2dF QSO Redshift Survey";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "2QZ";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "2QZ";
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

		folder_list.addElement(name.substring(0, 2));
		folder_list.addElement(name.substring(2, 3) + "0");

		return folder_list;
	}
}
