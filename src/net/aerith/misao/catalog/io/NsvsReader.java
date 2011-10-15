/*
 * @(#)NsvsReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.star.*;

/**
 * The <code>NsvsReader</code> is a class to read the Northern Sky 
 * Variability Survey (NSVS) Object Table.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 October 11
 */

public class NsvsReader extends FileReader {
	/**
	 * Constructs an empty <code>NsvsReader</code>.
	 */
	public NsvsReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>NsvsReader</code> with URL of the catalog
	 * file.
	 * @param url the URL of the catalog file.
	 */
	public NsvsReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Northern Sky Variability Survey (NSVS) Object Table";
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
		return 14.4;
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
		StringTokenizer st = new StringTokenizer(record, ";");

		double ra = Format.doubleValueOf(st.nextToken());
		double ra_error = Format.doubleValueOf(st.nextToken());
		double decl = Format.doubleValueOf(st.nextToken());
		double decl_error = Format.doubleValueOf(st.nextToken());
		Coor coor = new Coor(ra, decl);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			double mag = Format.doubleValueOf(st.nextToken());

			if (mag <= limiting_mag) {
				double mag_error = Format.doubleValueOf(st.nextToken());

				NsvsStar star = new NsvsStar(current_index, coor, mag, mag_error);

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
		html += "Northern Sky Variability Survey (NSVS) Object Table<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://skydot.lanl.gov/pub/projects/skydot/nsvs/skydot_catalog.sql.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
