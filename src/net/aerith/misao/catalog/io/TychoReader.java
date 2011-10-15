/*
 * @(#)TychoReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import java.net.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.star.*;

/**
 * The <code>TychoReader</code> is a class to read the Tycho Catalogue
 * file.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class TychoReader extends FileReader {
	/**
	 * Constructs an empty <code>TychoReader</code>.
	 */
	public TychoReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>TychoReader</code> with URL of the catalog
	 * file.
	 * @param url the URL of the catalog file.
	 */
	public TychoReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Tycho Catalogue (1 file)";
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
		String ra = record.substring(17, 28).trim();
		String decl = record.substring(29, 40).trim();
		String v_mag_str = record.substring(41, 46).trim();

		if (ra.length() == 0  ||  decl.length() == 0  ||  v_mag_str.length() == 0)
			return null;

		Coor coor = Coor.create(ra + " " + decl);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			double v_mag = Double.parseDouble(v_mag_str);

			if (v_mag <= limiting_mag) {
				short tyc1 = Short.parseShort(record.substring(2, 6).trim());
				short tyc2 = Short.parseShort(record.substring(6, 12).trim());
				short tyc3 = Short.parseShort(record.substring(12, 14).trim());

				double bt_mag = 999.9;
				double vt_mag = 999.9;
				double b_v = 999.9;

				String s = record.substring(217, 223).trim();
				if (s.length() > 0)
					bt_mag = Double.parseDouble(s);
				s = record.substring(230, 236).trim();
				if (s.length() > 0)
					vt_mag = Double.parseDouble(s);
				s = record.substring(245, 251).trim();
				if (s.length() > 0)
					b_v = Double.parseDouble(s);

				TychoStar star = new TychoStar(tyc1, tyc2, tyc3, coor, v_mag);
				if (bt_mag < 30.0)
					star.setBtMagnitude(bt_mag);
				if (vt_mag < 30.0)
					star.setVtMagnitude(vt_mag);
				if (b_v < 30.0)
					star.setBVDifference(b_v);

				if (center_coor != null) {
					ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
					Position position = cmf.mapCoordinatesToXY(coor);
					star.setPosition(position);
				}

				return star;
			}
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
		html += "The Tycho Catalogue<br>";
		html += "Astronomical Data Center catalog No. 1239<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/1/1239/tyc_main.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Gets the number of blocks in a file. If 0, it means the file is
	 * not separated into blocks. It must be overrided in the 
	 * subclasses if neccessary.
	 * @return the size of a block.
	 */
	public long getBlockCount ( ) {
		return 1997;
	}

	/**
	 * Gets the number of records in a block. If 0, it means the file
	 * is not separated into blocks. It must be overrided in the 
	 * subclasses if neccessary.
	 * @return the size of a block.
	 */
	public long getBlockSize ( ) {
		if (current_block >= getBlockCount())
			return 0;

		if (current_block == getBlockCount() -1)
			return 452;

		return 530;
	}

	/**
	 * Gets the characters of a record. If 0, it means the record size
	 * is not constant, so the reader skips the specified lines while
	 * reading one line by one. Otherwise, the reader skips the 
	 * specified characters at one time. It must be overrided in the 
	 * subclasses if neccessary.
	 * @return the size of a record.
	 */
	public long getRecordSize ( ) {
		return 351;
	}

	/**
	 * Checks if the current block is overlapping on the specified 
	 * circum area.
	 * @return true if the current block is overlapping on the 
	 * specified circum area.
	 */
	public boolean overlapsBlock ( ) {
		if (current_block >= getBlockCount())
			return true;

		if (circum_area == null)
			return true;

		Coor start_coor = new Coor((double)TychoIndex.ra_start[(int)current_block], (double)TychoIndex.decl_start[(int)current_block]);
		Coor end_coor = new Coor((double)TychoIndex.ra_end[(int)current_block], (double)TychoIndex.decl_end[(int)current_block]);
		if (circum_area.overlapsArea(start_coor, end_coor))
			return true;

		return false;
	}
}
