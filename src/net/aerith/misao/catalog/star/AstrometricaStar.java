/*
 * @(#)AstrometricaStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.star;
import java.util.Vector;
import java.awt.Color;
import net.aerith.misao.util.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.gui.PlotProperty;

/**
 * The <code>AstrometricaStar</code> represents a star data with the 
 * name, R.A. and Decl., and the magnitude.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 6
 */

public class AstrometricaStar extends UserStar {
	/**
	 * Constructs an empty <code>AstrometricaStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public AstrometricaStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>AstrometricaStar</code> with initial data.
	 * @param name the name, which can be null.
	 * @param coor the R.A. and Decl.
	 * @param mag  the magnitude.
	 */
	public AstrometricaStar ( String name,
							  Coor coor,
							  double mag )
	{
		super(name, coor, mag);
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Astrometrica Other(ASCII) Star Catalog";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "Astrometrica";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "Astrometrica";
	}

	/**
	 * Gets the category of the catalog.
	 * @return the category of the catalog.
	 */
	public String getCatalogCategory ( ) {
		return CatalogManager.getCatalogCategoryName(CatalogManager.CATEGORY_OTHERS);
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(CatalogManager.CATEGORY_OTHERS));
		folder_list.addElement(getCatalogFolderCode());

		return folder_list;
	}

	/**
	 * Gets the default property to plot stars.
	 * @return the default property to plot stars.
	 */
	public PlotProperty getDefaultProperty ( ) {
		PlotProperty property = new PlotProperty();
		property.setColor(Color.blue);
		property.setFilled(false);
		property.setDependentSizeParameters(1.0, 0.0, 1);
		property.setMark(PlotProperty.PLOT_CIRCLE);
		return property;
	}
}
