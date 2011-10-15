/*
 * @(#)SandStar.java
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
 * The <code>SandStar</code> represents a star data in the Sanduleak's 
 * Catalog of Probable Dwarf Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class SandStar extends DefaultStar {
	/**
	 * Constructs an empty <code>SandStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public SandStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>SandStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param v_mag       the V magnitude.
	 * @param b_v         the B-V magnitude.
	 * @param pmA         the proper motion of R.A. in msec/year.
	 * @param pmD         the proper motion of Decl. in msec/year.
	 */
	public SandStar ( String name,
					  String coor_string,
					  String v_mag,
					  String b_v,
					  String pmA, 
					  String pmD )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(V)", v_mag));
		setKeyAndValue(new KeyAndValue("B-V", b_v));

		if (pmA.trim().length() > 0) {
			double pm = (double)Format.intValueOf(pmA) / 1000.0;
			String s = Format.formatDouble(pm, 8, 3).trim() + "\"/year";
			setKeyAndValue(new KeyAndValue("ProperMotion(R.A.)", s));
		}
		if (pmD.trim().length() > 0) {
			double pm = (double)Format.intValueOf(pmD) / 1000.0;
			String s = Format.formatDouble(pm, 8, 3).trim() + "\"/year";
			setKeyAndValue(new KeyAndValue("ProperMotion(Decl.)", s));
		}
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "Sand ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "Sand" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Sanduleak's Catalog of Probable Dwarf Stars";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "Sand";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "Sand";
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

		int number = Integer.parseInt(name) / 100;
		number *= 100;

		folder_list.addElement(String.valueOf(number));

		return folder_list;
	}
}
