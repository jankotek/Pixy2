/*
 * @(#)Cks91Star.java
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
 * The <code>Cks91Star</code> represents a star data in the [CKS91] 
 * Cepheids and Other Variables in Crux and Centaurus.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Cks91Star extends DefaultStar {
	/**
	 * Constructs an empty <code>Cks91Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Cks91Star ( ) {
		super();
	}

	/**
	 * Constructs an <code>Cks91Star</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param i_mag       the I magnitude.
	 * @param range       the magnitude range.
	 * @param type        the type.
	 */
	public Cks91Star ( String name,
					   String coor_string,
					   String i_mag,
					   String range,
					   String type )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(I)", i_mag));
		setKeyAndValue(new KeyAndValue("Range", range));
		setKeyAndValue(new KeyAndValue("Type", type));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "[CKS91] ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "[CKS91]" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "[CKS91] Cepheids and Other Variables in Crux and Centaurus";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "[CKS91]";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "[CKS91]";
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
