/*
 * @(#)Fastt2Star.java
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
 * The <code>Fastt2Star</code> represents a star data in the New 
 * Variables in the SDSS Calibration Fields (FASTT-2).
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Fastt2Star extends DefaultStar {
	/**
	 * Constructs an empty <code>Fastt2Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Fastt2Star ( ) {
		super();
	}

	/**
	 * Constructs an <code>Fastt2Star</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param r_mag       the R magnitude.
	 * @param range       the magnitude range.
	 */
	public Fastt2Star ( String name,
						String coor_string,
						String r_mag,
						String range )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(R)", r_mag));
		setKeyAndValue(new KeyAndValue("Range", range));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "FASTT2-";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "New Variables in the SDSS Calibration Fields (FASTT-2)";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "FASTT2";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "FASTT-2";
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

		int number = Integer.parseInt(name) / 100;
		number *= 100;

		folder_list.addElement("" + number);

		return folder_list;
	}
}
