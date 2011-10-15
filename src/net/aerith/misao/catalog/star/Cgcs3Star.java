/*
 * @(#)Cgcs3Star.java
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
 * The <code>Cgcs3Star</code> represents a star data in the General 
 * Catalog of Galactic Carbon Stars 3d Edition.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class Cgcs3Star extends DefaultStar {
	/**
	 * Constructs an empty <code>Cgcs3Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Cgcs3Star ( ) {
		super();
	}

	/**
	 * Constructs an <code>Cgcs3Star</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param cgcs        the CGCS number.
	 * @param b_mag       the B magnitude.
	 * @param v_mag       the V magnitude.
	 * @param ir_mag      the IR magnitude.
	 */
	public Cgcs3Star ( String name,
					   String coor_string,
					   int cgcs,
					   String b_mag,
					   String v_mag,
					   String ir_mag )
	{
		super(name, coor_string);

		if (b_mag.length() > 0)
			setKeyAndValue(new KeyAndValue("Mag(B)", b_mag));
		if (v_mag.length() > 0)
			setKeyAndValue(new KeyAndValue("Mag(V)", v_mag));
		if (ir_mag.length() > 0)
			setKeyAndValue(new KeyAndValue("Mag(IR)", ir_mag));

		setKeyAndValue(new KeyAndValue("ID", "CGCS " + cgcs));
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>CGCS J0123+0123</tt> or <tt>CGCS J0123+0123 (CGCS 123)</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		int cgcs_number = getCgcsNumber();

		String s = "CGCS J" + name;

		if (isCgcsNumberRequired(cgcs_number))
			s += " (CGCS " + cgcs_number + ")";

		return s;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>CGCS_J0123+0123</tt> or <tt>CGCS_J0123+0123(CGCS123)</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		int cgcs_number = getCgcsNumber();

		String s = "CGCS_J" + name;

		if (isCgcsNumberRequired(cgcs_number))
			s += "(CGCS" + cgcs_number + ")";

		return s;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		int p = name.indexOf(" (");
		if (p > 0)
			this.name = name.substring(6, p);
		else
			this.name = name.substring(6);
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "CGCS J";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "General Catalog of Galactic Carbon Stars 3d Edition";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "CGCS";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "CGCS3";
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
		folder_list.addElement(name.substring(4, 6) + "0");

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		int cgcs_number = getCgcsNumber();
		if (isCgcsNumberRequired(cgcs_number))
			return name + "_CGCS" + cgcs_number;

		return name;
	}

	/**
	 * Gets the CGCS number.
	 * @return the CGCS number.
	 */
	private int getCgcsNumber ( ) {
		int cgcs_number = 0;

		KeyAndValue[] key_and_values = getKeyAndValues();
		for (int i = 0 ; i < key_and_values.length ; i++) {
			if (key_and_values[i].getKey().equals("ID"))
				cgcs_number = Integer.parseInt(key_and_values[i].getValue().substring(5));
		}

		return cgcs_number;
	}

	/**
	 * Returns true when the CGCS number is required to identify.
	 * @param cgcs_number the CGCS number.
	 * @return true when the CGCS number is required to identify.
	 */
	private static boolean isCgcsNumberRequired ( int cgcs_number ) {
		if (cgcs_number == 155  ||  cgcs_number == 159  ||  
			cgcs_number == 229  ||  cgcs_number == 231  ||  
			cgcs_number == 395  ||  cgcs_number == 396  ||  
			cgcs_number == 946  ||  cgcs_number == 951  ||  
			cgcs_number == 6120  ||  cgcs_number == 1193  ||  
			cgcs_number == 1781  ||  cgcs_number == 1764  ||  
			cgcs_number == 1834  ||  cgcs_number == 1836  ||  
			cgcs_number == 2006  ||  cgcs_number == 2008  ||  
			cgcs_number == 2846  ||  cgcs_number == 6410  ||  
			cgcs_number == 2909  ||  cgcs_number == 2911  ||  
			cgcs_number == 3250  ||  cgcs_number == 3253  ||  
			cgcs_number == 3338  ||  cgcs_number == 6511  ||  
			cgcs_number == 3342  ||  cgcs_number == 6514  ||  
			cgcs_number == 3401  ||  cgcs_number == 3402  ||  
			cgcs_number == 6590  ||  cgcs_number == 6591  ||  
			cgcs_number == 3777  ||  cgcs_number == 3778  ||  
			cgcs_number == 4091  ||  cgcs_number == 4096  ||  
			cgcs_number == 4269  ||  cgcs_number == 4271  ||  
			cgcs_number == 6789  ||  cgcs_number == 4305  ||  
			cgcs_number == 4383  ||  cgcs_number == 4388  ||  
			cgcs_number == 6827  ||  cgcs_number == 4448  ||  
			cgcs_number == 4493  ||  cgcs_number == 6839  ||  
			cgcs_number == 4511  ||  cgcs_number == 6844  ||  
			cgcs_number == 4530  ||  cgcs_number == 4534  ||  
			cgcs_number == 6874  ||  cgcs_number == 5026  ||  
			cgcs_number == 5117  ||  cgcs_number == 5118  ||  
			cgcs_number == 5327  ||  cgcs_number == 5332  ||  
			cgcs_number == 5539  ||  cgcs_number == 5541  ||  
			cgcs_number == 5970  ||  cgcs_number == 5972)
			return true;

		return false;
	}
}
