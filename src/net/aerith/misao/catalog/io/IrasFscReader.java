/*
 * @(#)IrasFscReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import java.net.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.star.*;

/**
 * The <code>IrasFscReader</code> is a class to read the IRAS Faint 
 * Source Catalogue.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class IrasFscReader extends FileReader {
	/**
	 * Constructs an empty <code>IrasFscReader</code>.
	 */
	public IrasFscReader ( ) {
		super();
	}

	/**
	 * Constructs an <code>IrasFscReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public IrasFscReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "IRAS Faint Source Catalogue";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 60.0;
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
		String number = record.substring(0, 12).trim();

		Coor coor = new Coor(Integer.parseInt(record.substring(12,14).trim()), Integer.parseInt(record.substring(14,16).trim()), (double)Integer.parseInt(record.substring(16,19).trim()) / 10.0, (record.charAt(19) == '-'), Integer.parseInt(record.substring(20,22).trim()), Integer.parseInt(record.substring(22,24).trim()), (double)Integer.parseInt(record.substring(24,26).trim()));

		Xyz xyz = coor.convertToXyz();
		xyz.precession(new JulianDay(Astro.BESSELL_1950), new JulianDay(Astro.JULIUS_2000));
		coor = xyz.convertToCoor();

		short ellipse_major = Short.parseShort(record.substring(26, 29).trim());
		short ellipse_minor = Short.parseShort(record.substring(29, 32).trim());
		short ellipse_pa = Short.parseShort(record.substring(32, 35).trim());

		float flux12 = (float)Format.doubleValueOf(record.substring(47,56).trim());
		float flux25 = (float)Format.doubleValueOf(record.substring(56,65).trim());
		float flux60 = (float)Format.doubleValueOf(record.substring(65,74).trim());
		float flux100 = (float)Format.doubleValueOf(record.substring(74,83).trim());

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			IrasFscStar star = new IrasFscStar(number, coor, ellipse_major, ellipse_minor, ellipse_pa);
			star.setFlux12(flux12, false);
			star.setFlux25(flux25, false);
			star.setFlux60(flux60, false);
			star.setFlux100(flux100, false);

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
		html += "IRAS Faint Source Catalog, |b| &gt; 10, Version 2.0<br>";
		html += "Astronomical Data Center catalog No. 2156A<br>";
		html += "ADC CD-ROM Vol. 4<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/2/2156A/main.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
