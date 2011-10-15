/*
 * @(#)PrimitiveManager.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.io.*;
import java.util.*;

/**
 * The <code>PrimitiveManager</code> is an abstract class of a 
 * database manager which covers one virtual folder containing XML 
 * elements of the same type.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public abstract class PrimitiveManager {
	/**
	 * The holder class object of the XML records.
	 */
	protected XmlDBHolder holder_class;

	/**
	 * The class of the XML records.
	 */
	protected XmlDBRecord record_class;

	/**
	 * Creates an empty holder object.
	 * @return an empty holder object.
	 */
	public XmlDBHolder createHolder ( ) {
		return holder_class.create();
	}

	/**
	 * Gets the element of the specified ID.
	 * @param id the ID.
	 * @return the XML element.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlDBRecord getElement ( String id )
		throws IOException
	{
		XmlDBAccessor accessor = getAccessor();

		XmlDBRecord record = accessor.getFirstElement();
		while (record != null) {
			if (record.getID().equals(id))
				return record;
			record = accessor.getNextElement();
		}

		return null;
	}

	/**
	 * Adds the specified XML element into the database. 
	 * @param element the XML element.
	 * @exception IOException if I/O error occurs.
	 */
	public abstract void addElement ( XmlDBRecord element )
		throws IOException;

	/**
	 * Adds the XML elements in the specified list into the database. 
	 * All elements in the list are saved in one new XML file. Because
	 * the total file size is uncertain, the limitation of the size of
	 * an XML file does not work when using this method.
	 * @param list the list of XML elements.
	 * @exception IOException if I/O error occurs.
	 */
	public abstract void addElements ( Vector list )
		throws IOException;

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
	public abstract XmlDBRecord setElement ( XmlDBRecord element )
		throws IOException;

	/**
	 * Deletes the element of the specified ID.
	 * @param id the ID.
	 * @return the deleted XML element, or null if the element of the 
	 * specified ID does not exist.
	 * @exception IOException if I/O error occurs.
	 */
	public abstract XmlDBRecord deleteElement ( String id )
		throws IOException;

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
	public abstract void deleteElements ( Hashtable hash )
		throws IOException;

	/**
	 * Gets the sequential accessor to the elements in the database.
	 * @return the sequential accessor.
	 */
	public abstract XmlDBAccessor getAccessor ( );
}
