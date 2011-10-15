/*
 * @(#)GspcReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import java.net.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.star.*;

/**
 * The <code>GspcReader</code> is a class to read the Guide Star 
 * Photometric Catalog file.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class GspcReader extends FileReader {
	/**
	 * Constructs an empty <code>GspcReader</code>.
	 */
	public GspcReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>GspcReader</code> with URL of the catalog
	 * file.
	 * @param url the URL of the catalog file.
	 */
	public GspcReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Guide Star Photometric Catalog";
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
		String number = record.substring(0, 6);

		double v_mag = Double.parseDouble(record.substring(8, 13).trim());
		double b_v = Double.parseDouble(record.substring(14, 19).trim());

		Coor coor = Coor.create(record.substring(21, 41));
		
		Xyz xyz = coor.convertToXyz();
		xyz.precession(new JulianDay(Astro.BESSELL_1950), new JulianDay(Astro.JULIUS_2000));
		coor = xyz.convertToCoor();

		if (v_mag <= limiting_mag) {
			if (circum_area == null  ||  circum_area.inArea(coor)) {
				GspcStar star = new GspcStar(number, coor, v_mag, b_v);

				if (center_coor != null) {
					ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
					Position position = cmf.mapCoordinatesToXY(coor);
					star.setPosition(position);
				}

				return star;
			}
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
		html += "Guide Star Photometric Catalog, Updated Version 1<br>";
		html += "Astronomical Data Center catalog No. 2143A<br>";
		html += "ADC CD-ROM Vol. 3<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/2/2143A/catalog.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
