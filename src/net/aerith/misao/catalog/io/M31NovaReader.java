/*
 * @(#)M31NovaReader.java
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
 * The <code>M31NovaReader</code> is a class to read the CBAT M31 
 * (Apparent) Novae Page.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 February 12
 */

public class M31NovaReader extends FileReader {
	/**
	 * Constructs an empty <code>M31NovaReader</code>.
	 */
	public M31NovaReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>M31NovaReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public M31NovaReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "CBAT M31 (Apparent) Novae Page";
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
		if (record.length() < 75)
			return null;

		if (! (('0' <= record.charAt(0)  &&  record.charAt(0) <= '9')  &&
			('0' <= record.charAt(1)  &&  record.charAt(1) <= '9')  &&
			('0' <= record.charAt(2)  &&  record.charAt(2) <= '9')  &&
			('0' <= record.charAt(3)  &&  record.charAt(3) <= '9')  &&
			record.charAt(4) == '-'  &&
			('0' <= record.charAt(5)  &&  record.charAt(5) <= '9')  &&
			('0' <= record.charAt(6)  &&  record.charAt(6) <= '9')  &&
			('0' <= record.charAt(11)  &&  record.charAt(11) <= '9')  &&
			('0' <= record.charAt(12)  &&  record.charAt(12) <= '9')  &&
			('0' <= record.charAt(13)  &&  record.charAt(13) <= '9')  &&
			('0' <= record.charAt(14)  &&  record.charAt(14) <= '9')  &&
			record.charAt(15) == ' '  &&
			('0' <= record.charAt(17)  &&  record.charAt(17) <= '9')  &&
			record.charAt(18) == ' '))
			return null;

		int year = Integer.parseInt(record.substring(0, 4).trim());
		String designation = record.substring(5, 10).trim();
		String coor_string = record.substring(27, 51).trim();
		String date = record.substring(11, 26).trim();
		String mag = record.substring(52, 56).trim();
		String mag_system = record.substring(56, 58).trim();
		String reporter = record.substring(74).trim();

		Coor coor = Coor.create(coor_string);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			M31NovaStar star = new M31NovaStar(year, designation, coor_string, mag, mag_system, date, reporter);

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
		html += "CBAT M31 (Apparent) Novae Page<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">http://cfa-www.harvard.edu/iau/CBAT_M31.html</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
