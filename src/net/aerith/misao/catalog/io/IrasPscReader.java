/*
 * @(#)IrasPscReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import java.net.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.star.*;

/**
 * The <code>IrasPscReader</code> is a class to read the IRAS 
 * catalogue of Point Sources. 
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class IrasPscReader extends FileReader {
	/**
	 * Constructs an empty <code>IrasPscReader</code>.
	 */
	public IrasPscReader ( ) {
		super();
	}

	/**
	 * Constructs an <code>IrasPscReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public IrasPscReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "IRAS Point Source Catalogue";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 60.0;
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
		String number = record.substring(0, 11).trim();

		Coor coor = new Coor(Integer.parseInt(record.substring(11,13).trim()), Integer.parseInt(record.substring(13,15).trim()), (double)Integer.parseInt(record.substring(15,18).trim()) / 10.0, (record.charAt(18) == '-'), Integer.parseInt(record.substring(19,21).trim()), Integer.parseInt(record.substring(21,23).trim()), (double)Integer.parseInt(record.substring(23,25).trim()));

		Xyz xyz = coor.convertToXyz();
		xyz.precession(new JulianDay(Astro.BESSELL_1950), new JulianDay(Astro.JULIUS_2000));
		coor = xyz.convertToCoor();

		short ellipse_major = Short.parseShort(record.substring(25, 28).trim());
		short ellipse_minor = Short.parseShort(record.substring(28, 31).trim());
		short ellipse_pa = Short.parseShort(record.substring(31, 34).trim());

		float flux12 = (float)Format.doubleValueOf(record.substring(36,45).trim());
		float flux25 = (float)Format.doubleValueOf(record.substring(45,54).trim());
		float flux60 = (float)Format.doubleValueOf(record.substring(54,63).trim());
		float flux100 = (float)Format.doubleValueOf(record.substring(63,72).trim());

		boolean flux12_limit = false;
		boolean flux25_limit = false;
		boolean flux60_limit = false;
		boolean flux100_limit = false;
	    if (record.charAt(72) == '1')
			flux12_limit = true;
	    if (record.charAt(73) == '1')
			flux25_limit = true;
	    if (record.charAt(74) == '1')
			flux60_limit = true;
	    if (record.charAt(75) == '1')
			flux100_limit = true;

		String variability = record.substring(116, 118).trim();

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			IrasPscStar star = new IrasPscStar(number, coor, ellipse_major, ellipse_minor, ellipse_pa);
			star.setFlux12(flux12, flux12_limit);
			star.setFlux25(flux25, flux25_limit);
			star.setFlux60(flux60, flux60_limit);
			star.setFlux100(flux100, flux100_limit);
			if (variability.equals("-1") == false)
				star.setVariability(Integer.parseInt(variability));

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
		html += "IRAS catalogue of Point Sources, Version 2.0<br>";
		html += "Astronomical Data Center catalog No. 2125<br>";
		html += "ADC CD-ROM Vol. 1<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/2/2125/main.dat</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
