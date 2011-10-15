/*
 * @(#)OmhrStar.java
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
 * The <code>OmhrStar</code> represents a star data in the O. Moreau 
 * and H. Reboul UV-excess Quasar Candidates.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class OmhrStar extends DefaultStar {
	/**
	 * Constructs an empty <code>OmhrStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public OmhrStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>OmhrStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param u_mag       the U magnitude.
	 * @param v_mag       the V magnitude.
	 */
	public OmhrStar ( String name,
					  String coor_string,
					  String u_mag,
					  String v_mag )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(U)", u_mag));
		setKeyAndValue(new KeyAndValue("Mag(V)", v_mag));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "OMHR J";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "O. Moreau and H. Reboul UV-excess Quasar Candidates";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "OMHR";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "OMHR";
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

		int p = name.indexOf('+');
		folder_list.addElement(name.substring(p, p + 3));

		return folder_list;
	}
}
