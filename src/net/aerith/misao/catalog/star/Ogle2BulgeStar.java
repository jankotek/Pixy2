/*
 * @(#)Ogle2BulgeStar.java
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
 * The <code>Ogle2BulgeStar</code> represents a star data in the 
 * Candidate Variable Stars from OGLE-II Bulge Data.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Ogle2BulgeStar extends DefaultStar {
	/**
	 * Constructs an empty <code>Ogle2BulgeStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Ogle2BulgeStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>Ogle2BulgeStar</code> with data read from the
	 * catalog file.
	 * @param area        the area.
	 * @param number      the number.
	 * @param coor_string the R.A. and Decl.
	 * @param i_mag       the I magnitude.
	 * @param range       the magnitude range.
	 * @param type        the type.
	 */
	public Ogle2BulgeStar ( String area,
							String number,
							String coor_string,
							String i_mag,
							String range,
							String type )
	{
		super(area + "-V" + number, coor_string);

		if (i_mag.length() > 0  &&  i_mag.equals("-") == false)
			setKeyAndValue(new KeyAndValue("Mag(I)", i_mag));
		if (range.length() > 0  &&  range.equals("-") == false)
			setKeyAndValue(new KeyAndValue("Range", range));
		setKeyAndValue(new KeyAndValue("Type", type));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "OGLE2-BUL-";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Candidate Variable Stars from OGLE-II Bulge Data";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "OGLE2-BUL";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "OGLE2-BUL";
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

		int p = name.lastIndexOf('-');
		String area = name.substring(0, p);
		String number = name.substring(p + 2);

		folder_list.addElement(area);

		int n = Integer.parseInt(number) / 100;
		n *= 100;
		folder_list.addElement(String.valueOf(n));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		int p = name.lastIndexOf('-');
		String area = name.substring(0, p);
		String number = name.substring(p + 2);

		return number;
	}
}
