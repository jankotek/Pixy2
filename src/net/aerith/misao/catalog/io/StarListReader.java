/*
 * @(#)StarListReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import java.net.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.star.*;
import net.aerith.misao.io.CdromNotFoundException;

/**
 * The <code>StarListReader</code> is a class to keep the list of 
 * catalog stars and read stars from the list. 
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 * <p>
 * Note that it does not clone the star objects. So the star object
 * obtained by the read() or readNext() method should not be 
 * modified.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 September 13
 */

public class StarListReader extends CatalogReader {
	/**
	 * The list of stars.
	 */
	protected Vector list;

	/**
	 * The maximum error of position in arcsec.
	 */
	protected double maximum_error_in_arcsec = 5.0;

	/**
	 * The field of view in degree.
	 */
	protected double fov_in_degree;

	/**
	 * The current index.
	 */
	protected int current_index;

	/**
	 * Constructs a <code>StarListReader</code>.
	 */
	public StarListReader ( ) {
		super();

		list = new Vector();
	}

	/**
	 * Adds a star.
	 * @param star the star to add.
	 */
	public void addStar ( CatalogStar star ) {
		list.addElement(star);

		if (maximum_error_in_arcsec < star.getMaximumPositionErrorInArcsec())
			maximum_error_in_arcsec = star.getMaximumPositionErrorInArcsec();
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return maximum_error_in_arcsec;
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
	public void open ( Coor coor, double fov )
		throws IOException, FileNotFoundException, CdromNotFoundException
	{
		center_coor = coor;
		fov_in_degree = fov;

		current_index = 0;
	}

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
	public CatalogStar readNext()
		throws IOException, FileNotFoundException, CdromNotFoundException, QueryFailException
	{
		while (current_index < list.size()) {
			CatalogStar catalog_star = (CatalogStar)list.elementAt(current_index);
			current_index++;

			boolean flag = false;
			if (fov_in_degree <= 0) {
				flag = true;
			} else {
				ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
				Position position = cmf.mapCoordinatesToXY(catalog_star.getCoor());
				if (- fov_in_degree / 2.0 <= position.getX()  &&  position.getX() <= fov_in_degree / 2.0  &&
					- fov_in_degree / 2.0 <= position.getY()  &&  position.getY() <= fov_in_degree / 2.0)
					flag = true;

				catalog_star.setPosition(position);
			}

			if (flag  &&  catalog_star.getMag() <= limiting_mag)
				return catalog_star;
		}

		return null;
	}

	/**
	 * Closes a catalog. This method must be invoked finally.
	 * @exception IOException if a file cannot be accessed.
	 */
	public void close()
		throws IOException
	{
	}
}
