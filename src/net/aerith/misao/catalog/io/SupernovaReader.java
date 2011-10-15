/*
 * @(#)SupernovaReader.java
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
 * The <code>SupernovaReader</code> is a class to read the List of 
 * Supernovae.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 December 29
 */

public class SupernovaReader extends FileReader {
	/**
	 * Constructs an empty <code>SupernovaReader</code>.
	 */
	public SupernovaReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>SupernovaReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public SupernovaReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "List of Supernovae";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 90.0;
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
		if (record.length() < 8  ||  ! record.substring(0, 8).equals("<a name="))
			return null;

		int p = record.indexOf('<');
		int q = record.indexOf('>');
		while (p >= 0  &&  q >= 0  &&  q > p) {
			record = record.substring(0, p) + record.substring(q + 1);
			p = record.indexOf('<');
			q = record.indexOf('>');
		}

		String name = record.substring(0, 7).trim();
		if (name.length() == 0  ||  
			! ('0' <= name.charAt(0)  &&  name.charAt(0) <= '9')) {
			name = record.substring(25, 29);
		}

		// Correction of illegal line in 1991N.
		if (record.charAt(87) == ' ') {
			String record2 = record.substring(87);
			p = record2.indexOf(name);
			if (p > 49)
				record = record.substring(0, 87) + record2.substring(p - 49);
		}

		String ra_decl = record.substring(87, 110).trim();
		if (name.length() == 0  ||  ra_decl.length() == 0)
			return null;

		Coor coor = Coor.create(ra_decl);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			int i = 0;
			for (i = 0 ; i < name.length() ; i++) {
				if (! ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9'))
					break;
			}
			int year = Integer.parseInt(name.substring(0, i));
			String number = name.substring(i);

			String host = record.substring(8, 24).trim();
			String date = record.substring(25, 36).trim();
			String mag = record.substring(63, 69).trim();
			String reference = record.substring(71, 86).trim();
			String type = record.substring(130, 135).trim();
			String discoverers = "";
			if (record.length() > 144)
				discoverers = record.substring(144).trim();

			String data = "";
			if (! ('0' <= record.charAt(25)  &&  record.charAt(25) <= '9'))
				data = record.substring(25, 63).trim();

			SupernovaStar star = new SupernovaStar(year, number, ra_decl);
			if (host.length() > 0)
				star.setHostGalaxy(host);
			if (date.length() > 0)
				star.setDiscoveryDate(date);
			if (mag.length() > 0)
				star.setDiscoveryMagnitude(mag);
			if (reference.length() > 0)
				star.setReference(reference);
			if (type.length() > 0)
				star.setType(type);
			if (discoverers.length() > 0)
				star.setDiscoverers(discoverers);
			if (data.length() > 0)
				star.setData(data);

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
		html += "List of Supernovae<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">http://cfa-www.harvard.edu/cfa/ps/lists/Supernovae.html</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
