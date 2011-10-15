/*
 * @(#)Msx5cReader.java
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
 * The <code>Msx5cReader</code> is a class to read the MSX5C Infrared 
 * Point Source Catalog.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class Msx5cReader extends FileReader {
	/**
	 * Constructs an empty <code>Msx5cReader</code>.
	 */
	public Msx5cReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>Msx5cReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public Msx5cReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "MSX5C Infrared Point Source Catalog";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 20.0;
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
		if (record.length() < 333)
			return null;

		double ra = Double.parseDouble(record.substring(25, 33).trim());
		double decl = Double.parseDouble(record.substring(35, 43).trim());
		Coor coor = new Coor(ra, decl);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			String name = record.substring(6, 23).trim();
			float error_in_scan = Float.parseFloat(record.substring(44, 48).trim());
			float error_cross_scan = Float.parseFloat(record.substring(49, 53).trim());

			float flux_B1 = Float.parseFloat(record.substring(64, 76).trim());
			if (record.charAt(77) == '0')
				flux_B1 = -1;
			boolean flux_B1_var = (record.charAt(334) == '1');

			float flux_B2 = Float.parseFloat(record.substring(109, 121).trim());
			if (record.charAt(122) == '0')
				flux_B2 = -1;
			boolean flux_B2_var = (record.charAt(335) == '1');

			float flux_A = Float.parseFloat(record.substring(154, 166).trim());
			if (record.charAt(167) == '0')
				flux_A = -1;
			boolean flux_A_var = (record.charAt(336) == '1');

			float flux_C = Float.parseFloat(record.substring(199, 211).trim());
			if (record.charAt(212) == '0')
				flux_C = -1;
			boolean flux_C_var = (record.charAt(337) == '1');

			float flux_D = Float.parseFloat(record.substring(244, 256).trim());
			if (record.charAt(257) == '0')
				flux_D = -1;
			boolean flux_D_var = (record.charAt(338) == '1');

			float flux_E = Float.parseFloat(record.substring(289, 301).trim());
			if (record.charAt(302) == '0')
				flux_E = -1;
			boolean flux_E_var = (record.charAt(339) == '1');

			Msx5cStar star = new Msx5cStar(name, coor, error_in_scan, error_cross_scan);

			if (flux_A >= 0)
				star.setFluxA(flux_A, flux_A_var);
			if (flux_B1 >= 0)
				star.setFluxB1(flux_B1, flux_B1_var);
			if (flux_B2 >= 0)
				star.setFluxB2(flux_B2, flux_B2_var);
			if (flux_C >= 0)
				star.setFluxC(flux_C, flux_C_var);
			if (flux_D >= 0)
				star.setFluxD(flux_D, flux_D_var);
			if (flux_E >= 0)
				star.setFluxE(flux_E, flux_E_var);

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
		html += "The Midcourse Space Experiment Point Source Catalog, Version 1.2<br>";
		html += "Astronomical Data Center catalog No. 5107<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/5/5107/gp_m05m2.dat.gz</font></u><br>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/5/5107/gp_m2m6.dat.gz</font></u><br>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/5/5107/gp_p05p2.dat.gz</font></u><br>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/5/5107/gp_p2p6.dat.gz</font></u><br>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/5/5107/gp_pm05.dat.gz</font></u><br>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/5/5107/lmc.dat.gz</font></u><br>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/5/5107/nonplane.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
