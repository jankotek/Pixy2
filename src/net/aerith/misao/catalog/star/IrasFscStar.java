/*
 * @(#)IrasFscStar.java
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
 * The <code>IrasFscStar</code> represents a star data in the IRAS 
 * Faint Source Catalogue.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class IrasFscStar extends IrasPscStar {
	/**
	 * Constructs an empty <code>IrasFscStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public IrasFscStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>IrasFscStar</code> with data read from the
	 * catalog file.
	 * @param number        the IRAS number.
	 * @param coor          the R.A. and Decl.
	 * @param ellipse_major the uncertainty ellipse major axis.
	 * @param ellipse_minor the uncertainty ellipse minor axis.
	 * @param ellipse_pa    the uncertainty ellipse position angle.
	 */
	public IrasFscStar ( String number,
						 Coor coor,
						 short ellipse_major,
						 short ellipse_minor,
						 short ellipse_pa )
	{
		super(number, coor, ellipse_major, ellipse_minor, ellipse_pa);
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "IRAS Faint Source Catalogue";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "IRAS F";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "IRAS FSC";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "IRAS-FSC";
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(CatalogManager.CATEGORY_INFRARED));
		folder_list.addElement(getCatalogFolderCode());

		String s = new String(number);

		folder_list.addElement(s.substring(1, 3));
		folder_list.addElement(s.substring(3, 4) + "0");
		folder_list.addElement(s.substring(6, 8) + "0");
		folder_list.addElement(s.substring(8, 9));

		return folder_list;
	}
}
