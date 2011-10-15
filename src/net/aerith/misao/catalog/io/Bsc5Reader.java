/*
 * @(#)Bsc5Reader.java
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
 * The <code>Bsc5Reader</code> is a class to read the Bright Star 
 * Catalogue, 5th Revised Ed.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class Bsc5Reader extends FileReader {
	/**
	 * Constructs an empty <code>Bsc5Reader</code>.
	 */
	public Bsc5Reader ( ) {
		super();
	}

	/**
	 * Constructs a <code>Bsc5Reader</code> with URL of the catalog
	 * file.
	 * @param url the URL of the catalog file.
	 */
	public Bsc5Reader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Bright Star Catalogue 5";
	}

	/**
	 * Checks if the catalog supports the use in PIXY examination.
	 * @return true if the catalog can be used in PIXY examination.
	 */
	public boolean supportsExamination ( ) {
		return true;
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
		int number = Integer.parseInt(record.substring(0, 4).trim());

		String ra = record.substring(75, 83).trim();
		String decl = record.substring(83, 90).trim();

		if (ra.length() == 0  ||  decl.length() == 0)
			return null;

		Coor coor = Coor.create(ra + " " + decl);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			String mag_str = record.substring(102, 107).trim();
			if (mag_str.length() == 0)
				return null;

			double v_mag = Format.doubleValueOf(mag_str);

			if (v_mag <= limiting_mag) {
				Bsc5Star star = new Bsc5Star(number, coor, v_mag);

				mag_str = record.substring(109, 114).trim();
				if (mag_str.length() > 0)
					star.setBVDifference(Format.doubleValueOf(mag_str));

				mag_str = record.substring(115, 120).trim();
				if (mag_str.length() > 0)
					star.setUBDifference(Format.doubleValueOf(mag_str));

				mag_str = record.substring(121, 126).trim();
				if (mag_str.length() > 0)
					star.setRIDifference(Format.doubleValueOf(mag_str));

				String spectrum = record.substring(127, 147).trim();
				star.setSpectrum(spectrum);

				if (record.substring(148, 154).trim().length() > 0)
					star.setProperMotionInRA(Format.doubleValueOf(record.substring(148, 154).trim()));
				if (record.substring(154, 160).trim().length() > 0)
					star.setProperMotionInDecl(Format.doubleValueOf(record.substring(154, 160).trim()));

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
		html += "Bright Star Catalogue, 5th Revised Ed.<br>";
		html += "Astronomical Data Center catalog No. 5050<br>";
		html += "ADC CD-ROM Vol. 3<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/5/5050/catalog.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
