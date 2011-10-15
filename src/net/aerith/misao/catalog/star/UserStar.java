/*
 * @(#)UserStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.star;
import java.io.*;
import java.util.Vector;
import java.awt.Color;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.PlotProperty;

/**
 * The <code>UserStar</code> represents a star data with the name, 
 * R.A. and Decl., and the magnitude.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 6
 */

public class UserStar extends CatalogStar {
	/**
	 * The name.
	 */
	protected String name = null;

	/**
	 * Constructs an empty <code>UserStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public UserStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>UserStar</code> with initial data.
	 * @param name the name, which can be null.
	 * @param coor the R.A. and Decl.
	 * @param mag  the magnitude.
	 */
	public UserStar ( String name,
					  Coor coor,
					  double mag )
	{
		super();
		setCoor(coor);
		setMag(mag);

		this.name = name;
	}

	/**
	 * Gets the name of this star. If no proper name is specified by 
	 * the user, it returns such a name as <tt>J123456.78+123456.7</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		if (name == null  ||  name.length() == 0) {
			return super.getName();
		}

		return name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		this.name = name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "User's Star Catalog";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "User's";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "Users";
	}

	/**
	 * Gets the category of the catalog.
	 * @return the category of the catalog.
	 */
	public String getCatalogCategory ( ) {
		return CatalogManager.getCatalogCategoryName(CatalogManager.CATEGORY_STAR);
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(CatalogManager.CATEGORY_STAR));
		folder_list.addElement(getCatalogFolderCode());

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		String s = getName();
		return s.replace('"', '-').replace('|', '-').replace('*', '-').replace('?', '-').replace('<', '-').replace('>', '-').replace('/', '-').replace('\\', '-').replace(':', '-');
	}

	/**
	 * Returns true if the catalog contains magnitude data.
	 * @return true if the catalog contains magnitude data.
	 */
	public boolean supportsMagnitude ( ) {
		return true;
	}

	/**
	 * Returns true if the catalog contains accurate R.A. and Decl.
	 * enough for astrometry.
	 * @return true if the catalog contains accurate R.A. and Decl.
	 * enough for astrometry.
	 */
	public boolean supportsAstrometry ( ) {
		return true;
	}

	/**
	 * Returns true if the catalog description of astrometry and 
	 * photometry is edittable. If not, the description is the catalog
	 * name.
	 * @return true if the catalog description of astrometry and 
	 * photometry is edittable.
	 */
	public boolean isDescriptionEdittable ( ) {
		return true;
	}

	/**
	 * Gets the list of magnitude systems supported by this catalog.
	 * @return the list of magnitude systems supported by this catalog.
	 */
	public String[] getAvailableMagnitudeSystems ( ) {
		String[] systems = new String[1];
		systems[0] = "";
		return systems;
	}

	/**
	 * Gets the magnitude of the specified system.
	 * @param system the magnitude system.
	 * @return the magnitude of the specified system.
	 * @exception UnsupportedMagnitudeSystemException if the specified
	 * magnitude system is not supported.
	 */
	public double getMagnitude ( String system )
		throws UnsupportedMagnitudeSystemException
	{
		if (system.length() == 0)
			return getMag();

		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the magnitude string of the specified system to output.
	 * @param system the magnitude system.
	 * @return the magnitude string of the specified system.
	 * @exception UnsupportedMagnitudeSystemException if the specified
	 * magnitude system is not supported.
	 */
	public String getMagnitudeString ( String system )
		throws UnsupportedMagnitudeSystemException
	{
		double mag = getMagnitude(system);
		return Format.formatDouble(mag, 5, 2).trim();
	}

	/**
	 * Gets the default property to plot stars.
	 * @return the default property to plot stars.
	 */
	public PlotProperty getDefaultProperty ( ) {
		PlotProperty property = new PlotProperty();
		property.setColor(Color.green);
		property.setFilled(false);
		property.setDependentSizeParameters(1.0, 0.0, 1);
		property.setMark(PlotProperty.PLOT_CIRCLE);
		return property;
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		KeyAndValue[] key_and_values = new KeyAndValue[1];
		key_and_values[0] = new KeyAndValue("Mag", Format.formatDouble(getMag(), 5, 2).trim());
		return key_and_values;
	}

	/**
	 * Sets the value of the specified key.
	 * @param key_and_value the key and value to set.
	 */
	public void setKeyAndValue ( KeyAndValue key_and_value ) {
		if (key_and_value.getKey().equals("Mag"))
			setMag(Double.parseDouble(key_and_value.getValue()));
	}

	/**
	 * Gets an array of keys and values related to the photometry.
	 * @return an array of keys and values related to the photometry.
	 */
	public KeyAndValue[] getKeyAndValuesForPhotometry ( ) {
		return getKeyAndValues();
	}
}
