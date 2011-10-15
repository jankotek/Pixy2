/*
 * @(#)UgcReader.java
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
 * The <code>UgcReader</code> is a class to read the Uppsala General 
 * Catalogue of Galaxies.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class UgcReader extends FileReader {
	/**
	 * Constructs an empty <code>UgcReader</code>.
	 */
	public UgcReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>UgcReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public UgcReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Uppsala General Catalogue of Galaxies";
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

		String ra_decl = record.substring(10, 12) + " " + record.substring(12, 16) + " " + record.substring(16, 19) + " " + record.substring(19, 21);
		Coor coor = Coor.create(ra_decl);
		coor.precession(new JulianDay(Astro.BESSELL_1950), new JulianDay(Astro.JULIUS_2000));

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			int number = Integer.parseInt(record.substring(4, 9).trim());
			String id = record.substring(21, 34).trim();
			String b_size_major = cutPeriod(record.substring(38, 44).trim());
			String b_size_minor = cutPeriod(record.substring(44, 49).trim());
			String b_size = b_size_major;
			if (b_size_minor.length() > 0)
				b_size = b_size_major + "x" + b_size_minor;
			String pa = record.substring(49, 52).trim();
			String classification = record.substring(52, 59).trim();
			String p_mag = cutPeriod(record.substring(60, 64).trim());
			String r_size_major = cutPeriod(record.substring(69, 75).trim());
			String r_size_minor = cutPeriod(record.substring(75, 80).trim());
			String r_size = r_size_major;
			if (r_size_minor.length() > 0)
				r_size = r_size_major + "x" + r_size_minor;
			String inclination = record.substring(80, 81).trim();

			UgcStar star = new UgcStar(number, coor, p_mag, b_size, r_size, pa, inclination, classification, id);

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
		html += "Uppsala General Catalogue of Galaxies (UGC)<br>";
		html += "Astronomical Data Center catalog No. 7026D<br>";
		html += "ADC CD-ROM Vol. 3<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/7/7026D/catalog.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
