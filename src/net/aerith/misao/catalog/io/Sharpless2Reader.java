/*
 * @(#)Sharpless2Reader.java
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
 * The <code>Sharpless2Reader</code> is a class to read the Catalogue 
 * of HII Regions (Sh2).
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 9
 */

public class Sharpless2Reader extends FileReader {
	/**
	 * Constructs an empty <code>Sharpless2Reader</code>.
	 */
	public Sharpless2Reader ( ) {
		super();
	}

	/**
	 * Constructs a <code>Sharpless2Reader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public Sharpless2Reader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Catalogue of HII Regions (Sh2)";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 120.0;
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
		int number = Integer.parseInt(record.substring(0, 4).trim());
		
		Coor coor = Coor.create(record.substring(34, 40) + "." + record.substring(40, 41) + " " + record.substring(41, 48));
		coor.precession(new JulianDay(Astro.BESSELL_1950), new JulianDay(Astro.JULIUS_2000));

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			int dia = Integer.parseInt(record.substring(48, 52).trim());

			Sharpless2Star star = new Sharpless2Star(number, coor, dia);

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
		html += "Catalogue of HII Regions<br>";
		html += "Astronomical Data Center catalog No. 7020<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://cdsarc.u-strasbg.fr/pub/cats/VII/20/catalog.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
