/*
 * @(#)FileReader.java
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
import net.aerith.misao.io.Decoder;
import net.aerith.misao.io.CdromNotFoundException;

/**
 * The <code>FileReader</code> is a base class to read a star catalog
 * file. It has a method <code>createStar</code> to create a 
 * <code>CatalogStar</code> object from one line record of the file,
 * which must be overrided in the subclasses.
 * <p>
 * When the filename ends with ".gz" suffix, it opens the file through
 * <code>GZIPInputStream</code>.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public class FileReader extends CatalogReader {
	/**
	 * The input stream reader.
	 */
	protected BufferedReader reader;

	/**
	 * The circum area to read stars.
	 */
	protected CircumArea circum_area;

	/**
	 * The index of the current block.
	 */
	protected long current_block = 0;

	/**
	 * The index of the star in the current block.
	 */
	protected long current_index = 0;

	/**
	 * Constructs an empty <code>FileReader</code>.
	 */
	protected FileReader ( ) {
		setDefaultURL();
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "";
	}

	/**
	 * Checks if the catalog data is a file.
	 * @return true if the catalog data is a file.
	 */
	public boolean isFile ( ) {
		return true;
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
		return null;
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

		URL url = url_set.exists();

		reader = Decoder.newReader(url);

		current_block = -1;
		current_index = getBlockSize();
	}

	/**
	 * Reads one data from the opened catalog. After this method is 
	 * invoked, the cursor is promoted to tne next star. When every 
	 * data is read, it returns null.
	 * <p>
	 * The check of the R.A. and Decl. is also judged in this method,
	 * however, no check of magnitude is judged because some catalog 
	 * stars have no significant magnitude.
	 * subclasses.
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
		if (reader == null)
			throw new IOException();

		long block_size = getBlockSize();
		long record_size = getRecordSize();

		while (true) {
			if (block_size > 0) {
				// Goes to the next block overlapping the circum area.
				if (current_index == block_size) {
					current_index = 0;
					current_block++;

					if (current_block == getBlockCount())
						return null;

					// Reads the block size of the current block.
					block_size = getBlockSize();

					// When not overlapping the next block, 
					if (overlapsBlock() == false) {
						if (record_size > 0) {
							StreamSkipTimer skip_timer = new StreamSkipTimer(reader, 10000);
							skip_timer.skip(block_size * record_size);
						} else {
							for (int i = 0 ; i < block_size ; i++)
								reader.readLine();
						}
						current_index = block_size;
						continue;
					}
				} 
			}

			String record = reader.readLine();
			if (record == null)
				return null;

			current_index++;

			CatalogStar star = createStar(record);
			if (star != null) {
				if (circum_area == null  ||  circum_area.inArea(star.getCoor()))
					return star;
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
		reader.close();
		reader = null;
	}

	/**
	 * Gets the number of blocks in a file. If 0, it means the file is
	 * not separated into blocks. It must be overrided in the 
	 * subclasses if neccessary.
	 * @return the size of a block.
	 */
	public long getBlockCount ( ) {
		return 0;
	}

	/**
	 * Gets the number of records in a block. If 0, it means the file
	 * is not separated into blocks. It must be overrided in the 
	 * subclasses if neccessary.
	 * @return the size of a block.
	 */
	public long getBlockSize ( ) {
		return 0;
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
		return 0;
	}

	/**
	 * Checks if the current block is overlapping on the specified 
	 * circum area.
	 * @return true if the current block is overlapping on the 
	 * specified circum area.
	 */
	public boolean overlapsBlock ( ) {
		return true;
	}
}
