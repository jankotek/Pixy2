/*
 * @(#)Ucac1Reader.java
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
 * The <code>Ucac1Reader</code> is a class to read the UCAC1 Catalogue. 
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class Ucac1Reader extends CatalogReader {
	/**
	 * The number of files.
	 */
	protected final static int file_N = 169;

	/**
	 * The current_input stream.
	 */
	protected DataInputStream current_stream;

	/**
	 * The index of current file.
	 */
	protected int current_file_index;

	/**
	 * The base R.A. of current chunk.
	 */
	protected double current_base_ra;

	/**
	 * The base Decl. of current zone.
	 */
	protected double current_base_decl;

	/**
	 * The record size in current zone.
	 */
	protected int current_record_size;

	/**
	 * The seek position of chunks in current zone.
	 */
	protected int[] current_seek_position = new int[78];

	/**
	 * The current position in the file.
	 */
	protected int current_position;

	/**
	 * The current chunk number.
	 */
	protected int current_chunk;

	/**
	 * The current star number.
	 */
	protected int current_star_number;

	/**
	 * The last star number in current chunk.
	 */
	protected int current_end_number;

	/**
	 * The current zone number.
	 */
	protected int current_zone;

	/**
	 * The circum area to read stars.
	 */
	protected CircumArea circum_area;

	/**
	 * The list of zones to be read.
	 */
	protected Vector zone_list;

	/**
	 * Constructs an empty <code>Ucac1Reader</code>.
	 */
	public Ucac1Reader ( ) {
		url_set.ignoreCases(true);
		url_set.acceptGzipped(true);

		setDefaultURL();
	}

	/**
	 * Constructs a <code>Ucac1Reader</code> with URL of directory 
	 * containing the AC 2000.2 Catalogue.
	 * @param url the URL of directory containing CD-ROMs data.
	 */
	public Ucac1Reader ( URL url ) {
		this();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "UCAC1 Catalogue";
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
		html += "First U.S. Naval Observatory CCD Astrograph Catalog (UCAC1)<br>";
		html += "Astronomical Data Center catalog No. 1268<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/1/1268/UCAC1/bindat/</font></u>";
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
	 * any URL, and the file is in any CD-ROMs.
	 * @exception CdromNotFoundException if a file does not exists in
	 * any URL, and the file is in a CD-ROM.
	 */
	public void open ( Coor coor, double fov )
		throws IOException, FileNotFoundException, CdromNotFoundException
	{
		center_coor = coor;
		circum_area = null;
		if (coor != null)
			circum_area = new CircumArea(coor, fov / 2.0);
		zone_list = new Vector();

		// Select files to read.
		for (int i = 0 ; i < file_N ; i++) {
			Integer zone = new Integer(i+1);

			double decl_start = (double)i * 0.5 - 90.0;
			double decl_end = decl_start + 0.5;

			Coor start_coor = new Coor(0, decl_start);
			Coor end_coor = new Coor(180, decl_end);

			if (circum_area == null  ||  circum_area.overlapsArea(start_coor, end_coor)) {
				zone_list.addElement(zone);
			} else {
				start_coor = new Coor(180, decl_start);
				end_coor = new Coor(0, decl_end);

				if (circum_area == null  ||  circum_area.overlapsArea(start_coor, end_coor))
					zone_list.addElement(zone);
			}
		}

		// Initializes to read data one by one.
		current_stream = null;
	}

	/**
	 * Reads one data from the opened catalog. After this method is 
	 * invoked, the cursor is promoted to tne next star. When every 
	 * data is read, it returns null. Note that data with the same ID
	 * are also returned one by one.
	 * @return a star data.
	 * @exception IOException if a file cannot be accessed.
	 * @exception FileNotFoundException if a file does not exists in
	 * any URL, and the file is in any CD-ROMs.
	 * @exception CdromNotFoundException if a file does not exists in
	 * any URL, and the file is in a CD-ROM.
	 * @exception QueryFailException if the query to the server is 
	 * failed.
	 */
	public CatalogStar readNext ( )
		throws IOException, FileNotFoundException, CdromNotFoundException, QueryFailException
	{
		byte[] buffer = null;
		byte[] b = new byte[4];

		while (true) {
			try {
				try {
					// In the case of the very first reading.
					if (current_stream == null) {
						current_file_index = -1;
						throw new EOFException();
					}
					if (current_chunk < 0) {
						throw new EndOfChunkException();
					}

					current_star_number++;
					if (current_star_number > current_end_number)
						throw new EndOfChunkException();

					buffer = new byte[current_record_size];

					current_stream.readFully(buffer, 0, current_record_size);
					current_position += current_record_size;

					int ra_offset = 0;
					if (current_record_size == 17)
						ra_offset = Format.intValueOf(buffer, 0, 3);
					else					
						ra_offset = Format.intValueOf(buffer, 0, 4);
					double ra = current_base_ra + (double)ra_offset / 1000.0 / 3600.0;

					int buffer_index = 0;
					if (current_record_size == 18)
						buffer_index = 1;

					int decl_offset = (((buffer[3 + buffer_index] >> 3) & 31) << 16);
					decl_offset |= (((buffer[3 + buffer_index] << 5) & 224) << 8);
					decl_offset |= (((buffer[4 + buffer_index] >> 3) & 31) << 8);
					decl_offset |= ((buffer[4 + buffer_index] << 5) & 224);
					decl_offset |= ((buffer[5 + buffer_index] >> 3) & 31);
					double decl = current_base_decl + (double)decl_offset / 1000.0 / 3600.0;

					Coor coor = new Coor(ra, decl);

					int mag_offset = (((buffer[9 + buffer_index] >> 6) & 3) << 8);
					mag_offset |= ((buffer[9 + buffer_index] << 2) & 252);
					mag_offset |= ((buffer[10 + buffer_index] >> 6) & 3);
					double mag = 7.50 + (double)mag_offset / 100.0;

					if ((circum_area == null  ||  circum_area.inArea(coor))  &&  mag <= limiting_mag) {
						b[0] = buffer[5 + buffer_index];
						b[1] = buffer[6 + buffer_index];
						b[0] &= (byte)7;
						int epoch_offset = Format.intValueOf(b, 0, 2);
						double epoch = 1998.0 + (double)epoch_offset / 1000.0;

						int pmx_offset = (((buffer[11 + buffer_index] << 1) & 30) << 8);
						pmx_offset |= (((buffer[12 + buffer_index] >> 7) & 1) << 8);
						pmx_offset |= ((buffer[12 + buffer_index] << 1) & 254);
						pmx_offset |= ((buffer[13 + buffer_index] >> 7) & 1);
						double pmx = (double)(pmx_offset - 2048) / 10.0;

						int pmy_offset = (((buffer[14 + buffer_index] >> 1) & 31) << 8);
						pmy_offset |= ((buffer[14 + buffer_index] << 7) & 128);
						pmy_offset |= ((buffer[15 + buffer_index] >> 1) & 127);
						double pmy = (double)(pmy_offset - 2048) / 10.0;

						Ucac1Star star = new Ucac1Star(current_star_number, coor, mag, epoch, pmx, pmy);

						if (center_coor != null) {
							ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
							Position position = cmf.mapCoordinatesToXY(coor);
							star.setPosition(position);
						}

						return star;
					}
				} catch ( EndOfChunkException exception ) {
					// Goes to the next chunk.
					int bytes_to_skip = -1;
					while (bytes_to_skip < 0) {
						current_chunk++;
						if (current_chunk == 78)
							throw new EOFException();

						current_base_ra = (double)current_chunk * 16777.216 / 3600.0;

						bytes_to_skip = current_seek_position[current_chunk] - current_position;
					}

					StreamSkipTimer skip_timer = new StreamSkipTimer(current_stream, 10000);
					skip_timer.skip(bytes_to_skip);
					current_position += bytes_to_skip;

					buffer = new byte[32];
					current_stream.readFully(buffer, 0, 32);
					current_position += 32;

					int length = Format.intValueOf(buffer, 0, 4);
					int first_number = Format.intValueOf(buffer, 4, 4);
					int header_length = Format.intValueOf(buffer, 8, 2);
					int chunk_no = Format.intValueOf(buffer, 10, 1);
					int oor_pm4 = Format.intValueOf(buffer, 11, 1);
					int oor_pm2 = Format.intValueOf(buffer, 12, 1);
					int oor_mag = Format.intValueOf(buffer, 13, 1);
					int number_of_records = (length - header_length) / current_record_size;

					if (header_length > 32) {
						bytes_to_skip = header_length - 32;
						skip_timer = new StreamSkipTimer(current_stream, 10000);
						skip_timer.skip(bytes_to_skip);
						current_position += bytes_to_skip;
					}

					current_star_number = first_number - 1;
					current_end_number = first_number + number_of_records - 1;

					// Checks if the next chunk is within the field.
					if (current_record_size == 17) {
						double ra_end = current_base_ra + 256.0 * 256.0 * 256.0 / 1000.0 / 3600.0;
						Coor start_coor = new Coor(current_base_ra, current_base_decl);
						Coor end_coor = new Coor(ra_end, current_base_decl + 0.5);
						if (circum_area == null  ||  circum_area.overlapsArea(start_coor, end_coor)) {
							// Within the field.
						} else {
							// Not within the field.

							// Skips to the next chunk.
							bytes_to_skip = current_record_size * number_of_records;
							skip_timer = new StreamSkipTimer(current_stream, 10000);
							skip_timer.skip(bytes_to_skip);
							current_position += bytes_to_skip;

							current_star_number = current_end_number;
						}
					}
				}
			} catch (EOFException e) {
				// All files have been read.
				if (current_file_index + 1 ==  zone_list.size())
					return null;

				Integer zone = (Integer)zone_list.elementAt(current_file_index + 1);
				current_zone = zone.intValue();

				String filename = "z" + Format.formatIntZeroPadding(current_zone, 3) + ".bin";
				URL url = url_set.existsFile(filename);
				DataInputStream stream = Decoder.newInputStream(url);

				// Closes the current file after confirming the next file can be open properly.
				if (current_stream != null)
					current_stream.close();
				current_file_index++;

				current_stream = stream;

				// Reads the file header.
				buffer = new byte[80];
				current_stream.readFully(buffer, 0, 80);
				String record = new String(buffer);

				current_record_size = Integer.parseInt(record.substring(7, 9));
				int start_number = Integer.parseInt(record.substring(43, 51));
				int end_number = Integer.parseInt(record.substring(52, 60));

				current_base_decl = (double)(current_zone - 1) * 0.5 - 90.0;

				buffer = new byte[80*4];
				stream.readFully(buffer, 0, 80*4);
				for (int i = 0 ; i < 78 ; i++)
					current_seek_position[i] = Format.intValueOf(buffer, i*4, 4);

				current_position = 80 + 80*4;

				// Reads the first chunk header.
				current_chunk = -1;
			}
		}
	}

	/**
	 * Closes a catalog. This method must be invoked finally.
	 * @exception IOException if a file cannot be accessed.
	 */
	public void close ( )
		throws IOException
	{
		if (current_stream != null)
			current_stream.close();
		current_stream = null;
	}

	/**
	 * The <code>EndOfChunkException</code> is an exception thrown 
	 * when all records in a chunk are read.
	 */
	protected class EndOfChunkException extends Exception {
	}
}
