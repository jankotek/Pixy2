/*
 * @(#)IfmStar.java
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
 * The <code>IfmStar</code> represents a star data in the IFM 
 * Catalog of Supergiants in M33.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class IfmStar extends DefaultStar {
	/**
	 * Constructs an empty <code>IfmStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public IfmStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>IfmStar</code> with data read from the
	 * catalog file.
	 * @param category    the star category.
	 * @param number      the star number.
	 * @param coor_string the R.A. and Decl.
	 * @param v_mag       the V magnitude.
	 * @param b_v         the B-V.
	 */
	public IfmStar ( String category,
					 String number,
					 String coor_string,
					 String v_mag,
					 String b_v )
	{
		super(category + " " + number, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(V)", v_mag));
		setKeyAndValue(new KeyAndValue("B-V", b_v));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "IFM-";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		int num_count = 0;
		for (int i = 2 ; i < name.length() ; i++) {
			if ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9')
				num_count++;
		}

		String s = name.substring(2);
		if (name.charAt(0) == 'B') {
			while (num_count < 4) {
				s = "0" + s;
				num_count++;
			}
		}

		return "IFM-" + name.substring(0, 1) + s;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "IFM Catalog of Supergiants in M33";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "IFM";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "IFM";
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

		folder_list.addElement(name.substring(0, 1));

		if (name.charAt(0) == 'B') {
			String s = "";
			for (int i = 2 ; i < name.length() ; i++) {
				if ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9')
					s = s + name.charAt(i);
			}

			int number = Integer.parseInt(s) / 100;
			number *= 100;
			folder_list.addElement(String.valueOf(number));
		}

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return name.substring(2);
	}
}
