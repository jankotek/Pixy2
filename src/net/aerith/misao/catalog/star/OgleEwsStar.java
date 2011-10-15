/*
 * @(#)OgleEwsStar.java
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
 * The <code>OgleEwsStar</code> represents a star data in the OGLE-II
 * Real Time Microlensing Early Warnin System
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class OgleEwsStar extends DefaultStar {
	/**
	 * Constructs an empty <code>OgleEwsStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public OgleEwsStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>OgleEwsStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param i_mag       the I magnitude.
	 * @param Dmag        the Dmag.
	 * @param Tmax        the Tmax.
	 * @param tau         the tau.
	 * @param Amax        the Amax.
	 */
	public OgleEwsStar ( String name,
						 String coor_string,
						 String i_mag,
						 String Dmag,
						 String Tmax,
						 String tau,
						 String Amax )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag(I)", i_mag));
		setKeyAndValue(new KeyAndValue("Dmag", Dmag));
		setKeyAndValue(new KeyAndValue("Tmax", Tmax));
		setKeyAndValue(new KeyAndValue("tau", tau));
		setKeyAndValue(new KeyAndValue("Amax", Amax));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "OGLE ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "OGLE-" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "OGLE-II Real Time Microlensing Early Warnin System";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "OGLE";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "OGLE-EWS";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_VARIABLE;
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(getCatalogCategoryNumber()));
		folder_list.addElement(getCatalogFolderCode());

		folder_list.addElement(name.substring(0, 4));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return name.substring(5);
	}
}
