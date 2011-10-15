/*
 * @(#)ChsmStar.java
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
 * The <code>ChsmStar</code> represents a star data in the Carpenter, 
 * Hillenbrand, Skrutskie, Meyer's Variable Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class ChsmStar extends DefaultStar {
	/**
	 * Constructs an empty <code>ChsmStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public ChsmStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>ChsmStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param Jmag        the J magnitude.
	 * @param Hmag        the H magnitude.
	 * @param Kmag        the K magnitude.
	 * @param index       the variability index.
	 */
	public ChsmStar ( String name,
					  String coor_string,
					  String Jmag,
					  String Hmag,
					  String Kmag,
					  String index )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(J)", Jmag));
		setKeyAndValue(new KeyAndValue("Mag(H)", Hmag));
		setKeyAndValue(new KeyAndValue("Mag(K)", Kmag));
		setKeyAndValue(new KeyAndValue("Index", index));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "CHSM ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "CHSM" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Carpenter, Hillenbrand, Skrutskie, Meyer's Variable Stars";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "CHSM";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "CHSM";
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
