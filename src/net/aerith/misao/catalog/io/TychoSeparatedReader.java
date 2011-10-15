/*
 * @(#)TychoSeparatedReader.java
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
import net.aerith.misao.io.Decoder;
import net.aerith.misao.io.CdromNotFoundException;

/**
 * The <code>TychoReader</code> is a class to read the separated Tycho 
 * Catalogue files.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class TychoSeparatedReader extends CatalogReader {
	/**
	 * The circum area to read stars.
	 */
	protected CircumArea circum_area;

	/**
	 * The input stream to read stars.
	 */
	protected DataInputStream stream;

	/**
	 * The index of current file.
	 */
	protected int current_file_index;

	/**
	 * The index of current block.
	 */
	protected int current_block_index;

	/**
	 * The index of current star.
	 */
	protected int current_star_index;

	/**
	 * The index of already read star.
	 */
	protected int read_star_index;

	/**
	 * Constructs an empty <code>TychoSeparatedReader</code>.
	 */
	public TychoSeparatedReader ( ) {
		url_set.acceptGzipped(true);

		setDefaultURL();
	}

	/**
	 * Constructs a <code>TychoSeparatedReader</code> with URL of the catalog
	 * file.
	 * @param url the URL of the catalog file.
	 */
	public TychoSeparatedReader ( URL url ) {
		this();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Tycho Catalogue";
	}

	/**
	 * Checks if the catalog data is in a directory.
	 * @return true if the catalog data is in a directory.
	 */
	public boolean isInDirectory ( ) {
		return true;
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
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/1/1239/</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Opens a catalog. This method must be invoked at first.
	 * @param coor the R.A. and Decl. of the center.
	 * @param fov  the field of view to read in degree.
	 * @exception IOException if a file cannot be accessed.
	 * @exception FileNotFoundException if a file does not exists in
	 * any URL.
	 * @exception CdromNotFoundException if this reader is to read 
	 * data from CD-ROMs and a file does not exists in any URL.
	 */
	public void open ( Coor coor, double fov )
		throws IOException, FileNotFoundException, CdromNotFoundException
	{
		center_coor = coor;
		circum_area = null;
		if (coor != null)
			circum_area = new CircumArea(coor, fov / 2.0);

		current_file_index = -1;
		current_block_index = -1;
		current_star_index = -1;
		read_star_index = -1;

		stream = null;
	}

	/**
	 * Reads one star from the opened catalog. After this method is 
	 * invoked, the cursor is promoted to tne next star. When every 
	 * data is read, it returns null.
	 * @return a star data.
	 * @exception IOException if a file cannot be accessed.
	 * @exception FileNotFoundException if a file does not exists in
	 * any URL.
	 * @exception CdromNotFoundException if this reader is to read 
	 * data from CD-ROMs and a file does not exists in any URL.
	 * @exception QueryFailException if the query to the server is 
	 * failed.
	 */
	public CatalogStar readNext()
		throws IOException, FileNotFoundException, CdromNotFoundException, QueryFailException
	{
		byte[] b = new byte[351];

		while (true) {
			current_star_index++;

			// Reaches to the end.
			if (current_star_index >= 53000 * 19 + 51332)
				return null;

			if (current_star_index % 530 == 0) {
				// Goes to the next block.
				current_block_index++;

				// Checks if the current block is in the field.
				Coor start_coor = new Coor((double)TychoIndex.ra_start[(int)current_block_index], (double)TychoIndex.decl_start[(int)current_block_index]);
				Coor end_coor = new Coor((double)TychoIndex.ra_end[(int)current_block_index], (double)TychoIndex.decl_end[(int)current_block_index]);
				if (circum_area == null  ||  circum_area.overlapsArea(start_coor, end_coor)) {
					// In the case in the field.
					int next_file_index = current_star_index / 53000;

					if (current_file_index != next_file_index) {
						// Opens the next star data file.
						if (stream != null)
							stream.close();

						current_file_index = next_file_index;

						// Opens the star data file.
						stream = openDataFile(current_file_index);

						read_star_index = current_file_index * 53000 - 1;
					}

					// Skips the stars in the file.
					StreamSkipTimer skip_timer = new StreamSkipTimer(stream, 10000);
					skip_timer.skip((current_star_index - 1 - read_star_index) * 351);

					read_star_index = current_star_index - 1;
				} else {
					// In the case not in the field.
					current_star_index += 530 - 1;

					continue;
				}
			}

			// Reads a star data.
			stream.readFully(b, 0, 351);
			String record = new String(b);

			read_star_index++;

			CatalogStar star = createStar(record);

			if (star == null)
				continue;

			return star;
		}
	}

	/**
	 * Closes a catalog. This method must be invoked finally.
	 * @exception IOException if a file cannot be accessed.
	 */
	public void close()
		throws IOException
	{
		if (stream != null)
			stream.close();

		stream = null;
	}

	/**
	 * Opens the star data file.
	 * @param file_number the file number.
	 * @return the input stream.
	 * @exception IOException if a file cannot be accessed.
	 */
	private DataInputStream openDataFile ( int file_number )
		throws IOException
	{
		URL url = url_set.existsFile("tyc_" + Format.formatIntZeroPadding(file_number, 2) + ".dat");

		return Decoder.newInputStream(url);
	}

	/**
	 * Creates a <code>CatalogStar</code> object from the specified
	 * one line record in the file.
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
}
