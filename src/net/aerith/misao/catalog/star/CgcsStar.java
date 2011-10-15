/*
 * @(#)CgcsStar.java
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
 * The <code>CgcsStar</code> represents a star data in the Cool 
 * Galactic Carbon Stars 2.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class CgcsStar extends DefaultStar {
	/**
	 * Constructs an empty <code>CgcsStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public CgcsStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>CgcsStar</code> with data read from the
	 * catalog file.
	 * @param name          the star name.
	 * @param coor          the R.A. and Decl.
	 * @param coor_accuracy the accuracy of R.A. and Decl.
	 * @param p_mag         the P magnitude.
	 * @param v_mag         the V magnitude.
	 * @param i_mag         the I magnitude.
	 * @param spectrum      the spectrum.
	 */
	public CgcsStar ( String name,
					  Coor coor,
					  byte coor_accuracy,
					  String p_mag,
					  String v_mag,
					  String i_mag,
					  String spectrum )
	{
		super();

		this.name = name;
		setCoor(coor);
		setCoorAccuracy(coor_accuracy);

		setKeyAndValue(new KeyAndValue("Mag(P)", p_mag));
		setKeyAndValue(new KeyAndValue("Mag(V)", v_mag));
		setKeyAndValue(new KeyAndValue("Mag(I)", i_mag));
		setKeyAndValue(new KeyAndValue("Spectrum", spectrum));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "CGCS ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "CGCS" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Cool Galactic Carbon Stars 2";
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
		return "CGCS";
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
