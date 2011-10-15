/*
 * @(#)XmlRecordHolder.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import net.aerith.misao.database.*;

/**
 * The <code>XmlRecordHolder</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 February 28
 */

public class XmlRecordHolder extends net.aerith.misao.xml.relaxer.XmlRecordHolder implements XmlDBHolder {
	/**
	 * Creates an empty <code>XmlDBHolder</code> object. This method 
	 * must be overrided by the subclass.
	 * @return the new empty object.
	 */
	public XmlDBHolder create ( ) {
		return new XmlRecordHolder();
	}

	/**
	 * Adds an XML element.
	 * @param element the XML element to add.
	 */
	public void addDBRecord ( XmlDBRecord element ) {
		addRecord((XmlRecord)element);
	}

	/**
	 * Gets the list of XML elements.
	 * @return the list of XML elements.
	 */
	public XmlDBRecord[] getDBRecords ( ) {
		return (XmlDBRecord[])getRecord();
	}
}
