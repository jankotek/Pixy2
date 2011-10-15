/*
 * @(#)XmlDBHolderCache.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io;
import java.io.*;
import net.aerith.misao.database.XmlDBHolder;

/**
 * The <code>XmlDBHolderCache</code> represents a disk cache to read
 * and write XML files in the database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 23
 */

public class XmlDBHolderCache {
	/**
	 * The number of XML files in cache.
	 */
	protected final static int file_count = 100;

	/**
	 * The limit to update without saving to a file.
	 */
	protected static int update_limit = 20;

	/**
	 * The entries.
	 */
	protected static CacheEntry[] entries = new CacheEntry[file_count];

	/**
	 * True if the disk cache is enabled.
	 */
	protected static boolean enable_cache = false;

	/**
	 * Enables/disables the disk cache.
	 * @param flag true when to enable the disk cache.
	 * @exception IOException if I/O error occurs.
	 */
	public static void enable ( boolean flag )
		throws IOException
	{
		if (flag == false) {
			flush();

			entries = new CacheEntry[file_count];
		}

		enable_cache = flag;
	}

	/**
	 * Reads the XML document.
	 * @param file         the file.
	 * @param holder_class the holder class object of the XML records.
	 * @return the holder.
	 * @exception IOException if I/O error occurs.
	 */
	public static XmlDBHolder read ( File file, XmlDBHolder holder_class )
		throws IOException
	{
		int empty_index = -1;

		if (enable_cache) {
			int LRU_index = -1;
			long LRU_last_used = -1;

			for (int i = 0 ; i < file_count ; i++) {
				if (entries[i] == null) {
					// Empty area is found.
					empty_index = i;
				} else {
					if (entries[i].getPath().equals(file.getAbsolutePath())) {
						// Specified file is found in cache.
						return (XmlDBHolder)entries[i].getContent();
					} else {
						// Searches least recently used file.
						if (LRU_last_used < 0  ||  LRU_last_used > entries[i].getLastUsedTimeMillis()) {
							LRU_index = i;
							LRU_last_used = entries[i].getLastUsedTimeMillis();
						}
					}
				}
			}

			// When not found in cache.

			// When no empty area is found, saves the least
			// recently used file and replaces.
			if (empty_index < 0) {
				if (entries[LRU_index].isUpdated()) {
					File LRU_file = new File(entries[LRU_index].getPath());
					Writer out = new PrintWriter(new DataOutputStream(new FileOutputStream(LRU_file)));
					XmlDBHolder holder = (XmlDBHolder)entries[LRU_index].getContent();
					holder.write(out);
					out.close();
				}

				empty_index = LRU_index;
			}
		}

		// Reads the XML document.
		XmlDBHolder holder = holder_class.create();
		Reader in = new BufferedReader(new FileReader(file));
		holder.read(in);
		in.close();

		if (enable_cache) {
			entries[empty_index] = new CacheEntry(file.getAbsolutePath(), holder);
		}

		return holder;
	}

	/**
	 * Writes the XML document.
	 * @param file   the file.
	 * @param holder the holder.
	 * @exception IOException if I/O error occurs.
	 */
	public static void write ( File file, XmlDBHolder holder )
		throws IOException
	{
		if (enable_cache) {
			for (int i = 0 ; i < file_count ; i++) {
				if (entries[i] != null) {
					if (entries[i].getPath().equals(file.getAbsolutePath())) {
						// Specified file is found in cache.
						entries[i].update(holder);

						// Saves forcely if the file is updated many times,
						// but not saved in the file for a while.
						// Then XmlDBFileAccessor#getVacantFile() can find
						// a vacant file properly, because it checks the
						// file size on the disk.
						if (entries[i].getUpdateCount() >= update_limit) {
							Writer out = new PrintWriter(new DataOutputStream(new FileOutputStream(file)));
							holder.write(out);
							out.close();

							entries[i].resetUpdateCount();
						}

						return;
					}
				}
			}
		}

		// When not found in cache.

		// Saves the XML document.
		Writer out = new PrintWriter(new DataOutputStream(new FileOutputStream(file)));
		holder.write(out);
		out.close();
	}

	/**
	 * Saves the XML documents into files.
	 * @exception IOException if I/O error occurs.
	 */
	public static void flush ( )
		throws IOException
	{
		for (int i = 0 ; i < file_count ; i++) {
			if (entries[i] != null) {
				if (entries[i].isUpdated()) {
					// Saves the XML document.
					File file = new File(entries[i].getPath());
					Writer out = new PrintWriter(new DataOutputStream(new FileOutputStream(file)));
					XmlDBHolder holder = (XmlDBHolder)entries[i].getContent();
					holder.write(out);
					out.close();

					entries[i].resetUpdateCount();
				}
			}
		}
	}
}
