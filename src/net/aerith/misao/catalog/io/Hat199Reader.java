/*
 * @(#)Hat199Reader.java
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
 * The <code>Hat199Reader</code> is a class to read the HAT
 * (Hungarian-made Automated Telescope)-199 Field Variable Star 
 * Catalog.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 December 31
 */

public class Hat199Reader extends FileReader {
	/**
	 * Constructs an empty <code>Hat199Reader</code>.
	 */
	public Hat199Reader ( ) {
		super();
	}

	/**
	 * Constructs a <code>Hat199Reader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public Hat199Reader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "HAT(Hungarian-made Automated Telescope)-199 Field Variable Star Catalog";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 5.0;
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
		if (record.length() < 112)
			return null;

		int number = Format.intValueOf(record.substring(0, 5));

		Coor coor = Coor.create(record.substring(6, 28));

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			String type = record.substring(69, 72).trim();
			double min = Format.doubleValueOf(record.substring(73, 79).trim());
			double max = Format.doubleValueOf(record.substring(80, 86).trim());
			String period = record.substring(87, 94).trim();

			Hat199Star star = new Hat199Star(number, coor, max, min, type);

			if (period.equals("00.000") == false)
				star.setPeriod(Format.doubleValueOf(period));

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
		html += "HAT Variability Survey in the High Stellar Density \"Kepler Field\" with Millimagnitude Image Subtraction Photometry<br>";
		html += "</p><p>";
		html += "Reference:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">http://arXiv.org/abs/astro-ph/0405597</font></u>";
		html += "</blockquote>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">http://cfa-www.harvard.edu/~gbakos/HAT/LC/199/H199.data</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
