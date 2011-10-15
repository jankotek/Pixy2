/*
 * @(#)Asas3Reader.java
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
 * The <code>Asas3Reader</code> is a class to read the ASAS-3 Catalog 
 * of Variable Stars.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 December 31
 */

public class Asas3Reader extends FileReader {
	/**
	 * Constructs an empty <code>Asas3Reader</code>.
	 */
	public Asas3Reader ( ) {
		super();
	}

	/**
	 * Constructs a <code>Asas3Reader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public Asas3Reader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "ASAS-3 Catalog of Variable Stars ";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 15.0;
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
		if (record.length() < 32)
			return null;

		if (record.charAt(0) == '#')
			return null;

		StringTokenizer st = new StringTokenizer(record);
		if (st.countTokens() < 8)
			return null;

		String id = st.nextToken();

		// Corrects a mistake of 183218-28.0.0.
		if (id.equals("183218-28.0.0"))
			id = "183218-2800.0";

		String ra = st.nextToken();
		String decl = st.nextToken();
		String period = st.nextToken();
		String epoch = st.nextToken();
		String mag = st.nextToken();
		String range = st.nextToken();
		String type = st.nextToken();

		if (ra.indexOf(':') <0) {
			type = mag;
			range = epoch;
			mag = period;
			epoch = decl;
			period = ra;
		}

		Coor coor = Coor.create(id.substring(0, 2) + " " + id.substring(2, 4) + " " + id.substring(4, 6) + " " + id.substring(6, 9) + " " + id.substring(9));

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			epoch = "245" + epoch;

			Asas3Star star = new Asas3Star(id, mag, range, type, period, epoch);

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
		html += "ASAS-3 Catalog of Variable Stars <br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">http://www.astrouw.edu.pl/~gp/asas/data/var3.gz</font></u>";
		html += "<u><font color=\"#0000ff\">http://www.astrouw.edu.pl/~gp/asas/data/var6.gz</font></u>";
		html += "<u><font color=\"#0000ff\">http://www.astrouw.edu.pl/~gp/asas/data/var12.gz</font></u>";
		html += "<u><font color=\"#0000ff\">http://www.astrouw.edu.pl/~gp/asas/data/var18.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
