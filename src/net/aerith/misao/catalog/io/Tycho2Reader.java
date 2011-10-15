/*
 * @(#)Tycho2Reader.java
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
 * The <code>Tycho2Reader</code> is a class to read the Tycho-2 
 * Catalogue file.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class Tycho2Reader extends CatalogReader {
	/**
	 * The circum area to read stars.
	 */
	protected CircumArea circum_area;

	/**
	 * The input stream reader of index.
	 */
	protected BufferedReader index_reader;

	/**
	 * The input stream to read stars.
	 */
	protected DataInputStream stream;

	/**
	 * The index of current file.
	 */
	protected int current_file_index;

	/**
	 * The index of current plate.
	 */
	protected int current_plate_index;

	/**
	 * The index of current star.
	 */
	protected int current_star_index;

	/**
	 * The index of already read star.
	 */
	protected int read_star_index;

	/**
	 * The start index of stars in the current plate.
	 */
	protected int star_index_start;

	/**
	 * The end index of stars in the current plate.
	 */
	protected int star_index_end;

	/**
	 * The minimum R.A. of the current plate.
	 */
	protected double ra_start;

	/**
	 * The maximum R.A. of the current plate.
	 */
	protected double ra_end;

	/**
	 * The minimum Decl. of the current plate.
	 */
	protected double decl_start;

	/**
	 * The maximum Decl. of the current plate.
	 */
	protected double decl_end;

	/**
	 * The minimum R.A. of the next plate.
	 */
	protected double next_ra_start;

	/**
	 * The maximum R.A. of the next plate.
	 */
	protected double next_ra_end;

	/**
	 * The minimum Decl. of the next plate.
	 */
	protected double next_decl_start;

	/**
	 * The maximum Decl. of the next plate.
	 */
	protected double next_decl_end;

	/**
	 * Constructs an empty <code>Tycho2Reader</code>.
	 */
	public Tycho2Reader ( ) {
		url_set.acceptGzipped(true);

		setDefaultURL();
	}

	/**
	 * Constructs a <code>Tycho2Reader</code> with URL of the catalog
	 * file.
	 * @param url the URL of the catalog file.
	 */
	public Tycho2Reader ( URL url ) {
		this();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Tycho-2 Catalogue";
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
		html += "The Tycho-2 Catalogue<br>";
		html += "Astronomical Data Center catalog No. 1259<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/1/1259/</font></u>";
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

		URL url = url_set.existsFile("index.dat");

		index_reader = Decoder.newReader(url);

		current_file_index = 0;
		current_plate_index = 0;
		current_star_index = 0;
		read_star_index = 0;

		// Reads the index and sets the start point.
		String s = index_reader.readLine();
		star_index_end = Integer.parseInt(s.substring(0, 7).trim());
		next_ra_start = Double.parseDouble(s.substring(15, 21).trim());
		next_ra_end = Double.parseDouble(s.substring(22, 28).trim());
		next_decl_start = Double.parseDouble(s.substring(29, 35).trim());
		next_decl_end = Double.parseDouble(s.substring(36, 42).trim());

		// Opens the first star data file.
		stream = openDataFile(current_file_index);
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
		byte[] b = new byte[207];

		while (true) {
			current_star_index++;

			if (current_star_index == star_index_end) {
				// Goes to the next plate.
				current_plate_index++;

				// Reaches to the last plate.
				if (current_plate_index >= 9538)
					return null;

				star_index_start = star_index_end;
				ra_start = next_ra_start;
				ra_end = next_ra_end;
				decl_start = next_decl_start;
				decl_end = next_decl_end;

				// Reads the index and sets the start point.
				String s = index_reader.readLine();
				star_index_end = Integer.parseInt(s.substring(0, 7).trim());
				next_ra_start = Double.parseDouble(s.substring(15, 21).trim());
				next_ra_end = Double.parseDouble(s.substring(22, 28).trim());
				next_decl_start = Double.parseDouble(s.substring(29, 35).trim());
				next_decl_end = Double.parseDouble(s.substring(36, 42).trim());

				// Checks if the current plate is in the field.
				Coor start_coor = new Coor(ra_start, decl_start);
				Coor end_coor = new Coor(ra_end, decl_end);
				if (circum_area == null  ||  circum_area.overlapsArea(start_coor, end_coor)) {
					// In the case in the field.
					int next_file_index = (star_index_start - 1) / 127000;

					if (current_file_index == next_file_index) {
						// Skips the stars in the plate.
						StreamSkipTimer skip_timer = new StreamSkipTimer(stream, 10000);
						skip_timer.skip((current_star_index - 1 - read_star_index) * 207);
					} else {
						// Opens the next star data file.
						stream.close();

						current_file_index = next_file_index;

						// Opens the star data file.
						stream = openDataFile(current_file_index);

						// Skips the stars in the plate.
						StreamSkipTimer skip_timer = new StreamSkipTimer(stream, 10000);
						skip_timer.skip(((current_star_index - 1) % 127000) * 207);
					}

					read_star_index = current_star_index - 1;
				} else {
					// In the case not in the field.
					current_star_index = star_index_end;
				}

				current_star_index--;
			} else {
				// Reads a star data.
				try {
					stream.readFully(b, 0, 207);
					String record = new String(b);

					read_star_index++;

					short tyc1 = Short.parseShort(record.substring(0, 4).trim());
					short tyc2 = Short.parseShort(record.substring(5, 10).trim());
					short tyc3 = Short.parseShort(record.substring(11, 12).trim());

					String ra = record.substring(15, 27).trim();
					String decl = record.substring(28, 40).trim();

					if (ra.length() != 0  &&  decl.length() != 0) {
						Coor coor = new Coor(Format.doubleValueOf(ra), Format.doubleValueOf(decl));

						double mag = 999.9;
						double bt_mag = 999.9;
						double vt_mag = 999.9;

						if (record.substring(123, 129).trim().length() > 0) {
							vt_mag = Format.doubleValueOf(record.substring(123, 129).trim());
							mag = vt_mag;
						}
						if (record.substring(110, 116).trim().length() > 0) {
							bt_mag = Format.doubleValueOf(record.substring(110, 116).trim());
							if (mag > 30.0)
								mag = bt_mag;
						}

						if (mag <= limiting_mag) {
							if (circum_area == null  ||  circum_area.inArea(coor)) {
								Tycho2Star star = new Tycho2Star(tyc1, tyc2, tyc3, coor);
								star.setMag(mag);

								if (bt_mag < 30.0)
									star.setBtMagnitude(bt_mag);
								if (vt_mag < 30.0)
									star.setVtMagnitude(vt_mag);

								if (record.substring(41, 48).trim().length() > 0)
									star.setProperMotionInRA(Format.doubleValueOf(record.substring(41, 48).trim()));
								if (record.substring(49, 56).trim().length() > 0)
									star.setProperMotionInDecl(Format.doubleValueOf(record.substring(49, 56).trim()));
								if (record.substring(57, 60).trim().length() > 0)
									star.setRAError(Format.doubleValueOf(record.substring(57, 60).trim()));
								if (record.substring(61, 64).trim().length() > 0)
									star.setDeclError(Format.doubleValueOf(record.substring(61, 64).trim()));
								if (record.substring(75, 82).trim().length() > 0)
									star.setEpochInRA(Format.doubleValueOf(record.substring(75, 82).trim()));
								if (record.substring(117, 122).trim().length() > 0)
									star.setBtMagnitudeError(Format.doubleValueOf(record.substring(117, 122).trim()));
								if (record.substring(130, 135).trim().length() > 0)
									star.setVtMagnitudeError(Format.doubleValueOf(record.substring(130, 135).trim()));

								if (center_coor != null) {
									ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
									Position position = cmf.mapCoordinatesToXY(coor);
									star.setPosition(position);
								}

								return star;
							}
						}
					}
				} catch ( EOFException exception ) {
					// Goes to the next file.
					current_file_index++;
					if (current_file_index >= 20)
						return null;

					stream.close();

					// Opens the star data file.
					stream = openDataFile(current_file_index);

					current_star_index--;
					read_star_index = current_star_index;
				}
			}
		}
	}

	/**
	 * Closes a catalog. This method must be invoked finally.
	 * @exception IOException if a file cannot be accessed.
	 */
	public void close()
		throws IOException
	{
		index_reader.close();
		stream.close();
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
		URL url = null;

		try {
			url = url_set.existsFile("tyc2.dat." + Format.formatIntZeroPadding(file_number, 2));
		} catch ( FileNotFoundException exception ) {
			url = url_set.existsFile("tyc2_" + Format.formatIntZeroPadding(file_number, 2) + ".dat");
		}

		return Decoder.newInputStream(url);
	}
}
