/*
 * @(#)AstrometricaReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.star.*;

/**
 * The <code>AstrometricaReader</code> is a class to read the 
 * Astrometrica Other(ASCII) Star Catalog.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 September 13
 */

public class AstrometricaReader extends FileReader {
	/**
	 * Constructs an empty <code>AstrometricaReader</code>.
	 */
	public AstrometricaReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>AstrometricaReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public AstrometricaReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Astrometrica Other(ASCII) Star Catalog";
	}

	/**
	 * Checks if the catalog supports the use in PIXY examination.
	 * @return true if the catalog can be used in PIXY examination.
	 */
	public boolean supportsExamination ( ) {
		return true;
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
		if (record.length() < 14)
			return null;

		String name = record.substring(0, 14).trim();

		StringTokenizer st = new StringTokenizer(record.substring(14));

		try {
			String ra_decl = "";
			for (int i = 0 ; i < 6 ; i++) {
				if (i > 0)
					ra_decl += " ";
				ra_decl += st.nextToken();
			}
			Coor coor = Coor.create(ra_decl);

			if (circum_area == null  ||  circum_area.inArea(coor)) {
				double mag = Format.doubleValueOf(st.nextToken());

				AstrometricaStar star = new AstrometricaStar(name, coor, mag);

				if (center_coor != null) {
					ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
					Position position = cmf.mapCoordinatesToXY(coor);
					star.setPosition(position);
				}

				return star;
			}
		} catch ( NoSuchElementException exception ) {
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
		html += "The Other(ASCII) format of the Astrometrica. ";
		html += "</p><p>";
		html += "The format of the data list, which the system can read, is as follows. ";
		html += "</p>";
		html += "<p><table><tr><td bgcolor=\"#ffffff\"><pre>";
		html += "Name          R.A.  (J2000.0)  Decl.  Mag<br>";
		html += "-------------------------------------------<br>";
		html += "Star 1        01 23 45.67 +01 23 45.6 12.34<br>";
		html += "</pre></td></tr></table></p>";
		html += "</body></html>";
		return html;
	}
}
