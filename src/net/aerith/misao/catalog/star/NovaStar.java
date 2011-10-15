/*
 * @(#)NovaStar.java
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
 * The <code>NovaStar</code> represents a nova data.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class NovaStar extends DefaultStar {
	/**
	 * The constellation of the nova.
	 */
	protected String constellation = "";

	/**
	 * The year when nova was discovered.
	 */
	protected int year = 0;

	/**
	 * The suffix of the nova.
	 */
	protected String suffix = "";

	/**
	 * Constructs an empty <code>NovaStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public NovaStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>NovaStar</code> with data read from the
	 * catalog file.
	 * @param constellation the constellation.
	 * @param year          the year when nova was discovered.
	 * @param suffix        the suffix of the nova.
	 * @param coor_string   the R.A. and Decl.
	 */
	public NovaStar ( String constellation,
					  int year,
					  String suffix,
					  String coor_string )
	{
		super("", coor_string);

		this.constellation = constellation;
		this.year = year;
		this.suffix = suffix;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>Nova Sgr 2000 No.1</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String s = "Nova " + constellation + " " + year;
		if (suffix.length() > 0)
			s += " No." + suffix;
		return s;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>SGRnova2000-1</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String s = constellation.toUpperCase() + "nova" + year;
		if (suffix.length() > 0)
			s += "-" + suffix;
		return s;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		StringTokenizer st = new StringTokenizer(name);
		st.nextToken();		// Nova
		constellation = st.nextToken();
		year = Integer.parseInt(st.nextToken());
		if (st.hasMoreTokens()) {
			String s = st.nextToken();
			suffix = s.substring(3);
		}
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Nova";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "Nova";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "Nova";
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
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return getVsnetName();
	}
}
