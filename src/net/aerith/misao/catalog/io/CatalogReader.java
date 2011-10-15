/*
 * @(#)CatalogReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import java.net.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.io.CdromNotFoundException;

/**
 * The <code>CatalogReader</code> is an abstract class of catalog 
 * reader.
 * <p>
 * R.A. and Decl. of the center, and field of view are required. In 
 * order to read all stars in the specified area, invoke
 * <code>read</code> method. In order to read stars in the specified
 * area one by one, invoke <code>open</code> method at first, then 
 * invoke <code>readNext</code> method repeatedly until it returns 
 * null. Finally invoke <code>close</code> method.
 * <p>
 * The (x,y) position must be set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 September 13
 */

public abstract class CatalogReader {
	/**
	 * The set of urls where to read data. In principle, one URL is
	 * set in the constructor.
	 */
	protected URLSet url_set = new URLSet();

	/**
	 * The R.A. and Decl. of the center to read.
	 */
	protected Coor center_coor;

	/**
	 * The limiting magnitude to read.
	 */
	protected double limiting_mag = 99999.9;

	/**
	 * The date.
	 */
	protected JulianDay date = new JulianDay();

	/**
	 * Sets the default catalog path. This method must be invoked in
	 * the constructor of the subclasses.
	 */
	protected void setDefaultURL ( ) {
		String default_path = Resource.getDefaultCatalogPath(getName());
		String[] paths = Format.separatePath(default_path);
		for (int i = 0 ; i < paths.length ; i++) {
			try {
				url_set.addURL(new File(paths[i]).toURL());
			} catch ( MalformedURLException exception ) {
				System.err.println(exception);
			}
		}
	}

	/**
	 * Adds URL where to read data.
	 * @param new_url the new URL to read data.
	 */
	public void addURL ( URL new_url ) {
		url_set.addURL(new_url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public abstract String getName ( );

	/**
	 * Checks if the catalog data is in a directory.
	 * @return true if the catalog data is in a directory.
	 */
	public boolean isInDirectory ( ) {
		return false;
	}

	/**
	 * Checks if the catalog data is a file.
	 * @return true if the catalog data is a file.
	 */
	public boolean isFile ( ) {
		return false;
	}

	/**
	 * Checks if the catalog supports the use in PIXY examination.
	 * @return true if the catalog can be used in PIXY examination.
	 */
	public boolean supportsExamination ( ) {
		return false;
	}

	/**
	 * Checks if the catalog depends on the date.
	 * @return true if the catalog depends on the date.
	 */
	public boolean isDateDependent ( ) {
		return false;
	}

	/**
	 * Sets the date.
	 * @param date the date.
	 */
	public void setDate ( JulianDay date ) {
		this.date = date;
	}

	/**
	 * Returns true if the reader has limit on the field of view.
	 * @return true if the reader has limit on the field of view.
	 */
	public boolean hasFovLimit ( ) {
		return false;
	}

	/**
	 * Gets the limit on the field of view.
	 * @return the limit on the field of view in degree.
	 */
	public double getFovLimit ( ) {
		return 0.0;
	}

	/**
	 * Gets the message for limit on the field of view.
	 * @return the message for limit on the field of view.
	 */
	public String getFovLimitMessage ( ) {
		return "";
	}

	/**
	 * Returns true if the reader has limit on the date.
	 * @return true if the reader has limit on the date.
	 */
	public boolean hasDateLimit ( ) {
		return false;
	}

	/**
	 * Gets the limit on the date.
	 * @return the limit on the date in day.
	 */
	public double getDateLimit ( ) {
		return 0.0;
	}

	/**
	 * Gets the message for limit on the date.
	 * @return the message for limit on the date.
	 */
	public String getDateLimitMessage ( ) {
		return "";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 180.0;
	}

	/**
	 * Gets the help message.
	 * @return the help message.
	 */
	public String getHelpMessage ( ) {
		return null;
	}

	/**
	 * Sets the limiting magnitude to read.
	 * @param new_limiting_mag the new limiting magnitude.
	 */
	public void setLimitingMagnitude ( double new_limiting_mag ) {
		limiting_mag = new_limiting_mag;
	}

	/**
	 * Opens a catalog to read all star data. This method must be 
	 * invoked at first.
	 * @exception IOException if a file cannot be accessed.
	 * @exception FileNotFoundException if a file does not exists in
	 * any URL.
	 * @exception CdromNotFoundException if this reader is to read 
	 * data from CD-ROMs and a file does not exists in any URL.
	 */
	public void open ( )
		throws IOException, FileNotFoundException, CdromNotFoundException
	{
		open(null, 0);
	}

	/**
	 * Opens a catalog. This method must be invoked at first.
	 * @param coor the R.A. and Decl. of the center.
	 * @param fov  the field of view to read in degree.
	 * @exception IOException if a file cannot be accessed.
	 * @exception FileNotFoundException if a file does not exists in
	 * any URL.
	 * @exception CdromNotFoundException if this reader is to read 
	 * data from CD-ROMs and a file does not exists in any URL.
	 */
	public abstract void open ( Coor coor, double fov )
		throws IOException, FileNotFoundException, CdromNotFoundException;

	/**
	 * Reads one star from the opened catalog. After this method is 
	 * invoked, the cursor is promoted to tne next star. When every 
	 * data is read, it returns null.
	 * @return a star data.
	 * @exception IOException if a file cannot be accessed.
	 * @exception FileNotFoundException if a file does not exists in
	 * any URL.
	 * @exception CdromNotFoundException if this reader is to read 
	 * data from CD-ROMs and a file does not exists in any URL.
	 * @exception QueryFailException if the query to the server is 
	 * failed.
	 */
	public abstract CatalogStar readNext()
		throws IOException, FileNotFoundException, CdromNotFoundException, QueryFailException;

	/**
	 * Closes a catalog. This method must be invoked finally.
	 * @exception IOException if a file cannot be accessed.
	 */
	public abstract void close()
		throws IOException;

	/**
	 * Reads all star data in the specified area.
	 * @param coor the R.A. and Decl. of the center.
	 * @param fov  the field of view to read in degree.
	 * @return the list of stars in the specified area.
	 * @exception IOException if a file cannot be accessed.
	 * @exception FileNotFoundException if a file does not exists in
	 * any URL.
	 * @exception CdromNotFoundException if this reader is to read 
	 * data from CD-ROMs and a file does not exists in any URL.
	 * @exception QueryFailException if the query to the server is 
	 * failed.
	 */
	public CatalogStarList read ( Coor coor, double fov )
		throws IOException, FileNotFoundException, CdromNotFoundException, QueryFailException
	{
		CatalogStarList list = new CatalogStarList();

		open(coor, fov);
		CatalogStar star = readNext();
		while (star != null) {
			list.addElement(star);
			star = readNext();
		}
		close();

		return list;
	}
}
