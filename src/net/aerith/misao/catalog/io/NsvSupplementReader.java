/*
 * @(#)NsvSupplementReader.java
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
 * The <code>NsvSupplementReader</code> is a class to read the New 
 * Catalogue of Suspected Variable Stars Supplement.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 December 29
 */

public class NsvSupplementReader extends FileReader {
	/**
	 * Constructs an empty <code>NsvSupplementReader</code>.
	 */
	public NsvSupplementReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>NsvSupplementReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public NsvSupplementReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "New Catalogue of Suspected Variable Stars Supplement";
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

		if (record.substring(24, 32).trim().length() == 0)
			return null;

		Coor coor = Coor.create(record.substring(24, 32).trim() + " " + record.substring(32, 39).trim());
		byte coor_accuracy = Coor.getAccuracy(record.substring(24, 32).trim() + " " + record.substring(32, 39).trim());

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			String type = record.substring(40, 46).trim();
			String spectrum = record.substring(80, 95).trim();

			String mag_max = record.substring(48, 53).trim();
			int l = mag_max.length();
			if (l > 0  &&  mag_max.charAt(l - 1) == '.')
				mag_max = mag_max.substring(0, l - 1);
			if (record.charAt(47) != ' ')
				mag_max = record.substring(47, 48) + mag_max;
			if (record.charAt(53) != ' ')
				mag_max = mag_max + record.substring(53, 54);

			String mag_min = record.substring(57, 63).trim();
			l = mag_min.length();
			if (l > 0  &&  mag_min.charAt(l - 1) == '.')
				mag_min = mag_min.substring(0, l - 1);
			if (record.charAt(56) != ' ')
				mag_min = record.substring(56, 57) + mag_min;
			if (record.charAt(65) != ' ')
				mag_min = mag_min + record.substring(65, 66);

			String mag_system = record.substring(67, 69).trim();

			NsvStar star = new NsvStar(number, null, coor, coor_accuracy, type, spectrum, "", "");

			if (record.charAt(55) == '(') {
				if (record.charAt(63) != ' ') {
					mag_max += mag_system;
					mag_min += record.substring(63, 64);
					star.setMagnitudeByRange(mag_max, mag_min);
				} else {
					star.setMagnitudeByRange(mag_max, mag_min);
					star.setMagSystem(mag_system);
				}
			} else {
				star.setMagnitudeByMaxAndMin(mag_max, mag_min);
				star.setMagSystem(mag_system);
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
		html += "New Catalogue of Suspected Variable Stars. Supplement.<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://ftp.sai.msu.su/pub/groups/cluster/gcvs/gcvs/nsvsup/nsvs.dat</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
