/*
 * @(#)InteractiveCatalogReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.io.CdromNotFoundException;

/**
 * The <code>InteractiveCatalogReader</code> represents a catalog 
 * reader, which will show a dialog message and wait until the 
 * required CD-ROM is set properly.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class InteractiveCatalogReader {
	/**
	 * The catalog reader.
	 */
	protected CatalogReader reader;

	/**
	 * Constructs an <code>InteractiveCatalogReader</code>.
	 * @param reader the catalog reader.
	 */
	public InteractiveCatalogReader ( CatalogReader reader ) {
		this.reader = reader;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return reader.getMaximumPositionErrorInArcsec();
	}

	/**
	 * Sets the date.
	 * @param date the date.
	 */
	public void setDate ( JulianDay date ) {
		reader.setDate(date);
	}

	/**
	 * Opens a catalog to read all star data. This method must be 
	 * invoked at first. If the required CD-ROM is not found, it will 
	 * show a dialog message and wait until they are set properly.
	 * @param pane the pane to show a dialog.
	 * @exception IOException if a file cannot be accessed.
	 * @exception FileNotFoundException if a file does not exists in
	 * any URL.
	 * @exception CdromNotFoundException if this reader is to read 
	 * data from CD-ROMs and a file does not exists in any URL.
	 */
	public void open ( Container pane )
		throws IOException, FileNotFoundException, CdromNotFoundException
	{
		open(pane, null, 0);
	}

	/**
	 * Opens a catalog. This method must be invoked at first.If the 
	 * required CD-ROM is not found, it will show a dialog message and 
	 * wait until they are set properly.
	 * @param pane the pane to show a dialog.
	 * @param coor the R.A. and Decl. of the center.
	 * @param fov  the field of view to read in degree.
	 * @exception IOException if a file cannot be accessed.
	 * @exception FileNotFoundException if a file does not exists in
	 * any URL.
	 * @exception CdromNotFoundException if this reader is to read 
	 * data from CD-ROMs and a file does not exists in any URL.
	 */
	public void open ( Container pane, Coor coor, double fov )
		throws IOException, FileNotFoundException, CdromNotFoundException
	{
		String[] messages = new String[2];

		int repeat_count = 0;
		while (true) {
			try {
				reader.open(coor, fov);
				return;
			} catch ( FileNotFoundException exception ) {
				messages[0] = "File is not found.";
				messages[1] = exception.getMessage();
				JOptionPane.showMessageDialog(pane, messages, "Error", JOptionPane.ERROR_MESSAGE);

				throw exception;
			} catch ( CdromNotFoundException exception ) {
				if (repeat_count == 0)
					messages[0] = "Please insert the disk.";
				else
					messages[0] = "Disk is not found.";
				messages[1] = exception.getDiskName();
				JOptionPane.showMessageDialog(pane, messages, "Error", JOptionPane.ERROR_MESSAGE);

				if (repeat_count > 0)
					throw exception;
			}

			repeat_count++;
		}
	}

	/**
	 * Reads one star from the opened catalog. After this method is 
	 * invoked, the cursor is promoted to tne next star. When every 
	 * data is read, it returns null. If the required CD-ROM is not 
	 * found, it will show a dialog message and wait until they are 
	 * set properly.
	 * @return a star data.
	 * @exception IOException if a file cannot be accessed.
	 * @exception FileNotFoundException if a file does not exists in
	 * any URL.
	 * @exception CdromNotFoundException if this reader is to read 
	 * data from CD-ROMs and a file does not exists in any URL.
	 * @exception QueryFailException if the query to the server is 
	 * failed.
	 */
	public CatalogStar readNext ( Container pane )
		throws IOException, FileNotFoundException, CdromNotFoundException, QueryFailException
	{
		String[] messages = new String[2];

		CatalogStar star = null;
		int repeat_count = 0;
		while (true) {
			try {
				star = reader.readNext();
				return star;
			} catch ( FileNotFoundException exception ) {
				messages[0] = "File is not found.";
				messages[1] = exception.getMessage();
				JOptionPane.showMessageDialog(pane, messages, "Error", JOptionPane.ERROR_MESSAGE);

				throw exception;
			} catch ( QueryFailException exception ) {
				messages[0] = "Query to the server is failed.";
				messages[1] = exception.getMessage();
				JOptionPane.showMessageDialog(pane, messages, "Error", JOptionPane.ERROR_MESSAGE);

				throw exception;
			} catch ( CdromNotFoundException exception ) {
				if (repeat_count == 0)
					messages[0] = "Please insert the disk.";
				else
					messages[0] = "Disk is not found.";
				messages[1] = exception.getDiskName();
				JOptionPane.showMessageDialog(pane, messages, "Error", JOptionPane.ERROR_MESSAGE);

				if (repeat_count > 0)
					throw exception;
			}

			repeat_count++;
		}
	}

	/**
	 * Closes a catalog. This method must be invoked finally.
	 * @exception IOException if a file cannot be accessed.
	 */
	public void close()
		throws IOException
	{
		reader.close();
	}

	/**
	 * Reads all star data in the specified area. If the required
	 * CD-ROM is not found, it will show a dialog message and wait 
	 * until they are set properly.
	 * @param pane the pane to show a dialog.
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
	public CatalogStarList read ( Container pane, Coor coor, double fov )
		throws IOException, FileNotFoundException, CdromNotFoundException, QueryFailException
	{
		CatalogStarList list = new CatalogStarList();

		open(pane, coor, fov);
		CatalogStar star = readNext(pane);
		while (star != null) {
			list.addElement(star);
			star = readNext(pane);
		}
		close();

		return list;
	}
}
