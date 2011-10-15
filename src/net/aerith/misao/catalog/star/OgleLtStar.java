/*
 * @(#)OgleLtStar.java
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
 * The <code>OgleLtStar</code> represents a star data in the OGLE-I
 * Long-period and Non-periodic Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class OgleLtStar extends DefaultStar {
	/**
	 * Constructs an empty <code>OgleLtStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public OgleLtStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>OgleLtStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param i_mag       the I magnitude.
	 * @param type        the type.
	 */
	public OgleLtStar ( String name,
						String coor_string,
						String i_mag,
						String type )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(I)", i_mag));
		setKeyAndValue(new KeyAndValue("Type", type));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "OGLE-LT-V";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "OGLE-I Long-period and Non-periodic Stars";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "OGLE-LT-V";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "OGLE-LT";
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
