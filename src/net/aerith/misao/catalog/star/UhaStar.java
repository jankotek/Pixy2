/*
 * @(#)UhaStar.java
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
 * The <code>UhaStar</code> represents a star data in the 
 * H alpha-emission Stars in and near NGC 7000.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class UhaStar extends DefaultStar {
	/**
	 * Constructs an empty <code>UhaStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public UhaStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>UhaStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param b_mag       the B magnitude.
	 * @param v_mag       the V magnitude.
	 */
	public UhaStar ( String name,
					 String coor_string,
					 String b_mag,
					 String v_mag )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(B)", b_mag));
		setKeyAndValue(new KeyAndValue("Mag(V)", v_mag));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "UHA ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "UHA" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "H alpha-emission Stars in and near NGC 7000";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "UHA";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "UHA";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_OTHERS;
	}
}
