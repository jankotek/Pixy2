/*
 * @(#)EcStar.java
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
 * The <code>EcStar</code> represents a star data in the 
 * Edinburgh-Cape Faint Blue Object Survey.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class EcStar extends DefaultStar {
	/**
	 * Constructs an empty <code>EcStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public EcStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>EcStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param v_mag       the V magnitude.
	 * @param b_v         the B-V.
	 */
	public EcStar ( String name,
					String coor_string,
					String v_mag, 
					String b_v )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(V)", v_mag));
		setKeyAndValue(new KeyAndValue("B-V", b_v));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "EC ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "EC" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Edinburgh-Cape Faint Blue Object Survey";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "EC";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "EC";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_OTHERS;
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(getCatalogCategoryNumber()));
		folder_list.addElement(getCatalogFolderCode());

		folder_list.addElement(name.substring(0, 2));

		return folder_list;
	}
}
