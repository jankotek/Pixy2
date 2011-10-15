/*
 * @(#)MisVStar.java
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
 * The <code>MisVStar</code> represents a star data in the MISAO
 * Project Variable Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class MisVStar extends DefaultVariableStar {
	/**
	 * The number.
	 */
	protected short number = 0;

	/**
	 * The GCVS name.
	 */
	protected String gcvs = null;

	/**
	 * The identified stars.
	 */
	protected byte[] id = null;

	/**
	 * The discovery date.
	 */
	protected String discovery_date = null;

	/**
	 * The discoverers.
	 */
	protected String discoverers = null;

	/**
	 * Constructs an empty <code>MisVStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public MisVStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>MisVStar</code> with data read from the
	 * catalog file.
	 * @param number the star number.
	 * @param coor   the R.A. and Decl.
	 */
	public MisVStar ( int number,
					  Coor coor )
	{
		super();
		setCoor(coor);

		this.number = (short)number;
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>R And</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String name = Format.formatIntZeroPadding((int)number, 4);
		return "MisV" + name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>ANDR</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String name = Format.formatIntZeroPadding((int)number, 4);
		return "MisV" + name;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		number = Short.parseShort(name.substring(4));
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "MISAO Project Variable Stars";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "MisV";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "MisV";
	}

	/**
	 * Gets the category of the catalog.
	 * @return the category of the catalog.
	 */
	public String getCatalogCategory ( ) {
		return CatalogManager.getCatalogCategoryName(CatalogManager.CATEGORY_VARIABLE);
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(CatalogManager.CATEGORY_VARIABLE));
		folder_list.addElement(getCatalogFolderCode());

		int no = (int)number / 100;
		no *= 100;

		folder_list.addElement(String.valueOf(no));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		int no = (int)number;
		return String.valueOf(no);
	}

	/**
	 * Gets the mean error of position in arcsec.
	 * @return the mean error of position in arcsec.
	 */
	public double getPositionErrorInArcsec ( ) {
		return 4.5;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 8.0;
	}

	/**
	 * Gets the accuracy of R.A. and Decl.
	 * @return the accuracy of R.A. and Decl.
	 */
	public byte getCoorAccuracy ( ) {
		return Coor.ACCURACY_100M_ARCSEC;
	}

	/**
	 * Sets the GCVS name.
	 * @param gcvs the GCVS name.
	 */
	public void setGcvsName ( String gcvs ) {
		this.gcvs = null;
		if (gcvs != null  &&  gcvs.length() > 0)
			this.gcvs = gcvs;
	}

	/**
	 * Sets the identified stars.
	 * @param id the identified stars.
	 */
	public void setID ( String id ) {
		this.id = null;
		if (id != null  &&  id.length() > 0) {
			try {
				this.id = id.getBytes("ASCII");
			} catch ( UnsupportedEncodingException exception ) {
				this.id = id.getBytes();
			} 
		}
	}

	/**
	 * Sets the discovery date.
	 * @param date the discovery date.
	 */
	public void setDiscoveryDate ( String date ) {
		this.discovery_date = null;
		if (date != null  &&  date.length() > 0)
			this.discovery_date = date;
	}

	/**
	 * Sets the discoverers.
	 * @param discoverers the discoverers.
	 */
	public void setDiscoverers ( String discoverers ) {
		this.discoverers = null;
		if (discoverers != null  &&  discoverers.length() > 0)
			this.discoverers = discoverers;
	}

	/**
	 * Gets the default property to plot stars.
	 * @return the default property to plot stars.
	 */
	public PlotProperty getDefaultProperty ( ) {
		PlotProperty property = new PlotProperty();
		property.setColor(Color.magenta);
		property.setFilled(false);
		property.setFixedRadius(6);
		property.setMark(PlotProperty.PLOT_RECTANGLE);
		return property;
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		KeyAndValue[] key_and_values = super.getKeyAndValues();

		Vector l = new Vector();
		for (int i = 0 ; i < key_and_values.length ; i++)
			l.addElement(key_and_values[i]);

		if (gcvs != null)
			l.addElement(new KeyAndValue("GCVS", gcvs));

		if (id != null)
			l.addElement(new KeyAndValue("ID", new String(id)));

		if (discovery_date != null)
			l.addElement(new KeyAndValue("DiscoveryDate", discovery_date));
		if (discoverers != null)
			l.addElement(new KeyAndValue("Discoverers", discoverers));

		key_and_values = new KeyAndValue[l.size()];
		for (int i = 0 ; i < l.size() ; i++)
			key_and_values[i] = (KeyAndValue)l.elementAt(i);

		return key_and_values;
	}

	/**
	 * Sets the value of the specified key.
	 * @param key_and_value the key and value to set.
	 */
	public void setKeyAndValue ( KeyAndValue key_and_value ) {
		if (key_and_value.getKey().equals("GCVS")) {
			gcvs = key_and_value.getValue();
			return;
		}
		if (key_and_value.getKey().equals("ID")) {
			try {
				id = key_and_value.getValue().getBytes("ASCII");
			} catch ( UnsupportedEncodingException exception ) {
				id = key_and_value.getValue().getBytes();
			}
			return;
		}
		if (key_and_value.getKey().equals("DiscoveryDate")) {
			discovery_date = key_and_value.getValue();
			return;
		}
		if (key_and_value.getKey().equals("Discoverers")) {
			discoverers = key_and_value.getValue();
			return;
		}

		super.setKeyAndValue(key_and_value);
	}
}
