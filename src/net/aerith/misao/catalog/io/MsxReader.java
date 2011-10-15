/*
 * @(#)MsxReader.java
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
 * The <code>MsxReader</code> is a class to read the MSX Infrared 
 * Astrometric Catalog.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class MsxReader extends FileReader {
	/**
	 * Constructs an empty <code>MsxReader</code>.
	 */
	public MsxReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>MsxReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public MsxReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "MSX Infrared Astrometric Catalog";
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
		if (record.length() < 186)
			return null;

		String ra_decl = record.substring(21, 23) + " " + record.substring(23, 25) + " " + record.substring(25, 31) + " " + record.substring(31, 34) + " " + record.substring(34, 36) + " " + record.substring(36, 41);
		Coor coor = Coor.create(ra_decl);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			int number = (int)current_index;

			float error_ra = Float.parseFloat(record.substring(41, 47).trim()) / (float)1000.0;
			float error_decl = Float.parseFloat(record.substring(47, 52).trim()) / (float)100.0;
			double pm_ra = Double.parseDouble(record.substring(52, 61).trim()) / 100.0;
			double pm_decl = Double.parseDouble(record.substring(61, 70).trim()) / 100.0;
			float pm_error_ra = Float.parseFloat(record.substring(70, 76).trim()) / (float)10.0;
			float pm_error_decl = Float.parseFloat(record.substring(76, 82).trim()) / (float)10.0;
			String spectrum = record.substring(82, 85).trim();
			float v_mag = Float.parseFloat(record.substring(87, 92).trim());

			float flux_A = Float.parseFloat(record.substring(132, 141).trim());
			float flux_B1 = Float.parseFloat(record.substring(141, 150).trim());
			float flux_B2 = Float.parseFloat(record.substring(150, 159).trim());
			float flux_C = Float.parseFloat(record.substring(159, 168).trim());
			float flux_D = Float.parseFloat(record.substring(168, 177).trim());
			float flux_E = Float.parseFloat(record.substring(177, 186).trim());

			MsxStar star = new MsxStar(number, coor, error_ra, error_decl, pm_ra, pm_decl, pm_error_ra, pm_error_decl, v_mag, spectrum);
			star.setFlux(flux_A, flux_B1, flux_B2, flux_C, flux_D, flux_E);

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
		html += "The MSX Infrared Astrometric Catalog<br>";
		html += "Astronomical Data Center catalog No. 5098<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/5/5098/msx.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
