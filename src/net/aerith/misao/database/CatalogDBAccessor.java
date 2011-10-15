/*
 * @(#)CatalogDBAccessor.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.io.IOException;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.io.*;
import net.aerith.misao.xml.*;

/**
 * The <code>CatalogDBAccessor</code> represents a sequential accessor
 * to the catalog star records within the specified circular area in 
 * the catalog database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 February 28
 */

public class CatalogDBAccessor {
	/**
	 * The sequential accessor.
	 */
	protected CelestialDivisionMapDBAccessor accessor;

	/**
	 * The next record.
	 */
	protected XmlDBRecord next_record;

	/**
	 * Constructs a <code>CatalogDBAccessor</code>.
	 * @param file_system the file system of the database.
	 * @param db_folder   the folder.
	 * @param map         the celestial division map.
	 */
	public CatalogDBAccessor ( FileSystem file_system, Folder db_folder, CelestialDivisionMap map ) {
		accessor = new CelestialDivisionMapDBAccessor(file_system, db_folder, map, new XmlRecordHolder(), new XmlRecord());
	}

	/**
	 * Gets the first star object.
	 * @return the first star object.
	 * @exception IOException if I/O error occurs.
	 */
	public CatalogStar getFirstElement ( )
		throws IOException
	{
		next_record = accessor.getFirstElement();
		return getNextElement();
	}

	/**
	 * Gets the next star object.
	 * @return the next star object.
	 * @exception IOException if I/O error occurs.
	 */
	public CatalogStar getNextElement ( )
		throws IOException
	{
		while (true) {
			if (next_record == null)
				return null;

			XmlDBRecord record = next_record;
			next_record = accessor.getNextElement();

			try {
				CatalogStar star = (CatalogStar)((XmlRecord)record).createStar();
				return star;
			} catch ( ClassNotFoundException exception ) {
				System.err.println(exception);
			} catch ( IllegalAccessException exception ) {
				System.err.println(exception);
			} catch ( InstantiationException exception ) {
				System.err.println(exception);
			}
		}
	}
}
