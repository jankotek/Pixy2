/*
 * @(#)OtherStar.java
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
 * The <code>OtherStar</code> represents a star data class, especially
 * of data the user adds.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 September 18
 */

public class OtherStar extends DefaultStar {
	/**
	 * Constructs an empty <code>OtherStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public OtherStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>OtherStar</code>.
	 * @param name        the star name.
	 * @param vsnet_name  the star name for the VSNET.
	 * @param coor_string the string which represents R.A. and Decl.
	 */
	public OtherStar ( String name,
					   String vsnet_name,
					   String coor_string )
	{
		super(name, coor_string);

		if (vsnet_name.equals(super.getVsnetName()) == false)
			setKeyAndValue(new KeyAndValue("VSNET-Name", vsnet_name));
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses. This method returns the name itself. The name 
	 * cannot have any space characters.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		KeyAndValue[] key_and_values = getKeyAndValues();
		for (int i = 0 ; i < key_and_values.length ; i++) {
			if (key_and_values[i].getKey().equals("VSNET-Name"))
				return key_and_values[i].getValue();
		}
		return super.getVsnetName();
	}

	/**
	 * Gets the name of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Others";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "Others";
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
