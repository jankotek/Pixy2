/*
 * @(#)UmStar.java
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
 * The <code>UmStar</code> represents a star data in the University
 * of Michigan Catalog of Emission-Line Objects.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class UmStar extends DefaultStar {
	/**
	 * Constructs an empty <code>UmStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public UmStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>UmStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 */
	public UmStar ( String name,
					 String coor_string )
	{
		super(name, coor_string);
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "UM ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String s = "";
		for (int i = 0 ; i < name.length() ; i++) {
			if ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9')
				s += name.charAt(i);
		}
		int number = Integer.parseInt(s);

		s = name;
		if (number < 10)
			s = "00" + s;
		else if (number < 100)
			s = "0" + s;

		return "UM" + s;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "University of Michigan Catalog of Emission-Line Objects";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "UM";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "UM";
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
