/*
 * @(#)NsvsRedVarsReader.java
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
 * The <code>NsvsRedVarsReader</code> is a class to read the Red AGB 
 * Variables from Northern Sky Variability Survey (NSVS).
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 December 31
 */

public class NsvsRedVarsReader extends FileReader {
	/**
	 * Constructs an empty <code>NsvsRedVarsReader</code>.
	 */
	public NsvsRedVarsReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>NsvsRedVarsReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public NsvsRedVarsReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Red AGB Variables from Northern Sky Variability Survey (NSVS)";
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
		if (record.length() < 98)
			return null;

		String name = record.substring(0, 14);

		double ra = Format.doubleValueOf(record.substring(17, 26).trim());
		double decl = Format.doubleValueOf(record.substring(28, 37).trim());
		Coor coor = new Coor(ra, decl);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			double mag = Format.doubleValueOf(record.substring(58, 64).trim());
			double mag_error = Format.doubleValueOf(record.substring(73, 78).trim());
			double range = Format.doubleValueOf(record.substring(93, 98).trim());
			int period = Format.intValueOf(record.substring(87, 91).trim());

			NsvsRedVarsStar star = new NsvsRedVarsStar(name, coor, mag, mag_error, range, period);

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
		html += "Red AGB Variables from Northern Sky Variability Survey (NSVS)<br>";
		html += "</blockquote>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://skydot.lanl.gov/pub/projects/skydot/nsvs/red_variables/nsvs_red_vars.dat</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
