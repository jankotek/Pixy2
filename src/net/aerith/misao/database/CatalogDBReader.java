/*
 * @(#)CatalogDBReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.io.*;
import java.net.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.io.CdromNotFoundException;

/**
 * The <code>CatalogDBReader</code> is a class to read catalog 
 * database. The <tt>open</tt>, <tt>readNext</tt>, <tt>close</tt>
 * methods and the <tt>read</tt> method are available. This is for 
 * access to the catalog database as it were a catalog file.
 * <p>
 * The reader must return the stars within the square area represented
 * by the specified field of view in definition. However, the 
 * <code>CatalogDBReader</code> returns the stars within the circular
 * area, because it invokes the <code>CatalogDBManager#getAccessor</code>
 * with a radius.
 * <p>
 * So in principle, the circular area must be the circumcircle of the
 * square represented by the specified field of view. But actually,
 * this is only used in identification to read stars on the image. 
 * Considering the rotation of the image, the field of view represents
 * the square area which covers the circumcircle of the image. 
 * Therefore, in order to read all stars overlapping on the image, the
 * radius for <code>CatalogDBManager#getAccessor</code> is the half of
 * the specified field of view. That is, this returns the stars within
 * the incscribed circle, not the circumcircle, of the square area 
 * represented by the specified field of view.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 July 31
 */

public class CatalogDBReader extends CatalogReader {
	/**
	 * The catalog database manager.
	 */
	protected CatalogDBManager manager;

	/**
	 * The accessor to the catalog database.
	 */
	protected CatalogDBAccessor accessor;

	/**
	 * The first flag.
	 */
	protected boolean first_flag;

	/**
	 * Construct a <code>CatalogDBReader</code>.
	 * @param manager the catalog database manager.
	 */
	public CatalogDBReader ( CatalogDBManager manager ) {
		this.manager = manager;
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "catalog database";
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
		open(new Coor(), 360);
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

		accessor = manager.getAccessor(coor, fov / 2.0);

		first_flag = true;
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
	public CatalogStar readNext ( )
		throws IOException, FileNotFoundException, CdromNotFoundException, QueryFailException
	{
		CatalogStar star = null;
		if (first_flag) {
			star = accessor.getFirstElement();

			first_flag = false;
		} else {
			star = accessor.getNextElement();
		}

		if (star == null)
			return null;

		if (center_coor != null) {
			ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
			Position position = cmf.mapCoordinatesToXY(star.getCoor());
			star.setPosition(position);
		}

		return star;
	}

	/**
	 * Closes a catalog. This method must be invoked finally.
	 * @exception IOException if a file cannot be accessed.
	 */
	public void close ( ) {
	}
}
