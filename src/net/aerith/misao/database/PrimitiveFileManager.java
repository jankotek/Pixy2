/*
 * @(#)PrimitiveFileManager.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.io.*;
import java.util.*;
import net.aerith.misao.io.XmlDBHolderCache;

/**
 * The <code>PrimitiveFileManager</code> represents a database manager 
 * which covers one folder containing XML elements of the same type.
 * <p>
 * The elements are stored in 1.xml, 2.xml, ..., Each XML file 
 * contains some elements and restricted not to be much larger than
 * the specified limit of file size.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 23
 */

public class PrimitiveFileManager extends PrimitiveManager {
	/**
	 * The folder of the database.
	 */
	protected File path;

	/**
	 * The limit of one XML file (50KB).
	 */
	protected int limit_filesize = 50000;

	/**
	 * Constructs a <code>PrimitiveFileManager</code> in the specified 
	 * folder, which consists of the XML elements of the specified
	 * class.
	 * @param path         the folder to create the database.
	 * @param holder_class the holder class object of the XML records.
	 * @param record_class the class object of the XML records.
	 * @exception IOException if I/O error occurs.
	 */
	public PrimitiveFileManager ( File path, XmlDBHolder holder_class, XmlDBRecord record_class )
		throws IOException
	{
		this.path = path;
		this.holder_class = holder_class;
		this.record_class = record_class;

		if (path.exists()) {
			if (path.isDirectory() == false)
				throw new IOException();
		} else {
			throw new IOException();
		}
	}

	/**
	 * Gets the folder path.
	 * @return the folder path.
	 */
	public File getPath ( ) {
		return path;
	}

	/**
	 * Gets the limit of one XML file.
	 * @return the limit of one XML file.
	 */
	public int getLimitFileSize ( ) {
		return limit_filesize;
	}

	/**
	 * Adds the specified XML element into the database. 
	 * @param element the XML element.
	 * @exception IOException if I/O error occurs.
	 */
	public void addElement ( XmlDBRecord element )
		throws IOException
	{
		XmlDBFileAccessor accessor = (XmlDBFileAccessor)getAccessor();

		File file = accessor.getVacantFile();

		// Reads the XML document.
		XmlDBHolder holder = XmlDBHolderCache.read(file, holder_class);

		holder.addDBRecord(element);

		// Saves the XML document.
		XmlDBHolderCache.write(file, holder);
	}

	/**
	 * Adds the XML elements in the specified list into the database. 
	 * All elements in the list are saved in one new XML file. Because
	 * the total file size is uncertain, the limitation of the size of
	 * an XML file does not work when using this method.
	 * @param list the list of XML elements.
	 * @exception IOException if I/O error occurs.
	 */
	public void addElements ( Vector list )
		throws IOException
	{
		XmlDBFileAccessor accessor = (XmlDBFileAccessor)getAccessor();

		File file = accessor.getNewFile();

		XmlDBHolder holder = holder_class.create();

		for (int i = 0 ; i < list.size() ; i++)
			holder.addDBRecord((XmlDBRecord)list.elementAt(i));

		// Saves the XML document.
		XmlDBHolderCache.write(file, holder);
	}

	/**
	 * Adds or updates the specified XML element into the database. 
	 * It checks if the element whose ID is the same as the specified
	 * element already exists or not. When it does, the data is 
	 * updated. Otherwise, the element is newly added.
	 * @param element the XML element.
	 * @return the old XML element if the element of the specified ID
	 * already exists, or null.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlDBRecord setElement ( XmlDBRecord element )
		throws IOException
	{
		XmlDBFileAccessor accessor = (XmlDBFileAccessor)getAccessor();

		XmlDBRecord old_record = deleteElement(element.getID());

		File file = accessor.getVacantFile();

		// Reads the XML document.
		XmlDBHolder holder = XmlDBHolderCache.read(file, holder_class);

		holder.addDBRecord(element);

		// Saves the XML document.
		XmlDBHolderCache.write(file, holder);

		return old_record;
	}

	/**
	 * Deletes the element of the specified ID.
	 * @param id the ID.
	 * @return the deleted XML element, or null if the element of the 
	 * specified ID does not exist.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlDBRecord deleteElement ( String id )
		throws IOException
	{
		XmlDBFileAccessor accessor = (XmlDBFileAccessor)getAccessor();

		XmlDBRecord record = accessor.getFirstElement();
		while (record != null) {
			if (record.getID().equals(id)) {
				File file = accessor.getCurrentFile();

				// Reads the XML document.
				XmlDBHolder holder = XmlDBHolderCache.read(file, holder_class);

				XmlDBHolder new_holder = holder_class.create();

				XmlDBRecord[] records = holder.getDBRecords();
				for (int i = 0 ; i < records.length ; i++) {
					if (records[i].getID().equals(id) == false)
						new_holder.addDBRecord(records[i]);
				}

				// Saves the XML document.
				XmlDBHolderCache.write(file, new_holder);

				return record;
			}

			record = accessor.getNextElement();
		}

		return null;
	}

	/**
	 * Deletes the elements in the specified hash table. After the
	 * operation, some of the elements in the specified hash table are
	 * deleted, which are in this database. Others are remained. The
	 * IDs of the deleted elements are also deleted from the hash
	 * table.
	 * @param hash the hash table whose keys are IDs of the elements
	 * to be deleted.
	 * @exception IOException if I/O error occurs.
	 */
	public void deleteElements ( Hashtable hash )
		throws IOException
	{
		File[] files = getPath().listFiles();
		for (int i = 0 ; i < files.length ; i++) {
			if (files[i].isFile()) {
				// Reads the XML document.
				XmlDBHolder holder = XmlDBHolderCache.read(files[i], holder_class);

				XmlDBHolder new_holder = holder_class.create();

				boolean update_flag = false;

				XmlDBRecord[] records = holder.getDBRecords();
				for (int j = 0 ; j < records.length ; j++) {
					if (hash.get(records[j].getID()) == null) {
						new_holder.addDBRecord(records[j]);
					} else {
						hash.remove(records[j].getID());
						update_flag = true;
					}
				}

				// Saves the XML document.
				if (update_flag) {
					XmlDBHolderCache.write(files[i], new_holder);

					if (hash.isEmpty())
						return;
				}
			}
		}
	}

	/**
	 * Gets the sequential accessor to the elements in the database.
	 * @return the sequential accessor.
	 */
	public XmlDBAccessor getAccessor ( ) {
		return new XmlDBFileAccessor(this);
	}
}
