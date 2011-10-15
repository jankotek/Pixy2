/*
 * @(#)CatalogWriter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;

/**
 * The <code>CatalogWriter</code> is an abstract class of catalog 
 * writer.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 August 21
 */

public abstract class CatalogWriter {
	/**
	 * The file to write the star data to.
	 */
	protected File file = null;

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public abstract String getName ( );

	/**
	 * Sets the file to write the star data to.
	 */
	public void setFile ( File file ) {
		this.file = file;
	}

	/**
	 * Opens a catalog to write star data. This method must be invoked
	 * at first.
	 * @exception IOException if a file cannot be accessed.
	 */
	public abstract void open()
		throws IOException;

	/**
	 * Writes one star to the opened catalog.
	 * @param star the star data.
	 * @exception IOException if a file cannot be accessed.
	 * @exception UnsupportedStarClassException if the specified star
	 * is not supported.
	 */
	public abstract void write ( Star star )
		throws IOException, UnsupportedStarClassException;

	/**
	 * Closes a catalog. This method must be invoked finally.
	 * @exception IOException if a file cannot be accessed.
	 */
	public abstract void close()
		throws IOException;

	/**
	 * Writes star data in the list.
	 * @param list the list of stars.
	 * @exception IOException if a file cannot be accessed.
	 * @exception UnsupportedStarClassException if the specified star
	 * is not supported.
	 */
	public void write ( StarList list )
		throws IOException, UnsupportedStarClassException
	{
		UnsupportedStarClassException unsupported_exception = null;

		for (int i = 0 ; i < list.size() ; i++) {
			try {
				Star star = (Star)list.elementAt(i);
				write(star);
			} catch ( UnsupportedStarClassException exception ) {
				unsupported_exception = exception;
			}
		}

		if (unsupported_exception != null)
			throw unsupported_exception;
	}

	/**
	 * Writes all star data.
	 * @param list the list of stars.
	 * @exception IOException if a file cannot be accessed.
	 * @exception UnsupportedStarClassException if the specified star
	 * is not supported.
	 */
	public void writeAll ( StarList list )
		throws IOException, UnsupportedStarClassException
	{
		open();
		write(list);
		close();
	}
}
