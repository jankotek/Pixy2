/*
 * @(#)CelestialDivisionMapDBAccessor.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.io.IOException;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.io.*;

/**
 * The <code>CelestialDivisionMapDBAccessor</code> represents a 
 * sequential accessor to the XML elements within the specified 
 * circular area in the database classified by the R.A. and Decl.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public class CelestialDivisionMapDBAccessor {
	/**
	 * The file system of the database.
	 */
	protected FileSystem file_system;

	/**
	 * The folder.
	 */
	protected Folder db_folder;

	/**
	 * The celestial division map.
	 */
	protected CelestialDivisionMap map;

	/**
	 * The sequential accessor of the folders.
	 */
	protected CelestialDivisionMapAccessor folder_accessor;

	/**
	 * The sequential accessor of the elements in one folder.
	 */
	protected XmlDBAccessor xml_accessor;

	/**
	 * The holder class object of the XML records.
	 */
	protected XmlDBHolder holder_class;

	/**
	 * The class of the XML records.
	 */
	protected XmlDBRecord record_class;

	/**
	 * Constructs a <code>CelestialDivisionMapDBAccessor</code>.
	 * @param file_system  the file system of the database.
	 * @param db_folder    the folder.
	 * @param map          the celestial division map.
	 * @param holder_class the holder class object of the XML records.
	 * @param record_class the class object of the XML records.
	 */
	public CelestialDivisionMapDBAccessor ( FileSystem file_system, Folder db_folder, CelestialDivisionMap map, XmlDBHolder holder_class, XmlDBRecord record_class ) {
		this.file_system = file_system;
		this.db_folder = db_folder;
		this.map = map;
		this.holder_class = holder_class;
		this.record_class = record_class;
	}

	/**
	 * Gets the first element.
	 * @return the first element.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlDBRecord getFirstElement ( )
		throws IOException
	{
		folder_accessor = null;
		xml_accessor = null;

		return getNextElement();
	}

	/**
	 * Gets the next element.
	 * @return the next element.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlDBRecord getNextElement ( )
		throws IOException
	{
		while (true) {
			if (xml_accessor == null) {
				Vector list = null;

				if (folder_accessor == null) {
					folder_accessor = new CelestialDivisionMapAccessor(map);
					list = folder_accessor.getFirstFolderHierarchy();
				} else {
					list = folder_accessor.getNextFolderHierarchy();
				}

				if (list == null)
					return null;

				Vector folder_list = db_folder.getHierarchy();
				for (int i = 0 ; i < list.size() ; i++)
					folder_list.addElement(list.elementAt(i));

				try {
					Folder folder = file_system.getHierarchicalFolder(folder_list);
					PrimitiveManager manager = file_system.getPrimitiveManager(folder, holder_class, record_class);
					xml_accessor = manager.getAccessor();
				} catch ( IOException exception ) {
					// In the case there is no data in that folder.
				}

				if (xml_accessor != null) {
					XmlDBRecord record = xml_accessor.getFirstElement();
					if (record != null)
						return record;

					xml_accessor = null;
				}
			}

			if (xml_accessor != null) {
				XmlDBRecord record = xml_accessor.getNextElement();
				if (record != null)
					return record;

				xml_accessor = null;
			}
		}
	}
}
