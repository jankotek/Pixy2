/*
 * @(#)Ctt83Star.java
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
 * The <code>Ctt83Star</code> represents a star data in the [CTT83] 
 * Faint Red Objects
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Ctt83Star extends DefaultStar {
	/**
	 * Constructs an empty <code>Ctt83Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Ctt83Star ( ) {
		super();
	}

	/**
	 * Constructs an <code>Ctt83Star</code> with data read from the
	 * catalog file.
	 * @param name        the name.
	 * @param coor_string the R.A. and Decl.
	 * @param r_mag       the r magnitude.
	 */
	public Ctt83Star ( String name,
					   String coor_string,
					   String r_mag )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(r)", r_mag));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "[CTT83] ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "[CTT83]" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "[CTT83] Faint Red Objects";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "[CTT83]";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "[CTT83]";
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

		int number = Integer.parseInt(name) / 100;
		number *= 100;

		folder_list.addElement(String.valueOf(number));

		return folder_list;
	}
}
