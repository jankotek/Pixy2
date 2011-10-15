/*
 * @(#)OtherVariableStar.java
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
 * The <code>OtherVariableStar</code> represents a star data class,
 * especially of data the user adds.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 September 18
 */

public class OtherVariableStar extends OtherStar {
	/**
	 * Constructs an empty <code>OtherVariableStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public OtherVariableStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>OtherVariableStar</code>.
	 * @param name        the star name.
	 * @param vsnet_name  the star name for the VSNET.
	 * @param coor_string the string which represents R.A. and Decl.
	 */
	public OtherVariableStar ( String name,
							   String vsnet_name,
							   String coor_string )
	{
		super(name, vsnet_name, coor_string);
	}

	/**
	 * Gets the name of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Other Variable Stars";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "Variables";
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
