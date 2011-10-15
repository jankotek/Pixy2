/*
 * @(#)SampleReader.java
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
 * The <code>SampleReader</code> is a class to read the sample star
 * catalog file attached to the PIXY System 2.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 September 13
 */

public class SampleReader extends FileReader {
	/**
	 * Constructs an empty <code>SampleReader</code>.
	 */
	public SampleReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>SampleReader</code> with URL of the sample
	 * star catalog file.
	 * @param url the URL of the sample star catalog file.
	 */
	public SampleReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Sample Star Catalog";
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
		StringTokenizer st = new StringTokenizer(record);
		String ra = st.nextToken();
		String decl = st.nextToken();
		String mag = st.nextToken();
		String memo = "";
		while (st.hasMoreTokens())
			memo = memo + " " + st.nextToken();
		memo = memo.trim();

		Coor coor = Coor.create(ra + " " + decl);
		float fmag = Float.valueOf(mag).floatValue();

		if ((circum_area == null  ||  circum_area.inArea(coor))  &&  (double)fmag <= limiting_mag) {
			SampleStar star = new SampleStar(coor, (double)fmag, memo);

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
		html += "The sample star catalog file attached to the PIXY System 2 package. ";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
