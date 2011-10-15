/*
 * @(#)GcvsReader.java
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
 * The <code>GcvsReader</code> is a class to read the General 
 * Catalogue of Variable Stars.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 February 12
 */

public class GcvsReader extends FileReader {
	/**
	 * Constructs an empty <code>GcvsReader</code>.
	 */
	public GcvsReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>GcvsReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public GcvsReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "General Catalogue of Variable Stars";
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
		if (record.indexOf("  GCVS  ") >= 0)
			return null;
		if (record.indexOf("--------") >= 0)
			return null;

		if (record.length() < 54)
			return null;

		String constellation = record.substring(14, 17);

		String number = record.substring(8, 14).trim();
		if (number.length() > 2  &&  number.charAt(0) == 'V'  &&  number.charAt(1) == '0')
			number = "V" + number.substring(2);
		String additional_number = "";
		if (number.indexOf(' ') > 0) {
			number = number.substring(0, 3).trim();
			if ('0' <= record.charAt(12)  &&  record.charAt(12) <= '9')
				additional_number = record.substring(12, 13);
		}
		if (number.equals("alf"))
			number = "alpha";
		if (number.equals("bet"))
			number = "beta";
		if (number.equals("del"))
			number = "delta";
		if (number.equals("eps"))
			number = "epsilon";
		if (number.equals("gam"))
			number = "gamma";
		if (number.equals("iot"))
			number = "iota";
		if (number.equals("kap"))
			number = "kappa";
		if (number.equals("khi"))
			number = "chi";
		if (number.equals("ksi"))
			number = "xi";
		if (number.equals("lam"))
			number = "lambda";
		if (number.equals("ome"))
			number = "omega";
		if (number.equals("omi"))
			number = "omicron";
		if (number.equals("sig"))
			number = "sigma";
		if (number.equals("tet"))
			number = "theta";
		if (number.equals("ups"))
			number = "upsilon";
		if (number.equals("zet"))
			number = "zeta";
		number += additional_number;
		if (record.charAt(17) != ' ')
			number += record.substring(17, 18).toLowerCase();

		if (record.substring(37, 53).trim().length() == 0)
			return null;

		Coor coor = Coor.create(record.substring(37, 45).trim() + " " + record.substring(45, 53).trim());
		byte coor_accuracy = Coor.getAccuracy(record.substring(37, 45).trim() + " " + record.substring(45, 53).trim());

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			// Columns are shifted after 2006 (The 78th Name-List of Variable).
			int column_shift = 0;
			if (record.length() >= 87) {
				if (record.charAt(86) == '|') {
					column_shift = -1;
				}
			}

			String type = "";
			if (record.length() >= 55) {
				type = record.substring(54);
				if (type.length() >= 10)
					type = type.substring(0, 10);
				type = type.trim();
			}

			String spectrum = "";
			if (record.length() >= 138 + column_shift) {
				spectrum = record.substring(137 + column_shift);
				if (spectrum.length() >= 17)
					spectrum = spectrum.substring(0, 17);
				spectrum = spectrum.trim();
			}

			String epoch = "";
			if (record.length() >= 92 + column_shift) {
				epoch = record.substring(91 + column_shift);
				if (epoch.length() >= 12)
					epoch = epoch.substring(0, 12);
				char inaccurate = ' ';
				if (epoch.length() >= 12) {
					if (epoch.charAt(11) == ':'  ||  epoch.charAt(11) == '+'  ||  epoch.charAt(11) == '-') {
						inaccurate = epoch.charAt(11);
						epoch = epoch.substring(0, 11);
					}
				}
				epoch = epoch.trim();
				int l = epoch.length();
				if (l > 0  &&  epoch.charAt(l - 1) == '.')
					epoch = epoch.substring(0, l - 1);
				if (epoch.equals(")")  ||  epoch.equals("|")) {
					epoch = "";
				} else {
					if (inaccurate != ' ')
						epoch = epoch + String.valueOf(inaccurate);
				}
			}

			String period = "";
			if (record.length() >= 111 + column_shift) {
				period = record.substring(110 + column_shift);
				if (period.length() >= 20)
					period = period.substring(0, 20);
				period = period.trim();
				int p_inaccurate = period.indexOf(':');
				if (p_inaccurate > 0) {
					period = period.substring(0, p_inaccurate) + period.substring(p_inaccurate + 1);
					period = period.trim();
				}
				int p_lpar = period.indexOf('(');
				if (p_lpar >= 0)
					period = period.substring(p_lpar + 1).trim();
				int p_gt = period.indexOf('>');
				if (p_gt >= 0)
					period = period.substring(p_gt + 1).trim();
				int p_lt = period.indexOf('<');
				if (p_lt >= 0)
					period = period.substring(p_lt + 1).trim();
				int p = period.indexOf(' ');
				if (p > 0)
					period = period.substring(0, p);
				int l = period.length();
				if (l > 0  &&  period.charAt(l - 1) == '.')
					period = period.substring(0, l - 1);
				if (p_inaccurate > 0)
					period = period + ":";
				if (p_gt >= 0)
					period = ">" + period;
				if (p_lt >= 0)
					period = "<" + period;
				if (p_lpar >= 0)
					period = "(" + period + ")";
			}

