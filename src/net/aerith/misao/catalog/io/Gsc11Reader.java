/*
 * @(#)Gsc11Reader.java
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
 * The <code>Gsc11Reader</code> is a class to read GSC 1.1 CD-ROMs.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public class Gsc11Reader extends CatalogReader {
	/**
	 * The number of directories of data.
	 */
	protected final static int dir_N = 24;

	/**
	 * The array of number of files in each directory.
	 */
	protected final static int dir_max[] = {
		 593, 1177, 1728, 2258, 2780, 3245, 3651, 4013, 
		4293, 4491, 4614, 4662, 5259, 5837, 6411, 6988, 
		7522, 8021, 8463, 8839, 9133, 9345, 9489, 9537
	};

	/**
	 * The array of directory names.
	 */
	protected final static String dir_name[] = {
		"n0000", "n0730", "n1500", "n2230", "n3000", "n3730", "n4500", "n5230", 
		"n6000", "n6730", "n7500", "n8230", "s0000", "s0730", "s1500", "s2230", 
		"s3000", "s3730", "s4500", "s5230", "s6000", "s6730", "s7500", "s8230"
	};

	/**
	 * The current input stream.
	 */
	protected DataInputStream current_stream;

	/**
	 * The index of current file.
	 */
	protected int current_file_index;

	/**
	 * The current area number.
	 */
	protected short current_area_number;

	/**
	 * The circum area to read stars.
	 */
	protected CircumArea circum_area;

	/**
	 * The array of plate date IDs.
	 */
	protected String[] plate_date_id = new String[1518];

	/**
	 * The array of plate date.
	 */
	protected String[] plate_date = new String[1518];

	/**
	 * The list of filename to be read.
	 */
	protected Vector filename_list;

	/**
	 * Constructs an empty <code>Gsc11Reader</code>.
	 */
	public Gsc11Reader ( ) {
		url_set.ignoreCases(true);

		setDefaultURL();
	}

	/**
	 * Constructs a <code>Gsc11Reader</code> with URL of directory 
	 * containing GSC 1.1 CD-ROMs data.
	 * @param url the URL of directory containing CD-ROMs data.
	 */
	public Gsc11Reader ( URL url ) {
		this();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Guide Star Catalog 1.1";
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
	 * Checks if reading GSC-ACT or not.
	 * @return true if reading GSC-ACT.
	 */
	public boolean isAct ( ) {
		return false;
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
		html += "HST Guide Star Catalogue, Version 1.1<br>";
		html += "Astronomical Data Center catalog No. 1220<br>";
		html += "</p><p>";
		html += "The set of 2 CD-ROMs. <br>";
		html += "</p><p>";
		html += "References:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">http://www-gsss.stsci.edu/gsc/gsc.html</font></u>";
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
		filename_list = new Vector();

		// Reads the plate table.
		try {
			byte b[] = new byte[115];
			URL url = url_set.existsFile("TABLES/PLATES.TBL");

			DataInputStream stream = Decoder.newInputStream(url);
			StreamSkipTimer skip_timer = new StreamSkipTimer(stream, 10000);
			skip_timer.skip(28800);
			for (int i = 0 ; i < 1518 ; i++) {
				stream.readFully(b, 0, 115);
				String s = new String(b);
				plate_date_id[i] = s.substring(9, 13);
				plate_date[i] = s.substring(27, 36);
			}
			stream.close();
		} catch ( FileNotFoundException exception ) {
			throw exception;
		}

		// Reads the region table.
		try {
			byte[] b = new byte[48];
			URL url = url_set.existsFile("TABLES/REGIONS.TBL");

			DataInputStream stream = Decoder.newInputStream(url);
			StreamSkipTimer skip_timer = new StreamSkipTimer(stream, 10000);
			skip_timer.skip(14400);
			try {
				for (int i = 0 ; i < 9537 ; i++) {
					stream.readFully(b, 0, 48);

					String s = new String(b);
					int file_number = Integer.parseInt(s.substring(0,5).trim());

					Coor start_coor = new Coor(Integer.parseInt(s.substring(7,9).trim()), Integer.parseInt(s.substring(10,12).trim()), (double)(Float.valueOf(s.substring(13,18).trim()).floatValue()), (s.charAt(31) == '-'), Integer.parseInt(s.substring(32,34).trim()), 0, (double)(Float.valueOf(s.substring(35,39).trim()).floatValue()) * 60.0);
					Coor end_coor = new Coor(Integer.parseInt(s.substring(19,21).trim()), Integer.parseInt(s.substring(22,24).trim()), (double)(Float.valueOf(s.substring(25,30).trim()).floatValue()), (s.charAt(40) == '-'), Integer.parseInt(s.substring(41,43).trim()), 0, (double)(Float.valueOf(s.substring(44,48).trim()).floatValue()) * 60.0);

					if (circum_area == null  ||  circum_area.overlapsArea(start_coor, end_coor)) {
						int j = 0;
						for (j = 0 ; j < dir_N ; j++) {
							if (file_number <= dir_max[j])
								break;
						}
						String filename = dir_name[j] + "/" + Format.formatIntZeroPadding(file_number, 4);
						filename_list.addElement(filename);
					}
				}
			} catch ( EOFException exception ) {
			}
			stream.close();
		} catch ( FileNotFoundException exception ) {
			throw exception;
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
		while (true) {
			try {
				// In the case of the very first reading.
				if (current_stream == null) {
					current_file_index = -1;
					throw new EOFException();
				}

				byte[] b = new byte[45];
				current_stream.readFully(b, 0, 45);

				if (b[0] == ' ')
					throw new EOFException();

				String s = new String(b);

				Coor coor = new Coor(Double.valueOf(s.substring(5,14).trim()).doubleValue(), Double.valueOf(s.substring(14,23).trim()).doubleValue());
				float mag = Float.valueOf(s.substring(28,33).trim()).floatValue();

				if ((circum_area == null  ||  circum_area.inArea(coor))  &&  (double)mag <= limiting_mag) {
					short star_number = Short.parseShort(s.substring(1,5));
					short position_error10 = (short)((b[25] - '0') * 10 + (b[27] - '0'));
					short mag_error100 = (short)((b[33] - '0') * 100 + (b[35] - '0') * 10 + (b[36] - '0'));
					byte band = b[38];
					byte star_class = b[39];
					byte[] plate = new byte[4];
					for (int i = 0 ; i < 4 ; i++)
						plate[i] = b[40+i];
					byte star_flag = b[44];

					byte[] epoch = null;
					String plate_str = new String(plate);
					for (int i = 0 ; i < 1518 ; i++) {
						if (plate_date_id[i].equals(plate_str)) {
							try {
								epoch = plate_date[i].getBytes("ASCII");
							} catch ( UnsupportedEncodingException exception ) {
								epoch = plate_date[i].getBytes();
							}
							break;
						}
					}

					Gsc11Star star = null;
					if (isAct()) {
						star = new GscActStar(current_area_number, star_number, coor, (double)mag,
											  position_error10, mag_error100, band, star_class, plate, 
											  star_flag, epoch);
					} else {
						star = new Gsc11Star(current_area_number, star_number, coor, (double)mag,
											 position_error10, mag_error100, band, star_class, plate, 
											 star_flag, epoch);
					}

					if (center_coor != null) {
						ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
						Position position = cmf.mapCoordinatesToXY(coor);
						star.setPosition(position);
					}

					return star;
				}
			} catch (EOFException e) {
				// All files have been read.
				if (current_file_index + 1 ==  filename_list.size())
					return null;

				String filename = (String)filename_list.elementAt(current_file_index + 1);

				try {
					URL url = url_set.existsFile("GSC/" + filename + ".GSC");
					DataInputStream stream = Decoder.newInputStream(url);

					// Closes the current file after confirming the next file can be open properly.
					if (current_stream != null)
						current_stream.close();
					current_file_index++;
					current_area_number = Short.parseShort(filename.substring(filename.length() - 4, filename.length()));

					current_stream = stream;
					StreamSkipTimer skip_timer = new StreamSkipTimer(current_stream, 10000);
					skip_timer.skip(8640);
				} catch ( FileNotFoundException exception ) {
					String disk = "volume 2 (southern part)";
					if (filename.toLowerCase().charAt(0) == 'n'  ||  filename.substring(0,5).equalsIgnoreCase("s0000"))
						disk = "volume 1 (northern part)";
					throw new CdromNotFoundException(disk);
				}

				return readNext();
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
		current_stream.close();
		current_stream = null;
	}
}
