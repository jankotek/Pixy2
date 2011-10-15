/*
 * @(#)XmlDBRecord.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;

/**
 * The <code>XmlDBRecord</code> represents a XML element which can be
 * contained by the <code>XmlDBHolder</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 December 30
 */

public interface XmlDBRecord {
	/**
	 * Creates an empty <code>XmlDBRecord</code> object. This method 
	 * must be overrided by the subclass.
	 * @return the new empty object.
	 */
	public abstract XmlDBRecord create ( );

	/**
	 * Gets the ID.
	 * @return the ID.
	 */
	public abstract String getID ( );
}