			String mag_max = "";
			if (record.length() >= 66) {
				mag_max = record.substring(65);
				if (mag_max.length() >= 9)
					mag_max = mag_max.substring(0, 9);
				mag_max = mag_max.trim();
				int p_inaccurate = mag_max.indexOf(':');
				if (p_inaccurate > 0) {
					mag_max = mag_max.substring(0, p_inaccurate) + mag_max.substring(p_inaccurate + 1);
					mag_max = mag_max.trim();
				}
				int p_gt = mag_max.indexOf('>');
				if (p_gt >= 0)
					mag_max = mag_max.substring(p_gt + 1).trim();
				int p_lt = mag_max.indexOf('<');
				if (p_lt >= 0)
					mag_max = mag_max.substring(p_lt + 1).trim();
				mag_max = mag_max.trim();
				int l = mag_max.length();
				if (l > 0  &&  mag_max.charAt(l - 1) == '|')
					mag_max = mag_max.substring(0, l - 1).trim();
				if (l > 0  &&  mag_max.charAt(l - 1) == '.')
					mag_max = mag_max.substring(0, l - 1);
				if (p_inaccurate > 0)
					mag_max = mag_max + ":";
				if (p_gt >= 0)
					mag_max = ">" + mag_max;
				if (p_lt >= 0)
					mag_max = "<" + mag_max;
				if (mag_max.equals("( 1.73 u)"))
					mag_max = "";
			}

			String mag_min = "";
			boolean mag_range = false;
			String mag_system2 = "";
			if (record.length() >= 76) {
				mag_min = record.substring(75);
				if (mag_min.length() >= 12 + column_shift) {
					mag_min = mag_min.substring(0, 12 + column_shift);

					// Special treatment for error until 2004.
					if (mag_min.equals("(  0.31  R c"))
						mag_min = "(  0.31  Rc)";

					if (mag_min.charAt(0) == '('  &&  mag_min.charAt(11 + column_shift) == ')') {
						mag_range = true;
						mag_min = mag_min.substring(1, 11 + column_shift);
					}
				}
				mag_min = mag_min.trim();
				int p_inaccurate = mag_min.indexOf(':');
				if (p_inaccurate > 0) {
					mag_min = mag_min.substring(0, p_inaccurate) + mag_min.substring(p_inaccurate + 1);
					mag_min = mag_min.trim();
				}
				int p_gt = mag_min.indexOf('>');
				if (p_gt >= 0)
					mag_min = mag_min.substring(p_gt + 1).trim();
				int p_lt = mag_min.indexOf('<');
				if (p_lt >= 0)
					mag_min = mag_min.substring(p_lt + 1).trim();
				mag_min = mag_min.trim();
				int p = mag_min.indexOf(' ');
				if (p > 0) {
					if (p < mag_min.length() - 1) {
						mag_system2 = mag_min.substring(p + 1).trim();
						if (mag_system2.equals("*")  ||  mag_system2.equals("|"))
							mag_system2 = "";
					}
					mag_min = mag_min.substring(0, p).trim();
				}
				int l = mag_min.length();
				if (l > 0  &&  mag_min.charAt(l - 1) == '.')
					mag_min = mag_min.substring(0, l - 1);
				if (p_inaccurate > 0)
					mag_min = mag_min + ":";
				if (p_gt >= 0)
					mag_min = ">" + mag_min;
				if (p_lt >= 0)
					mag_min = "<" + mag_min;
			}

			String mag_system = "";
			if (record.length() >= 89 + column_shift) {
				mag_system = record.substring(88 + column_shift);
				if (mag_system.length() >= 2)
					mag_system = mag_system.substring(0, 2);
				mag_system = mag_system.trim();
				if (mag_system.length() > 0  &&  (mag_system.charAt(0) == '|'  ||  mag_system.charAt(0) == '*'  ||  mag_system.charAt(0) == '1'))
					mag_system = "";
			}

			GcvsStar star = new GcvsStar(constellation, number, coor, coor_accuracy, type, spectrum, epoch, period);

			if (mag_system2.length() > 0) {
				mag_max = mag_max + mag_system;
				mag_min = mag_min + mag_system2;
			} else {
				star.setMagSystem(mag_system);
			}

			if (mag_range) {
				star.setMagnitudeByRange(mag_max, mag_min);
			} else {
				star.setMagnitudeByMaxAndMin(mag_max, mag_min);
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
		html += "General Catalogue of Variable Stars, 4th Edition, Volumes I-III.<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://ftp.sai.msu.su/pub/groups/cluster/gcvs/gcvs/iii/iii.dat</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
