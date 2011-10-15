/*
 * @(#)PrimitiveMemoryManager.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.io.*;
import java.util.*;

/**
 * The <code>PrimitiveMemoryManager</code> represents a database 
 * manager which covers one folder containing XML elements of the same
 * type.
 * <p>
 * The elements are kept on memory and stored in the hash table. In 
 * fact, only one holder of the XML elements is stored in the hash
 * table.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public class PrimitiveMemoryManager extends PrimitiveManager {
	/**
	 * The hash table of the folder.
	 */
	protected Hashtable hash_folder = null;

	/**
	 * The holder of the XML element.
	 */
	protected XmlDBHolder holder = null;

	/**
	 * The key of the hash table to access to the holder.
	 */
	protected final static String holder_key = "holder";

	/**
	 * Constructs a <code>PrimitiveMemoryManager</code> in the 
	 * specified folder, which consists of the XML elements of the
	 * specified class.
	 * @param hash_folder  the hash table of the folder.
	 * @param holder_class the holder class object of the XML records.
	 * @param record_class the class object of the XML records.
	 */
	public PrimitiveMemoryManager ( Hashtable hash_folder, XmlDBHolder holder_class, XmlDBRecord record_class ) {
		this.hash_folder = hash_folder;
		this.holder_class = holder_class;
		this.record_class = record_class;

		if (this.hash_folder == null)
			this.hash_folder = new Hashtable();

		holder = (XmlDBHolder)this.hash_folder.get(holder_key);
		if (holder == null) {
			holder = holder_class.create();
			this.hash_folder.put(holder_key, holder);
		}
	}

	/**
	 * Gets the holder of the XML elements.
	 * @return the holder of the XML elements.
	 */
	public XmlDBHolder getHolder ( ) {
		return holder;
	}

	/**
	 * Adds the specified XML element into the database. 
	 * @param element the XML element.
	 * @exception IOException if I/O error occurs.
	 */
	public void addElement ( XmlDBRecord element )
		throws IOException
	{
		holder.addDBRecord(element);
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
		for (int i = 0 ; i < list.size() ; i++)
			holder.addDBRecord((XmlDBRecord)list.elementAt(i));
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
		XmlDBRecord old_record = deleteElement(element.getID());

		holder.addDBRecord(element);

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
		XmlDBHolder new_holder = holder_class.create();

		XmlDBRecord record = null;
		XmlDBRecord[] records = holder.getDBRecords();
		for (int i = 0 ; i < records.length ; i++) {
			if (records[i].getID().equals(id) == false)
				new_holder.addDBRecord(records[i]);
			else
				record = records[i];
		}

		holder = new_holder;

		hash_folder.remove(holder_key);
		hash_folder.put(holder_key, holder);

		return record;
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
		XmlDBHolder new_holder = holder_class.create();

		XmlDBRecord[] records = holder.getDBRecords();
		for (int i = 0 ; i < records.length ; i++) {
			if (hash.get(records[i].getID()) == null) {
				new_holder.addDBRecord(records[i]);
			} else {
				hash.remove(records[i].getID());
			}
		}

		holder = new_holder;

		hash_folder.remove(holder_key);
		hash_folder.put(holder_key, holder);
	}

	/**
	 * Gets the sequential accessor to the elements in the database.
	 * @return the sequential accessor.
	 */
	public XmlDBAccessor getAccessor ( ) {
		return new XmlDBMemoryAccessor(holder);
	}
}
