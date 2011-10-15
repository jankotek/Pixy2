/*
 * @(#)XmlDBAccessor.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.io.*;
import java.util.*;

/**
 * The <code>XmlDBAccessor</code> represents a sequential accessor to
 * the elements in the primitive database. It keeps the current 
 * position in the database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 December 30
 */

public interface XmlDBAccessor {
	/**
	 * Gets the first element in the database.
	 * @return the XML element.
	 * @exception IOException if I/O error occurs.
	 */
	public abstract XmlDBRecord getFirstElement ( )
		throws IOException;

	/**
	 * Gets the next element in the database.
	 * @return the XML element.
	 * @exception IOException if I/O error occurs.
	 */
	public abstract XmlDBRecord getNextElement ( )
		throws IOException;
}
