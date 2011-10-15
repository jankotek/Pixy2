/*
 * @(#)KwbbeStar.java
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
 * The <code>KwbbeStar</code> represents a KWBBe star data.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class KwbbeStar extends DefaultStar {
	/**
	 * The object.
	 */
	private String object;

	/**
	 * The star number.
	 */
	private String number;

	/**
	 * Constructs an empty <code>KwbbeStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public KwbbeStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>KwbbeStar</code> with data read from the
	 * catalog file.
	 * @param object      the object.
	 * @param number      the star number.
	 * @param coor_string the R.A. and Decl.
	 * @param v_mag       the V magnitude.
	 */
	public KwbbeStar ( String object,
					   String number,
					   String coor_string,
					   String v_mag )
	{
		super(number, coor_string);

		this.object = object;
		this.number = number;

		setKeyAndValue(new KeyAndValue("Mag(V)", v_mag));
	}

	/**
	 * Gets the name of this star.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String s = object;
		if (s.indexOf("NGC") == 0)
			s = "NGC " + s.substring(3);

		return s + " : KWBBe " + number;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return object + ":KWBBe" + number;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		int p = name.indexOf(':');

		String s1 = name.substring(0, p).trim();
		this.object = "";
		for (int i = 0 ; i < s1.length() ; i++) {
			if (s1.charAt(i) != ' ')
				this.object += s1.charAt(i);
		}

		String s2 = name.substring(p + 1).trim();
		int q = s2.indexOf(' ');
		this.number = s2.substring(q + 1);

		this.name = this.number;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "KWBBe";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "KWBBe";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "KWBBe";
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

		folder_list.addElement(object);

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return String.valueOf(number);
	}
}
