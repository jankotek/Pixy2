/*
 * @(#)XmlDBFileAccessor.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.io.*;
import java.util.*;
import net.aerith.misao.io.FileManager;
import net.aerith.misao.io.XmlDBHolderCache;

/**
 * The <code>XmlDBFileAccessor</code> represents a sequential accessor
 * to the elements in the primitive database. It keeps the current 
 * position in the database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 23
 */

public class XmlDBFileAccessor implements XmlDBAccessor {
	/**
	 * The primitive database manager.
	 */
	protected PrimitiveFileManager manager;

	/**
	 * The list of XML files.
	 */
	protected File[] xml_files = null;

	/**
	 * The index in the file list.
	 */
	protected int index_file = -1;

	/**
	 * The records in the current XML document.
	 */
	protected XmlDBRecord[] xml_records = null;

	/**
	 * The index in the current XML document.
	 */
	protected int index_record = -1;

	/**
	 * Constructs a <code>XmlDBFileAccessor</code> of the specified 
	 * database.
	 * @param manager the primitive database manager.
	 */
	public XmlDBFileAccessor ( PrimitiveFileManager manager ) {
		this.manager = manager;
	}

	/**
	 * Initializes. Lists up all XML files in the database folder.
	 */
	public void initialize ( ) {
		File[] files = manager.getPath().listFiles();
		int xml_count = 0;
		for (int i = 0 ; i < files.length ; i++) {
			if (files[i].isFile())
				xml_count++;
		}

		xml_files = new File[xml_count];
		xml_count = 0;
		for (int i = 0 ; i < files.length ; i++) {
			if (files[i].isFile())
				xml_files[xml_count++] = files[i];
		}

		index_file = -1;

		xml_records = null;
		index_record = -1;
	}

	/**
	 * Gets the file which has room to add the XML element.
	 * @return the file which has room to add the XML element.
	 * @exception IOException if I/O error occurs.
	 */
	public File getVacantFile ( )
		throws IOException
	{
		initialize();

		for (int i = 0 ; i < xml_files.length ; i++) {
			try {
				FileInputStream stream = new FileInputStream(xml_files[i]);
				int size = stream.available();
				stream.close();
				if (size < manager.getLimitFileSize())
					return xml_files[i];
			} catch ( IOException exception ) {
				System.err.println(exception);
			}
		}

		// Creates a new XML file.
		return getNewFile();
	}

	/**
	 * Gets the new file.
	 * @return the new file.
	 * @exception IOException if I/O error occurs.
	 */
	public File getNewFile ( )
		throws IOException
	{
		int file_number = 1;
		File file = new File(FileManager.unitePath(manager.getPath().getPath(), file_number + ".xml"));
		while (file.exists()) {
			file_number++;
			file = new File(FileManager.unitePath(manager.getPath().getPath(), file_number + ".xml"));
		}

		// Saves the empty XML document.
		XmlDBHolder holder = manager.createHolder();
		Writer out = new PrintWriter(new DataOutputStream(new FileOutputStream(file)));
		holder.write(out);
		out.close();

		return file;
	}

	/**
	 * Gets the file which contains the current element.
	 * @return the file which contains the current element.
	 */
	public File getCurrentFile ( ) {
		return xml_files[index_file];
	}

	/**
	 * Gets the first element in the database.
	 * @return the XML element.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlDBRecord getFirstElement ( )
		throws IOException
	{
		initialize();

		return getNextElement();
	}

	/**
	 * Gets the next element in the database.
	 * @return the XML element.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlDBRecord getNextElement ( )
		throws IOException
	{
		index_record++;

		if (xml_records == null  ||  index_record >= xml_records.length) {
			index_file++;

			if (xml_files == null  ||  index_file >= xml_files.length)
				return null;

			// Reads the XML document.
			XmlDBHolder holder_class = manager.createHolder();
			XmlDBHolder holder = XmlDBHolderCache.read(xml_files[index_file], holder_class);

			xml_records = holder.getDBRecords();

			index_record = -1;

			return getNextElement();
		}

		return xml_records[index_record];
	}
}
