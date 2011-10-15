/*
 * @(#)Gcvs2000Reader.java
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
 * The <code>Gcvs2000Reader</code> is a class to read the GCVS2000
 * catalog.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 December 29
 */

public class Gcvs2000Reader extends FileReader {
	/**
	 * Constructs an empty <code>Gcvs2000Reader</code>.
	 */
	public Gcvs2000Reader ( ) {
		super();
	}

	/**
	 * Constructs a <code>Gcvs2000Reader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public Gcvs2000Reader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "General Catalogue of Variable Stars (GCVS 2000)";
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
		StringTokenizer st = new StringTokenizer(record);
		String[] fields = new String[11];
		for (int i = 0 ; i < 11 ; i++) {
			fields[i] = null;
			if (st.hasMoreTokens()) {
				fields[i] = st.nextToken();

				if (fields[i].equals("-"))
					fields[i] = null;
			}
		}

		String constellation = fields[0].substring(0, 3);
		String number = fields[0].substring(3);

		Coor coor = Coor.create(fields[1] + " " + fields[2]);
		byte coor_accuracy = Coor.getAccuracy(fields[1] + " " + fields[2]);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			// Special care on the illegal data.
			if (fields[0].equals("NSV00001"))
				fields[10] = null;

			DefaultVariableStar star = null;
			if (constellation.equalsIgnoreCase("NSV")) {
				String additional_character = null;
				String num = "";
				for (int i = 0 ; i < number.length() ; i++) {
					if ('0' <= number.charAt(i)  &&  number.charAt(i) <= '9') {
						num += number.charAt(i);
					} else {
						additional_character = number.substring(i);
						break;
					}
				}
				star = new NsvStar(Integer.parseInt(num), additional_character, coor, coor_accuracy, fields[3], fields[10], fields[7], fields[8]);
			} else {
				star = new GcvsStar(constellation, number, coor, coor_accuracy, fields[3], fields[10], fields[7], fields[8]);
			}

			String mag_max = "";
			if (fields[4] != null)
				mag_max = fields[4];
			String mag_min = "";
			if (fields[5] != null)
				mag_min = fields[5];
			String mag_system = "";
			if (fields[6] != null)
				mag_system = fields[6];

			int p1 = mag_min.indexOf('(');
			int p2 = mag_min.indexOf(')');
			if (p1 == 0  &&  p2 > 0) {
				mag_min = mag_min.substring(p1 + 1, p2);
				int p = -1;
				for (int q = 0 ; q < mag_min.length() ; q++) {
					if (('A' <= mag_min.charAt(q)  &&  mag_min.charAt(q) <= 'Z')  ||
						('a' <= mag_min.charAt(q)  &&  mag_min.charAt(q) <= 'z')) {
						p = q;
						break;
					}
				}
				if (p > 0) {
					star.setMagnitudeByRange(mag_max + mag_system, mag_min);
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
		html += "The arranged file of the General Catalogue of Variable Stars, 4.1 Edition, <br>";
		html += "the New Catalogue of Suspected Variable Stars, <br>";
		html += "the New Catalogue of Suspected Variable Stars Supplement, <br>";
		html += "and the recent Name Lists, compiled by Taichi Kato and Makoto Iida. <br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">http://www.kusastro.kyoto-u.ac.jp/vsnet/gcvs/gcvs2000.all</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
