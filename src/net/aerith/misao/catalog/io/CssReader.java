/*
 * @(#)CssReader.java
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
 * The <code>CssReader</code> is a class to read the General 
 * Catalogue of S Stars 2.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class CssReader extends FileReader {
	/**
	 * Constructs an empty <code>CssReader</code>.
	 */
	public CssReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>CssReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public CssReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "General Catalogue of S Stars 2";
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
		if (record.length() < 93)
			return null;

		String ra_decl = record.substring(6, 27).trim();
		byte accuracy = Coor.getAccuracy(ra_decl);
		Coor coor = Coor.create(ra_decl);
		coor.precession(new JulianDay(Astro.BESSELL_1900), new JulianDay(Astro.JULIUS_2000));

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			String number = record.substring(0, 4).trim();
			String Bmag = cutPeriod(record.substring(29, 33).trim()) + record.substring(33, 34).trim();
			String Vmag = cutPeriod(record.substring(34, 38).trim()) + record.substring(38, 39).trim();
			String Imag = cutPeriod(record.substring(39, 43).trim()) + record.substring(43, 44).trim();
			String spectrum = record.substring(77, 93).trim();

			if (Bmag.equals("0.0"))
				Bmag = "";
			if (Vmag.equals("0.0"))
				Vmag = "";
			if (Imag.equals("0.0"))
				Imag = "";

			CssStar star = new CssStar(number, coor, accuracy, Bmag, Vmag, Imag, spectrum);

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
		html += "General Catalog of S Stars, second edition<br>";
		html += "Astronomical Data Center catalog No. 3168<br>";
		html += "ADC CD-ROM Vol. 4<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/3/3168/catalog.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
