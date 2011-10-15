/*
 * @(#)McgReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.star.*;

/**
 * The <code>McgReader</code> is a class to read the Morphological 
 * Catalog of Galaxies.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class McgReader extends FileReader {
	/**
	 * Constructs an empty <code>McgReader</code>.
	 */
	public McgReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>McgReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public McgReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Morphological Catalog of Galaxies";
	}

	/**
	 * Cuts the period at the end of the specified string.
	 * @param string the string which may have a period at the end.
	 * @return the string without a period at the end.
	 */
	private final static String cutPeriod ( String string ) {
		if (string.length() == 0)
			return string;

		if (string.charAt(string.length() - 1) == '.')
			return string.substring(0, string.length() - 1);

		return string;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 90.0;
	}

	/**
	 * Creates a <code>CatalogStar</code> object from the specified
	 * one line record in the file. If some more records are required
	 * to create a star object, it returns null. This method must be
	 * overrided in the subclasses.
	 * @param record the one line record in the file.
	 * @return the star object.
	 */
	public CatalogStar createStar ( String record ) {
		if (record.length() < 64)
			return null;

		String ra_decl = record.substring(24, 26) + " " + record.substring(26, 31) + " " + record.substring(31, 37);
		Coor coor = Coor.create(ra_decl);
		coor.precession(new JulianDay(Astro.BESSELL_1950), new JulianDay(Astro.JULIUS_2000));

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			String name = record.substring(3, 14).trim();
			String id = record.substring(15, 24).trim();
			String p_mag = cutPeriod(record.substring(38, 43).trim());
			String inner_size_major = cutPeriod(record.substring(43, 48).trim());
			String inner_size_minor = cutPeriod(record.substring(48, 53).trim());
			String inner_size = inner_size_major;
			if (inner_size_minor.length() > 0)
				inner_size = inner_size_major + "x" + inner_size_minor;
			String size_major = cutPeriod(record.substring(53, 59).trim());
			String size_minor = cutPeriod(record.substring(59, 64).trim());
			String size = size_major;
			if (size_minor.length() > 0)
				size = size_major + "x" + size_minor;

			McgStar star = new McgStar(name, coor, p_mag, size, inner_size, id);

			if (center_coor != null) {
				ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
				Position position = cmf.mapCoordinatesToXY(coor);
				star.setPosition(position);
			}

			return star;
		}

		return null;
	}

	/**
	 * Gets the help message.
	 * @return the help message.
	 */
	public String getHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "Morphological Catalog of Galaxies<br>";
		html += "Morphological Cat. of Gal. (MCG)<br>";
		html += "Astronomical Data Center catalog No. 7062A<br>";
		html += "ADC CD-ROM Vol. 3<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/7/7062A/mcg.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
