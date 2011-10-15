/*
 * @(#)AstrometricaWriter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.io.Encoder;

/**
 * The <code>AstrometricaWriter</code> is a class to write the stars
 * into the Astrometrica Other(ASCII) Star Catalog.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 September 13
 */

public class AstrometricaWriter extends CatalogWriter {
	/**
	 * The output stream writer.
	 */
	protected PrintWriter writer = null;

	/**
	 * Constructs an empty <code>AstrometricaWriter</code>.
	 */
	public AstrometricaWriter ( ) {
		super();
	}

	/**
	 * Constructs an <code>AstrometricaWriter</code> with the name of
	 * the catalog file.
	 * @param file the catalog file.
	 */
	public AstrometricaWriter ( File file ) {
		super();

		this.file = file;
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Astrometrica Other(ASCII) Star Catalog";
	}

	/**
	 * Opens a catalog to write star data. This method must be invoked
	 * at first.
	 * @exception IOException if a file cannot be accessed.
	 */
	public void open()
		throws IOException
	{
		if (file == null)
			throw new IOException();

		writer = Encoder.newWriter(file);
	}

	/**
	 * Writes one star to the opened catalog.
	 * @param star the star data.
	 * @exception IOException if a file cannot be accessed.
	 * @exception UnsupportedStarClassException if the specified star
	 * is not supported.
	 */
	public void write ( Star star )
		throws IOException, UnsupportedStarClassException
	{
		if (writer == null)
			throw new IOException();

		String name = star.getName() + "             ";
		name = name.substring(0, 13);

		String coor = star.getCoor().getOutputStringTo100mArcsecWithoutUnit();

		String mag = Format.formatDouble(star.getMag(), 5, 2);

		writer.println(name + " " + coor + " " + mag);
	}

	/**
	 * Closes a catalog. This method must be invoked finally.
	 * @exception IOException if a file cannot be accessed.
	 */
	public void close()
		throws IOException
	{
		writer.close();
		writer = null;
	}
}
