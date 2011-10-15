/*
 * @(#)LandoltReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import java.net.*;
import java.util.Vector;
import java.util.StringTokenizer;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.star.*;

/**
 * The <code>LandoltReader</code> is a class to read the Landolt UBVRI
 * Photometric Standard Stars.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class LandoltReader extends FileReader {
	/**
	 * Constructs an empty <code>LandoltReader</code>.
	 */
	public LandoltReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>LandoltReader</code> with URL of the catalog
	 * file.
	 * @param url the URL of the catalog file.
	 */
	public LandoltReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Landolt UBVRI Photometric Standard Stars";
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
		Coor coor = Coor.create(record.substring(12, 30));
		coor.precession(new JulianDay(1985, 1, 1.5), new JulianDay(Astro.JULIUS_2000));

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			double v_mag = Format.doubleValueOf(record.substring(30, 37));

			if (v_mag <= limiting_mag) {
				double b_v = Format.doubleValueOf(record.substring(38, 44));
				double u_b = Format.doubleValueOf(record.substring(45, 51));
				double v_r = Format.doubleValueOf(record.substring(52, 58));
				double r_i = Format.doubleValueOf(record.substring(59, 65));
				double v_i = Format.doubleValueOf(record.substring(66, 72));
				double v_err = Format.doubleValueOf(record.substring(80, 86));
				double bv_err = Format.doubleValueOf(record.substring(87, 93));
				double ub_err = Format.doubleValueOf(record.substring(94, 100));
				double vr_err = Format.doubleValueOf(record.substring(101, 107));
				double ri_err = Format.doubleValueOf(record.substring(108, 114));
				double vi_err = Format.doubleValueOf(record.substring(115, 121));

				String name = record.substring(0, 2).trim();
				StringTokenizer st = new StringTokenizer(record.substring(2, 10).trim());
				while (st.hasMoreElements())
					name += " " + st.nextToken();

				LandoltStar star = new LandoltStar(name, coor, v_mag, b_v, u_b, v_r, r_i, v_i, v_err, bv_err, ub_err, vr_err, ri_err, vi_err);

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
		html += "UBVRI Photometric Standard Stars, Celestial Equator<br>";
		html += "Astronomical Data Center catalog No. 2118<br>";
		html += "ADC CD-ROM Vol. 4<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/2/2118/standard.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
