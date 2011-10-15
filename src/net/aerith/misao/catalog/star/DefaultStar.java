/*
 * @(#)DefaultStar.java
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
 * The <code>DefaultStar</code> represents a base star data class of a
 * default star. It has a list of keys and values.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class DefaultStar extends CatalogStar {
	/**
	 * The name of the star.
	 */
	protected String name;

	/**
	 * The list of keys and values.
	 */
	protected Vector list = new Vector();

	/**
	 * The accuracy of R.A. and Decl.
	 */
	protected byte coor_accuracy = Coor.ACCURACY_100M_ARCSEC;

	/**
	 * Constructs an empty <code>DefaultStar</code>. It is only for
	 * the subclasses.
	 */
	protected DefaultStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>DefaultStar</code>.
	 * @param name        the star name.
	 * @param coor_string the string which represents R.A. and Decl.
	 */
	protected DefaultStar ( String name,
							String coor_string )
	{
		super();

		this.name = name;

		setCoor(Coor.create(coor_string));
		setCoorAccuracy(Coor.getAccuracy(coor_string));
	}

	/**
	 * Gets the prefix of the name. It must be overrided in the
	 * subclasses.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "";
	}

	/**
	 * Gets the name of this star.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return getNamePrefix() + name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		int l = getNamePrefix().length();
		if (name.length() >= l  &&  name.substring(0, l).equals(getNamePrefix()))
			this.name = name.substring(l);
		else
			this.name = name;
	}

	/**
	 * Gets the accuracy of R.A. and Decl.
	 * @return the accuracy of R.A. and Decl.
	 */
	public byte getCoorAccuracy ( ) {
		return coor_accuracy;
	}

	/**
	 * Sets the accuracy of R.A. and Decl.
	 * @param accuracy the accuracy of R.A. and Decl.
	 */
	public void setCoorAccuracy ( byte accuracy ) {
		coor_accuracy = accuracy;
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
	 * Gets the category of the catalog.
	 * @return the category of the catalog.
	 */
	public String getCatalogCategory ( ) {
		return CatalogManager.getCatalogCategoryName(getCatalogCategoryNumber());
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(getCatalogCategoryNumber()));
		folder_list.addElement(getCatalogFolderCode());

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return name.replace('"', '-').replace('|', '-').replace('*', '-').replace('?', '-').replace('<', '-').replace('>', '-').replace('/', '-').replace('\\', '-').replace(':', '-');
	}

	/**
	 * Sets the data.
	 * @param data the data.
	 */
	public void setData ( String data ) {
		setKeyAndValue(new KeyAndValue("Data", data.trim()));
	}

	/**
	 * Gets the mean error of position in arcsec.
	 * @return the mean error of position in arcsec.
	 */
	public double getPositionErrorInArcsec ( ) {
		if (coor_accuracy == Coor.ACCURACY_ROUGH_ARCSEC)
			return 15.0;
		if (coor_accuracy == Coor.ACCURACY_100M_ARCMIN)
			return 15.0;
		if (coor_accuracy == Coor.ACCURACY_100M_ARCMIN_HOURSEC)
			return 25.0;
		if (coor_accuracy == Coor.ACCURACY_ARCMIN)
			return 60.0;
		return 3.0;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		if (coor_accuracy == Coor.ACCURACY_ROUGH_ARCSEC)
			return 30.0;
		if (coor_accuracy == Coor.ACCURACY_100M_ARCMIN)
			return 30.0;
		if (coor_accuracy == Coor.ACCURACY_100M_ARCMIN_HOURSEC)
			return 30.0;
		if (coor_accuracy == Coor.ACCURACY_ARCMIN)
			return 90.0;
		return 10.0;
	}

	/**
	 * Gets the default property to plot stars.
	 * @return the default property to plot stars.
	 */
	public PlotProperty getDefaultProperty ( ) {
		PlotProperty property = new PlotProperty();
		if (getCatalogCategoryNumber() == CatalogManager.CATEGORY_VARIABLE)
			property.setColor(Color.magenta);
		else if (getCatalogCategoryNumber() == CatalogManager.CATEGORY_CLUSTER_AND_NEBULA)
			property.setColor(Color.orange);
		else
			property.setColor(Color.blue);
		property.setFilled(false);
		property.setFixedRadius(6);
		property.setMark(PlotProperty.PLOT_CIRCLE);
		return property;
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		KeyAndValue[] key_and_values = new KeyAndValue[list.size()];
		for (int i = 0 ; i < list.size() ; i++)
			key_and_values[i] = (KeyAndValue)list.elementAt(i);

		return key_and_values;
	}

	/**
	 * Sets the value of the specified key.
	 * @param key_and_value the key and value to set.
	 */
	public void setKeyAndValue ( KeyAndValue key_and_value ) {
		for (int i = 0 ; i < list.size() ; i++) {
			KeyAndValue kv = (KeyAndValue)list.elementAt(i);
			if (kv.getKey().equals(key_and_value.getKey())) {
				list.removeElementAt(i);
				if (key_and_value.getValue() != null  &&  key_and_value.getValue().length() > 0)
					list.addElement(key_and_value);
				return;
			}
		}

		if (key_and_value.getValue() != null  &&  key_and_value.getValue().length() > 0)
			list.addElement(key_and_value);
	}
}
