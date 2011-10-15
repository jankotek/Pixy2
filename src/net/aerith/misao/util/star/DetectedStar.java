/*
 * @(#)DetectedStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util.star;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.catalog.*;

/**
 * The <code>DetectedStar</code> is a wrapper catalog star class of
 * the <code>StarImage</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 September 18
 */

public class DetectedStar extends CatalogStar {
	/**
	 * The astrometric error in arcsec.
	 */
	protected double astrometric_error = 0.0;

	/**
	 * Constructs an empty <code>DetectedStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	 public DetectedStar ( ) {
		super();
	 }

	/**
	 * Constructs a <code>DetectedStar</code>.
	 * @param star_image the <code>StarImage</code> object.
	 * @param pos_error  the astrometric error in arcsec.
	 */
	 public DetectedStar ( StarImage star_image, double pos_error ) {
		super();
		setCoor(star_image.getCoor());
		setMag(star_image.getMag());

		astrometric_error = pos_error;
	 }

	/**
	 * Constructs a <code>DetectedStar</code>.
	 * @param detected_star the <code>DetectedStar</code> object.
	 */
	 public DetectedStar ( DetectedStar detected_star ) {
		super();
		setCoor(detected_star.getCoor());
		setMag(detected_star.getMag());

		this.astrometric_error = detected_star.astrometric_error;
	 }

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
		// The name must be like "J123456.78+123456.7".
		String s = name.substring(1, 10) + " " + name.substring(10);
		setCoor(Coor.create(s));
	}

	/**
	 * Gets the name of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Detected Star";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "detected";
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

		String s = getName();
		folder_list.addElement(s.substring(1, 3));
		folder_list.addElement(s.substring(3, 5));
		folder_list.addElement(s.substring(5, 7));
		folder_list.addElement(s.substring(10, 13));
		folder_list.addElement(s.substring(13, 15));
		folder_list.addElement(s.substring(15, 17));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return getName();
	}

	/**
	 * Gets the mean error of position in arcsec.
	 * @return the mean error of position in arcsec.
	 */
	public double getPositionErrorInArcsec ( ) {
		return astrometric_error;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		double pos_error = astrometric_error * 1.5;
		if (pos_error < 5.0)
			pos_error = 5.0;
		return pos_error;
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		KeyAndValue[] key_and_values = new KeyAndValue[2];
		key_and_values[0] = new KeyAndValue("Mag", Format.formatDouble(getMag(), 5, 2));
		key_and_values[1] = new KeyAndValue("Error(arcsec)", Format.formatDouble(astrometric_error, 7, 4).trim());
		return key_and_values;
	}

	/**
	 * Sets the value of the specified key.
	 * @param key_and_value the key and value to set.
	 */
	public void setKeyAndValue ( KeyAndValue key_and_value ) {
		if (key_and_value.getKey().equals("Mag")) {
			setMag(Double.parseDouble(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("Error(arcsec)")) {
			astrometric_error = Double.parseDouble(key_and_value.getValue());
		}
	}
}
