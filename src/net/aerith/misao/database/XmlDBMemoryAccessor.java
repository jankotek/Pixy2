/*
 * @(#)XmlDBMemoryAccessor.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.io.*;
import java.util.*;

/**
 * The <code>XmlDBMemoryAccessor</code> represents a sequential 
 * accessor to the elements in the primitive database. It keeps the 
 * current position in the database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 December 30
 */

public class XmlDBMemoryAccessor implements XmlDBAccessor {
	/**
	 * The holder of the XML elements.
	 */
	protected XmlDBHolder holder;

	/**
	 * The list of XML elements.
	 */
	protected XmlDBRecord[] records;

	/**
	 * The index of the current element.
	 */
	protected int index = -1;

	/**
	 * Constructs a <code>XmlDBMemoryAccessor</code> of the specified 
	 * database.
	 * @param holder the holder of the XML elements.
	 */
	public XmlDBMemoryAccessor ( XmlDBHolder holder ) {
		this.holder = holder;
	}

	/**
	 * Gets the first element in the database.
	 * @return the XML element.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlDBRecord getFirstElement ( )
		throws IOException
	{
		records = holder.getDBRecords();
		index = -1;
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
		index++;

		if (records == null)
			throw new IOException();

		if (index >= records.length)
			return null;

		return records[index];
	}
}
