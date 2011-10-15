/*
 * @(#)ExtraGalacticReader.java
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
 * The <code>ExtraGalacticReader</code> is a class to read the 
 * Extragalactic Variable Stars.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 December 29
 */

public class ExtraGalacticReader extends FileReader {
	/**
	 * Constructs an empty <code>ExtraGalacticReader</code>.
	 */
	public ExtraGalacticReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>ExtraGalacticReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public ExtraGalacticReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Extragalactic Variable Stars";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 10.0;
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
		String host = record.substring(8, 14).trim();
		if (host.charAt(0) == 'N'  &&  '0' <= host.charAt(1)  &&  host.charAt(1) <= '9') {
			int n = Format.intValueOf(host.substring(1));
			host = "NGC " + n;
		} else if (host.charAt(0) == 'I'  &&  '0' <= host.charAt(1)  &&  host.charAt(1) <= '9') {
			int n = Format.intValueOf(host.substring(1));
			host = "IC " + n;
		} else if (host.charAt(0) == 'P'  &&  '0' <= host.charAt(1)  &&  host.charAt(1) <= '9') {
			int n = Format.intValueOf(host.substring(1));
			host = "PGC " + n;
		} else if (host.charAt(0) == 'U'  &&  '0' <= host.charAt(1)  &&  host.charAt(1) <= '9') {
			int n = Format.intValueOf(host.substring(1));
			host = "UGC " + n;
		} else if (host.charAt(0) == 'E'  &&  '0' <= host.charAt(1)  &&  host.charAt(1) <= '9') {
			host = "ESO " + host.substring(1);
		} else if (host.charAt(0) == 'M'  &&  '0' <= host.charAt(1)  &&  host.charAt(1) <= '9') {
		} else if (host.equals("LMC")  ||  host.equals("SMC")  ||  host.equals("W-L-M")) {
		} else {
			host = host.substring(0, 3) + " " + host.substring(3);
		}

		int number = Format.intValueOf(record.substring(16, 20).trim());

		if (record.substring(21, 39).trim().length() == 0)
			return null;

		Coor coor = Coor.create(record.substring(21, 30).trim() + " " + record.substring(30, 39).trim());
		byte coor_accuracy = Coor.getAccuracy(record.substring(21, 30).trim() + " " + record.substring(30, 39).trim());

		Xyz xyz = coor.convertToXyz();
		xyz.precession(new JulianDay(Astro.BESSELL_1950), new JulianDay(Astro.JULIUS_2000));
		coor = xyz.convertToCoor();

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			String type = record.substring(40, 47).trim();
			String spectrum = record.substring(96, 103).trim();

			String mag_max = record.substring(47, 53).trim();
			int l = mag_max.length();
			if (l > 0  &&  mag_max.charAt(l - 1) == '.')
				mag_max = mag_max.substring(0, l - 1);
			if (record.charAt(53) != ' ')
				mag_max = mag_max + record.substring(53, 54);

			String mag_min = record.substring(55, 60).trim();
			l = mag_min.length();
			if (l > 0  &&  mag_min.charAt(l - 1) == '.')
				mag_min = mag_min.substring(0, l - 1);
			if (record.charAt(60) != ' ')
				mag_min = mag_min + record.substring(60, 61);
			if (record.charAt(54) == '<')
				mag_min = "<" + mag_min;

			String mag_system = record.substring(62, 63).trim();

			String epoch = record.substring(63, 76).trim();
			l = epoch.length();
			if (l > 0  &&  epoch.charAt(l - 1) == '.')
				epoch = epoch.substring(0, l - 1);

			String period = record.substring(77, 90).trim();
			l = period.length();
			if (l > 0  &&  period.charAt(l - 1) == '.')
				period = period.substring(0, l - 1);
			if (record.charAt(90) != ' ')
				period = period + record.substring(90, 91);

			ExtraGalacticStar star = new ExtraGalacticStar(host, number, coor, coor_accuracy, type, spectrum, epoch, period);

			if (record.charAt(54) == '(') {
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
		html += "General Catalogue of Variable Stars, 4th Edition, Volumes I-III.<br>";
		html += "Extragalactic Variable Stars<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://ftp.sai.msu.su/pub/groups/cluster/gcvs/gcvs/v/EVS_CAT.DAT</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
