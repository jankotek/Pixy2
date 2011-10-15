/*
 * @(#)SupernovaStar.java
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
 * The <code>SupernovaStar</code> represents a star data in the List 
 * of Supernovae.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class SupernovaStar extends DefaultStar {
	/**
	 * The discovery year.
	 */
	protected int year = 0;

	/**
	 * The sequential number in the year.
	 */
	protected String number = "";

	/**
	 * Constructs an empty <code>SupernovaStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public SupernovaStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>SupernovaStar</code> with data read from the
	 * catalog file.
	 * @param year        the discovery year.
	 * @param number      the sequential number in the year.
	 * @param coor_string the R.A. and Decl.
	 */
	public SupernovaStar ( int year,
						   String number,
						   String coor_string )
	{
		super(String.valueOf(year) + number, coor_string);

		this.year = year;
		this.number = number;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		name = name.substring(2).trim();
		int i = 0;
		for (i = 0 ; i < name.length() ; i++) {
			if (! ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9'))
				break;
		}
		year = Integer.parseInt(name.substring(0, i));
		number = name.substring(i);

		this.name = String.valueOf(year) + number;
	}

	/**
	 * Sets the host galaxy.
	 * @param host the host galaxy.
	 */
	public void setHostGalaxy ( String host ) {
		setKeyAndValue(new KeyAndValue("Host", host));
	}

	/**
	 * Sets the discovery date.
	 * @param date the discovery date.
	 */
	public void setDiscoveryDate ( String date ) {
		setKeyAndValue(new KeyAndValue("Discovery", date));
	}

	/**
	 * Sets the magnitude at the discovery.
	 * @param mag the magnitude at the discovery.
	 */
	public void setDiscoveryMagnitude ( String mag ) {
		setKeyAndValue(new KeyAndValue("Mag(Discovery)", mag));
	}

	/**
	 * Sets the reference.
	 * @param reference the reference.
	 */
	public void setReference ( String reference ) {
		setKeyAndValue(new KeyAndValue("Reference", reference));
	}

	/**
	 * Sets the type.
	 * @param type the type.
	 */
	public void setType ( String type ) {
		setKeyAndValue(new KeyAndValue("Type", type));
	}

	/**
	 * Sets the discoverers.
	 * @param discoverers the discoverers.
	 */
	public void setDiscoverers ( String discoverers ) {
		setKeyAndValue(new KeyAndValue("Discoverers", discoverers));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "SN ";
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "SN" + name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Supernova";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "SN";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "Supernova";
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

		if (name.length() >= 4)
			folder_list.addElement(name.substring(0, 4));
		else
			folder_list.addElement(name);

		return folder_list;
	}
}
