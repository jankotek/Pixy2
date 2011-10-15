/*
 * @(#)MpcFormatStar.java
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
 * The <code>MpcFormatStar</code> represents a record of astrometric 
 * observation in the MPC format.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 April 29
 */

public class MpcFormatStar extends CatalogStar {
	/**
	 * The record.
	 */
	protected String record;

	/**
	 * Constructs an empty <code>MpcFormatStar</code>. It is
	 * used in <code>StarClass#newInstance</code> to review the XML
	 * data.
	 */
	public MpcFormatStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>MpcFormatStar</code> with data 
	 * read from the query result.
	 * @param record the record.
	 */
	public MpcFormatStar ( String record )
	{
		super();

		setCoor(Coor.create(record.substring(32, 55)));

		String mag = record.substring(65, 70).trim();
		if (mag.length() > 0) {
			this.mag = (float)Format.doubleValueOf(mag);
		} else {
			this.mag = (float)99.9;
		}

		this.record = record;
	}

	/**
	 * Gets the name of this star.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return record.substring(0, 13).trim();
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "MPC Format";
	}

	/**
	 * Gets the category of the catalog.
	 * @return the category of the catalog.
	 */
	public String getCatalogCategory ( ) {
		return CatalogManager.getCatalogCategoryName(CatalogManager.CATEGORY_COMET_AND_ASTEROID);
	}

	/**
	 * Returns the date if the R.A. and Decl. is date dependent. For 
	 * example, in the case of astrometric observations in the MPC 
	 * format. In general, it returns null.
	 * @return the date.
	 */
	public JulianDay getDate ( ) {
		return JulianDay.create(record.substring(15, 31));
	}

	/**
	 * Gets the default property to plot stars.
	 * @return the default property to plot stars.
	 */
	public PlotProperty getDefaultProperty ( ) {
		PlotProperty property = new PlotProperty();
		property.setColor(Color.blue);
		property.setFilled(false);
		property.setDependentSizeParameters(1.0, 0.0, 4);
		property.setMark(PlotProperty.PLOT_CIRCLE);
		return property;
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		Vector l = new Vector();

		String date = record.substring(15, 31).trim();
		l.addElement(new KeyAndValue("Date", date));

		String mag = record.substring(65, 70).trim();
		if (mag.length() > 0) {
			String mag_system = record.substring(70, 71).trim();
			if (mag_system.length() > 0) {
				l.addElement(new KeyAndValue("Mag(" + mag_system + ")", mag));
			} else {
				l.addElement(new KeyAndValue("Mag", mag));
			}
		}

		String obs_code = record.substring(77, 80).trim();
		if (obs_code.length() > 0)
			l.addElement(new KeyAndValue("Code", obs_code));

		l.addElement(new KeyAndValue("Record", record));

		KeyAndValue[] key_and_values = new KeyAndValue[l.size()];
		for (int i = 0 ; i < l.size() ; i++)
			key_and_values[i] = (KeyAndValue)l.elementAt(i);

		return key_and_values;
	}

	/**
	 * Sets the value of the specified key.
	 * @param key_and_value the key and value to set.
	 */
	public void setKeyAndValue ( KeyAndValue key_and_value ) {
		String value = key_and_value.getValue();

		if (key_and_value.getKey().equals("Record")) {
			this.record = key_and_value.getValue();
		}
	}
}
