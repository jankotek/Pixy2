/*
 * @(#)IcStar.java
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
 * The <code>IcStar</code> represents a star data of Index Catalogue
 * (IC).
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class IcStar extends CatalogStar {
	/**
	 * The number.
	 */
	protected short number = 0;

	/**
	 * The type.
	 */
	protected byte[] type = null;

	/**
	 * The size in arcmin. The -1 means no size is recorded.
	 */
	protected float size = -1;

	/**
	 * True if the size is upper limit.
	 */
	protected boolean size_limit = false;

	/**
	 * The magnitude system code.
	 */
	protected byte[] mag_system = null;

	/**
	 * Constructs an empty <code>IcStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public IcStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>IcStar</code> with data read from the
	 * catalog file.
	 * @param number the star number.
	 * @param coor   the R.A. and Decl.
	 * @param type   the type.
	 */
	public IcStar ( int number,
					Coor coor,
					String type )
	{
		super();
		setCoor(coor);

		this.number = (short)number;

		if (type != null  &&  type.length() > 0) {
			try {
				this.type = type.getBytes("ASCII");
			} catch ( UnsupportedEncodingException exception ) {
				this.type = type.getBytes();
			}
		}
	}

	/**
	 * Gets the name of this star. This method returns such a string
	 * as <tt>IC 1234</tt>.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return "IC " + number;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). This method returns such a string as 
	 * <tt>IC1234</tt>.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return "IC" + number;
	}

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		name = name.substring(2).trim();
		number = Short.parseShort(name);
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Index Catalogue";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "IC";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "IC";
	}

	/**
	 * Gets the category of the catalog.
	 * @return the category of the catalog.
	 */
	public String getCatalogCategory ( ) {
		return CatalogManager.getCatalogCategoryName(CatalogManager.CATEGORY_CLUSTER_AND_NEBULA);
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(CatalogManager.CATEGORY_CLUSTER_AND_NEBULA));
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
		return 120;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 120;
	}

	/**
	 * Gets the accuracy of R.A. and Decl.
	 * @return the accuracy of R.A. and Decl.
	 */
	public byte getCoorAccuracy ( ) {
		return Coor.ACCURACY_ARCMIN;
	}

	/**
	 * Gets the default property to plot stars.
	 * @return the default property to plot stars.
	 */
	public PlotProperty getDefaultProperty ( ) {
		PlotProperty property = new PlotProperty();
		property.setColor(Color.orange);
		property.setLineWidth(2);
		property.setFilled(false);
		property.setFixedRadius(14);
		property.setMark(PlotProperty.PLOT_CIRCLE);
		return property;
	}

	/**
	 * Sets the size.
	 * @param size  the size in arcmin.
	 * @param limit true if the size is upper limit.
	 */
	public void setSize ( float size, boolean limit ) {
		this.size = size;
		size_limit = limit;
	}

	/**
	 * Sets the magnitude system.
	 * @param mag_system the magnitude system.
	 */
	public void setMagSystem ( String mag_system ) {
		try {
			this.mag_system = mag_system.getBytes("ASCII");
		} catch ( UnsupportedEncodingException exception ) {
			this.mag_system = mag_system.getBytes();
		}
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		Vector l = new Vector();

		if (mag_system != null) {
			l.addElement(new KeyAndValue("Mag", Format.formatDouble(getMag(), 4, 2).trim()));
			l.addElement(new KeyAndValue("MagSystem", new String(mag_system)));
		}

		if (type != null)
			l.addElement(new KeyAndValue("Type", new String(type)));

		if (size >= 0) {
			String s = Format.formatDouble((double)size, 5, 3).trim() + "'";
			if (size_limit) 
				s = "<" + s;
			l.addElement(new KeyAndValue("Size", s));
		}

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
		byte[] b = null;
		try {
			b = key_and_value.getValue().getBytes("ASCII");
		} catch ( UnsupportedEncodingException exception ) {
			b = key_and_value.getValue().getBytes();
		} 

		if (key_and_value.getKey().equals("Mag")) {
			mag = Float.parseFloat(key_and_value.getValue());
		} else if (key_and_value.getKey().equals("MagSystem")) {
			mag_system = b;
		} else if (key_and_value.getKey().equals("Type")) {
			type = b;
		} else if (key_and_value.getKey().equals("Size")) {
			String value = key_and_value.getValue();

			size_limit = false;
			if (value.charAt(0) == '<') {
				size_limit = true;
				value = value.substring(1);
			}

			int len = value.length();
			size = Float.parseFloat(value.substring(0, len - 1));
		}
	}
}
