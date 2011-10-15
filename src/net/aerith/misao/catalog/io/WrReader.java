/*
 * @(#)WrReader.java
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
 * The <code>WrReader</code> is a class to read the Galactic 
 * Wolf-Rayet Stars.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class WrReader extends FileReader {
	/**
	 * Constructs an empty <code>WrReader</code>.
	 */
	public WrReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>WrReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public WrReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Galactic Wolf-Rayet Stars";
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
		if (record.length() < 112)
			return null;

		String ra_decl = record.substring(86, 97).trim() + " " + record.substring(100, 112).trim();
		Coor coor = Coor.create(ra_decl);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			String number = record.substring(1, 6).trim();

			WrStar star = new WrStar(number, ra_decl);

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
		html += "7th Catalog of Galactic Wolf-Rayet stars<br>";
		html += "Astronomical Data Center catalog No. 3215<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/3/3215/table13.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
