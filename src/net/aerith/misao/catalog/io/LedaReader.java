/*
 * @(#)LedaReader.java
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
 * The <code>LedaReader</code> is a class to read the HYPERLEDA. I. 
 * Catalog of Galaxies.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 9
 */

public class LedaReader extends FileReader {
	/**
	 * Constructs an empty <code>LedaReader</code>.
	 */
	public LedaReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>LedaReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public LedaReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "HYPERLEDA. I. Catalog of Galaxies";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 120.0;
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
		int number = Integer.parseInt(record.substring(3, 10).trim());
		
		Coor coor = Coor.create(record.substring(12, 20) + " " + record.substring(20, 27));

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			LedaStar star = new LedaStar(number, coor);

			String s = record.substring(36, 41).trim();
			if (s.length() > 0  &&  ! s.equals("9.99")) {
				double value = Format.doubleValueOf(s);
				value = Math.pow(10.0, value) * 0.1;
				star.setDiameter(value);
			}

			s = record.substring(50, 54).trim();
			if (s.length() > 0  &&  ! s.equals("9.99")) {
				double value = Format.doubleValueOf(s);
				value = Math.pow(10.0, value);
				star.setAxisRatio(value);
			}

			s = record.substring(63, 66).trim();
			if (s.length() > 0  &&  ! s.equals("999")) {
				int value = Format.intValueOf(s);
				star.setPositionAngle(value);
			}

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
		html += "HYPERLEDA. I. Catalog of Galaxies<br>";
		html += "Astronomical Data Center catalog No. 7237<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://cdsarc.u-strasbg.fr/pub/cats/VII/237/pgc.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
