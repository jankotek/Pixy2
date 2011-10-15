/*
 * @(#)UgcStar.java
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
 * The <code>UgcStar</code> represents a star data in the Uppsala 
 * General Catalogue of Galaxies.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class UgcStar extends DefaultStar {
	/**
	 * Constructs an empty <code>UgcStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public UgcStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>UgcStar</code> with data read from the
	 * catalog file.
	 * @param number         the number.
	 * @param coor           the R.A. and Decl.
	 * @param p_mag          the p magnitude.
	 * @param b_size         the size on B-plate.
	 * @param r_size         the size on R-plate.
	 * @param pa             the position angle.
	 * @param inclination    the inclination.
	 * @param classification the classification.
	 * @param id             the identified star.
	 */
	public UgcStar ( int number,
					 Coor coor,
					 String p_mag,
					 String b_size,
					 String r_size,
					 String pa,
					 String inclination,
					 String classification,
					 String id )
	{
		super();

		this.name = String.valueOf(number);
		setCoor(coor);
		setCoorAccuracy(Coor.ACCURACY_ARCMIN);

		setKeyAndValue(new KeyAndValue("Mag(p)", p_mag));
		setKeyAndValue(new KeyAndValue("Size(B)", b_size));
		setKeyAndValue(new KeyAndValue("Size(R)", r_size));
		setKeyAndValue(new KeyAndValue("P.A.", pa));
		setKeyAndValue(new KeyAndValue("Inclination", inclination));
		setKeyAndValue(new KeyAndValue("Class", classification));
		setKeyAndValue(new KeyAndValue("ID", id));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "UGC ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "UGC" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Uppsala General Catalogue of Galaxies";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "UGC";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "UGC";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_CLUSTER_AND_NEBULA;
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
