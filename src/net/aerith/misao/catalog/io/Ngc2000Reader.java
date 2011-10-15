/*
 * @(#)Ngc2000Reader.java
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
 * The <code>Ngc2000Reader</code> is a class to read the NGC 2000.0
 * catalog.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class Ngc2000Reader extends FileReader {
	/**
	 * Constructs an empty <code>Ngc2000Reader</code>.
	 */
	public Ngc2000Reader ( ) {
		super();
	}

	/**
	 * Constructs a <code>Ngc2000Reader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public Ngc2000Reader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "NGC 2000.0";
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
		boolean ic_flag = (record.charAt(0) == 'I');
		int number = Integer.parseInt(record.substring(1, 5).trim());
		String type = record.substring(6, 9).trim();

		Coor coor = Coor.create(record.substring(10, 25).trim());

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			String size_str = record.substring(33, 38).trim();
			boolean size_limit = (record.charAt(32) == '<');
			if (size_str.length() > 0) {
				if (size_str.charAt(size_str.length() - 1) == '.')
					size_str = size_str.substring(0, size_str.length() - 1);
			}

			String mag_str = record.substring(40, 44).trim();
			if (mag_str.length() > 0) {
				if (mag_str.charAt(mag_str.length() - 1) == '.')
					mag_str = mag_str.substring(0, mag_str.length() - 1);
			}

			String mag_system = record.substring(44, 45).trim();
			if (mag_system.length() == 0)
				mag_system = "v";

			CatalogStar s = null;

			if (ic_flag) {
				IcStar star = new IcStar(number, coor, type);

				if (size_str.length() > 0)
					star.setSize(Float.parseFloat(size_str), size_limit);
				if (mag_str.length() > 0) {
					star.setMag(Double.parseDouble(mag_str));
					star.setMagSystem(mag_system);
				}

				s = star;
			} else {
				NgcStar star = new NgcStar(number, coor, type);

				if (size_str.length() > 0)
					star.setSize(Float.parseFloat(size_str), size_limit);
				if (mag_str.length() > 0) {
					star.setMag(Double.parseDouble(mag_str));
					star.setMagSystem(mag_system);
				}

				s = star;
			}

			if (center_coor != null) {
				ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
				Position position = cmf.mapCoordinatesToXY(coor);
				s.setPosition(position);
			}

			return s;
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
		html += "NGC 2000.0<br>";
		html += "Complete New General Catalogue and Index Catalogue of Nebulae and Star Clusters<br>";
		html += "Astronomical Data Center catalog No. 7118<br>";
		html += "ADC CD-ROM Vol. 4<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/7/7118/ngc2000.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
