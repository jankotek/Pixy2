/*
 * @(#)XmlDBHolder.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.io.*;

/**
 * The <code>XmlDBHolder</code> represents a XML element which 
 * contains XML elements of the same type.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 December 30
 */

public interface XmlDBHolder {
	/**
	 * Creates an empty <code>XmlDBHolder</code> object. This method 
	 * must be overrided by the subclass.
	 * @return the new empty object.
	 */
	public abstract XmlDBHolder create ( );

	/**
	 * Reads this XML document from the specified reader.
	 * @param in the reader.
	 * @exception IOException if I/O error occurs.
	 */
	public abstract void read ( Reader in )
		throws IOException;

	/**
	 * Writes this XML document to the specified writer.
	 * @param out the writer.
	 * @exception IOException if I/O error occurs.
	 */
	public abstract void write ( Writer out )
		throws IOException;

	/**
	 * Adds an XML element.
	 * @param element the XML element to add.
	 */
	public abstract void addDBRecord ( XmlDBRecord element );

	/**
	 * Gets the list of XML elements.
	 * @return the list of XML elements.
	 */
	public abstract XmlDBRecord[] getDBRecords ( );
}
