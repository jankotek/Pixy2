/*
 * @(#)MisVReader.java
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
 * The <code>MisVReader</code> is a class to read the MISAO Project 
 * Variable Stars.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class MisVReader extends FileReader {
	/**
	 * Constructs an empty <code>MisVReader</code>.
	 */
	public MisVReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>MisVReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public MisVReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "MISAO Project Variable Stars";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 8.0;
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
		if (record.length() < 306  ||  ! record.substring(0,4).equals("MisV"))
			return null;

		int number = Integer.parseInt(record.substring(4, 8));

		Coor coor = Coor.create(record.substring(10, 29));

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			String max = record.substring(31, 36).trim();
			String min = record.substring(37, 43).trim();
			String range = record.substring(44, 51).trim();
			String mag_system = record.substring(52, 58).trim();
			String type = record.substring(59, 73).trim();
			if (type.equals("?"))
				type = "";
			String period = record.substring(74, 87).trim();
			if (period.equals("?"))
				period = "";
			String epoch = record.substring(88, 101).trim();
			if (epoch.equals("?"))
				epoch = "";

			String gcvs = record.substring(102, 112).trim();
			String id = record.substring(113, 293).trim();
			String discovery = record.substring(294, 305).trim();
			String discoverer = record.substring(306).trim();

			MisVStar star = new MisVStar(number, coor);

			if (range.length() > 0) {
				star.setMagnitudeByRange(max, range);
			} else {
				star.setMagnitudeByMaxAndMin(max, min);
			}
			star.setMagSystem(mag_system);

			star.setType(type);
			star.setPeriod(period);
			star.setEpoch(epoch);

			if (gcvs.length() > 0)
				star.setGcvsName(gcvs);
			if (id.length() > 0)
				star.setID(id);

			star.setDiscoveryDate(discovery);
			star.setDiscoverers(discoverer);

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
		html += "The list of the MISAO Project Variable Stars, available at the MISAO Project Home Page.<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">http://www.aerith.net/misao/public/MisV</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
