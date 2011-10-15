/*
 * @(#)PgcReader.java
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
 * The <code>PgcReader</code> is a class to read the Catalogue of 
 * Principal Galaxies (PGC).
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class PgcReader extends FileReader {
	/**
	 * Constructs an empty <code>PgcReader</code>.
	 */
	public PgcReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>PgcReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public PgcReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Catalogue of Principal Galaxies (PGC)";
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
		if (record.length() < 81)
			return null;

		int number = 0;
		try {
			number = Integer.parseInt(record.substring(0, 5).trim());
		} catch ( Exception exception ) {
			return null;
		}
		if (number == 0)
			return null;

		String ra_decl = record.substring(6, 8) + " " + record.substring(8, 10) + " " + record.substring(10, 14) + " " + record.substring(14, 17) + " " + record.substring(17, 19) + " " + record.substring(19, 21);
		Coor coor = Coor.create(ra_decl);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			String type = record.substring(39, 43).trim();
			int p = type.indexOf("  ");
			while (p >= 0) {
				type = type.substring(0, p) + type.substring(p + 1);
				p = type.indexOf("  ");
			}
			String size_major = cutPeriod(record.substring(43, 49).trim());
			String size_minor = cutPeriod(record.substring(51, 56).trim());
			String size = size_major + record.substring(49, 50).trim();
			if (size_minor.length() > 0)
				size = size_major + record.substring(49, 50).trim() + "x" + size_minor + record.substring(56, 57).trim();
			String mag = cutPeriod(record.substring(59, 63).trim());
			String pa = record.substring(73, 76).trim();

			PgcStar star = new PgcStar(number, ra_decl, mag, type, size, pa);

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
		html += "Catalogue of Principal Galaxies (PGC)<br>";
		html += "Astronomical Data Center catalog No. 7119<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/7/7119/pgc.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
