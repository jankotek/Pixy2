/*
 * @(#)NsvReader.java
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
 * The <code>NsvReader</code> is a class to read the New Catalogue of 
 * Suspected Variable Stars.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 December 29
 */

public class NsvReader extends FileReader {
	/**
	 * Constructs an empty <code>NsvReader</code>.
	 */
	public NsvReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>NsvReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public NsvReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "New Catalogue of Suspected Variable Stars";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 30.0;
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
		int number = Format.intValueOf(record.substring(0, 5));
		String additional_character = null;
		if (record.charAt(5) != ' ')
			additional_character = record.substring(5, 6);

		if (record.substring(27, 45).trim().length() == 0)
			return null;

		Coor coor = Coor.create(record.substring(27, 35).trim() + " " + record.substring(35, 45).trim());
		byte coor_accuracy = Coor.getAccuracy(record.substring(27, 35).trim() + " " + record.substring(35, 45).trim());

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			String type = record.substring(46, 51).trim();
			if (type.equals("-"))
				type = "";

			String spectrum = record.substring(95, 108).trim();

			String mag_max = record.substring(51, 59).trim();
			boolean inaccurate = false;
			int l = mag_max.length();
			if (l > 0  &&  mag_max.charAt(l - 1) == ':') {
				inaccurate = true;
				mag_max = mag_max.substring(0, l - 1).trim();
			}
			l = mag_max.length();
			if (l > 0  &&  mag_max.charAt(l - 1) == '.')
				mag_max = mag_max.substring(0, l - 1);
			if (inaccurate)
				mag_max += ":";

			String mag_min = record.substring(59, 70).trim();
			inaccurate = false;
			boolean mag_range = false;
			if (record.charAt(59) == '(') {
				mag_range = true;
				mag_min = record.substring(60, 69).trim();
			}
			l = mag_min.length();
			if (l > 0  &&  mag_min.charAt(l - 1) == ':') {
				inaccurate = true;
				mag_min = mag_min.substring(0, l - 1).trim();
			}
			l = mag_min.length();
			if (l > 0  &&  mag_min.charAt(l - 1) == '.')
				mag_min = mag_min.substring(0, l - 1);
			String mag_min2 = "";
			for (int i = 0 ; i < mag_min.length() ; i++) {
				if (mag_min.charAt(i) != ' ')
					mag_min2 += mag_min.charAt(i);
			}
			mag_min = mag_min2;
			if (inaccurate)
				mag_min += ":";

			String mag_system = record.substring(70, 72).trim();

			NsvStar star = new NsvStar(number, additional_character, coor, coor_accuracy, type, spectrum, "", "");

			if (mag_range) {
				star.setMagnitudeByRange(mag_max, mag_min);
			} else {
				star.setMagnitudeByMaxAndMin(mag_max, mag_min);
			}
			star.setMagSystem(mag_system);

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
		html += "New Catalogue of Suspected Variable Stars.<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://ftp.sai.msu.su/pub/groups/cluster/gcvs/gcvs/nsv/nsv.dat</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
