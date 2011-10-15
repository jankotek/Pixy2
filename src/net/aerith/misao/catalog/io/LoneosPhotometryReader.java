/*
 * @(#)LoneosPhotometryReader.java
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
 * The <code>LoneosPhotometryReader</code> is a class to read the 
 * LONEOS Photometry File.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 November 4
 */

public class LoneosPhotometryReader extends FileReader {
	/**
	 * Constructs an empty <code>LoneosPhotometryReader</code>.
	 */
	public LoneosPhotometryReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>LoneosPhotometryReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public LoneosPhotometryReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "LONEOS Photometry File";
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
		if (record.length() < 63)
			return null;

		if (! (
			'0' <= record.charAt(20)  &&  record.charAt(20) <= '9'  &&
			record.charAt(21) == ' '  &&
			'0' <= record.charAt(22)  &&  record.charAt(22) <= '9'  &&
			'0' <= record.charAt(23)  &&  record.charAt(23) <= '9'  &&
			record.charAt(24) == ' '  &&
			'0' <= record.charAt(25)  &&  record.charAt(25) <= '9'  &&
			'0' <= record.charAt(26)  &&  record.charAt(26) <= '9'  &&
			record.charAt(27) == '.'  &&
			'0' <= record.charAt(28)  &&  record.charAt(28) <= '9'  &&
			(record.charAt(32) == '+'  ||  record.charAt(32) == '-')  &&
			'0' <= record.charAt(33)  &&  record.charAt(33) <= '9'  &&
			'0' <= record.charAt(34)  &&  record.charAt(34) <= '9'  &&
			record.charAt(35) == ' '  &&
			'0' <= record.charAt(36)  &&  record.charAt(36) <= '9'  &&
			'0' <= record.charAt(37)  &&  record.charAt(37) <= '9'  &&
			record.charAt(38) == ' '  &&
			'0' <= record.charAt(39)  &&  record.charAt(39) <= '9'  &&
			'0' <= record.charAt(40)  &&  record.charAt(40) <= '9'))
			return null;

		String ra_decl = record.substring(19, 43);
		Coor coor = Coor.create(ra_decl);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			String name = record.substring(0, 19).trim();

			double v_mag = Format.doubleValueOf(record.substring(57, 63).trim());

			int accuracy = LoneosPhotometryStar.ACCURACY_100TH;
			if ('0' <= record.charAt(62)  &&  record.charAt(62) <= '9')
				accuracy = LoneosPhotometryStar.ACCURACY_1000TH;

			LoneosPhotometryStar star = new LoneosPhotometryStar(name, ra_decl, v_mag, accuracy);

			if (record.length() >= 70) {
				String s = record.substring(64, 70).trim();
				if (s.length() > 0)
					star.setBVDifference(Format.doubleValueOf(s));
			}
			if (record.length() >= 77) {
				String s = record.substring(71, 77).trim();
				if (s.length() > 0)
					star.setVRcDifference(Format.doubleValueOf(s));
			}
			if (record.length() >= 84) {
				String s = record.substring(78, 84).trim();
				if (s.length() > 0)
					star.setVIcDifference(Format.doubleValueOf(s));
			}

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
		html += "Johnson-Cousins BVRI photometry for faint field stars<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://ftp.lowell.edu/pub/bas/starcats/loneos.phot</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
