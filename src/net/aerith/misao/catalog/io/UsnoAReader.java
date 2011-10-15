/*
 * @(#)UsnoAReader.java
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
 * The <code>UsnoAReader</code> is a class to read USNO-A1.0/A2.0
 * CD-ROMs.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public abstract class UsnoAReader extends CatalogReader {
	/**
	 * The buffer size to read data at one time.
	 */
	protected final static int buffer_size = 100000;

	/**
	 * The circum area to read stars.
	 */
	protected CircumArea circum_area;

	/**
	 * The current input stream of ACC file.
	 */
	protected DataInputStream current_stream_acc;

	/**
	 * The current input stream of CAT file.
	 */
	protected DataInputStream current_stream_cat;

	/**
	 * The buffer to read data.
	 */
	protected byte[] data_buffer = new byte[buffer_size * 12];

	/**
	 * The index of current file.
	 */
	protected int current_file_index;

	/**
	 * The index of block in the current file.
	 */
	protected int current_block_index;

	/**
	 * The size of current block.
	 */
	protected int current_block_size;

	/**
	 * The index of buffer in the current block.
	 */
	protected int current_buffer_index;

	/**
	 * The number of buffers to read current block.
	 */
	protected int current_buffer_count;

	/**
	 * The index of data in the current buffer.
	 */
	protected int current_data_index;

	/**
	 * The index of star in the current file.
	 */
	protected int current_star_index;

	/**
	 * The R.A. and Decl. at the south west corner of an area to read.
	 */
	protected Coor current_start_coor;

	/**
	 * The R.A. and Decl. at the north east corner of an area to read.
	 */
	protected Coor current_end_coor;

	/**
	 * Gets the number of files. It must be overrided in the 
	 * subclasses.
	 * @return the number of files.
	 */
	protected abstract int getNumberOfFiles();

	/**
	 * Gets the file number of the specified index. It must be 
	 * overrided in the subclasses.
	 * @param index the index.
	 * @return the file number.
	 */
	protected abstract int getFileNumber ( int index );

	/**
	 * Gets the disc number of the specified index. It must be 
	 * overrided in the subclasses.
	 * @param index the index.
	 * @return the disc number.
	 */
	protected abstract int getDiscNumber ( int index );

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 5.0;
	}

	/**
	 * Creates a <code>UsnoAStar</code> object based on the specified 
	 * parameters. It must be overrided in the subclasses.
	 * @param file_numer  the file number.
	 * @param star_number the star number in the area.
	 * @param coor        the R.A. and Decl.
	 * @param valid_R_mag true if R magnitude is recorded.
	 * @param R_mag10     the R magnitude in 0.1 mag unit.
	 * @param valid_B_mag true if B magnitude is recorded.
	 * @param B_mag10     the B magnitude in 0.1 mag unit.
	 * @param V_mag       the V magnitude.
	 * @return the <code>UsnoAStar</code> object
	 */
	protected abstract UsnoAStar createStar ( short file_number,
											  int star_number,
											  Coor coor,
											  boolean valid_R_mag,
											  short R_mag10,
											  boolean valid_B_mag,
											  short B_mag10,
											  double V_mag );

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

		// Initializes to read data one by one.
		current_stream_acc = null;
		current_stream_cat = null;
		current_file_index = -1;
	}

	/**
	 * Reads one data from the opened catalog. After this method is 
	 * invoked, the cursor is promoted to tne next star. When every 
	 * data is read, it returns null.
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
				if (current_stream_acc == null  ||  current_stream_cat == null  ||  current_buffer_index < 0  ||  current_data_index < 0) {
					throw new EOFException();
				}

				int read_size = buffer_size;
				if (current_buffer_index == current_buffer_count - 1)
					read_size = current_block_size - buffer_size * (current_buffer_count - 1);

				// All data in the buffer have been read.
				if (current_data_index == read_size) {
					current_buffer_index++;
					current_data_index = 0;

					// All data in the block have been read.
					if (current_buffer_index == current_buffer_count)
						throw new EOFException();

					if (current_buffer_index == current_buffer_count - 1)
						read_size = current_block_size - buffer_size * (current_buffer_count - 1);
					current_stream_cat.readFully(data_buffer, 0, read_size * 12);
				}

				current_star_index++;

				int m = Format.intValueOf(data_buffer, current_data_index * 12 + 8, 4);
				if (m < 0)
					m = - m;
				int mr = m % 1000;
				int mb = (int)(m / 1000);
				mb = mb % 1000;
				boolean valid_R = true;
				boolean valid_B = true;
				if (mr == 0  ||  mr >= 250)
					valid_R = false;
				if (mb == 0  ||  mb >= 250)
					valid_B = false;
				double mag_r = (double)mr / 10.0;
				double mag_b = (double)mb / 10.0;

				// preliminary V mag
				double mag_v = 0.0;
				if (valid_R  &&  valid_B)
					mag_v = MagnitudeSystem.getUsnoVMag(mag_r, mag_b);
				else if (valid_R)
					mag_v = mag_r;
				else if (valid_B)
					mag_v = mag_b;
				else
					mag_v = 99.9;

				if (mag_v <= limiting_mag) {
					int ra = Format.intValueOf(data_buffer, current_data_index * 12, 4);
					int decl = Format.intValueOf(data_buffer, current_data_index * 12 + 4, 4);

					Coor coor = new Coor((double)ra / 100.0 / 3600.0, (double)decl / 100.0 / 3600.0 - 90.0);
					if (circum_area == null  ||  circum_area.inArea(coor)) {
						UsnoAStar star = createStar((short)getFileNumber(current_file_index), current_star_index, coor, valid_R, (short)mr, valid_B, (short)mb, mag_v);

						if (center_coor != null) {
							ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
							Position position = cmf.mapCoordinatesToXY(coor);
							star.setPosition(position);
						}

						current_data_index++;
						return star;
					}
				}

				current_data_index++;
			} catch (EOFException e) {
				if (current_stream_acc != null  &&  current_stream_cat != null  &&  current_block_index + 25 <= 2375) {
					// Goes to the next block in the current file.
					byte b[] = new byte[30];
					current_stream_acc.readFully(b, 0, 30);
					String s = new String(b);
					int ptr = Integer.parseInt(s.substring(6,17).trim());
					current_block_size = Integer.parseInt(s.substring(18,29).trim());

					current_start_coor.setRA((double)((current_block_index + 25) * 15) / 100.0);
					current_end_coor.setRA((double)((current_block_index + 25 + 25) * 15) / 100.0);

					current_block_index += 25;
					current_buffer_index = -1;
					current_data_index = -1;

					if (circum_area == null  ||  circum_area.overlapsArea(current_start_coor, current_end_coor)) {
						current_buffer_count = ((current_block_size - 1) / buffer_size) + 1;
						current_buffer_index = 0;
						current_data_index = 0;
						int offset = (ptr - 1) - current_star_index;
						current_star_index = ptr - 1;

						StreamSkipTimer skip_timer = new StreamSkipTimer(current_stream_cat, 10000);
						skip_timer.skip(offset * 12);

						int read_size = buffer_size;
						if (current_buffer_index == current_buffer_count - 1)
							read_size = current_block_size - buffer_size * (current_buffer_count - 1);
						current_stream_cat.readFully(data_buffer, 0, read_size * 12);
					}
				} else {
					// All data in the current file have been read.

					// All files have been read.
					if (current_file_index + 1 ==  getNumberOfFiles())
						return null;

					current_start_coor = new Coor(0.0, (double)getFileNumber(current_file_index + 1) / 10.0 - 90.0);
					current_end_coor = new Coor(360.0, (double)(getFileNumber(current_file_index + 1) + 75) / 10.0 - 90.0);

					if (circum_area == null  ||  circum_area.overlapsArea(current_start_coor, current_end_coor)) {
						try {
							String filename = Format.formatIntZeroPadding(getFileNumber(current_file_index + 1), 4);
							URL url = url_set.existsFile("ZONE" + filename + ".ACC");
							DataInputStream stream_acc = Decoder.newInputStream(url);
							url = url_set.existsFile("ZONE" + filename + ".CAT");
							DataInputStream stream_cat = Decoder.newInputStream(url);

							// Closes the current file after confirming the next file can be open properly.
							if (current_stream_cat != null)
								current_stream_cat.close();
							if (current_stream_acc != null)
								current_stream_acc.close();
							current_file_index++;
							current_stream_acc = stream_acc;
							current_stream_cat = stream_cat;

							current_block_index = -25;
							current_buffer_index = -1;
							current_data_index = -1;
							current_star_index = 0;
						} catch ( FileNotFoundException exception ) {
							throw new CdromNotFoundException("disc " + getDiscNumber(current_file_index + 1));
						}
					} else {
						current_file_index++;

						current_buffer_index = -1;
						current_data_index = -1;
					}
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
		if (current_stream_acc != null)
			current_stream_acc.close();
		current_stream_acc = null;
		if (current_stream_cat != null)
			current_stream_cat.close();
		current_stream_cat = null;
	}
}
