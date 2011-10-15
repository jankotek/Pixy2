/*
 * @(#)OglePeriodicStar.java
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
 * The <code>OglePeriodicStar</code> represents a star data in the OGLE-I
 * Periodic Variable Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class OglePeriodicStar extends DefaultStar {
	/**
	 * Constructs an empty <code>OglePeriodicStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public OglePeriodicStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>OglePeriodicStar</code> with data read from the
	 * catalog file.
	 * @param area        the area.
	 * @param number      the number.
	 * @param coor_string the R.A. and Decl.
	 * @param i_mag       the I magnitude.
	 * @param range       the magnitude range.
	 * @param type        the type.
	 * @param period      the period.
	 * @param epoch       the epoch.
	 * @param v_i         the V-I.
	 */
	public OglePeriodicStar ( String area,
							  String number,
							  String coor_string,
							  String i_mag,
							  String range,
							  String type,
							  String period,
							  String epoch,
							  String v_i )
	{
		super(area + "-V" + number, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(I)", i_mag));
		setKeyAndValue(new KeyAndValue("Range", range));
		setKeyAndValue(new KeyAndValue("Type", type));
		setKeyAndValue(new KeyAndValue("Period", period));
		setKeyAndValue(new KeyAndValue("Epoch", epoch));
		setKeyAndValue(new KeyAndValue("V-I", v_i));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "OGLE-";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "OGLE-I Periodic Variable Stars";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "OGLE";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "OGLE-Periodic";
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
