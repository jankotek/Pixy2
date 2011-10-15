/*
 * @(#)StmReader.java
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
 * The <code>StmReader</code> is a class to read the Minoru Sato's 
 * Possible Noises in GSC.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 September 13
 */

public class StmReader extends FileReader {
	/**
	 * Constructs an empty <code>StmReader</code>.
	 */
	public StmReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>StmReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public StmReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Minoru Sato's Possible Noises in GSC";
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
		if (record.length() < 28)
			return null;

		if (! ('0' <= record.charAt(0)  &&  record.charAt(0) <= '9'))
			return null;

		String ra = record.substring(5, 14).replace(' ', '0');
		String decl = record.substring(14, 23);
		boolean decl_sign = false;
		if (decl.indexOf('-') >= 0)
			decl_sign = true;
		decl = decl.replace('-', '0').replace(' ', '0').substring(1);
		if (decl_sign)
			decl = "-" + decl;
		Coor coor = Coor.create(ra + " " + decl);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			int number = Short.parseShort(record.substring(0, 4));
			float mag = Float.parseFloat(record.substring(23, 28).trim());

			StmStar star = new StmStar(number, coor, mag);

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
		html += "The list of possible noises on plates in the Guide Star Catalog,<br>";
		html += "not recorded in the Hipparcos and Tycho Catalogues,<br>";
		html += "compiled by Minoru Sato.<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">http://www.aerith.net/misao/public/STM.TXT</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
