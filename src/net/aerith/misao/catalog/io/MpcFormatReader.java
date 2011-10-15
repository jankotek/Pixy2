/*
 * @(#)MpcFormatReader.java
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
 * The <code>MpcFormatReader</code> is a class to read the MPC format.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 April 29
 */

public class MpcFormatReader extends FileReader {
	/**
	 * Constructs an empty <code>MpcFormatReader</code>.
	 */
	public MpcFormatReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>MpcFormatReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public MpcFormatReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "MPC Format";
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
		if (record.length() < 80)
			return null;

		Coor coor = Coor.create(record.substring(32, 55));

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			MpcFormatStar star = new MpcFormatStar(record);

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
		html += "The list of astrometric observations in the MPC format.<br>";
		html += "</p><p>";
		html += "The format of the list is as follows. ";
		html += "</p>";
		html += "<p><table><tr><td bgcolor=\"#ffffff\"><pre>";
		html += "00022         C2002 01 09.51407 05 01 47.00 +30 48 56.0          10.2 R      XXX<br>";
		html += "     K01SA9O  C2001 12 13.54637 04 59 39.97 +32 06 05.3          16.5 R      XXX<br>";
		html += "</pre></td></tr></table></p>";
		html += "</body></html>";
		return html;
	}
}
