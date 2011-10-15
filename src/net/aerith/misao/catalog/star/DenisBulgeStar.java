/*
 * @(#)DenisBulgeStar.java
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
 * The <code>DenisBulgeStar</code> represents a star data in the DENIS
 * and ISOGAL Variable Star Candidates in the Galactic Bulge.
 * New Variable Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class DenisBulgeStar extends DefaultStar {
	/**
	 * Constructs an empty <code>DenisBulgeStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public DenisBulgeStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>DenisBulgeStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param mag         the magnitude.
	 * @param range       the magnitude range.
	 */
	public DenisBulgeStar ( String name,
							String coor_string,
							String mag,
							String range )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag", mag));
		setKeyAndValue(new KeyAndValue("Range", range));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "DENIS Bulge ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses. 
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "DENIS_Bulge" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "DENIS and ISOGAL Variable Star Candidates in the Galactic Bulge";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "DENIS Bulge";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "DENIS Bulge";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "DENIS_Bulge";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_VARIABLE;
	}
}
