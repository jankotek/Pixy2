/*
 * @(#)GscActStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.star;
import java.io.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;

/**
 * The <code>GscActStar</code> represents a star data in the GSC-ACT
 * CD-ROMs
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 July 21
 */

public class GscActStar extends Gsc11Star {
	/**
	 * Constructs an empty <code>GscActStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public GscActStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>GscActStar</code> with data read from CD-ROMs.
	 * @param area_numer       the area number.
	 * @param star_number      the star number in the area.
	 * @param coor             the R.A. and Decl.
	 * @param mag              the magnitude.
	 * @param position_error10 the position error in 0.1 arcsec unit.
	 * @param mag_error100     the magnitude error in 0.01 mag unit.
	 * @param band             the band number.
	 * @param class_number     the class of the star.
	 * @param plate            the plate code.
	 * @param flag             the flag of this star.
	 * @param epoch            the epoch, which can be null.
	 */
	public GscActStar ( short area_numer,
						short star_number,
						Coor coor,
						double mag,
						short position_error10,
						short mag_error100,
						byte band,
						byte class_number,
						byte[] plate,
						byte flag,
						byte[] epoch )
	{
		super();
		detailed_output = true;

		setCoor(coor);
		setMag(mag);

		this.area_number = area_numer;
		this.star_number = star_number;
		this.position_error10 = position_error10;
		this.mag_error100 = mag_error100;
		this.band = band;
		this.class_number = class_number;
		this.plate = plate;
		this.flag = flag;
		this.epoch = epoch;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Guide Star Catalog GSC-ACT";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "GSC-ACT";
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return "GSC-ACT";
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(CatalogManager.CATEGORY_STAR));
		folder_list.addElement(getCatalogFolderCode());

		int area1 = (int)area_number / 100;
		area1 *= 100;
		int area2 = (int)area_number % 100;
		int star1 = (int)star_number / 100;
		star1 *= 100;

		folder_list.addElement(String.valueOf(area1));
		folder_list.addElement(String.valueOf(area2));
		folder_list.addElement(String.valueOf(star1));

		return folder_list;
	}

	/**
	 * Gets the folder string of the star.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		int star2 = (int)star_number % 100;
		return String.valueOf(star2);
	}
}
